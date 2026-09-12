package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.figure.SGILegendConstants.*;

import java.awt.*;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.data.*;
import org.w3c.dom.Element;

abstract class ElementGroupSetInLegend extends SGElementGroupSetForData implements SGISelectable {
  private final SGFigureElementLegend legend;

  /** A rectangle for this legend. */
  protected Rectangle2D mDataRect = null;

  /** String object which has a text actually displayed on the screen. */
  protected SGDrawingElementString2DExtended mDrawingString = null;

  /** Temporary properties for this data. */
  protected SGProperties mTemporaryPropertiesInner = null;

  /** The default constructor. */
  protected ElementGroupSetInLegend(final SGFigureElementLegend legend, SGData data) {
    super(data);
    this.legend = legend;
  }

  @Override
  public ArrayList<SGINode> getChildNodes() {
    return new ArrayList<>();
  }

  @Override
  public String getClassDescription() {
    return "Legend";
  }

  @Override
  public String getInstanceDescription() {
    return this.getName() != null ? this.getName() : "Legend";
  }

  /**
   * Returns the commands to move data objects.
   *
   * @return the commands
   */
  protected List<String> getMoveCommandList() {
    List<String> list = new ArrayList<String>();
    list.add(MENUCMD_MOVE_TO_TOP);
    list.add(MENUCMD_MOVE_TO_UPPER);
    list.add(MENUCMD_MOVE_TO_LOWER);
    list.add(MENUCMD_MOVE_TO_BOTTOM);
    return list;
  }

  /**
   * Returns whether this group set is visible in two means: the data is not deleted and visibility
   * flag is set to true.
   *
   * @return true if this legend is not deleted and is visible
   */
  boolean isViewable() {
    return (this.isVisible() && this.isVisibleInLegend());
  }

  /**
   * Returns whether this group set is visible.
   *
   * @return true if this legend is visible
   */
  public boolean getLegendVisibleFlag() {
    return this.isVisibleInLegend();
  }

