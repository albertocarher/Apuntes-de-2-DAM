package aula.tareas.modelo;

public class Tarea {

    private long id;
    private String titulo;

    public Tarea(long id, String titulo) {
        this.id = id;
        this.titulo = titulo;
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {}
}
