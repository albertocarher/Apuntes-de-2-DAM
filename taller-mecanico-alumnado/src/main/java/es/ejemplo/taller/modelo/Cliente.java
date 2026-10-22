package es.ejemplo.taller.modelo;
public class Cliente {
    private Integer idCliente;
    private String nombre;
    private String email;
    private String telefono;
    public Cliente() {}
    public Cliente(Integer idCliente, String nombre, String email, String telefono) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
    }
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer value) { this.idCliente = value; }
    public String getNombre() { return nombre; }
    public void setNombre(String value) { this.nombre = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String value) { this.telefono = value; }
}
