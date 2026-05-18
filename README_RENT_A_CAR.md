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
   - [4.1 Autenticazione](#41-autenticazione--apiv1auth)
   - [4.2 Refresh Token](#42-refresh-token--apiv1refresh-token)
   - [4.3 Verifica Email](#43-verifica-email--apiv1verify)
   - [4.4 Utenti](#44-utenti--apiv1users)
   - [4.5 Amministratori](#45-amministratori--apiv1admins)
   - [4.6 Clienti](#46-clienti--apiv1customers)
   - [4.7 Dipendenti](#47-dipendenti--apiv1employees)
   - [4.8 Veicoli](#48-veicoli--apiv1cars)
   - [4.9 Noleggi](#49-noleggi--apiv1rentals)
   - [4.10 Pagamenti](#410-pagamenti--apiv1paymentdetails-e-apiv1paymenttypes)
   - [4.11 Sconti](#411-sconti--apiv1discounts)
   - [4.12 Patenti di Guida](#412-patenti-di-guida--apiv1drivinglicensetype)
   - [4.13 Caratteristiche Veicolo](#413-caratteristiche-veicolo)
   - [4.14 Immagini](#414-immagini--apiv1images)
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
   - [9.1 Livello Root](#91-livello-root-githubworkflows)
   - [9.2 Livello Backend](#92-livello-backend-rentacar_backendgithubworkflows)
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

**ExtendRent** è un sistema REST API per la gestione del noleggio di autoveicoli. La piattaforma gestisce l'intero ciclo di vita del noleggio - dalla registrazione del cliente alla restituzione del mezzo - con autenticazione JWT, tre ruoli applicativi e integrazione con Cloudinary (immagini) e SMTP (email OTP). Tutte le entità adottano *soft delete*: i dati non vengono mai cancellati fisicamente.

Funzionalità principali:

- **Autenticazione JWT** - Registrazione, login, refresh token, verifica account via OTP; token veicolati come cookie `HttpOnly; Secure; SameSite=Strict` (patch V-02)
- **RBAC a tre ruoli** - Admin, Employee, Customer con policy deny-by-default
- **Catalogo Veicoli** - Ricerca filtrata su 23 attributi (marca, colore, carburante, segmento, tipo patente)
- **Ciclo di Vita del Noleggio** - Creazione, avvio, restituzione, cancellazione con tracciamento chilometri
- **Pagamenti e Sconti** - Validazione Luhn, report ricavi, codici sconto con percentuale configurabile
- **Upload Immagini** - Su Cloudinary con whitelist del tipo file (patch V07)
- **API Documentation** - Swagger / OpenAPI, riservato al solo ADMIN (patch V13)

All'avvio, `SeedDataConfig` popola automaticamente le tabelle di lookup con i valori di default definiti nelle enum: marche, modelli, colori, tipi carburante, cambi, carrozzerie, segmenti, stati veicolo e stati noleggio, tipi di pagamento, tipi di patente di guida, utente admin di default. Il tutto viene eseguito tramite `CommandLineRunner` nella classe `Application`, con logging AOP temporaneamente disabilitato durante l'inizializzazione per evitare output ridondante.

### 1.2 Stack Tecnologico

| Tecnologia | Versione | Scopo |
|---|---|---|
| **Java** | 17 | Linguaggio principale |
| **Spring Boot** | 3.5.14 | Framework REST API (aggiornato da 3.2.1 per CVE Snyk) |
| **Spring Security** | 6.5.10 | Autenticazione JWT, RBAC, filter chain |
| **Spring Data JPA + Hibernate** | (BOM) | ORM e accesso database |
| **Spring Validation** | (BOM) | Bean validation Jakarta |
| **Spring Mail** | (BOM) | Invio email OTP e notifiche |
| **Spring Actuator** | (BOM) | `/actuator/health` per health check Docker |
| **PostgreSQL driver** | 42.7.11 | Connettività database (pinned per CVE fix) |
| **JWT (jjwt)** | 0.11.2 | Generazione e validazione token HS256 |
| **Bucket4j** | 8.0.1 | Rate limiting token-bucket |
| **Caffeine** | 3.13.0 | Cache bounded per bucket per-IP (eviction 2 min, max 50k entry) |
| **Cloudinary SDK** | 1.27.0 | Storage immagini |
| **commons-text** | 1.10.0 | Escape/sanitize stringhe |
| **Lombok** | 1.18.30 | Riduzione boilerplate |
| **Swagger / OpenAPI** | 2.0.4 | Documentazione API (springdoc) |
| **progressbar** | 0.10.0 | Progress bar ASCII durante seed dati all'avvio |

**Dipendenze rimosse:**

- `spring-boot-devtools` - Snyk #73: Timing Attack (CWE-208); DevTools non deve mai essere in produzione
- `springfox-swagger2` / `springfox-swagger-ui` - Snyk #68/#69: XSS + Improper Input Validation; non mantenuti dal 2021; sostituiti da `springdoc-openapi-starter-webmvc-ui:2.0.4`

**Dipendenze di test:**

| Artifact | Scopo |
|----------|-------|
| `spring-boot-starter-test` | JUnit 5, Mockito, Spring Test, MockMvc |
| `spring-security-test` | `SecurityMockMvcRequestPostProcessors` - test con ruoli simulati |
| `h2` | Database in-memory per i web-layer test - nessun PostgreSQL richiesto in CI |
| `assertj-core` | Asserzioni fluent nei test di sicurezza |
| `mockito-core` | Mock degli oggetti nei test unitari |

**Override di versione per sicurezza (`pom.xml`):**

| Proprietà | Versione | Motivo |
|-----------|----------|--------|
| `spring-boot-starter-parent` | **3.5.14** | Aggiornato da 3.5.13 - fix Snyk #71/#74/#75/#76/#77/#78/#79 |
| `tomcat.version` | `10.1.54` | Pin esplicito oltre il default Spring Boot |
| `spring-security.version` | `6.5.10` | Pin esplicito per CVE Spring Security |
| `postgresql.version` | `42.7.11` | Fix Snyk #70 - Resource Allocation Without Limits |
| `logback.version` | `1.5.25` | Fix CVE Logback |
| `commons-lang3.version` | `3.18.0` | Allineamento versione sicura |

### 1.3 System Design - Class Diagram

Il modello delle entità è suddiviso in tre diagrammi tematici per ragioni di leggibilità. Tutti gli oggetti persistenti ereditano da `BaseEntity`, una *MappedSuperclass* che fornisce i campi comuni a tutte le entità: `id`, `isDeleted`, `deletedAt`, `lastModified` e `createdDate`, garantendo uniformità nella gestione del ciclo di vita e supportando il meccanismo di *soft delete* adottato dall'intera applicazione.

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
- **`store/`** - Redux Toolkit store con 22 slice tematici (`carSlice`, `rentalSlice`, `signInSlice`, ecc.) e loading globale automatico via `addMatcher` su `pending`/`fulfilled`/`rejected`.
- **`services/`** - Layer di comunicazione con il backend: 20+ service class che incapsulano le chiamate Axios per ogni entità (Car, Rental, Customer, Brand, Color, ecc.).
- **`models/`** - Modelli TypeScript tipizzati per tutte le request e response, garantendo type-safety end-to-end.
- **`utils/`** - `axiosInterceptors.ts` (istanza Axios condivisa con `withCredentials: true`) e `useToken.ts` (hook per decodare i claim JWT).

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

A seguito della patch **V02** (HttpOnly cookie), il frontend non memorizza più i token in `localStorage` (vettore XSS). La configurazione Axios centralizzata in `axiosInterceptors.ts` usa `withCredentials: true`, che istruisce il browser a inviare automaticamente i cookie HttpOnly su ogni richiesta API:

```ts
const axiosInstance = axios.create({
  baseURL: config.apiBaseUrl,
  withCredentials: true,  // invia cookie HttpOnly su ogni request
});
```

Il campo `token` restituito dal backend nel corpo della response di signin è ora vuoto (i token viaggiano esclusivamente via cookie); il frontend decodifica il JWT via `jwt-decode` per estrarre i claim (`id`, `role`, `emailAddress`) necessari all'UI, senza mai esporre il token grezzo.

**RBAC Lato Frontend**

Il controllo dei ruoli è implementato sia nella `Navbar` che nel componente `AdminRoutes`:

- **Navbar**: il link "Admin Panel" è visibile solo se `decodedToken.role` contiene `"ADMIN"`.
- **AdminRoutes**: componente di routing che blocca l'accesso alle route `/adminPanel/**` per utenti non ADMIN, reindirizzando alla homepage.

Il RBAC frontend è un livello di UX, non una misura di sicurezza primaria: il backend enforcement (via `SecurityConfig.hasRole("ADMIN")`) è la barriera reale contro accessi non autorizzati.

**Validazione Form**

Tutti i form usano **Formik** con schemi **Yup** per la validazione client-side prima dell'invio:
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

**Header di sicurezza nginx (`nginx.conf`):**

| Header | Scopo |
|--------|-------|
| `Content-Security-Policy` | Limita le origini di script, stili e connessioni - `connect-src 'self' http://rentacar-app:8080`; `script-src 'self' 'unsafe-inline' 'unsafe-eval'` |
| `X-Frame-Options: SAMEORIGIN` | Previene clickjacking (iframe da domini esterni) |
| `X-Content-Type-Options: nosniff` | Previene MIME type sniffing da parte del browser |
| `X-XSS-Protection: 1; mode=block` | Blocca XSS riflesso nei browser legacy |
| `Referrer-Policy: strict-origin-when-cross-origin` | Non espone URL completo nelle richieste cross-origin |
| `Permissions-Policy: camera=(), microphone=(), geolocation=()` | Disabilita esplicitamente accesso a camera, microfono e geolocalizzazione |
| `server_tokens off` | Nasconde la versione nginx dagli header di risposta |
| `gzip on` | Compressione delle risposte per ridurre latenza |

### 2.6 Flusso di Una Feature: Ricerca e Prenotazione Auto

Il flusso principale dell'applicazione illustra come backend e frontend cooperano:

1. **Homepage** - il componente `Search` raccoglie le date di inizio e fine noleggio tramite un date picker.
2. **Redux Thunk** - `getByAllFilteredCars()` chiama `GET /api/v1/cars/filter?startDate=&endDate=` e salva i risultati nello store.
3. **SelectedCar** - mostra le auto disponibili; il componente `ShowRental` calcola il prezzo (con eventuale sconto via `POST /api/v1/rentals/show`) e il componente `Payment` raccoglie i dati della carta di credito.
4. **Checkout** - `POST /api/v1/rentals` crea il noleggio; la response aggiorna lo store e mostra il riepilogo.

---

## 3. Architettura e Ruoli

### 3.1 Struttura Generale

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

**Componenti infrastrutturali trasversali (`core/`):**

| Componente | File | Responsabilità |
|------------|------|----------------|
| JWT Filter | `JwtAuthFilter.java` | Intercetta ogni richiesta, estrae e valida il token Bearer, popola il `SecurityContextHolder` |
| Rate Limiter | `RateLimitFilter.java` | Token-bucket per IP tramite Bucket4j + Caffeine - blocca burst su `/api/**` |
| Security Config | `SecurityConfig.java` | Definisce la filter chain, regole CORS, accesso per ruolo (vedi matrice §3.4) |
| Exception Handler | `GlobalExceptionHandler.java` | Traduce eccezioni in risposte HTTP strutturate senza esporre stack trace o dettagli interni |
| AOP Logging | `LoggingAspect.java` | Logging automatico di ingresso/uscita dei metodi Service tramite Spring AOP |
| Seed Data | `SeedDataConfig.java` | Popola le tabelle di lookup all'avvio (tipi patente, stati veicolo, tipi pagamento, admin default) |

### 3.3 Ruoli

| Ruolo | Descrizione |
|-------|-------------|
| **Admin** | Gestione completa: utenti, veicoli, noleggi, sconti, pagamenti, report, immagini |
| **Employee** | Gestione operativa: avanzamento stato noleggi (via business logic) |
| **Customer** | Ricerca veicoli, prenotazione e gestione dei propri noleggi |

Employee e Customer **non si distinguono a livello Spring Security** - entrambi sono `authenticated()`. La distinzione è nel service layer.

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

> **Nota V-16 (catalogo):** GET su cars/brands/colors/ecc. usa `authenticated()` (qualsiasi utente loggato). POST/PUT/DELETE sugli stessi path usa `hasRole("ADMIN")`. Due regole separate valutate in ordine - la prima che matcha GET vince, POST/PUT/DELETE cadono sulla seconda regola.

---

## 4. API Reference

> Base URL: `http://localhost:8080`
> Documentazione interattiva: `http://localhost:8080/swagger-ui/index.html` (solo ADMIN)

Tutte le risposte seguono un wrapper uniforme con i campi `data`, `message` e `success`. Per i codici di stato HTTP standard, vedi [MDN HTTP Status Codes](https://developer.mozilla.org/en-US/docs/Web/HTTP/Status).

---

### 4.1 Autenticazione - `/api/v1/auth`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/auth/signup` | Registrazione nuovo utente |
| `POST` | `/api/v1/auth/signin` | Login e ottenimento JWT |
| `POST` | `/api/v1/auth/isUserTrue` | Verifica credenziali |
| `POST` | `/api/v1/auth/logout` | Logout con revoca token |

`signup` richiede nome, cognome, email, telefono, password e tipo utente; restituisce `accessToken`, `refreshToken` e `expiresIn`. `signin` riceve email e password, restituisce gli stessi campi token. `isUserTrue` verifica le credenziali senza emettere token. `logout` revoca il token corrente.

---

### 4.2 Refresh Token - `/api/v1/refresh-token`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/refresh-token/` | Rinnovo access token (monouso - risposta include nuovo refreshToken) |

Richiede il `refreshToken` nel body; il token è monouso - la risposta include sia il nuovo `accessToken` che un nuovo `refreshToken`.

---

### 4.3 Verifica Email - `/api/v1/verify`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `GET` | `/api/v1/verify/email?token=X` | Conferma indirizzo email tramite OTP |

Il token OTP viene inviato via email all'indirizzo registrato; la verifica attiva l'account e abilita il login.

---

### 4.4 Utenti - `/api/v1/users`

| Metodo | Path | Parametri | Descrizione |
|--------|------|-----------|-------------|
| `GET` | `/api/v1/users/` | `page`, `size` (Pageable) | Lista utenti paginata |
| `GET` | `/api/v1/users/` | `isDeleted=true\|false` | Filtra per stato eliminazione |
| `GET` | `/api/v1/users/{id}` | path: `id` | Utente per ID |
| `GET` | `/api/v1/users/count/{isDeleted}` | path: `isDeleted` | Conteggio utenti |
| `PUT` | `/api/v1/users/updatePassword` | body JSON | Aggiorna password |
| `PUT` | `/api/v1/users/block/{id}` | path: `id` | Blocca utente |

Tutti gli endpoint richiedono ruolo ADMIN. La lista utenti è paginabile tramite i parametri Pageable standard di Spring (`page`, `size`, `sort`).

---

### 4.5 Amministratori - `/api/v1/admins`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/admins/` | Crea amministratore |
| `PUT` | `/api/v1/admins/` | Aggiorna amministratore |
| `GET` | `/api/v1/admins/` | Lista tutti gli amministratori |
| `GET` | `/api/v1/admins/{id}` | Amministratore per ID |
| `GET` | `/api/v1/admins/?isDeleted=X` | Filtra per stato eliminazione |
| `GET` | `/api/v1/admins/count/{isDeleted}` | Conteggio amministratori |
| `DELETE` | `/api/v1/admins/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

La creazione richiede dati anagrafici, credenziali, stipendio e il campo `adminType`.

---

### 4.6 Clienti - `/api/v1/customers`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/customers/` | Crea cliente |
| `PUT` | `/api/v1/customers/` | Aggiorna cliente |
| `GET` | `/api/v1/customers/` | Lista tutti i clienti |
| `GET` | `/api/v1/customers/{id}` | Cliente per ID |
| `GET` | `/api/v1/customers/rentals/{customerId}` | Storico noleggi del cliente |
| `GET` | `/api/v1/customers/?isDeleted=X` | Filtra per stato eliminazione |
| `GET` | `/api/v1/customers/count/{isDeleted}` | Conteggio clienti |
| `GET` | `/api/v1/customers/countByStatus/{status}` | Conteggio per stato |
| `DELETE` | `/api/v1/customers/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

Endpoint ad accesso ADMIN. L'eliminazione soft marca il record come eliminato logicamente (`isDeleted=true`) senza rimuoverlo dal database.

---

### 4.7 Dipendenti - `/api/v1/employees`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/employees/` | Crea dipendente |
| `PUT` | `/api/v1/employees/` | Aggiorna dipendente |
| `GET` | `/api/v1/employees/` | Lista tutti i dipendenti |
| `GET` | `/api/v1/employees/{id}` | Dipendente per ID |
| `GET` | `/api/v1/employees/phone/{phoneNumber}` | Dipendente per numero di telefono |
| `GET` | `/api/v1/employees/?startSalary=X&endSalary=Y` | Filtra per fascia di stipendio |
| `GET` | `/api/v1/employees/?isDeleted=X` | Filtra per stato eliminazione |
| `GET` | `/api/v1/employees/count/{isDeleted}` | Conteggio dipendenti |
| `DELETE` | `/api/v1/employees/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

Endpoint ad accesso ADMIN. Supporta filtro per fascia di stipendio e recupero diretto per numero di telefono.

---

### 4.8 Veicoli - `/api/v1/cars`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/cars/` | Crea veicolo |
| `PUT` | `/api/v1/cars/` | Aggiorna veicolo |
| `GET` | `/api/v1/cars/` | Lista tutti i veicoli |
| `GET` | `/api/v1/cars/{id}` | Veicolo per ID |
| `GET` | `/api/v1/cars/count/{isDeleted}` | Conteggio veicoli |
| `GET` | `/api/v1/cars/countByStatus/{statusId}` | Conteggio per stato |
| `GET` | `/api/v1/cars/?startDate=X&endDate=Y` | Veicoli disponibili nel periodo |
| `GET` | `/api/v1/cars/?isDeleted=X` | Filtra per stato eliminazione |
| `GET` | `/api/v1/cars/filter` | Ricerca avanzata multi-criterio |
| `DELETE` | `/api/v1/cars/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

#### `GET /api/v1/cars/filter` - Parametri di ricerca avanzata

| Parametro | Tipo | Descrizione |
|-----------|------|-------------|
| `customerId` | Long | Filtra veicoli compatibili con la patente del cliente |
| `licenseSuitable` | Boolean | Solo veicoli adatti alla patente del cliente |
| `startDate` | LocalDate | Data inizio noleggio desiderata |
| `endDate` | LocalDate | Data fine noleggio desiderata |
| `brandId` | Long | Marca |
| `modelId` | Long | Modello |
| `colorId` | Long | Colore |
| `fuelTypeId` | Long | Tipo carburante |
| `shiftTypeId` | Long | Tipo cambio |
| `seat` | Integer | Numero posti |
| `luggage` | Integer | Capacità bagagli |
| `startPrice` | Double | Prezzo giornaliero minimo |
| `endPrice` | Double | Prezzo giornaliero massimo |
| `startYear` | Integer | Anno immatricolazione minimo |
| `endYear` | Integer | Anno immatricolazione massimo |
| `isDeleted` | Boolean | Includi eliminati logicamente |
| `statusId` | Long | Stato veicolo |
| `segmentId` | Long | Segmento |

La creazione richiede targa, chilometraggio, anno, posti, bagagli, prezzo giornaliero e gli ID di colore, carburante, cambio, stato, modello, carrozzeria, segmento e tipo patente richiesta.

---

### 4.9 Noleggi - `/api/v1/rentals`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/rentals/showRental` | Anteprima noleggio con costi |
| `POST` | `/api/v1/rentals/` | Crea noleggio |
| `PUT` | `/api/v1/rentals/` | Aggiorna noleggio |
| `PUT` | `/api/v1/rentals/startRental/{rentalId}` | Avvia noleggio (segna come attivo) |
| `PUT` | `/api/v1/rentals/returnRental` | Registra restituzione veicolo |
| `PUT` | `/api/v1/rentals/cancelRental/{rentalId}` | Cancella noleggio |
| `GET` | `/api/v1/rentals/` | Lista tutti i noleggi |
| `GET` | `/api/v1/rentals/statuses` | Lista stati noleggio |
| `GET` | `/api/v1/rentals/{id}` | Noleggio per ID |
| `GET` | `/api/v1/rentals/?isDeleted=X` | Filtra per stato eliminazione |
| `GET` | `/api/v1/rentals/?statusId=X` | Filtra per stato noleggio |
| `GET` | `/api/v1/rentals/count/{isDeleted}` | Conteggio noleggi |
| `GET` | `/api/v1/rentals/countByStatus/{status}` | Conteggio per stato |
| `DELETE` | `/api/v1/rentals/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

`showRental` riceve ID cliente, ID auto, date e codice sconto opzionale; restituisce anteprima con giorni, prezzo giornaliero, percentuale sconto e totale. `returnRental` riceve l'ID noleggio, la data di restituzione e il chilometraggio finale.

---

### 4.10 Pagamenti - `/api/v1/paymentDetails` e `/api/v1/paymentTypes`

#### Payment Details

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `PUT` | `/api/v1/paymentDetails/` | Aggiorna dettagli pagamento |
| `GET` | `/api/v1/paymentDetails/{id}` | Dettaglio per ID |
| `GET` | `/api/v1/paymentDetails/` | Lista tutti i pagamenti |
| `GET` | `/api/v1/paymentDetails/monthlyIncome?startDate=X&endDate=Y` | Ricavi mensili nel periodo |
| `GET` | `/api/v1/paymentDetails/yearlyIncome?year=X` | Ricavi totali nell'anno |
| `GET` | `/api/v1/paymentDetails/totalIncome` | Ricavi totali assoluti |
| `GET` | `/api/v1/paymentDetails/filter?minAmount=X&maxAmount=Y&minDate=X&maxDate=Y&isDeleted=X` | Filtra pagamenti |

#### Payment Types

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `PUT` | `/api/v1/paymentTypes/` | Aggiorna tipo pagamento |
| `GET` | `/api/v1/paymentTypes/` | Lista tutti i tipi |
| `GET` | `/api/v1/paymentTypes/{id}` | Tipo per ID |
| `GET` | `/api/v1/paymentTypes/?isActive=true\|false` | Filtra per stato attivo |

I dettagli di pagamento sono creati automaticamente alla chiusura di un noleggio; gli endpoint di report (`monthlyIncome`, `yearlyIncome`, `totalIncome`) richiedono ruolo ADMIN. I tipi di pagamento sono una lookup table configurabile (carte, contanti, ecc.).

---

### 4.11 Sconti - `/api/v1/discounts`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/discounts/` | Crea codice sconto |
| `PUT` | `/api/v1/discounts/` | Aggiorna codice sconto |
| `GET` | `/api/v1/discounts/` | Lista tutti gli sconti |
| `GET` | `/api/v1/discounts/{id}` | Sconto per ID |
| `GET` | `/api/v1/discounts/code/{discountCode}` | Sconto per codice testuale |
| `GET` | `/api/v1/discounts/?isDeleted=X` | Filtra per stato eliminazione |
| `GET` | `/api/v1/discounts/?isActive=true\|false` | Filtra per stato attivo |
| `DELETE` | `/api/v1/discounts/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

La creazione richiede il codice testuale, la percentuale di sconto e il flag `isActive`.

---

### 4.12 Patenti di Guida - `/api/v1/drivingLicenseType`

| Metodo | Path | Descrizione |
|--------|------|-------------|
| `POST` | `/api/v1/drivingLicenseType/` | Crea tipo patente |
| `PUT` | `/api/v1/drivingLicenseType/` | Aggiorna tipo patente |
| `GET` | `/api/v1/drivingLicenseType/` | Lista tutti i tipi di patente |
| `GET` | `/api/v1/drivingLicenseType/{id}` | Tipo patente per ID |
| `GET` | `/api/v1/drivingLicenseType/?isDeleted=X` | Filtra per stato eliminazione |
| `DELETE` | `/api/v1/drivingLicenseType/?id=X&isHardDelete=true\|false` | Elimina (soft o hard) |

Lookup table per i tipi di patente richiesti dai veicoli (A, B, BE, ecc.). Modificabile solo da ADMIN.

---

### 4.13 Caratteristiche Veicolo

Tutti gli endpoint seguono il pattern CRUD standard con `POST /`, `PUT /`, `GET /`, `GET /{id}`, `GET ?isDeleted=X`, `DELETE ?id=X&isHardDelete=true|false`.

| Risorsa | Base Path |
|---------|-----------|
| Marche | `/api/v1/brands` |
| Modelli auto | `/api/v1/carModels` |
| Carrozzerie | `/api/v1/carBodyTypes` |
| Segmenti | `/api/v1/car-segments` |
| Colori | `/api/v1/colors` |
| Tipi carburante | `/api/v1/fuels` |
| Tipi cambio | `/api/v1/gearshifts` |
| Stati veicolo | `/api/v1/vehicle-statuses` |

> **Nota:** `/api/v1/carModels` espone anche `GET /brands/{brandId}` per filtrare i modelli per marca.

---

### 4.14 Immagini - `/api/v1/images`

| Metodo | Path | Parametri Form | Descrizione |
|--------|------|----------------|-------------|
| `POST` | `/api/v1/images/car` | `file` (MultipartFile), `licensePlate` | Carica immagine auto |
| `POST` | `/api/v1/images/user` | `file` (MultipartFile), `emailAddress` | Carica immagine profilo utente |
| `POST` | `/api/v1/images/brand` | `file` (MultipartFile), `brandName` | Carica immagine brand |

Le immagini vengono caricate su **Cloudinary** e l'URL viene salvato nel database.

---

## 5. Containerizzazione

### 5.1 Dockerfile Backend ([`rentACar_backend/Dockerfile`](Dockerfile))

Il backend adotta un **multi-stage build** in due fasi distinte: una per la compilazione, una per l'esecuzione. L'immagine finale non contiene Maven, il JDK, i sorgenti né i file `.class` intermedi - solo il JAR eseguibile.

**Scelte di sicurezza:**

| Scelta | Motivazione |
|--------|-------------|
| Multi-stage build | Immagine finale senza Maven/JDK; superficie Trivy minimale |
| `eclipse-temurin:17-jre` come base runtime | JRE senza compilatore; pacchetti OS ridotti |
| `dependency:go-offline` come layer separato | Cache Docker riutilizzata finché `pom.xml` non cambia |
| Utente `appuser` UID 10001 | Exploit app → nessun root sull'host |
| `--chown` su COPY | Proprietà corretta senza chmod separato |
| Health check via `/actuator/health` | Verifica reale HTTP, non solo TCP |
| Forma array in ENTRYPOINT | Processo Java riceve `SIGTERM` direttamente (shutdown ordinato) |

---

### 5.2 Dockerfile Frontend ([`rent-a-car-frontend-project/Dockerfile`](../rent-a-car-frontend-project/Dockerfile))

Anche il frontend adotta multi-stage: Node per la build React, nginx Alpine per il serving. L'immagine finale non contiene Node.js, npm né i sorgenti TypeScript.

**Scelte di sicurezza:**

| Scelta | Motivazione |
|--------|-------------|
| Multi-stage build | Immagine finale senza Node.js/npm/sorgenti TS |
| `nginx:1.27-alpine` come base serving | Immagine minimale (~25 MB vs ~700 MB Node) |
| `apk upgrade --no-cache` | Risolve CVE Alpine senza accumulare layer cache |
| Configurazione nginx custom | SPA routing corretto; reverse proxy `/api/` |
| `nginx` utente non-root (Alpine default) | Nessun privilegio root sul serving |
| `daemon off` in CMD | nginx in foreground: Docker vede il processo principale attivo |

---

### 5.3 Docker Compose ([`rentACar_backend/docker-compose.yml`](docker-compose.yml))

Il file orchestra tre servizi - `postgres`, `app` (backend), `frontend` - su una rete bridge privata. L'unica porta esposta all'host è la 8080 (nginx), tutto il resto comunica internamente.

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

## 6. Vulnerabilità Identificate - Audit Manuale

L'analisi si è svolta in due round distinti di audit statico del codice sorgente, seguendo il framework OWASP Top 10 2021 e CWE come riferimento tecnico. Il primo round ha rilevato 14 vulnerabilità e un flaw architetturale critico (`anyRequest().permitAll()` come regola globale); il secondo round ha identificato 14 vulnerabilità aggiuntive più sottili nel frontend e nella logica di business. **Totale: 29 vulnerabilità - tutte risolte.**

### 6.1 Vulnerabilità Identificate

| Round | ID | Titolo | Gravità | OWASP | CWE | Stato |
|:-----:|----|--------|---------|-------|-----|:-----:|
| 1 | S1-1 | Global `permitAll()` - nessun RBAC | Critica | A01 | 284, 862 | Risolto |
| 1 | V01 | Password nei query parameter (`isUserTrue`, `updatePassword`) | Critica | A07 | 598 | Risolto |
| 1 | V02 | Escalation ruolo via signup pubblico | Critica | A01 | 269 | Risolto |
| 1 | V03 | Domini attaccanti esplicitamente in whitelist CORS | Alta | A05 | 942 | Risolto |
| 1 | V04 | Credenziali hardcoded in `application.properties` | Alta | A02 | 798 | Risolto |
| 1 | V05 | Refresh token loggato in chiaro | Alta | A09 | 532 | Risolto |
| 1 | V06 | Nessun rate limiting su endpoint di autenticazione | Alta | A07 | 307 | Risolto |
| 1 | V07 | Nessuna validazione tipo file negli upload | Alta | A03 | 434 | Risolto |
| 1 | V08 | Exception handler espone `e.getMessage()` | Media | A05 | 209 | Risolto |
| 1 | V09 | TTL identico per access e refresh token (24h entrambi) | Media | A07 | 613 | Risolto |
| 1 | V10 | Nessuna revoca server-side del refresh token | Media | A07 | 613 | Risolto |
| 1 | V11 | Password deboli per utenti di seed (`"pass"`, 4 caratteri) | Media | A07 | 521 | Risolto |
| 1 | V12 | Configurazione CORS duplicata (due bean conflittuali) | Bassa | A05 | 16 | Risolto |
| 1 | V13 | Swagger UI nella whitelist pubblica | Bassa | A05 | 16 | Risolto |
| 1 | V14 | Header `Content-Security-Policy` mancante | Media | A05 | 693 | Risolto |
| 2 | V-01 | Frontend chiama ancora `GET isUserTrue` con credenziali nei params | Alta | A02 | 598 | Risolto |
| 2 | V-02 | Access token salvato in `localStorage` (leggibile da XSS) | Alta | A07 | 614 | Risolto |
| 2 | V-03 | Rate limiter si fida di `X-Forwarded-For` (spoofable) | Alta | A07 | 307 | Risolto |
| 2 | V-04 | Nessuna revoca token al logout (endpoint logout assente) | Alta | A07 | 613 | Risolto |
| 2 | V-05 | JWT claims espongono PII (nome, cognome, telefono) | Media | A02 | 312 | Risolto |
| 2 | V-06 | Validation errors espongono nomi dei campi DTO | Media | A05 | 209 | Risolto |
| 2 | V-07 | `console.log(response)` nell'interceptor Axios | Media | A09 | 532 | Risolto |
| 2 | V-08 | Password: solo lunghezza verificata, nessuna complessità | Media | A07 | 521 | Risolto |
| 2 | V-09 | `RateLimitFilter` usa `ConcurrentHashMap` senza eviction (OOM) | Media | A05 | 770 | Risolto |
| 2 | V-10 | `checkCreditCardNumber()` era uno stub vuoto - qualsiasi numero accettato | Media | A03 | 20 | Risolto |
| 2 | V-11 | `checkCreditCardExpirationDate()` aveva logica invertita e non veniva chiamata | Media | A03 | 20 | Risolto |
| 2 | V-12 | Numero di telefono: regex accettava `0000000000` | Bassa | A03 | 20 | Risolto |
| 2 | V-13 | Swagger accessibile a qualsiasi utente autenticato (non solo ADMIN) | Bassa | A05 | 284 | Risolto |
| 2 | V-14 | Nessun blocco account dopo N tentativi di login falliti | Bassa | A07 | 307 | Risolto |

### 6.2 Patch Applicate

| Round | ID | Patch |
|:-----:|----|-------|
| 1 | **S1-1** | `anyRequest().permitAll()` → policy deny-by-default con RBAC esplicito |
| 1 | **V01** | `GET /isUserTrue?password=...` → `POST` con body JSON; stesso fix su `updatePassword` |
| 1 | **V02** | `@AssertTrue isAuthorityCustomer()` in `SignUpRequest`: rifiuta `authority != CUSTOMER` |
| 1 | **V03** | Rimossi `evil-attacker.com` e `attacker.example.com` da `CorsConfig.ALLOWED_ORIGINS` |
| 1 | **V04** | `application.properties` in `.gitignore`; Docker usa Secrets (`/run/secrets/`) |
| 1 | **V05** | Rimosso `refreshTokenRequest.getToken()` dal `log.info()` in `RefreshTokenController` |
| 1 | **V06** | `RateLimitFilter` con Bucket4j: 10 req/min per IP, HTTP 429 + `Retry-After: 60` |
| 1 | **V07** | Whitelist `Content-Type` in `CarImageServiceImpl` / `UserImageServiceImpl`: non-immagini → 415 |
| 1 | **V08** | `e.getMessage()` → stringa generica statica; stack trace conservato solo nei log server-side |
| 1 | **V09** | Access token: 1h; refresh token: 7 giorni via property separata `refresh-expiration` |
| 1 | **V10** | Tabella `refresh_tokens` con SHA-256; rotazione obbligatoria; theft detection |
| 1 | **V11** | Password seed `"pass"` → `"Seed@1234"`; `@Pattern` per complessità minima |
| 1 | **V12** | Rimosso `addCorsMappings()` da `WebConfig`; unica fonte: `CorsConfig` |
| 1 | **V13** | Swagger spostato da whitelist pubblica a `.hasRole("ADMIN")` |
| 1 | **V14** | `Content-Security-Policy: default-src 'self'; frame-ancestors 'none'` in `SecurityConfig` |
| 2 | **V-01** | `signInService.ts`: da `axiosInstance.get()` con `{params}` a `axiosInstance.post()` con body |
| 2 | **V-02** | `signIn()` imposta cookie `HttpOnly; Secure; SameSite=Strict`; rimosso `localStorage.setItem`; frontend usa `withCredentials: true` |
| 2 | **V-03** | `RateLimitFilter.resolveClientIp()`: `X-Forwarded-For` trusted solo da IP proxy noti (loopback, RFC1918); IP pubblici → `getRemoteAddr()` diretto |
| 2 | **V-04** | `POST /api/v1/auth/logout` aggiunto: chiama `revokeAllForUser()` e scade i cookie con `maxAge=0` |
| 2 | **V-05** | `JwtService.generateToken()`: rimossi `firstname`, `lastname`, `phoneNumber` dal payload JWT |
| 2 | **V-06** | `CustomExceptionHandler`: flag `app.expose-validation-details=false` in produzione → risposta contiene solo `"Validation error"` |
| 2 | **V-07** | `axiosInterceptors.ts`: rimosso `console.log(response)` dall'interceptor |
| 2 | **V-08** | `SignUpRequest.password`: `@Size(min=8)` → `@Pattern` (maiuscola + minuscola + cifra + speciale) |
| 2 | **V-09** | `ConcurrentHashMap<String,Bucket>` → Caffeine `Cache` con `expireAfterAccess(2min)` e `maximumSize(50_000)` |
| 2 | **V-10** | `PaymentRules.checkCreditCardNumber()`: implementato algoritmo Luhn |
| 2 | **V-11** | `checkCreditCardExpirationDate()`: logica corretta (`isBefore` invece di `isAfter`) e agganciata a `checkCreditCard()` |
| 2 | **V-12** | Regex phone: `^[0-9]+$` → `^[1-9][0-9]{9}$` |
| 2 | **V-13** | Swagger paths: da `.authenticated()` a `.hasRole("ADMIN")` |
| 2 | **V-14** | `AccountLockoutService`: blocco account dopo 5 tentativi falliti con reset automatico dopo timeout |

---
## 7. Strumenti di Sicurezza Automatici

Il progetto integra cinque tool di analisi della sicurezza, ciascuno con uno scope diverso:

| Tool | Tipo analisi | Quando gira | Scope |
|------|-------------|-------------|-------|
| **GitGuardian** | Secret scanning | Push/PR (CI) | Working tree corrente |
| **Snyk** | SCA (dipendenze) | CI su ogni push | Librerie Maven (`pom.xml`) + Node (`package.json`) |
| **Semgrep** | SAST (codice sorgente) | CI su ogni push | Codice Java sorgente |
| **SonarCloud** | SAST + Quality Gate + Coverage | CI su ogni push | Java (backend) + TypeScript (frontend) |
| **Trivy** | Container scanning | Deploy (solo tag `v*.*.*`) | Immagine Docker completa (OS + JRE + JAR) |

---

### 7.1 GitGuardian - Secret Detection

GitGuardian scansiona il repository alla ricerca di credenziali hardcoded (API key, token, password) nel codice corrente e nella storia dei commit. Nel workflow CI il job usa `ggshield` in modalità path scan: blocca solo se trova segreti nel codice del branch corrente.

**Finding nel codice corrente:** nessuno. Il CI passa perché `application.properties` è in `.gitignore` e nessuna credenziale è presente nei file committati.

**Finding nella storia dei commit:** GitGuardian ha rilevato segreti in commit storici appartenenti al **fork originale** del progetto (team turco "tobeto", 2023-2024), prima che questo team prendesse il repository.

| Tipo | Valore (parziale) | Commit | Azione |
|------|-------------------|--------|--------|
| Cloudinary API key | `636629149633282` | `be2718a` → `082df8b` | Ignorati (fork originale) |
| Cloudinary API secret | `Hm05tc_JHU...` | `be2718a` → `082df8b` | Ignorati (fork originale) |
| JWT secret (Base64) | `evaVZ4gDLUSMdlY6...` | più commit | Ignorati (fork originale) |
| PostgreSQL password | `14531453`, `123asd123` | commit iniziali | Ignorati (fork originale) |
| AWS RDS endpoint | `tobeto-extendrent.cb48o06...` | commit iniziali | Ignorati (fork originale) |

**Perché ignorati e non FP:** questi segreti sono reali (non placeholder), ma appartengono all'account del team originale, non a questo deployment. Sono stati marcati come **"Ignored - Risk Accepted"** sul dashboard GitGuardian con la motivazione "credenziali del fork originale, non di questo team". La riscrittura della storia git (`git filter-repo`) è stata valutata ma scartata: i segreti appartengono a un account terzo e sono presumibilmente già scaduti/ruotati; la riscrittura forzata richiederebbe un force push e il re-clone da parte di tutti i collaboratori.

---

### 7.2 Snyk - Software Composition Analysis (SCA)

Snyk analizza le dipendenze Maven del backend e le dipendenze Node del frontend, confrontandole con il database di CVE. Nel branch analizzato erano presenti **12 alert** (5 High, 4 Medium, 3 Low). Tutti risolti.

**Analisi eseguita su:** branch `master-dev` - data: 2026-05-06 / aggiornamento: 2026-05-07

**Fix principale:** upgrade Spring Boot `3.5.13 → 3.5.14` - risolve 8 alert tramite aggiornamento transitivo di `spring-boot`, `spring-boot-autoconfigure`, `spring-web`, `spring-webmvc` alle versioni patchate.

| Alert | Pacchetto | Severità | CWE | Fix | Note |
|-------|-----------|----------|-----|-----|------|
| #68 | `springfox-swagger2` | Medium | CWE-20 | Dipendenza rimossa | Sostituita con springdoc |
| #69 | `springfox-swagger-ui` | Medium | CWE-79 (XSS) | Dipendenza rimossa | Non mantenuta dal 2021 |
| #70 | `postgresql` JDBC | High | CWE-770 | Pin `42.7.11` | Aggiunto `fetch_size=100` come defense-in-depth |
| #71 | `spring-web` | High | CWE-459 | Spring Boot 3.5.14 | WebFlux non usato; impatto ridotto |
| #72 | `spring-core` | Medium | CWE-770 | Spring Boot 3.5.14 + `JacksonConfig` | `StreamReadConstraints` aggiunto come defense-in-depth |
| #73 | `spring-boot-devtools` | High | CWE-208 (Timing) | Dipendenza rimossa | DevTools non appartiene a produzione |
| #74 | `spring-boot` | High | CWE-338 (Weak PRNG) | Spring Boot 3.5.14 | Il codice usava già `UUID.randomUUID()` (SecureRandom) |
| #75 | `spring-boot` | High | CWE-377 (Insecure Temp) | Spring Boot 3.5.14 + tmpfs | `/tmp` montato `noexec,nosuid` in docker-compose |
| #76 | `spring-boot` | Medium | CWE-61 (Symlink) | Spring Boot 3.5.14 + tmpfs | **Non sfruttabile**: nessun `ApplicationPidFileWriter` configurato |
| #77 | `spring-webmvc` | Low | CWE-444 (HTTP Smuggling) | Spring Boot 3.5.14 | **Non sfruttabile**: resource chain non configurata nel progetto |
| #78 | `spring-boot-autoconfigure` | Low | CWE-297 (Cert Mismatch) | Spring Boot 3.5.14 | **Non sfruttabile**: nessuna dipendenza Cassandra |
| #79 | `spring-boot-autoconfigure` | Low | CWE-297 (Cert Mismatch) | Spring Boot 3.5.14 + `checkserveridentity=true` | Aggiunto SSL property SMTP come defense-in-depth |

**Alert con impatto nullo (non FP ma non sfruttabili):**

- **#76 Symlink Attack:** richiede l'uso di `ApplicationPidFileWriter`. Questo progetto non configura nessun listener PID né in `application*.properties` né nel codice. Il fix tramite upgrade è applicato comunque perché porta il codice all'ultima versione sicura.
- **#77 HTTP Request Smuggling:** richiede resource chain caching abilitata + encoded resource resolution configurata. `WebConfig.java` non chiama `configurer.resourceChain()`. Il progetto non soddisfa nessuna delle tre condizioni necessarie.
- **#78 Cert Host Mismatch (Cassandra):** richiede `spring-boot-starter-data-cassandra` come dipendenza. Non presente nel `pom.xml`.

---

### 7.3 Semgrep - SAST Pattern-Based

Semgrep ha rilevato **4 finding** (tutti nello stesso file di regola: `java.lang.security.audit.active-debug-code-printstacktrace`, CWE-209 / CWE-489). Nessun falso positivo.

Il problema comune era l'uso di `e.printStackTrace()` al posto di un logger strutturato. `e.printStackTrace()` scrive l'intero stack trace su `System.err`, che in produzione viene catturato dal container Docker e finisce nei log del server. Uno stack trace espone nomi delle classi interne, numeri di riga, versioni delle librerie terze e sequenze di chiamate - informazioni che abbassano il costo di un attacco successivo.

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

1. **SARIF completo** - scansiona tutte le severità (`exit-code: 0`), carica su GitHub → Security → Code scanning (visibile anche su finding LOW)
2. **Upload SARIF** - `github/codeql-action/upload-sarif` rende i risultati navigabili nella Security tab
3. **Gate CRITICAL/HIGH** - scansione con `exit-code: 1`, `ignore-unfixed: true`, `severity: CRITICAL,HIGH` - blocca il deploy se trova CVE gravi con fix disponibile

Il flag `--ignore-unfixed` è una scelta deliberata: CVE senza fix upstream non possono essere risolti aggiornando le dipendenze, quindi bloccare il deploy su di essi sarebbe un gate permanentemente rosso indipendentemente dalle azioni del team.

**Riduzione della superficie Trivy ottenuta con scelte Dockerfile:**
- Backend: `eclipse-temurin:17-jre` invece del JDK riduce il numero di pacchetti OS inclusi
- Frontend: `nginx:1.27-alpine` + `apk upgrade` aggiorna tutti i pacchetti Alpine alla versione con fix disponibile al momento della build

**CVE in immagine base - decisione VEX:**

Nel corso del progetto sono state rilevate CVE in `stdlib@1.26.2` e `golang.org/x/net@0.40.0`, pacchetti Go bundled nel binario `pebble` (process manager) dell'immagine base `eclipse-temurin:17-jre`. Queste CVE non appartengono al codice Java del progetto.

**Perché Ubuntu 26.04 e non 24.04:**

`eclipse-temurin:17-jre` usa Ubuntu 26.04 come base OS - non è una scelta del progetto, è la scelta di Eclipse Temurin. Ubuntu 26.04 include pacchetti di sistema più recenti rispetto a 24.04, alcuni dei quali (tool di sistema moderni) sono scritti in Go e distribuiti come binari precompilati dentro pacchetti `.deb`. Quando quei pacchetti vengono installati nell'immagine, il binario Go compilato finisce nell'immagine stessa. Trivy legge la versione Go con cui quel binario è stato compilato (`1.26.2`) e segnala le CVE note per quella versione. Il progetto non controlla né la scelta di Ubuntu 26.04 né la versione Go usata per compilare quei binari - entrambe dipendono da Eclipse Temurin e da Canonical.

L'approccio VEX (Vulnerability Exploitability eXchange) per sopprimere queste CVE è stato valutato e scartato per tre motivi:
- **PURL instabile:** ogni build produce un nuovo digest dell'immagine - nessun PURL stabile da inserire nel documento VEX
- **Soppressione troppo ampia:** referenziare `stdlib` o `net` come prodotto sopprimerebbe quelle versioni ovunque nell'immagine, non solo dentro `pebble`
- **Manutenzione insostenibile:** ogni aggiornamento della base image richiederebbe aggiornamento manuale del VEX

**Soluzione adottata:** `--ignore-unfixed` nel gate (CVE in `pebble` non hanno fix disponibile nell'immagine base) - semanticamente corretto perché il problema è nella base, la soppressione è sulla base. Quando `eclipse-temurin:17-jre` aggiornerà `pebble` con versioni Go patchate, le CVE spariranno automaticamente dal report.

---

## 8. Test di Sicurezza

La strategia di testing adottata è **incentrata sulla sicurezza**, non sulla correttezza funzionale: il 95% della suite (403 test su 424) verifica il comportamento del sistema in scenari di attacco o accesso non autorizzato, seguendo la tassonomia **OWASP Top 10 2021**. La suite è completamente indipendente dall'ambiente di produzione - usa H2 in-memory per il web layer, non effettua chiamate a Cloudinary o SMTP, e il rate limiting è disabilitabile via `app.rate-limit.enabled=false` nel profilo `test` - garantendo che ogni test sia riproducibile senza configurazione esterna.

I test sono organizzati in **43 classi** nel package `com.extendrent.security`, strutturate per area OWASP. I 403 test di sicurezza includono 355 metodi `@Test` e 13 metodi `@ParameterizedTest` che generano **48 istanze** aggiuntive - un'unica definizione di test esegue automaticamente l'intera lista di payload malevoli (SQL injection, XSS, path traversal, password deboli, numeri di carta non validi). Ai test di sicurezza si aggiungono 21 test funzionali su controller specifici (`DiscountControllerTest`, `PaymentDetailControllerTest`, `PaymentTypeControllerTest`, `RentalControllerTest`, `ApplicationTests`), per un totale di **424 test**.

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

In CI i job `security-tests` e `test` girano **in parallelo** su GitHub Actions dopo il gate `compile`.

---

### 8.1 Broken Access Control (RBAC) - A01

Broken Access Control è la vulnerabilità al primo posto nell'OWASP Top 10 2021. In ExtendRent il rischio è duplice: endpoint che rispondono a ruoli non autorizzati (escalation verticale) e utenti che accedono a risorse altrui (IDOR, autorizzazione orizzontale). Tutta la filter chain di Spring Security - configurata in `SecurityConfig` con policy deny-by-default - viene verificata endpoint per endpoint, ruolo per ruolo, con MockMvc che simula richieste autenticate tramite `SecurityMockMvcRequestPostProcessors.jwt()`. Ogni controller ha una classe di test dedicata che copre sistematicamente tutti i metodi HTTP esposti.

[**`SecurityFilterChainTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SecurityFilterChainTest.java)

> Tutti gli endpoint del sistema

Usa MockMvc per colpire ogni endpoint del sistema con ciascun ruolo (Admin, Employee, Customer) e senza autenticazione. Verifica che gli endpoint pubblici (`/auth/**`, `/swagger-ui/**` solo per Admin) rispondano correttamente e che tutti gli altri endpoint restituiscano 401 o 403 per accessi non autorizzati. È il test più ampio: copre l'intera filter chain configurata in `SecurityConfig`.

---

[**`RoleEscalationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RoleEscalationSecurityTest.java)

> `POST /auth/register`

Il test invia una richiesta di registrazione con il campo `authority: ADMIN` nel body - un valore che un utente può inserire manualmente modificando la richiesta. Il ruolo viene determinato lato server, indipendentemente da ciò che il client invia.

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

Tutti gli endpoint CRUD del ciclo noleggio sono riservati ad Admin. Customer ed Employee non possono accedere né in lettura né in scrittura - il noleggio è gestito solo lato backoffice.

| Unauthenticated | Customer | Employee | Admin |
|:-:|:-:|:-:|:-:|
| 401 | 403 | 403 | Sì |

---

[**`UserControllerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/UserControllerSecurityTest.java)

> `PUT /api/v1/user/{id}/password`

Verifica la protezione contro IDOR (Insecure Direct Object Reference): ogni utente può modificare solo la propria password. Il controllo confronta l'ID nel path parameter con quello estratto dal JWT nel `SecurityContext` - se non coincidono, la richiesta viene rifiutata con 403.

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

Testa il servizio JWT in isolamento (senza Spring context): generazione con HMAC-SHA256, parsing e validazione standard, e resistenza a due attacchi noti. L'attacco `alg:none` bypassa la firma impostando l'header JWT a `"none"` - il sistema deve rifiutarlo. Il key confusion attack usa la chiave pubblica come segreto HMAC per ingannare il server che si aspetta HMAC-SHA256.

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

Decodifica il payload JWT emesso al login e verifica che non contenga dati personali sensibili. I claim PII erano stati aggiunti originariamente per evitare query aggiuntive al database; rimossi perché il JWT viaggia nel cookie e può essere intercettato o loggato - i dati personali non devono essere leggibili al di fuori del database.

| Claim | Status |
|-------|--------|
| `sub` (user ID) | presente |
| `firstname`, `lastname`, `phoneNumber` | rimossi |

**Outcome:** V05 - nessun dato personale (PII) nel payload JWT.

---

[**`LogoutSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/LogoutSecurityTest.java)

> `POST /api/v1/auth/logout`

Simula un flusso di logout completo: autentica un utente, usa il token per una richiesta protetta (atteso 200), chiama `/auth/logout`, poi ritenta la stessa richiesta con il token appena revocato - deve rispondere 401.

| Scenario | Comportamento |
|----------|--------------|
| Token valido prima del logout | accettato |
| Stesso token dopo logout | 401 - revocato |

**Outcome:** V-04 - logout revoca il token tramite `revokeAllForUser()` e scade i cookie con `maxAge=0`.

---

[**`RefreshTokenRevocationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenRevocationSecurityTest.java)

> `POST /api/v1/auth/logout`

Verifica che al logout il refresh token venga effettivamente invalidato nella tabella `refresh_tokens` (hashing SHA-256) e che un successivo tentativo di rinnovo con lo stesso token venga rifiutato. La rotazione obbligatoria e la theft detection (V10) fanno sì che ogni token sia usabile una sola volta - il riuso rivela un possibile furto.

---

[**`RefreshTokenLoggingSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenLoggingSecurityTest.java)

Verifica che il valore del refresh token non appaia mai in nessuna riga di log. Prima della patch V05, `RefreshTokenController` stampava `log.info("...", refreshTokenRequest.getToken())` - il token avrebbe potuto finire nei log aggregati accessibili a operatori. Il test controlla che nessun pattern riconducibile al token sia presente nell'output del logger.

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

Il rate limiting è la prima linea di difesa contro gli attacchi brute-force sugli endpoint di autenticazione. Senza un limite, un attaccante può tentare migliaia di combinazioni al secondo fino a compromettere un account. ExtendRent implementa un token-bucket per IP con Caffeine come backing cache, affiancato da un lockout esplicito dopo cinque tentativi falliti consecutivi. I test verificano entrambi i meccanismi - in isolamento e in integrazione con il filtro HTTP - e la corretta risoluzione dell'IP client dietro reverse proxy, impedendo che l'header `X-Forwarded-For` venga falsificato per aggirare il rate limiter.

[**`RateLimitingSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitingSecurityTest.java) · [**`RateLimitingBehaviorTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitingBehaviorTest.java)

> `POST /auth/**`

Il test simula un attacco brute-force: invia 10 richieste in rapida successione verificando che tutte abbiano successo, poi invia l'undicesima - che deve ricevere 429 con `Retry-After: 60`. `RateLimitingBehaviorTest` verifica la logica del token-bucket in isolamento; `RateLimitingSecurityTest` la integrazione con il filtro HTTP.

| Scenario | Comportamento |
|----------|--------------|
| Richieste entro soglia (≤10/min per IP) | 200 |
| Richieste oltre soglia | 429 + `Retry-After: 60` |

**Outcome:** V06/V-09 - token-bucket rate limiter su `/auth/**`; `ConcurrentHashMap` → Caffeine `Cache` con `expireAfterAccess(2min)` e `maximumSize(50_000)`.

---

[**`RateLimitXForwardedForSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RateLimitXForwardedForSecurityTest.java)

> Header `X-Forwarded-For`

Verifica che il rate limiter identifichi correttamente l'IP del client anche dietro un reverse proxy. Se la richiesta arriva da un IP noto (loopback o RFC1918), il rate limiter legge l'IP reale da `X-Forwarded-For`; altrimenti usa `getRemoteAddr()` - impedendo a un client diretto di falsificare l'header per aggirare il rate limiting.

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

Il test registra un utente, simula cinque tentativi di login con password errata e verifica che il sesto - anche con la password corretta - sia rifiutato. Verifica inoltre che il lockout abbia una scadenza temporale e che l'account si sblocchi automaticamente dopo il TTL configurato in `AccountLockoutService`.

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

`CorsSecurityTest` invia richieste preflight dalle origini whitelistate e verifica che `Access-Control-Allow-Origin` sia presente. `CorsAttackerDomainSecurityTest` usa `evil-attacker.com` e `attacker.example.com` - domini inclusi per errore nella configurazione originale - e verifica che vengano rifiutati con header CORS assente.

| Origine | Comportamento |
|---------|--------------|
| Whitelistata (origini legittime) | Sì |
| `evil-attacker.com` / `attacker.example.com` | rifiutata |

---

[**`CorsConfigDuplicationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/CorsConfigDuplicationSecurityTest.java)

Prima della patch V12, `WebConfig.addCorsMappings()` e `CorsConfig` coesistevano come due bean CORS distinti con regole potenzialmente conflittuali. Il test verifica che il bean `CorsFilter` sia registrato una sola volta e che non esista alcun `WebMvcConfigurer` che configuri regole CORS parallele - due sorgenti CORS portano a comportamenti imprevedibili a seconda dell'ordine di applicazione dei filtri.

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

Prima della patch, Swagger era accessibile a qualsiasi utente autenticato - un Customer poteva esplorare l'intera API, incluse le route Admin. La patch V13 ha ristretto l'accesso al solo ruolo `ADMIN`. Il test colpisce entrambi gli endpoint con tutti e quattro i livelli di accesso.

| Unauthenticated | Customer | Employee | Admin |
|:-:|:-:|:-:|:-:|
| 403 | 403 | 403 | Sì |

**Outcome:** V13/V-13 - Swagger spostato a `.hasRole("ADMIN")`.

---

[**`SwaggerProdAccessSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/SwaggerProdAccessSecurityTest.java)

Con il profilo Spring `prod` attivo, verifica che `/swagger-ui/**` e `/v3/api-docs/**` siano completamente inaccessibili - restituiscono 404 o vengono esclusi dalla filter chain. Swagger non deve essere esposto in produzione nemmeno al ruolo Admin: l'endpoint documenta l'intera API e ne facilita l'esplorazione da parte di un attaccante.

---

[**`GenericExceptionHandlerSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/GenericExceptionHandlerSecurityTest.java)

Provoca intenzionalmente un errore interno (input malformato che raggiunge il servizio) e verifica che la risposta HTTP non contenga stack trace, nomi di classi interne, numeri di riga o l'output di `e.getMessage()`. Prima della patch, il `CustomExceptionHandler` restituiva il messaggio raw dell'eccezione - informazioni preziose per un attaccante nel pianificare exploit successivi.

**Outcome:** V-08 - HTTP 500 non espone stack trace né dettagli interni; `e.getMessage()` sostituito con stringa generica statica.

---

[**`ValidationErrorExposureSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/ValidationErrorExposureSecurityTest.java)

Con profilo `prod` e flag `app.expose-validation-details=false`, verifica che gli errori di validazione bean restituiscano solo `"Validation error"` senza esporre i nomi dei campi DTO, i valori rifiutati o i messaggi del validator. Il comportamento è diverso in `test`/`dev` dove i dettagli sono visibili per facilitare il debugging.

**Outcome:** V-06 - `CustomExceptionHandler` con flag `app.expose-validation-details=false` in produzione → risposta contiene solo `"Validation error"`.

---

[**`HttpOnlyCookieSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/HttpOnlyCookieSecurityTest.java)

Verifica i flag del cookie di sessione impostato da `signIn()`: `HttpOnly` impedisce l'accesso da JavaScript (XSS non può estrarre il token), `Secure` limita la trasmissione a HTTPS, `SameSite=Strict` blocca le richieste cross-origin automatiche (CSRF). Prima della patch V-02, il token era salvato in `localStorage` - accessibile a qualsiasi script in pagina.

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

Invia file con `Content-Type` non in whitelist (PDF, testo, binario generico) all'endpoint di upload immagini e verifica che la risposta sia 415 Unsupported Media Type. Prima della patch V07, qualsiasi file veniva accettato e caricato su Cloudinary - un attaccante avrebbe potuto caricare script, eseguibili o file di configurazione.

**Outcome:** V-07/CWE-434 - whitelist `Content-Type` in `CarImageServiceImpl` / `UserImageServiceImpl`; file non-immagine → 415.

---

[**`PaymentValidationSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/PaymentValidationSecurityTest.java)

> Endpoint pagamento

Verifica l'algoritmo di Luhn implementato in `PaymentRules`. Quattro numeri di carta con checksum Luhn corretto devono superare la validazione; quattro con checksum errato devono essere rifiutati. Prima della patch, qualsiasi stringa numerica veniva accettata come numero di carta valido - mancava qualsiasi controllo sul formato.

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

**Outcome:** CWE-798 - nessuna credenziale hardcodata nel sorgente corrente; `application.properties` in `.gitignore`; Docker usa Secrets (`/run/secrets/`).

---

### 8.7 Logging & Monitoring - A09

Le due classi di questa sezione compaiono anche in 8.2 (JWT & Token Management): rientrano sia nella gestione dei token sia nel monitoraggio. Sono conteggiate una sola volta nel totale di 403 test.

I sistemi di log aggregato (ELK, CloudWatch, Loki) sono spesso accessibili a un numero più ampio di operatori rispetto al database applicativo. Un refresh token nei log equivale a un token esposto: chiunque legga i log può impersonare l'utente fino alla sua scadenza. I test in questa categoria verificano due proprietà complementari: che il ciclo di refresh non lasci il token in chiaro in nessuna riga di log (CWE-532), e che il logout invalidi il token nel database prima di restituire la risposta al client - rendendo inutilizzabile anche un token eventualmente esfiltrato dai log.

[**`RefreshTokenLoggingSecurityTest`**](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/src/test/java/com/extendrent/security/RefreshTokenLoggingSecurityTest.java)

Cattura l'output del logger durante un ciclo di refresh e verifica che nessuna riga contenga pattern riconducibili al token - né il valore raw né una sua sottostringa. Prima della patch, `RefreshTokenController` stampava `log.info("...", refreshTokenRequest.getToken())`: il token avrebbe potuto finire nei log aggregati accessibili agli operatori.

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

La pipeline è strutturata su due livelli distinti: un livello root che governa l'intero monorepo (backend + frontend) e un livello dedicato al solo backend con una pipeline più granulare. I due livelli sono indipendenti e si attivano su branch diversi, ma condividono gli stessi strumenti di sicurezza.

### 9.1 Livello Root ([`.github/workflows/`](https://github.com/aaselli2-unisa/rentACar_backend/tree/master/.github/workflows))

Il livello root si attiva su push e PR verso `main`/`develop` e copre contemporaneamente backend e frontend. È composto da due workflow separati con responsabilità distinte.

**[`ci.yml`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/.github/workflows/ci.yml)** - Build e test (2 job paralleli):

I due job girano in parallelo perché sono completamente indipendenti: non si scambiano artefatti e usano runner separati. Il backend usa Maven per compilare e lanciare i 424 test; il frontend usa `npm ci` (installazione riproducibile da lockfile) e `npm run build` per verificare che il bundle TypeScript/React compili senza errori.

| Job | Cosa fa |
|-----|---------|
| `build-backend` | `mvn -B package` nella directory `rentACar_backend/` - compila + esegue tutti i 424 test |
| `build-frontend` | `npm ci && npm run build` nella directory `rent-a-car-frontend-project/` |

**[`security.yml`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/.github/workflows/security.yml)** - Security scanning (si attiva anche ogni lunedì 06:00 UTC tramite cron):

Il workflow di sicurezza è separato dal CI per due motivi: può girare su schedule indipendente (cron settimanale per rilevare CVE nuove anche senza push), e i suoi job sono più lenti e non devono bloccare la fase di build/test. GitGuardian usa `fetch-depth: 0` per analizzare l'intera storia dei commit, non solo il diff del push. Snyk e Semgrep bloccano su finding HIGH/CRITICAL; SonarCloud calcola coverage e quality gate per entrambi i layer.

| Job | Tool | Gate |
|-----|------|------|
| `gitguardian` | ggshield (full history `fetch-depth: 0`) | Blocca su segreti nel codice corrente |
| `semgrep` | Semgrep OSS `config auto` | Blocca su finding attivi |
| `snyk-backend` | Snyk Maven (`--severity-threshold=high`) | Blocca su HIGH/CRITICAL |
| `snyk-frontend` | Snyk Node (`--severity-threshold=high`) | Blocca su HIGH/CRITICAL |
| `sonarcloud-backend` | SonarCloud `mvn verify sonar:sonar` | Quality gate + coverage Java |
| `sonarcloud-frontend` | SonarCloud `sonarcloud-github-action` | Quality gate + coverage TypeScript |

### 9.2 Livello Backend ([`rentACar_backend/.github/workflows/`](https://github.com/aaselli2-unisa/rentACar_backend/tree/master/rentACar_backend/.github/workflows))

Il backend ha una pipeline propria, più granulare, che si attiva su push/PR verso `master-dev`/`master`. È composta da 7 job con dipendenze esplicite. Il DAG è pensato per minimizzare il tempo di feedback: `gitguardian` gira subito in parallelo a tutto il resto (non ha bisogno del codice compilato), `compile` è il gate che sblocca tutti i job successivi.

```
gitguardian ─────────────────────────── (parallelo a compile, nessuna dipendenza)

compile ──┬── security-tests ── sonarcloud
          ├── snyk
          ├── semgrep
          └── docker-validate
```

**[`ci.yml`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/.github/workflows/ci.yml)** - Dettaglio dei job:

`compile` è il gate iniziale: se Maven non compila, tutti i job downstream vengono saltati immediatamente senza consumare minuti runner. `security-tests` lancia i 403 test di sicurezza e salva il report Surefire XML come artefatto GitHub - `sonarcloud` lo scarica per calcolare la coverage senza rieseguire i test. `snyk` carica i risultati come SARIF nella Security tab di GitHub e poi esegue un secondo scan con `exit-code: 1` che blocca la CI se trova CVE HIGH/CRITICAL con fix disponibile. `docker-validate` builda l'immagine con BuildKit senza fare push (`push: false`) - verifica che il Dockerfile sia sintatticamente corretto e che tutte le dipendenze OS siano risolvibili.

Il job `sonarcloud` usa `sonar.qualitygate.wait=false`: il job Maven completa non appena i dati vengono inviati a SonarCloud, senza aspettare che il Quality Gate venga calcolato. Il risultato appare nel dashboard SonarCloud in modo asincrono e non blocca il merge direttamente - scelta deliberata per non allungare il tempo di CI per analisi che impiegano 2-5 minuti.

| Job | Timeout | Tool | Gate |
|-----|---------|------|------|
| `gitguardian` | 10 min | ggshield path scan (working tree) | Blocca su segreti nel codice attuale |
| `compile` | 10 min | Maven | Gate iniziale - skip downstream se fallisce |
| `security-tests` | 20 min | JUnit 5 + Surefire | 403 test sicurezza; artifact Surefire → SonarCloud |
| `snyk` | 15 min | Snyk CLI (SARIF + gate separato) | SARIF → Security tab; gate blocca HIGH/CRITICAL upgradable |
| `semgrep` | 10 min | Semgrep `semgrep ci` con APP_TOKEN | Blocca in base alla policy del dashboard Semgrep |
| `docker-validate` | 15 min | BuildKit `push: false` | Blocca se Dockerfile non compila |
| `sonarcloud` | 15 min | SonarCloud `sonar.qualitygate.wait=false` | Analisi asincrona - non blocca la CI; risultati nel dashboard |

**[`deploy.yml`](https://github.com/aaselli2-unisa/rentACar_backend/blob/master/rentACar_backend/.github/workflows/deploy.yml)** - Su push di tag `v*.*.*` o `workflow_dispatch` manuale:

Il deploy è intenzionalmente separato dalla CI: si attiva solo con un tag Git esplicito (`v*.*.*`), non ad ogni push su `master`. Questo garantisce che il passaggio in produzione sia una decisione consapevole. Il job `publish` esegue tre step sequenziali: prima builda e pusha l'immagine su GHCR, poi esegue due scan Trivy separati - uno completo (SARIF per la Security tab, senza blocco) e uno gate che blocca solo su CRITICAL/HIGH con fix disponibile (`--ignore-unfixed`).

| Step | Cosa fa |
|------|---------|
| Build + push | Builda l'immagine con BuildKit (cache GHA), push su `ghcr.io` con tag `x.y.z` + `latest` |
| Trivy SARIF | Scansiona tutte le severità (`exit-code: 0`), carica risultati su GitHub Security tab |
| Trivy gate | Blocca solo su CRITICAL/HIGH con fix disponibile (`ignore-unfixed: true`, `exit-code: 1`) |

### 9.3 Sicurezza della Pipeline

Ogni workflow applica il principio del minimo privilegio: i permessi sono dichiarati globalmente come `contents: read` e i job che necessitano di privilegi aggiuntivi (upload SARIF, push su GHCR) li dichiarano esplicitamente a livello di job. Tutti i job hanno `timeout-minutes` espliciti - senza timeout, un job appeso consumerebbe minuti runner illimitati e potrebbe bloccare altri workflow. I secret non compaiono mai nel codice: vengono letti dai GitHub Secrets e iniettati come variabili d'ambiente dal runner.

| Pratica | Implementazione |
|---------|-----------------|
| Permessi minimi | `permissions: contents: read` globale; permessi aggiuntivi dichiarati per-job (`security-events: write`, `packages: write`) |
| Timeout espliciti | Tutti i job hanno `timeout-minutes` - nessun job può bloccare la pipeline indefinitamente |
| Deploy separato dal CI | `deploy.yml` si attiva su push tag `v*.*.*` - il deploy di produzione richiede una decisione esplicita (git tag), non è automatico ad ogni push |
| Nessun secret nel codice | `GITHUB_TOKEN` come variabile nativa GitHub; `SNYK_TOKEN`, `SEMGREP_APP_TOKEN`, `GITGUARDIAN_API_KEY`, `SONAR_TOKEN` come GitHub Secrets |

### 9.4 Segreti Pipeline

| Nome | Pipeline | Utilizzo |
|------|:--------:|---------|
| `GITGUARDIAN_API_KEY` | Entrambe | ggshield auth |
| `SNYK_TOKEN` | Entrambe | Snyk CLI |
| `SEMGREP_APP_TOKEN` | Entrambe | Semgrep regole |
| `SONAR_TOKEN_BACKEND` | Root | SonarCloud backend |
| `SONAR_TOKEN_FRONTEND` | Root | SonarCloud frontend |
| `SONAR_TOKEN` | Backend | SonarCloud pipeline dedicata |
| `GITHUB_TOKEN` | Backend CD | GHCR + SARIF Trivy |
| `SONAR_ORGANIZATION` | Backend | Organizzazione SonarCloud |
| `SONAR_PROJECT_KEY` | Backend | Chiave progetto SonarCloud |

---

## 10. Kubernetes

ExtendRent è stato deployato su un server Ubuntu con **microk8s** (Kubernetes single-node). Il deployment è completamente operativo.

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

Il backend non è mai esposto direttamente a internet. Tutto il traffico esterno passa per il frontend nginx, che agisce da reverse proxy per `/api/`.

### 10.2 Setup microk8s

```bash
sudo snap install microk8s --classic --channel=1.32/stable
sudo usermod -aG microk8s $USER
newgrp microk8s

microk8s enable dns
microk8s enable storage
microk8s enable ingress
```

| Addon | Funzione |
|-------|----------|
| `dns` | CoreDNS: i pod si raggiungono per nome (`postgres`, `rentacar-app`) - service discovery interna |
| `storage` | hostpath-provisioner: soddisfa PVC mappandole su directory locali del nodo (dati PostgreSQL) |
| `ingress` | nginx-ingress-controller nel namespace `ingress`: riceve traffico :80 e instrada ai Service K8s |

### 10.3 Secrets

I segreti K8s sono separati dai manifest (che sono su git). Ogni credenziale è un file in `secrets/` (`.gitignore`):

```
secrets/
  DB_PASSWORD          # password PostgreSQL
  JWT_SECRET
  CLOUDINARY_CLOUD_NAME
  CLOUDINARY_API_KEY
  CLOUDINARY_API_SECRET
  MAIL_USERNAME
  MAIL_PASSWORD
```

> **Critico - formato secrets:** usare sempre `echo -n` (senza newline finale). Un newline in coda causa autenticazioni fallite perché la password viene letta come `password\n`.

```bash
echo -n "valore_secret" > secrets/NOME_SECRET
bash k8s/setup-secrets.sh   # crea K8s Secret "rentacar-secrets" nel namespace rentacar
```

Il Secret viene montato nei pod come volume: K8s crea un file per ogni chiave in `/run/secrets/`. Spring Boot con profilo `docker` legge le credenziali da questi file.

### 10.4 Build e Push Immagini

K8s non builda immagini: le scarica da un registry. Le immagini vengono buildate e pushate su GHCR (`ghcr.io`) tramite `deploy.yml` al push di un tag versione.

```bash
# Deploy manuale da workflow_dispatch (GitHub Actions UI)
# oppure tramite tag git:
git tag v1.2.3 && git push origin v1.2.3
```

### 10.5 Manifest Kubernetes

Lo script `k8s/deploy.sh` applica i manifest in ordine numerico (l'ordine è importante):

| File | Risorsa | Perché prima |
|------|---------|--------------|
| `00-namespace.yaml` | Namespace `rentacar` | Tutti gli altri oggetti appartengono a questo namespace |
| `01-configmap.yaml` | ConfigMap `rentacar-config` | I Deployment referenziano il ConfigMap via `envFrom` - deve esistere prima |
| `02-postgres.yaml` | PVC + Deployment + Service postgres | Il pod Spring Boot usa un initContainer che aspetta postgres |
| `03-app.yaml` | Deployment + Service Spring Boot | initContainer blocca fino a postgres:5432 disponibile |
| `04-frontend.yaml` | Deployment + Service nginx | Si avvia subito; le call API aspettano Spring Boot |
| `04b-nginx-config.yaml` | ConfigMap nginx | Contiene `nginx.conf` con `listen 8080` e proxy `/api/` |
| `05-ingress.yaml` | Ingress | Il Service `rentacar-frontend` deve esistere già |
| `06-networkpolicy.yaml` | 3 NetworkPolicy | Firewall tra componenti |

**Dettagli manifest rilevanti per sicurezza:**

**`03-app.yaml` (Spring Boot):**
- `automountServiceAccountToken: false` - Spring Boot non usa l'API K8s; il mount automatico del token causava errori `read-only file system` con il filesystem restrittivo dell'immagine
- `securityContext`: `runAsUser: 10001` (appuser), `allowPrivilegeEscalation: false`, `drop: ["ALL"]`, `seccompProfile: RuntimeDefault`
- `initContainer wait-for-postgres`: usa `nc -z postgres 5432` in loop - K8s non avvia Spring Boot finché PostgreSQL non è raggiungibile
- `initialDelaySeconds: 60` nelle probe: la JVM con Spring Boot impiega 30-90s per avviarsi

**`04-frontend.yaml` (nginx):**
- nginx con `runAsNonRoot: true` + `runAsUser: 101` non riesce a fare `bind()` su porta 80 (porta privilegiata) → **nginx configurato su porta 8080** tramite ConfigMap (`04b-nginx-config.yaml`)
- Volume `emptyDir` su `/var/cache/nginx` e `/var/run` - K8s alloca filesystem temporaneo scrivibile (nginx scrive file temporanei lì senza problemi di permessi)
- Il Service mappa `port: 80 → targetPort: 8080`, quindi dall'esterno si vede sempre porta 80

**`06-networkpolicy.yaml` - NetworkPolicy (whitelist):**

| Pod | Può ricevere traffico da | Porta |
|-----|--------------------------|-------|
| `postgres` | solo `rentacar-app` | 5432 |
| `rentacar-app` | solo `rentacar-frontend` | 8080 |
| `rentacar-frontend` | solo ingress-controller (namespace `ingress`) | 8080 |

L'egress (traffico in uscita) è lasciato libero: Spring Boot deve raggiungere Gmail SMTP e Cloudinary, i cui IP cambiano e non è pratico bloccarli per indirizzo.

### 10.6 Aggiornamenti

```bash
# Rollout di una nuova immagine (già pushata su GHCR)
microk8s kubectl rollout restart deployment/rentacar-app -n rentacar
microk8s kubectl rollout restart deployment/rentacar-frontend -n rentacar
microk8s kubectl rollout status deployment/rentacar-app -n rentacar

# Aggiornare un manifest YAML
microk8s kubectl apply -f k8s/NOME_FILE.yaml

# Aggiornare i secrets
bash ~/rentacar/k8s/setup-secrets.sh
microk8s kubectl rollout restart deployment/rentacar-app -n rentacar
```

### 10.7 Comandi utili di diagnostica

```bash
# Stato generale
microk8s kubectl get pods -n rentacar
microk8s kubectl get services -n rentacar
microk8s kubectl get ingress -n rentacar

# Log in tempo reale
microk8s kubectl logs -f -n rentacar -l app=rentacar-app
microk8s kubectl logs -f -n rentacar -l app=rentacar-frontend

# Log del pod precedente (dopo crash)
microk8s kubectl logs -n rentacar <nome-pod> --previous

# Entrare nel pod Spring Boot
microk8s kubectl exec -it deployment/rentacar-app -n rentacar -- sh

# Entrare in postgres
microk8s kubectl exec -it deployment/postgres -n rentacar -- \
  psql -U postgres -d rentacar

# Descrivere un pod (eventi, stato, volumi montati)
microk8s kubectl describe pod <nome-pod> -n rentacar
```

### 10.8 Problemi Riscontrati e Soluzioni

| Problema | Causa | Fix |
|----------|-------|-----|
| `x509: certificate is valid for 192.168.1.59, not 192.168.1.60` | IP LAN riassegnato da DHCP - certificato kubelet generato con vecchio IP | `sudo microk8s refresh-certs --cert ca.crt`; fix permanente: IP statico tramite netplan |
| `mkdir "/var/cache/nginx/client_temp" failed (13: Permission denied)` | `runAsUser: 101` + directory owned da root nell'immagine | Volume `emptyDir` montato su `/var/cache/nginx` e `/var/run` |
| `bind() to 0.0.0.0:80 failed (13: Permission denied)` | `NET_BIND_SERVICE` non applicata con `allowPrivilegeEscalation: false` + `drop: ALL` | nginx configurato su porta 8080 tramite ConfigMap |
| `read-only file system` su service account token | K8s monta token API in `/var/run/secrets/kubernetes.io/serviceaccount`; filesystem restrittivo blocca il mountpoint | `automountServiceAccountToken: false` nel pod spec |
| `DataNotFoundException: Customer not found` - crash seed data | Seed parzialmente eseguito al primo avvio (admin+employee creati, customer no) → riavvio salta la creazione → rental referenzia customer inesistente | `try/catch` in `Application.run()` intorno a `seedDataConfig.runFirst()` - seed failure non fatale |
| NetworkPolicy bloccava ingress su porta errata | NetworkPolicy autorizzava solo porta 80 dopo cambio nginx a 8080 | Aggiornato `06-networkpolicy.yaml` per autorizzare porta 8080 |