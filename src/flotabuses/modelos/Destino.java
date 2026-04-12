/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Descripción: Representa un destino turístico programado por la agencia.
 *              Se almacena en una Lista Doblemente Enlazada ordenada
 *              por nombre del destino.
 */
package flotabuses.modelos;
import flotabuses.enums.EstadoDestino;
import flotabuses.enums.NombreDestino;

import java.time.LocalDate;

/**
 *
 * @author damiangarcia
 */
public class Destino {
    private int           codigoDestino;  // Identificador único numérico
    private NombreDestino nombre;         // Seleccionado de lista preestablecida
    private LocalDate     fechaSalida;    // Formato dd/mm/aaaa
    private double        costoBoleto;    // En quetzales
    private EstadoDestino estado;         // CONFIRMADO o PENDIENTE
    private String        descripcion;
 
    /*
     * Constructor completo.
     */
    public Destino(int codigoDestino, NombreDestino nombre, LocalDate fechaSalida,
                   double costoBoleto, EstadoDestino estado, String descripcion) {
        this.codigoDestino = codigoDestino;
        this.nombre        = nombre;
        this.fechaSalida   = fechaSalida;
        this.costoBoleto   = costoBoleto;
        this.estado        = estado;
        this.descripcion   = descripcion;
    }
 
    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================
 
    public int getCodigoDestino()              { return codigoDestino; }
    public void setCodigoDestino(int c)        { this.codigoDestino = c; }
 
    public NombreDestino getNombre()           { return nombre; }
    public void setNombre(NombreDestino n)     { this.nombre = n; }
 
    public LocalDate getFechaSalida()          { return fechaSalida; }
    public void setFechaSalida(LocalDate f)    { this.fechaSalida = f; }
 
    public double getCostoBoleto()             { return costoBoleto; }
    public void setCostoBoleto(double c)       { this.costoBoleto = c; }
 
    public EstadoDestino getEstado()           { return estado; }
    public void setEstado(EstadoDestino e)     { this.estado = e; }
 
    public String getDescripcion()             { return descripcion; }
    public void setDescripcion(String d)       { this.descripcion = d; }
 
    /*
     * Indica si el destino está confirmado para poder asignarle buses.
     * Solo los destinos CONFIRMADOS pueden recibir asignaciones (módulo 4).
     */
    public boolean estaConfirmado() {
        return estado == EstadoDestino.CONFIRMADO;
    }
 
    @Override
    public String toString() {
//        return "Destino{" +
//               "codigo=" + codigoDestino +
//               ", nombre='" + nombre + '\'' +
//               ", fechaSalida=" + fechaSalida +
//               ", costo=" + costoBoleto +
//               ", estado=" + estado +
//               '}';
        return nombre.getNombreMostrar() + " | " + fechaSalida + " | " + costoBoleto;
    }
}
