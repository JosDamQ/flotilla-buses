package flotabuses.enums;

/**
 * Estado de la operacion CRUD activa en un controlador de pantalla.
 *
 * <p>Cada controlador mantiene una variable de instancia de tipo {@code Operaciones}
 * que actua como una maquina de estados simple. Segun el valor actual, los botones
 * y campos se habilitan o deshabilitan de forma apropiada.</p>
 *
 * <p>Flujo tipico de estados en un controlador:</p>
 * <pre>
 *   NINGUNO  ──► NUEVO    ──► GUARDAR  (registro creado)
 *                              └──► NINGUNO
 *
 *   NINGUNO  ──► EDITAR   ──► ACTUALIZAR (registro modificado)
 *                              └──► NINGUNO
 *
 *   NINGUNO  ──► ELIMINAR ──► confirmacion ──► NINGUNO
 *
 *   cualquiera ──► CANCELAR ──► NINGUNO
 * </pre>
 *
 * @author damiangarcia
 * @version 1.0
 * @see flotabuses.controladores.ClienteController
 * @see flotabuses.controladores.BusController
 * @see flotabuses.controladores.DestinoController
 */
public enum Operaciones {

    /** El usuario inicio el flujo de registro de un nuevo elemento. */
    NUEVO,

    /** El sistema esta listo para persistir un nuevo registro en la estructura. */
    GUARDAR,

    /** El usuario confirmo la eliminacion de un elemento seleccionado. */
    ELIMINAR,

    /** El usuario selecciono un elemento para modificarlo. */
    EDITAR,

    /** El sistema esta listo para aplicar los cambios al registro existente. */
    ACTUALIZAR,

    /** El usuario cancelo la operacion en curso; el formulario vuelve al estado inicial. */
    CANCELAR,

    /** Estado inicial o de reposo; ningun flujo CRUD esta activo. */
    NINGUNO
}
