package es.ejemplo.gestion.model;

public class Vehiculo {
    private final String matricula;
    private final String marca;
    private final String modelo;
    private final int anio;

    public Vehiculo(String matricula, String marca, String modelo, int anio) {
        this.matricula = matricula;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
    }

    public String getMatricula() { return matricula; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnio() { return anio; }
}
