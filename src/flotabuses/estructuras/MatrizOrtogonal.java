package flotabuses.estructuras;

/**
 * Matriz Ortogonal implementada con listas enlazadas dispersas.
 *
 * <p>Representa una tabla bidimensional donde cada celda puede o no tener
 * un dato. En lugar de reservar memoria para todas las celdas posibles,
 * solo se almacenan las celdas que contienen datos, lo que la hace eficiente
 * cuando la mayoria de posibles intersecciones estan vacias.</p>
 *
 * <p>En este proyecto la matriz modela las asignaciones de buses a destinos:</p>
 * <ul>
 *   <li><b>Filas (eje vertical):</b> destinos turisticos, indexados por nombre.</li>
 *   <li><b>Columnas (eje horizontal):</b> buses de la flotilla, indexados por placa.</li>
 *   <li><b>Celda [destino][bus]:</b> contiene una instancia de
 *       {@code AsignacionBusDestino} cuando el bus ha sido asignado a ese destino.</li>
 * </ul>
 *
 * <p>Estructura interna:</p>
 * <pre>
 *   cabFilas  → [Antigua] → [Peten] → [Coban] → null
 *       |
 *   cabCols → [P-001-ABC] → [P-002-XYZ] → null
 *
 *   [Antigua][P-001-ABC] ←→ [Antigua][P-002-XYZ]
 *        ↑↓                      ↑↓
 *   [Peten][P-001-ABC]   ←→ [Peten][P-002-XYZ]
 * </pre>
 *
 * <p>Recorrido por filas tipico desde un controlador:</p>
 * <pre>
 *   NodoCabecera fila = matriz.getCabFilas();
 *   while (fila != null) {
 *       NodoMatriz celda = fila.primero;
 *       while (celda != null) {
 *           AsignacionBusDestino a = (AsignacionBusDestino) celda.dato;
 *           celda = celda.derecha;
 *       }
 *       fila = fila.siguiente;
 *   }
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see NodoCabecera
 * @see NodoMatriz
 */
public class MatrizOrtogonal {

    /**
     * Lista enlazada de cabeceras de fila (una por cada destino turistico registrado).
     * {@code null} si la matriz no tiene ninguna fila.
     */
    private NodoCabecera cabFilas;

    /**
     * Lista enlazada de cabeceras de columna (una por cada bus registrado).
     * {@code null} si la matriz no tiene ninguna columna.
     */
    private NodoCabecera cabColumnas;

    /**
     * Construye una matriz ortogonal vacia sin filas ni columnas.
     */
    public MatrizOrtogonal() {
        this.cabFilas    = null;
        this.cabColumnas = null;
    }

    // =========================================================
    // METODOS AUXILIARES DE CABECERAS
    // =========================================================

    /**
     * Busca la cabecera de fila con la clave indicada.
     * Si no existe, la crea y la agrega al final de la lista de filas.
     *
     * @param clave nombre del destino turistico (clave de fila)
     * @return el {@link NodoCabecera} de la fila, existente o recien creado
     */
    private NodoCabecera obtenerOCrearFila(String clave) {
        NodoCabecera actual = cabFilas;
        while (actual != null) {
            if (actual.clave.equalsIgnoreCase(clave)) return actual;
            actual = actual.siguiente;
        }
        NodoCabecera nueva = new NodoCabecera(clave);
        if (cabFilas == null) {
            cabFilas = nueva;
        } else {
            NodoCabecera ultimo = cabFilas;
            while (ultimo.siguiente != null) ultimo = ultimo.siguiente;
            ultimo.siguiente = nueva;
        }
        return nueva;
    }

    /**
     * Busca la cabecera de columna con la clave indicada.
     * Si no existe, la crea y la agrega al final de la lista de columnas.
     *
     * @param clave placa del bus (clave de columna)
     * @return el {@link NodoCabecera} de la columna, existente o recien creado
     */
    private NodoCabecera obtenerOCrearColumna(String clave) {
        NodoCabecera actual = cabColumnas;
        while (actual != null) {
            if (actual.clave.equalsIgnoreCase(clave)) return actual;
            actual = actual.siguiente;
        }
        NodoCabecera nueva = new NodoCabecera(clave);
        if (cabColumnas == null) {
            cabColumnas = nueva;
        } else {
            NodoCabecera ultimo = cabColumnas;
            while (ultimo.siguiente != null) ultimo = ultimo.siguiente;
            ultimo.siguiente = nueva;
        }
        return nueva;
    }

    // =========================================================
    // INSERTAR
    // =========================================================

