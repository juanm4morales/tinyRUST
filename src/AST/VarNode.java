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
 * Esta clase representa un Nodo de un Acceso a una variable. Es un subclase del
 * Nodo Acceso (AccessNode).
 * Los acceso que incluye son a: atributos self, atributos, variables.
 */
public class VarNode extends AccessNode {
    protected int size;

    public VarNode(Token token) {
        super.token = token;
    }

    public VarNode(Token token, Type type) {
        super.token = token;
        super.type = type;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
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

            super.chainType = symbolTable.getAttributeType(
                    super.token.getLexeme(), parentType);
            if (super.chainType == null) {
                throw new SemanticException("El atributo "
                        + super.token.getLexeme() + " no ha sido declarado en" +
                        " la clase "+parentType+".", super.token.row,
                        super.token.col);
            }

            if (!symbolTable.isAttributePub(super.token.getLexeme(),parentType)) {
                if (!Objects.equals(symbolTable.getCurrentClass().getId(), parentType)) {
                    super.chainType=null;
                    throw new SemanticException("El atributo "
                            + super.token.getLexeme() + " de la clase "+parentType+
                            " tiene visibilidad privada. Por lo que no ha podido ser " +
                            "accedido", super.token.row, super.token.col);
                }

            }

        }
        else {
            // Comienzo del acceso a variable encadenada
            if (super.token!=null) {
                if (Objects.equals(super.token.getLexeme(), "self")) {
                    // SELF
                    if (symbolTable.getCurrentClass()!=null) {
                        super.chainType = new ReferenceType(symbolTable.
                                getCurrentClass().getId());
                    }
                    else {
                        // Llamado self en Main
                        throw new SemanticException("No se puede utilizar la " +
                                "referencia \"self\" en el main.",
                                super.token.row, super.token.col);
                    }

                    // SE PUEDE LANZAR UN ERROR POR UTILIZAR MAL EL SELF
                }
                else {
                    super.chainType = symbolTable.getVariableType(
                            super.token.getLexeme(), false);
                    if (super.chainType == null) {
                        throw new SemanticException("La variable " +
                                super.token.getLexeme() + " no ha sido " +
                                "declarada.", super.token.row,
                                super.token.col);
                    }
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
                "\"nombre\": \""+"nodoVar"+((chain==null)?"":"(Encadenado)")+
                "\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"lexema\": \""+this.token.getLexeme()+"\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"tipo\": \""+this.chainType+"\",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"encadenado\": "+((chain==null)?"\"\"":"")+ "\n");
        if (chain!=null) {
            astJson = astJson.concat(chain.toJson(indents)+"\n");
        }


        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
