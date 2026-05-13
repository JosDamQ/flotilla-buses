package flotabuses.enums;

/**
 * Estado de un destino turistico programado por la agencia.
 *
 * <p>El estado controla el flujo de trabajo del destino a traves de los modulos:</p>
 * <ul>
 *   <li>{@link #CONFIRMADO} — el destino esta aprobado. Puede recibir buses en el
 *       modulo de asignaciones y sus boletos pueden venderse en el modulo de compra.</li>
 *   <li>{@link #PENDIENTE} — el destino esta en revision. Es visible en la lista
 *       pero no puede recibir asignaciones ni ventas hasta ser confirmado.</li>
 * </ul>
 *
 * <p>El cambio de estado se realiza desde {@link flotabuses.controladores.DestinoController}
 * editando el destino y seleccionando el nuevo estado en el ComboBox.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.modelos.Destino#estaConfirmado()
 * @see flotabuses.servicios.AsignacionBusDestinoService#guardar
 */
public enum EstadoDestino {

    /** Destino aprobado; acepta asignaciones de buses y venta de boletos. */
    CONFIRMADO,

    /** Destino en revision; no acepta asignaciones ni venta de boletos. */
    PENDIENTE
}
