# 🚀 Fraud Rule Engine Service

## 📌 Overview

This system processes transaction events, applies fraud detection rules, and flags suspicious activity.

It supports:

- Real-time processing via Kafka
- Rule-based fraud detection (extensible strategy engine)
- REST API for manual checks
- H2 in-memory persistence
- Retry + Dead Letter Queue (DLQ)

---

## ⚙️ Tech Stack

- Java 17
- Spring Boot
- Spring Kafka
- H2 Database
- Docker + Kafka (Confluent)
- Lombok

---



## 📦 Features

- Strategy-based rule engine
- Config-driven thresholds (`application.yml`)
- Kafka retry with backoff
- Dead Letter Queue (DLQ)
- Structured logging (`event=...`)

---

##  Running the Project

###
```bash

1. Build JAR
 
mvn clean install 

2. Start Services

docker-compose up --build

3. REST API

http://localhost:8080/api/fraud/check

4. H2 Console

http://localhost:8080/h2-console

JDBC URL:

jdbc:h2:mem:frauddb


🧪 CURL TEST SCENARIOS
✅ 1. Normal Transaction (No Fraud)
curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "user1",
  "amount": 100,
  "location": "ZA"
}'

🔥 2. High Value Fraud Trigger
curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "user1",
  "amount": 20000,
  "location": "ZA"
}'

🌍 3. Location Mismatch Fraud
curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "user1",
  "amount": 500,
  "location": "US"
}'

⚡ 4. Velocity Rule Trigger (Rapid Requests)

Run multiple times quickly:

curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "velocity-user",
  "amount": 50,
  "location": "ZA"
}'

❌ 5. Invalid Payload (Validation Test)
curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "",
  "amount": -100,
  "location": ""
}'

💥 6. Extreme Fraud (All Rules Triggered)
curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "fraud-user",
  "amount": 99999,
  "location": "US"
}'

