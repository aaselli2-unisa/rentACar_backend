<div align="center">

# ExtendRent - Secure Software Development Lifecycle

[![CI](https://github.com/aaselli2-unisa/rentACar_backend/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/aaselli2-unisa/rentACar_backend/actions/workflows/ci.yml)
[![Deploy](https://github.com/aaselli2-unisa/rentACar_backend/actions/workflows/deploy.yml/badge.svg)](https://github.com/aaselli2-unisa/rentACar_backend/actions/workflows/deploy.yml)

![Java](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.14-6DB33F?style=flat-square&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring_Security-6.5.10-6DB33F?style=flat-square&logo=springsecurity&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-42.7.11-4169E1?style=flat-square&logo=postgresql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-jjwt_0.11.2-000000?style=flat-square&logo=jsonwebtokens&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36?style=flat-square&logo=apachemaven&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-Latest-2496ED?style=flat-square&logo=docker&logoColor=white)
![React](https://img.shields.io/badge/React-18.0.2-61DAFB?style=flat-square&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-4.9.5-3178C6?style=flat-square&logo=typescript&logoColor=white)
![JUnit 5](https://img.shields.io/badge/JUnit_5-5.x-25A162?style=flat-square&logo=junit5&logoColor=white)
![GitGuardian](https://img.shields.io/badge/GitGuardian-enabled-EF0000?style=flat-square&logoColor=white)
![Snyk](https://img.shields.io/badge/Snyk-SCA-4C4A73?style=flat-square&logo=snyk&logoColor=white)
![Semgrep](https://img.shields.io/badge/Semgrep-SAST-0B72E5?style=flat-square&logoColor=white)
![Trivy](https://img.shields.io/badge/Trivy-container--scan-1904DA?style=flat-square&logoColor=white)

</div>

Progetto Universitario - **Andrea Aselli** · **Benedetto Pio Turino** · **Emanuele Pascale**. 

---

## Indice

1. [Descrizione dell'Applicazione, Stack Tecnologico e System Design](#1-descrizione-dellapplicazione-stack-tecnologico-e-system-design)
   - [1.1 Descrizione dell'Applicazione](#11-descrizione-dellapplicazione)
   - [1.2 Stack Tecnologico](#12-stack-tecnologico)
   - [1.3 System Design - Class Diagram](#13-system-design--class-diagram)
2. [Frontend](#2-frontend)
   - [2.1 Stack Tecnologico](#21-stack-tecnologico)
   - [2.2 Architettura e Struttura](#22-architettura-e-struttura)
   - [2.3 Autenticazione e Sicurezza](#23-autenticazione-e-sicurezza)
   - [2.4 Pagine e Route Principali](#24-pagine-e-route-principali)
   - [2.5 Configurazione nginx](#25-configurazione-nginx)
   - [2.6 Flusso di Una Feature](#26-flusso-di-una-feature-ricerca-e-prenotazione-auto)
3. [Architettura e Ruoli](#3-architettura-e-ruoli)
   - [3.1 Struttura Generale](#31-struttura-generale)
   - [3.2 Architettura a Layer del Backend](#32-architettura-a-layer-del-backend)
   - [3.3 Ruoli](#33-ruoli)
   - [3.4 Matrice di Accesso RBAC](#34-matrice-di-accesso-rbac)
4. [API Reference](#4-api-reference)
5. [Containerizzazione](#5-containerizzazione)
   - [5.1 Dockerfile Backend](#51-dockerfile-backend-rentacar_backenddockerfile)
   - [5.2 Dockerfile Frontend](#52-dockerfile-frontend-rent-a-car-frontend-projectdockerfile)
   - [5.3 Docker Compose](#53-docker-compose-rentacar_backenddocker-composeyml)
6. [Vulnerabilità Identificate - Audit Manuale](#6-vulnerabilità-identificate--audit-manuale)
   - [6.1 Vulnerabilità Identificate](#61-vulnerabilità-identificate)
   - [6.2 Patch Applicate](#62-patch-applicate)
7. [Strumenti di Sicurezza Automatici](#7-strumenti-di-sicurezza-automatici)
   - [7.1 GitGuardian](#71-gitguardian--secret-detection)
   - [7.2 Snyk](#72-snyk--software-composition-analysis-sca)
   - [7.3 Semgrep](#73-semgrep--sast-pattern-based)
   - [7.4 SonarCloud](#74-sonarcloud--sast--quality-gate--coverage)
   - [7.5 Trivy](#75-trivy--container-security-scanning)
8. [Test di Sicurezza](#8-test-di-sicurezza)
   - [8.1 Broken Access Control (RBAC)](#81-broken-access-control-rbac--a01)
   - [8.2 JWT & Token Management](#82-jwt--token-management--a07)
   - [8.3 Authentication & Rate Limiting](#83-authentication--rate-limiting--a07)
   - [8.4 Security Misconfiguration](#84-security-misconfiguration--a05)
   - [8.5 Input Validation & Injection](#85-input-validation--injection--a03)
   - [8.6 Cryptographic Failures](#86-cryptographic-failures--a02)
   - [8.7 Logging & Monitoring](#87-logging--monitoring--a09)
   - [Conformità OWASP Top 10 2021](#conformità-owasp-top-10-2021)
9. [Pipeline CI/CD](#9-pipeline-cicd)
   - [9.1 Workflow CI](#91-workflow-ci-githubworkflowsciyml)
   - [9.2 Workflow Deploy](#92-workflow-deploy-githubworkflowsdeployyml)
   - [9.3 Sicurezza della Pipeline](#93-sicurezza-della-pipeline)
   - [9.4 Segreti Pipeline](#94-segreti-pipeline)
10. [Kubernetes](#10-kubernetes)
    - [10.1 Architettura](#101-architettura)
    - [10.2 Setup microk8s](#102-setup-microk8s)
    - [10.3 Secrets](#103-secrets)
    - [10.4 Build e Push Immagini](#104-build-e-push-immagini)
    - [10.5 Manifest Kubernetes](#105-manifest-kubernetes)
    - [10.6 Aggiornamenti](#106-aggiornamenti)
    - [10.7 Comandi utili di diagnostica](#107-comandi-utili-di-diagnostica)
    - [10.8 Problemi Riscontrati e Soluzioni](#108-problemi-riscontrati-e-soluzioni)

---

## 1. Descrizione dell'Applicazione, Stack Tecnologico e System Design

### 1.1 Descrizione dell'Applicazione

**ExtendRent** è un sistema REST API per la gestione del noleggio di autoveicoli. La piattaforma copre l'intero ciclo di vita del noleggio, dalla registrazione del cliente alla restituzione del mezzo, con autenticazione JWT, tre ruoli applicativi e integrazione con Cloudinary (immagini) e SMTP (email OTP). Tutte le entità adottano *soft delete*: i dati non vengono mai cancellati fisicamente.

Funzionalità principali:

- **Autenticazione JWT**: Registrazione, login, refresh token, verifica account via OTP; token veicolati come cookie `HttpOnly; Secure; SameSite=Strict` (patch V-02)
- **RBAC a tre ruoli**: Admin, Employee, Customer con policy deny-by-default
- **Catalogo Veicoli**: Ricerca filtrata su 23 attributi (marca, colore, carburante, segmento, tipo patente)
- **Ciclo di Vita del Noleggio**: Creazione, avvio, restituzione, cancellazione con tracciamento chilometri
- **Pagamenti e Sconti**: Report ricavi, codici sconto con percentuale configurabile
- **Upload Immagini**: Su Cloudinary con whitelist del tipo file (patch V07)
- **API Documentation**: Swagger UI all'indirizzo `/swagger-ui/index.html`. Prima della patch V13 era accessibile a chiunque fosse loggato; dopo la patch richiede il ruolo ADMIN. Un Customer o un Employee che tenta di aprire quella pagina riceve 403.

All'avvio, `SeedDataConfig` popola automaticamente le tabelle di lookup: marche, modelli, colori, tipi carburante, cambi, carrozzerie, segmenti, stati veicolo, stati noleggio, tipi di pagamento, tipi di patente e utente admin di default.

### 1.2 Stack Tecnologico

| Tecnologia | Versione | Scopo |
|---|---|---|
| **Java** | 17 | Linguaggio principale |
| **Spring Boot** | 3.5.14 | Framework REST API (aggiornato da 3.5.13 a 3.5.14 per CVE Snyk) |
| **Spring Security** | 6.5.10 | Autenticazione JWT, RBAC, filter chain |
| **Spring Data JPA + Hibernate** | 3.5.11 / 6.6.49 | ORM e accesso database |
| **Spring Validation** | 3.5.14 | Bean validation Jakarta |
| **Spring Mail** | 3.5.14 | Invio email OTP e notifiche |
| **Spring Actuator** | 3.5.14 | `/actuator/health` per health check Docker |
| **PostgreSQL driver** | 42.7.11 | Connettività database (pinned per CVE fix) |
| **JWT (jjwt)** | 0.11.2 | Generazione e validazione token HS256 |
| **Bucket4j** | 8.0.1 | Rate limiting con algoritmo token-bucket (ogni IP dispone di un "secchio" di token che si ricarica a velocità fissa; ogni richiesta consuma un token; quando il secchio è vuoto si risponde 429) |
| **Caffeine** | 3.13.0 | Cache bounded per bucket per-IP (eviction 2 min, max 50k entry) |
| **Cloudinary SDK** | 1.27.0 | Storage immagini |
| **commons-text** | 1.10.0 | Escape/sanitize stringhe |
| **Lombok** | 1.18.30 | Riduzione boilerplate |
| **Swagger / OpenAPI** | 2.0.4 | Documentazione API (springdoc) |
| **progressbar** | 0.10.0 | Progress bar ASCII durante seed dati all'avvio |

> Le versioni dei moduli Spring (JPA, Validation, Mail, Actuator) sono gestite automaticamente dal POM padre `spring-boot-starter-parent 3.5.14`: dichiarare il parent è sufficiente perché Maven scelga versioni compatibili tra loro, senza doverle specificare una per una.

**Dipendenze di test:**

| Artifact | Scopo |
|----------|-------|
| `spring-boot-starter-test` | JUnit 5, Mockito, Spring Test, MockMvc |
| `spring-security-test` | `SecurityMockMvcRequestPostProcessors` - test con ruoli simulati |
| `h2` | Database in-memory per i web-layer test - nessun PostgreSQL richiesto in CI |
| `assertj-core` | Asserzioni fluent nei test di sicurezza |
| `mockito-core` | Mock degli oggetti nei test unitari |

### 1.3 System Design - Class Diagram

Tutti gli oggetti persistenti ereditano da `BaseEntity`, una *MappedSuperclass* che fornisce i campi comuni a tutte le entità: `id`, `isDeleted`, `deletedAt`, `lastModified` e `createdDate`, garantendo uniformità e supportando il meccanismo di *soft delete* adottato dall'intera applicazione.

#### Gerarchia degli Utenti

`UserEntity` è la classe base per tutti gli utenti del sistema e utilizza la strategia di ereditarietà JPA di tipo *JOINED*: ogni ruolo ha una propria tabella che estende quella comune. I campi condivisi includono nome, cognome, indirizzo email, numero di telefono, password, stato account (`DefaultUserStatus`) e ruolo (`UserRole`).

Le tre sottoclassi concrete modellano i ruoli applicativi:
- **`AdminEntity`** - amministratore del sistema; aggiunge `salary` e `adminType` per distinguere super-admin da admin ordinari.
- **`EmployeeEntity`** - dipendente operativo; aggiunge `salary` e `employeeType`.
- **`CustomerEntity`** - cliente finale; aggiunge `drivingLicenseNumber`, `customerType` e un riferimento a `DrivingLicenseTypeEntity`, che modella il tipo di patente posseduta e ne codifica il livello gerarchico (`licenseLevel`) per verificare la compatibilità con i veicoli noleggiabili.

![Class Diagram - Gerarchia degli Utenti](Images/cd_users.png)

#### Gerarchia dei Veicoli

`Vehicle` è una *MappedSuperclass* astratta che raggruppa le caratteristiche generali di qualsiasi mezzo noleggiabile: anno di immatricolazione, numero di posti, capacità bagagli, prezzo giornaliero e flag `isAvailable`. Contiene riferimenti alle entità di classificazione (`ColorEntity`, `FuelTypeEntity`, `ShiftTypeEntity`, `VehicleStatusEntity`) e al tipo di patente richiesta (`requiredLicenseType`), confrontato con quello del cliente in fase di prenotazione.

La classe concreta **`CarEntity`** estende `Vehicle` aggiungendo gli attributi specifici dell'automobile: targa (`licensePlate`), chilometraggio (`kilometer`), tipo di carrozzeria (`CarBodyTypeEntity`), segmento di mercato (`CarSegmentEntity`) e modello (`CarModelEntity`). Quest'ultimo è a sua volta collegato a `BrandEntity`, che rappresenta il costruttore del veicolo (es. Audi, BMW, Toyota) e ne conserva l'immagine su Cloudinary.

![Class Diagram - Gerarchia dei Veicoli](Images/cd_vehicles.png)

#### Dominio del Noleggio e dei Pagamenti

`RentalEntity` è il fulcro del sistema e rappresenta un contratto di noleggio nella sua interezza. Aggrega un riferimento al cliente (`CustomerEntity`) e al veicolo noleggiato (`CarEntity`), le date di inizio, fine e restituzione effettiva, i chilometri di partenza e arrivo per il calcolo del percorso, lo stato corrente del noleggio (`RentalStatusEntity`) e il flag `isActive` che indica se il noleggio è in corso.

Il pagamento è modellato da **`PaymentDetailsEntity`** (uno-a-uno per noleggio): registra l'importo totale e il tipo di pagamento tramite `PaymentTypeEntity` (es. carta di credito, contanti, bonifico), anch'essa configurabile con stato attivo/inattivo. Opzionalmente, `RentalEntity` può essere collegata a una **`DiscountEntity`** con codice sconto a percentuale configurabile e stato attivo/inattivo, permettendo al sistema di calcolare automaticamente l'importo scontato al momento della creazione del noleggio.

![Class Diagram - Dominio del Noleggio e dei Pagamenti](Images/cd_rental.png)

---

## 2. Frontend

Il frontend di **ExtendRent** è una Single Page Application (SPA) sviluppata con **React 18** e **TypeScript**, che si interfaccia con il backend Spring Boot tramite API REST. L'applicazione è containerizzata con Docker e servita da nginx.

### 2.1 Stack Tecnologico

| Categoria | Tecnologia | Versione |
|-----------|-----------|----------|
| **Framework UI** | React + TypeScript | 18.0.2 / 4.9.5 |
| **Routing** | React Router DOM | 6.21.1 |
| **State management** | Redux Toolkit | 2.0.1 |
| **HTTP client** | Axios (con interceptors) | 1.6.5 |
| **Form handling** | Formik + Yup | 2.4.5 / 1.3.3 |
| **UI libraries** | Material UI, Mantine, Bootstrap | 5.15 / 7.4 / 5.3 |
| **Tabelle** | material-react-table | 2.11.2 |
| **Date picker** | @mantine/dates, @mui/x-date-pickers | 7.4 / 6.19 |
| **Animazioni** | Framer Motion | 10.17 |
| **Token decoding** | jwt-decode | 4.0.0 |
| **Build** | Create React App + React Scripts | 5.0.1 |
| **Server** | nginx | 1.27-alpine |

### 2.2 Architettura e Struttura

Il progetto segue un'architettura a strati:

- **`pages/`** - 30+ pagine React suddivise per dominio funzionale. Includono pagine pubbliche (Homepage, Login, SignUp), pagine per utenti autenticati (Account, PastRentals) e pagine di amministrazione protette (`adminPanel/**`).
- **`components/`** - Componenti riutilizzabili: `Navbar`, `Search`, `Payment`, `CarCart`, `RentalDetail`, `OverlayLoader` (loading globale), `CreditCardForm`, `PasswordStrength`.
- **`store/`** - Memoria condivisa dell'applicazione, gestita con Redux che gestisce lo stato in maniera centralizzata per i componenti.
- **`services/`** - 20+ classi che raccolgono le chiamate al backend per ogni entità (auto, noleggi, clienti, marche, colori, ecc.). Ogni componente che ha bisogno di dati chiama il service corrispondente invece di costruire le richieste HTTP da solo.
- **`models/`** - Definizioni TypeScript dei dati scambiati con il backend. Servono al compilatore per segnalare errori se si usa un campo sbagliato.
- **`utils/`** - `axiosInterceptors.ts`: crea un'unica istanza Axios pre-configurata usata da tutti i service. Con `withCredentials: true` il browser include automaticamente i cookie di autenticazione in ogni chiamata. `useToken.ts`: funzione React che legge il JWT dal cookie e lo decodifica per estrarre le informazioni dell'utente corrente (ID, ruolo, email), usate dall'UI per decidere cosa mostrare.

```
rent-a-car-frontend-project/src/
|-- App.tsx               # Routing principale (React Router v6)
|-- index.tsx             # Entry point con Provider Redux
|-- pages/                # 30+ pagine per dominio
|   |-- Homepage/         # Pubblica: search + video background
|   |-- Login/ SignUp/    # Autenticazione
|   |-- AdminPanel/       # Dashboard admin (protetta)
|   |-- Cars/ Rental/     # CRUD auto e noleggi
|   |-- Customer/         # Gestione clienti
|   |-- PastRentals/      # Storico noleggi cliente
|   `-- [Brands, Colors, FuelType, Discount, Payment, ...]
|-- components/           # Componenti riutilizzabili
|-- store/                # Redux Toolkit (22 slice)
|-- services/             # Axios service classes (20+)
|-- models/               # TypeScript models (request/response)
|-- utils/                # axiosInterceptors, useToken
`-- data/config.json      # Base URL API
```

### 2.3 Autenticazione e Sicurezza

Il frontend implementa un meccanismo di autenticazione allineato con le patch di sicurezza applicate al backend.

**HttpOnly Cookie**

Un cookie `HttpOnly` è un cookie che il codice JavaScript della pagina non può leggere: solo il browser lo gestisce, inviandolo automaticamente al server su ogni richiesta verso lo stesso dominio. Questo protegge il token di autenticazione dagli attacchi XSS (se uno script malevolo venisse iniettato nella pagina, non potrebbe rubare il token perché non lo vede).

Prima della patch V02, il token JWT era salvato in `localStorage`, una zona di memoria a cui qualsiasi script in pagina ha accesso libero. Dopo la patch, il backend imposta il token come cookie `HttpOnly; Secure; SameSite=Strict` e il frontend smette di usare `localStorage`.

```ts
const axiosInstance = axios.create({
  baseURL: config.apiBaseUrl,
  withCredentials: true,  // dice al browser di allegare i cookie su ogni richiesta API
});
```

Il backend non include più il token JWT nel corpo della risposta al login (il campo `token` è vuoto); il token viaggia solo via cookie. Il frontend ha però bisogno di sapere chi è l'utente loggato (ID, ruolo, email) per decidere cosa mostrare nell'interfaccia. Per questo, `useToken.ts` legge e decodifica il JWT direttamente dal cookie senza mai esporlo.

**RBAC Lato Frontend**

Il controllo dei ruoli è implementato sia nella `Navbar` che nel componente `AdminRoutes`:

- **Navbar**: il link "Admin Panel" è visibile solo se `decodedToken.role` contiene `"ADMIN"`.
- **AdminRoutes**: componente di routing che blocca l'accesso alle route `/adminPanel/**` per utenti non ADMIN, reindirizzando alla homepage.

Il RBAC frontend è un livello di UX, non una misura di sicurezza primaria: il backend enforcement (via `SecurityConfig.hasRole("ADMIN")`) è la barriera reale contro accessi non autorizzati.

**Validazione Form**

Tutti i form usano **Formik** con schemi **Yup** (libreria JavaScript di validazione basata su schema: si definisce la forma attesa dei dati e Yup verifica ogni campo prima dell'invio) per la validazione client-side:
- Password: lunghezza, complessità, visualizzata con `PasswordStrength`.
- Targa veicolo: regex formato.
- Email: schema Yup `.email()`.
- Date noleggio: vincoli temporali (data fine dopo data inizio).
- Dati carta di credito: validazione numero e CVV.

### 2.4 Pagine e Route Principali

| Route | Accesso | Funzione |
|-------|---------|---------|
| `/` | Pubblico | Homepage con ricerca per date |
| `/login` | Pubblico | Form di autenticazione |
| `/signup` | Pubblico | Registrazione nuovi clienti |
| `/selectedCar` | Pubblico | Dettaglio auto + checkout |
| `/account` | Autenticato | Profilo personale |
| `/allMyRentals/:id` | Autenticato | Storico noleggi |
| `/adminPanel` | ADMIN | Dashboard |
| `/adminPanel/cars` | ADMIN | CRUD auto |
| `/adminPanel/rentals` | ADMIN | Gestione noleggi |
| `/adminPanel/customers` | ADMIN | Gestione clienti |
| `/adminPanel/employees` | ADMIN | Gestione dipendenti |
| `/adminPanel/discountCodes` | ADMIN | Gestione codici sconto |
| `/adminPanel/paymentDetails` | ADMIN | Dettagli pagamento |
| `/adminPanel/[brands\|colors\|...]` | ADMIN | CRUD dati tecnici auto |

### 2.5 Configurazione nginx

Il frontend è una **Single Page Application** React. Al build-time, Create React App compila tutto il codice TypeScript/JSX in bundle statici (HTML, CSS, JS). In produzione, nginx serve i bundle e fa da reverse proxy verso il backend:

```
Browser
  │
  ├── GET /dashboard    → nginx serve index.html → React Router gestisce la route
  └── POST /api/v1/...  → nginx proxy_pass → Spring Boot :8080 (rete Docker interna)
                                              (il browser non vede mai la porta 8080)
```

---

## 3. Architettura e Ruoli

### 3.1 Struttura Generale

Vista dall'esterno: come una richiesta del browser attraversa l'intero stack fino al database.

```
           Browser / Client
                │ :80 (unico punto di ingresso pubblico)
                ▼
           nginx:80 (Frontend SPA)
          ┌──────┴─────────────────────────────────┐
          │ / → asset React statici                 │ /api/ → reverse proxy
                                      Spring Boot :8080 (non esposto all'host)
                                      ├─ RateLimitFilter  ← Bucket4j, 10 req/min per IP
                                      ├─ JwtAuthFilter    ← legge cookie HttpOnly
                                      ├─ Controller Layer
                                      ├─ Service Layer
                                      └─ Repository Layer (23 repository JPA)
                                                   │
                                         PostgreSQL :5432 (non esposto all'host)
                                      Servizi esterni:
                                      ├─ Cloudinary  (immagini)
                                      └─ SMTP Gmail  (email OTP)
```

nginx è l'unico punto esposto all'host. Backend e database comunicano sulla rete bridge privata Docker `rentacar-net` e non sono mai raggiungibili dall'esterno. Questo elimina CORS (stessa origine) e nasconde la topologia interna.

### 3.2 Architettura a Layer del Backend

Vista interna: i livelli attraversati da una richiesta HTTP all'interno di Spring Boot.

```
Richiesta HTTP
      │
      ▼
┌─────────────────────────────────────┐
│       Filter Chain (Servlet)        │
│  RateLimitFilter → JwtAuthFilter   │
│  (rate limiting per IP → JWT check) │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│          Controller Layer           │
│  Riceve HTTP, deserializza JSON,    │
│  valida input (@Valid), delega      │
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│       Business Rules Layer          │
│  Validazioni di dominio: auto       │
│  disponibile, patente compatibile,  │
│  codice sconto valido, unicità email│
└──────────────────┬──────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│          Service Layer              │
│  Logica applicativa, orchestrazione │
│  tra repository e servizi esterni   │
└──────┬───────────────────┬──────────┘
       │                   │
       ▼                   ▼
┌─────────────┐    ┌──────────────────┐
│  Repository │    │ Servizi Esterni  │
│  Layer (JPA)│    │ Cloudinary, SMTP │
└──────┬──────┘    └──────────────────┘
       │
       ▼
  PostgreSQL 16
```

**Componenti di sicurezza e infrastruttura (`core/`):**

| Componente | File | Cosa fa |
|------------|------|---------|
| JWT Filter | `JwtAuthFilter.java` | Su ogni richiesta: estrae il token dal cookie, ne verifica firma e scadenza, imposta l'identità dell'utente per il resto della catena |
| Rate Limiter | `RateLimitFilter.java` | Conta le richieste per IP; blocca con HTTP 429 chi supera 10 req/min su `/api/**` |
| Security Config | `SecurityConfig.java` | Regola centrale che definisce quali endpoint sono pubblici e quali richiedono autenticazione o ruolo specifico (vedi matrice §3.4) |
| Exception Handler | `GlobalExceptionHandler.java` | Trasforma le eccezioni in risposte HTTP leggibili, senza mai includere stack trace o messaggi interni nella risposta al client |
| AOP Logging | `LoggingAspect.java` | Registra automaticamente le chiamate ai metodi del Service layer senza dover aggiungere codice di log in ogni metodo |
| Seed Data | `SeedDataConfig.java` | All'avvio del server, popola le tabelle di lookup con i dati iniziali (tipi patente, stati noleggio, admin di default) se il database è vuoto |

### 3.3 Ruoli

| Ruolo | Descrizione |
|-------|-------------|
| **Admin** | Gestione completa: utenti, veicoli, noleggi, sconti, pagamenti, report, immagini |
| **Employee** | Gestione operativa: avanzamento stato noleggi (via business logic) |
| **Customer** | Ricerca veicoli, prenotazione e gestione dei propri noleggi |

Employee e Customer **non si distinguono a livello Spring Security**: entrambi sono `authenticated()`. La distinzione è nel service layer.

### 3.4 Matrice di Accesso RBAC

Definita in `SecurityConfig.java`. Le regole sono valutate **top-to-bottom**: la prima regola che corrisponde alla richiesta vince. *Nella colonna ADMIN, la cella vuota indica che l'accesso è già garantito dalla regola Autenticato (qualsiasi utente loggato). Cella vuota in Autenticato = accesso negato.*

| Endpoint / Risorsa | Anonimo | Autenticato | ADMIN |
|--------------------|:-------:|:-----------:|:-----:|
| `POST /api/v1/auth/signup` | Sì | | Sì |
| `POST /api/v1/auth/signin` | Sì | | Sì |
| `POST /api/v1/auth/isUserTrue` | Sì | | Sì |
| `GET /api/v1/verify/**` | Sì | | Sì |
| `POST /api/v1/refresh-token/**` | Sì | | Sì |
| `GET /api/v1/drivingLicenseType/**` | Sì | | Sì |
| `GET /actuator/health` | Sì | | Sì |
| `POST /api/v1/auth/logout` | No | Sì | |
| `GET /api/v1/cars/**`, `/brands/**`, `/colors/**`, `/fuels/**`, `/gearshifts/**`, `/vehicle-statuses/**`, `/carBodyTypes/**`, `/carModels/**`, `/car-segments/**` | No | Sì | |
| `GET /api/v1/rentalStatuses/**` | No | Sì | |
| `POST/PUT/DELETE /api/v1/cars/**` (e tutti i catalogo) | No | | Sì |
| `POST/PUT/DELETE /api/v1/drivingLicenseType/**` | No | | Sì |
| `/api/v1/admins/**` | No | | Sì |
| `/api/v1/users/**` | No | | Sì |
| `/api/v1/employees/**` | No | | Sì |
| `/api/v1/rentals/**` | No | | Sì |
| `/api/v1/customers/**` | No | | Sì |
| `/api/v1/discounts/**` | No | | Sì |
| `/api/v1/paymentDetails/**` | No | | Sì |
| `/api/v1/paymentTypes/**` | No | | Sì |
| `/api/v1/images/**` | No | | Sì |
| `GET /swagger-ui/**`, `/v3/api-docs/**` | No | | Sì |
| Qualsiasi altra richiesta (`.anyRequest()`) | No | Sì | |

---

## 4. API Reference

La documentazione completa degli endpoint è disponibile tramite Swagger UI all'endpoint `/swagger-ui/index.html` (richiede ruolo ADMIN). Le sezioni seguenti descrivono i controller principali esposti dall'applicazione.

---

## 5. Containerizzazione

### 5.1 Dockerfile Backend ([`rentACar_backend/Dockerfile`](Dockerfile))

Il build Docker del backend usa due fasi separate: nella prima fase si usa un'immagine Java completa (con Maven e JDK) per compilare il codice e produrre il JAR. Nella seconda fase si usa un'immagine più leggera (solo JRE, senza compilatore) e si copia soltanto il JAR compilato. L'immagine finale che va in produzione non contiene Maven, il compilatore Java, i sorgenti né i file intermedi di compilazione.

**Scelte rilevanti per sicurezza e funzionamento:**

| Scelta | Perché |
|--------|--------|
| Multi-stage build | L'immagine finale è più piccola e ha meno componenti che Trivy potrebbe segnalare come vulnerabili |
| `eclipse-temurin:17-jre` come base runtime | JRE è il solo ambiente di esecuzione Java, senza il compilatore. Meno pacchetti OS installati rispetto a un'immagine JDK |
| Utente `appuser` UID 10001 | Il processo Java gira come utente non privilegiato. Se l'applicazione venisse compromessa, l'attaccante non avrebbe accesso root al server |
| Forma array in `ENTRYPOINT` | Il processo Java riceve il segnale di spegnimento (`SIGTERM`) direttamente, permettendo uno spegnimento ordinato invece di essere terminato forzatamente |

---

### 5.2 Dockerfile Frontend ([`rent-a-car-frontend-project/Dockerfile`](../rent-a-car-frontend-project/Dockerfile))

Anche il frontend usa due fasi: nella prima, Node.js compila il codice TypeScript/React in file statici HTML, CSS e JavaScript. Nella seconda, si usa un'immagine nginx minimale (circa 25 MB contro i 700 MB di Node) per servire quei file. L'immagine finale non contiene Node.js, npm né i sorgenti TypeScript.

**Scelte rilevanti:**

| Scelta | Perché |
|--------|--------|
| `nginx:1.27-alpine` come base | Immagine ~25 MB; superficie d'attacco molto ridotta rispetto a Node |
| `apk upgrade` nel Dockerfile | Aggiorna tutti i pacchetti Alpine all'ultima versione con fix disponibili al momento della build |
| nginx utente non-root | Il processo nginx gira senza privilegi di amministratore |
| Configurazione nginx custom | Necessaria per il routing React (ogni URL torna a `index.html`) e per il reverse proxy verso il backend (`/api/` → Spring Boot) |

---

### 5.3 Docker Compose ([`rentACar_backend/docker-compose.yml`](docker-compose.yml))

Il file avvia tre servizi in parallelo (`postgres`, `app` backend Spring Boot, `frontend` nginx) collegati tra loro su una rete interna Docker. Dall'esterno è raggiungibile solo la porta 8080 di nginx; il database e il backend non hanno porte esposte e non sono raggiungibili direttamente dall'esterno. I tre container si raggiungono tra loro usando i nomi come indirizzi (es. il backend si connette a `postgres:5432`).

```
Host (porta 8080)
      │
      ▼
 [frontend nginx:80]  ←──────────── rete rentacar-net ──────────────►  [postgres:5432]
      │ proxy /api/*                                                          ▲
      ▼                                                                       │
 [app Spring Boot:8080] ──────────────────────────────────────────────────────┘
```

**Meccanismo Docker Secrets:** ogni segreto è un file nella directory `secrets/` (gitignored). Docker monta questi file in `/run/secrets/<NOME>` all'interno del container. Spring Boot legge i valori tramite `spring.config.import=optional:configtree:/run/secrets/` in `application-docker.properties`. Le credenziali non appaiono mai in `docker inspect`, nei log del container né nelle variabili d'ambiente del processo.

```
./secrets/
├── DB_PASSWORD
├── JWT_SECRET
├── CLOUDINARY_CLOUD_NAME
├── CLOUDINARY_API_KEY
├── CLOUDINARY_API_SECRET
├── MAIL_USERNAME
└── MAIL_PASSWORD
```

---

## 6. Vulnerabilità Identificate tramite Fase di Testing

Nel corso del progetto sono state identificate e risolte 29 vulnerabilità, seguendo il framework OWASP Top 10 2021 e CWE come riferimento tecnico. Le vulnerabilità comprendono un flaw architetturale critico (`anyRequest().permitAll()` come regola di default), problemi sul backend (token management, CORS, rate limiting, upload file) e problemi sul frontend e nella logica di business (cookie, validazione pagamenti, lockout). **Tutte risolte.**

### 6.1 Vulnerabilità Identificate

| ID | Titolo | Gravità | OWASP | CWE | Test di riferimento | Stato |
|----|--------|---------|-------|-----|---------------------|:-----:|
| S1-1 | Global `permitAll()` - nessun RBAC | Critica | A01 | 284, 862 | `SecurityFilterChainTest` | Risolto |
| V01 | Password nei query parameter (`isUserTrue`, `updatePassword`) | Critica | A07 | 598 | `AuthenticationControllerSecurityTest` | Risolto |
| V02 | Escalation ruolo via signup pubblico | Critica | A01 | 269 | `RoleEscalationSecurityTest` | Risolto |
| V03 | Domini attaccanti esplicitamente in whitelist CORS | Alta | A05 | 942 | `CorsAttackerDomainSecurityTest` | Risolto |
| V04 | Credenziali hardcoded in `application.properties` | Alta | A02 | 798 | `HardcodedCredentialsSecurityTest` | Risolto |
| V05 | Refresh token loggato in chiaro | Alta | A09 | 532 | `RefreshTokenLoggingSecurityTest` | Risolto |
| V06 | Nessun rate limiting su endpoint di autenticazione | Alta | A07 | 307 | `RateLimitingSecurityTest` | Risolto |
| V07 | Nessuna validazione tipo file negli upload | Alta | A03 | 434 | `FileUploadSecurityTest`, `ImageMagicBytesSecurityTest` | Risolto |
| V08 | Exception handler espone `e.getMessage()` | Media | A05 | 209 | `GenericExceptionHandlerSecurityTest` | Risolto |
| V09 | TTL identico per access e refresh token (24h entrambi) | Media | A07 | 613 | `JwtTtlSecurityTest` | Risolto |
| V10 | Nessuna revoca server-side del refresh token | Media | A07 | 613 | `RefreshTokenRevocationSecurityTest` | Risolto |
| V11 | Password deboli per utenti di seed (`"pass"`, 4 caratteri) | Media | A07 | 521 | `PasswordComplexitySecurityTest` | Risolto |
| V12 | Configurazione CORS duplicata (due bean conflittuali) | Bassa | A05 | 16 | `CorsConfigDuplicationSecurityTest` | Risolto |
| V13 | Swagger UI nella whitelist pubblica | Bassa | A05 | 16 | `SwaggerAdminOnlySecurityTest` | Risolto |
| V14 | Header `Content-Security-Policy` mancante | Media | A05 | 693 | `CorsSecurityTest` | Risolto |
| V-01 | Frontend chiama ancora `GET isUserTrue` con credenziali nei params | Alta | A02 | 598 | `AuthenticationControllerSecurityTest` | Risolto |
| V-02 | Access token salvato in `localStorage` (leggibile da XSS) | Alta | A07 | 614 | `HttpOnlyCookieSecurityTest` | Risolto |
| V-03 | Rate limiter si fida di `X-Forwarded-For` (spoofable) | Alta | A07 | 307 | `RateLimitXForwardedForSecurityTest` | Risolto |
| V-04 | Nessuna revoca token al logout (endpoint logout assente) | Alta | A07 | 613 | `LogoutSecurityTest` | Risolto |
| V-05 | JWT claims espongono PII — dati personali identificabili (nome, cognome, telefono) | Media | A02 | 312 | `JwtClaimsPIISecurityTest` | Risolto |
| V-06 | Validation errors espongono nomi dei campi DTO | Media | A05 | 209 | `ValidationErrorExposureSecurityTest` | Risolto |
| V-07 | `console.log(response)` nell'interceptor Axios | Media | A09 | 532 | (frontend, nessun test backend) | Risolto |
| V-08 | Password: solo lunghezza verificata, nessuna complessità | Media | A07 | 521 | `PasswordComplexitySecurityTest` | Risolto |
| V-09 | `RateLimitFilter` usa `ConcurrentHashMap` senza eviction (OOM) | Media | A05 | 770 | `RateLimitingSecurityTest` | Risolto |
| V-10 | `checkCreditCardNumber()` era uno stub vuoto - qualsiasi numero accettato | Media | A03 | 20 | `PaymentValidationSecurityTest` | Risolto |
| V-11 | `checkCreditCardExpirationDate()` aveva logica invertita e non veniva chiamata | Media | A03 | 20 | `PaymentValidationSecurityTest` | Risolto |
| V-12 | Numero di telefono: regex accettava `0000000000` | Bassa | A03 | 20 | `PasswordComplexitySecurityTest` | Risolto |
| V-13 | Swagger accessibile a qualsiasi utente autenticato (non solo ADMIN) | Bassa | A05 | 284 | `SwaggerAdminOnlySecurityTest` | Risolto |
| V-14 | Nessun blocco account dopo N tentativi di login falliti | Bassa | A07 | 307 | `AccountLockoutSecurityTest` | Risolto |

> I codici CWE (Common Weakness Enumeration) identificano univocamente il tipo di debolezza software indipendentemente dal linguaggio o sistema. I codici OWASP A01–A09 sono le categorie dell'OWASP Top 10 2021, l'elenco delle vulnerabilità web più comuni secondo l'Open Worldwide Application Security Project.

### 6.2 Patch Applicate

| ID | Patch |
|----|-------|
| **S1-1** | `anyRequest().permitAll()` → policy deny-by-default con RBAC esplicito |
| **V01** | `GET /isUserTrue?password=...` → `POST` con body JSON; stesso fix su `updatePassword` |
| **V02** | `@AssertTrue isAuthorityCustomer()` in `SignUpRequest`: rifiuta `authority != CUSTOMER` |
| **V03** | Rimossi `evil-attacker.com` e `attacker.example.com` da `CorsConfig.ALLOWED_ORIGINS` |
| **V04** | `application.properties` in `.gitignore`; Docker usa Secrets (`/run/secrets/`) |
| **V05** | Rimosso `refreshTokenRequest.getToken()` dal `log.info()` in `RefreshTokenController` |
| **V06** | `RateLimitFilter` con Bucket4j: 10 req/min per IP, HTTP 429 + `Retry-After: 60` |
| **V07** | Whitelist `Content-Type` in `CarImageServiceImpl` / `UserImageServiceImpl`: non-immagini → 415 |
| **V08** | `e.getMessage()` → stringa generica statica; stack trace conservato solo nei log server-side |
| **V09** | Access token: 1h; refresh token: 7 giorni via property separata `refresh-expiration` |
| **V10** | Tabella `refresh_tokens` con SHA-256; rotazione obbligatoria; theft detection |
| **V11** | Password seed `"pass"` → `"Seed@1234"`; `@Pattern` per complessità minima |
| **V12** | Rimosso `addCorsMappings()` da `WebConfig`; unica fonte: `CorsConfig` |
| **V13** | Swagger spostato da whitelist pubblica a `.hasRole("ADMIN")` |
| **V14** | `Content-Security-Policy: default-src 'self'; frame-ancestors 'none'` in `SecurityConfig` |
| **V-01** | `signInService.ts`: da `axiosInstance.get()` con `{params}` a `axiosInstance.post()` con body |
| **V-02** | `signIn()` imposta cookie `HttpOnly; Secure; SameSite=Strict`; rimosso `localStorage.setItem`; frontend usa `withCredentials: true` |
| **V-03** | `RateLimitFilter.resolveClientIp()`: `X-Forwarded-For` trusted solo da IP proxy noti (loopback, RFC1918); IP pubblici → `getRemoteAddr()` diretto |
| **V-04** | `POST /api/v1/auth/logout` aggiunto: chiama `revokeAllForUser()` e scade i cookie con `maxAge=0` |
| **V-05** | `JwtService.generateToken()`: rimossi `firstname`, `lastname`, `phoneNumber` dal payload JWT |
| **V-06** | `CustomExceptionHandler`: flag `app.expose-validation-details=false` in produzione → risposta contiene solo `"Validation error"` |
| **V-07** | `axiosInterceptors.ts`: rimosso `console.log(response)` dall'interceptor |
| **V-08** | `SignUpRequest.password`: `@Size(min=8)` → `@Pattern` (maiuscola + minuscola + cifra + speciale) |
| **V-09** | `ConcurrentHashMap<String,Bucket>` → Caffeine `Cache` con `expireAfterAccess(2min)` e `maximumSize(50_000)` |
| **V-10** | `PaymentRules.checkCreditCardNumber()`: implementato algoritmo Luhn |
| **V-11** | `checkCreditCardExpirationDate()`: logica corretta (`isBefore` invece di `isAfter`) e agganciata a `checkCreditCard()` |
| **V-12** | Regex phone: `^[0-9]+$` → `^[1-9][0-9]{9}$` |
| **V-13** | Swagger paths: da `.authenticated()` a `.hasRole("ADMIN")` |
| **V-14** | `AccountLockoutService`: blocco account dopo 5 tentativi falliti con reset automatico dopo timeout |

---
## 7. Strumenti di Sicurezza Automatici

Il progetto integra cinque tool di analisi della sicurezza, ciascuno con uno scope diverso:

| Tool | Tipo analisi | Quando gira | Scope |
|------|-------------|-------------|-------|
| **GitGuardian** | Secret scanning | Push/PR (CI) | Working tree corrente |
| **Snyk** | SCA (dipendenze) | CI su ogni push | Librerie Maven (`pom.xml`) |
| **Semgrep** | SAST (codice sorgente) | CI su ogni push | Codice Java sorgente |
| **SonarCloud** | SAST + Quality Gate + Coverage | CI su ogni push | Java (backend) + TypeScript (frontend) |
| **Trivy** | Container scanning | Deploy (pubblicazione GitHub Release) | Immagine Docker completa (OS + JRE + JAR) |

---

### 7.1 GitGuardian - Secret Detection

GitGuardian scansiona il repository alla ricerca di credenziali hardcoded (API key, token, password) nel codice corrente e nella storia dei commit. Nel workflow CI il job usa `ggshield` in modalità path scan: blocca solo se trova segreti nel codice del branch corrente.

**Finding nel codice corrente:** nessuno. Il CI passa perché `application.properties` è in `.gitignore` e nessuna credenziale è presente nei file committati.

**Finding nella storia dei commit:** GitGuardian ha rilevato segreti in commit storici appartenenti al **fork originale** del progetto (team turco "tobeto", 2023-2024), prima che il nostro team "forkasse" il repository.

| Tipo | Valore (parziale) | Commit | Azione |
|------|-------------------|--------|--------|
| Cloudinary API key | `636629149633282` | `be2718a` → `082df8b` | Ignorati (fork originale) |
| Cloudinary API secret | `Hm05tc_JHU...` | `be2718a` → `082df8b` | Ignorati (fork originale) |
| JWT secret (Base64) | `evaVZ4gDLUSMdlY6...` | più commit | Ignorati (fork originale) |
| PostgreSQL password | `14531453`, `123asd123` | commit iniziali | Ignorati (fork originale) |
| AWS RDS endpoint | `tobeto-extendrent.cb48o06...` | commit iniziali | Ignorati (fork originale) |

**Perché ignorati e non FP:** questi segreti sono reali (non placeholder), ma appartengono all'account del team originale, non a questo deployment. Sono stati marcati come **"Ignored - Risk Accepted"** sul dashboard GitGuardian con la motivazione "credenziali del fork originale, non di questo team", e la riscrittura forzata avrebbe richiesto un force push e il re-clone da parte di tutti i collaboratori.

---

### 7.2 Snyk - Software Composition Analysis (SCA)

Snyk analizza le dipendenze Maven del backend (`snyk test --maven-projects`), confrontandole con il database di CVE. Nel branch analizzato erano presenti **12 alert** (5 High, 4 Medium, 3 Low). Tutti risolti.

**Analisi eseguita su:** branch `master-dev`, 2026-05-06, aggiornamento 2026-05-07.

Il fix principale è stato l'upgrade di Spring Boot da `3.5.13` a `3.5.14`: questa versione aggiorna in modo transitivo `spring-boot`, `spring-boot-autoconfigure`, `spring-web` e `spring-webmvc` alle versioni con le patch applicate, risolvendo 8 degli alert in un colpo solo. Gli altri 4 alert hanno richiesto interventi specifici descritti nella tabella.

| Alert | Pacchetto | Severità | CWE | Fix applicato |
|-------|-----------|----------|-----|---------------|
| #68 | `springfox-swagger2` | Medium | CWE-20 | Dipendenza rimossa, sostituita con springdoc |
| #69 | `springfox-swagger-ui` | Medium | CWE-79 (XSS) | Dipendenza rimossa (non mantenuta dal 2021) |
| #70 | `postgresql` JDBC | High | CWE-770 | Pin versione `42.7.11` + aggiunto `fetch_size=100` come difesa aggiuntiva |
| #71 | `spring-web` | High | CWE-459 | Upgrade Spring Boot 3.5.14 (WebFlux non usato nel progetto; impatto ridotto) |
| #72 | `spring-core` | Medium | CWE-770 | Upgrade Spring Boot 3.5.14 + aggiunto `StreamReadConstraints` in `JacksonConfig` |
| #73 | `spring-boot-devtools` | High | CWE-208 | Dipendenza rimossa (DevTools non appartiene a produzione) |
| #74 | `spring-boot` | High | CWE-338 | Upgrade Spring Boot 3.5.14 (il codice già usava `UUID.randomUUID()` basato su SecureRandom) |
| #75 | `spring-boot` | High | CWE-377 | Upgrade Spring Boot 3.5.14 + `/tmp` montato `noexec,nosuid` in docker-compose |
| #76 | `spring-boot` | Medium | CWE-61 | Upgrade Spring Boot 3.5.14 — non sfruttabile: richiede `ApplicationPidFileWriter`, non configurato nel progetto |
| #77 | `spring-webmvc` | Low | CWE-444 | Upgrade Spring Boot 3.5.14 — non sfruttabile: richiede resource chain caching abilitata, assente in `WebConfig.java` |
| #78 | `spring-boot-autoconfigure` | Low | CWE-297 | Upgrade Spring Boot 3.5.14 — non sfruttabile: richiede `spring-boot-starter-data-cassandra`, non presente nel progetto |
| #79 | `spring-boot-autoconfigure` | Low | CWE-297 | Upgrade Spring Boot 3.5.14 + aggiunto `checkserveridentity=true` per connessione SMTP |

**Modifiche al `pom.xml` per risolvere gli alert:**

| Proprietà | Versione | Motivo |
|-----------|----------|--------|
| `spring-boot-starter-parent` | 3.5.14 | Upgrade da 3.5.13: risolve gli alert #71–#79 per via transitiva |
| `tomcat.version` | 10.1.55 | Versione superiore al default incluso in Spring Boot 3.5.14, contenente fix di sicurezza aggiuntivi per Tomcat 10.1.x |
| `spring-security.version` | 6.5.10 | Versione superiore al default incluso in Spring Boot 3.5.14, contenente fix di sicurezza per Spring Security 6.x |
| `postgresql.version` | 42.7.11 | Risolve alert #70 (CWE-770, Resource Allocation Without Limits) |
| `logback.version` | 1.5.25 | Fix CVE Logback nella versione inclusa da Spring Boot |
| `commons-lang3.version` | 3.18.0 | Allineamento all'ultima versione stabile disponibile |

---

### 7.3 Semgrep - SAST Pattern-Based

Semgrep ha rilevato **4 finding** (tutti nello stesso file di regola: `java.lang.security.audit.active-debug-code-printstacktrace`, CWE-209 / CWE-489). Nessun falso positivo.

Il problema comune era l'uso di `e.printStackTrace()` al posto di un logger strutturato. `e.printStackTrace()` scrive l'intero stack trace su `System.err`, che in produzione viene catturato dal container Docker e finisce nei log del server. Uno stack trace espone nomi delle classi interne, numeri di riga, versioni delle librerie terze e sequenze di chiamate: tutte informazioni che abbassano il costo di un attacco successivo.

| Finding | File | Criticità aggiuntiva | Fix applicato |
|---------|------|---------------------|---------------|
| #80 | `ImageUtils.java:67` | `return null` silenzioso dopo l'eccezione causava NPE cascade nei caller | `log.error("Image decompression failed", e)` + `throw RuntimeException` |
| #81 | `BrandImageServiceImpl.java:40` | Solo `printStackTrace` | `log.error("Brand image upload failed for '{}'", brandName, e)` |
| #82 | `CarImageServiceImpl.java:71` | Solo `printStackTrace` | `log.error("Car image upload failed for '{}'", licensePlate, e)` |
| #83 | `UserImageServiceImpl.java:58` | Solo `printStackTrace` | `log.error("User image upload failed for '{}'", emailAddress, e)` |

Il finding #80 aveva un problema aggiuntivo rispetto agli altri: `decompressImage()` restituiva `null` silenziosamente in caso di errore. Tutti i caller usavano il risultato senza null-check, producendo una cascata di `NullPointerException` non gestite. L'NPE produceva un HTTP 500 con stack trace dettagliato nella risposta.

```java
// Prima (vulnerabile):
} catch (IOException | DataFormatException e) {
    e.printStackTrace();
    return null;
}

// Dopo (fix #80):
} catch (IOException | DataFormatException e) {
    log.error("Image decompression failed", e);
    throw new RuntimeException("Image decompression failed", e);
}
```

Tutti e quattro i finding sono stati risolti. Nessun finding di Semgrep è stato classificato come FP.

---

### 7.4 SonarCloud - SAST + Quality Gate + Coverage

SonarCloud analizza il codice sorgente con analisi statica dataflow, calcola la copertura del codice tramite i report JaCoCo prodotti nel job `security-tests`, e applica un quality gate prima del merge. Due istanze separate: una per il backend Java, una per il frontend TypeScript.

**Quality Gate configurato (backend):**
- Line coverage ≥ 60%
- Branch coverage ≥ 50%
- Class coverage ≥ 70%
- Package `src/core/security`: class coverage 100%, line coverage 80%

**Integrazione con la pipeline:** il job `security-tests` esegue i 403 test di sicurezza e produce un report Surefire XML. Il job `sonarcloud` legge questo report per associare la copertura alle singole classi di test. Se `security-tests` fallisce, il job `sonarcloud` non parte (dipendenza esplicita nel workflow).

Non vengono riportati finding specifici di SonarCloud perché i finding di analisi statica sovrapposti con Semgrep (SQL injection, hardcoded credentials, weak crypto) erano già stati risolti prima che la pipeline SonarCloud fosse configurata sul branch.

---

### 7.5 Trivy - Container Security Scanning

Trivy scansiona l'immagine Docker del backend alla ricerca di CVE nei pacchetti OS (Debian/Alpine) e nelle dipendenze applicative. Il job `publish` in `deploy.yml` esegue tre step sequenziali:

1. **SARIF completo**: scansiona tutte le severità (`exit-code: 0`) e carica i risultati su GitHub → Security → Code scanning. SARIF (Static Analysis Results Interchange Format) è il formato JSON standard usato dagli strumenti di analisi statica per riportare i finding; GitHub lo importa nel tab Security.
2. **Upload SARIF**: `github/codeql-action/upload-sarif` rende i risultati navigabili nella Security tab.
3. **Gate CRITICAL/HIGH**: scansione con `exit-code: 1`, `ignore-unfixed: true`, `severity: CRITICAL,HIGH`; blocca il deploy se trova CVE gravi con fix disponibile.

Il flag `--ignore-unfixed` è una scelta deliberata: CVE senza fix upstream non possono essere risolti aggiornando le dipendenze, quindi bloccare il deploy su di essi sarebbe un gate permanentemente rosso indipendentemente dalle azioni del team.

**Riduzione della superficie Trivy ottenuta con scelte Dockerfile:**
- Backend: `eclipse-temurin:17-jre` invece del JDK riduce il numero di pacchetti OS inclusi
- Frontend: `nginx:1.27-alpine` + `apk upgrade` aggiorna tutti i pacchetti Alpine alla versione con fix disponibile al momento della build

**CVE in immagine base - decisione VEX:**

Nel corso del progetto sono state rilevate CVE in `stdlib@1.26.2` e `golang.org/x/net@0.40.0`, pacchetti Go bundled nel binario `pebble` (process manager) dell'immagine base `eclipse-temurin:17-jre`. Queste CVE non appartengono al codice Java del progetto.

**Perché Ubuntu 26.04 e non 24.04:**

`eclipse-temurin:17-jre` usa Ubuntu 26.04 come base OS; non è una scelta del progetto, ma di Eclipse Temurin. Ubuntu 26.04 include pacchetti di sistema più recenti rispetto a 24.04, alcuni dei quali (tool di sistema moderni) sono scritti in Go e distribuiti come binari precompilati dentro pacchetti `.deb`. Quando quei pacchetti vengono installati nell'immagine, il binario Go compilato finisce nell'immagine stessa. Trivy legge la versione Go con cui quel binario è stato compilato (`1.26.2`) e segnala le CVE note per quella versione. Sia la scelta di Ubuntu 26.04 che la versione Go usata per compilare quei binari dipendono da Eclipse Temurin e da Canonical, non dal progetto.

L'approccio VEX (Vulnerability Exploitability eXchange) per sopprimere queste CVE è stato valutato e scartato per tre motivi:
- **PURL instabile:** ogni build produce un nuovo digest dell'immagine, senza un PURL stabile da inserire nel documento VEX
- **Soppressione troppo ampia:** referenziare `stdlib` o `net` come prodotto sopprimerebbe quelle versioni ovunque nell'immagine, non solo dentro `pebble`
- **Manutenzione insostenibile:** ogni aggiornamento della base image richiederebbe aggiornamento manuale del VEX

**Soluzione adottata:** `--ignore-unfixed` nel gate. Le CVE in `pebble` non hanno fix disponibile nell'immagine base, quindi il flag le esclude in modo semanticamente corretto. Quando `eclipse-temurin:17-jre` aggiornerà `pebble` con versioni Go patchate, le CVE spariranno automaticamente dal report.

---

## 8. Test di Sicurezza

La strategia di testing adottata è **incentrata sulla sicurezza**, non sulla correttezza funzionale: il 95% della suite (403 test su 424) verifica il comportamento del sistema in scenari di attacco o accesso non autorizzato, seguendo la tassonomia **OWASP Top 10 2021**. La suite è completamente indipendente dall'ambiente di produzione: usa H2 in-memory per il web layer, non effettua chiamate a Cloudinary o SMTP, e il rate limiting è disabilitabile via `app.rate-limit.enabled=false` nel profilo `test`. Ogni test è quindi riproducibile senza configurazione esterna.

I test sono organizzati in **43 classi** nel package `com.extendrent.security`, strutturate per area OWASP. I 403 test di sicurezza includono 355 metodi `@Test` e 13 metodi `@ParameterizedTest` che generano **48 istanze** aggiuntive: un'unica definizione di test esegue automaticamente l'intera lista di payload malevoli (SQL injection, XSS, path traversal, password deboli, numeri di carta non validi). Ai test di sicurezza si aggiungono 21 test funzionali su controller specifici (`DiscountControllerTest`, `PaymentDetailControllerTest`, `PaymentTypeControllerTest`, `RentalControllerTest`, `ApplicationTests`), per un totale di **424 test**.

Distribuzione per area OWASP:

| Area | OWASP | Classi | Vulnerabilità coperte |
|------|:-----:|:------:|----------------------|
| Broken Access Control (RBAC) | A01 | 15 | S1-1, V02, endpoint controller completi |
| JWT & Token Management | A07 | 5 | V05, V09, V10, V-02, V-04, V-05 |
| Authentication & Rate Limiting | A07 | 4 | V06, V11, V-03, V-08, V-14 |
| Security Misconfiguration | A05 | 6 | V03, V12, V13, V14, V-06, V-13 |
| Input Validation & Injection | A03 | 4 | V07, V-10, V-11, V-12, SQL injection, XSS |
| Cryptographic Failures | A02 | 1 | V04 |
| Logging & Monitoring | A09 | 2 | V05, V08 |
| **TOTALE test di sicurezza** | - | **43** + [`SecurityTestSupport`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SecurityTestSupport.java) | **403/403** |

```bash
# Suite completa (424 test)
mvn test

# Solo test di sicurezza (profilo Maven dedicato)
mvn test -P security-tests

# Alternativa con filtro package
mvn test -Dtest="com/extendrent/security/**"

# Copertura JaCoCo → target/site/jacoco/index.html
mvn verify
```

In CI i job `security-tests`, `snyk` e `semgrep` girano **in parallelo** su GitHub Actions dopo il gate `compile`.

---

### 8.1 Broken Access Control (RBAC) - A01

Broken Access Control è la vulnerabilità al primo posto nell'OWASP Top 10 2021. In ExtendRent il rischio è duplice: endpoint che rispondono a ruoli non autorizzati (escalation verticale) e utenti che accedono a risorse altrui (IDOR — Insecure Direct Object Reference: un utente modifica l'ID nella richiesta per accedere a dati di un altro utente, senza che il backend verifichi l'appartenenza). Tutta la filter chain di Spring Security — configurata in `SecurityConfig` con policy deny-by-default (qualsiasi endpoint non esplicitamente aperto è bloccato per default) — viene verificata endpoint per endpoint, ruolo per ruolo, con MockMvc (il framework di test Spring che simula richieste HTTP complete senza avviare un server reale, tramite `SecurityMockMvcRequestPostProcessors.jwt()` per le richieste autenticate). Ogni controller ha una classe di test dedicata che copre sistematicamente tutti i metodi HTTP esposti.

[**`SecurityFilterChainTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SecurityFilterChainTest.java)

> Tutti gli endpoint del sistema

Usa MockMvc per colpire ogni endpoint del sistema con ciascun ruolo (Admin, Employee, Customer) e senza autenticazione. Verifica che gli endpoint pubblici (`/auth/**`, `/swagger-ui/**` solo per Admin) rispondano correttamente e che tutti gli altri endpoint restituiscano 401 o 403 per accessi non autorizzati. È il test più ampio: copre l'intera filter chain configurata in `SecurityConfig`.

---

[**`RoleEscalationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RoleEscalationSecurityTest.java)

> `POST /auth/register`

Il test invia una richiesta di registrazione con il campo `authority: ADMIN` nel body, un valore che un utente può inserire manualmente modificando la richiesta. Il ruolo viene determinato lato server, indipendentemente da ciò che il client invia.

| Scenario | Comportamento |
|----------|--------------|
| CUSTOMER con `authority=ADMIN` in payload | 403 - escalation bloccata |

**Outcome:** S1-1/V-02 - policy deny-by-default con RBAC esplicito; un CUSTOMER non può auto-assegnarsi il ruolo ADMIN.

---

[**`AdminControllerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/AdminControllerSecurityTest.java) · [**`EmployeeControllerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/EmployeeControllerSecurityTest.java)

> Endpoint amministrativi e dipendenti

Verifica che tutti gli endpoint degli amministratori e dei dipendenti siano accessibili esclusivamente dal ruolo Admin. Richieste non autenticate ricevono 401 (token assente), ruoli Customer ed Employee ricevono 403 (autenticati ma non autorizzati).

| Unauthenticated | Customer | Employee | Admin |
|:-:|:-:|:-:|:-:|
| 401 | 403 | 403 | Sì |

---

[**`RentalControllerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RentalControllerSecurityTest.java)

> Endpoint noleggi

Tutti gli endpoint CRUD del ciclo noleggio sono riservati ad Admin. Customer ed Employee non possono accedere né in lettura né in scrittura; il noleggio è gestito solo lato backoffice.

| Unauthenticated | Customer | Employee | Admin |
|:-:|:-:|:-:|:-:|
| 401 | 403 | 403 | Sì |

---

[**`UserControllerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/UserControllerSecurityTest.java)

> `PUT /api/v1/user/{id}/password`

Verifica la protezione contro IDOR (Insecure Direct Object Reference): ogni utente può modificare solo la propria password. Il controllo confronta l'ID nel path parameter con quello estratto dal JWT nel `SecurityContext`; se non coincidono, la richiesta viene rifiutata con 403.

| Scenario | Comportamento |
|----------|--------------|
| Utente aggiorna propria password | Sì |
| Utente aggiorna password di altro utente (IDOR) | 403 |

### Miglioramenti Implementati

| Area | Prima | Dopo | Impact |
|------|-------|------|--------|
| **RBAC** | `anyRequest().permitAll()` | Policy deny-by-default con ruoli espliciti | Authorization enforcement |
| **Role Escalation** | Nessun controllo su `authority` | `@AssertTrue isAuthorityCustomer()` in `SignUpRequest` | Privilege escalation prevention |
| **CORS origins** | Includeva `evil-attacker.com`, `attacker.example.com` | Solo origini legittime in `CorsConfig` | Attacker domain blocking |
| **Swagger** | Accessibile a tutti gli autenticati | `.hasRole("ADMIN")` | Admin-only API docs |
| **CSP** | Assente | `Content-Security-Policy: default-src 'self'; frame-ancestors 'none'` | Clickjacking & XSS mitigation |

---

### 8.2 JWT & Token Management - A07

La gestione dei token è il cuore dell'autenticazione di ExtendRent. Dopo la rimozione di AWS Cognito, i JWT sono emessi e validati interamente dal server tramite HMAC-SHA256. I test coprono il ciclo di vita completo del token: generazione sicura, resistenza agli attacchi noti sulla firma (alg:none, key confusion), TTL separati per access e refresh, assenza di PII nel payload, revoca al logout e protezione del refresh token da furto e da logging accidentale.

[**`JwtServiceTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtServiceTest.java)

Testa il servizio JWT in isolamento (senza Spring context): generazione con HMAC-SHA256, parsing e validazione standard, e resistenza a due attacchi noti. L'attacco `alg:none` bypassa la firma impostando l'header JWT a `"none"`; il sistema deve rifiutarlo. Il key confusion attack usa la chiave pubblica come segreto HMAC per ingannare il server che si aspetta HMAC-SHA256.

| Scenario | Comportamento |
|----------|--------------|
| Generazione e validazione standard | Sì |
| Attacco `alg:none` | rifiutato |
| Key confusion attack | rifiutato |

---

[**`JwtTtlSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtTtlSecurityTest.java)

> Token access e refresh

TTL separati per i due tipi di token: il token di accesso ha una finestra breve (1h) per limitare l'esposizione in caso di furto; il refresh token dura 7 giorni per non costringere l'utente a rifare il login ogni ora. Il test verifica che entrambi i TTL siano rispettati e che un token scaduto restituisca sempre 401.

| Scenario | Comportamento |
|----------|--------------|
| Access token scaduto | 401 |
| Refresh token scaduto | 401 |
| TTL access token (1h) | Sì |
| TTL refresh token (7 giorni) | Sì |

**Outcome:** V09 - TTL separati via property `refresh-expiration`.

---

[**`JwtClaimsPIISecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtClaimsPIISecurityTest.java)

> Payload JWT

Decodifica il payload JWT emesso al login e verifica che non contenga dati personali sensibili. I claim PII erano stati aggiunti originariamente per evitare query aggiuntive al database; rimossi perché il JWT viaggia nel cookie e può essere intercettato o loggato, e i dati personali non devono essere leggibili al di fuori del database.

| Claim | Status |
|-------|--------|
| `sub` (user ID) | presente |
| `firstname`, `lastname`, `phoneNumber` | rimossi |

**Outcome:** V05 - nessun dato personale (PII) nel payload JWT.

---

[**`LogoutSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/LogoutSecurityTest.java)

> `POST /api/v1/auth/logout`

Simula un flusso di logout completo: autentica un utente, usa il token per una richiesta protetta (atteso 200), chiama `/auth/logout`, poi ritenta la stessa richiesta con il token appena revocato, che deve rispondere 401.

| Scenario | Comportamento |
|----------|--------------|
| Token valido prima del logout | accettato |
| Stesso token dopo logout | 401 - revocato |

**Outcome:** V-04 - logout revoca il token tramite `revokeAllForUser()` e scade i cookie con `maxAge=0`.

---

[**`RefreshTokenRevocationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenRevocationSecurityTest.java)

> `POST /api/v1/auth/logout`

Verifica che al logout il refresh token venga effettivamente invalidato nella tabella `refresh_tokens` (hashing SHA-256) e che un successivo tentativo di rinnovo con lo stesso token venga rifiutato. La rotazione obbligatoria e la theft detection (V10) fanno sì che ogni token sia usabile una sola volta; il riuso rivela un possibile furto.

---

[**`RefreshTokenLoggingSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenLoggingSecurityTest.java)

Verifica che il valore del refresh token non appaia mai in nessuna riga di log. Prima della patch V05, `RefreshTokenController` stampava `log.info("...", refreshTokenRequest.getToken())`; il token avrebbe potuto finire nei log aggregati accessibili a operatori. Il test controlla che nessun pattern riconducibile al token sia presente nell'output del logger.

**Outcome:** CWE-532 - il refresh token non appare mai nei log.

---

[**`JwtAuthFilterTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtAuthFilterTest.java)

> Tutti gli endpoint protetti

`JwtAuthFilter` si interpone prima di ogni controller: estrae il token dal cookie `HttpOnly`, ne valida la firma HMAC-SHA256 e la scadenza, e popola il `SecurityContext`. Il test colpisce un endpoint protetto con le tre condizioni di errore più comuni per verificare che il filtro risponda sempre con 401 senza sollevare eccezioni non gestite.

| Scenario | Comportamento |
|----------|--------------|
| Token mancante | 401 |
| Token malformato | 401 |
| Token scaduto | 401 |

### Miglioramenti Implementati

| Area | Prima | Dopo | Impact |
|------|-------|------|--------|
| **Token TTL** | Non differenziato | Access 1h · Refresh 7 giorni via `refresh-expiration` | Session expiry enforcement |
| **PII in JWT** | `firstname`, `lastname`, `phoneNumber` nel payload | Rimossi - solo claims necessari | Privacy by design |
| **Logout** | Assente | `POST /auth/logout` revoca token + scade cookie | Token invalidation |
| **Refresh Token** | Nessun hashing | SHA-256 in DB · rotazione obbligatoria · theft detection | Credential theft prevention |
| **Token nei log** | `refreshToken` in `log.info()` | Rimosso da tutti i log | CWE-532 compliance |

---

### 8.3 Authentication & Rate Limiting - A07

Il rate limiting è la prima linea di difesa contro gli attacchi brute-force sugli endpoint di autenticazione. Senza un limite, un attaccante può tentare migliaia di combinazioni al secondo fino a compromettere un account. ExtendRent implementa un token-bucket per IP con Caffeine come backing cache, affiancato da un lockout esplicito dopo cinque tentativi falliti consecutivi. I test verificano entrambi i meccanismi, in isolamento e in integrazione con il filtro HTTP, e la corretta risoluzione dell'IP client dietro reverse proxy, impedendo che l'header `X-Forwarded-For` venga falsificato per aggirare il rate limiter.

[**`RateLimitingSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitingSecurityTest.java) · [**`RateLimitingBehaviorTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitingBehaviorTest.java)

> `POST /auth/**`

Il test simula un attacco brute-force: invia 10 richieste in rapida successione verificando che tutte abbiano successo, poi invia l'undicesima, che deve ricevere 429 con `Retry-After: 60`. `RateLimitingBehaviorTest` verifica la logica del token-bucket in isolamento; `RateLimitingSecurityTest` la integrazione con il filtro HTTP.

| Scenario | Comportamento |
|----------|--------------|
| Richieste entro soglia (≤10/min per IP) | 200 |
| Richieste oltre soglia | 429 + `Retry-After: 60` |

**Outcome:** V06/V-09 - token-bucket rate limiter su `/auth/**`; `ConcurrentHashMap` → Caffeine `Cache` con `expireAfterAccess(2min)` e `maximumSize(50_000)`.

---

[**`RateLimitXForwardedForSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitXForwardedForSecurityTest.java)

> Header `X-Forwarded-For`

Verifica che il rate limiter identifichi correttamente l'IP del client anche dietro un reverse proxy. Se la richiesta arriva da un IP noto (loopback o RFC1918), il rate limiter legge l'IP reale da `X-Forwarded-For`; altrimenti usa `getRemoteAddr()`, impedendo a un client diretto di falsificare l'header per aggirare il rate limiting.

| Scenario | Comportamento |
|----------|--------------|
| IP da proxy noto (loopback/RFC1918) | Letto da `X-Forwarded-For` |
| IP da fonte non trusted | Usato `getRemoteAddr()` diretto |

**Outcome:** V-03 - no IP spoofing tramite header proxy non trusted.

---

[**`AuthenticationControllerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/AuthenticationControllerSecurityTest.java)

> `POST /auth/signin` · `POST /auth/register`

Copre scenari trasversali al controller di autenticazione: verifica che la password non possa essere passata come query parameter (prevenzione credential leakage negli access log del server), che il signup rifiuti payload malformati con 400, e che il signin risponda 401 a credenziali errate. Tre path sconosciuti verificano la policy `anyRequest().denyAll()`.

| Scenario | Comportamento |
|----------|--------------|
| Password in query string | - V-01: solo in body |
| Signup con dati non validi | 400 |
| Signin con credenziali errate | 401 |
| Path sconosciuti (`anyRequest`) - 3 istanze | 401/403 |

**Outcome:** V-01, V-08, V-12 - validazione signup/signin; password mai in URL.

---

[**`AccountLockoutSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/AccountLockoutSecurityTest.java)

> `POST /auth/signin`

Il test registra un utente, simula cinque tentativi di login con password errata e verifica che il sesto, anche con la password corretta, sia rifiutato. Verifica inoltre che il lockout abbia una scadenza temporale e che l'account si sblocchi automaticamente dopo il TTL configurato in `AccountLockoutService`.

| Scenario | Comportamento |
|----------|--------------|
| 5 tentativi falliti consecutivi | Account bloccato |
| Login con account bloccato (password corretta) | rifiutato |
| Scadenza lockout | Sblocco automatico |

**Outcome:** V-14 - `AccountLockoutService` blocca dopo 5 tentativi con reset automatico.

### Miglioramenti Implementati

| Area | Prima | Dopo | Impact |
|------|-------|------|--------|
| **Rate Limiting** | Assente | Bucket4j 10 req/min per IP · HTTP 429 + `Retry-After: 60` | Brute force prevention |
| **Rate Limiter Cache** | `ConcurrentHashMap` non bounded | Caffeine `expireAfterAccess(2min)` · max 50 K | Memory exhaustion prevention |
| **IP Resolution** | Nessuna | `X-Forwarded-For` trusted solo da proxy noti (loopback, RFC1918) | IP spoofing prevention |
| **Password in URL** | `GET /isUserTrue?password=...` | `POST` con body JSON | Credential leakage prevention |
| **Account Lockout** | Assente | 5 tentativi → lockout + reset automatico | Credential brute force prevention |

---

### 8.4 Security Misconfiguration - A05

Le misconfigurazioni di sicurezza non derivano da errori logici nel codice, ma da impostazioni errate del framework o dell'infrastruttura. Nell'audit di ExtendRent sono emersi quattro problemi distinti: due configurazioni CORS in conflitto (con domini attaccante nella whitelist), header di sicurezza HTTP assenti, Swagger accessibile a utenti non Admin, e messaggi di errore che esponevano stack trace e dettagli interni. I test in questa categoria verificano sistematicamente ogni aspetto della configurazione del `SecurityFilterChain`, dei cookie e del comportamento dell'exception handler in risposta a input anomali.

[**`CorsSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsSecurityTest.java) · [**`CorsAttackerDomainSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsAttackerDomainSecurityTest.java)

`CorsSecurityTest` invia richieste preflight dalle origini whitelistate e verifica che `Access-Control-Allow-Origin` sia presente. `CorsAttackerDomainSecurityTest` usa `evil-attacker.com` e `attacker.example.com` (domini inclusi per errore nella configurazione originale) e verifica che vengano rifiutati con header CORS assente.

| Origine | Comportamento |
|---------|--------------|
| Whitelistata (origini legittime) | Sì |
| `evil-attacker.com` / `attacker.example.com` | rifiutata |

---

[**`CorsConfigDuplicationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsConfigDuplicationSecurityTest.java)

Prima della patch V12, `WebConfig.addCorsMappings()` e `CorsConfig` coesistevano come due bean CORS distinti con regole potenzialmente conflittuali. Il test verifica che il bean `CorsFilter` sia registrato una sola volta e che non esista alcun `WebMvcConfigurer` che configuri regole CORS parallele; due sorgenti CORS portano a comportamenti imprevedibili a seconda dell'ordine di applicazione dei filtri.

---

[**`CorsSecurityTest$MissingSecurityHeaders`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsSecurityTest.java)

Classe inner di `CorsSecurityTest`. Verifica che ogni risposta HTTP contenga gli header di sicurezza fondamentali: `Content-Security-Policy` per limitare le sorgenti dei contenuti e prevenire XSS, `X-Frame-Options` per bloccare il clickjacking, e gli altri header configurati dal `SecurityFilterChain`.

| Header | Status |
|--------|--------|
| `Content-Security-Policy: default-src 'self'; frame-ancestors 'none'` | Sì |
| `X-Frame-Options` | Sì |
| Altri security headers | Sì |

---

[**`SwaggerAdminOnlySecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SwaggerAdminOnlySecurityTest.java)

> `/v3/api-docs` · `/swagger-ui/**`

Prima della patch, Swagger era accessibile a qualsiasi utente autenticato: un Customer poteva esplorare l'intera API, incluse le route Admin. La patch V13 ha ristretto l'accesso al solo ruolo `ADMIN`. Il test colpisce entrambi gli endpoint con tutti e quattro i livelli di accesso.

| Unauthenticated | Customer | Employee | Admin |
|:-:|:-:|:-:|:-:|
| 403 | 403 | 403 | Sì |

**Outcome:** V13/V-13 - Swagger spostato a `.hasRole("ADMIN")`.

---

[**`SwaggerProdAccessSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SwaggerProdAccessSecurityTest.java)

Con il profilo Spring `prod` attivo, verifica che `/swagger-ui/**` e `/v3/api-docs/**` siano completamente inaccessibili - restituiscono 404 o vengono esclusi dalla filter chain. Swagger non deve essere esposto in produzione nemmeno al ruolo Admin: l'endpoint documenta l'intera API e ne facilita l'esplorazione da parte di un attaccante.

---

[**`GenericExceptionHandlerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/GenericExceptionHandlerSecurityTest.java)

Provoca intenzionalmente un errore interno (input malformato che raggiunge il servizio) e verifica che la risposta HTTP non contenga stack trace, nomi di classi interne, numeri di riga o l'output di `e.getMessage()`. Prima della patch, il `CustomExceptionHandler` restituiva il messaggio raw dell'eccezione, informazioni preziose per un attaccante nel pianificare exploit successivi.

**Outcome:** V-08 - HTTP 500 non espone stack trace né dettagli interni; `e.getMessage()` sostituito con stringa generica statica.

---

[**`ValidationErrorExposureSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/ValidationErrorExposureSecurityTest.java)

Con profilo `prod` e flag `app.expose-validation-details=false`, verifica che gli errori di validazione bean restituiscano solo `"Validation error"` senza esporre i nomi dei campi DTO, i valori rifiutati o i messaggi del validator. Il comportamento è diverso in `test`/`dev` dove i dettagli sono visibili per facilitare il debugging.

**Outcome:** V-06 - `CustomExceptionHandler` con flag `app.expose-validation-details=false` in produzione → risposta contiene solo `"Validation error"`.

---

[**`HttpOnlyCookieSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/HttpOnlyCookieSecurityTest.java)

Verifica i flag del cookie di sessione impostato da `signIn()`: `HttpOnly` impedisce l'accesso da JavaScript (XSS non può estrarre il token), `Secure` limita la trasmissione a HTTPS, `SameSite=Strict` blocca le richieste cross-origin automatiche (CSRF). Prima della patch V-02, il token era salvato in `localStorage`, accessibile a qualsiasi script in pagina.

**Outcome:** V-02 - cookie `HttpOnly; Secure; SameSite=Strict`; rimosso `localStorage.setItem` dal frontend.

### Miglioramenti Implementati

| Area | Prima | Dopo | Impact |
|------|-------|------|--------|
| **CORS Sources** | Due fonti (`CorsConfig` + `WebConfig.addCorsMappings()`) | Unica fonte `CorsConfig` | Configuration consistency |
| **Error Messages** | `e.getMessage()` in risposta HTTP | Stringa generica statica; stack trace solo nei log | Information disclosure prevention |
| **Validation Details** | Sempre visibili | `expose-validation-details=false` in `prod` | Stack trace hiding |
| **Cookie Storage** | Token in `localStorage` | Cookie `HttpOnly; Secure; SameSite=Strict` | XSS token theft prevention |
| **Swagger Access** | Qualsiasi autenticato | Solo `.hasRole("ADMIN")` · inaccessibile in `prod` | API exposure control |

---

### 8.5 Input Validation & Injection - A03

La validazione dell'input è il principale meccanismo di difesa contro SQL injection, XSS e path traversal. In ExtendRent tutti i campi passano attraverso la Bean Validation di Jakarta (`@Valid`, `@Pattern`, `@Size`) prima di raggiungere il layer di servizio. I test usano `@ParameterizedTest` per eseguire automaticamente 19 payload malevoli noti attraverso tutti gli endpoint, garantendo che nessun input non sanitizzato raggiunga il database o venga riflesso nella risposta. La validazione del pagamento e la complessità della password completano la copertura della superficie di input esposta.

[**`InputValidationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/InputValidationSecurityTest.java)

> Tutti gli endpoint

| Input testati | Istanze | Comportamento |
|---------------|:-------:|--------------|
| SQL injection payloads | 6 | 400 |
| XSS in email, nome, signup | 10 | 400 |
| Path traversal payloads | 3 | 400/404 |

**Outcome:** V-07, V-08 - SQL injection, XSS e path traversal bloccati su tutti gli endpoint.

---

[**`PasswordComplexitySecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/PasswordComplexitySecurityTest.java)

> `POST /auth/register`

Verifica la regex di complessità applicata al campo `password` durante la registrazione. Sette password deboli (troppo corte, senza maiuscole, senza cifre o senza caratteri speciali) devono essere rifiutate con 400; quattro password che soddisfano tutti i criteri devono essere accettate. Tre numeri di telefono verificano la regex `^[1-9][0-9]{9}$`.

| Input testati | Istanze | Comportamento |
|---------------|:-------:|--------------|
| Password deboli (rifiutate) | 7 | 400 |
| Password forti (accettate) | 4 | Sì |
| Numeri di telefono non validi | 3 | 400 |

**Outcome:** V-08/V-12 - `@Pattern` per complessità minima; regex phone `^[1-9][0-9]{9}$`.

---

[**`FileUploadSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/FileUploadSecurityTest.java)

> Endpoint upload immagini

Invia file con `Content-Type` non in whitelist (PDF, testo, binario generico) all'endpoint di upload immagini e verifica che la risposta sia 415 Unsupported Media Type. Prima della patch V07, qualsiasi file veniva accettato e caricato su Cloudinary; un attaccante avrebbe potuto caricare script, eseguibili o file di configurazione.

**Outcome:** V-07/CWE-434 - whitelist `Content-Type` in `CarImageServiceImpl` / `UserImageServiceImpl`; file non-immagine → 415.

---

[**`PaymentValidationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/PaymentValidationSecurityTest.java)

> Endpoint pagamento

Verifica l'algoritmo di Luhn implementato in `PaymentRules`. Quattro numeri di carta con checksum Luhn corretto devono superare la validazione; quattro con checksum errato devono essere rifiutati. Prima della patch, qualsiasi stringa numerica veniva accettata come numero di carta valido; mancava qualsiasi controllo sul formato.

| Input testati | Istanze | Comportamento |
|---------------|:-------:|--------------|
| Numeri carta Luhn validi | 4 | Sì |
| Numeri carta Luhn non validi | 4 | No |

**Outcome:** V-10/V-11 - algoritmo Luhn implementato; `checkCreditCardExpirationDate()` corretta (`isBefore` anziché `isAfter`).

### Miglioramenti Implementati

| Area | Prima | Dopo | Impact |
|------|-------|------|--------|
| **File Upload** | Nessuna whitelist MIME | Whitelist `Content-Type` · non-immagini → 415 | CWE-434 mitigation |
| **Password Strength** | Seed `"pass"` · solo `@Size(min=8)` | `@Pattern` (maiuscola + minuscola + cifra + speciale) | Weak credential prevention |
| **Phone Validation** | Regex `^[0-9]+$` | `^[1-9][0-9]{9}$` | Format enforcement |
| **Payment - Luhn** | Assente | Algoritmo Luhn implementato in `PaymentRules` | Invalid card acceptance prevention |
| **Payment - Scadenza** | `isAfter` (logica invertita - accettava le scadute) | `isBefore` + agganciata a `checkCreditCard()` | Expired card fix |

---

### 8.6 Cryptographic Failures - A02

Le credenziali hardcodate nel codice sorgente sopravvivono nella storia git anche dopo la rimozione: chiunque abbia accesso al repository può recuperarle con `git log`. Nel progetto originale `application.properties` era committato con JWT secret, API key Cloudinary e credenziali SMTP in chiaro. Il test scansiona il classpath compilato e i file di configurazione cercando pattern riconducibili a queste credenziali, verificando che nulla sia presente nel sorgente corrente e che tutti i segreti siano iniettati a runtime tramite Docker Secrets (`/run/secrets/`).

[**`HardcodedCredentialsSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/HardcodedCredentialsSecurityTest.java)

Scansiona il classpath compilato cercando pattern riconducibili a credenziali hardcoded: chiavi JWT, password, API key Cloudinary, credenziali SMTP. Il test fallisce se trova qualsiasi stringa che corrisponde ai pattern noti (Base64 di lunghezza JWT, pattern SMTP, ecc.) nei file `.class` o `.properties` inclusi nel JAR. Prima della patch, `application.properties` era committato con credenziali reali.

**Nota CI:** il test usa `Assumptions.assumeTrue(Files.exists(PROPS_PATH))` — se `application.properties` non è presente (come accade in CI, dove il file è in `.gitignore`), il test viene **saltato automaticamente** invece di fallire. Questo è il comportamento corretto: in locale il test verifica l'assenza di credenziali nel file; in CI il file non esiste e non c'è nulla da verificare.

**Outcome:** CWE-798 - nessuna credenziale hardcodata nel sorgente corrente; `application.properties` in `.gitignore`; Docker usa Secrets (`/run/secrets/`).

---

### 8.7 Logging & Monitoring - A09

Le due classi di questa sezione compaiono anche in 8.2 (JWT & Token Management): rientrano sia nella gestione dei token sia nel monitoraggio. Sono conteggiate una sola volta nel totale di 403 test.

I sistemi di log aggregato (ELK, CloudWatch, Loki) sono spesso accessibili a un numero più ampio di operatori rispetto al database applicativo. Un refresh token nei log equivale a un token esposto: chiunque legga i log può impersonare l'utente fino alla sua scadenza. I test in questa categoria verificano due proprietà complementari: che il ciclo di refresh non lasci il token in chiaro in nessuna riga di log (CWE-532), e che il logout invalidi il token nel database prima di restituire la risposta al client - rendendo inutilizzabile anche un token eventualmente esfiltrato dai log.

[**`RefreshTokenLoggingSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenLoggingSecurityTest.java)

Cattura l'output del logger durante un ciclo di refresh e verifica che nessuna riga contenga pattern riconducibili al token, né il valore raw né una sua sottostringa. Prima della patch, `RefreshTokenController` stampava `log.info("...", refreshTokenRequest.getToken())`: il token avrebbe potuto finire nei log aggregati accessibili agli operatori.

**Outcome:** CWE-532 - il refresh token non appare mai nei log.

---

[**`RefreshTokenRevocationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenRevocationSecurityTest.java)

Verifica che al logout il refresh token venga invalidato nella tabella `refresh_tokens` (memorizzato come hash SHA-256) e che un successivo tentativo di rinnovo con lo stesso token restituisca 401. La rotazione obbligatoria fa sì che ogni token sia usabile una sola volta - il riuso rivela un possibile furto (theft detection, V10).

---

### Conformità OWASP Top 10 2021

| OWASP | Descrizione | Status | Suite |
|-------|-------------|:------:|-------|
| **A01** | Broken Access Control | Sì | [`SecurityFilterChainTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SecurityFilterChainTest.java) · [`RoleEscalationSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RoleEscalationSecurityTest.java) · [`AdminControllerSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/AdminControllerSecurityTest.java) · [`EmployeeControllerSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/EmployeeControllerSecurityTest.java) · [`RentalControllerSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RentalControllerSecurityTest.java) · [`UserControllerSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/UserControllerSecurityTest.java) |
| **A02** | Cryptographic Failures | Sì | [`HardcodedCredentialsSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/HardcodedCredentialsSecurityTest.java) |
| **A03** | Injection & Input Validation | Sì | [`InputValidationSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/InputValidationSecurityTest.java) · [`PasswordComplexitySecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/PasswordComplexitySecurityTest.java) · [`FileUploadSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/FileUploadSecurityTest.java) · [`PaymentValidationSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/PaymentValidationSecurityTest.java) |
| **A05** | Security Misconfiguration | Sì | [`CorsSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsSecurityTest.java) · [`CorsAttackerDomainSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsAttackerDomainSecurityTest.java) · [`CorsConfigDuplicationSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsConfigDuplicationSecurityTest.java) · [`SwaggerAdminOnlySecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SwaggerAdminOnlySecurityTest.java) · [`SwaggerProdAccessSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SwaggerProdAccessSecurityTest.java) · [`GenericExceptionHandlerSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/GenericExceptionHandlerSecurityTest.java) · [`ValidationErrorExposureSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/ValidationErrorExposureSecurityTest.java) · [`HttpOnlyCookieSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/HttpOnlyCookieSecurityTest.java) |
| **A07** | Identification & Authentication Failures | Sì | [`JwtServiceTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtServiceTest.java) · [`JwtTtlSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtTtlSecurityTest.java) · [`JwtClaimsPIISecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtClaimsPIISecurityTest.java) · [`LogoutSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/LogoutSecurityTest.java) · [`RefreshTokenRevocationSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenRevocationSecurityTest.java) · [`RefreshTokenLoggingSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenLoggingSecurityTest.java) · [`JwtAuthFilterTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/JwtAuthFilterTest.java) · [`RateLimitingSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitingSecurityTest.java) · [`RateLimitingBehaviorTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitingBehaviorTest.java) · [`RateLimitXForwardedForSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitXForwardedForSecurityTest.java) · [`AuthenticationControllerSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/AuthenticationControllerSecurityTest.java) · [`AccountLockoutSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/AccountLockoutSecurityTest.java) |
| **A09** | Security Logging & Monitoring Failures | Sì | [`RefreshTokenLoggingSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenLoggingSecurityTest.java) · [`RefreshTokenRevocationSecurityTest`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenRevocationSecurityTest.java) |

---

## 9. Pipeline CI/CD

Il progetto usa due workflow GitHub Actions separati per responsabilità distinte: `ci.yml` per la continuous integration su ogni push, `deploy.yml` per il rilascio su Docker Hub al momento della pubblicazione di una release GitHub.

### 9.1 Workflow CI ([`.github/workflows/ci.yml`](.github/workflows/ci.yml))

Si attiva su push verso `master-dev`/`master`, su pull request verso `master`, e tramite `workflow_dispatch` manuale. È composto da 6 job con dipendenze esplicite che formano un grafo aciclico diretto (DAG):

```
gitguardian ─────────────────────── (parallelo a compile, nessuna dipendenza)

compile ──┬── security-tests ── sonarcloud
          ├── snyk
          └── semgrep
```

`compile` è il gate iniziale: se Maven non compila, tutti i job dipendenti vengono saltati senza consumare minuti runner. `security-tests` esegue la suite di test di sicurezza con il profilo Maven `-Psecurity-tests` e salva il report Surefire XML come artefatto GitHub; `sonarcloud` lo scarica per calcolare la coverage senza rieseguire i test. Il job `sonarcloud` usa `sonar.qualitygate.wait=false`: i dati vengono inviati a SonarCloud e il job termina subito, senza attendere il calcolo del quality gate (che impiega 2-5 minuti) — il risultato appare nel dashboard SonarCloud in modo asincrono.

| Job | Timeout | Tool | Gate |
|-----|---------|------|------|
| `gitguardian` | 10 min | ggshield path scan (working tree, `--depth 1`) | `continue-on-error: true`; carica SARIF al tab Security |
| `compile` | 10 min | `mvn -B -DskipTests compile` | Gate iniziale — skip downstream se fallisce |
| `security-tests` | 20 min | JUnit 5 + Surefire, profilo `-Psecurity-tests` | Artefatto Surefire XML → SonarCloud |
| `snyk` | 15 min | Snyk CLI Maven (`snyk test --maven-projects`) | SARIF → Security tab; gate blocca HIGH/CRITICAL con fix disponibile |
| `semgrep` | 10 min | `semgrep ci` con `SEMGREP_APP_TOKEN` | Blocca secondo la policy del dashboard Semgrep |
| `sonarcloud` | 15 min | `mvn -B compile sonar:sonar`, `qualitygate.wait=false` | Analisi asincrona — non blocca la CI |

### 9.2 Workflow Deploy ([`.github/workflows/deploy.yml`](.github/workflows/deploy.yml))

Si attiva alla pubblicazione di una release GitHub (`on: release: types: [published]`) oppure tramite `workflow_dispatch` manuale per rollback o deploy di emergenza. È composto da 3 job sequenziali:

```
build → scan → push
```

`build` costruisce l'immagine Docker con BuildKit senza fare push e la salva come artefatto compresso (`.tar`). `scan` carica l'artefatto ed esegue due scanner di sicurezza in parallelo: **Trivy** (genera SARIF con tutte le severità + gate su CRITICAL/HIGH con `--ignore-unfixed`) e **Docker Scout** (genera SARIF + gate con `ignore-base: true`, che filtra le vulnerabilità già presenti nell'immagine base e non attribuibili al codice applicativo). Il push su Docker Hub avviene **solo se entrambi i gate passano**.

| Job | Timeout | Cosa fa |
|-----|---------|---------|
| `build` | 20 min | BuildKit multi-tag (versione release + `:latest`), salva `.tar` come artefatto GitHub |
| `scan` | 20 min | Trivy SARIF + gate `--ignore-unfixed`; Docker Scout SARIF + gate `ignore-base: true` |
| `push` | 10 min | `docker push --all-tags` su Docker Hub (`$DOCKERHUB_USERNAME/rentacar-backend`) |

### 9.3 Sicurezza della Pipeline

Ogni workflow applica il principio del minimo privilegio: i permessi sono dichiarati globalmente come `contents: read` e i job che richiedono privilegi aggiuntivi (upload SARIF, push su Docker Hub) li dichiarano esplicitamente. Tutti i job hanno `timeout-minutes` espliciti per evitare che un job bloccato consumi minuti runner indefinitamente. I secret non compaiono mai nel codice sorgente: vengono letti dai GitHub Secrets e iniettati come variabili d'ambiente dal runner al momento dell'esecuzione.

| Pratica | Implementazione |
|---------|-----------------|
| Permessi minimi | `permissions: contents: read` globale; `security-events: write` dichiarato per-job solo dove serve per l'upload SARIF |
| Timeout espliciti | Tutti i job hanno `timeout-minutes` — nessun job può bloccare la pipeline indefinitamente |
| Build separato dal push | `build` non fa push; il push avviene solo dopo che `scan` conferma assenza di CVE critiche |
| Nessun secret nel codice | Tutti i secret letti da GitHub Secrets/Variables al momento dell'esecuzione |

### 9.4 Segreti Pipeline

| Nome | Tipo | Workflow | Utilizzo |
|------|------|:--------:|---------|
| `GITGUARDIAN_API_KEY` | Secret | CI | ggshield auth |
| `SNYK_TOKEN` | Secret | CI | Snyk CLI |
| `SEMGREP_APP_TOKEN` | Secret | CI | Semgrep policy dashboard |
| `SONAR_TOKEN` | Secret | CI | SonarCloud autenticazione |
| `SONAR_ORGANIZATION` | Variable | CI | Organizzazione SonarCloud |
| `SONAR_PROJECT_KEY` | Variable | CI | Chiave progetto SonarCloud |
| `DOCKERHUB_USERNAME` | Secret | Deploy | Login Docker Hub + nome immagine |
| `DOCKERHUB_TOKEN` | Secret | Deploy | Accesso token Docker Hub |

---

## 10. Kubernetes

Il progetto è stato deployato su un server Ubuntu con **microk8s**, una distribuzione Kubernetes certificata CNCF pensata per ambienti single-node. I manifest sono nella cartella [`k8s/`](k8s/).

### 10.1 Architettura

```
Internet
  └── nginx-ingress-controller :80 (microk8s addon)
        └── Service rentacar-frontend :80→8080
              └── Pod nginx (React SPA + reverse proxy)
                    └── /api/* → proxy_pass → Service rentacar-app :8080
                                                └── Pod Spring Boot
                                                      └── Service postgres :5432
                                                            └── Pod PostgreSQL + PVC 5Gi
```

Il backend Spring Boot è un Service di tipo ClusterIP, raggiungibile solo dall'interno del cluster. Tutto il traffico esterno entra dall'Ingress, arriva al pod nginx del frontend, e solo le richieste verso `/api/` vengono inoltrate internamente allo Spring Boot tramite `proxy_pass`. Questo risolve anche il problema CORS: frontend e API risultano sulla stessa origine per il browser.

### 10.2 Setup microk8s

```bash
sudo snap install microk8s --classic --channel=1.32/stable
sudo usermod -aG microk8s $USER
newgrp microk8s
microk8s enable dns storage ingress
```

| Addon | Funzione |
|-------|----------|
| `dns` | CoreDNS: i pod si raggiungono per nome (`postgres`, `rentacar-app`) invece che per IP |
| `storage` | hostpath-provisioner: soddisfa automaticamente le PersistentVolumeClaim con directory locali |
| `ingress` | nginx-ingress-controller nel namespace `ingress`: riceve traffico sulla porta 80 del server |

### 10.3 Secrets

Le credenziali (password DB, JWT secret, API keys) non compaiono mai nei manifest YAML committati. Vengono create come oggetti Secret Kubernetes eseguendo lo script:

```bash
bash k8s/setup-secrets.sh
```

Lo script legge i file dalla directory `secrets/` (in `.gitignore`) e crea il Secret `rentacar-secrets` nel namespace `rentacar` con tecnica idempotente (`--dry-run=client -o yaml | kubectl apply`). I Secret vengono montati nei pod come file in `/run/secrets/`; Spring Boot con profilo `docker` legge le credenziali da quei file tramite `${file:/run/secrets/NOME}`.

### 10.4 Build e Push Immagini

Kubernetes non builda immagini: le scarica da un registry. Le immagini vengono buildate sulla macchina di sviluppo e pushate su Docker Hub con lo script:

```bash
bash k8s/build-push.sh          # tag :latest
bash k8s/build-push.sh v1.2.3   # tag :v1.2.3 + :latest
```

In alternativa, la pubblicazione di una GitHub Release attiva automaticamente il workflow `deploy.yml` descritto in §9.2, che aggiunge i gate di sicurezza Trivy e Docker Scout prima del push.

### 10.5 Manifest Kubernetes

I manifest vengono applicati in ordine numerico dallo script `k8s/deploy.sh`:

| File | Risorsa | Nota |
|------|---------|------|
| `00-namespace.yaml` | Namespace `rentacar` | Deve esistere prima di tutto il resto |
| `01-configmap.yaml` | ConfigMap `rentacar-config` | Variabili non sensibili (URL DB, porta, host SMTP) |
| `02-postgres.yaml` | PVC + Deployment + Service postgres | PVC `ReadWriteOnce` 5Gi; readiness/liveness via `pg_isready` |
| `03-app.yaml` | Deployment + Service Spring Boot | initContainer `wait-for-postgres` (`nc -z postgres 5432`) |
| `04-frontend.yaml` + `04b-nginx-config.yaml` | Deployment + Service nginx + ConfigMap nginx.conf | nginx su porta 8080; `emptyDir` per `/var/cache/nginx` |
| `05-ingress.yaml` | Ingress | `pathType: Prefix` `/` → Service `rentacar-frontend` |
| `06-networkpolicy.yaml` | 3 NetworkPolicy | Whitelist: postgres ← solo app; app ← solo frontend; frontend ← solo ingress |

Ogni container ha `allowPrivilegeEscalation: false`, `drop: ["ALL"]` sulle Linux capabilities e `seccompProfile: RuntimeDefault`. Il container postgres usa `fsGroup: 999` a livello di pod spec per garantire che il PVC sia accessibile all'utente `postgres` (UID 999) senza richiedere privilegi root.

### 10.6 Aggiornamenti

```bash
# Rollout restart dopo push nuova immagine
microk8s kubectl rollout restart deployment/rentacar-app -n rentacar
microk8s kubectl rollout restart deployment/rentacar-frontend -n rentacar
microk8s kubectl rollout status deployment/rentacar-app -n rentacar

# Aggiornamento manifest YAML
microk8s kubectl apply -f k8s/NOME_FILE.yaml
```

### 10.7 Comandi utili di diagnostica

```bash
microk8s kubectl get pods -n rentacar
microk8s kubectl logs -f -n rentacar -l app=rentacar-app
microk8s kubectl logs -n rentacar <nome-pod> --previous
microk8s kubectl exec -it deployment/rentacar-app -n rentacar -- sh
microk8s kubectl exec -it deployment/postgres -n rentacar -- psql -U postgres -d rentacar
microk8s kubectl describe pod <nome-pod> -n rentacar
```

### 10.8 Problemi Riscontrati e Soluzioni

| # | Problema | Causa | Fix |
|---|----------|-------|-----|
| 1 | `x509: certificate is valid for 192.168.1.59, not 192.168.1.60` | IP LAN cambiato via DHCP; certificato kubelet generato con vecchio IP | `sudo microk8s refresh-certs --cert ca.crt`; fix permanente: IP statico tramite netplan |
| 2 | `mkdir() "/var/cache/nginx/client_temp" failed (13: Permission denied)` | `runAsUser: 101` (nginx); directory owned da root | Mount `emptyDir` su `/var/cache/nginx` e `/var/run` |
| 3 | `bind() to 0.0.0.0:80 failed (13: Permission denied)` | `NET_BIND_SERVICE` non applicabile con `drop: ["ALL"]` + `allowPrivilegeEscalation: false` | nginx configurato su porta 8080 tramite ConfigMap |
| 4 | `read-only file system` al mount del service account token | Filesystem immagine in modalità restrittiva; K8s monta token SA automaticamente | `automountServiceAccountToken: false` nel pod spec |
| 5 | `DataNotFoundException: Customer not found` al riavvio | Seed parziale: admin+employee creati, customer no; al riavvio il check `getAll().isEmpty()` trova utenti e salta la creazione | `try/catch` in `Application.run()` intorno a `seedDataConfig.runFirst()`; seed failure non-fatale |
| 6 | 504 Gateway Timeout dall'Ingress | NetworkPolicy `frontend-allow-only-ingress` autorizzava porta 80; nginx cambiato a 8080 | Aggiornata policy per autorizzare porta 8080 |
| 7 | Semgrep finding su `02-postgres.yaml`: `allow-privilege-escalation-no-securitycontext` | Nessun `securityContext` sul container postgres | Aggiunto `runAsUser: 999`, `runAsNonRoot: true`, `fsGroup: 999` e `fsGroupChangePolicy: OnRootMismatch` |

---