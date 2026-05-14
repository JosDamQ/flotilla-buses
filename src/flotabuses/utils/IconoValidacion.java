/*
 * Clase: IconoValidacion
 * Descripción: Canvas personalizado que dibuja un icono de validación
 *              usando Java Graphics (JavaFX Canvas / GraphicsContext).
 *              - mostrarValido()   → círculo verde con 
 *              - mostrarInvalido() → círculo rojo con 
 *              - limpiar()         → borra el icono (estado neutro)
 */
package flotabuses.utils;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;

/**
 * Icono de validación dibujado con JavaFX Canvas + GraphicsContext.
 * Se coloca junto a un TextField en un HBox para retroalimentación visual.
 */
public class IconoValidacion extends Canvas {

    private static final double SIZE = 18.0;

    public IconoValidacion() {
        super(SIZE, SIZE);
        setManaged(true);
    }

    // =========================================================
    // DIBUJAR VÁLIDO  –  círculo verde + checkmark
    // =========================================================
    public void mostrarValido() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, SIZE, SIZE);

        // Fondo: círculo verde claro
        gc.setFill(Color.web("#eafaf1"));
        gc.fillOval(1, 1, SIZE - 2, SIZE - 2);

        // Borde verde
        gc.setStroke(Color.web("#27ae60"));
        gc.setLineWidth(1.5);
        gc.strokeOval(1, 1, SIZE - 2, SIZE - 2);

        // Checkmark (✔) redondeado
        gc.setStroke(Color.web("#27ae60"));
        gc.setLineWidth(2.2);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        gc.strokePolyline(
            new double[]{ 3.5,  7.5, 14.5 },
            new double[]{ 9.0, 14.0,  4.0 },
            3
        );
    }

    // =========================================================
    // DIBUJAR INVÁLIDO  –  círculo rojo + X
    // =========================================================
    public void mostrarInvalido() {
        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0, 0, SIZE, SIZE);

        // Fondo: círculo rojo claro
        gc.setFill(Color.web("#fdedec"));
        gc.fillOval(1, 1, SIZE - 2, SIZE - 2);

        // Borde rojo
        gc.setStroke(Color.web("#e74c3c"));
        gc.setLineWidth(1.5);
        gc.strokeOval(1, 1, SIZE - 2, SIZE - 2);

        // Cruz (✘) redondeada
        gc.setStroke(Color.web("#e74c3c"));
        gc.setLineWidth(2.2);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.strokeLine(5, 5, 13, 13);
        gc.strokeLine(13, 5, 5, 13);
    }

    // =========================================================
    // LIMPIAR  –  borrar el icono (estado neutro)
    // =========================================================
    public void limpiar() {
        getGraphicsContext2D().clearRect(0, 0, SIZE, SIZE);
    }
}
