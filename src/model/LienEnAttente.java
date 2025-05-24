package model;

/**
 * Représente une demande de lien de parenté en attente de validation.
 * Ce lien concerne uniquement des utilisateurs (pas des personnes ordinaires).
 */
public class LienEnAttente {

    /** La personne (utilisateur) qui a initié la demande de lien. */
    private Personne demandeur;

    /** La personne (utilisateur) ciblée par la demande. */
    private Personne cible;

    /** Le type de lien de parenté demandé (ex : PERE, FILLE, etc.). */
    private TypeLien typeLien;

    /**
     * Crée une nouvelle demande de lien entre deux personnes.
     *
     * @param demandeur La personne qui envoie la demande.
     * @param cible     La personne qui devra accepter ou refuser.
     * @param typeLien  Le type de lien demandé.
     */
    public LienEnAttente(Personne demandeur, Personne cible, TypeLien typeLien) {
        this.demandeur = demandeur;
        this.cible = cible;
        this.typeLien = typeLien;
    }

    public Personne getDemandeur() {
        return demandeur;
    }

    public Personne getCible() {
        return cible;
    }

    public TypeLien getTypeLien() {
        return typeLien;
    }
}
