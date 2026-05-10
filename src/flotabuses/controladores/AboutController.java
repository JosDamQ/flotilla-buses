package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

public class AboutController implements Initializable {

    private FlotaBuses escenarioPrincipal;

    @Override
    public void initialize(URL location, ResourceBundle resources) {}

    public FlotaBuses getEscenarioPrincipal() { return escenarioPrincipal; }

    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public void regresarMenu() {
        escenarioPrincipal.menuPrincipal();
    }
}
