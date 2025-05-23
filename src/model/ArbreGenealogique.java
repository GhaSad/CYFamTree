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
            if (n.getPersonne() != null && n.getPersonne().equals(personne)) {
                return n;
            }
        }
        return null;
    }

    public Personne getVraieRacine() {
        for (Noeud noeud : listeNoeuds) {
            if (noeud.getParents().isEmpty()) {
                return noeud.getPersonne();
            }
        }
        return racine; // fallback
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
        Personne racineLogique = getVraieRacine(); // 🔁 racine du point de vue généalogique

        Pane pane = new Pane();
        Map<Personne, Label> labels = new HashMap<>();

        // Position initiale
        double startX = 350;
        double startY = 50;

        Set<Personne> visites = new HashSet<>();

        dessinerRecursivement(pane, racineLogique, startX, startY, labels, visites);

        // ➕ Bouton retour
        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> ((Stage) btnFermer.getScene().getWindow()).close());

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.getChildren().addAll(pane, btnFermer);

        Scene scene = new Scene(layout, 1000, 700);
        Stage stage = new Stage();
        stage.setTitle("Arbre généalogique - Vue graphique");
        stage.setScene(scene);
        stage.show();
    }
    private void dessinerRecursivement(Pane pane, Personne personne, double x, double y, Map<Personne, Label> labels, Set<Personne> visites) {
        if (visites.contains(personne)) return;
        visites.add(personne);

        Label label = creerLabelAvecCouleur(personne);
        label.setLayoutX(x);
        label.setLayoutY(y);
        pane.getChildren().add(label);
        labels.put(personne, label);

        List<Personne> enfants = getEnfants(personne);
        double offsetX = -((enfants.size() - 1) * 150) / 2.0;

        for (int i = 0; i < enfants.size(); i++) {
            Personne enfant = enfants.get(i);
            double childX = x + offsetX + i * 150;
            double childY = y + 100;

            dessinerRecursivement(pane, enfant, childX, childY, labels, visites);

            Label enfantLabel = labels.get(enfant);
            if (enfantLabel != null) {
                Line line = new Line(
                        x + 40, y + 30,
                        enfantLabel.getLayoutX() + 40, enfantLabel.getLayoutY()
                );
                pane.getChildren().add(line);
            }
        }
    }




}