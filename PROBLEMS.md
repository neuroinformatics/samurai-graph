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

The class hierarchy for `SGData` / `SGMultiData` / `SGMultiDataMDArray` is a
source of duplication:

- `SGMultiData` and `SGMultiDataMDArray` are two separate classes with a shared
  abstract supertype, but no shared implementation.
- **75+ same-name methods are copied verbatim** between the two siblings
  (`getNData`, `getDataName(int)`, `getData(int)`, `getDataType`, `size`,
  `addData`, `deleteData`, `getNDatum`, `getDatum(int, int)`, `getDataName`,
  `setDataName`, `getNVariable`, `getVariable(int)`, `getDataVariable(int)`,
  `getVariableValue`, `setVariableValue`, `setVariableName`, `setVariableUnits`,
  `setVariableDescription`, `setVariableScale`, `setVariableOffset`,
  `setVariableValueRange`, `getVariableName`, `getVariableUnits`,
  `getVariableDescription`, `getVariableScale`, `getVariableOffset`,
  `getVariableValueRange`, `addVariable`, `deleteVariable`, `hasVariable`,
  `setVariable`, `getVariableIndex`, `hasData`, `hasDataVariable`,
  `setMultiColumnData`, `getMultiColumnData`, `isMultiColumn`, `getNColumns`,
  `getColumnName`, `addColumn`, `deleteColumn`, `hasColumn`, `getColumn`,
  `getColumnIndex`, `hasColumnData`, `setColumnData`, `getColumnValue`,
  `setColumnValueRange`, `getColumnValueRange`, `hasAttribute`,
  `setAttribute`, `getAttribute`, `getAttributeValue`, `removeAttribute`,
  `getNAttributes`, `attributeEquals`, `clone`, `hashCode`, `equals`,
  `toString`).
- Of these, **42 methods exist only in the `SGMultiData` / `SGMultiDataMDArray`
  pair** — they are not in `SGData`, confirming the duplication is specific to
  the two multiple-data siblings rather than inherited from the shared
  abstract supertype.
- The two siblings also duplicate their NetCDF read/write logic
  (`readFromNcFile` / `writeToNcFile`).
- The `figure` package calls `SGMultiData.getData(int)` and
  `SGMultiDataMDArray.getData(int)` in separate code paths, meaning the GUI
  also carries a parallel implementation of the same data-access logic.

**Extraction order** (recommended, for the work still to be done):

1. **Extract the shared `SGMultiData` / `SGMultiDataMDArray` logic** into an
   abstract base class or composition layer. This is the highest-leverage
   refactor — it eliminates ~42 duplicated methods and the duplicated NetCDF
   logic in one pass.
2. Extract `SGUtility` data-model helpers into `SGData` /
   `SGMultiData`-adjacent classes once the multi-data base is stable.
3. Extract `SGDrawingWindow`'s `SGData`-specific helpers once the data-model
   layer is stable.

### 2.2 God classes

24 classes exceed 2,000 lines (15 of them over 2,500):

| Class | Lines | Role |
|---|---|---|
| `SGDrawingWindow` | 4,471 | Main drawing window |
| `SGDrawingWindowBase` | 4,355 | Base drawing window |
| `SGDrawingWindowWithMDArray` | 2,559 | MDArray variant |
| `SGDrawingWindowWithSGData` | 2,327 | SGData variant |
| `SGUtility` | 4,160 | Static utility class, 139 public static methods |
| `SGSetupWindowMDArray` | 2,993 | Setup dialog |
| `SGSetupWindowSGData` | 2,929 | Setup dialog |
| `SGSetupWindowBase` | 2,834 | Setup dialog base |
| `SGData` | 2,519 | Core data model |
| `SGMultiData` | 2,338 | Core data model |
| `SGMultiDataMDArray` | 2,504 | Core data model |

The worst offenders are `SGDrawingWindow` and `SGDrawingWindowBase`, together
roughly 9,000 lines, plus `SGUtility` and the three `SGSetupWindow*` classes.
Most of the 24 classes over 2,000 lines are Swing GUI classes; the data-model
classes (`SGData`, `SGMultiData`, `SGMultiDataMDArray`) also exceed 2,000 lines
each.

Key problems:

- `SGDrawingWindowBase` mixes **window initialization, data binding,
  coordinate/axis management, scale handling, legend handling, and I/O
  (read/write)** in a single class.
- `SGDrawingWindowWithMDArray` and `SGDrawingWindowWithSGData` contain
  hundreds of private methods, most delegating to `SGDrawingWindowBase` —
  they are effectively pass-through subclasses with a large surface area.
- `SGUtility` holds **139 public static methods** spanning NetCDF I/O,
  MDArray operations, data conversion, and GUI helpers. It is a static
  grab-bag that has grown without a clear ownership boundary.
- `SGData` holds both the data model and a large amount of I/O
  (read/write/NetCDF) in the same class.

### 2.3 Data access, I/O and utility logic scattered across layers

- `SGDrawingWindowBase` directly manipulates `SGData` / `SGMultiData` internals
  (column layout, scaling, offset) rather than going through a data-access
  abstraction.
- `SGUtility` contains NetCDF read/write helpers used by both the data model
  and the GUI, so I/O logic is shared via a static utility rather than a
  dedicated I/O service.
- `figure/SGDataIOUtility` and `figure/SGMultiDataIOUtility` are thin wrappers
  around `SGUtility` methods — the actual I/O logic lives in `SGUtility`.

### 2.4 `SGDefaultColumnTypeMDArrayUtility` SXY parsing bug

`parseSXY(String, MDArray)` dereferences `xArray.size(0)` without a null check
on `xArray`. An SXY column-type file with an empty or missing x-array would
throw an NPE rather than producing a clean parse error.

### 2.5 Dependency and build hygiene

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
| `SGDrawingWindowBase` (4,355 LOC) | No direct unit tests; only exercised indirectly through window-construction tests |
| `SGUtility` (4,160 LOC, 139 static methods) | Branch-level coverage of individual static methods is incomplete |
| `hdf5` package (41.5%) | Read/write round-trip coverage is incomplete |
| `data` package (50.2%) | `SGData` I/O paths and edge cases (empty data, single datum) are under-tested |
| `figure` top-level (23.1%) | Most drawing logic is untested; only `figure.dialog` (65.6%) and a few window-construction tests exist |

**Next coverage work:**

1. Characterization tests for the 139 static methods in `SGUtility` (grouped
   by domain: NetCDF I/O, MDArray ops, data conversion, GUI helpers).
2. Round-trip tests for `hdf5` read/write.
3. Edge-case tests for `SGData` / `SGMultiData` I/O.
4. Integration tests for the `export` package and the jpackaged installer.

### 3.1 Known bug (open)

`SGDefaultColumnTypeMDArrayUtility.parseSXY` NPE — see §2.4.

## 4. Healthy Aspects

- `mdarray` package is well-factored and at 97.4% instruction coverage.
- Test isolation is good — no shared mutable state, no absolute paths in tests.
- The build is reproducible: `./mvnw clean verify` produces the fat JAR and
  jpackage inputs deterministically.

## 5. Recommended Actions (Current Work)

1. Extract the shared `SGMultiData` / `SGMultiDataMDArray` logic into an
   abstract base (see §2.1 extraction order, step 1).
2. Fix the SXY parsing NPE (§2.4) and add a regression test.
3. Add characterization tests for `SGUtility` (§3).
4. Document and pin the NetCDF4 system-library vs. classpath dependency
   (§2.5).
