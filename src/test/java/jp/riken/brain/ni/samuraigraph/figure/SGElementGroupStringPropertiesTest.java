package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroupString.StringProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the string element group properties. */
class SGElementGroupStringPropertiesTest {

  private static StringProperties create(
      final String fontName, final float size, final int decimalPlaces, final String dateFormat) {
    final StringProperties p = new StringProperties();
    p.setFontName(fontName);
    p.setFontSize(size);
    p.setFontStyle(0);
    p.setColor(Color.BLACK);
    p.decimalPlaces = decimalPlaces;
    p.exponent = 0;
    p.dateFormat = dateFormat;
    return p;
  }

  @Test
  void defaultsAreEmptyAndTheInnerPropertiesAreCreated() {
    final StringProperties p = new StringProperties();
    assertFalse(p.isVisible());
    assertEquals(0, p.decimalPlaces);
    assertEquals(0, p.exponent);
    assertNull(p.dateFormat);
    assertNotNull(p.stringProperties);
    assertNull(p.stringProperties.getFontName());
  }

  @Test
  void delegatedGettersReadTheInnerProperties() {
    final StringProperties p = new StringProperties();
    p.setFontName("Dialog");
    p.setFontSize(12.0f);
    p.setFontStyle(1);
    p.setAngle(45.0f);
    p.setColor(Color.RED);
    assertEquals("Dialog", p.getFontName());
    assertEquals(12.0f, p.getFontSize());
    assertEquals(1, p.getFontStyle());
    assertEquals(45.0f, p.getAngle());
    assertEquals(Color.RED, p.getColor());
  }

  @Test
  void equalsMatchesTheSameFields() {
    final StringProperties first = create("Dialog", 12.0f, 3, "yyyy-MM-dd");
    first.setVisible(true);
    final StringProperties second = create("Dialog", 12.0f, 3, "yyyy-MM-dd");
    second.setVisible(true);
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsMatchesNullDateFormat() {
    final StringProperties first = create("Dialog", 12.0f, 0, null);
    final StringProperties second = create("Dialog", 12.0f, 0, null);
    assertEquals(first, second);
  }

  @Test
  void equalsDistinguishesTheFields() {
    final StringProperties base = create("Dialog", 12.0f, 3, "yyyy-MM-dd");
    final StringProperties diffFont = create("Courier", 12.0f, 3, "yyyy-MM-dd");
    final StringProperties diffSize = create("Dialog", 13.0f, 3, "yyyy-MM-dd");
    final StringProperties diffDecimals = create("Dialog", 12.0f, 4, "yyyy-MM-dd");
    final StringProperties diffExponent = create("Dialog", 12.0f, 3, "yyyy-MM-dd");
    diffExponent.exponent = 2;
    final StringProperties diffFormat = create("Dialog", 12.0f, 3, "dd/MM/yyyy");
    final StringProperties diffVisible = create("Dialog", 12.0f, 3, "yyyy-MM-dd");
    diffVisible.setVisible(true);
    assertNotEquals(base, diffFont);
    assertNotEquals(base, diffSize);
    assertNotEquals(base, diffDecimals);
    assertNotEquals(base, diffExponent);
    assertNotEquals(base, diffFormat);
    assertNotEquals(base, diffVisible);
  }

  @Test
  void copyClonesTheInnerProperties() {
    final StringProperties original = create("Dialog", 12.0f, 3, "yyyy-MM-dd");
    final StringProperties copy = (StringProperties) original.copy();
    assertNotNull(copy);
    assertNotSame(copy, original);
    assertEquals(original, copy);
    assertNotSame(copy.stringProperties, original.stringProperties);
    assertEquals(3, copy.decimalPlaces);
    assertEquals(0, copy.exponent);
    assertEquals("yyyy-MM-dd", copy.dateFormat);
    // the copy holds its own inner properties
    copy.setFontSize(13.0f);
    assertNotEquals(original, copy);
  }

  @Test
  void disposeDisposesTheOuterButTheInnerDisposeIsANoOp() {
    final StringProperties p = new StringProperties();
    assertFalse(p.isDisposed());
    p.dispose();
    assertTrue(p.isDisposed());
    // the inner DrawingElementProperties.dispose() is a no-op
    assertFalse(p.stringProperties.isDisposed());
  }
}
