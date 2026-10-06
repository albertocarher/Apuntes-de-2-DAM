package es.codelearnacademy.filelab.validation;

import es.codelearnacademy.filelab.model.Producto;

public final class ProductoValidator {

    private ProductoValidator() {
    }

    public static void validar(Producto producto) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser null");
        }
        if (producto.id() <= 0) {
            throw new IllegalArgumentException("El id debe ser mayor que 0");
        }
        if (producto.nombre() == null || producto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser null, vacío ni contener solo espacios");
        }

        if (!(producto.precio() >= 0)) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (producto.stock() < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}