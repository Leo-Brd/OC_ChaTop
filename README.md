# ChaTop - API back-end

API REST du portail de location saisonnière **ChaTop**. Elle alimente le front-end Angular existant (mis en relation de locataires et de propriétaires) : inscription et connexion, consultation et publication de locations avec photo, et envoi de messages au propriétaire.

## Sommaire

- [Stack technique](#stack-technique)
- [Architecture](#architecture)
- [Prérequis](#prérequis)
- [Installation](#installation)
- [Lancer l'application](#lancer-lapplication)
- [Documentation de l'API (Swagger)](#documentation-de-lapi-swagger)
- [Routes](#routes)
- [Sécurité](#sécurité)
- [Utiliser le front-end Angular](#utiliser-le-front-end-angular)
- [Tests](#tests)

## Stack technique

| Élément | Version |
|---|---|
| Java | 17 |
| Spring Boot | 4.1.1 (Web MVC, Data JPA, Validation) |
| Spring Security | authentification par JWT (OAuth2 resource server) |
| Base de données | MySQL 8 |
| Documentation | springdoc-openapi 3.1.1 (OpenAPI 3 / Swagger UI) |
| Build | Maven (wrapper `mvnw` fourni) |

## Architecture

Architecture en couches `Controller -> Service -> Repository` : les contrôleurs ne contiennent aucune logique métier et les entités JPA ne sont jamais exposées (des DTO sont renvoyés).

```
src/main/java/com/openclassrooms/chatop/
├── controller/     Routes HTTP (Auth, Rental, Message, User)
├── service/        Logique métier, JWT, stockage des images
├── repository/     Accès aux données (Spring Data JPA)
├── model/          Entités JPA : User, Rental, Message
├── dto/            Corps de requête et de réponse
├── exception/      Exceptions métier et gestionnaire d'erreurs global
└── configuration/  Sécurité JWT, Swagger, accès aux images
```

## Prérequis

- JDK 17 ou supérieur (`JAVA_HOME` doit pointer vers un **JDK**, pas un JRE)
- MySQL 8
- Git

Maven n'est pas à installer : le wrapper `./mvnw` (Windows : `mvnw.cmd`) est fourni.

## Installation

### 1. Récupérer le projet

```bash
git clone https://github.com/Leo-Brd/OC_ChaTop.git
cd OC_ChaTop
```

### 2. Créer la base de données

Connectez-vous à MySQL avec un compte administrateur (`mysql -u root -p`), puis créez la base et un utilisateur **à droits limités** (l'application n'utilise jamais `root`) :

```sql
CREATE DATABASE chatop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'chatop_user'@'localhost' IDENTIFIED BY 'un_mot_de_passe_fort';
GRANT SELECT, INSERT, UPDATE, DELETE ON chatop_db.* TO 'chatop_user'@'localhost';
```

Créez ensuite les tables avec le script fourni, toujours avec le compte administrateur (l'utilisateur de l'application n'a pas le droit de créer des tables) :

```bash
mysql -u root -p chatop_db < ressources/sql/script.sql
```

Sous PowerShell, l'opérateur `<` n'existe pas : utilisez `mysql -u root -p chatop_db -e "source ressources/sql/script.sql"`.

Le script crée les tables `USERS`, `RENTALS` et `MESSAGES` avec leurs clés étrangères. Au démarrage, Hibernate vérifie que les entités correspondent aux tables (`ddl-auto=validate`) mais ne les modifie jamais.

### 3. Configurer les variables d'environnement

Aucun identifiant n'est écrit dans le code. Copiez le modèle puis renseignez vos valeurs :

```bash
cp .env.example .env
```

| Variable | Rôle |
|---|---|
| `DB_USER` | Utilisateur MySQL (`chatop_user`) |
| `DB_PASSWORD` | Mot de passe de cet utilisateur |
| `JWT_SECRET` | Clé de signature des tokens, **32 caractères minimum** |

Pour générer un secret :

```bash
openssl rand -base64 48
```

Le fichier `.env` est lu automatiquement au démarrage (format `NOM=valeur`, sans guillemets) et il est ignoré par Git. Des variables d'environnement système du même nom sont aussi acceptées et restent prioritaires.

## Lancer l'application

Depuis la racine du projet (là où se trouve le fichier `.env`) :

```bash
./mvnw spring-boot:run        # Windows PowerShell : .\mvnw spring-boot:run
```

L'API écoute sur **http://localhost:3001**. Les images envoyées sont stockées dans le dossier `uploads/` (ignoré par Git).

Paramètres modifiables dans [application.properties](src/main/resources/application.properties) :

| Propriété | Valeur par défaut | Rôle |
|---|---|---|
| `server.port` | `3001` | Port HTTP |
| `app.base-url` | `http://localhost:3001` | Début de l'URL enregistrée pour les images |
| `app.upload-dir` | `uploads` | Dossier de stockage des images |
| `jwt.expiration-seconds` | `86400` | Durée de validité d'un token (24 h) |
| `spring.servlet.multipart.max-file-size` | `5MB` | Taille maximale d'une image |

## Documentation de l'API (Swagger)

Une fois l'application lancée :

- Interface Swagger UI : http://localhost:3001/swagger-ui.html
- Description OpenAPI (JSON) : http://localhost:3001/v3/api-docs

La documentation est publique, mais les routes protégées exigent un token. Pour les tester dans Swagger :

1. Appelez `POST /api/auth/register` (ou `POST /api/auth/login`) et copiez la valeur de `token` dans la réponse.
2. Cliquez sur **Authorize** en haut de la page.
3. Collez le token (sans le préfixe `Bearer`) puis validez.

Les routes protégées affichent alors un cadenas fermé et fonctionnent depuis « Try it out ».

## Routes

Toutes les routes sont préfixées par `/api`. « Token » signifie que l'en-tête `Authorization: Bearer <token>` est obligatoire.

| Méthode | URL | Token | Description | Réponses |
|---|---|---|---|---|
| POST | `/api/auth/register` | Non | Crée un compte et renvoie un token | 200, 400 |
| POST | `/api/auth/login` | Non | Connecte l'utilisateur et renvoie un token | 200, 401 |
| GET | `/api/auth/me` | Oui | Utilisateur connecté | 200, 401 |
| GET | `/api/user/{id}` | Oui | Informations d'un utilisateur | 200, 401, 404 |
| GET | `/api/rentals` | Oui | Liste des locations | 200, 401 |
| GET | `/api/rentals/{id}` | Oui | Détail d'une location | 200, 401, 404 |
| POST | `/api/rentals` | Oui | Crée une location avec sa photo (`multipart/form-data`) | 200, 400, 401 |
| PUT | `/api/rentals/{id}` | Oui | Modifie une location, réservé à son propriétaire (`multipart/form-data`) | 200, 400, 401, 403, 404 |
| POST | `/api/messages` | Oui | Envoie un message à propos d'une location | 200, 400, 401, 403, 404 |
| GET | `/images/{fichier}` | Non | Image d'une location | 200, 404 |

Corps des erreurs : `{}` pour un 400, 403, 404 ou 413 ; `{"message": "error"}` pour un 401 de connexion. Ils reprennent ceux de l'environnement Mockoon du front-end.

Le fichier [api_model.odt](api_model.odt) contient la définition d'API initiale.

## Sécurité

- **JWT** : toutes les routes exigent un token valide, sauf l'inscription, la connexion, Swagger et les images. Les sessions sont désactivées (`STATELESS`).
- **Mots de passe** : chiffrés avec BCrypt avant d'être enregistrés ; ils ne sont jamais renvoyés par l'API.
- **Identifiants** : base de données et clé JWT sont lues depuis `.env` ou l'environnement, jamais dans le code.
- **Base de données** : l'application se connecte avec un utilisateur limité à `SELECT`, `INSERT`, `UPDATE` et `DELETE`.
- **Messages et locations** : l'auteur d'un message est l'utilisateur du token (le `user_id` envoyé doit lui correspondre, sinon 403), et seul le propriétaire peut modifier sa location.
- **Images** : seules les extensions `jpg`, `jpeg`, `png`, `gif` et `webp` avec un type `image/*` sont acceptées. Les fichiers sont enregistrés sous un nom aléatoire, jamais sous le nom d'origine.
- **Images publiques** : `/images/**` est accessible sans token, car une balise `<img>` ne peut pas envoyer d'en-tête `Authorization`. Les noms aléatoires ne sont pas devinables.

## Utiliser le front-end Angular

Le front-end n'est pas modifié. Son proxy redirige `/api/**` vers `http://localhost:3001`, le port de cette API.

1. Arrêtez Mockoon (il utilise le même port 3001).
2. Lancez cette API (voir plus haut).
3. Dans le dépôt du front-end :
   ```bash
   npm install
   npm run start
   ```
4. Ouvrez http://localhost:4200.

## Tests

```bash
./mvnw test
```

Les tests unitaires des services (`AuthServiceTest`, `RentalServiceTest`, `MessageServiceTest`, `FileStorageServiceTest`) n'ont pas besoin de base de données. Le test `ChatopApplicationTests` démarre l'application complète : il nécessite MySQL et le fichier `.env` configurés comme décrit plus haut.
