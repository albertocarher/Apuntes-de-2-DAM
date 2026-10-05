package es.codelearnacademy.filelab.support;

import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IProductoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoRepositoryMemoria implements IProductoRepository {

    private final List<Producto> productos;

    public ProductoRepositoryMemoria(List<Producto> productos) {
        this.productos = new ArrayList<>(productos);
    }

    @Override
    public List<Producto> findAll() {
        return List.copyOf(productos);
    }

    @Override
    public Optional<Producto> findById(Long id) {
        return productos.stream().filter(producto -> producto.id() == id).findFirst();
    }

    @Override
    public boolean create(Producto producto) {
        return productos.add(producto);
    }

    @Override
    public boolean update(Producto producto) {
        for (int indice = 0; indice < productos.size(); indice++) {
            if (productos.get(indice).id() == producto.id()) {
                productos.set(indice, producto);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Long id) {
        return productos.removeIf(producto -> producto.id() == id);
    }
}
