package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Espectador;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.mapper.EspectadorMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EspectadorDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(EspectadorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public EspectadorDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public EspectadorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todos los espectadores ordenados por id.
     *
     * @return lista de espectadores; vacía si no hay ninguno o si se produce un error
     */
    public List<Espectador> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select e from EspectadorEntity e order by e.idEspectador", EspectadorEntity.class)
                    .getResultStream()
                    .map(EspectadorMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca un espectador por su identificador.
     *
     * @param id identificador del espectador
     * @return el espectador, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Espectador> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(EspectadorEntity.class, id))
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta un espectador nuevo (el id recibido se ignora). Si {@code activo} es nulo se guarda como 1.
     *
     * @param dato espectador con nombre y email obligatorios; {@code activo} debe ser 0 o 1
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     *         (email duplicado, etc.)
     */
    public int insertar(Espectador dato) {
        EspectadorEntity entidad = EspectadorMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdEspectador(null);
                if (entidad.getActivo() == null) entidad.setActivo(1);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdEspectador();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza un espectador existente.
     *
     * @param dato espectador con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Espectador dato) {
        EspectadorEntity nueva = EspectadorMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdEspectador() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EspectadorEntity e = em.find(EspectadorEntity.class, nueva.getIdEspectador());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setNombre(nueva.getNombre());
                e.setEmail(nueva.getEmail());
                e.setFechaAlta(nueva.getFechaAlta());
                e.setActivo(nueva.getActivo() == null ? 1 : nueva.getActivo());
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
     * Elimina un espectador. Falla (devuelve 0) si tiene entradas o valoraciones y las FK están activas.
     *
     * @param id identificador del espectador
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EspectadorEntity e = em.find(EspectadorEntity.class, id);
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(e);
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

    private boolean validar(EspectadorEntity dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank()
                && dato.getEmail() != null && !dato.getEmail().isBlank()
                && (dato.getActivo() == null || dato.getActivo() == 0 || dato.getActivo() == 1);
    }

    /**
     * Busca un espectador por su email (único).
     *
     * @param email email exacto
     * @return el espectador, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Espectador> buscarPorEmail(String email) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e from EspectadorEntity e where e.email = :email",
                            EspectadorEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(EspectadorMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorEmail", ex);
            return Optional.empty();
        }
    }

    /**
     * Espectadores que nunca han comprado una entrada, en cualquier estado (C09).
     *
     * @return espectadores ordenados por nombre; vacía si no hay o hay error
     */
    public List<Espectador> espectadoresSinEntradas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e from EspectadorEntity e where not exists (select n.idEntrada "
                                    + "from EntradaEntity n where n.idEspectador = e.idEspectador) "
                                    + "order by e.nombre",
                            EspectadorEntity.class)
                    .getResultStream()
                    .map(EspectadorMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("espectadoresSinEntradas", ex);
            return List.of();
        }
    }

    /**
     * Ranking de espectadores por entradas no canceladas (C13).
     *
     * @return filas {@code [espectador, total_entradas]} ordenadas por total DESC y espectador ASC
     */
    public List<Object[]> rankingEspectadores() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e.nombre, count(n) from EspectadorEntity e, EntradaEntity n "
                                    + "where n.idEspectador = e.idEspectador and n.estado in ('PAGADA', 'USADA') "
                                    + "group by e.idEspectador, e.nombre order by count(n) desc, e.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("rankingEspectadores", ex);
            return List.of();
        }
    }

    /**
     * Espectadores con entradas válidas en al menos {@code minimo} sedes distintas (C20).
     *
     * @param minimo número mínimo de sedes distintas
     * @return filas {@code [espectador, sedes_distintas]} ordenadas por sedes DESC y espectador ASC
     */
    public List<Object[]> espectadoresVariasSedes(int minimo) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e.nombre, count(distinct a.idSede) from EspectadorEntity e, EntradaEntity n, "
                                    + "SesionEntity s, SalaEntity a where n.idEspectador = e.idEspectador "
                                    + "and n.estado in ('PAGADA', 'USADA') and s.idSesion = n.idSesion "
                                    + "and a.idSala = s.idSala "
                                    + "group by e.idEspectador, e.nombre "
                                    + "having count(distinct a.idSede) >= :minimo "
                                    + "order by count(distinct a.idSede) desc, e.nombre",
                            Object[].class)
                    .setParameter("minimo", (long) minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("espectadoresVariasSedes", ex);
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