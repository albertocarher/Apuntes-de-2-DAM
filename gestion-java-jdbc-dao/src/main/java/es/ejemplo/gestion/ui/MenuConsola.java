package es.ejemplo.gestion.ui;

import es.ejemplo.gestion.dao.ProductoDao;
import es.ejemplo.gestion.dao.VehiculoDao;
import es.ejemplo.gestion.model.Producto;
import es.ejemplo.gestion.model.Vehiculo;
import es.ejemplo.gestion.exception.AplicationException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Scanner;

/** Interfaz por consola: no contiene SQL. */
public class MenuConsola {
    private final Scanner entrada = new Scanner(System.in);
    private final ProductoDao productoDao;
    private final VehiculoDao vehiculoDao;

    public MenuConsola(String url) {
        this.productoDao = new ProductoDao(url);
        this.vehiculoDao = new VehiculoDao(url);
    }

    public void iniciar() {
        int opcion;
        do {
            System.out.println("\n=== GESTIÓN BÁSICA ===");
            System.out.println("1. Productos");
            System.out.println("2. Vehículos");
            System.out.println("3. Consultas estadísticas SQL");
            System.out.println("0. Salir");
            opcion = leerEntero("Elige opción: ", 0);
            switch (opcion) {
                case 1 -> menuProductos();
                case 2 -> menuVehiculos();
                case 3 -> mostrarEstadisticas();
                case 0 -> System.out.println("Hasta pronto.");
                default -> System.out.println("Opción desconocida.");
            }
        } while (opcion != 0);
    }

    private void menuProductos() {
        int opcion;
        do {
            System.out.println("\n--- PRODUCTOS ---");
            System.out.println("1. Listar productos");
            System.out.println("2. Buscar por ID");
            System.out.println("3. Crear producto");
            System.out.println("4. Modificar producto");
            System.out.println("5. Eliminar producto");
            System.out.println("6. Buscar por nombre");
            System.out.println("7. Buscar por rango de precios");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige opción: ", 0);
            try {
                switch (opcion) {
                    case 1 -> mostrarProductos(productoDao.listar());
                    case 2 -> productoDao.buscarPorId(leerEntero("ID: ", 1))
                            .ifPresentOrElse(this::mostrarProducto,
                                    () -> System.out.println("No existe el producto."));
                    case 3 -> crearProducto();
                    case 4 -> modificarProducto();
                    case 5 -> eliminarProducto();
                    case 6 -> mostrarProductos(productoDao.buscarPorNombre(leerTexto("Nombre (o parte): ")));
                    case 7 -> consultarRangoPrecios();
                    case 0 -> { }
                    default -> System.out.println("Opción desconocida.");
                }
            } catch (AplicationException e) {
                mostrarError(e);
            }
        } while (opcion != 0);
    }

    private void menuVehiculos() {
        int opcion;
        do {
            System.out.println("\n--- VEHÍCULOS ---");
            System.out.println("1. Listar vehículos");
            System.out.println("2. Buscar por matrícula");
            System.out.println("3. Crear vehículo");
            System.out.println("4. Modificar vehículo");
            System.out.println("5. Eliminar vehículo");
            System.out.println("6. Buscar por marca");
            System.out.println("7. Buscar por rango de años");
            System.out.println("0. Volver");
            opcion = leerEntero("Elige opción: ", 0);
            try {
                switch (opcion) {
                    case 1 -> mostrarVehiculos(vehiculoDao.listar());
                    case 2 -> vehiculoDao.buscarPorMatricula(leerMatricula())
                            .ifPresentOrElse(this::mostrarVehiculo,
                                    () -> System.out.println("No existe el vehículo."));
                    case 3 -> crearVehiculo();
                    case 4 -> modificarVehiculo();
                    case 5 -> eliminarVehiculo();
                    case 6 -> mostrarVehiculos(vehiculoDao.buscarPorMarca(leerTexto("Marca (o parte): ")));
                    case 7 -> consultarRangoAnios();
                    case 0 -> { }
                    default -> System.out.println("Opción desconocida.");
                }
            } catch (AplicationException e) {
                mostrarError(e);
            }
        } while (opcion != 0);
    }

