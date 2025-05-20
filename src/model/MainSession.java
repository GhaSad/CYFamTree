package model;

public class MainSession {

    private static Utilisateur utilisateur;

    public static Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public static void setUtilisateur(Utilisateur utilisateur) {
        MainSession.utilisateur = utilisateur;
    }
}
