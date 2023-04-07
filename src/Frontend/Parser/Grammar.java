package Frontend.Parser;

import java.util.*;

public class Grammar {
    private final Map<NonTerminal, Set<String>> first;
    private final Map<NonTerminal, Set<String>> follow;
    public enum NonTerminal {
        Start, ClaseR, Main, Clase, Clase_1, Clase_2, MiembroR, Herencia,
        Miembro, Atributo, Constructor, Metodo, ArgsFormales, ArgsFormales_1,
        ListaArgsFormales, ListaArgsFormales_1, ArgFormal, FormaMetodo,
        Visibilidad, TipoMetodo, Tipo, TipoPrimitivo, TipoReferencia, TipoArray,
        ListaDeclVar, BloqueMetodo, BloqueMetodo_1, BloqueMetodo_2,
        DeclVarLocalesR, SentenciaR, DeclVarLocales, Sentencia, Sent_rel,
        Sent_ab, Sent_ab_1, OtraSent, OtraSent_1, Bloque, Bloque_1, Asignacion,
        AsignVarSimple, AsignVarSimple_1, AsignSelfSimple, EncadenadoSimpleR,
        EncadenadoSimple, SentSimple, Expresion, ExpresionRD, ExpAnd, ExpAndRD,
        ExpIgual, ExpIgualRD, ExpCompuesta, ExpAdd, ExpAddRD, ExpMul, ExpMulRD,
        ExpUn, OpIgual, OpCompuesto, OpAdd, OpUnario, OpMul, Operando, Literal,
        Primario, ExprPar, AccesoSelf, AccesoVar, LlamadaMet, LlamadaMetEst,
        LlamadaConst, LlamadaConst_1, ArgsActuales, ArgsActuales_1,
        ListaExpresiones, Encadenado, Encadenado_1, LlamadaMetEncadenado,
        AccVarEncadenado, AccVarEncadenado_1
    }

