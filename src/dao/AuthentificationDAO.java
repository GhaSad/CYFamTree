package dao;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.time.LocalDate;

public class AuthentificationDAO {

    public Utilisateur authentifier(String login, String mdpClair) {
        String sql = "SELECT * FROM utilisateur WHERE login = ?";
        Utilisateur user = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // 🔒 début de transaction

            pstmt.setString(1, login);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String motDePasseHash = rs.getString("mot_de_passe");

                    if (BCrypt.checkpw(mdpClair, motDePasseHash)) {
                        user = new Utilisateur(
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
                                rs.getString("num_tel")
                        );
                        user.setLogin(rs.getString("login"));
                        user.setId(rs.getInt("id"));
                        user.setDoitChangerMotDePasse(rs.getInt("doit_changer_mdp") == 1);
                    }
                }
            }

            if (user != null) {
                // ✅ Enregistrement dans la même transaction
                Consultation consultation = new Consultation(user);
                ConsultationDAO.enregistrerConsultation(conn, consultation);
            }

            conn.commit(); // ✅ OK
        } catch (Exception e) {
            e.printStackTrace();
        }

        return user;
    }

    public void save(Utilisateur utilisateur, String login, String motDePasseClair) {
        if (userExists(login)) {
            System.out.println("⚠️ Utilisateur déjà existant : " + login);
            return;
        }

        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);

            String sqlUtilisateur = "INSERT INTO utilisateur(" +
                    "login, mot_de_passe, nom, prenom, date_naissance, nationalite, " +
                    "est_inscrit, est_valide, doit_changer_mdp, email, numero_securite, carte_identite, photo_numerique" +
                    ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(sqlUtilisateur, Statement.RETURN_GENERATED_KEYS)) {
                String hash = BCrypt.hashpw(utilisateur.getPrenom().toLowerCase(), BCrypt.gensalt());

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

                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int id = rs.getInt(1);
                        utilisateur.setId(id);

                        String sqlPersonne = "INSERT INTO personne(id_personne, nom, prenom, date_naissance, nationalite, age) VALUES (?, ?, ?, ?, ?, ?)";

                        try (PreparedStatement stmtPersonne = conn.prepareStatement(sqlPersonne)) {
                            stmtPersonne.setInt(1, id);
                            stmtPersonne.setString(2, utilisateur.getNom());
                            stmtPersonne.setString(3, utilisateur.getPrenom());
                            stmtPersonne.setString(4, utilisateur.getDateNaissance().toString());
                            stmtPersonne.setString(5, utilisateur.getNationalite().name());
                            stmtPersonne.setInt(6, utilisateur.getAge());

                            stmtPersonne.executeUpdate();
                        }

                        System.out.println("✅ Utilisateur et personne associés (id = " + id + ")");
                    }
                }
            }

            conn.commit(); // 🔒 fin de transaction
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean userExists(String login) {
        String sql = "SELECT COUNT(*) FROM utilisateur WHERE login = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
