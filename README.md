# Smart Bee House Management System

## Description

Le Smart Bee House Management System est un système complet de gestion apiculture développé en Java J2EE. Il permet aux apiculteurs et fermiers de gérer efficacement leurs ruches, sites d'apiculture, visites d'inspection et production de miel.

## Fonctionnalités Principales

### 🏠 Gestion des Structures
- **Fermiers** : Gestion des propriétaires de fermes apicoles
- **Fermes** : Organisation des exploitations apicoles
- **Sites** : Localisation géographique des ruchers avec coordonnées GPS
- **Ruches** : Gestion détaillée des ruches avec socles et extensions

### 👥 Gestion des Agents
- **Agents apiculteurs** : Techniciens, superviseurs, gestionnaires
- **Hiérarchie** : Système de supervision et d'approbation
- **Spécialisations** : Compétences spécifiques par agent

### 📅 Planification et Suivi
- **Visites programmées** : Planification des inspections
- **Rapports de visite** : Documentation complète des interventions
- **Évaluations** : Notation de la santé, population et productivité
- **Calendrier** : Vue d'ensemble des activités

### 📊 Tableaux de Bord
- **Production** : Suivi des quantités de miel
- **Alertes** : Détection automatique des problèmes
- **Statistiques** : Analyses et graphiques de performance
- **ROI** : Calcul du retour sur investissement

### 🔧 Monitoring Automatisé
- **Capteurs IoT** : Mesures automatiques (poids, température, humidité)
- **Géolocalisation** : Données horodatées et géolocalisées
- **Unités configurables** : Support multi-unités de mesure
- **Historique** : Conservation des données de mesure

## Architecture Technique

### Technologies Utilisées
- **Backend** : Java J2EE, Servlets, JSP, EJB
- **Base de données** : MySQL avec JDBC
- **Frontend** : HTML5, CSS3, JavaScript, Bootstrap 5
- **Services Web** : SOAP/XML Web Services
- **Sécurité** : SSL/TLS, Certificats X.509, OAuth Google
- **Internationalisation** : Support FR/EN/AR via fichiers XML

### Structure du Projet
```
src/
├── main/
│   ├── java/
│   │   └── com/smartbeehouse/
│   │       ├── model/          # Entités métier
│   │       ├── dao/            # Accès aux données
│   │       ├── service/        # Logique métier
│   │       ├── servlet/        # Contrôleurs web
│   │       ├── webservice/     # Services web
│   │       └── filter/         # Filtres HTTP
│   ├── resources/
│   │   ├── messages_*.xml      # Fichiers d'internationalisation
│   │   ├── ConfigBKS.ini       # Configuration système
│   │   └── database/           # Scripts SQL
│   └── webapp/
│       ├── WEB-INF/
│       ├── css/                # Styles CSS
│       ├── js/                 # Scripts JavaScript
│       └── *.jsp               # Pages JSP
```

### Modèle de Données

#### Entités Principales
- **Farmer** : Propriétaire de fermes apicoles
- **Farm** : Exploitation apicole
- **ApiaryySite** : Site géographique de ruches
- **Beehive** : Ruche avec socle et extensions
- **Agent** : Apiculteur responsable
- **Visit** : Visite d'inspection
- **SensorMeasurement** : Mesure de capteur

#### Relations
- Un fermier peut avoir plusieurs fermes
- Une ferme peut avoir plusieurs sites
- Un site peut contenir plusieurs ruches
- Une ruche a un socle et jusqu'à 5 extensions
- Chaque conteneur peut avoir jusqu'à 10 cadres
- Un agent peut être responsable de plusieurs ruches

## Installation et Configuration

### Prérequis
- Java JDK 11+
- Apache Tomcat 9+
- MySQL 8.0+
- Maven 3.6+

### Configuration de la Base de Données
1. Créer la base de données MySQL :
```sql
CREATE DATABASE smart_bee_house CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Exécuter le script de création des tables :
```bash
mysql -u root -p smart_bee_house < src/main/resources/database/schema.sql
```

3. Configurer les paramètres de connexion dans `ConfigBKS.ini`

### Compilation et Déploiement

#### 1. Compilation du projet
```bash
# Nettoyer et compiler
mvn clean compile

# Exécuter les tests
mvn test

# Créer le package WAR
mvn package
```

#### 2. Déploiement sur Tomcat
```bash
# Copier le WAR dans Tomcat
cp target/smart-bee-house.war $TOMCAT_HOME/webapps/

# Ou utiliser le plugin Maven
mvn tomcat7:deploy
```

#### 3. Déploiement avec différents profils
```bash
# Développement (par défaut)
mvn clean package

# Test
mvn clean package -Ptest

