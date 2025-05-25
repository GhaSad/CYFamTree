package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.LienEnAttente;
import model.Utilisateur;
import model.ArbreGenealogique;
import model.Noeud;
import model.TypeLien;
import service.LienManager;
import dao.NoeudDAO;
import dao.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;

/** Interface listant les demandes de lien de parenté reçues par l'utilisateur. */
public class DemandesLienPage {

    public static void show(Utilisateur utilisateur) {
        Stage stage = new Stage();
        VBox root = new VBox(10);
        root.setStyle("-fx-padding: 20;");

        Label titre = new Label("Demandes de lien de parenté reçues");
        root.getChildren().add(titre);

        for (LienEnAttente demande : LienManager.getDemandesPour(utilisateur)) {
            Label label = new Label(
                demande.getDemandeur().getPrenom() +
                " souhaite vous ajouter comme " +
                demande.getTypeLien().name().toLowerCase()
            );

            Button accepter = new Button("Accepter");
            Button refuser = new Button("Refuser");

            HBox labelBox = new HBox(10, label, accepter, refuser);

            accepter.setOnAction(e -> {
                LienManager.validerDemande(demande);

                Utilisateur demandeur = (Utilisateur) demande.getDemandeur();
                Utilisateur cible = utilisateur;
                TypeLien lien = demande.getTypeLien();

                // Créer l’arbre du destinataire si nécessaire
                if (cible.getArbre() == null) {
                    ArbreGenealogique nouvelArbre = new ArbreGenealogique(cible, cible);
                    nouvelArbre.ajouterNoeud(new Noeud(cible));
                    cible.setArbre(nouvelArbre);
                }
                ArbreGenealogique arbre = cible.getArbre();

                // Ajouter le demandeur à l’arbre s’il n’y est pas
                Noeud noeudDemandeur = arbre.getNoeudParPersonne(demandeur);
                if (noeudDemandeur == null) {
                    noeudDemandeur = new Noeud(demandeur);
                    arbre.ajouterNoeud(noeudDemandeur);
                    try (Connection conn = Database.getConnection()) {
                        NoeudDAO dao = new NoeudDAO(conn);
                        dao.sauvegarderNoeud(noeudDemandeur, arbre.getId());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // Assurer que le noeud de l'utilisateur (cible) est bien présent
                Noeud noeudCible = arbre.getNoeudParPersonne(cible);
                if (noeudCible == null) {
                    noeudCible = new Noeud(cible);
                    arbre.ajouterNoeud(noeudCible);
                    try (Connection conn = Database.getConnection()) {
                        NoeudDAO dao = new NoeudDAO(conn);
                        dao.sauvegarderNoeud(noeudCible, arbre.getId());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                // Ajouter le lien dans l’arbre
                try (Connection conn = Database.getConnection()) {
                    String sql = "INSERT INTO noeud_lien (id_parent, id_enfant, arbre_id) VALUES (?, ?, ?)";
                    PreparedStatement stmt = conn.prepareStatement(sql);

                    switch (lien) {
                        case PERE, MERE -> {
                            noeudCible.ajouterParent(noeudDemandeur);
                            stmt.setInt(1, noeudDemandeur.getId());
                            stmt.setInt(2, noeudCible.getId());
                        }
                        case FILS, FILLE -> {
                            noeudCible.ajouterEnfant(noeudDemandeur);
                            stmt.setInt(1, noeudCible.getId());
                            stmt.setInt(2, noeudDemandeur.getId());
                        }
                        default -> {
                            System.out.println("⚠️ Type de lien non géré automatiquement.");
                            return;
                        }
                    }

                    stmt.setInt(3, arbre.getId());
                    stmt.executeUpdate();
                    stmt.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

                root.getChildren().remove(labelBox);
            });

            refuser.setOnAction(e -> {
                LienManager.refuserDemande(demande);
                root.getChildren().remove(labelBox);
            });

            root.getChildren().add(labelBox);
        }

        Scene scene = new Scene(root);
        stage.setTitle("Demandes de lien");
        stage.setScene(scene);
        stage.show();
    }
    
}
