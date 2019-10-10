package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Area;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureConstants;
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
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureGridConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;

import org.joda.time.Period;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * An object to draw grid lines.
 */
public class SGFigureElementGrid extends SGFigureElement2D implements
        SGIFigureElementGrid, SGILineConstants, SGIPropertyDialogObserver,
        SGIDrawingElementConstants, SGIFigureGridConstants, SGIFigureConstants {

    /**
     * An SGIAxisElement object.
     */
    private SGIFigureElementAxis mAxisElement;

    /**
     * X-axis.
     */
    private SGAxis mXAxis;

    /**
     * Y-axis.
     */
    private SGAxis mYAxis;

    /**
     * The baseline value for X-axis.
     */
    private SGAxisValue mBaselineValueX = null;

    /**
     * The step value for X-axis.
     */
    private SGAxisStepValue mStepValueX = null;

    /**
     * The baseline value for Y-axis.
     */
    private SGAxisValue mBaselineValueY = null;

    /**
     * The step value for Y-axis.
     */
    private SGAxisStepValue mStepValueY = null;

    /**
     * Visible flag.
     */
    private boolean mVisibleFlag;

    /**
     * A flag whether to set location of grid lines automatically.
     */
    private boolean mAutoRangeFlag = true;

    /**
     * Line width.
     */
    private float mLineWidth;

    /**
     * Line type.
     */
    private int mLineType;

    /**
     * Line color.
     */
    private Color mColor;

    /**
     * Set of grid lines.
     */
    private Set<Shape> mLineSet = new HashSet<Shape>();

    /**
     * 
     */
    protected SGProperties mTemporaryProperties = null;

    /**
     * Default constructor.
     */
    public SGFigureElementGrid() {
        super();
        this.init();
    }

    private void init() {
        this.setGridVisible(DEFAULT_GRID_VISIBLE);
        this.setAutoRangeFlag(DEFAULT_GRID_AUTO_CALC);
        this.setLineWidth(DEFAULT_GRID_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setLineType(DEFAULT_GRID_LINE_TYPE);
        this.setColor(DEFAULT_GRID_COLOR);
    }

    public void dispose() {
        super.dispose();

        this.mColor = null;
        this.mXAxis = null;
        this.mYAxis = null;
        this.mAxisElement = null;
        this.mLineSet.clear();
        this.mLineSet = null;
        this.mPressedPoint = null;
        this.mTemporaryProperties = null;
    }

    /**
     * 
     * @return
     */
    public String getClassDescription() {
        return null;
    }

    /**
     * Set the SGIAxisElement object.
     * 
     * @param el -
     *            an SGIAxisElement object
     */
    public void setAxisElement(SGIFigureElementAxis el) {
        this.mAxisElement = el;
        if (this.mXAxis == null) {
            this.mXAxis = el.getAxis(DEFAULT_GRID_HORIZONTAL_AXIS);
        }
        if (this.mYAxis == null) {
            this.mYAxis = el.getAxis(DEFAULT_GRID_VERTICAL_AXIS);
        }
    }

    /**
     * 
     */
    public boolean addData(SGData data, String name) {
        if (super.addData(data, name) == false) {
            return false;
        }

        SGIFigureElementAxis el = this.mAxisElement;
        if (this.mXAxis == null) {
            this.mXAxis = el.getAxis(DEFAULT_GRID_HORIZONTAL_AXIS);
        }
        if (this.mYAxis == null) {
            this.mYAxis = el.getAxis(DEFAULT_GRID_VERTICAL_AXIS);
        }
        this.createAll();

        return true;
    }

    /**
     * 
     */
    public boolean setGraphRect(final float x, final float y,
            final float width, final float height) {
        super.setGraphRect(x, y, width, height);
        this.createAll();
        return true;
    }

    /**
     * 
     */
    public void paintGraphics(Graphics g, boolean clip) {
        if (!this.isGridVisible()) {
            return;
        }

        final Graphics2D g2d = (Graphics2D) g;

        // set properties
        final float width = this.mMagnification * this.mLineWidth;
        g2d.setPaint(this.getColor());

        // set stroke
        Stroke stroke = SGUtilityForFigureElementJava2D.getBasicStroke(this.mLineType, width,
                BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 1.0f, 0.0f);
        g2d.setStroke(stroke);

        // clip the rectangle
        if (clip) {
            SGUtilityForFigureElementJava2D.clipGraphRect(this, g2d);
        }

        // draw lines
        Set<Shape> lineSet = Collections.synchronizedSet(this.mLineSet);
        synchronized(lineSet) {
            Iterator<Shape> itr = lineSet.iterator();
            if (clip) {
                while (itr.hasNext()) {
                    Shape sh = (Shape) itr.next();
                    g2d.draw(sh);
                }
            } else {
                Area clipArea = new Area(this.getGraphRect());
                while (itr.hasNext()) {
                    Shape sh = (Shape) itr.next();
                    Area shArea = new Area(stroke.createStrokedShape(sh));
                    shArea.intersect(clipArea);
                    g2d.fill(shArea);
                }
            }
        }

        if (clip) {
            g2d.setClip(this.getViewBounds());
        }
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
            if (SGIFigureElement.NOTIFY_CHANGE.equals(msg)
    				|| SGIFigureElement.NOTIFY_CHANGE_ON_COMMIT.equals(msg)) {
            	SGProperties pTemp = this.getProperties();
            	if (this.mTemporaryProperties == null) {
            		this.mTemporaryProperties = pTemp;
            	}
            	
                // converts the scale values
            	final boolean dateX = this.mXAxis.getDateMode();
            	this.mBaselineValueX = this.convertAxisValue(dateX, this.mBaselineValueX);
            	this.mStepValueX = this.convertAxisStepValue(dateX, this.mStepValueX);
            	final boolean dateY = this.mYAxis.getDateMode();
            	this.mBaselineValueY = this.convertAxisValue(dateY, this.mBaselineValueY);
            	this.mStepValueY = this.convertAxisStepValue(dateY, this.mStepValueY);

                if (SGIFigureElement.NOTIFY_CHANGE_ON_COMMIT.equals(msg)) {
    	        	SGProperties p = this.getProperties();
    	        	if (!p.equals(pTemp)) {
    	        		// notifies the change
    	        		this.notifyChange();
    	        	}
                }
                
            } else if (SGIFigureElement.NOTIFY_CHANGE_ON_CANCEL.equals(msg)) {
            	if (this.mTemporaryProperties != null) {
                	this.setProperties(this.mTemporaryProperties);
                	this.mTemporaryProperties = null;
            	}
            }
            
        	// creates all grid lines
            this.createAll();
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
    
    private SGAxisValue convertAxisValue(final boolean toDateMode,
    		final SGAxisValue value) {
    	SGAxisValue ret = value;
    	if (toDateMode) {
			ret = new SGAxisDateValue(value.getValue());
    	} else {
			ret = new SGAxisDoubleValue(value.getValue());
    	}
    	return ret;
    }

    private SGAxisStepValue convertAxisStepValue(final boolean toDateMode,
    		final SGAxisStepValue value) {
    	SGAxisStepValue ret = value;
    	if (toDateMode) {
    		if (value instanceof SGAxisDoubleStepValue) {
    			SGAxisDoubleStepValue dValue = (SGAxisDoubleStepValue) value;
    			Period p = SGDateUtility.toPeriodOfDays(dValue.getValue());
    			ret = new SGAxisDateStepValue(p);
    		}
    	} else {
    		if (value instanceof SGAxisDateStepValue) {
    			SGAxisDateStepValue dValue = (SGAxisDateStepValue) value;
    			Period p = dValue.getPeriod();
    			final double dateValue = SGDateUtility.toApproximateDateValue(p);
    			ret = new SGAxisDoubleStepValue(dateValue);
    		}
    	}
    	return ret;
    }

    /**
     * Synchronize the element given by the argument.
     * 
     * @param element
     *            An object to be synchronized.
     */
    public boolean synchronizeArgument(SGIFigureElement element, String msg) {
        // this shouldn't happen
        throw new Error();
    }

    /**
     * Creates all grid lines.
     * 
     */
    private void createAll() {
	
        this.mLineSet.clear();
        
        // create line objects for X and Y axes
        if (this.mXAxis != null) {
            Set<Line2D> s = this.create(this.mXAxis, true);
            this.mLineSet.addAll(s);
        }
        if (this.mYAxis != null) {
            Set<Line2D> s = this.create(this.mYAxis, false);
            this.mLineSet.addAll(s);
        }
    }

    /**
     * Creates grid lines for given axis.
     * 
     * @param axis
     *           the axis
     * @param horizontal
     *           true if the axis is horizontal
     * @return the set of grid lines
     */
    private Set<Line2D> create(final SGAxis axis, final boolean horizontal) {
	
        final int type = axis.getScaleType();
        float[] array = null;
        if (type == SGAxis.LINEAR_SCALE) {
            array = this.calcLocationLinear(axis, horizontal);
        } else if (type == SGAxis.LOG_SCALE) {
            array = this.calcLocationLog(axis, horizontal);
        } else {
            throw new Error();
        }

        final int size = array.length;

        // create drawing elements
        final float gx = this.getGraphRectX();
        final float gy = this.getGraphRectY();
        final float gw = this.getGraphRectWidth();
        final float gh = this.getGraphRectHeight();
        Set<Line2D> lineSet = new HashSet<Line2D>();
        if (horizontal) {
            for (int ii = 0; ii < size; ii++) {
                final float x = array[ii];
                Line2D line = new Line2D.Float(x, gy, x, gy + gh);
                lineSet.add(line);
            }
        } else {
            for (int ii = 0; ii < size; ii++) {
                final float y = array[ii];
                Line2D line = new Line2D.Float(gx, y, gx + gw, y);
                lineSet.add(line);
            }
        }

        return lineSet;
    }

    /**
     * Calculates the locations of grid lines for the linear scale.
     * @param axis
     *               the axis
     * @param horizontal
     *               true for horizontal
     * @return an array of locations for given axis
     */
    private float[] calcLocationLinear(final SGAxis axis, final boolean horizontal) {
    	
        final SGAxisValue baseline;
        final SGAxisStepValue step;
        if (this.mAutoRangeFlag) {
            baseline = this.calcBaselineValue(axis);
            step = this.calcStepValue(axis);
        } else {
            if (horizontal) {
                step = this.mStepValueX;
                baseline = this.mBaselineValueX;
            } else {
                step = this.mStepValueY;
                baseline = this.mBaselineValueY;
            }
        }
        
        // set to the attributes
        if (horizontal) {
            this.mStepValueX = step;
            this.mBaselineValueX = baseline;
        } else {
            this.mStepValueY = step;
            this.mBaselineValueY = baseline;
        }

        // calculate axis values
        Set<SGAxisValue> set = this.calcAxisValues(axis, baseline, step);
        
        // calculate the location
        final int size = set.size();
        float[] array = new float[size];
        Iterator<SGAxisValue> itr = set.iterator();
        int cnt = 0;
        while (itr.hasNext()) {
            final SGAxisValue num = itr.next();
            array[cnt] = this.calcLocation(num.getValue(), axis, horizontal);
            cnt++;
        }

        return array;
    }

    /**
     * Calculates the locations of grid lines for the log scale.
     * 
     * @param axis
     *               the axis
     * @param horizontal
     *               true for horizontal
     * @return an array of locations for given axis
     */
    private float[] calcLocationLog(final SGAxis axis, final boolean horizontal) {

        // minimum and maximum values of axis range
        final double min = axis.getRange().x;
        final double max = axis.getRange().y;
        final int minOrder = SGUtilityNumber.getOrder(min);
        final int maxOrder = SGUtilityNumber.getOrder(max);

        // a list of exponents
        List<Integer> expList = new ArrayList<Integer>();
        for (int ii = minOrder + 1; ii <= maxOrder; ii++) {
            expList.add(ii);
        }
        
        // a list of axis values
        List<Double> valueList = new ArrayList<Double>();
        final int expLen = expList.size();
        for (int ii = 0; ii < expLen; ii++) {
            final int num = expList.get(ii);
            final double value = SGUtilityNumber.getPowersOfTen(num);
            valueList.add(value);
            
            // values between 10^n and 10^(n+1)
            for (int jj = 2; jj <= 9; jj++) {
                double v = jj * value;
                if (v > max) {
                    break;
                }
                valueList.add(jj * value);
            }
            
            // values between 10^(n-1) and 10^n for the smallest n
            // in the axis range
            if (ii == 0) {
                for (int jj = 9; jj >= 2; jj--) {
                    double v = jj * value / 10.0;
                    if (v < min) {
                        break;
                    }
                    valueList.add(v);
                }
            }
        }
        
        // an array of axis values
        final int valueLen = valueList.size();
        double[] valueArray = new double[valueLen];
        for (int ii = 0; ii < valueLen; ii++) {
            valueArray[ii] = valueList.get(ii);
        }

        // an array of locations
        final float[] locationArray = new float[valueLen];
        for (int ii = 0; ii < valueLen; ii++) {
            locationArray[ii] = this.calcLocation(valueArray[ii], axis,
                    horizontal);
        }

        return locationArray;
    }

    /**
     * Only returns true.
     * 
     * @param list
     *            a list to be added the focused objects
     */
    public boolean getFocusedObjectsList(ArrayList list) {
        return true;
    }

    @Override
    public SGProperties getProperties() {
        GridProperties p = new GridProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    public boolean getProperties(SGProperties p) {
        if ((p instanceof GridProperties) == false) {
            return false;
        }
        GridProperties gp = (GridProperties) p;
        gp.mXAxisLocation = this.mAxisElement.getLocationInPlane(this.mXAxis);
        gp.mYAxisLocation = this.mAxisElement.getLocationInPlane(this.mYAxis);
        gp.mVisibleFlag = this.mVisibleFlag;
        gp.mBaselineValueX = this.mBaselineValueX;
        gp.mBaselineValueY = this.mBaselineValueY;
        gp.mStepValueX = this.mStepValueX;
        gp.mStepValueY = this.mStepValueY;
        gp.mAutoRangeFlag = this.mAutoRangeFlag;
        gp.mLineWidth = this.mLineWidth;
        gp.mLineType = this.mLineType;
        gp.mColor = this.mColor;
        return true;
    }

    @Override
    public boolean setProperties(SGProperties p) {
        if ((p instanceof GridProperties) == false) {
            return false;
        }
        GridProperties gp = (GridProperties) p;
        this.mXAxis = this.mAxisElement.getAxisInPlane(gp.mXAxisLocation);
        this.mYAxis = this.mAxisElement.getAxisInPlane(gp.mYAxisLocation);
        this.mVisibleFlag = gp.mVisibleFlag;
        this.mBaselineValueX = gp.mBaselineValueX;
        this.mBaselineValueY = gp.mBaselineValueY;
        this.mStepValueX = gp.mStepValueX;
        this.mStepValueY = gp.mStepValueY;
        this.mAutoRangeFlag = gp.mAutoRangeFlag;
        this.mLineWidth = gp.mLineWidth;
        this.mLineType = gp.mLineType;
        this.mColor = gp.mColor;
        return true;
    }

    /**
     * Only returns true.
     * 
     * @param s -
     *            a selectable object to be hidden
     */
    public boolean hideSelectedObject(SGISelectable s) {
        return true;
    }

    /**
     * 
     */
    public String getTagName() {
        return TAG_NAME_GRID_ELEMENT;
    }

    /**
     * 
     * @param e
     */
    public boolean onMouseClicked(MouseEvent e) {
        return false;
    }

    /**
     * 
     * @param e
     */
    public boolean onMousePressed(MouseEvent e) {
        return false;
    }

    /**
     * 
     * @param e
     */
    public boolean onMouseDragged(MouseEvent e) {
        return true;
    }

    /**
     * 
     * @param e
     */
    public boolean onMouseReleased(MouseEvent e) {
        return true;
    }

    /**
     * 
     */
    public boolean setMouseCursor(int x, int y) {
        return false;
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

        return new Element[] { el };
    }

    /*
     * Initialize compatible properties for previous property file. Grid Element
     * is appeared since version 0.5.1
     * 
     * @see jp.riken.brain.ni.samuraigraph.base.SGIFigureElement#initCompatibleProperty()
     */
    public boolean initCompatibleProperty() {
        // KEY_GRID_VISIBLE
        this.setGridVisible(DEFAULT_GRID_VISIBLE);
        // KEY_AUTO_CALC
        this.setAutoRangeFlag(DEFAULT_GRID_AUTO_CALC);
        // KEY_X_AXIS_POSITION
        SGAxis xAxis = this.mAxisElement.getAxis(DEFAULT_GRID_HORIZONTAL_AXIS);
        if (xAxis == null)
            return false;
        if (this.setXAxis(xAxis) == false)
            return false;
        // KEY_X_AXIS_POSITION
        SGAxis yAxis = this.mAxisElement
                .getAxis(DEFAULT_GRID_VERTICAL_AXIS);
        if (yAxis == null)
            return false;
        if (this.setYAxis(yAxis) == false)
            return false;
        // KEY_STEP_VALUE_X
        this.mStepValueX = new SGAxisDoubleStepValue(DEFAULT_GRID_STEP_VALUE_X);
        // KEY_BASELINE_VALUE_X
        this.mBaselineValueX = new SGAxisDoubleValue(DEFAULT_GRID_BASELINE_VALUE_X);
        // KEY_STEP_VALUE_Y
        this.mStepValueY = new SGAxisDoubleStepValue(DEFAULT_GRID_STEP_VALUE_Y);
        // KEY_BASELINE_VALUE_Y
        this.mBaselineValueY = new SGAxisDoubleValue(DEFAULT_GRID_BASELINE_VALUE_Y);
        // KEY_LINE_WIDTH
        this.setLineWidth(DEFAULT_GRID_LINE_WIDTH, LINE_WIDTH_UNIT);
        // KEY_LINE_TYPE
        this.setLineType(DEFAULT_GRID_LINE_TYPE);
        // KEY_GRID_COLOR
        this.setColor(DEFAULT_GRID_COLOR);
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
        String str = null;
        Number num = null;
        Color cl = null;
        Boolean b = null;

        // grid visible
        str = element.getAttribute(SGIFigureElementGrid.KEY_GRID_VISIBLE);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean gridVisible = b.booleanValue();
            if (this.setGridVisible(gridVisible) == false) {
                return false;
            }
        }

        // set axes
        str = element.getAttribute(KEY_X_AXIS_POSITION);
        if (str.length() != 0) {
            SGAxis xAxis = this.mAxisElement.getAxis(str);
            if (xAxis == null) {
                return false;
            }
            if (this.setXAxis(xAxis) == false) {
                return false;
            }
        }

        str = element.getAttribute(KEY_Y_AXIS_POSITION);
        if (str.length() != 0) {
            SGAxis yAxis = this.mAxisElement.getAxis(str);
            if (yAxis == null) {
                return false;
            }
            if (this.setYAxis(yAxis) == false) {
                return false;
            }
        }

        // auto calc
        str = element.getAttribute(SGIFigureElementGrid.KEY_AUTO_CALC);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            final boolean autoCalc = b.booleanValue();
            if (this.setAutoRangeFlag(autoCalc) == false) {
                return false;
            }
        }

        // line width
        str = element.getAttribute(KEY_LINE_WIDTH);
        if (str != null) {
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
        str = element.getAttribute(KEY_LINE_TYPE);
        if (str != null) {
            num = SGDrawingElementLine.getLineTypeFromName(str);
            if (num == null) {
                return false;
            }
            final int lineType = num.intValue();
            if (this.setLineType(lineType) == false) {
                return false;
            }
        }

        // grid color
        str = element.getAttribute(KEY_GRID_COLOR);
        if (str.length() != 0) {
            cl = SGUtilityText.parseColor(str);
            if (cl == null) {
                return false;
            }
            final Color gridColor = cl;
            if (this.setColor(gridColor) == false) {
                return false;
            }
        }

        //
        // baseline and step values
        //
        
        // for x-axis
        str = element.getAttribute(KEY_BASELINE_VALUE_X);
        if (str.length() != 0) {
        	SGAxisValue baseline = this.getBaselineValue(str);
            if (baseline != null) {
                if (this.setBaselineValueX(baseline) == false) {
                    return false;
                }
            }
        }

        str = element.getAttribute(KEY_STEP_VALUE_X);
        if (str.length() != 0) {
        	SGAxisStepValue step = this.getStepValue(str);
        	if (step != null) {
                if (this.setStepValueX(step) == false) {
                    return false;
                }
        	}
        }

        // for y-axis
        str = element.getAttribute(KEY_BASELINE_VALUE_Y);
        if (str.length() != 0) {
        	SGAxisValue baseline = this.getBaselineValue(str);
            if (baseline != null) {
                if (this.setBaselineValueY(baseline) == false) {
                    return false;
                }
            }
        }

        str = element.getAttribute(KEY_STEP_VALUE_Y);
        if (str.length() != 0) {
        	SGAxisStepValue step = this.getStepValue(str);
        	if (step != null) {
                if (this.setStepValueY(step) == false) {
                    return false;
                }
        	}
        }

        return true;
    }
    
    private SGAxisValue getBaselineValue(String str) {
    	SGAxisValue baseline = null;
        Number num = SGUtilityText.getDouble(str);
        if (num != null) {
        	baseline = new SGAxisDoubleValue(num.doubleValue());
        } else {
        	SGDate date = SGUtilityText.getDate(str);
        	if (date != null) {
        		baseline = new SGAxisDateValue(date);
        	}
        }
        return baseline;
    }

    private SGAxisStepValue getStepValue(String str) {
    	SGAxisStepValue step = null;
        Number num = SGUtilityText.getDouble(str);
        if (num != null) {
        	step = new SGAxisDoubleStepValue(num.doubleValue());
        } else {
        	Period p = SGUtilityText.getPeriod(str);
        	if (p != null) {
        		step = new SGAxisDateStepValue(p);
        	}
        }
        return step;
    }
    
    /**
     * Updates changed flag of focused objects.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean updateChangedFlag() {
    	// do nothing
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
     * 
     */
    public boolean commit() {
        SGProperties pTemp = this.mTemporaryProperties;
        SGProperties pPresent = this.getProperties();
        if (pTemp.equals(pPresent) == false) {
            this.setChanged(true);
        }
        this.mTemporaryProperties = null;
        this.createAll();
        this.notifyChangeOnCommit();
        this.repaint();
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
        this.createAll();
        this.notifyChangeOnCancel();
        this.repaint();
        return true;
    }

    /**
     * 
     */
    public boolean preview() {
        this.createAll();
        this.notifyChange();
        this.repaint();
        return true;
    }

    /**
     * Returns a list of child nodes.
     * 
     * @return a list of chid nodes
     */
    public ArrayList getChildNodes() {
        return new ArrayList();
    }

    /**
     * 
     */
    public SGPropertyDialog getPropertyDialog() {
        return null;
    }

    /**
     * 
     * @return
     */
    public boolean showPropertyDialog() {
        return true;
    }

    /**
     * 
     */
    public boolean setMementoBackward() {
        if (!super.setMementoBackward()) {
            return false;
        }
        this.createAll();
        this.notifyChangeOnUndo();
        return true;
    }

    /**
     * 
     */
    public boolean setMementoForward() {
        if (!super.setMementoForward()) {
            return false;
        }
        this.createAll();
        this.notifyChangeOnUndo();
        return true;
    }

    /**
     * Returns the baseline value for x-axis.
     * 
     * @return baseline value
     */
    public SGAxisValue getBaselineValueX() {
        return this.mBaselineValueX;
    }

    /**
     * Returns the baseline value for y-axis.
     * 
     * @return baseline value
     */
    public SGAxisValue getBaselineValueY() {
        return this.mBaselineValueY;
    }

    /**
     * Returns the color of grid.
     * 
     * @return color of grid
     */
    public Color getColor() {
        return this.mColor;
    }

    /**
     * Returns the line type.
     * 
     * @return line type
     */
    public int getLineType() {
        return this.mLineType;
    }

    /**
     * Returns the line width.
     * 
     * @return line width
     */
    public float getLineWidth() {
        return this.mLineWidth;
    }

    /**
     * 
     */
    public float getLineWidth(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getLineWidth(), unit);
    }

    /**
     * Returns the step value for x-axis.
     * 
     * @return step value
     */
    public SGAxisStepValue getStepValueX() {
        return this.mStepValueX;
    }

    /**
     * Returns the step value for y-axis.
     * 
     * @return step value
     */
    public SGAxisStepValue getStepValueY() {
        return this.mStepValueY;
    }

    /**
     * Sets the x-axis.
     * 
     * @param axis
     *           an axis to set
     * @return true if succeeded
     */
    public boolean setXAxis(final SGAxis axis) {
        this.mXAxis = axis;
        this.createAll();
        return true;
    }

    /**
     * Sets the y-axis.
     * 
     * @param axis
     *           an axis to set
     * @return true if succeeded
     */
    public boolean setYAxis(final SGAxis axis) {
        this.mYAxis = axis;
        this.createAll();
        return true;
    }

    /**
     * Returns the x-axis.
     * 
     * @return x-axis
     */
    public SGAxis getXAxis() {
        return this.mXAxis;
    }

    /**
     * Returns the y-axis.
     * 
     * @return y-axis
     */
    public SGAxis getYAxis() {
        return this.mYAxis;
    }

    /**
     * Sets the baseline value for the x-axis.
     * 
     * @param value
     *         the baseline value to set
     * @return true if succeeded
     */
    public boolean setBaselineValueX(final SGAxisValue value) {
        this.mBaselineValueX = value;
        this.createAll();
        return true;
    }

    /**
     * Sets the baseline value for the y-axis.
     * 
     * @param value
     *         the baseline value to set
     * @return true if succeeded
     */
    public boolean setBaselineValueY(final SGAxisValue value) {
        this.mBaselineValueY = value;
        this.createAll();
        return true;
    }

    /**
     * Sets the line color.
     * 
     * @param color
     *          the color to set
     * @return true if succeeded
     */
    public boolean setColor(final Color color) {
        this.mColor = color;
        return true;
    }

    /**
     * Sets the line type
     * 
     * @param type
     *           line type to set
     * @return true if succeeded
     */
    public boolean setLineType(final int type) {
        if (SGDrawingElementLine.isValidLineType(type) == false) {
            return false;
        }
        this.mLineType = type;
        return true;
    }

    /**
     * Sets the line width.
     * 
     * @param lw
     *          line width to set
     * @return true if succeeded
     */
    public boolean setLineWidth(final float lw) {
        this.mLineWidth = lw;
        return true;
    }

    /**
     * Sets the line width in a given unit.
     * 
     * @param lw
     *          line width to set
     * @param unit
     *          the unit of line width
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

    /**
     * Sets the step value for the x-axis.
     * 
     * @param value
     *          a value to set to the step value for the x-axis
     * @return true if succeeded
     */
    public boolean setStepValueX(final SGAxisStepValue value) {
        this.mStepValueX = value;
        this.createAll();
        return true;
    }

    /**
     * Sets the step value for the y-axis.
     * 
     * @param value
     *          a value to set to the step value for the y-axis
     * @return true if succeeded
     */
    public boolean setStepValueY(final SGAxisStepValue value) {
        this.mStepValueY = value;
        this.createAll();
        return true;
    }

    /**
     * Returns whether the grid is visible.
     * 
     * @return visible flag
     */
    public boolean isGridVisible() {
        return this.mVisibleFlag;
    }

    /**
     * Sets the visibility of the grid lines.
     * 
     * @param b
     *          a flag for visibility
     * @return true if succeeded
     */
    public boolean setGridVisible(final boolean b) {
        this.mVisibleFlag = b;
        this.createAll();
        return true;
    }

    /**
     * Returns whether the range of lines is determined automatically.
     * 
     * @return auto-range flag
     */
    public boolean isAutoRange() {
        return this.mAutoRangeFlag;
    }

    /**
     * Sets whether to the range of lines is determined automatically.
     * 
     * @param b
     *          a flag to set
     * @return true if succeeded
     */
    public boolean setAutoRangeFlag(final boolean b) {
        this.mAutoRangeFlag = b;
        return true;
    }

    /**
     * Returns the location of the x-axis.
     * 
     * @return the location of the x-axis.
     */
    public int getXAxisLocation() {
        return this.mAxisElement.getLocationInPlane(this.mXAxis);
    }

    /**
     * Returns the location of the y-axis.
     * 
     * @return the location of the y-axis.
     */
    public int getYAxisLocation() {
        return this.mAxisElement.getLocationInPlane(this.mYAxis);
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
        SGAxis axis = this.getAxis(location);
        if (axis == null) {
            return false;
        }
        if (this.setXAxis(axis) == false) {
            return false;
        }
        return true;
    }

    /**
     * Sets the location of the x-axis.
     * 
     * @param location
     *           the location of the x-axis
     * @return true if succeeded
     */
    public boolean setYAxisLocation(final int location) {
        if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
            return false;
        }
        SGAxis axis = this.getAxis(location);
        if (axis == null) {
            return false;
        }
        if (this.setYAxis(axis) == false) {
            return false;
        }
        return true;
    }

    // Returns an axis at given location.
    private SGAxis getAxis(final int location) {
        return this.mAxisElement.getAxisInPlane(location);
    }

    /**
     * 
     * @param step
     * @return
     */
    public boolean hasValidStepXValue(final SGAxisStepValue step) {
        final SGAxisStepValue v = (step != null) ? step : this.getStepValueX();
        if (v.isZero()) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @param step
     * @return
     */
    public boolean hasValidStepYValue(final SGAxisStepValue step) {
        final SGAxisStepValue v = (step != null) ? step : this.getStepValueY();
        if (v.isZero()) {
            return false;
        }
        return true;
    }

    /**
     * Properties of grid lines.
     * 
     */
    public static class GridProperties extends SGProperties {

    	int mXAxisLocation;

        int mYAxisLocation;

        SGAxisValue mBaselineValueX;

        SGAxisStepValue mStepValueX;

        SGAxisValue mBaselineValueY;

        SGAxisStepValue mStepValueY;

        boolean mVisibleFlag;

        boolean mAutoRangeFlag;

        float mLineWidth;

        int mLineType;

        Color mColor;

        public GridProperties() {
            super();
        }

        @Override
        public void dispose() {
        	super.dispose();
            this.mColor = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof GridProperties)) {
                return false;
            }
            GridProperties p = (GridProperties) obj;
            if (this.mXAxisLocation != p.mXAxisLocation) {
                return false;
            }
            if (this.mYAxisLocation != p.mYAxisLocation) {
                return false;
            }
            if (!SGUtility.equals(this.mBaselineValueX, p.mBaselineValueX)) {
                return false;
            }
            if (!SGUtility.equals(this.mStepValueX, p.mStepValueX)) {
                return false;
            }
            if (!SGUtility.equals(this.mBaselineValueY, p.mBaselineValueY)) {
                return false;
            }
            if (!SGUtility.equals(this.mStepValueY, p.mStepValueY)) {
                return false;
            }
            if (this.mVisibleFlag != p.mVisibleFlag) {
                return false;
            }
            if (this.mAutoRangeFlag != p.mAutoRangeFlag) {
                return false;
            }
            if (this.mLineWidth != p.mLineWidth) {
                return false;
            }
            if (this.mLineType != p.mLineType) {
                return false;
            }
            if (!SGUtility.equals(this.mColor, p.mColor)) {
                return false;
            }
            return true;
        }
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
    	// always returns an empty list
    	return new ArrayList<SGIPropertyDialogObserver>();
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
    	// always returns an empty list
    	return new ArrayList<SGIPropertyDialogObserver>();
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
    	// always returns null
    	return null;
    }

    /**
     * 
     */
    public boolean writeProperty(Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
        return true;
    }

	@Override
	public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
		SGPropertyMap map = new SGPropertyMap();
		this.addProperties(map, KEY_GRID_VISIBLE, KEY_X_AXIS_POSITION, KEY_Y_AXIS_POSITION, 
				KEY_AUTO_CALC, KEY_STEP_VALUE_X, KEY_STEP_VALUE_Y, KEY_BASELINE_VALUE_X, 
				KEY_BASELINE_VALUE_Y, KEY_LINE_WIDTH, KEY_LINE_TYPE, KEY_GRID_COLOR);
		return map;
	}

	@Override
	public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
		SGPropertyMap map = new SGPropertyMap();
		this.addProperties(map, COM_FIGURE_GRID_VISIBLE, COM_FIGURE_GRID_AXIS_X, 
				COM_FIGURE_GRID_AXIS_Y, COM_FIGURE_GRID_AUTO, COM_FIGURE_GRID_STEP_X, 
				COM_FIGURE_GRID_STEP_Y, COM_FIGURE_GRID_BASE_X, COM_FIGURE_GRID_BASE_Y,
				COM_FIGURE_GRID_LINE_WIDTH, COM_FIGURE_GRID_LINE_TYPE, COM_FIGURE_GRID_LINE_COLOR);
		return map;
	}

    private void addProperties(SGPropertyMap map, String gridVisibleKey,
    		String gridAxisXKey, String gridAxisYKey, String gridAutoKey, 
    		String gridStepXKey, String gridStepYKey, String gridBaseXKey,
    		String gridBaseYKey, String gridLineWidthKey, String gridLineTypeKey, 
    		String gridColorKey) {
    	SGPropertyUtility.addProperty(map, gridVisibleKey, 
    			this.isGridVisible());
    	SGPropertyUtility.addProperty(map, gridAxisXKey, 
    			SGUtility.getLocationName(this.getXAxisLocation()));
    	SGPropertyUtility.addProperty(map, gridAxisYKey, 
    			SGUtility.getLocationName(this.getYAxisLocation()));
    	SGPropertyUtility.addProperty(map, gridAutoKey, 
    			this.isAutoRange());
    	SGPropertyUtility.addProperty(map, gridStepXKey, 
    			this.getStepValueX());
    	SGPropertyUtility.addProperty(map, gridStepYKey, 
    			this.getStepValueY());
    	SGPropertyUtility.addProperty(map, gridBaseXKey, 
    			this.getBaselineValueX());
    	SGPropertyUtility.addProperty(map, gridBaseYKey, 
    			this.getBaselineValueY());
    	SGPropertyUtility.addProperty(map, gridLineWidthKey, 
    			SGUtility.getExportLineWidth(this.getLineWidth(LINE_WIDTH_UNIT)), 
    			LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, gridLineTypeKey, 
    			SGDrawingElementLine.getLineTypeName(this.getLineType()));
    	SGPropertyUtility.addProperty(map, gridColorKey, this.getColor());
    }

	@Override
	public boolean getAxisDateMode(final int location) {
		return this.mAxisElement.getAxisDateMode(location);
	}
}
