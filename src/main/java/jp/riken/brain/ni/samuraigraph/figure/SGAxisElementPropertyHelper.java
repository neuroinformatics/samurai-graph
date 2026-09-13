package jp.riken.brain.ni.samuraigraph.figure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGFigureElementAxisConstants;

class SGAxisElementPropertyHelper {

  private final SGAxisElement owner;

  public SGAxisElementPropertyHelper(final SGAxisElement owner) {
    this.owner = owner;
  }

  double[] calcScaleValuesInLinearScale() {
    SGAxis axis = owner.mAxis;
    final double minValue = axis.getMinDoubleValue();
    final double maxValue = axis.getMaxDoubleValue();
    final SGAxisValue baseline;
    final SGAxisStepValue step;
    if (owner.isScaleAuto()) {
      baseline = owner.mAxisElement.calcBaselineValue(axis);
      step = owner.mAxisElement.calcStepValue(axis);
      owner.mBaselineValue = baseline;
      owner.mStepValue = step;
    } else {
      baseline = owner.getScaleBase();
      step = owner.getScaleStep();
    }

    SGAxisValue[] axisValueArray;
    if (owner.getDateMode()) {

      // get an array of numbers
      axisValueArray =
          SGUtilityNumber.calcStepValueSorted(
              new SGAxisDateValue(minValue),
              new SGAxisDateValue(maxValue),
              baseline,
              step,
              SGConstants.AXIS_SCALE_EFFECTIVE_DIGIT);

    } else {

      // get an array of numbers
      axisValueArray =
          SGUtilityNumber.calcStepValueSorted(
              new SGAxisDoubleValue(minValue),
              new SGAxisDoubleValue(maxValue),
              baseline,
              step,
              SGConstants.AXIS_SCALE_EFFECTIVE_DIGIT);

      // set scale numbers integer when all numbers
      // can be replaced with an integer
      if (owner.mAxisElement.mStartFlag) {
        boolean flag = true;
        for (int ii = 0; ii < axisValueArray.length; ii++) {
          final double value = axisValueArray[ii].getValue();
          final long round = Math.round(value);
          final double diff = Math.abs(value - round);
          if (diff != 0.0) {
            flag = false;
            break;
          }
        }
        owner.setNumbersInteger(flag);
      }

      // When the scale numbers are set to be integer,
      // cast values to integer
      if (owner.isNumbersInteger()) {

        final List<Integer> numList = new ArrayList<Integer>();
        for (int ii = 0; ii < axisValueArray.length; ii++) {
          double value;
          if (owner.isExponentVisible()) {
            BigDecimal db = new BigDecimal(Double.toString(axisValueArray[ii].getValue()));
            db = db.movePointLeft(owner.getExponentValue());
            value = db.doubleValue();
          } else {
            value = axisValueArray[ii].getValue();
          }
          final int num = (int) value;
          if (Math.abs(num - value) < Double.MIN_VALUE) {
            numList.add(Integer.valueOf(num));
          }
        }

        // remove the same values
        for (int ii = numList.size() - 1; ii >= 1; ii--) {
          final Integer n1 = numList.get(ii);
          for (int jj = ii - 1; jj >= 0; jj--) {
            final Integer n2 = numList.get(jj);
            if (n2.intValue() == n1.intValue()) {
              numList.remove(ii);
              break;
            }
          }
        }

        final double[] valueArray = new double[numList.size()];
        for (int ii = 0; ii < valueArray.length; ii++) {
          double value = ((Integer) numList.get(ii)).doubleValue();
          if (owner.isExponentVisible()) {
            BigDecimal db = new BigDecimal(Double.toString(value));
            db = db.movePointRight(owner.getExponentValue());
            value = db.doubleValue();
          }
          valueArray[ii] = value;
        }

        // these values reflect the integer flag,
        // but are not influenced by the exponent flag
        axisValueArray = new SGAxisDoubleValue[valueArray.length];
        for (int ii = 0; ii < axisValueArray.length; ii++) {
          axisValueArray[ii] = new SGAxisDoubleValue(valueArray[ii]);
        }
      }
    }

    double[] ret = new double[axisValueArray.length];
    for (int ii = 0; ii < axisValueArray.length; ii++) {
      ret[ii] = axisValueArray[ii].getValue();
    }
    return ret;
  }

