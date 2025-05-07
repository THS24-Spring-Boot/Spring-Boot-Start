# Databaser med Spring Boot: H2 och MySQL

Denna guide fokuserar på att komma igång med databaser i Spring Boot-applikationer, med särskilt fokus på H2 (inbyggd databas) och MySQL (extern databas). Vi går igenom både JDBC och JPA för databasåtkomst, samt konfiguration, beroenden och praktiska exempel.

## Introduktion: Två vanliga databasalternativ

### H2 Database
H2 är en inbyggd, minnesbaserad relationsdatabas skriven i Java. Den är:
- Extremt snabb (ingen nätverkskommunikation behövs)
- Kräver ingen separat installation
- Har en inbyggd webbkonsol för databashantering
- Perfekt för utveckling, testning och mindre applikationer

### MySQL
MySQL är en fullskalig relationsdatabas som körs separat från din applikation. Den erbjuder:
- Robust hantering av stora datamängder
- Bättre prestanda för komplexa operationer
- Fullständig datapersistens
- Brett använd för produktionssystem

## Två sätt att ansluta till databaser i Spring Boot

### 1. JDBC (Java Database Connectivity)
JDBC är en lågnivåteknik för direkt databasåtkomst:
- Mer explicit kontroll över SQL-frågor
- Mindre abstraktion och automatisering
- Kräver mer manuell kodning
- Bra för enklare applikationer eller specifika databasoptimerade lösningar

### 2. JPA (Java Persistence API)
JPA är en högnivåspecifikation för ORM (Object-Relational Mapping):
- Automatisk mappning mellan Java-objekt och databastabeller
- Betydligt mindre boilerplate-kod
- Hantering av entitetsrelationer
- Databasagnostisk implementation

I praktiken används JPA oftast genom Spring Data JPA som förenklar implementationen ytterligare.

## Nödvändiga beroenden

### Beroenden för H2 med JDBC

```xml
<!-- Spring Web för RESTful API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JDBC API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Beroenden för H2 med JPA

```xml
<!-- Spring Web för RESTful API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Beroenden för MySQL med JDBC

```xml
<!-- Spring Web för RESTful API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- JDBC API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Beroenden för MySQL med JPA

```xml
<!-- Spring Web för RESTful API -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Databaskonfiguration

Databasanslutningen och relaterade inställningar konfigureras i `application.properties` (eller `application.yml`).

### H2 med JDBC

```properties
# H2 Databasanslutning
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Aktivera H2-konsolen
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Automatisk schema initialisering
spring.sql.init.mode=always
```

### H2 med JPA

```properties
# H2 Databasanslutning
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate inställningar
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Aktivera H2-konsolen
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
```

### MySQL med JDBC

```properties
# MySQL Databasanslutning
spring.datasource.url=jdbc:mysql://localhost:3306/databasename
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password

# Visa SQL-frågor i loggen (för utveckling)
logging.level.org.springframework.jdbc.core=DEBUG
```

### MySQL med JPA

```properties
# MySQL Databasanslutning
spring.datasource.url=jdbc:mysql://localhost:3306/databasename
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=password

# JPA/Hibernate inställningar
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Loggnivå för SQL-frågor
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

## Viktiga konfigurationsparametrar förklarade

### `spring.datasource.url`

Detta är JDBC-URL som specificerar databasanslutningen:

- **H2 (in-memory)**: `jdbc:h2:mem:databasename`
  - När applikationen stängs av försvinner all data
  - Perfekt för utveckling och testning

- **H2 (fil)**: `jdbc:h2:file:./data/databasename`
  - Data sparas i en fil
  - Databasfilen skapas i den angivna sökvägen

- **MySQL**: `jdbc:mysql://host:port/databasename?param1=value1&param2=value2`
  - Du kan lägga till parametrar för att konfigurera anslutningen, t.ex.:
    - `useSSL=false` - Inaktiverar SSL
    - `serverTimezone=UTC` - Specificerar tidszon
    - `allowPublicKeyRetrieval=true` - Tillåter hämtning av publika nycklar
    - `createDatabaseIfNotExist=true` - Skapar databasen om den inte finns

