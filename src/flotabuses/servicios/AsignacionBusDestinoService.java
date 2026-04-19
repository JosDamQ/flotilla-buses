/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.servicios;

import flotabuses.estructuras.MatrizOrtogonal;
import flotabuses.estructuras.NodoCabecera;
import flotabuses.estructuras.NodoMatriz;
import flotabuses.modelos.AsignacionBusDestino;
import flotabuses.modelos.Bus;
import flotabuses.modelos.Destino;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author damiangarcia
 */
public class AsignacionBusDestinoService {
    private static AsignacionBusDestinoService instancia;
 
    // Matriz ortogonal: filas=destinos, columnas=buses
    private MatrizOrtogonal matriz;
 
    // Contador interno para códigos de asignación
    private int contadorCodigo;
 
    /*
     * Constructor privado.
     */
    private AsignacionBusDestinoService() {
        this.matriz          = new MatrizOrtogonal();
        this.contadorCodigo  = 1;
    }
 
    /*
     * Retorna la instancia única (Singleton).
     */
    public static AsignacionBusDestinoService getInstance() {
        if (instancia == null) {
            instancia = new AsignacionBusDestinoService();
        }
        return instancia;
    }
 
    // =========================================================
    // CREAR / AGREGAR HORA
    // =========================================================
 
    /*
     * Opción A: guarda una asignación destino+bus+hora.
     * Si la combinación destino+bus ya existe, solo agrega la hora.
     * Si no existe, crea la asignación con esa primera hora.
     *
     * Retorna:
     *   0 → hora agregada correctamente
     *   1 → destino no confirmado
     *   2 → hora inválida (antes de 04:00 o menos de 1 hora de diferencia)
     */
    public int guardar(Destino destino, Bus bus, LocalTime hora) {
 
//        // Solo se asignan destinos confirmados
//        if (!destino.estaConfirmado()) return 1;
// 
//        String filaKey    = destino.getNombre().getNombreMostrar();
//        String columnaKey = bus.getPlaca();
// 
//        // Buscar si ya existe la asignación para este destino+bus
//        AsignacionBusDestino asignacion =
//            (AsignacionBusDestino) matriz.buscar(filaKey, columnaKey);
// 
//        if (asignacion == null) {
//            // No existe → crear nueva asignación
//            asignacion = new AsignacionBusDestino(contadorCodigo, destino, bus);
//            contadorCodigo++;
//            boolean horaAgregada = asignacion.agregarHora(hora);
//            if (!horaAgregada) return 2;
//            matriz.insertar(filaKey, columnaKey, asignacion);
//        } else {
//            // Ya existe → solo agregar la hora nueva
//            boolean horaAgregada = asignacion.agregarHora(hora);
//            if (!horaAgregada) return 2;
//        }
// 
//        return 0;

        if (!destino.estaConfirmado()) return 1;

        String filaKey    = destino.getNombre().getNombreMostrar();
        String columnaKey = bus.getPlaca();

        // ← NUEVA VALIDACIÓN: recorrer toda la columna del bus
        // para verificar que no tenga conflicto de hora en otro destino
        NodoCabecera cabCol = matriz.getCabColumnas();
        while (cabCol != null) {
            if (cabCol.clave.equalsIgnoreCase(columnaKey)) {
                // Encontramos la columna de este bus
                NodoMatriz celda = cabCol.primero;
                while (celda != null) {
                    // Ignorar la celda del mismo destino (esa la valida agregarHora)
                    if (!celda.filaClave.equalsIgnoreCase(filaKey)) {
                        AsignacionBusDestino otraAsig =
                            (AsignacionBusDestino) celda.dato;
                        // Verificar conflicto de hora con este otro destino
                        flotabuses.estructuras.NodoLista nodoHora =
                            otraAsig.getHorasDisponibles().getCabeza();
                        while (nodoHora != null) {
                            LocalTime horaExistente = (LocalTime) nodoHora.dato;

                            // Solo hay conflicto si es la misma fecha Y la hora está muy cerca
                            boolean mismaFecha = otraAsig.getDestino().getFechaSalida()
                                    .equals(destino.getFechaSalida());

                            if (mismaFecha) {
                                long diferencia = Math.abs(
                                    hora.toSecondOfDay() - horaExistente.toSecondOfDay()
                                );
                                if (diferencia < 3600) {
                                    return 3; // Bus ocupado en otro destino ese mismo día a esa hora
                                }
                            }
                            nodoHora = nodoHora.siguiente;
                        }
                    }
                    celda = celda.abajo; // ← recorre verticalmente la columna
                }
                break;
            }
            cabCol = cabCol.siguiente;
        }
        // FIN NUEVA VALIDACIÓN

        AsignacionBusDestino asignacion =
            (AsignacionBusDestino) matriz.buscar(filaKey, columnaKey);

        if (asignacion == null) {
            asignacion = new AsignacionBusDestino(contadorCodigo, destino, bus);
            contadorCodigo++;
            boolean horaAgregada = asignacion.agregarHora(hora);
            if (!horaAgregada) return 2;
            matriz.insertar(filaKey, columnaKey, asignacion);
        } else {
            boolean horaAgregada = asignacion.agregarHora(hora);
            if (!horaAgregada) return 2;
        }

        return 0;
    }
 
