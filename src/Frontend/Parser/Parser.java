package Frontend.Parser;

import AST.*;
import Frontend.Lexer.Lexer;
import Frontend.Lexer.LexerException;
import Frontend.Lexer.Token;
import SymbolTable.*;
import DataType.ArrayType;
import DataType.PrimitiveType;
import DataType.ReferenceType;
import DataType.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

/**
 * Esta clase representa al analizador sintáctico descendente predictivo recursivo
 * junto a sus atributos y métodos necesarios para llevar a cabo su tarea.
 * También realiza chequeos semánticos de las declaraciones de variables, clases
 * y métodos. Construye la tabla de símbolos del programa.
 */
public class Parser {
    private Token currentToken;             // token actual
    private final Lexer lexer;              // Objeto Lexer (analizador léxico)
    private final Grammar grammar;          // Objeto Grammar (No terminales, primeros y siguientes
    private boolean error;                  // ha ocurrido un error
    // Tabla de símbolos
    private static final SymbolTable symbolTable = new SymbolTable();
    private static final AST ast = new AST();

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
    public void parse() throws IOException, LexerException, ParserException,
            SemanticException {
        currentToken = lexer.getNextToken();
        Start();
        symbolTable.consolidateST();
        ast.sentenceCheck(symbolTable);
    }

