package model;

import java.time.Period;

public class Lien {
	private Personne source;
    private Personne personneLiee;
    private TypeLien typeLien;
    
    public Lien(Personne source ,Personne personneLiee, TypeLien typeLien) {
        this.source = source;
    	this.personneLiee = personneLiee;
        this.typeLien = typeLien;
    }

    public Personne getPersonneLiee() {
        return personneLiee;
    }
    public Personne getSource() {
    	return source;
    }

    public TypeLien getTypeLien() {
        return typeLien;
    }
    
    
    public utils.ValidationResult estValideAvancee() {
        Personne source = this.getSource();
        Personne lie = this.getPersonneLiee();

        if (lie.estInscrit()) {
            lie.asUtilisateur().ifPresent(destinataire -> {
                String sujet = "Demande de confirmation de lien de parenté";
                String corps = "Bonjour " + destinataire.getPrenom() + ",\n\n" +
                        source.getPrenom() + " souhaite vous ajouter comme " +
                        this.typeLien.name().toLowerCase() + " dans son arbre généalogique.\n" +
                        "Merci de vous connecter à votre espace pour accepter ou refuser cette demande.";

                utils.EmailService.envoyerEmail(destinataire.getEmail(), sujet, corps);
            });
            service.LienManager.ajouterDemande(new LienEnAttente(source, lie, this.typeLien));
            return new utils.ValidationResult(false, "Lien soumis à validation par l'utilisateur ciblé.");
        }

        switch (this.typeLien) {
        case PERE:
        case MERE:
            System.out.println(">>> [DEBUG] Début validation lien PERE/MERE");
            System.out.println(">>> [DEBUG] Source = " + source.getPrenom() + " (id = " + source.getId() + ")");
            System.out.println(">>> [DEBUG] Cible (enfant) = " + lie.getPrenom() + " (id = " + lie.getId() + ")");

            if ((this.typeLien == TypeLien.PERE || this.typeLien == TypeLien.MERE)
                    && lie.getId() > 0
                    && dao.LienDAO.aDejaDeuxParents(lie.getId())) {
                System.out.println(">>> [DEBUG] Refus : déjà 2 parents trouvés en base pour l’enfant.");
                return new utils.ValidationResult(false, "Cette personne a déjà deux parents définis.");
            }

            if (!source.getDateNaissance().isBefore(lie.getDateNaissance())) {
                System.out.println(">>> [DEBUG] Refus : le parent est plus jeune ou même âge que l’enfant.");
                return new utils.ValidationResult(false, "Le parent doit être plus âgé que l'enfant.");
            }

            int ageDiff = Period.between(source.getDateNaissance(), lie.getDateNaissance()).getYears();
            System.out.println(">>> [DEBUG] Différence d’âge calculée : " + ageDiff + " ans");

            if (ageDiff < 15) {
                System.out.println(">>> [DEBUG] Refus : écart d’âge < 15 ans.");
                return new utils.ValidationResult(false, "Le parent doit avoir au moins 15 ans à la naissance de l'enfant.");
            }

            if (ageDiff > 80) {
                System.out.println(">>> [DEBUG] Refus : écart d’âge > 80 ans.");
                return new utils.ValidationResult(false, "L'écart d'âge entre parent et enfant est irréaliste.");
            }

            System.out.println(">>> [DEBUG] Lien PERE/MERE validé.");
            break;



        case FILS:
        case FILLE:
            System.out.println(">>> [DEBUG] Début validation lien FILS/FILLE");
            System.out.println(">>> [DEBUG] Source = " + source.getPrenom() + " (id = " + source.getId() + ", né(e) le " + source.getDateNaissance() + ")");
            System.out.println(">>> [DEBUG] Cible (enfant) = " + lie.getPrenom() + " (id = " + lie.getId() + ", né(e) le " + lie.getDateNaissance() + ")");

            if (!lie.getDateNaissance().isAfter(source.getDateNaissance())) {
                System.out.println(">>> [DEBUG] Refus : l’enfant est plus âgé ou né le même jour que le parent.");
                return new utils.ValidationResult(false, "L'enfant doit être plus jeune que le parent.");
            }

            int ageAtNaissance = Period.between(source.getDateNaissance(), lie.getDateNaissance()).getYears();
            System.out.println(">>> [DEBUG] Âge du parent à la naissance de l’enfant : " + ageAtNaissance + " ans");

            if (ageAtNaissance < 13) {
                System.out.println(">>> [DEBUG] Refus : le parent aurait eu l’enfant à " + ageAtNaissance + " ans (< 13).");
                return new utils.ValidationResult(false, "Le parent ne peut pas avoir un enfant à moins de 13 ans.");
            }

            System.out.println(">>> [DEBUG] Lien FILS/FILLE validé.");
            break;


            case FRERE:
            case SOEUR:
                int diff = Math.abs(source.getDateNaissance().getYear() - lie.getDateNaissance().getYear());
                if (diff > 40) {
                    return new utils.ValidationResult(false, "L'écart d'âge entre frères ou sœurs dépasse 40 ans.");
                }
                break;

            case TANTE:
            case ONCLE:
                if (!source.getDateNaissance().isBefore(lie.getDateNaissance())) {
                    return new utils.ValidationResult(false, "Un oncle ou une tante doit être plus âgé que le neveu ou la nièce.");
                }
                break;

            case GRAND_PERE:
            case GRAND_MERE:
                if (!source.getDateNaissance().isBefore(lie.getDateNaissance())) {
                    return new utils.ValidationResult(false, "Le grand-parent doit être plus âgé que le petit-enfant.");
                }
                int ecart = lie.getDateNaissance().getYear() - source.getDateNaissance().getYear();
                if (ecart < 30) {
                    return new utils.ValidationResult(false, "Un grand-parent doit avoir au moins 30 ans d'écart avec son petit-enfant.");
                }
                break;

            default:
                break;
        }

        // Vérification de doublon
        boolean lienExistant = source.getLiens().stream().anyMatch(l ->
                l.getPersonneLiee().equals(lie) && l.getTypeLien() == this.typeLien
        );
        if (lienExistant) {
            return new utils.ValidationResult(false, "Un lien de ce type existe déjà entre ces deux personnes.");
        }

        // Éviter les cycles directs (parentalité réciproque)
        boolean cycleInvalide = lie.getLiens().stream().anyMatch(l ->
                l.getPersonneLiee().equals(source) &&
                ((l.getTypeLien() == TypeLien.PERE || l.getTypeLien() == TypeLien.MERE) &&
                 (this.typeLien == TypeLien.PERE || this.typeLien == TypeLien.MERE))
        );
        if (cycleInvalide) {
            return new utils.ValidationResult(false, "Ce lien créerait une relation parentale circulaire invalide.");
        }

        return new utils.ValidationResult(true, "Lien valide.");
    }

    @Override
    public String toString() {
        return typeLien + " : " + personneLiee.getPrenom() + " " + personneLiee.getNom();
    }
}
