package model;

import java.util.*;

public class RessourcePartagee {
    private int idRessource = 0;
    private TypeRessource typeRessource;
    private String fichier;
    private Date date;
    private Personne Auteur;
    private List<Personne> destinataires;

    public RessourcePartagee(TypeRessource typeRessource, Personne Auteur, List<Personne> destinataires) {
        this.date = new Date();
        this.Auteur = Auteur;
        this.typeRessource = typeRessource;
        this.destinataires = destinataires;
        this.idRessource += 1;
    }
}
