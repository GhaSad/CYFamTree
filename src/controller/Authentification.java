package controller;
import java.util.*;

public class Authentification {
    private int codePrive;
    private String mdp;
    private int codePublic;
    private Date dateDerniereConnexion;

    public Authentification(int codePrive, String mdp) {
        this.codePrive = codePrive;
        this.mdp = mdp;
    }

    public Boolean VerifierConnexion(int codePrive, String mdp) {
        return this.codePrive == codePrive && this.mdp.equals(mdp);
    }

    public void réinitialiserMDP(String mdp) {
        this.mdp = mdp;
    }
}