  public SGPropertyMap getPropertyMap() {
    SGPropertyMap map = new SGPropertyMap();

    // visible
    SGPropertyUtility.addProperty(map, SGAxisConstants.COM_AXIS_VISIBLE, owner.isVisible());

    // frame line
    owner.addAxisLineProperties(
        map,
        owner.getLineVisibleCommandKey(),
        owner.getLineWidthCommandKey(),
        owner.getSpaceLineAndNumberCommandKey(),
        owner.getLineColorCommandKey());

    // title
    SGPropertyUtility.addQuotedStringProperty(
        map, SGAxisConstants.COM_AXIS_TITLE_TEXT, owner.getTitleString());
    owner.addTitleProperties(
        map,
        SGAxisConstants.COM_AXIS_TITLE_VISIBLE,
        SGAxisConstants.COM_AXIS_SPACE_TITLE_AND_NUMBER,
        SGAxisConstants.COM_AXIS_TITLE_CENTER_SHIFT,
        SGAxisConstants.COM_AXIS_TITLE_FONT_NAME,
        SGAxisConstants.COM_AXIS_TITLE_FONT_SIZE,
        SGAxisConstants.COM_AXIS_TITLE_FONT_STYLE,
        SGAxisConstants.COM_AXIS_TITLE_FONT_COLOR);

    // number
    owner.addNumberProperties(
        map,
        SGAxisConstants.COM_AXIS_NUMBER_VISIBLE,
        SGAxisConstants.COM_AXIS_NUMBER_INTEGER,
        SGAxisConstants.COM_AXIS_NUMBER_ANGLE,
        SGAxisConstants.COM_AXIS_EXPONENT_VISIBLE,
        SGAxisConstants.COM_AXIS_EXPONENT_VALUE,
        SGAxisConstants.COM_AXIS_EXPONENT_LOCATION_X,
        SGAxisConstants.COM_AXIS_EXPONENT_LOCATION_Y,
        SGAxisConstants.COM_AXIS_NUMBER_FONT_NAME,
        SGAxisConstants.COM_AXIS_NUMBER_FONT_SIZE,
        SGAxisConstants.COM_AXIS_NUMBER_FONT_STYLE,
        SGAxisConstants.COM_AXIS_NUMBER_FONT_COLOR);

    // scale
    StringBuilder sbScale = new StringBuilder();
    sbScale.append('(');
    sbScale.append(owner.getMinValue());
    sbScale.append(',');
    sbScale.append(owner.getMaxValue());
    sbScale.append(',');
    sbScale.append(SGUtilityText.getScaleTypeName(owner.getScaleType()));
    sbScale.append(')');
    SGPropertyUtility.addProperty(map, SGAxisConstants.COM_AXIS_SCALE_RANGE, sbScale.toString());
    owner.addScaleProperties(
        map,
        SGAxisConstants.COM_AXIS_INVERT_COORDINATES,
        SGAxisConstants.COM_AXIS_SCALE_AUTO,
        SGAxisConstants.COM_AXIS_SCALE_STEP,
        SGAxisConstants.COM_AXIS_SCALE_BASE);

    // tick mark
    owner.addTickMarkProperties(
        map,
        SGAxisConstants.COM_AXIS_TICK_MARK_VISIBLE,
        SGAxisConstants.COM_AXIS_TICK_MARK_BOTHSIDES,
        SGAxisConstants.COM_AXIS_TICK_MARK_WIDTH,
        SGAxisConstants.COM_AXIS_MAJOR_TICK_MARK_LENGTH,
        SGAxisConstants.COM_AXIS_MINOR_TICK_MARK_LENGTH,
        SGAxisConstants.COM_AXIS_MINOR_TICK_MARK_NUMBER,
        SGAxisConstants.COM_AXIS_TICK_MARK_COLOR);

    return map;
  }

  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = new SGPropertyMap();

