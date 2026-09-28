package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;

import java.util.List;
import java.util.Optional;

public interface IRepository {
    /**
     * Funcion que obtiene todos los elementos
     * @return List de productos
     */
    List<Producto> findAll();

    /**
     * Funcion que obtieneel elemento
     * @param id identificador del producto
     * @return Optional del producto
     */
    Optional<Producto> findById(long id);

    /**
     * Crea el producto
     * @param entity
     */
    void create(Producto entity);

    /**
     * Actualiza un producto
     * @param entity
     * @return
     */
    boolean update(Producto entity);

    /**
     * Borra un producto
     * @param id
     * @return
     */
    boolean delete(long id);
}
