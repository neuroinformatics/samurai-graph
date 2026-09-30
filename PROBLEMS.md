# Known Problems

Findings from a project-wide, all-architecture review of Samurai Graph
(as of 2026-09-29, v2.2.0). Items are ordered by priority.

## 1. Overview

Package sizes and JaCoCo instruction coverage (re-measured 2026-09-29,
`./mvnw clean test` with JDK 21, then a `jacoco:report`):

| Package | Files | LOC | Coverage | Test files |
|---------|------:|------:|----------|-----------:|
| `com.github...lib.mdarray` | 4 | 378 | 97.4% | 4 |
| `jp...samuraigraph.export` | 2 | 54 | 68.2% | 0 |
| `com.github...lib.hdf5` | 26 | 1,585 | 38.8% | 7 |
| `jp...samuraigraph.base` | 180 | 44,503 | 43.1% | 41 |
| `jp...samuraigraph.data` | 136 | 66,956 | 34.0% | 57 |
| `jp...samuraigraph.figure` | 186 | 112,405 | 23.1% | 63 |
| `jp...samuraigraph.figure.dialog` | 41 | 26,381 | 65.6% | 0 |
| `jp...samuraigraph.application` | 104 | 37,699 | 21.8% | 26 |

- Overall instruction coverage is **34.7%** (200 test classes / 1732
  test executions against 639 main files)
- The `figure` row above is the drawing-model package only; the figure
  dialogs and their observers live in `figure.dialog` (65.6%) and the
  combined `figure` + `figure.dialog` surface is 36.4%
- The type-level dependency DAG (`base` <- `data` <- `figure` <-
  `application`) is respected for regular imports; the problems below
  stem from duplicated backends and oversized classes rather than from
  import cycles
- The low coverage of `figure` and `application` shares the root
  causes of the architectural problems in section 2 (Swing coupling,
  2,000+ line classes)

## 2. Architecture

### 2.1 The multiple-data triple hierarchy (Major)

The three multiple-data classes (`SGNetCDFMultipleData`,
`SGMDArrayMultipleData`, `SGSDArrayMultipleData`) differ in only ~27
public method signatures yet are joined only by the marker-ish
`SGISXYTypeMultipleData`.

- **76 methods** share the same name in all three classes (the value
  access surface, the property I/O and the whole stride/pick-up
  family), and **42 methods** are shared only between the NetCDF and
  MDArray variants (they operate on variable arrays instead of the
  `Integer[]` column indices kept by the SDArray backend)
- Bug fixes must be applied three times (a fix landing on one backend
  but not the others is a realistic failure mode), and combined with
  the coverage deficit this is the highest-regression risk area
- The cache/properties plumbing and the simple bounds/delegation
  surface have already been pulled up as interface default methods
  (`SGISXYTypeMultipleData`/`SGISXYTypeData`), and the
  `setColumnTypeWithPickUp` dispatch chain is shared; the remaining
  residue is the typed name-to-dimension-index bookkeeping of the
  MDArray backend (`updateDimensionIndices`) and the typed
  `setPickUpDimensionInfo` validation, both requiring hook accessors

Recommended remaining extraction order (API-stable surface first):

1. column type + pick-up dimension handling (`setColumnType`,
   `setColumnTypeDimensionNotPicked`,
   `setColumnTypeDimensionPicked`, `isDimensionPicked`,
   `getPickUpDimensionInfo`, `setPickUpDimensionInfo`,
   `updateDimensionIndices`)
2. stride handling (`setStride`, `setTickLabelStride`,
   `getStrideMap`, `setArraySectionPropertySub`)
3. value access (shift/exponent/date array handling)

### 2.2 Concentration of 2,000+ line classes (Major)

24+ classes exceed 2,000 lines, the largest being:

- `application/SGMainFunctions.java` (3,782 lines) - startup, file
  open/reload, dialogs, command mode, and
  WindowListener/ActionListener/Runnable in one class; `openFile` alone
  spans 400+ lines
- `base/SGDrawingWindow.java` (3,660 lines) - Swing window + graph
  management + data operations
- `figure/SGAxisElement.java` (3,688), `SGPropertyDialogSXYData.java`
  (3,625), `SGElementGroupSetInGraphSXYMultiple.java` (3,616)

These are a direct cause of the low `application` coverage and make
headless testing structurally impossible. The file open flow of
`SGMainFunctions` is already extracted into headless-testable units
(`SGFileOpenCategorizer`/`SGFileOpenHandler`, `SGDataReloader`,
`SGEmbeddedContentReader`); the remaining work is the `SGDrawingWindow`
side.

### 2.3 Mixed responsibilities in `figure` (Medium)

The `figure` package mixes the drawing model hierarchy
(`SGDrawingElement*` -> `SGElementGroup*` -> `SGElementGroupSetInGraph*`),
12 Constants files and the Swing dialogs. The dialog side has been
moved to the dedicated `figure.dialog` subpackage; a further split of
the drawing model side remains possible.

The twenty per-dialog Observer interfaces are type-safe contracts with
almost no shared method surface, so a generic event unification was
evaluated and rejected; a common role marker `base/SGIDialogObserver`
names the common supertype, and all observers extend it through
`base/SGIPropertyDialogObserver` where available.

