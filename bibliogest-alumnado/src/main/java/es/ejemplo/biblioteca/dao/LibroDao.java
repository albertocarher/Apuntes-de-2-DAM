package es.ejemplo.biblioteca.dao;
import es.ejemplo.biblioteca.modelo.Libro;

import es.ejemplo.biblioteca.orm.entity.AutorEntity;
import es.ejemplo.biblioteca.orm.entity.LibroEntity;
import es.ejemplo.biblioteca.orm.mapper.LibroMapper;
import es.ejemplo.biblioteca.orm.config.OrmFactory;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.logging.Level;

public class LibroDao implements AutoCloseable {
    private static final Logger LOG=Logger.getLogger(LibroDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;
    
    public LibroDao(String url) {managerFactory=OrmFactory.crear(url);ownFactory=true;}
    
    public LibroDao(EntityManagerFactory factory) {managerFactory=factory;ownFactory=false;}
    private void registrar(String metodo,Exception ex) {LOG.log(Level.WARNING,metodo+" no se pudo completar: "+ex.getMessage());}
    
    public List<Libro> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    public Optional<Libro> buscarPorId(Integer id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(LibroEntity.class, id))
                    .map(LibroMapper::toDomain);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    public int insertar(Libro dato) {
        if (!validar(dato)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                if (em.find(AutorEntity.class, dato.getIdAutor()) == null) {
                    tx.rollback();
                    return 0;
                }
                LibroEntity entidad = LibroMapper.toEntity(dato);
                entidad.setIdLibro(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdLibro();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    public int actualizar(Libro dato) {
        if (!validar(dato) || dato.getIdLibro() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                LibroEntity l = em.find(LibroEntity.class, dato.getIdLibro());
                if (l == null || em.find(AutorEntity.class, dato.getIdAutor()) == null) {
                    tx.rollback();
                    return 0;
                }
                l.setTitulo(dato.getTitulo());
                l.setIsbn(dato.getIsbn());
                l.setGenero(dato.getGenero());
                l.setAnioPublicacion(dato.getAnioPublicacion());
                l.setEjemplares(dato.getEjemplares());
                l.setIdAutor(dato.getIdAutor());
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
                LibroEntity l = em.find(LibroEntity.class, id);
                if (l == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(l);
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("eliminar", ex);
                return 0;
            }
        }
    }

    private boolean validar(Libro dato) {
        return dato != null && dato.getTitulo() != null && !dato.getTitulo().isBlank() && dato.getIsbn() != null && !dato.getIsbn().isBlank() && dato.getGenero() != null && dato.getIdAutor() != null && dato.getEjemplares() != null && dato.getEjemplares() >= 0;
    }

    public List<Libro> buscarPorGenero(String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.genero = :genero order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorGenero", ex);
            return List.of();
        }
    }

    public List<Libro> buscarPorAutor(int idAutor) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.idAutor = :id order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("id", idAutor)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorAutor", ex);
            return List.of();
        }
    }

    public List<Libro> buscarPorTitulo(String texto) {
        if (texto == null) {
            return List.of();
        }
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where lower(l.titulo) like :texto "
                                    + "order by l.titulo, l.idLibro",
                            LibroEntity.class)
                    .setParameter("texto", "%" + texto.toLowerCase(Locale.ROOT) + "%")
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorTitulo", ex);
            return List.of();
        }
    }

    public List<Libro> listarDisponibles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where l.ejemplares > (select count(p) from PrestamoEntity p "
                                    + "where p.idLibro = l.idLibro and p.fechaDevolucion is null) order by l.titulo",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listarDisponibles", ex);
            return List.of();
        }
    }

    public List<Libro> librosDesdeAnio(int anio, String genero) {
        try (var em = managerFactory.createEntityManager()) {
            if (genero == null) {
                return em.createQuery(
                                "select l from LibroEntity l where l.anioPublicacion >= :anio "
                                        + "order by l.anioPublicacion, l.titulo",
                                LibroEntity.class)
                        .setParameter("anio", anio)
                        .getResultStream()
                        .map(LibroMapper::toDomain)
                        .toList();
            }
            return em.createQuery(
                            "select l from LibroEntity l where l.anioPublicacion >= :anio "
                                    + "and l.genero = :genero order by l.anioPublicacion, l.titulo",
                            LibroEntity.class)
                    .setParameter("anio", anio)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("librosDesdeAnio", ex);
            return List.of();
        }

    }
    
    public List<String> generos() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select distinct l.genero from LibroEntity l where l.genero is not null order by l.genero",
                            String.class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("generos", ex);
            return List.of();
        }
    }

    public List<Libro> nuncaPrestados() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l from LibroEntity l where not exists (select p.idPrestamo from PrestamoEntity p "
                                    + "where p.idLibro = l.idLibro) order by l.titulo",
                            LibroEntity.class)
                    .getResultStream()
                    .map(LibroMapper::toDomain)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("nuncaPrestados", ex);
            return List.of();
        }

    }
    
    public List<Libro> masPrestadosQueMedia() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Libro> reservadosSinDisponibilidad() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Object[]> librosConAutor() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l.titulo, a.nombre, a.nacionalidad from LibroEntity l, AutorEntity a "
                                    + "where l.idAutor = a.idAutor order by l.titulo, l.idLibro",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("librosConAutor", ex);
            return List.of();
        }
    }
    
    public List<Object[]> contarPorGenero() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select l.genero, count(l) from LibroEntity l group by l.genero order by l.genero",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("contarPorGenero", ex);
            return List.of();
        }
    }
    
    public List<Object[]> prestamosPorGeneroMinimo(long minimo) {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Object[]> disponibilidadPorLibro() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }
    
    public List<Object[]> estadisticasGenero() {
        // TODO: implementar con EntityManager, JPQL y rollback cuando proceda.
        return List.of();
    }

    @Override public void close() {if(ownFactory && managerFactory.isOpen())managerFactory.close();}
}
