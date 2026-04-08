/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: MatrizOrtogonal
 * Descripción: Matriz Ortogonal implementada desde cero con listas enlazadas.
 *              Usada en el Módulo 4: Asignación de buses a destinos turísticos.
 *
 *              Estructura:
 *                - FILAS    = Destinos turísticos (clave = nombre del destino)
 *                - COLUMNAS = Buses asignados     (clave = placa del bus)
 *                - CELDAS   = Objeto AsignacionBusDestino
 *
 *              Cada celda tiene 4 punteros (←→↑↓) para recorrer en cualquier
 *              dirección como lo requiere el proyecto.
 *
 *              El recorrido para mostrar datos se hace desde los controladores
 *              usando getCabFilas() o getCabColumnas(), haciendo cast al tipo:
 *
 *              Ejemplo desde un controlador:
 *                  NodoCabecera fila = matriz.getCabFilas();
 *                  while (fila != null) {
 *                      NodoMatriz celda = fila.primero;
 *                      while (celda != null) {
 *                          AsignacionBusDestino a = (AsignacionBusDestino) celda.dato;
 *                          celda = celda.derecha;
 *                      }
 *                      fila = fila.siguiente;
 *                  }
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class MatrizOrtogonal {
    private NodoCabecera cabFilas;    // Lista de cabeceras de FILAS    (destinos)
    private NodoCabecera cabColumnas; // Lista de cabeceras de COLUMNAS (buses)
 
    /*
     * Constructor: matriz vacía.
     */
    public MatrizOrtogonal() {
        this.cabFilas    = null;
        this.cabColumnas = null;
    }
 
    // =========================================================
    // MÉTODOS AUXILIARES DE CABECERAS
    // =========================================================
 
    /*
     * Busca o crea la cabecera de fila con la clave dada.
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
 
    /*
     * Busca o crea la cabecera de columna con la clave dada.
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
 
    /*
     * Inserta una celda en la posición (filaKey, columnaKey) con el dato dado.
     * Si ya existe una celda en esa posición actualiza su dato.
     *
     * Parámetros:
     *   filaKey    - nombre del destino turístico
     *   columnaKey - placa del bus
     *   dato       - objeto AsignacionBusDestino
     */
    public void insertar(String filaKey, String columnaKey, Object dato) {
        NodoCabecera cabFila    = obtenerOCrearFila(filaKey);
        NodoCabecera cabColumna = obtenerOCrearColumna(columnaKey);
 
        // Si ya existe la celda, solo actualiza el dato
        NodoMatriz existente = buscarNodo(filaKey, columnaKey);
        if (existente != null) {
            existente.dato = dato;
            return;
        }
 
        NodoMatriz nuevo = new NodoMatriz(filaKey, columnaKey, dato);
 
        // Enlazar horizontalmente en la fila
        if (cabFila.primero == null) {
            cabFila.primero = nuevo;
        } else {
            NodoMatriz actualFila = cabFila.primero;
            while (actualFila.derecha != null) actualFila = actualFila.derecha;
            actualFila.derecha = nuevo;
            nuevo.izquierda    = actualFila;
        }
 
        // Enlazar verticalmente en la columna
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
 
    /*
     * Método privado: retorna el NodoMatriz en (filaKey, columnaKey) o null.
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
 
    /*
     * Método público: retorna el dato de la celda (filaKey, columnaKey).
     * Desde el controlador hacés cast:
     *   AsignacionBusDestino a = (AsignacionBusDestino) matriz.buscar("Antigua", "ABC-123");
     */
    public Object buscar(String filaKey, String columnaKey) {
        NodoMatriz nodo = buscarNodo(filaKey, columnaKey);
        return nodo != null ? nodo.dato : null;
    }
 
    // =========================================================
    // ELIMINAR
    // =========================================================
 
    /*
     * Elimina la celda en (filaKey, columnaKey) actualizando todos los enlaces.
     * Retorna true si eliminó, false si no encontró la celda.
     */
    public boolean eliminar(String filaKey, String columnaKey) {
        NodoMatriz nodo = buscarNodo(filaKey, columnaKey);
        if (nodo == null) return false;
 
        // Reparar enlaces horizontales
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
 
        // Reparar enlaces verticales
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
    // GETTERS Y UTILS
    // =========================================================
 
    /*
     * Retorna la cabecera de filas (destinos).
     * Usado desde controladores para recorrer la matriz por filas.
     */
    public NodoCabecera getCabFilas() { return cabFilas; }
 
    /*
     * Retorna la cabecera de columnas (buses).
     * Usado desde controladores para recorrer la matriz por columnas.
     */
    public NodoCabecera getCabColumnas() { return cabColumnas; }
 
    /*
     * Verifica si la matriz no tiene celdas.
     */
    public boolean estaVacia() { return cabFilas == null; }
}
