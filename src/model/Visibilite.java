package model;

public enum Visibilite {
    PUBLIC("Public"),
    PRIVATE("Private"),
    PROTECTED("Protected");

    private final String libelle;

    Visibilite(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
