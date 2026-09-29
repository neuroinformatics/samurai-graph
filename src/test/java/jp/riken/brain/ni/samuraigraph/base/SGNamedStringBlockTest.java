package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Unit tests of the named string block. */
class SGNamedStringBlockTest {

  private static Map<String, SGIntegerSeries> seriesOf(
      final int firstLength, final int secondLength) {
    // e.g. series of length 2 and 3 give a block of 6 values
    final Map<String, SGIntegerSeries> map = new HashMap<String, SGIntegerSeries>();
    map.put("first", new SGIntegerSeries(0, firstLength - 1, 1));
    map.put("second", new SGIntegerSeries(0, secondLength - 1, 1));
    return map;
  }

  private static String[] valuesOf(final int length) {
    final String[] values = new String[length];
    for (int ii = 0; ii < length; ii++) {
      values[ii] = "v" + ii;
    }
    return values;
  }

  @Test
  void constructorRejectsNullValues() {
    assertThrows(
        IllegalArgumentException.class, () -> new SGNamedStringBlock(null, seriesOf(2, 3)));
  }

  @Test
  void constructorRejectsNullSeriesMap() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGNamedStringBlock(valuesOf(6), (Map<String, SGIntegerSeries>) null));
  }

  @Test
  void constructorRejectsEmptySeriesMap() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGNamedStringBlock(valuesOf(6), new HashMap<String, SGIntegerSeries>()));
  }

  @Test
  void constructorRejectsMismatchedValueCount() {
    // the product of the series lengths (2 * 3 = 6) does not match 5 values
    assertThrows(
        IllegalArgumentException.class, () -> new SGNamedStringBlock(valuesOf(5), seriesOf(2, 3)));
  }

  @Test
  void constructorAcceptsTheMatchingValueCount() {
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), seriesOf(2, 3));
    assertEquals(6, block.getLength());
    assertEquals("v0", block.getValue(0));
    assertEquals("v5", block.getValue(5));
  }

  @Test
  void getSeriesReturnsTheSeriesForEachName() {
    final Map<String, SGIntegerSeries> map = seriesOf(2, 3);
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), map);
    assertNotNull(block.getSeries("first"));
    assertEquals(map.get("first"), block.getSeries("first"));
    assertEquals(map.get("second"), block.getSeries("second"));
    assertNull(block.getSeries("unknown"));
  }

  @Test
  void getValueRejectsOutOfRangeIndexes() {
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), seriesOf(2, 3));
    assertThrows(IllegalArgumentException.class, () -> block.getValue(-1));
    assertThrows(IllegalArgumentException.class, () -> block.getValue(6));
  }

  @Test
  void getValuesReturnsACopy() {
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), seriesOf(2, 3));
    final String[] values = block.getValues();
    assertEquals(6, values.length);
    values[0] = "changed";
    // mutating the returned array does not affect the block
    assertEquals("v0", block.getValue(0));
  }

  @Test
  void theSeriesMapIsCopiedDefensively() {
    final Map<String, SGIntegerSeries> map = seriesOf(2, 3);
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), map);
    // mutating the original map after construction does not affect the block
    map.clear();
    assertNotNull(block.getSeries("first"));
  }

  @Test
  void paramStringContainsTheSeriesMap() {
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), seriesOf(2, 3));
    assertTrue(block.paramString().contains("map="));
    assertTrue(block.paramString().contains("first="));
  }

  @Test
  void toStringIncludesTheParamString() {
    final SGNamedStringBlock block = new SGNamedStringBlock(valuesOf(6), seriesOf(2, 3));
    // toString wraps the paramString (which carries the series map) in brackets
    assertTrue(block.toString().startsWith("["));
    assertTrue(block.toString().endsWith("]"));
    assertTrue(block.toString().contains("map="));
  }
}