    /**
     * Verifica si el token actual "matchea" con el terminal
     *
     * @param tag
     * @return
     * @throws IOException
     * @throws LexerException
     * @throws ParserException
     */
    private Token match(String tag) throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        if (Objects.equals(currentToken.getTag(), tag)){
            currentToken = lexer.getNextToken();
            return token;
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
    private Token match(String[] tags) throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        for (String tag : tags) {
            if (Objects.equals(currentToken.getTag(), tag)) {
                currentToken = lexer.getNextToken();
                return token;
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
    private void Start() throws IOException, LexerException, ParserException,
            SemanticException {

        if (isInFirstSet(Grammar.NonTerminal.ClaseR)){
            ClaseR();
            if (isInFirstSet(Grammar.NonTerminal.Main)){
                MethodNode main = Main();
                ast.setMain(main);
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
                MethodNode main = Main();
                ast.setMain(main);
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
    private void ClaseR() throws IOException, LexerException, ParserException,
            SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Clase)){
            ClassNode classN = Clase();
            ast.addClass(classN);
            if (isInFirstSet(Grammar.NonTerminal.ClaseR)){
                ClaseR();
            }
        }
        else {
            throw new ParserException("Se esperaba declaración de una" +
                    " una clase", currentToken.row, currentToken.col);
        }
    }
    private MethodNode Main() throws IOException, LexerException, ParserException,
            SemanticException {
        match("fn");
        match("main");
        match("(");
        match(")");
        symbolTable.setCurrentClass((String) null);
        symbolTable.setCurrentMethod(symbolTable.getMain());
        MethodNode main = new MethodNode();
        main.setName("main");
        if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo)){
            BlockNode mainBlock = BloqueMetodo();
            main.setBlock(mainBlock);
            return main;
        }
        else{
            throw new ParserException("Se esperaba" +
                    " un bloque de método", currentToken.row, currentToken.col);
        }

    }
    private ClassNode Clase() throws IOException, LexerException, ParserException,
            SemanticException {
        match("class");
        String classId = currentToken.getLexeme();
        Token token = currentToken;
        match("CLASSID");
        ClassEntry c = new ClassEntry(classId, true);
        c.setRowColDecl(token.row,token.col);
        if (symbolTable.addClass(c)!=null) {
            throw new SemanticException("La clase "+classId+" ya ha sido" +
                    " declarada. Use otro identificador." , token.row, token.col);
        }
        symbolTable.setCurrentClass(c);
        if (isInFirstSet(Grammar.NonTerminal.Clase_1)){
            ClassNode classN = new ClassNode(token);
            MethodNode defaultConstructor = new MethodNode();
            defaultConstructor.setBlock(new BlockNode());
            defaultConstructor.setName(token.getLexeme());
            classN.setConstructor(defaultConstructor);
            classN.setName(token.getLexeme());
            Clase_1(classN);
            return classN;
        }
        else{
            throw new ParserException("Se esperaba o \":\"" +
                    " o \"{\"", currentToken.row, currentToken.col);
        }
    }
    private void Clase_1(ClassNode classN) throws IOException, LexerException, ParserException,
            SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Herencia)){
            Herencia();
            Clase_1Right(classN);
        }
        else {
            symbolTable.getCurrentClass().setInheritance("Object");
            Clase_1Right(classN);
        }
    }
    private void Clase_1Right(ClassNode classN) throws IOException, LexerException,
            ParserException, SemanticException {
        if (Objects.equals(currentToken.getTag(), "{")){
            match("{");
            if (isInFirstSet(Grammar.NonTerminal.Clase_2)){
                Clase_2(classN);
            }
            else {
                throw new ParserException("Se esperaba declaración de" +
                        " declaración de atributo, Constructor, Método o \"}\"",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba o \":\"" +
                    " o \"{\"", currentToken.row, currentToken.col);
        }
    }
    private void Clase_2(ClassNode classN) throws IOException, LexerException, ParserException,
            SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.MiembroR)) {
            MiembroR(classN);
            match("}");
        }
        else {
            match("}");
        }
    }

    private void MiembroR(ClassNode classN) throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Miembro)){
            Miembro(classN);
            if (isInFirstSet(Grammar.NonTerminal.MiembroR)){
                MiembroR(classN);
            }
        }
        else {
            throw new ParserException("Se esperaba un declaración de" +
                    " atributo, Constructor o Método", currentToken.row,
                    currentToken.col);
        }
    }

    private void Herencia() throws IOException, LexerException,
            ParserException {
        match(":");
        String classId = currentToken.getLexeme();
        Token token = currentToken;
        match("CLASSID");
        symbolTable.getCurrentClass().setInheritance(classId);
        symbolTable.getCurrentClass().setRowColIDecl(token.row, token.col);
    }

    private void Miembro(ClassNode classN) throws IOException, LexerException, ParserException,
            SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Atributo)){
            Atributo();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Constructor)){
                MethodNode constructor = Constructor();
                classN.setConstructor(constructor);

            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.Metodo)){
                    MethodNode method = Metodo();
                    classN.addMethod(method);
                }
                else {
                    throw new ParserException("Se esperaba un declaración de "+
                            "atributo, Constructor o Método", currentToken.row,
                            currentToken.col);
                }
            }
        }
    }

    private void Atributo() throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Visibilidad)){
            Visibilidad();
            AtributoRight(true);
        }
        else {
            AtributoRight(false);
        }
    }
    private void AtributoRight(boolean pub) throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Type type = Tipo();
            match(":");
            if (isInFirstSet(Grammar.NonTerminal.ListaDeclVar)){
                ListaDeclVar(true, pub, type);
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

    private MethodNode Constructor() throws IOException, LexerException,
            ParserException, SemanticException {
        match("create");
        ConstructorEntry constructorEntry = new ConstructorEntry(
                symbolTable.getCurrentClass().getId());
        symbolTable.getCurrentClass().setConstructor(constructorEntry);
        symbolTable.setCurrentMethod(constructorEntry);
        if (isInFirstSet(Grammar.NonTerminal.ArgsFormales)){
            ArgsFormales();
            if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo)){
                MethodNode constructor = new MethodNode();
                constructor.setName(symbolTable.getCurrentClass().getId());
                BlockNode constBlock = BloqueMetodo();
                constructor.setBlock(constBlock);
                return constructor;
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

    private MethodNode Metodo() throws IOException, LexerException, ParserException,
            SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.FormaMetodo)){
            FormaMetodo();
            return MetodoRight(true);
        }
        else {
            return MetodoRight(false);
        }
    }
    private MethodNode MetodoRight(boolean isStatic) throws IOException, LexerException,
            ParserException, SemanticException {
        if (Objects.equals(currentToken.getTag(), "fn")){
            match("fn");
            String methodId = currentToken.getLexeme();
            Token token = currentToken;
            match("ID");
            MethodEntry methodEntry = new MethodEntry(methodId, isStatic);
            methodEntry.setToken(token);
            if (symbolTable.getCurrentClass().addMethod(methodEntry, true)
                    != null){
                throw new SemanticException("El método "+methodId+" ya ha" +
                        " sido declarado en la clase " +
                        symbolTable.getCurrentClass().getId()+". Use otro" +
                        " identificador de método.", token.row, token.col);
            }
            symbolTable.setCurrentMethod(methodEntry);
            if (isInFirstSet(Grammar.NonTerminal.ArgsFormales)){
                ArgsFormales();
                match("->");
                if (isInFirstSet(Grammar.NonTerminal.TipoMetodo)){
                    Type returnType = TipoMetodo();
                    methodEntry.setReturnType(returnType);
                    if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo)){
                        MethodNode method = new MethodNode();
                        method.setName(token.getLexeme());
                        BlockNode methodBlock = BloqueMetodo();
                        method.setBlock(methodBlock);
                        return  method;
                    }
                    else {
                        throw new ParserException("Se esperaba un bloque de" +
                                " método", currentToken.row, currentToken.col);
                    }
                }
                else {
                    throw new ParserException("Se esperaba el tipo de retorno" +
                            " del método", currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba \"(\"", currentToken.row,
                        currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba declaración de método",
                    currentToken.row, currentToken.col);
        }
    }
    private void ArgsFormales() throws IOException, LexerException,
            ParserException, SemanticException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.ArgsFormales_1)){
            ArgsFormales_1();
        }
        else {
            throw new ParserException("Se esperaba declaración de Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private void ArgsFormales_1() throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.ListaArgsFormales)){
            ListaArgsFormales();
            match(")");
        }
        else {
            if (Objects.equals(currentToken.getTag(), ")")){
                match(")");
            }
            else {
                throw new ParserException("Se esperaba argumento formal o" +
                        " \")\"", currentToken.row, currentToken.col);
            }

        }
    }
    private void ListaArgsFormales() throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.ArgFormal)){
            ArgFormal();
            if (Objects.equals(currentToken.getTag(), ",")){
                match(",");
                if (isInFirstSet(Grammar.NonTerminal.ListaArgsFormales)){
                    ListaArgsFormales();
                }
                else {
                    throw new ParserException("Se esperaba argumento formal",
                            currentToken.row, currentToken.col);
                }
            }
        }
        else {
            throw new ParserException("Se esperaba argumento formal",
                    currentToken.row, currentToken.col);
        }
    }

    private void ArgFormal() throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Type argType = Tipo();
            match(":");
            String argId = currentToken.getLexeme();
            int row = currentToken.row;
            int col = currentToken.col;
            match("ID");
            ParameterEntry parameterEntry = new ParameterEntry(argId, argType);
            if (symbolTable.getCurrentMethod().addParameter(parameterEntry)
                !=null) {
                throw new SemanticException("El identificador de parámetro "+
                        argId+" ya ha sido establecido en el método " +
                        (symbolTable.getCurrentClass()==null ? "":
                        symbolTable.getCurrentClass().getId() + ".") +
                        symbolTable.getCurrentMethod().getId()+". Use otro " +
                        "identificador para el párametro.", row, col);
            }
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

    private Type TipoMetodo() throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            return Tipo();
        }
        else {
            if (Objects.equals(currentToken.getTag(), "void")){
                match("void");
                return new Type();
            }
            else {
                throw new ParserException("Se esperaba un Tipo" +
                        " o \"void\"", currentToken.row, currentToken.col);
            }
        }
    }
    private Type Tipo() throws IOException, LexerException, ParserException,
            SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.TipoPrimitivo)){
            return TipoPrimitivo();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.TipoReferencia)){
                return TipoReferencia();
            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.TipoArray)){
                    return TipoArray();
                }
                else {
                    throw new ParserException("Se esperaba " +
                            "un Tipo", currentToken.row, currentToken.col);
                }
            }
        }
    }
    private PrimitiveType TipoPrimitivo() throws IOException, LexerException,
            ParserException {
        PrimitiveType pType = new PrimitiveType(currentToken.getLexeme());
        match(new String[]{"Bool", "I32", "Str", "Char"});
        return pType;
    }

    private ReferenceType TipoReferencia() throws IOException, LexerException,
            ParserException, SemanticException {
        ReferenceType rType = new ReferenceType(currentToken.getLexeme());
        match("CLASSID");
        return rType;
    }

    private ArrayType TipoArray() throws IOException, LexerException,
            ParserException {
        match("Array");
        if (isInFirstSet(Grammar.NonTerminal.TipoPrimitivo)){
            PrimitiveType pType = TipoPrimitivo();
            return new ArrayType(pType.getType());
        }
        else {
            throw new ParserException("Se esperaba un Tipo Primitivo",
                    currentToken.row, currentToken.col);
        }
    }
    private void ListaDeclVar(boolean attribute, boolean pub, Type type)
            throws IOException, LexerException, ParserException, SemanticException {
        String id = currentToken.getLexeme();
        Token tokenID = currentToken;
        match("ID");
        if (attribute) {
            AttributeEntry attr = new AttributeEntry(id, type, pub);
            attr.setToken(tokenID);
            if (symbolTable.getCurrentClass().addVariable(attr, true)!=null) {
                throw new SemanticException("El atributo "+ id +" ya ha sido" +
                        " declarado en la clase " +
                        symbolTable.getCurrentClass().getId()+". Use otro" +
                        " identifador de atributo", tokenID.row,
                        tokenID.col);
            }

        }
        else {
            VarEntry var = new VarEntry(id, type);
            var.setToken(tokenID);
            if (symbolTable.getCurrentMethod().containsParameter(var.getId())) {
                throw new SemanticException("La variable local "+ id +" ya" +
                        " ha sido declarada en los argumentos formales del" +
                        " método " + (symbolTable.getCurrentClass()==null ? "":
                                symbolTable.getCurrentClass().getId() + ".") +
                        symbolTable.getCurrentMethod().getId()+". Use otro" +
                        " identifador de variable", tokenID.row, tokenID.col);
            }
            if (symbolTable.getCurrentMethod().addVariable(var)!=null) {
                throw new SemanticException("La variable local "+ id +" ya" +
                        " ha sido declarada en el método "+
                        (symbolTable.getCurrentClass()==null ? "":
                        symbolTable.getCurrentClass().getId() + ".") +
                        symbolTable.getCurrentMethod().getId()+". Use otro" +
                        " identifador de variable", tokenID.row, tokenID.col);
            }
        }
        if (Objects.equals(currentToken.getTag(), ",")){
            match(",");
            if (isInFirstSet(Grammar.NonTerminal.ListaDeclVar)){
                ListaDeclVar(attribute, pub, type);
            }
            else {
                throw new ParserException("Se esperaba(n) identicador(es) de " +
                        "variable", currentToken.row, currentToken.col);
            }
        }
    }
    private BlockNode BloqueMetodo() throws IOException, LexerException,
            ParserException, SemanticException {
        match("{");
        if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo_1)){
            return BloqueMetodo_1();
        }
        else {
            throw new ParserException("Se esperaba declaración de tipo," +
                    " bucle While, estructura condicional, asignación," +
                    " sentencia de retorno, bloque, sentencia simple o \";\"",
                    currentToken.row, currentToken.col);
        }
    }

    private BlockNode BloqueMetodo_1() throws IOException, LexerException,
            ParserException, SemanticException {
        BlockNode block = new BlockNode();
        if (isInFirstSet(Grammar.NonTerminal.DeclVarLocalesR)){
            DeclVarLocalesR();
            if (isInFirstSet(Grammar.NonTerminal.BloqueMetodo_2)){
                BloqueMetodo_2(block);
                return block;
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
                SentenciaR(block);
                match("}");
                return block;
            }
            else {
                if (Objects.equals(currentToken.getTag(), "}")){
                    match("}");
                    return block;
                }
                else {
                    throw new ParserException("Se esperaba declaración de" +
                            " tipo, bucle While, estructura condicional," +
                            " asignación, sentencia de retorno, bloque," +
                            " sentencia simple, \";\" o \"}\"",
                            currentToken.row, currentToken.col);
                }
            }
        }
    }
    private BlockNode BloqueMetodo_2(BlockNode block) throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.SentenciaR)){
            SentenciaR(block);
            if (Objects.equals(currentToken.getTag(), "}")){
                match("}");
                return block;
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
            return block;
        }
    }
    private void DeclVarLocalesR() throws IOException, LexerException,
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.DeclVarLocales)){
            DeclVarLocales();
            if (isInFirstSet(Grammar.NonTerminal.DeclVarLocalesR)){
                DeclVarLocalesR();
            }
        }
        else {
            throw new ParserException("Se esperaba declaración de variable" +
                    " local", currentToken.row, currentToken.col);
        }
    }
    private void SentenciaR(BlockNode block) throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Sentencia)){
            SentenceNode sentence = Sentencia();
            if (sentence!=null) {
                block.addSentence(sentence);
            }

            if (isInFirstSet(Grammar.NonTerminal.SentenciaR)){
                SentenciaR(block);
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
            ParserException, SemanticException {
        if (isInFirstSet(Grammar.NonTerminal.Tipo)){
            Type type = Tipo();
            match(":");
            if (isInFirstSet(Grammar.NonTerminal.ListaDeclVar)){
                ListaDeclVar(false, false, type);
                match(";");
            }
            else {
                throw new ParserException("Se esperaba(n) identicador(es) de" +
                        " variable", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba declaración de Tipo",
                    currentToken.row, currentToken.col);
        }
    }

    private SentenceNode Sentencia() throws IOException, LexerException,
            ParserException {
        if (Objects.equals(currentToken.getTag(), ";")){
            match(";");
            return null;
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Asignacion)) {
                AssignNode assigment = Asignacion();
                match(";");
                return assigment;
            }
            else {
                if (isInFirstSet(Grammar.NonTerminal.SentSimple)) {
                    ExpNode simpleSent = SentSimple();
                    match(";");
                    return simpleSent;
                }
                else {
                    if (Objects.equals(currentToken.getTag(), "while")){
                        match("while");
                        match("(");
                        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                            ExpNode condition = Expresion();
                            match(")");
                            if (isInFirstSet(Grammar.NonTerminal.Sentencia)) {
                                SentenceNode body = Sentencia();
                                WhileNode whileN = new WhileNode();
                                whileN.makeFamily(condition, body);
                                return whileN;
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
                            return Bloque();
                        }
                        else {
                            if (isInFirstSet(Grammar.NonTerminal.If)) {
                                return If();
                            }
                            else {
                                if (Objects.equals(currentToken.getTag(),
                                        "return")){
                                    match("return");
                                    if (isInFirstSet(Grammar.NonTerminal.
                                            Return)) {
                                        ReturnNode returnNode = Return();
                                        returnNode.setHasReturnStmt(true);
                                        return returnNode;
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

    public IfElseNode If() throws IOException, LexerException, ParserException {
        match("if");
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            ExpNode condition = Expresion();
            match(")");
            if (isInFirstSet(Grammar.NonTerminal.Sentencia)) {
                SentenceNode thenPart = Sentencia();
                IfElseNode ifElse = new IfElseNode();
                ifElse.makeFamily(condition, thenPart);
                if (isInFirstSet(Grammar.NonTerminal.Else)) {
                    SentenceNode elsePart = Else();
                    ifElse.setElsePart(elsePart);
                    if (thenPart.hasReturnStmt() && elsePart.hasReturnStmt()) {
                        ifElse.setHasReturnStmt(true);
                    }
                }
                return ifElse;
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
    private SentenceNode Else() throws IOException, LexerException, ParserException {
        match("else");
        if (isInFirstSet(Grammar.NonTerminal.Sentencia)) {
            return Sentencia();
        }
        else {
            throw new ParserException("Se esperaba bucle While" +
                    ",estructura condicional, asignación, sentencia de" +
                    " retorno, bloque, sentencia simple, o \";\"",
                    currentToken.row, currentToken.col);
        }
    }

    private ReturnNode Return() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            ExpNode exp = Expresion();
            match(";");
            return new ReturnNode(exp);
        }
        else {
            if (Objects.equals(currentToken.getTag(), ";")){
                match(";");
                return new ReturnNode();
            }
            else {
                throw new ParserException("Se esperaba Expresión o \";\"",
                        currentToken.row, currentToken.col);
            }
        }
    }
    private BlockNode Bloque() throws IOException, LexerException, ParserException {
        match("{");
        if (isInFirstSet(Grammar.NonTerminal.Bloque_1)) {
            return Bloque_1();
        }
        else {
            throw new ParserException("Se esperaba bucle While, estructura" +
                    " condicional, asignación, sentencia de retorno, bloque," +
                    " sentencia simple, \";\" o \"}\""
                    , currentToken.row, currentToken.col);
        }
    }

    private BlockNode Bloque_1() throws IOException, LexerException,
            ParserException {
        BlockNode block = new BlockNode();
        if (isInFirstSet(Grammar.NonTerminal.SentenciaR)) {
            SentenciaR(block);
            match("}");
            return block;
        }
        else {
            if (Objects.equals(currentToken.getTag(), "}")){
                match("}");
                return  block;
            }
            else {
                throw new ParserException("Se esperaba bucle While, estructura" +
                        " condicional, asignación, sentencia de retorno, bloque," +
                        " sentencia simple, \";\" o \"}\""
                        , currentToken.row, currentToken.col);
            }
        }

    }
    private AssignNode Asignacion() throws IOException, LexerException,
            ParserException {
        AssignNode assignment = new AssignNode();
        if (isInFirstSet(Grammar.NonTerminal.AsignVarSimple)) {
            VarNode leftSide = AsignVarSimple();
            match("=");
            if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                ExpNode rightSide = Expresion();
                assignment.makeFamily(leftSide, rightSide);
                return assignment;
            }
            else {
                throw new ParserException("Se esperaba Expresión",
                        currentToken.row, currentToken.col);
            }
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.AsignSelfSimple)) {
                VarNode leftSide = AsignSelfSimple();
                match("=");
                if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                    ExpNode rightSide = Expresion();
                    assignment.makeFamily(leftSide, rightSide);
                    return assignment;
                }
                else {
                    throw new ParserException("Se esperaba Expresión",
                            currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba identificador de" +
                        " variable o referencia self ",
                        currentToken.row, currentToken.col);
            }
        }
    }

    private VarNode AsignVarSimple() throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        match("ID");
        VarNode var;
        if (isInFirstSet(Grammar.NonTerminal.AsignVarSimple_1)) {
            if (currentToken.getTag() == ".") {
                var = new VarNode(token);
            }
            else {
                var = new ArrayNode(token);
                ((ArrayNode) var).setAccess(true);
            }
            return AsignVarSimple_1(var);
        }
        return new VarNode(token);
    }
    private VarNode AsignVarSimple_1(VarNode var) throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimpleR)) {
            AccessNode chainedVar = EncadenadoSimpleR();
            var.setChain(chainedVar);
            return var;
        }
        else {
            if (Objects.equals(currentToken.getTag(), "[")){
                match("[");
                if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                    ExpNode indexExp = Expresion();
                    ((ArrayNode)var).setIndexExp(indexExp);
                    match("]");
                    return var;
                }
                else {
                    throw new ParserException("Se esperaba Expresión",
                            currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba \".\"",
                        currentToken.row, currentToken.col);

            }

        }
    }
    private VarNode AsignSelfSimple() throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        match("self");
        VarNode self;
        if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimpleR)) {
            self = new VarNode(token);
            AccessNode chainedVar = EncadenadoSimpleR();
            self.setChain(chainedVar);
            return self;
        }
        else {
            throw new ParserException("Se esperaba \".\"",
                    currentToken.row, currentToken.col);
        }
    }
    private AccessNode EncadenadoSimpleR() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimple)) {
            AccessNode chainedVar = EncadenadoSimple();
            if (isInFirstSet(Grammar.NonTerminal.EncadenadoSimpleR)) {

                AccessNode chainedVar2 = EncadenadoSimpleR();
                chainedVar.setChain(chainedVar2);
            }
            return chainedVar;
        }
        else {
            throw new ParserException("Se esperaba \".\"",
                    currentToken.row, currentToken.col);
        }
    }
    private AccessNode EncadenadoSimple() throws IOException, LexerException,
            ParserException {
        match(".");
        Token token = currentToken;
        match("ID");
        return new VarNode(token);
    }
    private ExpNode SentSimple() throws IOException, LexerException,
            ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            ExpNode exp = Expresion();
            match(")");
            return exp;
        }
        else {
            throw new ParserException("Se esperaba Expresión",
                    currentToken.row, currentToken.col);
        }
    }
    private ExpNode Expresion() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpAnd)) {
            ExpNode leftSide = ExpAnd();
            if (isInFirstSet(Grammar.NonTerminal.ExpresionRD)) {
                BinExpNode orExp = ExpresionRD();
                orExp.setLeftSide(leftSide);
                return orExp;
            }
            return leftSide;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }
    private BinExpNode ExpresionRD() throws IOException, LexerException,
            ParserException {
        Token operator = match("||");
        if (isInFirstSet(Grammar.NonTerminal.ExpAnd)) {
            BinExpNode orExp = new BinExpNode();
            orExp.setOperator(operator);
            ExpNode leftSide = ExpAnd();
            if (isInFirstSet(Grammar.NonTerminal.ExpresionRD)) {
                BinExpNode rightSide = ExpresionRD();
                rightSide.setLeftSide(leftSide);
                orExp.setRightSide(rightSide);
                return orExp;
            }
            orExp.setRightSide(leftSide);
            return orExp;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }
    private ExpNode ExpAnd() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpIgual)) {
            ExpNode leftSide = ExpIgual();
            if (isInFirstSet(Grammar.NonTerminal.ExpAndRD)) {
                BinExpNode andExp = ExpAndRD();
                andExp.setLeftSide(leftSide);
                return andExp;
            }
            return leftSide;
        }
        else {
            throw new ParserException("Se esperaba Operador de igualdad" +
                    " \"==\" o \"!=\"", currentToken.row, currentToken.col);
        }
    }
    private BinExpNode ExpAndRD() throws IOException, LexerException,
            ParserException {
        Token operator = match("&&");
        if (isInFirstSet(Grammar.NonTerminal.ExpIgual)) {
            BinExpNode andExp = new BinExpNode();
            andExp.setOperator(operator);
            ExpNode leftSide = ExpIgual();
            if (isInFirstSet(Grammar.NonTerminal.ExpAndRD)) {
                BinExpNode rightSide = ExpAndRD();
                rightSide.setLeftSide(leftSide);
                andExp.setRightSide(rightSide);
                return andExp;
            }
            andExp.setRightSide(leftSide);
            return andExp;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }

    }
    private ExpNode ExpIgual() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpCompuesta)) {
            ExpNode leftSide = ExpCompuesta();
            if (isInFirstSet(Grammar.NonTerminal.ExpIgualRD)) {
                BinExpNode eqExp = ExpIgualRD();
                eqExp.setLeftSide(leftSide);
                return eqExp;
            }
            return leftSide;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }
    private BinExpNode ExpIgualRD() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpIgual)) {
            Token operator = OpIgual();
            if (isInFirstSet(Grammar.NonTerminal.ExpCompuesta)) {
                BinExpNode eqExp = new BinExpNode();
                eqExp.setOperator(operator);
                ExpNode leftSide = ExpCompuesta();
                if (isInFirstSet(Grammar.NonTerminal.ExpIgualRD)) {
                    BinExpNode rightSide = ExpIgualRD();
                    rightSide.setLeftSide(leftSide);
                    eqExp.setRightSide(rightSide);
                    return eqExp;
                }
                eqExp.setRightSide(leftSide);
                return eqExp;
            }
            else {
                throw new ParserException("Se esperaba Operando u Operador" +
                        " Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba Operador de igualdad" +
                    " \"==\" o \"!=\"", currentToken.row, currentToken.col);
        }
    }
    private ExpNode ExpCompuesta() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpAdd)) {
            ExpNode leftSide = ExpAdd();
            if (isInFirstSet(Grammar.NonTerminal.OpCompuesto)) {
                Token operator = OpCompuesto();
                if (isInFirstSet(Grammar.NonTerminal.ExpAdd)) {
                    BinExpNode compExp = new BinExpNode();
                    compExp.setOperator(operator);
                    ExpNode rightSide = ExpAdd();
                    compExp.makeFamily(leftSide, rightSide);
                    return compExp;
                }
                else {
                    throw new ParserException("Se esperaba Operando o" +
                            " Operador Unario", currentToken.row,
                            currentToken.col);
                }
            }
            return leftSide;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }
    private ExpNode ExpAdd() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpMul)) {
            ExpNode leftSide = ExpMul();
            if (isInFirstSet(Grammar.NonTerminal.ExpAddRD)) {
                //ExpNode addExp = new ExpNode();
                BinExpNode addExp = ExpAddRD();
                addExp.setLeftSide(leftSide);
                return addExp;
            }
            return leftSide;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private BinExpNode ExpAddRD() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpAdd)) {
            Token operator = OpAdd();
            if (isInFirstSet(Grammar.NonTerminal.ExpMul)) {
                BinExpNode addExp = new BinExpNode();
                addExp.setOperator(operator);
                ExpNode leftSide = ExpMul();
                if (isInFirstSet(Grammar.NonTerminal.ExpAddRD)) {
                    BinExpNode rightSide = ExpAddRD();
                    rightSide.setLeftSide(leftSide);
                    addExp.setRightSide(rightSide);
                    return addExp;
                }
                addExp.setRightSide(leftSide);
                return addExp;
            }
            else {
                throw new ParserException("Se esperaba Operando u Operador " +
                        "Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            throw new ParserException("Se esperaba Operador de suma o resta",
                    currentToken.row, currentToken.col);
        }
    }
    private ExpNode ExpMul() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExpUn)) {
            ExpNode leftSide = ExpUn();
            if (isInFirstSet(Grammar.NonTerminal.ExpMulRD)) {
                //ExpNode multExp = new BinExpNode();
                BinExpNode multExp = ExpMulRD();
                multExp.setLeftSide(leftSide);
                return multExp;
            }
            return leftSide;
        }
        else {
            throw new ParserException("Se esperaba Operando u Operador Unario",
                    currentToken.row, currentToken.col);
        }
    }

    private BinExpNode ExpMulRD() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpMul)) {
            Token operator = OpMul();
            if (isInFirstSet(Grammar.NonTerminal.ExpUn)) {
                BinExpNode multExp = new BinExpNode();
                multExp.setOperator(operator);
                ExpNode leftSide = ExpUn();
                if (isInFirstSet(Grammar.NonTerminal.ExpMulRD)) {
                    BinExpNode rightSide = ExpMulRD();
                    rightSide.setLeftSide(leftSide);
                    multExp.setRightSide(rightSide);
                    return multExp;
                }
                multExp.setRightSide(leftSide);
                return multExp;

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

    private ExpNode ExpUn() throws IOException, LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.OpUnario)) {
            UnExpNode unExp = new UnExpNode();
            Token operator = OpUnario();
            unExp.setOperator(operator);
            if (isInFirstSet(Grammar.NonTerminal.ExpUn)) {
                ExpNode exp = ExpUn();
                unExp.setRightSide(exp);
                return unExp;
            }
            else {
                throw new ParserException("Se esperaba Operando o" +
                        " Operador Unario", currentToken.row, currentToken.col);
            }
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Operando)) {
                return Operando();
            }
            else {
                throw new ParserException("Se esperaba Operando u Operador " +
                        "Unario", currentToken.row, currentToken.col);
            }
        }

    }

    private Token OpIgual() throws IOException, LexerException, ParserException {
        return match(new String[]{"==","!="});
    }

    private Token OpCompuesto() throws IOException, LexerException,
            ParserException {
        return match(new String[]{"<",">","<=",">="});
    }

    private Token OpAdd() throws IOException, LexerException, ParserException {
        return match(new String[]{"+","-"});

    }

    private Token OpUnario() throws IOException, LexerException,
            ParserException {
        return match(new String[]{"+","-","!"});
    }

    private Token OpMul() throws IOException, LexerException, ParserException {
        return match(new String[]{"*","/","%"});
    }

    private ExpNode Operando() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Literal)) {
            return Literal();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.Primario)) {
                ExpNode exp = Primario();
                if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                    AccessNode chain = Encadenado();
                    try {
                        ((AccessNode) exp).setChain(chain);
                    }
                    catch (ClassCastException e) {
                        System.err.println(e.getMessage());
                        return null;
                    }
                }
                return exp;
            }
            else {
                throw new ParserException("Se esperaba Literal o alguno de" +
                        " los siguientes tokens: \"(\", \"self\", \"id\"," +
                        " \"idClase\", \"new\"", currentToken.row,
                        currentToken.col);
            }
        }
    }
    private LiteralNode Literal() throws IOException, LexerException, ParserException {
        Token token = match(new String[]{"nil","true","false","NUM","STRING",
                "CHAR"});
        return new LiteralNode(token, Type.createType(token.getTag()));
    }
    private ExpNode Primario() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ExprPar)) {
            return ExprPar();
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.AccesoSelf)) {
                return AccesoSelf();
            } else {
                if (isInFirstSet(Grammar.NonTerminal.VarOMet)) {
                    return VarOMet();
                } else {
                    if (isInFirstSet(Grammar.NonTerminal.LlamadaMetEst)) {
                        return LlamadaMetEst();
                    } else {
                        if (isInFirstSet(Grammar.NonTerminal.LlamadaConst)) {
                            return LlamadaConst();
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
    private ExpNode ExprPar() throws IOException, LexerException, ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            ExpNode exp = Expresion();
            match(")");
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                try {
                    AccessNode chained = Encadenado();
                    // ((AccessNode) exp).setChain(chained);
                    ((AccessNode) exp).addChain(chained);
                    return exp;
                } catch (ClassCastException e) {
                    System.err.println("Parser.ExprPar(). "+e.getMessage());
                    return null;
                }
            }
            return exp;
        }
        else {
            throw new ParserException("Se esperaba Expresión",
                    currentToken.row, currentToken.col);
        }
    }
    private VarNode AccesoSelf() throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        match("self");
        VarNode self = new VarNode(token);
        if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
            AccessNode chain = Encadenado();
            self.setChain(chain);
        }
        return self;
    }
    private AccessNode VarOMet() throws IOException, LexerException, ParserException {
        Token token = currentToken;
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.VarOMet_1)) {
            return VarOMet_1(token);
        }
        return new VarNode(token);

    }

    private AccessNode VarOMet_1(Token token) throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
            // AccesoVariableEncadenado
            AccessNode chain = Encadenado();
            VarNode var = new VarNode(token);
            var.setChain(chain);
            return var;
        }
        else {
            if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
                // LlamadaMetodo
                CallNode call = new CallNode();
                call.setToken(token);
                ArgsActuales(call);
                if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                    // LlamadaMetodoEncadenado
                    AccessNode chain = Encadenado();
                    call.setChain(chain);
                }
                return call;
            }
            else {
                if (Objects.equals(currentToken.getTag(), "[")){
                    match("[");
                    if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
                        ExpNode indexExp = Expresion();
                        match("]");
                        ArrayNode arrayVar = new ArrayNode(token, indexExp);
                        arrayVar.setAccess(true);
                        return  arrayVar;
                    }
                    else {
                        throw new ParserException("Se esperaba Expresión",
                                currentToken.row, currentToken.col);
                    }

                }
                else {
                    throw new ParserException("Se esperaba \".\", \"(\"" +
                            "o \"[\"", currentToken.row, currentToken.col);
                }
            }
        }

    }
    private CallNode LlamadaMet(CallNode method) throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
            if (method == null) {
                method = new CallNode();
            }
            method.setToken(token);
            ArgsActuales(method);
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                AccessNode chain = Encadenado();
                method.setChain(chain);
            }
            return method;

        }
        else{
            throw new ParserException("Se esperaba \"(\"",
                    currentToken.row, currentToken.col);
        }
    }
    private CallNode LlamadaMetEst() throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        match("CLASSID");
        match(".");
        if (isInFirstSet(Grammar.NonTerminal.LlamadaMet)) {
            CallNode staticCall = new CallNode();
            staticCall.setStaticClassT(token);
            LlamadaMet(staticCall);
            if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                AccessNode chain = Encadenado();
                staticCall.setChain(chain);
            }
            return staticCall;
        }
        else{
            throw new ParserException("Se esperaba Identificador de Método" +
                    " Estático", currentToken.row, currentToken.col);
        }
    }
    private AccessNode LlamadaConst() throws IOException, LexerException,
            ParserException {
        match("new");
        if (isInFirstSet(Grammar.NonTerminal.LlamadaConst_1)) {
            return LlamadaConst_1();
        }
        else {
            throw new ParserException("Se esperaba Identificador de Clase o" +
                    " Tipo Primitivo", currentToken.row, currentToken.col);
        }
    }
    private AccessNode LlamadaConst_1() throws IOException, LexerException,
            ParserException {
        if (isInFirstSet(Grammar.NonTerminal.TipoPrimitivo)) {
            // Constructor de Array
            PrimitiveType type = TipoPrimitivo(); // Se termina de declarar al inicializar
            match("[");
            //if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            //    ExpNode exp = Expresion(); // Debe ser I32
            //    match("]");
            //    ArrayNode arrayConst = new ArrayNode(null);
            //    arrayConst.setType(type.getType());
            //    arrayConst.setIndexExp(exp);
            //    return arrayConst;
            //}
            if (isInFirstSet(Grammar.NonTerminal.Literal)) {
                LiteralNode literalI32 = Literal(); // Debe ser I32
                match("]");
                ArrayNode arrayConst = new ArrayNode(null);
                arrayConst.setType(type.getType());
                arrayConst.setIndexExp(literalI32);
                return arrayConst;
            }
            else {
                throw new ParserException("Se esperaba Expresión",
                        currentToken.row, currentToken.col);
            }

        }
        else {
            if (Objects.equals(currentToken.getTag(), "CLASSID")) {
                // Constructor de Objeto CLASSID
                Token token = currentToken;
                match("CLASSID");
                if (isInFirstSet(Grammar.NonTerminal.ArgsActuales)) {
                    CallNode constructor = new CallNode(true);
                    constructor.setToken(token);
                    ArgsActuales(constructor);
                    if (isInFirstSet(Grammar.NonTerminal.Encadenado)) {
                        // Constructor encadenado
                        AccessNode chain = Encadenado();
                        constructor.setChain(chain);
                    }
                    return constructor;
                }
                else {
                    throw new ParserException("Se esperaba \"(\"",
                            currentToken.row, currentToken.col);
                }
            }
            else {
                throw new ParserException("Se esperaba Tipo Primitivo o" +
                        " Identificador de Clase", currentToken.row,
                        currentToken.col);
            }
        }
    }
    private void ArgsActuales(CallNode methodCall) throws IOException,
            LexerException, ParserException {
        match("(");
        if (isInFirstSet(Grammar.NonTerminal.ArgsActuales_1)) {
            ArgsActuales_1(methodCall);
        }
        else {
            throw new ParserException("Se esperaba Lista de Expresiones o" +
                    " \")\"", currentToken.row, currentToken.col);
        }
    }

    private void ArgsActuales_1(CallNode methodCall) throws IOException,
            LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.ListaExpresiones)) {
            ListaExpresiones(methodCall);
            match(")");
        }
        else {
            if (Objects.equals(currentToken.getTag(), ")")){
                match(")");
            }
            else {
                throw new ParserException("Se esperaba Lista de Expresiones o" +
                        " \")\"", currentToken.row, currentToken.col);
            }
        }
    }
    private void ListaExpresiones(CallNode methodCall) throws IOException,
            LexerException, ParserException {
        if (isInFirstSet(Grammar.NonTerminal.Expresion)) {
            ExpNode exp = Expresion();
            methodCall.addParamExp(exp);
            if (Objects.equals(currentToken.getTag(), ",")) {
                match(",");
                if (isInFirstSet(Grammar.NonTerminal.ListaExpresiones)) {
                    ListaExpresiones(methodCall);
                }
                else {
                    throw new ParserException("Se esperaba Lista de" +
                            " Expresiones o \")\"", currentToken.row,
                            currentToken.col);
                }
            }
        }
        else {
            throw new ParserException("Se esperaba Expresión", currentToken.row,
                    currentToken.col);
        }
    }
    private AccessNode Encadenado() throws IOException, LexerException,
            ParserException {
        match(".");
        if (isInFirstSet(Grammar.NonTerminal.Encadenado_1)) {
            return Encadenado_1();
        }
        else {
            throw new ParserException("Se esperaba Identificador de Variable" +
                    " o de Método ", currentToken.row, currentToken.col);
        }
    }
    private AccessNode Encadenado_1() throws IOException, LexerException,
            ParserException {
        Token token = currentToken;
        match("ID");
        if (isInFirstSet(Grammar.NonTerminal.VarOMet_1)) {
            AccessNode chain = VarOMet_1(token);
            return chain;
        }
        return new VarNode(token);
    }

    /**
     * Getter de la tabla de símbolos
     * @return tabla de símbolos
     */
    public SymbolTable getSymbolTable(){
        return symbolTable;
    }

    /**
     * Getter del AST generado
     * @return
     */
    public AST getAST(){
        return ast;
    }


}