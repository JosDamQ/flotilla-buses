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
 * Controlador del modulo de asignaciones bus-destino (M4).
 *
 * <p>Implementa el patron MVC: gestiona la pantalla {@code asignacionView.fxml} y
 * delega la persistencia a {@link flotabuses.servicios.AsignacionBusDestinoService},
 * que opera sobre la {@link flotabuses.estructuras.MatrizOrtogonal}.</p>
 *
 * <p>Flujo de la maquina de estados CRUD:</p>
 * <pre>
 *   NINGUNO  --(nuevo())--> GUARDAR  --(nuevo())--> NINGUNO   (creado)
 *   NINGUNO  --(editar())--> ACTUALIZAR  --(editar())--> NINGUNO   (actualizado)
 *   NINGUNO  --(eliminar())--> confirmacion --> NINGUNO  (eliminado)
 *   GUARDAR | ACTUALIZAR --(eliminar()/reporte())--> NINGUNO  (cancelar)
 * </pre>
 *
 * <p>Particularidades de este modulo:</p>
 * <ul>
 *   <li>Solo aparecen en {@code cmbDestino} los destinos con estado {@code CONFIRMADO}
 *       ({@link flotabuses.servicios.DestinoService#obtenerConfirmados()}).</li>
 *   <li>El horario se compone de hora ({@code cmbHora}, rango 4-23) y minutos
 *       ({@code cmbMinutos}: 0, 15, 30, 45); se construye como {@link java.time.LocalTime}.</li>
 *   <li>En edicion, {@code cmbDestino} se bloquea (la celda en la matriz
 *       es [destino][bus] y no puede cambiar de fila). El bus si puede cambiarse
 *       mediante {@link flotabuses.servicios.AsignacionBusDestinoService#cambiarBus}.</li>
 *   <li>La hora original se guarda en {@link #horaOriginal} para restaurarla si
 *       la nueva hora es invalida durante la edicion.</li>
 *   <li>La tabla muestra objetos {@link flotabuses.servicios.AsignacionBusDestinoService.FilaAsignacion},
 *       que son filas aplanadas del recorrido de la matriz ortogonal.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.AsignacionBusDestinoService
 * @see flotabuses.modelos.AsignacionBusDestino
 * @see flotabuses.estructuras.MatrizOrtogonal
 * @see flotabuses.enums.Operaciones
 */
public class AsignacionBusDestinoController implements Initializable{

    /** Referencia a la aplicacion principal para la navegacion entre escenas. */
    private FlotaBuses escenarioPrincipal;

    /**
     * Estado actual de la maquina de estados CRUD.
     * Controla el comportamiento de los botones.
     */
    private Operaciones tipoOperacion = Operaciones.NINGUNO;

    /** Servicio Singleton que gestiona la matriz ortogonal de asignaciones. */
    private AsignacionBusDestinoService asignacionServicio = AsignacionBusDestinoService.getInstance();

    /** Servicio Singleton utilizado para poblar el ComboBox de destinos confirmados. */
    private DestinoService destinoServicio = DestinoService.getInstance();

    /** Servicio Singleton utilizado para poblar el ComboBox de buses disponibles. */
    private BusService busServicio = BusService.getInstance();

    /** Fila de la tabla que se esta editando actualmente; {@code null} si no hay edicion activa. */
    private FilaAsignacion filaEnEdicion;

    /**
     * Hora original de la asignacion antes de la edicion.
     * Se guarda para poder restaurarla si la nueva hora es invalida.
     */
    private LocalTime horaOriginal;
    
    /** Boton dual: "Nuevo" en estado NINGUNO / "Guardar" en estado GUARDAR. */
    @FXML private Button btnNuevo;

    /** Boton dual: "Eliminar" en estado NINGUNO / "Cancelar" en estado GUARDAR. */
    @FXML private Button btnEliminar;

    /** Boton dual: "Editar" en estado NINGUNO / "Actualizar" en estado ACTUALIZAR. */
    @FXML private Button btnEditar;

    /** Boton dual: "Reporte" en estado NINGUNO / "Cancelar" en estado ACTUALIZAR. */
    @FXML private Button btnReporte;

    /** Boton para importar/exportar CSV. Solo visible para el rol ADMIN. */
    @FXML private Button btnCSV;

    /** Icono del boton Nuevo; cambia entre Agregar.png y Save.png. */
    @FXML private ImageView imgNuevo;

    /** Icono del boton Eliminar; cambia entre Quitar.png y Cancel.png. */
    @FXML private ImageView imgEliminar;

    /** Icono del boton Editar; cambia entre Edit.png y Save.png. */
    @FXML private ImageView imgEditar;

    /** Icono del boton Reporte; cambia entre Reporte.png y Cancel.png. */
    @FXML private ImageView imgReporte;

    /** Icono del boton CSV. */
    @FXML private ImageView imgCSV;

    /**
     * ComboBox con los destinos confirmados disponibles para asignacion.
     * Se bloquea en modo edicion ya que el destino es la clave de fila en la matriz.
     */
    @FXML private ComboBox<Destino> cmbDestino;

    /** ComboBox con los buses disponibles ({@code DISPONIBLE}) para asignacion. */
    @FXML private ComboBox<Bus> cmbBus;

    /** ComboBox con las horas del dia (4 a 23) en formato entero. */
    @FXML private ComboBox<Integer> cmbHora;

    /** ComboBox con los minutos en incrementos de 15 (0, 15, 30, 45). */
    @FXML private ComboBox<Integer> cmbMinutos;

    /** Tabla que muestra las asignaciones aplanadas del recorrido de la matriz ortogonal. */
    @FXML private TableView<FilaAsignacion> tblAsignaciones;

    /** Columna del codigo de la asignacion. */
    @FXML private TableColumn<FilaAsignacion, Integer> colCodAsignacion;

    /** Columna del nombre del destino turistico asignado. */
    @FXML private TableColumn<FilaAsignacion, String> colNombreDestino;

    /** Columna de la fecha de salida del destino. */
    @FXML private TableColumn<FilaAsignacion, String> colFechaSalida;

    /** Columna de la placa del bus asignado. */
    @FXML private TableColumn<FilaAsignacion, String> colPlacaBus;

    /** Columna del tipo de bus asignado. */
    @FXML private TableColumn<FilaAsignacion, String> colTipo;

    /** Columna de la capacidad del bus asignado. */
    @FXML private TableColumn<FilaAsignacion, Integer> colCapacidad;

    /** Columna de la hora de salida registrada en la asignacion. */
    @FXML private TableColumn<FilaAsignacion, String> colHora;
    
    /**
     * Inicializa el controlador tras cargar el FXML.
     * Carga los destinos confirmados y los buses disponibles en los ComboBox,
     * construye la lista de horas (4-23) y la de minutos (0/15/30/45),
     * y carga los datos iniciales en la tabla.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
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
     * Carga las filas de asignaciones desde la matriz ortogonal y configura
     * las {@code PropertyValueFactory} de cada columna.
     * Se llama al inicializar y despues de cada operacion CRUD exitosa.
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → GUARDAR:</b> limpia y activa controles, cambia etiquetas e
     *       iconos y avanza el estado a {@code GUARDAR}.</li>
     *   <li><b>GUARDAR → NINGUNO:</b> valida que los cuatro campos (destino, bus,
     *       hora, minutos) esten seleccionados, construye un {@code LocalTime} y llama
     *       a {@link flotabuses.servicios.AsignacionBusDestinoService#guardar}.
     *       Interpreta los codigos de retorno: {@code 1} = destino no confirmado,
     *       {@code 2} = hora invalida o muy cercana, {@code 3} = bus no disponible
     *       en esa hora. En caso de exito regresa a {@code NINGUNO}.</li>
     * </ul>
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>GUARDAR → NINGUNO:</b> cancela la creacion en curso.</li>
     *   <li><b>NINGUNO (default):</b> solicita confirmacion al usuario y, si acepta,
     *       elimina la hora de la asignacion seleccionada mediante
     *       {@link flotabuses.servicios.AsignacionBusDestinoService#eliminarHora}.
     *       Si no hay ninguna fila seleccionada, muestra una advertencia.</li>
     * </ul>
     */
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → ACTUALIZAR:</b> guarda la fila en {@link #filaEnEdicion} y
     *       la hora original en {@link #horaOriginal}, activa controles, bloquea
     *       {@code cmbDestino} y avanza el estado a {@code ACTUALIZAR}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> elimina la hora original de la asignacion,
     *       intenta guardar la nueva hora mediante
     *       {@link flotabuses.servicios.AsignacionBusDestinoService#guardar}.
     *       Si la nueva hora es invalida ({@code resEdit == 2}), restaura la original.
     *       Si el bus cambio, invoca
     *       {@link flotabuses.servicios.AsignacionBusDestinoService#cambiarBus}.
     *       En caso de exito, restaura la interfaz y regresa a {@code NINGUNO}.</li>
     * </ul>
     */
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
                    //cmbBus.setDisable(true);
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
                
                Bus busNuevo = cmbBus.getValue();
                Bus busOriginal = busServicio.buscarPorPlaca(filaEnEdicion.getPlacaBus());
                
                if (!busNuevo.getPlaca().equals(busOriginal.getPlaca())) {
                    // Obtener la asignación de la matriz
                    AsignacionBusDestino asig = asignacionServicio.buscar(destino, busOriginal);
                    if (asig != null) {
                        // Mover la celda en la matriz al nuevo bus
                        asignacionServicio.cambiarBus(destino, busOriginal, busNuevo);
                    }
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
    
    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO:</b> muestra un dialogo para elegir formato (PDF o HTML)
     *       y delega en {@link flotabuses.servicios.ReporteService}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> cancela la edicion en curso, limpia
     *       {@link #filaEnEdicion} y {@link #horaOriginal} y restaura la interfaz.</li>
     * </ul>
     */
    public void reporte(){
        switch (tipoOperacion) {
            case NINGUNO:
                ButtonType btnPdf  = new ButtonType("PDF");
                ButtonType btnHtml = new ButtonType("HTML");
                ButtonType btnCan  = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert fmtAlert = new Alert(Alert.AlertType.CONFIRMATION);
                fmtAlert.setTitle("Reporte de Asignaciones");
                fmtAlert.setHeaderText(null);
                fmtAlert.setContentText("Seleccione el formato del reporte:");
                fmtAlert.getButtonTypes().setAll(btnPdf, btnHtml, btnCan);
                Optional<ButtonType> res = fmtAlert.showAndWait();
                if (res.isPresent() && res.get() != btnCan) {
                    if (res.get() == btnPdf) ReporteService.getInstance().reporteAsignacionesPdf();
                    else                     ReporteService.getInstance().reporteAsignacionesHtml();
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
                filaEnEdicion = null;
                horaOriginal  = null;
                tipoOperacion = Operaciones.NINGUNO;
                break;
        }
    }
    
    /**
     * Muestra un dialogo de confirmacion para elegir entre importar o exportar
     * asignaciones en formato CSV.
     * Solo visible para usuarios con rol {@code ADMIN}.
     */
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

    /**
     * Abre un selector de archivo para elegir un CSV de asignaciones y lo importa
     * fila por fila a la matriz ortogonal. El formato esperado es:
     * {@code Codigo_Asig,Codigo_Destino,Nombre_Destino,Fecha_salida,Placa_bus,Tipo_bus,Capacidad_bus,Hora}.
     * Valida que el destino exista en el sistema, que el bus exista por placa y que
     * la hora tenga formato {@code HH:mm}. Al finalizar muestra un resumen
     * con importadas y errores detallados.
     */
    private void importarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importar Asignaciones CSV");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        int importados = 0, errores = 0, fila = 1;
        StringBuilder detalles = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new FileReader(archivo, StandardCharsets.UTF_8))) {
            br.readLine(); // saltar encabezado
            String linea;
            while ((linea = br.readLine()) != null) {
                fila++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                // Codigo_Asig,Codigo_Destino,Nombre_Destino,Fecha_salida,Placa_bus,Tipo_bus,Capacidad_bus,Hora
                String[] p = linea.split(",", -1);
                if (p.length < 8) {
                    errores++;
                    detalles.append("Fila ").append(fila)
                        .append(": columnas insuficientes (se esperan 8)\n");
                    continue;
                }
                try {
                    if (p[2].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Nombre_Destino' está vacío\n");
                        continue;
                    }
                    if (p[4].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Placa_bus' está vacío\n");
                        continue;
                    }
                    if (p[7].trim().isEmpty()) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": campo 'Hora' está vacío\n");
                        continue;
                    }
                    flotabuses.enums.NombreDestino nombreDestino = null;
                    for (flotabuses.enums.NombreDestino nd : flotabuses.enums.NombreDestino.values()) {
                        if (nd.getNombreMostrar().equalsIgnoreCase(p[2].trim())) {
                            nombreDestino = nd; break;
                        }
                    }
                    if (nombreDestino == null) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": destino desconocido \"").append(p[2].trim()).append("\"\n");
                        continue;
                    }
                    Destino destino = destinoServicio.buscarPorNombre(nombreDestino);
                    if (destino == null) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": destino \"").append(p[2].trim())
                            .append("\" no registrado en el sistema\n");
                        continue;
                    }
                    Bus bus = busServicio.buscarPorPlaca(p[4].trim());
                    if (bus == null) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": bus con placa \"").append(p[4].trim())
                            .append("\" no encontrado\n");
                        continue;
                    }
                    LocalTime hora;
                    try { hora = LocalTime.parse(p[7].trim()); }
                    catch (Exception e) {
                        errores++;
                        detalles.append("Fila ").append(fila)
                            .append(": hora invalida \"").append(p[7].trim())
                            .append("\" (formato HH:mm)\n");
                        continue;
                    }
                    int res = asignacionServicio.guardar(destino, bus, hora);
                    if (res == 0) {
                        importados++;
                    } else {
                        errores++;
                        String motivo = res == 1 ? "destino no confirmado"
                                      : res == 2 ? "hora invalida o muy cercana a otra asignada"
                                      : "bus no disponible en esa hora";
                        detalles.append("Fila ").append(fila)
                            .append(": no se pudo asignar (").append(motivo).append(")\n");
                    }
                } catch (Exception e) {
                    errores++;
                    detalles.append("Fila ").append(fila)
                        .append(": error inesperado - ").append(e.getMessage()).append("\n");
                }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                "No se pudo leer el archivo: " + e.getMessage());
            return;
        }
        cargarDatos();
        String msg = "Importadas: " + importados + "  |  Errores: " + errores;
        if (detalles.length() > 0)
            msg += "\n\nDetalle de errores:\n" + detalles;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importacion completa", msg);
    }

    /**
     * Abre un selector de archivo para guardar las asignaciones actuales en un CSV
     * con BOM UTF-8. El encabezado es:
     * {@code Código_Asig,Código_Destino,Nombre_Destino,Fecha_salida_destino,Placa_bus,Tipo_bus,Capacidad_bus,Hora_asignación}.
     * Recorre la matriz ortogonal celda por celda y, para cada asignacion,
     * genera una fila por cada hora registrada en la lista doblemente enlazada.
     */
    private void exportarCSV() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportar Asignaciones CSV");
        chooser.setInitialFileName("AsigBusDestino.csv");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = chooser.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;

        try (PrintWriter pw = new PrintWriter(
                new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            pw.write('﻿'); // BOM UTF-8 para compatibilidad con Excel
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
    
    /**
     * Busca un valor del enum {@link flotabuses.enums.NombreDestino} cuyo
     * {@code getNombreMostrar()} coincida exactamente con el texto dado.
     *
     * @param nombreMostrar texto del destino tal como aparece en la lista/tabla
     * @return el {@code NombreDestino} correspondiente, o {@code null} si no se encuentra
     */
    private flotabuses.enums.NombreDestino buscarNombreDestino(String nombreMostrar) {
        for (flotabuses.enums.NombreDestino nd : flotabuses.enums.NombreDestino.values()) {
            if (nd.getNombreMostrar().equals(nombreMostrar)) return nd;
        }
        return null;
    }
    
    /**
     * Muestra un dialogo de alerta modal con el tipo, titulo y mensaje indicados.
     *
     * @param tipo    tipo de alerta ({@code WARNING}, {@code ERROR}, {@code INFORMATION})
     * @param titulo  texto del titulo de la ventana
     * @param mensaje contenido del cuerpo del dialogo
     */
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Copia los datos de la fila de asignacion seleccionada hacia los controles
     * del formulario. Se invoca desde el evento {@code onMouseClicked} de la tabla.
     */
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
    
    /**
     * Deshabilita todos los controles del formulario (ComboBox).
     * Se llama al volver al estado {@code NINGUNO}.
     */
    public void desactivarControles(){
        cmbDestino.setDisable(true);
        cmbBus.setDisable(true);
        cmbHora.setDisable(true);
        cmbMinutos.setDisable(true);
    }
    
    /**
     * Habilita todos los controles del formulario (ComboBox).
     * En modo edicion, {@code editar()} bloquea {@code cmbDestino} inmediatamente
     * despues de activar.
     */
    public void activarControles(){
        cmbDestino.setDisable(false);
        cmbBus.setDisable(false);
        cmbHora.setDisable(false);
        cmbMinutos.setDisable(false);
    }
    
    /**
     * Vacia todos los ComboBox del formulario y limpia la seleccion de la tabla.
     */
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
