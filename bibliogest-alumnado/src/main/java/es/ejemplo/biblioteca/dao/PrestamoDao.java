package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Prestamo;
import es.ejemplo.biblioteca.orm.entity.PrestamoEntity;
import es.ejemplo.biblioteca.orm.mapper.PrestamoMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PrestamoDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(PrestamoDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public PrestamoDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public PrestamoDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    public List<Prestamo> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT p FROM PrestamoEntity p ORDER BY p.idPrestamo", PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Prestamo> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(PrestamoEntity.class, id))
                    .map(PrestamoMapper::toDomain);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Prestamo prestamo) {
        if (!validar(prestamo)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PrestamoEntity entidad = PrestamoMapper.toEntity(prestamo);
                entidad.setIdPrestamo(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdPrestamo();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Prestamo prestamo) {
        if (prestamo == null || prestamo.getIdPrestamo() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            boolean manejarTx = !tx.isActive();
            try {
                if (manejarTx) tx.begin();

                PrestamoEntity p = em.find(PrestamoEntity.class, prestamo.getIdPrestamo());
                if (p == null) {
                    if (manejarTx && tx.isActive()) tx.rollback();
                    return 0;
                }
                p.setIdSocio(prestamo.getIdSocio());
                p.setIdLibro(prestamo.getIdLibro());
                p.setFechaPrestamo(prestamo.getFechaPrestamo());
                p.setFechaLimite(prestamo.getFechaLimite());
                p.setFechaDevolucion(prestamo.getFechaDevolucion());

                em.flush();
                if (manejarTx) tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (manejarTx && tx.isActive()) tx.rollback();
                registrar("actualizar", ex);
                return 0;
            }
        }
    }

    public int eliminar(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            boolean manejarTx = !tx.isActive();
            try {
                if (manejarTx) tx.begin();

                PrestamoEntity s = em.find(PrestamoEntity.class, id);
                if (s == null) {
                    if (manejarTx && tx.isActive()) tx.rollback();
                    return 0;
                }
                em.remove(s);
                em.flush();

                if (manejarTx) tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (manejarTx && tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                return 0;
            }
        }
    }

    private boolean validar(Prestamo dato) {
        return dato != null && dato.getIdSocio() != null && dato.getIdLibro() != null && dato.getFechaPrestamo() != null && dato.getFechaLimite() != null && dato.getFechaLimite().compareTo(dato.getFechaPrestamo()) >= 0 && (dato.getFechaDevolucion() == null || dato.getFechaDevolucion().compareTo(dato.getFechaPrestamo()) >= 0);
    }

    public List<Prestamo> listarPorSocio(int idSocio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT p FROM PrestamoEntity p WHERE p.idSocio = :idSocio ORDER BY p.idPrestamo", PrestamoEntity.class)
                    .setParameter("idSocio", idSocio)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarPorSocio", ex);
            return List.of();
        }
    }

    public List<Prestamo> listarActivos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT p FROM PrestamoEntity p WHERE p.fechaDevolucion IS NULL ORDER BY p.idPrestamo", PrestamoEntity.class)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarActivos", ex);
            return List.of();
        }
    }

    public List<Prestamo> listarVencidos(String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT p FROM PrestamoEntity p WHERE p.fechaDevolucion IS NULL AND p.fechaLimite < :fecha ORDER BY p.idPrestamo", PrestamoEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream()
                    .map(PrestamoMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarVencidos", ex);
            return List.of();
        }
    }

    public long contarPrestamosPorLibro(int idLibro) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT COUNT(p) FROM PrestamoEntity p WHERE p.idLibro = :idLibro", Long.class)
                    .setParameter("idLibro", idLibro)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            registrar("contarPrestamosPorLibro", ex);
            return 0;
        }
    }

    public List<Object[]> prestamosConSocioYLibro() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT p, p.socio, p.libro FROM PrestamoEntity p JOIN FETCH p.socio JOIN FETCH p.libro ORDER BY p.idPrestamo", Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("prestamosConSocioYLibro", ex);
            return List.of();
        }
    }

    public List<Object[]> rankingMensual(String anioMes, int limite) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("SELECT p.idLibro, COUNT(p) FROM PrestamoEntity p WHERE p.fechaPrestamo LIKE :mes GROUP BY p.idLibro ORDER BY COUNT(p) DESC", Object[].class)
                    .setParameter("mes", anioMes + "%")
                    .setMaxResults(limite)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("rankingMensual", ex);
            return List.of();
        }
    }

    public int registrarPrestamo(int idSocio, int idLibro, String inicio, String limite) {
        return insertar(new Prestamo(null, idSocio, idLibro, inicio, limite, null));
    }

    public int devolver(int id, String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            boolean manejarTx = !tx.isActive();
            try {
                if (manejarTx) tx.begin();

                PrestamoEntity p = em.find(PrestamoEntity.class, id);
                if (p == null || p.getFechaDevolucion() != null) {
                    if (manejarTx && tx.isActive()) tx.rollback();
                    return 0;
                }
                p.setFechaDevolucion(fecha);
                em.flush();

                if (manejarTx) tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (manejarTx && tx.isActive()) tx.rollback();
                registrar("devolver", ex);
                return 0;
            }
        }
    }

    @Override
    public void close() {
        if (ownFactory && managerFactory.isOpen()) {
            managerFactory.close();
        }
    }
}
