/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: Usuario
 * Descripción: Representa un usuario del sistema (administrador u operador).
 *              Usado para el login y control de acceso a los módulos.
 *              No corresponde a un módulo del proyecto como tal, sino a la
 *              capa de autenticación del sistema.
 */
package flotabuses.modelos;
import flotabuses.enums.RolUsuario;


/**
 *
 * @author damiangarcia
 */
public class Usuario {
    private int        codigoUsuario;  // Único
    private String     username;       // Único
    private String     password;
    private String     nombre;
    private String     apellido;
    private RolUsuario rol;            // ADMIN u OPERADOR
 
    /*
     * Constructor completo.
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
 
    public int getCodigoUsuario()           { return codigoUsuario; }
    public void setCodigoUsuario(int c)     { this.codigoUsuario = c; }
 
    public String getUsername()             { return username; }
    public void setUsername(String u)       { this.username = u; }
 
    public String getPassword()             { return password; }
    public void setPassword(String p)       { this.password = p; }
 
    public String getNombre()               { return nombre; }
    public void setNombre(String n)         { this.nombre = n; }
 
    public String getApellido()             { return apellido; }
    public void setApellido(String a)       { this.apellido = a; }
 
    public String getNombreCompleto()       { return nombre + " " + apellido; }
 
    public RolUsuario getRol()              { return rol; }
    public void setRol(RolUsuario r)        { this.rol = r; }
 
    /*
     * Métodos de conveniencia para verificar rol en los controladores,
     * evitando comparar el enum directamente en cada pantalla.
     */
    public boolean esAdmin()               { return rol == RolUsuario.ADMIN; }
    public boolean esOperador()            { return rol == RolUsuario.OPERADOR; }
 
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
