package cl.untec.biblioteca.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Centraliza la configuracion y entrega conexiones JDBC hacia H2.
public class ConexionDB {

    private static final String URL = "jdbc:h2:~/biblioteca_untec_v2;AUTO_SERVER=TRUE";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    static {
        try {
            // Carga explicita del driver para asegurar compatibilidad en distintos contenedores.
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("No se pudo cargar el driver de H2", e);
        }
    }

    private ConexionDB() {
    }

    // Punto unico de acceso para obtener conexiones a la base de datos.
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}