package SymbolTable;

import DataType.Type;
import Frontend.Parser.SemanticException;
import Utils.IJsonable;
import Utils.StringUtils;

import java.util.*;

/**
 * Representa a las clases declarados junto a información que da la
 * identidad de la propia clase. Además de otra información y métodos útiles
 * para los chequeos semánticos.
 */
public class ClassEntry implements IJsonable {
    private String id; // Identificador de clase
    private String inheritance; // Identificador de clase heredad
    private boolean inheritable; // Indica si es heredable
    private HashMap<String, AttributeEntry> variables; // Map de atributos
    private int attrAmount; // cantidad de atributos declarados
    private HashMap<String, MethodEntry> methods; // Map de métodos
    private int methodAmount; // cantidad de métodos declarados
    private ConstructorEntry constructor; // Constructor de la clase
    // Atributos útiles para mantener localidad en la consolidación.
    // rowDecl,colDecl: Establecen la localidad del Token asociado al Class ID.
    public int rowDecl, colDecl;
    // Establecen la localidad del Token asociado al Class ID heredado.
    // Inicializados en -1.
    public int rowIDecl, colIDecl;

    public ClassEntry(String id, boolean inheritable){
        this.id = id;
        this.inheritable = inheritable;
        variables = new HashMap<String, AttributeEntry>();
        methods = new HashMap<String, MethodEntry>();
        constructor = new ConstructorEntry(this.id);
        inheritance = "Object";
        attrAmount = 0;
        methodAmount = 0;
        rowIDecl = -1;
        colIDecl = -1;
    }

    /**
     * Asigna la localidad del token asociado a la clase.
     * @param rowDecl Fila del token asociado.
     * @param colDecl Columna del token asociado.
     */
    public void setRowColDecl(int rowDecl, int colDecl) {
        this.rowDecl = rowDecl;
        this.colDecl = colDecl;
    }
    /**
     * Asigna la localidad del token asociado con la herencia.
     * @param rowIDecl Fila del token asociado.
     * @param colIDecl Columna del token asociado.
     */
    public void setRowColIDecl(int rowIDecl, int colIDecl) {
        this.rowIDecl = rowIDecl;
        this.colIDecl = colIDecl;
    }

    /**
     * Setter del identificador de la superclase de esta.
     * @param inheritance Identificador de la superclase.
     */
    public void setInheritance(String inheritance) {
        this.inheritance = inheritance;
    }

    /**
     * Indica si la clase es heredable.
     * @return True si la clase es heredable. False en caso contrario.
     */
    public boolean isInheritable() {
        return inheritable;
    }

    /**
     * Hereda los atributos de la clase especificada. Útil en la consolidación
     * de la tabla de símbolos.
     * @param inheritance Clase a heredar.
     * @throws SemanticException cuando la clase padre ya tiene un atributo
     * declarado con el mismo identificador que en la propia clase.
     */
    public void inheritAttributes(ClassEntry inheritance)
            throws SemanticException {
        HashMap<String, AttributeEntry> variables_inh =
                inheritance.getVariables();
        int inc = variables_inh.size();
        for (AttributeEntry attributeEntry: variables.values()) {
            attributeEntry.increasePosition(inc);
        }
        for (AttributeEntry attributeEntry:variables_inh.values()) {
            if (addVariable(attributeEntry, false)!=null) {
                AttributeEntry attr = variables.get(attributeEntry.getId());
                throw new SemanticException("Redefinición ilegal del atributo "+
                        attributeEntry.getId(),attr.token.row,
                        attr.token.col);
            }
        }
        attrAmount = attrAmount + inc;
    }

    /**
     * Hereda los métodos de la clase especificada. Útil en la consolidación
     * de la tabla de símbolos.
     * @param inheritance ClassEntry inheritance.
     * @throws SemanticException Cuando se ha redefinido un método de la
     * superclase que es estático.
     */
    public void inheritMethods(ClassEntry inheritance) throws SemanticException {
        HashMap<String, MethodEntry> methods_inh = inheritance.getMethods();
        int inc = 0;
        for (MethodEntry method_inh:methods_inh.values()) {
            if (addMethod(method_inh, false)==null) {
                methodAmount++;
                inc++;
            }
            else {
                MethodEntry method = methods.get(method_inh.getId());
                int paramAmount = method.paramAmount;
                int paramAmountSuper = method_inh.paramAmount;
                if (paramAmount!=paramAmountSuper) {
                    throw new SemanticException("La firma del método no " +
                            "coincide. El método "+ method_inh.getId()+" de " +
                            "la superclase contiene "+ paramAmountSuper +
                            " parámetros. La redefinición contiene " +
                            paramAmount+" parámetros.", method.token.row,
                            method.token.col);
                }
                // El método de la subclase tiene la misma cantidad de
                // parámetros que la superclase.
                ArrayList<ParameterEntry> params = new ArrayList<>();
                ArrayList<ParameterEntry> paramsSuper = new ArrayList<>();
                params.addAll(method.parameters.values());
                paramsSuper.addAll(method_inh.parameters.values());
                Comparator<ParameterEntry> comparator =
                        Comparator.comparingInt(ParameterEntry::getPosition);
                Collections.sort(params, comparator);
                Collections.sort(paramsSuper, comparator);

                for (int i=0; i < params.size(); i++) {
                    Type paramType = params.get(i).getType();
                    Type paramSuperType = paramsSuper.get(i).getType();
                    if (!paramSuperType.equals(paramType)) {
                        throw new SemanticException("La firma del método no " +
                                "coincide. El tipo del parámetro en la " +
                                "posición ["+(i+1)+"] del método redefinido "+
                                method.getId()+" no coincide con el tipo de" +
                                "l parámetro correspondiente en la superclase.",
                                method.token.row, method.token.col);
                    }
                }
                // Los tipos de los parámetros del método de la subclase
                // coinciden con los de la superclase
                if (!method_inh.getReturnType().equals(method.getReturnType())){
                    throw new SemanticException("La firma del método no " +
                            "coincide. El tipo de retorno del método " +
                            "redefinido "+ method.getId()+" no coincide con " +
                            "el tipo de retorno del método correspondiente de" +
                            " la superclase.", method.token.row,
                            method.token.col);
                }
                // El tipo de retorno del método de la subclase coincide con
                // el de la superclase
                if (methods_inh.get(method_inh.getId()).isStatic()) {
                    //method = methods.get(methodEntry.getId());
                    throw new SemanticException("El método "+
                            method_inh.getId()+" de la superclase "+
                            inheritance.getId()+" es estático, y no puede ser"+
                            " redefinido por la subclase "+id+".",
                            method.token.row, method.token.col);
                }
            }
        }
        for (MethodEntry methodEntry: methods.values()) {
            if (!methods_inh.containsKey(methodEntry.getId())) {
                methodEntry.increasePosition(inc);
            }
        }
    }

