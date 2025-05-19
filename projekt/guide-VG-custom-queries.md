# @Query och JPQL i Spring Data JPA - Koncis Guide

## Vad är JPQL?

**JPQL (Java Persistence Query Language)** är ett objektorienterat frågespråk som:
- Påminner om SQL men opererar på **entiteter** istället för tabeller
- Fungerar mot **Entity-objekten** i Java, inte direkt mot databasen
- Är databasoberoende (fungerar med MySQL, SQLite, Oracle, etc.)
- Hanteras av JPA-providern (Hibernate i Spring Boot)

## JPQL vs SQL

| JPQL | SQL |
|------|-----|
| `SELECT b FROM Book b` | `SELECT * FROM books` |
| Använder **entity-namn** (Book) | Använder **tabellnamn** (books) |
| Arbetar med **attributnamn** (title) | Arbetar med **kolumnnamn** (title) |
| Databasoberoende | Databasspecifik |

## @Query Annotation

`@Query` låter dig skriva anpassade frågor i repository-interfacet.

```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // JPQL-exempel
    @Query("SELECT b FROM Book b WHERE b.title LIKE %:keyword% OR b.author.lastName LIKE %:keyword%")
    List<Book> searchByKeyword(@Param("keyword") String keyword);
    
    // Native SQL-exempel (använder databasspecifik SQL)
    @Query(value = "SELECT * FROM books WHERE available_copies > 0", nativeQuery = true)
    List<Book> findAvailableBooks();
    
    // Named parameters
    @Query("SELECT b FROM Book b WHERE b.author.id = :authorId AND b.publicationYear >= :year")
    List<Book> findByAuthorAndYear(@Param("authorId") Long authorId, @Param("year") Integer year);
    
    // Pageable support
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    Page<Book> findAvailableBooks(Pageable pageable);
    
    // Med dynamiska villkor
    @Query("SELECT b FROM Book b WHERE " +
           "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
           "(:authorId IS NULL OR b.author.id = :authorId)")
    List<Book> findWithFilters(@Param("title") String title, @Param("authorId") Long authorId);
    
    // Returned Optional
    @Query("SELECT b FROM Book b WHERE b.isbn = :isbn")
    Optional<Book> findByIsbn(@Param("isbn") String isbn);
    
    // Counting
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
    long countByAuthor(@Param("authorId") Long authorId);
}
```

## JPQL Syntax

### 1. Basic Queries
```java
// Enkel SELECT
"SELECT b FROM Book b"

// SELECT med WHERE
"SELECT b FROM Book b WHERE b.availableCopies > 0"

// Specifika attribut
"SELECT b.title, b.author.lastName FROM Book b"
```

### 2. Joins
```java
// Implicit join med punkt-notation
"SELECT b FROM Book b WHERE b.author.lastName = :lastName"

// Explicit join (INNER JOIN)
"SELECT b FROM Book b JOIN b.author a WHERE a.lastName = :lastName"

// Left join
"SELECT b FROM Book b LEFT JOIN b.loans l WHERE l.returnedDate IS NULL"
```

### 3. Aggregation & Grouping
```java
// COUNT
"SELECT COUNT(b) FROM Book b"

// GROUP BY
"SELECT b.author.id, COUNT(b) FROM Book b GROUP BY b.author.id"

// Aggregation functions
"SELECT AVG(b.publicationYear) FROM Book b WHERE b.author.id = :authorId"
```

### 4. Subqueries
```java
"SELECT b FROM Book b WHERE b.publicationYear > (SELECT AVG(b2.publicationYear) FROM Book b2)"
```

### 5. Ordering
```java
"SELECT b FROM Book b ORDER BY b.title ASC"
```

## Avancerad @Query Användning

### 1. Dynamiska Queries
```java
@Query("SELECT b FROM Book b WHERE " +
       "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
       "(:year IS NULL OR b.publicationYear = :year)")
List<Book> searchBooks(
    @Param("title") String title,
    @Param("year") Integer year
);
```

### 2. Custom Result Projection (DTO)
```java
@Query("SELECT new com.example.dto.BookSummaryDTO(b.id, b.title, a.lastName) " +
       "FROM Book b JOIN b.author a")
List<BookSummaryDTO> getBookSummaries();
```

### 3. Native Queries (Rå SQL)
```java
@Query(value = "SELECT b.* FROM books b " +
               "JOIN loans l ON b.book_id = l.book_id " +
               "GROUP BY b.book_id " +
               "ORDER BY COUNT(l.loan_id) DESC LIMIT 10", 
       nativeQuery = true)
List<Book> findMostBorrowedBooks();
```

### 4. Modifying Queries
```java
@Modifying
@Transactional
@Query("UPDATE Book b SET b.availableCopies = :count WHERE b.id = :id")
int updateAvailableCopies(@Param("id") Long id, @Param("count") Integer count);
```

## Fördelar med @Query

1. **Flexibilitet**: Skriv exakt den query du behöver
2. **Prestanda**: Optimera frågor för specifika behov
3. **Läsbarhet**: Tydligare än långa metodnamn
4. **Komplex logik**: Queries som inte kan uttryckas med method naming

## Best Practices

1. **Använd parametrar**: Alltid använd named parameters med `@Param`
2. **Undvik N+1 problem**: Använd JOIN när du behöver relaterad data
3. **Validera input**: Var försiktig med user-provided input i queries
4. **Välj rätt**: Använd method naming för enkla frågor, @Query för komplexa
5. **Testa**: Verifiera queries med integrationstester

## Vanliga Fallgropar

1. **Case-sensitivity**: JPQL använder entitet/attributnamn, inte tabell/kolumnnamn
2. **Syntax skillnader**: JPQL har vissa skillnader från SQL
3. **Native queries**: Kan bryta när DB-schema ändras
4. **Prestanda**: Optimera för större datamängder med paginering