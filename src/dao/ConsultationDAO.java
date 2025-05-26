package dao;

import model.Consultation;

import model.Utilisateur;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des consultations d'arbres généalogiques entre utilisateurs.
 */
public class ConsultationDAO {

    /**
     * Enregistre une nouvelle consultation dans la base de données.
     *
     * @param consultation La consultation à enregistrer, contenant l'utilisateur consultant, l'utilisateur cible, et la date.
     */
    public static void enregistrerConsultation(Consultation consultation) {
        String sql = "INSERT INTO consultation (date_consultation, utilisateur_id, cible_id) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, consultation.getFormattedDate());
            stmt.setInt(2, consultation.getUtilisateurConsulteur().getId());
            stmt.setInt(3, consultation.getUtilisateurCible().getId());

            stmt.executeUpdate();
            System.out.println("✅ Consultation enregistrée : " +
                    "consulteur = " + consultation.getUtilisateurConsulteur().getId() +
                    ", cible = " + consultation.getUtilisateurCible().getId());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère toutes les consultations dont l'utilisateur spécifié est la cible.
     *
     * @param cibleId L'ID de l'utilisateur dont on veut voir les consultations reçues.
     * @return Liste des objets {@link Consultation} triée par date décroissante.
     */
    public static List<Consultation> getConsultationsParCible(int cibleId) {
        List<Consultation> consultations = new ArrayList<>();
        String sql = "SELECT * FROM consultation WHERE cible_id = ? ORDER BY date_consultation DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, cibleId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int consulteurId = rs.getInt("utilisateur_id");
                String dateStr = rs.getString("date_consultation");
                LocalDateTime date = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

                Utilisateur consulteur = UtilisateurDAO.trouverParId(consulteurId);
                Utilisateur cible = UtilisateurDAO.trouverParId(cibleId);

                Consultation consultation = new Consultation(consulteur, cible, date);
                consultation.setId(rs.getInt("id"));
                consultations.add(consultation);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return consultations;
    }
}
