package model;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.*;

/**
 * Classe représentant une personne dans l'arbre généalogique.
 * Elle peut être un utilisateur ou une personne associée.
 */
public class Personne {

    private static int compteurId = 0;
    private int id;
    private String nom, prenom;
    private LocalDate dateNaissance;
    private Nationalite nationalite;
    private int Age;
    private List<Lien> liens;
    private boolean estInscrit = false;

    /**
     * Constructeur de la classe Personne.
     *
     * @param nom           Nom de la personne.
     * @param prenom        Prénom de la personne.
     * @param date          Date de naissance.
     * @param nationalite   Nationalité.
     * @param age           Âge calculé ou saisi.
     */
    public Personne(String nom, String prenom, LocalDate date, Nationalite nationalite, int age) {
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = date;
        this.nationalite = nationalite;
        this.Age = age;
        this.liens = new ArrayList<>();
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

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public void setNationalite(Nationalite nationalite) {
        this.nationalite = nationalite;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne un Optional contenant l'utilisateur si cette personne en est un.
     *
     * @return {@code Optional<Utilisateur>}
     */
    public Optional<Utilisateur> asUtilisateur() {
        if (this instanceof Utilisateur) {
            return Optional.of((Utilisateur) this);
        }
        return Optional.empty();
    }

    /**
     * Crée un lien direct sans validation.
     *
     * @param autre Personne liée.
     * @param type  Type de lien.
     */
    public void creerLienDirect(Personne autre, TypeLien type) {
        Lien lien = new Lien(this, autre, type);
        this.liens.add(lien);
    }

    /**
     * Crée un lien entre deux personnes, avec options pour validation et création de l’inverse.
     *
     * @param autre             La personne cible.
     * @param type              Le type de lien.
     * @param genererInverse    Si vrai, crée aussi le lien inverse.
     * @param validerStrictement Si vrai, valide selon les règles métier avancées.
     */
    public void creerLien(Personne autre, TypeLien type, ArbreGenealogique arbre, boolean genererInverse, boolean validerStrictement) {
        Lien lien = new Lien(this, autre, type);
        utils.ValidationResult result = validerStrictement ? lien.estValideAvancee(arbre) : new utils.ValidationResult(true, "");

        if (!result.isValide()) {
            throw new IllegalArgumentException("Lien invalide : " + result.getMessage());
        }

        this.liens.add(lien);

        try (Connection conn = dao.Database.getConnection()) {
            if (this.getId() > 0 && autre.getId() > 0) {
                dao.LienDAO.sauvegarder(lien, conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (genererInverse) {
            TypeLien inverse = type.getLienInverse();
            if (inverse != null && autre.getLiens().stream().noneMatch(l -> l.getPersonneLiee().equals(this) && l.getTypeLien() == inverse)) {
                try {
                    autre.creerLien(this, inverse, arbre, false, false);
                } catch (IllegalArgumentException ex) {
                    System.out.println(">>> Le lien inverse a échoué : " + ex.getMessage());
                }
            }
        }
    }


    /**
     * Crée un lien avec validation stricte et génération automatique de l’inverse.
     *
     * @param autre Personne cible.
     * @param type  Type de lien.
     */
    public void creerLien(Personne autre, TypeLien type, ArbreGenealogique arbre) {
        this.creerLien(autre, type, arbre, true, true);
    }


    /**
     * Supprime cette personne en supprimant tous les liens associés dans la liste passée.
     *
     * @param toutesLesPersonnes Liste de toutes les personnes du système.
     */
    public void supprimer(List<Personne> toutesLesPersonnes) {
        this.liens.clear();

        for (Personne autre : toutesLesPersonnes) {
            if (autre != this) {
                autre.liens.removeIf(lien -> lien.getPersonneLiee().equals(this));
            }
        }
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Personne)) return false;
        return this.getId() == ((Personne) obj).getId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ID : " + id + " Nom : " + nom + " Prenom : " + prenom + " Date : " + dateNaissance;
    }
}
