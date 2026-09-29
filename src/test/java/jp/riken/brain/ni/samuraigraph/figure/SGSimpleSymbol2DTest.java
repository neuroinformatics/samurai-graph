package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Headless unit tests of the simple 2D symbol drawing element. */
class SGSimpleSymbol2DTest {

  private SGSimpleSymbol2D symbol;

  @BeforeEach
  void createSymbol() {
    this.symbol = new SGSimpleSymbol2D();
  }

  @Test
  void defaultAttributesAreInitial() {
    assertEquals(1.0f, this.symbol.getMagnification(), 1.0e-6f);
    assertEquals(0.0f, this.symbol.getAngle(), 1.0e-6f);
    assertEquals(0.0f, this.symbol.getSize(), 1.0e-6f);
    assertEquals(0, this.symbol.getType());
    assertNull(this.symbol.getLineColor());
    assertFalse(this.symbol.isLineVisible());
  }

  @Test
  void sizeAndTypeSetTheFieldsAndRebuildTheShape() {
    this.symbol.setType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE);
    assertEquals(SGSymbolConstants.SYMBOL_TYPE_CIRCLE, this.symbol.getType());
    assertTrue(this.symbol.setSize(2.0f));
    assertEquals(2.0f, this.symbol.getSize(), 1.0e-6f);
    assertNotNull(this.symbol.getSymbolShape());
  }

  @Test
  void magnificationMustBePositive() {
    assertThrows(IllegalArgumentException.class, () -> this.symbol.setMagnification(0.0f));
    assertThrows(IllegalArgumentException.class, () -> this.symbol.setMagnification(-1.0f));
  }

  @Test
  void magnificationScalesTheSymbolShape() {
    this.symbol.setType(SGSymbolConstants.SYMBOL_TYPE_SQUARE);
    this.symbol.setSize(2.0f);
    final Rectangle2D base = this.symbol.getSymbolShape().getBounds2D();
    final float baseWidth = (float) base.getWidth();
    this.symbol.setMagnification(3.0f);
    final Rectangle2D scaled = this.symbol.getSymbolShape().getBounds2D();
    assertEquals(baseWidth * 3.0f, (float) scaled.getWidth(), 1.0e-6f);
    assertEquals(3.0f, this.symbol.getMagnification(), 1.0e-6f);
  }

  @Test
  void eachSymbolTypeBuildsADistinctShape() {
    this.symbol.setSize(2.0f);
    for (int type :
        new int[] {
          SGSymbolConstants.SYMBOL_TYPE_CIRCLE,
          SGSymbolConstants.SYMBOL_TYPE_SQUARE,
          SGSymbolConstants.SYMBOL_TYPE_DIAMOND,
          SGSymbolConstants.SYMBOL_TYPE_TRIANGLE,
          SGSymbolConstants.SYMBOL_TYPE_INVERTED_TRIANGLE,
          SGSymbolConstants.SYMBOL_TYPE_CROSS,
          SGSymbolConstants.SYMBOL_TYPE_PLUS
        }) {
      this.symbol.setType(type);
      assertNotNull(this.symbol.getSymbolShape(), "shape for type " + type);
    }
  }

  @Test
  void circleHasTheConfiguredDiameter() {
    this.symbol.setSize(2.0f);
    this.symbol.setType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE);
    final Shape sh = this.symbol.getSymbolShape();
    assertNotNull(sh);
    final Rectangle2D bounds = sh.getBounds2D();
    assertEquals(2.0, bounds.getWidth(), 1.0e-6);
    assertEquals(2.0, bounds.getHeight(), 1.0e-6);
  }

  @Test
  void translationMovesTheSymbolShape() {
    this.symbol.setSize(2.0f);
    this.symbol.setType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE);
    this.symbol.setLocation(5.0f, 6.0f);
    final Rectangle2D bounds = this.symbol.getSymbolShape().getBounds2D();
    assertEquals(5.0, bounds.getCenterX(), 1.0e-6);
    assertEquals(6.0, bounds.getCenterY(), 1.0e-6);
  }

  @Test
  void rotationRotatesTheSymbolShape() {
    this.symbol.setSize(2.0f);
    this.symbol.setType(SGSymbolConstants.SYMBOL_TYPE_PLUS);
    this.symbol.setAngle(45.0f);
    assertTrue(this.symbol.setAngle(90.0f));
    assertEquals(90.0f, this.symbol.getAngle(), 1.0e-6f);
    assertNotNull(this.symbol.getSymbolShape());
  }

  @Test
  void locationAndCoordinatesAreManaged() {
    this.symbol.setLocation(3.0f, 4.0f);
    assertEquals(3.0f, this.symbol.getX(), 1.0e-6f);
    assertEquals(4.0f, this.symbol.getY(), 1.0e-6f);
    final SGTuple2f loc = this.symbol.getLocation();
    assertEquals(3.0f, loc.x, 1.0e-6f);
    assertEquals(4.0f, loc.y, 1.0e-6f);
    this.symbol.setX(7.0f);
    this.symbol.setY(8.0f);
    assertEquals(7.0f, this.symbol.getX(), 1.0e-6f);
    assertEquals(8.0f, this.symbol.getY(), 1.0e-6f);
  }

  @Test
  void locationIsCopiedOnReadAndWrite() {
    this.symbol.setLocation(1.0f, 2.0f);
    final SGTuple2f loc = this.symbol.getLocation();
    loc.x = 99.0f;
    // mutating the returned tuple does not affect the stored location
    assertEquals(1.0f, this.symbol.getX(), 1.0e-6f);
    final SGTuple2f point = new SGTuple2f(5.0f, 6.0f);
    this.symbol.setLocation(point);
    point.x = 100.0f;
    // mutating the passed tuple does not affect the stored location either
    assertEquals(5.0f, this.symbol.getX(), 1.0e-6f);
  }

  @Test
  void lineAttributesRoundTrip() {
    this.symbol.setLineColor(Color.RED);
    assertEquals(Color.RED, this.symbol.getLineColor());
    this.symbol.setLineWidth(1.5f);
    assertEquals(1.5f, this.symbol.getLineWidth(), 1.0e-6f);
    this.symbol.setLineVisible(true);
    assertTrue(this.symbol.isLineVisible());
    this.symbol.setLineVisible(false);
    assertFalse(this.symbol.isLineVisible());
  }

  @Test
  void innerPaintIsDelegatedToTheFillPaint() {
    final SGIPaint paint = this.symbol.getInnerPaint();
    assertNotNull(paint);
    assertTrue(paint instanceof SGFillPaint);
    this.symbol.setInnerColor(Color.BLUE);
    assertEquals(Color.BLUE, ((SGFillPaint) paint).getColor());
    assertTrue(this.symbol.setInnerColor(Color.GREEN));
    assertEquals(Color.GREEN, ((SGFillPaint) this.symbol.getInnerPaint()).getColor());
  }

  @Test
  void disposeClearsTheState() {
    this.symbol.setLineColor(Color.RED);
    this.symbol.setInnerColor(Color.BLUE);
    this.symbol.dispose();
    assertTrue(this.symbol.isDisposed());
    assertNull(this.symbol.getLineColor());
    assertNull(this.symbol.getInnerPaint());
  }
}
