package AST;

import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

/**
 * Esta clase representa un Nodo del AST para una sentencia de control if-else.
 * Contiene información sobre la expresión de condición (debe ser de tipo Bool),
 * información sobre la sentencia que se ejecutará si se cumple la condición,
 * y un atributo opcional con la sentencia que se ejecutará si no se cumple
 * la condición.
 *
 * @author Juan Martín Morales
 */
public class IfElseNode extends SentenceNode {
    private ExpNode condition;          // Expresión condición
    private SentenceNode thenPart;      // Sentencia Entonces (then)
    private SentenceNode elsePart;      // Sentencia SiNo (else)

    /**
     * Setter de la expresión de condición. Además establece el parentezco
     * entre esta instancia y la expresión del parámetro
     * @param condition Expresión que deberá ser de tipo Bool
     */
    public void setCondition(ExpNode condition) {
        if (condition!=null) {
            condition.parent = this;
        }
        this.condition = condition;
    }
    /**
     * Setter de la sentencia Then. Además establece el parentezco
     * entre esta instancia y la expresión del parámetro
     * @param thenPart Sentencia que se ejecutará en caso de que la condición
     *                sea verdadera.
     */
    public void setThenPart(SentenceNode thenPart) {
        if (thenPart!=null) {
            thenPart.parent = this;
        }
        this.thenPart = thenPart;
    }
    /**
     * Setter de la sentencia Else. Además establece el parentezco
     * entre esta instancia y la expresión del parámetro
     * @param elsePart Sentencia que se ejecutará en caso de que la condición
     *                 sea falsa.
     */
    public void setElsePart(SentenceNode elsePart) {
        if (elsePart!=null) {
            elsePart.parent = this;
        }
        this.elsePart = elsePart;
    }

    public ExpNode getCondition() {
        return condition;
    }

    public SentenceNode getThenPart() {
        return thenPart;
    }

    public SentenceNode getElsePart() {
        return elsePart;
    }

    /**
     * Setter de la condición y la sentencia Then. Además establece
     * como padre de estas, la instancia actual.
     * @param condition  Nodo Expresión (izquierda)
     * @param thenPart  Nodo Sentencia (derecha)
     */
    public void makeFamily(ExpNode condition, SentenceNode thenPart) {
        setCondition(condition);
        setThenPart(thenPart);
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        condition.sentenceCheck(symbolTable);
        if (condition.getType().getType()!= Type.BOOL) {
            throw new SemanticException("Se requiere una expresión de tipo" +
                    " Bool para evaluar la condición en la estructura de " +
                    "control If-[else]. Se recibió: "+condition.type.getType() +
                    ".", condition.token.row, condition.token.col);
        }
        thenPart.sentenceCheck(symbolTable);
        if (elsePart!=null) {
            elsePart.sentenceCheck(symbolTable);
        }


    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \"nodoIf(else)\",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"condicion\":\n");
        astJson = astJson.concat(condition.toJson(indents)+",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"parteThen\":\n");
        astJson = astJson.concat(thenPart.toJson(indents)+",\n");
        if (elsePart!=null) {
            astJson = astJson.concat(StringUtils.multiTabs(indents)+
                    "\"parteElse\":\n");
            astJson = astJson.concat(thenPart.toJson(indents)+"\n");
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
