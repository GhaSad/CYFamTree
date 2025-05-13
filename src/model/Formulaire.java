package model;
import java.util.*;

public class Formulaire {
    private String nom, prenom, numSecu;
    private Nationalite nationalite;
    private Statut statut;
    private Date dateNaissance;
    private Date dateSoumission;
    private String carteId, photo;

    public Boolean valider(){
        return false;
    }

    public void creerUtilisateur(){

    }

    public Boolean estComplet(){
        return false;
    }
}
