/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.main.FlotaBuses;
import flotabuses.utils.IconoValidacion;
import flotabuses.utils.ValidadorCampos;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

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
import javafx.scene.control.DateCell;

/**
 * Controlador del modulo de gestion de destinos turisticos (M3).
 *
 * <p>Implementa el patron MVC: gestiona la pantalla {@code destinoView.fxml} y
 * delega la persistencia a {@link flotabuses.servicios.DestinoService}.</p>
 *
 * <p>Opera con la misma maquina de estados que los demas modulos:</p>
 * <pre>
 *   NINGUNO  --(nuevo())--> GUARDAR  --(nuevo())--> NINGUNO   (creado)
 *   NINGUNO  --(editar())--> ACTUALIZAR  --(editar())--> NINGUNO   (actualizado)
 *   NINGUNO  --(eliminar())--> confirmacion --> NINGUNO  (eliminado)
 *   GUARDAR | ACTUALIZAR --(eliminar()/reporte())--> NINGUNO  (cancelar)
 * </pre>
 *
 * <p>Particularidades de este modulo:</p>
 * <ul>
 *   <li>El nombre del destino ({@link flotabuses.enums.NombreDestino}) se selecciona
 *       desde un {@code ComboBox} cerrado (catalogo fijo de 15 destinos guatemaltecos)
 *       y actua como clave unica; no puede modificarse en edicion.</li>
 *   <li>La fecha de salida se ingresa con un {@code DatePicker} que deshabilita
 *       fechas pasadas mediante un {@code DayCellFactory}.</li>
 *   <li>Solo los destinos con estado {@code CONFIRMADO} aparecen disponibles en
 *       el modulo de asignaciones y boletos.</li>
 *   <li>Solo se valida con icono Canvas el campo costo.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.DestinoService
 * @see flotabuses.modelos.Destino
 * @see flotabuses.enums.NombreDestino
 * @see flotabuses.enums.EstadoDestino
 * @see flotabuses.enums.Operaciones
 */
public class DestinoController implements Initializable{

    /** Referencia a la aplicacion principal para la navegacion entre escenas. */
    private FlotaBuses escenarioPrincipal;

    /**
     * Estado actual de la maquina de estados CRUD.
     * Controla el comportamiento de los botones Nuevo, Eliminar, Editar y Reporte.
     */
    private Operaciones tipoOperacion = Operaciones.NINGUNO;

    /** Servicio Singleton que gestiona la lista doblemente enlazada de destinos. */
    private DestinoService destinoService = DestinoService.getInstance();
            
    /** Boton para importar/exportar CSV. Solo visible para el rol ADMIN. */
    @FXML
    private Button btnCSV;

    /** Boton dual: "Editar" en estado NINGUNO / "Actualizar" en estado ACTUALIZAR. */
    @FXML
    private Button btnEditar;

    /** Boton dual: "Eliminar" en estado NINGUNO / "Cancelar" en estado GUARDAR. */
    @FXML
    private Button btnEliminar;

    /** Boton dual: "Nuevo" en estado NINGUNO / "Guardar" en estado GUARDAR. */
    @FXML
    private Button btnNuevo;

    /** Boton dual: "Reporte" en estado NINGUNO / "Cancelar" en estado ACTUALIZAR. */
    @FXML
    private Button btnReporte;

    /** ComboBox con los estados del destino (CONFIRMADO / PENDIENTE). */
    @FXML
    private ComboBox<EstadoDestino> cmbEstado;

    /**
     * ComboBox con el catalogo fijo de 15 destinos guatemaltecos.
     * Actua como clave unica y se bloquea en modo edicion.
     */
    @FXML
    private ComboBox<NombreDestino> cmbNombre;

    /** Columna del codigo interno del destino. */
    @FXML
    private TableColumn<Destino, Integer> colCodigoDestino;

    /** Columna del costo del boleto en quetzales. */
    @FXML
    private TableColumn<Destino, Double> colCostoBoleto;

    /** Columna de la descripcion libre del destino. */
    @FXML
    private TableColumn<Destino, String> colDescripcion;

