/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.estructuras;

/**
 *
 * @author damiangarcia
 */
public class NodoMatriz {
    public String filaClave;
    public String columnaClave;
    public Object dato; 
 
    public NodoMatriz derecha;
    public NodoMatriz izquierda;
    public NodoMatriz abajo;
    public NodoMatriz arriba;
 
    public NodoMatriz(String filaClave, String columnaClave, Object dato) {
        this.filaClave    = filaClave;
        this.columnaClave = columnaClave;
        this.dato         = dato;
        this.derecha      = null;
        this.izquierda    = null;
        this.abajo        = null;
        this.arriba       = null;
    }
}
