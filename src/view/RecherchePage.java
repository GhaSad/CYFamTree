package view;

import dao.UtilisateurDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import model.ArbreGenealogique;
import model.Nationalite;
import model.Utilisateur;

import java.util.List;

public class RecherchePage {

    private Stage stage;
    private TextField nomField;
    private TextField prenomField;
    private ComboBox<Nationalite> nationaliteComboBox;
    private VBox resultBox;
    private VBox root;
    private Utilisateur utilisateur;

    public RecherchePage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("Recherche d'utilisateurs ou personnes");

        root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("Rechercher une personne ou un utilisateur");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.setAlignment(Pos.CENTER);

        nomField = new TextField();
        prenomField = new TextField();
        nationaliteComboBox = new ComboBox<>();
        nationaliteComboBox.getItems().setAll(Nationalite.values());
        nationaliteComboBox.getItems().add(0, null); // permet un choix vide
        nationaliteComboBox.getSelectionModel().selectFirst();

        form.add(new Label("Nom :"), 0, 0);
        form.add(nomField, 1, 0);
        form.add(new Label("Prénom :"), 0, 1);
        form.add(prenomField, 1, 1);
        form.add(new Label("Nationalité :"), 0, 2);
        form.add(nationaliteComboBox, 1, 2);

        Button rechercherBtn = new Button("Rechercher");
        rechercherBtn.setOnAction(e -> handleRecherche());

        resultBox = new VBox(15);
        resultBox.setAlignment(Pos.TOP_LEFT);

        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilUtilisateur.show(utilisateur);
        });

        root.getChildren().addAll(titre, form, rechercherBtn, resultBox, btnRetour);

        stage.setScene(new Scene(root, 600, 500));
        stage.centerOnScreen();
    }

    private void handleRecherche() {
        resultBox.getChildren().clear();

        String nom = nomField.getText().trim();
        String prenom = prenomField.getText().trim();
        Nationalite nat = nationaliteComboBox.getValue();

        UtilisateurDAO dao = new UtilisateurDAO();
        List<Utilisateur> resultats = dao.rechercherParCritere(nom, prenom, nat);

        if (resultats.isEmpty()) {
            resultBox.getChildren().add(new Label("Aucun résultat."));
            return;
        }

        for (Utilisateur u : resultats) {
            VBox carte = new VBox(5);
            carte.setStyle("-fx-border-color: black; -fx-padding: 10;");

            Label info = new Label(u.getPrenom() + " " + u.getNom() + " (" + u.getNationalite() + ")");
            Button btnAfficher = new Button("Afficher arbre");

            btnAfficher.setOnAction(e -> {
                ArbreGenealogique arbre = u.getArbre();
                if (arbre != null) {
                    arbre.afficherArbreGraphiqueCustom();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Cet utilisateur n'a pas encore d'arbre.");
                    alert.show();
                }
            });

            carte.getChildren().addAll(info, btnAfficher);
            resultBox.getChildren().add(carte);
        }
    }

    public void show() {
        stage.show();
    }
}
