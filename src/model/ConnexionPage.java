package model;

import model.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;

public class ConnexionPage {
    private JFrame frame;
    private JTextField loginField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private AuthentificationDAO authentificationDAO;

    public ConnexionPage() {
        authentificationDAO = new AuthentificationDAO();
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Connexion");
        frame.setBounds(100, 100, 400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel loginLabel = new JLabel("Login:");
        loginLabel.setBounds(50, 50, 100, 30);
        frame.getContentPane().add(loginLabel);

        loginField = new JTextField();
        loginField.setBounds(150, 50, 150, 30);
        frame.getContentPane().add(loginField);
        loginField.setColumns(10);

        JLabel passwordLabel = new JLabel("Mot de Passe:");
        passwordLabel.setBounds(50, 100, 100, 30);
        frame.getContentPane().add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 100, 150, 30);
        frame.getContentPane().add(passwordField);

        loginButton = new JButton("Se connecter");
        loginButton.setBounds(150, 150, 150, 30);
        frame.getContentPane().add(loginButton);

        // Bouton retour à l'accueil optionnel
        JButton btnRetour = new JButton("Retour à l'accueil");
        btnRetour.setBounds(150, 200, 150, 30);
        frame.getContentPane().add(btnRetour);
        btnRetour.addActionListener(e -> {
            frame.dispose();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String login = loginField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (login.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "Veuillez remplir tous les champs.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Utilisateur utilisateur = authentificationDAO.authentifier(login, password);

if (utilisateur == null) {
    JOptionPane.showMessageDialog(frame, "Échec de la connexion. Vérifiez vos identifiants.");
} else if (!utilisateur.getEstValide()) {
    JOptionPane.showMessageDialog(frame, "Compte en cours de validation. Veuillez patienter.");
} else {
    // Connexion réussie et validée
    JOptionPane.showMessageDialog(frame, "Connexion réussie ! Bienvenue " + utilisateur.getPrenom());
    frame.dispose();
    ouvrirArbre(utilisateur);  // ou autre action après connexion
}

            }
        });
    }

    private void ouvrirArbre(Utilisateur utilisateur) {
        Personne parent = new Personne("Guillarme", "Arno", LocalDate.of(1970, 10, 24), Nationalite.FRANCAIS, 54);
        Personne enfant = new Personne("Martin", "Julie", LocalDate.of(2003, 3, 2), Nationalite.FRANCAIS, 29);
        Personne frere = new Personne("Dupont", "Jean", LocalDate.of(2010, 8, 22), Nationalite.FRANCAIS, 13);

        Noeud noeudParent = new Noeud(parent);
        Noeud noeudEnfant = new Noeud(enfant);
        Noeud noeudFrere = new Noeud(frere);

        noeudParent.ajouterEnfant(noeudEnfant);
        noeudParent.ajouterEnfant(noeudFrere);

        ArbreGenealogique arbre = new ArbreGenealogique(utilisateur, parent);
        arbre.ajouterNoeud(noeudParent);
        arbre.ajouterNoeud(noeudEnfant);
        arbre.ajouterNoeud(noeudFrere);

        SwingUtilities.invokeLater(() -> {
            JFrame arbreFrame = new JFrame("Arbre Généalogique");
            arbreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            arbreFrame.add(new ArbrePanel(arbre));
            arbreFrame.pack();
            arbreFrame.setLocationRelativeTo(null);
            arbreFrame.setVisible(true);
        });
    }

    public void show() {
        frame.setVisible(true);
    }
}
