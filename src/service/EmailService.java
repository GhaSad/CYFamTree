package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {


    public static void envoyerMail(String destinataire, String sujet, String corps) {
        final String expediteur = "cyfamtree@gmail.com";     // ⚠️ Adresse email de l'expéditeur
        final String motDePasse = "upvvgnywqvpaxmug";        // ⚠️ Mot de passe d'application (jamais le mot de passe principal !)

        // Configuration des propriétés SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");                    // Authentification requise
        props.put("mail.smtp.starttls.enable", "true");         // Utilisation de TLS
        props.put("mail.smtp.host", "smtp.gmail.com");          // Serveur SMTP Gmail
        props.put("mail.smtp.port", "587");                     // Port TLS

        // Création d'une session avec authentification
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(expediteur, motDePasse);
            }
        });

        try {
            // Construction du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(expediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(corps);

            // Envoi du message
            Transport.send(message);
            System.out.println("📧 Email envoyé à " + destinataire);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
