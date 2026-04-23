# ADR 019: Saga Pattern for Data Consistency

## Context
In a microservices architecture, transactions span multiple services (Order, Product, etc.). Traditional 2PC (Two-Phase Commit) is not suitable due to its blocking nature and high latency, especially under high load (Order Burst). We need a way to ensure data consistency across services without sacrificing performance or availability.

## Decision
We will implement the **Saga Pattern (Choreography-based)** to manage distributed transactions.

1. **Successful Path:**
   - `order-service`: Publishes `OrderCreatedEvent`.
   - `product-service`: Consumes event, decreases stock.
   - `notification-service`: Consumes event, sends alert.

2. **Failure Path (Compensation):**
   - If `product-service` fails (e.g., out of stock), it publishes `OrderFailedEvent`.
   - `order-service` consumes this event and updates order status to `CANCELLED`.

3. **Reliability:**
   - Use the **Transactional Outbox Pattern** to ensure events are never lost between DB and Kafka.

## Consequences
- **Positive:** High throughput, system decoupling, self-healing capability.
- **Negative:** Increased complexity in error handling, "Eventual Consistency" (users might see a pending status for a short time before a potential cancellation).
- **Tooling:** Requires Kafka as a robust message backbone.
