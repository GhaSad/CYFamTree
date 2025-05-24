package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Représente une consultation de l’arbre ou du profil d’un utilisateur par un autre.
 * Utilisée pour tracer les accès entre utilisateurs.
 */
public class Consultation {
    private int id;
    private Utilisateur utilisateurConsulteur;  // L'utilisateur qui consulte
    private Utilisateur utilisateurCible;       //L'utilisateur consulté
    private LocalDateTime date;                 // Date et heure de la consultation


    /**
     * Crée une nouvelle consultation entre un utilisateur source et une cible.
     *
     * @param consulteur L’utilisateur qui effectue la consultation
     * @param cible      L’utilisateur dont les données sont consultées
     * @param date       La date et l’heure de la consultation
     */
    public Consultation(Utilisateur consulteur, Utilisateur cible, LocalDateTime date) {
        this.utilisateurConsulteur = consulteur;
        this.utilisateurCible = cible;
        this.date = date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public Utilisateur getUtilisateurConsulteur() {
        return utilisateurConsulteur;
    }

    public Utilisateur getUtilisateurCible() {
        return utilisateurCible;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return date.format(formatter);
    }

}
