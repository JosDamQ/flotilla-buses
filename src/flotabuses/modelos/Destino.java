package flotabuses.modelos;

import flotabuses.enums.EstadoDestino;
import flotabuses.enums.NombreDestino;
import java.time.LocalDate;

/**
 * Entidad que representa un destino turistico programado por la agencia.
 *
 * <p>Los destinos se almacenan en una {@code ListaDoblementeEnlazada} ordenada
 * alfabeticamente por el nombre del destino (valor {@link NombreDestino#getNombreMostrar()}).
 * Esto garantiza listados siempre ordenados sin clasificacion adicional.</p>
 *
 * <p>Solo los destinos con estado {@code CONFIRMADO} pueden recibir asignaciones
 * de buses en el modulo de asignaciones (modulo 4). Un destino en estado
 * {@code PENDIENTE} es visible en la lista pero no puede ser seleccionado para
 * asignar buses ni para vender boletos.</p>
 *
 * <p>El costo del boleto se almacena aqui y se referencia desde cada
 * {@code Boleto} a traves de la cadena de navegacion:
 * {@code boleto -> asignacion -> destino -> costoBoleto}.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.DestinoService
 * @see flotabuses.estructuras.ListaDoblementeEnlazada
 * @see flotabuses.modelos.AsignacionBusDestino
 */
public class Destino {

    /** Identificador numerico unico generado automaticamente por {@code DestinoService}. */
    private int           codigoDestino;

    /**
     * Nombre del destino seleccionado de la lista preestablecida {@link NombreDestino}.
     * Se usa {@code getNombreMostrar()} como clave en la lista doblemente enlazada.
     */
    private NombreDestino nombre;

    /** Fecha de salida programada para este destino. Formato de visualizacion: dd/MM/yyyy. */
    private LocalDate     fechaSalida;

    /** Costo del boleto en quetzales (GTQ). */
    private double        costoBoleto;

    /**
     * Estado operativo del destino.
     * Solo los destinos {@code CONFIRMADO} pueden recibir asignaciones y venta de boletos.
     */
    private EstadoDestino estado;

    /** Descripcion libre del destino (atractivos, condiciones, observaciones). */
    private String        descripcion;

    /**
     * Construye un destino con todos sus atributos.
     *
     * @param codigoDestino identificador unico asignado por el servicio
     * @param nombre        nombre del destino de la lista preestablecida
     * @param fechaSalida   fecha programada de salida
     * @param costoBoleto   costo del boleto en quetzales
     * @param estado        estado operativo ({@code CONFIRMADO} o {@code PENDIENTE})
     * @param descripcion   descripcion libre del destino
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

    /** @return codigo unico del destino */
    public int getCodigoDestino()              { return codigoDestino; }

    /** @param c nuevo codigo de destino */
    public void setCodigoDestino(int c)        { this.codigoDestino = c; }

    /** @return nombre del destino como valor del enum {@link NombreDestino} */
    public NombreDestino getNombre()           { return nombre; }

    /** @param n nuevo nombre del destino */
    public void setNombre(NombreDestino n)     { this.nombre = n; }

    /** @return fecha de salida programada */
    public LocalDate getFechaSalida()          { return fechaSalida; }

    /** @param f nueva fecha de salida */
    public void setFechaSalida(LocalDate f)    { this.fechaSalida = f; }

    /** @return costo del boleto en quetzales */
    public double getCostoBoleto()             { return costoBoleto; }

    /** @param c nuevo costo del boleto */
    public void setCostoBoleto(double c)       { this.costoBoleto = c; }

    /** @return estado operativo del destino */
    public EstadoDestino getEstado()           { return estado; }

    /** @param e nuevo estado operativo */
    public void setEstado(EstadoDestino e)     { this.estado = e; }

    /** @return descripcion libre del destino */
    public String getDescripcion()             { return descripcion; }

    /** @param d nueva descripcion */
    public void setDescripcion(String d)       { this.descripcion = d; }

    /**
     * Indica si el destino puede recibir asignaciones de buses y venta de boletos.
     *
     * @return {@code true} si el estado es {@code CONFIRMADO}; {@code false} si es {@code PENDIENTE}
     */
    public boolean estaConfirmado() {
        return estado == EstadoDestino.CONFIRMADO;
    }

    /**
     * Representacion textual compacta del destino para uso en ComboBox y tablas.
     *
     * @return cadena con formato "nombreMostrar | fechaSalida | costoBoleto"
     */
    @Override
    public String toString() {
        return nombre.getNombreMostrar() + " | " + fechaSalida + " | " + costoBoleto;
    }
}
