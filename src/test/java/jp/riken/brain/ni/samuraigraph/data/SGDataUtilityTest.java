package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import org.junit.jupiter.api.Test;

/** Unit tests for the pure helpers of {@link SGDataUtility}. */
class SGDataUtilityTest {

  @Test
  void appendColumnNoAppendsOneBasedIndex() {
    assertEquals("SXY for No.1", SGDataUtility.appendColumnNo("SXY", 0));
    assertEquals("SXY for No.3", SGDataUtility.appendColumnNo("SXY", 2));
  }

  @Test
  void appendColumnTitleAppendsVariableName() {
    assertEquals("SXY for x", SGDataUtility.appendColumnTitle("SXY", "x"));
  }

  @Test
  void appendColumnNoOrTitleChoosesByFlag() {
    assertEquals("SXY for No.2", SGDataUtility.appendColumnNoOrTitle("SXY", 1, true, "ignored"));
    assertEquals("SXY for title", SGDataUtility.appendColumnNoOrTitle("SXY", 0, false, "title"));
  }

  @Test
  void removeHeaderTitleExtractsTitle() {
    assertEquals("x", SGDataUtility.removeHeaderTitle("SXY for x"));
    assertNull(SGDataUtility.removeHeaderTitle("SXY"));
  }

  @Test
  void removeHeaderNoExtractsNumber() {
    assertEquals("3", SGDataUtility.removeHeaderNo("SXY for No.3"));
    assertNull(SGDataUtility.removeHeaderNo("SXY for x"));
  }

  @Test
  void getAppendedColumnIndexReturnsZeroBasedIndex() {
    assertEquals(Integer.valueOf(2), SGDataUtility.getAppendedColumnIndex("SXY for No.3"));
    assertNull(SGDataUtility.getAppendedColumnIndex("SXY for x"));
  }

  @Test
  void isNetCDFDataRecognizesNetCDFTypes() {
    assertTrue(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_NETCDF_DATA));
    assertFalse(SGDataUtility.isNetCDFData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isHDF5DataRecognizesHDF5Types() {
    assertTrue(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_HDF5_DATA));
    assertFalse(SGDataUtility.isHDF5Data(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void isSXYTypeDataRecognizesSingleAndMultipleTypes() {
    assertTrue(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_DATA));
    assertTrue(SGDataUtility.isSXYTypeData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataUtility.isSXYTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isVXYTypeDataRecognizesVectorTypes() {
    assertTrue(SGDataUtility.isVXYTypeData(SGDataTypeConstants.VXY_DATA));
    assertFalse(SGDataUtility.isVXYTypeData(SGDataTypeConstants.SXYZ_DATA));
  }

  @Test
  void isSXYZTypeDataRecognizesScalarTypes() {
    assertTrue(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.SXYZ_DATA));
    assertFalse(SGDataUtility.isSXYZTypeData(SGDataTypeConstants.VXY_DATA));
  }

  @Test
  void isMultipleDataRecognizesMultipleTypes() {
    assertTrue(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_MULTIPLE_DATA));
    assertFalse(SGDataUtility.isMultipleData(SGDataTypeConstants.SXY_DATA));
  }

  @Test
  void predicatesRejectNullInput() {
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.isNetCDFData((String) null));
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.isHDF5Data((String) null));
    assertThrows(IllegalArgumentException.class, () -> SGDataUtility.isSXYTypeData((String) null));
  }

  @Test
  void getMinValueIgnoresNonFiniteValues() {
    assertEquals(2.0, SGDataUtility.getMinValue(new double[] {Double.NaN, 5.0, 2.0}), 0.0);
    assertEquals(
        Double.NaN,
        SGDataUtility.getMinValue(new double[] {Double.NaN, Double.POSITIVE_INFINITY}),
        0.0);
  }

  @Test
  void getMaxValueIgnoresNonFiniteValues() {
    assertEquals(5.0, SGDataUtility.getMaxValue(new double[] {5.0, Double.NaN, 2.0}), 0.0);
    assertEquals(Double.NaN, SGDataUtility.getMaxValue(new double[] {}), 0.0);
  }

  @Test
  void getBoundsReturnsMinMaxRange() {
    SGValueRange range = SGDataUtility.getBounds(new double[] {3.0, -1.0, 2.0});
    assertEquals(-1.0, range.getMinValue(), 0.0);
    assertEquals(3.0, range.getMaxValue(), 0.0);
  }
}
