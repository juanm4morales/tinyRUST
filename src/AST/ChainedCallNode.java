package AST;

import Backend.VisitorCodeGen;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;

import java.util.ArrayList;

@Deprecated
public class ChainedCallNode extends ChainedNode{
    private ArrayList<ExpNode> parameters;

    public ChainedCallNode(Token token) {
        parameters = new ArrayList<ExpNode>();
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
