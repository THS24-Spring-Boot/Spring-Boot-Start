# Data Transfer Objects (DTOs): En praktisk guide

## Vad är en DTO?

En Data Transfer Object (DTO) är ett designmönster som används för att transportera data mellan olika delar av en applikation eller mellan olika system. I praktiken är en DTO en enkel Java-klass som endast innehåller:

- Privata fält för att lagra data
- Getters och setters för att komma åt och ändra data
- Eventuellt konstruktorer för att skapa objektet
- Eventuellt `equals()`, `hashCode()` och `toString()` metoder

DTOs innehåller **ingen affärslogik** - de är bara behållare för data.

## Varför behöver vi DTOs?

DTOs löser flera viktiga problem i moderna applikationer:

### 1. Separation mellan datamodell och presentation

Dina entiteter (datamodellen) är utformade för att spegla databasens struktur. Men den data du vill visa för användaren (presentationen) behöver inte vara exakt samma som hur den lagras. DTOs låter dig:

- Visa endast den data användaren behöver se
- Formatera om data för presentation
- Kombinera data från flera entiteter till en enda datastruktur

### 2. Skydd mot oändliga loopar vid serialisering

När du har bidirektionella relationer mellan entiteter (till exempel Kund → Order → Kund) uppstår ofta oändliga loopar vid JSON-serialisering. DTOs bryter dessa cykler eftersom du endast inkluderar de data du behöver.

### 3. API-versionshantering

När ditt API utvecklas över tid kan du behöva stödja flera versioner. DTOs gör det enkelt att:

- Behålla bakåtkompatibilitet 
- Lägga till nya fält utan att påverka befintliga klienter
- Gradvis fasa ut äldre dataformat

### 4. Prestanda

DTOs hjälper dig att:

- Minska datamängden som överförs över nätverket
- Undvika att ladda onödig data från databasen

### 5. Säkerhet

Med DTOs kan du:

- Kontrollera exakt vilken data som exponeras externt
- Hindra känsliga fält från att läcka till API-svar

## Enkla exempel på DTOs

### Exempel 1: Grundläggande DTO

**Entitet (datamodell):**

```java
@Entity
public class Kund {
    @Id @GeneratedValue
    private Long id;
    
    private String namn;
    private String email;
    private String telefonNummer;
    private LocalDate födelsedatum;
    private String lösenordHash;
    private LocalDateTime senastInloggad;
    private boolean aktiv;
    
    // Relationer
    @OneToMany(mappedBy = "kund")
    private List<Order> ordrar = new ArrayList<>();
    
    // Getters och setters
}
```

**DTO (för presentation):**

```java
public class KundDTO {
    private Long id;
    private String namn;
    private String email;
    
    // Konstruktor
    public KundDTO() {}
    
    public KundDTO(Long id, String namn, String email) {
        this.id = id;
        this.namn = namn;
        this.email = email;
    }
    
    // Getters och setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNamn() {
        return namn;
    }
    
    public void setNamn(String namn) {
        this.namn = namn;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
}
```

Observera att KundDTO bara innehåller de fält som behövs för presentation - den utelämnar känslig information som lösenordhash, födelsedatum och full kontaktinformation.

### Exempel 2: DTO med nästlade objekt

**Entiteter:**

```java
@Entity
public class Order {
    @Id @GeneratedValue
    private Long id;
    private LocalDateTime orderDatum;
    private String status;
    
    @ManyToOne
    private Kund kund;
    
    @OneToMany(mappedBy = "order")
    private List<OrderRad> orderRader = new ArrayList<>();
    
    // Getters och setters
}

@Entity
public class OrderRad {
    @Id @GeneratedValue
    private Long id;
    private int antal;
    private BigDecimal pris;
    
    @ManyToOne
    private Order order;
    
    @ManyToOne
    private Produkt produkt;
    
    // Getters och setters
}

@Entity
public class Produkt {
    @Id @GeneratedValue
    private Long id;
    private String namn;
    private String beskrivning;
    private BigDecimal pris;
    
    // Getters och setters
}
```

**DTOs för detaljerad ordervisning:**

```java
public class OrderDetaljerDTO {
    private Long id;
    private LocalDateTime orderDatum;
    private String status;
    private KundSammanfattningDTO kund;
    private List<OrderRadDTO> orderRader = new ArrayList<>();
    private BigDecimal totalbelopp;
    
    // Getters och setters
}

public class KundSammanfattningDTO {
    private Long id;
    private String namn;
    
    // Getters och setters
}

public class OrderRadDTO {
    private Long id;
    private int antal;
    private BigDecimal styckPris;
    private BigDecimal radBelopp;
    private String produktNamn;
    
    // Getters och setters
}
```

