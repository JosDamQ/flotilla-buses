package flotabuses.modelos;

/**
 * Entidad que representa a un cliente de la agencia turistica.
 *
 * <p>Los clientes se almacenan en un {@code ArbolBinarioBusqueda} ordenado
 * por {@code codigoCliente}, lo que permite busquedas eficientes en O(log n).
 * El codigo se genera de forma incremental y automatica desde {@code ClienteService}.</p>
 *
 * <p>Validaciones de formato aplicadas en el controlador antes de persistir:</p>
 * <ul>
 *   <li><b>DPI:</b> exactamente 13 digitos numericos (CUI Guatemala).</li>
 *   <li><b>Telefono:</b> 8 digitos, primer digito entre 2 y 7 (estandar Guatemala).</li>
 *   <li><b>Email:</b> formato estandar usuario@dominio.tld.</li>
 *   <li><b>Nombre / Apellido:</b> solo letras, tildes y espacios.</li>
 *   <li><b>Password:</b> minimo 6 caracteres.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 2.0
 * @see flotabuses.servicios.ClienteService
 * @see flotabuses.estructuras.ArbolBinarioBusqueda
 */
public class Cliente {

    /** Identificador unico generado automaticamente por {@code ClienteService}. */
    private int    codigoCliente;

    /** Nombre de pila del cliente. Solo letras, tildes y espacios. */
    private String nombre;

    /** Apellido del cliente. Solo letras, tildes y espacios. */
    private String apellido;

    /**
     * Documento Personal de Identificacion (DPI / CUI).
     * Debe contener exactamente 13 digitos numericos segun el estandar guatemalteco.
     */
    private String dpi;

    /** Correo electronico del cliente. Se valida unicidad antes de persistir. */
    private String email;

    /** Contrasena de acceso del cliente. Minimo 6 caracteres. */
    private String password;

    /**
     * Numero de telefono del cliente en formato guatemalteco.
     * 8 digitos; el primer digito debe estar entre 2 y 7.
     */
    private String telefono;

    /**
     * Construye un cliente con todos sus atributos.
     *
     * @param codigoCliente identificador unico asignado por el servicio
     * @param nombre        nombre de pila del cliente
     * @param apellido      apellido del cliente
     * @param dpi           numero de DPI/CUI (13 digitos)
     * @param email         correo electronico valido y unico
     * @param password      contrasena de acceso (minimo 6 caracteres)
     * @param telefono      telefono guatemalteco de 8 digitos
     */
    public Cliente(int codigoCliente, String nombre, String apellido,
                   String dpi, String email, String password, String telefono) {
        this.codigoCliente = codigoCliente;
        this.nombre        = nombre;
        this.apellido      = apellido;
        this.dpi           = dpi;
        this.email         = email;
        this.password      = password;
        this.telefono      = telefono;
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    /**
     * Retorna el codigo unico del cliente.
     * @return codigo de cliente asignado por el servicio
     */
    public int getCodigoCliente()       { return codigoCliente; }

    /** @param c nuevo codigo de cliente */
    public void setCodigoCliente(int c) { this.codigoCliente = c; }

    /** @return nombre de pila del cliente */
    public String getNombre()           { return nombre; }

    /** @param n nuevo nombre de pila */
    public void setNombre(String n)     { this.nombre = n; }

    /** @return apellido del cliente */
    public String getApellido()         { return apellido; }

    /** @param a nuevo apellido */
    public void setApellido(String a)   { this.apellido = a; }

    /**
     * Retorna el nombre completo del cliente concatenando nombre y apellido.
     * @return cadena con formato "nombre apellido"
     */
    public String getNombreCompleto()   { return nombre + " " + apellido; }

    /** @return numero de DPI/CUI del cliente */
    public String getDpi()              { return dpi; }

    /** @param d nuevo numero de DPI/CUI */
    public void setDpi(String d)        { this.dpi = d; }

    /** @return correo electronico del cliente */
    public String getEmail()            { return email; }

    /** @param e nuevo correo electronico */
    public void setEmail(String e)      { this.email = e; }

    /** @return contrasena del cliente */
    public String getPassword()         { return password; }

    /** @param p nueva contrasena */
    public void setPassword(String p)   { this.password = p; }

    /** @return telefono del cliente en formato guatemalteco */
    public String getTelefono()         { return telefono; }

    /** @param t nuevo numero de telefono */
    public void setTelefono(String t)   { this.telefono = t; }

    /**
     * Representacion textual del cliente para depuracion y reportes.
     * @return cadena con codigo, nombre completo, DPI, email y telefono
     */
    @Override
    public String toString() {
        return "Cliente{" +
               "codigo=" + codigoCliente +
               ", nombre='" + getNombreCompleto() + '\'' +
               ", dpi='" + dpi + '\'' +
               ", email='" + email + '\'' +
               ", tel='" + telefono + '\'' +
               '}';
    }
}
