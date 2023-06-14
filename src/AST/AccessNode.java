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

    public AccessNode getChain() {
        return chain;
    }

    public boolean firstInChain() {
        if (super.parent instanceof AccessNode) {
            return false;
        }
        return true;
    }
}
