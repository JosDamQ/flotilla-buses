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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.collections.ObservableList;

/**
 * Servicio de reportes HTML.
 * Genera tablas HTML y las abre en el navegador del sistema.
 */
public class ReporteService {

    private static ReporteService instancia;

    private ReporteService() {}

    public static ReporteService getInstance() {
        if (instancia == null) instancia = new ReporteService();
        return instancia;
    }

    // =========================================================
    // REPORTE DE CLIENTES
    // =========================================================

    /** Genera reporte de clientes ordenado por código (asc o desc). */
    public void reporteClientes(boolean ascendente) {
        ClienteService cs = ClienteService.getInstance();
        ObservableList<Cliente> lista = ascendente
                ? cs.obtenerTodosAscendente()
                : cs.obtenerTodosDescendente();

        StringBuilder sb = new StringBuilder();
        sb.append(encabezado("Reporte de Clientes"));
        sb.append("<h1>Agencia Turística — Reporte de Clientes</h1>");
        sb.append("<p class='sub'>Orden por código: <b>")
          .append(ascendente ? "Ascendente" : "Descendente")
          .append("</b> &nbsp;|&nbsp; ").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Código</th><th>Nombre</th>"
                + "<th>Apellido</th><th>DPI</th><th>Email</th></tr>");
        int n = 0;
        for (Cliente c : lista) {
            n++;
            sb.append("<tr>")
              .append(td(n)).append(td(c.getCodigoCliente()))
              .append(td(c.getNombre())).append(td(c.getApellido()))
              .append(td(c.getDpi())).append(td(c.getEmail()))
              .append("</tr>");
        }
        sb.append("</table>").append(pie("Total: " + n + " cliente(s)"))
          .append("</body></html>");
        abrir(sb.toString(), "reporte_clientes");
    }

    // =========================================================
    // REPORTE DE BUSES
    // =========================================================

    /** Genera reporte de buses ordenado por placa (asc o desc). */
    public void reporteBuses(boolean ascendente) {
        BusService bs = BusService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(encabezado("Reporte de Buses"));
        sb.append("<h1>Agencia Turística — Reporte de Flotilla de Buses</h1>");
        sb.append("<p class='sub'>Orden por placa: <b>")
          .append(ascendente ? "Ascendente" : "Descendente")
          .append("</b> &nbsp;|&nbsp; ").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Placa</th><th>Tipo</th>"
                + "<th>Capacidad</th><th>Color</th><th>Estado</th><th>Descripción</th></tr>");
        int n = 0;
        if (ascendente) {
            NodoLista actual = bs.getLista().getCabeza();
            while (actual != null) {
                Bus b = (Bus) actual.dato;
                n++;
                sb.append("<tr>")
                  .append(td(n)).append(td(b.getPlaca())).append(td(b.getTipo()))
                  .append(td(b.getCapacidad())).append(td(b.getColor()))
                  .append(td(b.getEstado())).append(td(b.getDescripcion()))
                  .append("</tr>");
                actual = actual.siguiente;
            }
        } else {
            NodoLista actual = bs.getLista().getCola();
            while (actual != null) {
                Bus b = (Bus) actual.dato;
                n++;
                sb.append("<tr>")
                  .append(td(n)).append(td(b.getPlaca())).append(td(b.getTipo()))
                  .append(td(b.getCapacidad())).append(td(b.getColor()))
                  .append(td(b.getEstado())).append(td(b.getDescripcion()))
                  .append("</tr>");
                actual = actual.anterior;
            }
        }
        sb.append("</table>").append(pie("Total: " + n + " bus(es)"))
          .append("</body></html>");
        abrir(sb.toString(), "reporte_buses");
    }

    // =========================================================
    // REPORTE DE DESTINOS
    // =========================================================

    /** Genera reporte de destinos ordenado por nombre (asc o desc). */
    public void reporteDestinos(boolean ascendente) {
        DestinoService ds = DestinoService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(encabezado("Reporte de Destinos"));
        sb.append("<h1>Agencia Turística — Reporte de Destinos Turísticos</h1>");
        sb.append("<p class='sub'>Orden por nombre: <b>")
          .append(ascendente ? "Ascendente" : "Descendente")
          .append("</b> &nbsp;|&nbsp; ").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Código</th><th>Nombre</th>"
                + "<th>Fecha Salida</th><th>Costo (Q)</th><th>Estado</th><th>Descripción</th></tr>");
        int n = 0;
        if (ascendente) {
            NodoLista actual = ds.getLista().getCabeza();
            while (actual != null) {
                Destino d = (Destino) actual.dato;
                n++;
                sb.append("<tr>")
                  .append(td(n)).append(td(d.getCodigoDestino()))
                  .append(td(d.getNombre().getNombreMostrar()))
                  .append(td(d.getFechaSalida()))
                  .append(td(String.format("Q %.2f", d.getCostoBoleto())))
                  .append(td(d.getEstado())).append(td(d.getDescripcion()))
                  .append("</tr>");
                actual = actual.siguiente;
            }
        } else {
            NodoLista actual = ds.getLista().getCola();
            while (actual != null) {
                Destino d = (Destino) actual.dato;
                n++;
                sb.append("<tr>")
                  .append(td(n)).append(td(d.getCodigoDestino()))
                  .append(td(d.getNombre().getNombreMostrar()))
                  .append(td(d.getFechaSalida()))
                  .append(td(String.format("Q %.2f", d.getCostoBoleto())))
                  .append(td(d.getEstado())).append(td(d.getDescripcion()))
                  .append("</tr>");
                actual = actual.anterior;
            }
        }
        sb.append("</table>").append(pie("Total: " + n + " destino(s)"))
          .append("</body></html>");
        abrir(sb.toString(), "reporte_destinos");
    }

    // =========================================================
    // REPORTE DE ASIGNACIONES
    // =========================================================

    /** Genera reporte de buses asignados a destinos (una fila por hora). */
    public void reporteAsignaciones() {
        AsignacionBusDestinoService as = AsignacionBusDestinoService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(encabezado("Reporte de Asignaciones"));
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
                    sb.append("<tr>")
                      .append(td(n))
                      .append(td(asig.getCodigoAsignacion()))
                      .append(td(asig.getDestino().getNombre().getNombreMostrar()))
                      .append(td(asig.getDestino().getFechaSalida()))
                      .append(td(asig.getBus().getPlaca()))
                      .append(td(asig.getBus().getTipo()))
                      .append(td(asig.getBus().getCapacidad()))
                      .append(td(nodoHora.clave))
                      .append("</tr>");
                    nodoHora = nodoHora.siguiente;
                }
                celda = celda.derecha;
            }
            fila = fila.siguiente;
        }
        sb.append("</table>").append(pie("Total: " + n + " asignación(es)"))
          .append("</body></html>");
        abrir(sb.toString(), "reporte_asignaciones");
    }

    // =========================================================
    // REPORTE DE BOLETOS
    // =========================================================

    /** Genera reporte de boletos ordenados por hora. */
    public void reporteBoletos() {
        BoletoService boletoSvc = BoletoService.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(encabezado("Reporte de Boletos"));
        sb.append("<h1>Agencia Turística — Reporte de Boletos</h1>");
        sb.append("<p class='sub'>").append(fechaHora()).append("</p>");
        sb.append("<table><tr><th>#</th><th>Código</th><th>Cliente</th>"
                + "<th>Destino</th><th>Fecha Salida</th><th>Bus (Placa)</th>"
                + "<th>Tipo</th><th>Hora</th><th>Costo (Q)</th></tr>");
        int n = 0;
        NodoLista actual = boletoSvc.getLista().getCabeza();
        while (actual != null) {
            Boleto b = (Boleto) actual.dato;
            n++;
            sb.append("<tr>")
              .append(td(n))
              .append(td(b.getCodigoBoleto()))
              .append(td(b.getCliente().getNombreCompleto()))
              .append(td(b.getAsignacion().getDestino().getNombre().getNombreMostrar()))
              .append(td(b.getAsignacion().getDestino().getFechaSalida()))
              .append(td(b.getAsignacion().getBus().getPlaca()))
              .append(td(b.getAsignacion().getBus().getTipo()))
              .append(td(b.getClaveOrden()))
              .append(td(String.format("Q %.2f", b.getCosto())))
              .append("</tr>");
            actual = actual.siguiente;
        }
        sb.append("</table>").append(pie("Total: " + n + " boleto(s)"))
          .append("</body></html>");
        abrir(sb.toString(), "reporte_boletos");
    }

    // =========================================================
    // UTILIDADES PRIVADAS
    // =========================================================

    private String encabezado(String titulo) {
        return "<!DOCTYPE html><html lang='es'><head>"
             + "<meta charset='UTF-8'><title>" + titulo + "</title>"
             + "<style>"
             + "body{font-family:Arial,sans-serif;margin:30px;background:#f4f6f9;color:#333}"
             + "h1{color:#3a3835;border-bottom:3px solid #54a4d3;padding-bottom:8px}"
             + ".sub{color:#666;margin-bottom:16px}"
             + "table{border-collapse:collapse;width:100%;background:#fff;"
             +       "box-shadow:0 2px 8px rgba(0,0,0,.12)}"
             + "th{background:#54a4d3;color:#fff;padding:10px 12px;text-align:left;"
             +    "font-size:13px}"
             + "td{padding:8px 12px;border-bottom:1px solid #e0e0e0;font-size:13px}"
             + "tr:nth-child(even) td{background:#f9fbfd}"
             + "tr:hover td{background:#e8f4fb}"
             + ".pie{margin-top:12px;color:#888;font-size:12px;text-align:right}"
             + "</style></head><body>";
    }

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

    private void abrir(String html, String nombre) {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
