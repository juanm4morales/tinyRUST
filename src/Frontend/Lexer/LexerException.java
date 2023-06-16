package Frontend.Lexer;

/**
 * Esta clase representa a los errores léxicos que ocurren durante el escaneo.
 *
 * @author Juan Martín Morales
 */
public class LexerException extends Exception{
    public LexerException(String message, int row, int col) {
        super("ERROR: LEXICO\n| NUMERO DE LINEA: | NUMERO DE COLUMNA: | DESCRIPCION: |" + "\n" +
                "| LINEA " + row + " | COLUMNA " + col + " | " + message +
                " |");
    }
}
