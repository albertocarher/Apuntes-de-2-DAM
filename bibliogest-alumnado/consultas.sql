-- ALUMNADO: escribe aquí las 25 consultas del enunciado, identificadas del 01 al 25.
-- Utiliza schema.sql, los DAO y los tests para comprobar cada consulta.
1.- SELECT idLibro, titulo, isbn, genero, anioPublicacion, ejemplares, idAutor FROM libros ORDER BY titulo;
2.- SELECT idSocio, nombre, email, telefono, fechaAlta, activo FROM socios WHERE activo = true ORDER BY fechaAlta DESC;
3.- SELECT idLibro, titulo, isbn, genero, anioPublicacion, ejemplares, idAutor