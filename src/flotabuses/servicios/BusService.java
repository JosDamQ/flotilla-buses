/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.servicios;

import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.estructuras.NodoLista;
import flotabuses.enums.EstadoBus;
import flotabuses.enums.TipoBus;
import flotabuses.modelos.Bus;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author damiangarcia
 */
public class BusService {
    private static BusService instancia;
    private ListaDoblementeEnlazada listaBuses;
    private int contadorCodigo;
    
    private BusService() {
        this.listaBuses = new ListaDoblementeEnlazada();
        this.contadorCodigo = 1;
    }
    
    public static BusService getInstance() {
        if (instancia == null) {
            instancia = new BusService();
        }
        return instancia;
    }
    
    public boolean crear(String placa, TipoBus tipo, int capacidad,
                         String color, EstadoBus estado, String descripcion) 
    {
 
        // Validar placa única
        if (listaBuses.buscar(placa.toUpperCase()) != null) {
            return false;
        }
 
        Bus nuevo = new Bus(
            contadorCodigo,
            placa.toUpperCase(),
            tipo,
            capacidad,
            color,
            estado,
            descripcion
        );
 
        listaBuses.insertarOrdenado(placa.toUpperCase(), nuevo);
        contadorCodigo++;
        return true;
    }
    
    
    public boolean actualizar(String placa, int capacidad, String color,
                              EstadoBus estado, String descripcion) 
    {
 
        Bus bus = (Bus) listaBuses.buscar(placa.toUpperCase());
        if (bus == null) {
            return false;
        }
 
        bus.setCapacidad(capacidad);
        bus.setColor(color);
        bus.setEstado(estado);
        bus.setDescripcion(descripcion);
        return true;
    }
    
    public boolean eliminar(String placa) {
        return listaBuses.eliminar(placa.toUpperCase());
    }
    
    public Bus buscarPorPlaca(String placa) {
        return (Bus) listaBuses.buscar(placa.toUpperCase());
    }
    
    public ObservableList<Bus> obtenerTodos() {
        ObservableList<Bus> lista = FXCollections.observableArrayList();
        NodoLista actual = listaBuses.getCabeza();
        while (actual != null) {
            lista.add((Bus) actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }
    
    public ListaDoblementeEnlazada getLista() {
        return listaBuses;
    }
    
    public boolean existePlaca(String placa) {
        return listaBuses.buscar(placa.toUpperCase()) != null;
    }
}
