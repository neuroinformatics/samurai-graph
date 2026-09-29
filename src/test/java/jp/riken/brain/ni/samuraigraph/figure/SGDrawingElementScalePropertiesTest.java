package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;

/** Unit tests of the scale drawing element properties. */
class SGDrawingElementScalePropertiesTest {

  private static SGDrawingElementScale.ScaleProperties filled() {
    final SGDrawingElementScale.ScaleProperties p = new SGDrawingElementScale.ScaleProperties();
    p.mWidth = 10.0f;
    p.mHeight = 20.0f;
    p.mTextAngle = 30.0f;
    p.mHorizontalVisible = true;
    p.mVerticalVisible = true;
    p.mHorizontalText = "x";
    p.mVerticalText = "y";
    p.mHorizontalUnit = "cm";
    p.mVerticalUnit = "cm";
    p.mHorizontalTextDownside = true;
    p.mVerticalTextLeftside = true;
    p.mStringColor = Color.RED;
    // inherited line/string state
    p.mSpace = 1.0f;
    p.mLineWidth = 1.5f;
    p.mFontName = "SansSerif";
    p.mFontSize = 12.0f;
    p.mFontStyle = 0;
    p.mColor = Color.BLACK;
    return p;
  }

  @Test
  void equalPropertiesCompareEqual() {
    assertTrue(filled().equals(filled()));
  }

  @Test
  void eachScaleFieldDifferenceBreaksEquality() {
    final SGDrawingElementScale.ScaleProperties base = filled();

    final SGDrawingElementScale.ScaleProperties width = filled();
    width.mWidth = 99.0f;
    assertFalse(base.equals(width));

    final SGDrawingElementScale.ScaleProperties height = filled();
    height.mHeight = 99.0f;
    assertFalse(base.equals(height));

    final SGDrawingElementScale.ScaleProperties angle = filled();
    angle.mTextAngle = 99.0f;
    assertFalse(base.equals(angle));

    final SGDrawingElementScale.ScaleProperties hVisible = filled();
    hVisible.mHorizontalVisible = false;
    assertFalse(base.equals(hVisible));

    final SGDrawingElementScale.ScaleProperties vVisible = filled();
    vVisible.mVerticalVisible = false;
    assertFalse(base.equals(vVisible));

    final SGDrawingElementScale.ScaleProperties hText = filled();
    hText.mHorizontalText = "other";
    assertFalse(base.equals(hText));

    final SGDrawingElementScale.ScaleProperties vText = filled();
    vText.mVerticalText = "other";
    assertFalse(base.equals(vText));

    final SGDrawingElementScale.ScaleProperties hUnit = filled();
    hUnit.mHorizontalUnit = "mm";
    assertFalse(base.equals(hUnit));

    final SGDrawingElementScale.ScaleProperties vUnit = filled();
    vUnit.mVerticalUnit = "mm";
    assertFalse(base.equals(vUnit));

    final SGDrawingElementScale.ScaleProperties hDownside = filled();
    hDownside.mHorizontalTextDownside = false;
    assertFalse(base.equals(hDownside));

    final SGDrawingElementScale.ScaleProperties vLeftside = filled();
    vLeftside.mVerticalTextLeftside = false;
    assertFalse(base.equals(vLeftside));

    final SGDrawingElementScale.ScaleProperties color = filled();
    color.mStringColor = Color.BLUE;
    assertFalse(base.equals(color));
  }

  @Test
  void eachInheritedFieldDifferenceBreaksEquality() {
    final SGDrawingElementScale.ScaleProperties base = filled();

    final SGDrawingElementScale.ScaleProperties space = filled();
    space.mSpace = 9.0f;
    assertFalse(base.equals(space));

    final SGDrawingElementScale.ScaleProperties lineWidth = filled();
    lineWidth.mLineWidth = 9.0f;
    assertFalse(base.equals(lineWidth));

    final SGDrawingElementScale.ScaleProperties fontName = filled();
    fontName.mFontName = "Serif";
    assertFalse(base.equals(fontName));

    final SGDrawingElementScale.ScaleProperties fontSize = filled();
    fontSize.mFontSize = 9.0f;
    assertFalse(base.equals(fontSize));

    final SGDrawingElementScale.ScaleProperties color = filled();
    color.mColor = Color.RED;
    assertFalse(base.equals(color));
  }

  @Test
  void foreignTypeIsNotEqual() {
    assertFalse(filled().equals(new SGProperties() {}));
  }
}
