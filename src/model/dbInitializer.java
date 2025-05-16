package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbInitializer {
    private static final String URL = "jdbc:sqlite:mabase.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables(){


        String sqlPersonne = """
                
                CREATE TABLE IF NOT EXISTS personne (
                    id_personne INT PRIMARY KEY AUTO_INCREMENT,
                    nom TEXT NOT NULL,
                    prenom TEXT NOT NULL,
                    date_naissance TEXT NOT NULL,
                    nationalite ENUM in ("MAROCAIN", "FRANCAIS", "ALLEMAND", "ESPAGNOL", "ITALIEN",
                    "CHINOIS", "JAPONAIS", "CANADIEN", "ALGERIEN", "TUNISIEN", "BELGE"),
                    age INT NOT NULL);
                """;

        String sqlUtilisateur = """
               \s
                CREATE TABLE IF NOT EXISTS utilisateur (
                    id_personne INTEGER PRIMARY KEY,
                    login TEXT UNIQUE NOT NULL,
                    mot_de_passe TEXT NOT NULL,
                    est_inscrit INTEGER NOT NULL,
                    FOREIGN KEY (id_personne) REFERENCES personne(id_personne) ); \s
               \s""";

        String sqlLiens = """
                
                CREATE TABLE IF NOT EXISTS liens (
                
                )
                
                """;
    }
}
