package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utilisateur;
import dao.UtilisateurDAO;

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

        // ✅ On affiche la fenêtre ici
        stage.setScene(new Scene(root, 350, 450));
        stage.show();

        // Champs modifiables
        TextField prenomField = new TextField(utilisateur.getPrenom());
        TextField nomField = new TextField(utilisateur.getNom());

        PasswordField mdpField = new PasswordField();
        mdpField.setPromptText("Nouveau mot de passe");

        PasswordField confirmMdpField = new PasswordField();
        confirmMdpField.setPromptText("Confirmer le mot de passe");

        // Champs non modifiables
        Label dateNaissance = new Label("Date de naissance : " + utilisateur.getDateNaissance());
        Label nationalite = new Label("Nationalité : " + utilisateur.getNationalite());

        Button enregistrer = new Button("Enregistrer");
        
        Button voirDemandesLien = new Button("Voir mes demandes de lien");
        voirDemandesLien.setOnAction(e -> {
            DemandesLienPage.show(utilisateur); // Appelle la page JavaFX avec l'utilisateur connecté
        });

        enregistrer.setOnAction(e -> {
            String nouveauPrenom = prenomField.getText().trim();
            String nouveauNom = nomField.getText().trim();
            String mdp1 = mdpField.getText();
            String mdp2 = confirmMdpField.getText();

            if (!mdp1.isEmpty() || !mdp2.isEmpty()) {
                if (!mdp1.equals(mdp2)) {
                    showErreur("Les mots de passe ne correspondent pas.");
                    return;
                }
                UtilisateurDAO.updateMotDePasse(utilisateur.getLogin(), mdp1);
            }

            utilisateur.setPrenom(nouveauPrenom);
            utilisateur.setNom(nouveauNom);
            UtilisateurDAO.updateProfil(utilisateur);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Profil mis à jour avec succès.");
            alert.showAndWait();

            stage.close(); // ✅ ferme la fenêtre
            AccueilUtilisateur.show(utilisateur); // ✅ relance accueil
        });

        root.getChildren().addAll(
                new Label("Prénom :"), prenomField,
                new Label("Nom :"), nomField,
                dateNaissance,
                nationalite,
                new Label("Nouveau mot de passe :"), mdpField,
                new Label("Confirmer :"), confirmMdpField,
                enregistrer,
                voirDemandesLien
        );
    }


    private void showErreur(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }
}
