# Mappning mellan entiteter och DTOs: En praktisk guide

När du arbetar med DTOs i din Spring Boot-applikation behöver du en effektiv metod för att konvertera mellan dina entiteter (datamodell) och dina DTOs (presentationsmodell). Detta kallas för "mappning".

## Vad är mappning?

Mappning är processen att konvertera ett objekt av en typ till ett objekt av en annan typ. I kontexten av Spring Boot-applikationer handlar det ofta om att konvertera:

1. **Entity → DTO**: När du ska visa data genom ditt API
2. **DTO → Entity**: När du tar emot data från klienter och ska spara den i databasen

## Mappningsstrategier

Det finns flera sätt att implementera mappning i din applikation:

### 1. Manuell mappning

Den enklaste metoden är att skriva mappningskoden själv. Detta är tydligt och ger full kontroll.

```java
@Service
public class KundService {
    
    // Entity → DTO
    public KundDTO convertToDto(Kund kund) {
        KundDTO dto = new KundDTO();
        dto.setId(kund.getId());
        dto.setNamn(kund.getNamn());
        dto.setEmail(kund.getEmail());
        return dto;
    }
    
    // DTO → Entity
    public Kund convertToEntity(KundDTO dto) {
        Kund kund = new Kund();
        kund.setId(dto.getId());
        kund.setNamn(dto.getNamn());
        kund.setEmail(dto.getEmail());
        return kund;
    }
}
```

**Fördelar:**
- Enkel att implementera för små projekt
- Full kontroll över mappningsprocessen
- Inga externa beroenden

**Nackdelar:**
- Blir repetitiv för stora modeller
- Måste uppdateras manuellt när DTOs eller entiteter ändras
- Mer felbenägen

### 2. Dedikerade Mapper-klasser

Ett bättre sätt är att skapa dedikerade klasser för mappning:

```java
@Component
public class KundMapper {
    
    // Entity → DTO
    public KundDTO toDto(Kund kund) {
        if (kund == null) {
            return null;
        }
        
        KundDTO dto = new KundDTO();
        dto.setId(kund.getId());
        dto.setNamn(kund.getNamn());
        dto.setEmail(kund.getEmail());
        return dto;
    }
    
    // DTO → Entity
    public Kund toEntity(KundDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Kund kund = new Kund();
        kund.setId(dto.getId());
        kund.setNamn(dto.getNamn());
        kund.setEmail(dto.getEmail());
        return kund;
    }
    
    // Konvertera en lista av entiteter till en lista av DTOs
    public List<KundDTO> toDtoList(List<Kund> kunder) {
        if (kunder == null) {
            return Collections.emptyList();
        }
        
        return kunder.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
    
    // Konvertera en lista av DTOs till en lista av entiteter
    public List<Kund> toEntityList(List<KundDTO> dtos) {
        if (dtos == null) {
            return Collections.emptyList();
        }
        
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}
```

**Fördelar:**
- Separerad mappningslogik
- Återanvändbar kod
- Lättare att testa
- Bättre felhantering

**Nackdelar:**
- Fortfarande manuell kodning
- Måste uppdateras när modeller ändras

### 3. Använda mappningsbibliotek

För större projekt eller mer komplexa mappningar finns flera bibliotek som kan hjälpa:

#### MapStruct

MapStruct är ett Java-baserat kodgenereringsverktyg som skapar mappningskod automatiskt genom att använda annotationer och gränssnitt.

**Steg 1: Lägg till beroenden i pom.xml**

```xml
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct</artifactId>
    <version>1.5.3.Final</version>
</dependency>
<dependency>
    <groupId>org.mapstruct</groupId>
    <artifactId>mapstruct-processor</artifactId>
    <version>1.5.3.Final</version>
    <scope>provided</scope>
</dependency>
```

**Steg 2: Definiera ett Mapper-gränssnitt**

```java
@Mapper(componentModel = "spring")
public interface KundMapper {

    KundDTO toDto(Kund kund);
    
    Kund toEntity(KundDTO dto);
    
    List<KundDTO> toDtoList(List<Kund> kunder);
    
    List<Kund> toEntityList(List<KundDTO> dtos);
}
```

