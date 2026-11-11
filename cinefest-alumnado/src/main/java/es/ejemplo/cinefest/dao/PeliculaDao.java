package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Pelicula;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.PeliculaEntity;
import es.ejemplo.cinefest.orm.mapper.PeliculaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PeliculaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(PeliculaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public PeliculaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public PeliculaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todas las películas ordenadas por id.
     *
     * @return lista de películas; vacía si no hay ninguna o si se produce un error
     */
    public List<Pelicula> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select p from PeliculaEntity p order by p.idPelicula", PeliculaEntity.class)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca una película por su identificador.
     *
     * @param id identificador de la película
     * @return la película, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Pelicula> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(PeliculaEntity.class, id))
                    .map(PeliculaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una película nueva (el id recibido se ignora).
     *
     * @param dato película a insertar; el título es obligatorio
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     */
    public int insertar(Pelicula dato) {
        PeliculaEntity entidad = PeliculaMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdPelicula(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdPelicula();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza una película existente.
     *
     * @param dato película con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Pelicula dato) {
        PeliculaEntity nueva = PeliculaMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdPelicula() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PeliculaEntity p = em.find(PeliculaEntity.class, nueva.getIdPelicula());
                if (p == null) {
                    tx.rollback();
                    return 0;
                }
                p.setTitulo(nueva.getTitulo());
                p.setGenero(nueva.getGenero());
                p.setDuracionMin(nueva.getDuracionMin());
                p.setPais(nueva.getPais());
                p.setAnio(nueva.getAnio());
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
     * Elimina una película. Falla (devuelve 0) si tiene sesiones o valoraciones y las FK están activas.
     *
     * @param id identificador de la película
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                PeliculaEntity p = em.find(PeliculaEntity.class, id);
                if (p == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(p);
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

    private boolean validar(PeliculaEntity dato) {
        return dato != null && dato.getTitulo() != null && !dato.getTitulo().isBlank();
    }

    /**
     * Películas de un género (C03).
     *
     * @param genero género exacto, por ejemplo {@code "Drama"}
     * @return películas ordenadas por título; vacía si no hay o hay error
     */
    public List<Pelicula> buscarPorGenero(String genero) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PeliculaEntity p where p.genero = :genero order by p.titulo",
                            PeliculaEntity.class)
                    .setParameter("genero", genero)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("buscarPorGenero", ex);
            return List.of();
        }
    }

    /**
     * Películas de un año determinado o posterior.
     *
     * @param anio año mínimo (inclusive)
     * @return películas ordenadas por año y título; vacía si no hay o hay error
     */
    public List<Pelicula> peliculasDesdeAnio(int anio) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PeliculaEntity p where p.anio >= :anio order by p.anio, p.titulo",
                            PeliculaEntity.class)
                    .setParameter("anio", anio)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("peliculasDesdeAnio", ex);
            return List.of();
        }
    }

    /**
     * Películas que no tienen ninguna sesión (C16).
     *
     * @return películas ordenadas por título; vacía si no hay o hay error
     */
    public List<Pelicula> peliculasSinSesiones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p from PeliculaEntity p where not exists (select s.idSesion from SesionEntity s "
                                    + "where s.idPelicula = p.idPelicula) order by p.titulo",
                            PeliculaEntity.class)
                    .getResultStream()
                    .map(PeliculaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("peliculasSinSesiones", ex);
            return List.of();
        }
    }

    /**
     * Número de valoraciones y media por película, incluidas las no valoradas (C26).
     * La media es {@code null} si la película no tiene valoraciones.
     *
     * @return filas {@code [pelicula, numero_valoraciones, media]} ordenadas por película
     */
    public List<Object[]> peliculasConMediaValoraciones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, count(v), round(avg(v.puntuacion), 2) from PeliculaEntity p "
                                    + "left join ValoracionEntity v on v.idPelicula = p.idPelicula "
                                    + "group by p.idPelicula, p.titulo order by p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("peliculasConMediaValoraciones", ex);
            return List.of();
        }
    }

    /**
     * Películas con al menos {@code minimo} valoraciones, de mayor a menor media (C14).
     *
     * @param minimo número mínimo de valoraciones
     * @return filas {@code [pelicula, numero_valoraciones, media]} ordenadas por media DESC y película ASC
     */
    public List<Object[]> mejorValoradas(int minimo) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, count(v), round(avg(v.puntuacion), 2) from PeliculaEntity p, "
                                    + "ValoracionEntity v where v.idPelicula = p.idPelicula "
                                    + "group by p.idPelicula, p.titulo having count(v) >= :minimo "
                                    + "order by round(avg(v.puntuacion), 2) desc, p.titulo",
                            Object[].class)
                    .setParameter("minimo", (long) minimo)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("mejorValoradas", ex);
            return List.of();
        }
    }

    /**
     * Películas con más entradas válidas ({@code PAGADA} o {@code USADA}) que la media de todas las películas,
     * contando como 0 las que no han vendido (C19). La condición {@code ventas > total / nPeliculas} se
     * expresa como {@code ventas * nPeliculas > total} para evitar divisiones.
     *
     * @return filas {@code [pelicula, entradas_validas]} ordenadas por entradas DESC y película ASC
     */
    public List<Object[]> masVendidasQueMedia() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, count(e) from PeliculaEntity p "
                                    + "left join SesionEntity s on s.idPelicula = p.idPelicula "
                                    + "left join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA', 'USADA') "
                                    + "group by p.idPelicula, p.titulo "
                                    + "having count(e) * (select count(p2) from PeliculaEntity p2) > "
                                    + "(select count(e2) from EntradaEntity e2 where e2.estado in ('PAGADA', 'USADA')) "
                                    + "order by count(e) desc, p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("masVendidasQueMedia", ex);
            return List.of();
        }
    }

    /**
     * Estadísticas por género: títulos, sesiones y entradas válidas (C27).
     *
     * @return filas {@code [genero, titulos, sesiones, entradas_validas]} ordenadas por género
     */
    public List<Object[]> estadisticasGenero() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.genero, count(distinct p.idPelicula), count(distinct s.idSesion), count(e) "
                                    + "from PeliculaEntity p "
                                    + "left join SesionEntity s on s.idPelicula = p.idPelicula "
                                    + "left join EntradaEntity e on e.idSesion = s.idSesion "
                                    + "and e.estado in ('PAGADA', 'USADA') "
                                    + "group by p.genero order by p.genero",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("estadisticasGenero", ex);
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