    // =========================================================
    // ELIMINAR
    // =========================================================
 
    /*
     * Elimina una hora específica de una asignación.
     * Si al eliminar la hora la asignación queda sin horas,
     * elimina la celda completa de la matriz.
     *
     * Retorna:
     *   true  → eliminado correctamente
     *   false → asignación o hora no encontrada
     */
    public boolean eliminarHora(Destino destino, Bus bus, LocalTime hora) {
        String filaKey    = destino.getNombre().getNombreMostrar();
        String columnaKey = bus.getPlaca();
 
        AsignacionBusDestino asignacion =
            (AsignacionBusDestino) matriz.buscar(filaKey, columnaKey);
 
        if (asignacion == null) return false;
 
        boolean eliminada = asignacion.eliminarHora(hora);
 
        // Si ya no tiene horas, eliminamos la celda de la matriz también
        if (eliminada && asignacion.cantidadHoras() == 0) {
            matriz.eliminar(filaKey, columnaKey);
        }
 
        return eliminada;
    }
 
    /*
     * Elimina la asignación completa (todas las horas) de un destino+bus.
     */
    public boolean eliminarAsignacion(Destino destino, Bus bus) {
        String filaKey    = destino.getNombre().getNombreMostrar();
        String columnaKey = bus.getPlaca();
        return matriz.eliminar(filaKey, columnaKey);
    }
 
    // =========================================================
    // CONSULTAS
    // =========================================================
 
    /*
     * Convierte la matriz a ObservableList para el TableView.
     * Opción A: una fila por cada hora de cada asignación.
     * Cada fila es un FilaAsignacion con todos los datos aplanados.
     */
    public ObservableList<FilaAsignacion> obtenerTodos() {
        ObservableList<FilaAsignacion> lista = FXCollections.observableArrayList();
 
        NodoCabecera fila = matriz.getCabFilas();
        while (fila != null) {
            NodoMatriz celda = fila.primero;
            while (celda != null) {
                AsignacionBusDestino asig = (AsignacionBusDestino) celda.dato;
                // Una fila por cada hora
                flotabuses.estructuras.NodoLista nodoHora =
                    asig.getHorasDisponibles().getCabeza();
                while (nodoHora != null) {
                    LocalTime hora = (LocalTime) nodoHora.dato;
                    lista.add(new FilaAsignacion(
                        asig.getCodigoAsignacion(),
                        asig.getDestino().getNombre().getNombreMostrar(),
                        asig.getDestino().getFechaSalida().toString(),
                        asig.getBus().getPlaca(),
                        asig.getBus().getTipo().toString(),
                        asig.getBus().getCapacidad(),
                        hora.toString()
                    ));
                    nodoHora = nodoHora.siguiente;
                }
                celda = celda.derecha;
            }
            fila = fila.siguiente;
        }
        return lista;
    }
 
    /*
     * Busca una asignación por destino y bus.
     */
    public AsignacionBusDestino buscar(Destino destino, Bus bus) {
        return (AsignacionBusDestino) matriz.buscar(
            destino.getNombre().getNombreMostrar(),
            bus.getPlaca()
        );
    }
 
    /*
     * Retorna la matriz directamente.
     */
    public MatrizOrtogonal getMatriz() { return matriz; }
 
    // =========================================================
    // CLASE INTERNA: FilaAsignacion
    // =========================================================
 
    /*
     * Clase auxiliar que representa una fila aplanada para el TableView.
     * Como la tabla muestra una fila por hora, necesitamos un objeto
     * con todos los campos que la tabla necesita directamente.
     */
    public static class FilaAsignacion {
        private int    codAsignacion;
        private String nombreDestino;
        private String fechaSalida;
        private String placaBus;
        private String tipo;
        private int    capacidad;
        private String hora;
 
        public FilaAsignacion(int codAsignacion, String nombreDestino,
                              String fechaSalida, String placaBus,
                              String tipo, int capacidad, String hora) {
            this.codAsignacion = codAsignacion;
            this.nombreDestino = nombreDestino;
            this.fechaSalida   = fechaSalida;
            this.placaBus      = placaBus;
            this.tipo          = tipo;
            this.capacidad     = capacidad;
            this.hora          = hora;
        }
 
        public int    getCodAsignacion() { return codAsignacion; }
        public String getNombreDestino() { return nombreDestino; }
        public String getFechaSalida()   { return fechaSalida; }
        public String getPlacaBus()      { return placaBus; }
        public String getTipo()          { return tipo; }
        public int    getCapacidad()     { return capacidad; }
        public String getHora()          { return hora; }
    }
}
