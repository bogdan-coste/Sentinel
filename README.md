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
  <img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=JSON%20web%20tokens&logoColor=white" />
</p>

---

# **🛡️ Sentinel: Social Media, But Human Again.**

**Sentinel** is an open-source alternative to mainstream social media. It is designed for **digital well-being**, prioritizing authentic human connection and data sovereignty over addictive loops and intrusive surveillance.

> **The Mission:** **To prove that social platforms can be engaging without compromising your fundamental right to privacy.**

---

## **🌟 Why Sentinel? (The User Experience)**

Mainstream networks turn you into a product. **Sentinel puts you back in the driver's seat:**

* **🧘 Ethical Feed:** Our algorithm surfaces quality content that respects your time. Once you're caught up, Sentinel encourages you to step away and enjoy the real world.
* **🕵️ Radical Privacy:** We don't track you across the web. Connections are built on your explicit interests, not "shadow profiling" or hidden data harvesting.
* **🧒 Native Safety:** A dedicated environment for minors with built-in protection and engagement constraints as a default, not an afterthought.
* **🚫 Clean Monetization:** We use contextual ads based on what you’re viewing right now—never on your private browsing history or personal identity.

---

## **🚀 Quick Start**

Launch the entire Sentinel ecosystem (Frontend, Backend, and Database) in minutes using Docker.

### **Prerequisites**
* **Docker** and **Docker Compose** installed.

### **Installation**
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/bogdan-coste/Sentinel.git](https://github.com/bogdan-coste/Sentinel.git)
    cd Sentinel
    ```

2.  **Launch everything:**
    ```bash
    docker compose up -d
    ```

3.  **Access the App:**
    Open your browser and go to **`http://localhost:5173`**. You’re ready to go!

---

## **🛠️ Technical Achievements**

Sentinel isn't just ethical; it's engineered for scale and maintainability using modern industry standards.

### **🏛️ Frontend: Feature-Driven Design (FDD)**
The Vue.js 3 application was recently refactored from a monolithic structure into a modular, **Feature-Driven** architecture.
* **Domain Isolation:** Features like `Auth`, `Post`, `Chat`, and `User` are self-contained with their own components, logic, and types.
* **Scalable Types:** Decentralized TypeScript interfaces prevent "god-file" syndrome and ensure type safety across the board.
* **Clean Imports:** Advanced path aliasing (`@features`, `@shared`) and Barrel files (`index.ts`) reduce code clutter and import complexity.

### **⚙️ Backend: Robust Java Ecosystem**
* **Stateless Security:** Built with **Spring Boot** and **Spring Security**, utilizing **JWT** for secure, scalable authentication.
* **Database Integrity:** Powered by **PostgreSQL** with optimized JPA queries, maintaining a sub-100ms average API response time.
* **Edge-AI Ready:** The architecture is designed to support on-device inference, allowing behavioral analysis to stay 100% local to the user's hardware.

---

## **🌍 Open Source & Contributing**

Sentinel is built on the belief that **transparency is the only guarantee of privacy.** We are a community-driven project. Whether you want to fix a bug, suggest a feature, or improve the documentation, we welcome your contributions! Feel free to open an issue or submit a Pull Request.

---

**Sentinel — Privacy is a right, not a luxury.**