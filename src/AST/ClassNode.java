package AST;

import Backend.VisitorCodeGen;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.ArrayList;

/**
 * La clase ClassNode representa un Nodo de una clase. Contiene métodos
 * con sus comportamiento (sentencias), y el método constructor. Es una
 * subclase de Nodo (Node)
 */
public class ClassNode extends Node{
    private String name;                    // Identificador de la clase
    private ArrayList<MethodNode> methods;  // Lista de Nodos Métodos
    private MethodNode constructor;         // Nodo Constructor de la clase

    public ClassNode(Token token){
        methods = new ArrayList<>();
    }

    /**
     * Setter del Nodo Método constructor. Además establece la
     * relación padre/hijo entre esta instancia y la del constructor.
     * @param constructor Nodo método (constructor)
     */
    public void setConstructor(MethodNode constructor) {
        if (constructor!=null) {
            constructor.parent = this;
        }
        this.constructor = constructor;
    }

    /**
     * Setter del identificador de la clase (name)
     * @param name Identificador de la case
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Agrega un Nodo Método a la lista de métodos. Además establece la
     * relación padre/hijo entre esta instancia y la del método.
     * @param method Nodo método
     */
    public void addMethod(MethodNode method) {
        if (method!=null){
            method.parent = this;
        }
        methods.add(method);
    }

    public String getName() {
        return name;
    }

    public ArrayList<MethodNode> getMethods() {
        return methods;
    }

    public MethodNode getConstructor() {
        return constructor;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        symbolTable.setCurrentClass(name);
        for (MethodNode method:methods) {
            method.sentenceCheck(symbolTable);
        }
        if (constructor!=null) {
            constructor.sentenceCheck(symbolTable);
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
                "\"metodos\": [");
        int i = 0;
        int lastIndex = methods.size()-1;
        for (MethodNode method:methods) {
            astJson = astJson.concat("\n"+method.toJson(indents));
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
                "\"constructor\": "+((constructor==null)?"\"\"":"")+"\n");

        if (constructor!=null) {
            astJson = astJson.concat(constructor.toJson(indents)+"\n");
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
