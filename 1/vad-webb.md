# Vad är Webbutveckling (Front, Back, HTTP, REST)

## Grundläggande begrepp inom webbutveckling

Webbutveckling handlar om att skapa applikationer som körs via internet och som användare kan nå via en webbläsare. För att förstå hur webbutveckling fungerar, särskilt med ramverk som Spring Boot, behöver vi först förstå några grundläggande begrepp.

## Frontend vs Backend

Moderna webbapplikationer är vanligtvis uppdelade i två huvuddelar:

### Frontend

Frontend är den del av applikationen som användaren direkt interagerar med - allt som visas i webbläsaren.

**Kännetecken för frontend:**
- **Tekniker**: HTML, CSS, JavaScript
- **Ramverk**: React, Angular, Vue.js, Svelte
- **Funktion**: Användargränssnitt, användarupplevelse, presentation av data
- **Körs på**: Användarens enhet (i webbläsaren)

**Exempel på frontend-kod:**
```html
<!DOCTYPE html>
<html>
<head>
    <title>Min Webbapp</title>
    <link rel="stylesheet" href="styles.css">
</head>
<body>
    <h1>Välkommen till min webbplats</h1>
    <button id="loadData">Hämta data</button>
    <div id="result"></div>
    
    <script src="script.js"></script>
</body>
</html>
```

```javascript
// script.js
document.getElementById('loadData').addEventListener('click', async () => {
    const response = await fetch('/api/data');
    const data = await response.json();
    document.getElementById('result').innerHTML = 
        `<p>Hämtad data: ${data.message}</p>`;
});
```

### Backend

Backend är serverdelen av applikationen som hanterar affärslogik, databearbetning och databasåtkomst.

**Kännetecken för backend:**
- **Tekniker**: Java (Spring Boot), Python (Django, Flask), Node.js, PHP, C#, Go
- **Funktion**: Databasinteraktioner, affärslogik, autentisering, API:er
- **Körs på**: Servrar, ofta i molnet eller på dedikerade maskiner

**Exempel på backend-kod med Spring Boot:**
```java
@RestController
@RequestMapping("/api")
public class DataController {
    
    @GetMapping("/data")
    public Map<String, String> getData() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Hej från backend!");
        return response;
    }
}
```

## HTTP - HyperText Transfer Protocol

HTTP är det grundläggande kommunikationsprotokollet för webben och definierar hur meddelanden formateras och överförs.

### Viktiga egenskaper hos HTTP:

1. **Tillståndslöst**: Varje förfrågan är oberoende av tidigare förfrågningar. Detta betyder att servern inte automatiskt "kommer ihåg" tidigare interaktioner med samma klient.

2. **Förfrågan-Svar modell**: All kommunikation består av:
   - En klient som skickar en förfrågan till en server
   - Servern som bearbetar förfrågan och skickar ett svar tillbaka

3. **HTTP-metoder**:
   - **GET**: Hämta data (läsning)
   - **POST**: Skicka data för att skapa en ny resurs
   - **PUT**: Uppdatera en befintlig resurs (hela resursen)
   - **DELETE**: Ta bort en resurs
   - **PATCH**: Delvis uppdatering av en resurs
   - **HEAD**: Som GET men utan meddelandekropp (bara metadata)
   - **OPTIONS**: Undersök vilka metoder som stöds

4. **HTTP-statuskoder**: Numeriska koder som indikerar resultatet av förfrågan:
   - **2xx**: Framgång (200 OK, 201 Created, 204 No Content)
   - **3xx**: Omdirigering (301 Moved Permanently, 302 Found)
   - **4xx**: Klientfel (400 Bad Request, 401 Unauthorized, 404 Not Found)
   - **5xx**: Serverfel (500 Internal Server Error, 503 Service Unavailable)

### Exempel på HTTP-förfrågan och svar:

```
# HTTP-förfrågan
GET /api/users/123 HTTP/1.1
Host: example.com
Accept: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...

# HTTP-svar
HTTP/1.1 200 OK
Content-Type: application/json
Content-Length: 83

{
  "id": 123,
  "name": "Anna Andersson",
  "email": "anna@example.com"
}
```

### HTTP vs HTTPS

HTTPS är en säker version av HTTP där all kommunikation krypteras med TLS/SSL:
- Skyddar mot avlyssning
- Verifierar identiteten på webbplatsen
- Krävs för många moderna webbfunktioner

## RESTful API

