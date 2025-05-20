package model;

import java.util.*;
import java.time.LocalDateTime;

public class RessourcePartagee {
    private int idRessource;
    private static int compteur = 1;
    private TypeRessource typeRessource;
    private String fichier;
    private LocalDateTime date;
    private Personne auteur;
    private List<Personne> destinataires;

    public RessourcePartagee(TypeRessource typeRessource,String fichier, Personne Auteur, List<Personne> destinataires) {
        this.date = LocalDateTime.now();
        this.auteur = Auteur;
        this.typeRessource = typeRessource;
        this.fichier = fichier;
        this.destinataires = destinataires;
        this.idRessource += compteur++;
    }

    public Personne getAuteur() {
        return auteur;
    }
    public void setAuteur(Personne auteur) {
        this.auteur = auteur;
    }

    public String getFichier() {
        return fichier;
    }
}
