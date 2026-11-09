package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Reserva;
import es.ejemplo.biblioteca.orm.entity.ReservaEntity;
import es.ejemplo.biblioteca.orm.mapper.ReservaMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ReservaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ReservaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public ReservaDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public ReservaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    public List<Reserva> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT r FROM ReservaEntity r ORDER BY r.idReserva", ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Reserva> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ReservaEntity.class, id))
                    .map(ReservaMapper::toDomain);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Reserva dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ReservaEntity entidad = ReservaMapper.toEntity(dato);
                entidad.setIdReserva(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdReserva();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Reserva dato) {
        if (!validar(dato) || dato.getIdReserva() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ReservaEntity r = em.find(ReservaEntity.class, dato.getIdReserva());
                if (r == null) {
                    tx.rollback();
                    return 0;
                }
                r.setIdSocio(dato.getIdSocio());
                r.setIdLibro(dato.getIdLibro());
                r.setFechaReserva(dato.getFechaReserva());
                r.setEstado(dato.getEstado());
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

    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ReservaEntity r = em.find(ReservaEntity.class, id);
                if (r == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(r);
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

    private boolean validar(Reserva dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null && dato.getFechaReserva() != null && dato.getEstado() != null && (dato.getEstado().equals("PENDIENTE") || dato.getEstado().equals("ATENDIDA") || dato.getEstado().equals("CANCELADA"));
    }

    public List<Reserva> listarPendientes() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT r FROM ReservaEntity r WHERE r.estado = 'PENDIENTE' ORDER BY r.fechaReserva ASC", ReservaEntity.class)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarPendientes", ex);
            return List.of();
        }
    }

    public List<Reserva> buscarPorSocio(int idSocio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT r FROM ReservaEntity r WHERE r.idSocio = :idSocio ORDER BY r.idReserva", ReservaEntity.class)
                    .setParameter("idSocio", idSocio)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorSocio", ex);
            return List.of();
        }
    }

    public List<Reserva> listarPorLibro(int idLibro) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT r FROM ReservaEntity r WHERE r.idLibro = :idLibro ORDER BY r.idReserva", ReservaEntity.class)
                    .setParameter("idLibro", idLibro)
                    .getResultStream()
                    .map(ReservaMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarPorLibro", ex);
            return List.of();
        }
    }

    public long contarReservasPendientes() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT COUNT(r) FROM ReservaEntity r WHERE r.estado = 'PENDIENTE'", Long.class)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            registrar("contarReservasPendientes", ex);
            return 0;
        }
    }

    public List<Object[]> reservasConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT r, r.socio, r.libro FROM ReservaEntity r JOIN FETCH r.socio JOIN FETCH r.libro ORDER BY r.idReserva", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("reservasConDetalles", ex);
            return List.of();
        }
    }

    public List<Object[]> prioridadReservas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT r FROM ReservaEntity r WHERE r.estado = 'PENDIENTE' ORDER BY r.fechaReserva ASC, r.idReserva ASC", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("prioridadReservas", ex);
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
