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
        return arbre;
    }

    public void setArbre(ArbreGenealogique arbre) {
        this.arbre = arbre;
    }

}