    /** Columna del estado del destino (CONFIRMADO / PENDIENTE). */
    @FXML
    private TableColumn<Destino, String> colEstado;

    /** Columna de la fecha programada de salida. */
    @FXML
    private TableColumn<Destino, LocalDate> colFechaSalida;

    /** Columna del nombre del destino. */
    @FXML
    private TableColumn<Destino, String> colNombre;

    /**
     * Selector de fecha para la salida del destino.
     * Las fechas anteriores al dia actual se deshabilitan mediante un
     * {@code DayCellFactory} configurado en {@link #initialize}.
     */
    @FXML
    private DatePicker dtpFechaSalida;

    /** Icono del boton CSV. */
    @FXML
    private ImageView imgCSV;

    /** Icono del boton Editar; cambia entre Edit.png y Save.png. */
    @FXML
    private ImageView imgEditar;

    /** Icono del boton Eliminar; cambia entre Quitar.png y Cancel.png. */
    @FXML
    private ImageView imgEliminar;

    /** Icono del boton Nuevo; cambia entre Agregar.png y Save.png. */
    @FXML
    private ImageView imgNuevo;

    /** Icono del boton Reporte; cambia entre Reporte.png y Cancel.png. */
    @FXML
    private ImageView imgReporte;

    /** Tabla que muestra todos los destinos registrados ordenados por nombre. */
    @FXML
    private TableView<Destino> tblDestinos;

    /** Campo de texto de solo lectura para el codigo del destino (generado automaticamente). */
    @FXML
    private TextField txtCodigoDestino;

    /**
     * Campo de texto para el costo del boleto en quetzales.
     * TextFormatter restringe a hasta 8 digitos con hasta 2 decimales.
     */
    @FXML
    private TextField txtCostoBoleto;

    /** Campo de texto para la descripcion libre del destino. */
    @FXML
    private TextField txtDescripcion;

    /** Contenedor GridPane donde se inyecta el icono de validacion Canvas. */
    @FXML
    private GridPane gridDatos;

    // ── Icono de validación (Canvas) ─────────────────────
    /** Icono Canvas que indica validez del campo costo en tiempo real. */
    private IconoValidacion icoCosto;

