package Frontend.Lexer;

/**
 * Token representa no solo al token en si, con atributo y/o valor, también
 * indica su ubicación correspondiente en el archivo fuente
  */

public class Token {
    private final String tag;       // Etiqueta del token
    private final String lexeme;    // Lexema del token
    public final int row;           // Fila del código fuente
    public final int col;           // Columna del código fuente
    public Token(String tag, String lexeme, int row, int col) {
        this.tag = tag;
        this.lexeme = lexeme;
        this.row = row;
        this.col = col;
    }
    public String getTag() {
        return tag;
    }
    public String getLexeme() {
        return lexeme;
    }
}

