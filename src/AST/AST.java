package AST;

import Backend.VisitorCodeGen;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.ArrayList;

/**
 * La clase AST representa la raíz del árbol de sintaxis abstracta(AST).
 * Este nodo AST incluye las clases del código fuente y el método main.
 * Es una subclase de Nodo (Node)
 *
 * @author Juan Martín Morales
 */
public class AST extends Node {
    private ArrayList<ClassNode> classes;   // Lista de Nodos Clase
    private MethodNode main;                // Nodo Método main
    private String fileName;                // Nombre del archivo

    public AST() {
        classes = new ArrayList<ClassNode>();
        main = new MethodNode();
    }

    /**
     * Agrega un Nodo clase al nodo.
     * @param classNode Nodo clase a agregar
     */
    public void addClass(ClassNode classNode) {
        if (classNode!=null) {
            classNode.parent = this;
        }
        classes.add(classNode);
    }

    /**
     * Agrega un Nodo método que reprenta el main
     * @param main Nodo método main a agregar
     */
    public void setMain(MethodNode main) {
        if (main!=null) {
            main.parent = this;
        }
        this.main = main;
    }

    /**
     * Setter del nombre del archivo. Útil para el método toJson (de interfaz
     * IJsonable).
     * @param fileName String con el nombre del archivo
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ArrayList<ClassNode> getClasses() {
        return classes;
    }

    public MethodNode getMain() {
        return main;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        for (ClassNode classNode:classes) {
            classNode.sentenceCheck(symbolTable);
        }
        main.sentenceCheck(symbolTable);
    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+fileName+"\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"clases\": [");
        int i = 0;
        int lastIndex = classes.size()-1;
        for (ClassNode classNode:classes) {
            astJson = astJson.concat("\n"+classNode.toJson(indents));
            if (i<lastIndex) {
                astJson = astJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            astJson = astJson.concat(StringUtils.multiTabs(indents)+"],\n");
        }
        else {
            astJson = astJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"main\":\n");
        astJson = astJson.concat(main.toJson(indents)+"\n");

        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }

    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
