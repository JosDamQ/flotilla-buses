/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: ListaDoblementeEnlazada
 * Descripción: Lista Doblemente Enlazada implementada desde cero.
 *              Genérica (usa Object), ordenada alfabéticamente por clave (String).
 *              Reutilizada en:
 *                - Módulo Flotilla de Buses   (clave = placa)
 *                - Módulo Destinos Turísticos (clave = nombre del destino)
 *                - Módulo Boletos             (clave = hora seleccionada)
 *                - Horas disponibles en AsignacionBusDestino (clave = "HH:mm")
 *
 *              El recorrido para mostrar datos se hace desde los controladores
 *              usando getCabeza() o getCola(), haciendo cast al tipo correspondiente.
 *
 *              Ejemplo de uso desde un controlador:
 *                  NodoLista actual = lista.getCabeza();
 *                  while (actual != null) {
 *                      Bus b = (Bus) actual.dato;
 *                      actual = actual.siguiente;
 *                  }
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class ListaDoblementeEnlazada {
    private NodoLista cabeza;   // Primer nodo de la lista
    private NodoLista cola;     // Último nodo de la lista
    private int       tamanio;  // Cantidad de elementos
 
    /*
     * Constructor: lista vacía.
     */
    public ListaDoblementeEnlazada() {
        this.cabeza  = null;
        this.cola    = null;
        this.tamanio = 0;
    }
 
    // =========================================================
    // INSERTAR ORDENADO
    // =========================================================
 
    /*
     * Inserta un nuevo nodo manteniendo orden alfabético ascendente por clave.
     * Si la clave ya existe no inserta duplicados.
     */
    public void insertarOrdenado(String clave, Object dato) {
        NodoLista nuevo = new NodoLista(clave, dato);
 
        // Caso 1: lista vacía
        if (cabeza == null) {
            cabeza = nuevo;
            cola   = nuevo;
            tamanio++;
            return;
        }
 
        // Caso 2: va antes de la cabeza
        if (clave.compareToIgnoreCase(cabeza.clave) < 0) {
            nuevo.siguiente = cabeza;
            cabeza.anterior = nuevo;
            cabeza          = nuevo;
            tamanio++;
            return;
        }
 
        // Caso 3: va al final
        if (clave.compareToIgnoreCase(cola.clave) > 0) {
            nuevo.anterior = cola;
            cola.siguiente = nuevo;
            cola           = nuevo;
            tamanio++;
            return;
        }
 
        // Caso 4: posición intermedia
        NodoLista actual = cabeza.siguiente;
        while (actual != null) {
            int comparacion = clave.compareToIgnoreCase(actual.clave);
            if (comparacion == 0) {
                return; // Duplicado, no se inserta
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
 
    /*
     * Busca un nodo por su clave (de cabeza a cola).
     * Retorna el dato (Object) si lo encuentra, null si no existe.
     * Desde el controlador hacés cast: Bus b = (Bus) lista.buscar("ABC-123");
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
 
    /*
     * Elimina el nodo cuya clave coincide.
     * Maneja los casos: cabeza, cola e intermedio.
     * Retorna true si eliminó, false si no encontró la clave.
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
    // GETTERS Y UTILS
    // =========================================================
 
    /*
     * Retorna la cabeza de la lista.
     * Usado desde controladores para recorrido ascendente:
     *
     *   NodoLista actual = lista.getCabeza();
     *   while (actual != null) { ... actual = actual.siguiente; }
     */
    public NodoLista getCabeza() { return cabeza; }
 
    /*
     * Retorna la cola de la lista.
     * Usado desde controladores para recorrido descendente:
     *
     *   NodoLista actual = lista.getCola();
     *   while (actual != null) { ... actual = actual.anterior; }
     */
    public NodoLista getCola() { return cola; }
 
    /*
     * Retorna la cantidad de elementos en la lista.
     */
    public int getTamanio() { return tamanio; }
 
    /*
     * Verifica si la lista está vacía.
     */
    public boolean estaVacia() { return cabeza == null; }
}
