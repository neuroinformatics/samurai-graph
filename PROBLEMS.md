# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-12, v2.2.0).
Items are ordered by priority.

## 1. Low Test Coverage (Top Priority)

Actual JaCoCo measurement (instruction coverage) is **5.3%** overall.

| Package | Coverage | Test files | Notes |
|---------|----------|-----------|-------|
| `com.github...lib.mdarray` | 97.4% | 4 | Pure logic, well covered |
| `org.freehep...util.export` | 86.8% | 1 | Vendored replacement class |
| `jp...samuraigraph.export` | 68.2% | 0 (nested under `figure.java2d`) | Few instructions |
| `com.github...lib.hdf5` | 14.1% | 5 | Round-trip tests read/write real HDF5 |
| `jp...samuraigraph.base` | 10.1% | 18 | Only the pure-logic parts are tested |
| `jp...samuraigraph.data` | 11.8% | 40 | Largest application package |
| `jp...samuraigraph.application` | 1.4% | 3 | |
| `jp...samuraigraph.figure` | 1.4% | 2 | |
| `jp...samuraigraph.figure.java2d` | **0.0%** | 0 | All 109 rendering-layer files untested |

- 76 test files / 805 test methods against 582 main files / ~274k LOC
- The data-layer helpers live in ten single-purpose utility classes:
  `SGDataDataTypeUtility` (76.4%), `SGDataTextUtility` (78.5%),
  `SGDataColumnTitleUtility` (78.0%), `SGDataColumnInfoUtility` (21.9%),
  `SGDataRangeUtility` (17.8%), `SGDataMiscUtility` (15.5%),
  `SGDataBufferUtility` (29.9%), `SGDataViewerUtility` (11.1%),
  `SGDataStrideUtility` (24.6%), `SGDataFileUtility` (10.2%); the former
  `SGDataUtility` class no longer exists
- `SGDefaultColumnTypeUtility` is a 499-line core (entry point, dispatcher,
  shared helpers) with three source-specific classes:
  `SGDefaultColumnTypeSDArrayUtility` (28.1%),
  `SGDefaultColumnTypeNetCDFUtility` (24.3%), and
  `SGDefaultColumnTypeMDArrayUtility` (22.8%); the SXY/VXY/SXYZ resolution
  paths for SDArray, NetCDF and MATLAB data are exercised by file-based
  tests
- File-based tests cover the main import paths: example NetCDF files
  (parsing, variables, SXY data objects), a generated MATLAB file,
  a generated HDF5 file (write/read round trip), and a generated CSV
- Tight coupling to Swing/AWT makes headless testing hard; the design itself
  is part of the problem
- Thinning out tests in the pure-logic `data` / `base` / `mdarray` layers is
  the most practical entry point for building a refactoring safety net

## 2. God Classes

| LOC | File | Problem |
|-----|------|---------|
| 7,955 | `figure/java2d/SGFigureElementLegend.java` | Largest class |
| 7,087 | `base/SGDrawingWindow.java` | Frame handling mixed with window logic |
| 6,822 | `application/SGMainFunctions.java` | All commands, menu actions, and transforms in one class |
| 5,754 | `figure/java2d/SGAxisElement.java` | |
| 4,956 | `figure/java2d/SGPropertyDialogSXYData.java` | |
| 4,889 | `figure/java2d/SGFigureElementShape.java` | |
| 4,763 | `data/SGSXYNetCDFMultipleData.java` | |
| 4,532 | `data/SGSXYMDArrayMultipleData.java` | |
| 4,511 | `base/SGFigure.java` | |

68 files exceed 1,000 lines; 129 exceed 500.

Note: `SGMainFunctionsSplitMerge` (588 LOC) and `SGMainFunctionsTransform`
(700 LOC) are separate classes, but the main class is still 6,822 lines.

## 3. Duplicated Architecture

- **Dual class hierarchy**: `figure` (model) and `figure.java2d` (renderer)
  maintain mirror class sets per element (model + `*2D` renderer + property
  dialog). Changing one element ripples across 3+ files.
- **Copy-paste dialog families**: `SGPropertyFileMDArrayDataDialog` /
  `SGPropertyFileNetCDFDataDialog` / `SGPropertyFileSDArrayDataDialog`, and
  likewise the `*WizardDialog` family — each duplicates shared scaffolding
  as separate classes.

## 4. Legacy Idioms

- **Constant-bag interfaces**: 152 files use the `SGI*` prefix, and 55 of
  those interfaces declare no methods — they exist solely to hold constants
  (e.g. `SGIApplicationConstants`, `SGIImageConstants`) or as empty marker
  interfaces (`SGIRootObject`, `SGIWindowDialogObserver`). Classes implement
  3–5 of them at once. A Java 1.x idiom.
- **Raw `Thread` usage**: 7 files spawn 9 raw threads instead of using an
  executor: three `InputObserver extends Thread` classes,
  `SGMainFunctions.Initializer` / `SGMainFunctions.CommandThread` /
  anonymous `new Thread` (SGMainFunctions.java:4568),
  `SGWindowManager.DropEventHandler`, `SGAnimationThread extends Thread`,
  and `SGAsyncWorker` (`new Thread`, SGAsyncWorker.java:36).
- **Static mutable fields**: instance state held in static fields — e.g.
  `SGDrawingServer.mLookAndFeel` / `mAppMain`,
  `SGApplicationAdapter.mAdapter` (static singleton), and
  `SGMainFunctions.virtualBounds`.

## 5. Repository / Dependency Hygiene

- `dependency-reduced-pom.xml` (maven-shade-plugin output) is in `.gitignore`
  and untracked, but it is regenerated at the repository root by every
  `package` build. It cannot simply be redirected into `target/`: the shade
  plugin replaces the project POM with the reduced POM, which breaks the
  jpackage assembly (its `${project.basedir}` resolves to `target/`), so the
  file has to stay at the root as long as the assembly is bound to `package`.
- **Vendored code in-tree**:
  - `com.github...lib.hdf5` (26 files, ~1.6k LOC): a compatibility shim over
    `io.jhdf`
  - `org.freehep...ExportFileTypeRegistry` (125 LOC): replacement for the
    original that is deliberately excluded from the shaded JAR
  - Both are thin, but they carry maintenance and licensing-management risk.
- **Implicit external dependency**: reading NetCDF4 requires the system
  netcdf-c library (`cdm-core` / `netcdf4` 5.10.0 as runtime deps). Without
  it, only NetCDF3 files can be read. This is documented in the README, but
  it is easy to miss at build time.

## Recommended Priority

1. **Split candidates** (progressively, guarded by tests):
   `SGMainFunctions` / `SGDrawingWindow`
2. **Integrate the dual hierarchy**: design work to merge `figure` and
   `figure.java2d` into a single layer
3. **Thicken tests**: add unit tests to the pure-logic `data` / `base` /
   `mdarray` layers to build a refactoring safety net
