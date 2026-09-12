package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGIFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGIScaleConstants.*;

import java.awt.*;
import java.awt.Color;
import java.util.*;
import java.util.Iterator;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGPeriod;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

class SGAxisElementPropertyIO implements SGIStringConstants, SGILegendConstants {

  private final SGAxisElement axis;

  public SGAxisElementPropertyIO(final SGAxisElement axis) {
    this.axis = axis;
  }

  public Element createElement(final Document document, SGExportParameter params) {
    Element element = document.createElement(axis.getTagName());
    if (this.writeProperty(element, params) == false) {
      return null;
    }
    return element;
  }

  public boolean writeProperty(final Element el, SGExportParameter params) {
    SGPropertyMap map = axis.getPropertyFileMap(params);
    map.setToElement(el);
    return true;
  }

  protected boolean readProperties(final Element element, final String versionNumber) {
    String str = null;
    Number num = null;
    Boolean b = null;
    Color cl = null;

    // visible
    str = element.getAttribute(KEY_VISIBLE);
    if (str.length() == 0) {
      // for backward compatibility
      // version number <= 2.0.0
      str = element.getAttribute("AxisVisible");
    }
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      final boolean axisVisible = b.booleanValue();
      if (axis.setVisible(axisVisible) == false) {
        return false;
      }
    }

    //
    // Axis Line
    //

