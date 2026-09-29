package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager.MultipleColorMap.MultipleColorMapProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the multiple color map properties. */
class SGColorMapManagerMultiplePropertiesTest {

  @Test
  void equalsMatchesTheSameFields() {
    final MultipleColorMapProperties first = new MultipleColorMapProperties();
    first.colorMapProperties = new ColorMapProperties[] {new ColorMapProperties()};
    final MultipleColorMapProperties second = new MultipleColorMapProperties();
    second.colorMapProperties = new ColorMapProperties[] {new ColorMapProperties()};
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsDistinguishesTheColorMapProperties() {
    final MultipleColorMapProperties present = new MultipleColorMapProperties();
    present.colorMapProperties = new ColorMapProperties[] {new ColorMapProperties()};
    final MultipleColorMapProperties absent = new MultipleColorMapProperties();
    assertNotEquals(present, absent);
    final MultipleColorMapProperties longer = new MultipleColorMapProperties();
    longer.colorMapProperties =
        new ColorMapProperties[] {new ColorMapProperties(), new ColorMapProperties()};
    assertNotEquals(present, longer);
  }

  @Test
  void equalPropertiesHashEqually() {
    final MultipleColorMapProperties first = new MultipleColorMapProperties();
    first.colorMapProperties = new ColorMapProperties[] {new ColorMapProperties()};
    final MultipleColorMapProperties second = new MultipleColorMapProperties();
    second.colorMapProperties = new ColorMapProperties[] {new ColorMapProperties()};
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void copyClonesTheColorMapProperties() {
    final MultipleColorMapProperties original = new MultipleColorMapProperties();
    original.colorMapProperties = new ColorMapProperties[] {new ColorMapProperties()};
    final MultipleColorMapProperties copy = (MultipleColorMapProperties) original.copy();
    assertNotNull(copy);
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.colorMapProperties, original.colorMapProperties);
    assertNotSame(copy.colorMapProperties[0], original.colorMapProperties[0]);
  }

  @Test
  void copyKeepsNullColorMapProperties() {
    final MultipleColorMapProperties original = new MultipleColorMapProperties();
    final MultipleColorMapProperties copy = (MultipleColorMapProperties) original.copy();
    assertNotNull(copy);
    assertNull(copy.colorMapProperties);
    assertEquals(original, copy);
  }
}
