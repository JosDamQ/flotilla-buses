/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import flotabuses.enums.Operaciones;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javax.swing.JOptionPane;

/**
 *
 * @author damiangarcia
 */
public class BusController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Operaciones tipoOperacion = Operaciones.NINGUNO;
    
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnReporte;
    @FXML
    private Button btnCSV;
    @FXML
    private ComboBox<?> cmbEstado;
    @FXML
    private ComboBox<?> cmbTipo;
    @FXML
    private TableColumn<?, ?> colCapacidad;
    @FXML
    private TableColumn<?, ?> colCodigoBus;
    @FXML
    private TableColumn<?, ?> colColor;
    @FXML
    private TableColumn<?, ?> colDescripcion;
    @FXML
    private TableColumn<?, ?> colEstado;
    @FXML
    private TableColumn<?, ?> colPlaca;
    @FXML
    private TableColumn<?, ?> colTipo;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgReporte;
    @FXML
    private ImageView imgCSV;
    @FXML
    private TableView<?> tblBuses;
    @FXML
    private TextField txtCapacidad;
    @FXML
    private TextField txtColor;
    @FXML
    private TextField txtDescripcion;
    @FXML
    private TextField txtPlaca;
            
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Aqui vamos a cargar datos
        cargarDatos();
        // Setear los combo box
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
    
    public void cargarDatos() {
        
    }
    
    public void nuevo(){
        switch (tipoOperacion){
            case NINGUNO:
                limpiarControles();
                activarControles();
                tblBuses.getSelectionModel().clearSelection();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                btnCSV.setDisable(true);
                imgNuevo.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Save.png")));
                imgEliminar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Cancel.png")));
                tipoOperacion = Operaciones.GUARDAR;
                break;
             
            case GUARDAR:
                if (txtPlaca.getText().length() <= 0 ||
                    cmbTipo.getValue() == null ||
                    txtCapacidad.getText().length() <= 0 ||
                    txtColor.getText().length() <= 0 ||
                    cmbEstado.getValue() == null ||
                    txtDescripcion.getText().length() <= 0)
                {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Campos vacios");
                    alert.setHeaderText(null);
                    alert.setContentText("Debes ingresar todos los datos");
                    alert.showAndWait();
                } else {
                    // Funcion de Guardar
                    // COLOCAR AQUI
                    limpiarControles();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    btnEditar.setDisable(false);
                    btnReporte.setDisable(false);
                    btnCSV.setDisable(false);
                    imgNuevo.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Agregar.png")));
                    imgEliminar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Quitar.png")));
                    tipoOperacion = Operaciones.NINGUNO;
                    cargarDatos();
                }
                break;
        }
    }
    
    public void eliminar(){
        switch(tipoOperacion) {
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                btnCSV.setDisable(false);
                imgNuevo.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Agregar.png")));
                imgEliminar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Quitar.png")));
                tipoOperacion = Operaciones.NINGUNO;
                cargarDatos();
                break;
            default:
                if(tblBuses.getSelectionModel().getSelectedItem() != null){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Eliminar Bus");
                    alert.setHeaderText(null);
                    alert.setContentText("¿Estas seguro de eliminar el registro?");
                    
                    Optional<ButtonType> resultado = alert.showAndWait();
                    
                    if (resultado.isPresent() && resultado.get() == ButtonType.OK){
                        // Funcion de borrado va a ir aqui
                    } else {
                        desactivarControles();
                        limpiarControles();
                    }
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText(null);
                    alert.setContentText("Debes de seleccionar un dato");
                    alert.showAndWait();
                }
        }
    }
    
    public void editar() {
        System.out.println("Editar");
        switch(tipoOperacion){
            case NINGUNO:
                if(tblBuses.getSelectionModel().getSelectedItem() != null) {
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnCSV.setDisable(true);
                    imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Save.png")));
                    imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Cancel.png")));
                    activarControles();
                    cmbTipo.setDisable(true);
                    cmbEstado.setEditable(true);
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Advertencia");
                    alert.setHeaderText(null);
                    alert.setContentText("Debes de seleccionar un dato");
                    alert.showAndWait();
                }
                break;
            
            case ACTUALIZAR:
                // Funcion para actualizar va aqui
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnCSV.setDisable(false);
                imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Edit.png")));
                imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Reporte.png")));
                desactivarControles();
                limpiarControles();
                cargarDatos();
                tipoOperacion = Operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        System.out.println("Reporte");
        switch(tipoOperacion) {
            case NINGUNO:  
                // FUNCION DE REPORTE VA A IR AQUI

                limpiarControles();
            break;
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnCSV.setDisable(false);
                imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Edit.png")));
                imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Reporte.png")));
                tipoOperacion = Operaciones.NINGUNO;
        }
        
        
    }
    
    public void CSV() {
        System.out.println("CSV");
        
        //FUNCIONALIDAD DE CARGA DE CSV VA A IR AQUI
    }
    
    public void seleccionarElemento(){
        System.out.println("Seleccionar elemento");
        if(tblBuses.getSelectionModel().getSelectedItem() != null) {
            
        }
    }
    
    public void desactivarControles(){
        txtPlaca.setEditable(false);
        cmbTipo.setDisable(true);
        txtCapacidad.setEditable(false);
        txtColor.setEditable(false);
        cmbEstado.setDisable(true);
        txtDescripcion.setEditable(false);
    }
    
    public void activarControles() {
        txtPlaca.setEditable(true);
        cmbTipo.setDisable(false);
        txtCapacidad.setEditable(true);
        txtColor.setEditable(true);
        cmbEstado.setDisable(false);
        txtDescripcion.setEditable(true);
    }
    
    public void limpiarControles() {
        txtPlaca.clear();
        cmbTipo.getSelectionModel().clearSelection();
        cmbTipo.setValue(null);
        txtCapacidad.clear();
        txtColor.clear();
        cmbEstado.getSelectionModel().clearSelection();
        cmbEstado.setValue(null);
        txtDescripcion.clear();
        tblBuses.getSelectionModel().clearSelection();
    }
}
