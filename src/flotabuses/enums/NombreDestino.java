package flotabuses.enums;

/**
 * Catalogo fijo de los destinos turisticos ofrecidos por la agencia en Guatemala.
 *
 * <p>La lista de destinos es cerrada (no puede modificarse en tiempo de ejecucion).
 * El administrador selecciona uno de estos valores al registrar un nuevo destino
 * en el modulo correspondiente.</p>
 *
 * <p>Cada constante almacena un nombre legible ({@link #getNombreMostrar()}) que se
 * usa como clave de ordenamiento en la {@code ListaDoblementeEnlazada} y como texto
 * de visualizacion en la interfaz y los reportes.</p>
 *
 * <p>La clave de fila en la {@code MatrizOrtogonal} de asignaciones corresponde
 * al valor de {@code getNombreMostrar()} del destino.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.modelos.Destino
 * @see flotabuses.servicios.DestinoService
 */
public enum NombreDestino {

    /** La Antigua Guatemala — Patrimonio de la Humanidad, Sacatepequez. */
    ANTIGUA_GUATEMALA("La Antigua Guatemala"),

    /** Lago de Atitlan — Solola. Considerado uno de los lagos mas bellos del mundo. */
    ATITLAN("Lago de Atitlan"),

    /** Chichicastenango — El Quiche. Famoso por su mercado indigena. */
    CHICHICASTENANGO("Chichicastenango"),

    /** Semuc Champey — Alta Verapaz. Piscinas naturales sobre el rio Cahabon. */
    SEMUC_CHAMPEY("Semuc Champey"),

    /** Tikal — Peten. Zona arqueologica maya declarada Patrimonio Mundial. */
    TIKAL("Tikal, Peten"),

    /** Livingston — Izabal. Municipio de cultura garife a orillas del Caribe. */
    LIVINGSTON("Livingston, Izabal"),

    /** Quetzaltenango — Segunda ciudad mas importante de Guatemala (Xela). */
    QUETZALTENANGO("Quetzaltenango"),

    /** Puerto Quetzal — Escuintla. Principal puerto comercial del Pacifico guatemalteco. */
    PUERTO_QUETZAL("Puerto Quetzal, Escuintla"),

    /** Automariscos — Escuintla. Destino gastronomico de mariscos a orillas del Pacifico. */
    AUTOMARISCOS_ESCUINTLA("Automariscos, Escuintla"),

    /** Monterrico — Santa Rosa. Playa de arena negra y reserva natural de tortugas. */
    MONTERRICO("Monterrico, Santa Rosa"),

    /** Rio Dulce — Izabal. Ruta fluvial entre el Lago de Izabal y el Caribe. */
    RIO_DULCE("Rio Dulce, Izabal"),

    /** Coban — Alta Verapaz. Capital de las Verapaces; famosa por el cardamomo y las orquideas. */
    COBAN("Coban, Alta Verapaz"),

    /** Huehuetenango — Noroeste de Guatemala; puerta hacia la Sierra de los Cuchumatanes. */
    HUEHUETENANGO("Huehuetenango"),

    /** Flores — Peten. Isla en el Lago Peten Itza; base para visitar Tikal. */
    FLORES_PETEN("Flores, Peten"),

    /** Iximche — Chimaltenango. Capital del reino Maya-Kaqchikel; sitio arqueologico. */
    IXIMCHE("Iximche, Chimaltenango");

    /** Nombre legible para mostrar en pantalla, reportes y como clave de la lista. */
    private final String nombreMostrar;

    /**
     * Construye el destino con su nombre de presentacion.
     *
     * @param nombreMostrar texto legible del destino
     */
    NombreDestino(String nombreMostrar) {
        this.nombreMostrar = nombreMostrar;
    }

    /**
     * Retorna el nombre legible del destino para uso en pantalla y reportes.
     * Este valor se usa tambien como clave en la {@code ListaDoblementeEnlazada}
     * de destinos y como clave de fila en la {@code MatrizOrtogonal}.
     *
     * @return nombre de presentacion del destino
     */
    public String getNombreMostrar() {
        return nombreMostrar;
    }

    /**
     * Retorna el nombre de presentacion como representacion textual del enum.
     *
     * @return mismo valor que {@link #getNombreMostrar()}
     */
    @Override
    public String toString() {
        return nombreMostrar;
    }
}
