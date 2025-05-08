# Spring Boot Entiteter: Grundläggande guide

## Vad är en entitet?

En entitet i Spring Boot är en Java-klass som motsvarar en tabell i databasen. Varje instans (objekt) av entitetsklassen representerar en rad i tabellen. Entiteter används med JPA (Java Persistence API) för att knyta samman Java-objekt med databasposter.

Tänk på en entitet som ett "ritningspapper" för en databastabell. När du definierar en entitet, berättar du för Spring hur din databastabell ska se ut och hur datan ska lagras.

## Grundläggande struktur för en entitet

Låt oss börja med ett enkelt exempel på en entitet:

```java
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

@Entity
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String namn;
    private double pris;
    
    // Konstruktorer, getters och setters kommer här
}
```

Detta skapar en tabell `produkt` med kolumnerna `id`, `namn` och `pris`.

## De viktigaste basannotationerna

### 1. `@Entity`

**Annotation**: `@Entity`  
**Import**: `import jakarta.persistence.Entity;`  
**Placering**: Ovanför klassnamnet

**Förklaring med enkla ord**:  
Denna annotation säger till Spring: "Denna klass ska sparas i databasen." Den markerar klassen som en JPA-entitet, vilket betyder att den ska mappas till en databastabell.

När du använder `@Entity` på en klass, skapas automatiskt en tabell i databasen med samma namn som klassen (om tabellen inte redan finns). Spring kommer sedan att hjälpa dig hantera alla databasåtgärder för denna entitet.

**Exempel**:
```java
@Entity
public class Produkt {
    // innehåll här
}
```

Med denna annotation kommer Spring att skapa en tabell som heter "produkt" i databasen.

### 2. `@Table`

**Annotation**: `@Table`  
**Import**: `import jakarta.persistence.Table;`  
**Placering**: Ovanför klassnamnet, oftast tillsammans med `@Entity`

**Förklaring med enkla ord**:  
Denna annotation ger dig möjlighet att anpassa namnet på tabellen i databasen, samt andra tabellspecifika inställningar. Om du inte använder `@Table` kommer tabellen att få samma namn som klassen (vanligtvis i lowercase).

**Exempel**:
```java
@Entity
@Table(name = "produkter")
public class Produkt {
    // innehåll här
}
```

I detta exempel skapas en tabell med namnet "produkter" istället för "produkt".

**Exempel med avancerade inställningar**:
```java
@Entity
@Table(
    name = "produkter",
    schema = "lager",
    uniqueConstraints = {
        @UniqueConstraint(name = "UK_produkt_kod", columnNames = {"produktKod"})
    },
    indexes = {
        @Index(name = "IDX_produkt_namn", columnList = "namn")
    }
)
public class Produkt {
    // innehåll här
}
```

Denna konfiguration:
- Sätter tabellnamnet till "produkter"
- Placerar tabellen i databasens "lager"-schema
- Skapar en unik constraint på kolumnen produktKod
- Skapar ett index på namn-kolumnen för snabbare sökningar

### 3. `@Id`

**Annotation**: `@Id`  
**Import**: `import jakarta.persistence.Id;`  
**Placering**: Ovanför fältet som representerar primärnyckeln

**Förklaring med enkla ord**:  
Denna annotation markerar vilken egenskap som är primärnyckel (primary key) i databasen. Primärnyckeln är en unik identifierare för varje rad i tabellen, ungefär som ett personnummer för en person. Varje entitet måste ha exakt en `@Id`.

**Exempel**:
```java
@Entity
public class Produkt {
    
    @Id
    private Long id;
    
    // andra fält
}
```

### 4. `@GeneratedValue`

**Annotation**: `@GeneratedValue`  
**Import**: `import jakarta.persistence.GeneratedValue;`  
**Placering**: Används tillsammans med `@Id`

**Förklaring med enkla ord**:  
Denna annotation säger till databasen att automatiskt skapa och öka värdena för primärnyckeln. När du sparar en ny entitet behöver du inte manuellt ange ett ID - databasen gör detta automatiskt.

