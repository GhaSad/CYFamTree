package view;
import model.*;

import javax.swing.*;
import java.awt.*;

public class AccueilPage {

    private JFrame frame;
    private JButton btnInscription;
    private JButton btnConnexion;

    public AccueilPage() {
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Accueil");
        frame.setSize(350, 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new FlowLayout());

        btnInscription = new JButton("S'inscrire");
        btnConnexion = new JButton("Se connecter");

        frame.add(btnInscription);
        frame.add(btnConnexion);

        btnInscription.addActionListener(e -> {
            frame.dispose();  // Fermer la page d'accueil
            InscriptionPage inscriptionPage = new InscriptionPage();
            inscriptionPage.show();
        });

        btnConnexion.addActionListener(e -> {
            frame.dispose();  // Fermer la page d'accueil
            ConnexionPage connexionPage = new ConnexionPage();
            connexionPage.show();
        });
        JButton btnAdmin = new JButton("Administration");
frame.add(btnAdmin);

btnAdmin.addActionListener(e -> {
    frame.dispose();
    AdminPage adminPage = new AdminPage();
    adminPage.show();
});
        
        
    }

    public void show() {
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccueilPage accueil = new AccueilPage();
            accueil.show();
        });
    }
}
