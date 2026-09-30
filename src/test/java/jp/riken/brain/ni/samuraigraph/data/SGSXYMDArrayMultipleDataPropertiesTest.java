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
import org.junit.jupiter.api.Test;

/** Unit tests of the SXY multiple mdarray data properties. */
class SGSXYMDArrayMultipleDataPropertiesTest {

  private static final class Properties
      extends SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties {
    public void set(
        String[] x,
        String[] y,
        String[] t,
        String[] th,
        String[] l,
        String[] u,
        String[] eh,
        Object pickUp,
        SGIntegerSeriesSet stride,
        SGIntegerSeriesSet tickLabelStride) {
      this.xNames = x;
      this.yNames = y;
      this.tNames = t;
      this.thNames = th;
      this.lNames = l;
      this.uNames = u;
      this.ehNames = eh;
      this.mPickUpInfo = (SGMDArrayPickUpDimensionInfo) pickUp;
      this.mStride = stride;
      this.mTickLabelStride = tickLabelStride;
    }

    public void fillMaps() {
      final Map<String, Integer> dimMap = new HashMap<String, Integer>();
      dimMap.put("x", 0);
      dimMap.put("y", 1);
      this.dimensionIndexMap.put("var", dimMap);
      this.originMap.put("x", new int[] {0, 1});
      this.timeStride = SGIntegerSeriesSet.createInstance(7);
      this.strideAvailable = true;
      this.mDecimalPlaces = 2;
      this.mExponent = 3;
      this.editedDataValueList.add(new SGDataValueHistory.NetCDF.D1(1.5, "y", 3, 7, "var"));
    }

    public void baseEqualsFill() {
      this.dimensionIndexMap.clear();
      this.originMap.clear();
      this.timeStride = null;
      this.strideAvailable = false;
      this.mDecimalPlaces = 0;
      this.mExponent = 0;
      this.editedDataValueList.clear();
    }
  }

  private static Properties filled() {
    final Properties p = new Properties();
    p.set(
        new String[] {"x"},
        new String[] {"y"},
        new String[] {"t"},
        new String[] {"th"},
        new String[] {"l"},
        new String[] {"u"},
        new String[] {"eh"},
        new SGMDArrayPickUpDimensionInfo(),
        SGIntegerSeriesSet.createInstance(5),
        SGIntegerSeriesSet.createInstance(3));
    p.fillMaps();
    return p;
  }

  private static Properties baseEqual() {
    final Properties p = new Properties();
    p.set(
        new String[] {"x"},
        new String[] {"y"},
        new String[] {"t"},
        new String[] {"th"},
        new String[] {"l"},
        new String[] {"u"},
        new String[] {"eh"},
        new SGMDArrayPickUpDimensionInfo(),
        SGIntegerSeriesSet.createInstance(5),
        SGIntegerSeriesSet.createInstance(3));
    p.baseEqualsFill();
    return p;
  }

  @Test
  void defaultsAreZeroAndNull() {
    final SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties p =
        new SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties();
    assertNull(p.xNames);
    assertNull(p.mPickUpInfo);
    assertNull(p.mStride);
    assertNull(p.mTickLabelStride);
  }

  @Test
  void equalsMatchesIdenticalProperties() {
    assertTrue(filled().equals(filled()));
    assertFalse(filled().equals(new Object()));
  }

  @Test
  void equalsDistinguishesTheColumnTypes() {
    final Properties a = filled();
    final Properties b = filled();
    a.xNames = new String[] {"x2"};
    assertFalse(a.equals(b));
    a.xNames = new String[] {"x"};
    assertTrue(a.equals(b));

    a.thNames = new String[] {"th2"};
    assertFalse(a.equals(b));
  }

  @Test
  void equalsDistinguishesTheSizes() {
    final Properties a = filled();
    final Properties b = baseEqual();
    // the same name arrays but a different base size
    assertFalse(a.equals(b));

    final Properties c = filled();
    c.mStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(c.equals(filled()));

    final Properties d = filled();
    d.mPickUpInfo = null;
    assertFalse(d.equals(filled()));
  }

  @Test
  void copyClonesTheStridesAndSharesTheArrays() {
    final Properties original = filled();
    final SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties copy =
        (SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties) original.copy();
    assertNotSame(copy, original);
    // the stride sets are cloned
    assertNotSame(copy.mStride, original.mStride);
    assertNotSame(copy.mTickLabelStride, original.mTickLabelStride);
    // the name arrays are shared
    assertEquals(original.xNames, copy.xNames);
    assertTrue(original.equals(copy));
  }

  @Test
  void copyClonesNullStridesAsNull() {
    final Properties original = filled();
    original.mStride = null;
    original.mTickLabelStride = null;
    final SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties copy =
        (SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties) original.copy();
    assertNull(copy.mStride);
    assertNull(copy.mTickLabelStride);
    assertTrue(original.equals(copy));
  }

  @Test
  void disposeNullsTheFields() {
    final Properties p = filled();
    p.dispose();
    assertNull(p.xNames);
    assertNull(p.yNames);
    assertNull(p.lNames);
    assertNull(p.uNames);
    assertNull(p.ehNames);
    assertNull(p.tNames);
    assertNull(p.thNames);
    assertNull(p.mPickUpInfo);
    assertNull(p.mStride);
    assertNull(p.mTickLabelStride);
    // the base fields are cleared too
    assertTrue(p.dimensionIndexMap.isEmpty());
    assertTrue(p.originMap.isEmpty());
    assertNull(p.timeStride);
  }

  @Test
  void hashCodeIsStableForTheSameInstance() {
    final Properties p = filled();
    assertEquals(p.hashCode(), p.hashCode());
  }
}
