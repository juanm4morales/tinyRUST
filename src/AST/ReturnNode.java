package AST;

import Backend.VisitorCodeGen;
import DataType.ReferenceType;
import DataType.Type;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.Objects;

/**
 * Clase que representa a una sentencia return. Es una subclase de Nodo
 * Sentencia (SentenceNode).
 *
 * @author Juan Martín Morales
 */
public class ReturnNode extends SentenceNode{
    private ExpNode returnVal;      // Nodo expresión de retorno

    public ReturnNode(){
    }

    public ReturnNode(ExpNode returnVal){
        this.returnVal = returnVal;

    }

    public ExpNode getReturnVal() {
        return returnVal;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        Type returnType = symbolTable.getMethodType();
        if (returnVal!=null) {
            returnVal.sentenceCheck(symbolTable);

            if (Objects.equals(returnType.getType(), "Void")) {
                throw new SemanticException("La función no debe retornar " +
                        "ningún valor (void).", returnVal.token.row,
                        returnVal.token.col);
            }

            if (Objects.equals(returnVal.type.getType(), "nil")) {
                if (!(returnType instanceof ReferenceType)) {
                    throw new SemanticException("Una función que no retorne " +
                            "una referencia no puede retornar nil.",
                            returnVal.token.row, returnVal.token.col);
                }
            }
            else {
                if (!returnType.equals(returnVal.type)) {
                    throw new SemanticException("El tipo de la expresión a" +
                            " retornar es "+ returnVal.type.toString()+", y " +
                            "debe ser de tipo "+returnType+".",
                            returnVal.token.row, returnVal.token.col);
                }
            }
        }

    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+"nodoReturn\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"expRetorno\":");
        if (returnVal==null) {
            astJson = astJson.concat(" \"\""+"\n");
        }
        else {
            astJson = astJson.concat("\n"+returnVal.toJson(indents)+"\n");
        }


        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    // Debe verificar el Tipo de returnVal


    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}

