package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
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
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
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
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementAxisBreak.AxisBreakSymbolProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisBreakConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class managing axis break symbols.
 */
public class SGFigureElementAxisBreak extends SGFigureElement2D implements
        SGIFigureElementAxisBreak, SGIAxisBreakConstants {

    /**
     * 
     */
    private SGIFigureElementAxis mAxisElement = null;

    /**
     * The property dialog of axis break symbols.
     */
    private SGPropertyDialog mPropertyDialog = null;

    /**
     * 
     * 
     */
    public SGFigureElementAxisBreak() {
        super();
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
        this.mAxisElement = null;
    }

    /**
     * 
     * @return
     */
    public String getClassDescription() {
        return "Axis Break Symbols";
    }

    /**
     * 
     */
    public boolean synchronize(final SGIFigureElement element, final String msg) {

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
            AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            if (! el.isAnchored()) {
                if (el.setDrawingElementsLocation() == false) {
                    continue;
                }
            } else {
                if (el.setAxisValue() == false) {
                    continue;
                }
            }
            el.setChanged(true);
            this.setChanged(true);
        }

        return true;
    }

    /**
     * Synchronize the element given by the argument.
     * 
     * @param element
     *            An object to be synchronized.
     */
    public boolean synchronizeArgument(final SGIFigureElement element,
            String msg) {
        // this shouldn't happen
        throw new Error();
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
     */
    public String getTagName() {
        return TAG_NAME_AXIS_BREAK;
    }

    /**
     * 
     */
    public boolean writeProperty(Element el, SGExportParameter params) {
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
                .getElementsByTagName(SGIAxisBreakConstants.TAG_NAME_AXIS_BREAK_SYMBOL);
        for (int ii = 0; ii < nList.getLength(); ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;
                AxisBreakSymbol abs = new AxisBreakSymbol();
                if (abs.readProperty(el) == false) {
                    return false;
                }
                abs.initPropertiesHistory();
                this.addToList(abs);
            }
        }

        return true;
    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        super.setGraphRect(x, y, width, height);
        this.setLocationOfAllDrawingElements();

        return true;
    }

    /**
     * 
     */
    private boolean setLocationOfAllDrawingElements() {
        List list = this.mChildList;
        for (int ii = 0; ii < list.size(); ii++) {
            AxisBreakSymbol abs = (AxisBreakSymbol) list.get(ii);
            if (! abs.isAnchored()) {
                abs.setDrawingElementsLocation();
            } else {
                abs.setAxisValue();
                if (abs.setDrawingElementsLocation() == false) {
                    continue;
                }
            }
        }
        return true;
    }

