package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData.SXYZNetCDFDataProperties;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData.VXYNetCDFDataProperties;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of the two dimensional NetCDF data properties, covered through the SXYZ and VXY
 * concrete subclasses.
 */
class SGTwoDimensionalNetCDFDataPropertiesTest {

  private static SXYZNetCDFDataProperties filled() {
    final SXYZNetCDFDataProperties p = new SXYZNetCDFDataProperties();
    p.xName = "x";
    p.yName = "y";
    p.zName = "z";
    p.xIndexName = "ix";
    p.yIndexName = "iy";
    p.xStride = SGIntegerSeriesSet.createInstance(2);
    p.yStride = SGIntegerSeriesSet.createInstance(3);
    p.timeName = "time";
    p.indexName = "index";
    p.indexStride = SGIntegerSeriesSet.createInstance(4);
    p.timeStride = SGIntegerSeriesSet.createInstance(5);
    p.originMap = new HashMap<String, Integer>();
    p.originMap.put("x", 1);
    p.strideAvailable = true;
    p.mDecimalPlaces = 2;
    p.mExponent = 3;
    p.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(1.5, "y", 3, 7, "var"));
    return p;
  }

  private static VXYNetCDFDataProperties filledVXY() {
    final VXYNetCDFDataProperties p = new VXYNetCDFDataProperties();
    p.fName = "f";
    p.sName = "s";
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final SXYZNetCDFDataProperties p = new SXYZNetCDFDataProperties();
    assertNull(p.xName);
    assertNull(p.yName);
    assertNull(p.zName);
    assertNull(p.xIndexName);
    assertNull(p.yIndexName);
    assertNull(p.xStride);
    assertNull(p.yStride);
    assertNull(p.timeName);
    assertNull(p.indexStride);
    assertNull(p.timeStride);
    assertTrue(p.originMap.isEmpty());
  }

  @Test
  void equalsMatchesIdenticalProperties() {
    assertTrue(filled().equals(filled()));
    assertFalse(filled().equals(new Object()));
    // a sibling two dimensional NetCDF property type is not equal
    assertFalse(filled().equals(filledVXY()));
  }

  @Test
  void equalsDistinguishesTheColumnTypes() {
    final SXYZNetCDFDataProperties x = filled();
    x.xName = "x2";
    assertFalse(filled().equals(x));

    final SXYZNetCDFDataProperties y = filled();
    y.yName = "y2";
    assertFalse(filled().equals(y));

    final SXYZNetCDFDataProperties z = filled();
    z.zName = "z2";
    assertFalse(filled().equals(z));

    final SXYZNetCDFDataProperties index = filled();
    index.xIndexName = "ix2";
    assertFalse(filled().equals(index));
  }

  @Test
  void equalsDistinguishesTheStrides() {
    final SXYZNetCDFDataProperties xStride = filled();
    xStride.xStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(xStride));

    final SXYZNetCDFDataProperties yStride = filled();
    yStride.yStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(yStride));

    final SXYZNetCDFDataProperties indexStride = filled();
    indexStride.indexStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(indexStride));

    final SXYZNetCDFDataProperties timeStride = filled();
    timeStride.timeStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(timeStride));
  }

  @Test
  void vxyEqualsDistinguishesTheFieldNames() {
    final VXYNetCDFDataProperties f = filledVXY();
    f.fName = "f2";
    assertFalse(filledVXY().equals(f));

    final VXYNetCDFDataProperties s = filledVXY();
    s.sName = "s2";
    assertFalse(filledVXY().equals(s));

    assertTrue(filledVXY().equals(filledVXY()));
  }

  @Test
  void equalsDistinguishesTheArrayAndOriginFields() {
    final SXYZNetCDFDataProperties strideAvailable = filled();
    strideAvailable.strideAvailable = false;
    assertFalse(filled().equals(strideAvailable));

    final SXYZNetCDFDataProperties decimalPlaces = filled();
    decimalPlaces.mDecimalPlaces = 5;
    assertFalse(filled().equals(decimalPlaces));

    final SXYZNetCDFDataProperties history = filled();
    history.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(2.5, "y", 3, 7, "var"));
    assertFalse(filled().equals(history));

    final SXYZNetCDFDataProperties origin = filled();
    origin.originMap.put("x", 2);
    assertFalse(filled().equals(origin));
  }

  @Test
  void copyDeepCopiesTheMapsAndTheStrides() {
    final SXYZNetCDFDataProperties original = filled();
    final SXYZNetCDFDataProperties copy = (SXYZNetCDFDataProperties) original.copy();
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.xStride, original.xStride);
    assertNotSame(copy.yStride, original.yStride);
    assertNotSame(copy.indexStride, original.indexStride);
    assertNotSame(copy.timeStride, original.timeStride);
    assertNotSame(copy.originMap, original.originMap);
  }

  @Test
  void disposeClearsTheOwnedFields() {
    final SXYZNetCDFDataProperties p = filled();
    p.dispose();
    assertTrue(p.isDisposed());
    assertNull(p.xName);
    assertNull(p.yName);
    assertNull(p.zName);
    assertNull(p.xIndexName);
    assertNull(p.yIndexName);
    assertNull(p.xStride);
    assertNull(p.yStride);
    assertNull(p.timeName);
    assertNull(p.indexStride);
    assertNull(p.timeStride);
    assertNull(p.originMap);

    final VXYNetCDFDataProperties v = filledVXY();
    v.dispose();
    assertTrue(v.isDisposed());
    assertNull(v.fName);
    assertNull(v.sName);
  }
}
