# Samurai Graph — Current State and Known Problems

Coverage snapshot: 2026-10-05, JaCoCo 0.8.12 (`./mvnw clean test jacoco:report`).

## 1. Package Instruction Coverage

| Package | Instr | Missed | Cov | Method | Cov |
|---|---|---|---|---|---|
| `mdarray` | 2757 | 72 | 97.4% | 214 | 99.1% |
| `export` | 4102 | 1304 | 68.2% | 400 | 81.8% |
| `base` | 4296 | 2170 | 49.5% | 616 | 74.0% |
| `data` | 4656 | 2312 | 50.2% | 306 | 73.5% |
| `hdf5` | 6500 | 3800 | 41.5% | 420 | 73.3% |
| `figure` (top level) | 33646 | 25891 | 23.1% | 2870 | 61.4% |
| `figure.dialog` | 2486 | 858 | 65.6% | 449 | 84.0% |
| `application` | 1207 | 944 | 21.8% | 76 | 55.3% |
| **Total** | **58040** | **34351** | **40.8%** | **5351** | **75.6%** |

## 2. Current Problems

### 2.1 Data model: triple multiple-data hierarchy

The multiple-data model consists of three sibling classes, one per storage
backend:

| Class | Lines | Extends | Also implements |
|---|---|---|---|
| `SGSXYNetCDFMultipleData` | 3,405 | `SGNetCDFData` | `SGISXYMultipleDimensionData` |
| `SGSXYMDArrayMultipleData` | 3,207 | `SGMDArrayData` | `SGISXYMultipleDimensionData` |
| `SGSXYSDArrayMultipleData` | 2,705 | `SGSDArrayData` | — |

All three implement `SGISXYTypeMultipleData`. The backend parents are all
under `SGArrayData` (498 lines), which extends `base/SGData` (217 lines).

Measured duplication (name + signature scan of the three classes):

- **50 methods are shared by all three classes**, but only **one**
  (`getNumberFormat()`, a one-line getter) has a verbatim-identical body.
  It cannot be lifted because it returns a per-class `private` `mFormat`
  field.
- The NetCDF and MDArray siblings share **82 methods** in total (32 of them
  not in the SDArray variant); only **7** are verbatim-identical, and all 7
  are trivial one-to-three-line accessors or delegations that touch
  per-backend private fields or `super`.
- The remaining same-signature methods are **parallel implementations, not
  copy-paste**: each backend stores its child columns as a different element
  type (`SGNetCDFVariable[]`, `SGMDArrayVariable[]`, or `Integer[]` column
  indices), so the bodies differ even where signatures match.

Structural constraint: Java single inheritance binds each class to a
different backend parent, so **no shared abstract base can be inserted**
among the three without restructuring the backend hierarchy. The codebase's
pattern for shared logic is **interface default methods that delegate to
static utilities**: `SGISXYTypeMultipleData` carries 25 defaults delegating
to `SGDataBufferUtility`, `SGDataViewerUtility`, `SGDataRangeUtility`, and
`SGDataMiscUtility`, and `SGISXYTypeData` carries 9. Per-class collaborators
hold the extracted backend-specific logic
(`SGSXYNetCDFMultipleDataAccess`, `SGSXYNetCDFMultipleDataPropertyIO`,
`SGSXYMDArrayMultipleDataExporter`).

Shared accessors such as `getDataBuffer(SGDataBufferPolicy)` are provided
as default methods on `SGISXYTypeSingleData` and `SGISXYTypeMultipleData`,
with regression tests on real data objects for all three backends.

**Residual risk:** a behavioral fix that spans all backends must still be
applied in up to three backend-specific implementations (and mirrored in the
single-data trio for the common SXY surface). New shared logic should go
into interface defaults or static utilities, not class bodies.

### 2.2 God classes

28 classes exceed 2,000 lines (17 of them over 2,500). The largest:

| Class | Lines | Role |
|---|---|---|
| `figure/SGAxisElement` | 3,685 | Axis element |
| `base/SGDrawingWindow` | 3,660 | Main drawing window (JFrame) |
| `figure.dialog/SGPropertyDialogSXYData` | 3,631 | SXY data property dialog |
| `figure/SGElementGroupSetInGraphSXYMultiple` | 3,602 | Element group for multiple SXY data |
| `data/SGMDArrayDataSetupPanel` | 3,598 | MDArray data setup panel |
| `application/SGMainFunctions` | 3,597 | Application entry and menu actions |
| `base/SGFigure` | 3,487 | Figure model |
| `data/SGSXYNetCDFMultipleData` | 3,405 | Multiple SXY data, NetCDF backend (§2.1) |
| `data/SGSXYMDArrayMultipleData` | 3,207 | Multiple SXY data, MDArray backend (§2.1) |
| `figure/SGFigureElementAxis` | 3,166 | Axis figure element |
| `figure.dialog/SGAxisDialog` | 3,082 | Axis property dialog |
| `figure/SGFigureElementSignificantDifference` | 3,060 | Significant-difference element |

Beyond the top 12: `SGFigureElementLegend` (2,810), `SGUtility` (2,784),
`SGDataCreator` (2,775), `SGNetCDFData` (2,745),
`SGSXYSDArrayMultipleData` (2,705), `SGClientPanel` (2,463), and 10 more
between 2,062 and 2,306 lines. Most of the 28 classes over 2,000 lines are
Swing GUI classes in `figure` and `base`.

Key problems:

