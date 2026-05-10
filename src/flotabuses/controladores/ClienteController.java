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
 *
 * @author damiangarcia
 */
public class ClienteController implements Initializable {

    private FlotaBuses escenarioPrincipal;
    private Operaciones tipoOperacion = Operaciones.NINGUNO;

    private ClienteService clienteServicio = ClienteService.getInstance();

    // ── FXML Controles ────────────────────────────────────
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
    @FXML private TableColumn<Cliente, String>  colTelefono;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellido;
    @FXML private TextField txtDPI;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPassword;
    @FXML private TextField txtTelefono;
    @FXML private GridPane gridDatos;

    // ── Iconos de validación (Canvas) ────────────────────
    private IconoValidacion icoNombre;
    private IconoValidacion icoApellido;
    private IconoValidacion icoDPI;
    private IconoValidacion icoEmail;
    private IconoValidacion icoPassword;
    private IconoValidacion icoTelefono;

    // =========================================================
    // INITIALIZE
    // =========================================================
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
    public FlotaBuses getEscenarioPrincipal() { return escenarioPrincipal; }

    public void setEscenarioPrincipal(FlotaBuses escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
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
    // CARGAR DATOS
    // =========================================================
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
    public void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

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

    public void desactivarControles() {
        txtNombre.setEditable(false);
        txtApellido.setEditable(false);
        txtDPI.setEditable(false);
        txtEmail.setEditable(false);
        txtPassword.setEditable(false);
        txtTelefono.setEditable(false);
    }

    public void activarControles() {
        txtNombre.setEditable(true);
        txtApellido.setEditable(true);
        txtDPI.setEditable(true);
        txtEmail.setEditable(true);
        txtPassword.setEditable(true);
        txtTelefono.setEditable(true);
    }

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
