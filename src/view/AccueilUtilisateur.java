package view;

import dao.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

import java.sql.Connection;

public class AccueilUtilisateur extends javafx.application.Application {

    private static Utilisateur utilisateurStatic; // stockage temporaire
    private Utilisateur utilisateur;
    private Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        this.utilisateur = utilisateurStatic;

        if (utilisateur == null) {
            throw new IllegalStateException("Aucun utilisateur fourni à AccueilUtilisateur");
        }

        // Chargement complet de l'arbre depuis la base
        ArbreGenealogique arbre = ArbreDAO.chargerArbreParUtilisateur(this.utilisateur);
        this.utilisateur.setArbre(arbre);

        stage.setTitle("Accueil - Arbre Généalogique");

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label bienvenue = new Label("Bienvenue, " + utilisateur.getPrenom() + " !");
        root.getChildren().add(bienvenue);

        VBox arbreBox = new VBox(15);
        arbreBox.setAlignment(Pos.CENTER);
        arbreBox.setPadding(new Insets(20));

        if (utilisateur.getArbre() == null) {
            Label info = new Label("😕 Vous n'avez pas encore d'arbre généalogique.");
            info.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

            Button creerBtn = new Button("Créer mon arbre");
            creerBtn.setPrefWidth(150);

            creerBtn.setOnAction(e -> {
                try {
                    if (utilisateur.getArbre() != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vous avez déjà un arbre.");
                        alert.show();
                        return;
                    }

                    Personne personneRacine = utilisateur;
                    Noeud racine = new Noeud(personneRacine);

                    try (Connection conn = Database.getConnection()) {
                        NoeudDAO noeudDAO = new NoeudDAO(conn);
                        noeudDAO.sauvegarderNoeud(racine, -1);
                    }

                    ArbreGenealogique arbre1 = new ArbreGenealogique(utilisateur, personneRacine);
                    arbre1.ajouterNoeud(racine);
                    utilisateur.setArbre(arbre1);

                    int idArbre = ArbreDAO.creerArbre(arbre1);
                    arbre1.setId(idArbre);

                    try (Connection conn = Database.getConnection()) {
                        NoeudDAO noeudDAO = new NoeudDAO(conn);
                        noeudDAO.ajouterArbreIdAuNoeud(racine, idArbre);
                    }

                    arbre1.afficherArbreGraphiqueCustom();

                    // Actualiser la zone arbreBox avec le nouveau contenu
                    arbreBox.getChildren().clear();

                    Label arbreInfo = new Label("🌳 Vous avez déjà un arbre généalogique.");
                    arbreInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #228B22;");

                    Button btnConsulterArbre = new Button("Consulter mon arbre");
                    btnConsulterArbre.setOnAction(ev -> {
                        utilisateur.getArbre().afficherArbreGraphiqueCustom();
                    });

                    arbreBox.getChildren().addAll(arbreInfo, btnConsulterArbre);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de la création de l'arbre.").show();
                }
            });

            arbreBox.getChildren().addAll(info, creerBtn);

        } else {
            Label arbreInfo = new Label("🌳 Vous avez déjà un arbre généalogique.");
            arbreInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #228B22;");

            Button btnConsulterArbre = new Button("Consulter mon arbre");
            btnConsulterArbre.setOnAction(e -> utilisateur.getArbre().afficherArbreGraphiqueCustom());

            arbreBox.getChildren().addAll(arbreInfo, btnConsulterArbre);
        }

        root.getChildren().add(arbreBox);

        Button btnProfil = new Button("Mon profil");
        btnProfil.setOnAction(e -> new ProfilPage(utilisateur).show());

        Button ajouterPersonneBtn = new Button("Ajouter une personne");
        ajouterPersonneBtn.setPrefWidth(150);
        ajouterPersonneBtn.setOnAction(e -> new AjoutPersonnePage(utilisateur).show());

        Button btnRessources = new Button("Ressources partagées");
        btnRessources.setOnAction(e -> new RPPage(utilisateur).show());

        Button btnRecherche = new Button("Recherche par critère");
        btnRecherche.setOnAction(e -> new RecherchePage(utilisateur).show());

        root.getChildren().addAll(
                ajouterPersonneBtn,
                btnProfil,
                btnRessources,
                btnRecherche
        );

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public static void show(Utilisateur utilisateur) {
        utilisateurStatic = utilisateur;
        AccueilUtilisateur app = new AccueilUtilisateur();
        Stage stage = new Stage();
        try {
            app.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
