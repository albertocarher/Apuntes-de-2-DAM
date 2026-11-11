package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Valoracion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.ValoracionEntity;
import es.ejemplo.cinefest.orm.mapper.ValoracionMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ValoracionDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(ValoracionDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public ValoracionDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public ValoracionDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todas las valoraciones ordenadas por id.
     *
     * @return lista de valoraciones; vacía si no hay ninguna o si se produce un error
     */
    public List<Valoracion> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select v from ValoracionEntity v order by v.idValoracion", ValoracionEntity.class)
                    .getResultStream()
                    .map(ValoracionMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca una valoración por su identificador.
     *
     * @param id identificador de la valoración
     * @return la valoración, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Valoracion> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(ValoracionEntity.class, id))
                    .map(ValoracionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una valoración nueva (el id recibido se ignora). No exige compra previa de entrada.
     *
     * @param dato valoración con película, espectador y puntuación entre 1 y 5
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     *         (FK inexistente, valoración duplicada por película/espectador...)
     */
    public int insertar(Valoracion dato) {
        ValoracionEntity entidad = ValoracionMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdValoracion(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdValoracion();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza una valoración existente.
     *
     * @param dato valoración con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Valoracion dato) {
        ValoracionEntity nueva = ValoracionMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdValoracion() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ValoracionEntity v = em.find(ValoracionEntity.class, nueva.getIdValoracion());
                if (v == null) {
                    tx.rollback();
                    return 0;
                }
                v.setIdPelicula(nueva.getIdPelicula());
                v.setIdEspectador(nueva.getIdEspectador());
                v.setPuntuacion(nueva.getPuntuacion());
                v.setComentario(nueva.getComentario());
                v.setFecha(nueva.getFecha());
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
     * Elimina una valoración.
     *
     * @param id identificador de la valoración
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                ValoracionEntity v = em.find(ValoracionEntity.class, id);
                if (v == null) {
                    tx.rollback();
                    return 0;
                }
                em.remove(v);
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

    private boolean validar(ValoracionEntity dato) {
        return dato != null && dato.getIdPelicula() != null && dato.getIdEspectador() != null
                && dato.getPuntuacion() != null && dato.getPuntuacion() >= 1 && dato.getPuntuacion() <= 5;
    }

    /**
     * Valoraciones con el título de la película y el nombre del espectador (C10).
     *
     * @return filas {@code [pelicula, espectador, puntuacion]} ordenadas por película y espectador
     */
    public List<Object[]> valoracionesConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, e.nombre, v.puntuacion from ValoracionEntity v, PeliculaEntity p, "
                                    + "EspectadorEntity e where p.idPelicula = v.idPelicula "
                                    + "and e.idEspectador = v.idEspectador order by p.titulo, e.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("valoracionesConDetalles", ex);
            return List.of();
        }
    }

    /**
     * Películas que no han recibido ninguna valoración.
     *
     * @return filas {@code [id_pelicula, titulo]} ordenadas por título
     */
    public List<Object[]> peliculasSinValoraciones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.idPelicula, p.titulo from PeliculaEntity p where not exists "
                                    + "(select v.idValoracion from ValoracionEntity v "
                                    + "where v.idPelicula = p.idPelicula) order by p.titulo",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("peliculasSinValoraciones", ex);
            return List.of();
        }
    }

    /**
     * Valoraciones de una película.
     *
     * @param idPelicula identificador de la película
     * @return filas {@code [espectador, puntuacion, comentario, fecha]} ordenadas por fecha y espectador
     */
    public List<Object[]> valoracionesPorPelicula(int idPelicula) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select e.nombre, v.puntuacion, v.comentario, v.fecha from ValoracionEntity v, "
                                    + "EspectadorEntity e where e.idEspectador = v.idEspectador "
                                    + "and v.idPelicula = :idPelicula order by v.fecha, e.nombre",
                            Object[].class)
                    .setParameter("idPelicula", idPelicula)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("valoracionesPorPelicula", ex);
            return List.of();
        }
    }

    /**
     * Distribución de valoraciones por puntuación.
     *
     * @return filas {@code [puntuacion, total_valoraciones]} ordenadas por puntuación ascendente
     */
    public List<Object[]> estadisticasPuntuaciones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select v.puntuacion, count(v) from ValoracionEntity v "
                                    + "group by v.puntuacion order by v.puntuacion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("estadisticasPuntuaciones", ex);
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