package dao;

import model.RessourcePartagee;
import model.TypeRessource;
import model.Utilisateur;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO pour la gestion des ressources partagées dans la base de données.
 */
public class RessourceDAO {

    /**
     * Sauvegarde une ressource partagée dans la base de données avec un destinataire.
     *
     * @param ressource        L'objet RessourcePartagee à enregistrer.
     * @param idDestinataire   L'identifiant de l'utilisateur destinataire.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static void sauvegarder(RessourcePartagee ressource, int idDestinataire) throws SQLException {
        String sql = "INSERT INTO ressource_partagee (type_ressource, fichier, date, id_auteur, id_destinataire) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, ressource.getTypeRessource().name());
            stmt.setString(2, ressource.getFichier());
            stmt.setString(3, ressource.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            stmt.setInt(4, ressource.getAuteur().getId());
            stmt.setInt(5, idDestinataire);

            stmt.executeUpdate();
        }
    }

    /**
     * Récupère toutes les ressources partagées reçues par un utilisateur.
     *
     * @param idUtilisateur L'identifiant du destinataire.
     * @return Liste des ressources partagées reçues par l'utilisateur.
     * @throws SQLException En cas d'erreur SQL.
     */
    public static List<RessourcePartagee> getRessourcesPour(int idUtilisateur) throws SQLException {
        List<RessourcePartagee> ressources = new ArrayList<>();

        String sql = "SELECT * FROM ressource_partagee WHERE id_destinataire = ? ORDER BY date DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, idUtilisateur);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                RessourcePartagee r = new RessourcePartagee(
                        TypeRessource.valueOf(rs.getString("type_ressource")),
                        rs.getString("fichier"),
                        UtilisateurDAO.trouverParId(rs.getInt("id_auteur")),
                        List.of(UtilisateurDAO.trouverParId(idUtilisateur)) // destinataire unique
                );

                r.setIdRessource(rs.getInt("id_ressource"));
                r.setDate(LocalDateTime.parse(rs.getString("date"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                ressources.add(r);
            }
        }

        return ressources;
    }
}
