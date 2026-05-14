package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Usuario;
import flotabuses.servicios.LoginService;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controlador de la pantalla de inicio de sesion.
 *
 * <p>Gestiona la pantalla {@code login.fxml} y delega la autenticacion en
 * {@link flotabuses.servicios.LoginService}.</p>
 *
 * <p>Al autenticarse correctamente, el usuario queda registrado en la aplicacion
 * mediante {@link flotabuses.main.FlotaBuses#setUsuarioActual(flotabuses.modelos.Usuario)}
 * y se navega al menu principal. Si las credenciales son incorrectas, se muestra
 * una alerta de error y se limpia el campo de contrasena.</p>
 *
 * <p>Usuarios predefinidos del sistema:</p>
 * <ul>
 *   <li>{@code admin / admin123} — rol ADMIN (acceso completo)</li>
 *   <li>{@code operador / oper123} — rol OPERADOR (acceso restringido)</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.LoginService
 * @see flotabuses.modelos.Usuario
 * @see flotabuses.enums.RolUsuario
 */
public class LoginController implements Initializable {

    /** Referencia a la aplicacion principal para la navegacion y para registrar el usuario. */
    private FlotaBuses escenarioPrincipal;

    /** Campo de texto donde el usuario ingresa su nombre de usuario. */
    @FXML private TextField     txtUsuario;

    /** Campo de contrasena que oculta los caracteres tecleados. */
    @FXML private PasswordField pswPassword;

    /**
     * Inicializa el controlador forzando la creacion del {@link flotabuses.servicios.LoginService}
     * Singleton para que los usuarios predefinidos esten listos antes del primer login.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Asegura que el servicio (y sus usuarios por defecto) esté inicializado
        LoginService.getInstance();
    }

    /**
     * Retorna la instancia principal de la aplicacion.
     *
     * @return referencia a {@link FlotaBuses}
     */
    public FlotaBuses getEscenarioPrincipal() { return escenarioPrincipal; }

    /**
     * Inyecta la instancia principal de la aplicacion.
     *
     * @param escenarioPrincipal instancia principal de la aplicacion
     */
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    /**
     * Autentica al usuario con las credenciales ingresadas en el formulario.
     * Si alguno de los campos esta vacio, muestra una alerta de campos vacios.
     * Invoca {@link flotabuses.servicios.LoginService#autenticar} y, si las
     * credenciales son correctas, establece el usuario en la aplicacion y navega
     * al menu principal. Si son incorrectas, limpia el campo de contrasena y
     * muestra una alerta de error.
     */
    @FXML
    private void login() {
        String username = txtUsuario.getText().trim();
        String password = pswPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarError("Campos vacíos", "Por favor ingrese usuario y contraseña.");
            return;
        }

        Usuario usuario = LoginService.getInstance().autenticar(username, password);

        if (usuario == null) {
            mostrarError("Credenciales incorrectas", "Usuario o contraseña inválidos.");
            pswPassword.clear();
        } else {
            escenarioPrincipal.setUsuarioActual(usuario);
            escenarioPrincipal.menuPrincipal();
        }
    }

    /**
     * Muestra un dialogo de alerta de tipo {@code ERROR} con el titulo y mensaje
     * indicados.
     *
     * @param titulo  texto del titulo del dialogo
     * @param mensaje contenido del cuerpo del dialogo
     */
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
