package model;

public enum TypeRessource {
    FICHIER("Fichier"),
    IMAGE("Image"),
    LIEN("model.Lien");

    private final String libelle;

    TypeRessource(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
