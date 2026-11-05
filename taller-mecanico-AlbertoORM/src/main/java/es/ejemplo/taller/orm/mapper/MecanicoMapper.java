package es.ejemplo.taller.orm.mapper;

import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.orm.entity.MecanicoEntity;

public class MecanicoMapper {
    public static MecanicoEntity toEntity (Mecanico vehiculo) {
        return new MecanicoEntity(vehiculo.getIdMecanico(),vehiculo.getNombre(),vehiculo.getEspecialidad(),vehiculo.getTarifaHora());
    }

    public static Mecanico toDomain (MecanicoEntity entity) {
        return new Mecanico(entity.getIdMecanico(),entity.getNombre(),entity.getEspecialidad(),entity.getTarifaHora());
    }
}
