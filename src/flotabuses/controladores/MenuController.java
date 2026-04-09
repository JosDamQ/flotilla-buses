/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author damiangarcia
 */
public class MenuController implements Initializable{
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
    
    // todas las vistas a crear
    
//    public void ventanaClientes() {
//        escenarioPrincipal.ventaClientes();
//    }
}
