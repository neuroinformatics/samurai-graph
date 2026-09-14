package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;

/** Unit tests for the error bar group for data with a stub subclass. */
class SGElementGroupErrorBarForDataTest {

  private static class TestErrorBarGroup extends SGElementGroupErrorBarForData {

    TestErrorBarGroup() {
      super(null);
    }

    @Override
    public SGTuple2f getStartLocation(int index) {
      return null;
    }

    @Override
    public SGTuple2f getUpperEndLocation(int index) {
      return null;
    }

    @Override
    public SGTuple2f getLowerEndLocation(int index) {
      return null;
    }

    @Override
    public boolean updateLocation() {
      return true;
    }

    @Override
    public SGDrawingElement createDrawingElementInstance(int index) {
      return null;
    }

    @Override
    public boolean paintElement(Graphics2D g2d, Rectangle2D clipRect) {
      return true;
    }

    @Override
    public String getTagName() {
      return "ErrorBar";
    }

    @Override
    public boolean writeProperty(Element el) {
      return true;
    }

    @Override
    public boolean readProperty(Element el) {
      return true;
    }
  }

  @Test
  void defaultsAreAppliedAtConstruction() {
    TestErrorBarGroup group = new TestErrorBarGroup();
    assertTrue(group.isVisible());
    assertEquals(SGSXYDataConstants.DEFAULT_ERROR_BAR_LINE_WIDTH, group.getLineWidth(), 1.0e-6f);
    assertEquals(SGSXYDataConstants.DEFAULT_ERROR_BAR_STYLE, group.getErrorBarStyle());
    assertEquals(SGSXYDataConstants.DEFAULT_ERROR_BAR_COLOR, group.getColor());
    assertTrue(group.isVertical());
    assertTrue(group.isPositionOnLine());
  }

  @Test
  void lineWidthIsValidated() {
    TestErrorBarGroup group = new TestErrorBarGroup();
    assertTrue(group.setLineWidth(1.5f, "pt"));
    assertFalse(group.setLineWidth(100.0f, "pt"));
  }

  @Test
  void headSizeIsValidated() {
    TestErrorBarGroup group = new TestErrorBarGroup();
    assertTrue(group.setHeadSize(0.3f, "cm"));
    assertFalse(group.setHeadSize(100.0f, "cm"));
  }

  @Test
  void styleAndHeadTypeAreValidated() {
    TestErrorBarGroup group = new TestErrorBarGroup();
    assertTrue(group.setErrorBarStyle(SGErrorBarConstants.ERROR_BAR_UPSIDE));
    assertEquals(SGErrorBarConstants.ERROR_BAR_UPSIDE, group.getErrorBarStyle());
    assertFalse(group.setErrorBarStyle(999));
    assertEquals(SGErrorBarConstants.ERROR_BAR_UPSIDE, group.getErrorBarStyle());
    assertTrue(group.setHeadType(SGSymbolConstants.SYMBOL_TYPE_CIRCLE));
    assertEquals(SGSymbolConstants.SYMBOL_TYPE_CIRCLE, group.getHeadType());
    assertFalse(group.setHeadType(999));
  }

  @Test
  void layoutFlagsToggle() {
    TestErrorBarGroup group = new TestErrorBarGroup();
    assertTrue(group.setVertical(false));
    assertFalse(group.isVertical());
    assertTrue(group.setPositionOnLine(false));
    assertFalse(group.isPositionOnLine());
  }
}
