package utils;

public class EmailService {
    public static void envoyerEmail(String destinataire, String sujet, String corps) {
        
    	
        System.out.println("=== 📧 Envoi d’un email ===");
        System.out.println("À      : " + destinataire);
        System.out.println("Sujet  : " + sujet);
        System.out.println("Corps  : " + corps);
        System.out.println("===========================");
    }
}
