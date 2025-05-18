package model;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Consultation {
    private int id; // facultatif mais bon pour la base
    private Date dateConsultation;
    private Utilisateur utilisateurConsulteur;

    public Consultation(Utilisateur utilisateurConsulteur) {
        this.utilisateurConsulteur = utilisateurConsulteur;
        this.dateConsultation = new Date();
    }

    public Date getDateConsultation() {
        return dateConsultation;
    }

    public Utilisateur getUtilisateurConsulteur() {
        return utilisateurConsulteur;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(dateConsultation);
    }
}
