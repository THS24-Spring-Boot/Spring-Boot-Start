# Databaser med Spring Boot: Vad behöver du?

## Introduktion

Denna guide fokuserar på vad du behöver för att ansluta en databas till ditt Spring Boot-projekt. Vi går igenom olika databasalternativ, nödvändiga beroenden och grundläggande konfigurationer.

## Databasalternativ för Spring Boot

Spring Boot stödjer många olika databaser, inklusive:

- **MySQL** - En populär öppen källkod-relationsdatabas
- **PostgreSQL** - En avancerad öppen källkod relationsdatabas
- **SQLite** - En lätt, filbaserad databas
- **H2** - En inbäddad in-memory databas (bra för testning)
- **Oracle Database** - En kommersiell enterprisedatabas
- **SQL Server** - Microsofts relationsdatabas

De tre vanligaste alternativen för mindre till medelstora projekt är MySQL, PostgreSQL och SQLite.

## Nödvändiga beroenden

För att ansluta till en databas med Spring Boot behöver du:

1. **Spring Data-beroende** (oftast Spring Data JPA)
2. **Databasdrivrutin** för din specifika databas

### Maven-beroenden (pom.xml)

#### För MySQL

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### För PostgreSQL

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- PostgreSQL Driver -->
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```

#### För SQLite

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- SQLite Driver -->
<dependency>
    <groupId>org.xerial</groupId>
    <artifactId>sqlite-jdbc</artifactId>
    <version>3.40.1.0</version>
</dependency>

<!-- SQLite Dialect -->
<dependency>
    <groupId>com.github.gwenn</groupId>
    <artifactId>sqlite-dialect</artifactId>
    <version>0.1.2</version>
</dependency>
```

#### För inbyggd databas (testning)

```xml
<!-- H2 Database -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>
```

## Konfiguration i application.properties

Den huvudsakliga konfigurationen av din databasanslutning görs i `application.properties` eller `application.yml`-filen.

### Grundläggande konfigurationsinställningar

Följande inställningar behöver oftast konfigureras:

1. **URL** - Databasanslutningssträngen
2. **Användarnamn** och **lösenord** - Autentiseringsuppgifter
3. **Drivrutin** - Databasdrivrutinens klassnamn
4. **Hibernate-dialekt** - Anger databasens SQL-dialekt
5. **Schema-hantering** - Hur databasschemat ska hanteras (skapa, uppdatera etc.)

### MySQL-konfiguration

```properties
# Databasanslutning
spring.datasource.url=jdbc:mysql://localhost:3306/databasename
spring.datasource.username=root
spring.datasource.password=password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate-inställningar
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# Visa SQL-frågor (utvecklingsläge)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### PostgreSQL-konfiguration

```properties
# Databasanslutning
spring.datasource.url=jdbc:postgresql://localhost:5432/databasename
spring.datasource.username=postgres
spring.datasource.password=password
spring.datasource.driver-class-name=org.postgresql.Driver

# Hibernate-inställningar
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

# Visa SQL-frågor (utvecklingsläge)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### SQLite-konfiguration

```properties
# Databasanslutning
spring.datasource.url=jdbc:sqlite:path/to/database.db
spring.datasource.driver-class-name=org.sqlite.JDBC

# Hibernate-inställningar
spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.sqlite.hibernate.dialect.SQLiteDialect

# Visa SQL-frågor (utvecklingsläge)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# SQLite-specifika inställningar
spring.jpa.properties.hibernate.globally_quoted_identifiers=true
```

### H2 In-Memory-databaskonfiguration (för testning)

```properties
# H2 konsol (komma åt H2-databasen via webbläsaren)
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Databasanslutning
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Hibernate-inställningar
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

## Förklaring av viktiga konfigurationsalternativ

### spring.datasource.url

Detta är JDBC-URL:en som anger hur Spring Boot ska ansluta till din databas:

- **MySQL**: `jdbc:mysql://host:port/dbname?param1=value1&param2=value2`
- **PostgreSQL**: `jdbc:postgresql://host:port/dbname`
- **SQLite**: `jdbc:sqlite:path/to/database.db`
- **H2 (in-memory)**: `jdbc:h2:mem:dbname`
- **H2 (fil)**: `jdbc:h2:file:./data/dbname`

### spring.jpa.hibernate.ddl-auto

