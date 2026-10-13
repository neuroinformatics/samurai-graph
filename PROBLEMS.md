# Samurai Graph — Current State and Known Problems

Coverage snapshot: 2026-10-14, JaCoCo 0.8.12 (`./mvnw clean test jacoco:report`).

## 1. Package Instruction Coverage

| Package | Instr | Missed | Cov | Method | Cov |
|---|---|---|---|---|---|
| `mdarray` | 664 | 17 | 97.4% | 53 | 98.1% |
| `export` | 44 | 0 | 100.0% | 8 | 100.0% |
| `base` | 69,607 | 33,327 | 52.1% | 2,713 | 53.7% |
| `data` | 126,951 | 58,532 | 53.9% | 3,380 | 53.5% |
| `hdf5` | 2,775 | 263 | 90.5% | 121 | 97.5% |
| `figure` (top level) | 137,762 | 105,972 | 23.1% | 5,446 | 33.4% |
| `figure.dialog` | 62,808 | 21,582 | 65.6% | 968 | 22.2% |
| `application` | 68,566 | 49,891 | 27.2% | 1,678 | 21.3% |
| `org.freehep...export` | 220 | 5 | 97.7% | 8 | 100.0% |
| **Total** | **469,397** | **269,589** | **42.6%** | **14,375** | **40.6%** |

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

- **jpackage installer**: the jpackaged installer build has limited automated
  verification. The installer is a native artifact that is not covered by unit
  tests and is only smoke-tested manually. (The `export` package it feeds is now
  fully covered — `export` at 100%, `org.freehep...export` at 97.7% — leaving only
  the installer artifact itself untested.)

### 2.5 The HDF5 writer supports only up to the rank the reader can convert

The typed `writeMDArray` methods of `Hdf5WriterAdapter` (float64, int32,
string) store a dataset with the shape of the source `MDArray`: float64 keeps
the 1-D, 2-D and 3-D shapes, int32 keeps 1-D and 2-D, and string stores a
1-D dataset. The rank is chosen per type because the reader converts a
dataset to an `MDArray` from a nested array of at most that rank:
`convertToMDDoubleArray` accepts 1-D, 2-D and 3-D data,
`convertToMDIntArray` 1-D and 2-D, and `convertToMDStringArray` 1-D only.
An array whose rank the reader cannot convert is written as a flat 1-D
dataset so the values still round-trip. The convenience writers
(`writeDoubleArray` / `writeIntArray` / `writeDoubleMatrix` /
`writeIntMatrix`) preserve the shape the same way.

- `SGMDArrayData.saveToArchiveDataSetFileAsHDF5File` (lines 1474-1485)
  round-trips each variable through `readMDArray` and `writeMDArray`, so an
  archive save keeps the shape of every dataset the reader can convert. The
  same-format export path (`saveToHDF5File` via the `write` overloads, lines
  1683-1724) writes 2-D data through `writeDoubleMatrix` / `writeIntMatrix`.
- `SGSXYZSDArrayData` and `SGVXYSDArrayData` write their 2-D grid values
  (`z`, `f`/`s`) through the float64 `writeMDArray`, so their grid files
  store 2-D datasets.

## 3. Test Gaps

| Area | Status |
|---|---|
| `SGDrawingWindow` (3,660 LOC) | No direct unit tests for the window logic; exercised indirectly through window-construction tests (e.g. `SGDrawingWindowPropertyIOTest`) |
| `SGUtility` (2,784 LOC, 139 static methods) | Only unreachable or platform-specific defensive branches remain untested (class coverage: 99.5% instruction, 98.8% branch, 100% method): the `moveObjectTo`-failure returns inside `moveObject` / `moveObjectRawList`, the `getFileName` forward-slash fallback for Windows paths, the `assignIdNumber` `Error` guard and its loop exit, the `PermittingEmptyString` empty-string rechecks shadowed by the earlier return, the `getVersionNumber` defensive `property == null` check, and the `setupMediaTracker` `InterruptedException` catch |
| `hdf5` package (90.5%) | The remaining 263 missed instructions are unreachable through jhdf or defensive fallbacks: the `mapDataClass` cases jhdf never reports (opaque, compound, reference, enum, vlen, time), the dead `Hdf5WriterAdapter$FileReader` (the `IHDF5Writer` interface exposes no `file()` accessor), the five convenience-writer `catch` blocks (jhdf buffers `putDataset` in memory, so a failing write is not thrown until `close`), the `getDataset` by-name fallback, the `byte[][]`/`Number[]`/`String.valueOf` attribute-coercion fallbacks jhdf never produces, and the `close()` error handlers |
| `export` package (100.0%) | Fully covered. The `org.freehep...export` registry is at 97.7%: only the outer `getResources` `IOException` catch (a defensive handler for an unreadable classloader) remains unreachable in tests |
| `data` package (53.9%) | The backend setup dialogs remain under-tested |
| `application` package (27.2%) | `SGDataCreator` (2,775 LOC) now at 77.1% with the factory-path test suite; the remaining covered-unit gap is the dialog and menu surface of `SGMainFunctions` |
| `figure` top-level (23.1%) | Most drawing logic is untested; only `figure.dialog` (65.6%) and a few window-construction tests exist |

**Next coverage work:**

1. The `data` package (53.9%): the multiple-data SXY classes
   `SGSXYNetCDFMultipleData` (4,510 missed) and `SGSXYMDArrayMultipleData` (4,104
   missed) are the largest untested units after `SGDataCreator`.

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
