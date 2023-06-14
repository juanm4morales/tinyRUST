package Backend;

import AST.*;
import DataType.Type;
import SymbolTable.SymbolTable;
import SymbolTable.Method;
import SymbolTable.MethodEntry;
import SymbolTable.ClassEntry;
import SymbolTable.AttributeEntry;
import SymbolTable.VarEntry;

import java.util.*;

public class CodeGenerator implements VisitorCodeGen{
    private static StringBuilder data = new StringBuilder();
    private static StringBuilder vtables = new StringBuilder();
    private static StringBuilder text = new StringBuilder();
    private static int branchAmount = 0;

    private SymbolTable symbolTable;
    private ClassEntry currentClass;
    private Method currentMethod;

    private ClassEntry chainedTo;
    // flags útiles
    private static boolean f_getVal = true;
    private static boolean f_self = false;
    private static boolean f_main = false;

    private static int v_asgm_pos = 0;

    private static int c_self_position = 2;



    public CodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        data.append("\t.data\n");
        vtables.append("\t# vtables\n\t.data\n");
        text.append("\t.text\n\t.globl main\n");
    }


    @Override
    public void visit(AST ast) {
        predCodeGen();
        ArrayList<ClassNode> classes = ast.getClasses();
        for (ClassNode classNode:classes) {
            classNode.codeGen(this);
        }
        MethodNode main = ast.getMain();
        f_main = true;
        currentClass = null;
        currentMethod = symbolTable.getMain();
        main.codeGen(this);
        f_main = false;
    }

    @Override
    public void visit(ClassNode classNode) {
        String classId = classNode.getName();
        currentClass=symbolTable.getClass(classId);
        // Creación de VTable
        vtables.append(classId+"_vtable:\n");
        String methodLabel = "";
        ArrayList<MethodEntry> methods =
                new ArrayList<MethodEntry>(currentClass.getMethods().values());
        Collections.sort(methods,
                Comparator.comparingInt((MethodEntry::getPosition)));
        for (MethodEntry method : methods) {
            methodLabel = method.getId();
            vtables.append("\t.word "+classId+"_"+methodLabel+"\n");
        }
        // Generación de código para el constructor
        classNode.getConstructor().codeGen(this);
        // Generación de código para los métodos
        for (MethodNode method:classNode.getMethods()) {
            method.codeGen(this);
        }
    }

    @Override
    public void visit(MethodNode methodNode) {
        String classId =(currentClass==null) ? "" : currentClass.getId();
        String methodId = methodNode.getName();
        String methodLabel;
        if (Objects.equals(methodId, "main")) {
            methodLabel = methodId;
        }
        else {
            methodLabel = classId+"_"+methodId;
            currentMethod = currentClass.getMethod(methodId);
            if (currentMethod==null) {  // CONSTRUCTOR
                currentMethod = currentClass.getConstructor();
            }
        }

        text.append(methodLabel+":\n");
        if (methodLabel.equals("main")) {
            // Método main
            text.append("\tmove $fp, $sp\n");
            text.append("\taddiu $sp, $sp, -12\n");
        }
        else {
            // Método
            text.append("\tsw $ra, 0($fp)\n");
            text.append("\tlw $s0, -8($fp)\n"); // cargo self en $s0 (¿?)
            if (Objects.equals(methodId, classId)) {    // Constructor
                text.append("\tla $a0, "+classId+"_vtable\n");
                text.append("\tsw $a0, 0($s0)\n");
                // Construccion por defecto (valores iniciales)
                int pos, absPos, strOffSet;
                strOffSet = 0;
                String type;
                for (AttributeEntry attribute: currentClass.getVariables()
                        .values()) {
                    pos = attribute.getPosition();
                    absPos = 4 + 4*(pos+1) + strOffSet;
                    type = attribute.getType().getType();

                    switch (type) {
                        case "I32":
                            // text.append("\tsw $zero, "+absPos+"($s0)\n");
                            break;
                        case "Char":
                            // text.append("\tla $t0, default_char\n");
                            // text.append("\tlb $t0, ($t0)\n");
                            // text.append("sw $t0, "+absPos+"($s0)\n");
                        case "Str":
                            // text.append("\tla $t0, default_string\n");
                            // text.append("\tlb $t1, 0($t0)\n");
                            // text.append("\tsb $t1, "+absPos+"($s0)\n");
                            strOffSet+=32;
                        default:
                            System.err.print("DEBUG PURPOSE: No se han " +
                                    "contemplado otros" +
                                    " casos de inicializacion");
                    }
                }
            }
            // methodNode.getBlock().codeGen(this);

        }

        // AGREGAR VARIABLES LOCALES
        Collection<VarEntry> variables = currentMethod.getVariables().values();
        int varAmount = variables.size();
        text.append("\taddiu $sp, $sp, "+(-varAmount*4)+"\n");
        for (VarEntry var: variables) {
            // Solo almaceno referencias a los objetos declarados (Tipo
            // referencia)
            if (Type.isReference(var.getType())) {
                String refId = var.getType().getType();
                int attrAmount = symbolTable.getClass(refId).getAttrAmount();
                text.append("\tli $a0, "+(4+4*attrAmount)+"\n");
                text.append("\tli $v0, 9\n\tsyscall\n");
                int offset = varAmount-var.getPosition()*4;
                text.append("\tsw $v0, "+(offset*4)+"($sp)\n");
            }

        }
        // Generación de código para cada sentencia del método
        for (SentenceNode sentence:methodNode.getBlock().getSentences()) {
            sentence.codeGen(this);
        }
        // (Optimizar)
        // SI HAY UN RETURN LAS SIGUIENTES SENTENCIAS NO TIENEN SENTIDO
        //
        int varParamAmount =
                currentMethod.getParamAmount() + varAmount;
        text.append("\taddiu $sp, $sp, "+(varParamAmount*4+8)+"\n");

        if (!Objects.equals(methodId, "main")) {
            text.append("\tlw $fp, 0($sp)\n");
            text.append("\taddiu $sp, $sp, 4\n");
            text.append("\tlw $ra, 0($sp)\n\tjr $ra\n");
        }
        else { // MAIN
            text.append("\taddiu $sp, $sp, 4\n");
        }
    }

    @Override
    public void visit(BlockNode blockNode) {
        for (SentenceNode sentence: blockNode.getSentences()) {
            sentence.codeGen(this);
        }
    }

    @Override
    public void visit(AccessNode accessNode) {
        
        
    }

    @Override
    public void visit(ArrayNode arrayNode) {
        
        
    }

    @Override
    public void visit(AssignNode assignNode) {
        text.append("#ASIGNACION\n");
        f_getVal = false;
        VarNode leftSide = assignNode.getLeftSide();
        leftSide.codeGen(this);
        f_getVal = true;
        text.append("\tmove $t1, $a0\n");
        // ACAAAAAAA
        ExpNode rightSide = assignNode.getRightSide();
        if (rightSide instanceof CallNode) {
            if (((CallNode) rightSide).isConstructor()) {
                c_self_position = v_asgm_pos;
            }
        }

        rightSide.codeGen(this);
        /// Y ACAAA
        text.append("\tsw $a0, ($t1)\n");
        text.append("#FIN_ASIGNACION\n");
    }

    /**
     * Generador de código para acceso a variables (encadenadas o no) de todo
     * tipo. Acceso a valor o a referencia.
     * @param varNode
     */
    @Override
    public void visit(VarNode varNode) {
        int position;
        if (Type.isReference(varNode.getType())) {
            f_getVal = false;
        }
        AccessNode chainVar = varNode.getChain();
        String varId = varNode.getToken().getLexeme();
        if (!varNode.firstInChain()) { // No es el primero de la cadena
            if (f_self) {
                AttributeEntry varST = symbolTable.getAttribute(varId,
                        currentClass);
                if (varST==null) {
                    System.err.println("DEBUG: No se encontró el atributo "
                            + varId);
                    return;
                }
                position = varST.getPosition();

                // text.append("\tmove $a0, $fp\n");        // Ya está cargado
                // text.append("\taddiu $a0, $a0, -8\n");   // self en $s0
                //text.append("\tlw $s0, ($a0)\n");         //
                text.append("\taddiu $a0, $s0, "+(4+position*4)+"\n");

                f_self = false;

                if (f_getVal) {
                    text.append("\tlw $a0, ($a0)\n"); // val
                }
                if (chainVar!=null) {
                    chainedTo =
                            symbolTable.getClass(varNode.getType().getType());
                    chainVar.codeGen(this);
                }

            }
            else {
                // NO SE PUEDE ACCEDER A ATRIBUTOS PUBS y tampoco ..
            }
            return;
        }
        // Primero en la cadena
        if (chainVar!=null) {
            if (Objects.equals(varNode.getToken().getLexeme(), "self")) {
                f_self = true;
                chainedTo = currentClass;
                c_self_position = 2;
                chainVar.codeGen(this);
            }
            else {
                chainedTo =
                        symbolTable.getClass(varNode.getChainType().getType());
                chainVar.codeGen(this);

            }
        } else {
            VarEntry varST = symbolTable.getVariable(varId, currentMethod);

            if (varST == null) {
                varST = symbolTable.getAttribute(varId, currentClass);
                f_self = true;
            }

            position = varST.getPosition();
            v_asgm_pos = position+3; // offset 3 ($ra call, $fp call, self)
            text.append("\tmove $a0, $fp\n");
            if (f_self) {
                text.append("\taddiu $a0, $a0, -8\n");
                text.append("\tlw $s0, ($a0)\n");
                text.append("\taddiu $a0, $s0, "+(4+position*4)+"\n"); // dir
            }
            else {
                text.append("\taddiu $a0, $a0, "+(-12-position*4)+"\n"); // dir
            }

            if (f_getVal) {
                text.append("\tlw $a0, ($a0)\n"); // val
            }
        }

    }

    @Override
    public void visit(CallNode callNode) {
        String classId;
        String methodId;
        String vtableId;
        int methodPosition = 0;
        boolean constructor = false;

        classId = chainedTo.getId();
        methodId = callNode.getToken().getLexeme();
        vtableId = classId + "_vtable";
        if (callNode.firstInChain()) {
            if (callNode.isStatic()){
                ClassEntry staticClass =
                        symbolTable.getClass(callNode.getStaticClassT()
                                .getLexeme());
                methodPosition = staticClass.getMethod(methodId).getPosition();
            }
            else if (currentClass!=null) {
                methodPosition = currentClass.getMethod(methodId).getPosition();
            }
            else { // Constructor
                constructor = true;
            }
        }
        else {
            methodPosition = chainedTo.getMethod(methodId).getPosition();
        }
        text.append("# LLamado \n");
        if (!constructor) {

            // Cargamos referencia a la VT del objeto
            text.append("\tla $t0, "+vtableId+"\n");
            // Cargamos el label del metodo correspondiente
            text.append("\tlw $t1, "+(methodPosition*4)+"($t0)\n"); // -> $t1
            //
        }


        text.append("\taddiu $sp, $sp, -4\n");  //
        text.append("\tsw $fp, 0($sp)\n");      // $fp caller
        text.append("\taddiu $sp, $sp, -4\n");      //
        //
        if (!f_main) {
            text.append("\tlw $t0, "+(-c_self_position*4)+"($fp)\n");    // $t0
            // <- self
        }
        // REPETIDO SE PUEDE BORRAR condicional (!!!!!!!!!!!!!!)
        else {
            text.append("\tlw $t0, "+(-c_self_position*4)+"($fp)\n");
        }

        text.append(("\tsw $t0, 0($sp)\n"));    // Guardo self en stack
        text.append(("\taddiu $sp, $sp, -4\n"));    // Guardo self en stack

        int position = 0;
        for (ExpNode exp:callNode.getParamExp()) {
            exp.codeGen(this);
            text.append("\tsw $a0, "+(-4*position)+"($sp)\n");
            text.append("\taddiu $sp, $sp, -4\n");
            position++;
        }
        text.append("\taddiu $fp, $sp, "+(12+4*position)+"\n"); // nuevo $fp
        if (!constructor) {
            text.append("\tjalr $t1\n");
        }
        else {
            text.append("\tjal "+classId+"_"+classId+"\n");
        }
        constructor = false;

    }
    /**
     * Generador de código para expresiones binarias.
     * @param binExpNode Referencia al nodo BinExpNode
     */
    @Override
    public void visit(BinExpNode binExpNode) {
        binExpNode.getLeftSide().codeGen(this);
        text.append("\tsw $a0, ($sp)\n\taddiu $sp, $sp, -4\n");
        binExpNode.getRightSide().codeGen(this);
        text.append("\tlw $t0, 4($sp)\n");
        text.append("\taddiu $sp, $sp, 4\n");
        switch (binExpNode.getToken().getLexeme()) {
            // Operador aritmetico
            case "+":
                text.append("\tadd $a0, $t0, $a0\n");
                break;
            case "-":
                text.append("\tsub $a0, $t0, $a0\n");
                break;
            case "*":
                text.append("\tmul $a0, $t0, $a0\n");
                break;
            case "/":
                // CONTROL DIVISIÓN POR CERO
                text.append("\tdiv $a0, $t0, $a0\n");
                break;
            case "%":
                // CONTROL DIVISIÓN POR CERO
                text.append("\tdiv $a0, $t0\n");
                text.append("\tmflo $a0");
                break;
            // Operador relacional
            case "<":
                text.append("\tslt $a0, $t0, $a0\n");
                break;
            case "<=":
                text.append("\tsle $a0, $t0, $a0\n");
                break;
            case ">":
                text.append("\tsgt $a0, $t0, $a0\n");
                break;
            case ">=":
                text.append("\tsge $a0, $t0, $a0\n");
                break;
            // Operador igualdad
            case "==":
                text.append("\tseq $a0, $t0, $a0\n");
                break;
            case "!=":
                text.append("\tsne $a0, $t0, $a0\n");
                break;
            // Operador booleano
            case "&&":
                text.append("\tand $a0, $t0, $a0\n");
                break;
            case "||":
                text.append("\tor $a0, $t0, $a0\n");
                break;
            default:
                System.out.println("ERROR. TEST PURPOSE");
        }
    }

    /**
     * Generador de código para expresiones unarias.
     * @param unExpNode Referencia al nodo UnExpNode
     */
    @Override
    public void visit(UnExpNode unExpNode) {
        unExpNode.getRightSide().codeGen(this);
        switch (unExpNode.getToken().getLexeme()) {
            case "+":
                break;
            case "-":
                text.append("\tneg $a0, $a0\n");
                break;
            case "!":
                text.append("\txori $a0, $a0, 1\n");
                break;
            default:
                System.out.println("DEFAULT. TEST PURPOSE");
        }
    }
    /**
     * Generador de código para sentencias de retorno.
     * @param returnNode Referencia al nodo ReturnNode
     */
    @Override
    public void visit(ReturnNode returnNode) {
        ExpNode returnVal = returnNode.getReturnVal();
        returnVal.codeGen(this);
        text.append("\tmove $v0, $a0\n");
        int varAmount =
                currentMethod.getParamAmount() + currentMethod.getVarAmount();

        text.append("\taddiu $sp, $sp, "+(varAmount*4+8)+"\n\tlw $fp, 0($sp)" +
                "\n");
        text.append("\taddiu $sp, $sp, 4\n\tlw $ra, 0($sp)\n\tjr $ra\n");
    }

    /**
     * Generador de código para literales.
     * @param literalNode Referencia al nodo LiteralNode
     */
    @Override
    public void visit(LiteralNode literalNode) {
        String type = literalNode.getType().getType();
        String literal = literalNode.getToken().getLexeme();
        switch (type) {
            case "I32":
                text.append("\tli $a0, " + literal + "\n");
                break;
            case "Char":
                text.append("\tli $a0, '" + literal + "'\n");
                break;
            case "Bool":
                text.append("\tli $a0, ");
                if (Objects.equals(literal, "true")) {
                    text.append("1\n");
                }
                else {
                    text.append("0\n");
                }
                break;
            case "Str":
                data.append(literal+":\t.asciiz \""+literal+"\"\n");
                // hay que colocar el string en $a0
                break;
        }
        
    }

    /**
     * Generador de código para las sentencias while
     * @param whileNode Referencia al nodo WhileNode
     */
    @Override
    public void visit(WhileNode whileNode) {
        text.append("# WHILE\n");
        String whileLabel = ("while_"+branchAmount);
        String endLabel = ("end_while_"+branchAmount);
        text.append(whileLabel+":\n");
        ExpNode condition = whileNode.getCondition();
        condition.codeGen(this);
        SentenceNode body = whileNode.getBody();


        text.append("\tbeq $a0, $zero, "+endLabel+"\n");
        if (body!=null) {
            body.codeGen(this);
            text.append("\tj "+whileLabel+"\n");
        }
        text.append(endLabel+":\n");
        branchAmount++;
        text.append("# END_WHILE\n");
    }

    /**
     * Generador de código para las sentencias ifElseNode
     * @param ifElseNode Referencia al nodo IfElseNode
     */
    @Override
    public void visit(IfElseNode ifElseNode) {
        text.append("# IF_ELSE\n");
        ExpNode condition = ifElseNode.getCondition();
        condition.codeGen(this);
        SentenceNode thenPart = ifElseNode.getThenPart();
        SentenceNode elsePart = ifElseNode.getElsePart();
        String branchLabel;
        String endLabel;
        if (elsePart!=null) {
            branchLabel = ("else_"+branchAmount);
            endLabel = ("endif_"+branchAmount);
        }
        else {
            branchLabel = ("endif_"+branchAmount);
            endLabel = branchLabel;
        }
        text.append("\tbeq $a0, $zero, "+branchLabel+"\n");
        if (thenPart!=null) {
            thenPart.codeGen(this);
        }
        if (elsePart!=null) {
            text.append("\tj "+endLabel+"\n");
            text.append(branchLabel+":\n");
            elsePart.codeGen(this);
        }
        text.append(endLabel+":\n");
        branchAmount++;
        text.append("# END_IF_ELSE\n");
    }


    /**
     * Generador de código de las clases predefinidas de tinyRust+
     */
    private void predCodeGen() {
        text.append("IO_out_string:\n");
        text.append("\tsw $ra, 0($fp)\n\tlw $a0, 4($sp)\n\tli $v0, 4\n" +
                "\tsyscall\n\taddiu $sp, $sp, 8\n\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n\tjr $ra\n");

        text.append("IO_out_i32:\n");
        text.append("\tsw $ra, 0($fp)\n\tlw $a0, 4($sp)\n\tli $v0, 1\n" +
                "\tsyscall\n\taddiu $sp, $sp, 8\n\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n\tjr $ra\n");

        text.append("IO_out_bool:\n");
        text.append("\tsw $ra, 0($fp)\n\tlw $a0, 4($sp)\n\tli $v0, 1\n" +
                "\tsyscall\n\taddiu $sp, $sp, 8\n\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n\tjr $ra\n");

        text.append("IO_out_char:\n");

        text.append("IO_out_array:\n");

        text.append("IO_in_str:\n");

        text.append("IO_in_i32:\n");

        text.append("IO_in_bool:\n");

        text.append("IO_in_Char:\n");

        text.append("Str_length:\n");
        text.append("Str_concat:\n");
        text.append("Str_substr:\n");

        text.append("Array_length:\n");


    }

    private void defaultValues() {
        data.append("default_char:\t.byte ' '\n");
        data.append("default_string:\t.asciiz \"\"\n");
    }

    private void storeString(String memRefReg, String strReg) {

    }

    /**
     * Getter del código generado
     * @return String con el código asm
     */
    public String getCode(){
        StringBuilder asm = new StringBuilder();
        asm.append(data).append(vtables).append(text);
        return asm.toString();
    }
}
