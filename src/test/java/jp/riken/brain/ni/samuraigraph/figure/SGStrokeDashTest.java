package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGStrokeDash}. */
class SGStrokeDashTest {

  @Test
  void constructorSetsValues() {
    SGStrokeDash dash = new SGStrokeDash(2, 1, 4.0f, 2.0f, 1.0f);

    assertEquals(2, dash.getLineNum1());
    assertEquals(1, dash.getLineNum2());
    assertEquals(4.0f, dash.getLineLen1(), 0.0f);
    assertEquals(2.0f, dash.getLineLen2(), 0.0f);
    assertEquals(1.0f, dash.getSpace(), 0.0f);
  }

  @Test
  void setLineNum1RejectsNegative() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    assertThrows(IllegalArgumentException.class, () -> dash.setLineNum1(-1));
  }

  @Test
  void setLineNum2RejectsNegative() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    assertThrows(IllegalArgumentException.class, () -> dash.setLineNum2(-1));
  }

  @Test
  void setLineLen1RejectsNegative() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    assertThrows(IllegalArgumentException.class, () -> dash.setLineLen1(-1.0f));
  }

  @Test
  void setLineLen2RejectsNegative() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    assertThrows(IllegalArgumentException.class, () -> dash.setLineLen2(-1.0f));
  }

  @Test
  void setSpaceRejectsNegative() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    assertThrows(IllegalArgumentException.class, () -> dash.setSpace(-1.0f));
  }

  @Test
  void setLineNum1AllowsZero() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    dash.setLineNum1(0);
    assertEquals(0, dash.getLineNum1());
  }

  @Test
  void setLineLenAllowsZero() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    dash.setLineLen1(0.0f);
    assertEquals(0.0f, dash.getLineLen1(), 0.0f);
  }

  @Test
  void setSpaceAllowsZero() {
    SGStrokeDash dash = new SGStrokeDash(1, 0, 1.0f, 1.0f, 1.0f);
    dash.setSpace(0.0f);
    assertEquals(0.0f, dash.getSpace(), 0.0f);
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGStrokeDash original = new SGStrokeDash(2, 1, 4.0f, 2.0f, 1.0f);
    SGStrokeDash copy = (SGStrokeDash) original.clone();

    assertNotSame(original, copy);
    assertEquals(2, copy.getLineNum1());
    assertEquals(1, copy.getLineNum2());
    assertEquals(4.0f, copy.getLineLen1(), 0.0f);
  }
}
