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

public class LoginController implements Initializable {

    private FlotaBuses escenarioPrincipal;

    @FXML private TextField     txtUsuario;
    @FXML private PasswordField pswPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Asegura que el servicio (y sus usuarios por defecto) esté inicializado
        LoginService.getInstance();
    }

    public FlotaBuses getEscenarioPrincipal() { return escenarioPrincipal; }

    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

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

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
