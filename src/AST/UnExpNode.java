package AST;

import Backend.VisitorCodeGen;
import DataType.PrimitiveType;
import DataType.Type;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.Objects;


/**
 * Esta clase representa un Nodo de una Expresión Unaria. Es un subclase del
 * Nodo Expresión (ExpNode).
 * Contiene información sobre la expresión del lado derecho y el token del
 * operador asociado.
 *
 * @author Juan Martín Morales
 */
public class UnExpNode extends ExpNode {
    ExpNode rightSide;      // lado derecho de la expresión unaria


    /**
     * Setter del operador binario de la expresión unaria .
     * @param operator Token del operador asociado a la expresión
     */
    public void setOperator(Token operator) {
        super.token = operator;
    }
    /**
     * Setter del lado derecho del Nodo Expresión Unaria . Además establece
     * como padre de este, la instancia actual.
     * @param rightSide  Nodo Expresión
     */
    public void setRightSide(ExpNode rightSide) {
        if (rightSide!=null) {
            rightSide.parent = this;
        }
        this.rightSide = rightSide;
    }

    public ExpNode getRightSide() {
        return rightSide;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        rightSide.sentenceCheck(symbolTable);
        Type rightType = rightSide.type;
        Token operator = super.token;
        switch (operator.getLexeme()) {
            case "+": case "-":
                if (!Objects.equals(rightType.getType(), Type.I32)) {
                    throw new SemanticException("El operador unario " +
                            operator.getLexeme()+ " no puede ser utilizado " +
                            "con una expresión de tipo " + rightType.getType() +
                            ". Solo expresiones de tipo I32", operator.row, 0);
                }
                super.type = rightType;
                break;
            case "!":
                if (!Objects.equals(rightType.getType(), Type.BOOL)) {
                    throw new SemanticException("El operador unario " +
                            operator.getLexeme() + " debe ser utilizado con " +
                            "expresiones del tipo Bool.", super.token.row,
                            super.token.col);
                }
                super.type = new PrimitiveType(Type.BOOL);
                break;
            default:
                System.err.println("Debug purpose: El operador " +
                        operator.getLexeme()+ " no se reconoce.");
        }
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \"nodoExpBinaria\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"Operador\": \""+super.token.getLexeme()+"\",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"ladoDerecho\":\n");
        astJson = astJson.concat(rightSide.toJson(indents+1)+"\n");

        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
