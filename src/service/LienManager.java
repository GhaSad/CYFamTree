package service;

import model.LienEnAttente;
import model.Personne;
import model.TypeLien;

import java.util.ArrayList;
import java.util.List;

public class LienManager {
    private static final List<LienEnAttente> demandesEnAttente = new ArrayList<>();

    public static void ajouterDemande(LienEnAttente demande) {
        demandesEnAttente.add(demande);
    }

    public static void validerDemande(LienEnAttente demande) {
        Personne source = demande.getDemandeur();
        Personne cible = demande.getCible();
        TypeLien type = demande.getTypeLien();

        source.creerLienDirect(cible, type);

        TypeLien inverse = getLienInverse(type);
        if (inverse != null) {
            cible.creerLienDirect(source, inverse);
        } else if (estLienSymetrique(type)) {
            cible.creerLienDirect(source, type);
        }

        demandesEnAttente.remove(demande);
    }


    public static void refuserDemande(LienEnAttente demande) {
        demandesEnAttente.remove(demande);
    }
    
    private static boolean estLienSymetrique(TypeLien type) {
        return type == TypeLien.FRERE || type == TypeLien.SOEUR;
    }
    
    public static TypeLien getLienInverse(TypeLien type) {
        switch (type) {
            case PERE: return TypeLien.FILS; // ou FILLE selon sexe
            case MERE: return TypeLien.FILS; // ou FILLE selon sexe
            case FILS: return TypeLien.PERE; // ou MERE
            case FILLE: return TypeLien.MERE; // ou PERE
            case GRAND_PERE: return TypeLien.PETIT_FILS;
            case GRAND_MERE: return TypeLien.PETITE_FILLE;
            case ONCLE: return TypeLien.NEVEU;
            case TANTE: return TypeLien.NIECE;
            case NEVEU: return TypeLien.ONCLE;
            case NIECE: return TypeLien.TANTE;
            case PETIT_FILS: return TypeLien.GRAND_PERE;
            case PETITE_FILLE: return TypeLien.GRAND_MERE;
            case FRERE:
            case SOEUR:
                return type; // symétrique
            default:
                return null;
        }
    }
    

    public static List<LienEnAttente> getDemandesPour(Personne cible) {
        List<LienEnAttente> result = new ArrayList<>();
        for (LienEnAttente d : demandesEnAttente) {
            if (d.getCible().equals(cible)) {
                result.add(d);
            }
        }
        return result;
    }
}
