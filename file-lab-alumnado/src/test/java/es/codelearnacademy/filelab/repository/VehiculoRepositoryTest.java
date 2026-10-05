package es.codelearnacademy.filelab.repository;

import static org.junit.jupiter.api.Assertions.*;
import es.codelearnacademy.filelab.csv.VehiculoCsvRepository;
import es.codelearnacademy.filelab.support.FixtureSupport;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class VehiculoRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void usaStringComoIdentificador() throws Exception {
        Path archivo = FixtureSupport.copiar("vehiculos.csv", tempDir.resolve("vehiculos.csv"));
        IVehiculoRepository repositorio = new VehiculoCsvRepository(archivo);
        assertEquals("Toyota", repositorio.findById("1234ABC").orElseThrow().marca());
    }
}
