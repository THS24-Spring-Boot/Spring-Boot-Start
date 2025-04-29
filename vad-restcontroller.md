# Spring RestController
## Teoretisk guide med praktiskt sammanhang

### Vad är en RestController?

En RestController i Spring är din applikations sätt att kommunicera med omvärlden via HTTP. Tänk på den som en receptionist på ett hotell - den tar emot förfrågningar från besökare (klienter), skickar dem vidare till rätt avdelning (services) och ger sedan ett svar tillbaka.

I tekniska termer är RestController en komponent som hanterar RESTful webbtjänster. REST (Representational State Transfer) är ett arkitekturmönster för att bygga API:er som följer vissa principer för att göra dem enkla, förutsägbara och skalbara.

### Vad är skillnaden mellan @Controller och @RestController?

- **@Controller** används för traditionella webbapplikationer där vyerna (HTML-sidor) renderas på serversidan.
- **@RestController** är en specialiserad version av Controller som är anpassad för RESTful webbtjänster som skickar och tar emot data i format som JSON.

Tekniskt sett är `@RestController = @Controller + @ResponseBody`, vilket betyder att den automatiskt konverterar returdata till JSON/XML.

### Varför behöver vi RestController?

I moderna applikationer behöver vi ofta:
- Bygga API:er som kan användas av många olika klienter (webbapplikationer, mobilappar, andra system)
- Utbyta data i ett format som är lätt att bearbeta (som JSON)
- Följa standardiserade metoder för kommunikation

RestController förenklar allt detta genom att:
1. Automatiskt konvertera Java-objekt till JSON (eller XML)
2. Hantera olika HTTP-metoder (GET, POST, PUT, DELETE)
3. Göra det enkelt att bygga API:er som följer REST-principer

### De viktigaste annotationerna för RestController

#### @RestController
Denna annotation markerar en klass som en controller där alla metoder automatiskt kommer att returnera data (vanligtvis JSON) istället för en vy.

```java
@RestController
public class BookController {
    // Metoder här...
}
```

När du lägger till `@RestController` på en klass, talar du om för Spring att:
- Denna klass kommer att hantera HTTP-förfrågningar
- Svaren från metoderna ska automatiskt konverteras till JSON
- Den här klassen är en specialiserad typ av Spring-komponent

#### @RequestMapping
Denna annotation definierar den grundläggande URL-sökvägen (eller "roten") för alla endpoints i din controller.

```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    // Alla endpoints kommer att börja med /api/books
}
```

Du kan också ange vilken HTTP-metod som ska användas:

```java
@RequestMapping(value = "/api/books", method = RequestMethod.GET)
```

Men för tydlighets skull är det ofta bättre att använda de specifika metodannotationerna som vi kommer att se nedan.

#### @GetMapping
Denna annotation kopplar HTTP GET-förfrågningar till en specifik metod. GET används för att hämta data utan att ändra något.

```java
@GetMapping
public List<Book> getAllBooks() {
    // Detta hanterar GET /api/books
    return bookService.findAllBooks();
}

@GetMapping("/bestsellers")
public List<Book> getBestsellers() {
    // Detta hanterar GET /api/books/bestsellers
    return bookService.findBestsellers();
}
```

GET-förfrågningar är perfekta för operationer som bara läser data. De är så vanliga att webbläsare använder dem när du skriver in en URL i adressfältet.

#### @PostMapping
Denna annotation kopplar HTTP POST-förfrågningar till en metod. POST används för att skapa nya resurser.

```java
@PostMapping
public Book createBook(@RequestBody Book book) {
    // Detta hanterar POST /api/books
    return bookService.saveBook(book);
}
```

POST-förfrågningar innehåller ofta data i request-body, vilket vi hanterar med `@RequestBody` (mer om detta nedan).

#### @PutMapping
Denna annotation kopplar HTTP PUT-förfrågningar till en metod. PUT används för att uppdatera en befintlig resurs.

```java
@PutMapping("/{id}")
public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
    // Detta hanterar PUT /api/books/123
    return bookService.updateBook(id, bookDetails);
}
```

PUT-förfrågningar ersätter vanligtvis hela resursen med nya data.

#### @DeleteMapping
Denna annotation kopplar HTTP DELETE-förfrågningar till en metod. DELETE används för att ta bort en resurs.

```java
@DeleteMapping("/{id}")
public void deleteBook(@PathVariable Long id) {
    // Detta hanterar DELETE /api/books/123
    bookService.deleteBook(id);
}
```

