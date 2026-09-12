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
}
