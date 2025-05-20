package view;

import dao.*;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;

import javafx.scene.control.ComboBox;


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
            Label info = new Label("Vous n'avez pas encore d'arbre généalogique.");
            Button creerBtn = new Button("Créer mon arbre");

            creerBtn.setOnAction(e -> {
                try {
                    if (utilisateur.getArbre() != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Vous avez déjà un arbre.");
                        alert.show();
                        return;
                    }

                    Personne personneRacine = utilisateur;

                    // ⚠️ NE PAS sauvegarder à nouveau la personne : elle est déjà dans la table avec id = utilisateur.id

                    // 3. Création du noeud racine
                    Noeud racine = new Noeud(personneRacine);

                    Connection conn = Database.getConnection();
                    NoeudDAO noeudDAO = new NoeudDAO(conn);
                    noeudDAO.sauvegarderNoeud(racine, -1); // insertion du noeud (arbre_id temporaire)

                    // 5. Création de l'arbre
                    ArbreGenealogique arbre1 = new ArbreGenealogique(utilisateur, personneRacine);
                    arbre1.ajouterNoeud(racine);
                    utilisateur.setArbre(arbre1);

                    // 6. Enregistrement de l'arbre
                    int idArbre = ArbreDAO.creerArbre(arbre1);
                    noeudDAO.ajouterArbreIdAuNoeud(racine, idArbre);

                    // 8. Affichage
                    primaryStage.close();
                    arbre1.afficherArbreGraphiqueCustom();

                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de la création de l'arbre.").show();
                }
            });



            // Nouveau bouton pour ajouter une personne
            Button ajouterPersonneBtn = new Button("Ajouter une personne");

            ajouterPersonneBtn.setOnAction(e -> {
                Stage formulaireStage = new Stage();
                formulaireStage.setTitle("Ajouter une personne");

                VBox form = new VBox(10);
                form.setPadding(new Insets(10));

                TextField nomField = new TextField();
                nomField.setPromptText("Nom");

                TextField prenomField = new TextField();
                prenomField.setPromptText("Prénom");

                DatePicker dateNaissancePicker = new DatePicker();

                TextField lienParenteField = new TextField();
                lienParenteField.setPromptText("Lien de parenté (ex : père, mère, frère...)");

                Button validerBtn = new Button("Valider");

                validerBtn.setOnAction(ev -> {
                    String nom = nomField.getText();
                    String prenom = prenomField.getText();
                    LocalDate dateNaissance = dateNaissancePicker.getValue();
                    String lien = lienParenteField.getText();
                    Nationalite nationalite = utilisateur.getNationalite();
                    int age = Period.between(dateNaissance, LocalDate.now()).getYears();

                    if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || lien.isEmpty() || nationalite == null) {
                        Alert alert = new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.");
                        alert.show();
                        return;
                    }

                    // Création de la nouvelle personne
                    Personne nouvellePersonne = new Personne(nom, prenom, dateNaissance, nationalite, age);
                    Noeud nouveauNoeud = new Noeud(nouvellePersonne);

                    utilisateur.ajouterNoeudAvecLien(nouveauNoeud, lien); // méthode à définir

                    formulaireStage.close();
                });

                form.getChildren().addAll(
                        new Label("Nom :"), nomField,
                        new Label("Prénom :"), prenomField,
                        new Label("Date de naissance :"), dateNaissancePicker,
                        new Label("Lien de parenté :"), lienParenteField,
                        validerBtn
                );

                Scene scene = new Scene(form, 300, 300);
                formulaireStage.setScene(scene);
                formulaireStage.show();
            });

            root.getChildren().addAll(info, creerBtn, ajouterPersonneBtn);

        } else {
            Label info = new Label("Chargement de votre arbre généalogique...");
            root.getChildren().add(info);
            primaryStage.close();
            utilisateur.getArbre().afficherArbreGraphiqueCustom();  // À adapter si besoin
            return;
        }

        // 🔹 Bouton profil toujours visible
        Button btnProfil = new Button("Mon profil");

        btnProfil.setOnAction(e -> {
            ProfilPage profilPage = new ProfilPage(utilisateur);
            profilPage.show();
        });

        root.getChildren().add(btnProfil);


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
