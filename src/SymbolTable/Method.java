package SymbolTable;

import DataType.Type;
import Frontend.Lexer.Token;
import Utils.IJsonable;

import java.util.HashMap;

/**
 * Clase que abstrae las características y comportamientos esenciales de todos
 * los tipos de métodos: constructores, main, métodos
 */
public abstract class Method implements IJsonable {
    protected String id;        // identificador del método
    protected HashMap<String, ParameterEntry> parameters; /* Map con los parámetros
    (argumentos) del método. */
    protected int paramAmount; // cantidad de parámetros.
    protected HashMap<String, VarEntry> variables; /* Map con las variables locales
    del método */
    protected Token token;

    public Method(String id) {
        this.id = id;
        this.parameters = new HashMap<String, ParameterEntry>();
        this.variables = new HashMap<String, VarEntry>();
        this.paramAmount = 0;
    }


    /**
     * Getter del token asociado al methodID.
     * @return Token asociado al metodo.
     */
    public Token getToken() {
        return token;
    }

    /*
    @Deprecated
    public boolean sameParameter(ParameterEntry parameter2compare) {
        ParameterEntry parameter = parameters.get(parameter2compare.getId());
        //if (parameter.getPosition())
        return true;
    }
    */

    /**
     * Setter del token asociado al methodID.
     * @param token Token asociado al método.
     */
    public void setToken(Token token) {
        this.token = token;
    }

    /**
     * Getter del identificador del método.
     * @return Identificador del método.
     */
    public String getId() {
        return id;
    }

    /**
     * Indica si el método contiene un parámetro con cierto identificador.
     * @param parameterId Identificador del parámetro.
     * @return True si el metodo contiene el parámetro. False en caso contrario.
     */
    public boolean containsParameter(String parameterId) {
        return parameters.containsKey(parameterId);
    }

    /**
     * Agrega, si no existe, un parámetro al Map de parámetros, asignando su
     * posición, e incrementando la variable paramAmount.
     * @param parameter Entrada del parámetro a agregar.
     * @return Null si ya existe un parámetro con el identificador de parameter.
     * Retorna la entrada del parámetro en caso contario.
     */
    public ParameterEntry addParameter(ParameterEntry parameter){
        parameter.setPosition(this.paramAmount);
        this.paramAmount++;
        return parameters.putIfAbsent(parameter.getId(), parameter);

    }

    /**
     * Agrega, si no existe, una variable al Map de variables locales.
     * @param variable Entrada de la variable local a agregar.
     * @return Null si ya existe una variable local con el mismo identificador
     * que tiene variable. Retorna la entrada de la variable en caso contrario.
     */
    public VarEntry addVariable(VarEntry variable){
        return variables.putIfAbsent(variable.getId(), variable);
    }

    /**
     * Getter de la variable local que contiene el identificador especificado.
     * @param id Identificador de la variable local.
     * @return Entrada de la variable local.
     */
    public VarEntry getVariable(String id){
        return variables.get(id);
    }

    /**
     * Getter del parámetro que contiene el identificador especificado.
     * @param parameterId Identificador del parámetro.
     * @return Entrada del parámetro.
     */
    public ParameterEntry getParameter(String parameterId) {
        return parameters.get(parameterId);
    }

    /**
     * Getter del HashMap de parámetros del método.
     * @return HashMap con los parámetros del método.
     */
    public HashMap<String, ParameterEntry> getParameters() {
        return parameters;
    }

    /**
     * Obtiene el tipo del parámetro ubicado en la posición especificada.
     * @param paramIndex Posición específica del parámetro a consultar.
     * @return Tipo del parámetro ubicado en la posición paramIndex. Null
     * en caso contrario.
     */
    public Type getParamType(int paramIndex) {
        for (ParameterEntry parameter: parameters.values()) {
            if (parameter.indexCorrespondence(paramIndex)) {
                return parameter.getType();
            }
        }
        return null;
    }

    /**
     * Getter de la cantidad de parámetros que tiene la firma del método.
     * @return Cantidad de parámetros que pide el método.
     */
    public int getParamAmount() {
        return paramAmount;
    }

    public int getVarAmount() {
        return variables.size();
    }

    /**
     * Getter del HashMap de variables locales dentro del bloque del
     * método.
     * @return HashMap de las variables locales del método.
     */
    public HashMap<String, VarEntry> getVariables() {
        return variables;
    }
}
