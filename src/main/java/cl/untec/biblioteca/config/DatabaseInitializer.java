package cl.untec.biblioteca.config;

public class DatabaseInitializer {

    private static boolean initialized = false;

    private DatabaseInitializer() {
    }

    public static synchronized void initialize() {
        if (initialized) {
            return;
        }

        initialized = true;
    }
}