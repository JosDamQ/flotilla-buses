/**
 * Implementaciones propias de las tres estructuras de datos del sistema.
 *
 * <p>Ninguna clase de este paquete depende de colecciones de la biblioteca
 * estandar de Java ({@code ArrayList}, {@code HashMap}, etc.).
 * Toda la logica de enlace, busqueda e insercion es implementada
 * manualmente sobre nodos con punteros.</p>
 *
 * <h2>Estructuras disponibles</h2>
 *
 * <table border="1">
 *   <caption>Resumen de estructuras</caption>
 *   <tr><th>Clase</th><th>Tipo</th><th>Complejidad busqueda</th><th>Uso en el sistema</th></tr>
 *   <tr>
 *     <td>{@link flotabuses.estructuras.ArbolBinarioBusqueda}</td>
 *     <td>ABB con clave entera</td>
 *     <td>O(log n) promedio, O(n) peor caso</td>
 *     <td>Clientes (clave = codigoCliente)</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.estructuras.ListaDoblementeEnlazada}</td>
 *     <td>Lista ordenada alfabeticamente</td>
 *     <td>O(n)</td>
 *     <td>Buses (placa), Destinos (nombre), Horas, Boletos</td>
 *   </tr>
 *   <tr>
 *     <td>{@link flotabuses.estructuras.MatrizOrtogonal}</td>
 *     <td>Matriz dispersa con listas enlazadas</td>
 *     <td>O(f + c) donde f=filas, c=columnas</td>
 *     <td>Asignaciones bus-destino</td>
 *   </tr>
 * </table>
 *
 * <h2>Clases de nodo</h2>
 * <ul>
 *   <li>{@link flotabuses.estructuras.NodoArbol} — nodo del ABB con clave entera y punteros izq/der.</li>
 *   <li>{@link flotabuses.estructuras.NodoLista} — nodo de la lista con clave de texto y punteros anterior/siguiente.</li>
 *   <li>{@link flotabuses.estructuras.NodoCabecera} — cabecera de fila o columna de la matriz ortogonal.</li>
 *   <li>{@link flotabuses.estructuras.NodoMatriz} — celda de la matriz con navegacion en 4 direcciones.</li>
 * </ul>
 *
 * @author damiangarcia
 * @version 1.0
 */
package flotabuses.estructuras;