//    public boolean zoom(final float ratio) {
//        super.zoom(ratio);
//
//        List list = this.mChildList;
//        for (int ii = 0; ii < list.size(); ii++) {
//            final AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
//            el.zoom(ratio);
//        }
//
//        return true;
//    }

    /**
     * 
     */
    public void paintGraphics(Graphics g, boolean clip) {

        final Graphics2D g2d = (Graphics2D) g;

        // draw all visible symbols
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            el.paint(g2d);
            // if( el.mFrameFlag )
            // {
            // el.drawBoundingBox(g2d);
            // }
        }

        // draw symbols around all objects
        if (this.mSymbolsVisibleFlagAroundAllObjects) {
            for (int ii = 0; ii < list.size(); ii++) {
                AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
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
                AxisBreakSymbol el = (AxisBreakSymbol) fList.get(ii);
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

        // axis symbol
        List<SGIChildObject> abList = this.getVisibleChildList();
        for (int ii = 0; ii < abList.size(); ii++) {
            AxisBreakSymbol abs = (AxisBreakSymbol) abList.get(ii);
            if (abs.isValid() == false) {
                continue;
            }

            Element elAbs = abs.createElement(document, params);
            if (elAbs == null) {
                return null;
            }
            el.appendChild(elAbs);
        }

        return new Element[] { el };
    }

    /**
     * Inserts an axis break symbol at a given point with default axes.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final float x, final float y) {
        if (this.getGraphRect().contains(x, y) == false) {
            return false;
        }
        // get axes
        SGAxis xAxis = SGFigureElementAxisBreak.this.mAxisElement
                .getAxis(DEFAULT_AXIS_BREAK_HORIZONTAL_AXIS);
        SGAxis yAxis = SGFigureElementAxisBreak.this.mAxisElement
                .getAxis(DEFAULT_AXIS_BREAK_VERTICAL_AXIS);
        if (xAxis == null || yAxis == null) {
            return false;
        }
        return this.addAxisBreakSymbol(this.assignChildId(), x, y, xAxis, yAxis);
    }

    /**
     * Inserts an axis break symbol at a given point with given axes.
     * 
     * @param id
     *          the id to set
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
    private boolean addAxisBreakSymbol(final int id, final float x, 
            final float y, final SGAxis xAxis, final SGAxis yAxis) {

        AxisBreakSymbol el = new AxisBreakSymbol(DEFAULT_AXIS_BREAK_LENGTH,
                AXIS_BREAK_LENGTH_UNIT, DEFAULT_AXIS_BREAK_INTERVAL,
                AXIS_BREAK_INTERVAL_UNIT, DEFAULT_AXIS_BREAK_DISTORTION,
                DEFAULT_AXIS_BREAK_ANGLE, DEFAULT_AXIS_BREAK_FOR_HORIZONTAL);
        el.setInnerColor(DEFAULT_AXIS_BREAK_INNER_COLOR);
        el.setLineColor(DEFAULT_AXIS_BREAK_LINE_COLOR);
        el.setLineWidth(DEFAULT_AXIS_BREAK_LINE_WIDTH, LINE_WIDTH_UNIT);
        el.setMagnification(this.mMagnification);
        el.mXAxis = xAxis;
        el.mYAxis = yAxis;
        el.setLocation(x, y);
        el.setAxisValue();

        // add to the list
        if (this.addToList(id, el) == false) {
            return false;
        }

        // initialize history
        el.initPropertiesHistory();

        this.setChanged(true);
        this.notifyToRoot();

        return true;
    }

    /**
     * Inserts a axis break symbol with given axis value for default axes.
     * 
     * @param id
     *          the ID to set
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addAxisBreakSymbol(final int id, final double x, final double y) {
        return this.addAxisBreakSymbol(id, x, y, DEFAULT_AXIS_BREAK_HORIZONTAL_AXIS, 
                DEFAULT_AXIS_BREAK_VERTICAL_AXIS);
    }
    
    /**
     * Inserts a axis break symbol with given axis value for given axes.
     * 
     * @param id
     *          the ID to set
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
    public boolean addAxisBreakSymbol(final int id, final double x, 
            final double y, final String xAxisLocation, final String yAxisLocation) {
        
        // get axes
        String xAxisName = (xAxisLocation != null) ? xAxisLocation : DEFAULT_AXIS_BREAK_HORIZONTAL_AXIS;
        String yAxisName = (yAxisLocation != null) ? yAxisLocation : DEFAULT_AXIS_BREAK_VERTICAL_AXIS;
        SGAxis xAxis = this.mAxisElement.getAxis(xAxisName);
        SGAxis yAxis = this.mAxisElement.getAxis(yAxisName);
        if (xAxis == null || yAxis == null) {
            return false;
        }
        if (xAxis.isValidValue(x) == false) {
            return false;
        }
        if (yAxis.isValidValue(y) == false) {
            return false;
        }

        // insert a symbol
        final float posX = this.calcLocation(x, xAxis, true);
        final float posY = this.calcLocation(y, yAxis, false);
        if (this.addAxisBreakSymbol(id, posX, posY, xAxis, yAxis) == false) {
            return false;
        }
        
        return true;
    }

    /**
     * set all drawing element location
     */
    protected boolean setAllDrawingElementsLocation() {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            final AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            if (el.setDrawingElementsLocation() == false) {
                return false;
            }
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
                final AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
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
        // Axis Break Symbols
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            if (this.clickDrawingElements(el, e)) {
                return true;
            }
        }

        return false;
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setTemporaryPropertiesOfFocusedObjects() {
//    	List<SGISelectable> list = this.getFocusedObjectsList();
//        for (int ii = 0; ii < list.size(); ii++) {
//            AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
//            el.mTemporaryProperties = el.getProperties();
//        }
//        return true;
//    }

    /**
     * Returns the class object of property dialog observer.
     * 
     * @return the class object
     */
    @Override
    public Class<?> getPropertyDialogObserverClass() {
    	return AxisBreakSymbol.class;
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
     * 
     */
    private boolean clickDrawingElements(final AxisBreakSymbol el,
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
        final int x = e.getX();
        final int y = e.getY();

        // Axis Break Symbols
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
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
     * @param e
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
     * Updates changed flag of focused objects.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean updateChangedFlag() {
    	List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
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
     * 
     * @param e
     */
    public boolean onMouseReleased(final MouseEvent e) {

        // Axis Break Symbol
    	List<SGISelectable> list = this.getFocusedObjectsList();
        for (int ii = 0; ii < list.size(); ii++) {
            AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            Rectangle2D rect = el.getElementBounds();
            if (rect.contains(e.getPoint())) {
                this.setMouseCursor(Cursor.HAND_CURSOR);
            } else {
                this.setMouseCursor(Cursor.DEFAULT_CURSOR);
            }
        }

        this.mDraggableFlag = false;
        this.mDraggedDirection = null;

        return true;
    }

    /**
     * 
     */
    public boolean setMouseCursor(final int x, final int y) {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            final boolean flag = el.contains(x, y);
            // el.mFrameFlag = flag;
            if (flag) {
                this.setMouseCursor(Cursor.HAND_CURSOR);
                return true;
            }
        }

        return false;
    }

    /**
     * 
     */
    public boolean getMarginAroundGraphRect(final SGTuple2f topAndBottom,
            final SGTuple2f leftAndRight) {

        if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
            return false;
        }

        List<SGIChildObject> list = this.getVisibleChildList();

        ArrayList axisBreakRectList = new ArrayList();
        for (int ii = 0; ii < list.size(); ii++) {
            AxisBreakSymbol el = (AxisBreakSymbol) list.get(ii);
            Rectangle2D rect = el.getElementBounds();
            axisBreakRectList.add(rect);
        }

        Rectangle2D rectAxisBreakSymbols = null;
        if (axisBreakRectList.size() != 0) {
            rectAxisBreakSymbols = SGUtility.createUnion(axisBreakRectList);
        }

        Rectangle2D gRect = this.getGraphRect();

        ArrayList rectList = new ArrayList();
        rectList.add(gRect);

        if (rectAxisBreakSymbols != null) {
            rectList.add(rectAxisBreakSymbols);
        }

        Rectangle2D rectAll = SGUtility.createUnion(rectList);

        final float top = (float) (gRect.getY() - rectAll.getY());
        final float bottom = (float) ((rectAll.getY() + rectAll.getHeight()) - (gRect
                .getY() + gRect.getHeight()));
        final float left = (float) (gRect.getX() - rectAll.getX());
        final float right = (float) ((rectAll.getX() + rectAll.getWidth()) - (gRect
                .getX() + gRect.getWidth()));

        topAndBottom.x += top;
        topAndBottom.y += bottom;
        leftAndRight.x += left;
        leftAndRight.y += right;

        return true;
    }

    /**
     * 
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            AxisProperties ap = (AxisProperties) mList.get(ii);
            set.addAll(ap.visibleAxisBreakSymbolList);
        }

        return set;
    }

    /**
     * 
     */
    public static class AxisProperties extends SGProperties {
        List visibleAxisBreakSymbolList = new ArrayList();

        AxisProperties() {
        }

        public void dispose() {
        	super.dispose();
            this.visibleAxisBreakSymbolList.clear();
            this.visibleAxisBreakSymbolList = null;
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof AxisProperties) == false) {
                return false;
            }

            AxisProperties p = (AxisProperties) obj;
            if (p.visibleAxisBreakSymbolList
                    .equals(this.visibleAxisBreakSymbolList) == false) {
                return false;
            }

            return true;
        }
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        AxisProperties wp = new AxisProperties();
        wp.visibleAxisBreakSymbolList = this.getVisibleChildList();
        return wp;
    }

    /**
     * 
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof AxisProperties) == false)
            return false;

        AxisProperties wp = (AxisProperties) p;
        final boolean flag = this
                .setVisibleChildList(wp.visibleAxisBreakSymbolList);
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
            AxisBreakSymbol el = (AxisBreakSymbol) cList.get(ii);

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
            if (obj instanceof AxisBreakSymbol) {
                AxisBreakSymbol abs = (AxisBreakSymbol) obj;

                // translate the instance to be pasted
                abs.translate(ox, oy);

                SGProperties p = abs.getProperties();

                AxisBreakSymbol el = new AxisBreakSymbol();
                el.setMagnification(mag);
                el.setProperties(p);

                // el.mXAxis = this.mAxisElement.getAxisInCube(abs.mTempXAxis);
                // el.mYAxis = this.mAxisElement.getAxisInCube(abs.mTempYAxis);
                el.mXAxis = this.mAxisElement.getAxisInPlane(abs.mTempXAxis);
                el.mYAxis = this.mAxisElement.getAxisInPlane(abs.mTempYAxis);

                el.setDrawingElementsLocation();
                el.setAxisValue();

                // add to the list
                this.addToList(el);

                // initialize history
                el.initPropertiesHistory();

                cnt++;
            }
        }

        if (cnt != 0) {
            this.setChanged(true);
        }

        return true;
    }

    private static class AxisBreakSymbolPopupMenu extends JPopupMenu {
        
        private static final long serialVersionUID = -2688401892783919136L;
        
        private JCheckBoxMenuItem anchoredCheckBox;

        void init(AxisBreakSymbol abs) {
            this.setBounds(0, 0, 100, 100);

            StringBuffer sb = new StringBuffer();
            sb.append("  -- AxisBreak: ");
            sb.append(abs.getID());
            sb.append(" --");
            
            add(new JLabel(sb.toString()));
            this.addSeparator();

            SGUtility.addArrangeItems(this, abs);

            this.addSeparator();

            SGUtility.addItem(this, abs, MENUCMD_CUT);
            SGUtility.addItem(this, abs, MENUCMD_COPY);

            this.addSeparator();

            SGUtility.addItem(this, abs, MENUCMD_DELETE);
            SGUtility.addItem(this, abs, MENUCMD_DUPLICATE);

            this.addSeparator();
            
            this.anchoredCheckBox = new JCheckBoxMenuItem(MENUCMD_ANCHORED);
            add(this.anchoredCheckBox);
            this.anchoredCheckBox.addActionListener(abs);

            this.addSeparator();

            SGUtility.addItem(this, abs, MENUCMD_PROPERTY);
        }
        
        void setAnchored(final boolean anchored) {
            this.anchoredCheckBox.setSelected(anchored);
        }
    }
    
    private class AxisBreakSymbol extends SGDrawingElementAxisBreak2D implements
            ActionListener, SGIUndoable, SGIChildObject, SGIMovable, SGICopiable, SGINode, 
            SGIAxisBreakDialogObserver {

        /**
         * ID number.
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
         * 
         */
        private SGAxis mXAxis = null;

        /**
         * 
         */
        private SGAxis mYAxis = null;

        /**
         * 
         */
        private double mXValue;

        /**
         * 
         */
        private double mYValue;
        
        /**
         * 
         */
        private boolean mIsAnchored;

        /**
         * 
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * The pop-up menu.
         */
        private AxisBreakSymbolPopupMenu mPopupMenu = null;

        /**
         * 
         */
        AxisBreakSymbol() {
            super();
        }

        /**
         * 
         */
        AxisBreakSymbol(final float length, final String lengthUnit,
                final float interval, final String intervalUnit,
                final float dist, final float angle, final boolean horizontal) {
            super(length, lengthUnit, interval, intervalUnit, dist, angle,
                    horizontal);
        }

        /**
         * Dispose this object.
         * Do not call any method of this object after this method is called.
         */
        public void dispose() {
            super.dispose();
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

        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(this.getClass().getSimpleName());
            sb.append(":");
            sb.append(this.mID);
            return sb.toString();
        }

        /**
         * 
         */
        public int getXAxisLocation() {
            return SGFigureElementAxisBreak.this.mAxisElement
                    .getLocationInPlane(this.mXAxis);
        }

        /**
         * 
         */
        public int getYAxisLocation() {
            return SGFigureElementAxisBreak.this.mAxisElement
                    .getLocationInPlane(this.mYAxis);
        }

        /**
		 * Sets the x-axis.
		 * 
		 * @param location
		 *            the axis location
		 * @return true if succeeded
		 */
		public boolean setXAxisLocation(final int location) {
			if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
					&& location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
				return false;
			}
			SGAxis axis = SGFigureElementAxisBreak.this.mAxisElement
					.getAxisInPlane(location);
			if (axis == null) {
				return false;
			}
			this.mXAxis = axis;
			return true;
		}

		/**
		 * Sets the y-axis.
		 * 
		 * @param location
		 *            the axis location
		 * @return true if succeeded
		 */
		public boolean setYAxisLocation(final int location) {
			if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
					&& location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
				return false;
			}
			SGAxis axis = SGFigureElementAxisBreak.this.mAxisElement
					.getAxisInPlane(location);
			if (axis == null) {
				return false;
			}
			this.mYAxis = axis;
			return true;
		}

        /**
		 * 
		 */
        public double getXValue() {
            return this.mXValue;
        }

        /**
		 * 
		 */
        public double getYValue() {
            return this.mYValue;
        }

        /**
         * Sets the axis value for the x-coordinate.
         * 
         * @param value
         *           the value for the x-coordinate
         * @return true if succeeded
         */
        public boolean setXValue(final double value) {
        	if (this.mXAxis.isValidValue(value) == false) {
        		return false;
        	}
            this.mXValue = value;
            return true;
        }

        /**
         * Sets the axis value for the y-coordinate.
         * 
         * @param value
         *           the value fot the y-coordinate
         * @return true if succeeded
         */
        public boolean setYValue(final double value) {
        	if (this.mYAxis.isValidValue(value) == false) {
        		return false;
        	}
            this.mYValue = value;
            return true;
        }

        /**
         * Returns whether the x value is valid.
         * 
         * @param location
         *           the axis location
         * @param value
         *           the x value
         * @return true if valid
         */
        public boolean hasValidXValue(final int location, final Number value) {
            final SGAxis axis = (location == -1) ? this.mXAxis
                    : SGFigureElementAxisBreak.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getXValue();
            return axis.isValidValue(v);
        }

        /**
         * Returns whether the y value is valid.
         * 
         * @param location
         *           the axis location
         * @param value
         *           the y value
         * @return true if valid
         */
        public boolean hasValidYValue(final int location, final Number value) {
            final SGAxis axis = (location == -1) ? this.mYAxis
                    : SGFigureElementAxisBreak.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getYValue();
            return axis.isValidValue(v);
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
        public Object copy() {
            AxisBreakSymbol el = new AxisBreakSymbol();
            el.setMagnification(this.getMagnification());
            el.setProperties(this.getProperties());
            el.setLocation(this.getX(), this.getY());
            // el.mTempXAxis = SGFigureElementAxisBreak.this.mAxisElement
            // .getLocationInCube(this.mXAxis);
            // el.mTempYAxis = SGFigureElementAxisBreak.this.mAxisElement
            // .getLocationInCube(this.mYAxis);
            el.mTempXAxis = mAxisElement.getLocationInPlane(this.mXAxis);
            el.mTempYAxis = mAxisElement.getLocationInPlane(this.mYAxis);
            el.setAnchored(this.isAnchored());
            return el;
        }

        private int mTempXAxis = -1;

        private int mTempYAxis = -1;

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
                dg = new SGAxisBreakDialog(mDialogOwner, true);
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
            SGIFigureElementAxis aElement = SGFigureElementAxisBreak.this.mAxisElement;
            String xAxis = aElement.getLocationName(this.mXAxis);
            String yAxis = aElement.getLocationName(this.mYAxis);
            StringBuffer sb = new StringBuffer();
            sb.append(this.mID);
            sb.append(": ");
            sb.append(" AxisX=");
            sb.append(xAxis.toString());
            sb.append(", AxisY=");
            sb.append(yAxis.toString());
            sb.append(", X=");
            sb.append(this.getXValue());
            sb.append(", Y=");
            sb.append(this.getYValue());
            return sb.toString();
        }

        /**
         * Returns a pop-up menu.
         * @return
         *         a pop-up menu
         */
        public JPopupMenu getPopupMenu() {
            AxisBreakSymbolPopupMenu p = null;
            if (this.mPopupMenu != null) {
                p = this.mPopupMenu;
            } else {
                p = new AxisBreakSymbolPopupMenu();
                p.init(this);
                this.mPopupMenu = p;
            }
            p.setAnchored(this.isAnchored());
            return p;
        }
        
