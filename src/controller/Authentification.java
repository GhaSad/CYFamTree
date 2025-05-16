package controller;
import java.util.*;

public class Authentification {
    private final String email;
    private String mdp;
    private int codePublic;
    private Date dateDerniereConnexion;

    public Authentification(String email, String mdp) {
        this.email = email;
        this.mdp = mdp;
    }

    public Boolean VerifierConnexion(String email, String mdp) {
        return this.email.equals(email)  && this.mdp.equals(mdp);
    }

    public void réinitialiserMDP(String mdp) {
        this.mdp = mdp;
    }
}
