# Testing Basics för Spring Boot - Fokuserad Guide

## Varför testa? (2 minuter)

Testing är **kvalitetskontroll för kod**. Precis som du testar att din bil startar innan du kör, testar vi att koden fungerar innan användare får den.

**Fördelar:**
- **Självförtroende**: Du vet att koden fungerar
- **Säkerhet**: Ändra kod utan rädsla för att bryta något
- **Dokumentation**: Tester visar hur koden ska användas

**I denna kurs:** Vi fokuserar på grunderna så du kan testa din Spring Boot-kod. Testing är ett stort ämne - vi täcker bara det nödvändiga.

---

## Test Basics - Anatomin av ett Test

### AAA-mönstret

**Alla bra tester följer samma struktur:**

```java
@Test
void testName() {
    // ARRANGE - Förbered testdata
    LocalDate borrowDate = LocalDate.of(2024, 1, 10);
    
    // ACT - Utför det du testar  
    LocalDate result = loanService.calculateDueDate(borrowDate);
    
    // ASSERT - Kontrollera resultatet
    LocalDate expected = LocalDate.of(2024, 1, 24); // +14 dagar
    assertEquals(expected, result);
}
```

**ARRANGE:** Skapa data som testet behöver
**ACT:** Kör metoden du vill testa
**ASSERT:** Kontrollera att resultatet är korrekt

---

## Varför är det svårt att definiera vad ett test är?

### Problemet med stora metoder

I verkliga Spring Boot-applikationer gör metoder ofta mycket:

```java
@Transactional
public Loan createLoan(Long userId, Long bookId) {
    // 1. Hämta data från databas
    User user = userRepository.findById(userId)...
    Book book = bookRepository.findById(bookId)...
    
    // 2. Validera affärsregler
    if (book.getAvailableCopies() <= 0) throw new Exception...
    
    // 3. Skapa objekt
    Loan loan = new Loan();
    loan.setDueDate(LocalDate.now().plusDays(14));
    
    // 4. Uppdatera databas
    book.setAvailableCopies(book.getAvailableCopies() - 1);
    return loanRepository.save(loan);
}
```

**Ett test av denna metod blir automatiskt:**
- Databas-test (repositories)
- Affärslogik-test (validering)
- Integration-test (flera komponenter)

**Slutsats:** I Spring Boot är gränsen mellan "unit test" och "integration test" suddig. **Det är okej!** Fokusera på att testa att koden fungerar, inte på vad testet "heter".

---

## Vad är Mocking? (Enkelt)

**Mocking = Ersätta riktiga komponenter med "falska" för testet**

### Varför mocka?

**Utan mock:**
```java
// Kräver databas, långsamt, komplicerat setup
User user = userRepository.findById(1L); // Riktig databas-anrop
```

**Med mock:**
```java
// Snabbt, kontrollerat, enkelt
when(userRepository.findById(1L)).thenReturn(testUser); // Fake svar
```

### När mocka?

**Mocka när du vill:**
- **Snabba tester** - Undvik databas-anrop
- **Kontrollera input** - Bestäm vad metoder returnerar
- **Isolera problem** - Testa bara EN sak

**Mocka INTE när:**
- **Du vill testa databas** - Använd riktig databas
- **Du testar integration** - Låt komponenter prata med varandra
- **Det är enklare utan** - Hårdkodad data kan räcka

**För denna kurs:** Vi visar enkla exempel av båda approaches. Välj det som känns enklast för din situation.

---

## Praktiska Exempel - Era Krav

### Krav 1: Testa att rätt datum sätts för dueDate

**Scenario:** När lån skapas ska dueDate vara 14 dagar efter borrowedDate

#### Approach A: Unit Test (Isolerad metod)

```java
class LoanServiceTest {
    
    @Test
    void createLoan_ShouldSetDueDateTo14DaysLater() {
        // ARRANGE - Förbered testdata
        LoanService service = new LoanService();
        LocalDate borrowDate = LocalDate.of(2024, 1, 10); // 10 januari
        
        // ACT - Testa datum-logiken
        Loan loan = service.createLoanWithDate(1L, 2L, borrowDate);
        
        // ASSERT - Kontrollera att dueDate är +14 dagar
        LocalDate expected = LocalDate.of(2024, 1, 24); // 24 januari
        assertEquals(expected, loan.getDueDate());
    }
}
```

