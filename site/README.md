# Générateur de Quiz JSON

Site statique (HTML/CSS/JS) permettant de créer, éditer et exporter des fichiers JSON
compatibles avec l'application Android de quiz.

## Format JSON généré

```json
{
  "titre": "Culture générale",
  "description": "Un questionnaire test",
  "questions": [
    {
      "question": "Quelle est la capitale de la France ?",
      "bonneReponse": "Paris",
      "mauvaiseReponse": ["Lyon", "Marseille", "Toulouse"]
    }
  ]
}
```

## Fonctionnalités

- Ajout / suppression de questions
- Exactement 3 mauvaises réponses par question
- Import d'un JSON existant pour le modifier
- Aperçu du JSON avant export
- Export en fichier `.json` téléchargeable
- Validation basique avant export (titre, questions non vides, etc.)
- Bouton de téléchargement de l'APK depuis la dernière release GitHub du dépôt de l'application

## Configuration

Le site lit une variable d'environnement `REPO_URL` (URL complète du dépôt GitHub,
ex. `https://github.com/<owner>/<repo>`) pour aller chercher automatiquement l'APK
de la dernière release via l'API GitHub (`/releases/latest`). Le bouton de
téléchargement n'apparaît que si une release contient un fichier `.apk` en asset.

Cette variable est injectée dans `config.js` au démarrage du container (voir
`generate-config.sh`), donc pas besoin de rebuild l'image pour changer de dépôt.

## Lancer en local (sans Docker)

Ouvrir simplement `index.html` dans un navigateur.

## Lancer avec Docker

```bash
docker compose up -d --build
```

Le site sera accessible sur http://localhost:8080

Pour changer le port, modifier la ligne `ports` dans `docker-compose.yml`.

## Lancer avec Docker (sans compose)

```bash
docker build -t quiz-json-builder .
docker run -d -p 8080:80 -e REPO_URL=https://github.com/<owner>/<repo> --name quiz-json-builder quiz-json-builder
```
