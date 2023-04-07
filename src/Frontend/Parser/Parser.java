package Frontend.Parser;

import Frontend.Lexer.Lexer;
import Frontend.Lexer.LexerException;
import Frontend.Lexer.Token;

import java.beans.Expression;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class Parser {
    private Token currentToken;
    private final Lexer lexer;
    private final Grammar grammar;

    public Parser(BufferedReader sourceCode) {
        lexer = new Lexer(sourceCode);
        grammar = new Grammar();

    }
    private void match(String tag) throws IOException, LexerException {
        if (Objects.equals(currentToken.getTag(), tag)){
            currentToken = lexer.getNextToken();
        }
        else {
            System.out.println("ERROR!");
        }
    }
    private String match(String[] tags) throws IOException, LexerException {
        for (String tag : tags) {
            if (Objects.equals(currentToken.getTag(), tag)) {
                currentToken = lexer.getNextToken();
                return tag;
            }
        }
        System.out.println("ERROR!");
        return null;
    }

    private void Start() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ClaseR).contains(
                currentToken.getTag())){
            ClaseR();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Main).contains(
                    currentToken.getTag())){
                Main();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }
    private void ClaseR() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Clase).contains(
                currentToken.getTag())){
            Clase();
            if (grammar.getFirsts(Grammar.NonTerminal.ClaseR).contains(
                    currentToken.getTag())){
                ClaseR();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }
    private void Main() throws IOException, LexerException {
        match("fn");
        match("main");
        match("(");
        match(")");
        if (grammar.getFirsts(Grammar.NonTerminal.BloqueMetodo).contains(
                currentToken.getTag())){
            BloqueMetodo();
        }
        else{
            System.out.println("ERROR!");
        }

    }
    private void Clase() throws IOException, LexerException {
        match("class");
        match("CLASSID");
        if (grammar.getFirsts(Grammar.NonTerminal.Clase_1).contains(
                currentToken.getTag())){
            Clase_1();
        }
        else{
            System.out.println("ERROR!");
        }
    }

    private void Clase_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Herencia).contains(
                currentToken.getTag())){
            Herencia();
            Clase_1Right();
        }
        else {
            Clase_1Right();
        }
    }
    private void Clase_1Right() throws IOException, LexerException {
        match("{");
        if (grammar.getFirsts(Grammar.NonTerminal.Clase_2).contains(
                currentToken.getTag())){
            Clase_2();
        }
        else {
            System.out.println("ERROR!");
        }

    }

    private void Clase_2() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.MiembroR).contains(
                currentToken.getTag())) {
            MiembroR();
            match("}");
        }
        else {
            match("}");
        }
    }

    private void MiembroR() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Miembro).contains(
                currentToken.getTag())){
            Miembro();
            if (grammar.getFirsts(Grammar.NonTerminal.MiembroR).contains(
                    currentToken.getTag())){
                MiembroR();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void Herencia() throws IOException, LexerException {
        match(":");
        match("idClase");
    }

    private void Miembro() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Atributo).contains(
                currentToken.getTag())){
            Atributo();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Constructor).contains(
                    currentToken.getTag())){
                Constructor();

            }
            else {
                if (grammar.getFirsts(Grammar.NonTerminal.Metodo).contains(
                        currentToken.getTag())){
                    Metodo();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
    }

    private void Atributo() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Visibilidad).contains(
                currentToken.getTag())){
            Visibilidad();
            AtributoRight();
        }
        else {
            AtributoRight();
        }
    }
    private void AtributoRight() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Tipo).contains(
                currentToken.getTag())){
            Tipo();
            match(":");
            if (grammar.getFirsts(Grammar.NonTerminal.ListaDeclVar)
                    .contains(currentToken.getTag())){
                ListaDeclVar();
                match(";");
            }
            else {
                //ERROR
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void Constructor() throws IOException, LexerException {
        match("create");
        if (grammar.getFirsts(Grammar.NonTerminal.ArgsFormales).contains(
                currentToken.getTag())){
            ArgsFormales();
            if (grammar.getFirsts(Grammar.NonTerminal.Bloque).contains(
                    currentToken.getTag())){
                Bloque();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }


    }

    private void Metodo() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.FormaMetodo).contains(
                currentToken.getTag())){
            FormaMetodo();
            MetodoRight();
        }
        else {
            MetodoRight();
        }

    }
    private void MetodoRight() throws IOException, LexerException {
        match("fn");
        match("idMetodoVarible");
        if (grammar.getFirsts(Grammar.NonTerminal.ArgsFormales).contains(
                currentToken.getTag())){
            ArgsFormales();
            match("->");
            if (grammar.getFirsts(Grammar.NonTerminal.TipoMetodo).contains(
                    currentToken.getTag())){
                TipoMetodo();
                if (grammar.getFirsts(Grammar.NonTerminal.BloqueMetodo)
                        .contains(currentToken.getTag())){
                    BloqueMetodo();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }
    private void ArgsFormales() throws IOException, LexerException {
        match("(");
        if (grammar.getFirsts(Grammar.NonTerminal.ArgsFormales_1).contains(
                currentToken.getTag())){
            ArgsFormales_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ArgsFormales_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ListaArgsFormales).contains(
                currentToken.getTag())){
            ListaArgsFormales();
            match(")");
        }
        else {
            match(")");
        }
    }

    private void ListaArgsFormales() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ArgFormal).contains(
                currentToken.getTag())){
            ArgFormal();
            if (grammar.getFirsts(Grammar.NonTerminal.ListaArgsFormales_1).contains(
                    currentToken.getTag())){
                ListaArgsFormales_1();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ListaArgsFormales_1() throws IOException, LexerException {
        if (grammar.getFollows(Grammar.NonTerminal.ListaArgsFormales_1).contains(
                currentToken.getTag())){
            ;
        }
        else {
            match(",");
            if (grammar.getFirsts(Grammar.NonTerminal.ListaArgsFormales).contains(
                    currentToken.getTag())){
                ListaArgsFormales();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void ArgFormal() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Tipo).contains(
                currentToken.getTag())){
            Tipo();
            match(":");
            match("idMetodoVariable");
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void FormaMetodo() throws IOException, LexerException {
        match("static");
    }

    private void Visibilidad() throws IOException, LexerException {
        match("pub");
    }

    private void TipoMetodo() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Tipo).contains(
                currentToken.getTag())){
            Tipo();
        }
        else {
            match("void");
        }
    }
    private void Tipo() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.TipoPrimitivo).contains(
                currentToken.getTag())){
            TipoPrimitivo();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.TipoReferencia).contains(
                    currentToken.getTag())){
                TipoReferencia();
            }
            else {
                if (grammar.getFirsts(Grammar.NonTerminal.TipoArray).contains(
                        currentToken.getTag())){
                    TipoArray();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
    }
    private void TipoPrimitivo() throws IOException, LexerException {
        match(new String[]{"Bool", "I32", "Str", "Char"});
    }

    private void TipoReferencia() throws IOException, LexerException {
        match("idClase");
    }

    private void TipoArray() throws IOException, LexerException {
        match("Array");
        if (grammar.getFirsts(Grammar.NonTerminal.TipoPrimitivo).contains(
                currentToken.getTag())){
            TipoPrimitivo();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ListaDeclVar() throws IOException, LexerException {
        match("idMetodoVariable");
        if (Objects.equals(currentToken.getTag(), ",")){
            match(",");
            if (grammar.getFirsts(Grammar.NonTerminal.ListaDeclVar).contains(
                    currentToken.getTag())){
                ListaDeclVar();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }
    private void BloqueMetodo() throws IOException, LexerException {
        match("{");
        if (grammar.getFirsts(Grammar.NonTerminal.BloqueMetodo_1).contains(
                currentToken.getTag())){
            BloqueMetodo_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void BloqueMetodo_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.DeclVarLocalesR).contains(
                currentToken.getTag())){
            DeclVarLocalesR();
            if (grammar.getFirsts(Grammar.NonTerminal.BloqueMetodo_2).contains(
                    currentToken.getTag())){
                BloqueMetodo_2();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.SentenciaR).contains(
                    currentToken.getTag())){
                SentenciaR();
                match("}");
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }
    private void BloqueMetodo_2() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.SentenciaR).contains(
                currentToken.getTag())){
            SentenciaR();
            match("}");
        }
        else {
            match("}");
        }
    }
    private void DeclVarLocalesR() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.DeclVarLocales).contains(
                currentToken.getTag())){
            DeclVarLocales();
            if (grammar.getFirsts(Grammar.NonTerminal.DeclVarLocalesR).contains(
                    currentToken.getTag())){
                DeclVarLocalesR();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void SentenciaR() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Sentencia).contains(
                currentToken.getTag())){
            Sentencia();
            if (grammar.getFirsts(Grammar.NonTerminal.SentenciaR).contains(
                    currentToken.getTag())){
                SentenciaR();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void DeclVarLocales() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Tipo).contains(
                currentToken.getTag())){
            Tipo();
            match(":");
            if (grammar.getFirsts(Grammar.NonTerminal.ListaDeclVar).contains(
                    currentToken.getTag())){
                ListaDeclVar();
                match(";");
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void Sentencia() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Sent_rel).contains(
                currentToken.getTag())){
            Sent_rel();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Sent_ab).contains(
                    currentToken.getTag())){
                Sent_ab();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void Sent_rel() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.OtraSent).contains(
                currentToken.getTag())){
            OtraSent();
        }
        else {
            match("if");
            match("(");
            if (grammar.getFirsts(Grammar.NonTerminal.Expresion).contains(
                    currentToken.getTag())){
                Expresion();
                match(")");
                if (grammar.getFirsts(Grammar.NonTerminal.Sent_rel).contains(
                        currentToken.getTag())){
                    Sent_rel();
                    match("else");
                    if (grammar.getFirsts(Grammar.NonTerminal.Sent_rel).contains(
                            currentToken.getTag())){
                        Sent_rel();
                    }
                    else {
                        System.out.println("ERROR!");
                    }
                }
                else {
                    System.out.println("ERROR!");
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void Sent_ab() throws IOException, LexerException {
        match("if");
        match("(");
        if (grammar.getFirsts(Grammar.NonTerminal.Expresion).contains(
                currentToken.getTag())) {
            Expresion();
            match(")");
            if (grammar.getFirsts(Grammar.NonTerminal.Sent_ab_1).contains(
                    currentToken.getTag())) {
                Sent_ab_1();
            } else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }
    private void Sent_ab_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Sentencia).contains(
                currentToken.getTag())) {
            Sentencia();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Sent_rel).contains(
                    currentToken.getTag())) {
                Sent_rel();
                match("else");
                if (grammar.getFirsts(Grammar.NonTerminal.Sent_ab).contains(
                        currentToken.getTag())) {
                    Sent_ab();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    public void OtraSent() throws IOException, LexerException {
        if (currentToken.getTag()==";"){
            match(";");
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Asignacion).contains(
                    currentToken.getTag())) {
                Asignacion();
                match(";");
            }
            else {
                if (grammar.getFirsts(Grammar.NonTerminal.SentSimple).contains(
                        currentToken.getTag())) {
                    SentSimple();
                    match(";");
                }
                else {
                    if (currentToken.getTag()=="while"){
                        match("while");
                        match("(");
                        if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                                .contains(currentToken.getTag())) {
                            Expresion();
                            match(")");
                            if (grammar.getFirsts(
                                    Grammar.NonTerminal.Sentencia).contains(
                                    currentToken.getTag())) {
                                Sentencia();
                            }
                            else {
                                System.out.println("ERROR!");
                            }
                        }
                        else {
                            System.out.println("ERROR!");
                        }
                    }
                    else {
                        if (grammar.getFirsts(Grammar.NonTerminal.Bloque)
                                .contains(currentToken.getTag())) {
                            Bloque();
                        }
                        else {
                            match("return");
                            if (grammar.getFirsts(
                                    Grammar.NonTerminal.OtraSent_1)
                                    .contains(currentToken.getTag())) {
                                OtraSent_1();
                            }
                        }
                    }
                }
            }
        }
    }

    private void OtraSent_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                .contains(currentToken.getTag())) {
            Expresion();
            match(";");
        }
        match(";");
    }

    private void Bloque() throws IOException, LexerException {
        match("{");
        if (grammar.getFirsts(Grammar.NonTerminal.Bloque_1)
                .contains(currentToken.getTag())) {
            Bloque_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void Bloque_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.SentenciaR)
                .contains(currentToken.getTag())) {
            SentenciaR();
            match("}");
        }
        match("}");
    }

    private void Asignacion() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.AsignVarSimple)
                .contains(currentToken.getTag())) {
            AsignVarSimple();
            match("=");
            if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                    .contains(currentToken.getTag())) {
                Expresion();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.AsignSelfSimple)
                    .contains(currentToken.getTag())) {
                AsignSelfSimple();
            }
            match("=");
            if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                    .contains(currentToken.getTag())) {
                Expresion();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void AsignVarSimple() throws IOException, LexerException {
        match("id");
        if (grammar.getFirsts(Grammar.NonTerminal.AsignVarSimple_1)
                .contains(currentToken.getTag())) {
            AsignVarSimple_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }
    private void AsignVarSimple_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.EncadenadoSimpleR)
                .contains(currentToken.getTag())) {
            EncadenadoSimpleR();
        }
        else {
            if (grammar.getFollows(Grammar.NonTerminal.AsignVarSimple_1)
                    .contains(currentToken.getTag())) {
                ;
            }
            else {
                match("[");
                if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                        .contains(currentToken.getTag())) {
                    Expresion();
                    match("[");
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
    }

    private void AsignSelfSimple() throws IOException, LexerException {
        match("self");
        if (grammar.getFirsts(Grammar.NonTerminal.EncadenadoSimpleR)
                .contains(currentToken.getTag())) {
            EncadenadoSimpleR();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void EncadenadoSimpleR() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.EncadenadoSimpleR)
                .contains(currentToken.getTag())) {
            EncadenadoSimple();
            if (grammar.getFirsts(Grammar.NonTerminal.EncadenadoSimpleR)
                    .contains(currentToken.getTag())) {
                EncadenadoSimpleR();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void EncadenadoSimple() throws IOException, LexerException {
        match(".");
        match("id");
    }

    private void SentSimple() throws IOException, LexerException {
        match("(");
        if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                .contains(currentToken.getTag())) {
            Expresion();
            match(")");
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void Expresion() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExpAnd)
                .contains(currentToken.getTag())) {
            ExpAnd();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpresionRD)
                    .contains(currentToken.getTag())) {
                ExpresionRD();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpresionRD() throws IOException, LexerException {
        match("||");
        if (grammar.getFirsts(Grammar.NonTerminal.ExpAnd)
                .contains(currentToken.getTag())) {
            ExpAnd();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpresionRD)
                    .contains(currentToken.getTag())) {
                ExpresionRD();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpAnd() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExpIgual)
                .contains(currentToken.getTag())) {
            ExpIgual();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpAndRD)
                    .contains(currentToken.getTag())) {
                ExpresionRD();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpAndRD() throws IOException, LexerException {
        match("&&");
        if (grammar.getFirsts(Grammar.NonTerminal.ExpIgual)
                .contains(currentToken.getTag())) {
            ExpIgual();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpAndRD)
                    .contains(currentToken.getTag())) {
                ExpAndRD();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpIgual() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExpCompuesta)
                .contains(currentToken.getTag())) {
            ExpCompuesta();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpIgualRD)
                    .contains(currentToken.getTag())) {
                ExpIgualRD();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpIgualRD() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.OpIgual)
                .contains(currentToken.getTag())) {
            OpIgual();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpCompuesta)
                    .contains(currentToken.getTag())) {
                ExpCompuesta();
            }
            else {
                if (grammar.getFirsts(Grammar.NonTerminal.ExpIgualRD)
                        .contains(currentToken.getTag())) {
                    ExpIgualRD();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpCompuesta() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExpAdd)
                .contains(currentToken.getTag())) {
            ExpAdd();
            if (grammar.getFirsts(Grammar.NonTerminal.OpCompuesto)
                    .contains(currentToken.getTag())) {
                OpCompuesto();
                if (grammar.getFirsts(Grammar.NonTerminal.ExpAdd)
                        .contains(currentToken.getTag())) {
                    ExpAdd();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpAdd() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExpMul)
                .contains(currentToken.getTag())) {
            ExpMul();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpAddRD)
                    .contains(currentToken.getTag())) {
                ExpAddRD();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpAddRD() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.OpAdd)
                .contains(currentToken.getTag())) {
            OpAdd();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpMul)
                    .contains(currentToken.getTag())) {
                ExpMul();
            }
            else {
                if (grammar.getFirsts(Grammar.NonTerminal.ExpAddRD)
                        .contains(currentToken.getTag())) {
                    ExpAddRD();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpMul() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExpUn)
                .contains(currentToken.getTag())) {
            ExpUn();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpMulRD)
                    .contains(currentToken.getTag())) {
                ExpMulRD();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpMulRD() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.OpMul)
                .contains(currentToken.getTag())) {
            OpMul();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpUn)
                    .contains(currentToken.getTag())) {
                ExpUn();
            }
            else {
                if (grammar.getFirsts(Grammar.NonTerminal.ExpMulRD)
                        .contains(currentToken.getTag())) {
                    ExpMulRD();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ExpUn() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.OpUnario)
                .contains(currentToken.getTag())) {
            OpUnario();
            if (grammar.getFirsts(Grammar.NonTerminal.ExpUn)
                    .contains(currentToken.getTag())) {
                ExpUn();
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Operando)
                    .contains(currentToken.getTag())) {
                Operando();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void OpIgual() throws IOException, LexerException {
        match(new String[]{"==","!="});
    }

    private void OpCompuesto() throws IOException, LexerException {
        match(new String[]{"<",">","<=",">="});
    }

    private void OpAdd() throws IOException, LexerException {
        match(new String[]{"+","-"});
    }
    private void OpUnario() throws IOException, LexerException {
        match(new String[]{"+","-","!"});
    }

    private void OpMul() throws IOException, LexerException {
        match(new String[]{"*","/","%"});
    }
    private void Operando() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Literal)
                .contains(currentToken.getTag())) {
            Literal();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.Primario)
                    .contains(currentToken.getTag())) {
                Primario();
                if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                        .contains(currentToken.getTag())) {
                    Encadenado();
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }
    private void Literal() throws IOException, LexerException {
        match(new String[]{"nil","true","false","intLiteral","stringLiteral",
                "charLiteral"});
    }
    private void Primario() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ExprPar)
                .contains(currentToken.getTag())) {
            ExprPar();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.AccesoSelf)
                    .contains(currentToken.getTag())) {
                AccesoSelf();
            } else {
                if (grammar.getFirsts(Grammar.NonTerminal.AccesoVar)
                        .contains(currentToken.getTag())) {
                    AccesoVar();
                } else {
                    if (grammar.getFirsts(Grammar.NonTerminal.LlamadaMet)
                            .contains(currentToken.getTag())) {
                        LlamadaMet();
                    } else {
                        if (grammar.getFirsts(Grammar.NonTerminal.LlamadaMetEst)
                                .contains(currentToken.getTag())) {
                            LlamadaMetEst();
                        } else {
                            if (grammar.getFirsts(
                                            Grammar.NonTerminal.LlamadaConst)
                                    .contains(currentToken.getTag())) {
                                LLamadaConst();
                            } else {
                                System.out.println("ERROR!");
                            }
                        }
                    }
                }
            }
        }
    }

    private void ExprPar() throws IOException, LexerException {
        match("(");
        if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                .contains(currentToken.getTag())) {
            Expresion();
            match(")");
            if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                    .contains(currentToken.getTag())) {
                Encadenado();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void AccesoSelf() throws IOException, LexerException {
        match("self");
        if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                .contains(currentToken.getTag())) {
            Encadenado();
        }
    }

    private void AccesoVar() throws IOException, LexerException {
        match("id");
        if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                .contains(currentToken.getTag())) {
            Encadenado();
        }
    }

    private void LlamadaMet() throws IOException, LexerException {
        match("id");
        if (grammar.getFirsts(Grammar.NonTerminal.ArgsActuales)
                .contains(currentToken.getTag())) {
            ArgsActuales();
            if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                    .contains(currentToken.getTag())) {
                Encadenado();
            }
        }
        else{
            System.out.println("ERROR!");
        }
    }
    private void LlamadaMetEst() throws IOException, LexerException {
        match("idClase");
        match(".");
        if (grammar.getFirsts(Grammar.NonTerminal.LlamadaMet)
                .contains(currentToken.getTag())) {
            LlamadaMet();
            if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                    .contains(currentToken.getTag())) {
                Encadenado();
            }
        }
        else{
            System.out.println("ERROR!");
        }
    }

    private void LLamadaConst() throws IOException, LexerException {
        match("new");
        if (grammar.getFirsts(Grammar.NonTerminal.LlamadaConst_1)
                .contains(currentToken.getTag())) {
            LLamadaConst_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void LLamadaConst_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.TipoPrimitivo)
                .contains(currentToken.getTag())) {
            TipoPrimitivo();
            match("[");
            if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                    .contains(currentToken.getTag())) {
                Expresion();
                match("]");
            }
            else {
                System.out.println("ERROR!");
            }
        }
        else {
            match("idClase");
            if (grammar.getFirsts(Grammar.NonTerminal.ArgsActuales)
                    .contains(currentToken.getTag())) {
                ArgsActuales();
                if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                        .contains(currentToken.getTag())) {
                    Encadenado();
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void ArgsActuales() throws IOException, LexerException {
        match("(");
        if (grammar.getFirsts(Grammar.NonTerminal.ArgsActuales_1)
                .contains(currentToken.getTag())) {
            ArgsActuales_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void ArgsActuales_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.ListaExpresiones)
                .contains(currentToken.getTag())) {
            ListaExpresiones();
            match(")");
        }
        else {
            match(")");
        }

    }

    private void ListaExpresiones() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                .contains(currentToken.getTag())) {
            Expresion();
            if (Objects.equals(currentToken.getTag(), ",")) {
                match(",");
                if (grammar.getFirsts(Grammar.NonTerminal.ListaExpresiones)
                        .contains(currentToken.getTag())) {
                    ListaExpresiones();
                }
                else {
                    System.out.println("ERROR!");
                }
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }
    private void Encadenado() throws IOException, LexerException {
        match(".");
        if (grammar.getFirsts(Grammar.NonTerminal.Encadenado_1)
                .contains(currentToken.getTag())) {
            Encadenado_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }
    private void Encadenado_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.LlamadaMetEncadenado)
                .contains(currentToken.getTag())) {
            LlamadaMetEncadenado();
        }
        else {
            if (grammar.getFirsts(Grammar.NonTerminal.AccVarEncadenado)
                    .contains(currentToken.getTag())) {
                AccVarEncadenado();
            }
            else {
                System.out.println("ERROR!");
            }
        }
    }

    private void LlamadaMetEncadenado() throws IOException, LexerException {
        match("id");
        if (grammar.getFirsts(Grammar.NonTerminal.ArgsActuales)
                .contains(currentToken.getTag())) {
            ArgsActuales();
            if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                    .contains(currentToken.getTag())) {
                Encadenado();
            }
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void AccVarEncadenado() throws IOException, LexerException {
        match("id");
        if (grammar.getFirsts(Grammar.NonTerminal.AccVarEncadenado_1)
                .contains(currentToken.getTag())) {
            AccVarEncadenado_1();
        }
        else {
            System.out.println("ERROR!");
        }
    }

    private void AccVarEncadenado_1() throws IOException, LexerException {
        if (grammar.getFirsts(Grammar.NonTerminal.Encadenado)
                .contains(currentToken.getTag())) {
            Encadenado();
        }
        else {
            if (grammar.getFollows(Grammar.NonTerminal.AccVarEncadenado_1)
                    .contains(currentToken.getTag())) {
                ;
            }
            else {
                match("[");
                if (grammar.getFirsts(Grammar.NonTerminal.Expresion)
                        .contains(currentToken.getTag())) {
                    Expresion();
                    match("]");
                }
                else {
                    System.out.println("ERROR!");
                }
            }
        }
    }


}


