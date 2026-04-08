/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class ArbolBinarioBusqueda {
    private NodoArbol raiz; // Referencia a la raíz del árbol
 
    /*
     * Constructor: árbol vacío.
     */
    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }
 
    // =========================================================
    // INSERTAR
    // =========================================================
 
    /*
     * Inserta un nuevo nodo con la clave y dato dados.
     * Si la clave ya existe no inserta duplicados.
     */
    public void insertar(int clave, Object dato) {
        raiz = insertarRec(raiz, clave, dato);
    }
 
    /*
     * Método privado recursivo: recorre el árbol comparando claves
     * para encontrar la posición correcta de inserción.
     */
    private NodoArbol insertarRec(NodoArbol nodo, int clave, Object dato) {
        if (nodo == null) {
            return new NodoArbol(clave, dato);
        }
        if (clave < nodo.clave) {
            nodo.izq = insertarRec(nodo.izq, clave, dato);
        } else if (clave > nodo.clave) {
            nodo.der = insertarRec(nodo.der, clave, dato);
        }
        // clave == nodo.clave: duplicado, no se inserta
        return nodo;
    }
 
    // =========================================================
    // BUSCAR
    // =========================================================
 
    /*
     * Busca un nodo por su clave.
     * Retorna el dato (Object) si lo encuentra, null si no existe.
     * Desde el controlador hacés cast: Cliente c = (Cliente) arbol.buscar(101);
     */
    public Object buscar(int clave) {
        NodoArbol resultado = buscarRec(raiz, clave);
        return resultado != null ? resultado.dato : null;
    }
 
    /*
     * Método privado recursivo de búsqueda.
     */
    private NodoArbol buscarRec(NodoArbol nodo, int clave) {
        if (nodo == null || nodo.clave == clave) {
            return nodo;
        }
        if (clave < nodo.clave) {
            return buscarRec(nodo.izq, clave);
        } else {
            return buscarRec(nodo.der, clave);
        }
    }
 
    // =========================================================
    // ELIMINAR
    // =========================================================
 
    /*
     * Elimina el nodo con la clave dada.
     */
    public void eliminar(int clave) {
        raiz = eliminarRec(raiz, clave);
    }
 
    /*
     * Método privado recursivo: maneja los tres casos de eliminación:
     *   1. Nodo hoja (sin hijos)    → se elimina directamente.
     *   2. Nodo con un solo hijo    → se reemplaza por ese hijo.
     *   3. Nodo con dos hijos       → se reemplaza por su sucesor
     *                                 in-orden (mínimo del subárbol derecho).
     */
    private NodoArbol eliminarRec(NodoArbol nodo, int clave) {
        if (nodo == null) {
            return null;
        }
        if (clave < nodo.clave) {
            nodo.izq = eliminarRec(nodo.izq, clave);
        } else if (clave > nodo.clave) {
            nodo.der = eliminarRec(nodo.der, clave);
        } else {
            // Nodo encontrado
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;
            // Caso dos hijos: buscar sucesor in-orden
            NodoArbol sucesor = minimoNodo(nodo.der);
            nodo.clave = sucesor.clave;
            nodo.dato  = sucesor.dato;
            nodo.der   = eliminarRec(nodo.der, sucesor.clave);
        }
        return nodo;
    }
 
    /*
     * Retorna el nodo con la clave mínima dentro del subárbol dado.
     */
    private NodoArbol minimoNodo(NodoArbol nodo) {
        NodoArbol actual = nodo;
        while (actual.izq != null) {
            actual = actual.izq;
        }
        return actual;
    }
 
    // =========================================================
    // GETTERS Y UTILS
    // =========================================================
 
    /*
     * Retorna la raíz del árbol.
     * Usado desde los controladores para iterar y mostrar datos:
     *
     *   NodoArbol nodo = arbol.getRaiz();
     *   // recorrido in-orden ascendente manual desde el controlador
     */
    public NodoArbol getRaiz() {
        return raiz;
    }
 
    /*
     * Verifica si el árbol está vacío.
     */
    public boolean estaVacio() {
        return raiz == null;
    }
}
