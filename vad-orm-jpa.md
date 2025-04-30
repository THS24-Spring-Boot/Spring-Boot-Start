# ORM och JPA: Teoretisk Förståelse

## Vad är ORM?

**ORM (Object-Relational Mapping)** är ett programmeringstekniskt koncept där objekt i din kod (klasser i Java) kopplas till tabeller i en relationsdatabas. ORM löser det fundamentala problemet med "impedance mismatch" – skillnaden mellan hur data representeras i objektorienterad programmering och i relationsdatabaser.

### Utan ORM:

Utan ORM behöver du:
1. Skriva SQL-frågor manuellt
2. Konvertera resultaten till objekt manuellt
3. Hantera relationer mellan tabeller manuellt
4. Skriva specifik kod för varje databasoperation

```java
// Utan ORM - måste skriva SQL och mappa manuellt
String sql = "SELECT * FROM products WHERE id = ?";
PreparedStatement stmt = connection.prepareStatement(sql);
stmt.setLong(1, productId);
ResultSet rs = stmt.executeQuery();

Product product = null;
if (rs.next()) {
    product = new Product();
    product.setId(rs.getLong("id"));
    product.setName(rs.getString("name"));
    product.setPrice(rs.getDouble("price"));
    // ... mappa fler fält
}
```

### Med ORM:

Med ORM:
1. Du definierar mappningen mellan klasser och tabeller (ofta med annotationer)
2. Ramverket genererar SQL-frågor åt dig
3. Du arbetar direkt med objekt utan att behöva tänka på SQL
4. Du får automatisk hantering av relationer mellan objekt

```java
// Med ORM - enkelt och objektorienterat
Product product = productRepository.findById(productId).orElse(null);
```

## Fördelar med ORM

1. **Produktivitet**: Minskar mängden kod du behöver skriva
2. **Databasoberoende**: Din kod fungerar med olika databastyper
3. **Objektorienterat**: Du kan arbeta med objekt och relationer naturligt
4. **Typkontroll**: Kompilatorn kan hitta fel i dina databasoperationer
5. **Enkapsulering**: SQL-detaljer döljs inom ORM-lagret
6. **Optimeringar**: ORM-ramverk kan optimera databasåtkomst (cachning, batch-operationer, etc.)

## Nackdelar med ORM

1. **Inlärningskurva**: Tar tid att lära sig använda effektivt
2. **Prestanda**: Kan vara långsammare för komplexa frågor
3. **"Magisk"**: Svårare att veta exakt vilken SQL som körs
4. **N+1-problemet**: Ineffektiv lastning av relationer om det görs fel

## Vad är JPA?

**JPA (Java Persistence API)** är en Java-specifikation som beskriver hur ORM ska fungera i Java. Det är ett standardiserat API för ORM i Java, vilket betyder att olika implementationer av JPA följer samma grundläggande principer och gränssnitt.

JPA definierar:
- Hur entiteter (mappade klasser) ska definieras
- Ett frågespråk (JPQL) som liknar SQL men arbetar med objekt
- Hur transaktioner och livscykler för objekt ska hanteras
- Standardiserade gränssnitt för databasoperationer

## Varför JPA?

- **Standardisering**: Du kan byta implementation utan att ändra din kod
- **Portabilitet**: Din kod fungerar med olika JPA-implementationer
- **Ekosystem**: Många andra ramverk och verktyg integrerar med JPA
- **Utbildning**: Kunskap om JPA kan användas med många olika Java-ramverk

## JPA-implementationer

JPA är bara en specifikation. De faktiska implementationer du kan använda inkluderar:

- **Hibernate**: Den mest populära och funktionsrika implementationen
- **EclipseLink**: Referensimplementationen för JPA
- **OpenJPA**: Apache-projekt med fokus på prestanda

De flesta Spring Boot-applikationer använder Hibernate som JPA-implementation.

## Nyckelbegrepp i JPA

### 1. Entiteter

**Entiteter** är Java-klasser som mappas till databastabeller. De märks med `@Entity`-annotationen.

```java
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private double price;
    
    // getters och setters
}
```

Denna klass motsvarar en `products`-tabell i databasen med kolumner för `id`, `name` och `price`.

### 2. Primärnycklar

Varje entitet måste ha en primärnyckel, identifierad med `@Id`-annotationen. Generering av nyckelvärden kan konfigureras med `@GeneratedValue`.

### 3. Relationer

JPA stöder alla typer av databasrelationer:

- **@OneToOne**: En-till-en-relation
- **@OneToMany / @ManyToOne**: En-till-många / Många-till-en-relation
- **@ManyToMany**: Många-till-många-relation

```java
@Entity
public class Order {
    @Id
    @GeneratedValue
    private Long id;
    
    // Många-till-en: Många ordrar kan tillhöra en kund
    @ManyToOne
    private Customer customer;
    
    // En-till-många: En order kan ha många orderrader
    @OneToMany(mappedBy = "order")
    private List<OrderItem> items;
}
```

### 4. EntityManager

