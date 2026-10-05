package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final IProductoRepository repository;

    public ProductoService(IProductoRepository repository) {
        this.repository = repository;
    }

    public Optional<Producto> maximoPrecio() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public Optional<Producto> minimoPrecio() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public Optional<Producto> maximoStock() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public Optional<Producto> minimoStock() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public int stockTotal() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public double valorInventario() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public List<Producto> sinStock() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public List<Producto> buscar(String texto) {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
