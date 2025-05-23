package dao;

import model.ArbreGenealogique;

import model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ArbreDAO {

    // ✅ Création d’un arbre généalogique
    public static int creerArbre(ArbreGenealogique arbre) {
        int id = -1;
        String sql = "INSERT INTO arbre (utilisateur_id, racine_id) VALUES (?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, arbre.getUtilisateur().getId());
            stmt.setInt(2, arbre.getRacine().getId());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    id = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    // 🔁 Surcharge avec connexion externe (ex. transaction en cours)
    public static ArbreGenealogique chargerArbreParUtilisateur(Utilisateur utilisateur, Connection conn) {
        String sql = "SELECT id FROM arbre WHERE utilisateur_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, utilisateur.getId());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int idArbre = rs.getInt("id");
                    NoeudDAO noeudDAO = new NoeudDAO(conn);
                    return noeudDAO.chargerArbreComplet(utilisateur, idArbre);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 🧾 Méthode actuelle conservée pour compatibilité, mais appelle la version avec Connection
    public static ArbreGenealogique chargerArbreParUtilisateur(Utilisateur utilisateur) {
        try (Connection conn = Database.getConnection()) {
            return chargerArbreParUtilisateur(utilisateur, conn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
