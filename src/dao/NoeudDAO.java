package dao;

import model.*;

import java.sql.*;
import java.util.*;

/**
 * DAO responsable des opérations sur les nœuds de l’arbre généalogique dans la base de données.
 * Il permet de sauvegarder des nœuds, mettre à jour leur lien avec un arbre et charger l’arbre complet.
 */
public class NoeudDAO {
    private Connection connection;

    /**
     * Constructeur prenant une connexion à la base de données.
     * @param connection Connexion active à la base.
     */
    public NoeudDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Insère un nœud dans la base de données et récupère son ID généré automatiquement.
     *
     * @param noeud Le nœud à sauvegarder.
     * @param idArbre L’ID de l’arbre auquel appartient ce nœud.
     * @throws SQLException Si une erreur SQL survient.
     */
    public void sauvegarderNoeud(Noeud noeud, int idArbre) throws SQLException {
        String sql = "INSERT INTO noeud (id_personne, visibilite, arbre_id) VALUES (?, ?, ?)";
        Personne p = noeud.getPersonne();

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, p.getId());
            stmt.setString(2, noeud.getVisibilite().toString());
            stmt.setInt(3, idArbre);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    noeud.setId(rs.getInt(1));
                } else {
                    throw new SQLException("Échec de récupération de l'ID du nœud.");
                }
            }
        }
    }

    /**
     * Met à jour l’ID de l’arbre pour un nœud existant en base.
     *
     * @param noeud Le nœud à mettre à jour.
     * @param idArbre Le nouvel ID de l’arbre.
     * @throws SQLException Si une erreur SQL survient.
     */
    public void ajouterArbreIdAuNoeud(Noeud noeud, int idArbre) throws SQLException {
        String sql = "UPDATE noeud SET arbre_id = ? WHERE id_personne = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idArbre);
            stmt.setInt(2, noeud.getPersonne().getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Charge complètement un arbre généalogique (avec ses nœuds et ses relations) à partir de son ID.
     *
     * @param utilisateur L’utilisateur propriétaire de l’arbre.
     * @param idArbre L’identifiant de l’arbre à charger.
     * @return L’arbre généalogique reconstruit.
     * @throws SQLException En cas d’erreur de récupération depuis la base.
     */
    public ArbreGenealogique chargerArbreComplet(Utilisateur utilisateur, int idArbre) throws SQLException {
        ArbreGenealogique arbre = null;
        Map<Integer, Noeud> noeudsMap = new HashMap<>();

        // 🔹 Récupérer la personne racine de l’arbre
        String sqlRacine = "SELECT racine_id FROM arbre WHERE id = ?";
        try (PreparedStatement stmtR = connection.prepareStatement(sqlRacine)) {
            stmtR.setInt(1, idArbre);
            ResultSet rsR = stmtR.executeQuery();

            if (rsR.next()) {
                int racineIdNoeud = rsR.getInt("racine_id");

                String sqlRacinePersonne = "SELECT id_personne FROM noeud WHERE id_noeud = ?";
                try (PreparedStatement stmtP = connection.prepareStatement(sqlRacinePersonne)) {
                    stmtP.setInt(1, racineIdNoeud);
                    ResultSet rsP = stmtP.executeQuery();
                    if (rsP.next()) {
                        int idPersonneRacine = rsP.getInt("id_personne");
                        Personne racinePersonne = PersonneDAO.trouverPersonneParId(idPersonneRacine);

                        arbre = new ArbreGenealogique(utilisateur, racinePersonne);
                        arbre.setId(idArbre);
                    } else {
                        throw new SQLException("❌ Personne racine introuvable");
                    }
                }
            } else {
                throw new SQLException("❌ Arbre introuvable");
            }
        }

        if (arbre == null) {
            throw new SQLException("❌ Erreur : arbre non instancié");
        }

        // 🔹 Charger tous les nœuds de l’arbre
        String sqlNoeuds = "SELECT id_noeud, id_personne, visibilite FROM noeud WHERE arbre_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlNoeuds)) {
            stmt.setInt(1, idArbre);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int idNoeud = rs.getInt("id_noeud");
                int idPersonne = rs.getInt("id_personne");
                String visStr = rs.getString("visibilite");

                Personne p = PersonneDAO.trouverPersonneParId(idPersonne);
                Visibilite vis = Visibilite.valueOf(visStr);

                Noeud noeud = new Noeud(idNoeud, p, vis);
                noeudsMap.put(idNoeud, noeud);
            }
        }

        // 🔹 Reconstituer les liens parent-enfant
        String sqlRelations = "SELECT id_parent, id_enfant FROM noeud_lien WHERE arbre_id = ?";
        try (PreparedStatement stmtRel = connection.prepareStatement(sqlRelations)) {
            stmtRel.setInt(1, idArbre);
            ResultSet rsRel = stmtRel.executeQuery();

            while (rsRel.next()) {
                int idParent = rsRel.getInt("id_parent");
                int idEnfant = rsRel.getInt("id_enfant");

                Noeud parent = noeudsMap.get(idParent);
                Noeud enfant = noeudsMap.get(idEnfant);

                if (parent != null && enfant != null) {
                    parent.ajouterEnfant(enfant);
                }
            }
        }

        // 🔹 Ajout des nœuds à l’arbre
        for (Noeud n : noeudsMap.values()) {
            arbre.ajouterNoeud(n);
        }

        return arbre;
    }
}
