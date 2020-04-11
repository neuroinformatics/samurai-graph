package jp.riken.brain.ni.samuraigraph.figure;

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGICopiable;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIMovable;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGIVisible;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragInput;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Period;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An object in the figure.
 */
public abstract class SGFigureElement implements SGIFigureElement {

    private int mMode = MODE_DISPLAY;

    public void setMode(final int mode) {
        this.mMode = mode;
    }

    public int getMode() {
        return this.mMode;
    }

    /**
     * The list of "visible" and "selectable" child objects.
     */
    protected List<SGIChildObject> mChildList = new ArrayList<SGIChildObject>();

    private JComponent mComponent = null;

    public JComponent getComponent() {
        return this.mComponent;
    }

    public void setComponent(JComponent com) {
        this.mComponent = com;
    }

    public void repaint() {
        // System.out.println("FigureElement");
        if (this.mComponent != null) {
            this.mComponent.repaint();
        }
    }

    /**
     * X-coordinate of the rectangle of the graph area.
     */
    protected float mGraphRectX;

    /**
     * Y-coordinate of the rectangle of the graph area.
     */
    protected float mGraphRectY;

    /**
     * Width of the rectangle of the graph area.
     */
    protected float mGraphRectWidth;

    /**
     * Height of the rectangle of the graph area.
     */
    protected float mGraphRectHeight;

    /**
     * Magnification.
     */
    protected float mMagnification = 1.0f;

    /**
     * 
     */
    protected Rectangle2D mViewBounds = null;

    /**
     * List of the SGData objects.
     */
    protected List<SGData> mDataList = new ArrayList<SGData>();

    /**
     * List of action listeners.
     */
    protected List mActionListenerList = new ArrayList();

    /**
     * 
     */
    private Cursor mCursor = null;

    /**
     * Owner of property dialogs.
     */
    protected Frame mDialogOwner = null;

    protected Point mPressedPoint = null;
    
    protected boolean mDraggableFlag = false;

    /**
     * Default constructor.
     */
    public SGFigureElement() {
        super();
    }

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
    	
        this.mComponent = null;

        this.mActionListenerList.clear();
        this.mActionListenerList = null;

        this.mDataList.clear();
        this.mDataList = null;

        List<SGIChildObject> cList = this.mChildList;
        for (int ii = 0; ii < cList.size(); ii++) {
            Object obj = cList.get(ii);
            if (obj instanceof SGIDisposable) {
                SGIDisposable d = (SGIDisposable) obj;
                d.dispose();
            }
        }
        this.mChildList.clear();
        this.mChildList = null;

        this.mUndoManager.dispose();
        this.mUndoManager = null;

        this.mDialogOwner = null;
        this.mViewBounds = null;
        this.mPressedPoint = null;
        this.mCursor = null;
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
     * Returns the ID for a new child object.
     * 
     * @return the ID for a new child object
     */
    protected int assignChildId() {
    	List<Integer> idList = new ArrayList<Integer>();
    	List<SGIChildObject> cList = this.getVisibleChildList();
    	for (int ii = 0; ii < cList.size(); ii++) {
    		SGIChildObject c = cList.get(ii);
    		idList.add(c.getID());
    	}
    	final int id = SGUtility.assignIdNumber(idList);
    	return id;
    }

    /**
     * Add a data object.
     * @param data
     *          a data object
     * @param name
     *          the name of the data object
     * @return
     *          true if succeeded
     */
    public boolean addData(final SGData data, final String name) {
	
        if (data == null) {
            throw new Error("data == null");
        }

        // add to the list
        this.mDataList.add(data);
        return true;
    }
    
    /**
     * Add a data object with given name.
     * 
     * @param data
     *            data to add
     * @param name
     *            name of data
     * @param infoMap
     *            the information map of data
     * @return true if succeeded
     */
    public boolean addData(final SGData data, final String name, final Map<String, Object> infoMap) {
        return this.addData(data, name);
    }

    /**
     * Add a data object with given name and given ID.
     * 
     * @param data
     *            data to add
     * @param name
     *            name of data
     * @param id
     *            the ID to set
     * @param infoMap
     *            the information map of data
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData data, final String name, final int id, final Map<String, Object> infoMap) {
        return this.addData(data, name, infoMap);
    }

    /**
     * Adds data objects with given name.
     * 
     * @param data
     *            an array of data objects
     * @param name
     *            an array of names of data objects
     * @param infoMap
     *            the information map of data
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData[] data, final String[] name,
            final Map<String, Object> infoMap) {
        if (data.length != name.length) {
            return false;
        }
        for (int ii = 0; ii < data.length; ii++) {
            if (this.addData(data[ii], name[ii], infoMap) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds data objects with given name and given ID.
     * 
     * @param data
     *            an array of data objects
     * @param name
     *            an array of names of data objects
     * @param id
     *            the ID of data objects
     * @param infoMap
     *            the information map of data
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData[] data, final String[] name, final int[] id,
            final Map<String, Object> infoMap) {
        if (data.length != name.length) {
            return false;
        }
        if (data.length != id.length) {
            return false;
        }
        for (int ii = 0; ii < data.length; ii++) {
            if (this.addData(data[ii], name[ii], id[ii], infoMap) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds a child object.
     * 
     * @param el
     *          a child object to add
     * @return true if succeeded
     */
    protected boolean addToList(final SGIChildObject el) {
        
    	List<Integer> idList = new ArrayList<Integer>();
    	for (int ii = 0; ii < this.mChildList.size(); ii++) {
    		SGIChildObject c = this.mChildList.get(ii);
    		idList.add(c.getID());
    	}
    	final int id = SGUtility.assignIdNumber(idList);
    	el.setID(id);

        this.mChildList.add(el);

//        el.setID(this.mIDCounter);
//        
//        // update the ID counter
//        this.mIDCounter++;
        
        return true;
    }

    /**
     * Adds a child object of given ID.
     * 
     * @param id
     *          the object ID to set
     * @param el
     *          a child object to add
     * @return true if succeeded
     */
    protected boolean addToList(final int id, final SGIChildObject el) {
        if (id <= 0) {
            throw new IllegalArgumentException("id <= 0");
        }
        
        // check overlapping
        List<SGIChildObject> cList = this.getVisibleChildList();
        for (int ii = 0; ii < cList.size(); ii++) {
            SGIChildObject c = cList.get(ii);
            final int cid = c.getID();
            if (cid == id) {
                return false;
            }
        }
        
        this.mChildList.add(el);
        el.setID(id);
        
//        // update the ID counter
//        int maxId = id;
//        for (int ii = 0; ii < cList.size(); ii++) {
//            SGIChildObject c = cList.get(ii);
//            final int cid = c.getID();
//            if (cid > maxId) {
//                maxId = cid;
//            }
//        }
//        this.mIDCounter = maxId + 1;
        
        return true;
    }

    /**
     * Returns the visible child object of a given ID.
     * 
     * @param id
     *          the ID of a child object
     * @return a child object of a given ID if it exists and visible
     */
    protected SGIChildObject getVisibleChild(final int id) {
        List<SGIChildObject> cList = this.getVisibleChildList();
        for (int ii = 0; ii < cList.size(); ii++) {
            SGIChildObject el = cList.get(ii);
            if (el.getID() == id) {
                return el;
            }
        }
        return null;
    }

    /**
     * Remove a child object.
     * 
     * @param obj
     *            An object to be removed.
     * @return
     *            true if this object exists
     */
    protected boolean removeChild(final Object obj) {
        if (obj instanceof SGIDisposable) {
            SGIDisposable d = (SGIDisposable) obj;
            d.dispose();
        }
        return this.mChildList.remove(obj);
    }

