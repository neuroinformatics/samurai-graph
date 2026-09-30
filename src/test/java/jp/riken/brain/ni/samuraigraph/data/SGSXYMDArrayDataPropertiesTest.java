package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayData.SXYMDDataProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the SXY MD array data properties. */
class SGSXYMDArrayDataPropertiesTest {

  private static SXYMDDataProperties filled() {
    final SXYMDDataProperties p = new SXYMDDataProperties();
    p.xName = "x";
    p.yName = "y";
    p.lName = "l";
    p.uName = "u";
    p.ehName = "eh";
    p.tName = "t";
    p.thName = "th";
    p.mStride = SGIntegerSeriesSet.createInstance(5);
    p.mTickLabelStride = SGIntegerSeriesSet.createInstance(3);
    p.timeStride = SGIntegerSeriesSet.createInstance(7);
    p.dimensionIndexMap = new HashMap<String, Map<String, Integer>>();
    final Map<String, Integer> dimMap = new HashMap<String, Integer>();
    dimMap.put("t", 0);
    dimMap.put("y", 1);
    p.dimensionIndexMap.put("var", dimMap);
    p.originMap = new HashMap<String, int[]>();
    p.originMap.put("x", new int[] {0, 1});
    p.strideAvailable = true;
    p.mDecimalPlaces = 2;
    p.mExponent = 3;
    p.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(1.5, "y", 3, 7, "var"));
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final SXYMDDataProperties p = new SXYMDDataProperties();
    assertNull(p.xName);
    assertNull(p.mStride);
    assertNull(p.mTickLabelStride);
    assertNull(p.timeStride);
    assertTrue(p.dimensionIndexMap.isEmpty());
    assertTrue(p.originMap.isEmpty());
    assertFalse(p.strideAvailable);
    assertEquals(0, p.mDecimalPlaces);
    assertEquals(0, p.mExponent);
    assertTrue(p.editedDataValueList.isEmpty());
  }

  @Test
  void equalsMatchesIdenticalProperties() {
    assertTrue(filled().equals(filled()));
    assertFalse(filled().equals(new Object()));
    // a sibling MD array property type is not equal
    assertFalse(filled().equals(new SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties()));
  }

  @Test
  void equalsDistinguishesTheColumnTypes() {
    final SXYMDDataProperties x = filled();
    x.xName = "x2";
    assertFalse(filled().equals(x));

    final SXYMDDataProperties th = filled();
    th.thName = "th2";
    assertFalse(filled().equals(th));
  }

  @Test
  void equalsDistinguishesTheSizes() {
    final SXYMDDataProperties stride = filled();
    stride.mStride = SGIntegerSeriesSet.createInstance(4);
    assertFalse(filled().equals(stride));

    final SXYMDDataProperties tickStride = filled();
    tickStride.mTickLabelStride = SGIntegerSeriesSet.createInstance(6);
    assertFalse(filled().equals(tickStride));

    final SXYMDDataProperties dimension = filled();
    final Map<String, Integer> otherDim = new HashMap<String, Integer>();
    otherDim.put("t", 0);
    otherDim.put("y", 2);
    dimension.dimensionIndexMap.put("var", otherDim);
    assertFalse(filled().equals(dimension));

    final SXYMDDataProperties timeStride = filled();
    timeStride.timeStride = SGIntegerSeriesSet.createInstance(8);
    assertFalse(filled().equals(timeStride));
  }

  @Test
  void equalsDistinguishesTheArrayFields() {
    final SXYMDDataProperties strideAvailable = filled();
    strideAvailable.strideAvailable = false;
    assertFalse(filled().equals(strideAvailable));

    final SXYMDDataProperties decimalPlaces = filled();
    decimalPlaces.mDecimalPlaces = 5;
    assertFalse(filled().equals(decimalPlaces));

    final SXYMDDataProperties exponent = filled();
    exponent.mExponent = 1;
    assertFalse(filled().equals(exponent));

    final SXYMDDataProperties history = filled();
    history.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(2.5, "y", 3, 7, "var"));
    assertFalse(filled().equals(history));
  }

  @Test
  void equalsDistinguishesTheOriginMap() {
    final SXYMDDataProperties p = filled();
    p.originMap.put("x", new int[] {0, 2});
    assertFalse(filled().equals(p));
  }

  @Test
  void copyDeepCopiesTheMapsAndTheStrides() {
    final SXYMDDataProperties original = filled();
    final SXYMDDataProperties copy = (SXYMDDataProperties) original.copy();
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.mStride, original.mStride);
    assertNotSame(copy.timeStride, original.timeStride);
    assertNotSame(copy.dimensionIndexMap, original.dimensionIndexMap);
    assertNotSame(copy.dimensionIndexMap.get("var"), original.dimensionIndexMap.get("var"));
    assertNotSame(copy.originMap.get("x"), original.originMap.get("x"));
  }

  @Test
  void editedDataValueListAccessorsReturnCopies() {
    final SXYMDDataProperties p = filled();
    assertEquals(1, p.getEditedDataValueList().size());
    p.addEditedDataValue(new SGDataValueHistory.NetCDF.D1(2.5, "y", 3, 7, "var"));
    assertEquals(2, p.getEditedDataValueList().size());
    p.clearEditedDataValueList();
    assertTrue(p.getEditedDataValueList().isEmpty());
  }

  @Test
  void disposeClearsTheOwnedFields() {
    final SXYMDDataProperties p = filled();
    p.dispose();
    assertTrue(p.isDisposed());
    assertNull(p.xName);
    assertNull(p.mStride);
    assertNull(p.mTickLabelStride);
    assertNull(p.timeStride);
    assertTrue(p.dimensionIndexMap.isEmpty());
    assertTrue(p.originMap.isEmpty());
  }
}
