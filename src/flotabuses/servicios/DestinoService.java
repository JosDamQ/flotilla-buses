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
 * Servicio de gestion de destinos turisticos de la agencia.
 *
 * <p>Implementa el patron <b>Singleton</b>: existe una unica instancia durante
 * toda la sesion, accesible mediante {@link #getInstance()}.</p>
 *
 * <p>Los destinos se almacenan en una {@link ListaDoblementeEnlazada} ordenada
 * alfabeticamente por {@link NombreDestino#getNombreMostrar()}, que actua como
 * clave de busqueda e insercion.</p>
 *
 * <p>Solo los destinos con estado {@code CONFIRMADO} pueden recibir asignaciones
 * de buses. Este filtro se aplica en {@link #obtenerConfirmados()}.</p>
 *
 * <p>Ejemplo de uso desde un controlador:</p>
 * <pre>
 *   DestinoService svc = DestinoService.getInstance();
 *
 *   // Crear destino
 *   boolean ok = svc.crear(NombreDestino.TIKAL, LocalDate.now().plusDays(30),
 *                           450.0, EstadoDestino.CONFIRMADO, "Visita arqueologica");
 *
 *   // Poblar tabla
 *   tblDestinos.setItems(svc.obtenerTodos());
 *
 *   // Solo confirmados para el ComboBox de asignacion
 *   cmbDestinos.setItems(svc.obtenerConfirmados());
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.estructuras.ListaDoblementeEnlazada
 * @see flotabuses.modelos.Destino
 * @see flotabuses.servicios.AsignacionBusDestinoService
 */
public class DestinoService {

    /** Instancia unica del servicio (Singleton). */
    private static DestinoService instancia;

    /** Lista doblemente enlazada de destinos, ordenada por nombre del destino. */
    private ListaDoblementeEnlazada listaDestinos;

    /** Contador incremental para generar codigos de destino unicos. */
    private int contadorCodigo;

    /**
     * Constructor privado. Inicializa la lista vacia y el contador en 1.
     */
    private DestinoService() {
        this.listaDestinos  = new ListaDoblementeEnlazada();
        this.contadorCodigo = 1;
    }

    /**
     * Retorna la instancia unica del servicio, creandola si no existe.
     *
     * @return instancia Singleton de {@code DestinoService}
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

    /**
     * Crea e inserta un nuevo destino en la lista ordenada por nombre.
     * El codigo se genera automaticamente de forma incremental.
     *
     * <p>La unicidad se valida por nombre: no puede existir dos destinos
     * con el mismo {@link NombreDestino}.</p>
     *
     * @param nombre      nombre del destino del catalogo {@link NombreDestino}
     * @param fechaSalida fecha programada de salida
     * @param costoBoleto costo del boleto en quetzales
     * @param estado      estado inicial ({@code CONFIRMADO} o {@code PENDIENTE})
     * @param descripcion descripcion libre del destino
     * @return {@code true} si el destino fue creado; {@code false} si ya existe uno con ese nombre
     */
    public boolean crear(NombreDestino nombre, LocalDate fechaSalida,
                         double costoBoleto, EstadoDestino estado, String descripcion) {
        if (listaDestinos.buscar(nombre.getNombreMostrar()) != null) {
            return false;
        }
        Destino nuevo = new Destino(
            contadorCodigo, nombre, fechaSalida, costoBoleto, estado, descripcion
        );
        listaDestinos.insertarOrdenado(nombre.getNombreMostrar(), nuevo);
        contadorCodigo++;
        return true;
    }

    // =========================================================
    // ACTUALIZAR
    // =========================================================

    /**
     * Actualiza los datos de un destino existente buscandolo por nombre.
     * El nombre no puede cambiarse (es la clave de ordenamiento en la lista).
     *
     * @param nombre      nombre del destino a actualizar (clave de busqueda)
     * @param fechaSalida nueva fecha de salida
     * @param costoBoleto nuevo costo del boleto
     * @param estado      nuevo estado operativo
     * @param descripcion nueva descripcion
     * @return {@code true} si el destino fue encontrado y actualizado;
     *         {@code false} si no existe un destino con ese nombre
     */
    public boolean actualizar(NombreDestino nombre, LocalDate fechaSalida,
                              double costoBoleto, EstadoDestino estado, String descripcion) {
        Destino destino = (Destino) listaDestinos.buscar(nombre.getNombreMostrar());
        if (destino == null) return false;
        destino.setFechaSalida(fechaSalida);
        destino.setCostoBoleto(costoBoleto);
        destino.setEstado(estado);
        destino.setDescripcion(descripcion);
        return true;
    }

    // =========================================================
    // ELIMINAR
    // =========================================================

    /**
     * Elimina un destino de la lista por su nombre.
     *
     * @param nombre nombre del destino a eliminar
     * @return {@code true} si fue eliminado; {@code false} si no existe
     */
    public boolean eliminar(NombreDestino nombre) {
        return listaDestinos.eliminar(nombre.getNombreMostrar());
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    /**
     * Busca un destino por su nombre en el catalogo.
     *
     * @param nombre nombre del destino buscado
     * @return el {@link Destino} si existe, o {@code null} si no fue encontrado
     */
    public Destino buscarPorNombre(NombreDestino nombre) {
        return (Destino) listaDestinos.buscar(nombre.getNombreMostrar());
    }

    /**
     * Retorna todos los destinos registrados ordenados alfabeticamente por nombre.
     * Utilizado para poblar la tabla en el modulo de Destinos.
     *
     * @return lista observable con todos los destinos
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

    /**
     * Retorna unicamente los destinos con estado {@code CONFIRMADO}.
     * Utilizado en los ComboBox de los modulos de Asignacion y Compra de Boletos,
     * donde solo los destinos confirmados pueden seleccionarse.
     *
     * @return lista observable con destinos confirmados
     */
    public ObservableList<Destino> obtenerConfirmados() {
        ObservableList<Destino> lista = FXCollections.observableArrayList();
        NodoLista actual = listaDestinos.getCabeza();
        while (actual != null) {
            Destino d = (Destino) actual.dato;
            if (d.estaConfirmado()) lista.add(d);
            actual = actual.siguiente;
        }
        return lista;
    }

    /**
     * Retorna la lista doblemente enlazada interna de destinos.
     * Util para el {@link flotabuses.controladores.VisualizadorEstructurasController}
     * y para otros servicios que necesiten recorrer la estructura directamente.
     *
     * @return lista interna de destinos ordenada por nombre
     */
    public ListaDoblementeEnlazada getLista() {
        return listaDestinos;
    }

    /**
     * Verifica si ya existe un destino con el nombre indicado.
     *
     * @param nombre nombre a verificar
     * @return {@code true} si existe; {@code false} si no
     */
    public boolean existeNombre(NombreDestino nombre) {
        return listaDestinos.buscar(nombre.getNombreMostrar()) != null;
    }
}
