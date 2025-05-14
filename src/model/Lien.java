package model;

public class Lien {
    private Personne personneLiee;
    private TypeLien typeLien;
    
    public Lien(Personne personneLiee, TypeLien typeLien) {
        this.personneLiee = personneLiee;
        this.typeLien = typeLien;
    }

    public Personne getPersonneLiee() {
        return personneLiee;
    }

    public TypeLien getTypeLien() {
        return typeLien;
    }

    @Override
    public String toString() {
        return typeLien + " : " + personneLiee.getPrenom() + " " + personneLiee.getNom();
    }
}
