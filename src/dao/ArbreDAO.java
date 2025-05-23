package dao;

import model.ArbreGenealogique;
import model.Utilisateur;

import java.sql.Connection;

public class ArbreDAO {

    public static int creerArbre(ArbreGenealogique arbre) {
        int id = -1;
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO arbre (utilisateur_id, racine_id) VALUES (?, ?)";
            var stmt = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, arbre.getUtilisateur().getId());
            stmt.setInt(2, arbre.getRacine().getId());
            stmt.executeUpdate();

            var rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return id;
    }

    public static ArbreGenealogique chargerArbreParUtilisateur(Utilisateur utilisateur) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT id FROM arbre WHERE utilisateur_id = ?";
            System.out.println("🔍 Recherche arbre pour utilisateur id = " + utilisateur.getId());
            var stmt = conn.prepareStatement(sql);
            stmt.setInt(1, utilisateur.getId());

            var rs = stmt.executeQuery();

            if (rs.next()) {
                int idArbre = rs.getInt("id");

                NoeudDAO noeudDAO = new NoeudDAO(conn);

                return noeudDAO.chargerArbreComplet(utilisateur, idArbre);

            }
            if (!rs.next()) {
                System.out.println("❌ Aucun arbre trouvé pour utilisateur id = " + utilisateur.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