//        /**
//         * Create a pop-up menu.
//         * @return
//         *         a pop-up menu
//         */
//        private JPopupMenu createPopupMenu() {
//            JPopupMenu p = new JPopupMenu();
//
//            p.setBounds(0, 0, 100, 100);
//
//            StringBuffer sb = new StringBuffer();
//            sb.append("  -- AxisBreak: ");
//            sb.append(this.getID());
//            sb.append(" --");
//            
//            p.add(new JLabel(sb.toString()));
//            p.addSeparator();
//
//            SGUtility.addItem(p, this, MENUCMD_BRING_TO_FRONT);
//            SGUtility.addItem(p, this, MENUCMD_BRING_FORWARD);
//            SGUtility.addItem(p, this, MENUCMD_SEND_BACKWARD);
//            SGUtility.addItem(p, this, MENUCMD_SEND_TO_BACK);
//
//            p.addSeparator();
//
//            SGUtility.addItem(p, this, MENUCMD_CUT);
//            SGUtility.addItem(p, this, MENUCMD_COPY);
//            SGUtility.addItem(p, this, MENUCMD_PASTE);
//
//            p.addSeparator();
//
//            SGUtility.addItem(p, this, MENUCMD_DELETE);
//            SGUtility.addItem(p, this, MENUCMD_DUPLICATE);
//
//            p.addSeparator();
//
//            SGUtility.addItem(p, this, MENUCMD_PROPERTY);
//
//            return p;
//        }

        /**
         * Returns the location of this symbol.
         * 
         * @return the location of this symbol
         */
        public SGTuple2f getLocation() {
            final float basex = super.getX();
            final float basey = super.getY();
            final float x = this.getMagnification() * basex
                    + SGFigureElementAxisBreak.this.mGraphRectX;
            final float y = this.getMagnification() * basey
                    + SGFigureElementAxisBreak.this.mGraphRectY;
            return new SGTuple2f(x, y);
        }

        /**
         * Returns the x coordinate of the location of this symbol.
         * 
         * @return the x coordinate of the location of this symbol 
         */
        public float getX() {
            return this.getLocation().x;
        }

        /**
         * Returns the y coordinate of the location of this symbol.
         * 
         * @return the y coordinate of the location of this symbol 
         */
        public float getY() {
            return this.getLocation().y;
        }

        /**
         * Sets the location of this symbol.
         * 
         * @param x
         *          the x coordinate to set
         * @param y
         *          the y coordinate to set
         * @return true if succeeded
         */
        public boolean setLocation(final float x, final float y) {
            final float mag = this.getMagnification();
            final float nx = (x - SGFigureElementAxisBreak.this.mGraphRectX)
                    / mag;
            final float ny = (y - SGFigureElementAxisBreak.this.mGraphRectY)
                    / mag;
            super.setLocation(nx, ny);
            return true;
        }

        /**
         * Translates this object.
         * 
         * @param dx
         *           the x-axis displacement
         * @param dy
         *           the y-axis displacement
         */
        public void translate(final float dx, final float dy) {
            this.setLocation(this.getX() + dx, this.getY() + dy);
            this.setAxisValue();
            this.setDrawingElementsLocation();
        }

        /**
         * 
         * @return
         */
        private ArrayList getAnchorPointList() {
            ArrayList list = new ArrayList();

            Shape array[] = this.getShapeArray();
            if (array.length==0) {
                return list;
            }
            Line2D line1 = (Line2D) array[1];
            Line2D line2 = (Line2D) array[3];

            Point2D c1e = line1.getP1(); // mCurve1 end
            Point2D c2s = line1.getP2(); // mCurve2 start
            Point2D c2e = line2.getP1(); // mCurve2 end
            Point2D c1s = line2.getP2(); // mCurve1 start
            Point2D c1m = new Point2D.Float(
                    (float) (c1s.getX() + c1e.getX()) / 2.0f, (float) (c1s
                            .getY() + c1e.getY()) / 2.0f);
            Point2D c2m = new Point2D.Float(
                    (float) (c2s.getX() + c2e.getX()) / 2.0f, (float) (c2s
                            .getY() + c2e.getY()) / 2.0f);
            list.add(c1s);
            list.add(c1m);
            list.add(c1e);
            list.add(c2s);
            list.add(c2m);
            list.add(c2e);

            return list;
        }

        /**
         * 
         * @param e
         */
        public void actionPerformed(final ActionEvent e) {
            final String command = e.getActionCommand();

            if (command.equals(MENUCMD_PROPERTY)) {
                SGFigureElementAxisBreak.this.setPropertiesOfSelectedObjects(this);
            } else {
                notifyToListener(command, e.getSource());
            }

        }

        private boolean mValidFlag = true;

        public boolean isValid() {
            return this.mValidFlag;
        }

        public void setValid(final boolean b) {
            this.mValidFlag = b;
        }

        /**
         * 
         */
        public boolean setDrawingElementsLocation() {
            final float x = calcLocation(this.mXValue, this.mXAxis, true);
            final float y = calcLocation(this.mYValue, this.mYAxis, false);

            if (Float.isNaN(x) || Float.isNaN(y)) {
                this.setValid(false);
                return false;
            }
            this.setValid(true);

            this.setLocation(x, y);
            return true;
        }

        /**
         * 
         * @return
         */
        public boolean setAxisValue() {
            final double xValue = calcValue(this.getX(), this.mXAxis, true);
            final double yValue = calcValue(this.getY(), this.mYAxis, false);

            this.mXValue = SGUtilityNumber.getNumberInRangeOrder(xValue, this.mXAxis);
            this.mYValue = SGUtilityNumber.getNumberInRangeOrder(yValue, this.mYAxis);

            return true;
        }

        /**
         * @return
         */
        public String getTagName() {
            return TAG_NAME_AXIS_BREAK_SYMBOL;
        }

        /**
         * 
         * @param document
         * @return
         */
        public Element createElement(final Document document, SGExportParameter params) {
            Element el = document.createElement(this.getTagName());
            if (this.writeProperty(el, params) == false) {
                return null;
            }
            return el;
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

            // x axis
            str = el.getAttribute(KEY_X_AXIS_POSITION);
            if (str.length() != 0) {
                SGAxis xAxis = SGFigureElementAxisBreak.this.mAxisElement
                        .getAxis(str);
                if (xAxis == null) {
                    return false;
                }
                this.mXAxis = xAxis;
            }

            // y axis
            str = el.getAttribute(KEY_Y_AXIS_POSITION);
            if (str.length() != 0) {
                SGAxis yAxis = SGFigureElementAxisBreak.this.mAxisElement
                        .getAxis(str);
                if (yAxis == null) {
                    return false;
                }
                this.mYAxis = yAxis;
            }

            // x value
            str = el.getAttribute(SGIAxisBreakConstants.KEY_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double xValue = num.doubleValue();
                if (this.mXAxis.isValidValue(xValue) == false) {
                    return false;
                }
                if (this.setXValue(xValue) == false) {
                    return false;
                }
            }

            // y value
            str = el.getAttribute(SGIAxisBreakConstants.KEY_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double yValue = num.doubleValue();
                if (this.mYAxis.isValidValue(yValue) == false) {
                    return false;
                }
                if (this.setYValue(yValue) == false) {
                    return false;
                }
            }
            
            // anchored
            str = el.getAttribute(SGIAxisBreakConstants.KEY_ANCHORED);
            if (str.length() != 0) {
                final Boolean b = SGUtilityText.getBoolean(str);
                if (b == null) {
                    return false;
                }
                if (this.setAnchored(b.booleanValue()) == false) {
                    return false;
                }
                this.setDrawingElementsLocation();
            }

            return true;
        }

        // create temporary object
        public boolean prepare() {
            this.mTemporaryProperties = this.getProperties();
            return true;
        }

        /**
         * 
         */
        public boolean commit() {
            SGProperties p = this.getProperties();
            if (p == null) {
                return false;
            }
            if (p.equals(this.mTemporaryProperties) == false) {
                this.setChanged(true);
            }
            if (this.create() == false) {
                return false;
            }
            if (this.setDrawingElementsLocation() == false) {
                return false;
            }
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
            if (this.create() == false) {
                return false;
            }
            if (this.setDrawingElementsLocation() == false) {
                return false;
            }
            notifyChangeOnCancel();
            repaint();
            return true;
        }

        /**
         * 
         */
        public boolean preview() {
            if (this.create() == false) {
                return false;
            }
            if (this.setDrawingElementsLocation() == false) {
                return false;
            }
            notifyChange();
            repaint();
            return true;
        }

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

        /**
         * 
         */
        public boolean setMementoBackward() {
            if (this.mUndoManager.setMementoBackward() == false) {
                return false;
            }

            this.create();
            this.setDrawingElementsLocation();

            return true;
        }

        /**
         * 
         */
        public boolean setMementoForward() {
            if (this.mUndoManager.setMementoForward() == false) {
                return false;
            }

            this.create();
            this.setDrawingElementsLocation();

            return true;
        }

        /**
         * undo
         */
        public boolean undo() {
            return this.setMementoBackward();
        }

        /**
         * redo
         */
        public boolean redo() {
            return this.setMementoForward();
        }

        /**
         * 
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
         * 
         */
        public boolean isChanged() {
            return this.mUndoManager.isChanged();
        }

        public void setChanged(final boolean b) {
            this.mUndoManager.setChanged(b);
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
         * 
         */
        public void notifyToRoot() {
            SGFigureElementAxisBreak.this.notifyToRoot();
        }

        /**
         * 
         */
        public boolean setProperties(SGProperties p) {

            if ((p instanceof AxisBreakSymbolWithAxesProperties) == false)
                return false;

            if (super.setProperties(p) == false)
                return false;

            AxisBreakSymbolWithAxesProperties ap = (AxisBreakSymbolWithAxesProperties) p;

            final Double x = ap.getXValue();
            if (x == null) {
                return false;
            }
            this.mXValue = x.doubleValue();

            final Double y = ap.getYValue();
            if (y == null) {
                return false;
            }
            this.mYValue = y.doubleValue();

            this.mXAxis = ap.mXAxis;
            this.mYAxis = ap.mYAxis;

            final Boolean anchored = ap.getAnchored();
            if (anchored == null)
                return false;
            this.mIsAnchored = anchored.booleanValue();
            
            return true;
        }

        /**
         * 
         * @return
         */
        public SGProperties getProperties() {
            final AxisBreakSymbolWithAxesProperties p = new AxisBreakSymbolWithAxesProperties();
            if (this.getProperties(p) == false) {
                return null;
            }
            return p;
        }

        /**
         * 
         * @param p
         * @return
         */
        public boolean getProperties(final SGProperties p) {
            if ((p instanceof AxisBreakSymbolWithAxesProperties) == false) {
                return false;
            }

            if (super.getProperties(p) == false) {
                return false;
            }

            final AxisBreakSymbolWithAxesProperties dp = (AxisBreakSymbolWithAxesProperties) p;
            dp.setXValue(this.mXValue);
            dp.setYValue(this.mYValue);
            dp.mXAxis = this.mXAxis;
            dp.mYAxis = this.mYAxis;
            dp.setAnchored(this.mIsAnchored);

            return true;
        }

        /**
         * Flag whether this object is focused.
         */
        private boolean mSelectedFlag = false;

        /**
         * Returns whether this object is selected
         * 
         * @return whether this object is selected
         */
        @Override
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

        @Override
        public float getMagnification() {
            return SGFigureElementAxisBreak.this.getMagnification();
        }

        @Override
        public boolean setMagnification(float mag) {
            // do nothing
            return true;
        }

        /**
         * Sets the properties directly.
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
                
                if (COM_BREAK_AXIS_X.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_BREAK_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setXAxisLocation(loc) == false) {
                        result.putResult(COM_BREAK_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_AXIS_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_AXIS_Y.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_BREAK_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setYAxisLocation(loc) == false) {
                        result.putResult(COM_BREAK_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_AXIS_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_LOCATION_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_BREAK_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_BREAK_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setXValue(num.doubleValue()) == false) {
                        result.putResult(COM_BREAK_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_LOCATION_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_LOCATION_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_BREAK_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_BREAK_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setYValue(num.doubleValue()) == false) {
                        result.putResult(COM_BREAK_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_LOCATION_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_LENGTH.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_BREAK_LENGTH,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLength(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_BREAK_LENGTH,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_LENGTH, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_INTERVAL.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_BREAK_INTERVAL,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setInterval(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_BREAK_INTERVAL,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_INTERVAL, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_DISTORTION.equalsIgnoreCase(key)) {
                    Number num = SGUtilityText.getFloat(value);
                    if (num == null) {
                        result.putResult(COM_BREAK_DISTORTION,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.floatValue()) == false) {
                        result.putResult(COM_BREAK_DISTORTION,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setDistortion(num.floatValue()) == false) {
                        result.putResult(COM_BREAK_DISTORTION,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_DISTORTION,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_ANGLE.equalsIgnoreCase(key)) {
                    Number num = SGUtilityText.getFloat(value);
                    if (num == null) {
                        result.putResult(COM_BREAK_ANGLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAngle(num.floatValue()) == false) {
                        result.putResult(COM_BREAK_ANGLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_ANGLE,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_LINE_WIDTH.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_BREAK_LINE_WIDTH,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLineWidth(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_BREAK_LINE_WIDTH,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_HORIZONTAL.equalsIgnoreCase(key)) {
                    Boolean b = SGUtilityText.getBoolean(value);
                    if (b == null) {
                        result.putResult(COM_BREAK_HORIZONTAL,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setForHorizontalAxisFlag(b.booleanValue()) == false) {
                        result.putResult(COM_BREAK_HORIZONTAL,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_HORIZONTAL, SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_LINE_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.getColor(value);
                    if (cl != null) {
                        if (this.setLineColor(cl) == false) {
                            result.putResult(COM_BREAK_LINE_COLOR,
                                    SGPropertyResults.INVALID_INPUT_VALUE);
                            continue;
                        }
                    } else {
                    	cl = SGUtilityText.parseColor(value);
    					if (cl == null) {
    						result.putResult(COM_BREAK_LINE_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
    					if (this.setLineColor(cl) == false) {
    						result.putResult(COM_BREAK_LINE_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
                    }
                    result.putResult(COM_BREAK_LINE_COLOR,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_INNER_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.getColor(value);
                    if (cl != null) {
                        if (this.setInnerColor(cl) == false) {
                            result.putResult(COM_BREAK_INNER_COLOR,
                                    SGPropertyResults.INVALID_INPUT_VALUE);
                            continue;
                        }
                    } else {
                    	cl = SGUtilityText.parseColor(value);
    					if (cl == null) {
    						result.putResult(COM_BREAK_INNER_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
    					if (this.setInnerColor(cl) == false) {
    						result.putResult(COM_BREAK_INNER_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
                    }
                    result.putResult(COM_BREAK_INNER_COLOR,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_BREAK_ANCHORED.equalsIgnoreCase(key)) {
                    Boolean b = SGUtilityText.getBoolean(value);
                    if (b == null) {
                        result.putResult(COM_BREAK_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAnchored(b.booleanValue()) == false) {
                        result.putResult(COM_BREAK_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_BREAK_ANCHORED, SGPropertyResults.SUCCEEDED);
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
        	return SGCommandUtility.createCommandString(COM_AXIS_BREAK, 
    				Integer.toString(this.mID), this.getCommandPropertyMap(params));
        }

        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
    	@Override
        public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
        	SGPropertyMap map = super.getCommandPropertyMap(params);
        	this.addProperties(map, COM_BREAK_LOCATION_X, COM_BREAK_LOCATION_Y, 
        			COM_BREAK_AXIS_X, COM_BREAK_AXIS_Y, COM_BREAK_ANCHORED);
			return map;
		}

    	@Override
    	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	this.addProperties(map, KEY_X_VALUE, KEY_Y_VALUE, KEY_X_AXIS_POSITION, 
        			KEY_Y_AXIS_POSITION, KEY_ANCHORED);
        	return map;
    	}

        private void addProperties(SGPropertyMap map, String xValueKey, String yValueKey,
        		String xAxisKey, String yAxisKey, String anchoredKey) {
            SGPropertyUtility.addProperty(map, xValueKey, this.getXValue());
            SGPropertyUtility.addProperty(map, yValueKey, this.getYValue());
            SGPropertyUtility.addProperty(map, xAxisKey, 
            		mAxisElement.getLocationName(this.mXAxis));
            SGPropertyUtility.addProperty(map, yAxisKey, 
            		mAxisElement.getLocationName(this.mYAxis));
            SGPropertyUtility.addProperty(map, anchoredKey, this.isAnchored());
        }

    	@Override
    	public boolean getAxisDateMode(final int location) {
    		return mAxisElement.getAxisDateMode(location);
    	}
    }

    /**
     * Property of Axis Break Symbol.
     */
    public static class AxisBreakSymbolWithAxesProperties extends
            AxisBreakSymbolProperties {

        private double mXValue = 0.0;

        private double mYValue = 0.0;

        private SGAxis mXAxis = null;

        private SGAxis mYAxis = null;
        
        private boolean mAnchored = false;

        /**
         * 
         */
        public AxisBreakSymbolWithAxesProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof AxisBreakSymbolWithAxesProperties) == false) {
                return false;
            }

            if (super.equals(obj) == false)
                return false;

            AxisBreakSymbolWithAxesProperties p = (AxisBreakSymbolWithAxesProperties) obj;

            if (p.mXValue != this.mXValue)
                return false;
            if (p.mYValue != this.mYValue)
                return false;
            if (this.mXAxis.equals(p.mXAxis) == false)
                return false;
            if (this.mYAxis.equals(p.mYAxis) == false)
                return false;
            if (p.mAnchored != this.mAnchored)
                return false;

            return true;
        }

        public Double getXValue() {
            return Double.valueOf(this.mXValue);
        }

        public Double getYValue() {
            return Double.valueOf(this.mYValue);
        }

        public SGAxis getXAxis() {
            return this.mXAxis;
        }

        public SGAxis getYAxis() {
            return this.mYAxis;
        }
        
        public boolean getAnchored() {
            return this.mAnchored;
        }

        public boolean setXValue(final double value) {
            this.mXValue = value;
            return true;
        }

        public boolean setYValue(final double value) {
            this.mYValue = value;
            return true;
        }

        public boolean setAnchored(final boolean value) {
            this.mAnchored = value;
            return true;
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
                    mPropertyDialog = new SGAxisBreakDialog(mDialogOwner, true);
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
            String sx = null;
            String sy = null;
            String sAxisX = null;
            String sAxisY = null;
            Iterator itr = map.getKeyIterator();
            while (itr.hasNext()) {
                Object keyObj = itr.next();
                String key = keyObj.toString();
                if (COM_BREAK_LOCATION_X.equalsIgnoreCase(key)) {
                	sx = map.getValueString(key);
                } else if (COM_BREAK_LOCATION_Y.equalsIgnoreCase(key)) {
                	sy = map.getValueString(key);
                } else if (COM_BREAK_AXIS_X.equalsIgnoreCase(key)) {
                	sAxisX = map.getValueString(key);
                } else if (COM_BREAK_AXIS_Y.equalsIgnoreCase(key)) {
                	sAxisY = map.getValueString(key);
                }
            }
            if (sx == null) {
                return null;
            }
            if (sy == null) {
                return null;
            }
            Double x = SGUtilityText.getDouble(sx);
            Double y = SGUtilityText.getDouble(sy);
            if (x == null) {
                return null;
            }
            if (y == null) {
                return null;
            }
        	if (SGUtility.isValidPropertyValue(x.doubleValue()) == false) {
                return null;
        	}
        	if (SGUtility.isValidPropertyValue(y.doubleValue()) == false) {
                return null;
        	}

            if (this.addAxisBreakSymbol(id, x.doubleValue(), y.doubleValue(),
            		sAxisX, sAxisY) == false) {
                return null;
            }
            
            child = this.getVisibleChild(id);
        }

        // set properties to the child object
        SGPropertyResults result = child.setProperties(map);

        return result;
    }
}
