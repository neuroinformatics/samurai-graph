package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGNamedStringBlock;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import ucar.ma2.Array;
import ucar.ma2.ArrayByte;
import ucar.ma2.ArrayChar;
import ucar.ma2.ArrayDouble;
import ucar.ma2.ArrayFloat;
import ucar.ma2.ArrayInt;
import ucar.ma2.ArrayLong;
import ucar.ma2.ArrayShort;
import ucar.ma2.DataType;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

/**
 * Characterization tests for the base-class behavior of {@link SGNetCDFData}: construction, state
 * copying, origin map, coordinate values, time and index strides, value arrangement, block lists,
 * column information, command strings and the export entry points.
 */
class SGNetCDFDataCharacterizationTest {

  private static final String EXAMPLE_15 = "examples/data/Example15.nc";

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  // -----------------------------------------------------------------
  // Test-only stubs
  // -----------------------------------------------------------------

  /** A concrete {@link SGNetCDFData} for exercising the base-class behavior. */
  static class StubData extends SGNetCDFData {

    private SGNetCDFVariable[] assignedVariables = new SGNetCDFVariable[0];

    private String[] columnTypes = new String[0];

    private Map<String, SGIntegerSeriesSet> dimensionStrideMap =
        new HashMap<String, SGIntegerSeriesSet>();

    private List<Dimension> tooltipDimList = new ArrayList<Dimension>();

    private boolean exportResult = true;

    int exportCallCount;

    int setEditedValuesCallCount;

    StubData() {
      super();
    }

    StubData(SGNetCDFFile file, SGDataSourceObserver obs) {
      this(file, obs, null, null, null, false);
    }

    StubData(
        SGNetCDFFile file,
        SGDataSourceObserver obs,
        SGNetCDFDataColumnInfo timeInfo,
        SGNetCDFDataColumnInfo indexInfo,
        SGIntegerSeriesSet indexStride,
        boolean strideAvailable) {
      super(file, obs, timeInfo, indexInfo, indexStride, strideAvailable);
    }

    void setAssignedVariables(SGNetCDFVariable[] vars) {
      this.assignedVariables = vars;
    }

    void setExportResult(boolean result) {
      this.exportResult = result;
    }

    void setToolTipDimensions(List<Dimension> dims) {
      this.tooltipDimList = dims;
    }

    @Override
    public void restoreCache() {
      // no cache to restore
    }

    @Override
    public void setDataValue(SGDataValueHistory value) {
      // no state to set
    }

    @Override
    public int getAllPointsNumber() {
      return 0;
    }

    @Override
    public SGDataBuffer getDataBuffer(SGDataBufferPolicy policy) {
      return null;
    }

    @Override
    public boolean hasEffectiveStride() {
      return false;
    }

    @Override
    public String[] getDataViewerColumnTypes() {
      return new String[0];
    }

    @Override
    public String getPreferredDataViewColumnType() {
      return null;
    }

    @Override
    public Double getDataViewerValue(String columnType, int row, int col) {
      return null;
    }

    @Override
    public void setDataViewerValue(String columnType, int row, int col, Object value) {
      // no state to set
    }

    @Override
    public int getDataViewerColumnNumber(String columnType, boolean all) {
      return 0;
    }

    @Override
    public int getDataViewerRowNumber(String columnType, boolean all) {
      return 0;
    }

    @Override
    public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
      return null;
    }

