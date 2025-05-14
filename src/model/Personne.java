package model;

import java.time.LocalDate;
import java.util.*;


public class Personne {
	private static int compteurId = 0;
    private int id;
    private String nom, prenom;
    private LocalDate dateNaissance;
    private Nationalite nationalite;
    private int Age;
    private List<TypeLien> lien;

    public Personne(String nom, String prenom, LocalDate dateNaissance2, Nationalite nationalite, int age) {
        this.id = ++compteurId;
    	this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance2;
        this.nationalite = nationalite;
        this.Age = age;
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
    @Override
    public String toString() {
    	return "ID : " +id + " Nom : " + nom + " Prenom ; " + prenom + " Date : " +dateNaissance + "";
    }
}
