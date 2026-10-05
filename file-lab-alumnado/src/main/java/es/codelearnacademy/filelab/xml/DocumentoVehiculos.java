package es.codelearnacademy.filelab.xml;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import es.codelearnacademy.filelab.model.Vehiculo;
import java.util.ArrayList;
import java.util.List;

@JacksonXmlRootElement(localName = "vehiculos")
public class DocumentoVehiculos {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "vehiculo")
    private List<Vehiculo> vehiculos = new ArrayList<>();

    public DocumentoVehiculos() {
    }

    public DocumentoVehiculos(List<Vehiculo> vehiculos) {
        this.vehiculos = vehiculos;
    }

    public List<Vehiculo> getVehiculos() {
        throw new UnsupportedOperationException("Función no implementada");
    }

    public void setVehiculos(List<Vehiculo> vehiculos) {
        throw new UnsupportedOperationException("Función no implementada");
    }
}
