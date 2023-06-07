package AST;

import Backend.VisitorCodeGen;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
@Deprecated
public class ChainedVarNode extends ChainedNode {
    public ChainedVarNode(Token token) {
        super.token = token;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {

    }

    @Override
    public String toJson(int indents) {
        return null;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        // visitor.visit(this);
    }
}
