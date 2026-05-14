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
 * Controlador del menu principal de la aplicacion.
 *
 * <p>Gestiona la pantalla {@code menuPrincipal.fxml} y actua como enrutador
 * central hacia todos los modulos del sistema.</p>
 *
 * <p>Al recibir el usuario autenticado mediante {@link #setUsuarioActual(Usuario)},
 * invoca {@link #aplicarPermisosPorRol()} que elimina del menu los items
 * exclusivos del rol {@code ADMIN} si el usuario es {@code OPERADOR}:</p>
 * <ul>
 *   <li>Buses ({@code mnuItemBuses})</li>
 *   <li>Destinos ({@code mnuItemDestinos})</li>
 *   <li>Asignaciones ({@code mnuItemAsignaciones})</li>
 * </ul>
 *
 * <p>Cada metodo de navegacion delega en el metodo correspondiente de
 * {@link FlotaBuses}, que carga el FXML e inyecta el controlador.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.main.FlotaBuses
 * @see flotabuses.modelos.Usuario
 * @see flotabuses.enums.RolUsuario
 */
public class MenuController implements Initializable{

    /** Referencia a la aplicacion principal para la navegacion entre escenas. */
    private FlotaBuses escenarioPrincipal;

    /** Usuario autenticado cuyo rol determina que opciones del menu se muestran. */
    private Usuario usuarioActual;

    /** Menu "Modulos" del que se eliminan items segun el rol del usuario. */
    @FXML private Menu     mnuModulos;

    /** Item de menu "Buses"; se elimina para usuarios con rol OPERADOR. */
    @FXML private MenuItem mnuItemBuses;

    /** Item de menu "Destinos"; se elimina para usuarios con rol OPERADOR. */
    @FXML private MenuItem mnuItemDestinos;

    /** Item de menu "Asignaciones"; se elimina para usuarios con rol OPERADOR. */
    @FXML private MenuItem mnuItemAsignaciones;

    /**
     * Inicializa el controlador. No realiza acciones; la restriccion de permisos
     * se aplica en {@link #setUsuarioActual(Usuario)}.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    /**
     * Retorna la instancia principal de la aplicacion.
     *
     * @return referencia a {@link FlotaBuses}
     */
    public FlotaBuses getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    /**
     * Inyecta la instancia principal de la aplicacion.
     *
     * @param escenarioPrincipal instancia principal de la aplicacion
     */
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }

    /**
     * Retorna el usuario autenticado en la sesion actual.
     *
     * @return usuario con sesion activa
     */
    public Usuario getUsuarioActual() { return usuarioActual; }

    /**
     * Establece el usuario autenticado y aplica los permisos del menu segun su rol.
     *
     * @param usuario usuario autenticado retornado por {@link flotabuses.servicios.LoginService}
     */
    public void setUsuarioActual(Usuario usuario) {
        this.usuarioActual = usuario;
        aplicarPermisosPorRol();
    }

    /**
     * Elimina del menu "Modulos" los items exclusivos del rol {@code ADMIN}
     * si el usuario tiene rol {@code OPERADOR}.
     * Los items eliminados son Buses, Destinos y Asignaciones.
     */
    private void aplicarPermisosPorRol() {
        if (usuarioActual != null && usuarioActual.esOperador()) {
            mnuModulos.getItems().removeAll(
                mnuItemBuses,
                mnuItemDestinos,
                mnuItemAsignaciones
            );
        }
    }

    /** Navega al modulo de gestion de clientes. */
    public void ventanaClientes() {
        escenarioPrincipal.ventanaCliente();
    }

    /** Navega al modulo de gestion de buses. Solo accesible para ADMIN. */
    public void ventanaBus() {
        escenarioPrincipal.ventanaBus();
    }

    /** Navega al modulo de gestion de destinos. Solo accesible para ADMIN. */
    public void ventanaDestino() {
        escenarioPrincipal.ventanaDestino();
    }

    /** Navega al modulo de asignaciones bus-destino. Solo accesible para ADMIN. */
    public void ventanaAsignacionBusDestinoController(){
        escenarioPrincipal.ventanaAsignacionBusDestino();
    }

    /** Navega al modulo de compra de boletos. */
    public void ventanaCompraBoletos(){
        escenarioPrincipal.ventanaCompraBoletos();
    }

    /** Navega a la pantalla de login (inicio de sesion). */
    public void ventanaLogin(){
        escenarioPrincipal.ventanaLogin();
    }

    /**
     * Cierra la sesion del usuario actual y navega a la pantalla de login.
     * Equivalente a {@link #ventanaLogin()}.
     */
    public void cerrarSesion(){
        escenarioPrincipal.ventanaLogin();
    }

    /** Navega a la pantalla "Acerca de" con informacion del sistema. */
    public void ventanaAbout(){
        escenarioPrincipal.ventanaAbout();
    }

    /** Navega al visualizador grafico de estructuras de datos. */
    public void ventanaVisualizador(){
        escenarioPrincipal.ventanaVisualizador();
    }
}
