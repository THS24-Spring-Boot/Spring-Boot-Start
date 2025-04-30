# Övningar: Konfigurera databaser med Spring Boot

Här är några praktiska övningar för att lära dig konfigurera och arbeta med databaser i Spring Boot. Övningarna fokuserar på själva databasanslutningen och grundläggande interaktion utan att gå in på ORM/JPA-detaljer.

## Förberedelser

Innan du börjar med övningarna behöver du:

1. Ha Java installerat (version 11 eller högre)
2. Ha Maven eller Gradle installerat
3. Ha en IDE (t.ex. IntelliJ IDEA, Eclipse, VS Code)
4. Ha Postman installerat (för att testa dina endpoints)

## Övning 1: Konfigurera H2-databas (inbyggd)

**Mål:** Skapa ett Spring Boot-projekt med en H2 in-memory databas och verifiera anslutningen.

**Uppgift:**

1. Skapa ett nytt Spring Boot-projekt via [Spring Initializr](https://start.spring.io/) med följande beroenden:
   - Spring Web
   - JDBC API
   - H2 Database

2. Konfigurera H2-databasen i `application.properties` med följande inställningar:
   ```properties
   # H2 databasanslutning
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   
   # Aktivera H2-konsolen
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   ```

3. Skapa en enkel controller för att testa anslutningen (återanvänd strukturen från den praktiska guiden).

4. Starta applikationen och testa:
   - Gå till H2-konsolen i webbläsaren: `http://localhost:8080/h2-console`
   - Använd databasens URL, användarnamn och lösenord från konfigurationen
   - Klicka på "Connect" och utforska databasen
   - Testa din controller-endpoint via Postman

**Tips:**
- H2 är en bra databas för utveckling och testning eftersom den är inbyggd och kräver ingen extern installation
- I H2-konsolen kan du skriva SQL-kommandon direkt för att skapa tabeller, lägga till data, etc.

## Övning 2: Konfigurera SQLite-databas

**Mål:** Byt från H2 till SQLite och verifiera att allt fortfarande fungerar.

**Uppgift:**

1. Utgå från projektet från Övning 1 eller skapa ett nytt
2. Ersätt H2-beroendet med SQLite i din `pom.xml`:
   ```xml
   <dependency>
       <groupId>org.xerial</groupId>
       <artifactId>sqlite-jdbc</artifactId>
       <version>3.40.1.0</version>
   </dependency>
   ```

3. Uppdatera `application.properties` med SQLite-konfiguration:
   ```properties
   # SQLite databasanslutning
   spring.datasource.url=jdbc:sqlite:./mydatabase.db
   spring.datasource.driver-class-name=org.sqlite.JDBC
   ```

4. Anpassa din controller för att använda SQLite-kompatibel SQL (observera att vissa datatyper och funktioner kan skilja sig från H2)

5. Starta applikationen och verifiera att:
   - Filen `mydatabase.db` skapas i projekt-mappen
   - Din controller fungerar som förväntat via Postman

**Tips:**
- SQLite lagrar data i en fil, vilket gör det enkelt att inspektera och flytta databasen
- Du kan använda verktyg som "DB Browser for SQLite" för att inspektera SQLite-databasfiler

## Övning 3: Hantera flera databaskonfigurationer

**Mål:** Lär dig att växla mellan databaser baserat på miljö (utveckling vs. produktion).

**Uppgift:**

1. Skapa två olika konfigurationsfiler:
   - `application-dev.properties` för utvecklingsmiljö (med H2)
   - `application-prod.properties` för produktionsmiljö (med SQLite)

2. I `application-dev.properties`:
   ```properties
   # H2 databasanslutning för utveckling
   spring.datasource.url=jdbc:h2:mem:devdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   spring.h2.console.enabled=true
   ```

3. I `application-prod.properties`:
   ```properties
   # SQLite databasanslutning för produktion
   spring.datasource.url=jdbc:sqlite:./proddb.db
   spring.datasource.driver-class-name=org.sqlite.JDBC
   ```

4. I `application.properties`, välj vilken profil som ska användas:
   ```properties
   # Välj aktiv profil (dev eller prod)
   spring.profiles.active=dev
   ```

5. Starta applikationen och testa med profilen "dev"
6. Ändra den aktiva profilen till "prod" och starta om applikationen

**Tips:**
- Olika miljöer kräver ofta olika databaskonfigurationer
- Du kan också starta applikationen med en specifik profil via kommandoraden:
  ```
  java -jar app.jar --spring.profiles.active=prod
  ```

## Övning 4: Arbeta med schema och data

**Mål:** Konfigurera en databas och automatiskt ladda in schema och testdata vid uppstart.

**Uppgift:**

1. Skapa en `schema.sql`-fil i `src/main/resources` med följande innehåll:
   ```sql
   DROP TABLE IF EXISTS products;
   
   CREATE TABLE products (
     id INT PRIMARY KEY,
     name VARCHAR(100) NOT NULL,
     price DECIMAL(10,2),
     description TEXT
   );
   ```

2. Skapa en `data.sql`-fil i `src/main/resources` med följande innehåll:
   ```sql
   INSERT INTO products (id, name, price, description) VALUES
   (1, 'Laptop', 12000.00, 'Kraftfull bärbar dator'),
   (2, 'Mobiltelefon', 8000.00, 'Smartphone med bra kamera'),
   (3, 'Headset', 1500.00, 'Trådlöst headset med brusreducering');
   ```

3. Konfigurera din `application.properties` för att använda dessa filer:
   ```properties
   # H2 databasanslutning
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   
   # Aktivera automatisk initializing med schema.sql och data.sql
   spring.sql.init.mode=always
   ```

4. Skapa en enkel controller med en endpoint som returnerar alla produkter från databasen

5. Starta applikationen och testa din nya endpoint i Postman

**Tips:**
- `schema.sql` och `data.sql` används automatiskt av Spring Boot för att initialisera databasen
- Detta är mycket användbart för testning och utveckling
- För SQLite och andra databaser kan du behöva anpassa SQL-syntaxen

## Övning 5: Skapa en CRUD-API för produkter med JDBC

**Mål:** Implementera en fullständig CRUD-API för produkter med hjälp av JDBC.

**Uppgift:**

1. Skapa en produktklass (utan annotationer):
   ```java
   public class Product {
       private Long id;
       private String name;
       private double price;
       private String description;
       
       // Konstruktorer, getters och setters
   }
   ```

2. Skapa en produktdao-klass för JDBC-åtkomst:
   ```java
   @Repository
   public class ProductDao {
       private final JdbcTemplate jdbcTemplate;
       
       public ProductDao(JdbcTemplate jdbcTemplate) {
           this.jdbcTemplate = jdbcTemplate;
       }
       
       // Implementera metoder för att:
       // 1. Hitta alla produkter
       // 2. Hitta en produkt med ID
       // 3. Skapa en ny produkt
       // 4. Uppdatera en produkt
       // 5. Ta bort en produkt
   }
   ```

3. Skapa en produktcontroller som använder ditt ProductDao för att:
   - GET /api/products - Lista alla produkter
   - GET /api/products/{id} - Visa en produkt
   - POST /api/products - Skapa en produkt
   - PUT /api/products/{id} - Uppdatera en produkt
   - DELETE /api/products/{id} - Ta bort en produkt

4. Testa alla dina API-endpoints med Postman

**Tips:**
- För att mappa resultatet från databassökningar till objekt, använd `RowMapper`:
  ```java
  private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
      Product product = new Product();
      product.setId(rs.getLong("id"));
      product.setName(rs.getString("name"));
      product.setPrice(rs.getDouble("price"));
      product.setDescription(rs.getString("description"));
      return product;
  };
  ```

## Övning 6: Implementera databasmigrering med Flyway

**Mål:** Lär dig att använda verktyg för databasmigrering för att hantera databasändringar över tid.

**Uppgift:**

1. Lägg till Flyway-beroendet i din pom.xml:
   ```xml
   <dependency>
       <groupId>org.flywaydb</groupId>
       <artifactId>flyway-core</artifactId>
   </dependency>
   ```

2. Skapa migreringsfilerna i `src/main/resources/db/migration`:
   - `V1__Initial_Schema.sql` - Skapa grundläggande tabeller
   - `V2__Add_Products.sql` - Lägg till testdata
   - `V3__Add_Categories.sql` - Lägg till kategorirelaterade tabeller

3. Konfigurera Flyway i `application.properties`:
   ```properties
   # Flyway-konfiguration
   spring.flyway.enabled=true
   spring.flyway.baseline-on-migrate=true
   ```

4. Starta applikationen och kontrollera att migrationerna utförs korrekt
5. Undersök tabellen `flyway_schema_history` i din databas för att se migreringshistoriken

**Tips:**
- Databasmigrering är kritisk för produktionsapplikationer där databasschemat utvecklas över tid
- Migreringsfilnamn måste följa mönstret `V{version}__{description}.sql`
- Flyway håller reda på vilka migreringar som har körts via tabellen `flyway_schema_history`

## Felsökning

Om du stöter på problem under övningarna, kontrollera följande:

1. **Databasanslutningsfel**:
   - Kontrollera att beroendena är korrekt konfigurerade i pom.xml
   - Verifiera att anslutningssträngen är korrekt
   - Se till att användaren har rätt behörigheter

2. **SQL-fel**:
   - Olika databaser har olika SQL-syntax - se till att du använder rätt syntax för din databas
   - SQLite har t.ex. begränsat stöd för `ALTER TABLE` och datatyper jämfört med H2/MySQL/PostgreSQL

3. **Spring Boot-konfigurationsfel**:
   - Kontrollera stavningen av konfigurationsparametrarna i application.properties
   - Verifiera att du har rätt drivrutinsklassnamn

## Sammanfattning

Genom dessa övningar har du lärt dig att:
1. Konfigurera olika typer av databaser i Spring Boot
2. Växla mellan databaser baserat på miljö
3. Ladda schema och testdata automatiskt
4. Implementera grundläggande dataåtkomst med JDBC
5. Använda databasmigrering för att hantera schemaändringar

När du känner dig bekväm med dessa övningar är du redo att utforska mer avancerade koncept som JPA och ORM.
