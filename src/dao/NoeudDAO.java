package dao;

import model.*;

import java.sql.*;
import java.util.*;

public class NoeudDAO {
    private Connection connection;

    public NoeudDAO(Connection connection) {
        this.connection = connection;
    }

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
                    noeud.setId(rs.getInt(1)); // ✅ très important pour les liens
                } else {
                    throw new SQLException("Échec de récupération de l'ID du nœud.");
                }
            }
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

    public ArbreGenealogique chargerArbreComplet(Utilisateur utilisateur, int idArbre) throws SQLException {
        ArbreGenealogique arbre = null;
        Map<Integer, Noeud> noeudsMap = new HashMap<>();

        // 🔹 Charger la racine de l’arbre
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

        // 🔹 Charger tous les noeuds de cet arbre
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

        // 🔹 Relations parent-enfant (avec filtre correct par arbre)
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

        // 🔹 Ajouter les noeuds dans l’arbre en mémoire
        for (Noeud n : noeudsMap.values()) {
            arbre.ajouterNoeud(n);
        }

        return arbre;
    }
}
