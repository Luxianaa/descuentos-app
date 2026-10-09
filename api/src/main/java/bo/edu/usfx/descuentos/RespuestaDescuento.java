package bo.edu.usfx.descuentos;

/** Respuesta del POST /api/descuento. */
public record RespuestaDescuento(double precioOriginal, double porcentaje, double precioFinal) {
}