    @Override
    public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
      return null;
    }

    @Override
    public SGNetCDFVariable[] getAssignedVariables() {
      return this.assignedVariables;
    }

    @Override
    public String getDataType() {
      return "StubData";
    }

    @Override
    public SGProperties getProperties() {
      return new StubNetCDFDataProperties();
    }

    @Override
    public boolean setColumnType(String[] columns) {
      this.columnTypes = columns;
      return true;
    }

    @Override
    public String[] getCurrentColumnType() {
      return this.columnTypes;
    }

    @Override
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
      this.dimensionStrideMap = map;
    }

    @Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
      return this.dimensionStrideMap;
    }

    @Override
    protected Map<String, SGIntegerSeriesSet> getDimensionStrideMap() {
      return this.dimensionStrideMap;
    }

    @Override
    protected double[] arrangeValueArray(
        Array data,
        SGNetCDFVariable var,
        SGNetCDFVariable[] cVars,
        Map<String, SGIntegerSeries> dimensionStride) {
      return this.arrange1Dim(data);
    }

    @Override
    protected String[] arrangeStringArray(
        ArrayChar data,
        SGNetCDFVariable var,
        SGNetCDFVariable[] cVars,
        Map<String, SGIntegerSeries> dimensionStride) {
      return this.arrangeString1Dim(data, (int) data.getSize());
    }

    @Override
    protected double[] arrangeValueArray(
        SGNetCDFVariable var, List<SGNamedDoubleValueIndexBlock> blockList) {
      List<Double> values = new ArrayList<Double>();
      for (SGNamedDoubleValueIndexBlock block : blockList) {
        double[] array = block.getValues();
        for (int ii = 0; ii < array.length; ii++) {
          values.add(array[ii]);
        }
      }
      double[] ret = new double[values.size()];
      for (int ii = 0; ii < ret.length; ii++) {
        ret[ii] = values.get(ii);
      }
      return ret;
    }

    @Override
    protected String[] arrangeStringArray(List<SGNamedStringBlock> blockList) {
      List<String> values = new ArrayList<String>();
      for (SGNamedStringBlock block : blockList) {
        String[] array = block.getValues();
        for (int ii = 0; ii < array.length; ii++) {
          values.add(array[ii]);
        }
      }
      return values.toArray(new String[0]);
    }

    @Override
    protected Array setEditedValues(
        ucar.nc2.write.NetcdfFormatWriter writer, String varName, Array array, boolean all) {
      this.setEditedValuesCallCount++;
      return array;
    }

    @Override
    protected boolean exportToFile(
        ucar.nc2.write.NetcdfFormatWriter.Builder builder,
        SGExportParameter mode,
        SGDataBufferPolicy policy) {
      this.exportCallCount++;
      return this.exportResult;
    }

    @Override
    protected List<Dimension> getToolTipNotSpatiallyVariedDimensionList() {
      return this.tooltipDimList;
    }

    @Override
    protected boolean setArraySectionPropertySub(SGPropertyMap map) {
      return true;
    }

    @Override
    protected boolean matches(
        int col, int row, String columnType, SGDataValueHistory value, double d) {
      return false;
    }
  }

  /** A stub data source that provides no path and no attributes. */
  static class StubDataSource implements SGIDataSource {

    StubDataSource() {
      super();
    }

    @Override
    public String getPath() {
      return null;
    }

    @Override
    public List<SGAttribute> getAttributes() {
      return new ArrayList<SGAttribute>();
    }

    @Override
    public void dispose() {
      // no state to dispose
    }

    @Override
    public boolean isDisposed() {
      return false;
    }
  }

  /** A stub of {@link NetCDFDataProperties} with no additional behavior. */
  static class StubNetCDFDataProperties extends SGNetCDFData.NetCDFDataProperties {

    private static final long serialVersionUID = 1L;

    StubNetCDFDataProperties() {
      super();
    }
  }

  /** A concrete {@link SGArrayData} used as an invalid source for {@link SGNetCDFData#setData}. */
  static class StubArrayData extends SGArrayData {

    private String[] columnTypes = new String[0];

    private Map<String, SGIntegerSeriesSet> strideMap = new HashMap<String, SGIntegerSeriesSet>();

    StubArrayData() {
      super();
    }

    StubArrayData(SGIDataSource src, SGDataSourceObserver obs) {
      super(src, obs);
    }

    @Override
    public void restoreCache() {
      // no cache to restore
    }

    @Override
    public void setDataValue(SGDataValueHistory value) {
      // no state to set
    }

    @Override
    public int getAllPointsNumber() {
      return 0;
    }

    @Override
    public SGDataBuffer getDataBuffer(SGDataBufferPolicy policy) {
      return null;
    }

    @Override
    public boolean hasEffectiveStride() {
      return false;
    }

    @Override
    public String[] getDataViewerColumnTypes() {
      return new String[0];
    }

    @Override
    public String getPreferredDataViewColumnType() {
      return null;
    }

    @Override
    public Double getDataViewerValue(String columnType, int row, int col) {
      return null;
    }

    @Override
    public void setDataViewerValue(String columnType, int row, int col, Object value) {
      // no state to set
    }

    @Override
    public int getDataViewerColumnNumber(String columnType, boolean all) {
      return 0;
    }

    @Override
    public int getDataViewerRowNumber(String columnType, boolean all) {
      return 0;
    }

    @Override
    public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
      return null;
    }

    @Override
    public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
      return null;
    }

    @Override
    public String getDataType() {
      return "StubArrayData";
    }

    @Override
    public boolean isIndexAvailable() {
      return false;
    }

    @Override
    public boolean isAnimationSupported() {
      return false;
    }

    @Override
    public boolean isAnimationAvailable() {
      return false;
    }

    @Override
    public SGIntegerSeriesSet getIndexStride() {
      return null;
    }

    @Override
    public int getCurrentTimeValueIndex() {
      return -1;
    }

    @Override
    public void setCurrentTimeValueIndex(int index) {
      // no state to set
    }

    @Override
    public SGIntegerSeriesSet getTimeStride() {
      return null;
    }

    @Override
    public SGProperties getProperties() {
      return new SGProperties() {
        private static final long serialVersionUID = 1L;
      };
    }

    @Override
    public String getNetCDFDataSetDataType() {
      return "StubArrayData";
    }

    @Override
    public String getDataSetFileExtension() {
      return "stub";
    }

    @Override
    public boolean setColumnType(String[] columns) {
      this.columnTypes = columns;
      return true;
    }

    @Override
    public String[] getCurrentColumnType() {
      return this.columnTypes;
    }

    @Override
    public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
      this.strideMap = map;
    }

    @Override
    protected Map<String, SGIntegerSeriesSet> getStrideMap() {
      return this.strideMap;
    }

    @Override
    public SGDataColumnInfo[] getColumnInfo() {
      return new SGDataColumnInfo[0];
    }

    @Override
    public boolean saveToArchiveDataSetFile(File file, SGExportParameter mode) {
      return false;
    }

    @Override
    public boolean saveToSameFormatFile(
        File file, SGExportParameter mode, SGDataBufferPolicy policy) {
      return false;
    }

    @Override
    public boolean saveToTextFile(File file, SGExportParameter mode, SGDataBufferPolicy policy) {
      return false;
    }

    @Override
    public boolean saveToNetCDFFile(File file, SGExportParameter mode, SGDataBufferPolicy policy) {
      return false;
    }

    @Override
    public boolean saveToDataSetNetCDFFile(File file) {
      return false;
    }

    @Override
    public boolean saveToHDF5File(File file, SGExportParameter mode, SGDataBufferPolicy policy) {
      return false;
    }

    @Override
    public boolean saveToMATLABFile(File file, SGExportParameter mode, SGDataBufferPolicy policy) {
      return false;
    }

    @Override
    public String getColumnTypeCommandString() {
      return "";
    }

    @Override
    public String getOriginCommandString() {
      return "";
    }

    @Override
    protected boolean setArraySectionPropertySub(SGPropertyMap map) {
      return true;
    }

    @Override
    public int getAnimationLength() {
      return -1;
    }

    @Override
    public String getToolTipTextNotSpatiallyVaried() {
      return "";
    }

    @Override
    protected boolean matches(
        int col, int row, String columnType, SGDataValueHistory value, double d) {
      return false;
    }
  }

  // -----------------------------------------------------------------
  // Helpers
  // -----------------------------------------------------------------

  private static SGNetCDFFile openFile(String path) throws IOException {
    return new SGNetCDFFile(NetcdfFiles.open(path));
  }

  private static SGNetCDFDataColumnInfo columnInfo(SGNetCDFFile file, String name, String type) {
    return SGDataFileUtility.createDataColumnInfo(file.findVariable(name), type);
  }

  // Example16: x(8), y(12) coordinate variables; height(x, y).
  private static StubData data16() throws IOException {
    return new StubData(openFile(EXAMPLE_16), new SGDataSourceObserver());
  }

  private static StubData data16IndexY() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_16);
    return new StubData(
        file,
        new SGDataSourceObserver(),
        null,
        columnInfo(file, "y", SGDataColumnTypeConstants.INDEX),
        null,
        false);
  }

  private static StubData data16IndexYWithStride() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_16);
    return new StubData(
        file,
        new SGDataSourceObserver(),
        null,
        columnInfo(file, "y", SGDataColumnTypeConstants.INDEX),
        new SGIntegerSeriesSet(1, 3, 1),
        true);
  }

  // Example15: x(8), y(12), time(4); height(time, x, y).
  private static StubData data15Time() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_15);
    return new StubData(
        file,
        new SGDataSourceObserver(),
        columnInfo(file, "time", "Animation Frame"),
        null,
        null,
        false);
  }

  private static StubData data15TimeIndexX() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_15);
    return new StubData(
        file,
        new SGDataSourceObserver(),
        columnInfo(file, "time", "Animation Frame"),
        columnInfo(file, "x", SGDataColumnTypeConstants.INDEX),
        new SGIntegerSeriesSet(1, 3, 1),
        true);
  }

  private static Element element() throws Exception {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    return document.createElement("properties");
  }

  private static Path tempFile(String suffix) throws IOException {
    return Files.createTempFile("samurai-graph-test", suffix);
  }

  // returns a path that does not exist yet, for writers that cannot create
  // a file over an existing one
  private static Path newTempPath(String suffix) throws IOException {
    return Files.createTempDirectory("samurai-graph-test").resolve("out" + suffix);
  }

  private static double[] readAllValues(Variable var) throws IOException {
    Array array = var.read();
    int size = (int) array.getSize();
    double[] values = new double[size];
    for (int ii = 0; ii < size; ii++) {
      values[ii] = array.getDouble(ii);
    }
    return values;
  }

  // -----------------------------------------------------------------
  // Constructor
  // -----------------------------------------------------------------

  @Test
  void constructorSeedsOriginMapAndFileState() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    assertEquals(file, data.getDataSource());
    assertFalse(data.isStrideAvailable());
    assertEquals(0, data.getOrigin("x"));
    assertEquals(0, data.getOrigin("y"));
    assertFalse(data.isTimeVariableAvailable());
    assertFalse(data.isIndexAvailable());
    assertNull(data.getTimeVariable());
    assertNull(data.getIndexVariable());
    assertEquals(-1, data.getAnimationLength());
    assertEquals(-1, data.getCurrentTimeValueIndex());
    assertEquals(-1, data.getIndexDimensionLength());
  }

  @Test
  void constructorRejectsUnknownTimeVariable() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_16);
    SGNetCDFFile file15 = openFile(EXAMPLE_15);
    // the name of the column is the name of the variable, so a variable that
    // does not exist in the file is needed to exercise the failure path
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new StubData(
                    file,
                    new SGDataSourceObserver(),
                    columnInfo(file15, "time", SGDataColumnTypeConstants.VALUE_TYPE_NUMBER),
                    null,
                    null,
                    false));
    assertEquals("Illegal variable name: time", e.getMessage());
  }

  @Test
  void constructorRejectsNonCoordinateTimeVariable() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_16);
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new StubData(
                    file,
                    new SGDataSourceObserver(),
                    columnInfo(file, "height", "Animation Frame"),
                    null,
                    null,
                    false));
    assertEquals("Time variable must be a coordinate variable.", e.getMessage());
  }

  @Test
  void constructorRejectsUnknownIndexVariable() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_16);
    SGNetCDFFile file15 = openFile(EXAMPLE_15);
    // the name of the column is the name of the variable, so a variable that
    // does not exist in the file is needed to exercise the failure path
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new StubData(
                    file,
                    new SGDataSourceObserver(),
                    null,
                    columnInfo(file15, "time", SGDataColumnTypeConstants.INDEX),
                    null,
                    false));
    assertEquals("Variable of given name is not found: time", e.getMessage());
  }

  @Test
  void constructorRejectsNonCoordinateIndexVariable() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_16);
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new StubData(
                    file,
                    new SGDataSourceObserver(),
                    null,
                    columnInfo(file, "height", SGDataColumnTypeConstants.INDEX),
                    null,
                    false));
    assertEquals("Index variable must be a coordinate variable: height", e.getMessage());
  }

  @Test
  void constructorRejectsTimeAndIndexOnSameDimension() throws IOException {
    SGNetCDFFile file = openFile(EXAMPLE_15);
    IllegalArgumentException e =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new StubData(
                    file,
                    new SGDataSourceObserver(),
                    columnInfo(file, "time", "Animation Frame"),
                    columnInfo(file, "time", SGDataColumnTypeConstants.INDEX),
                    null,
                    false));
    assertEquals("Dimensions of time and index variables must not be the same.", e.getMessage());
  }

  @Test
  void constructorWithIndexVariableSetsStrideAndOrigin() throws IOException {
    StubData data = data16IndexY();
    assertTrue(data.isIndexAvailable());
    assertEquals("y", data.getIndexVariable().getName());
    assertNotNull(data.getIndexStride());
    assertEquals("0:end", data.getIndexStride().toString());
    assertEquals(12, data.getIndexDimensionLength());
  }

  @Test
  void constructorWithTimeVariableSetsTimeStride() throws IOException {
    StubData data = data15Time();
    assertTrue(data.isTimeVariableAvailable());
    assertTrue(data.isAnimationAvailable());
    assertTrue(data.isAnimationSupported());
    assertEquals(4, data.getAnimationLength());
    assertEquals("0:end", data.getTimeStride().toString());
    assertEquals("time", data.getTimeVariable().getName());
  }

  // -----------------------------------------------------------------
  // Clone, dispose and setData
  // -----------------------------------------------------------------

  @Test
  void cloneCopiesOriginMapIndependently() throws IOException {
    StubData data = data16();
    data.setOrigin("x", 1);
    StubData clone = (StubData) data.clone();
    assertEquals(1, clone.getOrigin("x"));
    clone.setOrigin("x", 2);
    assertEquals(1, data.getOrigin("x"));
    assertEquals(0, clone.getOrigin("y"));
  }

  @Test
  void cloneCopiesStridesIndependently() throws IOException {
    StubData data = data15TimeIndexX();
    StubData clone = (StubData) data.clone();
    assertNotNull(clone.getIndexStride());
    assertNotSame(data.getIndexStride(), clone.getIndexStride());
    assertEquals("1:end", clone.getIndexStride().toString());
    assertNotNull(clone.getTimeStride());
    assertEquals("0:end", clone.getTimeStride().toString());
    // the variables are shared references
    assertSame(data.getTimeVariable(), clone.getTimeVariable());
    assertSame(data.getIndexVariable(), clone.getIndexVariable());
  }

  @Test
  void disposeClearsState() throws IOException {
    StubData data = data16IndexY();
    data.dispose();
    assertTrue(data.isDisposed());
    assertNull(data.getTimeVariable());
    assertNull(data.getIndexVariable());
    assertNull(data.getIndexStride());
  }

  @Test
  void setDataCopiesStateFromNetCDFData() throws IOException {
    SGNetCDFFile srcFile = openFile(EXAMPLE_15);
    StubData source =
        new StubData(
            srcFile,
            new SGDataSourceObserver(),
            columnInfo(srcFile, "time", "Animation Frame"),
            null,
            null,
            true);
    StubData target = new StubData(openFile(EXAMPLE_15), new SGDataSourceObserver());
    assertTrue(target.setData(source));
    assertTrue(target.isStrideAvailable());
    assertTrue(target.isTimeVariableAvailable());
    assertEquals("time", target.getTimeVariable().getName());
    assertNotSame(source.getTimeStride(), target.getTimeStride());
    assertEquals("0:end", target.getTimeStride().toString());
  }

  @Test
  void setDataCopiesIndexStrideAndOriginMap() throws IOException {
    SGNetCDFFile srcFile = openFile(EXAMPLE_15);
    StubData source =
        new StubData(
            srcFile,
            new SGDataSourceObserver(),
            null,
            columnInfo(srcFile, "x", SGDataColumnTypeConstants.INDEX),
            new SGIntegerSeriesSet(1, 3, 1),
            true);
    source.setOrigin("x", 2);
    StubData target = new StubData(openFile(EXAMPLE_15), new SGDataSourceObserver());
    assertTrue(target.setData(source));
    // the index stride is copied with the same content
    assertEquals(source.getIndexStride(), target.getIndexStride());
    assertEquals(2, target.getOrigin("x"));
    // the stride can be changed independently
    target.setIndexStride(new SGIntegerSeriesSet(2, 4, 1));
    assertEquals("1:end", source.getIndexStride().toString());
    assertEquals("2:end", target.getIndexStride().toString());
    // the origin map is a copy
    target.setOrigin("x", 3);
    assertEquals(2, source.getOrigin("x"));
  }

  @Test
  void setDataRejectsNonNetCDFData() throws IOException {
    StubData target = data16();
    StubArrayData source = new StubArrayData(new StubDataSource(), new SGDataSourceObserver());
    assertThrows(
        IllegalArgumentException.class,
        () -> target.setData(source),
        "Given data is not the instance of SGNetCDFData class.");
  }

  // -----------------------------------------------------------------
  // Properties
  // -----------------------------------------------------------------

  @Test
  void getPropertiesFillsNetCDFDataProperties() throws IOException {
    StubData data = data15TimeIndexX();
    StubNetCDFDataProperties props = new StubNetCDFDataProperties();
    assertTrue(data.getProperties(props));
    assertEquals("time", props.timeName);
    assertEquals("x", props.indexName);
    assertEquals(0, props.originMap.get("time").intValue());
    assertEquals("1:end", props.indexStride.toString());
    assertEquals("0:end", props.timeStride.toString());
    assertTrue(props.strideAvailable);
  }

  @Test
  void getPropertiesRejectsOtherProperties() throws IOException {
    StubData data = data16();
    SGProperties other =
        new SGProperties() {
          private static final long serialVersionUID = 1L;
        };
    assertFalse(data.getProperties(other));
  }

  @Test
  void setPropertiesRestoresState() throws IOException {
    SGNetCDFFile srcFile = openFile(EXAMPLE_15);
    StubData source =
        new StubData(
            srcFile,
            new SGDataSourceObserver(),
            columnInfo(srcFile, "time", "Animation Frame"),
            columnInfo(srcFile, "x", SGDataColumnTypeConstants.INDEX),
            new SGIntegerSeriesSet(1, 3, 1),
            true);
    source.setOrigin("x", 3);
    StubNetCDFDataProperties props = new StubNetCDFDataProperties();
    assertTrue(source.getProperties(props));

    SGNetCDFFile dstFile = openFile(EXAMPLE_15);
    StubData target = new StubData(dstFile, new SGDataSourceObserver());
    assertTrue(target.setProperties(props));
    assertTrue(target.isTimeVariableAvailable());
    assertTrue(target.isIndexAvailable());
    assertEquals(3, target.getOrigin("x"));
    assertEquals("1:end", target.getIndexStride().toString());
    assertTrue(target.isStrideAvailable());
  }

  @Test
  void setPropertiesRejectsOtherProperties() throws IOException {
    StubData data = data16();
    SGProperties other =
        new SGProperties() {
          private static final long serialVersionUID = 1L;
        };
    assertFalse(data.setProperties(other));
  }

  @Test
  void setPropertiesRestoresNullTimeAndIndex() throws IOException {
    StubData data = data16();
    StubNetCDFDataProperties props = new StubNetCDFDataProperties();
    assertTrue(data.getProperties(props));
    assertNull(props.timeName);
    assertNull(props.indexName);

    StubData target = data16();
    assertTrue(target.setProperties(props));
    assertFalse(target.isTimeVariableAvailable());
    assertFalse(target.isIndexAvailable());
  }

  // -----------------------------------------------------------------
  // Time and index
  // -----------------------------------------------------------------

  @Test
  void timeAccessorsWithoutTimeVariableReturnDefaults() throws IOException {
    StubData data = data16();
    assertNull(data.getTimeValueArray());
    assertEquals(-1, data.getAnimationLength());
    assertEquals(-1, data.getCurrentTimeValueIndex());
    assertEquals(-1, data.getCurrentTimeValue());
    assertEquals(-1, data.getCurrentTimeValue().intValue());
    // no-op without a time variable
    data.setCurrentTimeValueIndex(0);
    assertNull(data.getTimeStride());
    assertNull(data.getTimeStrideInstance());
    assertNull(data.getIndexStrideInstance());
  }

  @Test
  void timeAccessorsWithTimeVariable() throws IOException {
    StubData data = data15Time();
    double[] timeValues = data.getTimeValueArray();
    assertArrayEquals(new double[] {0.0, 1.0, 2.0, 3.0}, timeValues);
    assertEquals(0, data.getCurrentTimeValueIndex());
    assertEquals(0.0, data.getCurrentTimeValue().doubleValue(), 0.0);
    data.setCurrentTimeValueIndex(3);
    assertEquals(3, data.getCurrentTimeValueIndex());
    assertEquals(3.0, data.getCurrentTimeValue().doubleValue(), 0.0);
  }

  @Test
  void setCurrentTimeValueIndexRejectsOutOfRangeIndex() throws IOException {
    StubData data = data15Time();
    assertThrows(
        IllegalArgumentException.class,
        () -> data.setCurrentTimeValueIndex(4),
        "Index out of bounds: 4");
    assertThrows(
        IllegalArgumentException.class,
        () -> data.setCurrentTimeValueIndex(-1),
        "Index out of bounds: -1");
  }

  @Test
  void indexStrideAccessorsReturnClones() throws IOException {
    StubData data = data16IndexYWithStride();
    SGIntegerSeriesSet stride1 = data.getIndexStride();
    SGIntegerSeriesSet stride2 = data.getIndexStride();
    assertNotSame(stride1, stride2);
    assertEquals("1:end", stride1.toString());
    assertNull(data16().getIndexStride());
  }

  @Test
  void setIndexStrideReplacesAndNullsStride() throws IOException {
    StubData data = data16IndexY();
    data.setIndexStride(new SGIntegerSeriesSet(0, 7, 2));
    assertEquals("0:2:end", data.getIndexStride().toString());
    data.setIndexStride(null);
    assertNull(data.getIndexStride());
  }

  @Test
  void timeStrideAccessorsReturnClones() throws IOException {
    StubData data = data15Time();
    SGIntegerSeriesSet stride1 = data.getTimeStride();
    SGIntegerSeriesSet stride2 = data.getTimeStride();
    assertNotSame(stride1, stride2);
    assertEquals("0:end", stride1.toString());
    data.setTimeStride(null);
    assertNull(data.getTimeStride());
    assertNull(data16().getTimeStride());
  }

  @Test
  void setPropertiesRestoresTimeStrideNull() throws IOException {
    StubData data = data16();
    StubNetCDFDataProperties props = new StubNetCDFDataProperties();
    assertTrue(data.getProperties(props));
    StubData target = data15Time();
    assertTrue(target.setProperties(props));
    assertFalse(target.isTimeVariableAvailable());
    assertNull(target.getTimeStride());
  }

  // -----------------------------------------------------------------
  // Origin
  // -----------------------------------------------------------------

  @Test
  void setOriginAcceptsValidIndex() throws IOException {
    StubData data = data16();
    assertTrue(data.setOrigin("x", 3));
    assertEquals(3, data.getOrigin("x"));
    assertTrue(data.setOrigin("y", 11));
    assertEquals(11, data.getOrigin("y"));
  }

  @Test
  void setOriginRejectsUnknownDimensionAndOutOfRangeOrigin() throws IOException {
    StubData data = data16();
    assertFalse(data.setOrigin("z", 0));
    assertFalse(data.setOrigin("x", -1));
    assertFalse(data.setOrigin("x", 8));
    assertFalse(data.setOrigin("y", 12));
    assertEquals(0, data.getOrigin("x"));
  }

  @Test
  void getOriginThrowsForUnknownDimension() throws IOException {
    StubData data = data16();
    assertThrows(
        IllegalArgumentException.class, () -> data.getOrigin("z"), "Dimension is not found: z");
  }

  // -----------------------------------------------------------------
  // Coordinate values
  // -----------------------------------------------------------------

  @Test
  void getCoordinateValueReturnsStoredValues() throws IOException {
    StubData data = data16();
    assertEquals(1.0, data.getCoordinateValue("x", 0), 0.0);
    assertEquals(15.0, data.getCoordinateValue("x", 7), 0.0);
    assertEquals(0.5, data.getCoordinateValue("y", 0), 0.0);
    assertEquals(11.5, data.getCoordinateValue("y", 11), 0.0);
  }

  @Test
  void getCoordinateValueThrowsForUnknownOrNonCoordinateVariable() throws IOException {
    StubData data = data16();
    assertThrows(
        IllegalArgumentException.class,
        () -> data.getCoordinateValue("z", 0),
        "Variable is not found: z");
    assertThrows(
        IllegalArgumentException.class,
        () -> data.getCoordinateValue("height", 0),
        "Not a coordinate variable: height");
    assertThrows(
        IllegalArgumentException.class,
        () -> data.getCoordinateValue("x", 8),
        "Index out of bounds [0, 8 + ]: 8");
    assertThrows(
        IllegalArgumentException.class,
        () -> data.getCoordinateValue("x", -1),
        "Index out of bounds [0, 8 + ]: -1");
  }

  // -----------------------------------------------------------------
  // Names and dimensions
  // -----------------------------------------------------------------

  @Test
  void getNameWithUnitUsesLongNameAndUnit() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    assertEquals("X Coordinate [m]", data.getNameWithUnit(file.findVariable("x")));
    assertEquals("Height [km]", data.getNameWithUnit(file.findVariable("height")));
  }

  @Test
  void findDimensionReturnsDimensionOfGivenName() throws IOException {
    StubData data = data16();
    assertEquals("x", data.findDimension("x").getShortName());
    assertEquals(8, data.findDimension("x").getLength());
    assertNull(data.findDimension("z"));
  }

  @Test
  void findDimensionOrderReturnsPosition() throws IOException {
    StubData data = data16();
    List<Dimension> dims = data.getNetcdfFile().getDimensions();
    assertEquals(0, data.findDimensionOrder(dims, "x"));
    assertEquals(1, data.findDimensionOrder(dims, "y"));
    assertEquals(-1, data.findDimensionOrder(dims, "time"));
  }

  @Test
  void getValidNameSanitizesInvalidNames() throws IOException {
    StubData data = data16();
    assertEquals("abc", data.getValidName("abc"));
    assertEquals("sg_9x", data.getValidName("9x"));
    assertEquals("a_b", data.getValidName("a b"));
    assertEquals("sg___", data.getValidName("__"));
  }

  @Test
  void createIntArrayReturnsSequentialIndices() throws IOException {
    StubData data = data16();
    assertArrayEquals(new int[] {0, 1, 2, 3}, data.createIntArray(4));
  }

  @Test
  void getNetCDFStrideFormatsSeries() throws IOException {
    assertEquals("0:7", SGNetCDFData.getNetCDFStride(new SGIntegerSeries(0, 7, 1)));
    assertEquals("0:7:2", SGNetCDFData.getNetCDFStride(new SGIntegerSeries(0, 7, 2)));
    assertEquals("3", SGNetCDFData.getNetCDFStride(new SGIntegerSeries(3)));
    assertEquals("0:7", SGNetCDFData.getNetCDFStride(new SGIntegerSeries(7, 0, -1)));
  }

  @Test
  void checkNonCoordinateVariableAcceptsValueVariable() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    // no exception expected
    data.checkNonCoordinateVariable(file.findVariable("height"), file.findDimension("x"));
    data.checkNonCoordinateVariable(null, file.findDimension("x"));
  }

  @Test
  void checkNonCoordinateVariableRejectsCoordinateVariable() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    assertThrows(
        IllegalArgumentException.class,
        () -> data.checkNonCoordinateVariable(file.findVariable("x"), file.findDimension("x")),
        "The variable x must not be coordinate values.");
    // a dimension of another file does not belong to height
    SGNetCDFFile file15 = openFile(EXAMPLE_15);
    assertThrows(
        IllegalArgumentException.class,
        () ->
            data.checkNonCoordinateVariable(
                file.findVariable("height"), file15.findDimension("time")),
        "The variable height does not have a dimension time");
  }

  // -----------------------------------------------------------------
  // Data source
  // -----------------------------------------------------------------

  @Test
  void setDataSourceRejectsNonNetCDFFile() throws IOException {
    StubData data = data16();
    assertThrows(
        IllegalArgumentException.class,
        () -> data.setDataSource(new StubDataSource()),
        "Invalid data source.");
  }

  @Test
  void setDataSourceRefindsVariablesInNewFile() throws IOException {
    StubData data = data15Time();
    SGNetCDFFile newFile = openFile(EXAMPLE_15);
    data.setDataSource(newFile);
    assertEquals(newFile, data.getDataSource());
    assertTrue(data.isTimeVariableAvailable());
    assertEquals("time", data.getTimeVariable().getName());
  }

  // -----------------------------------------------------------------
  // Command strings and info
  // -----------------------------------------------------------------

  @Test
  void getColumnTypeCommandStringListsIndexAndTime() throws IOException {
    assertEquals("()", data16().getColumnTypeCommandString());
    assertEquals("(y:Index)", data16IndexY().getColumnTypeCommandString());
    assertEquals("(x:Index,time:Animation Frame)", data15TimeIndexX().getColumnTypeCommandString());
  }

  @Test
  void getOriginCommandStringListsCoordinateOrigins() throws IOException {
    StubData data = data16();
    assertEquals("(x(0),y(0))", data.getOriginCommandString());
    data.setOrigin("x", 1);
    assertEquals("(x(1),y(0))", data.getOriginCommandString());
  }

  @Test
  void getInfoMapContainsTypeSourceAndStrides() throws IOException {
    StubData data = data16IndexYWithStride();
    // the info map is built from the dimension stride map
    Map<String, SGIntegerSeriesSet> strideMap = new HashMap<String, SGIntegerSeriesSet>();
    strideMap.put("x", SGIntegerSeriesSet.createInstance(8));
    strideMap.put("y", data.getIndexStride());
    data.setStrideMap(strideMap);
    Map<String, Object> map = data.getInfoMap();
    assertEquals("StubData", map.get("DataType"));
    assertEquals(data.getNetcdfFile(), map.get("Data Source"));
    assertEquals(true, map.get("Stride Available"));
    assertEquals("0:end", ((SGIntegerSeriesSet) map.get("x")).toString());
    assertEquals("1:end", ((SGIntegerSeriesSet) map.get("y")).toString());
  }

  @Test
  void setArraySectionPropertyAddsAvailability() throws IOException {
    StubData data = data16IndexYWithStride();
    SGPropertyMap map = new SGPropertyMap();
    assertTrue(data.setArraySectionProperty(map));
    assertEquals("true", map.getValue("ArraySectionAvailable"));
  }

  @Test
  void getDataSetFileExtensionAndDataType() throws IOException {
    StubData data = data16();
    assertEquals("nc", data.getDataSetFileExtension());
    assertEquals("StubData", data.getNetCDFDataSetDataType());
  }

  // -----------------------------------------------------------------
  // Property writing
  // -----------------------------------------------------------------

  @Test
  void writePropertyWritesOriginsTimeAndIndex() throws Exception {
    StubData data = data16IndexY();
    Element el = element();
    assertTrue(
        data.writeProperty(el, new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF)));
    assertEquals("false", el.getAttribute("ArraySectionAvailable"));
    // the origin map is written as comma joined key=value tokens
    String origin = el.getAttribute("Origin");
    List<String> tokens = new ArrayList<String>();
    for (String token : origin.split(",")) {
      tokens.add(token);
    }
    tokens.sort(null);
    assertEquals(java.util.Arrays.asList("x=0", "y=0"), tokens);
    assertEquals("y", el.getAttribute("IndexVariableName"));
    assertEquals("0:end", el.getAttribute("IndexArraySection"));
    assertFalse(el.hasAttribute("TimeVariableName"));
  }

  @Test
  void writePropertyWritesOnlyAssignedVariableOriginsForArchive() throws Exception {
    StubData data = data16IndexY();
    SGNetCDFFile file = data.getNetcdfFile();
    data.setAssignedVariables(
        new SGNetCDFVariable[] {file.findVariable("x"), file.findVariable("height")});
    Element el = element();
    assertTrue(
        data.writeProperty(
            el, new SGExportParameter(SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET)));
    // only the origins of the assigned variables are written
    assertEquals("x=0", el.getAttribute("Origin"));
    assertEquals("y", el.getAttribute("IndexVariableName"));
    assertEquals("0:end", el.getAttribute("IndexArraySection"));
  }

  @Test
  void writePropertyWritesTimeVariableName() throws Exception {
    StubData data = data15TimeIndexX();
    Element el = element();
    assertTrue(
        data.writeProperty(el, new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF)));
    assertEquals("time", el.getAttribute("TimeVariableName"));
    assertEquals("x", el.getAttribute("IndexVariableName"));
    assertEquals("1:end", el.getAttribute("IndexArraySection"));
  }

  // -----------------------------------------------------------------
  // Value arrangement
  // -----------------------------------------------------------------

  @Test
  void arrange1DimFlattensArray() {
    StubData data = new StubData();
    ArrayDouble.D2 array = new ArrayDouble.D2(2, 3);
    array.set(0, 0, 1.5);
    array.set(0, 1, 2.5);
    array.set(0, 2, 3.5);
    array.set(1, 0, 4.5);
    array.set(1, 1, 5.5);
    array.set(1, 2, 6.5);
    assertArrayEquals(new double[] {1.5, 2.5, 3.5, 4.5, 5.5, 6.5}, data.arrange1Dim(array));
  }

  @Test
  void arrangeString1DimTransformsStrings() {
    StubData data = new StubData();
    ArrayChar array = new ArrayChar.D1(3);
    array.setChar(0, 'a');
    array.setChar(1, 'b');
    array.setChar(2, 'c');
    // ucar getString(i) returns the whole string, so every element is the
    // full string
    assertArrayEquals(new String[] {"abc", "abc", "abc"}, data.arrangeString1Dim(array, 3));
  }

  @Test
  void arrange2DimFlattensInVariableDimensionOrder() throws Exception {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    SGNetCDFVariable xVar = file.findVariable("x");
    SGNetCDFVariable yVar = file.findVariable("y");
    SGNetCDFVariable hVar = file.findVariable("height");
    Array array = hVar.read("0:7,0:11").reduce();
    Map<String, SGIntegerSeries> stride = new HashMap<String, SGIntegerSeries>();
    stride.put("x", new SGIntegerSeries(0, 7, 1));
    stride.put("y", new SGIntegerSeries(0, 11, 1));

    // the flattened order is x-major: ret[k] = height(k % 8, k / 8)
    double[] values = data.arrange2Dim(array, hVar, new SGNetCDFVariable[] {xVar, yVar}, stride);
    assertEquals(1.5, values[0], 0.0);
    assertEquals(3.5, values[1], 0.0);
    assertEquals(5.5, values[2], 0.0);
    assertEquals(7.5, values[3], 0.0);
    assertEquals(1.0e36, values[4], 0.0);
    assertEquals(11.5, values[5], 0.0);
    assertEquals(13.5, values[6], 0.0);
    assertEquals(2.5, values[8], 0.0);
    assertEquals(26.5, values[95], 0.0);

    // the order also depends on the order of the given coordinate variables
    // relative to the dimension order of the variable
    double[] values2 = data.arrange2Dim(array, hVar, new SGNetCDFVariable[] {yVar, xVar}, stride);
    assertEquals(1.5, values2[0], 0.0);
    assertEquals(2.5, values2[1], 0.0);
    assertEquals(3.5, values2[2], 0.0);
    assertEquals(5.5, values2[4], 0.0);
    assertEquals(1.0e36, values2[5], 0.0);
    assertEquals(7.5, values2[6], 0.0);
    assertEquals(9.5, values2[8], 0.0);
    assertEquals(26.5, values2[95], 0.0);
    assertFalse(values[1] == values2[1]);
  }

  // -----------------------------------------------------------------
  // Block lists
  // -----------------------------------------------------------------

  @Test
  void createBlockListReadsFullAndPartialSections() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    SGNetCDFVariable xVar = file.findVariable("x");
    SGNetCDFVariable yVar = file.findVariable("y");
    SGNetCDFVariable hVar = file.findVariable("height");
    SGNetCDFVariable[] cVars = new SGNetCDFVariable[] {xVar, yVar};
    Map<String, Integer> originMap = new HashMap<String, Integer>();
    originMap.put("x", 0);
    originMap.put("y", 0);

    // full ranges
    Map<String, SGIntegerSeriesSet> fullStride = new HashMap<String, SGIntegerSeriesSet>();
    fullStride.put("x", SGIntegerSeriesSet.createInstance(8));
    fullStride.put("y", SGIntegerSeriesSet.createInstance(12));
    List<SGNamedDoubleValueIndexBlock> blocks =
        data.createBlockList(hVar, cVars, originMap, fullStride);
    assertEquals(1, blocks.size());
    SGNamedDoubleValueIndexBlock block = blocks.get(0);
    assertEquals(96, block.getLength());
    assertEquals("0:end", block.getSeries("x").toString());
    assertEquals("0:end", block.getSeries("y").toString());
    double[] values = block.getValues();
    assertEquals(1.5, values[0], 0.0);
    assertEquals(2.5, values[1], 0.0);
    assertEquals(3.5, values[2], 0.0);
    assertEquals(7.5, values[6], 0.0);
    assertEquals(9.5, values[8], 0.0);
    assertEquals(1.0e36, values[5], 0.0);
    assertEquals(1.0e36, values[48], 0.0);
    assertEquals(26.5, values[95], 0.0);

    // partial x range
    Map<String, SGIntegerSeriesSet> partialStride = new HashMap<String, SGIntegerSeriesSet>();
    partialStride.put("x", new SGIntegerSeriesSet(1, 3, 1));
    partialStride.put("y", SGIntegerSeriesSet.createInstance(12));
    blocks = data.createBlockList(hVar, cVars, originMap, partialStride);
    assertEquals(1, blocks.size());
    block = blocks.get(0);
    assertEquals(36, block.getLength());
    assertEquals("1:end", block.getSeries("x").toString());
    values = block.getValues();
    assertEquals(3.5, values[0], 0.0);
    assertEquals(4.5, values[1], 0.0);
    assertEquals(7.5, values[24], 0.0);
  }

  @Test
  void createAllValueBlockListUsesFullRanges() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    SGNetCDFVariable hVar = file.findVariable("height");
    SGNetCDFVariable[] cVars =
        new SGNetCDFVariable[] {file.findVariable("x"), file.findVariable("y")};
    Map<String, Integer> originMap = new HashMap<String, Integer>();
    originMap.put("x", 0);
    originMap.put("y", 0);
    List<SGNamedDoubleValueIndexBlock> blocks =
        data.createAllValueBlockList(hVar, cVars, originMap);
    assertEquals(1, blocks.size());
    assertEquals(96, blocks.get(0).getLength());
  }

  @Test
  void get2DValueBlockListReturnsNullWhenIndexIsAvailable() throws IOException {
    StubData data = data16IndexY();
    SGNetCDFFile file = data.getNetcdfFile();
    assertNull(
        data.get2DValueBlockList(
            file.findVariable("height"),
            file.findVariable("x"),
            file.findVariable("y"),
            false,
            false));
  }

  @Test
  void get2DValueBlockListReturnsBlocksOfTheRequestedSection() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    SGNetCDFVariable hVar = file.findVariable("height");
    SGNetCDFVariable xVar = file.findVariable("x");
    SGNetCDFVariable yVar = file.findVariable("y");

    // full range
    List<SGXYSimpleDoubleValueIndexBlock> blocks =
        data.get2DValueBlockList(hVar, xVar, yVar, true, false);
    assertEquals(1, blocks.size());
    SGXYSimpleDoubleValueIndexBlock block = blocks.get(0);
    assertEquals(Double.valueOf(1.5), block.getValue(0, 0));
    assertEquals(Double.valueOf(2.5), block.getValue(1, 0));
    assertEquals(Double.valueOf(5.5), block.getValue(4, 0));
    assertEquals(Double.valueOf(1.0e36), block.getValue(5, 0));
    assertEquals(Double.valueOf(26.5), block.getValue(7, 11));

    // the fill value is removed when requested
    blocks = data.get2DValueBlockList(hVar, xVar, yVar, true, true);
    block = blocks.get(0);
    assertTrue(Double.isNaN(block.getValue(5, 0)));
    assertTrue(Double.isNaN(block.getValue(1, 2)));
    assertEquals(Double.valueOf(1.5), block.getValue(0, 0));

    // partial section from the dimension stride map
    Map<String, SGIntegerSeriesSet> strideMap = new HashMap<String, SGIntegerSeriesSet>();
    strideMap.put("x", new SGIntegerSeriesSet(1, 3, 1));
    strideMap.put("y", new SGIntegerSeriesSet(2, 4, 1));
    data.setStrideMap(strideMap);
    blocks = data.get2DValueBlockList(hVar, xVar, yVar, false, false);
    assertEquals(1, blocks.size());
    block = blocks.get(0);
    assertEquals(Double.valueOf(5.5), block.getValue(1, 2));
    assertEquals(Double.valueOf(7.5), block.getValue(3, 2));
    assertEquals(Double.valueOf(11.5), block.getValue(3, 4));
    assertNull(block.getValue(0, 2));
    assertNull(block.getValue(1, 1));
  }

  // -----------------------------------------------------------------
  // Fill values
  // -----------------------------------------------------------------

  @Test
  void removeFillValueReplacesFillValuesWithNaN() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    SGNetCDFVariable hVar = file.findVariable("height");
    double[] values = new double[] {1.5, 1.0e36, 9.5};
    double[] result = data.removeFillValue(hVar, values);
    assertEquals(1.5, result[0], 0.0);
    assertTrue(Double.isNaN(result[1]));
    assertEquals(9.5, result[2], 0.0);
  }

  // -----------------------------------------------------------------
  // Index values
  // -----------------------------------------------------------------

  @Test
  void getIndexValueAtReturnsValueFromIndexDimension() throws IOException {
    StubData data = data16IndexY();
    SGNetCDFFile file = data.getNetcdfFile();
    // the index variable is y, so index 1 is height at x origin and y = 1
    SGDataValue.Value value = data.getIndexValueAt(file.findVariable("height"), 1);
    assertEquals(2.5, value.number, 0.0);
    assertTrue(value.missing);
  }

  // -----------------------------------------------------------------
  // Column information
  // -----------------------------------------------------------------

  @Test
  void getColumnInfoListsAllVariablesInFileOrder() throws IOException {
    StubData data = data16();
    data.setColumnType(new String[] {"X", "Y", "Number", "Number", "Number"});
    SGDataColumnInfo[] cols = data.getColumnInfo();
    assertEquals(5, cols.length);
    assertEquals("x", ((SGNetCDFDataColumnInfo) cols[0]).getName());
    assertEquals("height", ((SGNetCDFDataColumnInfo) cols[2]).getName());
    assertEquals("ue", ((SGNetCDFDataColumnInfo) cols[4]).getName());
    assertEquals("X", cols[0].getColumnType());
    assertEquals("Number", cols[4].getColumnType());
    assertEquals(0, ((SGNetCDFDataColumnInfo) cols[0]).getOrigin());
    assertEquals(0, ((SGNetCDFDataColumnInfo) cols[2]).getOrigin());
  }

  @Test
  void getColumnInfoUsesCurrentOriginsForCoordinateVariables() throws IOException {
    StubData data = data16();
    data.setColumnType(new String[] {"X", "Y", "Number", "Number", "Number"});
    data.setOrigin("x", 3);
    SGDataColumnInfo[] cols = data.getColumnInfo();
    assertEquals(3, ((SGNetCDFDataColumnInfo) cols[0]).getOrigin());
    assertEquals(0, ((SGNetCDFDataColumnInfo) cols[1]).getOrigin());
  }

  // -----------------------------------------------------------------
  // Tooltips and flags
  // -----------------------------------------------------------------

  @Test
  void getToolTipTextNotSpatiallyVariedListsDimensions() throws IOException {
    StubData data = data16();
    data.setToolTipDimensions(new ArrayList<Dimension>());
    assertEquals("", data.getToolTipTextNotSpatiallyVaried());

    SGNetCDFFile file = data.getNetcdfFile();
    List<Dimension> dims = new ArrayList<Dimension>();
    dims.add(file.findDimension("x"));
    dims.add(file.findDimension("y"));
    data.setToolTipDimensions(dims);
    assertEquals("x=0 (1.0),<br>y=0 (0.5)", data.getToolTipTextNotSpatiallyVaried());
    data.setOrigin("x", 7);
    assertEquals("x=7 (15.0),<br>y=0 (0.5)", data.getToolTipTextNotSpatiallyVaried());
  }

  @Test
  void remoteFileAndAnimationFlags() throws IOException {
    StubData data = data16();
    assertFalse(data.isRemoteFile());
    assertTrue(data.isAnimationSupported());
    assertFalse(data.isAnimationAvailable());
    assertEquals(-1, data.getAnimationLength());
  }

  // -----------------------------------------------------------------
  // Array setup
  // -----------------------------------------------------------------

  @Test
  void setArrayWritesValuesOfEachNumericType() throws IOException {
    StubData data = data16();
    double[] values = new double[] {1.9, -1.9, 70000.0, 65.5, 200.0};

    ArrayInt.D1 aInt = new ArrayInt.D1(5, true);
    assertTrue(data.setArray(aInt, values));
    assertEquals(1, aInt.get(0));
    assertEquals(-1, aInt.get(1));

    ArrayShort.D1 aShort = new ArrayShort.D1(5, true);
    assertTrue(data.setArray(aShort, values));
    assertEquals((double) (short) 70000.0, aShort.get(2), 0.0);

    ArrayLong.D1 aLong = new ArrayLong.D1(5, true);
    assertTrue(data.setArray(aLong, values));
    assertEquals(70000L, aLong.get(2));

    ArrayFloat.D1 aFloat = new ArrayFloat.D1(5);
    assertTrue(data.setArray(aFloat, values));
    assertEquals(70000.0f, aFloat.get(2), 0.0f);

    ArrayDouble.D1 aDouble = new ArrayDouble.D1(5);
    assertTrue(data.setArray(aDouble, values));
    assertEquals(70000.0, aDouble.get(2), 0.0);

    ArrayChar.D1 aChar = new ArrayChar.D1(5);
    assertTrue(data.setArray(aChar, values));
    assertEquals('A', aChar.get(3));

    ArrayByte.D1 aByte = new ArrayByte.D1(5, true);
    assertTrue(data.setArray(aByte, values));
    assertEquals(-56.0, aByte.get(4), 0.0);
  }

  @Test
  void setArrayWritesTwoDimensionalValuesAndRejectsOtherShapes() throws IOException {
    StubData data = data16();
    double[][] values = new double[][] {{1.9, 2.9}, {3.9, 4.9}};

    ArrayInt.D2 aInt = new ArrayInt.D2(2, 2, true);
    assertTrue(data.setArray(aInt, values));
    assertEquals(1, aInt.get(0, 0));
    assertEquals(4, aInt.get(1, 1));

    ArrayShort.D2 aShort = new ArrayShort.D2(2, 2, true);
    assertTrue(data.setArray(aShort, values));
    assertEquals(3, aShort.get(1, 0));

    ArrayLong.D2 aLong = new ArrayLong.D2(2, 2, true);
    assertTrue(data.setArray(aLong, values));
    assertEquals(4L, aLong.get(1, 1));

    ArrayFloat.D2 aFloat = new ArrayFloat.D2(2, 2);
    assertTrue(data.setArray(aFloat, values));
    assertEquals(2.9f, aFloat.get(0, 1), 0.0f);

    ArrayDouble.D2 aDouble = new ArrayDouble.D2(2, 2);
    assertTrue(data.setArray(aDouble, values));
    assertEquals(4.9, aDouble.get(1, 1), 0.0);

    ArrayChar.D2 aChar = new ArrayChar.D2(2, 2);
    assertTrue(data.setArray(aChar, values));
    assertEquals((char) 4.9, aChar.get(1, 1));

    ArrayByte.D2 aByte = new ArrayByte.D2(2, 2, true);
    assertTrue(data.setArray(aByte, values));
    assertEquals((double) (byte) 4.9, aByte.get(1, 1), 0.0);

    // shapes other than the one- and two-dimensional typed arrays are not
    // supported
    ArrayDouble.D0 d0 = new ArrayDouble.D0();
    d0.set(0.0);
    assertFalse(data.setArray(d0, new double[] {0.0}));
    assertFalse(data.setArray(d0, new double[][] {{0.0}}));
    assertFalse(data.setArray(new ArrayDouble.D2(2, 2), new double[] {0.0}));
    assertFalse(data.setArray(new ArrayDouble.D1(2), new double[][] {{0.0}}));
  }

  // -----------------------------------------------------------------
  // Block arrangement and export data type
  // -----------------------------------------------------------------

  @Test
  void arrangeValueArrayConcatenatesBlockValues() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    Map<String, SGIntegerSeries> seriesMap = new HashMap<String, SGIntegerSeries>();
    seriesMap.put("x", new SGIntegerSeries(0, 1, 1));
    seriesMap.put("y", new SGIntegerSeries(0, 0, 1));
    SGNamedDoubleValueIndexBlock b1 =
        new SGNamedDoubleValueIndexBlock(new double[] {1.0, 2.0}, seriesMap);
    // the second block has a different series map with a single index
    Map<String, SGIntegerSeries> seriesMap2 = new HashMap<String, SGIntegerSeries>();
    seriesMap2.put("x", new SGIntegerSeries(2, 2, 1));
    seriesMap2.put("y", new SGIntegerSeries(0, 0, 1));
    SGNamedDoubleValueIndexBlock b2 =
        new SGNamedDoubleValueIndexBlock(new double[] {3.0}, seriesMap2);
    assertArrayEquals(
        new double[] {1.0, 2.0, 3.0},
        data.arrangeValueArray(file.findVariable("height"), java.util.Arrays.asList(b1, b2)));
  }

  @Test
  void getExportNumberDataTypeDependsOnOperation() throws IOException {
    StubData data = data16();
    SGNetCDFVariable hVar = data.getNetcdfFile().findVariable("height");
    SGDataBufferPolicy policy = new SGDataBufferPolicy(true, false, false);
    assertEquals(
        DataType.DOUBLE,
        data.getExportNumberDataType(
            hVar, new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF), policy));
    // height is a double variable, so the archive data type is the same
    assertEquals(
        DataType.DOUBLE,
        data.getExportNumberDataType(
            hVar, new SGExportParameter(SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET), policy));
  }

  // -----------------------------------------------------------------
  // Export
  // -----------------------------------------------------------------

  @Test
  void saveToNetCDFFileDelegatesToExport() throws IOException {
    StubData data = data16();
    File file = newTempPath(".nc").toFile();
    SGExportParameter mode = new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF);
    SGDataBufferPolicy policy = new SGDataBufferPolicy(true, false, false);
    assertTrue(data.saveToNetCDFFile(file, mode, policy));
    assertEquals(1, data.exportCallCount);
    // the stub never writes the file
    assertFalse(file.exists());

    data.setExportResult(false);
    assertFalse(data.saveToNetCDFFile(file, mode, policy));
    assertEquals(2, data.exportCallCount);
  }

  @Test
  void saveToSameFormatFileDelegatesToNetCDFExport() throws IOException {
    StubData data = data16();
    File file = newTempPath(".nc").toFile();
    SGExportParameter mode = new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF);
    SGDataBufferPolicy policy = new SGDataBufferPolicy(true, false, false);
    assertTrue(data.saveToSameFormatFile(file, mode, policy));
    assertEquals(1, data.exportCallCount);
    assertFalse(file.exists());
  }

  @Test
  void unsupportedExportOperationsThrow() throws IOException {
    StubData data = data16();
    File file = newTempPath(".h5").toFile();
    SGExportParameter mode = new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF);
    SGDataBufferPolicy policy = new SGDataBufferPolicy(true, false, false);
    UnsupportedOperationException e =
        assertThrows(
            UnsupportedOperationException.class, () -> data.saveToHDF5File(file, mode, policy));
    assertEquals("HDF5 export is not supported for NetCDF data", e.getMessage());
    e =
        assertThrows(
            UnsupportedOperationException.class, () -> data.saveToMATLABFile(file, mode, policy));
    assertEquals("MATLAB export is not supported for NetCDF data", e.getMessage());
    e =
        assertThrows(
            UnsupportedOperationException.class, () -> data.saveToTextFile(file, mode, policy));
    assertEquals("Text export is not supported for NetCDF data", e.getMessage());
  }

  @Test
  void saveToArchiveDataSetFileWritesAssignedVariables() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    data.setAssignedVariables(
        new SGNetCDFVariable[] {file.findVariable("x"), file.findVariable("height")});
    File out = newTempPath(".nc").toFile();

    // a non-archive operation is rejected
    assertFalse(
        data.saveToArchiveDataSetFile(
            out, new SGExportParameter(SGConstants.OPERATION.EXPORT_TO_NETCDF)));
    assertFalse(out.exists());

    assertTrue(
        data.saveToArchiveDataSetFile(
            out, new SGExportParameter(SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET)));
    assertEquals(2, data.setEditedValuesCallCount);
    assertTrue(out.exists());

    // the archived height is the x row at the y origin
    try (NetcdfFile read = NetcdfFiles.open(out.getAbsolutePath())) {
      Variable h = read.findVariable("height");
      assertNotNull(h);
      assertEquals(1, h.getDimensions().size());
      assertArrayEquals(
          new double[] {1.5, 3.5, 5.5, 7.5, 1.0e36, 11.5, 13.5, 15.5}, readAllValues(h));
      Variable x = read.findVariable("x");
      assertEquals(8, x.getShape()[0]);
    } finally {
      out.delete();
    }
  }

  @Test
  void saveToArchiveDataSetFileWritesCoordinateVariables() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    data.setAssignedVariables(new SGNetCDFVariable[] {file.findVariable("x")});
    File out = tempFile(".nc").toFile();
    assertTrue(
        data.saveToArchiveDataSetFile(
            out, new SGExportParameter(SGConstants.OPERATION.SAVE_TO_ARCHIVE_DATA_SET)));
    assertEquals(1, data.setEditedValuesCallCount);
    try (NetcdfFile read = NetcdfFiles.open(out.getAbsolutePath())) {
      Variable x = read.findVariable("x");
      assertNotNull(x);
      assertArrayEquals(new double[] {1.0, 3.0, 5.0, 7.0, 9.0, 11.0, 13.0, 15.0}, readAllValues(x));
    } finally {
      out.delete();
    }
  }

  @Test
  void saveToDataSetNetCDFFileCopiesGlobalAttributesAndAssignedVariables() throws IOException {
    StubData data = data16();
    SGNetCDFFile file = data.getNetcdfFile();
    data.setAssignedVariables(new SGNetCDFVariable[] {file.findVariable("height")});
    File out = tempFile(".nc").toFile();
    assertTrue(data.saveToDataSetNetCDFFile(out));
    try (NetcdfFile read = NetcdfFiles.open(out.getAbsolutePath())) {
      String title = null;
      for (ucar.nc2.Attribute attr : read.getGlobalAttributes()) {
        if ("title".equals(attr.getName())) {
          title = attr.getStringValue();
        }
      }
      assertEquals("Test Data 001", title);
      // dimensions of the assigned variable are added
      assertEquals(8, read.findDimension("x").getLength());
      assertEquals(12, read.findDimension("y").getLength());
      // coordinate variables matching dimension names are copied as well
      assertNotNull(read.findVariable("x"));
      assertNotNull(read.findVariable("y"));
      // variables that are neither assigned nor coordinate values are not
      // copied
      assertNull(read.findVariable("le"));
      assertNull(read.findVariable("ue"));
      Variable h = read.findVariable("height");
      assertNotNull(h);
      assertEquals(96, h.getShape()[0] * h.getShape()[1]);
      assertEquals(1.5, h.read().getDouble(0), 0.0);
    } finally {
      out.delete();
    }
  }
}
