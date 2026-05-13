/**
 * Controladores JavaFX de cada pantalla del sistema.
 *
 * <p>Cada controlador implementa {@link javafx.fxml.Initializable} y recibe
 * una referencia al navegador de escenas {@link flotabuses.main.FlotaBuses}
 * a traves del metodo {@code setEscenarioPrincipal(FlotaBuses)}.</p>
 *
 * <p>El ciclo de vida de un controlador es el siguiente:</p>
 * <ol>
 *   <li>{@code FlotaBuses.cambiarEscena(fxml, w, h)} carga el FXML y obtiene el controlador.</li>
 *   <li>Llama a {@code setEscenarioPrincipal(this)} para inyectar el navegador.</li>
 *   <li>El metodo {@code initialize()} de JavaFX configura la tabla, el formulario
 *       y los iconos de validacion.</li>
 *   <li>Los eventos del usuario (@FXML) invocan los metodos del servicio correspondiente.</li>
 *   <li>Los resultados se reflejan en la tabla mediante {@code ObservableList}.</li>
 * </ol>
 *
 * <h2>Controladores disponibles</h2>
 * <ul>
 *   <li>{@link flotabuses.controladores.LoginController} — autenticacion de usuarios.</li>
 *   <li>{@link flotabuses.controladores.MenuController} — menu principal con control de acceso por rol.</li>
 *   <li>{@link flotabuses.controladores.ClienteController} — CRUD de clientes con validacion en tiempo real.</li>
 *   <li>{@link flotabuses.controladores.BusController} — CRUD de buses de la flotilla.</li>
 *   <li>{@link flotabuses.controladores.DestinoController} — CRUD de destinos turisticos.</li>
 *   <li>{@link flotabuses.controladores.AsignacionBusDestinoController} — gestion de la matriz de asignaciones.</li>
 *   <li>{@link flotabuses.controladores.CompraBoletos} — emision y cancelacion de boletos.</li>
 *   <li>{@link flotabuses.controladores.VisualizadorEstructurasController} — diagrama grafico Canvas de las estructuras.</li>
 *   <li>{@link flotabuses.controladores.AboutController} — pantalla de informacion del sistema.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 2.0
 */
package flotabuses.controladores;
