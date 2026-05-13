package flotabuses.enums;

/**
 * Rol de acceso de un usuario del sistema.
 *
 * <p>El rol se asigna al momento de la autenticacion en {@link flotabuses.servicios.LoginService}
 * y se propaga a {@link flotabuses.controladores.MenuController}, donde determina
 * que modulos se muestran en el menu principal.</p>
 *
 * <table border="1" cellpadding="5" cellspacing="0">
 *   <caption>Permisos por rol</caption>
 *   <tr><th>Modulo</th><th>ADMIN</th><th>OPERADOR</th></tr>
 *   <tr><td>Clientes</td><td>Si</td><td>Si</td></tr>
 *   <tr><td>Buses</td><td>Si</td><td>No</td></tr>
 *   <tr><td>Destinos</td><td>Si</td><td>No</td></tr>
 *   <tr><td>Asignaciones</td><td>Si</td><td>No</td></tr>
 *   <tr><td>Compra de Boletos</td><td>Si</td><td>Si</td></tr>
 *   <tr><td>Visualizador de Estructuras</td><td>Si</td><td>Si</td></tr>
 * </table>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.modelos.Usuario#esAdmin()
 * @see flotabuses.modelos.Usuario#esOperador()
 * @see flotabuses.controladores.MenuController
 */
public enum RolUsuario {

    /** Acceso completo a todos los modulos del sistema. */
    ADMIN,

    /** Acceso restringido: solo Clientes y Compra de Boletos. */
    OPERADOR
}
