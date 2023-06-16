package DataType;

/**
 * Esta clase representa al tipo Array. Contiene información sobre el tipo de
 * Array y métodos útiles en las tareas del compilador.
 *
 * @author Juan Martín Morales
 */
public class ArrayType extends Type{

    private String arrayType;   // Tipo del Array

    public ArrayType() {
        super(ARRAY);
    }
    public ArrayType(String arrayType) {
        super(ARRAY);
        this.arrayType=arrayType;
    }

    /**
     * Obtiene el tipo del arreglo
     * @return Cadena con el tipo del arreglo
     */
    public String getArrayType() {
        if (arrayType!=null) {
            return arrayType;
        }
        return "";

    }

    /**
     * Devuelve la descripción formateada del tipo Array[TipoPrimitivo]
     * @return descripción del tipo formateada
     */
    @Override
    public String toString(){

        return getType().concat("["+getArrayType()+"]");
    }
}
