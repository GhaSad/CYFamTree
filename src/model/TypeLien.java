package model;

public enum TypeLien {
    PERE("Père"),
    MERE("Mère"),
    SOEUR("Soeur"),
    FRERE("Frère"),
    TANTE("Tante"),
    ONCLE("Oncle"),
    GRAND_PERE("Grand-père");

    private final String libelle;

    TypeLien(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public Boolean estValide(){
        //Conditions à implémenter
        return true;
    }

    public void demandeConfirmation(){

    }
}
