package SymbolTable;

import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import DataType.ArrayType;
import DataType.PrimitiveType;
import DataType.Type;
import Utils.IJsonable;
import Utils.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

/**
 * Esta clase representa la tabla de símbolos utilizada por el compilador para
 * almacenar la información relevante obtenida de las clases y el método main.
 * También tiene una referencia a la clase actual, como tambíen al método actual
 * Además contiene métodos necesarios para la manipulación de la tabla.
 */
public class SymbolTable implements IJsonable {
    private String fileName;                        // Nombre del archivo
    private HashMap<String,ClassEntry> classTable;  // Map con las clases declaradas
    private ClassEntry currentClass;                // clase actual
    private Method currentMethod;                   // método actual
    private MainEntry main;                         // método main

    /**
     * Constructor de la tabla de símbolos
     */
    public SymbolTable(){
        this.classTable = new HashMap<String, ClassEntry>();
        this.main = new MainEntry();
        initPredefinedClasses();
    }

    /**
     * Inicialización y almacenamiento de las clases predefinidas, junto a
     * sus métodos. Las clases son: Object, IO, Str, I32, Bool, Char.
     */
    private void initPredefinedClasses(){
        // Clase Predefinida Object
        ClassEntry object = new ClassEntry("Object", true);
        addClass(object);
        // Clase Predefinida IO
        ClassEntry io = new ClassEntry("IO", true);
        io.setInheritance("Object");
        MethodEntry out_str = new MethodEntry("out_str", new Type(), true);
        out_str.addParameter(new ParameterEntry("s",
                new PrimitiveType("Str")));
        MethodEntry out_i32 = new MethodEntry("out_i32", new Type(), true);
        out_i32.addParameter(new ParameterEntry("i", new PrimitiveType("I32")));
        MethodEntry out_bool = new MethodEntry("out_bool", new Type(), true);
        out_bool.addParameter(new ParameterEntry("b",
                new PrimitiveType("Bool")));
        MethodEntry out_char = new MethodEntry("out_char", new Type(), true);
        out_char.addParameter(new ParameterEntry("c",
                new PrimitiveType("Char")));
        MethodEntry out_array = new MethodEntry("out_array", new Type(), true);
        out_array.addParameter(new ParameterEntry("a", new ArrayType()));
        MethodEntry in_str = new MethodEntry("in_str", new PrimitiveType("Str"),
                true);
        MethodEntry in_i32 = new MethodEntry("in_i32", new PrimitiveType("I32"),
                true);
        MethodEntry in_bool = new MethodEntry("in_bool",
                new PrimitiveType("Bool"), true);
        MethodEntry in_Char = new MethodEntry("in_Char",
                new PrimitiveType("Char"), true);
        io.addMethod(out_str, true); io.addMethod(out_i32, true);
        io.addMethod(out_bool, true); io.addMethod(out_char, true);
        io.addMethod(out_array, true); io.addMethod(in_str, true);
        io.addMethod(in_i32, true); io.addMethod(in_bool, true);
        io.addMethod(in_Char, true);
        addClass(io);
        // Clase Predefinida Array
        ClassEntry array = new ClassEntry("Array", false);
        array.setInheritance("Object");
        MethodEntry length = new MethodEntry("length", new Type("I32"), true);
        array.addMethod(length, true);
        addClass(array);
        // Clase Predefinida Str
        ClassEntry str = new ClassEntry("Str", false);
        str.setInheritance("Object");
        MethodEntry length_str = new MethodEntry("length", new PrimitiveType("I32"), true);
        MethodEntry concat = new MethodEntry("concat", new PrimitiveType("Str"), true);
        concat.addParameter(new ParameterEntry("s", new PrimitiveType("Str")));
        MethodEntry substr = new MethodEntry("substr", new PrimitiveType("Str"), true);
        substr.addParameter(new ParameterEntry("i", new PrimitiveType("I32")));
        substr.addParameter(new ParameterEntry("l", new PrimitiveType("I32")));
        str.addMethod(length_str, true); str.addMethod(concat, true);
        str.addMethod(substr, true);
        addClass(str);
        // Clase Predefinida Bool
        ClassEntry bool = new ClassEntry("Bool", false);
        bool.setInheritance("Object");
        addClass(bool);
        // Clase Predefinida Char
        ClassEntry character = new ClassEntry("Char", false);
        character.setInheritance("Object");
        addClass(character);
        // Clase Predefinida I32
        ClassEntry i32 = new ClassEntry("I32", false);
        character.setInheritance("Object");
        addClass(i32);
    }

