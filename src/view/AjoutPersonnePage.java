package view;

import dao.*;
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

    private final Utilisateur utilisateur;

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
                return null;
            }
        });
        lienParenteCombo.setPromptText("Sélectionnez un type de lien");

        Button validerBtn = new Button("Valider");
        validerBtn.setDefaultButton(true);

        validerBtn.setOnAction(e -> {
            String nom = nomField.getText().trim();
            String prenom = prenomField.getText().trim();
            LocalDate dateNaissance = dateNaissancePicker.getValue();
            TypeLien typeLien = lienParenteCombo.getValue();
            Nationalite nationalite = utilisateur.getNationalite();

            if (nom.isEmpty() || prenom.isEmpty() || dateNaissance == null || typeLien == null) {
                new Alert(Alert.AlertType.WARNING, "Tous les champs doivent être remplis.").show();
                return;
            }

            int age = Period.between(dateNaissance, LocalDate.now()).getYears();
            Personne nouvellePersonne = new Personne(nom, prenom, dateNaissance, nationalite, age);
            Noeud nouveauNoeud = new Noeud(nouvellePersonne);

            try (Connection conn = Database.getConnection()) {
                conn.setAutoCommit(false); // ✅ une seule transaction globale

                // 1. Charger l’arbre de l’utilisateur
                ArbreGenealogique arbreMisAJour = ArbreDAO.chargerArbreParUtilisateur(utilisateur, conn);
                if (arbreMisAJour == null) {
                    new Alert(Alert.AlertType.ERROR, "❌ Erreur : Impossible de charger l’arbre de l’utilisateur.").show();
                    return;
                }
                utilisateur.setArbre(arbreMisAJour);
                // 2. Sauvegarder la personne et le nœud
                PersonneDAO.sauvegarder(nouvellePersonne, conn);
                NoeudDAO noeudDAO = new NoeudDAO(conn);
                noeudDAO.sauvegarderNoeud(nouveauNoeud, utilisateur.getArbre().getId());

                // 3. Lien avec le noeud de l’utilisateur
                Noeud source = utilisateur.getArbre().getNoeudParPersonne(utilisateur);
                if (source == null) throw new IllegalStateException("Noeud source introuvable.");

                int idParent, idEnfant;
                switch (typeLien) {
                case PERE, MERE, GRAND_PERE, GRAND_MERE, TANTE, ONCLE -> {
                    idParent = nouveauNoeud.getId();
                    idEnfant = source.getId();
                    source.ajouterParent(nouveauNoeud);
                    utilisateur.getArbre().ajouterNoeud(nouveauNoeud); // ✅ AJOUT
                }
                default -> {
                    idParent = source.getId();
                    idEnfant = nouveauNoeud.getId();
                    source.ajouterEnfant(nouveauNoeud);
                    utilisateur.getArbre().ajouterNoeud(nouveauNoeud); // ✅ AJOUT
                }
            }

                String sqlLien = "INSERT INTO noeud_lien (id_parent, id_enfant) VALUES (?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlLien)) {
                    stmt.setInt(1, idParent);
                    stmt.setInt(2, idEnfant);
                    stmt.executeUpdate();
                }

                conn.commit(); // ✅ fin de transaction
                utilisateur.setArbre(ArbreDAO.chargerArbreParUtilisateur(utilisateur, conn)); // 🔄 mise à jour mémoire

                new Alert(Alert.AlertType.INFORMATION, "Personne ajoutée avec succès !").show();
                stage.close();

            } catch (Exception ex) {
                ex.printStackTrace();
                new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ajout.").show();
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
