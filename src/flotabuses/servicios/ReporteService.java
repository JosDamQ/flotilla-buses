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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;

/**
 * Genera reportes en PDF y en HTML para cada módulo.
 * PDF: usa PdfBuilder (puro Java, sin dependencias externas).
 * HTML: abre tabla estilizada en el navegador del sistema.
 */
public class ReporteService {

    private static ReporteService instancia;
    private ReporteService() {}
    public static ReporteService getInstance() {
        if (instancia == null) instancia = new ReporteService();
        return instancia;
    }

    // =========================================================
    // CLIENTES — PDF
    // =========================================================

    public void reporteClientesPdf(boolean ascendente) {
        ClienteService cs = ClienteService.getInstance();
        ObservableList<Cliente> lista = ascendente
                ? cs.obtenerTodosAscendente() : cs.obtenerTodosDescendente();

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
            pdf.addRow(String.valueOf(n), String.valueOf(c.getCodigoCliente()),
                c.getNombre(), c.getApellido(), c.getDpi(), c.getEmail());
        }
        pdf.addFooter("Total: " + n + " cliente(s)");
        guardarPdf(pdf.build(), "reporte_clientes");
    }

    // =========================================================
    // CLIENTES — HTML
    // =========================================================

    public void reporteClientesHtml(boolean ascendente) {
        ClienteService cs = ClienteService.getInstance();
        ObservableList<Cliente> lista = ascendente
                ? cs.obtenerTodosAscendente() : cs.obtenerTodosDescendente();

        StringBuilder sb = new StringBuilder();
        sb.append(htmlHead("Reporte de Clientes"));
        sb.append("<h1>Agencia Turística — Reporte de Clientes</h1>");
        sb.append("<p class='sub'>Orden por código: <b>")
          .append(ascendente ? "Ascendente" : "Descendente")
          .append("</b> &nbsp;|&nbsp; ").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Código</th><th>Nombre</th>"
                + "<th>Apellido</th><th>DPI</th><th>Email</th></tr>");
        int n = 0;
        for (Cliente c : lista) {
            n++;
            sb.append("<tr>").append(td(n)).append(td(c.getCodigoCliente()))
              .append(td(c.getNombre())).append(td(c.getApellido()))
              .append(td(c.getDpi())).append(td(c.getEmail())).append("</tr>");
        }
        sb.append("</table>").append(pie("Total: " + n + " cliente(s)")).append("</body></html>");
        abrirHtml(sb.toString(), "reporte_clientes");
    }

    // =========================================================
    // BUSES — PDF
    // =========================================================

    public void reporteBusesPdf(boolean ascendente) {
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
        NodoLista actual = ascendente ? bs.getLista().getCabeza() : bs.getLista().getCola();
        while (actual != null) {
            Bus b = (Bus) actual.dato; n++;
            pdf.addRow(String.valueOf(n), b.getPlaca(), b.getTipo().name(),
                String.valueOf(b.getCapacidad()), b.getColor(),
                b.getEstado().name(), b.getDescripcion());
            actual = ascendente ? actual.siguiente : actual.anterior;
        }
        pdf.addFooter("Total: " + n + " bus(es)");
        guardarPdf(pdf.build(), "reporte_buses");
    }

    // =========================================================
    // BUSES — HTML
    // =========================================================

    public void reporteBusesHtml(boolean ascendente) {
        BusService bs = BusService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(htmlHead("Reporte de Buses"));
        sb.append("<h1>Agencia Turística — Reporte de Flotilla de Buses</h1>");
        sb.append("<p class='sub'>Orden por placa: <b>")
          .append(ascendente ? "Ascendente" : "Descendente")
          .append("</b> &nbsp;|&nbsp; ").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Placa</th><th>Tipo</th>"
                + "<th>Capacidad</th><th>Color</th><th>Estado</th><th>Descripción</th></tr>");
        int n = 0;
        NodoLista actual = ascendente ? bs.getLista().getCabeza() : bs.getLista().getCola();
        while (actual != null) {
            Bus b = (Bus) actual.dato; n++;
            sb.append("<tr>").append(td(n)).append(td(b.getPlaca())).append(td(b.getTipo()))
              .append(td(b.getCapacidad())).append(td(b.getColor()))
              .append(td(b.getEstado())).append(td(b.getDescripcion())).append("</tr>");
            actual = ascendente ? actual.siguiente : actual.anterior;
        }
        sb.append("</table>").append(pie("Total: " + n + " bus(es)")).append("</body></html>");
        abrirHtml(sb.toString(), "reporte_buses");
    }

    // =========================================================
    // DESTINOS — PDF
    // =========================================================

    public void reporteDestinosPdf(boolean ascendente) {
        DestinoService ds = DestinoService.getInstance();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        PdfBuilder pdf = new PdfBuilder();
        pdf.setup(
            "Agencia Turistica - Reporte de Destinos",
            "Orden: " + (ascendente ? "Ascendente" : "Descendente")
                + "  |  Generado: " + fechaHora(),
            new String[]{"#", "Codigo", "Nombre Destino", "Fecha Salida", "Costo (Q)", "Estado", "Descripcion"},
            new float[]{28, 43, 110, 73, 58, 73, 143}
        );
        int n = 0;
        NodoLista actual = ascendente ? ds.getLista().getCabeza() : ds.getLista().getCola();
        while (actual != null) {
            Destino d = (Destino) actual.dato; n++;
            pdf.addRow(String.valueOf(n), String.valueOf(d.getCodigoDestino()),
                d.getNombre().getNombreMostrar(), d.getFechaSalida().format(fmt),
                String.format("Q %.2f", d.getCostoBoleto()),
                d.getEstado().name(), d.getDescripcion());
            actual = ascendente ? actual.siguiente : actual.anterior;
        }
        pdf.addFooter("Total: " + n + " destino(s)");
        guardarPdf(pdf.build(), "reporte_destinos");
    }

    // =========================================================
    // DESTINOS — HTML
    // =========================================================

    public void reporteDestinosHtml(boolean ascendente) {
        DestinoService ds = DestinoService.getInstance();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        StringBuilder sb = new StringBuilder();
        sb.append(htmlHead("Reporte de Destinos"));
        sb.append("<h1>Agencia Turística — Reporte de Destinos Turísticos</h1>");
        sb.append("<p class='sub'>Orden por nombre: <b>")
          .append(ascendente ? "Ascendente" : "Descendente")
          .append("</b> &nbsp;|&nbsp; ").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Código</th><th>Nombre</th>"
                + "<th>Fecha Salida</th><th>Costo (Q)</th><th>Estado</th><th>Descripción</th></tr>");
        int n = 0;
        NodoLista actual = ascendente ? ds.getLista().getCabeza() : ds.getLista().getCola();
        while (actual != null) {
            Destino d = (Destino) actual.dato; n++;
            sb.append("<tr>").append(td(n)).append(td(d.getCodigoDestino()))
              .append(td(d.getNombre().getNombreMostrar()))
              .append(td(d.getFechaSalida().format(fmt)))
              .append(td(String.format("Q %.2f", d.getCostoBoleto())))
              .append(td(d.getEstado())).append(td(d.getDescripcion())).append("</tr>");
            actual = ascendente ? actual.siguiente : actual.anterior;
        }
        sb.append("</table>").append(pie("Total: " + n + " destino(s)")).append("</body></html>");
        abrirHtml(sb.toString(), "reporte_destinos");
    }

    // =========================================================
    // ASIGNACIONES — PDF
    // =========================================================

    public void reporteAsignacionesPdf() {
        AsignacionBusDestinoService as = AsignacionBusDestinoService.getInstance();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
                    pdf.addRow(String.valueOf(n), String.valueOf(asig.getCodigoAsignacion()),
                        asig.getDestino().getNombre().getNombreMostrar(),
                        asig.getDestino().getFechaSalida().format(fmt),
                        asig.getBus().getPlaca(), asig.getBus().getTipo().name(),
                        String.valueOf(asig.getBus().getCapacidad()), nodoHora.clave);
                    nodoHora = nodoHora.siguiente;
                }
                celda = celda.derecha;
            }
            fila = fila.siguiente;
        }
        pdf.addFooter("Total: " + n + " asignacion(es)");
        guardarPdf(pdf.build(), "reporte_asignaciones");
    }

    // =========================================================
    // ASIGNACIONES — HTML
    // =========================================================

    public void reporteAsignacionesHtml() {
        AsignacionBusDestinoService as = AsignacionBusDestinoService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(htmlHead("Reporte de Asignaciones"));
        sb.append("<h1>Agencia Turística — Reporte de Asignación de Buses</h1>");
        sb.append("<p class='sub'>").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Cód. Asig.</th><th>Destino</th>"
                + "<th>Fecha Salida</th><th>Bus (Placa)</th><th>Tipo</th>"
                + "<th>Capacidad</th><th>Hora</th></tr>");
        int n = 0;
        NodoCabecera fila = as.getMatriz().getCabFilas();
        while (fila != null) {
            NodoMatriz celda = fila.primero;
            while (celda != null) {
                AsignacionBusDestino asig = (AsignacionBusDestino) celda.dato;
                NodoLista nodoHora = asig.getHorasDisponibles().getCabeza();
                while (nodoHora != null) {
                    n++;
                    sb.append("<tr>").append(td(n)).append(td(asig.getCodigoAsignacion()))
                      .append(td(asig.getDestino().getNombre().getNombreMostrar()))
                      .append(td(asig.getDestino().getFechaSalida()))
                      .append(td(asig.getBus().getPlaca())).append(td(asig.getBus().getTipo()))
                      .append(td(asig.getBus().getCapacidad())).append(td(nodoHora.clave))
                      .append("</tr>");
                    nodoHora = nodoHora.siguiente;
                }
                celda = celda.derecha;
            }
            fila = fila.siguiente;
        }
        sb.append("</table>").append(pie("Total: " + n + " asignación(es)")).append("</body></html>");
        abrirHtml(sb.toString(), "reporte_asignaciones");
    }

    // =========================================================
    // BOLETOS — PDF
    // =========================================================

    public void reporteBoletosPdf() {
        BoletoService boletoSvc = BoletoService.getInstance();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
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
            Boleto b = (Boleto) actual.dato; n++;
            pdf.addRow(String.valueOf(n), String.valueOf(b.getCodigoBoleto()),
                b.getCliente().getNombreCompleto(),
                b.getAsignacion().getDestino().getNombre().getNombreMostrar(),
                b.getAsignacion().getDestino().getFechaSalida().format(fmt),
                b.getAsignacion().getBus().getPlaca(),
                b.getAsignacion().getBus().getTipo().name(),
                b.getClaveOrden(), String.format("%.2f", b.getCosto()));
            actual = actual.siguiente;
        }
        pdf.addFooter("Total: " + n + " boleto(s)");
        guardarPdf(pdf.build(), "reporte_boletos");
    }

    // =========================================================
    // BOLETOS — HTML
    // =========================================================

    public void reporteBoletosHtml() {
        BoletoService boletoSvc = BoletoService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(htmlHead("Reporte de Boletos"));
        sb.append("<h1>Agencia Turística — Reporte de Boletos</h1>");
        sb.append("<p class='sub'>").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Código</th><th>Cliente</th>"
                + "<th>Destino</th><th>Fecha Salida</th><th>Bus (Placa)</th>"
                + "<th>Tipo</th><th>Hora</th><th>Costo (Q)</th></tr>");
        int n = 0;
        NodoLista actual = boletoSvc.getLista().getCabeza();
        while (actual != null) {
            Boleto b = (Boleto) actual.dato; n++;
            sb.append("<tr>").append(td(n)).append(td(b.getCodigoBoleto()))
              .append(td(b.getCliente().getNombreCompleto()))
              .append(td(b.getAsignacion().getDestino().getNombre().getNombreMostrar()))
              .append(td(b.getAsignacion().getDestino().getFechaSalida()))
              .append(td(b.getAsignacion().getBus().getPlaca()))
              .append(td(b.getAsignacion().getBus().getTipo()))
              .append(td(b.getClaveOrden()))
              .append(td(String.format("Q %.2f", b.getCosto()))).append("</tr>");
            actual = actual.siguiente;
        }
        sb.append("</table>").append(pie("Total: " + n + " boleto(s)")).append("</body></html>");
        abrirHtml(sb.toString(), "reporte_boletos");
    }

    // =========================================================
    // UTILIDADES PRIVADAS
    // =========================================================

    private String fechaHora() {
        return "Generado: <b>"
             + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
             + "</b>";
    }

    private String td(Object val) {
        return "<td>" + (val == null ? "" : val.toString()) + "</td>";
    }

    private String pie(String texto) {
        return "<p class='pie'>" + texto + "</p>";
    }

    private String htmlHead(String titulo) {
        return "<!DOCTYPE html><html lang='es'><head>"
             + "<meta charset='UTF-8'><title>" + titulo + "</title>"
             + "<style>"
             + "body{font-family:Arial,sans-serif;margin:30px;background:#f4f6f9;color:#333}"
             + "h1{color:#3a3835;border-bottom:3px solid #54a4d3;padding-bottom:8px}"
             + ".sub{color:#666;margin-bottom:16px}"
             + "table{border-collapse:collapse;width:100%;background:#fff;"
             +       "box-shadow:0 2px 8px rgba(0,0,0,.12)}"
             + "th{background:#54a4d3;color:#fff;padding:10px 12px;text-align:left;font-size:13px}"
             + "td{padding:8px 12px;border-bottom:1px solid #e0e0e0;font-size:13px}"
             + "tr:nth-child(even) td{background:#f9fbfd}"
             + "tr:hover td{background:#e8f4fb}"
             + ".pie{margin-top:12px;color:#888;font-size:12px;text-align:right}"
             + "</style></head><body>";
    }

    /** Abre un HTML en el navegador del sistema. */
    private void abrirHtml(String html, String nombre) {
        try {
            File archivo = File.createTempFile(nombre + "_", ".html");
            archivo.deleteOnExit();
            try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
                pw.print(html);
            }
            if (Desktop.isDesktopSupported()
                    && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                Desktop.getDesktop().browse(archivo.toURI());
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    /** Guarda un PDF y lo abre con el visor del sistema. */
    private void guardarPdf(byte[] pdfBytes, String nombre) {
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
        } catch (Exception e) { e.printStackTrace(); }
    }
}
