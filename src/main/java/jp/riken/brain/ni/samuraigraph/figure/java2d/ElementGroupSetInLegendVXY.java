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
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.figure.*;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphVXY.ElementGroupSetInVXYGraphProperties;

class ElementGroupSetInLegendVXY extends ElementGroupSetInLegend
    implements SGIElementGroupSetVXY, SGIVXYDataDialogObserver, SGIVXYDataConstants {
  private final SGFigureElementLegend legend;

  /** */
  protected ElementGroupSetInLegendVXY(final SGFigureElementLegend legend, SGData data) {
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

  // scaling factor for the magnitude of vectors
  private float mMagnitudePerCM = 1.0f;

  public float getMagnitudePerCM() {
    return this.mMagnitudePerCM;
  }

  public boolean setMagnitudePerCM(final float mag) {
    if (mag <= 0.0) {
      throw new IllegalArgumentException("mag <= 0.0");
    }
    SGIVXYTypeData vData = (SGIVXYTypeData) this.mData;
    if (Float.isNaN(mag)) {
      this.mMagnitudePerCM = mag;
    } else {
      this.mMagnitudePerCM = SGDataStrideUtility.roundMagnitudePerCM(mag, vData);
    }
    return true;
  }

  // a flag whether to fix the direction of each vector
  private boolean mDirectionFixedFlag;

  public boolean isDirectionInvariant() {
    return this.mDirectionFixedFlag;
  }

  public boolean setDirectionInvariant(final boolean b) {
    this.mDirectionFixedFlag = b;
    return true;
  }

  /** */
  public boolean addDrawingElementGroup(final int type) {
    SGElementGroup group = null;
    if (type == SGIElementGroupConstants.ARROW_GROUP) {
      group = new ElementGroupArrow(legend);
    } else {
      throw new Error();
    }

    this.addDrawingElementGroup(group);

    return true;
  }

  /** */
  void paintSymbol(final Graphics2D g2d) {
    // arrow
    ElementGroupArrow groupArrow = legend.getGroupArrow(this);
    if (groupArrow != null) {
      if (groupArrow.isVisible()) {
        groupArrow.paintElement(g2d);
      }
    }
  }

  boolean onMouseClicked(final MouseEvent e) {
    // do nothing
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

  public float getLineWidth(final String unit) {
    return this.getArrowGroup().getLineWidth(unit);
  }

  public int getLineType() {
    return this.getArrowGroup().getLineType();
  }

  public Color getColor() {
    return this.getArrowGroup().getColor();
  }

  public float getHeadSize(final String unit) {
    return this.getArrowGroup().getHeadSize(unit);
  }

  public float getHeadOpenAngle() {
    return this.getArrowGroup().getHeadOpenAngle();
  }

  public float getHeadCloseAngle() {
    return this.getArrowGroup().getHeadCloseAngle();
  }

  public int getStartHeadType() {
    return this.getArrowGroup().getStartHeadType();
  }

  public int getEndHeadType() {
    return this.getArrowGroup().getEndHeadType();
  }

  /** */
  public boolean setXAxisLocation(final int config) {
    this.mXAxis = this.getAxis(config);
    return true;
  }

  /** */
  public boolean setYAxisLocation(final int config) {
    this.mYAxis = this.getAxis(config);
    return true;
  }

  public boolean setLineWidth(final float width, final String unit) {
    return this.getArrowGroup().setLineWidth(width, unit);
  }

  public boolean setLineType(final int type) {
    return this.getArrowGroup().setLineType(type);
  }

  public boolean setColor(final Color cl) {
    return this.getArrowGroup().setColor(cl);
  }

  public boolean setHeadSize(final float size, final String unit) {
    return this.getArrowGroup().setHeadSize(size, unit);
  }

  public boolean setHeadAngle(final Float openAngle, final Float closeAngle) {
    return this.getArrowGroup().setHeadAngle(openAngle, closeAngle);
  }

  public boolean setStartHeadType(final int type) {
    return this.getArrowGroup().setStartHeadType(type);
  }

  public boolean setEndHeadType(final int type) {
    return this.getArrowGroup().setEndHeadType(type);
  }

  public boolean hasValidAngle(final Number open, final Number close) {
    final float openAngle = (open != null) ? open.floatValue() : this.getHeadOpenAngle();
    final float closeAngle = (close != null) ? close.floatValue() : this.getHeadCloseAngle();
    return (openAngle < closeAngle);
  }

  /** */
  public SGProperties getProperties() {
    ElementGroupSetInVXYGraphProperties ep = new ElementGroupSetInVXYGraphProperties();
    if (this.getProperties(ep) == false) {
      return null;
    }

    return ep;
  }

  /** */
  public boolean getProperties(final SGProperties p) {
    if ((p instanceof ElementGroupSetInVXYGraphProperties) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }

    ElementGroupSetInVXYGraphProperties ep = (ElementGroupSetInVXYGraphProperties) p;
    ep.mMagnitudeScalingFactor = this.getMagnitudePerCM();
    ep.mDirectionInvariant = this.isDirectionInvariant();

    return true;
  }

  /** */
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof ElementGroupSetInVXYGraphProperties) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }

    ElementGroupSetInVXYGraphProperties ep = (ElementGroupSetInVXYGraphProperties) p;
    this.setMagnitudePerCM(ep.mMagnitudeScalingFactor);
    this.setDirectionInvariant(ep.mDirectionInvariant);

    return true;
  }

  /**
   * Returns a list of arrow groups.
   *
   * @return a list of arrow groups
   */
  public List<SGElementGroupArrow> getArrowGroups() {
    List<SGElementGroupArrow> retList = new ArrayList<SGElementGroupArrow>();
    List<SGElementGroup> list =
        SGUtilityForFigureElement.getGroups(
            SGElementGroupArrow.class, this.mDrawingElementGroupList);
    for (int ii = 0; ii < list.size(); ii++) {
      retList.add((SGElementGroupArrow) list.get(ii));
    }
    return retList;
  }

  /**
   * Returns an arrow group which is the first element of an array.
   *
   * @return the first element of an array of arrow groups, or null when this group set does not
   *     have any arrow groups
   */
  public SGElementGroupArrow getArrowGroup() {
    return (SGElementGroupArrow)
        SGUtilityForFigureElement.getGroup(
            SGElementGroupArrow.class, this.mDrawingElementGroupList);
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
      if (gp instanceof SGElementGroupArrow.ArrowProperties) {
        group = this.getArrowGroup();
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

  @Override
  public boolean setStrideX(SGIntegerSeriesSet stride) {
    return this.setStrideXToData(stride);
  }

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
    return this.setIndexStride(stride);
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
