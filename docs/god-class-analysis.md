# God Class Analysis

> Generated for TASK-009. This document analyzes the 7 largest classes in the codebase, groups their methods by responsibility, and proposes a splitting strategy.

---

## Summary

| # | Class | Lines | Methods | Package | Primary Responsibility |
|---|-------|-------|---------|---------|----------------------|
| 1 | SGDataUtility | 8,243 | ~283 | data | Static utility methods for data parsing, formatting, and manipulation |
| 2 | SGFigureElementLegend | 7,966 | ~731 | figure/java2d | Legend rendering, layout, and property management |
| 3 | SGDrawingWindow | 7,150 | ~418 | base | Main application window, menu/toolbar/event handling |
| 4 | SGMainFunctions | 6,818 | ~165 | application | Application-level operations: load, save, export, wizard |
| 5 | SGAxisElement | 5,763 | ~319 | figure/java2d | Axis rendering, tick computation, label formatting |
| 6 | SGPropertyDialogSXYData | 5,047 | ~352 | figure/java2d | Property dialog for SXY data configuration |
| 7 | SGFigureElementShape | 4,991 | ~423 | figure/java2d | Shape figure element rendering and properties |

---

## 1. SGDataUtility (8,243 lines, ~283 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| CSV/Data Parsing | ~40 | `parseCSV`, `parseData`, `parseHeader`, delimiter detection | `SGCSVParser` |
| Value Formatting | ~25 | `formatValue`, `getPreferredFormat`, number/string formatting | `SGValueFormatter` |
| Data Type Classification | ~15 | `isSDArrayData`, `isNetCDFData`, `isHDF5Data`, `isMATLABData`, `isMDArrayData`, `isVirtualMDArrayData`, `isSXYTypeData`, `isVXYTypeData`, `isSXYZTypeData`, `isMultipleData`, `isNetCDFDimensionData` | `SGDataTypeClassifier` |
| Data Bounds/Statistics | ~20 | `getBounds`, `getMaxValue`, `getMinValue`, `getAllAnimationFrameBoundsX/Y` | `SGDataStatistics` |
| Column Type/Annotation | ~20 | `appendColumnNo`, `appendColumnTitle`, `getAppendedColumnIndex`, `removeHeaderNo`, `removeHeaderTitle` | `SGColumnAnnotation` |
| Data Value Extraction | ~30 | `getDataValue`, `getXValueArray`, `getYValueArray`, `getValueTable`, `getDataBuffer` | `SGDataExtractor` |
| NetCDF-Specific | ~25 | `calcNetCDFDefaultStride`, NetCDF variable/dimension handling | `SGNetCDFDataUtility` |
| MDArray/HDF5-Specific | ~20 | `calcMDArrayDefaultStride`, MDArray dimension handling | `SGMDArrayDataUtility` |
| Property Serialization | ~15 | Property file read/write, serialization helpers | `SGPropertySerializer` |
| Animation | ~10 | Animation frame bounds, keyframe handling | `SGAnimationUtility` |
| Validation | ~15 | `checkDataColumns`, `checkColumnTypes`, data integrity checks | `SGDataValidator` |
| Miscellaneous | ~48 | Various small utility methods | Keep in SGDataUtility or distribute |

### Dependencies
- Heavily depends on: `SGDataColumnInfo`, `SGDataBuffer`, `SGDataTypeConstants`, `SGIDataColumnTypeConstants`
- Referenced by: ~200+ files (widely used static utility)

### Split Strategy
1. **Phase 1:** Extract independent groups (CSV Parsing, Value Formatting, Data Type Classification) into new utility classes
2. **Phase 2:** Extract data-source-specific groups (NetCDF, MDArray/HDF5)
3. **Phase 3:** Extract remaining groups (Bounds/Statistics, Column Annotation, Animation)
4. Leave delegation methods in SGDataUtility with `@Deprecated` annotations
5. Target: Reduce SGDataUtility to <3,000 lines

### Impact Assessment
- **High risk:** Every change must maintain backward compatibility since this is a widely-used static utility
- **Mitigation:** Keep delegation methods; update callers incrementally

---

## 2. SGFigureElementLegend (7,966 lines, ~731 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| Legend Layout | ~80 | Position calculation, size computation, arrangement algorithms | `SGLegendLayout` |
| Legend Rendering | ~60 | Drawing methods, paint overrides, visual composition | `SGLegendRenderer` |
| Legend Item Management | ~100 | Add/remove/update legend items, item state management | `SGLegendItemManager` |
| Property Handling | ~150 | Get/set properties, property change listeners, serialization | `SGLegendPropertyHandler` |
| Data Binding | ~80 | Link legend items to data series, update on data change | `SGLegendDataBinding` |
| Style/Formatting | ~100 | Font, color, border, background styling | `SGLegendStyler` |
| Event Handling | ~50 | Mouse events, selection, drag-and-drop | `SGLegendEventHandler` |
| Dialog/Wizard Integration | ~60 | Property dialog support, wizard panel creation | `SGLegendDialogSupport` |
| Serialization | ~50 | Save/load legend state to/from property files | `SGLegendSerializer` |

