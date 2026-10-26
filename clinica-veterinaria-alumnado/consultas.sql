-- 1. Todos los clientes alfabéticamente
    SELECT id, nombre, email, telefono FROM clientes ORDER BY nombre;
-- 2. Mascotas de una especie (modifica 'Perro' si deseas)
    SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE especie = ?;
-- 3. Mascotas de un cliente
    SELECT id, nombre, especie, raza, fecha_nacimiento, id_cliente FROM mascotas WHERE id_cliente = ?;
-- 4. Veterinarios de una especialidad
    SELECT id, nombre, especialidad, tarifa_hora FROM veterinarios WHERE especialidad = ?;
-- 5. Consultas programadas
    
-- 6a. Mascota y propietario con WHERE

-- 6b. Mascota y propietario con INNER JOIN

-- 7. Consultas, mascota, propietario y veterinario

-- 8. Cantidad de mascotas por cliente, incluidos los de cero mascotas

-- 9. Importe tratamientos, incluyendo consultas sin tratamientos

-- 10. Consultas sin tratamientos
