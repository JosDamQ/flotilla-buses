package flotabuses.modelos;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad que representa un boleto de viaje emitido a un cliente.
 *
 * <p>Un boleto vincula un {@link Cliente} con una {@link AsignacionBusDestino} especifica
 * y con la hora de salida que el cliente selecciono de las disponibles en esa asignacion.
 * La hora seleccionada se valida en el constructor: si no existe en la lista de horas
 * de la asignacion, se lanza {@code IllegalArgumentException}.</p>
 *
 * <p>Los boletos se almacenan en una {@code ListaDoblementeEnlazada} ordenada
 * por hora de salida (clave {@code "HH:mm"}).</p>
 *
 * <p>Navegacion completa desde un boleto:</p>
 * <pre>
 *   Destino    d = boleto.getAsignacion().getDestino();
 *   Bus        b = boleto.getAsignacion().getBus();
 *   double  cost = boleto.getAsignacion().getDestino().getCostoBoleto();
 *   LocalDate fs = boleto.getAsignacion().getDestino().getFechaSalida();
 * </pre>
 *
 * <p>El metodo de conveniencia {@link #getCosto()} abstrae la navegacion
 * para obtener el costo directamente.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.BoletoService
 * @see flotabuses.estructuras.ListaDoblementeEnlazada
 */
public class Boleto {

    /** Identificador numerico unico generado automaticamente por {@code BoletoService}. */
    private int                  codigoBoleto;

    /**
     * Asignacion bus-destino a la que corresponde este boleto.
     * Permite navegar al destino, al bus y a sus horas disponibles.
     */
    private AsignacionBusDestino asignacion;

    /** Cliente que adquirio el boleto. */
    private Cliente              cliente;

    /**
     * Hora de salida escogida por el cliente de entre las disponibles
     * en la asignacion referenciada.
     */
    private LocalTime            horaSeleccionada;

    /** Formato de hora utilizado como clave de ordenamiento en la lista. */
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Construye un boleto validando que la hora seleccionada exista en la asignacion.
     *
     * @param codigoBoleto    identificador unico asignado por el servicio
     * @param asignacion      asignacion bus-destino a la que pertenece el boleto
     * @param cliente         cliente que adquiere el boleto
     * @param horaSeleccionada hora de salida seleccionada por el cliente
     * @throws IllegalArgumentException si la hora no esta disponible en la asignacion
     */
    public Boleto(int codigoBoleto, AsignacionBusDestino asignacion,
                  Cliente cliente, LocalTime horaSeleccionada) {
        if (!asignacion.tieneHora(horaSeleccionada)) {
            throw new IllegalArgumentException(
                "La hora " + horaSeleccionada.format(FORMATO_HORA) +
                " no esta disponible en esta asignacion."
            );
        }
        this.codigoBoleto     = codigoBoleto;
        this.asignacion       = asignacion;
        this.cliente          = cliente;
        this.horaSeleccionada = horaSeleccionada;
    }

    // =========================================================
    // METODOS DE CONVENIENCIA
    // =========================================================

    /**
     * Retorna el costo del boleto navegando desde la asignacion al destino.
     *
     * @return costo del boleto en quetzales
     */
    public double getCosto() {
        return asignacion.getDestino().getCostoBoleto();
    }

    /**
     * Retorna la clave de ordenamiento para la {@code ListaDoblementeEnlazada}.
     * El formato {@code "HH:mm"} garantiza ordenamiento cronologico correcto.
     *
     * @return cadena con formato "HH:mm" de la hora seleccionada
     */
    public String getClaveOrden() {
        return horaSeleccionada.format(FORMATO_HORA);
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    /** @return codigo unico del boleto */
    public int getCodigoBoleto()                      { return codigoBoleto; }

    /** @param c nuevo codigo de boleto */
    public void setCodigoBoleto(int c)                { this.codigoBoleto = c; }

    /** @return asignacion bus-destino asociada a este boleto */
    public AsignacionBusDestino getAsignacion()       { return asignacion; }

    /** @param a nueva asignacion */
    public void setAsignacion(AsignacionBusDestino a) { this.asignacion = a; }

    /** @return cliente propietario del boleto */
    public Cliente getCliente()                       { return cliente; }

    /** @param c nuevo cliente propietario */
    public void setCliente(Cliente c)                 { this.cliente = c; }

    /** @return hora de salida seleccionada por el cliente */
    public LocalTime getHoraSeleccionada()            { return horaSeleccionada; }

    /** @param h nueva hora seleccionada */
    public void setHoraSeleccionada(LocalTime h)      { this.horaSeleccionada = h; }

    /**
     * Representacion textual del boleto para depuracion y reportes.
     *
     * @return cadena con codigo, cliente, destino, bus, hora y costo
     */
    @Override
    public String toString() {
        return "Boleto{" +
               "codigo=" + codigoBoleto +
               ", cliente='" + cliente.getNombreCompleto() + '\'' +
               ", destino='" + asignacion.getDestino().getNombre() + '\'' +
               ", bus='" + asignacion.getBus().getPlaca() + '\'' +
               ", hora=" + horaSeleccionada.format(FORMATO_HORA) +
               ", costo=Q" + getCosto() +
               '}';
    }
}