    /**
     * Setter de la entrada del constructor de la clase.
     * @param constructor Entrada del constructor de la clase.
     */
    public void setConstructor(ConstructorEntry constructor) {
        this.constructor = constructor;
    }

    /**
     * Getter del identificador de la clase.
     * @return Identificador de la case.
     */
    public String getId() {
        return id;
    }

    /**
     * Getter del identificador de la clase de la cual hereda.
     * @return Identificador de la superclase.
     */
    public String getInheritance() {
        return inheritance;
    }

    /**
     * Getter del HashMap de los atributos de la clase.
     * @return HashMap de los atributos de la clase.
     */
    public HashMap<String, AttributeEntry> getVariables() {
        return variables;
    }

    /**
     * Getter de la variable asociada el identificador pasado por parámetro.
     * @param variable Identificador del atributo.
     * @return Entrada del atributo de la clase.
     */
    public AttributeEntry getVariable(String variable) {
        return variables.get(variable);
    }

    /**
     * Getter del HashMap de los métodos declarados en la clase.
     * @return HashMap de los métodos de la clase.
     */
    public HashMap<String, MethodEntry> getMethods() {
        return methods;
    }

    /**
     * Getter del método asociado al identificador pasado por parámetro.
     * @param method Identificador del método.
     * @return Entrada del método de la clase-
     */
    public MethodEntry getMethod(String method) {
        return methods.get(method);
    }

    /**
     * Getter del método constructor de la clase.
     * @return Entrada del constructor de la clase.
     */
    public ConstructorEntry getConstructor() {
        return constructor;
    }

    public int getAttrAmount() {
        return attrAmount;
    }

    /**
     * Agrega, si no existe, un atributo declarado al map de atributos.
     * @param variable Entrada del atributo declarado.
     * @param auto determina si establecer la posición del atributo sumandole 1
     *             a la cantidad de atributos declarados antes de el agregado.
     * @return Si no había sido declarado retorna la entrada del atributo. Null
     * en caso contrario.
     */
    public AttributeEntry addVariable(AttributeEntry variable, boolean auto){

        if (auto){
            variable.setPosition(attrAmount);
        }
        attrAmount++;
        return variables.putIfAbsent(variable.getId(), variable);
    }

    /**
     * Agrega, si no existe, un método declarado al map de métodos.
     * @param method Entrada del método declarado.
     * @param auto Determina si establecer la posición del método sumandole 1
     *             a la cantidad de métodos declarados antes de el agregado.
     * @return Si no había sido declarado retorna la entrada del atributo. Null
     * en caso contrario.
     */
    public MethodEntry addMethod(MethodEntry method, boolean auto){
        if (auto){
            method.setPosition(methodAmount);
        }
        methodAmount++;
        return methods.putIfAbsent(method.getId(), method);
    }

    @Override
    public String toJson(int indents) {
        String classJson = "";
        classJson = classJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        classJson = classJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+id+"\",\n");
        classJson = classJson.concat(StringUtils.multiTabs(indents)+
                "\"heredaDe\": \""+inheritance+"\",\n");
        //
        classJson = classJson.concat(StringUtils.multiTabs(indents)+
                "\"atributos\": [");
        int i = 0;
        int lastIndex = variables.size()-1;
        for (VarEntry varEntry:variables.values()) {
            classJson = classJson.concat("\n"+varEntry.toJson(indents));
            if (i<lastIndex) {
                classJson = classJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            classJson = classJson.concat("],\n");
        }
        else {
            classJson = classJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }
        //
        classJson = classJson.concat(StringUtils.multiTabs(indents)+
                "\"metodos\": [");
        i = 0;
        lastIndex = methods.size()-1;
        for (MethodEntry methodEntry:methods.values()) {
            classJson = classJson.concat("\n"+methodEntry.toJson(indents));
            if (i<lastIndex) {
                classJson = classJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            classJson = classJson.concat("],\n");
        }
        else {
            classJson = classJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }

        classJson = classJson.concat(StringUtils.multiTabs(indents)+
                "\"constructor\":\n");
        classJson = classJson.concat(constructor.toJson(indents)+"\n");
        indents--;
        classJson = classJson.concat(StringUtils.multiTabs(indents)+"}");
        return classJson;
    }
}
