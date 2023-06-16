package SymbolTable;

import DataType.Type;
import Utils.StringUtils;

/**
 * Esta clase representa los atributos declarados junto a información relevante
 * para el análisis y métodos de utilidad.
 *
 * @author Juan Martín Morales
 */
public class AttributeEntry extends VarEntry{
    private boolean pub;            // indica si el atributo es público
    public AttributeEntry(String id, Type type, boolean pub) {
        super(id, type);
        this.pub = pub;
    }

    /**
     * Método que indica si el atributo correspondiente a la entrada tiene acceso global.
     * @return True si el atributo tiene alcance global. False en caso contrario.
     */
    public boolean isPub(){
        return pub;
    }
    @Override
    public String toJson(int indents) {
        String attrJson = "";
        attrJson = attrJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        attrJson = attrJson.concat(StringUtils.multiTabs(indents)+
            "\"nombre\": \""+super.id+"\",\n");
        attrJson = attrJson.concat(StringUtils.multiTabs(indents)+
            "\"tipo\": \""+super.type.toString()+"\",\n");
        attrJson = attrJson.concat(StringUtils.multiTabs(indents)+
            "\"public\": "+this.pub+",\n");
        attrJson = attrJson.concat(StringUtils.multiTabs(indents)+
            "\"posicion\": "+super.position+"\n");
        indents--;
        attrJson = attrJson.concat(StringUtils.multiTabs(indents)+"}");
        return attrJson;
    }
}
