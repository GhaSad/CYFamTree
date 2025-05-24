package model;

public enum Nationalite {
    FRANCAIS("Français"),
    ALLEMAND("Allemand"),
    ESPAGNOL("Espagnol"),
    ITALIEN("Italien"),
    AMERICAIN("Américain"),
    JAPONAIS("Japonais"),
    CHINOIS("Chinois"),
    MAROCAIN("Marocain"),
    CANADIEN("Canadien");

    private final String libelle;

    Nationalite(String libelle) {
        this.libelle = libelle;
    }

}
