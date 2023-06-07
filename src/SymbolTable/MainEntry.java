package SymbolTable;

import Utils.StringUtils;

/**
 * Clase que representa al método main. Hereda de la clase Method todas las
 * características necesarias para esta.
 */
public class MainEntry extends Method {
    private static final String MAIN = "main"; // identificador de método constante "main"
    public MainEntry() {
        super(MAIN);
    }

    @Override
    public String toJson(int indents) {
        String mainJson = "";
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \"main\",\n");
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+
                "\"static\": true,\n");
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+
                "\"retorno\": \"void\",\n");
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+
                "\"posicion\": 0,\n");
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+
                "\"paramF\": [");
        int i = 0;
        int lastIndex = super.parameters.size()-1;
        for (ParameterEntry parEntry:super.parameters.values()) {
            mainJson = mainJson.concat("\n"+parEntry.toJson(indents));
            if (i<lastIndex) {
                mainJson = mainJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            mainJson = mainJson.concat("],\n");
        }
        else {
            mainJson = mainJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+
                "\"varL\": [");
        i = 0;
        lastIndex = super.variables.size()-1;
        for (VarEntry varEntry:super.variables.values()) {
            mainJson = mainJson.concat("\n"+varEntry.toJson(indents));
            if (i<lastIndex) {
                mainJson = mainJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            mainJson = mainJson.concat("]\n");
        }
        else {
            mainJson = mainJson.concat("\n"+StringUtils.multiTabs(indents)+"]\n");
        }

        indents--;
        mainJson = mainJson.concat(StringUtils.multiTabs(indents)+"}");
        return mainJson;
    }
}
