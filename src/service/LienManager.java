package service;

import model.LienEnAttente;
import model.Personne;
import model.TypeLien;
import model.Utilisateur;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe utilitaire de gestion des demandes de lien de parenté entre personnes ou utilisateurs.
 * <p>
 * Cette classe stocke en mémoire les demandes de lien qui doivent être validées manuellement,
 * par exemple lorsqu'un utilisateur souhaite ajouter un autre utilisateur à son arbre.
 * </p>
 */
public class LienManager {

    /** Liste statique de toutes les demandes de lien en attente */
    private static final List<LienEnAttente> demandesEnAttente = new ArrayList<>();

    /**
     * Ajoute une nouvelle demande de lien à la liste des demandes en attente.
     *
     * @param demande LienEnAttente à ajouter
     */
    public static void ajouterDemande(LienEnAttente demande) {
        demandesEnAttente.add(demande);
    }

    /**
     * Valide une demande de lien en créant le lien entre les deux personnes,
     * et en générant le lien inverse si applicable.
     * <p>
     * La demande est ensuite retirée de la liste.
     *
     * @param demande La demande de lien à valider
     */
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

    /**
     * Supprime une demande de la liste sans la valider.
     *
     * @param demande la demande à refuser
     */
    public static void refuserDemande(LienEnAttente demande) {
        demandesEnAttente.remove(demande);
    }

    /**
     * Retourne les demandes de lien en attente pour un utilisateur donné.
     *
     * @param cible L'utilisateur cible des demandes
     * @return Liste des demandes où l'utilisateur est la cible
     */
    public static List<LienEnAttente> getDemandesPour(Utilisateur cible) {
        List<LienEnAttente> result = new ArrayList<>();
        for (LienEnAttente d : demandesEnAttente) {
            if (d.getCible() instanceof Utilisateur && d.getCible().equals(cible)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Retourne les demandes de lien en attente pour une personne donnée (utilisée dans les cas généraux).
     *
     * @param cible La personne cible
     * @return Liste des demandes concernant cette personne
     */
    public static List<LienEnAttente> getDemandesPour(Personne cible) {
        List<LienEnAttente> result = new ArrayList<>();
        for (LienEnAttente d : demandesEnAttente) {
            if (d.getCible().equals(cible)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Indique si un lien est symétrique (par exemple, frère <-> frère).
     *
     * @param type Le type de lien à tester
     * @return true si le lien est symétrique
     */
    private static boolean estLienSymetrique(TypeLien type) {
        return type == TypeLien.FRERE || type == TypeLien.SOEUR;
    }

    /**
     * Retourne le lien inverse d’un lien donné (ex. : FILLE ↔ MERE).
     *
     * @param type Le type de lien original
     * @return Le lien inverse correspondant, ou null si aucun n’est défini
     */
    public static TypeLien getLienInverse(TypeLien type) {
        switch (type) {
            case PERE: return TypeLien.FILS;
            case MERE: return TypeLien.FILS;
            case FILS: return TypeLien.PERE;
            case FILLE: return TypeLien.MERE;
            case GRAND_PERE: return TypeLien.PETIT_FILS;
            case GRAND_MERE: return TypeLien.PETITE_FILLE;
            case PETIT_FILS: return TypeLien.GRAND_PERE;
            case PETITE_FILLE: return TypeLien.GRAND_MERE;
            default:
                return null;
        }
    }
}
