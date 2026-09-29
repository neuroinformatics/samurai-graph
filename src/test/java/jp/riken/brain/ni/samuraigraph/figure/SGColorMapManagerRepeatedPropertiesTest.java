package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager.RepeatedColorMap.RepeatedColorMapProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the repeated color map properties. */
class SGColorMapManagerRepeatedPropertiesTest {

  @Test
  void equalsMatchesTheSameFields() {
    final RepeatedColorMapProperties first = new RepeatedColorMapProperties();
    first.colorMapProperties = new ColorMapProperties();
    first.repeatedNum = 3;
    final RepeatedColorMapProperties second = new RepeatedColorMapProperties();
    second.colorMapProperties = new ColorMapProperties();
    second.repeatedNum = 3;
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsDistinguishesTheRepeatedNum() {
    final RepeatedColorMapProperties first = new RepeatedColorMapProperties();
    first.colorMapProperties = new ColorMapProperties();
    first.repeatedNum = 3;
    final RepeatedColorMapProperties second = new RepeatedColorMapProperties();
    second.colorMapProperties = new ColorMapProperties();
    second.repeatedNum = 4;
    assertNotEquals(first, second);
  }

  @Test
  void equalsDistinguishesTheColorMapProperties() {
    final RepeatedColorMapProperties present = new RepeatedColorMapProperties();
    present.colorMapProperties = new ColorMapProperties();
    present.repeatedNum = 3;
    final RepeatedColorMapProperties absent = new RepeatedColorMapProperties();
    absent.repeatedNum = 3;
    assertNotEquals(present, absent);
  }

  @Test
  void equalPropertiesHashEqually() {
    final RepeatedColorMapProperties first = new RepeatedColorMapProperties();
    first.colorMapProperties = new ColorMapProperties();
    first.repeatedNum = 3;
    final RepeatedColorMapProperties second = new RepeatedColorMapProperties();
    second.colorMapProperties = new ColorMapProperties();
    second.repeatedNum = 3;
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void copyClonesTheColorMapProperties() {
    final RepeatedColorMapProperties original = new RepeatedColorMapProperties();
    original.colorMapProperties = new ColorMapProperties();
    original.repeatedNum = 3;
    final RepeatedColorMapProperties copy = (RepeatedColorMapProperties) original.copy();
    assertNotNull(copy);
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertEquals(3, copy.repeatedNum);
    assertNotSame(copy.colorMapProperties, original.colorMapProperties);
  }

  @Test
  void copyKeepsNullColorMapProperties() {
    final RepeatedColorMapProperties original = new RepeatedColorMapProperties();
    final RepeatedColorMapProperties copy = (RepeatedColorMapProperties) original.copy();
    assertNotNull(copy);
    assertNull(copy.colorMapProperties);
    assertEquals(original, copy);
  }

  @Test
  void defaultRepeatedNumIsZero() {
    final RepeatedColorMapProperties p = new RepeatedColorMapProperties();
    assertEquals(0, p.repeatedNum);
    assertNull(p.colorMapProperties);
  }
}
