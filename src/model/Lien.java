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

            if ((this.typeLien == TypeLien.PERE || this.typeLien == TypeLien.MERE)
                    && lie.getId() > 0
                    && dao.LienDAO.aDejaDeuxParents(lie.getId())) {
                return new utils.ValidationResult(false, "Cette personne a déjà deux parents définis.");
            }

            if (!source.getDateNaissance().isBefore(lie.getDateNaissance())) {

                return new utils.ValidationResult(false, "Le parent doit être plus âgé que l'enfant.");
            }

            int ageDiff = Period.between(source.getDateNaissance(), lie.getDateNaissance()).getYears();


            if (ageDiff < 15) {
                return new utils.ValidationResult(false, "Le parent doit avoir au moins 15 ans à la naissance de l'enfant.");
            }

            if (ageDiff > 80) {
                return new utils.ValidationResult(false, "L'écart d'âge entre parent et enfant est irréaliste.");
            }

            break;



        case FILS:
        case FILLE:


            if (!lie.getDateNaissance().isAfter(source.getDateNaissance())) {

                return new utils.ValidationResult(false, "L'enfant doit être plus jeune que le parent.");
            }

            int ageAtNaissance = Period.between(source.getDateNaissance(), lie.getDateNaissance()).getYears();


            if (ageAtNaissance < 13) {

                return new utils.ValidationResult(false, "Le parent ne peut pas avoir un enfant à moins de 13 ans.");
            }

            break;


            case FRERE:
            case SOEUR:
                int diff = Math.abs(source.getDateNaissance().getYear() - lie.getDateNaissance().getYear());
                if (diff > 40) {
                    return new utils.ValidationResult(false, "L'écart d'âge entre frères ou sœurs dépasse 40 ans.");
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
