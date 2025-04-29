# Praktiska övningar för Spring Boot RestController

## Förberedelser

1. Skapa ett nytt Spring Boot-projekt med Spring Initializr (https://start.spring.io/)
   - Välj **Spring Web** som beroende
   - Ladda ner och öppna projektet i din IDE

2. Skapa följande klasser som vi kommer att använda i övningarna:

**Product.java**
```java
package com.example.demo.model;

public class Product {
    private Long id;
    private String name;
    private String description;
    private double price;

    // Konstruktorer, getters och setters
    // (lägg till dessa själv)
}
```

**ProductService.java**
```java
package com.example.demo.service;

import com.example.demo.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    private final List<Product> products = new ArrayList<>();
    private long nextId = 1;

    // Lägg till några exempelprodukter i konstruktorn
    public ProductService() {
        // Skapa några produkter och lägg till dem i products-listan
        Product laptop = new Product();
        laptop.setId(nextId++);
        laptop.setName("Laptop");
        laptop.setDescription("Kraftfull bärbar dator");
        laptop.setPrice(12000.0);
        products.add(laptop);
        
        Product phone = new Product();
        phone.setId(nextId++);
        phone.setName("Mobiltelefon");
        phone.setDescription("Smartphone med bra kamera");
        phone.setPrice(8000.0);
        products.add(phone);
        
        Product headset = new Product();
        headset.setId(nextId++);
        headset.setName("Headset");
        headset.setDescription("Trådlöst headset med brusreducering");
        headset.setPrice(1500.0);
        products.add(headset);
    }

    // Hämta alla produkter
    public List<Product> getAllProducts() {
        return products;
    }

    // Hämta en specifik produkt med ID
    public Product getProductById(Long id) {
        for (Product product : products) {
            if (product.getId().equals(id)) {
                return product;
            }
        }
        return null;
    }

    // Lägg till en ny produkt
    public Product addProduct(Product product) {
        product.setId(nextId++);
        products.add(product);
        return product;
    }

    // Uppdatera en befintlig produkt
    public Product updateProduct(Long id, Product updatedProduct) {
        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);
            if (product.getId().equals(id)) {
                updatedProduct.setId(id);
                products.set(i, updatedProduct);
                return updatedProduct;
            }
        }
        return null;
    }

    // Ta bort en produkt
    public boolean deleteProduct(Long id) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equals(id)) {
                products.remove(i);
                return true;
            }
        }
        return false;
    }
}
```

## Övning 1: Skapa en grundläggande RestController

**Mål:** Skapa en controller som kan visa alla produkter och en specifik produkt med ID.

**Uppgift:**
1. Skapa en ny klass `ProductController` i paketet `com.example.demo.controller`
2. Markera klassen med rätt annotation för att göra den till en RestController
3. Konfigurera grundläggande URL-väg till `/api/products` för alla metoder
4. Injicera ProductService med konstruktorinjektion
5. Implementera en metod för att hantera GET-förfrågan som ska visa alla produkter
6. Implementera en metod för att hantera GET-förfrågan som ska visa en specifik produkt med ID
7. Testa dina endpoints med en webbläsare eller Postman

**Tips:**
- Använd `@RestController` för att markera klassen
- Använd `@RequestMapping` för att ange grundläggande URL-väg
- Använd `@GetMapping` för GET-metoder
- Använd `@PathVariable` för att extrahera ID från URL:en

## Övning 2: Implementera CRUD-operationer

**Mål:** Utöka din controller med metoder för att skapa, uppdatera och ta bort produkter.

**Uppgift:**
1. Implementera en metod för att hantera POST-förfrågan som ska skapa en ny produkt
2. Implementera en metod för att hantera PUT-förfrågan som ska uppdatera en befintlig produkt
3. Implementera en metod för att hantera DELETE-förfrågan som ska ta bort en produkt
4. Testa dina nya endpoints med Postman

**Tips:**
- Använd `@PostMapping` för att hantera POST-förfrågan
- Använd `@PutMapping` för att hantera PUT-förfrågan
- Använd `@DeleteMapping` för att hantera DELETE-förfrågan
- Använd `@RequestBody` för att konvertera JSON i HTTP-begäran till ett Java-objekt

## Övning 3: Implementera sökning och filtrering

**Mål:** Lägg till möjlighet att söka och filtrera produkter.

**Uppgift:**
1. Lägg till en ny metod i `ProductService` för att söka produkter efter namn:
```java
public List<Product> searchProductsByName(String name) {
    List<Product> result = new ArrayList<>();
    for (Product product : products) {
        if (product.getName().toLowerCase().contains(name.toLowerCase())) {
            result.add(product);
        }
    }
    return result;
}
```

2. Implementera en ny metod i din controller för att hantera sökning
3. Använd `@RequestParam` för att ta emot sökparametrar
4. Testa din nya endpoint med Postman

**Tips:**
- Använd en URL som `/api/products/search?name=abc` för sökning
- Använd `@GetMapping("/search")` för att mappa denna URL
- Använd `@RequestParam` för att extrahera namn-parametern

## Övning 4: Använda ResponseEntity

**Mål:** Förbättra dina API-svar genom att använda ResponseEntity.

**Uppgift:**
1. Uppdatera alla metoder i din controller för att returnera `ResponseEntity<>` istället för direkta objekt
2. Använd lämpliga HTTP-statuskoder för olika situationer:
   - 200 OK för lyckade GET-, PUT-förfrågningar
   - 201 Created för lyckade POST-förfrågningar
   - 204 No Content för lyckade DELETE-förfrågningar
   - 404 Not Found när en resurs inte hittas
3. Testa att alla endpoints fortfarande fungerar och nu returnerar korrekta statuskoder

**Tips:**
- Använd `ResponseEntity.ok(data)` för att returnera data med 200 OK
- Använd `ResponseEntity.status(HttpStatus.CREATED).body(data)` för att returnera data med 201 Created
- Använd `ResponseEntity.noContent().build()` för att returnera 204 No Content
- Använd `ResponseEntity.notFound().build()` för att returnera 404 Not Found

## Övning 5: Implementera versionshantering av API

**Mål:** Lär dig hur man hanterar olika versioner av ett API.

**Uppgift:**
1. Skapa en ny produktmodell `ProductV2` som innehåller ytterligare ett fält, t.ex. `category`
2. Skapa en ny controller `ProductV2Controller` som hanterar den nya modellen
3. Konfigurera URL-väg till `/api/v2/products` för den nya controllern
4. Implementera samma metoder som i den ursprungliga controllern
5. Testa att båda versionerna av API:et fungerar parallellt

**Tips:**
- Detta ger dig insikt i hur man hanterar API-utveckling över tid
- I verkliga projekt kan du använda olika strategier för versionshantering (URL-väg, headers, parametrar)

## Övning 6: Testa dina endpoints med Postman

**Mål:** Lär dig att testa REST API:er med Postman.

**Uppgift:**
1. Ladda ner och installera Postman om du inte redan har det (https://www.postman.com/downloads/)
2. Skapa en ny Postman-samling för ditt API
3. Skapa begäran för varje endpoint i ditt API:
   - GET alla produkter
   - GET en specifik produkt
   - POST för att skapa en produkt
   - PUT för att uppdatera en produkt
   - DELETE för att ta bort en produkt
   - GET för att söka produkter
4. Spara begärandena i samlingen så att du enkelt kan återanvända dem
5. Använd Postman för att verifiera att dina endpoints fungerar korrekt och returnerar förväntade statuskoder

**Tips:**
- För POST och PUT, kom ihåg att lägga till en JSON-body
- Kontrollera att Content-Type är inställd på application/json
- Använd miljövariabler i Postman för att hantera basURL (t.ex. http://localhost:8080)

### Exempel på testning med Postman

- **GET** `http://localhost:8080/api/products` för att hämta alla produkter
- **GET** `http://localhost:8080/api/products/1` för att hämta en specifik produkt
- **POST** `http://localhost:8080/api/products` med en JSON-body för att skapa en produkt:
  ```json
  {
    "name": "Datormus",
    "description": "Ergonomisk trådlös mus",
    "price": 450.0
  }
  ```
- **PUT** `http://localhost:8080/api/products/1` med en JSON-body för att uppdatera en produkt:
  ```json
  {
    "name": "Premium Laptop",
    "description": "Uppdaterad beskrivning",
    "price": 13000.0
  }
  ```
- **DELETE** `http://localhost:8080/api/products/1` för att ta bort en produkt
- **GET** `http://localhost:8080/api/products/search?name=Lap` för att söka efter produkter med "Lap" i namnet
