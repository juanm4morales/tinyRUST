package AST;

import DataType.*;
import Frontend.Lexer.Token;

@Deprecated
public abstract class OperandNode extends ExpNode {
    Type type;


    public OperandNode(Token token, Type type) {
        super.token = token;
        this.type = type;
    }
    @Override
    public void setToken(Token token) {
        super.token = token;
    }

    public void setType(Type type) {
        type = type;
    }

}
