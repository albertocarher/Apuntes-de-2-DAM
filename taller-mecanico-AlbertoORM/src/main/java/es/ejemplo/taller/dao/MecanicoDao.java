package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Mecanico;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.ClienteEntity;
import es.ejemplo.taller.orm.entity.MecanicoEntity;
import es.ejemplo.taller.orm.mapper.ClienteMapper;
import es.ejemplo.taller.orm.mapper.MecanicoMapper;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class MecanicoDao  {
    private final String url;
    private final EntityManagerFactory managerFactory;

    public MecanicoDao(String url) {
        this.url = url;
        managerFactory = OrmFactory.crear(url);
    }
    
    public List<Mecanico> listar() {
        // TODO: implementar según el contrato del README.
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select m from MecanicoEntity m order by m.idMecanico",
                            MecanicoEntity.class)
                    .getResultStream()
                    .map(MecanicoMapper::toDomain)
                    .toList();
        }
    }
    public Optional<Mecanico> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(MecanicoEntity.class, id))
                    .map(MecanicoMapper::toDomain);
        }
    }
    public int insertar(Mecanico mecanico) {
        // TODO: implementar según el contrato del README.
        if(mecanico == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                MecanicoEntity entidad = MecanicoMapper.toEntity(mecanico);
                em.persist(entidad);
                em.flush(); // Necesario aquí para leer inmediatamente el ID generado
                mecanico.setIdMecanico(entidad.getIdMecanico());
                tx.commit();
                return mecanico.getIdMecanico();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }
    public int actualizar(Mecanico dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public List<Mecanico> buscarPorEspecialidad(String especialidad) {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
}
