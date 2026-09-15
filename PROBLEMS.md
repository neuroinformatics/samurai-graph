# Known Problems

Findings from a project-wide, all-architecture review of Samurai Graph
(as of 2026-09-15, v2.2.0). Items are ordered by priority.

## 1. Overview

Package sizes and JaCoCo instruction coverage:

| Package | Files | LOC | Coverage | Test files |
|---------|------:|------:|----------|-----------:|
| `com.github...lib.mdarray` | - | - | 97.4% | 4 |
| `jp...samuraigraph.export` | 2 | 54 | 68.2% | 1 |
| `com.github...lib.hdf5` | 30 | ~1.7k | 38.6% | 6 |
| `jp...samuraigraph.base` | 179 | 45,193 | 40.1% | 30 |
| `jp...samuraigraph.data` | 135 | 69,291 | 33.8% | 59 |
| `jp...samuraigraph.figure` | 185 | 112,960 | 35.0% | 53 |
| `jp...samuraigraph.application` | 92 | 37,398 | 20.0% | 20 |

- Overall instruction coverage is **33.4%** (155 test files / 1393 test
  executions against 624 main files)
- The type-level dependency DAG (`base` <- `data` <- `figure` <-
  `application`) is respected for regular imports; the problems below
  stem from constants (static imports), duplicated backends and
  oversized classes rather than from import cycles
- The low coverage of `figure` and `application` and the architectural
  problems in section 2 share the same root causes (Swing coupling,
  2,000+ line classes)

## 2. Architecture

### 2.1 Constants + static star imports break the layering (Critical)
**RESOLVED (2026-09-15):** all upward static constant imports below
`application` (307 in `base`, 646 in `data`, 42 in `figure`) were
analyzed and turned out to be entirely unused, so they were removed;
no constants needed to be moved.

- 45 `*Constants` classes across `base`/`data`/`figure`/`application`,
  referenced via **3,742** `import static ...Constants.*` statements
- ~~Constants leak back down the layers~~ (resolved): `base` ->
  `figure` (241 sites), `base` -> `application` (66),
  `data` -> `figure` (532), `data` -> `application` (114) and
  `figure` -> `application` (42) were all unused wildcard imports
  that could be removed without moving any constant
- Worst case example from the review: `SGAnimationThread`
  wildcard-static-imported from 31 files in all four packages
- Practical impact during the review: `base`/`data` could not be
  reused or tested in isolation, and locating the owning package of a
  given constant (e.g. `KEY_FRAME_RATE`) required searching all
  Constants files
- Resolution: the 995 upward import lines were removed in three
  commits; the lower three layers no longer reference upper layers
  through static imports

- The three multiple-data classes differ in only ~27 public method
  signatures yet are joined only by the marker-ish
  `SGISXYTypeMultipleData`
- Bug fixes must be applied three times (a fix landing on one backend
  but not the others is a realistic failure mode), and combined with
  the coverage deficit this is the highest-regression risk area
- Mitigation: pull shared logic up into an intermediate base class
  incrementally, starting from the next bug fix that touches several
  backends

**Consolidation progress (as of 2026-09-15):** the following
duplicated members have been unified.

- `SGXYNumberFormat` (package-private holder shared by the six SXY
  data classes): shift, exponent and decimal places, extended with
  the date format; the MDArray classes intentionally decode dates
  elsewhere, so their date handling stays per backend
- `SGArrayData`: the tick label stride state with the
  clone-on-read getter and its setter, the string number getter,
  the array stride state with the main stride getter, the stride
  based points number getter and the cache restore logic shared
  through the static helpers on `SGDataMiscUtility`
- `SGISXYTypeData`: the tick label value indices getter as a
  default method using the interface-facing accessors

A `javap`-level API analysis of the SXY multiple triplet (member count
including package-private, 2026-09-15):

