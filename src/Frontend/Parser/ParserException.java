package Frontend.Parser;

/**
 * Esta clase representa a los errores sintácticos que ocurren en el parser.
 *
 * @author Juan Martín Morales
 */
public class ParserException extends Exception{
    public ParserException(String message, int row, int col) {
        super("ERROR: SINTACTICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |" + "\n" +
                "| LINEA " + row + " | COLUMNA " + col + " | " + message +
                " |");
    }
}
