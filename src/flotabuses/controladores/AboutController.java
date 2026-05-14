package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * Controlador de la pantalla "Acerca de" del sistema.
 *
 * <p>Gestiona la pantalla {@code about.fxml}, que muestra informacion general
 * del proyecto: nombre del sistema, version, autores y tecnologias utilizadas.</p>
 *
 * <p>Solo contiene un boton de regreso al menu principal.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.main.FlotaBuses#ventanaAbout()
 */
public class AboutController implements Initializable {

    /** Referencia a la aplicacion principal para la navegacion entre escenas. */
    private FlotaBuses escenarioPrincipal;

    /**
     * Inicializa el controlador. No realiza acciones adicionales.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {}

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
     * Navega de regreso al menu principal invocando
     * {@link FlotaBuses#menuPrincipal()}.
     */
    public void regresarMenu() {
        escenarioPrincipal.menuPrincipal();
    }
}
