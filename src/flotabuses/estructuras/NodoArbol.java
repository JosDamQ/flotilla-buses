package flotabuses.estructuras;

/**
 * Nodo interno del Arbol Binario de Busqueda.
 *
 * <p>Cada nodo almacena una clave entera que determina su posicion en el
 * arbol, un dato generico ({@code Object}) que puede ser convertido al tipo
 * concreto mediante cast, y referencias a los subarboles izquierdo y derecho.</p>
 *
 * <p>Uso tipico desde los controladores:</p>
 * <pre>
 *   NodoArbol nodo = arbol.getRaiz();
 *   Cliente c = (Cliente) nodo.dato;
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see ArbolBinarioBusqueda
 */
public class NodoArbol {

    /**
     * Clave entera que ordena al nodo dentro del arbol.
     * Valores menores van al subarbol izquierdo; mayores, al derecho.
     */
    public int clave;

    /**
     * Dato generico almacenado en el nodo.
     * En el modulo de Clientes contiene una instancia de {@code Cliente}.
     */
    public Object dato;

    /**
     * Referencia al hijo izquierdo (clave menor que la del nodo actual).
     * {@code null} si no existe subarbol izquierdo.
     */
    public NodoArbol izq;

    /**
     * Referencia al hijo derecho (clave mayor que la del nodo actual).
     * {@code null} si no existe subarbol derecho.
     */
    public NodoArbol der;

    /**
     * Construye un nodo hoja con la clave y el dato indicados.
     * Los punteros {@code izq} y {@code der} quedan en {@code null}.
     *
     * @param clave identificador numerico unico del nodo
     * @param dato  objeto a almacenar (por ejemplo, una instancia de {@code Cliente})
     */
    public NodoArbol(int clave, Object dato) {
        this.clave = clave;
        this.dato  = dato;
        this.izq   = null;
        this.der   = null;
    }
}