**Exempel på olika strategier**:

```java
// Auto-increment (vanligast)
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

// Sekvens (vanligt i Oracle)
@Id
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "produkt_seq")
@SequenceGenerator(name = "produkt_seq", sequenceName = "PRODUKT_SEQ", allocationSize = 1)
private Long id;

// Table (databasoberoende)
@Id
@GeneratedValue(strategy = GenerationType.TABLE, generator = "produkt_gen")
@TableGenerator(name = "produkt_gen", table = "id_generator", pkColumnName = "gen_name", 
    valueColumnName = "gen_value", pkColumnValue = "produkt_id", initialValue = 1000)
private Long id;

// UUID (för distribuerade system)
@Id
@GeneratedValue(generator = "UUID")
@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
@Column(updatable = false, nullable = false)
private UUID id;
```

### 5. `@Column`

**Annotation**: `@Column`  
**Import**: `import jakarta.persistence.Column;`  
**Placering**: Ovanför fält som ska mappas till kolumner i tabellen

**Förklaring med enkla ord**:  
Med denna annotation kan du anpassa hur en kolumn ser ut i databasen. Du kan ändra kolumnnamn, ange om fältet kan vara null, sätta maximal längd för text, osv.

Om du inte använder `@Column` kommer kolumnen automatiskt att få samma namn som fältet i Java-klassen.

**Exempel på olika användningar**:
```java
// Grundläggande användning - ändra kolumnnamn
@Column(name = "produkt_namn")
private String namn;

// Ange att kolumnen inte får vara null
@Column(nullable = false)
private String namn;

// Begränsa textlängd
@Column(length = 100)
private String namn;

// Kombinera flera inställningar
@Column(name = "produkt_namn", nullable = false, length = 100)
private String namn;

// För numeriska fält - ange precision och skala
@Column(precision = 10, scale = 2)
private BigDecimal pris;

// För unika värden
@Column(unique = true)
private String produktKod;

// För fält som inte ska uppdateras efter skapande
@Column(updatable = false)
private LocalDateTime skapadDatum;

// För stora textfält
@Column(columnDefinition = "TEXT")
private String beskrivning;

// För binära data (t.ex. bilder)
@Column(columnDefinition = "BLOB")
private byte[] bild;
```

## Java-datatyper och deras SQL-motsvarigheter

När du definierar fält i dina entiteter är det viktigt att förstå hur olika Java-datatyper översätts till SQL-datatyper:

| Java-datatyp | SQL-datatyp (vanligtvis) | Anteckningar |
|--------------|--------------------------|-------------|
| `String` | VARCHAR | Använd `@Column(length=X)` för att ange maxlängd |
| `char` / `Character` | CHAR | Enstaka tecken |
| `boolean` / `Boolean` | BIT, BOOLEAN, TINYINT | Varierar beroende på databas |
| `byte` / `Byte` | TINYINT | 8-bit heltal |
| `short` / `Short` | SMALLINT | 16-bit heltal |
| `int` / `Integer` | INTEGER | 32-bit heltal |
| `long` / `Long` | BIGINT | 64-bit heltal, bra för ID |
| `float` / `Float` | FLOAT | Flyttal med enkel precision |
| `double` / `Double` | DOUBLE | Flyttal med dubbel precision |
| `BigDecimal` | DECIMAL, NUMERIC | För exakta decimala värden (t.ex. pengar) |
| `byte[]` | VARBINARY, BLOB | Binära data (bilder, filer) |
| `java.util.Date` | DATE, TIME, TIMESTAMP | Kräver `@Temporal` |
| `java.time.LocalDate` | DATE | Java 8+ datumtyp |
| `java.time.LocalTime` | TIME | Java 8+ tidtyp |
| `java.time.LocalDateTime` | TIMESTAMP | Java 8+ datum- och tidtyp |
| `java.time.ZonedDateTime` | TIMESTAMP WITH TIMEZONE | Datum och tid med tidszon |
| `java.util.UUID` | UUID, VARCHAR | Unika identifierare |
| `Enum` | VARCHAR, INTEGER | Se nedan för detaljer |

