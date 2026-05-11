package flotabuses.estructuras;

/**
 * Lista Doblemente Enlazada de proposito general con insercion ordenada.
 *
 * <p>Mantiene los nodos en orden alfabetico ascendente segun su clave de texto.
 * No admite claves duplicadas. Cada nodo tiene punteros al anterior y al
 * siguiente, lo que permite recorridos eficientes en ambas direcciones.</p>
 *
 * <p>Esta estructura se reutiliza en varios modulos del sistema:</p>
 * <ul>
 *   <li><b>Buses:</b> clave = placa, dato = {@code Bus}</li>
 *   <li><b>Destinos:</b> clave = nombre del destino, dato = {@code Destino}</li>
 *   <li><b>Horas de asignacion:</b> clave = "HH:mm", dato = {@code LocalTime}</li>
 *   <li><b>Boletos:</b> clave = "HH:mm" hora seleccionada, dato = {@code Boleto}</li>
 * </ul>
 *
 * <p>Recorrido ascendente tipico desde un controlador:</p>
 * <pre>
 *   NodoLista actual = lista.getCabeza();
 *   while (actual != null) {
 *       Bus b = (Bus) actual.dato;
 *       actual = actual.siguiente;
 *   }
 * </pre>
 *
 * <p>Recorrido descendente tipico:</p>
 * <pre>
 *   NodoLista actual = lista.getCola();
 *   while (actual != null) {
 *       Bus b = (Bus) actual.dato;
 *       actual = actual.anterior;
 *   }
 * </pre>
 *
 * <p>Complejidad de insercion ordenada: O(n). Busqueda y eliminacion: O(n).</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see NodoLista
 */
public class ListaDoblementeEnlazada {

    /** Primer nodo de la lista. {@code null} si la lista esta vacia. */
    private NodoLista cabeza;

    /** Ultimo nodo de la lista. {@code null} si la lista esta vacia. */
    private NodoLista cola;

    /** Numero de elementos actualmente en la lista. */
    private int tamanio;

    /**
     * Construye una lista doblemente enlazada vacia.
     */
    public ListaDoblementeEnlazada() {
        this.cabeza  = null;
        this.cola    = null;
        this.tamanio = 0;
    }

    // =========================================================
    // INSERTAR ORDENADO
    // =========================================================

    /**
     * Inserta un nuevo nodo manteniendo el orden alfabetico ascendente por clave.
     * Si la clave ya existe en la lista, la operacion no tiene efecto.
     *
     * <p>Casos de insercion manejados:</p>
     * <ol>
     *   <li>Lista vacia: el nuevo nodo es cabeza y cola a la vez.</li>
     *   <li>Clave menor que la cabeza: se inserta al inicio.</li>
     *   <li>Clave mayor que la cola: se inserta al final.</li>
     *   <li>Caso general: se recorre la lista para encontrar la posicion correcta.</li>
     * </ol>
     *
     * @param clave identificador de texto que determina la posicion del nodo
     * @param dato  objeto a almacenar en el nuevo nodo
     */
    public void insertarOrdenado(String clave, Object dato) {
        NodoLista nuevo = new NodoLista(clave, dato);

        // Caso 1: lista vacia
        if (cabeza == null) {
            cabeza = nuevo;
            cola   = nuevo;
            tamanio++;
            return;
        }

        // Caso 2: va antes de la cabeza actual
        if (clave.compareToIgnoreCase(cabeza.clave) < 0) {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza          = nuevo;
            tamanio++;
            return;
        }

        // Caso 3: va despues de la cola actual
        if (clave.compareToIgnoreCase(cola.clave) > 0) {
            nuevo.anterior = cola;
            cola.siguiente = nuevo;
            cola           = nuevo;
            tamanio++;
            return;
        }

        // Caso 4: posicion intermedia — recorrer para encontrar el lugar
        NodoLista actual = cabeza.siguiente;
        while (actual != null) {
            int comparacion = clave.compareToIgnoreCase(actual.clave);
            if (comparacion == 0) {
                return; // Duplicado: no se inserta
            }
            if (comparacion < 0) {
                nuevo.siguiente           = actual;
                nuevo.anterior            = actual.anterior;
                actual.anterior.siguiente = nuevo;
                actual.anterior           = nuevo;
                tamanio++;
                return;
            }
            actual = actual.siguiente;
        }
    }

    // =========================================================
    // BUSCAR
    // =========================================================

    /**
     * Busca un nodo por su clave y retorna el dato almacenado.
     * La busqueda se realiza desde la cabeza hacia la cola (O(n)).
     *
     * <p>Uso tipico:</p>
     * <pre>
     *   Bus b = (Bus) lista.buscar("P-123-ABC");
     * </pre>
     *
     * @param clave identificador de texto del nodo buscado
     * @return el dato almacenado en el nodo, o {@code null} si la clave no existe
     */
    public Object buscar(String clave) {
        NodoLista actual = cabeza;
        while (actual != null) {
            if (actual.clave.equalsIgnoreCase(clave)) {
                return actual.dato;
            }
            actual = actual.siguiente;
        }
        return null;
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    /**
     * Elimina el nodo cuya clave coincide con la indicada.
     * Actualiza correctamente los punteros de los nodos adyacentes
     * y las referencias a cabeza y cola si corresponde.
     *
     * @param clave identificador de texto del nodo a eliminar
     * @return {@code true} si el nodo fue encontrado y eliminado;
     *         {@code false} si la clave no existe en la lista
     */
    public boolean eliminar(String clave) {
        NodoLista actual = cabeza;
        while (actual != null) {
            if (actual.clave.equalsIgnoreCase(clave)) {
                if (actual == cabeza) {
                    cabeza = actual.siguiente;
                    if (cabeza != null) {
                        cabeza.anterior = null;
                    } else {
                        cola = null;
                    }
                } else if (actual == cola) {
                    cola = actual.anterior;
                    if (cola != null) {
                        cola.siguiente = null;
                    }
                } else {
                    actual.anterior.siguiente = actual.siguiente;
                    actual.siguiente.anterior = actual.anterior;
                }
                tamanio--;
                return true;
            }
            actual = actual.siguiente;
        }
        return false;
    }

    // =========================================================
    // GETTERS Y UTILIDADES
    // =========================================================

    /**
     * Retorna el primer nodo de la lista (cabeza).
     * Punto de entrada para recorridos ascendentes.
     *
     * @return el {@link NodoLista} cabeza, o {@code null} si la lista esta vacia
     */
    public NodoLista getCabeza() { return cabeza; }

    /**
     * Retorna el ultimo nodo de la lista (cola).
     * Punto de entrada para recorridos descendentes.
     *
     * @return el {@link NodoLista} cola, o {@code null} si la lista esta vacia
     */
    public NodoLista getCola() { return cola; }

    /**
     * Retorna el numero de elementos actualmente en la lista.
     *
     * @return cantidad de nodos en la lista
     */
    public int getTamanio() { return tamanio; }

    /**
     * Indica si la lista no contiene ningun nodo.
     *
     * @return {@code true} si la lista esta vacia; {@code false} en caso contrario
     */
    public boolean estaVacia() { return cabeza == null; }
}
