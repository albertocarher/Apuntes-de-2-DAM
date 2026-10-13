package es.codelearnacademy.filelab.persona.xml;
import es.codelearnacademy.filelab.model.Persona;
import java.util.*;
public class DocumentoPersonas {
    private List<Persona> personas=new ArrayList<>();
    public DocumentoPersonas() {
    }
    public DocumentoPersonas(List<Persona> personas) {
        this.personas=personas;
    }
    public List<Persona> getPersonas() {
        return personas;
    }
    public void setPersonas(List<Persona> personas) {
        this.personas=personas;
    }
    // TODO: anade las anotaciones Jackson XML para <personas><persona>...</persona></personas>
}
