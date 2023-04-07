package Frontend.Lexer;

import java.util.HashSet;

/**
 * Esta es la clase que determina el alfabeto de entrada de nuestro lenguaje.
 */
public class Alphabet {
    // Conjunto donde se guardará el alfabeto
    private HashSet<Integer> alphabet;
    // Subset de alphabet con solo letras
    private HashSet<Integer> letters;
    // Subset de alphabet con solo números
    private HashSet<Integer> numbers;

    // Constructor del alfabeto
    public Alphabet() {
        alphabet = new HashSet<Integer>();
        letters = new HashSet<Integer>();
        numbers = new HashSet<Integer>();
        // a-z. Sin incluir el símbolo ñ
        for(char c = 'a'; c <= 'z'; c++){
            alphabet.add((int)c);
            letters.add((int)c);
        }
        // A-Z. Sin incluir el símbolo Ñ
        for(char c = 'A'; c <= 'Z'; c++){
            alphabet.add((int)c);
            letters.add((int)c);
        }
        // 0-9
        for(char c = '0'; c <= '9'; c++){
            alphabet.add((int)c);
        }
        // Símbolos especiales
        alphabet.add((int)'_');
        alphabet.add((int)'|');
        alphabet.add((int)'-');
        alphabet.add((int)' ');
        alphabet.add((int)'\n');
        alphabet.add((int)'\r');
        alphabet.add((int)':');
        alphabet.add((int)'\\');
        alphabet.add((int)'\t');
        alphabet.add((int)'"');
        alphabet.add((int)'\'');
        alphabet.add((int)'<');
        alphabet.add((int)'>');
        alphabet.add((int)'=');
        alphabet.add((int)'/');
        alphabet.add((int)'{');
        alphabet.add((int)'}');
        alphabet.add((int)'[');
        alphabet.add((int)']');
        alphabet.add((int)'(');
        alphabet.add((int)')');
        alphabet.add((int)'%');
        alphabet.add((int)'+');
        alphabet.add((int)'*');
        alphabet.add((int)'^');
        alphabet.add((int)'!');
        alphabet.add((int)';');
        alphabet.add((int)',');
        alphabet.add((int)'.');
        alphabet.add((int)'&');
        alphabet.add((int)'$');
        alphabet.add((int)'#');
        alphabet.add((int)'@');
        alphabet.add((int)'?');
        alphabet.add((int)'¿');
        alphabet.add((int)'¡');
        alphabet.add(-1);       // EOF
    }

    /**
     * Determina si el caracter es una letra
     * @param c
     * @return
     */
    public boolean isLetter(int c){
        return letters.contains(c);
    }
    /**
     * Determina si el caracter es un número
     * @param c
     * @return
     */
    public boolean isNumber(int c){
        return Character.isDigit(c);
    }
    /**
     * Determina si el caracter esta contenido en el alfabeto de entrada
     * @param c
     * @return
     */
    public boolean inAlphabet(int c){
        return alphabet.contains(c);
    }

    public boolean illegalCharacter(int c){
        return (c=='\\' || c=='?' || c=='¿' || c=='$' || c=='#' || c=='_'
            || c=='¡' || c=='@');
    }


}
