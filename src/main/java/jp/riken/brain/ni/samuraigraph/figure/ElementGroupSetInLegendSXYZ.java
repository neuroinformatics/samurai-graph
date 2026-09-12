package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.*;
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
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;

class ElementGroupSetInLegendSXYZ extends ElementGroupSetInLegend
    implements SGIElementGroupSetSXYZ, SGISXYZDataConstants, SGISXYZDataDialogObserver {
  private final SGFigureElementLegend legend;

  protected ElementGroupSetInLegendSXYZ(final SGFigureElementLegend legend, SGData data) {
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

  boolean onMouseClicked(MouseEvent e) {
    return false;
  }

  /** Paint the symbol in legend. */
  void paintSymbol(Graphics2D g2d) {
    // Color Map
    ElementGroupPseudocolorMap groupBar = legend.getColorMap(this);
    if (groupBar != null) {
      if (groupBar.isVisible()) {
        groupBar.paintElement(g2d);
      }
    }
  }

  public boolean addDrawingElementGroup(int type) {
    SGElementGroup group = null;
    if (type == SGIElementGroupConstants.RECTANGLE_GROUP) {
      SGElementGroupPseudocolorMap colorMap = new ElementGroupPseudocolorMap(legend);
      colorMap.setColorBarModel(legend.getColorBarModel());
      group = colorMap;
    } else {
      throw new Error();
    }
    this.addDrawingElementGroup(group);
    return true;
  }

  public SGElementGroupPseudocolorMap getColorMap() {
    return legend.getColorMap(this);
  }

  public boolean setZAxis(SGAxis axis) {
    this.mZAxis = axis;
    return true;
  }

  public double getRectangleWidthValue() {
    return this.getColorMap().getWidthValue();
  }

  public double getRectangleHeightValue() {
    return this.getColorMap().getHeightValue();
  }

  public boolean setRectangleWidthValue(double value) {
    this.getColorMap().setWidthValue(value);
    return true;
  }

  public boolean setRectangleHeightValue(double value) {
    this.getColorMap().setHeightValue(value);
    return true;
  }

  public boolean hasValidRectangleWidthValue(int config, Number value) {
    final SGAxis axis = (config == -1) ? this.mXAxis : legend.mAxisElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getRectangleWidthValue();
    return axis.isValidValue(v);
  }

  public boolean hasValidRectangleHeightValue(int config, Number value) {
    final SGAxis axis = (config == -1) ? this.mYAxis : legend.mAxisElement.getAxisInPlane(config);
    final double v = (value != null) ? value.doubleValue() : this.getRectangleHeightValue();
    return axis.isValidValue(v);
  }

  public int getXAxisLocation() {
    return legend.mAxisElement.getLocationInPlane(this.getXAxis());
  }

  public int getYAxisLocation() {
    return legend.mAxisElement.getLocationInPlane(this.getYAxis());
  }

  public boolean setXAxisLocation(int location) {
    this.mXAxis = this.getAxis(location);
    return true;
  }

  public boolean setYAxisLocation(int location) {
    this.mYAxis = this.getAxis(location);
    return true;
  }

  /**
   * Returns a list of color map groups.
   *
   * @return a list of color map groups
   */
  public List<SGElementGroupPseudocolorMap> getColorMapGroups() {
    List<SGElementGroupPseudocolorMap> retList = new ArrayList<SGElementGroupPseudocolorMap>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(
            SGElementGroupPseudocolorMap.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupPseudocolorMap) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns a color map group which is the first element of an array.
   *
   * @return the first element of an array of arrow groups, or null when this group set does not
   *     have any arrow groups
   */
  public SGElementGroupPseudocolorMap getColorMapGroup() {
    return (SGElementGroupPseudocolorMap)
        SGUtilityForFigureElement.getGroup(
            SGElementGroupPseudocolorMap.class, this.mDrawingElementGroupList);
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
      if (gp instanceof SGElementGroupPseudocolorMap.PseudocolorMapProperties) {
        group = this.getColorMapGroup();
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
   * Updates the size of color map.
   *
   * @return true if succeeded
   */
  public boolean updateColorMapSize() {

    SGISXYZTypeData data = (SGISXYZTypeData) this.getData();

    SGAxis axisX = this.getXAxis();
    final double[] xArray = data.getXValueArray(false);
    final double widthValue = SGUtilityForFigureElementJava2D.calcSizeValue(xArray, axisX, true);

    SGAxis axisY = this.getYAxis();
    final double[] yArray = data.getYValueArray(false);
    final double heightValue = SGUtilityForFigureElementJava2D.calcSizeValue(yArray, axisY, false);

    SGElementGroupPseudocolorMap colorMap = (SGElementGroupPseudocolorMap) this.getColorMap();
    colorMap.setWidthValue(widthValue);
    colorMap.setHeightValue(heightValue);

    return true;
  }

  /**
   * Sets the stride for the x-direction.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  @Override
  public boolean setStrideX(SGIntegerSeriesSet stride) {
    return this.setStrideXToData(stride);
  }

  /**
   * Sets the stride for the y-direction.
   *
   * @param stride stride of arrays
   * @return true if succeeded
   */
  @Override
  public boolean setStrideY(SGIntegerSeriesSet stride) {
    return this.setStrideYToData(stride);
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

  @Override
  public Map<String, SGProperties> getElementGroupPropertiesMap() {
    return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
  }

  @Override
  public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
    return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
  }

  @Override
  public boolean getAxisDateMode(final int location) {
    return legend.mAxisElement.getAxisDateMode(location);
  }
}
