# Komma igång med Spring Boot: IntelliJ Ultimate, Spring Initializr och Maven

## IntelliJ IDEA Ultimate

IntelliJ IDEA Ultimate är en kraftfull integrerad utvecklingsmiljö (IDE) som är särskilt populär för Java- och Spring-utveckling.

### Vad är IntelliJ IDEA Ultimate?

IntelliJ IDEA Ultimate är en premiumprogramvara utvecklad av JetBrains. Den erbjuder:

- **Omfattande stöd för Java-utveckling** med intelligent kodkomplettering och analyser
- **Inbyggt stöd för Spring Boot** med förståelse för Spring-komponenter och konfiguration
- **Databasverktyg** för att hantera och köra SQL-frågor direkt i IDE:n
- **Integration med byggverktyg** som Maven och Gradle
- **Versionshantering** med Git, Subversion och andra versionshanteringssystem
- **Stöd för webb- och företagsutveckling** som Java EE, JavaScript, TypeScript och mer

### Skillnad mot IntelliJ IDEA Community Edition

Community Edition är en gratis version med begränsade funktioner. För Spring Boot-utveckling rekommenderas Ultimate eftersom:

- Den har inbyggt stöd för Spring-ramverket
- Den förstår Spring-konfiguration och kan ge specifika Spring-relaterade kodförbättringar
- Den kan köra och felsöka Spring Boot-applikationer med specialanpassade verktyg
- Den har inbyggd Spring Initializr integration

## Spring Initializr

Spring Initializr är ett verktyg för att snabbt skapa en grundläggande struktur för Spring Boot-projekt.

### Vad är Spring Initializr?

