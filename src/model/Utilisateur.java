package model;

import java.time.LocalDate;

public class Utilisateur extends Personne {
    private String login;           // <-- ajout
    private Boolean estInscrit;
    private Boolean estValide;
    private ArbreGenealogique arbre;


    public Utilisateur(String nom, String prenom, LocalDate dateNaissance, Nationalite nationalite, int age, Boolean estInscrit, Boolean estValide){
        super(nom, prenom, dateNaissance, nationalite, age);
        this.estInscrit = estInscrit;
        this.estValide = estValide;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getAge() {
        return LocalDate.now().getYear() - getDateNaissance().getYear();
    }

    public Boolean getEstInscrit() {
        return estInscrit;
    }

    public Boolean getEstValide() {
        return estValide;
    }

    public void setEstValide(Boolean estValide) {
        this.estValide = estValide;
    }

    public ArbreGenealogique getArbre() {
        return this.arbre;
    }

    public void setArbre(ArbreGenealogique arbre) {
        this.arbre = arbre;
    }

    public void ajouterNoeudAvecLien(Noeud nouveauNoeud, String lien) {
        if (this.arbre == null) {
            System.out.println("Erreur : l'utilisateur n'a pas encore d'arbre.");
            return;
        }

        // Cherche le noeud associé à l'utilisateur
        Noeud source = arbre.getNoeudParPersonne(this);
        if (source == null) {
            System.out.println("Erreur : noeud de l'utilisateur introuvable.");
            return;
        }

        // Ajout logique selon le type de lien
        switch (lien.toLowerCase()) {
            case "père":
            case "mère":
                // le nouveauNoeud est un parent du source
                source.ajouterParent(nouveauNoeud);
                break;
            case "fils":
            case "fille":
                // le nouveauNoeud est un enfant du source
                source.ajouterEnfant(nouveauNoeud);
                break;
            default:
                System.out.println("Lien non reconnu. Ajouter une logique personnalisée pour : " + lien);
                return;
        }

        arbre.ajouterNoeud(nouveauNoeud);
        System.out.println("Ajout effectué avec lien : " + lien);
    }

}
