PRAGMA foreign_keys=ON;
BEGIN;
CREATE TABLE peliculas (
    pelicula_codigo TEXT NOT NULL PRIMARY KEY
        CHECK (pelicula_codigo GLOB 'PEL[0-9][0-9][0-9]'),
    titulo_texto TEXT NOT NULL
        CHECK (length(trim(titulo_texto)) BETWEEN 1 AND 120),
    estreno_anio INTEGER NOT NULL
        CHECK (typeof(estreno_anio) = 'integer' AND estreno_anio BETWEEN 1888 AND 2100),
    duracion_minutos INTEGER NOT NULL
        CHECK (typeof(duracion_minutos) = 'integer' AND duracion_minutos BETWEEN 1 AND 1000)
);
INSERT INTO "peliculas" VALUES('PEL001','El viaje de Chihiro',2001,125);
INSERT INTO "peliculas" VALUES('PEL002','Mi vecino Totoro',1988,86);

CREATE TABLE categorias (
    id_categoria INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre TEXT NOT NULL UNIQUE
);
CREATE TABLE actores (
    id_actor TEXT NOT NULL PRIMARY KEY,
    nombre TEXT NOT NULL
);
CREATE TABLE pelicula_categoria (
    pelicula_codigo TEXT NOT NULL REFERENCES peliculas(pelicula_codigo) ON DELETE CASCADE,
    id_categoria INTEGER NOT NULL REFERENCES categorias(id_categoria) ON DELETE CASCADE,
    PRIMARY KEY (pelicula_codigo, id_categoria)
);
CREATE TABLE pelicula_actor (
    pelicula_codigo TEXT NOT NULL REFERENCES peliculas(pelicula_codigo) ON DELETE CASCADE,
    id_actor TEXT NOT NULL REFERENCES actores(id_actor) ON DELETE CASCADE,
    PRIMARY KEY (pelicula_codigo, id_actor)
);
INSERT INTO categorias (nombre) VALUES ('Animación'), ('Fantasía');
INSERT INTO actores VALUES ('PAS000001', 'Alba Vega'), ('PAS000002', 'Leo Sol');
INSERT INTO pelicula_categoria
    SELECT 'PEL001', id_categoria FROM categorias;
INSERT INTO pelicula_categoria
    SELECT 'PEL002', id_categoria FROM categorias WHERE nombre = 'Animación';
INSERT INTO pelicula_actor VALUES ('PEL001', 'PAS000001'), ('PEL001', 'PAS000002');

COMMIT;
