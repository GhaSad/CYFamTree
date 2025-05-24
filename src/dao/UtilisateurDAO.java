package dao;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import java.util.Optional;


public class UtilisateurDAO {

    /**
     * Récupère tous les utilisateurs qui n'ont pas encore été validés.
     *
     * @return Liste des utilisateurs en attente de validation.
     */

    public List<Utilisateur> findAllEnAttenteValidation() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE est_valide = 0";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("✅ Utilisateur en attente trouvé : " + rs.getString("login"));
                Utilisateur u = new Utilisateur(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        LocalDate.parse(rs.getString("date_naissance")),
                        Nationalite.valueOf(rs.getString("nationalite")),
                        0,
                        rs.getInt("est_inscrit") == 1,
                        rs.getInt("est_valide") == 1,
                        rs.getString("email"),
                        rs.getString("numero_securite"),
                        rs.getString("carte_identite"),
                        rs.getString("photo_numerique"),
                        rs.getString("num_tel"),
                        rs.getString("code_public")

                );
                u.setLogin(rs.getString("login"));
                u.setId(rs.getInt("id"));
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    /**
     * Récupère tous les utilisateurs de la base.
     *
     * @return Liste de tous les utilisateurs.
     */

    public List<Utilisateur> findAll() {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                System.out.println("✅ Utilisateur trouvé : " + rs.getString("login"));
                Utilisateur u = new Utilisateur(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        LocalDate.parse(rs.getString("date_naissance")),
                        Nationalite.valueOf(rs.getString("nationalite")),
                        0,
                        rs.getInt("est_inscrit") == 1,
                        rs.getInt("est_valide") == 1,
                        rs.getString("email"),
                        rs.getString("numero_securite"),
                        rs.getString("carte_identite"),
                        rs.getString("photo_numerique"),
                        rs.getString("num_tel"),
                        rs.getString("code_public")
                );
                u.setLogin(rs.getString("login"));
                u.setId(rs.getInt("id"));
                utilisateurs.add(u);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return utilisateurs;
    }

