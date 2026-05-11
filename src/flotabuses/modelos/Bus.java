package flotabuses.modelos;

import flotabuses.enums.EstadoBus;
import flotabuses.enums.TipoBus;

/**
 * Entidad que representa un bus de la flotilla de la agencia turistica.
 *
 * <p>Los buses se almacenan en una {@code ListaDoblementeEnlazada} ordenada
 * alfabeticamente por placa, lo que garantiza listados siempre ordenados
 * sin necesidad de clasificacion posterior.</p>
 *
 * <p>La placa sigue el formato vehicular de Guatemala: {@code X-000-XXX}
 * (una letra, guion, tres digitos, guion, tres letras). Ejemplo: {@code P-123-ABC}.</p>
 *
 * <p>La capacidad se valida automaticamente contra los rangos permitidos
 * segun el tipo de bus al momento de asignarla:</p>
 * <ul>
 *   <li><b>MICROBUS:</b> 10 a 25 pasajeros.</li>
 *   <li><b>COUNTY:</b> 26 a 45 pasajeros.</li>
 *   <li><b>PULLMAN:</b> 46 a 80 pasajeros.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.BusService
 * @see flotabuses.estructuras.ListaDoblementeEnlazada
 */
public class Bus {

    /** Identificador numerico unico generado automaticamente. */
    private int       codigoBus;

    /**
     * Placa del bus, unica en la flotilla.
     * Formato guatemalteco: {@code X-000-XXX}.
     */
    private String    placa;

    /** Tipo de bus que determina el rango de capacidad valido. */
    private TipoBus   tipo;

    /**
     * Capacidad de pasajeros validada segun el tipo.
     * Se asigna a traves del setter {@link #setCapacidad(int)} que lanza
     * {@code IllegalArgumentException} si el valor esta fuera del rango.
     */
    private int       capacidad;

    /** Color del bus. Solo letras y espacios. */
    private String    color;

    /** Estado operativo del bus: {@code DISPONIBLE} o {@code NO_DISPONIBLE}. */
    private EstadoBus estado;

    /** Descripcion libre del bus (modelo, marca, observaciones). */
    private String    descripcion;

    /**
     * Construye un bus con todos sus atributos.
     * La capacidad se valida internamente mediante {@link #setCapacidad(int)}.
     *
     * @param codigoBus   identificador unico asignado por el servicio
     * @param placa       placa en formato guatemalteco {@code X-000-XXX}
     * @param tipo        tipo de bus ({@code MICROBUS}, {@code COUNTY} o {@code PULLMAN})
     * @param capacidad   numero de pasajeros; debe estar en el rango del tipo
     * @param color       color del bus
     * @param estado      estado operativo ({@code DISPONIBLE} o {@code NO_DISPONIBLE})
     * @param descripcion descripcion libre del bus
     * @throws IllegalArgumentException si la capacidad esta fuera del rango del tipo
     */
    public Bus(int codigoBus, String placa, TipoBus tipo, int capacidad,
               String color, EstadoBus estado, String descripcion) {
        this.codigoBus   = codigoBus;
        this.placa       = placa;
        this.tipo        = tipo;
        this.color       = color;
        this.estado      = estado;
        this.descripcion = descripcion;
        setCapacidad(capacidad);
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    /** @return codigo unico del bus */
    public int getCodigoBus()            { return codigoBus; }

    /** @param c nuevo codigo de bus */
    public void setCodigoBus(int c)      { this.codigoBus = c; }

    /** @return placa del bus */
    public String getPlaca()             { return placa; }

    /** @param p nueva placa */
    public void setPlaca(String p)       { this.placa = p; }

    /** @return tipo de bus */
    public TipoBus getTipo()             { return tipo; }

    /** @param t nuevo tipo */
    public void setTipo(TipoBus t)       { this.tipo = t; }

    /** @return capacidad de pasajeros */
    public int getCapacidad()            { return capacidad; }

    /**
     * Asigna la capacidad del bus validando que este dentro del rango
     * permitido para el tipo actual.
     *
     * @param capacidad numero de pasajeros a asignar
     * @throws IllegalArgumentException si el valor no corresponde al rango del tipo
     */
    public void setCapacidad(int capacidad) {
        if (tipo == null) {
            this.capacidad = capacidad;
            return;
        }
        if (!tipo.capacidadValida(capacidad)) {
            throw new IllegalArgumentException(
                "Capacidad " + capacidad + " no valida para tipo " + tipo +
                ". Rango permitido: " + tipo.getCapacidadMin() +
                " - " + tipo.getCapacidadMax()
            );
        }
        this.capacidad = capacidad;
    }

    /** @return color del bus */
    public String getColor()             { return color; }

    /** @param c nuevo color */
    public void setColor(String c)       { this.color = c; }

    /** @return estado operativo del bus */
    public EstadoBus getEstado()         { return estado; }

    /** @param e nuevo estado operativo */
    public void setEstado(EstadoBus e)   { this.estado = e; }

    /** @return descripcion libre del bus */
    public String getDescripcion()       { return descripcion; }

    /** @param d nueva descripcion */
    public void setDescripcion(String d) { this.descripcion = d; }

    /**
     * Indica si el bus puede ser asignado a un destino turistico.
     *
     * @return {@code true} si el estado es {@code DISPONIBLE}; {@code false} en caso contrario
     */
    public boolean estaDisponible() {
        return estado == EstadoBus.DISPONIBLE;
    }

    /**
     * Representacion textual compacta del bus para uso en ComboBox y tablas.
     * @return cadena con formato "placa | tipo | Cap: N"
     */
    @Override
    public String toString() {
        return placa + " | " + tipo + " | Cap: " + capacidad;
    }
}
