package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.BasicStroke;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGStroke}. */
class SGStrokeTest {

  @Test
  void defaultConstructorHasDefaults() {
    SGStroke stroke = new SGStroke();

    assertEquals(1.0f, stroke.getMagnification(), 0.0f);
    assertEquals(1.0f, stroke.getLineWidth(), 0.0f);
    assertEquals(SGILineConstants.LINE_TYPE_SOLID, stroke.getLineType());
    assertEquals(BasicStroke.CAP_BUTT, stroke.getEndCap());
    assertEquals(BasicStroke.JOIN_BEVEL, stroke.getLineJoin());
    assertEquals(1.0f, stroke.getMiterLimit(), 0.0f);
  }

  @Test
  void setLineWidth() {
    SGStroke stroke = new SGStroke();
    stroke.setLineWidth(2.5f);
    assertEquals(2.5f, stroke.getLineWidth(), 0.0f);
  }

  @Test
  void setLineWidthRejectsNegative() {
    SGStroke stroke = new SGStroke();
    assertThrows(IllegalArgumentException.class, () -> stroke.setLineWidth(-1.0f));
  }

  @Test
  void setLineWidthAllowsZero() {
    SGStroke stroke = new SGStroke();
    stroke.setLineWidth(0.0f);
    assertEquals(0.0f, stroke.getLineWidth(), 0.0f);
  }

  @Test
  void setMagnification() {
    SGStroke stroke = new SGStroke();
    stroke.setMagnification(2.0f);
    assertEquals(2.0f, stroke.getMagnification(), 0.0f);
  }

  @Test
  void setMagnificationRejectsZero() {
    SGStroke stroke = new SGStroke();
    assertThrows(IllegalArgumentException.class, () -> stroke.setMagnification(0.0f));
  }

  @Test
  void setMagnificationRejectsNegative() {
    SGStroke stroke = new SGStroke();
    assertThrows(IllegalArgumentException.class, () -> stroke.setMagnification(-1.0f));
  }

  @Test
  void setLineTypeToSolid() {
    SGStroke stroke = new SGStroke();
    stroke.setLineType(SGILineConstants.LINE_TYPE_SOLID);
    assertEquals(SGILineConstants.LINE_TYPE_SOLID, stroke.getLineType());
  }

  @Test
  void setLineTypeToDotted() {
    SGStroke stroke = new SGStroke();
    stroke.setLineType(SGILineConstants.LINE_TYPE_DOTTED);
    assertEquals(SGILineConstants.LINE_TYPE_DOTTED, stroke.getLineType());
  }

  @Test
  void setLineTypeToDashed() {
    SGStroke stroke = new SGStroke();
    stroke.setLineType(SGILineConstants.LINE_TYPE_DASHED);
    assertEquals(SGILineConstants.LINE_TYPE_DASHED, stroke.getLineType());
  }

  @Test
  void setLineTypeRejectsInvalid() {
    SGStroke stroke = new SGStroke();
    assertThrows(IllegalArgumentException.class, () -> stroke.setLineType(999));
  }

  @Test
  void setEndCap() {
    SGStroke stroke = new SGStroke();
    stroke.setEndCap(BasicStroke.CAP_ROUND);
    assertEquals(BasicStroke.CAP_ROUND, stroke.getEndCap());

    stroke.setEndCap(BasicStroke.CAP_SQUARE);
    assertEquals(BasicStroke.CAP_SQUARE, stroke.getEndCap());
  }

  @Test
  void setEndCapRejectsInvalid() {
    SGStroke stroke = new SGStroke();
    assertThrows(IllegalArgumentException.class, () -> stroke.setEndCap(999));
  }

  @Test
  void setJoin() {
    SGStroke stroke = new SGStroke();
    stroke.setJoin(BasicStroke.JOIN_ROUND);
    assertEquals(BasicStroke.JOIN_ROUND, stroke.getLineJoin());

    stroke.setJoin(BasicStroke.JOIN_MITER);
    assertEquals(BasicStroke.JOIN_MITER, stroke.getLineJoin());
  }

  @Test
  void setJoinRejectsInvalid() {
    SGStroke stroke = new SGStroke();
    assertThrows(IllegalArgumentException.class, () -> stroke.setJoin(999));
  }

  @Test
  void setMiterLimit() {
    SGStroke stroke = new SGStroke();
    stroke.setMiterLimit(2.0f);
    assertEquals(2.0f, stroke.getMiterLimit(), 0.0f);
  }

  @Test
  void setDashPhase() {
    SGStroke stroke = new SGStroke();
    stroke.setDashPhase(5.0f);
    assertEquals(5.0f, stroke.getDashPhase(), 0.0f);
  }

  @Test
  void getBasicStrokeReturnsNullForSolidDefault() {
    SGStroke stroke = new SGStroke();
    // Default line type is SOLID, mBasicStroke is null until setLineType is called
    assertEquals(null, stroke.getBasicStroke());
  }

  @Test
  void getBasicStrokeReturnsNonNullAfterSetLineType() {
    SGStroke stroke = new SGStroke();
    stroke.setLineType(SGILineConstants.LINE_TYPE_DOTTED);
    assertNotNull(stroke.getBasicStroke());
  }

  @Test
  void getStrokeDashForSolidReturnsNull() {
    SGStroke stroke = new SGStroke();
    assertEquals(null, stroke.getStrokeDash(SGILineConstants.LINE_TYPE_SOLID));
  }

  @Test
  void getStrokeDashForDottedReturnsNonNull() {
    SGStroke stroke = new SGStroke();
    assertNotNull(stroke.getStrokeDash(SGILineConstants.LINE_TYPE_DOTTED));
  }

  @Test
  void getStrokeDashReturnsClone() {
    SGStroke stroke = new SGStroke();
    SGStrokeDash d1 = stroke.getStrokeDash(SGILineConstants.LINE_TYPE_DOTTED);
    SGStrokeDash d2 = stroke.getStrokeDash(SGILineConstants.LINE_TYPE_DOTTED);
    assertNotSame(d1, d2);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGStroke original = new SGStroke();
    original.setLineWidth(3.0f);
    original.setMagnification(2.0f);

    SGStroke copy = (SGStroke) original.clone();
    assertEquals(3.0f, copy.getLineWidth(), 0.0f);
    assertEquals(2.0f, copy.getMagnification(), 0.0f);
  }

  @Test
  void getLineNum1ForDashed() {
    SGStroke stroke = new SGStroke();
    // Default dashed: (1, 1, 4.0f, 1.0f, 1.0f)
    assertEquals(1, stroke.getLineNum1(SGILineConstants.LINE_TYPE_DASHED));
  }

  @Test
  void getLineLength1ForDashed() {
    SGStroke stroke = new SGStroke();
    assertEquals(4.0f, stroke.getLineLength1(SGILineConstants.LINE_TYPE_DASHED), 0.0f);
  }
}
