package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Interface d'accueil principale de l'application. */
public class AccueilPage {

    private Stage stage;

    public AccueilPage() {
        stage = new Stage();
        stage.setTitle("Bienvenue sur CYFamTree");

        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setAlignment(Pos.CENTER);

        Label titre = new Label("Bienvenue sur CYFamTree 👨‍👩‍👧‍👦");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        Label sousTitre = new Label("Créez, explorez et partagez votre arbre généalogique.");
        sousTitre.setStyle("-fx-font-size: 14px; -fx-text-fill: #444;");

  
        Button btnInscription = new Button("S'inscrire");
        Button btnConnexion = new Button("Se connecter");
        Button btnAdmin = new Button("Administration");

        btnInscription.setPrefWidth(200);
        btnConnexion.setPrefWidth(200);
        btnAdmin.setPrefWidth(200);

        VBox boutonBox = new VBox(15, btnInscription, btnConnexion, btnAdmin);
        boutonBox.setAlignment(Pos.CENTER);

        btnInscription.setOnAction(e -> {
            stage.close();
            InscriptionPage inscriptionPage = new InscriptionPage();
            inscriptionPage.show();
        });

        btnConnexion.setOnAction(e -> {
            stage.close();
            ConnexionPage connexionPage = new ConnexionPage();
            connexionPage.show();
        });

        btnAdmin.setOnAction(e -> {
            stage.close();
            AdminPage adminPage = new AdminPage();
            adminPage.show();
        });

        root.getChildren().addAll(titre, sousTitre, boutonBox);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public void show() {
        stage.show();
    }
}
