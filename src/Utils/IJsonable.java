package Utils;

/**
 * Esta interfaz define el comportamiento de un objeto del paquete SymbolTable,
 * para obtener su bloque de objeto en formato JSON
 */
public interface IJsonable {
    /**
     * Convierte al objeto de la tabla de símbolos en un String con formato JSON
     * @param indents cantidad de identaciones del bloque del objeto
     * @return String con formato JSON
     */
    String toJson(int indents);
}
