package dao;
import model.*;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


public class Database {
    private static final String URL = "jdbc:sqlite:mabase.db";

    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:mabase.db";
        Connection conn = DriverManager.getConnection(url);
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA busy_timeout = 5000"); // Attend jusqu’à 5s si la base est occupée
        }
        return conn;
    }

}
