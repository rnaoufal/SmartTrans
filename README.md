# SmartTransport – Instructions de démarrage rapide

##  Compilation et exécution du projet

###  1. Compiler le projet avec Maven

```bash
mvn clean package
```

### 2. Lancer l’application Spring Boot

Depuis la racine du projet :

```bash
mvn spring-boot:run
```
ou bien :

```bash
java -jar target/smart-transport-1.0.0.jar

```

L’application démarre par défaut sur le port **8080** :

 [http://localhost:8080](http://localhost:8080)

---

## API REST – `/api/optimize`

### Méthode

**POST** `/api/optimize`

### Type de contenu

`multipart/form-data`


### Paramètre requis
- **file** : fichier JSON contenant la liste des commandes (`input.json`)

### 🔹 Exemple d’appel avec `curl`
```bash
curl -X POST "http://localhost:8080/api/optimize"      -H "accept: application/json"      -F "file=@src/main/resources/input.json"      -o result.json
```

---

##  Documentation interactive
Accède à Swagger UI :

[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

pour tester directement le endpoint `/api/optimize`.
