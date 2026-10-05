package es.codelearnacademy.filelab.csv;

import es.codelearnacademy.filelab.repository.IProductoRepository;
import es.codelearnacademy.filelab.repository.ProductoRepositoryContractTest;
import es.codelearnacademy.filelab.support.FixtureSupport;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

class ProductoCsvRepositoryTest extends ProductoRepositoryContractTest {

    @TempDir
    Path tempDir;

    @Override
    protected IProductoRepository crearRepositorio(Path archivo) {
        return new ProductoCsvRepository(archivo);
    }

    @Override
    protected Path crearFixture() throws Exception {
        return FixtureSupport.copiar("productos.csv", tempDir.resolve("productos.csv"));
    }
}
