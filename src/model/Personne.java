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
    private List<Lien> liens;
    boolean estInscrit = false;

    public Personne(String nom, String prenom, LocalDate date, Nationalite nationalite, int age) {
        this.id = ++compteurId;
    	this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = date;
        this.nationalite = nationalite;
        this.Age = age;
        liens = new ArrayList<>();
    }
    public boolean estInscrit() {
        return estInscrit;
    }

    public void setEstInscrit(boolean estInscrit) {
        this.estInscrit = estInscrit;
    }
    
    public int getId() {
        return id;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public Nationalite getNationalite() {
        return nationalite;
    }

    public int getAge() {
        return Age;
    }
    
    public String getPrenom() {
    	return prenom;
    }
    
    public String getNom() {
    	return nom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void creerLien(Personne autre, TypeLien type) {
        
	    Lien lien = new Lien(this, autre, type);
	    	if (!lien.estValide()) {
	    	    throw new IllegalArgumentException("Lien invalide : contrainte non respectée.");
	    	}
	    	liens.add(lien);
    }

    public List<Lien> getLiens() {
        return liens;
    }

    public List<Personne> getParents() {
        List<Personne> parents = new ArrayList<>();
        for (Lien lien : liens) {
            if (lien.getTypeLien() == TypeLien.PERE || lien.getTypeLien() == TypeLien.MERE) {
                parents.add(lien.getPersonneLiee());
            }
        }
        return parents;
    }

    public List<Personne> getEnfants() {
        List<Personne> enfants = new ArrayList<>();
        for (Lien lien : liens) {
            if (lien.getTypeLien() == TypeLien.FILS || lien.getTypeLien() == TypeLien.FILLE) {
                enfants.add(lien.getPersonneLiee());
            }
        }
        return enfants;
    }

    public List<Personne> getFreres() {
        List<Personne> freres = new ArrayList<>();
        for (Lien lien : liens) {
            if (lien.getTypeLien() == TypeLien.FRERE) {
                freres.add(lien.getPersonneLiee());
            }
        }
        return freres;
    }

    public List<Personne> getSoeurs() {
        List<Personne> soeurs = new ArrayList<>();
        for (Lien lien : liens) {
            if (lien.getTypeLien() == TypeLien.SOEUR) {
                soeurs.add(lien.getPersonneLiee());
            }
        }
        return soeurs;
    }
    
    
    public void modifierDetails(){

    }

    public void supprimer(List<Personne> toutesLesPersonnes) {
        this.liens.clear();

        for (Personne autre : toutesLesPersonnes) {
            if (autre != this) {
                autre.liens.removeIf(lien -> lien.getPersonneLiee().equals(this));
            }
        }
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    
    @Override
    public String toString() {
    	return "ID : " +id + " Nom : " + nom + " Prenom ; " + prenom + " Date : " +dateNaissance + "";
    }
}
