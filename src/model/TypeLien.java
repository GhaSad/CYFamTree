package model;

public enum TypeLien {
    PERE("PERE"),
    MERE("MERE"),
    FILS("FILS"),
    FILLE("FILLE"),
    ENFANT("ENFANT"),
    SOEUR("SOEUR"),
    FRERE("FRERE"),
    TANTE("TANTE"),
    ONCLE("ONCLE"),
    GRAND_PERE("GRAND_PERE"),
	GRAND_MERE("GRAND_MERE"),
	NEVEU("NEVEU"),
	NIECE("NIECE"),
	PETIT_FILS("PETIT_FILS"),
	PETITE_FILLE("PETITE_FILLE");

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
