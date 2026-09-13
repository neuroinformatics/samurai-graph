package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import jp.riken.brain.ni.samuraigraph.base.*;

class SGAxisScaleDragHelper {

  private final SGAxisElement owner;

  public SGAxisScaleDragHelper(final SGAxisElement owner) {
    this.owner = owner;
  }

  protected SGAxisElement.AxisValueRange dragScaleNumberPerpendicular(
      MouseEvent e, SGAxisElement.ElementLineAxis line) {
    final boolean invCoord = owner.isInvertCoordinates();
    final SGAxisElement.ElementStringNumber el =
        (SGAxisElement.ElementStringNumber) owner.mDraggingElement;
    SGAxisElement.ValueInScale value = owner.getValueTempInScale(el.mValue);
    final double minValueTempInScale = value.minValueTempInScale;
    final double maxValueTempInScale = value.maxValueTempInScale;
    final double oldValueInScale = value.oldValueInScale;
    final Rectangle2D rect = el.getElementBounds();

    float draggedCoordinate =
        e.getY() - owner.mAxisElement.mPressedElementOrigin.y + (float) rect.getHeight() / 2.0f;
    final float minCoordinate = line.getEnd().y;
    final float maxCoordinate = line.getStart().y;
    if (invCoord) {
      if (draggedCoordinate <= minCoordinate) {
        return null;
      }
      if (draggedCoordinate > maxCoordinate) {
        draggedCoordinate = maxCoordinate;
      }
    } else {
      if (draggedCoordinate >= maxCoordinate) {
        return null;
      }
      if (draggedCoordinate < minCoordinate) {
        draggedCoordinate = minCoordinate;
      }
    }

    final double valueInScale;
    if (invCoord) {
      valueInScale =
          minValueTempInScale
              + (maxValueTempInScale - minValueTempInScale)
                  * (1.0 - (maxCoordinate - draggedCoordinate) / (maxCoordinate - minCoordinate));
    } else {
      valueInScale =
          minValueTempInScale
              + (maxValueTempInScale - minValueTempInScale)
                  * (1.0 - (draggedCoordinate - minCoordinate) / (maxCoordinate - minCoordinate));
    }

    // get new range
    double minValueInScale = minValueTempInScale;
    double maxValueInScale =
        minValueTempInScale
            + (maxValueTempInScale - minValueTempInScale)
                * (oldValueInScale - minValueTempInScale)
                / (valueInScale - minValueTempInScale);
    minValueInScale = SGUtilityNumber.getNumberInRangeOrder(minValueInScale, owner.mAxis);
    maxValueInScale = SGUtilityNumber.getNumberInRangeOrder(maxValueInScale, owner.mAxis);

    final float y = (float) (draggedCoordinate - rect.getHeight() / 2.0);
    el.setLocation(el.getLocation().x, y);

    final float ratio =
        (float) ((maxValueInScale - minValueInScale) / (maxValueTempInScale - minValueTempInScale));
    if (ratio < 0.05) {
      return null;
    }

    return owner.createValueRange(minValueInScale, maxValueInScale);
  }

  protected SGAxisElement.AxisValueRange dragScaleLinePerpendicular(
      MouseEvent e, SGAxisElement.ElementLineAxis line) {
    final SGAxisElement.ElementLineTickMark el =
        (SGAxisElement.ElementLineTickMark) owner.mDraggingElement;
    SGAxisElement.ValueInScale value = owner.getValueTempInScale(el.mValue);
    final double minValueTempInScale = value.minValueTempInScale;
    final double maxValueTempInScale = value.maxValueTempInScale;
    final double oldValueInScale = value.oldValueInScale;

    // calculate the location of the point at which the mouse button is dragged
    float draggedCoordinate = e.getY();
    if (owner.mAxisElement.mPressedElementOrigin.y == draggedCoordinate) {
      return null;
    }

    final float minCoordinate = line.getEnd().y;
    final float maxCoordinate = line.getStart().y;
    if (draggedCoordinate > maxCoordinate) {
      draggedCoordinate = maxCoordinate;
    }
    if (draggedCoordinate < minCoordinate) {
      draggedCoordinate = minCoordinate;
    }
    final double draggedValue;
    if (owner.isInvertCoordinates()) {
      draggedValue =
          minValueTempInScale
              + (maxValueTempInScale - minValueTempInScale)
                  * (1.0 - (maxCoordinate - draggedCoordinate) / (maxCoordinate - minCoordinate));
    } else {
      draggedValue =
          minValueTempInScale
              + (maxValueTempInScale - minValueTempInScale)
                  * (1.0 - (draggedCoordinate - minCoordinate) / (maxCoordinate - minCoordinate));
    }
    final double diff = Math.abs(draggedValue - oldValueInScale);

    // get new range
    double minValueInScale;
    double maxValueInScale;
    if (draggedValue > oldValueInScale) {
      minValueInScale = minValueTempInScale - diff;
      maxValueInScale = maxValueTempInScale - diff;
    } else {
      minValueInScale = minValueTempInScale + diff;
      maxValueInScale = maxValueTempInScale + diff;
    }
    minValueInScale = SGUtilityNumber.getNumberInRangeOrder(minValueInScale, owner.mAxis);
    maxValueInScale = SGUtilityNumber.getNumberInRangeOrder(maxValueInScale, owner.mAxis);

    return owner.createValueRange(minValueInScale, maxValueInScale);
  }

  protected void setScaleRange(
      SGPropertyMap map,
      String key,
      String value,
      final boolean dateMode,
      SGPropertyResults result) {
    String[] strArray = SGUtilityText.getStringsInBracket(value);
    if (strArray == null) {
      result.putResult(SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
      return;
    }
    final SGAxisValue min;
    final SGAxisValue max;
    if (dateMode) {
      SGDate dMin = null;
      SGDate dMax = null;
      if (strArray.length >= 2) {
        dMin = SGUtilityText.getDate(strArray[0]);
        dMax = SGUtilityText.getDate(strArray[1]);
      }
      if (dMin == null || dMax == null) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
      min = new SGAxisDateValue(dMin);
      max = new SGAxisDateValue(dMax);
    } else {
      Double dMin = null;
      Double dMax = null;
      if (strArray.length >= 2) {
        dMin = SGUtilityText.getDouble(strArray[0]);
        dMax = SGUtilityText.getDouble(strArray[1]);
      }
      if (dMin == null || dMax == null) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
      if (SGUtility.isValidPropertyValue(dMin.doubleValue()) == false) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
      if (SGUtility.isValidPropertyValue(dMax.doubleValue()) == false) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
      min = new SGAxisDoubleValue(dMin);
      max = new SGAxisDoubleValue(dMax);
    }
    if (strArray.length == 2) {
      if (owner.setScale(min, max) == false) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
    } else if (strArray.length == 3) {
      final int scaleType = SGUtilityText.getScaleType(strArray[2]);
      if (scaleType == -1) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
      if (owner.setScale(min, max, scaleType) == false) {
        result.putResult(
            SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
        return;
      }
    } else {
      result.putResult(SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.INVALID_INPUT_VALUE);
      return;
    }
    result.putResult(SGAxisConstants.COM_AXIS_SCALE_RANGE, SGPropertyResults.SUCCEEDED);
  }
}
