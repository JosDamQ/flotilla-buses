package flotabuses.controladores;

import flotabuses.enums.Operaciones;
import flotabuses.main.FlotaBuses;
import flotabuses.modelos.AsignacionBusDestino;
import flotabuses.modelos.Boleto;
import flotabuses.modelos.Cliente;
import flotabuses.servicios.AsignacionBusDestinoService;
import flotabuses.servicios.BoletoService;
import flotabuses.servicios.ClienteService;
import flotabuses.servicios.ReporteService;
import flotabuses.estructuras.NodoCabecera;
import flotabuses.estructuras.NodoLista;
import flotabuses.estructuras.NodoMatriz;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.StringConverter;

/**
 * Controlador del módulo de compra de boletos (M5).
 * Permite crear y eliminar boletos vinculando cliente, asignación y hora.
 */
public class CompraBoletos implements Initializable {

    private FlotaBuses escenarioPrincipal;
    private Operaciones tipoOperacion = Operaciones.NINGUNO;

    private final BoletoService               boletoServicio     = BoletoService.getInstance();
    private final AsignacionBusDestinoService asignacionServicio = AsignacionBusDestinoService.getInstance();
    private final ClienteService              clienteServicio    = ClienteService.getInstance();

    @FXML private Button    btnNuevo;
    @FXML private Button    btnEliminar;
    @FXML private Button    btnReporte;
    @FXML private Button    btnCSV;
    @FXML private ImageView imgNuevo;
    @FXML private ImageView imgEliminar;

    @FXML private ComboBox<AsignacionBusDestino> cmbAsignacion;
    @FXML private ComboBox<String>               cmbHora;
    @FXML private ComboBox<Cliente>              cmbCliente;

