package dao;

import model.Personne;
import model.ArbreGenealogique;
import model.Utilisateur;
import java.sql.*;

public class ArbreDAO {

    public static int creerArbre(ArbreGenealogique arbre) {
        int id = -1;
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO arbre (utilisateur_id, racine_id) VALUES (?,?)";
            PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, arbre.getUtilisateur().getId());
            stmt.setInt(2, arbre.getRacine().getId());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return id;
    }

    public static ArbreGenealogique chargerArbreParUtilisateur(Utilisateur utilisateur) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM arbre WHERE utilisateur_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, utilisateur.getId());

            ResultSet rs = stmt.executeQuery();

            System.out.println("🔍 Résultats de SELECT * FROM arbre WHERE utilisateur_id = " + utilisateur.getId());

            boolean found = false;

            while (rs.next()) {
                found = true;
                int idArbre = rs.getInt("id"); // ou "id_arbre" si c'est le vrai nom
                int utilisateurId = rs.getInt("utilisateur_id");
                int racineId = rs.getInt("racine_id");

                System.out.println("→ id_arbre = " + idArbre +
                        ", utilisateur_id = " + utilisateurId +
                        ", racine_id = " + racineId);

                // Chargement de la racine
                Personne racine = PersonneDAO.trouverParId(racineId);
                if (racine == null) {
                    System.out.println("❌ Aucun résultat trouvé dans PERSONNE pour id = " + racineId);
                    return null;
                } else {
                    System.out.println("✅ Personne racine trouvée : " + racine.getNom());
                }

                return new ArbreGenealogique(utilisateur, racine);
            }

            if (!found) {
                System.out.println("⚠️ Aucun enregistrement trouvé dans la table ARBRE.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