### Exempel på användning av olika datatyper:

```java
@Entity
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String namn;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal pris;
    
    @Column(columnDefinition = "TEXT")
    private String beskrivning;
    
    private LocalDateTime skapadDatum;
    
    @Column(length = 20)
    private String skapareId;
    
    private boolean aktiv;
    
    @Column(name = "total_lager")
    private Integer lagerSaldo;
    
    @Column(columnDefinition = "BLOB")
    private byte[] produktBild;
    
    @Column(unique = true)
    private UUID externtId;
    
    // Konstruktorer, getters och setters
}
```

## Speciella datatypsannotationer

### 1. `@Temporal` för äldre datum-/tidklasser

**Annotation**: `@Temporal`  
**Import**: `import jakarta.persistence.Temporal;`  
**Placering**: Ovanför datum- och tidfält av typerna `java.util.Date` och `java.util.Calendar`

**Förklaring med enkla ord**:  
Denna annotation hjälper till att hantera äldre datum och tid på rätt sätt i databasen.

**Exempel**:
```java
@Entity
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Temporal(TemporalType.DATE)
    private Date orderDatum;        // Endast datum (år, månad, dag)
    
    @Temporal(TemporalType.TIME)
    private Date orderTid;          // Endast tid (timmar, minuter, sekunder)
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderTidpunkt;     // Både datum och tid
    
    // andra fält
}
```

**Observera**: För Java 8+ datum-/tidklasser (LocalDate, LocalDateTime) behöver du inte använda @Temporal eftersom de mappas automatiskt.

```java
// För Java 8+ datum-/tidklasser (rekommenderas för nya projekt)
private LocalDate orderDatum;      // Mappas till DATE
private LocalTime orderTid;        // Mappas till TIME
private LocalDateTime orderTidpunkt; // Mappas till TIMESTAMP
```

### 2. `@Enumerated` för enum-datatyper

**Annotation**: `@Enumerated`  
**Import**: `import jakarta.persistence.Enumerated;`  
**Placering**: Ovanför enum-fält

**Förklaring med enkla ord**:  
Denna annotation används för att tala om hur Java enum-typer ska sparas i databasen. Du kan välja att spara dem som strängar eller som heltal.

**Exempel**:
```java
public enum OrderStatus {
    NY, BEARBETAS, SKICKAD, LEVERERAD, AVBRUTEN
}

@Entity
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Spara enum som STRING - lagrar "NY", "BEARBETAS", etc.
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    // ALTERNATIVT: Spara enum som ORDINAL - lagrar 0, 1, 2, etc.
    // @Enumerated(EnumType.ORDINAL)
    // private OrderStatus status;
    
    // andra fält
}
```

**Rekommendation**: Använd `EnumType.STRING` för bättre läsbarhet och flexibilitet. Med `EnumType.ORDINAL` kan du få problem om du ändrar ordningen på enum-värden i framtiden.

### 3. `@Lob` för stora objekt

**Annotation**: `@Lob`  
**Import**: `import jakarta.persistence.Lob;`  
**Placering**: Ovanför fält som ska lagra stora datamängder

**Förklaring med enkla ord**:  
`@Lob` används för att markera ett fält som ska lagra stora dataobjekt, som långa textavsnitt eller binära data (bilder, filer).

**Exempel**:
```java
@Entity
public class Dokument {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String namn;
    
    // För stora textfält (TEXT/CLOB i databasen)
    @Lob
    private String innehall;
    
    // För binära data (BLOB i databasen)
    @Lob
    private byte[] filData;
    
    // andra fält
}
```

## Validerings- och Constraints-annotationer

JPA-annotationer definierar databaskopplingen, men för datavalidering används ofta Bean Validation API-annotationer:

