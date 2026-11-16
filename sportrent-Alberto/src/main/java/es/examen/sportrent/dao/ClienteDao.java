package es.examen.sportrent.dao;
import es.examen.sportrent.entity.ClienteEntity;
import es.examen.sportrent.factory.ormFactory;
import es.examen.sportrent.mapper.ClienteMapper;
import es.examen.sportrent.model.Cliente;
import es.examen.sportrent.model.ClienteAlquileres;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDao implements AutoCloseable {

    public final String url = "jdbc:sqlite:datos/sportrent.db";
    private static final Logger LOG = Logger.getLogger(ClienteDao.class.getName());
    private EntityManagerFactory managerFactory;

    private EntityManager abrir() {
        Class<?> util;
        try {
            util = Class.forName("es.examen.sportrent.test.config.JpaUtil");
        } catch (ClassNotFoundException ex) {
            if (managerFactory == null) managerFactory = ormFactory.crear(url);
            return managerFactory.createEntityManager();
        }
        try {
            return (EntityManager) util.getMethod("abrir").invoke(null);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void registrar (String metodo, Exception ex){
        LOG.log(Level.WARNING, metodo + "no se pudo compilar:" + ex.getMessage());
    }


    public List<Cliente> listarTodos() {
        try(var em = abrir()){
            return em.createQuery("select c from ClienteEntity c order by c.dni",ClienteEntity.class).
                    getResultStream()
                    .map(ClienteMapper :: toDomain)
                    .toList();
        }catch (RuntimeException ex){
            registrar("listar",ex);
            return List.of();
        }
    }

    public  Cliente buscarPorId(String dni) {
        if (dni == null) return null;
        try (var em = abrir()){
            return em.createQuery("select c from ClienteEntity c where c.dni = :dni",ClienteEntity.class)
                    .setParameter("dni",dni)
                    .getResultStream()
                    .findFirst()
                    .map(ClienteMapper::toDomain)
                    .orElse(null);
        }
    }

    public boolean insertar(Cliente m) {
        if (m == null || m.getDni() == null || m.getDni().isBlank()
                || m.getNombre() == null || m.getNombre().isBlank()) return false;
        try(var em = abrir()){
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity entidad = ClienteMapper.toEntity(m);
                em.persist(entidad);
                em.flush();
                tx.commit();
                return true;
            }catch (RuntimeException ex){
                if (tx.isActive()) tx.rollback();
                registrar("insertar",ex);
                return false;
            }
        }
    }

    public boolean actualizar(Cliente m) {
        try(var em = abrir()){
            var tx = em.getTransaction();
            try {
                tx.begin();
                ClienteEntity c = em.find(ClienteEntity.class,m.getDni());
                c.setNombre(m.getNombre());
                c.setEmail(m.getEmail());
                c.setTelefono((m.getTelefono()));
                tx.commit();
                return true;
            } catch (RuntimeException ex){
                if(tx.isActive()) tx.rollback();
                registrar("actualizar",ex);
                return false;
            }
        }
    }

    public boolean eliminar(String id) {
        try(var em = abrir()){
            var tx = em.getTransaction();
            try{
                tx.begin();
                ClienteEntity cliente = em.find(ClienteEntity.class,id);
                if(cliente == null){
                    tx.rollback();
                    return false;
                }
                em.remove(cliente);
                tx.commit();
                return true;
            } catch (RuntimeException ex) {
                if(tx.isActive()) tx.rollback();
                registrar("eliminar",ex);
                return false;
            }
        }
    }

    public List<ClienteAlquileres> alquileresPorCliente() {
        try(var em = abrir()){
            return em.createQuery("select new es.examen.sportrent.model.ClienteAlquileres(c.dni, c.nombre, count(a)) " +
                            "from ClienteEntity c left join AlquilerEntity a on a.cliente = c " +
                            "group by c.dni, c.nombre order by c.dni",ClienteAlquileres.class)
                    .getResultList();
        }catch (RuntimeException ex){
            registrar("alquileresPorCliente",ex);
            return List.of();
        }
    }

    public List<ClienteAlquileres> clientesConMasDeDosAlquileres() {
        try(var em = abrir()){
            return em.createQuery("select new es.examen.sportrent.model.ClienteAlquileres(c.dni, c.nombre, count(a)) " +
                            "from ClienteEntity c join AlquilerEntity a on a.cliente = c " +
                            "group by c.dni, c.nombre having count(a) > 2 order by c.dni",ClienteAlquileres.class)
                    .getResultList();
        }catch (RuntimeException ex){
            registrar("clientesConMasDeDosAlquileres",ex);
            return List.of();
        }
    }

    public List<ClienteAlquileres> clientesSuperanReferencia(String dniReferencia) {
        try(var em = abrir()){
            return em.createQuery("select new es.examen.sportrent.model.ClienteAlquileres(c.dni, c.nombre, count(a)) " +
                            "from ClienteEntity c join AlquilerEntity a on a.cliente = c " +
                            "group by c.dni, c.nombre " +
                            "having count(a) > (select count(a2) from AlquilerEntity a2 where a2.cliente.dni = :dniRef) " +
                            "order by c.dni",ClienteAlquileres.class)
                    .setParameter("dniRef",dniReferencia)
                    .getResultList();
        }catch (RuntimeException ex){
            registrar("clientesSuperanReferencia",ex);
            return List.of();
        }
    }

    @Override
    public void close() throws Exception {
        if (managerFactory != null && managerFactory.isOpen()) managerFactory.close();
    }
}