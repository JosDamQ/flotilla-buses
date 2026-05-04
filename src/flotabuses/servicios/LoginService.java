package flotabuses.servicios;

import flotabuses.enums.RolUsuario;
import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.estructuras.NodoLista;
import flotabuses.modelos.Usuario;

/**
 * Servicio de autenticación.
 * Almacena usuarios en ListaDoblementeEnlazada (clave = username).
 * Crea dos usuarios al arrancar (Admin y Operador).
 */
public class LoginService {

    private static LoginService instancia;

    private final ListaDoblementeEnlazada usuarios = new ListaDoblementeEnlazada();
    private Usuario usuarioActual;

    private LoginService() {
        // Usuarios por defecto creados al arrancar el programa
        usuarios.insertarOrdenado("admin",
            new Usuario(1, "admin", "admin123", "Administrador", "Sistema", RolUsuario.ADMIN));
        usuarios.insertarOrdenado("operador",
            new Usuario(2, "operador", "oper123", "Operador", "Sistema", RolUsuario.OPERADOR));
    }

    public static LoginService getInstance() {
        if (instancia == null) {
            instancia = new LoginService();
        }
        return instancia;
    }

    /**
     * Busca el username en la lista y verifica la contraseña.
     * @return El Usuario si las credenciales son correctas, null en caso contrario.
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

    public Usuario getUsuarioActual() {
        return usuarioActual;
    }

    public void cerrarSesion() {
        usuarioActual = null;
    }
}
