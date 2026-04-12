/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.servicios;

import flotabuses.estructuras.ArbolBinarioBusqueda;
import flotabuses.estructuras.NodoArbol;
import flotabuses.modelos.Cliente;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author damiangarcia
 */
public class ClienteService {
    private static ClienteService instancia;
 
    // Árbol binario de búsqueda ordenado por codigoCliente
    private ArbolBinarioBusqueda arbolClientes;
 
    // Contador interno para generar códigos únicos automáticamente
    private int contadorCodigo;
 
    /*
     * Constructor privado: inicializa el árbol vacío y el contador en 1.
     */
    private ClienteService() {
        this.arbolClientes  = new ArbolBinarioBusqueda();
        this.contadorCodigo = 1;
    }
 
    /*
     * Retorna la instancia única del servicio (Singleton).
     */
    public static ClienteService getInstance() {
        if (instancia == null) {
            instancia = new ClienteService();
        }
        return instancia;
    }
 
    // =========================================================
    // CREAR
    // =========================================================
 
    /*
     * Crea e inserta un nuevo cliente en el árbol.
     * El código se genera automáticamente de forma incremental.
     * Valida que el DPI y el email no existan ya en el árbol.
     *
     * Retorna:
     *   0 → creado correctamente
     *   1 → DPI duplicado
     *   2 → email duplicado
     */
    public int crear(String nombre, String apellido, String dpi,
                     String email, String password) {
 
        // Validar DPI y email únicos recorriendo el árbol
        if (existeDPI(dpi))    return 1;
        if (existeEmail(email)) return 2;
 
        Cliente nuevo = new Cliente(
            contadorCodigo,
            nombre,
            apellido,
            dpi,
            email,
            password
        );
 
        arbolClientes.insertar(contadorCodigo, nuevo);
        contadorCodigo++;
        return 0;
    }
 
    // =========================================================
    // ACTUALIZAR
    // =========================================================
 
    /*
     * Actualiza los datos de un cliente existente buscándolo por código.
     * El código no se puede cambiar (es la clave del árbol).
     * Valida que el nuevo DPI y email no existan en otro cliente.
     *
     * Retorna:
     *   0 → actualizado correctamente
     *   1 → cliente no encontrado
     *   2 → DPI duplicado en otro cliente
     *   3 → email duplicado en otro cliente
     */
    public int actualizar(int codigoCliente, String nombre, String apellido,
                          String dpi, String email, String password) {
 
        Cliente cliente = (Cliente) arbolClientes.buscar(codigoCliente);
        if (cliente == null) return 1;
 
        // Validar DPI único (ignorando el del mismo cliente)
        if (!cliente.getDpi().equals(dpi) && existeDPI(dpi))       return 2;
        // Validar email único (ignorando el del mismo cliente)
        if (!cliente.getEmail().equals(email) && existeEmail(email)) return 3;
 
        cliente.setNombre(nombre);
        cliente.setApellido(apellido);
        cliente.setDpi(dpi);
        cliente.setEmail(email);
        cliente.setPassword(password);
        return 0;
    }
 
    // =========================================================
    // ELIMINAR
    // =========================================================
 
    /*
     * Elimina un cliente del árbol por su código.
     */
    public void eliminar(int codigoCliente) {
        arbolClientes.eliminar(codigoCliente);
    }
 
    // =========================================================
    // CONSULTAS
    // =========================================================
 
    /*
     * Busca un cliente por su código.
     * Retorna el objeto Cliente si existe, null si no.
     */
    public Cliente buscarPorCodigo(int codigoCliente) {
        return (Cliente) arbolClientes.buscar(codigoCliente);
    }
 
    /*
     * Convierte el árbol a ObservableList para el TableView de JavaFX.
     * Usa recorrido in-orden ASCENDENTE (menor a mayor código).
     */
    public ObservableList<Cliente> obtenerTodosAscendente() {
        ObservableList<Cliente> lista = FXCollections.observableArrayList();
        inOrdenAscendente(arbolClientes.getRaiz(), lista);
        return lista;
    }
 
    /*
     * Convierte el árbol a ObservableList en orden DESCENDENTE.
     * Usado en el reporte de clientes orden descendente.
     */
    public ObservableList<Cliente> obtenerTodosDescendente() {
        ObservableList<Cliente> lista = FXCollections.observableArrayList();
        inOrdenDescendente(arbolClientes.getRaiz(), lista);
        return lista;
    }
 
    /*
     * Recorrido in-orden ascendente (Izq → Raíz → Der).
     * Agrega cada cliente a la lista en orden de menor a mayor código.
     */
    private void inOrdenAscendente(NodoArbol nodo, ObservableList<Cliente> lista) {
        if (nodo != null) {
            inOrdenAscendente(nodo.izq, lista);
            lista.add((Cliente) nodo.dato);
            inOrdenAscendente(nodo.der, lista);
        }
    }
 
    /*
     * Recorrido in-orden descendente (Der → Raíz → Izq).
     * Agrega cada cliente a la lista en orden de mayor a menor código.
     */
    private void inOrdenDescendente(NodoArbol nodo, ObservableList<Cliente> lista) {
        if (nodo != null) {
            inOrdenDescendente(nodo.der, lista);
            lista.add((Cliente) nodo.dato);
            inOrdenDescendente(nodo.izq, lista);
        }
    }
 
    /*
     * Verifica si ya existe un cliente con el DPI dado.
     * Recorre todo el árbol in-orden para buscar coincidencia.
     */
    public boolean existeDPI(String dpi) {
        return buscarDPI(arbolClientes.getRaiz(), dpi);
    }
 
    private boolean buscarDPI(NodoArbol nodo, String dpi) {
        if (nodo == null) return false;
        Cliente c = (Cliente) nodo.dato;
        if (c.getDpi().equals(dpi)) return true;
        return buscarDPI(nodo.izq, dpi) || buscarDPI(nodo.der, dpi);
    }
 
    /*
     * Verifica si ya existe un cliente con el email dado.
     */
    public boolean existeEmail(String email) {
        return buscarEmail(arbolClientes.getRaiz(), email);
    }
 
    private boolean buscarEmail(NodoArbol nodo, String email) {
        if (nodo == null) return false;
        Cliente c = (Cliente) nodo.dato;
        if (c.getEmail().equalsIgnoreCase(email)) return true;
        return buscarEmail(nodo.izq, email) || buscarEmail(nodo.der, email);
    }
 
    /*
     * Retorna el árbol directamente.
     * Útil para otros servicios que necesiten acceder al árbol.
     */
    public ArbolBinarioBusqueda getArbol() {
        return arbolClientes;
    }
}
