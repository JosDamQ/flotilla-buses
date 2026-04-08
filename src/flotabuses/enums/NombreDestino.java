/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package flotabuses.enums;

/**
 *
 * @author damiangarcia
 */
public enum NombreDestino {
    ANTIGUA_GUATEMALA("La Antigua Guatemala"),
    ATITLAN("Lago de Atitlán"),
    CHICHICASTENANGO("Chichicastenango"),
    SEMUC_CHAMPEY("Semuc Champey"),
    TIKAL("Tikal, Petén"),
    LIVINGSTON("Livingston, Izabal"),
    QUETZALTENANGO("Quetzaltenango"),
    PUERTO_QUETZAL("Puerto Quetzal, Escuintla"),
    AUTOMARISCOS_ESCUINTLA("Automariscos, Escuintla"),
    MONTERRICO("Monterrico, Santa Rosa"),
    RIO_DULCE("Río Dulce, Izabal"),
    COBAN("Cobán, Alta Verapaz"),
    HUEHUETENANGO("Huehuetenango"),
    FLORES_PETEN("Flores, Petén"),
    IXIMCHE("Iximché, Chimaltenango");
 
    private final String nombreMostrar;
 
    NombreDestino(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }
 
    /*
     * Retorna el nombre legible para mostrar en pantalla o reportes.
     */
    public String getNombreMostrar() {
        return nombreMostrar;
    }
 
    @Override
    public String toString() {
        return nombreMostrar;
    }
}
