package AST;

import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Lexer.Token;

/**
 * Clase abstracta que representa a los Nodos Expresiones del AST.
 * Es una subclase del Nodo sentencia (SentenceNode).
 * Contiene información sobre el token asociado, y el tipo de la expresión.
 *
 * @author Juan Martín Morales
 */
public abstract class ExpNode extends SentenceNode {
    Token token;                            // Token asociado a la expresión
    Type type;                              // Tipo de la expresión

    /**
     * Setter del token asociado a la expresión
     * @param token Token asociado a la expresión
     */
    public void setToken(Token token) {
        token = token;
    }

    /**
     * Setter del tipo de la expresión
     * @param type Tipo de la expresión
     */
    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Token getToken() {
        return token;
    }


}