```java
import jakarta.validation.constraints.*;

@Entity
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Namn är obligatoriskt")
    @Size(min = 2, max = 100, message = "Namnet måste vara mellan 2 och 100 tecken")
    private String namn;
    
    @NotNull(message = "Pris måste anges")
    @Positive(message = "Pris måste vara större än noll")
    private BigDecimal pris;
    
    @Min(value = 0, message = "Lagersaldo kan inte vara negativt")
    private Integer lagerSaldo;
    
    @Email(message = "Ogiltig e-postadress")
    private String kontaktEmail;
    
    @Pattern(regexp = "^[A-Z]{2}\\d{4}$", message = "Produktkod måste vara på formatet: två versaler följt av fyra siffror")
    private String produktKod;
    
    @PastOrPresent(message = "Skapad-datum kan inte vara i framtiden")
    private LocalDate skapadDatum;
    
    @Future(message = "Utgångsdatum måste vara i framtiden")
    private LocalDate utgangsDatum;
    
    // Konstruktorer, getters och setters
}
```

Här är vanliga valideringsannotationer och deras användning:

| Annotation | Används för | Exempel |
|------------|-------------|---------|
| `@NotNull` | Värdet får inte vara null | `@NotNull private Integer antal;` |
| `@NotEmpty` | Samlingar/strängar får inte vara tomma | `@NotEmpty private List<String> taggar;` |
| `@NotBlank` | Strängar får inte vara tomma eller enbart whitespace | `@NotBlank private String namn;` |
| `@Size` | Begränsa storleken på strängar, samlingar | `@Size(min=5, max=50) private String titel;` |
| `@Min` / `@Max` | Begränsa numeriska värden | `@Min(0) @Max(100) private Integer procent;` |
| `@Positive` / `@PositiveOrZero` | För positiva tal | `@Positive private BigDecimal pris;` |
| `@Negative` / `@NegativeOrZero` | För negativa tal | `@Negative private Integer temperatur;` |
| `@Past` / `@PastOrPresent` | För datum i det förflutna | `@Past private LocalDate fodelsedatum;` |
| `@Future` / `@FutureOrPresent` | För datum i framtiden | `@Future private LocalDate utgangsDatum;` |
| `@Pattern` | Validera strängar mot regex | `@Pattern(regexp="^\\d{10}$") private String telefon;` |
| `@Email` | Validera e-postadresser | `@Email private String email;` |
| `@Digits` | Validera numerisk precision | `@Digits(integer=6, fraction=2) private BigDecimal belopp;` |

## Ytterligare användbara annotationer

### 1. `@Transient`

**Annotation**: `@Transient`  
**Import**: `import jakarta.persistence.Transient;`  
**Placering**: Ovanför fält som inte ska sparas i databasen

**Förklaring med enkla ord**:  
Denna annotation används för att markera fält som inte ska sparas i databasen. Det kan vara beräknade värden eller temporära fält.

**Exempel**:
```java
@Entity
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String namn;
    private BigDecimal pris;
    
    @Transient
    private BigDecimal prisInklusiveMoms;
    
    public BigDecimal getPrisInklusiveMoms() {
        return pris.multiply(new BigDecimal("1.25")); // 25% moms
    }
    
    // andra fält och metoder
}
```

I detta exempel kommer "prisInklusiveMoms" inte att sparas i databasen eftersom värdet beräknas från "pris"-fältet.

### 2. Hibernate-specifika annotationer

#### `@CreationTimestamp` och `@UpdateTimestamp`

**Import**: `import org.hibernate.annotations.CreationTimestamp;` och `import org.hibernate.annotations.UpdateTimestamp;`  
**Placering**: Ovanför datum- och tidfält

**Förklaring med enkla ord**:  
Dessa annotationer sätter automatiskt tidsstämplar när en entitet skapas (`@CreationTimestamp`) eller uppdateras (`@UpdateTimestamp`).

**Exempel**:
```java
@Entity
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String namn;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime skapadDatum;
    
    @UpdateTimestamp
    private LocalDateTime uppdateradDatum;
    
    // andra fält
}
```

