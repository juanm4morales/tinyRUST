package SymbolTable;

import DataType.Type;
import Frontend.Lexer.Token;
import Utils.IJsonable;
import Utils.StringUtils;

/**
 * Esta clase representa un parámetro (argumento) que tendrá asociado un método.
 * Incluye información relevante al mismo, junto a métodos de utilidad.
 */
public class ParameterEntry implements IJsonable {
    private String id;
    private Type type;
    private int position;
    private Token token;

    public ParameterEntry(String id, Type type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Getter del token asociado al parámetro.
     * @return Token asociado al parámetro.
     */
    public Token getToken() {
        return token;
    }

    /**
     * Getter del identificador del parámetro.
     * @return Identificador del parámetro.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter del tipo del parámetro.
     * @return Tipo del parámetro.
     */
    public Type getType() {
        return type;
    }

    /**
     * Getter de la posición del parámetro dentro de la lista de parámetros del método.
     * @return Posición del parámetro en el método.
     */
    public int getPosition() {
        return position;
    }

    /**
     * Setter de la posición del parámetro dentro de la lista de parámetros del método.
     * @param position Posición del parámetro en el método.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Setter del token asociado al parámetro.
     * @param token Token asociado al parámetro.
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * Verifica que la posición pasada por parámetro coincida con la de la entrada del parámetro del método.
     * @param index Posición a comprobar.
     * @return True si coincide la posición a consultar con la real. False en caso contrario.
     */
    public boolean indexCorrespondence(int index) {
        return index==position;
    }

    @Override
    public String toJson(int indents) {
        String parJson = "";
        parJson = parJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        parJson = parJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+id+"\",\n");
        parJson = parJson.concat(StringUtils.multiTabs(indents)+
                "\"tipo\": \""+type.toString()+"\",\n");
        parJson = parJson.concat(StringUtils.multiTabs(indents)+
                "\"posicion\": "+position+"\n");
        indents--;
        parJson = parJson.concat(StringUtils.multiTabs(indents)+"}");
        return parJson;
    }
}
