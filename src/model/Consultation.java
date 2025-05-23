package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Consultation {
    private int id;
    private Utilisateur utilisateurConsulteur;
    private Utilisateur utilisateurCible;
    private LocalDateTime date;

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
