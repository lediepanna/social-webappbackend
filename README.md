Social WebApp Backend
Backend för ett socialt nätverksprojekt byggt med Spring Boot.
Projektet hanterar användare, autentisering och inlägg (posts).
Tekniker:
Java 17
Spring Boot
Spring Security (JWT)
Spring Data JPA
PostgreSQL
OAuth2 Authorization Server
Docker
GitHub Actions
Funktioner:
Registrering & inloggning (JWT)
Skapa, läsa, uppdatera och ta bort inlägg
Skyddade och publika API-endpoints
Docker
Kör via Docker Hub:
docker pull annalediep/quyenle:latest
docker run -p 8080:8080 annalediep/quyenle:latest
Testa:
http://localhost:8080/api/test/public
CI/CD
Projektet använder GitHub Actions för att automatiskt bygga och pusha Docker image till Docker Hub vid push till feature/socialbackend.
Reflektion: Projektet gav praktisk erfarenhet av Spring Boot, JWT-säkerhet och CI/CD med Docker och GitHub Actions.
