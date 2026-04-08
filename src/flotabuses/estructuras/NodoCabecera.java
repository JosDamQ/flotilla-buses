/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class NodoCabecera {
    public String clave;
    public NodoCabecera siguiente;
    public NodoMatriz primero;
 
    public NodoCabecera(String clave) {
        this.clave     = clave;
        this.siguiente = null;
        this.primero   = null;
    }
}
