package view;
import javax.swing.*;

public class AdminLoginDialog {

    private static final String ADMIN_PASSWORD = "admin123"; // Mot de passe de base (à sécuriser en prod)

    public static boolean showLoginDialog() {
        JPasswordField pwd = new JPasswordField(10);
        int action = JOptionPane.showConfirmDialog(null, pwd, "Mot de passe admin", JOptionPane.OK_CANCEL_OPTION);
        if (action == JOptionPane.OK_OPTION) {
            String password = new String(pwd.getPassword());
            if (ADMIN_PASSWORD.equals(password)) {
                return true;
            } else {
                JOptionPane.showMessageDialog(null, "Mot de passe incorrect", "Erreur", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }
}
