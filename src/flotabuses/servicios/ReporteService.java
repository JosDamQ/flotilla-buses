package flotabuses.servicios;

import flotabuses.estructuras.NodoCabecera;
import flotabuses.estructuras.NodoLista;
import flotabuses.estructuras.NodoMatriz;
import flotabuses.modelos.AsignacionBusDestino;
import flotabuses.modelos.Boleto;
import flotabuses.modelos.Bus;
import flotabuses.modelos.Cliente;
import flotabuses.modelos.Destino;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;

/**
 * Genera reportes en PDF para cada módulo.
 * Usa PdfBuilder (puro Java, sin dependencias externas).
 */
public class ReporteService {

    private static ReporteService instancia;

    private ReporteService() {}

    public static ReporteService getInstance() {
        if (instancia == null) instancia = new ReporteService();
        return instancia;
    }

    private String fechaHora() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    // =========================================================
    // CLIENTES
    // =========================================================

    public void reporteClientes(boolean ascendente) {
        ClienteService cs = ClienteService.getInstance();
        ObservableList<Cliente> lista = ascendente
                ? cs.obtenerTodosAscendente()
                : cs.obtenerTodosDescendente();

        PdfBuilder pdf = new PdfBuilder();
        pdf.setup(
            "Agencia Turistica - Reporte de Clientes",
            "Orden: " + (ascendente ? "Ascendente" : "Descendente")
                + "  |  Generado: " + fechaHora(),
            new String[]{"#", "Codigo", "Nombre", "Apellido", "DPI", "Email"},
            new float[]{28, 45, 100, 100, 92, 163}
        );

        int n = 0;
        for (Cliente c : lista) {
            n++;
            pdf.addRow(
                String.valueOf(n),
                String.valueOf(c.getCodigoCliente()),
                c.getNombre(),
                c.getApellido(),
                c.getDpi(),
                c.getEmail()
            );
        }
        pdf.addFooter("Total: " + n + " cliente(s)");
        guardar(pdf.build(), "reporte_clientes");
    }

    // =========================================================
    // BUSES
    // =========================================================

    public void reporteBuses(boolean ascendente) {
        BusService bs = BusService.getInstance();

        PdfBuilder pdf = new PdfBuilder();
        pdf.setup(
            "Agencia Turistica - Reporte de Flotilla de Buses",
            "Orden por placa: " + (ascendente ? "Ascendente" : "Descendente")
                + "  |  Generado: " + fechaHora(),
            new String[]{"#", "Placa", "Tipo", "Capacidad", "Color", "Estado", "Descripcion"},
            new float[]{28, 65, 65, 52, 68, 68, 182}
        );

        int n = 0;
        NodoLista actual = ascendente
                ? bs.getLista().getCabeza()
                : bs.getLista().getCola();

        while (actual != null) {
            Bus b = (Bus) actual.dato;
            n++;
            pdf.addRow(
                String.valueOf(n),
                b.getPlaca(),
                b.getTipo().name(),
                String.valueOf(b.getCapacidad()),
                b.getColor(),
                b.getEstado().name(),
                b.getDescripcion()
            );
            actual = ascendente ? actual.siguiente : actual.anterior;
        }
        pdf.addFooter("Total: " + n + " bus(es)");
        guardar(pdf.build(), "reporte_buses");
    }

    // =========================================================
    // DESTINOS
    // =========================================================

    public void reporteDestinos(boolean ascendente) {
        DestinoService ds = DestinoService.getInstance();
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        PdfBuilder pdf = new PdfBuilder();
        pdf.setup(
            "Agencia Turistica - Reporte de Destinos",
            "Orden: " + (ascendente ? "Ascendente" : "Descendente")
                + "  |  Generado: " + fechaHora(),
            new String[]{"#", "Codigo", "Nombre Destino", "Fecha Salida", "Costo (Q)", "Estado", "Descripcion"},
            new float[]{28, 43, 110, 73, 58, 73, 143}
        );

        int n = 0;
        NodoLista actual = ascendente
                ? ds.getLista().getCabeza()
                : ds.getLista().getCola();

        while (actual != null) {
            Destino d = (Destino) actual.dato;
            n++;
            pdf.addRow(
                String.valueOf(n),
                String.valueOf(d.getCodigoDestino()),
                d.getNombre().getNombreMostrar(),
                d.getFechaSalida().format(fmtFecha),
                String.format("Q %.2f", d.getCostoBoleto()),
                d.getEstado().name(),
                d.getDescripcion()
            );
            actual = ascendente ? actual.siguiente : actual.anterior;
        }
        pdf.addFooter("Total: " + n + " destino(s)");
        guardar(pdf.build(), "reporte_destinos");
    }

