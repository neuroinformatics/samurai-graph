package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGIntegerSeriesSet}. */
class SGIntegerSeriesSetTest {

  @Test
  void parseListOfNumbers() {
    SGIntegerSeriesSet set = SGIntegerSeriesSet.parse("1,3,5", new HashMap<String, Integer>());
    assertNotNull(set);
    assertArrayEquals(new int[] {1, 3, 5}, set.getNumbers());
    assertEquals(3, set.getLength());
  }

  @Test
  void parseSeriesWithExplicitStepUsesStartStepEndOrder() {
    SGIntegerSeriesSet set = SGIntegerSeriesSet.parse("1:2:5", new HashMap<String, Integer>());
    assertNotNull(set);
    assertArrayEquals(new int[] {1, 3, 5}, set.getNumbers());
  }

  @Test
  void parseSeriesWithInferredStep() {
    SGIntegerSeriesSet set = SGIntegerSeriesSet.parse("1:3", new HashMap<String, Integer>());
    assertNotNull(set);
    assertArrayEquals(new int[] {1, 2, 3}, set.getNumbers());
  }

  @Test
  void parseWithEndAliasIsComplete() {
    SGIntegerSeriesSet set = SGIntegerSeriesSet.parse("0:end", 5);
    assertNotNull(set);
    assertArrayEquals(new int[] {0, 1, 2, 3, 4}, set.getNumbers());
    assertTrue(set.isComplete());
    assertEquals(Integer.valueOf(4), set.getEndIndex());
  }

  @Test
  void singleEndAliasKeepsEndIndex() {
    SGIntegerSeriesSet set = SGIntegerSeriesSet.parse("end", 5);
    assertNotNull(set);
    assertArrayEquals(new int[] {4}, set.getNumbers());
    assertEquals(Integer.valueOf(4), set.getEndIndex());
  }

  @Test
  void parseRejectsIndexOutOfRange() {
    assertNull(SGIntegerSeriesSet.parse("6", 5));
  }

  @Test
  void parseRejectsInvalidToken() {
    assertNull(SGIntegerSeriesSet.parse("a", new HashMap<String, Integer>()));
  }

  @Test
  void parseRejectsNullArguments() {
    assertThrows(
        IllegalArgumentException.class,
        () -> SGIntegerSeriesSet.parse(null, new HashMap<String, Integer>()));
  }

  @Test
  void constructorWithStartEndStep() {
    SGIntegerSeriesSet set = new SGIntegerSeriesSet(0, 2, 1);
    assertArrayEquals(new int[] {0, 1, 2}, set.getNumbers());
  }

  @Test
  void addAppendsNumber() {
    SGIntegerSeriesSet set = new SGIntegerSeriesSet();
    set.add(5);
    assertArrayEquals(new int[] {5}, set.getNumbers());
    assertTrue(set.contains(5));
    assertFalse(set.contains(9));
  }

  @Test
  void isValidInputChecksSeriesConsistency() {
    assertTrue(SGIntegerSeriesSet.isValidInput(0, 3, 1));
    assertFalse(SGIntegerSeriesSet.isValidInput(3, 0, 1));
    assertFalse(SGIntegerSeriesSet.isValidInput(0, 3, 0));
  }

  @Test
  void equalsComparesSeriesLists() {
    SGIntegerSeriesSet a = SGIntegerSeriesSet.parse("1,2", new HashMap<String, Integer>());
    SGIntegerSeriesSet b = SGIntegerSeriesSet.parse("1,2", new HashMap<String, Integer>());
    SGIntegerSeriesSet c = SGIntegerSeriesSet.parse("1,3", new HashMap<String, Integer>());
    assertEquals(a, b);
    assertFalse(a.equals(c));
  }
}
