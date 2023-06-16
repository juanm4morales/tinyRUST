package AST;

import Backend.VisitorCodeGen;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;
/**
 * La clase MethodNode representa un Nodo de un método: método de instancia,
 * de clase (static), constructor, método main. Contiene un bloque con las
 * sentencias del método en cuestión. Es una subclase de Nodo (Node).
 *
 * @author Juan Martín Morales
 */
public class MethodNode extends Node {
    private String name;            // Identificador del método
    private BlockNode block;        // Nodo Bloque

    /**
     * Setter del nombre de la clase
     * @param name Identificador de la clase
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter del Nodo bloque
     * @param block Nodo Bloque de sentencias
     */
    public void setBlock(BlockNode block) {
        if (block!=null) {
            block.parent = this;
        }
        this.block = block;
    }

    public String getName() {
        return name;
    }
    public BlockNode getBlock() {
        return block;
    }
    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        symbolTable.setCurrentMethod(name);
        if (block != null) {
            block.sentenceCheck(symbolTable);
        }
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+name+"\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"Bloque\":");

        astJson = astJson.concat("\n"+block.toJson(indents)+"\n");
        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
