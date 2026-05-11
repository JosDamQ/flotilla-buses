package flotabuses.estructuras;

/**
 * Nodo de datos (celda) de la Matriz Ortogonal.
 *
 * <p>Cada instancia representa la interseccion entre una fila (destino
 * turistico) y una columna (bus asignado) en la {@link MatrizOrtogonal}.
 * Almacena la clave de su fila, la clave de su columna, el dato concreto
 * (una instancia de {@code AsignacionBusDestino}) y cuatro punteros que
 * permiten navegar en las cuatro direcciones dentro de la matriz:</p>
 *
 * <pre>
 *           arriba
 *             |
 *  izquierda--[nodo]--derecha
 *             |
 *           abajo
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see MatrizOrtogonal
 * @see NodoCabecera
 */
public class NodoMatriz {

    /**
     * Clave de la fila a la que pertenece este nodo (nombre del destino).
     */
    public String filaClave;

    /**
     * Clave de la columna a la que pertenece este nodo (placa del bus).
     */
    public String columnaClave;

    /**
     * Dato almacenado en la celda.
     * En el modulo de Asignaciones contiene una instancia de
     * {@code AsignacionBusDestino}.
     */
    public Object dato;

    /** Puntero a la celda de la derecha en la misma fila. {@code null} si es la ultima. */
    public NodoMatriz derecha;

    /** Puntero a la celda de la izquierda en la misma fila. {@code null} si es la primera. */
    public NodoMatriz izquierda;

    /** Puntero a la celda de abajo en la misma columna. {@code null} si es la ultima. */
    public NodoMatriz abajo;

    /** Puntero a la celda de arriba en la misma columna. {@code null} si es la primera. */
    public NodoMatriz arriba;

    /**
     * Construye un nodo de celda con las claves de fila y columna indicadas.
     * Todos los punteros de navegacion quedan en {@code null}.
     *
     * @param filaClave    clave de la fila (nombre del destino turistico)
     * @param columnaClave clave de la columna (placa del bus)
     * @param dato         objeto a almacenar en la celda
     */
    public NodoMatriz(String filaClave, String columnaClave, Object dato) {
        this.filaClave    = filaClave;
        this.columnaClave = columnaClave;
        this.dato         = dato;
        this.derecha      = null;
        this.izquierda    = null;
        this.abajo        = null;
        this.arriba       = null;
    }
}
