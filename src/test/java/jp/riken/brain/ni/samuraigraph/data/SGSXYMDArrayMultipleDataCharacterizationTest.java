package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5FactoryProvider;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Characterization tests for the less-exercised paths of {@link SGSXYMDArrayMultipleData}: column
 * type resolution, data viewer cells, info maps, property I/O, bind helpers and the
 * picked-dimension value path.
 */
class SGSXYMDArrayMultipleDataCharacterizationTest {

  private static Path createTempHdf5File() throws IOException {
    Path path = Files.createTempFile("samurai-graph-test", ".h5");
    Files.delete(path);
    path.toFile().deleteOnExit();
    return path;
  }

  // Builds a 1D file with x, y1, y2, le and ue of length 3.
  private static SGHDF5File create1DFile(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      writer.writeDoubleArray("x", new double[] {1.0, 2.0, 3.0});
      writer.writeDoubleArray("y1", new double[] {10.0, 20.0, 30.0});
      writer.writeDoubleArray("y2", new double[] {40.0, 50.0, 60.0});
      writer.writeDoubleArray("le", new double[] {1.0, 1.0, 1.0});
      writer.writeDoubleArray("ue", new double[] {2.0, 2.0, 2.0});
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  // Builds a 2D file with x(5,3) and y(5,3).
  private static SGHDF5File create2DFile(Path path) throws IOException {
    try (IHDF5Writer writer = HDF5FactoryProvider.get().open(path.toFile())) {
      double[][] xm = new double[5][3];
      double[][] ym = new double[5][3];
      for (int i = 0; i < 5; i++) {
        for (int j = 0; j < 3; j++) {
          xm[i][j] = i * 3 + j;
          ym[i][j] = 100 + i * 3 + j;
        }
      }
      writer.writeDoubleMatrix("x", xm);
      writer.writeDoubleMatrix("y", ym);
    }
    IHDF5Reader reader = HDF5FactoryProvider.get().openForReading(path.toFile());
    return new SGHDF5File(reader);
  }

  private static SGMDArrayDataColumnInfo info(SGMDArrayFile file, String name, String type) {
    SGMDArrayDataColumnInfo col =
        SGDataFileUtility.createDataColumnInfo(file.findVariable(name), type);
    col.setGenericDimensionIndex(0);
    return col;
  }

  private static SGSXYMDArrayMultipleData create1D(SGMDArrayFile file) {
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        new SGMDArrayDataColumnInfo[] {info(file, "x", SGDataColumnTypeConstants.X_VALUE)},
        new SGMDArrayDataColumnInfo[] {
          info(file, "y1", SGDataColumnTypeConstants.Y_VALUE),
          info(file, "y2", SGDataColumnTypeConstants.Y_VALUE)
        },
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true);
  }

