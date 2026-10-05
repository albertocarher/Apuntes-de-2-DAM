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
    public XmlProductoRepository(Path path) {
        super(path);
        productos = load();
        mapper = new ObjectMapper();
    }

    @Override
    public void saveAll(List<Producto> productos) {
        try {
            ProductosXml productosXml = new ProductosXml();
            productosXml.setProductos(productos);
            mapper.writerWithDefaultPrettyPrinter().writeValue(getPath().toFile(), productosXml);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar " + getPath(), e);
        }
    }

    @Override
    public List<Producto> load() {
        try {
            ProductosXml productosXml = mapper.readValue(getPath().toFile(), ProductosXml.class);
            productos.clear();
            productos.addAll(productosXml.getProductos());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo cargar " + getPath(), e);
        }
        return productos;
    }
}
