# 🥫 Willis-pantry

> **Track. Organize. Reduce Waste.** The most expensive food is the food you throw away. Willis-pantry is a full-stack pantry management application designed to automate grocery tracking, monitor expiration dates, and reduce household food waste without manual scanning.

**🌍 Live Demo:** [https://willis-frontend-production.up.railway.app/](https://willis-frontend-production.up.railway.app/)

🔗 **Frontend Repo:** [https://github.com/erikcerne/willis-frontend](https://github.com/erikcerne/willis-frontend)

---

## 📖 About the Project

Willis helps households keep track of groceries, monitor real-time shelf life, and eliminate food waste. Instead of requiring manual item registration, Willis is built around an automated ingest model that accepts external POS/receipt payloads containing product IDs, quantities, production dates, and expiration dates.

The backend automatically ingests, groups, and maps this data directly into the user's digital pantry. The platform bridges a responsive React interface, a secured Spring Boot REST API, Auth0 authentication, and a PostgreSQL database on Supabase.

---

<img width="1024" height="558" alt="7b0aa8a1-3290-4092-a52b-12b8d99687ca" src="https://github.com/user-attachments/assets/96afd39b-fdc7-48f4-bfac-8c9c0a29889d" />

## 🛠️ Tech Stack

This project is organized into two separate application layers: the Client and the API.

### **Frontend (`/willis-frontend`)**

- **Framework:** React 19 (via Vite)
- **Language:** TypeScript / TSX
- **Styling:** Tailwind CSS + DaisyUI
- **Routing:** TanStack Router (`@tanstack/react-router`)
- **Data Fetching & State:** TanStack Query (`@tanstack/react-query`)
- **Authentication:** Auth0 React SDK (`@auth0/auth0-react`)

### **Backend (`/Willis-backend`)**

- **Language & Framework:** Java 17, Spring Boot 3.5.13
- **API & Data Access:** Spring Web (REST), Spring Data JPA, Hibernate
- **Security:** Spring Security (OAuth2 Resource Server with Auth0 JWT validation)
- **Validation & Tooling:** Spring Boot Starter Validation, Lombok, Spring Boot DevTools
- **API Documentation:** Springdoc OpenAPI / Swagger UI
- **Build Tool:** Maven (Maven Wrapper)

### **Database & Infrastructure**

- **Database:** PostgreSQL (hosted on Supabase)
- **Authentication:** Auth0 (JWT Bearer Token verification)
- **Hosting & Deployment:** Railway (Docker-compatible deployment)

---

## 🏗️ Architecture & Data Flow

The application enforces a secure, decoupled full-stack architecture:

```text
[ React Client ]  ──( Auth0 JWT )──>  [ Spring Boot REST API ]  ──>  [ PostgreSQL / Supabase ]
       │                                       ▲
       └────── Simulated POS Receipt Ingest ───┘
```

1. **Authentication:** The user logs in via Auth0. The client registers the user profile via `POST /api/users/register` and attaches the signed JWT to all subsequent requests.
2. **Automated Ingest:** External JSON payloads simulating POS receipt scans hit `POST /api/inventory`. The backend verifies the user and product catalogs, matches existing batch dates, and either increments existing stock or creates new inventory records.
3. **Pantry Management:** The client fetches grouped inventories, tracks shelf life calculations in real time, decrements quantities as items are consumed, and moves items to the shopping list.

---

## ✨ Features (Currently Live)

### 🏠 Smart Digital Pantry

- **Live Shelf Tracking:** Displays all active, non-expired goods categorized with product images, units, and clear remaining-day counters.
- **Batch Grouping:** Identical products purchased across different dates are grouped into clean, expandable cards while maintaining individual batch timestamps.
- **Expiration Progress Bars:** Color-coded status indicators (green, yellow, red) visually communicate shelf life progress based on production and expiration dates.

### ⚠️ Expired Goods Management

- **Dedicated Expiration View:** Filter quickly between `All`, `Fresh`, and `Expired` groceries.
- **Bulk Clearance:** One-click bulk action to clear all expired records across the user's pantry.

### 🛒 Integrated Shopping List

- **Pantry-to-List Sync:** Add consumed or low-stock items directly to the user's shopping list.
- **Duplicate Prevention:** Backend repository logic guarantees duplicate entries for the same item are avoided.

### 🧾 Automated Receipt Simulator

- **Instant Batch Sync:** Ingest multiple purchased items in a single request, simulating modern checkout and POS integrations.

---

## 🗄️ Database Schema (High Level)

The database runs on PostgreSQL via Supabase:

- `users`: Stores user identity mapped from Auth0 (`user_id` mapped to the JWT `sub` claim) and email.
- `product`: Global catalog storing product details (`product_id`, `name`, `category`, and `pic` URL).
- `inventory`: User-specific inventory linking users and products, tracking `quantity`, `produceDate`, and `expiryDate`.
- `shoppingList`: User-curated list mapping products to users for upcoming store visits.

---

## 🚀 Getting Started

### Prerequisites

- Node.js and npm
- Java 17 or later
- PostgreSQL database (e.g., Supabase instance)
- Auth0 account (configured Single Page Application and API)

### 1. Clone the repositorys

```powershell
# Clone the backend
git clone [https://github.com/erikcerne/Willis-backend.git](https://github.com/erikcerne/Willis-backend.git)

# Clone the frontend
git clone [https://github.com/erikcerne/willis-frontend.git](https://github.com/erikcerne/willis-frontend.git)
```

### 2. Configure & Run Backend

```powershell
cd Willis-backend
$env:DB_URL="jdbc:postgresql://localhost:5432/willis"
$env:DB_USER="postgres"
$env:DB_PASS="<your-password>"
./mvnw.cmd spring-boot:run
```

- API base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`

### 3. Configure & Run Frontend

Create `willis-frontend/.env.local`:

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_AUTH0_AUDIENCE=https://willis-api/
VITE_AUTH0_CLIENT_ID=<your-auth0-client-id>
VITE_AUTH0_DOMAIN=<your-auth0-domain>
```

Start the client:

```powershell
cd willis-frontend
npm install
npm run dev
```
