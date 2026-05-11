package flotabuses.modelos;

import flotabuses.enums.RolUsuario;

/**
 * Entidad que representa un usuario del sistema (administrador u operador).
 *
 * <p>Los usuarios se usan exclusivamente en la capa de autenticacion.
 * No corresponden a un modulo CRUD del proyecto, sino al mecanismo de
 * control de acceso basado en roles.</p>
 *
 * <p>Roles disponibles y sus permisos:</p>
 * <ul>
 *   <li><b>ADMIN:</b> acceso completo a todos los modulos (Clientes, Buses,
 *       Destinos, Asignaciones, Boletos, Visualizador).</li>
 *   <li><b>OPERADOR:</b> acceso restringido; solo puede gestionar Clientes
 *       y realizar Compra de Boletos. Los modulos Buses, Destinos y Asignaciones
 *       se ocultan del menu al iniciar sesion.</li>
 * </ul>
 *
 * <p>Los usuarios se inicializan en {@code LoginService} con datos de prueba
 * predefinidos. El sistema no expone un modulo de gestion de usuarios en la
 * interfaz grafica.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.servicios.LoginService
 * @see flotabuses.enums.RolUsuario
 */
public class Usuario {

    /** Identificador numerico unico del usuario. */
    private int        codigoUsuario;

    /** Nombre de usuario para el inicio de sesion. Debe ser unico en el sistema. */
    private String     username;

    /** Contrasena de acceso del usuario. */
    private String     password;

    /** Nombre de pila del usuario. */
    private String     nombre;

    /** Apellido del usuario. */
    private String     apellido;

    /**
     * Rol del usuario que determina los modulos accesibles.
     * {@code ADMIN} tiene acceso completo; {@code OPERADOR} tiene acceso restringido.
     */
    private RolUsuario rol;

    /**
     * Construye un usuario con todos sus atributos.
     *
     * @param codigoUsuario identificador unico del usuario
     * @param username      nombre de usuario para el login
     * @param password      contrasena de acceso
     * @param nombre        nombre de pila
     * @param apellido      apellido
     * @param rol           rol que determina los permisos del usuario
     */
    public Usuario(int codigoUsuario, String username, String password,
                   String nombre, String apellido, RolUsuario rol) {
        this.codigoUsuario = codigoUsuario;
        this.username      = username;
        this.password      = password;
        this.nombre        = nombre;
        this.apellido      = apellido;
        this.rol           = rol;
    }

    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================

    /** @return codigo unico del usuario */
    public int getCodigoUsuario()           { return codigoUsuario; }

    /** @param c nuevo codigo de usuario */
    public void setCodigoUsuario(int c)     { this.codigoUsuario = c; }

    /** @return nombre de usuario para el login */
    public String getUsername()             { return username; }

    /** @param u nuevo nombre de usuario */
    public void setUsername(String u)       { this.username = u; }

    /** @return contrasena del usuario */
    public String getPassword()             { return password; }

    /** @param p nueva contrasena */
    public void setPassword(String p)       { this.password = p; }

    /** @return nombre de pila del usuario */
    public String getNombre()               { return nombre; }

    /** @param n nuevo nombre de pila */
    public void setNombre(String n)         { this.nombre = n; }

    /** @return apellido del usuario */
    public String getApellido()             { return apellido; }

    /** @param a nuevo apellido */
    public void setApellido(String a)       { this.apellido = a; }

    /**
     * Retorna el nombre completo del usuario concatenando nombre y apellido.
     *
     * @return cadena con formato "nombre apellido"
     */
    public String getNombreCompleto()       { return nombre + " " + apellido; }

    /** @return rol del usuario */
    public RolUsuario getRol()              { return rol; }

    /** @param r nuevo rol */
    public void setRol(RolUsuario r)        { this.rol = r; }

    /**
     * Indica si el usuario tiene rol administrador.
     *
     * @return {@code true} si el rol es {@code ADMIN}
     */
    public boolean esAdmin()               { return rol == RolUsuario.ADMIN; }

    /**
     * Indica si el usuario tiene rol operador.
     *
     * @return {@code true} si el rol es {@code OPERADOR}
     */
    public boolean esOperador()            { return rol == RolUsuario.OPERADOR; }

    /**
     * Representacion textual del usuario para depuracion.
     *
     * @return cadena con codigo, username, nombre completo y rol
     */
    @Override
    public String toString() {
        return "Usuario{" +
               "codigo=" + codigoUsuario +
               ", username='" + username + '\'' +
               ", nombre='" + getNombreCompleto() + '\'' +
               ", rol=" + rol +
               '}';
    }
}
