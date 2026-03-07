<p align="center">
  <img src="resources/readme/banner.png" alt="Sentinel Banner" width="100%">
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" />
  <img src="https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D" />
  <img src="https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" />
</p>

---

# **🛡️ Sentinel — Privacy-Centric Social Infrastructure**

**Sentinel** is a full-stack social networking platform engineered for high performance and data sovereignty. It provides a secure, modular environment for authentic interaction, built on a modern **Java** and **Vue.js** ecosystem.

---

## **🚀 Platform Capabilities**

Sentinel provides a streamlined social experience focused on functional utility and user data protection:

* **⚡ Optimized Content Delivery:** High-performance feed engine utilizing server-side filtering to surface relevant content.
* **🔒 Sovereign Data Model:** User activity and behavioral analysis remain strictly **on-device**, utilizing localized processing.
* **🛡️ Built-in Safeguards:** Native mode for minors with dynamic engagement constraints and preemptive moderation protocols.
* **📈 Contextual Advertising:** Monetization through category-based content matching, eliminating the need for cross-site tracking.

---

## **🏗️ Architecture & Engineering Deep Dive**

Sentinel is built on a foundation of decoupled, scalable, and maintainable software patterns designed for enterprise-grade stability.

### **🏛️ Frontend: Feature-Driven Design (FDD)**
The frontend is built with **Vue.js 3** and **TypeScript**, organized into an FDD architecture to ensure modularity.
* **Domain Isolation:** Logical modules (**Auth, Post, Chat, User**) are self-contained. Changes to one domain do not impact others.
* **The Barrel Pattern:** Using `index.ts` files as "Public APIs" for each feature to enforce strict encapsulation and prevent spaghetti code.
* **Scalable Type System:** Decentralized TypeScript interfaces ensure type safety without the bottlenecks of monolithic type files.

### **☕ Backend: Stateless & High-Performance**
A robust, stateless backend designed for high concurrency following the industry gold standard.
* **Stateless Authentication (JWT):** Utilizes **Spring Security** with **JSON Web Tokens**. This allows the backend to scale horizontally without server-side session dependencies.
* **Sub-100ms Latency:** Achieved through specialized **DTOs** and optimized **JPA/Hibernate** interogations, ensuring rapid data delivery.
* **Persistence Layer:** **PostgreSQL** with strategic indexing and relational integrity for high-speed data retrieval.

### **🔒 Innovation: Edge AI & Privacy**
* **Local Processing:** Sentinel is architected for **on-device inference**. Behavioral analysis tasks stay strictly on the user's hardware.
* **Sovereign Analytics:** By analyzing habits locally, the server remains "blind" to private user patterns, fundamentally reducing the risk of centralized data breaches.

---

## **⚙️ Tech Stack Summary**

| Layer | Technology |
| :--- | :--- |
| **Backend** | Java 17+, Spring Boot, Spring Security |
| **Frontend** | Vue.js 3 (Composition API), TypeScript, Vite |
| **Database** | PostgreSQL |
| **Auth** | Stateless JWT (JSON Web Tokens) |
| **Infrastructure** | Docker, Docker Compose |
| **AI/ML** | Architectural support for ONNX/Python local inference |

---

## **🏁 Getting Started**

### **Prerequisites**
* **Docker** & **Docker Compose**

### **Deployment**
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/bogdan-coste/Sentinel.git](https://github.com/bogdan-coste/Sentinel.git)
    cd Sentinel
    ```

2.  **Launch the infrastructure:**
    ```bash
    docker compose up -d
    ```

3.  **Access the application:**
    The frontend is available at **`http://localhost:5173`**.

---

## **🌍 Open Source & Contributing**

Sentinel is an **open-source project**. We welcome contributions regarding performance optimizations, security audits, and feature enhancements.

**Sentinel — Privacy-First. Performance-Driven.**