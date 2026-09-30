# Thymeleaf Content Starter

A Java web application that combines Spring Boot, server-rendered Thymeleaf pages, Bootstrap styling, JPA persistence, and JWT authentication components. Use it as a foundation for a content site or an article dashboard.

**Java 17 · Spring Boot 3.5.0 · Thymeleaf · Spring Security · MySQL · JUnit 5 · Mockito · H2 · JaCoCo**

## What is available

- A public home/demo page at `/admin/index`.
- A database-backed article list at `/admin/posts`, including an empty state and escaped text rendering.
- Post DTO mapping, a service layer, and JPA repositories.
- Email-based user lookup and `ROLE_USER` / `ROLE_ADMIN` authorities.
- JWT generation and bearer-token verification components.
- Tests for mappings, services, persistence, page rendering, and authentication behavior.
- A Maven Wrapper and GitHub Actions verification workflow.

This is a starter, not a complete CMS. There are no registration/login/token HTTP endpoints, article creation or editing screens, donation processing, or contact/volunteer submission handlers. The additional charity-themed templates are demonstration assets. Their forms and some navigation links are not connected to application routes.

## Quick start

### Requirements

- Java 17.
- MySQL 8 for a normal application run.
- Internet access on the first Maven Wrapper run. Maven itself does not need to be installed.

Create the database and a dedicated database account, then configure the environment. Spring Boot reads environment variables directly; it does **not** automatically load a `.env` file.

Linux / macOS:

```bash
export DB_URL='jdbc:mysql://localhost:3306/inventory'
export DB_USERNAME='your_database_user'
export DB_PASSWORD='your_database_password'
export SPRING_PROFILES_ACTIVE=local
bash ./mvnw spring-boot:run
```

Windows PowerShell:

```powershell
$env:DB_URL='jdbc:mysql://localhost:3306/inventory'
$env:DB_USERNAME='your_database_user'
$env:DB_PASSWORD='your_database_password'
$env:SPRING_PROFILES_ACTIVE='local'
.\mvnw.cmd spring-boot:run
```

Open **http://localhost:8080/admin/index** or **http://localhost:8080/admin/posts**. An empty database renders an empty article list; no sample accounts or posts are seeded.

The `local` profile enables Hibernate `ddl-auto=update` for initial development. Without that profile the default is `validate`, so an existing schema is required. Introduce migrations before production use.

## Configuration

| Environment variable | Default | Purpose |
| --- | --- | --- |
| `DB_URL` | `jdbc:mysql://localhost:3306/inventory` | Database connection |
| `DB_USERNAME` | `root` | Database user; override for deployment |
| `DB_PASSWORD` | Empty | Database password |
| `DDL_AUTO` | `validate` | Schema policy outside the local profile |
| `SHOW_SQL` | `false` | SQL logging |
| `JWT_SECRET` | Empty | Base64 key; decoded bytes must be sufficient for HS256 |
| `JWT_EXPIRATION_MILLIS` | `86400000` | Positive token lifetime in milliseconds |
| `SERVER_PORT` | Spring Boot default `8080` | HTTP port |

Generate a Base64 key from at least 32 random bytes. If `JWT_SECRET` is absent, a fresh key is generated in memory at startup; tokens then stop working after a restart. Multiple instances must share a configured key.

The original committed database password has been removed from the delivered configuration. If it was used beyond local development, change that credential at its source.

## Routes and security behavior

| Route | Current behavior |
| --- | --- |
| `GET /admin/index` | Public home/demo template |
| `GET /admin/posts` | Public list of all posts |
| `/css/**`, `/js/**`, `/images/**`, `/fonts/**`, `/webjars/**` | Public static resources |
| Other routes | Authentication required; no complete protected feature is implemented |

Despite the `/admin` prefix, existing pages are **public demonstration pages**. The supplied policy preserves that behavior. No roles are required for viewing the article list.

The application uses stateless bearer authentication. The filter loads the current user's authorities from the database after validating the token. CSRF is disabled for the current bearer-oriented setup; revisit that choice if browser sessions or cookie authentication are introduced. BCrypt password verification is configured with strength 12, but account creation/password hashing is not exposed as an application feature.

## Tests and build

```bash
# Linux / macOS
bash ./mvnw -B verify
# Windows
.\mvnw.cmd -B verify
```

Tests activate their own H2 profile and never require your MySQL instance. Unit tests use Mockito; repository and page integration tests use an in-memory database and real Spring components.

Reports:

- `target/surefire-reports/`: test results.
- `target/site/jacoco/index.html`: coverage report.
- `target/thymeleaf-0.0.1-SNAPSHOT.jar`: executable application.

Run the packaged application with the same environment variables and local profile if appropriate:

```bash
java -jar target/thymeleaf-0.0.1-SNAPSHOT.jar
```

See [TESTING.md](TESTING.md) for the recorded results and limits. GitHub Actions runs Maven verification using Java 17 and uploads test and coverage reports.

## Repository structure

```text
src/main/java/bootstrap/web/thymeleaf/
  config/        Spring Security wiring
  controller/    MVC route handlers
  dto/           View data and validation constraints
  mapper/        Post entity / DTO conversion
  model/         JPA entities and role enum
  repository/    Spring Data queries
  security/      JWT issuance, verification and error responses
  service/       Content queries and account lookup
src/main/resources/
  templates/     Thymeleaf pages
  static/        Bootstrap, scripts, fonts and images
src/test/        Unit and integration tests with H2
.github/         Automated verification
```

## Design and compatibility

Read [ARCHITECTURE.md](ARCHITECTURE.md) for component responsibilities, security flow, data relationships, and scope.

The original `4.0.0-SNAPSHOT` parent was replaced with the fixed Spring Boot `3.5.0` release to make this delivery reproducible with the project's existing Java 17, Jackson 2, MVC testing, and security APIs. This is a compatibility baseline, not a claim that it is the latest release. Upgrade framework versions through a separately validated change.

The role enum now stores names rather than ordinals. An existing database using numeric role values needs an explicit data/schema migration before using this build. The enum `role` is the authentication source; the separate legacy many-to-many `roles` collection remains in the model but is not used for authorities.

## Contributing and license

Run `verify` before opening a pull request, keep credentials out of commits, and add behavior-focused tests for new routes. Choose a project license before promising reuse terms: no project license file was included. Preserve the existing third-party template and asset attribution when redistributing them.

[GitHub description and suggested topics](GITHUB_DESCRIPTION.md)
