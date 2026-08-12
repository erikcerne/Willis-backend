# Willis Backend API

Backend-API för en full-stack applikation som automatiserar hantering av matvaror och hjälper hushåll att minska matsvinn. API:et fungerar som lagret mellan en frontend, Auth0-inloggning och en PostgreSQL-databas i Supabase.

Kärnidén är att applikationen kan ta emot extern JSON-data som simulerar ett köp från en matbutiks kassa. Datat innehåller produkt-id, kvantitet, produktionsdatum och utgångsdatum. Backend lagrar eller uppdaterar sedan automatiskt användarens digitala skafferi utan att användaren manuellt behöver registrera varje vara.

För matbutiker och större livsmedelsaktörer skapar detta en tydlig integrationspunkt: kassasystem, e-handel eller framtida 2D/QR-koder kan skicka strukturerad produktdata direkt till användarens konto. Automatiseringen minskar friktion, gör bäst-före-data användbar i realtid och skapar bättre förutsättningar för smarta påminnelser, planering och minskat matsvinn.

## Innehåll

- [Tech Stack](#tech-stack)
- [Arkitektur](#arkitektur)
- [Databas och domänmodell](#databas-och-domänmodell)
- [API Endpoints](#api-endpoints)
- [Säkerhet och Auth0](#säkerhet-och-auth0)
- [Lokal konfiguration](#lokal-konfiguration)
- [Köra lokalt](#köra-lokalt)
- [Deployment på Railway](#deployment-på-railway)
- [Projektstruktur](#projektstruktur)

## Tech Stack

| Område | Teknik |
| --- | --- |
| Språk | Java 17 |
| Ramverk | Spring Boot 3.5.13 |
| API | Spring Web, REST |
| Databasaccess | Spring Data JPA, Hibernate |
| Databas | PostgreSQL via Supabase |
| Säkerhet | Spring Security, OAuth2 Resource Server, Auth0 JWT |
| Validering | Spring Boot Starter Validation |
| API-dokumentation | Springdoc OpenAPI / Swagger UI |
| Build tool | Maven |
| Deployment | Railway |
| Utvecklingsstöd | Lombok, Spring Boot DevTools |

## Arkitektur

Projektet är uppbyggt som en Spring Boot-backend med tydliga lager:

- `Controller` exponerar REST-endpoints under `/api`.
- `InventoryService` innehåller affärslogik för skafferi, utgångna varor och shoppinglista.
- Repository-klasserna kapslar in Spring Data JPA-repositories.
- Entiteterna `User`, `Product`, `Inventory` och `ShoppingList` mappar till databastabeller.
- `SecurityConfig` skyddar API:et med Auth0 JWT-validering och CORS-regler.

Det centrala flödet är:

1. Användaren loggar in via Auth0 och skickar JWT-token i `Authorization`-headern.
2. Frontend registrerar användaren i backend via `/api/users/register`.
3. Extern JSON-data från ett simulerat kassaflöde skickas till `/api/inventory`.
4. Backend hittar produkt och användare, grupperar befintliga lagerrader och uppdaterar kvantitet om samma produkt redan finns med samma produktions- och utgångsdatum.
5. Frontend kan hämta aktivt skafferi, minska kvantitet, ta bort varor, rensa utgångna varor och skapa shoppinglisteposter.

## Databas och domänmodell

Databasen körs mot PostgreSQL i Supabase. Hibernate är konfigurerat med `spring.jpa.hibernate.ddl-auto=update`, vilket gör att tabeller kan uppdateras automatiskt under utveckling.

### `users`

Representerar en applikationsanvändare. Primärnyckeln är Auth0-användarens `sub`-claim.

| Kolumn | Typ i Java | Beskrivning |
| --- | --- | --- |
| `user_id` | `String` | Auth0 user id, exempelvis `auth0|...` |
| `email` | `String` | Användarens e-postadress |

Relationer:

- En användare kan ha många `inventory`-rader.
- En användare kan ha många `shoppingList`-rader.

### `product`

Produktkatalogen. Ingest-API:et förutsätter att `productId` redan finns i denna tabell.

| Kolumn | Typ i Java | Beskrivning |
| --- | --- | --- |
| `product_id` | `UUID` | Produktens id |
| `name` | `String` | Produktnamn |
| `category` | `String` | Produktkategori |
| `pic` | `URL` | Bild-URL för produkten |

### `inventory`

Användarspecifikt lager/skafferi. Varje rad kopplar en produkt till en användare och har egen kvantitet samt datumdata.

| Kolumn | Typ i Java | Beskrivning |
| --- | --- | --- |
| `inventory_id` | `UUID` | Unikt id för lagerraden |
| `product_id` | `Product` | Many-to-one till `product` |
| `user_id` | `User` | Many-to-one till `users` |
| `quantity` | `int` | Antal enheter |
| `expiryDate` | `LocalDateTime` | Utgångsdatum / bäst före |
| `produceDate` | `LocalDateTime` | Produktionsdatum |

Affärslogik:

- Om en inköpsrad matchar befintlig rad på samma användare, produkt, produktionsdatum och utgångsdatum adderas kvantiteten.
- Vid hämtning grupperas flera lagerrader per produkt i svaret.
- Backend beräknar `expiryProgress` mellan produktionsdatum och utgångsdatum.
- Backend beräknar `daysLeft` till utgångsdatum.
- Utgångna varor kan rensas i bulk per användare.

### `shoppingList`

Användarspecifik shoppinglista baserad på produkter från skafferiet.

| Kolumn | Typ i Java | Beskrivning |
| --- | --- | --- |
| `shoppingList_id` | `UUID` | Unikt id för shoppinglisteposten |
| `product_id` | `Product` | Many-to-one till `product` |
| `user_id` | `User` | Many-to-one till `users` |

Repository-logiken förhindrar dubbletter för samma produkt och användare i shoppinglistan.

## API Endpoints

Alla skyddade endpoints kräver:

```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Översikt

| Method | Endpoint | Request body / parametrar | Beskrivning |
| --- | --- | --- | --- |
| `POST` | `/api/users/register` | Ingen body. JWT används för `sub` och e-post. | Registrerar användaren om den inte redan finns. |
| `POST` | `/api/inventory` | JSON-array med inköpta varor. Se exempel nedan. | Tar emot kassadata/JSON ingest och skapar eller uppdaterar skafferi. |
| `GET` | `/api/inventory` | Ingen body. JWT identifierar användaren. | Hämtar användarens skafferi grupperat per produkt. |
| `PUT` | `/api/inventory/{id}` | Ett heltal, exempelvis `2`. | Uppdaterar kvantitet för en specifik inventory-rad. |
| `DELETE` | `/api/inventory/{id}` | Ingen body. | Tar bort en specifik inventory-rad. |
| `DELETE` | `/api/inventory/expired` | Ingen body. JWT identifierar användaren. | Rensar alla utgångna varor för användaren. |
| `POST` | `/api/shopping?inventoryId={uuid}` | Query parameter `inventoryId`. | Lägger produkt från en inventory-rad i shoppinglistan. |
| `GET` | `/api/shopping` | Ingen body. JWT identifierar användaren. | Hämtar användarens shoppinglista. |
| `DELETE` | `/api/shopping?id={uuid}` | Query parameter `id`. | Tar bort en post från shoppinglistan. |

> Obs: Koden innehåller bulk-rensning för utgångna varor via `/api/inventory/expired`. Ett separat endpoint för att rensa hela skafferiet finns inte i nuvarande backendkod.

### `POST /api/users/register`

Registrerar användaren efter Auth0-inloggning. Backend läser:

- `jwt.getSubject()` som `userId`
- custom claim `https://willis-api/email`, annars standardclaim `email`

Exempel:

```bash
curl -X POST http://localhost:8080/api/users/register \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

### `POST /api/inventory`

Tar emot extern JSON-data som simulerar ett köp från butikens kassa.

Request body:

```json
[
  {
    "productId": "9a2d91c1-52e2-46fb-b7e2-8f1fd9f67a10",
    "userId": "auth0|abc123",
    "quantity": 2,
    "produceDate": "2026-08-01T00:00:00",
    "expiryDate": "2026-08-18T00:00:00"
  },
  {
    "productId": "0db41ca6-b01f-43a2-b908-3fb84214dd11",
    "userId": "auth0|abc123",
    "quantity": 1,
    "produceDate": "2026-08-05T00:00:00",
    "expiryDate": "2026-08-20T00:00:00"
  }
]
```

Svar:

```http
200 OK
```

Logik:

- `productId` måste finnas i produkttabellen.
- `userId` måste finnas i `users`.
- Om samma produkt redan finns för användaren med samma `produceDate` och `expiryDate`, ökas befintlig kvantitet.
- Annars skapas en ny inventory-rad.

### `GET /api/inventory`

Hämtar användarens skafferi. Svaret grupperas per produkt och innehåller en lista med separata lagerposter för olika datum.

Exempel på svar:

```json
[
  {
    "name": "Mjölk",
    "category": "Mejeri",
    "pic": "https://example.com/milk.png",
    "inventoryItems": [
      {
        "expiryDate": "2026-08-18T00:00:00",
        "expiryProgress": 0.65,
        "quantity": 2,
        "daysLeft": 6,
        "inventoryId": "6dcecfad-1b91-4584-aafe-88ac92759a41"
      }
    ]
  }
]
```

### `PUT /api/inventory/{id}`

Uppdaterar kvantiteten för en specifik inventory-rad.

Request body:

```json
2
```

Svar:

```http
202 Accepted
```

Om den begärda kvantiteten är större än befintlig kvantitet kastar service-lagret ett runtime-fel. Tanken är att endpointen används för att reducera eller sätta kvarvarande antal när en vara konsumeras.

### `DELETE /api/inventory/{id}`

Tar bort en specifik rad från användarens skafferi.

Exempel:

```bash
curl -X DELETE http://localhost:8080/api/inventory/6dcecfad-1b91-4584-aafe-88ac92759a41 \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

Svar:

```http
204 No Content
```

### `DELETE /api/inventory/expired`

Rensar alla utgångna varor för den inloggade användaren. Backend hämtar användarens inventory-rader, beräknar `daysLeft` och tar bort rader där `daysLeft == 0`.

Svar:

```http
204 No Content
```

### Shoppinglista

Lägg till produkt från skafferi:

```bash
curl -X POST "http://localhost:8080/api/shopping?inventoryId=6dcecfad-1b91-4584-aafe-88ac92759a41" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

Hämta shoppinglista:

```json
[
  {
    "name": "Mjölk",
    "category": "Mejeri",
    "id": "b1ad66dc-22fd-4040-8a57-73b0f8d98fb5"
  }
]
```

Ta bort shoppinglistepost:

```bash
curl -X DELETE "http://localhost:8080/api/shopping?id=b1ad66dc-22fd-4040-8a57-73b0f8d98fb5" \
  -H "Authorization: Bearer <JWT_TOKEN>"
```

## Säkerhet och Auth0

Backend är konfigurerad som en OAuth2 Resource Server med JWT-validering.

Skyddade endpoints:

- `/api/users/register`
- `/api/inventory`
- `/api/inventory/**`
- `/api/shopping`

Konfigurationen finns i `SecurityConfig` och `application.properties`.

Aktuell Auth0-konfiguration:

```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://dev-jnlbp5hqfmbs1xp1.us.auth0.com/
spring.security.oauth2.resourceserver.jwt.audiences=https://willis-api/
```

`SecurityConfig` använder även Auth0 JWKS-endpointen:

```text
https://dev-jnlbp5hqfmbs1xp1.us.auth0.com/.well-known/jwks.json
```

När en request kommer in validerar Spring Security JWT-signaturen mot Auth0. I controllers används `@AuthenticationPrincipal Jwt jwt` för att läsa användarens `sub` och koppla datat till rätt användare.

CORS tillåter i nuläget:

- `http://localhost:5173`
- `https://willis-frontend-production.up.railway.app`

Tillåtna metoder:

- `GET`
- `POST`
- `PUT`
- `DELETE`
- `OPTIONS`

## Lokal konfiguration

Applikationen läser databasuppgifter från miljövariabler:

```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
```

Skapa miljövariabler lokalt:

```powershell
$env:DB_URL="jdbc:postgresql://<host>:<port>/<database>"
$env:DB_USER="<database-user>"
$env:DB_PASS="<database-password>"
```

Exempel för macOS/Linux:

```bash
export DB_URL="jdbc:postgresql://<host>:<port>/<database>"
export DB_USER="<database-user>"
export DB_PASS="<database-password>"
```

Rekommenderade Auth0-variabler för Railway eller framtida extern konfiguration:

```text
AUTH0_ISSUER_URI=https://<tenant>.<region>.auth0.com/
AUTH0_AUDIENCE=https://willis-api/
```

I nuvarande kod är issuer/audience och JWKS-URL satta direkt i konfigurationen.

## Köra lokalt

Krav:

- Java 17
- Maven eller projektets Maven Wrapper
- Tillgång till Supabase/PostgreSQL
- Auth0-token med rätt audience

Starta applikationen:

```powershell
.\mvnw.cmd spring-boot:run
```

Alternativt med installerad Maven:

```bash
mvn spring-boot:run
```

Backend startar normalt på:

```text
http://localhost:8080
```

Swagger UI finns via Springdoc när applikationen är igång:

```text
http://localhost:8080/swagger-ui/index.html
```

Kör tester:

```powershell
.\mvnw.cmd test
```

## Deployment på Railway

Backend är avsedd att köras på Railway med Supabase som extern PostgreSQL-databas.

Typisk Railway-konfiguration:

| Variabel | Beskrivning |
| --- | --- |
| `DB_URL` | JDBC-URL till Supabase PostgreSQL |
| `DB_USER` | Databasanvändare |
| `DB_PASS` | Databaslösenord |
| `PORT` | Sätts av Railway. Spring Boot kan använda den om serverport konfigureras. |

För produktion bör hemligheter lagras som Railway Variables, inte i versionshanterade filer.

Rekommenderad production hardening:

- Sätt `spring.jpa.hibernate.ddl-auto=validate` eller hantera schema via migrationsverktyg som Flyway.
- Flytta hårdkodad JWKS-URL, issuer och audience till miljövariabler.
- Lägg till tydlig global felhantering i `GlobalExceptionHandler`.
- Säkerställ att endpoints som uppdaterar eller tar bort inventory verifierar ägarskap mot JWT-användaren.

## Framtidssäkring: 2D/QR-koder

Systemets ingest-design är byggd för att kunna ta emot mer strukturerad produktdata än en traditionell 1D-streckkod kan bära. När livsmedelsbranschen rör sig mot 2D/QR-koder med inbäddad information, exempelvis batchnummer och bäst-före-datum, kan samma API-flöde användas för att skicka:

- produkt-id
- kvantitet
- produktionsdatum
- utgångsdatum
- eventuell framtida batch- eller ursprungsdata

Det gör backend relevant för framtida butikssystem där varans livscykel kan följa med från kassa till konsumentens digitala skafferi.

## Projektstruktur

```text
src/main/java/com/example/Backend
|-- BackendApplication.java
|-- Controller.java
|-- GlobalExceptionHandler.java
|-- InventoryService.java
|-- SecurityConfig.java
|-- dtos/
|   |-- InventoryDto.java
|   |-- InventoryListDto.java
|   |-- ReceiveInventoryDto.java
|   |-- ShoppingListDto.java
|-- inventory/
|   |-- Inventory.java
|   |-- InventoryRepository.java
|   |-- JpaInventoryRepository.java
|-- products/
|   |-- Product.java
|   |-- ProductRepository.java
|   |-- JpaProductRepository.java
|-- shoppingList/
|   |-- ShoppingList.java
|   |-- ShoppingListRepository.java
|   |-- JpaShoppingListRepository.java
|-- user/
|   |-- User.java
|   |-- UserService.java
|   |-- UserRepository.java
|   |-- JpaUserRepository.java
```

## Sammanfattning

Willis Backend API är ett Spring Boot-baserat REST API för automatiserad matvaruhantering. Genom att kombinera Auth0-säkrad användaridentitet, Supabase/PostgreSQL och JSON ingest från externa köpflöden kan applikationen automatiskt bygga upp ett digitalt skafferi, hålla koll på bäst-före-datum och hjälpa användare att minska matsvinn.
