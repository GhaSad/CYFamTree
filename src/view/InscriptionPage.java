package view;
import model.*;
import dao.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class InscriptionPage {
    private JFrame frame;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JTextField nomField;
    private JTextField prenomField;
    private JTextField dateNaissanceField;
    private JComboBox<Nationalite> nationaliteComboBox;
    private JButton registerButton;
    private AuthentificationDAO authentificationDAO;

    public InscriptionPage() {
        authentificationDAO = new AuthentificationDAO();
        initialize();
    }
	
    private void initialize() {
        frame = new JFrame("Inscription");
        frame.setBounds(100, 100, 400, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
		
		// Dans la méthode initialize() de InscriptionPage, ajoute après le bouton d'inscription :

JButton btnRetour = new JButton("Retour à l'accueil");
btnRetour.setBounds(130, 320, 150, 30);
frame.getContentPane().add(btnRetour);

btnRetour.addActionListener(e -> {
    frame.dispose();  // Fermer la fenêtre d'inscription
    AccueilPage accueil = new AccueilPage();
    accueil.show();   // Ouvrir la page d'accueil
});
		
		
        JLabel loginLabel = new JLabel("Login :");
        loginLabel.setBounds(50, 30, 100, 25);
        frame.getContentPane().add(loginLabel);

        loginField = new JTextField();
        loginField.setBounds(160, 30, 160, 25);
        frame.getContentPane().add(loginField);

        JLabel passwordLabel = new JLabel("Mot de passe :");
        passwordLabel.setBounds(50, 70, 100, 25);
        frame.getContentPane().add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(160, 70, 160, 25);
        frame.getContentPane().add(passwordField);

        JLabel nomLabel = new JLabel("Nom :");
        nomLabel.setBounds(50, 110, 100, 25);
        frame.getContentPane().add(nomLabel);

        nomField = new JTextField();
        nomField.setBounds(160, 110, 160, 25);
        frame.getContentPane().add(nomField);

        JLabel prenomLabel = new JLabel("Prénom :");
        prenomLabel.setBounds(50, 150, 100, 25);
        frame.getContentPane().add(prenomLabel);

        prenomField = new JTextField();
        prenomField.setBounds(160, 150, 160, 25);
        frame.getContentPane().add(prenomField);

        JLabel dateLabel = new JLabel("Date naissance (YYYY-MM-DD) :");
        dateLabel.setBounds(50, 190, 200, 25);
        frame.getContentPane().add(dateLabel);

        dateNaissanceField = new JTextField();
        dateNaissanceField.setBounds(250, 190, 100, 25);
        frame.getContentPane().add(dateNaissanceField);

        JLabel nationaliteLabel = new JLabel("Nationalité :");
        nationaliteLabel.setBounds(50, 230, 100, 25);
        frame.getContentPane().add(nationaliteLabel);

        nationaliteComboBox = new JComboBox<>(Nationalite.values());
        nationaliteComboBox.setBounds(160, 230, 160, 25);
        frame.getContentPane().add(nationaliteComboBox);

        registerButton = new JButton("S'inscrire");
        registerButton.setBounds(130, 280, 120, 30);
        frame.getContentPane().add(registerButton);

        registerButton.addActionListener(e -> {
            try {
                String login = loginField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();
                String nom = nomField.getText().trim();
                String prenom = prenomField.getText().trim();
                String dateStr = dateNaissanceField.getText().trim();
                LocalDate dateNaissance = LocalDate.parse(dateStr);
                Nationalite nationalite = (Nationalite) nationaliteComboBox.getSelectedItem();

                if(login.isEmpty() || password.isEmpty() || nom.isEmpty() || prenom.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Tous les champs doivent être remplis.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if(authentificationDAO.userExists(login)) {
                    JOptionPane.showMessageDialog(frame, "Ce login existe déjà.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Utilisateur utilisateur = new Utilisateur(nom, prenom, dateNaissance, nationalite, 0, true,false);

                authentificationDAO.save(utilisateur, login, password);
                JOptionPane.showMessageDialog(frame, "Inscription en attente ! Votre demande va être traitée");

                frame.dispose();

                // Ouvrir la page de connexion après inscription
                ConnexionPage connexionPage = new ConnexionPage();
                connexionPage.show();

            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(frame, "Format de date invalide. Utilisez YYYY-MM-DD.", "Erreur", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Erreur lors de l'inscription : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });
    }
	
    public void show() {
        frame.setVisible(true);
    }
    public JFrame getFrame() {
    return frame;
}
    

    public static void main(String[] args) {
        InscriptionPage inscriptionPage = new InscriptionPage();
        inscriptionPage.show();
    }
}