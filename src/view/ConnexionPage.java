package view;

import dao.AuthentificationDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
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
        stage.setTitle("Connexion - CYFamTree");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label titre = new Label("Connexion à CYFamTree");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setVgap(12);
        form.setHgap(10);
        form.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Login :");
        loginField = new TextField();
        loginField.setPromptText("Votre login");

        Label passwordLabel = new Label("Mot de passe :");
        passwordField = new PasswordField();
        passwordField.setPromptText("Votre mot de passe");

        form.add(loginLabel, 0, 0);
        form.add(loginField, 1, 0);

        form.add(passwordLabel, 0, 1);
        form.add(passwordField, 1, 1);

        loginButton = new Button("Se connecter");
        Button btnRetour = new Button("Retour");

        loginButton.setPrefWidth(150);
        btnRetour.setPrefWidth(150);

        HBox boutons = new HBox(20, loginButton, btnRetour);
        boutons.setAlignment(Pos.CENTER);

        loginButton.setOnAction(e -> handleConnexion());
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        root.getChildren().addAll(titre, form, boutons);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
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
        } else if (utilisateur.isDoitChangerMotDePasse()) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Changement requis",
                    "Ceci est votre première connexion. Veuillez changer votre mot de passe.");
            stage.close();
            new ChangementMDPPage(utilisateur).show();
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
