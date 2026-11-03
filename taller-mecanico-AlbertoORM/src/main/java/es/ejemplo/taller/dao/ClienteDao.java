package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.ClienteEntity;
import es.ejemplo.taller.orm.mapper.ClienteMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;
public class ClienteDao  {
    private final String url;
    private final EntityManagerFactory managerFactory;

    public ClienteDao(String url, EntityManagerFactory managerFactory) {
        this.url = url;
        this.managerFactory = OrmFactory.crear(url);
    }
    
    public List<Cliente> listar() {
        // TODO: implementar según el contrato del README.
        return List.of();
    }
    public Optional<Cliente> buscarPorId(Integer id) {
        // TODO: implementar según el contrato del README.
        return Optional.empty();
    }
    public int insertar(Cliente cliente) {
        // TODO: implementar según el contrato del README.
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity entidad = ClienteMapper.toEntity(cliente);
                em.persist(entidad);
                em.flush(); // Necesario aquí para leer inmediatamente el ID generado
                cliente.setIdCliente(entidad.getIdCliente());
                tx.commit();
                return entidad.getIdCliente();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
    public int actualizar(Cliente dato) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
    public int eliminar(Integer id) {
        // TODO: implementar según el contrato del README.
        return 0;
    }
}
