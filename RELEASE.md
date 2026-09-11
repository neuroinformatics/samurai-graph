# How to Release

1. Update change logs
   - Edit `changelog/product.xml`
   - Regenerate: `./mvnw generate-resources`
2. Update version in `pom.xml` (`<version>X.Y.Z</version>`)
   - `samurai-graph.properties` is auto-filtered from pom.xml
3. Commit: `git commit`
4. Tag: `git tag vX.Y.Z`
5. Rebuild: `./mvnw clean package`
6. Create native installers (build on target OS; the profile auto-activates)
   - Windows: `./mvnw clean verify`
   - macOS: `./mvnw clean verify`
   - Linux: `JAVA_HOME=/path/to/temurin-jdk ./mvnw clean verify`
