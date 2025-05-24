package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitaire pour gérer la connexion à la base de données SQLite.
 */
public class Database {

    // URL de la base de données SQLite utilisée par l'application
    private static final String URL = "jdbc:sqlite:mabase.db";

    /**
     * Établit une connexion à la base de données SQLite.
     * Applique également un `PRAGMA busy_timeout` de 5000 ms pour éviter les conflits de verrouillage.
     *
     * @return Une connexion {@link Connection} ouverte vers la base de données.
     * @throws SQLException Si la connexion échoue.
     */
    public static Connection getConnection() throws SQLException {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement stmt = conn.createStatement()) {
            // Définit un temps d’attente de 5 secondes si la base est occupée (verrouillée par un autre processus)
            stmt.execute("PRAGMA busy_timeout = 5000");
        }
        return conn;
    }
}
