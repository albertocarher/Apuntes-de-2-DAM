package com.ejemplo.catalogo.configuration;

import com.ejemplo.catalogo.repository.file.csv.IProductoRepository;
import com.ejemplo.catalogo.repository.file.csv.JsonProductoRepository;

import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class AppConfiguration{
    private static Path path;
    private static Properties props;
    static IProductoRepository repository;
    public static void main(String[] args) {
        path = Path.of("data", "app.properties");
        props = new Properties();
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            props.load(reader);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        System.out.println("Formato fichero: "+props.getProperty("storage.format"));
        System.out.println("Ruta fichero: "+props.getProperty("storage.fichero","Valor_por_defecto"));
        props.setProperty("app.name", "FileLab");
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            props.store(writer, "Configuración de la aplicación");
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        if(props.getProperty("storage.format").equals("json")){
            repository = new JsonProductoRepository(Path.of(props.getProperty("storage.path")));
        }
    }

}
