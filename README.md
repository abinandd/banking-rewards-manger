# VaultRewards — Banking Rewards & Cashback Engine

A production-grade, modular financial application built with **Spring Boot 3 (Java 21)** and **React + TypeScript + Tailwind CSS**, implementing real-world financial transaction reward calculations, multi-rule evaluation pipelines, immutable double-entry wallet ledgering, and 1-click refund reversals.

---

## 🏛 Architecture & Financial Principles

1. **Core Backend Modules:**
   - **User Service:** Customer profiles, referral code generators, and multi-tier loyalty matrix (Silver, Gold, Platinum).
   - **Transaction Service:** Validates, records purchases, and coordinates real-time reward settlement and refund reversals.
   - **Reward Engine:** Extensible pipeline evaluating `RewardRule` strategies (`BaseRewardRule`, `CategoryRewardRule`, `MerchantRewardRule`, `TierRewardRule`, `CampaignRewardRule`).
   - **Reward Wallet & Double-Entry Ledger:** Audit trail tracking available cashback and loyalty points with DB-level pessimistic locking (`@Lock(PESSIMISTIC_WRITE)`).
   - **Campaigns & Rules Administration:** Real-time dynamic boosters and category/merchant promo matrices.

2. **Immutable Ledger & Refund Reversal:**
   - When transactions are refunded, rewards are NOT deleted or quietly mutated.
   - The platform creates an explicit `REVERSED` negative audit record in the ledger (`-₹40.00`) and adjusts the wallet balance to ensure full compliance and reconciliation.

3. **Production Enhancements:**
   - **Spring Boot Actuator & Micrometer Prometheus:** Health probes (`/actuator/health`), metrics (`/actuator/metrics`), and Prometheus scrape target (`/actuator/prometheus`).
   - **OpenAPI 3.0 / Swagger UI:** Interactive documentation at `/swagger-ui.html` and `/v3/api-docs`.
   - **Request Tracing & Security Headers:** SLF4J MDC `X-Correlation-Id` tracking and HTTP security headers (`nosniff`, `DENY`, `X-XSS-Protection`).
   - **Containerization:** Multi-stage Dockerfiles (Temurin 21 JRE & Nginx Alpine) with `docker-compose.yml` for PostgreSQL 16 orchestration.
   - **Automated CI/CD:** GitHub Actions workflow (`.github/workflows/ci.yml`) testing backend, frontend, and Docker image builds.

---

## 🚀 Deployment & Running

### Option 1: One-Click Production Deployment with Docker Compose
```bash
# 1. Copy environment template
cp .env.example .env

# 2. Start PostgreSQL, Backend, and Frontend containers
docker compose up -d --build

# 3. Access applications:
# - Frontend: http://localhost
# - Backend API: http://localhost:8080/api
# - Swagger UI: http://localhost:8080/swagger-ui.html
# - Actuator Health: http://localhost:8080/actuator/health
```

### Option 2: Local Development

#### 1. Start the Spring Boot Backend (Port 8080)
```bash
cd backend
mvn spring-boot:run
```
- API Base URL: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- In-memory H2 Console: `http://localhost:8080/h2-console` (`jdbc:h2:mem:rewardsdb`, User: `sa`, Password: empty)

#### 2. Start the React Frontend (Port 5173)
```bash
cd frontend
npm install
npm run dev
```
- Open `http://localhost:5173` in your browser.

---

---

## 📡 API Endpoints Overview

| Method | Endpoint | Description |
| :--- | :--- | :--- |
| `POST` | `/api/transactions` | Create transaction & evaluate real-time rewards |
| `GET` | `/api/transactions?userId={id}` | Retrieve transaction history |
| `POST` | `/api/transactions/{id}/refund` | Refund transaction & reverse rewards in ledger |
| `GET` | `/api/users/{userId}/rewards` | Retrieve customer rewards audit ledger |
| `GET` | `/api/users/{userId}/rewards/balance` | Query wallet cashback & points balance |
| `GET` | `/api/rewards/rules` | List active reward rules |
| `POST` | `/api/rewards/rules` | Create dynamic reward rule |
| `GET` | `/api/campaigns` | List active promotional campaigns |
| `POST` | `/api/referrals/invite` | Send customer referral invitation |
| `GET` | `/actuator/health` | Application liveness & readiness probes |
| `GET` | `/actuator/prometheus` | Prometheus metrics scrape endpoint |