Denna inställning kontrollerar vad Hibernate ska göra med databasschemat när applikationen startar:

- **none**: Gör ingenting med schemat
- **validate**: Validera schemat men gör inga ändringar
- **update**: Uppdatera schemat för att matcha entitetsklasserna
- **create**: Skapa tabeller först genom att radera befintliga
- **create-drop**: Skapa tabeller vid start och radera dem vid avslut

**Rekommendation**:
- Utveckling: `update` eller `create-drop`
- Produktion: `none` eller `validate`

### spring.jpa.properties.hibernate.dialect

Anger vilken SQL-dialekt Hibernate ska använda för att generera optimal SQL för din databas:

- **MySQL**: `org.hibernate.dialect.MySQLDialect`
- **PostgreSQL**: `org.hibernate.dialect.PostgreSQLDialect`
- **SQLite**: Kräver anpassad dialekt
- **H2**: `org.hibernate.dialect.H2Dialect`

### Andra användbara inställningar

```properties
# Loggnivå för SQL-frågor
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Anslutningspool (HikariCP)
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000

# Teckenkodning (för MySQL)
spring.datasource.url=jdbc:mysql://localhost:3306/databasename?useUnicode=true&characterEncoding=utf8
```

## Databasspecifika överväganden

### MySQL

- **URL-parametrar**: MySQL stöder många URL-parametrar som kan vara användbara:
  ```
  useSSL=false
  serverTimezone=UTC
  allowPublicKeyRetrieval=true
  createDatabaseIfNotExist=true
  ```

- **Versionsskillnader**: MySQL 5.x och MySQL 8.x har olika dialekter:
  - MySQL 5.x: `org.hibernate.dialect.MySQL5Dialect`
  - MySQL 8.x: `org.hibernate.dialect.MySQLDialect`

### PostgreSQL

- **Schema**: PostgreSQL har bra stöd för scheman, vilket gör det lättare att organisera tabeller:
  ```properties
  spring.jpa.properties.hibernate.default_schema=myschema
  ```

- **Tidszon**: PostgreSQL hanterar datum och tid noggrant:
  ```
  spring.datasource.url=jdbc:postgresql://localhost:5432/databasename?useTimezone=true&serverTimezone=UTC
  ```

### SQLite

- **Begränsningar**: SQLite har flera begränsningar jämfört med fullvärdiga databaser:
  - Begränsat stöd för ALTER TABLE
  - Sämre hantering av samtidiga skrivningar
  - Saknar stöd för vissa datatyper

- **Transaktioner**: SQLite låser hela databasen under transaktioner, vilket kan orsaka problem med samtidighet

## Anslutningspooler

Spring Boot använder HikariCP som standard anslutningspool. Du kan konfigurera den i `application.properties`:

```properties
# Grundläggande HikariCP-inställningar
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.idle-timeout=300000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.max-lifetime=1800000
```

## Vanlig felsökning

### Problem med drivrutin

Om du ser fel som:
```
java.lang.ClassNotFoundException: com.mysql.cj.jdbc.Driver
```

Se till att du har lagt till rätt drivrutin i dina beroenden.

### Problem med anslutning

Om du får:
```
Communications link failure
```
eller
```
Connection refused
```

Kontrollera:
1. Att databasservern är igång
2. Användarnamn/lösenord är korrekta
3. IP/port är korrekta
4. Att användaren har rätt behörigheter
5. Att ingen brandvägg blockerar anslutningen

### Problem med dialekt

Om du ser:
```
org.hibernate.HibernateException: Access to DialectResolutionInfo cannot be null
```

Se till att du har konfigurerat rätt dialekt i `application.properties`.

## Sammanfattning

För att använda en databas med Spring Boot behöver du:

1. **Lägga till rätt beroenden**:
   - Spring Data JPA
   - Databasdrivrutin

2. **Konfigurera anslutningen i application.properties**:
   - URL, användarnamn, lösenord
   - Databasdrivrutin
   - Hibernate-dialekt
   - Schema-hantering

3. **Välja rätt inställningar för din miljö**:
   - Utveckling: visa SQL, ddl-auto=update
   - Produktion: dölja SQL, ddl-auto=none/validate

Med denna grundläggande konfiguration kan du enkelt ansluta din Spring Boot-applikation till MySQL, PostgreSQL eller SQLite.
