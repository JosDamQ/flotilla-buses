/*
 * Clase: ValidadorCampos
 * Descripción: Validaciones de formato acordes al estándar guatemalteco.
 *              DPI/CUI, teléfono, placa, nombres, email y demás campos
 *              usados en los módulos de la flotilla de buses.
 */
package flotabuses.utils;

/**
 * Utilería estática para validar campos de formulario con reglas
 * propias de Guatemala (DPI 13 dígitos, teléfono 8 dígitos, placa
 * X-000-XXX, etc.).
 */
public class ValidadorCampos {

    private ValidadorCampos() { /* No instanciar */ }

    // =========================================================
    // VALIDADORES
    // =========================================================

    /**
     * DPI / CUI de Guatemala: exactamente 13 dígitos numéricos.
     */
    public static boolean esDpiValido(String v) {
        return v != null && v.matches("\\d{13}");
    }

    /**
     * Teléfono de Guatemala: 8 dígitos, primer dígito entre 2 y 7.
     * Fijos: 2xxxxxxx  |  Celulares: 3xxxxxxx a 7xxxxxxx
     */
    public static boolean esTelefonoValido(String v) {
        return v != null && v.matches("[2-7]\\d{7}");
    }

    /**
     * Correo electrónico estándar.
     */
    public static boolean esEmailValido(String v) {
        return v != null && v.matches(
            "[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}");
    }

    /**
     * Nombre o apellido: solo letras (incluyendo tildes y ñ) y espacios.
     * No puede estar vacío ni contener dígitos o símbolos.
     */
    public static boolean esNombreValido(String v) {
        return v != null && !v.trim().isEmpty()
               && v.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+");
    }

    /**
     * Contraseña: mínimo 6 caracteres.
     */
    public static boolean esPasswordValida(String v) {
        return v != null && v.length() >= 6;
    }

    /**
     * Placa vehicular de Guatemala: formato X-000-XXX
     * (una letra, guion, tres dígitos, guion, tres letras).
     * Ejemplo válido: P-123-ABC
     */
    public static boolean esPlacaValida(String v) {
        return v != null && v.toUpperCase().matches("[A-Z]-\\d{3}-[A-Z]{3}");
    }

    /**
     * Capacidad: número entero positivo.
     */
    public static boolean esCapacidadValida(String v) {
        if (v == null || v.trim().isEmpty()) return false;
        try {
            return Integer.parseInt(v.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Costo de boleto: número decimal positivo.
     */
    public static boolean esCostoValido(String v) {
        if (v == null || v.trim().isEmpty()) return false;
        try {
            return Double.parseDouble(v.trim()) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Color: solo letras (incluyendo tildes y ñ) y espacios.
     */
    public static boolean esColorValido(String v) {
        return v != null && !v.trim().isEmpty()
               && v.matches("[a-zA-ZáéíóúÁÉÍÓÚüÜñÑ ]+");
    }

    // =========================================================
    // MENSAJES DE ERROR DESCRIPTIVOS
    // =========================================================

    public static String mensajeDpi() {
        return "DPI debe tener exactamente 13 dígitos numéricos (CUI Guatemala)";
    }
    public static String mensajeTelefono() {
        return "Teléfono: 8 dígitos, primer dígito entre 2 y 7 (Guatemala)";
    }
    public static String mensajeEmail() {
        return "Correo electrónico no tiene formato válido";
    }
    public static String mensajeNombre() {
        return "Solo se permiten letras, tildes y espacios (sin números ni símbolos)";
    }
    public static String mensajePassword() {
        return "La contraseña debe tener al menos 6 caracteres";
    }
    public static String mensajePlaca() {
        return "Placa debe tener formato X-000-XXX  (ej: P-123-ABC)";
    }
    public static String mensajeCapacidad() {
        return "La capacidad debe ser un número entero positivo";
    }
    public static String mensajeCosto() {
        return "El costo debe ser un número mayor a 0  (ej: 150.00)";
    }
    public static String mensajeColor() {
        return "Solo se permiten letras y espacios para el color";
    }
}
