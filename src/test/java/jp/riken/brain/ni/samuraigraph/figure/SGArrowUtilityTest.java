package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGArrowUtility}. */
class SGArrowUtilityTest {

  /** A concrete arrow group with a minimal implementation. */
  private static class TestArrowGroup extends SGElementGroupArrow {
    private static final long serialVersionUID = 1L;

    TestArrowGroup() {
      super();
    }

    @Override
    public boolean setLineWidth(final float lw, final String unit) {
      return true;
    }

    @Override
    public boolean setHeadSize(final float size, final String unit) {
      return true;
    }

    @Override
    public boolean setHeadAngle(final float openAngle, final float closeAngle) {
      return true;
    }

    @Override
    public SGTuple2f getStartLocation(final int index) {
      return null;
    }

    @Override
    public SGTuple2f getEndLocation(final int index) {
      return null;
    }

    @Override
    public SGDrawingElementArrow createDrawingElementInstance(int index) {
      return null;
    }

    @Override
    public boolean initDrawingElement(float[] xArray, float[] yArray) {
      return true;
    }
  }

  private SGElementGroupArrow arrows;

  @BeforeEach
  void createArrowGroup() {
    this.arrows = new TestArrowGroup();
  }

  @Test
  void lineWidthIsSetWithinAllowedRange() {
    assertTrue(SGArrowUtility.setLineWidth(1.5f, "pt", this.arrows));
    assertEquals(1.5f, this.arrows.getLineWidth(), 1.0e-4f);
  }

  @Test
  void excessLineWidthIsRejected() {
    assertFalse(SGArrowUtility.setLineWidth(100.0f, "pt", this.arrows));
  }

  @Test
  void headSizeIsSetWithinAllowedRange() {
    assertTrue(SGArrowUtility.setHeadSize(0.4f, "cm", this.arrows));
    assertEquals(0.4f, this.arrows.getHeadSize("cm"), 1.0e-2f);
  }

  @Test
  void excessHeadSizeIsRejected() {
    assertFalse(SGArrowUtility.setHeadSize(100.0f, "cm", this.arrows));
  }

  @Test
  void headAngleIsSet() {
    assertTrue(SGArrowUtility.setHeadAngle(30.0f, 60.0f, this.arrows));
    assertEquals(30.0f, this.arrows.mHeadOpenAngle, 1.0e-4);
    assertEquals(60.0f, this.arrows.mHeadCloseAngle, 1.0e-4);
  }

  @Test
  void closeAngleMustExceedOpenAngle() {
    this.arrows.mHeadOpenAngle = 30.0f;
    this.arrows.mHeadCloseAngle = 60.0f;
    assertFalse(SGArrowUtility.setHeadAngle(60.0f, 60.0f, this.arrows));
    assertEquals(30.0f, this.arrows.mHeadOpenAngle);
    assertEquals(60.0f, this.arrows.mHeadCloseAngle);
  }

  @Test
  void nullAngleKeepsCurrentValue() {
    this.arrows.mHeadOpenAngle = 25.0f;
    this.arrows.mHeadCloseAngle = 80.0f;
    assertTrue(SGArrowUtility.setHeadAngle((Float) null, Float.valueOf(60.0f), this.arrows));
    assertEquals(25.0f, this.arrows.mHeadOpenAngle);
    assertEquals(60.0f, this.arrows.mHeadCloseAngle);
  }

  @Test
  void outOfRangeAngleIsRejected() {
    assertFalse(SGArrowUtility.setHeadAngle(90.0f, 90.0f, this.arrows));
    assertTrue(SGArrowUtility.setHeadAngle(5.0f, 6.0f, this.arrows));
  }

  private static SGTuple2f calcOrthogonalEnd(
      float startX, float startY, double xComponent, double yComponent) {
    return SGArrowUtility.calcEndLocation(
        startX, startY, xComponent, yComponent, false, 1.0f, true, 2.0f, 0.0);
  }

  private static SGTuple2f calcPolarEnd(
      float startX, float startY, double magnitude, double angle) {
    return SGArrowUtility.calcEndLocation(
        startX, startY, magnitude, angle, true, 1.0f, true, 2.0f, 0.0);
  }

  @Test
  void orthogonalEndWithInvariantScale() {
    SGTuple2f end = calcOrthogonalEnd(10.0f, 20.0f, 3.0, 4.0);
    assertEquals(16.0f, end.x, 1.0e-2);
    assertEquals(12.0f, end.y, 1.0e-2);
  }

  @Test
  void orthogonalEndWithNegativeComponents() {
    SGTuple2f end = calcOrthogonalEnd(10.0f, 20.0f, -3.0, 0.0);
    assertEquals(4.0f, end.x, 1.0e-2);
    assertEquals(20.0f, end.y, 1.0e-2);
  }

  @Test
  void polarEndWithPositiveMagnitude() {
    double angle = Math.PI / 4.0;
    SGTuple2f end = calcPolarEnd(0.0f, 0.0f, 10.0, angle);
    final double expected = 20.0 * Math.sqrt(2.0) / 2.0;
    assertEquals(expected, end.x, 1.0e-2);
    assertEquals(-expected, end.y, 1.0e-2);
  }

  @Test
  void polarEndWithNegativeAngle() {
    double angle = -Math.PI / 4.0;
    SGTuple2f end = calcPolarEnd(0.0f, 0.0f, 10.0, angle);
    final double expected = 20.0 * Math.sqrt(2.0) / 2.0;
    assertEquals(expected, end.x, 1.0e-2);
    assertEquals(expected, end.y, 1.0e-2);
  }

  @Test
  void zeroMagnitudeKeepsStartLocation() {
    SGTuple2f end = calcPolarEnd(10.0f, 20.0f, 0.0, Math.PI / 2.0);
    assertEquals(10.0f, end.x);
    assertEquals(20.0f, end.y);
  }

  @Test
  void negativeMagnitudeIsRejected() {
    SGTuple2f end = calcPolarEnd(0.0f, 0.0f, -1.0, Math.PI / 2);
    assertTrue(end.isNaN());
  }

  @Test
  void nanInputsAreRejected() {
    SGTuple2f end = calcOrthogonalEnd(Float.NaN, 0.0f, 1.0, 1.0);
    assertTrue(end.isNaN());
    end = calcPolarEnd(0.0f, 0.0f, Double.NaN, 0.0);
    assertTrue(end.isNaN());
  }
}
