package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGColorBarColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGIColorBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIColorBarDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The axis for the color bar.
 * 
 */
class SGColorBarAxis extends SGAxisElement 
        implements SGINode, SGIUndoable, SGIColorBarDialogObserver,
        SGIColorBarConstants, SGIFigureConstants, SGIStringConstants {

    /**
     * Relative x-coordinate from the origin of the graph rectangle
     * at the default magnification.
     */
    private float mX = 0.0f;

    /**
     * Relative y-coordinate from the origin of the graph rectangle
     * at the default magnification.
     */
    private float mY = 0.0f;

    /**
     * Bar width of the color bar at the default magnification.
     */
    private float mBarWidth = 0.0f;

    /**
     * Length of the axis line and the color bar at the default magnification.
     */
    private float mBarLength = 0.0f;

    /**
     * The direction of color bar.
     */
    private String mDirection;

    /**
     * The x-axis for the location of this object.
     */
    private SGAxis mXAxis = null;
    
    /**
     * The y-axis for the location of this object.
     */
    private SGAxis mYAxis = null;
    
    /**
     * The pop-up menu.
     */
    private JPopupMenu mPopupMenu = null;

    /**
     * The color map manager.
     */
    private SGColorMapManager mColorMapManager = new SGColorBarColorMapManager();

    /**
     * The color map.
     */
    private SGColorMap mColorMap = null;

    /**
     * Builds a object.
     *
     */
    SGColorBarAxis(SGFigureElementAxis axisElement) {
        super(axisElement);
        
        // set axes
        SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_COLOR_BAR_HORIZONTAL_AXIS);
        SGAxis yAxis = this.mAxisElement.getAxis(DEFAULT_COLOR_BAR_VERTICAL_AXIS);
        this.mXAxis = xAxis;
        this.mYAxis = yAxis;

        // size
        this.setBarWidth(DEFAULT_COLOR_BAR_WIDTH, COLOR_BAR_SIZE_UNIT);
        this.setBarLength(DEFAULT_COLOR_BAR_LENGTH, COLOR_BAR_SIZE_UNIT);

        // direction
        this.mDirection = DEFAULT_COLOR_BAR_DIRECTION;

        // color map
        String colorMapName = SGColorBarColorMapManager.COLOR_MAP_NAME_HUE_GRADATION;
        this.mColorMap = this.mColorMapManager.getColorMap(colorMapName);
        this.mAxis = this.mColorMap.getAxis();
    }
    
    public void init() {
    	this.initLocationOfExponentDrawingElement();
    }
    
    @Override
    public void dispose() {
    	super.dispose();
        this.mColorMapManager = null;
        this.mColorMap = null;
    }

    /**
     * Returns the color map.
     * 
     * @return the color map
     */
    public SGColorMap getColorMap() {
        return this.mColorMap;
    }

    /**
     * Returns the shift of axis line.
     * 
     * @return the shift of axis line
     */
    public float getShift() {
    	// always returns zero
    	return 0.0f;
    }

    /**
     * Returns the shift of axis line in a given unit.
     * 
     * @param unit
     *           the unit of length
     * @return the shift of axis line
     */
    public float getShift(String unit) {
    	// always returns zero
    	return 0.0f;
    }

    /**
     * Sets the shift of axis line.
     * 
     * @param shift
     *            the shift of axis line to set
     * @param unit
     *            the unit of length
     * @return true if succeeded
     */
    public boolean setShift(final float shift, final String unit) {
    	// do nothing
    	return true;
    }

    /**
     * Returns a pop-up menu.
     * 
     * @return a pop-up menu
     */
    public JPopupMenu getPopupMenu() {
        JPopupMenu p = null;
        if (this.mPopupMenu != null) {
            p = this.mPopupMenu;
        } else {
            p = this.createPopupMenu();
            this.mPopupMenu = p;
        }
        
        JCheckBoxMenuItem item;
        Component[] array = p.getComponents();
        for (int ii = 0; ii < array.length; ii++) {
            if (array[ii] instanceof JCheckBoxMenuItem) {
                item = (JCheckBoxMenuItem) array[ii];
                String com = item.getActionCommand();
                if (MENUCMD_DRAW_LATER.equals(com)) {
                    item.setSelected(!SGFigureElementAxis.mNotifyChangeOnDraggingFlag);
                } else if (MENUCMD_SHOW_AXIS.equals(com)) {
                    item.setSelected(this.isVisible());
                }
            }
        }
        
        return p;
    }
    
    /**
     * Create a pop-up menu.
     * @return
     *         a pop-up menu
     */
    protected JPopupMenu createPopupMenu() {
        JPopupMenu p = new JPopupMenu();
        
        p.setBounds(0, 0, 100, 100);

        p.add(new JLabel("  -- Color Bar --"));
        p.addSeparator();

        SGUtility.addItem(p, this, MENUCMD_HIDE_COLOR_BAR);

        p.addSeparator();

        SGUtility.addItem(p, this, MENUCMD_PROPERTY);

        return p;
    }

    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals(MENUCMD_HIDE_COLOR_BAR)) {
            this.mVisible = false;
            this.mAxisElement.repaint();
            this.setChanged(true);
            this.notifyToRoot();
        } else if (command.equals(MENUCMD_PROPERTY)) {
            this.showPropertyDialog();
        } else if (command.equals(MENUCMD_DRAW_LATER)) {
            SGFigureElementAxis.mNotifyChangeOnDraggingFlag = !SGFigureElementAxis.mNotifyChangeOnDraggingFlag;
        }
    }

    /**
     * Creates a drawing element of a scale number.
     */
    protected ElementStringNumber createSingleNumberInstance(
            final String str) {
        ElementStringNumber el = new ElementStringNumber(str,
                this.mNumberFontName, this.mNumberFontStyle,
                this.mNumberFontSize, this.mNumberFontColor,
                this.getMagnification(),
                this.mNumberAngle, this.getLocationInPlane());
        return el;
    }

    /**
     * Creates a string element for the exponent.
     */
    protected ElementStringExponent createExponentStringElement(
            final String str) {
    	ElementStringExponent el = new ElementStringExponent(
    			this, str, this.mNumberFontName, this.mNumberFontStyle,
                this.mNumberFontSize, this.mNumberFontColor,
                this.getMagnification(), 0.0f);
        return el;
    }

    protected int getLocationInPlane() {
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)) {
            return SGIFigureElementAxis.AXIS_NORMAL_HORIZONTAL_LOWER;
        } else if (DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            return SGIFigureElementAxis.AXIS_NORMAL_HORIZONTAL_UPPER;
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)) {
            return SGIFigureElementAxis.AXIS_NORMAL_VERTICAL_LEFT;
        } else if (DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            return SGIFigureElementAxis.AXIS_NORMAL_VERTICAL_RIGHT;
        } else {
            return -1;
        }
    }
    
    protected int getLocation() {
    	return SGIFigureElementAxis.AXIS_NORMAL;
    }

    /**
     * Set properties to axis lines.
     * 
     * @return true if succeeded
     */
    protected boolean setAxisLineProperties() {
        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            this.mAxisLines[ii].setLineWidth(this.getAxisLineWidth());
            this.mAxisLines[ii].setColor(this.mAxisLineColor);
        }
        return true;
    }

    /**
     * Creates axis lines.
     * 
     * @return axis lines
     */
    protected ElementLineAxis[] createAxisLines() {
        ElementLineAxis[] array = new ElementLineAxis[4];
        for (int ii = 0; ii < array.length; ii++) {
            array[ii] = new ElementLineAxis();
        }
        return array;
    }

    /**
     * Create the scale lines.
     * 
     * @return true if succeeded
     */
    protected boolean createTickMarks() {
        // clear the list
        this.mTickMarksList.clear();

        final float lw = this.mTickMarkStroke.getLineWidth();
        
        // create
        boolean flag = true;
        switch (this.mAxis.getScaleType()) {
        case SGAxis.LINEAR_SCALE:
            flag = this.createTickMarksInLinearScale(
                    lw, this.mMajorTickMarkLength, this.mTickMarkColor);
            break;
        case SGAxis.LOG_SCALE:
            flag = this.createTickMarksInLogScale(
                    lw, this.mMajorTickMarkLength, this.mTickMarkColor);
            break;
        default:
            throw new Error();
        }

        return flag;
    }

    // Creates tick marks for a given axis value for given direction.
    protected void createTicksSub(final double value, 
            final float width, final float len, final Color cl, final boolean inside) {
        ElementLineTickMark el;
        el = this.createSingleTickMark(value,
                width, len, inside);
        if (el != null) {
            this.setupCreatedScaleLineProperties(el, value, cl);
        }
        el = this.createSingleTickMarkCounter(value,
                width, len, inside);
        if (el != null) {
            this.setupCreatedScaleLineProperties(el, value, cl);
        }
    }

    /**
     * Sets the location of the color bar.
     * 
     * @param x
     *           x coordinate
     * @param y
     *           y coordinate
     */
    public void setLocation(final float x, final float y) {
    	final float mag = this.mAxisElement.getMagnification();
        this.mX = (x - this.mAxisElement.getGraphRectX()) / mag;
        this.mY = (y - this.mAxisElement.getGraphRectY()) / mag;
    }
    
    /**
     * Sets the size of the color bar.
     * 
     * @param len
     *           the length
     * @param barWidth
     *           the bar width
     */
    public void setSize(final float len, final float barWidth) {
        this.mBarLength = len;
        this.mBarWidth = barWidth;
    }
    
    /**
     * Returns the x-coordinate of the color bar in the figure.
     * @return
     *         the x-coordinate of the color bar in the figure
     */
    public float getX() {
        return this.mAxisElement.getGraphRectX() + this.mX * this.mAxisElement.getMagnification();
    }

    /**
     * Returns the y-coordinate of the color bar in the figure.
     * @return
     *         the y-coordinate of the color bar in the figure
     */
    public float getY() {
        return this.mAxisElement.getGraphRectY() + this.mY * this.mAxisElement.getMagnification();
    }
    
    /**
     * Returns the length multiplied by the magnification.
     * 
     * @return the length of the color bar multiplied by the magnification
     */
    public float getLength() {
        return this.mBarLength * this.mAxisElement.getMagnification();
    }

    /**
     * Returns the bar width multiplied by the magnification.
     * 
     * @return the bar width of the color bar multiplied by the magnification
     */
    public float getBarWidth() {
        return this.mBarWidth * this.mAxisElement.getMagnification();
    }
    
    /**
     * Returns the bounds of color bar.
     * 
     * @return the bounds of color bar
     */
    public Rectangle2D getColorBarRectangle() {
        final double len = this.getLength();
        final double size = this.getBarWidth();
        final double w, h;
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            w = len;
            h = size;
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            w = size;
            h = len;
        } else {
            throw new Error();
        }
        Rectangle2D rect = new Rectangle2D.Double(
                this.getX(), this.getY(), w, h);
        return rect;
    }

    /**
     * Draw all objects.
     * 
     * @param g2d
     *           the graphic context
     */
    public void paintGraphics2D(final Graphics2D g2d) {
        
        if (!this.mVisible) {
            return;
        }
        
        // get the bounds of the color bar
        Rectangle2D bounds = this.getColorBarRectangle();

        final double x, y, w, h;
        final int nStart, nEnd;
        final double stepX, stepY;
        final double size = this.getBarWidth();
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            x = bounds.getMinX();
            y = bounds.getMinY();
            w = 1.0;
            h = size;
            nStart = (int) x;
            nEnd = (int) bounds.getMaxX();
            stepX = 1.0;
            stepY = 0.0;
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            x = bounds.getMinX();
            y = bounds.getMaxY();
            w = size;
            h = 1.0;
            nStart = (int) y;
            nEnd = (int) bounds.getMinY();
            stepX = 0.0;
            stepY = - 1.0;
        } else {
            throw new Error();
        }

        // get the value range of the color bar
        final double zMin = this.mColorMap.getMinValue();
        final double zMax = this.mColorMap.getMaxValue();
        final double zRange = zMax - zMin;

        // fill the rectangle
        final Rectangle2D rect = new Rectangle2D.Double();
        final int nDiff = Math.abs(nEnd - nStart);
        for (int ii = 0; ii <= nDiff; ii++) {
            rect.setRect(x + stepX * ii, y + stepY * ii, w, h);
            double ratio = (double) ii / nDiff;
            if (this.mAxis.isInvertCoordinates()) {
                ratio = 1.0 - ratio;
            }
            final double zValue = zMin + ratio * zRange;
            final Color cl = this.mColorMap.evaluate(
                    zValue, SGAxis.LINEAR_SCALE);
            g2d.setColor(cl);
            g2d.fill(rect);
        }
        
        // draw axis lines and numbers
        super.paintGraphics2D(g2d);
        
        // draw focused anchors
        if (this.mAxisElement.isSymbolsVisibleAroundFocusedObjects()
                && this.isSelected()) {
            ArrayList pList = this.getAnchorPointList();
            SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
                    pList, g2d);
        }
        
        // draw title anchors
        List<Point2D> anchorPointList = new ArrayList<Point2D>();
        if (this.mTitle != null) {
            if (this.mTitle.isSelected()) {
                anchorPointList.addAll(this.mTitle.getAnchorPoints());
            }
        }
        if (this.mExponentSymbol != null) {
            if (this.mExponentSymbol.isSelected()) {
                anchorPointList.addAll(this.mExponentSymbol.getAnchorPoints());
            }
        }
        if (this.mAxisElement.isSymbolsVisibleAroundFocusedObjects()) {
        	SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(anchorPointList, g2d);
        }

        // draw child anchors
        if (this.mAxisElement.isSymbolsVisibleAroundAllObjects()) {
            ArrayList pList = this.getAnchorPointList();
            SGUtilityForFigureElementJava2D.drawAnchorAsChildObject(pList,
                    g2d);
        }
    }
    
    /**
     * Returns a list of points to draw anchors.
     * 
     * @return
     *         a list of points to draw anchors
     */
    private ArrayList getAnchorPointList() {
        ArrayList list = new ArrayList();
        Rectangle2D rect = this.getColorBarRectangle();
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
     * Called when the mouse is pressed.
     */
    public boolean onMousePressed(final MouseEvent e) {
        Rectangle2D bounds = this.getColorBarRectangle();
        final boolean contained = bounds.contains(e.getPoint());
        if (super.onMousePressed(e)) {
            if (contained) {
                this.prepare();
            }
            return true;
        }
        if (contained) {
            this.prepare();
            return true;
        }
        return false;
    }
    
    /**
     * Called when the mouse is released.
     */
    public boolean onMouseReleased(final MouseEvent e) {
        if (!this.mVisible) {
            return false;
        }
        try {
        	if (this.mTitle.equals(this.mDraggingElement)) {
                // title
            	this.onTitleReleased();
            	return true;
            } else if (this.mExponentSymbol.equals(this.mDraggingElement)) {
            	// exponent symbol
            	this.onExponentReleased();
            	return true;
            } else if (this.mDraggingElement instanceof ElementStringNumber
            		|| this.mDraggingElement instanceof ElementLineTickMark) {
                // a number or a tick mark
            	this.setChangedFocusedObjects();
            	this.onMouseReleasedNumberOrTickMark();
                return true;
            }
        } finally {
        	this.onMouseReleasedFinally();
        }
        return false;
    }
    
    /**
     * Check whether a given point is on drawing element.
     */
    public boolean setMouseCursor(final int x, final int y) {
        if (!this.mVisible) {
            return false;
        }
        if (super.setMouseCursor(x, y)) {
            return true;
        }
        Rectangle2D bounds = this.getColorBarRectangle();
        if (bounds.contains(x, y)) {
            return true;
        }
        return false;
    }

    /**
     * Called when the mouse is clicked.
     * 
     * @param e
     *          the mouse event
     * @return true if some object is clicked
     */
    public boolean onMouseClicked(final MouseEvent e) {
        if (!this.mVisible) {
            return false;
        }
        if (super.onMouseClicked(e)) {
            return true;
        }
        Rectangle2D bounds = this.getColorBarRectangle();
        if (bounds.contains(e.getPoint())) {
            if (this.clicked(e)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Called when the mouse is dragged.
     */
    public boolean onMouseDragged(final MouseEvent e) {
        Point point = this.mAxisElement.getPressedPoint();
        if (point == null) {
            return false;
        }

        // set the location of the color bar
        if (this.isSelected() && this.mDraggingElement == null
                && this.mAxisElement.mDraggableAxisScaleElement == null) {
            return true;
    	} else if (this.mTitle.equals(this.mDraggingElement)) {
    		return true;
        } else if (this.mExponentSymbol.equals(this.mDraggingElement)) {
    		return true;
        }
        
        if (super.onMouseDragged(e) == false) {
    		// if mouse drag is succeeded for scale numbers or lines,
    		// returns true
            return false;
        }

        // update the range of the color model
        this.mColorMap.setValueRange(
                this.mAxis.getMinDoubleValue(), this.mAxis.getMaxDoubleValue());
        return true;
    }
    
    /**
     * Called when a string of the scale number is dragged.
     */
    protected AxisValueRange dragScaleNumber(MouseEvent e) {
        final int loc = this.getLocationInPlane();
        AxisValueRange range = null;
        switch(loc) {
        case AXIS_NORMAL_HORIZONTAL_LOWER:
        case AXIS_NORMAL_HORIZONTAL_UPPER:
            // horizontal axes
        	range = this.dragScaleNumberHorizontal(e, this.mAxisLines[0]);
        	break;
        case AXIS_NORMAL_VERTICAL_LEFT:
        case AXIS_NORMAL_VERTICAL_RIGHT:
            // vertical axes
        	range = this.dragScaleNumberPerpendiculer(e, this.mAxisLines[0]);
        	break;
       	default:
            throw new Error("Invalid location: " + loc);
        }
        return range;
    }

    /**
     * Called when a string of the scale number is dragged.
     */
    protected AxisValueRange dragElementLineOfScale(MouseEvent e) {
        final int loc = this.getLocationInPlane();
        AxisValueRange range = null;
        switch(loc) {
        case AXIS_NORMAL_HORIZONTAL_LOWER:
        case AXIS_NORMAL_HORIZONTAL_UPPER:
            // horizontal axes
        	range = this.dragScaleLineHorizontal(e, this.mAxisLines[0]);
        	break;
        case AXIS_NORMAL_VERTICAL_LEFT:
        case AXIS_NORMAL_VERTICAL_RIGHT:
            // vertical axes
        	range = this.dragScaleLinePerpendiculer(e, this.mAxisLines[0]);
        	break;
       	default:
            throw new Error("Invalid location: " + loc);
        }
        return range;
    }

    /**
     * Sets the location of the axis line.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfAxisLines() {
        float x1;
        float y1;
        float x2;
        float y2;
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            x1 = this.getX();
            y1 = this.getY();
            x2 = x1 + this.getLength();
            y2 = y1 + this.getBarWidth();
            this.mAxisLines[0].setTermPoints(x1, y1, x2, y1);
            this.mAxisLines[1].setTermPoints(x1, y1, x1, y2);
            this.mAxisLines[2].setTermPoints(x1, y2, x2, y2);
            this.mAxisLines[3].setTermPoints(x2, y1, x2, y2);
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            x1 = this.getX();
            y1 = this.getY();
            x2 = x1 + this.getBarWidth();
            y2 = y1 + this.getLength();
            this.mAxisLines[0].setTermPoints(x1, y2, x1, y1);
            this.mAxisLines[1].setTermPoints(x1, y2, x2, y2);
            this.mAxisLines[2].setTermPoints(x2, y2, x2, y1);
            this.mAxisLines[3].setTermPoints(x1, y1, x2, y1);
        } else {
            throw new Error();
        }
        return true;
    }

    /**
     * Creates a scale line.
     * 
     * @param value
     *           axis value of the scale line
     * @param length
     *           the length of the scale line
     * @param inside
     *            true for inside direction
     * @return an instance of the scale line
     */
    protected ElementLineTickMark createSingleTickMark(final double value,
            final float width, final float length, final boolean inside) {

        if (this.mAxis.insideRange(value) == false) {
            return null;
        }

        final float mag = this.mAxisElement.getMagnification();
        final boolean invcoord = this.isInvertCoordinates();

        double min;
        double max;
        double valueInScale;
        switch (this.mAxis.getScaleType()) {
        case SGAxis.LINEAR_SCALE: {
            min = this.mAxis.getMinDoubleValue();
            max = this.mAxis.getMaxDoubleValue();
            valueInScale = value;
            break;
        }

        case SGAxis.LOG_SCALE: {
            min = Math.log(this.mAxis.getMinDoubleValue());
            max = Math.log(this.mAxis.getMaxDoubleValue());
            valueInScale = Math.log(value);
            break;
        }

        default: {
            throw new Error();
        }
        }

        ElementLineTickMark el = null;
        final SGTuple2f start;
        final SGTuple2f end;
        final float len = this.getLength();
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            final double x;
            if (invcoord) {
                x = this.getX() + len * (max - valueInScale) / (max - min);
            } else {
                x = this.getX() + len * (valueInScale - min) / (max - min);
            }
            final double yStart = this.getY() + this.getBarWidth();
            final double yEnd;
            if (inside) {
                yEnd = yStart - length;
            } else {
                yEnd = yStart + length;
            }
            start = new SGTuple2f((float) x, (float) yStart);
            end = new SGTuple2f((float) x, (float) yEnd);
            
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            final double y;
            if (invcoord) {
                y = this.getY() + len * (1.0 - (max - valueInScale) / (max - min));
            } else {
                y = this.getY() + len * (1.0 - (valueInScale - min) / (max - min));
            }
            final double xStart = this.getX();
            final double xEnd;
            if (inside) {
                xEnd = xStart + length;
            } else {
                xEnd = xStart - length;
            }
            start = new SGTuple2f((float) xStart, (float) y);
            end = new SGTuple2f((float) xEnd, (float) y);

        } else {
            throw new Error();
        }
        
        el = new ElementLineOfColorBarScale(start, end, this);
        el.setLineWidth(width);
        el.setMagnification(mag);
        
        return el;
    }

    /**
     * Creates a scale line at the counter position.
     * 
     * @param value
     *            axis value of the scale line
     * @param length
     *            the length of the scale line
     * @return an instance of the scale line
     */
    protected ElementLineTickMark createSingleTickMarkCounter(final double value,
            final float width, final float length, final boolean inside) {
        ElementLineTickMark el = this.createSingleTickMark(value, width, length, inside);
        if (el == null) {
            return null;
        }
        
        // change the location
        final SGTuple2f start = el.getStart();
        final SGTuple2f end = el.getEnd();
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            final double yStart = start.y - this.getBarWidth();
            final double yEnd;
            if (inside) {
                yEnd = yStart + length;
            } else {
                yEnd = yStart - length;
            }
            start.y = (float) yStart;
            end.y = (float) yEnd;
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            final double xStart = start.x + this.getBarWidth();
            final double xEnd;
            if (inside) {
                xEnd = xStart - length;
            } else {
                xEnd = xStart + length;
            }
            start.x = (float) xStart;
            end.x = (float) xEnd;
        } else {
            throw new Error();
        }

        return el;
    }
    
    /**
     * Sets the location of the title.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfTitle() {
        final float mag = this.mAxisElement.getMagnification();
        final float factor = mag / SGIConstants.CM_POINT_RATIO;
        final float spaceLN = this.mSpaceAxisLineAndNumbers * factor;
        final float spaceNT = this.mSpaceTitleAndNumbers * factor;
        final float barWidth = this.getBarWidth();
        final float shiftFromCenter = this.getTitleShiftFromCenter() * factor;
        float scaleNumberHeight = 0.0f;
        float maxWidth = 0.0f;
        scaleNumberHeight = (float) getScaleHeight();
        maxWidth = this.mAxisElement.getMaxLengthOfScaleNumbers(this);
        Rectangle2D rectTitle = this.mTitle.getElementBounds();
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            this.mTitle.setAngle(0.0f);
            final float x = this.getX() + (this.getLength() - (float) rectTitle.getWidth()) / 2.0f + shiftFromCenter;
            final float y;
            if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)) {
                y = this.getY() + barWidth + spaceLN + scaleNumberHeight + spaceNT;
            } else {
                y = this.getY() - (spaceLN + scaleNumberHeight + spaceNT + (float) rectTitle.getHeight());
            }
            this.mTitle.setLocation(x, y);
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            this.mTitle.setAngle(90.0f); // rotation of 90 degree
            final float y = this.getY() + (this.getLength() + (float) rectTitle.getHeight()) / 2.0f + shiftFromCenter;
            final float x;
            if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)) {
                x = this.getX() - (spaceLN + maxWidth + spaceNT + (float) rectTitle.getWidth());
            } else {
                x = this.getX() + barWidth + (spaceLN + maxWidth + spaceNT);
            }
            this.mTitle.setLocation(x, y);
        } else {
            throw new Error();
        }
        
        return true;
    }

    /**
     * Sets the location of scale numbers.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfScaleNumbers() {
        final boolean invcoord = this.isInvertCoordinates();
        final int scaleType = this.mAxis.getScaleType();
        final float mag = this.mAxisElement.getMagnification();
        final float factor = mag / SGIConstants.CM_POINT_RATIO;
        final float spaceLN = this.mSpaceAxisLineAndNumbers * factor;

        double[] valueArrayInScale = null;
        if (scaleType == SGAxis.LINEAR_SCALE) {
            valueArrayInScale = this.mAxisValueArray;
        } else if (scaleType == SGAxis.LOG_SCALE) {
            valueArrayInScale = new double[this.mAxisValueArray.length];
            for (int ii = 0; ii < valueArrayInScale.length; ii++) {
                valueArrayInScale[ii] = Math.log(this.mAxisValueArray[ii]);
            }
        }

        double axisMinInScale = 0.0;
        double axisMaxInScale = 0.0;
        if (scaleType == SGAxis.LINEAR_SCALE) {
            axisMinInScale = this.mAxis.getMinDoubleValue();
            axisMaxInScale = this.mAxis.getMaxDoubleValue();
        } else if (scaleType == SGAxis.LOG_SCALE) {
            axisMinInScale = Math.log(this.mAxis.getMinDoubleValue());
            axisMaxInScale = Math.log(this.mAxis.getMaxDoubleValue());
        }

        final float barWidth = this.getBarWidth();
        final float barLength = this.getLength();
        for (int ii = 0; ii < this.mNumberList.size(); ii++) {
            ElementStringNumber el = this.mNumberList.get(ii);
            Rectangle2D rect = el.getElementBounds();
            final float width = (float) rect.getWidth();
            final float height = (float) rect.getHeight();
            if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                    || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
                
                final float x;
                if (invcoord) {
                    x = (float) (this.getX() + barLength
                            * (axisMaxInScale - valueArrayInScale[ii])
                            / (axisMaxInScale - axisMinInScale));
                } else {
                    x = (float) (this.getX() + barLength
                            * (valueArrayInScale[ii] - axisMinInScale)
                            / (axisMaxInScale - axisMinInScale));
                }
                
                final float y;
                if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)) {
                    y = this.getY() + barWidth + spaceLN;
                } else {
                    y = this.getY() - spaceLN - (float) rect.getHeight();
                }
                el.setLocation((x - 0.50f * width), y);
                
            } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                    || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
                
                final float y;
                if (invcoord) {
                    y = (float) (this.getY() + barLength
                            * (1.0 - (axisMaxInScale - valueArrayInScale[ii])
                                    / (axisMaxInScale - axisMinInScale)));
                } else {
                    y = (float) (this.getY() + barLength
                            * (1.0 - (valueArrayInScale[ii] - axisMinInScale)
                                    / (axisMaxInScale - axisMinInScale)));
                }
                
                final float x;
                if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)) {
                    x = (float) (this.getX() - spaceLN - rect.getWidth());
                } else {
                    x = (float) (this.getX() + barWidth + spaceLN);
                }
                el.setLocation(x, (y - 0.5f * height));
                
            } else {
                throw new Error();
            }
        }

        // the exponent drawing element
        if (this.isExponentVisible()) {
            this.setLocationOfExponentDrawingElement();
        }

        return true;
    }

    /**
     * Sets the location of exponent object.
     * 
     * @return true if succeeded
     */
    protected boolean setLocationOfExponentDrawingElement() {
    	SGIFigureElementAxis aElement = this.mAxisElement;
        final float barLength = this.getLength();
        final float barWidth = this.getBarWidth();
		final float baseX, baseY;
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            baseX = this.getX() + barLength;
            if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)) {
            	baseY = this.getY() + barWidth;
            } else {
            	baseY = this.getY();
            }
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
        	baseY = this.getY();
            if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)) {
            	baseX = this.getX();
            } else {
            	baseX = this.getX() + barWidth;
            }
        } else {
            throw new Error();
        }
		
        final float factor = aElement.getMagnification() / SGIConstants.CM_POINT_RATIO;
		final float x = baseX + this.mExponentLocationX * factor;
		final float y = baseY + this.mExponentLocationY * factor;

		// set the location to the drawing element
		this.mExponentSymbol.setLocation(x, y);

		return true;
    }

    private boolean initLocationOfExponentDrawingElement() {
        SGDrawingElementString2D el = this.mExponentSymbol;
        final Rectangle2D rect = el.getElementBounds();
        final float mag = this.mAxisElement.getMagnification();
        final float factor = mag / SGIConstants.CM_POINT_RATIO;
        final float spaceLN = this.mSpaceAxisLineAndNumbers * factor;
        final float spaceNT = this.mSpaceTitleAndNumbers * factor;
		float x, y;
        if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)
                || DIRECTION_HORIZONTAL_UPPER.equals(this.mDirection)) {
            x = 0.0f;
            if (DIRECTION_HORIZONTAL_LOWER.equals(this.mDirection)) {
                y = spaceLN
                        + (float) getScaleHeight() + spaceNT;
            } else {
                y = - (spaceLN
                        + (float) getScaleHeight() + spaceNT + (float) rect.getHeight());
            }
        } else if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)
                || DIRECTION_VERTICAL_RIGHT.equals(this.mDirection)) {
            y = - (float) rect.getHeight();
            if (DIRECTION_VERTICAL_LEFT.equals(this.mDirection)) {
                x = - (float) (spaceLN + this.mAxisElement.getMaxLengthOfScaleNumbers(this)
                        + spaceNT + rect.getWidth());
            } else {
                x = (float) (spaceLN + this.mAxisElement.getMaxLengthOfScaleNumbers(this) + spaceNT);
            }
        } else {
            throw new Error();
        }
		x /= mag;
		y /= mag;
		x *= SGIConstants.CM_POINT_RATIO;
		y *= SGIConstants.CM_POINT_RATIO;
		x = this.roundOffExponentShift(x);
		y = this.roundOffExponentShift(y);

		// set to the attributes
		this.mExponentLocationX = x;
		this.mExponentLocationY = y;

		return true;
    }

    /**
     * Translate this object.
     * 
     * @param dx
     *          displacement to the horizontal axis direction
     * @param dy
     *          displacement to the vertical axis direction
     */
    public void translate(final float dx, final float dy) {
        this.setLocation(this.getX() + dx, this.getY() + dy);
        
        // updates the location of drawing elements
        this.updateLocationOfDrawingElements();
    }

    /**
     * Shows the property dialog.
     * 
     */
    protected void showPropertyDialog() {
        // show the dialog
        this.mAxisElement.setPropertiesOfSelectedObjects(this);
    }
    
    public int getXAxisLocation() {
        return this.mAxisElement.getLocationInPlane(this.mXAxis);
    }

    public int getYAxisLocation() {
        return this.mAxisElement.getLocationInPlane(this.mYAxis);
    }

    public double getXValue() {
        SGAxis axis = this.mXAxis;
        double value = this.mAxisElement.calcValue(this.getX(), axis, true);
        if (Double.isNaN(value)) {
            return value;
        }
        value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
        return value;
    }

    public double getYValue() {
        SGAxis axis = this.mYAxis;
        double value = this.mAxisElement.calcValue(this.getY(), axis, false);
        if (Double.isNaN(value)) {
            return value;
        }
        value = SGUtilityNumber.getNumberInRangeOrder(value, axis);
        return value;
    }

    public float getBarWidth(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.mBarWidth, unit);
    }

    public float getBarLength(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.mBarLength, unit);
    }

    public String getDirection() {
        return this.mDirection;
    }

