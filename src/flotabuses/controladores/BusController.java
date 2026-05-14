/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.controladores;

import flotabuses.enums.EstadoBus;
import flotabuses.enums.Operaciones;
import flotabuses.enums.TipoBus;
import flotabuses.estructuras.NodoLista;
import flotabuses.main.FlotaBuses;
import flotabuses.modelos.Bus;
import flotabuses.servicios.BusService;
import flotabuses.servicios.ReporteService;
import flotabuses.utils.IconoValidacion;
import flotabuses.utils.ValidadorCampos;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.FileChooser;


/**
 * Controlador del modulo de gestion de la flotilla de buses (M2).
 *
 * <p>Implementa el patron MVC: gestiona la pantalla {@code busView.fxml} y
 * delega la persistencia a {@link flotabuses.servicios.BusService}.</p>
 *
 * <p>Opera como una maquina de estados a traves de {@link #tipoOperacion}.
 * Los flujos CRUD son los mismos que en {@link ClienteController}:</p>
 * <pre>
 *   NINGUNO  --(nuevo())--> GUARDAR  --(nuevo())--> NINGUNO   (registro creado)
 *   NINGUNO  --(editar())--> ACTUALIZAR  --(editar())--> NINGUNO   (registro actualizado)
 *   NINGUNO  --(eliminar())--> confirmacion --> NINGUNO  (registro eliminado)
 *   GUARDAR | ACTUALIZAR --(eliminar()/reporte())--> NINGUNO  (cancelar)
 * </pre>
 *
 * <p>Diferencias clave respecto a {@link ClienteController}:</p>
 * <ul>
 *   <li>Los campos {@code txtPlaca} y {@code cmbTipo} se bloquean en modo
 *       edicion: la placa es la clave de busqueda y el tipo no puede cambiar
 *       una vez registrado (afectaria la validacion de capacidad).</li>
 *   <li>La capacidad se valida contra el rango del tipo de bus seleccionado
 *       ({@link flotabuses.enums.TipoBus#capacidadValida(int)}).</li>
 *   <li>Solo se validan con icono Canvas los campos: placa, capacidad y color.</li>
 * </ul>
 *
 * <p>El boton CSV solo es visible para el rol {@code ADMIN}.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.BusService
 * @see flotabuses.modelos.Bus
 * @see flotabuses.enums.TipoBus
 * @see flotabuses.enums.EstadoBus
 * @see flotabuses.enums.Operaciones
 */
public class BusController implements Initializable{

    /** Referencia a la aplicacion principal para la navegacion entre escenas. */
    private FlotaBuses escenarioPrincipal;

    /**
     * Estado actual de la maquina de estados CRUD.
     * Controla el comportamiento de los botones Nuevo, Eliminar, Editar y Reporte.
     */
    private Operaciones tipoOperacion = Operaciones.NINGUNO;

    /** Servicio Singleton que gestiona la lista doblemente enlazada de buses. */
    private BusService busService = BusService.getInstance();

    /** Boton dual: "Nuevo" en estado NINGUNO / "Guardar" en estado GUARDAR. */
    @FXML
    private Button btnEditar;

    /** Boton dual: "Eliminar" en estado NINGUNO / "Cancelar" en estado GUARDAR. */
    @FXML
    private Button btnEliminar;

    /** Boton dual: "Editar" en estado NINGUNO / "Actualizar" en estado ACTUALIZAR. */
    @FXML
    private Button btnNuevo;

    /** Boton dual: "Reporte" en estado NINGUNO / "Cancelar" en estado ACTUALIZAR. */
    @FXML
    private Button btnReporte;

    /** Boton para importar/exportar CSV. Solo visible para el rol ADMIN. */
    @FXML
    private Button btnCSV;

    /** ComboBox con los estados operativos del bus (DISPONIBLE / NO_DISPONIBLE). */
    @FXML
    private ComboBox<EstadoBus> cmbEstado;

    /**
     * ComboBox con los tipos de bus (MICROBUS, COUNTY, PULLMAN).
     * Se bloquea en modo edicion ya que el tipo determina el rango de capacidad.
     */
    @FXML
    private ComboBox<TipoBus>   cmbTipo;

    /** Columna del codigo interno del bus. */
    @FXML
    private TableColumn<Bus, Integer>  colCodigoBus;

    /** Columna de la placa del bus (clave unica, formato guatemalteco X-000-XXX). */
    @FXML
    private TableColumn<Bus, String>   colPlaca;

    /** Columna del tipo de bus. */
    @FXML
    private TableColumn<Bus, String>   colTipo;

