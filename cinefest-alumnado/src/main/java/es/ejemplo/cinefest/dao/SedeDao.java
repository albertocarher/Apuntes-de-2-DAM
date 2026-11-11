package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sede;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SedeEntity;
import es.ejemplo.cinefest.orm.mapper.SedeMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SedeDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SedeDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SedeDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public SedeDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todas las sedes ordenadas por id.
     *
     * @return lista de sedes; vacía si no hay ninguna o si se produce un error
     */
    public List<Sede> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SedeEntity s order by s.idSede", SedeEntity.class)
                    .getResultStream()
                    .map(SedeMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca una sede por su identificador.
     *
     * @param id identificador de la sede
     * @return la sede, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Sede> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SedeEntity.class, id))
                    .map(SedeMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sede nueva (el id recibido se ignora).
     *
     * @param dato sede a insertar; el nombre es obligatorio
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     */
    public int insertar(Sede dato) {
        SedeEntity entidad = SedeMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdSede(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdSede();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza una sede existente.
     *
     * @param dato sede con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Sede dato) {
        SedeEntity nueva = SedeMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdSede() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SedeEntity s = em.find(SedeEntity.class, nueva.getIdSede());
                if (s == null) {
                    tx.rollback();
                    return 0;
                }
                s.setNombre(nueva.getNombre());
                s.setMunicipio(nueva.getMunicipio());
                em.flush();
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("actualizar", ex);
                return 0;
            }
        }
    }

    /**
     * Elimina una sede. Falla (devuelve 0) si tiene salas y las FK están activas.
     *
     * @param id identificador de la sede
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SedeEntity s = em.find(SedeEntity.class, id);
                if (s == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(s);
                em.flush();
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                return 0;
            }
        }
    }

    private boolean validar(SedeEntity dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank();
    }

    /**
     * Salas de una sede.
     *
     * @param idSede identificador de la sede
     * @return filas {@code [id_sala, sala, aforo]} ordenadas por nombre de sala; vacía si no hay o hay error
     */
    public List<Object[]> salasPorSede(int idSede) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a.idSala, a.nombre, a.aforo from SalaEntity a "
                                    + "where a.idSede = :idSede order by a.nombre",
                            Object[].class)
                    .setParameter("idSede", idSede)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("salasPorSede", ex);
            return List.of();
        }
    }

    /**
     * Número de salas y aforo total por sede, incluidas las sedes sin salas (C12).
     *
     * @return filas {@code [sede, total_salas, aforo_total]} ordenadas por sede
     */
    public List<Object[]> aforoTotalPorSede() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select d.nombre, count(a), coalesce(sum(a.aforo), 0) from SedeEntity d "
                                    + "left join SalaEntity a on a.idSede = d.idSede "
                                    + "group by d.idSede, d.nombre order by d.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("aforoTotalPorSede", ex);
            return List.of();
        }
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}