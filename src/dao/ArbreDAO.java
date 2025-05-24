package dao;

import model.ArbreGenealogique;
import model.Utilisateur;

import java.sql.Connection;

/**
 * La classe {@code ArbreDAO} fournit les méthodes d'accès à la base de données
 * pour les objets {@link ArbreGenealogique}.
 * Elle permet notamment la création d'un arbre et le chargement de l'arbre associé à un utilisateur.
 */
public class ArbreDAO {

    /**
     * Crée un nouvel arbre généalogique dans la base de données.
     *
     * @param arbre L'arbre généalogique à sauvegarder (doit contenir un utilisateur et une racine valide).
     * @return L'identifiant de l'arbre créé, ou -1 en cas d'échec.
     */
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

    /**
     * Charge l'arbre généalogique associé à un utilisateur donné.
     * Cette méthode recherche dans la base l’arbre dont l’ID utilisateur correspond à l'utilisateur passé en paramètre.
     *
     * @param utilisateur L'utilisateur dont on souhaite charger l'arbre.
     * @return L'arbre généalogique associé à l'utilisateur, ou {@code null} si aucun arbre n’est trouvé.
     */
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

            System.out.println("❌ Aucun arbre trouvé pour utilisateur id = " + utilisateur.getId());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