**Steg 3: Använd i din service eller controller**

```java
@Service
public class KundService {
    
    private final KundRepository kundRepository;
    private final KundMapper kundMapper;
    
    @Autowired
    public KundService(KundRepository kundRepository, KundMapper kundMapper) {
        this.kundRepository = kundRepository;
        this.kundMapper = kundMapper;
    }
    
    public List<KundDTO> getAllKunder() {
        List<Kund> kunder = kundRepository.findAll();
        return kundMapper.toDtoList(kunder);
    }
    
    public KundDTO saveKund(KundDTO kundDTO) {
        Kund kund = kundMapper.toEntity(kundDTO);
        Kund savedKund = kundRepository.save(kund);
        return kundMapper.toDto(savedKund);
    }
}
```

**Fördelar med MapStruct:**
- Genererar effektiv mappningskod vid kompilering
- Automatisk hantering av null-värden
- Stöd för komplexa mappningar och typkonverteringar
- Uttrycksfull API för anpassade mappningar
- Felmeddelanden vid kompilering, inte runtime

### 4. Effektiv mappning med Java Streams och Lambda-uttryck

Java Streams och Lambda-uttryck ger ett kraftfullt sätt att implementera mappning, särskilt när du arbetar med samlingar av objekt. Detta är en förbättring av den manuella mappningsmetoden men utan externa beroenden.

**Mappning av enskilda objekt:**

```java
@Component
public class KundMapper {
    
    public KundDTO toDto(Kund kund) {
        if (kund == null) {
            return null;
        }
        
        return new KundDTO(
            kund.getId(),
            kund.getNamn(),
            kund.getEmail()
        );
    }
    
    public Kund toEntity(KundDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Kund kund = new Kund();
        kund.setId(dto.getId());
        kund.setNamn(dto.getNamn());
        kund.setEmail(dto.getEmail());
        return kund;
    }
    
    public List<KundDTO> toDtoList(List<Kund> kunder) {
        if (kunder == null) {
            return Collections.emptyList();
        }
        
        return kunder.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
```

**Mappning med filtrering:**

```java
public List<KundDTO> findActiveKunder() {
    return kundRepository.findAll().stream()
            .filter(Kund::isActive)  // Filtrera endast aktiva kunder
            .map(kund -> new KundDTO(
                kund.getId(),
                kund.getNamn(),
                kund.getEmail()
            ))
            .collect(Collectors.toList());
}
```

**Grupperad mappning:**

```java
public Map<String, List<KundDTO>> getKunderByCity() {
    return kundRepository.findAll().stream()
            .collect(Collectors.groupingBy(
                Kund::getStad,  // Gruppera efter stad
                Collectors.mapping(
                    kund -> new KundDTO(
                        kund.getId(),
                        kund.getNamn(),
                        kund.getEmail()
                    ),
                    Collectors.toList()
                )
            ));
}
```

**För-bearbetning före mappning:**

```java
public List<KundSummaryDTO> getKundSummaries() {
    return kundRepository.findAll().stream()
            .filter(kund -> kund.getOrders().size() > 0)  // Filtrera kunder med beställningar
            .map(kund -> {
                // Beräkna total ordersumma
                BigDecimal totalOrderValue = kund.getOrders().stream()
                        .map(Order::getTotalBelopp)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                
                // Skapa och returnera sammanfattnings-DTO
                return new KundSummaryDTO(
                    kund.getId(),
                    kund.getNamn(),
                    kund.getOrders().size(),
                    totalOrderValue
                );
            })
            .sorted(Comparator.comparing(KundSummaryDTO::getTotalOrderValue).reversed())  // Sortera efter ordervärde
            .collect(Collectors.toList());
}
```

**Fördelar med Stream/Lambda-mappning:**
- Deklarativ och lättläst kod
- Bra för samlingsbaserade operationer
- Inbyggt stöd för filtrering, sortering och gruppering
- Ingen extra konfiguration eller beroenden
- Bättre prestanda än reflektionsbaserade lösningar

