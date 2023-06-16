package AST;

import Backend.VisitorCodeGen;
import DataType.Type;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;

/**
 * Esta clase abstracta representa un Nodo de acceso. Un Nodo de acceso puede
 * ser un llamado a una función o el acceso a una variable.
 * Este tipo de nodos son encadenables con otros Nodos de esta clase.
 * Es una subclase del Nodo Expresión (ExpNode)
 *
 * @author Juan Martín Morales
 */
public abstract class AccessNode extends ExpNode{
    AccessNode chain;                       // Nodo de acceso encadenado a este
    Type chainType;                         // Tipo del Nodo particular

    /**
     * Setter del nodo encadenado
     * @param chain Nodo (
     */
    public void setChain(AccessNode chain) {
        if (chain!=null){
            chain.parent = this;
        }
        this.chain = chain;
    }

    public void addChain(AccessNode chain) {
        AccessNode currentChain = this.chain;
        while (currentChain.getChain()!=null) {
            currentChain = currentChain.getChain();
        }
        currentChain.setChain(chain);
    }

    /**
     * Getter del nodo encadenado
     * @return Nodo encadenado
     */
    public AccessNode getChain() {
        return chain;
    }

    /**
     * Getter del tipo asociado a este nodo particular
     * @return
     */
    public Type getChainType() {
        return chainType;
    }

    /**
     * Determina si el nodo es el primero de la cadena. Es decir, que siendo
     * x0.x1...xn el encadenado de accesos y llamados, x0 representa el nodo
     * actual.
     * @return
     */
    public boolean firstInChain() {
        if (super.parent instanceof AccessNode) {
            return false;
        }
        return true;
    }
}