    private void crearProducto()  {
        int id = leerEntero("ID (>0): ", 1);
        String nombre = leerTexto("Nombre: ");
        double precio = leerReal("Precio (>=0): ", 0);
        System.out.println(productoDao.insertar(new Producto(id, nombre, precio))
                ? "Producto creado." : "No se pudo crear el producto.");
    }

    private void modificarProducto()  {
        int id = leerEntero("ID del producto: ", 1);
        Optional<Producto> encontrado = productoDao.buscarPorId(id);
        if (encontrado.isEmpty()) {
            System.out.println("No existe el producto.");
            return;
        }
        Producto anterior = encontrado.get();
        String nombre = leerTextoOpcional("Nombre [" + anterior.getNombre() + "]: ", anterior.getNombre());
        double precio = leerRealOpcional("Precio [" + anterior.getPrecio() + "]: ", anterior.getPrecio(), 0);
        System.out.println(productoDao.actualizar(new Producto(id, nombre, precio))
                ? "Producto actualizado." : "No se pudo actualizar.");
    }

    private void eliminarProducto()  {
        int id = leerEntero("ID del producto: ", 1);
        if (productoDao.buscarPorId(id).isEmpty()) {
            System.out.println("No existe el producto.");
            return;
        }
        if (confirmar("¿Eliminar producto " + id + "? (s/n): ")) {
            System.out.println(productoDao.eliminar(id) ? "Producto eliminado." : "No se encontró.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void consultarRangoPrecios()  {
        double min = leerReal("Precio mínimo: ", 0);
        double max = leerReal("Precio máximo: ", min);
        mostrarProductos(productoDao.buscarPorPrecio(min, max));
    }

    private void crearVehiculo()  {
        String matricula = leerMatricula();
        String marca = leerTexto("Marca: ");
        String modelo = leerTexto("Modelo: ");
        int anio = leerEntero("Año (>=1886): ", 1886);
        System.out.println(vehiculoDao.insertar(new Vehiculo(matricula, marca, modelo, anio))
                ? "Vehículo creado." : "No se pudo crear el vehículo.");
    }

    private void modificarVehiculo()  {
        String matricula = leerMatricula();
        Optional<Vehiculo> encontrado = vehiculoDao.buscarPorMatricula(matricula);
        if (encontrado.isEmpty()) {
            System.out.println("No existe el vehículo.");
            return;
        }
        Vehiculo anterior = encontrado.get();
        String marca = leerTextoOpcional("Marca [" + anterior.getMarca() + "]: ", anterior.getMarca());
        String modelo = leerTextoOpcional("Modelo [" + anterior.getModelo() + "]: ", anterior.getModelo());
        int anio = leerEnteroOpcional("Año [" + anterior.getAnio() + "]: ", anterior.getAnio(), 1886);
        System.out.println(vehiculoDao.actualizar(new Vehiculo(matricula, marca, modelo, anio))
                ? "Vehículo actualizado." : "No se pudo actualizar.");
    }

    private void eliminarVehiculo()  {
        String matricula = leerMatricula();
        if (vehiculoDao.buscarPorMatricula(matricula).isEmpty()) {
            System.out.println("No existe el vehículo.");
            return;
        }
        if (confirmar("¿Eliminar vehículo " + matricula + "? (s/n): ")) {
            System.out.println(vehiculoDao.eliminar(matricula) ? "Vehículo eliminado." : "No se encontró.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void consultarRangoAnios()  {
        int desde = leerEntero("Año inicial: ", 1886);
        int hasta = leerEntero("Año final: ", desde);
        mostrarVehiculos(vehiculoDao.buscarPorAnio(desde, hasta));
    }

    private void mostrarEstadisticas() {
        try {
            System.out.println("\n--- CONSULTAS SQL ---");
            System.out.println("Total de productos: " + productoDao.contar());
            System.out.printf(Locale.forLanguageTag("es-ES"), "Precio medio: %.2f €%n", productoDao.precioMedio());
            System.out.println("Total de vehículos: " + vehiculoDao.contar());
            System.out.println("Vehículos por marca:");
            vehiculoDao.contarPorMarca().forEach((marca, cantidad) ->
                    System.out.printf("  %-18s %d%n", marca, cantidad));
        } catch (AplicationException e) {
            mostrarError(e);
        }
    }

    private void mostrarProductos(List<Producto> productos) {
        if (productos.isEmpty()) {
            System.out.println("No hay resultados.");
            return;
        }
        System.out.printf("%-6s %-32s %12s%n", "ID", "NOMBRE", "PRECIO (€)");
        for (Producto p : productos) mostrarProducto(p);
        System.out.println("Total encontrados: " + productos.size());
    }

    private void mostrarProducto(Producto p) {
        System.out.printf(Locale.forLanguageTag("es-ES"), "%-6d %-32s %12.2f%n",
                p.getId(), p.getNombre(), p.getPrecio());
    }

    private void mostrarVehiculos(List<Vehiculo> vehiculos) {
        if (vehiculos.isEmpty()) {
            System.out.println("No hay resultados.");
            return;
        }
        System.out.printf("%-12s %-18s %-20s %5s%n", "MATRÍCULA", "MARCA", "MODELO", "AÑO");
        for (Vehiculo v : vehiculos) mostrarVehiculo(v);
        System.out.println("Total encontrados: " + vehiculos.size());
    }

    private void mostrarVehiculo(Vehiculo v) {
        System.out.printf("%-12s %-18s %-20s %5d%n",
                v.getMatricula(), v.getMarca(), v.getModelo(), v.getAnio());
    }

    private String leerLinea(String mensaje) {
        System.out.print(mensaje);
        return entrada.nextLine().trim();
    }

    private String leerTexto(String mensaje) {
        while (true) {
            String valor = leerLinea(mensaje);
            if (!valor.isEmpty()) return valor;
            System.out.println("El campo no puede estar vacío.");
        }
    }

    private String leerTextoOpcional(String mensaje, String actual) {
        String nuevo = leerLinea(mensaje);
        return nuevo.isEmpty() ? actual : nuevo;
    }

    private String leerMatricula() {
        return leerTexto("Matrícula: ").toUpperCase(Locale.ROOT);
    }

    private int leerEntero(String mensaje, int minimo) {
        while (true) {
            try {
                int n = Integer.parseInt(leerLinea(mensaje));
                if (n >= minimo) return n;
            } catch (NumberFormatException ignored) { }
            System.out.println("Introduce un número entero >= " + minimo + ".");
        }
    }

    private int leerEnteroOpcional(String mensaje, int actual, int minimo) {
        while (true) {
            String texto = leerLinea(mensaje);
            if (texto.isEmpty()) return actual;
            try {
                int n = Integer.parseInt(texto);
                if (n >= minimo) return n;
            } catch (NumberFormatException ignored) { }
            System.out.println("Introduce un número entero >= " + minimo + " o pulsa Intro.");
        }
    }

    private double leerReal(String mensaje, double minimo) {
        while (true) {
            String texto = leerLinea(mensaje).replace(',', '.');
            try {
                double n = Double.parseDouble(texto);
                if (Double.isFinite(n) && n >= minimo) return n;
            } catch (NumberFormatException ignored) { }
            System.out.println("Introduce un número >= " + minimo + ".");
        }
    }

    private double leerRealOpcional(String mensaje, double actual, double minimo) {
        while (true) {
            String texto = leerLinea(mensaje).replace(',', '.');
            if (texto.isEmpty()) return actual;
            try {
                double n = Double.parseDouble(texto);
                if (Double.isFinite(n) && n >= minimo) return n;
            } catch (NumberFormatException ignored) { }
            System.out.println("Introduce un número >= " + minimo + " o pulsa Intro.");
        }
    }

    private boolean confirmar(String mensaje) {
        return leerLinea(mensaje).equalsIgnoreCase("s");
    }

    private void mostrarError(AplicationException e) {
        System.out.println("Error de base de datos: " + e.getMessage());
        System.out.println("Comprueba si la clave está duplicada o incumple una restricción de la tabla.");
    }
}
