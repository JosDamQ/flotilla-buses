/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.enums.Operaciones;
import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Bus;
import flotabuses.modelos.Destino;
import flotabuses.servicios.AsignacionBusDestinoService;
import flotabuses.servicios.AsignacionBusDestinoService.FilaAsignacion;
import flotabuses.servicios.BusService;
import flotabuses.servicios.DestinoService;
import flotabuses.servicios.ReporteService;
import flotabuses.estructuras.NodoCabecera;
import flotabuses.estructuras.NodoMatriz;
import flotabuses.modelos.AsignacionBusDestino;
import flotabuses.estructuras.NodoLista;
import java.net.URL;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author damiangarcia
 */
public class AsignacionBusDestinoController implements Initializable{
    private FlotaBuses escenarioPrincipal;
    private Operaciones tipoOperacion = Operaciones.NINGUNO;
    
    private AsignacionBusDestinoService asignacionServicio = AsignacionBusDestinoService.getInstance();
    private DestinoService destinoServicio = DestinoService.getInstance();
    private BusService busServicio = BusService.getInstance();
    
    private FilaAsignacion filaEnEdicion;
    private LocalTime horaOriginal;
    
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    @FXML private Button btnCSV;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;
    @FXML private ImageView imgEditar;
    @FXML private ImageView imgReporte;
    @FXML private ImageView imgCSV;
 
    @FXML private ComboBox<Destino> cmbDestino;
    @FXML private ComboBox<Bus> cmbBus;
    @FXML private ComboBox<Integer> cmbHora;
    @FXML private ComboBox<Integer> cmbMinutos;
 
    @FXML private TableView<FilaAsignacion> tblAsignaciones;
    @FXML private TableColumn<FilaAsignacion, Integer> colCodAsignacion;
    @FXML private TableColumn<FilaAsignacion, String> colNombreDestino;
    @FXML private TableColumn<FilaAsignacion, String> colFechaSalida;
    @FXML private TableColumn<FilaAsignacion, String> colPlacaBus;
    @FXML private TableColumn<FilaAsignacion, String> colTipo;
    @FXML private TableColumn<FilaAsignacion, Integer> colCapacidad;
    @FXML private TableColumn<FilaAsignacion, String> colHora;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbDestino.setItems(destinoServicio.obtenerConfirmados());
        cmbBus.setItems(busServicio.obtenerTodos()
            .filtered(b -> b.estaDisponible()));
        
        javafx.collections.ObservableList<Integer> horas =
            FXCollections.observableArrayList();
        for (int i = 4; i <= 23; i++) horas.add(i);
        cmbHora.setItems(horas);
        
        cmbMinutos.setItems(FXCollections.observableArrayList(0, 15, 30, 45));
        
