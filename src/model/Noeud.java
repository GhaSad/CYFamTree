package model;

import java.util.*;

/**
 * Représente un nœud dans un arbre généalogique.
 * Un nœud est associé à une personne et contient les liens vers ses parents et enfants.
 */
public class Noeud {

    /** Identifiant unique du nœud dans la base de données. */
    private int id;

    /** La personne associée à ce nœud. */
    private Personne personne;

    /** Niveau de visibilité du nœud (PRIVATE ou PUBLIC). */
    private Visibilite visibilite;

    /** Liste des nœuds parents (maximum 2 biologiques). */
    private List<Noeud> parents;

    /** Liste des nœuds enfants. */
    private List<Noeud> enfants;

    /**
     * Constructeur principal.
     *
     * @param id          L'identifiant du nœud (généré depuis la base).
     * @param personne    La personne associée.
     * @param visibilite  La visibilité (par défaut PRIVATE si null).
     */
    public Noeud(int id, Personne personne, Visibilite visibilite) {
        this.id = id;
        this.personne = personne;
        this.visibilite = visibilite != null ? visibilite : Visibilite.PRIVATE;
        this.parents = new ArrayList<>();
        this.enfants = new ArrayList<>();
    }

    /**
     * Constructeur pratique pour créer un nœud sans ID, avec visibilité PRIVATE.
     *
     * @param personne La personne associée.
     */
    public Noeud(Personne personne) {
        this(0, personne, Visibilite.PRIVATE);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Ajoute un parent à ce nœud. La relation est bidirectionnelle.
     *
     * @param parent Le parent à ajouter.
     */
    public void ajouterParent(Noeud parent) {
        if (parent == null) return;
        if (!parents.contains(parent)) {
            parents.add(parent);
            parent.ajouterEnfant(this);
        }
    }

    /**
     * Ajoute un enfant à ce nœud. La relation est bidirectionnelle.
     *
     * @param enfant L'enfant à ajouter.
     */
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
