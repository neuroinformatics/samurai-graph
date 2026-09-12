package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.*;
import java.awt.Color;
import java.awt.geom.*;
import java.util.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.*;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager.HueColorMap;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXYMultiple.MultipleSXYElementGroupSetPropertiesInFigureElement;
import org.w3c.dom.Element;

class ElementGroupSetInLegendMultipleSXY extends ElementGroupSetInLegendSXY
    implements SGIElementGroupSetMultipleSXY, SGISXYDataDialogObserver {
  private final SGFigureElementLegend legend;

  @Override
  public boolean setShiftX(final double shift) {
    if (super.setShiftX(shift) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY legend =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      if (legend.setShiftX(shift) == false) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean setShiftY(final double shift) {
    if (super.setShiftY(shift) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY legend =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      if (legend.setShiftY(shift) == false) {
        return false;
      }
    }
    return true;
  }

  /** Color map manager for lines. */
  private SGColorMapManager mLineColorMapManager = new SGLineStyleColorMapManager();

  /** The color map for lines. */
  private String mLineColorMapName = SGLineStyleColorMapManager.COLOR_MAP_NAME_HUE_GRADATION;

  /**
   * Returns the map of line style.
   *
   * @return the map of line style
   */
  @Override
  public Map<Integer, SGLineStyle> getLineStyleMap() {
    Map<Integer, SGLineStyle> lineStyleMap = new TreeMap<Integer, SGLineStyle>();
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY gs =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      lineStyleMap.put(gs.getSeriesIndex(), gs.getLineStyle());
    }
    return lineStyleMap;
  }

  /**
   * Sets the line style to the child data object.
   *
   * @param style the line style to set
   * @param index array index of child data object
   * @return true if succeeded
   */
  @Override
  public boolean setLineStyle(final SGLineStyle style, final int index) {
    for (SGIElementGroupSetForData gs : this.mElementGroupSetList) {
      ElementGroupSetInLegendSXY gsXY = (ElementGroupSetInLegendSXY) gs;
      if (gsXY.getSeriesIndex() == index) {
        gsXY.setLineStyle(style);
        break;
      }
    }

    // set to line group of this group set
    ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
    lineGroup.setLineStyle(style, index);

    return true;
  }

  /**
   * Sets the line styles to the child data object.
   *
   * @param styleList list of line styles
   * @return true if succeeded
   */
  @Override
  public boolean setLineStyle(final List<SGLineStyle> styleList) {
    if (styleList.size() != this.getChildNumber()) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY gs =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      SGLineStyle style = styleList.get(ii);
      gs.setLineStyle(style);
    }
    ElementGroupLine groupLine = (ElementGroupLine) this.getLineGroup();
    groupLine.setLineStyleList(styleList);

    return true;
  }

  /** The list of element group set. */
  protected ArrayList<SGIElementGroupSetForData> mElementGroupSetList =
      new ArrayList<SGIElementGroupSetForData>();

  /**
   * Adds a group set.
   *
   * @param gs the group set to be add
   */
  public void addChildGroupSet(SGIElementGroupSetForData gs) {
    this.mElementGroupSetList.add(gs);
  }

  /**
   * Removes a group set.
   *
   * @param gs the group set to be removed
   */
  public void removeChildGroupSet(SGIElementGroupSetForData gs) {
    this.mElementGroupSetList.remove(gs);
  }

  @Override
  public SGIElementGroupSetForData[] getChildGroupSetArray() {
    SGIElementGroupSetForData[] array =
        new SGIElementGroupSetForData[this.mElementGroupSetList.size()];
    this.mElementGroupSetList.toArray(array);
    return array;
  }

  /**
   * Returns the number of child data objects.
   *
   * @return the number of child data objects
   */
  @Override
  public int getChildNumber() {
    return this.mElementGroupSetList.size();
  }

  /**
   * Returns the list of child objects.
   *
   * @return the list of child objects
   */
  @Override
  public List<String> getChildNameList() {
    SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) this.mData;
    return data.getChildNameList();
  }

  /**
   * Builds the legend object with a given data object.
   *
   * @param data a data object
   */
  protected ElementGroupSetInLegendMultipleSXY(final SGFigureElementLegend legend, SGData data) {
    super(legend, data);
    this.legend = legend;
  }

  /** Disposes this object. */
  public void dispose() {
    super.dispose();
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegend gs = (ElementGroupSetInLegend) this.mElementGroupSetList.get(ii);
      SGData data = gs.mData;
      if (data != null) {
        data.dispose();
      }
    }
  }

  /**
   * Returns the first element group set.
   *
   * @return the first element group set
   */
  public SGIElementGroupSetForData getFirst() {
    if (this.mElementGroupSetList.size() == 0) {
      return null;
    } else {
      return (SGElementGroupSetForData) this.mElementGroupSetList.get(0);
    }
  }

  public SGProperties getProperties() {
    MultipleSXYElementGroupSetPropertiesInFigureElement p =
        new MultipleSXYElementGroupSetPropertiesInFigureElement();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  public boolean getProperties(SGProperties p) {
    if ((p instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    MultipleSXYElementGroupSetPropertiesInFigureElement mp =
        (MultipleSXYElementGroupSetPropertiesInFigureElement) p;
    mp.shiftX = this.mShiftX;
    mp.shiftY = this.mShiftY;
    mp.lineColorAutoAssigned = this.isLineColorAutoAssigned();
    mp.lineColorMapName = this.getLineColorMapName();
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY gs =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      mp.childPropertyList.add(gs.getProperties());
      mp.childLineStyleList.add(gs.getLineStyle());
    }
    Map<String, SGColorMap> colorMaps = this.mLineColorMapManager.getColorMaps();
    Iterator<Entry<String, SGColorMap>> itr = colorMaps.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, SGColorMap> entry = itr.next();
      String name = entry.getKey();
      SGColorMap colorMap = entry.getValue();
      mp.colorMapPropertiesMap.put(name, (ColorMapProperties) colorMap.getProperties());
    }
    return true;
  }

  public boolean setProperties(SGProperties p) {
    if ((p instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }

    // disposes of old data
    if (SGMultipleSXYUtility.removeAllChildData(this) == false) {
      return false;
    }

    // synchronize the child group set
    if (legend.updateChildGroupSet(this) == false) {
      return false;
    }

    MultipleSXYElementGroupSetPropertiesInFigureElement mp =
        (MultipleSXYElementGroupSetPropertiesInFigureElement) p;
    this.mShiftX = mp.shiftX;
    this.mShiftY = mp.shiftY;
    this.setLineColorAutoAssigned(mp.lineColorAutoAssigned);
    this.setLineColorMapName(mp.lineColorMapName);

    Iterator<Entry<String, ColorMapProperties>> itr =
        mp.colorMapPropertiesMap.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<String, ColorMapProperties> entry = itr.next();
      String name = entry.getKey();
      ColorMapProperties colorMapProperties = entry.getValue();
      SGColorMap colorMap = this.mLineColorMapManager.getColorMap(name);
      if (!colorMap.setProperties(colorMapProperties)) {
        return false;
      }
    }

    /*
     * When merging data, mChildPropertyLit of MultipleGroupSet property has
     * one child only (which property is the last merging split data).
     */
    int count = 0;
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      gs.setMagnification(this.mMagnification);
      if (gs.setProperties(mp.childPropertyList.get(count)) == false) {
        return false;
      }
      count++;
      if (count >= mp.childPropertyList.size()) {
        count = mp.childPropertyList.size() - 1;
      }
    }
    ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
    lineGroup.clearLineStyleList();
    count = 0;
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY gs =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      SGLineStyle lineStyle = mp.childLineStyleList.get(count);
      gs.setLineStyle(lineStyle);
      count++;
      if (count >= mp.childLineStyleList.size()) {
        count = mp.childLineStyleList.size() - 1;
      }
      if (count < mp.childLineStyleList.size()) {
        lineGroup.addLineStyle(lineStyle);
      }
    }
    return true;
  }

  /**
   * Sets the x-axis.
   *
   * @param axis the x-axis
   * @return true if succeeded
   */
  public boolean setXAxis(final SGAxis axis) {
    if (super.setXAxis(axis) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      if (gs.setXAxis(axis) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the y-axis.
   *
   * @param axis the y-axis
   * @return true if succeeded
   */
  public boolean setYAxis(final SGAxis axis) {
    if (super.setYAxis(axis) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      if (gs.setYAxis(axis) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the x-axis location.
   *
   * @param location the location of the x-axis
   * @return true if succeeded
   */
  public boolean setXAxisLocation(final int location) {
    if (super.setXAxisLocation(location) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      if (gs.setXAxisLocation(location) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the y-axis location.
   *
   * @param location the location of the y-axis
   * @return true if succeeded
   */
  public boolean setYAxisLocation(final int location) {
    if (super.setYAxisLocation(location) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      if (gs.setYAxisLocation(location) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the name to this group set.
   *
   * @param name the name to set to this group set
   * @return true if succeeded
   */
  public boolean setName(final String name) {
    if (super.setName(name) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      if (gs.setName(name) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Sets the flag whether this data is visible in the legend
   *
   * @param b true to set visible
   * @return true if succeeded
   */
  public boolean setVisibleInLegend(final boolean b) {
    if (super.setVisibleInLegend(b) == false) {
      return false;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
      if (gs.setVisibleInLegend(b) == false) {
        return false;
      }
    }
    return true;
  }

  public SGElementGroupErrorBar createErrorBars(SGISXYTypeMultipleData dataXY) {
    if (this.addDrawingElementGroup(SGIElementGroupConstants.ERROR_BAR_GROUP) == false) {
      return null;
    }
    return this.getErrorBarGroup();
  }

  public SGElementGroupTickLabel createTickLabels(SGISXYTypeMultipleData dataXY) {
    if (this.addDrawingElementGroup(SGIElementGroupConstants.TICK_LABEL_GROUP) == false) {
      return null;
    }
    return this.getTickLabelGroup();
  }

  public List<SGElementGroupLine> getLineGroups() {
    return SGMultipleSXYUtility.getLineGroups(this);
  }

  public List<SGElementGroupLine> getLineGroupsIgnoreNull() {
    return SGMultipleSXYUtility.getLineGroupsIgnoreNull(this);
  }

  @Override
  public boolean setLineVisible(final boolean b) {
    return SGMultipleSXYUtility.setLineVisible(this.getLineGroupsIgnoreNull(), b);
  }

  @Override
  public boolean setLineWidth(final float width) {
    return SGMultipleSXYUtility.setLineWidth(this.getLineGroupsIgnoreNull(), width);
  }

  @Override
  public boolean setLineWidth(final float width, final String unit) {
    return SGMultipleSXYUtility.setLineWidth(this, width, unit);
  }

  @Override
  public boolean setLineType(final int type) {
    return SGMultipleSXYUtility.setLineType(this, type);
  }

  @Override
  public boolean setLineColor(final Color cl) {
    return SGMultipleSXYUtility.setLineColor(this, cl);
  }

  /**
   * Sets whether the lines connect all effective points.
   *
   * @return true if succeeded
   */
  @Override
  public boolean setLineConnectingAll(final boolean b) {
    return SGMultipleSXYUtility.setLineConnectingAll(this.getLineGroupsIgnoreNull(), b);
  }

  public List<SGElementGroupSymbol> getSymbolGroups() {
    return SGMultipleSXYUtility.getSymbolGroups(this);
  }

  public List<SGElementGroupSymbol> getSymbolGroupsIgnoreNull() {
    return SGMultipleSXYUtility.getSymbolGroupsIgnoreNull(this);
  }

  public boolean setSymbolVisible(final boolean b) {
    return SGMultipleSXYUtility.setSymbolVisible(this.getSymbolGroupsIgnoreNull(), b);
  }

  public boolean setSymbolType(final int type) {
    return SGMultipleSXYUtility.setSymbolType(this.getSymbolGroupsIgnoreNull(), type);
  }

  public boolean setSymbolSize(final float size) {
    return SGMultipleSXYUtility.setSymbolSize(this.getSymbolGroupsIgnoreNull(), size);
  }

  public boolean setSymbolSize(final float size, final String unit) {
    return SGMultipleSXYUtility.setSymbolSize(this.getSymbolGroupsIgnoreNull(), size, unit);
  }

  public boolean setSymbolLineWidth(final float width) {
    return SGMultipleSXYUtility.setSymbolLineWidth(this.getSymbolGroupsIgnoreNull(), width);
  }

  public boolean setSymbolLineWidth(final float width, final String unit) {
    return SGMultipleSXYUtility.setSymbolLineWidth(this.getSymbolGroupsIgnoreNull(), width, unit);
  }

  @Override
  public boolean setSymbolInnerPaint(final SGIPaint paint) {
    return SGMultipleSXYUtility.setSymbolInnerPaint(this.getSymbolGroupsIgnoreNull(), paint);
  }

  public boolean setSymbolLineColor(final Color cl) {
    return SGMultipleSXYUtility.setSymbolLineColor(this.getSymbolGroupsIgnoreNull(), cl);
  }

  @Override
  public boolean setSymbolLineVisible(boolean b) {
    return SGMultipleSXYUtility.setSymbolLineVisible(this.getSymbolGroupsIgnoreNull(), b);
  }

  public List<SGElementGroupBar> getBarGroups() {
    return SGMultipleSXYUtility.getBarGroups(this);
  }

  public List<SGElementGroupBar> getBarGroupsIgnoreNull() {
    return SGMultipleSXYUtility.getBarGroupsIgnoreNull(this);
  }

  public boolean setBarVisible(final boolean b) {
    return SGMultipleSXYUtility.setBarVisible(this.getBarGroupsIgnoreNull(), b);
  }

  public boolean setBarVertical(final boolean b) {
    return SGMultipleSXYUtility.setBarVertical(this.getBarGroupsIgnoreNull(), b);
  }

  public boolean setBarBaselineValue(final double value) {
    return SGMultipleSXYUtility.setBarBaselineValue(this.getBarGroupsIgnoreNull(), value);
  }

  public boolean setBarWidth(final float width) {
    return SGMultipleSXYUtility.setBarWidth(this.getBarGroupsIgnoreNull(), width);
  }

  public boolean setBarWidthValue(final double value) {
    return SGMultipleSXYUtility.setBarWidthValue(this.getBarGroupsIgnoreNull(), value);
  }

  public boolean setBarEdgeLineWidth(final float width) {
    return SGMultipleSXYUtility.setBarEdgeLineWidth(this.getBarGroupsIgnoreNull(), width);
  }

  public boolean setBarEdgeLineWidth(final float width, final String unit) {
    return SGMultipleSXYUtility.setBarEdgeLineWidth(this.getBarGroupsIgnoreNull(), width, unit);
  }

  @Override
  public boolean setBarInnerPaint(final SGIPaint paint) {
    return SGMultipleSXYUtility.setBarInnerPaint(this.getBarGroupsIgnoreNull(), paint);
  }

  @Override
  public boolean setBarEdgeLineColor(final Color cl) {
    return SGMultipleSXYUtility.setBarEdgeLineColor(this.getBarGroupsIgnoreNull(), cl);
  }

  @Override
  public boolean setBarEdgeLineVisible(final boolean visible) {
    return SGMultipleSXYUtility.setBarEdgeLineVisible(this.getBarGroupsIgnoreNull(), visible);
  }

  @Override
  public boolean setBarOffsetX(final double shift) {
    return SGMultipleSXYUtility.setBarOffsetX(this.getBarGroupsIgnoreNull(), shift);
  }

  @Override
  public boolean setBarOffsetY(final double shift) {
    return SGMultipleSXYUtility.setBarOffsetY(this.getBarGroupsIgnoreNull(), shift);
  }

  @Override
  public boolean setBarInterval(final double interval) {
    return SGMultipleSXYUtility.setBarInterval(this.getBarGroupsIgnoreNull(), interval);
  }

  public List<SGElementGroupErrorBar> getErrorBarGroups() {
    return SGMultipleSXYUtility.getErrorBarGroups(this);
  }

  public List<SGElementGroupErrorBar> getErrorBarGroupsIgnoreNull() {
    return SGMultipleSXYUtility.getErrorBarGroupsIgnoreNull(this);
  }

  public boolean setErrorBarVisible(final boolean b) {
    return SGMultipleSXYUtility.setErrorBarVisible(this.getErrorBarGroupsIgnoreNull(), b);
  }

  public boolean setErrorBarVertical(final boolean b) {
    return SGMultipleSXYUtility.setErrorBarVertical(this.getErrorBarGroupsIgnoreNull(), b);
  }

  public boolean setErrorBarHeadType(final int type) {
    return SGMultipleSXYUtility.setErrorBarHeadType(this.getErrorBarGroupsIgnoreNull(), type);
  }

  public boolean setErrorBarHeadSize(final float size) {
    return SGMultipleSXYUtility.setErrorBarHeadSize(this.getErrorBarGroupsIgnoreNull(), size);
  }

  public boolean setErrorBarHeadSize(final float size, final String unit) {
    return SGMultipleSXYUtility.setErrorBarHeadSize(this.getErrorBarGroupsIgnoreNull(), size, unit);
  }

  public boolean setErrorBarColor(final Color cl) {
    return SGMultipleSXYUtility.setErrorBarColor(this.getErrorBarGroupsIgnoreNull(), cl);
  }

  public boolean setErrorBarLineWidth(final float width) {
    return SGMultipleSXYUtility.setErrorBarLineWidth(this.getErrorBarGroupsIgnoreNull(), width);
  }

  public boolean setErrorBarLineWidth(final float width, final String unit) {
    return SGMultipleSXYUtility.setErrorBarLineWidth(
        this.getErrorBarGroupsIgnoreNull(), width, unit);
  }

  public boolean setErrorBarStyle(final int style) {
    return SGMultipleSXYUtility.setErrorBarStyle(this.getErrorBarGroupsIgnoreNull(), style);
  }

  @Override
  public boolean setErrorBarOnLinePosition(boolean b) {
    return SGMultipleSXYUtility.setErrorBarOnLinePosition(this.getErrorBarGroupsIgnoreNull(), b);
  }

  public List<SGElementGroupTickLabel> getTickLabelGroups() {
    return SGMultipleSXYUtility.getTickLabelGroups(this);
  }

  public List<SGElementGroupTickLabel> getTickLabelGroupsIgnoreNull() {
    return SGMultipleSXYUtility.getTickLabelGroupsIgnoreNull(this);
  }

  public boolean setTickLabelVisible(final boolean b) {
    return SGMultipleSXYUtility.setTickLabelVisible(this.getTickLabelGroupsIgnoreNull(), b);
  }

  public boolean setTickLabelHorizontalAlignment(final boolean b) {
    return SGMultipleSXYUtility.setTickLabelHorizontalAlignment(
        this.getTickLabelGroupsIgnoreNull(), b);
  }

  public boolean setTickLabelFontName(final String name) {
    return SGMultipleSXYUtility.setTickLabelFontName(this.getTickLabelGroupsIgnoreNull(), name);
  }

  public boolean setTickLabelFontStyle(final int style) {
    return SGMultipleSXYUtility.setTickLabelFontStyle(this.getTickLabelGroupsIgnoreNull(), style);
  }

  public boolean setTickLabelFontSize(final float size) {
    return SGMultipleSXYUtility.setTickLabelFontSize(this.getTickLabelGroupsIgnoreNull(), size);
  }

  public boolean setTickLabelFontSize(final float size, final String unit) {
    return SGMultipleSXYUtility.setTickLabelFontSize(
        this.getTickLabelGroupsIgnoreNull(), size, unit);
  }

  public boolean setTickLabelColor(final Color cl) {
    return SGMultipleSXYUtility.setTickLabelColor(this.getTickLabelGroupsIgnoreNull(), cl);
  }

  public boolean setTickLabelAngle(final float angle) {
    return SGMultipleSXYUtility.setTickLabelAngle(this.getTickLabelGroupsIgnoreNull(), angle);
  }

  public boolean setTickLabelDecimalPlaces(final int dp) {
    return SGMultipleSXYUtility.setTickLabelDecimalPlaces(this.getTickLabelGroupsIgnoreNull(), dp);
  }

  public boolean setTickLabelExponent(final int exp) {
    return SGMultipleSXYUtility.setTickLabelExponent(this.getTickLabelGroupsIgnoreNull(), exp);
  }

  /**
   * Returns whether this group set "contains" the given group set.
   *
   * @param gs the group set
   * @return true if this group set "contains" the given group set
   */
  public boolean contains(SGElementGroupSetForData gs) {
    if (super.contains(gs)) {
      return true;
    }
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      SGIElementGroupSetForData groupSet = this.mElementGroupSetList.get(ii);
      if (groupSet.equals(gs)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Sets the information of data columns. This method is overridden to update the child data
   * objects.
   *
   * @param columns information of data columns
   * @return true if succeeded
   */
  public boolean setColumnInfo(SGDataColumnInfo[] columns, String message) {
    if (!super.setColumnInfo(columns, message)) {
      return false;
    }
    if (SGMultipleSXYUtility.setColumnInfo(this, legend) == false) {
      return false;
    }
    return true;
  }

  /**
   * Sets the data.
   *
   * @param data a data object
   * @return true if succeeded
   */
  public boolean setData(SGData data) {
    if (super.setData(data) == false) {
      return false;
    }
    if (data == null) {
      for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);

        // disposes of the data of child group set
        SGData d = gs.getData();
        if (d != null) {
          d.dispose();
        }

        // set null
        gs.setData(null);
      }
    }
    return true;
  }

  /**
   * Updates the line style of child objects.
   *
   * @return true if succeeded
   */
  @Override
  public boolean initChildLineStyle() {
    final int dataNum = this.mElementGroupSetList.size();
    if (dataNum > 1 && this.mLineColorAutoAssigned) {
      List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
      HueColorMap model = new HueColorMap();
      for (int ii = 0; ii < dataNum; ii++) {
        ElementGroupSetInLegendSXY gs =
            (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
        Color cl = model.eval((double) ii / (dataNum - 1));
        SGLineStyle s = new SGLineStyle(DEFAULT_LINE_TYPE, cl, DEFAULT_LINE_WIDTH);
        gs.setLineStyle(s);
        lineStyleList.add(s);
      }
      // set to line group of this group set
      ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
      lineGroup.setLineStyleList(lineStyleList);
    } else {
      for (int ii = 0; ii < dataNum; ii++) {
        ElementGroupSetInLegendSXY gs =
            (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
        SGLineStyle s = new SGLineStyle(DEFAULT_LINE_TYPE, DEFAULT_LINE_COLOR, DEFAULT_LINE_WIDTH);
        gs.setLineStyle(s);

        // set to line group of this group set
        ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
        lineGroup.addLineStyle(s);
      }
    }
    return true;
  }

  /**
   * Sets the style of drawing elements.
   *
   * @param styleList the list of style
   * @return true if succeeded
   */
  @Override
  public boolean setStyle(List<SGStyle> styleList) {
    if (styleList.size() != this.mElementGroupSetList.size()) {
      return false;
    }
    Map<Integer, SGLineStyle> prevMap = this.getLineStyleMap();
    for (int ii = 0; ii < styleList.size(); ii++) {
      SGStyle s = styleList.get(ii);
      if (!(s instanceof SGLineStyle)) {
        return false;
      }
      SGLineStyle lineStyle = (SGLineStyle) s;
      if (!this.setLineStyle(lineStyle, ii)) {
        return false;
      }
      SGLineStyle prev = prevMap.get(ii);
      if (!lineStyle.equals(prev)) {
        this.setChanged(true);
      }
    }
    legend.notifyChangeOnCommit();
    return true;
  }

  /**
   * Returns the style of drawing elements.
   *
   * @return the list of style
   */
  @Override
  public List<SGStyle> getStyle() {
    Map<Integer, SGLineStyle> styleMap = this.getLineStyleMap();
    return new ArrayList<SGStyle>(styleMap.values());
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

  /**
   * Returns the color map manager for lines.
   *
   * @return the color map manager for lines
   */
  @Override
  public SGColorMapManager getLineColorMapManager() {
    return this.mLineColorMapManager;
  }

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
  @Override
  public boolean setLineColorMapName(String name) {
    super.setLineColorMapName(name);
    if (this.mLineColorMapManager.getColorMap(name) == null) {
      return false;
    }
    return true;
  }

  /**
   * Returns the color map for lines.
   *
   * @return the color map for lines
   */
  @Override
  public SGColorMap getLineColorMap() {
    return this.mLineColorMapManager.getColorMap(this.mLineColorMapName);
  }

  /**
   * Returns a map of the properties of line color maps.
   *
   * @return a map of the properties of line color maps
   */
  @Override
  public Map<String, SGProperties> getLineColorMapProperties() {
    return this.mLineColorMapManager.getColorMapProperties();
  }

  /**
   * Sets the properties of line color map.
   *
   * @param colorMapProperties the map of properties of color maps
   * @return true if succeeded
   */
  @Override
  public boolean setLineColorMapProperties(Map<String, SGProperties> colorMapProperties) {
    return this.mLineColorMapManager.setColorMapProperties(colorMapProperties);
  }

  @Override
  public boolean initLineStyle(Element el) {
    List<SGLineStyle> lineStyleList = new ArrayList<SGLineStyle>();
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY gs =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      SGElementGroupLine lineGroup = gs.getLineGroup();
      if (el != null) {
        if (SGElementGroupLine.readStyleProperty(el, lineGroup, ii) == false) {
          return false;
        }
      }
      SGLineStyle lineStyle = lineGroup.getLineStyle();
      gs.setLineStyle(lineStyle);
      lineStyleList.add(lineStyle);
    }
    ElementGroupLine lineGroup = (ElementGroupLine) this.getLineGroup();
    lineGroup.setLineStyleList(lineStyleList);
    return true;
  }

  @Override
  public boolean readColorMap(final Element el) {
    return this.mLineColorMapManager.readProperty(el);
  }

  /**
   * Returns the line style at given index.
   *
   * @param index index of child object
   * @return true if succeeded
   */
  @Override
  public SGLineStyle getLineStyle(final int index) {
    if (index < 0 || index >= this.getChildNumber()) {
      throw new IllegalArgumentException("Index out of bounds: " + index);
    }
    ElementGroupSetInLegendSXY gs =
        (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(index);
    return (SGLineStyle) gs.getLineStyle().clone();
  }

  /**
   * Sets the properties of a child data object.
   *
   * @param map a map of properties
   * @param childId ID of a child data object
   * @return the result of setting properties
   */
  @Override
  public SGPropertyResults setProperties(SGPropertyMap map, final int childId) {
    // do nothing
    return null;
  }

  /**
   * Sets the properties of color map.
   *
   * @param colorMapName the name of color map
   * @param map a map of properties
   * @return the result of setting properties
   */
  @Override
  public SGPropertyResults setColorMapProperties(final String colorMapName, SGPropertyMap map) {
    // do nothing
    return null;
  }

  /**
   * Updates the child objects.
   *
   * @return true if succeeded
   */
  @Override
  public boolean updateChild() {
    if (!legend.updateChildGroupSet(this)) {
      return false;
    }
    if (!this.initChildLineStyle()) {
      return false;
    }
    return true;
  }

  @Override
  public boolean isDataShifted() {
    SGISXYTypeMultipleData sxyData = (SGISXYTypeMultipleData) this.mData;
    SGTuple2d shift = sxyData.getShift();
    return !shift.isZero();
  }

  /**
   * Replaces the data source.
   *
   * @param srcOld old data source
   * @param srcNew new data source
   * @param obs data source observer
   */
  @Override
  public void replaceDataSource(
      final SGIDataSource srcOld, final SGIDataSource srcNew, final SGDataSourceObserver obs) {
    super.replaceDataSource(srcOld, srcNew, obs);
    for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
      ElementGroupSetInLegendSXY gs =
          (ElementGroupSetInLegendSXY) this.mElementGroupSetList.get(ii);
      gs.replaceDataSource(srcOld, srcNew, obs);
    }
  }

  @Override
  public boolean getAxisDateMode(final int location) {
    return legend.mAxisElement.getAxisDateMode(location);
  }

  @Override
  public boolean setTickLabelDateFormat(String format) {
    return SGMultipleSXYUtility.setTickLabelDateFormat(this.getTickLabelGroupsIgnoreNull(), format);
  }

  @Override
  public boolean hasDateTickLabels() {
    SGISXYTypeMultipleData sxyDate = (SGISXYTypeMultipleData) this.mData;
    return !sxyDate.hasGenericTickLabels();
  }
}
