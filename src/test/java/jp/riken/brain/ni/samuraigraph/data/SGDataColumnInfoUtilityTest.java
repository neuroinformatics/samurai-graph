package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.jmatio.io.MatFileReader;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLDouble;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGDataColumnInfoUtility}. */
class SGDataColumnInfoUtilityTest {

  private static SGSDArrayDataColumnInfo column(
      final String title, final String valueType, final String columnType) {
    SGSDArrayDataColumnInfo info = new SGSDArrayDataColumnInfo(title, valueType, 4);
    info.setColumnType(columnType);
    return info;
  }

  @Test
  void hasEqualColumnTypeComparesTypes() {
    SGSDArrayDataColumnInfo[] a = {
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE),
      column("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.Y_VALUE)
    };
    SGSDArrayDataColumnInfo[] b = {
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE),
      column("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.Y_VALUE)
    };
    assertTrue(SGDataColumnInfoUtility.hasEqualColumnType(a, b));
    b[1].setColumnType(SGIDataColumnTypeConstants.Z_VALUE);
    assertFalse(SGDataColumnInfoUtility.hasEqualColumnType(a, b));
  }

  @Test
  void hasEqualInputComparesColumns() {
    SGSDArrayDataColumnInfo[] a = {
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE)
    };
    SGSDArrayDataColumnInfo[] b = {
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE)
    };
    assertTrue(SGDataColumnInfoUtility.hasEqualInput(a, b));
    b[0].setColumnType(SGIDataColumnTypeConstants.Z_VALUE);
    assertFalse(SGDataColumnInfoUtility.hasEqualInput(a, b));
  }

  @Test
  void findColumnsByTypeNameAndValueType() {
    SGSDArrayDataColumnInfo[] cols = {
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE),
      column("y", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.Y_VALUE),
      column("label", SGIDataColumnTypeConstants.VALUE_TYPE_TEXT, "")
    };
    List<SGDataColumnInfo> byType =
        SGDataColumnInfoUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.Y_VALUE);
    assertEquals(1, byType.size());
    assertEquals("y", byType.get(0).getName());
    assertNotNull(SGDataColumnInfoUtility.findColumnWithName(cols, "x"));
    assertNull(SGDataColumnInfoUtility.findColumnWithName(cols, "missing"));
    List<SGDataColumnInfo> byValue =
        SGDataColumnInfoUtility.findColumnsWithValueType(
            cols, SGIDataColumnTypeConstants.VALUE_TYPE_TEXT);
    assertEquals(1, byValue.size());
    assertEquals("label", byValue.get(0).getName());
    List<SGDataColumnInfo> startsWith =
        SGDataColumnInfoUtility.findColumnsWithColumnTypeStartsWith(
            cols, SGIDataColumnTypeConstants.Y_VALUE);
    assertEquals(1, startsWith.size());
  }

  @Test
  void getHolderNameExtractsNameAfterMiddleColumn() {
    SGSDArrayDataColumnInfo info =
        column("t", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, "Lower for y");
    assertEquals("y", SGDataColumnInfoUtility.getHolderName(info));
  }

  @Test
  void isValidPickUpAndTimeValue() {
    assertTrue(SGDataColumnInfoUtility.isValidPickUpValue(0));
    assertTrue(SGDataColumnInfoUtility.isValidPickUpValue(3));
    assertFalse(SGDataColumnInfoUtility.isValidPickUpValue(-1));
    assertTrue(SGDataColumnInfoUtility.isValidTimeValue(0));
    assertFalse(SGDataColumnInfoUtility.isValidTimeValue(-1));
  }

  @Test
  void hasIndexAndPickupChecks() {
    SGSDArrayDataColumnInfo[] cols = {
      column(
          "index", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.INDEX),
      column("x", SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER, SGIDataColumnTypeConstants.X_VALUE)
    };
    assertTrue(SGDataColumnInfoUtility.hasIndexColumnType(cols));
    assertFalse(SGDataColumnInfoUtility.isPickupColumnContained(cols));
  }

  private static SGMDArrayDataColumnInfo[] createMDArrayColumns() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".mat");
    path.toFile().deleteOnExit();
    MLDouble a = new MLDouble("a", new double[][] {{1.0}, {2.0}, {3.0}});
    MLDouble b = new MLDouble("b", new double[][] {{4.0}, {5.0}, {6.0}});
    new MatFileWriter(path.toFile(), Arrays.asList(a, b));
    MatFileReader reader = new MatFileReader(path.toFile());
    SGMATLABFile matFile = new SGMATLABFile(path.toString(), reader);
    SGMDArrayVariable[] vars = matFile.getVariables();
    SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[2];
    cols[0] = SGDataFileUtility.createDataColumnInfo(vars[0], SGIDataColumnTypeConstants.X_VALUE);
    cols[1] = SGDataFileUtility.createDataColumnInfo(vars[1], SGIDataColumnTypeConstants.Y_VALUE);
    return cols;
  }

  @Test
  void getSXYMDArrayDataLengthFromGenericDimension() throws Exception {
    assertEquals(-1, SGDataColumnInfoUtility.getSXYMDArrayDataLength(createMDArrayColumns()));
  }

  @Test
  void getSXYNetCDFDataLengthReturnsXDimensionLength() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE)
    };
    assertEquals(8, SGDataColumnInfoUtility.getSXYNetCDFDataLength(cols));
    assertEquals(-1, SGDataColumnInfoUtility.getNetCDFDataIndexLength(cols));
  }

  @Test
  void getMDArrayPickUpColumnReturnsNullWithoutPickup() throws Exception {
    assertNull(SGDataColumnInfoUtility.getMDArrayPickUpColumn(createMDArrayColumns()));
  }

  private static SGNetCDFFile createVXYNetCDFFile() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.write.NetcdfFormatWriter.Builder writer =
        ucar.nc2.write.NetcdfFormatWriter.createNewNetcdf3(path.toString());
    ucar.nc2.Dimension xDim = writer.addDimension("x", 5);
    ucar.nc2.Dimension yDim = writer.addDimension("y", 4);
    writer.addVariable("x", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(xDim));
    writer.addVariable("y", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim));
    writer.addVariable("v1", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim, xDim));
    writer.addVariable("v2", ucar.ma2.DataType.FLOAT, java.util.Arrays.asList(yDim, xDim));
    try (ucar.nc2.write.NetcdfFormatWriter ignored = writer.build()) {}
    return new SGNetCDFFile(ucar.nc2.NetcdfFiles.open(path.toString()));
  }

  @Test
  void getVXYNetCDFDataLengthsUseCoordinateDimensions() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.Y_COORDINATE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("v1"), SGIDataColumnTypeConstants.X_COMPONENT)
    };
    assertEquals(5, SGDataColumnInfoUtility.getVXYNetCDFDataXLength(cols));
    assertEquals(4, SGDataColumnInfoUtility.getVXYNetCDFDataYLength(cols));
  }

  @Test
  void getVXYNetCDFDataXLengthReturnsMinusOneWithoutCoordinate() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE)
    };
    assertEquals(-1, SGDataColumnInfoUtility.getVXYNetCDFDataXLength(cols));
  }

  @Test
  void findFrequentTimeOriginReturnsMinusOneWithoutTimeDimension() throws Exception {
    assertEquals(-1, SGDataColumnInfoUtility.findFrequentTimeOrigin(createMDArrayColumns()));
  }

  @Test
  void mdArrayGenericDimensionLengthsReturnMinusOneWhenUnset() throws Exception {
    SGMDArrayDataColumnInfo[] cols = createMDArrayColumns();
    assertEquals(
        -1, SGDataColumnInfoUtility.getVXYMDArrayDataComponentGenericDimensionLength(cols, false));
    assertEquals(-1, SGDataColumnInfoUtility.getSXYZMDArrayDataZGenericDimensionLength(cols));
  }

  @Test
  void findValidPickUpXYColumnsReturnsPickUpColumn() throws Exception {
    SGMDArrayDataColumnInfo[] cols = createMDArrayColumns();
    cols[0].setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    cols[0].setGenericDimensionIndex(1);
    List<SGMDArrayDataColumnInfo> list = SGDataColumnInfoUtility.findValidPickUpXYColumns(cols);
    assertNotNull(list);
    assertEquals(1, list.size());
    assertEquals("a", list.get(0).getName());
  }

  @Test
  void getSXYZMDArrayDataXLengthReturnsMinusOneWithoutZ() throws Exception {
    assertEquals(-1, SGDataColumnInfoUtility.getSXYZMDArrayDataXLength(createMDArrayColumns()));
  }

  private static SGMDArrayVariable variable(
      final String name, final int[] dims, final int[] origins) {
    SGMDArrayVariable var =
        new SGMDArrayVariable() {
          @Override
          public int[] getDimensions() {
            return dims.clone();
          }

          @Override
          public List<jp.riken.brain.ni.samuraigraph.base.SGAttribute> getAttributes() {
            return new java.util.ArrayList<jp.riken.brain.ni.samuraigraph.base.SGAttribute>();
          }

          @Override
          public String getValueType() {
            return SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER;
          }

          @Override
          public boolean isNumberVariable() {
            return true;
          }

          @Override
          public double getDoubleValue(
              final int dimensionIndex, final int arrayIndex, final int[] origins) {
            return 0.0;
          }

          @Override
          public double getDoubleValue(final int index) {
            return 0.0;
          }

          @Override
          public double[] getDoubleArray(
              final int dimensionIndex, final SGIntegerSeriesSet stride, final int[] origins) {
            return new double[0];
          }

          @Override
          public double[] getDoubleArray(final int xIndex, final int yIndex, final int[] origins) {
            return new double[0];
          }

          @Override
          public double[] getDoubleArray(
              final int xIndex,
              final int yIndex,
              final SGIntegerSeriesSet xStride,
              final SGIntegerSeriesSet yStride,
              final int[] origins) {
            return new double[0];
          }

          @Override
          public double[] getDoubleArray(
              final int xIndex,
              final int yIndex,
              final SGIntegerSeries xSeries,
              final SGIntegerSeries ySeries,
              final int[] origins) {
            return new double[0];
          }

          @Override
          public String[] getStringArray(final int dimensionIndex) {
            return new String[0];
          }

          @Override
          public String[] getStringArray(
              final int dimensionIndex, final SGIntegerSeriesSet stride) {
            return new String[0];
          }

          @Override
          public String[] getStringArray(
              final int dimensionIndex, final SGIntegerSeriesSet stride, final int[] origins) {
            return new String[0];
          }

          @Override
          public String getString(
              final int dimensionIndex, final int arrayIndex, final int[] origins) {
            return null;
          }

          @Override
          public String getString(final int[] origins) {
            return null;
          }

          @Override
          public String getString(final int dimensionIndex, final int arrayIndex) {
            return null;
          }

          @Override
          protected MDArrayDataType getDataType() {
            return new MDArrayDataType(VALUE_TYPE.FLOAT);
          }

          @Override
          protected MDArrayDataType getExportFloatingNumberDataType() {
            return new MDArrayDataType(VALUE_TYPE.FLOAT);
          }
        };
    var.mName = name;
    var.mOrigins = origins;
    return var;
  }

  private static SGMDArrayDataColumnInfo mdColumn(
      final String name, final int[] dims, final String columnType) {
    SGMDArrayDataColumnInfo info =
        new SGMDArrayDataColumnInfo(
            variable(name, dims, new int[dims.length]),
            name,
            SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
    info.setColumnType(columnType);
    return info;
  }

  @Test
  void findFrequentTimeOriginReturnsMostFrequentOrigin() {
    SGMDArrayDataColumnInfo xCol =
        mdColumn("x", new int[] {10, 5}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    xCol.setOrigins(new int[] {3, 0});
    SGMDArrayDataColumnInfo yCol =
        mdColumn("y", new int[] {10, 5}, SGIDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    yCol.setOrigins(new int[] {3, 0});
    SGMDArrayDataColumnInfo zCol =
        mdColumn("z", new int[] {10, 5}, SGIDataColumnTypeConstants.Z_VALUE);
    zCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    zCol.setOrigins(new int[] {1, 0});
    assertEquals(
        3,
        SGDataColumnInfoUtility.findFrequentTimeOrigin(new SGDataColumnInfo[] {xCol, yCol, zCol}));
  }

  @Test
  void findFrequentTimeOriginRejectsMismatchedLengths() {
    SGMDArrayDataColumnInfo xCol =
        mdColumn("x", new int[] {10, 5}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol =
        mdColumn("y", new int[] {20, 5}, SGIDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    assertEquals(
        -1, SGDataColumnInfoUtility.findFrequentTimeOrigin(new SGDataColumnInfo[] {xCol, yCol}));
  }

  @Test
  void findFrequentTimeOriginRejectsAllEqualOrigins() {
    SGMDArrayDataColumnInfo xCol =
        mdColumn("x", new int[] {10, 5}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol =
        mdColumn("y", new int[] {10, 5}, SGIDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, 0);
    assertEquals(
        -1, SGDataColumnInfoUtility.findFrequentTimeOrigin(new SGDataColumnInfo[] {xCol, yCol}));
  }

  @Test
  void hasEqualInputComparesMdArrayDimensionIndices() {
    SGMDArrayDataColumnInfo a = mdColumn("x", new int[] {5, 5}, SGIDataColumnTypeConstants.X_VALUE);
    a.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
    SGMDArrayDataColumnInfo b = mdColumn("x", new int[] {5, 5}, SGIDataColumnTypeConstants.X_VALUE);
    b.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
    assertTrue(
        SGDataColumnInfoUtility.hasEqualInput(
            new SGDataColumnInfo[] {a}, new SGDataColumnInfo[] {b}));
    b.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertFalse(
        SGDataColumnInfoUtility.hasEqualInput(
            new SGDataColumnInfo[] {a}, new SGDataColumnInfo[] {b}));
  }

  @Test
  void getVXYMDArrayDataXYLengthsUseGenericOrComponentDimension() {
    SGMDArrayDataColumnInfo xCol =
        mdColumn("x", new int[] {6, 4}, SGIDataColumnTypeConstants.X_COORDINATE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertEquals(
        6, SGDataColumnInfoUtility.getVXYMDArrayDataXLength(new SGDataColumnInfo[] {xCol}, false));

    SGMDArrayDataColumnInfo fCol =
        mdColumn("v1", new int[] {6, 4}, SGIDataColumnTypeConstants.X_COMPONENT);
    fCol.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_X_DIMENSION, 1);
    fCol.setDimensionIndex(SGIMDArrayConstants.KEY_VXY_Y_DIMENSION, 0);
    fCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
    assertEquals(
        4, SGDataColumnInfoUtility.getVXYMDArrayDataXLength(new SGDataColumnInfo[] {fCol}, false));
    assertEquals(
        6, SGDataColumnInfoUtility.getVXYMDArrayDataYLength(new SGDataColumnInfo[] {fCol}, false));
    assertEquals(
        4,
        SGDataColumnInfoUtility.getVXYMDArrayDataComponentGenericDimensionLength(
            new SGDataColumnInfo[] {fCol}, false));
  }

  @Test
  void getSXYZMDArrayDataXYLengthsUseGenericOrZDimension() {
    SGMDArrayDataColumnInfo xCol =
        mdColumn("x", new int[] {5, 3}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertEquals(
        5, SGDataColumnInfoUtility.getSXYZMDArrayDataXLength(new SGDataColumnInfo[] {xCol}));
    assertEquals(
        -1, SGDataColumnInfoUtility.getSXYZMDArrayDataYLength(new SGDataColumnInfo[] {xCol}));

    SGMDArrayDataColumnInfo zCol =
        mdColumn("z", new int[] {3, 2}, SGIDataColumnTypeConstants.Z_VALUE);
    zCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION, 0);
    zCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION, 1);
    assertEquals(
        3, SGDataColumnInfoUtility.getSXYZMDArrayDataYLength(new SGDataColumnInfo[] {zCol}));
    assertEquals(
        2, SGDataColumnInfoUtility.getSXYZMDArrayDataXLength(new SGDataColumnInfo[] {zCol}));
    assertEquals(
        -1,
        SGDataColumnInfoUtility.getSXYZMDArrayDataZGenericDimensionLength(
            new SGDataColumnInfo[] {zCol}));
    zCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
    assertEquals(
        2,
        SGDataColumnInfoUtility.getSXYZMDArrayDataZGenericDimensionLength(
            new SGDataColumnInfo[] {zCol}));
  }

  @Test
  void getSXYMDArrayDataLengthUsesFirstAvailableColumn() {
    SGMDArrayDataColumnInfo yCol = mdColumn("y", new int[] {7}, SGIDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertEquals(7, SGDataColumnInfoUtility.getSXYMDArrayDataLength(new SGDataColumnInfo[] {yCol}));
  }

  @Test
  void getMDArrayPickUpColumnReturnsSinglePickUpColumn() {
    SGMDArrayDataColumnInfo xCol = mdColumn("x", new int[] {5}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol = mdColumn("y", new int[] {5}, SGIDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    assertNull(SGDataColumnInfoUtility.getMDArrayPickUpColumn(new SGDataColumnInfo[] {xCol, yCol}));
    yCol.clearDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    assertEquals(
        xCol.getName(),
        SGDataColumnInfoUtility.getMDArrayPickUpColumn(new SGDataColumnInfo[] {xCol, yCol})
            .getName());
  }

  @Test
  void findValidPickUpXYColumnsHandlesBothAndInvalidCases() {
    SGMDArrayDataColumnInfo xCol = mdColumn("x", new int[] {5}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    SGMDArrayDataColumnInfo yCol =
        mdColumn("y", new int[] {5, 5}, SGIDataColumnTypeConstants.Y_VALUE);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 0);
    assertNull(
        SGDataColumnInfoUtility.findValidPickUpXYColumns(new SGDataColumnInfo[] {xCol, yCol}));

    yCol.setDimensionIndex(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, 1);
    assertEquals(
        1, SGDataColumnInfoUtility.findValidPickUpXYColumns(new SGDataColumnInfo[] {yCol}).size());

    yCol.clearDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
    assertEquals(
        0, SGDataColumnInfoUtility.findValidPickUpXYColumns(new SGDataColumnInfo[] {yCol}).size());
  }

  @Test
  void isPickupColumnContainedDetectsNetCDFAndMdArray() throws IOException {
    assertFalse(SGDataColumnInfoUtility.isPickupColumnContained(new SGDataColumnInfo[] {}));
    SGMDArrayDataColumnInfo xCol = mdColumn("x", new int[] {5}, SGIDataColumnTypeConstants.X_VALUE);
    xCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, 0);
    assertTrue(SGDataColumnInfoUtility.isPickupColumnContained(new SGDataColumnInfo[] {xCol}));

    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open("examples/data/Example16.nc"));
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.PICKUP)
    };
    assertTrue(SGDataColumnInfoUtility.isPickupColumnContained(cols));
  }

  @Test
  void getNetCDFDataIndexLengthAndSXYLengthWithIndex() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.INDEX)
    };
    assertEquals(5, SGDataColumnInfoUtility.getNetCDFDataIndexLength(cols));
    assertEquals(5, SGDataColumnInfoUtility.getSXYNetCDFDataLength(cols));
  }

  @Test
  void getSXYZNetCDFDataLengthsUseCoordinateDimensions() throws Exception {
    SGNetCDFFile file = createVXYNetCDFFile();
    SGDataColumnInfo[] cols = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("y"), SGIDataColumnTypeConstants.Y_VALUE),
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE)
    };
    assertEquals(4, SGDataColumnInfoUtility.getSXYZNetCDFDataYLength(cols));
    assertEquals(5, SGDataColumnInfoUtility.getSXYZNetCDFDataXLength(cols));
    assertEquals(
        4,
        SGDataColumnInfoUtility.getVXYNetCDFDataYLength(
            new SGDataColumnInfo[] {
              SGDataFileUtility.createDataColumnInfo(
                  file.findVariable("y"), SGIDataColumnTypeConstants.Y_COORDINATE)
            }));
  }
}
