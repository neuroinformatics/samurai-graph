package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
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
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGGradationPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIAnchored;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGICopiable;
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
import jp.riken.brain.ni.samuraigraph.base.SGPatternPaint;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGSelectablePaint;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementArrow.ArrowProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementRectangle.RectangleProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIRectangleConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIShapeConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class managing shape objects.
 */
public class SGFigureElementShape extends SGFigureElement2D implements
        SGIFigureElementShape, SGIShapeConstants {

    /**
     * 
     */
    private SGIFigureElementAxis mAxisElement = null;

    /**
     * A map of property dialogs for shape objects.
     */
    private HashMap mShapeDialogMap = new HashMap();
    
    private ShapeObject mPressedShape = null;

    /**
     * 
     */
    public SGFigureElementShape() {
        super();
    }

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element) {
        this.mAxisElement = element;
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();

        // dispose the map of property dialogs
        Iterator itr = this.mShapeDialogMap.values().iterator();
        while (itr.hasNext()) {
            SGPropertyDialog dg = (SGPropertyDialog) itr.next();
            dg.dispose();
        }
        this.mShapeDialogMap.clear();
        this.mShapeDialogMap = null;
        
        this.mAxisElement = null;
    }

    /**
     * 
     * @return
     */
    public SGIFigureElementAxis getAxisElement() {
        return this.mAxisElement;
    }

    /**
     * Returns the property dialog for a shape.
     * @param sh
     *          a shape
     * @return
     *          the property dialog for a given shape
     */
    private SGPropertyDialog getShapeDialog(IElement sh) {
        
        // select the key of the dialog map
        Object key = null;
        if (sh instanceof Rect) {
            key = Rect.NAME;
        } else if (sh instanceof Arrow) {
            key = Arrow.NAME;
        } else {
            throw new Error("Unsupported shape: " + sh);
        }
        
        // get or create a property dialog
        Object obj = mShapeDialogMap.get(key);
        SGPropertyDialog dg = null;
        if (obj != null) {
            dg = (SGPropertyDialog) obj;
        } else {
            if (sh instanceof Rect) {
                dg = new SGRectangularShapeDialog(this.mDialogOwner, true);
            } else if (sh instanceof Arrow) {
                dg = new SGArrowDialog(this.mDialogOwner, true);
            }
            mShapeDialogMap.put(key, dg);
        }
        return dg;
    }
    
    /**
     * 
     */
    public boolean synchronize(SGIFigureElement element, String msg) {

        boolean flag = true;
        if (element instanceof SGIFigureElementGraph) {

        } else if (element instanceof SGIFigureElementString) {

        } else if (element instanceof SGIFigureElementLegend) {

        } else if (element instanceof SGIFigureElementAxis) {
            flag = this.synchronizeToAxisElement(
                    (SGIFigureElementAxis) element, msg);
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

    private boolean synchronizeToAxisElement(SGIFigureElementAxis element,
            String msg) {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            ShapeObject sh = (ShapeObject) list.get(ii);
            if (! sh.getIElement().isAnchored()) {
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
     * @param element
     *            An object to be synchronized.
     * @return
     */
    public boolean synchronizeArgument(final SGIFigureElement element,
            String msg) {
        // this shouldn't happen
        throw new Error();
    }

    /**
     * Inserts a shape object at a given point with default axes.
     * 
     * @param type
     *          the type of shape
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addShape(final int type, final float x, final float y) {
        if (this.getGraphRect().contains(x, y) == false) {
            return false;
        }

        // get axes
        SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_SHAPE_HORIZONTAL_AXIS);
        SGAxis yAxis = this.mAxisElement
                .getAxis(DEFAULT_SHAPE_VERTICAL_AXIS);

        return this.addShape(this.assignChildId(), type, x, y, xAxis, yAxis);
    }

    /**
     * Inserts a shape object at a given point with given axes.
     * 
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @param xAxis
     *          the x-axis
     * @param yAxis
     *          the y-axis
     * @return true if succeeded
     */
    private boolean addShape(final int id, final int type, final float x, 
            final float y, final SGAxis xAxis, final SGAxis yAxis) {
        
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
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addShape(final int id, final int type, final double x, 
            final double y) {
        return this.addShape(id, type, x, y, DEFAULT_SHAPE_HORIZONTAL_AXIS, 
                DEFAULT_SHAPE_VERTICAL_AXIS);
    }

    /**
     * Inserts a shape object with given axis values for given axes.
     * 
     * @param id
     *          the ID to set
     * @param type
     *          the type of shape
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxisLocation
     *          location of the x-axis
     * @param yAxisLocation
     *          location of the y-axis
     * @return true if succeeded
     */
    public boolean addShape(final int id, final int type, final double x,
            final double y, final String xAxisLocation,
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
    private boolean addRectangle(final int id, final float x, final float y,
            final SGAxis xAxis, final SGAxis yAxis) {
        Rect el = new Rect();
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
    private boolean addEllipse(final int id, 
            final float x, final float y, final SGAxis xAxis, final SGAxis yAxis) {
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

    private boolean addRectangularShape(final int id, Rect el, final double leftX,
			final double rightX, final double topY, final double bottomY,
			final SGAxis xAxis, final SGAxis yAxis) {
    	
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
    private boolean addArrow(final int id, 
            final float sx, final float sy, final float ex, final float ey,
            final SGAxis xAxis, final SGAxis yAxis) {
        Arrow el = new Arrow();
        return this
                .addArrow(id, el, DEFAULT_SHAPE_ARROW_START_HEAD_TYPE,
                        DEFAULT_SHAPE_ARROW_END_HEAD_TYPE,
                        sx, sy, ex, ey, xAxis, yAxis);
    }

    //
    private boolean addLine(final int id,
            final float sx, final float sy, final float ex, final float ey,
            final SGAxis xAxis, final SGAxis yAxis) {
        Arrow el = new Arrow();
        return this.addArrow(id, el, SGIArrowConstants.SYMBOL_TYPE_VOID,
        		SGIArrowConstants.SYMBOL_TYPE_VOID, sx, sy, ex, ey,
                xAxis, yAxis);
    }

    //
    private boolean addArrow(final int id, final Arrow el, 
            final int startType, final int endType, 
            final float sx, final float sy, final float ex, final float ey,
            final SGAxis xAxis, final SGAxis yAxis) {
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

    private boolean addArrowShape(final int id, Arrow el, final double startX,
			final double startY, final double endX, final double endY,
			final SGAxis xAxis, final SGAxis yAxis) {
    	
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
     * @param id
     *           the ID to set
     * @param sh
     *           a shape
     * @return true if succeeded
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

            // if one of two classes is not the subclass of another class,
            // clear the focus
            if (!cl_.isAssignableFrom(cl) && !cl.isAssignableFrom(cl_)) {
                el.setSelected(false);
            }
        }

        return true;
    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        if (super.setGraphRect(x, y, width, height) == false) {
            return false;
        }

        List<SGIChildObject> list = this.mChildList;
        for (int ii = 0; ii < list.size(); ii++) {
            final ShapeObject el = (ShapeObject) list.get(ii);
            if (! el.getIElement().isAnchored()) {
                el.setShapeWithAxesValues();
            } else {
                el.setAxisValuesWithShape();
            }
        }

        return true;
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        ShapeElementProperties p = new ShapeElementProperties();
        p.visibleShapeList = new ArrayList(this.getVisibleChildList());
        return p;
    }

    /**
     * 
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ShapeElementProperties) == false)
            return false;

        ShapeElementProperties wp = (ShapeElementProperties) p;
        final boolean flag = this.setVisibleChildList(wp.visibleShapeList);
        if (!flag) {
            return false;
        }

        return true;
    }

    /**
     * 
     */
    public boolean setMementoBackward() {
        boolean flag = super.setMementoBackward();
        if (!flag) {
            return false;
        }

        this.clearFocusedObjects();
        this.notifyChangeOnUndo();

        return true;
    }

    /**
     * 
     */
    public boolean setMementoForward() {
        boolean flag = super.setMementoForward();
        if (!flag) {
            return false;
        }

        this.clearFocusedObjects();
        this.notifyChangeOnUndo();

        return true;
    }

    /**
     * Create copies of the focused objects.
     * 
     * @return
     */
    public boolean duplicateFocusedObjects() {
        final int ox = (int) (this.mMagnification * OFFSET_DUPLICATED_OBJECT_X);
        final int oy = (int) (this.mMagnification * OFFSET_DUPLICATED_OBJECT_Y);

        List<SGICopiable> cList = this.duplicateObjects();
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

        // this.repaint();

        return true;
    }

    /**
     * Paste the objects.
     * 
     * @param list
     *            of the objects to be pasted
     * @return true:succeeded, false:failed
     */
    public boolean paste(List<SGICopiable> list) {
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

                shNew.setXAxis(this.mAxisElement
                        .getAxisInPlane(shOld.mTempXAxis));
                shNew.setYAxis(this.mAxisElement
                        .getAxisInPlane(shOld.mTempYAxis));

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

        // this.repaint();

        return true;
    }

    /**
     * 
     */
    public String getTagName() {
        return TAG_NAME_SHAPE;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el, SGExportParameter params) {
        return true;
    }

/*    
    public boolean onKeyPressed(final KeyEvent e) {
        boolean effective = false;
        final int keycode = e.getKeyCode();
        final int mod = e.getModifiersEx();
        final boolean isShiftPressed = ((mod & InputEvent.SHIFT_DOWN_MASK) != 0);
        int dx = 0;
        int dy = 0;
        switch (keycode) {
        case KeyEvent.VK_UP:
            if (isShiftPressed) {
                dy = -1;
            } else {
                dy = -10;
            }
            break;
        case KeyEvent.VK_DOWN:
            if (isShiftPressed) {
                dy = 1;
            } else {
                dy = 10;
            }
            break;
        case KeyEvent.VK_LEFT:
            if (isShiftPressed) {
                dx = -1;
            } else {
                dx = -10;
            }
            break;
        case KeyEvent.VK_RIGHT:
            if (isShiftPressed) {
                dx = 1;
            } else {
                dx = 10;
            }
            break;
        }
        if (dx != 0 || dy != 0) {
            final List<SGIChildObject> list = this.getVisibleChildList();
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                final ShapeObject el = (ShapeObject) list.get(ii);
                if (el.isSelected()) {
                    if (el.prepare() == false) {
                    	return false;
                    }
                    el.translate((float) dx, (float) dy);
                    if (el.commit() == false) {
                        return false;
                    }
                    notifyChange();
                    notifyToRoot();
                    repaint();
                    effective = true;
                }
            }
        }
        return effective;
    }
*/
    
    /**
     * 
     */
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
     * @param cl
     *            the class
     * @return the list of selected property dialog observers
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
     * @param cl
     *            the class
     * @return the list of visible property dialog observers
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
     * @param cl
     *            the class
     * @return the list of all property dialog observers
     */
    @Override
    public List<SGIPropertyDialogObserver> getAllPropertyDialogObserverList(Class<?> cl) {
    	return this.getVisiblePropertyDialogObserverList();
    }

    /**
     * Returns the class object of property dialog observer.
     * 
     * @return the class object
     */
    @Override
    public Class<?> getPropertyDialogObserverClass() {
    	return IElement.class;
    }

    /**
     * 
     */
    private boolean clickDrawingElements(final ShapeObject el,
            final MouseEvent e) {
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

    /**
     * 
     */
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

    /**
     * 
     */
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

    /**
     * 
     */
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

    /**
     * 
     */
    public boolean setMouseCursor(int x, int y) {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final ShapeObject el = (ShapeObject) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            final boolean flag = el.contains(x, y);
            // el.mFrameFlag = flag;

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
     * @param document
     *           an Document objects to append elements
     * @return an array of Element objects
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

        return new Element[] { el };
    }

    /**
     * Read properties from the Element object.
     * 
     * @param element
     *            an Element object which has properties
     * @param versionNumber
     *            the version number of property file
     * @return true if succeeded
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

//    /**
//     * 
//     */
//    public boolean setTemporaryPropertiesOfFocusedObjects() {
//    	List<SGISelectable> list = this.getFocusedObjectsList();
//        for (int ii = 0; ii < list.size(); ii++) {
//            ShapeObject el = (ShapeObject) list.get(ii);
//            el.mTemporaryProperties = el.getMemento();
//        }
//        return true;
//    }

    /**
     * Updates changed flag of focused objects.
     * 
     * @return true if succeeded
     */
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

    /**
     * 
     */
    public void paintGraphics(Graphics g, boolean clip) {
        Graphics2D g2d = (Graphics2D) g;

        List list = this.getVisibleChildList();
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
                List pList = sh.getAnchorPointList();
                if (!sh.getIElement().isAnchored()) {
                    SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(pList, g2d);
                } else {
                    SGUtilityForFigureElementJava2D.drawAnchorPointsAsAnchoredChildObject(pList, g2d);
                }
            }
        }

        // draw symbols around focused objects
        if (this.mSymbolsVisibleFlagAroundFocusedObjects) {
            ArrayList fList = new ArrayList();
            this.getFocusedObjectsList(fList);
            for (int ii = 0; ii < fList.size(); ii++) {
                ShapeObject sh = (ShapeObject) fList.get(ii);
                if (sh.isValid() == false) {
                    continue;
                }
                List pList = sh.getAnchorPointList();
                if (!sh.getIElement().isAnchored()) {
                    SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pList, g2d);
                } else {
                    SGUtilityForFigureElementJava2D.drawAnchorPointsAsAnchoredForcusObject(pList, g2d);
                }
            }
        }

    }

    /**
     * Returns a list of child nodes.
     * 
     * @return a list of chid nodes
     */
    public ArrayList getChildNodes() {
        final ArrayList list = new ArrayList();
        final ArrayList aList = new ArrayList(this.mChildList);
        for (int ii = 0; ii < aList.size(); ii++) {
            final ShapeObject el = (ShapeObject) aList.get(ii);
            if (el.isVisible()) {
                list.add(el.mElement);
            }
        }

        return list;
    }

    /**
     * 
     */
    public String getClassDescription() {
        return "Shape";
    }

    // check whether two points are within the radius
    private static boolean isInside(final int x1, final int y1, final int x2,
            final int y2) {
        final int radius = (int) (1.25f * ANCHOR_SIZE_FOR_FOCUSED_OBJECTS);
        return ((Math.abs(x1 - x2) < radius) && (Math.abs(y1 - y2) < radius));
    }

    /**
     * 
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            ShapeElementProperties p = (ShapeElementProperties) mList.get(ii);
            set.addAll(p.visibleShapeList);
        }

        return set;
    }

//    //
//    private ShapeObject getShape(final int id) {
//        ShapeObject el = (ShapeObject) this.getVisibleChild(id);
//        if (el == null) {
//            return null;
//        }
//        return el;
//    }
//
//    //
//    private Rect getRectangularShape(final int id) {
//        ShapeObject sh = this.getShape(id);
//        if (sh == null) {
//            return null;
//        }
//        IElement el = sh.mElement;
//        if (el instanceof Rect) {
//            return (Rect) el;
//        }
//        return null;
//    }
//
//    //
//    private Arrow getArrow(final int id) {
//        ShapeObject sh = this.getShape(id);
//        if (sh == null) {
//            return null;
//        }
//        IElement el = sh.mElement;
//        if (el instanceof Arrow) {
//            return (Arrow) el;
//        }
//        return null;
//    }


    /**
     * 
     */
    private static class ShapeElementProperties extends SGProperties {
        ArrayList visibleShapeList = new ArrayList();

        /**
         * 
         * 
         */
        public ShapeElementProperties() {
            super();
        }

        public void dispose() {
        	super.dispose();
            this.visibleShapeList.clear();
            this.visibleShapeList = null;
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ShapeElementProperties) == false)
                return false;

            ShapeElementProperties p = (ShapeElementProperties) obj;

            if (p.visibleShapeList.equals(this.visibleShapeList) == false)
                return false;

            return true;
        }

        /**
         * 
         */
        public String toString() {
        	StringBuffer sb = new StringBuffer();
        	sb.append('[');
        	sb.append(this.visibleShapeList.toString());
        	sb.append(']');
        	return sb.toString();
        }

    }

    /**
     * An interface of the drawing element.
     */
    private interface IElement extends SGINode,
            SGIPropertyDialogObserver, SGIDisposable, SGIAnchored {
        public void setShapeObject(ShapeObject sh);

        public Object copy();

        public void translate(final float dx, final float dy);

        public void translateSub(final float dx, final float dy);

        public SGProperties getMement();
        
        public boolean setMagnification(final float mag);

        /**
         * @param p
         * @return
         * @uml.property name="mement"
         */
        public boolean setMement(final SGProperties p);

        public String getName();

        public String getClassDescription();

        public String getInstanceDescription();

        public boolean setAxisValuesWithShape();

        public boolean setShapeWithAxesValues();

        public List getAnchorPointList();

        public int getMouseLocation(final int x, final int y);

        public boolean drag(MouseEvent e, Point pos, final int ml);

        public boolean writeProperty(final Element el, SGExportParameter params);

        public boolean readProperty(final Element element);

        /**
         * Sets the properties.
         * 
         * @param map
         *           a map of properties
         * @param iResult
         *           the result of setting properties
         * @return updated result
         */
        public SGPropertyResults setProperties(SGPropertyMap map, 
        		SGPropertyResults iResult);

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
        public SGPropertyMap getCommandPropertyMap();
        
        public String getShapeType();
    }

    private interface PropertyWithAxes {
        public int getXAxisLocation();

        public int getYAxisLocation();
    }
    
    private static class ShapeObjectPopupMenu extends JPopupMenu {
        
        private static final long serialVersionUID = 7825575947035545853L;
        
        private JCheckBoxMenuItem anchoredCheckBox;

        void init(ShapeObject so) {
            setBounds(0, 0, 100, 100);

            StringBuffer sb = new StringBuffer();
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

    /**
     * An inner class for the shape objects.
     */
    private class ShapeObject extends SGDrawingElement implements ActionListener,
    		SGIUndoable, SGIChildObject, SGIMovable, SGICopiable, SGIPropertyDialogObserver, 
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

        // /**
        // *
        // */
        // private boolean mFrameFlag = false;

        /**
         * 
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * The pop-up menu.
         */
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
        private void init() {
//            this.createPopupMenu();
        }

        /**
         * Disposes of this object.
         */
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

        /**
         * Returns whether this object is already disposed of.
         * 
         * @return true if this object is already disposed of
         */
        public boolean isDisposed() {
        	return this.mDisposed;
        }

        /**
         * Returns a pop-up menu.
         * @return
         *         a pop-up menu
         */
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
        
        /**
         * 
         */
        public void actionPerformed(ActionEvent e) {
            final String command = e.getActionCommand();
            // final Object source = e.getSource();

            if (command.equals(MENUCMD_PROPERTY)) {
                // clear all focused objects other type object clicked
                SGFigureElementShape.this.clearFocusedObjectOtherThan(this);

                // notify to figure
                SGFigureElementShape.this.setPropertiesOfSelectedObjects(this);
            } else {
                notifyToListener(command, e.getSource());
            }
        }

        /**
         * 
         */
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

        private SGFigureElementShape getShapeElement() {
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

//        public boolean zoom(final float mag) {
//            return this.getDrawingElement().zoom(mag);
//        }

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

        /**
         * 
         * @return
         */
        private List getAnchorPointList() {
            return this.mElement.getAnchorPointList();
        }

        /**
         * Location of mouse pointer.
         */
        private int mMouseLocation;

        /**
         * Sets the mouse location to an attribute.
         */
        private int getMouseLocation(final int x, final int y) {
            return this.mElement.getMouseLocation(x, y);
        }

        /**
         * 
         */
        private Cursor getCursor(final int location) {

            Cursor cur = null;

            switch (location) {
            case NORTH: {
                cur = new Cursor(Cursor.N_RESIZE_CURSOR);
                break;
            }
            case SOUTH: {
                cur = new Cursor(Cursor.S_RESIZE_CURSOR);
                break;
            }
            case WEST: {
                cur = new Cursor(Cursor.W_RESIZE_CURSOR);
                break;
            }
            case EAST: {
                cur = new Cursor(Cursor.E_RESIZE_CURSOR);
                break;
            }
            case NORTH_WEST: {
                cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
                break;
            }
            case SOUTH_WEST: {
                cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
                break;
            }
            case NORTH_EAST: {
                cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
                break;
            }
            case SOUTH_EAST: {
                cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
                break;
            }
            default: {
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

                // final float mag = this.getMagnification();

                // // set the temporary object
                // this.mTempSymbol = new SigDiffSymbol();
                // mTempSymbol.setMagnification( mag );
                // mTempSymbol.setLocation( this.getX(), this.getY() );
                // mTempSymbol.setSize(
                // this.getWidth(),
                // this.getVerticalHeight1(),
                // this.getVerticalHeight2()
                // );

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

        /**
         * 
         */
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

        /**
         * Flag whether this object is focused.
         */
        private boolean mSelectedFlag = false;

        /**
         * Get the flag as a focused object.
         * 
         * @return whether this object is focused.
         */
        public boolean isSelected() {
            return this.mSelectedFlag;
        }

        /**
         * Sets selected or deselected the object.
         * 
         * @param b
         *            true selects and false deselects the object
         */
        @Override
        public void setSelected(final boolean b) {
            this.mSelectedFlag = b;
        	this.mTemporaryProperties = b ? this.getProperties() : null;
        }

        /**
         * 
         * @return
         */
        public int getAxisConfiguration(SGAxis axis) {
            return SGFigureElementShape.this.mAxisElement
                    .getLocationInPlane(axis);
        }

        /**
         * Sets the location of the x-axis.
         * 
         * @param location
         *           the location of the x-axis
         * @param true if succeeded
         */
        public boolean setXAxis(final int location) {
            if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                    && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
                return false;
            }
            this.setXAxis(SGFigureElementShape.this.mAxisElement
                    .getAxisInPlane(location));
            return true;
        }

        /**
         * Sets the location of the y-axis.
         * 
         * @param location
         *           the location of the y-axis
         * @param true if succeeded
         */
        public boolean setYAxis(final int location) {
            if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                    && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
                return false;
            }
            this.setYAxis(SGFigureElementShape.this.mAxisElement
                    .getAxisInPlane(location));
            return true;
        }

        /**
         * 
         */
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

        /**
         * 
         */
        public void translate(final float dx, final float dy) {
        	if (this.equals(mPressedShape)) {
        		return;
        	}
            this.mElement.translate(dx, dy);
        }

        /**
         * 
         */
        public boolean isChanged() {
            return this.mUndoManager.isChanged();
        }

        public void setChanged(final boolean b) {
            this.mUndoManager.setChanged(b);
        }

        public boolean isChangedRoot() {
            return this.isChanged();
        }

        /**
         * Clear changed flag of this undoable object and all child objects.
         *
         */
        public void clearChanged() {
            this.setChanged(false);
        }

        /**
         * 
         */
        public boolean initPropertiesHistory() {
            return this.mUndoManager.initPropertiesHistory();
        }

        /**
         * 
         * 
         */
        public void notifyToRoot() {
            SGFigureElementShape.this.notifyToRoot();
        }

        /**
         * Update the list of history.
         */
        public boolean updateHistory() {
            return this.mUndoManager.updateHistory();
        }

        /**
         * 
         */
        public void initUndoBuffer() {
            this.mUndoManager.initUndoBuffer();
        }

        /**
         * Undo this object.
         */
        public boolean setMementoBackward() {
            if (this.mUndoManager.setMementoBackward() == false) {
                return false;
            }

            // this.mUndoManager.dump();

            this.setShapeWithAxesValues();

            return true;
        }

        /**
         * Redo this object.
         */
        public boolean setMementoForward() {
            if (this.mUndoManager.setMementoForward() == false) {
                return false;
            }

            // this.mUndoManager.dump();

            this.setShapeWithAxesValues();

            return true;
        }

        /**
         * Just calls undo method.
         */
        public boolean undo() {
            return this.setMementoBackward();
        }

        /**
         * Just calls redo method.
         */
        public boolean redo() {
            return this.setMementoForward();
        }

        /**
         * 
         */
        public SGProperties getMemento() {
            return this.mElement.getMement();
        }

        /**
         * 
         */
        public boolean setMemento(SGProperties p) {
            if ((p instanceof PropertyWithAxes) == false)
                return false;

            if (super.setProperties(p) == false)
                return false;

            PropertyWithAxes rp = (PropertyWithAxes) p;

            SGAxis xAxis = SGFigureElementShape.this.mAxisElement
                    .getAxisInPlane(rp.getXAxisLocation());
            if (xAxis == null)
                return false;
            this.setXAxis(xAxis);

            SGAxis yAxis = SGFigureElementShape.this.mAxisElement
                    .getAxisInPlane(rp.getYAxisLocation());
            if (yAxis == null)
                return false;
            this.setYAxis(yAxis);

            return this.mElement.setMement(p);
        }

        /**
         * 
         * @return
         */
        public boolean isUndoable() {
            return this.mUndoManager.isUndoable();
        }

        /**
         * 
         * @return
         */
        public boolean isRedoable() {
            return this.mUndoManager.isRedoable();
        }

        /**
         * Delete all forward histories.
         *
         * @return
         *         true if succeeded
         */
        public boolean deleteForwardHistory() {
            return this.mUndoManager.deleteForwardHistory();
        }

        /**
         * 
         * @param document
         * @return
         */
        public Element createElement(final Document document, SGExportParameter params) {
            Element el = document.createElement(this.mElement.getName());

            if (this.writeProperty(el, params) == false) {
                return null;
            }

            return el;
        }

        /**
         * 
         * @param element
         * @return
         */
        public boolean writeProperty(final Element element, SGExportParameter params) {
            SGIFigureElementAxis aElement = this.getAxisElement();
            element.setAttribute(KEY_X_AXIS_POSITION, aElement
                    .getLocationName(this.getXAxis()));
            element.setAttribute(KEY_Y_AXIS_POSITION, aElement
                    .getLocationName(this.getYAxis()));

            if (this.mElement.writeProperty(element, params) == false) {
                return false;
            }

            return true;
        }

        /**
         * 
         */
        public boolean readProperty(final Element element) {
            SGIFigureElementAxis aElement = this.getAxisElement();
            String str = null;

            // create an IElement object
            String tag = element.getTagName();
            IElement ie = null;
            if (tag.equals(Rect.NAME)) {
                ie = new Rect();
            } else if (tag.equals(Ellipse.NAME)) {
                ie = new Ellipse();
            } else if (tag.equals(Arrow.NAME)) {
                ie = new Arrow();
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
         * @param map
         *           a map of properties
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
    		StringBuffer sb = new StringBuffer();
    		
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
        	return SGCommandUtility.createCommandString(COM_SHAPE, 
    				Integer.toString(this.mID), this.getCommandPropertyMap(params));
        }

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
    	@Override
        public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
        	SGPropertyMap map = this.mElement.getCommandPropertyMap();
        	SGPropertyUtility.addProperty(map, COM_SHAPE_TYPE, 
        			this.mElement.getShapeType());
            SGPropertyUtility.addProperty(map, COM_SHAPE_AXIS_X, 
            		mAxisElement.getLocationName(this.getXAxis()));
            SGPropertyUtility.addProperty(map, COM_SHAPE_AXIS_Y, 
            		mAxisElement.getLocationName(this.getYAxis()));
			return map;
		}
    }

    /**
     * A class of rectangles.
     */
    private class Rect extends SGSimpleRectangle2D implements
            IElement, SGIRectangleConstants, SGIRectangularShapeDialogObserver {
    	
        public static final String NAME = "Rectangle";

        public static final String KEY_LEFT_X_VALUE = "LeftXValue";

        public static final String KEY_RIGHT_X_VALUE = "RightXValue";

        public static final String KEY_BOTTOM_Y_VALUE = "BottomYValue";

        public static final String KEY_TOP_Y_VALUE = "TopYValue";
        
        public static final String KEY_ANCHORED = "Anchored";

        // Shape object
        private ShapeObject mShape = null;

        /**
         * 
         */
        private double mXValue1;

        /**
         * 
         */
        private double mYValue1;

        /**
         * 
         */
        private double mXValue2;

        /**
         * 
         */
        private double mYValue2;
        
        /**
         * If this is true, rect object does not move or reshape with axis scaling.
         */
        private boolean mIsAnchored;

        //		
        private Rect() {
            super();
            this.init();
        }

        /**
         * 
         */
        private boolean init() {
            this.setWidth(DEFAULT_SHAPE_RETANGLE_WIDTH, cm);
            this.setHeight(DEFAULT_SHAPE_RETANGLE_HEIGHT, cm);
            
            SGSelectablePaint paint = new SGSelectablePaint();
            paint.setFillColor(DEFAULT_SHAPE_RECTANGLE_INNER_COLOR);
            paint.setSelectedPaintStyle(SGSelectablePaint.STYLE_INDEX_FILL);
            this.setInnerPaint(paint);
            
            this.setEdgeLineWidth(DEFAULT_SHAPE_RCTANGLE_EDGE_LINE_WIDTH,
                    LINE_WIDTH_UNIT);
            this.setEdgeLineType(DEFAULT_SHAPE_RCTANGLE_EDGE_LINE_TYPE);
            this.setEdgeLineColor(DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_COLOR);
            this.setEdgeLineVisible(DEFAULT_SHAPE_RECTANGLE_EDGE_LINE_VISIBLE);
            
            this.setAnchored(DEFAULT_SHAPE_RECTANGLE_ANCHORED);

            return true;
        }

        public void dispose() {
            super.dispose();
            this.mShape = null;
            if (this.mTemporaryProperties != null) {
                this.mTemporaryProperties.dispose();
                this.mTemporaryProperties = null;
            }
        }

        /**
         * 
         * @return
         */
        protected ShapeObject getShapeObject() {
            return this.mShape;
        }

        /**
         * 
         */
        public void setShapeObject(ShapeObject sh) {
            this.mShape = sh;
        }

        protected SGFigureElementShape getShapeElement() {
            return this.mShape.getShapeElement();
        }

        protected int getID() {
            return this.mShape.getID();
        }

        protected SGAxis getXAxis() {
            return this.mShape.getXAxis();
        }

        protected SGAxis getYAxis() {
            return this.mShape.getYAxis();
        }

        public float getLineWidth(final String unit) {
        	return this.getEdgeLineWidth(unit);
        }

        /**
         * 
         * @return
         */
        protected boolean isHorizontallyReversed() {
            return (this.getWidth() < 0.0f);
        }

        /**
         * 
         * @return
         */
        protected boolean isVerticallyReversed() {
            return (this.getHeight() < 0.0f);
        }

        /**
         * 
         * @return
         */
        public float getX1() {
            return this.getShapeElement().getXFromGraphRectValue(super.getX());
        }

        /**
         * 
         */
        public float getX() {
            return this.getX1();
        }

        /**
         * 
         * @return
         */
        public float getX2() {
            return this.getX1() + this.getMagnification() * this.getWidth();
        }

        /**
         * 
         */
        public float getY1() {
            return this.getShapeElement().getYFromGraphRectValue(super.getY());
        }

        /**
         * 
         */
        public float getY() {
            return this.getY1();
        }

        /**
         * 
         * @return
         */
        public float getY2() {
            return this.getY1() + this.getMagnification() * this.getHeight();
        }

        /**
         * 
         * @return
         */
        public float getLeftX() {
            return !this.isHorizontallyReversed() ? this.getX1() : this.getX2();
        }

        /**
         * 
         * @return
         */
        public float getRightX() {
            return !this.isHorizontallyReversed() ? this.getX2() : this.getX1();
        }

        /**
         * 
         * @return
         */
        public float getCenterX() {
            return this.getX1() + 0.50f * this.getMagnification()
                    * this.getWidth();
        }

        /**
         * 
         * @return
         */
        public float getTopY() {
            return !this.isVerticallyReversed() ? this.getY1() : this
                    .getY2();
        }

        /**
         * 
         * @return
         */
        public float getBottomY() {
            return !this.isVerticallyReversed() ? this.getY2() : this
                    .getY1();
        }

        /**
         * 
         * @return
         */
        public float getCenterY() {
            return this.getY1() + 0.50f * this.getMagnification()
                    * this.getHeight();
        }

        /**
         * 
         * @param value
         */
        public void setLeftX(final float x) {
            if (!this.isHorizontallyReversed()) {
                this.setX1(x);
            } else {
                this.setX2(x);
            }
        }

        /**
         * 
         * @param value
         */
        public void setRightX(final float x) {
            if (!this.isHorizontallyReversed()) {
                this.setX2(x);
            } else {
                this.setX1(x);
            }
        }

        /**
         * 
         * @param value
         */
        public void setTopY(final float y) {
            if (!this.isVerticallyReversed()) {
                this.setY1(y);
            } else {
                this.setY2(y);
            }
        }

        /**
         * 
         * @param value
         */
        public void setBottomY(final float y) {
            if (!this.isVerticallyReversed()) {
                this.setY2(y);
            } else {
                this.setY1(y);
            }
        }

        /**
         * 
         * @param x
         * @return
         */
        public boolean setX1(final float x) {
            return this.setX(x);
        }

        /**
         * 
         * @param x
         * @return
         */
        public boolean setX2(final float x) {
            return this.setX(x - this.getMagnification() * this.getWidth());
        }

        /**
         * 
         * @param y
         * @return
         */
        public boolean setY1(final float y) {
            return this.setY(y);
        }

        /**
         * 
         * @param y
         * @return
         */
        public boolean setY2(final float y) {
            return this.setY(y - this.getMagnification() * this.getHeight());
        }

        /**
         * 
         */
        public boolean setX(final float x) {
            return super.setX(this.getShapeElement().getGraphRectValueX(x));
        }

        /**
         * 
         */
        public boolean setY(final float y) {
            return super.setY(this.getShapeElement().getGraphRectValueY(y));
        }

        /**
         * 
         */
        public boolean setShapeWithAxesValues() {
            ShapeObject sh = this.mShape;
            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();

            SGFigureElementShape sElement = this.getShapeElement();

            final float x1 = sElement.calcLocation(this.mXValue1, xAxis, true);
            final float y1 = sElement.calcLocation(this.mYValue1, yAxis, false);
            final float x2 = sElement.calcLocation(this.mXValue2, xAxis, true);
            final float y2 = sElement.calcLocation(this.mYValue2, yAxis, false);

            if (Float.isNaN(x1) || Float.isNaN(y1) || Float.isNaN(x2)
                    || Float.isNaN(y2)) {
                sh.setValid(false);
                return false;
            }
            sh.setValid(true);

            // final float ratio = SGIConstants.CM_POINT_RATIO;
            // System.out.println(x1*ratio);
            // System.out.println(x2*ratio);
            // System.out.println(y1*ratio);
            // System.out.println(y2*ratio);
            // System.out.println();

            final float mag = this.getMagnification();
            final float w = (x2 - x1) / mag;
            final float h = (y2 - y1) / mag;
            this.setLocation(x1, y1);
            this.setWidth(w);
            this.setHeight(h);

            return true;
        }

        /**
         * 
         */
        public boolean setAxisValuesWithShape() {
            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();

            SGFigureElementShape sElement = this.getShapeElement();

            final double xValue1 = sElement
                    .calcValue(this.getX1(), xAxis, true);
            final double yValue1 = sElement.calcValue(this.getY1(), yAxis,
                    false);
            final double xValue2 = sElement
                    .calcValue(this.getX2(), xAxis, true);
            final double yValue2 = sElement.calcValue(this.getY2(), yAxis,
                    false);

            this.mXValue1 = SGUtilityNumber.getNumberInRangeOrder(xValue1, xAxis);
            this.mYValue1 = SGUtilityNumber.getNumberInRangeOrder(yValue1, yAxis);
            this.mXValue2 = SGUtilityNumber.getNumberInRangeOrder(xValue2, xAxis);
            this.mYValue2 = SGUtilityNumber.getNumberInRangeOrder(yValue2, yAxis);

            return true;
        }

        public int getMouseLocation(final int x, final int y) {
            final int left = (int) this.getLeftX();
            final int right = (int) this.getRightX();
            final int top = (int) this.getTopY();
            final int bottom = (int) this.getBottomY();
            final int centerX = (int) this.getCenterX();
            final int centerY = (int) this.getCenterY();

            int location = -1;
            if (isInside(left, top, x, y)) {
                location = NORTH_WEST;
            } else if (isInside(left, centerY, x, y)) {
                location = WEST;
            } else if (isInside(left, bottom, x, y)) {
                location = SOUTH_WEST;
            } else if (isInside(right, top, x, y)) {
                location = NORTH_EAST;
            } else if (isInside(right, centerY, x, y)) {
                location = EAST;
            } else if (isInside(right, bottom, x, y)) {
                location = SOUTH_EAST;
            } else if (isInside(centerX, top, x, y)) {
                location = NORTH;
            } else if (isInside(centerX, bottom, x, y)) {
                location = SOUTH;
            } else if (this.getElementBounds().contains(x, y)) {
                location = OTHER;
            }

            return location;
        }

        /**
         * 
         * @return
         */
        public List getAnchorPointList() {
            ArrayList list = new ArrayList();

            final float mag = this.getMagnification();
            final float x = this.getX();
            final float y = this.getY();
            final float w = mag * this.getWidth();
            final float h = mag * this.getHeight();
            final float cx = x + 0.50f * w;
            final float cy = y + 0.50f * h;

            Point2D pNW = new Point2D.Float(x, y);
            Point2D pNE = new Point2D.Float(x + w, y);
            Point2D pSW = new Point2D.Float(x, y + h);
            Point2D pSE = new Point2D.Float(x + w, y + h);
            Point2D pW = new Point2D.Float(x, cy);
            Point2D pE = new Point2D.Float(x + w, cy);
            Point2D pN = new Point2D.Float(cx, y);
            Point2D pS = new Point2D.Float(cx, y + h);

            list.add(pNW);
            list.add(pNE);
            list.add(pSW);
            list.add(pSE);
            list.add(pW);
            list.add(pE);
            list.add(pN);
            list.add(pS);

            return list;
        }

        /**
         * 
         */
        public boolean drag(MouseEvent e, Point pos, final int ml) {
            Rectangle2D rect = this.getElementBounds();
            // System.out.println(rect.getBounds());

            // update the rectangle
            SGUtility.resizeRectangle(rect, pos, e, ml);
            // System.out.println(rect.getBounds());

            final float mag = this.getMagnification();
            final float x = (float) rect.getX();
            final float y = (float) rect.getY();
            final float w = (float) rect.getWidth() / mag;
            final float h = (float) rect.getHeight() / mag;

            this.setWidth(w);
            this.setHeight(h);
            this.setLeftX(x);
            this.setTopY(y);

            // this.setRightX(x+w);
            // this.setBottomY(y+h);

            // System.out.println(this.getElementBounds().getBounds());
            // System.out.println();

            return true;
        }

        /**
         * 
         */
        public Object copy() {
            Rect el = new Rect();
            el.setShapeObject(this.mShape);
            el.setMagnification(this.getMagnification());
            el.setProperties(this.getProperties());
            el.setShapeWithAxesValues();

            return el;
        }

        /**
         * 
         */
        public void translate(final float dx, final float dy) {
        	this.translateSub(dx, dy);
        }

        public void translateSub(final float dx, final float dy) {
            this.setLocation(this.getX() + dx, this.getY() + dy);
            this.setAxisValuesWithShape();
        }

        /**
         * Overrode for the anchors.
         */
        public boolean contains(final int x, final int y) {
            if (super.contains(x, y) == true) {
                return true;
            }

            // if anchor is displayed
            if (this.mShape.isSelected()) {
                final int left = (int) this.getLeftX();
                final int right = (int) this.getRightX();
                final int top = (int) this.getTopY();
                final int bottom = (int) this.getBottomY();
                final int centerX = (int) this.getCenterX();
                final int centerY = (int) this.getCenterY();

                final boolean b = isInside(left, top, x, y)
                        || (isInside(left, centerY, x, y))
                        || (isInside(left, bottom, x, y))
                        || (isInside(right, top, x, y))
                        || (isInside(right, centerY, x, y))
                        || (isInside(right, bottom, x, y))
                        || (isInside(centerX, top, x, y))
                        || (isInside(centerX, bottom, x, y));
                if (b) {
                    return true;
                }
            }

            return false;
        }

        /**
         * 
         */
        public String getName() {
            return NAME;
        }

        /**
         * 
         */
        public String getClassDescription() {
            return "";
        }

        /**
         * Returns the description of an instance.
         * @return
         *       the description of an instance
         */
        public String getInstanceDescription() {
            SGIFigureElementAxis aElement = SGFigureElementShape.this.mAxisElement;
            String xAxis = aElement.getLocationName(this.getXAxis());
            String yAxis = aElement.getLocationName(this.getYAxis());

            StringBuffer sb = new StringBuffer();
            sb.append(this.getID());
            sb.append(": ");
            sb.append(this.getName());
            sb.append(", AxisX=");
            sb.append(xAxis.toString());
            sb.append(", AxisY=");
            sb.append(yAxis.toString());
            sb.append(", X=");
            sb.append(this.mXValue1);
            sb.append(", Y=");
            sb.append(this.mYValue1);
            return sb.toString();
        }

        /**
         * 
         */
        public ArrayList getChildNodes() {
            return new ArrayList();
        }

        /**
         * 
         */
        public SGProperties getProperties() {
            SGProperties p = new RectangularShapeProperties();
            if (this.getProperties(p) == false) {
                return null;
            }
            return p;
        }

        /**
         * 
         */
        public boolean getProperties(SGProperties p) {
            if ((p instanceof RectangularShapeProperties) == false) {
                return false;
            }
            if (super.getProperties(p) == false) {
                return false;
            }
            RectangularShapeProperties rp = (RectangularShapeProperties) p;

            SGFigureElementShape sElement = this.getShapeElement();
            SGIFigureElementAxis aElement = sElement.getAxisElement();

            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();
            rp.setXAxisLocation(aElement.getLocationInPlane(xAxis));
            rp.setYAxisLocation(aElement.getLocationInPlane(yAxis));

            rp.setXValue1(this.mXValue1);
            rp.setYValue1(this.mYValue1);
            rp.setXValue2(this.mXValue2);
            rp.setYValue2(this.mYValue2);
            rp.setAnchored(this.mIsAnchored);

            // System.out.println(this.mXValue1+" "+this.mXValue2);
            return true;
        }

        /**
         * 
         */
        public boolean setProperties(SGProperties p) {
            if ((p instanceof RectangularShapeProperties) == false)
                return false;

            if (super.setProperties(p) == false)
                return false;

            RectangularShapeProperties rp = (RectangularShapeProperties) p;

            final Double x1 = rp.getXValue1();
            if (x1 == null)
                return false;
            this.mXValue1 = x1.doubleValue();

            final Double y1 = rp.getYValue1();
            if (y1 == null)
                return false;
            this.mYValue1 = y1.doubleValue();

            final Double x2 = rp.getXValue2();
            if (x2 == null)
                return false;
            this.mXValue2 = x2.doubleValue();

            final Double y2 = rp.getYValue2();
            if (y2 == null)
                return false;
            this.mYValue2 = y2.doubleValue();
            
            final Boolean anchored = rp.getAnchored();
            if (anchored == null)
                return false;
            this.mIsAnchored = anchored.booleanValue();

            return true;
        }

        /**
         * 
         */
        public SGProperties getMement() {
            return this.getProperties();
        }

        /**
         * 
         */
        public boolean setMement(SGProperties p) {
            return this.setProperties(p);
        }

        //
        // dialog
        //

        /**
         * 
         */
        public double getLeftXValue() {
            return !this.isHorizontallyReversed() ? this.mXValue1
                    : this.mXValue2;
        }

        /**
         * 
         */
        public double getTopYValue() {
            return !this.isVerticallyReversed() ? this.mYValue1
                    : this.mYValue2;
        }

        /**
         * 
         */
        public double getRightXValue() {
            return !this.isHorizontallyReversed() ? this.mXValue2
                    : this.mXValue1;
        }

        /**
         * 
         */
        public double getBottomYValue() {
            return !this.isVerticallyReversed() ? this.mYValue2
                    : this.mYValue1;
        }

        @Override
        public boolean isAnchored() {
            return this.mIsAnchored;
        }

        /**
         * 
         */
        public float getLineWidth() {
            return this.getEdgeLineWidth();
        }

        /**
         * 
         */
        public int getLineType() {
            return this.getEdgeLineType();
        }

        /**
         * 
         */
        public Color getLineColor() {
            return this.getEdgeLineColor();
        }

        @Override
        public boolean isLineVisible() {
            return this.isEdgeLineVisible();
        }

        /**
         * Sets the axis value of the left edge.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setLeftXValue(final double value) {
        	if (this.getXAxis().isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isHorizontallyReversed()) {
                this.mXValue1 = value;
            } else {
                this.mXValue2 = value;
            }
            return true;
        }

        /**
         * Sets the axis value of the right edge.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setRightXValue(final double value) {
        	if (this.getXAxis().isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isHorizontallyReversed()) {
                this.mXValue2 = value;
            } else {
                this.mXValue1 = value;
            }
            return true;
        }


        /**
         * Sets the axis value of the upper edge.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setTopYValue(final double value) {
        	if (this.getYAxis().isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isVerticallyReversed()) {
                this.mYValue1 = value;
            } else {
                this.mYValue2 = value;
            }
            return true;
        }

        /**
         * Sets the axis value of the lower edge.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setBottomYValue(final double value) {
        	if (this.getYAxis().isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isVerticallyReversed()) {
                this.mYValue2 = value;
            } else {
                this.mYValue1 = value;
            }
            return true;
        }

        @Override
        public boolean setAnchored(boolean anchored) {
            this.mIsAnchored = anchored;
            return true;
        }
        
        /**
         * Sets the line width in a given unit.
         * 
         * @param lw
         *           the line width to set
         * @param unit
         *           the unit for given line width
         * @return true if succeeded
         */
        public boolean setLineWidth(final float lw, final String unit) {
        	return this.setEdgeLineWidth(lw, unit);
        }

        /**
         * Sets the edge line type.
         * 
         * @param type
         *           the line type
         * @return true if succeeded
         */
        public boolean setLineType(final int type) {
        	return this.setEdgeLineType(type);
        }

        /**
         * Sets the line color.
         * 
         * @param cl
         *           the color to set
         * @return true if succeeded
         */
        public boolean setLineColor(final Color cl) {
            return this.setEdgeLineColor(cl);
        }
        
        @Override
        public boolean setLineVisible(final boolean visible) {
            return this.setEdgeLineVisible(visible);
        }

        /**
         * 
         */
        public int getXAxisLocation() {
            return this.mShape.getAxisConfiguration(this.getXAxis());
        }

        /**
         * 
         */
        public int getYAxisLocation() {
            return this.mShape.getAxisConfiguration(this.getYAxis());
        }

        /**
         * 
         */
        public boolean setXAxisLocation(final int config) {
            if (config != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                    && config != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
                return false;
            }
            this.mShape.setXAxis(config);
            return true;
        }

        /**
         * 
         */
        public boolean setYAxisLocation(final int config) {
            if (config != SGIFigureElementAxis.AXIS_VERTICAL_1
                    && config != SGIFigureElementAxis.AXIS_VERTICAL_2) {
                return false;
            }
            this.mShape.setYAxis(config);
            return true;
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidLeftXValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.getXAxis() : aElement
                    .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getLeftXValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidTopYValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.mShape.getYAxis()
                    : aElement.getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getTopYValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidRightXValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.getXAxis() : aElement
                    .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getRightXValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidBottomYValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.getYAxis() : aElement
                    .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getBottomYValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * 
         */
        public boolean prepare() {
            this.mTemporaryProperties = this.getProperties();
            // this.dump();
            return true;
        }

        /**
         * 
         */
        public boolean commit() {
            SGProperties pTemp = this.mTemporaryProperties;
            SGProperties pPresent = this.getProperties();
            if (pTemp.equals(pPresent) == false) {
                this.mShape.setChanged(true);
            }

            this.setShapeWithAxesValues();

            SGFigureElementShape sh = this.getShapeElement();
            sh.notifyChangeOnCommit();
            sh.repaint();

            return true;
        }

        /**
         * 
         */
        public boolean cancel() {
            if (this.setProperties(this.mTemporaryProperties) == false) {
                return false;
            }
            this.mTemporaryProperties = null;

            this.setShapeWithAxesValues();
            SGFigureElementShape sh = this.getShapeElement();
            sh.notifyChangeOnCancel();
            sh.repaint();
            return true;
        }

        /**
         * 
         */
        public boolean preview() {
            this.setShapeWithAxesValues();
            SGFigureElementShape sh = this.getShapeElement();
            sh.notifyChange();
            sh.repaint();
            return true;
        }

        /**
         * Returns the property dialog.
         * 
         * @return
         *        a property dialog
         */
        public SGPropertyDialog getPropertyDialog() {
            return getShapeDialog(this);
        }

        public void dump() {
            System.out.println(this.mXValue1 + "  " + this.mXValue2);
            System.out.println(this.mYValue1 + "  " + this.mYValue2);
            System.out.println();
        }

		@Override
		public SGPropertyMap getCommandPropertyMap() {
			SGPropertyMap map = new SGPropertyMap();
			
			// properties of shape
			this.addProperties(map, COM_RECTANGLE_EDGE_LINE_WIDTH, COM_RECTANGLE_EDGE_LINE_TYPE, 
					COM_RECTANGLE_EDGE_LINE_COLOR, COM_RECTANGLE_EDGE_LINE_VISIBLE);

			// properties of location
			this.addProperties(map, COM_RECTANGLE_LEFT_X, COM_RECTANGLE_RIGHT_X, 
					COM_RECTANGLE_TOP_Y, COM_RECTANGLE_BOTTOM_Y, COM_RECTANGLE_ANCHORED);
			
			// properties of inner paint
        	String[] rectKeys = {
        			COM_RECTANGLE_BACKGROUND_PAINT_STYLE,
        			COM_RECTANGLE_BACKGROUND_FILL_COLOR,
        			COM_RECTANGLE_BACKGROUND_PATTERN_COLOR,
        			COM_RECTANGLE_BACKGROUND_PATTERN_TYPE,
        			COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1,
        			COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2,
        			COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION,
        			COM_RECTANGLE_BACKGROUND_GRADATION_ORDER,
        			COM_RECTANGLE_BACKGROUND_TRANSPARENCY
        	};
        	SGSelectablePaint.COMMAND_KEYS[] comKeys = SGSelectablePaint.COMMAND_KEYS.values();
        	Map<SGSelectablePaint.COMMAND_KEYS, String> keyMap 
        			= new HashMap<SGSelectablePaint.COMMAND_KEYS, String>();
        	for (int ii = 0; ii < comKeys.length; ii++) {
        		keyMap.put(comKeys[ii], rectKeys[ii]);
        	}
        	SGSelectablePaint paint = (SGSelectablePaint) this.getInnerPaint();
        	paint.getProperties(map, keyMap);

			return map;
		}

        @Override
    	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	this.addProperties(map, KEY_LEFT_X_VALUE, KEY_RIGHT_X_VALUE, KEY_TOP_Y_VALUE, 
        			KEY_BOTTOM_Y_VALUE, KEY_ANCHORED);
    		return map;
    	}

    	private void addProperties(SGPropertyMap map, String leftXKey, String rightXKey,
    			String topYKey, String bottomYKey, String anchorKey) {
            SGPropertyUtility.addProperty(map, leftXKey, this.getLeftXValue());
            SGPropertyUtility.addProperty(map, rightXKey, this.getRightXValue());
            SGPropertyUtility.addProperty(map, topYKey, this.getTopYValue());
            SGPropertyUtility.addProperty(map, bottomYKey, this.getBottomYValue());
            SGPropertyUtility.addProperty(map, anchorKey, this.isAnchored());
    	}

        /**
         * 
         */
        public boolean readProperty(final Element el) {
            if (super.readProperty(el) == false) {
                return false;
            }

            String str = null;
            Number num = null;
            Boolean b = null;

            final SGAxis xAxis = this.getXAxis();
            final SGAxis yAxis = this.getYAxis();

            // left x value
            str = el.getAttribute(KEY_LEFT_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double leftXValue = num.doubleValue();
                if (xAxis.isValidValue(leftXValue) == false) {
                    return false;
                }
                this.setLeftXValue(leftXValue);
            }

            // right x value
            str = el.getAttribute(KEY_RIGHT_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double rightXValue = num.doubleValue();
                if (xAxis.isValidValue(rightXValue) == false) {
                    return false;
                }
                this.setRightXValue(rightXValue);
            }

            // bottom y value
            str = el.getAttribute(KEY_BOTTOM_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double bottomYValue = num.doubleValue();
                if (yAxis.isValidValue(bottomYValue) == false) {
                    return false;
                }
                this.setBottomYValue(bottomYValue);
            }

            // top y value
            str = el.getAttribute(KEY_TOP_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double topYValue = num.doubleValue();
                if (yAxis.isValidValue(topYValue) == false) {
                    return false;
                }
                this.setTopYValue(topYValue);
            }
            
            // anchored
            str = el.getAttribute(KEY_ANCHORED);
            if (str.length() != 0) {
                b = SGUtilityText.getBoolean(str);
                if (b == null) {
                    return false;
                }
                this.setAnchored(b.booleanValue());
                this.setShapeWithAxesValues();
            }

            return true;
        }

        /**
         * Sets the properties.
         * 
         * @param map
         *           a map of properties
         * @param iResult
         *           the result of setting properties
         * @return updated result
         */
        public SGPropertyResults setProperties(SGPropertyMap map, 
        		SGPropertyResults iResult) {
        	
            SGPropertyResults result = (SGPropertyResults) iResult.clone();

            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                String value = map.getValueString(key);
                
                if (COM_RECTANGLE_LEFT_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLeftXValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_LEFT_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_RIGHT_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setRightXValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_RIGHT_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_TOP_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setTopYValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_TOP_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BOTTOM_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setBottomYValue(num.doubleValue()) == false) {
                        result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BOTTOM_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_EDGE_LINE_WIDTH.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setEdgeLineWidth(num.floatValue(), unit
                            .toString()) == false) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_EDGE_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_EDGE_LINE_TYPE.equalsIgnoreCase(key)) {
                    final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                    if (type == null) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setEdgeLineType(type) == false) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_EDGE_LINE_TYPE, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_EDGE_LINE_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.parseColorString(value);
                    if (cl == null) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineColor(cl) == false) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_EDGE_LINE_COLOR, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_EDGE_LINE_VISIBLE.equalsIgnoreCase(key)) {
                    Boolean b = SGUtilityText.getBoolean(value);
                    if (b == null) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineVisible(b.booleanValue()) == false) {
                        result.putResult(COM_RECTANGLE_EDGE_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_EDGE_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_PAINT_STYLE.equalsIgnoreCase(key)) {
                    final Integer index = SGSelectablePaint.getStyleIndex(value);
                    if (index == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_PAINT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerPaintStyle(index) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_PAINT_STYLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_PAINT_STYLE, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_FILL_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.parseColorString(value);
                    if (cl == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_FILL_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerFillColor(cl) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_FILL_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_FILL_COLOR, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_PATTERN_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.parseColorString(value);
                    if (cl == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerPatternColor(cl) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_COLOR, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_PATTERN_TYPE.equalsIgnoreCase(key)) {
                    final Integer type = SGPatternPaint.getTypeFromName(value);
                    if (type == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerPatternType(type) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_PATTERN_TYPE, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.parseColorString(value);
                    if (cl == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerGradationColor1(cl) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR1, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.parseColorString(value);
                    if (cl == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerGradationColor2(cl) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_COLOR2, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION.equalsIgnoreCase(key)) {
                    final Integer index = SGGradationPaint.getDirectionIndex(value);
                    if (index == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerGradationDirection(index) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_DIRECTION, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_GRADATION_ORDER.equalsIgnoreCase(key)) {
                    final Integer index = SGGradationPaint.getOrderIndex(value);
                    if (index == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_ORDER, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInnerGradationOrder(index) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_ORDER, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_GRADATION_ORDER, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_BACKGROUND_TRANSPARENCY.equalsIgnoreCase(key)) {
                    Integer num = SGUtilityText.getInteger(value, SGIConstants.percent);
                    if (num == null) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setTransparent(num.intValue()) == false) {
                        result.putResult(COM_RECTANGLE_BACKGROUND_TRANSPARENCY, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_BACKGROUND_TRANSPARENCY, SGPropertyResults.SUCCEEDED);
                } else if (COM_RECTANGLE_ANCHORED.equalsIgnoreCase(key)) {
                    Boolean b = SGUtilityText.getBoolean(value);
                    if (b == null) {
                        result.putResult(COM_RECTANGLE_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAnchored(b.booleanValue()) == false) {
                        result.putResult(COM_RECTANGLE_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_RECTANGLE_ANCHORED, SGPropertyResults.SUCCEEDED);
                }
            }            

            return result;
        }

		@Override
		public String getShapeType() {
			return SHAPE_TYPE_RECTANGLE;
		}

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
    }

    /**
     * A class of ellipses.
     * 
     */
    private class Ellipse extends Rect {
        public static final String NAME = "Ellipse";

        //
        private Ellipse() {
            super();
        }

        /**
         * 
         */
        protected Shape getRectShape() {
            Rectangle2D rect = this.getElementBounds();
            Ellipse2D.Float el = new Ellipse2D.Float((float) rect.getX(),
                    (float) rect.getY(), (float) rect.getWidth(), (float) rect
                            .getHeight());
            return el;
        }

        /**
         * 
         */
        public Object copy() {
            Ellipse el = new Ellipse();
            el.setShapeObject(this.getShapeObject());
            el.setMagnification(this.getMagnification());
            el.setProperties(this.getProperties());
            el.setShapeWithAxesValues();

            return el;
        }

        /**
         * 
         */
        public String getName() {
            return NAME;
        }

		@Override
		public String getShapeType() {
			return SHAPE_TYPE_ELLIPSE;
		}
    }

    /**
     * Properties of rectangular shape.
     */
    private static class RectangularShapeProperties extends RectangleProperties
            implements PropertyWithAxes {

        private double mXValue1 = 0.0;

        private double mYValue1 = 0.0;

        private double mXValue2 = 0.0;

        private double mYValue2 = 0.0;

        private int mXAxisLocation = -1;

        private int mYAxisLocation = -1;
        
        private boolean isAnchored = false;

        /**
         * 
         */
        public RectangularShapeProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof RectangularShapeProperties) == false) {
                return false;
            }

            if (super.equals(obj) == false)
                return false;

            RectangularShapeProperties p = (RectangularShapeProperties) obj;

            if (p.mXValue1 != this.mXValue1)
                return false;
            if (p.mYValue1 != this.mYValue1)
                return false;
            if (p.mXValue2 != this.mXValue2)
                return false;
            if (p.mYValue2 != this.mYValue2)
                return false;
            if (this.mXAxisLocation != p.mXAxisLocation)
                return false;
            if (this.mYAxisLocation != p.mYAxisLocation)
                return false;
            if (this.isAnchored != p.isAnchored)
                return false;

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

        private boolean setXAxisLocation(final int location) {
            this.mXAxisLocation = location;
            return true;
        }

        private boolean setYAxisLocation(final int location) {
            this.mYAxisLocation = location;
            return true;
        }
        
        public boolean setAnchored(final boolean value) {
            this.isAnchored = value;
            return true;
        }

    }

    /**
     * A class of arrow.
     */
    private class Arrow extends SGSimpleArrow2D implements
            IElement, SGIArrowConstants, SGIArrowDialogObserver {
        public static final String NAME = "Arrow";

        public static final String KEY_START_X_VALUE = "StartXValue";

        public static final String KEY_START_Y_VALUE = "StartYValue";

        public static final String KEY_END_X_VALUE = "EndXValue";

        public static final String KEY_END_Y_VALUE = "EndYValue";
        
        public static final String KEY_ANCHORED = "Anchored";

        /**
         * 
         */
        private float mStartX;

        private float mStartY;

        private float mEndX;

        private float mEndY;

        /**
         * 
         */
        private double mStartXValue;

        private double mStartYValue;

        private double mEndXValue;

        private double mEndYValue;

        private ShapeObject mShape = null;
        /**
         * If this is true, rect object does not move or reshape with axis scaling.
         */
        private boolean mIsAnchored;

        private Arrow() {
            super();
            
            // set default properties
            this.setLineWidth(DEFAULT_SHAPE_ARROW_LINE_WIDTH, LINE_WIDTH_UNIT);
            this.setLineType(DEFAULT_SHAPE_ARROW_LINE_TYPE);
            this.setColor(DEFAULT_SHAPE_ARROW_COLOR);
            this.setHeadSize(DEFAULT_SHAPE_ARROW_HEAD_SIZE,
                    SGIArrowConstants.ARROW_HEAD_SIZE_UNIT);
            this.setHeadAngle(DEFAULT_SHAPE_ARROW_HEAD_OPEN_ANGLE,
                    DEFAULT_SHAPE_ARROW_HEAD_CLOSE_ANGLE);
            this.setAnchored(DEFAULT_SHAPE_ARROW_ANCHORED);
        }

        public void dispose() {
            super.dispose();
            this.mShape = null;
            if (this.mTemporaryProperties != null) {
                this.mTemporaryProperties.dispose();
                this.mTemporaryProperties = null;
            }
        }

        protected SGFigureElementShape getShapeElement() {
            return this.mShape.getShapeElement();
        }

        /**
         * 
         */
        public void setShapeObject(ShapeObject sh) {
            this.mShape = sh;
        }

        protected int getID() {
            return this.mShape.getID();
        }

        protected SGAxis getXAxis() {
            return this.mShape.getXAxis();
        }

        protected SGAxis getYAxis() {
            return this.mShape.getYAxis();
        }

        private static final int START = NORTH_EAST;

        private static final int END = SOUTH_EAST;

        private static final int BODY = OTHER;

        public int getMouseLocation(final int x, final int y) {
            // final int radius = (int)( 1.25f*ANCHOR_SIZE_FOR_FOCUSED_OBJECTS
            // );

            final int startX = (int) this.getStartX();
            final int startY = (int) this.getStartY();
            final int endX = (int) this.getEndX();
            final int endY = (int) this.getEndY();

            int location = -1;
            {
                if (isInside(startX, startY, x, y)) {
                    location = START;
                } else if (isInside(endX, endY, x, y)) {
                    location = END;
                } else {
                    location = BODY;
                }
            }

            return location;
        }

        /**
         * 
         */
        public int getXAxisLocation() {
            return this.mShape.getAxisConfiguration(this.getXAxis());
        }

        /**
         * 
         */
        public int getYAxisLocation() {
            return this.mShape.getAxisConfiguration(this.getYAxis());
        }

        /**
         * 
         * @return
         */
        public float getStartX() {
            return this.getShapeElement().getXFromGraphRectValue(this.mStartX);
        }

        /**
         * 
         * @return
         */
        public float getStartY() {
            return this.getShapeElement().getYFromGraphRectValue(this.mStartY);
        }

        /**
         * 
         * @return
         */
        public float getEndX() {
            return this.getShapeElement().getXFromGraphRectValue(this.mEndX);
        }

        /**
         * 
         * @return
         */
        public float getEndY() {
            return this.getShapeElement().getYFromGraphRectValue(this.mEndY);
        }

        /**
         * Sets the location of the x-axis.
         * 
         * @param location
         *           the location of the x-axis
         * @param true if succeeded
         */
        public boolean setXAxisLocation(final int location) {
            return this.mShape.setXAxis(location);
        }

        /**
         * Sets the location of the x-axis.
         * 
         * @param location
         *           the location of the x-axis
         * @param true if succeeded
         */
        public boolean setYAxisLocation(final int location) {
            return this.mShape.setYAxis(location);
        }

        /**
         * 
         */
        public boolean setStartX(final float x) {
            super.setStartX(x);
            this.mStartX = this.getShapeElement().getGraphRectValueX(x);
            return true;
        }

        /**
         * 
         */
        public boolean setStartY(final float y) {
            super.setStartY(y);
            this.mStartY = this.getShapeElement().getGraphRectValueY(y);
            return true;
        }

        /**
         * 
         */
        public boolean setEndX(final float x) {
            super.setEndX(x);
            this.mEndX = this.getShapeElement().getGraphRectValueX(x);
            return true;
        }

        /**
         * 
         */
        public boolean setEndY(final float y) {
            super.setEndY(y);
            this.mEndY = this.getShapeElement().getGraphRectValueY(y);
            return true;
        }

        /**
         * 
         */
        public double getStartXValue() {
            return this.mStartXValue;
        }

        /**
         * 
         */
        public double getStartYValue() {
            return this.mStartYValue;
        }

        /**
         * 
         */
        public double getEndXValue() {
            return this.mEndXValue;
        }

        /**
         * 
         */
        public double getEndYValue() {
            return this.mEndYValue;
        }

        /**
         * Sets the axis value of the start x.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setStartXValue(final double value) {
        	if (this.getXAxis().isValidValue(value) == false) {
        		return false;
        	}
            this.mStartXValue = value;
            return true;
        }

        /**
         * Sets the axis value of the start y.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setStartYValue(final double value) {
        	if (this.getYAxis().isValidValue(value) == false) {
        		return false;
        	}
            this.mStartYValue = value;
            return true;
        }

        /**
         * Sets the axis value of the end x.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setEndXValue(final double value) {
        	if (this.getXAxis().isValidValue(value) == false) {
        		return false;
        	}
            this.mEndXValue = value;
            return true;
        }

        /**
         * Sets the axis value of the end y.
         * 
         * @param value
         *           the axis value to set
         * @return true if succeeded
         */
        public boolean setEndYValue(final double value) {
        	if (this.getYAxis().isValidValue(value) == false) {
        		return false;
        	}
            this.mEndYValue = value;
            return true;
        }

        @Override
        public boolean isAnchored() {
            return this.mIsAnchored;
        }

        @Override
        public boolean setAnchored(boolean anchored) {
            this.mIsAnchored = anchored;
            return true;
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidStartXValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.getXAxis() : aElement
                    .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getStartXValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidStartYValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.getYAxis() : aElement
                    .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getStartYValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidEndXValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.getXAxis() : aElement
                    .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getEndXValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidEndYValue(final int config, final Number value) {
            SGIFigureElementAxis aElement = this.getShapeElement()
                    .getAxisElement();
            final SGAxis axis = (config == -1) ? this.mShape.getYAxis()
                    : aElement.getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getEndYValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param open
         * @param close
         * @return
         */
        public boolean hasValidAngle(final Number open, final Number close) {
            final float openAngle = (open != null) ? open.floatValue() : this
                    .getHeadOpenAngle();
            final float closeAngle = (close != null) ? close.floatValue()
                    : this.getHeadCloseAngle();
            return (openAngle < closeAngle);
        }

        /**
         * 
         */
        public boolean setAxisValuesWithShape() {
            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();

            SGFigureElementShape sElement = this.getShapeElement();

            // System.out.println(super.getStartX()+" "+this.getStartX());

            final double xValue1 = sElement.calcValue(this.getStartX(), xAxis,
                    true);
            final double yValue1 = sElement.calcValue(this.getStartY(), yAxis,
                    false);
            final double xValue2 = sElement.calcValue(this.getEndX(), xAxis,
                    true);
            final double yValue2 = sElement.calcValue(this.getEndY(), yAxis,
                    false);

            this.mStartXValue = SGUtilityNumber.getNumberInRangeOrder(xValue1, xAxis);
            this.mStartYValue = SGUtilityNumber.getNumberInRangeOrder(yValue1, yAxis);
            this.mEndXValue = SGUtilityNumber.getNumberInRangeOrder(xValue2, xAxis);
            this.mEndYValue = SGUtilityNumber.getNumberInRangeOrder(yValue2, yAxis);
            
            this.setShapeWithAxesValues();

            // System.out.println(xValue1+" "+xValue2);
            // System.out.println(this.mStartXValue+" "+this.mEndXValue);
            // System.out.println();

            return true;
        }

        /**
         * 
         */
        public boolean setShapeWithAxesValues() {
            ShapeObject sh = this.mShape;
            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();

            SGFigureElementShape sElement = this.getShapeElement();

            final float x1 = sElement.calcLocation(this.mStartXValue, xAxis,
                    true);
            final float y1 = sElement.calcLocation(this.mStartYValue, yAxis,
                    false);
            final float x2 = sElement
                    .calcLocation(this.mEndXValue, xAxis, true);
            final float y2 = sElement.calcLocation(this.mEndYValue, yAxis,
                    false);

            if (Float.isNaN(x1) || Float.isNaN(y1) || Float.isNaN(x2)
                    || Float.isNaN(y2)) {
                sh.setValid(false);
                return false;
            }
            sh.setValid(true);

            this.setStartX(x1);
            this.setStartY(y1);
            this.setEndX(x2);
            this.setEndY(y2);

            return true;
        }

        /**
         * 
         * @return
         */
        public List getAnchorPointList() {
            ArrayList list = new ArrayList();

            Point2D ps = new Point2D.Float(this.getStartX(), this.getStartY());
            Point2D pe = new Point2D.Float(this.getEndX(), this.getEndY());

            list.add(ps);
            list.add(pe);

            return list;
        }

        /**
         * 
         */
        public Object copy() {
            Arrow el = new Arrow();
            el.setShapeObject(this.mShape);
            el.setMagnification(this.getMagnification());
            el.setProperties(this.getProperties());
            el.setShapeWithAxesValues();
            return el;
        }

        /**
         * 
         */
        public boolean drag(MouseEvent e, Point pos, final int ml) {
        	MouseDragResult result = SGFigureElementShape.this.getMouseDragResult(e);
        	final int diffX = result.dx;
        	final int diffY = result.dy;
            pos.setLocation(pos.getX() + diffX, pos.getY() + diffY);

            if (ml == START) {
                this.setStartX(this.getStartX() + diffX);
                this.setStartY(this.getStartY() + diffY);
            } else if (ml == END) {
                this.setEndX(this.getEndX() + diffX);
                this.setEndY(this.getEndY() + diffY);
            }

            return true;
        }

        /**
         * 
         */
        public void translate(final float dx, final float dy) {
        	this.translateSub(dx, dy);
        }
        
        public void translateSub(final float dx, final float dy) {
            this.setStartX(this.getStartX() + dx);
            this.setStartY(this.getStartY() + dy);
            this.setEndX(this.getEndX() + dx);
            this.setEndY(this.getEndY() + dy);

            this.setAxisValuesWithShape();
        }

        /**
         * 
         */
        public String getName() {
            return NAME;
        }

        /**
         * 
         */
        public String getClassDescription() {
            return "";
        }

        /**
         * Returns the description of an instance.
         * @return
         *       the description of an instance
         */
        public String getInstanceDescription() {
            SGIFigureElementAxis aElement = SGFigureElementShape.this.mAxisElement;
            String xAxis = aElement.getLocationName(this.getXAxis());
            String yAxis = aElement.getLocationName(this.getYAxis());

            StringBuffer sb = new StringBuffer();
            sb.append(this.getID());
            sb.append(": ");
            sb.append(this.getName());
            sb.append(", AxisX=");
            sb.append(xAxis.toString());
            sb.append(", AxisY=");
            sb.append(yAxis.toString());
            sb.append(", START=(");
            sb.append(this.mStartXValue);
            sb.append(", ");
            sb.append(this.mStartYValue);
            sb.append("), END=(");
            sb.append(this.mEndXValue);
            sb.append(", ");
            sb.append(this.mEndYValue);
            sb.append(")");
            return sb.toString();
        }

        /**
         * 
         */
        public ArrayList getChildNodes() {
            return new ArrayList();
        }

        /**
         * 
         */
        public SGProperties getMement() {
            return this.getProperties();
        }

        /**
         * 
         */
        public boolean setMement(SGProperties p) {
            return this.setProperties(p);
        }

        /**
         * 
         */
        public SGProperties getProperties() {
            SGProperties p = new ArrowShapeProperties();
            if (this.getProperties(p) == false) {
                return null;
            }
            return p;
        }

        /**
         * 
         */
        public boolean getProperties(SGProperties p) {
            if ((p instanceof ArrowShapeProperties) == false) {
                return false;
            }
            if (super.getProperties(p) == false) {
                return false;
            }
            ArrowShapeProperties rp = (ArrowShapeProperties) p;
            SGFigureElementShape sElement = this.getShapeElement();
            SGIFigureElementAxis aElement = sElement.getAxisElement();
            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();
            rp.setXAxisLocation(aElement.getLocationInPlane(xAxis));
            rp.setYAxisLocation(aElement.getLocationInPlane(yAxis));
            rp.setStartXValue(this.mStartXValue);
            rp.setStartYValue(this.mStartYValue);
            rp.setEndXValue(this.mEndXValue);
            rp.setEndYValue(this.mEndYValue);
            rp.setAnchored(this.mIsAnchored);
            return true;
        }

        /**
         * 
         */
        public boolean setProperties(SGProperties p) {
            if ((p instanceof ArrowShapeProperties) == false) {
                return false;
            }
            if (super.setProperties(p) == false) {
                return false;
            }
            ArrowShapeProperties rp = (ArrowShapeProperties) p;

            final Double x1 = rp.getStartXValue();
            if (x1 == null)
                return false;
            this.mStartXValue = x1.doubleValue();

            final Double y1 = rp.getStartYValue();
            if (y1 == null)
                return false;
            this.mStartYValue = y1.doubleValue();

            final Double x2 = rp.getEndXValue();
            if (x2 == null)
                return false;
            this.mEndXValue = x2.doubleValue();

            final Double y2 = rp.getEndYValue();
            if (y2 == null)
                return false;
            this.mEndYValue = y2.doubleValue();
            
            final Boolean anchored = rp.getAnchored();
            if (anchored == null)
                return false;
            this.mIsAnchored = anchored.booleanValue();

            // System.out.println(x1+" "+x2);
            // System.out.println(y1+" "+y2);
            // System.out.println();

            return true;
        }

        /**
         * 
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * 
         */
        public boolean prepare() {
            this.mTemporaryProperties = this.getProperties();
            // this.dump();
            return true;
        }

        /**
         * 
         */
        public boolean commit() {
            SGProperties pTemp = this.mTemporaryProperties;
            SGProperties pPresent = this.getProperties();
            if (pTemp.equals(pPresent) == false) {
                this.mShape.setChanged(true);
            }

            this.setShapeWithAxesValues();

            SGFigureElementShape sh = this.getShapeElement();
            sh.notifyChangeOnCommit();
            sh.repaint();

            return true;
        }

        /**
         * 
         */
        public boolean cancel() {
            if (this.setProperties(this.mTemporaryProperties) == false) {
                return false;
            }
            this.mTemporaryProperties = null;

            this.setShapeWithAxesValues();
            SGFigureElementShape sh = this.getShapeElement();
            sh.notifyChangeOnCancel();
            sh.repaint();

            return true;
        }

        /**
         * 
         */
        public boolean preview() {
            this.setShapeWithAxesValues();
            SGFigureElementShape sh = this.getShapeElement();
            sh.notifyChange();
            sh.repaint();
            return true;
        }

        /**
         * Returns the property dialog.
         * 
         * @return
         *        a property dialog
         */
        public SGPropertyDialog getPropertyDialog() {
            return getShapeDialog(this);
        }

        public boolean readProperty(final Element el) {
            if (super.readProperty(el) == false) {
                return false;
            }

            String str = null;
            Number num = null;

            // start x value
            str = el.getAttribute(KEY_START_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double startXValue = num.doubleValue();
                if (this.setStartXValue(startXValue) == false) {
                    return false;
                }
            }

            // start y value
            str = el.getAttribute(KEY_START_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double startYValue = num.doubleValue();
                if (this.setStartYValue(startYValue) == false) {
                    return false;
                }
            }

            // end x value
            str = el.getAttribute(KEY_END_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double endXValue = num.doubleValue();
                if (this.setEndXValue(endXValue) == false) {
                    return false;
                }
            }

            // end y value
            str = el.getAttribute(KEY_END_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double endYValue = num.doubleValue();
                if (this.setEndYValue(endYValue) == false) {
                    return false;
                }
            }
            
            // anchored
            str = el.getAttribute(KEY_ANCHORED);
            if (str.length() != 0) {
                final Boolean b = SGUtilityText.getBoolean(str);
                if (b == null) {
                    return false;
                }
                this.setAnchored(b.booleanValue());
                this.setShapeWithAxesValues();
            }

            return true;
        }

        /**
         * Sets the properties.
         * 
         * @param map
         *           a map of properties
         * @param iResult
         *           the result of setting properties
         * @return updated result
         */
        public SGPropertyResults setProperties(SGPropertyMap map, 
        		SGPropertyResults iResult) {
        	
            SGPropertyResults result = (SGPropertyResults) iResult.clone();

            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                String value = map.getValueString(key);
                
                if (COM_ARROW_START_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_ARROW_START_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_START_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setStartXValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_START_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_START_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_START_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_ARROW_START_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_START_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setStartYValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_START_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_START_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_END_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_ARROW_END_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_END_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setEndXValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_END_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_END_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_END_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_ARROW_END_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_END_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setEndYValue(num.doubleValue()) == false) {
                        result.putResult(COM_ARROW_END_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_END_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_LINE_WIDTH.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_ARROW_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineWidth(num.floatValue(), unit
                            .toString()) == false) {
                        result.putResult(COM_ARROW_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_LINE_TYPE.equalsIgnoreCase(key)) {
                    final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                    if (type == null) {
                        result.putResult(COM_ARROW_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineType(type) == false) {
                        result.putResult(COM_ARROW_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_LINE_TYPE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_HEAD_SIZE.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_ARROW_HEAD_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setHeadSize(num.floatValue(), unit
                            .toString()) == false) {
                        result.putResult(COM_ARROW_HEAD_SIZE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_HEAD_SIZE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.getColor(value);
                    if (cl != null) {
                        if (this.setColor(cl) == false) {
                            result.putResult(COM_ARROW_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                            continue;
                        }
                    } else {
                    	cl = SGUtilityText.parseColor(value);
    					if (cl == null) {
    						result.putResult(COM_ARROW_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
    					if (this.setColor(cl) == false) {
    						result.putResult(COM_ARROW_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
                    }
                    result.putResult(COM_ARROW_COLOR, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_START_TYPE.equalsIgnoreCase(key)) {
                    final Integer type = SGDrawingElementArrow.getArrowHeadTypeFromName(value);
                    if (type == null) {
                        result.putResult(COM_ARROW_START_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setStartHeadType(type) == false) {
                        result.putResult(COM_ARROW_START_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_START_TYPE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_END_TYPE.equalsIgnoreCase(key)) {
                    final Integer type = SGDrawingElementArrow.getArrowHeadTypeFromName(value);
                    if (type == null) {
                        result.putResult(COM_ARROW_END_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setEndHeadType(type) == false) {
                        result.putResult(COM_ARROW_END_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_END_TYPE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_HEAD_OPEN_ANGLE.equalsIgnoreCase(key)) {
                    Float openAngle = SGUtilityText.getFloat(value);
                    if (openAngle == null) {
                        result.putResult(COM_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setHeadAngle(openAngle, null) == false) {
                        result.putResult(COM_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_HEAD_OPEN_ANGLE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_HEAD_CLOSE_ANGLE.equalsIgnoreCase(key)) {
                    Float closeAngle = SGUtilityText.getFloat(value);
                    if (closeAngle == null) {
                        result.putResult(COM_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setHeadAngle(null, closeAngle) == false) {
                        result.putResult(COM_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_HEAD_CLOSE_ANGLE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_HEAD_ANGLE.equalsIgnoreCase(key)) {
                	float[] angles = SGUtilityText.getFloatArray(value);
                	if (angles == null) {
                        result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	if (angles.length != 2) {
                        result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setHeadAngle(angles[0], angles[1]) == false) {
                        result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_HEAD_ANGLE, SGPropertyResults.SUCCEEDED);
                } else if (COM_ARROW_ANCHORED.equalsIgnoreCase(key)) {
                    Boolean anchored = SGUtilityText.getBoolean(value);
                    if (anchored == null) {
                        result.putResult(COM_ARROW_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAnchored(anchored) == false) {
                        result.putResult(COM_ARROW_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_ARROW_ANCHORED, SGPropertyResults.SUCCEEDED);
                }
            }            

            return result;
        }
        
        private float getExportHeadOpenAngle() {
        	return SGUtility.getExportValue(this.getHeadOpenAngle(), ARROW_HEAD_ANGLE_MINIMAL_ORDER);
        }

        private float getExportHeadCloseAngle() {
        	return SGUtility.getExportValue(this.getHeadCloseAngle(), ARROW_HEAD_ANGLE_MINIMAL_ORDER);
        }

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
    	@Override
        public SGPropertyMap getCommandPropertyMap() {
        	SGPropertyMap map = new SGPropertyMap();
        	
        	// open angles
            StringBuffer sbAngle = new StringBuffer();
            sbAngle.append('(');
            sbAngle.append(this.getExportHeadOpenAngle());
            sbAngle.append(',');
            sbAngle.append(this.getExportHeadCloseAngle());
            sbAngle.append(')');
            SGPropertyUtility.addProperty(map, COM_ARROW_HEAD_ANGLE, sbAngle.toString());

            // properties for arrow shape
            this.addProperties(map, COM_ARROW_LINE_WIDTH, COM_ARROW_LINE_TYPE, COM_ARROW_HEAD_SIZE, 
            		COM_ARROW_COLOR, COM_ARROW_START_TYPE, COM_ARROW_END_TYPE);

            // properties related to the location
            this.addProperties(map, COM_ARROW_START_X, COM_ARROW_START_Y, COM_ARROW_END_X, 
            		COM_ARROW_END_Y, COM_ARROW_ANCHORED);
            
			return map;
		}
    	
    	@Override
    	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	
        	// open angles
        	SGPropertyUtility.addProperty(map, KEY_HEAD_OPEN_ANGLE, this.getExportHeadOpenAngle());
        	SGPropertyUtility.addProperty(map, KEY_HEAD_CLOSE_ANGLE, this.getExportHeadCloseAngle());
        	
            // properties related to the location
        	this.addProperties(map, KEY_START_X_VALUE, KEY_START_Y_VALUE, KEY_END_X_VALUE, 
        			KEY_END_Y_VALUE, KEY_ANCHORED);
    		return map;
    	}
    	
    	private void addProperties(SGPropertyMap map, String startXKey, String startYKey,
    			String endXKey, String endYKey, String anchorKey) {
            SGPropertyUtility.addProperty(map, startXKey, this.getStartXValue());
            SGPropertyUtility.addProperty(map, startYKey, this.getStartYValue());
            SGPropertyUtility.addProperty(map, endXKey, this.getEndXValue());
            SGPropertyUtility.addProperty(map, endYKey, this.getEndYValue());
            SGPropertyUtility.addProperty(map, anchorKey, this.isAnchored());
    	}

		@Override
		public String getShapeType() {
			return SHAPE_TYPE_ARROW;
		}

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
    }

    /**
     * Properties of rectangular shape.
     */
    private static class ArrowShapeProperties extends ArrowProperties implements
            PropertyWithAxes {
        private double mStartXValue = 0.0;

        private double mStartYValue = 0.0;

        private double mEndXValue = 0.0;

        private double mEndYValue = 0.0;

        private int mXAxisLocation = -1;

        private int mYAxisLocation = -1;
        
        private boolean mAnchored = false;

        /**
         * 
         */
        public ArrowShapeProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ArrowShapeProperties) == false) {
                return false;
            }

            if (super.equals(obj) == false)
                return false;

            ArrowShapeProperties p = (ArrowShapeProperties) obj;

            if (p.mStartXValue != this.mStartXValue)
                return false;
            if (p.mStartYValue != this.mStartYValue)
                return false;
            if (p.mEndXValue != this.mEndXValue)
                return false;
            if (p.mEndYValue != this.mEndYValue)
                return false;
            if (this.mXAxisLocation != p.mXAxisLocation)
                return false;
            if (this.mYAxisLocation != p.mYAxisLocation)
                return false;
            if (this.mAnchored != p.mAnchored)
                return false;

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

        private boolean setXAxisLocation(final int location) {
            this.mXAxisLocation = location;
            return true;
        }

        private boolean setYAxisLocation(final int location) {
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
     * @param frame
     *            the dialog owner
     * @return true if succeeded
     */
    public boolean setDialogOwner(final Frame frame) {
        if (super.setDialogOwner(frame) == false) {
            return false;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (mShapeDialogMap != null) {
                    if (mShapeDialogMap.get(Rect.NAME) == null) {
                        mShapeDialogMap.put(Rect.NAME, new SGRectangularShapeDialog(
                                mDialogOwner, true));
                    }
                    if (mShapeDialogMap.get(Arrow.NAME) == null) {
                        mShapeDialogMap.put(Arrow.NAME, new SGArrowDialog(
                                mDialogOwner, true));
                    }
                }
            }
        });
        return true;
    }

    /**
     * Sets the properties of child object.
     * 
     * @param id
     *           the ID of child object
     * @param map
     *           a map properties
     * @return the result of setting properties
     */
    public SGPropertyResults setChildProperties(final int id, SGPropertyMap map) {
        
        SGIChildObject child = this.getVisibleChild(id);

        // add a child object if it does not exist
        boolean shapeTypeFlag = false;
        if (child == null) {        	
            String sType = null;
            String sAxisX = null;
            String sAxisY = null;
            Iterator<String> itr = map.getKeyIterator();
            while (itr.hasNext()) {
                String key = itr.next();
                if (COM_SHAPE_TYPE.equalsIgnoreCase(key)) {
                	sType = map.getValueString(key);
                } else if (COM_SHAPE_AXIS_X.equalsIgnoreCase(key)) {
                	sAxisX = map.getValueString(key);
                } else if (COM_SHAPE_AXIS_Y.equalsIgnoreCase(key)) {
                	sAxisY = map.getValueString(key);
                }
            }
            if (sType == null) {
                return null;
            }
            final int type;
            if (SHAPE_TYPE_RECTANGLE.equalsIgnoreCase(sType)) {
            	type = RECTANGLE;
            } else if (SHAPE_TYPE_ELLIPSE.equalsIgnoreCase(sType)) {
            	type = ELLIPSE;
            } else if (SHAPE_TYPE_ARROW.equalsIgnoreCase(sType)) {
            	type = ARROW;
            } else if (SHAPE_TYPE_LINE.equalsIgnoreCase(sType)) {
            	type = LINE;
            } else {
            	return null;
            }
            
            // get axes
            String xAxisName = (sAxisX != null) ? sAxisX : DEFAULT_SHAPE_HORIZONTAL_AXIS;
            String yAxisName = (sAxisY != null) ? sAxisY : DEFAULT_SHAPE_VERTICAL_AXIS;
            SGAxis xAxis = this.mAxisElement.getAxis(xAxisName);
            SGAxis yAxis = this.mAxisElement.getAxis(yAxisName);
            if (xAxis == null || yAxis == null) {
                return null;
            }

            itr = map.getKeyIterator();
            switch (type) {
			case SGIFigureElementShape.RECTANGLE:
			case SGIFigureElementShape.ELLIPSE:
				String sLeft = null;
				String sRight = null;
				String sBottom = null;
				String sTop = null;
	            while (itr.hasNext()) {
	                Object keyObj = itr.next();
	                String key = keyObj.toString();
	                if (COM_RECTANGLE_LEFT_X.equalsIgnoreCase(key)) {
	                	sLeft = map.getValueString(key);
	                } else if (COM_RECTANGLE_RIGHT_X.equalsIgnoreCase(key)) {
	                	sRight = map.getValueString(key);
	                } else if (COM_RECTANGLE_BOTTOM_Y.equalsIgnoreCase(key)) {
	                	sBottom = map.getValueString(key);
	                } else if (COM_RECTANGLE_TOP_Y.equalsIgnoreCase(key)) {
	                	sTop = map.getValueString(key);
	                }
	            }
                if (sLeft == null) {
                    return null;
                }
                if (sRight == null) {
                    return null;
                }
                if (sTop == null) {
                    return null;
                }
                if (sBottom == null) {
                    return null;
                }
                Double left = SGUtilityText.getDouble(sLeft);
                Double right = SGUtilityText.getDouble(sRight);
                Double top = SGUtilityText.getDouble(sTop);
                Double bottom = SGUtilityText.getDouble(sBottom);
                if (left == null) {
                    return null;
                }
                if (right == null) {
                    return null;
                }
                if (top == null) {
                    return null;
                }
                if (bottom == null) {
                    return null;
                }
                if (SGUtility.isValidPropertyValue(left.doubleValue()) == false) {
                	return null;
                }
                if (SGUtility.isValidPropertyValue(right.doubleValue()) == false) {
                	return null;
                }
                if (SGUtility.isValidPropertyValue(top.doubleValue()) == false) {
                	return null;
                }
                if (SGUtility.isValidPropertyValue(bottom.doubleValue()) == false) {
                	return null;
                }
                
                Rect rect = null;
                if (type == SGIFigureElementShape.RECTANGLE) {
                	rect = new Rect();
                } else {
                	rect = new Ellipse();
                }
                if (this.addRectangularShape(id, rect, left, right, top,
						bottom, xAxis, yAxis) == false) {
					return null;
				}
				break;
			case SGIFigureElementShape.ARROW:
			case SGIFigureElementShape.LINE:
				String sStartX = null;
				String sStartY = null;
				String sEndX = null;
				String sEndY = null;
	            while (itr.hasNext()) {
	                Object keyObj = itr.next();
	                String key = keyObj.toString();
	                if (COM_ARROW_START_X.equalsIgnoreCase(key)) {
	                	sStartX = map.getValueString(key);
	                } else if (COM_ARROW_START_Y.equalsIgnoreCase(key)) {
	                	sStartY = map.getValueString(key);
	                } else if (COM_ARROW_END_X.equalsIgnoreCase(key)) {
	                	sEndX = map.getValueString(key);
	                } else if (COM_ARROW_END_Y.equalsIgnoreCase(key)) {
	                	sEndY = map.getValueString(key);
	                }
	            }
                if (sStartX == null) {
                    return null;
                }
                if (sStartY == null) {
                    return null;
                }
                if (sEndX == null) {
                    return null;
                }
                if (sEndY == null) {
                    return null;
                }
                Double startX = SGUtilityText.getDouble(sStartX);
                Double startY = SGUtilityText.getDouble(sStartY);
                Double endX = SGUtilityText.getDouble(sEndX);
                Double endY = SGUtilityText.getDouble(sEndY);
                if (startX == null) {
                    return null;
                }
                if (startY == null) {
                    return null;
                }
                if (endX == null) {
                    return null;
                }
                if (endY == null) {
                    return null;
                }
                if (SGUtility.isValidPropertyValue(startX.doubleValue()) == false) {
                	return null;
                }
                if (SGUtility.isValidPropertyValue(startY.doubleValue()) == false) {
                	return null;
                }
                if (SGUtility.isValidPropertyValue(endX.doubleValue()) == false) {
                	return null;
                }
                if (SGUtility.isValidPropertyValue(endY.doubleValue()) == false) {
                	return null;
                }
                
                Arrow arrow = new Arrow();
                if (type == SGIFigureElementShape.ARROW) {
                	arrow.setStartHeadType(DEFAULT_SHAPE_ARROW_START_HEAD_TYPE);
                	arrow.setEndHeadType(DEFAULT_SHAPE_ARROW_END_HEAD_TYPE);
                } else {
                	arrow.setStartHeadType(SGIArrowConstants.SYMBOL_TYPE_VOID);
                	arrow.setEndHeadType(SGIArrowConstants.SYMBOL_TYPE_VOID);
                }
                if (this.addArrowShape(id, arrow, startX, startY, endX, endY,
						xAxis, yAxis) == false) {
					return null;
				}
				break;
			default:
                return null;
			}

            child = this.getVisibleChild(id);
            shapeTypeFlag = true;
        }

        // set properties to the child object
        SGPropertyResults result = child.setProperties(map);
        if (shapeTypeFlag) {
            result.putResult(COM_SHAPE_TYPE, SGPropertyResults.SUCCEEDED);
        }

        return result;
    }
}
