
# Fraud Rule Engine

Enterprise-grade fraud detection and rule engine microservice built with Java 17, Spring Boot 3.x, PostgreSQL, Kafka, and Redis.

## Features

- **Configurable Fraud Rules**: Create, update, and delete fraud rules without code changes
- **Shadow/Draft Mode**: Test rules in production without affecting real decisions
- **Sliding Window Velocity Checks**: Track events over time using Redis sorted sets
- **Kafka Integration**: Async processing of transactions
- **Risk Scoring**: Dynamic risk calculation based on matched rules
- **Rule Conflict Resolution**: Highest priority rules and most severe actions take precedence
- **Resilience Patterns**: Circuit Breaker and Rate Limiter (using Resilience4j)
- **Multiple Data Sources**: Support for customer, device, payment, location, and merchant data
- **JWT Authentication**: Secure API access with role-based authorization
- **Redis Caching**: High-performance rule caching
- **Flyway Migrations**: Automated database schema management
- **OpenAPI/Swagger**: Interactive API documentation
- **Docker Support**: Full containerized deployment

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                          REST API / Kafka Producer                          │
├─────────────────────────────────────────────────────────────────────────────┤
│                           Service Layer                                     │
├─────────────────────────────────────────────────────────────────────────────┤
│           Rule Engine         │     Risk Engine         │  Velocity Service │
├─────────────────────────────────────────────────────────────────────────────┤
│                          Repository Layer                                    │
├─────────────────────────────────────────────────────────────────────────────┤
│    PostgreSQL  │    Redis    │                Kafka                           │
└─────────────────────────────────────────────────────────────────────────────┘
```

## Quick Start

### Prerequisites

- Java 17
- Maven 3.8+
- Docker Desktop or Docker Engine with Docker Compose

**Verify your Docker installation by running:**
```bash
docker --version
```

**Check if Compose is available with:**
```bash
# For modern Docker Desktop (preferred)
docker compose version

# Or for older standalone Docker Compose
docker-compose version
```

### Run Locally with Docker

If using Docker Desktop (modern versions), use `docker compose` (with space, no hyphen):
```bash
docker compose up -d --build
```

If using the older Docker Compose standalone (with hyphen):
```bash
docker-compose up -d --build
```

### Run Locally without Docker

1. Start PostgreSQL, Redis, and Kafka locally
2. Update `application-dev.yml` with your database and Kafka credentials
3. Run the application:

```bash
mvn spring-boot:run
```

## API Documentation

Swagger UI is available at: http://localhost:8080/api/swagger-ui/index.html

## Default Credentials

For demonstration purposes, the default admin credentials are:
- Username: `admin`
- Password: `admin123` (bcrypt encoded in the database)

## Example Usage

### Fraud Check Request

```bash
curl -X POST http://localhost:8080/api/fraud/check \
  -H "Content-Type: application/json" \
  -H "Idempotency-Key: test-transaction-1" \
  -d '{
    "sessionId": "session-123",
    "customer": {
      "id": "cust-456",
      "accountAgeDays": 2
    },
    "payment": {
      "amount": 1500.00
    }
  }'
```

### Create a Fraud Rule (Active Mode)

```bash
curl -X POST http://localhost:8080/api/fraud-rules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Account High Amount",
    "description": "Flag new accounts with high payment amounts",
    "enabled": true,
    "status": "ACTIVE",
    "priority": 10,
    "weight": 50,
    "action": "REVIEW",
    "ruleType": "PAYMENT",
    "conditions": [
      {
        "field": "customer.accountAgeDays",
        "operator": "LESS_THAN",
        "value": "7"
      },
      {
        "field": "payment.amount",
        "operator": "GREATER_THAN",
        "value": "1000"
      }
    ]
  }'
```

### Create a Velocity Rule

```bash
curl -X POST http://localhost:8080/api/fraud-rules \
  -H "Content-Type: application/json" \
  -d '{
    "name": "High Transaction Velocity",
    "description": "Block users with >5 transactions in 10 minutes",
    "enabled": true,
    "status": "ACTIVE",
    "priority": 20,
    "weight": 100,
    "action": "BLOCK",
    "ruleType": "CUSTOMER",
    "conditions": [
      {
        "field": "customer.id",
        "operator": "VELOCITY_GT",
        "value": "5:600"
      }
    ]
  }'
```

## Configuration

Key configuration properties in `application.yml`:

```yaml
spring:
  kafka:
    bootstrap-servers: localhost:9092
    consumer:
      group-id: fraud-check-group
      auto-offset-reset: earliest

app:
  jwt:
    secret: your-secret-key
    expiration: 86400000  # 24 hours
  risk:
    thresholds:
      allow: 30
      review: 70
      block: 100

resilience4j:
  circuitbreaker:
    instances:
      fraudCheck:
        register-health-indicator: true
        sliding-window-type: COUNT_BASED
        sliding-window-size: 100
        minimum-number-of-calls: 10
        permitted-number-of-calls-in-half-open-state: 3
        automatic-transition-from-open-to-half-open-enabled: true
        wait-duration-in-open-state: 10s
        failure-rate-threshold: 50
  ratelimiter:
    instances:
      fraudCheck:
        limit-for-period: 100
        limit-refresh-period: 1s
```

## Technologies

- **Java 17**
- **Spring Boot 3.3.x**
- **Spring Security**
- **Spring Data JPA**
- **Spring Data Redis**
- **Spring Kafka**
- **PostgreSQL**
- **Redis**
- **Flyway**
- **Lombok**
- **MapStruct**
- **Hypersistence Utils**
- **Resilience4j**
- **OpenAPI/Swagger**
- **JUnit 5**
- **Mockito**

## License

Apache 2.0
