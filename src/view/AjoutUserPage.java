package view;

import dao.UtilisateurDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Lien;
import model.TypeLien;
import model.Utilisateur;
import utils.EmailService;

import java.util.Optional;

public class AjoutUserPage {

    private Utilisateur utilisateurCourant;

    public AjoutUserPage(Utilisateur utilisateurCourant) {
        this.utilisateurCourant = utilisateurCourant;
    }

    public void show() {
        Stage stage = new Stage();
        stage.setTitle("Ajouter un utilisateur existant à mon arbre");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        TextField codePublicField = new TextField();
        codePublicField.setPromptText("Code public (ex: CY123456)");

        ComboBox<TypeLien> lienCombo = new ComboBox<>();
        lienCombo.getItems().addAll(TypeLien.values());
        lienCombo.setPromptText("Type de lien");

        Label infoTrouvee = new Label();
        Button ajouterBtn = new Button("Ajouter à l’arbre");
        ajouterBtn.setDisable(true);

        Utilisateur[] cibleTrouvee = new Utilisateur[1]; // hack pour accès dans lambda

        Button rechercherBtn = new Button("Rechercher");
        rechercherBtn.setOnAction(e -> {
            String code = codePublicField.getText().trim().toUpperCase();
            if (code.isEmpty()) {
                new Alert(Alert.AlertType.WARNING, "Veuillez saisir un code public.").show();
                return;
            }

            UtilisateurDAO dao = new UtilisateurDAO();
            Optional<Utilisateur> opt = dao.trouverParCodePublic(code);

            if (opt.isEmpty()) {
                infoTrouvee.setText("❌ Aucun utilisateur trouvé.");
                ajouterBtn.setDisable(true);
                return;
            }

            Utilisateur cible = opt.get();
            if (cible.getId() == utilisateurCourant.getId()) {
                infoTrouvee.setText("⚠️ Vous ne pouvez pas vous ajouter vous-même.");
                ajouterBtn.setDisable(true);
                return;
            }

            cibleTrouvee[0] = cible;
            infoTrouvee.setText("👤 " + cible.getPrenom() + " " + cible.getNom() + " trouvé.");
            ajouterBtn.setDisable(false);
        });

        ajouterBtn.setOnAction(e -> {
            Utilisateur cible = cibleTrouvee[0];
            TypeLien typeLien = lienCombo.getValue();

            if (typeLien == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez choisir un type de lien.").show();
                return;
            }

            Lien lien = new Lien(utilisateurCourant, cible, typeLien);
            if (!lien.estValide()) {
                new Alert(Alert.AlertType.ERROR, "Le lien proposé n'est pas valide.").show();
                return;
            }

            EmailService.envoyerEmail(
                    cible.getEmail(),
                    "CYFamTree - Demande de lien de parenté",
                    "Bonjour " + cible.getPrenom() + ",\n\n" +
                            utilisateurCourant.getPrenom() + " souhaite vous ajouter comme " + typeLien.getLibelle().toLowerCase() +
                            " dans son arbre généalogique.\nVeuillez vous connecter pour accepter ou refuser cette demande."
            );

            new Alert(Alert.AlertType.INFORMATION, "✅ Demande envoyée à " + cible.getPrenom()).show();
            stage.close();
        });

        root.getChildren().addAll(
                new Label("Saisissez le code public de l'utilisateur à ajouter :"),
                codePublicField,
                rechercherBtn,
                infoTrouvee,
                lienCombo,
                ajouterBtn
        );

        stage.setScene(new Scene(root, 420, 350));
        stage.show();
    }
}
