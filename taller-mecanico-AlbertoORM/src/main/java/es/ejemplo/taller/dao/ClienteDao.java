package es.ejemplo.taller.dao;
import es.ejemplo.taller.modelo.Cliente;
import es.ejemplo.taller.orm.config.OrmFactory;
import es.ejemplo.taller.orm.entity.ClienteEntity;
import es.ejemplo.taller.orm.mapper.ClienteMapper;
import jakarta.persistence.EntityManagerFactory;

import java.sql.*;
import java.util.*;
import java.math.BigDecimal;

import static java.lang.module.ModuleDescriptor.read;

public class ClienteDao {
    private final String url;
    private final EntityManagerFactory managerFactory;

    public ClienteDao(String url) {
        this.url = url;
        managerFactory = OrmFactory.crear(url);
    }

    public List<Cliente> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select c from ClienteEntity c order by c.nombre",
                            ClienteEntity.class)
                    .getResultStream()
                    .map(ClienteMapper::toDomain)
                    .toList();
        }
    }

    public Optional<Cliente> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ClienteEntity.class, id))
                    .map(ClienteMapper::toDomain);
        }
    }

    public int insertar(Cliente cliente) {
        if(cliente == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity entidad = ClienteMapper.toEntity(cliente);
                em.persist(entidad);
                em.flush(); // Necesario aquí para leer inmediatamente el ID generado
                cliente.setIdCliente(entidad.getIdCliente());
                tx.commit();
                return cliente.getIdCliente();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                return 0;
            }
        }
    }
    public int actualizar(Cliente cliente) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity c = em.find(ClienteEntity.class, cliente.getIdCliente());
                if (c == null) {
                    tx.commit();
                    return 0;
                }
                ClienteEntity clienteEntityActualizado = ClienteMapper.toEntity(cliente);
                c.setNombre(clienteEntityActualizado.getNombre());
                c.setEmail(clienteEntityActualizado.getEmail());
                c.setTelefono(clienteEntityActualizado.getTelefono());
                tx.commit(); // Hibernate detecta el cambio
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }
    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity c = em.find(ClienteEntity.class, id);
                if (c == null) {
                    tx.commit();
                    return 0;
                }
                em.remove(c);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                throw ex;
            }
        }
    }

    public Optional<Cliente> buscarPorEmail(Integer email) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select c from ClienteEntity c where c.email = :email",
                            ClienteEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(ClienteMapper::toDomain);
        }
    }

    /*"select v from VehiculoEntity v where v.idCliente = :id",
    VehiculoEntity.class)
            .setParameter("id", idCliente)*/
}