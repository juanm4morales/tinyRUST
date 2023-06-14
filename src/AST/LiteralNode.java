package AST;

import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

/**
 * Clase que representa a un NodoLiteral: literal entero, literal cadena,
 * literal caracter, literal nil, literal bool. Es una subclase de Nodo
 * expresión (ExpNode).
 */
public class LiteralNode extends ExpNode {

    public LiteralNode(Token token, Type type) {
        super.token = token;
        this.type = type;
    }

    public Token getToken(){
        return token;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        if (type==null) {
            System.err.println("Debug purpose: No se ha identificado el tipo " +
                    "del literal.");
        }

    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+"nodoLiteral\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"lexema\": \""+this.token.getLexeme()+"\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"tipo\": \""+this.type+"\"\n");

        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
