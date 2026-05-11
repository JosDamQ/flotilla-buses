package flotabuses.controladores;

import flotabuses.estructuras.NodoArbol;
import flotabuses.estructuras.NodoCabecera;
import flotabuses.estructuras.NodoLista;
import flotabuses.estructuras.NodoMatriz;
import flotabuses.main.FlotaBuses;
import flotabuses.modelos.AsignacionBusDestino;
import flotabuses.modelos.Bus;
import flotabuses.modelos.Cliente;
import flotabuses.servicios.AsignacionBusDestinoService;
import flotabuses.servicios.BusService;
import flotabuses.servicios.ClienteService;
import java.net.URL;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Controlador del visualizador de estructuras de datos del sistema.
 *
 * <p>Renderiza graficamente en un {@link Canvas} JavaFX las tres estructuras
 * de datos personalizadas del proyecto, utilizando datos reales de los servicios
 * en tiempo de ejecucion:</p>
 * <ul>
 *   <li><b>Tab 1 — Arbol Binario de Busqueda:</b> dibuja el arbol de clientes
 *       con nodos circulares, aristas padre-hijo y el codigo y nombre de cada cliente.</li>
 *   <li><b>Tab 2 — Lista Doblemente Enlazada:</b> dibuja la lista de buses con
 *       rectangulos y flechas bidireccionales que representan los punteros
 *       {@code siguiente} y {@code anterior}.</li>
 *   <li><b>Tab 3 — Matriz Ortogonal:</b> dibuja la cuadricula de asignaciones
 *       bus-destino con cabeceras de fila (destinos) y columna (buses); las celdas
 *       con asignacion se destacan en verde.</li>
 * </ul>
 *
 * <p>Cada canvas se redimensiona automaticamente segun la cantidad de datos antes
 * de redibujar, y esta envuelto en un {@code ScrollPane} para permitir
 * el desplazamiento cuando el contenido supera el area visible.</p>
 *
 * @author FabianH
 * @version 1.0
 * @see flotabuses.estructuras.ArbolBinarioBusqueda
 * @see flotabuses.estructuras.ListaDoblementeEnlazada
 * @see flotabuses.estructuras.MatrizOrtogonal
 */
public class VisualizadorEstructurasController implements Initializable {

    @FXML private Canvas canvasArbol;
    @FXML private Canvas canvasLista;
    @FXML private Canvas canvasMatriz;

    private FlotaBuses escenarioPrincipal;

    // Constantes de layout para el Arbol Binario
    private static final int RADIO_NODO  = 28;
    private static final int X_STEP_ABB  = 80;
    private static final int Y_STEP_ABB  = 90;

    // Constantes de layout para la Lista
    private static final double NODO_W  = 145;
    private static final double NODO_H  = 65;
    private static final double GAP     = 55;

    // Constantes de layout para la Matriz
    private static final double HEADER_W = 185;
    private static final double HEADER_H = 75;
    private static final double CELL_W   = 130;
    private static final double CELL_H   = 75;

