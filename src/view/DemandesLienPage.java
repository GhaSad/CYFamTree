package view;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.LienEnAttente;
import model.Personne;
import service.LienManager;
import model.Utilisateur;
import model.ArbreGenealogique;
import model.Noeud;
import model.TypeLien;
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

                Utilisateur demandeur = (Utilisateur) demande.getDemandeur(); // celui qui a envoyé la demande
                Utilisateur cible = utilisateur;                               // celui qui accepte
                TypeLien lien = demande.getTypeLien();

                ArbreGenealogique arbreDemandeur = demandeur.getArbre();
                Noeud noeudCible = arbreDemandeur.getNoeudParPersonne(cible);

                if (noeudCible == null) {
                    noeudCible = new Noeud(cible);
                    arbreDemandeur.ajouterNoeud(noeudCible);
                    try (Connection conn = Database.getConnection()) {
                        NoeudDAO dao = new NoeudDAO(conn);
                        dao.sauvegarderNoeud(noeudCible, arbreDemandeur.getId());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                Noeud racine = arbreDemandeur.getNoeudParPersonne(demandeur);
                if (racine != null) {
                    switch (lien) {
                        case PERE:
                        case MERE:
                            racine.ajouterParent(noeudCible);
                            break;
                        case FILS:
                        case FILLE:
                            racine.ajouterEnfant(noeudCible);
                            break;
                        default:
                            System.out.println("⚠️ Type de lien non géré automatiquement.");
                    }

                    try (Connection conn = Database.getConnection()) {
                        String sql = "INSERT INTO noeud_lien (id_parent, id_enfant, arbre_id) VALUES (?, ?, ?)";
                        PreparedStatement stmt = conn.prepareStatement(sql);

                        if (lien == TypeLien.PERE || lien == TypeLien.MERE) {
                            stmt.setInt(1, noeudCible.getId());
                            stmt.setInt(2, racine.getId());
                        } else if (lien == TypeLien.FILS || lien == TypeLien.FILLE) {
                            stmt.setInt(1, racine.getId());
                            stmt.setInt(2, noeudCible.getId());
                        }
                        stmt.setInt(3, arbreDemandeur.getId());
                        stmt.executeUpdate();
                        stmt.close();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
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
