package aula.tareas.repository;

import aula.tareas.api.TareaRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository("repositoryJdbc")
public class TareaRepositoryJdbc implements ITareaRepository{

    private String jdbcUrl;

    public TareaRepositoryJdbc() {
        this("jdbc:sqlite:data/tareas.sqlite3");
    }

    public TareaRepositoryJdbc(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        if(!validarUrl(jdbcUrl)) {
            throw new RuntimeException("No se encuentra la bbdd");
        }
    }

    private boolean validarUrl(String jdbcUrl) {
        String url = jdbcUrl.replace("jdbc:sqlite:", "");
        Path path = Path.of(url);
        return (Files.exists(path));
    }

    @Override
    public TareaRequest create(String titulo) {
        return null;
    }

    @Override
    public boolean delete(long identificador) {
        return false;
    }

    @Override
    public List<TareaRequest> findAll() {
        String sql = "SELECT id, titulo FROM tareas_jbdc ORDER BY id";
        List<TareaRequest> tareas = new ArrayList<>();
        try (Connection c = DriverManager.getConnection(jdbcUrl);
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                tareas.add(new TareaRequest(
                        rs.getLong("id"),
                        rs.getString("titulo")));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return tareas;
    }

    @Override
    public TareaRequest findById(long identificador) {
        return null;
    }

    @Override
    public TareaRequest update(TareaRequest tarea) {
        return null;
    }
}