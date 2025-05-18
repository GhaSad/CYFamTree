package dao;

import model.Consultation;
import model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConsultationDAO {

    public static void enregistrerConsultation(Consultation consultation) {
        String sql = "INSERT INTO consultation (date_consultation, utilisateur_id) VALUES (?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            Utilisateur u = consultation.getUtilisateurConsulteur();

            stmt.setString(1, consultation.getFormattedDate());
            stmt.setInt(2, u.getId()); // Assure-toi que l’utilisateur a un ID valide

            stmt.executeUpdate();
            System.out.println("✅ Consultation enregistrée avec succès pour l'utilisateur ID=" + u.getId());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
