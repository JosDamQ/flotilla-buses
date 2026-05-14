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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
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
 * Controlador del modulo de gestion de clientes (M1).
 *
 * <p>Implementa el patron MVC: gestiona la pantalla {@code clientesView.fxml} y
 * delega la persistencia a {@link flotabuses.servicios.ClienteService}.</p>
 *
 * <p>Opera como una maquina de estados a traves de {@link #tipoOperacion}.
 * Los flujos CRUD son:</p>
 * <pre>
 *   NINGUNO  --(nuevo())--> GUARDAR  --(nuevo())--> NINGUNO   (registro creado)
 *   NINGUNO  --(editar())--> ACTUALIZAR  --(editar())--> NINGUNO   (registro actualizado)
 *   NINGUNO  --(eliminar())-->  confirmacion --> NINGUNO  (registro eliminado)
 *   GUARDAR | ACTUALIZAR --(eliminar()/reporte())--> NINGUNO  (cancelar)
 * </pre>
 *
 * <p>Cada campo de texto cuenta con un {@link IconoValidacion} (Canvas) inyectado
 * dinamicamente en el {@code GridPane} mediante {@link #envolver(GridPane, TextField)}.
 * Los listeners de {@link #configurarValidacion} actualizan el icono y el estilo CSS
 * en tiempo real mientras el usuario escribe.</p>
 *
 * <p>El boton CSV solo es visible para el rol {@code ADMIN}; se oculta automaticamente
 * en {@link #setEscenarioPrincipal(FlotaBuses)} si el usuario es {@code OPERADOR}.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.ClienteService
 * @see flotabuses.modelos.Cliente
 * @see flotabuses.utils.ValidadorCampos
 * @see flotabuses.enums.Operaciones
 */
public class ClienteController implements Initializable {

    /** Referencia a la aplicacion principal para la navegacion entre escenas. */
    private FlotaBuses escenarioPrincipal;

    /**
     * Estado actual de la maquina de estados CRUD.
     * Controla el comportamiento de los botones Nuevo, Eliminar, Editar y Reporte.
     */
    private Operaciones tipoOperacion = Operaciones.NINGUNO;

    /** Servicio Singleton que gestiona el arbol de clientes. */
    private ClienteService clienteServicio = ClienteService.getInstance();

    // ── FXML Controles ────────────────────────────────────
    /** Boton dual: "Nuevo" en estado NINGUNO / "Guardar" en estado GUARDAR. */
    @FXML private Button  btnNuevo;

    /** Boton dual: "Eliminar" en estado NINGUNO / "Cancelar" en estado GUARDAR. */
    @FXML private Button  btnEliminar;

    /** Boton dual: "Editar" en estado NINGUNO / "Actualizar" en estado ACTUALIZAR. */
    @FXML private Button  btnEditar;

    /** Boton dual: "Reporte" en estado NINGUNO / "Cancelar" en estado ACTUALIZAR. */
    @FXML private Button  btnReporte;

    /** Boton para importar/exportar CSV. Solo visible para el rol ADMIN. */
    @FXML private Button  btnCSV;

    /** Icono del boton Nuevo; cambia entre Agregar.png y Save.png segun el estado. */
    @FXML private ImageView imgNuevo;

    /** Icono del boton Eliminar; cambia entre Quitar.png y Cancel.png segun el estado. */
    @FXML private ImageView imgEliminar;

    /** Icono del boton Editar; cambia entre Edit.png y Save.png segun el estado. */
    @FXML private ImageView imgEditar;

    /** Icono del boton Reporte; cambia entre Reporte.png y Cancel.png segun el estado. */
    @FXML private ImageView imgReporte;

    /** Icono del boton CSV. */
    @FXML private ImageView imgCSV;

    /** Tabla que muestra todos los clientes registrados en orden ascendente por codigo. */
    @FXML private TableView<Cliente>            tblClientes;

    /** Columna del codigo de cliente (clave del arbol BST). */
    @FXML private TableColumn<Cliente, Integer> colCodigoCliente;

    /** Columna del nombre de pila del cliente. */
    @FXML private TableColumn<Cliente, String>  colNombre;

    /** Columna del apellido del cliente. */
    @FXML private TableColumn<Cliente, String>  colApellido;

    /** Columna del DPI/CUI del cliente (13 digitos). */
    @FXML private TableColumn<Cliente, String>  colDPI;

    /** Columna del correo electronico del cliente. */
    @FXML private TableColumn<Cliente, String>  colEmail;

    /** Columna de la contrasena del cliente. */
    @FXML private TableColumn<Cliente, String>  colPassword;

    /** Columna del telefono del cliente (8 digitos). */
    @FXML private TableColumn<Cliente, String>  colTelefono;

    /** Campo de texto para el nombre del cliente. Solo letras, tildes y espacios. */
    @FXML private TextField txtNombre;

    /** Campo de texto para el apellido del cliente. Solo letras, tildes y espacios. */
    @FXML private TextField txtApellido;

    /** Campo de texto para el DPI/CUI. TextFormatter restringe a 13 digitos. */
    @FXML private TextField txtDPI;

    /** Campo de texto para el correo electronico del cliente. */
    @FXML private TextField txtEmail;

    /** Campo de texto para la contrasena del cliente (minimo 6 caracteres). */
    @FXML private TextField txtPassword;

    /** Campo de texto para el telefono. TextFormatter restringe a 8 digitos. */
    @FXML private TextField txtTelefono;

    /** Contenedor GridPane donde se inyectan los iconos de validacion Canvas. */
    @FXML private GridPane gridDatos;

    // ── Iconos de validación (Canvas) ────────────────────
    /** Icono Canvas que indica validez del campo nombre en tiempo real. */
    private IconoValidacion icoNombre;

    /** Icono Canvas que indica validez del campo apellido en tiempo real. */
    private IconoValidacion icoApellido;

    /** Icono Canvas que indica validez del campo DPI en tiempo real. */
    private IconoValidacion icoDPI;

    /** Icono Canvas que indica validez del campo email en tiempo real. */
    private IconoValidacion icoEmail;

    /** Icono Canvas que indica validez del campo password en tiempo real. */
    private IconoValidacion icoPassword;

    /** Icono Canvas que indica validez del campo telefono en tiempo real. */
    private IconoValidacion icoTelefono;

    // =========================================================
    // INITIALIZE
    // =========================================================

    /**
     * Inicializa el controlador tras cargar el FXML.
     * Aplica TextFormatters a los campos numericos, inyecta los iconos Canvas
     * de validacion en el GridPane, registra los listeners en tiempo real y
     * carga los datos iniciales en la tabla.
     *
     * @param location  URL del archivo FXML (no utilizado)
     * @param resources paquete de recursos de internacionalizacion (no utilizado)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Restricciones de entrada (TextFormatter)
        aplicarFormatterSoloDigitos(txtDPI, 13);
        aplicarFormatterSoloDigitos(txtTelefono, 8);

        // Crear e inyectar iconos Canvas en el GridPane
        icoNombre   = envolver(gridDatos, txtNombre);
        icoApellido = envolver(gridDatos, txtApellido);
        icoDPI      = envolver(gridDatos, txtDPI);
        icoEmail    = envolver(gridDatos, txtEmail);
        icoPassword = envolver(gridDatos, txtPassword);
        icoTelefono = envolver(gridDatos, txtTelefono);

        // Registrar validaciones en tiempo real
        configurarValidacion(txtNombre,   icoNombre,   ValidadorCampos::esNombreValido,   ValidadorCampos.mensajeNombre());
        configurarValidacion(txtApellido, icoApellido, ValidadorCampos::esNombreValido,   ValidadorCampos.mensajeNombre());
        configurarValidacion(txtDPI,      icoDPI,      ValidadorCampos::esDpiValido,      ValidadorCampos.mensajeDpi());
        configurarValidacion(txtEmail,    icoEmail,    ValidadorCampos::esEmailValido,    ValidadorCampos.mensajeEmail());
        configurarValidacion(txtPassword, icoPassword, ValidadorCampos::esPasswordValida, ValidadorCampos.mensajePassword());
        configurarValidacion(txtTelefono, icoTelefono, ValidadorCampos::esTelefonoValido, ValidadorCampos.mensajeTelefono());

        cargarDatos();
    }

    // =========================================================
    // ESCENARIO PRINCIPAL
    // =========================================================

    /**
     * Retorna la instancia principal de la aplicacion.
     *
     * @return referencia a {@link FlotaBuses}
     */
    public FlotaBuses getEscenarioPrincipal() { return escenarioPrincipal; }

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
    public void menuPrincipal() {
        escenarioPrincipal.menuPrincipal();
    }

    // =========================================================
    // CARGAR DATOS
    // =========================================================

    /**
     * Carga todos los clientes del servicio en la tabla y configura las
     * {@code PropertyValueFactory} de cada columna.
     * Se llama al inicializar y despues de cada operacion CRUD exitosa.
     */
    public void cargarDatos() {
        tblClientes.setItems(clienteServicio.obtenerTodosAscendente());
        colCodigoCliente.setCellValueFactory(new PropertyValueFactory<>("codigoCliente"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colApellido.setCellValueFactory(new PropertyValueFactory<>("apellido"));
        colDPI.setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
    }

    // =========================================================
    // NUEVO / GUARDAR
    // =========================================================

    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → GUARDAR:</b> limpia y activa los controles, cambia etiquetas
     *       e iconos de los botones y avanza el estado a {@code GUARDAR}.</li>
     *   <li><b>GUARDAR → NINGUNO:</b> valida todos los campos (vacios y formato
     *       guatemalteco), invoca {@link flotabuses.servicios.ClienteService#crear} y,
     *       si el resultado es exitoso ({@code 0}), desactiva controles y regresa a
     *       {@code NINGUNO}. Muestra alertas si el DPI ({@code 1}) o el email
     *       ({@code 2}) ya existen.</li>
     * </ul>
     */
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
                // Validar campos vacíos
                if (txtNombre.getText().trim().isEmpty()   ||
                    txtApellido.getText().trim().isEmpty()  ||
                    txtDPI.getText().trim().isEmpty()       ||
                    txtEmail.getText().trim().isEmpty()     ||
                    txtPassword.getText().trim().isEmpty()  ||
                    txtTelefono.getText().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING,
                        "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }

                // Validar formato guatemalteco
                if (!ValidadorCampos.esNombreValido(txtNombre.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Nombre inválido", ValidadorCampos.mensajeNombre()); return;
                }
                if (!ValidadorCampos.esNombreValido(txtApellido.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Apellido inválido", ValidadorCampos.mensajeNombre()); return;
                }
                if (!ValidadorCampos.esDpiValido(txtDPI.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "DPI inválido", ValidadorCampos.mensajeDpi()); return;
                }
                if (!ValidadorCampos.esEmailValido(txtEmail.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Email inválido", ValidadorCampos.mensajeEmail()); return;
                }
                if (!ValidadorCampos.esPasswordValida(txtPassword.getText())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Contraseña inválida", ValidadorCampos.mensajePassword()); return;
                }
                if (!ValidadorCampos.esTelefonoValido(txtTelefono.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido", ValidadorCampos.mensajeTelefono()); return;
                }

                int resultado = clienteServicio.crear(
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDPI.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPassword.getText().trim(),
                    txtTelefono.getText().trim()
                );

                if (resultado == 1) { mostrarAlerta(Alert.AlertType.WARNING, "DPI duplicado",   "Ya existe un cliente con ese DPI."); return; }
                if (resultado == 2) { mostrarAlerta(Alert.AlertType.WARNING, "Email duplicado", "Ya existe un cliente con ese email."); return; }

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

    // =========================================================
    // ELIMINAR / CANCELAR
    // =========================================================

    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>GUARDAR → NINGUNO:</b> cancela la creacion en curso, restaura
     *       etiquetas e iconos originales y regresa a {@code NINGUNO}.</li>
     *   <li><b>NINGUNO (default):</b> solicita confirmacion al usuario y, si acepta,
     *       elimina el cliente seleccionado en la tabla mediante
     *       {@link flotabuses.servicios.ClienteService#eliminar}.
     *       Si no hay ningun elemento seleccionado, muestra una advertencia.</li>
     * </ul>
     */
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
                        seleccionado.getNombreCompleto() + "?");
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
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debes seleccionar un cliente primero.");
                }
        }
    }

    // =========================================================
    // EDITAR / ACTUALIZAR
    // =========================================================

    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO → ACTUALIZAR:</b> verifica que haya un cliente seleccionado,
     *       activa controles, cambia etiquetas e iconos y fuerza la revalidacion de
     *       los campos con los datos actuales mediante {@link #revalidarCampos()}.
     *       Avanza el estado a {@code ACTUALIZAR}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> valida campos (vacios y formato), invoca
     *       {@link flotabuses.servicios.ClienteService#actualizar} y, si el resultado
     *       es exitoso ({@code 0}), restaura la interfaz y regresa a {@code NINGUNO}.
     *       Muestra alertas si el DPI ({@code 2}) o el email ({@code 3}) estan
     *       duplicados en otro cliente.</li>
     * </ul>
     */
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
                    revalidarCampos();
                    tipoOperacion = Operaciones.ACTUALIZAR;
                } else {
                    mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Debes seleccionar un cliente primero.");
                }
                break;

            case ACTUALIZAR:
                if (txtNombre.getText().trim().isEmpty()   ||
                    txtApellido.getText().trim().isEmpty()  ||
                    txtDPI.getText().trim().isEmpty()       ||
                    txtEmail.getText().trim().isEmpty()     ||
                    txtPassword.getText().trim().isEmpty()  ||
                    txtTelefono.getText().trim().isEmpty()) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Debes ingresar todos los datos.");
                    return;
                }

                if (!ValidadorCampos.esNombreValido(txtNombre.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Nombre inválido", ValidadorCampos.mensajeNombre()); return;
                }
                if (!ValidadorCampos.esNombreValido(txtApellido.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Apellido inválido", ValidadorCampos.mensajeNombre()); return;
                }
                if (!ValidadorCampos.esDpiValido(txtDPI.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "DPI inválido", ValidadorCampos.mensajeDpi()); return;
                }
                if (!ValidadorCampos.esEmailValido(txtEmail.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Email inválido", ValidadorCampos.mensajeEmail()); return;
                }
                if (!ValidadorCampos.esPasswordValida(txtPassword.getText())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Contraseña inválida", ValidadorCampos.mensajePassword()); return;
                }
                if (!ValidadorCampos.esTelefonoValido(txtTelefono.getText().trim())) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Teléfono inválido", ValidadorCampos.mensajeTelefono()); return;
                }

                Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
                int resActualizar = clienteServicio.actualizar(
                    seleccionado.getCodigoCliente(),
                    txtNombre.getText().trim(),
                    txtApellido.getText().trim(),
                    txtDPI.getText().trim(),
                    txtEmail.getText().trim(),
                    txtPassword.getText().trim(),
                    txtTelefono.getText().trim()
                );

                if (resActualizar == 2) { mostrarAlerta(Alert.AlertType.WARNING, "DPI duplicado",   "Ya existe otro cliente con ese DPI."); return; }
                if (resActualizar == 3) { mostrarAlerta(Alert.AlertType.WARNING, "Email duplicado", "Ya existe otro cliente con ese email."); return; }

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

    // =========================================================
    // REPORTE
    // =========================================================

    /**
     * Boton dual que actua segun el estado de {@link #tipoOperacion}:
     * <ul>
     *   <li><b>NINGUNO:</b> muestra dos dialogos de confirmacion encadenados:
     *       primero para elegir el formato (PDF o HTML) y luego el orden
     *       (Ascendente o Descendente). Delega la generacion en
     *       {@link flotabuses.servicios.ReporteService}.</li>
     *   <li><b>ACTUALIZAR → NINGUNO:</b> cancela la edicion en curso y
     *       restaura la interfaz al estado {@code NINGUNO}.</li>
     * </ul>
     */
    public void reporte() {
        switch (tipoOperacion) {
            case NINGUNO:
                ButtonType btnPdf  = new ButtonType("PDF");
                ButtonType btnHtml = new ButtonType("HTML");
                ButtonType btnCan0 = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert fmtAlert = new Alert(Alert.AlertType.CONFIRMATION);
                fmtAlert.setTitle("Reporte de Clientes");
                fmtAlert.setHeaderText(null);
                fmtAlert.setContentText("Seleccione el formato del reporte:");
                fmtAlert.getButtonTypes().setAll(btnPdf, btnHtml, btnCan0);
                Optional<ButtonType> fmtRes = fmtAlert.showAndWait();
                if (!fmtRes.isPresent() || fmtRes.get() == btnCan0) { limpiarControles(); break; }
                boolean esPdf = fmtRes.get() == btnPdf;

                ButtonType btnAsc  = new ButtonType("Ascendente");
                ButtonType btnDesc = new ButtonType("Descendente");
                ButtonType btnCan  = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
                Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
                alerta.setTitle("Reporte de Clientes");
                alerta.setHeaderText(null);
                alerta.setContentText("Seleccione el orden por código:");
                alerta.getButtonTypes().setAll(btnAsc, btnDesc, btnCan);
                Optional<ButtonType> res = alerta.showAndWait();
                if (res.isPresent() && res.get() != btnCan) {
                    boolean asc = res.get() == btnAsc;
                    if (esPdf) ReporteService.getInstance().reporteClientesPdf(asc);
                    else       ReporteService.getInstance().reporteClientesHtml(asc);
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

    // =========================================================
    // CSV
    // =========================================================

    /**
     * Muestra un dialogo de confirmacion para que el usuario elija entre
     * importar o exportar clientes en formato CSV.
     * Delega en {@link #importarCSV()} o {@link #exportarCSV()} segun la
     * eleccion. Solo visible para usuarios con rol {@code ADMIN}.
     */
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

    /**
     * Abre un selector de archivo para elegir un CSV de clientes y lo importa
     * fila por fila al arbol de clientes. El formato esperado es:
     * {@code Código,Nombre,Identificación,Contraseña,Correo,Teléfono}.
     * Valida formato de DPI, email y telefono en cada fila; al finalizar muestra
     * un resumen con la cantidad de registros importados y los errores detallados.
     */
    private void importarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleccionar Clientes.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showOpenDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        int ok = 0, err = 0, fila = 1;
        StringBuilder detalles = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String encabezado = br.readLine(); // saltar encabezado
            // Detectar si el CSV tiene columna de teléfono (col 5)
            boolean tienetelefono = encabezado != null && encabezado.split(",", -1).length >= 6;
            String linea;
            while ((linea = br.readLine()) != null) {
                fila++;
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                // Quitar BOM si lo hubiera en primera línea de datos
                if (linea.startsWith("﻿")) linea = linea.substring(1);
                String[] p = linea.split(",", -1);
                if (p.length < 5) {
                    err++;
                    detalles.append("Fila ").append(fila).append(": columnas insuficientes (se esperan 5)\n");
                    continue;
                }
                try {
                    // Formato: Codigo,NombreCompleto,DPI,Password,Email[,Telefono]
                    String nombreCompleto = p[1].trim();
                    String dpi           = p[2].trim();
                    String password      = p[3].trim();
                    String email         = p[4].trim();
                    String telefono      = (tienetelefono && p.length > 5) ? p[5].trim() : "";

                    if (nombreCompleto.isEmpty()) { err++; detalles.append("Fila ").append(fila).append(": campo 'Nombre' vacío\n"); continue; }
                    if (dpi.isEmpty())            { err++; detalles.append("Fila ").append(fila).append(": campo 'DPI' vacío\n"); continue; }
                    if (password.isEmpty())       { err++; detalles.append("Fila ").append(fila).append(": campo 'Contraseña' vacío\n"); continue; }
                    if (email.isEmpty())          { err++; detalles.append("Fila ").append(fila).append(": campo 'Correo' vacío\n"); continue; }

                    // Validaciones de formato
                    if (!ValidadorCampos.esDpiValido(dpi)) {
                        err++; detalles.append("Fila ").append(fila).append(": DPI inválido \"").append(dpi).append("\" (debe tener 13 dígitos)\n"); continue;
                    }
                    if (!ValidadorCampos.esEmailValido(email)) {
                        err++; detalles.append("Fila ").append(fila).append(": email inválido \"").append(email).append("\"\n"); continue;
                    }
                    if (!telefono.isEmpty() && !ValidadorCampos.esTelefonoValido(telefono)) {
                        err++; detalles.append("Fila ").append(fila).append(": teléfono inválido \"").append(telefono).append("\" (8 dígitos, inicia 2-7)\n"); continue;
                    }

                    String[] partes = nombreCompleto.split(" ", 2);
                    String n = partes[0].trim();
                    String a = partes.length > 1 ? partes[1].trim() : "";

                    int resultado = clienteServicio.crear(n, a, dpi, email, password, telefono);
                    if (resultado == 0) {
                        ok++;
                    } else if (resultado == 1) {
                        err++; detalles.append("Fila ").append(fila).append(": DPI \"").append(dpi).append("\" ya existe\n");
                    } else {
                        err++; detalles.append("Fila ").append(fila).append(": email \"").append(email).append("\" ya existe\n");
                    }
                } catch (Exception e) {
                    err++; detalles.append("Fila ").append(fila).append(": error - ").append(e.getMessage()).append("\n");
                }
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error leyendo archivo: " + e.getMessage());
            return;
        }
        String msg = "Importados: " + ok + " | Errores: " + err;
        if (detalles.length() > 0) msg += "\n\nDetalle de errores:\n" + detalles;
        mostrarAlerta(Alert.AlertType.INFORMATION, "Importación completa", msg);
        cargarDatos();
    }

    /**
     * Abre un selector de archivo para guardar los clientes actuales en un CSV
     * con BOM UTF-8 para compatibilidad con Excel. El encabezado y cada fila
     * siguen el formato: {@code Código,Nombre,Identificación,Contraseña,Correo,Teléfono}.
     * Los clientes se exportan en orden ascendente por codigo.
     */
    private void exportarCSV() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Guardar Clientes.csv");
        fc.setInitialFileName("Clientes.csv");
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File archivo = fc.showSaveDialog(escenarioPrincipal.getStage());
        if (archivo == null) return;
        try (PrintWriter pw = new PrintWriter(
                new java.io.OutputStreamWriter(
                    new java.io.FileOutputStream(archivo), StandardCharsets.UTF_8))) {
            pw.write('﻿'); // BOM UTF-8 para Excel
            pw.println("Código,Nombre,Identificación,Contraseña,Correo,Teléfono");
            for (Cliente c : clienteServicio.obtenerTodosAscendente()) {
                pw.println(c.getCodigoCliente() + "," + c.getNombreCompleto()
                    + "," + c.getDpi() + "," + c.getPassword()
                    + "," + c.getEmail() + "," + (c.getTelefono() != null ? c.getTelefono() : ""));
            }
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exportación completa",
                "Archivo guardado: " + archivo.getName());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error escribiendo archivo: " + e.getMessage());
        }
    }

    // =========================================================
    // AUXILIARES DE LA TABLA
    // =========================================================

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
     * Copia los datos del cliente seleccionado en la tabla hacia los campos
     * de texto del formulario. Se invoca desde el evento {@code onMouseClicked}
     * de la tabla en el FXML.
     */
    public void seleccionarElemento() {
        Cliente seleccionado = tblClientes.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            txtNombre.setText(seleccionado.getNombre());
            txtApellido.setText(seleccionado.getApellido());
            txtDPI.setText(seleccionado.getDpi());
            txtEmail.setText(seleccionado.getEmail());
            txtPassword.setText(seleccionado.getPassword());
            txtTelefono.setText(seleccionado.getTelefono() != null ? seleccionado.getTelefono() : "");
        }
    }

    /**
     * Pone todos los campos de texto en modo solo lectura ({@code editable = false}).
     * Se llama al volver al estado {@code NINGUNO} despues de guardar, cancelar
     * o completar una actualizacion.
     */
    public void desactivarControles() {
        txtNombre.setEditable(false);
        txtApellido.setEditable(false);
        txtDPI.setEditable(false);
        txtEmail.setEditable(false);
        txtPassword.setEditable(false);
        txtTelefono.setEditable(false);
    }

    /**
     * Habilita todos los campos de texto para edicion ({@code editable = true}).
     * Se llama al iniciar una operacion de creacion o edicion.
     */
    public void activarControles() {
        txtNombre.setEditable(true);
        txtApellido.setEditable(true);
        txtDPI.setEditable(true);
        txtEmail.setEditable(true);
        txtPassword.setEditable(true);
        txtTelefono.setEditable(true);
    }

    /**
     * Vacia todos los campos de texto, limpia la seleccion de la tabla y
     * resetea los iconos Canvas y los estilos CSS de validacion de cada campo.
     */
    public void limpiarControles() {
        txtNombre.clear();
        txtApellido.clear();
        txtDPI.clear();
        txtEmail.clear();
        txtPassword.clear();
        txtTelefono.clear();
        tblClientes.getSelectionModel().clearSelection();
        // Limpiar iconos y estilos
        limpiarIcono(txtNombre,   icoNombre);
        limpiarIcono(txtApellido, icoApellido);
        limpiarIcono(txtDPI,      icoDPI);
        limpiarIcono(txtEmail,    icoEmail);
        limpiarIcono(txtPassword, icoPassword);
        limpiarIcono(txtTelefono, icoTelefono);
    }

    // =========================================================
    // HELPERS – Iconos Canvas + Validación
    // =========================================================

    /**
     * Extrae el TextField del GridPane, lo envuelve en un HBox junto a un
     * IconoValidacion (Canvas) y lo re-inserta en la misma posición.
     * Devuelve el icono para ser almacenado y actualizado.
     */
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

    /**
     * Registra un listener en el TextField que actualiza el icono Canvas
     * y el estilo CSS en tiempo real mientras el usuario escribe.
     * Si el campo no está editable (modo lectura), se ignora.
     */
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

    /** Limpia el icono y los estilos CSS de un campo. */
    private void limpiarIcono(TextField campo, IconoValidacion ico) {
        if (ico != null) ico.limpiar();
        if (campo != null) campo.getStyleClass().removeAll("campo-valido", "campo-invalido");
    }

    /** Fuerza la revalidación de todos los campos (útil al activar modo edición). */
    private void revalidarCampos() {
        revalidarCampo(txtNombre,   icoNombre,   ValidadorCampos::esNombreValido);
        revalidarCampo(txtApellido, icoApellido, ValidadorCampos::esNombreValido);
        revalidarCampo(txtDPI,      icoDPI,      ValidadorCampos::esDpiValido);
        revalidarCampo(txtEmail,    icoEmail,    ValidadorCampos::esEmailValido);
        revalidarCampo(txtPassword, icoPassword, ValidadorCampos::esPasswordValida);
        revalidarCampo(txtTelefono, icoTelefono, ValidadorCampos::esTelefonoValido);
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

    /** TextFormatter que solo admite dígitos hasta maxLen caracteres. */
    private void aplicarFormatterSoloDigitos(TextField campo, int maxLen) {
        campo.setTextFormatter(new TextFormatter<>(change -> {
            String nuevoTexto = change.getControlNewText();
            if (nuevoTexto.matches("\\d{0," + maxLen + "}")) return change;
            return null;
        }));
    }
}
