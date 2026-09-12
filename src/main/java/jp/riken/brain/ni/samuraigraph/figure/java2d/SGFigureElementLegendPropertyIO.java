package jp.riken.brain.ni.samuraigraph.figure.java2d;

import static jp.riken.brain.ni.samuraigraph.base.SGIFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend.*;

import java.awt.*;
import java.awt.geom.*;
import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SGFigureElementLegendPropertyIO implements SGIStringConstants, SGILegendConstants {

  private final SGFigureElementLegend legend;

  public SGFigureElementLegendPropertyIO(final SGFigureElementLegend legend) {
    this.legend = legend;
  }

  public SGProperties getProperties() {
    SGProperties p = new LegendProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  public boolean getProperties(final SGProperties p) {
    if ((p instanceof LegendProperties) == false) {
      return false;
    }

    LegendProperties lp = (LegendProperties) p;

    lp.x = legend.mLegendX;
    lp.y = legend.mLegendY;

    lp.visible = legend.isVisible();
    lp.frameLineVisible = legend.isFrameVisible();
    lp.frameLineWidth = legend.getFrameLineWidth();
    lp.frameLineColor = legend.getFrameColor();
    lp.backgroundPaint.setColor(legend.getBackgroundColor());
    lp.backgroundPaint.setTransparency(legend.getBackgroundTransparency());
    lp.fontName = legend.getFontName();
    lp.fontSize = legend.getFontSize();
    lp.fontStyle = legend.getFontStyle();
    lp.stringColor = legend.getFontColor();
    lp.symbolSpan = legend.getSymbolSpan();

    lp.xAxis = legend.mXAxis;
    lp.yAxis = legend.mYAxis;

    lp.visibleElementGroupList = new ArrayList<SGIChildObject>(legend.getVisibleChildList());

    return true;
  }

  public boolean setProperties(final SGProperties p) {

    if ((p instanceof LegendProperties) == false) return false;

    LegendProperties wp = (LegendProperties) p;

    if (this.setCommonProperties(wp) == false) {
      return false;
    }

    return true;
  }

  boolean setCommonProperties(final LegendProperties p) {

    legend.mLegendX = p.x;
    legend.mLegendY = p.y;

    legend.setVisible(p.visible);
    legend.setFrameVisible(p.frameLineVisible);
    legend.setFrameLineWidth(p.frameLineWidth);
    legend.setFrameLineColor(p.frameLineColor);
    legend.setBackgroundColor(p.backgroundPaint.getColor());
    legend.setBackgroundTransparent(p.backgroundPaint.getTransparencyPercent());
    legend.setFontName(p.fontName);
    legend.setFontSize(p.fontSize);
    legend.setFontStyle(p.fontStyle);
    legend.setFontColor(p.stringColor);
    legend.setSymbolSpan(p.symbolSpan);

    legend.mXAxis = p.xAxis;
    legend.mYAxis = p.yAxis;

    boolean flag;
    flag = legend.setVisibleChildListForPropertyIO(p.visibleElementGroupList);
    if (!flag) {
      return false;
    }

    return true;
  }

  public boolean writeProperty(final Element el, SGExportParameter params) {
    SGPropertyMap map = legend.getPropertyFileMap(params);
    map.setToElement(el);
    return true;
  }

  public Element[] createElement(final Document document, SGExportParameter params) {
    Element el = legend.createThisElementForPropertyIO(document, params);
    return new Element[] {el};
  }

  public boolean readProperty(final Element element, final String versionNumber) {
    String str = null;
    Number num = null;
    Color cl = null;
    Boolean b = null;

    // set legend visible
    str = element.getAttribute(SGIFigureElementLegend.KEY_LEGEND_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (legend.setVisible(b.booleanValue()) == false) {
        return false;
      }
    }

    // set axes
    str = element.getAttribute(KEY_X_AXIS_POSITION);
    if (str.length() != 0) {
      SGAxis xAxis = legend.mAxisElement.getAxis(str);
      legend.mXAxis = xAxis;
    }

    str = element.getAttribute(KEY_Y_AXIS_POSITION);
    if (str.length() != 0) {
      SGAxis yAxis = legend.mAxisElement.getAxis(str);
      legend.mYAxis = yAxis;
    }

    str = element.getAttribute(KEY_X_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double xValue = num.doubleValue();
      if (legend.mXAxis.isValidValue(xValue) == false) {
        return false;
      }
      if (legend.setXValue(xValue) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_Y_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getDouble(str);
      if (num == null) {
        return false;
      }
      final double yValue = num.doubleValue();
      if (legend.mYAxis.isValidValue(yValue) == false) {
        return false;
      }
      if (legend.setYValue(yValue) == false) {
        return false;
      }
    }

    // set frame visible
    str = element.getAttribute(SGIFigureElementLegend.KEY_FRAME_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (legend.setFrameVisible(b.booleanValue()) == false) {
        return false;
      }
    }

    // set frame line width
    str = element.getAttribute(SGIFigureElementLegend.KEY_FRAME_LINE_WIDTH);
    if (str.length() != 0) {
      StringBuilder uFrameLineWidth = new StringBuilder();
      num = SGUtilityText.getNumber(str, uFrameLineWidth);
      if (num == null) {
        return false;
      }
      if (legend.setFrameLineWidth(num.floatValue(), uFrameLineWidth.toString()) == false) {
        return false;
      }
    }

    // set frame line color
    str = element.getAttribute(SGIFigureElementLegend.KEY_FRAME_LINE_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (legend.setFrameLineColor(cl) == false) {
        return false;
      }
    }

    // background color
    str = element.getAttribute(SGIFigureElementLegend.KEY_BACKGROUND_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (legend.setBackgroundColor(cl) == false) {
        return false;
      }
    }

    // transparent
    str = element.getAttribute(SGIFigureElementLegend.KEY_BACKGROUND_TRANSPARENT);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b != null) {
        if (b.booleanValue() == false) {
          if (legend.setBackgroundTransparent(SGTransparentPaint.ALL_OPAQUE_VALUE) == false) {
            return false;
          }
        } else {
          if (legend.setBackgroundTransparent(SGTransparentPaint.ALL_TRANSPARENT_VALUE) == false) {
            return false;
          }
        }
      } else {
        num = SGUtilityText.getInteger(str, SGIConstants.percent);
        if (num == null) {
          return false;
        }
        if (legend.setBackgroundTransparent(num.intValue()) == false) {
          return false;
        }
      }
    }

    // set font name
    str = element.getAttribute(KEY_FONT_NAME);
    if (str.length() != 0) {
      final String fontName = str;
      if (legend.setFontName(fontName) == false) {
        return false;
      }
    }

    // set font size
    str = element.getAttribute(KEY_FONT_SIZE);
    if (str.length() != 0) {
      StringBuilder uFontSize = new StringBuilder();
      num = SGUtilityText.getNumber(str, uFontSize);
      if (num == null) {
        return false;
      }
      if (legend.setFontSize(num.floatValue(), uFontSize.toString()) == false) {
        return false;
      }
    }

    // set font style
    str = element.getAttribute(KEY_FONT_STYLE);
    if (str.length() != 0) {
      final Integer fontStyle = SGUtilityText.getFontStyle(str);
      if (fontStyle == null) {
        return false;
      }
      if (legend.setFontStyle(fontStyle.intValue()) == false) {
        return false;
      }
    }

    // set the font color
    str = element.getAttribute(KEY_STRING_COLORS);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (legend.setFontColor(cl) == false) {
        return false;
      }
    }

    // set symbol span
    // SymbolSpan is appeared since ver. 0.9.1
    str = element.getAttribute(KEY_SYMBOL_SPAN);
    float symbolSpan;
    if (str.length() != 0) {
      num = SGUtilityText.getLengthInPoint(str);
      if (num == null) {
        return false;
      }
      symbolSpan = num.floatValue();
    } else {
      // for previous version before 0.9.1
      num =
          Float.valueOf(
              (float) SGUtilityText.convertToPoint(DEFAULT_LEGEND_SYMBOL_SPAN, SYMBOL_SPAN_UNIT));
      symbolSpan = num.floatValue();
    }
    if (legend.setSymbolSpan(symbolSpan) == false) {
      return false;
    }

    return true;
  }
}
