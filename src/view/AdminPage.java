package view;

import dao.UtilisateurDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import model.Utilisateur;

import java.util.List;

public class AdminPage {

    private Stage stage;
    private TableView<Utilisateur> tableView;
    private ObservableList<Utilisateur> utilisateursObservable;
    private UtilisateurDAO utilisateurDAO;

    private static final String ADMIN_PASSWORD = "admin123";

    public AdminPage() {
        utilisateurDAO = new UtilisateurDAO();
    }

    public void show() {
        if (!demanderMotDePasseAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", null, "Mot de passe incorrect. Accès refusé.");
            return;
        }

        stage = new Stage();
        stage.setTitle("Administration des utilisateurs");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Création de la TableView
        tableView = new TableView<>();
        utilisateursObservable = FXCollections.observableArrayList();
        tableView.setItems(utilisateursObservable);

        TableColumn<Utilisateur, String> loginCol = new TableColumn<>("Login");
        loginCol.setCellValueFactory(new PropertyValueFactory<>("login"));

        TableColumn<Utilisateur, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));

        TableColumn<Utilisateur, String> prenomCol = new TableColumn<>("Prénom");
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));

        TableColumn<Utilisateur, String> dateNaissanceCol = new TableColumn<>("Date Naissance");
        dateNaissanceCol.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDateNaissance() != null) {
                return new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDateNaissance().toString());
            } else {
                return new javafx.beans.property.SimpleStringProperty("");
            }
        });

        TableColumn<Utilisateur, String> nationaliteCol = new TableColumn<>("Nationalité");
        nationaliteCol.setCellValueFactory(new PropertyValueFactory<>("nationalite"));

        TableColumn<Utilisateur, String> valideCol = new TableColumn<>("Validé");
        valideCol.setCellValueFactory(cellData -> {
            boolean estValide = cellData.getValue().getEstValide();
            return new javafx.beans.property.SimpleStringProperty(estValide ? "Validé" : "En attente");
        });

        tableView.getColumns().addAll(loginCol, nomCol, prenomCol, dateNaissanceCol, nationaliteCol, valideCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        root.setCenter(tableView);

        // Boutons
        Button btnValider = new Button("Valider inscription");
        Button btnSupprimer = new Button("Supprimer utilisateur");
        Button btnRetour = new Button("Retour à l'accueil");

        btnValider.setOnAction(e -> validerUtilisateur());
        btnSupprimer.setOnAction(e -> supprimerUtilisateur());
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        HBox buttonBox = new HBox(10, btnValider, btnSupprimer, btnRetour);
        buttonBox.setPadding(new Insets(10));
        root.setBottom(buttonBox);

        chargerUtilisateurs();

        Scene scene = new Scene(root, 700, 400);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    private void chargerUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurDAO.findAll(); // ✅
        utilisateursObservable.setAll(utilisateurs);
    }

    private void validerUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", null, "Veuillez sélectionner un utilisateur.");
            return;
        }
        if (selected.getEstValide()) {
            showAlert(Alert.AlertType.INFORMATION, "Information", null, "Cet utilisateur est déjà validé.");
            return;
        }
        utilisateurDAO.validerUtilisateur(selected.getLogin());
        chargerUtilisateurs();
        showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Inscription validée.");
    }

    private void supprimerUtilisateur() {
        Utilisateur selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Attention", null, "Veuillez sélectionner un utilisateur.");
            return;
        }
        utilisateurDAO.supprimerUtilisateur(selected.getLogin());
        chargerUtilisateurs();
        showAlert(Alert.AlertType.INFORMATION, "Succès", null, "Utilisateur supprimé.");
    }

    private boolean demanderMotDePasseAdmin() {
        // Dialogue personnalisé avec PasswordField
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Mot de passe admin");
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        ButtonType btnOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(btnOk, btnCancel);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");

        dialog.getDialogPane().setContent(passwordField);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == btnOk) {
                return passwordField.getText();
            }
            return null;
        });

        String result = dialog.showAndWait().orElse(null);
        return ADMIN_PASSWORD.equals(result);
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
