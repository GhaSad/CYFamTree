package model;
import java.util.*;

public class Admin {
    private static int id = 0;
    private String nom, prenom, adresse, email, mdp;
    private Date dateDerniereConnexion;

    public Admin(String nom, String prenom, String adresse, String email, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.email = email;
        this.mdp = mdp;
        dateDerniereConnexion = new Date();
        id +=1 ;
    }

    public void examinerFormulaire(Formulaire f){

    }

}
