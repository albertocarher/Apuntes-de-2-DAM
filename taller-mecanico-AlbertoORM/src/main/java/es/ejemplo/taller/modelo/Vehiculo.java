package es.ejemplo.taller.modelo;
public class Vehiculo {
    private String matricula;
    private String marca;
    private String modelo;
    private Integer anio;
    private Integer idCliente;
    public Vehiculo() {}
    public Vehiculo(String matricula, String marca, String modelo, Integer anio, Integer idCliente) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.idCliente = idCliente;
    }
    public String getMatricula() { return matricula; }
    public void setMatricula(String value) { this.matricula = value; }
    public String getMarca() { return marca; }
    public void setMarca(String value) { this.marca = value; }
    public String getModelo() { return modelo; }
    public void setModelo(String value) { this.modelo = value; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer value) { this.anio = value; }
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer value) { this.idCliente = value; }
}
