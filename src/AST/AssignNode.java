package AST;


import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.Objects;

/**
 * Esta clase representa un Nodo de Asignación de un valor a una variable. En
 * el lado izquierdo (leftSide) hay un Nodo Variable, y en el lado derecho
 * (rightSide) hay un Nodo Expresión. Es una subclase del Nodo Sentencia
 * (SentenceNode)
 *
 * @author Juan Martín Morales
 */
public class AssignNode extends SentenceNode {
    private VarNode leftSide;               // Lado izquierdo de la asignación
    private ExpNode rightSide;              // Lado derecho de la asignación

    /**
     * Setter del lado izquierdo del Nodo Asignación. Además establece como
     * padre de este, la instancia actual.
     * @param leftSide  Nodo variable
     */
    public void setLeftSide(VarNode leftSide) {
        if (leftSide!=null) {
            leftSide.parent = this;
        }
        this.leftSide = leftSide;
    }
    /**
     * Setter del lado derecho del Nodo Asignación. Además establece como
     * padre de este a la instancia actual.
     * @param rightSide  Nodo expresión
     */
    public void setRightSide(ExpNode rightSide) {
        if (rightSide!=null) {
            rightSide.parent = this;
        }
        this.rightSide = rightSide;
    }

    public VarNode getLeftSide() {
        return leftSide;
    }

    public ExpNode getRightSide() {
        return rightSide;
    }

    /**
     * Setter del lado izquierdo y derecho del Nodo asignación. Además establece como
     * padre de estos a la instancia actual.
     * @param leftSide Nodo variable
     * @param rightSide Nodo expresión
     */
    public void makeFamily(VarNode leftSide, ExpNode rightSide) {
        setLeftSide(leftSide);
        setRightSide(rightSide);
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        leftSide.sentenceCheck(symbolTable);
        rightSide.sentenceCheck(symbolTable);
        String leftType = leftSide.type.getType();
        String righType = rightSide.type.getType();
        if (!symbolTable.satisfiesPolymorphism(leftSide.type, rightSide.type)) {
            throw new SemanticException("El tipo de la expresión derecha " +
                    "("+rightSide.type+"), no es " + leftSide.type + " ni de" +
                    " una subclase de este.", leftSide.token.row,
                    leftSide.token.col);
        }
        if (Objects.equals(righType, "Array")) {
            if (rightSide instanceof ArrayNode) {
                leftSide.setSize(((ArrayNode)rightSide).getSize());
            }
            else {
                System.err.println("DEBUG PURPOSE: ERROR al castear ExpNode a" +
                        " ArrayNode");
            }
        }
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \"nodoAsignacion\",\n");
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
