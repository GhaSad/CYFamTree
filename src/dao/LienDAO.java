package dao;

import model.Lien;
import model.TypeLien;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO responsable de la gestion des liens de parenté entre personnes dans la base de données.
 * Utilise la table `personne_lien` pour stocker les relations.
 */
public class LienDAO {

    /**
     * Enregistre un lien de parenté entre deux personnes dans la table `personne_lien`.
     *
     * @param lien Le lien de parenté à sauvegarder.
     * @param conn Connexion JDBC active (transmise pour permettre un contrôle transactionnel global).
     * @throws SQLException En cas d'erreur lors de l'insertion SQL.
     */
    public static void sauvegarder(Lien lien, Connection conn) throws SQLException {
        String sql = "INSERT INTO personne_lien (id_source, id_cible, type_lien) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idSource = lien.getSource().getId();
            int idCible = lien.getPersonneLiee().getId();
            String type = lien.getTypeLien().name();

            stmt.setInt(1, idSource);
            stmt.setInt(2, idCible);
            stmt.setString(3, type);
            stmt.executeUpdate();
        }
    }

    /**
     * Vérifie si une personne donnée (cible) a déjà deux parents enregistrés
     * (type de lien = PERE ou MERE).
     *
     * @param idCible L'ID de la personne cible (enfant).
     * @return {@code true} si la personne a déjà deux parents enregistrés, sinon {@code false}.
     */
    public static boolean aDejaDeuxParents(int idCible) {
        String sql = """
            SELECT COUNT(*) FROM personne_lien
            WHERE id_cible = ? AND (type_lien = 'PERE' OR type_lien = 'MERE')
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCible);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count >= 2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