    /**
     * Inserta o actualiza la celda en la posicion (filaKey, columnaKey).
     * Si la celda ya existe, solo actualiza su dato sin crear un nuevo nodo.
     * Si es nueva, enlaza el nodo horizontal y verticalmente con sus vecinos.
     *
     * @param filaKey    nombre del destino turistico (clave de fila)
     * @param columnaKey placa del bus (clave de columna)
     * @param dato       instancia de {@code AsignacionBusDestino} a almacenar
     */
    public void insertar(String filaKey, String columnaKey, Object dato) {
        NodoCabecera cabFila    = obtenerOCrearFila(filaKey);
        NodoCabecera cabColumna = obtenerOCrearColumna(columnaKey);

        // Si ya existe, solo actualizar el dato
        NodoMatriz existente = buscarNodo(filaKey, columnaKey);
        if (existente != null) {
            existente.dato = dato;
            return;
        }

        NodoMatriz nuevo = new NodoMatriz(filaKey, columnaKey, dato);

        // Enlazar horizontalmente dentro de la fila
        if (cabFila.primero == null) {
            cabFila.primero = nuevo;
        } else {
            NodoMatriz actualFila = cabFila.primero;
            while (actualFila.derecha != null) actualFila = actualFila.derecha;
            actualFila.derecha = nuevo;
            nuevo.izquierda    = actualFila;
        }

        // Enlazar verticalmente dentro de la columna
        if (cabColumna.primero == null) {
            cabColumna.primero = nuevo;
        } else {
            NodoMatriz actualCol = cabColumna.primero;
            while (actualCol.abajo != null) actualCol = actualCol.abajo;
            actualCol.abajo = nuevo;
            nuevo.arriba    = actualCol;
        }
    }

    // =========================================================
    // BUSCAR
    // =========================================================

    /**
     * Busca el {@link NodoMatriz} ubicado en la interseccion (filaKey, columnaKey).
     * Se utiliza internamente para operaciones de insercion y eliminacion.
     *
     * @param filaKey    nombre del destino turistico
     * @param columnaKey placa del bus
     * @return el {@link NodoMatriz} encontrado, o {@code null} si la celda no existe
     */
    private NodoMatriz buscarNodo(String filaKey, String columnaKey) {
        NodoCabecera cf = cabFilas;
        while (cf != null) {
            if (cf.clave.equalsIgnoreCase(filaKey)) {
                NodoMatriz actual = cf.primero;
                while (actual != null) {
                    if (actual.columnaClave.equalsIgnoreCase(columnaKey)) return actual;
                    actual = actual.derecha;
                }
                return null;
            }
            cf = cf.siguiente;
        }
        return null;
    }

    /**
     * Retorna el dato almacenado en la celda (filaKey, columnaKey).
     *
     * <p>Uso tipico:</p>
     * <pre>
     *   AsignacionBusDestino a =
     *       (AsignacionBusDestino) matriz.buscar("Antigua", "P-123-ABC");
     * </pre>
     *
     * @param filaKey    nombre del destino turistico (clave de fila)
     * @param columnaKey placa del bus (clave de columna)
     * @return el dato de la celda, o {@code null} si la celda no existe
     */
    public Object buscar(String filaKey, String columnaKey) {
        NodoMatriz nodo = buscarNodo(filaKey, columnaKey);
        return nodo != null ? nodo.dato : null;
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    /**
     * Elimina la celda en la posicion (filaKey, columnaKey) y repara todos
     * los enlaces horizontales y verticales afectados.
     * Si la fila o la columna queda sin celdas, la cabecera permanece
     * (no se eliminan cabeceras automaticamente).
     *
     * @param filaKey    nombre del destino turistico (clave de fila)
     * @param columnaKey placa del bus (clave de columna)
     * @return {@code true} si la celda fue encontrada y eliminada;
     *         {@code false} si la celda no existia
     */
    public boolean eliminar(String filaKey, String columnaKey) {
        NodoMatriz nodo = buscarNodo(filaKey, columnaKey);
        if (nodo == null) return false;

        // Reparar enlaces horizontales (dentro de la fila)
        if (nodo.izquierda != null) {
            nodo.izquierda.derecha = nodo.derecha;
        } else {
            NodoCabecera cf = cabFilas;
            while (cf != null) {
                if (cf.clave.equalsIgnoreCase(filaKey)) { cf.primero = nodo.derecha; break; }
                cf = cf.siguiente;
            }
        }
        if (nodo.derecha != null) nodo.derecha.izquierda = nodo.izquierda;

        // Reparar enlaces verticales (dentro de la columna)
        if (nodo.arriba != null) {
            nodo.arriba.abajo = nodo.abajo;
        } else {
            NodoCabecera cc = cabColumnas;
            while (cc != null) {
                if (cc.clave.equalsIgnoreCase(columnaKey)) { cc.primero = nodo.abajo; break; }
                cc = cc.siguiente;
            }
        }
        if (nodo.abajo != null) nodo.abajo.arriba = nodo.arriba;

        return true;
    }

    // =========================================================
    // GETTERS Y UTILIDADES
    // =========================================================

    /**
     * Retorna la primera cabecera de fila de la matriz.
     * Punto de entrada para recorridos por filas (por destino).
     *
     * @return el {@link NodoCabecera} de la primera fila,
     *         o {@code null} si la matriz esta vacia
     */
    public NodoCabecera getCabFilas() { return cabFilas; }

    /**
     * Retorna la primera cabecera de columna de la matriz.
     * Punto de entrada para recorridos por columnas (por bus).
     *
     * @return el {@link NodoCabecera} de la primera columna,
     *         o {@code null} si no hay columnas registradas
     */
    public NodoCabecera getCabColumnas() { return cabColumnas; }

    /**
     * Indica si la matriz no tiene ninguna fila ni celda registrada.
     *
     * @return {@code true} si la matriz esta vacia; {@code false} en caso contrario
     */
    public boolean estaVacia() { return cabFilas == null; }
}
