package view;

import dao.RessourceDAO;
import dao.UtilisateurDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.RessourcePartagee;
import model.TypeRessource;
import model.Utilisateur;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/** Interface de gestion des ressources partagées entre utilisateurs. */
public class RPPage {

    private final Stage stage;
    private final Utilisateur utilisateur;
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

        Label titre = new Label("📂 Mes ressources reçues");
        titre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ressourcesBox = new VBox(10);
        ressourcesBox.setAlignment(Pos.TOP_LEFT);
        chargerRessourcesRecues();

        Separator sep = new Separator();

        Label partageLabel = new Label("📤 Partager une ressource");
        partageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        TextField codePublicField = new TextField();
        codePublicField.setPromptText("Code public du destinataire (ex: CY79)");

        ComboBox<TypeRessource> typeBox = new ComboBox<>();
        typeBox.getItems().setAll(TypeRessource.values());
        typeBox.setPromptText("Type de ressource");

        Label fichierLabel = new Label("Aucun fichier choisi");
        final File[] fichierSelectionne = new File[1];
        Button btnChoisirFichier = new Button("Choisir un fichier");
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
            String codePublic = codePublicField.getText().trim();
            Optional<Utilisateur> destOpt = new UtilisateurDAO().trouverParCodePublic(codePublic);

            if (destOpt.isEmpty()) {
                showAlert("Aucun utilisateur trouvé avec ce code public.");
                return;
            }

            Utilisateur destinataire = destOpt.get();
            TypeRessource type = typeBox.getValue();
            File fichier = fichierSelectionne[0];

            if (type == null || fichier == null) {
                showAlert("Veuillez remplir tous les champs.");
                return;
            }

            try {
                File dossierDestination = new File("ressources_partagees");
                if (!dossierDestination.exists()) dossierDestination.mkdirs();

                // Construire le nom de fichier unique
                String nouveauNom = System.currentTimeMillis() + "_" + fichier.getName();
                File destination = new File(dossierDestination, nouveauNom);

                // Copier le fichier dans le dossier local
                java.nio.file.Files.copy(fichier.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                // Créer la ressource avec le chemin du fichier copié
                RessourcePartagee res = new RessourcePartagee(type, destination.getPath(), utilisateur, List.of(destinataire));

                // Sauvegarde en base
                RessourceDAO.sauvegarder(res, destinataire.getId());
                showAlert("✅ Ressource envoyée avec succès à " + destinataire.getPrenom());

            } catch (Exception ex) {
                ex.printStackTrace();
                showAlert("❌ Erreur lors de l'envoi de la ressource.");
            }
        });

        VBox partageBox = new VBox(10, codePublicField, typeBox, btnChoisirFichier, fichierLabel, btnEnvoyer);
        partageBox.setAlignment(Pos.CENTER);

        Button btnRetour = new Button("Retour");
        btnRetour.setOnAction(e -> {
            stage.close();
            AccueilUtilisateur.show(utilisateur);
        });

        root.getChildren().addAll(titre, ressourcesBox, sep, partageLabel, partageBox, btnRetour);

        stage.setScene(new Scene(root, 600, 600));
        stage.show();
    }

    private void chargerRessourcesRecues() {
        ressourcesBox.getChildren().clear();
        try {
            List<RessourcePartagee> recues = RessourceDAO.getRessourcesPour(utilisateur.getId());

            if (recues.isEmpty()) {
                ressourcesBox.getChildren().add(new Label("Aucune ressource reçue."));
            } else {
                for (RessourcePartagee res : recues) {
                    Label label = new Label("📁 " + res.getAuteur().getPrenom() + " a partagé : " + res.getFichier() + " (" + res.getTypeRessource() + ")");

                    Button ouvrirBtn = new Button("Ouvrir");
                    ouvrirBtn.setOnAction(ev -> {
                        try {
                            File fichier = new File(res.getFichier());
                            if (fichier.exists()) {
                                java.awt.Desktop.getDesktop().open(fichier);
                            } else {
                                showAlert("Fichier introuvable : " + fichier.getAbsolutePath());
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            showAlert("Impossible d'ouvrir le fichier.");
                        }
                    });

                    HBox ligne = new HBox(10, label, ouvrirBtn);
                    ligne.setAlignment(Pos.CENTER_LEFT);
                    ressourcesBox.getChildren().add(ligne);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            ressourcesBox.getChildren().add(new Label("❌ Erreur de chargement des ressources."));
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message);
        alert.setHeaderText(null);
        alert.setTitle("Information");
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }
}