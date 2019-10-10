package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataAxisInfo;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisBreak;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementForData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGrid;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementShape;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementSignificantDifference;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementString;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementTimingLine;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility.MouseDragResult;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIXYData;
import jp.riken.brain.ni.samuraigraph.data.SGIXYZData;
import jp.riken.brain.ni.samuraigraph.figure.SGAxisDialog;
import jp.riken.brain.ni.samuraigraph.figure.SGColorBarDialog;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIColorBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIScaleConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * A class managing axes.
 */
public class SGFigureElementAxis extends SGFigureElement2D implements
        SGIFigureElementAxis, SGIStringConstants, 
        CaretListener, DocumentListener, ActionListener, KeyListener, 
        SGIAxisConstants, SGIColorBarConstants {

	/**
     * The list of axes.
     */
    List<SGFigureAxis> mElementsGroupList = new ArrayList<SGFigureAxis>();
    
    /**
     * The z-axis.
     */
    SGColorBarAxis mZAxisElementsGroup = null;
    
    /**
     * The flag whether the color bar is available.
     */
    boolean mColorBarAvailableFlag = false;

    /**
     * A flag whether the frame lines are visible.
     */
    boolean mFrameLineVisibleFlag = true;

    /**
     * Color of frame lines.
     */
    Color mFrameLineColor;

    /**
     * A drawing element pressed by the mouse such as a scale number or a tick
     * mark.
     */
    AxisScaleElement mDraggableAxisScaleElement = null;

    /**
     * 
     */
    Point mPressedElementOrigin = null;

    /**
     * Property dialog for axes.
     */
    SGPropertyDialog mPropertyDialog = null;

    /**
     * Property dialog for the color bar.
     */
    SGPropertyDialog mColorBarPropertyDialog = null;

    /**
     * Property dialog for the axis scale.
     */
    SGPropertyDialog mAxisScalePropertyDialog = null;

    static boolean mNotifyChangeOnDraggingFlag = true;

    /**
     * Used to edit a text string.
     */
    JTextField mTextField = new JTextField();

    // used only on the start-up
    boolean mStartFlag = true;

    /**
     * The line stroke for the frame lines.
     */
    SGStroke mFrameLineStroke = new SGStroke();
    
    /**
     * The axis scale.
     */
    SGAxisScale mAxisScale = null;

    /**
     * The default constructor.
     */
    public SGFigureElementAxis() {
        super();
        this.initEditField();
        this.init();
    }

    // initialization
    private void init() {

    	// initialize frame lines
        this.setFrameVisible(DEFAULT_FRAME_LINE_VISIBLE);
        this.setFrameLineWidth(DEFAULT_FRAME_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setFrameLineColor(DEFAULT_FRAME_LINE_COLOR);
        
        // creates the X and Y-axes
        this.createXYAxisGroups(
                new SGTuple2d(DEFAULT_X_MIN_VALUE, DEFAULT_X_MAX_VALUE),
                new SGTuple2d(DEFAULT_Y_MIN_VALUE, DEFAULT_Y_MAX_VALUE), 
                DEFAULT_HORIZONTAL_AXIS_TITLE, DEFAULT_VERTICAL_AXIS_TITLE);
        
        // creates the Z-axis
        final SGColorBarAxis group = new SGColorBarAxis(this);
        group.setLocationInPlane(AXIS_NORMAL);
        group.setVisible(false);
        group.setTitleText(DEFAULT_NORMAL_AXIS_TITLE);
        this.mZAxisElementsGroup = group;
        
        // create all drawing elements
        this.createAllDrawingElements();
        
        List<SGAxisElement> axes = this.getAllAxisGroupList();
        for (SGAxisElement axis : axes) {
        	axis.init();
        }
        
        // create the axis scale
        this.mAxisScale = new SGAxisScale(this);
        for (SGAxisElement axis : axes) {
        	axis.addAxisScaleChangeListener(this.mAxisScale);
        }
        this.mAxisScale.setVisible(false);
    }

    // initialization of the text field
    private void initEditField() {
        this.mTextField.setVisible(false);
        this.mTextField.addActionListener(this);
        this.mTextField.addCaretListener(this);
        this.mTextField.getDocument().addDocumentListener(this);
        this.mTextField.addKeyListener(this);
    }

    /**
     * Initialize the history of properties.
     * 
     * @return true if succeeded
     */
    public boolean initPropertiesHistory() {
        if (super.initPropertiesHistory() == false) {
            return false;
        }
        List<SGFigureAxis> axes = this.getAxisGroupList();
        for (SGFigureAxis axis : axes) {
        	if (axis.initPropertiesHistory() == false) {
        		return false;
        	}
        }
        if (this.mZAxisElementsGroup.initPropertiesHistory() == false) {
            return false;
        }
        if (this.mAxisScale.initPropertiesHistory() == false) {
        	return false;
        }
        
        // clear the start up flag on initialization
        this.mStartFlag = false;
        return true;
    }

    /**
     * Dispose all objects in this class.
     */
    public void dispose() {

        super.dispose();

        // dispose the property dialog
        if (this.mPropertyDialog != null) {
            this.mPropertyDialog.dispose();
            this.mPropertyDialog = null;
        }
        
        this.mEditingStringElement = null;
        this.mFrameLineColor = null;
        this.mDraggableAxisScaleElement = null;
        this.mPressedElementOrigin = null;
        this.mPressedPoint = null;
//        this.mTemporaryProperties = null;
        this.mTextField = null;
        for (int ii = 0; ii < this.mElementsGroupList.size(); ii++) {
            SGFigureAxis group = (SGFigureAxis) this.mElementsGroupList
                    .get(ii);
            group.dispose();
        }
        this.mElementsGroupList.clear();
        this.mElementsGroupList = null;
        this.mZAxisElementsGroup.dispose();
        this.mZAxisElementsGroup = null;
        this.mAxisScale.dispose();
        this.mAxisScale = null;
    }

    /**
     * 
     */
    public void setComponent(JComponent com) {
        super.setComponent(com);
        com.add(this.mTextField);
    }

    /**
     * 
     */
    public void actionPerformed(final ActionEvent e) {
        // String command = e.getActionCommand();
        Object source = e.getSource();

        if (source.equals(this.mTextField)) {
            this.closeTextField();
            return;
        }
    }

    /**
     * Overrode for the text field for axis title.
     * 
     * @return true if a text field is shown
     */
    public boolean closeTextField() {
        this.commitEdit();
        this.hideEditField();
        return true;
    }

    // commit the change of the text in the text field
    private boolean commitEdit() {
        String str = this.mTextField.getText();

        if (!SGUtilityText.isValidString(str)) {
            return false;
        }
        
        String before = this.mEditingStringElement.getString();
        String after = str;
        this.mEditingStringElement.setString(after);

        // update the history
        if (before.equals(after) == false) {
            if (this.mEditingStringElement.equals(this.mZAxisElementsGroup.mTitle)) {
                // if the title of the color bar is changed,
                // set changed the color bar
                this.mZAxisElementsGroup.setChanged(true);
            } else if (this.mEditingStringElement.equals(this.mAxisScale.getHorizontalStringElement())) {
            	this.mAxisScale.setHorizontalUnit(str);
            	this.mAxisScale.updateNumberLabelsWithLocation();
            	this.mAxisScale.setChanged(true);
            } else if (this.mEditingStringElement.equals(this.mAxisScale.getVerticalStringElement())) {
            	this.mAxisScale.setVerticalUnit(str);
            	this.mAxisScale.updateNumberLabelsWithLocation();
            	this.mAxisScale.setChanged(true);
            } else {
            	boolean found = false;
            	List<SGFigureAxis> axes = this.getAxisGroupList();
            	for (SGFigureAxis axis : axes) {
            		if (this.mEditingStringElement.equals(axis.mTitle)) {
            			axis.setChanged(true);
            			found = true;
            			break;
            		}
            	}
            	if (!found) {
                    // otherwise, set changed this figure element
                    this.setChanged(true);
            	}
            }
        }

        this.createAllDrawingElements();

        this.repaint();

        notifyChange();
        this.notifyToRoot();

        return true;
    }

    // clear and hide the text field
    private boolean hideEditField() {
        this.mTextField.setText("");
        this.mTextField.setVisible(false);
        return true;
    }

    /**
     * Returns whether the frame line is visible.
     * 
     * @return whether the frame line is visible
     */
    public boolean isFrameLineVisible() {
        return this.mFrameLineVisibleFlag;
    }

    /**
     * Returns width of frame lines.
     * 
     * @return width of frame lines
     */
    public float getFrameLineWidth() {
        return this.mFrameLineStroke.getLineWidth();
    }

    /**
     * Returns width of frame lines in given unit.
     * 
     * @param unit
     *            unit of length
     * @return width of frame lines in given unit
     */
    public float getFrameLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getFrameLineWidth(),
                unit);
    }

    /**
     * 
     * @return
     */
    public Color getFrameLineColor() {
        return this.mFrameLineColor;
    }

    /**
     * Sets the visibility of the frame.
     * 
     * @param b
     *          true to set visible
     * @return true if succeeded
     */
    public boolean setFrameVisible(final boolean b) {
        this.mFrameLineVisibleFlag = b;
        return true;
    }

    /**
     * Shows the property dialog for the current axis.
     * 
     */
    void showPropertyDialog(SGIPropertyDialogObserver obs) {
        this.setPropertiesOfSelectedObjects(obs);
    }

    /**
     * Shows the property dialog for axes.
     * 
     */
    void showPropertyDialog(SGIPropertyDialogObserver obs, final boolean forVisibleAxes) {
    	/*
    	List<SGFigureAxis> axes = this.getAxisGroupList();

    	// gets selected axes
    	List<SGFigureAxis> selectedAxes = new ArrayList<SGFigureAxis>();
    	for (SGFigureAxis axis : axes) {
    		if (axis.isSelected()) {
    			selectedAxes.add(axis);
    		}
    	}
    	
    	// deselects all axes
    	for (SGFigureAxis axis : axes) {
			axis.setSelected(false);
    	}
    	
    	// selects axes
    	if (forVisibleAxes) {
        	for (SGFigureAxis axis : axes) {
        		if (axis.isVisible()) {
        			axis.setSelected(true);
        		}
        	}
    	} else {
        	for (SGFigureAxis axis : axes) {
    			axis.setSelected(true);
        	}
    	}
    	
    	// recovers selected axes
    	for (SGFigureAxis axis : axes) {
			axis.setSelected(selectedAxes.contains(axis));
    	}
    	*/
    	if (forVisibleAxes) {
    		this.setPropertiesOfAllVisibleObjects(obs);
    	} else {
    		this.setPropertiesOfAllObjects(obs);
    	}
    }

    /**
     * Synchronize with other figure element.
     */
    public boolean synchronize(final SGIFigureElement element, String msg) {

        boolean flag = true;
        if (element instanceof SGIFigureElementGraph) {
            SGIFigureElementGraph gElement = (SGIFigureElementGraph) element;
            this.synchronizeColorBar(gElement, msg);
            this.synchronizeDataColumnChange(gElement, msg);
        } else if (element instanceof SGIFigureElementString) {

        } else if (element instanceof SGIFigureElementLegend) {
            SGIFigureElementLegend lElement = (SGIFigureElementLegend) element;
            this.synchronizeColorBar(lElement, msg);
            this.synchronizeDataColumnChange(lElement, msg);
        } else if (element instanceof SGIFigureElementAxis) {
        	
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
    
    /**
     * Synchronizes the color bar.
     * 
     * @param element
     *           figure element as an event source
     * @param msg
     *           the message
     */
    private void synchronizeColorBar(final SGIFigureElementForData element, 
            final String msg) {
        if (SGIFigureElement.NOTIFY_DATA_HIDDEN.equals(msg)
                || SGIFigureElement.NOTIFY_CHANGE_ON_UNDO.equals(msg)) {
            // if there are no SXYZ type of data exist, hide the color bar
            List<SGData> dataList = this.getDataList();
            boolean sxyzVisible = false;
            for (int ii = 0; ii < dataList.size(); ii++) {
                SGData data = dataList.get(ii);
                if (data instanceof SGISXYZTypeData) {
                    final boolean b = element.isDataVisible(data);
                    if (b) {
                        sxyzVisible = true;
                        break;
                    }
                }
            }
            this.mColorBarAvailableFlag = sxyzVisible;
        }
    }
    
    /**
     * Synchronizes the element given by the argument.
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
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification to set
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        super.setMagnification(mag);
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            group.setMagnification(mag);
        }
        this.mFrameLineStroke.setMagnification(mag);
        this.mAxisScale.setMagnification(mag);
        return true;
    }

    /**
     * Called when the mouse is clicked.
     */
    public boolean onMouseClicked(final MouseEvent e) {
    	if (this.mAxisScale.isVisible()) {
    		if (this.mAxisScale.onMouseClicked(e)) {
    			return true;
    		}
    	}
        if (this.mZAxisElementsGroup.isVisible()) {
            if (this.mZAxisElementsGroup.onMouseClicked(e)) {
                return true;
            }
        }
        List<SGFigureAxis> gList = this.getAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGFigureAxis group = gList.get(ii);
            final boolean flag = group.onMouseClicked(e);
            if (flag) {
                return true;
            }
        }
        if (this.mTextField.isVisible()) {
            this.closeTextField();
        } else {
            this.hideEditField();
        }
        return false;
    }

    /**
     * Called when the mouse is pressed.
     */
    public boolean onMousePressed(final MouseEvent e) {
        if (this.mAxisScale.isVisible()) {
        	if (this.mAxisScale.onMousePressed(e)) {
                this.mPressedPoint = e.getPoint();
                this.mDraggableFlag = true;
                return true;
        	}
        }
        if (this.mZAxisElementsGroup.isVisible()) {
            if (this.mZAxisElementsGroup.onMousePressed(e)) {
                this.mPressedPoint = e.getPoint();
                this.mDraggableFlag = true;
                return true;
            }
        }
        List<SGFigureAxis> gList = this.getAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGFigureAxis group = gList.get(ii);
            if (group.onMousePressed(e)) {
                this.mPressedPoint = e.getPoint();
                this.mDraggableFlag = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the mouse is dragged.
     */
    public boolean onMouseDragged(final MouseEvent e) {
        if (this.mDraggableFlag == false) {
            return false;
        }
        if (this.mPressedPoint == null) {
            return false;
        }
        if (this.mAxisScale.isVisible()) {
        	if (this.mAxisScale.onMouseDragged(e)) {
        		return true;
        	}
        }
        if (this.mZAxisElementsGroup.isVisible()) {
            if (this.mZAxisElementsGroup.onMouseDragged(e)) {
            	if (this.mZAxisElementsGroup.hasDraggableAxisScaleElement()) {
            		this.clearFocusedObjects();
            	}
                return true;
            }
        }
        List<SGFigureAxis> gList = this.getAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGFigureAxis group = gList.get(ii);
            final boolean flag = group.onMouseDragged(e);
            if (flag) {
            	if (group.hasDraggableAxisScaleElement()) {
            		this.clearFocusedObjects();
            	}
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the mouse is released.
     */
    public boolean onMouseReleased(final MouseEvent e) {
        boolean changed = false;
        if (this.mAxisScale.isVisible()) {
        	if (this.mAxisScale.onMouseReleased(e)) {
        		if (this.mAxisScale.isChanged()) {
        			changed = true;
        		}
        	}
        }
        if (this.mZAxisElementsGroup.isVisible()) {
            if (this.mZAxisElementsGroup.onMouseReleased(e)) {
                if (this.mZAxisElementsGroup.isChanged()) {
                    changed = true;
                }
            }
        }
        if (!changed) {
            List<SGFigureAxis> gList = this.getAxisGroupList();
            for (int ii = 0; ii < gList.size(); ii++) {
                final SGFigureAxis group = gList.get(ii);
                if (group.onMouseReleased(e)) {
                    if (group.isChanged()) {
                        changed = true;
                    }
                    break;
                }
            }
        }
        this.initializePressedElement();
        if (changed) {
            this.notifyChange();
            this.notifyToRoot();
        }
        this.repaint();
        this.mDraggableFlag = false;
        this.mDraggedDirection = null;
        return true;
    }

    /**
     * Called when the mouse is on drawing elements.
     * 
     * @param x
     *            x coordinate
     * @param y
     *            y coordinate
     * @return true when the mouse is on something
     */
    public boolean setMouseCursor(final int x, final int y) {
        if (this.mAxisScale.isVisible()) {
        	if (this.mAxisScale.setMouseCursor(x, y)) {
        		return true;
        	}
        }
        if (this.mZAxisElementsGroup.isVisible()) {
            if (this.mZAxisElementsGroup.setMouseCursor(x, y)) {
                return true;
            }
        }
        List<SGFigureAxis> axes = this.getAxisGroupList();
        for (SGFigureAxis axis : axes) {
            if (axis.setMouseCursor(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates changed flag of focused objects.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean updateChangedFlag() {
    	if (this.mAxisScale.isVisible()) {
    		if (!this.mAxisScale.setChangedFocusedObjects()) {
    			return false;
    		}
    	}
    	if (this.mZAxisElementsGroup.isVisible()) {
    		if (!this.mZAxisElementsGroup.setChangedFocusedObjects()) {
    			return false;
    		}
    	}
    	List<SGFigureAxis> axes = this.getAxisGroupList();
    	for (SGFigureAxis axis : axes) {
    		if (axis.isVisible()) {
    			if (!axis.setChangedFocusedObjects()) {
    				return false;
    			}
    		}
    	}
    	return true;
    }
    
    /**
     * Overrode to check child objects of axes.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean isFocusedObjectsChanged() {
    	if (super.isFocusedObjectsChanged()) {
    		return true;
    	}
    	List<SGAxisElement> axes = this.getAllAxisGroupList();
    	for (SGAxisElement axis : axes) {
    		if (axis.isChildFocusedObjectChanged()) {
    			return true;
    		}
    	}
    	return false;
    }

    void initializePressedElement() {
        this.mDraggableAxisScaleElement = null;
        this.mPressedElementOrigin = null;
    }

    // calculate appropriate axis range
    private SGValueRange calcRange(final double min, final double max,
            final double factor) {
        final double diff = max - min;
        double minNew = min;
        double maxNew = max;
        if (Math.abs(max - min) < Double.MIN_VALUE) {
            final double aValue = this.calcAlternativeAxisValue(min, max);
            minNew = min - aValue;
            maxNew = max + aValue;
        } else {
            final double margin = factor * diff;
            minNew = min - margin;
            maxNew = max + margin;

            minNew = SGUtilityNumber.getNumberInRangeOrder(minNew, min, max,
                    SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
            maxNew = SGUtilityNumber.getNumberInRangeOrder(maxNew, min, max,
                    SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT, BigDecimal.ROUND_HALF_UP);
        }

        SGValueRange range = new SGValueRange(minNew, maxNew);
        return range;
    }

    private double calcAlternativeAxisValue(final double min, final double max) {
        if (Math.abs(max - min) < Double.MIN_VALUE) {
        	if (min == 0) {
        		return 1.0;
        	} else {
        		final int order = SGUtilityNumber.getOrder(max);
        		final double num = SGUtilityNumber.getPowersOfTen(order - 1);
        		return num;
        	}
        } else {
        	return 0.0;
        }
    }
    
    /**
     * Creates a text string for the title.
     * 
     * @param ori
     *           the original text string
     * @param location
     *           the axis location
     * @return a text string for the title
     */
    private String createTitleString(final String ori, final int location) {
        String title = null;
        if (ori == null || "".equals(ori)) {
            switch (location) {
            case AXIS_HORIZONTAL_1:
                title = DEFAULT_BOTTOM_AXIS_TITLE;
                break;
            case AXIS_HORIZONTAL_2:
                title = DEFAULT_TOP_AXIS_TITLE;
                break;
            case AXIS_VERTICAL_1:
                title = DEFAULT_LEFT_AXIS_TITLE;
                break;
            case AXIS_VERTICAL_2:
                title = DEFAULT_RIGHT_AXIS_TITLE;
                break;
            case AXIS_NORMAL:
                title = DEFAULT_Z_AXIS_TITLE;
                break;
            default:
                break;
            }
        } else {
            title = SGUtility.addEscapeChar(ori);
        }
        return title;
    }
    
    /**
     * Creates X and Y-axes.
     */
    private void createXYAxisGroups(final SGTuple2d xRange, final SGTuple2d yRange, 
            final String xTitle, final String yTitle) {
        // bottom
        this.createAxisGroup(xRange, AXIS_HORIZONTAL_1, xTitle, DEFAULT_BOTTOM_AXIS_VISIBLE);
        
        // top
        this.createAxisGroup(xRange, AXIS_HORIZONTAL_2, xTitle, DEFAULT_TOP_AXIS_VISIBLE);

        // left
        this.createAxisGroup(yRange, AXIS_VERTICAL_1, yTitle, DEFAULT_LEFT_AXIS_VISIBLE);

        // right
        this.createAxisGroup(yRange, AXIS_VERTICAL_2, yTitle, DEFAULT_RIGHT_AXIS_VISIBLE);
    }

    /**
     * Creates a single axis group.
     * 
     */
    private SGFigureAxis createAxisGroup(final SGTuple2d range,
            final int location, final String title, final boolean visible) {
        final SGAxis axis = new SGAxis(range);
        final SGFigureAxis group = new SGFigureAxis(this);
        group.setLocationInPlane(location);
        group.mAxis = axis;
        group.setVisible(visible);
        group.setTitleText(title);
        this.mElementsGroupList.add(group);
        return group;
    }

    /**
     * Sets up X and Y-axes.
     */
    private void setupXYAxisGroups(final String xTitle, final String yTitle) {
    	SGAxisElement groupX1 = this.getAxisGroup(AXIS_HORIZONTAL_1);
    	SGAxisElement groupX2 = this.getAxisGroup(AXIS_HORIZONTAL_2);
    	SGAxisElement groupY1 = this.getAxisGroup(AXIS_VERTICAL_1);
    	SGAxisElement groupY2 = this.getAxisGroup(AXIS_VERTICAL_2);
        
        // set magnification
        final float mag = this.getMagnification();
        groupX1.setMagnification(mag);
        groupX2.setMagnification(mag);
        groupY1.setMagnification(mag);
        groupY2.setMagnification(mag);
        
        // set the title
        groupX1.setTitleText(xTitle);
        groupX2.setTitleText(xTitle);
        groupY1.setTitleText(yTitle);
        groupY2.setTitleText(yTitle);

    }

    /**
     * Add data.
     * 
     * @param data
     *            data added
     * @param name
     *            name of data
     * @return true if succeeded
     */
    public boolean addData(final SGData data, final String name) {
        if (super.addData(data, name) == false) {
            return false;
        }
        SGData[] dataArray = new SGData[] { data };
        if (this.setupAxes(dataArray) == false) {
        	return false;
        }
        return true;
    }
    
    private boolean setupAxes(SGData[] dataArray) {
    	
        // setup axes only once at start up
        if (this.mStartFlag) {

            // determine title strings
            String xt = "";
            String yt = "";
            if (dataArray.length > 0 && dataArray[0] instanceof SGIXYData) {
            	SGIXYData tData0 = (SGIXYData) dataArray[0];
            	xt = tData0.getTitleX();
            	if (xt == null) {
            		xt = "";
            	}
            	yt = tData0.getTitleY();
            	if (yt == null) {
            		yt = "";
            	}
            	if (dataArray.length > 1) {
                    for (int ii = 1; ii < dataArray.length; ii++) {
                    	if (dataArray[ii] instanceof SGIXYData) {
                        	SGIXYData tData = (SGIXYData) dataArray[ii];
                        	String xtCur = tData.getTitleX();
                        	if (xtCur == null) {
                        		xtCur = "";
                        	}
                        	if (!xtCur.equals(xt)) {
                        		xt = "";
                        		break;
                        	}
                    	}
                    }
                    for (int ii = 1; ii < dataArray.length; ii++) {
                    	if (dataArray[ii] instanceof SGIXYData) {
                        	SGIXYData tData = (SGIXYData) dataArray[ii];
                        	String ytCur = tData.getTitleY();
                        	if (ytCur == null) {
                        		ytCur = "";
                        	}
                        	if (!ytCur.equals(yt)) {
                        		yt = "";
                        		break;
                        	}
                    	}
                    }
            	}
            }
            final String xTitle = this.createTitleString(xt,
                    AXIS_HORIZONTAL_1);
            final String yTitle = this.createTitleString(yt,
                    AXIS_VERTICAL_1);

            // setup X and Y-axes
            this.setupXYAxisGroups(xTitle, yTitle);

    		if (dataArray.length == 1 && dataArray[0] instanceof SGISXYTypeData) {
    			SGISXYTypeData sxy = (SGISXYTypeData) dataArray[0];
                if (sxy.isTickLabelAvailable()) {
                    // for data with tick labels
                	SGAxisElement group1;
                	SGAxisElement group2;
                	if (sxy.isTickLabelHorizontal()) {
                        group1 = this.getAxisGroup(AXIS_HORIZONTAL_1);
                        group2 = this.getAxisGroup(AXIS_HORIZONTAL_2);
                	} else {
                        group1 = this.getAxisGroup(AXIS_VERTICAL_1);
                        group2 = this.getAxisGroup(AXIS_VERTICAL_2);
                	}
                    group1.setNumbersVisible(false);
                    group1.setTickMarkVisible(false);
                    group2.setNumbersVisible(false);
                    group2.setTickMarkVisible(false);
                    if (sxy.hasDateTypeXVariable()) {
                    	group1.setDateMode(true);
                    }
                    if (sxy.hasDateTypeYVariable()) {
                    	group2.setDateMode(true);
                    }
                }

                // set unit string
                String unitX = sxy.getUnitsStringX();
                if (unitX != null) {
                	String escapedUnit = SGUtility.addEscapeChar(unitX);
                    this.mAxisScale.setHorizontalUnit(escapedUnit);
                }
                String unitY = sxy.getUnitsStringY();
                if (unitY != null) {
                	String escapedUnit = SGUtility.addEscapeChar(unitY);
                    this.mAxisScale.setVerticalUnit(escapedUnit);
                }
            }

            // creates the map of lists of axis information
    		SGAxisElement groupHorizontal1 = this.getAxisGroup(AXIS_HORIZONTAL_1);
    		SGAxisElement groupHorizontal2 = this.getAxisGroup(AXIS_HORIZONTAL_2);
    		SGAxisElement groupVertical1 = this.getAxisGroup(AXIS_VERTICAL_1);
    		SGAxisElement groupVertical2 = this.getAxisGroup(AXIS_VERTICAL_2);
        	Map<SGData, List<SGDataAxisInfo>> axisInfoListMap = new HashMap<SGData, List<SGDataAxisInfo>>();
        	for (int ii = 0; ii < dataArray.length; ii++) {
                if (!(dataArray[ii] instanceof SGIXYData)) {
                    continue;
                }
                SGIXYData tData = (SGIXYData) dataArray[ii];
                
                // gets bounds
                SGValueRange xBounds = tData.getBoundsX();
                SGValueRange yBounds = tData.getBoundsY();
                
        		List<SGDataAxisInfo> axisInfoList = new ArrayList<SGDataAxisInfo>();
        		SGDataAxisInfo axisInfoHorizontal = new SGDataAxisInfo(dataArray[ii], groupHorizontal1.mAxis, 
        				xBounds, xTitle, AXIS_HORIZONTAL_1);
        		SGDataAxisInfo axisInfoVertical = new SGDataAxisInfo(dataArray[ii], groupVertical1.mAxis, 
        				yBounds, yTitle, AXIS_VERTICAL_1);
        		axisInfoList.add(axisInfoHorizontal);
        		axisInfoList.add(axisInfoVertical);
        		axisInfoListMap.put(dataArray[ii], axisInfoList);
        	}
        	
    		// fits axis range
        	this.fitAxes(dataArray, axisInfoListMap, new HashMap<SGData, SGISXYAxisShiftEnabled>(),
        			SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL);
        	this.fitAxes(dataArray, axisInfoListMap, new HashMap<SGData, SGISXYAxisShiftEnabled>(),
        			SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL);
        	
        	// copies the same axis range
        	SGAxisValue minHorizontal = groupHorizontal1.getMinValue();
        	SGAxisValue maxHorizontal = groupHorizontal1.getMaxValue();
        	groupHorizontal2.mAxis.setScale(minHorizontal, maxHorizontal);
        	SGAxisValue minVertical = groupVertical1.getMinValue();
        	SGAxisValue maxVertical = groupVertical1.getMaxValue();
        	groupVertical2.mAxis.setScale(minVertical, maxVertical);

            // creates all drawing elements
            this.createAllDrawingElements();
            
            // set up the axis scale
            this.setUpAxisScale();
        }
        
        // if the color bar is not available and SXYZ type of data is added, 
        // setup the color bar
        if (!this.mColorBarAvailableFlag && dataArray.length > 0) {

        	SGData data = dataArray[0];
        	
        	if (data instanceof SGIXYZData) {
        		
	            // in case of three-dimensional data
	            SGIXYZData tData = (SGIXYZData) data;
	            
	            String zTitle = null;
	            String dataType = data.getDataType();
	            if (SGDataUtility.isSXYZTypeData(dataType)) {
	                zTitle = this.createTitleString(tData.getTitleZ(), AXIS_NORMAL);
	                
	            } else {
	                throw new Error("Unsupported data type: " + data.getDataType());
	            }

	        	Map<SGData, List<SGDataAxisInfo>> axisInfoListMap = new HashMap<SGData, List<SGDataAxisInfo>>();
                SGValueRange zBounds = tData.getBoundsZ();
        		List<SGDataAxisInfo> axisInfoList = new ArrayList<SGDataAxisInfo>();
        		SGAxis axis = this.mZAxisElementsGroup.mAxis;
        		SGDataAxisInfo axisInfo = new SGDataAxisInfo(data, axis, zBounds, zTitle, AXIS_NORMAL);
        		axisInfoList.add(axisInfo);
        		axisInfoListMap.put(data, axisInfoList);
        		this.fitAxes(dataArray, axisInfoListMap, new HashMap<SGData, SGISXYAxisShiftEnabled>(),
	        			SGIFigureElementAxis.AXIS_DIRECTION_NORMAL);

	            // setup the z-axis
	            SGColorMap model = this.getColorMap();
	            model.setValueRange(axis.getMinDoubleValue(), axis.getMaxDoubleValue());
	            this.mZAxisElementsGroup.setTitleText(zTitle);
	            this.mZAxisElementsGroup.setMagnification(this.getMagnification());
	
	            // set the default location and size
	            final float gx = this.mGraphRectX;
	            final float gy = this.mGraphRectY;
	            final float gw = this.mGraphRectWidth;
	            final float gh = this.mGraphRectHeight;
	            this.mZAxisElementsGroup.setDirection(SGIColorBarConstants.DIRECTION_HORIZONTAL_LOWER);
	            this.mZAxisElementsGroup.setLocation(gx, gy + 1.20f * gh);
	            this.mZAxisElementsGroup.setSize(gw / this.mMagnification, 
	                    DEFAULT_COLOR_BAR_WIDTH / CM_POINT_RATIO);
	            
	            this.mZAxisElementsGroup.setVisible(true);
	            
	            // create all drawing elements
	            this.createAllDrawingElements();
	
	            // initialize the property history
	            if (this.mZAxisElementsGroup.initPropertiesHistory() == false) {
	                return false;
	            }
	            
	            // set the visibility flag to true
	            this.mColorBarAvailableFlag = true;
        	}
        }

        return true;
    }
    
    /**
     * Adds data objects with given name.
     * 
     * @param data
     *            an array of data objects
     * @param name
     *            an array of names of data objects
     * @param infoMap
     *            the information map of data object
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData[] data, final String[] name, final Map<String, Object> infoMap) {
    	if (data.length != name.length) {
    		return false;
    	}
    	for (int ii = 0; ii < data.length; ii++) {
    		if (super.addData(data[ii], name[ii]) == false) {
    			return false;
    		}
    	}
    	if (this.setupAxes(data) == false) {
    		return false;
    	}
    	return true;
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
        if (super.addData(data, name, p) == false) {
            return false;
        }
        if (data instanceof SGIXYZData) {
            if (data instanceof SGISXYZTypeData) {
                // set the visibility flag to true
                this.mColorBarAvailableFlag = true;
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
        if (this.mTextField.isVisible()) {
            if (this.closeTextField() == false) {
                return false;
            }
        }
        this.updateLocationOfDrawingElements();
        this.mAxisScale.updateLabelLocation();
        return true;
    }

    /**
     * 
     */
    public boolean getMarginAroundGraphRect(final SGTuple2f topAndBottom,
            final SGTuple2f leftAndRight) {

        if (super.getMarginAroundGraphRect(topAndBottom, leftAndRight) == false) {
            return false;
        }

        // groups
        final SGAxisElement groupTop = this
                .getAxisGroup(AXIS_HORIZONTAL_2);
        final SGAxisElement groupBottom = this
                .getAxisGroup(AXIS_HORIZONTAL_1);
        final SGAxisElement groupLeft = this
                .getAxisGroup(AXIS_VERTICAL_1);
        final SGAxisElement groupRight = this
                .getAxisGroup(AXIS_VERTICAL_2);
        if (groupTop == null || groupBottom == null || groupLeft == null
                || groupRight == null) {
            return false;
        }

        //
        // final float space =
        // this.getSpaceAxisLineAndNumber()*this.mMagnification;

        // get a bounding box which contains all drawing elements
        Rectangle2D rectAllTop = groupTop.getBoundingBox();
        Rectangle2D rectAllBottom = groupBottom.getBoundingBox();
        Rectangle2D rectAllLeft = groupLeft.getBoundingBox();
        Rectangle2D rectAllRight = groupRight.getBoundingBox();

        Rectangle2D gRect = this.getGraphRect();

        ArrayList rectList = new ArrayList();
        rectList.add(gRect);

        if (rectAllTop != null) {
            rectList.add(rectAllTop);
        }
        if (rectAllBottom != null) {
            rectList.add(rectAllBottom);
        }
        if (rectAllLeft != null) {
            rectList.add(rectAllLeft);
        }
        if (rectAllRight != null) {
            rectList.add(rectAllRight);
        }
        
        if (this.mZAxisElementsGroup.isVisible()) {
            rectList.add(this.mZAxisElementsGroup.getBoundingBox());
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
     * Sets the frame line width.
     * 
     * @param lw
     *          the line width to set
     * @return true if succeeded
     */
    public boolean setFrameLineWidth(final float lw) {
        this.mFrameLineStroke.setLineWidth(lw);
        return true;
    }

    /**
     * Sets the frame line width in a given unit.
     * 
     * @param lw
     *          the line width to set in a given unit
     * @param unit
     *          the unit of length
     * @return true if succeeded
     */
    public boolean setFrameLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        return this.setFrameLineWidth(lwNew);
    }

    /**
     * Sets the frame line color.
     * 
     * @param cl
     *          the frame line color to set
     * @return true if succeeded
     */
    public boolean setFrameLineColor(final Color cl) {
        if (cl == null) {
            throw new IllegalArgumentException("cl == null");
        }
        this.mFrameLineColor = cl;
        return true;
    }


    /**
     * Returns the space between axis line and numbers in the default unit at given location.
     * 
     * @param
     *     location of axis
     * @return the space between axis line and numbers in the default unit at given location
     */
    public float getSpaceAxisLineAndNumber(final int location) {
    	SGAxisElement aElement = this.getAxisGroup(location);
    	if (aElement == null) {
            throw new IllegalArgumentException("Invalid axis location: " + location);
    	}
        return aElement.getSpaceAxisLineAndNumbers();
    }

    /**
     * Returns the space between numbers and title in the default unit at given location.
     * 
     * @param
     *     location of axis
     * @return the space between numbers and title in the default unit at given location
     */
    public float getSpaceNumberAndTitle(final int location) {
    	SGAxisElement aElement = this.getAxisGroup(location);
    	if (aElement == null) {
            throw new IllegalArgumentException("Invalid axis location: " + location);
    	}
        return aElement.getSpaceTitleAndNumbers();
    }

    /**
     * Returns the list of focused objects.
     * 
     * @param list
     *           the list of focused objects
     * @return true if succeeded
     */
    public boolean getFocusedObjectsList(List<SGISelectable> list) {
    	List<SGISelectable> tempList = new ArrayList<SGISelectable>();
        if (this.mZAxisElementsGroup.isVisible()) {
        	tempList.add(this.mZAxisElementsGroup);
        	if (this.mZAxisElementsGroup.isTitleVisible()) {
            	tempList.add(this.mZAxisElementsGroup.mTitle);
        	}
        	if (this.mZAxisElementsGroup.isExponentVisible()) {
            	tempList.add(this.mZAxisElementsGroup.mExponentSymbol);
        	}
        }
        if (this.mAxisScale.isVisible() && this.mAxisScale.isValid()) {
        	tempList.add(this.mAxisScale);
        }
        List<SGFigureAxis> gList = this.getAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGFigureAxis group = gList.get(ii);
        	tempList.add(group);	// adds to the list independently of its visibility
            if (group.isVisible()) {
            	if (group.isTitleVisible()) {
                	tempList.add(group.mTitle);
            	}
            	if (group.isExponentVisible()) {
                	tempList.add(group.mExponentSymbol);
            	}
            }
        }
        for (SGISelectable s : tempList) {
        	if (s.isSelected()) {
        		list.add(s);
        	}
        }
        return true;
    }

    /**
     * 
     * 
     */
    public boolean hideSelectedObject(SGISelectable s) {
        return true;
    }

    /**
     * 
     * @param loc
     * @param x
     * @param y
     * @return
     */
    public double getValue(final int loc, final int x, final int y) {
        final SGAxisElement group = this.getAxisGroup(loc);
        final SGAxis axis = group.mAxis;
        double value = 0.0;
        if (loc == AXIS_HORIZONTAL_1
                || loc == AXIS_HORIZONTAL_2) {
            value = calcValue(x, axis, true);
        } else if (loc == AXIS_VERTICAL_1
                || loc == AXIS_VERTICAL_2) {
            value = calcValue(y, axis, false);
        } else {
            throw new Error();
        }
        value = SGUtilityNumber.getNumberInRangeOrder(value, axis);

        return value;
    }

    /**
     * Paint axes.
     */
    public void paintGraphics(Graphics g, boolean clip) {
        final Graphics2D g2d = (Graphics2D) g;

        // draw frame lines
        if (this.mFrameLineVisibleFlag) {
            this.drawGraphAreaBoundsLines(g2d);
        }

        // draw axes
        List<Point2D> anchorPointList = new ArrayList<Point2D>();
        List<SGFigureAxis> gList = this.getAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGFigureAxis group = gList.get(ii);
            if (group.isVisible() == false) {
                continue;
            }
            group.paintGraphics2D(g2d);
            anchorPointList.addAll(group.getAnchorPoints());
            if (group.mTitle != null) {
                if (group.mTitle.isSelected()) {
                    anchorPointList.addAll(group.mTitle.getAnchorPoints());
                }
            }
            if (group.mExponentSymbol != null) {
                if (group.mExponentSymbol.isSelected()) {
                    anchorPointList.addAll(group.mExponentSymbol.getAnchorPoints());
                }
            }
        }
        if (this.mSymbolsVisibleFlagAroundFocusedObjects) {
            SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(anchorPointList, g2d);
        }
        
        // draw color bar
        if (this.isColorBarAvailable() && this.isColorBarVisible()) {
            this.mZAxisElementsGroup.paintGraphics2D(g2d);
        }
        
        // draw axis scale
        if (this.mAxisScale.isVisible()) {
            this.mAxisScale.paint(g2d);
        }
    }

    // paint frame lines
    private boolean drawGraphAreaBoundsLines(final Graphics2D g2d) {
        if (g2d == null) {
            return false;
        }
        g2d.setPaint(this.mFrameLineColor);
        g2d.setStroke(new BasicStroke(this.getFrameLineWidth()));
        g2d.drawRect((int) (this.mGraphRectX), (int) (this.mGraphRectY),
                (int) (this.mGraphRectWidth), (int) (this.mGraphRectHeight));
        return true;
    }

    /**
     * Returns whether given axis is horizontal.
     * 
     * @param axis
     *            an axis
     * @return true when given axis is horizontal
     */
    public boolean isHorizontal(final SGAxis axis) {
        return this.getHorizontalAxisList().contains(axis);
    }

    /**
     * Returns whether given axis is vertical.
     * 
     * @param axis
     *            an axis
     * @return true when given axis is vertical
     */
    public boolean isVertical(final SGAxis axis) {
        return this.getVerticalAxisList().contains(axis);
    }

    /**
     * Returns whether a given axis is normal.
     * 
     * @param axis
     *           an axis
     * @return true if a given axis is normal
     */
    public boolean isNormal(final SGAxis axis) {
        if (this.mZAxisElementsGroup.mAxis.equals(axis)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns a list which contains all axes.
     * 
     * @return a list of all axes
     */
    public List<SGAxis> getAxisList() {
        final List<SGAxis> list = new ArrayList<SGAxis>();
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            list.add(group.mAxis);
        }
        return list;
    }

    /**
     * Returns a list of horizontal axes.
     * 
     * @return a list of horizontal axes
     */
    public List<SGAxis> getHorizontalAxisList() {
        final List<SGAxis> list = new ArrayList<SGAxis>();
        for (int ii = 0; ii < this.mElementsGroupList.size(); ii++) {
            final SGFigureAxis group = (SGFigureAxis) this.mElementsGroupList
                    .get(ii);
            final int loc = group.mLocationInPlane;
            if (loc == AXIS_HORIZONTAL_1 || loc == AXIS_HORIZONTAL_2) {
                list.add(group.mAxis);
            }
        }
        return list;
    }

    /**
     * 
     */
    public List<SGAxis> getVerticalAxisList() {
        final List<SGAxis> list = new ArrayList<SGAxis>();
        for (int ii = 0; ii < this.mElementsGroupList.size(); ii++) {
            final SGFigureAxis group = (SGFigureAxis) this.mElementsGroupList
                    .get(ii);
            final int loc = group.mLocationInPlane;
            if (loc == AXIS_VERTICAL_1 || loc == AXIS_VERTICAL_2) {
                list.add(group.mAxis);
            }
        }
        return list;
    }

    /**
     * Returns the list of normal axes.
     * 
     * @return the list of normal axes
     */
    public List<SGAxis> getNormalAxisList() {
        final List<SGAxis> list = new ArrayList<SGAxis>();
        list.add(this.mZAxisElementsGroup.mAxis);
        return list;
    }
    

    /**
     * 
     */
    public SGAxis getAxisInPlane(final int locInPlane) {
        final SGAxisElement group = this.getAxisGroup(locInPlane);
        if (group == null) {
            return null;
        }
        return group.mAxis;
    }

    /**
     * Returns an axis group of given location.
     * 
     * @param loc
     *            location of the axis in a plane
     * @return the axis group
     */
    SGAxisElement getAxisGroup(final int loc) {
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            if (group.getLocation() == loc) {
                return group;
            }
        }
        return null;
    }
    
    SGAxisElement getAxisGroup(SGAxis axis) {
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            if (group.mAxis.equals(axis)) {
                return group;
            }
        }
        return null;
    }

    /**
     * Returns a name of axis location in a plane.
     * 
     * @param axis
     * @return
     */
    public String getLocationName(final SGAxis axis) {
        final int loc = this.getLocationInPlane(axis);
        return this.getLocationName(loc);
    }

    /**
     * Returns the location name of a given location ID.
     * 
     * @param id
     *           a location ID
     * @return the location name
     */
    public String getLocationName(final int id) {
        return SGUtility.getLocationName(id);
    }

    /**
     * Returns the location ID of a given location name.
     * 
     * @param name
     *           a location name
     * @return the location ID
     */
    public int getLocationInPlane(final String name) {
        return SGUtility.getLocationInPlane(name);
    }

    /**
     * Returns a text string for the axis location in a plane.
     * 
     * @param axis
     *            an axis
     * @return location such as AXIS_HORIZONTAL_1
     */
    public String getLocationStringInPlane(final SGAxis axis) {
        final int loc = this.getLocationInPlane(axis);
        return this.getLocationName(loc);
    }

    /**
     * Returns a location in a plane from the location.
     * 
     * @param axis
     *            an axis
     * @return location such as AXIS_HORIZONTAL_1
     */
    public int getLocationInPlane(final SGAxis axis) {
        if (axis == null) {
            throw new IllegalArgumentException("axis == null");
        }
        for (int ii = 0; ii < this.mElementsGroupList.size(); ii++) {
            SGFigureAxis group = (SGFigureAxis) this.mElementsGroupList
                    .get(ii);
            if (axis.equals(group.mAxis)) {
                return group.mLocationInPlane;
            }
        }
        return -1;
    }

    /**
     * 
     * @param str
     * @return
     */
    public SGAxis getAxis(final String str) {
        final SGAxis axis = this.getAxisGroup(str).mAxis;
        return axis;
    }

    /**
     * 
     * @return
     */
    public String getClassDescription() {
        return "Axes";
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
    	List<SGIPropertyDialogObserver> list = new ArrayList<SGIPropertyDialogObserver>();
        if (SGFigureAxis.class.equals(cl)) {
        	List<SGFigureAxis> axes = this.getAxisGroupList();
        	for (SGFigureAxis axis : axes) {
        		if (axis.isVisible() && axis.isSelected()) {
                	list.add(axis);
        		}
        	}
        } else if (SGColorBarAxis.class.equals(cl)) {
            if (this.mZAxisElementsGroup.isVisible() 
            		&& this.mZAxisElementsGroup.isSelected()) {
                list.add(this.mZAxisElementsGroup);
            }
        } else if (SGAxisScale.class.equals(cl)) {
        	if (this.mAxisScale.isVisible()
        			&& this.mAxisScale.isSelected()) {
        		list.add(this.mAxisScale);
        	}
        }
    	return list;
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
    	List<SGIPropertyDialogObserver> list = new ArrayList<SGIPropertyDialogObserver>();
        if (SGFigureAxis.class.equals(cl)) {
        	List<SGFigureAxis> axes = this.getAxisGroupList();
        	for (SGFigureAxis axis : axes) {
        		if (axis.isVisible()) {
                	list.add(axis);
        		}
        	}
        } else if (SGColorBarAxis.class.equals(cl)) {
            if (this.mZAxisElementsGroup.isVisible()) {
                list.add(this.mZAxisElementsGroup);
            }
        } else if (SGAxisScale.class.equals(cl)) {
        	if (this.mAxisScale.isVisible()) {
        		list.add(this.mAxisScale);
        	}
        }
    	return list;
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
    	List<SGIPropertyDialogObserver> list = new ArrayList<SGIPropertyDialogObserver>();
        if (SGFigureAxis.class.equals(cl)) {
        	List<SGFigureAxis> axes = this.getAxisGroupList();
        	list.addAll(axes);
        } else if (SGColorBarAxis.class.equals(cl)) {
            list.add(this.mZAxisElementsGroup);
        }
    	return list;
    }

    /**
     * Returns a list of child nodes.
     * 
     * @return a list of child nodes
     */
    public ArrayList getChildNodes() {
        return new ArrayList();
    }

    /**
     * Returns a property dialog.
     * 
     * @return
     *         a property dialog
     */
    public SGPropertyDialog getPropertyDialog() {
        return this.mPropertyDialog;
    }

    // edited string
    SGDrawingElementString2D mEditingStringElement = null;

    // Returns whether a string is edited now
    boolean isEdited() {
        return this.mTextField.isVisible();
    }

    /**
     * 
     */
    float getMaxLengthOfScaleNumbers(final SGAxisElement group) {
        final List<ElementStringNumber> numberList = group.mNumberList;
        double maxLength = Double.MIN_VALUE;
        for (int ii = 0; ii < numberList.size(); ii++) {
            final SGDrawingElementString2D str = (SGDrawingElementString2D) numberList
                    .get(ii);
            final Rectangle2D rect = str.getElementBounds();
            final double width = rect.getWidth();
            // System.out.println(ii+" "+width);
            if (width > maxLength) {
                maxLength = width;
            }
        }

        return (float) maxLength;
    }

    // Creates all drawing elements.
    boolean createAllDrawingElements() {
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            if (group.createDrawingElements() == false) {
                return false;
            }
        }
        return true;
    }

    // Updates the location of all drawing elements.
    private boolean updateLocationOfDrawingElements() {
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            if (group.updateLocationOfDrawingElements() == false) {
                return false;
            }
        }
        this.mAxisScale.updateLocationWithAxisValues();
        this.mAxisScale.updateSizeWithAxisValues();
        this.mAxisScale.updateNumberLabelsWithLocation();
        return true;
    }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return SGIFigureElementAxis.TAG_NAME_AXES;
    }

    /**
     * Creates an array of Element objects.
     * 
     * @param document
     *           an Document objects to append elements
     * @return an array of Element objects
     */
    public Element[] createElement(final Document document, SGExportParameter params) {

    	List<Element> elList = new ArrayList<Element>();

        // create an Element object
        Element el = this.createThisElement(document, params);
        if (el == null) {
            return null;
        }

        // each axes
        List<SGFigureAxis> gList = this.getAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
            final SGAxisElement group = gList.get(ii);
            Element elAxis = group.createElement(document, params);
            if (elAxis == null) {
                return null;
            }
            el.appendChild(elAxis);
        }
        
        elList.add(el);
        
        // color bar
        if (this.isColorBarAvailable()) {
            Element elColorBar = this.mZAxisElementsGroup.createElement(document, params);
            if (elColorBar == null) {
            	return null;
            }
            elList.add(elColorBar);
        }
        
        // scale
        Element elScale = this.mAxisScale.createElement(document, params);
        if (elScale == null) {
        	return null;
        }
        elList.add(elScale);
        
        Element[] elArray = elList.toArray(new Element[elList.size()]);
        return elArray;
    }

    /**
     * 
     */
    public boolean createDataObject(final Element el, final SGData data, final boolean readDataProperty) {
        if (super.createDataObject(el, data, readDataProperty) == false) {
            return false;
        }
        
        if (data instanceof SGIXYZData) {
            if (data instanceof SGISXYZTypeData) {
                this.mColorBarAvailableFlag = true;
            }
        }

        // set false the flag
        this.mStartFlag = false;

        return true;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el, SGExportParameter params) {
	
        final int digitLineWidth = SGIConstants.LINE_WIDTH_MINIMAL_ORDER - 1;
		final float frameLineWidth = (float) SGUtilityNumber.roundOffNumber(
				this.getFrameLineWidth(), digitLineWidth);
		
        el.setAttribute(KEY_FRAME_LINE_VISIBLE, Boolean.toString(this.mFrameLineVisibleFlag));
        el.setAttribute(KEY_FRAME_LINE_WIDTH, Float.toString(frameLineWidth) + SGIConstants.pt);
        el.setAttribute(KEY_FRAME_LINE_COLOR, SGUtilityText.getColorString(this.mFrameLineColor));

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

        // set older properties
        if (this.setOlderProperties(element) == false) {
            return false;
        }

        // set false the flag
        this.mStartFlag = false;

        // read frame properties
        String str;
        Boolean b;
        Number num;
        Color cl;
        
        // frame line visible
        str = element.getAttribute(SGIFigureElementAxis.KEY_FRAME_LINE_VISIBLE);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean frameLineVisible = b.booleanValue();
            if (this.setFrameVisible(frameLineVisible) == false) {
                return false;
            }
        }

        // frame line width
        str = element.getAttribute(SGIFigureElementAxis.KEY_FRAME_LINE_WIDTH);
        if (str.length() != 0) {
            StringBuffer uFrameLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uFrameLineWidth);
            if (num == null) {
                return false;
            }
            final float frameLineWidth = num.floatValue();
            if (this.setFrameLineWidth(frameLineWidth, uFrameLineWidth.toString()) == false) {
                return false;
            }
        }

        // frame line color
        str = element.getAttribute(SGIFigureElementAxis.KEY_FRAME_LINE_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color frameLineColor = cl;
            if (this.setFrameLineColor(frameLineColor) == false) {
                return false;
            }
        }

        NodeList nList = element
                .getElementsByTagName(SGIFigureElementAxis.TAG_NAME_AXIS);
        for (int ii = 0; ii < nList.getLength(); ii++) {
            Node node = nList.item(ii);
            if (node instanceof Element) {
                Element el = (Element) node;

                // location
                str = el.getAttribute(SGIFigureElementAxis.KEY_POSITION);
                if (str.length() == 0) {
                    return false;
                }
                final int locInPlane = this.getLocationInPlane(str);
                if (locInPlane == -1) {
                    return false;
                }

                // get the axis-group
                SGAxisElement group = this.getAxisGroup(locInPlane);
                if (group.readProperties(el, versionNumber) == false) {
                    return false;
                }
                if (group.createDrawingElements() == false) {
                    return false;
                }
            }
        }

        Node parentNode = element.getParentNode();
        Element parentElement = (Element) parentNode;

        // create the color bar
        NodeList cbnList = parentElement
                .getElementsByTagName(SGIColorBarConstants.TAG_NAME_COLOR_BAR);
        if (cbnList.getLength() > 0) {
            // set properties
            Node node = cbnList.item(0);
            if (node instanceof Element) {
                Element el = (Element) node;
                if (this.mZAxisElementsGroup.readProperties(el, versionNumber) == false) {
                    return false;
                }
            }
        }
        
        // create the scale
        NodeList scaleList = parentElement
                .getElementsByTagName(SGIScaleConstants.TAG_NAME_SCALE);
        if (scaleList.getLength() > 0) {
            // set properties
            Node node = scaleList.item(0);
            if (node instanceof Element) {
                Element el = (Element) node;
                if (this.mAxisScale.readProperty(el) == false) {
                    return false;
                }
            }
        } else {
            // set up the axis scale
        	this.setUpAxisScale();
        }

        // create all drawing elements
        if (this.createAllDrawingElements() == false) {
        	return false;
        }

        return true;
    }

    // for property file
    // version <= 2.0.0
    private boolean setOlderProperties(final Element element) {
    	
    	final String versionNumber = SGUtility.getVersionNumber(element);
    	if (SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(
    			versionNumber, "2.0.0")) {
            String str = null;
            Number num = null;
            Color cl = null;
            
            // axis line width
            str = element.getAttribute(SGIFigureElementAxis.KEY_AXIS_LINE_WIDTH);
            if (str.length() == 0) {
                return false;
            }
            StringBuffer uAxisLineWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uAxisLineWidth);
            if (num == null) {
                return false;
            }
            final float axisLineWidth = num.floatValue();

            // tick mark width
            str = element.getAttribute(SGIFigureElementAxis.KEY_TICK_MARK_WIDTH);
            if (str.length() == 0) {
                return false;
            }
            StringBuffer uTickMarkWidth = new StringBuffer();
            num = SGUtilityText.getNumber(str, uTickMarkWidth);
            if (num == null) {
                return false;
            }
            final float tickMarkWidth = num.floatValue();

            // tick mark length
            str = element.getAttribute(SGIFigureElementAxis.KEY_TICK_MARK_LENGTH);
            if (str.length() == 0) {
                return false;
            }
            StringBuffer uTickMarkLength = new StringBuffer();
            num = SGUtilityText.getNumber(str, uTickMarkLength);
            if (num == null) {
                return false;
            }
            final float tickMarkLength = num.floatValue();

            // line color
            str = element.getAttribute(SGIFigureElementAxis.KEY_LINE_COLOR);
            if (str.length() == 0) {
                return false;
            }
            final Color lineColor = SGUtilityText.parseColor(str);

            //
            // font name
            //

            str = element.getAttribute(KEY_FONT_NAME);
            final String fontName = str;

            // title font name
            str = element.getAttribute(KEY_TITLE_FONT_NAME);
            final String titleFontName = str;

            // scale font name
            str = element.getAttribute(KEY_NUMBER_FONT_NAME);
            final String scaleFontName = str;

            // check whether the same font name is set to title and scale
            boolean sameFontNameFlag;
            if (fontName.length() != 0) {
                sameFontNameFlag = true;
            } else if (titleFontName.length() != 0 && scaleFontName.length() != 0) {
                sameFontNameFlag = false;
            } else {
                return false;
            }

            //
            // font style
            //

            str = element.getAttribute(KEY_FONT_STYLE);
            String fontStyleStr = str;

            // title font style
            str = element.getAttribute(KEY_TITLE_FONT_STYLE);
            String titleFontStyleStr = str;

            // scale font style
            str = element.getAttribute(KEY_NUMBER_FONT_STYLE);
            String scaleFontStyleStr = str;

            // check whether the same font style is set to title and scale
            boolean sameFontStyleFlag;
            if (fontStyleStr.length() != 0) {
                sameFontStyleFlag = true;
            } else if (titleFontStyleStr.length() != 0
                    && scaleFontStyleStr.length() != 0) {
                sameFontStyleFlag = false;
            } else {
                return false;
            }

            Integer fontStyle = null;
            Integer titleFontStyle = null;
            Integer scaleFontStyle = null;
            if (sameFontStyleFlag) {
                fontStyle = SGUtilityText.getFontStyle(fontStyleStr);
                if (fontStyle == null) {
                    return false;
                }
            } else {
                titleFontStyle = SGUtilityText.getFontStyle(titleFontStyleStr);
                if (titleFontStyle == null) {
                    return false;
                }
                scaleFontStyle = SGUtilityText.getFontStyle(scaleFontStyleStr);
                if (scaleFontStyle == null) {
                    return false;
                }
            }

            //
            // font size
            //

            str = element.getAttribute(KEY_FONT_SIZE);
            String fontSizeStr = str;

            // title font size
            str = element.getAttribute(KEY_TITLE_FONT_SIZE);
            String titleFontSizeStr = str;

            // scale font size
            str = element.getAttribute(KEY_NUMBER_FONT_SIZE);
            String scaleFontSizeStr = str;

            // check whether the same font size is set to title and scale
            boolean sameFontSizeFlag;
            if (fontSizeStr.length() != 0) {
                sameFontSizeFlag = true;
            } else if (titleFontSizeStr.length() != 0
                    && scaleFontSizeStr.length() != 0) {
                sameFontSizeFlag = false;
            } else {
                return false;
            }

            float fontSize = 0.0f;
            float titleFontSize = 0.0f;
            float scaleFontSize = 0.0f;
            StringBuffer uFontSize = new StringBuffer();
            StringBuffer uTitleFontSize = new StringBuffer();
            StringBuffer uScaleFontSize = new StringBuffer();
            if (sameFontSizeFlag) {
                num = SGUtilityText.getNumber(fontSizeStr, uFontSize);
                if (num == null) {
                    return false;
                }
                fontSize = num.floatValue();
            } else {
                num = SGUtilityText.getNumber(titleFontSizeStr, uTitleFontSize);
                if (num == null) {
                    return false;
                }
                titleFontSize = num.floatValue();

                num = SGUtilityText.getNumber(scaleFontSizeStr, uScaleFontSize);
                if (num == null) {
                    return false;
                }
                scaleFontSize = num.floatValue();
            }

            // string color
            str = element.getAttribute("StringColor");
            if (str.length() == 0) {
                return false;
            }
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color stringColor = cl;

            //
            // set properties
            //

            for (int ii = 0; ii < this.mElementsGroupList.size(); ii++) {
            	SGFigureAxis axis = this.mElementsGroupList.get(ii);
                if (axis.setAxisLineWidth(axisLineWidth, uAxisLineWidth.toString()) == false) {
                    return false;
                }
                if (axis.setTickMarkWidth(tickMarkWidth, uTickMarkWidth.toString()) == false) {
                    return false;
                }
                if (axis.setMajorTickMarkLength(tickMarkLength, uTickMarkLength.toString()) == false) {
                    return false;
                }
                if (axis.setMinorTickMarkLength(tickMarkLength, uTickMarkLength.toString()) == false) {
                    return false;
                }
                if (axis.setAxisLineColor(lineColor) == false) {
                    return false;
                }
                if (axis.setTickMarkColor(lineColor) == false) {
                    return false;
                }
                if (axis.setTitleFontColor(stringColor) == false) {
                    return false;
                }
                if (axis.setNumberFontColor(stringColor) == false) {
                    return false;
                }
                
                if (sameFontNameFlag) {
                    if (axis.setTitleFontName(fontName) == false) {
                        return false;
                    }
                    if (axis.setNumberFontName(fontName) == false) {
                        return false;
                    }
                } else {
                    if (axis.setTitleFontName(titleFontName) == false) {
                        return false;
                    }
                    if (axis.setNumberFontName(scaleFontName) == false) {
                        return false;
                    }
                }
                if (sameFontStyleFlag) {
                    if (axis.setTitleFontStyle(fontStyle.intValue()) == false) {
                        return false;
                    }
                    if (axis.setNumberFontStyle(fontStyle.intValue()) == false) {
                        return false;
                    }
                } else {
                    if (axis.setTitleFontStyle(titleFontStyle) == false) {
                        return false;
                    }
                    if (axis.setNumberFontStyle(scaleFontStyle) == false) {
                        return false;
                    }
                }
                if (sameFontSizeFlag) {
                    if (axis.setTitleFontSize(fontSize, uFontSize.toString()) == false) {
                        return false;
                    }
                    if (axis.setNumberFontSize(fontSize, uFontSize.toString()) == false) {
                        return false;
                    }
                } else {
                    if (axis.setTitleFontSize(titleFontSize, uTitleFontSize.toString()) == false) {
                        return false;
                    }
                    if (axis.setNumberFontSize(scaleFontSize, uScaleFontSize.toString()) == false) {
                        return false;
                    }
                }
            }
    	}

        return true;
    }
    
    SGAxisElement getAxisGroup(final String location) {
        int loc = -1;
        if (SGUtilityText.isEqualString(AXIS_BOTTOM, location)) {
            loc = AXIS_HORIZONTAL_1;
        } else if (SGUtilityText.isEqualString(AXIS_TOP, location)) {
            loc = AXIS_HORIZONTAL_2;
        } else if (SGUtilityText.isEqualString(AXIS_LEFT, location)) {
            loc = AXIS_VERTICAL_1;
        } else if (SGUtilityText.isEqualString(AXIS_RIGHT, location)) {
            loc = AXIS_VERTICAL_2;
        } else if (SGUtilityText.isEqualString(AXIS_COLOR_BAR, location)) {
            loc = AXIS_NORMAL;
        }
        return this.getAxisGroup(loc);
    }
    
    static class AxisProperties extends SGProperties {
    	// a dummy class
    }

    /**
     * Returns the properties.
     * 
     * @return the properties
     */
    public SGProperties getProperties() {
    	// returns a dummy instance
    	return new AxisProperties();
    }

    /**
     * Sets the properties.
     * 
     * @param p
     *          the properties to set
     * @return true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
    	// do nothing
        return true;
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
		if (source.equals(this.mTextField)) {
		    // hide the text field
		    if (c == KeyEvent.VK_ESCAPE) {
				this.hideEditField();
				this.mEditingStringElement = null;
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
                    mPropertyDialog = new SGAxisDialog(mDialogOwner, true);
                }
                if (mColorBarPropertyDialog == null) {
                    mColorBarPropertyDialog = new SGColorBarDialog(mDialogOwner, true);
                }
                if (mAxisScalePropertyDialog  == null) {
                	mAxisScalePropertyDialog = new SGAxisScaleDialog(mDialogOwner, true);
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
    	/*
        // create the font
        final Font font = new Font(this.getTitleFontName(), this
                .getTitleFontStyle(), (int) (this.getTitleFontSize() * this
                .getMagnification()));
        
        // update the text field
        this.updateTextField(this.mTextField, font);
        */
    }
    
    /**
     * Called when the caret in the text field is update.
     */
    public void caretUpdate(final CaretEvent e) {
//        final String str = this.mTextField.getText();
//        final Font font = new Font(this.getTitleFontName(), this
//                .getTitleFontStyle(), (int) (this.getTitleFontSize() * this
//                .getMagnification()));
//        final Rectangle2D stringRect = font.getStringBounds(str,
//                new FontRenderContext(null, false, false));
//
//        final double width = stringRect.getWidth();
//        if (width > this.mTextField.getWidth()) {
//            this.mTextField.setSize((int) (stringRect.getWidth() + this
//                    .getMagnification()
//                    * this.getTitleFontSize()), this.mTextField.getHeight());
//        }
    }

    /**
     * Called when menu items in the menu bar is selected.
     *
     */
    public void onMenuSelected() {
        if (this.mTextField.isVisible()) {
            this.closeTextField();
        }
    }

    /**
     * Returns the z-axis.
     * 
     * @return the z-axis
     */
    public SGAxis getZAxis() {
        return this.mZAxisElementsGroup.mAxis;
    }
    
    /**
     * Returns all axis groups.
     * 
     * @return the list of all axis groups
     */
    private List<SGFigureAxis> getAxisGroupList() {
        List<SGFigureAxis> list = new ArrayList<SGFigureAxis>();
        list.addAll(this.mElementsGroupList);
        return list;
    }

    /**
     * Returns all axis groups including the z-axis.
     * 
     * @return the list of all axis groups
     */
    private List<SGAxisElement> getAllAxisGroupList() {
        List<SGAxisElement> list = new ArrayList<SGAxisElement>(this.getAxisGroupList());
        if (this.mZAxisElementsGroup != null) {
            // add to the head of the list
            list.add(0, this.mZAxisElementsGroup);
        }
        return list;
    }

    /**
     * Returns whether the axis is available.
     * 
     * @param location
     *           location of an axis
     * @return true if the axis is available
     */
    public boolean isAxisAvailable(final String location) {
        if (AXIS_COLOR_BAR.equals(location)) {
            return true;
        } else if (AXIS_BOTTOM.equals(location)
                || AXIS_TOP.equals(location)
                || AXIS_LEFT.equals(location)
                || AXIS_RIGHT.equals(location)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the color map.
     * 
     * @return the color map
     */
    public SGColorMap getColorMap() {
    	SGColorBarAxis cAxis = (SGColorBarAxis) this.getAxisGroup(AXIS_NORMAL);
        return cAxis.getColorMap();
    }

    
    protected ArrayList getUndoableChildList() {
        ArrayList uList = new ArrayList();
        if (this.isColorBarAvailable()) {
            uList.add(this.mZAxisElementsGroup);
        }
        uList.add(this.mAxisScale);
        List<SGFigureAxis> axes = this.getAxisGroupList();
        for (SGFigureAxis axis : axes) {
        	uList.add(axis);
        }
        return uList;
    }

    /**
     * Returns whether the color bar is visible.
     * 
     * @return true if visible
     */
    public boolean isColorBarVisible() {
    	return this.mZAxisElementsGroup.isVisible();
    }

    /**
     * Sets whether the color bar is visible.
     * 
     * @param true to set visible
     * 
     * @return true if succeeded
     */
    public boolean setColorBarVisible(final boolean b) {
        return this.mZAxisElementsGroup.setVisible(b);
    }

    /**
     * Returns whether the color bar is available.
     * 
     * @return true if the color bar is available
     */
    public boolean isColorBarAvailable() {
        return this.mColorBarAvailableFlag;
    }
    
    /**
     * Fits axis range to the focused data.
     * 
     * @param element
     *           a figure element for data
     * @return true if succeeded
     */
    public boolean fitAxisRangeToFocusedData(final SGIFigureElementForData element, 
    		final int axisDirection, final boolean forAnimationFrames) {
        List<SGData> dList = element.getFocusedDataList();
        if (dList.size() == 0) {
        	return true;
        }
        return this.fitAxisRangeToData(element, dList, axisDirection,
        		forAnimationFrames);
    }

    public boolean fitAxisRangeToFocusedData(final SGIFigureElementForData element,
    		final boolean forAnimationFrames) {
    	if (!this.fitAxisRangeToFocusedData(element, SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToFocusedData(element, SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToFocusedData(element, SGIFigureElementAxis.AXIS_DIRECTION_NORMAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Fits axis range to given data objects.
     * 
     * @param element
     *           a figure element for data
     * @param dList
     *           a list of data
     * @return true if succeeded
     */
    @Override
    public boolean fitAxisRangeToData(final SGIFigureElementForData element,
            final List<SGData> dList, final int axisDirection,
            final boolean forAnimationFrames) {

        // creates a map of data and its display group-set
        List<SGIChildObject> elList = element.getVisibleChildList();
        Map<SGData, SGISXYAxisShiftEnabled> mapDataToShift = new HashMap<SGData, SGISXYAxisShiftEnabled>();
        for (int i = 0; i < elList.size(); i++) {
            SGIChildObject child = elList.get(i);
            if (child instanceof SGElementGroupSetInGraph) {
                SGElementGroupSetInGraph gs = (SGElementGroupSetInGraph)child;
                if (gs instanceof SGISXYAxisShiftEnabled) {
                    mapDataToShift.put(gs.getData(), (SGISXYAxisShiftEnabled)gs);
                }
            }
        }

        // creates a map of data-axis information list
        Map<SGData, List<SGDataAxisInfo>> axisInfoListMap = new HashMap<SGData, List<SGDataAxisInfo>>();
        for (int ii = 0; ii < dList.size(); ii++) {
            SGData data = dList.get(ii);
            List<SGDataAxisInfo> axisInfoList = element.getAxisInfoList(data,
            		forAnimationFrames);
            axisInfoListMap.put(data, axisInfoList);
        }

        SGData[] dataArray = new SGData[dList.size()];
        dList.toArray(dataArray);
        
        FitAxisResult result = this.fitAxes(dataArray, axisInfoListMap, mapDataToShift, axisDirection);
        
        final boolean axesChanged = (result.changedAxes.size() != 0);
        final boolean colorBarChanged = result.colorBarChanged;
        if (axesChanged || colorBarChanged) {
            // update the drawing elements
            this.createAllDrawingElements();
            this.notifyChange();

            // notify the changes
            if (axesChanged) {
            	for (SGFigureAxis axis : result.changedAxes) {
                    axis.setChanged(true);
            	}
            }
            if (colorBarChanged) {
                if (this.isColorBarAvailable()) {
                    this.mZAxisElementsGroup.setChanged(true);
                }
            }
        }

        return true;
    }
    
    public boolean fitAxisRangeToData(final SGIFigureElementForData element,
            final List<SGData> dList, final boolean forAnimationFrames) {
    	if (!this.fitAxisRangeToData(element, dList, SGIFigureElementAxis.AXIS_DIRECTION_HORIZONTAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToData(element, dList, SGIFigureElementAxis.AXIS_DIRECTION_VERTICAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	if (!this.fitAxisRangeToData(element, dList, SGIFigureElementAxis.AXIS_DIRECTION_NORMAL,
    			forAnimationFrames)) {
    		return false;
    	}
    	return true;
    }
    
	// Fits axes for given direction.
    private FitAxisResult fitAxes(SGData[] dataArray, Map<SGData, List<SGDataAxisInfo>> axisInfoListMap, 
    		Map<SGData, SGISXYAxisShiftEnabled> mapDataToShift, final int axisDirection) {
    	
        // check data types
        boolean allSXY = true;
        for (int ii = 0; ii < dataArray.length; ii++) {
            if (!(dataArray[ii] instanceof SGISXYTypeData)) {
                allSXY = false;
                break;
            }
        }
        boolean allVXY = true;
        for (int ii = 0; ii < dataArray.length; ii++) {
        	SGData data = dataArray[ii];
            if (!(data instanceof SGIVXYTypeData)) {
                allVXY = false;
                break;
            }
        }

    	List<Double> xMinList = new ArrayList<Double>();
    	List<Double> xMaxList = new ArrayList<Double>();
    	List<Double> yMinList = new ArrayList<Double>();
    	List<Double> yMaxList = new ArrayList<Double>();
    	
        // creates a map of axes and data ranges
        Map<SGAxis, List<SGValueRange>> axisRangeListMap = new HashMap<SGAxis, List<SGValueRange>>();
        for (SGData data: dataArray) {
            List<SGDataAxisInfo> axisInfoList = axisInfoListMap.get(data);
            for (SGDataAxisInfo axisInfo : axisInfoList) {
                SGValueRange range = axisInfo.getRange();
                SGAxis axis = axisInfo.getAxis();

                // check the axis
                if (!this.isAxisMatching(axis, axisDirection)) {
                	continue;
                }
                
                List<SGValueRange> rangeList = axisRangeListMap.get(axis);
                if (rangeList == null) {
                    rangeList = new ArrayList<SGValueRange>();
                    axisRangeListMap.put(axis, rangeList);
                }
                
                // contains shift movement if object shifts in SXY graph
                SGISXYAxisShiftEnabled shiftObject = mapDataToShift.get(data);
                if (shiftObject != null) {
                    Double minShift = shiftObject.getShiftMinValue(axis);
                    Double maxShift = shiftObject.getShiftMaxValue(axis);
                    SGTuple2d tuple = range.getRange();
                    double min = tuple.x;
                    double max = tuple.y;
                    if (minShift != null) {
                        min += minShift;
                    }
                    if (maxShift != null) {
                        max += maxShift;
                    }
                    Double barBaseline = shiftObject.getBarBaselineForAxis(axis);
                    if (barBaseline != null) {
                        if (min > barBaseline) {
                            min = barBaseline;
                        }
                        if (max < barBaseline) {
                            max = barBaseline;
                        }
                    }
                    range.setRange(min, max);
                }
                
                rangeList.add(range);
            }
            
            // get minimum values and maximum values
            if (allVXY && axisDirection != AXIS_DIRECTION_NORMAL) {
                for (SGDataAxisInfo axisInfo : axisInfoList) {
                    SGValueRange range = axisInfo.getRange();
                    SGAxis axis = axisInfo.getAxis();
                    if (this.isHorizontal(axis)) {
                        xMinList.add(Double.valueOf(range.getMinValue()));
                        xMaxList.add(Double.valueOf(range.getMaxValue()));
                    } else if (this.isVertical(axis)) {
                        yMinList.add(Double.valueOf(range.getMinValue()));
                        yMaxList.add(Double.valueOf(range.getMaxValue()));
                    }
                }
            }
        }

        // set the same minimum and maximum values for VXY data
        if (allVXY && axisDirection != AXIS_DIRECTION_NORMAL) {
        	double[] xMinArray = SGUtilityNumber.toArray(xMinList);
        	double[] xMaxArray = SGUtilityNumber.toArray(xMaxList);
        	double[] yMinArray = SGUtilityNumber.toArray(yMinList);
        	double[] yMaxArray = SGUtilityNumber.toArray(yMaxList);
        	double xMin = SGUtilityNumber.min(xMinArray);
        	double xMax = SGUtilityNumber.max(xMaxArray);
        	double yMin = SGUtilityNumber.min(yMinArray);
        	double yMax = SGUtilityNumber.max(yMaxArray);
        	double xDiff = xMax - xMin;
        	double yDiff = yMax - yMin;
        	SGValueRange xRangeNew, yRangeNew;
        	if (xDiff < yDiff) {
        		xRangeNew = this.calcRange(xMin, xMin + yDiff, 0.0);
        		yRangeNew = this.calcRange(yMin, yMax, 0.0);
        	} else {
        		xRangeNew = this.calcRange(xMin, xMax, 0.0);
        		yRangeNew = this.calcRange(yMin, yMin + xDiff, 0.0);
        	}
            for (Entry<SGAxis, List<SGValueRange>> e : axisRangeListMap.entrySet()) {
            	SGAxis axis = e.getKey();
            	if (this.isHorizontal(axis)) {
                	List<SGValueRange> rangeList = e.getValue();
                	for (SGValueRange range : rangeList) {
                		range.setRange(xRangeNew);
                	}
            	} else if (this.isVertical(axis)) {
                	List<SGValueRange> rangeList = e.getValue();
                	for (SGValueRange range : rangeList) {
                		range.setRange(yRangeNew);
                	}
            	}
            }
        }

        List<SGFigureAxis> changedAxes = new ArrayList<SGFigureAxis>();
        boolean colorBarChanged = false;
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
        	
            // get minimum and maximum axis values
            SGAxisElement group = gList.get(ii);
            SGAxis axis = group.mAxis;
            
            // check the axis
            if (!this.isAxisMatching(axis, axisDirection)) {
            	continue;
            }

            List<SGValueRange> rangeList = axisRangeListMap.get(axis);
            if (rangeList == null) {
                continue;
            }
            boolean bMin = false;
            boolean bMax = false;
            double min = Double.MAX_VALUE;
            double max = - Double.MAX_VALUE;
            for (int jj = 0; jj < rangeList.size(); jj++) {
            	SGValueRange range = rangeList.get(jj);
            	final double minValue = range.getMinValue();
            	final double maxValue = range.getMaxValue();
            	if (!Double.isNaN(minValue)) {
                    if (minValue < min) {
                        min = minValue;
                        bMin = true;
                    }
            	}
            	if (!Double.isNaN(maxValue)) {
                    if (maxValue > max) {
                        max = maxValue;
                        bMax = true;
                    }
            	}
            }
            if (!bMin && !bMax) {
                final double aValue = this.calcAlternativeAxisValue(min, max);
            	min = - aValue;
            	max = aValue;
            } else {
            	if (!bMin) {
                    final double aValue = this.calcAlternativeAxisValue(min, max);
            		min = max - aValue;
            	}
            	if (!bMax) {
                    final double aValue = this.calcAlternativeAxisValue(min, max);
            		max = min + aValue;
            	}
            }
            
            SGValueRange range = null;
        	if (allSXY) {
				if (axisDirection == AXIS_DIRECTION_HORIZONTAL
						&& this.isHorizontal(axis)) {
					range = this.calcRange(min, max, 0.0);
				} else if (axisDirection == AXIS_DIRECTION_VERTICAL
						&& this.isVertical(axis)) {
					range = this.calcRange(min, max, 0.05);
				}
        	} else if (allVXY && axisDirection != AXIS_DIRECTION_NORMAL) {
        		range = this.calcRange(min, max, 0.10);
        	}
            if (range != null) {
            	min = range.getMinValue();
            	max = range.getMaxValue();
            }
            
            if (min >= max) {
                final double aValue = this.calcAlternativeAxisValue(min, max);
                final double temp = min;
            	min = temp - aValue;
            	max = temp + aValue;
            }

            // set new axis range
            if (axis.isValidValue(min) && axis.isValidValue(max)) {
                final SGTuple2d rangeCur = axis.getRange();
                final SGTuple2d rangeNew = new SGTuple2d(min, max);
                if (rangeNew.equals(rangeCur) == false) {
                    if (axisDirection == AXIS_DIRECTION_NORMAL) {
                        if (this.isColorBarAvailable() 
                                && axis.equals(this.mZAxisElementsGroup.mAxis)) {
                            colorBarChanged = true;
                        }
                    } else {
                    	// figure axis
                        changedAxes.add((SGFigureAxis) group);
                    }
                }
            	final boolean dateMode = axis.getDateMode();
            	SGAxisValue minAxisValue = dateMode ? new SGAxisDateValue(min) : new SGAxisDoubleValue(min);
            	SGAxisValue maxAxisValue = dateMode ? new SGAxisDateValue(max) : new SGAxisDoubleValue(max);
            	axis.setScale(minAxisValue, maxAxisValue);
            }
        }
        
    	FitAxisResult result = new FitAxisResult();
        result.changedAxes = changedAxes;
        if (axisDirection == AXIS_DIRECTION_NORMAL) {
            result.colorBarChanged = colorBarChanged;
        }
        
        return result;
    }
    
    private boolean isAxisMatching(final SGAxis axis, final int axisDirection) {
        // check the axis
        boolean matches = false;
        if (axisDirection == AXIS_DIRECTION_HORIZONTAL) {
        	matches = this.isHorizontal(axis);
        } else if (axisDirection == AXIS_DIRECTION_VERTICAL) {
        	matches = this.isVertical(axis);
        } else if (axisDirection == AXIS_DIRECTION_NORMAL) {
        	matches = this.isNormal(axis);
        }
        return matches;
    }

    /**
     * Synchronize the axes to a given data object.
     * 
     * @param element
     *           a figure element for data
     * @param data
     *           a data object
     * @return true if succeeded
     */
    private boolean synchronizeAxis(final SGIFigureElementForData element, 
            final SGData data) {

        // get axis information
        List<SGDataAxisInfo> infoList = element.getAxisInfoList(data, false);
        
        // update the axis title
        List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (int ii = 0; ii < gList.size(); ii++) {
        	SGAxisElement group = gList.get(ii);
            for (int jj = 0; jj < infoList.size(); jj++) {
                SGDataAxisInfo info = (SGDataAxisInfo) infoList.get(jj);
                if (info.getAxis().equals(group.mAxis)) {
                    final String title = info.getTitle();
                    final int location = info.getLocation();
                    String titleNew = this.createTitleString(title, location);
                    group.setTitleText(titleNew);
                }
            }
        }

        // update the visibility of axis numbers and tick marks
		if (data instanceof SGISXYTypeData) {
			SGISXYTypeData sxy = (SGISXYTypeData) data;
			final Boolean tickLabelHorizontal = sxy.isTickLabelHorizontal();
			if (sxy.isTickLabelAvailable()) {
				for (int ii = 0; ii < gList.size(); ii++) {
					SGAxisElement group = gList.get(ii);
					for (int jj = 0; jj < infoList.size(); jj++) {
						SGDataAxisInfo info = (SGDataAxisInfo) infoList.get(jj);
						if (info.getAxis().equals(group.mAxis)) {
							final boolean visible = (this
									.isHorizontal(group.mAxis) != tickLabelHorizontal
									.booleanValue());
							group.setNumbersVisible(visible);
							group.setTickMarkVisible(visible);
						}
					}
				}
			}
		}

        // create all drawing elements
        if (this.createAllDrawingElements() == false) {
        	return false;
        }

        return true;
    }

    /**
     * Synchronizes axes with data column.
     * 
     * @param element
     *           figure element as an event source
     * @param msg
     *           the message
     */
    private void synchronizeDataColumnChange(final SGIFigureElementForData element, 
            final String msg) {
    	List<SGFigureAxis> axes = this.getAxisGroupList();
        if (SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW.equals(msg)
                || SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT.equals(msg)) {

            // set temporary properties
        	for (SGFigureAxis axis : axes) {
        		axis.prepare();
        	}

            // get current data
            List<SGData> dList = element.getFocusedDataList();
            if (dList.size() == 1) {
                SGData data = dList.get(0);
                
                // fit axis range
            	this.fitAxisRangeToData(element, dList, false);
                
                // synchronize axis titles
                this.synchronizeAxis(element, data);
                
            } else if (dList.size() == 0) {
            	for (SGFigureAxis axis : axes) {
                	axis.mTemporaryProperties = null;
            	}
            	return;
            } else {
                // this should not happen
                throw new Error("Data column selection for multiple data is not supported. dList.size="+dList.size());
            }
            
            // clear temporary properties on commit
            if (SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT.equals(msg)) {
            	for (SGFigureAxis axis : axes) {
                    SGProperties p = axis.getProperties();
                    if (p.equals(axis.mTemporaryProperties) == false) {
                        axis.setChanged(true);
                    }
                    
                    // clear temporary properties
                    axis.mTemporaryProperties = null;
            	}
            }
            
        } else if (SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_CANCEL.equals(msg)) {

        	// recover the properties
        	for (SGFigureAxis axis : axes) {
            	axis.setProperties(axis.mTemporaryProperties);
        	}
        	this.createAllDrawingElements();
        	this.notifyChange();
        	this.repaint();
        	
            // clear temporary properties
        	for (SGFigureAxis axis : axes) {
        		axis.mTemporaryProperties = null;
        	}
        }
        
    }

    /**
     * Sets the common properties.
     * 
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map) {
        SGPropertyResults result = new SGPropertyResults();

    	List<SGFigureAxis> axes = this.getAxisGroupList();
    	for (SGFigureAxis axis : axes) {
            if (axis.prepare() == false) {
                return null;
            }
    	}

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            
            // set common properties
            // backward compatibility for version <= 2.0.0
            if (COM_AXIS_FRAME_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_FRAME_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFrameVisible(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_FRAME_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_FRAME_VISIBLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_FRAME_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_FRAME_LINE_WIDTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setFrameLineWidth(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_FRAME_LINE_WIDTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_FRAME_LINE_WIDTH,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_FRAME_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setFrameLineColor(cl) == false) {
                        result.putResult(COM_AXIS_FRAME_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_AXIS_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setFrameLineColor(cl) == false) {
                        result.putResult(COM_AXIS_FRAME_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_AXIS_FRAME_COLOR,
                        SGPropertyResults.SUCCEEDED);
            }

            // set properties to child
        	for (SGFigureAxis axis : axes) {
        		result = axis.setProperties(map, result);
        	}
        }

        // commit the changes
    	for (SGFigureAxis axis : axes) {
            if (axis.commit() == false) {
                return null;
            }
    	}
        this.notifyToRoot();
        this.notifyChange();
        this.repaint();
        
        return result;
    }

    /**
     * Sets the properties of each axis.
     * 
     * @param id
     *           the ID of child object
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setChildProperties(final int id,
            SGPropertyMap map) {
    	List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (SGAxisElement group : gList) {
            if (group.getLocation() == id) {
                return group.setProperties(map);
            }
        }
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
    public SGPropertyResults setChildColorMapProperties(final int id, 
    		final String colorMapName, SGPropertyMap map) {
    	
    	SGColorBarAxis colorBarAxis = null;
    	List<SGAxisElement> gList = this.getAllAxisGroupList();
        for (SGAxisElement group : gList) {
            if (group.getLocation() == id && group instanceof SGColorBarAxis) {
            	colorBarAxis = (SGColorBarAxis) group;
            	break;
            }
        }
        if (colorBarAxis == null) {
        	return null;
        }
    	
        // set properties to the color map
        SGPropertyResults result = colorBarAxis.setColorMapProperties(colorMapName, map);
        return result;
    }

    @Override
    public SGPropertyResults setScaleProperties(SGPropertyMap map) {
    	return this.mAxisScale.setProperties(map);
    }

    Point getPressedPoint() {
    	return this.mPressedPoint;
    }
    
    void setPressedPoint(Point point) {
    	this.mPressedPoint = point; 
    }
    
    void setPressedPointLocation(final double x, final double y) {
    	this.mPressedPoint.setLocation(x, y);
    }

    public boolean showPopupMenu(JPopupMenu menu, final int x, final int y) {
    	return super.showPopupMenu(menu, x, y);
    }
    
    public void setMouseCursor(int type) {
    	super.setMouseCursor(type);
    }

    public void setMouseCursor(Cursor cursor) {
    	super.setMouseCursor(cursor);
    }

    public SGAxisValue calcBaselineValue(SGAxis axis) {
    	return super.calcBaselineValue(axis);
    }

    public SGAxisStepValue calcStepValue(SGAxis axis) {
    	return super.calcStepValue(axis);
    }

    public MouseDragResult getMouseDragResult(MouseEvent e) {
    	return super.getMouseDragResult(e);
    }
    
    /**
     * Returns whether the axis at given location is visible.
     * 
     * @param location
     *           location of an axis
     * @return true if the axis at given location is visible
     */
	@Override
	public boolean isAxisVisible(final int location) {
		SGAxisElement el = this.getAxisGroup(location);
		if (el == null) {
			throw new IllegalArgumentException("Invalid axis location: " + location);
		}
		return el.isVisible();
	}

    /**
     * Sets the visibility of the axis at given location is visible.
     * 
     * @param location
     *           location of an axis
     * @param b
     *           true to set visible
     */
	@Override
    public void setAxisVisible(final int location, final boolean b) {
		SGAxisElement el = this.getAxisGroup(location);
		if (el == null) {
			throw new IllegalArgumentException("Invalid axis location: " + location);
		}
//		final boolean pre = el.isVisible();
		el.setVisible(b);
//    	el.setChanged(b != pre);
//    	this.notifyToRoot();
//    	this.repaint();
    }

    public void setChanged(final int location, final boolean b) {
		SGAxisElement el = this.getAxisGroup(location);
		if (el == null) {
			throw new IllegalArgumentException("Invalid axis location: " + location);
		}
		el.setChanged(b);
    }
    
    public void hideSelectedAxes() {
		List<SGFigureAxis> axes = this.getAxisGroupList();
		for (SGFigureAxis axis : axes) {
			if (!axis.isVisible()) {
				continue;
			}
			if (!axis.isSelected()) {
				continue;
			}
			axis.exchangeVisibility();
		}
    }

    /**
     * Overrode to clear axes or the color bar.
     * 
     */
    @Override
    public boolean clearFocusedObjects(SGIFigureElement ori) {
    	if (!this.getClass().equals(ori.getClass())) {
    	    return this.clearFocusedObjects();
    	} else {
    		Class<?> cl = this.getPropertyDialogObserverClass();
    		// clear selection of "other" objects
    		if (SGFigureAxis.class.equals(cl)) {
    			this.mZAxisElementsGroup.setSelected(false);
    			this.mAxisScale.setSelected(false);
    		} else if (SGColorBarAxis.class.equals(cl)) {
    			List<SGFigureAxis> axes = this.getAxisGroupList();
    			for (SGFigureAxis axis : axes) {
    				axis.setSelected(false);
    			}
    			this.mAxisScale.setSelected(false);
    		} else if (SGAxisScale.class.equals(cl)) {
    			List<SGFigureAxis> axes = this.getAxisGroupList();
    			for (SGFigureAxis axis : axes) {
    				axis.setSelected(false);
    			}
    			this.mZAxisElementsGroup.setSelected(false);
    		} else {
    			return false;
    		}
    	    return true;
    	}
    }

	@Override
    protected List<MovableInfo> getMovableObjectList() {
    	List<MovableInfo> objList = new ArrayList<MovableInfo>();
    	List<SGAxisElement> axes = this.getAllAxisGroupList();
    	for (SGAxisElement axis : axes) {
    		if (!axis.isVisible()) {
    			continue;
    		}
    		if (axis.mTitle.isSelected()) {
    			objList.add(new MovableInfo(axis.mTitle, axis));
    		}
    		if (axis.isSelected()) {
    			objList.add(new MovableInfo(axis, axis));
    		}
    	}
    	if (this.mAxisScale.isVisible() && this.mAxisScale.isSelected()) {
			objList.add(new MovableInfo(this.mAxisScale, this.mAxisScale));
    	}
    	return objList;
    }

	static class FitAxisResult {
		List<SGFigureAxis> changedAxes = new ArrayList<SGFigureAxis>();
		boolean colorBarChanged = true;
	}
	
    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
	@Override
	public String getCommandString(SGExportParameter params) {
		StringBuffer sb = new StringBuffer();
    	List<SGFigureAxis> axes = this.getAxisGroupList();
    	for (SGAxisElement axis : axes) {
    		String cmdString = axis.getCommandString();
    		sb.append(cmdString);
    	}
    	if (this.isColorBarAvailable()) {
    		String cmdString = this.mZAxisElementsGroup.getCommandString();
    		sb.append(cmdString);
    	}
    	String scaleCmdString = this.mAxisScale.getCommandString();
    	sb.append(scaleCmdString);
		return sb.toString();
	}

	@Override
	public boolean getAxisDateMode(final int location) {
		SGAxis axis = this.getAxisInPlane(location);
		if (axis == null) {
			throw new IllegalArgumentException("Invalid location: " + location);
		}
		return axis.getDateMode();
	}
	
	static interface AxisScaleElement {
		
	}
	
    boolean hasDraggableAxisScaleElement() {
    	return (this.mDraggableAxisScaleElement != null);
    }

	@Override
    public boolean isAxisScaleVisible() {
    	return this.mAxisScale.isVisible();
    }
    
	@Override
    public void setAxisScaleVisibleForCommit(final boolean b) {
        final boolean cur = this.isAxisScaleVisible();
		this.mAxisScale.setVisible(b);
        if (cur != b) {
            this.mAxisScale.setChanged(true);
        }
	}
	
	@Override
    public void setColorBarVisibleForCommit(final boolean b) {
        final boolean v = this.isColorBarVisible();
        this.mZAxisElementsGroup.setVisible(b);
        if (v != b) {
            this.mZAxisElementsGroup.setChanged(true);
        }
    }

    @Override
    public List<SGINode> getNodes() {
    	List<SGINode> list =  new ArrayList<SGINode>();
    	if (this.isColorBarAvailable()) {
        	list.add(this.mZAxisElementsGroup);
    	}
    	list.add(this.mAxisScale);
    	return list;
    }

    @Override
    public void setAxisScaleVisible(final boolean b) {
        this.mAxisScale.setVisible(b);
    }

    // set up the axis scale
    private void setUpAxisScale() {
		SGAxisElement groupHorizontal = this.getAxisGroup(AXIS_HORIZONTAL_1);
		SGAxisElement groupVertical = this.getAxisGroup(AXIS_VERTICAL_1);
    	SGAxisValue minHorizontal = groupHorizontal.getMinValue();
    	SGAxisValue maxHorizontal = groupHorizontal.getMaxValue();
    	SGAxisValue minVertical = groupVertical.getMinValue();
    	SGAxisValue maxVertical = groupVertical.getMaxValue();
        this.mAxisScale.setXAxisLocation(AXIS_HORIZONTAL_1);
        this.mAxisScale.setYAxisLocation(AXIS_VERTICAL_1);
        final double xLen = groupHorizontal.getStepValue();
        final double yLen = groupVertical.getStepValue();
        this.mAxisScale.setXLengthValue(xLen);
        this.mAxisScale.setYLengthValue(yLen);
        double scaleX = (minHorizontal.getValue() + maxHorizontal.getValue()) / 2;
        scaleX = SGUtilityNumber.getNumberInRangeOrder(scaleX, groupHorizontal.getAxis());
        double scaleY = (minVertical.getValue() + maxVertical.getValue()) / 2;
        scaleY = SGUtilityNumber.getNumberInRangeOrder(scaleY, groupVertical.getAxis());
        this.mAxisScale.setXValue(scaleX);
        this.mAxisScale.setYValue(scaleY);
    }

}
