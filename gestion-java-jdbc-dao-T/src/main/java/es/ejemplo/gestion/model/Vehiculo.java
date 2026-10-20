package es.ejemplo.gestion.model;

/** Entidad Vehiculo representada mediante una clase Java convencional. */
public class Vehiculo {
    private String matricula;
    private String marca;
    private String modelo;
    private int anio;

    public Vehiculo() { }

    public Vehiculo(String matricula, String marca, String modelo, int anio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAnio() { return anio; }
    public void setAnio(int anio) { this.anio = anio; }

    @Override
    public String toString() {
        return "Vehiculo{matricula='" + matricula + "', marca='" + marca
                + "', modelo='" + modelo + "', anio=" + anio + "}";
    }
}
