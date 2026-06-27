# Agent Guidelines for Samurai Graph

This document serves as the guide for AI agents (and human developers) contributing to the `samurai-graph` codebase.

## 1. Code Style & Language Guidelines

### Comment Language

- **ALL comments inside the source code must be written in English.**
- This applies to Javadoc comments, inline comments, blocks of notes, and debug logs.
- Do not write comments in Japanese or other languages.

### Code Formatting

- This project utilizes [Spotless](https://github.com/diffplug/spotless) to enforce uniform code styling (Google Java Format).
- Always format the files using the Maven Spotless plugin before building or committing.

---

## 2. Build & Verification Process

After making any code modifications, you **MUST** run the verification process in the exact order below to ensure the code style is pristine and that no compilation or lint issues are introduced.

### Step 1: Apply Formatter

Format all changed files using:

```bash
mvn spotless:apply
```

### Step 2: Clean Build Artifacts

Clean the project to avoid state pollution from prior builds:

```bash
mvn clean
```

### Step 3: Compile with Lint Warning Flags

Compile the source code with lint flags to verify there are no compilation warnings, deprecation warnings, or lint failures:

```bash
mvn compile
```

*(Note: The `pom.xml` is configured to automatically pass `-Xlint:all` to the compiler. Carefully review the output of `mvn compile` and resolve any compiler warnings introduced by your changes.)*

---

## 3. Git Commit Rules

### CRITICAL: Always use `skill git-commit`

- **You MUST run `skill git-commit` for every commit.** Never write commit messages manually.
- This rule is non-negotiable. Ignoring it is a violation of project workflow.
- The skill enforces Conventional Commits format, line length limits, and proper structure.

### Commit message rules

- **Never include task numbers (TASK-xxx) or task names in commit messages.**
- The message must clearly describe what was changed so that anyone reading `git log` understands the change.
- Examples:
  - `refactor(data): extract NetCDF methods to SGNetCDFDataUtility`
  - `test(application): add tests for SGWizardManager`
  - `docs: update execution plan and mark refactoring task done`

### Branch workflow

- Create a new branch for each task (e.g., `task/test-data-utility-bounds`).
- Merge to `master` only after full completion (all tests pass, compilation succeeds, documentation updated).
- Delete the task branch after merging.
- **Never push to `origin`.** Only push changes to `master`.

---

## 4. General Architecture & Guidelines

- **Java Version:** The project is configured for **Java 21**. Do not use language features or APIs that are incompatible with Java 21.
- **Existing Code:** Respect the existing architecture and patterns. If you need to make changes to GUI components or backend data parsing, search the codebase for similar implementations first to ensure consistency.
