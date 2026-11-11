package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Entrada;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.mapper.EntradaMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EntradaDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(EntradaDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public EntradaDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public EntradaDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todas las entradas ordenadas por id.
     *
     * @return lista de entradas; vacía si no hay ninguna o si se produce un error
     */
    public List<Entrada> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select e from EntradaEntity e order by e.idEntrada", EntradaEntity.class)
                    .getResultStream()
                    .map(EntradaMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca una entrada por su identificador.
     *
     * @param id identificador de la entrada
     * @return la entrada, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Entrada> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(EntradaEntity.class, id))
                    .map(EntradaMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una entrada tal cual (el id recibido se ignora). No comprueba aforo ni el estado de la
     * sesión: para vender con control de aforo se usa {@code SesionDao.venderEntrada}.
     *
     * @param dato entrada con sesión, espectador, fecha de compra y estado ({@code PAGADA}, {@code USADA}
     *             o {@code CANCELADA})
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     *         (FK inexistente, entrada duplicada por sesión/espectador...)
     */
    public int insertar(Entrada dato) {
        EntradaEntity entidad = EntradaMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdEntrada(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdEntrada();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza una entrada existente.
     *
     * @param dato entrada con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Entrada dato) {
        EntradaEntity nueva = EntradaMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdEntrada() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity e = em.find(EntradaEntity.class, nueva.getIdEntrada());
                if (e == null) {
                    tx.rollback();
                    return 0;
                }
                e.setIdSesion(nueva.getIdSesion());
                e.setIdEspectador(nueva.getIdEspectador());
                e.setFechaCompra(nueva.getFechaCompra());
                e.setEstado(nueva.getEstado());
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
     * Elimina físicamente una entrada. Para anular una venta conservando el registro se usa
     * {@link #cancelarEntrada(int)}.
     *
     * @param id identificador de la entrada
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                EntradaEntity e = em.find(EntradaEntity.class, id);
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

    private boolean validar(EntradaEntity dato) {
        return dato != null && dato.getIdSesion() != null && dato.getIdEspectador() != null
                && dato.getFechaCompra() != null && !dato.getFechaCompra().isBlank()
                && dato.getEstado() != null
                && (dato.getEstado().equals("PAGADA") || dato.getEstado().equals("USADA")
                || dato.getEstado().equals("CANCELADA"));
    }

    /**
     * Entradas con el nombre del espectador y el título de la película (C08).
     *
     * @return filas {@code [id_entrada, espectador, pelicula, estado]} ordenadas por id de entrada
     */
    public List<Object[]> entradasConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select n.idEntrada, e.nombre, p.titulo, n.estado from EntradaEntity n, "
                                    + "EspectadorEntity e, SesionEntity s, PeliculaEntity p "
                                    + "where e.idEspectador = n.idEspectador and s.idSesion = n.idSesion "
                                    + "and p.idPelicula = s.idPelicula order by n.idEntrada",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("entradasConDetalles", ex);
            return List.of();
        }
    }

    /**
     * Historial de entradas de un espectador, en cualquier estado (C24).
     *
     * @param idEspectador identificador del espectador
     * @return filas {@code [pelicula, fecha_sesion, estado_entrada, precio]} ordenadas por fecha y película
     */
    public List<Object[]> entradasPorEspectador(int idEspectador) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, s.fecha, n.estado, s.precio from EntradaEntity n, SesionEntity s, "
                                    + "PeliculaEntity p where s.idSesion = n.idSesion "
                                    + "and p.idPelicula = s.idPelicula and n.idEspectador = :idEspectador "
                                    + "order by s.fecha, p.titulo",
                            Object[].class)
                    .setParameter("idEspectador", idEspectador)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("entradasPorEspectador", ex);
            return List.of();
        }
    }

    /**
     * Ventas por día de compra, excluyendo entradas canceladas (C23). El importe es el precio de la sesión.
     *
     * @return filas {@code [fecha_compra, entradas_validas, total]} ordenadas por fecha de compra
     */
    public List<Object[]> ventasPorDia() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select n.fechaCompra, count(n), round(sum(s.precio), 2) from EntradaEntity n, "
                                    + "SesionEntity s where s.idSesion = n.idSesion "
                                    + "and n.estado in ('PAGADA', 'USADA') "
                                    + "group by n.fechaCompra order by n.fechaCompra",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("ventasPorDia", ex);
            return List.of();
        }
    }

    /**
     * Cancela una entrada (C30) en una única transacción, sin borrarla: cambia su estado a
     * {@code CANCELADA}, lo que libera la plaza. El {@code UPDATE} está condicionado a
     * {@code estado <> 'CANCELADA'}, así que no se puede cancelar dos veces con éxito.
     *
     * @param idEntrada identificador de la entrada
     * @return 1 si se canceló; 0 si no existe, ya estaba cancelada o hubo un error (se hace rollback)
     */
    public int cancelarEntrada(int idEntrada) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                int filas = em.createQuery(
                                "update EntradaEntity n set n.estado = 'CANCELADA' "
                                        + "where n.idEntrada = :id and n.estado <> 'CANCELADA'")
                        .setParameter("id", idEntrada)
                        .executeUpdate();
                if (filas != 1) {
                    tx.rollback();
                    return 0;
                }
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("cancelarEntrada", ex);
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