package jp.riken.brain.ni.samuraigraph.figure;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementAxisConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGArrowConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGShapeConstants.*;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGICopyable;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisBreak;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGrid;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementSignificantDifference;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementTimingLine;
import jp.riken.brain.ni.samuraigraph.base.SGIMovable;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow.ArrowProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementRectangle.RectangleProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** A class managing shape objects. */
public class SGFigureElementShape extends SGFigureElement implements SGIFigureElementShape {

  private final SGShapePropertyUpdater mShapePropertyUpdater = new SGShapePropertyUpdater(this);

  /** */
  SGIFigureElementAxis mAxisElement = null;

  /** A map of property dialogs for shape objects. */
  private HashMap<Object, SGPropertyDialog> mShapeDialogMap =
      new HashMap<Object, SGPropertyDialog>();

  private ShapeObject mPressedShape = null;

  /** */
  public SGFigureElementShape() {
    super();
  }

  /**
   * @param element
   */
  public void setAxisElement(final SGIFigureElementAxis element) {
    this.mAxisElement = element;
  }

  /** Dispose this object. */
  public void dispose() {
    super.dispose();

    // dispose the map of property dialogs
    Iterator<SGPropertyDialog> itr = this.mShapeDialogMap.values().iterator();
    while (itr.hasNext()) {
      SGPropertyDialog dg = itr.next();
      dg.dispose();
    }
    this.mShapeDialogMap.clear();
    this.mShapeDialogMap = null;

    this.mAxisElement = null;
  }

  /** */
  public SGIFigureElementAxis getAxisElement() {
    return this.mAxisElement;
  }

  /**
   * Returns the property dialog for a shape.
   *
   * @param sh a shape
   */
  SGPropertyDialog getShapeDialog(IElement sh) {

    // select the key of the dialog map
    Object key = null;
    if (sh instanceof SGShapeRect) {
      key = SGShapeRect.NAME;
    } else if (sh instanceof SGShapeArrow) {
      key = SGShapeArrow.NAME;
    } else {
      throw new Error("Unsupported shape: " + sh);
    }

    // get or create a property dialog
    Object obj = mShapeDialogMap.get(key);
    SGPropertyDialog dg = null;
    if (obj != null) {
      dg = (SGPropertyDialog) obj;
    } else {
      if (sh instanceof SGShapeRect) {
        dg = new SGRectangularShapeDialog(this.mDialogOwner, true);
      } else if (sh instanceof SGShapeArrow) {
        dg = new SGArrowDialog(this.mDialogOwner, true);
      }
      mShapeDialogMap.put(key, dg);
    }
    return dg;
  }

  /** */
  public boolean synchronize(SGIFigureElement element, String msg) {

    boolean flag = true;
    if (element instanceof SGIFigureElementGraph) {

    } else if (element instanceof SGIFigureElementString) {

    } else if (element instanceof SGIFigureElementLegend) {

    } else if (element instanceof SGIFigureElementAxis) {
      flag = this.synchronizeToAxisElement((SGIFigureElementAxis) element, msg);
    } else if (element instanceof SGIFigureElementAxisBreak) {

    } else if (element instanceof SGIFigureElementSignificantDifference) {

    } else if (element instanceof SGIFigureElementTimingLine) {

    } else if (element instanceof SGIFigureElementGrid) {

    } else if (element instanceof SGIFigureElementShape) {

    } else {
      flag = element.synchronizeArgument(this, msg);
    }

    return flag;
  }

  private boolean synchronizeToAxisElement(SGIFigureElementAxis element, String msg) {
    List<SGIChildObject> list = this.getVisibleChildList();
    for (int ii = 0; ii < list.size(); ii++) {
      ShapeObject sh = (ShapeObject) list.get(ii);
      if (!sh.getIElement().isAnchored()) {
        sh.setShapeWithAxesValues();
      } else {
        sh.setAxisValuesWithShape();
        sh.setChanged(true);
      }
    }

    return true;
  }

  /**
   * Synchronize the element given by the argument.
   *
   * @param msg the msg parameter
   * @param element An object to be synchronized.
   */
  public boolean synchronizeArgument(final SGIFigureElement element, String msg) {
    // this shouldn't happen
    throw new Error();
  }

  /**
   * Inserts a shape object at a given point with default axes.
   *
   * @param type the type of shape
   * @param x the x-coordinate
   * @param y the y-coordinate
   */
  public boolean addShape(final int type, final float x, final float y) {
    if (this.getGraphRect().contains(x, y) == false) {
      return false;
    }

    // get axes
    SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_SHAPE_HORIZONTAL_AXIS);
    SGAxis yAxis = this.mAxisElement.getAxis(DEFAULT_SHAPE_VERTICAL_AXIS);

