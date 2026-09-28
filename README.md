# Automated Public Transit Fleet Tracking & Ticketing System

A Spring Boot + MySQL backend for a transit management system: bus fleet operations,
dynamic passenger ticketing, route maintenance billing, and transit authority budget/financial
reporting — built as a layered `entity / repository / service / controller` project (matching
your demo project's structure) under the base package `com.example.transit`.

## 1. Tech stack

- Java 17, Spring Boot 3.2.5
- Spring Web, Spring Data JPA, Bean Validation
- MySQL 8 (via `mysql-connector-j`)
- Lombok
- Maven

## 2. Project structure

```
src/main/java/com/example/transit/
  entity/            14 JPA entities (Bus, Route, Contact, Product, Account, Journal,
                      JournalEntry, PurchaseOrder, VendorBill, SalesOrder, CustomerInvoice,
                      Payment, AnalyticAccount, Budget)
  entity/enums/       Status & type enums
  repository/         Spring Data JPA repositories, one per entity
  service/             Business logic, incl. LedgerService (posts double-entry journal
                      entries) and ReportService (Balance Sheet / P&L / Budget / Route reports)
  controller/         REST controllers, one per resource
  dto/                Request/response DTOs for transactional & reporting endpoints
  exception/          Custom exceptions + a global @RestControllerAdvice error handler
  config/              DataSeeder — seeds the Chart of Accounts, Journals and default
                      Products on first run
```

## 3. Setup

### 3.1 Create the database (optional)
The datasource URL uses `createDatabaseIfNotExist=true`, so MySQL will create the
`transit_management` schema automatically the first time the app connects — you only need a
MySQL server running and reachable with the configured credentials.

### 3.2 Configure credentials
Edit `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/transit_management?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root
```

### 3.3 Run

```bash
mvn spring-boot:run
```

or build a jar and run it:

```bash
mvn clean package
java -jar target/transit-management-system-1.0.0.jar
```

The API starts on **http://localhost:8080**.

On first startup, `DataSeeder` automatically creates:
- **Chart of Accounts**: Cash (1000), Bank (1001), Accounts Receivable (1100),
  Bus Fleet Inventory (1200), Vendor Creditors/Accounts Payable (2000),
  Fare Box Revenue (4000), Fuel & Maintenance Expenses (5000)
- **Journals**: Sales Journal, Purchase Journal, Bank Journal, Cash Journal
- **Default Products**: Express Route Ticket (Service), Fleet Maintenance (Service),
  Diesel Fuel (Goods)

## 4. Core business flows

### 4.1 Fleet & route setup
```
POST /api/buses          { "registrationNumber": "BUS-101", "model": "Volvo 9400", "capacity": 40 }
POST /api/routes         { "routeCode": "R-101", "routeName": "Urban Express", "startPoint": "Downtown",
                            "endPoint": "Airport", "distanceKm": 18.5, "baseFare": 50 }
POST /api/contacts       { "name": "Jane Doe", "type": "PASSENGER", "phone": "555-1234" }
```

### 4.2 Ticket booking (with double-booking prevention + dynamic fare)
```
POST /api/tickets
{
  "passengerId": 1,
  "routeId": 1,
  "busId": 1,
  "seatNumber": "12A",
  "travelDate": "2026-10-05"
}
```
Booking the same bus/date/seat again returns **409 Conflict**. When a bus crosses 80%
occupancy for that trip, the fare is automatically surged by 10% over the route's base fare.

### 4.3 Generate the passenger's digital receipt & collect payment
```
POST /api/invoices/from-ticket/{salesOrderId}
POST /api/payments/receive/{invoiceId}     { "mode": "CASH" }   // or BANK / DIGITAL
```
This posts `Dr Accounts Receivable / Cr Fare Box Revenue` on invoice creation, then
`Dr Cash-or-Bank / Cr Accounts Receivable` on payment — tagged to the route's analytic account.

### 4.4 Fuel / maintenance purchasing
```
POST /api/purchase-orders
{ "vendorId": 2, "productId": 2, "quantity": 1, "orderDate": "2026-10-01" }

POST /api/vendor-bills/from-po/{purchaseOrderId}
POST /api/payments/pay/{billId}            { "mode": "BANK" }
```
This posts `Dr Fuel & Maintenance Expense / Cr Accounts Payable` on bill creation, then
`Dr Accounts Payable / Cr Cash-or-Bank` on payment.

### 4.5 Budgets (Analytic Account = Transit Route)
```
POST /api/analytic-accounts     { "name": "Route-101 Urban Express", "route": { "id": 1 } }
POST /api/budgets
{ "name": "Q4 Route-101 Budget", "analyticAccountId": 1, "plannedAmount": 20000,
  "periodStart": "2026-10-01", "periodEnd": "2026-12-31" }
```
(An analytic account is also auto-created the first time a route generates ticket revenue,
so you can skip this step and let the system tag it automatically.)

## 5. Reporting endpoints

| Report | Endpoint |
|---|---|
| Balance Sheet | `GET /api/reports/balance-sheet?asOfDate=2026-12-31` |
| Profit & Loss | `GET /api/reports/profit-and-loss?from=2026-10-01&to=2026-12-31` |
| Budget variance (planned vs actual per route) | `GET /api/reports/budget-variance` |
| Route utilization (bookings, revenue, occupancy %) | `GET /api/reports/route-utilization/{routeId}?from=...&to=...` |

## 6. Full endpoint list

| Resource | Endpoints |
|---|---|
| Buses | `POST/GET /api/buses`, `GET/PUT/DELETE /api/buses/{id}` |
| Routes | `POST/GET /api/routes`, `GET/PUT/DELETE /api/routes/{id}` |
| Contacts | `POST/GET /api/contacts`, `GET/PUT/DELETE /api/contacts/{id}` |
| Products | `POST/GET /api/products`, `GET/PUT/DELETE /api/products/{id}` |
| Chart of Accounts | `POST/GET /api/accounts`, `GET /api/accounts/{id}` |
| Journals | `POST/GET /api/journals`, `GET /api/journals/{id}` |
| Tickets (Sales Order) | `POST/GET /api/tickets`, `GET /api/tickets/{id}`, `POST /api/tickets/{id}/cancel` |
| Purchase Orders | `POST/GET /api/purchase-orders`, `GET /api/purchase-orders/{id}` |
| Vendor Bills | `POST /api/vendor-bills/from-po/{purchaseOrderId}`, `GET /api/vendor-bills`, `GET /api/vendor-bills/{id}` |
| Customer Invoices | `POST /api/invoices/from-ticket/{salesOrderId}`, `GET /api/invoices`, `GET /api/invoices/{id}` |
| Payments | `POST /api/payments/receive/{invoiceId}`, `POST /api/payments/pay/{billId}`, `GET /api/payments` |
| Analytic Accounts | `POST/GET /api/analytic-accounts`, `GET /api/analytic-accounts/{id}` |
| Budgets | `POST/GET /api/budgets`, `GET /api/budgets/{id}` |
| Reports | see section 5 |

## 7. Notes & assumptions

- Every financial transaction posts a **balanced double-entry pair** (one debit line, one
  credit line) to `journal_entries`, so the Balance Sheet and P&L are always derivable straight
  from the ledger rather than from cached totals.
- `spring.jpa.hibernate.ddl-auto=update` auto-creates/updates all tables from the entities —
  no manual schema script is needed for a fresh database.
- Double-booking prevention checks bus + travel date + seat number against all non-cancelled
  bookings.
- Dynamic fare: +10% surcharge once a bus's confirmed bookings for that trip reach 80% of its
  seating capacity; otherwise the route's base fare applies.
- Route utilization's "capacity offered" is the sum of seating capacity across the distinct
  bus/travel-date trips that actually had bookings in the given period — adjust this logic in
  `ReportService` if you want it to include scheduled-but-unbooked trips too.