    /**
     * Valide un utilisateur en mettant à jour les colonnes `est_valide` et `est_inscrit`.
     *
     * @param login Le login de l'utilisateur à valider.
     */
    public void validerUtilisateur(String login) {
        String sql = "UPDATE utilisateur SET est_valide = 1, est_inscrit = 1 WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Supprime un utilisateur de la base.
     *
     * @param login Le login de l'utilisateur à supprimer.
     */
    public void supprimerUtilisateur(String login) {
        String sql = "DELETE FROM utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour l'email et le numéro de téléphone d'un utilisateur.
     *
     * @param utilisateur L'utilisateur avec les nouvelles informations.
     */
    public static void updateProfil(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateur SET email = ?, num_tel = ? WHERE login = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, utilisateur.getEmail());
            stmt.setString(2, utilisateur.getNumTel());
            stmt.setString(3, utilisateur.getLogin());

            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Met à jour le mot de passe d'un utilisateur et réinitialise le flag `doit_changer_mdp`.
     *
     * @param login      Login de l'utilisateur.
     * @param nouveauMdp Nouveau mot de passe en clair.
     */
    public static void updateMotDePasse(String login, String nouveauMdp) {
        String hash = BCrypt.hashpw(nouveauMdp, BCrypt.gensalt());

        String sql = "UPDATE utilisateur SET mot_de_passe = ?, doit_changer_mdp = 0 WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, hash);
            stmt.setString(2, login);
            stmt.executeUpdate();

            System.out.println("✅ Mot de passe mis à jour + flag 'doit_changer_mdp' remis à 0 pour : " + login);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Recherche les utilisateurs correspondant à des critères facultatifs : nom, prénom, nationalité.
     *
     * @param nom  Le nom à filtrer (peut être vide).
     * @param prenom Le prénom à filtrer (peut être vide).
     * @param nat  La nationalité (peut être null).
     * @return Liste des utilisateurs correspondants.
     */
    public List<Utilisateur> rechercherParCritere(String nom, String prenom, Nationalite nat) {
        List<Utilisateur> utilisateurs = new ArrayList<>();
        String sql = "SELECT * FROM utilisateur WHERE 1=1";
        List<Object> params = new ArrayList<>();

        if (nom != null && !nom.isEmpty()) {
            sql += " AND nom LIKE ?";
            params.add("%" + nom + "%");
        }
        if (prenom != null && !prenom.isEmpty()) {
            sql += " AND prenom LIKE ?";
            params.add("%" + prenom + "%");
        }
        if (nat != null) {
            sql += " AND nationalite = ?";
            params.add(nat.name());
        }

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        LocalDate.parse(rs.getString("date_naissance")),
                        Nationalite.valueOf(rs.getString("nationalite")),
                        0,
                        rs.getInt("est_inscrit") == 1,
                        rs.getInt("est_valide") == 1,
                        rs.getString("email"),
                        rs.getString("numero_securite"),
                        rs.getString("carte_identite"),
                        rs.getString("photo_numerique"),
                        rs.getString("num_tel"), // ✅ ajout du nouveau champ ici
                        rs.getString("code_public")
                );
                u.setLogin(rs.getString("login"));
                u.setId(rs.getInt("id"));
                utilisateurs.add(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return utilisateurs;
    }


    /**
     * Recherche un utilisateur par son identifiant unique.
     *
     * @param id L'identifiant de l'utilisateur.
     * @return L'utilisateur trouvé ou null s'il n'existe pas.
     */
    public static Utilisateur trouverParId(int id) {
        String sql = "SELECT * FROM utilisateur WHERE id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                LocalDate dateNaissance = LocalDate.parse(rs.getString("date_naissance"));
                String nationaliteStr = rs.getString("nationalite");
                Nationalite nationalite = Nationalite.valueOf(nationaliteStr.toUpperCase());
                String email = rs.getString("email");
                String numSec = rs.getString("numero_securite"); // ou num_tel selon ton schéma
                String carteId = rs.getString("carte_identite");
                String photo = rs.getString("photo_numerique");
                String numTel = rs.getString("num_tel");
                String code_public = rs.getString("code_public");

                Utilisateur utilisateur = new Utilisateur(
                        nom,
                        prenom,
                        dateNaissance,
                        nationalite,
                        LocalDate.now().getYear() - dateNaissance.getYear(),
                        true, // estInscrit
                        rs.getInt("est_valide") == 1,
                        email,
                        numSec,
                        carteId,
                        photo,
                        numTel,
                        code_public// ou remplacer si tu as un champ "num_tel"
                );
                utilisateur.setId(id);
                utilisateur.setLogin(rs.getString("login"));
                utilisateur.setDoitChangerMotDePasse(rs.getInt("doit_changer_mdp") == 1);

                return utilisateur;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Recherche un utilisateur par son code public (ex: "CY1234").
     *
     * @param code Le code public à rechercher.
     * @return Un Optional contenant l'utilisateur trouvé, ou vide si aucun trouvé.
     */
    public Optional<Utilisateur> trouverParCodePublic(String code) {
        String sql = "SELECT * FROM utilisateur WHERE code_public = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Utilisateur u = new Utilisateur(
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        LocalDate.parse(rs.getString("date_naissance")),
                        Nationalite.valueOf(rs.getString("nationalite")),
                        0,
                        rs.getInt("est_inscrit") == 1,
                        rs.getInt("est_valide") == 1,
                        rs.getString("email"),
                        rs.getString("numero_securite"),
                        rs.getString("carte_identite"),
                        rs.getString("photo_numerique"),
                        rs.getString("num_tel"),
                        rs.getString("code_public")
                );
                u.setLogin(rs.getString("login"));
                u.setId(rs.getInt("id"));
                return Optional.of(u);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }



}
