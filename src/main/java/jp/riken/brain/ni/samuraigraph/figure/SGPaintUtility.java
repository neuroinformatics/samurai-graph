package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;

import java.awt.Color;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.Element;

public class SGPaintUtility {

  public static SGIPaint readProperty(final Element el) {

    Number transparent = null;
    Boolean transparentFlag = null;
    String str = null;
    Color fillColor = null;
    Color patternColor = null;
    String patternType = null;
    Color gradationColor1 = null;
    Color gradationColor2 = null;
    String gradationDirection = null;
    String gradationOrder = null;
    String paintStyle = null;

    // transparent
    str = el.getAttribute(KEY_TRANSPARENT);
    if (str.length() != 0) {
      transparent = SGUtilityText.getInteger(str, TRANSPARENCY_UNIT);
      transparentFlag = SGUtilityText.getBoolean(str);
    }

    // fill paint
    str = el.getAttribute(KEY_FILL_COLOR);
    if (str.length() != 0) {
      fillColor = SGUtilityText.parseColor(str);
    }

    // pattern paint
    str = el.getAttribute(KEY_PATTERN_COLOR);
    if (str.length() != 0) {
      patternColor = SGUtilityText.parseColor(str);
    }
    str = el.getAttribute(KEY_PATTERN_TYPE);
    if (str.length() != 0) {
      patternType = str.trim();
    }

    // gradation paint
    str = el.getAttribute(KEY_GRADATION_COLOR1);
    if (str.length() != 0) {
      gradationColor1 = SGUtilityText.parseColor(str);
    }
    str = el.getAttribute(KEY_GRADATION_COLOR2);
    if (str.length() != 0) {
      gradationColor2 = SGUtilityText.parseColor(str);
    }
    str = el.getAttribute(KEY_GRADATION_DIRECTION);
    if (str.length() != 0) {
      gradationDirection = str.trim();
    }
    str = el.getAttribute(KEY_GRADATION_ORDER);
    if (str.length() != 0) {
      gradationOrder = str.trim();
    }

    // selectable paint
    str = el.getAttribute(KEY_PAINT_STYLE);
    if (str.length() != 0) {
      paintStyle = str.trim();
    }

    if (null != paintStyle) {
      SGSelectablePaint paint = new SGSelectablePaint();
      if (null != transparentFlag) {
        if (transparentFlag.booleanValue() == false) {
          paint.setTransparency(SGSelectablePaint.ALL_OPAQUE_VALUE);
        } else {
          paint.setTransparency(SGSelectablePaint.ALL_TRANSPARENT_VALUE);
        }
      } else {
        if (null != transparent) {
          paint.setTransparency(transparent.intValue());
        }
      }
      if (null != fillColor) {
        paint.setFillColor(fillColor);
      }
      if (null != patternType) {
        if (null != patternColor) {
          paint.setPatternColor(patternColor);
        }
        Integer typeIndex = SGPatternPaint.getTypeFromName(patternType);
        if (null != typeIndex) {
          paint.setPatternIndex(typeIndex.intValue());
        }
      }
      SGGradationPaint gPaint = new SGGradationPaint();
      if (null != gradationColor1 && null != gradationColor2) {
        gPaint.setColors(new Color[] {gradationColor1, gradationColor2});
      }
      if (null != gradationDirection) {
        Integer directionIndex = SGGradationPaint.getDirectionIndex(gradationDirection);
        if (null != directionIndex) {
          gPaint.setDirection(directionIndex.intValue());
        }
      }
      if (null != gradationOrder) {
        Integer orderIndex = SGGradationPaint.getOrderIndex(gradationOrder);
        if (null != orderIndex) {
          gPaint.setOrder(orderIndex.intValue());
        }
      }
      paint.setGradationPaint(gPaint);

      Integer paintStyleIndex = SGSelectablePaint.getStyleIndex(paintStyle);
      if (null != paintStyleIndex) {
        paint.setSelectedPaintStyle(paintStyleIndex.intValue());
        return paint;
      }
    } else {
      if (null != fillColor) {
        SGFillPaint paint = new SGFillPaint();
        if (null != transparent) {
          paint.setTransparency(transparent.intValue());
        }
        paint.setColor(fillColor);
        return paint;
      } else if (null != patternType) {
        SGPatternPaint paint = new SGPatternPaint();
        if (null != transparent) {
          paint.setTransparency(transparent.intValue());
        }
        if (null != patternColor) {
          paint.setColor(patternColor);
        }
        Integer typeIndex = SGPatternPaint.getTypeFromName(patternType);
        if (null != typeIndex) {
          paint.setTypeIndex(typeIndex.intValue());
          return paint;
        }
      } else if (null != gradationColor1
          && null != gradationColor2
          && null != gradationDirection
          && null != gradationOrder) {
        SGGradationPaint paint = new SGGradationPaint();
        if (null != transparent) {
          paint.setTransparency(transparent.intValue());
        }
        paint.setColors(new Color[] {gradationColor1, gradationColor2});
        Integer directionIndex = SGGradationPaint.getDirectionIndex(gradationDirection);
        if (null != directionIndex) {
          paint.setDirection(directionIndex.intValue());
        }
        Integer orderIndex = SGGradationPaint.getOrderIndex(gradationOrder);
        if (null != orderIndex) {
          paint.setOrder(orderIndex.intValue());
        }
        return paint;
      }
    }

    return null;
  }
}
