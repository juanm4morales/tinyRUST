package AST;

import Backend.VisitorCodeGen;
import DataType.ArrayType;
import DataType.ReferenceType;
import DataType.Type;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.Objects;

/**
 * Esta clase representa un Nodo de Arreglo. Es un subclase del Nodo Variable
 * (VarNode).
 * Contiene información sobre el índice utilizado para acceder o construir
 * un Arreglo.
 *
 * @author Juan Martín Morales
 */
public class ArrayNode extends VarNode{
    private ExpNode indexExp;           // Expresión del índice del arreglo
    private boolean access;             // Determina si es un acceso por índice

    public ArrayNode(Token token) {
        super(token);
    }
    public ArrayNode(Token token, ExpNode indexExp) {
        super(token);
        if (indexExp!=null) {
            indexExp.parent = this;
        }
        this.indexExp = indexExp;

    }

    /**
     * Setter del Tipo del Nodo (ArrayType(PrimitiveType)
     * @param arrayType Tipo del arreglo
     */
    public void setType(String arrayType) {
        super.chainType = new ArrayType(arrayType);
        super.type = new ArrayType(arrayType);
    }

    /**
     * Setter de la expresión del índice del Nodo arreglo
     * @param indexExp Expresión que representa el valor del índice
     */
    public void setIndexExp(ExpNode indexExp) {
        if (indexExp!=null) {
            indexExp.parent = this;
        }
        this.indexExp = indexExp;
    }

    /**
     * Setter del atributo que identifica si se está accediendo al
     * arreglo. Si es es falso, el nodo arreglo representa la creación
     * de una instancia
     * @param access Valor booleano que determina si el nodo representa
     *              un acceso o no
     */
    public void setAccess(boolean access) {
        this.access = access;
    }


    @Override
    public void sentenceCheck(SymbolTable symbolTable)
            throws SemanticException {

        if (super.parent instanceof AccessNode) {
            //
            String parentType = ((AccessNode) super.parent).chainType.getType();
            if (!symbolTable.containsClass(parentType)) {
                throw new SemanticException("La clase " + parentType + " no" +
                        " ha sido declarada.", super.token.row,
                        super.token.col);
            }

            ArrayType type = (ArrayType) symbolTable.getAttributeType(
                    token.getLexeme(), parentType);
            if (access) {
                super.chainType = Type.createType(type.getArrayType());
            }
            else {
                super.chainType = type;
            }
            if (super.chainType == null) {
                throw new SemanticException("El atributo "
                        + token.getLexeme() + " no ha sido declarado en" +
                        " la clase "+parentType+".", super.token.row,
                        super.token.col);
            }

            if (!symbolTable.isAttributePub(token.getLexeme(), parentType)) {
                if (!Objects.equals(symbolTable.getCurrentClass().getId(), parentType)) {
                    super.chainType=null;
                    throw new SemanticException("El atributo "
                            + token.getLexeme() + " de la clase "+parentType+
                            " tiene visibilidad privada. Por lo que no ha podido ser " +
                            "accedido", super.token.row, super.token.col);
                }

            }

        }
        else {
            // Comienzo del acceso a variable encadenada
            if (super.token!=null) {
                ArrayType type = (ArrayType) symbolTable.getVariableType(
                        super.token.getLexeme(), false);
                if (access) {
                    super.chainType = Type.createType(type.getArrayType());
                }
                //else {  // Constructor
                //    super.chainType = type;
                //}

                if (super.chainType == null) {
                    throw new SemanticException("La variable " +
                            super.token.getLexeme() + " no ha sido " +
                            "declarada.", super.token.row,
                            super.token.col);

                }
            }
            else {  // Constructor
                if (!access) {
                    super.size = Integer.parseInt(indexExp.token.getLexeme());
                    super.chainType = type;
                }
            }
        }
        if (chain!=null) {
            chain.sentenceCheck(symbolTable);
            super.type = chain.type;
        }
        else {
            super.type = chainType;
        }
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+"nodoArr\",\n");
        if (super.token != null) {
            astJson = astJson.concat(StringUtils.multiTabs(indents)+
                    "\"lexema\": \""+super.token.getLexeme()+"\",\n");
        }

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"tipo\": \""+super.type+"\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"indice (exp)\":\n");
        astJson = astJson.concat(indexExp.toJson(indents+1)+"\n");
        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }
    @Override
    public void codeGen(VisitorCodeGen visitor) {

    }
}
