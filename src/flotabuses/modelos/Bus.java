/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: Bus
 * Descripción: Representa un bus de la flotilla de la agencia turística.
 *              Se almacena en una Lista Doblemente Enlazada ordenada por placa.
 *              La capacidad se valida contra el tipo de bus al momento de asignarla.
 */
package flotabuses.modelos;
import flotabuses.enums.EstadoBus;
import flotabuses.enums.TipoBus;

/**
 *
 * @author damiangarcia
 */
public class Bus {
    private int       codigoBus;    // Identificador único numérico
    private String    placa;        // Identificador único alfanumérico
    private TipoBus   tipo;         // MICROBUS, COUNTY o PULLMAN
    private int       capacidad;    // Validada según rango del tipo
    private String    color;
    private EstadoBus estado;       // DISPONIBLE o NO_DISPONIBLE
    private String    descripcion;
 
    /*
     * Constructor completo.
     * Lanza IllegalArgumentException si la capacidad no corresponde al tipo.
     */
    public Bus(int codigoBus, String placa, TipoBus tipo, int capacidad,
               String color, EstadoBus estado, String descripcion) {
        this.codigoBus   = codigoBus;
        this.placa       = placa;
        this.tipo        = tipo;
        this.color       = color;
        this.estado      = estado;
        this.descripcion = descripcion;
        setCapacidad(capacidad); // Pasa por validación
    }
 
    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================
 
    public int getCodigoBus()               { return codigoBus; }
    public void setCodigoBus(int c)         { this.codigoBus = c; }
 
    public String getPlaca()                { return placa; }
    public void setPlaca(String p)          { this.placa = p; }
 
    public TipoBus getTipo()                { return tipo; }
    public void setTipo(TipoBus t)          { this.tipo = t; }
 
    /*
     * Setter de capacidad con validación:
     * verifica que el valor esté dentro del rango permitido para el tipo actual.
     */
    public int getCapacidad()               { return capacidad; }
    public void setCapacidad(int capacidad) {
        if (tipo == null) {
            this.capacidad = capacidad;
            return;
        }
        if (!tipo.capacidadValida(capacidad)) {
            throw new IllegalArgumentException(
                "Capacidad " + capacidad + " no válida para tipo " + tipo +
                ". Rango permitido: " + tipo.getCapacidadMin() +
                " - " + tipo.getCapacidadMax()
            );
        }
        this.capacidad = capacidad;
    }
 
    public String getColor()                { return color; }
    public void setColor(String c)          { this.color = c; }
 
    public EstadoBus getEstado()            { return estado; }
    public void setEstado(EstadoBus e)      { this.estado = e; }
 
    public String getDescripcion()          { return descripcion; }
    public void setDescripcion(String d)    { this.descripcion = d; }
 
    /*
     * Indica si el bus está disponible para ser asignado a un destino.
     */
    public boolean estaDisponible() {
        return estado == EstadoBus.DISPONIBLE;
    }
 
    @Override
    public String toString() {
//        return "Bus{" +
//               "codigo=" + codigoBus +
//               ", placa='" + placa + '\'' +
//               ", tipo=" + tipo +
//               ", capacidad=" + capacidad +
//               ", estado=" + estado +
//               '}';
        
        return placa + " | " + tipo + " | Cap: " + capacidad;
    }
}
