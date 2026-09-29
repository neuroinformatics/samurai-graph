package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the color map properties. */
class SGColorMapColorMapPropertiesTest {

  private static ColorMapProperties create(final Color[] colors, final boolean reversed) {
    final ColorMapProperties p = new ColorMapProperties();
    p.colors = colors;
    p.reversedOrder = reversed;
    return p;
  }

  @Test
  void equalsMatchesTheSameColorsAndOrder() {
    final Color[] colors = {Color.RED, Color.BLUE};
    assertEquals(create(colors, false), create(colors.clone(), false));
    assertEquals(create(colors, true), create(colors.clone(), true));
    // null colors on both sides compare equal
    assertEquals(create(null, false), create(null, false));
    assertFalse(create(colors, false).equals(new Object()));
  }

  @Test
  void equalsDistinguishesTheColors() {
    final Color[] colors = {Color.RED, Color.BLUE};
    final Color[] otherColors = {Color.RED, Color.GREEN};
    final Color[] reversedArray = {Color.BLUE, Color.RED};
    assertNotEquals(create(colors, false), create(otherColors, false));
    assertNotEquals(create(colors, false), create(reversedArray, false));
    assertNotEquals(create(colors, false), create(new Color[] {Color.RED}, false));
    assertNotEquals(create(colors, false), create(null, false));
  }

  @Test
  void equalsDistinguishesTheReversedOrder() {
    final Color[] colors = {Color.RED, Color.BLUE};
    assertNotEquals(create(colors, false), create(colors.clone(), true));
  }

  @Test
  void equalPropertiesHashEqually() {
    final Color[] colors = {Color.RED, Color.BLUE};
    final ColorMapProperties first = create(colors, true);
    final ColorMapProperties second = create(colors.clone(), true);
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }

  @Test
  void copyClonesTheColors() {
    final Color[] colors = {Color.RED, Color.BLUE};
    final ColorMapProperties original = create(colors, true);
    final ColorMapProperties copy = (ColorMapProperties) original.copy();
    assertNotNull(copy);
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertTrue(copy.isReversedOrder());
    // the copy holds its own color array
    copy.colors[0] = Color.GREEN;
    assertNotEquals(original, copy);
  }

  @Test
  void copyKeepsNullColors() {
    final ColorMapProperties original = create(null, false);
    final ColorMapProperties copy = (ColorMapProperties) original.copy();
    assertNotNull(copy);
    assertNull(copy.colors);
    assertEquals(original, copy);
  }

  @Test
  void isReversedOrderReturnsTheFlag() {
    assertFalse(new ColorMapProperties().isReversedOrder());
    final ColorMapProperties p = create(new Color[] {Color.RED}, true);
    assertTrue(p.isReversedOrder());
  }

  @Test
  void disposeMarksThePropertiesDisposed() {
    final ColorMapProperties p = new ColorMapProperties();
    assertFalse(p.isDisposed());
    p.dispose();
    assertTrue(p.isDisposed());
  }
}
