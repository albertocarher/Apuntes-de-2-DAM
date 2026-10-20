package es.ejemplo.gestion.dao;

import es.ejemplo.gestion.config.ConexionBD;
import es.ejemplo.gestion.exception.AplicationException;
import es.ejemplo.gestion.model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDao<T> extends ConexionBD {

    public AbstractDao(String url) {
        super(url);
    }

    abstract T mapToClass(ResultSet rs);

    public  List<T> consultar(String query){
        List<T> lista = new ArrayList<>();

        try (Connection connection = this.obtenerConexion()){

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //lista.add(mapeoProducto(rs));
                lista.add(mapToClass(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }
        return lista;
    }

    public  List<T> consultar(PreparedStatement ps){
        List<T> lista = new ArrayList<>();
        try {
            ResultSet rs = ps.executeQuery();
            while(rs.next()) {
                lista.add(mapToClass(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        /*try (Connection connection = this.obtenerConexion()){

            PreparedStatement ps = connection.prepareStatement(query);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                //lista.add(mapeoProducto(rs));
                lista.add(mapToClass(rs));
            }
        } catch (Exception e) {
            throw new AplicationException(e.getMessage(), e);
        }*/
        return lista;
    }

}
