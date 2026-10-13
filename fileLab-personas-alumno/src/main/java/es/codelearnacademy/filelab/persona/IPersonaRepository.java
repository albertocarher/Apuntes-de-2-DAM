package es.codelearnacademy.filelab.persona;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import es.codelearnacademy.filelab.model.Persona;
import es.codelearnacademy.filelab.model.Producto;
import es.codelearnacademy.filelab.repository.IRepository;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** TODO: documenta la finalidad de esta interfaz. */
public interface IPersonaRepository extends IRepository<Persona,String> {

    @JacksonXmlRootElement(localName = "productos")
    public static class DocumentoProductos {
        @JacksonXmlElementWrapper(useWrapping = false)
        @JacksonXmlProperty(localName = "producto")
        public List<Producto> productos = new ArrayList<>();

        public DocumentoProductos() {}
        public DocumentoProductos(List<Producto> productos) {
            this.productos = new ArrayList<>(productos);
        }
    }

    XmlMapper xmlMapper = new XmlMapper();


    /** TODO: documenta edad, retorno y ausencia de resultados. */
    default List<Persona> findByEdadMinima(int edad) {
        if (edad > 18){
            throw new RuntimeException();
        }
        List<Persona> personas;
        personas = findAll();
        for(Persona p : personas){
            if(p.edad() == edad){
                personas.add(p);
            }
        }
        return personas;
    }
    /** TODO: documenta el parametro activo y el resultado. */
    default List<Persona> findByActivo(boolean activo) {
        List<Persona> personas;
        personas = findAll();
        for(Persona p : personas){
            if(p.activo() == activo){
                personas.add(p);
            }
        }
        return personas;
    }
}
