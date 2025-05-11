package model;

import java.util.*;

public class Personne {
    private int id = 0;
    private String nom, prenom;
    private Date dateNaissance;
    private Nationalite nationalite;
    private int Age;
    private List<TypeLien> lien;

    public Personne(String nom, String prenom, Date dateNaissance, Nationalite nationalite, int age) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.nationalite = nationalite;
        this.Age = age;
        id += 1;
        lien = new ArrayList<>();
    }

    public void ajouterLien(Personne p, Lien TypeLien){

    }

    public List<Personne> getParents(){

        return null;
    }

    public List<Personne> getEnfants(){

        return null;
    }

    public List<Personne> getFreres(){

        return null;
    }

    public List<Personne> getSoeurs(){

        return null;
    }

    public void modifierDetails(){

    }

    public void supprimer(){

    }
}