  /**
   * Sets the location of x-axis.
   *
   * @param location the location of the x-axis
   */
  public boolean setXAxisLocation(final int location) {
    if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
        && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
      return false;
    }
    this.mXAxis = legend.mAxisElement.getAxisInPlane(location);
    return true;
  }

  /**
   * Sets the location of y-axis.
   *
   * @param location the location of the y-axis
   */
  public boolean setYAxisLocation(final int location) {
    if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
        && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
      return false;
    }
    this.mYAxis = legend.mAxisElement.getAxisInPlane(location);
    return true;
  }

  /**
   * Create a string object to display the data name.
   *
   * @return true if succeeded
   */
  boolean createStringElement() {
    SGDrawingElementString2DExtended el =
        new SGDrawingElementString2DExtended(
            this.mName,
            legend.mStyleHolder.getFontName(),
            legend.mStyleHolder.getFontStyle(),
            legend.mStyleHolder.getFontSize(),
            legend.mStyleHolder.getFontColor(),
            this.getMagnification(),
            0.0f);
    this.mDrawingString = el;
    return true;
  }

  /**
   * @return
   */
  private boolean paintString(final Graphics2D g2d) {
    if (this.mDrawingString != null) {
      this.mDrawingString.paint(g2d);
    }
    return true;
  }

  /**
   * Sets the magnification.
   *
   * @param mag the magnification to set
   * @return true if succeeded
   */
  public boolean setMagnification(final float mag) {
    if (super.setMagnification(mag) == false) {
      return false;
    }
    if (this.mDrawingString != null) {
      if (this.mDrawingString.setMagnification(mag) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * @return
   */
  public SGDrawingElementString2DExtended getStringElement() {
    return this.mDrawingString;
  }

  /**
   * @return
   */
  public Rectangle2D getStringBounds() {
    if (this.mDrawingString == null) {
      return null;
    }
    Rectangle2D rect = this.mDrawingString.getStringRect();
    final float visual_height = (float) rect.getHeight();
    final float visual_y = (float) rect.getY();
    final float strike_through_offset = this.mDrawingString.getStrikethroughOffset();
    final float advance = this.mDrawingString.getAdvance();
    final float top = -visual_y + strike_through_offset;
    final float bottom = visual_height - top;
    float glowH = 0.0f;
    float glowY = 0.0f;
    if (bottom > top) {
      glowH = bottom - top;
      glowY = glowH;
    } else {
      glowH = top - bottom;
    }
    rect.setRect(0, glowY, advance, visual_height + glowH);
    return rect;
  }

  /**
   * @param rect
   */
  void setRect(Rectangle2D rect) {
    this.mDataRect = rect;
  }

  /** */
  public float getMaxDataElementWidth() {
    float max = 0.0f;
    final List<SGElementGroup> list = this.mDrawingElementGroupList;
    for (int ii = 0; ii < list.size(); ii++) {
      ILegendElement el = (ILegendElement) list.get(ii);
      final float width = el.getPreferredWidth();
      if (width > max) {
        max = width;
      }
    }

    return max;
  }

  /** */
  public double getMaxDataElementHeight() {
    double max = 0.0;
    List<SGElementGroup> list = this.mDrawingElementGroupList;
    for (int ii = 0; ii < list.size(); ii++) {
      SGElementGroup group = (SGElementGroup) list.get(ii);
      if (!group.isVisible()) {
        continue;
      }
      ILegendElement el = (ILegendElement) list.get(ii);
      double height = el.getPreferredHeight();
      if (height > max) {
        max = height;
      }
    }

    return max;
  }

  /**
   * @param rect
   */
  void setDrawingElementBounds(final Rectangle2D rect) {
    List<SGElementGroup> list = this.mDrawingElementGroupList;
    for (int ii = 0; ii < list.size(); ii++) {
      SGElementGroup group = (SGElementGroup) list.get(ii);
      if (!group.isVisible()) {
        continue;
      }
      ILegendElement el = (ILegendElement) list.get(ii);
      el.setDataElementBounds(rect);
    }
  }

  /**
   * @return
   */
  protected boolean createDrawingElement() {
    final List<SGElementGroup> list = this.mDrawingElementGroupList;
    for (int ii = 0; ii < list.size(); ii++) {
      SGElementGroup group = (SGElementGroup) list.get(ii);
      if (group.isVisible()) {
        ILegendElement el = (ILegendElement) group;
        el.createDrawingElementInLegend();
      }
    }

    return true;
  }

  /** */
  public void paintGraphics2D(final Graphics2D g2d) {
    // draw the name of data
    g2d.setPaint(legend.mStyleHolder.getFontColor());
    this.paintString(g2d);

    // draw the symbols
    this.paintSymbol(g2d);
  }

  abstract void paintSymbol(Graphics2D g2d);

  abstract boolean onMouseClicked(final MouseEvent e);

  /** */
  boolean addDrawingElementGroup(final SGElementGroup group) {

    ILegendElement lElement = (ILegendElement) group;
    lElement.setElementGroupSet(this);

    // create drawing elements
    group.initDrawingElement(lElement.getNumberOfPoints());

    // // set the properties to drawing elements
    // if (group.setPropertiesOfDrawingElements() == false) {
    // return false;
    // }

    // set magnification
    group.setMagnification(this.getMagnification());

    // add to the list
    this.mDrawingElementGroupList.add(group);

    return true;
  }

  protected SGAxis getAxis(final int location) {
    return legend.mAxisElement.getAxisInPlane(location);
  }

  /**
   * Returns a property dialog.
   *
   * @return property dialog
   */
  public SGPropertyDialog getPropertyDialog() {
    SGData data = this.getData();
    // SGPropertyDialog dg =
    // legend.getDataDialog(data.getDataType());
    SGPropertyDialog dg = legend.getDataDialog(data);
    return dg;
  }

  /** Prepare before the property setting starts. */
  public boolean prepare() {
    // create temporary properties
    this.mTemporaryPropertiesInner = this.getProperties();
    return true;
  }

  /**
   * Update drawing elements with related data object.
   *
   * @return true if succeeded
   */
  public boolean updateWithData() {
    // update drawing elements
    if (legend.updateAllDrawingElements() == false) {
      return false;
    }
    return true;
  }

  /**
   * Commit the change of the properties.
   *
   * @return true if succeeded
   */
  public boolean commit() {

    // compare current data properties and temporary data properties and notify the
    // change
    this.notifyDataProperties(
        this.mTemporaryPropertiesInner,
        SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT,
        SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_COMMIT);

    // compare two properties
    SGProperties pTemp = this.mTemporaryPropertiesInner;
    SGProperties pPresent = this.getProperties();
    if (pTemp.equals(pPresent) == false) {
      this.setChanged(true);
    }

    // clear temporary properties
    this.mTemporaryPropertiesInner = null;

    // update drawing elements
    if (this.updateWithData() == false) {
      return false;
    }
    legend.notifyChangeOnCommit();
    legend.repaint();

    return true;
  }

  /**
   * Cancel the setting of properties.
   *
   * @return true if succeeded
   */
  public boolean cancel() {

    // compare current data properties and temporary data properties and notify the
    // change
    this.notifyDataProperties(
        this.mTemporaryPropertiesInner,
        SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_CANCEL,
        SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_CANCEL);

    // set temporary properties to drawing elements to cancel the change
    if (this.setProperties(this.mTemporaryPropertiesInner) == false) {
      return false;
    }

    // clear temporary properties
    this.mTemporaryPropertiesInner = null;

    // update drawing elements
    if (this.updateWithData() == false) {
      return false;
    }
    legend.notifyChangeOnCancel();
    legend.repaint();

    return true;
  }

  /**
   * Set properties from the property dialog.
   *
   * @return true if succeeded
   */
  public boolean preview() {

    // compare current data properties and temporary data properties and notify the
    // change
    this.notifyDataProperties(
        this.mTemporaryPropertiesInner,
        SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW,
        SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_PREVIEW);

    // update drawing elements
    if (this.updateWithData() == false) {
      return false;
    }
    legend.notifyChange();
    legend.repaint();

    return true;
  }

  @Override
  protected void notifyToListener(final String msg) {
    legend.notifyToListener(msg);
  }

  @Override
  protected void notifyToListener(final String msg, final Object source) {
    legend.notifyToListener(msg, source);
  }

  /**
   * @return
   */
  public String getTagName() {
    return "";
  }

  /** */
  public boolean writeProperty(final Element el) {
    return true;
  }

  /** */
  public void actionPerformed(final ActionEvent e) {
    String command = e.getActionCommand();
    Object source = e.getSource();

    if (command.equals(SGIConstants.MENUCMD_PROPERTY)) {
      // clear the selection of figure element
      legend.setSelected(false);

      // show the property dialog of a legend
      legend.setPropertiesOfSelectedObjects(this);
    } else if (command.equals(MENUCMD_MOVE_TO_TOP)) {
      legend.moveChildToEnd(this.getID(), false);
    } else if (command.equals(MENUCMD_MOVE_TO_UPPER)) {
      legend.moveChild(this.getID(), false);
    } else if (command.equals(MENUCMD_MOVE_TO_LOWER)) {
      legend.moveChild(this.getID(), true);
    } else if (command.equals(MENUCMD_MOVE_TO_BOTTOM)) {
      legend.moveChildToEnd(this.getID(), true);
      // } else if (command.equals(SGIConstants.MENUCMD_ANIMATION)) {
      // this.mTempDataProperties = this.mData.getProperties();
      // notifyToListener(command);
      // doAnimation(this);
    } else {
      notifyToListener(command, source);
    }

    // update drawing elements
    legend.updateAllDrawingElements();
    legend.repaint();
  }

  /** */
  public boolean getProperties(final SGProperties p) {
    if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
    SGIFigureElementAxis aElement = legend.mAxisElement;
    ep.xAxis = aElement.getLocationInPlane(this.getXAxis());
    ep.yAxis = aElement.getLocationInPlane(this.getYAxis());
    return true;
  }

  /** */
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }
    ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
    SGIFigureElementAxis aElement = legend.mAxisElement;
    this.setXAxis(aElement.getAxisInPlane(ep.xAxis));
    this.setYAxis(aElement.getAxisInPlane(ep.yAxis));
    return true;
  }

  /** undo */
  public boolean setMementoBackward() {
    if (super.setMementoBackward() == false) return false;

    legend.updateAllDrawingElements();
    legend.notifyChangeOnUndo();

    return true;
  }

  /** redo */
  public boolean setMementoForward() {
    if (super.setMementoForward() == false) return false;

    legend.updateAllDrawingElements();
    legend.notifyChangeOnUndo();

    return true;
  }

  /** */
  public void notifyToRoot() {
    legend.notifyToRoot();
  }

  /**
   * Sets the current frame index.
   *
   * @param index the frame index to be set
   */
  public void setCurrentFrameIndex(int index) {
    super.setCurrentFrameIndex(index);
    // notify the change
    legend.notifyChange();
  }

  protected void onSaveChanges() {
    // notify the change
    this.setChanged(true);
    legend.notifyChangeOnCommit();
    this.notifyToRoot();
  }

  /** Cancels all changes of this data source. */
  public void cancelChanges() {
    super.cancelChanges();
    // notify the change
    legend.notifyChange();
  }

  /**
   * Sets the properties.
   *
   * @param map a map of properties
   * @return the result of setting properties
   */
  public SGPropertyResults setProperties(SGPropertyMap map) {
    // do nothing
    return null;
  }

  @Override
  protected boolean checkChanged() {
    if (this.mTemporaryPropertiesInner == null) {
      return false;
    }
    return !this.mTemporaryPropertiesInner.equals(this.getProperties());
  }
}
