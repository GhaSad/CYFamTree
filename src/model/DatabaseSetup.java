package model;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

public class DatabaseSetup {

    // Créer la table utilisateur avec la colonne est_valide
    public static void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS utilisateur (" +
                     "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "login TEXT UNIQUE NOT NULL," +
                     "mot_de_passe TEXT NOT NULL," +
                     "nom TEXT," +
                     "prenom TEXT," +
                     "date_naissance TEXT," +  // stocké en ISO (yyyy-MM-dd)
                     "nationalite TEXT," +
                     "est_inscrit INTEGER DEFAULT 0," +   // par défaut 0 (non inscrit)
                     "est_valide INTEGER DEFAULT 0" +     // par défaut 0 (non validé)
                     ");";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Table 'utilisateur' créée ou déjà existante.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
