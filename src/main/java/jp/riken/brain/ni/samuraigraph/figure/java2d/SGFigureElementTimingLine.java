package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
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
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGICopiable;
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
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGITimingLineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class of timing lines.
 */
public class SGFigureElementTimingLine extends SGFigureElement2D implements
        SGIFigureElementTimingLine, SGITimingLineConstants {

    /**
     * 
     */
    private SGIFigureElementAxis mAxisElement = null;

    /**
     * The property dialog of timing lines.
     */
    private SGPropertyDialog mPropertyDialog = null;

    /**
     * Builds this object.
     * 
     */
    public SGFigureElementTimingLine() {
        super();
        
        // create an instance of the guide arrow
        SGDrawingElementArrow2D a = new GuideArrow();
        a.setVisible(false);
        this.mGuideArrow = a;
    }

    /**
     * Arrow for the guide.
     *
     */
    private static class GuideArrow extends SGSimpleArrow2D {
        public GuideArrow() {
            super();
            
            // set fixed properties
            this.setStartHeadType(SGIArrowConstants.SYMBOL_TYPE_VOID);
            this.setEndHeadType(SGIArrowConstants.SYMBOL_TYPE_ARROW_HEAD);
            this.setLineWidth(3.0f);
            this.setLineType(SGILineConstants.LINE_TYPE_SOLID);
            this.setColor(Color.RED);
            this.setHeadSize(12.0f);
            this.setHeadAngle(36.0f, 60.0f);
        }
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        
        // dispose the property dialog
        if (this.mPropertyDialog != null) {
            this.mPropertyDialog.dispose();
            this.mPropertyDialog = null;
        }
        
        // dispose the guide arrow
        if (this.mGuideArrow != null) {
            this.mGuideArrow.dispose();
            this.mGuideArrow = null;
        }
    }

    /**
     * 
     * @return
     */
    public String getClassDescription() {
        return "Timing Lines";
    }

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element) {
        this.mAxisElement = element;
    }

    /**
     * 
     * @param x
     * @param y
     */
    public void guideToAdd(final int x, final int y) {

        if (this.getGraphRect().contains(x, y) == false) {
            this.mGuideArrow.setVisible(false);
            this.repaint();
            return;
        }

        ArrayList configList = new ArrayList();
        ArrayList distList = new ArrayList();
        this.getNearestAxisInfo(x, y, configList, distList);
        final int config = ((Integer) configList.get(0)).intValue();
        final float distance = ((Number) distList.get(0)).floatValue();

        final float length = distance;

        SGTuple2f start = new SGTuple2f(x, y);
        SGTuple2f end = new SGTuple2f();
        if (config == SGIFigureElementAxis.AXIS_HORIZONTAL_1) {
            end.x = x;
            end.y = y + length;
        } else if (config == SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
            end.x = x;
            end.y = y - length;
        } else if (config == SGIFigureElementAxis.AXIS_VERTICAL_1) {
            end.x = x - length;
            end.y = y;
        } else if (config == SGIFigureElementAxis.AXIS_VERTICAL_2) {
            end.x = x + length;
            end.y = y;
        }

        this.mGuideArrow.setLocation(start, end);

        this.mGuideArrow.setVisible(true);
        this.repaint();
    }

    private void getNearestAxisInfo(final int x, final int y,
            final ArrayList configList, final ArrayList distList) {

        final float left = this.getGraphRectX();
        final float right = left + this.getGraphRectWidth();
        final float top = this.getGraphRectY();
        final float bottom = top + this.getGraphRectHeight();

        final float dl = x - left;
        final float dr = right - x;
        final float dt = y - top;
        final float db = bottom - y;

        float[] array = { dl, dr, dt, db };
        float min = Float.MAX_VALUE;
        int minIndex = -1;
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] < min) {
                min = array[ii];
                minIndex = ii;
            }
        }

        int config = -1;
        if (minIndex == 0) {
            config = SGIFigureElementAxis.AXIS_VERTICAL_1;
        } else if (minIndex == 1) {
            config = SGIFigureElementAxis.AXIS_VERTICAL_2;
        } else if (minIndex == 2) {
            config = SGIFigureElementAxis.AXIS_HORIZONTAL_2;
        } else if (minIndex == 3) {
            config = SGIFigureElementAxis.AXIS_HORIZONTAL_1;
        }

        configList.add(Integer.valueOf(config));
        distList.add(Float.valueOf(min));
    }

    /**
     * Inserts a timing line with given axis value to given axis.
     * 
     * @param id
     *           the ID to set
     * @param axisLocation
     *          location of the axis
     * @param value
     *           the axis value for given axis
     * @return true if succeeded
     */
    public boolean addTimingLine(final int id, final int axisLocation,
    		final double value) {

        // get axis
        SGAxis axis = this.mAxisElement.getAxisInPlane(axisLocation);
        if (axis == null) {
            return false;
        }

        if (axis.isValidValue(value) == false) {
            return false;
        }

        // add a line
        if (this.addTimingLine(id, axis, value) == false) {
            return false;
        }

        return true;
    }
    
    /**
     * Inserts a timing line at a given point with default axis.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addTimingLine(final int x, final int y) {
        
        if (this.getGraphRect().contains(x, y) == false) {
            return false;
        }

        final ArrayList configList = new ArrayList();
        final ArrayList distList = new ArrayList();
        this.getNearestAxisInfo(x, y, configList, distList);
        final int config = ((Integer) configList.get(0)).intValue();

        final SGAxis axis = this.mAxisElement.getAxisInPlane(config);
        final double value = this.mAxisElement.getValue(config, x, y);

        // add a line
        if (this.addTimingLine(this.assignChildId(), axis, value) == false) {
            return false;
        }

        return true;
    }

    /**
     * Adds a timing line with given axis value for given axis.
     * 
     * @param id
     *           the ID to set
     * @param axis
     *           the axis
     * @param value
     *           the axis value
     */
    private boolean addTimingLine(final int id, final SGAxis axis, final double value) {
        
        // create an instance
        final TimingLine line = new TimingLine();

        line.setAxis(axis);
        line.setValue(value);
        line.setMagnification(this.getMagnification());

        // add to the list
        if (this.addToList(id, line) == false) {
            return false;
        }

        // hide the guide
        this.mGuideArrow.setVisible(false);

        // set focus
        this.updateFocusedObjectsList(line, 0);

        // update the history
        this.setChanged(true);
        this.notifyToRoot();
        
        return true;
    }
    

    protected boolean addToList(final SGIChildObject obj) {
        if (super.addToList(obj) == false) {
            return false;
        }
        this.afterAddedToList(obj);
        return true;
    }

    protected boolean addToList(final int id, final SGIChildObject obj) {
        if (super.addToList(id, obj) == false) {
            return false;
        }
        this.afterAddedToList(obj);
        return true;
    }
    
    private void afterAddedToList(final SGIChildObject obj) {
        TimingLine line = (TimingLine) obj;

        // location
        line.setDrawingElementsLocation();

        // properties
        line.initPropertiesHistory();
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setDialogOwner(final Frame frame) {
//        super.setDialogOwner(frame);
//        this.createTimingLineDialog();
//        return true;
//    }

    /**
     * Synchronize with other figure elements.
     */
    public boolean synchronize(final SGIFigureElement element, final String msg) {

        boolean flag = true;
        if (element instanceof SGIFigureElementLegend) {

        } else if (element instanceof SGIFigureElementAxis) {
            final SGIFigureElementAxis aElement = (SGIFigureElementAxis) element;
            flag = this.synchronizeToAxisElement(aElement, msg);
        } else if (element instanceof SGIFigureElementString) {

        } else if (element instanceof SGIFigureElementGraph) {

        } else if (element instanceof SGIFigureElementAxisBreak) {

        } else if (element instanceof SGIFigureElementSignificantDifference) {

        } else if (element instanceof SGIFigureElementTimingLine) {

        } else if (element instanceof SGIFigureElementGrid) {

        } else if (element instanceof SGIFigureElementShape) {

        } else {
            flag = this.synchronizeArgument(element, msg);
        }

        return flag;
    }

    /**
     * Synchronize the element given by the argument.
     * 
     * @param element
     *            An object to be synchronized.
     */
    public boolean synchronizeArgument(final SGIFigureElement element,
            final String msg) {
        // this shouldn't happen
        throw new Error();
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        SGProperties p = new TimingElementProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * 
     */
    public String getTagName() {
        return TAG_NAME_TIMING_LINES;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el, SGExportParameter params) {
        return true;
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
        NodeList nList = element
                .getElementsByTagName(TimingLine.TAG_NAME_TIMING_LINE);
        for (int ii = 0; ii < nList.getLength(); ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;
                TimingLine line = new TimingLine();
                if (line.readProperty(el) == false) {
                    return false;
                }
                this.addToList(line);
            }
        }

        return true;
    }

    /**
     * Creates an array of Element objects.
     * 
     * @param document
     *           an Document objects to append elements
     * @return an array of Element objects
     */
    public Element[] createElement(Document document, SGExportParameter params) {
        Element el = this.createThisElement(document, params);
        if (el == null) {
            return null;
        }

        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            TimingLine line = (TimingLine) list.get(ii);
            if (line.isValid() == false) {
                continue;
            }
            Element elTiming = line.createElement(document, params);
            if (elTiming == null) {
                return null;
            }
            el.appendChild(elTiming);
        }
        return new Element[] { el };
    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        super.setGraphRect(x, y, width, height);
        if (this.setAllAnchorEnabledDrawingElementsLocation() == false) {
            return false;
        }
        return true;
    }

    private boolean calcLocationOfTimingLine(final double value,
            final SGAxis axis, final boolean horizontal,
            final SGTuple2f startPoint, final SGTuple2f endPoint) {

        final float location = this.calcLocation(value, axis, horizontal);
        if (Float.isNaN(location)) {
            return false;
        }

        Rectangle2D rect = this.getGraphRect();
        if (horizontal) {
            startPoint.x = location;
            startPoint.y = (float) rect.getY();
            endPoint.x = location;
            endPoint.y = startPoint.y + (float) (rect.getHeight());
        } else {
            startPoint.x = (float) rect.getX();
            startPoint.y = location;
            endPoint.x = startPoint.x + (float) (rect.getWidth());
            endPoint.y = location;
        }

        return true;
    }

//    /**
//     * Create the property dialog.
//     */
//    private SGTimingLineDialog createPropertyDialog() {
//        final SGTimingLineDialog dg = new SGTimingLineDialog(this.mDialogOwner,
//                true);
////        this.mDialog = dg;
//        return dg;
//    }

    /**
     * 
     * @return
     */
    private ArrayList getVisibleTimingElementListInside() {
        ArrayList list = new ArrayList();
        List<SGIChildObject> lList = this.getVisibleChildList();
        for (int ii = 0; ii < lList.size(); ii++) {
            TimingLine line = (TimingLine) lList.get(ii);
            if (line.isInsideRange()) {
                list.add(line);
            }
        }

        return list;
    }

//    /**
//     * 
//     * @param groupSet
//     * @return
//     */
//    private boolean isInsideRange(SGElementGroupTimingLine line) {
//        final SGAxis axis = line.mAxis;
//        final double value = line.mValue;
//        return axis.insideRange(value);
//    }

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
                final TimingLine el = (TimingLine) list.get(ii);
                if (el.isSelected()) {
                	if (el.prepare() == false) {
                		return false;
                	}
                    el.translate((float) dx, (float) dy);
                    if (el.commit() == false) {
                    	return false;
                    }
                    notifyToRoot();
                    notifyChange();
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

        ArrayList list = this.getVisibleTimingElementListInside();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final TimingLine line = (TimingLine) list.get(ii);
            if (line.isValid() == false) {
                continue;
            }
            if (this.clickDrawingElements(line, e)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 
     */
    private boolean clickDrawingElements(final TimingLine el, final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int cnt = e.getClickCount();

        // clicked on the line elements
        if (el.contains(x, y)) {
            this.updateFocusedObjectsList(el, e);

            if (cnt == 1) {
                if (SwingUtilities.isLeftMouseButton(e)) {

                } else if (SwingUtilities.isRightMouseButton(e)) {
                    el.getPopupMenu().show(this.getComponent(), x, y);
                }
            } else if (cnt == 2) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    this.setPropertiesOfSelectedObjects(el);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 
     */
    public boolean onMousePressed(final MouseEvent e) {

        final int x = e.getX();
        final int y = e.getY();

        ArrayList list = this.getVisibleTimingElementListInside();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final TimingLine el = (TimingLine) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            if (el.contains(x, y)) {
                this.mPressedPoint = e.getPoint();
                this.setMouseCursor(Cursor.MOVE_CURSOR);
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
        return true;
    }

    /**
     * 
     */
    public boolean onMouseReleased(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();

        List<SGISelectable> list = this.getFocusedObjectsList();
        boolean onLine = false;
        for (int ii = 0; ii < list.size(); ii++) {
            TimingLine el = (TimingLine) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }

            if (el.contains(x, y)) {
                onLine = true;
            }
        }

        if (onLine) {
            this.setMouseCursor(Cursor.HAND_CURSOR);
        } else {
            this.setMouseCursor(Cursor.DEFAULT_CURSOR);
        }

        this.unselectOuterLines();

        this.mDraggableFlag = false;
        this.mDraggedDirection = null;

        return true;
    }

    /**
     * 
     */
    public boolean setMouseCursor(final int x, final int y) {
        ArrayList list = this.getVisibleTimingElementListInside();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final TimingLine el = (TimingLine) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            final boolean flag = el.contains(x, y);
            if (flag) {
                this.setMouseCursor(Cursor.HAND_CURSOR);
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
    	return this.getSelectedPropertyDialogObserverList();
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
    	return this.getVisiblePropertyDialogObserverList();
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
    	return TimingLine.class;
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setTemporaryPropertiesOfFocusedObjects() {
//    	List<SGISelectable> list = this.getFocusedObjectsList();
//        for (int ii = 0; ii < list.size(); ii++) {
//            TimingLine el = (TimingLine) list.get(ii);
//            el.mTemporaryProperties = el.getProperties();
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
            TimingLine el = (TimingLine) list.get(ii);
            SGProperties temp = el.mTemporaryProperties;
            if (temp != null) {
                SGProperties p = el.getProperties();
                if (p.equals(temp) == false) {
                    el.setChanged(true);
                }
            }
        }
        return true;
    }

    /**
     * An arrow for the guide to add a timing line.
     */
    private SGDrawingElementArrow2D mGuideArrow = null;

    /**
     * 
     */
    public void paintGraphics(Graphics g, boolean clip) {
        Graphics2D g2d = (Graphics2D) g;

        ArrayList list = this.getVisibleTimingElementListInside();
        Rectangle2D gRect = this.getGraphRect();

        // draw guide arrow
        if (this.mGuideArrow.isVisible()) {
            this.mGuideArrow.paint(g2d, gRect);
        }

        if (clip) {
            SGUtilityForFigureElementJava2D.clipGraphRect(this, g2d);
        }

        // draw timing line
        for (int ii = 0; ii < list.size(); ii++) {
            TimingLine tl = (TimingLine) list.get(ii);
            if (tl.isValid() == false) {
                continue;
            }
            if (clip) {
                tl.paint(g2d);
//                tl.paintElement(g2d);
            } else {
//                tl.paintElement(g2d, gRect);
                tl.paint(g2d, gRect);
            }
        }

        if (clip) {
            g2d.setClip(this.getViewBounds());
        }

        // draw symbols around all objects
        if (this.mSymbolsVisibleFlagAroundAllObjects) {
            for (int ii = 0; ii < list.size(); ii++) {
                TimingLine el = (TimingLine) list.get(ii);
                if (el.isValid() == false) {
                    continue;
                }
                ArrayList pList = el.getAnchorPointList();
                if (! el.isAnchored()) {
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
                TimingLine el = (TimingLine) fList.get(ii);
                if (el.isValid() == false) {
                    continue;
                }
                ArrayList pList = el.getAnchorPointList();
                if (! el.isAnchored()) {
                    SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pList, g2d);
                } else {
                    SGUtilityForFigureElementJava2D.drawAnchorPointsAsAnchoredForcusObject(pList, g2d);
                }
            }
        }

    }

//    /**
//     * 
//     */
//    protected boolean removeTimingElementGroupSet(
//            final SGElementGroupTimingLine line) {
//        this.mChildList.remove(line);
//        return false;
//    }

    /**
     * 
     */
    protected boolean synchronizeToAxisElement(
            final SGIFigureElementAxis aElement, final String msg) {
        this.setAllAnchorEnabledDrawingElementsLocation();
        this.unselectOuterLines();
        return true;
    }

    protected boolean setAllDrawingElementsLocation() {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            final TimingLine line = (TimingLine) list.get(ii);
            if (line.setDrawingElementsLocation() == false) {
                return false;
            }
        }

        return true;
    }
    
    protected boolean setAllAnchorEnabledDrawingElementsLocation() {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            final TimingLine line = (TimingLine) list.get(ii);
            if (! line.isAnchored()) {
                if (line.setDrawingElementsLocation() == false) {
                    return false;
                }
            } else {
                if (line.setAxisValue() == false) {
                    return false;
                }
                line.setChanged(true);
            }
        }

        return true;
    }

    // set unselected the lines out of range
    private void unselectOuterLines() {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            final TimingLine line = (TimingLine) list.get(ii);
            final boolean inside = line.isInsideRange();
            if (line.isSelected() && !inside) {
                line.setSelected(false);
            }
        }
    }

    /**
     * 
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof TimingElementProperties) == false) {
            return false;
        }

        TimingElementProperties gp = (TimingElementProperties) p;
        gp.visibleTimingElementList = new ArrayList(this.getVisibleChildList());

        return true;
    }

    /**
     * 
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof TimingElementProperties) == false) {
            return false;
        }

        TimingElementProperties gp = (TimingElementProperties) p;

        boolean flag;
        flag = this.setVisibleChildList(gp.visibleTimingElementList);
        if (!flag) {
            return false;
        }

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
            TimingLine el = (TimingLine) cList.get(ii);

            // translate the duplicate
            el.translate(ox, oy);

            // el.setDrawingElementsLocation();

            // set selected
            el.setSelected(true);

            // add to the list
            this.addToList(el);
        }

        if (cList.size() != 0) {
            this.setChanged(true);
        }

        // // set the location of drawing elements
        // this.setAllDrawingElementsLocation();

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
            if (obj instanceof TimingLine) {
                TimingLine line = (TimingLine) obj;

                // translate the instance to be pasted
                line.translate(ox, oy);

                SGProperties p = line.getProperties();

                TimingLine el = new TimingLine();
                el.setMagnification(mag);
                el.setProperties(p);

                // el.mAxis = this.mAxisElement.getAxisInCube(line.mTempAxis);
                el.mAxis = this.mAxisElement.getAxisInPlane(line.mTempAxis);

                // el.setDrawingElementsLocation();

                // add to the list
                this.addToList(el);

                // initialize history
                el.initPropertiesHistory();

                cnt++;
            }
        }

        // this.setAllDrawingElementsLocation();

        if (cnt != 0) {
            this.setChanged(true);
        }

        // this.repaint();

        return true;
    }

    /**
     * 
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            TimingElementProperties p = (TimingElementProperties) mList.get(ii);
            set.addAll(p.visibleTimingElementList);
        }

        return set;
    }
    
    private static class TimingLinePopupMenu extends JPopupMenu {
        private static final long serialVersionUID = -121087808318837856L;

        private JCheckBoxMenuItem anchoredCheckBox;
        
        void init(TimingLine tm) {
            this.setBounds(0, 0, 100, 100);

            StringBuffer sb = new StringBuffer();
            sb.append("  -- Timing Line: ");
            sb.append(tm.getID());
            sb.append(" --");
            
            this.add(new JLabel(sb.toString()));
            this.addSeparator();

            SGUtility.addArrangeItems(this, tm);

            this.addSeparator();

            SGUtility.addItem(this, tm, MENUCMD_CUT);
            SGUtility.addItem(this, tm, MENUCMD_COPY);

            this.addSeparator();

            SGUtility.addItem(this, tm, MENUCMD_DELETE);
            SGUtility.addItem(this, tm, MENUCMD_DUPLICATE);
            
            addSeparator();
            
            this.anchoredCheckBox = new JCheckBoxMenuItem(MENUCMD_ANCHORED);
            add(this.anchoredCheckBox);
            this.anchoredCheckBox.addActionListener(tm);

            this.addSeparator();

            SGUtility.addItem(this, tm, MENUCMD_PROPERTY);
        }
        
        void setAnchored(final boolean anchored) {
            this.anchoredCheckBox.setSelected(anchored);
        }
    }

    protected class TimingLine extends SGDrawingElementLine2D implements
            ActionListener, SGIUndoable, SGICopiable, SGIChildObject, SGIMovable,
            SGITimingLineDialogObserver, SGINode, SGITimingLineConstants {

        /**
         * The line stroke.
         */
        protected SGStroke mStroke = new SGStroke();

        /**
         * The line color.
         */
        protected Color mColor = null;
        
        /**
         * The related axis.
         */
        protected SGAxis mAxis = null;

        /**
         * The value.
         */
        protected double mValue;

        /**
         * The flag which is whether the value is anchored.
         */
        protected boolean mIsAnchored;
        
        private SGTuple2f mLocation = new SGTuple2f();
        
        /**
         * Returns the related axis
         * @return
         *       the related axis
         */
        public SGAxis getAxis() {
            return this.mAxis;
        }

        /**
         * Set the related axis.
         * @param axis
         *           an axis to be related
         */
        public void setAxis(final SGAxis axis) {
            this.mAxis = axis;
        }

        /**
         * @return mValue
         */
        public double getValue() {
            return this.mValue;
        }


        /**
         * Sets the axis value.
         * 
         * @param value
         *           the value to set
         * @return true if succeeded
         */
        public boolean setValue(final double value) {
        	if (this.mAxis.isValidValue(value) == false) {
        		return false;
        	}
            this.mValue = value;
            return true;
        }

        /**
         * 
         */
        public void dispose() {
            super.dispose();
            this.mAxis = null;
            this.mPopupMenu = null;
            this.mUndoManager.dispose();
            this.mUndoManager = null;
            if (this.mTemporaryProperties != null) {
                this.mTemporaryProperties.dispose();
                this.mTemporaryProperties = null;
            }
            this.mStroke = null;
        }

        /**
         * 
         */
        private int mID;

        public int getID() {
            return this.mID;
        }

        public boolean setID(final int id) {
            this.mID = id;
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
         */
        public Color getColor() {
            return this.mColor;
        }

        /**
         * 
         */
        public float getLineWidth(final String unit) {
            return (float) SGUtilityText.convertFromPoint(this.getLineWidth(),
                    unit);
        }

        /**
         * Sets the line width in a given unit.
         * 
         * @param lw
         *           the line width to set
         * @param unit
         *           a unit of length
         * @return true if succeeded
         */
        public boolean setLineWidth(final float lw, final String unit) {
            final Float lwNew = SGUtility.getLineWidth(lw, unit);
            if (lwNew == null) {
                return false;
            }
            if (this.setLineWidth(lwNew) == false) {
                return false;
            }
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
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * 
         */
        public boolean prepare() {
            this.mTemporaryProperties = this.getProperties();
            return true;
        }
        
        public SGProperties getProperties() {
            TimingLineProperties p = new TimingLineProperties();
            if (this.getProperties(p) == false) {
                return null;
            }
            return p;
        }
        
        public boolean getProperties(SGProperties p) {
            if ((p instanceof TimingLineProperties) == false) {
                return false;
            }
            if (super.getProperties(p) == false) {
                return false;
            }
            TimingLineProperties lp = (TimingLineProperties) p;
            lp.mAxis = this.getAxis();
            lp.mValue = this.getValue();
            lp.mAnchored = this.isAnchored();
            return true;
        }
        
        public boolean setProperties(SGProperties p) {
            if ((p instanceof TimingLineProperties) == false) {
                return false;
            }
            if (super.setProperties(p) == false) {
                return false;
            }
            TimingLineProperties lp = (TimingLineProperties) p;
            this.mAxis = lp.mAxis;
            this.mValue = lp.mValue;
            this.mIsAnchored = lp.mAnchored;
            return true;
        }

        /**
         * The pop-up menu.
         */
        private TimingLinePopupMenu mPopupMenu = null;

        /**
         * 
         * 
         */
        protected TimingLine() {
            super();
            this.init();
        }

        private void init() {
            this.setLineWidth(DEFAULT_LINE_WIDTH, LINE_WIDTH_UNIT);
            this.setLineType(DEFAULT_LINE_TYPE);
            this.setColor(DEFAULT_LINE_COLOR);
            this.setAnchored(DEFAULT_LINE_ANCHORED);

            // setup the stroke object
            this.mStroke.setEndCap(BasicStroke.CAP_BUTT);
            this.mStroke.setJoin(BasicStroke.JOIN_BEVEL);
        }

        /**
         * 
         * @return
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

            String config = SGFigureElementTimingLine.this.mAxisElement
                    .getLocationName(this.mAxis);
            String direction;
            if (this.isHorizontal()) {
        	direction = "X";
            } else {
        	direction = "Y";
            }
            StringBuffer sb = new StringBuffer();
            sb.append(this.mID);
            sb.append(": ");
            sb.append(" Axis=");
            sb.append(config);
            sb.append(", ");
            sb.append(direction);
            sb.append("=");
            sb.append(this.getValue());
            return sb.toString();
        }

        /**
         * 
         * @return
         */
        private ArrayList getAnchorPointList() {
            ArrayList list = new ArrayList();

            Point2D pos0 = new Point2D.Float();
            Point2D pos1 = new Point2D.Float();

            final boolean flag = this.isHorizontal();
            float location = calcLocation(this.mValue, this.mAxis, flag);
            Rectangle2D rect = getGraphRect();
            if (flag) {
                final float y0 = (float) rect.getY();
                final float y1 = y0 + (float) rect.getHeight();
                pos0.setLocation(location, y0);
                pos1.setLocation(location, y1);
            } else {
                final float x0 = (float) rect.getX();
                final float x1 = x0 + (float) rect.getWidth();
                pos0.setLocation(x0, location);
                pos1.setLocation(x1, location);
            }

            list.add(pos0);
            list.add(pos1);

            return list;
        }

        /**
         * Returns a pop-up menu.
         * @return
         *         a pop-up menu
         */
        public JPopupMenu getPopupMenu() {
            TimingLinePopupMenu p = null;
            if (this.mPopupMenu != null) {
                p = this.mPopupMenu;
            } else {
                p = new TimingLinePopupMenu();
                p.init(this);
                this.mPopupMenu = p;
            }
            p.setAnchored(this.isAnchored());
            return p;
        }
        
        /**
         * 
         */
        public int getAxisLocation() {
            return SGFigureElementTimingLine.this.mAxisElement
                    .getLocationInPlane(this.mAxis);
        }

        /**
         * Sets the axis location.
         * 
         * @param location
         *           the axis location to set
         * @return true if succeeded
         */
        public boolean setAxisLocation(final int location) {
            SGAxis axis = SGFigureElementTimingLine.this.mAxisElement.getAxisInPlane(
            		location);
            if (axis == null) {
                return false;
            }
            this.mAxis = axis;
            return true;
        }
        
        private SGTuple2f mStart = new SGTuple2f();
        
        private SGTuple2f mEnd = new SGTuple2f();

        /**
         * Called when the location of data points have changed.
         */
        private boolean setDrawingElementsLocation() {
            final SGAxis axis = this.mAxis;
            final double value = this.mValue;
            final boolean horizontal = this.isHorizontal();

            SGTuple2f[] array = new SGTuple2f[2];
            array[0] = new SGTuple2f();
            array[1] = new SGTuple2f();

            if (calcLocationOfTimingLine(value, axis, horizontal, array[0],
                    array[1]) == false) {
                this.setValid(false);
                return false;
            }
            this.setValid(true);

            this.mStart.setValues(array[0]);
            this.mEnd.setValues(array[1]);
            
//            if (this.setLocation(array) == false) {
//                return false;
//            }
            if (this.setLocation(this.mStart.x, this.mStart.y) == false) {
                return false;
            }

            return true;
        }
        
        public boolean setLocation(final float x, final float y) {
            final float mag = this.getMagnification();
            final float nx = (x - SGFigureElementTimingLine.this.mGraphRectX)
                    / mag;
            final float ny = (y - SGFigureElementTimingLine.this.mGraphRectY)
                    / mag;
            this.mLocation.setValues(nx, ny);
            return true;
        }
        
        private boolean setAxisValue() {
            final SGAxis axis = this.mAxis;
            final boolean horizontal = this.isHorizontal();
            float location;
            if (horizontal) {
                location = this.getMagnification()*this.mLocation.x + SGFigureElementTimingLine.this.mGraphRectX;
            } else {
                location = this.getMagnification()*this.mLocation.y + SGFigureElementTimingLine.this.mGraphRectY;
            }
            double value = SGFigureElementTimingLine.this.calcValue(location, axis, horizontal);
            value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
            if (Double.isNaN(value) || Double.isInfinite(value)) {
//                this.setValid(false);
                return false;
            }
//            this.setValid(true);
            this.setValue(value);
            this.setDrawingElementsLocation();
            return true;
        }

        private boolean mValidFlag = true;

        public boolean isValid() {
            return this.mValidFlag;
        }

        public void setValid(final boolean b) {
            this.mValidFlag = b;
        }

        /**
         * Translate the object.
         * 
         * @param dx
         * @param dy
         */
        public void translate(float dx, float dy) {
            SGAxis axis = this.mAxis;
            final boolean horizontal = this.isHorizontal();
            float location = SGFigureElementTimingLine.this.calcLocation(this
                    .getValue(), axis, horizontal);
            float locationNew;
            if (this.isHorizontal()) {
                locationNew = location + dx;
            } else {
                locationNew = location + dy;
            }
            double value = SGFigureElementTimingLine.this.calcValue(
                    locationNew, axis, horizontal);
            value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
            this.setValue(value);

            this.setDrawingElementsLocation();
        }

        /**
         * 
         * @return
         */
        private boolean isHorizontal() {
            final List<SGAxis> hAxisList = SGFigureElementTimingLine.this.mAxisElement
                    .getHorizontalAxisList();
            final List<SGAxis> pAxisList = SGFigureElementTimingLine.this.mAxisElement
                    .getVerticalAxisList();

            Boolean hFlag = null;
            if (hAxisList.contains(this.mAxis)) {
                hFlag = Boolean.TRUE;
            }
            if (pAxisList.contains(this.mAxis)) {
                hFlag = Boolean.FALSE;
            }
            if (hFlag == null) {
                throw new Error("");
            }

            return hFlag.booleanValue();
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidValue(final int config, final Number value) {
            final SGAxis axis = (config == -1) ? this.mAxis
                    : SGFigureElementTimingLine.this.mAxisElement
                            .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         */
        public void actionPerformed(final ActionEvent e) {
            // final Object source = e.getSource();
            final String command = e.getActionCommand();

            if (command.equals(MENUCMD_PROPERTY)) {
                SGFigureElementTimingLine.this.setPropertiesOfSelectedObjects(this);
            } else {
                notifyToListener(command, e.getSource());
            }
        }

        /**
         * 
         */
        public boolean commit() {
            SGProperties pTemp = this.mTemporaryProperties;
            SGProperties pPresent = this.getProperties();
            if (pTemp.equals(pPresent) == false) {
                this.setChanged(true);
            }
            this.mTemporaryProperties = null;
            this.setDrawingElementsLocation();
            notifyChangeOnCommit();
            repaint();
            return true;
        }

        /**
         * 
         */
        public boolean cancel() {
            if (this.setProperties(this.mTemporaryProperties) == false) {
                return false;
            }
            this.setDrawingElementsLocation();
            notifyChangeOnCancel();
            repaint();
            return true;
        }

        /**
         * 
         */
        public boolean preview() {
            this.setDrawingElementsLocation();
            notifyChange();
            repaint();
            return true;
        }

        /**
         * Returns the property dialog.
         * 
         * @return
         *        a property dialog
         */
        public SGPropertyDialog getPropertyDialog() {
            SGPropertyDialog dg = null;
            if (mPropertyDialog != null) {
                dg = mPropertyDialog;
            } else {
                dg = new SGTimingLineDialog(mDialogOwner, true);
                mPropertyDialog = dg;
            }
            return dg;
        }
        
        /**
         * Returns a list of child nodes.
         * 
         * @return
         *       a list of child nodes
         */
        public ArrayList getChildNodes() {
            return new ArrayList();
        }

        //
        private SGUndoManager mUndoManager = new SGUndoManager(this);

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

        /**
         * 
         */
        public boolean initPropertiesHistory() {
            return this.mUndoManager.initPropertiesHistory();
        }

        public boolean setMementoBackward() {
            if (this.mUndoManager.setMementoBackward() == false) {
                return false;
            }

            setAllDrawingElementsLocation();
            notifyChangeOnUndo();

            return true;
        }

        public boolean setMementoForward() {
            if (this.mUndoManager.setMementoForward() == false) {
                return false;
            }

            setAllDrawingElementsLocation();
            notifyChangeOnUndo();

            return true;
        }

        public boolean undo() {
            return this.setMementoBackward();
        }

        public boolean redo() {
            return this.setMementoForward();
        }

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
         * 
         */
        public void notifyToRoot() {
            SGFigureElementTimingLine.this.notifyToRoot();
        }

        /**
         * 
         * @return
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
        public Element createElement(final Document document, SGExportParameter params) {
            Element element = document.createElement(this.getTagName());
            if (this.writeProperty(element, params) == false) {
                return null;
            }
            return element;
        }

        public String getTagName() {
            return TAG_NAME_TIMING_LINE;
        }

        /**
         * 
         * @param el
         * @param p
         * @return
         */
        public boolean readProperty(final Element el) {

            String str = null;
            Number num = null;

            // line width
            str = el.getAttribute(KEY_LINE_WIDTH);
            if (str.length() != 0) {
                StringBuffer uLineWidth = new StringBuffer();
                num = SGUtilityText.getNumber(str, uLineWidth);
                if (num == null) {
                    return false;
                }
                final float lineWidth = num.floatValue();
                if (this.setLineWidth(lineWidth, uLineWidth.toString()) == false) {
                    return false;
                }
            }

            // line type
            str = el.getAttribute(KEY_LINE_TYPE);
            if (str.length() != 0) {
                num = SGDrawingElementLine.getLineTypeFromName(str);
                if (num == null) {
                    return false;
                }
                final int lineType = num.intValue();
                if (this.setLineType(lineType) == false) {
                    return false;
                }
            }

            // color
            str = el.getAttribute(KEY_COLOR);
            if (str.length() != 0) {
                Color cl = SGUtilityText.parseColorIncludingList(str);
                if (cl == null) {
                	return false;
                }
                if (this.setColor(cl) == false) {
                    return false;
                }
            }

            // axis
            str = el.getAttribute(KEY_AXIS_POSITION);
            if (str.length() != 0) {
                SGAxis axis = SGFigureElementTimingLine.this.mAxisElement
                        .getAxis(str);
                if (axis == null) {
                    return false;
                }
                this.setAxis(axis);
            }

            // value
            str = el.getAttribute(KEY_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double value = num.doubleValue();
                if (this.mAxis.isValidValue(value) == false) {
                    return false;
                }
                this.setValue(value);
            }

            // anchored
            str = el.getAttribute(KEY_ANCHORED);
            if (str.length() != 0) {
                Boolean b = SGUtilityText.getBoolean(str);
                if (b == null) {
                    return false;
                }
                this.setAnchored(b.booleanValue());
            }
            
            return true;
        }

        /**
         * 
         */
        public Object copy() {
            TimingLine el = new TimingLine();
            el.setProperties(this.getProperties());
            el.mTempAxis = mAxisElement.getLocationInPlane(this.mAxis);
            return el;
        }

        private int mTempAxis = -1;

        @Override
        public SGTuple2f getEnd() {
            return this.mEnd;
        }

        @Override
        public SGTuple2f getStart() {
            return this.mStart;
        }

        @Override
        protected SGStroke getStroke() {
            return this.mStroke;
        }

        /**
         * Sets the color.
         * 
         * @param cl
         *           the color to set
         * @return true if succeeded
         */
        public boolean setColor(final Color cl) {
        	if (cl == null) {
        		throw new IllegalArgumentException("cl == null");
        	}
            this.mColor = cl;
            return true;
        }

        /**
         * Sets the line type.
         * 
         * @param type
         *           the line type to set
         * @return true if succeeded
         */
        public boolean setLineType(final int type) {
        	if (SGDrawingElementLine.isValidLineType(type) == false) {
        		return false;
        	}
            this.mStroke.setLineType(type);
            return true;
        }

        @Override
        public boolean setLineWidth(float width) {
            this.mStroke.setLineWidth(width);
            return true;
        }

        @Override
        public boolean setTermPoints(SGTuple2f start, SGTuple2f end) {
            this.mStart.setValues(start);
            this.mEnd.setValues(end);
            return true;
        }

        @Override
        public float getMagnification() {
            return SGFigureElementTimingLine.this.getMagnification();
        }

        @Override
        public boolean setMagnification(final float mag) {
            this.mStroke.setMagnification(mag);
            return true;
        }

        /**
         * Returns whether this timing line is within the range
         * @return true if this timing line is within the range
         */
        public boolean isInsideRange() {
            return this.mAxis.insideRange(this.mValue);
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
                
                if (COM_TIMING_LINE_AXIS.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_TIMING_LINE_AXIS, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAxisLocation(loc) == false) {
                        result.putResult(COM_TIMING_LINE_AXIS, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_TIMING_LINE_AXIS, SGPropertyResults.SUCCEEDED);
                } else if (COM_TIMING_LINE_VALUE.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_TIMING_LINE_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_TIMING_LINE_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setValue(num.doubleValue()) == false) {
                        result.putResult(COM_TIMING_LINE_VALUE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_TIMING_LINE_VALUE, SGPropertyResults.SUCCEEDED);
                } else if (COM_TIMING_LINE_WIDTH.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_TIMING_LINE_WIDTH,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineWidth(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_TIMING_LINE_WIDTH,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_TIMING_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
                } else if (COM_TIMING_LINE_TYPE.equalsIgnoreCase(key)) {
                    final Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                    if (type == null) {
                        result.putResult(COM_TIMING_LINE_TYPE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineType(type.intValue()) == false) {
                        result.putResult(COM_TIMING_LINE_TYPE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_TIMING_LINE_TYPE, SGPropertyResults.SUCCEEDED);
                } else if (COM_TIMING_LINE_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.getColor(value);
                    if (cl != null) {
                        if (this.setColor(cl) == false) {
                            result.putResult(COM_TIMING_LINE_COLOR,
                                    SGPropertyResults.INVALID_INPUT_VALUE);
                            continue;
                        }
                    } else {
                    	cl = SGUtilityText.parseColor(value);
    					if (cl == null) {
    						result.putResult(COM_TIMING_LINE_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
    					if (this.setColor(cl) == false) {
    						result.putResult(COM_TIMING_LINE_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
                    }
                    result.putResult(COM_TIMING_LINE_COLOR, SGPropertyResults.SUCCEEDED);
                } else if (COM_TIMING_LINE_ANCHORED.equalsIgnoreCase(key)) {
                    Boolean b = SGUtilityText.getBoolean(value);
                    if (b == null) {
                        result.putResult(COM_TIMING_LINE_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAnchored(b.booleanValue()) == false) {
                        result.putResult(COM_TIMING_LINE_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_TIMING_LINE_ANCHORED, SGPropertyResults.SUCCEEDED);
                }
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
        	return SGCommandUtility.createCommandString(COM_TIMING_LINE, 
    				Integer.toString(this.mID), this.getCommandPropertyMap(params));
        }

        public boolean writeProperty(final Element el, SGExportParameter params) {
        	SGPropertyMap map = this.getPropertyFileMap(params);
        	map.setToElement(el);
            return true;
        }

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
    	@Override
        public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
        	SGPropertyMap map = new SGPropertyMap();
            this.addProperties(map, COM_TIMING_LINE_AXIS, COM_TIMING_LINE_VALUE, 
            		COM_TIMING_LINE_WIDTH, COM_TIMING_LINE_TYPE, COM_TIMING_LINE_COLOR, 
            		COM_TIMING_LINE_ANCHORED);
			return map;
		}
    	
    	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = new SGPropertyMap();
        	this.addProperties(map, KEY_AXIS_POSITION, KEY_VALUE, KEY_LINE_WIDTH, KEY_LINE_TYPE, 
        			KEY_COLOR, KEY_ANCHORED);
        	return map;
    	}

    	private void addProperties(SGPropertyMap map, String axisKey, String valueKey,
    			String lineWidthKey, String lineTypeKey, String colorKey, String anchorKey) {
            SGPropertyUtility.addProperty(map, axisKey, 
            		mAxisElement.getLocationName(this.mAxis));
            SGPropertyUtility.addProperty(map, valueKey, this.getValue());
            SGPropertyUtility.addProperty(map, lineWidthKey, 
            		SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
            		LINE_WIDTH_UNIT);
            SGPropertyUtility.addProperty(map, lineTypeKey, 
            		SGDrawingElementLine.getLineTypeName(this.getLineType()));
            SGPropertyUtility.addProperty(map, colorKey, this.getColor());
            SGPropertyUtility.addProperty(map, anchorKey, this.isAnchored());
    	}

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
    }

    public static class TimingElementProperties extends SGProperties {

        ArrayList visibleTimingElementList = new ArrayList();

        /**
         * 
         * 
         */
        public TimingElementProperties() {
            super();
        }

        public void dispose() {
        	super.dispose();
            this.visibleTimingElementList.clear();
            this.visibleTimingElementList = null;
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof TimingElementProperties) == false) {
                return false;
            }

            TimingElementProperties p = (TimingElementProperties) obj;
            if (p.visibleTimingElementList
                    .equals(this.visibleTimingElementList) == false) {
                return false;
            }

            return true;
        }

        /**
         * 
         */
        public String toString() {
        	StringBuffer sb = new StringBuffer();
        	sb.append('[');
        	sb.append(this.visibleTimingElementList.toString());
        	sb.append(']');
        	return sb.toString();
        }

    }

    /**
     * @author okumura
     */
    public static class TimingLineProperties extends
            SGDrawingElementLine.LineProperties {

        protected double mValue;

        protected SGAxis mAxis;
        
        protected boolean mAnchored;

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof TimingLineProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            TimingLineProperties p = (TimingLineProperties) obj;
            if (p.mValue != this.mValue) {
                return false;
            }
//            if (p.axis != this.axis)
//                return false;
            if (SGUtility.equals(p.mAxis, this.mAxis) == false) {
                return false;
            }
            if (p.mAnchored != this.mAnchored)
                return false;

            return true;
        }

        public void setValue(final double value) {
            this.mValue = value;
        }

        public void setAxis(final SGAxis Axis) {
            this.mAxis = Axis;
        }
        
        public void setAnchored(final boolean value) {
            this.mAnchored = value;
        }

        public Double getValue() {
            return Double.valueOf(this.mValue);
        }

        public SGAxis getAxis() {
            return this.mAxis;
        }
        
        public Boolean getAnchored() {
            return Boolean.valueOf(this.mAnchored);
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
                if (mPropertyDialog == null) {
                    mPropertyDialog = new SGTimingLineDialog(mDialogOwner, true);
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
        if (child == null) {
            // get pairs of a key and a value to add a new label
            String sAxis = null;
            String sValue = null;
            Iterator itr = map.getKeyIterator();
            while (itr.hasNext()) {
                Object keyObj = itr.next();
                String key = keyObj.toString();
                if (COM_TIMING_LINE_AXIS.equalsIgnoreCase(key)) {
                	sAxis = map.getValueString(key);
                } else if (COM_TIMING_LINE_VALUE.equalsIgnoreCase(key)) {
                	sValue = map.getValueString(key);
                }
            }
            if (sAxis == null) {
                return null;
            }
            if (sValue == null) {
                return null;
            }
            final int location = SGUtility.getAxisLocation(sAxis);
            Double value = SGUtilityText.getDouble(sValue);
            if (location == -1) {
                return null;
            }
            if (value == null) {
                return null;
            }
        	if (SGUtility.isValidPropertyValue(value.doubleValue()) == false) {
                return null;
        	}

            if (this.addTimingLine(id, location, value.doubleValue()) == false) {
                return null;
            }
            
            child = this.getChild(id);
        }

        // set properties to the child object
        SGPropertyResults result = child.setProperties(map);

        return result;
    }
}
