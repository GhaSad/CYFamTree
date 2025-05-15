package model;

import java.util.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

public class ArbreGenealogique {
    private Utilisateur utilisateur;
    private Personne racine;
    private List<Noeud> listeNoeuds;

    public ArbreGenealogique(Utilisateur utilisateur, Personne racine) {
        this.utilisateur = utilisateur;
        this.racine = racine;
        this.listeNoeuds = new ArrayList<>();
    }

    public void ajouterNoeud(Noeud noeud) {
        listeNoeuds.add(noeud);
    }

    public void supprimerNoeud(Noeud noeud) {
        listeNoeuds.remove(noeud);
    }

    public Noeud rechercherNoeud(String nom) {
        for (Noeud n : listeNoeuds) {
            if (n.getPersonne().getNom().equalsIgnoreCase(nom)) {
                return n;
            }
        }
        return null;
    }

    public Personne getRacine() {
        return racine;
    }

    public List<Personne> getEnfants(Personne p) {
        Noeud noeud = null;
        for (Noeud n : listeNoeuds) {
            if (n.getPersonne().equals(p)) {
                noeud = n;
                break;
            }
        }
        if (noeud == null) {
            return new ArrayList<>();
        }
        List<Personne> enfants = new ArrayList<>();
        for (Noeud enfantNoeud : noeud.getEnfants()) {
            enfants.add(enfantNoeud.getPersonne());
        }
        return enfants;
    }

    // Affichage texte console avec détection de cycles
    public void afficherTexte() {
        Set<Personne> visites = new HashSet<>();
        afficherNoeud(racine, 0, visites);
    }

    private void afficherNoeud(Personne p, int niveau, Set<Personne> visites) {
        if (visites.contains(p)) {
            for (int i = 0; i < niveau; i++) System.out.print("  ");
            System.out.println("(cycle détecté avec " + p.getPrenom() + " " + p.getNom() + ")");
            return;
        }
        visites.add(p);

        for (int i = 0; i < niveau; i++) System.out.print("  ");
        System.out.println(p.getPrenom() + " " + p.getNom());

        List<Personne> enfants = getEnfants(p);
        for (Personne enfant : enfants) {
            afficherNoeud(enfant, niveau + 1, visites);
        }
    }

    // Affichage graphique avec JTree classique
    public void afficherGraphique() {
        Set<Personne> visites = new HashSet<>();
        DefaultMutableTreeNode rootNode = construireArbreGraphique(racine, visites);
        JTree tree = new JTree(rootNode);

        JFrame frame = new JFrame("Arbre Généalogique (JTree)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new JScrollPane(tree));
        frame.setSize(400, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private DefaultMutableTreeNode construireArbreGraphique(Personne p, Set<Personne> visites) {
        if (visites.contains(p)) {
            return new DefaultMutableTreeNode("(cycle détecté avec " + p.getPrenom() + " " + p.getNom() + ")");
        }
        visites.add(p);

        DefaultMutableTreeNode node = new DefaultMutableTreeNode(p.getPrenom() + " " + p.getNom());
        List<Personne> enfants = getEnfants(p);
        for (Personne enfant : enfants) {
            node.add(construireArbreGraphique(enfant, visites));
        }
        return node;
    }
}
