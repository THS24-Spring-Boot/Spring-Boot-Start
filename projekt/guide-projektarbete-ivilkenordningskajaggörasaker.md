# Arbetsguide - Bibliotekssystem

## 📋 Rekommenderad Ordning

### 1. Setup och Databasanslutning
- [ ] Skapa Spring Boot projekt (Spring Initializr)
- [ ] Lägg till SQLite dependency i `pom.xml`
- [ ] Placera `library.db` i projektets rot
- [ ] Konfigurera `application.properties` (använd `ddl-auto=none`)
- [ ] Skapa minimal test controller för att verifiera att Spring Boot startar

### 2. Projektstruktur
**Rekommenderad package-struktur:**
```
com.example.library
├── entity/         # @Entity klasser
├── repository/     # JPA repositories
├── service/        # Business logic
├── controller/     # REST endpoints
├── dto/           # Data Transfer Objects
└── config/        # Konfiguration (om behövs)
```

### 3. Börja med Book (Enklast)
- [ ] Skapa `Book` entity med rätt mappning till `books` tabellen
- [ ] Skapa `BookRepository extends JpaRepository<Book, Long>`
- [ ] Skapa `BookService` med grundläggande metoder
- [ ] Skapa `BookController` med GET endpoints
- [ ] **TESTA**: `GET /books` ska returnera alla böcker

### 4. Lägg till Author 
- [ ] Skapa `Author` entity
- [ ] Skapa `AuthorRepository`
- [ ] Skapa `AuthorService`
- [ ] Skapa `AuthorController`
- [ ] **TESTA**: Endpoints fungerar

### 5. DTOs och Mappning
- [ ] Skapa `BookDTO`, `AuthorDTO`
- [ ] Lägg till mappning-metoder i services
- [ ] Uppdatera controllers att returnera DTOs
- [ ] Skapa `BookWithDetailsDTO` (med Author-info)

### 6. User Entitet
- [ ] Skapa `User` entity (tänk på password-hantering)
- [ ] Skapa repository, service, controller
- [ ] **VIKTIGT**: UserDTO ska INTE inkludera password

### 7. Loan - Den Komplexa Delen
- [ ] Skapa `Loan` entity med rätt relations
- [ ] Skapa `LoanRepository` med custom queries
- [ ] Skapa `LoanService` med affärslogik:
  - Kontrollera boktillgänglighet
  - Minska/öka `availableCopies`
  - Sätt `dueDate` (+14 dagar)
- [ ] Skapa `LoanController`
- [ ] **TESTA NOGA**: Låning och återlämning

### 8. Validering och Felhantering
- [ ] Lägg till validering i services
- [ ] Implementera lämplig felhantering
- [ ] Testa edge cases (bok ej tillgänglig, etc.)

### 9. Testning
- [ ] Skriv unittest för `LoanService.createLoan()`
- [ ] Skriv integrationstest för `POST /loans`

### 10. VG-implementationer (om tillämpligt)
- [ ] Lägg till `@Transactional` på loan operations
- [ ] Implementera `Pageable` för books
- [ ] Byt till `ResponseEntity` i controllers
- [ ] Lägg till `@Query` annotations
- [ ] Implementera `@ControllerAdvice`

## 🎯 Tips för Framgång

**Börja Enkelt:**
- Implementera GET endpoints först
- Lägg till POST senare
- Spara komplex logik (loans) till sist

**Testa Löpande:**
- Kör applikationen efter varje steg
- Använd Postman/browser för att testa endpoints
- Kontrollera logs för hibernate-queries

**Debugging:**
- Sätt `spring.jpa.show-sql=true` 
- Använd `spring.jpa.hibernate.ddl-auto=validate` för att kontrollera entity-mappning
- Kolla SQLite direkt med `sqlite3 library.db`

**Vanliga Misstag att Undvika:**
- Glöm inte `@Repository`, `@Service`, `@RestController` annotations
- Matcha kolumnnamn exakt med `@Column(name = "...")`
- Tänk på cascade-beteende vid saving
- Konfiguration av `ddl-auto=none` för befintlig databas

## ⚡ Snabbcheck: Fungerar det?
Efter varje steg, testa:
1. Applikationen startar utan fel
2. Endpoints svarar som förväntat
3. Data hämtas korrekt från databasen
4. Logs visar förväntade SQL queries

**Lycka till! 🚀**