package Frontend.Runner;

import Frontend.Lexer.FileReaderWriter;
import Frontend.Lexer.Lexer;
import Frontend.Lexer.LexerException;
import Frontend.Lexer.Token;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Runner representa el ejecutador de la etapa de análisis léxico de nuestro
 * código fuente.
 */
public class Runner {
    ArrayList<Token> tokens;    // Arreglo de tokens del código fuente
    String path;                // Direción del archivo a escanear
    boolean error;              // Atributo que determina si ha habido un error
    // Constructor de clase
    public Runner(String path){
        this.path = path;
        tokens = new ArrayList<Token>();
        error = false;
    }

    /**
     * run() lee un archivo fuente e instancia al Lexer. Luego coloca en
     * el arreglo "tokens" todos los tokens del sourceCode hasta leer EOF
     * o hasta que se produzca un error.
     */
    public void run(){
        BufferedReader sourceCode = null;
        try {
            sourceCode = FileReaderWriter.read(path);
        } catch (FileNotFoundException e) {
            error = true;
            System.err.println("El archivo " + path + " no existe");
            return;
        }
        Lexer lexer = new Lexer(sourceCode);
        try {
            Token token;
            while (!lexer.EOF){
                token = lexer.getNextToken();
                if (token != null){
                    tokens.add(token);
                }
            }
        } catch (IOException | LexerException e) {
            error = true;
            System.err.println(e.getMessage());
        }
    }

    /**
     * Imprime los tokens identificados
     */
    public void printTokens(){
        Token token;
        if (!error){
            System.out.println("CORRECTO: ANALISIS LEXICO \n| TOKEN |" +
                    " LEXEMA | NÚMERO DE LÍNEA (NÚMERO DE COLUMNA) |");
            for (Token value : tokens) {
                token = value;
                System.out.println("| "+token.getTag()+" | "+token.getLexeme()+
                        " | LINEA "+token.row+" (COLUMNA "+token.col+") |");
            }
        }
    }
    // main
    public static void main(String[] args) {
        String path;
        if (args.length!=1){
            System.err.println("Se debe pasar un argumento...");
            return;
        }
        else {
            path = args[0];
        }
        Runner runner = new Runner(path);
        runner.run();
        runner.printTokens();
    }
}
