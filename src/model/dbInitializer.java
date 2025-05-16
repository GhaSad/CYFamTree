package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class dbInitializer {

    private static final String URL = "jdbc:sqlite:mabase.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void createTables() {
        System.out.println("📍 Base utilisée : " + new java.io.File("mabase.db").getAbsolutePath());
        try (Connection conn = getConnection();
             java.sql.Statement stmt = conn.createStatement()) {

            String sqlPersonne = """
            CREATE TABLE IF NOT EXISTS personne (
                id_personne INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                date_naissance TEXT NOT NULL,
                nationalite TEXT NOT NULL,
                age INT NOT NULL
            );
        """;

            String sqlUtilisateur = """
            CREATE TABLE IF NOT EXISTS utilisateur (
                id_utilisateur INTEGER PRIMARY KEY,
                email TEXT UNIQUE NOT NULL,
                mot_de_passe TEXT NOT NULL,
                est_inscrit INTEGER NOT NULL,
                FOREIGN KEY (id_utilisateur) REFERENCES personne(id_personne)
            );
        """;

            String sqlLiens = """
            CREATE TABLE IF NOT EXISTS lien (
                id_lien INTEGER PRIMARY KEY AUTOINCREMENT,
                id_personne_liee INTEGER NOT NULL,
                type_lien TEXT NOT NULL CHECK (type_lien IN ('PERE', 'MERE', 'FRERE', 'SŒUR', 'FILLE', 'FILS')),
                FOREIGN KEY (id_personne_liee) REFERENCES personne(id_personne)
            );
        """;

            String sqlAdmin = """
            CREATE TABLE IF NOT EXISTS admin (
                id_admin INTEGER PRIMARY KEY AUTOINCREMENT,
                nom TEXT NOT NULL,
                prenom TEXT NOT NULL,
                adresse TEXT,
                email TEXT UNIQUE NOT NULL,
                mot_de_passe TEXT NOT NULL,
                date_derniere_connexion TEXT NOT NULL
            );
        """;

            String sqlConsultation = """
            CREATE TABLE IF NOT EXISTS consultation (
                id_consultation INTEGER PRIMARY KEY AUTOINCREMENT,
                date_consultation TEXT NOT NULL,
                id_utilisateur_consulteur INTEGER NOT NULL,
                id_utilisateur_cible INTEGER NOT NULL,
                FOREIGN KEY (id_utilisateur_consulteur) REFERENCES utilisateur(id_utilisateur),
                FOREIGN KEY (id_utilisateur_cible) REFERENCES utilisateur(id_utilisateur)
            );
        """;

            String sqlRessource = """
            CREATE TABLE IF NOT EXISTS ressource_partagee (
                id_ressource INTEGER PRIMARY KEY AUTOINCREMENT,
                type_ressource TEXT NOT NULL CHECK (type_ressource IN ('FICHIER', 'IMAGE', 'LIEN')),
                fichier TEXT NOT NULL,
                date TEXT NOT NULL,
                id_auteur INTEGER NOT NULL,
                FOREIGN KEY (id_auteur) REFERENCES personne(id_personne)
            );
        """;

            String sqlNoeud = """
            CREATE TABLE IF NOT EXISTS noeud (
                id_noeud INTEGER PRIMARY KEY AUTOINCREMENT,
                id_personne INTEGER UNIQUE NOT NULL,
                visibilite TEXT NOT NULL CHECK (visibilite IN ('PUBLIC', 'PRIVATE', 'PARTAGE')),
                FOREIGN KEY (id_personne) REFERENCES personne(id_personne)
            );
        """;

            String sqlNoeudLien = """
            CREATE TABLE IF NOT EXISTS noeud_lien (
                id_parent INTEGER NOT NULL,
                id_enfant INTEGER NOT NULL,
                PRIMARY KEY (id_parent, id_enfant),
                FOREIGN KEY (id_parent) REFERENCES noeud(id_noeud),
                FOREIGN KEY (id_enfant) REFERENCES noeud(id_noeud)
            );
        """;

            // Exécution des requêtes
            stmt.execute(sqlPersonne);
            stmt.execute(sqlUtilisateur);
            stmt.execute(sqlLiens);
            stmt.execute(sqlAdmin);
            stmt.execute(sqlConsultation);
            stmt.execute(sqlRessource);
            stmt.execute(sqlNoeud);
            stmt.execute(sqlNoeudLien);

            System.out.println("✅ Toutes les tables ont été créées avec succès !");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Erreur lors de la création des tables.");
        }
    }

}