#### `@ColumnDefault`

**Import**: `import org.hibernate.annotations.ColumnDefault;`  
**Placering**: Ovanför fält som ska ha ett standardvärde

**Förklaring med enkla ord**:  
Denna annotation sätter ett standardvärde för kolumnen på databasnivå.

**Exempel**:
```java
@Entity
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String namn;
    
    @ColumnDefault("0.0")
    private BigDecimal pris;
    
    @ColumnDefault("true")
    private boolean aktiv;
    
    // andra fält
}
```

## Varför använda Long för ID?

Det finns flera skäl till att `Long` ofta används som datatyp för ID-fält i JPA-entiteter:

1. **Kapacitet**: `Long` är ett 64-bitars heltal som kan representera värden upp till ungefär 9 quintiljoner (9 * 10^18), vilket ger mer än tillräckligt med utrymme för de flesta applikationer. `Integer` (32-bit) har en maxgräns på cirka 2 miljarder, vilket kan vara för litet för stora system.

2. **Kompatibilitet**: De flesta databaser har datatyper som direkt motsvarar Java `Long` (t.ex. BIGINT).

3. **Nollvärde**: `Long` är ett objekt som kan vara `null`, vilket kan vara användbart när man arbetar med ej persisterade entiteter eller när ID saknas.

4. **Prestandabalans**: `Long` erbjuder en bra balans mellan prestanda och kapacitet.

5. **Standardpraxis**: Att använda `Long` för ID-fält är en allmänt accepterad praxis inom Java-utveckling.

Alternativ till `Long` för ID:

- **Integer**: För mindre system där du vet att du aldrig kommer att överskrida 2 miljarder poster
- **UUID**: För distribuerade system där IDs behöver genereras oberoende utan central koordinering
- **String**: För egna ID-format eller läsbarhet, men med sämre prestanda

## Komplett exempel på en entitetsklass

Låt oss sätta ihop allt i ett komplett exempel:

```java
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "produkter", 
       uniqueConstraints = @UniqueConstraint(name = "UK_produkt_kod", columnNames = "produkt_kod"),
       indexes = @Index(name = "IDX_produkt_namn", columnList = "namn"))
public class Produkt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Namn är obligatoriskt")
    @Size(min = 2, max = 100)
    @Column(nullable = false)
    private String namn;
    
    @NotNull
    @Positive
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal pris;
    
    @Column(name = "produkt_kod", length = 10, unique = true)
    @Pattern(regexp = "^[A-Z]{2}\\d{4}$")
    private String produktKod;
    
    @Lob
    @Column(columnDefinition = "TEXT")
    private String beskrivning;
    
    @Min(0)
    @Column(name = "lager_saldo")
    private Integer lagerSaldo = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ProduktStatus status = ProduktStatus.AKTIV;
    
    @CreationTimestamp
    @Column(name = "skapad_datum", updatable = false)
    private LocalDateTime skapadDatum;
    
    @UpdateTimestamp
    @Column(name = "uppdaterad_datum")
    private LocalDateTime uppdateradDatum;
    
    @Transient
    private BigDecimal prisMedMoms;
    
    // Enum för produktstatus
    public enum ProduktStatus {
        AKTIV, INAKTIV, UTGANGEN, KOMMANDE
    }
    
    // Konstruktorer
    public Produkt() {
    }
    
    public Produkt(String namn, BigDecimal pris) {
        this.namn = namn;
        this.pris = pris;
    }
    
    // Getters och setters
    public Long getId() {
        return id;
    }
    
    public String getNamn() {
        return namn;
    }
    
    public void setNamn(String namn) {
        this.namn = namn;
    }
    
    public BigDecimal getPris() {
        return pris;
    }
    
    public void setPris(BigDecimal pris) {
        this.pris = pris;
    }
    
    public String getProduktKod() {
        return produktKod;
    }
    
    public void setProduktKod(String produktKod) {
        this.produktKod = produktKod;
    }
    
    public String getBeskrivning() {
        return beskrivning;
    }
    
    public void setBeskrivning(String beskrivning) {
        this.beskrivning = beskrivning;
    }
    
    public Integer getLagerSaldo() {
        return lagerSaldo;
    }
    
    public void setLagerSaldo(Integer lagerSaldo) {
        this.lagerSaldo = lagerSaldo;
    }
    
    public ProduktStatus getStatus() {
        return status;
    }
    
    public void setStatus(ProduktStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getSkapadDatum() {
        return skapadDatum;
    }
    
    public LocalDateTime getUppdateradDatum() {
        return uppdateradDatum;
    }
    
    public BigDecimal getPrisMedMoms() {
        return pris.multiply(new BigDecimal("1.25"));
    }
    
    // toString för bättre debugging
    @Override
    public String toString() {
        return "Produkt{" +
                "id=" + id +
                ", namn='" + namn + '\'' +
                ", pris=" + pris +
                ", status=" + status +
                '}';
    }
}
```