**Nackdelar:**
- Manuell kodning krävs fortfarande
- Kan bli komplext för djupt nästlade objektstrukturer

## Hantering av komplexa mappningsscenarier

### 1. Mappa nästlade objekt

#### Med manuell mappning:

```java
@Component
public class OrderMapper {
    
    private final KundMapper kundMapper;
    private final ProduktMapper produktMapper;
    
    @Autowired
    public OrderMapper(KundMapper kundMapper, ProduktMapper produktMapper) {
        this.kundMapper = kundMapper;
        this.produktMapper = produktMapper;
    }
    
    public OrderDTO toDto(Order order) {
        if (order == null) {
            return null;
        }
        
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderDatum(order.getOrderDatum());
        dto.setStatus(order.getStatus());
        
        // Mappa kund
        if (order.getKund() != null) {
            dto.setKund(kundMapper.toDto(order.getKund()));
        }
        
        // Mappa orderrader
        if (order.getOrderRader() != null) {
            List<OrderRadDTO> orderRadDTOs = order.getOrderRader().stream()
                    .map(this::mapOrderRadToDto)
                    .collect(Collectors.toList());
            dto.setOrderRader(orderRadDTOs);
        }
        
        return dto;
    }
    
    private OrderRadDTO mapOrderRadToDto(OrderRad orderRad) {
        OrderRadDTO dto = new OrderRadDTO();
        dto.setId(orderRad.getId());
        dto.setAntal(orderRad.getAntal());
        dto.setStyckPris(orderRad.getPris());
        
        // Beräkna radbelopp
        dto.setRadBelopp(orderRad.getPris().multiply(BigDecimal.valueOf(orderRad.getAntal())));
        
        // Hämta produktinformation
        if (orderRad.getProdukt() != null) {
            dto.setProduktNamn(orderRad.getProdukt().getNamn());
        }
        
        return dto;
    }
}
```

#### Med MapStruct:

```java
@Mapper(componentModel = "spring", uses = {KundMapper.class})
public interface OrderMapper {
    
    OrderDTO toDto(Order order);
    
    // Anpassad mappning för OrderRad → OrderRadDTO
    @Mapping(target = "produktNamn", source = "produkt.namn")
    @Mapping(target = "radBelopp", expression = "java(orderRad.getPris().multiply(BigDecimal.valueOf(orderRad.getAntal())))")
    OrderRadDTO orderRadToDto(OrderRad orderRad);
    
    // Övriga mappningsmetoder...
}
```

### 2. Mappa olika fältnamn

#### Med manuell mappning:

```java
public KundDTO toDto(Kund kund) {
    KundDTO dto = new KundDTO();
    dto.setId(kund.getId());
    dto.setFullName(kund.getNamn());  // Olika namn: namn → fullName
    dto.setEmailAdress(kund.getEmail());  // Olika namn: email → emailAdress
    return dto;
}
```

#### Med MapStruct:

```java
@Mapper(componentModel = "spring")
public interface KundMapper {
    
    @Mapping(source = "namn", target = "fullName")
    @Mapping(source = "email", target = "emailAdress")
    KundDTO toDto(Kund kund);
    
    @Mapping(source = "fullName", target = "namn")
    @Mapping(source = "emailAdress", target = "email")
    Kund toEntity(KundDTO dto);
}
```

### 3. Hantera beräknade fält

#### Med manuell mappning:

```java
public ProduktDTO toDto(Produkt produkt) {
    ProduktDTO dto = new ProduktDTO();
    dto.setId(produkt.getId());
    dto.setNamn(produkt.getNamn());
    dto.setPris(produkt.getPris());
    
    // Beräkna priset med moms
    dto.setPrisMedMoms(produkt.getPris().multiply(BigDecimal.valueOf(1.25)));
    
    return dto;
}
```

#### Med MapStruct:

```java
@Mapper(componentModel = "spring")
public interface ProduktMapper {
    
    @Mapping(target = "prisMedMoms", expression = "java(produkt.getPris().multiply(BigDecimal.valueOf(1.25)))")
    ProduktDTO toDto(Produkt produkt);
    
    Produkt toEntity(ProduktDTO dto);
}
```

