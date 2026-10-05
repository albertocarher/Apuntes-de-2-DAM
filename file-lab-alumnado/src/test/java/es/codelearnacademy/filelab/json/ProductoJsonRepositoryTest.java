package es.codelearnacademy.filelab.json;

import es.codelearnacademy.filelab.repository.IProductoRepository;
import es.codelearnacademy.filelab.repository.ProductoRepositoryContractTest;
import es.codelearnacademy.filelab.support.FixtureSupport;
import java.nio.file.Path;
import org.junit.jupiter.api.io.TempDir;

class ProductoJsonRepositoryTest extends ProductoRepositoryContractTest {

    @TempDir
    Path tempDir;

    @Override
    protected IProductoRepository crearRepositorio(Path archivo) {
        return new ProductoJsonRepository(archivo);
    }

    @Override
    protected Path crearFixture() throws Exception {
        return FixtureSupport.copiar("productos.json", tempDir.resolve("productos.json"));
    }
}
