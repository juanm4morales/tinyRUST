package Frontend.Lexer;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Esta clase representa al analizador léxico (Lexer), sus atributos importantes
 * y
 */
public class Lexer{
    // Atributos
    private int row,col,                        // Ubicación actual del escaneo
        rowT,colT;                              // Ubicación del último Token
    private final BufferedReader sourceCode;    // Código fuente (a escanear)
    private final Alphabet alphabet;            // Alfabeto de entrada
    private boolean canRead;                    // Puede leer proximo caracter?
    private int currentChar;                    // Valor caracter actual (leyendo)
    private char theChar;
    private Hashtable<String,String> words;     // Tabla para keyWords
    private State dfa_state;                    // Estado actual del AFD
    public boolean EOF;                         // Llegó al final del archivo?

    /**
     * Constructor de la clase Lexer.
     * @param sourceCode El código fuente.
     */
    public Lexer(BufferedReader sourceCode){
        this.row=1;
        this.col=0;
        this.sourceCode=sourceCode;
        this.alphabet=new Alphabet();
        this.canRead=true;
        this.dfa_state=State.START;
        this.EOF=false;
        // Inicialización de tabla con palabras reservadas
        words=new Hashtable<String,String>();
        words.put("class","class");
        words.put("else","else");
        words.put("false","false");
        words.put("if","if");
        words.put("return","return");
        words.put("while","while");
        words.put("true","true");
        words.put("nil","nil");
        words.put("new","new");
        words.put("fn","fn");
        words.put("static","static");
        words.put("pub","pub");
        words.put("void","void");
        words.put("self","self");
        words.put("create","create");
        words.put("I32","I32");
        words.put("Str","Str");
        words.put("Char","Char");
        words.put("Array","Array");
        words.put("main","main");
    }

