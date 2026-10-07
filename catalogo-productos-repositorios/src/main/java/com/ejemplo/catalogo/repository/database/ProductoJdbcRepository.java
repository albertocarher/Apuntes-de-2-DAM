package com.ejemplo.catalogo.repository.database;

import com.ejemplo.catalogo.model.Producto;

import java.util.List;
import java.util.Optional;

public class ProductoJdbcRepository extends AbstractJdbcRepository<Producto,Long>{

    public ProductoJdbcRepository(String url) {
        super(url);
    }

    @Override
    public List<Producto> findAll() {
        return findAll2();
    }

    @Override
    public Optional<Producto> findById(Long id) {
        Producto producto = new Producto(id,null,0.0);
        return findById2(producto);
    }

    @Override
    public void create(Producto producto) {
    create(producto);
    }

    @Override
    public boolean update(Producto producto) {
        int modificadas = update2(producto);
        if(modificadas > 1){
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(Long id) {
        Producto producto = new Producto(id,null,0.0);
        int modificadas = update2(producto);
        if(modificadas > 1){
            return false;
        }
        return true;
    }
}