    /** Columna de la capacidad de pasajeros. */
    @FXML
    private TableColumn<Bus, Integer>  colCapacidad;

    /** Columna del color del bus. */
    @FXML
    private TableColumn<Bus, String>   colColor;

    /** Columna del estado operativo del bus. */
    @FXML
    private TableColumn<Bus, String>   colEstado;

    /** Columna de la descripcion libre del bus. */
    @FXML
    private TableColumn<Bus, String>   colDescripcion;

    /** Icono del boton Editar; cambia entre Edit.png y Save.png segun el estado. */
    @FXML
    private ImageView imgEditar;

    /** Icono del boton Eliminar; cambia entre Quitar.png y Cancel.png segun el estado. */
    @FXML
    private ImageView imgEliminar;

    /** Icono del boton Nuevo; cambia entre Agregar.png y Save.png segun el estado. */
    @FXML
    private ImageView imgNuevo;

    /** Icono del boton Reporte; cambia entre Reporte.png y Cancel.png segun el estado. */
    @FXML
    private ImageView imgReporte;

    /** Icono del boton CSV. */
    @FXML
    private ImageView imgCSV;

    /** Tabla que muestra todos los buses registrados ordenados por placa. */
    @FXML
    private TableView<Bus> tblBuses;

    /** Campo de texto para la capacidad del bus. TextFormatter restringe a 4 digitos. */
    @FXML
    private TextField txtCapacidad;

    /** Campo de texto para el color del bus. Solo letras y espacios. */
    @FXML
    private TextField txtColor;

    /** Campo de texto para la descripcion libre del bus. */
    @FXML
    private TextField txtDescripcion;

    /**
     * Campo de texto para la placa del bus (formato X-000-XXX).
     * TextFormatter restringe a letras, digitos y guion, maximo 9 caracteres.
     * Se pone en modo no editable al actualizar.
     */
    @FXML
    private TextField txtPlaca;

    /** Contenedor GridPane donde se inyectan los iconos de validacion Canvas. */
    @FXML
    private GridPane gridDatos;

    // ── Iconos de validación (Canvas) ────────────────────
    /** Icono Canvas que indica validez del campo placa en tiempo real. */
    private IconoValidacion icoPlaca;

    /** Icono Canvas que indica validez del campo capacidad en tiempo real. */
    private IconoValidacion icoCapacidad;

    /** Icono Canvas que indica validez del campo color en tiempo real. */
    private IconoValidacion icoColor;

    /**
     * Inicializa el controlador tras cargar el FXML.
     * Carga los valores de los ComboBox, aplica TextFormatters a los campos
     * numericos y de placa, inyecta los iconos Canvas de validacion y
     * carga los datos iniciales en la tabla.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbTipo.setItems(FXCollections.observableArrayList(TipoBus.values()));
        cmbEstado.setItems(FXCollections.observableArrayList(EstadoBus.values()));

        // TextFormatter: placa solo letras, dígitos y guión, máx 9 caracteres
        txtPlaca.setTextFormatter(new TextFormatter<>(change -> {
            String t = change.getControlNewText();
            if (t.matches("[a-zA-Z0-9\\-]{0,9}")) return change;
            return null;
        }));
        // TextFormatter: capacidad solo dígitos, máx 4 cifras
        txtCapacidad.setTextFormatter(new TextFormatter<>(change -> {
            if (change.getControlNewText().matches("\\d{0,4}")) return change;
            return null;
        }));

        // Inyectar iconos Canvas
        icoPlaca    = envolver(gridDatos, txtPlaca);
        icoCapacidad = envolver(gridDatos, txtCapacidad);
        icoColor    = envolver(gridDatos, txtColor);

        // Listeners de validación en tiempo real
        configurarValidacion(txtPlaca,    icoPlaca,    ValidadorCampos::esPlacaValida,    ValidadorCampos.mensajePlaca());
        configurarValidacion(txtCapacidad, icoCapacidad, ValidadorCampos::esCapacidadValida, ValidadorCampos.mensajeCapacidad());
        configurarValidacion(txtColor,    icoColor,    ValidadorCampos::esColorValido,    ValidadorCampos.mensajeColor());

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
        if (escenarioPrincipal.getUsuarioActual() != null
                && escenarioPrincipal.getUsuarioActual().esOperador()) {
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
     * Carga todos los buses del servicio en la tabla y configura las
     * {@code PropertyValueFactory} de cada columna.
     * Se llama al inicializar y despues de cada operacion CRUD exitosa.
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → GUARDAR:</b> limpia y activa controles, cambia etiquetas e
     *       iconos y avanza el estado a {@code GUARDAR}.</li>
     *   <li><b>GUARDAR → NINGUNO:</b> valida campos (vacios y formatos), invoca
     *       {@link flotabuses.servicios.BusService#crear} y, si la placa ya existe,
     *       muestra una advertencia. Si la capacidad esta fuera del rango del tipo,
     *       captura la {@code IllegalArgumentException} y muestra el mensaje del servicio.
     *       En caso de exito, desactiva controles y regresa a {@code NINGUNO}.</li>
     * </ul>
     */
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

