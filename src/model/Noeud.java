package model;

import java.util.*;

public class Noeud {
    private Personne personne;
    private Visibilite visibilite;
    private List<Noeud> Parents;
    private List<Noeud> Enfants;

    public Noeud(Personne personne) {
        this.personne = personne;
    }

    public void ajouterParent(Personne p){

    }

    public void ajouterEnfant(Noeud n){

    }

    public void changerVisibilite(Visibilite v){
        this.visibilite = v;
    }
}
