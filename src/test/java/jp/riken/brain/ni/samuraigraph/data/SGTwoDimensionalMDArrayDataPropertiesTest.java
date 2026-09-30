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
import jp.riken.brain.ni.samuraigraph.data.SGSXYZMDArrayData.SXYZMDDataProperties;
import jp.riken.brain.ni.samuraigraph.data.SGVXYMDArrayData.VXYMDDataProperties;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of the two dimensional MD array data properties, covered through the SXYZ and VXY
 * concrete subclasses.
 */
class SGTwoDimensionalMDArrayDataPropertiesTest {

  private static SXYZMDDataProperties filled() {
    final SXYZMDDataProperties p = new SXYZMDDataProperties();
    p.xName = "x";
    p.yName = "y";
    p.zName = "z";
    p.xStride = SGIntegerSeriesSet.createInstance(2);
    p.yStride = SGIntegerSeriesSet.createInstance(3);
    p.scatterStride = SGIntegerSeriesSet.createInstance(4);
    p.timeStride = SGIntegerSeriesSet.createInstance(5);
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

  private static VXYMDDataProperties filledVXY() {
    final VXYMDDataProperties p = new VXYMDDataProperties();
    p.fName = "f";
    p.sName = "s";
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final SXYZMDDataProperties p = new SXYZMDDataProperties();
    assertNull(p.xName);
    assertNull(p.yName);
    assertNull(p.zName);
    assertNull(p.xStride);
    assertNull(p.yStride);
    assertNull(p.scatterStride);
    assertNull(p.timeStride);
    assertTrue(p.dimensionIndexMap.isEmpty());
    assertTrue(p.originMap.isEmpty());
  }

  @Test
  void equalsMatchesIdenticalProperties() {
    assertTrue(filled().equals(filled()));
    assertFalse(filled().equals(new Object()));
    // a sibling two dimensional MD property type is not equal
    assertFalse(filled().equals(filledVXY()));
  }

  @Test
  void equalsDistinguishesTheColumnTypes() {
    final SXYZMDDataProperties x = filled();
    x.xName = "x2";
    assertFalse(filled().equals(x));

    final SXYZMDDataProperties y = filled();
    y.yName = "y2";
    assertFalse(filled().equals(y));

    final SXYZMDDataProperties z = filled();
    z.zName = "z2";
    assertFalse(filled().equals(z));
  }

  @Test
  void equalsDistinguishesTheStrides() {
    final SXYZMDDataProperties xStride = filled();
    xStride.xStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(xStride));

    final SXYZMDDataProperties yStride = filled();
    yStride.yStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(yStride));

    final SXYZMDDataProperties scatterStride = filled();
    scatterStride.scatterStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(scatterStride));

    final SXYZMDDataProperties timeStride = filled();
    timeStride.timeStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(timeStride));
  }

  @Test
  void vxyEqualsDistinguishesTheFieldNames() {
    final VXYMDDataProperties f = filledVXY();
    f.fName = "f2";
    assertFalse(filledVXY().equals(f));

    final VXYMDDataProperties s = filledVXY();
    s.sName = "s2";
    assertFalse(filledVXY().equals(s));

    assertTrue(filledVXY().equals(filledVXY()));
  }

  @Test
  void copyDeepCopiesTheStrides() {
    final SXYZMDDataProperties original = filled();
    final SXYZMDDataProperties copy = (SXYZMDDataProperties) original.copy();
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.xStride, original.xStride);
    assertNotSame(copy.yStride, original.yStride);
    assertNotSame(copy.scatterStride, original.scatterStride);
    assertNotSame(copy.timeStride, original.timeStride);
    assertNotSame(copy.dimensionIndexMap, original.dimensionIndexMap);
    assertNotSame(copy.originMap.get("x"), original.originMap.get("x"));
  }

  @Test
  void disposeClearsTheOwnedFields() {
    final SXYZMDDataProperties p = filled();
    p.dispose();
    assertTrue(p.isDisposed());
    assertNull(p.xName);
    assertNull(p.yName);
    assertNull(p.zName);
    assertNull(p.xStride);
    assertNull(p.yStride);
    assertNull(p.scatterStride);
    assertNull(p.timeStride);
    assertTrue(p.dimensionIndexMap.isEmpty());
    assertTrue(p.originMap.isEmpty());

    final VXYMDDataProperties v = filledVXY();
    v.dispose();
    assertTrue(v.isDisposed());
    assertNull(v.fName);
    assertNull(v.sName);
  }
}
