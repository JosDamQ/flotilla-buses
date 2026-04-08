/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: NodoArbol
 * Descripción: Nodo para el Árbol Binario de Búsqueda.
 *              Cada nodo guarda un dato genérico (objeto Cliente),
 *              una clave numérica para ordenar, y referencias
 *              al hijo izquierdo y al hijo derecho.
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class NodoArbol {
    public int clave;
    public Object dato; 
    public NodoArbol izq;
    public NodoArbol der;
 
    public NodoArbol(int clave, Object dato) {
        this.clave = clave;
        this.dato  = dato;
        this.izq   = null;
        this.der   = null;
    }
}
