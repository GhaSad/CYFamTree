package model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Représente un utilisateur inscrit sur la plateforme CYFamTree.
 * Hérite de la classe {@link Personne} et ajoute des attributs spécifiques à l'utilisateur du système.
 */
public class Utilisateur extends Personne {

    private String login;
    private Boolean estValide;
    private ArbreGenealogique arbre;
    private String email;
    private String numeroSecurite;
    private String carteIdentite;
    private String photoNumerique;
    private String numTel;
    private String codePublic;
    private Boolean estInscrit;
    private boolean doitChangerMotDePasse;

    /**
     * Constructeur principal pour un utilisateur.
     *
     * @param nom              Nom de l'utilisateur.
     * @param prenom           Prénom de l'utilisateur.
     * @param dateNaissance    Date de naissance.
     * @param nationalite      Nationalité.
     * @param age              Âge calculé (généralement ignoré car recalculé dynamiquement).
     * @param estInscrit       Indique si l'utilisateur est inscrit.
     * @param estValide        Indique si l'utilisateur a été validé par un administrateur.
     * @param email            Adresse email.
     * @param numeroSecurite   Numéro de sécurité sociale.
     * @param carteIdentite    Chemin de la carte d'identité.
     * @param photoNumerique   Chemin de la photo numérique.
     * @param numTel           Numéro de téléphone.
     * @param codePublic       Code public de l'utilisateur (utilisé pour la recherche/partage).
     */
    public Utilisateur(String nom, String prenom, LocalDate dateNaissance, Nationalite nationalite, int age,
                       boolean estInscrit, boolean estValide,
                       String email, String numeroSecurite, String carteIdentite,
                       String photoNumerique, String numTel, String codePublic) {

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

    // --- Getters & Setters ---

    /**
     * Retourne le login (code privé).
     */
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return email;
    }

    public String getNumTel() {
        return numTel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setNumTel(String numTel) {
        this.numTel = numTel;
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

    public void setCarteIdentite(String carteIdentite) {
        this.carteIdentite = carteIdentite;
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

    public String getCodePublic() {
        return this.codePublic;
    }

    public void setCodePublic(String codePublic) {
        this.codePublic = codePublic;
    }

    /**
     * Renvoie l'âge en années, basé sur l'année actuelle.
     */
    @Override
    public int getAge() {
        return LocalDate.now().getYear() - getDateNaissance().getYear();
    }

    public void setEstInscrit(boolean estInscrit) {
        this.estInscrit = estInscrit;
    }

    public boolean getEstInscrit() {
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

    // --- Méthodes utiles ---

    /**
     * Ajoute un nouveau nœud à l’arbre de l’utilisateur avec un lien de parenté.
     *
     * @param nouveauNoeud Nœud à ajouter.
     * @param lien          Type de lien (PERE, MERE, FILS...).
     */
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
        System.out.println("✅ Lien " + lien + " ajouté entre " +
                source.getPersonne().getPrenom() + " et " +
                nouveauNoeud.getPersonne().getPrenom());
    }

    /**
     * Représentation lisible de l’utilisateur.
     */
    @Override
    public String toString() {
        return getPrenom() + " " + getNom() + " (" + login + ")";
    }

    /**
     * Égalité basée sur l'identifiant unique.
     */
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
}
