package model;

import java.util.*;

public class Noeud {
    private Personne personne;
    private Visibilite visibilite;
    private List<Noeud> parents;
    private List<Noeud> enfants;

    public Noeud(Personne personne) {
        this.personne = personne;
        this.visibilite = Visibilite.PRIVATE; // valeur par défaut
        this.parents = new ArrayList<>();
        this.enfants = new ArrayList<>();
    }

    public void ajouterParent(Noeud parent) {
        if (parent == null) return;
        if (!parents.contains(parent)) {
            parents.add(parent);
            parent.ajouterEnfant(this); // relation bidirectionnelle
        }
    }

    public void ajouterEnfant(Noeud enfant) {
        if (enfant == null) return;
        if (!enfants.contains(enfant)) {
            enfants.add(enfant);
            enfant.ajouterParent(this); // relation bidirectionnelle
        }
    }

    public void changerVisibilite(Visibilite v) {
        this.visibilite = v;
    }

    // Getters utiles
    public Personne getPersonne() {
        return personne;
    }

    public List<Noeud> getParents() {
        return parents;
    }

    public List<Noeud> getEnfants() {
        return enfants;
    }

    public Visibilite getVisibilite() {
        return visibilite;
    }

    // Optionnel : afficher le noeud et ses enfants récursivement
    public void afficher(int niveau) {
        for (int i = 0; i < niveau; i++) System.out.print("  ");
        System.out.println(personne.getPrenom() + " " + personne.getNom());
        for (Noeud enfant : enfants) {
            enfant.afficher(niveau + 1);
        }
    }
}
