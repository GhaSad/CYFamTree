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
import utils.ValidationResult;

/** Interface permettant d'ajouter une personne manuellement dans l'arbre généalogique. */
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


            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || lien == null) {

                new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.").show();
                return;
            }

            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            Personne nouvellePersonne = new Personne(nom, prenom, dateNaissance, nationalite, age);

            Noeud source = utilisateur.getArbre().getNoeudParPersonne(sourcePersonne);
            if (source == null) {
                Noeud nouveauSource = new Noeud(sourcePersonne);
                try (Connection connTemp = Database.getConnection()) {
                    NoeudDAO noeudDAOtemp = new NoeudDAO(connTemp);
                    noeudDAOtemp.sauvegarderNoeud(nouveauSource, utilisateur.getArbre().getId());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "❌ Erreur lors de la sauvegarde du noeud source.").show();
                    return;
                }
                utilisateur.getArbre().ajouterNoeud(nouveauSource);
                source = nouveauSource;
            }


            try (Connection conn = Database.getConnection()) {
                int idPersonne = PersonneDAO.sauvegarder(nouvellePersonne);
                nouvellePersonne.setId(idPersonne);


                try {
                	Lien lienCree;

                	
                	switch (lien) {
                	    case FILS, FILLE -> lienCree = new Lien(source.getPersonne(), nouvellePersonne, lien);  // source = parent
                	    case PERE, MERE -> lienCree = new Lien(nouvellePersonne, source.getPersonne(), lien);   // source = enfant
                	    default -> throw new IllegalArgumentException("Type de lien non supporté.");
                	}

                	// Valider le lien avec l'arbre
                	utils.ValidationResult res = lienCree.estValideAvancee(utilisateur.getArbre());
                	if (!res.isValide()) {
                	    new Alert(Alert.AlertType.ERROR, res.getMessage()).show();
                	    return;
                	}
                	// ➕ AJOUTE CE QUI SUIT :
                	dao.LienDAO.sauvegarder(lienCree, conn);

                    
                } catch (IllegalArgumentException ex) {
                    new Alert(Alert.AlertType.ERROR, ex.getMessage()).show();
                    return;
                }

                Noeud nouveauNoeud = new Noeud(nouvellePersonne);
                NoeudDAO noeudDAO = new NoeudDAO(conn);
                noeudDAO.sauvegarderNoeud(nouveauNoeud, utilisateur.getArbre().getId());

                // Ajout dans l'arbre
                if (utilisateur.getArbre().getNoeudParPersonne(nouvellePersonne) == null) {
                    utilisateur.getArbre().ajouterNoeud(nouveauNoeud);
                }

                // Crée la relation logique (en mémoire)
                if (lien == TypeLien.FILS || lien == TypeLien.FILLE) {
                    source.ajouterEnfant(nouveauNoeud);
                } else if (lien == TypeLien.PERE || lien == TypeLien.MERE) {
                    nouveauNoeud.ajouterEnfant(source);
                }

                // Enregistre la relation en base via NoeudDAO
                noeudDAO.enregistrerLienParentEnfant(
                    (lien == TypeLien.FILS || lien == TypeLien.FILLE) ? source : nouveauNoeud,
                    (lien == TypeLien.FILS || lien == TypeLien.FILLE) ? nouveauNoeud : source,
                    utilisateur.getArbre().getId()
                );


                

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