    // =========================================================
    // ASIGNACIONES
    // =========================================================

    public void reporteAsignaciones() {
        AsignacionBusDestinoService as = AsignacionBusDestinoService.getInstance();
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        PdfBuilder pdf = new PdfBuilder();
        pdf.setup(
            "Agencia Turistica - Reporte de Asignaciones",
            "Generado: " + fechaHora(),
            new String[]{"#", "Cod.Asig", "Destino", "Fecha Salida", "Placa Bus", "Tipo", "Cap.", "Hora"},
            new float[]{25, 42, 128, 68, 58, 55, 38, 114}
        );

        int n = 0;
        NodoCabecera fila = as.getMatriz().getCabFilas();
        while (fila != null) {
            NodoMatriz celda = fila.primero;
            while (celda != null) {
                AsignacionBusDestino asig = (AsignacionBusDestino) celda.dato;
                NodoLista nodoHora = asig.getHorasDisponibles().getCabeza();
                while (nodoHora != null) {
                    n++;
                    pdf.addRow(
                        String.valueOf(n),
                        String.valueOf(asig.getCodigoAsignacion()),
                        asig.getDestino().getNombre().getNombreMostrar(),
                        asig.getDestino().getFechaSalida().format(fmtFecha),
                        asig.getBus().getPlaca(),
                        asig.getBus().getTipo().name(),
                        String.valueOf(asig.getBus().getCapacidad()),
                        nodoHora.clave
                    );
                    nodoHora = nodoHora.siguiente;
                }
                celda = celda.derecha;
            }
            fila = fila.siguiente;
        }
        pdf.addFooter("Total: " + n + " asignacion(es)");
        guardar(pdf.build(), "reporte_asignaciones");
    }

    // =========================================================
    // BOLETOS
    // =========================================================

    public void reporteBoletos() {
        BoletoService boletoSvc = BoletoService.getInstance();
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        PdfBuilder pdf = new PdfBuilder();
        pdf.setup(
            "Agencia Turistica - Reporte de Boletos",
            "Generado: " + fechaHora(),
            new String[]{"#", "Codigo", "Cliente", "Destino", "Fecha", "Placa", "Tipo", "Hora", "Costo(Q)"},
            new float[]{25, 38, 110, 92, 72, 55, 48, 40, 48}
        );

        int n = 0;
        NodoLista actual = boletoSvc.getLista().getCabeza();
        while (actual != null) {
            Boleto b = (Boleto) actual.dato;
            n++;
            pdf.addRow(
                String.valueOf(n),
                String.valueOf(b.getCodigoBoleto()),
                b.getCliente().getNombreCompleto(),
                b.getAsignacion().getDestino().getNombre().getNombreMostrar(),
                b.getAsignacion().getDestino().getFechaSalida().format(fmtFecha),
                b.getAsignacion().getBus().getPlaca(),
                b.getAsignacion().getBus().getTipo().name(),
                b.getClaveOrden(),
                String.format("%.2f", b.getCosto())
            );
            actual = actual.siguiente;
        }
        pdf.addFooter("Total: " + n + " boleto(s)");
        guardar(pdf.build(), "reporte_boletos");
    }

    // =========================================================
    // UTILIDADES
    // =========================================================

    private void guardar(byte[] pdfBytes, String nombre) {
        try {
            File archivo = File.createTempFile(nombre + "_", ".pdf");
            archivo.deleteOnExit();
            try (FileOutputStream fos = new FileOutputStream(archivo)) {
                fos.write(pdfBytes);
            }
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(archivo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
