package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFData.SXYNetCDFDataProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the SXY NetCDF data properties. */
class SGSXYNetCDFDataPropertiesTest {

  private static SXYNetCDFDataProperties filled() {
    final SXYNetCDFDataProperties p = new SXYNetCDFDataProperties();
    p.xName = "x";
    p.yName = "y";
    p.lName = "l";
    p.uName = "u";
    p.ehName = "eh";
    p.tName = "t";
    p.thName = "th";
    p.mStride = SGIntegerSeriesSet.createInstance(5);
    p.mTickLabelStride = SGIntegerSeriesSet.createInstance(3);
    p.timeName = "time";
    p.indexName = "index";
    p.indexStride = SGIntegerSeriesSet.createInstance(4);
    p.timeStride = SGIntegerSeriesSet.createInstance(6);
    p.originMap = new HashMap<String, Integer>();
    p.originMap.put("x", 1);
    p.strideAvailable = true;
    p.mDecimalPlaces = 2;
    p.mExponent = 3;
    p.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(1.5, "y", 3, 7, "var"));
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final SXYNetCDFDataProperties p = new SXYNetCDFDataProperties();
    assertNull(p.xName);
    assertNull(p.mStride);
    assertNull(p.mTickLabelStride);
    assertNull(p.timeName);
    assertNull(p.indexStride);
    assertNull(p.timeStride);
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
    // a sibling NetCDF property type is not equal
    assertFalse(filled().equals(new SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties()));
  }

  @Test
  void equalsDistinguishesTheColumnTypes() {
    final SXYNetCDFDataProperties x = filled();
    x.xName = "x2";
    assertFalse(filled().equals(x));

    final SXYNetCDFDataProperties th = filled();
    th.thName = "th2";
    assertFalse(filled().equals(th));

    final SXYNetCDFDataProperties time = filled();
    time.timeName = "t2";
    assertFalse(filled().equals(time));

    final SXYNetCDFDataProperties index = filled();
    index.indexName = "i2";
    assertFalse(filled().equals(index));
  }

  @Test
  void equalsDistinguishesTheSizes() {
    final SXYNetCDFDataProperties stride = filled();
    stride.mStride = SGIntegerSeriesSet.createInstance(4);
    assertFalse(filled().equals(stride));

    final SXYNetCDFDataProperties tickStride = filled();
    tickStride.mTickLabelStride = SGIntegerSeriesSet.createInstance(6);
    assertFalse(filled().equals(tickStride));

    final SXYNetCDFDataProperties indexStride = filled();
    indexStride.indexStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(filled().equals(indexStride));

    final SXYNetCDFDataProperties timeStride = filled();
    timeStride.timeStride = SGIntegerSeriesSet.createInstance(8);
    assertFalse(filled().equals(timeStride));
  }

  @Test
  void equalsDistinguishesTheArrayAndOriginFields() {
    final SXYNetCDFDataProperties strideAvailable = filled();
    strideAvailable.strideAvailable = false;
    assertFalse(filled().equals(strideAvailable));

    final SXYNetCDFDataProperties decimalPlaces = filled();
    decimalPlaces.mDecimalPlaces = 5;
    assertFalse(filled().equals(decimalPlaces));

    final SXYNetCDFDataProperties exponent = filled();
    exponent.mExponent = 1;
    assertFalse(filled().equals(exponent));

    final SXYNetCDFDataProperties history = filled();
    history.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(2.5, "y", 3, 7, "var"));
    assertFalse(filled().equals(history));

    final SXYNetCDFDataProperties origin = filled();
    origin.originMap.put("x", 2);
    assertFalse(filled().equals(origin));
  }

  @Test
  void copyDeepCopiesTheMapsAndTheStrides() {
    final SXYNetCDFDataProperties original = filled();
    final SXYNetCDFDataProperties copy = (SXYNetCDFDataProperties) original.copy();
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.mStride, original.mStride);
    assertNotSame(copy.indexStride, original.indexStride);
    assertNotSame(copy.timeStride, original.timeStride);
    assertNotSame(copy.originMap, original.originMap);
  }

  @Test
  void editedDataValueListAccessorsReturnCopies() {
    final SXYNetCDFDataProperties p = filled();
    assertEquals(1, p.getEditedDataValueList().size());
    p.addEditedDataValue(new SGDataValueHistory.NetCDF.D1(2.5, "y", 3, 7, "var"));
    assertEquals(2, p.getEditedDataValueList().size());
    p.clearEditedDataValueList();
    assertTrue(p.getEditedDataValueList().isEmpty());
  }

  @Test
  void disposeClearsTheOwnedFields() {
    final SXYNetCDFDataProperties p = filled();
    p.dispose();
    assertTrue(p.isDisposed());
    assertNull(p.xName);
    assertNull(p.mStride);
    assertNull(p.mTickLabelStride);
    assertNull(p.timeName);
    assertNull(p.indexStride);
    assertNull(p.timeStride);
    assertNull(p.originMap);
  }
}
