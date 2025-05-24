package view;

import dao.UtilisateurDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utilisateur;

/** Interface permettant à l'utilisateur de modifier son mot de passe. */
public class ChangementMDPPage {

    private Stage stage;
    private PasswordField nouveauMdpField;
    private PasswordField confirmationField;
    private Button validerBtn;
    private Utilisateur utilisateur;

    public ChangementMDPPage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("🔐 Changer mon mot de passe");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);

        Label titre = new Label("Veuillez définir un nouveau mot de passe");
        titre.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        nouveauMdpField = new PasswordField();
        nouveauMdpField.setPromptText("Nouveau mot de passe");

        confirmationField = new PasswordField();
        confirmationField.setPromptText("Confirmer le mot de passe");

        validerBtn = new Button("Valider");
        validerBtn.setOnAction(e -> handleValider());

        layout.getChildren().addAll(titre, nouveauMdpField, confirmationField, validerBtn);

        Scene scene = new Scene(layout, 350, 250);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private void handleValider() {
        String nouveauMdp = nouveauMdpField.getText();
        String confirmation = confirmationField.getText();

        if (nouveauMdp.isEmpty() || confirmation.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Veuillez remplir les deux champs.");
            return;
        }

        if (!nouveauMdp.equals(confirmation)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas.");
            return;
        }

        // Mise à jour du mot de passe + désactivation du flag "doit_changer_mdp"
        UtilisateurDAO.updateMotDePasse(utilisateur.getLogin(), nouveauMdp);
        utilisateur.setDoitChangerMotDePasse(false);

        showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe mis à jour avec succès !");
        stage.close();

        // Retour à l'accueil ou connexion
        new ConnexionPage().show();
    }

    private void showAlert(Alert.AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}
