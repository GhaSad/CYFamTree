package model;

import java.time.LocalDate;
import java.util.Objects;

public class Utilisateur extends Personne {
    private String login;           // <-- ajout
    private Boolean estValide;
    private ArbreGenealogique arbre;
    private String email;
    private String numeroSecurite;
    private String carteIdentite;
    private String photoNumerique;
    private String numTel;
    private String codePublic;
    private boolean doitChangerMotDePasse;


    public Utilisateur(String nom, String prenom, LocalDate dateNaissance, Nationalite nationalite,int age, boolean estInscrit, boolean estValide,
                       String email,String numeroSecurite, String carteIdentite, String photoNumerique, String numTel,String codePublic) {
        super(nom, prenom, dateNaissance, nationalite, age);
        this.setEstInscrit(estInscrit);
        this.estValide = estValide;
        this.email = email;
        this.numTel = numTel;
        this.numeroSecurite = numeroSecurite;
        this.carteIdentite = carteIdentite;
        this.photoNumerique = photoNumerique;
        this.codePublic = codePublic;
    }

    public String getLogin() {
        return login;
    }
    
    public String getEmail() {
        return email;
    }

    public String getCodePublic(){
        return this.codePublic;
    }

    public void setCodePublic(String codePublic) {
        this.codePublic = codePublic;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getAge() {
        return LocalDate.now().getYear() - getDateNaissance().getYear();
    }

    public String getNumeroSecurite() {
        return numeroSecurite;
    }

    public void setNumeroSecurite(String numeroSecurite) {
        this.numeroSecurite = numeroSecurite;
    }

    public String getCarteIdentite() {
        return carteIdentite;
    }

    public void setCarteIdentite(String numeroCarteIdentite) {
        this.carteIdentite = numeroCarteIdentite;
    }


    public String getPhotoNumerique() {
        return photoNumerique;
    }

    public void setPhotoNumerique(String photoNumerique) {
        this.photoNumerique = photoNumerique;
    }

    public boolean isDoitChangerMotDePasse() {
        return doitChangerMotDePasse;
    }

    public void setDoitChangerMotDePasse(boolean doitChangerMotDePasse) {
        this.doitChangerMotDePasse = doitChangerMotDePasse;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur)) return false;
        Utilisateur that = (Utilisateur) o;
        return this.getId() == that.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
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

    public void ajouterNoeudAvecLien(Noeud nouveauNoeud, TypeLien lien) {
        if (this.arbre == null) {
            System.out.println("Erreur : l'utilisateur n'a pas encore d'arbre.");
            return;
        }

        Noeud source = arbre.getNoeudParPersonne(this);
        if (source == null) {
            System.out.println("Erreur : noeud de l'utilisateur introuvable.");
            return;
        }

        switch (lien) {
            case PERE:
            case MERE:
                source.ajouterParent(nouveauNoeud);
                break;
            case FILS:
            case FILLE:
                source.ajouterEnfant(nouveauNoeud);
                break;
            default:
                System.out.println("Lien non reconnu dans ajouterNoeudAvecLien");
                return;
        }

        arbre.ajouterNoeud(nouveauNoeud);
        System.out.println("✅ Lien " + lien + " ajouté entre " + source.getPersonne().getPrenom() + " et " + nouveauNoeud.getPersonne().getPrenom());
    }
    
    @Override
    public String toString() {
        return getPrenom() + " " + getNom() + " (" + login + ")";
    }


}
