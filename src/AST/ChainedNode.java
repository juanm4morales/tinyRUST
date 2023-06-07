package AST;

@Deprecated
public abstract class ChainedNode extends ExpNode {
    AccessNode chainedNode;
    /*
    public void setChainedNode(ChainableNode chainedNode) {
        if (chainedNode!=null) {
            chainedNode.parent = this;
        }
        this.chainedNode = chainedNode;

    }
    */

}