    // visible
    SGPropertyUtility.addProperty(map, SGDrawingElementConstants.KEY_VISIBLE, owner.isVisible());

    // axis line
    owner.addAxisLineProperties(
        map,
        owner.getLineVisibleCommandKey(),
        owner.getLineWidthPropertyFileKey(),
        owner.getSpaceLineAndNumberPropertyFileKey(),
        owner.getLineColorPropertyFileKey());

    // Title
    SGPropertyUtility.addProperty(
        map, SGFigureElementAxisConstants.KEY_TITLE_TEXT, owner.getTitleString());
    owner.addTitleProperties(
        map,
        SGFigureElementAxisConstants.KEY_TITLE_VISIBLE,
        SGFigureElementAxisConstants.KEY_SPACE_TITLE_AND_NUMBERS,
        SGFigureElementAxisConstants.KEY_TITLE_SHIFT_FROM_CENTER,
        SGFigureElementAxisConstants.KEY_TITLE_FONT_NAME,
        SGFigureElementAxisConstants.KEY_TITLE_FONT_SIZE,
        SGFigureElementAxisConstants.KEY_TITLE_FONT_STYLE,
        SGFigureElementAxisConstants.KEY_TITLE_FONT_COLOR);

    // Number
    owner.addNumberProperties(
        map,
        SGFigureElementAxisConstants.KEY_NUMBER_VISIBLE,
        SGFigureElementAxisConstants.KEY_NUMBER_INTEGER,
        SGFigureElementAxisConstants.KEY_NUMBER_ANGLE,
        SGFigureElementAxisConstants.KEY_EXPONENT_VISIBLE,
        SGFigureElementAxisConstants.KEY_EXPONENT_VALUE,
        SGFigureElementAxisConstants.KEY_EXPONENT_LOCATION_X,
        SGFigureElementAxisConstants.KEY_EXPONENT_LOCATION_Y,
        SGFigureElementAxisConstants.KEY_NUMBER_FONT_NAME,
        SGFigureElementAxisConstants.KEY_NUMBER_FONT_SIZE,
        SGFigureElementAxisConstants.KEY_NUMBER_FONT_STYLE,
        SGFigureElementAxisConstants.KEY_NUMBER_FONT_COLOR);

    // Scale
    SGPropertyUtility.addProperty(
        map, SGFigureElementAxisConstants.KEY_AXIS_MIN_VALUE, owner.mAxis.getMinValue());
    SGPropertyUtility.addProperty(
        map, SGFigureElementAxisConstants.KEY_AXIS_MAX_VALUE, owner.mAxis.getMaxValue());
    SGPropertyUtility.addProperty(
        map,
        SGFigureElementAxisConstants.KEY_AXIS_SCALE_TYPE,
        SGUtilityText.getScaleTypeName(owner.getScaleType()));
    owner.addScaleProperties(
        map,
        SGFigureElementAxisConstants.KEY_AXIS_INVERT_COORDINATES,
        SGFigureElementAxisConstants.KEY_AUTO_CALC_NUMBER,
        SGFigureElementAxisConstants.KEY_STEP_VALUE,
        SGFigureElementAxisConstants.KEY_BASELINE_VALUE);

    // Tick Mark
    owner.addTickMarkProperties(
        map,
        SGFigureElementAxisConstants.KEY_TICK_MARK_VISIBLE,
        SGFigureElementAxisConstants.KEY_TICK_MARK_BOTHSIDES,
        SGFigureElementAxisConstants.KEY_TICK_MARK_WIDTH,
        SGFigureElementAxisConstants.KEY_MAJOR_TICK_MARK_LENGTH,
        SGFigureElementAxisConstants.KEY_MINOR_TICK_MARK_LENGTH,
        SGFigureElementAxisConstants.KEY_MINOR_TICK_MARK_NUMBER,
        SGFigureElementAxisConstants.KEY_TICK_MARK_COLOR);

    return map;
  }
}
