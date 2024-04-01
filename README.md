# Github Challenge

This Scala application is designed to interact with pull request events through webhooks, ensuring idempotent processing to handle duplicate events without inconsistencies.  The application provides an API to deliver metrics related to pull requests, focusing on scalability, security, and efficient data handling and retrieval.

## Table of Contents
- [Project Structure](#project-structure)
- [Tech Stack](#tech-stack)
- [Features](#features)
- [Setup](#setup)
    - [Requirements](#requirements)
    - [Local Development](#local-development)
- [API Endpoints](#api-endpoints)
- [Handling Webhooks](#handling-webhooks)

## Project Structure
```
- src
  - main
    - scala
      - com
        - githubchallenge
          - api
            - ProjectMetricsApi.scala
            - ContributorMetricsApi.scala
            - GithubWebhookApi.scala
          - db
            - repositories
              - sql
                - ContributorsSql.scala
                - GithubWebhookSql.scala
                - ProjectsSql.scala
              - ContributorsRepository.scala
              - GithubWebhookRepository.scala
              - ProjectsRepository.scala
            - Repositories.scala
          - model
            - Contributor.scala
            - Metrics.scala
            - Project.scala
          - service
            - ContributorMetricsService.scala
            - GithubWebhookService.scala
            - ProjectMetricsService.scala
          - App.scala
  - test
    - scala
      - service
        - MetricsServiceSpec.scala
- build.sbt
```

## Tech Stack
- Scala
- Http4s (HTTP server)
- Doobie (Database access)
- Cats Effect 3 (Effect system)
- Circe (JSON parsing)
- Flyway (Database migrations)
- PostgreSQL (Database)

## Features
- **GitHub Webhooks**: Receive and process webhook notifications for pull request events. The system handles duplicates idempotently.
- **Scheduled Job**: Fetch pull request data twice daily.
- **Metrics API**:
    - **Project Metrics**:
        - Total Contributors
        - Total Commits
        - Total Closed Pull Requests
        - Total Open Pull Requests
    - **Contributor Metrics**:
        - Total Projects
        - Total Commits
        - Total Closed PRs
        - Total Open PRs
- **Database Interaction**: Uses PostgreSQL database with Flyway for migrations.

## Setup

### Requirements
- Java JDK (11+)
- Scala (2.13.x)
- SBT (Scala Build Tool)
- PostgreSQL

### Local Development
1. **Clone Repository**:
   ```bash
   git clone <repository_url>
   cd <project_directory>
   ```

2. **Database Setup**:
    - Create a PostgreSQL database.
    - Update `src/main/resources/application.conf` with database credentials.
   

3. **Run Application**:
   ```bash
   sbt run
   ```
   The server will start at `http://localhost:8080`.


## API Endpoints

### Project Metrics
- **GET** `/projects/:projectId/metrics`
    - Returns metrics for a specific project.
    - Example Response:
      ```json
      {
        "totalContributors": 10,
        "totalCommits": 100,
        "totalClosedPRs": 50,
        "totalOpenPRs": 5
      }
      ```

### Contributor Metrics
- **GET** `/contributors/:contributorId/metrics`
    - Returns metrics for a specific contributor.
    - Example Response:
      ```json
      {
        "totalProjects": 5,
        "totalCommits": 50,
        "totalClosedPRs": 20,
        "totalOpenPRs": 3
      }
      ```

## Handling Webhooks
- Webhook endpoint is set up at `/webhook`.
- When a GitHub pull request event is received, it is processed idempotently.
- The `GithubWebhookService` decodes the JSON payload into a `PullRequestEvent` and then processes it.
