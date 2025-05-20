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
