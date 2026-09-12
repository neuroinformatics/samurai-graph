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
./mvnw spotless:apply
```

### Step 2: Clean Build Artifacts

Clean the project to avoid state pollution from prior builds:

```bash
./mvnw clean
```

### Step 3: Compile with Lint Warning Flags

Compile the source code with lint flags to verify there are no compilation warnings, deprecation warnings, or lint failures:

```bash
./mvnw compile
```

*(Note: The `pom.xml` is configured to automatically pass `-Xlint:all` to the compiler. Carefully review the output of `./mvnw compile` and resolve any compiler warnings introduced by your changes.)*

### Step 4: Run Tests

Execute the test suite to verify no existing tests are broken:

```bash
./mvnw test
```

Review the test output for any failures or errors. Write new tests for new functionality and update existing tests when behavior changes.

---

## 3. Distribution Build (jpackage)

The project uses [jpackage](https://docs.oracle.com/en/java/javase/21/jpackage/) to create native platform installers. Native packaging is bound to the `verify` phase (not `package`), so `./mvnw package` builds only the fat JAR and jpackage input directory, while `./mvnw verify` produces the platform-specific installer.

The appropriate jpackage profile is auto-activated based on the host OS. Each platform's installer must be built on that platform.

| Command | Phase | Output |
|--------|-------|--------|
| `./mvnw package` | `package` | Fat JAR + jpackage input directory |
| `./mvnw verify` | `verify` | Above + native platform installer |

| Platform | Installer Output |
|----------|------------------|
| Windows | `target/dist/Samurai Graph-<ver>.exe` |
| macOS | `target/dist/Samurai Graph-<ver>.dmg` |
| Linux | `target/dist/samurai-graph-<ver>.deb` + `.rpm` |

> [!IMPORTANT]
> On Fedora/RHEL, the system OpenJDK package modifies `java.security`, which causes `jlink` (used internally by `jpackage`) to fail with `"Error: .../java.security has been modified"`. Use a non-distro JDK (e.g. Eclipse Temurin) via `JAVA_HOME` when building Linux packages:
>
> ```bash
> JAVA_HOME=/usr/lib/jvm/temurin-21-jdk ./mvnw clean verify
> ```

---

## 4. Testing

- **Test Framework:** JUnit 5 (Jupiter) with Maven Surefire plugin.
- **Test Location:** Tests reside in `src/test/java/`, mirroring the `src/main/java/` package structure.
- **New Code:** Write unit tests for new public methods and classes.
- **Bug Fixes:** Add regression tests when fixing bugs.

---

## 5. General Architecture & Guidelines

- **Java Version:** The project is configured for **Java 21**. Do not use language features or APIs that are incompatible with Java 21.
- **Existing Code:** Respect the existing architecture and patterns. If you need to make changes to GUI components or backend data parsing, search the codebase for similar implementations first to ensure consistency.

---

## 6. Git Commit Workflow

- **Never commit without explicit user confirmation.**
- Before committing, present the commit message draft (subject/body) and the target files to the user, and wait for their explicit approval (e.g. "go", "OK").
- Only run `git commit` after the user has approved the draft.
- Follow the project's commit message conventions (see `git-commit` skill / Conventional Commits): valid type, lowercase imperative description, subject and every body line ≤ 72 characters, no agent attribution or session references.
- Stage changes only after the work is verified (format, compile, tests) and the commit plan is approved.
