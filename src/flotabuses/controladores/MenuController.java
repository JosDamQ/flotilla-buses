/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Usuario;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 *
 * @author damiangarcia
 */
public class MenuController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Usuario usuarioActual;

    @FXML private Menu     mnuModulos;
    @FXML private MenuItem mnuItemBuses;
    @FXML private MenuItem mnuItemDestinos;
    @FXML private MenuItem mnuItemAsignaciones;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public FlotaBuses getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    public Usuario getUsuarioActual() { return usuarioActual; }

    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        aplicarPermisosPorRol();
    }

    private void aplicarPermisosPorRol() {
        if (usuarioActual != null && usuarioActual.esOperador()) {
            mnuModulos.getItems().removeAll(
                mnuItemBuses,
                mnuItemDestinos,
                mnuItemAsignaciones
            );
        }
    }
    
    // todas las vistas a crear
    
    public void ventanaClientes() {
        escenarioPrincipal.ventanaCliente();
    }
    
    public void ventanaBus() {
        escenarioPrincipal.ventanaBus();
    }
    
    public void ventanaDestino() {
        escenarioPrincipal.ventanaDestino();
    }
    
    public void ventanaAsignacionBusDestinoController(){
        escenarioPrincipal.ventanaAsignacionBusDestino();
    }
    
    public void ventanaCompraBoletos(){
        escenarioPrincipal.ventanaCompraBoletos();
    }
    
    public void ventanaLogin(){
        escenarioPrincipal.ventanaLogin();
    }
}