//    public boolean isReversedOrder() {
//        return this.mColorMap.isReversedOrder();
//    }

    public String getColorBarStyle() {
        return this.mColorMapManager.getColorMapName(this.mColorMap);
    }

    public boolean setXAxisLocation(int location) {
        if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
            return false;
        }
        this.mXAxis = this.mAxisElement.getAxisInPlane(location);
        return true;
    }

    public boolean setYAxisLocation(int location) {
        if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
            return false;
        }
        this.mYAxis = this.mAxisElement.getAxisInPlane(location);
        return true;
    }

    public boolean setXValue(final double value) {
        SGAxis axis = this.mXAxis;

        // current values gotten from the position of legend
        double currentValue = this.mAxisElement.calcValue(this.mAxisElement.getComponent().getX(), axis, true);
        currentValue = SGUtilityNumber.getNumberInRangeOrder(currentValue, axis);

        // if values from the dialog is different from the current values,
        // set the values from the dialog
        float x;
        if (value == currentValue) {
            x = this.mX;
        } else {
            final float x_ = this.mAxisElement.calcLocation(value, axis, true);
            x = (x_ - this.mAxisElement.getGraphRectX()) / this.mAxisElement.getMagnification();
        }
        this.mX = x;

        return true;
    }

    public boolean setYValue(double value) {
        SGAxis axis = this.mYAxis;

        // current values gotten from the position of legend
        double currentValue = this.mAxisElement.calcValue(this.mAxisElement.getComponent().getY(), axis, false);
        currentValue = SGUtilityNumber.getNumberInRangeOrder(currentValue, axis);

        // if values from the dialog is diffrent from the current values,
        // set the values from the dialog
        float y;
        if (value == currentValue) {
            y = mY;
        } else {
            final float y_ = this.mAxisElement.calcLocation(value, axis, false);
            y = (y_ - this.mAxisElement.getGraphRectY()) / this.mAxisElement.getMagnification();
        }
        this.mY = y;

        return true;
    }

	/**
	 * Sets the bar width.
	 * 
	 * @param w
	 *           the bar width to set
	 * @return true if succeeded
	 */
	public boolean setBarWidth(final float w) {
		if (w < 0.0f) {
			throw new IllegalArgumentException("w < 0.0f");
		}
		this.mBarWidth = w;
		return true;
	}

	/**
	 * Sets the bar width in a given unit.
	 * 
	 * @param w
	 *           the bar width to set in a given unit
	 * @param unit
	 *           an unit of length
	 * @return true if succeeded
	 */
    public boolean setBarWidth(final float w, String unit) {
		final Float sNew = SGUtility.calcPropertyValue(w, unit,
				COLOR_BAR_SIZE_UNIT, COLOR_BAR_WIDTH_MIN, COLOR_BAR_WIDTH_MAX,
				COLOR_BAR_SIZE_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		return this.setBarWidth(sNew.floatValue());
    }
    
	/**
	 * Sets the bar length.
	 * 
	 * @param len
	 *           the bar length to set
	 * @return true if succeeded
	 */
	public boolean setBarLength(final float len) {
		if (len < 0.0f) {
			throw new IllegalArgumentException("len < 0.0f");
		}
		this.mBarLength = len;
		return true;
	}

	/**
	 * Sets the bar length in a given unit.
	 * 
	 * @param len
	 *           the bar length to set in a given unit
	 * @param unit
	 *           an unit of length
	 * @return true if succeeded
	 */
    public boolean setBarLength(final float len, final String unit) {
		final Float sNew = SGUtility.calcPropertyValue(len, unit,
				COLOR_BAR_SIZE_UNIT, COLOR_BAR_LENGTH_MIN, COLOR_BAR_LENGTH_MAX,
				COLOR_BAR_SIZE_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		return this.setBarLength(sNew.floatValue());
    }

    /**
     * Sets the bar direction.
     * 
     * @param direction
     *           the direction of the color bar to set
	 * @return true if succeeded
     */
    public boolean setDirection(final String direction) {
    	String dir;
    	if (SGUtilityText.isEqualString(DIRECTION_HORIZONTAL_LOWER, direction)) {
    		dir = DIRECTION_HORIZONTAL_LOWER;
    	} else if (SGUtilityText.isEqualString(DIRECTION_HORIZONTAL_UPPER, direction))  {
    		dir = DIRECTION_HORIZONTAL_UPPER;
    	} else if (SGUtilityText.isEqualString(DIRECTION_VERTICAL_LEFT, direction)
    			|| SGUtilityText.isEqualString("Perpendicular Left", direction))  {
    		dir = DIRECTION_VERTICAL_LEFT;
    	} else if (SGUtilityText.isEqualString(DIRECTION_VERTICAL_RIGHT, direction)
    			|| SGUtilityText.isEqualString("Perpendicular Right", direction))  {
    		dir = DIRECTION_VERTICAL_RIGHT;
    	} else {
    		return false;
    	}
        this.mDirection = dir;
    	return true;
    }

	/**
	 * Sets to be reversed.
	 * 
	 * @param b
	 *            true to be reversed
	 * @return true if succeeded
	 */
	public boolean setReversedOrder(final boolean b) {
		this.mColorMapManager.setReversedOrder(b);
		return true;
	}

    public boolean setReversedOrder(final String name, final boolean b) {
    	this.mColorMapManager.setReversedOrder(name, b);
    	return true;
    }

    /**
     * Sets the color bar style.
     * 
     * @param style
     *           the color bar style
	 * @return true if succeeded
     */
    public boolean setColorBarStyle(final String style) {
        SGColorMap model = this.mColorMapManager.getColorMap(style);
        if (model == null) {
        	return false;
        }
        // set the current value range
        final double min = this.mColorMap.getMinValue();
        final double max = this.mColorMap.getMaxValue();
        final int scaleType = this.mColorMap.getScaleType();
        model.setValueRange(min, max, scaleType);
        
        this.mColorMap = model;
        return true;
    }
    
//    @Override
//    public boolean setColorMap(String name, SGProperties p) {
//    	if (!this.setColorBarStyle(name)) {
//    		return false;
//    	}
//    	if (!this.mColorMap.setProperties(p)) {
//    		return false;
//    	}
//    	return true;
//    }

    @Override
    public boolean setColors(String name, Color[] colors) {
    	if (name == null || colors == null) {
    		return false;
    	}
    	SGColorMap colorMap = this.mColorMapManager.getColorMap(name);
    	if (colorMap == null) {
    		return false;
    	}
    	colorMap.setColors(colors.clone());
    	return true;
    }
    
    /**
     * Sets the frame line width in a given unit.
     * 
     * @param lw
     *           the frame line width to set in a given unit
     * @param unit
     *           an unit of length
     * @return true if succeeded
     */
    public boolean setFrameLineWidth(final float lw, final String unit) {
    	Float lwNew = SGUtility.getLineWidth(lw, unit);
    	if (lwNew == null) {
    		return false;
    	}
        return this.setAxisLineWidth(lwNew.floatValue());
    }
    
    /**
     * Sets the range and the scale type.
     * 
     * @param minValue
     *           the minimum value
     * @param maxValue
     *           the maximum value
     * @param scaleType
     *           the scale type
     * @return true if succeeded
     */
    public boolean setScale(final SGAxisValue minValue, final SGAxisValue maxValue, final Integer scaleType) {
        if (super.setScale(minValue, maxValue, scaleType) == false) {
            return false;
        }
        final double min = (minValue == null) ? this.mColorMap.getMinValue() : minValue.getValue();
        final double max = (maxValue == null) ? this.mColorMap.getMaxValue() : maxValue.getValue();
        final int type = (scaleType == null) ? this.mColorMap.getScaleType() : scaleType.intValue();
        this.mColorMap.setValueRange(min, max, type);
        return true;
    }
    
    /**
     * 
     * @param value
     * @return
     */
    public boolean setInvertedCoordinates(final boolean b) {
        if (super.setInvertedCoordinates(b) == false) {
            return false;
        }
        this.mColorMap.setInvertCoordinates(b);
        return true;
    }

    /**
     * Returns whether the observer has valid x axis value.
     * 
     * @param config
     *           configuration of the x-axis
     * @param value
     *           axis value
     * @return true for valid axis value
     */
    public boolean hasValidXAxisValue(final int config, final Number value) {
        final SGAxis axis = (config == -1) ? this.mXAxis : this.mAxisElement.getAxisInPlane(config);
        final double v = (value != null) ? value.doubleValue() : this
                .getXValue();
        return axis.isValidValue(v);
    }

    /**
     * Returns whether the observer has valid y axis value.
     * 
     * @param config
     *           configuration of the y-axis
     * @param value
     *           axis value
     * @return true for valid axis value
     */
    public boolean hasValidYAxisValue(final int config, final Number value) {
        final SGAxis axis = (config == -1) ? this.mYAxis : this.mAxisElement.getAxisInPlane(config);
        final double v = (value != null) ? value.doubleValue() : this
                .getYValue();
        return axis.isValidValue(v);
    }

    public SGPropertyDialog getPropertyDialog() {
        return this.mAxisElement.mColorBarPropertyDialog;
    }
    
    /**
     * Called when the axis is clicked.
     * 
     * @param e
     *          the mouse event
     * @return true if a drawing element is clicked, and false otherwise
     */
    protected boolean clicked(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int cnt = e.getClickCount();
        
        // updates selected states
        this.mAxisElement.updateFocusedObjectsList(this, e);
        
        // avoids simultaneous selection
		if (this.isSelected()) {
			this.mTitle.setSelectedFlag(false);
			this.mExponentSymbol.setSelectedFlag(false);
		}

        if ((SwingUtilities.isRightMouseButton(e)) && (cnt == 1)) {
        	this.mAxisElement.showPopupMenu(this.getPopupMenu(), x, y);
        } else if ((SwingUtilities.isLeftMouseButton(e)) && (cnt == 2)) {
            this.showPropertyDialog();
        }
        
        return true;
    }

    public SGProperties getProperties() {
        ColorBarProperties p = new ColorBarProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }
    public boolean getProperties(SGProperties p) {
        if (super.getProperties(p) == false) {
            return false;
        }
        ColorBarProperties cp = (ColorBarProperties) p;
        cp.xAxis = this.mXAxis;
        cp.yAxis = this.mYAxis;
        cp.x = this.mX;
        cp.y = this.mY;
        cp.barWidth = this.mBarWidth;
        cp.barLength = this.mBarLength;
        cp.spaceLineAndNumbers = this.mSpaceAxisLineAndNumbers;
        cp.spaceTitleAndNumbers = this.mSpaceTitleAndNumbers;
        cp.direction = this.mDirection;
        cp.colorBarStyle = this.getColorBarStyle();
        cp.frameLineWidth = this.getAxisLineWidth();
        cp.tickMarkWidth = this.mTickMarkStroke.getLineWidth();
        cp.tickMarkLength = this.mMajorTickMarkLength;
        
        Map<String, SGColorMap> colorMaps = this.mColorMapManager.getColorMaps();
        Iterator<Entry<String, SGColorMap>> itr = colorMaps.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<String, SGColorMap> entry = itr.next();
        	String name = entry.getKey();
            SGColorMap colorMap = entry.getValue();
            cp.colorMapPropertiesMap.put(name, (ColorMapProperties) colorMap.getProperties());
        }

        return true;
    }
    
    public boolean setProperties(SGProperties p) {
        if (super.setProperties(p) == false) {
            return false;
        }
        ColorBarProperties cp = (ColorBarProperties) p;
        this.mXAxis = cp.xAxis;
        this.mYAxis = cp.yAxis;
        this.mX = cp.x;
        this.mY = cp.y;
        this.mBarWidth = cp.barWidth;
        this.mBarLength = cp.barLength;
        this.mSpaceAxisLineAndNumbers = cp.spaceLineAndNumbers;
        this.mSpaceTitleAndNumbers = cp.spaceTitleAndNumbers;
        this.mDirection = cp.direction;
        this.mColorMap = this.mColorMapManager.getColorMap(cp.colorBarStyle);
        this.mColorMap.setValueRange(
                this.mAxis.getMinDoubleValue(), this.mAxis.getMaxDoubleValue(), this.mAxis.getScaleType());
        this.setAxisLineWidth(cp.frameLineWidth);
        this.mTickMarkStroke.setLineWidth(cp.tickMarkWidth);
        this.mMajorTickMarkLength = cp.tickMarkLength;
        
        Iterator<Entry<String, ColorMapProperties>> itr = cp.colorMapPropertiesMap.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<String, ColorMapProperties> entry = itr.next();
        	String name = entry.getKey();
        	ColorMapProperties colorMapProperties = entry.getValue();
        	SGColorMap colorMap = this.mColorMapManager.getColorMap(name);
        	if (!colorMap.setProperties(colorMapProperties)) {
        		return false;
        	}
        }

        return true;
    }

    /**
     * Returns the list of child nodes.
     * 
     * @return the list of child nodes
     */
    public ArrayList getChildNodes() {
        return new ArrayList();
    }

    /**
     * Returns a text string that denotes this class.
     * 
     * @return a text string that denotes this class
     */
    public String getClassDescription() {
        return "Color Bar";
    }

    /**
     * Returns a text string that denotes this instance.
     * 
     * @return a text string that denotes this instance
     */
    public String getInstanceDescription() {
        return "Color Bar";
    }
    
    /**
     * Returns the tag name.
     * 
     * @return the tag name
     */
    public String getTagName() {
        return SGIColorBarConstants.TAG_NAME_COLOR_BAR;
    }

    /**
     * Create an Element object.
     * 
     * @param document
     *           a Document object to append the Element object
     * @return an Element object
     */
    public Element createElement(final Document document, SGExportParameter params) {
        Element element = super.createElement(document, params);
        
        // color map
        Element elColorMaps = this.mColorMapManager.createElement(document, params);
        if (elColorMaps == null) {
        	return null;
        }
        element.appendChild(elColorMaps);

        return element;
    }

