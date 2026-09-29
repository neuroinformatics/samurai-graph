package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the grid figure element properties. */
class SGFigureElementGridPropertiesTest {

  private static SGFigureElementGrid.GridProperties filled() {
    final SGFigureElementGrid.GridProperties p = new SGFigureElementGrid.GridProperties();
    p.mXAxisLocation = 1;
    p.mYAxisLocation = 2;
    p.mBaselineValueX = new SGAxisDoubleValue(1.5);
    p.mStepValueX = new SGAxisDoubleStepValue(0.5);
    p.mBaselineValueY = new SGAxisDoubleValue(2.5);
    p.mStepValueY = new SGAxisDoubleStepValue(0.25);
    p.mVisibleFlag = true;
    p.mAutoRangeFlag = true;
    p.mLineWidth = 1.5f;
    p.mLineType = SGLineConstants.LINE_TYPE_DASHED;
    p.mColor = Color.RED;
    return p;
  }

  @Test
  void equalPropertiesCompareEqual() {
    assertTrue(filled().equals(filled()));
  }

  @Test
  void eachFieldDifferenceBreaksEquality() {
    final SGFigureElementGrid.GridProperties base = filled();

    final SGFigureElementGrid.GridProperties xAxis = filled();
    xAxis.mXAxisLocation = 9;
    assertFalse(base.equals(xAxis));

    final SGFigureElementGrid.GridProperties yAxis = filled();
    yAxis.mYAxisLocation = 9;
    assertFalse(base.equals(yAxis));

    final SGFigureElementGrid.GridProperties baselineX = filled();
    baselineX.mBaselineValueX = new SGAxisDoubleValue(9.0);
    assertFalse(base.equals(baselineX));

    final SGFigureElementGrid.GridProperties stepX = filled();
    stepX.mStepValueX = new SGAxisDoubleStepValue(9.0);
    assertFalse(base.equals(stepX));

    final SGFigureElementGrid.GridProperties visible = filled();
    visible.mVisibleFlag = false;
    assertFalse(base.equals(visible));

    final SGFigureElementGrid.GridProperties autoRange = filled();
    autoRange.mAutoRangeFlag = false;
    assertFalse(base.equals(autoRange));

    final SGFigureElementGrid.GridProperties lineWidth = filled();
    lineWidth.mLineWidth = 3.0f;
    assertFalse(base.equals(lineWidth));

    final SGFigureElementGrid.GridProperties lineType = filled();
    lineType.mLineType = SGLineConstants.LINE_TYPE_SOLID;
    assertFalse(base.equals(lineType));

    final SGFigureElementGrid.GridProperties color = filled();
    color.mColor = Color.BLUE;
    assertFalse(base.equals(color));
  }

  @Test
  void nullIsNotEqual() {
    assertFalse(filled().equals(null));
  }

  @Test
  void foreignTypeIsNotEqual() {
    assertFalse(filled().equals(new SGProperties() {}));
  }

  @Test
  void disposeClearsTheColor() {
    final SGFigureElementGrid.GridProperties p = filled();
    p.dispose();
    assertNull(p.mColor);
    // the numeric state stays readable after dispose
    assertEquals(1, p.mXAxisLocation);
    assertEquals(1.5f, p.mLineWidth, 1.0e-6f);
  }
}
