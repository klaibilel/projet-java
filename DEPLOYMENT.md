# Guide de Déploiement - Smart Bee House Management System

## 🚀 Instructions de Déploiement

### Correction des Erreurs Maven

Vous avez rencontré deux erreurs principales :

1. **Erreur de commande** : `mvn clear install -U`
2. **Dépendance dupliquée** dans le POM

### ✅ Commandes Correctes

```bash
# ❌ INCORRECT
mvn clear install -U

# ✅ CORRECT
mvn clean install -U
```

### 🔧 Étapes de Déploiement

#### 1. Nettoyer et Compiler
```bash
cd projet-java
mvn clean compile
```

#### 2. Exécuter les Tests
```bash
mvn test
```

#### 3. Créer le Package WAR
```bash
mvn package
```

#### 4. Installation Complète
```bash
mvn clean install
```

### 📋 Vérifications Pré-Déploiement

#### Vérifier Java
```bash
java -version
# Doit afficher Java 11 ou supérieur
```

#### Vérifier Maven
```bash
mvn -version
# Doit afficher Maven 3.6+ avec Java 11+
```

#### Vérifier MySQL
```bash
mysql --version
# Vérifier que MySQL est installé et accessible
```

### 🗄️ Configuration Base de Données

#### 1. Créer la Base de Données
```sql
CREATE DATABASE smart_bee_house 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

#### 2. Créer un Utilisateur
```sql
CREATE USER 'smartbee'@'localhost' IDENTIFIED BY 'password123';
GRANT ALL PRIVILEGES ON smart_bee_house.* TO 'smartbee'@'localhost';
FLUSH PRIVILEGES;
```

#### 3. Exécuter le Script de Création
```bash
mysql -u smartbee -p smart_bee_house < supabase/migrations/20250610132417_fancy_night.sql
```

### ⚙️ Configuration du Système

#### Modifier ConfigBKS.ini
```ini
[DATABASE]
db.url=jdbc:mysql://localhost:3306/smart_bee_house
db.username=smartbee
db.password=password123

[SECURITY]
ssl.enabled=true
oauth.google.client.id=YOUR_GOOGLE_CLIENT_ID
oauth.google.client.secret=YOUR_GOOGLE_CLIENT_SECRET
```

### 🌐 Déploiement sur Tomcat

#### 1. Installation Tomcat 9
```bash
# Windows
# Télécharger depuis https://tomcat.apache.org/download-90.cgi

# Linux/Mac
sudo apt-get install tomcat9  # Ubuntu/Debian
brew install tomcat@9         # macOS
```

#### 2. Déploiement du WAR
```bash
# Copier le fichier WAR
cp target/smart-bee-house.war $TOMCAT_HOME/webapps/

# Ou utiliser Maven
mvn tomcat7:deploy
```

#### 3. Configuration SSL pour Tomcat

Modifier `$TOMCAT_HOME/conf/server.xml` :
```xml
<Connector port="8443" protocol="org.apache.coyote.http11.Http11NioProtocol"
           maxThreads="150" SSLEnabled="true">
    <SSLHostConfig>
        <Certificate certificateKeystoreFile="conf/keystore.jks"
                     certificateKeystorePassword="changeit"
                     type="RSA" />
    </SSLHostConfig>
</Connector>
```

### 🔐 Configuration OAuth Google

#### 1. Créer un Projet Google Cloud
1. Aller sur [Google Cloud Console](https://console.cloud.google.com/)
2. Créer un nouveau projet
3. Activer l'API Google+ 

#### 2. Configurer OAuth 2.0
1. Aller dans "APIs & Services" > "Credentials"
2. Créer des identifiants OAuth 2.0
3. Ajouter les URI de redirection :
   - `https://localhost:8443/smart-bee-house/auth/callback`
   - `https://votre-domaine.com/smart-bee-house/auth/callback`

#### 3. Mettre à Jour la Configuration
```ini
[SECURITY]
oauth.google.client.id=VOTRE_CLIENT_ID
oauth.google.client.secret=VOTRE_CLIENT_SECRET
oauth.google.redirect.uri=https://localhost:8443/smart-bee-house/auth/callback
```