## Bästa praxis för mappning

### 1. Var konsekvent med din mappningsstrategi

Välj en strategi och använd den konsekvent i hela projektet. En blandning av manuell mappning och olika bibliotek skapar förvirring.

### 2. Håll mappern ansvarig för endast mappning

Mappers bör bara konvertera mellan objekt, inte hämta data från databaser eller utföra affärslogik.

### 3. Hantera null-värden

Se alltid till att din mappning kan hantera null-värden korrekt.

```java
public KundDTO toDto(Kund kund) {
    if (kund == null) {
        return null;
    }
    
    // Resten av mappningskoden...
}
```

### 4. Använd hjälpmetoder för repetitiva uppgifter

```java
@Component
public class ProduktMapper {
    
    public ProduktDTO toDto(Produkt produkt) {
        if (produkt == null) {
            return null;
        }
        
        ProduktDTO dto = new ProduktDTO();
        mapBasicFields(produkt, dto);
        
        // Produkt-specifik mappning...
        
        return dto;
    }
    
    public TjänstDTO toDto(Tjänst tjänst) {
        if (tjänst == null) {
            return null;
        }
        
        TjänstDTO dto = new TjänstDTO();
        mapBasicFields(tjänst, dto);
        
        // Tjänst-specifik mappning...
        
        return dto;
    }
    
    // Gemensam metod för att mappa grundläggande fält
    private void mapBasicFields(BaseEntity entity, BaseDTO dto) {
        dto.setId(entity.getId());
        dto.setNamn(entity.getNamn());
        dto.setSkapadDatum(entity.getSkapadDatum());
    }
}
```

### 5. Tänk på prestanda

För stora datamängder, tänk på prestandan:
- MapStruct genererar kod vid kompilering och är därför mer prestandavänligt
- ModelMapper använder reflektion vilket kan vara långsammare
- Manuell mappning är ofta snabbast men mest arbetskrävande

### 6. Skriva tester för dina mappers

Oavsett vilken metod du väljer är det viktigt att skriva tester:

```java
@Test
public void testKundToDto() {
    // Arrange
    Kund kund = new Kund();
    kund.setId(1L);
    kund.setNamn("Test Person");
    kund.setEmail("test@example.com");
    
    // Act
    KundDTO dto = kundMapper.toDto(kund);
    
    // Assert
    assertNotNull(dto);
    assertEquals(kund.getId(), dto.getId());
    assertEquals(kund.getNamn(), dto.getNamn());
    assertEquals(kund.getEmail(), dto.getEmail());
}
```

## Jämförelse av mappningsstrategier

| Strategi | Fördelar | Nackdelar | När används? |
|----------|----------|-----------|--------------|
| Manuell mappning | Full kontroll, enkelt, inga beroenden | Repetitiv, underhållskrävande | Små projekt, enkla mappningar |
| Egna mapper-klasser | Strukturerad, testbar | Fortfarande manuell | Mellanstora projekt med kontrollbehov |
| MapStruct | Snabb, säker, kompileringstidskontroll | Kräver kodgenerering | Stora projekt, komplexa mappningar |
| Stream/Lambda | Deklarativ, läsbar, inbyggd filtrering | Manuell, kan bli komplex | När du behöver kombinera mappning med filtrering/transformering |

## Sammanfattning

Mappning mellan entiteter och DTOs är en viktig del av en väl designad Spring Boot-applikation. Det hjälper dig att:

1. Hålla presentationslagret separat från datamodellen
2. Undvika exponering av känslig data
3. Optimera dataöverföring
4. Förbättra applikationens struktur

Välj den mappningsstrategi som passar ditt projekt bäst baserat på storlek, komplexitet och krav. För mindre projekt kan manuell mappning vara tillräcklig, medan större projekt ofta drar nytta av verktyg som MapStruct.

Oavsett vilken metod du väljer, se till att vara konsekvent, hantera null-värden korrekt och testa din mappningskod noggrant.
