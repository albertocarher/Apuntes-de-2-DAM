package es.codelearnacademy.filelab.support;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public final class FixtureSupport {

    private FixtureSupport() {
    }

    public static Path copiar(String recurso, Path destino) throws IOException {
        try (InputStream entrada = FixtureSupport.class.getResourceAsStream("/fixtures/" + recurso)) {
            if (entrada == null) {
                throw new IOException("No existe el fixture " + recurso);
            }
            Files.createDirectories(destino.getParent());
            Files.copy(entrada, destino, StandardCopyOption.REPLACE_EXISTING);
            return destino;
        }
    }
}
