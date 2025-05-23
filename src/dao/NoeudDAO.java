package dao;

import model.*;

import java.sql.*;
import java.util.*;

public class NoeudDAO {
    private Connection connection;

    public NoeudDAO(Connection connection) {
        this.connection = connection;
    }

    // ➕ Sauvegarder un nœud dans la base
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
                    noeud.setId(rs.getInt(1)); // 🔁 Mise à jour de l'objet en mémoire
                } else {
                    throw new SQLException("Échec de récupération de l'ID du nœud.");
                }
            }
        }
    }

    // 🔁 Mise à jour de l'arbre_id pour un nœud existant
    public void ajouterArbreIdAuNoeud(Noeud noeud, int idArbre) throws SQLException {
        String sql = "UPDATE noeud SET arbre_id = ? WHERE id_personne = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idArbre);
            stmt.setInt(2, noeud.getPersonne().getId());
            stmt.executeUpdate();
        }
    }

    // 📥 Charger un arbre complet depuis la base
 // 📥 Charger un arbre complet depuis la base
    public ArbreGenealogique chargerArbreComplet(Utilisateur utilisateur, int idArbre) throws SQLException {
        ArbreGenealogique arbre = null;
        Map<Integer, Noeud> noeudsMap = new HashMap<>();

        // 1. 🔍 Charger la racine
        String sqlRacine = "SELECT racine_id FROM arbre WHERE id = ?";
        try (PreparedStatement stmtR = connection.prepareStatement(sqlRacine)) {
            stmtR.setInt(1, idArbre);
            try (ResultSet rsR = stmtR.executeQuery()) {
                if (rsR.next()) {
                    int racineIdNoeud = rsR.getInt("racine_id");

                    // Chercher la personne de la racine
                    String sqlRacinePersonne = "SELECT id_personne FROM noeud WHERE id_noeud = ?";
                    try (PreparedStatement stmtP = connection.prepareStatement(sqlRacinePersonne)) {
                        stmtP.setInt(1, racineIdNoeud);
                        try (ResultSet rsP = stmtP.executeQuery()) {
                            if (rsP.next()) {
                                int idPersonneRacine = rsP.getInt("id_personne");
                                Personne racinePersonne = PersonneDAO.trouverParId(idPersonneRacine);
                                arbre = new ArbreGenealogique(utilisateur, racinePersonne);
                                arbre.setId(idArbre);
                            } else {
                                throw new SQLException("Personne racine introuvable");
                            }
                        }
                    }
                } else {
                    throw new SQLException("Arbre introuvable");
                }
            }
        }

        if (arbre == null) throw new SQLException("Erreur lors de la création de l'arbre");

        // 2. 📦 Charger tous les nœuds de l’arbre
        String sqlNoeuds = "SELECT id_noeud, id_personne, visibilite FROM noeud WHERE arbre_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sqlNoeuds)) {
            stmt.setInt(1, idArbre);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idNoeud = rs.getInt("id_noeud");
                    int idPersonne = rs.getInt("id_personne");
                    String visStr = rs.getString("visibilite");

                    Personne p = PersonneDAO.trouverParId(idPersonne);
                    if (p == null) {
                        System.err.println("⚠️ Personne introuvable pour id_personne = " + idPersonne);
                        continue;
                    }

                    Visibilite vis = Visibilite.valueOf(visStr);
                    Noeud noeud = new Noeud(idNoeud, p, vis);
                    noeudsMap.put(idNoeud, noeud);
                }
            }
        }

        // 3. 🔗 Charger les relations parent-enfant avec correction
        String sqlRelations = """
            SELECT id_parent, id_enfant 
            FROM noeud_lien 
            WHERE id_parent IN (SELECT id_noeud FROM noeud WHERE arbre_id = ?)
               OR id_enfant IN (SELECT id_noeud FROM noeud WHERE arbre_id = ?)
            """;

        try (PreparedStatement stmtRel = connection.prepareStatement(sqlRelations)) {
            stmtRel.setInt(1, idArbre);
            stmtRel.setInt(2, idArbre);

            try (ResultSet rsRel = stmtRel.executeQuery()) {
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
        }

        // 4. Ajouter les nœuds dans l’arbre
        for (Noeud n : noeudsMap.values()) {
            arbre.ajouterNoeud(n);
        }

        return arbre;
    }

}
