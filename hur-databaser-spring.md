# Databaser med Spring Boot: Praktisk guide

Denna guide visar hur du lägger till och konfigurerar en databas i ditt Spring Boot-projekt, och testar anslutningen via en enkel REST-endpoint med Postman.

## Steg 1: Skapa ett nytt Spring Boot-projekt

Börja med att skapa ett nytt Spring Boot-projekt:

1. Gå till [Spring Initializr](https://start.spring.io/)
2. Välj:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: Välj senaste stabila versionen
   - **Packaging**: Jar
   - **Java**: 17 (eller den version du använder)
3. Klicka på "Add dependencies" och välj:
   - **Spring Web** (för att skapa REST-endpoints)
   - **JDBC API** (för databasåtkomst utan ORM)
4. Klicka på "Generate" för att ladda ner projektet

## Steg 2: Lägg till databasdrivrutin

Öppna `pom.xml` och lägg till drivrutinen för din valda databas.

### För MySQL

```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### För PostgreSQL

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

### För SQLite

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.40.1.0</version>
</dependency>
```

## Steg 3: Konfigurera anslutningen i application.properties

Skapa eller öppna filen `src/main/resources/application.properties` och lägg till konfigurationen för din databas.

### För MySQL

```properties
# Databasanslutning
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

Se till att:
1. Ändra "mydatabase" till namnet på din databas
2. Ange rätt användarnamn och lösenord
3. Skapa databasen innan du startar applikationen:
   ```sql
   CREATE DATABASE mydatabase;
   ```

### För PostgreSQL

```properties
# Databasanslutning
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver
```

Se till att:
1. Skapa databasen innan du startar applikationen:
   ```sql
   CREATE DATABASE mydatabase;
   ```
2. Ändra användarnamn/lösenord vid behov

### För SQLite

```properties
# Databasanslutning
spring.datasource.url=jdbc:sqlite:./mydatabase.db
spring.datasource.driver-class-name=org.sqlite.JDBC
```

För SQLite:
1. Filen skapas automatiskt om den inte finns
2. Ändra sökvägen vid behov (`./mydatabase.db` sparar filen i projektroten)

## Steg 4: Skapa en REST-controller för att testa databasanslutningen

Skapa en ny fil `DatabaseTestController.java` i paketet `com.example.demo.controller` (du kan behöva skapa mappstrukturen först):

```java
package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/db-test")
public class DatabaseTestController {

    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public DatabaseTestController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getDatabaseStatus() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Enkel SQL-fråga för att kontrollera anslutningen
            String result = jdbcTemplate.queryForObject(
                "SELECT 'Anslutningen fungerar!' AS message", 
                (rs, rowNum) -> rs.getString("message")
            );
            
            response.put("status", "success");
            response.put("message", result);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Databasfel: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/create-table")
    public ResponseEntity<Map<String, String>> createTestTable() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // SQLite-version
            String createTableSQL = "CREATE TABLE IF NOT EXISTS test_table (id INTEGER PRIMARY KEY, name TEXT)";
            
            // MySQL och PostgreSQL-version (avkommentera om du använder någon av dessa)
            // String createTableSQL = "CREATE TABLE IF NOT EXISTS test_table (id INT PRIMARY KEY, name VARCHAR(100))";
            
            jdbcTemplate.execute(createTableSQL);
            
            response.put("status", "success");
            response.put("message", "Testtabell skapad");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Kunde inte skapa tabell: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @PostMapping("/add-data")
    public ResponseEntity<Map<String, String>> addTestData() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Kontrollera om data redan finns
            List<Map<String, Object>> existingData = jdbcTemplate.queryForList(
                "SELECT COUNT(*) as count FROM test_table WHERE id IN (1, 2)"
            );
            
            // Om data redan finns, ta bort det först
            if (existingData.size() > 0 && ((Number)existingData.get(0).get("count")).intValue() > 0) {
                jdbcTemplate.update("DELETE FROM test_table WHERE id IN (1, 2)");
            }
            
            // Lägg till testdata
            jdbcTemplate.update(
                "INSERT INTO test_table (id, name) VALUES (?, ?)",
                1, "Test Item 1"
            );
            
            jdbcTemplate.update(
                "INSERT INTO test_table (id, name) VALUES (?, ?)",
                2, "Test Item 2"
            );
            
            response.put("status", "success");
            response.put("message", "Testdata tillagd");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Kunde inte lägga till data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @GetMapping("/data")
    public ResponseEntity<?> getTestData() {
        try {
            // Hämta testdata
            List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * FROM test_table");
            return ResponseEntity.ok(rows);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Kunde inte hämta data: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
    
    @DeleteMapping("/drop-table")
    public ResponseEntity<Map<String, String>> dropTestTable() {
        Map<String, String> response = new HashMap<>();
        
        try {
            // Ta bort testtabell
            jdbcTemplate.execute("DROP TABLE IF EXISTS test_table");
            
            response.put("status", "success");
            response.put("message", "Testtabell borttagen");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Kunde inte ta bort tabell: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
```

## Steg 5: Starta applikationen

1. Starta din databasserver (om du använder MySQL eller PostgreSQL)
2. Kör Spring Boot-applikationen:
   - I IDE: högerklicka på Application-filen och välj "Run"
   - Med Maven: `mvn spring-boot:run`

Applikationen bör starta på port 8080 som standard.

## Steg 6: Testa databasanslutningen med Postman

Nu kan du använda Postman för att testa databasanslutningen. Om du inte redan har Postman installerat, ladda ner det från [postman.com](https://www.postman.com/downloads/).

### Steg-för-steg guide för Postman:

1. Öppna Postman
2. Skapa en ny Collection (klicka på "New" > "Collection") och namnge den "Database Test"
3. För varje endpoint nedan, skapa en ny request (högerklicka på din Collection > "Add request")

### 1. Testa anslutningsstatus

- **Metod**: GET
- **URL**: http://localhost:8080/api/db-test/status
- **Instruktioner**:
  - Välj GET i rullgardinsmenyn
  - Klistra in URL:en
  - Klicka på "Send"
- **Förväntat svar**:
  ```json
  {
    "status": "success",
    "message": "Anslutningen fungerar!"
  }
  ```

### 2. Skapa testtabell

- **Metod**: POST
- **URL**: http://localhost:8080/api/db-test/create-table
- **Instruktioner**:
  - Välj POST i rullgardinsmenyn
  - Klistra in URL:en
  - Klicka på "Send"
- **Förväntat svar**:
  ```json
  {
    "status": "success",
    "message": "Testtabell skapad"
  }
  ```

### 3. Lägg till testdata

- **Metod**: POST
- **URL**: http://localhost:8080/api/db-test/add-data
- **Instruktioner**:
  - Välj POST i rullgardinsmenyn
  - Klistra in URL:en
  - Klicka på "Send"
- **Förväntat svar**:
  ```json
  {
    "status": "success",
    "message": "Testdata tillagd"
  }
  ```

### 4. Hämta testdata

- **Metod**: GET
- **URL**: http://localhost:8080/api/db-test/data
- **Instruktioner**:
  - Välj GET i rullgardinsmenyn
  - Klistra in URL:en
  - Klicka på "Send"
- **Förväntat svar**:
  ```json
  [
    {
      "id": 1,
      "name": "Test Item 1"
    },
    {
      "id": 2,
      "name": "Test Item 2"
    }
  ]
  ```

### 5. Ta bort testtabell

- **Metod**: DELETE
- **URL**: http://localhost:8080/api/db-test/drop-table
- **Instruktioner**:
  - Välj DELETE i rullgardinsmenyn
  - Klistra in URL:en
  - Klicka på "Send"
- **Förväntat svar**:
  ```json
  {
    "status": "success",
    "message": "Testtabell borttagen"
  }
  ```

Om alla förfrågningar ger förväntade svar har du lyckats konfigurera databasanslutningen korrekt!

## Felsökning

### Vanliga problem med MySQL

1. **Kan inte ansluta till MySQL-servern**:
   - Kontrollera att MySQL-servern är igång (`service mysql status` eller motsvarande)
   - Verifiera att användaren har rätt behörigheter:
     ```sql
     CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';
     GRANT ALL PRIVILEGES ON mydatabase.* TO 'username'@'localhost';
     FLUSH PRIVILEGES;
     ```

2. **Fel med tidszon**:
   Lägg till `serverTimezone=UTC` i din URL:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase?serverTimezone=UTC
   ```

### Vanliga problem med PostgreSQL

1. **Autentiseringsfel**:
   - Kontrollera pg_hba.conf-filen för att säkerställa att anslutning tillåts
   - Verifiera användarnamn och lösenord

2. **Databasen finns inte**:
   - Skapa databasen innan du startar applikationen:
     ```sql
     CREATE DATABASE mydatabase;
     ```

### Vanliga problem med SQLite

1. **Rättighetsproblem**: 
   - Se till att applikationen har skrivrättigheter i mappen där databasfilen ska sparas

2. **SQL-syntaxfel**:
   - SQLite har en något annorlunda SQL-dialekt. Om du får syntaxfel, kontrollera att SQL-kommandona är kompatibla med SQLite

## Exempel på olika URL-format

### MySQL

```properties
# Grundläggande
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase

# Med parameterinställningar
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true

# Med specifik teckenkodning
spring.datasource.url=jdbc:mysql://localhost:3306/mydatabase?useUnicode=true&characterEncoding=UTF-8
```

### PostgreSQL

```properties
# Grundläggande
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase

# Med specifikt schema
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase?currentSchema=myschema

# Med SSL
spring.datasource.url=jdbc:postgresql://localhost:5432/mydatabase?ssl=true
```

### SQLite

```properties
# I projektroten
spring.datasource.url=jdbc:sqlite:./mydatabase.db

# Absolut sökväg (Windows)
spring.datasource.url=jdbc:sqlite:C:/path/to/mydatabase.db

# Absolut sökväg (Linux/Mac)
spring.datasource.url=jdbc:sqlite:/path/to/mydatabase.db

# I minnet (för testning)
spring.datasource.url=jdbc:sqlite::memory:
```

## Sammanfattning av stegen

1. Skapa ett Spring Boot-projekt med Spring Web och JDBC API
2. Lägg till rätt databasdrivrutin för din valda databas
3. Konfigurera anslutningen i application.properties
4. Skapa en REST-controller för att testa databasanslutningen
5. Starta applikationen
6. Testa anslutningen med Postman

När dessa steg är slutförda har du en fungerande Spring Boot-applikation med databasanslutning som har testats via REST-endpoints.