### Dependencies
- Depends on: `SGProperty`, `SGFigureElement`, `SGData` classes
- Referenced by: `SGFigure`, `SGDrawingWindow`, property dialogs

### Split Strategy
1. Extract `SGLegendLayout` and `SGLegendRenderer` as inner classes or separate classes
2. Extract property handling into `SGLegendPropertyHandler`
3. Extract item management into `SGLegendItemManager`
4. Target: Reduce to <3,000 lines

### Impact Assessment
- **Medium risk:** Primarily GUI-related, changes are more contained
- **Mitigation:** Inner classes can be extracted first with minimal API changes

---

## 3. SGDrawingWindow (7,150 lines, ~418 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| Menu Handling | ~80 | Menu creation, menu item actions, keyboard shortcuts | `SGMenuHandler` |
| Toolbar Handling | ~40 | Toolbar creation, toolbar button actions | `SGToolbarHandler` |
| Component Layout | ~50 | Panel arrangement, resize handling, split pane management | `SGWindowLayout` |
| Property Change | ~60 | Property listener callbacks, preference updates | `SGPropertyHandler` |
| File Operations | ~30 | Open, save, export from window context | Delegate to SGMainFunctions |
| Drawing Canvas | ~40 | Canvas management, repaint, zoom, pan | `SGCanvasManager` |
| Status/Info Display | ~20 | Status bar, info panel updates | `SGStatusManager` |
| Dialog Management | ~30 | Open various dialogs, manage modal state | `SGDialogManager` |
| Serialization | ~20 | Window state save/load | `SGWindowStateSerializer` |
| Initialization | ~20 | Window setup, component initialization | Keep in SGDrawingWindow |
| Miscellaneous | ~68 | Various window-specific utilities | Keep or distribute |

### Dependencies
- Depends on: `SGMainFunctions`, `SGFigure`, `SGProperty`
- Referenced by: Application startup, `SGMainFunctions`

### Split Strategy
1. Extract menu and toolbar handlers into separate inner classes
2. Extract property change handler
3. Extract layout management
4. Target: Reduce to <4,000 lines

### Impact Assessment
- **Medium risk:** GUI class with many event listeners
- **Mitigation:** Inner-class extraction is safe; delegation pattern preserves behavior

---

## 4. SGMainFunctions (6,818 lines, ~165 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| File Load | ~20 | Load data files, property files, figure files | `SGFileLoader` |
| File Save/Export | ~25 | Save/export in various formats (PNG, PDF, SVG, etc.) | `SGFileExporter` |
| Wizard Dialogs | ~30 | Data import wizard, figure setup wizard | `SGWizardManager` |
| Clipboard | ~10 | Copy/paste operations | `SGClipboardManager` |
| Application Init | ~15 | Initializer, library setup, temp file management | `SGApplicationInitializer` |
| Data Creation | ~15 | Create new data/figure objects | `SGDataCreator` (exists) |
| Undo/Redo | ~10 | Command pattern for undo/redo | `SGUndoManager` |
| Print | ~5 | Print figure to printer | `SGPrintManager` |
| Preferences | ~10 | Application preferences handling | `SGPreferencesManager` |
| Miscellaneous | ~25 | Various application utilities | Keep or distribute |

### Dependencies
- Depends on: Nearly all packages (central application controller)
- Referenced by: `SGDrawingWindow`, menu handlers, toolbar handlers

### Split Strategy
1. Extract file operations into `SGFileLoader` and `SGFileExporter`
2. Extract wizard management into `SGWizardManager`
3. Extract clipboard into `SGClipboardManager`
4. Extract initialization into `SGApplicationInitializer`
5. Target: Reduce to <4,000 lines

### Impact Assessment
- **High risk:** Central application controller with wide dependencies
- **Mitigation:** Extract managers that SGMainFunctions delegates to; minimize API changes

---

## 5. SGAxisElement (5,763 lines, ~319 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| Tick Computation | ~40 | Tick interval calculation, major/minor tick generation | `SGTickCalculator` |
| Label Formatting | ~30 | Tick label text formatting, date/number formatting | `SGTickLabelFormatter` |
| Axis Rendering | ~50 | Drawing axis lines, ticks, labels, arrows | `SGAxisRenderer` |
| Scale Computation | ~20 | Linear/log scale, axis range calculation | `SGAxisScaler` |
| Property Handling | ~60 | Axis property get/set, serialization | `SGAxisPropertyHandler` |
| Grid Lines | ~20 | Grid line computation and rendering | `SGGridLineRenderer` |
| Title/Label | ~20 | Axis title positioning and rendering | `SGAxisTitleRenderer` |
| Event Handling | ~15 | Mouse interaction with axis | `SGAxisEventHandler` |
| Miscellaneous | ~64 | Various axis utilities | Keep or distribute |

