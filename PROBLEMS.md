# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-15, v2.2.0).
Items are ordered by priority.

## 1. Test Coverage

The overall instruction coverage measured by JaCoCo is **19.6%**.

| Package | Coverage | Test files |
|---------|----------|-----------|
| `com.github...lib.mdarray` | 97.4% | 4 |
| `jp...samuraigraph.export` | 68.2% | 1 |
| `com.github...lib.hdf5` | 35.3% | 5 |
| `jp...samuraigraph.base` | 31.5% | 23 |
| `jp...samuraigraph.data` | 32.0% | 50 |
| `jp...samuraigraph.figure` | 12.0% | 27 |
| `jp...samuraigraph.application` | 4.6% | 12 |

- 121 test files / 1292 test executions against 624 main files
- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling limits coverage of the GUI classes
- Headful tests (window / dialog construction) require a running X
  server; on display-less machines run them under a virtual X server
  (see the Testing section of AGENTS.md)
- Not covered: the in-graph group set classes in `figure`
  (`SGElementGroupLineInGraph`, `SGElementGroupSymbolInGraph` and
  friends) and the dialog or window level classes beyond the headful
  smoke tests

## 2. Repository / Dependency Hygiene

- **Vendored code in-tree** (license headers present in all files) is
  thin but carries maintenance risk:
  - `com.github...lib.hdf5` (30 files, ~1.7k LOC): a compatibility shim
    over `io.jhdf` that must track the upstream API changes; reading
    the freshly written files is limited to the dataset level since
    the group enumeration of freshly written files is unreliable
  - `org.freehep...ExportFileTypeRegistry` (125 LOC): replacement for
    the original class that is deliberately excluded from the shaded
    JAR
- **NetCDF4 dependency**: reading NetCDF4 files requires the system
  netcdf-c library (`cdm-core` / `netcdf4` 5.10.0 as runtime deps).
  This is an implicit dependency that cannot be detected at build
  time; when a `.nc` file cannot be opened because the library is
  missing, a warning with installation guidance is logged and the file
  falls back to the text import path. The README documents the symptom
  and the per-OS install packages.

## Recommended Priority

1. **Thicken tests**: keep extending the property round-trip pattern
   and the integration tests to more groups. The next candidate is
   the in-graph integration path with real data in
   `SGFigureElementGraph` covering `SGElementGroupSetInGraphSXY` and
   the line, symbol and bar group implementations in the graph
