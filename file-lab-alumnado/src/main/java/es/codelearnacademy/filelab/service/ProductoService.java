package es.codelearnacademy.filelab.service;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ProductoService {

    private final IProductoRepository repository;

    public ProductoService(IProductoRepository repository) {
        this.repository = repository;
    }

    public Optional<Producto> maximoPrecio() {
        return repository.findAll().stream()
                .max(Comparator.comparingDouble(Producto::precio));
    }

    public Optional<Producto> minimoPrecio() {
        return repository.findAll().stream()
                .min(Comparator.comparingDouble(Producto::precio));
    }

    public Optional<Producto> maximoStock() {
        return repository.findAll().stream()
                .max(Comparator.comparingInt(Producto::stock));
    }

    public Optional<Producto> minimoStock() {
        return repository.findAll().stream()
                .min(Comparator.comparingInt(Producto::stock));
    }

    public int stockTotal() {
        return repository.findAll().stream()
                .mapToInt(Producto::stock)
                .sum();
    }

    public double valorInventario() {
        return repository.findAll().stream()
                .mapToDouble(producto -> producto.precio() * producto.stock())
                .sum();
    }

    public List<Producto> sinStock() {
        return repository.findAll().stream()
                .filter(producto -> producto.stock() == 0)
                .toList();
    }

    public List<Producto> buscar(String texto) {
        if (texto == null) {
            return List.of();
        }
        var buscado = texto.toLowerCase(Locale.ROOT);
        return repository.findAll().stream()
                .filter(producto -> producto.nombre() != null
                        && producto.nombre().toLowerCase(Locale.ROOT).contains(buscado))
                .toList();
    }
}