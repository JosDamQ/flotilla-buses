package flotabuses.enums;

/**
 * Clasifica los buses de la flotilla segun su capacidad de pasajeros.
 *
 * <p>Cada tipo define un rango valido de capacidad. El metodo
 * {@link #capacidadValida(int)} permite verificar si un numero de pasajeros
 * es correcto para un tipo dado sin exponer los limites directamente.</p>
 *
 * <table border="1">
 *   <caption>Rangos de capacidad por tipo</caption>
 *   <tr><th>Tipo</th><th>Minimo</th><th>Maximo</th><th>Descripcion</th></tr>
 *   <tr><td>MICROBUS</td><td>1</td><td>10</td><td>Vehiculo compacto para grupos pequenos</td></tr>
 *   <tr><td>COUNTY</td><td>24</td><td>28</td><td>Bus mediano tipo County/Sprinter</td></tr>
 *   <tr><td>PULLMAN</td><td>45</td><td>45</td><td>Bus completo de turismo de larga distancia</td></tr>
 * </table>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *   TipoBus tipo = TipoBus.COUNTY;
 *   if (!tipo.capacidadValida(30)) {
 *       throw new IllegalArgumentException("Capacidad fuera de rango");
 *   }
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.modelos.Bus#setCapacidad(int)
 */
public enum TipoBus {

    /** Bus compacto. Capacidad de 1 a 10 pasajeros. */
    MICROBUS(1, 10),

    /** Bus mediano tipo County o Sprinter. Capacidad de 24 a 28 pasajeros. */
    COUNTY(24, 28),

    /** Bus completo de turismo. Capacidad fija de 45 pasajeros. */
    PULLMAN(45, 45);

    /** Numero minimo de pasajeros permitido para este tipo. */
    private final int capacidadMin;

    /** Numero maximo de pasajeros permitido para este tipo. */
    private final int capacidadMax;

    /**
     * Construye el tipo de bus con sus limites de capacidad.
     *
     * @param capacidadMin limite inferior de pasajeros
     * @param capacidadMax limite superior de pasajeros
     */
    TipoBus(int capacidadMin, int capacidadMax) {
        this.capacidadMin = capacidadMin;
        this.capacidadMax = capacidadMax;
    }

    /**
     * Verifica si una capacidad dada es valida para este tipo de bus.
     *
     * <p>Ejemplo:</p>
     * <pre>
     *   TipoBus.MICROBUS.capacidadValida(10)  // true
     *   TipoBus.MICROBUS.capacidadValida(45)  // false
     *   TipoBus.PULLMAN.capacidadValida(45)   // true
     * </pre>
     *
     * @param capacidad numero de pasajeros a verificar
     * @return {@code true} si {@code capacidadMin <= capacidad <= capacidadMax}
     */
    public boolean capacidadValida(int capacidad) {
        return capacidad >= capacidadMin && capacidad <= capacidadMax;
    }

    /**
     * Retorna el limite inferior de pasajeros de este tipo.
     *
     * @return capacidad minima permitida
     */
    public int getCapacidadMin() { return capacidadMin; }

    /**
     * Retorna el limite superior de pasajeros de este tipo.
     *
     * @return capacidad maxima permitida
     */
    public int getCapacidadMax() { return capacidadMax; }
}