## Vanliga frågor om entiteter

### Varför måste en entitet ha en tom konstruktor?

JPA kräver att alla entiteter har en tom (no-args) konstruktor. Detta eftersom JPA skapar objekt dynamiskt när den läser data från databasen, och då anropar den först den tomma konstruktorn och sätter sedan värden för alla fält.

### Vad är skillnaden mellan @Column(nullable = false) och @NotNull?

- `@Column(nullable = false)` är en JPA-annotation som skapar en databaskonstraint på kolumnnivå.
- `@NotNull` är en validerings-annotation som kontrollerar att värdet inte är null när det valideras, oftast innan det skickas till databasen.

För bästa skydd bör du använda båda: `@Column(nullable = false)` för databasintegritet och `@NotNull` för validering på applikationsnivå.

### Hur hanterar jag fält med stora textmängder?

Använd `@Lob` (Large Object) annotation tillsammans med en lämplig kolumndefinition:

```java
@Lob
@Column(columnDefinition = "TEXT")
private String langBeskrivning;
```

### Hur hanterar jag datum och tid i entiteter?

För moderna applikationer, använd Java 8+ datum-/tidklasser:

```java
// Endast datum
private LocalDate leveransDatum;

// Endast tid
private LocalTime leveransTid;

// Datum och tid
private LocalDateTime orderTidpunkt;

// Datum och tid med tidszon
private ZonedDateTime globalTidpunkt;

// För äldre Date-klasser, använd @Temporal
@Temporal(TemporalType.TIMESTAMP)
private Date gammalTidstampel;
```

### Hur skapar jag sammansatta primärnycklar?

Använd en separat ID-klass med `@Embeddable` och `@EmbeddedId`:

```java
@Embeddable
public class OrderItemId implements Serializable {
    private Long orderId;
    private Long produktId;
    
    // Konstruktorer, equals(), hashCode()
}

@Entity
public class OrderItem {
    @EmbeddedId
    private OrderItemId id;
    
    private Integer antal;
    private BigDecimal pris;
    
    // Övriga fält och metoder
}
```

## Sammanfattning

Entiteter är kärnan i hur Spring Boot och JPA kommunicerar med databasen. Genom att använda olika annotationer kan du:

1. Definiera vilka klasser som ska sparas i databasen (`@Entity`)
2. Anpassa tabeller och kolumner (`@Table`, `@Column`)
3. Definiera primärnycklar och hur de genereras (`@Id`, `@GeneratedValue`)
4. Hantera speciella datatyper (`@Temporal`, `@Enumerated`, `@Lob`)
5. Utesluta fält från databasen (`@Transient`)
6. Validera data med Bean Validation-annotationer (`@NotNull`, `@Size`, etc.)
7. Automatisera tidsstämplar (`@CreationTimestamp`, `@UpdateTimestamp`)

Genom att förstå dessa grundläggande annotationer kan du effektivt modellera din datamodell och låta Spring Boot och JPA hantera databasoperationerna automatiskt.
