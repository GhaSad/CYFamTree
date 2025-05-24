package view;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.StageStyle;

/** Boîte de dialogue pour l'authentification administrateur. */
public class AdminLoginDialog {

    private static final String ADMIN_PASSWORD = "admin123"; // Mot de passe (à sécuriser en prod)

    public static boolean showLoginDialog() {
        // Création du dialogue de mot de passe
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Mot de passe admin");
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setHeaderText(null);
        dialog.setResizable(false);

        // Boutons OK et Annuler
        ButtonType buttonOk = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonCancel = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonOk, buttonCancel);

        // Champ mot de passe
        PasswordField pwdField = new PasswordField();

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Entrez le mot de passe :"), 0, 0);
        grid.add(pwdField, 1, 0);

        dialog.getDialogPane().setContent(grid);

        // Focus sur le champ mot de passe
        pwdField.requestFocus();

        // Convertir la réponse en String (le mot de passe)
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonOk) {
                return pwdField.getText();
            }
            return null;
        });

        // Afficher le dialogue et attendre la réponse
        String result = dialog.showAndWait().orElse(null);

        if (result != null) {
            if (ADMIN_PASSWORD.equals(result)) {
                return true;
            } else {
                // Afficher une alerte erreur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Mot de passe incorrect");
                alert.showAndWait();
                return false;
            }
        }
        return false; // Annulé ou fermé
    }
}
