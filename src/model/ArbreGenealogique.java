package model;

import java.util.*;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import view.AjoutPersonnePage;

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

    public Personne getRacine() {
        return racine;
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

    public Noeud getNoeudParPersonne(Personne personne) {
        for (Noeud n : listeNoeuds) {
            if (n.getPersonne().getId() == personne.getId()) { 
                return n;
            }
        }
        return null;
    }

    public Personne getVraieRacine() {
        return trouverAncetreUltime(racine);
    }


    public List<Personne> getEnfants(Personne p) {
        Noeud noeud = getNoeudParPersonne(p);
        if (noeud == null) return new ArrayList<>();
        List<Personne> enfants = new ArrayList<>();
        for (Noeud enfantNoeud : noeud.getEnfants()) {
            enfants.add(enfantNoeud.getPersonne());
        }
        return enfants;
    }

    public List<Personne> getParents(Personne p) {
        Noeud noeud = getNoeudParPersonne(p);
        if (noeud == null) return new ArrayList<>();
        List<Personne> parents = new ArrayList<>();
        for (Noeud parentNoeud : noeud.getParents()) {
            parents.add(parentNoeud.getPersonne());
        }
        return parents;
    }

   
    public Personne trouverAncetreUltime(Personne personne) {
        Set<Personne> visites = new HashSet<>();
        Personne ancetreMax = personne;
        int profondeurMax = 0;

   
        int profondeur = calculerProfondeurMaximale(personne, visites, 0);

  
        visites.clear();
        return trouverAncetreAvecProfondeurMax(personne, visites, 0).personne;
    }

    private int calculerProfondeurMaximale(Personne personne, Set<Personne> visites, int profondeurCourante) {
        if (visites.contains(personne)) {return profondeurCourante;}

        visites.add(personne);
        List<Personne> parents = getParents(personne);

        if (parents.isEmpty()) {
            return profondeurCourante;
        }

        int profondeurMax = profondeurCourante;
        for (Personne parent : parents) {
            int profondeur = calculerProfondeurMaximale(parent, new HashSet<>(visites), profondeurCourante + 1);
            profondeurMax = Math.max(profondeurMax, profondeur);
        }

        return profondeurMax;
    }

    private ResultatAncetre trouverAncetreAvecProfondeurMax(Personne personne, Set<Personne> visites, int profondeurCourante) {
        if (visites.contains(personne)) {
            return new ResultatAncetre(personne, profondeurCourante);
        }

        visites.add(personne);
        List<Personne> parents = getParents(personne);

        if (parents.isEmpty()) {
            return new ResultatAncetre(personne, profondeurCourante);
        }

        ResultatAncetre meilleurAncetre = new ResultatAncetre(personne, profondeurCourante);

        for (Personne parent : parents) {
            ResultatAncetre resultat = trouverAncetreAvecProfondeurMax(parent, new HashSet<>(visites), profondeurCourante + 1);
            if (resultat.profondeur > meilleurAncetre.profondeur) {
                meilleurAncetre = resultat;
            }
        }

        return meilleurAncetre;
    }


    private static class ResultatAncetre {
        Personne personne;
        int profondeur;

        ResultatAncetre(Personne personne, int profondeur) {
            this.personne = personne;
            this.profondeur = profondeur;
        }
    }


    private TreeItem<String> construireArbreFXDescendants(Personne personne, Set<Personne> visites) {
        if (visites.contains(personne)) {
            return new TreeItem<>("(cycle avec " + personne.getPrenom() + " " + personne.getNom() + ")");
        }

        visites.add(personne);
        TreeItem<String> item = new TreeItem<>(personne.getPrenom() + " " + personne.getNom());

        for (Personne enfant : getEnfants(personne)) {
            item.getChildren().add(construireArbreFXDescendants(enfant, new HashSet<>(visites)));
        }

        return item;
    }

  
    private TreeItem<String> construireArbreFXAncetres(Personne personne, Set<Personne> visites) {
        if (visites.contains(personne)) {
            return new TreeItem<>("(cycle avec " + personne.getPrenom() + " " + personne.getNom() + ")");
        }

        visites.add(personne);
        TreeItem<String> item = new TreeItem<>(personne.getPrenom() + " " + personne.getNom());

        for (Personne parent : getParents(personne)) {
            item.getChildren().add(construireArbreFXAncetres(parent, new HashSet<>(visites)));
        }

        return item;
    }

    public void afficherGraphiqueFX() {
    
        afficherGraphiqueFXDescendants();
    }

    public void afficherGraphiqueFXDescendants() {
        Personne racineAffichage = trouverAncetreUltime(racine);
        Set<Personne> visites = new HashSet<>();
        TreeItem<String> racineItem = construireArbreFXDescendants(racineAffichage, visites);

        TreeView<String> treeView = new TreeView<>(racineItem);
        treeView.setShowRoot(true);

        BorderPane layout = new BorderPane();
        layout.setCenter(treeView);

        Stage stage = new Stage();
        stage.setTitle("Arbre Généalogique - Descendants (JavaFX)");
        stage.setScene(new Scene(layout, 400, 500));
        stage.show();
    }

    public void afficherGraphiqueFXAncetres() {
  
        Set<Personne> visites = new HashSet<>();
        TreeItem<String> racineItem = construireArbreFXAncetres(racine, visites);

        TreeView<String> treeView = new TreeView<>(racineItem);
        treeView.setShowRoot(true);

        BorderPane layout = new BorderPane();
        layout.setCenter(treeView);

        Stage stage = new Stage();
        stage.setTitle("Arbre Généalogique - Ancêtres (JavaFX)");
        stage.setScene(new Scene(layout, 400, 500));
        stage.show();
    }

    public void afficherTexte() {
        Personne racineAffichage = trouverAncetreUltime(racine);
        Set<Personne> visites = new HashSet<>();
        System.out.println("=== Arbre généalogique (descendants) ===");
        afficherNoeudTexteDescendants(racineAffichage, 0, visites);
    }

    public void afficherTexteAncetres() {
        Set<Personne> visites = new HashSet<>();
        System.out.println("=== Arbre généalogique (ancêtres) ===");
        afficherNoeudTexteAncetres(racine, 0, visites);
    }

    private void afficherNoeudTexteDescendants(Personne p, int niveau, Set<Personne> visites) {
        if (!visites.add(p)) {
            printIndent(niveau);
            System.out.println("(cycle avec " + p.getPrenom() + " " + p.getNom() + ")");
            return;
        }

        printIndent(niveau);
        System.out.println(p.getPrenom() + " " + p.getNom());

        for (Personne enfant : getEnfants(p)) {
            afficherNoeudTexteDescendants(enfant, niveau + 1, new HashSet<>(visites));
        }
    }

    private void afficherNoeudTexteAncetres(Personne p, int niveau, Set<Personne> visites) {
        if (!visites.add(p)) {
            printIndent(niveau);
            System.out.println("(cycle avec " + p.getPrenom() + " " + p.getNom() + ")");
            return;
        }

        printIndent(niveau);
        System.out.println(p.getPrenom() + " " + p.getNom());

        for (Personne parent : getParents(p)) {
            afficherNoeudTexteAncetres(parent, niveau + 1, new HashSet<>(visites));
        }
    }

    private void printIndent(int niveau) {
        for (int i = 0; i < niveau; i++) System.out.print("  ");
    }
    public void afficherArbreCompletDepuisToutesRacines() {
        Set<Personne> visites = new HashSet<>();
        System.out.println("=== Arbre généalogique complet ===");

        for (Noeud noeud : listeNoeuds) {
            Personne personne = noeud.getPersonne();
            if (getParents(personne).isEmpty() && !visites.contains(personne)) {
                afficherDescendants(personne, 0, visites);
            }
        }
    }

    private void afficherDescendants(Personne personne, int niveau, Set<Personne> visites) {
        if (!visites.add(personne)) return;

        printIndent(niveau);
        System.out.println(personne.getPrenom() + " " + personne.getNom());

        for (Personne enfant : getEnfants(personne)) {
            afficherDescendants(enfant, niveau + 1, visites);
        }
    }

    

    public void afficherArbreGraphiqueCustom() {
   
        afficherArbreGraphiqueCustomDescendants();
    }

    public void afficherArbreGraphiqueCustomDescendants() {
        Pane pane = new Pane();
        Personne racineAffichage = trouverAncetreUltime(racine);


        Label rootLabel = creerLabelAvecCouleur(racineAffichage);
        rootLabel.setLayoutX(250);
        rootLabel.setLayoutY(50);
        pane.getChildren().add(rootLabel);

    
        List<Personne> enfants = getEnfants(racineAffichage);
        if (!enfants.isEmpty()) {
            int startX = Math.max(50, 250 - (enfants.size() * 75));

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

        
                Set<Personne> chemin = new HashSet<>();
                chemin.add(enfant);
                afficherNiveauSuivant(pane, enfant, enfantLabel, 250, 2, chemin);

            }
        }

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());

        ScrollPane scrollPane = new ScrollPane(pane);
        scrollPane.setFitToWidth(true);

        VBox layout = new VBox(15, scrollPane, btnFermer);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 800, 600);
        Stage stage = new Stage();
        stage.setTitle("Arbre généalogique - Vue graphique (Descendants)");
        stage.setScene(scene);
        stage.show();
    }

    public void afficherArbreGraphiqueCustomAncetres() {
        Pane pane = new Pane();

     
        Label rootLabel = creerLabelAvecCouleur(racine);
        rootLabel.setLayoutX(250);
        rootLabel.setLayoutY(350);
        pane.getChildren().add(rootLabel);

     
        List<Personne> parents = getParents(racine);
        if (!parents.isEmpty()) {
            int startX = Math.max(50, 250 - (parents.size() * 75));

            for (int i = 0; i < parents.size(); i++) {
                Personne parent = parents.get(i);
                Label parentLabel = creerLabelAvecCouleur(parent);
                parentLabel.setLayoutX(startX + i * 150);
                parentLabel.setLayoutY(250);

                Line line = new Line(
                        parentLabel.getLayoutX() + 30, parentLabel.getLayoutY() + 20,
                        rootLabel.getLayoutX() + 30, rootLabel.getLayoutY()
                );

                pane.getChildren().addAll(line, parentLabel);

          
                afficherNiveauPrecedent(pane, parent, parentLabel, 150, 2);
            }
        }

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(event -> ((Stage) btnFermer.getScene().getWindow()).close());

        VBox layout = new VBox(15, pane, btnFermer);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 800, 600);
        Stage stage = new Stage();
        stage.setTitle("Arbre généalogique - Vue graphique (Ancêtres)");
        stage.setScene(scene);
        stage.show();
    }

    private void afficherNiveauSuivant(Pane pane, Personne personne, Label labelParent, int yPos, int niveau, Set<Personne> cheminLocal) {
        if (niveau > 6) return; 

        List<Personne> enfants = getEnfants(personne);
        if (enfants.isEmpty()) return;

        int startX = Math.max(50, (int) labelParent.getLayoutX() - (enfants.size() * 75));

        for (int i = 0; i < enfants.size(); i++) {
            Personne enfant = enfants.get(i);

     
            if (cheminLocal.contains(enfant)) continue;

            Label enfantLabel = creerLabelAvecCouleur(enfant);
            enfantLabel.setLayoutX(startX + i * 150);
            enfantLabel.setLayoutY(yPos);

            Line line = new Line(
                labelParent.getLayoutX() + 30, labelParent.getLayoutY() + 20,
                enfantLabel.getLayoutX() + 30, enfantLabel.getLayoutY()
            );

            pane.getChildren().addAll(line, enfantLabel);

            Set<Personne> nouveauChemin = new HashSet<>(cheminLocal);
            nouveauChemin.add(enfant);
            afficherNiveauSuivant(pane, enfant, enfantLabel, yPos + 100, niveau + 1, nouveauChemin);
        }
    }

    
    public void afficherArbreGraphiqueCustomComplet(Personne centre) {
    	Pane pane = new Pane();
    	ScrollPane scroll = new ScrollPane(pane);
    	
    	scroll.setFitToWidth(true);
    	scroll.setFitToHeight(true);


        Label centreLabel = creerLabelAvecCouleur(centre);
        int centreX = 400;
        int centreY = 300;
        centreLabel.setLayoutX(centreX);
        centreLabel.setLayoutY(centreY);
        pane.getChildren().add(centreLabel);


        List<Personne> parents = getParents(centre);
        if (!parents.isEmpty()) {
            int startX = centreX - (parents.size() * 75);
            for (int i = 0; i < parents.size(); i++) {
                Personne parent = parents.get(i);
                Label parentLabel = creerLabelAvecCouleur(parent);
                parentLabel.setLayoutX(startX + i * 150);
                parentLabel.setLayoutY(centreY - 100);

                Line line = new Line(
                    parentLabel.getLayoutX() + 30, parentLabel.getLayoutY() + 40,
                    centreLabel.getLayoutX() + 30, centreLabel.getLayoutY()
                );

                pane.getChildren().addAll(line, parentLabel);

                Set<Personne> cheminParent = new HashSet<>();
                cheminParent.add(parent);
                int yParent = (int) parentLabel.getLayoutY() - 100;
                afficherNiveauPrecedent(pane, parent, parentLabel, yParent, 2);
            }
        }

 
        List<Personne> enfants = getEnfants(centre);
        if (!enfants.isEmpty()) {
            int startX = centreX - (enfants.size() * 75);
            for (int i = 0; i < enfants.size(); i++) {
                Personne enfant = enfants.get(i);
                Label enfantLabel = creerLabelAvecCouleur(enfant);
                enfantLabel.setLayoutX(startX + i * 150);
                enfantLabel.setLayoutY(centreY + 100);

                Line line = new Line(
                    centreLabel.getLayoutX() + 30, centreLabel.getLayoutY() + 40,
                    enfantLabel.getLayoutX() + 30, enfantLabel.getLayoutY()
                );

                pane.getChildren().addAll(line, enfantLabel);

                Set<Personne> chemin = new HashSet<>();
                chemin.add(enfant);
                int ySuivant = (int) enfantLabel.getLayoutY() + 100;
                afficherNiveauSuivant(pane, enfant, enfantLabel, ySuivant, 2, chemin);
            }
        }

        Button btnFermer = new Button("Fermer");
        btnFermer.setOnAction(e -> ((Stage) btnFermer.getScene().getWindow()).close());


        VBox layout = new VBox(15, scroll, btnFermer);
        layout.setPadding(new Insets(15));
        layout.setAlignment(Pos.TOP_CENTER);

        Scene scene = new Scene(layout, 1000, 800);
        Stage stage = new Stage();
        stage.setTitle("Arbre Généalogique - Vue complète");
        stage.setScene(scene);
        stage.show();
    }

    private void afficherNiveauPrecedent(Pane pane, Personne personne, Label labelEnfant, int yPos, int niveau) {
        if (niveau > 4 || yPos < 50) return;

        List<Personne> parents = getParents(personne);
        if (parents.isEmpty()) return;

        int startX = Math.max(50, (int)labelEnfant.getLayoutX() - (parents.size() * 75));

        for (int i = 0; i < parents.size(); i++) {
            Personne parent = parents.get(i);
            Label parentLabel = creerLabelAvecCouleur(parent);
            parentLabel.setLayoutX(startX + i * 150);
            parentLabel.setLayoutY(yPos);

            Line line = new Line(
                    parentLabel.getLayoutX() + 30, parentLabel.getLayoutY() + 20,
                    labelEnfant.getLayoutX() + 30, labelEnfant.getLayoutY()
            );

            pane.getChildren().addAll(line, parentLabel);

            afficherNiveauPrecedent(pane, parent, parentLabel, yPos - 100, niveau + 1);
        }
    }

    private Label creerLabelAvecCouleur(Personne personne) {
        final Label label = new Label(personne.getPrenom() + " " + personne.getNom());

        String couleurTexte = (personne instanceof Utilisateur) ? "green" : "red";

        label.setStyle(
                "-fx-border-color: black;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5;" +
                        "-fx-background-color: white;" +
                        "-fx-text-fill: " + couleurTexte + ";"
        );

        label.setOnMouseClicked(e -> {
            Stage popup = new Stage();
            popup.setTitle("Modifier ou supprimer la personne");

            VBox content = new VBox(10);
            content.setPadding(new Insets(15));
            content.setAlignment(Pos.CENTER_LEFT);

            TextField nomField = new TextField(personne.getNom());
            TextField prenomField = new TextField(personne.getPrenom());
            DatePicker dateNaissancePicker = new DatePicker(personne.getDateNaissance());
            ComboBox<Nationalite> nationaliteCombo = new ComboBox<>();
            nationaliteCombo.getItems().addAll(Nationalite.values());
            nationaliteCombo.setValue(personne.getNationalite());

            Button btnModifier = new Button("Modifier");
            Button btnSupprimer = new Button("Supprimer");
            Button btnAjouterPersonne = new Button("Ajouter parent/enfant");
            Button btnFermer = new Button("Fermer");

            btnModifier.setOnAction(ev -> {
                personne.setNom(nomField.getText());
                personne.setPrenom(prenomField.getText());
                personne.setDateNaissance(dateNaissancePicker.getValue());
                personne.setNationalite(nationaliteCombo.getValue());

                try {
                    dao.PersonneDAO.mettreAJour(personne);
                    popup.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "✅ Modifications enregistrées !");
                    alert.show();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "❌ Erreur lors de la modification.").show();
                }
            });

            btnSupprimer.setOnAction(ev -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Supprimer cette personne ?", ButtonType.YES, ButtonType.NO);
                confirm.showAndWait().ifPresent(type -> {
                    if (type == ButtonType.YES) {
                        try {
                            dao.PersonneDAO.supprimer(personne);
                            popup.close();
                            Alert alert = new Alert(Alert.AlertType.INFORMATION, "❌ Personne supprimée.");
                            alert.show();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            new Alert(Alert.AlertType.ERROR, "❌ Erreur lors de la suppression.").show();
                        }
                    }
                });
            });
            btnAjouterPersonne.setOnAction(ev -> {
                AjoutPersonnePage page = new AjoutPersonnePage(utilisateur, personne);
                page.show();
            });
            

            content.getChildren().addAll(
                    new Label("Nom :"), nomField,
                    new Label("Prénom :"), prenomField,
                    new Label("Date de naissance :"), dateNaissancePicker,
                    new Label("Nationalité :"), nationaliteCombo,
                    new HBox(10, btnModifier, btnSupprimer, btnFermer),
                    new Label("Ajouter un parent ou un enfant :"),
                    btnAjouterPersonne
            );

            btnFermer.setOnAction(ev -> popup.close());

            popup.setScene(new Scene(content, 350, 300));
            popup.show();
        });


        return label;
    }
}