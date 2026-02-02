# Contributing to ArchAdvisor

First off, thank you for considering contributing to ArchAdvisor!

## Table of Contents
1. [Code of Conduct](#code-of-conduct)
2. [Getting Started](#getting-started)
3. [Development Workflow](#development-workflow)
4. [Coding Standards](#coding-standards)
5. [Commit Messages](#commit-messages)

---

## Code of Conduct
We are committed to providing a friendly, safe and welcoming environment for all. Please be respectful and considerate in issues and pull requests.

## Getting Started

### Prerequisites
Ensure your local environment matches the project standards:
* **Java:** JDK 21
* **Node.js:** v18 (LTS) or higher
* **Database:** Docker & Docker Compose (for PostgreSQL)

### Setup
1.  **Fork** the repository on GitHub.
2.  **Clone** your fork locally:
    ```bash
    git clone [https://github.com/YOUR-USERNAME/ArchAdvisor.git](https://github.com/ArchAdvisor/ArchAdvisor.git)
    cd ArchAdvisor
    ```
3.  **Start up the infrastructure:**
    ```bash
    docker compose up -d db
    ```
4.  **Verify the build:**
    * Backend: `mvn clean verify`
    * Frontend: `npm install && npm run build` (inside `/frontend`)

---

## Development Workflow

We follow a standard **Feature Branch Workflow**.

1.  **Find an Issue:** Look for open issues or create a new one to discuss a feature/bug.
2.  **Create a Branch:** Always branch off `main`.
    ```bash
    git checkout -b feat/your-feature-name
    # or
    git checkout -b fix/issue-number-bug-name
    ```
3.  **Develop:** Write your code.
    * *Backend:* Ensure you add unit tests (`JUnit 5`) for logic-heavy components.
    * *Frontend:* Ensure components are modular and typed (if using TypeScript).
4.  **Test:** Run the full test suite before committing.
    ```bash
    mvn test
    ```
5.  **Push & PR:** Push to your fork and open a Pull Request against the `main` branch.

### Pull Request Guidelines
* **Title:** clear and descriptive (e.g., "Add weighting logic to recommendation engine").
* **Description:** Reference the Issue ID (e.g., "Closes #12"). Explain *what* changed and *why*.
* **Screenshots:** If changing the UI, please attach before/after screenshots.

---

## Coding Standards

### Backend (Java / Spring Boot)
* **Style:** We follow standard Java conventions.
* **Architecture:** Respect the Layered Architecture (Controller -> Service -> Repository).
* **Clean Code:**
    * Avoid "Magic Numbers"; use constants or enums.
    * Keep Controllers thin; move logic to Services.
    * **Fact Check:** Do not hardcode recommendation metrics without a source or comment explaining the assumption.

### Frontend (React)
* **Components:** Functional components with Hooks only.
* **State:** Use Context API or local state; avoid over-engineering with Redux unless necessary.
* **Styling:** Keep CSS modular (CSS Modules or Styled Components).

---

## Commit Messages

We encourage the use of **Conventional Commits** to keep the history clean and readable.

**Format:** `<type>(<scope>): <subject>`

**Examples:**
* `feat(backend): add scoring algorithm for mobile stacks`
* `fix(ui): resolve questionnaire validation error`
* `docs: update README with architecture diagram`
* `chore: bump spring-boot version to 3.2.2`

**Allowed Types:**
* `feat`: A new feature
* `fix`: A bug fix
* `docs`: Documentation only changes
* `style`: Formatting (white-space, formatting, missing semi-colons, etc)
* `refactor`: A code change that neither fixes a bug nor adds a feature
* `test`: Adding missing tests or correcting existing tests
* `chore`: Changes to the build process or auxiliary tools

---

## Reporting Issues

When filing an issue, please use the provided templates.
* **Bugs:** Include steps to reproduce, expected behavior, and environment details.
* **Features:** Describe the "User Story" and acceptance criteria.
