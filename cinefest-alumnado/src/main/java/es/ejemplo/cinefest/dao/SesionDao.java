package es.ejemplo.cinefest.dao;

import es.ejemplo.cinefest.modelo.Sesion;
import es.ejemplo.cinefest.orm.config.OrmFactory;
import es.ejemplo.cinefest.orm.entity.EntradaEntity;
import es.ejemplo.cinefest.orm.entity.EspectadorEntity;
import es.ejemplo.cinefest.orm.entity.SalaEntity;
import es.ejemplo.cinefest.orm.entity.SesionEntity;
import es.ejemplo.cinefest.orm.mapper.SesionMapper;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO de sesiones. Una entrada es <em>válida</em> si su estado es {@code PAGADA} o {@code USADA}; el
 * precio unitario de los informes de ingresos es {@code sesiones.precio}.
 */
public class SesionDao implements AutoCloseable {
    private static final Logger LOG = Logger.getLogger(SesionDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public SesionDao(String jdbcUrl) {
        managerFactory = OrmFactory.crear(jdbcUrl);
        ownFactory = true;
    }

    public SesionDao(EntityManagerFactory factory) {
        managerFactory = factory;
        ownFactory = false;
    }

    private void registrar(String metodo, Exception ex) {
        LOG.log(Level.WARNING, metodo + " no se pudo completar: " + ex.getMessage());
    }

    /**
     * Lista todas las sesiones ordenadas por id.
     *
     * @return lista de sesiones; vacía si no hay ninguna o si se produce un error
     */
    public List<Sesion> listar() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery("select s from SesionEntity s order by s.idSesion", SesionEntity.class)
                    .getResultStream()
                    .map(SesionMapper::desdeEntidad)
                    .toList();
        } catch (RuntimeException ex) {
            registrar("listar", ex);
            return List.of();
        }
    }

    /**
     * Busca una sesión por su identificador.
     *
     * @param id identificador de la sesión
     * @return la sesión, o {@link Optional#empty()} si no existe o se produce un error
     */
    public Optional<Sesion> buscarPorId(int id) {
        try (var em = managerFactory.createEntityManager()) {
            return Optional.ofNullable(em.find(SesionEntity.class, id))
                    .map(SesionMapper::desdeEntidad);
        } catch (RuntimeException ex) {
            registrar("buscarPorId", ex);
            return Optional.empty();
        }
    }

    /**
     * Inserta una sesión nueva (el id recibido se ignora).
     *
     * @param dato sesión con película, sala, fecha, hora, precio &ge; 0 y estado
     *             ({@code PROGRAMADA}, {@code CANCELADA} o {@code FINALIZADA})
     * @return id generado (&gt; 0); 0 si los datos no son válidos o la BD rechaza la operación
     *         (FK inexistente, sala/fecha/hora duplicadas...)
     */
    public int insertar(Sesion dato) {
        SesionEntity entidad = SesionMapper.aEntidad(dato);
        if (!validar(entidad)) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                entidad.setIdSesion(null);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return entidad.getIdSesion();
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("insertar", ex);
                return 0;
            }
        }
    }

    /**
     * Actualiza una sesión existente.
     *
     * @param dato sesión con su id y los nuevos valores
     * @return 1 si se actualizó; 0 si no existe, los datos no son válidos o hay un error
     */
    public int actualizar(Sesion dato) {
        SesionEntity nueva = SesionMapper.aEntidad(dato);
        if (!validar(nueva) || nueva.getIdSesion() == null) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SesionEntity s = em.find(SesionEntity.class, nueva.getIdSesion());
                if (s == null) {
                    tx.rollback();
                    return 0;
                }
                s.setIdPelicula(nueva.getIdPelicula());
                s.setIdSala(nueva.getIdSala());
                s.setFecha(nueva.getFecha());
                s.setHora(nueva.getHora());
                s.setPrecio(nueva.getPrecio());
                s.setEstado(nueva.getEstado());
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
     * Elimina una sesión. Falla (devuelve 0) si tiene entradas y las FK están activas.
     *
     * @param id identificador de la sesión
     * @return 1 si se eliminó; 0 si no existe o la BD lo impide
     */
    public int eliminar(int id) {
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();
                SesionEntity s = em.find(SesionEntity.class, id);
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

    private boolean validar(SesionEntity dato) {
        return dato != null && dato.getIdPelicula() != null && dato.getIdSala() != null
                && dato.getFecha() != null && !dato.getFecha().isBlank()
                && dato.getHora() != null && !dato.getHora().isBlank()
                && dato.getPrecio() != null && dato.getPrecio() >= 0
                && dato.getEstado() != null
                && (dato.getEstado().equals("PROGRAMADA") || dato.getEstado().equals("CANCELADA")
                || dato.getEstado().equals("FINALIZADA"));
    }

    /**
     * Sesiones con película, sede, sala, fecha y hora (C07).
     *
     * @return filas {@code [id_sesion, pelicula, sede, sala, fecha, hora]} ordenadas por id de sesión
     */
    public List<Object[]> sesionesConDetalles() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, d.nombre, a.nombre, s.fecha, s.hora "
                                    + "from SesionEntity s, PeliculaEntity p, SalaEntity a, SedeEntity d "
                                    + "where p.idPelicula = s.idPelicula and a.idSala = s.idSala "
                                    + "and d.idSede = a.idSede order by s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesConDetalles", ex);
            return List.of();
        }
    }

    /**
     * Sesiones de una fecha (mismas columnas que C04).
     *
     * @param fecha fecha en formato {@code YYYY-MM-DD}
     * @return filas {@code [fecha, hora, id_sesion, precio]} ordenadas por hora e id de sesión
     */
    public List<Object[]> sesionesPorFecha(String fecha) {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.fecha, s.hora, s.idSesion, s.precio from SesionEntity s "
                                    + "where s.fecha = :fecha order by s.hora, s.idSesion",
                            Object[].class)
                    .setParameter("fecha", fecha)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesPorFecha", ex);
            return List.of();
        }
    }

    /**
     * Sesiones programadas ({@code PROGRAMADA}) sin ninguna entrada válida (C17).
     *
     * @return filas {@code [id_sesion, pelicula, fecha]} ordenadas por id de sesión
     */
    public List<Object[]> sesionesSinVentas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, s.fecha from SesionEntity s, PeliculaEntity p "
                                    + "where p.idPelicula = s.idPelicula and s.estado = 'PROGRAMADA' "
                                    + "and not exists (select n.idEntrada from EntradaEntity n "
                                    + "where n.idSesion = s.idSesion and n.estado in ('PAGADA', 'USADA')) "
                                    + "order by s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesSinVentas", ex);
            return List.of();
        }
    }

    /**
     * Plazas vendidas (entradas válidas) y libres por sesión (C15).
     *
     * @return filas {@code [id_sesion, aforo, vendidas, libres]} ordenadas por id de sesión
     */
    public List<Object[]> disponibilidadSesiones() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, a.aforo, count(n), a.aforo - count(n) from SesionEntity s "
                                    + "join SalaEntity a on a.idSala = s.idSala "
                                    + "left join EntradaEntity n on n.idSesion = s.idSesion "
                                    + "and n.estado in ('PAGADA', 'USADA') "
                                    + "group by s.idSesion, a.aforo order by s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("disponibilidadSesiones", ex);
            return List.of();
        }
    }

    /**
     * Sesiones agotadas: entradas válidas &ge; aforo, sin contar cancelaciones (C18).
     *
     * @return filas {@code [id_sesion, pelicula, aforo]} ordenadas por id de sesión
     */
    public List<Object[]> sesionesAgotadas() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.idSesion, p.titulo, a.aforo from SesionEntity s "
                                    + "join PeliculaEntity p on p.idPelicula = s.idPelicula "
                                    + "join SalaEntity a on a.idSala = s.idSala "
                                    + "left join EntradaEntity n on n.idSesion = s.idSesion "
                                    + "and n.estado in ('PAGADA', 'USADA') "
                                    + "group by s.idSesion, p.titulo, a.aforo "
                                    + "having count(n) >= a.aforo order by s.idSesion",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("sesionesAgotadas", ex);
            return List.of();
        }
    }

    /**
     * Top 3 de películas por recaudación de entradas válidas (C21).
     *
     * @return filas {@code [pelicula, entradas_validas, recaudacion]} ordenadas por recaudación DESC y
     *         película ASC
     */
    public List<Object[]> rankingRecaudacion() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select p.titulo, count(n), round(sum(s.precio), 2) from PeliculaEntity p, "
                                    + "SesionEntity s, EntradaEntity n where s.idPelicula = p.idPelicula "
                                    + "and n.idSesion = s.idSesion and n.estado in ('PAGADA', 'USADA') "
                                    + "group by p.idPelicula, p.titulo order by sum(s.precio) desc, p.titulo",
                            Object[].class)
                    .setMaxResults(3)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("rankingRecaudacion", ex);
            return List.of();
        }
    }

    /**
     * Recaudación por sede, incluidas las sedes sin ventas (C22).
     *
     * @return filas {@code [sede, recaudacion]} ordenadas por recaudación DESC y sede ASC
     */
    public List<Object[]> ingresosPorSede() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select d.nombre, round(coalesce(sum(case when n.idEntrada is not null "
                                    + "then s.precio else 0.0 end), 0.0), 2) from SedeEntity d "
                                    + "left join SalaEntity a on a.idSede = d.idSede "
                                    + "left join SesionEntity s on s.idSala = a.idSala "
                                    + "left join EntradaEntity n on n.idSesion = s.idSesion "
                                    + "and n.estado in ('PAGADA', 'USADA') "
                                    + "group by d.idSede, d.nombre "
                                    + "order by coalesce(sum(case when n.idEntrada is not null "
                                    + "then s.precio else 0.0 end), 0.0) desc, d.nombre",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("ingresosPorSede", ex);
            return List.of();
        }
    }

    /**
     * Informe por fecha de sesión: sesiones, entradas válidas y recaudación, incluidos los días sin ventas (C25).
     *
     * @return filas {@code [fecha, sesiones_programadas, entradas_validas, recaudacion]} ordenadas por fecha
     */
    public List<Object[]> informeDiario() {
        try (var em = managerFactory.createEntityManager()) {
            return em.createQuery(
                            "select s.fecha, count(distinct s.idSesion), count(n), "
                                    + "round(coalesce(sum(case when n.idEntrada is not null "
                                    + "then s.precio else 0.0 end), 0.0), 2) from SesionEntity s "
                                    + "left join EntradaEntity n on n.idSesion = s.idSesion "
                                    + "and n.estado in ('PAGADA', 'USADA') "
                                    + "group by s.fecha order by s.fecha",
                            Object[].class)
                    .getResultList();
        } catch (RuntimeException ex) {
            registrar("informeDiario", ex);
            return List.of();
        }
    }

    /**
     * Vende una entrada con control de aforo (C29), todo en una única transacción.
     * <p>
     * Lo primero es un {@code UPDATE} sin cambio de valor sobre la sesión para tomar el bloqueo de escritura
     * de SQLite: así la comprobación de plazas y el {@code INSERT} no pueden intercalarse con otra venta
     * concurrente (evita la sobreventa). Después valida que la sesión exista y esté {@code PROGRAMADA}, que el
     * espectador exista y esté activo, que no tenga ya entrada para esa sesión (aunque esté cancelada, por la
     * restricción {@code UNIQUE}) y que queden plazas libres. Si todo es correcto inserta una entrada
     * {@code PAGADA} y confirma.
     *
     * @param idSesion     identificador de la sesión
     * @param idEspectador identificador del espectador
     * @param fechaCompra  fecha de compra en formato {@code YYYY-MM-DD}
     * @return 1 si se vendió la entrada; 0 si no se cumple alguna validación o hay un error (rollback)
     */
    public int venderEntrada(int idSesion, int idEspectador, String fechaCompra) {
        if (fechaCompra == null || fechaCompra.isBlank()) {
            return 0;
        }
        try (var em = managerFactory.createEntityManager()) {
            var tx = em.getTransaction();
            try {
                tx.begin();

                int bloqueo = em.createQuery("update SesionEntity s set s.estado = s.estado where s.idSesion = :id")
                        .setParameter("id", idSesion)
                        .executeUpdate();
                if (bloqueo == 0) {
                    tx.rollback();
                    return 0;
                }

                SesionEntity sesion = em.find(SesionEntity.class, idSesion);
                if (sesion == null || !"PROGRAMADA".equals(sesion.getEstado())) {
                    tx.rollback();
                    return 0;
                }

                EspectadorEntity espectador = em.find(EspectadorEntity.class, idEspectador);
                if (espectador == null || espectador.getActivo() == null || espectador.getActivo() != 1) {
                    tx.rollback();
                    return 0;
                }

                long existentes = em.createQuery(
                                "select count(n) from EntradaEntity n "
                                        + "where n.idSesion = :s and n.idEspectador = :e",
                                Long.class)
                        .setParameter("s", idSesion)
                        .setParameter("e", idEspectador)
                        .getSingleResult();
                if (existentes > 0) {
                    tx.rollback();
                    return 0;
                }

                SalaEntity sala = em.find(SalaEntity.class, sesion.getIdSala());
                long ocupadas = em.createQuery(
                                "select count(n) from EntradaEntity n "
                                        + "where n.idSesion = :s and n.estado in ('PAGADA', 'USADA')",
                                Long.class)
                        .setParameter("s", idSesion)
                        .getSingleResult();
                if (sala == null || ocupadas >= sala.getAforo()) {
                    tx.rollback();
                    return 0;
                }

                EntradaEntity entrada = new EntradaEntity();
                entrada.setIdSesion(idSesion);
                entrada.setIdEspectador(idEspectador);
                entrada.setFechaCompra(fechaCompra);
                entrada.setEstado("PAGADA");
                em.persist(entrada);
                em.flush();
                tx.commit();
                return 1;
            } catch (RuntimeException ex) {
                if (tx.isActive()) tx.rollback();
                registrar("venderEntrada", ex);
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