# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-14, v2.2.0).
Items are ordered by priority.

## 1. Test Coverage

Actual JaCoCo measurement (instruction coverage) is **18.6%** overall.

| Package | Coverage | Test files | Notes |
|---------|----------|-----------|-------|
| `com.github...lib.mdarray` | 97.4% | 4 | Pure logic, well covered |
| `org.freehep...util.export` | 86.8% | 1 | Vendored replacement class |
| `jp...samuraigraph.export` | 68.2% | 1 | Few instructions |
| `com.github...lib.hdf5` | 27.8% | 5 | Round-trip tests read/write real HDF5 |
| `jp...samuraigraph.base` | 31.3% | 24 | Pure-logic parts, the window property I/O round-trip tests and dialog virtual-bounds tests |
| `jp...samuraigraph.data` | 31.0% | 50 | Largest application package; all core utilities covered |
| `jp...samuraigraph.application` | 4.5% | 12 | Dialog construction smoke tests plus the console command loop, archive extraction and file path handling |
| `jp...samuraigraph.figure` | 10.6% | 25 | Legend, axis, axis scaling, grid, graph, string, shape, axis-break, significant-difference and timing-line round-trip tests plus paint, arrow geometry, line and symbol element groups, legend group sets and static geometry utilities |

- 113 test files / 1263 test executions against 624 main files
- Per-class coverage of the data-layer utilities reaches 60% or more:
  column info 92%, stride 81%, range 81%, misc 76%, merge 66%, data
  type 66%, buffer 62%, viewer 60%, file 60%, text 80%, column title
  75%
- Covered utilities in `figure` / `application`: string brace modifier
  100%, line style 95%, paint XML round trip 95%, console runner 83%,
  archive extractor 76%, element group base 71%, file handler 53%,
  symbol group base 40%, error bar helpers 31%, static geometry
  utilities 29%, dialog virtual bounds 29%, axis scale 13%
- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling limits coverage of the GUI classes; the
  property round-trip pattern works headlessly for the legend, axis,
  grid, string, shape, axis-break, significant-difference and
  timing-line elements
- Headful tests (window / dialog construction) require a running X
  server; on display-less machines run them under a virtual X server
  (see the Testing section of AGENTS.md)

## 2. Legacy Idioms

- **Constants**: all constant values live in `SG*Constants` final
  classes with private constructors and `public static final` fields.
  Three interfaces (`SGIRootObject`, `SGIIndex`,
  `SGIWindowDialogObserver`) are interfaces by design: they are used
  as types in method signatures and class hierarchies, not as
  constant bags, and contain no constants.
- **Threads**: no class extends `Thread`; long-running work runs as
  `Runnable` tasks on executor services, and background tasks submit
  to shared daemon executors.
- **Static mutable state**: no known static mutable field remains;
  shared state is held in dedicated holders, instance `final` fields
  or immutable values.

## 3. Repository / Dependency Hygiene

- **Vendored code in-tree** (license headers present in all files):
  - `com.github...lib.hdf5` (30 files, ~1.7k LOC): a compatibility shim over
    `io.jhdf`
  - `org.freehep...ExportFileTypeRegistry` (125 LOC): replacement for the
    original that is deliberately excluded from the shaded JAR
  - Both are thin, but they carry maintenance risk.
- **NetCDF4 dependency**: reading NetCDF4 files requires the system
  netcdf-c library (`cdm-core` / `netcdf4` 5.10.0 as runtime deps).
  When a `.nc` file cannot be opened because the library is missing, a
  warning with installation guidance is logged and the file falls back
  to the text import path. The README documents the symptom and the
  per-OS install packages.

## Recommended Priority

1. **Thicken tests**: keep extending the property round-trip pattern
   and the pure-logic utility tests to more GUI classes. Next
   candidates: the data dialog utilities in `application` and the
   data-coupled element group classes (`SGElementGroupBar` and the
   remaining legend group set models) in `figure`
