package Backend;

import AST.*;
import DataType.Type;
import SymbolTable.SymbolTable;
import SymbolTable.Method;
import SymbolTable.MethodEntry;
import SymbolTable.ClassEntry;
import SymbolTable.AttributeEntry;
import SymbolTable.VarEntry;
import SymbolTable.ParameterEntry;
import Utils.StringUtils;

import java.util.*;

/**
 * Esta clase mediante los nodos del AST y la tabla de símbolos, generada en
 * el análisis del código tinyRust+, traduce a instrucciones de MIPS32.
 *
 * @author Juan Martín Morales
 */
public class CodeGenerator implements VisitorCodeGen{
    // Porción del código .data
    private static StringBuilder data = new StringBuilder();
    // Porción del código .data para vtables
    private static StringBuilder vtables = new StringBuilder();
    // Porción del código .text
    private static StringBuilder text = new StringBuilder();
    // Cantidad de labels de branch creados
    private static int branchAmount = 0;
    // Tabla de Símbolos
    private SymbolTable symbolTable;
    // Clase actual
    private ClassEntry currentClass;
    // Método actual
    private Method currentMethod;
    // Referencia a la entrada de la clase encadenada al nodo actual
    private ClassEntry chainedTo;
    // Flags útiles
    private static boolean f_getVal = true; // Obtener valor?
    private static boolean f_self = false;  // Acceso self?
    private static boolean f_main = false;  // Posicionados en el main=
    //
    private static int v_asgm_pos = 0;
    //
    private static int c_self_position = 2;


