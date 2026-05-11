package flotabuses.estructuras;

/**
 * Nodo interno de la Lista Doblemente Enlazada.
 *
 * <p>Cada nodo almacena una clave de texto usada para mantener el orden
 * alfabetico ascendente, un dato generico ({@code Object}) convertible
 * mediante cast al tipo concreto, y punteros bidireccionales al nodo
 * anterior y al siguiente.</p>
 *
 * <p>La misma clase se reutiliza en tres modulos:</p>
 * <ul>
 *   <li><b>Buses:</b> clave = placa del bus, dato = {@code Bus}</li>
 *   <li><b>Destinos:</b> clave = nombre del destino, dato = {@code Destino}</li>
 *   <li><b>Horas de asignacion:</b> clave = "HH:mm", dato = {@code LocalTime}</li>
 *   <li><b>Boletos:</b> clave = "HH:mm" de la hora seleccionada, dato = {@code Boleto}</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 * @see ListaDoblementeEnlazada
 */
public class NodoLista {

    /**
     * Clave de texto que determina la posicion del nodo en el orden alfabetico.
     */
    public String clave;

    /**
     * Dato generico almacenado en el nodo.
     * El tipo concreto depende del modulo que utiliza la lista.
     */
    public Object dato;

    /**
     * Puntero al nodo anterior en la lista.
     * {@code null} si este nodo es la cabeza.
     */
    public NodoLista anterior;

    /**
     * Puntero al nodo siguiente en la lista.
     * {@code null} si este nodo es la cola.
     */
    public NodoLista siguiente;

    /**
     * Construye un nodo con la clave y el dato indicados.
     * Los punteros {@code anterior} y {@code siguiente} quedan en {@code null}.
     *
     * @param clave identificador de texto que define el orden del nodo
     * @param dato  objeto a almacenar en el nodo
     */
    public NodoLista(String clave, Object dato) {
        this.clave     = clave;
        this.dato      = dato;
        this.anterior  = null;
        this.siguiente = null;
    }
}
