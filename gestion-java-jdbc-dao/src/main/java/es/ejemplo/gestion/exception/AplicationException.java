package es.ejemplo.gestion.exception;

/** Error no recuperable de acceso o integridad de la base de datos. */
public class AplicationException extends RuntimeException {
    public AplicationException(String mensaje) { super(mensaje); }
    public AplicationException(String mensaje, Throwable causa) { super(mensaje, causa); }
}
