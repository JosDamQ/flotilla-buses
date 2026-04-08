/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package flotabuses.enums;

/**
 *
 * @author damiangarcia
 */
public enum TipoBus {
    MICROBUS(1, 10),
    COUNTY(24, 28),
    PULLMAN(45, 45);
 
    private final int capacidadMin;
    private final int capacidadMax;
 
    TipoBus(int capacidadMin, int capacidadMax) {
        this.capacidadMin = capacidadMin;
        this.capacidadMax = capacidadMax;
    }
 
    /*
     * Valida si una capacidad dada es válida para este tipo de bus.
     * Ejemplo: MICROBUS.capacidadValida(10) → true
     *          MICROBUS.capacidadValida(45) → false
     */
    public boolean capacidadValida(int capacidad) {
        return capacidad >= capacidadMin && capacidad <= capacidadMax;
    }
 
    public int getCapacidadMin() { return capacidadMin; }
    public int getCapacidadMax() { return capacidadMax; }
}