    /**
     * Inicializa el controlador tras cargar el FXML.
     * Carga los valores de los ComboBox, aplica el {@code DayCellFactory} al
     * {@code DatePicker} para deshabilitar fechas pasadas, configura el
     * {@code TextFormatter} del costo e inyecta el icono Canvas de validacion.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbNombre.setItems(FXCollections.observableArrayList(NombreDestino.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoDestino.values()));

        dtpFechaSalida.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });

        // TextFormatter: costo solo dígitos y punto decimal
        txtCostoBoleto.setTextFormatter(new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.matches("\\d{0,8}(\\.\\d{0,2})?")) return change;
            return null;
        }));

        // Inyectar icono Canvas junto al campo de costo
        icoCosto = envolver(gridDatos, txtCostoBoleto);

        // Listener de validación en tiempo real
        configurarValidacion(txtCostoBoleto, icoCosto,
            ValidadorCampos::esCostoValido, ValidadorCampos.mensajeCosto());

        cargarDatos();
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
     * Inyecta la instancia principal de la aplicacion y aplica restricciones
     * de visibilidad segun el rol del usuario autenticado.
     * Si el usuario tiene rol {@code OPERADOR}, oculta y descarta el boton CSV.
     *
     * @param escenarioPrincipal instancia principal de la aplicacion
     */
    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
        flotabuses.modelos.Usuario u = escenarioPrincipal.getUsuarioActual();
        if (u != null && u.esOperador()) {
            btnCSV.setVisible(false);
            btnCSV.setManaged(false);
        }
    }

    /**
     * Navega de regreso al menu principal invocando
     * {@link FlotaBuses#menuPrincipal()}.
     */
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }

    /**
     * Carga todos los destinos del servicio en la tabla y configura las
     * {@code PropertyValueFactory} de cada columna.
     * Se llama al inicializar y despues de cada operacion CRUD exitosa.
     */
    public void cargarDatos(){
        tblDestinos.setItems(destinoService.obtenerTodos());
        colCodigoDestino.setCellValueFactory(new PropertyValueFactory<>("codigoDestino"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colFechaSalida.setCellValueFactory(new PropertyValueFactory<>("fechaSalida"));
        colCostoBoleto.setCellValueFactory(new PropertyValueFactory<>("costoBoleto"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → GUARDAR:</b> limpia y activa controles, cambia etiquetas e
     *       iconos y avanza el estado a {@code GUARDAR}.</li>
     *   <li><b>GUARDAR → NINGUNO:</b> valida que todos los campos esten completos,
     *       parsea el costo y llama a {@link flotabuses.servicios.DestinoService#crear}.
     *       Muestra alerta si el destino ya existe ({@code false}). En caso de exito,
     *       desactiva controles y regresa a {@code NINGUNO}.</li>
     * </ul>
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>GUARDAR → NINGUNO:</b> cancela la creacion en curso y restaura la
     *       interfaz al estado {@code NINGUNO}.</li>
     *   <li><b>NINGUNO (default):</b> solicita confirmacion al usuario y, si acepta,
     *       elimina el destino seleccionado mediante
     *       {@link flotabuses.servicios.DestinoService#eliminar}.
     *       Si no hay ningun destino seleccionado, muestra una advertencia.</li>
     * </ul>
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → ACTUALIZAR:</b> verifica que haya un destino seleccionado,
     *       activa controles, bloquea {@code cmbNombre} (la clave no puede cambiar)
     *       y avanza el estado a {@code ACTUALIZAR}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> valida campos, invoca
     *       {@link flotabuses.servicios.DestinoService#actualizar} y, en caso de
     *       exito, restaura la interfaz y regresa a {@code NINGUNO}.</li>
     * </ul>
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO:</b> muestra dos dialogos encadenados para elegir formato
     *       (PDF o HTML) y orden (Ascendente o Descendente).
     *       Delega en {@link flotabuses.servicios.ReporteService}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> cancela la edicion en curso.</li>
     * </ul>
     */
    public void reporte() {
        switch(tipoOperacion) {
            case NINGUNO:
                // Paso 1: elegir formato
                ButtonType btnPdf  = new ButtonType("PDF");
                ButtonType btnHtml = new ButtonType("HTML");
                ButtonType btnCan0 = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert fmtAlert = new Alert(Alert.AlertType.CONFIRMATION);
                fmtAlert.setTitle("Reporte de Destinos");
                fmtAlert.setHeaderText(null);
                fmtAlert.setContentText("Seleccione el formato del reporte:");
                fmtAlert.getButtonTypes().setAll(btnPdf, btnHtml, btnCan0);
                Optional<ButtonType> fmtRes = fmtAlert.showAndWait();
                if (!fmtRes.isPresent() || fmtRes.get() == btnCan0) { limpiarControles(); break; }
                boolean esPdf = fmtRes.get() == btnPdf;

                // Paso 2: elegir orden
                ButtonType btnAsc  = new ButtonType("Ascendente");
                ButtonType btnDesc = new ButtonType("Descendente");
                ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert dialogo = new Alert(Alert.AlertType.CONFIRMATION);
                dialogo.setTitle("Reporte de Destinos");
                dialogo.setHeaderText(null);
                dialogo.setContentText("¿En qué orden deseas el reporte?");
                dialogo.getButtonTypes().setAll(btnAsc, btnDesc, btnCancelar);
                Optional<ButtonType> resp = dialogo.showAndWait();
                if (resp.isPresent() && resp.get() != btnCancelar) {
                    boolean ascendente = resp.get() == btnAsc;
                    if (esPdf) ReporteService.getInstance().reporteDestinosPdf(ascendente);
                    else       ReporteService.getInstance().reporteDestinosHtml(ascendente);
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
    
    /**
     * Muestra un dialogo de confirmacion para elegir entre importar o exportar
     * destinos en formato CSV. Delega en {@link #importarCSV()} o
     * {@link #exportarCSV()} segun la eleccion.
     * Solo visible para usuarios con rol {@code ADMIN}.
     */
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

    /**
     * Abre un selector de archivo para elegir un CSV de destinos y lo importa
     * fila por fila. El formato esperado es:
     * {@code Código,Nombre_Destino,Fecha_salida,Costo_persona,Estado,Descripción}.
     * La fecha debe tener formato {@code dd/MM/yyyy}; el estado debe ser
     * {@code CONFIRMADO} o {@code PENDIENTE}; el nombre debe coincidir
     * exactamente con uno de los valores del enum {@link flotabuses.enums.NombreDestino}.
     * Al finalizar muestra un resumen con importados y errores detallados.
     */
    private void importarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar Destinos CSV");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        int importados = 0, errores = 0, fila = 1;
        StringBuilder detalles = new StringBuilder();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        try (BufferedReader br = new BufferedReader(
                new FileReader(archivo, StandardCharsets.UTF_8))) {
            br.readLine(); // saltar encabezado
            String linea;
            while ((linea = br.readLine()) != null) {
                fila++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split(",", -1);
                if (p.length < 6) {
                    errores++;
                    detalles.append("Fila ").append(fila)
                        .append(": columnas insuficientes (se esperan 6)\n");
                    continue;
                }
                try {
                    if (p[1].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Nombre_Destino' está vacío\n");
                        continue;
                    }
                    if (p[2].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Fecha_salida' está vacío\n");
                        continue;
                    }
                    if (p[3].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Costo_persona' está vacío\n");
                        continue;
                    }
                    if (p[4].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Estado' está vacío\n");
                        continue;
                    }
                    NombreDestino nombre = null;
                    for (NombreDestino nd : NombreDestino.values()) {
                        if (nd.getNombreMostrar().equalsIgnoreCase(p[1].trim())) {
                            nombre = nd; break;
                        }
                    }
                    if (nombre == null) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": destino desconocido \"").append(p[1].trim()).append("\"\n");
                        continue;
                    }
                    LocalDate fecha;
                    try { fecha = LocalDate.parse(p[2].trim(), fmt); }
                    catch (Exception e) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": fecha invalida \"").append(p[2].trim())
                            .append("\" (formato esperado: dd/MM/yyyy)\n");
                        continue;
                    }
                    double costo;
                    try {
                        costo = Double.parseDouble(p[3].trim());
                        if (costo <= 0) throw new NumberFormatException();
                    } catch (NumberFormatException e) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": costo invalido \"").append(p[3].trim())
                            .append("\" (debe ser numero > 0)\n");
                        continue;
                    }
                    EstadoDestino estado;
                    try { estado = EstadoDestino.valueOf(p[4].trim().toUpperCase()); }
                    catch (IllegalArgumentException e) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": estado invalido \"").append(p[4].trim())
                            .append("\" (CONFIRMADO o PENDIENTE)\n");
                        continue;
                    }
                    String desc = p[5].trim();
                    boolean ok = destinoService.crear(nombre, fecha, costo, estado, desc);
                    if (ok) {
                        importados++;
                    } else {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": destino \"").append(nombre.getNombreMostrar())
                            .append("\" ya existe\n");
                    }
                } catch (Exception e) {
                    errores++;
                    detalles.append("Fila ").append(fila)
                        .append(": error inesperado - ").append(e.getMessage()).append("\n");
                }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo leer el archivo: " + e.getMessage());
            return;
        }
        cargarDatos();
        String msg = "Importados: " + importados + "  |  Errores: " + errores;
        if (detalles.length() > 0)
            msg += "\n\nDetalle de errores:\n" + detalles;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importacion completa", msg);
    }

    /**
     * Abre un selector de archivo para guardar los destinos actuales en un CSV
     * con BOM UTF-8. El encabezado es:
     * {@code Código,Nombre_Destino,Fecha_salida,Costo_persona,Estado,Descripción}.
     * Las fechas se formatean como {@code dd/MM/yyyy} y los destinos se exportan
     * en el orden de la lista doblemente enlazada (A-Z por nombre).
     */
    private void exportarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Destinos CSV");
        chooser.setInitialFileName("Destinos.csv");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        try (PrintWriter pw = new PrintWriter(
                new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            pw.write('﻿'); // BOM UTF-8 para compatibilidad con Excel
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
    
    /**
     * Muestra un dialogo de alerta modal con el tipo, titulo y mensaje indicados.
     *
     * @param tipo    tipo de alerta ({@code WARNING}, {@code ERROR}, {@code INFORMATION})
     * @param titulo  texto del titulo de la ventana
     * @param mensaje contenido del cuerpo del dialogo
     */
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Copia los datos del destino seleccionado en la tabla hacia los controles
     * del formulario. Se invoca desde el evento {@code onMouseClicked} de la tabla.
     */
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
    
    /**
     * Pone todos los controles del formulario en modo solo lectura / deshabilitado.
     * Se llama al volver al estado {@code NINGUNO}.
     */
    public void desactivarControles() {
        //txtCodigoDestino.setEditable(false);
        cmbNombre.setDisable(true);
        dtpFechaSalida.setDisable(true);
        txtCostoBoleto.setEditable(false);
        cmbEstado.setDisable(true);
        txtDescripcion.setEditable(false);
    }
    
    /**
     * Habilita todos los controles del formulario para edicion.
     * En modo actualizacion, {@code editar()} bloquea el {@code cmbNombre}
     * inmediatamente despues de activar.
     */
    public void activarControles() {
        //txtCodigoDestino.setEditable(true);
        cmbNombre.setDisable(false);
        dtpFechaSalida.setDisable(false);
        txtCostoBoleto.setEditable(true);
        cmbEstado.setDisable(false);
        txtDescripcion.setEditable(true);
    }
    
    /**
     * Vacia todos los controles del formulario, limpia la seleccion de la tabla
     * y resetea el icono Canvas y el estilo CSS del campo costo.
     */
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
        // Limpiar icono y estilo
        if (icoCosto != null) icoCosto.limpiar();
        if (txtCostoBoleto != null)
            txtCostoBoleto.getStyleClass().removeAll("campo-valido", "campo-invalido");
    }

    // =========================================================
    // HELPERS – Icono Canvas + Validación
    // =========================================================

    private IconoValidacion envolver(GridPane grid, TextField campo) {
        if (grid == null || campo == null) return new IconoValidacion();
        Integer col    = GridPane.getColumnIndex(campo);
        Integer row    = GridPane.getRowIndex(campo);
        Integer colSpan = GridPane.getColumnSpan(campo);
        Integer rowSpan = GridPane.getRowSpan(campo);
        javafx.geometry.Insets margin = GridPane.getMargin(campo);
        if (col == null) col = 0;
        if (row == null) row = 0;

        IconoValidacion ico = new IconoValidacion();
        HBox hbox = new HBox(4, campo, ico);
        hbox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(campo, Priority.ALWAYS);

        grid.getChildren().remove(campo);
        GridPane.setColumnIndex(hbox, col);
        GridPane.setRowIndex(hbox, row);
        if (colSpan != null) GridPane.setColumnSpan(hbox, colSpan);
        if (rowSpan != null) GridPane.setRowSpan(hbox, rowSpan);
        if (margin  != null) GridPane.setMargin(hbox, margin);
        grid.getChildren().add(hbox);
        return ico;
    }

    private void configurarValidacion(TextField campo, IconoValidacion ico,
                                      Predicate<String> regla, String mensajeError) {
        Tooltip tooltip = new Tooltip(mensajeError);
        campo.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!campo.isEditable() || newVal == null || newVal.isEmpty()) {
                if (ico != null) ico.limpiar();
                campo.getStyleClass().removeAll("campo-valido", "campo-invalido");
                Tooltip.uninstall(campo, tooltip);
                return;
            }
            if (regla.test(newVal)) {
                if (ico != null) ico.mostrarValido();
                campo.getStyleClass().removeAll("campo-invalido");
                if (!campo.getStyleClass().contains("campo-valido"))
                    campo.getStyleClass().add("campo-valido");
                Tooltip.uninstall(campo, tooltip);
            } else {
                if (ico != null) ico.mostrarInvalido();
                campo.getStyleClass().removeAll("campo-valido");
                if (!campo.getStyleClass().contains("campo-invalido"))
                    campo.getStyleClass().add("campo-invalido");
                Tooltip.install(campo, tooltip);
            }
        });
    }
}