- **76 methods** have the same name in all three classes: the value
  access surface (`getXValueArray`, `getYValueArray`, date/tick label
  and error bar accessors), the property I/O (`getProperties`,
  `setProperties`, `writeProperty`, `merge`, `matches`) and the whole
  stride/pick-up family (`setColumnType` with a
  `SGPickUpDimensionInfo`, `setStrideMap`, `getStrideMap`,
  `getTickLabelStride`, ...)
- **42 methods** are shared only between the NetCDF and the MDArray
  variants: they operate on variable arrays instead of the
  `Integer[]` column indices kept by the SDArray backend
  (e.g. `getXVariables`, `setLowerErrorVariables`): the variable-object
  API converges while the SDArray backend still stores plain index
  arrays with an equivalent method shape by name
- only 4 fields and no additional inherited contracts are common, so
  a shared base class can own the column-type/pick-up/stride state
  without disturbing the backend-specific variables
- signature typing differs (e.g. `Integer[]` indices vs
  `SGNetCDFVariable[]`/`SGMDArrayVariable[]`), so the consolidation
  pattern is "same name, same contract": extract the shared logic into
  an intermediate base class typed against the backend-neutral
  abstractions (`SGPickUpDimensionInfo`, `SGDataColumnInfo`), and let
  each backend override only the typed variable access

Recommended extraction order (API-stable surface first):

1. cache/properties plumbing (`useCache`, `restoreCache`,
   `getProperties`/`setProperties`, date format and decimal places)
2. column type + pick-up dimension handling (`setColumnType`,
   `setColumnTypeDimensionNotPicked`,
   `setColumnTypeDimensionPicked`, `isDimensionPicked`,
   `getPickUpDimensionInfo`, `setPickUpDimensionInfo`,
   `updateDimensionIndices`)
3. stride handling (`setStride`, `setTickLabelStride`,
   `getStrideMap`, `setArraySectionPropertySub`)
4. value access (shift/exponent/date array handling)

### 2.3 Concentration of 2,000+ line classes (Major)

24+ classes exceed 2,000 lines, the largest being:

- `application/SGMainFunctions.java` (3,782 lines) - startup, file
  open/reload, dialogs, command mode, and
  WindowListener/ActionListener/Runnable in one class; `openFile` alone
  spans 400+ lines
- `base/SGDrawingWindow.java` (3,660 lines) - Swing window + graph
  management + data operations
- `figure/SGAxisElement.java` (3,688), `SGPropertyDialogSXYData.java`
  (3,625), `SGElementGroupSetInGraphSXYMultiple.java` (3,616)

These are a direct cause of the low `application` coverage (20.0%) and
make headless testing structurally impossible.

### 2.4 Mixed responsibilities in `figure` (Medium)

185 files / ~113k lines mixing:

- the drawing model hierarchy `SGDrawingElement*` -> `SGElementGroup*`
  -> `SGElementGroupSetInGraph*`
- Swing dialogs (`SGArrowDialog`, `SGAxisDialog`, `SGPropertyDialog*`,
  ...)
- 12 Constants files and **20 per-dialog Observer interfaces**
  (`SGIArrowDialogObserver`, `SGIAxisDialogObserver`, ...)

The dialog-in-model-package layout is also a main driver of the
`data` -> `figure` backflow (see 2.1); the one-observer-per-dialog
pattern could be merged into a common event notification abstraction.

**Split progress (as of 2026-09-15):** the dialog side now lives in
the dedicated `figure.dialog` subpackage: the twenty per-dialog
Observer interfaces and the Swing dialog classes including the
panels (34 classes in total, the former `figure` package remains
for the drawing model, the constants and data side elements).

### 2.5 `base` as a grab-bag (Medium)

### 2.5 `base` as a grab-bag (Medium)

Although a primitive layer, `base` contains:

- UI panels (`SGClientPanel` 2,490 lines, `SGAxisSelectionPanel`)
- `SGUtility` (2,784 lines, **139 public static methods**) and
  `SGUtilityText` (2,217 lines) acting as catch-all utility dumps
