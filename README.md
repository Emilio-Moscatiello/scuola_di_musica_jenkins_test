# Scuola di Musica - API REST

Sistema di gestione per una scuola di musica, sviluppato con Spring Boot e Spring Security. Fornisce un'API RESTful completa per la gestione di studenti, insegnanti, corsi, iscrizioni e strumenti musicali, con autenticazione JWT e controllo degli accessi basato sui ruoli.

---

## Indice

- [Tecnologie](#tecnologie)
- [Architettura](#architettura)
- [Funzionalità](#funzionalità)
- [Sicurezza](#sicurezza)
- [Struttura del progetto](#struttura-del-progetto)
- [Configurazione](#configurazione)
- [Avvio dell'applicazione](#avvio-dellapplicazione)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [CI/CD con Jenkins](#cicd-con-jenkins)

---

## Tecnologie

### Backend
| Tecnologia | Versione | Utilizzo |
|---|---|---|
| Java | 21 | Linguaggio principale |
| Spring Boot | 3.4.3 | Framework applicativo |
| Spring Security | (incluso in Boot) | Autenticazione e autorizzazione |
| Spring Data JPA | (incluso in Boot) | ORM e accesso al database |
| Spring Web | (incluso in Boot) | API REST |
| Spring Validation | (incluso in Boot) | Validazione degli input |
| JJWT | 0.11.5 | Generazione e validazione token JWT |
| Lombok | (incluso in Boot) | Riduzione del boilerplate |

### Database
| Tecnologia | Utilizzo |
|---|---|
| H2 (in-memory) | Database per sviluppo e test |
| Hibernate | ORM / generazione schema |

### Documentazione API
| Tecnologia | Versione | Utilizzo |
|---|---|---|
| Springdoc OpenAPI | 2.1.0 | Generazione documentazione Swagger |

### Build & Test
| Tecnologia | Utilizzo |
|---|---|
| Maven | Build system e gestione dipendenze |
| JUnit 5 | Framework di testing |
| MockMvc | Test degli endpoint HTTP |
| Spring Security Test | Test con autenticazione simulata |

### CI/CD
| Tecnologia | Utilizzo |
|---|---|
| Jenkins | Pipeline CI/CD automatizzata |

---

## Architettura

L'applicazione segue un'architettura a livelli classica di Spring Boot:

```
Request HTTP
     │
     ▼
┌─────────────────────────────────────┐
│          Security Filter Chain      │  ◄── JWT validation, autenticazione
└─────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────┐
│            Controller Layer         │  ◄── REST endpoints, @PreAuthorize
└─────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────┐
│             Service Layer           │  ◄── Business logic, transazioni
└─────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────┐
│           Repository Layer          │  ◄── Spring Data JPA, query methods
└─────────────────────────────────────┘
     │
     ▼
┌─────────────────────────────────────┐
│         H2 In-Memory Database       │
└─────────────────────────────────────┘
```

### Pattern utilizzati
- **DTO Pattern** — separazione netta tra oggetti di request/response e le entity JPA
- **Service-Repository Pattern** — logica di business separata dall'accesso ai dati
- **Global Exception Handling** — gestione centralizzata degli errori tramite `@RestControllerAdvice`
- **Stateless JWT Authentication** — nessuna sessione server-side, autenticazione tramite token

---

## Funzionalità

### Autenticazione e Registrazione
- Registrazione utente con assegnazione ruolo (`ROLE_STUDENT`, `ROLE_TEACHER`, `ROLE_ADMIN`)
- Login con generazione token JWT (algoritmo HS512, scadenza 24 ore)
- Protezione endpoint tramite `@PreAuthorize` con verifica ruolo

### Gestione Studenti
- Creazione, lettura, aggiornamento ed eliminazione studenti
- Campi gestiti: matricola, codice fiscale, nome, cognome, data di nascita, numero di telefono
- Livelli: `PRINCIPIANTE`, `INTERMEDIO`, `AVANZATO`
- Ricerca per matricola e per livello

### Gestione Insegnanti
- CRUD completo per gli insegnanti
- Campi gestiti: matricola, codice fiscale, nome, cognome, data di nascita, stipendio, specializzazione, anni di esperienza

### Gestione Corsi
- CRUD completo per i corsi
- Campi gestiti: codice corso, nome, date di inizio/fine, costo orario, totale ore, modalità online, livello
- Associazione con un insegnante responsabile

### Iscrizioni
- Iscrizione e rimozione di uno studente da un corso
- Registrazione anno di iscrizione
- Inserimento voto finale (scala 18-30) al termine del corso
- Visualizzazione iscrizioni per studente o per corso

### Gestione Strumenti
- CRUD completo per gli strumenti musicali
- Tipo strumento: `TASTIERA`, `CORDA`, `ARCO`, `PERCUSSIONE`, `FIATO`
- Attributi specifici per tipologia (es. numero corde, tipo pelle, diametro)
- Gestione dei prestiti: registrazione data inizio/fine, verifica disponibilità

### Lezioni
- Associazione lezioni a corsi specifici
- Campi gestiti: numero progressivo, data, ora di inizio, durata (minuti), aula, argomento

---

## Sicurezza

### Ruoli
| Ruolo | Accesso |
|---|---|
| `ROLE_ADMIN` | Accesso completo a tutte le operazioni |
| `ROLE_TEACHER` | Gestione corsi e lezioni propri |
| `ROLE_STUDENT` | Visualizzazione corsi, gestione iscrizioni proprie |

### Flusso di autenticazione JWT

```
1. POST /api/auth/signup   →  Registrazione utente
2. POST /api/auth/signin   →  Login → Risposta con JWT token
3. Tutte le richieste successive includono l'header:
   Authorization: Bearer <token>
4. AuthTokenFilter valida il token ad ogni richiesta
5. SecurityContext viene popolato con i dati dell'utente
```

### Endpoint pubblici
- `/api/auth/**` — registrazione e login
- `/h2-console/**` — console H2 (solo sviluppo)
- `/swagger-ui/**` — documentazione API
- `/api-docs/**` — schema OpenAPI

### Configurazione sicurezza
- Password hashing con **BCrypt**
- Sessione **STATELESS** (nessun HttpSession)
- CSRF **disabilitato** (API REST stateless)
- Frame options disabilitate per la console H2

---

## Struttura del progetto

```
src/
├── main/
│   ├── java/com/scuoladimusica/
│   │   ├── ScuolaDiMusicaApplication.java
│   │   ├── config/
│   │   │   └── DataLoader.java                  # Inizializzazione ruoli all'avvio
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── CourseController.java
│   │   │   ├── EnrollmentController.java
│   │   │   ├── InstrumentController.java
│   │   │   ├── StudentController.java
│   │   │   └── TeacherController.java
│   │   ├── exception/
│   │   │   ├── BusinessRuleException.java        # HTTP 400
│   │   │   ├── DuplicateResourceException.java   # HTTP 409
│   │   │   ├── GlobalExceptionHandler.java
│   │   │   └── ResourceNotFoundException.java    # HTTP 404
│   │   ├── model/
│   │   │   ├── dto/
│   │   │   │   ├── request/                      # 11 DTO di input
│   │   │   │   └── response/                     # 11 DTO di output
│   │   │   └── entity/                           # 12 entity JPA
│   │   ├── repository/                           # 9 repository JPA
│   │   ├── security/
│   │   │   ├── WebSecurityConfig.java
│   │   │   ├── jwt/
│   │   │   │   ├── AuthEntryPointJwt.java
│   │   │   │   ├── AuthTokenFilter.java
│   │   │   │   └── JwtUtils.java
│   │   │   └── services/
│   │   │       ├── UserDetailsImpl.java
│   │   │       └── UserDetailsServiceImpl.java
│   │   └── service/                              # 6 service
│   └── resources/
│       └── application.properties
└── test/
    ├── java/com/scuoladimusica/
    │   ├── TestDataFactory.java
    │   ├── controller/                           # 5 test di integrazione
    │   └── service/                              # 5 test di unità
    └── resources/
        └── application.properties
```

---

## Configurazione

### `application.properties` (sviluppo)

```properties
# Database H2 in-memory
spring.datasource.url=jdbc:h2:mem:scuola_musica
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=admin
spring.datasource.password=admin
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JWT
myapp.jwtSecret=<chiave_segreta_minimo_64_caratteri>
myapp.jwtExpirationMs=86400000

# Swagger
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

> **Attenzione**: Non inserire mai la chiave JWT reale in chiaro nel repository. Utilizzare variabili d'ambiente o un secret manager.

---

## Avvio dell'applicazione

### Prerequisiti
- Java 21+
- Maven 3.8+

### Avvio locale

```bash
# Clona il repository
git clone <repository-url>
cd "Esercizio Testing"

# Compila e avvia
mvn spring-boot:run
```

L'applicazione sarà disponibile su `http://localhost:8080`.

| Risorsa | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI Docs | http://localhost:8080/api-docs |
| H2 Console | http://localhost:8080/h2-console |

---

## API Endpoints

### Autenticazione

| Metodo | Endpoint | Descrizione | Accesso |
|---|---|---|---|
| POST | `/api/auth/signup` | Registrazione nuovo utente | Pubblico |
| POST | `/api/auth/signin` | Login e ottenimento token JWT | Pubblico |

### Studenti

| Metodo | Endpoint | Descrizione | Ruolo richiesto |
|---|---|---|---|
| GET | `/api/students` | Lista tutti gli studenti | ADMIN, TEACHER |
| GET | `/api/students/{id}` | Dettaglio studente | ADMIN, TEACHER, STUDENT |
| POST | `/api/students` | Crea nuovo studente | ADMIN |
| PUT | `/api/students/{id}` | Aggiorna studente | ADMIN |
| DELETE | `/api/students/{id}` | Elimina studente | ADMIN |

### Insegnanti

| Metodo | Endpoint | Descrizione | Ruolo richiesto |
|---|---|---|---|
| GET | `/api/teachers` | Lista tutti gli insegnanti | ADMIN |
| GET | `/api/teachers/{id}` | Dettaglio insegnante | ADMIN, TEACHER |
| POST | `/api/teachers` | Crea nuovo insegnante | ADMIN |
| PUT | `/api/teachers/{id}` | Aggiorna insegnante | ADMIN |
| DELETE | `/api/teachers/{id}` | Elimina insegnante | ADMIN |

### Corsi

| Metodo | Endpoint | Descrizione | Ruolo richiesto |
|---|---|---|---|
| GET | `/api/courses` | Lista tutti i corsi | Autenticato |
| GET | `/api/courses/{id}` | Dettaglio corso | Autenticato |
| POST | `/api/courses` | Crea nuovo corso | ADMIN, TEACHER |
| PUT | `/api/courses/{id}` | Aggiorna corso | ADMIN, TEACHER |
| DELETE | `/api/courses/{id}` | Elimina corso | ADMIN |

### Iscrizioni

| Metodo | Endpoint | Descrizione | Ruolo richiesto |
|---|---|---|---|
| GET | `/api/enrollments` | Lista iscrizioni | ADMIN, TEACHER |
| POST | `/api/enrollments` | Iscrive studente a corso | ADMIN |
| PUT | `/api/enrollments/{id}` | Aggiorna iscrizione / inserisce voto | ADMIN, TEACHER |
| DELETE | `/api/enrollments/{id}` | Rimuove iscrizione | ADMIN |

### Strumenti

| Metodo | Endpoint | Descrizione | Ruolo richiesto |
|---|---|---|---|
| GET | `/api/instruments` | Lista tutti gli strumenti | Autenticato |
| GET | `/api/instruments/{id}` | Dettaglio strumento | Autenticato |
| POST | `/api/instruments` | Aggiunge strumento | ADMIN |
| PUT | `/api/instruments/{id}` | Aggiorna strumento | ADMIN |
| DELETE | `/api/instruments/{id}` | Elimina strumento | ADMIN |

---

## Testing

### Struttura dei test

I test sono organizzati in due categorie:

#### Test di integrazione (Controller)
Utilizzano `@SpringBootTest` + `@AutoConfigureMockMvc` per testare l'intera catena request → response, inclusa la sicurezza.

```
src/test/java/com/scuoladimusica/controller/
├── StudentControllerTest.java
├── TeacherControllerTest.java
├── CourseControllerTest.java
├── EnrollmentControllerTest.java
└── InstrumentControllerTest.java
```

#### Test di unità (Service)
Testano la logica di business isolata dalla sicurezza e dal database.

```
src/test/java/com/scuoladimusica/service/
├── StudentServiceTest.java
├── TeacherServiceTest.java
├── CourseServiceTest.java
├── EnrollmentServiceTest.java
└── InstrumentServiceTest.java
```

#### TestDataFactory
Classe utility che centralizza la creazione di dati di test consistenti per tutti i test.

### Esecuzione dei test

```bash
# Esegui tutti i test
mvn test

# Esegui una classe specifica
mvn test -Dtest=StudentControllerTest

# Esegui con report
mvn verify
```

### Tecniche di test utilizzate
- `@WithMockUser(roles = "ADMIN")` — simula utente autenticato con ruolo specifico
- `@Nested` + `@DisplayName` — organizzazione leggibile dei test
- `MockMvc` — simulazione richieste HTTP senza server reale
- `@Transactional` — rollback automatico dopo ogni test
- Database H2 dedicato per i test (`jdbc:h2:mem:testdb`)

---

## CI/CD con Jenkins

Il progetto utilizza **Jenkins** per automatizzare il processo di build, test e deploy.

### Pipeline

La pipeline Jenkins (definita nel `Jenkinsfile`) esegue le seguenti fasi:

```
┌─────────┐    ┌──────────┐    ┌────────┐    ┌────────┐    ┌────────┐
│Checkout │ ─► │  Build   │ ─► │  Test  │ ─► │Package │ ─► │ Deploy │
└─────────┘    └──────────┘    └────────┘    └────────┘    └────────┘
```

| Stage | Comando | Descrizione |
|---|---|---|
| **Checkout** | `git checkout` | Recupero del codice sorgente |
| **Build** | `mvn compile` | Compilazione del progetto |
| **Test** | `mvn test` | Esecuzione di tutti i test |
| **Package** | `mvn package -DskipTests` | Creazione del JAR eseguibile |
| **Deploy** | `java -jar target/*.jar` | Avvio dell'applicazione |

### Requisiti Jenkins
- **JDK 21** installato sul nodo Jenkins
- **Maven** configurato come tool in Jenkins
- Plugin consigliati:
  - Pipeline
  - Git
  - JUnit (per pubblicazione report test)
  - Maven Integration

### Esempio Jenkinsfile

```groovy
pipeline {
    agent any

    tools {
        maven 'Maven 3.8'
        jdk 'JDK 21'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completata con successo!'
        }
        failure {
            echo 'Pipeline fallita. Controllare i log.'
        }
    }
}
```

---

## Modello dei dati

```
User ──────────────── Role
 │         (M:N)
 │
 ├──► Student ──────► Enrollment ◄──── Course ◄──── Teacher
 │                                        │
 │                                        └──────► Lesson
 │
 └──► (tramite prestito)
         Loan ◄──────────────────────────── Instrument
```

### Entity principali

| Entity | Campi chiave |
|---|---|
| `User` | username, email, password (BCrypt) |
| `Student` | matricola, CF, nome, cognome, dataNascita, livello |
| `Teacher` | matricolaInsegnante, CF, specializzazione, stipendio, anniEsperienza |
| `Course` | codiceCorso, dataInizio, dataFine, costoOrario, totaleOre, online |
| `Enrollment` | annoIscrizione, votoFinale (18-30) |
| `Instrument` | codiceStrumento, tipoStrumento, marca, annoProduzione |
| `Loan` | dataInizio, dataFine (null = prestito attivo) |
| `Lesson` | numero, data, oraInizio, durata (minuti), aula, argomento |
