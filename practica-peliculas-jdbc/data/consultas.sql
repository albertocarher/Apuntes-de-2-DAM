SELECT name FROM sqlite_schema WHERE type = 'table' ORDER BY name;
PRAGMA table_info('peliculas');
SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos
FROM peliculas ORDER BY pelicula_codigo;
SELECT pelicula_codigo, titulo_texto, estreno_anio, duracion_minutos
FROM peliculas WHERE pelicula_codigo = 'PEL001';
