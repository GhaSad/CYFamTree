package view;

import dao.Database;
import dao.NoeudDAO;
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

        ComboBox<TypeLien> lienParenteCombo = new ComboBox<>();
        lienParenteCombo.getItems().addAll(TypeLien.values());
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
            Noeud nouveauNoeud = new Noeud(nouvellePersonne);

            // Ajout dans l'arbre en mémoire avec la relation
            utilisateur.ajouterNoeudAvecLien(nouveauNoeud, lien.getLibelle());

            try (Connection conn = Database.getConnection()) {
                NoeudDAO noeudDAO = new NoeudDAO(conn);

                // Sauvegarder le nouveau noeud en base avec idArbre
                noeudDAO.sauvegarderNoeud(nouveauNoeud, utilisateur.getArbre().getId());

                // Mettre à jour l'id du noeud (si auto-increment et tu peux récupérer l'id généré)
                // Sinon, il faudra faire une requête pour récupérer l'id du noeud créé

                // Récupérer noeud source (utilisateur) en base pour avoir son id_noeud
                Noeud source = utilisateur.getArbre().getNoeudParPersonne(utilisateur);

                if (source != null && nouveauNoeud.getId() != 0) {
                    // Ajouter la relation parent-enfant en base dans noeud_lien
                    String sql = "INSERT INTO noeud_lien (id_parent, id_enfant) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        int idParent = source.getId();
                        int idEnfant = nouveauNoeud.getId();
                        stmt.setInt(1, idParent);
                        stmt.setInt(2, idEnfant);
                        stmt.executeUpdate();
                    }
                } else {
                    System.out.println("Erreur : noeud source ou nouveau noeud non trouvé pour relation en base.");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de la sauvegarde en base.").show();
                return;
            }

            new Alert(Alert.AlertType.INFORMATION, "Personne ajoutée avec succès !").show();
            stage.close();
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
