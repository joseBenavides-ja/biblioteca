package cl.untec.biblioteca.config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

// Crea la estructura minima de la BD y datos base la primera vez que inicia la app.
public class DatabaseInitializer {

    private static boolean initialized = false;

    private DatabaseInitializer() {
    }

    // Evita inicializaciones duplicadas cuando Tomcat crea varias instancias o hilos.
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        try (Connection conn = ConexionDB.getConnection();
             Statement st = conn.createStatement()) {

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS usuarios (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "username VARCHAR(50) NOT NULL UNIQUE, " +
                "password VARCHAR(100) NOT NULL, " +
                "nombre VARCHAR(100) NOT NULL, " +
                "rol VARCHAR(30) NOT NULL" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS libros (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "titulo VARCHAR(150) NOT NULL, " +
                "autor VARCHAR(100) NOT NULL, " +
                "serial_interno VARCHAR(7) NOT NULL UNIQUE, " +
                "stock_total INT NOT NULL, " +
                "stock_disponible INT NOT NULL" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS prestamos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "libro_id INT NOT NULL, " +
                "titulo_libro VARCHAR(150) NOT NULL, " +
                "serial_libro VARCHAR(7) NOT NULL, " +
                "nombre_prestatario VARCHAR(120) NOT NULL, " +
                "rut_prestatario VARCHAR(20) NOT NULL, " +
                "fecha_prestamo TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                "fecha_devolucion TIMESTAMP, " +
                "estado VARCHAR(20) NOT NULL, " +
                "rut_devolucion VARCHAR(20), " +
                "observacion_devolucion VARCHAR(255)" +
                ")"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS movimientos (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "libro_id INT, " +
                "titulo_libro VARCHAR(150) NOT NULL, " +
                "tipo VARCHAR(30) NOT NULL, " +
                "cantidad INT NOT NULL, " +
                "usuario VARCHAR(100) NOT NULL, " +
                "fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                "observacion VARCHAR(255)" +
                ")"
            );

            st.executeUpdate("ALTER TABLE movimientos ADD COLUMN IF NOT EXISTS serial_libro VARCHAR(7)");
            st.executeUpdate("ALTER TABLE movimientos ADD COLUMN IF NOT EXISTS rut_referencia VARCHAR(20)");

            // Inserta datos semilla para poder probar el sistema inmediatamente.
            insertarUsuarioSiNoExiste(conn);
            insertarLibrosSiNoExisten(conn);

            initialized = true;

        } catch (Exception e) {
            throw new RuntimeException("Error inicializando la base de datos", e);
        }
    }

    // Crea un usuario admin por defecto si la tabla aun no tiene registros.
    private static void insertarUsuarioSiNoExiste(Connection conn) throws Exception {
        String sqlCount = "SELECT COUNT(*) FROM usuarios";
        try (PreparedStatement ps = conn.prepareStatement(sqlCount);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insert = "INSERT INTO usuarios (username, password, nombre, rol) VALUES (?, ?, ?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    psInsert.setString(1, "admin");
                    psInsert.setString(2, "1234");
                    psInsert.setString(3, "Administrador");
                    psInsert.setString(4, "ADMIN");
                    psInsert.executeUpdate();
                }
            }
        }
    }

    // Carga un pequeno catalogo inicial cuando la tabla libros esta vacia.
    private static void insertarLibrosSiNoExisten(Connection conn) throws Exception {
        String sqlCount = "SELECT COUNT(*) FROM libros";
        try (PreparedStatement ps = conn.prepareStatement(sqlCount);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next() && rs.getInt(1) == 0) {
                String insert = "INSERT INTO libros (titulo, autor, serial_interno, stock_total, stock_disponible) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement psInsert = conn.prepareStatement(insert)) {
                    insertarLibro(psInsert, "Clean Code", "Robert C. Martin", "1000001", 5, 5);
                    insertarLibro(psInsert, "Effective Java", "Joshua Bloch", "1000002", 4, 4);
                    insertarLibro(psInsert, "Patrones de Diseno", "GoF", "1000003", 3, 3);
                    insertarLibro(psInsert, "Java Web Development", "Varios autores", "1000004", 2, 1);
                }
            }
        }
    }

    // Reutiliza el mismo PreparedStatement para registrar varios libros semilla.
    private static void insertarLibro(PreparedStatement ps, String titulo, String autor, String serialInterno,
                                      int stockTotal, int stockDisponible) throws Exception {
        ps.setString(1, titulo);
        ps.setString(2, autor);
        ps.setString(3, serialInterno);
        ps.setInt(4, stockTotal);
        ps.setInt(5, stockDisponible);
        ps.executeUpdate();
    }
}