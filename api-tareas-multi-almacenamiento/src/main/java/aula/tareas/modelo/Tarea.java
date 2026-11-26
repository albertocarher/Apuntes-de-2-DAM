package aula.tareas.modelo;

public record Tarea(Long identificador, String nombre) {

    public Tarea(Long identificador, String nombre) {
        this.identificador = identificador;
        this.nombre = nombre;
    }

    public Tarea (Long identificador){
        this(identificador,"");
    }

    @Override
    public Long identificador() {
        return identificador;
    }

    @Override
    public String nombre() {
        return nombre;
    }

    public Long getIdentificador() {
        return identificador;
    }

}
