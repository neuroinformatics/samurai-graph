package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGGradationPaint}. */
class SGGradationPaintTest {

  @Test
  void defaultPaintHasHorizontalOrder() {
    SGGradationPaint paint = new SGGradationPaint();
    assertEquals(SGGradationPaint.INDEX_DIRECTION_HORIZONTAL, paint.getDirectionIndex());
    assertEquals(SGGradationPaint.INDEX_ORDER_COLOR_1_2, paint.getOrderIndex());
    assertFalse(paint.isDisposed());
  }

  @Test
  void settersUpdateDirectionOrderAndColors() {
    SGGradationPaint paint = new SGGradationPaint();
    assertTrue(paint.setDirection(SGGradationPaint.INDEX_DIRECTION_VERTICAL));
    assertEquals(SGGradationPaint.INDEX_DIRECTION_VERTICAL, paint.getDirectionIndex());
    assertTrue(paint.setOrder(SGGradationPaint.INDEX_ORDER_COLOR_2_1));
    assertEquals(SGGradationPaint.INDEX_ORDER_COLOR_2_1, paint.getOrderIndex());
    assertTrue(paint.setColor1(Color.RED));
    assertTrue(paint.setColor2(Color.BLUE));
    Color[] colors = paint.getColors();
    assertEquals(Color.RED, colors[0]);
    assertEquals(Color.BLUE, colors[1]);
    assertTrue(paint.setColors(new Color[] {Color.GREEN, Color.YELLOW}));
    assertEquals(Color.GREEN, paint.getColors()[0]);
  }

  @Test
  void staticHelpersResolveDirectionAndOrder() {
    assertTrue(SGGradationPaint.isValidDirection(SGGradationPaint.INDEX_DIRECTION_HORIZONTAL));
    assertFalse(SGGradationPaint.isValidDirection(99));
    assertTrue(SGGradationPaint.isValidOrder(SGGradationPaint.INDEX_ORDER_COLOR_1_2));
    assertFalse(SGGradationPaint.isValidOrder(99));
    assertEquals(
        SGGradationPaint.NAME_DIRECTION_VERTICAL,
        SGGradationPaint.getDirectionName(SGGradationPaint.INDEX_DIRECTION_VERTICAL));
    assertEquals(SGGradationPaint.NAME_UNKNOWN, SGGradationPaint.getDirectionName(99));
    assertEquals(
        SGGradationPaint.NAME_ORDER_COLOR_1_2_1,
        SGGradationPaint.getOrderName(SGGradationPaint.INDEX_ORDER_COLOR_1_2_1));
    assertEquals(SGGradationPaint.NAME_UNKNOWN, SGGradationPaint.getOrderName(99));
    assertEquals(
        SGGradationPaint.INDEX_DIRECTION_DIAGONAL_UP_RIGHT,
        SGGradationPaint.getDirectionIndex(SGGradationPaint.NAME_DIRECTION_DIAGONAL_UP_RIGHT));
    assertNull(SGGradationPaint.getDirectionIndex("No such direction"));
    assertEquals(
        SGGradationPaint.INDEX_ORDER_COLOR_2_1_2,
        SGGradationPaint.getOrderIndex(SGGradationPaint.NAME_ORDER_COLOR_2_1_2));
    assertNull(SGGradationPaint.getOrderIndex("No such order"));
  }

  @Test
  void paintAndPropertyMapAreAvailable() {
    SGGradationPaint paint = new SGGradationPaint();
    Paint awtPaint = paint.getPaint(new Rectangle2D.Double(0, 0, 10, 10));
    assertNotNull(awtPaint);
    assertNotNull(paint.getPropertyFileMap());
  }

  @Test
  void equalsAndCloneFollowValues() throws Exception {
    SGGradationPaint paint1 = new SGGradationPaint();
    assertTrue(paint1.setColor1(Color.RED));
    SGGradationPaint paint2 = (SGGradationPaint) paint1.clone();
    assertEquals(paint1, paint2);
    assertEquals(paint1.hashCode(), paint2.hashCode());
    paint2.setColor1(Color.BLUE);
    assertNotEquals(paint1, paint2);
    paint1.dispose();
    assertTrue(paint1.isDisposed());
  }
}
