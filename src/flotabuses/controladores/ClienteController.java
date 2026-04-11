/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

/**
 *
 * @author damiangarcia
 */
public class ClienteController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aqui vamos a cargar datos
    }
    
    public FlotaBuses getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void nuevo() {
        
    }
    
    public void eliminar() {
        
    }
    
    public void editar() {
        
    }
    
    public void reporte(){
        
    }
    
    public void CSV(){
        
    }
    
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void seleccionarElemento(){
        
    }
    
    public void desactivarControles(){
        
    }
    
    public void activarControles(){
        
    }
    
    public void limpiarControles(){
        
    }
}
