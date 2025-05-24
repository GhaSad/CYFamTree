package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utilisateur;
import dao.UtilisateurDAO;

import java.io.File;
import java.io.FileInputStream;

/** Interface de gestion du profil utilisateur (édition des infos personnelles). */
public class ProfilPage {

    private Utilisateur utilisateur;

    public ProfilPage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Mon Profil");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        try {
            File photoFile = new File(utilisateur.getPhotoNumerique());
            if (photoFile.exists()) {
                Image image = new Image(new FileInputStream(photoFile));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(150);
                imageView.setPreserveRatio(true);
                root.getChildren().add(imageView);
            } else {
                root.getChildren().add(new Label("📸 Photo non disponible"));
            }
        } catch (Exception e) {
            root.getChildren().add(new Label("⚠️ Erreur lors du chargement de la photo"));
            e.printStackTrace();
        }

        // Champs non modifiables (Label au lieu de TextField)
        Label nomLabel = new Label("Nom : " + utilisateur.getNom());
        Label prenomLabel = new Label("Prénom : " + utilisateur.getPrenom());

        // Champs modifiables
        TextField emailField = new TextField(utilisateur.getEmail());
        TextField numTelField = new TextField(utilisateur.getNumTel());

        PasswordField mdpField = new PasswordField();
        mdpField.setPromptText("Nouveau mot de passe");

        PasswordField confirmMdpField = new PasswordField();
        confirmMdpField.setPromptText("Confirmer le mot de passe");

        Label dateNaissance = new Label("Date de naissance : " + utilisateur.getDateNaissance());
        Label nationalite = new Label("Nationalité : " + utilisateur.getNationalite());

        Button enregistrer = new Button("Enregistrer");
        Button voirDemandesLien = new Button("Voir mes demandes de lien");

        voirDemandesLien.setOnAction(e -> {
            DemandesLienPage.show(utilisateur);
        });

        enregistrer.setOnAction(e -> {
            String mdp1 = mdpField.getText();
            String mdp2 = confirmMdpField.getText();
            String email = emailField.getText().trim();
            String tel = numTelField.getText().trim();

            if (!mdp1.isEmpty() || !mdp2.isEmpty()) {
                if (!mdp1.equals(mdp2)) {
                    showErreur("Les mots de passe ne correspondent pas.");
                    return;
                }
                UtilisateurDAO.updateMotDePasse(utilisateur.getLogin(), mdp1);
            }

            utilisateur.setEmail(email);
            utilisateur.setNumTel(tel);
            UtilisateurDAO.updateProfil(utilisateur);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Profil mis à jour avec succès.");
            alert.showAndWait();

            stage.close();
            AccueilUtilisateur.show(utilisateur);
        });

        root.getChildren().addAll(
                nomLabel,
                prenomLabel,
                new Label("Email :"), emailField,
                new Label("Téléphone :"), numTelField,
                dateNaissance,
                nationalite,
                new Label("Nouveau mot de passe :"), mdpField,
                new Label("Confirmer :"), confirmMdpField,
                enregistrer,
                voirDemandesLien
        );

        stage.setScene(new Scene(root, 400, 500));
        stage.show();
    }

    private void showErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
