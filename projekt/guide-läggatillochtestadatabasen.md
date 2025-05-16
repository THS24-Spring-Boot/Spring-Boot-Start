# Spring Boot med SQLite - Setup Guide

## 1. Skapa Spring Boot Projekt

### Via Spring Initializr (start.spring.io)
1. Gå till https://start.spring.io
2. Välj:
   - **Project**: Maven
   - **Language**: Java
   - **Spring Boot**: 3.2.x eller senare
   - **Group**: com.example
   - **Artifact**: library-system
   - **Package name**: com.example.librarysystem
   - **Packaging**: Jar
   - **Java**: 17 eller 21

3. Lägg till dependencies:
   - Spring Web
   - Spring Data JPA
   - Spring Boot DevTools

4. Klicka "Generate" och ladda ner

## 2. Lägg till SQLite Dependency

Öppna `pom.xml` och lägg till:

```xml
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.45.1.0</version>
</dependency>
```

## 3. Placera databasfilen

Lägg din `library.db` fil i projektets rotmapp (samma mapp som `pom.xml`).

## 4. Förklaring: Hur JPA normalt fungerar

### JPA's standardbeteende
Normalt sett:
- JPA läser dina `@Entity` klasser
- Skapar databastabeller baserat på dessa entities
- Denna process styrs av `spring.jpa.hibernate.ddl-auto`

### DDL-auto inställningar:
- `create`: Skapar helt nya tabeller (förstör befintlig data!)
- `create-drop`: Skapar tabeller vid start, tar bort vid stopp
- `update`: Försöker uppdatera befintliga tabeller
- `validate`: Kontrollerar att entities matchar befintliga tabeller
- `none`: Rör inte databasen alls

### Vårt problem
Vi har en **befintlig databas** med data, så vi vill:
1. INTE att JPA skapar nya tabeller
2. Använda våra befintliga tabeller
3. Mappa våra entities till befintliga tabeller

## 5. Konfigurera application.properties

```properties
# Databasinställningar
spring.datasource.url=jdbc:sqlite:library.db
spring.datasource.driver-class-name=org.sqlite.JDBC
spring.datasource.username=
spring.datasource.password=

# Använd H2 dialect för SQLite-kompatibilitet
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect

# VIKTIGT: Använd 'none' för att bevara befintlig databas
spring.jpa.hibernate.ddl-auto=none

# Förhindra att JPA försöker skapa schema
spring.jpa.generate-ddl=false

# Debugging (kan tas bort senare)
spring.jpa.show-sql=true
spring.jpa.format-sql=true
```

## 6. Test Entity och Repository

Skapa en minimal entity för att testa anslutningen:

```java
// src/main/java/com/example/librarysystem/entity/Book.java
package com.example.librarysystem.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "books")  // Måste matcha tabellnamnet i databasen
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")  // Måste matcha kolumnnamnet i DB
    private Long id;
    
    @Column(name = "title")
    private String title;
    
    // Default constructor krävs av JPA
    public Book() {}
    
    // Getters och setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
```

```java
// src/main/java/com/example/librarysystem/repository/BookRepository.java
package com.example.librarysystem.repository;

import com.example.librarysystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
```

## 7. Test Controller

```java
// src/main/java/com/example/librarysystem/controller/TestController.java
package com.example.librarysystem.controller;

import com.example.librarysystem.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    
    @Autowired
    private BookRepository bookRepository;
    
    @GetMapping("/test")
    public String test() {
        return "Spring Boot is running!";
    }
    
    @GetMapping("/test/database")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Database connection successful!");
        result.put("book_count", bookRepository.count());
        return result;
    }
}
```

## 8. Testa applikationen

1. **Starta applikationen**: `mvn spring-boot:run`

2. **Testa i browser**:
   - `http://localhost:8080/test` - Bör visa "Spring Boot is running!"
   - `http://localhost:8080/test/database` - Bör visa book count (50)

## 9. Validera entity-mappning

När du skapat dina entities kan du validera att de matchar databasen:

```properties
# Ändra från 'none' till 'validate'
spring.jpa.hibernate.ddl-auto=validate
```

Om du får fel betyder det att dina entities inte matchar databasens struktur.

## 10. Vanliga fel och lösningar

### "Table doesn't exist"
- Kontrollera att `library.db` ligger i rätt plats
- Dubbelkolla `@Table(name = "...")` matchar exakt tabellnamn

### "Wrong column name"
- Kontrollera att `@Column(name = "...")` matchar exakt kolumnnamn i DB
- Använd `hibernate.ddl-auto=validate` för att hitta fel

### "Cannot determine dialect"
- Se till att du har H2 dialect i properties:
  ```properties
  spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
  ```

## Nästa steg

Nu när grundinställningen fungerar kan du:
1. Skapa resterande entities (Author, User, Loan) 
2. Mappa alla kolumner korrekt
3. Bygga dina repositories och services
4. Implementera REST controllers

**Viktigt**: Använd `ddl-auto=validate` för att säkerställa att alla dina entities matchar databasen perfekt innan du fortsätter med affärslogik.
