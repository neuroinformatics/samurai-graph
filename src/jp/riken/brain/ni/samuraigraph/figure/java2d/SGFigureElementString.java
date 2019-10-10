package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString.StringProperties;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class managing labels.
 */
public class SGFigureElementString extends SGFigureElement2D implements
        SGIFigureElementString, SGIStringConstants, CaretListener, DocumentListener, 
        ActionListener, KeyListener {

    /**
     * 
     */
    private SGIFigureElementAxis mAxisElement = null;

    /**
     * 
     */
    private LabelElement mEditedLabelElement = null;

    /**
     * A temporary point.
     */
    private Point mTempPoint = new Point();
    
    /**
     * The text field to input a text string.
     */
    private JTextField mEditField = null;

    private static final int BOUNDARY_LINE_WIDTH = 6;
    
    /**
     * The property dialog of labels.
     */
    private SGPropertyDialog mPropertyDialog = null;


    /**
     * The default constructor.
     */
    public SGFigureElementString() {
        super();
        this.initEditField();
    }

    // initialize the text field
    private boolean initEditField() {
        this.mEditField = new JTextField(10);
        JTextField tf = this.mEditField;
        tf.setVisible(false);
        tf.addActionListener(this);
        tf.addCaretListener(this);
        tf.getDocument().addDocumentListener(this);
        tf.addKeyListener(this);
        return true;
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
        return "Labels";
    }

    public void setComponent(JComponent com) {
        super.setComponent(com);
        com.add(this.mEditField);
    }

    /**
     * 
     * @param element
     */
    public void setAxisElement(final SGIFigureElementAxis element) {
        this.mAxisElement = element;
    }

    /**
     * Insert a label at a given point with default axes. The text string must be input later.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return true if succeeded
     */
    public boolean addString(final int x, final int y) {

        // if the graph rectangle does not contain the point, returns false
        if (this.getGraphRect().contains(x, y) == false) {
            return false;
        }

        // prepare
        this.clearFocusedObjects();

        // set the location to attributes
        this.mTempPoint = new Point(x, y);

        JTextField tf = this.mEditField;

        // set the font
        final float fontSize = DEFAULT_LABEL_FONT_SIZE * this.mMagnification;
        Font font = new Font(DEFAULT_LABEL_FONT_NAME, DEFAULT_LABEL_FONT_STYLE,
                (int) fontSize);
        tf.setFont(font);

        // set the location and the size
        final String str = "   ";
        final Rectangle2D stringRect = font.getStringBounds(str,
                new FontRenderContext(null, false, false));
        final int lx = x - this.mEditField.getInsets().left;
        final int ly = y - (int) (fontSize / 2.0f);
        final int width = (int) (stringRect.getWidth() + fontSize);
        final int height = (int) (stringRect.getHeight() + fontSize);
        tf.setLocation(lx, ly);
        tf.setSize(width, height);
        
        // set the text
        tf.setText("");

        // show the text field
        tf.setVisible(true);
        tf.requestFocus();

        this.repaint();

        return true;
    }
    
    /**
     * Insert a label at a given point with default axes.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     * @return id of string label if succeeds. or -1.
     */
    @Override
    public int addNewString(final String str, final int x, final int y) {
        SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_LABEL_HORIZONTAL_AXIS);
        SGAxis yAxis = this.mAxisElement.getAxis(DEFAULT_LABEL_VERTICAL_AXIS);
        int id = this.assignChildId();
        if (this.addString(id, str, x, y, xAxis, yAxis)==false) {
            return -1;
        } else {
            return id;
        }
    }

    /**
     * Inserts a label at a given point with given axes.
     * 
     * @param id
     *          the ID to set
     * @param str
     *          the text string
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
    private boolean addString(final int id, final String str, final float x, 
            final float y, final SGAxis xAxis, final SGAxis yAxis) {
        
        final LabelElement label = new LabelElement(str);
        label.setMagnification(this.mMagnification);
        label.mXAxis = xAxis;
        label.mYAxis = yAxis;
        label.setLocation(x, y);

        // add to the list
        if (this.addToList(id, label) == false) {
            return false;
        }

        // initialize history
        label.initPropertiesHistory();

        this.setChanged(true);

        return true;
    }

    /**
     * Insert a label at a given point with given axis values with default axes.
     * 
     * @param id
     *          the ID to set
     * @param str
     *          a text string to insert
     * @param x
     *          axis value for default x-axis
     * @param y
     *          axis value for default y-axis
     * @return true if succeeded
     */
    public boolean addString(final int id, final String str, final double x, 
            final double y) {
        return this.addString(id, str, x, y, DEFAULT_LABEL_HORIZONTAL_AXIS, 
                DEFAULT_LABEL_VERTICAL_AXIS);
    }

    /**
     * Insert a label with given axis values with given axes.
     * 
     * @param id
     *          the ID to set
     * @param str
     *          a text string to insert
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
    public boolean addString(final int id, final String str, final double x, 
            final double y, final String xAxisLocation, final String yAxisLocation) {
        
        // check the input
        if (SGUtilityText.isValidString(str) == false) {
            return false;
        }
        
        // get axes
        String xAxisName = (xAxisLocation != null) ? xAxisLocation : DEFAULT_LABEL_HORIZONTAL_AXIS;
        String yAxisName = (yAxisLocation != null) ? yAxisLocation : DEFAULT_LABEL_VERTICAL_AXIS;
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
        
        // calculate the location to insert a label
        final float xCoordinate = this.calcLocation(x, xAxis, true);
        final float yCoordinate = this.calcLocation(y, yAxis, false);

        // insert a label
        if (this.addString(id, str, xCoordinate, yCoordinate, 
                xAxis, yAxis) == false) {
            return false;
        }
        this.notifyToRoot();
        
        // repaint
        this.repaint();

        return true;
    }
    
    @Override
    public String getString(final int id) {
        SGIChildObject child = this.getVisibleChild(id);
        if (child instanceof LabelElement) {
            return ((LabelElement)child).getString();
        }
        return null;
    }
    
    @Override
    public boolean setString(final int id, final String newText) {
        SGIChildObject child = this.getVisibleChild(id);
        if (child instanceof LabelElement) {
            LabelElement labelElement = (LabelElement)child;
            if (labelElement.getString().equals(newText)) {
                return false;
            }
            if (labelElement.setString(newText)==false) {
                return false;
            }
            labelElement.setChanged(true);
            this.repaint();
            return true;
        }
        return false;
    }
    
    @Override
    public void setVisible(final int id, final boolean visible) {
        SGIChildObject child = this.getVisibleChild(id);
        if (null!=child) {
            child.setVisible(visible);
        }
    }

    /**
     * 
     */
    public void paintGraphics(Graphics g, boolean clip) {
        final Graphics2D g2d = (Graphics2D) g;

        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = 0; ii < list.size(); ii++) {
            final LabelElement el = (LabelElement) list.get(ii);
            // draw string
            el.paint(g2d);
        }

        // draw symbols around all objects
        if (this.mSymbolsVisibleFlagAroundAllObjects) {
            for (int ii = 0; ii < list.size(); ii++) {
                LabelElement el = (LabelElement) list.get(ii);
                ArrayList pList = el.getAnchorPointList();
                SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(pList,
                        g2d);
            }
        }

        // draw symbols around focused objects
        if (this.mSymbolsVisibleFlagAroundFocusedObjects) {
            ArrayList fList = new ArrayList();
            this.getFocusedObjectsList(fList);
            for (int ii = 0; ii < fList.size(); ii++) {
                LabelElement el = (LabelElement) fList.get(ii);
                ArrayList pList = el.getAnchorPointList();
                SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
                        pList, g2d);
            }
        }

        // draw border line of the text field
        JTextField tf = this.mEditField;
        if (tf.isVisible()) {
            g2d.setPaint(Color.GRAY);
            g2d.setStroke(new BasicStroke(BOUNDARY_LINE_WIDTH));
            g2d.draw(tf.getBounds());
            g2d.setStroke(new BasicStroke(2));
            tf.repaint();
        }

    }

    /**
     * Synchronize with other figure elements.
     */
    public boolean synchronize(final SGIFigureElement element, final String msg) {

        boolean flag = true;
        if (element instanceof SGIFigureElementGraph) {
            if (SGIFigureElement.NOTIFY_DATA_WILL_BE_HIDDEN.equals(msg)) {
                SGIFigureElementGraph gElement = (SGIFigureElementGraph) element;
                gElement.hideNetCDFLabelsOfFocusedObjects(this);
            }
        } else if (element instanceof SGIFigureElementString) {

        } else if (element instanceof SGIFigureElementLegend) {
            
        } else if (element instanceof SGIFigureElementAxis) {
            SGIFigureElementAxis aElement = (SGIFigureElementAxis) element;
            flag = this.synchronizedToAxisElement(aElement, msg);
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
    
    @Override
    public boolean hideSelectedObjects() {
        this.notifyToListener(NOTIFY_NETCDF_DATALABEL_WILL_BE_HIDDEN);
        if (super.hideSelectedObjects() == false) {
            return false;
        }
        return true;
    }

    private boolean synchronizedToAxisElement(
            final SGIFigureElementAxis element, final String msg) {
        /*
         * ArrayList list = this.getVisibleStringElementList(); for( int ii=0;
         * ii<list.size(); ii++ ) { ElementString el =
         * (ElementString)list.get(ii); el.setAxisValue(); }
         */
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
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        if (this.closeTextField() == false) {
            return false;
        }
        if (super.setMagnification(mag) == false) {
            return false;
        }
        return true;
    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y, final float w,
            final float h) {
        if (super.setGraphRect(x, y, w, h) == false) {
            return false;
        }

        if (this.mEditField.isVisible()) {
            if (this.closeTextField() == false) {
                return false;
            }
        }

        List list = this.mChildList;
        for (int ii = 0; ii < list.size(); ii++) {
            LabelElement el = (LabelElement) list.get(ii);
            el.requestUpdateLocation();
        }

        return true;
    }

    /**
     * 
     */
    public boolean getMarginAroundGraphRect(SGTuple2f topAndBottom,
            SGTuple2f leftAndRight) {

        //
        if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
            return false;
        }

        Rectangle2D graphRect = this.getGraphRect();

        List<SGIChildObject> sList = this.getVisibleChildList();
        ArrayList rectList = new ArrayList();
        for (int ii = 0; ii < sList.size(); ii++) {
            LabelElement el = (LabelElement) sList.get(ii);
            rectList.add(el.getElementBounds());
        }

        if (rectList.size() == 0) {
            return true;
        }

        Rectangle2D sRect = SGUtility.createUnion(rectList);

        ArrayList list = new ArrayList();
        list.add(graphRect);
        list.add(sRect);

        Rectangle2D uniRect = SGUtility.createUnion(list);

        final float top = (float) (graphRect.getY() - uniRect.getY());
        final float bottom = (float) ((uniRect.getY() + uniRect.getHeight()) - (graphRect
                .getY() + graphRect.getHeight()));
        final float left = (float) (graphRect.getX() - uniRect.getX());
        final float right = (float) ((uniRect.getX() + uniRect.getWidth()) - (graphRect
                .getX() + graphRect.getWidth()));

        topAndBottom.x += top;
        topAndBottom.y += bottom;
        leftAndRight.x += left;
        leftAndRight.y += right;

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
                final LabelElement el = (LabelElement) list.get(ii);
                if (el.isSelected()) {
                    if (el.prepare() == false) {
                    	return false;
                    }
                    el.translate((float) dx, (float) dy);
                    if (el.commit() == false) {
                    	return false;
                    }
                    this.notifyToRoot();
                    this.notifyChange();
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

        final int x = e.getX();
        final int y = e.getY();
        final int mod = e.getModifiers();
        final int cnt = e.getClickCount();
        final boolean onEdgeFlag = this.onEdge(x, y);
        final boolean ctrl = (mod & InputEvent.CTRL_MASK) != 0;
        final boolean shift = (mod & InputEvent.SHIFT_MASK) != 0;

        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final LabelElement el = (LabelElement) list.get(ii);

            // when double-clicked the edge, show the property dialog
            if (onEdgeFlag && el.equals(this.mEditedLabelElement)) {
                if (SwingUtilities.isLeftMouseButton(e) && cnt == 2) {
                    
                    // show the property dialog
                    this.setPropertiesOfSelectedObjects(el);
                    
                    // post-processing
                    this.hideEditField();
                    this.mEditedLabelElement = null;
                }
                return true;
            }

            if (el.contains(x, y)) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (cnt == 1) {
                        if (el.isSelected() && !ctrl && !shift) {
                            this.mEditedLabelElement = el;
//                            this.mFocusedX = (int) el.getX();
//                            this.mFocusedY = (int) el.getY();
//                            this.showEditField(el);
                            this.mTempPoint = new Point((int) el.getX(), (int) el.getY());
                            final int tx = this.mPressedPoint.x - this.mTempPoint.x;
                            final int ty = this.mPressedPoint.y - this.mTempPoint.y;
//System.out.println(tx + "  " + ty);                            
                            this.showEditField(this.mEditField, el, tx, ty);
                        } else {
                            this.updateFocusedObjectsList(el, e);
                        }
                    }
                } else if (SwingUtilities.isRightMouseButton(e) && cnt == 1) {
                    this.updateFocusedObjectsList(el, e);
                    el.getPopupMenu().show(this.getComponent(), x, y);
                }

                return true;
            }

        }

        return false;

    }

    /**
     * Overrode for the text field for the text of symbols.
     * 
     * @return true if a text field is shown
     */
    public boolean closeTextField() {
        if (this.mEditedLabelElement == null) {
            // if no label is edited, add a new label
            if (this.addStringElementFromTextField() == false) {
                return false;
            }
        } else {
            // if a label is edited, commit the change of properties
            if (this.commitEdit() == false) {
                return false;
            }
        }

        return true;
    }

    // commit the change of properties of a label
    private boolean commitEdit() {
        String before = this.mEditedLabelElement.getString();
        String after = this.mEditField.getText();
        if (SGUtilityText.isValidString(after)) {
            this.mEditedLabelElement.setString(after);
            if (before.equals(after) == false) {
                this.mEditedLabelElement.setChanged(true);
                this.notifyToRoot();
            }
        }

        // post-processing
        this.mEditedLabelElement = null;
        this.hideEditField();
        this.repaint();

        return true;
    }

    // hide the text field
    private boolean hideEditField() {
        this.mEditField.setText("");
        this.mEditField.setVisible(false);
        return true;
    }

    /**
     * 
     * @param e
     */
    public boolean onMousePressed(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();

        List<SGISelectable> fList = this.getFocusedObjectsList();
        List<SGIChildObject> list = this.getVisibleChildList();
        boolean flag = false;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final LabelElement el = (LabelElement) list.get(ii);
            if (el.contains(x, y)) {
                // el.mFrameFlag = true;
                if (fList.contains(el)) {
                    this.mPressedPoint = e.getPoint();
                    el.mTemporaryProperties = el.getProperties();
                    this.mDraggableFlag = true;
                }
                flag = true;
                break;
            }
        }

        // if the mouse is pressed on the label
        if (flag) {
            this.setMouseCursor(Cursor.MOVE_CURSOR);
        }
        // otherwise
        else {
            final boolean onEdgeFlag = this.onEdge(x, y);

            // if the mouse is on the edge of the text field
            if (this.mEditedLabelElement != null && onEdgeFlag) {
                flag = true;
            } else {
                // hide the edit field
                if (this.mEditField.isVisible()) {
                    this.closeTextField();
                } else {
                    // this.clearFocusedObjects();
                }
            }

        }

        return flag;
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
        this.hideEditField();
        return true;
    }

    /**
     * 
     * @param e
     */
    public boolean onMouseReleased(final MouseEvent e) {

    	List<SGISelectable> list = this.getFocusedObjectsList();
        boolean contained = false;
        // boolean changed = false;
        for (int ii = 0; ii < list.size(); ii++) {
            LabelElement el = (LabelElement) list.get(ii);
            Rectangle2D rect = el.getElementBounds();
            contained |= rect.contains(e.getPoint());
        }

        if (contained) {
            setMouseCursor(Cursor.HAND_CURSOR);
        } else {
            setMouseCursor(Cursor.DEFAULT_CURSOR);
        }
        this.mDraggableFlag = false;
        this.mDraggedDirection = null;
        
        return true;
    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setTemporaryPropertiesOfFocusedObjects() {
//    	List<SGISelectable> list = this.getFocusedObjectsList();
//        for (int ii = 0; ii < list.size(); ii++) {
//            LabelElement el = (LabelElement) list.get(ii);
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
            LabelElement el = (LabelElement) list.get(ii);
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
     */
    public boolean onEdge(final int x, final int y) {
        if (this.mEditField.isVisible() == false) {
            return false;
        }

        final int bWidth = BOUNDARY_LINE_WIDTH;
        final Rectangle rect = this.mEditField.getBounds();
        final int xx = rect.x - bWidth;
        final int yy = rect.y - bWidth;
        final int ww = rect.width + 2 * bWidth;
        final int hh = rect.height + 2 * bWidth;
        final Rectangle rectNew = new Rectangle(xx, yy, ww, hh);

        final boolean flagShape = rect.contains(x, y);
        final boolean flagRectNew = rectNew.contains(x, y);

        final boolean flag = (!flagShape) && (flagRectNew);

        return flag;
    }

    /**
     * 
     * @param e
     */
    public boolean setMouseCursor(final int x, final int y) {
        List<SGIChildObject> list = this.getVisibleChildList();
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            final LabelElement el = (LabelElement) list.get(ii);
            final boolean flag = el.contains(x, y);
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
    public void actionPerformed(final ActionEvent e) {

        final Object source = e.getSource();

        if (source.equals(this.mEditField)) {
            this.closeTextField();
        }

    }

    // add a new label from the input from the text field
    private boolean addStringElementFromTextField() {
        final String str = this.mEditField.getText();
        if (SGUtilityText.isValidString(str)) {
            // add new label
            if (this.addNewString(str, this.mTempPoint.x, this.mTempPoint.y) < 0) {
                return false;
            }
            this.notifyToRoot();
        }

        // post-processing
        this.hideEditField();
        this.repaint();

        return true;
    }

    // /**
    // *
    // */
    // private boolean removeString( final LabelElement string )
    // {
    // List list = this.mChildList;
    // for( int ii=list.size()-1; ii>=0; ii-- )
    // {
    // final LabelElement el = (LabelElement)list.get(ii);
    // if( el.equals(string) )
    // {
    // list.remove(string);
    // return true;
    // }
    // }
    //
    // return false;
    // }

    // /**
    // *
    // * @param el
    // * @return
    // */
    // private boolean hideLabel( final LabelElement el )
    // {
    // return this.hideObject(el);
    // }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return TAG_NAME_STRING_ELEMENT;
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
            LabelElement label = (LabelElement) list.get(ii);
            Element elLabel = label.createElement(document, params);
            if (elLabel == null) {
                return null;
            }
            el.appendChild(elLabel);
        }
        return new Element[] { el };
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
                .getElementsByTagName(SGIStringConstants.TAG_NAME_LABEL);
        for (int ii = 0; ii < nList.getLength(); ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;
                LabelElement label = new LabelElement();
                if (label.readProperty(el) == false) {
                    return false;
                }
                
                // modifies the text string for previous releases older than 2_0_0
                String str = label.getString();
                String modStr = SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str, versionNumber);
                label.setString(modStr);
                
                label.initPropertiesHistory();
                this.addToList(label);
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

//    /**
//     * 
//     */
//    private SGStringElementDialog mDialog = null;

//    // create the property dialog
//    private SGStringElementDialog createPropertyDialog() {
//        final SGStringElementDialog dg = new SGStringElementDialog(
//                this.mDialogOwner, true);
////        this.mDialog = dg;
//        return dg;
//    }

//    /**
//     * 
//     * @return
//     */
//    public boolean setDialogOwner(final Frame frame) {
//        super.setDialogOwner(frame);
//        this.createDialog();
//        return true;
//    }

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
            LabelElement el = (LabelElement) cList.get(ii);

            // translate
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
            if (obj instanceof LabelElement) {
                LabelElement label = (LabelElement) obj;

                // translate the instance to be pasted
                label.translate(ox, oy);

                SGProperties p = label.getProperties();

                LabelElement el = new LabelElement();
                el.setMagnification(mag);
                el.setProperties(p);

                // el.mXAxis =
                // this.mAxisElement.getAxisInCube(label.mTempXAxis);
                // el.mYAxis =
                // this.mAxisElement.getAxisInCube(label.mTempYAxis);
                el.mXAxis = this.mAxisElement.getAxisInPlane(label.mTempXAxis);
                el.mYAxis = this.mAxisElement.getAxisInPlane(label.mTempYAxis);

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
    	return LabelElement.class;
    }

    /**
     * 
     */
    protected Set getAvailableChildSet() {
        Set set = new HashSet();
        List mList = this.getMementoList();
        for (int ii = 0; ii < mList.size(); ii++) {
            StringElementProperties p = (StringElementProperties) mList.get(ii);
            set.addAll(p.visibleStringElementList);
        }

        return set;
    }

    private class LabelElement extends SGDrawingElementString2DExtended
            implements ActionListener, SGIUndoable, SGIChildObject, SGIMovable, 
            SGICopiable, SGILabelDialogObserver, SGINode {

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
        private SGAxis mXAxis = null;

        /**
         * 
         */
        private SGAxis mYAxis = null;

        /**
         * A pop-up menu.
         */
        private JPopupMenu mPopupMenu = null;

        /**
         * 
         */
        private SGProperties mTemporaryProperties = null;

        /**
         * 
         */
        LabelElement() {
            super();
            this.init();
        }

        /**
         * 
         */
        LabelElement(final String str) {
            super(str);
            this.init();
        }

        /**
         * 
         */
        private boolean init() {
            this.setFontSize(DEFAULT_LABEL_FONT_SIZE, FONT_SIZE_UNIT);
            this.setFontName(DEFAULT_LABEL_FONT_NAME);
            this.setFontStyle(DEFAULT_LABEL_FONT_STYLE);
            this.setAngle(DEFAULT_LABEL_ANGLE);
            this.setColor(DEFAULT_LABEL_FONT_COLOR);
            return true;
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
            String xAxis = SGFigureElementString.this.mAxisElement
                    .getLocationName(this.mXAxis);
            String yAxis = SGFigureElementString.this.mAxisElement
                    .getLocationName(this.mYAxis);
            StringBuffer sb = new StringBuffer();
            sb.append(this.mID);
            sb.append(": ");
//            sb.append(this.getString());
            sb.append(SGUtility.removeEscapeChar(this.getString()));
            sb.append(", AxisX=");
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
         * 
         */
        public Object copy() {
            LabelElement el = new LabelElement();
            el.setMagnification(this.getMagnification());
            el.setProperties(this.getProperties());
            el.setLocation(this.getX(), this.getY());
            // el.mTempXAxis = SGFigureElementString.this.mAxisElement
            // .getLocationInCube(this.mXAxis);
            // el.mTempYAxis = SGFigureElementString.this.mAxisElement
            // .getLocationInCube(this.mYAxis);
            el.mTempXAxis = mAxisElement.getLocationInPlane(this.mXAxis);
            el.mTempYAxis = mAxisElement.getLocationInPlane(this.mYAxis);
            return el;
        }

        private int mTempXAxis = -1;

        private int mTempYAxis = -1;

        /**
         * 
         * @return
         */
        public double getXValue() {
            SGAxis axis = this.mXAxis;
            double value = calcValue(this.getX(), axis, true);
            value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
            return value;
        }

        /**
         * 
         * @return
         */
        public double getYValue() {
            SGAxis axis = this.mYAxis;
            double value = calcValue(this.getY(), axis, false);
            value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
            return value;
        }

        /**
         * Sets the axis value for the x-coordinate.
         * 
         * @param value
         *           the axis value for the x-coordinate
         * @return true if succeeded
         */
        public boolean setXValue(final double value) {
            SGAxis axis = this.mXAxis;
            if (axis.isValidValue(value) == false) {
            	return false;
            }

            // current values gotten from the position of legend
            double currentValue = calcValue(this.getX(), axis, true);
            currentValue = SGUtilityNumber.getNumberInRangeOrder(currentValue, axis);

            // if values from the dialog is different from the current values,
            // set the values from the dialog
            float x;
            if (value == currentValue) {
                x = this.getX();
            } else {
                x = calcLocation(value, axis, true);
            }

            if (this.setLocation(x, this.getY()) == false) {
            	return false;
            }
            return true;
        }

        /**
         * Sets the axis value for the y-coordinate.
         * 
         * @param value
         *           the axis value for the y-coordinate
         * @return true if succeeded
         */
        public boolean setYValue(final double value) {
            SGAxis axis = this.mYAxis;
            if (axis.isValidValue(value) == false) {
            	return false;
            }

            // current values gotten from the position of legend
            double currentValue = calcValue(this.getY(), axis, false);
            currentValue = SGUtilityNumber.getNumberInRangeOrder(currentValue, axis);

            // if values from the dialog is different from the current values,
            // set the values from the dialog
            float y;
            if (value == currentValue) {
                y = this.getY();
            } else {
                y = calcLocation(value, axis, false);
            }

            if (this.setLocation(this.getX(), y) == false) {
            	return false;
            }
            return true;
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidXValue(final int config, final Number value) {
            final SGAxis axis = (config == -1) ? this.mXAxis
                    : SGFigureElementString.this.mAxisElement
                            .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getXValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         * @param config
         * @param value
         * @return
         */
        public boolean hasValidYValue(final int config, final Number value) {
            final SGAxis axis = (config == -1) ? this.mYAxis
                    : SGFigureElementString.this.mAxisElement
                            .getAxisInPlane(config);
            final double v = (value != null) ? value.doubleValue() : this
                    .getYValue();
            return axis.isValidValue(v);
        }

        /**
         * 
         */
        public int getXAxisLocation() {
            return SGFigureElementString.this.mAxisElement
                    .getLocationInPlane(this.mXAxis);
        }

        /**
         * 
         */
        public int getYAxisLocation() {
            return SGFigureElementString.this.mAxisElement
                    .getLocationInPlane(this.mYAxis);
        }

        /**
         * Sets the location of the x-axis.
         * 
         * @param location
         *           the location of the x-axis
         * @return true if succeeded
         */
        public boolean setXAxisLocation(final int location) {
            if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                    && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
                return false;
            }
            this.mXAxis = SGFigureElementString.this.mAxisElement.getAxisInPlane(location);
            return true;
        }

        /**
         * Sets the location of the y-axis.
         * 
         * @param location
         *           the location of the y-axis
         * @return true if succeeded
         */
        public boolean setYAxisLocation(final int location) {
            if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                    && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
                return false;
            }
            this.mYAxis = SGFigureElementString.this.mAxisElement.getAxisInPlane(location);
            return true;
        }

        /**
         * Sets the text string.
         * 
         * @param str
         *           a text string to set
         * @return true if succeeded
         */
        public boolean setString(final String str) {
            if (SGUtilityText.isValidString(str) == false) {
                return false;
            }
            return super.setString(str);
        }

        /**
         * Sets the color.
         * 
         * @param color
         *           the color to set
         * @return true if succeeded
         */
        public boolean setStringColor(final Color color) {
            return this.setColor(color);
        }

        /**
         * 
         */
        public Color getStringColor() {
            return this.getColor();
        }

        /**
         * 
         */
        public boolean prepare() {
            this.mTemporaryProperties = this.getProperties();
            return true;
        }

        /**
         * 
         * @return
         */
        private ArrayList getAnchorPointList() {
            ArrayList list = new ArrayList();

            Rectangle2D rect = this.getElementBounds();
            final float x = (float) rect.getX();
            final float y = (float) rect.getY();
            final float w = (float) rect.getWidth();
            final float h = (float) rect.getHeight();

            Point2D nw = new Point2D.Float(x, y);
            Point2D sw = new Point2D.Float(x, y + h);
            Point2D ne = new Point2D.Float(x + w, y);
            Point2D se = new Point2D.Float(x + w, y + h);

            list.add(nw);
            list.add(sw);
            list.add(ne);
            list.add(se);

            return list;
        }

        /**
         * Returns a pop-up menu.
         * @return
         *         a pop-up menu
         */
        public JPopupMenu getPopupMenu() {
            JPopupMenu p = null;
            if (this.mPopupMenu != null) {
                p = this.mPopupMenu;
            } else {
                p = this.createPopupMenu();
                this.mPopupMenu = p;
            }
            return p;
        }

        /**
         * Create a pop-up menu.
         * @return
         *         a pop-up menu
         */
        private JPopupMenu createPopupMenu() {
            JPopupMenu p = new JPopupMenu();
            p.setBounds(0, 0, 100, 100);

            StringBuffer sb = new StringBuffer();
            sb.append("  -- Label: ");
            sb.append(this.getID());
            sb.append(" --");
            
            p.add(new JLabel(sb.toString()));
            p.addSeparator();

            SGUtility.addArrangeItems(p, this);

            p.addSeparator();

            SGUtility.addItem(p, this, MENUCMD_CUT);
            SGUtility.addItem(p, this, MENUCMD_COPY);

            p.addSeparator();

            SGUtility.addItem(p, this, MENUCMD_DELETE);
            SGUtility.addItem(p, this, MENUCMD_DUPLICATE);

            p.addSeparator();

            SGUtility.addItem(p, this, MENUCMD_PROPERTY);

            return p;
        }

        /**
         * 
         * @return
         */
        public String getTagName() {
            return SGIStringConstants.TAG_NAME_LABEL;
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

            // axes
            str = el.getAttribute(KEY_X_AXIS_POSITION);
            if (str.length() != 0) {
                SGAxis xAxis = SGFigureElementString.this.mAxisElement
                        .getAxis(str);
                if (xAxis == null) {
                    return false;
                }
                this.mXAxis = xAxis;
            }

            str = el.getAttribute(KEY_Y_AXIS_POSITION);
            if (str.length() != 0) {
                SGAxis yAxis = SGFigureElementString.this.mAxisElement
                        .getAxis(str);
                if (yAxis == null) {
                    return false;
                }
                this.mYAxis = yAxis;
            }

            // x value
            str = el.getAttribute(KEY_X_VALUE);
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
            str = el.getAttribute(KEY_Y_VALUE);
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

            return true;
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
                    + SGFigureElementString.this.mGraphRectX;
            final float y = this.getMagnification() * basey
                    + SGFigureElementString.this.mGraphRectY;
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
            final float nx = (x - SGFigureElementString.this.mGraphRectX)
                    / mag;
            final float ny = (y - SGFigureElementString.this.mGraphRectY)
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
        }

        /**
         * 
         */
        public SGProperties getProperties() {
            LabelProperties p = new LabelProperties();
            if (this.getProperties(p) == false) {
                return null;
            }
            return p;
        }

        /**
         * 
         */
        public boolean getProperties(SGProperties p) {
            if ((p instanceof LabelProperties) == false) {
                return false;
            }
            if (super.getProperties(p) == false) {
                return false;
            }
            LabelProperties lp = (LabelProperties) p;
            lp.setX(super.getX());
            lp.setY(super.getY());
            lp.setXAxis(this.mXAxis);
            lp.setYAxis(this.mYAxis);
            lp.setText(this.getString());
            lp.setColor(this.getColor());
            return true;
        }

        /**
         * 
         */
        public boolean setProperties(SGProperties p) {
            if ((p instanceof LabelProperties) == false) {
                return false;
            }
            if (super.setProperties(p) == false) {
                return false;
            }
            LabelProperties lp = (LabelProperties) p;
            super.setLocation(lp.getX(), lp.getY());
            this.mXAxis = lp.getXAxis();
            this.mYAxis = lp.getYAxis();
            this.setString(lp.getText());
            this.setColor(lp.getColor());
            return true;
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
        public void actionPerformed(final ActionEvent e) {

            final String command = e.getActionCommand();
            // final Object source = e.getSource();

            if (command.equals(MENUCMD_PROPERTY)) {
                SGFigureElementString.this.setPropertiesOfSelectedObjects(this);
            } else {
                notifyToListener(command, e.getSource());
            }
        }

        /**
         * 
         */
        public boolean commit() {
            // if property has changed between before and after dialog showing
            // then update history.
            SGProperties pTemp = this.mTemporaryProperties;
            SGProperties pPresent = this.getProperties();
            if (pTemp.equals(pPresent) == false) {
                this.mUndoManager.setChanged(true);
            }
            this.mTemporaryProperties = null;
            notifyChangeOnCommit();
            repaint();
            return true;
        }

        /**
         * 
         */
        public boolean preview() {
            notifyChange();
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
            notifyChangeOnCancel();
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
                dg = new SGStringElementDialog(mDialogOwner, true);
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
            return this.mUndoManager.setMementoBackward();
        }

        /**
         * redo
         */
        public boolean setMementoForward() {
            return this.mUndoManager.setMementoForward();
        }

        /**
         * ask undo operation
         */
        public boolean undo() {
            return this.setMementoBackward();
        }

        /**
         * ask redo operation
         */
        public boolean redo() {
            return this.setMementoForward();
        }

        /**
         * update history. this is called after undo operation as first.
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
            SGFigureElementString.this.notifyToRoot();
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
                
                if (COM_LABEL_AXIS_X.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_LABEL_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setXAxisLocation(loc) == false) {
                        result.putResult(COM_LABEL_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_AXIS_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_AXIS_Y.equalsIgnoreCase(key)) {
                    final int loc = SGUtility.getAxisLocation(value);
                    if (loc == -1) {
                        result.putResult(COM_LABEL_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setYAxisLocation(loc) == false) {
                        result.putResult(COM_LABEL_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_AXIS_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_TEXT.equalsIgnoreCase(key)) {
                	if (map.isDoubleQuoted(key) == false) {
                        result.putResult(COM_LABEL_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setString(value) == false) {
                        result.putResult(COM_LABEL_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_TEXT, SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_LOCATION_X.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_LABEL_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_LABEL_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setXValue(num.doubleValue()) == false) {
                        result.putResult(COM_LABEL_LOCATION_X, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_LOCATION_X, SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_LOCATION_Y.equalsIgnoreCase(key)) {
                    Double num = SGUtilityText.getDouble(value);
                    if (num == null) {
                        result.putResult(COM_LABEL_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                        result.putResult(COM_LABEL_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setYValue(num.doubleValue()) == false) {
                        result.putResult(COM_LABEL_LOCATION_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_LOCATION_Y, SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_FONT_NAME.equalsIgnoreCase(key)) {
                	final String name = SGUtility.findFontFamilyName(value);
                	if (name == null) {
                        result.putResult(COM_LABEL_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setFontName(name) == false) {
                        result.putResult(COM_LABEL_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_FONT_NAME, SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_FONT_STYLE.equalsIgnoreCase(key)) {
                    Integer style = SGUtilityText.getFontStyle(value);
                    if (style == null) {
                        result.putResult(COM_LABEL_FONT_STYLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setFontStyle(style.intValue()) == false) {
                        result.putResult(COM_LABEL_FONT_STYLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_FONT_STYLE,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_FONT_SIZE.equalsIgnoreCase(key)) {
                    StringBuffer unit = new StringBuffer();
                    Number num = SGUtilityText.getNumber(value, unit);
                    if (num == null) {
                        result.putResult(COM_LABEL_FONT_SIZE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setFontSize(num.floatValue(), unit.toString()) == false) {
                        result.putResult(COM_LABEL_FONT_SIZE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_FONT_SIZE,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_FONT_COLOR.equalsIgnoreCase(key)) {
                    Color cl = SGUtilityText.getColor(value);
                    if (cl != null) {
                        if (this.setColor(cl) == false) {
                            result.putResult(COM_LABEL_FONT_COLOR,
                                    SGPropertyResults.INVALID_INPUT_VALUE);
                            continue;
                        }
                    } else {
                    	cl = SGUtilityText.parseColor(value);
    					if (cl == null) {
    						result.putResult(COM_LABEL_FONT_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
    					if (this.setColor(cl) == false) {
    						result.putResult(COM_LABEL_FONT_COLOR,
    								SGPropertyResults.INVALID_INPUT_VALUE);
    						continue;
    					}
                    }
                    result.putResult(COM_LABEL_FONT_COLOR,
                            SGPropertyResults.SUCCEEDED);
                } else if (COM_LABEL_ANGLE.equalsIgnoreCase(key)) {
                    Number num = SGUtilityText.getFloat(value);
                    if (num == null) {
                        result.putResult(COM_LABEL_ANGLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setAngle(num.floatValue()) == false) {
                        result.putResult(COM_LABEL_ANGLE,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    result.putResult(COM_LABEL_ANGLE,
                            SGPropertyResults.SUCCEEDED);
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
        	return SGCommandUtility.createCommandString(COM_LABEL, 
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
        	this.addProperties(map, COM_LABEL_LOCATION_X, COM_LABEL_LOCATION_Y, 
        			COM_LABEL_AXIS_X, COM_LABEL_AXIS_Y);
			return map;
		}
    	
    	@Override
    	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
        	SGPropertyMap map = super.getPropertyFileMap(params);
        	this.addProperties(map, KEY_X_VALUE, KEY_Y_VALUE, KEY_X_AXIS_POSITION, 
        			KEY_Y_AXIS_POSITION);
    		return map;
    	}
    	
        private void addProperties(SGPropertyMap map, String xKey, String yKey,
        		String axisXKey, String axisYKey) {
            SGPropertyUtility.addProperty(map, xKey, this.getXValue());
            SGPropertyUtility.addProperty(map, yKey, this.getYValue());
            SGPropertyUtility.addProperty(map, axisXKey, 
            		mAxisElement.getLocationName(this.mXAxis));
            SGPropertyUtility.addProperty(map, axisYKey, 
            		mAxisElement.getLocationName(this.mYAxis));
        }

		@Override
		public boolean getAxisDateMode(final int location) {
			return mAxisElement.getAxisDateMode(location);
		}
    }

    public static class LabelProperties extends StringProperties {

        private float mX = 0.0f;

        private float mY = 0.0f;

        private String mText = null;

        private SGAxis mXAxis = null;

        private SGAxis mYAxis = null;

        /**
         * 
         * 
         */
        public LabelProperties() {
            super();
        }

        /**
         * 
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof LabelProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            LabelProperties p = (LabelProperties) obj;

            if (this.mX != p.mX) {
                return false;
            }
            if (this.mY != p.mY) {
                return false;
            }
//            if (this.mText.equals(p.mText) == false)
//                return false;
            if (SGUtility.equals(this.mText, p.mText) == false) {
                return false;
            }
//            if (p.mXAxis.equals(this.mXAxis) == false)
//                return false;
//            if (p.mYAxis.equals(this.mYAxis) == false)
//                return false;
            if (SGUtility.equals(this.mXAxis, p.mXAxis) == false) {
                return false;
            }
            if (SGUtility.equals(this.mYAxis, p.mYAxis) == false) {
                return false;
            }

            return true;
        }

        public Float getX() {
            return Float.valueOf(this.mX);
        }

        public Float getY() {
            return Float.valueOf(this.mY);
        }

        public String getText() {
            return this.mText;
        }

        public SGAxis getXAxis() {
            return this.mXAxis;
        }

        public SGAxis getYAxis() {
            return this.mYAxis;
        }

        public void setX(final float x) {
            this.mX = x;
        }

        public void setY(final float y) {
            this.mY = y;
        }

        public void setText(final String text) {
            this.mText = text;
        }

        public void setXAxis(SGAxis axis) {
            this.mXAxis = axis;
        }

        public void setYAxis(SGAxis axis) {
            this.mYAxis = axis;
        }

    }

    /**
     * 
     */
    public SGProperties getProperties() {
        StringElementProperties p = new StringElementProperties();
        p.visibleStringElementList = new ArrayList(this.getVisibleChildList());
        return p;
    }

    /**
     * 
     */
    public boolean setProperties(final SGProperties p) {

        if ((p instanceof StringElementProperties) == false) {
            return false;
        }

        StringElementProperties sp = (StringElementProperties) p;

        this.setVisibleChildList(sp.visibleStringElementList);

        return true;

    }

    /**
     * 
     */
    public static class StringElementProperties extends SGProperties {
        ArrayList visibleStringElementList = new ArrayList();

        public StringElementProperties() {
            super();
        }

        public void dispose() {
        	super.dispose();
            this.visibleStringElementList.clear();
            this.visibleStringElementList = null;
        }

        public boolean equals(final Object obj) {

            if ((obj instanceof StringElementProperties) == false) {
                return false;
            }

            StringElementProperties p = (StringElementProperties) obj;

            if (p.visibleStringElementList
                    .equals(this.visibleStringElementList) == false) {
                return false;
            }

            return true;

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
		this.mEditedLabelElement = null;
		this.repaint();
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
                    mPropertyDialog = new SGStringElementDialog(mDialogOwner, true);
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
        
        // create a font
        Font font = null;
        if (this.mEditedLabelElement != null) {
            SGDrawingElementString2D el = this.mEditedLabelElement;
            
            // edit
            final float fontSize = el.getFontSize();
            font = new Font(el.getFontName(), el.getFontStyle(), (int) (el
                    .getMagnification() * fontSize));
        } else {
            // add
            final float fontSize = SGIStringConstants.DEFAULT_LABEL_FONT_SIZE;
            font = new Font(SGIStringConstants.DEFAULT_LABEL_FONT_NAME, 
                    SGIStringConstants.DEFAULT_LABEL_FONT_STYLE,
                    (int) (this.mMagnification * fontSize));
        }

        // update the text field
        this.updateTextField(this.mEditField, font);
    }
    
    /**
     * Called when the caret in the text field is update.
     */
    public void caretUpdate(final CaretEvent e) {
    }

    /**
     * Called when menu items in the menu bar is selected.
     *
     */
    public void onMenuSelected() {
        if (this.mEditField.isVisible()) {
            this.closeTextField();
        }
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
            String sText = null;
            String sValueX = null;
            String sValueY = null;
            String sAxisX = null;
            String sAxisY = null;
            Iterator itr = map.getKeyIterator();
            while (itr.hasNext()) {
                Object keyObj = itr.next();
                String key = keyObj.toString();
                if (COM_LABEL_TEXT.equalsIgnoreCase(key)) {
                    sText = map.getValueString(key);
                } else if (COM_LABEL_LOCATION_X.equalsIgnoreCase(key)) {
                    sValueX = map.getValueString(key);
                } else if (COM_LABEL_LOCATION_Y.equalsIgnoreCase(key)) {
                    sValueY = map.getValueString(key);
                } else if (COM_LABEL_AXIS_X.equalsIgnoreCase(key)) {
                	sAxisX = map.getValueString(key);
                } else if (COM_LABEL_AXIS_Y.equalsIgnoreCase(key)) {
                	sAxisY = map.getValueString(key);
                }
            }
            if (sText == null) {
                return null;
            }
            if (sValueX == null) {
                return null;
            }
            if (sValueY == null) {
                return null;
            }
            Double valueX = SGUtilityText.getDouble(sValueX);
            Double valueY = SGUtilityText.getDouble(sValueY);
            if (valueX == null) {
                return null;
            }
            if (valueY == null) {
                return null;
            }
            if (SGUtility.isValidPropertyValue(valueX.doubleValue()) == false) {
            	return null;
            }
            if (SGUtility.isValidPropertyValue(valueY.doubleValue()) == false) {
            	return null;
            }
        	if (map.isDoubleQuoted(COM_LABEL_TEXT) == false) {
        		return null;
        	}
            if (this.addString(id, sText, valueX.doubleValue(), valueY.doubleValue(),
            		sAxisX, sAxisY) == false) {
                return null;
            }
            
            child = this.getVisibleChild(id);
        }

        // set properties to the child object
        SGPropertyResults result = child.setProperties(map);

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
		List<SGIChildObject> cList = this.getVisibleChildList();
    	for (SGIChildObject c : cList) {
    		LabelElement el = (LabelElement) c;
    		String cmdString = el.getCommandString(params);
    		sb.append(cmdString);
    	}
		return sb.toString();
	}

}
