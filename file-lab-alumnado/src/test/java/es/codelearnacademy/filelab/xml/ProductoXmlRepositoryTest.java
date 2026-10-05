package es.codelearnacademy.filelab.xml;

import es.codelearnacademy.filelab.repository.IProductoRepository;
import es.codelearnacademy.filelab.repository.ProductoRepositoryContractTest;
import es.codelearnacademy.filelab.support.FixtureSupport;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

class ProductoXmlRepositoryTest extends ProductoRepositoryContractTest {

    @TempDir
    Path tempDir;

    @Override
    protected IProductoRepository crearRepositorio(Path archivo) {
        return new ProductoXmlRepository(archivo);
    }

    @Override
    protected Path crearFixture() throws Exception {
        return FixtureSupport.copiar("productos.xml", tempDir.resolve("productos.xml"));
    }
}