# Production
mvn clean package -Pprod
```

### Configuration SSL
1. Générer un certificat SSL :
```bash
keytool -genkey -alias smartbeehouse -keyalg RSA -keystore keystore.jks
```

2. Configurer Tomcat pour SSL dans `server.xml`

3. Mettre à jour les chemins dans `ConfigBKS.ini`

## Utilisation

### Interface Web
- Accès via : `https://localhost:8443/smart-bee-house/`
- Authentification Google OAuth requise
- Interface multilingue (FR/EN/AR)

### API REST
- Endpoint ruches : `/api/beehives`
- Endpoint visites : `/api/visits`
- Endpoint agents : `/api/agents`

### Service Web SOAP
- WSDL : `/ws/honey?wsdl`
- Méthode principale : `getBeeHouseHoneyActualQuantity(int beehiveId)`

## Tests

### Tests Unitaires
```bash
mvn test
```

### Tests d'Intégration
```bash
mvn verify
```

### Plan de Tests
- Tests des DAO avec base de données en mémoire
- Tests des services métier avec mocks
- Tests des servlets avec MockMVC
- Tests des services web avec SoapUI

## Sécurité

### Authentification
- OAuth 2.0 avec Google
- Sessions sécurisées avec cookies HttpOnly
- Timeout de session configurable

### Autorisation
- Rôles : ADMIN, MANAGER, AGENT, FARMER
- Contrôle d'accès basé sur les rôles
- Filtres de sécurité sur les endpoints sensibles

### Chiffrement
- Communication HTTPS obligatoire
- Certificats X.509 pour l'authentification inter-services
- Hachage sécurisé des données sensibles

## Monitoring et Logs

### Logs d'Audit
- Traçabilité complète des actions utilisateur
- Logs de sécurité et d'accès
- Rotation automatique des fichiers de logs

### Métriques
- Monitoring des performances
- Alertes automatiques
- Tableaux de bord de supervision

## Internationalisation

### Langues Supportées
- Français (FR) - par défaut
- Anglais (EN)
- Arabe (AR)

### Configuration
- Fichiers XML dans `/resources/messages_*.xml`
- Changement de langue dynamique
- Support RTL pour l'arabe

## API Documentation

### Endpoints REST

#### Ruches
- `GET /api/beehives` - Liste toutes les ruches
- `GET /api/beehives/{id}` - Détails d'une ruche
- `POST /api/beehives` - Créer une ruche
- `PUT /api/beehives/{id}` - Modifier une ruche
- `DELETE /api/beehives/{id}` - Supprimer une ruche

#### Visites
- `GET /api/visits` - Liste des visites
- `POST /api/visits` - Planifier une visite
- `PUT /api/visits/{id}` - Modifier une visite

### Service Web SOAP

#### HoneyQuantityService
```xml
<soap:operation name="getBeeHouseHoneyActualQuantity">
    <soap:input>
        <beeHouseID>int</beeHouseID>
    </soap:input>
    <soap:output>
        <return>float</return>
    </soap:output>
</soap:operation>
```

## Contribution

### Standards de Code
- Respect des conventions Java
- Documentation Javadoc obligatoire
- Tests unitaires pour toute nouvelle fonctionnalité
- Code review obligatoire

### Processus de Développement
1. Fork du repository
2. Création d'une branche feature
3. Développement avec tests
4. Pull request avec description détaillée
5. Review et merge

## Dépannage

### Problèmes Courants

#### Erreur de compilation Maven
```bash
# Vérifier la version Java
java -version

# Nettoyer le cache Maven
mvn dependency:purge-local-repository

# Recompiler
mvn clean compile
```

#### Problème de base de données
```bash
# Vérifier la connexion MySQL
mysql -u root -p -e "SELECT 1"

# Recréer la base de données
mysql -u root -p -e "DROP DATABASE IF EXISTS smart_bee_house; CREATE DATABASE smart_bee_house;"
```

#### Problème SSL
- Vérifier les certificats dans `ConfigBKS.ini`
- Régénérer le keystore si nécessaire
- Vérifier la configuration Tomcat

## Support

### Documentation
- Javadoc générée : `/docs/api/`
- Guide utilisateur : `/docs/user-guide.pdf`
- Guide administrateur : `/docs/admin-guide.pdf`

### Contact
- Email : support@smartbeehouse.com
- Issues GitHub : [Repository Issues]
- Documentation : [Wiki du projet]

## Licence

Ce projet est sous licence MIT. Voir le fichier `LICENSE` pour plus de détails.

## Changelog

### Version 1.0.0 (2024-01-15)
- Version initiale
- Gestion complète des ruches et visites
- Interface web responsive
- API REST et service web SOAP
- Authentification OAuth Google
- Support multilingue

### Roadmap

#### Version 1.1.0 (Q2 2024)
- Application mobile
- Notifications push
- Intégration IoT avancée
- Rapports PDF automatiques

#### Version 1.2.0 (Q3 2024)
- Intelligence artificielle pour prédictions
- Marketplace de produits apicoles
- Intégration météo
- Module de formation en ligne

---

**Smart Bee House Management System** - Optimisez votre production apicole avec la technologie moderne.