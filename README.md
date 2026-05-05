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

##  Running the Project


1. Build JAR
 
    mvn clean install 

2. Start Services

    docker-compose up --build

3. REST API

    http://localhost:8080/api/fraud/check

4. H2 Console

    http://localhost:8080/h2-console

5. JDBC URL:

    jdbc:h2:mem:frauddb

    Username: sa



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


############################
# 🚀 1. SUBMIT TRANSACTION
############################

curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "user1",
  "amount": 100,
  "location": "ZA"
}'

# Response
{
  "transactionId": 1,
  "status": "PROCESSING",
  "message": "Transaction queued for processing"
}

############################
# 🔄 2. CHECK STATUS
############################

curl http://localhost:8080/status/1

# Processing
{
  "transactionId": 1,
  "status": "PROCESSING",
  "fraudDetected": false,
  "alertCount": 0
}

# Completed
{
  "transactionId": 1,
  "status": "COMPLETED",
  "fraudDetected": false,
  "alertCount": 0
}

############################
# 📄 2.1 TRANSACTION DETAILS
############################

curl http://localhost:8080/status/1/details

{
  "transactionId": 1,
  "userId": "user1",
  "amount": 100,
  "location": "ZA",
  "status": "COMPLETED",
  "fraudDetected": false,
  "rulesTriggered": [],
  "alerts": []
}

############################
# 🔥 3. HIGH VALUE FRAUD
############################

curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "rich-user",
  "amount": 50000,
  "location": "ZA"
}'

curl http://localhost:8080/status/2/details

{
  "transactionId": 2,
  "fraudDetected": true,
  "rulesTriggered": ["HIGH_VALUE"],
  "alerts": [
    {
      "id": 1,
      "transactionId": 2,
      "rulesTriggered": "HIGH_VALUE",
      "createdAt": "2026-05-05T23:10:00"
    }
  ]
}

############################
# 🌍 4. LOCATION FRAUD
############################

curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "user2",
  "amount": 500,
  "location": "US"
}'

curl http://localhost:8080/status/3/details

{
  "transactionId": 3,
  "fraudDetected": true,
  "rulesTriggered": ["LOCATION_MISMATCH"],
  "alerts": [
    {
      "id": 2,
      "transactionId": 3,
      "rulesTriggered": "LOCATION_MISMATCH",
      "createdAt": "2026-05-05T23:10:00"
    }
  ]
}

############################
# ⚡ 5. VELOCITY FRAUD TEST
############################


curl -s -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
"userId": "speed-user",
"amount": 10,
"location": "ZA"
}'


curl http://localhost:8080/status/10/details

{
  "transactionId": 10,
  "fraudDetected": true,
  "rulesTriggered": ["VELOCITY"]
}

############################
# 💥 6. EXTREME FRAUD
############################

curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "fraudster",
  "amount": 99999,
  "location": "US"
}'

curl http://localhost:8080/status/11/details

{
  "transactionId": 11,
  "fraudDetected": true,
  "rulesTriggered": [
    "HIGH_VALUE",
    "LOCATION_MISMATCH"
  ]
}

############################
# ❌ 7. INVALID PAYLOAD
############################

curl -X POST http://localhost:8080/api/fraud/check \
-H "Content-Type: application/json" \
-d '{
  "userId": "",
  "amount": -100,
  "location": ""
}'

{"status":"FAILED","errors":["amount: amount must be greater than 0","location: location is required","userId: userId is required"]}


