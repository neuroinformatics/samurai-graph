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

### Step 4: Run Tests

Execute the test suite to verify no existing tests are broken:

```bash
mvn test
```

Review the test output for any failures or errors. Write new tests for new functionality and update existing tests when behavior changes.

---

## 3. Distribution Build (jpackage)

The project uses [jpackage](https://docs.oracle.com/en/java/javase/21/jpackage/) to create native platform installers. Each platform's installer must be built on that platform.

| Platform | Command | Output |
|----------|---------|--------|
| Windows | `mvn clean package -Pjpackage-windows` | `target/dist/Samurai Graph-<ver>.exe` |
| macOS | `mvn clean package -Pjpackage-mac` | `target/dist/Samurai Graph-<ver>.dmg` |
| Linux | `JAVA_HOME=/path/to/temurin-jdk mvn clean package -Pjpackage-linux` | `target/dist/samurai-graph-<ver>.deb` + `.rpm` |

> [!IMPORTANT]
> On Fedora/RHEL, the system OpenJDK package modifies `java.security`, which causes `jlink` (used internally by `jpackage`) to fail with `"Error: .../java.security has been modified"`. Use a non-distro JDK (e.g. Eclipse Temurin) via `JAVA_HOME` when building Linux packages:
>
> ```bash
> JAVA_HOME=/usr/lib/jvm/temurin-21-jdk mvn clean package -Pjpackage-linux
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
