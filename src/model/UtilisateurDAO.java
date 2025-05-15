package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDAO {

    public Utilisateur findByLogin(String login) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Utilisateur(
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    java.time.LocalDate.parse(rs.getString("date_naissance")),
                    Nationalite.valueOf(rs.getString("nationalite")),
                    0, // age à calculer
                    rs.getInt("est_inscrit") == 1
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void save(Utilisateur utilisateur, String login, String motDePasse) {
        String sql = "INSERT INTO utilisateur(login, mot_de_passe, nom, prenom, date_naissance, nationalite, est_inscrit) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            pstmt.setString(2, motDePasse);  // à hasher en prod
            pstmt.setString(3, utilisateur.getNom());
            pstmt.setString(4, utilisateur.getPrenom());
            pstmt.setString(5, utilisateur.getDateNaissance().toString());
            pstmt.setString(6, utilisateur.getNationalite().name());
            pstmt.setInt(7, utilisateur.estInscrit ? 1 : 0);

            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