  private static SGSXYMDArrayMultipleData createPicked(SGMDArrayFile file) {
    SGMDArrayDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE);
    xInfo.setGenericDimensionIndex(0);
    xInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    SGMDArrayDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("y"), SGDataColumnTypeConstants.Y_VALUE);
    yInfo.setGenericDimensionIndex(0);
    yInfo.setDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 1);
    return new SGSXYMDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        xInfo,
        yInfo,
        null,
        null,
        null,
        new SGIntegerSeriesSet(0, 2, 1),
        new SGIntegerSeriesSet(0, 4, 1),
        null,
        true);
  }

  // Maps each variable name to its resolved column type. The column type array is aligned to the
  // data's variable order (the file iteration order), so the mapping is built from the actual
  // variables rather than a hard-coded assumption.
  private static Map<String, String> typeByName(
      final SGSXYMDArrayMultipleData data, final String[] types) {
    Map<String, String> map = new HashMap<>();
    SGMDArrayVariable[] vars = data.getVariables();
    for (int ii = 0; ii < vars.length; ii++) {
      map.put(vars[ii].getName(), types[ii]);
    }
    return map;
  }

  // Builds a column-type array aligned to the data's variable order, assigning a type per
  // variable name and defaulting the rest to the empty string.
  private static String[] columnsFor(
      final SGSXYMDArrayMultipleData data, final Map<String, String> byName) {
    SGMDArrayVariable[] vars = data.getVariables();
    String[] columns = new String[vars.length];
    for (int ii = 0; ii < vars.length; ii++) {
      String name = vars[ii].getName();
      columns[ii] = byName.containsKey(name) ? byName.get(name) : "";
    }
    return columns;
  }

  // -----------------------------------------------------------------
  // Non-picked column type resolution
  // -----------------------------------------------------------------

  @Test
  void currentColumnTypeForBasicData() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    String[] types = data.getCurrentColumnType();
    assertNotNull(types);
    // Map each variable to its resolved column type (order independent).
    Map<String, String> typeByName = typeByName(data, types);
    assertEquals(SGDataColumnTypeConstants.X_VALUE, typeByName.get("x"));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, typeByName.get("y1"));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, typeByName.get("y2"));
    assertEquals("", typeByName.get("le"));
    assertEquals("", typeByName.get("ue"));
    // The multiple variables are resolved in the assigned order.
    List<String> names = new ArrayList<>();
    for (SGMDArrayVariable v : data.getAssignedVariables()) {
      names.add(v.getName());
    }
    assertTrue(names.containsAll(Arrays.asList("x", "y1", "y2")));
  }

  @Test
  void setColumnTypeNotPickedReassignsY() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    // Swap the roles: make x into a Y value and y1 into the X value. The columns align with
    // the data's variable order, so they are built from the actual variables.
    Map<String, String> byName = new HashMap<>();
    byName.put("x", SGDataColumnTypeConstants.Y_VALUE);
    byName.put("y1", SGDataColumnTypeConstants.X_VALUE);
    byName.put("y2", SGDataColumnTypeConstants.Y_VALUE);
    boolean ok = data.setColumnType(columnsFor(data, byName));
    assertTrue(ok);
    assertEquals("y1", data.getXVariable().getName());
    Map<String, String> types = typeByName(data, data.getCurrentColumnType());
    assertEquals(SGDataColumnTypeConstants.X_VALUE, types.get("y1"));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, types.get("x"));
    assertEquals(SGDataColumnTypeConstants.Y_VALUE, types.get("y2"));
  }

  @Test
  void setColumnTypeRejectsUnknownString() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    assertFalse(data.setColumnType(new String[] {"Bogus", "Y"}));
  }

  @Test
  void setColumnTypeErrorBarAndTickLabel() throws IOException {
    SGHDF5File file = create1DFile(createTempHdf5File());
    SGSXYMDArrayMultipleData data =
        new SGSXYMDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            new SGMDArrayDataColumnInfo[] {info(file, "x", SGDataColumnTypeConstants.X_VALUE)},
            new SGMDArrayDataColumnInfo[] {
              info(file, "y1", SGDataColumnTypeConstants.Y_VALUE),
              info(file, "y2", SGDataColumnTypeConstants.Y_VALUE)
            },
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            true);
    // Assign the error bar columns that reference the y1 holder. The columns align with the
    // data's variable order, so they are built from the actual variables.
    String leTitle =
        SGDataColumnTitleUtility.appendColumnTitle(
            SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y1");
    String ueTitle =
        SGDataColumnTitleUtility.appendColumnTitle(
            SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "y1");
    Map<String, String> byName = new HashMap<>();
    byName.put("x", SGDataColumnTypeConstants.X_VALUE);
    byName.put("y1", SGDataColumnTypeConstants.Y_VALUE);
    byName.put("y2", SGDataColumnTypeConstants.Y_VALUE);
    byName.put("le", leTitle);
    byName.put("ue", ueTitle);
    boolean ok = data.setColumnType(columnsFor(data, byName));
    assertTrue(ok);
    assertTrue(data.isErrorBarAvailable());
    assertTrue(data.getLowerErrorVariable().getName().equals("le"));
    assertTrue(data.getUpperErrorVariable().getName().equals("ue"));
  }

  @Test
  void setColumnTypeRejectsUnmatchedHolder() throws IOException {
    SGHDF5File file = create1DFile(createTempHdf5File());
    SGSXYMDArrayMultipleData data = create1D(file);
    // le/ue referencing different holders (y1 and y2) must be rejected. The columns align
    // with the data's variable order, so they are built from the actual variables.
    String leTitle =
        SGDataColumnTitleUtility.appendColumnTitle(
            SGDataColumnTypeConstants.LOWER_ERROR_VALUE, "y1");
    String ueTitle =
        SGDataColumnTitleUtility.appendColumnTitle(
            SGDataColumnTypeConstants.UPPER_ERROR_VALUE, "y2");
    Map<String, String> byName = new HashMap<>();
    byName.put("x", SGDataColumnTypeConstants.X_VALUE);
    byName.put("y1", SGDataColumnTypeConstants.Y_VALUE);
    byName.put("y2", SGDataColumnTypeConstants.Y_VALUE);
    byName.put("le", leTitle);
    byName.put("ue", ueTitle);
    assertFalse(data.setColumnType(columnsFor(data, byName)));
  }

  // -----------------------------------------------------------------
  // Data viewer cells (non-picked, multiple Y)
  // -----------------------------------------------------------------

  @Test
  void dataViewerCellCollapsesXToFirstChild() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    // For a shared X value with multiple Y, a non-X cell is remapped to child 0.
    SGTwoDimensionalArrayIndex cell =
        data.getDataViewerCell(
            new SGTwoDimensionalArrayIndex(1, 2), SGDataColumnTypeConstants.X_VALUE, false);
    assertNotNull(cell);
    assertEquals(0, cell.getColumn());
    assertEquals(2, cell.getRow());
  }

  @Test
  void dataViewerCellKeepsYChild() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    SGTwoDimensionalArrayIndex cell =
        data.getDataViewerCell(
            new SGTwoDimensionalArrayIndex(1, 2), SGDataColumnTypeConstants.Y_VALUE, false);
    assertNotNull(cell);
    assertEquals(1, cell.getColumn());
    assertEquals(2, cell.getRow());
  }

  // -----------------------------------------------------------------
  // Info map and bind helpers
  // -----------------------------------------------------------------

  @Test
  void infoMapMarksMultipleVariable() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    Map<String, Object> map = data.getInfoMap();
    assertEquals(Boolean.TRUE, map.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE));
    assertEquals(Boolean.TRUE, map.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE));
    assertFalse(map.containsKey(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES));
  }

  @Test
  void bindVariableNamesFormatsBracketAndIndex() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    assertEquals("{x}", data.bindVariableNameInBracket("x"));
    SGMDArrayVariable xVar = data.getXVariable();
    assertEquals("x:0", data.bindVariableNames(xVar, true));
    assertEquals("x", data.bindVariableNames(xVar, false));
    List<String> xNames = new ArrayList<>();
    List<String> yNames = new ArrayList<>();
    List<String> leNames = new ArrayList<>();
    List<String> ueNames = new ArrayList<>();
    List<String> tlNames = new ArrayList<>();
    data.getVariableNames(xNames, yNames, leNames, ueNames, tlNames);
    assertEquals(Arrays.asList("x"), xNames);
    assertEquals(2, yNames.size());
  }

  @Test
  void usedDimensionIndexMapAndStrideAccessors() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    Map<String, Map<String, Integer>> dimMap = data.getUsedDimensionIndexMap();
    assertNotNull(dimMap);
    assertTrue(dimMap.containsKey("x"));
    // A null stride request is materialized into a full (0..len-1) stride.
    assertNotNull(data.getStride());
    assertTrue(data.useCache(false));
    assertFalse(data.isDimensionPicked());
    assertEquals(2, data.getMultipleVariables().length);
    assertEquals("x", data.getSingleVariable().getName());
  }

  // -----------------------------------------------------------------
  // Property I/O (non-picked)
  // -----------------------------------------------------------------

  @Test
  void propertiesRoundTripNotPicked() throws IOException {
    SGHDF5File file = create1DFile(createTempHdf5File());
    SGSXYMDArrayMultipleData data = create1D(file);
    SGProperties p = data.getProperties();
    assertNotNull(p);
    SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties sp =
        (SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties) p;
    assertNotNull(sp.xNames);
    assertNotNull(sp.yNames);
    assertNull(sp.mPickUpInfo);
    // Apply back to a fresh instance.
    SGSXYMDArrayMultipleData restored = create1D(file);
    assertTrue(restored.setProperties(p));
    assertEquals("x", restored.getXVariable().getName());
  }

  @Test
  void getPropertiesRejectsWrongType() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    assertFalse(
        data.getProperties(
            new SGProperties() {
              @Override
              public boolean equals(Object obj) {
                return this == obj;
              }
            }));
    assertFalse(
        data.setProperties(
            new SGProperties() {
              @Override
              public boolean equals(Object obj) {
                return this == obj;
              }
            }));
  }

  // -----------------------------------------------------------------
  // Picked-dimension value and column type paths
  // -----------------------------------------------------------------

  @Test
  void pickedDataExposesValues() throws IOException {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    assertTrue(data.isDimensionPicked());
    assertEquals(3, data.getChildNumber());
    assertEquals(5, data.getAllPointsNumber());
    assertTrue(data.hasMultipleYValues());
    double[][] x = data.getXValueArray(false);
    assertEquals(3, x.length);
    assertEquals(5, x[0].length);
    // Child i holds x-values along the generic dimension at pickup index i:
    // x[i][j] = xm[j][i], so x[0] = [xm[0][0], xm[1][0], xm[2][0], xm[3][0], xm[4][0]].
    assertEquals(0.0, x[0][0], 0.0);
    assertEquals(9.0, x[0][3], 0.0);
    assertEquals(1.0, x[1][0], 0.0);
    String[] types = data.getCurrentColumnType();
    assertNotNull(types);
  }

  @Test
  void pickedInfoMapContainsIndicesAndDimMap() throws IOException {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    Map<String, Object> map = data.getInfoMap();
    assertEquals(Boolean.FALSE, map.get(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE));
    assertNotNull(map.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES));
    Map<String, Integer> dimMap =
        (Map<String, Integer>)
            map.get(SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);
    assertEquals(Integer.valueOf(1), dimMap.get("x"));
    assertEquals(Integer.valueOf(1), dimMap.get("y"));
  }

  @Test
  void clearPickUpResetsDimension() throws IOException {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    assertTrue(data.isDimensionPicked());
    assertTrue(data.clearPickUp());
    assertFalse(data.isDimensionPicked());
    // Clearing a second time is a no-op success.
    assertTrue(data.clearPickUp());
  }

  @Test
  void pickedPropertiesCarryPickUpInfo() throws IOException {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    SGProperties p = data.getProperties();
    assertNotNull(p);
    SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties sp =
        (SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties) p;
    assertNotNull(sp.mPickUpInfo);
    assertNotNull(sp.xNames);
    assertEquals(1, sp.xNames.length);
  }

  @Test
  void splitEnabledFollowsPickedCount() throws IOException {
    SGSXYMDArrayMultipleData picked = createPicked(create2DFile(createTempHdf5File()));
    assertTrue(picked.isSplitEnabled());
    SGSXYMDArrayMultipleData plain = create1D(create1DFile(createTempHdf5File()));
    assertTrue(plain.isSplitEnabled());
  }

  // -----------------------------------------------------------------
  // writeProperty via exporter
  // -----------------------------------------------------------------

  @Test
  void writePropertyNotPickedWritesBracketNames() throws Exception {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("data");
    SGExportParameter param = new SGExportParameter(SGConstants.OPERATION.SAVE_TO_PROPERTY_FILE);
    assertTrue(data.writeProperty(el, param));
    String xv = el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES);
    assertTrue(xv.startsWith("{") && xv.contains("x:0"));
    String yv = el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES);
    assertTrue(yv.contains("y1") && yv.contains("y2"));
  }

  @Test
  void writePropertyPickedWritesSingleNames() throws Exception {
    SGSXYMDArrayMultipleData data = createPicked(create2DFile(createTempHdf5File()));
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("data");
    SGExportParameter param = new SGExportParameter(SGConstants.OPERATION.SAVE_TO_PROPERTY_FILE);
    assertTrue(data.writeProperty(el, param));
    String xv = el.getAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAME);
    assertEquals("x:0", xv);
    String yv = el.getAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME);
    assertEquals("y:0", yv);
    String pickDim = el.getAttribute(SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION);
    assertTrue(pickDim.contains("x:1"));
    assertTrue(pickDim.contains("y:1"));
  }

  // -----------------------------------------------------------------
  // Clone / dispose / copy behavior
  // -----------------------------------------------------------------

  @Test
  void cloneProducesIndependentCopy() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    data.setDecimalPlaces(3);
    data.setExponent(2);
    SGSXYMDArrayMultipleData copy = (SGSXYMDArrayMultipleData) data.clone();
    assertEquals(3, copy.getDecimalPlaces());
    assertEquals(2, copy.getExponent());
    assertEquals("x", copy.getXVariable().getName());
    // Mutating the copy does not affect the source.
    copy.setDecimalPlaces(0);
    assertEquals(3, data.getDecimalPlaces());
  }

  @Test
  void setDataCopiesVariablesAndState() throws IOException {
    SGSXYMDArrayMultipleData src = create1D(create1DFile(createTempHdf5File()));
    SGSXYMDArrayMultipleData dst = create1D(src.getMDArrayFile());
    assertTrue(dst.setData(src));
    // The x variable and its role are copied across.
    assertEquals("x", dst.getXVariable().getName());
    // The number format (decimal places / exponent) is intentionally not copied
    // by the multiple-data setData, unlike the single MDArray data.
    assertEquals(0, dst.getDecimalPlaces());
    // A non-multiple data object is rejected.
    assertThrows(IllegalArgumentException.class, () -> dst.setData(new SGSXYSDArrayMultipleData()));
  }

  @Test
  void setColumnTypeWithPickUpInfoNullAppliesNotPicked() throws IOException {
    SGSXYMDArrayMultipleData data = create1D(create1DFile(createTempHdf5File()));
    Map<String, String> byName = new HashMap<>();
    byName.put("x", SGDataColumnTypeConstants.X_VALUE);
    byName.put("y1", SGDataColumnTypeConstants.Y_VALUE);
    byName.put("y2", SGDataColumnTypeConstants.Y_VALUE);
    assertTrue(data.setColumnTypeWithPickUp(columnsFor(data, byName), null));
    assertFalse(data.isDimensionPicked());
  }
}
