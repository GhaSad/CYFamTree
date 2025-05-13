package model;

public enum Statut {
    EN_COURS("En cours"),
    SOUMIS("Soumis"),
    APPROUVE("Approuvé"),
    REJETE("Rejeté"),
    ANNULE("Annulé");

    private final String label;

    Statut(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }


}
