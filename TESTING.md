# Testing and verification

## Recorded results

Verification completed on 2026-09-30 using OpenJDK 17.0.20 and Maven Wrapper 3.9.10.

| Check | Result |
| --- | --- |
| `mvnw -B verify` | BUILD SUCCESS |
| JUnit tests | 31 passed, 0 failures, 0 errors |
| JaCoCo line coverage | 76.3% (100 covered, 31 missed) |
| JaCoCo instruction coverage | 79.2% (431 covered, 113 missed) |
| Executable Spring Boot JAR | Produced successfully |

The GitHub Actions workflow was added but has not been run on GitHub. The local verification uses the same Java major version and Maven command, with environment-specific network/cache configuration outside the project.

## Suite composition

| Suite | Tests | Behavior |
| --- | --- | --- |
| `PostMapperTests` | 2 | Complete field round trip, absent optional fields |
| `PostServiceTests` | 3 | DTO listing, empty results, persistence failure propagation |
| `UserDetailsTests` | 2 | Email/password/authority mapping, missing account |
| `JwtServiceTests` | 8 | Claims, lifetime, identity mismatch, shared key, wrong signature, malformed/expired tokens, weak keys |
| `JwtFilterTests` | 5 | Anonymous pass-through, invalid token, deleted account, authenticated context, downstream exception boundary |
| `RepositoryTests` | 4 | Post timestamps/lookup, user lookup/roles, bean validation, database constraints |
| `PageIntegrationTests` | 6 | Empty and populated rendering, HTML escaping, home route, static CSS, security policy, malformed bearer token |
| `ThymeleafApplicationTests` | 1 | Full application context startup |

## Isolation and tools

Unit tests use JUnit 5, AssertJ, and Mockito. Repository tests use `@DataJpaTest`; page tests use `@SpringBootTest`, `@AutoConfigureMockMvc`, and the real security chain. All Spring tests activate the `test` profile and use in-memory H2 in MySQL compatibility mode. No development MySQL credentials are needed.

Repository test transactions roll back automatically. Page tests clear post records before each case. Filter tests clear the thread-bound security context after each test.

## Run locally

```bash
bash ./mvnw -B verify
```

Windows PowerShell:

```powershell
.\mvnw.cmd -B verify
```

Results are in `target/surefire-reports/`. Coverage is in `target/site/jacoco/index.html`. A portable copy of the coverage HTML is included in `verification-reports.zip` inside the delivered archive.

## Scope limits

H2 compatibility mode does not prove MySQL-specific behavior. There are no real-browser, MySQL-container, load, or deployment tests. Uncovered code includes parts of the access-denied response handler, model helpers, process entry point, and some JWT filter branches. No coverage threshold is enforced.

## Changes verified with the suite

- Reproducible Spring Boot 3.5.0 parent replaces the original 4.0.0 snapshot.
- Home controller is registered; logical view names are portable.
- Post list renders actual DTOs and an empty state using escaped output.
- Template resources use context-relative paths, and static assets are public.
- Database credentials are environment-driven; local and test profiles are separate.
- JWT key and lifetime are configurable, with an ephemeral development fallback.
- Deleted-user tokens return 401; downstream application errors are not misclassified as JWT errors.
- Role enum values are stored as names; existing ordinal schemas require migration.
- Comments explain transaction, DTO, escaping, key lifecycle, and security-policy choices.
