package view;

import dao.AuthentificationDAO;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Nationalite;
import model.Utilisateur;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class InscriptionPage {

    private Stage stage;
    private TextField loginField;
    private PasswordField passwordField;
    private TextField nomField;
    private TextField prenomField;
    private TextField dateNaissanceField;
    private ComboBox<Nationalite> nationaliteComboBox;
    private Button registerButton;
    private Button btnRetour;

    private AuthentificationDAO authentificationDAO;

    public InscriptionPage() {
        authentificationDAO = new AuthentificationDAO();
        stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("Inscription");

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setPadding(new Insets(20));

        // Labels and fields
        Label loginLabel = new Label("Login :");
        loginField = new TextField();
        loginField.setPromptText("Votre login");

        Label passwordLabel = new Label("Mot de passe :");
        passwordField = new PasswordField();
        passwordField.setPromptText("Votre mot de passe");

        Label nomLabel = new Label("Nom :");
        nomField = new TextField();
        nomField.setPromptText("Votre nom");

        Label prenomLabel = new Label("Prénom :");
        prenomField = new TextField();
        prenomField.setPromptText("Votre prénom");

        Label dateLabel = new Label("Date naissance (YYYY-MM-DD) :");
        dateNaissanceField = new TextField();
        dateNaissanceField.setPromptText("YYYY-MM-DD");

        Label nationaliteLabel = new Label("Nationalité :");
        nationaliteComboBox = new ComboBox<>();
        nationaliteComboBox.getItems().setAll(Nationalite.values());
        nationaliteComboBox.getSelectionModel().selectFirst();

        registerButton = new Button("S'inscrire");
        btnRetour = new Button("Retour à l'accueil");

        // Position dans la grille
        grid.add(loginLabel, 0, 0);
        grid.add(loginField, 1, 0);

        grid.add(passwordLabel, 0, 1);
        grid.add(passwordField, 1, 1);

        grid.add(nomLabel, 0, 2);
        grid.add(nomField, 1, 2);

        grid.add(prenomLabel, 0, 3);
        grid.add(prenomField, 1, 3);

        grid.add(dateLabel, 0, 4);
        grid.add(dateNaissanceField, 1, 4);

        grid.add(nationaliteLabel, 0, 5);
        grid.add(nationaliteComboBox, 1, 5);

        grid.add(registerButton, 0, 6);
        grid.add(btnRetour, 1, 6);

        // Actions des boutons
        registerButton.setOnAction(e -> handleRegister());
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        stage.setScene(new Scene(grid, 400, 400));
        stage.centerOnScreen();
    }

    private void handleRegister() {
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String dateStr = dateNaissanceField.getText().trim();
        Nationalite nationalite = nationaliteComboBox.getSelectionModel().getSelectedItem();

        if (login.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        LocalDate dateNaissance;
        try {
            dateNaissance = LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format de date invalide. Utilisez YYYY-MM-DD.");
            return;
        }

        if (authentificationDAO.userExists(login)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ce login existe déjà.");
            return;
        }

        Utilisateur utilisateur = new Utilisateur(nom, prenom, dateNaissance, nationalite, 0, true, false);

        try {
            authentificationDAO.save(utilisateur, login, password);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription en attente ! Votre demande va être traitée");
            stage.close();

            ConnexionPage connexionPage = new ConnexionPage();
            connexionPage.show();

        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'inscription : " + ex.getMessage());
            ex.printStackTrace();
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
