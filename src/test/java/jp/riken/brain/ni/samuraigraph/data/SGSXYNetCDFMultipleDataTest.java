package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/**
 * Unit tests for {@link SGSXYNetCDFMultipleData} built from the example NetCDF file.
 *
 * <p>Example16.nc contains coordinate variables x (8) and y (12) and variables height, le and ue of
 * type (x, y).
 */
class SGSXYNetCDFMultipleDataTest {

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  private SGNetCDFFile ncfile;

  @BeforeEach
  void openFile() throws IOException {
    this.ncfile = new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_16));
  }

  private SGNetCDFDataColumnInfo info(String name) {
    return SGDataFileUtility.createDataColumnInfo(
        this.ncfile.findVariable(name), SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER);
  }

  private SGNetCDFDataColumnInfo xInfo(String name) {
    return SGDataFileUtility.createDataColumnInfo(
        this.ncfile.findVariable(name), SGIDataColumnTypeConstants.X_VALUE);
  }

  private SGNetCDFDataColumnInfo yInfo(String name) {
    return SGDataFileUtility.createDataColumnInfo(
        this.ncfile.findVariable(name), SGIDataColumnTypeConstants.Y_VALUE);
  }

  private SGSXYNetCDFMultipleData createBasic() {
    return new SGSXYNetCDFMultipleData(
        this.ncfile,
        new SGDataSourceObserver(),
        new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
        new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
        null,
        null,
        null,
        null,
        true);
  }

  @Test
  void buildsMultipleDataFromExample16() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    assertEquals(SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA, data.getDataType());
    assertTrue(data.hasMultipleYValues());
    assertFalse(data.isDimensionPicked());
    assertEquals(8, data.getAllPointsNumber());
    assertEquals(1, data.getChildNumber());
    assertNull(data.getDateFlag());
  }

  @Test
  void singleAndMultipleVariablesAreResolved() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    assertEquals("height", data.getMultipleVariables()[0].getName());
    assertEquals("x", data.getSingleVariable().getName());
    assertEquals("x", data.getXVariable().getName());
    assertEquals("height", data.getYVariable().getName());
  }

  @Test
  void constructorRejectsNullOrEmptyColumnInfo() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYNetCDFMultipleData(
                this.ncfile,
                new SGDataSourceObserver(),
                null,
                new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
                null,
                null,
                null,
                null,
                true));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYNetCDFMultipleData(
                this.ncfile,
                new SGDataSourceObserver(),
                new SGNetCDFDataColumnInfo[] {},
                new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
                null,
                null,
                null,
                null,
                true));
  }

  @Test
  void constructorRejectsBothSidesNonCoordinate() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYNetCDFMultipleData(
                this.ncfile,
                new SGDataSourceObserver(),
                new SGNetCDFDataColumnInfo[] {this.xInfo("height")},
                new SGNetCDFDataColumnInfo[] {this.yInfo("le")},
                null,
                null,
                null,
                null,
                true));
  }

  @Test
  void constructorRejectsUnknownVariableName() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYNetCDFMultipleData(
                this.ncfile,
                new SGDataSourceObserver(),
                new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
                new SGNetCDFDataColumnInfo[] {this.info("unknown")},
                null,
                null,
                null,
                null,
                true));
  }

  @Test
  void valueArraysMatchExample16() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    double[][] xValues = data.getXValueArray(false);
    double[][] yValues = data.getYValueArray(false);
    SGSXYDataBufferPolicy policy = new SGSXYDataBufferPolicy(false, false, false, false, false);
    double[][] xUnshifted = data.getUnshiftedXValueArray(policy);
    double[][] yUnshifted = data.getUnshiftedYValueArray(policy);
    assertEquals(1, xValues.length);
    assertEquals(1, yValues.length);
    assertEquals(8, xValues[0].length);
    assertEquals(1.0, xValues[0][0], 0.0);
    assertEquals(7.0, xValues[0][3], 0.0);
    assertEquals(15.0, xValues[0][7], 0.0);
    assertEquals(9.0, xValues[0][4], 0.0);
    assertTrue(Double.isNaN(yValues[0][4]));
    assertArrayEquals(xValues[0], xUnshifted[0]);
    assertEquals(1.0E36, yUnshifted[0][4], 0.0);
    assertNotNull(yValues[0]);
    assertEquals(8, yValues[0].length);
    assertEquals(1.5, yValues[0][0], 0.0);
    assertEquals(7.5, yValues[0][3], 0.0);
    assertEquals(15.5, yValues[0][7], 0.0);

    assertEquals(8, xUnshifted[0].length);
    assertEquals(8, yUnshifted[0].length);
  }

  @Test
  void shiftIsClonedOnGetAndSet() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    SGTuple2d shift = data.getShift();
    assertEquals(0.0, shift.x, 0.0);
    assertEquals(0.0, shift.y, 0.0);
    data.setShift(new SGTuple2d(2.0, -1.0));
    SGTuple2d got = data.getShift();
    assertEquals(2.0, got.x, 0.0);
    assertEquals(-1.0, got.y, 0.0);
    assertThrows(IllegalArgumentException.class, () -> data.setShift(null));
  }

  @Test
  void exponentRoundTrip() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    data.setExponent(3);
    assertEquals(3, data.getExponent());
  }

  @Test
  void strideAndTickLabelStrideHaveDefaults() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    SGIntegerSeriesSet stride = data.getStride();
    SGIntegerSeriesSet tickLabelStride = data.getTickLabelStride();
    assertNotNull(stride);
    assertNotNull(tickLabelStride);
  }

  @Test
  void errorBarUnavailableWithoutErrorColumns() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    assertFalse(data.isErrorBarAvailable());
    assertNull(data.hasSameErrorVariable());
  }

  @Test
  void errorColumnValidationRejectsMismatchedLengths() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYNetCDFMultipleData(
                this.ncfile,
                new SGDataSourceObserver(),
                new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
                new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
                new SGNetCDFDataColumnInfo[] {this.info("le"), this.info("ue")},
                new SGNetCDFDataColumnInfo[] {this.info("ue")},
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true));
  }

  @Test
  void errorColumnValidationRejectsUnknownNames() {
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSXYNetCDFMultipleData(
                this.ncfile,
                new SGDataSourceObserver(),
                new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
                new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
                new SGNetCDFDataColumnInfo[] {this.info("le")},
                new SGNetCDFDataColumnInfo[] {this.info("unknown")},
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                true));
  }

  @Test
  void strideLimitsValueArrays() {
    SGSXYNetCDFMultipleData data =
        new SGSXYNetCDFMultipleData(
            this.ncfile,
            new SGDataSourceObserver(),
            new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
            new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
            null,
            null,
            new SGIntegerSeriesSet(0, 7, 2),
            null,
            true);
    double[][] x = data.getXValueArray(false);
    assertEquals(1, x.length);
    assertEquals(4, x[0].length);
    assertEquals(1.0, x[0][0], 0.0);
    assertEquals(5.0, x[0][1], 0.0);
    assertEquals(9.0, x[0][2], 0.0);
    assertEquals(13.0, x[0][3], 0.0);
  }

  @Test
  void sxyDataAndMultipleArraysHaveChildren() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
    assertEquals(1, sxyArray.length);
    assertTrue(sxyArray[0] instanceof SGSXYNetCDFData);
    SGISXYTypeMultipleData[] multipleArray = data.getSXYTypeMultipleDataArray();
    assertEquals(1, multipleArray.length);
    assertNotNull(multipleArray[0]);
  }

  @Test
  void propertiesAreReturned() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    assertNotNull(data.getProperties());
  }

  @Test
  void errorBarValueArraysWithHolder() throws Exception {
    Path path = Files.createTempFile("samurai-graph-test", ".nc");
    Files.delete(path);
    path.toFile().deleteOnExit();
    ucar.nc2.NetcdfFileWriter writer =
        ucar.nc2.NetcdfFileWriter.createNew(
            ucar.nc2.NetcdfFileWriter.Version.netcdf3, path.toString());
    writer.addDimension(null, "x", 5);
    writer.addVariable(null, "x", ucar.ma2.DataType.FLOAT, "x");
    writer.addVariable(null, "height", ucar.ma2.DataType.FLOAT, "x");
    writer.addVariable(null, "le", ucar.ma2.DataType.FLOAT, "x");
    writer.addVariable(null, "ue", ucar.ma2.DataType.FLOAT, "x");
    writer.create();
    float[] floats = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f};
    writer.write("x", ucar.ma2.Array.factory(ucar.ma2.DataType.FLOAT, new int[] {5}, floats));
    floats = new float[] {10.0f, 20.0f, 30.0f, 40.0f, 50.0f};
    writer.write("height", ucar.ma2.Array.factory(ucar.ma2.DataType.FLOAT, new int[] {5}, floats));
    floats = new float[] {0.5f, 1.0f, 1.5f, 2.0f, 2.5f};
    writer.write("le", ucar.ma2.Array.factory(ucar.ma2.DataType.FLOAT, new int[] {5}, floats));
    floats = new float[] {1.5f, 2.0f, 2.5f, 3.0f, 3.5f};
    writer.write("ue", ucar.ma2.Array.factory(ucar.ma2.DataType.FLOAT, new int[] {5}, floats));
    writer.close();

    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open(path.toString()));
    SGNetCDFDataColumnInfo[] xInfo = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE)
    };
    SGNetCDFDataColumnInfo[] yInfo = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE)
    };
    SGNetCDFDataColumnInfo[] leInfo = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("le"), SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER)
    };
    SGNetCDFDataColumnInfo[] ueInfo = {
      SGDataFileUtility.createDataColumnInfo(
          file.findVariable("ue"), SGIDataColumnTypeConstants.VALUE_TYPE_NUMBER)
    };
    SGNetCDFDataColumnInfo[] ehInfo = {
      SGDataFileUtility.createDataColumnInfo(file.findVariable("height"), "")
    };
    SGSXYNetCDFMultipleData data =
        new SGSXYNetCDFMultipleData(
            file,
            new SGDataSourceObserver(),
            xInfo,
            yInfo,
            leInfo,
            ueInfo,
            ehInfo,
            null,
            null,
            null,
            null,
            null,
            null,
            true);
    assertTrue(data.isErrorBarAvailable());
    Boolean[] same = data.hasSameErrorVariable();
    assertNotNull(same);
    assertEquals(1, same.length);
    SGSXYDataBufferPolicy policy = new SGSXYDataBufferPolicy(false, false, false, false, false);
    double[][] le = data.getLowerErrorValueArray(policy);
    double[][] ue = data.getUpperErrorValueArray(policy);
    assertNotNull(le);
    assertEquals(1, le.length);
    assertEquals(5, le[0].length);
    assertEquals(0.5, le[0][0], 0.0);
    assertEquals(2.5, le[0][4], 0.0);
    assertEquals(1.5, ue[0][0], 0.0);
    assertEquals(3.5, ue[0][4], 0.0);
  }

  @Test
  void propertiesRoundTrip() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    SGProperties properties = data.getProperties();
    assertNotNull(properties);
    SGSXYNetCDFMultipleData restored =
        new SGSXYNetCDFMultipleData(
            this.ncfile,
            new SGDataSourceObserver(),
            new SGNetCDFDataColumnInfo[] {this.xInfo("x")},
            new SGNetCDFDataColumnInfo[] {this.yInfo("height")},
            null,
            null,
            null,
            null,
            true);
    assertTrue(restored.setProperties(properties));
    assertEquals("x", restored.getXVariable().getName());
    assertEquals("height", restored.getYVariable().getName());
  }

  @Test
  void setStrideRefetchesValues() {
    SGSXYNetCDFMultipleData data = this.createBasic();
    data.setStride(new SGIntegerSeriesSet(0, 7, 2));
    double[][] x = data.getXValueArray(false);
    assertEquals(4, x[0].length);
    assertEquals(1.0, x[0][0], 0.0);
    assertEquals(9.0, x[0][2], 0.0);
  }
}
