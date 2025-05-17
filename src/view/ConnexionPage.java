package view;

import dao.AuthentificationDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Utilisateur;

public class ConnexionPage {

    private Stage stage;
    private TextField loginField;
    private PasswordField passwordField;
    private Button loginButton;
    private AuthentificationDAO authentificationDAO;

    public ConnexionPage() {
        authentificationDAO = new AuthentificationDAO();
        stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("Connexion");

        Label loginLabel = new Label("Login:");
        loginField = new TextField();
        loginField.setPromptText("Votre login");

        Label passwordLabel = new Label("Mot de Passe:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Votre mot de passe");

        loginButton = new Button("Se connecter");
        Button btnRetour = new Button("Retour à l'accueil");

        loginButton.setOnAction(e -> handleConnexion());
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        VBox vbox = new VBox(10, loginLabel, loginField, passwordLabel, passwordField, loginButton, btnRetour);
        vbox.setPadding(new Insets(20));
        stage.setScene(new Scene(vbox, 350, 300));
        stage.centerOnScreen();
    }

    private void handleConnexion() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();

        if (login.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs.");
            return;
        }

        Utilisateur utilisateur = authentificationDAO.authentifier(login, password);

        if (utilisateur == null) {
            showAlert(Alert.AlertType.ERROR, "Échec de la connexion", "Vérifiez vos identifiants.");
        } else if (!utilisateur.getEstValide()) {
            showAlert(Alert.AlertType.WARNING, "Compte en attente", "Compte en cours de validation. Veuillez patienter.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Connexion réussie", "Bienvenue " + utilisateur.getPrenom() + " !");
            stage.close();
            AccueilUtilisateur.show(utilisateur);
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}
