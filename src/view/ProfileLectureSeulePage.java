package view;

import model.Utilisateur;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utilisateur;

/** Interface de consultation d'un profil utilisateur en lecture seule. */
public class ProfileLectureSeulePage {

    private final Utilisateur utilisateur;

    public ProfileLectureSeulePage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Profil de " + utilisateur.getPrenom());

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label nomLabel = new Label("Nom : " + utilisateur.getNom());
        Label prenomLabel = new Label("Prénom : " + utilisateur.getPrenom());
        Label dateNaissanceLabel = new Label("Date de naissance : " + utilisateur.getDateNaissance());
        Label nationaliteLabel = new Label("Nationalité : " + utilisateur.getNationalite());
        Label codePublicLabel = new Label("Code public : " + utilisateur.getCodePublic());

        root.getChildren().addAll(
                new Label("Profil (lecture seule)"),
                nomLabel,
                prenomLabel,
                dateNaissanceLabel,
                nationaliteLabel,
                codePublicLabel
        );

        stage.setScene(new Scene(root, 350, 300));
        stage.show();
    }

}
