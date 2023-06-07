package AST;

import Frontend.Parser.SemanticException;
import SymbolTable.SymbolTable;

/**
 * Interfaz que contiene el método encargado de chequear los tipos de las
 * sentencias, y propagar estos tipos. Utiliza la tabla de símbolos para
 * consultarla, resolución de nombres, chequeo de tipos.
 */
public interface ISentenceCheck {
    /**
     * Utilizando la tabla de símbolos, se chequea la validez semántica de la
     * sentencia, según el caso, se propaga el tipo y también se resuelven
     * nombres.
     * @param symbolTable tabla de símbolos para consultar
     * @throws SemanticException Error Semántico en declaraciones.
     */
    void sentenceCheck(SymbolTable symbolTable) throws SemanticException;
}
