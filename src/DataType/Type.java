package DataType;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Esta clase representa un tipo, tanto del retorno de una función, de un
 * atributo/variable local o de un argumento.
 *
 * @author Juan Martín Morales
 */
public class Type {
    private String type;

    private static final String VOID = "Void";
    public static final String ARRAY = "Array";
    public static final String I32 = "I32";
    public static final String STR = "Str";
    public static final String CHAR = "Char";
    public static final String BOOL = "Bool";


    public Type() {
        this.type=VOID;
    }
    public Type(String type) {
        this.type=type;
    }

    /**
     * Getter del identificador principal del tipo.
     * @return Identificador de tipo
     */
    public String getType() {
        return type;
    }

    /**
     * Obtiene un identificador del tipo formateado.
     * @return
     */
    public String toString(){
        return getType();
    }

    /**
     * Compara las características de tipo con las de otra instancia Type
     * @param type Instancia Type con la que comparar.
     * @return True si las características de ambos son iguales. False en caso
     * contrario
     */
    public boolean equals(Type type) {
        if (type == null) {
            return false;
        }
        if (type.type.equals(this.type)) {
            if (type.type.equals("Array")) {
                return Objects.equals(((ArrayType) type).getArrayType(),
                        ((ArrayType) this).getArrayType());
            }
            return true;
        }
        return false;
    }

    /**
     * Crea una nueva instancia del tipo indicado por el parámetro.
     * @param type Nombre del tipo.
     * @return Nueva instancia del tipo solicitado.
     */
    public static Type createType(String type) {
        switch (type.toLowerCase()) {
            case "str": case"string":
                return new PrimitiveType(PrimitiveType.STR);
            case "i32": case "num":
                return new PrimitiveType(PrimitiveType.I32);
            case "char":
                return new PrimitiveType(PrimitiveType.CHAR);
            case "nil":
                return new Type("nil");
            case "true": case "false":
                return new PrimitiveType(PrimitiveType.BOOL);
            default:
                System.err.println("Error en: Type.getType("+type+"). "+type+" no se reconoce como tipo permitido.");
        }
        return null;
    }

    public static boolean isReference(Type type) {
        if (type instanceof ReferenceType) {
            return true;
        }
        return false;

    }
}