DELETE-förfrågningar används för att ta bort resurser och kräver ofta en identifierare för att veta vad som ska tas bort.

#### @PathVariable
Denna annotation extraherar värden från URL-sökvägen. Du kan använda den för att fånga dynamiska delar av URL:en.

```java
@GetMapping("/{id}")
public Book getBookById(@PathVariable Long id) {
    // Om URL är /api/books/123, kommer id att vara 123
    return bookService.findBookById(id);
}
```

Du kan också ge parametern ett explicit namn om det behövs:

```java
@GetMapping("/{bookId}")
public Book getBookById(@PathVariable("bookId") Long id) {
    // Om URL är /api/books/123, kommer id att vara 123
    return bookService.findBookById(id);
}
```

Detta är särskilt användbart när variabelnamnet i URL:en och metodparametern inte matchar.

#### @RequestBody
Denna annotation konverterar automatiskt JSON-data i HTTP-begäran till ett Java-objekt.

```java
@PostMapping
public Book createBook(@RequestBody Book book) {
    // JSON-data i begäran konverteras till ett Book-objekt
    return bookService.saveBook(book);
}
```

Spring använder Jackson (eller en annan JSON-bibliotek) för att automatiskt konvertera JSON till Java-objekt och vice versa. Detta gör det mycket enkelt att ta emot och skicka komplexa objekt.

#### @RequestParam
Denna annotation hämtar parametrar från URL-frågesträngen (det som kommer efter ?).

```java
@GetMapping("/search")
public List<Book> searchBooks(@RequestParam String title) {
    // Hanterar GET /api/books/search?title=Harry
    return bookService.findBooksByTitle(title);
}
```

Du kan också ange att parametern är valfri och ge den ett standardvärde:

```java
@GetMapping("/search")
public List<Book> searchBooks(
    @RequestParam(required = false, defaultValue = "") String title,
    @RequestParam(required = false, defaultValue = "0") Integer page
) {
    // Hanterar GET /api/books/search?title=Harry&page=2
    return bookService.findBooksByTitle(title, page);
}
```

### Ett praktiskt exempel: Bokbibliotek API

