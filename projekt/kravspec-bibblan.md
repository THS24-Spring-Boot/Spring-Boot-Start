# Kravspecifikation - Bibliotekssystem

## Entiteter
Se den medföljande SQLite-databasen (`library.db`) för exakt struktur av:
- **books** (book_id, title, publication_year, available_copies, total_copies, author_id)
- **authors** (author_id, first_name, last_name, birth_year, nationality)
- **users** (user_id, first_name, last_name, email, password, registration_date)
- **loans** (loan_id, user_id, book_id, borrowed_date, due_date, returned_date)

---

## G-krav (Grundläggande nivå)

### Book Management
- **GET /books** - Lista alla böcker
- **GET /books/search** - Sök böcker på title eller author (query parameters)
- **POST /books** - Skapa ny bok

### Author Management
- **GET /authors** - Lista alla författare
- **GET /authors/name/{lastName}** - Hämta författare via efternamn
- **POST /authors** - Skapa ny författare

### User Management
- **GET /users/email/{email}** - Hämta användare via email
- **POST /users** - Skapa ny användare

### Loan Management
- **GET /users/{userId}/loans** - Hämta användarens lån
- **POST /loans** - Låna bok (kräver userId och bookId)
- **PUT /loans/{id}/return** - Returnera bok
- **PUT /loans/{id}/extend** - Förläng lån

### Service Logic (G)
- Kontrollera boktillgänglighet vid låning
- Minska/öka availableCopies vid lån/retur
- Sätt dueDate till +14 dagar vid låning

### DTOs (G)
- BookWithDetailsDTO (med Author-info)
- UserDTO (utan password)

### Testing (G)
- Skriv ett test som kontrollerar att rätt datum sätts på dueDate när man lägger ett lån
- Skriv ett test som kontrollerar att man inte kan lägga ett lån om boken har 0 available copies

---

## VG-krav (Avancerad nivå)

### 1. Transactional Management
- **@Transactional** för LoanService.createLoan()

### 2. Pagination
- Implementera **Pageable** för GET /books
- Support för sorting och filtering

### 3. ResponseEntity
- Använd **ResponseEntity** för alla endpoints
- Korrekt HTTP status codes (200, 201, 404, 400)

### 4. DTO Mapping Patterns
- Visa minst 2 olika mappningstekniker:
  - Manuell mappning
  - Stream API för listor

### 5. Custom Queries
- **@Query** med JPQL och native SQL
- Optional return types

### 6. Exception Handling
- Custom exceptions (BookNotFoundException, etc.)

---

## Teknisk Stack
- Spring Boot 3.x
- Spring Web + Spring Data JPA
- SQLite databas
- Maven
