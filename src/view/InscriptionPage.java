package view;

import dao.AuthentificationDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Nationalite;
import model.Utilisateur;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class InscriptionPage {

    private Stage stage;
    private TextField loginField;
    private PasswordField passwordField;
    private TextField nomField;
    private TextField prenomField;
    private TextField emailField;
    private TextField numeroSecuField;
    private TextField cheminCarteIdentiteField;
    private TextField cheminPhotoField;
    private DatePicker dateNaissancePicker; // ✅ accessible partout
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
        stage.setTitle("Inscription - CYFamTree");

        // Conteneur principal
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label titre = new Label("Créer un compte sur CYFamTree");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Formulaire
        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setAlignment(Pos.CENTER);

        Label loginLabel = new Label("Login :");
        loginField = new TextField();
        loginField.setPromptText("Choisissez un login");

        Label nomLabel = new Label("Nom :");
        nomField = new TextField();
        nomField.setPromptText("Votre nom");

        Label prenomLabel = new Label("Prénom :");
        prenomField = new TextField();
        prenomField.setPromptText("Votre prénom");

        Label dateLabel = new Label("Date de naissance :");
        dateNaissancePicker = new DatePicker();
        dateNaissancePicker.setPromptText("YYYY-MM-DD");
        dateNaissancePicker.setValue(LocalDate.of(2000, 1, 1));

        Label nationaliteLabel = new Label("Nationalité :");
        nationaliteComboBox = new ComboBox<>();
        nationaliteComboBox.getItems().setAll(Nationalite.values());
        nationaliteComboBox.getSelectionModel().selectFirst();

        // Ajout des champs
        Label emailLabel = new Label("Email :");
        emailField = new TextField();
        emailField.setPromptText("ex: exemple@cyu.fr");

        Label numeroSecuLabel = new Label("Numéro sécurité sociale :");
        numeroSecuField = new TextField();
        numeroSecuField.setPromptText("Numéro sécurité sociale");

        Label carteIdentiteLabel = new Label("Carte d'identité :");
        cheminCarteIdentiteField = new TextField();
        cheminCarteIdentiteField.setPromptText("Aucun fichier choisi");
        cheminCarteIdentiteField.setEditable(false);
        Button btnCarte = new Button("Parcourir...");

        Label photoLabel = new Label("Photo numérique :");
        cheminPhotoField = new TextField();
        cheminPhotoField.setPromptText("Aucun fichier choisi");
        cheminPhotoField.setEditable(false);
        Button btnPhoto = new Button("Parcourir...");


// Gestion des FileChooser
        FileChooser fileChooser = new FileChooser();
        btnCarte.setOnAction(e -> {
            File fichier = fileChooser.showOpenDialog(stage);
            if (fichier != null) cheminCarteIdentiteField.setText(fichier.getAbsolutePath());
        });

        btnPhoto.setOnAction(e -> {
            File fichier = fileChooser.showOpenDialog(stage);
            if (fichier != null) cheminPhotoField.setText(fichier.getAbsolutePath());
        });

        // Positionnement des éléments
        form.add(loginLabel, 0, 0);
        form.add(loginField, 1, 0);

        form.add(nomLabel, 0, 2);
        form.add(nomField, 1, 2);

        form.add(prenomLabel, 0, 3);
        form.add(prenomField, 1, 3);

        form.add(dateLabel, 0, 4);
        form.add(dateNaissancePicker, 1, 4);

        form.add(nationaliteLabel, 0, 5);
        form.add(nationaliteComboBox, 1, 5);

        form.add(emailLabel, 0, 6);
        form.add(emailField, 1, 6);

        form.add(numeroSecuLabel, 0, 7);
        form.add(numeroSecuField, 1, 7);

        form.add(carteIdentiteLabel, 0, 8);
        form.add(new HBox(10, cheminCarteIdentiteField, btnCarte), 1, 8);

        form.add(photoLabel, 0, 9);
        form.add(new HBox(10, cheminPhotoField, btnPhoto), 1, 9);

        // Boutons
        registerButton = new Button("S'inscrire");
        btnRetour = new Button("Retour");

        registerButton.setPrefWidth(150);
        btnRetour.setPrefWidth(150);

        HBox boutons = new HBox(20, registerButton, btnRetour);
        boutons.setAlignment(Pos.CENTER);

        // Actions
        registerButton.setOnAction(e -> handleRegister());
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        // Construction finale
        root.getChildren().addAll(titre, form, boutons);

        Scene scene = new Scene(root, 450, 500);
        stage.setScene(scene);
        stage.centerOnScreen();
    }


    private void handleRegister() {
        String login = loginField.getText().trim();
        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        String email = emailField.getText().trim();
        String numeroSecu = numeroSecuField.getText().trim();
        String carteIdentitePath = cheminCarteIdentiteField.getText().trim();
        String photoPath = cheminPhotoField.getText().trim();
        Nationalite nationalite = nationaliteComboBox.getSelectionModel().getSelectedItem();

        if (login.isEmpty() || nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || numeroSecu.isEmpty()
                || carteIdentitePath.isEmpty() || photoPath.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Tous les champs doivent être remplis.");
            return;
        }

        LocalDate dateNaissance = dateNaissancePicker.getValue();
        if (dateNaissance == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez sélectionner une date.");
            return;
        }

        if (authentificationDAO.userExists(login)) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Ce login existe déjà.");
            return;
        }

        Utilisateur utilisateur = new Utilisateur(
                nom, prenom, dateNaissance, nationalite, 0,
                true, false, email, numeroSecu, carteIdentitePath, photoPath
        );

        try {
            authentificationDAO.save(utilisateur, login, null); // mot de passe = prénom automatiquement
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Inscription enregistrée !\nMot de passe initial : votre prénom.");
            stage.close();
            new ConnexionPage().show();
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
