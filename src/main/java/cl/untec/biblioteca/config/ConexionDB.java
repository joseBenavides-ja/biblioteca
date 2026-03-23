package cl.untec.biblioteca.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionDB {

    private static final String DRIVER;
    private static final String URL;
    private static final String USER;
    private static final String PASSWORD;

    static {
        try (InputStream input = ConexionDB.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                throw new RuntimeException("No se encontró el archivo db.properties");
            }

            Properties properties = new Properties();
            properties.load(input);

            DRIVER = properties.getProperty("db.driver");
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");

            Class.forName(DRIVER);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error cargando configuración de base de datos", e);
        }
    }

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}