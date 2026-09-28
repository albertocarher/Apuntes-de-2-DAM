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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CsvRepository extends AbstractRepository implements IRepository {

    private final CSVFormat inputFormat = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();
    private final CSVFormat outputFormat = CSVFormat.DEFAULT.builder()
            .setHeader("id", "nombre", "precio")
            .get();
    List<Producto> productos;

    CsvRepository(Path path) {
        super(path);
        productos = load();
    }


    public List<Producto> load()  {
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
    public void saveAll(List<Producto> productos){
        try{
            try (Writer writer = Files.newBufferedWriter(getPath(), StandardCharsets.UTF_8);
                 CSVPrinter printer = new CSVPrinter(writer, outputFormat)) {
                for (Producto p : productos) printer.printRecord(p.id(), p.nombre(), p.precio());
            }
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
