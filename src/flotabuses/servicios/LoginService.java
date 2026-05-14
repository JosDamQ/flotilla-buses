package flotabuses.servicios;

import flotabuses.enums.RolUsuario;
import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.modelos.Usuario;

/**
 * Servicio de autenticacion de usuarios del sistema.
 *
 * <p>Implementa el patron <b>Singleton</b>. Al crearse la instancia, registra
 * automaticamente los dos usuarios predefinidos del sistema:</p>
 *
 * <table border="1">
 *   <caption>Usuarios predefinidos</caption>
 *   <tr><th>Username</th><th>Password</th><th>Rol</th></tr>
 *   <tr><td>admin</td><td>admin123</td><td>ADMIN</td></tr>
 *   <tr><td>operador</td><td>oper123</td><td>OPERADOR</td></tr>
 * </table>
 *
 * <p>Los usuarios se almacenan en una {@link ListaDoblementeEnlazada}
 * con el {@code username} como clave de busqueda. La busqueda en la
 * lista es O(n), lo que es aceptable dado el numero reducido de usuarios.</p>
 *
 * <p>El usuario autenticado se conserva en {@link #getUsuarioActual()} durante
 * toda la sesion y se limpia al llamar a {@link #cerrarSesion()}.</p>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.controladores.LoginController
 * @see flotabuses.modelos.Usuario
 * @see flotabuses.enums.RolUsuario
 */
public class LoginService {

    /** Instancia unica del servicio (Singleton). */
    private static LoginService instancia;

    /** Lista de usuarios del sistema indexados por username. */
    private final ListaDoblementeEnlazada usuarios = new ListaDoblementeEnlazada();

    /** Usuario que inicio sesion actualmente; {@code null} si no hay sesion activa. */
    private Usuario usuarioActual;

    /**
     * Constructor privado. Registra los usuarios predefinidos del sistema.
     */
    private LoginService() {
        usuarios.insertarOrdenado("admin",
            new Usuario(1, "admin", "admin123", "Administrador", "Sistema", RolUsuario.ADMIN));
        usuarios.insertarOrdenado("operador",
            new Usuario(2, "operador", "oper123", "Operador", "Sistema", RolUsuario.OPERADOR));
    }

    /**
     * Retorna la instancia unica del servicio, creandola si no existe.
     *
     * @return instancia Singleton de {@code LoginService}
     */
    public static LoginService getInstance() {
        if (instancia == null) {
            instancia = new LoginService();
        }
        return instancia;
    }

    /**
     * Autentica un usuario verificando su username y contrasena.
     *
     * <p>Si las credenciales son correctas, el usuario queda registrado como
     * sesion activa y puede recuperarse con {@link #getUsuarioActual()}.</p>
     *
     * @param username nombre de usuario ingresado en el formulario de login
     * @param password contrasena ingresada en el formulario de login
     * @return el {@link Usuario} autenticado si las credenciales son validas,
     *         o {@code null} si el username no existe o la contrasena es incorrecta
     */
    public Usuario autenticar(String username, String password) {
        Object resultado = usuarios.buscar(username);
        if (resultado != null) {
            Usuario u = (Usuario) resultado;
            if (u.getPassword().equals(password)) {
                usuarioActual = u;
                return u;
            }
        }
        return null;
    }

    /**
     * Retorna el usuario que inicio sesion en la sesion actual.
     *
     * @return usuario autenticado, o {@code null} si no hay sesion activa
     */
    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    /**
     * Cierra la sesion activa eliminando la referencia al usuario actual.
     * Despues de llamar a este metodo, {@link #getUsuarioActual()} devuelve {@code null}.
     */
    public void cerrarSesion() {
        usuarioActual = null;
    }
}
