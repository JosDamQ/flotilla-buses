/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: Boleto
 * Descripción: Representa un boleto de viaje comprado por un cliente.
 *              Referencia una AsignacionBusDestino y la hora específica
 *              que el cliente seleccionó de las disponibles.
 *              Se almacena en una Lista Doblemente Enlazada ordenada por hora.
 *
 *              Para reportes, los datos del destino y bus se navegan así:
 *                - Destino:    boleto.getAsignacion().getDestino()
 *                - Bus:        boleto.getAsignacion().getBus()
 *                - Costo:      boleto.getAsignacion().getDestino().getCostoBoleto()
 *                - FechaSalida: boleto.getAsignacion().getDestino().getFechaSalida()
 */
package flotabuses.modelos;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author damiangarcia
 */
public class Boleto {
    private int                  codigoBoleto;      // Único
    private AsignacionBusDestino asignacion;
    private Cliente              cliente;
    private LocalTime            horaSeleccionada;  // Hora escogida de la lista
 
    private static final DateTimeFormatter FORMATO_HORA =
            DateTimeFormatter.ofPattern("HH:mm");
 
    /*
     * Constructor completo.
     * Lanza IllegalArgumentException si la hora seleccionada no existe
     * en las horas disponibles de la asignación.
     */
    public Boleto(int codigoBoleto, AsignacionBusDestino asignacion,
                  Cliente cliente, LocalTime horaSeleccionada) {
        if (!asignacion.tieneHora(horaSeleccionada)) {
            throw new IllegalArgumentException(
                "La hora " + horaSeleccionada.format(FORMATO_HORA) +
                " no está disponible en esta asignación."
            );
        }
        this.codigoBoleto     = codigoBoleto;
        this.asignacion       = asignacion;
        this.cliente          = cliente;
        this.horaSeleccionada = horaSeleccionada;
    }
 
    // =========================================================
    // MÉTODOS DE CONVENIENCIA (para reportes y UI)
    // =========================================================
 
    /*
     * Retorna el costo del boleto navegando desde la asignación.
     */
    public double getCosto() {
        return asignacion.getDestino().getCostoBoleto();
    }
 
    /*
     * Retorna la clave de ordenamiento para la Lista Doblemente Enlazada.
     * Formato "HH:mm" para que ordene correctamente.
     */
    public String getClaveOrden() {
        return horaSeleccionada.format(FORMATO_HORA);
    }
 
    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================
 
    public int getCodigoBoleto()                      { return codigoBoleto; }
    public void setCodigoBoleto(int c)                { this.codigoBoleto = c; }
 
    public AsignacionBusDestino getAsignacion()       { return asignacion; }
    public void setAsignacion(AsignacionBusDestino a) { this.asignacion = a; }
 
    public Cliente getCliente()                       { return cliente; }
    public void setCliente(Cliente c)                 { this.cliente = c; }
 
    public LocalTime getHoraSeleccionada()            { return horaSeleccionada; }
    public void setHoraSeleccionada(LocalTime h)      { this.horaSeleccionada = h; }
 
    @Override
    public String toString() {
        return "Boleto{" +
               "codigo=" + codigoBoleto +
               ", cliente='" + cliente.getNombreCompleto() + '\'' +
               ", destino='" + asignacion.getDestino().getNombre() + '\'' +
               ", bus='" + asignacion.getBus().getPlaca() + '\'' +
               ", hora=" + horaSeleccionada.format(FORMATO_HORA) +
               ", costo=Q" + getCosto() +
               '}';
    }
}
