package ies.puesto.peliculas.repository.jdbc;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
class PeliculaRowMapper implements RowMapper<PeliculaEntity> {

    @Override
    public PeliculaEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new PeliculaEntity(
                rs.getString("pelicula_codigo"),
                rs.getString("titulo_texto"),
                rs.getInt("estreno_anio"),
                rs.getInt("duracion_minutos")
        );
    }
}
