package Frontend.Parser;

import java.util.*;

/**
 * Esta clase representa los no terminales de la gramática junto a sus conjuntos
 * de primeros y siguientes
 */
public class Grammar {
    private final Map<NonTerminal, Set<String>> first;  // Conjuntos de Primeros
    private final Map<NonTerminal, Set<String>> follow; // Conjuntos de Siguientes

    /**
     * NonTerminal representa todos los no terminales de la gramática final
     */
    public enum NonTerminal {
        Start, ClaseR, Main, Clase, Clase_1, Clase_2, MiembroR, Herencia,
        Miembro, Atributo, Constructor, Metodo, ArgsFormales, ArgsFormales_1,
        ListaArgsFormales, ListaArgsFormales_1, ArgFormal, FormaMetodo,
        Visibilidad, TipoMetodo, Tipo, TipoPrimitivo, TipoReferencia, TipoArray,
        ListaDeclVar, BloqueMetodo, BloqueMetodo_1, BloqueMetodo_2,
        DeclVarLocalesR, SentenciaR, DeclVarLocales, Sentencia, If, Else,
        Return, Bloque, Bloque_1, Asignacion, AsignVarSimple, AsignVarSimple_1,
        AsignSelfSimple, EncadenadoSimpleR, EncadenadoSimple, SentSimple,
        Expresion, ExpresionRD, ExpAnd, ExpAndRD, ExpIgual, ExpIgualRD,
        ExpCompuesta, ExpAdd, ExpAddRD, ExpMul, ExpMulRD, ExpUn, OpIgual,
        OpCompuesto, OpAdd, OpUnario, OpMul, Operando, Literal, Primario,
        ExprPar, AccesoSelf/* AccesoVar*/, LlamadaMet, VarOMet, VarOMet_1,
        LlamadaMetEst, LlamadaConst, LlamadaConst_1, ArgsActuales,
        ArgsActuales_1, ListaExpresiones, Encadenado, Encadenado_1,
        LlamadaMetEncadenado, AccVarEncadenado, AccVarEncadenado_1
    }

    /**
     * Constructor de la gramática donde se llenan los Maps que representan los
     * conjuntos de primeros y los conjuntos de siguientes (útiles) de la
     * gramática
     */
    public Grammar(){
        first = new HashMap<>();
        follow = new HashMap<>();
        fillFirstSet();
        fillFollowSet();
    }

