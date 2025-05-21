package dao;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

public class UtilisateurDAO {

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
                        rs.getString("num_tel")
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
                        rs.getString("num_tel")
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
                        rs.getString("numero_carte"),
                        rs.getString("photo_numerique"),
                        rs.getString("num_tel") // ✅ ajout du nouveau champ ici
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

}
