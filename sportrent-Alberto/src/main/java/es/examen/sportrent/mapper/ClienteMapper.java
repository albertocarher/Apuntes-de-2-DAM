package es.examen.sportrent.mapper;
import es.examen.sportrent.entity.*;
import es.examen.sportrent.model.*;

public final class ClienteMapper {
    public static ClienteEntity toEntity (Cliente dato){
        ClienteEntity entidad = new ClienteEntity();
        entidad.setDni(dato.getDni());
        entidad.setNombre(dato.getNombre());
        entidad.setEmail(dato.getEmail());
        entidad.setTelefono(dato.getTelefono());
        return entidad;
    }

    public static Cliente toDomain (ClienteEntity entidad){
        return new Cliente(entidad.getDni(), entidad.getNombre(), entidad.getEmail(), entidad.getTelefono());
    }
    
}
