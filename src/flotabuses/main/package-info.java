/**
 * Punto de entrada de la aplicacion JavaFX.
 *
 * <p>{@link flotabuses.main.FlotaBuses} extiende {@link javafx.application.Application}
 * y actua como navegador central de escenas. Cada modulo tiene un metodo dedicado
 * ({@code ventanaCliente()}, {@code ventanaBus()}, etc.) que carga el FXML
 * correspondiente y devuelve el controlador ya inicializado.</p>
 *
 * <p>El metodo clave es {@code cambiarEscena(fxml, ancho, alto)} que usa
 * {@link javafx.fxml.FXMLLoader} para cargar el archivo FXML desde el classpath,
 * construye la {@link javafx.scene.Scene} y la asigna al {@link javafx.stage.Stage}
 * principal, redimensionandolo al tamano especificado.</p>
 *
 * @author damiangarcia
 * @version 1.0
 */
package flotabuses.main;
