package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Personne;
import model.RessourcePartagee;
import model.TypeRessource;
import model.Utilisateur;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RPPage {

    private Stage stage;
    private Utilisateur utilisateur;
    private VBox ressourcesBox;

    public RPPage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        this.stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("Ressources partagées");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("Mes ressources reçues");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ressourcesBox = new VBox(10);
        ressourcesBox.setAlignment(Pos.TOP_LEFT);
        chargerRessourcesRecues();

        Separator sep = new Separator();

        Label partageLabel = new Label("Partager une ressource");
        partageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        ComboBox<Personne> destinataireBox = new ComboBox<>();
        destinataireBox.setPromptText("Choisir un destinataire");
        destinataireBox.getItems().setAll(getMembresFamille(utilisateur)); // à implémenter

        ComboBox<TypeRessource> typeBox = new ComboBox<>();
        typeBox.getItems().setAll(TypeRessource.values());
        typeBox.setPromptText("Type de ressource");

        Button btnChoisirFichier = new Button("Choisir un fichier");
        Label fichierLabel = new Label("Aucun fichier choisi");

        final File[] fichierSelectionne = new File[1];
        btnChoisirFichier.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            File f = fileChooser.showOpenDialog(stage);
            if (f != null) {
                fichierSelectionne[0] = f;
                fichierLabel.setText(f.getName());
            }
        });

        Button btnEnvoyer = new Button("Envoyer");
        btnEnvoyer.setOnAction(e -> {
            Personne dest = destinataireBox.getValue();
            TypeRessource type = typeBox.getValue();
            File fichier = fichierSelectionne[0];

            if (dest == null || type == null || fichier == null) {
                showAlert("Veuillez remplir tous les champs.");
                return;
            }

            // Créer et sauvegarder la ressource partagée
            RessourcePartagee res = new RessourcePartagee(type, fichier.getAbsolutePath(), utilisateur, List.of(dest));
            // TODO : Sauvegarder la ressource dans la base (DAO à implémenter)
            showAlert("Ressource envoyée avec succès à " + dest.getPrenom());
        });

        VBox partageBox = new VBox(10, destinataireBox, typeBox, btnChoisirFichier, fichierLabel, btnEnvoyer);
        partageBox.setAlignment(Pos.CENTER);

        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilUtilisateur.show(utilisateur);
        });

        root.getChildren().addAll(titre, ressourcesBox, sep, partageLabel, partageBox,btnRetour);

        stage.setScene(new Scene(root, 600, 600));
        stage.show();
    }

    private void chargerRessourcesRecues() {
        // TODO : charger depuis la base les ressources dont utilisateur est destinataire
        List<RessourcePartagee> recues = new ArrayList<>();

        // exemple fictif
        // recues = RessourceDAO.getRessourcesPour(utilisateur);

        if (recues.isEmpty()) {
            ressourcesBox.getChildren().add(new Label("Aucune ressource reçue."));
        } else {
            for (RessourcePartagee res : recues) {
                ressourcesBox.getChildren().add(new Label(res.getAuteur().getPrenom() + " a partagé : " + res.getFichier()));
            }
        }
    }

    private List<Personne> getMembresFamille(Utilisateur u) {
        // TODO : retourner uniquement les personnes liées à l'utilisateur dans son arbre
        return new ArrayList<>();
    }

    public void show(){
        stage.show();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.setTitle("Information");
        alert.showAndWait();
    }
}
