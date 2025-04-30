# JPA i praktiken: Bygga ditt första Entity och Repository

Denna guide visar hur du praktiskt implementerar JPA med Spring Boot genom att skapa en enkel entitet och repository. Vi fokuserar på att göra det så enkelt som möjligt att komma igång.

## Vad vi ska bygga

Vi ska skapa en enkel produkt-entitet och ett repository för att hantera produkter i en databas. Du kommer att lära dig:

1. Hur du skapar en JPA-entitet
2. Hur du definierar ett Spring Data JPA Repository
3. Hur du använder repositoryt för att utföra databasoperationer
4. Hur allt kopplas ihop, och varför det är så kraftfullt

## Steg 1: Skapa ett Spring Boot-projekt med JPA

Börja med att skapa ett Spring Boot-projekt:

1. Gå till [Spring Initializr](https://start.spring.io/)
2. Välj:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: Välj senaste stabila versionen
   - **Packaging**: Jar
   - **Java**: 17 (eller den version du använder)
3. Lägg till följande beroenden:
   - **Spring Web**
   - **Spring Data JPA**
   - **H2 Database** (för enkel testning)
4. Klicka på "Generate" och ladda ner projektet
5. Öppna projektet i din IDE

## Steg 2: Konfigurera databas

Lägg till följande i `src/main/resources/application.properties`:

```properties
# H2 Database - minnesbaserad för enkel testning
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Aktivera H2 konsolåtkomst
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA-inställningar
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Steg 3: Skapa din första entitet

En entitet är en Java-klass som motsvarar en tabell i databasen. Varje instans av klassen motsvarar en rad i tabellen.

Skapa en ny klass `Product.java` i `src/main/java/com/example/demo/model`:

```java
package com.example.demo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity  // Markerar att den här klassen är en JPA-entitet
public class Product {

    @Id  // Markerar detta fält som primärnyckel
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Automatisk ID-generering
    private Long id;
    
    private String name;
    private String description;
    private double price;
    
    // Standard konstruktor (krävs av JPA)
    public Product() {
    }
    
    // Konstruktor med parametrar för enklare användning
    public Product(String name, String description, double price) {
        this.name = name;
        this.description = description;
        this.price = price;
    }
    
    // Getters och setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
```

### Vad gör annotationerna?

- **@Entity**: Talar om för JPA att denna klass motsvarar en tabell i databasen
- **@Id**: Markerar fältet som primärnyckel i tabellen
- **@GeneratedValue**: Anger att primärnyckeln ska genereras automatiskt när nya rader läggs till

När applikationen startar kommer Hibernate (Spring Boot's JPA-implementation) automatiskt att skapa en `product`-tabell med kolumner för `id`, `name`, `description` och `price`.

## Steg 4: Skapa ditt första Repository

Ett repository ger dig ett enkelt sätt att interagera med databasen. Du definierar bara ett interface, och Spring Data JPA implementerar det åt dig!

Skapa en ny interface `ProductRepository.java` i `src/main/java/com/example/demo/repository`:

```java
package com.example.demo.repository;

import com.example.demo.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Det är allt! Spring Data JPA implementerar detta gränssnitt åt dig
    // Du får CRUD-operationer (Create, Read, Update, Delete) gratis!
}
```

### Vad får du med JpaRepository?

Genom att utöka `JpaRepository<Product, Long>` får du direkt tillgång till dessa metoder:

- `save(Product entity)` - Sparar en produkt
- `findById(Long id)` - Hittar en produkt med angivet ID
- `findAll()` - Hämtar alla produkter
- `deleteById(Long id)` - Tar bort en produkt med angivet ID
- `count()` - Räknar antalet produkter
- ... och många fler

Och du behöver inte skriva en enda rad implementationskod!

## Steg 5: Skapa en service-klass

För att följa bästa praxis, låt oss skapa en service-klass som använder vårt repository:

```java
package com.example.demo.service;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    
    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    // Hämta alla produkter
    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }
    
    // Hitta en produkt efter ID
    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // Spara en produkt
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    // Ta bort en produkt
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
```

## Steg 6: Skapa en controller

Nu skapar vi en RESTful controller för att exponera våra produkter via API:

```java
package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    
    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // Hämta alla produkter
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAllProducts();
    }
    
    // Hämta en produkt med ID
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Skapa en ny produkt
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Product createProduct(@RequestBody Product product) {
        return productService.saveProduct(product);
    }
    
    // Uppdatera en produkt
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, 
                                                @RequestBody Product product) {
        return productService.findProductById(id)
                .map(existingProduct -> {
                    product.setId(id);
                    return ResponseEntity.ok(productService.saveProduct(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Ta bort en produkt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.findProductById(id)
                .map(product -> {
                    productService.deleteProduct(id);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
```

## Steg 7: Lägg till testdata (valfritt)

För att lägga till testdata när applikationen startar, skapa en konfigurations-klass:

```java
package com.example.demo.config;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(ProductRepository productRepository) {
        return args -> {
            // Lägg till några exempelprodukter
            productRepository.save(new Product("Laptop", "Kraftfull bärbar dator", 12000.0));
            productRepository.save(new Product("Mobiltelefon", "Smartphone med bra kamera", 8000.0));
            productRepository.save(new Product("Headset", "Trådlöst headset med brusreducering", 1500.0));
            
            System.out.println("Testdata har lagts till!");
        };
    }
}
```

## Steg 8: Kör och testa applikationen

1. Starta applikationen (kör `main`-metoden i din `Application.java`-fil)
2. Öppna H2-konsolen i webbläsaren: http://localhost:8080/h2-console
   - Använd anslutnings-URL: jdbc:h2:mem:testdb
   - Användarnamn: sa
   - Lösenord: (tomt)
3. Testa dina endpoints med Postman eller en webbläsare:
   - GET http://localhost:8080/api/products
   - GET http://localhost:8080/api/products/1
   - POST http://localhost:8080/api/products (med JSON-body)
   - PUT http://localhost:8080/api/products/1 (med JSON-body)
   - DELETE http://localhost:8080/api/products/1

## Fördelar med JPA och Spring Data JPA som vi just utnyttjat

Nu när vi har byggt vår applikation, låt oss tänka på vad vi fick ut av att använda JPA och Spring Data JPA:

### 1. Minimal kodmängd

Vi behövde inte skriva:
- SQL för att skapa tabeller
- SQL för CRUD-operationer
- Kod för att mappa mellan ResultSet och objekt
- Transaktionshanteringskod

### 2. Objektorienterat tillvägagångssätt

Vi arbetade helt med Java-objekt och lät JPA hantera översättningen till databastabeller och -rader.

### 3. Databasoberoende

Samma kod fungerar med MySQL, PostgreSQL, Oracle, etc. - vi behöver bara ändra konfiguration och drivrutin.

### 4. Fokus på affärslogik

Vi kunde fokusera på vår service- och controller-logik istället för databasåtkomst.

## Utöka ditt Repository med anpassade frågor

Spring Data JPA blir ännu kraftfullare när du lägger till anpassade frågor. Låt oss utöka vårt ProductRepository med några användbara metoder:

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Hitta produkter efter namn (exakt matchning)
    Product findByName(String name);
    
    // Hitta produkter där namnet innehåller en söksträng (inte skiftlägeskänslig)
    List<Product> findByNameContainingIgnoreCase(String nameFragment);
    
    // Hitta produkter under ett visst pris
    List<Product> findByPriceLessThan(double maxPrice);
    
    // Hitta produkter efter pris i ett intervall
    List<Product> findByPriceBetween(double minPrice, double maxPrice);
    
    // Kombination av flera villkor
    List<Product> findByNameContainingIgnoreCaseAndPriceLessThan(String nameFragment, double maxPrice);
}
```

Spring Data JPA analyserar metodnamnen och genererar automatiskt rätt SQL-frågor!

Du kan nu använda dessa i din service:

```java
public List<Product> findCheapProducts(double maxPrice) {
    return productRepository.findByPriceLessThan(maxPrice);
}

public List<Product> searchProducts(String nameFragment) {
    return productRepository.findByNameContainingIgnoreCase(nameFragment);
}
```

## Mer avancerade frågor med @Query

För mer komplexa frågor kan du använda `@Query`-annotationen:

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // JPQL-fråga (objektbaserad)
    @Query("SELECT p FROM Product p WHERE p.price > ?1 AND p.price < ?2")
    List<Product> findProductsInPriceRange(double minPrice, double maxPrice);
    
    // Native SQL-fråga
    @Query(value = "SELECT * FROM product WHERE LENGTH(description) > ?1", nativeQuery = true)
    List<Product> findProductsWithLongDescription(int minLength);
}
```

## Relatera till andra entiteter

Låt oss lägga till en kategori-entitet och visa hur relationer fungerar:

```java
@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // En-till-många relation: En kategori kan ha många produkter
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();
    
    // Konstruktorer, getters och setters...
}
```

Uppdatera `Product.java` för att inkludera relationen:

```java
@Entity
public class Product {
    // Befintliga fält...
    
    // Många-till-en relation: Många produkter kan tillhöra en kategori
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    
    // Uppdatera konstruktörer, getters och setters...
}
```

## Sammanfattning

Du har nu lärt dig grunderna i att arbeta med JPA och Spring Data JPA:

1. Skapa en entitet med rätt annotationer
2. Definiera ett JpaRepository för enkel databasåtkomst
3. Använda standardmetoder som `findAll()`, `findById()`, `save()` och `delete()`
4. Skapa anpassade frågor baserat på konventioner för metodnamn
5. Använda `@Query` för mer komplexa frågor
6. Inse fördelarna: mindre kod, databasoberoende, objektorientering

Detta är bara början! JPA och Spring Data JPA erbjuder många fler funktioner för att hantera komplexa datamodeller, relationer, transaktioner och mycket mer.
