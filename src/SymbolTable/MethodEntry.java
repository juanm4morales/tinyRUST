package SymbolTable;

import DataType.Type;
import Utils.StringUtils;

/**
 * Representa a los métodos de instancia y clase (static) declarados dentro de
 * una clase. Hereda características de Method.
 */
public class MethodEntry extends Method {
    private Type returnType;        // tipo de retorno
    private boolean isStatic;       // establece si el método es estático
    private int position;           // posicón del método en la clase

    public MethodEntry(String id, boolean isStatic) {
        super(id);
        this.isStatic = isStatic;
    }
    public MethodEntry(String id, Type returnType, boolean isStatic) {
        super(id);
        this.returnType = returnType;
        this.isStatic = isStatic;
    }

    /**
     * Setter del tipo de retorno del método.
     * @param returnType Tipo del retorno.
     */
    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    /**
     * Indica si el método es estático.
     * @return True si el método es estático. False en caso contrario.
     */
    public boolean isStatic() {
        return isStatic;
    }

    /**
     * Setter de la ubicación del método dentro de la clase correspondiente.
     * @param position Posición del método.
     */
    public void setPosition(int position) {
        this.position = position;
    }

    /**
     * Getter del tipo de retorno del método.
     * @return Tipo de retorno del método.
     */
    public Type getReturnType() {
        return returnType;
    }


    /**
     * Incrementa la posición del método en lo que indique el parámetro inc.
     * @param inc Valor que incrementará la posición del método en la clase.
     */
    public void increasePosition(int inc) {
        this.position = this.position + inc;
    }

    @Override
    public String toJson(int indents) {
        String methodJson = "";
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+
            "\"nombre\": \""+super.id+"\",\n");
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+
            "\"static\": "+this.isStatic+",\n");
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+
            "\"retorno\": \""+this.returnType.toString()+"\",\n");
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+
            "\"posicion\": "+this.position+",\n");
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+
            "\"paramF\": [");
        int i = 0;
        int lastIndex = super.parameters.size()-1;
        for (ParameterEntry parEntry:super.parameters.values()) {
            methodJson = methodJson.concat("\n"+parEntry.toJson(indents));
            if (i<lastIndex) {
                methodJson = methodJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            methodJson = methodJson.concat("],\n");
        }
        else {
            methodJson = methodJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+
                "\"varL\": [");
        i = 0;
        lastIndex = super.variables.size()-1;
        for (VarEntry varEntry:super.variables.values()) {
            methodJson = methodJson.concat("\n"+varEntry.toJson(indents));
            if (i<lastIndex) {
                methodJson = methodJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            methodJson = methodJson.concat("]\n");
        }
        else {
            methodJson = methodJson.concat("\n"+StringUtils.multiTabs(indents)+"]\n");
        }

        indents--;
        methodJson = methodJson.concat(StringUtils.multiTabs(indents)+"}");
        return methodJson;
    }
}
