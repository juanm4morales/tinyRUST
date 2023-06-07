package AST;

import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

/**
 * Esta clase representa un Nodo del AST para la sentencia de control iterativa
 * while. Contiene información sobre la expresión de condición (debe ser de
 * tipo Bool) e información sobre la sentencia que se ejecutará
 * iterativamente mientras se cumpla la condición establecida.
 */
public class WhileNode extends SentenceNode {
    private ExpNode condition;      // Expresión de la condición a cumplir
    // para que entre en el bloque.
    private SentenceNode body;      // Cuerpo del while que se ejecutará
    // mientrás se cumpla la condición

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
     * @param body Sentencia cuerpo, que se ejecutará cuando se cumpla la
     *             condición
     */
    public void setBody(SentenceNode body) {
        if (body!=null) {
            body.parent = this;
        }
        this.body = body;
    }

    public ExpNode getCondition() {
        return condition;
    }

    public SentenceNode getBody() {
        return body;
    }

    /**
     * Setter de la condición y la sentencia body. Además establece
     * como padre de estas, la instancia actual.
     * @param condition  Nodo Expresión (condición)
     * @param body Nodo Sentencia (cuerpo del while).
     */
    public void makeFamily(ExpNode condition, SentenceNode body) {
        setCondition(condition);
        setBody(body);
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        condition.sentenceCheck(symbolTable);
        if (!condition.getType().getType().equals(Type.BOOL)) {
            throw new SemanticException("Se requiere una expresión de tipo" +
                    " Bool en la condición del bucle while.",
                    condition.token.row,
                    condition.token.col);
        }
        body.sentenceCheck(symbolTable);
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \"nodoWhile\",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"condicion\":\n");
        astJson = astJson.concat(condition.toJson(indents)+",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"cuerpo\":\n");
        astJson = astJson.concat(body.toJson(indents)+"\n");


        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
