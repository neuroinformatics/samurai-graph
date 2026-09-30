package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/** Unit tests of the drawing-element string properties. */
class SGDrawingElementStringPropertiesTest {

  private static SGDrawingElementString.StringProperties create(
      final String fontName,
      final float size,
      final int style,
      final float angle,
      final Color color) {
    final SGDrawingElementString.StringProperties p = new SGDrawingElementString.StringProperties();
    p.setFontName(fontName);
    p.setFontSize(size);
    p.setFontStyle(style);
    p.setAngle(angle);
    p.setColor(color);
    return p;
  }

  @Test
  void settersAndGettersRoundTripTheFields() {
    final SGDrawingElementString.StringProperties p = new SGDrawingElementString.StringProperties();
    p.setFontName("Dialog");
    p.setFontSize(12.5f);
    p.setFontStyle(1);
    p.setAngle(30.0f);
    p.setColor(Color.RED);
    assertEquals("Dialog", p.getFontName());
    assertEquals(12.5f, p.getFontSize());
    assertEquals(1, p.getFontStyle());
    assertEquals(30.0f, p.getAngle());
    assertEquals(Color.RED, p.getColor());
  }

  @Test
  void defaultsHaveNullFontNameAndColor() {
    final SGDrawingElementString.StringProperties p = new SGDrawingElementString.StringProperties();
    assertNull(p.getFontName());
    assertNull(p.getColor());
  }

  @Test
  void equalsMatchesTheSameFields() {
    final SGDrawingElementString.StringProperties first =
        create("Dialog", 12.0f, 1, 30.0f, Color.RED);
    final SGDrawingElementString.StringProperties second =
        create("Dialog", 12.0f, 1, 30.0f, Color.RED);
    assertEquals(first, second);
    assertFalse(first.equals(new Object()));
  }

  @Test
  void equalsMatchesNullFontNameWithoutThrowing() {
    // the font name defaults to null; equals must not dereference it
    final SGDrawingElementString.StringProperties first =
        new SGDrawingElementString.StringProperties();
    first.setFontSize(12.0f);
    first.setColor(Color.RED);
    final SGDrawingElementString.StringProperties second =
        new SGDrawingElementString.StringProperties();
    second.setFontSize(12.0f);
    second.setColor(Color.RED);
    assertEquals(first, second);
  }

  @Test
  void equalsDistinguishesTheFields() {
    final SGDrawingElementString.StringProperties base =
        create("Dialog", 12.0f, 1, 30.0f, Color.RED);
    assertNotEquals(base, create("Courier", 12.0f, 1, 30.0f, Color.RED));
    assertNotEquals(base, create("Dialog", 13.0f, 1, 30.0f, Color.RED));
    assertNotEquals(base, create("Dialog", 12.0f, 2, 30.0f, Color.RED));
    assertNotEquals(base, create("Dialog", 12.0f, 1, 45.0f, Color.RED));
    assertNotEquals(base, create("Dialog", 12.0f, 1, 30.0f, Color.BLUE));
    assertNotEquals(base, create(null, 12.0f, 1, 30.0f, Color.RED));
  }

  @Test
  void copyProducesAnEqualIndependentInstance() {
    final SGDrawingElementString.StringProperties original =
        create("Dialog", 12.0f, 1, 30.0f, Color.RED);
    final SGDrawingElementString.StringProperties copy =
        (SGDrawingElementString.StringProperties) original.copy();
    assertNotNull(copy);
    assertNotSame(copy, original);
    assertEquals(original, copy);
  }

  @Test
  void disposeIsANoOpForTheDrawingElementStringProperties() {
    // DrawingElementProperties.dispose() is an empty override, so the
    // disposed flag is never set
    final SGDrawingElementString.StringProperties p = new SGDrawingElementString.StringProperties();
    assertFalse(p.isDisposed());
    p.dispose();
    assertFalse(p.isDisposed());
  }
}
