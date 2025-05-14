package model;

import java.time.LocalDate;

public class Utilisateur extends Personne {
    private Boolean estInscrit;

    public Utilisateur(String nom, String prenom, LocalDate dateNaissance, Nationalite nationalite, int age, Boolean estInscrit){
        super(nom, prenom, dateNaissance, nationalite, age);
        this.estInscrit = estInscrit;
    }

    public void modifierCoordonnées(){
        //Ici ça serait bien qu'on puisse gérer un onglet profil ou l'utilisateur pourrait gérer ses coordonnées
    }

    public ArbreGenealogique consulterArbre(){

        return null;
    }

    public String lienParente(Utilisateur utilisateur){

        return null;
    }

    public void ajouterLien(Utilisateur utilisateur, TypeLien typeLien){

    }

    public void modifierLien(Utilisateur utilisateur, TypeLien typeLien){

    }

    public Boolean confirmerLien(){

        return null;
    }


}
