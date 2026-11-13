package es.examen.sportrent.dao;
import es.examen.sportrent.entity.ClienteEntity;
import es.examen.sportrent.factory.ormFactory;
import es.examen.sportrent.mapper.ClienteMapper;
import es.examen.sportrent.model.Cliente;
import es.examen.sportrent.model.ClienteAlquileres;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteDao implements AutoCloseable {

    public final String url = "jdbc:sqlite:datos/sportrent.db";
    private static final Logger LOG = Logger.getLogger(ClienteDao.class.getName());
    private final EntityManagerFactory managerFactory;
    private final boolean ownFactory;

    public ClienteDao(){
        this.managerFactory = ormFactory.crear(url);
        this.ownFactory = true;
    }

    private void registrar (String metodo, Exception ex){
        LOG.log(Level.WARNING, metodo + "no se pudo compilar:" + ex.getMessage());
    }


    public List<Cliente> listarTodos() {
        try(var em = managerFactory.createEntityManager()){
            return em.createQuery("select c from ClienteEntity c order by id",ClienteEntity.class).
                    getResultStream()
                    .map(ClienteMapper :: toDomain)
                    .toList();
        }catch (RuntimeException ex){
            registrar("listar",ex);
            return List.of();
        }
    }

    public  Cliente buscarPorId(String dni) {
        try (var em = managerFactory.createEntityManager()){
            return em.createQuery("select c from ClienteEntity c where c.dni = ?",ClienteEntity.class)
                    .setParameter("dni",dni)
                    .getResultStream()
                    .findFirst()
                    .map(ClienteMapper::toDomain)
                    .orElseThrow();
        }
    }

    public boolean insertar(Cliente m) {
        try(var em = managerFactory.createEntityManager()){
         var tx = em.getTransaction();
         try {
             tx.begin();
             ClienteEntity entidad = ClienteMapper.toEntity(m);
             entidad.setDni(null);
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
        try(var em = managerFactory.createEntityManager()){
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
        try(var em = managerFactory.createEntityManager()){
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
        throw new UnsupportedOperationException("No implementado");    
    }
    
    public List<ClienteAlquileres> clientesConMasDeDosAlquileres() {
        throw new UnsupportedOperationException("No implementado");
    }

    public List<ClienteAlquileres> clientesSuperanReferencia(String dniReferencia) {
        throw new UnsupportedOperationException("No implementado");    }

    @Override
    public void close() throws Exception {

    }
}
