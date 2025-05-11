package model;

import java.util.*;

public class Consultation {
    private Date dateConsultation;
    private Utilisateur utilisateurConsulteur;
    private Utilisateur utilisateurCible;
    private static int nombreConsultation = 0;

    public Consultation(Utilisateur utilisateurCible, Utilisateur utilisateurConsulteur) {
        this.utilisateurCible = utilisateurCible;
        this.utilisateurConsulteur = utilisateurConsulteur;
        this.dateConsultation = new Date();
        nombreConsultation += 1;
    }
}
