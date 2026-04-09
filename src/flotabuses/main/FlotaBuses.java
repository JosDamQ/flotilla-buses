/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package flotabuses.main;

/**
 *
 * @author damiangarcia
 */
import java.io.IOException;
import java.io.InputStream;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import flotabuses.controladores.LoginController;
import flotabuses.controladores.MenuController;
import flotabuses.controladores.ClienteController;
import flotabuses.controladores.BusController;
import flotabuses.controladores.DestinoController;
import flotabuses.controladores.AsignacionBusDestinoController;
import flotabuses.controladores.CompraBoletos;

public class FlotaBuses extends Application {
    private final String PAQUETE_VISTA = "/flotabuses/vistas/";
    private Stage escenarioPrincipal;
    private Scene escena;

    @Override
    public void start(Stage escenarioPrincipal) throws Exception{
        this.escenarioPrincipal = escenarioPrincipal;
        this.escenarioPrincipal.setTitle("Flotilla Buses");
        System.out.println(getClass().getResource("/flotabuses/images/Icon.png"));
        escenarioPrincipal.getIcons().add(
            new Image(getClass().getResourceAsStream("/flotabuses/images/Icon.png"))
        );
        
        ventanaLogin();
        escenarioPrincipal.show();
    }
    
    public void ventanaLogin(){
        try{
            LoginController vistaLogin = (LoginController) cambiarEscena("login.fxml", 600,400);
            vistaLogin.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void menuPrincipal(){
        try{
            MenuController vistaMenu = (MenuController) cambiarEscena("menuPrincipal.fxml", 600, 400);
            vistaMenu.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaCliente(){
        try{
            ClienteController vistaCliente = (ClienteController) cambiarEscena("clientesView.fxml", 600, 400);
            vistaCliente.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaBus(){
        try{
            BusController vistaBus = (BusController) cambiarEscena("busesView.fxml", 600, 400);
            vistaBus.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaDestino(){
        try{
            DestinoController vistaDestino = (DestinoController) cambiarEscena("destinosView.fxml", 600, 400);
            vistaDestino.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaAsignacionBusDestino(){
        try{
            AsignacionBusDestinoController vistaAsignacionBusDestinoController = (AsignacionBusDestinoController) cambiarEscena("asignacionBusDestinoView.fxml", 600, 400);
            vistaAsignacionBusDestinoController.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ventanaCompraBoletos(){
        try{
            CompraBoletos vistaCompraBoletos = (CompraBoletos) cambiarEscena("compraBoletosView.fxml", 600, 400);
            vistaCompraBoletos.setEscenarioPrincipal(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
    
    public Initializable cambiarEscena(String fxml, int ancho, int alto) throws Exception{
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = FlotaBuses.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(FlotaBuses.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane)cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
}
