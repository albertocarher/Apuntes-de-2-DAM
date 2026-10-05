package com.ejemplo.catalogo.repository.file.csv;

import com.ejemplo.catalogo.model.Producto;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class CsvProductoRepository extends AbstractFileRepository<Producto, Long>
        implements IProductoRepository {

    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "nombre", "precio")
            .get();

    public CsvProductoRepository(Path path) {
        super(path);
    }

    @Override
    protected List<Producto> readAll() {
        try (Reader reader = Files.newBufferedReader(getPath(), StandardCharsets.UTF_8);
             CSVParser parser = inputFormat.parse(reader)) {
            for (CSVRecord row : parser) {
                list.add(new Producto(
                        Long.parseLong(row.get("id")),
                        row.get("nombre"),
                        Double.parseDouble(row.get("precio"))));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            //logger error
        }
        return list;
    }

    @Override
    protected void writeAll(List<Producto> elements) {
        try{
            try (Writer writer = Files.newBufferedWriter(getPath(), StandardCharsets.UTF_8);
                 CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {
                for (Producto p : elements) printer.printRecord(p.id(), p.nombre(), p.precio());
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }

   /* List<Producto> productos;

    @Override
    protected List<Producto> readAll() {
        return List.of();
    }

    CsvProductoRepository(Path path) {
        super(path);
        list = load();
    }


    public List<T> load()  {
        try (Reader reader = Files.newBufferedReader(getPath(), StandardCharsets.UTF_8);
             CSVParser parser = inputFormat.parse(reader)) {
            for (CSVRecord row : parser) {
                productos.add(new Producto(
                        Long.parseLong(row.get("id")),
                        row.get("nombre"),
                        Double.parseDouble(row.get("precio"))));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
            //logger error
        }
        return productos;
    }

    @Override
    public void writeAll(List<T> productos){
        try{
            try (Writer writer = Files.newBufferedWriter(getPath(), StandardCharsets.UTF_8);
                 CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {
                for (T p : productos) printer.printRecord(p.id(), p.nombre(), p.precio());
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }*/
}
