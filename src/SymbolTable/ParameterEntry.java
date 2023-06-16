package SymbolTable;

import DataType.Type;
import Frontend.Lexer.Token;
import Utils.IJsonable;
import Utils.StringUtils;

/**
 * Esta clase representa un parámetro (argumento) que tendrá asociado un método.
 * Incluye información relevante al mismo, junto a métodos de utilidad.
 *
 * @author Juan Martín Morales
 */
public class ParameterEntry extends VarEntry {

    public ParameterEntry(String id, Type type) {
        super(id, type);
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
