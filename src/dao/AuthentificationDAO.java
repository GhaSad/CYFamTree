package dao;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;

/**
 * DAO responsable de l'authentification et de l'enregistrement des utilisateurs.
 */
public class AuthentificationDAO {

    /**
     * Vérifie les identifiants fournis (login et mot de passe) pour authentifier un utilisateur.
     *
     * @param login    Identifiant (code privé) de l'utilisateur.
     * @param mdpClair Mot de passe saisi par l'utilisateur.
     * @return L'objet {@link Utilisateur} si l'identification est correcte, sinon {@code null}.
     */
    public Utilisateur authentifier(String login, String mdpClair) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            System.out.println("🔍 Requête exécutée avec login = '" + login + "'");

            if (rs.next()) {
                System.out.println("✅ Utilisateur trouvé en base : " + rs.getString("login"));
                String motDePasseHash = rs.getString("mot_de_passe");

                if (BCrypt.checkpw(mdpClair, motDePasseHash)) {
                    Utilisateur user = new Utilisateur(
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            LocalDate.parse(rs.getString("date_naissance")),
                            Nationalite.valueOf(rs.getString("nationalite")),
                            0,
                            rs.getInt("est_inscrit") == 1,
                            rs.getInt("est_valide") == 1,
                            rs.getString("email"),
                            rs.getString("numero_securite"),
                            rs.getString("carte_identite"),
                            rs.getString("photo_numerique"),
                            rs.getString("num_tel"),
                            rs.getString("code_public")
                    );

                    user.setLogin(rs.getString("login"));
                    user.setId(rs.getInt("id"));
                    user.setDoitChangerMotDePasse(rs.getInt("doit_changer_mdp") == 1);

                    return user;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Enregistre un nouvel utilisateur ainsi que son équivalent dans la table {@code personne}.
     *
     * @param utilisateur      Utilisateur à sauvegarder.
     * @param login            Identifiant (code privé) généré.
     * @param motDePasseClair Mot de passe initial (non utilisé ici, généré automatiquement via le prénom).
     */
    public void save(Utilisateur utilisateur, String login, String motDePasseClair) {
        if (userExists(login)) {
            System.out.println("Erreur : L'utilisateur avec le login '" + login + "' existe déjà.");
            return;
        }

        try (Connection conn = Database.getConnection()) {

            String sqlUtilisateur = "INSERT INTO utilisateur(" +
                    "login, mot_de_passe, nom, prenom, date_naissance, nationalite, " +
                    "est_inscrit, est_valide, doit_changer_mdp, email, numero_securite, carte_identite, photo_numerique, num_tel, code_public" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            int idUtilisateur;

            try (PreparedStatement pstmt = conn.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS)) {
                String motDePasseInitial = utilisateur.getPrenom().toLowerCase();
                String hash = BCrypt.hashpw(motDePasseInitial, BCrypt.gensalt());

                pstmt.setString(1, login);
                pstmt.setString(2, hash);
                pstmt.setString(3, utilisateur.getNom());
                pstmt.setString(4, utilisateur.getPrenom());
                pstmt.setString(5, utilisateur.getDateNaissance().toString());
                pstmt.setString(6, utilisateur.getNationalite().name());
                pstmt.setInt(7, utilisateur.getEstInscrit() ? 1 : 0);
                pstmt.setInt(8, 0); // est_valide = false
                pstmt.setInt(9, 1); // doit changer mdp
                pstmt.setString(10, utilisateur.getEmail());
                pstmt.setString(11, utilisateur.getNumeroSecurite());
                pstmt.setString(12, utilisateur.getCarteIdentite());
                pstmt.setString(13, utilisateur.getPhotoNumerique());
                pstmt.setString(14, utilisateur.getNumTel());
                pstmt.setString(15, utilisateur.getCodePublic());

                pstmt.executeUpdate();

                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    idUtilisateur = rs.getInt(1);
                    utilisateur.setId(idUtilisateur);
                } else {
                    throw new SQLException("Échec de création de l'utilisateur : aucun ID généré.");
                }
            }

            // Insertion dans la table personne
            String sqlPersonne = "INSERT INTO personne(nom, prenom, date_naissance, nationalite, age, utilisateur_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stmtPersonne = conn.prepareStatement(sqlPersonne)) {
                stmtPersonne.setString(1, utilisateur.getNom());
                stmtPersonne.setString(2, utilisateur.getPrenom());
                stmtPersonne.setString(3, utilisateur.getDateNaissance().toString());
                stmtPersonne.setString(4, utilisateur.getNationalite().name());
                stmtPersonne.setInt(5, utilisateur.getAge());
                stmtPersonne.setInt(6, utilisateur.getId());

                stmtPersonne.executeUpdate();
            }

            System.out.println("✅ Utilisateur et personne associés enregistrés (id_utilisateur = " + utilisateur.getId() + ")");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vérifie si un utilisateur avec le login spécifié existe déjà.
     *
     * @param login Login à tester.
     * @return {@code true} si l'utilisateur existe, sinon {@code false}.
     */
    public boolean userExists(String login) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Vérifie si un email est déjà utilisé dans la base.
     *
     * @param email Email à tester.
     * @return {@code true} si l'email existe déjà, sinon {@code false}.
     */
    public boolean emailExiste(String email) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE email = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