    /**
     * Remove a data object.
     * 
     * @param data
     *             data to be removed
     * @return
     *             true if succeeded
     */
    public boolean removeData(final SGData data) {
        List dList = this.mDataList;
        for (int ii = dList.size() - 1; ii >= 0; ii--) {
            final SGData d = (SGData) dList.get(ii);
            if (d.equals(data)) {
                dList.remove(ii);
            }
        }
        return true;
    }

    /**
     * Returns a list of useless data objects in this figure element.
     * By default, this method returns an empty list.
     * 
     * @return a list of useless data objects in this figure element
     */
    public List<SGData> getUselessDataList() {
		List<SGData> dataList = new ArrayList<SGData>();
		return dataList;
    }
    
    /**
     * Returns a list of data.
     */
    public List<SGData> getDataList() {
        return new ArrayList<SGData>(this.mDataList);
    }

    /**
     * 
     */
    public boolean getMarginAroundGraphRect(final SGTuple2f topAndBottom,
            final SGTuple2f leftAndRight) {

        if (topAndBottom == null || leftAndRight == null) {
            return false;
        }

        topAndBottom.clear();
        leftAndRight.clear();

        return true;
    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        this.setGraphRectLocation(x, y);
        this.setGraphRectSize(width, height);
        return true;
    }

    /**
     * 
     */
    public boolean setGraphRect(Rectangle2D rect) {
        return this.setGraphRect((float) rect.getX(), (float) rect.getY(),
                (float) rect.getWidth(), (float) rect.getHeight());
    }

    /**
     * 
     */
    public boolean setGraphRectLocation(final float x, final float y) {
        this.mGraphRectX = x;
        this.mGraphRectY = y;
        return true;
    }

    /**
     * 
     */
    public boolean setGraphRectSize(final float width, final float height) {
        if (width < 0.0 || height < 0.0) {
            throw new IllegalArgumentException("width<0.0 || height<0.0");
        }
        this.mGraphRectWidth = width;
        this.mGraphRectHeight = height;
        return true;
    }

    /**
     * 
     */
    public Rectangle2D getGraphRect() {
        final Rectangle2D rect = new Rectangle2D.Float(this.mGraphRectX,
                this.mGraphRectY, this.mGraphRectWidth, this.mGraphRectHeight);

        return rect;
    }

    /**
     * 
     */
    public boolean isInsideGraphArea(final SGTuple2f pos) {
        final float x = pos.x;
        final float y = pos.y;

        final float gx = this.mGraphRectX;
        final float gy = this.mGraphRectY;
        final float gw = this.mGraphRectWidth;
        final float gh = this.mGraphRectHeight;

        if (x < gx || x > gx + gw) {
            return false;
        }

        if (y < gy || y > gy + gh) {
            return false;
        }

        return true;
    }

    /**
     * 
     */
    public boolean isInsideGraphArea(final Point2D pos) {
        return this.isInsideGraphArea((int) pos.getX(), (int) pos.getY());
    }

    /**
     * 
     */
    public boolean isInsideGraphArea(final int x, final int y) {
        final SGTuple2f pos = new SGTuple2f(x, y);
        return this.isInsideGraphArea(pos);
    }

    /**
     * Sets the dialog owner this figure element.
     * 
     * @param frame
     *            the dialog owner
     * @return true if succeeded
     */
    public boolean setDialogOwner(final Frame frame) {
        this.mDialogOwner = frame;
        return true;
    }

    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public float getMagnification() {
        return this.mMagnification;
    }

    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (mag <= 0.0) {
            throw new IllegalArgumentException("mag <= 0.0");
        }
        this.mMagnification = mag;
        
        // set to the child objects
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGIChildObject obj = (SGIChildObject) this.mChildList.get(ii);
            if (obj.setMagnification(mag) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     */
    public Cursor getFigureElementCursor() {
        return this.mCursor;
    }

    /**
     * 
     * @param type
     */
    protected void setMouseCursor(int type) {
        this.mCursor = Cursor.getPredefinedCursor(type);
        this.notifyChangeCursor();
    }

    /**
     * 
     * @param cur
     */
    protected void setMouseCursor(Cursor cur) {
        this.mCursor = cur;
        this.notifyChangeCursor();
    }

    /**
     * Notify that the mouse cursor is changed.
     */
    public void notifyChangeCursor() {
        this.notifyToListener(SGIFigureElement.NOTIFY_CHANGE_CURSOR);
    }

//    /**
//     * Zoom in/out this component.<BR>
//     */
//    public boolean zoom(final float ratio) {
//        this.setMagnification(ratio);
//        return true;
//    }

    // /**
    // * Synchronize this component to the other component. <BR>
    // * @param element the SGFigureElement object whose property has changed.
    // * @return true:succeeded, false:failed
    // */
    // public abstract boolean synchronize( SGIFigureElement element );
    //
    //
    // /**
    // *
    // */
    // public abstract boolean synchronizeArgument( SGIFigureElement element );

    /**
     * 
     */
    public boolean setViewBounds(final Rectangle2D rect) {
        this.mViewBounds = rect;
        return true;
    }

    /**
     * 
     * @return
     */
    public Rectangle2D getViewBounds() {
        return this.mViewBounds;
    }

    // /**
    // *
    // */
    // public void paintComponent( final Graphics g )
    // {
    // // super.paintComponent(g);
    // this.paintGraphics( g, true );
    // }

    /**
     * 
     * @param el
     * @param data
     * @return
     */
    public boolean createDataObject(Element el, SGData data, final boolean readDataProperty) {
        if (el == null || data == null) {
            throw new IllegalArgumentException();
        }
        this.mDataList.add(data);
        return true;
    }

    /**
     * 
     * @param el
     * @param mod
     * @return
     */
    protected boolean updateFocusedObjectsList(final SGISelectable el,
            final int mod) {
        final List<SGISelectable> fList = this.getFocusedObjectsList();

        // Neither CTRL key nor SHIFT key is pressed.
        if (((mod & InputEvent.CTRL_MASK) == 0)
                && ((mod & InputEvent.SHIFT_MASK) == 0)) {
            // If the list already contains this object.
            if (fList.contains(el)) {
                // do nothing
            } else {
                // set all objects unselected
                // this.clearFocusedObjects();
                this.notifyToListener(SGIFigureElement.CLEAR_FOCUSED_OBJECTS);

                // set given object selected
                el.setSelected(true);
            }

        }
        // otherwise
        else {
            // If the list already contains this object.
            el.setSelected(!el.isSelected());
        }

        return true;
    }

    /**
     * Update the focused object with a mouse event.
     * 
     * @param el
     *            a selectable object
     * @param e
     *            a mouse event
     * @return
     */
    public boolean updateFocusedObjectsList(final SGISelectable el,
            final MouseEvent e) {
        final int mod = e.getModifiers();
        return this.updateFocusedObjectsList(el, mod);
    }

    /**
     * 
     */
    public void translateFocusedObjects(final int dx, final int dy) {
        List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            Object obj = list.get(ii);
            if (obj instanceof SGIMovable) {
                SGIMovable m = (SGIMovable) obj;
                m.translate(dx, dy);
            }
        }
    }

