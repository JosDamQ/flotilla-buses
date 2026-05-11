package flotabuses.modelos;

import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.estructuras.NodoLista;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Entidad que representa la asignacion de un bus a un destino turistico.
 *
 * <p>Una asignacion vincula un {@link Bus} con un {@link Destino} confirmado.
 * Cada asignacion puede tener multiples horas de salida, almacenadas en una
 * {@code ListaDoblementeEnlazada} interna ordenada por hora ({@code "HH:mm"}).</p>
 *
 * <p>Las asignaciones se almacenan en la {@code MatrizOrtogonal} del servicio:</p>
 * <ul>
 *   <li><b>Fila:</b> nombre del destino ({@code destino.getNombre().getNombreMostrar()})</li>
 *   <li><b>Columna:</b> placa del bus ({@code bus.getPlaca()})</li>
 *   <li><b>Celda [destino][bus]:</b> esta instancia de {@code AsignacionBusDestino}</li>
 * </ul>
 *
 * <p>Reglas de validacion para horas de salida:</p>
 * <ul>
 *   <li>La hora no puede ser anterior a las 04:00 AM.</li>
 *   <li>Debe existir al menos 1 hora de diferencia con cualquier otra hora ya registrada
 *       en la misma asignacion.</li>
 *   <li>El servicio adicionalmente valida que el mismo bus no tenga una hora conflictiva
 *       en otro destino el mismo dia.</li>
 * </ul>
 *
 * <p>Navegacion desde un boleto:</p>
 * <pre>
 *   Destino d  = boleto.getAsignacion().getDestino();
 *   Bus b      = boleto.getAsignacion().getBus();
 *   double c   = boleto.getAsignacion().getDestino().getCostoBoleto();
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.AsignacionBusDestinoService
 * @see flotabuses.estructuras.MatrizOrtogonal
 */
public class AsignacionBusDestino {

    /** Identificador numerico unico generado automaticamente por el servicio. */
    private int                     codigoAsignacion;

    /** Destino turistico al que se asigna el bus. */
    private Destino                 destino;

    /** Bus asignado al destino. */
    private Bus                     bus;

    /**
     * Lista de horas de salida disponibles, ordenada por hora ascendente.
     * La clave de cada nodo es la representacion {@code "HH:mm"} de la hora.
     */
    private ListaDoblementeEnlazada horasDisponibles;

    /** Formato de hora utilizado como clave en la lista interna. */
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Construye una asignacion para el par destino-bus indicado.
     * La lista de horas inicia vacia; las horas se agregan con {@link #agregarHora(LocalTime)}.
     *
     * @param codigoAsignacion identificador unico asignado por el servicio
     * @param destino          destino turistico (debe estar {@code CONFIRMADO})
     * @param bus              bus disponible a asignar
     */
    public AsignacionBusDestino(int codigoAsignacion, Destino destino, Bus bus) {
        this.codigoAsignacion = codigoAsignacion;
        this.destino          = destino;
        this.bus              = bus;
        this.horasDisponibles = new ListaDoblementeEnlazada();
    }

    // =========================================================
    // MANEJO DE HORAS
    // =========================================================

    /**
     * Agrega una hora de salida a la lista si pasa las validaciones internas.
     *
     * <p>Validaciones aplicadas:</p>
     * <ol>
     *   <li>La hora no debe ser anterior a las 04:00 AM.</li>
     *   <li>La diferencia con cualquier hora ya registrada debe ser de al menos 3600 segundos (1 hora).</li>
     * </ol>
     *
     * @param hora hora de salida a registrar
     * @return {@code true} si la hora fue agregada; {@code false} si no paso la validacion
     */
    public boolean agregarHora(LocalTime hora) {
        if (hora.isBefore(LocalTime.of(4, 0))) {
            return false;
        }
        NodoLista actual = horasDisponibles.getCabeza();
        while (actual != null) {
            LocalTime horaExistente = (LocalTime) actual.dato;
            long diferencia = Math.abs(
                hora.toSecondOfDay() - horaExistente.toSecondOfDay()
            );
            if (diferencia < 3600) {
                return false;
            }
            actual = actual.siguiente;
        }
        horasDisponibles.insertarOrdenado(hora.format(FORMATO_HORA), hora);
        return true;
    }

    /**
     * Elimina una hora de salida de la lista.
     *
     * @param hora hora a eliminar
     * @return {@code true} si la hora existia y fue eliminada; {@code false} si no existia
     */
    public boolean eliminarHora(LocalTime hora) {
        return horasDisponibles.eliminar(hora.format(FORMATO_HORA));
    }

    /**
     * Verifica si una hora especifica esta registrada en esta asignacion.
     *
     * @param hora hora a verificar
     * @return {@code true} si la hora existe; {@code false} en caso contrario
     */
    public boolean tieneHora(LocalTime hora) {
        return horasDisponibles.buscar(hora.format(FORMATO_HORA)) != null;
    }

    /**
     * Retorna la cantidad de horas de salida registradas en esta asignacion.
     *
     * @return numero de horas disponibles
     */
    public int cantidadHoras() {
        return horasDisponibles.getTamanio();
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    /** @return codigo unico de la asignacion */
    public int getCodigoAsignacion()                  { return codigoAsignacion; }

    /** @param c nuevo codigo de asignacion */
    public void setCodigoAsignacion(int c)            { this.codigoAsignacion = c; }

    /** @return destino turistico de esta asignacion */
    public Destino getDestino()                       { return destino; }

    /** @param d nuevo destino */
    public void setDestino(Destino d)                 { this.destino = d; }

    /** @return bus asignado */
    public Bus getBus()                               { return bus; }

    /** @param b nuevo bus asignado */
    public void setBus(Bus b)                         { this.bus = b; }

    /**
     * Retorna la lista interna de horas disponibles.
     * Utilizada por el servicio y los controladores para recorrer las horas.
     *
     * @return lista doblemente enlazada de {@code LocalTime} ordenada por hora
     */
    public ListaDoblementeEnlazada getHorasDisponibles() { return horasDisponibles; }

    /**
     * Representacion textual de la asignacion para depuracion.
     *
     * @return cadena con codigo, destino, placa del bus y cantidad de horas
     */
    @Override
    public String toString() {
        return "AsignacionBusDestino{" +
               "codigo=" + codigoAsignacion +
               ", destino='" + destino.getNombre() + '\'' +
               ", bus='" + bus.getPlaca() + '\'' +
               ", horas=" + horasDisponibles.getTamanio() +
               '}';
    }
}
