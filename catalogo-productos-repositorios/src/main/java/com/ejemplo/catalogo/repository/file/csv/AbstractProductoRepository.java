package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public abstract class AbstractProductoRepository extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {


    public AbstractProductoRepository(Path path) {
        super(path);
    }
}
