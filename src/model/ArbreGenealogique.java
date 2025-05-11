package model;

import java.util.*;

public class ArbreGenealogique {
    private Utilisateur utilisateurProprietaire;
    private Personne racine;
    private List<Noeud> listeNoeuds;

    public ArbreGenealogique(Utilisateur utilisateur, Personne racine) {
        this.utilisateurProprietaire = utilisateur;
        this.racine = racine;
    }

    public void ajouterNoeud(Noeud noeud) {
        listeNoeuds.add(noeud);
    }

    public void supprimerNoeud(Noeud noeud) {
        listeNoeuds.remove(noeud);
    }

    public Noeud rechercherNoeud(String nom) {
        return null;
    }

    public void afficherTexte(){

    }

    public void afficherGraphique(){

    }
}
