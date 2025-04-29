# ResponseEntity i Spring: En Teoretisk Guide

## Vad är ResponseEntity?

`ResponseEntity` är en klass i Spring Framework som representerar hela HTTP-svaret: statuskod, headers och body. Det ger dig mer kontroll över HTTP-svaret än att bara returnera ett objekt direkt från en controller-metod.

Tänk på `ResponseEntity` som ett kuvert för ditt svar. Istället för att bara skicka ett vykort (ditt objekt) till mottagaren, kan du lägga vykortet i ett kuvert, skriva önskad returadressen (headers) och välja rätt typ av porto (statuskod).

## Varför använda ResponseEntity?

När du bara returnerar ett objekt från en controller-metod i Spring, gör ramverket sitt bästa för att skapa ett lämpligt HTTP-svar:
- Objektet konverteras till JSON/XML
- Statuskoden sätts till 200 OK
- Grundläggande headers läggs till

Men ofta behöver du mer kontroll:
1. **Anpassade statuskoder**: Returnera 201 Created, 204 No Content, 404 Not Found, etc.
2. **Anpassade headers**: Lägga till auktoriseringsheaders, caching-direktiv, etc.
3. **Villkorliga svar**: Returnera olika svarkroppar eller statuskoder baserat på data
4. **Tomma svar**: Returnera en statuskod utan någon svarkropp

## Grundläggande syntax

Här är den grundläggande syntaxen för att använda `ResponseEntity`:

```java
@GetMapping("/{id}")
public ResponseEntity<Product> getProduct(@PathVariable Long id) {
    Product product = productService.findById(id);
    
    if (product != null) {
        return ResponseEntity.ok(product);
    } else {
        return ResponseEntity.notFound().build();
    }
}
```

Observera att:
- `ResponseEntity<Product>` anger att svarkroppen kommer att innehålla ett `Product`-objekt
- `ResponseEntity.ok(product)` skapar ett svar med statuskod 200 OK och produkten som svarkropp
- `ResponseEntity.notFound().build()` skapar ett svar med statuskod 404 Not Found utan svarkropp

## Vanliga statuskoder och hur man använder dem

### 200 OK
Används för lyckade förfrågningar där svaret innehåller begärd data.

```java
return ResponseEntity.ok(product);
```

### 201 Created
Används när en ny resurs har skapats framgångsrikt.

```java
Product newProduct = productService.save(product);
return ResponseEntity.status(HttpStatus.CREATED).body(newProduct);

// Alternativt, med URI till den nya resursen
URI location = ServletUriComponentsBuilder
    .fromCurrentRequest()
    .path("/{id}")
    .buildAndExpand(newProduct.getId())
    .toUri();
return ResponseEntity.created(location).body(newProduct);
```

### 204 No Content
Används för lyckade förfrågningar som inte returnerar någon data (t.ex. efter DELETE).

```java
productService.delete(id);
return ResponseEntity.noContent().build();
```

### 400 Bad Request
Används när förfrågan innehåller ogiltiga data.

```java
return ResponseEntity.badRequest().body(errorMessage);
```

### 404 Not Found
Används när begärd resurs inte finns.

```java
return ResponseEntity.notFound().build();
```

### 500 Internal Server Error
Används för oväntade serverfel.

```java
return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
```

## Anpassade headers

Du kan lägga till anpassade headers i ditt svar:

```java
return ResponseEntity.ok()
    .header("Custom-Header", "value")
    .body(product);
```

## Bygg steg för steg

`ResponseEntity` använder ett builder-mönster, vilket låter dig kedjekoppla metoder:

```java
return ResponseEntity
    .status(HttpStatus.OK)
    .header("Custom-Header", "value")
    .contentType(MediaType.APPLICATION_JSON)
    .body(product);
```

## Användning med olika typer av svar

### Med listor

```java
@GetMapping
public ResponseEntity<List<Product>> getAllProducts() {
    List<Product> products = productService.findAll();
    return ResponseEntity.ok(products);
}
```

### Med generiska typer

```java
@GetMapping("/statistics")
public ResponseEntity<Map<String, Object>> getStatistics() {
    Map<String, Object> stats = new HashMap<>();
    stats.put("totalProducts", productService.count());
    stats.put("averagePrice", productService.getAveragePrice());
    return ResponseEntity.ok(stats);
}
```

### Med void (inget innehåll)

```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
}
```

## Villkorliga svar

En av styrkorna med `ResponseEntity` är att du enkelt kan returnera olika typer av svar baserat på logik:

```java
@GetMapping("/{id}")
public ResponseEntity<?> getProduct(@PathVariable Long id) {
    try {
        Product product = productService.findById(id);
        
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        
        if (product.isDiscontinued()) {
            Map<String, String> message = new HashMap<>();
            message.put("message", "Product is discontinued");
            message.put("alternativeId", product.getAlternativeProductId());
            return ResponseEntity.status(HttpStatus.GONE).body(message);
        }
        
        return ResponseEntity.ok(product);
        
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Error retrieving product: " + e.getMessage());
    }
}
```

## ResponseEntity vs. @ResponseStatus

Spring erbjuder också en enklare annotation `@ResponseStatus` för att ange statuskod:

```java
@GetMapping("/{id}")
@ResponseStatus(HttpStatus.OK)
public Product getProduct(@PathVariable Long id) {
    return productService.findById(id);
}
```

Men denna metod är mindre flexibel eftersom:
1. Du kan inte dynamiskt ändra statuskoden baserat på logik
2. Du kan inte anpassa headers
3. Du kan inte returnera olika typer av svar

## Integrering med felhantering

`ResponseEntity` passar perfekt med central felhantering:

```java
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("Resource not found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse error = new ErrorResponse("Server error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

## Sammanfattning

`ResponseEntity` är ett kraftfullt verktyg i Spring som ger dig full kontroll över HTTP-svaret. Det låter dig:

1. Ange exakta statuskoder för olika scenarier
2. Anpassa HTTP-headers
3. Returnera olika typer av svarkroppar baserat på logik
4. Skapa tomma svar när det behövs
5. Bygga mer professionella och standardkompatibla REST API:er

Genom att använda `ResponseEntity` konsekvent i dina controllers kan du skapa mer robusta och tydliga API:er som följer HTTP-standarder, vilket gör dem lättare att använda för klienter.
