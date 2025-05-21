package view;

import dao.*;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;


import javafx.util.Duration;
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
        System.out.println("🧪 utilisateur ID = " + utilisateur.getId() + " " + utilisateur.getNom());



        ArbreGenealogique arbre = ArbreDAO.chargerArbreParUtilisateur(this.utilisateur);
        this.utilisateur.setArbre(arbre);
        System.out.println("📌 appel setArbre() avec : " + arbre);
        System.out.println("📌 utilisateur.getArbre() = " + this.utilisateur.getArbre());
        if (this.utilisateur.getArbre() != null) {
            System.out.println("✅ Arbre trouvé pour " + utilisateur.getNom() + " !");
        } else {
            System.out.println("⚠️ Aucun arbre trouvé pour " + utilisateur.getNom());
        }


        stage.setTitle("Accueil - Arbre Généalogique");

        VBox root = new VBox();
        root.setSpacing(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        Label bienvenue = new Label("Bienvenue, " + utilisateur.getPrenom() + " !");
        root.getChildren().add(bienvenue);

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
                    Connection conn = Database.getConnection();
                    NoeudDAO noeudDAO = new NoeudDAO(conn);
                    noeudDAO.sauvegarderNoeud(racine, -1);

                    ArbreGenealogique arbre1 = new ArbreGenealogique(utilisateur, personneRacine);
                    arbre1.ajouterNoeud(racine);
                    utilisateur.setArbre(arbre1);

                    int idArbre = ArbreDAO.creerArbre(arbre1);
                    arbre1.setId(idArbre);
                    noeudDAO.ajouterArbreIdAuNoeud(racine, idArbre);

                    primaryStage.close();
                    arbre1.afficherArbreGraphiqueCustom();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de la création de l'arbre.").show();
                }
            });

// ➕ Organisation des boutons
            HBox actionsBox = new HBox(20, creerBtn);
            actionsBox.setAlignment(Pos.CENTER);

            VBox blocArbre = new VBox(15, info, actionsBox);
            blocArbre.setAlignment(Pos.CENTER);
            blocArbre.setPadding(new Insets(20));

            root.getChildren().add(blocArbre);


        } else {
            Label arbreInfo = new Label("🌳 Vous avez déjà un arbre généalogique.");
            arbreInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #228B22;");

        }

        // 🔹 Bouton profil toujours visible
        Button btnProfil = new Button("Mon profil");

        btnProfil.setOnAction(e -> {
            ProfilPage profilPage = new ProfilPage(utilisateur);
            profilPage.show();
        });

        Button btnConsulterArbre = new Button("Consulter mon arbre");
        btnConsulterArbre.setOnAction(e -> {
            if (utilisateur.getArbre() != null) {
                utilisateur.getArbre().afficherArbreGraphiqueCustom();
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Aucun arbre n'a encore été créé.").show();
            }
        });


        Button ajouterPersonneBtn = new Button("Ajouter une personne");
        ajouterPersonneBtn.setPrefWidth(150);

        ajouterPersonneBtn.setOnAction(e -> {
            new AjoutPersonnePage(utilisateur).show();
        });


        Button btnRessources = new Button("Ressources partagées");
        btnRessources.setOnAction(e -> {
                    new RPPage(utilisateur).show();
        });


        Button btnRecherche = new Button("Recherche par critère");

        btnRecherche.setOnAction(e -> {
            RecherchePage recherchePage = new RecherchePage(utilisateur);
            recherchePage.show();
        });

        root.getChildren().addAll(
                btnConsulterArbre,
                ajouterPersonneBtn,
                btnProfil,
                btnRessources,
                btnRecherche
        );



        Scene scene = new Scene(root, 400, 250);
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    // ✅ Méthode statique pour lancer l'écran avec un utilisateur
    public static void show(Utilisateur utilisateur) {
        utilisateurStatic = utilisateur;

        // Corrigé : création manuelle d'une instance si déjà dans le thread FX
        AccueilUtilisateur app = new AccueilUtilisateur();
        Stage stage = new Stage();
        try {
            app.start(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
