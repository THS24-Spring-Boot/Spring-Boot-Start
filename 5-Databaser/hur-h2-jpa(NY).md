# Spring Boot med H2-databas: JPA-anslutning

Denna guide visar hur du snabbt kommer igång med en databas i Spring Boot genom att använda:
- H2 (inbyggd databas, perfekt för utveckling och tester)
- Spring Data JPA för objektrelationell mappning
- RESTful endpoints för att testa databasåtkomsten

## Steg 1: Skapa ett nytt Spring Boot-projekt

1. Gå till [Spring Initializr](https://start.spring.io/)
2. Välj:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: Senaste stabila versionen
   - **Group**: com.example
   - **Artifact**: h2jpademO
   - **Packaging**: Jar
   - **Java**: 17

3. Lägg till följande beroenden:
   - **Spring Web**
   - **Spring Data JPA**
   - **H2 Database**

4. Klicka på "Generate" för att ladda ner projektet
5. Extrahera och öppna projektet i din IDE

## Steg 2: Konfigurera H2-databasen

Öppna `src/main/resources/application.properties` och lägg till följande konfiguration:

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

## Steg 3: Skapa en entitetsklass

Skapa en JPA-entitetsklass för produkter.

Skapa filen `src/main/java/com/example/h2jpademO/entity/Product.java`:

```java
package com.example.h2jpademO.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    private double price;
    
    // Konstruktorer
    public Product() {
    }
    
    public Product(String name, double price) {
        this.name = name;
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
                ", price=" + price +
                '}';
    }
}
```

## Steg 4: Skapa ett JPA Repository

Skapa ett repository-interface för dataåtkomst.

Skapa filen `src/main/java/com/example/h2jpademO/repository/ProductRepository.java`:

```java
package com.example.h2jpademO.repository;

import com.example.h2jpademO.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // Spring Data JPA skapar automatiskt implementationer för standardmetoder som:
    // findAll(), findById(), save(), deleteById() etc.
    
    // Du kan också lägga till anpassade sökmetoder:
    List<Product> findByNameContaining(String name);
    
    List<Product> findByPriceLessThan(double price);
}
```

## Steg 5: Skapa en Service-klass (valfritt men rekommenderat)

Skapa en service-klass för affärslogik.

Skapa filen `src/main/java/com/example/h2jpademO/service/ProductService.java`:

```java
package com.example.h2jpademO.service;

import com.example.h2jpademO.entity.Product;
import com.example.h2jpademO.repository.ProductRepository;
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

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

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

## Steg 6: Skapa en RESTful Controller

Skapa en controller för att hantera HTTP-förfrågningar.

Skapa filen `src/main/java/com/example/h2jpademO/controller/ProductController.java`:

```java
package com.example.h2jpademO.controller;

import com.example.h2jpademO.entity.Product;
import com.example.h2jpademO.service.ProductService;
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

## Steg 7: Lägg till testdata

Skapa filen `src/main/resources/data.sql` för att lägga till testdata:

```sql
INSERT INTO products (name, price) VALUES
('Bärbar dator', 8999.00),
('Smartphone', 6499.00),
('Trådlösa hörlurar', 1499.00);
```

## Steg 8: Starta och testa applikationen

1. Starta Spring Boot-applikationen genom att köra huvudklassen (`H2jpademOApplication`)

2. Testa H2-konsolen:
   - Öppna webbläsaren och gå till http://localhost:8080/h2-console
   - Logga in med:
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Användarnamn: `sa`
     - Lösenord: (tomt)
   - Utforska tabeller och data

3. Testa API-endpoints med Postman:

   **Hämta alla produkter**
   - GET http://localhost:8080/api/products
   
   **Hämta en produkt med ID**
   - GET http://localhost:8080/api/products/1
   
   **Sök efter produkter**
   - GET http://localhost:8080/api/products/search?name=dator
   - GET http://localhost:8080/api/products/search?maxPrice=5000
   
   **Skapa en ny produkt**
   - POST http://localhost:8080/api/products
   - Body (JSON):
     ```json
     {
       "name": "Spelkonsol",
       "price": 4999.00
     }
     ```
   
   **Uppdatera en produkt**
   - PUT http://localhost:8080/api/products/1
   - Body (JSON):
     ```json
     {
       "name": "Premium Laptop",
       "price": 12999.00
     }
     ```
   
   **Ta bort en produkt**
   - DELETE http://localhost:8080/api/products/2

## Fördelar med JPA jämfört med ren JDBC

1. **Mindre kod**: JPA minskar mängden boilerplate-kod jämfört med JDBC
2. **Automatisk mappning**: Entiteter mappas automatiskt till databastabeller
3. **Flexibilitet**: Lättare att byta databasmotor
4. **Relationsmappning**: Enkelt att hantera relationer mellan entiteter
5. **Query-metoder**: Spring Data JPA kan generera sökmetoder från metodnamn

## Sammanfattning

Du har nu skapat en Spring Boot-applikation med:
1. H2 in-memory databas
2. Spring Data JPA för objekt-relationell mappning
3. En Service-klass för affärslogik
4. En RESTful API för CRUD-operationer och sökningar

Denna applikation visar den rekommenderade arkitekturen för de flesta Spring Boot-applikationer med databasåtkomst.

## Nästa steg

- Lägg till validering med Bean Validation
- Implementera fler relationer (One-to-Many, Many-to-Many)
- Bygg ut med ett webbgränssnitt
- Lägg till säkerhet med Spring Security