    /**
     * Agrega, si no existe, una clase en la tabla de clases.
     * @param classEntry clase a insertar.
     * @return classEntry si ya existía en la tabla, sino null.
     */
    public ClassEntry addClass(ClassEntry classEntry){
        return classTable.putIfAbsent(classEntry.getId(), classEntry);
    }

    public ClassEntry getCurrentClass() {
        return currentClass;
    }

    public Method getCurrentMethod() {
        return currentMethod;
    }

    public MainEntry getMain() {
        return main;
    }

    public ClassEntry getClass(String classId){
        return classTable.get(classId);
    }

    public ClassEntry getMeth(String classId){
        return classTable.get(classId);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setCurrentClass(ClassEntry currentClass) {
        this.currentClass = currentClass;
    }

    public void setCurrentClass(String currentClass) {
        this.currentClass = classTable.get(currentClass);
    }

    public void setCurrentMethod(Method currentMethod) {
        this.currentMethod = currentMethod;
    }

    public void setCurrentMethod(String currentMethod) {
        if (!Objects.equals(currentMethod, "main")) {
            ClassEntry classEntry = classTable.get(currentClass.getId());
            this.currentMethod = classEntry.getMethod(currentMethod);
            if (this.currentMethod==null) {
                if (Objects.equals(currentMethod, currentClass.getId())){   // El método es un contructor
                    this.currentMethod = classEntry.getConstructor();
                }
            }
        }
        else {  // El método es el main
            this.currentClass = null;
            this.currentMethod = main;
        }

    }

    //
    public VarEntry getVariable(String varId, Method method,
                                ClassEntry classEntry) {
        VarEntry var;
        var = method.getVarPar(varId);
        if (var==null) {
            if (classEntry!=null) {
                var = classEntry.getVariable(varId);
            }
        }
        return var;
    }
    public VarEntry getVariable(String varId, Method method) {
        return method.getVarPar(varId);

    }

    public AttributeEntry getAttribute(String varId, ClassEntry classEntry) {
        return classEntry.getVariable(varId);

    }

    //


    // ------------------------------------------------------------------------ //
    // --------- METODOS USADOS POR AST (para chequeo de sentencias) ---------- //

    // Considerar crear una especie de interfaz/controlador (clase), para no saturar de métodos la clase tabla de símbolos

    /**
     * Obtiene el tipo de una variable de acuerdo al alcance.
     * @param varId identificador de la variable.
     * @param self determina si hay que buscar en la clase actual (atributo)
     *             o en el alcance actual.
     * @return tipo de la variable. null si la clase o la variable no han sido
     * declaradas.
     */
    public Type getVariableType(String varId, boolean self){
        VarEntry variable;

        if (self){
            if (currentClass==null) {
                return null;
            }
            variable = currentClass.getVariable(varId);     // Busca en los atributos de la clase actual
        }
        else {
            if (currentMethod==null) {
                return null;
            }
            variable = currentMethod.getVariable(varId);    // Busca en la variables del método actual
            if (variable==null) {
                ParameterEntry parameterEntry = currentMethod.getParameter(varId); // Buscan en los argumentos del método actual
                if (parameterEntry!=null) {
                    return parameterEntry.getType();
                }
                if (currentClass==null) {
                    return null;
                }
                variable = currentClass.getVariable(varId);
            }
        }
        if (variable!=null) {
            return variable.type;
        }
        else {
            return null;
        }
    }

    /**
     * Obtiene el tipo de un atributo de una clase particular.
     * @param attrId identificador del atributo.
     * @param classId identificador de la clase.
     * @return tipo del atributo. null si la clase o el atributo no han sido
     * declarados.
     */
    public Type getAttributeType(String attrId,  String classId){
        ClassEntry classEntry = classTable.get(classId);
        if (classEntry!=null) {
            AttributeEntry attribute = classEntry.getVariable(attrId);
            if (attribute!=null) {
                return attribute.type;
            }
        }
        return null;
    }

    /**
     * Determina si un atributo de una clase determinada es de acceso global.
     * @param attrId Atributo.
     * @param classId Clase.
     * @return True si el atributo de la clase tiene el modificador pub.
     * False en caso contrario.
     */
    public boolean isAttributePub(String attrId, String classId) {
        ClassEntry classEntry = classTable.get(classId);
        if (classEntry!=null) {
            AttributeEntry attribute = classEntry.getVariable(attrId);
            if (attribute!=null) {
                return attribute.isPub();
            }
        }
        return false;
    }

    /**
     * Obtiene la cantidad de parametros de un método de una clase.
     * @param classId Identificador de la clase.
     * @param methodId Identificador del método.
     * @return Cantidad de parámetros del método de la clase.
     */
    public int getMethodParamAmount(String classId, String methodId) {
        ClassEntry classEntry;
        if (classId!=null) {
            classEntry = classTable.get(classId);
        }
        else { // Si el argumento classId es null, utilizar currentClass
            classEntry = currentClass;
        }

        if (classEntry==null) {
            // La clase no está declarada
            return -1;
        }

        Method method;
        if (Objects.equals(classId, methodId)) {
            // Constructor
            method = classEntry.getConstructor();
        }
        else {
            // Método
            method = classEntry.getMethod(methodId);
        }
        if (method!=null) {
            return method.getParamAmount();
        }
        return -1;

    }

    /**
     * Busca y devuelve el tipo de retorno del método actual, que debe estar
     * declarado en la clase actual.
     * @return Tipo de retorno del método actual.
     */
    public Type getMethodType() {
        MethodEntry method;
        try {
            method = (MethodEntry) currentMethod;
        } catch (ClassCastException e) {
            System.err.println("Debug: Se ha intentado castear un método main o un constructor, a un método de clase");
            return null;
        }

        if (method==null) {
            return null;
        }
        return method.getReturnType();
    }
    /**
     * Obtiene el tipo de retorno de un método "methodId", que debe estar
     * declarado en la clase actual.
     * @param methodId Identificador de método.
     * @return Tipo de retorno del método.
     */
    public Type getMethodType(String methodId) {
        if (currentClass!=null) {
            MethodEntry method = currentClass.getMethod(methodId);
            if (method==null) {
                return null;
            }
            return method.getReturnType();
        }
        return null;
    }

    /**
     * Obtiene el tipo de retorno de un método de una clase particular.
     * @param methodId identificador del método.
     * @param className identificador de la clase.
     * @return null si la clase y/o método no han sido declarados. El tipo de
     * retorno, en caso contrario.
     */
    public Type getMethodType(String methodId, String className) {
        ClassEntry classEntry = classTable.get(className);
        if (classEntry!=null) {
            MethodEntry method = classEntry.getMethod(methodId);
            if (method!=null) {
                return method.getReturnType();
            }
        }
        return null;
    }

    /**
     * Obtiene el tipo de retorno un método estático.
     * @param methodId identificador del método estático.
     * @param className identificador de la clase que contiene el método.
     * @return null si el método no es estático o la clase y/o método no han
     * sido declarados. El tipo de retorno, en caso contrario.
     */
    public Type getStaticMethodType(String methodId, String className) {
        ClassEntry classEntry = classTable.get(className);
        if (classEntry!=null) { // Debe existir la clase
            MethodEntry method = classEntry.getMethod(methodId);
            if (method!=null) { // Debe existir el método
                if (method.isStatic()) { // Debe ser estático
                    return method.getReturnType();
                }
            }
        }
        return null;
    }

    /**
     * Verifica que el tipo de un parámetro, de un método declarado, en una
     * clase declarada coincida con el tipo de una expresión.
     * @param expType tipo de la expresión.
     * @param index índice del parámetro referenciado.
     * @param classId identificador de la clase que contiene el método.
     * @param methodId identificador del método.
     * @return True si los tipos coinciden. False en caso contrario.
     */
    public boolean typeCheckParamExp(Type expType, int index, String classId,
                                     String methodId) {
        ClassEntry classEntry;
        if (classId!=null) {    // Si el argumento classId es null, utilizar currentClass
            classEntry = classTable.get(classId);
        }
        else {
            classEntry = currentClass;
        }

        if (classEntry==null) {
            // La clase no está declarada
            return false;
        }

        Method method;
        if (Objects.equals(classId, methodId)) {
            // Constructor
            method = classEntry.getConstructor();
        }
        else {
            // Método
            method = classEntry.getMethod(methodId);
        }

        if (method==null) {
            // El método no esta declarado
            return false;
        }
        Type paramType = method.getParamType(index);
        return paramType.equals(expType);
    }

    /**
     * Verifica que un método haya sido declarada en la clase especificada.
     * @param methodId Identificador del método.
     * @param classId Identificador de la clase.
     * @return True si la clase especificada contiene al método. False en
     * caso contrario.
     */
    public boolean containsMethod(String methodId, String classId) {
        ClassEntry classEntry = classTable.get(classId);
        if (classEntry!=null) {
            return classEntry.getMethods().containsKey(methodId);
        }
        return false;
    }

    /**
     * Verifica si se ha declarado la clase.
     * @param classId Identificador de la clase.
     * @return true si className ha sido declarado, false en caso contrario.
     */
    public boolean containsClass(String classId) {
        return classTable.containsKey(classId);
    }

    /**
     * Verifica que se satisfaga el polimorfismo en una asignación.
     * @param leftType Tipo del lado izquierdo de la asignación.
     * @param rightType Tipo del lado derecho de la asignación.
     * @return True si el tipo (clase) del lado derecho es igual al tipo (clase)
     * del lado izquierdo, o es una subclase de la clase asociada al lado
     * lado izquierdo. False en caso contrario.
     */
    public boolean satisfiesPolymorphism(Type leftType, Type rightType) {
        String leftType_str = leftType.getType();
        String rightType_str = rightType.getType();
        do {
            if (Objects.equals(rightType_str, leftType_str)) {
                return true;
            }
            if (DataType.Type.isReference(leftType) && Objects.equals(rightType_str, "nil")) {
                return true;
            }
            rightType_str = getClass(rightType_str).getInheritance();
        } while (!Objects.equals(rightType, "Object"));
        return false;
    }

    // ------------------ FIN DE MÉTODOS USADOS POR AST --------------------- //
    // ---------------------------------------------------------------------- //

    /**
     * Punto de entrada para consolidar la tabla de símbolos una vez leído
     * todo el código fuente.
     * @throws SemanticException Cuando se produzca un error semántico en la
     * consolidación de la tabla de símbolos.
     */
    public void consolidateST() throws SemanticException {
        HashSet<ClassEntry> solvedC = new HashSet<>(); // Clases ya resueltas
        for (ClassEntry classEntry:classTable.values()) {
            HashSet<ClassEntry> inhCS = new HashSet<>(); // Recorrido de herencia
            solveInheritance(classEntry,inhCS, solvedC);
            solveReferences(classEntry);
            solvedC.add(classEntry);
        }
    }

    /**
     * Resuelve las referencias de tipo de atributos y de variables locales (en
     * los métodos de una clase determinada).
     * @param classEntry Entrada de una clase en la tabla de símbolos.
     * @throws SemanticException Cuando se produzca un error semántico
     * mientras se resuelvan las referencias a otras clases (tipos).
     */
    private void solveReferences(ClassEntry classEntry)
            throws SemanticException {
        HashMap<String, AttributeEntry> attributes = classEntry.getVariables();
        Type type;
        for (AttributeEntry attribute: attributes.values()) {
            type = attribute.getType();
            solveReferenceType(type, attribute.getToken(), " el atributo ");
        }
        HashMap<String, MethodEntry> methods = classEntry.getMethods();
        for (MethodEntry method: methods.values()) {
            HashMap<String, ParameterEntry> parameters = method.getParameters();
            for (ParameterEntry parameter: parameters.values()) {
                type = parameter.getType();
                solveReferenceType(type, parameter.getToken(), " el argumento ");
            }
            HashMap<String, VarEntry> variables = method.getVariables();
            for (VarEntry variable: variables.values()) {
                type = variable.getType();
                solveReferenceType(type, variable.getToken() ," la variable ");
            }
        }
    }

    /**
     * Determina si se puede tipar una variable/parámetro.
     * @param type Tipo de la variable/parámetro.
     * @param token Token del identificador de la variable/parámetro.
     * @param entity string que determina si es variable, atributo o
     *               parámetro (argumento).
     * @throws SemanticException Cuando se produzca un error semántico.
     */
    private void solveReferenceType(Type type, Token token, String entity)
            throws SemanticException {
        String typeDesc;
        if (Objects.equals(type.getType(), "Array[]")) {
            typeDesc = ((ArrayType) (type)).getArrayType();
        }
        else {
            typeDesc = type.getType();
        }
        if (!classTable.containsKey(typeDesc)) {
            throw new SemanticException("La clase " +typeDesc+ " no " +
                    "ha sido declarada. No se ha podido tipar" +
                    entity+token.getLexeme()+".", token.row, token.col);
        }
    }

    /**
     * A medida que resuelve el árbol de herencias recursivamente, chequea si
     * existen herencias cíclicas o clases no declaradas.
     * @param classEntry Entrada de la clase a resolver.
     * @param inhCS conjunto de clases ya exploradas. Útil para corroborar
     *              herencias cíclicas.
     * @return True si no han habido errores. False en caso contrario.
     * @throws SemanticException Cuando se produzca un error semántico al
     * intentar resolver el árbol de dependencias.
     */
    private boolean solveInheritance(ClassEntry classEntry,
                                     HashSet<ClassEntry> inhCS,
                                     HashSet<ClassEntry> solvedClasses)
            throws SemanticException {
        String inhStr = classEntry.getInheritance();
        ClassEntry inh = classTable.get(inhStr);
        if (inhCS.contains(inh)) {
            throw new SemanticException("Se ha encontrado herencia cíclica" +
                    " en las clases " + StringUtils.getClassesString(inhCS) +
                    " y "+classEntry.getId(), classEntry.rowDecl,
                    classEntry.colDecl);
        }


        if (inh==null) {
            throw new SemanticException("La clase "+classEntry.getInheritance()+
                    " no esta declarada", classEntry.rowIDecl,
                    classEntry.colIDecl);
        }

        if (!inh.isInheritable()) {
            throw new SemanticException("La clase "+classEntry.getInheritance()+
                    " no es heredable", classEntry.rowIDecl,
                    classEntry.colIDecl);
        }
        if (Objects.equals(inh.getId(), "Object")) {
            return true;
        }

        inhCS.add(classEntry);
        boolean solved = true;
        if (!solvedClasses.contains(inh)) {
            solved = solveInheritance(inh, inhCS, solvedClasses);
        }

        classEntry.inheritAttributes(inh);
        classEntry.inheritMethods(inh);
        return solved;
    }

    @Override
    public String toJson(int indents) {
        String sTJson = "";
        sTJson = sTJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        sTJson = sTJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+fileName+"\",\n");
        sTJson = sTJson.concat(StringUtils.multiTabs(indents)+
                "\"clases\": [");
        int i = 0;
        int lastIndex = classTable.size()-1;
        for (ClassEntry classEntry:classTable.values()) {
            sTJson = sTJson.concat("\n"+classEntry.toJson(indents));
            if (i<lastIndex) {
                sTJson = sTJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            sTJson = sTJson.concat(StringUtils.multiTabs(indents)+"],\n");
        }
        else {
            sTJson = sTJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }
        sTJson = sTJson.concat(StringUtils.multiTabs(indents)+
                "\"main\":\n");
        sTJson = sTJson.concat(main.toJson(indents+1)+"\n");


        indents--;
        sTJson = sTJson.concat(StringUtils.multiTabs(indents)+"}");
        return sTJson;
    }
}
