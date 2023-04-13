package Frontend.Parser;

import Frontend.Lexer.Lexer;
import Frontend.Lexer.LexerException;
import Frontend.Lexer.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Esta clase representa al analizador sintáctico descendente predictivo recursivo
 * junto a sus atributos y métodos necesarios para llevar a cabo su tarea
 */
public class Parser {
    private Token currentToken;             // token actual
    private final Lexer lexer;              // Objeto Lexer (analizador léxico)
    private final Grammar grammar;          // Objeto Grammar (No terminales, primeros y siguientes
    private boolean error;                  // ha ocurrido un error

    public Parser(BufferedReader sourceCode) {
        lexer = new Lexer(sourceCode);
        grammar = new Grammar();
    }

    /**
     * Método inicial para analizar sintácticamente el código fuente
     * @throws IOException
     * @throws LexerException
     * @throws ParserException
     */
    public void parse() throws IOException, LexerException, ParserException {
        currentToken = lexer.getNextToken();
        Start();
    }

    /**
     * Verifica si el token actual "matchea" con el terminal
     * @param tag
     * @throws IOException
     * @throws LexerException
     * @throws ParserException
     */
    private void match(String tag) throws IOException, LexerException,
            ParserException {
        if (Objects.equals(currentToken.getTag(), tag)){
            currentToken = lexer.getNextToken();
        }
        else {
            throw new ParserException("Se esperaba \"" + tag +"\"",
                    currentToken.row, currentToken.col);
        }
    }

    /**
     * Verifica si el token actual "matchea" con alguno de los posibles terminales
     * @param tags
     * @return
     * @throws IOException
     * @throws LexerException
     * @throws ParserException
     */
    private String match(String[] tags) throws IOException, LexerException,
            ParserException {
        for (String tag : tags) {
            if (Objects.equals(currentToken.getTag(), tag)) {
                currentToken = lexer.getNextToken();
                return tag;
            }
        }
        throw new ParserException("Se esperaba alguno de los siguientes" +
                " tokens: " + Arrays.toString(tags), currentToken.row,
                currentToken.col);
    }

    /**
     * Determina si el token actual se encuentra en el conjunto de primeros del
     * no terminal pasado por argumento
     * @param nonTerminal
     * @return
     */
    private boolean isInFirstSet(Grammar.NonTerminal nonTerminal) {
        return grammar.getFirsts(nonTerminal).contains(currentToken.getTag())
                || grammar.getFirsts(nonTerminal).contains("");
    }

