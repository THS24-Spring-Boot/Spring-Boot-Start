

###  **Övning 1: Skapa en enkel REST-controller**


**Uppgift:**
- Skapa ett nytt Spring Boot-projekt med `spring-boot-starter-web`.
- Skapa en klass `HelloController` med endpoint `/hello` som returnerar `"Hello, world!"`.

---

###  **Övning 2: Trelagersarkitektur – statisk data**


**Uppgift:**
- Skapa en modellklass `Book` (id, title, author).
- Skapa en `BookController`, `BookService` och `BookRepository`.
- Returnera en hårdkodad lista av böcker från `BookRepository`.
- Låt `BookController` returnera JSON från `/books`.

---

###  **Övning 3: Dependency Injection och GET by ID**
**Mål:** Använda `@Autowired` eller konstruktorinjektion för att koppla lager.

**Uppgift:**
- Lägg till en GET-endpoint `/books/{id}` som hämtar en bok med specifikt id.
- Hantera fallet där boken inte hittas (returnera 404).

---

###  **Övning 4: POST – Lägg till en bok**
**Mål:** Introducera POST-endpoints och `@RequestBody`.

**Uppgift:**
- Lägg till ett endpoint `/books` med POST som tar emot en `Book` i JSON-format.
- Spara boken i en lokal lista (än så länge utan databas).
- Returnera den sparade boken som svar.

###  **Övning 5: DELETE – Radera en bok**

**Uppgift:**
- Lägg till ett endpoint `/books/{id}` med DELETE som tar in ett id
- Radera boken från listan 

