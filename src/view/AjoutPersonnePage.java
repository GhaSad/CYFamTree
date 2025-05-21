package view;

import dao.Database;
import dao.NoeudDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.Period;

public class AjoutPersonnePage {

    private Utilisateur utilisateur;

    public AjoutPersonnePage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
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

        TextField lienParenteField = new TextField();
        lienParenteField.setPromptText("Lien de parenté (père, mère, frère...)");

        Button validerBtn = new Button("Valider");
        validerBtn.setDefaultButton(true);

        validerBtn.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            LocalDate dateNaissance = dateNaissancePicker.getValue();
            String lien = lienParenteField.getText().trim();
            Nationalite nationalite = utilisateur.getNationalite();

            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || lien.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.").show();
                return;
            }

            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            Personne nouvellePersonne = new Personne(nom, prenom, dateNaissance, nationalite, age);
            Noeud nouveauNoeud = new Noeud(nouvellePersonne);

            // Ajout logique dans l’arbre
            utilisateur.ajouterNoeudAvecLien(nouveauNoeud, lien);

            try {
                Connection conn = Database.getConnection();
                NoeudDAO noeudDAO = new NoeudDAO(conn);
                noeudDAO.sauvegarderNoeud(nouveauNoeud, utilisateur.getArbre().getId());
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            new Alert(Alert.AlertType.INFORMATION, "Personne ajoutée avec succès !").show();
            stage.close();
        });

        root.getChildren().addAll(
                new Label("Nom :"), nomField,
                new Label("Prénom :"), prenomField,
                new Label("Date de naissance :"), dateNaissancePicker,
                new Label("Lien de parenté :"), lienParenteField,
                validerBtn
        );

        stage.setScene(new Scene(root, 350, 350));
        stage.show();
    }
}
