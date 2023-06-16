package Utils;

import SymbolTable.ClassEntry;

import java.util.HashSet;

/**
 * Esta clase contiene métodos útiles para trabajar con String.
 *
 * @author Juan Martín Morales
 */
public class StringUtils {
    /**
     * Genera un string con una determinada cantidad de tabs ("\t")
     * @param tabs  Cantidad de tabs ("\t") a generar
     * @return Retorna un string con la cantidad de tabs solicitada
     */
    public static String multiTabs(int tabs) {
        String string = "";
        for (int i = 0; i < tabs; i++) {
            string = string.concat("\t");
        }
        return string;
    }

    /**
     * Dado un conjunto de ClassEntry's retorna un string con el listado de
     * identificadores de las clases del conjunto.
     * @param classes HashSet con entradas de clase
     * @return String con listado de los correspondientes identificadores de
     * clases.
     */
    public static String getClassesString(HashSet<ClassEntry> classes) {
        String classesString = "";
        int i = 0;
        int size = classes.size();
        for (ClassEntry classEntry:classes) {
            if (i<size-1){
                classesString = classesString.concat(classEntry.getId()+", ");
            }
            else {
                classesString = classesString.concat(classEntry.getId());
            }
        }
        return classesString;
    }

    /**
     * Dado un string, retorna un identificador válido para un label en MIPS.
     * Añade el prefijo "str_" y luego agrega los caracteres que son válidos,
     * si no lo son, agrega el código ascii correspondiente.
     * @param string Cadena de la cual se quiere obtener un label válido
     * @return Cadena con el label válido.
     */
    public static String getAsciiLabel(String string) {
        StringBuilder sb = new StringBuilder("str_");
        char currentChar;
        for (int i = 0; i<string.length(); i++) {
            currentChar = string.charAt(i);
            if (Character.isDigit(currentChar) || Character.isLetter(currentChar)
            || currentChar=='_') {
                sb.append(currentChar);
            }
            else {
                sb.append((int) currentChar);
            }
        }
        return sb.toString();
    }
}
