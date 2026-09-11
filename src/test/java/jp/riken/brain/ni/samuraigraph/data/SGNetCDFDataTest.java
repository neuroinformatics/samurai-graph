package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import org.junit.jupiter.api.Test;
import ucar.nc2.NetcdfFiles;

/** Unit tests for {@link SGSXYNetCDFData} built from the example NetCDF files. */
class SGNetCDFDataTest {

  private static final String EXAMPLE_16 = "examples/data/Example16.nc";

  private SGSXYNetCDFData createSXYDataFromExample16() throws IOException {
    SGNetCDFFile file = new SGNetCDFFile(NetcdfFiles.open(EXAMPLE_16));
    SGNetCDFDataColumnInfo xInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("x"), SGIDataColumnTypeConstants.X_VALUE);
    SGNetCDFDataColumnInfo yInfo =
        SGDataFileUtility.createDataColumnInfo(
            file.findVariable("height"), SGIDataColumnTypeConstants.Y_VALUE);
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
}
