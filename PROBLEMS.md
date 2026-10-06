# Samurai Graph — Current State and Known Problems

Coverage snapshot: 2026-10-06, JaCoCo 0.8.12 (`./mvnw clean test jacoco:report`).

## 1. Package Instruction Coverage

| Package | Instr | Missed | Cov | Method | Cov |
|---|---|---|---|---|---|
| `mdarray` | 664 | 17 | 97.4% | 53 | 98.1% |
| `export` | 44 | 14 | 68.2% | 8 | 75.0% |
| `base` | 69,607 | 34,517 | 50.4% | 2,713 | 51.8% |
| `data` | 126,944 | 63,179 | 50.2% | 3,380 | 51.9% |
| `hdf5` | 2,625 | 1,413 | 46.2% | 118 | 89.0% |
| `figure` (top level) | 137,762 | 105,972 | 23.1% | 5,446 | 33.4% |
| `figure.dialog` | 62,808 | 21,582 | 65.6% | 968 | 22.2% |
| `application` | 68,566 | 53,649 | 21.8% | 1,678 | 21.3% |
| `org.freehep...export` | 220 | 29 | 86.8% | 8 | 100.0% |
| **Total** | **469,240** | **280,372** | **40.2%** | **14,372** | **39.8%** |

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

- **jpackage / `export` package coverage**: the `export` package at 68.2% and
  the jpackaged installer build have limited automated verification. The
  installer is a native artifact that is not covered by unit tests and is only
  smoke-tested manually.

### 2.5 The HDF5 writer flattens multidimensional datasets

The typed `writeMDArray` methods of `Hdf5WriterAdapter` (float64, int32,
string) write `array.getFlatArrayRef()`, so a multidimensional array is
stored as a 1-D dataset and its shape is lost. The convenience writers in
the same adapter (`writeDoubleArray` / `writeIntArray` /
`writeDoubleMatrix` / `writeIntMatrix`) preserve the shape instead.

- `SGMDArrayData.saveToArchiveDataSetFileAsHDF5File` (lines 1474-1485)
  round-trips each variable through `readMDArray` (shape-preserving) and
  `writeMDArray` (flattening), so saving a 2-D HDF5 dataset as an archive
  downgrades it to a 1-D dataset. The same-format export path
  (`saveToHDF5File` via the `write` overloads, lines 1683-1724) writes 2-D
  data through `writeDoubleMatrix` / `writeIntMatrix` and keeps the shape.
- `HDF5RoundTripTest.typedMdArrayWriteFlattensTwoDimensionalArrays` pins
  the current flattening behavior.

## 3. Test Gaps

| Area | Status |
|---|---|
| `SGDrawingWindow` (3,660 LOC) | No direct unit tests for the window logic; exercised indirectly through window-construction tests (e.g. `SGDrawingWindowPropertyIOTest`) |
| `SGUtility` (2,784 LOC, 139 static methods) | The modal `show*MessageDialog` / `showConfirmationDialog` / `showColorSelectionDialog` display helpers, the `moveObjectTo*` list wrappers, and a few font/canonical-path accessors are untested (class coverage: 77.3% instruction, 78.8% branch) |
| `hdf5` package (46.2%) | Data-array read/write round-trip is covered; the int64 read path, the 3-D / long 2-D flatten helpers, and the `IHDF5ObjectReader` group/attribute inspection methods are untested |
| `data` package (50.2%) | SXY single/multiple data I/O paths and edge cases (empty data, single datum) are under-tested |
| `figure` top-level (23.1%) | Most drawing logic is untested; only `figure.dialog` (65.6%) and a few window-construction tests exist |

**Next coverage work:**

1. `SGUtility` modal display helpers
   (`show*MessageDialog`, `showConfirmationDialog`, `showColorSelectionDialog`)
   need a headful harness; a few list/font/canonical-path accessors remain
   untested.
2. Edge-case tests for the SXY single/multiple data I/O paths.
3. Integration tests for the `export` package and the jpackaged installer.

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
2. Test the remaining `SGUtility` static methods (§3): the headful
   modal display helpers (`show*MessageDialog`, `showConfirmationDialog`,
   `showColorSelectionDialog`) and a few list/font/canonical-path accessors.
3. Decide whether the typed HDF5 `writeMDArray` methods should preserve the
   array shape (§2.5); the archive-save path currently loses 2-D shape
   through the flattening write.
