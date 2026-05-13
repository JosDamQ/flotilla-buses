/**
 * Utilidades transversales de la aplicacion.
 *
 * <ul>
 *   <li>{@link flotabuses.utils.ValidadorCampos} — metodos estaticos de validacion
 *       con expresiones regulares para los formatos guatemaltecos (DPI, telefono, placa).
 *       Todos los controladores lo usan para validar entradas antes de invocar servicios.</li>
 *   <li>{@link flotabuses.utils.IconoValidacion} — subclase de {@link javafx.scene.canvas.Canvas}
 *       que dibuja un icono de verificacion (verde) o error (rojo) usando
 *       {@link javafx.scene.canvas.GraphicsContext}. Se inyecta dinamicamente
 *       en los GridPane de los formularios.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 */
package flotabuses.utils;
