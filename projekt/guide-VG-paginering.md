# Pagination i Spring Boot - Koncis Guide

## Vad är Pagination?

**Pagination** (sidnumrering) delar upp stora resultatset i hanterbara "sidor" för att:
1. **Förbättra prestanda**: Hämtar bara en del av datan
2. **Förbättra UX**: Användare kan navigera genom data
3. **Minska nätverkstrafik**: Mindre dataöverföring per request

## Spring Data JPA Pagination

Spring Data JPA har inbyggt stöd för pagination via `Pageable`-parametern.

## Enkel Implementation

### 1. Repository
```java
public interface BookRepository extends JpaRepository<Book, Long> {
    // Grundläggande paginering ingår automatiskt i JpaRepository!
    
    // Custom query med paginering
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    
    // Sök med flera parametrar
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:authorId IS NULL OR b.authorId = :authorId)")
    Page<Book> searchBooks(@Param("title") String title, 
                          @Param("authorId") Long authorId,
                          Pageable pageable);
}
```

### 2. Service
```java
@Service
public class BookService {
    
    private final BookRepository bookRepository;
    
    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    @Transactional(readOnly = true)
    public Page<BookDTO> getAllBooks(Pageable pageable) {
        // Hämta en Page av Book-entiteter
        Page<Book> bookPage = bookRepository.findAll(pageable);
        
        // Omvandla till Page av BookDTO genom att mappa innehållet
        return bookPage.map(this::convertToDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<BookDTO> searchBooks(String title, Long authorId, Pageable pageable) {
        return bookRepository.searchBooks(title, authorId, pageable)
                            .map(this::convertToDTO);
    }
    
    private BookDTO convertToDTO(Book book) {
        // Mappning kod här...
    }
}
```

### 3. Controller
```java
@RestController
@RequestMapping("/books")
public class BookController {
    
    private final BookService bookService;
    
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }
    
    @GetMapping
    public ResponseEntity<Page<BookDTO>> getAllBooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        // Skapa Pageable objekt från parametrar
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<BookDTO> books = bookService.getAllBooks(pageable);
        return ResponseEntity.ok(books);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<BookDTO>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "title") String sort,
            @RequestParam(defaultValue = "asc") String direction) {
        
        Sort sortObj = Sort.by(Sort.Direction.fromString(direction), sort);
        Pageable pageable = PageRequest.of(page, size, sortObj);
        
        Page<BookDTO> books = bookService.searchBooks(title, authorId, pageable);
        return ResponseEntity.ok(books);
    }
}
```

## Hur man använder API:et (Exempel URLs)

```
GET /books?page=0&size=20
GET /books?page=1&size=10&sort=title&direction=desc
GET /books/search?title=potter&page=0&size=5
```

## Page Response Format

```json
{
  "content": [
    { "id": 1, "title": "Harry Potter", ... },
    { "id": 2, "title": "Lord of the Rings", ... },
    ...
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": { "sorted": true, ... }
  },
  "totalPages": 5,
  "totalElements": 42,
  "last": false,
  "size": 10,
  "number": 0,
  "first": true,
  "numberOfElements": 10,
  "empty": false
}
```

## Viktiga Metoder och Attribut i Page

- **content**: Listan med resultat på denna sida
- **totalElements**: Totalt antal element över alla sidor
- **totalPages**: Antal sidor med aktuell storlek
- **pageNumber/number**: Aktuell sidnummer (0-baserat)
- **pageSize/size**: Antal element per sida
- **first/last/empty**: Booleans som indikerar position
- **hasNext()/hasPrevious()**: Metoder för att kolla navigering

## Sorteringsalternativ

```java
// Enkel sortering
Sort sort = Sort.by("title");

// Fallande ordning
Sort sort = Sort.by(Sort.Direction.DESC, "title");

// Flera fält
Sort sort = Sort.by(Sort.Order.desc("totalCopies"), 
                  Sort.Order.asc("title"));
```

## Performance Tips

1. **Använd optimerad SQL**: Spring genererar vanligtvis COUNT-query + data-query
2. **Sätt rimliga page sizes**: 10-20 items är oftast lagom
3. **Caching**: Överväg caching för sidor som besöks ofta
4. **Indexera**: Se till att kolumner som filtreras/sorteras är indexerade

## Vanliga Misstag

1. **Returnera hela listan**: Ignorera pageable och returnera all data
2. **Glömma validera input**: Alltid validera page och size
3. **Skapar tunga DTOs**: Tunga transformationer på sidan förstör prestanda-fördelarna