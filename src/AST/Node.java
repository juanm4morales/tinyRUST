package AST;

import Backend.VisitorCodeGen;
import Utils.IJsonable;

import java.util.ArrayList;

/**
 * Clase abstracta principal que representa a todos los nodos de un AST.
 * Tiene una referencia hacia el nodo padre.
 */
public abstract class Node implements ISentenceCheck, IJsonable {
    Node parent;            // Nodo padre

    /**
     * Setter de la referencia al Nodo Padre
     * @param parent Nodo padre
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }


    public abstract void codeGen(VisitorCodeGen visitor);

}