        cargarDatos();
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
        tblAsignaciones.setItems(asignacionServicio.obtenerTodos());
        colCodAsignacion.setCellValueFactory(new PropertyValueFactory<>("codAsignacion"));
        colNombreDestino.setCellValueFactory(new PropertyValueFactory<>("nombreDestino"));
        colFechaSalida.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        colPlacaBus.setCellValueFactory(new PropertyValueFactory<>("placaBus"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCapacidad.setCellValueFactory(new PropertyValueFactory<>("capacidad"));
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
    }
    
    public void nuevo() {
        switch (tipoOperacion) {
            case NINGUNO:
                limpiarControles();
                activarControles();
                tblAsignaciones.getSelectionModel().clearSelection();
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
                // Validar campos
                if (cmbDestino.getValue() == null ||
                    cmbBus.getValue() == null ||
                    cmbHora.getValue() == null ||
                    cmbMinutos.getValue() == null) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes seleccionar destino, bus, hora y minutos.");
                    return;
                }
 
                LocalTime hora = LocalTime.of(cmbHora.getValue(), cmbMinutos.getValue());
 
                int resultado = asignacionServicio.guardar(
                    cmbDestino.getValue(),
                    cmbBus.getValue(),
                    hora
                );
 
                if (resultado == 1) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Destino no confirmado",
                        "Solo se pueden asignar buses a destinos confirmados.");
                    return;
                }
                if (resultado == 2) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Hora inválida",
                        "La hora debe ser desde las 04:00 y con al menos 1 hora " +
                        "de diferencia respecto a las horas ya asignadas para este bus y destino.");
                    return;
                }
                if (resultado == 3) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Bus no disponible",
                        "El bus ya tiene una asignación a esa hora en otro destino.");
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
                // Cancelar
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
                FilaAsignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Eliminar Asignación");
                    confirmacion.setHeaderText(null);
                    confirmacion.setContentText(
                        "¿Estás seguro de eliminar la asignación de " +
                        seleccionada.getNombreDestino() + " - Bus " +
                        seleccionada.getPlacaBus() + " a las " +
                        seleccionada.getHora() + "?"
                    );
                    Optional<ButtonType> res = confirmacion.showAndWait();
 
                    if (res.isPresent() && res.get() == ButtonType.OK) {
                        // Obtener destino y bus desde los servicios
                        Destino destino = destinoServicio.buscarPorNombre(
                            cmbDestino.getValue() != null
                                ? cmbDestino.getValue().getNombre()
                                : buscarNombreDestino(seleccionada.getNombreDestino())
                        );
                        Bus bus = busServicio.buscarPorPlaca(seleccionada.getPlacaBus());
 
                        String[] partes = seleccionada.getHora().split(":");
                        LocalTime hora = LocalTime.of(
                            Integer.parseInt(partes[0]),
                            Integer.parseInt(partes[1])
                        );
 
                        asignacionServicio.eliminarHora(destino, bus, hora);
                        limpiarControles();
                        cargarDatos();
                    } else {
                        desactivarControles();
                        limpiarControles();
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar una asignación primero.");
                }
        }
    }
    
    public void editar() {
        switch (tipoOperacion) {
            case NINGUNO:
                FilaAsignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();
                if (seleccionada != null) {
                    filaEnEdicion = seleccionada;
                    // Guardar hora original para eliminarla al actualizar
                    String[] partes = seleccionada.getHora().split(":");
                    horaOriginal = LocalTime.of(
                        Integer.parseInt(partes[0]),
                        Integer.parseInt(partes[1])
                    );
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    btnCSV.setDisable(true);
                    imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Save.png")));
                    imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Cancel.png")));
                    activarControles();
                    // Destino y bus no se pueden cambiar al editar
                    cmbDestino.setDisable(true);
                    cmbBus.setDisable(true);
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar una asignación primero.");
                }
                break;
 
            case ACTUALIZAR:
                if (cmbHora.getValue() == null || cmbMinutos.getValue() == null) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes seleccionar hora y minutos.");
                    return;
                }
 
                LocalTime horaNew = LocalTime.of(cmbHora.getValue(), cmbMinutos.getValue());
 
                // Obtener la asignación desde la matriz
                Destino destino = destinoServicio.buscarPorNombre(
                    buscarNombreDestino(filaEnEdicion.getNombreDestino())
                );
                Bus bus = busServicio.buscarPorPlaca(filaEnEdicion.getPlacaBus());
 
                // Eliminar hora original y agregar la nueva
                asignacionServicio.eliminarHora(destino, bus, horaOriginal);
                int resEdit = asignacionServicio.guardar(destino, bus, horaNew);
 
                if (resEdit == 2) {
                    // Si la nueva hora es inválida, restaurar la original
                    asignacionServicio.guardar(destino, bus, horaOriginal);
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Hora inválida",
                        "La hora debe ser desde las 04:00 y con al menos 1 hora " +
                        "de diferencia respecto a las otras horas asignadas.");
                    return;
                }
 
                tblAsignaciones.refresh();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnCSV.setDisable(false);
                imgEditar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Edit.png")));
                imgReporte.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Reporte.png")));
                desactivarControles();
                limpiarControles();
                filaEnEdicion = null;
                horaOriginal  = null;
                cargarDatos();
                tipoOperacion = Operaciones.NINGUNO;
                break;
        }
    }
    
    public void reporte(){
        switch (tipoOperacion) {
            case NINGUNO:
                ReporteService.getInstance().reporteAsignaciones();
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
                filaEnEdicion = null;
                horaOriginal  = null;
                tipoOperacion = Operaciones.NINGUNO;
                break;
        }
    }
    
    public void CSV(){
        Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
        dialogo.setTitle("CSV - Asignaciones");
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
        chooser.setTitle("Importar Asignaciones CSV");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        int importados = 0, errores = 0;
        try (BufferedReader br = new BufferedReader(
                new FileReader(archivo, StandardCharsets.UTF_8))) {
            String linea = br.readLine(); // saltar encabezado
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                // Código_Asig,Código_Destino,Nombre_Destino,Fecha_salida_destino,
                // Placa_bus,Tipo_bus,Capacidad_bus,Hora_asignación
                String[] p = linea.split(",", -1);
                if (p.length < 8) { errores++; continue; }
                try {
                    flotabuses.enums.NombreDestino nombreDestino = null;
                    for (flotabuses.enums.NombreDestino nd :
                            flotabuses.enums.NombreDestino.values()) {
                        if (nd.getNombreMostrar().equalsIgnoreCase(p[2].trim())) {
                            nombreDestino = nd; break;
                        }
                    }
                    if (nombreDestino == null) { errores++; continue; }
                    Destino destino = destinoServicio.buscarPorNombre(nombreDestino);
                    if (destino == null) { errores++; continue; }
                    Bus bus = busServicio.buscarPorPlaca(p[4].trim());
                    if (bus == null) { errores++; continue; }
                    LocalTime hora = LocalTime.parse(p[7].trim());
                    int res = asignacionServicio.guardar(destino, bus, hora);
                    if (res == 0) importados++; else errores++;
                } catch (Exception e) { errores++; }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                "No se pudo leer el archivo: " + e.getMessage());
            return;
        }
        cargarDatos();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importación completa",
            "Importadas: " + importados + "  |  Errores/omitidos: " + errores);
    }

    private void exportarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Asignaciones CSV");
        chooser.setInitialFileName("AsigBusDestino.csv");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            pw.println("Código_Asig,Código_Destino,Nombre_Destino,Fecha_salida_destino," +
                       "Placa_bus,Tipo_bus,Capacidad_bus,Hora_asignación");
            NodoCabecera fila = asignacionServicio.getMatriz().getCabFilas();
            while (fila != null) {
                NodoMatriz celda = fila.primero;
                while (celda != null) {
                    AsignacionBusDestino asig = (AsignacionBusDestino) celda.dato;
                    NodoLista nodoHora = asig.getHorasDisponibles().getCabeza();
                    while (nodoHora != null) {
                        LocalTime hora = (LocalTime) nodoHora.dato;
                        pw.printf("%d,%d,%s,%s,%s,%s,%d,%s%n",
                            asig.getCodigoAsignacion(),
                            asig.getDestino().getCodigoDestino(),
                            asig.getDestino().getNombre().getNombreMostrar(),
                            asig.getDestino().getFechaSalida().toString(),
                            asig.getBus().getPlaca(),
                            asig.getBus().getTipo().toString(),
                            asig.getBus().getCapacidad(),
                            hora.toString());
                        nodoHora = nodoHora.siguiente;
                    }
                    celda = celda.derecha;
                }
                fila = fila.siguiente;
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completa",
                "Archivo guardado correctamente.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                "No se pudo guardar: " + e.getMessage());
        }
    }
    
    private flotabuses.enums.NombreDestino buscarNombreDestino(String nombreMostrar) {
        for (flotabuses.enums.NombreDestino nd : flotabuses.enums.NombreDestino.values()) {
            if (nd.getNombreMostrar().equals(nombreMostrar)) return nd;
        }
        return null;
    }
    
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void seleccionarElemento(){
        FilaAsignacion seleccionada = tblAsignaciones.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            // Seleccionar el destino en el combobox
            for (Destino d : cmbDestino.getItems()) {
                if (d.getNombre().getNombreMostrar().equals(seleccionada.getNombreDestino())) {
                    cmbDestino.getSelectionModel().select(d);
                    break;
                }
            }
            // Seleccionar el bus en el combobox
            for (Bus b : cmbBus.getItems()) {
                if (b.getPlaca().equals(seleccionada.getPlacaBus())) {
                    cmbBus.getSelectionModel().select(b);
                    break;
                }
            }
            // Parsear y seleccionar la hora
            String[] partes = seleccionada.getHora().split(":");
            cmbHora.getSelectionModel().select(Integer.valueOf(partes[0]));
            cmbMinutos.getSelectionModel().select(Integer.valueOf(partes[1]));
        }
    }
    
    public void desactivarControles(){
        cmbDestino.setDisable(true);
        cmbBus.setDisable(true);
        cmbHora.setDisable(true);
        cmbMinutos.setDisable(true);
    }
    
    public void activarControles(){
        cmbDestino.setDisable(false);
        cmbBus.setDisable(false);
        cmbHora.setDisable(false);
        cmbMinutos.setDisable(false);
    }
    
    public void limpiarControles(){
        cmbDestino.getSelectionModel().clearSelection();
        cmbDestino.setValue(null);
        cmbBus.getSelectionModel().clearSelection();
        cmbBus.setValue(null);
        cmbHora.getSelectionModel().clearSelection();
        cmbHora.setValue(null);
        cmbMinutos.getSelectionModel().clearSelection();
        cmbMinutos.setValue(null);
        tblAsignaciones.getSelectionModel().clearSelection();
    }
}
