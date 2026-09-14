package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGLineStyle}. */
class SGLineStyleTest {

  @Test
  void constructorStoresStyleAttributes() {
    SGLineStyle style = new SGLineStyle(SGLineConstants.LINE_TYPE_DASHED, Color.RED, 2.0f);
    assertEquals(SGLineConstants.LINE_TYPE_DASHED, style.getLineType());
    assertEquals(Color.RED, style.getColor());
    assertEquals(2.0f, style.getLineWidth());
  }

  @Test
  void settersUpdateStyle() {
    SGLineStyle style = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLACK, 1.0f);
    style.setLineType(SGLineConstants.LINE_TYPE_DOTTED);
    style.setColor(Color.BLUE);
    style.setLineWidth(2.5f);
    assertEquals(SGLineConstants.LINE_TYPE_DOTTED, style.getLineType());
    assertEquals(Color.BLUE, style.getColor());
    assertEquals(2.5f, style.getLineWidth());
  }

  @Test
  void lineWidthCanBeConvertedToUnit() {
    SGLineStyle style = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLACK, 1.0f);
    // 1 pt in cm units
    assertEquals(1.0f, style.getLineWidth("pt"), 1.0e-6f);
  }

  @Test
  void cloneIsValidCopy() {
    SGLineStyle style = new SGLineStyle(SGLineConstants.LINE_TYPE_DASHED, Color.RED, 2.0f);
    SGLineStyle copy = (SGLineStyle) style.clone();
    assertEquals(style, copy);
    style.setColor(Color.GREEN);
    assertEquals(Color.RED, copy.getColor());
  }

  @Test
  void equalsAndHashCodeAreConsistent() {
    SGLineStyle style = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLACK, 1.0f);
    SGLineStyle same = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLACK, 1.0f);
    assertEquals(style, same);
    assertEquals(style, style);
    assertEquals(style, style.clone());

    SGLineStyle otherType = new SGLineStyle(SGLineConstants.LINE_TYPE_DASHED, Color.BLACK, 1.0f);
    assertNotEquals(style, otherType);
    otherType = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLUE, 1.0f);
    assertNotEquals(style, otherType);
    otherType = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLACK, 2.0f);
    assertNotEquals(style, otherType);
  }

  @Test
  void toStringContainsTypeName() {
    SGLineStyle style = new SGLineStyle(SGLineConstants.LINE_TYPE_SOLID, Color.BLACK, 1.0f);
    String str = style.toString();
    assertTrue(str.contains("color="));
    assertTrue(str.contains("type="));
    assertTrue(str.contains("lineWidth="));
  }
}
