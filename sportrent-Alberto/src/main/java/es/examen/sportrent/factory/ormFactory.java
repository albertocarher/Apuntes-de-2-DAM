package es.examen.sportrent.factory;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.Map;

public final class ormFactory {
    private ormFactory (){}

    public static EntityManagerFactory crear (String jdbcUrl){
        return Persistence.createEntityManagerFactory("sportrent", Map.of("jakarta.persistence.jdbc.url",jdbcUrl,"hibernate.connection.foreign_keys","true"));
    }
}
