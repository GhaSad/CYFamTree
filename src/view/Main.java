package view;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Lancer la page d'accueil
        AccueilPage accueilPage = new AccueilPage();
        accueilPage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
