package view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

public class AccueilUtilisateur extends Application {

    private static Utilisateur utilisateurStatic; // stockage temporaire
    private Utilisateur utilisateur;
    private Stage primaryStage;

    public AccueilUtilisateur() {
        // Constructeur par défaut requis par JavaFX
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;

        // ✅ récupération de l'utilisateur passé via méthode statique
        this.utilisateur = utilisateurStatic;

        if (utilisateur == null) {
            throw new IllegalStateException("Aucun utilisateur fourni à AccueilUtilisateur");
        }

        stage.setTitle("Accueil - Arbre Généalogique");

        VBox root = new VBox();
        root.setSpacing(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label bienvenue = new Label("Bienvenue, " + utilisateur.getPrenom() + " !");
        root.getChildren().add(bienvenue);

        if (utilisateur.getArbre() == null) {
            Label info = new Label("Vous n'avez pas encore d'arbre généalogique.");
            Button creerBtn = new Button("Créer mon arbre");

            creerBtn.setOnAction(e -> {
                ArbreGenealogique arbre = new ArbreGenealogique(utilisateur, utilisateur);
                Noeud racine = new Noeud(utilisateur);
                arbre.ajouterNoeud(racine);
                utilisateur.setArbre(arbre);

                primaryStage.close();
                arbre.afficherGraphique();  // À adapter si besoin
            });

            root.getChildren().addAll(info, creerBtn);

        } else {
            Label info = new Label("Chargement de votre arbre généalogique...");
            root.getChildren().add(info);

            primaryStage.close();
            utilisateur.getArbre().afficherGraphique();  // À adapter si besoin
            return;
        }

        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // ✅ Méthode statique pour lancer l'écran avec un utilisateur
    public static void show(Utilisateur utilisateur) {
        utilisateurStatic = utilisateur;
        launch();  // Appelle start() via le constructeur par défaut
    }
}
