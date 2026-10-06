package es.codelearnacademy.filelab.io;

import java.io.File;
import java.nio.file.Path;

public class FileService {

    public boolean existe(File file) {
        return file.exists();
    }

    public boolean esArchivo(File file) {
        return file.isFile();
    }

    public boolean esDirectorio(File file) {
        return file.isDirectory();
    }

    public String nombre(File file) {
        return file.getName();
    }

    public File padre(File file) {
        return file.getParentFile();
    }

    public Path convertirAPath(File file) {
        return file.toPath();
    }

    public File convertirAFile(Path path) {
        return path.toFile();
    }
}

