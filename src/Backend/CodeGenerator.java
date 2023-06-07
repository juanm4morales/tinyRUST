package Backend;

import AST.*;
import SymbolTable.SymbolTable;
import SymbolTable.Method;
import SymbolTable.MethodEntry;
import SymbolTable.ClassEntry;

import java.util.ArrayList;

public class CodeGenerator implements VisitorCodeGen{
    private static StringBuilder data = new StringBuilder();
    private static StringBuilder vtables = new StringBuilder();
    private static StringBuilder text = new StringBuilder();
    private static int branchAmount = 0;

    private SymbolTable symbolTable;
    private ClassEntry currentClass;
    private Method currentMethod;

    public CodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
        data.append("\t.data\n");
        vtables.append("\t# vtables\n\t.data\n");
        text.append("\t.text\n\t.globl main\n");
    }
    @Override
    public void visit(AccessNode accessNode) {
        
        
    }

    @Override
    public void visit(ArrayNode arrayNode) {
        
        
    }

    @Override
    public void visit(AssignNode assignNode) {
        
        
    }

    @Override
    public void visit(AST ast) {
        ArrayList<ClassNode> classes = ast.getClasses();
        for (ClassNode classNode:classes) {
            classNode.codeGen(this);
        }
        MethodNode main = ast.getMain();
        main.codeGen(this);
    }

    @Override
    public void visit(WhileNode whileNode) {
        ExpNode condition = whileNode.getCondition();
        condition.codeGen(this);
        SentenceNode body = whileNode.getBody();

        String whileLabel = ("while_"+branchAmount);
        String endLabel = ("end_while_"+branchAmount);
        text.append(whileLabel+":\n");
        text.append("\tbeq $a0, $zero, "+endLabel+"\n");
        if (body!=null) {
            body.codeGen(this);
            text.append("\tj "+whileLabel+"\n");
        }
        text.append(endLabel+":\n");
        branchAmount++;
    }

    @Override
    public void visit(VarNode varNode) {
        
        
    }

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

    @Override
    public void visit(ReturnNode returnNode) {
        ExpNode returnVal = returnNode.getReturnVal();
        returnVal.codeGen(this);
        text.append("\tmove $v0, $a0\n");
        int varAmount =
                currentMethod.getParamAmount() + currentMethod.getVarAmount();

        text.append("\taddiu $sp, $sp, "+(varAmount-4)+"\n\tmove $fp, $sp\n");
        text.append("\taddiu $sp, $sp, 4\n\tlw $ra, 0($sp)\n\tjr $ra\n");
    }

    @Override
    public void visit(MethodNode methodNode) {
        String classId = currentClass.getId();
        String methodId = methodNode.getName();
        String methodLabel = classId+"_"+methodId;
        currentMethod = currentClass.getMethod(methodId);

        text.append(methodLabel+":\n");
        text.append("\tsw $ra, 0($fp)\n");
        //
        // AGREGAR VARIABLES LOCALES
        //
        for (SentenceNode sentence:methodNode.getBlock().getSentences()) {
            sentence.codeGen(this);
        }
        //
        // SI HAY UN RETURN LAS SIGUIENTES SENTENCIAS NO TIENEN SENTIDO 1,2,3,4
        //
        int varAmount =
                currentMethod.getParamAmount() + currentMethod.getVarAmount();
        text.append("\taddiu $sp, $sp, "+(varAmount-4)+"\n\tmove $fp, $sp\n");
        text.append("\taddiu $sp, $sp, 4\n\tlw $ra, 0($sp)\n\tjr $ra\n");
    }

    @Override
    public void visit(LiteralNode literalNode) {
        String type = literalNode.getType().getType();
        String literal = literalNode.getToken().getLexeme();
        switch (type) {
            case "I32":
                text.append("\tli $a0, " + literal + "\n");
                break;
            case "Char":
                text.append("\tli $a0, \'" + literal + "\'\n");
                break;
            case "Bool":
                text.append("\tli $a0, ");
                if (literal=="true") {
                    text.append("1\n");
                }
                else {
                    text.append("0\n");
                }
                break;
            case "Str":
                data.append(literal+":\t.asciiz \""+literal+"\"\n");
                break;
        }
        
    }

    @Override
    public void visit(IfElseNode ifElseNode) {
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
    }

    @Override
    public void visit(ClassNode classNode) {
        String classId = classNode.getName();
        currentClass=symbolTable.getClass(classId);
        vtables.append(classId+"_vtable:\n");
        String methodLabel = "";
        for (MethodEntry method : currentClass.getMethods().values()) {
            methodLabel = method.getId();
            vtables.append("\t.word "+classId+"_"+methodLabel+"\n");
        }

        for (MethodNode method:classNode.getMethods()) {
            method.codeGen(this);
        }
        
    }

    @Override
    public void visit(CallNode callNode) {
        
        
    }


    @Deprecated
    @Override
    public void visit(BlockNode blockNode) {

    }


    @Override
    public void visit(BinExpNode binExpNode) {
        
        binExpNode.getLeftSide().codeGen(this);
        text.append("\tmove $t0, $a0\n");
        binExpNode.getRightSide().codeGen(this);
        switch (binExpNode.getToken().getLexeme()) {
            case "+":
                text.append("\tadd $a0 $t0 $a0\n");
                break;
            case "-":
                text.append("\tsub $a0 $t0 $a0\n");
                break;
            case "*":
                text.append("\tmul $a0 $t0 $a0\n");
                break;
            case "/":
                text.append("\tdiv $a0 $t0 $a0\n");
                break;
            default:
                System.out.println("DEFAULT. TEST PURPOSE");
        }
        
    }

    private void predCodeGen() {

        text.append("IO_out_void:\n");
        text.append("\tsw $ra, 0($fp)\n\tlw $a0, 4($sp)\n\tli $v0, 4\n" +
                "\tsyscall\n\taddiu $sp, $sp, 8\n\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n\tjr $ra\n");

        text.append("IO_out_i32:\n");
        text.append("\tsw $ra, 0($fp)\n\tlw $a0, 4($sp)\n\tli $v0, 1\n" +
                "\tsyscall\n\taddiu $sp, $sp, 8\n\tlw $fp, 0($sp)\n" +
                "\taddiu $sp, $sp, 4\n\tjr $ra\n");


        text.append("Str_length:\n");
        text.append("Str_concat:\n");
        text.append("Str_substr:\n");

        

    }
}