    /*
    A continuación se encuentran todos los métodos que simulan el comportamiento
    de las reglas de producción de determinado no terminal. El nombre de dicho
    no terminal coincide con el nombre del método.
    Menos en los métodos que tienen como sufijo la palbra "Right", estos métodos
    son para simplificar el código,
     */
    private void Start() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ClaseR)){
            ClaseR();
            if (isInFirstSet(Grammar.NonTerminal.Main)){
                Main();
                if (!Objects.equals(currentToken.getTag(), "EOF")){
                    throw new ParserException("No puede" +
                            " haber nada después del método Main",
                            currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba" +
                        " un método Main", currentToken.row, currentToken.col);
            }
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Main)){
                Main();
                if (!Objects.equals(currentToken.getTag(), "EOF")){
                    throw new ParserException("No puede" +
                            " haber nada después del método Main",
                            currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba" +
                        " un método Main", currentToken.row, currentToken.col);
            }
        }
    }
    private void ClaseR() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Clase)){
            Clase();
            if (isInFirstSet(Grammar.NonTerminal.ClaseR)){
                ClaseR();
            }
        }
        else {
            throw new ParserException("Se esperaba" +
                    " una clase", currentToken.row, currentToken.col);
        }
    }
    private void Main() throws IOException, LexerException, ParserException {
        match("fn");
        match("main");
        match("(");
        match(")");
        if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo)){
            BloqueMetodo();
        }
        else{
            throw new ParserException("Se esperaba" +
                    " un bloque de método", currentToken.row, currentToken.col);
        }

    }
    private void Clase() throws IOException, LexerException, ParserException {
        match("class");
        match("CLASSID");
        if (isInFirstSet(Grammar.NonTerminal.Clase_1)){
            Clase_1();
        }
        else{
            throw new ParserException("Se esperaba o \":\"" +
                    " o \"{\"", currentToken.row, currentToken.col);
        }
    }

    private void Clase_1() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Herencia)){
            Herencia();
            Clase_1Right();
        }
        else {
            Clase_1Right();
        }
    }
    private void Clase_1Right() throws IOException, LexerException,
            ParserException {
        match("{");
        if (isInFirstSet(Grammar.NonTerminal.Clase_2)){
            Clase_2();
        }
        else {
            throw new ParserException("Se esperaba un Atributo, Constructor," +
                    " Método o \"}\"", currentToken.row, currentToken.col);
        }
    }
    private void Clase_2() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.MiembroR)) {
            MiembroR();
            match("}");
        }
        else {
            match("}");
        }
    }

    private void MiembroR() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Miembro)){
            Miembro();
            if (isInFirstSet(Grammar.NonTerminal.MiembroR)){
                MiembroR();
            }
        }
        else {
            throw new ParserException("Se esperaba un Atributo, Constructor" +
                    " o Método", currentToken.row, currentToken.col);
        }
    }

    private void Herencia() throws IOException, LexerException,
            ParserException {
        match(":");
        match("CLASSID");
    }

    private void Miembro() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Atributo)){
            Atributo();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Constructor)){
                Constructor();

            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.Metodo)){
                    Metodo();
                }
                else {
                    throw new ParserException("Se esperaba un Atributo," +
                            " Constructor o Método", currentToken.row,
                            currentToken.col);
                }
            }
        }
    }

    private void Atributo() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Visibilidad)){
            Visibilidad();
            AtributoRight();
        }
        else {
            AtributoRight();
        }
    }
    private void AtributoRight() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Tipo();
            match(":");
            if (isInFirstSet(Grammar.NonTerminal.ListaDeclVar)){
                ListaDeclVar();
                match(";");
            }
            else {
                throw new ParserException("Se esperaba(n) " +
                        "identicador(es) de variable", currentToken.row,
                        currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba un Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private void Constructor() throws IOException, LexerException,
            ParserException {
        match("create");
        if (isInFirstSet(Grammar.NonTerminal.ArgsFormales)){
            ArgsFormales();
            if (isInFirstSet(Grammar.NonTerminal.Bloque)){
                Bloque();
            }
            else {
                throw new ParserException("Se esperaba \"{\"", currentToken.row,
                        currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba " +
                    "\"(\"", currentToken.row, currentToken.col);
        }
    }

    private void Metodo() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.FormaMetodo)){
            FormaMetodo();
            MetodoRight();
        }
        else {
            MetodoRight();
        }

    }
    private void MetodoRight() throws IOException, LexerException,
            ParserException {
        match("fn");
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.ArgsFormales)){
            ArgsFormales();
            match("->");
            if (isInFirstSet(Grammar.NonTerminal.TipoMetodo)){
                TipoMetodo();
                if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo)){
                    BloqueMetodo();
                }
                else {
                    throw new ParserException("Se esperaba un bloque de" +
                            " método", currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba el tipo de retorno del" +
                        " método", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba \"(\"", currentToken.row,
                    currentToken.col);
        }
    }
    private void ArgsFormales() throws IOException, LexerException,
            ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.ArgsFormales_1)){
            ArgsFormales_1();
        }
        else {
            throw new ParserException("Se esperaba un Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private void ArgsFormales_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ListaArgsFormales)){
            ListaArgsFormales();
            match(")");
        }
        else {
            match(")");
        }
    }

    private void ListaArgsFormales() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ArgFormal)){
            ArgFormal();
            if (isInFirstSet(Grammar.NonTerminal.ListaArgsFormales_1)){
                ListaArgsFormales_1();
            }
            else {
                throw new ParserException("Se esperaba \",\"",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba un Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private void ListaArgsFormales_1() throws IOException, LexerException,
            ParserException {
        if (grammar.getFollows(Grammar.NonTerminal.ListaArgsFormales_1).contains(
                currentToken.getTag())){
            ;
        }
        else {
            match(",");
            if (isInFirstSet(Grammar.NonTerminal.ListaArgsFormales)){
                ListaArgsFormales();
            }
            else {
                throw new ParserException("Se esperaba un Tipo",
                        currentToken.row, currentToken.col);
            }
        }
    }

    private void ArgFormal() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Tipo();
            match(":");
            match("ID");
        }
        else {
            throw new ParserException("Se esperaba un Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private void FormaMetodo() throws IOException, LexerException,
            ParserException {
        match("static");
    }

    private void Visibilidad() throws IOException, LexerException,
            ParserException {
        match("pub");
    }

    private void TipoMetodo() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Tipo();
        }
        else {
            match("void");
        }
    }
    private void Tipo() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.TipoPrimitivo)){
            TipoPrimitivo();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.TipoReferencia)){
                TipoReferencia();
            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.TipoArray)){
                    TipoArray();
                }
                else {
                    throw new ParserException("Se esperaba " +
                            "un Tipo", currentToken.row, currentToken.col);
                }
            }
        }
    }
    private void TipoPrimitivo() throws IOException, LexerException,
            ParserException {
        match(new String[]{"Bool", "I32", "Str", "Char"});
    }

    private void TipoReferencia() throws IOException, LexerException,
            ParserException {
        match("CLASSID");
    }

    private void TipoArray() throws IOException, LexerException,
            ParserException {
        match("Array");
        if (isInFirstSet(Grammar.NonTerminal.TipoPrimitivo)){
            TipoPrimitivo();
        }
        else {
            throw new ParserException("Se esperaba un Tipo" +
                    "Primitivo", currentToken.row, currentToken.col);
        }
    }

    private void ListaDeclVar() throws IOException, LexerException,
    ParserException {
        match("ID");
        if (Objects.equals(currentToken.getTag(), ",")){
            match(",");
            if (isInFirstSet(Grammar.NonTerminal.ListaDeclVar)){
                ListaDeclVar();
            }
            else {
                throw new ParserException("Se esperaba(n) identicador(es) de " +
                        "variable", currentToken.row, currentToken.col);
            }
        }
    }
    private void BloqueMetodo() throws IOException, LexerException,
            ParserException {
        match("{");
        if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo_1)){
            BloqueMetodo_1();
        }
        else {
            throw new ParserException("Se esperaba Tipo, bucle While," +
                    " estructura condicional, asignación, sentencia de" +
                    " retorno, bloque, sentencia simple o \";\""
                    , currentToken.row, currentToken.col);
        }
    }

    private void BloqueMetodo_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.DeclVarLocalesR)){
            DeclVarLocalesR();
            if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo_2)){
                BloqueMetodo_2();
            }
            else {
                throw new ParserException("Se esperaba bucle While, " +
                        "estructura condicional, asignación, sentencia de" +
                        " retorno, bloque, sentencia simple, \";\" o \"}\"",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.SentenciaR)){
                SentenciaR();
                match("}");
            }
            else {
                if (Objects.equals(currentToken.getTag(), "}")){
                    match("}");
                }
                else {
                    throw new ParserException("Se esperaba bucle While,"+
                            " estructura condicional, asignación, sentencia de" +
                            " retorno, bloque, sentencia simple, \";\" o \"}\"",
                            currentToken.row, currentToken.col);
                }
            }
        }
    }
    private void BloqueMetodo_2() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.SentenciaR)){
            SentenciaR();
            if (Objects.equals(currentToken.getTag(), "}")){
                match("}");
            }
            else {
                throw new ParserException("Se esperaba bucle While, "+
                        "estructura condicional, asignación, sentencia de" +
                        " retorno, bloque, sentencia simple, \";\" o \"}\"",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            match("}");
        }
    }
    private void DeclVarLocalesR() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.DeclVarLocales)){
            DeclVarLocales();
            if (isInFirstSet(Grammar.NonTerminal.DeclVarLocalesR)){
                DeclVarLocalesR();
            }
        }
        else {
            throw new ParserException("Se esperaba un Tipo" +
                    "Primitivo", currentToken.row, currentToken.col);
        }
    }

    private void SentenciaR() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Sentencia)){
            Sentencia();
            if (isInFirstSet(Grammar.NonTerminal.SentenciaR)){
                SentenciaR();
            }
        }
        else {
            throw new ParserException("Se esperaba bucle While, estructura" +
                    " condicional, asignación, sentencia de retorno, bloque," +
                    " sentencia simple, o \";\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void DeclVarLocales() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Tipo();
            match(":");
            if (isInFirstSet(Grammar.NonTerminal.ListaDeclVar)){
                ListaDeclVar();
                match(";");
            }
            else {
                throw new ParserException("Se esperaba(n) identicador(es) de" +
                        " variable", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba un Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private void Sentencia() throws IOException, LexerException,
            ParserException {
        if (Objects.equals(currentToken.getTag(), ";")){
            match(";");
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Asignacion)) {
                Asignacion();
                match(";");
            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.SentSimple)) {
                    SentSimple();
                    match(";");
                }
                else {
                    if (Objects.equals(currentToken.getTag(), "while")){
                        match("while");
                        match("(");
                        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                            Expresion();
                            match(")");
                            if (isInFirstSet(Grammar.NonTerminal.Sentencia)) {
                                Sentencia();
                            }
                            else {
                                throw new ParserException("Se esperaba bucle" +
                                        " While, estructura condicional," +
                                        " asignación, sentencia de retorno," +
                                        " bloque, sentencia simple, o \";\"",
                                        currentToken.row, currentToken.col);
                            }
                        }
                        else {
                            throw new ParserException("Se esperaba Expresión",
                                    currentToken.row, currentToken.col);
                        }
                    }
                    else {
                        if (isInFirstSet(Grammar.NonTerminal.Bloque)) {
                            Bloque();
                        }
                        else {
                            if (isInFirstSet(Grammar.NonTerminal.If)) {
                                If();
                            }
                            else {
                                if (Objects.equals(currentToken.getTag(),
                                        "return")){
                                    match("return");
                                    if (isInFirstSet(Grammar.NonTerminal.
                                            Return)) {
                                        Return();
                                    }
                                    else {
                                        throw new ParserException(
                                                "Se esperaba Expresión o \";\"",
                                                currentToken.row,
                                                currentToken.col);

                                    }
                                }
                                else {
                                    throw new ParserException("Se esperaba" +
                                            " bucle While, " +
                                            "estructura condicional," +
                                            " asignación, sentencia de " +
                                            "retorno," +
                                            " bloque, sentencia simple," +
                                            " o \";\"",
                                            currentToken.row, currentToken.col);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void If() throws IOException, LexerException, ParserException {
        match("if");
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            Expresion();
            match(")");
            if (isInFirstSet(Grammar.NonTerminal.Sentencia)) {
                Sentencia();
                if (isInFirstSet(Grammar.NonTerminal.Else)) {
                    Else();
                }
            }
            else {
                throw new ParserException("Se esperaba bucle While" +
                        ",estructura condicional, asignación, sentencia de" +
                        " retorno, bloque, sentencia simple, o \";\"",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba Expresión", currentToken.row,
                    currentToken.col);
        }
    }
    private void Else() throws IOException, LexerException, ParserException {
        match("else");
        if (isInFirstSet(Grammar.NonTerminal.Sentencia)) {
            Sentencia();
        }
        else {
            throw new ParserException("Se esperaba bucle While" +
                    ",estructura condicional, asignación, sentencia de" +
                    " retorno, bloque, sentencia simple, o \";\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void Return() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            Expresion();
            match(";");
        }
        else {
            match(";");
        }

    }

    private void Bloque() throws IOException, LexerException, ParserException {
        match("{");
        if (isInFirstSet(Grammar.NonTerminal.Bloque_1)) {
            Bloque_1();
        }
        else {
            throw new ParserException("Se esperaba bucle While, estructura" +
                    " condicional, asignación, sentencia de retorno, bloque," +
                    " sentencia simple, \";\" o \"}\""
                    , currentToken.row, currentToken.col);
        }
    }

    private void Bloque_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.SentenciaR)) {
            SentenciaR();
            match("}");
        }
        else {
            match("}");
        }

    }

    private void Asignacion() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.AsignVarSimple)) {
            AsignVarSimple();
            match("=");
            if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                Expresion();
            }
            else {
                throw new ParserException("Se esperaba Expresión",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.AsignSelfSimple)) {
                AsignSelfSimple();
            }
            match("=");
            if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                Expresion();
            }
            else {
                throw new ParserException("Se esperaba Expresión",
                        currentToken.row, currentToken.col);
            }
        }
    }

    private void AsignVarSimple() throws IOException, LexerException,
            ParserException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.AsignVarSimple_1)) {
            AsignVarSimple_1();
        }
        else {
            throw new ParserException("Se esperaba \".\", \"[\" o \"=\"",
                    currentToken.row, currentToken.col);
        }
    }
    private void AsignVarSimple_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimpleR)) {
            EncadenadoSimpleR();
        }
        else {
            if (grammar.getFollows(Grammar.NonTerminal.AsignVarSimple_1)
                    .contains(currentToken.getTag())) {
                ;
            }
            else {
                match("[");
                if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                    Expresion();
                    match("[");
                }
                else {
                    throw new ParserException("Se esperaba Expresión",
                            currentToken.row, currentToken.col);
                }
            }
        }
    }

    private void AsignSelfSimple() throws IOException, LexerException,
            ParserException {
        match("self");
        if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimpleR)) {
            EncadenadoSimpleR();
        }
        else {
            throw new ParserException("Se esperaba \".\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void EncadenadoSimpleR() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimple)) {
            EncadenadoSimple();
            if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimpleR)) {
                EncadenadoSimpleR();
            }
        }
        else {
            throw new ParserException("Se esperaba \".\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void EncadenadoSimple() throws IOException, LexerException,
            ParserException {
        match(".");
        match("ID");
    }

    private void SentSimple() throws IOException, LexerException,
            ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            Expresion();
            match(")");
        }
        else {
            throw new ParserException("Se esperaba Expresión",
                    currentToken.row, currentToken.col);
        }
    }

    private void Expresion() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpAnd)) {
            ExpAnd();
            if (isInFirstSet(Grammar.NonTerminal.ExpresionRD)) {
                ExpresionRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpresionRD() throws IOException, LexerException,
            ParserException {
        match("||");
        if (isInFirstSet(Grammar.NonTerminal.ExpAnd)) {
            ExpAnd();
            if (isInFirstSet(Grammar.NonTerminal.ExpresionRD)) {
                ExpresionRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpAnd() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpIgual)) {
            ExpIgual();
            if (isInFirstSet(Grammar.NonTerminal.ExpAndRD)) {
                ExpAndRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operador de igualdad" +
                    " \"==\" o \"!=\"", currentToken.row, currentToken.col);
        }
    }

    private void ExpAndRD() throws IOException, LexerException,
            ParserException {
        match("&&");
        if (isInFirstSet(Grammar.NonTerminal.ExpIgual)) {
            ExpIgual();
            if (isInFirstSet(Grammar.NonTerminal.ExpAndRD)) {
                ExpAndRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpIgual() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpCompuesta)) {
            ExpCompuesta();
            if (isInFirstSet(Grammar.NonTerminal.ExpIgualRD)) {
                ExpIgualRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpIgualRD() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpIgual)) {
            OpIgual();
            if (isInFirstSet(Grammar.NonTerminal.ExpCompuesta)) {
                ExpCompuesta();
                if (isInFirstSet(Grammar.NonTerminal.ExpIgualRD)) {
                    ExpIgualRD();
                }
            }
            else {
                throw new ParserException("Se esperaba Operando o Operador" +
                        " Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba Operador de igualdad" +
                    " \"==\" o \"!=\"", currentToken.row, currentToken.col);
        }
    }

    private void ExpCompuesta() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpAdd)) {
            ExpAdd();
            if (isInFirstSet(Grammar.NonTerminal.OpCompuesto)) {
                OpCompuesto();
                if (isInFirstSet(Grammar.NonTerminal.ExpAdd)) {
                    ExpAdd();
                }
                else {
                    throw new ParserException("Se esperaba Operando o" +
                            " Operador Unario", currentToken.row,
                            currentToken.col);
                }
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpAdd() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpMul)) {
            ExpMul();
            if (isInFirstSet(Grammar.NonTerminal.ExpAddRD)) {
                ExpAddRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpAddRD() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpAdd)) {
            OpAdd();
            if (isInFirstSet(Grammar.NonTerminal.ExpMul)) {
                ExpMul();
                if (isInFirstSet(Grammar.NonTerminal.ExpAddRD)) {
                    ExpAddRD();
                }
            }
            else {
                throw new ParserException("Se esperaba Operando o Operador " +
                        "Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba Operador de suma o resta",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpMul() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpUn)) {
            ExpUn();
            if (isInFirstSet(Grammar.NonTerminal.ExpMulRD)) {
                ExpMulRD();
            }
        }
        else {
            throw new ParserException("Se esperaba Operando o Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private void ExpMulRD() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpMul)) {
            OpMul();
            if (isInFirstSet(Grammar.NonTerminal.ExpUn)) {
                ExpUn();
                if (isInFirstSet(Grammar.NonTerminal.ExpMulRD)) {
                    ExpMulRD();
                }
            }
            else {
                throw new ParserException("Se esperaba Operando o" +
                        " Operador Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba Operador de" +
                    " multiplicación, división o resto", currentToken.row,
                    currentToken.col);
        }
    }

    private void ExpUn() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpUnario)) {
            OpUnario();
            if (isInFirstSet(Grammar.NonTerminal.ExpUn)) {
                ExpUn();
            }
            else {
                throw new ParserException("Se esperaba Operando o" +
                        " Operador Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Operando)) {
                Operando();
            }
            else {
                throw new ParserException("Se esperaba Operando o Operador " +
                        "Unario", currentToken.row, currentToken.col);
            }
        }
    }

    private void OpIgual() throws IOException, LexerException, ParserException {
        match(new String[]{"==","!="});
    }

    private void OpCompuesto() throws IOException, LexerException,
            ParserException {
        match(new String[]{"<",">","<=",">="});
    }

    private void OpAdd() throws IOException, LexerException, ParserException {
        match(new String[]{"+","-"});
    }
    private void OpUnario() throws IOException, LexerException,
            ParserException {
        match(new String[]{"+","-","!"});
    }

    private void OpMul() throws IOException, LexerException, ParserException {
        match(new String[]{"*","/","%"});
    }
    private void Operando() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Literal)) {
            Literal();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Primario)) {
                Primario();
                if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                    Encadenado();
                }
            }
            else {
                throw new ParserException("Se esperaba Literal o alguno de" +
                        " los siguientes tokens: \"(\", \"self\", \"id\"," +
                        " \"idClase\", \"new\"", currentToken.row,
                        currentToken.col);
            }
        }
    }
    private void Literal() throws IOException, LexerException, ParserException {
        match(new String[]{"nil","true","false","NUM","STRING",
                "CHAR"});
    }
    private void Primario() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExprPar)) {
            ExprPar();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.AccesoSelf)) {
                AccesoSelf();
            } else {
                if (isInFirstSet(Grammar.NonTerminal.VarOMet)) {
                    VarOMet();
                } else {
                    if (isInFirstSet(Grammar.NonTerminal.LlamadaMetEst)) {
                        LlamadaMetEst();
                    } else {
                        if (isInFirstSet(Grammar.NonTerminal.LlamadaConst)) {
                            LlamadaConst();
                        } else {
                            throw new ParserException("Se esperaba alguno de" +
                                    " los siguientes tokens: \"(\", \"self\"," +
                                    " \"id\", \"idClase\", \"new\"",
                                    currentToken.row, currentToken.col);
                        }
                    }
                }
            }
        }
    }

    private void ExprPar() throws IOException, LexerException, ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            Expresion();
            match(")");
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                Encadenado();
            }
        }
        else {
            throw new ParserException("Se esperaba Expresión",
                    currentToken.row, currentToken.col);
        }
    }

    private void AccesoSelf() throws IOException, LexerException,
            ParserException {
        match("self");
        if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
            Encadenado();
        }
    }

    private void VarOMet() throws IOException, LexerException, ParserException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.VarOMet_1)) {
            VarOMet_1();
        }
        else {
            throw new ParserException("Se esperaba \".\", \"(\" o \"[\"",
                    currentToken.row, currentToken.col);

        }
    }

    private void VarOMet_1() throws IOException, LexerException,
            ParserException {
        if (grammar.getFollows(Grammar.NonTerminal.VarOMet_1)
                .contains(currentToken.getTag())) {
            ;
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                Encadenado();
            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
                    ArgsActuales();
                    if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                        Encadenado();
                    }
                }
                else {
                    if (Objects.equals(currentToken.getTag(), "[")){
                        match("[");
                        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                            Expresion();
                        }
                        match("]");
                    }
                    else {
                        throw new ParserException("Se esperaba \".\", \"(\"" +
                                "o \"[\"", currentToken.row, currentToken.col);
                    }
                }
            }
        }
    }

    /* Sin usar. Reemplazado por método VarOMet
    private void AccesoVar() throws IOException, LexerException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
            Encadenado();
        }
    }
    */
    private void LlamadaMet() throws IOException, LexerException,
            ParserException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
            ArgsActuales();
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                Encadenado();
            }
        }
        else{
            throw new ParserException("Se esperaba \"(\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void LlamadaMetEst() throws IOException, LexerException,
            ParserException {
        match("CLASSID");
        match(".");
        if (isInFirstSet(Grammar.NonTerminal.LlamadaMet)) {
            LlamadaMet();
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                Encadenado();
            }
        }
        else{
            throw new ParserException("Se esperaba Identificador de Método" +
                    " Estático", currentToken.row, currentToken.col);
        }
    }

    private void LlamadaConst() throws IOException, LexerException,
            ParserException {
        match("new");
        if (isInFirstSet(Grammar.NonTerminal.LlamadaConst_1)) {
            LlamadaConst_1();
        }
        else {
            throw new ParserException("Se esperaba Identificador de Clase o" +
                    " Tipo Primitivo", currentToken.row, currentToken.col);
        }
    }

    private void LlamadaConst_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.TipoPrimitivo)) {
            TipoPrimitivo();
            match("[");
            if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                Expresion();
                match("]");
            }
            else {
                throw new ParserException("Se esperaba Expresión",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            if (Objects.equals(currentToken.getTag(), "CLASSID")) {
                match("CLASSID");
                if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
                    ArgsActuales();
                    if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                        Encadenado();
                    }
                }
            }
            else {
                throw new ParserException("Se esperaba Tipo Primitivo o" +
                        " Identificador de Clase", currentToken.row,
                        currentToken.col);
            }
        }
    }

    private void ArgsActuales() throws IOException, LexerException,
            ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.ArgsActuales_1)) {
            ArgsActuales_1();
        }
        else {
            throw new ParserException("Se esperaba Lista de Expresiones o" +
                    " \"(\"", currentToken.row, currentToken.col);
        }
    }

    private void ArgsActuales_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ListaExpresiones)) {
            ListaExpresiones();
            match(")");
        }
        else {
            match(")");
        }

    }
    private void ListaExpresiones() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            Expresion();
            if (Objects.equals(currentToken.getTag(), ",")) {
                match(",");
                if (isInFirstSet(Grammar.NonTerminal.ListaExpresiones)) {
                    ListaExpresiones();
                }
                else {
                    throw new ParserException("Se esperaba Expresión",
                            currentToken.row, currentToken.col);
                }
            }
        }
        else {
            throw new ParserException("Se esperaba Expresión", currentToken.row,
                    currentToken.col);
        }
    }
    private void Encadenado() throws IOException, LexerException,
            ParserException {
        match(".");
        if (isInFirstSet(Grammar.NonTerminal.Encadenado_1)) {
            Encadenado_1();
        }
        else {
            throw new ParserException("Se esperaba Identificador de Variable" +
                    " o de Método ", currentToken.row, currentToken.col);
        }
    }
    private void Encadenado_1() throws IOException, LexerException,
            ParserException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.VarOMet_1)) {
            VarOMet_1();
        }
        else {
            throw new ParserException("Se esperaba \".\", \"(\" o \"[\"",
                    currentToken.row, currentToken.col);
        }
    }

    /* Deprecated
    private void LlamadaMetEncadenado() throws IOException, LexerException, ParserException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
            ArgsActuales();
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                Encadenado();
            }
        }
        else {
            throw new ParserException("Se esperaba \"(\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void AccVarEncadenado() throws IOException, LexerException, ParserException {
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.AccVarEncadenado_1)) {
            AccVarEncadenado_1();
        }
        else {
            throw new ParserException("Se esperaba \".\" o \"[\"",
                    currentToken.row, currentToken.col);
        }
    }

    private void AccVarEncadenado_1() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
            Encadenado();
        }
        else {
            if (grammar.getFollows(Grammar.NonTerminal.AccVarEncadenado_1)
                    .contains(currentToken.getTag())) {
                ;
            }
            else {
                match("[");
                if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                    Expresion();
                    match("]");
                }
                else {
                    throw new ParserException("Se esperaba Expresión o \".\"",
                            currentToken.row, currentToken.col);
                }
            }
        }
    }
    */
}