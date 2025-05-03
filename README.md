# Allergy Terminology API

[![Java](https://img.shields.io/badge/Java-21-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-3.9+-orange.svg)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Used-blue.svg)](https://www.postgresql.org/)
[![SQLite](https://img.shields.io/badge/SQLite-Used-blue.svg)](https://www.sqlite.org/index.html)
[![JPA](https://img.shields.io/badge/JPA-Hibernate-lightgrey.svg)](https://hibernate.org/)
[![Lombok](https://img.shields.io/badge/Lombok-Used-red.svg)](https://projectlombok.org/)

Clinical Allergy Terminology Repository - A REST API for querying allergy information.

## Overview

This application provides a RESTful API to access a catalog of clinical terminologies related to allergies. The main catalog data is loaded from a CSV file into an **SQLite** database upon startup. Additionally, the application logs every request made to its endpoints into a separate **PostgreSQL** database for auditing or monitoring purposes.

## Features

*   Loads allergy data from a CSV file (`catalogo_alergias.csv`). This data is available at https://www.ctc.min-saude.pt/catalogos/alergias-e-outras-reacoes-adversas/
*   Stores the allergy catalog in an **SQLite** database (`allergy_catalog.db`).
*   Stores request logs (URI, method, IP, timestamp, etc.) in a **PostgreSQL** database (database name `allergy_logs`).
*   REST endpoints for:
    *   Listing all allergies.
    *   Searching allergies by term (case-insensitive and accent-insensitive search across relevant text fields).
    *   (Potentially other endpoints like fetch by ID or code, if implemented).
*   Configured for multiple DataSources (SQLite and PostgreSQL).

## Technology Stack

*   **Language:** Java 21
*   **Core Framework:** Spring Boot 3.4.5
    *   Spring Web (MVC) - For the REST API
    *   Spring Data JPA - For data access
    *   Spring Boot DevTools - For development productivity
*   **Persistence:**
    *   Hibernate (JPA Implementation)
    *   **SQLite:** Stores the allergy catalog (`allergy_catalog.db`)
    *   **PostgreSQL:** Stores the request logs (database `allergy_logs`)
*   **Database Drivers:**
    *   `sqlite-jdbc` (Xerial)
    *   `postgresql` JDBC Driver
*   **Additional Libraries:**
    *   Lombok - Reduces boilerplate code (getters, setters, etc.)
    *   OpenCSV - For reading the CSV file
    *   Hibernate Community Dialects - Required for the SQLite dialect with recent Hibernate versions
*   **Build Tool:** Apache Maven

## Prerequisites

*   **JDK 21** or higher installed.
*   **Apache Maven** 3.9 or higher installed.
*   **PostgreSQL Server** installed and running.
*   A database named `allergy_logs` (or as configured in `application.properties`) must be **created manually** in your PostgreSQL instance before running the application.

## Setup and Installation

1.  **Clone the Repository:**

3.  **Configure PostgreSQL Database:**
    *   Ensure the database `allergy_logs` exists in your PostgreSQL server.
    *   Configure your PostgreSQL access credentials in `src/main/resources/application.properties`. Pay close attention to the environment variable used for the password:
        ```properties
        app.datasource.pg.url=jdbc:postgresql://localhost:5432/allergy_logs
        app.datasource.pg.username=postgres # Your PostgreSQL user
        app.datasource.pg.password=${PW_POSTGRES} # Password via Environment Variable
        ```
    *   **Set the `PW_POSTGRES` environment variable** with your PostgreSQL user's password before running the application.
        *   Linux/macOS: `export PW_POSTGRES='your_password'`
        *   Windows (cmd): `set PW_POSTGRES=your_password`
        *   Windows (PowerShell): `$env:PW_POSTGRES='your_password'`

4.  **Verify CSV File:**
    *   Ensure the `catalogo_alergias.csv` file is present in `src/main/resources/db/`.
    *   Confirm that the column order in the CSV matches the order expected by the `CsvDataLoaderService.java`.

5.  **Build the Project:**
    ```bash
    mvn clean install
    ```

## Configuration Notes

*   The `src/main/resources/application.properties` file contains all database and JPA settings for both SQLite and PostgreSQL, using distinct prefixes (`app.datasource.sqlite.*`, `app.jpa.sqlite.*`, `app.datasource.pg.*`, `app.jpa.pg.*`).
*   The property `app.jpa.sqlite.hibernate.ddl-auto=update` will cause Hibernate to attempt to create/update the `catalogo_alergias` table in the SQLite file `allergy_catalog.db`.
*   The property `app.jpa.pg.hibernate.ddl-auto=update` will cause Hibernate to attempt to create/update the `request_logs` table in the PostgreSQL `allergy_logs` database.

## Running the Application

1.  **Ensure the `PW_POSTGRES` environment variable is set.**
2.  Run the application using the Spring Boot Maven plugin:
    ```bash
    mvn spring-boot:run
    ```
3.  Alternatively, after building (`mvn clean install`), run the executable JAR:
    ```bash
    java -jar target/Allergy-0.0.1-SNAPSHOT.jar
    ```

The application will start and be accessible at `http://localhost:8080` (or the configured port).

## API Endpoints (Examples)

*   **`GET /v1/allergies`**
    *   Returns the complete list of allergies from the catalog (stored in SQLite).
*   **`GET /v1/allergies/search?term={your_term}`**
    *   Searches for allergies where `{your_term}` appears (case-insensitive, accent-insensitive) in fields like name, FSN, synonyms, etc.
    *   Example: `GET /v1/allergies/search?term=milk`
*   **(Other Endpoints)**
    *   `GET /v1/allergies/{id}` (Not implemented yet)
    *   `GET /v1/allergies/category/{categoryCode}` (Not implemented yet)
    *   `GET /v1/allergies/allergen/{allergenCode}` (Not implemented yet)

## Database Structure

*   **SQLite (`allergy_catalog.db`):** Contains the `catalogo_alergias` table holding data loaded from the CSV. The structure is defined by the `Allergy.java` entity.
*   **PostgreSQL (Database `allergy_logs`):** Contains the `request_logs` table storing information about incoming API requests. The structure is defined by the `RequestLog.java` entity.

Tables are automatically created or updated on startup if the corresponding `ddl-auto` property is set to `update` or `create`.

