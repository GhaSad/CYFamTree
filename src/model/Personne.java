package model;

import java.sql.Connection;
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
        //this.id = ++compteurId;
    	this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = date;
        this.nationalite = nationalite;
        this.Age = age;
        liens = new ArrayList<>();
    }
    public int getId() {
        return id;
    }

    public boolean estInscrit() {
        return estInscrit;
    }

    public void setEstInscrit(boolean estInscrit) {
        this.estInscrit = estInscrit;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setNationalite(Nationalite nationalite) {
        this.nationalite = nationalite;
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
    
    public void creerLienDirect(Personne autre, TypeLien type) {
        Lien lien = new Lien(this, autre, type);
        this.liens.add(lien);
    }
    
    public Optional<Utilisateur> asUtilisateur() {
        if (this instanceof Utilisateur) {
            return Optional.of((Utilisateur) this);
        }
        return Optional.empty();
    }


    public void creerLien(Personne autre, TypeLien type, boolean genererInverse, boolean validerStrictement) {
        System.out.println(">>> Création de lien : " + type + " entre " + this.getPrenom() + " (" + this.getDateNaissance() + ") et " + autre.getPrenom() + " (" + autre.getDateNaissance() + ")");

        Lien lien = new Lien(this, autre, type);
        utils.ValidationResult result = validerStrictement ? lien.estValideAvancee() : new utils.ValidationResult(true, "");

        if (!result.isValide()) {
            System.out.println(">>> Refusé : " + result.getMessage());
            throw new IllegalArgumentException("Lien invalide : " + result.getMessage());
        }

        this.liens.add(lien);
        System.out.println(">>> Lien ajouté à la personne source (" + this.getPrenom() + ")");

        try (Connection conn = dao.Database.getConnection()) {
            if (this.getId() > 0 && autre.getId() > 0) {
                dao.LienDAO.sauvegarder(lien, conn);
                System.out.println(">>> Lien enregistré dans la base (id_source = " + this.getId() + ", id_cible = " + autre.getId() + ", type = " + type + ")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (genererInverse) {
            TypeLien inverse = type.getLienInverse();
            if (inverse != null && autre.getLiens().stream().noneMatch(l -> l.getPersonneLiee().equals(this) && l.getTypeLien() == inverse)) {
                try {
                    System.out.println(">>> Lien inverse tenté : " + autre.getPrenom() + " → " + this.getPrenom() + " (" + inverse + ")");
                    autre.creerLien(this, inverse, false, false); // ❗ ICI : validerStrictement = false
                } catch (IllegalArgumentException ex) {
                    System.out.println(">>> Le lien inverse a échoué : " + ex.getMessage());
                }
            }
        }
    }


    
    public void creerLien(Personne autre, TypeLien type) {
        this.creerLien(autre, type, true, true);
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
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (!(obj instanceof Personne)) return false;
        Personne other = (Personne) obj;
        return this.getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }


    @Override
    public String toString() {
    	return "ID : " +id + " Nom : " + nom + " Prenom ; " + prenom + " Date : " +dateNaissance + "";
    }
}
