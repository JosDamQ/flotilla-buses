package flotabuses.estructuras;

/**
 * Arbol Binario de Busqueda (ABB) de proposito general.
 *
 * <p>Almacena objetos arbitrarios indexados por una clave entera.
 * Las claves se mantienen ordenadas de forma que, para cualquier nodo N,
 * todos los nodos del subarbol izquierdo tienen clave menor y todos los del
 * subarbol derecho tienen clave mayor. No se permiten claves duplicadas.</p>
 *
 * <p>En este proyecto se utiliza exclusivamente en el modulo de Clientes,
 * donde la clave es el {@code codigoCliente} generado de forma incremental.</p>
 *
 * <p>Recorrido tipico desde un controlador (in-orden ascendente):</p>
 * <pre>
 *   private void inOrden(NodoArbol nodo, List&lt;Cliente&gt; lista) {
 *       if (nodo == null) return;
 *       inOrden(nodo.izq, lista);
 *       lista.add((Cliente) nodo.dato);
 *       inOrden(nodo.der, lista);
 *   }
 * </pre>
 *
 * <p>Complejidad promedio de las operaciones: O(log n).
 * En el peor caso (arbol degenerado): O(n).</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see NodoArbol
 */
public class ArbolBinarioBusqueda {

    /** Referencia a la raiz del arbol. {@code null} si el arbol esta vacio. */
    private NodoArbol raiz;

    /**
     * Construye un arbol binario de busqueda vacio.
     */
    public ArbolBinarioBusqueda() {
        this.raiz = null;
    }

    // =========================================================
    // INSERTAR
    // =========================================================

    /**
     * Inserta un nuevo nodo con la clave y el dato indicados.
     * Si la clave ya existe en el arbol, la operacion no tiene efecto
     * (no se permiten duplicados).
     *
     * @param clave identificador entero unico del nodo
     * @param dato  objeto a almacenar (por ejemplo, una instancia de {@code Cliente})
     */
    public void insertar(int clave, Object dato) {
        raiz = insertarRec(raiz, clave, dato);
    }

    /**
     * Metodo auxiliar recursivo que recorre el arbol para encontrar la
     * posicion correcta de insercion comparando claves.
     *
     * @param nodo  nodo actual evaluado durante la recursion
     * @param clave clave del nuevo nodo
     * @param dato  dato del nuevo nodo
     * @return el nodo actualizado (raiz del subarbol procesado)
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
        // Si clave == nodo.clave: duplicado, no se inserta
        return nodo;
    }

    // =========================================================
    // BUSCAR
    // =========================================================

    /**
     * Busca un nodo por su clave y retorna el dato almacenado.
     *
     * <p>Uso tipico:</p>
     * <pre>
     *   Cliente c = (Cliente) arbol.buscar(101);
     * </pre>
     *
     * @param clave identificador entero del nodo buscado
     * @return el dato almacenado en el nodo, o {@code null} si la clave no existe
     */
    public Object buscar(int clave) {
        NodoArbol resultado = buscarRec(raiz, clave);
        return resultado != null ? resultado.dato : null;
    }

    /**
     * Metodo auxiliar recursivo de busqueda por clave.
     *
     * @param nodo  nodo actual evaluado durante la recursion
     * @param clave clave a buscar
     * @return el {@link NodoArbol} encontrado, o {@code null} si no existe
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

    /**
     * Elimina el nodo con la clave indicada del arbol.
     * Maneja los tres casos clasicos de eliminacion en un ABB:
     * <ol>
     *   <li>Nodo hoja (sin hijos): se elimina directamente.</li>
     *   <li>Nodo con un solo hijo: se reemplaza por ese hijo.</li>
     *   <li>Nodo con dos hijos: se reemplaza por su sucesor in-orden
     *       (el nodo con la clave minima del subarbol derecho).</li>
     * </ol>
     *
     * @param clave identificador entero del nodo a eliminar
     */
    public void eliminar(int clave) {
        raiz = eliminarRec(raiz, clave);
    }

    /**
     * Metodo auxiliar recursivo que localiza y elimina el nodo con la clave dada.
     *
     * @param nodo  nodo actual evaluado durante la recursion
     * @param clave clave del nodo a eliminar
     * @return el nodo actualizado (raiz del subarbol procesado tras la eliminacion)
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
            // Nodo encontrado: manejar los tres casos
            if (nodo.izq == null) return nodo.der;
            if (nodo.der == null) return nodo.izq;
            // Caso dos hijos: sustituir por el sucesor in-orden
            NodoArbol sucesor = minimoNodo(nodo.der);
            nodo.clave = sucesor.clave;
            nodo.dato  = sucesor.dato;
            nodo.der   = eliminarRec(nodo.der, sucesor.clave);
        }
        return nodo;
    }

    /**
     * Retorna el nodo con la clave minima dentro del subarbol cuya raiz
     * es el nodo indicado.
     * Se usa para encontrar el sucesor in-orden al eliminar un nodo con
     * dos hijos.
     *
     * @param nodo raiz del subarbol donde se busca el minimo
     * @return el {@link NodoArbol} con la clave mas pequena del subarbol
     */
    private NodoArbol minimoNodo(NodoArbol nodo) {
        NodoArbol actual = nodo;
        while (actual.izq != null) {
            actual = actual.izq;
        }
        return actual;
    }

    // =========================================================
    // GETTERS Y UTILIDADES
    // =========================================================

    /**
     * Retorna la raiz del arbol.
     * Utilizado desde los controladores y servicios para recorridos
     * in-orden manuales o para la visualizacion grafica de la estructura.
     *
     * @return el {@link NodoArbol} raiz, o {@code null} si el arbol esta vacio
     */
    public NodoArbol getRaiz() {
        return raiz;
    }

    /**
     * Indica si el arbol no contiene ningun nodo.
     *
     * @return {@code true} si el arbol esta vacio; {@code false} en caso contrario
     */
    public boolean estaVacio() {
        return raiz == null;
    }
}