    str = element.getAttribute(axis.getLineVisiblePropertyFileKey());
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b != null) {
        if (axis.setAxisLineVisible(b.booleanValue()) == false) {
          return false;
        }
      }
    }

    str = element.getAttribute(axis.getLineWidthPropertyFileKey());
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setAxisLineWidth(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(axis.getLineColorPropertyFileKey());
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (axis.setAxisLineColor(cl) == false) {
        return false;
      }
    }

    str = element.getAttribute(axis.getSpaceLineAndNumberPropertyFileKey());
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setSpaceAxisLineAndNumbers(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    //
    // Title
    //

    str = element.getAttribute(KEY_TITLE_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setTitleVisible(b.booleanValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TITLE_TEXT);
    if (str != null) {
      // modifies the text string for previous releases older than 2_0_0
      str = SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str, versionNumber);
    }
    final String titleString = str;
    if (axis.setTitleText(titleString) == false) {
      return false;
    }

    str = element.getAttribute(KEY_SPACE_TITLE_AND_NUMBERS);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setSpaceTitleAndNumbers(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TITLE_SHIFT_FROM_CENTER);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setTitleShiftFromCenter(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TITLE_FONT_NAME);
    if (str.length() != 0) {
      if (axis.setTitleFontName(str) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TITLE_FONT_SIZE);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setTitleFontSize(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TITLE_FONT_STYLE);
    if (str.length() != 0) {
      Integer fontStyle = SGUtilityText.getFontStyle(str);
      if (fontStyle == null) {
        return false;
      }
      if (axis.setTitleFontStyle(fontStyle.intValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TITLE_FONT_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (axis.setTitleFontColor(cl) == false) {
        return false;
      }
    }

    //
    // Number
    //

    str = element.getAttribute(KEY_NUMBER_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setNumbersVisible(b.booleanValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_NUMBER_INTEGER);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setNumbersInteger(b.booleanValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(SGIFigureElementAxis.KEY_NUMBER_ANGLE);
    if (str.length() != 0) {
      num = SGUtilityText.getFloat(str);
      if (num == null) {
        return false;
      }
      if (axis.setNumbersAngle(num.floatValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setExponentVisible(b.booleanValue()) == false) {
        return false;
      }
    }

    // exponent value
    str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_VALUE);
    if (str.length() != 0) {
      num = SGUtilityText.getInteger(str);
      if (num == null) {
        return false;
      }
      if (axis.setExponent(num.intValue()) == false) {
        return false;
      }
    }

    // exponent location
    str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_LOCATION_X);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setExponentLocationX(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }
    str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_LOCATION_Y);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setExponentLocationY(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_NUMBER_FONT_NAME);
    if (str.length() != 0) {
      if (axis.setNumberFontName(str) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_NUMBER_FONT_SIZE);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setNumberFontSize(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_NUMBER_FONT_STYLE);
    if (str.length() != 0) {
      Integer fontStyle = SGUtilityText.getFontStyle(str);
      if (fontStyle == null) {
        return false;
      }
      if (axis.setNumberFontStyle(fontStyle.intValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_NUMBER_FONT_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (axis.setNumberFontColor(cl) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_NUMBER_DATE_FORMAT);
    if (str.length() != 0) {
      if (SGDateUtility.isValidDateFormat(str) == false) {
        return false;
      }
      if (axis.setNumberDateFormat(str) == false) {
        return false;
      }
    }

    //
    // Scale
    //

    str = element.getAttribute(KEY_AXIS_INVERT_COORDINATES);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setInvertedCoordinates(b.booleanValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_AUTO_CALC_NUMBER);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setCalculateAutomatically(b.booleanValue()) == false) {
        return false;
      }
    }

    //
    // Tick Mark
    //

    str = element.getAttribute(KEY_TICK_MARK_VISIBLE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setTickMarkVisible(b.booleanValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TICK_MARK_BOTHSIDES);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      if (axis.setTickMarkBothsides(b.booleanValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TICK_MARK_WIDTH);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setTickMarkWidth(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TICK_MARK_LENGTH);
    if (str.length() != 0) {
      StringBuilder unit = new StringBuilder();
      num = SGUtilityText.getNumber(str, unit);
      if (num == null) {
        return false;
      }
      if (axis.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
        return false;
      }
      if (axis.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
        return false;
      }
    } else {
      str = element.getAttribute(KEY_MAJOR_TICK_MARK_LENGTH);
      if (str.length() != 0) {
        StringBuilder unit = new StringBuilder();
        num = SGUtilityText.getNumber(str, unit);
        if (num == null) {
          return false;
        }
        if (axis.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
          return false;
        }
      }
      str = element.getAttribute(SGIFigureElementAxis.KEY_MINOR_TICK_MARK_LENGTH);
      if (str.length() != 0) {
        StringBuilder unit = new StringBuilder();
        num = SGUtilityText.getNumber(str, unit);
        if (num == null) {
          return false;
        }
        if (axis.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
          return false;
        }
      }
    }

    // Note: set this property after tick mark length is set
    str = element.getAttribute(KEY_TICK_MARK_INSIDE);
    if (str.length() != 0) {
      b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return false;
      }
      float majorLength = Math.abs(axis.getMajorTickMarkLength());
      float minorLength = Math.abs(axis.getMinorTickMarkLength());
      if (!b) {
        majorLength *= -1;
        minorLength *= -1;
      }
      axis.setMajorTickMarkLength(majorLength);
      axis.setMinorTickMarkLength(minorLength);
    }

    str = element.getAttribute(KEY_MINOR_TICK_MARK_NUMBER);
    if (str.length() != 0) {
      num = SGUtilityText.getInteger(str);
      if (num == null) {
        return false;
      }
      if (axis.setMinorTickMarkNumber(num.intValue()) == false) {
        return false;
      }
    }

    str = element.getAttribute(KEY_TICK_MARK_COLOR);
    if (str.length() != 0) {
      cl = SGUtilityText.parseColor(str);
      if (cl == null) {
        return false;
      }
      if (axis.setTickMarkColor(cl) == false) {
        return false;
      }
    }

    return true;
  }

  protected boolean readScaleProperties(
      Element element, String versionNumber, final boolean dateMode) {
    String str = null;
    Number num = null;
    SGDate date = null;

    //
    // scale type, minimum value and maximum value
    //

    int scaleType = -1;
    str = element.getAttribute(KEY_AXIS_SCALE_TYPE);
    if (str.length() != 0) {
      scaleType = SGUtilityText.getScaleType(str);
    }

    SGAxisValue minValue = null;
    str = element.getAttribute(KEY_AXIS_MIN_VALUE);
    if (str.length() != 0) {
      if (dateMode) {
        date = SGUtilityText.getDate(str);
        if (date != null) {
          minValue = new SGAxisDateValue(date);
        }
      } else {
        num = SGUtilityText.getDouble(str);
        if (num != null) {
          minValue = new SGAxisDoubleValue(num.doubleValue());
        }
      }
    }

    SGAxisValue maxValue = null;
    str = element.getAttribute(KEY_AXIS_MAX_VALUE);
    if (str.length() != 0) {
      if (dateMode) {
        date = SGUtilityText.getDate(str);
        if (date != null) {
          maxValue = new SGAxisDateValue(date);
        }
      } else {
        num = SGUtilityText.getDouble(str);
        if (num != null) {
          maxValue = new SGAxisDoubleValue(num.doubleValue());
        }
      }
    }

    if ((scaleType != -1) && (minValue != null) && (maxValue != null)) {
      if (axis.setScale(minValue, maxValue, scaleType) == false) {
        return false;
      }
    }

    // baseline value
    str = element.getAttribute(SGIFigureElementAxis.KEY_BASELINE_VALUE);
    if (str.length() != 0) {
      SGAxisValue baseline = null;
      if (dateMode) {
        date = SGUtilityText.getDate(str);
        if (date != null) {
          baseline = new SGAxisDateValue(date);
        }
      } else {
        num = SGUtilityText.getDouble(str);
        if (num != null) {
          baseline = new SGAxisDoubleValue(num.doubleValue());
        }
      }
      if (baseline != null) {
        if (axis.setBaselineValue(baseline) == false) {
          return false;
        }
      }
    }

    // step value
    str = element.getAttribute(KEY_STEP_VALUE);
    if (str.length() != 0) {
      SGAxisStepValue step = null;
      if (dateMode) {
        SGPeriod p = SGUtilityText.getPeriod(str);
        if (p != null) {
          step = new SGAxisDateStepValue(p);
        }
      } else {
        num = SGUtilityText.getDouble(str);
        if (num != null) {
          step = new SGAxisDoubleStepValue(num.doubleValue());
        }
      }
      if (step != null) {
        if (axis.setStepValue(step) == false) {
          return false;
        }
      }
    }

    return true;
  }

  public SGProperties getProperties() {
    SGAxisElement.AxisProperties p = new SGAxisElement.AxisProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  public boolean getProperties(SGProperties p) {
    SGAxisElement.AxisProperties ap = (SGAxisElement.AxisProperties) p;

    ap.axisVisible = axis.isVisible();

    // Axis Line
    ap.axisLineVisible = axis.isAxisLineVisible();
    ap.axisLineWidth = axis.getAxisLineWidth();
    ap.axisLineColor = axis.getAxisLineColor();
    ap.spaceLineAndNumbers = axis.getSpaceAxisLineAndNumbers();

    // Title
    ap.titleVisible = axis.isTitleVisible();
    ap.titleText = axis.getTitleString();
    ap.spaceTitleAndNumbers = axis.getSpaceTitleAndNumbers();
    ap.titleShiftFromCenter = axis.getTitleShiftFromCenter();
    ap.titleFontName = axis.getTitleFontName();
    ap.titleFontSize = axis.getTitleFontSize();
    ap.titleFontStyle = axis.getTitleFontStyle();
    ap.titleFontColor = axis.getTitleFontColor();

    // Scale
    ap.dateMode = axis.getDateMode();
    ap.numberInteger = axis.isNumbersInteger();
    ap.minValue = axis.getMinValue();
    ap.maxValue = axis.getMaxValue();
    ap.scaleType = axis.getScaleType();
    ap.invertedCoordinates = axis.isInvertCoordinates();
    ap.autoCalc = axis.isScaleAuto();
    ap.stepValue = axis.getScaleStep();
    ap.baselineValue = axis.getScaleBase();
    ap.exponentVisible = axis.isExponentVisible();
    ap.exponent = axis.getExponentValue();

    // Number
    ap.numberVisible = axis.isNumbersVisible();
    ap.numberAngle = axis.getNumberAngle();
    ap.numberFontName = axis.getNumberFontName();
    ap.numberFontSize = axis.getNumberFontSize();
    ap.numberFontStyle = axis.getNumberFontStyle();
    ap.numberFontColor = axis.getNumberFontColor();
    ap.exponentLocationX = axis.getExponentLocationX();
    ap.exponentLocationY = axis.getExponentLocationY();
    ap.numberDateFormat = axis.getNumberDateFormat();

    // Tick Mark
    ap.tickMarkVisible = axis.isTickMarkVisible();
    ap.tickMarkBothsides = axis.isTickMarkBothsides();
    ap.tickMarkWidth = axis.getTickMarkWidth();
    ap.tickMarkLength = axis.getMajorTickMarkLength();
    ap.minorTickMarkNumber = axis.getMinorTickMarkNumber();
    ap.minorTickMarkLength = axis.getMinorTickMarkLength();
    ap.tickMarkColor = axis.getTickMarkColor();

    return true;
  }

  public boolean setProperties(SGProperties p) {
    SGAxisElement.AxisProperties ap = (SGAxisElement.AxisProperties) p;

    axis.setVisible(ap.axisVisible);

    // Axis Line
    axis.setAxisLineVisible(ap.axisLineVisible);
    axis.setAxisLineWidth(ap.axisLineWidth);
    axis.setAxisLineColor(ap.axisLineColor);
    axis.setSpaceAxisLineAndNumbers(ap.spaceLineAndNumbers);

    // Title
    axis.setTitleVisible(ap.titleVisible);
    axis.setTitleText(ap.titleText);
    axis.setSpaceTitleAndNumbers(ap.spaceTitleAndNumbers);
    axis.setTitleShiftFromCenter(ap.titleShiftFromCenter);
    axis.setTitleFontName(ap.titleFontName);
    axis.setTitleFontStyle(ap.titleFontStyle);
    axis.setTitleFontSize(ap.titleFontSize);
    axis.setTitleFontColor(ap.titleFontColor);

    // Scale
    axis.setDateMode(ap.dateMode);
    axis.setNumbersInteger(ap.numberInteger);
    axis.mAxis.setScale(ap.minValue, ap.maxValue, ap.scaleType);
    axis.setInvertedCoordinates(ap.invertedCoordinates);
    axis.setCalculateAutomatically(ap.autoCalc);
    axis.setStepValue(ap.stepValue);
    axis.setBaselineValue(ap.baselineValue);
    axis.setExponentVisible(ap.exponentVisible);
    axis.setExponent(ap.exponent);

    // Number
    axis.setNumbersVisible(ap.numberVisible);
    axis.setNumbersAngle(ap.numberAngle);
    axis.setNumberFontName(ap.numberFontName);
    axis.setNumberFontStyle(ap.numberFontStyle);
    axis.setNumberFontSize(ap.numberFontSize);
    axis.setNumberFontColor(ap.numberFontColor);
    axis.setExponentLocationX(ap.exponentLocationX);
    axis.setExponentLocationY(ap.exponentLocationY);
    axis.setNumberDateFormat(ap.numberDateFormat);

    // Tick Mark
    axis.setTickMarkVisible(ap.tickMarkVisible);
    axis.setTickMarkBothsides(ap.tickMarkBothsides);
    axis.setTickMarkWidth(ap.tickMarkWidth);
    axis.setMajorTickMarkLength(ap.tickMarkLength);
    axis.setMinorTickMarkNumber(ap.minorTickMarkNumber);
    axis.setMinorTickMarkLength(ap.minorTickMarkLength);
    axis.setTickMarkColor(ap.tickMarkColor);

    return true;
  }

  protected SGPropertyResults setProperties(SGPropertyMap map, SGPropertyResults iResult) {
    SGPropertyResults result = (SGPropertyResults) iResult.clone();
    Iterator<String> itr = map.getKeyIterator();
    while (itr.hasNext()) {
      String key = itr.next();
      String value = map.getValueString(key);
      if (COM_AXIS_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setVisible(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (axis.getLineVisibleCommandKey().equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(axis.getLineVisibleCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setAxisLineVisible(b.booleanValue()) == false) {
          result.putResult(axis.getLineVisibleCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(axis.getLineVisibleCommandKey(), SGPropertyResults.SUCCEEDED);
      } else if (axis.getLineWidthCommandKey().equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(axis.getLineWidthCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setAxisLineWidth(num.floatValue(), unit.toString()) == false) {
          result.putResult(axis.getLineWidthCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(axis.getLineWidthCommandKey(), SGPropertyResults.SUCCEEDED);
      } else if (axis.getLineColorCommandKey().equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (axis.setAxisLineColor(cl) == false) {
            result.putResult(axis.getLineColorCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(axis.getLineColorCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setAxisLineColor(cl) == false) {
            result.putResult(axis.getLineColorCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(axis.getLineColorCommandKey(), SGPropertyResults.SUCCEEDED);
      } else if (axis.getSpaceLineAndNumberCommandKey().equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(
              axis.getSpaceLineAndNumberCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setSpaceAxisLineAndNumbers(num.floatValue(), unit.toString()) == false) {
          result.putResult(
              axis.getSpaceLineAndNumberCommandKey(), SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(axis.getSpaceLineAndNumberCommandKey(), SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_TITLE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleVisible(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_TITLE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TITLE_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_TEXT.equalsIgnoreCase(key)) {
        if (map.isDoubleQuoted(key) == false) {
          result.putResult(COM_AXIS_TITLE_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleText(value) == false) {
          result.putResult(COM_AXIS_TITLE_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TITLE_TEXT, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_SPACE_TITLE_AND_NUMBER.equalsIgnoreCase(key)
          || COM_AXIS_SPACE_TO_TITLE.equalsIgnoreCase(key)) {
        String k = map.getOriginalKey(key);
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setSpaceTitleAndNumbers(num.floatValue(), unit.toString()) == false) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(k, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_CENTER_SHIFT.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_TITLE_CENTER_SHIFT, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleShiftFromCenter(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_TITLE_CENTER_SHIFT, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TITLE_CENTER_SHIFT, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_FONT_NAME.equalsIgnoreCase(key)) {
        final String name = SGUtility.findFontFamilyName(value);
        if (name == null) {
          result.putResult(COM_AXIS_TITLE_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleFontName(name) == false) {
          result.putResult(COM_AXIS_TITLE_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TITLE_FONT_NAME, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_FONT_STYLE.equalsIgnoreCase(key)) {
        Integer style = SGUtilityText.getFontStyle(value);
        if (style == null) {
          result.putResult(COM_AXIS_TITLE_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleFontStyle(style.intValue()) == false) {
          result.putResult(COM_AXIS_TITLE_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TITLE_FONT_STYLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_FONT_SIZE.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_TITLE_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleFontSize(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_TITLE_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TITLE_FONT_SIZE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TITLE_FONT_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (axis.setTitleFontColor(cl) == false) {
            result.putResult(COM_AXIS_TITLE_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_AXIS_TITLE_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setTitleFontColor(cl) == false) {
            result.putResult(COM_AXIS_TITLE_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_AXIS_TITLE_FONT_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_NUMBER_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumbersVisible(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_NUMBER_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_NUMBER_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_INTEGER.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_NUMBER_INTEGER, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumbersInteger(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_NUMBER_INTEGER, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_NUMBER_INTEGER, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_ANGLE.equalsIgnoreCase(key)) {
        Float num = SGUtilityText.getFloat(value);
        if (num == null) {
          result.putResult(COM_AXIS_NUMBER_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumbersAngle(num.intValue()) == false) {
          result.putResult(COM_AXIS_NUMBER_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_NUMBER_ANGLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_EXPONENT_VISIBLE.equalsIgnoreCase(key)
          || COM_AXIS_NUMBER_FORMAT_EXPONENT_VISIBLE.equalsIgnoreCase(key)) {
        String k = map.getOriginalKey(key);
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setExponentVisible(b.booleanValue()) == false) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(k, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_EXPONENT_VALUE.equalsIgnoreCase(key)
          || COM_AXIS_NUMBER_FORMAT_EXPONENT_VALUE.equalsIgnoreCase(key)) {
        String k = map.getOriginalKey(key);
        Integer num = SGUtilityText.getInteger(value);
        if (num == null) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setExponent(num.intValue()) == false) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(k, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_EXPONENT_LOCATION_X.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_EXPONENT_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setExponentLocationX(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_EXPONENT_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_EXPONENT_LOCATION_X, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_EXPONENT_LOCATION_Y.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_EXPONENT_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setExponentLocationY(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_EXPONENT_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_EXPONENT_LOCATION_Y, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_FONT_NAME.equalsIgnoreCase(key)) {
        final String name = SGUtility.findFontFamilyName(value);
        if (name == null) {
          result.putResult(COM_AXIS_NUMBER_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumberFontName(name) == false) {
          result.putResult(COM_AXIS_NUMBER_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_NUMBER_FONT_NAME, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_FONT_STYLE.equalsIgnoreCase(key)) {
        Integer style = SGUtilityText.getFontStyle(value);
        if (style == null) {
          result.putResult(COM_AXIS_NUMBER_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumberFontStyle(style.intValue()) == false) {
          result.putResult(COM_AXIS_NUMBER_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_NUMBER_FONT_STYLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_FONT_SIZE.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_NUMBER_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumberFontSize(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_NUMBER_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_NUMBER_FONT_SIZE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_NUMBER_FONT_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (axis.setNumberFontColor(cl) == false) {
            result.putResult(COM_AXIS_NUMBER_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_AXIS_NUMBER_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setNumberFontColor(cl) == false) {
            result.putResult(COM_AXIS_NUMBER_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_AXIS_NUMBER_FONT_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_SCALE_TYPE.equalsIgnoreCase(key)) {
        final int scaleType = SGUtilityText.getScaleType(value);
        if (scaleType == -1) {
          result.putResult(COM_AXIS_SCALE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setScaleType(scaleType) == false) {
          result.putResult(COM_AXIS_SCALE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_SCALE_TYPE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_INVERT_COORDINATES.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_INVERT_COORDINATES, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setInvertedCoordinates(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_INVERT_COORDINATES, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_INVERT_COORDINATES, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_SCALE_AUTO.equalsIgnoreCase(key)
          || COM_AXIS_TICK_MARK_AUTO.equalsIgnoreCase(key)) {
        String k = map.getOriginalKey(key);
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setCalculateAutomatically(b.booleanValue()) == false) {
          result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(k, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TICK_MARK_VISIBLE.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_TICK_MARK_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTickMarkVisible(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_TICK_MARK_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TICK_MARK_VISIBLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TICK_MARK_BOTHSIDES.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTickMarkBothsides(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TICK_MARK_BOTHSIDES.equalsIgnoreCase(key)) {
        Boolean b = SGUtilityText.getBoolean(value);
        if (b == null) {
          result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTickMarkBothsides(b.booleanValue()) == false) {
          result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TICK_MARK_WIDTH.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_TICK_MARK_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTickMarkWidth(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_TICK_MARK_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TICK_MARK_WIDTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_MAJOR_TICK_MARK_LENGTH.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_MAJOR_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_MAJOR_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_MAJOR_TICK_MARK_LENGTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_MINOR_TICK_MARK_NUMBER.equalsIgnoreCase(key)) {
        Integer num = SGUtilityText.getInteger(value);
        if (num == null) {
          result.putResult(COM_AXIS_MINOR_TICK_MARK_NUMBER, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setMinorTickMarkNumber(num.intValue()) == false) {
          result.putResult(COM_AXIS_MINOR_TICK_MARK_NUMBER, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_MINOR_TICK_MARK_NUMBER, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_MINOR_TICK_MARK_LENGTH.equalsIgnoreCase(key)) {
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_MINOR_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_MINOR_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_MINOR_TICK_MARK_LENGTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TICK_MARK_COLOR.equalsIgnoreCase(key)) {
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (axis.setTickMarkColor(cl) == false) {
            result.putResult(COM_AXIS_TICK_MARK_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_AXIS_TICK_MARK_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setTickMarkColor(cl) == false) {
            result.putResult(COM_AXIS_TICK_MARK_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_AXIS_TICK_MARK_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_TICK_MARK_LENGTH.equalsIgnoreCase(key)) {
        // for backward compatibility (<= 2.0.0)
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_TICK_MARK_LENGTH, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_TICK_MARK_LENGTH, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_LINE_COLOR.equalsIgnoreCase(key)) {
        // for backward compatibility (<= 2.0.0)
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (axis.setAxisLineColor(cl) == false) {
            result.putResult(COM_AXIS_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setTickMarkColor(cl) == false) {
            result.putResult(COM_AXIS_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_AXIS_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setAxisLineColor(cl) == false) {
            result.putResult(COM_AXIS_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setTickMarkColor(cl) == false) {
            result.putResult(COM_AXIS_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_AXIS_LINE_COLOR, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_FONT_NAME.equalsIgnoreCase(key)) {
        // for backward compatibility (<= 2.0.0)
        final String name = SGUtility.findFontFamilyName(value);
        if (name == null) {
          result.putResult(COM_AXIS_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleFontName(name) == false) {
          result.putResult(COM_AXIS_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumberFontName(name) == false) {
          result.putResult(COM_AXIS_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_FONT_NAME, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_FONT_STYLE.equalsIgnoreCase(key)) {
        // for backward compatibility (<= 2.0.0)
        Integer style = SGUtilityText.getFontStyle(value);
        if (style == null) {
          result.putResult(COM_AXIS_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleFontStyle(style.intValue()) == false) {
          result.putResult(COM_AXIS_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumberFontStyle(style.intValue()) == false) {
          result.putResult(COM_AXIS_FONT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_FONT_STYLE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_FONT_SIZE.equalsIgnoreCase(key)) {
        // for backward compatibility (<= 2.0.0)
        StringBuilder unit = new StringBuilder();
        Number num = SGUtilityText.getNumber(value, unit);
        if (num == null) {
          result.putResult(COM_AXIS_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setTitleFontSize(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (axis.setNumberFontSize(num.floatValue(), unit.toString()) == false) {
          result.putResult(COM_AXIS_FONT_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(COM_AXIS_FONT_SIZE, SGPropertyResults.SUCCEEDED);
      } else if (COM_AXIS_FONT_COLOR.equalsIgnoreCase(key)) {
        // for backward compatibility (<= 2.0.0)
        Color cl = SGUtilityText.getColor(value);
        if (cl != null) {
          if (axis.setTitleFontColor(cl) == false) {
            result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setNumberFontColor(cl) == false) {
            result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        } else {
          cl = SGUtilityText.parseColor(value);
          if (cl == null) {
            result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setTitleFontColor(cl) == false) {
            result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (axis.setNumberFontColor(cl) == false) {
            result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
        }
        result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.SUCCEEDED);
      }
    }

    // Note: after set tick mark length
    // for backward compatibility (<= 2.0.0)
    if (map.getKeys().contains(COM_AXIS_TICK_MARK_INNER.toUpperCase())) {
      String value = map.getValue(COM_AXIS_TICK_MARK_INNER);
      Boolean b = SGUtilityText.getBoolean(value);
      if (b == null) {
        result.putResult(COM_AXIS_TICK_MARK_INNER, SGPropertyResults.INVALID_INPUT_VALUE);
      } else {
        float majorLength = Math.abs(axis.getMajorTickMarkLength());
        float minorLength = Math.abs(axis.getMinorTickMarkLength());
        if (!b) {
          majorLength *= -1;
          minorLength *= -1;
        }
        int status = SGPropertyResults.SUCCEEDED;
        if (axis.setMajorTickMarkLength(majorLength) == false) {
          status = SGPropertyResults.INVALID_INPUT_VALUE;
        }
        if (axis.setMinorTickMarkLength(minorLength) == false) {
          status = SGPropertyResults.INVALID_INPUT_VALUE;
        }
        result.putResult(COM_AXIS_TICK_MARK_INNER, status);
      }
    }

    return result;
  }
}
