package AST;

import Backend.VisitorCodeGen;
import DataType.PrimitiveType;
import DataType.Type;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;


/**
 * Esta clase representa un Nodo de una Expresión Binaria. Es un subclase del
 * Nodo Expresión (ExpNode).
 * Contiene información sobre las expresiones del lado izquierdo y derecho. Y
 * el token representa el operador binario asociado.
 *
 * @author Juan Martín Morales
 */
public class BinExpNode extends ExpNode {
    private ExpNode leftSide;           // Lado izquierdo de expresión binaria
    private ExpNode rightSide;          // Lado derecho de expresión binaria

    /**
     * Setter del lado izquierdo del Nodo Expresión Binaria . Además establece
     * como padre de este, la instancia actual.
     * @param leftSide  Nodo Expresión
     */
    public void setLeftSide(ExpNode leftSide) {
        if (leftSide!=null) {
            leftSide.parent = this;
        }
        this.leftSide = leftSide;
    }
    /**
     * Setter del operador binario de la expresión .
     * @param operator Token del operador asociado a la expresión
     */
    public void setOperator(Token operator) {
        super.token = operator;
    }
    /**
     * Setter del lado derecho del Nodo Expresión Binaria . Además establece
     * como padre de este, la instancia actual.
     * @param rightSide  Nodo Expresión
     */
    public void setRightSide(ExpNode rightSide) {
        if (rightSide!=null) {
            rightSide.parent = this;
        }
        this.rightSide = rightSide;
    }
    /**
     * Setter del lado derecho del Nodo Expresión Binaria . Además establece
     * como padre de este, la instancia actual.
     * @param leftSide  Nodo Expresión (izquierda)
     * @param rightSide  Nodo Expresión (derecha)
     */
    public void makeFamily(ExpNode leftSide, ExpNode rightSide) {
        setLeftSide(leftSide);
        setRightSide(rightSide);
    }

    public ExpNode getLeftSide() {
        return leftSide;
    }

    public ExpNode getRightSide() {
        return rightSide;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        leftSide.sentenceCheck(symbolTable);
        rightSide.sentenceCheck(symbolTable);
        Type leftType = leftSide.type;
        Type rightType = rightSide.type;
        Token operator = super.token;
        if (operator==null) {
            System.err.println("DEBUG PURPOSE: \"Error: operador nulo.\"");
            return;
        }

        switch (operator.getLexeme()) {
            case "+": case "-": case "*": case "/": case "%":
                if (!leftType.getType().equals(Type.I32)) {
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme()+ " no puede ser utilizado con" +
                            " una expresión de tipo " + leftType.getType() +
                            ". Solo expresiones de tipo I32", super.token.row,
                            super.token.col);
                }
                if (!rightType.getType().equals(Type.I32)){
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme()+ " no puede ser utilizado" +
                            " con una expresión de tipo " + rightType.getType() +
                            ". Solo expresiones de tipo I32", super.token.row,
                            super.token.col);
                }
                super.type = leftType;
                break;

            case "<": case ">": case "<=": case ">=":
                if (!leftType.getType().equals(Type.I32)) {
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme()+ " no puede ser utilizado " +
                            "con una expresión de tipo " + leftType.getType() +
                            ". Solo expresiones de tipo I32", super.token.row,
                            super.token.col);
                }
                if (!rightType.getType().equals(Type.I32)){
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme() + " no puede ser utilizado " +
                            "con una expresión de tipo " + rightType.getType() +
                            ". Solo expresiones de tipo I32", super.token.row,
                            super.token.col);
                }
                super.type = new PrimitiveType(Type.BOOL);
                break;
            case "==": case "!=":
                if (!leftType.equals(rightType)) {
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme()+ " debe ser utilizado con  " +
                            "expresiones del mismo tipo. {"+leftSide.getType()+
                            "!="+rightType.getType()+"}", super.token.row,
                            super.token.col);
                }
                super.type = new PrimitiveType(Type.BOOL);
                break;
            case "||": case "&&":
                if (!leftType.getType().equals(Type.BOOL)) {
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme()+ " no puede ser utilizado" +
                            " con una expresión de tipo " + leftType.getType(),
                            super.token.row, super.token.col);
                }
                if (!rightType.getType().equals(Type.BOOL)) {
                    throw new SemanticException("El operador binario " +
                            operator.getLexeme()+ " no puede ser utilizado " +
                            "con una expresión de tipo " + rightType.getType() +
                            ". Solo expresiones de tipo Bool", super.token.row,
                            super.token.col);
                }
                super.type = new PrimitiveType(Type.BOOL);
                break;
            default:
                System.err.println("Debug purpose: El operador binario " +
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
                "\"ladoIzquierdo\":\n");
        astJson = astJson.concat(leftSide.toJson(indents)+",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"ladoDerecho\":\n");
        astJson = astJson.concat(rightSide.toJson(indents)+"\n");

        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
