package dao;

import model.Personne;

import model.Nationalite;

import java.sql.*;
import java.time.LocalDate;

public class PersonneDAO {

	public static int sauvegarder(Personne p, Connection conn) throws SQLException {
	    String sql = "INSERT INTO personne (nom, prenom, date_naissance, nationalite, age) VALUES (?, ?, ?, ?, ?)";

	    try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
	        stmt.setString(1, p.getNom());
	        stmt.setString(2, p.getPrenom());
	        stmt.setString(3, p.getDateNaissance().toString());
	        stmt.setString(4, p.getNationalite().toString());
	        stmt.setInt(5, p.getAge());

	        int affectedRows = stmt.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Échec de la création de la personne.");
	        }

	        try (ResultSet rs = stmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                int generatedId = rs.getInt(1);
	                p.setId(generatedId);
	                return generatedId;
	            } else {
	                throw new SQLException("Aucun ID généré.");
	            }
	        }
	    }
	}



    public static Personne trouverParId(int id) throws SQLException {
        String sql = "SELECT * FROM personne WHERE id_personne = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                LocalDate dateNaissance = LocalDate.parse(rs.getString("date_naissance"));
                Nationalite nationalite = Nationalite.valueOf(rs.getString("nationalite").toUpperCase());
                int age = rs.getInt("age");

                Personne p = new Personne(nom, prenom, dateNaissance, nationalite, age);
                p.setId(id);
                return p;
            }
        }

        return null;
    }
}