- domain logic such as `SGAnimationThread` and date handling
  (`SGAxisDateValue`)

String-based value parsing helpers (`SGUtility.xxxStaticValue`) also
make static analysis hard in this layer.

### 2.6 Minor issues

- `export` (2 files, 54 lines) has little presence; export logic mostly
  lives in `application/SGImageExportManager` and the ported FreeHEP
  classes
- The `plugins/jna` boundary is otherwise sound, but
  `SGDataPluginConstants` is static-imported from `base` (same root
  cause as 2.1) and the plugin contract lives in the `application`
  package

## 3. Test Coverage

The overall instruction coverage measured by JaCoCo is **33.4%**
(seen per package in section 1). Current state:

- File-based tests cover the main import paths (NetCDF, MATLAB, HDF5,
  CSV)
- The heavy Swing/AWT coupling limits coverage of the GUI classes
- Headful tests (window / dialog construction) require a running X
  server; on display-less machines run them under a virtual X server
  (see the Testing section of AGENTS.md)
- Integration tests exercise the add-data path of the graph and the
  legend elements for the single SXY, vector, multiple SXY and SXYZ
  data types
- The in-graph group sets are painted on an off-screen image, the click
  handling of the group based on the mouse coordinates is keyed against
  the in-graph group set, and the animation dialog is constructed on a
  real window and disposed
- The significant difference drawing element can be constructed as a
  headless stub with the magnification override, and the full family of
  the figure property dialogs (the legend, the arrows, the axis, the
  axis scaling, the color bar, the shapes, the timing lines, the
  significant differences and the strings) are constructed on the EDT,
  the data property dialogs of the SXY, VXY and SXYZ groups are
  constructed on a real window and the axis break dialog and the data
  pop-up menus are constructed on the window with the graph and the
  data
- The XY figure is created with a real window, the axis break element
  rejects symbols outside the graph rect and the significant difference
  element family is exercised on the graph
- Not covered: the show dialog paths of the property dialog utility
  with real figures and the data setup wizard option paths beyond the
  smoke tests

Because of the coverage level, any refactoring of the areas in
section 2 must be preceded by characterization tests (file I/O round
trips).

## 4. Repository / Dependency Hygiene

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

## 5. Healthy Aspects

- The type-level dependency DAG is respected; no import cycles in
  regular imports
- `SGAnimationThread` has been modernized from a raw thread to
  `ExecutorService` + `SwingUtilities.invokeAndWait`
- The in-house libs are localized and well tested (mdarray 97.4%)
- File I/O is concentrated in `SGDataCreator` et al., with some
  testable units
- Existing integration and property round trip tests already cover the
  main import paths

## 6. Recommended Action Plan

In cost-benefit order. Every refactoring step must be preceded by
characterization tests (file I/O round trips) given the current
coverage level.

1. **Thicken tests**: keep extending the integration and property round
   trip tests. Remaining candidates: the figure-level column type
   updater, the drawing window alignment utility and the data handler
   interactions
2. ~~Fix the static-constant dependency direction (2.1)~~ **DONE
   (2026-09-15)**: the upward static imports in `base` (307), `data`
   (646) and `figure` (42) were all unused and were simply removed;
   no constants needed to be moved
3. **Consolidate the `data` triple hierarchy (2.2)**: pull common logic
   into intermediate base classes, following the documented analysis
   (76 name-compatible methods, extraction order listed in 2.2)
4. **Extract file operation logic from `SGMainFunctions.openFile` and
   `SGDrawingWindow` (2.3)**: fold into the existing
   `SGFileHandler`/`SGArchiveFileCreator` to make them
   headless-testable
5. **Split `figure` into `figure/model` and `figure/dialog` (2.4)**;
   unify the Observer interfaces afterwards. **Partial progress:** the
   Observer interfaces and the dialog classes are in `figure.dialog`
   now; the remaining steps are the observer unification and, if
   worthwhile, a further split of the drawing model side
