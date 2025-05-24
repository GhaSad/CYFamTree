package model;

/**
 * Classe utilitaire représentant la session principale de l'application.
 * Elle permet de stocker l'utilisateur actuellement connecté.
 */
public class MainSession {

    /** L'utilisateur actuellement connecté à l'application. */
    private static Utilisateur utilisateur;

    /**
     * Retourne l'utilisateur actuellement connecté.
     *
     * @return L'utilisateur courant, ou {@code null} s'il n'y en a pas.
     */
    public static Utilisateur getUtilisateur() {
        return utilisateur;
    }

    /**
     * Définit l'utilisateur actuellement connecté.
     *
     * @param utilisateur L'utilisateur à enregistrer dans la session.
     */
    public static void setUtilisateur(Utilisateur utilisateur) {
        MainSession.utilisateur = utilisateur;
    }
}
