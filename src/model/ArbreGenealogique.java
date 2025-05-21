package model;

import java.util.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class ArbreGenealogique {
    private Utilisateur utilisateur;
    private Personne racine;
    private List<Noeud> listeNoeuds;
    private int id;

    public ArbreGenealogique(Utilisateur utilisateur, Personne racine) {
        this.utilisateur = utilisateur;
        this.racine = racine;
        this.listeNoeuds = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void ajouterNoeud(Noeud noeud) {
        listeNoeuds.add(noeud);
    }

    public void supprimerNoeud(Noeud noeud) {
        listeNoeuds.remove(noeud);
    }

    public Noeud rechercherNoeud(String nom) {
        for (Noeud n : listeNoeuds) {
            if (n.getPersonne().getNom().equalsIgnoreCase(nom)) {
                return n;
            }
        }
        return null;
    }

    public Personne getRacine() {
        return racine;
    }

    public List<Personne> getEnfants(Personne p) {
        Noeud noeud = null;
        for (Noeud n : listeNoeuds) {
            if (n.getPersonne().equals(p)) {
                noeud = n;
                break;
            }
        }
        if (noeud == null) {
            return new ArrayList<>();
        }
        List<Personne> enfants = new ArrayList<>();
        for (Noeud enfantNoeud : noeud.getEnfants()) {
            enfants.add(enfantNoeud.getPersonne());
        }
        return enfants;
    }

    // Affichage texte console avec détection de cycles
    public void afficherTexte() {
        Set<Personne> visites = new HashSet<>();
        afficherNoeud(racine, 0, visites);
    }

    public Noeud getNoeudParPersonne(Personne personne) {
        for (Noeud n : listeNoeuds) {
            if (n.getPersonne().equals(personne)) {
                return n;
            }
        }
        return null;
    }


    private void afficherNoeud(Personne p, int niveau, Set<Personne> visites) {
        if (visites.contains(p)) {
            for (int i = 0; i < niveau; i++) System.out.print("  ");
            System.out.println("(cycle détecté avec " + p.getPrenom() + " " + p.getNom() + ")");
            return;
        }
        visites.add(p);

        for (int i = 0; i < niveau; i++) System.out.print("  ");
        System.out.println(p.getPrenom() + " " + p.getNom());

        List<Personne> enfants = getEnfants(p);
        for (Personne enfant : enfants) {
            afficherNoeud(enfant, niveau + 1, visites);
        }
    }

    public void afficherGraphiqueFX() {
        if (racine == null) {
            System.out.println("⚠️ Aucun racine définie pour cet arbre.");
            return;
        }

        Set<Personne> visites = new HashSet<>();
        TreeItem<String> racineItem = construireArbreFX(racine, visites);

        TreeView<String> treeView = new TreeView<>(racineItem);
        treeView.setShowRoot(true);

        BorderPane layout = new BorderPane();
        layout.setCenter(treeView);

        Stage stage = new Stage();
        stage.setTitle("Arbre Généalogique (JavaFX)");
        stage.setScene(new Scene(layout, 400, 500));
        stage.show();
    }

    private TreeItem<String> construireArbreFX(Personne personne, Set<Personne> visites) {
        if (visites.contains(personne)) {
            return new TreeItem<>("(cycle avec " + personne.getPrenom() + " " + personne.getNom() + ")");
        }

        visites.add(personne);
        TreeItem<String> item = new TreeItem<>(personne.getPrenom() + " " + personne.getNom());

        List<Personne> enfants = getEnfants(personne);
        for (Personne enfant : enfants) {
            item.getChildren().add(construireArbreFX(enfant, visites));
        }

        return item;
    }

    private Label creerLabelAvecCouleur(Personne personne) {
        Label label = new Label(personne.getPrenom() + " " + personne.getNom());

        String couleurTexte = (personne instanceof Utilisateur) ? "green" : "red";

        label.setStyle(
                "-fx-border-color: black;" +         // ✅ bordure noire
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5;" +
                        "-fx-background-color: white;" +     // optionnel : fond blanc
                        "-fx-text-fill: " + couleurTexte + ";" // texte coloré
        );

        label.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Détails de la personne");
            alert.setHeaderText(null);
            alert.setContentText(
                    "Nom : " + personne.getNom() + "\n" +
                            "Prénom : " + personne.getPrenom() + "\n" +
                            "Date de naissance : " + personne.getDateNaissance() + "\n" +
                            "Nationalité : " + personne.getNationalite()
            );
            alert.showAndWait();
        });

        return label;
    }


    public void afficherArbreGraphiqueCustom() {
        Pane pane = new Pane();

        // Label racine
        Label rootLabel = creerLabelAvecCouleur(racine);
        rootLabel.setLayoutX(200);
        rootLabel.setLayoutY(50);
        pane.getChildren().add(rootLabel);

        List<Personne> enfants = getEnfants(racine);
        int startX = 100;

        for (int i = 0; i < enfants.size(); i++) {
            Personne enfant = enfants.get(i);
            Label enfantLabel = creerLabelAvecCouleur(enfant);
            enfantLabel.setLayoutX(startX + i * 150);
            enfantLabel.setLayoutY(150);

            Line line = new Line(
                    rootLabel.getLayoutX() + 30, rootLabel.getLayoutY() + 20,
                    enfantLabel.getLayoutX() + 30, enfantLabel.getLayoutY()
            );

            pane.getChildren().addAll(line, enfantLabel);
        }

        // ➕ Bouton retour en bas
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> ((Stage) btnFermer.getScene().getWindow()).close());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getChildren().addAll(pane, btnFermer);

        Scene scene = new Scene(layout, 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Arbre généalogique - Vue graphique");
        stage.setScene(scene);
        stage.show();
    }

}