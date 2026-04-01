![Sentinel Banner](resources/readme/banner.jpg)
# 🛡️ Sentinel - Privacy-First Social Platform

**Sentinel** is an open-source, privacy-first social media platform built with Spring Boot. Designed with digital well-being at its core, it prioritizes authentic human connections and data security over addictive engagement loops and intrusive tracking.

## 💡 Motivation

In today's digital landscape, mainstream social networks often rely on harvesting user data and employing addictive algorithms to maximize screen time. **Sentinel** was created as an ethical alternative. 

The motivation behind this project is to prove that a social platform can be engaging without compromising user privacy. By strictly adhering to EU GDPR standards, completely eliminating third-party trackers, and introducing a proactive "Safeguarded Mode" for minors, Sentinel aims to foster a healthier and safer digital environment.

## ✨ The "Ethical Feed" & Core Features

Sentinel delivers the seamless, entertaining experience you expect from mainstream social networks, but replaces manipulative engagement loops with user-centric boundaries. 

* **Familiar, Yet Bounded Entertainment:** Enjoy a highly engaging, personalized content feed that respects your time. Sentinel's algorithms are designed to entertain and surface high-quality posts, but they enforce healthy session limits. The system actively moderates the volume and type of content delivered in a single sitting to prevent endless "doomscrolling."
* **Strictly Isolated Recommendation Engine**: Mainstream apps compromise privacy by suggesting connections based on invisible metrics (exposing hidden follower graphs). Sentinel's engine is strictly isolated: friend and content suggestions are generated **ONLY** based on your own explicit activity and opted-in interests. The core intent is to empower users to form **authentic, meaningful relationships** instead of performative ones, moving away from profit-driven models that compromise mental health.
* **On-Device AI Wellbeing Assistant:** Sentinel utilizes a smart, integrated AI module to study your interaction habits and session lengths. It provides adequate, context-aware suggestions tailored to your routine—such as reminders to take a screen break, step away for a walk, or wind down for sleep.
* **100% Local Data Storage:** To guarantee absolute privacy, all behavioral data analyzed by the Wellbeing AI is processed and stored **strictly LOCALLY** on your device. Your usage patterns, sleep schedule suggestions, and session metrics never touch our backend servers.
* **Age-Aware Location Privacy:** For adult users (18+), location tracking is completely blocked by default. For minors, location services are enabled by default as an integrated safety measure. Crucially, this geolocation data is **NEVER** monetized, shared with third-party vendors, or used for any commercial purposes.
* **Native 'Safeguarded Mode':** A dedicated environment for minors that dynamically adjusts engagement constraints (e.g., messaging limits, content filtering) based on the user's age, proactively monitored by preemptive moderation layers.
* **Fully Customizable GUI:** The application interface is built from the ground up for maximum customizability. It empowers users to tailor their layouts, themes, and content delivery exactly how they want it, putting them fully in charge of their digital environment.

## 💰 Ethical Monetization Model

Sentinel is designed to be a self-sustaining platform without compromising its "Privacy-First" core. Our revenue model is built on transparency and respect for the user:

* **Ad-Supported, Not Surveillance-Driven**: Profit is generated through non-intrusive advertisements and strategic sponsorships.
* **Zero Data Brokerage**: We strictly pledge to **NEVER** sell, rent, or trade user data to third-party vendors or commercial brokers.
* **Contextual Advertising**: Ads are served based on general content categories or opted-in interests, completely avoiding invasive cross-site tracking or "shadow profiling".
* **Sustainability through Integrity**: By decoupling profit from data harvesting, Sentinel proves that a social network can be financially viable while treating user privacy as a fundamental human right.
  
## ⚙️ Built With (Tech Stack)

Sentinel is architected with scalability and security in mind, utilizing a modern enterprise technology stack:

* **Backend:** Java, Spring Boot, Spring Security
* **Frontend:** Vue.js
* **Database:** PostgreSQL, Hibernate / Spring Data JPA
* **Authentication:** OAuth2 / JWT (JSON Web Tokens)

## 📈 Performance & Security Metrics

* **API Efficiency:** Delivers content across 20+ RESTful API endpoints with an average response time of under 100ms.
* **Zero Data Retention Moderation:** Implemented preemptive moderation layers that analyze and filter hazardous content in under 200ms with zero server-side data retention.
* **GDPR Compliance:** Architected to achieve 100% compliance with EU data protection standards by intentionally excluding third-party tracking metrics.

## 🌍 Open Source

Sentinel is proudly **Open Source**. We believe that privacy-centric tools should be completely transparent. Developers, security researchers, and privacy advocates are highly encouraged to explore the codebase, audit the ethical recommendation algorithms, and contribute to building a safer social web.

---
