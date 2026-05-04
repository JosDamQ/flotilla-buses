/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.enums.Operaciones;
import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Cliente;
import flotabuses.servicios.ClienteService;
import flotabuses.servicios.ReporteService;
import flotabuses.estructuras.NodoLista;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

/**
 *
 * @author damiangarcia
 */
public class ClienteController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Operaciones   tipoOperacion = Operaciones.NINGUNO;
    
    private ClienteService clienteServicio = ClienteService.getInstance();
    
    @FXML private Button  btnNuevo;
    @FXML private Button  btnEliminar;
    @FXML private Button  btnEditar;
    @FXML private Button  btnReporte;
    @FXML private Button  btnCSV;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private ImageView imgCSV;
    @FXML private TableView<Cliente>            tblClientes;
    @FXML private TableColumn<Cliente, Integer> colCodigoCliente;
    @FXML private TableColumn<Cliente, String>  colNombre;
    @FXML private TableColumn<Cliente, String>  colApellido;
    @FXML private TableColumn<Cliente, String>  colDPI;
    @FXML private TableColumn<Cliente, String>  colEmail;
    @FXML private TableColumn<Cliente, String>  colPassword;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDPI;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public FlotaBuses getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        if (escenarioPrincipal.getUsuarioActual() != null
                && escenarioPrincipal.getUsuarioActual().esOperador()) {
            btnCSV.setVisible(false);
            btnCSV.setManaged(false);
        }
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void cargarDatos(){
        tblClientes.setItems(clienteServicio.obtenerTodosAscendente());
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<>("codigoCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDPI.setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
    }
    
    public void nuevo() {
        switch (tipoOperacion) {
            case NINGUNO:
                limpiarControles();
                activarControles();
                tblClientes.getSelectionModel().clearSelection();
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
                if (txtNombre.getText().trim().isEmpty() ||
                    txtApellido.getText().trim().isEmpty() ||
                    txtDPI.getText().trim().isEmpty() ||
                    txtEmail.getText().trim().isEmpty() ||
                    txtPassword.getText().trim().isEmpty()) {
 
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }
 
                int resultado = clienteServicio.crear(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDPI.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPassword.getText().trim()
                );
 
                if (resultado == 1) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "DPI duplicado", "Ya existe un cliente con ese DPI.");
                    return;
                }
                if (resultado == 2) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Email duplicado", "Ya existe un cliente con ese email.");
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
    
    public void eliminar() {
        switch (tipoOperacion) {
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
                Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Eliminar Cliente");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText(
                        "¿Estás seguro de eliminar al cliente " +
                        seleccionado.getNombreCompleto() + "?"
                    );
                    Optional<ButtonType> res = confirmacion.showAndWait();
 
                    if (res.isPresent() && res.get() == ButtonType.OK) {
                        clienteServicio.eliminar(seleccionado.getCodigoCliente());
                        limpiarControles();
                        cargarDatos();
                    } else {
                        desactivarControles();
                        limpiarControles();
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar un cliente primero.");
                }
        }
    }
    
    public void editar() {
        switch (tipoOperacion) {
            case NINGUNO:
                if (tblClientes.getSelectionModel().getSelectedItem() != null) {
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnCSV.setDisable(true);
                    imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Save.png")));
                    imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Cancel.png")));
                    activarControles();
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar un cliente primero.");
                }
                break;
 
            case ACTUALIZAR:
                if (txtNombre.getText().trim().isEmpty() ||
                    txtApellido.getText().trim().isEmpty() ||
                    txtDPI.getText().trim().isEmpty() ||
                    txtEmail.getText().trim().isEmpty() ||
                    txtPassword.getText().trim().isEmpty()) {
 
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }
 
                Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
 
                int resActualizar = clienteServicio.actualizar(
                    seleccionado.getCodigoCliente(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDPI.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPassword.getText().trim()
                );
 
                if (resActualizar == 2) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "DPI duplicado", "Ya existe otro cliente con ese DPI.");
                    return;
                }
                if (resActualizar == 3) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Email duplicado", "Ya existe otro cliente con ese email.");
                    return;
                }
 
                tblClientes.refresh();
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
        switch (tipoOperacion) {
            case NINGUNO:
                ButtonType btnAsc  = new ButtonType("Ascendente");
                ButtonType btnDesc = new ButtonType("Descendente");
                ButtonType btnCan  = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Reporte de Clientes");
                alerta.setHeaderText(null);
                alerta.setContentText("Seleccione el orden por código:");
                alerta.getButtonTypes().setAll(btnAsc, btnDesc, btnCan);
                Optional<ButtonType> res = alerta.showAndWait();
                if (res.isPresent()) {
                    if (res.get() == btnAsc)  ReporteService.getInstance().reporteClientes(true);
                    else if (res.get() == btnDesc) ReporteService.getInstance().reporteClientes(false);
                }
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
                break;
        }
    }

    public void CSV() {
        ButtonType btnImp = new ButtonType("Importar");
        ButtonType btnExp = new ButtonType("Exportar");
        ButtonType btnCan = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("CSV — Clientes");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Qué deseas hacer?");
        alerta.getButtonTypes().setAll(btnImp, btnExp, btnCan);
        Optional<ButtonType> res = alerta.showAndWait();
        if (!res.isPresent()) return;
        if (res.get() == btnImp) importarCSV();
        else if (res.get() == btnExp) exportarCSV();
    }

    private void importarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Clientes.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        int ok = 0, err = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            br.readLine(); // saltar encabezado
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",", -1);
                if (p.length < 5) { err++; continue; }
                try {
                    // Formato: Código, Nombre, Identificación, Contraseña, Correo
                    String[] nombre = p[1].trim().split(" ", 2);
                    String n = nombre[0];
                    String a = nombre.length > 1 ? nombre[1] : "";
                    String dpi      = p[2].trim();
                    String password = p[3].trim();
                    String email    = p[4].trim();
                    if (clienteServicio.crear(n, a, dpi, email, password) == 0) ok++;
                    else err++;
                } catch (Exception e) { err++; }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error leyendo archivo: " + e.getMessage());
            return;
        }
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importación completa",
            "Importados: " + ok + " | Errores: " + err);
        cargarDatos();
    }

    private void exportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Clientes.csv");
        fc.setInitialFileName("Clientes.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            pw.println("Código,Nombre,Identificación,Contraseña,Correo");
            for (Cliente c : clienteServicio.obtenerTodosAscendente()) {
                pw.println(c.getCodigoCliente() + "," + c.getNombreCompleto()
                    + "," + c.getDpi() + "," + c.getPassword() + "," + c.getEmail());
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completa",
                "Archivo guardado: " + archivo.getName());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error escribiendo archivo: " + e.getMessage());
        }
    }
    
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void seleccionarElemento(){
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtNombre.setText(seleccionado.getNombre());
            txtApellido.setText(seleccionado.getApellido());
            txtDPI.setText(seleccionado.getDpi());
            txtEmail.setText(seleccionado.getEmail());
            txtPassword.setText(seleccionado.getPassword());
        }
    }
    
    public void desactivarControles(){
        txtNombre.setEditable(false);
        txtApellido.setEditable(false);
        txtDPI.setEditable(false);
        txtEmail.setEditable(false);
        txtPassword.setEditable(false);
    }
    
    public void activarControles(){
        txtNombre.setEditable(true);
        txtApellido.setEditable(true);
        txtDPI.setEditable(true);
        txtEmail.setEditable(true);
        txtPassword.setEditable(true);
    }
    
    public void limpiarControles(){
        txtNombre.clear();
        txtApellido.clear();
        txtDPI.clear();
        txtEmail.clear();
        txtPassword.clear();
        tblClientes.getSelectionModel().clearSelection();
    }
}
