# Payment Orchestrator (Simplified)

Backend assignment: a simplified payment orchestration system (inspired by platforms like PhonePe) focusing on backend functionality.

## High-level overview

Flow (single service, layered architecture):

Client → **Controller** → **Service (Orchestration Engine)** → **Routing Engine** → **Provider Connectors (A/B)** → **Persistence (H2/JPA)**

Key ideas:
- **Routing**: `CARD → Provider A`, `UPI → Provider B` (with failover to the other provider)
- **Retry & Failover**: retry within a provider, then fail over to the next provider in the route
- **Status tracking**: `PROCESSING → SUCCESS/FAILED` stored in DB and retrievable via Fetch API

## Functional requirements mapping

- **Create Payment API**: `POST /api/v1/payments`
- **Fetch Payment API**: `GET /api/v1/payments/{paymentId}`
- **Routing (CARD → A, UPI → B)**: `routing/RoutingEngine`
- **Retry & Failover**: `service/PaymentOrchestrationServiceImpl`
- **Payment Status Tracking**: `entity/PaymentEntity` persisted via JPA

## Non-functional requirements (implemented)

- **Validation**: Bean Validation on request DTOs (`spring-boot-starter-validation`)
- **Observability**: Actuator metrics endpoints (`/actuator/metrics`, `/actuator/health`)
- **Resilience**: Provider retry + failover is implemented in the orchestration layer

## API documentation (Swagger UI)

- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Installation & execution

### Prerequisites
- Java installed (project targets **Java 8 bytecode**; newer JDKs can still compile/run it)

### Run locally

```bash
cd C:\Users\Admin\Developer\payment-orchestrator
.\mvnw.cmd test
.\mvnw.cmd spring-boot:run
```

### Optional dependencies

None.

## Example requests

### Create (CARD)

Body:
```json
{
  "amount": 10.00,
  "currency": "INR",
  "method": "CARD"
}
```

### Create (UPI)

```json
{
  "amount": 10.00,
  "currency": "INR",
  "method": "UPI"
}
```

### Fetch

`GET /api/v1/payments/{paymentId}`

## Integration points

- **Provider connectors**: `provider/ProviderConnector` (A and B are simulated in-code today)
  - Input: `PaymentEntity` + `CreatePaymentRequest`
  - Output: `ProviderChargeResult { success, providerReference, failureReason, retriable }`
- **Persistence**: JPA repository for `PaymentEntity`

## Test cases (high-level catalog)

### Sanity
- Create payment (CARD) success
- Create payment (UPI) success
- Fetch payment by id returns persisted status

### Regression
- Routing: CARD routes to A then B; UPI routes to B then A
- Failover: primary provider retriable failures → secondary provider success
- All providers fail → payment status `FAILED`

### Integration
- Controller validation: missing fields → **400 BAD REQUEST**
- Persistence: create then fetch returns consistent data in H2

## “Vibe coding” prompts used (development log)

This submission was built by iterating through prompts such as:
- “Scaffold a Spring Boot Java 8 project with layered packages (controller, dto, entity, enums, exception, provider, repository, retry, routing, service, config).”
- “Implement create/fetch payment APIs with routing (CARD→A, UPI→B), retry/failover, and status tracking.”
- “Add JUnit5/Mockito unit tests and a README including full test matrix (positive + negative), classification (sanity/regression/integration), and run instructions.”

## Performance considerations / metrics

- **DB writes**: single `payments` row per payment.
- **Metrics**: leverage Spring Boot Actuator:
  - request latency (via web metrics)
  - JVM metrics (heap, GC)
  - custom metrics can be added if needed (next improvement)

## Next improvements (bonus)
- Add `PaymentAttempt` table for detailed retry history and auditability
- Add provider timeouts + circuit breaker (Resilience4j)
- Add integration tests with Testcontainers (Redis/Postgres)

