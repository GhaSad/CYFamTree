package tests;
import model.*;
import java.time.LocalDate;
import java.util.*;





public class PersonneTest {
	public static void main(String[] args) {
		
		LocalDate dateNaissance = LocalDate.of(2003, 10, 24);
        Personne parent = new Personne("Guillarme", "Arno", dateNaissance, Nationalite.FRANCAIS, 29);
        Personne enfant = new Personne("Martin", "Julie", LocalDate.of(1970, 3, 2), Nationalite.FRANCAIS, 54);
        Personne frere = new Personne("Dupont", "Jean", LocalDate.of(2010, 8, 22), Nationalite.FRANCAIS, 13);
        
        parent.creerLien(enfant, TypeLien.FILLE);
        enfant.creerLien(parent, TypeLien.PERE);
        enfant.creerLien(frere, TypeLien.FRERE);
        frere.creerLien(parent, TypeLien.PERE);
        frere.creerLien(enfant, TypeLien.FRERE);

        // Création des Noeuds associés
        Noeud noeud1 = new Noeud(parent);
        Noeud noeud2 = new Noeud(enfant);
        Noeud noeud3 = new Noeud(frere);
        
        System.out.println("Liens de Arno :");
        afficherLiens(parent.getLiens());
        System.out.println(parent.getEnfants());
        

        System.out.println("\nLiens de Julie :");
        afficherLiens(enfant.getLiens());
        System.out.println(enfant.getFreres());

        System.out.println("\nLiens de Jean :");
        afficherLiens(frere.getLiens());
        System.out.println(frere.getParents());
        
        

        // Changement de visibilité
        noeud1.changerVisibilite(Visibilite.PUBLIC);
        noeud2.changerVisibilite(Visibilite.PRIVATE);

        // Ajout de parents et enfants (implémentation fictive ici)
        //noeud1.ajouterParent(p2);       // p2 est le parent de p1
        //noeud1.ajouterEnfant(noeud3);   // p3 est l'enfant de p1

        System.out.println("Tests terminés pour Noeud.");
    }
	
	private static void afficherLiens(List<Lien> liens) {
        for (Lien lien : liens) {
            System.out.println("- " + lien.getTypeLien() + " : " +
                    lien.getPersonneLiee().getPrenom() + " " + lien.getPersonneLiee().getNom());
        }
    }

    private static boolean contientLien(List<Lien> liens, Personne personne, TypeLien type) {
        for (Lien lien : liens) {
            if (lien.getPersonneLiee().equals(personne) && lien.getTypeLien() == type) {
                return true;
            }
        }
        return false;
    }
}