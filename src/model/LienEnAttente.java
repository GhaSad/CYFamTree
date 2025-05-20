package model;

public class LienEnAttente {
    private Personne demandeur;
    private Personne cible;
    private TypeLien typeLien;

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