### 2.4 `base` as a grab-bag (Medium)

Although a primitive layer, `base` contains:

- UI panels (`SGClientPanel` 2,490 lines, `SGAxisSelectionPanel`)
- `SGUtility` (2,784 lines, **139 public static methods**) and
  `SGUtilityText` (2,217 lines) acting as catch-all utility dumps
- domain logic such as `SGAnimationThread` and date handling
  (`SGAxisDateValue`)

String-based value parsing helpers (`SGUtility.xxxStaticValue`) also
make static analysis hard in this layer.

### 2.5 Minor issues

- `export` (2 files, 54 lines) has little presence; export logic mostly
  lives in `application/SGImageExportManager` and the ported FreeHEP
  classes
- The `plugins/jna` boundary is otherwise sound, but
  `SGDataPluginConstants` is static-imported from `base` and the plugin
  contract lives in the `application` package

## 3. Test Coverage

The overall instruction coverage measured by JaCoCo is **34.7%**
(seen per package in section 1; re-measured 2026-09-29 with
`./mvnw clean test` on JDK 21). Current state:

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

Headless characterization units added so far (2026-09-16 to
2026-09-29): `SGXYNumberFormatTest` (8), `SGPropertyMapTest` (15),
`SGPropertyResultsTest` (7), `SGDataBufferPolicyTest` (4),
`SGDataValueHistoryTest` (7), `SGIntegerSeriesTest` (29),
`SGPropertyUtilityTest` (8), `SGSXYNetCDFMultipleDataPropertyIOTest`
(5, file I/O against the real `Example16.nc`),
`SGSimpleSymbol2DTest` (13), `SGDrawingElementRectangleTest` (14),
`SGElementGroupBarTest` (8), `SGBufferedFileWriterTest` (4),
`SGNamedStringBlockTest` (11), `SGFigureElementGridPropertiesTest`
(6), `SGDrawingElementScalePropertiesTest` (5),
`SGAxisElementAxisPropertiesTest` (9), `SGDataValueHistoryDimTest`
(18), `SGColorMapColorMapPropertiesTest` (8),
`SGColorMapManagerRepeatedPropertiesTest` (7),
`SGColorMapManagerMultiplePropertiesTest` (5),
`SGDataValueHistoryD1Test` (15),
`SGDrawingElementStringPropertiesTest` (7),
`SGElementGroupStringPropertiesTest` (7),
`SGFigureElementStringLabelPropertiesTest` (7),
`SGDrawingElementAxisBreakPropertiesTest` (6),
`SGFigureElementAxisBreakPropertiesTest` (5),
`SGColorBarAxisColorBarPropertiesTest` (8),
`SGFigureElementSignificantDifferencePropertiesTest` (9) and
`SGFigureElementTimingLinePropertiesTest` (6). Notable per-class rises:
`SGSimpleSymbol2D` and `SGNamedStringBlock` to 100%,
`SGDrawingElementRectangle` from 12.9% to 72.1%, `SGDrawingElementBar`
from 1.5% to 52.4%, `SGBufferedFileWriter` from 0% to 83.7%,
`SGFigureElementGrid.GridProperties` from 2.9% to 96.1%,
`SGDrawingElementScale.ScaleProperties` to 100%,
`SGAxisElement.AxisProperties` from 1.0% to 84.2%, the
`SGDataValueHistory` dimension entries (`NetCDF.D2`, `MDArray.D2` and
`MDArray.MD1` from 0% to 97% or above, `NetCDF.MD1` from 22.3% to
98.9%), `SGColorMap.ColorMapProperties` from 12.7% to 100%, the
`SGColorMapManager` repeated/multiple map properties from 0% to 96% or
above, the `SGDataValueHistory` `NetCDF.D1` and `MDArray.D1` entries
from 31% to 100%, the figure string property classes
(`SGDrawingElementString.StringProperties` from 37.1% to 98.3%,
`SGElementGroupString.StringProperties` from 32.8% to 100%,
`SGFigureElementString.LabelProperties` from 34.2% to 100%), the
axis break symbol properties from 0% to 99.1% and 98.1%,
`SGFigureElementTimingLine.TimingLineProperties` and
`SGFigureElementSignificantDifference.SigDiffPropertiesWithAxes`
from 0% to 100%, the significant difference base properties from 0%
to 100% and `ColorBarProperties` from 0% to 91.8%.

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
2. **Consolidate the `data` triple hierarchy (2.1)**: pull common logic
   into intermediate base classes, following the documented analysis
   (76 name-compatible methods, extraction order listed in 2.1)
3. **Extract file operation logic from `SGDrawingWindow` (2.2)**: the
   `SGMainFunctions` side (`openFile`, `reloadData`, embedded content)
   is already extracted into headless-testable units
   (`SGFileOpenCategorizer`/`SGFileOpenHandler`/`SGDataReloader`); the
   remaining work is the `SGDrawingWindow` side
4. **Split the `figure` drawing model (2.3)**: the dialog side is
   already in `figure.dialog`; a further split of the drawing model
   side remains possible
