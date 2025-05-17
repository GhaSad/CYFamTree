package view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class AccueilPage {

    private Stage stage;

    public AccueilPage() {
        stage = new Stage();
        stage.setTitle("Accueil");

        FlowPane root = new FlowPane();
        root.setHgap(10);
        root.setVgap(10);
        root.setPadding(new Insets(10));

        Button btnInscription = new Button("S'inscrire");
        Button btnConnexion = new Button("Se connecter");
        Button btnAdmin = new Button("Administration");

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

        root.getChildren().addAll(btnInscription, btnConnexion, btnAdmin);

        Scene scene = new Scene(root, 350, 150);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    public void show() {
        stage.show();
    }
}
