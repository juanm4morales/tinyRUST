package Frontend.Parser;

/**
 * Esta clase representa a los errores semánticos que ocurren en el parser
 * y en la consolidación de la tabla de símbolos
 */
public class SemanticException extends Exception{
    public SemanticException(String message, int row, int col) {
        super("ERROR: SEMANTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |" + "\n" +
                "| LINEA " + row + " | COLUMNA " + col + " | " + message +
                " |");
    }
}
