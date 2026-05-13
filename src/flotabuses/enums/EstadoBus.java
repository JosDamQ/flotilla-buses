package flotabuses.enums;

/**
 * Estado operativo de un bus de la flotilla.
 *
 * <p>El estado determina si el bus puede ser seleccionado en el modulo de
 * asignaciones. Solo los buses con estado {@code DISPONIBLE} aparecen
 * en el ComboBox del formulario de asignacion.</p>
 *
 * <ul>
 *   <li>{@link #DISPONIBLE} — el bus esta operativo y puede asignarse a destinos.</li>
 *   <li>{@link #NO_DISPONIBLE} — el bus esta fuera de servicio (mantenimiento,
 *       averia, etc.) y no puede recibir nuevas asignaciones.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.modelos.Bus#estaDisponible()
 * @see flotabuses.servicios.AsignacionBusDestinoService
 */
public enum EstadoBus {

    /** El bus esta operativo y disponible para asignaciones. */
    DISPONIBLE,

    /** El bus esta fuera de servicio; no puede asignarse a nuevos destinos. */
    NO_DISPONIBLE
}
