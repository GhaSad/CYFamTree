package tests;

import model.*;
import javax.swing.*;
import java.time.LocalDate;

public class PersonneTest {
    public static void main(String[] args) {
        // Création de l'authentification avec des utilisateurs par défaut
        Authentification auth = new Authentification();

        String loginTest = "alice";
        String mdpTest = "password123";

        Utilisateur user = auth.authentifier(loginTest, mdpTest);

        if (user == null) {
            System.out.println("Échec de la connexion pour " + loginTest);
            return;
        }

        System.out.println("Connexion réussie : " + user.getPrenom() + " " + user.getNom());

        // Création des personnes (tu peux adapter ou ajouter plus)
        Personne parent = new Personne("Guillarme", "Arno", LocalDate.of(1970, 10, 24), Nationalite.FRANCAIS, 54);
        Personne enfant = new Personne("Martin", "Julie", LocalDate.of(2003, 3, 2), Nationalite.FRANCAIS, 29);
        Personne frere = new Personne("Dupont", "Jean", LocalDate.of(2010, 8, 22), Nationalite.FRANCAIS, 13);

        // Création des noeuds
        Noeud noeudParent = new Noeud(parent);
        Noeud noeudEnfant = new Noeud(enfant);
        Noeud noeudFrere = new Noeud(frere);

        // Relations parents-enfants
        noeudParent.ajouterEnfant(noeudEnfant);
        noeudParent.ajouterEnfant(noeudFrere);

        // Création de l'arbre généalogique avec racine parent
        ArbreGenealogique arbre = new ArbreGenealogique(user, parent);
        arbre.ajouterNoeud(noeudParent);
        arbre.ajouterNoeud(noeudEnfant);
        arbre.ajouterNoeud(noeudFrere);

        // Affichage texte
        arbre.afficherTexte();

        // Affichage graphique classique
        arbre.afficherGraphique();

        // Affichage graphique personnalisé
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Arbre Généalogique Custom");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new ArbrePanel(arbre));
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
