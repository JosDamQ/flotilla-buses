/**
 * Entidades de dominio del sistema de flotilla de buses.
 *
 * <p>Las clases de este paquete son POJOs (Plain Old Java Objects) que
 * representan los conceptos del negocio. No contienen logica de persistencia
 * ni acceso a datos; esa responsabilidad recae en los servicios del paquete
 * {@link flotabuses.servicios}.</p>
 *
 * <h2>Entidades y su estructura de almacenamiento</h2>
 * <ul>
 *   <li>{@link flotabuses.modelos.Cliente} — almacenado en
 *       {@link flotabuses.estructuras.ArbolBinarioBusqueda}, clave = {@code codigoCliente}.</li>
 *   <li>{@link flotabuses.modelos.Bus} — almacenado en
 *       {@link flotabuses.estructuras.ListaDoblementeEnlazada}, clave = placa.</li>
 *   <li>{@link flotabuses.modelos.Destino} — almacenado en
 *       {@link flotabuses.estructuras.ListaDoblementeEnlazada}, clave = nombre del destino.</li>
 *   <li>{@link flotabuses.modelos.AsignacionBusDestino} — almacenado como celda en
 *       {@link flotabuses.estructuras.MatrizOrtogonal} [destino][bus].</li>
 *   <li>{@link flotabuses.modelos.Boleto} — almacenado en
 *       {@link flotabuses.estructuras.ListaDoblementeEnlazada}, clave = "HH:mm_NNNN".</li>
 *   <li>{@link flotabuses.modelos.Usuario} — gestionado directamente por
 *       {@link flotabuses.servicios.LoginService}, sin estructura de datos propia.</li>
 * </ul>
 *
 * <h2>Relaciones entre entidades</h2>
 * <pre>
 *   Usuario
 *
 *   Cliente ──────────────────────────────────────────┐
 *                                                     |
 *   Bus ──────────────────────┐                       |
 *                             |                       |
 *   Destino ──────────────────┤                       |
 *                             v                       v
 *                    AsignacionBusDestino ──────► Boleto
 *                    (contiene Lista de horas)
 * </pre>
 *
 * @author damiangarcia
 * @version 2.0
 */
package flotabuses.modelos;
