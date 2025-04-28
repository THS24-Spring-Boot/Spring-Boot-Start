# Vad är Ramverk (och Spring Boot)

## Vad är ett ramverk?

Ett ramverk är en färdig uppsättning kodbibliotek, verktyg och designmönster som underlättar utveckling av applikationer. Ramverk erbjuder en standardiserad struktur och återanvändbara komponenter som gör att utvecklare kan fokusera på att lösa verksamhetsproblem istället för att implementera vanlig infrastrukturkod.

Ett bra ramverk ger flera fördelar:
- **Ökad produktivitet** genom färdiga lösningar på vanliga problem
- **Standardisering** som gör det lättare för nya utvecklare att förstå kodbasen
- **Bästa praxis** inbyggt i arkitekturen
- **Testad och pålitlig** kod för grundläggande funktioner

## Spring Framework

Spring Framework är ett av de mest populära ramverken för Java-utveckling. Det lanserades 2003 och erbjuder en omfattande infrastruktur för Java-applikationer med fokus på:

- **Dependency Injection (DI)**: Gör det möjligt att koppla loss komponenter från varandra vilket ökar testbarhet och flexibilitet.
- **Aspektdriven programmering (AOP)**: Tillåter separation av tvärfunktionella angelägenheter som loggning och säkerhet.
- **Robust dataåtkomst**: Förenklad databashantering och integration med olika datakällor.
- **Transaktionshantering**: Stöd för både programmatiska och deklarativa transaktioner.
- **MVC-ramverk**: För webbutveckling.

Spring erbjuder ett ekosystem av projekt som bygger på grundramverket:
- Spring MVC (för webbutveckling)
- Spring Data (för dataåtkomst)
- Spring Security (för säkerhet)
- Spring Cloud (för molnbaserade tjänster)

Spring har dock en utmaning - konfiguration. I tidigare versioner krävdes omfattande XML-konfiguration för att sätta upp en applikation, vilket kunde vara tidskrävande och komplicerat.

## Spring Boot

Spring Boot lanserades 2014 för att förenkla och accelerera utvecklingen med Spring Framework. Spring Boot är ett "opinionated" (förutbestämt) ramverk byggt ovanpå Spring Framework.

### Skillnaden mellan Spring och Spring Boot

| Spring Framework | Spring Boot |
|------------------|-------------|
| Kräver manuell konfiguration (XML eller Java) | Använder "convention over configuration" |
| Kräver separat konfiguration av servern | Inbäddad server (Tomcat, Jetty, etc.) |
| Manuell hantering av beroenden | Starter-beroenden som automatiskt konfigurerar komponenter |
| Kräver explicit konfiguration för de flesta funktioner | Auto-konfiguration av vanliga behov |

### Spring Boot: Nyckelfunktioner

1. **Auto-konfiguration**
   - Konfigurerar applikationen automatiskt baserat på beroenden i classpath
   - Anpassningsbara standardvärden för de flesta scenarion
   - Intelligenta val som kan åsidosättas vid behov

2. **Starter-beroenden**
   - Förpaketerade uppsättningar av beroenden för specifika funktioner:
     - `spring-boot-starter-web`: För webbutveckling
     - `spring-boot-starter-data-jpa`: För databasåtkomst med JPA
     - `spring-boot-starter-security`: För säkerhetsfunktioner
     - `spring-boot-starter-test`: För testning

3. **Spring Boot Actuator**
   - Inbyggda verktyg för övervakning och hantering
   - Endpoints för hälsokontroll, mätvärden, loggningskonfiguration

4. **Inbäddad server**
   - Ingen separat installation eller konfiguration av webbserver
   - JAR-filer istället för WAR-filer (men WAR stöds också)
   - Enkelt att köra med `java -jar minapp.jar`

5. **Spring Boot CLI**
   - Kommandoradsverktyg för snabb prototyputveckling
   - Groovy-stöd för koncis kod

6. **Spring Initializr**
   - Webbaserat verktyg för att skapa projektstruktur
   - Finns på [start.spring.io](https://start.spring.io)

### Vanliga användningsområden för Spring Boot

- **Mikroservices**: Självständiga, lättviktiga tjänster i en distribuerad arkitektur.
- **RESTful API:er**: Snabbt bygga och exponera API:er för frontend eller andra system.
- **Webbapplikationer**: Fullständiga webbapplikationer med MVC-mönster.
- **Batchbearbetning**: För schemalagda eller tunga databearbetningsuppgifter.
- **Reaktiva applikationer**: För realtidsapplikationer med höga prestandakrav.

### Hur Spring Boot förenklar utvecklingen

Spring Boot eliminerar mycket av det repetitiva arbete som traditionellt associerats med Java-utveckling:

- **Minimal konfiguration**: De flesta applikationer kan köras med mycket lite eller ingen konfiguration
- **Embedded server**: Förenklar distribution och eliminerar behov av extern server
- **Enkelt att bygga och köra**: En körbar JAR-fil med allt inkluderat
- **Snabb uppstart**: Kommer igång med nya projekt på minuter istället för timmar

### Exempel på en minimal Spring Boot-applikation

```java
@SpringBootApplication
public class MinApplikation {
    public static void main(String[] args) {
        SpringApplication.run(MinApplikation.class, args);
    }
}

@RestController
class HejController {
    @GetMapping("/hej")
    public String säg() {
        return "Hej från Spring Boot!";
    }
}
```

Denna minimala kod skapar en körbar webbapplikation med en endpoint `/hej` som svarar med en hälsning. Spring Boot tar hand om alla nödvändiga konfigurationer bakom kulisserna.

### Spring Boot vs Andra Ramverk

| Funktion | Spring Boot | JavaEE/Jakarta EE | Micronaut | Quarkus |
|----------|-------------|-------------------|-----------|---------|
| Uppstartstid | Snabb | Långsam | Mycket snabb | Mycket snabb |
| Minnesfotavtryck | Moderat | Stort | Litet | Litet |
| Ekosystem | Mycket stort | Stort | Växande | Växande |
| Mognad | Mycket mogen | Mycket mogen | Nyare | Nyare |
| Molnstöd | Utmärkt | Begränsat | Bra | Utmärkt |

## Sammanfattning

Spring Boot är en kraftfull förenkling av Spring Framework som hjälper utvecklare att snabbt komma igång med Spring-baserade applikationer. Genom att eliminera behovet av omfattande konfiguration och erbjuda förinställda standarder, gör Spring Boot det möjligt för både nybörjare och erfarna utvecklare att bygga robusta Java-applikationer med minimal uppstartskostnad.

Spring Boot fortsätter att vara ett av de mest populära valen för Java-utveckling, särskilt för företagsapplikationer, mikroservices och webbtjänster, tack vare dess flexibilitet, omfattande funktionalitet och starka community-stöd.
