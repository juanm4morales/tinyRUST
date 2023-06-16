package AST;

import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * La clase BlockNode representa un bloque de un método, constructor,
 * estructura de control, etc. Contiene una lista ordena de las sentencias
 * dentro de este. Es una subclase de Nodo Sentencia (SentenceNode).
 *
 * @author Juan Martín Morales
 */
public class BlockNode extends SentenceNode {
    private ArrayList<SentenceNode> sentences; // Lista de Nodos Sentencia

    public BlockNode() {
        sentences = new ArrayList<>();
    }

    /**
     * Agrega una sentencia a la lista de sentencias. Además establece la
     * relación padre/hijo entre esta instancia y la de la sentencia.
     * @param sentence Nodo sentencia
     */
    public void addSentence(SentenceNode sentence) {
        if (sentence!=null) {
            sentence.parent = this;
        }
        sentences.add(sentence);
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        boolean hasReturn = false;
        for (SentenceNode sentece:sentences) {
            if (sentece!=null) {
                sentece.sentenceCheck(symbolTable);
                if (!hasReturn) {
                    hasReturn = sentece.hasReturnStmt();
                }
            }

        }
        if (super.parent instanceof MethodNode) {
            String methodName = ((MethodNode) super.parent).getName();
            Type returnType = symbolTable.getMethodType(methodName);
            if (returnType!=null && !Objects.equals(returnType.getType(), "Void")) {
                if (!hasReturn) {
                    throw new SemanticException("(" + symbolTable
                            .getCurrentClass().getId()+"."+methodName+") " +
                            "Se esperaba una expresión de retorno de tipo " +
                            returnType +".",
                            symbolTable.getCurrentMethod().getToken().row,
                            symbolTable.getCurrentMethod().getToken().row);
                }
            }

        }
    }

    public ArrayList<SentenceNode> getSentences() {
        return sentences;
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"sentencias\": [");
        int i = 0;
        int lastIndex = sentences.size()-1;
        for (SentenceNode sentence:sentences) {
            astJson = astJson.concat("\n"+sentence.toJson(indents));
            if (i<lastIndex) {
                astJson = astJson.concat(",");
            }
            i++;
        }

        astJson = astJson.concat("\n"+StringUtils.multiTabs(indents)+"]\n");


        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
