package Backend;

import AST.*;

/**
 * Interfaz útil para separar el objeto de tipo Node de la generación de
 * código del mismo (Visitor Pattern).
 *
 * @author Juan Martín Morales
 */
public interface VisitorCodeGen {
    void visit(AccessNode accessNode);
    void visit(ArrayNode arrayNode);
    void visit(AssignNode assignNode);
    void visit(AST ast);
    void visit(BinExpNode binExpNode);
    void visit(BlockNode blockNode);
    void visit(CallNode callNode);
    void visit(ClassNode classNode);
    void visit(IfElseNode ifElseNode);
    void visit(LiteralNode literalNode);
    void visit(MethodNode methodNode);
    void visit(ReturnNode returnNode);
    void visit(UnExpNode unExpNode);
    void visit(VarNode varNode);
    void visit(WhileNode whileNode);

}
