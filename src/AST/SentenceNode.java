package AST;

import Backend.VisitorCodeGen;

/**
 * Clase abstracta que representa una sentencia en un método del programa.
 * Subclase de Nodo (Node)
 */
public abstract class SentenceNode extends Node {
    private boolean hasReturnStmt;      // Determina si la sentencia, o dentro de esta hay un return

    /**
     * Getter de hasReturnStmt;
     * @return True si la sentencia, o detro de esta hay un return
     */
    public boolean hasReturnStmt() {
        return hasReturnStmt;
    }

    /**
     * Setter de hasReturnStmt
     * @param hasReturnStmt
     */
    public void setHasReturnStmt(boolean hasReturnStmt) {
        this.hasReturnStmt = hasReturnStmt;
    }



}
