/**
 * Capa de logica de negocio del sistema de flotilla de buses.
 *
 * <p>Cada servicio implementa el patron <b>Singleton</b> (instancia unica accesible
 * mediante {@code getInstance()}) y posee la estructura de datos que mantiene
 * los registros en memoria durante la sesion de la aplicacion.</p>
 *
 * <p>Los datos <b>no se persisten en disco</b> al cerrar la aplicacion; el usuario
 * debe exportar CSV antes de cerrar si desea conservarlos.</p>
 *
 * <h2>Servicios disponibles</h2>
 * <table border="1">
 *   <caption>Resumen de servicios</caption>
 *   <tr><th>Servicio</th><th>Estructura interna</th><th>Modelo gestionado</th></tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.ClienteService}</td>
 *     <td>{@link flotabuses.estructuras.ArbolBinarioBusqueda}</td>
 *     <td>{@link flotabuses.modelos.Cliente}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.BusService}</td>
 *     <td>{@link flotabuses.estructuras.ListaDoblementeEnlazada}</td>
 *     <td>{@link flotabuses.modelos.Bus}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.DestinoService}</td>
 *     <td>{@link flotabuses.estructuras.ListaDoblementeEnlazada}</td>
 *     <td>{@link flotabuses.modelos.Destino}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.AsignacionBusDestinoService}</td>
 *     <td>{@link flotabuses.estructuras.MatrizOrtogonal}</td>
 *     <td>{@link flotabuses.modelos.AsignacionBusDestino}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.BoletoService}</td>
 *     <td>{@link flotabuses.estructuras.ListaDoblementeEnlazada}</td>
 *     <td>{@link flotabuses.modelos.Boleto}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.LoginService}</td>
 *     <td>Array estatico de {@link flotabuses.modelos.Usuario}</td>
 *     <td>{@link flotabuses.modelos.Usuario}</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.servicios.ReporteService}</td>
 *     <td>—</td>
 *     <td>Genera reportes PDF/HTML consultando los otros servicios</td>
 *   </tr>
 * </table>
 *
 * <h2>Patron de acceso desde los controladores</h2>
 * <pre>
 *   // Obtener instancia del servicio
 *   ClienteService svc = ClienteService.getInstance();
 *
 *   // Crear un registro
 *   int resultado = svc.crear(nombre, apellido, dpi, email, password, telefono);
 *
 *   // Acceder a la estructura de datos directamente (para el visualizador)
 *   NodoArbol raiz = svc.getArbol().getRaiz();
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 */
package flotabuses.servicios;
