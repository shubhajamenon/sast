# Employee Tracker — SAST Training Application

A Spring Boot REST API built for the **T-04 SAST Vulnerability Remediation** workshop.
The application originally contained eight deliberate SAST vulnerabilities. Remediations for V1–V5 are included; V6–V8 remain workshop findings.

---

## About the application

The Employee Tracker exposes three functional areas:

| Area | Endpoints |
|------|-----------|
| Employee management | Create, list, search, and retrieve employees |
| Authentication | Register users and log in |
| Reports | Download files from a reports directory |

The application demonstrates remediation of security findings flagged by a SAST scanner.

### SAST findings and remediations

| ID | Finding | Remediation |
|----|---------|-------------|
| V1 | SQL Injection (CWE-89) | Employee name search binds the `LIKE` value as a query parameter. |
| V2 | Weak Cryptography (CWE-327) | Passwords are encoded with BCrypt through Spring Security's `PasswordEncoder`. |
| V3 | Hardcoded Secret (CWE-798) | The fixed token secret was removed; login does not return a fabricated shared token. |
| V4 | Sensitive Data in Logs (CWE-532) | Login logs contain no credentials or user-supplied values. |
| V5 | Path Traversal (CWE-22) | Downloads are resolved and checked against the configured reports directory, including symlinks. |
| V6 | Verbose Error Disclosure (CWE-209) | Present in `GlobalExceptionHandler.java`; not yet remediated. |
| V7 | Insecure Direct Object Reference (CWE-639) | Present in `EmployeeController.java`; not yet remediated. |
| V8 | Reflected XSS (CWE-79) | Present in `StatusController.java`; not yet remediated. |

---

## Prerequisites

| Tool | Version | Check |
|------|---------|-------|
| JDK | 17 or newer | `java -version` |
| Git | any recent | `git --version` |
| IDE | IntelliJ IDEA or VS Code with Extension Pack for Java | — |

> **Gradle is bundled** — no installation needed. The `gradlew.bat` / `gradlew` wrapper downloads Gradle automatically on first use.

---

## 1. Clone the repository

```bash
git clone https://github.com/antonypaul001/sast.git
cd sast
```

---

## 2. Build the application

```bash
.\gradlew.bat build
```

This compiles the source, runs the tests, and assembles the JAR. You should see:

```
BUILD SUCCESSFUL
```

> On macOS / Linux use `./gradlew build` instead of `.\gradlew.bat build`.

---

## 3. Run the application

```bash
.\gradlew.bat bootRun
```

The app starts on **http://localhost:8080**.  
Watch the console for:

```
Started SastTrackerApplication
```

That line means the app is ready.

Stop it at any time with **Ctrl+C**.

---

## 4. Test the application

Run the full test suite:

```bash
.\gradlew.bat test
```

> **Do not modify any test file.** The tests describe the correct behaviour of the application. When you fix a vulnerability, the tests must still pass.

---

## 5. Try the API

With the app running, use `curl`, Postman, or Bruno to call each endpoint.

### Create an employee

```bash
curl -X POST http://localhost:8080/api/employees \
     -H "Content-Type: application/json" \
     -d "{\"name\":\"Alice Smith\",\"role\":\"Engineer\",\"email\":\"alice@co.com\",\"onboardingDate\":\"2026-12-01\"}"
```

### List all employees

```bash
curl http://localhost:8080/api/employees
```

### Search by name

```bash
curl "http://localhost:8080/api/employees/search?name=alice"
```

### Register a user

```bash
curl -X POST http://localhost:8080/api/auth/register \
     -H "Content-Type: application/json" \
     -d "{\"username\":\"frank\",\"password\":\"pass123\"}"
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
     -H "Content-Type: application/json" \
     -d "{\"username\":\"frank\",\"password\":\"pass123\"}"
```

> Login attempts are logged without user-supplied values or credentials.

### Report downloads

Reports are read from the `reports` directory by default. Set `REPORTS_DIRECTORY` to use another directory. Requested files must remain within that directory.

### Get employee profile (sensitive fields — V7)

```bash
curl http://localhost:8080/api/employees/1/profile
```

> Returns salary and national ID with no authorization check. That is **Vulnerability V7**.

### Status page (V8)

```bash
curl "http://localhost:8080/api/status?message=Hello"
```

> Try injecting `<script>alert(1)</script>` as the message value in a browser. That is **Vulnerability V8**.

---

## 6. H2 database console

An in-browser SQL console is available while the app is running:

- URL: http://localhost:8080/h2-console  
- JDBC URL: `jdbc:h2:mem:sastdb`  
- Username: `sa`  
- Password: *(leave blank)*

---

## Project structure

```
sast-tracker/
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
└── src/
    ├── main/
    │   ├── java/com/accenture/sast/tracker/
    │   │   ├── SastTrackerApplication.java
    │   │   ├── controller/
    │   │   │   ├── AuthController.java       ← V4 Sensitive logging
    │   │   │   ├── EmployeeController.java   ← V7 IDOR (profile endpoint)
    │   │   │   ├── GlobalExceptionHandler.java ← V6 Verbose error disclosure
    │   │   │   ├── ReportController.java     ← V5 Path traversal
    │   │   │   └── StatusController.java     ← V8 Reflected XSS
    │   │   ├── model/
    │   │   ├── repository/
    │   │   └── service/
    │   │       ├── AuthService.java          ← V2 Weak crypto, V3 Hardcoded secret
    │   │       └── EmployeeService.java      ← V1 SQL injection
    │   └── resources/
    │       └── application.properties
    └── test/
        └── java/com/accenture/sast/tracker/
            ├── AuthControllerTest.java
            └── EmployeeControllerTest.java
```

---

## Tech stack

| Layer | Choice |
|-------|--------|
| Framework | Spring Boot 3.5 |
| Database | H2 in-memory |
| Build | Gradle 9.7 (wrapper included) |
| Java | 17+ |
| Tests | JUnit 5 + Spring MockMvc |
