package jp.riken.brain.ni.samuraigraph.figure;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisConstants;

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
              SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT);

    } else {

      // get an array of numbers
      axisValueArray =
          SGUtilityNumber.calcStepValueSorted(
              new SGAxisDoubleValue(minValue),
              new SGAxisDoubleValue(maxValue),
              baseline,
              step,
              SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT);

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
    SGPropertyUtility.addProperty(map, owner.COM_AXIS_VISIBLE, owner.isVisible());

    // frame line
    owner.addAxisLineProperties(
        map,
        owner.getLineVisibleCommandKey(),
        owner.getLineWidthCommandKey(),
        owner.getSpaceLineAndNumberCommandKey(),
        owner.getLineColorCommandKey());

    // title
    SGPropertyUtility.addQuotedStringProperty(
        map, owner.COM_AXIS_TITLE_TEXT, owner.getTitleString());
    owner.addTitleProperties(
        map,
        owner.COM_AXIS_TITLE_VISIBLE,
        owner.COM_AXIS_SPACE_TITLE_AND_NUMBER,
        owner.COM_AXIS_TITLE_CENTER_SHIFT,
        owner.COM_AXIS_TITLE_FONT_NAME,
        owner.COM_AXIS_TITLE_FONT_SIZE,
        owner.COM_AXIS_TITLE_FONT_STYLE,
        owner.COM_AXIS_TITLE_FONT_COLOR);

    // number
    owner.addNumberProperties(
        map,
        owner.COM_AXIS_NUMBER_VISIBLE,
        owner.COM_AXIS_NUMBER_INTEGER,
        owner.COM_AXIS_NUMBER_ANGLE,
        owner.COM_AXIS_EXPONENT_VISIBLE,
        owner.COM_AXIS_EXPONENT_VALUE,
        owner.COM_AXIS_EXPONENT_LOCATION_X,
        owner.COM_AXIS_EXPONENT_LOCATION_Y,
        owner.COM_AXIS_NUMBER_FONT_NAME,
        owner.COM_AXIS_NUMBER_FONT_SIZE,
        owner.COM_AXIS_NUMBER_FONT_STYLE,
        owner.COM_AXIS_NUMBER_FONT_COLOR);

    // scale
    StringBuilder sbScale = new StringBuilder();
    sbScale.append('(');
    sbScale.append(owner.getMinValue());
    sbScale.append(',');
    sbScale.append(owner.getMaxValue());
    sbScale.append(',');
    sbScale.append(SGUtilityText.getScaleTypeName(owner.getScaleType()));
    sbScale.append(')');
    SGPropertyUtility.addProperty(map, owner.COM_AXIS_SCALE_RANGE, sbScale.toString());
    owner.addScaleProperties(
        map,
        owner.COM_AXIS_INVERT_COORDINATES,
        owner.COM_AXIS_SCALE_AUTO,
        owner.COM_AXIS_SCALE_STEP,
        owner.COM_AXIS_SCALE_BASE);

    // tick mark
    owner.addTickMarkProperties(
        map,
        owner.COM_AXIS_TICK_MARK_VISIBLE,
        owner.COM_AXIS_TICK_MARK_BOTHSIDES,
        owner.COM_AXIS_TICK_MARK_WIDTH,
        owner.COM_AXIS_MAJOR_TICK_MARK_LENGTH,
        owner.COM_AXIS_MINOR_TICK_MARK_LENGTH,
        owner.COM_AXIS_MINOR_TICK_MARK_NUMBER,
        owner.COM_AXIS_TICK_MARK_COLOR);

    return map;
  }

  public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    SGPropertyMap map = new SGPropertyMap();

    // visible
    SGPropertyUtility.addProperty(map, SGIDrawingElementConstants.KEY_VISIBLE, owner.isVisible());

    // axis line
    owner.addAxisLineProperties(
        map,
        owner.getLineVisibleCommandKey(),
        owner.getLineWidthPropertyFileKey(),
        owner.getSpaceLineAndNumberPropertyFileKey(),
        owner.getLineColorPropertyFileKey());

    // Title
    SGPropertyUtility.addProperty(
        map, SGIFigureElementAxisConstants.KEY_TITLE_TEXT, owner.getTitleString());
    owner.addTitleProperties(
        map,
        SGIFigureElementAxisConstants.KEY_TITLE_VISIBLE,
        SGIFigureElementAxisConstants.KEY_SPACE_TITLE_AND_NUMBERS,
        SGIFigureElementAxisConstants.KEY_TITLE_SHIFT_FROM_CENTER,
        SGIFigureElementAxisConstants.KEY_TITLE_FONT_NAME,
        SGIFigureElementAxisConstants.KEY_TITLE_FONT_SIZE,
        SGIFigureElementAxisConstants.KEY_TITLE_FONT_STYLE,
        SGIFigureElementAxisConstants.KEY_TITLE_FONT_COLOR);

    // Number
    owner.addNumberProperties(
        map,
        SGIFigureElementAxisConstants.KEY_NUMBER_VISIBLE,
        SGIFigureElementAxisConstants.KEY_NUMBER_INTEGER,
        SGIFigureElementAxisConstants.KEY_NUMBER_ANGLE,
        SGIFigureElementAxisConstants.KEY_EXPONENT_VISIBLE,
        SGIFigureElementAxisConstants.KEY_EXPONENT_VALUE,
        SGIFigureElementAxisConstants.KEY_EXPONENT_LOCATION_X,
        SGIFigureElementAxisConstants.KEY_EXPONENT_LOCATION_Y,
        SGIFigureElementAxisConstants.KEY_NUMBER_FONT_NAME,
        SGIFigureElementAxisConstants.KEY_NUMBER_FONT_SIZE,
        SGIFigureElementAxisConstants.KEY_NUMBER_FONT_STYLE,
        SGIFigureElementAxisConstants.KEY_NUMBER_FONT_COLOR);

    // Scale
    SGPropertyUtility.addProperty(
        map, SGIFigureElementAxisConstants.KEY_AXIS_MIN_VALUE, owner.mAxis.getMinValue());
    SGPropertyUtility.addProperty(
        map, SGIFigureElementAxisConstants.KEY_AXIS_MAX_VALUE, owner.mAxis.getMaxValue());
    SGPropertyUtility.addProperty(
        map,
        SGIFigureElementAxisConstants.KEY_AXIS_SCALE_TYPE,
        SGUtilityText.getScaleTypeName(owner.getScaleType()));
    owner.addScaleProperties(
        map,
        SGIFigureElementAxisConstants.KEY_AXIS_INVERT_COORDINATES,
        SGIFigureElementAxisConstants.KEY_AUTO_CALC_NUMBER,
        SGIFigureElementAxisConstants.KEY_STEP_VALUE,
        SGIFigureElementAxisConstants.KEY_BASELINE_VALUE);

    // Tick Mark
    owner.addTickMarkProperties(
        map,
        SGIFigureElementAxisConstants.KEY_TICK_MARK_VISIBLE,
        SGIFigureElementAxisConstants.KEY_TICK_MARK_BOTHSIDES,
        SGIFigureElementAxisConstants.KEY_TICK_MARK_WIDTH,
        SGIFigureElementAxisConstants.KEY_MAJOR_TICK_MARK_LENGTH,
        SGIFigureElementAxisConstants.KEY_MINOR_TICK_MARK_LENGTH,
        SGIFigureElementAxisConstants.KEY_MINOR_TICK_MARK_NUMBER,
        SGIFigureElementAxisConstants.KEY_TICK_MARK_COLOR);

    return map;
  }
}