    @Override
    public boolean clearFocusedObjects() {
        List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGISelectable s = (SGISelectable) list.get(ii);
            s.setSelected(false);
        }
        return true;
    }

    /**
     * Clear focused objects.
     * By default, this method clear all focused objects
     * if an origin is not equal to this.
     * @param ori
     *            an origin of this clearance
     * @return
     *           true if succeeded
     */
    public boolean clearFocusedObjects(SGIFigureElement ori) {
	if (!this.getClass().equals(ori.getClass())) {
	    return this.clearFocusedObjects();
	} else {
	    // do nothing
	    return true;
	}
    }
    
    /**
     * 
     */
    protected boolean hideSelectedData() {

        List<SGISelectable> list = this.getFocusedObjectsList();
        if (list.size() == 0) {
            return true;
        }

        for (int ii = 0; ii < list.size(); ii++) {
            Object obj = list.get(ii);
            if (obj instanceof SGElementGroupSet) {
                final SGElementGroupSet groupSet = (SGElementGroupSet) list.get(ii);
                groupSet.setVisible(false);
            }
        }

        this.clearFocusedObjects();
//        notifyChange();	// Do not notify the change!

        this.setChanged(true);

        return true;
    }
    
    @Override
    public boolean hideData(final int[] dataIdArray) {
    	// do nothing by default
    	return true;
    }
    
    /**
     * 
     */
    public void addActionListener(final ActionListener listener) {
        for (int ii = 0; ii < this.mActionListenerList.size(); ii++) {
            final ActionListener el = (ActionListener) this.mActionListenerList
                    .get(ii);
            if (el.equals(listener)) {
                return;
            }
        }
        this.mActionListenerList.add(listener);
    }

    /**
     * 
     */
    public void removeActionListener(ActionListener listener) {
        for (int ii = this.mActionListenerList.size() - 1; ii >= 0; ii--) {
            final ActionListener el = (ActionListener) this.mActionListenerList
                    .get(ii);
            if (el.equals(listener)) {
                this.mActionListenerList.remove(listener);
            }
        }
    }

    /**
     * Notify the change to other figure elements for synchronization.
     */
    public void notifyChange() {
        this.notifyToListener(SGIFigureElement.NOTIFY_CHANGE);
    }

    /**
     * Notify the undo operation to other figure elements for synchronization.
     */
    public void notifyChangeOnUndo() {
        this.notifyToListener(SGIFigureElement.NOTIFY_CHANGE_ON_UNDO);
    }

    /**
     * Notify the change on commitment to other figure elements for synchronization.
     */
    public void notifyChangeOnCommit() {
        this.notifyToListener(SGIFigureElement.NOTIFY_CHANGE_ON_COMMIT);
    }

    public void notifyChangeOnCancel() {
        this.notifyToListener(SGIFigureElement.NOTIFY_CHANGE_ON_CANCEL);
    }

    public void setPropertiesOfSelectedObjects(SGIPropertyDialogObserver obs) {
    	this.setPropertyDialogObserverClass(obs.getClass());
        this.notifyToListener(SGIFigureElement.SHOW_PROPERTY_DIALOG_FOR_SELECTED_OBJECTS);
    	this.setPropertyDialogObserverClass(null);
    }

    public void setPropertiesOfAllVisibleObjects(SGIPropertyDialogObserver obs) {
    	this.setPropertyDialogObserverClass(obs.getClass());
        this.notifyToListener(SGIFigureElement.SHOW_PROPERTY_DIALOG_FOR_VISIBLE_OBJECTS);
    	this.setPropertyDialogObserverClass(null);
    }

    public void setPropertiesOfAllObjects(SGIPropertyDialogObserver obs) {
    	this.setPropertyDialogObserverClass(obs.getClass());
        this.notifyToListener(SGIFigureElement.SHOW_PROPERTY_DIALOG_FOR_ALL_OBJECTS);
    	this.setPropertyDialogObserverClass(null);
    }

    /**
     * Notify an event to all listeners.
     * 
     * @param command
     *            a command to notify
     */
    public void notifyToListener(final String command) {
        this.notifyToListener(command, this);
    }

    public void notifyToListener(final String command, Object source) {
        for (int ii = 0; ii < this.mActionListenerList.size(); ii++) {
            final ActionListener el = (ActionListener) this.mActionListenerList
                    .get(ii);
            el.actionPerformed(new ActionEvent(source, 0, command));
        }
    }

    /**
     * Returns the X-coordinate of the graph rectangle.
     * @return
     *         the X-coordinate of the graph rectangle
     */
    public float getGraphRectX() {
        return this.mGraphRectX;
    }

    /**
     * Returns the Y-coordinate of the graph rectangle.
     * @return
     *         the Y-coordinate of the graph rectangle
     */
    public float getGraphRectY() {
        return this.mGraphRectY;
    }

    /**
     * Returns the width of the graph rectangle.
     * @return
     *         the width of the graph rectangle
     */
    public float getGraphRectWidth() {
        return this.mGraphRectWidth;
    }

    /**
     * Returns the height of the graph rectangle.
     * @return
     *         the height of the graph rectangle
     */
    public float getGraphRectHeight() {
        return this.mGraphRectHeight;
    }

    /**
     * Calculate the coordinate of a point for a given value with a given axis
     * for given direction.
     * @param value
     *                   the value for a given axis
     * @param axis
     *                   an axis
     * @param horizontal
     *                   true if a given axis is horizontal
     */
    public float calcLocation(final double value, final SGAxis axis,
            final boolean horizontal) {

        final SGTuple2d range = axis.getRange();
        final double min = range.x;
        final double max = range.y;

        final int type = axis.getScaleType();
        final boolean invcoord = axis.isInvertCoordinates();

        // calculate the ratio in the graph rectangle
        float ratio = 0.0f;
        if (type == SGAxis.LINEAR_SCALE) {
            if (invcoord)
                ratio = (float) ((max - value) / (max - min));
            else
                ratio = (float) ((value - min) / (max - min));
        } else if (type == SGAxis.LOG_SCALE) {
            if (value <= 0.0) {
                return Float.NaN;
            }

            final double logMin = Math.log(min);
            final double logMax = Math.log(max);
            final double logValue = Math.log(value);

            if (invcoord)
                ratio = (float) ((logMax - logValue) / (logMax - logMin));
            else
                ratio = (float) ((logValue - logMin) / (logMax - logMin));
        }

        // calculate the location
        float pos = 0.0f;
        if (horizontal) {
            pos = (this.mGraphRectX + ratio * this.mGraphRectWidth);
        } else {
            pos = (this.mGraphRectY + (1.0f - ratio) * this.mGraphRectHeight);
        }

        return pos;
    }

    /**
     * Calculate the value at a given point with a given axis for given direction.
     * @param pos
     *                   the location of a point
     * @param axis
     *                   an axis
     * @param horizontal
     *                   true if a given axis is horizontal
     */
    public double calcValue(final float pos, final SGAxis axis,
            final boolean horizontal) {

        final SGTuple2d range = axis.getRange();
        final double min = range.x;
        final double max = range.y;

        if (min >= max) {
            throw new IllegalArgumentException("min>=max");
        }

        final int type = axis.getScaleType();
        final boolean invcoord = axis.isInvertCoordinates();

        // calculate the ratio in the graph rectangle
        float ratio;
        if (horizontal) {
            if (invcoord) {
            	if (this.mGraphRectWidth == 0.0f) {
            		ratio = 1.0f;
            	} else {
                    ratio = 1.0f - (pos - this.mGraphRectX) / this.mGraphRectWidth;
            	}
            } else {
            	if (this.mGraphRectWidth == 0.0f) {
            		ratio = 0.0f;
            	} else {
                    ratio = (pos - this.mGraphRectX) / this.mGraphRectWidth;
            	}
            }
        } else {
            if (invcoord) {
            	if (this.mGraphRectHeight == 0.0f) {
            		ratio = 0.0f;
            	} else {
                    ratio = (pos - this.mGraphRectY) / this.mGraphRectHeight;
            	}
            } else {
            	if (this.mGraphRectHeight == 0.0f) {
            		ratio = 1.0f;
            	} else {
                    ratio = 1.0f - (pos - this.mGraphRectY) / this.mGraphRectHeight;
            	}
            }
        }

        // calculate the value
        double value = 0.0;
        if (type == SGAxis.LINEAR_SCALE) {
            value = min + ratio * (max - min);
        } else if (type == SGAxis.LOG_SCALE) {
            if (min >= 0.0 && max >= 0.0f) {
                final double logMin = Math.log(min);
                final double logMax = Math.log(max);
                value = Math.exp(logMin + ratio * (logMax - logMin));
            } else {
                value = Double.NaN;
            }
        }

        return value;
    }

    /**
     * Shows the pop-up menu.
     * @param menu
     *             the pop-up menu
     * @param x
     *             the X-coordinate
     * @param y
     *             the Y-coordinate
     * @return
     *         true if succeeded
     */
    protected boolean showPopupMenu(JPopupMenu menu, final int x, final int y) {
        menu.show(this.getComponent(), x, y);
        return true;
    }

    /**
     * 
     */
    public abstract SGProperties getProperties();

    /**
     * initialize compatible properties for previous property file.
     * 
     * @return
     */
    public boolean initCompatibleProperty() {
        return true;
    }

    /**
     * @uml.property name="properties"
     */
    public abstract boolean setProperties(final SGProperties p);

    /**
     * 
     * @return
     */
    public SGProperties getMemento() {
        return this.getProperties();
    }

    /**
     * 
     * @param p
     * @return
     */
    public boolean setMemento(SGProperties p) {
        return this.setProperties(p);
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

    //
    protected SGUndoManager mUndoManager = new SGUndoManager(this);

    /**
     * 
     */
    public boolean initPropertiesHistory() {
        return this.mUndoManager.initPropertiesHistory();
    }

    /**
     * 
     */
    protected boolean updateHistory(ArrayList list) {
        return this.mUndoManager.updateHistory(list);
    }

    /**
     * 
     */
    public boolean undo() {
        return this.mUndoManager.undo();
    }

    /**
     * 
     */
    public boolean redo() {
        return this.mUndoManager.redo();
    }

    /**
     * 
     */
    public boolean setMementoBackward() {
        return this.mUndoManager.setMementoBackward();
    }

    /**
     * 
     */
    public boolean setMementoForward() {
        return this.mUndoManager.setMementoForward();
    }

    /**
     * 
     * @return
     */
    protected List getMementoList() {
        return this.mUndoManager.getMementoList();
    }

    /**
     * Notify the change to the root object. This method is used to update the
     * history tree.
     */
    public void notifyToRoot() {
        this.notifyToListener(SGIFigureElement.NOTIFY_CHANGE_TO_ROOT);
    }

    public void setChanged(final boolean b) {
        this.mUndoManager.setChanged(b);
    }

    /**
     * Clear changed flag of this undoable object and all child objects.
     *
     */
    public void clearChanged() {
        this.setChanged(false);
        List cList = this.getUndoableChildList();
        for (int ii = 0; ii < cList.size(); ii++) {
            SGIUndoable obj = (SGIUndoable) cList.get(ii);
            obj.clearChanged();
        }
    }
    
    /**
     * 
     * @param listAll
     * @param listVisible
     * @return
     */
    protected boolean setVisibleList(final List listAll, final List listVisible) {
        return SGUtility.setVisibleList(listAll, listVisible);
    }

    /**
     * 
     * @param el
     * @param list
     * @return
     */
    protected boolean hideObject(final SGDrawingElement el) {
        el.setVisible(false);
        notifyChange();
        this.setChanged(true);
        this.notifyToRoot();
        return true;
    }

    /**
     * Hide the selected objects.
     * @return
     *         true if selected
     */
    public boolean hideSelectedObjects() {
    	List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            SGISelectable s = (SGISelectable) list.get(ii);
            if (this.hideSelectedObject(s) == false) {
                return false;
            }
        }

        if (list.size() != 0) {
            this.setChanged(true);
//            notifyChange();	// Do not notify the change!
        }

        return true;
    }

    /**
     * 
     * @return
     */
    public abstract String getTagName();

    /**
     * 
     * @param el
     * @return
     */
    public abstract boolean writeProperty(final Element el, SGExportParameter params);

    /**
     * 
     * @param document
     * @return
     */
    protected Element createThisElement(final Document document, SGExportParameter params) {
        Element el = document.createElement(this.getTagName());

        // set common properties
        if (this.writeProperty(el, params) == false) {
            return null;
        }

        return el;
    }

    /**
     * A flag whether anchors are visible around focused objects.
     */
    protected boolean mSymbolsVisibleFlagAroundFocusedObjects = true;

    /**
     * Returns whether anchors are visible around focused objects.
     * @return
     *         true if anchors are visible
     */
    public boolean isSymbolsVisibleAroundFocusedObjects() {
        return this.mSymbolsVisibleFlagAroundFocusedObjects;
    }

    /**
     * Sets whether anchors are visible around focused objects.
     * @param b
     *          a value to set
     */
    public void setSymbolsVisibleAroundFocusedObjects(final boolean b) {
        this.mSymbolsVisibleFlagAroundFocusedObjects = b;
    }

    /**
     * A flag whether anchors are visible around all child objects.
     */
    protected boolean mSymbolsVisibleFlagAroundAllObjects = false;

    /**
     * Returns whether anchors are visible around all child objects.
     * @return
     *         true if anchors are visible
     */
    public boolean isSymbolsVisibleAroundAllObjects() {
        return this.mSymbolsVisibleFlagAroundAllObjects;
    }

    /**
     * Sets whether anchors are visible around child objects.
     * @param b
     *          a value to set
     */
    public void setSymbolsAroundAllObjectsVisible(final boolean b) {
        this.mSymbolsVisibleFlagAroundAllObjects = b;
    }

    /**
     * 
     * @param list
     * @return
     */
    protected List<SGICopiable> getCopyList(List<SGICopiable> list) {
    	List<SGICopiable> cList = new ArrayList<SGICopiable>();
        for (int ii = 0; ii < list.size(); ii++) {
            SGICopiable obj = (SGICopiable) list.get(ii);
            cList.add((SGICopiable) obj.copy());
        }
        return cList;
    }

    /**
     * 
     * @return
     */
    public List<SGISelectable> getFocusedObjectsList() {
        ArrayList<SGISelectable> list = new ArrayList<SGISelectable>();
        this.getFocusedObjectsList(list);
        return list;
    }

    protected List<SGIPropertyDialogObserver> getSelectedPropertyDialogObserverList() {
    	List<SGIPropertyDialogObserver> obsList = new ArrayList<SGIPropertyDialogObserver>();
    	List<SGISelectable> fList = this.getFocusedObjectsList();
    	for (SGISelectable f : fList) {
    		if (f instanceof SGIPropertyDialogObserver) {
        		SGIPropertyDialogObserver obs = (SGIPropertyDialogObserver) f;
        		obsList.add(obs);
    		}
    	}
    	return obsList;
    }

    protected List<SGIPropertyDialogObserver> getVisiblePropertyDialogObserverList() {
    	List<SGIPropertyDialogObserver> obsList = new ArrayList<SGIPropertyDialogObserver>();
    	List<SGIChildObject> cList = this.getVisibleChildList();
    	for (SGIChildObject c : cList) {
    		if (c instanceof SGIPropertyDialogObserver) {
        		SGIPropertyDialogObserver obs = (SGIPropertyDialogObserver) c;
        		obsList.add(obs);
    		}
    	}
    	return obsList;
    }

    /**
     * Returns the list of focused copiable objects.
     * 
     * @return a list of focused copiable objects
     */
    protected List<SGICopiable> getCopiableFocusedObjectsList() {
    	List<SGICopiable> list = new ArrayList<SGICopiable>();
    	List fList = this.getFocusedObjectsList();
        for (int ii = fList.size() - 1; ii >= 0; ii--) {
            Object obj = fList.get(ii);
            if (obj instanceof SGICopiable) {
            	SGICopiable c = (SGICopiable) obj;
                list.add(c);
            }
        }
        return list;
    }

    /**
     * Offset x-value of the location for duplicated objects.
     */
    public static final int OFFSET_DUPLICATED_OBJECT_X = 10;

    /**
     * Offset y-value of the location for duplicated objects.
     */
    public static final int OFFSET_DUPLICATED_OBJECT_Y = 10;

    /**
     * Create copies of the focused objects.
     * 
     * @return true if succeeded
     */
    public boolean duplicateFocusedObjects() {
        // do nothing by default
        return true;
    }

    /**
     * Duplicate the focused objects.
     * 
     * @return list of duplicated objects
     */
    protected List<SGICopiable> duplicateObjects() {
        // get the list of copiable focused objects
    	List<SGICopiable> list = this.getCopiableFocusedObjectsList();

        // create copies
    	List<SGICopiable> cList = this.getCopyList(list);

        // clear all focused objects
        this.clearFocusedObjects();

        return cList;
    }

    /**
     * Returns the list of copied objects.
     * 
     * @return list of copied objects
     */
    public List<SGICopiable> getCopiedObjectsList() {
        // get the list of copiable focused objects
    	List<SGICopiable> list = this.getCopiableFocusedObjectsList();

        // create copies
        return this.getCopyList(list);
    }

    /**
     * Paste the objects.
     * 
     * @param list
     *            of the objects to be pasted
     * @return true:succeeded, false:failed
     */
    public boolean paste(List<SGICopiable> list) {
        return true;
    }

    /**
     * Cut focused copiable objects.
     * 
     * @return a list of cut objects
     */
    public List<SGICopiable> cutFocusedObjects() {
    	List<SGICopiable> list = this.getCopiedObjectsList();
        this.hideSelectedObjects();
        return list;
    }

    /**
     * 
     */
    public SGProperties getDataProperties(SGData data) {
        return null;
    }

    /**
     * Add a data object with a set of properties.
     * 
     * @param data
     *             added data.
     * @param name
     *             the name set to the data
     * @param p
     *             properties set to be data.
     * @return
     *             true if succeeded
     */
    public boolean addData(final SGData data, final String name,
            final SGProperties p) {
        // call addData(SGData, String) by default
        return this.addData(data, name);
    }

	static int getPowersOfTen(final double value) {
        final int vOrder = SGUtilityNumber.getOrder(value);
        final int vPower = (int) SGUtilityNumber.getPowersOfTen(vOrder);
        return vPower;
	}

	
    /**
     * Calculate the baseline value from a given axis.
     * 
     * @param axis
     *          an axis
     * @return baseline value
     */
	protected SGAxisValue calcBaselineValue(SGAxis axis) {
        final SGAxisValue ret;
    	if (axis.getDateMode()) {
            final double min = axis.getMinDoubleValue();
            final double max = axis.getMaxDoubleValue();
            final double range = max - min;
            final long rangeMillis = SGDateUtility.toMillis(range);
            Period p = new Period();
            long temp = rangeMillis;
            p = p.withMillis((int) (temp % 1000L));
            temp /= 1000L;
            p = p.withSeconds((int) (temp % 60L));
            temp /= 60L;
            p = p.withMinutes((int) (temp % 60L));
            temp /= 60L;
            p = p.withHours((int) (temp % 24L));
            temp /= 24L;
            p = p.withDays((int) (temp % 30L));
            temp /= 30L;
            p = p.withMonths((int) (temp % 12L));
            temp /= 12L;
            p = p.withYears((int) temp);

            SGDate minDate = new SGDate(min);
            DateTime minDateTime = minDate.getUTCDateTime();
            final int year = minDateTime.getYear();
            final int month = minDateTime.getMonthOfYear();
            final int day = minDateTime.getDayOfMonth();
            final int hour = minDateTime.getHourOfDay();
            final int minute = minDateTime.getMinuteOfHour();
            final int second = minDateTime.getSecondOfMinute();
            final int millis = minDateTime.getMillisOfSecond();

            DateTime baselineDateTime = new DateTime(0L, 
            		DateTimeZone.forTimeZone(SGDateUtility.getUTCTimeZoneInstance()));
            if (p.getYears() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year);
            } else if (p.getMonths() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year)
            			.withMonthOfYear(month);
            } else if (p.getDays() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year)
            			.withMonthOfYear(month).withDayOfMonth(day);
            } else if (p.getHours() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year)
            			.withMonthOfYear(month).withDayOfMonth(day)
            			.withHourOfDay(hour);
            } else if (p.getMinutes() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year)
            			.withMonthOfYear(month).withDayOfMonth(day)
            			.withHourOfDay(hour).withMinuteOfHour(minute);
            } else if (p.getSeconds() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year)
            			.withMonthOfYear(month).withDayOfMonth(day)
            			.withHourOfDay(hour).withMinuteOfHour(minute)
            			.withSecondOfMinute(second);
            } else if (p.getMillis() != 0) {
            	baselineDateTime = baselineDateTime.withYear(year)
            			.withMonthOfYear(month).withDayOfMonth(day)
            			.withHourOfDay(hour).withMinuteOfHour(minute)
            			.withSecondOfMinute(second).withMillisOfSecond(millis);
            }
            ret = new SGAxisDateValue(baselineDateTime);
    	} else {
    		ret = new SGAxisDoubleValue();
    	}
    	return ret;
	}
	
    /**
     * Calculate the step value from a given axis.
     * 
     * @param axis
     *          an axis
     * @return step value
     */
    protected SGAxisStepValue calcStepValue(SGAxis axis) {
        // minimum and maximum values of axis range
        final double min = axis.getMinDoubleValue();
        final double max = axis.getMaxDoubleValue();
        final double width = max - min;

        final SGAxisStepValue ret;
    	if (axis.getDateMode()) {
            final long millisAll = SGDateUtility.toMillis(width);
            final long secondsAll = millisAll / 1000L;
            final long minutesAll = secondsAll / 60L;
            final long hoursAll = minutesAll / 60L;
            final long daysAll = hoursAll / 24L;
            final long monthsAll = daysAll / 30L;
            final long yearsAll = monthsAll / 12L;

            final int millis = (int) (millisAll - 1000L * secondsAll);
            final int seconds = (int) (secondsAll - 60L * minutesAll);
            final int minutes = (int) (minutesAll - 60L * hoursAll);
            final int hours = (int) (hoursAll - 24L * daysAll);
            final int days = (int) (daysAll - 30L * monthsAll);
            final int months = (int) (monthsAll - 12L * yearsAll);
            final int years = (int) yearsAll;

            final int dateRangeOrder = SGUtilityNumber.getOrder(width);
            final double dateRangePower = SGUtilityNumber.getPowersOfTen(dateRangeOrder);
            final double yearValue = (width - 0.10 * dateRangePower) / 365;
            final int yearPowersOfTen = getPowersOfTen(yearValue);
            final double yearRatio;
            if (yearPowersOfTen != 0) {
                yearRatio = (double) years / yearPowersOfTen;
            } else {
            	yearRatio = 0.0;
            }

            final Period step;
            if (yearRatio > 6.0) {
            	step = Period.years(2 * yearPowersOfTen);
            } else if (yearRatio > 2.0 && yearRatio <= 6.0) {
            	step = Period.years(yearPowersOfTen);
            } else if (yearRatio > 1.1 && yearRatio <= 2.0) {
            	if (yearPowersOfTen > 1) {
                	step = Period.years(yearPowersOfTen / 2);
            	} else {
                	step = Period.years(1);
            	}
            } else {
            	if (years > 0) {
                	if (yearPowersOfTen > 1) {
                		step = Period.years(yearPowersOfTen / 5);
                	} else {
                    	step = Period.years(1);
                	}
            	} else {
                	if (months > 0) {
                		step = Period.months(months);
                	} else {
                		if (days > 0) {
                    		step = Period.days(days);
                		} else {
                			if (hours > 0) {
                        		step = Period.hours(hours);
                			} else {
                				if (minutes > 0) {
                            		step = Period.minutes(minutes);
                				} else {
                					if (seconds > 0) {
                                		step = Period.seconds(seconds);
                					} else {
                                		step = Period.millis((int) millis);
                					}
                				}
                			}
                		}
                	}
            	}
            }
            ret = new SGAxisDateStepValue(step);

    	} else {

            // get the order of axis range
            final int wOrder = SGUtilityNumber.getOrder(width);
            final double wPower = SGUtilityNumber.getPowersOfTen(wOrder);

            final double value = width - 0.10 * wPower;
            final int vOrder = SGUtilityNumber.getOrder(value);
            final double vPower = SGUtilityNumber.getPowersOfTen(vOrder);
            final double ratio = value / vPower;

            double step = 0.0;
            if (ratio <= 1.1) {
                step = 0.20 * vPower;
            } else if (1.1 < ratio && ratio <= 2.0) {
                step = 0.50 * vPower;
            } else if (2.0 < ratio && ratio <= 6.0) {
                step = 1.0 * vPower;
            } else if (6.0 < ratio) {
                step = 2.0 * vPower;
            }

            step = SGUtilityNumber.getNumberInRangeOrder(step, axis);
            ret = new SGAxisDoubleStepValue(step);
    	}
    	
    	return ret;
    }

    /**
     * 
     * @return
     */
    public String getInstanceDescription() {
        return this.getClassDescription();
    }

    /**
     * 
     * @param x
     * @return
     */
    protected float getXFromGraphRectValue(final float x) {
        final float xx = this.getMagnification() * x + this.mGraphRectX;
        return xx;
    }

    /**
     * 
     * @param y
     * @return
     */
    protected float getYFromGraphRectValue(final float y) {
        final float yy = this.getMagnification() * y + this.mGraphRectY;
        return yy;
    }

    /**
     * 
     * @param x
     * @return
     */
    protected float getGraphRectValueX(final float x) {
        final float xx = (x - this.mGraphRectX) / this.getMagnification();
        return xx;
    }

    /**
     * 
     * @param y
     * @return
     */
    protected float getGraphRectValueY(final float y) {
        final float yy = (y - this.mGraphRectY) / this.getMagnification();
        return yy;
    }

    //
    // Image
    //

    protected abstract void paintGraphics(Graphics g, boolean clip);

    // protected Image mImg = null;

    // protected void updateImage() {
    // //System.out.println("updateImage");
    // //final long before = System.currentTimeMillis();
    //
    // final int w = this.getComponent().getWidth();
    // final int h = this.getComponent().getHeight();
    //
    // if( w<=0 || h<=0 )
    // {
    // return;
    // }
    //
    // BufferedImage bImg = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB
    // );
    //
    // Graphics2D g2d = bImg.createGraphics();
    // this.paintGraphics( g2d, true );
    //
    // this.mImg = bImg;
    //
    // //final long after = System.currentTimeMillis();
    // //System.out.println(after-before);
    //
    // this.repaint();
    // }

    public void paint(final Graphics g, final boolean clip) {
        this.paintGraphics(g, clip);
    }

    @Override
    public List<SGIChildObject> getVisibleChildList() {
        final List<SGIChildObject> list = new ArrayList<SGIChildObject>();
        final List<SGIChildObject> aList = new ArrayList<SGIChildObject>(this.mChildList);
        for (int ii = 0; ii < aList.size(); ii++) {
            final SGIChildObject el = aList.get(ii);
            if (el.isVisible()) {
                list.add(el);
            }
        }

        return list;
    }
    
    protected List<SGIUndoable> getUndoableChildList() {
        final List<SGIUndoable> ucList = new ArrayList<SGIUndoable>();
        final List<SGIChildObject> cList = this.getVisibleChildList();
        for (int ii = 0; ii < cList.size(); ii++) {
            SGIChildObject obj = cList.get(ii);
            if (obj instanceof SGIUndoable) {
                SGIUndoable u = (SGIUndoable) obj;
                ucList.add(u);
            }
        }
        return ucList;
    }

    /**
     * 
     */
    protected boolean setVisibleChildList(final List<SGIChildObject> listVisible) {
        return this.setVisibleList(this.mChildList, listVisible);
    }

    /**
     * Get a list of the focused objects.
     * @param list
     *             a list of the focused objects
     * @return
     *             true if succeeded
     */
    public boolean getFocusedObjectsList(List<SGISelectable> list) {
        List<SGIChildObject> elList = this.getVisibleChildList();
        for (int ii = 0; ii < elList.size(); ii++) {
            SGIChildObject el = elList.get(ii);
            if (el instanceof SGISelectable) {
                SGISelectable s = (SGISelectable) el;
                if (s.isSelected()) {
                    list.add(s);
                }
            }
        }
        return true;
    }
    
    /**
     * Returns whether any focused objects are changed.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean isFocusedObjectsChanged() {
        List<SGISelectable> elList = this.getFocusedObjectsList();
        for (int ii = 0; ii < elList.size(); ii++) {
            SGISelectable s = elList.get(ii);
            if (s instanceof SGIUndoable) {
	        	SGIUndoable u = (SGIUndoable) s;
	        	if (u.isChanged()) {
	        	    return true;
	        	}
            }
        }
        return false;
    }

    /**
     * 
     */
    public boolean hideSelectedObject(final SGISelectable s) {
        SGIVisible el = (SGIVisible) s;
        el.setVisible(false);
        return true;
    }

    /**
     * Move the focused objects to the head or the tail of the list
     * 
     * @param toTail
     *                flag whether to move focused objects to the tail of the list
     * @return true if succeeded
     */
    public boolean moveFocusedObjects(final boolean toTail) {
        return this.moveFocusedObjects(toTail, this.mChildList);
    }

    /**
     * Move the focused objects to forward or backward for given steps.
     * 
     * @param num
     *           the number of levels to move
     * @return true if succeeded
     */
    public boolean moveFocusedObjects(int num) {
        return this.moveFocusedObjects(num, this.mChildList);
    }
    
    /**
     * Brings to front or sends to back the object of given ID.
     * 
     * @param id
     *           the ID of an object
     * @param toFront
     *           true to bring to front
     * @return true if succeeded
     */
    public boolean moveChildToEnd(final int id, final boolean toFront) {
        SGIChildObject el = this.getVisibleChild(id);
        if (el == null) {
            return false;
        }

        // record the list before edited
        ArrayList objListOld = new ArrayList(this.mChildList);

        // move focused objects
        if (toFront) {
            SGUtility.moveObjectToTail(el, this.mChildList);
        } else {
            SGUtility.moveObjectToHead(el, this.mChildList);
        }

        final boolean ch = !this.mChildList.equals(objListOld);
        if (ch) {
            this.setChanged(true);
            this.notifyChange();
            this.notifyToRoot();
            this.repaint();
        }

        return true;
    }

    /**
     * Brings forward or sends backward the object of given ID.
     * 
     * @param id
     *           the ID of an object
     * @param toFront
     *           true to bring forward
     * @return true if succeeded
     */
    public boolean moveChild(final int id, final boolean toFront) {
        SGIChildObject el = this.getVisibleChild(id);
        if (el == null) {
            return false;
        }

        // record the list before edited
        ArrayList objListOld = new ArrayList(this.mChildList);

        // move focused objects
        if (toFront) {
            SGUtility.moveObjectToNext(el, this.mChildList);
        } else {
            SGUtility.moveObjectToPrevious(el, this.mChildList);
        }

        final boolean ch = !this.mChildList.equals(objListOld);
        if (ch) {
            this.setChanged(true);
            this.notifyChange();
            this.notifyToRoot();
            this.repaint();
        }

        return true;
    }
    
    /**
     * Move focused objects in a list.
     * 
     * @param toTail
     *                whether to move focused objects to the tail of the list
     * @param objList
     *                the object list
     * @return
     *                true if succeeded
     */
    protected boolean moveFocusedObjects(final boolean toTail, final List objList) {
        // get the focused objects
    	List<SGISelectable> list = this.getFocusedObjectsList();
        if (list.size() == 0) {
            return true;
        }

        // record the list before edited
        List<SGISelectable> objListOld = new ArrayList<SGISelectable>(objList);

        // move focused objects
        if (toTail) {
            for (int ii = 0; ii < list.size(); ii++) {
                Object el = list.get(ii);
                SGUtility.moveObjectToTail(el, objList);
            }
        } else {
            for (int ii = list.size() - 1; ii >= 0; ii--) {
                Object el = list.get(ii);
                SGUtility.moveObjectToHead(el, objList);
            }
        }

        // set changed flag
        if (objList.equals(objListOld) == false) {
            notifyChange();
            this.setChanged(true);
        }

        return true;
    }

    /**
     * Move focused objects to back or front.
     * 
     * @param num
     *                the number of levels to move
     * @param objList
     *                the object list
     * @return
     *                true if succeeded
     */
    protected boolean moveFocusedObjects(final int num, final List objList) {
        // get the focused objects
    	List<SGISelectable> list = this.getFocusedObjectsList();
        if (list.size() == 0) {
            return true;
        }
        
        // record the list before edited
        List<SGISelectable> objListOld = new ArrayList<SGISelectable>(objList);
        if (SGUtility.moveObject(list, objList, num) == false) {
            return false;
        }

        // set changed flag
        if (objList.equals(objListOld) == false) {
            notifyChange();
            this.setChanged(true);
        }

        return true;
    }
        
    /**
     * Returns a list of child nodes.
     * 
     * @return a list of chid nodes
     */
    public ArrayList getChildNodes() {
        return new ArrayList(this.getVisibleChildList());
    }

    /**
     * 
     */
    public boolean isChanged() {
        return this.mUndoManager.isChanged();
    }

    /**
     * 
     */
    public boolean isChangedRoot() {
        if (this.isChanged()) {
            return true;
        }
        final List list = this.getUndoableChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            final Object obj = list.get(ii);
            if (obj instanceof SGIUndoable) {
                final SGIUndoable el = (SGIUndoable) obj;
                if (el.isChangedRoot()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 
     */
    public boolean updateHistory() {
//        final boolean changed = this.isChanged();

//        if (this.updateHistory(this.getVisibleChildList()) == false) {
//            return false;
//        }
        if (this.updateHistory(new ArrayList(this.getUndoableChildList())) == false) {
            return false;
        }

//        // if the properties of this figure element was changed,
//        // remove useless child objects
//        if (changed) {
//            this.removeUselessChild();
//        }

        return true;
    }

    /**
     *  Delete useless child objects.
     *  @return
     *          true if succeeded
     */
    protected boolean deleteUselessChild() {
        Set set = this.getAvailableChildSet();
        List cList = new ArrayList(this.mChildList);
        for (int ii = cList.size() - 1; ii >= 0; ii--) {
            Object obj = cList.get(ii);
            if (set.contains(obj) == false) {
                this.removeChild(obj);
            }
            obj = null;
        }
        return true;
    }

    /**
     * Returns a set of available child objects in the histories.
     * By default, this method returns an empty set in the histories.
     * 
     * @return
     *         a set of available child objects in the histories
     */
    protected Set getAvailableChildSet() {
        return new HashSet();
    }

    /**
     * Initialize the undo buffer.
     */
    public void initUndoBuffer() {

    	// child objects
        List cList = this.getUndoableChildList();
        for (int ii = 0; ii < cList.size(); ii++) {
            Object obj = cList.get(ii);
            if (obj instanceof SGIUndoable) {
                SGIUndoable u = (SGIUndoable) obj;
                u.initUndoBuffer();
            }
        }
        
        // this object
        this.mUndoManager.initUndoBuffer();
        
        // delete useless child objects
        if (this.deleteUselessChild() == false) {
            throw new Error("Failed to initialize undo buffer.");
        }

    }
    
    /**
     * Delete all forward histories.
     *
     * @return
     *         true if succeeded
     */
    public boolean deleteForwardHistory() {
	
        // delete forward history
        List cList = this.getUndoableChildList();
        for (int ii = 0; ii < cList.size(); ii++) {
            SGIUndoable obj = (SGIUndoable) cList.get(ii);
            if (obj.deleteForwardHistory() == false) {
        	return false;
            }
        }
        if (this.mUndoManager.deleteForwardHistory() == false) {
            return false;
        }
        
        // remove useless child objects
        if (this.deleteUselessChild() == false) {
            return false;
        }

        return true;
    }
    
    /**
     * 
     */
    public boolean hideChildObject(final int id) {
        SGIChildObject obj = this.getVisibleChild(id);
        if (obj == null) {
            return false;
        }

        obj.setVisible(false);

        this.clearFocusedObjects();
        this.setChanged(true);
        this.notifyChange();
        this.notifyToRoot();
        this.repaint();

        return true;
    }

    /**
     * Invoked when the key is pressed.
     * 
     * @param e
     *          the key event
     * @return true if effective
     */
    public boolean onKeyPressed(final KeyEvent e) {
        boolean effective = false;
        
        // translate the focused movable child objects
        SGTuple2f d = this.getDisplacement(e);
        if (!d.isZero()) {
        	List<MovableInfo> list = this.getMovableObjectList();
        	for (MovableInfo info : list) {
        		SGIMovable m = info.movableObject;
        		SGIPropertyDialogObserver obs = info.observer;
                if (m.isSelected()) {
                    if (obs.prepare() == false) {
                    	return false;
                    }
                    m.translate(d.x, d.y);
                    if (obs.commit() == false) {
                    	return false;
                    }
                    effective = true;
                }
        	}
        }
        if (effective) {
            this.notifyChange();
        }
        return effective;
    }
    
    protected List<MovableInfo> getMovableObjectList() {
    	List<MovableInfo> objList = new ArrayList<MovableInfo>();
        final List<SGIChildObject> list = this.getVisibleChildList();
        for (SGIChildObject child :list) {
            if (child instanceof SGIMovable && child instanceof SGIPropertyDialogObserver) {
            	SGIMovable m = (SGIMovable) child;
            	if (!m.isSelected()) {
            		continue;
            	}
            	SGIPropertyDialogObserver obs = (SGIPropertyDialogObserver) child;
            	MovableInfo info = new MovableInfo(m, obs);
            	objList.add(info);
            }
        }
    	return objList;
    }

    public static class MovableInfo {
    	SGIMovable movableObject;
    	SGIPropertyDialogObserver observer;
    	public MovableInfo(SGIMovable m, SGIPropertyDialogObserver obs) {
    		super();
    		this.movableObject = m;
    		this.observer = obs;
    	}
    }
    
    /**
     * The smaller displacement by the key event.
     */
    protected static final int DISPLACEMENT_SMALL = 1;

    /**
     * The larger displacement by the key event.
     */
    protected static final int DISPLACEMENT_LARGE = 10;

	/**
	 * Returns the displacement for objects those are translated by the key
	 * event.
	 * 
	 * @param e
	 *            the key event
	 * @return displacement for movable child objects
	 */
	protected SGTuple2f getDisplacement(KeyEvent e) {
		final int keycode = e.getKeyCode();
		final int mod = e.getModifiersEx();
		final boolean isShiftPressed = ((mod & InputEvent.SHIFT_DOWN_MASK) != 0);
		int dx = 0;
		int dy = 0;
		switch (keycode) {
		case KeyEvent.VK_UP:
			if (isShiftPressed) {
				dy = -DISPLACEMENT_SMALL;
			} else {
				dy = -DISPLACEMENT_LARGE;
			}
			break;
		case KeyEvent.VK_DOWN:
			if (isShiftPressed) {
				dy = DISPLACEMENT_SMALL;
			} else {
				dy = DISPLACEMENT_LARGE;
			}
			break;
		case KeyEvent.VK_LEFT:
			if (isShiftPressed) {
				dx = -DISPLACEMENT_SMALL;
			} else {
				dx = -DISPLACEMENT_LARGE;
			}
			break;
		case KeyEvent.VK_RIGHT:
			if (isShiftPressed) {
				dx = DISPLACEMENT_SMALL;
			} else {
				dx = DISPLACEMENT_LARGE;
			}
			break;
		}
		return new SGTuple2f(dx, dy);
	}

	/**
	 * Notify to listeners that the data selection is updated.
	 */
	public void notifyDataSelection() {
		this.notifyToListener(SGIFigureElement.NOTIFY_DATA_SELECTION);
	}

    /**
	 * Returns a string representation of this class. This method returns the
	 * simple class name.
	 * 
	 * @return a string representation of this class
	 */
    public String toString() {
        return this.getClass().getSimpleName();
    }
    
    /**
	 * Duplicate the focused data.
	 * 
	 * @param data
	 *            a data to duplicate
	 * @param nameNew
	 *            new name of data
	 * @return true if succeeded
	 */
    public boolean duplicateFocusedData(SGData data, String nameNew) {
        // do nothing by default
        return true;
    }

    /**
     * Synchronize the data properties with given data properties.
     * 
     * @param p
     *          the properties
     * @param dp
     *          data properties
     * @return synchronized properties
     */
    public SGProperties synchronizeDataProperties(SGProperties p, SGProperties dp) {
        // do nothing by default
        return null;
    }

    /**
     * Called when menu items in the menu bar is selected.
     *
     */
    public void onMenuSelected() {
        // do nothing by default
    }

    /**
     * Calculates and returns a set of axis values.
     * 
     * @param axis
     *           an axis
     * @param baseline
     *           the baseline value
     * @param step
     *           the step value
     * @return a set of axis values
     */
    protected Set<SGAxisValue> calcAxisValues(SGAxis axis, 
    		final SGAxisValue baseline, final SGAxisStepValue step) {
    	final boolean dateMode = axis.getDateMode();
        final double min = axis.getMinDoubleValue();
        final double max = axis.getMaxDoubleValue();
        return SGUtilityNumber.calcStepValue(
        		dateMode ? new SGAxisDateValue(min) : new SGAxisDoubleValue(min), 
        		dateMode ? new SGAxisDateValue(max) : new SGAxisDoubleValue(max), 
        		baseline, step, 
        		SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT);
    }

    /**
     * Sets the properties.
     * 
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    @Override
    public SGPropertyResults setProperties(SGPropertyMap map) {
        // do nothing by default
        return null;
    }

    /**
     * Sets the properties of child object.
     * 
     * @param id
     *           the ID of child object
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    @Override
    public SGPropertyResults setChildProperties(final int id,
            SGPropertyMap map) {
        // do nothing by default
        return null;
    }

    /**
     * Sets the properties of sub child object.
     * 
     * @param id
     *           the ID of child object
     * @param subId
     *           the ID of sub child object
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    @Override
    public SGPropertyResults setChildProperties(final int id, final int subId,
            SGPropertyMap map) {
        // do nothing by default
    	return null;
    }

    /**
     * Sets the properties of sub child object.
     * 
     * @param id
     *           the ID of child object
     * @param colorMapName
     *           the name of color map
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    @Override
    public SGPropertyResults setChildColorMapProperties(final int id, 
    		final String colorMapName, SGPropertyMap map) {
        // do nothing by default
    	return null;
    }

    /**
     * Returns the child object of a given ID.
     * 
     * @param id
     *           the ID number of the child
     * @return the child object if it exists
     */
    @Override
    public SGIChildObject getChild(final int id) {
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGIChildObject c = this.mChildList.get(ii);
            if(c.getID() == id) {
                return c;
            }
        }
        return null;
    }

    /**
     * Closes the text field.
     * 
     * @return true if succeed
     */
    @Override
    public boolean closeTextField() {
    	// do nothing by default
    	return true;
    }
    
    protected SGTuple2f mDraggedDirection = null;
    
    protected int mFixedCoordinate = 0;

    protected MouseDragResult getMouseDragResult(MouseEvent e) {
    	MouseDragInput params = new MouseDragInput(e, 
    			this.mPressedPoint, this.mDraggedDirection, this.mFixedCoordinate);
    	MouseDragResult result = SGUtility.getMouseDragResult(params);
    	this.mDraggedDirection = result.draggedDirection;
    	this.mFixedCoordinate = result.fixedCoordinate;
        return result;
    }

    /**
     * Sets the flag whether data objects in this figure are anchored
     * 
     * @param b
     *           true to set data objects in this figure anchored
     * @return true if succeeded
     */
    @Override
    public boolean setDataAnchored(final boolean b) {
    	// do nothing by default
    	return true;
    }
    
    private Class<?> mPropertyDialogObserverClass = null;
    
    /**
     * Returns the class object of property dialog observer.
     * 
     * @return the class object
     */
    @Override
    public Class<?> getPropertyDialogObserverClass() {
    	return this.mPropertyDialogObserverClass;
    }

    /**
     * Sets the class object of property dialog observer.
     * 
     * @param cl
     *           a class object to set
     */
    @Override
    public void setPropertyDialogObserverClass(Class<?> cl) {
		this.mPropertyDialogObserverClass = cl;
    }

    protected SGDrawingWindow mWnd = null;
    
    /**
     * Sets the window.
     * 
     * @param wnd
     *           a window
     */
    @Override
    public void setWindow(SGDrawingWindow wnd) {
    	this.mWnd = wnd;
    }
    
    /**
     * Returns the window.
     * 
     * @return the window
     */
    @Override
    public SGDrawingWindow getWindow() {
    	return this.mWnd;
    }
    
    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
	@Override
	public String getCommandString(SGExportParameter params) {
		StringBuffer sb = new StringBuffer();
		List<SGIChildObject> cList = this.getVisibleChildList();
    	for (SGIChildObject c : cList) {
    		SGIChildObject el = (SGIChildObject) c;
    		String cmdString = el.getCommandString(params);
    		sb.append(cmdString);
    	}
		return sb.toString();
	}

    /**
     * Replaces the data source.
     * 
     * @param srcOld
     *           old data source
     * @param srcNew
     *           new data source
     * @param obs
     *           data source observer
     */
    @Override
    public void replaceDataSource(final SGIDataSource srcOld, final SGIDataSource srcNew,
    		final SGDataSourceObserver obs) {
    	// do nothing by default
    }

    protected boolean isNotificationMessage(final String msg) {
    	return SGIFigureElement.NOTIFY_CHANGE.equals(msg)
				|| SGIFigureElement.NOTIFY_CHANGE_ON_CANCEL.equals(msg)
				|| SGIFigureElement.NOTIFY_CHANGE_ON_COMMIT.equals(msg)
				|| SGIFigureElement.NOTIFY_CHANGE_ON_UNDO.equals(msg);
    }

    /**
     * Called when the mouse pointer moves.
     * 
     * @param e
     *          the mouse event
     * @return true if the mouse pointer passes over some objects
     */
    @Override
    public boolean onMouseMoved(MouseEvent e) {
    	// returns false by default
    	return false;
    }
    
    @Override
    public List<SGINode> getNodes() {
    	List<SGINode> list = new ArrayList<SGINode>();
    	list.add(this);
    	return list;
    }

}
