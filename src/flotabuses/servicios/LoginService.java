package flotabuses.servicios;

import flotabuses.enums.RolUsuario;
import flotabuses.modelos.Usuario;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio de autenticación.
 * Crea dos usuarios al arrancar (Admin y Operador) y expone
 * el método autenticar() para validar credenciales.
 */
public class LoginService {

    private static LoginService instancia;

    private final List<Usuario> usuarios = new ArrayList<>();
    private Usuario usuarioActual;

    private LoginService() {
        // Usuarios por defecto creados al arrancar el programa
        usuarios.add(new Usuario(1, "admin",    "admin123",  "Administrador", "Sistema",   RolUsuario.ADMIN));
        usuarios.add(new Usuario(2, "operador", "oper123",   "Operador",      "Sistema",   RolUsuario.OPERADOR));
    }

    public static LoginService getInstance() {
        if (instancia == null) {
            instancia = new LoginService();
        }
        return instancia;
    }

    /**
     * Valida username y password.
     * @return El Usuario si las credenciales son correctas, null en caso contrario.
     */
    public Usuario autenticar(String username, String password) {
        for (Usuario u : usuarios) {
            if (u.getUsername().equals(username) && u.getPassword().equals(password)) {
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
