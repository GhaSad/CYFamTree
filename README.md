
# 🔧 Arbre Généalogique Pro++ - Java Project

Bienvenue dans le projet **Arbre Généalogique Pro++**, une application Java permettant la création et la gestion d’arbres généalogiques de manière simple, interactive et graphique.

---

## 🚀 Objectif du projet

Créer une application Java :
- Orientée objet (POO)
- Avec interface **console** et **graphique (JavaFX/Swing)**
- Supportant l'ajout, la modification, la visualisation et la suppression de membres familiaux
- Intégrant des fonctionnalités avancées : filtres, visibilité, requêtes généalogiques, etc.

---

## 🔄 Synchronisation avec GitHub (tutoriel)

### Prérequis
- Eclipse IDE
- Git installé localement
- Dépôt GitHub existant (contenant par ex. un `README.md`)

### Étapes

```bash
# 1. Initialisation Git (si ce n’est pas encore fait)
git init

# 2. Lier le dépôt distant
git remote add origin https://github.com/GhaSad/CYFamTree

# 3. Récupérer les fichiers distants
git pull origin main --allow-unrelated-histories

# 4. Ajouter le code local
git add .
git commit -m "Ajout du projet Java initial"

# 5. Pousser vers GitHub
git push --set-upstream origin main
```

> Remplacez `main` par `master` si nécessaire.

### Authentification GitHub

⚠️ GitHub ne permet plus l’authentification par mot de passe. Utilisez un **Personal Access Token (PAT)** comme mot de passe.  
Générez-le ici : [https://github.com/settings/tokens](https://github.com/settings/tokens)

---

## 📁 Architecture actuelle du projet

```
ArbreGenealogiquePro/
├── pom.xml                   # Fichier Maven (Java 17, dépendances)
├── .gitignore                # À ajouter : ignore les fichiers Eclipse et Maven
├── src/
│   ├── model/                # Entités métier : Personne, Utilisateur, Arbre, etc.
│   ├── view/                 # Interfaces console et graphiques
│   ├── controller/           # Logique d'application (actions utilisateurs)
│   ├── utils/                # Outils divers (validateurs, règles métier)
│   └── tests/                # Tests unitaires (JUnit 5)
└── README.md                 # Ce fichier
```

---

## 🧪 Technologies utilisées

- Java 17
- Maven
- JUnit 5
- JavaFX (ou Swing)
- Git & GitHub

---

## ✍️ Auteur

Projet réalisé par l'équipe [CYTech GI ING1], année 2024-2025  
Encadré par Zaouche Djaouida  
Dépôt GitHub : [https://github.com/GhaSad/CYFamTree](https://github.com/GhaSad/CYFamTree)

---

Pour toute contribution ou question, merci d’ouvrir une *issue* ou une *pull request*.