`EntityManager` är det centrala gränssnittet i JPA som hanterar entiteter, inklusive:
- Att spara, uppdatera och ta bort entiteter
- Att köra frågor
- Att hantera relationer mellan entiteter

I Spring Boot används normalt inte EntityManager direkt, utan via Spring Data JPA.

### 5. JPQL

JPQL (Java Persistence Query Language) är ett objektorienterat frågespråk som liknar SQL men arbetar med entiteter istället för tabeller:

```java
// SQL: SELECT * FROM products WHERE price > 100
// JPQL: 
String jpql = "SELECT p FROM Product p WHERE p.price > 100";
List<Product> expensiveProducts = em.createQuery(jpql, Product.class).getResultList();
```

## Spring Data JPA: Vad är det?

Spring Data JPA är ett abstraktionslager ovanpå JPA som ytterligare förenklar databasåtkomst. Det bygger på JPA men lägger till:

1. **Repository-mönstret**: En abstraktionsnivå för att arbeta med samlingar av entiteter
2. **Metoder baserade på konvention**: Genererar implementationer baserat på metodnamn
3. **Paginering och sortering**: Inbyggt stöd för att hantera stora datamängder
4. **Anpassade frågor**: Enkel integration med JPQL, native SQL och Criteria API

## Varför Spring Data JPA?

Spring Data JPA förenklar JPA ännu mer genom att:

1. **Eliminera boilerplate-kod**: Du definierar bara ett interface, Spring implementerar det
2. **Standardisera vanliga operationer**: CRUD-operationer finns tillgängliga utan egen kod
3. **Underlätta frågebyggande**: Bygg frågor från metodnamn utan att skriva JPQL
4. **Integrera med Spring-ekosystemet**: Sömlös integrering med andra Spring-komponenter

## Hur fungerar Spring Data JPA Repositories?

Spring Data JPA repositories är interfaces som du definierar. Spring skapar automatiskt implementeringar av dessa interfaces vid körtid.

```java
// Definiera bara ett interface - ingen implementering behövs!
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Finns automatiskt:
    // - save(Product)
    // - findById(Long)
    // - findAll()
    // - delete(Product)
    // - count()
    // ...och många fler
}
```

Bara genom att definiera detta interface får du:
- Alla grundläggande CRUD-operationer
- Paginering och sortering
- Transaktionshantering
- Och mycket mer

## Frågebyggande med metodnamn

En av de mest kraftfulla funktionerna i Spring Data JPA är att skapa frågor baserat på metodnamn:

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Genererar: SELECT * FROM products WHERE name = ?
    List<Product> findByName(String name);
    
    // Genererar: SELECT * FROM products WHERE price < ?
    List<Product> findByPriceLessThan(double price);
    
    // Genererar: SELECT * FROM products WHERE category = ? ORDER BY price ASC
    List<Product> findByCategoryOrderByPriceAsc(String category);
}
```

Du skriver bara metodsignaturen, och Spring Data JPA genererar implementationen automatiskt!

## Vad får du ut av att använda JPA och Spring Data JPA?

### 1. Drastiskt minskad kodmängd

```java
// Utan ORM/JPA (JDBC)
public Product getProductById(long id) {
    try (Connection conn = dataSource.getConnection();
         PreparedStatement stmt = conn.prepareStatement("SELECT * FROM products WHERE id = ?")) {
        stmt.setLong(1, id);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                Product product = new Product();
                product.setId(rs.getLong("id"));
                product.setName(rs.getString("name"));
                product.setPrice(rs.getDouble("price"));
                return product;
            }
            return null;
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error fetching product", e);
    }
}

// Med Spring Data JPA
public Product getProductById(long id) {
    return productRepository.findById(id).orElse(null);
}
```

### 2. Enklare kodunderhåll

- Mindre mängd kod betyder färre fel
- Ändringar i datamodellen kräver mindre kodändringar
- Tydligare syfte med varje metod

### 3. Databasoberoende

- Samma kod fungerar med MySQL, PostgreSQL, Oracle, etc.
- Byta databas kräver bara konfigurationsändringar, inte kodändringar

### 4. Hantering av relationer

- JPA hanterar automatiskt relationer mellan entiteter
- Lazy loading eller eager loading av relaterade entiteter

### 5. Caching och optimering

- JPA implementationer som Hibernate har inbyggda cachningsfunktioner
- Batch-uppdateringar för bättre prestanda

### 6. Transaktionshantering

- Deklarativ hantering med `@Transactional`
- Automatisk rollback vid undantag

## Sammanfattning

ORM och JPA löser ett grundläggande problem i applikationsutveckling: att överbrygga klyftan mellan objektorienterad kod och relationsdatabaser. Genom att använda JPA, och specifikt Spring Data JPA, kan du:

1. Arbeta med databaser på ett objektorienterat sätt
2. Skriva mycket mindre kod
3. Fokusera på affärslogik istället för databashantering
4. Få kraftfulla funktioner som relationsmappning och caching
5. Skapa lättunderhållen, läsbar kod

I nästa dokument ska vi titta på hur du implementerar dessa koncept praktiskt i en Spring Boot-applikation.
