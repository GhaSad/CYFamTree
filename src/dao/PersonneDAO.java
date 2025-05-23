package dao;

import model.Personne;
import model.Nationalite;

import java.sql.*;
import java.time.LocalDate;

public class PersonneDAO {

	public static int sauvegarder(Personne p) throws SQLException {
	    String sql = "INSERT INTO personne (nom, prenom, date_naissance, nationalite, age, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?)";

	    try (Connection conn = Database.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        stmt.setString(1, p.getNom());
	        stmt.setString(2, p.getPrenom());
            stmt.setString(3, p.getDateNaissance().toString());
	        stmt.setString(4, p.getNationalite().toString());
	        stmt.setInt(5, p.getAge());

	        int affectedRows = stmt.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Échec de la création de la personne, aucune ligne affectée.");
	        }

	        try (ResultSet rs = stmt.getGeneratedKeys()) {
	            if (rs.next()) {
	                int generatedId = rs.getInt(1);
	                p.setId(generatedId);  // Fixe l'id dans l'objet Personne
	                return generatedId;
	            } else {
	                throw new SQLException("Échec de la création de la personne, aucun ID généré.");
	            }
	        }
	    }
	}

	public static int sauvegarder(Personne p, int utilisateurId) throws SQLException {
		String sql = "INSERT INTO personne (nom, prenom, date_naissance, nationalite, age, utilisateur_id) VALUES (?, ?, ?, ?, ?, ?)";

		try (Connection conn = Database.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

			stmt.setString(1, p.getNom());
			stmt.setString(2, p.getPrenom());
			stmt.setString(3, p.getDateNaissance().toString());
			stmt.setString(4, p.getNationalite().toString());
			stmt.setInt(5, p.getAge());
			stmt.setInt(6, utilisateurId);

			int affectedRows = stmt.executeUpdate();

			if (affectedRows == 0) {
				throw new SQLException("Échec de la création de la personne liée à un utilisateur.");
			}

			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next()) {
					int id = rs.getInt(1);
					p.setId(id);
					return id;
				} else {
					throw new SQLException("Aucun ID généré.");
				}
			}
		}
	}

	public static Personne trouverPersonneParId(int id) throws SQLException {
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

	public static Personne trouverParUtilisateurId(int idUtilisateur) throws SQLException {
		String sql = "SELECT * FROM personne WHERE utilisateur_id = ?";

		System.out.println("🔍 Recherche personne avec utilisateur_id = " + idUtilisateur);

		try (Connection conn = Database.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, idUtilisateur);
			ResultSet rs = stmt.executeQuery();

			if (rs.next()) {
				String nom = rs.getString("nom");
				String prenom = rs.getString("prenom");
				LocalDate dateNaissance = LocalDate.parse(rs.getString("date_naissance"));
				Nationalite nationalite = Nationalite.valueOf(rs.getString("nationalite").toUpperCase());
				int age = rs.getInt("age");

				Personne p = new Personne(nom, prenom, dateNaissance, nationalite, age);
				p.setId(rs.getInt("id_personne"));

				System.out.println("✅ Personne trouvée : " + prenom + " " + nom + " (id_personne = " + p.getId() + ")");
				return p;
			} else {
				System.out.println("❌ Aucune personne trouvée avec utilisateur_id = " + idUtilisateur);
			}
		}

		return null;
	}

	public static void mettreAJour(Personne p) throws SQLException {
		String sql = "UPDATE personne SET nom = ?, prenom = ?, date_naissance = ?, nationalite = ?, age = ? WHERE id_personne = ?";

		try (Connection conn = Database.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setString(1, p.getNom());
			stmt.setString(2, p.getPrenom());
			stmt.setString(3, p.getDateNaissance().toString());
			stmt.setString(4, p.getNationalite().toString());
			stmt.setInt(5, p.getAge());
			stmt.setInt(6, p.getId());

			int lignes = stmt.executeUpdate();
			if (lignes == 0) {
				throw new SQLException("❌ Aucune ligne mise à jour pour la personne avec ID : " + p.getId());
			}

			System.out.println("✅ Personne mise à jour avec succès (ID = " + p.getId() + ")");
		}
	}


	public static void supprimer(Personne p) throws SQLException {
		String sql = "DELETE FROM personne WHERE id_personne = ?";

		try (Connection conn = Database.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, p.getId());

			int lignes = stmt.executeUpdate();
			if (lignes == 0) {
				throw new SQLException("❌ Suppression échouée : aucune personne avec ID = " + p.getId());
			}

			System.out.println("🗑️ Personne supprimée (ID = " + p.getId() + ")");
		}
	}

}
