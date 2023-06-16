package DataType;

/**
 * Esta clase representa el tipo primitivo. Las características están
 * discretizadas en: I32, Str, Char, Bool
 *
 * @author Juan Martín Morales
 */
public class PrimitiveType extends Type {


    /**
     * Este constructor asegura que el nombre del tipo sea o "I32" o "Str" o
     * "Char" o "Bool
     * @param type
     */
    public PrimitiveType(String type) {
        super(type);

        if (!type.equals(I32) && !type.equals(STR) && !type.equals(CHAR) &&
                !type.equals(BOOL)) {
            throw new IllegalArgumentException("Invalid primitive type: " +type);
        }
    }
}
