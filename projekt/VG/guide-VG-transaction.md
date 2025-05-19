# @Transactional i Spring Boot - Koncis Guide

## Vad är @Transactional?

**@Transactional** säkerställer att en metod (eller klass) exekveras inom en databastransaktion. Detta garanterar att:

1. **Atomicitet**: Alla operationer lyckas eller misslyckas tillsammans
2. **Konsistens**: Databasen förblir i konsistent tillstånd
3. **Isolering**: Transaktioner är isolerade från varandra
4. **Hållbarhet**: Förändringar är permanenta efter commit

## När använda @Transactional?

Använd @Transactional när en operation:
- Ändrar **flera rader** eller **flera tabeller**
- Kräver **all-or-nothing** garanti
- Innehåller **flera steg** som måste lyckas tillsammans

## Enkel implementation (LoanService)

```java
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanService {
    
    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    
    @Autowired
    public LoanService(BookRepository bookRepository, LoanRepository loanRepository) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
    }
    
    @Transactional  // Garanterar att båda operationer lyckas eller misslyckas
    public LoanDTO createLoan(CreateLoanRequest request) {
        // 1. Hämta bok och kontrollera tillgänglighet
        Book book = bookRepository.findById(request.getBookId())
            .orElseThrow(() -> new BookNotFoundException("Book not found"));
            
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException("Book not available");
        }
        
        // 2. Minska tillgängliga kopior
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);  // Operation 1
        
        // 3. Skapa lån
        Loan loan = new Loan();
        loan.setUserId(request.getUserId());
        loan.setBookId(book.getId());
        loan.setBorrowedDate(LocalDateTime.now());
        loan.setDueDate(LocalDateTime.now().plusDays(14));
        
        Loan savedLoan = loanRepository.save(loan);  // Operation 2
        
        // Om något går fel mellan operation 1 och 2 → ROLLBACK!
        return convertToDTO(savedLoan);
    }
    
    @Transactional(readOnly = true)  // Optimering för läsoperationer
    public List<LoanDTO> getUserLoans(Long userId) {
        return loanRepository.findByUserId(userId)
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private LoanDTO convertToDTO(Loan loan) {
        // Mappning kod här...
    }
}
```

## Vad händer utan @Transactional?

Utan `@Transactional` i `createLoan()`-metoden:

```java
public LoanDTO createLoan(CreateLoanRequest request) {
    // Bok uppdateras (availableCopies minskar)
    book.setAvailableCopies(book.getAvailableCopies() - 1);
    bookRepository.save(book);
    
    // Om ett fel inträffar här (t.ex. Exception) →
    // Boken har redan uppdaterats, men inget lån skapas!
    // → INKONSISTENT DATA
    
    Loan loan = new Loan();
    // ...
    loanRepository.save(loan);
}
```

## Viktiga @Transactional-parametrar

```java
// Bara läsning (optimering)
@Transactional(readOnly = true)

// Specificera vilka exceptions som triggar rollback
@Transactional(rollbackFor = {CustomException.class})

// Isolationsnivå
@Transactional(isolation = Isolation.READ_COMMITTED)

// Timeout
@Transactional(timeout = 60)
```

## Vanliga misstag

1. **Self-invocation**: @Transactional fungerar inte vid anrop från samma klass
2. **Private metoder**: @Transactional fungerar inte på private metoder
3. **Runtime vs Checked Exceptions**: Endast Runtime Exceptions triggar rollback by default

## Best Practices

1. Applicera `@Transactional` på **service**-nivå, inte på repositories eller controllers
2. Använd `readOnly=true` för alla read-operationer
3. Gör transaktioner så **korta** som möjligt
4. Var medveten om att transaktioner kan **låsa** rader i databasen