Här ser vi flera fördelar med DTOs:
1. Vi inkluderar bara den kundinformation som behövs i ordervisningen
2. Vi beräknar och inkluderar totalbelopp och radbelopp i DTOn, istället för att göra dessa beräkningar på klientsidan
3. Vi bryter de cykliska beroendena mellan Order, OrderRad och Produkt
4. Vi förenklar strukturen genom att endast ta med produktens namn, inte hela produktobjektet

### Exempel 3: DTO för aggregerad data

DTOs är utmärkta för att aggregera data från flera entiteter.

```java
public class DashboardDTO {
    private int antalAktivaKunder;
    private int antalOrderarIdag;
    private BigDecimal totalFörsäljningIdag;
    private List<TopProduktDTO> toppProdukter = new ArrayList<>();
    
    // Getters och setters
}

public class TopProduktDTO {
    private String produktNamn;
    private int antalSålda;
    
    // Getters och setters
}
```

Denna DTO kombinerar data från flera entiteter för att presentera en dashboard-vy.

## När ska man använda DTOs?

DTOs är särskilt användbara i följande situationer:

### Bör använda DTOs:

1. **I REST API-svar**
   - Skyddar känslig information
   - Optimerar dataöverföringen
   - Hindrar oändliga loopar

2. **Vid komplexa datastrukturer**
   - Förenklar komplexa modeller
   - Kombinerar data från flera källor

3. **Vid datakonvertering mellan system**
   - Översätter mellan olika dataformat
   - Hjälper till med versionshantering

### Kanske inte behöver DTOs:

1. **Enkla CRUD-operationer i interna system**
   - För mycket boilerplate-kod för enkla uppgifter

2. **Prototyper och snabba utvecklingsprojekt**
   - Kan införas senare när applikationen växer

3. **När entitetsmodellen exakt matchar visningsbehovet**
   - Sällsynt, men händer ibland för enkla entiteter

## Vanliga mönster för DTOs

### Request vs Response DTOs

Det är ofta bra att separera DTOs för inkommande förfrågningar (Request) och utgående svar (Response):

```java
// För inkommande data
public class KundSkapaRequestDTO {
    private String namn;
    private String email;
    private String lösenord;  // Klartextlösenord tas emot för skapande
    
    // Getters och setters
}

// För utgående svar
public class KundResponseDTO {
    private Long id;
    private String namn;
    private String email;
    private LocalDateTime skapadDatum;
    
    // Getters och setters
}
```

### DTO-hierarkier

För att minska duplicering kan du skapa en hierarki av DTOs:

```java
// Bas-DTO med gemensamma fält
public class ProduktBasDTO {
    private Long id;
    private String namn;
    
    // Getters och setters
}

// Utökad version med fler detaljer
public class ProduktDetaljerDTO extends ProduktBasDTO {
    private String beskrivning;
    private BigDecimal pris;
    private List<String> kategorier = new ArrayList<>();
    
    // Getters och setters
}
```

## Bästa praxis för DTOs

1. **Håll DTOs enkla**
   - Inga komplexa beräkningar
   - Ingen affärslogik
   - Inga anrop till tjänster eller databaser

2. **Namnge DTOs tydligt**
   - Tydliga suffix som visar att det är en DTO (KundDTO)
   - Ange syftet (KundSammanfattningDTO, OrderDetaljerDTO)

3. **Validera data i separata validerare**
   - Använd Bean Validation (@NotNull, @Size, osv.) i Request DTOs

4. **Använd builders eller konstruktorer för att skapa DTOs**
   - Gör det lättare att skapa och underhålla

5. **Överväg att göra DTOs immutable (oföränderliga)**
   - Använd final-fält
   - Skapa endast getters, inte setters
   - Ange alla värden i konstruktorn

```java
public class ProduktDTO {
    private final Long id;
    private final String namn;
    private final BigDecimal pris;
    
    public ProduktDTO(Long id, String namn, BigDecimal pris) {
        this.id = id;
        this.namn = namn;
        this.pris = pris;
    }
    
    // Endast getters
    public Long getId() { return id; }
    public String getNamn() { return namn; }
    public BigDecimal getPris() { return pris; }
}
```

## Sammanfattning

DTOs är ett kraftfullt designmönster som hjälper dig att:

- Separera presentationslogik från datamodellen
- Skydda känslig information
- Optimera dataöverföring
- Undvika serialiseringsproblem
- Hantera API-versioner

De är enkla att implementera och ger många fördelar, särskilt i API-baserade applikationer. Med tydliga DTOs blir din kod mer flexibel, säkrare och lättare att underhålla.

För att fullt utnyttja DTOs behöver du också effektiva sätt att mappa mellan entiteter och DTOs - se vår separata guide om mappning för mer information.
