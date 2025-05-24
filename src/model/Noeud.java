package model;

import java.util.*;

public class Noeud {
    private int id;
    private Personne personne;
    private Visibilite visibilite;
    private List<Noeud> parents;
    private List<Noeud> enfants;

    public Noeud(int id,Personne personne, Visibilite visibilite) {
        this.id = id;
        this.personne = personne;
        this.visibilite = visibilite != null ? visibilite : Visibilite.PRIVATE; 
        this.parents = new ArrayList<>();
        this.enfants = new ArrayList<>();
    }

    public Noeud(Personne personne) {
        this(0, personne, Visibilite.PRIVATE);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    

    public void ajouterParent(Noeud parent) {
        if (parent == null) return;
        if (!parents.contains(parent)) {
            parents.add(parent);
            parent.ajouterEnfant(this); 
        }
    }

    public void ajouterEnfant(Noeud enfant) {
        if (enfant == null) return;
        if (!enfants.contains(enfant)) {
            enfants.add(enfant);
            enfant.ajouterParent(this); 
        }
    }


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

}