REST (Representational State Transfer) är en arkitekturstil för att designa nätverksapplikationer, särskilt webbtjänster.

### Principer för RESTful design:

1. **Resursbaserad**: Allt är en resurs som identifieras av en unik URI.
   - `/users` (en samling av användare)
   - `/users/123` (en specifik användare)
   - `/users/123/orders` (beställningar för en specifik användare)

2. **Tillståndslös kommunikation**: Varje förfrågan innehåller all information som behövs för att förstå och behandla den.

3. **Använder HTTP-metoder semantiskt korrekt**: 
   - GET för att läsa (inte ändra tillstånd)
   - POST för att skapa
   - PUT för att uppdatera/ersätta
   - DELETE för att ta bort

4. **Standardiserade representationer**: Oftast JSON eller XML.

5. **HATEOAS** (Hypermedia As The Engine Of Application State): API:et tillhandahåller länkar till relaterade resurser i svaren, vilket gör API:et självdokumenterande.

### Exempel på RESTful resurser:

| HTTP-metod | URI | Funktion |
|------------|-----|----------|
| GET | /api/products | Hämta alla produkter |
| GET | /api/products/42 | Hämta en specifik produkt |
| POST | /api/products | Skapa en ny produkt |
| PUT | /api/products/42 | Uppdatera produkt 42 |
| DELETE | /api/products/42 | Ta bort produkt 42 |

### Exempel på implementering med Spring Boot:

```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    // Hämta alla produkter
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }
    
    // Hämta en specifik produkt
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Skapa en ny produkt
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product savedProduct = productService.save(product);
        return ResponseEntity
            .created(URI.create("/api/products/" + savedProduct.getId()))
            .body(savedProduct);
    }
    
    // Uppdatera en produkt
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id, 
            @RequestBody Product product) {
        return productService.update(id, product)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    // Ta bort en produkt
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productService.existsById(id)) {
            productService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
```

## Hur frontend och backend kommunicerar

När en modern webbapplikation körs sker följande:

1. **Användaren interagerar** med frontend-gränssnittet (klickar på en knapp, fyller i ett formulär)
2. **Frontend skickar en HTTP-förfrågan** till en backend-endpoint
3. **Backend tar emot förfrågan**, behandlar den och hämtar/lagrar data i databasen
4. **Backend skickar ett HTTP-svar** (oftast i JSON-format)
5. **Frontend tar emot data** och uppdaterar användargränssnittet

### Exempel på frontend-backend-kommunikation:

```javascript
// Frontend JavaScript med Fetch API
async function getProduct(id) {
    try {
        const response = await fetch(`/api/products/${id}`);
        
        // Hantera HTTP-fel
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const product = await response.json();
        
        // Uppdatera UI med produktinformation
        document.getElementById('product-name').textContent = product.name;
        document.getElementById('product-price').textContent = `${product.price} kr`;
        
    } catch (error) {
        console.error('Fel vid hämtning av produkt:', error);
        // Visa felmeddelande för användaren
    }
}
```

## Arkitekturella mönster för webbutveckling

### 1. Monolitisk arkitektur
- Frontend och backend i samma kodbas/applikation
- Enklare att utveckla och driftsätta för mindre applikationer
- Spring Boot kan generera HTML med template-motorer som Thymeleaf

### 2. Microservices-arkitektur
- Backend uppdelad i flera små, oberoende tjänster
- Varje mikroservice hanterar en specifik affärsfunktion
- Skalbar och flexibel men mer komplex att hantera
- Spring Boot är idealiskt för att bygga mikroservices

### 3. SPA (Single-Page Application)
- Frontend är en JavaScript-applikation som laddar en enda HTML-sida
- Backend exponerar bara API:er
- Ger en app-liknande upplevelse i webbläsaren
- Populärt med React, Angular eller Vue.js för frontend och Spring Boot för backend

## Sammanfattning

Webbutveckling består av två huvuddelar:
- **Frontend**: Det användaren ser och interagerar med i webbläsaren
- **Backend**: Serverdelen som hanterar affärslogik och datalagring

Kommunikationen mellan frontend och backend sker via HTTP-protokollet, ofta i form av RESTful API:er. Spring Boot är ett kraftfullt ramverk för att bygga backend-system som kan exponera RESTful API:er på ett enkelt och standardiserat sätt.

Genom att förstå dessa grundläggande begrepp kan utvecklare bygga moderna, skalbara och underhållbara webbapplikationer med Spring Boot och andra tekniker.
