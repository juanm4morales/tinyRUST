package SymbolTable;

import Utils.StringUtils;

import java.util.HashMap;

/**
 * Esta clase representa las características esenciales de un constructor de
 * clase. Hereda de la clase abstracta Method.
 *
 * @author Juan Martín Morales
 */
public class ConstructorEntry extends Method {
    public ConstructorEntry(String id) {
        super(id);
    }

    @Override
    public String toJson(int indents) {
        String constrJson = "";
        constrJson = constrJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        constrJson = constrJson.concat(StringUtils.multiTabs(indents)+
                "\"paramF\": [");
        int i = 0;
        int lastIndex = super.parameters.size()-1;
        for (ParameterEntry parEntry:super.parameters.values()) {
            constrJson = constrJson.concat("\n"+parEntry.toJson(indents));
            if (i<lastIndex) {
                constrJson = constrJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            constrJson = constrJson.concat("],\n");
        }
        else {
            constrJson = constrJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }
        constrJson = constrJson.concat(StringUtils.multiTabs(indents)+
                "\"varL\": [");
        i = 0;
        lastIndex = super.variables.size()-1;
        for (VarEntry varEntry:super.variables.values()) {
            constrJson = constrJson.concat("\n"+varEntry.toJson(indents));
            if (i<lastIndex) {
                constrJson = constrJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            constrJson = constrJson.concat("]\n");
        }
        else {
            constrJson = constrJson.concat("\n"+StringUtils.multiTabs(indents)+"]\n");
        }

        indents--;
        constrJson = constrJson.concat(StringUtils.multiTabs(indents)+"}");
        return constrJson;
    }
}
