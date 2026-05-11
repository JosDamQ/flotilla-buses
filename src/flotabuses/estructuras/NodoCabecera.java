package flotabuses.estructuras;

/**
 * Nodo de cabecera de fila o columna en la Matriz Ortogonal.
 *
 * <p>Cada instancia representa el encabezado de una fila (destino turistico)
 * o de una columna (bus asignado) dentro de la {@link MatrizOrtogonal}.
 * Contiene una clave de texto para identificarla y un puntero al primer
 * {@link NodoMatriz} de esa fila o columna.</p>
 *
 * <p>Las cabeceras de filas y columnas forman listas enlazadas simples
 * independientes entre si:</p>
 * <pre>
 *   cabFilas  → [Antigua] → [Peten] → [Coban] → null
 *   cabCols   → [P-001-ABC] → [P-002-XYZ] → null
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see MatrizOrtogonal
 * @see NodoMatriz
 */
public class NodoCabecera {

    /**
     * Clave que identifica a esta fila o columna.
     * Para filas: nombre del destino turistico.
     * Para columnas: placa del bus asignado.
     */
    public String clave;

    /**
     * Puntero a la siguiente cabecera en la lista de filas o columnas.
     * {@code null} si es la ultima cabecera.
     */
    public NodoCabecera siguiente;

    /**
     * Puntero al primer nodo de datos de esta fila o columna.
     * {@code null} si la fila o columna no tiene celdas.
     */
    public NodoMatriz primero;

    /**
     * Construye una cabecera con la clave indicada.
     * Los punteros {@code siguiente} y {@code primero} quedan en {@code null}.
     *
     * @param clave identificador de la fila o columna
     */
    public NodoCabecera(String clave) {
        this.clave     = clave;
        this.siguiente = null;
        this.primero   = null;
    }
}
