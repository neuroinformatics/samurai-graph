# HDF5 Library Alternatives Analysis

## Current State

### Dependency

The project uses `cisd:jhdf5` version 19.04.1 for HDF5 file I/O. This library is a JNI-based wrapper around the native HDF5 C library, bundled by the Swiss Institute of Bioinformatics (cisd).

```xml
<dependency>
    <groupId>cisd</groupId>
    <artifactId>jhdf5</artifactId>
    <version>19.04.1</version>
</dependency>
<dependency>
    <groupId>cisd</groupId>
    <artifactId>base</artifactId>
    <version>18.09.0</version>
</dependency>
```

### Usage Surface Area

The HDF5 library is used across **18 Java files** with the following key APIs:

| API Class | Usage | Files |
|-----------|-------|-------|
| `HDF5FactoryProvider.get()` | Opens readers/writers | SGApplicationUtility, SGMainFunctions, SGMDArrayData, SGSDArrayData, SGDataUtility |
| `IHDF5Reader` | Read HDF5 files | SGHDF5File, SGHDF5Variable, SGDataCreator, SGMainFunctions, SGPropertyDataFileChooserWizardDialog, SGApplicationUtility, SGDataUtility, SGMDArrayData |
| `IHDF5Writer` | Write HDF5 files | SGMDArrayData, SGSDArrayData, SGSXYMDArrayData, SGSXYZMDArrayData, SGTwoDimensionalMDArrayData, SGVXYMDArrayData, SGSXYMDArrayMultipleData, SGVXYSDArrayData, SGSXYZSDArrayData, SGDataUtility, SGMainFunctions |
| `HDF5DataClass` | Data type enum (FLOAT/INTEGER/STRING) | SGHDF5File, SGHDF5Variable, SGVirtualMDArrayVariable, SGMDArrayData, SGDataUtility |
| `HDF5DataSetInformation` | Dataset metadata | SGHDF5File, SGHDF5Variable |
| `HDF5DataTypeInformation` | Type information | SGHDF5File, SGHDF5Variable, SGDataUtility |
| `HDF5EnumerationValue` | Enumeration values | SGDataUtility |
| `hdf.hdf5lib.exceptions.HDF5Exception` | Exception handling | SGApplicationUtility, SGDataCreator, SGPropertyDataFileChooserWizardDialog, SGMainFunctions |

### Native Library Handling

The `SGMainFunctions.Initializer.removeHDF5TemporaryFiles()` method (line 309) cleans up native shared libraries extracted to the temp directory:
- `jhdf5*.so` - jhdf5 native library
- `nativedata*.so` - related native data library

This cleanup is necessary because the JNI-based jhdf5 extracts native `.so` files at runtime to a temporary directory.

### Windows-Specific Workaround

Method `SGDataUtility.hasValidHDF5CharacterForWin()` (line 6424) checks whether a file path contains only ASCII characters (0x20-0x7E). This is a known limitation of the jhdf5 native library on Windows, which fails with non-ASCII characters in file paths. Used in:
- `SGApplicationUtility.java:1625` - file type detection fallback
- `SGDataFileExporter.java:162` - export validation

### Wrapper Classes

Two custom wrapper classes abstract the HDF5 library:
- **`SGHDF5File`** - wraps `IHDF5Reader`, manages dataset discovery
- **`SGHDF5Variable`** - wraps individual datasets, handles data reading/writing

These wrappers would need to be updated if switching to a different HDF5 library.

## Alternative Libraries

### 1. OME HDF5 (`ome:hdf5-hdf`)

**Description:** Open Microscopy Environment's HDF5 library. JNA-based wrapper that bundles native HDF5 libraries, eliminating the need for system-level HDF5 installation.

**Pros:**
- JNA-based (no JNI compilation required)
- Bundles native libraries for multiple platforms (Linux, macOS, Windows)
- Actively maintained by OME community
- Used by popular tools: ImageJ/Fiji, Bio-Formats
- No native library extraction to temp directory needed
- Better cross-platform support

**Cons:**
- Different API surface than cisd:jhdf5 (requires wrapper class updates)
- May have different data type mappings

**Estimated Migration Effort:** Medium. The wrapper classes (`SGHDF5File`, `SGHDF5Variable`) would need to be rewritten to use OME's API, but the core operations (open, read, write, enumerate datasets) are similar.

### 2. ch.systemsx.cisd:hdf5-hdf

**Description:** This appears to be the same underlying library as `cisd:jhdf5` (same organization). The `ch.systemsx.cisd` groupId is the original Swiss Institute of Bioinformatics group ID.

**Note:** This is NOT a distinct alternative - it's the same library. The project currently uses `cisd:jhdf5` which is the Maven Central repackaged version.

### 3. HDF-Java (NASA GroupDAAC)

**Description:** Official HDF Group's Java library. Pure Java implementation for HDF4, with JNI for HDF5.

**Pros:**
- Official from HDF Group
- Well-documented

**Cons:**
- Still requires native HDF5 library installation
- Older, less actively maintained
- Different API

**Estimated Migration Effort:** High. Significant API differences.

### 4. Apache Commons VFS + HDF5

Not a direct alternative - requires native HDF5 installation.

## Recommendation

### Short-term: Keep current library

The `cisd:jhdf5` library works adequately for the project's needs. The main pain points are:
1. Native library extraction and cleanup overhead
2. Windows non-ASCII path limitation
3. Outdated version (19.04.1)

### Long-term: Migrate to OME HDF5

OME's `hdf5-hdf` is the recommended replacement because:
- Eliminates native library extraction/cleanup (removes `removeHDF5TemporaryFiles`)
- Better cross-platform compatibility
- Actively maintained
- Used by established scientific imaging tools

### Migration Approach

1. Create a new `HDF5Manager` abstraction layer with interfaces for read/write operations
2. Implement OME-based backend
3. Update `SGHDF5File` and `SGHDF5Variable` to use the new abstraction
4. Update all data classes that use `IHDF5Writer`
5. Remove `removeHDF5TemporaryFiles()` method
6. Remove Windows ASCII-only path workaround

### Estimated Effort: 3-5 days

- Day 1-2: Create abstraction layer, implement OME backend
- Day 2-3: Update wrapper classes
- Day 3-4: Update data classes (10+ files)
- Day 4-5: Testing and cleanup
