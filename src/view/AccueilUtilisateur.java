package view;

import model.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AccueilUtilisateur {
    private JFrame frame;
    private Utilisateur utilisateur;

    public AccueilUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
        initialize();
    }

    private void initialize() {
        frame = new JFrame("Accueil - Arbre Généalogique");
        frame.setBounds(100, 100, 400, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bienvenue = new JLabel("Bienvenue, " + utilisateur.getPrenom() + " !");
        bienvenue.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(bienvenue);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        if (utilisateur.getArbre() == null) {
            JLabel info = new JLabel("Vous n'avez pas encore d'arbre généalogique.");
            info.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(info);

            JButton creerBtn = new JButton("Créer mon arbre");
            creerBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            creerBtn.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    ArbreGenealogique arbre = new ArbreGenealogique(utilisateur, utilisateur);
                    Noeud racine = new Noeud(utilisateur);
                    arbre.ajouterNoeud(racine);
                    utilisateur.setArbre(arbre);

                    frame.dispose();
                    arbre.afficherGraphique();
                }
            });
            panel.add(Box.createRigidArea(new Dimension(0, 10)));
            panel.add(creerBtn);
        } else {
            JLabel info = new JLabel("Chargement de votre arbre généalogique...");
            info.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(info);

            frame.dispose();
            utilisateur.getArbre().afficherGraphique();
            return;
        }

        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}