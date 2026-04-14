/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

import flotabuses.enums.Operaciones;
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import flotabuses.servicios.DestinoService;
import flotabuses.servicios.ReporteService;
import flotabuses.enums.NombreDestino;
import flotabuses.enums.EstadoDestino;
import flotabuses.modelos.Destino;
import flotabuses.estructuras.NodoLista;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author damiangarcia
 */
public class DestinoController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Operaciones tipoOperacion = Operaciones.NINGUNO;
    
    private DestinoService destinoService = DestinoService.getInstance();
            
    @FXML
    private Button btnCSV;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnReporte;
    @FXML
    private ComboBox<EstadoDestino> cmbEstado;
    @FXML
    private ComboBox<NombreDestino> cmbNombre;
    @FXML
    private TableColumn<Destino, Integer> colCodigoDestino;
    @FXML
    private TableColumn<Destino, Double> colCostoBoleto;
    @FXML
    private TableColumn<Destino, String> colDescripcion;
    @FXML
    private TableColumn<Destino, String> colEstado;
    @FXML
    private TableColumn<Destino, LocalDate> colFechaSalida;
    @FXML
    private TableColumn<Destino, String> colNombre;
    @FXML
    private DatePicker dtpFechaSalida;
    @FXML
    private ImageView imgCSV;
    @FXML
    private ImageView imgEditar;
    @FXML
    private ImageView imgEliminar;
    @FXML
    private ImageView imgNuevo;
    @FXML
    private ImageView imgReporte;
    @FXML
    private TableView<Destino> tblDestinos;
    @FXML
    private TextField txtCodigoDestino;
    @FXML
    private TextField txtCostoBoleto;
    @FXML
    private TextField txtDescripcion;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbNombre.setItems(FXCollections.observableArrayList(NombreDestino.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoDestino.values()));
        
        cargarDatos();
        
        //txtCodigoDestino.setEditable(false);
    }
    
    public FlotaBuses getEscenarioPrincipal() {
        return escenarioPrincipal;
    }
    
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        flotabuses.modelos.Usuario u = escenarioPrincipal.getUsuarioActual();
        if (u != null && u.getRol() == flotabuses.enums.RolUsuario.OPERADOR) {
            btnCSV.setVisible(false);
            btnCSV.setManaged(false);
        }
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    public void cargarDatos(){
        tblDestinos.setItems(destinoService.obtenerTodos());
        colCodigoDestino.setCellValueFactory(new PropertyValueFactory<>("codigoDestino"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaSalida.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        colCostoBoleto.setCellValueFactory(new PropertyValueFactory<>("costoBoleto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }
    
    public void nuevo(){
        switch (tipoOperacion) {
            case NINGUNO:
                limpiarControles();
                activarControles();
                tblDestinos.getSelectionModel().clearSelection();
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
                if (/*txtCodigoDestino.getText().trim().isEmpty() ||*/
                    cmbNombre.getValue() == null ||
                    dtpFechaSalida.getValue() == null ||
                    txtCostoBoleto.getText().trim().isEmpty() ||
                    cmbEstado.getValue() == null ||
                    txtDescripcion.getText().trim().isEmpty()) {
                    
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }
                
                double costo;
                
                try {
                    costo = Double.parseDouble(txtCostoBoleto.getText().trim());
                    if (costo <= 0) throw new NumberFormatException();
                } catch(NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Costo inválido", "El costo debe ser un número mayor a 0.");
                    return;
                }
                
                try {
                    boolean guardado = destinoService.crear(
                        cmbNombre.getValue(),
                        dtpFechaSalida.getValue(),
                        costo,
                        cmbEstado.getValue(),
                        txtDescripcion.getText().trim()
                    );
                    
                    if (!guardado) {
                        mostrarAlerta(Alert.AlertType.WARNING,
                            "Destino duplicado", "Ya existe un destino con ese nombre.");
                        return;
                    }
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error al guardado", e.getMessage());
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
                Destino seleccionado = tblDestinos.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Eliminar Destino");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText(
                        "¿Estás seguro de eliminar el destino " +
                        seleccionado.getNombre() + "?"
                    );
                    Optional<ButtonType> resultado = confirmacion.showAndWait();
 
                    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                        destinoService.eliminar(seleccionado.getNombre());
                        limpiarControles();
                        cargarDatos();
                    } else {
                        desactivarControles();
                        limpiarControles();
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar un destino primero.");
                }
        }
    }
    
    public void editar() {
        switch(tipoOperacion) {
            case NINGUNO:
                if(tblDestinos.getSelectionModel().getSelectedItem() != null) {
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnCSV.setDisable(true);
                    imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Save.png")));
                    imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Cancel.png")));
                    activarControles();
                    //txtCodigoDestino.setEditable(false);
                    cmbNombre.setDisable(true);
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                            "Advertencia", "Debes seleccionar un destino primero.");
                }
                break;
                
            case ACTUALIZAR:
                if (/*txtCodigoDestino.getText().trim().isEmpty() ||*/
                    cmbNombre.getValue() == null ||
                    dtpFechaSalida.getValue() == null ||
                    txtCostoBoleto.getText().trim().isEmpty() ||
                    cmbEstado.getValue() == null ||
                    txtDescripcion.getText().trim().isEmpty()) {
                    
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }
                
                double costoEdit;
                try {
                    costoEdit = Double.parseDouble(txtCostoBoleto.getText().trim());
                    if (costoEdit <= 0) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Costo inválido", "El costo debe ser un número mayor a 0.");
                    return;
                }
                
                try {
                    destinoService.actualizar(
                        cmbNombre.getValue(),
                        dtpFechaSalida.getValue(),
                        costoEdit,
                        cmbEstado.getValue(),
                        txtDescripcion.getText().trim()
                    );
                    
                } catch (IllegalArgumentException e) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Error a actualizar", e.getMessage());
                    return;
                }
                
                // Llamado del service para editar
                
                tblDestinos.refresh();
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
    
    public void reporte() {
        switch(tipoOperacion) {
            case NINGUNO:
                Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
                dialogo.setTitle("Reporte de Destinos");
                dialogo.setHeaderText("¿En qué orden deseas el reporte?");
                ButtonType btnAsc  = new ButtonType("Ascendente");
                ButtonType btnDesc = new ButtonType("Descendente");
                ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                dialogo.getButtonTypes().setAll(btnAsc, btnDesc, btnCancelar);
                Optional<ButtonType> resp = dialogo.showAndWait();
                if (resp.isPresent() && resp.get() != btnCancelar) {
                    boolean ascendente = resp.get() == btnAsc;
                    ReporteService.getInstance().reporteDestinos(ascendente);
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
        }
    }
    
    public void CSV() {
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
        dialogo.setTitle("CSV - Destinos");
        dialogo.setHeaderText("¿Qué acción deseas realizar?");
        ButtonType btnImportar = new ButtonType("Importar");
        ButtonType btnExportar = new ButtonType("Exportar");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialogo.getButtonTypes().setAll(btnImportar, btnExportar, btnCancelar);
        Optional<ButtonType> resp = dialogo.showAndWait();
        if (resp.isPresent()) {
            if (resp.get() == btnImportar) importarCSV();
            else if (resp.get() == btnExportar) exportarCSV();
        }
    }

    private void importarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar Destinos CSV");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        int importados = 0, errores = 0;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (BufferedReader br = new BufferedReader(
                new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // saltar encabezado
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split(",", -1);
                if (p.length < 6) { errores++; continue; }
                try {
                    NombreDestino nombre = null;
                    for (NombreDestino nd : NombreDestino.values()) {
                        if (nd.getNombreMostrar().equalsIgnoreCase(p[1].trim())) {
                            nombre = nd; break;
                        }
                    }
                    if (nombre == null) { errores++; continue; }
                    LocalDate fecha = LocalDate.parse(p[2].trim(), fmt);
                    double costo    = Double.parseDouble(p[3].trim());
                    EstadoDestino estado = EstadoDestino.valueOf(p[4].trim().toUpperCase());
                    String desc   = p[5].trim();
                    boolean ok = destinoService.crear(nombre, fecha, costo, estado, desc);
                    if (ok) importados++; else errores++;
                } catch (Exception e) { errores++; }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo leer el archivo: " + e.getMessage());
            return;
        }
        cargarDatos();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importación completa",
            "Importados: " + importados + "  |  Errores/omitidos: " + errores);
    }

    private void exportarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Destinos CSV");
        chooser.setInitialFileName("Destinos.csv");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            pw.println("Código,Nombre_Destino,Fecha_salida,Costo_persona,Estado,Descripción");
            NodoLista actual = destinoService.getLista().getCabeza();
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            while (actual != null) {
                Destino d = (Destino) actual.dato;
                pw.printf("%d,%s,%s,%.2f,%s,%s%n",
                    d.getCodigoDestino(),
                    d.getNombre().getNombreMostrar(),
                    d.getFechaSalida().format(fmt),
                    d.getCostoBoleto(),
                    d.getEstado().name(),
                    d.getDescripcion());
                actual = actual.siguiente;
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completa",
                "Archivo guardado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar: " + e.getMessage());
        }
    }
    
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void seleccionarElemento() {
        Destino seleccionado = tblDestinos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            //txtCodigoDestino.setText(String.valueOf(seleccionado.getCodigoDestino()));
            cmbNombre.getSelectionModel().select(seleccionado.getNombre());
            dtpFechaSalida.setValue(seleccionado.getFechaSalida());
            txtCostoBoleto.setText(String.valueOf(seleccionado.getCostoBoleto()));
            cmbEstado.getSelectionModel().select(seleccionado.getEstado());
            txtDescripcion.setText(seleccionado.getDescripcion());
        }
    }
    
    public void desactivarControles() {
        //txtCodigoDestino.setEditable(false);
        cmbNombre.setDisable(true);
        dtpFechaSalida.setDisable(true);
        txtCostoBoleto.setEditable(false);
        cmbEstado.setDisable(true);
        txtDescripcion.setEditable(false);
    }
    
    public void activarControles() {
        //txtCodigoDestino.setEditable(true);
        cmbNombre.setDisable(false);
        dtpFechaSalida.setDisable(false);
        txtCostoBoleto.setEditable(true);
        cmbEstado.setDisable(false);
        txtDescripcion.setEditable(true);
    }
    
    public void limpiarControles() {
        //txtCodigoDestino.clear();
        cmbNombre.getSelectionModel().clearSelection();
        cmbNombre.setValue(null);
        dtpFechaSalida.setValue(null);
        txtCostoBoleto.clear();
        cmbEstado.getSelectionModel().clearSelection();
        cmbEstado.setValue(null);
        txtDescripcion.clear();
        tblDestinos.getSelectionModel().clearSelection();
    }
}
