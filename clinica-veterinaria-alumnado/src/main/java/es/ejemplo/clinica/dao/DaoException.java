package es.ejemplo.clinica.dao;
public class DaoException extends RuntimeException {

    public DaoException(String mensaje, Throwable causa) { 
        super(mensaje, causa); 
    }

}
