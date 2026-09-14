# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-13, v2.2.0).
Items are ordered by priority.

## 1. Low Test Coverage

Actual JaCoCo measurement (instruction coverage) is **16.4%** overall.

| Package | Coverage | Test files | Notes |
|---------|----------|-----------|-------|
| `com.github...lib.mdarray` | 97.4% | 4 | Pure logic, well covered |
| `org.freehep...util.export` | 86.8% | 1 | Vendored replacement class |
| `jp...samuraigraph.export` | 68.2% | 1 | Few instructions |
| `com.github...lib.hdf5` | 27.2% | 5 | Round-trip tests read/write real HDF5 |
| `jp...samuraigraph.base` | 30.1% | 22 | Pure-logic parts plus the window property I/O round-trip tests |
| `jp...samuraigraph.data` | 30.9% | 50 | Largest application package; all core utilities covered |
| `jp...samuraigraph.application` | 3.4% | 5 | Data setup dialog construction smoke tests |
| `jp...samuraigraph.figure` | 6.3% | 12 | Legend, axis, grid, graph, string, shape, axis-break, significant-difference and timing-line round-trip tests |

- 100 test files / 1155 test executions against 620 main files
- Per-class coverage of the data-layer utilities now reaches 60% or
  more: column info 92%, stride 81%, range 81%, misc 76%, merge 66%,
  data type 66%, buffer 62%, viewer 60%, file 60%, text 80%, column
  title 75%
- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling limits coverage of the GUI classes; the
  property round-trip pattern works headlessly for the legend, axis,
  grid, string, shape, axis-break, significant-difference and
  timing-line elements

## 2. Legacy Idioms

- **Constant-bag interfaces resolved**: 51 constant-only `SGI*Constants`
  interfaces were converted to `SG*Constants` final classes (private
  constructor, `public static final` fields, nested types promoted to
  `public static`); implementing classes now use `import static` from
  the relevant constant classes and ambiguous same-named constants were
  qualified with their declaring class. Empty marker interfaces
  (`SGIRootObject`, `SGIIndex`, `SGIWindowDialogObserver`) remain as
  interfaces since they are used as marker types.
- **Raw `Thread` usage resolved**: all `extends Thread` classes were
  converted to `Runnable` tasks on executors — `InputObserver` in
  `SGDataSetupWizardDialog` / `SGPropertyFileDataDialog` / `SGDataDialog`,
  `CommandThread` in `SGConsoleRunner`,
  `SGMainFunctions.Initializer` (via `Future` join),
  `SGWindowManager.DropEventHandler` (inlined into the EDT), and
  `SGAnimationThread` (single-flight submit, `join()`, daemon
  executor). The one-off command reader (`SGMainFunctions`) and
  `SGAsyncWorker` submit to a shared daemon executor service.
- **Static mutable fields**: the data plug-in list / manager statics
  duplicated in `SGDrawingWindow`, `SGDataViewerDialog` and
  `SGDataPopupMenu` were consolidated into a single
  `SGDataPluginHolder`. `SGUserProperties.mInstance` is now `final`.
  `SGDialog.virtualBounds` is still a static mutable `Rectangle`
  (screen bounds computed once at startup). The server adapter,
  look-and-feel and main-function references were moved to instance
  state.

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
2. **Replace legacy idioms**: the remaining items are the typed
   constants migration for the marker interfaces (if desired) and moving
   the remaining static mutable state (`SGDialog.virtualBounds`) into
   instance or immutable holders