    // Contador de posicion in-orden reutilizado en cada redibujado del arbol
    private int xCounter;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dibujarArbol();
        dibujarLista();
        dibujarMatriz();
    }

    /** @param ep instancia principal de la aplicacion para la navegacion entre escenas */
    public void setEscenarioPrincipal(FlotaBuses ep) {
        this.escenarioPrincipal = ep;
    }

    // =========================================================
    // ACCIONES DE LA BARRA DE HERRAMIENTAS
    // =========================================================

    /** Redibuja las tres estructuras con los datos actuales de los servicios. */
    @FXML
    public void refrescar() {
        dibujarArbol();
        dibujarLista();
        dibujarMatriz();
    }

    /** Navega de regreso al menu principal. */
    @FXML
    public void volverMenu() {
        escenarioPrincipal.menuPrincipal();
    }

    // =========================================================
    // ARBOL BINARIO DE BUSQUEDA — CLIENTES
    // =========================================================

    /**
     * Dibuja el arbol binario de busqueda de clientes en el canvas correspondiente.
     * Usa recorrido in-orden para asignar posiciones X (orden natural de claves)
     * y profundidad de nivel para las posiciones Y.
     */
    private void dibujarArbol() {
        GraphicsContext gc = canvasArbol.getGraphicsContext2D();
        NodoArbol raiz = ClienteService.getInstance().getArbol().getRaiz();

        if (raiz == null) {
            limpiarCanvas(gc, canvasArbol, 900, 200);
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("SansSerif", 14));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("El arbol esta vacio. Registre al menos un cliente para visualizarlo.", 30, 60);
            return;
        }

        IdentityHashMap<NodoArbol, double[]> posiciones = new IdentityHashMap<>();
        xCounter = 0;
        asignarX(raiz, posiciones);
        asignarY(raiz, 0, posiciones);

        double maxX = 0, maxY = 0;
        for (double[] p : posiciones.values()) {
            if (p[0] > maxX) maxX = p[0];
            if (p[1] > maxY) maxY = p[1];
        }

        canvasArbol.setWidth(Math.max(900, maxX + 80));
        canvasArbol.setHeight(Math.max(500, maxY + 80));
        limpiarCanvas(gc, canvasArbol, canvasArbol.getWidth(), canvasArbol.getHeight());

        // Titulo
        gc.setFill(Color.DARKSLATEGRAY);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Arbol Binario de Busqueda — Clientes (ordenados por codigo ascendente, recorrido in-orden)", 10, 25);

        // Aristas (primero para que queden debajo de los nodos)
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        dibujarAristas(raiz, posiciones, gc);

        // Nodos
        dibujarNodos(raiz, posiciones, gc);
    }

    private void asignarX(NodoArbol nodo, IdentityHashMap<NodoArbol, double[]> pos) {
        if (nodo == null) return;
        asignarX(nodo.izq, pos);
        double[] xy = pos.computeIfAbsent(nodo, k -> new double[2]);
        xy[0] = 50 + xCounter * X_STEP_ABB;
        xCounter++;
        asignarX(nodo.der, pos);
    }

    private void asignarY(NodoArbol nodo, int nivel, IdentityHashMap<NodoArbol, double[]> pos) {
        if (nodo == null) return;
        double[] xy = pos.computeIfAbsent(nodo, k -> new double[2]);
        xy[1] = 55 + nivel * Y_STEP_ABB;
        asignarY(nodo.izq, nivel + 1, pos);
        asignarY(nodo.der, nivel + 1, pos);
    }

    private void dibujarAristas(NodoArbol nodo,
                                IdentityHashMap<NodoArbol, double[]> pos,
                                GraphicsContext gc) {
        if (nodo == null) return;
        double[] padre = pos.get(nodo);
        if (nodo.izq != null) {
            double[] hijo = pos.get(nodo.izq);
            gc.strokeLine(padre[0], padre[1], hijo[0], hijo[1]);
            dibujarAristas(nodo.izq, pos, gc);
        }
        if (nodo.der != null) {
            double[] hijo = pos.get(nodo.der);
            gc.strokeLine(padre[0], padre[1], hijo[0], hijo[1]);
            dibujarAristas(nodo.der, pos, gc);
        }
    }

    private void dibujarNodos(NodoArbol nodo,
                              IdentityHashMap<NodoArbol, double[]> pos,
                              GraphicsContext gc) {
        if (nodo == null) return;
        double[] p = pos.get(nodo);
        double x = p[0], y = p[1];

        gc.setFill(Color.STEELBLUE);
        gc.fillOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(2);
        gc.strokeOval(x - RADIO_NODO, y - RADIO_NODO, RADIO_NODO * 2, RADIO_NODO * 2);

        Cliente c = (Cliente) nodo.dato;
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
        gc.fillText("C-" + c.getCodigoCliente(), x, y - 5);
        gc.setFont(Font.font("SansSerif", 9));
        gc.fillText(c.getNombre(), x, y + 8);

        dibujarNodos(nodo.izq, pos, gc);
        dibujarNodos(nodo.der, pos, gc);
    }

    // =========================================================
    // LISTA DOBLEMENTE ENLAZADA — BUSES
    // =========================================================

    /**
     * Dibuja la lista doblemente enlazada de buses en el canvas correspondiente.
     * Cada nodo se representa como un rectangulo con la placa, tipo, capacidad y
     * estado del bus. Las flechas bidireccionales representan los punteros
     * {@code siguiente} (derecha, arriba) y {@code anterior} (izquierda, abajo).
     */
    private void dibujarLista() {
        GraphicsContext gc = canvasLista.getGraphicsContext2D();
        var lista = BusService.getInstance().getLista();

        if (lista.estaVacia()) {
            limpiarCanvas(gc, canvasLista, 900, 180);
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("SansSerif", 14));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("La lista esta vacia. Registre al menos un bus para visualizarla.", 30, 60);
            return;
        }

        int size = lista.getTamanio();
        double stepX  = NODO_W + GAP;
        double totalW = Math.max(900, 50 + 70 + size * stepX + GAP + 70 + 40);
        canvasLista.setWidth(totalW);
        canvasLista.setHeight(220);
        limpiarCanvas(gc, canvasLista, totalW, 220);

        double startX = 40;
        double nodeY  = 80;

        // Titulo
        gc.setFill(Color.DARKSLATEGRAY);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Lista Doblemente Enlazada — Buses (ordenados por placa, A-Z)", startX, 28);

        // Centinela null izquierdo
        double nullWL = 65;
        dibujarNuloCentinela(gc, startX, nodeY, nullWL, NODO_H);

        NodoLista actual = lista.getCabeza();
        int idx = 0;
        double prevRight = startX + nullWL;

        while (actual != null) {
            double x = prevRight + GAP;

            // Flechas bidireccionales (siguiente arriba, anterior abajo)
            gc.setStroke(Color.DARKSLATEGRAY);
            gc.setLineWidth(1.5);
            double arrowYTop = nodeY + NODO_H * 0.28;
            double arrowYBot = nodeY + NODO_H * 0.72;
            dibujarFlecha(gc, prevRight, arrowYTop, x, arrowYTop);
            dibujarFlecha(gc, x, arrowYBot, prevRight, arrowYBot);

            // Rectangulo del nodo
            gc.setFill(Color.MEDIUMSEAGREEN);
            gc.fillRoundRect(x, nodeY, NODO_W, NODO_H, 10, 10);
            gc.setStroke(Color.DARKGREEN);
            gc.setLineWidth(2);
            gc.strokeRoundRect(x, nodeY, NODO_W, NODO_H, 10, 10);

            // Texto del nodo
            Bus bus = (Bus) actual.dato;
            gc.setFill(Color.WHITE);
            gc.setTextAlign(TextAlignment.CENTER);
            double cx = x + NODO_W / 2;
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 12));
            gc.fillText(bus.getPlaca(), cx, nodeY + 18);
            gc.setFont(Font.font("SansSerif", 10));
            gc.fillText(bus.getTipo() + "  Cap:" + bus.getCapacidad(), cx, nodeY + 33);
            gc.fillText(bus.getEstado().toString(), cx, nodeY + 47);
            gc.setFont(Font.font("SansSerif", 9));
            gc.fillText(bus.getColor(), cx, nodeY + 59);

            prevRight = x + NODO_W;
            idx++;
            actual = actual.siguiente;
        }

        // Flecha hacia centinela null derecho y centinela
        double nullXR = prevRight + GAP;
        gc.setStroke(Color.DARKSLATEGRAY);
        gc.setLineWidth(1.5);
        double arrowYTop = nodeY + NODO_H * 0.28;
        double arrowYBot = nodeY + NODO_H * 0.72;
        dibujarFlecha(gc, prevRight, arrowYTop, nullXR, arrowYTop);
        dibujarFlecha(gc, nullXR + 65, arrowYBot, prevRight, arrowYBot);
        dibujarNuloCentinela(gc, nullXR, nodeY, 65, NODO_H);

        // Leyenda
        gc.setFill(Color.DARKSLATEGRAY);
        gc.setFont(Font.font("SansSerif", 11));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Flecha superior -> siguiente  |  Flecha inferior <- anterior", startX, nodeY + NODO_H + 25);
    }

    private void dibujarNuloCentinela(GraphicsContext gc, double x, double y, double w, double h) {
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRoundRect(x, y, w, h, 8, 8);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1.5);
        gc.strokeRoundRect(x, y, w, h, 8, 8);
        gc.setFill(Color.DIMGRAY);
        gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText("null", x + w / 2, y + h / 2 + 4);
    }

    // =========================================================
    // MATRIZ ORTOGONAL — ASIGNACIONES
    // =========================================================

    /**
     * Dibuja la matriz ortogonal de asignaciones en el canvas correspondiente.
     * Las filas representan destinos turisticos y las columnas representan buses.
     * Las celdas con asignacion activa se destacan en verde con el codigo
     * de asignacion y la cantidad de horas registradas.
     */
    private void dibujarMatriz() {
        GraphicsContext gc = canvasMatriz.getGraphicsContext2D();
        var matriz = AsignacionBusDestinoService.getInstance().getMatriz();

        if (matriz.estaVacia()) {
            limpiarCanvas(gc, canvasMatriz, 900, 200);
            gc.setFill(Color.GRAY);
            gc.setFont(Font.font("SansSerif", 14));
            gc.setTextAlign(TextAlignment.LEFT);
            gc.fillText("La matriz esta vacia. Realice al menos una asignacion para visualizarla.", 30, 60);
            return;
        }

        List<String> filas    = new ArrayList<>();
        List<String> columnas = new ArrayList<>();

        NodoCabecera cf = matriz.getCabFilas();
        while (cf != null) { filas.add(cf.clave);    cf = cf.siguiente; }

        NodoCabecera cc = matriz.getCabColumnas();
        while (cc != null) { columnas.add(cc.clave); cc = cc.siguiente; }

        double startX = 10, startY = 50;
        double totalW = Math.max(900,  startX + HEADER_W + columnas.size() * CELL_W + 20);
        double totalH = Math.max(400,  startY + HEADER_H + filas.size() * CELL_H + 50);
        canvasMatriz.setWidth(totalW);
        canvasMatriz.setHeight(totalH);
        limpiarCanvas(gc, canvasMatriz, totalW, totalH);

        // Titulo
        gc.setFill(Color.DARKSLATEGRAY);
        gc.setFont(Font.font("SansSerif", FontWeight.BOLD, 13));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("Matriz Ortogonal — Asignaciones (Filas = Destinos  |  Columnas = Buses)", startX, 30);

        // Cabeceras de columna (buses)
        for (int col = 0; col < columnas.size(); col++) {
            double x = startX + HEADER_W + col * CELL_W;
            double y = startY;
            gc.setFill(Color.STEELBLUE);
            gc.fillRect(x, y, CELL_W, HEADER_H);
            gc.setStroke(Color.DARKBLUE);
            gc.setLineWidth(1);
            gc.strokeRect(x, y, CELL_W, HEADER_H);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 10));
            gc.setTextAlign(TextAlignment.CENTER);
            String placa = columnas.get(col);
            gc.fillText(placa.length() > 9 ? placa.substring(0, 9) : placa,
                        x + CELL_W / 2, y + HEADER_H / 2 + 4);
        }

        // Cabeceras de fila (destinos) y celdas
        for (int row = 0; row < filas.size(); row++) {
            double y = startY + HEADER_H + row * CELL_H;

            // Cabecera de fila
            gc.setFill(Color.SLATEGRAY);
            gc.fillRect(startX, y, HEADER_W, CELL_H);
            gc.setStroke(Color.DARKSLATEGRAY);
            gc.setLineWidth(1);
            gc.strokeRect(startX, y, HEADER_W, CELL_H);
            gc.setFill(Color.WHITE);
            gc.setFont(Font.font("SansSerif", 11));
            gc.setTextAlign(TextAlignment.CENTER);
            String fila = filas.get(row);
            gc.fillText(fila.length() > 22 ? fila.substring(0, 22) : fila,
                        startX + HEADER_W / 2, y + CELL_H / 2 + 4);

            // Celdas de datos
            for (int col = 0; col < columnas.size(); col++) {
                double x = startX + HEADER_W + col * CELL_W;
                Object dato = matriz.buscar(filas.get(row), columnas.get(col));
                if (dato != null) {
                    AsignacionBusDestino asig = (AsignacionBusDestino) dato;
                    gc.setFill(Color.LIGHTGREEN);
                    gc.fillRect(x, y, CELL_W, CELL_H);
                    gc.setStroke(Color.DARKGREEN);
                    gc.setLineWidth(1.5);
                    gc.strokeRect(x, y, CELL_W, CELL_H);
                    gc.setFill(Color.DARKGREEN);
                    gc.setFont(Font.font("Monospaced", FontWeight.BOLD, 11));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText("Asig. #" + asig.getCodigoAsignacion(), x + CELL_W / 2, y + 24);
                    gc.setFont(Font.font("SansSerif", 10));
                    gc.fillText(asig.cantidadHoras() + " hora(s)", x + CELL_W / 2, y + 42);
                } else {
                    gc.setFill(Color.WHITESMOKE);
                    gc.fillRect(x, y, CELL_W, CELL_H);
                    gc.setStroke(Color.LIGHTGRAY);
                    gc.setLineWidth(1);
                    gc.strokeRect(x, y, CELL_W, CELL_H);
                    gc.setFill(Color.LIGHTGRAY);
                    gc.setFont(Font.font("SansSerif", 13));
                    gc.setTextAlign(TextAlignment.CENTER);
                    gc.fillText("—", x + CELL_W / 2, y + CELL_H / 2 + 5);
                }
            }
        }

        // Leyenda
        gc.setFill(Color.MEDIUMSEAGREEN);
        gc.fillRect(startX, startY + HEADER_H + filas.size() * CELL_H + 10, 18, 18);
        gc.setFill(Color.DARKSLATEGRAY);
        gc.setFont(Font.font("SansSerif", 11));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.fillText("  Asignacion activa", startX + 20, startY + HEADER_H + filas.size() * CELL_H + 24);
        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(startX + 180, startY + HEADER_H + filas.size() * CELL_H + 10, 18, 18);
        gc.setStroke(Color.LIGHTGRAY);
        gc.strokeRect(startX + 180, startY + HEADER_H + filas.size() * CELL_H + 10, 18, 18);
        gc.setFill(Color.DARKSLATEGRAY);
        gc.fillText("  Sin asignacion", startX + 200, startY + HEADER_H + filas.size() * CELL_H + 24);
    }

    // =========================================================
    // UTILIDADES DE DIBUJO
    // =========================================================

    /**
     * Dibuja una flecha de la coordenada (x1, y1) a (x2, y2) con una punta triangular
     * en el extremo destino.
     *
     * @param gc canvas en el que se dibuja
     * @param x1 coordenada X de origen
     * @param y1 coordenada Y de origen
     * @param x2 coordenada X de destino
     * @param y2 coordenada Y de destino
     */
    private void dibujarFlecha(GraphicsContext gc, double x1, double y1,
                                double x2, double y2) {
        gc.strokeLine(x1, y1, x2, y2);
        double angulo = Math.atan2(y2 - y1, x2 - x1);
        double lon    = 10;
        double alpha  = Math.toRadians(25);
        gc.strokeLine(x2, y2,
            x2 - lon * Math.cos(angulo - alpha),
            y2 - lon * Math.sin(angulo - alpha));
        gc.strokeLine(x2, y2,
            x2 - lon * Math.cos(angulo + alpha),
            y2 - lon * Math.sin(angulo + alpha));
    }

    /**
     * Limpia el canvas y pinta el fondo blanco con las dimensiones indicadas.
     *
     * @param gc     contexto grafico del canvas
     * @param canvas canvas a limpiar
     * @param w      ancho en pixeles
     * @param h      alto en pixeles
     */
    private void limpiarCanvas(GraphicsContext gc, Canvas canvas, double w, double h) {
        canvas.setWidth(w);
        canvas.setHeight(h);
        gc.clearRect(0, 0, w, h);
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, w, h);
    }
}
