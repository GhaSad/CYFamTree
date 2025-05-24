package tests;

import model.TypeLien;
import model.Lien;
import model.LienEnAttente;
import model.Nationalite;
import model.Utilisateur;
import model.Personne;
import service.*;
import view.*;

import java.time.LocalDate;

public class LienMainTest {
    public static void main(String[] args) {
        // Création de deux utilisateurs
        Utilisateur alice = new Utilisateur("Alice", "Durand", LocalDate.of(1990, 1, 1), Nationalite.FRANCAIS, 34, true, true,"email@gage.com","12233","12434","aofaeof","1461645646","ara");
        Utilisateur bob = new Utilisateur("Bob", "Martin", LocalDate.of(1995, 1, 1), Nationalite.FRANCAIS, 29, true, true,"email@gage.com","12233","12434","aofaeof","191643464","pkp");

        alice.setEmail("alice@example.com");
        bob.setEmail("bob@example.com");

        // Alice demande un lien avec Bob
        Lien lien = new Lien(alice, bob, TypeLien.FRERE);
        boolean resultat = lien.estValideAvancee().isValide(); // Cela déclenche l'ajout dans LienManager

        System.out.println("Lien validé immédiatement ? " + resultat);

        // Bob consulte ses demandes en attente
        for (LienEnAttente demande : service.LienManager.getDemandesPour(bob)) {
            System.out.println("Demande reçue de " + demande.getDemandeur().getPrenom()
                + " pour être " + demande.getTypeLien());

            // Bob accepte la demande
            service.LienManager.validerDemande(demande);
        }

        // Vérification que le lien a bien été créé (liens ajoutés)
        System.out.println("Bob a comme frère(s) :");
        for (Personne frere : bob.getFreres()) {
            System.out.println(frere.getPrenom());
        }
    }
}
