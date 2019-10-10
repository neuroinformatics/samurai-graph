package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

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
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementSignificantDifference.SigDiffProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGISignificantDifferenceConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class managing significant difference symbols.
 */
public class SGFigureElementSignificantDifference extends SGFigureElement2D
        implements SGIFigureElementSignificantDifference,
        SGISignificantDifferenceConstants, SGIStringConstants, CaretListener,
        DocumentListener, ActionListener, KeyListener {

    /**
     * 
     */
    private SGIFigureElementAxis mAxisElement = null;

    /**
     * 
     */
    private JTextField mEditField = null;

    /**
     * A symbol which the user is editing the text.
     */
    private SigDiffSymbol mEditingSymbol = null;

    /**
     * The property dialog of significant difference symbols.
     */
    private SGPropertyDialog mPropertyDialog = null;

    /**
     * 
     */
    public SGFigureElementSignificantDifference() {
        super();
        this.initEditField();
    }

    // initialize the text field
    private void initEditField() {
        this.mEditField = new JTextField(10);
        this.mEditField.setVisible(false);
        this.mEditField.addActionListener(this);
        this.mEditField.addCaretListener(this);
        this.mEditField.getDocument().addDocumentListener(this);
        this.mEditField.addKeyListener(this);
    }
    
    /**
     * Disposes this object.
     */
    public void dispose() {
        super.dispose();
        
        // dispose the property dialog
        if (this.mPropertyDialog != null) {
            this.mPropertyDialog.dispose();
            this.mPropertyDialog = null;
        }
        
        this.mAxisElement = null;
        this.mEditField = null;
        this.mEditingSymbol = null;
    }

    /**
     * Inserts a significant difference symbol at a given point with default axes.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final float x, final float y) {
        if (this.getGraphRect().contains(x, y) == false) {
            return false;
        }
        SGAxis xAxis = this.mAxisElement
                .getAxis(DEFAULT_SIGDIFF_HORIZONTAL_AXIS);
        SGAxis yAxis = this.mAxisElement
                .getAxis(DEFAULT_SIGDIFF_VERTICAL_AXIS);
        return this.addSignificantDifferenceSymbol(this.assignChildId(), x, y, 
        		xAxis, yAxis);
    }

    /**
     * Inserts a significant difference symbol with given axis values with given axes.
     * 
     * @param id
     *          the ID to set
     * @param x
     *          axis value for given x-axis
     * @param y
     *          axis value for given y-axis
     * @param xAxis
     *          the x-axis
     * @param yAxis
     *          the y-axis
     * @return true if succeeded
     */
    private boolean addSignificantDifferenceSymbol(final int id, final float x, 
            final float y, final SGAxis xAxis, final SGAxis yAxis) {
        
        SigDiffSymbol sd = new SigDiffSymbol();
        sd.setMagnification(this.mMagnification);

        // set axes
        sd.mXAxis = xAxis;
        sd.mYAxis = yAxis;

        // set the location
        if (sd.setLocation(x, y) == false) {
        	return false;
        }

        // create drawing elements
        if (sd.createDrawingElement() == false) {
        	return false;
        }

        // set axis values
        if (sd.setAxisValuesWithShape() == false) {
        	return false;
        }

        // add to the list
        if (this.addToList(id, sd) == false) {
            return true;
        }

        // initialize history
        sd.initPropertiesHistory();

        this.setChanged(true);
        this.notifyToRoot();

        return true;
    }

    /**
     * Inserts a significant difference symbol with given axis values for default axes.
     * 
     * @param id
     *          the ID to set
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int id, final double x, 
            final double y) {
        return this.addSignificantDifferenceSymbol(id, x, y, 
                DEFAULT_SIGDIFF_HORIZONTAL_AXIS, DEFAULT_SIGDIFF_VERTICAL_AXIS);
    }

    /**
     * Inserts a significant difference symbol with given axis values for given axes.
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
    public boolean addSignificantDifferenceSymbol(final int id, final double x, 
            final double y, final String xAxisLocation, final String yAxisLocation) {

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

        // insert a symbol
        final float posX = this.calcLocation(x, xAxis, true);
        final float posY = this.calcLocation(y, yAxis, false);
        if (this.addSignificantDifferenceSymbol(id, posX, posY, xAxis, yAxis) == false) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Inserts a significant difference symbol with given axis values for default axes.
     * 
     * @param id
     *          the ID to set
     * @param leftX
     *          axis value of the left end for default x-axis
     * @param leftY
     *          axis value of the left end for default y-axis
     * @param rightX
     *          axis value of the right end for default x-axis
     * @param rightY
     *          axis value of the right end for default y-axis
     * @param horizontalY
     *          axis value of the horizontal bar for default y-axis
     * @return true if succeeded
     */
    public boolean addSignificantDifferenceSymbol(final int id,
            final double leftX, final double leftY, final double rightX,
            final double rightY, final double horizontalY) {
        return this.addSignificantDifferenceSymbol(id, leftX, leftY, rightX, rightY,
        		horizontalY, DEFAULT_SIGDIFF_HORIZONTAL_AXIS, DEFAULT_SIGDIFF_VERTICAL_AXIS);
    }

    /**
     * Inserts a significant difference symbol with given axis values for default axes.
     * 
     * @param id
     *          the ID to set
     * @param leftX
     *          axis value of the left end for default x-axis
     * @param leftY
     *          axis value of the left end for default y-axis
     * @param rightX
     *          axis value of the right end for default x-axis
     * @param rightY
     *          axis value of the right end for default y-axis
     * @param horizontalY
     *          axis value of the horizontal bar for default y-axis
     * @return true if succeeded
     */
    private boolean addSignificantDifferenceSymbol(final int id,
            final double leftX, final double leftY, final double rightX,
            final double rightY, final double horizontalY, final String xAxisLocation,
            final String yAxisLocation) {

        // get axes
        String xAxisName = (xAxisLocation != null) ? xAxisLocation : DEFAULT_SIGDIFF_HORIZONTAL_AXIS;
        String yAxisName = (yAxisLocation != null) ? yAxisLocation : DEFAULT_SIGDIFF_VERTICAL_AXIS;
        SGAxis xAxis = this.mAxisElement.getAxis(xAxisName);
        SGAxis yAxis = this.mAxisElement.getAxis(yAxisName);
        if (xAxis == null || yAxis == null) {
            return false;
        }
        if (xAxis.isValidValue(leftX) == false) {
            return false;
        }
        if (xAxis.isValidValue(rightX) == false) {
            return false;
        }
        if (yAxis.isValidValue(leftY) == false) {
        	return false;
        }
        if (yAxis.isValidValue(rightY) == false) {
        	return false;
        }
        if (yAxis.isValidValue(horizontalY) == false) {
        	return false;
        }

        SigDiffSymbol sd = new SigDiffSymbol();
        sd.setMagnification(this.mMagnification);

        // set axes
        sd.mXAxis = xAxis;
        sd.mYAxis = yAxis;

        // create drawing elements
        if (sd.createDrawingElement() == false) {
        	return false;
        }

        // set axis values
        if (sd.setLeftXValue(leftX) == false) {
        	return false;
        }
        if (sd.setLeftYValue(leftY) == false) {
        	return false;
        }
        if (sd.setRightXValue(rightX) == false) {
        	return false;
        }
        if (sd.setRightYValue(rightY) == false) {
        	return false;
        }
        if (sd.setHorizontalYValue(horizontalY) == false) {
        	return false;
        }

        // add to the list
        if (this.addToList(id, sd) == false) {
            return true;
        }

        // initialize history
        sd.initPropertiesHistory();

        this.setChanged(true);
        this.notifyToRoot();

        return true;
    }

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element) {
        this.mAxisElement = element;
    }

    /**
     * synchronize
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
            SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
            if (! el.isAnchored()) {
                if (el.setShapeWithAxesValues()==false) {
                    continue;
                }
            } else {
                if (el.setAxisValuesWithShape()==false) {
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
            final String msg) {
        // this shouldn't happen
        throw new Error();
    }

    /**
     * 
     */
    public SGProperties getProperties() {
        SGProperties p = new SigDiffElementProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    /**
     * 
     */
    public String getTagName() {
        return TAG_NAME_SIGNIFICANT_DIFFERENCE;
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
                .getElementsByTagName(SGISignificantDifferenceConstants.TAG_NAME_SIGDIFF_SYMBOL);
        for (int ii = 0; ii < nList.getLength(); ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;
                SigDiffSymbol sd = new SigDiffSymbol();
                if (sd.readProperty(el) == false) {
                    return false;
                }
                
                // modifies the text string for previous releases older than 2_0_0
                String str = sd.getText();
                String modStr = SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str, versionNumber);
                sd.setText(modStr);
                
                sd.initPropertiesHistory();
                this.addToList(sd);
            }
        }
        return true;
    }

    /**
     * 
     */
    public void actionPerformed(ActionEvent e) {
        final Object source = e.getSource();
        // final String command = e.getActionCommand();

        if (source.equals(this.mEditField)) {
            this.closeTextField();
        }

    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        if (super.setGraphRect(x, y, width, height) == false) {
            return false;
        }

        if (this.closeTextField() == false) {
            return false;
        }

        List list = this.mChildList;
        for (int ii = 0; ii < list.size(); ii++) {
            final SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
            if (! el.isAnchored()) {
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
    public boolean getMarginAroundGraphRect(SGTuple2f topAndBottom,
            SGTuple2f leftAndRight) {

        if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
            return false;
        }

        List<SGIChildObject> sList = this.getVisibleChildList();
        ArrayList rectList = new ArrayList();
        for (int ii = 0; ii < sList.size(); ii++) {
            SigDiffSymbol el = (SigDiffSymbol) sList.get(ii);
            Rectangle2D rect = el.getElementBounds();
            rectList.add(rect);
        }
        if (rectList.size() == 0) {
            return true;
        }

        // create combined rectangle
        Rectangle2D uniRect = SGUtility.createUnion(rectList);

        // calculate width from combined rectangle
        final float top = this.mGraphRectY - (float) uniRect.getY();
        final float bottom = -(this.mGraphRectY + this.mGraphRectHeight)
                + (float) (uniRect.getY() + uniRect.getHeight());
        final float left = this.mGraphRectX - (float) uniRect.getX();
        final float right = -(this.mGraphRectX + this.mGraphRectWidth)
                + (float) (uniRect.getX() + uniRect.getWidth());

        // set arguments
        topAndBottom.x += top;
        topAndBottom.y += bottom;
        leftAndRight.x += left;
        leftAndRight.y += right;

        return true;
    }

    /**
     * 
     */
    public void paintGraphics(Graphics g, boolean clip) {
        final Graphics2D g2d = (Graphics2D) g;

        // draw significant difference symbols
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            el.paint(g2d);
        }

        // draw symbols around all objects
        if (this.mSymbolsVisibleFlagAroundAllObjects) {
            for (int ii = 0; ii < list.size(); ii++) {
                SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
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
        	List<SGISelectable> fList = this.getFocusedObjectsList();
            for (int ii = 0; ii < fList.size(); ii++) {
                SigDiffSymbol el = (SigDiffSymbol) fList.get(ii);
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
     * 
     * @param e
     */
    public boolean setMouseCursor(final int x, final int y) {

        // significant difference symbols
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            SigDiffSymbol sd = (SigDiffSymbol) list.get(ii);
            if (sd.isValid() == false) {
                continue;
            }
            if (sd.contains(x, y)) {
                if (sd.isSelected()) {
                    sd.mMouseLocation = sd.getMouseLocation(x, y);
                    setMouseCursor(sd.getCursor(sd.mMouseLocation));
                    return true;
                }
                setMouseCursor(Cursor.HAND_CURSOR);
                return true;
            }
        }

        return false;
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
                final SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
                if (el.isSelected()) {
                	if (el.prepare() == false) {
                		return false;
                	}
                    el.translate((float) dx, (float) dy);
                    if (el.commit() == false) {
                    	return false;
                    }
                    this.notifyChange();
                    this.notifyToRoot();
                    this.repaint();
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
            SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
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
     * 
     */
    private boolean clickDrawingElements(final SigDiffSymbol el,
            final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int mod = e.getModifiers();
        final boolean ctrl = (mod & InputEvent.CTRL_MASK) != 0;
        final boolean shift = (mod & InputEvent.SHIFT_MASK) != 0;
        final int cnt = e.getClickCount();

        // clicked on the line elements
        if (el.contains(x, y)) {
            // clicked on the string element
            if (el.getStringElement().contains(x, y)) {
                if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
                    if (el.isSelected() && !ctrl && !shift) {
                        this.mEditingSymbol = el;
//                        this.showEditField(el);
                        Rectangle2D sRect = el.getStringBounds();
                        final int tx = this.mPressedPoint.x - (int) sRect.getX();
                        final int ty = this.mPressedPoint.y - (int) sRect.getY();
                        this.showEditField(this.mEditField, 
                                (SGDrawingElementString2D) el.getStringElement(), tx, ty);
                    } else {
                        this.updateFocusedObjectsList(el, e);
                    }
                } else if (SwingUtilities.isRightMouseButton(e) && cnt == 1) {
                    this.updateFocusedObjectsList(el, e);
                    el.getPopupMenu().show(this.getComponent(), x, y);
                }
            } else
            // otherwise
            {
                this.updateFocusedObjectsList(el, e);
                if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {

                } else if (SwingUtilities.isLeftMouseButton(e) && cnt == 2) {
                    this.setPropertiesOfSelectedObjects(el);
                } else if (SwingUtilities.isRightMouseButton(e) && cnt == 1) {
                    el.getPopupMenu().show(this.getComponent(), x, y);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 
     */
    public void translateFocusedObjects(final int dx, final int dy) {
        if (this.closeTextField() == false) {
            return;
        }

        super.translateFocusedObjects(dx, dy);
    }
    
    private SigDiffSymbol mPressedSymbol = null;
    
    /**
     * 
     * @param e
     */
    public boolean onMousePressed(final MouseEvent e) {
        if (this.closeTextField() == false) {
            return false;
        }

        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
            if (el.isValid() == false) {
                continue;
            }
            if (el.pressDrawingElements(e)) {
                this.mPressedPoint = e.getPoint();
                this.mPressedSymbol = el;
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

        SigDiffSymbol sd = null;
        List<SGISelectable> list = this.getFocusedObjectsList();
        if (list.size() == 1) {
            sd = (SigDiffSymbol) list.get(0);
        } else if (list.size() > 1 && this.mPressedSymbol != null) {
        	sd = this.mPressedSymbol;
        }
        if (sd != null) {
            if (sd.isValid() == false) {
                return false;
            }
            if (sd.drag(e) == false) {
                return false;
            }
            sd.createDrawingElement();
            if (sd.setAxisValuesWithShape() == false) {
                return false;
            }
            if (sd.setShapeWithAxesValues() == false) {
                return false;
            }
            this.mPressedPoint = e.getPoint();
        }

        return true;

    }

    /**
     * 
     * @param e
     */
    public boolean onMouseReleased(final MouseEvent e) {
        this.mDraggableFlag = false;
        this.mDraggedDirection = null;
        this.mPressedSymbol = null;
        return true;
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setTemporaryPropertiesOfFocusedObjects() {
//    	List<SGISelectable> list = this.getFocusedObjectsList();
//        for (int ii = 0; ii < list.size(); ii++) {
//            SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
//            el.mTemporaryProperties = el.getProperties();
//        }
//        return true;
//    }

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
    	return SigDiffSymbol.class;
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
            SigDiffSymbol el = (SigDiffSymbol) list.get(ii);
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
     * @return
     */
    public boolean clearFocusedObjects() {
        if (super.clearFocusedObjects() == false) {
            return false;
        }

        if (this.closeTextField() == false) {
            return false;
        }

        return true;
    }

    /**
     * 
     * @return
     */
    public String getClassDescription() {
        return "Significant Difference Symbols";
    }

    public void setComponent(JComponent com) {
        super.setComponent(com);
        com.add(this.mEditField);
    }

//    /**
//     * 
//     */
//    private boolean showEditField(final SigDiffSymbol el) {
//        SGDrawingElementString2DExtended sElement = (SGDrawingElementString2DExtended) el
//                .getStringElement();
//        final JTextField tf = this.mEditField;
//
//        final Rectangle2D rect = sElement.getElementBounds();
//        final Rectangle2D sRect = sElement.getStringRect();
//
//        final float fontSize = el.getMagnification() * el.getFontSize();
//
//        final int x = (int) (rect.getX() - tf.getInsets().left);
//        final int y = (int) (rect.getY() - fontSize / 2.0f);
//        final int w = (int) (sRect.getWidth() + fontSize);
//        final int h = (int) (sRect.getHeight() + fontSize);
//
//        tf.setLocation(x, y);
//        tf.setSize(w, h);
//        tf
//                .setFont(new Font(el.getFontName(), el.getFontStyle(),
//                        (int) fontSize));
//        tf.setForeground(el.getColor());
//        tf.setText(sElement.getString());
//
//        // show the text field
//        tf.setVisible(true);
//        tf.requestFocus();
//        tf.setCaretPosition(0);
//
//        return true;
//
//    }

    /**
     * Overrode for the text field for the text of symbols.
     * 
     * @return true if a text field is shown
     */
    public boolean closeTextField() {
        if (this.mEditField.isVisible()) {
            return this.commitEdit();
        }
        return true;
    }

    /**
     * 
     * @return
     */
    private boolean hideEditField() {
        this.mEditField.setText("");
        this.mEditField.setVisible(false);
        return true;
    }

    /**
     * 
     * @return
     */
    private boolean commitEdit() {
        if (this.mEditingSymbol == null) {
            return false;
        }

        String before = this.mEditingSymbol.getStringElement().getString();
        String after = this.mEditField.getText();
        if (SGUtilityText.isValidString(after)) {
            this.mEditingSymbol.setText(after);

            if (before.equals(after) == false) {
                this.mEditingSymbol.setChanged(true);
                this.notifyToRoot();
            }

            this.mEditingSymbol.createDrawingElement();
        }

        this.mEditingSymbol = null;
        this.hideEditField();
        this.repaint();

        return true;
    }

    /**
     * 
     */
    public boolean getProperties(final SGProperties p) {

        if ((p instanceof SigDiffElementProperties) == false) {
            return false;
        }

        SigDiffElementProperties gp = (SigDiffElementProperties) p;
        gp.visibleSigDiffSymbolList = new ArrayList(this.getVisibleChildList());

        return true;

    }

    /**
     * 
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof SigDiffElementProperties) == false) {
            return false;
        }

        SigDiffElementProperties gp = (SigDiffElementProperties) p;

        boolean flag = this.setVisibleChildList(gp.visibleSigDiffSymbolList);
        if (!flag) {
            return false;
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
    public Element[] createElement(final Document document, SGExportParameter params) {
        Element el = this.createThisElement(document, params);
        if (el == null) {
            return null;
        }

        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            SigDiffSymbol sd = (SigDiffSymbol) list.get(ii);
            if (sd.isValid() == false) {
                continue;
            }

            Element elSigDiff = sd.createElement(document, params);
            if (elSigDiff == null) {
                return null;
            }
            el.appendChild(elSigDiff);
        }
        return new Element[] { el };
    }

    /**
     * 
     */
    public boolean setMementoBackward() {
        boolean flag = super.setMementoBackward();
        if (!flag) {
            return false;
        }
        this.notifyChangeOnUndo();
        this.clearFocusedObjects();

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
        this.notifyChangeOnUndo();
        this.clearFocusedObjects();

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
            SigDiffSymbol el = (SigDiffSymbol) cList.get(ii);
            el.setShapeWithAxesValues();

            // translate the duplicate
            el.translate(ox, oy);

            // set selected
            el.setSelected(true);

            // add to the list
            this.addToList(el);

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
            if (obj instanceof SigDiffSymbol) {
                SigDiffSymbol sd = (SigDiffSymbol) obj;
                sd.setShapeWithAxesValues();

                // translate the instance to be pasted
                sd.translate(ox, oy);

                SGProperties p = sd.getProperties();

                SigDiffSymbol el = new SigDiffSymbol();
                el.setMagnification(mag);
                el.setProperties(p);

                // el.mXAxis = this.mAxisElement.getAxisInCube(sd.mTempXAxis);
                // el.mYAxis = this.mAxisElement.getAxisInCube(sd.mTempYAxis);
                el.mXAxis = this.mAxisElement.getAxisInPlane(sd.mTempXAxis);
                el.mYAxis = this.mAxisElement.getAxisInPlane(sd.mTempYAxis);

                // set the shape
                el.setShapeWithAxesValues();

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

    /**
     * 
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            SigDiffElementProperties p = (SigDiffElementProperties) mList
                    .get(ii);
            set.addAll(p.visibleSigDiffSymbolList);
        }

        return set;
    }

    /**
     * 
     */
    public static class SigDiffElementProperties extends SGProperties {
        ArrayList visibleSigDiffSymbolList = new ArrayList();

        /**
         * 
         * 
         */
        public SigDiffElementProperties() {
            super();
        }

        public void dispose() {
        	super.dispose();
            this.visibleSigDiffSymbolList.clear();
            this.visibleSigDiffSymbolList = null;
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof SigDiffElementProperties) == false) {
                return false;
            }

            SigDiffElementProperties p = (SigDiffElementProperties) obj;

            if (p.visibleSigDiffSymbolList
                    .equals(this.visibleSigDiffSymbolList) == false) {
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
        	sb.append(this.visibleSigDiffSymbolList.toString());
        	sb.append(']');
        	return sb.toString();
        }

    }

    private static class SigDiffSymbolPopupMenu extends JPopupMenu {
        
        private static final long serialVersionUID = 4970384335056528846L;
        
        private JCheckBoxMenuItem anchoredCheckBox;

        public void init(SigDiffSymbol sd) {
            this.setBounds(0, 0, 100, 100);

            StringBuffer sb = new StringBuffer();
            sb.append("  -- Significant Difference: ");
            sb.append(sd.getID());
            sb.append(" --");
            
            this.add(new JLabel(sb.toString()));
            this.addSeparator();

            SGUtility.addArrangeItems(this, sd);

            this.addSeparator();

            SGUtility.addItem(this, sd, MENUCMD_CUT);
            SGUtility.addItem(this, sd, MENUCMD_COPY);

            this.addSeparator();

            SGUtility.addItem(this, sd, MENUCMD_DELETE);
            SGUtility.addItem(this, sd, MENUCMD_DUPLICATE);

            this.addSeparator();
            
            this.anchoredCheckBox = new JCheckBoxMenuItem(MENUCMD_ANCHORED);
            add(this.anchoredCheckBox);
            this.anchoredCheckBox.addActionListener(sd);

            this.addSeparator();

            SGUtility.addItem(this, sd, MENUCMD_PROPERTY);
        }
        
        void setAnchored(final boolean anchored) {
            this.anchoredCheckBox.setSelected(anchored);
        }
    }
    
    private class SigDiffSymbol extends SGDrawingElementSignificantDifference2D
            implements ActionListener, SGIUndoable, SGIChildObject, SGIMovable, SGICopiable, 
            SGISignificantDifferenceDialogObserver, SGINode {

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
         * 
         */
        private SigDiffSymbol mTempSymbol = null;

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
        public String toString() {
            return "SigDiff:" + this.mID;
        }

        /**
         * 
         */
        public void dispose() {
            super.dispose();

            this.mPopupMenu = null;

            if (this.mTemporaryProperties != null) {
                this.mTemporaryProperties.dispose();
                this.mTemporaryProperties = null;
            }

            this.mTempSymbol = null;

            this.mUndoManager.dispose();
            this.mUndoManager = null;

            this.mXAxis = null;
            this.mYAxis = null;
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
        private double mXValue1;

        /**
         * 
         */
        private double mXValue2;

        /**
         * 
         */
        private double mHorizontalYValue;

        /**
         * 
         */
        private double mYValue1;

        /**
         * 
         */
        private double mYValue2;
        
        /**
         * 
         */
        private boolean mIsAnchored;

        /**
         * 
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * popup menu
         */
        private SigDiffSymbolPopupMenu mPopupMenu = null;

        /**
         * 
         */
        private SigDiffSymbol() {
            super();
            this.init();
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
            String xAxis = SGFigureElementSignificantDifference.this.mAxisElement
                    .getLocationName(this.mXAxis);
            String yAxis = SGFigureElementSignificantDifference.this.mAxisElement
                    .getLocationName(this.mYAxis);
            StringBuffer sb = new StringBuffer();
            sb.append(this.mID);
            sb.append(": ");
//            sb.append(this.getText());
            sb.append(SGUtility.removeEscapeChar(this.getText()));
            sb.append(", AxisX=");
            sb.append(xAxis.toString());
            sb.append(", AxisY=");
            sb.append(yAxis.toString());
            sb.append(", LeftX=");
            sb.append(this.getLeftXValue());
            sb.append(", RightX=");
            sb.append(this.getRightXValue());
            sb.append(", HorizontalY=");
            sb.append(this.getHorizontalYValue());
            return sb.toString();
        }

        /**
         * 
         */
        public int getXAxisLocation() {
            return SGFigureElementSignificantDifference.this.mAxisElement
                    .getLocationInPlane(this.mXAxis);
        }

        /**
         * 
         */
        public int getYAxisLocation() {
            return SGFigureElementSignificantDifference.this.mAxisElement
                    .getLocationInPlane(this.mYAxis);
        }

        /**
         * Sets the x-axis.
         * 
         * @param location
         *           the axis location
         * @return true if succeeded
         */
        public boolean setXAxisLocation(final int location) {
            if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                    && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
                return false;
            }
            SGAxis axis = mAxisElement.getAxisInPlane(location);
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
         *           the axis location
         * @return true if succeeded
         */
        public boolean setYAxisLocation(final int location) {
            if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                    && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
                return false;
            }
            SGAxis axis = mAxisElement.getAxisInPlane(location);
            if (axis == null) {
            	return false;
            }
            this.mYAxis = axis;
            return true;
        }

        /**
         * Sets the text.
         * 
         * @param text
         *           a text string to set
         * @return true if succeeded
         */
        public boolean setText(final String text) {
        	if (SGUtilityText.isValidString(text) == false) {
        		return false;
        	}
            return super.setText(text);
        }

        /**
         * 
         */
        public Object copy() {
            SigDiffSymbol el = new SigDiffSymbol();
            el.mTempXAxis = mAxisElement.getLocationInPlane(this.mXAxis);
            el.mTempYAxis = mAxisElement.getLocationInPlane(this.mYAxis);
            el.setMagnification(this.getMagnification());
            el.setProperties(this.getProperties());
            el.setLocation(this.getX(), this.getY());
            el.setAnchored(this.isAnchored());
            return el;
        }

        private int mTempXAxis = -1;

        private int mTempYAxis = -1;

        /**
         * 
         */
//        private SigDiffSymbol(final float x, final float y, final float w,
//                final float hl, final float hr) {
//            super(x, y, w, hl, hr);
//            this.init();
//        }

        /**
         * Returns a pop-up menu.
         * @return
         *         a pop-up menu
         */
        public JPopupMenu getPopupMenu() {
            SigDiffSymbolPopupMenu p = null;
            if (this.mPopupMenu != null) {
                p = this.mPopupMenu;
            } else {
                p = new SigDiffSymbolPopupMenu();
                p.init(this);
                this.mPopupMenu = p;
            }
            p.setAnchored(this.isAnchored());
            return p;
        }

        /**
         * 
         */
        private boolean init() {
            this.setWidth(DEFAULT_SIGDIFF_SYMBOL_WIDTH, cm);
            this
                    .setVerticalHeight1(
                            DEFAULT_SIGDIFF_SYMBOL_LEFT_HEIGHT, cm);
            this.setVerticalHeight2(DEFAULT_SIGDIFF_SYMBOL_RIGHT_HEIGHT,
                    cm);

            this.setFontSize(DEFAULT_SIGDIFF_SYMBOL_FONT_SIZE, FONT_SIZE_UNIT);
            this.setFontName(DEFAULT_SIGDIFF_SYMBOL_FONT_NAME);
            this.setFontStyle(DEFAULT_SIGDIFF_SYMBOL_FONT_STYLE);

            this.setLineWidth(DEFAULT_SIGDIFF_SYMBOL_LINE_WIDTH,
                    LINE_WIDTH_UNIT);
            this.setColor(DEFAULT_SIGDIFF_SYMBOL_COLOR);
            this.setSpace(DEFAULT_SIGDIFF_SYMBOL_SPACE, SIGDIFF_SPACE_UNIT);
            
            this.setAnchored(DEFAULT_SIGDIFF_SYMBOL_ANCHORED);

            return true;
        }

        /**
         * 
         */
        public boolean prepare() {
            this.mTemporaryProperties = this.getProperties();
            return true;
        }

        /**
         * @return
         */
        public String getTagName() {
            return TAG_NAME_SIGDIFF_SYMBOL;
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
         * @param el
         * @return
         */
        public boolean writeProperty(final Element el, SGExportParameter params) {
            if (super.writeProperty(el, params) == false) {
                return false;
            }

            el.setAttribute(KEY_SIGDIFF_LEFT_X_VALUE, Double.toString(this
                    .getLeftXValue()));
            el.setAttribute(KEY_SIGDIFF_LEFT_Y_VALUE, Double.toString(this
                    .getLeftYValue()));
            el.setAttribute(KEY_SIGDIFF_RIGHT_X_VALUE, Double.toString(this
                    .getRightXValue()));
            el.setAttribute(KEY_SIGDIFF_RIGHT_Y_VALUE, Double.toString(this
                    .getRightYValue()));
            el.setAttribute(KEY_SIGDIFF_HORIZONTAL_Y_VALUE, Double.toString(this
                    .getHorizontalYValue()));
            el.setAttribute(KEY_X_AXIS_POSITION,
                    SGFigureElementSignificantDifference.this.mAxisElement
                            .getLocationName(this.mXAxis));
            el.setAttribute(KEY_Y_AXIS_POSITION,
                    SGFigureElementSignificantDifference.this.mAxisElement
                            .getLocationName(this.mYAxis));
            el.setAttribute(KEY_SIGDIFF_ANCHORED, Boolean.toString(this.mIsAnchored));

            return true;
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
                SGAxis xAxis = SGFigureElementSignificantDifference.this.mAxisElement
                        .getAxis(str);
                if (xAxis == null) {
                    return false;
                }
                this.mXAxis = xAxis;
            }

            // y axis
            str = el.getAttribute(KEY_Y_AXIS_POSITION);
            if (str.length() != 0) {
                SGAxis yAxis = SGFigureElementSignificantDifference.this.mAxisElement
                        .getAxis(str);
                if (yAxis == null) {
                    return false;
                }
                this.mYAxis = yAxis;
            }

            // left x value
            str = el.getAttribute(KEY_SIGDIFF_LEFT_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double leftXValue = num.doubleValue();
                if (this.mXAxis.isValidValue(leftXValue) == false) {
                    return false;
                }
                this.setLeftXValue(leftXValue);
            }

            // left y value
            str = el.getAttribute(KEY_SIGDIFF_LEFT_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double leftYValue = num.doubleValue();
                if (this.mYAxis.isValidValue(leftYValue) == false) {
                    return false;
                }
                this.setLeftYValue(leftYValue);
            }

            // right x value
            str = el.getAttribute(KEY_SIGDIFF_RIGHT_X_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double rightXValue = num.doubleValue();
                if (this.mXAxis.isValidValue(rightXValue) == false) {
                    return false;
                }
                this.setRightXValue(rightXValue);
            }

            // right y value
            str = el.getAttribute(KEY_SIGDIFF_RIGHT_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double rightYValue = num.doubleValue();
                if (this.mYAxis.isValidValue(rightYValue) == false) {
                    return false;
                }
                this.setRightYValue(rightYValue);
            }

            // horizontal y value
            str = el.getAttribute(KEY_SIGDIFF_HORIZONTAL_Y_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                final double horizontalYValue = num.doubleValue();
                if (this.mYAxis.isValidValue(horizontalYValue) == false) {
                    return false;
                }
                this.setHorizontalYValue(horizontalYValue);
            }
            
            // anchored
            str = el.getAttribute(KEY_SIGDIFF_ANCHORED);
            if (str.length() != 0) {
                final Boolean b = SGUtilityText.getBoolean(str);
                if (b == null) {
                    return false;
                }
                if (this.setAnchored(b.booleanValue()) == false) {
                    return false;
                }
                this.setShapeWithAxesValues();
            }

            return true;
        }

        /**
         * 
         */
        public SGProperties getProperties() {
            SigDiffPropertiesWithAxes p = new SigDiffPropertiesWithAxes();
            if (this.getProperties(p) == false) {
                return null;
            }
            return p;
        }

        /**
         * 
         */
        public boolean getProperties(SGProperties p) {
            if ((p instanceof SigDiffPropertiesWithAxes) == false) {
                return false;
            }
            if (super.getProperties(p) == false) {
                return false;
            }
            SigDiffPropertiesWithAxes sp = (SigDiffPropertiesWithAxes) p;
            sp.mLeftXValue = this.getLeftXValue();
            sp.mLeftYValue = this.getLeftYValue();
            sp.mRightXValue = this.getRightXValue();
            sp.mRightYValue = this.getRightYValue();
            sp.mHorizontalYValue = this.getHorizontalYValue();
            sp.mXAxis = this.mXAxis;
            sp.mYAxis = this.mYAxis;
            sp.mAnchored = this.mIsAnchored;

            return true;
        }

        /**
         * 
         */
        public boolean setProperties(final SGProperties p) {
            if ((p instanceof SigDiffPropertiesWithAxes) == false) {
                return false;
            }
            if (super.setProperties(p) == false) {
                return false;
            }
            SigDiffPropertiesWithAxes sp = (SigDiffPropertiesWithAxes) p;
            this.mXAxis = sp.mXAxis;
            this.mYAxis = sp.mYAxis;
            this.setLeftXValue(sp.mLeftXValue);
            this.setLeftYValue(sp.mLeftYValue);
            this.setRightXValue(sp.mRightXValue);
            this.setRightYValue(sp.mRightYValue);
            this.setHorizontalYValue(sp.mHorizontalYValue);
            this.setAnchored(sp.mAnchored);
            return true;
        }

        /**
         * 
         */
        public void actionPerformed(final ActionEvent e) {

            final String command = e.getActionCommand();
            // final Object source = e.getSource();

            if (command.equals(MENUCMD_PROPERTY)) {
                SGFigureElementSignificantDifference.this
                        .setPropertiesOfSelectedObjects(this);
            } else {
                notifyToListener(command, e.getSource());
            }
        }

        /**
         * 
         */
        public boolean commit() {
            // update history if properties changed after dialog was closed
            SGProperties pTemp = this.mTemporaryProperties;
            SGProperties pPresent = this.getProperties();
            if (pTemp.equals(pPresent) == false) {
                this.mUndoManager.setChanged(true);
            }
            this.setShapeWithAxesValues();
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
            this.mTemporaryProperties = null;
            this.setShapeWithAxesValues();
            notifyChangeOnCancel();
            repaint();
            return true;
        }

        /**
         * 
         */
        public boolean preview() {
            this.setShapeWithAxesValues();
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
                dg = new SGSignificantDifferenceDialog(mDialogOwner, true);
                mPropertyDialog = dg;
            }
            return dg;
        }
        
        /**
         * Returns a list of child nodes.
         * @return
         *       a list of child nodes
         */
        public ArrayList getChildNodes() {
            return new ArrayList();
        }

        /**
         * Returns the location of this symbol.
         * 
         * @return the location of this symbol
         */
        public SGTuple2f getLocation() {
            final float basex = super.getX();
            final float basey = super.getY();
            final float x = this.getMagnification() * basex
                    + SGFigureElementSignificantDifference.this.mGraphRectX;
            final float y = this.getMagnification() * basey
                    + SGFigureElementSignificantDifference.this.mGraphRectY;
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
            final float nx = (x - SGFigureElementSignificantDifference.this.mGraphRectX)
                    / mag;
            final float ny = (y - SGFigureElementSignificantDifference.this.mGraphRectY)
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
        	if (this.equals(mPressedSymbol)) {
        		return;
        	}
        	this.translateSub(dx, dy);
        }
        
        private void translateSub(final float dx, final float dy) {
            this.setLocation(this.getX() + dx, this.getY() + dy);
            this.createDrawingElement();
            this.setAxisValuesWithShape();
            this.setShapeWithAxesValues();
        }

        /**
         * 
         * @return
         */
        public double getLeftXValue() {
            if (!this.isFlippingHorizontal()) {
                return this.mXValue1;
            }

            return this.mXValue2;
        }

        /**
         * 
         * @return
         */
        public double getLeftYValue() {
            if (!this.isFlippingHorizontal()) {
                return this.mYValue1;
            }
            return this.mYValue2;
        }

        /**
         * 
         * @return
         */
        public double getRightXValue() {
            if (!this.isFlippingHorizontal()) {
                return this.mXValue2;
            }
            return this.mXValue1;
        }

        /**
         * 
         * @return
         */
        public double getRightYValue() {
            if (!this.isFlippingHorizontal()) {
                return this.mYValue2;
            }
            return this.mYValue1;
        }

        /**
         * 
         * @return
         */
        public double getHorizontalYValue() {
            return this.mHorizontalYValue;
        }

        /**
         * Sets the x-coordinate of the left end.
         * 
         * @param value
         *           an axis value for the x-coordinate
         * @return true if succeeded
         */
        public boolean setLeftXValue(final double value) {
        	if (this.mXAxis.isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isFlippingHorizontal()) {
                this.mXValue1 = value;
            } else {
                this.mXValue2 = value;
            }
            return true;
        }

        /**
         * Sets the y-coordinate of the left end.
         * 
         * @param value
         *           an axis value for the y-coordinate
         * @return true if succeeded
         */
        public boolean setLeftYValue(final double value) {
        	if (this.mYAxis.isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isFlippingHorizontal()) {
                this.mYValue1 = value;
            } else {
                this.mYValue2 = value;
            }
            return true;
        }

        /**
         * Sets the x-coordinate of the right end.
         * 
         * @param value
         *           an axis value for the x-coordinate
         * @return true if succeeded
         */
        public boolean setRightXValue(final double value) {
        	if (this.mXAxis.isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isFlippingHorizontal()) {
                this.mXValue2 = value;
            } else {
                this.mXValue1 = value;
            }
            return true;
        }

        /**
         * Sets the y-coordinate of the right end.
         * 
         * @param value
         *           an axis value for the y-coordinate
         * @return true if succeeded
         */
        public boolean setRightYValue(final double value) {
        	if (this.mYAxis.isValidValue(value) == false) {
        		return false;
        	}
            if (!this.isFlippingHorizontal()) {
                this.mYValue2 = value;
            } else {
                this.mYValue1 = value;
            }
            return true;
        }

        /**
         * Sets the y-coordinate of the horizontal bar.
         * 
         * @param value
         *           an axis value for the y-coordinate
         * @return true if succeeded
         */
        public boolean setHorizontalYValue(final double value) {
        	if (this.mYAxis.isValidValue(value) == false) {
        		return false;
        	}
            this.mHorizontalYValue = value;
            return true;
        }

        /**
         * Checks whether the axis values are valid.
         * 
         * @return true if all axis values are valid
         */
        public boolean hasValidValues() {
            Number horizontalY = Double.valueOf(this.getHorizontalYValue());
            Number leftX = Double.valueOf(this.getLeftXValue());
            Number leftY = Double.valueOf(this.getLeftYValue());
            Number rightX = Double.valueOf(this.getRightXValue());
            Number rightY = Double.valueOf(this.getRightYValue());

            if (this.mYAxis.isValidValue(horizontalY.doubleValue()) == false) {
                return false;
            }

            if (this.mXAxis.isValidValue(leftX.doubleValue()) == false) {
                return false;
            }

            if (this.mYAxis.isValidValue(leftY.doubleValue()) == false) {
                return false;
            }

            if (this.mXAxis.isValidValue(rightX.doubleValue()) == false) {
                return false;
            }

            if (this.mYAxis.isValidValue(rightY.doubleValue()) == false) {
                return false;
            }

            return true;
        }

        /**
         * Checks whether the y-value of the horizontal bar is valid.
         * 
         * @param location
         *           the location of the axis
         * @param value
         *           the axis value
         * @return true if the axis value is valid
         */
        public boolean hasValidHorizontalYValue(final int location,
                final Number value) {
            final SGAxis axis = (location == -1) ? this.mYAxis
                    : SGFigureElementSignificantDifference.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getHorizontalYValue();
            return axis.isValidValue(v);
        }

        /**
         * Checks whether the x-value of the left end is valid.
         * 
         * @param location
         *           the location of the axis
         * @param value
         *           the axis value
         * @return true if the axis value is valid
         */
        public boolean hasValidLeftXValue(final int location, final Number value) {
            final SGAxis axis = (location == -1) ? this.mXAxis
                    : SGFigureElementSignificantDifference.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getLeftXValue();
            return axis.isValidValue(v);
        }

        /**
         * Checks whether the y-value of the left end is valid.
         * 
         * @param location
         *           the location of the axis
         * @param value
         *           the axis value
         * @return true if the axis value is valid
         */
        public boolean hasValidLeftYValue(final int location, final Number value) {
            final SGAxis axis = (location == -1) ? this.mYAxis
                    : SGFigureElementSignificantDifference.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getLeftYValue();
            return axis.isValidValue(v);
        }

        /**
         * Checks whether the x-value of the right end is valid.
         * 
         * @param location
         *           the location of the axis
         * @param value
         *           the axis value
         * @return true if the axis value is valid
         */
        public boolean hasValidRightXValue(final int location, final Number value) {
            final SGAxis axis = (location == -1) ? this.mXAxis
                    : SGFigureElementSignificantDifference.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getRightXValue();
            return axis.isValidValue(v);
        }

        /**
         * Checks whether the y-value of the right end is valid.
         * 
         * @param location
         *           the location of the axis
         * @param value
         *           the axis value
         * @return true if the axis value is valid
         */
        public boolean hasValidRightYValue(final int location, final Number value) {
            final SGAxis axis = (location == -1) ? this.mYAxis
                    : SGFigureElementSignificantDifference.this.mAxisElement
                            .getAxisInPlane(location);
            final double v = (value != null) ? value.doubleValue() : this
                    .getRightYValue();
            return axis.isValidValue(v);
        }

        @Override
        public boolean isAnchored() {
            return this.mIsAnchored;
        }

        @Override
        public boolean setAnchored(final boolean anchored) {
            this.mIsAnchored = anchored;
            return true;
        }

        /**
         * 
         */
        private boolean pressDrawingElements(final MouseEvent e) {

            final int x = e.getX();
            final int y = e.getY();

            if (this.contains(x, y) || this.getStringElement().contains(x, y)) {

                final float mag = this.getMagnification();

                // set the temporary object
                this.mTempSymbol = new SigDiffSymbol();
                this.mTempSymbol.setMagnification(mag);
                this.mTempSymbol.setLocation(this.getX(), this.getY());
                this.mTempSymbol.setSize(this.getWidth(), this
                        .getVerticalHeight1(), this
                        .getVerticalHeight2());

                this.mFlippingHorizontalFlag = this.isFlippingHorizontal();

                this.mMouseLocation = this.getMouseLocation(x, y);
                Cursor cur = null;
                if (this.mMouseLocation == BODY
                        || this.mMouseLocation == ON_STRING) {
                    cur = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
                } else {
                    this.getCursor(this.mMouseLocation);
                }
                setMouseCursor(cur);

                return true;
            }

            return false;
        }

        /**
         * 
         */
        private boolean mFlippingHorizontalFlag;

        /**
         * 
         * @return
         */
        private ArrayList getAnchorPointList() {
            ArrayList<Point2D> list = new ArrayList<Point2D>();
            if (this.isLineVisible()) {
                list.add(this.getLeftTerm());
                list.add(this.getLeftMiddle());
                list.add(this.getLeftJoint());
                list.add(this.getHorizontalMiddle());
                list.add(this.getRightJoint());
                list.add(this.getRightMiddle());
                list.add(this.getRightTerm());
            }

            Rectangle2D rect = ((SGDrawingElementString2DExtended) this
                    .getStringElement()).getElementBounds();
            list.addAll(SGUtilityForFigureElementJava2D.getAnchorPointList(rect));

            return list;
        }

        /**
         * Location of mouse pointer.
         */
        private int mMouseLocation;

        /**
         * 
         * @param pos
         * @param radius
         * @param x
         * @param y
         * @return
         */
        private boolean isInside(final Point2D pos, final int radius,
                final int x, final int y) {
            return ((Math.abs(pos.getX() - x) < radius) && (Math.abs(pos.getY()
                    - y) < radius));
        }

        private int getMouseLocation(final int x, final int y) {
            final int radius = (int) (1.25f * ANCHOR_SIZE_FOR_FOCUSED_OBJECTS);

            final Point2D posHorizontalMiddle = this.getHorizontalMiddle();
            final Point2D posLeftMiddle = this.getLeftMiddle();
            final Point2D posRightMiddle = this.getRightMiddle();
            final Point2D posLeftJoint = this.getLeftJoint();
            final Point2D posRightJoint = this.getRightJoint();
            final Point2D posLeftTerm = this.getLeftTerm();
            final Point2D posRightTerm = this.getRightTerm();

            int location = -1;

            if (this.isLineVisible()) {
                if (this.isInside(posHorizontalMiddle, radius, x, y)) {
                    location = HORIZONTAL_MIDDLE;
                } else if (this.isInside(posLeftJoint, radius, x, y)) {
                    location = LEFT_JOINT;
                } else if (this.isInside(posRightJoint, radius, x, y)) {
                    location = RIGHT_JOINT;
                } else if (this.isInside(posLeftTerm, radius, x, y)) {
                    location = LEFT_TERM;
                } else if (this.isInside(posLeftMiddle, radius, x, y)) {
                    location = LEFT_MIDDLE;
                } else if (this.isInside(posRightTerm, radius, x, y)) {
                    location = RIGHT_TERM;
                } else if (this.isInside(posRightMiddle, radius, x, y)) {
                    location = RIGHT_MIDDLE;
                } else {
                    location = BODY;
                }
            }

            if (this.getStringElement().contains(x, y)) {
                location = ON_STRING;
            }

            return location;
        }

        /**
         * 
         */
        private Cursor getCursor(final int location) {

            Cursor cur = null;
            switch (location) {
            case HORIZONTAL_MIDDLE: {
                if (!this.isFlippingVertical()) {
                    cur = new Cursor(Cursor.N_RESIZE_CURSOR);
                } else {
                    cur = new Cursor(Cursor.S_RESIZE_CURSOR);
                }
                break;
            }
            case LEFT_MIDDLE: {
                cur = new Cursor(Cursor.W_RESIZE_CURSOR);
                break;
            }
            case RIGHT_MIDDLE: {
                cur = new Cursor(Cursor.E_RESIZE_CURSOR);
                break;
            }
            case LEFT_TERM: {
                if (!this.isFlippingVerticalLeft()) {
                    cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
                } else {
                    cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
                }
                break;
            }
            case LEFT_JOINT: {
                if (!this.isFlippingVerticalLeft()) {
                    cur = new Cursor(Cursor.NW_RESIZE_CURSOR);
                } else {
                    cur = new Cursor(Cursor.SW_RESIZE_CURSOR);
                }
                break;
            }
            case RIGHT_TERM: {
                if (!this.isFlippingVerticalRight()) {
                    cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
                } else {
                    cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
                }
                break;
            }
            case RIGHT_JOINT: {
                if (!this.isFlippingVerticalRight()) {
                    cur = new Cursor(Cursor.NE_RESIZE_CURSOR);
                } else {
                    cur = new Cursor(Cursor.SE_RESIZE_CURSOR);
                }
                break;
            }
            case ON_STRING: {
                cur = new Cursor(Cursor.HAND_CURSOR);
                break;
            }
            default: {
                cur = new Cursor(Cursor.HAND_CURSOR);
            }
            }

            // System.out.println(cur);

            return cur;

        }

        /**
         * 
         */
        private boolean dragOtherPoint(final MouseEvent e) {
            // parallel displacement
            if (SGFigureElementSignificantDifference.this.mPressedPoint != null) {
                // set the location to the symbol
            	MouseDragResult result = SGFigureElementSignificantDifference.this.getMouseDragResult(e);
            	final int dx = result.dx;
            	final int dy = result.dy;
                this.translateSub(dx, dy);
                SGFigureElementSignificantDifference.this.mPressedPoint = e
                        .getPoint();
            }

            return true;
        }

        /**
         * 
         */
        private boolean drag(final MouseEvent e) {

            if (SGFigureElementSignificantDifference.this.mPressedPoint == null) {
                return false;
            }

            if (this.mMouseLocation == BODY || this.mMouseLocation == ON_STRING) {
                return this.dragOtherPoint(e);
            }

            final float mag = this.getMagnification();

            SigDiffSymbol temp = this.mTempSymbol;
            final float xOld = temp.getX();
            final float yOld = temp.getY();
            final float wOld = mag * temp.getWidth();
            final float h1Old = mag * temp.getVerticalHeight1();
            final float h2Old = mag * temp.getVerticalHeight2();

        	MouseDragResult result = SGFigureElementSignificantDifference.this.getMouseDragResult(e);
        	final int diffX = result.dx;
        	final int diffY = result.dy;

            final float sizeOldX = wOld;
            final float sizeOldY1 = h1Old;
            final float sizeOldY2 = h2Old;

            float sizeNewX = 0.0f;
            float sizeNewY1 = 0.0f;
            float sizeNewY2 = 0.0f;

            // new values
            float x = xOld;
            float y = yOld;
            float w = wOld;
            float h1 = h1Old;
            float h2 = h2Old;

            // switch operation by different dragging point

            final int loc = this.mMouseLocation;
            final boolean flag = this.mFlippingHorizontalFlag;
            if (loc == HORIZONTAL_MIDDLE) {
                // System.out.println("NORTH");
                sizeNewY1 = sizeOldY1 - diffY;
                sizeNewY2 = sizeOldY2 - diffY;

                y = yOld + sizeOldY1 - sizeNewY1;
                h1 = sizeNewY1;
                h2 = sizeNewY2;
            } else if ((loc == LEFT_MIDDLE && !flag)
                    || (loc == RIGHT_MIDDLE && flag)) {
                sizeNewX = sizeOldX - diffX;
                x = xOld + sizeOldX - sizeNewX;
                w = sizeNewX;
            } else if ((loc == RIGHT_MIDDLE && !flag)
                    || (loc == LEFT_MIDDLE && flag)) {
                sizeNewX = sizeOldX + diffX;
                w = sizeNewX;
            } else if ((loc == LEFT_JOINT && !flag)
                    || (loc == RIGHT_JOINT && flag)) {
                sizeNewY1 = sizeOldY1 - diffY;
                sizeNewY2 = sizeOldY2 - diffY;
                sizeNewX = sizeOldX - diffX;

                x = xOld + sizeOldX - sizeNewX;
                y = yOld + sizeOldY1 - sizeNewY1;
                w = sizeNewX;
                h1 = sizeNewY1;
                h2 = sizeNewY2;
            } else if ((loc == RIGHT_JOINT && !flag)
                    || (loc == LEFT_JOINT && flag)) {
                sizeNewX = sizeOldX + diffX;
                sizeNewY1 = sizeOldY1 - diffY;
                sizeNewY2 = sizeOldY2 - diffY;

                y = yOld + sizeOldY2 - sizeNewY2;
                w = sizeNewX;
                h1 = sizeNewY1;
                h2 = sizeNewY2;
            } else if ((loc == LEFT_TERM && !flag)
                    || (loc == RIGHT_TERM && flag)) {
                sizeNewX = sizeOldX - diffX;
                sizeNewY1 = sizeOldY1 + diffY;

                x = xOld + sizeOldX - sizeNewX;
                w = sizeNewX;
                h1 = sizeNewY1;
            } else if ((loc == RIGHT_TERM && !flag)
                    || (loc == LEFT_TERM && flag)) {
                sizeNewX = sizeOldX + diffX;
                sizeNewY2 = sizeOldY2 + diffY;

                w = sizeNewX;
                h2 = sizeNewY2;
            }

            // update the pressed point
            SGFigureElementSignificantDifference.this.mPressedPoint
                    .setLocation(
                            SGFigureElementSignificantDifference.this.mPressedPoint
                                    .getX()
                                    + diffX,
                            SGFigureElementSignificantDifference.this.mPressedPoint
                                    .getY()
                                    + diffY);

            // set properties
            this.setLocation(x, y);
            this.setSize(w / mag, h1 / mag, h2 / mag);
            this.createDrawingElement();

            temp.setLocation(x, y);
            temp.setSize(w / mag, h1 / mag, h2 / mag);

            if (loc == LEFT_TERM || loc == LEFT_JOINT || loc == RIGHT_TERM
                    || loc == RIGHT_JOINT) {
                final int location = this.getMouseLocation(e.getX(), e.getY());
                setMouseCursor(this.getCursor(location));
            }

            return true;

        }

        private boolean setShapeWithAxesValues() {
            final float x1 = calcLocation(this.mXValue1, this.mXAxis, true);
            final float x2 = calcLocation(this.mXValue2, this.mXAxis, true);
            final float y = calcLocation(this.mHorizontalYValue, this.mYAxis,
                    false);
            final float y1 = calcLocation(this.mYValue1, this.mYAxis, false);
            final float y2 = calcLocation(this.mYValue2, this.mYAxis, false);

            if (Float.isNaN(x1) || Float.isNaN(x2) || Float.isNaN(y)
                    || Float.isNaN(y1) || Float.isNaN(y2)) {
                this.setValid(false);
                return false;
            }
            this.setValid(true);

            this.setNodePointLocation(x1, y1, x2, y2, y);

            this.createDrawingElement();

            return true;
        }

        private boolean mValidFlag = true;

        public boolean isValid() {
            return this.mValidFlag;
        }

        public void setValid(final boolean b) {
            this.mValidFlag = b;
        }

        private boolean setAxisValuesWithShape() {
            final double xValue1 = calcValue(this.getX1(), this.mXAxis, true);
            final double yValue1 = calcValue(this.getY1(), this.mYAxis, false);
            final double xValue2 = calcValue(this.getX2(), this.mXAxis, true);
            final double yValue2 = calcValue(this.getY2(), this.mYAxis, false);
            final double hYValue = calcValue(this.getY(), this.mYAxis, false);

            this.mXValue1 = SGUtilityNumber.getNumberInRangeOrder(xValue1, this.mXAxis);
            this.mYValue1 = SGUtilityNumber.getNumberInRangeOrder(yValue1, this.mYAxis);
            this.mXValue2 = SGUtilityNumber.getNumberInRangeOrder(xValue2, this.mXAxis);
            this.mYValue2 = SGUtilityNumber.getNumberInRangeOrder(yValue2, this.mYAxis);
            this.mHorizontalYValue = SGUtilityNumber.getNumberInRangeOrder(hYValue, this.mYAxis);

            this.setValid(true);
            this.createDrawingElement();
            
            return true;
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

        /**
         * undo
         */
        public boolean setMementoBackward() {
            if (this.mUndoManager.setMementoBackward() == false) {
                return false;
            }

            this.setShapeWithAxesValues();

            if (this.isValid()) {
                this.createDrawingElement();
            }

            return true;
        }

        /**
         * redo
         */
        public boolean setMementoForward() {
            if (this.mUndoManager.setMementoForward() == false) {
                return false;
            }

            this.setShapeWithAxesValues();

            if (this.isValid()) {
                this.createDrawingElement();
            }

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
         * update history. it will called first when undo
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
            SGFigureElementSignificantDifference.this.notifyToRoot();
        }

        @Override
        public float getMagnification() {
            return SGFigureElementSignificantDifference.this.getMagnification();
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
                
                if (COM_SIGDIFF_AXIS_X.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_SIGDIFF_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setXAxisLocation(loc) == false) {
                        result.putResult(COM_SIGDIFF_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_AXIS_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_AXIS_Y.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_SIGDIFF_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setYAxisLocation(loc) == false) {
                        result.putResult(COM_SIGDIFF_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_AXIS_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_LOCATION_LEFT_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_SIGDIFF_LOCATION_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLeftXValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_LEFT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_LOCATION_LEFT_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_LOCATION_LEFT_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_SIGDIFF_LOCATION_LEFT_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_LEFT_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setLeftYValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_LEFT_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_LOCATION_LEFT_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_LOCATION_RIGHT_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_SIGDIFF_LOCATION_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setRightXValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_RIGHT_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_LOCATION_RIGHT_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_LOCATION_RIGHT_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_SIGDIFF_LOCATION_RIGHT_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_RIGHT_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setRightYValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_RIGHT_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_LOCATION_RIGHT_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_LOCATION_HORIZONTAL_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_SIGDIFF_LOCATION_HORIZONTAL_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_HORIZONTAL_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setHorizontalYValue(num.doubleValue()) == false) {
                        result.putResult(COM_SIGDIFF_LOCATION_HORIZONTAL_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_LOCATION_HORIZONTAL_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_LINE_VISIBLE.equalsIgnoreCase(key)) {
                	Boolean b = SGUtilityText.getBoolean(value);
                	if (b == null) {
                        result.putResult(COM_SIGDIFF_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setLineVisible(b.booleanValue()) == false) {
                        result.putResult(COM_SIGDIFF_LINE_VISIBLE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_LINE_VISIBLE, SGPropertyResults.SUCCEEDED);
                } else if (COM_SPACE.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                	if (num == null) {
                        result.putResult(COM_SPACE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setSpace(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_SPACE, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SPACE, SGPropertyResults.SUCCEEDED);
                } else if (COM_LINE_WIDTH.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                	if (num == null) {
                        result.putResult(COM_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setLineWidth(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_TEXT.equalsIgnoreCase(key)) {
                	if (map.isDoubleQuoted(key) == false) {
                        result.putResult(COM_SIGDIFF_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setText(value) == false) {
                        result.putResult(COM_SIGDIFF_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_TEXT, SGPropertyResults.SUCCEEDED);
                } else if (COM_FONT_NAME.equalsIgnoreCase(key)) {
                	final String name = SGUtility.findFontFamilyName(value);
                	if (name == null) {
                        result.putResult(COM_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setFontName(name) == false) {
                        result.putResult(COM_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_FONT_NAME, SGPropertyResults.SUCCEEDED);
                } else if (COM_FONT_STYLE.equalsIgnoreCase(key)) {
                    Integer style = SGUtilityText.getFontStyle(value);
                    if (style == null) {
                        result.putResult(COM_FONT_STYLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setFontStyle(style.intValue()) == false) {
                        result.putResult(COM_FONT_STYLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_FONT_STYLE,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_FONT_SIZE.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_FONT_SIZE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setFontSize(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_FONT_SIZE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_FONT_SIZE,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.getColor(value);
                    if (cl != null) {
                        if (this.setColor(cl) == false) {
                            result.putResult(COM_SIGDIFF_COLOR,
                                    SGPropertyResults.INVALID_INPUT_VALUE);
                            continue;
                        }
                    } else {
                    	cl = SGUtilityText.parseColor(value);
    					if (cl == null) {
    						result.putResult(COM_SIGDIFF_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
    					if (this.setColor(cl) == false) {
    						result.putResult(COM_SIGDIFF_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
                    }
                    result.putResult(COM_SIGDIFF_COLOR,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_SIGDIFF_ANCHORED.equalsIgnoreCase(key)) {
                    Boolean b = SGUtilityText.getBoolean(value);
                    if (b == null) {
                        result.putResult(COM_SIGDIFF_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAnchored(b.booleanValue()) == false) {
                        result.putResult(COM_SIGDIFF_ANCHORED, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_SIGDIFF_ANCHORED, SGPropertyResults.SUCCEEDED);
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
        	return SGCommandUtility.createCommandString(COM_SIGNIFICANT_DIFFERENCE, 
    				Integer.toString(this.mID), this.getCommandPropertyMap(params));
        }

    	@Override
        public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	this.addProperties(map, KEY_SIGDIFF_LEFT_X_VALUE, KEY_SIGDIFF_LEFT_Y_VALUE, 
        			KEY_SIGDIFF_RIGHT_X_VALUE, KEY_SIGDIFF_RIGHT_Y_VALUE, 
        			KEY_SIGDIFF_HORIZONTAL_Y_VALUE, KEY_X_AXIS_POSITION, 
        			KEY_Y_AXIS_POSITION, KEY_SIGDIFF_ANCHORED);
        	return map;
        }
        
        /**
         * Creates and returns the map of properties.
         * 
         * @return the map of properties
         */
    	@Override
        public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
        	SGPropertyMap map = super.getCommandPropertyMap(params);
        	this.addProperties(map, COM_SIGDIFF_LOCATION_LEFT_X, COM_SIGDIFF_LOCATION_LEFT_Y, 
        			COM_SIGDIFF_LOCATION_RIGHT_X, COM_SIGDIFF_LOCATION_RIGHT_Y, 
        			COM_SIGDIFF_LOCATION_HORIZONTAL_Y, COM_SIGDIFF_AXIS_X, COM_SIGDIFF_AXIS_Y, 
        			COM_SIGDIFF_ANCHORED);
			return map;
		}
    	
        private void addProperties(SGPropertyMap map, String leftXKey, String leftYKey,
        		String rightXKey, String rightYKey, String horizontalYKey,
        		String axisXKey, String axisYKey, String anchorKey) {
            SGPropertyUtility.addProperty(map, leftXKey, 
            		this.getLeftXValue());
            SGPropertyUtility.addProperty(map, leftYKey, 
            		this.getLeftYValue());
            SGPropertyUtility.addProperty(map, rightXKey, 
            		this.getRightXValue());
            SGPropertyUtility.addProperty(map, rightYKey, 
            		this.getRightYValue());
            SGPropertyUtility.addProperty(map, horizontalYKey, 
            		this.getHorizontalYValue());
            SGPropertyUtility.addProperty(map, axisXKey, 
            		mAxisElement.getLocationName(this.mXAxis));
            SGPropertyUtility.addProperty(map, axisYKey, 
            		mAxisElement.getLocationName(this.mYAxis));
            SGPropertyUtility.addProperty(map, anchorKey, this.isAnchored());
        }

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
    }

    public static class SigDiffPropertiesWithAxes extends SigDiffProperties {

        private double mLeftXValue;

        private double mRightXValue;

        private double mLeftYValue;

        private double mRightYValue;

        private double mHorizontalYValue;

        private SGAxis mXAxis;

        private SGAxis mYAxis;
        
        private boolean mAnchored;

        public SigDiffPropertiesWithAxes() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof SigDiffPropertiesWithAxes) == false)
                return false;

            if (super.equals(obj) == false)
                return false;

            SigDiffPropertiesWithAxes p = (SigDiffPropertiesWithAxes) obj;

            if (p.mLeftXValue != this.mLeftXValue)
                return false;
            if (p.mRightXValue != this.mRightXValue)
                return false;
            if (p.mLeftYValue != this.mLeftYValue)
                return false;
            if (p.mRightYValue != this.mRightYValue)
                return false;
            if (p.mHorizontalYValue != this.mHorizontalYValue)
                return false;
            if (p.mXAxis.equals(this.mXAxis) == false)
                return false;
            if (p.mYAxis.equals(this.mYAxis) == false)
                return false;
            if (p.mAnchored != this.mAnchored)
                return false;

            return true;
        }

        public double getLeftXValue() {
            return this.mLeftXValue;
        }

        public double getLeftYValue() {
            return this.mLeftYValue;
        }

        public double getRightXValue() {
            return this.mRightXValue;
        }

        public double getRightYValue() {
            return this.mRightYValue;
        }

        public double getHorizontalYValue() {
            return this.mHorizontalYValue;
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

        public void setLeftXValue(final double value) {
            this.mLeftXValue = value;
        }

        public void setLeftYValue(final double value) {
            this.mLeftYValue = value;
        }

        public void setRightXValue(final double value) {
            this.mRightXValue = value;
        }

        public void setRightYValue(final double value) {
            this.mRightYValue = value;
        }

        public void setHorizontalYValue(final double value) {
            this.mHorizontalYValue = value;
        }

        public void setXAxis(SGAxis axis) {
            if (axis == null) {
                throw new IllegalArgumentException("axis==null");
            }
            this.mXAxis = axis;
        }

        public void setYAxis(SGAxis axis) {
            if (axis == null) {
                throw new IllegalArgumentException("axis==null");
            }
            this.mYAxis = axis;
        }
        
        public void setAnchored(final boolean anchored) {
            this.mAnchored = anchored;
        }
    }

    /**
     * Called when the key is pressed.
     */
    public void keyPressed(KeyEvent e) {
    }

    /**
     * Called when the key is released.
     */
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Called when the key is typed.
     */
    public void keyTyped(KeyEvent e) {
	Object source = e.getSource();
	char c = e.getKeyChar();
	
	// if the text field is visible
	if (source.equals(this.mEditField)) {
	    
	    // hide the text field
	    if (c == KeyEvent.VK_ESCAPE) {
		this.hideEditField();
		this.mEditingSymbol = null;
	    }
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
                    mPropertyDialog = new SGSignificantDifferenceDialog(mDialogOwner, true);
                }
            }
        });
        return true;
    }

    
    /**
     * Called when the text string in the text field is updated.
     * 
     * @param e
     *          a document event
     */
    public void changedUpdate(DocumentEvent e) {
        this.updateLabelTextField();
    }

    /**
     * Called when the text string in the text field is updated.
     * 
     * @param e
     *          a document event
     */
    public void insertUpdate(DocumentEvent e) {
        this.updateLabelTextField();
    }

    /**
     * Called when the text string in the text field is updated.
     * 
     * @param e
     *          a document event
     */
    public void removeUpdate(DocumentEvent e) {
        this.updateLabelTextField();
    }

    /**
     * Update the text field.
     *
     */
    private void updateLabelTextField() {
        if (this.mEditingSymbol != null) {
            
            // create a font
            final Font font = new Font(this.mEditingSymbol.getFontName(),
                    this.mEditingSymbol.getFontStyle(), (int) (this.mEditingSymbol
                            .getFontSize() * this.getMagnification()));

            // update the text field
            this.updateTextField(this.mEditField, font);            
        }
    }
    
    /**
     * Called when the caret in the text field is update.
     */
    public void caretUpdate(final CaretEvent e) {
//        final String str = this.mEditField.getText();
//
//        if (this.mEditingSymbol != null) {
//            final Font font = new Font(this.mEditingSymbol.getFontName(),
//                    this.mEditingSymbol.getFontStyle(),
//                    (int) (this.mEditingSymbol.getFontSize() * this
//                            .getMagnification()));
//            final Rectangle2D stringRect = font.getStringBounds(str,
//                    new FontRenderContext(null, false, false));
//
//            final double width = stringRect.getWidth();
//            if (width > this.mEditField.getWidth()) {
//                this.mEditField.setSize((int) (stringRect.getWidth() + this
//                        .getMagnification()
//                        * this.mEditingSymbol.getFontSize()), this.mEditField
//                        .getHeight());
//            }
//        }
    }
 
    /**
     * Called when menu items in the menu bar is selected.
     *
     */
    public void onMenuSelected() {
        this.closeTextField();
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
            String sHorizontalY = null;
            String sLeftX = null;
            String sLeftY = null;
            String sRightX = null;
            String sRightY = null;
            String sAxisX = null;
            String sAxisY = null;
            Iterator itr = map.getKeyIterator();
            while (itr.hasNext()) {
                Object keyObj = itr.next();
                String key = keyObj.toString();
                if (COM_SIGDIFF_LOCATION_HORIZONTAL_Y.equalsIgnoreCase(key)) {
                	sHorizontalY = map.getValueString(key);
                } else if (COM_SIGDIFF_LOCATION_LEFT_X.equalsIgnoreCase(key)) {
                	sLeftX = map.getValueString(key);
                } else if (COM_SIGDIFF_LOCATION_LEFT_Y.equalsIgnoreCase(key)) {
                	sLeftY = map.getValueString(key);
                } else if (COM_SIGDIFF_LOCATION_RIGHT_X.equalsIgnoreCase(key)) {
                	sRightX = map.getValueString(key);
                } else if (COM_SIGDIFF_LOCATION_RIGHT_Y.equalsIgnoreCase(key)) {
                	sRightY = map.getValueString(key);
                } else if (COM_SIGDIFF_AXIS_X.equalsIgnoreCase(key)) {
                	sAxisX = map.getValueString(key);
                } else if (COM_SIGDIFF_AXIS_Y.equalsIgnoreCase(key)) {
                	sAxisY = map.getValueString(key);
                }
            }
            if (sHorizontalY == null) {
                return null;
            }
            if (sLeftX == null) {
                return null;
            }
            if (sLeftY == null) {
                return null;
            }
            if (sRightX == null) {
                return null;
            }
            if (sRightY == null) {
                return null;
            }
            Double horizontalY = SGUtilityText.getDouble(sHorizontalY);
            Double leftX = SGUtilityText.getDouble(sLeftX);
            Double leftY = SGUtilityText.getDouble(sLeftY);
            Double rightX = SGUtilityText.getDouble(sRightX);
            Double rightY = SGUtilityText.getDouble(sRightY);
            if (horizontalY == null) {
                return null;
            }
            if (leftX == null) {
                return null;
            }
            if (leftY == null) {
                return null;
            }
            if (rightX == null) {
                return null;
            }
            if (rightY == null) {
                return null;
            }
        	if (SGUtility.isValidPropertyValue(leftX.doubleValue()) == false) {
                return null;
        	}
        	if (SGUtility.isValidPropertyValue(leftY.doubleValue()) == false) {
                return null;
        	}
        	if (SGUtility.isValidPropertyValue(rightX.doubleValue()) == false) {
                return null;
        	}
        	if (SGUtility.isValidPropertyValue(rightY.doubleValue()) == false) {
                return null;
        	}
        	if (SGUtility.isValidPropertyValue(horizontalY.doubleValue()) == false) {
                return null;
        	}

            if (this.addSignificantDifferenceSymbol(id, 
                    leftX.doubleValue(), leftY.doubleValue(), rightX.doubleValue(),
                    rightY.doubleValue(), horizontalY.doubleValue(), sAxisX, sAxisY) == false) {
                return null;
            }
            
            child = this.getVisibleChild(id);
        }

        // set properties to the child object
        SGPropertyResults result = child.setProperties(map);

        return result;
    }
}