    /**
     * Llenado de los conjuntos de Primeros
     */
    private void fillFirstSet(){
        first.put(NonTerminal.Start, new HashSet<>(
                Arrays.asList("class","fn")));
        first.put(NonTerminal.ClaseR, new HashSet<>(
                Arrays.asList("class")));
        first.put(NonTerminal.Main, new HashSet<>(
                Arrays.asList("fn")));
        first.put(NonTerminal.Clase, new HashSet<>(
                Arrays.asList("class")));
        first.put(NonTerminal.Clase_1, new HashSet<>(
                Arrays.asList(":","{")));
        first.put(NonTerminal.Clase_2, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "CLASSID",
                        "Array","create", "static", "fn", "}")));
        first.put(NonTerminal.MiembroR, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "CLASSID",
                        "Array","create",  "static", "fn")));
        first.put(NonTerminal.Herencia, new HashSet<>(
                Arrays.asList(":")));
        first.put(NonTerminal.Miembro, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "CLASSID",
                        "Array", "create", "static", "fn")));
        first.put(NonTerminal.Atributo, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "CLASSID",
                        "Array")));
        first.put(NonTerminal.Constructor, new HashSet<>(
                Arrays.asList("create")));
        first.put(NonTerminal.Metodo, new HashSet<>(
                Arrays.asList("static", "fn")));
        first.put(NonTerminal.ArgsFormales, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.ArgsFormales_1, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID", "Array",
                        ")")));
        first.put(NonTerminal.ListaArgsFormales, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID",
                        "Array")));
        first.put(NonTerminal.ListaArgsFormales_1, new HashSet<>(
                Arrays.asList(",",""))); // + λ
        first.put(NonTerminal.ArgFormal, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID",
                        "Array")));
        first.put(NonTerminal.FormaMetodo, new HashSet<>(
                Arrays.asList("static")));
        first.put(NonTerminal.Visibilidad, new HashSet<>(
                Arrays.asList("pub")));
        first.put(NonTerminal.TipoMetodo, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID", "Array",
                        "void")));
        first.put(NonTerminal.Tipo, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID",
                        "Array")));
        first.put(NonTerminal.TipoPrimitivo, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char")));
        first.put(NonTerminal.TipoReferencia, new HashSet<>(
                Arrays.asList("CLASSID")));
        first.put(NonTerminal.TipoArray, new HashSet<>(
                Arrays.asList("Array")));
        first.put(NonTerminal.ListaDeclVar, new HashSet<>(
                Arrays.asList("ID")));
        first.put(NonTerminal.BloqueMetodo, new HashSet<>(
                Arrays.asList("{")));
        first.put(NonTerminal.BloqueMetodo_1, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID", "Array",
                        "if", ";", "ID", "(", "while", "{", "return")));
        first.put(NonTerminal.BloqueMetodo_2, new HashSet<>(
                Arrays.asList("if", ";", "ID", "(", "while", "{", "return",
                        "}")));
        first.put(NonTerminal.DeclVarLocalesR, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID",
                        "Array")));
        first.put(NonTerminal.SentenciaR, new HashSet<>(
                Arrays.asList("if", ";", "ID", "(", "while", "{", "return")));
        first.put(NonTerminal.DeclVarLocales, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "CLASSID",
                        "Array")));
        first.put(NonTerminal.Sentencia, new HashSet<>(
                Arrays.asList("if", ";", "ID", "self", "(", "while", "{", "return")));
        first.put(NonTerminal.If, new HashSet<>(
                Arrays.asList("if")));
        first.put(NonTerminal.Else, new HashSet<>(
                Arrays.asList("else")));
        first.put(NonTerminal.Return, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new", ";")));
        first.put(NonTerminal.Bloque, new HashSet<>(
                Arrays.asList("{")));
        first.put(NonTerminal.Bloque_1, new HashSet<>(
                Arrays.asList("if", ";", "ID", "(", "while", "{", "return",
                        "}")));
        first.put(NonTerminal.Asignacion, new HashSet<>(
                Arrays.asList("ID", "self")));
        first.put(NonTerminal.AsignVarSimple, new HashSet<>(
                Arrays.asList("ID")));
        first.put(NonTerminal.AsignVarSimple_1, new HashSet<>(
                Arrays.asList(".", "[", "")));
        first.put(NonTerminal.AsignSelfSimple, new HashSet<>(
                Arrays.asList("self")));
        first.put(NonTerminal.EncadenadoSimpleR, new HashSet<>(
                Arrays.asList(".")));
        first.put(NonTerminal.EncadenadoSimple, new HashSet<>(
                Arrays.asList(".")));
        first.put(NonTerminal.SentSimple, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.Expresion, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExpresionRD, new HashSet<>(
                Arrays.asList("||")));
        first.put(NonTerminal.ExpAnd, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExpAndRD, new HashSet<>(
                Arrays.asList("&&")));
        first.put(NonTerminal.ExpIgual, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExpIgualRD, new HashSet<>(
                Arrays.asList("==", "!=")));
        first.put(NonTerminal.ExpCompuesta, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExpAdd, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExpAddRD, new HashSet<>(
                Arrays.asList("+", "-")));
        first.put(NonTerminal.ExpMul, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExpMulRD, new HashSet<>(
                Arrays.asList("*", "/", "%")));
        first.put(NonTerminal.ExpUn, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.OpIgual, new HashSet<>(
                Arrays.asList("==", "!=")));
        first.put(NonTerminal.OpCompuesto, new HashSet<>(
                Arrays.asList("<", ">", "<=", ">=")));
        first.put(NonTerminal.OpAdd, new HashSet<>(
                Arrays.asList("+", "-")));
        first.put(NonTerminal.OpUnario, new HashSet<>(
                Arrays.asList("+", "-", "!")));
        first.put(NonTerminal.OpMul, new HashSet<>(
                Arrays.asList("*", "/", "%")));
        first.put(NonTerminal.Operando, new HashSet<>(
                Arrays.asList("nil", "true", "false", "NUM",
                        "STRING", "CHAR", "(", "self", "ID",
                        "CLASSID", "new")));
        first.put(NonTerminal.Literal, new HashSet<>(
                Arrays.asList("nil", "true", "false", "NUM",
                        "STRING", "CHAR")));
        first.put(NonTerminal.Primario, new HashSet<>(
                Arrays.asList("(", "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ExprPar, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.AccesoSelf, new HashSet<>(
                Arrays.asList("self" )));
        //
        first.put(NonTerminal.VarOMet, new HashSet<>(
                Arrays.asList("ID")));
        first.put(NonTerminal.VarOMet_1, new HashSet<>(
                Arrays.asList(".", "(", "")));
        // first.put(NonTerminal.AccesoVar, new HashSet<>(
        //        Arrays.asList("ID")));
        first.put(NonTerminal.LlamadaMet, new HashSet<>(
                Arrays.asList("ID")));
        //
        first.put(NonTerminal.LlamadaMetEst, new HashSet<>(
                Arrays.asList("CLASSID")));
        first.put(NonTerminal.LlamadaConst, new HashSet<>(
                Arrays.asList("new")));
        first.put(NonTerminal.LlamadaConst_1, new HashSet<>(
                Arrays.asList("CLASSID", "Bool", "I32", "Str", "Char")));
        first.put(NonTerminal.ArgsActuales, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.ArgsActuales_1, new HashSet<>(
                Arrays.asList(")", "+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.ListaExpresiones, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "NUM", "STRING", "CHAR", "(",
                        "self", "ID", "CLASSID", "new")));
        first.put(NonTerminal.Encadenado, new HashSet<>(
                Arrays.asList(".")));
        first.put(NonTerminal.Encadenado_1, new HashSet<>(
                Arrays.asList("ID")));
        first.put(NonTerminal.LlamadaMetEncadenado, new HashSet<>(
                Arrays.asList("ID")));
        first.put(NonTerminal.AccVarEncadenado, new HashSet<>(
                Arrays.asList("ID")));
        first.put(NonTerminal.AccVarEncadenado_1, new HashSet<>(
                Arrays.asList(".", "[", "")));
    }

    /**
     * Llenado de los conjuntos de Siguientes
     */
    private void fillFollowSet() {
        follow.put(NonTerminal.ListaArgsFormales_1, new HashSet<>(
                Arrays.asList(")")));
        follow.put(NonTerminal.AsignVarSimple_1, new HashSet<>(
                Arrays.asList("=")));
        follow.put(NonTerminal.AccVarEncadenado_1, new HashSet<>(
                Arrays.asList(".", "*", "/", "%", "+", "-", "<", ">", "<=",
                        ">=", "==", "!=", "&&", "||", ")", "]", ";", ",",
                        ")")));
        follow.put(NonTerminal.VarOMet_1, new HashSet<>(
                Arrays.asList(".", "*", "/", "%", "+", "-", "<", ">", "<=",
                        ">=", "==", "!=", "&&", "||", ")", "]", ";", ",",
                        ")")));
    }

    /**
     * getter de el conjunto de primeros del no terminal pasado por parámetro
     * @param nonTerminal
     * @return
     */
    public Set<String> getFirsts(NonTerminal nonTerminal){
        return first.get(nonTerminal);
    }
    /**
     * getter de el conjunto de siguientes del no terminal pasado por parámetro
     * @param nonTerminal
     * @return
     */
    public Set<String> getFollows(NonTerminal nonTerminal){
        return follow.get(nonTerminal);
    }
}
