/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package flotabuses.servicios;

import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.estructuras.NodoLista;
import flotabuses.enums.EstadoDestino;
import flotabuses.enums.NombreDestino;
import flotabuses.modelos.Destino;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 *
 * @author damiangarcia
 */
public class DestinoService {
    // Instancia única (Singleton)
    private static DestinoService instancia;
 
    // Lista doblemente enlazada ordenada por nombre del destino
    private ListaDoblementeEnlazada listaDestinos;
 
    // Contador interno para generar códigos únicos automáticamente
    private int contadorCodigo;
 
    /*
     * Constructor privado: inicializa la lista vacía y el contador en 1.
     */
    private DestinoService() {
        this.listaDestinos  = new ListaDoblementeEnlazada();
        this.contadorCodigo = 1;
    }
 
    /*
     * Retorna la instancia única del servicio (Singleton).
     */
    public static DestinoService getInstance() {
        if (instancia == null) {
            instancia = new DestinoService();
        }
        return instancia;
    }
 
    // =========================================================
    // CREAR
    // =========================================================
 
    /*
     * Crea e inserta un nuevo destino en la lista ordenada por nombre.
     * El código se genera automáticamente de forma incremental.
     * Valida que no exista ya un destino con el mismo nombre.
     *
     * Retorna:
     *   true  → destino creado correctamente
     *   false → ya existe un destino con ese nombre
     */
    public boolean crear(NombreDestino nombre, LocalDate fechaSalida,
                         double costoBoleto, EstadoDestino estado, String descripcion) {
 
        // Validar nombre único (clave de la lista)
        if (listaDestinos.buscar(nombre.getNombreMostrar()) != null) {
            return false; // Ya existe un destino con ese nombre
        }
 
        Destino nuevo = new Destino(
            contadorCodigo,
            nombre,
            fechaSalida,
            costoBoleto,
            estado,
            descripcion
        );
 
        listaDestinos.insertarOrdenado(nombre.getNombreMostrar(), nuevo);
        contadorCodigo++;
        return true;
    }
 
    // =========================================================
    // ACTUALIZAR
    // =========================================================
 
    /*
     * Actualiza los datos de un destino existente buscándolo por nombre.
     * No se permite cambiar el nombre del destino (es la clave de ordenamiento).
     *
     * Retorna:
     *   true  → actualización exitosa
     *   false → destino no encontrado
     */
    public boolean actualizar(NombreDestino nombre, LocalDate fechaSalida,
                              double costoBoleto, EstadoDestino estado, String descripcion) {
 
        Destino destino = (Destino) listaDestinos.buscar(nombre.getNombreMostrar());
        if (destino == null) {
            return false;
        }
 
        destino.setFechaSalida(fechaSalida);
        destino.setCostoBoleto(costoBoleto);
        destino.setEstado(estado);
        destino.setDescripcion(descripcion);
        return true;
    }
 
    // =========================================================
    // ELIMINAR
    // =========================================================
 
    /*
     * Elimina un destino de la lista por su nombre.
     *
     * Retorna:
     *   true  → eliminado correctamente
     *   false → destino no encontrado
     */
    public boolean eliminar(NombreDestino nombre) {
        return listaDestinos.eliminar(nombre.getNombreMostrar());
    }
 
    // =========================================================
    // CONSULTAS
    // =========================================================
 
    /*
     * Busca un destino por su nombre.
     * Retorna el objeto Destino si existe, null si no.
     */
    public Destino buscarPorNombre(NombreDestino nombre) {
        return (Destino) listaDestinos.buscar(nombre.getNombreMostrar());
    }
 
    /*
     * Convierte la lista enlazada a ObservableList para el TableView de JavaFX.
     * Recorre de cabeza a cola (orden ascendente por nombre).
     */
    public ObservableList<Destino> obtenerTodos() {
        ObservableList<Destino> lista = FXCollections.observableArrayList();
        NodoLista actual = listaDestinos.getCabeza();
        while (actual != null) {
            lista.add((Destino) actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }
 
    /*
     * Retorna solo los destinos con estado CONFIRMADO.
     * Usado en el módulo de asignación de buses (solo se asignan confirmados).
     */
    public ObservableList<Destino> obtenerConfirmados() {
        ObservableList<Destino> lista = FXCollections.observableArrayList();
        NodoLista actual = listaDestinos.getCabeza();
        while (actual != null) {
            Destino d = (Destino) actual.dato;
            if (d.estaConfirmado()) {
                lista.add(d);
            }
            actual = actual.siguiente;
        }
        return lista;
    }
 
    /*
     * Retorna la lista enlazada directamente.
     * Útil para otros servicios que necesiten iterar (ej. AsignacionServicio).
     */
    public ListaDoblementeEnlazada getLista() {
        return listaDestinos;
    }
 
    /*
     * Verifica si ya existe un destino con el nombre dado.
     */
    public boolean existeNombre(NombreDestino nombre) {
        return listaDestinos.buscar(nombre.getNombreMostrar()) != null;
    }
}
