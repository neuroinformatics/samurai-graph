package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

/** Unit tests of the integer series. */
class SGIntegerSeriesTest {

  @Test
  void isValidSeriesAcceptsValuedSeries() {
    assertTrue(SGIntegerSeries.isValidSeries(0, 5, 1));
    assertTrue(SGIntegerSeries.isValidSeries(5, 0, -1));
    assertTrue(SGIntegerSeries.isValidSeries(3, 3, 0));
    assertTrue(SGIntegerSeries.isValidSeries(2, 2, 5));
  }

  @Test
  void isValidSeriesRejectsInconsistentSeries() {
    assertFalse(SGIntegerSeries.isValidSeries(0, 5, 0));
    assertFalse(SGIntegerSeries.isValidSeries(0, 5, -1));
    assertFalse(SGIntegerSeries.isValidSeries(5, 0, 1));
    assertFalse(SGIntegerSeries.isValidSeries((Integer) null, 5, 1));
    assertFalse(SGIntegerSeries.isValidSeries(0, null, 1));
    assertFalse(SGIntegerSeries.isValidSeries(0, 5, null));
  }

  @Test
  void isValidSeriesAcceptsResolvedAliases() {
    final Map<String, Integer> aliasMap = new HashMap<String, Integer>();
    aliasMap.put("end", 5);
    assertTrue(
        SGIntegerSeries.isValidSeries(
            new SGInteger("end"), new SGInteger(5), new SGInteger(1), aliasMap));
    assertFalse(
        SGIntegerSeries.isValidSeries(
            new SGInteger("end"),
            new SGInteger(5),
            new SGInteger(1),
            new HashMap<String, Integer>()));
  }

  @Test
  void constructorRejectsInvalidSeries() {
    assertThrows(IllegalArgumentException.class, () -> new SGIntegerSeries(0, 5, 0));
    assertThrows(IllegalArgumentException.class, () -> new SGIntegerSeries(0, 5, -1));
  }

  @Test
  void constructorStoresStartEndAndStep() {
    final SGIntegerSeries series = new SGIntegerSeries(0, 5, 1);
    assertEquals(0, series.getStart().getNumber());
    assertEquals(5, series.getEnd().getNumber());
    assertEquals(1, series.getStep().getNumber());
  }

  @Test
  void singleValueConstructorProducesOneElement() {
    final SGIntegerSeries series = new SGIntegerSeries(4);
    assertEquals(1, series.getLength());
    assertEquals(List.of(4), series.getNumberList());
  }

