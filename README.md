# Task

Aplikacja jest prostym API typu proxy, które udostępnia dane o repozytoriach użytkownika GitHuba.

API:

- pobiera repozytoria użytkownika z GitHub REST API,

- filtruje repozytoria będące forkami,

- dla każdego repozytorium zwraca listę branchy wraz z SHA ostatniego commita.
## Wymagania

- Java 25

- Spring Boot 4.0.1

- Gradle (wrapper w projekcie)
## Uruchomienie aplikacji

```bash
./gradlew bootRun
```

## Budowanie artefaktu

```bash
./gradlew build
```

## Uruchomienie testów

```bash
./gradlew test
```

## Testy

- poprawne pobranie listy repozytoriów w sytuacji, gdzie jedno repozytorium jest 'forkiem', a drugie nie
- zwrócenie błędu ze statusem 404 i wiadomością dotyczącą błędu w przypadku próby pobrania danych od nieistniejącego użytkownika