- `SGDrawingWindow` (3,660 lines, `extends JFrame`) is a single class that
  still mixes window management with figure and data operations (it has its
  own `readProperty` and `doOutputDataToFile` over `SGData`). Part of the
  responsibility has already been extracted into `SGDrawingWindow*`
  collaborators (ActionHandler, AlignmentUtility, Clipboard, ExportHelper,
  GeometryHelper, ObjectHelper, PropertyDialogUtility, PropertyIO,
  UndoUtility, ViewportUtility), but the remainder is still large.
- `SGUtility` (2,784 lines) holds **139 public static methods**. It is a
  static grab-bag with no clear ownership boundary, called directly from 66
  classes in `figure` and 57 classes in `data`.
- The three multiple-data SXY classes (§2.1) each exceed 2,700 lines, and
  `SGNetCDFData` (2,745) is the largest non-SXY data class.
- The setup panels have diverged under the shared abstract
  `SGDataSetupPanel`: `SGMDArrayDataSetupPanel` (3,598 lines) vs
  `SGNetCDFDataSetupPanel` (1,569) vs `SGSDArrayDataSetupPanel` (582).

### 2.3 Data access, I/O and utility logic scattered across layers

- Data property I/O is implemented per backend class: each of the three
  multiple-data SXY classes has its own `writeProperty` over its
  backend-specific element type, plus per-class collaborators
  (`SGSXYNetCDFMultipleDataPropertyIO`,
  `SGSXYNetCDFMultipleDataAccess`, `SGSXYMDArrayMultipleDataExporter`).
  There is no shared data-I/O service.
- `SGUtility` static helpers are shared across layers instead of a service:
  57 classes in `data` and 66 in `figure` call them directly (property
  validation, value computation, text/date parsing, GUI helpers).
- `SGDrawingWindow` operates on `SGData` objects directly (e.g.
  `readProperty`, `doOutputDataToFile`) rather than going through a
  data-access abstraction.
- The figure layer consumes the data model through
  `SGISXYTypeMultipleData` casts with per-type `instanceof` branches (e.g.
  `SGElementGroupSetInGraphSXYMultiple`), so data-type-specific behavior
  also lives in the GUI.

### 2.4 Dependency and build hygiene

- **NetCDF4 system-library dependency**: the data model assumes a system
  NetCDF4 library is available at runtime. The project ships its own `nc`
  (NetCDF-Java) in the classpath, but the documentation and build do not
  clearly state which is authoritative, and the test suite does not exercise
  the system-library path.
- **jpackage / `export` package coverage**: the `export` package at 68.2% and
  the jpackaged installer build have limited automated verification. The
  installer is a native artifact that is not covered by unit tests and is only
  smoke-tested manually.

## 3. Test Gaps

| Area | Status |
|---|---|
| `SGDrawingWindow` (3,660 LOC) | No direct unit tests for the window logic; exercised indirectly through window-construction tests (e.g. `SGDrawingWindowPropertyIOTest`) |
| `SGUtility` (2,784 LOC, 139 static methods) | Pure and headless-safe static methods covered via `SGUtilityTest`, `SGUtilityTextTest`, `SGUtilityNumberTest`, `SGUtilityRectangleTest`, `SGUtilityPropertyValueTest`, `SGUtilityMenuTest` (instruction 77.3%, branch 78.8%). Still untested: the modal `show*MessageDialog`/`showConfirmationDialog`/`showColorSelectionDialog` display helpers, the `moveObjectTo*` list wrappers, and a few font/canonical-path accessors |
| `hdf5` package (41.5%) | Read/write round-trip coverage is incomplete |
| `data` package (50.2%) | SXY single/multiple data I/O paths and edge cases (empty data, single datum) are under-tested |
| `figure` top-level (23.1%) | Most drawing logic is untested; only `figure.dialog` (65.6%) and a few window-construction tests exist |

**Next coverage work:**

1. Remaining `SGUtility` static methods: the modal display helpers
   (`show*MessageDialog`, `showConfirmationDialog`, `showColorSelectionDialog`)
   need a headful harness, plus a few remaining list/font/canonical-path
   accessors. (The pure and headless-safe value, rectangle, menu and
   visibility helpers are now covered.)
2. Round-trip tests for `hdf5` read/write.
3. Edge-case tests for the SXY single/multiple data I/O paths.
4. Integration tests for the `export` package and the jpackaged installer.

### 3.1 Resolved defects

- **SXY default-column NPE**: the no-property overload
  `SGDefaultColumnTypeMDArrayUtility.getForSXYMDArrayData` dereferenced the
  result of `extractDimensions` (`yDimList.size()`) before its null check, so
  an SXY data file whose variables have no dimensions threw a
  `NullPointerException` instead of returning `false`. The null/empty check
  now runs before the size cap, and the regression test
  `theSXYDataFailsWithoutDimensionedVariables` pins the clean failure.

## 4. Healthy Aspects

- `mdarray` package is well-factored and at 97.4% instruction coverage.
- Test isolation is good — no shared mutable state, no absolute paths in tests.
- The build is reproducible: `./mvnw clean verify` produces the fat JAR and
  jpackage inputs deterministically.

## 5. Recommended Actions (Current Work)

1. Continue consolidating backend-neutral SXY logic in interface default
   methods and static utilities (the §2.1 pattern); a shared abstract base
   is infeasible because the three multiple-data classes extend three
   different backend parents.
2. Add characterization tests for `SGUtility` (§3).
3. Document and pin the NetCDF4 system-library vs. classpath dependency
   (§2.4).
