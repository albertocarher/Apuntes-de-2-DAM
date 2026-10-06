package com.ejemplo.catalogo.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "productos")
public class ProductosXML {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "producto")
    public List<Producto> productos = new ArrayList<>();

    public ProductosXML() {
        productos = new ArrayList<>();
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}