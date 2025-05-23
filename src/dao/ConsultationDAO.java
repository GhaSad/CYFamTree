package dao;

import model.Consultation;
import model.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConsultationDAO {

	public static void enregistrerConsultation(Connection conn, Consultation consultation) throws SQLException {
	    String sql = "INSERT INTO consultation (date_consultation, utilisateur_id) VALUES (?, ?)";
	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	        stmt.setString(1, consultation.getFormattedDate());
	        stmt.setInt(2, consultation.getUtilisateurConsulteur().getId());
	        stmt.executeUpdate();
	    }
	}


}

