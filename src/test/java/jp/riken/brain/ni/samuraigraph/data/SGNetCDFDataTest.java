package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGSXYNetCDFData} built from the example NetCDF files. */
class SGNetCDFDataTest {

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";
  private static final String EXAMPLE_17 = "examples/data/Example17.nc";

  private static SGSXYNetCDFData createSXYDataFrom(String examplePath) throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open(examplePath));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGDataColumnTypeConstants.Y_VALUE);
    return new SGSXYNetCDFData(
        file,
        new SGDataSourceObserver(),
        xInfo,
        yInfo,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true);
  }

  private static SGSXYNetCDFData createSXYDataFromExample16() throws IOException {
    return createSXYDataFrom(EXAMPLE_16);
  }

  @Test
  void createsSXYNetCDFData() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    assertEquals(SGDataTypeConstants.SXY_NETCDF_DATA, data.getDataType());
  }

  @Test
  void xValuesAreReadFromCoordinateVariable() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    double[] xValues = data.getXValueArray(false);
    assertNotNull(xValues);
    assertEquals(1.0, xValues[0], 0.0);
    assertEquals(15.0, xValues[xValues.length - 1], 0.0);
  }

  @Test
  void yValuesAreReadFromTwoDimensionalVariable() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    double[] yValues = data.getYValueArray(false);
    assertNotNull(yValues);
    assertEquals(8, yValues.length);
    assertEquals(1.5, yValues[0], 0.0);
    assertEquals(3.5, yValues[1], 0.0);
  }

  @Test
  void copyCopiesTheFormatStateByValue() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    data.setShift(new SGTuple2d(1.0, 2.0));
    data.setDecimalPlaces(3);
    data.setExponent(4);
    data.setDateFormat("yyyy-MM-dd");
    SGSXYNetCDFData copy = (SGSXYNetCDFData) data.copy();
    SGTuple2d shift = copy.getShift();
    assertEquals(1.0, shift.x, 0.0);
    assertEquals(2.0, shift.y, 0.0);
    assertEquals(3, copy.getDecimalPlaces());
    assertEquals(4, copy.getExponent());
    assertEquals("yyyy-MM-dd", copy.getDateFormat());
    // the copy must not share the state with the source
    assertNotSame(data.getNumberFormat(), copy.getNumberFormat());
    copy.setShift(new SGTuple2d(5.0, 6.0));
    SGTuple2d source = data.getShift();
    assertEquals(1.0, source.x, 0.0);
    assertEquals(2.0, source.y, 0.0);
  }

  @Test
  void copySurvivesDisposalOfTheSource() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    data.setShift(new SGTuple2d(2.0, 3.0));
    SGSXYNetCDFData copy = (SGSXYNetCDFData) data.copy();
    data.dispose();
    SGTuple2d shift = copy.getShift();
    assertEquals(2.0, shift.x, 0.0);
    assertEquals(3.0, shift.y, 0.0);
  }

  @Test
  void setterRejectsNullShift() throws IOException {
    SGSXYNetCDFData data = createSXYDataFromExample16();
    assertThrows(IllegalArgumentException.class, () -> data.setShift(null));
  }

  // -----------------------------------------------------------------
  // NetCDF-4 reading (Example17)
  //
  // Reading a NetCDF-4 (HDF5-based) file must be handled by the pure-Java
  // reader shipped on the classpath (cdm-core), not by the JNA-backed
  // Nc4Iosp that requires a system libnetcdf. These tests pin that behavior
  // so a future change that routes NetCDF-4 reads through the native library
  // is caught.
  // -----------------------------------------------------------------

  @Test
  void netcdf4FileIsOpenedByThePureJavaReader() throws IOException {
    try (NetcdfFile file = NetcdfFiles.open(EXAMPLE_17)) {
      String iosp = file.getIosp().getClass().getName();
      assertFalse(iosp.contains("Nc4Iosp"), "NetCDF-4 read must not use the JNA reader: " + iosp);
      assertFalse(iosp.contains("nc2.jni"), "NetCDF-4 read must not use the JNI reader: " + iosp);
    }
  }

  @Test
  void createsSXYNetCDFDataFromNetcdf4File() throws IOException {
    SGSXYNetCDFData data = createSXYDataFrom(EXAMPLE_17);
    assertEquals(SGDataTypeConstants.SXY_NETCDF_DATA, data.getDataType());
  }

  @Test
  void xValuesAreReadFromNetcdf4CoordinateVariable() throws IOException {
    SGSXYNetCDFData data = createSXYDataFrom(EXAMPLE_17);
    double[] xValues = data.getXValueArray(false);
    assertNotNull(xValues);
    assertEquals(8, xValues.length);
    assertEquals(1.0, xValues[0], 0.0);
    assertEquals(15.0, xValues[xValues.length - 1], 0.0);
  }

  @Test
  void yValuesAreReadFromNetcdf4TwoDimensionalVariable() throws IOException {
    SGSXYNetCDFData data = createSXYDataFrom(EXAMPLE_17);
    double[] yValues = data.getYValueArray(false);
    assertNotNull(yValues);
    assertEquals(8, yValues.length);
    assertEquals(0.0, yValues[0], 0.0);
    assertEquals(1.0, yValues[1], 0.0);
    assertEquals(7.0, yValues[yValues.length - 1], 0.0);
  }
}
