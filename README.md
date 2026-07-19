# Samurai Graph

A highly functional and user-friendly scientific graphing application
for visualizing experimental data from various file formats.

## Features

- **Multi-format data import**: CSV, NetCDF, HDF5, MATLAB (.mat) files
- **Rich graph types**: Single/Multiple XY, Date XY, Vector XY,
  Polar, Pseudocolor, (x,y,z) 3D data
- **Image export**: EMF, PDF, PostScript, SVG, SWF (via FreeHEP)
- **Property files** (.sgp): Save and reload graph configurations
- **Archive files** (.sga): Bundle data and properties together
- **Script files** (.sgs): Automate graph generation
- **Native installers**: Windows (.exe), macOS (.dmg), Linux (.deb, .rpm)
- **JNA plugin system**: Extend functionality with native libraries

## System Requirements

- Java 21 or later
- **NetCDF4/HDF5 support (optional):** The NetCDF-C library must be installed
  on the system for NetCDF4 files to be readable.
  - **Linux:** `libnetcdf-dev` (Debian/Ubuntu), `netcdf` (Fedora/RHEL)
  - **macOS:** `netcdf-c` via Homebrew (`brew install netcdf-c`)
  - **Windows:** NetCDF-C redistributable must be installed and on `PATH`
  - Without it, only NetCDF3 files are supported.

## Installation

### Pre-built Installers

Download the latest release for your platform from the
[Releases](https://github.com/neuroinformatics/samurai-graph/releases)
page.

| Platform | Format |
|----------|--------|
| Windows  | `.exe` installer |
| macOS    | `.dmg` disk image |
| Linux    | `.deb` (Debian/Ubuntu) or `.rpm` (Fedora/RHEL) |

### Build from Source

```bash
mvn clean package
```

The executable JAR is created at
`target/samurai-graph-<version>-shaded.jar`.

## Usage

Launch the application:

```bash
java -jar target/samurai-graph-*-shaded.jar
```

### Supported Data Formats

| Format | Extension / Type |
|--------|-----------------|
| CSV / TSV | `.txt`, `.csv` (whitespace or comma delimited) |
| NetCDF | `.nc` |
| HDF5 | `.h5`, `.hdf5` |
| MATLAB | `.mat` (v5) |

### Graph Property Files

Graph configurations can be saved as `.sgp` files and reloaded later.
Example property files are included in the `examples/data/` directory.

## Project Structure

```
samurai-graph/
├── src/main/java/       # Application source code
├── src/main/resources/  # Icons, properties, DTDs
├── src/main/jpackage/   # Platform-specific installer resources
├── plugins/jna/         # JNA native plugin (C source)
├── examples/data/       # Sample data and property files
├── changelog/           # Release notes (product.xml → ChangeLog.html)
└── pom.xml              # Maven build configuration
```

## Development

### Prerequisites

- Java 21 (JDK)
- Maven 3.9+

### Build Commands

```bash
# Format code (Google Java Format via Spotless)
mvn spotless:apply

# Clean compile with lint checks
mvn clean compile

# Create fat JAR
mvn clean package

# Create native installer (requires building on target OS)
# Windows:
mvn clean package -Pjpackage-windows
# macOS:
mvn clean package -Pjpackage-mac
# Linux (use non-distro JDK on Fedora/RHEL):
JAVA_HOME=/usr/lib/jvm/temurin-21-jdk mvn clean package -Pjpackage-linux
```

See [AGENTS.md](AGENTS.md) for detailed contribution guidelines.

## License

This project is licensed under the
[GNU Lesser General Public License v2.1](LICENSE).

Copyright (c) 2004-2026 RIKEN (The Institute of Physical and
Chemical Research). All rights reserved.
