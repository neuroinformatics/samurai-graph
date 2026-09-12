package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.data.SGISXYMultipleDimensionData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.figure.*;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXY.SXYElementGroupSetPropertiesInFigureElement;

class ElementGroupSetInLegendSXY extends ElementGroupSetInLegend
    implements SGIElementGroupSetSXY, SGISXYDataConstants {
  private final SGFigureElementLegend legend;

  /** Shift to the x direction. */
  protected double mShiftX;

  /** Shift to the y direction. */
  protected double mShiftY;

  /** */
  protected ElementGroupSetInLegendSXY(final SGFigureElementLegend legend, SGData data) {
    super(legend, data);
    this.legend = legend;
  }

  /**
   * Returns the figure element.
   *
   * @return the figure element
   */
  public SGFigureElementForData getFigureElement() {
    return legend;
  }

  public ELEMENT_TYPE getSelectedGroupType() {
    return this.mSelectedGroupType;
  }

  private ELEMENT_TYPE mSelectedGroupType = ELEMENT_TYPE.Void;

  /**
   * The series index in multiple graph when this group set is created from the group set of
   * multiple graph.
   */
  private int mSeriesIndex = -1;

  /** The total number of series in multiple graph which creates this group set. */
  private int mNumberOfSeries = -1;

  public int getSeriesIndex() {
    return this.mSeriesIndex;
  }

  public int getNumberOfSeries() {
    return this.mNumberOfSeries;
  }

  public void setSeriesIndexAndNumber(final int index, final int number) {
    this.mSeriesIndex = index;
    this.mNumberOfSeries = number;
  }

  private SGLineStyle mLineStyle = null;

  public SGLineStyle getLineStyle() {
    return (this.mLineStyle != null) ? (SGLineStyle) this.mLineStyle.clone() : null;
  }

  public void setLineStyle(SGLineStyle style) {
    SGLineStyle lineStyle = (SGLineStyle) style.clone();
    this.mLineStyle = lineStyle;
    SGElementGroupLine lineGroup = this.getLineGroup();
    lineGroup.setStyle(lineStyle);
  }

  private String mLineColorMapName = null;

  /**
   * Returns the name of the color map for lines.
   *
   * @return the name of the color map for lines
   */
  @Override
  public String getLineColorMapName() {
    return this.mLineColorMapName;
  }

  /**
   * Sets the color map for lines.
   *
   * @param name name of the map to set
   * @return true if succeeded
   */
  public boolean setLineColorMapName(String name) {
    this.mLineColorMapName = name;
    return true;
  }

  /** */
  void paintSymbol(final Graphics2D g2d) {

    // bar
    ElementGroupBar groupBar = legend.getGroupBar(this);
    if (groupBar != null) {
      if (groupBar.isVisible()) {
        groupBar.paintElement(g2d);
      }
    }

    // error bar
    SGISXYTypeData dataSXY = (SGISXYTypeData) this.getData();
    if (dataSXY.isErrorBarAvailable()) {
      ElementGroupErrorBar groupErrorBar = legend.getGroupErrorBar(this);
      if (groupErrorBar != null) {
        if (groupErrorBar.isVisible()) {
          groupErrorBar.paintElement(g2d);
        }
      }
    }

    // line
    ElementGroupLine groupLine = legend.getGroupLine(this);
    if (groupLine != null) {
      if (groupLine.isVisible()) {
        groupLine.paintElement(g2d);
      }
    }

    // symbol
    ElementGroupSymbol groupSymbol = legend.getGroupSymbol(this);
    if (groupSymbol != null) {
      if (groupSymbol.isVisible()) {
        groupSymbol.paintElement(g2d);
      }
    }
  }

  /**
   * Returns a list of line groups.
   *
   * @return a list of line groups
   */
  public List<SGElementGroupLine> getLineGroups() {
    List<SGElementGroupLine> retList = new ArrayList<SGElementGroupLine>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(
            SGElementGroupLine.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupLine) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns a list of symbol groups.
   *
   * @return a list of symbol groups
   */
  public List<SGElementGroupSymbol> getSymbolGroups() {
    List<SGElementGroupSymbol> retList = new ArrayList<SGElementGroupSymbol>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(
            SGElementGroupSymbol.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupSymbol) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns a list of bar groups.
   *
   * @return a list of bar groups
   */
  public List<SGElementGroupBar> getBarGroups() {
    List<SGElementGroupBar> retList = new ArrayList<SGElementGroupBar>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(SGElementGroupBar.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupBar) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns a list of error bar groups.
   *
   * @return a list of error bar groups
   */
  public List<SGElementGroupErrorBar> getErrorBarGroups() {
    List<SGElementGroupErrorBar> retList = new ArrayList<SGElementGroupErrorBar>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(
            SGElementGroupErrorBar.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupErrorBar) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns a list of tick label groups.
   *
   * @return a list of tick label groups
   */
  public List<SGElementGroupTickLabel> getTickLabelGroups() {
    List<SGElementGroupTickLabel> retList = new ArrayList<SGElementGroupTickLabel>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(
            SGElementGroupTickLabel.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupTickLabel) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns a line group which is the first element of an array.
   *
   * @return the first element of an array of line groups, or null when this group set does not have
   *     any line groups
   */
  public SGElementGroupLine getLineGroup() {
    return (SGElementGroupLine)
        SGUtilityForFigureElement.getGroup(SGElementGroupLine.class, this.mDrawingElementGroupList);
  }

  /**
   * Returns a symbol group which is the first element of an array.
   *
   * @return the first element of an array of symbol groups, or null when this group set does not
   *     have any symbol groups
   */
  public SGElementGroupSymbol getSymbolGroup() {
    return (SGElementGroupSymbol)
        SGUtilityForFigureElement.getGroup(
            SGElementGroupSymbol.class, this.mDrawingElementGroupList);
  }

  /**
   * Returns a bar group which is the first element of an array.
   *
   * @return the first element of an array of bar groups, or null when this group set does not have
   *     any bar groups
   */
  public SGElementGroupBar getBarGroup() {
    return (SGElementGroupBar)
        SGUtilityForFigureElement.getGroup(SGElementGroupBar.class, this.mDrawingElementGroupList);
  }

  /**
   * Returns an error bar group which is the first element of an array.
   *
   * @return the first element of an array of error bar groups, or null when this group set does not
   *     have any error bar groups
   */
  public SGElementGroupErrorBar getErrorBarGroup() {
    return (SGElementGroupErrorBar)
        SGUtilityForFigureElement.getGroup(
            SGElementGroupErrorBar.class, this.mDrawingElementGroupList);
  }

  /**
   * Returns a tick label group which is the first element of an array.
   *
   * @return the first element of an array of tick label groups, or null when this group set does
   *     not have any tick label groups
   */
  public SGElementGroupTickLabel getTickLabelGroup() {
    return (SGElementGroupTickLabel)
        SGUtilityForFigureElement.getGroup(
            SGElementGroupTickLabel.class, this.mDrawingElementGroupList);
  }

  // Line

  public boolean isLineVisible() {
    return this.getLineGroup().isVisible();
  }

  public float getLineWidth() {
    return this.getLineGroup().getLineWidth();
  }

  public float getLineWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getLineWidth(), unit);
  }

  public int getLineType() {
    return this.getLineGroup().getLineType();
  }

  public Color getLineColor() {
    return this.getLineGroup().getColor();
  }

  /**
   * Returns whether the lines connect all effective points.
   *
   * @return true if connecting all effective points
   */
  public boolean isLineConnectingAll() {
    return this.getLineGroup().isLineConnectingAll();
  }

  public double getShiftX() {
    return this.mShiftX;
  }

  public double getShiftY() {
    return this.mShiftY;
  }

  public boolean setShiftX(final double shift) {
    this.mShiftX = shift;
    return true;
  }

  public boolean setShiftY(final double shift) {
    this.mShiftY = shift;
    return true;
  }

  public boolean setLineVisible(final boolean b) {
    this.getLineGroup().setVisible(b);
    return true;
  }

  public boolean setLineWidth(final float width) {
    return this.getLineGroup().setLineWidth(width);
  }

  public boolean setLineWidth(final float width, final String unit) {
    return this.setLineWidth((float) SGUtilityText.convertToPoint(width, unit));
  }

  public boolean setLineType(final int type) {
    return this.getLineGroup().setLineType(type);
  }

  public boolean setLineColor(final Color cl) {
    return this.getLineGroup().setColor(cl);
  }

  /**
   * Sets whether the lines connect all effective points.
   *
   * @return true if succeeded
   */
  public boolean setLineConnectingAll(final boolean b) {
    return this.getLineGroup().setLineConnectingAll(b);
  }

  // Symbol

  public boolean isSymbolVisible() {
    return this.getSymbolGroup().isVisible();
  }

  public int getSymbolType() {
    return this.getSymbolGroup().getType();
  }

  public float getSymbolSize() {
    return this.getSymbolGroup().getSize();
  }

  public float getSymbolSize(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getSymbolSize(), unit);
  }

  public float getSymbolLineWidth() {
    return this.getSymbolGroup().getLineWidth();
  }

  public float getSymbolLineWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getSymbolLineWidth(), unit);
  }

  public SGIPaint getSymbolInnerPaint() {
    return this.getSymbolGroup().getInnerPaint();
  }

  public Color getSymbolLineColor() {
    return this.getSymbolGroup().getLineColor();
  }

  public boolean isSymbolLineVisible() {
    return this.getSymbolGroup().isLineVisible();
  }

  public boolean setSymbolVisible(final boolean b) {
    this.getSymbolGroup().setVisible(b);
    return true;
  }

  public boolean setSymbolType(final int type) {
    return this.getSymbolGroup().setType(type);
  }

  public boolean setSymbolSize(final float size) {
    return this.getSymbolGroup().setSize(size);
  }

  public boolean setSymbolSize(final float size, final String unit) {
    return this.setSymbolSize((float) SGUtilityText.convertToPoint(size, unit));
  }

  public boolean setSymbolLineWidth(final float width) {
    return this.getSymbolGroup().setLineWidth(width);
  }

  public boolean setSymbolLineWidth(final float width, final String unit) {
    return this.setSymbolLineWidth((float) SGUtilityText.convertToPoint(width, unit));
  }

  public boolean setSymbolInnerPaint(final SGIPaint paint) {
    return this.getSymbolGroup().setInnerPaint(paint);
  }

  public boolean setSymbolLineColor(final Color cl) {
    return this.getSymbolGroup().setLineColor(cl);
  }

  public boolean setSymbolLineVisible(boolean b) {
    return this.getSymbolGroup().setLineVisible(b);
  }

  // Bar

  public boolean isBarVisible() {
    return this.getBarGroup().isVisible();
  }

  public double getBarBaselineValue() {
    return this.getBarGroup().getBaselineValue();
  }

  public float getBarWidth() {
    return this.getBarGroup().getRectangleWidth();
  }

  public double getBarWidthValue() {
    return this.getBarGroup().getWidthValue();
  }

  public float getBarEdgeLineWidth() {
    return this.getBarGroup().getEdgeLineWidth();
  }

  public float getBarEdgeLineWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getBarEdgeLineWidth(), unit);
  }

  public SGIPaint getBarInnerPaint() {
    return this.getBarGroup().getInnerPaint();
  }

  public Color getBarEdgeLineColor() {
    return this.getBarGroup().getEdgeLineColor();
  }

  public boolean isBarEdgeLineVisible() {
    return this.getBarGroup().isEdgeLineVisible();
  }

  public boolean isBarVertical() {
    return this.getBarGroup().isVertical();
  }

  public double getBarOffsetX() {
    return this.getBarGroup().getOffsetX();
  }

  public double getBarOffsetY() {
    return this.getBarGroup().getOffsetY();
  }

  public double getBarInterval() {
    return this.getBarGroup().getInterval();
  }

  public boolean setBarVisible(final boolean b) {
    this.getBarGroup().setVisible(b);
    return true;
  }

  public boolean setBarBaselineValue(final double value) {
    return this.getBarGroup().setBaselineValue(value);
  }

  public boolean setBarWidth(final float width) {
    return this.getBarGroup().setRectangleWidth(width);
  }

  public boolean setBarWidthValue(final double value) {
    return this.getBarGroup().setWidthValue(value);
  }

  public boolean setBarEdgeLineWidth(final float width) {
    return this.getBarGroup().setEdgeLineWidth(width);
  }

  public boolean setBarEdgeLineWidth(final float width, final String unit) {
    return this.setBarEdgeLineWidth((float) SGUtilityText.convertToPoint(width, unit));
  }

  public boolean setBarInnerPaint(final SGIPaint paint) {
    return this.getBarGroup().setInnerPaint(paint);
  }

  public boolean setBarEdgeLineColor(final Color cl) {
    return this.getBarGroup().setEdgeLineColor(cl);
  }

  public boolean setBarEdgeLineVisible(boolean b) {
    return this.getBarGroup().setEdgeLineVisible(b);
  }

  public boolean setBarVertical(boolean b) {
    return this.getBarGroup().setVertical(b);
  }

  public boolean hasValidBaselineValue(final int config, final Number value) {
    final SGAxis axis = (config == -1) ? this.mYAxis : legend.mAxisElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getBarBaselineValue();
    return axis.isValidValue(v);
  }

  public boolean setBarOffsetX(final double shift) {
    return this.getBarGroup().setOffsetX(shift);
  }

  public boolean setBarOffsetY(final double shift) {
    return this.getBarGroup().setOffsetY(shift);
  }

  public boolean setBarInterval(final double interval) {
    return this.getBarGroup().setInterval(interval);
  }

  // Error Bar
  public boolean isErrorBarAvailable() {
    if (this.mData instanceof SGISXYTypeData) {
      SGISXYTypeData data = (SGISXYTypeData) this.mData;
      return data.isErrorBarAvailable();
    } else {
      return false;
    }
  }

  public boolean isErrorBarVisible() {
    return this.getErrorBarGroup().isVisible();
  }

  public boolean isErrorBarVertical() {
    return this.getErrorBarGroup().isVertical();
  }

  public int getErrorBarHeadType() {
    return this.getErrorBarGroup().getHeadType();
  }

  public float getErrorBarHeadSize() {
    return this.getErrorBarGroup().getHeadSize();
  }

  public float getErrorBarHeadSize(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getErrorBarHeadSize(), unit);
  }

  public Color getErrorBarColor() {
    return this.getErrorBarGroup().getColor();
  }

  public float getErrorBarLineWidth() {
    return this.getErrorBarGroup().getLineWidth();
  }

  public float getErrorBarLineWidth(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getErrorBarLineWidth(), unit);
  }

  public int getErrorBarStyle() {
    return this.getErrorBarGroup().getErrorBarStyle();
  }

  public boolean isErrorBarOnLinePosition() {
    return this.getErrorBarGroup().isPositionOnLine();
  }

  public boolean setErrorBarVisible(final boolean b) {
    this.getErrorBarGroup().setVisible(b);
    return true;
  }

  public boolean setErrorBarHeadType(final int type) {
    this.getErrorBarGroup().setHeadType(type);
    return true;
  }

  public boolean setErrorBarHeadSize(final float size) {
    this.getErrorBarGroup().setHeadSize(size);
    return true;
  }

  public boolean setErrorBarHeadSize(final float size, final String unit) {
    return this.setErrorBarHeadSize((float) SGUtilityText.convertToPoint(size, unit));
  }

  public boolean setErrorBarColor(final Color cl) {
    return this.getErrorBarGroup().setColor(cl);
  }

  public boolean setErrorBarLineWidth(final float width) {
    this.getErrorBarGroup().setLineWidth(width);
    return true;
  }

  public boolean setErrorBarLineWidth(final float width, final String unit) {
    return this.setErrorBarLineWidth((float) SGUtilityText.convertToPoint(width, unit));
  }

  public boolean setErrorBarStyle(final int style) {
    this.getErrorBarGroup().setErrorBarStyle(style);
    return true;
  }

  public boolean setErrorBarVertical(final boolean b) {
    return this.getErrorBarGroup().setVertical(b);
  }

  @Override
  public boolean setErrorBarOnLinePosition(boolean b) {
    return this.getErrorBarGroup().setPositionOnLine(b);
  }

  // Tick Label
  public boolean isTickLabelAvailable() {
    if (this.mData instanceof SGISXYTypeData) {
      SGISXYTypeData data = (SGISXYTypeData) this.mData;
      return data.isTickLabelAvailable();
    } else {
      return false;
    }
  }

  public boolean isTickLabelVisible() {
    return this.getTickLabelGroup().isVisible();
  }

  public String getTickLabelFontName() {
    return this.getTickLabelGroup().getFontName();
  }

  public int getTickLabelFontStyle() {
    return this.getTickLabelGroup().getFontStyle();
  }

  public float getTickLabelFontSize() {
    return this.getTickLabelGroup().getFontSize();
  }

  public float getTickLabelFontSize(final String unit) {
    return (float) SGUtilityText.convertFromPoint(this.getTickLabelFontSize(), unit);
  }

  public Color getTickLabelColor() {
    return this.getTickLabelGroup().getColor();
  }

  public float getTickLabelAngle() {
    return this.getTickLabelGroup().getAngle();
  }

  public boolean hasTickLabelHorizontalAlignment() {
    return this.getTickLabelGroup().hasHorizontalAlignment();
  }

  public int getTickLabelDecimalPlaces() {
    return this.getTickLabelGroup().getDecimalPlaces();
  }

  public int getTickLabelExponent() {
    return this.getTickLabelGroup().getExponent();
  }

  public String getTickLabelDateFormat() {
    return this.getTickLabelGroup().getDateFormat();
  }

  public boolean setTickLabelVisible(final boolean b) {
    this.getTickLabelGroup().setVisible(b);
    return true;
  }

  public boolean setTickLabelFontName(final String name) {
    return this.getTickLabelGroup().setFontName(name);
  }

  public boolean setTickLabelFontStyle(final int style) {
    return this.getTickLabelGroup().setFontStyle(style);
  }

  public boolean setTickLabelFontSize(final float size) {
    return this.getTickLabelGroup().setFontSize(size);
  }

  public boolean setTickLabelFontSize(final float size, final String unit) {
    return this.setTickLabelFontSize((float) SGUtilityText.convertToPoint(size, unit));
  }

  public boolean setTickLabelColor(final Color cl) {
    return this.getTickLabelGroup().setColor(cl);
  }

  public boolean setTickLabelAngle(final float angle) {
    return this.getTickLabelGroup().setAngle(angle);
  }

  public boolean setTickLabelHorizontalAlignment(final boolean b) {
    return this.getTickLabelGroup().setHorizontalAlignment(b);
  }

  public boolean setTickLabelDecimalPlaces(final int dp) {
    this.getTickLabelGroup().setDecimalPlaces(dp);
    return true;
  }

  public boolean setTickLabelExponent(final int exp) {
    this.getTickLabelGroup().setExponent(exp);
    return true;
  }

  /** Update the text strings of tick labels. */
  public void updateTickLabelStrings() {
    // do nothing
  }

  //
  boolean onMouseClicked(final MouseEvent e) {
    SGElementGroup group = this.getElementGroupAt(e.getX(), e.getY());

    ELEMENT_TYPE type = ELEMENT_TYPE.Void;
    if (group instanceof ElementGroupLine) {
      type = ELEMENT_TYPE.Line;
    } else if (group instanceof ElementGroupSymbol) {
      type = ELEMENT_TYPE.Symbol;
    } else if (group instanceof ElementGroupBar) {
      type = ELEMENT_TYPE.Bar;
    } else if (group instanceof ElementGroupErrorBar) {
      type = ELEMENT_TYPE.ErrorBar;
    } else if (group instanceof ElementGroupTickLabels) {
      type = ELEMENT_TYPE.TickLabel;
    }
    this.mSelectedGroupType = type;

    return true;
  }

  /** */
  public boolean addDrawingElementGroup(final int type) {

    SGISXYTypeData data = (SGISXYTypeData) this.mData;
    SGElementGroup group = null;
    if (type == SGIElementGroupConstants.POLYLINE_GROUP) {
      group = new ElementGroupLine(legend, data);
    } else if (type == SGIElementGroupConstants.RECTANGLE_GROUP) {
      group = new ElementGroupBar(legend, data);
    } else if (type == SGIElementGroupConstants.SYMBOL_GROUP) {
      group = new ElementGroupSymbol(legend, data);
    } else if (type == SGIElementGroupConstants.ERROR_BAR_GROUP) {
      group = new ElementGroupErrorBar(legend, data);
    } else if (type == SGIElementGroupConstants.TICK_LABEL_GROUP) {
      group = new ElementGroupTickLabels(legend, data);
    } else {
      throw new Error();
    }

    this.addDrawingElementGroup(group);

    return true;
  }

  /** */
  public int getXAxisLocation() {
    return legend.mAxisElement.getLocationInPlane(this.getXAxis());
  }

  /** */
  public int getYAxisLocation() {
    return legend.mAxisElement.getLocationInPlane(this.getYAxis());
  }

  /** */
  public boolean setXAxisLocation(final int location) {
    if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
        && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
      return false;
    }
    this.mXAxis = this.getAxis(location);
    return true;
  }

  /** */
  public boolean setYAxisLocation(final int location) {
    if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
        && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
      return false;
    }
    this.mYAxis = this.getAxis(location);
    return true;
  }

  public SGElementGroupErrorBar createErrorBars(SGISXYTypeSingleData dataXY) {
    if (this.addDrawingElementGroup(SGIElementGroupConstants.ERROR_BAR_GROUP) == false) {
      return null;
    }
    return this.getErrorBarGroup();
  }

  public SGElementGroupTickLabel createTickLabels(SGISXYTypeSingleData dataXY) {
    if (this.addDrawingElementGroup(SGIElementGroupConstants.TICK_LABEL_GROUP) == false) {
      return null;
    }
    return this.getTickLabelGroup();
  }

  /**
   * Sets the properties of element groups.
   *
   * @param elementGroupPropertiesList
   * @return true if succeeded
   */
  protected boolean setElementGroupProperties(List<SGProperties> elementGroupPropertiesList) {
    for (int ii = 0; ii < elementGroupPropertiesList.size(); ii++) {
      SGProperties gp = elementGroupPropertiesList.get(ii);
      SGElementGroup group = null;
      if (gp instanceof SGElementGroupLine.LineProperties) {
        group = this.getLineGroup();
      } else if (gp instanceof SGElementGroupSymbol.SymbolProperties) {
        group = this.getSymbolGroup();
      } else if (gp instanceof SGElementGroupBar.BarProperties) {
        group = this.getBarGroup();
      } else if (gp instanceof SGElementGroupErrorBar.ErrorBarProperties) {
        group = this.getErrorBarGroup();
      } else if (gp instanceof SGElementGroupString.StringProperties) {
        group = this.getTickLabelGroup();
      } else {
        throw new Error("Illegal group property: " + gp);
      }
      if (group == null) {
        continue;
      }
      if (group.setProperties(gp) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the direction of error bars.
   *
   * @param vertical true to set vertical
   * @return true if succeeded
   */
  public boolean setErrorBarDirection(final boolean vertical) {
    return this.setErrorBarVertical(vertical);
  }

  /**
   * Sets the alignment of tick label.
   *
   * @param horizontal true to align horizontally
   * @return true if succeeded
   */
  public boolean setTickLabelAlignment(final boolean horizontal) {
    return this.setTickLabelHorizontalAlignment(horizontal);
  }

  /**
   * Sets the information of picked up dimension.
   *
   * @param info the information of picked up dimension
   * @return true if succeeded
   */
  public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info) {
    SGData data = this.getData();
    if (data instanceof SGISXYMultipleDimensionData) {
      SGISXYMultipleDimensionData nData = (SGISXYMultipleDimensionData) data;
      return nData.setPickUpDimensionInfo(info);
    } else {
      return false;
    }
  }

  /**
   * Sets the stride.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  public boolean setStride(SGIntegerSeriesSet stride) {
    return this.setSXYStrideToData(stride);
  }

  @Override
  public boolean setIndexStride(SGIntegerSeriesSet stride) {
    return this.setIndexStrideToData(stride);
  }

  /**
   * Sets the stride for single dimensional data.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  @Override
  public boolean setSDArrayStride(SGIntegerSeriesSet stride) {
    return this.setSDArrayStrideToData(stride);
  }

  public boolean setTickLabelStride(SGIntegerSeriesSet stride) {
    return this.setTickLabelStrideToData(stride);
  }

  /**
   * Sets the column types and related parameters.
   *
   * @param map property map
   * @param result results of setting properties
   * @param cols an array of data columns
   * @return true if succeeded
   */
  @Override
  protected boolean setProperties(
      SGPropertyMap map, SGPropertyResults result, SGDataColumnInfo[] cols) {
    // do nothing
    return true;
  }

  private boolean mLineColorAutoAssigned = false;

  /**
   * Returns whether line color is automatically assigned.
   *
   * @return true if line color is automatically assigned
   */
  @Override
  public boolean isLineColorAutoAssigned() {
    return this.mLineColorAutoAssigned;
  }

  /**
   * Sets whether line color is automatically assigned
   *
   * @param b a flag whether line color is automatically assigned
   */
  @Override
  public void setLineColorAutoAssigned(final boolean b) {
    this.mLineColorAutoAssigned = b;
  }

  @Override
  public SGProperties getProperties() {
    SXYElementGroupSetPropertiesInFigureElement p =
        new SXYElementGroupSetPropertiesInFigureElement();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  @Override
  public boolean getProperties(SGProperties p) {
    if ((p instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    SXYElementGroupSetPropertiesInFigureElement mp =
        (SXYElementGroupSetPropertiesInFigureElement) p;
    mp.shiftX = this.getShiftX();
    mp.shiftY = this.getShiftY();
    mp.lineColorAutoAssigned = this.isLineColorAutoAssigned();
    mp.lineColorMapName = this.getLineColorMapName();
    return true;
  }

  @Override
  public boolean setProperties(SGProperties p) {
    if ((p instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }

    SXYElementGroupSetPropertiesInFigureElement mp =
        (SXYElementGroupSetPropertiesInFigureElement) p;
    this.setShiftX(mp.shiftX);
    this.setShiftY(mp.shiftY);
    this.setLineColorAutoAssigned(mp.lineColorAutoAssigned);
    this.setLineColorMapName(mp.lineColorMapName);
    return true;
  }

  @Override
  public Map<String, SGProperties> getElementGroupPropertiesMap() {
    return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
  }

  @Override
  public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
    return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
  }
}
