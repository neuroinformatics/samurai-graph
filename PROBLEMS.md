# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-13, v2.2.0).
Items are ordered by priority.

## 1. Low Test Coverage

Actual JaCoCo measurement (line coverage) is **13.2%** overall.

| Package | Coverage | Test files | Notes |
|---------|----------|-----------|-------|
| `com.github...lib.mdarray` | 97.8% | 4 | Pure logic, well covered |
| `org.freehep...util.export` | 89.7% | 1 | Vendored replacement class |
| `jp...samuraigraph.export` | 80.0% | 0 (nested under `figure.java2d`) | Few instructions |
| `com.github...lib.hdf5` | 25.5% | 5 | Round-trip tests read/write real HDF5 |
| `jp...samuraigraph.base` | 26.5% | 19 | Pure-logic parts plus the window property I/O round-trip tests |
| `jp...samuraigraph.data` | 24.8% | 43 | Largest application package |
| `jp...samuraigraph.application` | 3.4% | 4 | Data setup dialog construction smoke tests added |
| `jp...samuraigraph.figure` | 5.2% | 2 | |
| `jp...samuraigraph.figure.java2d` | **4.2%** | 2 | Legend and axis property round-trip tests added |

- 86 test files / 977 test methods (1004 executions) against 616 main files
  / ~275k LOC
- Per-class coverage of the data-layer utilities is uneven: the pure
  groups (data type 64%, text 80%, column title 73%) reach 64-80%, while
  the others remain below 60% (buffer 51%, stride 31%, column info 54%,
  range 32%, misc 32%, viewer 18%, file 14%)
- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling still limits coverage of the GUI classes,
  but the property round-trip pattern now works headlessly for the
  legend and axis elements, which keeps top-priority splitting work
  testable

## 2. God Classes

| LOC | File | Problem |
|-----|------|---------|
| 4,963 | `base/SGDrawingWindow.java` | Largest class |
| 4,958 | `figure/java2d/SGPropertyDialogSXYData.java` | |
| 4,889 | `figure/java2d/SGFigureElementShape.java` | |
| 4,775 | `data/SGSXYNetCDFMultipleData.java` | |
| 4,537 | `data/SGSXYMDArrayMultipleData.java` | |
| 4,516 | `figure/java2d/SGAxisElement.java` | Delegates property I/O to a collaborator class |
| 4,511 | `base/SGFigure.java` | |
| 4,414 | `figure/java2d/SGElementGroupSetInGraphSXYMultiple.java` | |
| 3,828 | `application/SGMainFunctions.java` | All menu actions, commands, and the console loop; delegates to eight collaborator classes |

75 files exceed 1,000 lines; 141 exceed 500.

## 3. Duplicated Architecture

- **Dual class hierarchy**: `figure` (model) and `figure.java2d` (renderer)
  maintain mirror class sets per element (model + `*2D` renderer + property
  dialog). Changing one element ripples across 3+ files.
- **Copy-paste dialog families**: the dialog and wizard data families
  (`SGPropertyFile*DataDialog` / `SG*DataSetupWizardDialog`) previously
  duplicated their button and data name scaffolding; the scaffolding is
  now built by the shared base classes, and the remaining data-type
  specific parts stay in the subclasses.

## 4. Legacy Idioms

- **Constant-bag interfaces**: 152 files use the `SGI*` prefix, and 55 of
  those interfaces declare no methods — they exist solely to hold constants
  (e.g. `SGIApplicationConstants`, `SGIImageConstants`) or as empty marker
  interfaces (`SGIRootObject`, `SGIWindowDialogObserver`). Classes implement
  3–5 of them at once. A Java 1.x idiom.
- **Raw `Thread` usage**: 7 files spawn 9 raw threads instead of using an
  executor: three `InputObserver extends Thread` classes,
  `SGMainFunctions.Initializer` / `SGMainFunctions.CommandThread` /
  anonymous `new Thread` (SGMainFunctions.java:2065),
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

1. **Thicken tests (top priority)**: extend unit tests to the pure-logic
   `data` / `base` / `mdarray` layers and apply the proven property
   round-trip pattern to more GUI classes, to keep a safety net for
   further splitting
2. **Split remaining god classes** (progressively, guarded by tests):
   `SGPropertyDialogSXYData` / `SGFigureElementShape` /
   `SGSXYNetCDFMultipleData` / `SGSXYMDArrayMultipleData` /
   `SGElementGroupSetInGraphSXYMultiple` — the legend and axis classes
   (`SGDrawingWindow`, `SGMainFunctions`, `SGFigureElementLegend`,
   `SGAxisElement`) have already been decomposed
3. **Integrate the dual hierarchy**: design work to merge `figure` and
   `figure.java2d` into a single layer
