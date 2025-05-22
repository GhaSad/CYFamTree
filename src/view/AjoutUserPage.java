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

        TextField loginField = new TextField();
        loginField.setPromptText("Login de l'utilisateur");

        TextField secuField = new TextField();
        secuField.setPromptText("Numéro de sécurité sociale");

        ComboBox<TypeLien> lienCombo = new ComboBox<>();
        lienCombo.getItems().addAll(TypeLien.values());
        lienCombo.setPromptText("Type de lien");

        Button rechercherBtn = new Button("Rechercher et ajouter");

        rechercherBtn.setOnAction(e -> {
            String login = loginField.getText().trim();
            String secu = secuField.getText().trim();
            TypeLien typeLien = lienCombo.getValue();

            if ((login.isEmpty() && secu.isEmpty()) || typeLien == null) {
                new Alert(Alert.AlertType.WARNING, "Veuillez remplir au moins un champ de recherche et choisir un lien.").show();
                return;
            }

            UtilisateurDAO dao = new UtilisateurDAO();
            Optional<Utilisateur> cibleOpt = dao.trouverParLoginOuSecu(login, secu);

            if (cibleOpt.isEmpty()) {
                new Alert(Alert.AlertType.INFORMATION, "Aucun utilisateur trouvé.").show();
                return;
            }

            Utilisateur cible = cibleOpt.get();

            if (cible.getId() == utilisateurCourant.getId()) {
                new Alert(Alert.AlertType.WARNING, "Vous ne pouvez pas vous ajouter vous-même.").show();
                return;
            }

            Lien lien = new Lien(utilisateurCourant, cible, typeLien);
            if (!lien.estValide()) {
                new Alert(Alert.AlertType.ERROR, "Le lien proposé n'est pas valide.").show();
                return;
            }

            // Envoi de l'e-mail
            EmailService.envoyerEmail(
                    cible.getEmail(),
                    "CYFamTree - Demande de lien de parenté",
                    "Bonjour " + cible.getPrenom() + ",\n\n" +
                            utilisateurCourant.getPrenom() + " souhaite vous ajouter comme " + typeLien.getLibelle().toLowerCase() +
                            " dans son arbre généalogique.\nVeuillez vous connecter pour accepter ou refuser cette demande."
            );

            new Alert(Alert.AlertType.INFORMATION, "Demande envoyée à l'utilisateur. En attente de confirmation.").show();
            stage.close();
        });

        root.getChildren().addAll(
                new Label("Rechercher un utilisateur par :"),
                loginField,
                secuField,
                new Label("Lien de parenté :"),
                lienCombo,
                rechercherBtn
        );

        stage.setScene(new Scene(root, 400, 350));
        stage.show();
    }
}