#### Approach B: Integration Test (Med Spring Boot)

```java
@SpringBootTest
@Transactional
class LoanIntegrationTest {
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void createLoan_ShouldSetCorrectDueDate() {
        // ARRANGE - Skapa testdata i databas
        User user = new User();
        user.setFirstName("Test");
        user.setEmail("test@test.com");
        user = userRepository.save(user);
        
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAvailableCopies(5);
        book = bookRepository.save(book);
        
        // ACT - Kör hela flödet
        Loan result = loanService.createLoan(user.getId(), book.getId());
        
        // ASSERT - Kontrollera datum (acceptera att det är "ungefär" rätt)
        LocalDate expectedDueDate = LocalDate.now().plusDays(14);
        assertEquals(expectedDueDate, result.getDueDate().toLocalDate());
    }
}
```

---

### Krav 2: Testa att lån inte kan skapas när availableCopies = 0

#### Approach A: Unit Test (Med Mock)

```java
@ExtendWith(MockitoExtension.class)
class LoanServiceMockTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private LoanService loanService;
    
    @Test
    void createLoan_WhenBookNotAvailable_ShouldThrowException() {
        // ARRANGE - Mock returnerar bok utan kopior
        Book unavailableBook = new Book();
        unavailableBook.setId(1L);
        unavailableBook.setAvailableCopies(0); // Inga kopior!
        
        when(bookRepository.findById(1L)).thenReturn(Optional.of(unavailableBook));
        
        // ACT & ASSERT - Förvänta exception
        assertThrows(BookNotAvailableException.class, () -> {
            loanService.createLoan(1L, 1L);
        });
    }
}
```

#### Approach B: Integration Test (Enklare, ingen mock)

```java
@SpringBootTest
@Transactional
class LoanAvailabilityTest {
    
    @Autowired
    private LoanService loanService;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Test
    void createLoan_WhenBookHasZeroCopies_ShouldThrowException() {
        // ARRANGE - Skapa bok utan kopior
        Book book = new Book();
        book.setTitle("Unavailable Book");
        book.setAvailableCopies(0); // Inga kopior!
        book = bookRepository.save(book);
        
        // ACT & ASSERT - Förvänta exception
        assertThrows(BookNotAvailableException.class, () -> {
            loanService.createLoan(1L, book.getId());
        });
    }
}
```

---

## Vilken Approach Ska Du Välja?

### För denna kurs - Rekommendation:

**Börja med Approach B (Integration Tests)**
- ✅ Enklare setup (Spring gör jobbet)
- ✅ Testar "hela flödet"
- ✅ Mer realistiskt
- ✅ Färre koncept att lära

**Gå till Approach A (Unit Tests med Mocks) om:**
- Du vill snabbare tester
- Du vill isolera specifik logik
- Du är bekväm med mocking-syntax

### Båda är korrekta!

**Viktigt:** Det finns inget "rätt" eller "fel" sätt. Båda approaches testar samma affärslogik. Välj det som känns enklast för dig.

---

## Sammanfattning - Det Viktigaste

### För att klara kursen behöver du:

1. **Förstå AAA-mönstret** - Arrange, Act, Assert
2. **Kunna skriva tester** för era två krav:
   - DueDate sätts korrekt
   - Exception när availableCopies = 0
3. **Välja en approach** - Unit test eller Integration test

### Du behöver INTE:

- ❌ Förstå alla test-typer och terminologi
- ❌ Kunna avancerad mocking
- ❌ Skriva perfekta tester
- ❌ Täcka alla edge cases

**Målet:** Grundläggande förståelse för testing så du kan verifiera att din Spring Boot-kod fungerar. Testing är ett stort ämne - detta är bara starten!

### Dependencies som behövs:

```xml
<!-- Spring Boot Test (redan inkluderad i spring-boot-starter-test) -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
```

**Starta enkelt, bygg förståelse steg för steg! 🎯**