    /**
     * Constructor del generador de código
     * @param symbolTable
     */
    public CodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        data.append("\t.data\n");
        vtables.append("# vtables_section\n" +
                "\t.data\n");
        text.append("\t.text\n" +
                "\t.globl main\n" +
                "\t j main\n\n");
    }

    /**
     * Generador de código para el código completo
     * @param ast Referencia al AST
     */
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
        text.append("\tli $v0, 10\n" +
                "\tsyscall\n");
        f_main = false;
    }

    /**
     * Generador de código para el cuerpo de una clase.
     * @param classNode Referencial al nodo classNode
     */
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

    /**
     * Generador de código para el cuerpo de un método.
     * @param methodNode Referencia al nodo MethodNode
     */
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
        text.append("# DECL_VARS_LOCALES\n");
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
        text.append("# FIN_DECL_VARS_LOCALES\n");
        // Generación de código para cada sentencia del método
        for (SentenceNode sentence:methodNode.getBlock().getSentences()) {
            sentence.codeGen(this);
            // chainedTo = null;

        }
        if (Objects.equals(methodId, classId)) {    // CONSTRUCTOR
            text.append("\tlw $a0, -8($fp) #\n");     // $a0 <- self
            // (asignación!)
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

    /**
     * Generador de código para todas las sentencias de un bloque.
     * @param blockNode N
     */
    @Override
    public void visit(BlockNode blockNode) {
        for (SentenceNode sentence: blockNode.getSentences()) {
            sentence.codeGen(this);
            // chainedTo = null;
        }
    }

    @Override
    public void visit(AccessNode accessNode) {
        
        
    }

    @Override
    public void visit(ArrayNode arrayNode) {
        
        
    }

    /**
     * Generador de código para las asignaciones <var> = <exp>.
     * @param assignNode
     */
    @Override
    public void visit(AssignNode assignNode) {
        boolean callReturn = false; // Determina si el lado derecho es un
        // callNode
        text.append("# ASIGNACION\n");
        f_getVal = false;   // No obtener valor. Solo referencia del lado izq.
        VarNode leftSide = assignNode.getLeftSide();
        text.append("# LADO IZQUIERDO\n");
        leftSide.codeGen(this);
        f_getVal = true;    // Por defecto se obtiene el valor.
        text.append("\tsw $a0, 0($sp)\n");
        text.append("\taddiu $sp, $sp, -4\n");
        //text.append("\tmove $t1, $a0\n");
        //
        ExpNode rightSide = assignNode.getRightSide();
        if (rightSide instanceof CallNode) {
            callReturn = true;
            if (((CallNode) rightSide).isConstructor()) {
                c_self_position = v_asgm_pos;
            }
        }
        // if (rightSide)
        text.append("# LADO DERECHO\n");
        rightSide.codeGen(this);
        //
        text.append("\tlw $t1, 4($sp)\n");
        text.append("\taddiu $sp, $sp, 4\n");
        if (callReturn) {
            text.append("\tsw $v0, ($t1)\n");
        }
        else {
            text.append("\tsw $a0, ($t1)\n");
        }


        text.append("#FIN_ASIGNACION\n");
    }

    /**
     * Generador de código para acceso a variables (encadenadas o no) de
     * cualquier tipo. Acceso a valor o a referencia.
     * @param varNode
     */
    @Override
    public void visit(VarNode varNode) {
        int position;
        int offsetHeader = 3;
        int offsetParams;
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
                // text.append("\tlw $s0, ($a0)\n");         //
                text.append("\taddiu $a0, $s0, "+(4+position*4)+"\n");

                f_self = false;

                if (f_getVal) {
                    text.append("\tlw $a0, ($a0)\n"); // val
                }
                if (chainVar!=null) {
                    chainedTo =
                            symbolTable.getClass(varNode.getType().getType());
                    chainVar.codeGen(this);
                    chainedTo = null;
                }

            }
            else {
                // NO SE PUEDE ACCEDER A ATRIBUTOS PUBS.
            }
            return;
        }
        // Primero en la cadena
        if (chainVar!=null) {   // Tiene una cadena  ... .(Xi).(Xi+1). ...
            if (Objects.equals(varNode.getToken().getLexeme(), "self")) {
                f_self = true;
                chainedTo = currentClass;
                c_self_position = 2;
                chainVar.codeGen(this);
                f_self = false;
                chainedTo = null;
            }
            else {              // No tiene una cadena
                chainedTo =
                        symbolTable.getClass(varNode.getChainType().getType());
                chainVar.codeGen(this);
                chainedTo = null;

            }
        } else {
            VarEntry varST = symbolTable.getVariable(varId, currentMethod);


            if (varST == null) {
                varST = symbolTable.getAttribute(varId, currentClass);
                f_self = true;
            }

            position = varST.getPosition();
            int offsetForVar = 0;

            if (!(varST instanceof ParameterEntry)) {
                offsetForVar = currentMethod.getParamAmount();
            }
            v_asgm_pos = position+offsetHeader; // offset 3 ($ra call, $fp call, self)
            text.append("\tmove $a0, $fp\n");
            if (f_self) {
                text.append("\taddiu $a0, $a0, -8\n");
                text.append("\tlw $s0, ($a0)\n");
                text.append("\taddiu $a0, $s0, "+(4+position*4)+"\n"); // dir
                f_self = false;
            }
            else {
                text.append("\taddiu $a0, $a0, "+(-12-(offsetForVar*4)-position*4)
                        + "\n"); //
                // dir
            }

            if (f_getVal) {
                text.append("\tlw $a0, ($a0)\n"); // val. Obtengo el valor
            }
            //
        }

    }

    /**
     * Generador de código para llamado a métodos (encadenados o no).
     * @param callNode
     */
    @Override
    public void visit(CallNode callNode) {
        String classId;             // Identificador de la clase
        String methodId;            // Identificador del método llamado
        String vtableId = "";            // Label de la la V_table
        int methodPosition = 0;     // Posición del método dentro de la clase
        boolean constructor = false;// Se llama a un constructor?
        boolean staticCall = callNode.isStatic();
        if (chainedTo!=null) {          // Ya ha sido ENCADENADO
            classId = chainedTo.getId();
        }
        else if (staticCall) { // Estático
            classId = callNode.getStaticClassT().getLexeme();
        }
        else if (currentClass!=null){ // Llamada a método de la misma clase
            // (dentro de la clase)
            classId = currentClass.getId();
        }
        else { // CONSTRUCTOR
            constructor = true;
            classId = "";
        }
        /*
        if (chainedTo==null && callNode.isStatic()) {
            classId = callNode.getStaticClassT().getLexeme();
        }
        else {
            classId = chainedTo.getId();
        }
        */


        methodId = callNode.getToken().getLexeme();
        vtableId = classId + "_vtable";
        if (callNode.firstInChain()) {  // PRIMERO DE LA CADENA
            if (callNode.isStatic()){
                ClassEntry staticClass =
                        symbolTable.getClass(callNode.getStaticClassT()
                                .getLexeme());
                methodPosition = staticClass.getMethod(methodId).getPosition();
            }
            else if (currentClass!=null) {
                methodPosition = currentClass.getMethod(methodId).getPosition();
            }

        }                               // TAIL DE LA CADENA
        else {
            methodPosition = chainedTo.getMethod(methodId).getPosition();
        }
        text.append("# LLamado \n");
        if (!constructor && !staticCall) {
            // Cargamos referencia a la VT del objeto
            text.append("\tla $t0, "+vtableId+"\n");
            // Cargamos el label del metodo correspondiente
            text.append("\tlw $t1, "+(methodPosition*4)+"($t0)\n"); // -> $t1
            //
        }

        text.append("\taddiu $sp, $sp, -4\n");  //
        text.append("\tsw $fp, 0($sp)\n");      // $fp caller
        text.append("\taddiu $sp, $sp, -4\n");  //
        //
        if (!f_main) {
            text.append("\tlw $t0, "+(-c_self_position*4)+"($fp)\n");    // $t0
            // <- self
        }
        // (!)
        else {
            text.append("\tlw $t0, "+(-c_self_position*4)+"($fp)\n");
        }
        if (staticCall) {
            text.append("\tli $t0, 0\n");       // No uso self (cargo 0)
        }
        text.append(("\tsw $t0, 0($sp)\n"));    // Guardo self en stack
        text.append(("\taddiu $sp, $sp, -4\n"));// Guardo self en stack

        int position = 0;
        for (ExpNode exp:callNode.getParamExp()) {
            exp.codeGen(this);
            text.append("\tsw $a0, "+(-4*position)+"($sp)\n");
            text.append("\taddiu $sp, $sp, -4\n");
            position++;
        }
        text.append("\taddiu $fp, $sp, "+(12+4*position)+"\n"); // nuevo $fp
        if (!constructor && !staticCall) {
            text.append("\tjalr $t1\n");
        }
        else if (staticCall){
            text.append("\tjal "+classId+"_"+methodId+"\n");
        }
        else { // CONSTRUCTOR. label -> [ClassID]_[ClassID]
            text.append("\tjal "+methodId+"_"+methodId+"\n");
        }
        // constructor = false;

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
        // text.append("\tmove $v0, $a0\n"); Mod 1 (return reg $a0. -> to $v0)
        int varAmount =
                currentMethod.getParamAmount() + currentMethod.getVarAmount();

        text.append("\taddiu $sp, $sp, "+(varAmount*4+8)+"\n" +
                "\tlw $fp, 0($sp)\n");
        text.append("\taddiu $sp, $sp, 4\n" +
                "\tlw $ra, 0($sp)\n" +
                "\tjr $ra\n");
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
                String validLabel = StringUtils.getAsciiLabel(literal);
                data.append(validLabel+": .asciiz \""+literal+"\"\n");
                // hay que colocar el string en $a0
                text.append("\tla $a0, "+validLabel+"\n");
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
        branchAmount++;
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
            branchAmount++;
        }
        else {
            branchLabel = ("endif_"+branchAmount);
            endLabel = branchLabel;
            branchAmount++;
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

        text.append("# END_IF_ELSE\n");
    }


    /**
     * Generador de código de las clases predefinidas de tinyRust+
     */
    private void predCodeGen() {
        text.append("IO_out_str:\n");
        text.append("\tsw $ra, 0($fp)\n" +
                "\tlw $a0, -12($fp)\n" +
                "\tli $v0, 4\n" +
                "\tsyscall\n" +
                "\taddiu $sp, $sp, 12\n" +
                "\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n" +
                "\tjr $ra\n");

        text.append("IO_out_i32:\n");
        text.append("\tsw $ra, 0($fp)\n" +
                "\tlw $a0, -12($fp)\n" +
                "\tli $v0, 1\n" +
                "\tsyscall\n" +
                "\taddiu $sp, $sp, 12\n" +
                "\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n" +
                "\tjr $ra\n");

        data.append("true: .asciiz \"true\"\n");
        data.append("false: .asciiz \"false\"\n");

        text.append("IO_out_bool:\n");
        text.append("\tsw $ra, 0($fp)\n" +
                "\tlw $a0, -12($fp)\n" +
                "\tli $t0, 1\n" +
                "\tbeq $a0, $t0, IO_out_bool_true_case\n" +
                "\tla $a0, false\n" +
                "\tj IO_out_bool_end\n" +
                "IO_out_bool_true_case:\n" +
                "\tla $a0, true\n" +
                "IO_out_bool_end:\n" +
                "\tli $v0, 4\n" +
                "\tsyscall\n" +
                "\taddiu $sp, $sp, 12\n" +
                "\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n" +
                "\tjr $ra\n");

        text.append("IO_out_char:\n");
        text.append("\tsw $ra, 0($fp)\n" +
                "\tlw $a0, -12($fp)\n" +
                "\tli $v0, 11\n" +
                "\tsyscall\n" +
                "\taddiu $sp, $sp, 12\n" +
                "\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n" +
                "\tjr $ra\n");

        text.append("IO_out_array:\n");

        text.append("IO_in_str:\n");


        text.append("IO_in_i32:\n");
        text.append("\tsw $ra, 0($fp)\n" +
                "\tli $v0, 5\n" +
                "\tsyscall\n" +
                "\taddiu $sp, $sp, 8\n" +
                "\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n" +
                "\tjr $ra\n");

        text.append("IO_in_bool:\n");
        text.append("\tsw $ra, 0($fp)\n" +
                "\tli $v0, 5\n" +
                "\tsyscall\n" +
                "\taddiu $sp, $sp, 8\n" +
                "\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n" +
                "\tjr $ra\n");

        text.append("IO_in_Char:\n");

        text.append("Str_length:\n");
        text.append("Str_concat:\n");
        text.append("Str_substr:\n");

        text.append("Array_length:\n");


    }
    /*
    private void defaultValues() {
        data.append("default_char:\t.byte ' '\n");
        data.append("default_string:\t.asciiz \"\"\n");
    }

    private void storeString(String memRefReg, String strReg) {

    }
    */


    /**
     * Getter del código generado
     * @return String con el código .asm
     */
    public String getCode(){
        StringBuilder asm = new StringBuilder();
        asm.append(data).append(vtables).append(text);
        return asm.toString();
    }
}
