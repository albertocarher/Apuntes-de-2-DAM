package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import com.ejemplo.catalogo.model.ProductosXml;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

public class XmlProductoRepository extends AbstractProductoRepository {
    private final ObjectMapper mapper;

    @Override
    protected List<Producto> readAll() {
        return List.of();
    }

    @Override
    protected void writeAll(List<Producto> elements) {

    }

    public XmlProductoRepository(Path path) {
        super(path);
        list = load();
        mapper = new ObjectMapper();
    }


    public void saveAll(List<Producto> productos) {
        try {
            ProductosXml productosXml = new ProductosXml();
            productosXml.setProductos(productos);
            mapper.writerWithDefaultPrettyPrinter().writeValue(getPath().toFile(), productosXml);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + getPath(), e);
        }
    }


    public List<Producto> load() {
        try {
            ProductosXml productosXml = mapper.readValue(getPath().toFile(), ProductosXml.class);
            list.clear();
            list.addAll(productosXml.getProductos());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return list;
    }
}
