/**
 * Tipos enumerados del dominio de la aplicacion.
 *
 * <p>Los enums de este paquete representan conjuntos de valores cerrados
 * (no modificables por el usuario en tiempo de ejecucion) que determinan
 * comportamientos y reglas de negocio en el sistema.</p>
 *
 * <ul>
 *   <li>{@link flotabuses.enums.TipoBus} — clasifica los buses por capacidad;
 *       cada tipo define un rango minimo y maximo de pasajeros.</li>
 *   <li>{@link flotabuses.enums.EstadoBus} — indica si un bus esta disponible
 *       para ser asignado a un destino.</li>
 *   <li>{@link flotabuses.enums.NombreDestino} — catalogo fijo de los 15 destinos
 *       turisticos ofrecidos por la agencia en Guatemala.</li>
 *   <li>{@link flotabuses.enums.EstadoDestino} — controla si un destino puede
 *       recibir asignaciones de buses y venta de boletos.</li>
 *   <li>{@link flotabuses.enums.RolUsuario} — determina los permisos de acceso
 *       a los modulos del sistema.</li>
 *   <li>{@link flotabuses.enums.Operaciones} — estado de la operacion activa
 *       en cada controlador de pantalla (CRUD state machine).</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 */
package flotabuses.enums;
