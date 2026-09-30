package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

/** Unit tests of the SXY multiple sdarray data properties. */
class SGSXYSDArrayMultipleDataPropertiesTest {

  private static final class Properties extends SGSXYSDArrayMultipleData.SXYMultipleDataProperties {
    public void set(
        Integer[] x,
        Integer[] y,
        Integer[] l,
        Integer[] u,
        Integer[] eh,
        Integer[] t,
        Integer[] th,
        SGIntegerSeriesSet tickLabelStride) {
      this.mXIndices = x;
      this.mYIndices = y;
      this.mLowerErrorIndices = l;
      this.mUpperErrorIndices = u;
      this.mErrorBarHolderIndices = eh;
      this.mTickLabelIndices = t;
      this.mTickLabelHolderIndices = th;
      this.mTickLabelStride = tickLabelStride;
    }
  }

  private static Properties filled() {
    final Properties p = new Properties();
    p.set(
        new Integer[] {0},
        new Integer[] {1},
        new Integer[] {2},
        new Integer[] {3},
        new Integer[] {4},
        new Integer[] {5},
        new Integer[] {6},
        SGIntegerSeriesSet.createInstance(3));
    p.mStride = SGIntegerSeriesSet.createInstance(5);
    p.strideAvailable = true;
    p.mDecimalPlaces = 2;
    p.mExponent = 3;
    p.editedDataValueList.add(new SGDataValueHistory.SDArray.D1(1.5, "y", 3, 2.5));
    return p;
  }

  @Test
  void defaultsAreNull() {
    final SGSXYSDArrayMultipleData.SXYMultipleDataProperties p =
        new SGSXYSDArrayMultipleData.SXYMultipleDataProperties();
    assertNull(p.mXIndices);
    assertNull(p.mYIndices);
    assertNull(p.mLowerErrorIndices);
    assertNull(p.mUpperErrorIndices);
    assertNull(p.mErrorBarHolderIndices);
    assertNull(p.mTickLabelIndices);
    assertNull(p.mTickLabelHolderIndices);
    assertNull(p.mTickLabelStride);
  }

  @Test
  void equalsMatchesIdenticalProperties() {
    assertTrue(filled().equals(filled()));
    assertFalse(filled().equals(new Object()));
    // a sibling property type is not equal
    assertFalse(filled().equals(new SGSXYMDArrayMultipleData.SXYMDArrayMultipleDataProperties()));
  }

  @Test
  void equalsDistinguishesTheColumnTypes() {
    final Properties a = filled();
    final Properties b = filled();
    a.mXIndices = new Integer[] {9};
    assertFalse(a.equals(b));
    a.mXIndices = new Integer[] {0};
    assertTrue(a.equals(b));

    a.mTickLabelHolderIndices = new Integer[] {8};
    assertFalse(a.equals(b));
  }

  @Test
  void equalsDistinguishesTheSizes() {
    final Properties a = filled();
    final Properties b = filled();
    b.mTickLabelStride = SGIntegerSeriesSet.createInstance(4);
    assertFalse(a.equals(b));

    final Properties c = filled();
    c.mStride = SGIntegerSeriesSet.createInstance(9);
    assertFalse(c.equals(filled()));
  }

  @Test
  void copyClonesTheIndexArraysAndTheStride() {
    final Properties original = filled();
    final SGSXYSDArrayMultipleData.SXYMultipleDataProperties copy =
        (SGSXYSDArrayMultipleData.SXYMultipleDataProperties) original.copy();
    assertNotSame(copy, original);
    // the index arrays are cloned
    assertNotSame(copy.mXIndices, original.mXIndices);
    assertNotSame(copy.mTickLabelHolderIndices, original.mTickLabelHolderIndices);
    assertArrayEquals(original.mXIndices, copy.mXIndices);
    // the tick label stride is cloned
    assertNotSame(copy.mTickLabelStride, original.mTickLabelStride);
    assertTrue(original.equals(copy));
  }

  @Test
  void copyKeepsNullArraysNull() {
    final Properties original = new Properties();
    final SGSXYSDArrayMultipleData.SXYMultipleDataProperties copy =
        (SGSXYSDArrayMultipleData.SXYMultipleDataProperties) original.copy();
    assertNull(copy.mXIndices);
    assertNull(copy.mTickLabelStride);
    assertTrue(original.equals(copy));
  }

  @Test
  void disposeNullsTheFields() {
    final Properties p = filled();
    p.dispose();
    assertNull(p.mXIndices);
    assertNull(p.mYIndices);
    assertNull(p.mLowerErrorIndices);
    assertNull(p.mUpperErrorIndices);
    assertNull(p.mErrorBarHolderIndices);
    assertNull(p.mTickLabelIndices);
    assertNull(p.mTickLabelHolderIndices);
    assertNull(p.mTickLabelStride);
    // the base stride is cleared too
    assertNull(p.mStride);
  }
}
