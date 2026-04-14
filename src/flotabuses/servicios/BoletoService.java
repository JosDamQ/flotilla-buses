package flotabuses.servicios;

import flotabuses.estructuras.ListaDoblementeEnlazada;
import flotabuses.estructuras.NodoLista;
import flotabuses.modelos.AsignacionBusDestino;
import flotabuses.modelos.Boleto;
import flotabuses.modelos.Cliente;
import java.time.LocalTime;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Servicio para gestión de boletos de viaje.
 * Usa ListaDoblementeEnlazada ordenada por clave "HH:mm_NNN" (hora + código).
 */
public class BoletoService {

    private static BoletoService instancia;
    private final ListaDoblementeEnlazada listaBoletos;
    private int contadorCodigo;

    private BoletoService() {
        this.listaBoletos = new ListaDoblementeEnlazada();
        this.contadorCodigo = 1;
    }

    public static BoletoService getInstance() {
        if (instancia == null) instancia = new BoletoService();
        return instancia;
    }

    /**
     * Crea un boleto para el cliente en la asignación y hora dadas.
     * @return 0=ok, 1=hora no disponible en la asignación
     */
    public int crear(AsignacionBusDestino asignacion, Cliente cliente, LocalTime hora) {
        if (!asignacion.tieneHora(hora)) return 1;
        Boleto boleto = new Boleto(contadorCodigo, asignacion, cliente, hora);
        // Clave "HH:mm_0001" permite múltiples boletos a la misma hora
        String clave = boleto.getClaveOrden() + "_" + String.format("%04d", contadorCodigo);
        listaBoletos.insertarOrdenado(clave, boleto);
        contadorCodigo++;
        return 0;
    }

    /**
     * Elimina un boleto por su código único.
     * @return true si fue eliminado, false si no se encontró
     */
    public boolean eliminar(int codigoBoleto) {
        NodoLista actual = listaBoletos.getCabeza();
        while (actual != null) {
            Boleto b = (Boleto) actual.dato;
            if (b.getCodigoBoleto() == codigoBoleto) {
                return listaBoletos.eliminar(actual.clave);
            }
            actual = actual.siguiente;
        }
        return false;
    }

    /**
     * Retorna todos los boletos ordenados por hora (ascendente).
     */
    public ObservableList<Boleto> obtenerTodos() {
        ObservableList<Boleto> lista = FXCollections.observableArrayList();
        NodoLista actual = listaBoletos.getCabeza();
        while (actual != null) {
            lista.add((Boleto) actual.dato);
            actual = actual.siguiente;
        }
        return lista;
    }

    public ListaDoblementeEnlazada getLista() { return listaBoletos; }
}