                // Validar formato guatemalteco
                if (!ValidadorCampos.esPlacaValida(txtPlaca.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Placa inválida", ValidadorCampos.mensajePlaca()); return;
                }
                if (!ValidadorCampos.esCapacidadValida(txtCapacidad.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad inválida", ValidadorCampos.mensajeCapacidad()); return;
                }
                if (!ValidadorCampos.esColorValido(txtColor.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Color inválido", ValidadorCampos.mensajeColor()); return;
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>GUARDAR → NINGUNO:</b> cancela la creacion en curso y restaura la
     *       interfaz al estado {@code NINGUNO}.</li>
     *   <li><b>NINGUNO (default):</b> solicita confirmacion al usuario y, si acepta,
     *       elimina el bus seleccionado mediante
     *       {@link flotabuses.servicios.BusService#eliminar}.
     *       Si no hay ningun bus seleccionado, muestra una advertencia.</li>
     * </ul>
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → ACTUALIZAR:</b> verifica que haya un bus seleccionado,
     *       activa controles y bloquea {@code txtPlaca} y {@code cmbTipo} (no se
     *       pueden cambiar al editar), fuerza la revalidacion de campos y avanza
     *       el estado a {@code ACTUALIZAR}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> valida campos, invoca
     *       {@link flotabuses.servicios.BusService#actualizar} y, en caso de
     *       {@code IllegalArgumentException} por capacidad fuera de rango, muestra
     *       la alerta correspondiente. En caso de exito, restaura la interfaz.</li>
     * </ul>
     */
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
                    revalidarCampos();
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Advertencia", "Debes seleccionar un bus primero.");
                }
                break;
            
            case ACTUALIZAR:
                if (txtCapacidad.getText().trim().isEmpty() ||
                    txtColor.getText().trim().isEmpty() ||
                    cmbEstado.getValue() == null ||
                    txtDescripcion.getText().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }

                // Validar formato
                if (!ValidadorCampos.esCapacidadValida(txtCapacidad.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Capacidad inválida", ValidadorCampos.mensajeCapacidad()); return;
                }
                if (!ValidadorCampos.esColorValido(txtColor.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Color inválido", ValidadorCampos.mensajeColor()); return;
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO:</b> muestra dos dialogos encadenados para elegir el formato
     *       (PDF o HTML) y el orden (Ascendente o Descendente por placa).
     *       Delega la generacion en {@link flotabuses.servicios.ReporteService}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> cancela la edicion en curso y restaura
     *       la interfaz al estado {@code NINGUNO}.</li>
     * </ul>
     */
    public void reporte() {
        switch (tipoOperacion) {
            case NINGUNO:
                // Paso 1: elegir formato
                ButtonType btnPdf  = new ButtonType("PDF");
                ButtonType btnHtml = new ButtonType("HTML");
                ButtonType btnCan0 = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert fmtAlert = new Alert(Alert.AlertType.CONFIRMATION);
                fmtAlert.setTitle("Reporte de Buses");
                fmtAlert.setHeaderText(null);
                fmtAlert.setContentText("Seleccione el formato del reporte:");
                fmtAlert.getButtonTypes().setAll(btnPdf, btnHtml, btnCan0);
                Optional<ButtonType> fmtRes = fmtAlert.showAndWait();
                if (!fmtRes.isPresent() || fmtRes.get() == btnCan0) { limpiarControles(); break; }
                boolean esPdf = fmtRes.get() == btnPdf;

                // Paso 2: elegir orden
                ButtonType btnAsc  = new ButtonType("Ascendente");
                ButtonType btnDesc = new ButtonType("Descendente");
                ButtonType btnCan  = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Reporte de Buses");
                alerta.setHeaderText(null);
                alerta.setContentText("Seleccione el orden por placa:");
                alerta.getButtonTypes().setAll(btnAsc, btnDesc, btnCan);
                Optional<ButtonType> res = alerta.showAndWait();
                if (res.isPresent() && res.get() != btnCan) {
                    boolean asc = res.get() == btnAsc;
                    if (esPdf) ReporteService.getInstance().reporteBusesPdf(asc);
                    else       ReporteService.getInstance().reporteBusesHtml(asc);
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

    /**
     * Muestra un dialogo de confirmacion para elegir entre importar o exportar
     * la flotilla de buses en formato CSV.
     * Delega en {@link #importarCSV()} o {@link #exportarCSV()} segun la eleccion.
     * Solo visible para usuarios con rol {@code ADMIN}.
     */
    public void CSV() {
        ButtonType btnImp = new ButtonType("Importar");
        ButtonType btnExp = new ButtonType("Exportar");
        ButtonType btnCan = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("CSV — Buses");
        alerta.setHeaderText(null);
        alerta.setContentText("¿Qué deseas hacer?");
        alerta.getButtonTypes().setAll(btnImp, btnExp, btnCan);
        Optional<ButtonType> res = alerta.showAndWait();
        if (!res.isPresent()) return;
        if (res.get() == btnImp) importarCSV();
        else if (res.get() == btnExp) exportarCSV();
    }

    /**
     * Abre un selector de archivo para elegir un CSV de buses y lo importa
     * fila por fila a la lista doblemente enlazada. El formato esperado es:
     * {@code Placa,Tipo,Capacidad,Color,Estado,Descripcion}.
     * Valida el tipo ({@code MICROBUS/COUNTY/PULLMAN}), el estado
     * ({@code DISPONIBLE/NO_DISPONIBLE}) y la capacidad segun el rango del tipo.
     * Al finalizar muestra un resumen con la cantidad de registros importados y
     * los errores detallados.
     */
    private void importarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar FlotillaBuses.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        int ok = 0, err = 0, fila = 1;
        StringBuilder detalles = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            br.readLine(); // saltar encabezado
            String linea;
            while ((linea = br.readLine()) != null) {
                fila++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                String[] p = linea.split(",", -1);
                if (p.length < 5) {
                    err++;
                    detalles.append("Fila ").append(fila)
                        .append(": columnas insuficientes (se esperan 6)\n");
                    continue;
                }
                try {
                    String placa = p[0].trim();
                    if (placa.isEmpty()) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Placa' está vacío\n");
                        continue;
                    }
                    if (p[1].trim().isEmpty()) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Tipo' está vacío\n");
                        continue;
                    }
                    TipoBus tipo;
                    try { tipo = TipoBus.valueOf(p[1].trim().toUpperCase()); }
                    catch (IllegalArgumentException e) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": tipo invalido \"").append(p[1].trim())
                            .append("\" (MICROBUS, COUNTY o PULLMAN)\n");
                        continue;
                    }
                    if (p[2].trim().isEmpty()) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Capacidad' está vacío\n");
                        continue;
                    }
                    int capacidad;
                    try { capacidad = Integer.parseInt(p[2].trim()); }
                    catch (NumberFormatException e) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": capacidad no es numero \"").append(p[2].trim()).append("\"\n");
                        continue;
                    }
                    String color = p[3].trim();
                    if (color.isEmpty()) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Color' está vacío\n");
                        continue;
                    }
                    if (p[4].trim().isEmpty()) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Estado' está vacío\n");
                        continue;
                    }
                    EstadoBus estado;
                    try { estado = EstadoBus.valueOf(p[4].trim().toUpperCase()); }
                    catch (IllegalArgumentException e) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": estado invalido \"").append(p[4].trim())
                            .append("\" (DISPONIBLE o NO_DISPONIBLE)\n");
                        continue;
                    }
                    String desc = p.length > 5 ? p[5].trim() : "";
                    try {
                        boolean guardado = busService.crear(placa, tipo, capacidad, color, estado, desc);
                        if (guardado) {
                            ok++;
                        } else {
                            err++;
                            detalles.append("Fila ").append(fila)
                                .append(": placa \"").append(placa).append("\" ya existe\n");
                        }
                    } catch (IllegalArgumentException e) {
                        err++;
                        detalles.append("Fila ").append(fila)
                            .append(": ").append(e.getMessage()).append("\n");
                    }
                } catch (Exception e) {
                    err++;
                    detalles.append("Fila ").append(fila)
                        .append(": error - ").append(e.getMessage()).append("\n");
                }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error leyendo archivo: " + e.getMessage());
            return;
        }
        String msg = "Importados: " + ok + " | Errores: " + err;
        if (detalles.length() > 0)
            msg += "\n\nDetalle de errores:\n" + detalles;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importacion completa", msg);
        cargarDatos();
    }

    /**
     * Abre un selector de archivo para guardar los buses actuales en un CSV
     * con BOM UTF-8 para compatibilidad con Excel. El encabezado y cada fila
     * siguen el formato: {@code Placa,Tipo,Capacidad,Color,Estado,Descripcion}.
     * Los buses se exportan en el orden de la lista doblemente enlazada (A-Z por placa).
     */
    private void exportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar FlotillaBuses.csv");
        fc.setInitialFileName("FlotillaBuses.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        try (PrintWriter pw = new PrintWriter(
                new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            pw.write('﻿'); // BOM UTF-8 para compatibilidad con Excel
            pw.println("Placa,Tipo,Capacidad,Color,Estado,Descripcion");
            NodoLista nodo = busService.getLista().getCabeza();
            while (nodo != null) {
                Bus b = (Bus) nodo.dato;
                pw.println(b.getPlaca() + "," + b.getTipo() + "," + b.getCapacidad()
                    + "," + b.getColor() + "," + b.getEstado() + "," + b.getDescripcion());
                nodo = nodo.siguiente;
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completa",
                "Archivo guardado: " + archivo.getName());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error escribiendo archivo: " + e.getMessage());
        }
    }
    
    /**
     * Muestra un dialogo de alerta modal con el tipo, titulo y mensaje indicados.
     *
     * @param tipo    tipo de alerta ({@code WARNING}, {@code ERROR}, {@code INFORMATION})
     * @param titulo  texto del titulo de la ventana
     * @param mensaje contenido del cuerpo del dialogo
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Copia los datos del bus seleccionado en la tabla hacia los controles
     * del formulario. Se invoca desde el evento {@code onMouseClicked}
     * de la tabla en el FXML.
     */
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
    
    /**
     * Pone todos los controles del formulario en modo solo lectura / deshabilitado.
     * Se llama al volver al estado {@code NINGUNO}.
     */
    public void desactivarControles(){
        txtPlaca.setEditable(false);
        cmbTipo.setDisable(true);
        txtCapacidad.setEditable(false);
        txtColor.setEditable(false);
        cmbEstado.setDisable(true);
        txtDescripcion.setEditable(false);
    }
    
    /**
     * Habilita todos los controles del formulario para edicion.
     * Se llama al iniciar una operacion de creacion.
     * En edicion, {@code editar()} bloquea placa y tipo despues de activar.
     */
    public void activarControles() {
        txtPlaca.setEditable(true);
        cmbTipo.setDisable(false);
        txtCapacidad.setEditable(true);
        txtColor.setEditable(true);
        cmbEstado.setDisable(false);
        txtDescripcion.setEditable(true);
    }
    
    /**
     * Vacia todos los campos de texto, resetea los ComboBox, limpia la
     * seleccion de la tabla y resetea los iconos Canvas y estilos CSS de validacion.
     */
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
        // Limpiar iconos y estilos
        limpiarIcono(txtPlaca,    icoPlaca);
        limpiarIcono(txtCapacidad, icoCapacidad);
        limpiarIcono(txtColor,    icoColor);
    }

    // =========================================================
    // HELPERS – Iconos Canvas + Validación
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

    private void limpiarIcono(TextField campo, IconoValidacion ico) {
        if (ico != null) ico.limpiar();
        if (campo != null) campo.getStyleClass().removeAll("campo-valido", "campo-invalido");
    }

    private void revalidarCampos() {
        revalidarCampo(txtCapacidad, icoCapacidad, ValidadorCampos::esCapacidadValida);
        revalidarCampo(txtColor,    icoColor,    ValidadorCampos::esColorValido);
        // Placa: no se edita al actualizar, limpiar su icono
        limpiarIcono(txtPlaca, icoPlaca);
    }

    private void revalidarCampo(TextField campo, IconoValidacion ico,
                                 Predicate<String> regla) {
        String val = campo.getText();
        if (val == null || val.isEmpty()) {
            limpiarIcono(campo, ico);
        } else if (regla.test(val)) {
            if (ico != null) ico.mostrarValido();
            campo.getStyleClass().removeAll("campo-invalido");
            if (!campo.getStyleClass().contains("campo-valido"))
                campo.getStyleClass().add("campo-valido");
        } else {
            if (ico != null) ico.mostrarInvalido();
            campo.getStyleClass().removeAll("campo-valido");
            if (!campo.getStyleClass().contains("campo-invalido"))
                campo.getStyleClass().add("campo-invalido");
        }
    }
}
