/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import flotabuses.main.FlotaBuses;
import javafx.fxml.FXML;

/**
 *
 * @author damiangarcia
 */
public class LoginController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
    }
    
    public FlotaBuses getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    @FXML
    private void login(){
        // Agregar logica para login TODO: FABIAN
        escenarioPrincipal.menuPrincipal();
        
    }
    
}
