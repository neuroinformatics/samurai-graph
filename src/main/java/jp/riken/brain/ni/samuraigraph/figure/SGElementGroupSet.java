package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGAnimationConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDateConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPaintConstant.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGTextDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGColorMapConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGElementGroupConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGFigureDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSXYDataConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGStringConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGSymbolConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGTimingLineConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGVXYDataConstants.*;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIVisible;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Base class for the set of the groups of drawing elements. This object has an array of drawing
 * element groups.
 */
public abstract class SGElementGroupSet implements SGIVisible, SGIDisposable {

  /** The list of groups of drawing elements. */
  protected List<SGElementGroup> mDrawingElementGroupList = new ArrayList<SGElementGroup>();

  /** The magnification. */
  protected float mMagnification = 1.0f;

  /** The flag of visibility. */
  protected boolean mVisibleFlag = true;

  /** The default constructor. */
  public SGElementGroupSet() {
    super();
  }

  /** Disposes of this object. */
  public void dispose() {
    this.mDisposed = true;
    for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
      group.dispose();
    }
    this.mDrawingElementGroupList.clear();
    this.mDrawingElementGroupList = null;
  }

  /** Returns a new instance of a list of groups of drawing elements. */
  public ArrayList<SGElementGroup> getElementGroupList() {
    return new ArrayList<SGElementGroup>(this.mDrawingElementGroupList);
  }

  /**
   * Move a group of drawing elements to front.
   *
   * @param group a group to move
   */
  public boolean moveElementsToFront(final SGElementGroup group) {
    List<SGElementGroup> groupList = this.mDrawingElementGroupList;
    for (int ii = 0; ii < groupList.size(); ii++) {
      if (group.equals(groupList.get(ii))) {
        groupList.remove(ii);
        break;
      }
    }
    groupList.add(groupList.size(), group);
    return true;
  }

  /**
   * Move a group of drawing elements to back.
   *
   * @param group a group to move
   */
  public boolean moveElementsToBack(final SGElementGroup group) {
    List<SGElementGroup> groupList = this.mDrawingElementGroupList;
    for (int ii = 0; ii < groupList.size(); ii++) {
      if (group.equals(groupList.get(ii))) {
        groupList.remove(ii);
        break;
      }
    }
    groupList.add(0, group);
    return true;
  }

  /**
   * Remove a group of drawing elements.
   *
   * @param group a group to remove
   */
  public boolean removeElements(final SGElementGroup group) {
    List<SGElementGroup> groupList = this.mDrawingElementGroupList;
    for (int ii = 0; ii < groupList.size(); ii++) {
      if (group.equals(groupList.get(ii))) {
        groupList.remove(ii);
        return true;
      }
    }
    return false;
  }

  /**
   * Set the visibility of this group set.
   *
   * @param b a value to set to the visibility
   */
  public void setVisible(final boolean b) {
    this.mVisibleFlag = b;
  }

  /** Returns the visibility of this group set. */
  public boolean isVisible() {
    return this.mVisibleFlag;
  }

  /** Returns the magnification. */
  public float getMagnification() {
    return this.mMagnification;
  }

  /**
   * Set the magnification.
   *
   * @param mag a value to set to the magnification
   */
  public boolean setMagnification(final float mag) {
    this.mMagnification = mag;
    for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
      if (group.setMagnification(mag) == false) {
        return false;
      }
    }
    return true;
  }

  /**
   * Add a group of drawing elements to this group set.
   *
   * @param type the type of group
   */
  public abstract boolean addDrawingElementGroup(final int type);

  /**
   * Paint this group set.
   *
   * @param g2d graphic context
   */
  public abstract void paintGraphics2D(final Graphics2D g2d);

  /**
   * Returns whether this group set contains the given point.
   *
   * @param x x coordinate
   * @param y y coordinate
   */
  public boolean contains(final int x, final int y) {
    List<SGElementGroup> list = this.mDrawingElementGroupList;
    for (int ii = list.size() - 1; ii >= 0; ii--) {
      SGElementGroup group = (SGElementGroup) list.get(ii);
      if (group.isVisible() == false) {
        continue;
      }
      if (group.contains(x, y)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns an element group at a given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public SGElementGroup getElementGroupAt(final int x, final int y) {
    List<SGElementGroup> list = this.mDrawingElementGroupList;
    for (int ii = list.size() - 1; ii >= 0; ii--) {
      SGElementGroup group = (SGElementGroup) list.get(ii);
      if (group.isVisible() == false) {
        continue;
      }
      if (group.contains(x, y)) {
        return group;
      }
    }
    return null;
  }

  /** Returns the name of tag in property file. */
  public abstract String getTagName();

  /**
   * Write properties of this object to the Element.
   *
   * @param el the Element object
   * @param params the operation
   */
  public abstract boolean writeProperty(final Element el, final SGExportParameter params);

  /**
   * Create an Element object and write properties of this group set and element groups.
   *
   * @param document the Document object
   * @param params the operation
   */
  public Element createElement(final Document document, final SGExportParameter params) {

    // create an Element object
    Element el = document.createElement(this.getTagName());

    // write properties of this group set
    if (this.writeProperty(el, params) == false) {
      return null;
    }

    // create Element objects and write properties for all element groups
    for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
      String tagName = group.getTagName();
      Element elGroup = document.createElement(tagName);
      if (group.writeProperty(elGroup) == false) {
        return null;
      }
      el.appendChild(elGroup);
    }

    return el;
  }

  /**
   * Check whether this group set contains a given point.
   *
   * @param x the x coordinate
   * @param y the y coordinate
   */
  public abstract boolean onDrawingElement(final int x, final int y);

  /**
   * Set properties to this group set.
   *
   * @param p properties to set to this group set
   */
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof ElementGroupSetProperties) == false) {
      return false;
    }
    ElementGroupSetProperties ep = (ElementGroupSetProperties) p;
    this.setVisible(ep.visible);

    // set properties of element groups
    if (this.setElementGroupProperties(ep.mElementGroupPropertiesList) == false) {
      return false;
    }

    return true;
  }

  /**
   * Sets the properties of element groups.
   *
   * @param elementGroupPropertiesList
   */
  protected abstract boolean setElementGroupProperties(
      List<SGProperties> elementGroupPropertiesList);

  /** Returns properties of this group set. */
  public SGProperties getProperties() {
    ElementGroupSetProperties p = new ElementGroupSetProperties();
    if (this.getProperties(p) == false) {
      return null;
    }
    return p;
  }

  /**
   * Get properties of this group set.
   *
   * @param p a property object to be set
   */
  public boolean getProperties(final SGProperties p) {
    if ((p instanceof ElementGroupSetProperties) == false) {
      return false;
    }
    ElementGroupSetProperties gp = (ElementGroupSetProperties) p;
    gp.visible = this.isVisible();
    (gp.mElementGroupPropertiesList).clear();
    for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
      SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
      (gp.mElementGroupPropertiesList).add(group.getProperties());
    }
    return true;
  }

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("[");
    sb.append("visible=");
    sb.append(this.mVisibleFlag);
    sb.append(", ");
    sb.append("]");
    return sb.toString();
  }

  /** A class of properties of a group set. */
  public static class ElementGroupSetProperties extends SGProperties {

    /** visibility */
    protected boolean visible;

    /** a list of properties of element groups in the group set */
    protected List<SGProperties> mElementGroupPropertiesList = new ArrayList<SGProperties>();

    /** The default constructor. */
    public ElementGroupSetProperties() {
      super();
    }

    /** Copy this object. */
    public Object copy() {
      Object obj = super.copy();
      ElementGroupSetProperties p = (ElementGroupSetProperties) obj;
      List<SGProperties> list = new ArrayList<>();
      for (int ii = 0; ii < this.mElementGroupPropertiesList.size(); ii++) {
        SGProperties gp = this.mElementGroupPropertiesList.get(ii);
        list.add((SGProperties) gp.copy());
      }
      p.mElementGroupPropertiesList = list;
      return p;
    }

    /** Disposes of this object. */
    public void dispose() {
      super.dispose();
      for (int ii = 0; ii < this.mElementGroupPropertiesList.size(); ii++) {
        SGProperties p = this.mElementGroupPropertiesList.get(ii);
        p.dispose();
      }
      this.mElementGroupPropertiesList.clear();
      this.mElementGroupPropertiesList = null;
    }

    /**
     * Returns whether this object is equal to given object.
     *
     * @param obj an object to be compared
     * @return true if two objects are equal
     */
    public boolean equals(final Object obj) {
      if ((obj instanceof ElementGroupSetProperties) == false) {
        return false;
      }
      ElementGroupSetProperties p = (ElementGroupSetProperties) obj;
      if (p.visible != this.visible) {
        return false;
      }
      if (p.mElementGroupPropertiesList.equals(this.mElementGroupPropertiesList) == false) {
        return false;
      }
      return true;
    }

    /**
     * Returns string representation of this object.
     *
     * @return string representation of this object
     */
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("[");
      sb.append("visible=");
      sb.append(this.visible);
      sb.append(", ");
      sb.append(this.mElementGroupPropertiesList);
      sb.append("]");
      return sb.toString();
    }

    public List<SGProperties> getElementGroupPropertiesList() {
      return new ArrayList<SGProperties>(mElementGroupPropertiesList);
    }

    public void setElementGroupPropertiesList(List<SGProperties> elementGroupPropertiesList) {
      mElementGroupPropertiesList = new ArrayList<SGProperties>(elementGroupPropertiesList);
    }

    public boolean isVisible() {
      return visible;
    }

    public void setVisible(boolean visible) {
      this.visible = visible;
    }
  }

  // The flag whether this object is already disposed of.
  private boolean mDisposed = false;

  /**
   * Returns whether this object is already disposed of.
   *
   * @return true if this object is already disposed of
   */
  public boolean isDisposed() {
    return this.mDisposed;
  }

  public SGTuple2f getLocation(final int index, final SGTuple2f[] pointsArray) {
    if (pointsArray == null) {
      return null;
    }
    if (index < 0 || index >= pointsArray.length) {
      throw new IllegalArgumentException("Index out of bounds: " + index);
    }
    return pointsArray[index];
  }
}
