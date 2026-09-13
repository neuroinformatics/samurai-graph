package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGStringBlock}. */
class SGStringBlockTest {

  private static SGSimpleStringBlock createBlock() {
    return new SGSimpleStringBlock(
        new String[] {"a", "b", "c"},
        new SGIntegerSeries[] {new SGIntegerSeries(0, 2, 1), new SGIntegerSeries(1, 1, 1)});
  }

  @Test
  void blockReturnsValuesAndLength() {
    SGSimpleStringBlock block = createBlock();
    assertEquals(3, block.getLength());
    assertEquals("a", block.getValue(0));
    assertEquals("b", block.getValue(1));
    String[] values = block.getValues();
    assertEquals(3, values.length);
    assertEquals("a", values[0]);
  }

  @Test
  void blockRejectsOutOfRangeAccess() {
    SGSimpleStringBlock block = createBlock();
    assertThrows(IllegalArgumentException.class, () -> block.getValue(5));
  }

  @Test
  void blockProvidesSeriesAndTextRepresentation() {
    SGSimpleStringBlock block = createBlock();
    assertEquals(0, block.getSeries(0).getMin());
    assertEquals(2, block.getSeries(0).getMax());
    assertEquals(1, block.getSeries(1).getMin());
    assertTrue(block.toString().startsWith("["));
    assertTrue(block.paramString().contains("seriesArray"));
  }

  @Test
  void blockRejectsInvalidArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGSimpleStringBlock(null, new SGIntegerSeries[0]));
    assertThrows(
        IllegalArgumentException.class,
        () ->
            new SGSimpleStringBlock(
                new String[] {"a"}, new SGIntegerSeries[] {new SGIntegerSeries(0, 2, 1)}));
    assertThrows(
        IllegalArgumentException.class, () -> new SGSimpleStringBlock(new String[] {"a"}, null));
  }
}
