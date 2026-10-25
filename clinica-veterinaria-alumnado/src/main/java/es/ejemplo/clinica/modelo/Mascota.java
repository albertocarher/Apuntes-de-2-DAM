package es.ejemplo.clinica.modelo;

/** Entidad Mascota. Clase Java mutable con constructores y accesores. */
public class Mascota {
    private Integer id;
    private String nombre;
    private String especie;
    private String raza;
    private String fechaNacimiento;
    private Integer idCliente;

    public Mascota() { }

    public Mascota(Integer id, String nombre, String especie, String raza, String fechaNacimiento, Integer idCliente) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.fechaNacimiento = fechaNacimiento;
        this.idCliente = idCliente;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }

    public String getRaza() { return raza; }
    public void setRaza(String raza) { this.raza = raza; }

    public String getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

}
