package SymbolTable;

import DataType.Type;
import Frontend.Lexer.Token;
import Utils.IJsonable;
import Utils.StringUtils;

/**
 * Esta clase representa una entrada de una variable local o atributo, en la
 * tabla de símbolos.
 */
public class VarEntry implements IJsonable {
    protected String id;    // Identificador de la variable
    protected Type type;    // Tipo de la variable
    protected Token token;  // Token asociado a la variable
    protected int position; // Posición de la declaración dentro del alcance
    private int size=1;     // Tamaño de la variable. Útil para cadenas,
    // arreglos y objetos

    public VarEntry(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Getter del identificador de la variable.
     * @return Identificador de la variable.
     */
    public String getId() {
        return id;
    }

    public int getSize() {
        return size;
    }
    /**
     * Getter del tipo de la variable.
     * @return Tipo de la variable.
     */
    public Type getType() {
        return type;
    }


    /**
     * Getter del token asociado a la variable.
     * @return Token asociado a la variable.
     */
    public Token getToken() {
        return token;
    }

    public int getPosition() {
        return position;
    }
    /**
     * Setter del token asociado a la variable.
     * @param token Token asociado a la variable.
     */
    public void setToken(Token token) {
        this.token = token;
    }

    public void setSize(int size) {
        this.size = size;
    }
    /**
     * Setter de la posición de la declaración de la variable en el ámbito
     * correspondiente.
     * @param position
     */
    public void setPosition(int position){
        this.position = position;
    }

    /**
     * Incrementa el valor de la posición de la variable.
     * @param inc Incremento a sumar a la posición actual.
     */
    public void increasePosition(int inc) {
        this.position = this.position + inc;
    }

    @Override
    public String toJson(int indents) {
        String varJson = "";
        varJson = varJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        varJson = varJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+id+"\",\n");
        varJson = varJson.concat(StringUtils.multiTabs(indents)+
                "\"tipo\": \""+type.toString()+"\"\n");
        indents--;
        varJson = varJson.concat(StringUtils.multiTabs(indents)+"}");
        return varJson;
    }
}
