package Frontend.Lexer;

import java.util.Hashtable;
// NO UTILIZADA de momento
// Tabla con valores de las etiquetas para palabras reservadas,
// identificadores y tokens compuestos
@Deprecated
public class Tag {
    public final static int
    CLASS = 256,      //
    IF = 2,         //
    ELSE = 3,       //
    WHILE = 4,      //
    FN = 5,         //
    NEW = 7,        //
    TRUE = 8,       //
    FALSE = 9,      //
    STATIC = 10,    //
    PUB = 11,       //
    RETURN = 12,    //
    STRING = 13,    //
    EQ = 14,        // ==
    AND = 15,       // &&
    OR = 16,        // ||
    GE = 18,        // >=
    LE = 20,        // <=
    NUM = 22,       // [0-9]
    CHAR = 28,      //
    RETT = 40,      // ->
    ID = 41,        //
    NIL = 42,       //
    VOID = 43,      //
    SELF = 44;      //
}
