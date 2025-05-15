package model;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;

public class Authentification {
    private Map<String, String> comptes;  // login -> mot de passe hashé
    private Map<String, Utilisateur> utilisateurs; // login -> Utilisateur

    public Authentification() {
        comptes = new HashMap<>();
        utilisateurs = new HashMap<>();

        // Ajout d'utilisateurs avec mot de passe en clair, mais stocké hashé
        ajouterUtilisateur("alice", "password123",
            new Utilisateur("Dupont", "Alice", LocalDate.of(1990, 5, 12), Nationalite.FRANCAIS, 33, true));
        ajouterUtilisateur("bob", "secret456",
            new Utilisateur("Martin", "Bob", LocalDate.of(1985, 9, 20), Nationalite.FRANCAIS, 38, true));
    }

    public void ajouterUtilisateur(String login, String mdpClair, Utilisateur utilisateur) {
        // Hachage du mot de passe avant stockage
        String hash = BCrypt.hashpw(mdpClair, BCrypt.gensalt());
        comptes.put(login, hash);
        utilisateurs.put(login, utilisateur);
    }

    public Utilisateur authentifier(String login, String mdpClair) {
        if (!comptes.containsKey(login)) return null;
        String hash = comptes.get(login);
        // Vérification du mot de passe
        if (BCrypt.checkpw(mdpClair, hash)) {
            Utilisateur user = utilisateurs.get(login);
            if (user != null && user.getEstInscrit()) {  // utilisation du getter
                return user;
            }
        }
        return null;
    }
}
