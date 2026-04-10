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
import flotabuses.servicios.BusService;
import flotabuses.enums.EstadoBus;
import flotabuses.enums.TipoBus;
import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Bus;
import javafx.collections.FXCollections;
import javafx.scene.control.cell.PropertyValueFactory;


/**
 *
 * @author damiangarcia
 */
public class BusController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Operaciones tipoOperacion = Operaciones.NINGUNO;
    
    private BusService busService = BusService.getInstance();
    
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
    private ComboBox<EstadoBus> cmbEstado;
    @FXML 
    private ComboBox<TipoBus>   cmbTipo;
    @FXML 
    private TableColumn<Bus, Integer>  colCodigoBus;
    @FXML 
    private TableColumn<Bus, String>   colPlaca;
    @FXML 
    private TableColumn<Bus, String>   colTipo;
    @FXML 
    private TableColumn<Bus, Integer>  colCapacidad;
    @FXML 
    private TableColumn<Bus, String>   colColor;
    @FXML 
    private TableColumn<Bus, String>   colEstado;
    @FXML 
    private TableColumn<Bus, String>   colDescripcion;
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
    private TableView<Bus> tblBuses;
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
        cmbTipo.setItems(FXCollections.observableArrayList(TipoBus.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoBus.values()));
        
        cargarDatos();
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
        tblBuses.setItems(busService.obtenerTodos());
        colCodigoBus.setCellValueFactory(new PropertyValueFactory<>("codigoBus"));
        colPlaca.setCellValueFactory(new PropertyValueFactory<>("placa"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colColor.setCellValueFactory(new PropertyValueFactory<>("color"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
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
                if (txtPlaca.getText().trim().isEmpty() ||
                    cmbTipo.getValue() == null ||
                    txtCapacidad.getText().trim().isEmpty() ||
                    txtColor.getText().trim().isEmpty() ||
                    cmbEstado.getValue() == null ||
                    txtDescripcion.getText().trim().isEmpty()) {
 
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                } 
                
                int capacidad;
                try {
                    capacidad = Integer.parseInt(txtCapacidad.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Capacidad inválida", "La capacidad debe ser un número entero.");
                    return;
                }
 
                // Intentar guardar — el servicio valida placa única y capacidad por tipo
                try {
                    boolean guardado = busService.crear(
                        txtPlaca.getText().trim(),
                        cmbTipo.getValue(),
                        capacidad,
                        txtColor.getText().trim(),
                        cmbEstado.getValue(),
                        txtDescripcion.getText().trim()
                    );
 
                    if (!guardado) {
                        mostrarAlerta(Alert.AlertType.WARNING,
                            "Placa duplicada", "Ya existe un bus con esa placa.");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    // Capacidad fuera del rango permitido para el tipo
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad inválida", e.getMessage());
                    return;
                }
 
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
                Bus seleccionado = tblBuses.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Eliminar Bus");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText(
                        "¿Estás seguro de eliminar el bus con placa " +
                        seleccionado.getPlaca() + "?"
                    );
                    Optional<ButtonType> resultado = confirmacion.showAndWait();
 
                    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                        busService.eliminar(seleccionado.getPlaca());
                        limpiarControles();
                        cargarDatos();
                    } else {
                        desactivarControles();
                        limpiarControles();
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar un bus primero.");
                }
        }
    }
    
    public void editar() {
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
                    txtPlaca.setEditable(false);
                    //cmbEstado.setEditable(true);
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar un bus primero.");
                }
                break;
            
            case ACTUALIZAR:
                
                // Validar campos
                if (txtCapacidad.getText().trim().isEmpty() ||
                    txtColor.getText().trim().isEmpty() ||
                    cmbEstado.getValue() == null ||
                    txtDescripcion.getText().trim().isEmpty()) {
 
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }
 
                int capacidadEdit;
                try {
                    capacidadEdit = Integer.parseInt(txtCapacidad.getText().trim());
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Capacidad inválida", "La capacidad debe ser un número entero.");
                    return;
                }
 
                try {
                    busService.actualizar(
                        txtPlaca.getText().trim(),
                        capacidadEdit,
                        txtColor.getText().trim(),
                        cmbEstado.getValue(),
                        txtDescripcion.getText().trim()
                    );
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad inválida", e.getMessage());
                    return;
                }
                
                tblBuses.refresh();
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
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void seleccionarElemento(){
        Bus seleccionado = tblBuses.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtPlaca.setText(seleccionado.getPlaca());
            cmbTipo.getSelectionModel().select(seleccionado.getTipo());
            txtCapacidad.setText(String.valueOf(seleccionado.getCapacidad()));
            txtColor.setText(seleccionado.getColor());
            cmbEstado.getSelectionModel().select(seleccionado.getEstado());
            txtDescripcion.setText(seleccionado.getDescripcion());
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
