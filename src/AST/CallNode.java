package AST;

import Backend.VisitorCodeGen;
import DataType.ReferenceType;
import DataType.Type;
import Frontend.Lexer.Token;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;
import Utils.StringUtils;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Esta clase representa un Nodo de un llamado a método. Es un subclase del
 * Nodo Acceso (AccessNode).
 * Los llamados que incluye son a: constructores, métodos estáticos y no
 * estáticos.
 */
public class CallNode extends AccessNode {
    private Token staticClassT;         // token de la clase de un método
    // estático
    private ArrayList<ExpNode> paramExp;// Lista de expresiones (argumentos)
    private boolean isConstructor;      // Determina si es un llamado a un
    // constructor

    public CallNode() {
        paramExp = new ArrayList<ExpNode>();
    }

    public CallNode(boolean isConstructor) {
        paramExp = new ArrayList<ExpNode>();
        this.isConstructor = isConstructor;
    }

    /**
     * Setter del token asociado a la clase que contiene un método estático
     * @param staticClassT Token de tipo CLASSID
     */
    public void setStaticClassT(Token staticClassT) {
        this.staticClassT = staticClassT;
    }

    /**
     * Setter del token asociado a la llamada
     * @param token Token de tipo ID
     */
    public void setToken(Token token) {
        super.token = token;
    }

    /**
     * Agrega una expresión a la lista de expresiones que utilizará el
     * llamado al método
     * @param exp Expresión
     */
    public void addParamExp(ExpNode exp) {
        paramExp.add(exp);
    }
    @Deprecated
    public String getStaticClassId() {
        if (super.parent instanceof CallNode) {
            if (((CallNode) super.parent).staticClassT!=null){
                return ((CallNode) super.parent).staticClassT.getLexeme();
            }
        }
        return null;
    }

    public Token getStaticClassT() {
        return staticClassT;
    }

    public ArrayList<ExpNode> getParamExp() {
        return paramExp;
    }

    public boolean isStatic() {
        if (staticClassT==null) {
            return false;
        }
        return true;
    }

    public boolean isConstructor() {
        return isConstructor;
    }

    @Override
    public void sentenceCheck(SymbolTable symbolTable) throws SemanticException {
        String methodId;
        String classId;

        if (super.parent instanceof AccessNode) { // Llamada ya encadenada
            methodId = super.token.getLexeme();
            classId = ((AccessNode) super.parent).chainType.getType();
            if (!symbolTable.containsClass(classId)) {
                throw new SemanticException("La clase " + classId + " no ha " +
                        "sido declarada.", super.token.row,
                        super.token.col);
            }
            super.chainType = symbolTable.getMethodType(methodId, classId);

            if (super.chainType == null) {
                throw new SemanticException("El método de instancia " +
                        methodId + " no ha sido declarado en la clase "+
                        classId + ".", super.token.row,
                        super.token.col);
            }
        }
        else {  // COMIENZO de la llamada a método encadenado
            if (staticClassT!=null) {
                // MÉTODO ESTÁTICO
                classId = staticClassT.getLexeme();
                // Verificar que la clase esté declarada.
                if (!symbolTable.containsClass(classId)) {  // Compruebo que la clase este declarada
                    throw new SemanticException("La clase " + classId +
                            " no ha sido declarada.", super.token.row,
                            super.token.col);
                }
                methodId = super.token.getLexeme();
                // Verificar que el método esté declarado en la clase.
                if (!symbolTable.containsMethod(methodId, classId)) {
                    throw new SemanticException("El método " + methodId +
                            " no ha sido declarado en la clase "+
                            classId+".", super.token.row,
                            super.token.col);
                }
                super.chainType = symbolTable.getStaticMethodType(methodId,
                        classId);
                // Verificar si el método es estático.
                if (chainType==null) {
                    throw new SemanticException("El método " + methodId +
                            " en la clase "+ classId+" no es estático.",
                            super.token.row, super.token.col);
                }
            }
            else {
                if (isConstructor) {
                    // CONSTRUCTOR
                    classId = super.token.getLexeme();
                    // Verificar que la clase esté declarada.
                    if (!symbolTable.containsClass(classId)) {  // Compruebo que la clase este declarada
                        throw new SemanticException("La clase " + classId +
                                " no ha sido declarada.", super.token.row,
                                super.token.col);
                    }
                    methodId = classId;
                    super.chainType = new ReferenceType(classId);


                } else {
                    // MÉTODO DE INSTANCIA
                    methodId = super.token.getLexeme();
                    classId = null;
                    super.chainType = symbolTable.getMethodType(methodId);
                    if (chainType==null) {
                        throw new SemanticException("El método " + methodId +
                                " no está declarado en el bloque actual " +
                                symbolTable.getCurrentClass().getId()+".",
                                super.token.row, super.token.col); // si es null significa que estamos en el MAIN
                    }
                }
            }
        }
        // Chequeo de expresiones con parámetros
        int i = 0;
        int paramAmount = symbolTable.getMethodParamAmount(classId, methodId);
        if (paramExp.size()!=paramAmount) {
            throw new SemanticException("("+classId+"."+methodId+"). La " +
                    "cantidad de parámetros del metodo ("+paramExp.size() +
                    ") no coincide con la cantidad de expresiones (" + paramAmount +
                    ").", super.token.row, super.token.col);

        }
        for (ExpNode exp:paramExp) {
            exp.sentenceCheck(symbolTable);;
            if (!symbolTable.typeCheckParamExp(exp.type, i, classId, methodId)) {
                throw new SemanticException("("+classId+"."+methodId+"). El " +
                        "tipo del parámetro ["+ (i+1) + "] del metodo, no " +
                        "coincide con la expresión correspondiente.",
                        super.token.row, super.token.col);
            }
            i++;
        }

        if (chain!=null) {
            chain.sentenceCheck(symbolTable);
            super.type = chain.type;
        }
        else {
            super.type = chainType;
        }

    }

    @Override
    public String toJson(int indents) {
        String astJson = "";
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"{\n");
        indents++;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"nombre\": \""+"nodoLlamadaMetodo"+((chain==null)?"":
                "(Encadenado)")+
                "\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"lexema\": \""+this.token.getLexeme()+"\",\n");
        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"tipo\": \""+this.chainType+"\",\n");

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"parametros\": [");
        int i = 0;
        int lastIndex = paramExp.size()-1;
        for (ExpNode param:paramExp) {
            astJson = astJson.concat("\n"+param.toJson(indents));
            if (i<lastIndex) {
                astJson = astJson.concat(",");
            }
            i++;
        }
        if (i==0) {
            astJson = astJson.concat(StringUtils.multiTabs(indents)+"],\n");
        }
        else {
            astJson = astJson.concat("\n"+StringUtils.multiTabs(indents)+"],\n");
        }

        astJson = astJson.concat(StringUtils.multiTabs(indents)+
                "\"encadenado\": "+((chain==null)?"\"\"":"")+ "\n");
        if (chain!=null) {
            astJson = astJson.concat(chain.toJson(indents+1)+"\n");
        }
        indents--;
        astJson = astJson.concat(StringUtils.multiTabs(indents)+"}");
        return astJson;
    }


    @Override
    public void codeGen(VisitorCodeGen visitor) {
        visitor.visit(this);
    }
}
