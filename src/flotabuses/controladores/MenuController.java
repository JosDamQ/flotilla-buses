/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Usuario;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 *
 * @author damiangarcia
 */
public class MenuController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Usuario usuarioActual;

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