    @FXML private TableView<Boleto>                      tblBoletos;
    @FXML private TableColumn<Boleto, Integer>           colCodBoleto;
    @FXML private TableColumn<Boleto, String>            colCliente;
    @FXML private TableColumn<Boleto, String>            colDestino;
    @FXML private TableColumn<Boleto, String>            colFechaSalida;
    @FXML private TableColumn<Boleto, String>            colBus;
    @FXML private TableColumn<Boleto, String>            colTipoBus;
    @FXML private TableColumn<Boleto, String>            colHora;
    @FXML private TableColumn<Boleto, Double>            colCosto;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configurarComboBoxes();
        cargarDatos();
    }

    public FlotaBuses getEscenarioPrincipal() { return escenarioPrincipal; }

    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        // Ocultar botón CSV para Operadores
        if (escenarioPrincipal.getUsuarioActual() != null
                && escenarioPrincipal.getUsuarioActual().esOperador()) {
            btnCSV.setVisible(false);
            btnCSV.setManaged(false);
        }
    }

    public void menuPrincipal() {
        escenarioPrincipal.menuPrincipal();
    }

    // =========================================================
    // CONFIGURACIÓN INICIAL
    // =========================================================

    private void configurarComboBoxes() {
        // AsignacionBusDestino: mostrar "Destino → Placa"
        cmbAsignacion.setConverter(new StringConverter<AsignacionBusDestino>() {
            @Override
            public String toString(AsignacionBusDestino a) {
                if (a == null) return "";

                String destino = a.getDestino().getNombre().getNombreMostrar();
                String bus = a.getBus().getPlaca();

                // Formato bonito de fecha
                DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                String fecha = a.getDestino().getFechaSalida().format(fmtFecha);

                return destino + " → " + bus + " | " + fecha;
            }

            @Override
            public AsignacionBusDestino fromString(String s) {
                return null;
            }
        });
        cmbAsignacion.setItems(obtenerAsignaciones());

        // Cliente: mostrar "Código - Nombre Completo"
        cmbCliente.setConverter(new StringConverter<Cliente>() {
            @Override public String toString(Cliente c) {
                if (c == null) return "";
                return c.getCodigoCliente() + " - " + c.getNombreCompleto();
            }
            @Override public Cliente fromString(String s) { return null; }
        });
        cmbCliente.setItems(clienteServicio.obtenerTodosAscendente());
    }

    /** Obtiene todas las asignaciones únicas de la matriz ortogonal. */
    private ObservableList<AsignacionBusDestino> obtenerAsignaciones() {
        ObservableList<AsignacionBusDestino> lista = FXCollections.observableArrayList();
        NodoCabecera fila = asignacionServicio.getMatriz().getCabFilas();
        while (fila != null) {
            NodoMatriz celda = fila.primero;
            while (celda != null) {
                lista.add((AsignacionBusDestino) celda.dato);
                celda = celda.derecha;
            }
            fila = fila.siguiente;
        }
        return lista;
    }

    /** Al seleccionar una asignación, cargar sus horas disponibles en cmbHora. */
    @FXML
    public void onAsignacionSeleccionada() {
        AsignacionBusDestino asig = cmbAsignacion.getValue();
        cmbHora.getItems().clear();
        if (asig == null) return;
        NodoLista nodo = asig.getHorasDisponibles().getCabeza();
        while (nodo != null) {
            cmbHora.getItems().add(nodo.clave); // clave = "HH:mm"
            nodo = nodo.siguiente;
        }
    }

    // =========================================================
    // CARGAR DATOS
    // =========================================================

    public void cargarDatos() {
        tblBoletos.setItems(boletoServicio.obtenerTodos());
        colCodBoleto.setCellValueFactory(new PropertyValueFactory<>("codigoBoleto"));
        colCliente.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getCliente().getNombreCompleto()));
        colDestino.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAsignacion().getDestino()
                .getNombre().getNombreMostrar()));
        colFechaSalida.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAsignacion().getDestino()
                .getFechaSalida().toString()));
        colBus.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAsignacion().getBus().getPlaca()));
        colTipoBus.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getAsignacion().getBus()
                .getTipo().toString()));
        colHora.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getClaveOrden()));
        colCosto.setCellValueFactory(data ->
            new SimpleDoubleProperty(data.getValue().getCosto()).asObject());
    }

    // =========================================================
    // NUEVO / GUARDAR
    // =========================================================

    @FXML
    public void nuevo() {
        switch (tipoOperacion) {
            case NINGUNO:
                limpiarControles();
                activarControles();
                tblBoletos.getSelectionModel().clearSelection();
                // Recargar combos con datos actuales
                cmbAsignacion.setItems(obtenerAsignaciones());
                cmbCliente.setItems(clienteServicio.obtenerTodosAscendente());
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnReporte.setDisable(true);
                btnCSV.setDisable(true);
                imgNuevo.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Save.png")));
                imgEliminar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Cancel.png")));
                tipoOperacion = Operaciones.GUARDAR;
                break;

            case GUARDAR:
                if (cmbAsignacion.getValue() == null
                        || cmbHora.getValue() == null
                        || cmbCliente.getValue() == null) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                        "Debes seleccionar la asignación, la hora y el cliente.");
                    return;
                }
                LocalTime hora = LocalTime.parse(cmbHora.getValue(),
                        DateTimeFormatter.ofPattern("HH:mm"));
                int res = boletoServicio.crear(
                        cmbAsignacion.getValue(),
                        cmbCliente.getValue(),
                        hora);
                if (res == 1) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Hora no disponible",
                        "La hora seleccionada no está disponible en esta asignación.");
                    return;
                }
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnReporte.setDisable(false);
                btnCSV.setDisable(false);
                imgNuevo.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Agregar.png")));
                imgEliminar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Quitar.png")));
                tipoOperacion = Operaciones.NINGUNO;
                cargarDatos();
                break;
        }
    }

    // =========================================================
    // ELIMINAR / CANCELAR
    // =========================================================

    @FXML
    public void eliminar() {
        switch (tipoOperacion) {
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnReporte.setDisable(false);
                btnCSV.setDisable(false);
                imgNuevo.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Agregar.png")));
                imgEliminar.setImage(new Image(getClass().getResourceAsStream("/flotabuses/images/Quitar.png")));
                tipoOperacion = Operaciones.NINGUNO;
                cargarDatos();
                break;
            default:
                Boleto seleccionado = tblBoletos.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    Alert conf = new Alert(Alert.AlertType.CONFIRMATION);
                    conf.setTitle("Eliminar Boleto");
                    conf.setHeaderText(null);
                    conf.setContentText("¿Eliminar el boleto #" + seleccionado.getCodigoBoleto()
                        + " de " + seleccionado.getCliente().getNombreCompleto() + "?");
                    Optional<ButtonType> res = conf.showAndWait();
                    if (res.isPresent() && res.get() == ButtonType.OK) {
                        boletoServicio.eliminar(seleccionado.getCodigoBoleto());
                        limpiarControles();
                        cargarDatos();
                    }
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                        "Debes seleccionar un boleto primero.");
                }
        }
    }

    // =========================================================
    // REPORTE
    // =========================================================

    @FXML
    public void reporte() {
        ReporteService.getInstance().reporteBoletos();
    }

    // =========================================================
    // CSV
    // =========================================================

    @FXML
    public void CSV() {
        ButtonType btnImport  = new ButtonType("Importar");
        ButtonType btnExport  = new ButtonType("Exportar");
        ButtonType btnCancel  = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("CSV — Boletos");
        alert.setHeaderText(null);
        alert.setContentText("¿Qué deseas hacer?");
        alert.getButtonTypes().setAll(btnImport, btnExport, btnCancel);
        Optional<ButtonType> res = alert.showAndWait();
        if (!res.isPresent()) return;
        if (res.get() == btnImport) importarCSV();
        else if (res.get() == btnExport) exportarCSV();
    }

    private void importarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Ticket.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        int ok = 0, err = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            br.readLine(); // saltar encabezado
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",", -1);
                if (p.length < 10) { err++; continue; }
                try {
                    // Columnas: CodAsig, Hora, CodDest, NomDest, Fecha, Placa, Tipo, CodCliente, NomCliente, Costo
                    String placaBus     = p[5].trim();
                    String nombreDest   = p[3].trim();
                    int    codCliente   = Integer.parseInt(p[7].trim());
                    LocalTime hora      = LocalTime.parse(p[1].trim(),
                                            DateTimeFormatter.ofPattern("HH:mm"));

                    flotabuses.modelos.Bus bus =
                        flotabuses.servicios.BusService.getInstance().buscarPorPlaca(placaBus);
                    flotabuses.modelos.Destino destino = null;
                    for (flotabuses.enums.NombreDestino nd : flotabuses.enums.NombreDestino.values()) {
                        if (nd.getNombreMostrar().equalsIgnoreCase(nombreDest)) {
                            destino = flotabuses.servicios.DestinoService.getInstance()
                                        .buscarPorNombre(nd);
                            break;
                        }
                    }
                    Cliente cliente = clienteServicio.buscarPorCodigo(codCliente);
                    if (bus == null || destino == null || cliente == null) { err++; continue; }
                    AsignacionBusDestino asig = asignacionServicio.buscar(destino, bus);
                    if (asig == null) { err++; continue; }
                    if (boletoServicio.crear(asig, cliente, hora) == 0) ok++;
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
        fc.setTitle("Guardar Ticket.csv");
        fc.setInitialFileName("Ticket.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            pw.println("Código_asignación,Hora_seleccionada,Código_destino,Nombre_destino,"
                     + "Fecha_salida_destino_asignado,Placa_bus,Tipo_bus,Código_cliente,"
                     + "Nombre_cliente,Costo_generado");
            NodoLista nodo = boletoServicio.getLista().getCabeza();
            while (nodo != null) {
                Boleto b = (Boleto) nodo.dato;
                pw.println(b.getCodigoBoleto() + ","
                    + b.getClaveOrden() + ","
                    + b.getAsignacion().getDestino().getCodigoDestino() + ","
                    + b.getAsignacion().getDestino().getNombre().getNombreMostrar() + ","
                    + b.getAsignacion().getDestino().getFechaSalida() + ","
                    + b.getAsignacion().getBus().getPlaca() + ","
                    + b.getAsignacion().getBus().getTipo() + ","
                    + b.getCliente().getCodigoCliente() + ","
                    + b.getCliente().getNombreCompleto() + ","
                    + String.format("%.2f", b.getCosto()));
                nodo = nodo.siguiente;
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completa",
                "Archivo guardado: " + archivo.getName());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error escribiendo archivo: " + e.getMessage());
        }
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    @FXML
    public void seleccionarElemento() {
        // Solo permite visualizar; no se llena el form para edición inline
    }

    private void activarControles() {
        cmbAsignacion.setDisable(false);
        cmbHora.setDisable(false);
        cmbCliente.setDisable(false);
    }

    private void desactivarControles() {
        cmbAsignacion.setDisable(true);
        cmbHora.setDisable(true);
        cmbCliente.setDisable(true);
    }

    private void limpiarControles() {
        cmbAsignacion.getSelectionModel().clearSelection();
        cmbAsignacion.setValue(null);
        cmbHora.getItems().clear();
        cmbCliente.getSelectionModel().clearSelection();
        cmbCliente.setValue(null);
        tblBoletos.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