Spring Initializr kan användas på flera sätt:
1. Via webbgränssnittet på [start.spring.io](https://start.spring.io)
2. Direkt genom IntelliJ IDEA Ultimate
3. Genom Spring Boot CLI eller via andra IDE:er

Spring Initializr låter dig välja:
- Projekttyp (Maven eller Gradle)
- Programmeringsspråk (Java, Kotlin, Groovy)
- Spring Boot-version
- Projekt-metadata (grupp-ID, artefakt-ID)
- Beroenden (starter-paket för olika funktioner)

## Starta ett Spring Boot-projekt med Maven i IntelliJ Ultimate

### Steg 1: Skapa ett nytt projekt

1. Starta IntelliJ IDEA Ultimate
2. Klicka på **New Project** (på välkomstskärmen) eller **File → New → Project**
3. Välj **Spring Initializr** från listan till vänster
4. Kontrollera att rätt JDK är vald under "Project SDK"

### Steg 2: Konfigurera projektinformation

1. Fyll i projektets grundläggande information:
   - **Group**: Vanligtvis din organisations domännamn i omvänd ordning (t.ex. `com.mittforetag`)
   - **Artifact**: Projektets namn (t.ex. `min-applikation`)
   - **Name**: Fylls i automatiskt baserat på artefakt-ID
   - **Description**: Kort beskrivning av projektet
   - **Package name**: Genereras automatiskt från grupp och artefakt
   - **Packaging**: Välj `Jar` för fristående applikationer
   - **Java**: Välj Java-version (t.ex. 11, 17 eller högre)
   - Klicka på **Next**

### Steg 3: Välj beroenden

1. I listan över beroenden, välj de starter-paket du behöver. För en enkel webbapplikation:
   - **Spring Web** (under Web-kategorin)
   - **Spring Data JPA** (under SQL, om du behöver databasåtkomst)
   - **H2 Database** (för att snabbt komma igång med en inbäddad databas)
   - **Spring Boot DevTools** (för snabbare utveckling)
   - Klicka på **Finish**

### Steg 4: Utforska projektstrukturen

När projektet har skapats ser du en katalogstruktur som liknar detta:

```
min-applikation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── mittforetag/
│   │   │           └── minapplikation/
│   │   │               └── MinApplikationApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/
│               └── mittforetag/
│                   └── minapplikation/
│                       └── MinApplikationApplicationTests.java
└── pom.xml
```

### Steg 5: Kör applikationen

1. Öppna klassen med `@SpringBootApplication`-annoteringen (vanligtvis `*Application.java`)
2. Klicka på den gröna "Run"-ikonen bredvid `main`-metoden, eller högerklicka och välj **Run**
3. IntelliJ kompilerar och startar Spring Boot-applikationen
4. Se loggmeddelanden i konsolen och vänta tills applikationen har startat
5. Öppna en webbläsare och gå till `http://localhost:8080` för att testa applikationen

## Maven och dess roll i Spring Boot-projekt

### Vad är Maven?

Maven är ett kraftfullt byggverktyg och projekthanteringssystem som automatiserar många aspekter av utvecklingsarbetet:

- **Beroendehantering**: Laddar ner och hanterar alla externa bibliotek ditt projekt behöver
- **Byggautomatisering**: Kompilerar kod, kör tester och skapar distributionspaket
- **Standardiserad projektstruktur**: Följer principen "Convention over Configuration"
- **Livscykelhantering**: Definierar byggfaser (kompilering, test, paketering, installation)

### POM.xml - Projektets konfigurationsfil

Maven använder en fil kallad `pom.xml` (Project Object Model) för att definiera projektets konfiguration:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" 
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                             https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <!-- Förälder-POM som definierar Spring Boot-konfiguration -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <!-- Projektinformation -->
    <groupId>com.mittforetag</groupId>
    <artifactId>min-applikation</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>min-applikation</name>
    <description>Min första Spring Boot-applikation</description>
    
    <!-- Java-version -->
    <properties>
        <java.version>17</java.version>
    </properties>
    
    <!-- Beroenden (bibliotek som projektet använder) -->
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>
    
    <!-- Plugins för byggprocessen -->
    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Viktiga Maven-koncept för nybörjare

1. **Beroenden**: Externa bibliotek som ditt projekt använder, definieras i `<dependencies>`-sektionen
   ```xml
   <dependency>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-starter-web</artifactId>
   </dependency>
   ```

2. **Transitivitetsprincipen**: När du lägger till ett beroende hanterar Maven automatiskt alla dess beroenden

3. **Maven-repositories**: Platser där Maven hämtar beroenden från
   - **Central Repository**: Standard för offentliga bibliotek
   - **Local Repository**: Din lokala cache på datorn (vanligtvis `~/.m2/repository`)

4. **Bygga projektet med Maven**
   - Kompilera: `mvn compile`
   - Köra tester: `mvn test`
   - Paketera: `mvn package` (skapar en JAR- eller WAR-fil)
   - Installera lokalt: `mvn install`
   - Rensa: `mvn clean`

5. **Spring Boot Maven Plugin**
   ```xml
   <plugin>
       <groupId>org.springframework.boot</groupId>
       <artifactId>spring-boot-maven-plugin</artifactId>
   </plugin>
   ```
   Detta plugin:
   - Skapar körbara "fat JAR"-filer med alla beroenden
   - Hanterar Spring Boot-specifika byggaspekter
   - Möjliggör körning med `mvn spring-boot:run`

## Vanliga kommando med Maven i Spring Boot-projekt

1. **Starta applikationen från terminalen**:
   ```
   mvn spring-boot:run
   ```

2. **Bygga en körbar JAR-fil**:
   ```
   mvn clean package
   ```
   Den resulterade JAR-filen kan köras med:
   ```
   java -jar target/min-applikation-0.0.1-SNAPSHOT.jar
   ```

3. **Rensa tidigare byggen**:
   ```
   mvn clean
   ```

4. **Köra tester**:
   ```
   mvn test
   ```

## Fördelar med Maven i Spring Boot-projekt

1. **Konsekvent projektstruktur**: Standardiserad katalogstruktur som alla utvecklare känner igen
2. **Enkelt att dela projekt**: Alla beroenden definieras i POM-filen
3. **Integration med IDE:er**: Automatisk import av beroenden i IntelliJ och andra IDE:er
4. **Build-automatisering**: Enkel process för att bygga och paketera applikationer
5. **Versionshantering**: Tydlig kontroll över både projektversion och beroenden
6. **Byggprofiler**: Möjlighet att konfigurera olika bygginställningar för olika miljöer (utveckling, test, produktion)

## Tips för nybörjare

1. **Utforska Spring Boot Starter-beroenden** - De förenklar konfigurationen för vanliga funktioner
2. **Använd IntelliJ:s Maven-verktyg** - Öppna Maven-panelen för att enkelt köra byggoption
3. **Spring Boot DevTools** - Lägg till detta beroende för automatisk omstart vid kodändringar
4. **Undersök application.properties** - Här kan du konfigurera din Spring Boot-applikation
5. **Titta på loggmeddelanden** - Spring Boot ger värdefull information vid uppstart

## Sammanfattning

Med IntelliJ IDEA Ultimate, Spring Initializr och Maven kan du snabbt och enkelt komma igång med Spring Boot-utveckling. Denna kombination av verktyg ger en kraftfull utvecklingsmiljö med intelligent kodstöd, enkel projektgenerering och robust beroendehantering.

Maven spelar en kritisk roll genom att hantera dina beroenden, automatisera byggprocessen och följa konventioner som gör projektet lättare att förstå för andra utvecklare. För nybörjare är det viktigt att förstå grundläggande Maven-koncept, men tack vare IntelliJ:s integration kan du snabbt bli produktiv även innan du behärskar alla detaljer i Maven.