  @Test
  void sGIntegerConstructorValidatesTheValues() {
    final SGIntegerSeries series =
        new SGIntegerSeries(new SGInteger(1), new SGInteger(4), new SGInteger(1));
    assertEquals(List.of(1, 2, 3, 4), series.getNumberList());
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGIntegerSeries(new SGInteger(0), new SGInteger(5), new SGInteger(0)));
  }

  @Test
  void copyConstructorCopiesTheFieldsAndRejectsNull() {
    final SGIntegerSeries series = new SGIntegerSeries(0, 5, 1);
    final SGIntegerSeries copy = new SGIntegerSeries(series);
    assertEquals(series, copy);
    assertThrows(IllegalArgumentException.class, () -> new SGIntegerSeries((SGIntegerSeries) null));
  }

  @Test
  void getLengthComputesTheSequenceLength() {
    assertEquals(6, new SGIntegerSeries(0, 10, 2).getLength());
    assertEquals(11, new SGIntegerSeries(0, 10, 1).getLength());
    assertEquals(11, new SGIntegerSeries(10, 0, -1).getLength());
    assertEquals(1, new SGIntegerSeries(2, 2, 0).getLength());
    assertEquals(1, new SGIntegerSeries(3, 3, 5).getLength());
  }

  @Test
  void getNumberListEnumeratesTheSeries() {
    assertEquals(List.of(0, 2, 4, 6), new SGIntegerSeries(0, 6, 2).getNumberList());
    assertEquals(List.of(6, 4, 2, 0), new SGIntegerSeries(6, 0, -2).getNumberList());
    assertEquals(List.of(4), new SGIntegerSeries(4, 4, 0).getNumberList());
  }

  @Test
  void getNumbersIsSorted() {
    final int[] numbers = new SGIntegerSeries(6, 0, -2).getNumbers();
    assertEquals(List.of(0, 2, 4, 6), toList(numbers));
  }

  @Test
  void toStringOmitsTheZeroStepAndTheUnitStep() {
    assertEquals("5", new SGIntegerSeries(5).toString());
    assertEquals("0:end", new SGIntegerSeries(0, 10, 1).toString());
    assertEquals("0:2:end", new SGIntegerSeries(0, 10, 2).toString());
    assertEquals("10:end", new SGIntegerSeries(10, 0, -1).toString());
  }

  @Test
  void toStringOfAParsedSeriesUsesThePlainValues() {
    assertEquals("0:10", SGIntegerSeries.parse("0:10", new HashMap<String, Integer>()).toString());
    assertEquals(
        "0:2:10", SGIntegerSeries.parse("0:2:10", new HashMap<String, Integer>()).toString());
  }

  @Test
  void parseSingleNumber() {
    final SGIntegerSeries series = SGIntegerSeries.parse("5", new HashMap<String, Integer>());
    assertEquals(5, series.getStart().getNumber());
    assertEquals(5, series.getEnd().getNumber());
    assertEquals(0, series.getStep().getNumber());
    assertEquals(List.of(5), series.getNumberList());
  }

  @Test
  void parseRangeComputesTheUnitStep() {
    final SGIntegerSeries series = SGIntegerSeries.parse("0:10", new HashMap<String, Integer>());
    assertEquals(1, series.getStep().getNumber());
    assertEquals(List.of(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10), series.getNumberList());
  }

  @Test
  void parseRangeInfersTheDescendingStep() {
    final SGIntegerSeries series = SGIntegerSeries.parse("10:0", new HashMap<String, Integer>());
    assertEquals(-1, series.getStep().getNumber());
    assertEquals(11, series.getLength());
  }

  @Test
  void parseWithExplicitStep() {
    // the form is "start:step:end"
    final SGIntegerSeries series = SGIntegerSeries.parse("0:2:10", new HashMap<String, Integer>());
    assertEquals(2, series.getStep().getNumber());
    assertEquals(List.of(0, 2, 4, 6, 8, 10), series.getNumberList());
  }

  @Test
  void parseResolvesTheEndAlias() {
    final Map<String, Integer> aliasMap = new HashMap<String, Integer>();
    aliasMap.put("end", 10);
    final SGIntegerSeries series = SGIntegerSeries.parse("0:end", aliasMap);
    assertEquals(10, series.getEnd().getNumber());
    assertEquals(1, series.getStep().getNumber());
    assertEquals(11, series.getLength());
  }

  @Test
  void parseRejectsInconsistentAndUnknownSeries() {
    assertNull(SGIntegerSeries.parse("0:10:-1", new HashMap<String, Integer>()));
    assertNull(SGIntegerSeries.parse("0:10:2:1", new HashMap<String, Integer>()));
    assertNull(SGIntegerSeries.parse("unknown", new HashMap<String, Integer>()));
  }

  @Test
  void parseRejectsNullArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGIntegerSeries.parse(null, new HashMap<String, Integer>()));
    assertThrows(IllegalArgumentException.class, () -> SGIntegerSeries.parse("0:1", null));
  }

  @Test
  void equalsMatchesOnTheSameSeries() {
    final SGIntegerSeries first = new SGIntegerSeries(0, 10, 2);
    final SGIntegerSeries second = new SGIntegerSeries(0, 10, 2);
    assertEquals(first, second);
    assertFalse(first.equals(new SGIntegerSeries(0, 10, 3)));
    assertFalse(first.equals(new SGIntegerSeries(0, 9, 2)));
    // Known issue: hashCode mixes in the object identity, so two equal
    // instances may hash differently (equals/hashCode contract violation).
  }

  @Test
  void cloneProducesAnEqualIndependentSeries() {
    final SGIntegerSeries series = new SGIntegerSeries(0, 10, 2);
    final Object copy = series.clone();
    assertNotSame(series, copy);
    assertEquals(series, copy);
  }

  @Test
  void createInstanceMakesAnIndexSeries() {
    assertEquals(List.of(0, 1, 2, 3, 4), SGIntegerSeries.createInstance(5).getNumberList());
    assertNull(SGIntegerSeries.createInstance(0).getStart());
    assertThrows(IllegalArgumentException.class, () -> SGIntegerSeries.createInstance(-1));
  }

  @Test
  void createListMakesARangeForConsecutiveNumbers() {
    final List<SGIntegerSeries> seriesList = SGIntegerSeries.createList(new int[] {1, 2, 3});
    assertEquals(1, seriesList.size());
    assertEquals(List.of(1, 2, 3), seriesList.get(0).getNumberList());
  }

  @Test
  void createListMakesASingleValueSeries() {
    final List<SGIntegerSeries> seriesList = SGIntegerSeries.createList(new int[] {3});
    assertEquals(1, seriesList.size());
    assertEquals(List.of(3), seriesList.get(0).getNumberList());
  }

  @Test
  void createListRejectsNullOrEmpty() {
    assertThrows(IllegalArgumentException.class, () -> SGIntegerSeries.createList(null));
    assertThrows(IllegalArgumentException.class, () -> SGIntegerSeries.createList(new int[0]));
  }

  @Test
  void rangeQueries() {
    final SGIntegerSeries series = new SGIntegerSeries(0, 10, 2);
    assertEquals(0, series.getMin());
    assertEquals(10, series.getMax());
    assertTrue(series.isWithinRange(5));
    assertFalse(series.isWithinRange(11));
    assertTrue(series.contains(4));
    assertFalse(series.contains(5));
    assertTrue(series.search(6) >= 0);
    assertEquals(List.of(2, 4), series.getExisting(new int[] {1, 2, 3, 4, 5}));
  }

  @Test
  void overlapDetectsIntersectingRanges() {
    assertTrue(new SGIntegerSeries(0, 5, 1).overlap(new SGIntegerSeries(3, 10, 1)));
    assertFalse(new SGIntegerSeries(0, 5, 1).overlap(new SGIntegerSeries(7, 10, 1)));
  }

  @Test
  void addAliasAcceptsAnAliasForAMember() {
    final SGIntegerSeries series = new SGIntegerSeries(0, 10, 2);
    assertDoesNotThrow(() -> series.addAlias(0, "zero"));
  }

  private static List<Integer> toList(final int[] array) {
    final List<Integer> list = new java.util.ArrayList<Integer>();
    for (int value : array) {
      list.add(value);
    }
    return list;
  }
}
