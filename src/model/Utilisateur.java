package model;

import java.util.Date;

public class Utilisateur extends Personne {
    private Boolean estInscrit;

    public Utilisateur(String nom, String prenom, Date dateNaissance, Nationalite nationalite, int age, Boolean estInscrit){
        super(nom, prenom, dateNaissance, nationalite, age);
        this.estInscrit = estInscrit;
    }

    public void modifierCoordonnées(){

    }

    public ArbreGenealogique consulterArbre(){
        return null;
    }

    public String lienParente(Utilisateur utilisateur){
        return null;
    }

    public void ajouterLien(Utilisateur utilisateur){

    }

    public void modifierLien(Utilisateur utilisateur){

    }

    public Boolean confirmerLien(){

        return null;
    }


}