### `spring.jpa.hibernate.ddl-auto`

Denna viktiga parameter styr hur Hibernate hanterar databasschemat:

- **none**: Gör ingenting med schemat
- **validate**: Verifierar att schemat matchar entiteter men gör inga ändringar
- **update**: Uppdaterar schemat för att matcha entiteterna (lägger till tabeller/kolumner)
- **create**: Skapar schemat, raderar eventuellt befintliga tabeller
- **create-drop**: Skapar schemat vid start och raderar det vid avstängning

**Rekommendation**:
- Utveckling: `update` eller `create-drop`
- Testning: `create` eller `update`
- Produktion: `none` eller `validate`

### Skillnad mellan H2 och MySQL i konfigurationen

**H2**:
- Ingen separat installation behövs
- Databas kan skapas automatiskt
- Inbyggd webbkonsol via `spring.h2.console.enabled=true`
- Kräver typiskt ingen konfiguration av tidszon eller teckenuppsättning

**MySQL**:
- Kräver separat databasinstallation och -hantering
- Kan behöva ytterligare parametrar för tidszon: `serverTimezone=UTC`
- Kräver ofta specifika inställningar för teckenuppsättning: `useUnicode=true&characterEncoding=utf8`
- Kan behöva specifik hantering av SSL: `useSSL=false`

## Implementeringsstrategier: JDBC vs JPA

### Strukturskillnader

**Med JDBC:**
1. Datamodellklasser (vanliga Java-klasser, POJOs)
2. Repository-klasser (innehåller SQL-frågor)
3. Controller (RESTful API)

**Med JPA:**
1. Entitetsklasser (med JPA-annotationer)
2. Repository-interfaces (extends JpaRepository)
3. Service-klasser (innehåller affärslogik)
4. Controller (RESTful API)

### JDBC-implementation (med H2 eller MySQL)

#### 1. Datamodell

```java
public class Product {
    private Long id;
    private String name;
    private double price;
    
    // Konstruktorer, getters, setters, etc.
}
```

#### 2. Repository

```java
@Repository
public class ProductRepository {
    private final JdbcTemplate jdbcTemplate;
    
    @Autowired
    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    // RowMapper för att konvertera resultat till objekt
    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        return product;
    };
    
    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM products", productRowMapper);
    }
    
    public Optional<Product> findById(Long id) {
        List<Product> results = jdbcTemplate.query(
            "SELECT * FROM products WHERE id = ?", 
            productRowMapper, 
            id
        );
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }
    
    public Product save(Product product) {
        if (product.getId() == null) {
            // Skapa ny produkt
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO products (name, price) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS
                );
                ps.setString(1, product.getName());
                ps.setDouble(2, product.getPrice());
                return ps;
            }, keyHolder);
            product.setId(keyHolder.getKey().longValue());
        } else {
            // Uppdatera befintlig produkt
            jdbcTemplate.update(
                "UPDATE products SET name = ?, price = ? WHERE id = ?",
                product.getName(),
                product.getPrice(),
                product.getId()
            );
        }
        return product;
    }
    
    public void deleteById(Long id) {
        jdbcTemplate.update("DELETE FROM products WHERE id = ?", id);
    }
}
```

#### 3. Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productRepository.save(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    product.setId(id);
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.deleteById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
```

### JPA-implementation (med H2 eller MySQL)

#### 1. Entitet

```java
@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private double price;
    
    // Konstruktorer, getters, setters, etc.
}
```

#### 2. Repository Interface

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Spring Data JPA genererar implementationer av standardmetoder automatiskt:
    // findAll(), findById(), save(), deleteById() etc.
    
    // Du kan också lägga till anpassade sökmetoder:
    List<Product> findByNameContaining(String name);
    
    List<Product> findByPriceLessThan(double price);
}
```

#### 3. Service

```java
@Service
public class ProductService {
    
    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }
    
    @Transactional
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Transactional
    public void deleteProductById(Long id) {
        productRepository.deleteById(id);
    }
    
    public List<Product> findProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }
    
    public List<Product> findProductsCheaperThan(double price) {
        return productRepository.findByPriceLessThan(price);
    }
}
```

