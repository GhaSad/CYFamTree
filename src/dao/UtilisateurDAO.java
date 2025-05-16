package dao;
import model.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

public List<Utilisateur> findAllEnAttenteValidation() {
    List<Utilisateur> utilisateurs = new ArrayList<>();
    String sql = "SELECT * FROM utilisateur WHERE est_valide = 0";
    try (Connection conn = Database.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                rs.getString("nom"),
                rs.getString("prenom"),
                LocalDate.parse(rs.getString("date_naissance")),
                Nationalite.valueOf(rs.getString("nationalite")),
                0,
                rs.getInt("est_inscrit") == 1,
                rs.getInt("est_valide") == 1
            );
            u.setLogin(rs.getString("login"));  // Récupération du login ici
            utilisateurs.add(u);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return utilisateurs;
}


public List<Utilisateur> findAll() {
    List<Utilisateur> utilisateurs = new ArrayList<>();
    String sql = "SELECT * FROM utilisateur";  // récupère tous, car supprimés ont disparu
    try (Connection conn = Database.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {

        while (rs.next()) {
            Utilisateur u = new Utilisateur(
                rs.getString("nom"),
                rs.getString("prenom"),
                LocalDate.parse(rs.getString("date_naissance")),
                Nationalite.valueOf(rs.getString("nationalite")),
                0,
                rs.getInt("est_inscrit") == 1,
                rs.getInt("est_valide") == 1
            );
            u.setLogin(rs.getString("login"));
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
}
