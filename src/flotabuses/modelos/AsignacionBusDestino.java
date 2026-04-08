/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: AsignacionBusDestino
 * Descripción: Representa la asignación de un bus a un destino turístico.
 *              Incluye una lista de horas de salida disponibles (mínimo 1 hora
 *              de diferencia entre cada una, comenzando desde las 04:00 AM).
 *              Se almacena en la Matriz Ortogonal:
 *                - Fila    = Destino turístico
 *                - Columna = Bus asignado
 *              El administrador puede agregar varias horas de salida para
 *              distribuir pasajeros en diferentes horarios.
 */
package flotabuses.modelos;
import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.estructuras.NodoLista;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author damiangarcia
 */
public class AsignacionBusDestino {
    private int                     codigoAsignacion;  // Único
    private Destino                 destino;
    private Bus                     bus;
    private ListaDoblementeEnlazada horasDisponibles;  // Lista de LocalTime ordenada
 
    // Formato para usar como clave en la lista: "HH:mm"
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");
 
    /*
     * Constructor completo.
     * La lista de horas inicia vacía; se agregan con agregarHora().
     */
    public AsignacionBusDestino(int codigoAsignacion, Destino destino, Bus bus) {
        this.codigoAsignacion = codigoAsignacion;
        this.destino          = destino;
        this.bus              = bus;
        this.horasDisponibles = new ListaDoblementeEnlazada();
    }
 
    // =========================================================
    // MANEJO DE HORAS
    // =========================================================
 
    /*
     * Agrega una hora de salida a la lista.
     * Valida que:
     *   1. La hora no sea antes de las 04:00 AM.
     *   2. Exista al menos 1 hora de diferencia con cualquier hora ya registrada.
     * Retorna true si se agregó, false si no pasó la validación.
     */
    public boolean agregarHora(LocalTime hora) {
        LocalTime horaMinima = LocalTime.of(4, 0);
        if (hora.isBefore(horaMinima)) {
            return false; // No se permiten horas antes de las 04:00
        }
 
        // Verificar diferencia mínima de 1 hora con las ya existentes
        NodoLista actual = horasDisponibles.getCabeza();
        while (actual != null) {
            LocalTime horaExistente = (LocalTime) actual.dato;
            long diferencia = Math.abs(
                hora.toSecondOfDay() - horaExistente.toSecondOfDay()
            );
            if (diferencia < 3600) { // 3600 segundos = 1 hora
                return false; // Muy cerca de una hora existente
            }
            actual = actual.siguiente;
        }
 
        // Pasa validación → insertar ordenado por clave "HH:mm"
        horasDisponibles.insertarOrdenado(hora.format(FORMATO_HORA), hora);
        return true;
    }
 
    /*
     * Elimina una hora de salida de la lista.
     */
    public boolean eliminarHora(LocalTime hora) {
        return horasDisponibles.eliminar(hora.format(FORMATO_HORA));
    }
 
    /*
     * Verifica si una hora específica está disponible en esta asignación.
     */
    public boolean tieneHora(LocalTime hora) {
        return horasDisponibles.buscar(hora.format(FORMATO_HORA)) != null;
    }
 
    /*
     * Retorna cuántas horas de salida tiene esta asignación.
     */
    public int cantidadHoras() {
        return horasDisponibles.getTamanio();
    }
 
    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================
 
    public int getCodigoAsignacion()                  { return codigoAsignacion; }
    public void setCodigoAsignacion(int c)            { this.codigoAsignacion = c; }
 
    public Destino getDestino()                       { return destino; }
    public void setDestino(Destino d)                 { this.destino = d; }
 
    public Bus getBus()                               { return bus; }
    public void setBus(Bus b)                         { this.bus = b; }
 
    public ListaDoblementeEnlazada getHorasDisponibles() { return horasDisponibles; }
 
    @Override
    public String toString() {
        return "AsignacionBusDestino{" +
               "codigo=" + codigoAsignacion +
               ", destino='" + destino.getNombre() + '\'' +
               ", bus='" + bus.getPlaca() + '\'' +
               ", horas=" + horasDisponibles.getTamanio() +
               '}';
    }
}