Låt oss se hur alla dessa annotationer kan användas tillsammans för att skapa ett enkelt API för ett bokbibliotek:

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    // Konstruktor-injektion av BookService
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Hämta alla böcker
    @GetMapping
    public List<Book> getAllBooks() {
        return bookService.findAllBooks();
    }

    // Hämta en specifik bok med ID
    @GetMapping("/{id}")
    public Book getBookById(@PathVariable Long id) {
        return bookService.findBookById(id);
    }

    // Sök efter böcker baserat på titel
    @GetMapping("/search")
    public List<Book> searchBooks(@RequestParam String title) {
        return bookService.findBooksByTitle(title);
    }

    // Skapa en ny bok
    @PostMapping
    public Book createBook(@RequestBody Book book) {
        return bookService.saveBook(book);
    }

    // Uppdatera en befintlig bok
    @PutMapping("/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
        return bookService.updateBook(id, bookDetails);
    }

    // Ta bort en bok
    @DeleteMapping("/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }
}
```

### Hur detta fungerar i praktiken

När en HTTP-förfrågan kommer in till din Spring Boot-applikation:

1. Spring identifierar vilken controller och metod som ska hantera förfrågan baserat på URL och HTTP-metod
2. Spring extraherar data från URL:en (@PathVariable), frågesträngen (@RequestParam) och/eller request-body (@RequestBody)
3. Spring anropar lämplig metod i din controller med de extraherade parametrarna
4. Din metod bearbetar förfrågan och returnerar ett resultat
5. Spring konverterar automatiskt resultatet till JSON och skickar det tillbaka till klienten

### Vad ska vara i en Controller och vad ska INTE vara där?

En vanlig fallgrop för nybörjare är att lägga för mycket logik i controllern. Det är viktigt att förstå vad som hör hemma i en controller och vad som bör flyttas till andra lager, särskilt service-lagret.

#### Vad som BÖR vara i en Controller:

1. **Request-hantering**: Ta emot HTTP-förfrågningar och extrahera data från dem
2. **Input-validering**: Grundläggande validering av inkommande data
3. **Anrop till service-lager**: Delegera affärslogik till service-klasser
4. **Response-formatering**: Strukturera svar som ska skickas tillbaka

#### Vad som INTE bör vara i en Controller utan istället i en Service:

1. **Affärslogik**: All affärslogik som implementerar verksamhetsregler
   ```java
   // INTE i controllern:
   if (book.getPrice() > 100 && book.getCategory().equals("Fiction")) {
       book.setDiscount(0.1);  // 10% rabatt
   }
   
   // Istället anropa servicen:
   bookService.applyPricingRules(book);
   ```

2. **Databearbetning**: Komplex filtrering, mappning eller transformering av data
   ```java
   // INTE i controllern:
   List<Book> expensiveBooks = allBooks.stream()
       .filter(b -> b.getPrice() > 200)
       .collect(Collectors.toList());
   
   // Istället anropa servicen:
   List<Book> expensiveBooks = bookService.findExpensiveBooks();
   ```

3. **Databasinteraktion**: All kod som arbetar direkt med databaser
   ```java
   // INTE i controllern:
   Book book = bookRepository.findById(id).orElse(null);
   
   // Istället anropa servicen:
   Book book = bookService.findBookById(id);
   ```

4. **Transaktionshantering**: Kod som hanterar transaktioner
   ```java
   // INTE i controllern:
   @Transactional
   public void transferBook(Long bookId, Long fromUser, Long toUser) { ... }
   
   // Istället anropa servicen:
   bookService.transferBook(bookId, fromUser, toUser);
   ```

5. **Komplex felhantering**: Detaljerad felhantering och återhämtning
   ```java
   // INTE i controllern:
   try {
       // Komplex logik...
   } catch (SpecificException e) {
       // Återhämtningslogik...
   }
   
   // Istället anropa servicen som hanterar felen:
   bookService.processBooksWithErrorHandling();
   ```

#### En välbalanserad Controller

Här är ett exempel på en välbalanserad controller som följer dessa principer:

```java
@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public List<BookDto> getAllBooks() {
        // Controller ansvarar för att ta emot förfrågan och returnera resultatet
        // Men delegerar allt faktiskt arbete till servicen
        return bookService.findAllBooks();
    }

    @PostMapping
    public BookDto createBook(@RequestBody BookDto bookDto) {
        // Controllern validerar inkommande data
        if (bookDto.getTitle() == null || bookDto.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        
        // Men den faktiska affärslogiken delegeras till servicen
        return bookService.createBook(bookDto);
    }
}
```

#### Service-lagret

Service-klasser innehåller affärslogiken och interagerar med repositories (dataåtkomst). En typisk service för vår bokapplikation kan se ut så här:

```java
@Service
public class BookService {

    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<BookDto> findAllBooks() {
        // Hämta böcker från repository
        List<Book> books = bookRepository.findAll();
        
        // Transformera entiteter till DTOs (affärslogik)
        return books.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public BookDto createBook(BookDto bookDto) {
        // Validera affärsregler
        validateBookBusinessRules(bookDto);
        
        // Konvertera DTO till entitet
        Book book = convertToEntity(bookDto);
        
        // Tilldela kategorier baserat på affärsregler
        assignCategories(book);
        
        // Spara och konvertera tillbaka
        Book savedBook = bookRepository.save(book);
        return convertToDto(savedBook);
    }
    
    // Privata hjälpmetoder för affärslogik
    private void validateBookBusinessRules(BookDto bookDto) {
        // Implementera affärsregler
    }
    
    private void assignCategories(Book book) {
        // Tillämpa affärslogik för kategorisering
    }
    
    // Konverteringsmetoder
    private BookDto convertToDto(Book book) {
        // Konverteringslogik
    }
    
    private Book convertToEntity(BookDto dto) {
        // Konverteringslogik
    }
}
```

### Sammanfattning

RestController är en kraftfull komponent i Spring som gör det enkelt att bygga RESTful API:er. De viktigaste annotationerna är:

- **@RestController**: Markerar klassen som en controller för REST-tjänster
- **@RequestMapping**: Definierar grundläggande URL-sökvägen för alla metoder
- **@GetMapping, @PostMapping, @PutMapping, @DeleteMapping**: Kopplar HTTP-metoder till kontrollermetoder
- **@PathVariable**: Extraherar värden från URL-sökvägen
- **@RequestBody**: Konverterar JSON i HTTP-begäran till ett Java-objekt
- **@RequestParam**: Hämtar parametrar från URL-frågesträngen

Genom att förstå och använda dessa annotationer, samt upprätthålla en tydlig separation av ansvarsområden mellan controllers och services, kan du bygga välstrukturerade och underhållbara API:er med Spring Boot.
