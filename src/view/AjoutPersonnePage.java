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

            try (Connection conn = Database.getConnection()) {
                int idPersonne = PersonneDAO.sauvegarder(nouvellePersonne);
                nouvellePersonne.setId(idPersonne);
                System.out.println("🧠 Nouvelle personne enregistrée avec ID : " + idPersonne);

                System.out.println("🧠 Création noeud avec id_personne = " + nouvellePersonne.getId());
                Noeud nouveauNoeud = new Noeud(nouvellePersonne);
                NoeudDAO noeudDAO = new NoeudDAO(conn);
                noeudDAO.sauvegarderNoeud(nouveauNoeud, utilisateur.getArbre().getId());
                System.out.println("✅ Noeud enregistré avec ID : " + nouveauNoeud.getId());

                // Ajout logique dans l’arbre en mémoire
                utilisateur.ajouterNoeudAvecLien(nouveauNoeud, lien);
                utilisateur.getArbre().ajouterNoeud(nouveauNoeud);


                // 🔁 Enregistrement du lien dans noeud_lien (arbre_id + id_parent + id_enfant)
                Personne vraiePersonne = PersonneDAO.trouverParUtilisateurId(utilisateur.getId());
                Noeud source = utilisateur.getArbre().getNoeudParPersonne(vraiePersonne);
                if (source != null) {
                    String sql = "INSERT INTO noeud_lien (id_parent, id_enfant, arbre_id) VALUES (?, ?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        if (lien == TypeLien.FILS || lien == TypeLien.FILLE) {
                            // Utilisateur est parent
                            stmt.setInt(1, source.getId());
                            stmt.setInt(2, nouveauNoeud.getId());
                        } else if (lien == TypeLien.PERE || lien == TypeLien.MERE) {
                            // Utilisateur est enfant
                            stmt.setInt(1, nouveauNoeud.getId());
                            stmt.setInt(2, source.getId());
                        } else {
                            // Autres types (frère/soeur...) — relation non hiérarchique → on ne stocke pas pour l'instant
                            stmt.close();
                            new Alert(Alert.AlertType.INFORMATION, "Lien de type " + lien.getLibelle() + " enregistré uniquement en mémoire.").show();
                            stage.close();
                            return;
                        }
                        stmt.setInt(3, utilisateur.getArbre().getId());
                        stmt.executeUpdate();
                    }
                } else {
                    System.out.println("❌ Erreur : Noeud racine (source) non trouvé.");
                }

                new Alert(Alert.AlertType.INFORMATION, "Personne ajoutée avec succès !").show();
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