//    /**
//     * Write the properties to an given Element object.
//     * 
//     * @param el
//     *          an Element object
//     * @return true if succeeded
//     */
//    public boolean writeProperty(final Element el, SGExportParameter params) {
//    	if (super.writeProperty(el, params) == false) {
//    		return false;
//    	}
//
//        final int digitColorBarSize = COLOR_BAR_SIZE_MINIMAL_ORDER - 1;
//        
//        final float barWidth = (float) SGUtilityNumber.roundOffNumber(
//                this.mBarWidth * SGIConstants.CM_POINT_RATIO,
//                digitColorBarSize);
//        final float barLength = (float) SGUtilityNumber.roundOffNumber(
//                this.mBarLength * SGIConstants.CM_POINT_RATIO,
//                digitColorBarSize);
//        
//        final String xAxisLocation = this.mAxisElement.getLocationName(this.mXAxis);
//        final String yAxisLocation = this.mAxisElement.getLocationName(this.mYAxis);
//        el.setAttribute(KEY_X_AXIS_POSITION, xAxisLocation);
//        el.setAttribute(KEY_Y_AXIS_POSITION, yAxisLocation);
//        el.setAttribute(KEY_VISIBLE, Boolean.toString(this.mVisible));
//        
//        // Style
//        el.setAttribute(KEY_COLOR_BAR_STYLE, this.getColorBarStyle());
//        el.setAttribute(KEY_COLOR_BAR_REVERSED_ORDER, Boolean
//                .toString(this.mColorMapManager.isReversedOrder()));
//
//        // Layout
//        el.setAttribute(KEY_X_VALUE, Double.toString(this.getXValue()));
//        el.setAttribute(KEY_Y_VALUE, Double.toString(this.getYValue()));
//        el.setAttribute(KEY_COLOR_BAR_WIDTH, Float.toString(barWidth) 
//                + COLOR_BAR_SIZE_UNIT);
//        el.setAttribute(KEY_COLOR_BAR_LENGTH, Float.toString(barLength) 
//                + COLOR_BAR_SIZE_UNIT);
//        el.setAttribute(KEY_COLOR_BAR_DIRECTION, this.mDirection);
//
//        // Frame Line
//        el.setAttribute(KEY_FRAME_LINE_WIDTH, Float.toString(frameLineWidth)
//                + LINE_WIDTH_UNIT);
//        
//        el.setAttribute(KEY_COLOR_BAR_SPACE_LINE_AND_NUMBERS, Float.toString(spaceLineAndNumbers) 
//                + SPACE_UNIT);
//        el.setAttribute(KEY_COLOR_BAR_SPACE_TITLE_AND_NUMBERS, Float.toString(spaceTitleAndNumbers) 
//                + SPACE_UNIT);
//        el.setAttribute(KEY_COLOR_BAR_LINE_COLOR, 
//                SGUtilityText.getColorString(this.mAxisLineColor));
//        el.setAttribute(KEY_TICK_MARK_WIDTH, Float.toString(tickMarkWidth)
//                + LINE_WIDTH_UNIT);
//        el.setAttribute(KEY_TICK_MARK_LENGTH, Float.toString(tickMarkLength)
//                + TICK_MARK_LENGTH_UNIT);
//        el.setAttribute(KEY_FONT_NAME, this.mTitle.getFontName());
//        el.setAttribute(KEY_FONT_SIZE, Float.toString(fontSize)
//                + FONT_SIZE_UNIT);
//        el.setAttribute(KEY_FONT_STYLE, SGUtilityText
//                .getFontStyleName(this.mTitle.getFontStyle()));
//        el.setAttribute(KEY_STRING_COLORS, SGUtilityText
//                .getColorString(this.mTitle.getColor()));
//
//        return true;
//    }

    /**
     * Read properties from a given Element object.
     * 
     * @param element
     *           an Element object
     * @param versionNumber
     *           version number
     * @return true if succeeded
     */
    protected boolean readProperties(final Element element, 
    		final String versionNumber) {

    	if (super.readProperties(element, versionNumber) == false) {
            return false;
        }
        
        String str = null;
        Number num = null;
        Boolean b = null;

        // date mode is always false
		if (!this.readScaleProperties(element, versionNumber, false)) {
			return false;
		}

        // x axis location
        str = element.getAttribute(KEY_X_AXIS_POSITION);
        if (str.length() != 0) {
            this.mXAxis = this.mAxisElement.getAxis(str);
        }

        // y axis location
        str = element.getAttribute(KEY_Y_AXIS_POSITION);
        if (str.length() != 0) {
            this.mYAxis = this.mAxisElement.getAxis(str);
        }
        
        // x value
        str = element.getAttribute(KEY_X_VALUE);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            if (this.setXValue(num.doubleValue()) == false) {
                return false;
            }
        }

        // y value
        str = element.getAttribute(KEY_Y_VALUE);
        if (str.length() != 0) {
            num = SGUtilityText.getDouble(str);
            if (num == null) {
                return false;
            }
            if (this.setYValue(num.doubleValue()) == false) {
                return false;
            }
        }

        // bar width
        str = element.getAttribute(SGIColorBarConstants.KEY_COLOR_BAR_WIDTH);
        if (str.length() != 0) {
            StringBuffer unit = new StringBuffer();
            num = SGUtilityText.getNumber(str, unit);
            if (num == null) {
                return false;
            }
            if (this.setBarWidth(num.floatValue(), unit.toString()) == false) {
                return false;
            }
        }

        // bar length
        str = element.getAttribute(SGIColorBarConstants.KEY_COLOR_BAR_LENGTH);
        if (str.length() != 0) {
            StringBuffer unit = new StringBuffer();
            num = SGUtilityText.getNumber(str, unit);
            if (num == null) {
                return false;
            }
            if (this.setBarLength(num.floatValue(), unit.toString()) == false) {
                return false;
            }
        }

        // direction
        str = element.getAttribute(SGIColorBarConstants.KEY_COLOR_BAR_DIRECTION);
        if (this.setDirection(str) == false) {
            return false;
        }
        
        // color bar style
        str = element.getAttribute(SGIColorBarConstants.KEY_COLOR_BAR_STYLE);
        if (this.mColorMapManager.getColorMap(str) == null) {
            return false;
        }
        if (this.setColorBarStyle(str) == false) {
            return false;
        }
        
        // reversed order
        // for backward compatibility for version <= 2.0.0
    	if (SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(
    			versionNumber, "2.0.0")) {
            str = element.getAttribute(SGIColorBarConstants.KEY_COLOR_BAR_REVERSED_ORDER);
            if (str.length() != 0) {
                b = SGUtilityText.getBoolean(str);
                if (b == null) {
                    return false;
                }
                this.mColorMapManager.setReversedOrder(b);
            }
    	}
        
        // color maps
        NodeList colorMapsNodeList = element.getElementsByTagName(SGColorMapManager.TAG_NAME_COLOR_STYLES);
        if (colorMapsNodeList.getLength() > 0) {
        	Element colorMapsElement = (Element) colorMapsNodeList.item(0);
            if (this.mColorMapManager.readProperty(colorMapsElement) == false) {
            	return false;
            }
        }

        // changes default colors
        // for backward compatibility for version <= 2.0.0
    	if (SGUtility.isVersionNumberEqualOrSmallerThanPermittingEmptyString(
    			versionNumber, "2.0.0")) {
    		String[] colorMapNames = { SGColorBarColorMapManager.COLOR_MAP_NAME_TWO_COLORS,
    				SGColorBarColorMapManager.COLOR_MAP_NAME_COLOR_GRAY_SCALE, 
    				SGColorBarColorMapManager.COLOR_MAP_NAME_COLOR_SAW };
    		for (String colorMapName : colorMapNames) {
    			SGColorMap colorMap = this.mColorMapManager.getColorMap(colorMapName);
    			Color[] colors = colorMap.getColors();
    			colors[1] = Color.WHITE;
    			colorMap.setColors(colors);
    		}
    	}
    	
        return true;
    }

    /**
	 * Sets the properties of this axis.
	 * 
	 * @param map
	 *            a map of properties
	 * @param iResult
	 *            the input result
	 * @return the updated result of setting properties
	 */
    @Override
    protected SGPropertyResults setProperties(SGPropertyMap map,
    		SGPropertyResults iResult) {
        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        
        // merges the results
        SGPropertyResults supResult = super.setProperties(map, iResult);
        Iterator<String> supItr = supResult.getKeyIterator();
        while (supItr.hasNext()) {
            String key = supItr.next();
            Integer status = supResult.getResult(key);
            String oriKey = map.getOriginalKey(key);
            result.putResult(oriKey, status);
        }        
        
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
			if (COM_COLOR_BAR_AXIS_X.equalsIgnoreCase(key)) {
				final int loc = SGUtility.getAxisLocation(value);
				if (loc == -1) {
					result.putResult(COM_COLOR_BAR_AXIS_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXAxisLocation(loc) == false) {
					result.putResult(COM_COLOR_BAR_AXIS_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result
						.putResult(COM_COLOR_BAR_AXIS_X,
								SGPropertyResults.SUCCEEDED);
			} else if (COM_COLOR_BAR_AXIS_Y.equalsIgnoreCase(key)) {
				final int loc = SGUtility.getAxisLocation(value);
				if (loc == -1) {
					result.putResult(COM_COLOR_BAR_AXIS_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYAxisLocation(loc) == false) {
					result.putResult(COM_COLOR_BAR_AXIS_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_COLOR_BAR_AXIS_Y,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_COLOR_BAR_LOCATION_X.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_COLOR_BAR_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
					result.putResult(COM_COLOR_BAR_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setXValue(num.doubleValue()) == false) {
					result.putResult(COM_COLOR_BAR_LOCATION_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_COLOR_BAR_LOCATION_X,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_COLOR_BAR_LOCATION_Y.equalsIgnoreCase(key)) {
				Double num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_COLOR_BAR_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
					result.putResult(COM_COLOR_BAR_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setYValue(num.doubleValue()) == false) {
					result.putResult(COM_COLOR_BAR_LOCATION_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_COLOR_BAR_LOCATION_Y,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_COLOR_BAR_WIDTH.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_COLOR_BAR_WIDTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setBarWidth(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_COLOR_BAR_WIDTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_COLOR_BAR_WIDTH,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_COLOR_BAR_LENGTH.equalsIgnoreCase(key)) {
				StringBuffer unit = new StringBuffer();
				Number num = SGUtilityText.getNumber(value, unit);
				if (num == null) {
					result.putResult(COM_COLOR_BAR_LENGTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (this.setBarLength(num.floatValue(), unit.toString()) == false) {
					result.putResult(COM_COLOR_BAR_LENGTH,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_COLOR_BAR_LENGTH,
						SGPropertyResults.SUCCEEDED);
            } else if (COM_COLOR_BAR_DIRECTION.equalsIgnoreCase(key)) {
                if (this.setDirection(value) == false) {
                    result.putResult(COM_COLOR_BAR_DIRECTION,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_COLOR_BAR_DIRECTION,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_COLOR_BAR_STYLE.equalsIgnoreCase(key)) {
            	if (this.setColorBarStyle(value) == false) {
                    result.putResult(COM_COLOR_BAR_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                result.putResult(COM_COLOR_BAR_STYLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_SCALE_MIN_VALUE.equalsIgnoreCase(key)) {
            	this.setScaleMinValue(map, key, value, false, result);
            } else if (COM_AXIS_SCALE_MAX_VALUE.equalsIgnoreCase(key)) {
            	this.setScaleMaxValue(map, key, value, false, result);
            } else if (COM_AXIS_SCALE_RANGE.equalsIgnoreCase(key)) {
            	this.setScaleRange(map, key, value, false, result);
            } else if (COM_AXIS_SCALE_BASE.equalsIgnoreCase(key)
            		|| COM_AXIS_TICK_MARK_BASE.equalsIgnoreCase(key)) {
            	this.setScaleBaselineValue(map, key, value, false, result);
            } else if (COM_AXIS_SCALE_STEP.equalsIgnoreCase(key)
            		|| COM_AXIS_TICK_MARK_STEP.equalsIgnoreCase(key)) {
            	this.setScaleStepValue(map, key, value, false, result);
			} else if (COM_COLOR_MAP_REVERSED_ORDER.equalsIgnoreCase(key)) {
				// for backward compatibility
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_COLOR_MAP_REVERSED_ORDER,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setReversedOrder(b.booleanValue());
                result.putResult(COM_COLOR_MAP_REVERSED_ORDER,
                        SGPropertyResults.SUCCEEDED);
			} else if (COM_COLOR_BAR_LINE_COLOR.equalsIgnoreCase(key)) {
				// for backward compatibility
            	String k = map.getOriginalKey(key);
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(k,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
					cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(k,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(k,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(k,
						SGPropertyResults.SUCCEEDED);
            } else if (COM_COLOR_BAR_SPACE_TO_SCALE.equalsIgnoreCase(key)) {
            	// for backward compatibility
            	String k = map.getOriginalKey(key);
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(k, 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setSpaceAxisLineAndNumbers(num.floatValue(), unit.toString()) == false) {
                    result.putResult(k, 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k, 
                		SGPropertyResults.SUCCEEDED);
    		}
        }
        
        return result;
    }
    
    /**
     * Sets the properties of color map.
     * 
     * @param colorMapName
     *           the name of color map
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setColorMapProperties(final String colorMapName, SGPropertyMap map) {
		SGColorMap colorMap = this.mColorMapManager.getColorMap(colorMapName);
		if (colorMap == null) {
			return null;
		}
		
        // prepare
        if (this.prepare() == false) {
            return null;
        }

        // sets the properties to the color map
        SGPropertyResults result = colorMap.setProperties(map);

        // sets the style
        if (this.setColorBarStyle(colorMapName) == false) {
        	return null;
        }
        
        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.notifyToRoot();
        
        return result;
	}
    
    /**
     * Overrode to create a scale line instance.
     */
    protected ElementLineTickMark createScaleLineInstance(ElementLineTickMark el) {
    	return new ElementLineOfColorBarScale(el);
    }

	@Override
	protected String getLineWidthPropertyFileKey() {
		return KEY_FRAME_LINE_WIDTH;
	}

	@Override
	protected String getLineColorPropertyFileKey() {
		return KEY_COLOR_BAR_LINE_COLOR;
	}

	@Override
	protected String getSpaceLineAndNumberPropertyFileKey() {
		return KEY_COLOR_BAR_SPACE_LINE_AND_NUMBERS;
	}

	@Override
	protected String getLineWidthCommandKey() {
		return COM_COLOR_BAR_FRAME_LINE_WIDTH;
	}

	@Override
	protected String getLineColorCommandKey() {
		return COM_COLOR_BAR_FRAME_LINE_COLOR;
	}

	@Override
	protected String getSpaceLineAndNumberCommandKey() {
		return COM_COLOR_BAR_SPACE_FRAME_LINE_AND_NUMBER;
	}

	@Override
	public SGColorMap getColorMap(final String name) {
		return this.mColorMapManager.getColorMap(name);
	}
	
    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getPropertyMap() {
    	SGPropertyMap map = super.getPropertyMap();
    	
    	// axis
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_AXIS_X, 
				this.mAxisElement.getLocationName(this.mXAxis));
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_AXIS_Y, 
				this.mAxisElement.getLocationName(this.mYAxis));

		// location
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_LOCATION_X, this.getXValue());
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_LOCATION_Y, this.getYValue());
    	
		// layout
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_WIDTH, 
				SGUtility.getExportValue(this.getBarWidth(COLOR_BAR_SIZE_UNIT), 
						COLOR_BAR_SIZE_MINIMAL_ORDER), COLOR_BAR_SIZE_UNIT);
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_LENGTH, 
				SGUtility.getExportValue(this.getBarLength(COLOR_BAR_SIZE_UNIT), 
						COLOR_BAR_SIZE_MINIMAL_ORDER), COLOR_BAR_SIZE_UNIT);
		SGPropertyUtility.addProperty(map, COM_COLOR_BAR_DIRECTION, this.getDirection());

    	return map;
    }
	
    /**
     * Returns a text string of the commands.
     * Overrode to append properties for the color maps.
     * 
     * @return a text string of the commands
     */
	public String getCommandString() {
		StringBuffer sb = new StringBuffer();

		// creates the command for this axis
		String axisCommands = SGCommandUtility.createCommandString(COM_COLOR_BAR, 
				null, this.getPropertyMap());
		sb.append(axisCommands);

		/*
		Iterator<Entry<String, SGColorMap>> colorMapItr = this.mColorMapManager.getColorMaps().entrySet().iterator();
		while (colorMapItr.hasNext()) {
			StringBuffer sbTmp = new StringBuffer();
			Entry<String, SGColorMap> entry = colorMapItr.next();
			String colorMapName = entry.getKey();
			SGColorMap colorMap = entry.getValue();
			SGPropertyMap pMap = colorMap.getPropertyMap();
			sbTmp.append(COM_COLOR_BAR);
			sbTmp.append('(');
			sbTmp.append(colorMapName);
			sbTmp.append(", ");
			sbTmp.append(SGCommandUtility.createCommandString(pMap));
			sbTmp.append(")\n");
			sb.append(sbTmp.toString());
		}
		*/
		
		String colorMapName = this.getColorBarStyle();
		SGColorMap colorMap = this.mColorMapManager.getColorMap(colorMapName);
		SGPropertyMap pMap = colorMap.getPropertyMap();
		sb.append(COM_COLOR_BAR);
		sb.append('(');
		sb.append(colorMapName);
		sb.append(", ");
		sb.append(pMap.toString());
		sb.append(")\n");
		
		return sb.toString();
	}
	
    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = super.getPropertyFileMap(params);

		SGPropertyUtility.addProperty(map, KEY_VISIBLE, 
				Boolean.toString(this.mVisible));

    	// axis
		SGPropertyUtility.addProperty(map, KEY_X_AXIS_POSITION, 
				this.mAxisElement.getLocationName(this.mXAxis));
		SGPropertyUtility.addProperty(map, KEY_Y_AXIS_POSITION, 
				this.mAxisElement.getLocationName(this.mYAxis));

		// location
		SGPropertyUtility.addProperty(map, KEY_X_VALUE, this.getXValue());
		SGPropertyUtility.addProperty(map, KEY_Y_VALUE, this.getYValue());
    	
		// layout
		SGPropertyUtility.addProperty(map, KEY_X_VALUE, 
				Double.toString(this.getXValue()));
		SGPropertyUtility.addProperty(map, KEY_Y_VALUE, 
				Double.toString(this.getYValue()));
		SGPropertyUtility.addProperty(map, KEY_COLOR_BAR_WIDTH,
				SGUtility.getExportValue(this.mBarWidth * CM_POINT_RATIO, 
						COLOR_BAR_SIZE_MINIMAL_ORDER), COLOR_BAR_SIZE_UNIT);
		SGPropertyUtility.addProperty(map, KEY_COLOR_BAR_LENGTH, 
				SGUtility.getExportValue(this.mBarLength * CM_POINT_RATIO,
						COLOR_BAR_SIZE_MINIMAL_ORDER), COLOR_BAR_SIZE_UNIT);
		SGPropertyUtility.addProperty(map, KEY_COLOR_BAR_DIRECTION, 
				this.getDirection());

		// style
		SGPropertyUtility.addProperty(map, KEY_COLOR_BAR_STYLE, 
				this.getColorBarStyle());
		
    	return map;
    }

	@Override
	protected String getLineVisiblePropertyFileKey() {
		return KEY_FRAME_LINE_VISIBLE;
	}

	@Override
	protected String getLineVisibleCommandKey() {
		return COM_COLOR_BAR_FRAME_LINE_VISIBLE;
	}

	@Override
	public boolean getAxisDateMode(final int location) {
		SGAxis axis = this.mAxisElement.getAxisInPlane(location);
		return axis.getDateMode();
	}
}

/**
 * Line for the tick marks in the color bar.
 */
class ElementLineOfColorBarScale extends ElementLineTickMark {
	
	ElementLineOfColorBarScale(final SGTuple2f start, final SGTuple2f end, SGAxisElement axisElement) {
        super(start, end, axisElement);
    }

	ElementLineOfColorBarScale(final ElementLineTickMark el) {
        super(el);
    }

    /**
     * Overrode to return the line color of the color bar.
     * 
     * @return the line color of the color bar
     */
    public Color getColor() {
        return this.mAxisElement.getTickMarkColor();
    }

    /**
     * Overrode to return the stroke for the tick marks in the color bar.
     * 
     * @return the stroke for the tick marks in the color bar
     */
    public SGStroke getStroke() {
    	return this.mAxisElement.getTickMarkStroke();
    }
}

/**
 * Properties for the color bar.
 *
 */
class ColorBarProperties extends AxisProperties {
    
    SGAxis xAxis = null;

    SGAxis yAxis = null;

    float x;
    
    float y;
    
    float barWidth;
    
    float barLength;
    
    String direction;
    
    float frameLineWidth;

    String colorBarStyle;

    Map<String, ColorMapProperties> colorMapPropertiesMap = new HashMap<String, ColorMapProperties>();

    /**
     * Dispose this object.
     */
    @Override
    public void dispose() {
        this.xAxis = null;
        this.yAxis = null;
        this.colorBarStyle = null;
        this.colorMapPropertiesMap.clear();
        this.colorMapPropertiesMap = null;
        super.dispose();
    }

    /**
     * Copy this object.
     * @return a copied object
     */
    public Object copy() {
        Object obj = super.copy();
        ColorBarProperties p = (ColorBarProperties) obj;
        Map<String, ColorMapProperties> cpMap = new HashMap<String, ColorMapProperties>();
        Iterator<Entry<String, ColorMapProperties>> itr = this.colorMapPropertiesMap.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<String, ColorMapProperties> entry = itr.next();
        	String name = entry.getKey();
        	ColorMapProperties cp = entry.getValue();
        	cpMap.put(name, (ColorMapProperties) cp.copy());
        }
        p.colorMapPropertiesMap = cpMap;
        return p;
    }

    @Override
    public boolean equals(final Object obj) {
        if ((obj instanceof ColorBarProperties) == false) {
            return false;
        }
        if (super.equals(obj) == false) {
            return false;
        }
        ColorBarProperties p = (ColorBarProperties) obj;
        if (this.xAxis != null) {
            if (this.xAxis.equals(p.xAxis) == false) {
                return false;
            }
        } else {
            if (p.xAxis != null) {
                return false;
            }
        }
        if (this.yAxis != null) {
            if (this.yAxis.equals(p.yAxis) == false) {
                return false;
            }
        } else {
            if (p.yAxis != null) {
                return false;
            }
        }
        if (this.x != p.x) {
            return false;
        }
        if (this.y != p.y) {
            return false;
        }
        if (this.barWidth != p.barWidth) {
            return false;
        }
        if (this.barLength != p.barLength) {
            return false;
        }
        if (this.spaceLineAndNumbers != p.spaceLineAndNumbers) {
            return false;
        }
        if (this.spaceTitleAndNumbers != p.spaceTitleAndNumbers) {
            return false;
        }
        if (!SGUtility.equals(this.direction, p.direction)) {
            return false;
        }
        if (!SGUtility.equals(this.colorBarStyle, p.colorBarStyle)) {
        	return false;
        }
        if (this.frameLineWidth != p.frameLineWidth) {
            return false;
        }
        if (this.tickMarkWidth != p.tickMarkWidth) {
            return false;
        }
        if (this.tickMarkLength != p.tickMarkLength) {
            return false;
        }
        if (!this.colorMapPropertiesMap.equals(p.colorMapPropertiesMap)) {
        	return false;
        }
        return true;
    }
}
