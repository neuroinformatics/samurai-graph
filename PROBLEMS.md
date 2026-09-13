# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-13, v2.2.0).
Items are ordered by priority.

## 1. Low Test Coverage

Actual JaCoCo measurement (line coverage) is **14.9%** overall.

| Package | Coverage | Test files | Notes |
|---------|----------|-----------|-------|
| `com.github...lib.mdarray` | 97.8% | 4 | Pure logic, well covered |
| `org.freehep...util.export` | 89.7% | 1 | Vendored replacement class |
| `jp...samuraigraph.export` | 80.0% | 1 | Few instructions |
| `com.github...lib.hdf5` | 33.5% | 5 | Round-trip tests read/write real HDF5 |
| `jp...samuraigraph.base` | 26.7% | 19 | Pure-logic parts plus the window property I/O round-trip tests |
| `jp...samuraigraph.data` | 29.9% | 50 | Largest application package; all core utilities covered |
| `jp...samuraigraph.application` | 3.4% | 5 | Data setup dialog construction smoke tests |
| `jp...samuraigraph.figure` | 5.5% | 9 | Legend, axis, grid, string, axis-break, significant-difference and timing-line round-trip tests |

- 93 test files / 1125 test executions against 622 main files / 276k LOC
- Per-class coverage of the data-layer utilities now reaches 60% or
  more: column info 92%, stride 81%, range 81%, misc 76%, merge 66%,
  data type 66%, buffer 62%, viewer 60%, file 60%, text 80%, column
  title 75%
- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling limits coverage of the GUI classes; the
  property round-trip pattern works headlessly for the legend, axis,
  grid, string, axis-break, significant-difference and timing-line
  elements

## 2. Legacy Idioms

- **Constant-bag interfaces**: 152 files use the `SGI*` prefix, and 60 of
  the 149 interface declarations found there declare no methods — they
  exist solely to hold constants (e.g. `SGIApplicationConstants`,
  `SGIImageConstants`) or as empty marker interfaces (`SGIRootObject`,
  `SGIWindowDialogObserver`). Classes implement 3–5 of them at once.
  A Java 1.x idiom.
- **Raw `Thread` usage**: 8 files use raw threads instead of an executor:
  eight `extends Thread` classes — `InputObserver` in
  `SGDataSetupWizardDialog` / `SGPropertyFileDataDialog` / `SGDataDialog`,
  `CommandThread` in `SGConsoleRunner`,
  `SGMainFunctions.Initializer`, `SGWindowManager.DropEventHandler`,
  and `SGAnimationThread` — plus anonymous `new Thread` in
  `SGMainFunctions.java:2073` and `SGAsyncWorker.java:36`.
- **Static mutable fields**: instance state held in static fields — e.g.
  `SGDrawingServer.mLookAndFeel` / `mAppMain`,
  `SGApplicationAdapter.mAdapter` (static singleton), and
  `SGMainFunctions.virtualBounds` (`SGMainFunctions.java:152`).

## 3. Repository / Dependency Hygiene

- `dependency-reduced-pom.xml` (maven-shade-plugin output) is in `.gitignore`
  and untracked, but it is regenerated at the repository root by every
  `package` build. It cannot simply be redirected into `target/`: the shade
  plugin replaces the project POM with the reduced POM, which breaks the
  jpackage assembly (its `${project.basedir}` resolves to `target/`), so the
  file has to stay at the root as long as the assembly is bound to `package`.
- **Vendored code in-tree**:
  - `com.github...lib.hdf5` (30 files, ~1.7k LOC): a compatibility shim over
    `io.jhdf`
  - `org.freehep...ExportFileTypeRegistry` (125 LOC): replacement for the
    original that is deliberately excluded from the shaded JAR
  - Both are thin, but they carry maintenance and licensing-management risk.
- **Implicit external dependency**: reading NetCDF4 requires the system
  netcdf-c library (`cdm-core` / `netcdf4` 5.10.0 as runtime deps). Without
  it, only NetCDF3 files can be read. This is documented in the README, but
  it is easy to miss at build time.

## Recommended Priority

1. **Thicken tests (top priority)**: the pure-logic `data` / `base` /
   `mdarray` layers are covered; keep extending the property
   round-trip pattern to more GUI classes
2. **Replace legacy idioms**: migrate constant-bag interfaces to typed
   constants or enums, replace raw `Thread` usage with an executor, and
   move static mutable state into instance or immutable holders