    /**
     * Estados del autómata finito usado para reconocer lexemas y generar tokens
     */
    private enum State{
        REM, MULT, ADD, SUBS, INH, LPAREN, RPAREN, LBRACE, RBRACE, LBRACKET,
        RBRACKET, DOT, SEMMI, COMMA, EXCL, L, LE, G, GE, AS, EQ, NEQ, AND, AND1,
        OR, OR1, RETT, STRO, STR_BS, STR, CHARO, CHAR0, CHAR1, CHAR2, CHAR,
        ID, NUM, ERROR_ID, START, DIV, SC, MCO, MCS, CLASSID, ERROR_SYM,
        ERROR_USTR, ERROR_UMC, ERROR_UCHAR, ERROR_ICHAR1, ERROR_ICHAR2,
        ERROR_IJSTR, ERROR_IJCHAR
    }
    /**
     * Establece el comienzo de un "posible" token
     */
    private int nextChar(){
        try {
            return sourceCode.read();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setRowColToken(){
        rowT=row;
        colT=col;
    }
    /**
     * El caracter es un espacio en blanco?
     * @param c Caracter acuatl
     * @return
     */
    private boolean isWhiteSpace(int c){
        return (c==' ' || c=='\n'|| c=='\t' || c=='\r');
    }
    /**
     * Determina si un caracter puede pertenecer a algún lexema
     * @param c Caracter actual
     * @return true si puede pertenecer a un lexema, caso contrario devuelve
     * false
     */
    private boolean allowedCharInLexeme(int c){
        switch (dfa_state){
            case CHARO:
                return (c!='\n' && c!='\t' && c!='\r');
            case STRO:
                return (c!='\n' && c!='\r');
            case ID: case CLASSID:
                return(alphabet.isLetter(c) || alphabet.isNumber(c)) || c=='_';
        }
        return !isWhiteSpace(c);
    }

    /**
     * Mediante un autómata finito devolveremos un Token si terminamos el
     * escaneo en un estado ACEPTADOR.
     * @return El token solicitado o null si no encuentra
     * @throws IOException
     * @throws LexerException
     */
    public Token getNextToken() throws IOException, LexerException{
        StringBuilder lexeme=new StringBuilder();
        dfa_state=State.START;
        Token token=null;
        int intChar;
        while (!EOF) {
            if (canRead){
                currentChar=nextChar();
                theChar = (char)currentChar;
                if (currentChar!='\n'){
                    col++;
                }
                if (currentChar==-1){
                    EOF = true;
                }
                if (!alphabet.inAlphabet(currentChar)){
                    throw new LexerException("El símbolo no pertenece al" +
                        " alfabeto de entrada",rowT,colT);
                }
                if (currentChar=='\n'){
                    col=0;
                    row++;
                }
                if (allowedCharInLexeme(currentChar)){
                    lexeme.append((char)currentChar);
                }
            }
            // canRead puede bloquear la lectura de caracter
            // (cuando sea "false") SOLO para la siguiente iteracion.
            else{
                if (allowedCharInLexeme(currentChar) && dfa_state==State.START){
                    lexeme.append((char)currentChar);
                }
                canRead=true;
            }
            token=transitionPlus(lexeme);
            if (token!=null){
                return token;
            }
        }
        token=new Token("EOF", null, row, col);
        return token;
    }

    /**
     * Función de transición del autómata finito que reconocerá tokens,
     * con añadidos que ayudan en la obtención.
     * @param lexeme
     * @return Token solicitado
     * @throws LexerException
     */
    private Token transitionPlus(StringBuilder lexeme) throws LexerException{
        switch (dfa_state){
            case START:
                // Espacios en blanco
                if (isWhiteSpace(currentChar)){
                    dfa_state=State.START;
                }
                setRowColToken();
                if (alphabet.isLetter(currentChar)){
                    if (Character.isUpperCase(currentChar)){
                        dfa_state=State.CLASSID;
                    }
                    else {
                        dfa_state=State.ID;
                    }
                }
                if (alphabet.isNumber(currentChar)){
                    dfa_state=State.NUM;
                }
                if (alphabet.illegalCharacter(currentChar)){
                    dfa_state=State.ERROR_SYM;
                    canRead=false;
                }
                else {
                    switch (currentChar){
                        case '\'':
                            lexeme.setLength(0);
                            dfa_state=State.CHARO;
                            break;
                        case '"':
                            lexeme.setLength(0);
                            dfa_state=State.STRO;
                            break;
                        case '!':
                            dfa_state=State.EXCL;
                            //canRead=false;
                            break;
                        case ',':
                            dfa_state=State.COMMA;
                            canRead=false;
                            break;
                        case ';':
                            dfa_state=State.SEMMI;
                            canRead=false;
                            break;
                        case '.':
                            dfa_state=State.DOT;
                            canRead=false;
                            break;
                        case '[':
                            dfa_state=State.LBRACKET;
                            canRead=false;
                            break;
                        case ']':
                            dfa_state=State.RBRACKET;
                            canRead=false;
                            break;
                        case '(':
                            dfa_state=State.LPAREN;
                            canRead=false;
                            break;
                        case ')':
                            dfa_state=State.RPAREN;
                            canRead=false;
                            break;
                        case '{':
                            dfa_state=State.LBRACE;
                            canRead=false;
                            break;
                        case '}':
                            dfa_state=State.RBRACE;
                            canRead=false;
                            break;
                        case ':':
                            dfa_state=State.INH;
                            canRead=false;
                            break;
                        case '+':
                            dfa_state=State.ADD;
                            canRead=false;
                            break;
                        case '-':
                            dfa_state=State.SUBS;
                            break;
                        case '*':
                            dfa_state=State.MULT;
                            canRead=false;
                            break;
                        case '%':
                            dfa_state=State.REM;
                            canRead=false;
                            break;
                        case '<':
                            dfa_state=State.L;
                            break;
                        case '>':
                            dfa_state=State.G;
                            break;
                        case '=':
                            dfa_state=State.AS;
                            break;
                        case '&':
                            dfa_state=State.AND1;
                            break;
                        case '|':
                            dfa_state=State.OR1;
                            break;
                        case '/':
                            dfa_state=State.DIV;
                            break;
                    }

                }
                break;
            case ID: // ACEPTADOR
                if (alphabet.isLetter(currentChar) || currentChar=='_'
                    || alphabet.isNumber(currentChar)){
                    dfa_state=State.ID;
                }
                else{
                    canRead=false;
                    if (words.containsKey(lexeme.toString())){
                        return new Token(words.get(lexeme.toString()),
                                lexeme.toString(),rowT,colT);
                    }
                    //lexeme.deleteCharAt(lexeme.toString().length()-1);
                    return new Token("ID", lexeme.toString(),rowT,colT);
                }
                break;
            case CLASSID: // ACEPTADOR
                if (alphabet.isLetter(currentChar) || currentChar=='_'
                        || alphabet.isNumber(currentChar)){
                    dfa_state=State.CLASSID;
                }
                else{
                    canRead=false;
                    if (words.containsKey(lexeme.toString())){
                        return new Token(words.get(lexeme.toString()),
                                lexeme.toString(),rowT,colT);
                    }
                    // lexeme.deleteCharAt(lexeme.toString().length()-1);
                    return new Token("CLASSID", lexeme.toString(),rowT,colT);
                }
                break;
            case NUM: // ACEPTADOR
                if (isWhiteSpace(currentChar) || EOF){
                    if (EOF){
                        lexeme.deleteCharAt(lexeme.length()-1);
                    }
                    return new Token("NUM", lexeme.toString(),rowT,colT);
                }
                if (alphabet.isLetter(currentChar) || currentChar=='_' || currentChar=='\\'){
                    dfa_state=State.ERROR_ID;
                    canRead=false;
                }
                if (alphabet.isNumber(currentChar)){
                    dfa_state=State.NUM;
                }
                else{
                    canRead=false;
                    lexeme.deleteCharAt(lexeme.length()-1);
                    return new Token("NUM", lexeme.toString(),rowT,colT);
                }
                break;
            case CHARO:
                switch (currentChar){
                    case '\\':
                        dfa_state=State.CHAR1;
                        break;
                    case -1:
                        dfa_state=State.ERROR_UCHAR;
                        canRead=false;
                        break;
                    case '\n': case '\r': case '\'': case '\t':
                        dfa_state=State.ERROR_ICHAR1;
                        canRead=false;
                        break;
                    default:
                        dfa_state=State.CHAR0;
                }
                break;
            case CHAR0:
                switch (currentChar) {
                    case '\'':
                        lexeme.deleteCharAt(lexeme.toString().length() - 1);
                        dfa_state = State.CHAR;
                        canRead = false;
                        break;
                    case '\n':
                        dfa_state = State.ERROR_IJCHAR;
                        canRead = false;
                        break;
                    case -1:
                        dfa_state = State.ERROR_UCHAR;
                        canRead = false;
                        break;
                    default:
                        dfa_state = State.ERROR_ICHAR2;
                        canRead = false;
                }
                break;
            case CHAR1:
                switch (currentChar){
                    case '\n': case '\r': case '\t':
                        dfa_state=State.ERROR_ICHAR1;
                        canRead=false;
                        break;
                    case -1:
                        dfa_state=State.ERROR_UCHAR;
                        canRead=false;
                        break;
                    default:
                        dfa_state=State.CHAR2;
                }
                break;
            case CHAR2:
                switch (currentChar) {
                    case '\'':
                        lexeme.deleteCharAt(lexeme.length() - 1);
                        dfa_state = State.CHAR;
                        canRead = false;
                        break;
                    case -1:
                        dfa_state = State.ERROR_UCHAR;
                        canRead = false;
                        break;
                    default:
                        dfa_state = State.ERROR_ICHAR1;
                        canRead = false;
                }
                break;
            case CHAR: // ACEPTADOR
                return new Token("CHAR",lexeme.toString(),rowT,colT);
            case STRO:
                switch (currentChar){
                    case -1:
                        dfa_state=State.ERROR_USTR;
                        canRead=false;
                        break;
                    case '"':
                        lexeme.deleteCharAt(lexeme.length()-1);
                        dfa_state=State.STR;
                        canRead=false;
                        break;
                    case '\\':
                        dfa_state=State.STR_BS;
                        break;
                    case '\n': case '\r':
                        dfa_state=State.ERROR_IJSTR;
                        canRead=false;
                        break;
                    default:
                        dfa_state=State.STRO;
                }
                break;
            case STR_BS:
                if (currentChar !='\n' && currentChar!='\r'
                    && currentChar!=-1){
                    dfa_state=State.STRO;
                }
                else{
                    if (currentChar == '\n' || currentChar == '\r') {
                        dfa_state=State.ERROR_IJSTR;
                    }
                    else {
                        dfa_state=State.ERROR_USTR;
                    }
                    canRead=false;
                }
                break;
            case STR: // ACEPTADOR
                return new Token("STRING", lexeme.toString(),rowT,colT);
            case AS: // ACEPTADOR
                if (currentChar=='='){
                    dfa_state=State.EQ;
                    canRead=false;
                }
                else{
                    canRead=false;
                    return new Token(String.valueOf(lexeme.charAt(0)),
                        String.valueOf(lexeme.charAt(0)),rowT,colT);
                }
                break;
            case EQ: // ACEPTADOR
                return new Token(lexeme.toString(),lexeme.toString(),rowT,colT);
            case NEQ: // ACEPTADOR
                return new Token(lexeme.toString(),lexeme.toString(),rowT,colT);
            case AND1:
                if (currentChar=='&'){
                    dfa_state=State.AND;
                    canRead=false;
                }
                else{
                    throw new LexerException("Operador inválido. Se esperaba"+
                        " && (AND).",rowT,colT);
                }
                break;
            case OR1:
                if (currentChar=='|'){
                    dfa_state=State.OR;
                    canRead=false;
                }
                else{
                    throw new LexerException("Operador inválido. Se esperaba "+
                            "|| (OR).",rowT,colT);
                }
                break;
            case AND: // ACEPTADOR
                return new Token(lexeme.toString(), lexeme.toString(),rowT,colT);
            case OR: // ACEPTADOR
                return new Token(lexeme.toString(), lexeme.toString(),rowT,colT);
            case L: // ACEPTADOR
                if (currentChar=='='){
                    dfa_state=State.LE;
                    canRead=false;
                }
                else{
                    canRead=false;
                    return new Token(String.valueOf(lexeme.charAt(0)),
                        String.valueOf(lexeme.charAt(0)),rowT,colT);
                }
                break;
            case LE: // ACEPTADOR
                return new Token(lexeme.toString(), lexeme.toString(),rowT,colT);
            case G: // ACEPTADOR
                if (currentChar=='='){
                    dfa_state=State.GE;
                    canRead=false;
                }
                else{
                    canRead=false;
                    return new Token(String.valueOf(lexeme.charAt(0)),
                        String.valueOf(lexeme.charAt(0)),rowT,colT);
                }
                break;
            case GE: // ACEPTADOR
                return new Token(lexeme.toString(),lexeme.toString(),rowT,colT);
            case SUBS: // ACEPTADOR
                if (currentChar=='>'){
                    dfa_state=State.RETT;
                    canRead=false;
                }
                else{
                    canRead=false;
                    return new Token(String.valueOf(lexeme.charAt(0)),
                        String.valueOf(lexeme.charAt(0)),rowT,colT);
                }
                break;
            case RETT: // ACEPTADOR
                return new Token(lexeme.toString(),lexeme.toString(),rowT,colT);
            case DIV: // ACEPTADOR
                if (currentChar=='/'){
                    dfa_state=State.SC;
                }
                else{
                    if (currentChar=='*'){
                        dfa_state=State.MCO;
                    }
                    else{
                        canRead=false;
                        return new Token(String.valueOf(lexeme.charAt(0)),
                                String.valueOf(lexeme.charAt(0)),rowT,colT);
                    }
                }
                break;
            case SC:
                if (currentChar !='\n' && currentChar!='\r'){
                    dfa_state=State.SC;
                }
                else{
                    lexeme.setLength(0);
                    dfa_state=State.START;
                }
                break;
            case MCO:
                switch (currentChar){
                    case '*':
                        dfa_state=State.MCS;
                        break;
                    case -1:
                        dfa_state=State.ERROR_UMC;
                        canRead=false;
                        break;
                    default:
                        dfa_state=State.MCO;
                }
                break;
            case MCS:
                switch (currentChar) {
                    case '/':
                        lexeme.setLength(0);
                        dfa_state = State.START;
                        break;
                    case -1:
                        dfa_state = State.ERROR_UMC;
                        canRead = false;
                        break;
                    default:
                        dfa_state = State.MCO;
                }
                break;

            case EXCL:
                if (currentChar=='='){
                    dfa_state=State.NEQ;
                    canRead=false;
                }
                else{
                    canRead=false;
                    return new Token(String.valueOf(lexeme.charAt(0)),
                            String.valueOf(lexeme.charAt(0)),rowT,colT);
                }
                break;

            // ACEPTADORES
            case COMMA: case SEMMI: case DOT: case LBRACE:
            case RBRACE: case LBRACKET: case RBRACKET: case LPAREN:
            case RPAREN: case INH: case ADD: case MULT: case REM:
                return new Token(lexeme.toString(),lexeme.toString(),rowT,colT);
            case ERROR_ID: // ACEPTADOR
                throw new LexerException("Identificador no válido " +
                    lexeme.toString(),rowT,colT);
            case ERROR_IJCHAR:
                throw new LexerException("Salto de línea ilegal dentro de"+
                    " char.",rowT,colT);
            case ERROR_ICHAR1:
                throw new LexerException("Literal caracter invalido.",
                    row,col);
            case ERROR_ICHAR2:
                throw new LexerException("Literal caracter invalido." +
                    " Hay más de un caracter", rowT, colT);
            case ERROR_IJSTR:
                throw new LexerException("Salto de línea ilegal dentro de"+
                    " string.",rowT,colT);
            case ERROR_SYM:
                throw new LexerException("Caracter ilegal.",rowT,colT);
            case ERROR_UMC:
                throw new LexerException("Comentario multilínea sin cerrar.",
                    rowT,colT);
            case ERROR_UCHAR:
                throw new LexerException("Literal caracter sin cerrar.",
                        rowT,colT);
            case ERROR_USTR:
                throw new LexerException("Literal cadena sin cerrar.",
                        rowT,colT);
            default:
                System.out.println("...");
                return null;
        }
        return null;
    }
}