package model;

import java.util.*;
import java.time.LocalDateTime;

/**
 * Représente une ressource (fichier) partagée entre utilisateurs.
 * Elle est associée à un type (image, document, etc.), un auteur et un ou plusieurs destinataires.
 */
public class RessourcePartagee {

    private int idRessource;
    private static int compteur = 1;
    private TypeRessource typeRessource;
    private String fichier;
    private LocalDateTime date;
    private Personne auteur;
    private List<Personne> destinataires;

    /**
     * Constructeur principal pour créer une ressource partagée.
     *
     * @param typeRessource Le type de la ressource (image, document...).
     * @param fichier        Le chemin du fichier partagé.
     * @param Auteur         L'auteur de la ressource.
     * @param destinataires  Liste des destinataires de la ressource.
     */
    public RessourcePartagee(TypeRessource typeRessource, String fichier, Personne Auteur, List<Personne> destinataires) {
        this.date = LocalDateTime.now();
        this.auteur = Auteur;
        this.typeRessource = typeRessource;
        this.fichier = fichier;
        this.destinataires = destinataires;
        this.idRessource += compteur++;
    }

    /**
     * Retourne l'identifiant unique de la ressource.
     *
     * @return ID de la ressource.
     */
    public int getIdRessource() {
        return idRessource;
    }

    /**
     * Définit manuellement l'ID (utile lors du chargement depuis la base).
     *
     * @param idRessource L'identifiant à assigner.
     */
    public void setIdRessource(int idRessource) {
        this.idRessource = idRessource;
    }

    /**
     * Retourne le type de la ressource.
     *
     * @return TypeRessource (ex: IMAGE, PDF...).
     */
    public TypeRessource getTypeRessource() {
        return typeRessource;
    }

    /**
     * Retourne la date d'envoi de la ressource.
     *
     * @return Date et heure d'envoi.
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Définit manuellement la date (ex: depuis la base).
     *
     * @param date Date de partage.
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Retourne l’auteur de la ressource.
     *
     * @return Personne qui a partagé la ressource.
     */
    public Personne getAuteur() {
        return auteur;
    }

    /**
     * Définit manuellement l’auteur.
     *
     * @param auteur La personne ayant partagé.
     */
    public void setAuteur(Personne auteur) {
        this.auteur = auteur;
    }

    /**
     * Retourne le chemin du fichier partagé.
     *
     * @return Chemin absolu ou relatif vers le fichier.
     */
    public String getFichier() {
        return fichier;
    }
}
