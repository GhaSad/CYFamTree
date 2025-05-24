package view;

import dao.Database;
import dao.NoeudDAO;
import dao.PersonneDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import model.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.Period;

public class AjoutPersonnePage {

    private Utilisateur utilisateur;
    private Personne sourcePersonne;

    public AjoutPersonnePage(Utilisateur utilisateur, Personne sourcePersonne) {
        this.utilisateur = utilisateur;
        this.sourcePersonne = sourcePersonne;
    }
    public AjoutPersonnePage(Utilisateur utilisateur) {
    	this(utilisateur, utilisateur);
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Ajouter une personne à l’arbre");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        TextField nomField = new TextField();
        nomField.setPromptText("Nom");

        TextField prenomField = new TextField();
        prenomField.setPromptText("Prénom");

        DatePicker dateNaissancePicker = new DatePicker();

        ComboBox<TypeLien> lienParenteCombo = new ComboBox<>();
        lienParenteCombo.getItems().addAll(
        	    TypeLien.PERE,
        	    TypeLien.MERE,
        	    TypeLien.FILS,
        	    TypeLien.FILLE
        	);
        lienParenteCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(TypeLien typeLien) {
                return typeLien == null ? "" : typeLien.getLibelle();
            }

            @Override
            public TypeLien fromString(String s) {
                return null; // non utilisé
            }
        });
        lienParenteCombo.setPromptText("Sélectionnez un type de lien");

        Button validerBtn = new Button("Valider");
        validerBtn.setDefaultButton(true);

        validerBtn.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            LocalDate dateNaissance = dateNaissancePicker.getValue();
            TypeLien lien = lienParenteCombo.getValue();
            Nationalite nationalite = utilisateur.getNationalite();

            System.out.println(">>> [DEBUG] Clic sur valider : nom = " + nom + ", prenom = " + prenom + ", date = " + dateNaissance + ", lien = " + lien);

            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || lien == null) {
                System.out.println(">>> [DEBUG] Champs incomplets, annulation");
                new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.").show();
                return;
            }

            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            Personne nouvellePersonne = new Personne(nom, prenom, dateNaissance, nationalite, age);

            Noeud source = utilisateur.getArbre().getNoeudParPersonne(sourcePersonne);
            if (source == null) {
                Noeud nouveauSource = new Noeud(sourcePersonne);
                utilisateur.getArbre().ajouterNoeud(nouveauSource);
                source = nouveauSource;
            }

            try (Connection conn = Database.getConnection()) {
                System.out.println(">>> [DEBUG] Connexion DB ouverte");
                int idPersonne = PersonneDAO.sauvegarder(nouvellePersonne);
                nouvellePersonne.setId(idPersonne);
                System.out.println(">>> [DEBUG] Personne sauvegardée avec ID : " + idPersonne);

                try {
                    System.out.println(">>> [DEBUG] Tentative de création de lien logique : " + lien);
                    switch (lien) {
                    case FILS, FILLE -> source.getPersonne().creerLien(nouvellePersonne, lien);  // source est le parent
                    case PERE, MERE -> nouvellePersonne.creerLien(source.getPersonne(), lien);   // nouvellePersonne est le parent
                    default -> throw new IllegalArgumentException("Type de lien non supporté.");
                }
                    
                } catch (IllegalArgumentException ex) {
                    System.out.println(">>> [DEBUG] Erreur lors de la création du lien : " + ex.getMessage());
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                    return;
                }

                System.out.println("===== DEBUG LIENS =====");
                System.out.println("Liens de " + source.getPersonne().getPrenom() + " " + source.getPersonne().getNom() + " :");
                for (Lien l : source.getPersonne().getLiens()) {
                    System.out.println("- " + l.getTypeLien() + " : " + l.getPersonneLiee().getPrenom() + " " + l.getPersonneLiee().getNom());
                }
                System.out.println("Liens de " + nouvellePersonne.getPrenom() + " " + nouvellePersonne.getNom() + " :");
                for (Lien l : nouvellePersonne.getLiens()) {
                    System.out.println("- " + l.getTypeLien() + " : " + l.getPersonneLiee().getPrenom() + " " + l.getPersonneLiee().getNom());
                }
                System.out.println("========================");

                Noeud nouveauNoeud = new Noeud(nouvellePersonne);
                NoeudDAO noeudDAO = new NoeudDAO(conn);
                noeudDAO.sauvegarderNoeud(nouveauNoeud, utilisateur.getArbre().getId());
                System.out.println(">>> [DEBUG] Noeud sauvegardé avec ID : " + nouveauNoeud.getId());

                if (utilisateur.getArbre().getNoeudParPersonne(nouvellePersonne) == null) {
                    utilisateur.getArbre().ajouterNoeud(nouveauNoeud);
                }
                utilisateur.ajouterNoeudAvecLien(nouveauNoeud, lien);
                System.out.println(">>> [DEBUG] Noeud ajouté dans l’arbre mémoire");

                String sql = "INSERT INTO noeud_lien (id_parent, id_enfant, arbre_id) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    if (lien == TypeLien.FILS || lien == TypeLien.FILLE) {
                        stmt.setInt(1, source.getId());
                        stmt.setInt(2, nouveauNoeud.getId());
                    } else if (lien == TypeLien.PERE || lien == TypeLien.MERE) {
                        stmt.setInt(1, nouveauNoeud.getId());
                        stmt.setInt(2, source.getId());
                    }
                    stmt.setInt(3, utilisateur.getArbre().getId());
                    stmt.executeUpdate();
                    System.out.println(">>> [DEBUG] Lien noeud_lien sauvegardé en base");
                }

                new Alert(Alert.AlertType.INFORMATION, "Personne ajoutée avec succès !").show();
                utilisateur.setArbre(dao.ArbreDAO.chargerArbreParUtilisateur(utilisateur));
                stage.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la sauvegarde en base.").show();
            }
        });

        root.getChildren().addAll(
                new Label("Nom :"), nomField,
                new Label("Prénom :"), prenomField,
                new Label("Date de naissance :"), dateNaissancePicker,
                new Label("Lien de parenté :"), lienParenteCombo,
                validerBtn
        );

        stage.setScene(new Scene(root, 350, 350));
        stage.show();
    }
}
