package es.ejemplo.biblioteca.dao;

import es.ejemplo.biblioteca.modelo.Socio;
import es.ejemplo.biblioteca.orm.entity.SocioEntity;
import es.ejemplo.biblioteca.orm.mapper.SocioMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class SocioDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SocioDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SocioDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public SocioDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    public List<Socio> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s from SocioEntity s order by s.idSocio",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Socio> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SocioEntity.class, id))
                    .map(SocioMapper::toDomain);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Socio dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity entidad = SocioMapper.toEntity(dato);
                entidad.setIdSocio(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdSocio();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Socio dato) {
        if (!validar(dato) || dato.getIdSocio() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SocioEntity s = em.find(SocioEntity.class, dato.getIdSocio());
                if (s == null) {
                    tx.rollback();
                    return 0;
                }
                s.setNombre(dato.getNombre());
                s.setEmail(dato.getEmail());
                s.setTelefono(dato.getTelefono());
                s.setFechaAlta(dato.getFechaAlta());
                s.setActivo(dato.getActivo());
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
                SocioEntity s = em.find(SocioEntity.class, id);
                if (s == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(s);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                return 0;
            }
        }
    }

    private boolean validar(Socio dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank() && dato.getEmail() != null && !dato.getEmail().isBlank() && dato.getFechaAlta() != null && dato.getActivo() != null;
    }

    public List<Socio> listarActivos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s from SocioEntity s where s.activo = :activo order by s.fechaAlta desc, s.idSocio",
                            SocioEntity.class)
                    .setParameter("activo", true)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarActivos", ex);
            return List.of();
        }
    }

    public Optional<Socio> buscarPorEmail(String email) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s from SocioEntity s where s.email = :email",
                            SocioEntity.class)
                    .setParameter("email", email)
                    .getResultStream()
                    .findFirst()
                    .map(SocioMapper::toDomain);
        } catch (RuntimeException ex) {
            registrar("buscarPorEmail", ex);
            return Optional.empty();
        }
    }

    public List<Socio> listarConPrestamosVencidos(String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s from SocioEntity s where exists (select p.idPrestamo from PrestamoEntity p "
                                    + "where p.idSocio = s.idSocio and p.fechaDevolucion is null "
                                    + "and p.fechaLimite < :fecha) order by s.nombre",
                            SocioEntity.class)
                    .setParameter("fecha", fecha)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarConPrestamosVencidos", ex);
            return List.of();
        }
    }

    public List<Socio> sociosSinPrestamos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s from SocioEntity s where not exists (select p.idPrestamo from PrestamoEntity p "
                                    + "where p.idSocio = s.idSocio) order by s.nombre",
                            SocioEntity.class)
                    .getResultStream()
                    .map(SocioMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("sociosSinPrestamos", ex);
            return List.of();
        }
    }

    public List<Object[]> rankingSocios(int limite) {
        if (limite <= 0) {
            return List.of();
        }
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT s.idSocio, COUNT(p) FROM PrestamoEntity p JOIN SocioEntity s ON p.idSocio = s.idSocio GROUP BY s.idSocio ORDER BY COUNT(p) DESC", Object[].class)
                    .setMaxResults(limite)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("rankingSocios", ex);
            return List.of();
        }
    }

    public List<Object[]> historialSocio(int idSocio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "SELECT p, p.libro FROM PrestamoEntity p JOIN FETCH p.libro WHERE p.idSocio = :idSocio ORDER BY p.fechaPrestamo DESC",
                            Object[].class)
                    .setParameter("idSocio", idSocio)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("historialSocio", ex);
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
