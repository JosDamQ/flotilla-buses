/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 * Clase: Cliente
 * Descripción: Representa a un cliente de la agencia turística.
 *              Se almacena en un Árbol Binario de Búsqueda ordenado
 *              por codigoCliente.
 */
package flotabuses.modelos;

/**
 *
 * @author damiangarcia
 */
public class Cliente {
    private int    codigoCliente;  // Identificador único numérico
    private String nombre;
    private String apellido;
    private String dpi;            // Identificación única
    private String email;
    private String password;
 
    /*
     * Constructor completo.
     */
    public Cliente(int codigoCliente, String nombre, String apellido,
                   String dpi, String email, String password) {
        this.codigoCliente = codigoCliente;
        this.nombre        = nombre;
        this.apellido      = apellido;
        this.dpi           = dpi;
        this.email         = email;
        this.password      = password;
    }
 
    // =========================================================
    // GETTERS Y SETTERS
    // =========================================================
 
    public int getCodigoCliente()            { return codigoCliente; }
    public void setCodigoCliente(int c)      { this.codigoCliente = c; }
 
    public String getNombre()                { return nombre; }
    public void setNombre(String n)          { this.nombre = n; }
 
    public String getApellido()              { return apellido; }
    public void setApellido(String a)        { this.apellido = a; }
 
    public String getNombreCompleto()        { return nombre + " " + apellido; }
 
    public String getDpi()                   { return dpi; }
    public void setDpi(String d)             { this.dpi = d; }
 
    public String getEmail()                 { return email; }
    public void setEmail(String e)           { this.email = e; }
 
    public String getPassword()              { return password; }
    public void setPassword(String p)        { this.password = p; }
 
    @Override
    public String toString() {
        return "Cliente{" +
               "codigo=" + codigoCliente +
               ", nombre='" + getNombreCompleto() + '\'' +
               ", dpi='" + dpi + '\'' +
               ", email='" + email + '\'' +
               '}';
    }
}
