package dao;

import model.Lien;
import model.TypeLien;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LienDAO {

    public static void sauvegarder(Lien lien, Connection conn) throws SQLException {
        String sql = "INSERT INTO personne_lien (id_source, id_cible, type_lien) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            int idSource = lien.getSource().getId();
            int idCible = lien.getPersonneLiee().getId();
            String type = lien.getTypeLien().name();

            stmt.setInt(1, idSource);
            stmt.setInt(2, idCible);
            stmt.setString(3, type);
            stmt.executeUpdate();
        }
    }

    public static boolean aDejaDeuxParents(int idCible) {
        String sql = """
            SELECT COUNT(*) FROM personne_lien
            WHERE id_cible = ? AND (type_lien = 'PERE' OR type_lien = 'MERE')
        """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idCible);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count >= 2;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
