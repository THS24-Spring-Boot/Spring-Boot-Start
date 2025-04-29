# Dependency Injection & Inversion of Control
## En nybörjarguide för Spring Boot-utvecklare

### Vad är Dependency Injection (DI)?

Dependency Injection är ett designmönster där ett objekt (eller en klass) får sina beroenden från en extern källa istället för att skapa dem själv. Enkelt uttryckt: istället för att en klass skapar de objekt den behöver, får den dem levererade utifrån.

**Exempel utan DI:**
```java
public class OrderService {
    private DatabaseRepository repository = new DatabaseRepository();
    
    public void processOrder(Order order) {
        repository.save(order);
    }
}
```

**Exempel med DI:**
```java
public class OrderService {
    private final Repository repository;
    
    // Dependency injiceras genom konstruktorn
    public OrderService(Repository repository) {
        this.repository = repository;
    }
    
    public void processOrder(Order order) {
        repository.save(order);
    }
}
```

I det andra exemplet är `OrderService` inte längre ansvarig för att skapa sitt eget `Repository`-objekt. Istället "injiceras" det utifrån.

### Vad är Inversion of Control (IoC)?

Inversion of Control är principen bakom Dependency Injection. Det innebär att kontrollen över hur objekt skapas och hur beroenden hanteras flyttas från din kod till ett ramverk (i vårt fall Spring Boot).

Namnet kommer från att man "vänder på" kontrollflödet:
- **Traditionell kontroll**: Din kod skapar och hanterar alla objekt själv
- **Inverterad kontroll**: Ramverket (Spring) skapar och hanterar objekten åt dig

### Varför behövs DI och IoC?

1. **Löskoppling (Loose Coupling)**: Klasser blir mindre beroende av varandra, vilket gör koden mer flexibel och enklare att underhålla.

2. **Testbarhet**: När beroenden kan injiceras utifrån blir det mycket enklare att ersätta riktiga implementationer med mockobjekt för testning.

3. **Återanvändbarhet**: Komponenter med injicerade beroenden är lättare att återanvända i olika delar av applikationen.

4. **Tydligare kodstruktur**: Det blir tydligare vilka beroenden en klass har när de deklareras explicit.

### Spring IoC Container

I Spring är IoC Container den centrala komponenten som hanterar objektens livscykel och beroenden. Den ansvarar för att:

1. Skapa objekt (kallas "beans" i Spring)
2. Konfigurera objekten
3. Hantera deras livscykel
4. Injicera beroenden mellan dem

Spring har två typer av containrar:
- **BeanFactory** (grundläggande)
- **ApplicationContext** (utökad med fler funktioner, används oftast)

### Olika typer av Dependency Injection

Spring stödjer tre huvudtyper av dependency injection:

1. **Konstruktor-injektion** (rekommenderas):
```java
public class OrderService {
    private final Repository repository;
    
    public OrderService(Repository repository) {
        this.repository = repository;
    }
}
```

2. **Setter-injektion**:
```java
public class OrderService {
    private Repository repository;
    
    @Autowired
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}
```

3. **Fält-injektion** (enklast men inte rekommenderad för produktionskod):
```java
public class OrderService {
    @Autowired
    private Repository repository;
}
```

### Fördelar med DI och IoC i Spring Boot

1. **Minskad boilerplate-kod**: Spring sköter mycket av den "plumbing code" som annars skulle behövas.

2. **Fokus på affärslogik**: Du kan fokusera mer på applikationens faktiska funktionalitet istället för tekniska detaljer.

3. **Förkonfigurerade beroenden**: Spring Boot kommer med många förkonfigurerade beroenden som fungerar direkt.

4. **Enklare byten av implementationer**: Genom att jobba mot gränssnitt (interfaces) kan implementationer lätt bytas ut utan att ändra koden som använder dem.

5. **Enklare att hantera tvärfunktionella aspekter**: Funktioner som loggning, transaktionshantering och säkerhet kan läggas till utan att ändra affärslogiken.

### Sammanfattning

- **Dependency Injection**: Låter klasser få sina beroenden utifrån istället för att skapa dem själva.
- **Inversion of Control**: Principen att ramverket (Spring) styr objektens livscykel istället för din kod.
- **Tillsammans** gör de din kod mer testbar, flexibel och underhållbar.

I Spring Boot är dessa principer centrala. När du förstår dessa grundläggande koncept blir det mycket lättare att bygga välstrukturerade applikationer. Du behöver inte förstå alla detaljer än, men dessa koncept hjälper dig att förstå varför Spring Boot fungerar som det gör och hur du kan utnyttja det på bästa sätt.
