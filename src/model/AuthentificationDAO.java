package model;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;  // <-- N'oublie pas cet import !

public class AuthentificationDAO {

    // Vérifier les identifiants de l'utilisateur
public Utilisateur authentifier(String login, String mdpClair) {
    String sql = "SELECT * FROM utilisateur WHERE login = ?";
    try (Connection conn = Database.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setString(1, login);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            String motDePasseHash = rs.getString("mot_de_passe");

            if (BCrypt.checkpw(mdpClair, motDePasseHash)) {
                Utilisateur user = new Utilisateur(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    LocalDate.parse(rs.getString("date_naissance")),
                    Nationalite.valueOf(rs.getString("nationalite")),
                    0,
                    rs.getInt("est_inscrit") == 1,
                    rs.getInt("est_valide") == 1
                );
                user.setLogin(rs.getString("login"));
                return user;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}


    // Enregistrer un nouvel utilisateur dans la base de données
    public void save(Utilisateur utilisateur, String login, String motDePasseClair) {
        if (userExists(login)) {
            System.out.println("Erreur : L'utilisateur avec le login '" + login + "' existe déjà.");
            return; // Ne pas insérer l'utilisateur si le login existe déjà
        }

        String sql = "INSERT INTO utilisateur(login, mot_de_passe, nom, prenom, date_naissance, nationalite, est_inscrit, est_valide) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String hash = BCrypt.hashpw(motDePasseClair, BCrypt.gensalt());

            pstmt.setString(1, login);
            pstmt.setString(2, hash);
            pstmt.setString(3, utilisateur.getNom());
            pstmt.setString(4, utilisateur.getPrenom());
            pstmt.setString(5, utilisateur.getDateNaissance().toString());
            pstmt.setString(6, utilisateur.getNationalite().name());
            pstmt.setInt(7, utilisateur.getEstInscrit() ? 1 : 0);
            pstmt.setInt(8, 0); // est_valide à 0 = en attente de validation

            pstmt.executeUpdate();
            System.out.println("Utilisateur ajouté avec succès.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Vérifier si l'utilisateur existe déjà dans la base de données
    public boolean userExists(String login) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
