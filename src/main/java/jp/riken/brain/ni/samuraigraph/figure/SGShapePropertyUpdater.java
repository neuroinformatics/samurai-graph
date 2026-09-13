package jp.riken.brain.ni.samuraigraph.figure;

import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.*;

class SGShapePropertyUpdater implements SGIShapeConstants {

  private final SGFigureElementShape owner;

  public SGShapePropertyUpdater(final SGFigureElementShape owner) {
    this.owner = owner;
  }

  public SGPropertyResults setChildProperties(final int id, SGPropertyMap map) {

    SGIChildObject child = owner.getVisibleChild(id);

    // add a child object if it does not exist
    boolean shapeTypeFlag = false;
    if (child == null) {
      String sType = null;
      String sAxisX = null;
      String sAxisY = null;
      Iterator<String> itr = map.getKeyIterator();
      while (itr.hasNext()) {
        String key = itr.next();
        if (COM_SHAPE_TYPE.equalsIgnoreCase(key)) {
          sType = map.getValueString(key);
        } else if (COM_SHAPE_AXIS_X.equalsIgnoreCase(key)) {
          sAxisX = map.getValueString(key);
        } else if (COM_SHAPE_AXIS_Y.equalsIgnoreCase(key)) {
          sAxisY = map.getValueString(key);
        }
      }
      if (sType == null) {
        return null;
      }
      final int type;
      if (SHAPE_TYPE_RECTANGLE.equalsIgnoreCase(sType)) {
        type = SGIFigureElementShape.RECTANGLE;
      } else if (SHAPE_TYPE_ELLIPSE.equalsIgnoreCase(sType)) {
        type = SGIFigureElementShape.ELLIPSE;
      } else if (SHAPE_TYPE_ARROW.equalsIgnoreCase(sType)) {
        type = SGIFigureElementShape.ARROW;
      } else if (SHAPE_TYPE_LINE.equalsIgnoreCase(sType)) {
        type = SGIFigureElementShape.LINE;
      } else {
        return null;
      }

      // get axes
      String xAxisName = (sAxisX != null) ? sAxisX : DEFAULT_SHAPE_HORIZONTAL_AXIS;
      String yAxisName = (sAxisY != null) ? sAxisY : DEFAULT_SHAPE_VERTICAL_AXIS;
      SGAxis xAxis = owner.mAxisElement.getAxis(xAxisName);
      SGAxis yAxis = owner.mAxisElement.getAxis(yAxisName);
      if (xAxis == null || yAxis == null) {
        return null;
      }

      itr = map.getKeyIterator();
      switch (type) {
        case SGIFigureElementShape.RECTANGLE:
        case SGIFigureElementShape.ELLIPSE:
          String sLeft = null;
          String sRight = null;
          String sBottom = null;
          String sTop = null;
          while (itr.hasNext()) {
            Object keyObj = itr.next();
            String key = keyObj.toString();
            if (COM_RECTANGLE_LEFT_X.equalsIgnoreCase(key)) {
              sLeft = map.getValueString(key);
            } else if (COM_RECTANGLE_RIGHT_X.equalsIgnoreCase(key)) {
              sRight = map.getValueString(key);
            } else if (COM_RECTANGLE_BOTTOM_Y.equalsIgnoreCase(key)) {
              sBottom = map.getValueString(key);
            } else if (COM_RECTANGLE_TOP_Y.equalsIgnoreCase(key)) {
              sTop = map.getValueString(key);
            }
          }
          if (sLeft == null) {
            return null;
          }
          if (sRight == null) {
            return null;
          }
          if (sTop == null) {
            return null;
          }
          if (sBottom == null) {
            return null;
          }
          Double left = SGUtilityText.getDouble(sLeft);
          Double right = SGUtilityText.getDouble(sRight);
          Double top = SGUtilityText.getDouble(sTop);
          Double bottom = SGUtilityText.getDouble(sBottom);
          if (left == null) {
            return null;
          }
          if (right == null) {
            return null;
          }
          if (top == null) {
            return null;
          }
          if (bottom == null) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(left.doubleValue()) == false) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(right.doubleValue()) == false) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(top.doubleValue()) == false) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(bottom.doubleValue()) == false) {
            return null;
          }

          SGShapeRect rect = null;
          if (type == SGIFigureElementShape.RECTANGLE) {
            rect = new SGShapeRect(owner);
          } else {
            rect = owner.new Ellipse();
          }
          if (owner.addRectangularShape(id, rect, left, right, top, bottom, xAxis, yAxis)
              == false) {
            return null;
          }
          break;
        case SGIFigureElementShape.ARROW:
        case SGIFigureElementShape.LINE:
          String sStartX = null;
          String sStartY = null;
          String sEndX = null;
          String sEndY = null;
          while (itr.hasNext()) {
            Object keyObj = itr.next();
            String key = keyObj.toString();
            if (COM_ARROW_START_X.equalsIgnoreCase(key)) {
              sStartX = map.getValueString(key);
            } else if (COM_ARROW_START_Y.equalsIgnoreCase(key)) {
              sStartY = map.getValueString(key);
            } else if (COM_ARROW_END_X.equalsIgnoreCase(key)) {
              sEndX = map.getValueString(key);
            } else if (COM_ARROW_END_Y.equalsIgnoreCase(key)) {
              sEndY = map.getValueString(key);
            }
          }
          if (sStartX == null) {
            return null;
          }
          if (sStartY == null) {
            return null;
          }
          if (sEndX == null) {
            return null;
          }
          if (sEndY == null) {
            return null;
          }
          Double startX = SGUtilityText.getDouble(sStartX);
          Double startY = SGUtilityText.getDouble(sStartY);
          Double endX = SGUtilityText.getDouble(sEndX);
          Double endY = SGUtilityText.getDouble(sEndY);
          if (startX == null) {
            return null;
          }
          if (startY == null) {
            return null;
          }
          if (endX == null) {
            return null;
          }
          if (endY == null) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(startX.doubleValue()) == false) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(startY.doubleValue()) == false) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(endX.doubleValue()) == false) {
            return null;
          }
          if (SGUtility.isValidPropertyValue(endY.doubleValue()) == false) {
            return null;
          }

          SGShapeArrow arrow = new SGShapeArrow(owner);
          if (type == SGIFigureElementShape.ARROW) {
            arrow.setStartHeadType(DEFAULT_SHAPE_ARROW_START_HEAD_TYPE);
            arrow.setEndHeadType(DEFAULT_SHAPE_ARROW_END_HEAD_TYPE);
          } else {
            arrow.setStartHeadType(SGIArrowConstants.SYMBOL_TYPE_VOID);
            arrow.setEndHeadType(SGIArrowConstants.SYMBOL_TYPE_VOID);
          }
          if (owner.addArrowShape(id, arrow, startX, startY, endX, endY, xAxis, yAxis) == false) {
            return null;
          }
          break;
        default:
          return null;
      }

      child = owner.getVisibleChild(id);
      shapeTypeFlag = true;
    }

    // set properties to the child object
    SGPropertyResults result = child.setProperties(map);
    if (shapeTypeFlag) {
      result.putResult(COM_SHAPE_TYPE, SGPropertyResults.SUCCEEDED);
    }

    return result;
  }
}
