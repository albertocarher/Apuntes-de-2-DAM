package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Autor;

import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import es.ejemplo.biblioteca.orm.mapper.AutorMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class AutorDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(AutorDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public AutorDao(String url) {
        managerFactory = OrmFactory.crear(url);
        ownFactory = true;
    }

    public AutorDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }
    
    public List<Autor> listar() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a order by a.idAutor",
                            AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Autor> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(AutorEntity.class, id))
                    .map(AutorMapper::toDomain);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Autor dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity entidad = AutorMapper.toEntity(dato);
                entidad.setIdAutor(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdAutor();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Autor dato) {
        if (!validar(dato) || dato.getIdAutor() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                AutorEntity a = em.find(AutorEntity.class, dato.getIdAutor());
                if (a == null) {
                    tx.rollback();
                    return 0;
                }
                a.setNombre(dato.getNombre());
                a.setNacionalidad(dato.getNacionalidad());
                a.setAnioNacimiento(dato.getAnioNacimiento());
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
                AutorEntity a = em.find(AutorEntity.class, id);
                if (a == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(a);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                return 0;
            }
        }
    }

    private boolean validar(Autor dato) {
        return dato != null && dato.getNombre() != null && !dato.getNombre().isBlank();
    }

    public List<Autor> buscarPorNacionalidad(String nacionalidad) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a where a.nacionalidad = :nacionalidad order by a.nombre",
                            AutorEntity.class)
                    .setParameter("nacionalidad", nacionalidad)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorNacionalidad", ex);
            return List.of();
        }
    }

    public long contarLibrosPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select count(l) from LibroEntity l where l.idAutor = :id",
                            Long.class)
                    .setParameter("id", idAutor)
                    .getSingleResult();
        } catch (RuntimeException ex) {
            registrar("contarLibrosPorAutor", ex);
            return 0;
        }
    }

    public List<Object[]> autoresConRecuento() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a.nombre, count(l) from AutorEntity a "
                                    + "left join LibroEntity l on l.idAutor = a.idAutor "
                                    + "group by a.idAutor, a.nombre order by a.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("autoresConRecuento", ex);
            return List.of();
        }
    }

    public List<Autor> autoresLeidosPorTresSocios() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select a from AutorEntity a where (select count(distinct p.idSocio) "
                                    + "from PrestamoEntity p, LibroEntity l "
                                    + "where p.idLibro = l.idLibro and l.idAutor = a.idAutor) >= 3 "
                                    + "order by a.nombre",
                            AutorEntity.class)
                    .getResultStream()
                    .map(AutorMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("autoresLeidosPorTresSocios", ex);
            return List.of();
        }

    }


    
    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