    return this.addShape(this.assignChildId(), type, x, y, xAxis, yAxis);
  }

  /**
   * Inserts a shape object at a given point with given axes.
   *
   * @param id the ID to set
   * @param type the type of shape
   * @param x the x-coordinate
   * @param y the y-coordinate
   * @param xAxis the x-axis
   * @param yAxis the y-axis
   */
  private boolean addShape(
      final int id,
      final int type,
      final float x,
      final float y,
      final SGAxis xAxis,
      final SGAxis yAxis) {

    final float mag = this.getMagnification();
    final float dx = 60;
    final float dy = 20;
    if (type == RECTANGLE) {
      return this.addRectangle(id, x, y, xAxis, yAxis);
    } else if (type == ELLIPSE) {
      return this.addEllipse(id, x, y, xAxis, yAxis);
    } else if (type == ARROW) {
      final float xx = x + mag * dx;
      final float yy = y + mag * dy;
      return this.addArrow(id, x, y, xx, yy, xAxis, yAxis);
    } else if (type == LINE) {
      final float xx = x + mag * dx;
      final float yy = y + mag * dy;
      return this.addLine(id, x, y, xx, yy, xAxis, yAxis);
    }
    return true;
  }

  /**
   * Inserts a shape object at a given point with default axes.
   *
   * @param id the ID to set
   * @param type the type of shape
   * @param x the x-coordinate
   * @param y the y-coordinate
   */
  public boolean addShape(final int id, final int type, final double x, final double y) {
    return this.addShape(
        id, type, x, y, DEFAULT_SHAPE_HORIZONTAL_AXIS, DEFAULT_SHAPE_VERTICAL_AXIS);
  }

  /**
   * Inserts a shape object with given axis values for given axes.
   *
   * @param id the ID to set
   * @param type the type of shape
   * @param x axis value for given x-axis
   * @param y axis value for given y-axis
   * @param xAxisLocation location of the x-axis
   * @param yAxisLocation location of the y-axis
   */
  public boolean addShape(
      final int id,
      final int type,
      final double x,
      final double y,
      final String xAxisLocation,
      final String yAxisLocation) {

    // get axes
    SGAxis xAxis = this.mAxisElement.getAxis(xAxisLocation);
    SGAxis yAxis = this.mAxisElement.getAxis(yAxisLocation);
    if (xAxis == null || yAxis == null) {
      return false;
    }

    if (xAxis.isValidValue(x) == false) {
      return false;
    }
    if (yAxis.isValidValue(y) == false) {
      return false;
    }

    // insert a shape
    final float posX = this.calcLocation(x, xAxis, true);
    final float posY = this.calcLocation(y, yAxis, false);
    if (this.addShape(id, type, posX, posY, xAxis, yAxis) == false) {
      return false;
    }

    return true;
  }

  //
  private boolean addRectangle(
      final int id, final float x, final float y, final SGAxis xAxis, final SGAxis yAxis) {
    SGShapeRect el = new SGShapeRect(this);
    el.setMagnification(this.getMagnification());

    ShapeObject sh = new ShapeObject(id, el, xAxis, yAxis);
    el.setShapeObject(sh);

    el.setLocation(x, y);
    el.setAxisValuesWithShape();

    if (this.addShape(id, sh) == false) {
      return false;
    }

    return true;
  }

  //
  private boolean addEllipse(
      final int id, final float x, final float y, final SGAxis xAxis, final SGAxis yAxis) {
    Ellipse el = new Ellipse();
    el.setMagnification(this.getMagnification());

    ShapeObject sh = new ShapeObject(id, el, xAxis, yAxis);
    el.setShapeObject(sh);

    el.setLocation(x, y);
    el.setAxisValuesWithShape();

    if (this.addShape(id, sh) == false) {
      return false;
    }

    return true;
  }

  boolean addRectangularShape(
      final int id,
      SGShapeRect el,
      final double leftX,
      final double rightX,
      final double topY,
      final double bottomY,
      final SGAxis xAxis,
      final SGAxis yAxis) {

    el.setMagnification(this.getMagnification());

    ShapeObject sh = new ShapeObject(id, el, xAxis, yAxis);
    el.setShapeObject(sh);

    if (el.setLeftXValue(leftX) == false) {
      return false;
    }
    if (el.setRightXValue(rightX) == false) {
      return false;
    }
    if (el.setTopYValue(topY) == false) {
      return false;
    }
    if (el.setBottomYValue(bottomY) == false) {
      return false;
    }

    if (this.addShape(id, sh) == false) {
      return false;
    }

    return true;
  }

  //
  private boolean addArrow(
      final int id,
      final float sx,
      final float sy,
      final float ex,
      final float ey,
      final SGAxis xAxis,
      final SGAxis yAxis) {
    SGShapeArrow el = new SGShapeArrow(SGFigureElementShape.this);
    return this.addArrow(
        id,
        el,
        DEFAULT_SHAPE_ARROW_START_HEAD_TYPE,
        DEFAULT_SHAPE_ARROW_END_HEAD_TYPE,
        sx,
        sy,
        ex,
        ey,
        xAxis,
        yAxis);
  }

  //
  private boolean addLine(
      final int id,
      final float sx,
      final float sy,
      final float ex,
      final float ey,
      final SGAxis xAxis,
      final SGAxis yAxis) {
    SGShapeArrow el = new SGShapeArrow(SGFigureElementShape.this);
    return this.addArrow(id, el, SYMBOL_TYPE_VOID, SYMBOL_TYPE_VOID, sx, sy, ex, ey, xAxis, yAxis);
  }

  //
  private boolean addArrow(
      final int id,
      final SGShapeArrow el,
      final int startType,
      final int endType,
      final float sx,
      final float sy,
      final float ex,
      final float ey,
      final SGAxis xAxis,
      final SGAxis yAxis) {
    ShapeObject sh = new ShapeObject(id, el, xAxis, yAxis);
    el.setMagnification(this.getMagnification());
    el.setShapeObject(sh);
    el.setStartHeadType(startType);
    el.setEndHeadType(endType);
    el.setStartX(sx);
    el.setStartY(sy);
    el.setEndX(ex);
    el.setEndY(ey);

    el.setAxisValuesWithShape();

    if (this.addShape(id, sh) == false) {
      return false;
    }

    return true;
  }

  boolean addArrowShape(
      final int id,
      SGShapeArrow el,
      final double startX,
      final double startY,
      final double endX,
      final double endY,
      final SGAxis xAxis,
      final SGAxis yAxis) {

    if (xAxis.isValidValue(startX) == false) {
      return false;
    }
    if (yAxis.isValidValue(startY) == false) {
      return false;
    }
    if (xAxis.isValidValue(endX) == false) {
      return false;
    }
    if (yAxis.isValidValue(endY) == false) {
      return false;
    }

    el.setMagnification(this.getMagnification());

    ShapeObject sh = new ShapeObject(id, el, xAxis, yAxis);
    el.setShapeObject(sh);

    el.setStartXValue(startX);
    el.setStartYValue(startY);
    el.setEndXValue(endX);
    el.setEndYValue(endY);

    if (this.addShape(id, sh) == false) {
      return false;
    }

    return true;
  }

  /**
   * Adds a shape.
   *
   * @param id the ID to set
   * @param sh a shape
   */
  private boolean addShape(final int id, final ShapeObject sh) {
    if (this.addToList(id, sh) == false) {
      return false;
    }

    // initialize history
    sh.initPropertiesHistory();

    this.setChanged(true);
    this.notifyToRoot();

    return true;
  }

  // clear other type focused objects
  private boolean clearFocusedObjectOtherThan(ShapeObject sh) {
    Class<?> cl = sh.getIElement().getClass();
    List<SGISelectable> list = this.getFocusedObjectsList();
    for (int ii = 0; ii < list.size(); ii++) {
      ShapeObject el = (ShapeObject) list.get(ii);
      Class<?> cl_ = el.getIElement().getClass();

      // clear the focus
      if (!cl_.isAssignableFrom(cl) && !cl.isAssignableFrom(cl_)) {
        el.setSelected(false);
      }
    }

    return true;
  }

  /** */
  public boolean setGraphRect(final float x, final float y, final float width, final float height) {
    if (super.setGraphRect(x, y, width, height) == false) {
      return false;
    }

    List<SGIChildObject> list = this.mChildList;
    for (int ii = 0; ii < list.size(); ii++) {
      final ShapeObject el = (ShapeObject) list.get(ii);
      if (!el.getIElement().isAnchored()) {
        el.setShapeWithAxesValues();
      } else {
        el.setAxisValuesWithShape();
      }
    }

    return true;
  }

  /** */
  public SGProperties getProperties() {
    ShapeElementProperties p = new ShapeElementProperties();
    p.visibleShapeList = new ArrayList<SGIChildObject>(this.getVisibleChildList());
    return p;
  }

  /** */
  public boolean setProperties(final SGProperties p) {
    if ((p instanceof ShapeElementProperties) == false) return false;

    ShapeElementProperties wp = (ShapeElementProperties) p;
    final boolean flag = this.setVisibleChildList(wp.visibleShapeList);
    if (!flag) {
      return false;
    }

    return true;
  }

  /** */
  public boolean setMementoBackward() {
    boolean flag = super.setMementoBackward();
    if (!flag) {
      return false;
    }

    this.clearFocusedObjects();
    this.notifyChangeOnUndo();

    return true;
  }

  /** */
  public boolean setMementoForward() {
    boolean flag = super.setMementoForward();
    if (!flag) {
      return false;
    }

    this.clearFocusedObjects();
    this.notifyChangeOnUndo();

    return true;
  }

  /** Create copies of the focused objects. */
  public boolean duplicateFocusedObjects() {
    final int ox = (int) (this.mMagnification * OFFSET_DUPLICATED_OBJECT_X);
    final int oy = (int) (this.mMagnification * OFFSET_DUPLICATED_OBJECT_Y);

    List<SGICopyable> cList = this.duplicateObjects();
    for (int ii = 0; ii < cList.size(); ii++) {
      // duplicate
      ShapeObject el = (ShapeObject) cList.get(ii);

      // translate the duplicate
      el.translate(ox, oy);

      // set selected
      el.setSelected(true);

      // add to the list
      this.addToList(el);

      // initialize history
      el.initPropertiesHistory();
    }

    if (cList.size() != 0) {
      this.setChanged(true);
    }

    return true;
  }

  /**
   * Paste the objects.
   *
   * @param list of the objects to be pasted
   */
  public boolean paste(List<SGICopyable> list) {
    final float mag = this.getMagnification();
    final int ox = (int) (mag * OFFSET_DUPLICATED_OBJECT_X);
    final int oy = (int) (mag * OFFSET_DUPLICATED_OBJECT_Y);

    int cnt = 0;
    for (int ii = 0; ii < list.size(); ii++) {
      Object obj = list.get(ii);
      if (obj instanceof ShapeObject) {
        ShapeObject shOld = (ShapeObject) obj;

        // translate the instance to be pasted
        shOld.translate(ox, oy);

        SGProperties p = shOld.getMemento();

        IElement elCopy = (IElement) shOld.getIElement().copy();

        ShapeObject shNew = new ShapeObject(elCopy);
        elCopy.setShapeObject(shNew);
        shNew.setMagnification(mag);
        shNew.setMemento(p);

        shNew.setXAxis(this.mAxisElement.getAxisInPlane(shOld.mTempXAxis));
        shNew.setYAxis(this.mAxisElement.getAxisInPlane(shOld.mTempYAxis));

        shNew.setShapeWithAxesValues();

        // add to the list
        this.addToList(shNew);

        // initialize history
        shNew.initPropertiesHistory();

        cnt++;
      }
    }

    if (cnt != 0) {
      this.setChanged(true);
    }

    return true;
  }

  /** */
  public String getTagName() {
    return TAG_NAME_SHAPE;
  }

  /** */
  public boolean writeProperty(final Element el, SGExportParameter params) {
    return true;
  }

  /** */
  public boolean onMouseClicked(final MouseEvent e) {
    List<SGIChildObject> list = this.getVisibleChildList();
    for (int ii = list.size() - 1; ii >= 0; ii--) {
      final ShapeObject el = (ShapeObject) list.get(ii);
      if (el.isValid() == false) {
        continue;
      }
      if (this.clickDrawingElements(el, e)) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the list of selected property dialog observers of given class type.
   *
   * @param cl the class
   */
  @Override
  public List<SGIPropertyDialogObserver> getSelectedPropertyDialogObserverList(Class<?> cl) {
    List<SGISelectable> fList = this.getFocusedObjectsList();
    List<SGIPropertyDialogObserver> obsList = new ArrayList<SGIPropertyDialogObserver>();
    for (SGISelectable f : fList) {
      ShapeObject obj = (ShapeObject) f;
      obsList.add(obj.getIElement());
    }
    return obsList;
  }

  /**
   * Returns the list of visible property dialog observers of given class type.
   *
   * @param cl the class
   */
  @Override
  public List<SGIPropertyDialogObserver> getVisiblePropertyDialogObserverList(Class<?> cl) {
    List<SGIChildObject> cList = this.getVisibleChildList();
    List<SGIPropertyDialogObserver> obsList = new ArrayList<SGIPropertyDialogObserver>();
    for (SGIChildObject c : cList) {
      ShapeObject obj = (ShapeObject) c;
      obsList.add(obj.getIElement());
    }
    return obsList;
  }

  /**
   * Returns the list of all property dialog observers of given class type.
   *
   * @param cl the class
   */
  @Override
  public List<SGIPropertyDialogObserver> getAllPropertyDialogObserverList(Class<?> cl) {
    return this.getVisiblePropertyDialogObserverList();
  }

  /** Returns the class object of property dialog observer. */
  @Override
  public Class<?> getPropertyDialogObserverClass() {
    return IElement.class;
  }

  /** */
  private boolean clickDrawingElements(final ShapeObject el, final MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();
    final int cnt = e.getClickCount();

    // clicked on the line elements
    if (el.contains(x, y)) {
      this.updateFocusedObjectsList(el, e);

      if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {

      } else if (SwingUtilities.isLeftMouseButton(e) && cnt == 2) {
        this.setPropertiesOfSelectedObjects(el);
      } else if (SwingUtilities.isRightMouseButton(e) && cnt == 1) {
        el.getPopupMenu().show(this.getComponent(), x, y);
      }

      return true;
    }

    return false;
  }

  /** */
  public boolean onMousePressed(final MouseEvent e) {
    List<SGIChildObject> list = this.getVisibleChildList();
    for (int ii = list.size() - 1; ii >= 0; ii--) {
      final ShapeObject el = (ShapeObject) list.get(ii);
      if (el.isValid() == false) {
        continue;
      }
      if (el.press(e)) {
        this.mPressedPoint = e.getPoint();
        this.mPressedShape = el;
        if (el.isSelected()) {
          this.mDraggableFlag = true;
        }
        return true;
      }
    }

    return false;
  }

  /** */
  public boolean onMouseDragged(final MouseEvent e) {
    if (this.mDraggableFlag == false) {
      return false;
    }
    if (this.mPressedPoint == null) {
      return false;
    }

    ShapeObject el = null;
    List<SGISelectable> list = this.getFocusedObjectsList();
    if (list.size() == 1) {
      el = (ShapeObject) list.get(0);
    } else if (list.size() > 1 && this.mPressedShape != null) {
      el = this.mPressedShape;
    }
    if (el != null) {
      if (el.drag(e) == false) {
        return false;
      }
      el.setAxisValuesWithShape();
      this.mPressedPoint = e.getPoint();
    }

    return true;
  }

  /** */
  public boolean onMouseReleased(MouseEvent e) {
    final int x = e.getX();
    final int y = e.getY();

    List<SGISelectable> list = this.getFocusedObjectsList();
    for (int ii = 0; ii < list.size(); ii++) {
      ShapeObject el = (ShapeObject) list.get(ii);
      if (el.isValid() == false) {
        continue;
      }
      if (el.contains(x, y)) {
        this.setMouseCursor(Cursor.HAND_CURSOR);
      } else {
        this.setMouseCursor(Cursor.DEFAULT_CURSOR);
      }
    }

    this.mDraggableFlag = false;
    this.mDraggedDirection = null;
    this.mPressedShape = null;

    return false;
  }

  /** */
  public boolean setMouseCursor(int x, int y) {
    List<SGIChildObject> list = this.getVisibleChildList();
    for (int ii = list.size() - 1; ii >= 0; ii--) {
      final ShapeObject el = (ShapeObject) list.get(ii);
      if (el.isValid() == false) {
        continue;
      }
      final boolean flag = el.contains(x, y);

      if (flag) {
        if (el.isSelected()) {
          final int ml = el.getMouseLocation(x, y);
          el.mMouseLocation = ml;
          setMouseCursor(el.getCursor(ml));
          return true;
        }
        setMouseCursor(Cursor.HAND_CURSOR);
        return true;
      }
    }

    return false;
  }

  /**
   * Creates an array of Element objects.
   *
   * @param params the params parameter
   * @param document an Document objects to append elements
   */
  public Element[] createElement(final Document document, SGExportParameter params) {
    // create an Element object
    Element el = this.createThisElement(document, params);
    if (el == null) {
      return null;
    }

    // shape
    List<SGIChildObject> list = this.getVisibleChildList();
    for (int ii = 0; ii < list.size(); ii++) {
      ShapeObject sh = (ShapeObject) list.get(ii);
      if (sh.isValid() == false) {
        continue;
      }
      Element elShape = sh.createElement(document, params);
      if (elShape == null) {
        return null;
      }
      el.appendChild(elShape);
    }

    return new Element[] {el};
  }

  /**
   * Read properties from the Element object.
   *
   * @param element an Element object which has properties
   * @param versionNumber the version number of property file
   */
  public boolean readProperty(final Element element, final String versionNumber) {
    NodeList nList = element.getChildNodes();
    for (int ii = 0; ii < nList.getLength(); ii++) {
      Node node = nList.item(ii);
      if (node instanceof Element) {
        Element el = (Element) node;
        ShapeObject sh = new ShapeObject();
        if (sh.readProperty(el) == false) {
          return false;
        }
        sh.initPropertiesHistory();
        this.addToList(sh);
      }
    }
    return true;
  }

  /** Updates changed flag of focused objects. */
  @Override
  public boolean updateChangedFlag() {
    List<SGISelectable> list = this.getFocusedObjectsList();
    for (int ii = 0; ii < list.size(); ii++) {
      ShapeObject el = (ShapeObject) list.get(ii);
      SGProperties temp = el.mTemporaryProperties;
      if (temp != null) {
        SGProperties p = el.getMemento();
        if (p.equals(temp) == false) {
          el.setChanged(true);
        }
      }
    }
    return true;
  }

  /** */
  public void paintGraphics(Graphics g, boolean clip) {
    Graphics2D g2d = (Graphics2D) g;

    List<SGIChildObject> list = this.getVisibleChildList();
    for (int ii = 0; ii < list.size(); ii++) {
      ShapeObject sh = (ShapeObject) list.get(ii);
      if (sh.isValid() == false) {
        continue;
      }
      sh.paintElement(g2d);
    }

    // draw symbols around all objects
    if (this.mSymbolsVisibleFlagAroundAllObjects) {
      for (int ii = 0; ii < list.size(); ii++) {
        ShapeObject sh = (ShapeObject) list.get(ii);
        if (sh.isValid() == false) {
          continue;
        }
        List<Point2D> pList = sh.getAnchorPointList();
        if (!sh.getIElement().isAnchored()) {
          SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(pList, g2d);
        } else {
          SGUtilityForFigureElementJava2D.drawAnchorPointsAsAnchoredChildObject(pList, g2d);
        }
      }
    }

    // draw symbols around focused objects
    if (this.mSymbolsVisibleFlagAroundFocusedObjects) {
      ArrayList<SGISelectable> fList = new ArrayList<SGISelectable>();
      this.getFocusedObjectsList(fList);
      for (int ii = 0; ii < fList.size(); ii++) {
        ShapeObject sh = (ShapeObject) fList.get(ii);
        if (sh.isValid() == false) {
          continue;
        }
        List<Point2D> pList = sh.getAnchorPointList();
        if (!sh.getIElement().isAnchored()) {
          SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pList, g2d);
        } else {
          SGUtilityForFigureElementJava2D.drawAnchorPointsAsAnchoredFocusObject(pList, g2d);
        }
      }
    }
  }

  /** Returns a list of child nodes. */
  public ArrayList<SGINode> getChildNodes() {
    final ArrayList<SGINode> list = new ArrayList<SGINode>();
    final ArrayList<SGIChildObject> aList = new ArrayList<>(this.mChildList);
    for (int ii = 0; ii < aList.size(); ii++) {
      final ShapeObject el = (ShapeObject) aList.get(ii);
      if (el.isVisible()) {
        list.add(el.mElement);
      }
    }

    return list;
  }

  /** */
  public String getClassDescription() {
    return "Shape";
  }

  // check whether two points are within the radius
  static boolean isInside(final int x1, final int y1, final int x2, final int y2) {
    final int radius = (int) (1.25f * ANCHOR_SIZE_FOR_FOCUSED_OBJECTS);
    return ((Math.abs(x1 - x2) < radius) && (Math.abs(y1 - y2) < radius));
  }

  /** */
  protected Set<SGIChildObject> getAvailableChildSet() {
    Set<SGIChildObject> set = new HashSet<>();
    List<SGProperties> mList = this.getMementoList();
    for (int ii = 0; ii < mList.size(); ii++) {
      ShapeElementProperties p = (ShapeElementProperties) mList.get(ii);
      set.addAll(p.visibleShapeList);
    }

    return set;
  }

  /** */
  private static class ShapeElementProperties extends SGProperties {
    ArrayList<SGIChildObject> visibleShapeList = new ArrayList<SGIChildObject>();

    /** */
    public ShapeElementProperties() {
      super();
    }

    public void dispose() {
      super.dispose();
      this.visibleShapeList.clear();
      this.visibleShapeList = null;
    }

    /** */
    public boolean equals(final Object obj) {
      if ((obj instanceof ShapeElementProperties) == false) return false;

      ShapeElementProperties p = (ShapeElementProperties) obj;

      if (p.visibleShapeList.equals(this.visibleShapeList) == false) return false;

      return true;
    }

    /** */
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      sb.append(this.visibleShapeList.toString());
      sb.append(']');
      return sb.toString();
    }
  }

  /** An interface of the drawing element. */
  interface IElement extends SGINode, SGIPropertyDialogObserver, SGIDisposable, SGIAnchored {
    public void setShapeObject(ShapeObject sh);

    public Object copy();

    public void translate(final float dx, final float dy);

    public void translateSub(final float dx, final float dy);

    public SGProperties getMemento();

    public boolean setMagnification(final float mag);

    /**
     * @param p
     */
    public boolean setMemento(final SGProperties p);

    public String getName();

    public String getClassDescription();

    public String getInstanceDescription();

    public boolean setAxisValuesWithShape();

    public boolean setShapeWithAxesValues();

    public List<Point2D> getAnchorPointList();

    public int getMouseLocation(final int x, final int y);

    public boolean drag(MouseEvent e, Point pos, final int ml);

    public boolean writeProperty(final Element el, SGExportParameter params);

    public boolean readProperty(final Element element);

    /**
     * Sets the properties.
     *
     * @param map a map of properties
     * @param iResult the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map, SGPropertyResults iResult);

    /** Creates and returns the map of properties. */
    public SGPropertyMap getCommandPropertyMap();

    public String getShapeType();
  }

  interface PropertyWithAxes {
    public int getXAxisLocation();

    public int getYAxisLocation();
  }

  private static class ShapeObjectPopupMenu extends JPopupMenu {

    private static final long serialVersionUID = 7825575947035545853L;

    private JCheckBoxMenuItem anchoredCheckBox;

    void init(ShapeObject so) {
      setBounds(0, 0, 100, 100);

      StringBuilder sb = new StringBuilder();
      sb.append("  -- Shape: ");
      sb.append(so.getID());
      sb.append(" --");

      add(new JLabel(sb.toString()));
      addSeparator();

      SGUtility.addArrangeItems(this, so);

      addSeparator();

      SGUtility.addItem(this, so, MENUCMD_CUT);
      SGUtility.addItem(this, so, MENUCMD_COPY);

      addSeparator();

      SGUtility.addItem(this, so, MENUCMD_DELETE);
      SGUtility.addItem(this, so, MENUCMD_DUPLICATE);

      addSeparator();

      this.anchoredCheckBox = new JCheckBoxMenuItem(MENUCMD_ANCHORED);
      add(this.anchoredCheckBox);
      this.anchoredCheckBox.addActionListener(so);

      addSeparator();

      SGUtility.addItem(this, so, MENUCMD_PROPERTY);
    }

    void setAnchored(final boolean anchored) {
      this.anchoredCheckBox.setSelected(anchored);
    }
  }

  /** An inner class for the shape objects. */
  class ShapeObject extends SGDrawingElement
      implements ActionListener,
          SGIUndoable,
          SGIChildObject,
          SGIMovable,
          SGICopyable,
          SGIPropertyDialogObserver,
          SGIAnchored {

    // the ID number
    private int mID;

    public int getID() {
      return this.mID;
    }

    public boolean setID(final int id) {
      this.mID = id;
      return true;
    }

    // x axis
    private SGAxis mXAxis;

    // y axis
    private SGAxis mYAxis;

    // the drawing element
    private IElement mElement = null;

    // undo manager
    private SGUndoManager mUndoManager = new SGUndoManager(this);

    private int mTempXAxis = -1;

    private int mTempYAxis = -1;

    /** */
    private SGProperties mTemporaryProperties = null;

    /** The pop-up menu. */
    private ShapeObjectPopupMenu mPopupMenu = null;

    // The constructor.
    private ShapeObject() {
      super();
      this.init();
    }

    // The constructor.
    private ShapeObject(IElement sh) {
      super();
      this.setIElement(sh);
      this.init();
    }

    // The constructor.
    private ShapeObject(final int id, IElement sh, SGAxis xAxis, SGAxis yAxis) {
      super();
      this.setIElement(sh);
      this.setXAxis(xAxis);
      this.setYAxis(yAxis);
      this.init();
    }

    // initialize
    private void init() {}

    /** Disposes of this object. */
    public void dispose() {
      super.dispose();

      this.mElement.dispose();
      this.mElement = null;

      this.mPopupMenu = null;

      if (this.mTemporaryProperties != null) {
        this.mTemporaryProperties.dispose();
        this.mTemporaryProperties = null;
      }

      this.mUndoManager.dispose();
      this.mUndoManager = null;

      this.mXAxis = null;
      this.mYAxis = null;
    }

    // The flag whether this object is already disposed of.
    private boolean mDisposed = false;

    /** Returns whether this object is already disposed of. */
    public boolean isDisposed() {
      return this.mDisposed;
    }

    /** Returns a pop-up menu. */
    public JPopupMenu getPopupMenu() {
      ShapeObjectPopupMenu p = null;
      if (this.mPopupMenu != null) {
        p = this.mPopupMenu;
      } else {
        p = new ShapeObjectPopupMenu();
        p.init(this);
        this.mPopupMenu = p;
      }
      p.setAnchored(this.mElement.isAnchored());
      return p;
    }

    /** */
    public void actionPerformed(ActionEvent e) {
      final String command = e.getActionCommand();

      if (command.equals(MENUCMD_PROPERTY)) {
        // clear all focused objects other type object clicked
        SGFigureElementShape.this.clearFocusedObjectOtherThan(this);

        // notify to figure
        SGFigureElementShape.this.setPropertiesOfSelectedObjects(this);
      } else {
        notifyToListener(command, e.getSource());
      }
    }

    /** */
    public boolean prepare() {
      return this.mElement.prepare();
    }

    public boolean commit() {
      return this.mElement.commit();
    }

    public IElement getIElement() {
      return this.mElement;
    }

    public void setIElement(IElement sh) {
      this.mElement = sh;
    }

    SGFigureElementShape getShapeElement() {
      return SGFigureElementShape.this;
    }

    private SGIFigureElementAxis getAxisElement() {
      return this.getShapeElement().getAxisElement();
    }

    public SGAxis getXAxis() {
      return this.mXAxis;
    }

    public SGAxis getYAxis() {
      return this.mYAxis;
    }

    public void setXAxis(SGAxis axis) {
      this.mXAxis = axis;
    }

    public void setYAxis(SGAxis axis) {
      this.mYAxis = axis;
    }

    private SGDrawingElement getDrawingElement() {
      return (SGDrawingElement) this.mElement;
    }

    private SGIDrawingElementJava2D getDrawingElement2D() {
      return (SGIDrawingElementJava2D) this.mElement;
    }

    public void setVisible(final boolean b) {
      super.setVisible(b);
      this.getDrawingElement().setVisible(b);
    }

    private void paintElement(Graphics2D g2d) {
      if (this.isValid()) {
        this.getDrawingElement2D().paint(g2d);
      }
    }

    private boolean mValidFlag = true;

    public boolean isValid() {
      return this.mValidFlag;
    }

    public void setValid(final boolean b) {
      this.mValidFlag = b;
    }

    public boolean setAxisValuesWithShape() {
      return this.mElement.setAxisValuesWithShape();
    }

    public boolean setShapeWithAxesValues() {
      return this.mElement.setShapeWithAxesValues();
    }

    public boolean contains(final int x, final int y) {
      return ((SGDrawingElement) this.mElement).contains(x, y);
    }

    /** */
    private List<Point2D> getAnchorPointList() {
      return this.mElement.getAnchorPointList();
    }

    /** Location of mouse pointer. */
    private int mMouseLocation;

    /** Sets the mouse location to an attribute. */
    private int getMouseLocation(final int x, final int y) {
      return this.mElement.getMouseLocation(x, y);
    }

    /** */
    private Cursor getCursor(final int location) {

      Cursor cur = null;

      switch (location) {
        case NORTH:
          {
            cur = new Cursor(Cursor.N_RESIZE_CURSOR);
            break;
          }
        case SOUTH:
          {
            cur = new Cursor(Cursor.S_RESIZE_CURSOR);
            break;
          }
        case WEST:
          {
            cur = new Cursor(Cursor.W_RESIZE_CURSOR);
            break;
          }
        case EAST:
          {
            cur = new Cursor(Cursor.E_RESIZE_CURSOR);
            break;
          }
        case NORTH_WEST:
          {
            cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
            break;
          }
        case SOUTH_WEST:
          {
            cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
            break;
          }
        case NORTH_EAST:
          {
            cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
            break;
          }
        case SOUTH_EAST:
          {
            cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
            break;
          }
        default:
          {
            cur = new Cursor(Cursor.HAND_CURSOR);
          }
      }

      return cur;
    }

    // on pressed
    private boolean press(final MouseEvent e) {

      final int x = e.getX();
      final int y = e.getY();

      if (this.contains(x, y)) {

        this.mMouseLocation = this.getMouseLocation(x, y);
        Cursor cur = null;
        if (this.mMouseLocation == OTHER) {
          cur = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        } else {
          this.getCursor(this.mMouseLocation);
        }
        setMouseCursor(cur);

        return true;
      }

      return false;
    }

    // on dragged
    private boolean drag(MouseEvent e) {
      if (SGFigureElementShape.this.mPressedPoint == null) {
        return false;
      }

      // translation
      if (this.mMouseLocation == OTHER) {
        return this.dragOtherPoint(e);
      }
      // create a temporary object
      Point posNew = new Point(SGFigureElementShape.this.mPressedPoint);

      // drag the element
      this.mElement.drag(e, posNew, this.mMouseLocation);

      // set to an attribute
      SGFigureElementShape.this.mPressedPoint.setLocation(posNew);

      return true;
    }

    /** */
    private boolean dragOtherPoint(final MouseEvent e) {
      // parallel displacement
      if (SGFigureElementShape.this.mPressedPoint != null) {
        // set the location to the symbol
        MouseDragResult result = SGFigureElementShape.this.getMouseDragResult(e);
        final int dx = result.dx;
        final int dy = result.dy;
        this.mElement.translateSub(dx, dy);

        SGFigureElementShape.this.mPressedPoint = e.getPoint();
      }

      return true;
    }

    /** Flag whether this object is focused. */
    private boolean mSelectedFlag = false;

    /** Get the flag as a focused object. */
    public boolean isSelected() {
      return this.mSelectedFlag;
    }

    /**
     * Sets selected or deselected the object.
     *
     * @param b true selects and false deselects the object
     */
    @Override
    public void setSelected(final boolean b) {
      this.mSelectedFlag = b;
      this.mTemporaryProperties = b ? this.getProperties() : null;
    }

    /** */
    public int getAxisConfiguration(SGAxis axis) {
      return SGFigureElementShape.this.mAxisElement.getLocationInPlane(axis);
    }

    /**
     * Sets the location of the x-axis.
     *
     * @param location the location of the x-axis
     */
    public boolean setXAxis(final int location) {
      if (location != AXIS_HORIZONTAL_1 && location != AXIS_HORIZONTAL_2) {
        return false;
      }
      this.setXAxis(SGFigureElementShape.this.mAxisElement.getAxisInPlane(location));
      return true;
    }

    /**
     * Sets the location of the y-axis.
     *
     * @param location the location of the y-axis
     */
    public boolean setYAxis(final int location) {
      if (location != AXIS_VERTICAL_1 && location != AXIS_VERTICAL_2) {
        return false;
      }
      this.setYAxis(SGFigureElementShape.this.mAxisElement.getAxisInPlane(location));
      return true;
    }

    /** */
    public Object copy() {
      IElement el = (IElement) this.mElement.copy();

      SGAxis xAxis = this.getXAxis();
      SGAxis yAxis = this.getYAxis();
      final int xConfig = this.getAxisConfiguration(this.getXAxis());
      final int yConfig = this.getAxisConfiguration(this.getYAxis());

      ShapeObject sh = new ShapeObject(el);
      el.setShapeObject(sh);
      sh.setXAxis(xAxis);
      sh.setYAxis(yAxis);
      sh.mTempXAxis = xConfig;
      sh.mTempYAxis = yConfig;

      return sh;
    }

    /** */
    public void translate(final float dx, final float dy) {
      if (this.equals(mPressedShape)) {
        return;
      }
      this.mElement.translate(dx, dy);
    }

    /** */
    public boolean isChanged() {
      return this.mUndoManager.isChanged();
    }

    public void setChanged(final boolean b) {
      this.mUndoManager.setChanged(b);
    }

    public boolean isChangedRoot() {
      return this.isChanged();
    }

    /** Clear changed flag of this undoable object and all child objects. */
    public void clearChanged() {
      this.setChanged(false);
    }

    /** */
    public boolean initPropertiesHistory() {
      return this.mUndoManager.initPropertiesHistory();
    }

    /** */
    public void notifyToRoot() {
      SGFigureElementShape.this.notifyToRoot();
    }

    /** Update the list of history. */
    public boolean updateHistory() {
      return this.mUndoManager.updateHistory();
    }

    /** */
    public void initUndoBuffer() {
      this.mUndoManager.initUndoBuffer();
    }

    /** Undo this object. */
    public boolean setMementoBackward() {
      if (this.mUndoManager.setMementoBackward() == false) {
        return false;
      }

      this.setShapeWithAxesValues();

      return true;
    }

    /** Redo this object. */
    public boolean setMementoForward() {
      if (this.mUndoManager.setMementoForward() == false) {
        return false;
      }

      this.setShapeWithAxesValues();

      return true;
    }

    /** Just calls undo method. */
    public boolean undo() {
      return this.setMementoBackward();
    }

    /** Just calls redo method. */
    public boolean redo() {
      return this.setMementoForward();
    }

    /** */
    public SGProperties getMemento() {
      return this.mElement.getMemento();
    }

    /** */
    public boolean setMemento(SGProperties p) {
      if ((p instanceof PropertyWithAxes) == false) return false;

      if (super.setProperties(p) == false) return false;

      PropertyWithAxes rp = (PropertyWithAxes) p;

      SGAxis xAxis = SGFigureElementShape.this.mAxisElement.getAxisInPlane(rp.getXAxisLocation());
      if (xAxis == null) return false;
      this.setXAxis(xAxis);

      SGAxis yAxis = SGFigureElementShape.this.mAxisElement.getAxisInPlane(rp.getYAxisLocation());
      if (yAxis == null) return false;
      this.setYAxis(yAxis);

      return this.mElement.setMemento(p);
    }

    /**
     * @return
     */
    public boolean isUndoable() {
      return this.mUndoManager.isUndoable();
    }

    /**
     * @return
     */
    public boolean isRedoable() {
      return this.mUndoManager.isRedoable();
    }

    /**
     * Delete all forward histories.
     *
     * @return true if succeeded
     */
    public boolean deleteForwardHistory() {
      return this.mUndoManager.deleteForwardHistory();
    }

    /**
     * @param document
     * @return
     * @param params the params parameter
     */
    public Element createElement(final Document document, SGExportParameter params) {
      Element el = document.createElement(this.mElement.getName());

      if (this.writeProperty(el, params) == false) {
        return null;
      }

      return el;
    }

    /**
     * @param element
     * @return
     * @param params the params parameter
     */
    public boolean writeProperty(final Element element, SGExportParameter params) {
      SGIFigureElementAxis aElement = this.getAxisElement();
      element.setAttribute(KEY_X_AXIS_POSITION, aElement.getLocationName(this.getXAxis()));
      element.setAttribute(KEY_Y_AXIS_POSITION, aElement.getLocationName(this.getYAxis()));

      if (this.mElement.writeProperty(element, params) == false) {
        return false;
      }

      return true;
    }

    /** */
    public boolean readProperty(final Element element) {
      SGIFigureElementAxis aElement = this.getAxisElement();
      String str = null;

      // create an IElement object
      String tag = element.getTagName();
      IElement ie = null;
      if (tag.equals(SGShapeRect.NAME)) {
        ie = new SGShapeRect(SGFigureElementShape.this);
      } else if (tag.equals(Ellipse.NAME)) {
        ie = new Ellipse();
      } else if (tag.equals(SGShapeArrow.NAME)) {
        ie = new SGShapeArrow(SGFigureElementShape.this);
      } else {
        return false;
      }
      this.setIElement(ie);
      ie.setShapeObject(this);

      //
      // read axis properties
      //

      // x axis
      str = element.getAttribute(KEY_X_AXIS_POSITION);
      if (str.length() == 0) {
        return false;
      }
      SGAxis xAxis = aElement.getAxis(str);
      if (xAxis == null) {
        return false;
      }

      // y axis
      str = element.getAttribute(KEY_Y_AXIS_POSITION);
      if (str.length() == 0) {
        return false;
      }
      SGAxis yAxis = aElement.getAxis(str);
      if (yAxis == null) {
        return false;
      }

      this.setXAxis(xAxis);
      this.setYAxis(yAxis);

      // read properties of IElement object
      if (ie.readProperty(element) == false) {
        return false;
      }

      return true;
    }

    @Override
    public float getMagnification() {
      return SGFigureElementShape.this.getMagnification();
    }

    @Override
    public boolean setMagnification(float mag) {
      if (this.mElement.setMagnification(mag) == false) {
        return false;
      }
      return true;
    }

    /**
     * Sets the properties.
     *
     * @param map a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map) {
      SGPropertyResults result = new SGPropertyResults();

      // prepare
      if (this.prepare() == false) {
        return null;
      }

      Iterator<String> itr = map.getKeyIterator();
      while (itr.hasNext()) {
        String key = itr.next();
        String value = map.getValueString(key);

        if (COM_SHAPE_AXIS_X.equalsIgnoreCase(key)) {
          final int loc = SGUtility.getAxisLocation(value);
          if (loc == -1) {
            result.putResult(COM_SHAPE_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (this.setXAxis(loc) == false) {
            result.putResult(COM_SHAPE_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          result.putResult(COM_SHAPE_AXIS_X, SGPropertyResults.SUCCEEDED);
        } else if (COM_SHAPE_AXIS_Y.equalsIgnoreCase(key)) {
          final int loc = SGUtility.getAxisLocation(value);
          if (loc == -1) {
            result.putResult(COM_SHAPE_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          if (this.setYAxis(loc) == false) {
            result.putResult(COM_SHAPE_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
            continue;
          }
          result.putResult(COM_SHAPE_AXIS_Y, SGPropertyResults.SUCCEEDED);
        }
      }

      // set properties for each shape
      result = this.getIElement().setProperties(map, result);
      if (result == null) {
        return null;
      }

      // commit the changes
      if (this.commit() == false) {
        return null;
      }
      notifyToRoot();
      notifyChange();
      repaint();

      return result;
    }

    public boolean cancel() {
      return this.mElement.cancel();
    }

    public boolean preview() {
      return this.mElement.preview();
    }

    public SGPropertyDialog getPropertyDialog() {
      return this.mElement.getPropertyDialog();
    }

    @Override
    public boolean isAnchored() {
      return this.mElement.isAnchored();
    }

    @Override
    public boolean setAnchored(boolean anchored) {
      return this.mElement.setAnchored(anchored);
    }

    /**
     * Returns a text string of the commands.
     *
     * @return a text string of the commands
     */
    @Override
    public String getCommandString(SGExportParameter params) {
      StringBuilder sb = new StringBuilder();

      // creates the command for this data
      String dataCommands = this.createCommandString(params);
      sb.append(dataCommands);

      return sb.toString();
    }

    /**
     * Creates and returns a text string of commands.
     *
     * @return a text string of commands
     */
    @Override
    public String createCommandString(SGExportParameter params) {
      return SGCommandUtility.createCommandString(
          COM_SHAPE, Integer.toString(this.mID), this.getCommandPropertyMap(params));
    }

    /**
     * Creates and returns the map of properties.
     *
     * @return the map of properties
     */
    @Override
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
      SGPropertyMap map = this.mElement.getCommandPropertyMap();
      SGPropertyUtility.addProperty(map, COM_SHAPE_TYPE, this.mElement.getShapeType());
      SGPropertyUtility.addProperty(
          map, COM_SHAPE_AXIS_X, mAxisElement.getLocationName(this.getXAxis()));
      SGPropertyUtility.addProperty(
          map, COM_SHAPE_AXIS_Y, mAxisElement.getLocationName(this.getYAxis()));
      return map;
    }

    @Override
    public ArrayList<SGINode> getChildNodes() {
      return new ArrayList<>();
    }

    @Override
    public String getClassDescription() {
      return this.mElement != null ? this.mElement.getClassDescription() : "Shape";
    }

    @Override
    public String getInstanceDescription() {
      return this.mElement != null ? this.mElement.getInstanceDescription() : "Shape";
    }
  }

  /** A class of rectangles. */

  /** A class of ellipses. */
  class Ellipse extends SGShapeRect {
    public static final String NAME = "Ellipse";

    //
    Ellipse() {
      super(SGFigureElementShape.this);
    }

    /** */
    protected Shape getRectShape() {
      Rectangle2D rect = this.getElementBounds();
      Ellipse2D.Float el =
          new Ellipse2D.Float(
              (float) rect.getX(),
              (float) rect.getY(),
              (float) rect.getWidth(),
              (float) rect.getHeight());
      return el;
    }

    /** */
    public Object copy() {
      Ellipse el = new Ellipse();
      el.setShapeObject(this.getShapeObject());
      el.setMagnification(this.getMagnification());
      el.setProperties(this.getProperties());
      el.setShapeWithAxesValues();

      return el;
    }

    /** */
    public String getName() {
      return NAME;
    }

    @Override
    public String getShapeType() {
      return SHAPE_TYPE_ELLIPSE;
    }
  }

  /** Properties of rectangular shape. */
  static class RectangularShapeProperties extends RectangleProperties implements PropertyWithAxes {

    private double mXValue1 = 0.0;

    private double mYValue1 = 0.0;

    private double mXValue2 = 0.0;

    private double mYValue2 = 0.0;

    private int mXAxisLocation = -1;

    private int mYAxisLocation = -1;

    private boolean isAnchored = false;

    /** */
    public RectangularShapeProperties() {
      super();
    }

    /** */
    public boolean equals(final Object obj) {
      if ((obj instanceof RectangularShapeProperties) == false) {
        return false;
      }

      if (super.equals(obj) == false) return false;

      RectangularShapeProperties p = (RectangularShapeProperties) obj;

      if (p.mXValue1 != this.mXValue1) return false;
      if (p.mYValue1 != this.mYValue1) return false;
      if (p.mXValue2 != this.mXValue2) return false;
      if (p.mYValue2 != this.mYValue2) return false;
      if (this.mXAxisLocation != p.mXAxisLocation) return false;
      if (this.mYAxisLocation != p.mYAxisLocation) return false;
      if (this.isAnchored != p.isAnchored) return false;

      return true;
    }

    public Double getXValue1() {
      return Double.valueOf(this.mXValue1);
    }

    public Double getYValue1() {
      return Double.valueOf(this.mYValue1);
    }

    public Double getXValue2() {
      return Double.valueOf(this.mXValue2);
    }

    public Double getYValue2() {
      return Double.valueOf(this.mYValue2);
    }

    public int getXAxisLocation() {
      return this.mXAxisLocation;
    }

    public int getYAxisLocation() {
      return this.mYAxisLocation;
    }

    public Boolean getAnchored() {
      return Boolean.valueOf(this.isAnchored);
    }

    public boolean setXValue1(final double value) {
      this.mXValue1 = value;
      return true;
    }

    public boolean setYValue1(final double value) {
      this.mYValue1 = value;
      return true;
    }

    public boolean setXValue2(final double value) {
      this.mXValue2 = value;
      return true;
    }

    public boolean setYValue2(final double value) {
      this.mYValue2 = value;
      return true;
    }

    boolean setXAxisLocation(final int location) {
      this.mXAxisLocation = location;
      return true;
    }

    boolean setYAxisLocation(final int location) {
      this.mYAxisLocation = location;
      return true;
    }

    public boolean setAnchored(final boolean value) {
      this.isAnchored = value;
      return true;
    }
  }

  /** A class of arrow. */
  /** Properties of rectangular shape. */
  static class ArrowShapeProperties extends ArrowProperties implements PropertyWithAxes {
    private double mStartXValue = 0.0;

    private double mStartYValue = 0.0;

    private double mEndXValue = 0.0;

    private double mEndYValue = 0.0;

    private int mXAxisLocation = -1;

    private int mYAxisLocation = -1;

    private boolean mAnchored = false;

    /** */
    public ArrowShapeProperties() {
      super();
    }

    /** */
    public boolean equals(final Object obj) {
      if ((obj instanceof ArrowShapeProperties) == false) {
        return false;
      }

      if (super.equals(obj) == false) return false;

      ArrowShapeProperties p = (ArrowShapeProperties) obj;

      if (p.mStartXValue != this.mStartXValue) return false;
      if (p.mStartYValue != this.mStartYValue) return false;
      if (p.mEndXValue != this.mEndXValue) return false;
      if (p.mEndYValue != this.mEndYValue) return false;
      if (this.mXAxisLocation != p.mXAxisLocation) return false;
      if (this.mYAxisLocation != p.mYAxisLocation) return false;
      if (this.mAnchored != p.mAnchored) return false;

      return true;
    }

    public Double getStartXValue() {
      return Double.valueOf(this.mStartXValue);
    }

    public Double getStartYValue() {
      return Double.valueOf(this.mStartYValue);
    }

    public Double getEndXValue() {
      return Double.valueOf(this.mEndXValue);
    }

    public Double getEndYValue() {
      return Double.valueOf(this.mEndYValue);
    }

    public int getXAxisLocation() {
      return this.mXAxisLocation;
    }

    public int getYAxisLocation() {
      return this.mYAxisLocation;
    }

    public boolean setStartXValue(final double value) {
      this.mStartXValue = value;
      return true;
    }

    public boolean setStartYValue(final double value) {
      this.mStartYValue = value;
      return true;
    }

    public boolean setEndXValue(final double value) {
      this.mEndXValue = value;
      return true;
    }

    public boolean setEndYValue(final double value) {
      this.mEndYValue = value;
      return true;
    }

    boolean setXAxisLocation(final int location) {
      this.mXAxisLocation = location;
      return true;
    }

    boolean setYAxisLocation(final int location) {
      this.mYAxisLocation = location;
      return true;
    }

    public boolean setAnchored(final boolean value) {
      this.mAnchored = value;
      return true;
    }

    public boolean getAnchored() {
      return this.mAnchored;
    }
  }

  /**
   * Sets the dialog owner this figure element.
   *
   * @param frame the dialog owner
   * @return true if succeeded
   */
  public boolean setDialogOwner(final Frame frame) {
    if (super.setDialogOwner(frame) == false) {
      return false;
    }
    SwingUtilities.invokeLater(
        new Runnable() {
          public void run() {
            if (mShapeDialogMap != null) {
              if (mShapeDialogMap.get(SGShapeRect.NAME) == null) {
                mShapeDialogMap.put(
                    SGShapeRect.NAME, new SGRectangularShapeDialog(mDialogOwner, true));
              }
              if (mShapeDialogMap.get(SGShapeArrow.NAME) == null) {
                mShapeDialogMap.put(SGShapeArrow.NAME, new SGArrowDialog(mDialogOwner, true));
              }
            }
          }
        });
    return true;
  }

  /**
   * Sets the properties of child object.
   *
   * @param id the ID of child object
   * @param map a map properties
   * @return the result of setting properties
   */
  public SGPropertyResults setChildProperties(final int id, SGPropertyMap map) {
    return this.mShapePropertyUpdater.setChildProperties(id, map);
  }
}
