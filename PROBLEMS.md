# Known Problems

Findings from a project-wide review of Samurai Graph (as of 2026-09-15, v2.2.0).
Items are ordered by priority.

## 1. Test Coverage


The overall instruction coverage measured by JaCoCo is **28.3%**.

| Package | Coverage | Test files |
|---------|----------|-----------|
| `com.github...lib.mdarray` | 97.4% | 4 |
| `jp...samuraigraph.export` | 68.2% | 1 |
| `com.github...lib.hdf5` | 36.2% | 7 |
| `jp...samuraigraph.base` | 35.4% | 26 |
| `jp...samuraigraph.data` | 33.8% | 59 |
| `jp...samuraigraph.figure` | 28.5% | 44 |
| `jp...samuraigraph.application` | 9.0% | 15 |

- 143 test files / 1345 test executions against 624 main files
- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling limits coverage of the GUI classes
- Headful tests (window / dialog construction) require a running X
  server; on display-less machines run them under a virtual X server
  (see the Testing section of AGENTS.md)
- Integration tests exercise the add-data path of the graph and the
  legend elements for the single SXY, vector, multiple SXY and
  SXYZ data types
- The in-graph group sets are painted on an off-screen image, the
  click handling of the group based on the mouse coordinates is
  keyed against the in-graph group set, and the animation dialog is
  constructed on a real window and disposed
- The full family of the figure property dialogs (the legend, the
  arrows, the axis, the axis scaling, the color bar, the shapes,
  the timing lines, the significant differences and the strings)
  are constructed on the EDT, the NetCDF data setup dialog is
  constructed on a real window and the property dialog utility of
  a drawing window returns empty observer lists without dialogs
- Not covered: the show dialog paths of the property dialog
  utility with real figures and the data setup wizard option
  paths beyond the smoke tests

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

1. **Thicken tests**: keep extending the integration and property
   round trip tests. Remaining candidates: the show dialog paths of
   the property dialog utility with real figures
