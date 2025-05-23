package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.Properties;

public class EmailService {

    public static void envoyerMail(String destinataire, String sujet, String corps) {
        final String expediteur = "cyfamtree@gmail.com";     // ⚠️ met ton email
        final String motDePasse = "upvvgnywqvpaxmug   ";         // ⚠️ mot de passe d'application

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(expediteur, motDePasse);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(expediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject(sujet);
            message.setText(corps);

            Transport.send(message);
            System.out.println("📧 Email envoyé à " + destinataire);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
