package view;

import dao.ConsultationDAO;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Consultation;
import model.Utilisateur;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

/** Interface d'affichage de l'historique des consultations de l'arbre de l'utilisateur. */
public class HistoriqueConsultationPage {

    private Utilisateur utilisateur;

    public HistoriqueConsultationPage(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public void show() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #eaf2ff;");
        root.setAlignment(Pos.TOP_CENTER);

        Label titre = new Label("📊 Consultations de votre arbre généalogique");
        titre.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        List<Consultation> consultations = ConsultationDAO.getConsultationsParCible(utilisateur.getId());

        // Regroupements
        Map<String, Long> parMois = consultations.stream().collect(Collectors.groupingBy(
                c -> c.getDate().getYear() + "-" + String.format("%02d", c.getDate().getMonthValue()),
                TreeMap::new,
                Collectors.counting()
        ));

        Map<Integer, Long> parAnnee = consultations.stream().collect(Collectors.groupingBy(
                c -> c.getDate().getYear(),
                TreeMap::new,
                Collectors.counting()
        ));

        Map<String, Long> parUtilisateur = consultations.stream().collect(Collectors.groupingBy(
                c -> c.getUtilisateurConsulteur().getPrenom() + " " + c.getUtilisateurConsulteur().getNom(),
                TreeMap::new,
                Collectors.counting()
        ));

        int totalMois = (int) parMois.entrySet().stream()
                .filter(e -> e.getKey().equals(YearMonth.now().toString()))
                .mapToLong(Map.Entry::getValue).sum();

        Long totalAnneeLong = parAnnee.getOrDefault(LocalDate.now().getYear(), 0L);
        int totalAnnee = totalAnneeLong.intValue();

        int totalUtilisateurs = (int) parUtilisateur.keySet().size();

        Label resume = new Label("🔍 Résumé : " + totalMois + " ce mois-ci, " +
                totalAnnee + " cette année, " +
                totalUtilisateurs + " utilisateurs différents.");
        resume.setStyle("-fx-font-style: italic; -fx-padding: 5px;");

        VBox boxMois = new VBox(new Label("📆 Fréquences mensuelles :"));
        parMois.forEach((mois, count) -> boxMois.getChildren().add(new Label("- " + mois + " : " + count + " consultations")));

        VBox boxAnnee = new VBox(new Label("📅 Fréquences annuelles :"));
        parAnnee.forEach((annee, count) -> boxAnnee.getChildren().add(new Label("- " + annee + " : " + count + " consultations")));

        VBox boxUtilisateurs = new VBox(new Label("🧑 Consultations par utilisateur :"));
        parUtilisateur.forEach((nom, count) -> boxUtilisateurs.getChildren().add(new Label("- " + nom + " : " + count + " consultations")));

        Button retourBtn = new Button("Retour");
        retourBtn.setOnAction(e -> ((Stage) retourBtn.getScene().getWindow()).close());

        root.getChildren().addAll(titre, resume, boxMois, boxAnnee, boxUtilisateurs, retourBtn);

        Scene scene = new Scene(root, 600, 600);
        Stage stage = new Stage();
        stage.setTitle("Statistiques de consultation - Mon arbre");
        stage.setScene(scene);
        stage.show();
    }

}
