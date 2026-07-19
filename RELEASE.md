# How to Release

1. Update change logs
   - Edit `changelog/product.xml`
   - Regenerate: `mvn generate-resources`
2. Update version in `pom.xml` (`<version>X.Y.Z</version>`)
   - `samurai-graph.properties` is auto-filtered from pom.xml
3. Commit: `git commit`
4. Tag: `git tag vX.Y.Z`
5. Rebuild: `mvn clean package`
6. Create native installers (build on target OS)
   - Windows: `mvn clean package -Pjpackage-windows`
   - macOS: `mvn clean package -Pjpackage-mac`
   - Linux: `JAVA_HOME=/path/to/temurin-jdk mvn clean package -Pjpackage-linux`
