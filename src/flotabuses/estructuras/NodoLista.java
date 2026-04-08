/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class NodoLista {
    public String clave;
    public Object dato;
    public NodoLista anterior;
    public NodoLista siguiente;
 
    public NodoLista(String clave, Object dato) {
        this.clave     = clave;
        this.dato      = dato;
        this.anterior  = null;
        this.siguiente = null;
    }
}
