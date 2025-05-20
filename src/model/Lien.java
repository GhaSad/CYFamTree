package model;

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
    public boolean estValide() {
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
            return false; 
        }
        
        switch (this.typeLien) {
            case PERE:
            case MERE:
                // Le parent doit être né avant l'enfant
                if (source.getDateNaissance().isAfter(lie.getDateNaissance())) {
                    return false;
                }
                // Le parent doit avoir au moins 15 ans à la naissance de l'enfant
                int ageDifference = lie.getDateNaissance().getYear() - source.getDateNaissance().getYear();
                if (ageDifference < 15) {
                    return false;
                }
                break;

            case FILS:
            case FILLE:
                // L'enfant doit être plus jeune que le parent
                if (source.getDateNaissance().isBefore(lie.getDateNaissance())) {
                    return false;
                }
                // L'enfant ne peut pas être parent de son propre parent
                if (lie.getLiens().stream().anyMatch(l -> l.getPersonneLiee().equals(source)
                        && (l.getTypeLien() == TypeLien.PERE || l.getTypeLien() == TypeLien.MERE))) {
                    return false;
                }
                break;

            case FRERE:
            case SOEUR:
                // Écarter les écarts d'âge irréalistes entre frères/soeurs (> 40 ans)
                int diff = Math.abs(source.getDateNaissance().getYear() - lie.getDateNaissance().getYear());
                if (diff > 40) {
                    return false;
                }
                break;

            case TANTE:
            case ONCLE:
                // Tantes/oncles doivent être plus âgés que leurs neveux/nièces
                if (source.getDateNaissance().isAfter(lie.getDateNaissance())) {
                    return false;
                }
                break;

            case GRAND_PERE:
            case GRAND_MERE:
                // Le grand-parent doit être plus âgé que le parent de la personne liée
                if (source.getDateNaissance().isAfter(lie.getDateNaissance())) {
                    return false;
                }
                // Et avoir au moins 30 ans d'écart
                if ((lie.getDateNaissance().getYear() - source.getDateNaissance().getYear()) < 30) {
                    return false;
                }
                break;

            default:
                break;
        }

        // Vérifier qu'il n'existe pas déjà un lien identique
        boolean lienExistant = source.getLiens().stream().anyMatch(l ->
            l.getPersonneLiee().equals(lie) && l.getTypeLien() == this.typeLien
        );
        if (lienExistant) {
            return false;
        }

        // Éviter les cycles directs (ex : A est parent de B et B est parent de A)
        boolean cycleInvalide = lie.getLiens().stream().anyMatch(l ->
            l.getPersonneLiee().equals(source) &&
            ((l.getTypeLien() == TypeLien.PERE || l.getTypeLien() == TypeLien.MERE) &&
             (this.typeLien == TypeLien.PERE || this.typeLien == TypeLien.MERE))
        );
        if (cycleInvalide) {
            return false;
        }

        return true;
    }


    @Override
    public String toString() {
        return typeLien + " : " + personneLiee.getPrenom() + " " + personneLiee.getNom();
    }
}
