# Spring Boot med H2-databas: JDBC-anslutning

Denna guide visar hur du snabbt kommer igång med en databas i Spring Boot genom att använda:
- H2 (inbyggd databas, perfekt för utveckling och tester)
- JDBC för direkt databashantering (utan JPA/ORM)
- RESTful endpoints för att testa databasåtkomsten

## Steg 1: Skapa ett nytt Spring Boot-projekt

1. Gå till [Spring Initializr](https://start.spring.io/)
2. Välj:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: Senaste stabila versionen
   - **Group**: com.example
   - **Artifact**: h2demo
   - **Packaging**: Jar
   - **Java**: 17

3. Lägg till följande beroenden:
   - **Spring Web**
   - **JDBC API**
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

# Aktivera H2-konsolen
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Visa SQL-frågor i loggen
spring.sql.init.mode=always
logging.level.org.springframework.jdbc.core=DEBUG
```

## Steg 3: Skapa en enkel datamodell

Skapa en enkel Java-klass för att representera en produkt.

Skapa filen `src/main/java/com/example/h2demo/model/Product.java`:

```java
package com.example.h2demo.model;

public class Product {
    private Long id;
    private String name;
    private double price;
    
    // Konstruktorer
    public Product() {
    }
    
    public Product(Long id, String name, double price) {
        this.id = id;
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

## Steg 4: Skapa databastabell och testdata

Skapa filerna:

`src/main/resources/schema.sql`:
```sql
DROP TABLE IF EXISTS products;

CREATE TABLE products (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) NOT NULL,
  price DOUBLE NOT NULL
);
```

`src/main/resources/data.sql`:
```sql
INSERT INTO products (name, price) VALUES
('Bärbar dator', 8999.00),
('Smartphone', 6499.00),
('Trådlösa hörlurar', 1499.00);
```

Spring Boot kommer automatiskt att köra dessa filer när applikationen startar.

## Steg 5: Skapa en Repository-klass för databasåtkomst

Skapa filen `src/main/java/com/example/h2demo/repository/ProductRepository.java`:

```java
package com.example.h2demo.repository;

import com.example.h2demo.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;
    
    private final RowMapper<Product> productRowMapper = (rs, rowNum) -> {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setPrice(rs.getDouble("price"));
        return product;
    };

    @Autowired
    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

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
            // Uppdatera existerande produkt
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

## Steg 6: Skapa en RESTful Controller

Skapa filen `src/main/java/com/example/h2demo/controller/ProductController.java`:

```java
package com.example.h2demo.controller;

import com.example.h2demo.model.Product;
import com.example.h2demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

## Steg 7: Starta och testa applikationen

1. Starta Spring Boot-applikationen genom att köra huvudklassen (`H2demoApplication`)

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

## Sammanfattning

Du har nu skapat en Spring Boot-applikation med:
1. H2 in-memory databas
2. JDBC för direktåtkomst till databasen
3. En RESTful API för CRUD-operationer

Detta är en enkel men funktionell grund som du kan bygga vidare på. För större applikationer bör du överväga att använda JPA istället för JDBC för att förenkla databashanteringen.

## Nästa steg

- Prova att lägga till fler fält till Product-klassen
- Implementera felhantering
- Lägg till validering av inkommande data
- Utöka med mer avancerade sökfunktioner