### 🧪 Tests de Déploiement

#### 1. Test de Compilation
```bash
mvn clean compile
# Doit se terminer avec BUILD SUCCESS
```

#### 2. Test des Unités
```bash
mvn test
# Tous les tests doivent passer
```

#### 3. Test d'Intégration
```bash
mvn verify
# Vérification complète du build
```

#### 4. Test de l'Application
```bash
# Démarrer Tomcat
$TOMCAT_HOME/bin/startup.sh  # Linux/Mac
$TOMCAT_HOME/bin/startup.bat # Windows

# Accéder à l'application
curl -k https://localhost:8443/smart-bee-house/
```

### 📊 Vérification du Déploiement

#### 1. Vérifier les Logs Tomcat
```bash
tail -f $TOMCAT_HOME/logs/catalina.out
```

#### 2. Tester l'API REST
```bash
# Test de l'endpoint des ruches
curl -k https://localhost:8443/smart-bee-house/api/beehives

# Test du service web SOAP
curl -k https://localhost:8443/smart-bee-house/ws/honey?wsdl
```

#### 3. Tester l'Interface Web
- Ouvrir : `https://localhost:8443/smart-bee-house/`
- Vérifier l'authentification Google
- Tester les fonctionnalités principales

### 🐛 Dépannage

#### Problème : Port 8080 déjà utilisé
```bash
# Trouver le processus
netstat -ano | findstr :8080  # Windows
lsof -i :8080                 # Linux/Mac

# Changer le port dans server.xml ou arrêter le processus
```

#### Problème : Erreur de connexion MySQL
```bash
# Vérifier le service MySQL
systemctl status mysql        # Linux
net start mysql              # Windows

# Tester la connexion
mysql -u smartbee -p -h localhost
```

#### Problème : Certificat SSL
```bash
# Générer un nouveau keystore
keytool -genkey -alias smartbeehouse -keyalg RSA -keystore keystore.jks -keysize 2048
```

### 🔄 Mise à Jour de l'Application

#### 1. Arrêter Tomcat
```bash
$TOMCAT_HOME/bin/shutdown.sh
```

#### 2. Supprimer l'Ancienne Version
```bash
rm -rf $TOMCAT_HOME/webapps/smart-bee-house*
```

#### 3. Déployer la Nouvelle Version
```bash
mvn clean package
cp target/smart-bee-house.war $TOMCAT_HOME/webapps/
```

#### 4. Redémarrer Tomcat
```bash
$TOMCAT_HOME/bin/startup.sh
```

### 📈 Monitoring de Production

#### 1. Logs d'Application
```bash
# Configurer logback.xml pour la production
tail -f logs/smart-bee-house.log
```

#### 2. Métriques JVM
```bash
# Activer JMX pour monitoring
export CATALINA_OPTS="-Dcom.sun.management.jmxremote"
```

#### 3. Sauvegarde Base de Données
```bash
# Script de sauvegarde quotidienne
mysqldump -u smartbee -p smart_bee_house > backup_$(date +%Y%m%d).sql
```

### ✅ Checklist de Déploiement

- [ ] Java 11+ installé
- [ ] Maven 3.6+ installé
- [ ] MySQL 8.0+ installé et configuré
- [ ] Tomcat 9+ installé
- [ ] Base de données créée et initialisée
- [ ] Configuration OAuth Google
- [ ] Certificats SSL générés
- [ ] Application compilée sans erreur
- [ ] Tests unitaires passent
- [ ] WAR déployé sur Tomcat
- [ ] Application accessible via HTTPS
- [ ] API REST fonctionnelle
- [ ] Service web SOAP accessible
- [ ] Authentification Google opérationnelle

### 🆘 Support

En cas de problème, vérifiez :
1. Les logs Tomcat dans `$TOMCAT_HOME/logs/`
2. Les logs de l'application
3. La connectivité réseau et base de données
4. Les certificats SSL
5. La configuration OAuth

Pour plus d'aide, consultez la documentation complète dans `README.md`.