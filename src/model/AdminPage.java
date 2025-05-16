package model;

import model.Utilisateur;
import model.UtilisateurDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class AdminPage {

    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private UtilisateurDAO utilisateurDAO;

    // Mot de passe admin simple en dur (à sécuriser en production)
    private static final String ADMIN_PASSWORD = "admin123";

    public AdminPage() {
        utilisateurDAO = new UtilisateurDAO();
        initialize();
        chargerUtilisateurs();
    }

    private void initialize() {
        frame = new JFrame("Administration des utilisateurs");
        frame.setSize(700, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        tableModel = new DefaultTableModel(new Object[]{"Login", "Nom", "Prénom", "Date Naissance", "Nationalité", "Validé"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnValider = new JButton("Valider inscription");
        JButton btnSupprimer = new JButton("Supprimer utilisateur");
        JButton btnRetour = new JButton("Retour à l'accueil");

        btnValider.addActionListener(e -> validerUtilisateur());
        btnSupprimer.addActionListener(e -> supprimerUtilisateur());
        btnRetour.addActionListener(e -> {
            frame.dispose();
            AccueilPage accueilPage = new AccueilPage();
            accueilPage.show();
        });

        JPanel panelBoutons = new JPanel();
        panelBoutons.add(btnValider);
        panelBoutons.add(btnSupprimer);
        panelBoutons.add(btnRetour);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panelBoutons, BorderLayout.SOUTH);
    }

private void chargerUtilisateurs() {
    tableModel.setRowCount(0);
    List<Utilisateur> utilisateurs = utilisateurDAO.findAll();  // Tous les utilisateurs
    for (Utilisateur u : utilisateurs) {
        tableModel.addRow(new Object[]{
            u.getLogin(),
            u.getNom(),
            u.getPrenom(),
            u.getDateNaissance(),
            u.getNationalite(),
            u.getEstValide() ? "Validé" : "En attente"
        });
    }
}


private void validerUtilisateur() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un utilisateur.");
        return;
    }
    String login = (String) tableModel.getValueAt(selectedRow, 0);
    String etat = (String) tableModel.getValueAt(selectedRow, 5);
    if ("Validé".equals(etat)) {
        JOptionPane.showMessageDialog(frame, "Cet utilisateur est déjà validé.");
        return;
    }
    utilisateurDAO.validerUtilisateur(login);
    chargerUtilisateurs();
    JOptionPane.showMessageDialog(frame, "Inscription validée.");
}



private void supprimerUtilisateur() {
    int selectedRow = table.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(frame, "Veuillez sélectionner un utilisateur.");
        return;
    }
    String login = (String) tableModel.getValueAt(selectedRow, 0);
    utilisateurDAO.supprimerUtilisateur(login);
    chargerUtilisateurs();   // <-- mettre ici le nom correct de ta méthode de chargement
    JOptionPane.showMessageDialog(frame, "Utilisateur supprimé.");
}



public void show() {
    if (demanderMotDePasseAdmin()) {
        chargerUtilisateurs();  // Charge tous
        frame.setVisible(true);
    } else {
        JOptionPane.showMessageDialog(null, "Mot de passe incorrect. Accès refusé.", "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}


    private boolean demanderMotDePasseAdmin() {
        JPasswordField pwd = new JPasswordField();
        int action = JOptionPane.showConfirmDialog(null, pwd, "Veuillez entrer le mot de passe admin", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            String saisie = new String(pwd.getPassword());
            return ADMIN_PASSWORD.equals(saisie);
        }
        return false;
    }
}
