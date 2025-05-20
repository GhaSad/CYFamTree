package dao;

import model.Noeud;
import model.Personne;
import java.sql.*;

public class NoeudDAO {
    private Connection connection;

    public NoeudDAO(Connection connection) {
        this.connection = connection;
    }

    public void sauvegarderNoeud(Noeud noeud, int idArbre) throws SQLException {
        String sql = "INSERT INTO noeud (id_personne, visibilite, arbre_id) VALUES (?, ?, ?)";
        Personne p = noeud.getPersonne();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, p.getId());  // tu dois avoir sauvegardé la personne avant
            stmt.setString(2, noeud.getVisibilite().toString());
            stmt.setInt(3, idArbre);
            stmt.executeUpdate();
        }
    }

    public void ajouterArbreIdAuNoeud(Noeud noeud, int idArbre) throws SQLException {
        String sql = "UPDATE noeud SET arbre_id = ? WHERE id_personne = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idArbre);
            stmt.setInt(2, noeud.getPersonne().getId());
            stmt.executeUpdate();
        }
    }

}
