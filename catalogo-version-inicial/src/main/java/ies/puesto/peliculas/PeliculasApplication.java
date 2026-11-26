package ies.puesto.peliculas;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(info = @Info(title = "Catálogo de películas",
        description = "Consulta películas, actores y categorías del catálogo."))
@SpringBootApplication
public class PeliculasApplication {
    public static void main(String[] args) {
        SpringApplication.run(PeliculasApplication.class, args);
    }
}