### Dependencies
- Depends on: `SGProperty`, `SGValueRange`, axis constants
- Referenced by: `SGFigureElementAxis`, graph rendering

### Split Strategy
1. Extract `SGTickCalculator` and `SGTickLabelFormatter`
2. Extract rendering into `SGAxisRenderer`
3. Extract property handling
4. Target: Reduce to <3,000 lines

### Impact Assessment
- **Low-Medium risk:** Well-defined domain (axis rendering)
- **Mitigation:** Computation logic can be extracted safely as independent utilities

---

## 6. SGPropertyDialogSXYData (5,047 lines, ~352 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| Dialog Layout | ~40 | Panel construction, component arrangement | `SGSXYDialogLayout` |
| Data Column Mapping | ~60 | Column type selection, X/Y assignment UI | `SGSXYColumnMapper` |
| Error Bar Configuration | ~30 | Error bar property UI | `SGSErrorBarConfig` |
| Tick Label Configuration | ~20 | Tick label property UI | `SGSTickLabelConfig` |
| Property Binding | ~50 | Bind UI components to data properties | `SGSXYPropertyBinder` |
| Validation | ~30 | Validate user input, show errors | `SGSXYInputValidator` |
| Preview/Preview Update | ~20 | Data preview rendering | `SGSXYPreviewRenderer` |
| Event Handling | ~40 | Button clicks, selection changes | `SGSXYDialogHandler` |
| Serialization | ~20 | Save/load dialog state | `SGSXYDialogSerializer` |
| Miscellaneous | ~42 | Various dialog utilities | Keep or distribute |

### Dependencies
- Depends on: `SGData`, `SGProperty`, dialog framework
- Referenced by: `SGMainFunctions`, property menu

### Split Strategy
1. Extract column mapping logic into `SGSXYColumnMapper`
2. Extract property binding into `SGSXYPropertyBinder`
3. Extract validation into `SGSXYInputValidator`
4. Target: Reduce to <2,500 lines

### Impact Assessment
- **Low risk:** Dialog class with contained scope
- **Mitigation:** Inner-class extraction is safe

---

## 7. SGFigureElementShape (4,991 lines, ~423 methods)

### Responsibility Groups

| Group | Approx. Methods | Description | Target Class |
|-------|----------------|-------------|-------------|
| Shape Rendering | ~60 | Drawing shapes, fill, stroke, transforms | `SGShapeRenderer` |
| Shape Properties | ~80 | Shape type, size, color, border properties | `SGShapePropertyHandler` |
| Position/Layout | ~40 | Shape positioning, alignment, distribution | `SGShapeLayout` |
| Data Binding | ~30 | Bind shape positions/sizes to data values | `SGShapeDataBinding` |
| Event Handling | ~30 | Mouse interaction, selection | `SGShapeEventHandler` |
| Serialization | ~30 | Save/load shape state | `SGShapeSerializer` |
| Animation | ~20 | Shape animation properties | `SGShapeAnimation` |
| Style/Formatting | ~40 | Gradient, pattern, transparency | `SGShapeStyler` |
| Miscellaneous | ~93 | Various shape utilities | Keep or distribute |

### Dependencies
- Depends on: `SGProperty`, `SGFigureElement`, `SGData`
- Referenced by: `SGFigure`, `SGDrawingWindow`

### Split Strategy
1. Extract rendering into `SGShapeRenderer`
2. Extract property handling into `SGShapePropertyHandler`
3. Extract data binding
4. Target: Reduce to <2,500 lines

### Impact Assessment
- **Low-Medium risk:** Well-contained figure element
- **Mitigation:** Extract rendering and properties as inner classes first

---

## Proposed Execution Order

| Order | Task | Class | Estimated Effort | Risk |
|-------|------|-------|-----------------|------|
| 1 | TASK-014 | SGDataUtility (Part 1) | 3-5 days | High |
| 2 | TASK-015 | SGDataUtility (Part 2) | 3-5 days | High |
| 3 | TASK-016 | SGMainFunctions | 5-7 days | High |
| 4 | TASK-018 | SGDrawingWindow | 3-5 days | Medium |
| 5 | SGAxisElement | - | 2-3 days | Low-Medium |
| 6 | SGFigureElementLegend | - | 3-5 days | Medium |
| 7 | SGPropertyDialogSXYData | - | 2-3 days | Low |
| 8 | SGFigureElementShape | - | 2-3 days | Low-Medium |

## General Refactoring Principles

1. **Delegation Pattern:** Extract new classes, keep delegation methods in original class with `@Deprecated`
2. **Incremental Migration:** Update callers gradually, not all at once
3. **Test-First:** Write tests for extracted methods before moving them
4. **Inner-Class First:** For GUI classes, start by extracting inner classes
5. **No API Breakage:** Public API surface should remain unchanged during refactoring
