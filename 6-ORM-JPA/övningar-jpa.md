# JPA Övningar: Grundläggande steg

Här är några enkla, grundläggande övningar för att lära dig JPA och Spring Data JPA. Vi håller oss på en grundläggande nivå som passar för nybörjare.

## Förberedelser

För att kunna göra dessa övningar behöver du:
- Java Development Kit (JDK) 11 eller senare
- Maven eller Gradle
- En IDE (som IntelliJ IDEA, Eclipse eller VS Code)
- Postman för att testa REST API:er

## Övning 1: Skapa en enkel entitet

**Mål:** Skapa en grundläggande JPA-entitet och se hur den blir en tabell.

**Steg:**

1. Skapa ett nytt Spring Boot-projekt med dessa beroenden:
   - Spring Web
   - Spring Data JPA
   - H2 Database

2. Lägg till H2-konfiguration i application.properties:
   ```
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.datasource.username=sa
   spring.datasource.password=
   
   spring.h2.console.enabled=true
   spring.h2.console.path=/h2-console
   
   spring.jpa.show-sql=true
   ```

3. Skapa en `Book` entitet i paketet `model` med dessa fält:
   - `id` (Long) - märk med @Id och @GeneratedValue
   - `title` (String)
   - `author` (String)
   - `publicationYear` (Integer)

4. Starta applikationen och öppna H2-konsolen (http://localhost:8080/h2-console)

5. Kontrollera att tabellen BOOK har skapats med rätt kolumner

## Övning 2: Skapa ett enkelt Repository

**Mål:** Skapa ett Spring Data JPA repository för att jobba med databasen.

**Steg:**

1. Fortsätt från Övning 1

2. Skapa ett interface `BookRepository` i paketet `repository` som utökar JpaRepository<Book, Long>

3. Skapa en enkel klass för att lägga till testdata:
   ```java
   @Component
   public class DataInitializer implements CommandLineRunner {
   
       private final BookRepository bookRepository;
       
       // Lägg till en konstruktor som tar BookRepository
       
       @Override
       public void run(String... args) {
           // Skapa några böcker och spara dem med bookRepository
           
           // Hämta och skriv ut alla böcker
       }
   }
   ```

4. Starta applikationen och kontrollera att böckerna sparas i databasen

5. Öppna H2-konsolen och kör en SELECT-fråga för att se dina böcker

## Övning 3: Skapa enkla sökmetoder

**Mål:** Lär dig att skapa anpassade sökmetoder i repository.

**Steg:**

1. Fortsätt från Övning 2

2. Lägg till dessa metoder i ditt BookRepository:
   - En metod för att hitta böcker efter författare
   - En metod för att hitta böcker publicerade efter ett visst år
   - En metod för att hitta böcker vars titel innehåller ett visst ord

3. Uppdatera DataInitializer för att testa dina nya metoder

4. Starta applikationen och kontrollera att metoderna fungerar

5. I H2-konsolen, titta på SQL-loggarna för att se vilka frågor som genereras

## Övning 4: Skapa en enkel Service-klass

**Mål:** Skapa ett servicelager mellan repository och controller.

**Steg:**

1. Fortsätt från Övning 3

2. Skapa en `BookService` klass i paketet `service`

3. Injicera BookRepository i servicen

4. Implementera dessa metoder:
   - `List<Book> getAllBooks()`
   - `Optional<Book> getBookById(Long id)`
   - `Book saveBook(Book book)`
   - `void deleteBook(Long id)`
   - `List<Book> findBooksByAuthor(String author)`

5. Uppdatera DataInitializer för att använda BookService istället för BookRepository direkt

6. Starta applikationen och verifiera att allt fungerar

## Övning 5: Skapa en koppling till en annan entitet

**Mål:** Lär dig att koppla samman två entiteter.

**Steg:**

1. Fortsätt från Övning 4

2. Skapa en ny entitet `Author` med:
   - `id` (Long)
   - `name` (String)
   - `birthYear` (Integer)

3. Uppdatera Book-entiteten:
   - Ta bort `author` String-fältet
   - Lägg till en `@ManyToOne`-relation till Author
   - Lägg till `author` (Author)

4. Skapa ett AuthorRepository

5. Uppdatera BookService för att hantera den nya relationen

6. Uppdatera DataInitializer för att skapa författare och koppla dem till böcker

7. Starta applikationen och kontrollera i H2-konsolen att tabellerna är kopplade rätt

## Övning 6: Skapa en enkel Controller

**Mål:** Skapa en REST-controller för Book.

**Steg:**

1. Fortsätt från Övning 5

2. Skapa en `BookController` i paketet `controller`

3. Injicera BookService

4. Implementera dessa endpoints:
   - GET `/api/books` (hämta alla böcker)
   - GET `/api/books/{id}` (hämta en specifik bok)
   - POST `/api/books` (skapa en ny bok)
   - PUT `/api/books/{id}` (uppdatera en bok)
   - DELETE `/api/books/{id}` (ta bort en bok)

5. Starta applikationen

6. Använd Postman för att testa alla endpoints

## Övning 7: Utöka med Author-controller

**Mål:** Skapa en komplett uppsättning av entitet, repository, service och controller.

**Steg:**

1. Fortsätt från Övning 6

2. Skapa en `AuthorService` med grundläggande CRUD-metoder

3. Skapa en `AuthorController` med endpoints för att:
   - Hämta alla författare
   - Hämta en specifik författare
   - Skapa en ny författare
   - Uppdatera en författare
   - Ta bort en författare

4. Lägg till en endpoint i BookController för att:
   - Hämta alla böcker av en specifik författare (GET `/api/books/author/{authorId}`)

5. Starta applikationen

6. Testa alla endpoints med Postman

## Övning 8: Lägg till en tredje entitet

**Mål:** Implementera en tredje entitet och alla tillhörande klasser.

**Steg:**

1. Fortsätt från Övning 7

2. Skapa en ny entitet `Category` med:
   - `id` (Long)
   - `name` (String)

3. Uppdatera Book-entiteten för att lägga till en `@ManyToOne`-relation till Category

4. Skapa ett CategoryRepository

5. Skapa en CategoryService med grundläggande CRUD-metoder

6. Skapa en CategoryController med standard CRUD-endpoints

7. Uppdatera BookService och BookController för att hantera kategorier

8. Uppdatera DataInitializer för att skapa kategorier och tilldela böcker till dem

9. Starta applikationen

10. Testa alla endpoints med Postman

## Sammanfattning och tips

Efter dessa övningar har du nu:

1. Skapat tre JPA-entiteter (Book, Author, Category)
2. Implementerat repositories för alla entiteter
3. Skapat service-lager med affärslogik
4. Byggt REST controllers för att exponera funktionaliteten
5. Testat allt med Postman

### Tips för felsökning:

- Kontrollera SQL-loggarna (spring.jpa.show-sql=true) för att se vad som händer på databasnivå
- När du skapar relationer, var försiktig med hur du hanterar JSON-serialisering för att undvika infinite recursion
- Kom ihåg att JPA-entiteter måste ha en standardkonstruktor
- Om du får valideringsfel, kontrollera att dina entiteter har alla nödvändiga fält

### Nästa steg:

När du är bekväm med dessa grundläggande övningar kan du gå vidare och utforska:
- Mer komplexa relationer (many-to-many)
- Valideringar med Bean Validation
- Paginering och sortering
- Mer avancerade frågor med @Query
