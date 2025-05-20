package dao;

import model.Personne;
import model.Nationalite;

import java.sql.*;
import java.time.LocalDate;

public class PersonneDAO {

    public static int sauvegarder(Personne p) throws SQLException {
        String sql = "INSERT INTO personne (nom, prenom, date_naissance, nationalite, age) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, p.getNom());
            stmt.setString(2, p.getPrenom());
            stmt.setDate(3, Date.valueOf(p.getDateNaissance()));
            stmt.setString(4, p.getNationalite().toString()); // toString() → "Français" etc.
            stmt.setInt(5, p.getAge());

            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // id généré
            }
        }

        return -1; // erreur
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
                LocalDate dateNaissance = rs.getDate("date_naissance").toLocalDate();
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
