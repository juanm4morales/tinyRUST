package Runner;

import AST.AST;
import Backend.CodeGenerator;
import Frontend.FileReaderWriter;
import Frontend.Lexer.LexerException;
import Frontend.Lexer.Token;
import Frontend.Parser.Parser;
import Frontend.Parser.ParserException;
import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Runner representa el ejecutador de la etapa de análisis semántico con
 * declaraciones sobre un código fuente.
 */
public class Runner {
    ArrayList<Token> tokens;    // Arreglo de tokens del código fuente
    String path;                // Dirección del archivo a escanear
    boolean error;              // Atributo que determina si ha habido un error
    // Constructor de clase
    public Runner(String path){
        this.path = path;
        tokens = new ArrayList<Token>();
        error = false;
    }

    /**
     * run() lee un archivo fuente e instancia al Parser. Luego
     * ejecuta el analizador sintáctico mediante el método parse()
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
        Parser parser = new Parser(sourceCode);
        try {
            parser.parse();
        }
        catch (LexerException | ParserException | IOException |
               SemanticException e) {
            error = true;
            System.err.println(e.getMessage());
        }
        if (!error){
            // System.out.println("CORRECTO: ANALISIS SEMANTICO");
            SymbolTable symbolTable = parser.getSymbolTable();
            symbolTable.setFileName(FileReaderWriter.getFileName(path));
            // String sT_JSON = symbolTable.toJson(0);
            String filename = FileReaderWriter.getFileNameWithoutExt(path);
            // FileReaderWriter.write(sT_JSON, filename.concat(".json") );

            AST ast = parser.getAST();
            // ast.setFileName(FileReaderWriter.getFileName(path));
            // String ast_JSON = ast.toJson(0);
            // FileReaderWriter.write(ast_JSON, filename.concat(".ast.json") );

            // Generacion de codigo
            CodeGenerator codeGenerator = new CodeGenerator(symbolTable);
            ast.codeGen(codeGenerator);
            String asm = codeGenerator.getCode();
            FileReaderWriter.write(asm, filename.concat(".asm"));


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
    }
}