    public Grammar(){
        first = new HashMap<>();
        follow = new HashMap<>();
        fillFirstSet();
        fillFollowSet();
    }



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
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "idClase",
                        "Array","create", "static", "fn", "}")));
        first.put(NonTerminal.MiembroR, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "idClase",
                        "Array","create",  "static", "fn")));
        first.put(NonTerminal.Herencia, new HashSet<>(
                Arrays.asList(":")));
        first.put(NonTerminal.Miembro, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "idClase",
                        "Array", "create", "static", "fn")));
        first.put(NonTerminal.Atributo, new HashSet<>(
                Arrays.asList("pub", "Bool", "I32", "Str", "Char", "idClase",
                        "Array")));
        first.put(NonTerminal.Constructor, new HashSet<>(
                Arrays.asList("create")));
        first.put(NonTerminal.Metodo, new HashSet<>(
                Arrays.asList("static", "fn")));
        first.put(NonTerminal.ArgsFormales, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.ArgsFormales_1, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase", "Array",
                        ")")));
        first.put(NonTerminal.ListaArgsFormales, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase",
                        "Array")));
        first.put(NonTerminal.ListaArgsFormales_1, new HashSet<>(
                Arrays.asList(","))); // + λ
        first.put(NonTerminal.ArgFormal, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase",
                        "Array")));
        first.put(NonTerminal.FormaMetodo, new HashSet<>(
                Arrays.asList("static")));
        first.put(NonTerminal.Visibilidad, new HashSet<>(
                Arrays.asList("pub")));
        first.put(NonTerminal.TipoMetodo, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase", "Array",
                        "void")));
        first.put(NonTerminal.Tipo, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase",
                        "Array")));
        first.put(NonTerminal.TipoPrimitivo, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char")));
        first.put(NonTerminal.TipoReferencia, new HashSet<>(
                Arrays.asList("idClase")));
        first.put(NonTerminal.TipoArray, new HashSet<>(
                Arrays.asList("Array")));
        first.put(NonTerminal.ListaDeclVar, new HashSet<>(
                Arrays.asList("idMetodoVariable")));
        first.put(NonTerminal.BloqueMetodo, new HashSet<>(
                Arrays.asList("{")));
        first.put(NonTerminal.BloqueMetodo_1, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase", "Array",
                        "if", ";", "id", "(", "while", "{", "return")));
        first.put(NonTerminal.BloqueMetodo_2, new HashSet<>(
                Arrays.asList("if", ";", "id", "(", "while", "{", "return",
                        "}")));
        first.put(NonTerminal.DeclVarLocalesR, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase",
                        "Array")));
        first.put(NonTerminal.SentenciaR, new HashSet<>(
                Arrays.asList("if", ";", "id", "(", "while", "{", "return")));
        first.put(NonTerminal.DeclVarLocales, new HashSet<>(
                Arrays.asList("Bool", "I32", "Str", "Char", "idClase",
                        "Array")));
        first.put(NonTerminal.Sentencia, new HashSet<>(
                Arrays.asList("if", ";", "id", "(", "while", "{", "return")));
        first.put(NonTerminal.Sent_rel, new HashSet<>(
                Arrays.asList("if", ";", "id", "(", "while", "{", "return")));
        first.put(NonTerminal.Sent_ab, new HashSet<>(
                Arrays.asList("if")));
        first.put(NonTerminal.Sent_ab_1, new HashSet<>(
                Arrays.asList("if", ";", "id", "(", "while", "{", "return")));
        first.put(NonTerminal.OtraSent, new HashSet<>(
                Arrays.asList(";", "id", "(", "while", "{", "return")));
        first.put(NonTerminal.OtraSent_1, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new", ";")));
        first.put(NonTerminal.Bloque, new HashSet<>(
                Arrays.asList("{")));
        first.put(NonTerminal.Bloque_1, new HashSet<>(
                Arrays.asList("if", ";", "id", "(", "while", "{", "return",
                        "}")));
        first.put(NonTerminal.Asignacion, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.AsignVarSimple, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.AsignVarSimple_1, new HashSet<>(
                Arrays.asList(".", "[")));
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
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExpresionRD, new HashSet<>(
                Arrays.asList("||")));
        first.put(NonTerminal.ExpAnd, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExpAndRD, new HashSet<>(
                Arrays.asList("&&")));
        first.put(NonTerminal.ExpIgual, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExpIgualRD, new HashSet<>(
                Arrays.asList("==", "!=")));
        first.put(NonTerminal.ExpCompuesta, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExpAdd, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExpAddRD, new HashSet<>(
                Arrays.asList("+", "-")));
        first.put(NonTerminal.ExpMul, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExpMulRD, new HashSet<>(
                Arrays.asList("*", "/", "%")));
        first.put(NonTerminal.ExpUn, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
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
                Arrays.asList("nil", "true", "false", "intLiteral",
                        "stringLiteral", "charLiteral", "(", "self", "id",
                        "idClase", "new")));
        first.put(NonTerminal.Literal, new HashSet<>(
                Arrays.asList("nil", "true", "false", "intLiteral",
                        "stringLiteral", "charLiteral")));
        first.put(NonTerminal.Primario, new HashSet<>(
                Arrays.asList("(", "self", "id", "idClase", "new")));
        first.put(NonTerminal.ExprPar, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.AccesoSelf, new HashSet<>(
                Arrays.asList("self" )));
        first.put(NonTerminal.AccesoVar, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.LlamadaMet, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.LlamadaMetEst, new HashSet<>(
                Arrays.asList("idClase")));
        first.put(NonTerminal.LlamadaConst, new HashSet<>(
                Arrays.asList("new")));
        first.put(NonTerminal.LlamadaConst_1, new HashSet<>(
                Arrays.asList("idClase", "Bool", "I32", "Str", "Char")));
        first.put(NonTerminal.ArgsActuales, new HashSet<>(
                Arrays.asList("(")));
        first.put(NonTerminal.ArgsActuales_1, new HashSet<>(
                Arrays.asList(")", "+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.ListaExpresiones, new HashSet<>(
                Arrays.asList("+", "-", "!", "nil", "true", "false",
                        "intLiteral", "stringLiteral", "charLiteral", "(",
                        "self", "id", "idClase", "new")));
        first.put(NonTerminal.Encadenado, new HashSet<>(
                Arrays.asList(".")));
        first.put(NonTerminal.Encadenado_1, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.LlamadaMetEncadenado, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.AccVarEncadenado, new HashSet<>(
                Arrays.asList("id")));
        first.put(NonTerminal.AccVarEncadenado_1, new HashSet<>(
                Arrays.asList(".", "[")));
    }

    private void fillFollowSet() {
        follow.put(NonTerminal.ListaArgsFormales_1, new HashSet<>(
                Arrays.asList(")")));
        follow.put(NonTerminal.AsignVarSimple_1, new HashSet<>(
                Arrays.asList("=")));
        follow.put(NonTerminal.AccVarEncadenado_1, new HashSet<>(
                Arrays.asList(".", "*", "/", "%", "+", "-", "<", ">", "<=",
                        ">=", "==", "!=", "&&", "||", ")", "]", ";", ",",
                        ")")));
    }
    public Set<String> getFirsts(NonTerminal nonTerminal){
        return first.get(nonTerminal);
    }
    public Set<String> getFollows(NonTerminal nonTerminal){
        return follow.get(nonTerminal);
    }
}
