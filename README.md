# Banking Rewards & Cashback Platform 

A modular monolith financial application built with **Spring Boot 3 (Java 21)** and **React + TypeScript + Tailwind CSS** demonstrating real-world financial transaction reward calculations, multi-rule evaluation pipelines, immutable double-entry wallet ledgering, and 1-click refund reversals.

---

## 🏛 Architecture & Financial Principles

1. **5 Core Backend Modules:**
   - **User Service:** Profile management, referral generation, and loyalty tiers (Silver, Gold, Platinum).
   - **Transaction Service:** Validates, records purchases, and coordinates rewards/refunds.
   - **Reward Engine:** Extensible pipeline implementing `RewardRule` interfaces (`BaseRewardRule`, `CategoryRewardRule`, `MerchantRewardRule`, `TierRewardRule`, `CampaignRewardRule`).
   - **Reward Wallet:** Audit ledger tracking available cashback and loyalty points without direct unverified balance mutations.
   - **Campaign/Rules Service:** Real-time dynamic boosters and category/merchant promo matrices.

2. **Immutable Ledger & Refund Reversal:**
   - When transactions are refunded, rewards are NOT silently deleted.
   - The platform creates an explicit `REVERSED` negative audit record in the ledger (`-₹40`) and deducts the balance from the wallet to ensure complete auditability.

---

## 🚀 Running the Project

### 1. Start the Spring Boot Backend (Port 8080)
```bash
cd backend
mvn spring-boot:run
```
- API Base URL: `http://localhost:8080/api`
- In-memory H2 Console (PostgreSQL mode): `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:rewardsdb`, User: `sa`, Password: empty)

### 2. Start the React Frontend (Port 5173)
```bash
cd frontend
npm run dev
```
- Open `http://localhost:5173` in your browser.

---

## 🧪 Running Automated Tests
```bash
cd backend
mvn clean test
```
