package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sala;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.mapper.SalaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SalaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SalaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SalaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public SalaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todas las salas ordenadas por id.
     *
     * @return lista de salas; vacía si no hay ninguna o si se produce un error
     */
    public List<Sala> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SalaEntity s order by s.idSala", SalaEntity.class)
                    .getResultStream()
                    .map(SalaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca una sala por su identificador.
     *
     * @param id identificador de la sala
     * @return la sala, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Sala> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SalaEntity.class, id))
                    .map(SalaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sala nueva (el id recibido se ignora).
     *
     * @param dato sala con nombre, aforo &gt; 0 e id de sede
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     *         (sede inexistente, pareja sede/nombre duplicada...)
     */
    public int insertar(Sala dato) {
        SalaEntity entidad = SalaMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdSala(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdSala();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza una sala existente.
     *
     * @param dato sala con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Sala dato) {
        SalaEntity nueva = SalaMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdSala() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SalaEntity s = em.find(SalaEntity.class, nueva.getIdSala());
                if (s == null) {
                    tx.rollback();
                    return 0;
                }
                s.setNombre(nueva.getNombre());
                s.setAforo(nueva.getAforo());
                s.setIdSede(nueva.getIdSede());
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
     * Elimina una sala. Falla (devuelve 0) si tiene sesiones y las FK están activas.
     *
     * @param id identificador de la sala
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SalaEntity s = em.find(SalaEntity.class, id);
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

    private boolean validar(SalaEntity dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getAforo() != null && dato.getAforo() > 0 && dato.getIdSede() != null;
    }

    /**
     * Salas con el nombre de su sede (C06).
     *
     * @return filas {@code [sede, sala, aforo]} ordenadas por sede y sala
     */
    public List<Object[]> salasConSede() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select d.nombre, a.nombre, a.aforo from SalaEntity a, SedeEntity d "
                                    + "where d.idSede = a.idSede order by d.nombre, a.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("salasConSede", ex);
            return List.of();
        }
    }

    /**
     * Salas sin ninguna sesión programada, es decir, sin sesiones en estado {@code PROGRAMADA} (C28).
     *
     * @return salas ordenadas por nombre de sede y de sala; vacía si no hay ninguna o hay error
     */
    public List<Sala> salasSinSesiones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from SalaEntity a, SedeEntity d where d.idSede = a.idSede "
                                    + "and not exists (select s.idSesion from SesionEntity s "
                                    + "where s.idSala = a.idSala and s.estado = 'PROGRAMADA') "
                                    + "order by d.nombre, a.nombre",
                            SalaEntity.class)
                    .getResultStream()
                    .map(SalaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("salasSinSesiones", ex);
            return List.of();
        }
    }

    /**
     * Ocupación por sala: sesiones y entradas válidas ({@code PAGADA} o {@code USADA}).
     *
     * @return filas {@code [sede, sala, aforo, sesiones, entradas_validas]} ordenadas por sede y sala
     */
    public List<Object[]> ocupacionPorSala() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select d.nombre, a.nombre, a.aforo, count(distinct s.idSesion), count(e) "
                                    + "from SalaEntity a join SedeEntity d on d.idSede = a.idSede "
                                    + "left join SesionEntity s on s.idSala = a.idSala "
                                    + "left join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA', 'USADA') "
                                    + "group by a.idSala, d.nombre, a.nombre, a.aforo "
                                    + "order by d.nombre, a.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("ocupacionPorSala", ex);
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