#### 4. Controller

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAllProducts();
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) Double maxPrice) {
        if (name != null) {
            return productService.findProductsByName(name);
        } else if (maxPrice != null) {
            return productService.findProductsCheaperThan(maxPrice);
        }
        return productService.findAllProducts();
    }
    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.findProductById(id)
                .map(existingProduct -> {
                    product.setId(id);
                    return ResponseEntity.ok(productService.saveProduct(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(product -> {
                    productService.deleteProductById(id);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
```

## Schemahantering och testdata

Både med JDBC och JPA kan du använda SQL-filer för att initiera ditt schema och testdata.

### Med JDBC

Spring Boot kör automatiskt filerna `schema.sql` och `data.sql` när de finns i `src/main/resources`:

**schema.sql**:
```sql
DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price DOUBLE NOT NULL
);
```

**data.sql**:
```sql
INSERT INTO products (name, price) VALUES
('Bärbar dator', 8999.00),
('Smartphone', 6499.00),
('Trådlösa hörlurar', 1499.00);
```

Aktivera detta i `application.properties`:
```properties
spring.sql.init.mode=always
```

### Med JPA

Med JPA genereras schemat automatiskt från entitetsklasserna baserat på `spring.jpa.hibernate.ddl-auto`.

För att lägga till testdata med JPA kan du fortfarande använda `data.sql` eller skapa en initialiserare:

```java
@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(ProductRepository productRepository) {
        return args -> {
            // Kolla om vi redan har data
            if (productRepository.count() == 0) {
                productRepository.save(new Product("Bärbar dator", 8999.00));
                productRepository.save(new Product("Smartphone", 6499.00));
                productRepository.save(new Product("Trådlösa hörlurar", 1499.00));
                System.out.println("Testdata har laddats.");
            }
        };
    }
}
```

## Testa endpoints

### För H2-databaser

1. **Starta applikationen**
2. **Testa H2-konsolen** (för både JDBC och JPA):
   - Öppna webbläsaren och gå till: `http://localhost:8080/h2-console`
   - Anslutningsinformation:
     - JDBC URL: `jdbc:h2:mem:testdb` (samma som i application.properties)
     - Användarnamn: `sa`
     - Lösenord: (lämna tomt)
   - Klicka på "Connect"
   - Nu kan du utforska databasen, tabeller och köra SQL-frågor direkt

3. **Testa REST-endpoints med Postman**:
   
   Installera och öppna Postman, skapa sedan följande förfrågningar:

   **Hämta alla produkter**
   - Metod: GET
   - URL: http://localhost:8080/api/products

   **Hämta en produkt med ID**
   - Metod: GET
   - URL: http://localhost:8080/api/products/1

   **Skapa en ny produkt**
   - Metod: POST
   - URL: http://localhost:8080/api/products
   - Headers: Content-Type: application/json
   - Body:
     ```json
     {
       "name": "Spelkonsol",
       "price": 4999.00
     }
     ```

   **Uppdatera en produkt**
   - Metod: PUT
   - URL: http://localhost:8080/api/products/1
   - Headers: Content-Type: application/json
   - Body:
     ```json
     {
       "name": "Uppgraderad laptop",
       "price": 12999.00
     }
     ```

   **Ta bort en produkt**
   - Metod: DELETE
   - URL: http://localhost:8080/api/products/2

### För MySQL-databaser

Stegen är desamma som för H2, förutom:

1. **Se till att MySQL-servern är igång**
2. **Kontrollera databasanslutningsinställningarna**:
   - Rätt användarnamn och lösenord
   - Korrekt databasnamn
   - Servern körs på rätt port (vanligtvis 3306)
3. **Testa databasanslutningen**:
   - Lägg till en endpoint för att testa anslutningen:

```java
@GetMapping("/api/db-test")
public ResponseEntity<String> testConnection() {
    try {
        // För JDBC:
        String result = jdbcTemplate.queryForObject(
            "SELECT 'Anslutningen fungerar!' AS message", 
            String.class
        );
        
        // Alternativt för JPA:
        // boolean result = productRepository.count() >= 0;
        
        return ResponseEntity.ok("Databasanslutning OK: " + result);
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Databasfel: " + e.getMessage());
    }
}
```

4. **Testa endpoints med Postman** (som för H2)

## Felsökning och vanliga problem

### 1. Databasanslutning misslyckas

**H2**:
- Kontrollera att URL-formatet är korrekt
- Se till att ingen annan process använder samma fil (för filbaserad H2)

**MySQL**:
- Kontrollera att MySQL-servern är igång
- Verifiera användarnamn och lösenord
- Se till att databasen finns eller lägg till `createDatabaseIfNotExist=true` i URL:en
- Kontrollera att användaren har rätt behörigheter:
  ```sql
  GRANT ALL PRIVILEGES ON databasename.* TO 'user'@'localhost';
  FLUSH PRIVILEGES;
  ```

### 2. Tabell eller kolumn finns inte

**JDBC**:
- Kontrollera att `schema.sql` körs korrekt (`spring.sql.init.mode=always`)
- Verifiera SQL-syntaxen för din specifika databas

**JPA**:
- Kontrollera `spring.jpa.hibernate.ddl-auto` inställningen
- Se till att entitetsklasser och annotationer är rätt konfigurerade
- Vid ändringar, prova att sätta `ddl-auto` till `create` tillfälligt

### 3. JPA-specifika problem

- **Felaktiga relationer**: Kontrollera mappningsannotationer
- **LazyInitializationException**: Uppstår när du försöker komma åt en lazy-laddad relation utanför en transaktion
- **"No identifier specified for entity"**: Saknad `@Id`-annotation
- **Oanvändbara ID-värden**: Felaktig `@GeneratedValue`-strategi för databasen

### 4. JDBC-specifika problem

- **SQL-syntax fel**: Olika databaser har olika SQL-dialekter
- **Problem med autoincrement ID**: Olika databaser hanterar genererade nycklar olika
- **Fel vid mappning av resultatuppsättning**: Kontrollera att din RowMapper matchar tabellschemat

## Jämförelse: JDBC vs JPA

### JDBC fördelar
- **Full kontroll** över SQL-frågor
- **Optimerad prestanda** för specifika frågor
- **Låg overhead** för enklare applikationer
- **Direkt åtkomst** till databasspecifika funktioner

### JPA fördelar
- **Snabbare utveckling** med mindre boilerplate-kod
- **Objektorienterad modell** för databasåtkomst
- **Automatiska relationer** mellan entiteter
- **Databasagnostisk kod** som gör det enklare att byta databas
- **Inbyggd caching** för bättre prestanda
- **Deklarativa transaktioner** och query-metoder

## Rekommendationer

1. **För utveckling och testning**:
   - H2 in-memory databas
   - JPA för snabbare utveckling
   - `spring.jpa.hibernate.ddl-auto=update`

2. **För mindre applikationer**:
   - H2 filbaserad eller MySQL
   - JPA för enklare implementation

3. **För produktionsapplikationer**:
   - MySQL (eller annan extern databas)
   - JPA för de flesta användningsfall
   - JDBC för prestandakritiska delar
   - `spring.jpa.hibernate.ddl-auto=validate` eller `none`
   - Överväg databasmigrering (Flyway eller Liquibase)

4. **För applikationer med extrema prestandakrav**:
   - MySQL (eller annan extern databas)
   - JDBC för direkt kontroll över SQL
   - Optimerade databasscheman
   - Caching-lager

## Sammanfattning

Spring Boot ger dig flexibilitet att välja mellan H2 och MySQL databaser, samt mellan JDBC- och JPA-baserad dataåtkomst. Valet beror på dina specifika behov:

- **H2** är utmärkt för utveckling, test och enklare applikationer
- **MySQL** är bättre för produktion och större datamängder
- **JDBC** ger mer direkt kontroll men kräver mer manuell kodning
- **JPA** ger snabbare utveckling och mer abstraktion

Genom att förstå skillnaderna och fördelarna med varje approach kan du välja den bästa kombinationen för ditt projekt.
