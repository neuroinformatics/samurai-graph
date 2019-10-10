package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDateValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisDoubleValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisStepValue;
import jp.riken.brain.ni.samuraigraph.base.SGAxisValue;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGDateUtility;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDisposable;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxisConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIMovable;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisDialogObserver;
import jp.riken.brain.ni.samuraigraph.figure.SGIStringConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGStroke;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGFigureElementAxis.AxisScaleElement;

import org.joda.time.Period;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class SGAxisElement implements ActionListener,
		SGIFigureElementAxisConstants, SGIAxisDialogObserver, SGIDisposable,
		SGIAxisConstants, SGIStringConstants, SGIUndoable, SGIMovable {

	/**
	 * The axis figure element.
	 */
	protected SGFigureElementAxis mAxisElement = null;
	
    /**
     * The location of axis in the plane.
     */
    protected int mLocationInPlane = -1;

    /**
     * The drawing element currently dragged.
     */
    protected SGDrawingElement mDraggingElement = null;

    /**
     * The flag whether this object is already disposed of.
     */
    protected boolean mDisposed = false;

    /**
     * The visibility flag for whole drawing elements of axis.
     */
    protected boolean mVisible = true;

    /**
     * A flag whether this object is selected.
     */
    protected boolean mSelectedFlag = false;

    //
    // Axis Line
    //

    /**
     * Line elements of axis.
     */
    protected ElementLineAxis[] mAxisLines = null;

    /**
     * The line stroke for the axis lines.
     */
    protected SGStroke mAxisLineStroke = new SGStroke();

	/**
	 * A flag for the visibility of the axis line.
	 */
	protected boolean mAxisLineVisible = true;

    /**
     * Color of axis lines.
     */
    protected Color mAxisLineColor;
    
    /**
     * Space between an axis line and scale numbers in the default unit.
     */
    protected float mSpaceAxisLineAndNumbers;

    //
    // Title
    //
    
    /**
     * A string element of the title.
     */
    protected ElementStringTitle mTitle;

    /**
     * Space between scale numbers and the title in the default unit.
     */
    protected float mSpaceTitleAndNumbers;

    /**
     * Shift of the title from the center in the default unit.
     */
    protected float mTitleCenterShift;
    
    //
    // Scale
    //

    /**
     * An axis object.
     */
    protected SGAxis mAxis = null;

    /**
     * The array of axis values.
     */
    protected double[] mAxisValueArray = null;

    /**
     * The flag for the automatic calculation.
     */
    protected boolean mAutoCalcFlag = true;

    /**
     * The baseline value.
     */
    protected SGAxisValue mBaselineValue = new SGAxisDoubleValue(0.0);

    /**
     * The step value.
     */
    protected SGAxisStepValue mStepValue = new SGAxisDoubleStepValue(1.0);

    /**
     * The flag whether scale numbers are set to integer.
     */
    protected boolean mNumberInteger = false;

    /**
     * The flag whether to use the exponent.
     */
    protected boolean mExponentVisible = false;

    /**
     * The exponent value.
     */
    protected int mExponentValue = 0;

    //
    // Number
    //
    
    /**
     * The list of scale numbers.
     */
    protected final List<ElementStringNumber> mNumberList = new ArrayList<ElementStringNumber>();

    /**
     * The visibility flag for scale numbers.
     */
    protected boolean mNumbersVisibleFlag = true;

    /**
     * Font name of scale.
     */
    protected String mNumberFontName;

    /**
     * Font size of the scale.
     */
    protected float mNumberFontSize;

    /**
     * Font size of the scale.
     */
    protected int mNumberFontStyle;

    /**
     * Color of the scale.
     */
    protected Color mNumberFontColor;
    
    /**
     * The angle for scale numbers.
     */
    protected float mNumberAngle = 0.0f;

    /**
     * The exponent symbol.
     */
    protected ElementStringExponent mExponentSymbol = null;
    
    /**
     * The x-coordinate of the exponent symbol in the default unit.
     */
    protected float mExponentLocationX = 0.0f;
    
    /**
     * The y-coordinate of the exponent symbol in the default unit.
     */
    protected float mExponentLocationY = 0.0f;
    
    /**
     * The format for date string.
     * Initializes with an empty string.
     */
    protected String mDateFormat = "";

    //
    // Tick Mark
    //

    /**
     * The list of scale lines.
     */
    protected final List<ElementLineTickMark> mTickMarksList = new ArrayList<ElementLineTickMark>();

    /**
     * The visibility flag for tick marks.
     */
    protected boolean mTickMarksVisibleFlag = true;

    /**
     * The line stroke for the tick marks.
     */
    protected SGStroke mTickMarkStroke = new SGStroke();
    
    /**
     * The color of tick marks.
     */
    protected Color mTickMarkColor = null;
    
    /**
     * Length of tick mark lines.
     */
    protected float mMajorTickMarkLength;
    
    /**
     * A flag whether to draw tick marks to both sides.
     */
    protected boolean mTickMarkBothsides;

    /**
     * The number of minor tick marks between two major tick marks.
     */
    protected int mMinorTickMarkNumber;

    /**
     * The length of a minor tick mark.
     */
    protected float mMinorTickMarkLength;
    
    // Scale
    protected SGAxisScale mPressedSymbol = null;
    
    /**
     * The undo manager.
     */
    protected SGUndoManager mUndoManager = new SGUndoManager(this);

    // temporary properties
    protected SGProperties mTemporaryProperties = null;

    List<AxisScaleChangeListener> mAxisScaleChangeListenerList = new ArrayList<AxisScaleChangeListener>();

    /**
     * Builds an axis.
     */
    protected SGAxisElement(SGFigureElementAxis axisElement) {
        super();
        
        this.mAxisElement = axisElement;
        this.mAxisLines = this.createAxisLines();
        this.mTitle = new ElementStringTitle(this);
        
        // axis line
        this.setAxisLineWidth(DEFAULT_AXIS_LINE_WIDTH, LINE_WIDTH_UNIT);
        this.setAxisLineColor(DEFAULT_LINE_COLOR);
		this.setSpaceAxisLineAndNumbers(DEFAULT_SPACE_AXIS_LINE_AND_NUMBER,
				SPACE_UNIT);

        // title
        this.setTitleVisible(DEFAULT_TITLE_VISIBLE);
		this.setSpaceTitleAndNumbers(DEFAULT_SPACE_NUMBER_AND_TITLE, SPACE_UNIT);
		this.setTitleShiftFromCenter(DEFAULT_TITLE_SHIFT_FROM_CENTER, TITLE_SHIFT_UNIT);
        this.setTitleFontName(DEFAULT_FONT_NAME);
		this.setTitleFontStyle(DEFAULT_FONT_STYLE);
		this.setTitleFontSize(DEFAULT_FONT_SIZE, FONT_SIZE_UNIT);
		this.setTitleFontColor(DEFAULT_FONT_COLOR);
		
		// scale
		this.setCalculateAutomatically(DEFAULT_AUTO_CALC);

        // number
        this.setNumbersVisible(DEFAULT_NUMBER_VISIBLE);
        this.setNumbersInteger(DEFAULT_NUMBER_INTEGER);
        this.setNumbersAngle(DEFAULT_NUMBER_ANGLE);
        this.setExponentVisible(DEFAULT_EXPONENT_VISIBLE);
        this.setExponent(DEFAULT_EXPONENT);
        this.setNumberFontName(DEFAULT_FONT_NAME);
        this.setNumberFontSize(DEFAULT_FONT_SIZE, FONT_SIZE_UNIT);
        this.setNumberFontStyle(DEFAULT_FONT_STYLE);
        this.setNumberFontColor(DEFAULT_FONT_COLOR);

        // tick mark
        this.setTickMarkVisible(DEFAULT_TICK_MARK_VISIBLE);
        this.setTickMarkBothsides(DEFAULT_TICK_MARK_BOTHSIDES);
        this.setTickMarkWidth(DEFAULT_TICK_MARK_WIDTH, LINE_WIDTH_UNIT);
        this.setMajorTickMarkLength(DEFAULT_TICK_MARK_LENGTH, TICK_MARK_LENGTH_UNIT);
        this.setMinorTickMarkNumber(DEFAULT_MINOR_TICK_MARK_NUMBER);
        this.setMinorTickMarkLength(DEFAULT_MINOR_TICK_MARK_LENGTH, TICK_MARK_LENGTH_UNIT);
        this.setTickMarkColor(DEFAULT_LINE_COLOR);
    }
    
    public abstract void init();

    /**
     * Creates axis lines.
     * 
     * @return axis lines
     */
    protected abstract ElementLineAxis[] createAxisLines();

    /**
     * Disposes of this object.
     */
    public void dispose() {
    	this.mDisposed = true;
    	
        this.mAxis = null;
        this.mAxisValueArray = null;

        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            this.mAxisLines[ii].dispose();
        }
        this.mAxisLines = null;

        for (int ii = 0; ii < this.mNumberList.size(); ii++) {
            SGDrawingElementString el = (SGDrawingElementString) this.mNumberList.get(ii);
            el.dispose();
        }
        this.mNumberList.clear();

        for (int ii = 0; ii < this.mTickMarksList.size(); ii++) {
            SGDrawingElementLine el = (SGDrawingElementLine) this.mTickMarksList.get(ii);
            el.dispose();
        }
        this.mTickMarksList.clear();

        this.mTitle.dispose();
        this.mTitle = null;
    }

    /**
     * Returns whether this object is already disposed of.
     * 
     * @return true if this object is already disposed of
     */
    public boolean isDisposed() {
    	return this.mDisposed;
    }

    /**
     * Returns the axis.
     * 
     * @return the axis
     */
    public SGAxis getAxis() {
        return this.mAxis;
    }

    /**
     * Returns whether this axis is visible.
     * 
     * @return true if this axis visible
     */
    public boolean isVisible() {
        return this.mVisible;
    }
    
    /**
     * 
     * @return
     */
    public boolean isTitleVisible() {
        return this.mTitle.isVisible();
    }

    /**
     * 
     * @return
     */
    public String getTitleString() {
        return this.mTitle.getString();
    }

    /**
     * 
     * @return
     */
    public SGAxisValue getMinValue() {
    	return this.mAxis.getMinValue();
    }

    /**
     * 
     * @return
     */
    public SGAxisValue getMaxValue() {
    	return this.mAxis.getMaxValue();
    }
    
    /**
     * 
     * @return
     */
    public boolean isInvertCoordinates() {
        return this.mAxis.isInvertCoordinates();
    }

    /**
     * 
     * @return
     */
    public int getScaleType() {
        return this.mAxis.getScaleType();
    }

    /**
     * 
     * @return
     */
    public boolean isScaleAuto() {
        return this.mAutoCalcFlag;
    }

    /**
     * 
     * @return
     */
    public SGAxisStepValue getScaleStep() {
        return this.mStepValue;
    }

    /**
     * 
     * @return
     */
    public SGAxisValue getScaleBase() {
        return this.mBaselineValue;
    }

    /**
     * 
     * @return
     */
    public boolean isTickMarkVisible() {
        return this.mTickMarksVisibleFlag;
    }

    /**
     * Returns whether tick marks are drawn to both sides.
     * 
     * @return the direction of tick marks
     */
    public boolean isTickMarkBothsides() {
    	return this.mTickMarkBothsides;
    }
    
    /**
     * Returns the number of minor tick marks between two major tick marks.
     * 
     * @return the number of minor tick marks between two major tick marks
     */
    public int getMinorTickMarkNumber() {
    	return this.mMinorTickMarkNumber;
    }
    
    /**
     * Returns the length of minor tick mark in a given unit of length.
     * 
     * @param unit
     *           a unit of length
     * @return the length of minor tick mark
     */
    public float getMinorTickMarkLength(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.mMinorTickMarkLength, unit);
    }

    /**
     * Returns the length of minor tick mark.
     * 
     * @return the length of minor tick mark
     */
    public float getMinorTickMarkLength() {
    	return this.mMinorTickMarkLength;
    }

    /**
     * 
     * @return
     */
    public boolean isNumbersVisible() {
        return this.mNumbersVisibleFlag;
    }

    /**
     * 
     * @return
     */
    public boolean isNumbersInteger() {
        return this.mNumberInteger;
    }

    /**
     * 
     * @return
     */
    public boolean isExponentVisible() {
        return this.mExponentVisible;
    }

    /**
     * 
     * @return
     */
    public int getExponentValue() {
        return this.mExponentValue;
    }
    
    /**
     * 
     * @return
     */
    @Override
    public float getNumberAngle() {
        return this.mNumberAngle;
    }

    /**
     * Sets the visibility of the axis.
     * 
     * @param b
     *          true to set visible
     * @return true if succeeded
     */
    public boolean setVisible(final boolean b) {
        this.mVisible = b;
        return true;
    }

    /**
     * Sets the visibility of the title.
     * 
     * @param b
     *          true to set visible
     * @return true if succeeded
     */
    public boolean setTitleVisible(final boolean b) {
        this.mTitle.setVisible(b);
        return true;
    }

    /**
     * Sets the title.
     * 
     * @param str
     *           a text string to set to the title
     * @return true if succeeded
     */
    public boolean setTitleText(final String str) {
        if (SGUtilityText.isValidString(str) == false) {
            return false;
        }
        this.mTitle.setString(str);
        return true;
    }

    /**
     * Sets whether the axis scale is inverted.
     * 
     * @param b
     *          true to be inverted
     * @return true if succeeded
     */
    public boolean setInvertedCoordinates(final boolean b) {
        return this.mAxis.setInvertCoordinates(b);
    }

    /**
     * Sets the scale range and the scale type.
     * 
     * @param minValue
     *           the minimum value to set
     * @param maxValue
     *           the maximum value to set
     * @param scaleType
     *           the scale type to set
     * @return true if succeeded
     */
    public boolean setScale(final SGAxisValue minValue, final SGAxisValue maxValue, final Integer scaleType) {
        final SGAxisValue min = (minValue != null) ? minValue : this.getMinValue();
        final SGAxisValue max = (maxValue != null) ? maxValue : this.getMaxValue();
        final int type = (scaleType != null) ? scaleType.intValue() : this.getScaleType();
        if (max.getValue() <= min.getValue()) {
            return false;
        }
        if (!SGAxis.isValidValue(min, type) 
        		|| !SGAxis.isValidValue(max, type)) {
            return false;
        }
        if (this.mAxis.setScale(min, max, type) == false) {
        	return false;
        }
        return true;
    }

    /**
     * Sets the scale range with current scale type.
     * 
     * @param minValue
     *           the minimum value to set
     * @param maxValue
     *           the maximum value to set
     * @return true if succeeded
     */
    public boolean setScale(final SGAxisValue minValue, final SGAxisValue maxValue) {
        return this.setScale(minValue, maxValue, this.mAxis.getScaleType());
    }

    /**
     * Sets the scale type.
     * 
     * @param type
     *           the scale type to set
     * @return true if succeeded
     */
    public boolean setScaleType(final int type) {
        if (SGAxis.isValidScaleType(type) == false) {
            return false;
        }
        final double min = this.mAxis.getMinDoubleValue();
        final double max = this.mAxis.getMaxDoubleValue();
        if (!SGAxis.isValidValue(min, type) || !SGAxis.isValidValue(max, type)) {
            return false;
        }
        return this.mAxis.setScale(min, max, type);
    }
    
    /**
     * Sets the flag whether the tick marks are calculated automatically.
     * @param b
     *          true to calculate automatically
     * @return true if succeeded
     */
    public boolean setCalculateAutomatically(final boolean b) {
        this.mAutoCalcFlag = b;
        return true;
    }

    /**
     * Sets the step value.
     * 
     * @param value
     *           the step value to set
     * @return true if succeeded
     */
    public boolean setStepValue(final SGAxisStepValue value) {
        this.mStepValue = value;
        return true;
    }

    /**
     * Sets the baseline value.
     * 
     * @param value
     *           the baseline value to set
     * @return true if succeeded
     */
    public boolean setBaselineValue(final SGAxisValue value) {
        this.mBaselineValue = value;
        return true;
    }

    /**
     * Sets the visibility of the tick marks.
     * 
     * @param b
     *          true to set visible
     * @return true if succeeded
     */
    public boolean setTickMarkVisible(final boolean b) {
        this.mTickMarksVisibleFlag = b;
        return true;
    }

    /**
     * Sets whether tick marks are drawn to both sides.
     * 
     * @param b
     *           true to draw to both sides
     * @return true if succeeded
     */
    public boolean setTickMarkBothsides(final boolean b) {
    	this.mTickMarkBothsides = b;
        return true;
    }
    
    /**
     * Sets the number of minor tick marks.
     * 
     * @param num
     *           the number of minor tick marks
     * @return true if succeeded
     */
    public boolean setMinorTickMarkNumber(final int num) {
    	if (num < MINOR_TICK_MARK_NUMBER_MIN) {
    		return false;
    	} else if (num > MINOR_TICK_MARK_NUMBER_MAX) {
    		return false;
    	}
    	this.mMinorTickMarkNumber = num;
    	return true;
    }

    /**
     * Sets the length of minor tick marks in a given unit of length.
     * 
     * @param len
     *           the length of minor tick marks
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setMinorTickMarkLength(final float len, final String unit) {
        final Float lNew = SGUtility.calcPropertyValue(len, unit, 
                TICK_MARK_LENGTH_UNIT, MINOR_TICK_MARK_LENGTH_MIN, MINOR_TICK_MARK_LENGTH_MAX, 
                MINOR_TICK_MARK_LENGTH_MINIMAL_ORDER);
        if (lNew == null) {
            return false;
        }
        return this.setMinorTickMarkLength(lNew);
    }

    /**
     * Sets the length of minor tick marks.
     * 
     * @param len
     *           the length of minor tick marks
     * @return true if succeeded
     */
    public boolean setMinorTickMarkLength(final float len) {
    	this.mMinorTickMarkLength = len;
    	return true;
    }

    /**
     * Sets the visibility of the numbers.
     * 
     * @param b
     *          true to set visible
     * @return true if succeeded
     */
    public boolean setNumbersVisible(final boolean b) {
        this.mNumbersVisibleFlag = b;
        return true;
    }

    /**
     * Sets the numbers to be integer.
     * 
     * @param b
     *          true for integer
     * @return true if succeeded
     */
    public boolean setNumbersInteger(final boolean b) {
        this.mNumberInteger = b;
        return true;
    }

    /**
     * Sets whether to use the exponent symbol.
     * 
     * @param b
     *          true to use the exponent symbol
     * @return true if succeeded
     */
    public boolean setExponentVisible(final boolean b) {
        this.mExponentVisible = b;
        return true;
    }

    /**
     * Sets the exponent value.
     * 
     * @param value
     *           the exponent value to set
     * @return true if succeeded
     */
    public boolean setExponent(final int value) {
        final int vNew;
        if (value < AXIS_EXPONENT_MIN) {
            return false;
        } else if (value > AXIS_EXPONENT_MAX) {
            return false;
        } else {
            vNew = value;
        }
        this.mExponentValue = vNew;
        return true;
    }
    
    @Override
    public boolean setNumbersAngle(final float angle) {
        final Float aNew = SGUtility.calcPropertyValue(angle, null, null, 
        		STRING_ANGLE_MIN, STRING_ANGLE_MAX,
        		STRING_ANGLE_MINIMAL_ORDER);
        if (aNew == null) {
            return false;
        }
        this.mNumberAngle = aNew.floatValue();
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean hasValidAxisValues(final SGAxisValue minValue,
            final SGAxisValue maxValue, final Integer scaleType,
            final SGAxisValue baseValue, final SGAxisStepValue stepValue) {
        final double min = (minValue != null) ? minValue.getValue()
                : this.getMinValue().getValue();
        final double max = (maxValue != null) ? maxValue.getValue()
                : this.getMaxValue().getValue();
        final int type = (scaleType != null) ? scaleType.intValue() : this
                .getScaleType();
//        final double step = (stepValue != null) ? stepValue.doubleValue()
//                : this.getScaleStep();
//        if (stepValue != null) {
//            // step value
//            if (stepValue.doubleValue() == 0.0) {
//                return false;
//            }
//        }

        // min and max values
        if (min >= max) {
            return false;
        }

        // scale type
        if (type == SGAxis.LOG_SCALE) {
            if (min <= 0.0) {
                return false;
            }
        }

//        // step value
//        if (step == 0.0) {
//            return false;
//        }

        return true;
    }

    /**
     * 
     * @param minValue
     * @param maxValue
     * @param scaleType
     * @return
     */
    public boolean hasValidAxisRange(final SGAxisValue minValue,
            final SGAxisValue maxValue, final Integer scaleType) {
        final int type = (scaleType != null) ? scaleType.intValue() : this
                .getScaleType();
        final double min = (minValue != null) ? minValue.getValue()
                : this.getMinValue().getValue();
        final double max = (maxValue != null) ? maxValue.getValue()
                : this.getMaxValue().getValue();

        if (min >= max) {
            return false;
        }

        if (type == SGAxis.LOG_SCALE) {
            if (min <= 0.0) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * @param baseValue
     * @param stepValue
     * @param scaleType
     * @return
     */
    public boolean hasValidAxisValues(final SGAxisValue baseValue,
            final SGAxisStepValue stepValue, final Integer scaleType) {
        if (stepValue != null) {
            if (stepValue.isZero()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Draw all objects.
     * 
     * @param g2d
     *           the graphic context
     */
    public void paintGraphics2D(final Graphics2D g2d) {

        if (this.isVisible()) {
            
            // axis line
        	if (this.isAxisLineVisible()) {
                this.drawAxisLine(g2d);
        	}

            // title
            if (this.isTitleVisible()) {
                this.drawTitle(g2d);
            }

            // scale numbers
            if (this.isNumbersVisible()) {
                this.drawScaleNumbers(g2d);
            }

            // scale lines
            if (this.isTickMarkVisible()) {
                this.drawTickMarks(g2d);
            }
        }
    }

    // draw a bounding box for a string element
    boolean drawStringBounds(final SGDrawingElementString2D element,
            final Graphics2D g2d) {
        if (element == null || g2d == null) {
            return false;
        }
        final double strRectLineWidth = 1.0;
        final Color strRectLineColor = Color.BLACK;
        g2d.setPaint(strRectLineColor);
        g2d.setStroke(new BasicStroke((float) strRectLineWidth));
        final Rectangle2D rect = element.getElementBounds();
        g2d.drawRect((int) rect.getX(), (int) rect.getY(), (int) rect
                .getWidth(), (int) rect.getHeight());
        return true;

    }

    // draw a string element
    boolean drawString(final SGDrawingElementString2D element,
            final Graphics2D g2d) {
        if (element == null || g2d == null) {
            return false;
        }
        element.paint(g2d);
        return true;
    }

    // draw a line
    boolean drawLine(final SGDrawingElementLine2D element,
            final Graphics2D g2d, final BasicStroke stroke) {
        if (element == null || g2d == null) {
            return false;
        }
        g2d.setPaint(element.getColor());
        g2d.setStroke(stroke);
        SGTuple2f start = element.getStart();
        SGTuple2f end = element.getEnd();
        final Line2D line = new Line2D.Float(start.x, start.y, end.x, end.y);
        g2d.draw(line);
        return true;
    }

    protected void drawAxisLine(final Graphics2D g2d) {
        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            ElementLineAxis line = this.mAxisLines[ii];
            final float width = this.getMagnification()
                    * line.getLineWidth();
            BasicStroke stroke = new BasicStroke(width,
                    BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER);
            this.drawLine(line, g2d, stroke);
        }
    }
    
    protected void drawTitle(final Graphics2D g2d) {
    	this.drawString(this.mTitle, g2d);
        if (this.mTitle
                .equals(this.mAxisElement.mDraggableAxisScaleElement)) {
        	this.drawStringBounds(this.mTitle, g2d);
        }
    }
    
    protected void drawScaleNumbers(final Graphics2D g2d) {
        for (int ii = 0; ii < this.mNumberList.size(); ii++) {
            final ElementStringNumber el = this.mNumberList.get(ii);
            this.drawString(el, g2d);
        }

        // draw temporary number
        if (this.mDraggingElement != null) {
            if (this.mDraggingElement instanceof ElementStringNumber) {
                ElementStringNumber el = (ElementStringNumber) this.mDraggingElement;
                this.drawString(el, g2d);
                this.drawStringBounds(el, g2d);
            }
        }

        // exponent
        if (this.isExponentAvailable()) {
        	this.drawString(this.mExponentSymbol, g2d);
        }
    }
    
    private boolean isExponentAvailable() {
    	return (this.isExponentVisible() && !this.getDateMode());
    }
    
    protected void drawTickMarks(final Graphics2D g2d) {
        final boolean lineDragFlag = (this.mDraggingElement != null)
                && (this.mDraggingElement instanceof ElementLineTickMark);
        for (int ii = 0; ii < this.mTickMarksList.size(); ii++) {
            final ElementLineTickMark el = this.mTickMarksList.get(ii);
            final float width = this.getMagnification() * el.getLineWidth();
            final float dWidth = lineDragFlag ? width * 2.0f : width;
            BasicStroke stroke = new BasicStroke(dWidth,
                    BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
            if (lineDragFlag) {
                el.setLineWidth(dWidth);
            }
            this.drawLine(el, g2d, stroke);
            if (lineDragFlag) {
                el.setLineWidth(width);
            }
        }
    }

    /**
     * 
     */
    protected double getScaleHeight() {
        double height = 0.0;
        if (this.mNumberList.size() == 0) {
            final Font font = new Font(
            		this.mNumberFontName,
            		this.mNumberFontStyle,
                    (int) (this.mAxisElement.getMagnification() * this.mNumberFontSize));
            
            // create font render context
            final FontRenderContext frc = new FontRenderContext(null, false, false);

            // create text layout
            final TextLayout layout = new TextLayout("0", font, frc);

            // get a visual bounds rectangle from Font object
            final Rectangle2D rect = layout.getOutline(new AffineTransform()).getBounds2D();
            height = rect.getHeight();
            
        } else {
            ElementStringNumber el = this.mNumberList.get(0);
            Rectangle2D rect = el.getStringRect();
            height = rect.getHeight();
        }
        return height;
    }

    protected int getLocationInPlane() {
        return this.mLocationInPlane;
    }

    protected boolean setLocationInPlane(final int locationInPlane) {
        this.mLocationInPlane = locationInPlane;
        return true;
    }
    
    protected int getLocation() {
    	return this.getLocationInPlane();
    }

    /**
     * Returns the magnification.
     * 
     * @return the magnification
     */
    public float getMagnification() {
    	return this.mAxisElement.getMagnification();
    }
    
    /**
     * Sets the magnification.
     * 
     * @param mag
     *           the magnification
     * @return true if succeeded
     */
    public boolean setMagnification(final float mag) {
        this.mTitle.setMagnification(mag);
        List<ElementStringNumber> nList = this.mNumberList;
        for (int ii = 0; ii < nList.size(); ii++) {
            ElementStringNumber el = nList.get(ii);
            el.setMagnification(mag);
        }
        if (this.mExponentSymbol != null) {
            this.mExponentSymbol.setMagnification(mag);
        }
        this.mAxisLineStroke.setMagnification(mag);
    	this.mTickMarkStroke.setMagnification(mag);
        return true;
    }

    /**
     * 
     * @return
     */
    protected Rectangle2D getBoundingBox() {
        if (this.isVisible() == false) {
            return null;
        }

        Rectangle2D rect = new Rectangle2D.Float();

        ArrayList list = new ArrayList();

        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            list.add(this.mAxisLines[ii]);
        }

        if (this.isTitleVisible()) {
            list.add(this.mTitle);
        }

        if (this.isNumbersVisible()) {
            for (int ii = 0; ii < this.mNumberList.size(); ii++) {
                ElementStringNumber el = this.mNumberList.get(ii);
                if (el.isVisible()) {
                    list.add(el);
                }
            }

            if (this.isExponentVisible()) {
                list.add(this.mExponentSymbol);
            }
        }

        if (list.size() != 0) {
            rect.setRect(SGUtilityForFigureElementJava2D.getBoundingBox(list));
        }

        return rect;
    }

    /**
     * 
     */
    public boolean onMouseClicked(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int mod = e.getModifiers();
        final int cnt = e.getClickCount();
        final boolean ctrl = (mod & InputEvent.CTRL_MASK) != 0;
        final boolean shift = (mod & InputEvent.SHIFT_MASK) != 0;
        
        // check axis line even when axis is invisible
        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            if (this.mAxisLines[ii].contains(x, y)) {
                return this.clicked(e);
            }
        }

        // return if axis is invisible
        if (!this.isVisible()) {
            return false;
        }

        // title
        if (this.isTitleVisible()) {
            if (this.mTitle.contains(x, y)) {
                if (this.mAxisElement.isEdited()) {
                    this.mAxisElement.closeTextField();
                }
                if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
                    if (this.mTitle.isSelected() && !ctrl && !shift) {
                        this.mAxisElement.mEditingStringElement = this.mTitle;
                        Point point = this.mAxisElement.getPressedPoint();
                        final int tx = point.x - (int) this.mTitle.getX();
                        final int ty = point.y - (int) this.mTitle.getY();                            
                        this.mAxisElement.showEditField(this.mAxisElement.mTextField, this.mTitle, tx, ty);
                	} else {
                        this.mAxisElement.updateFocusedObjectsList(this.mTitle, e);
                        
                        // avoids simultaneous selection
                		if (this.mTitle.isSelected()) {
                	        this.setSelectedFlag(false);
                		}
                	}
                    return true;
                }
                return this.clicked(e);
            }
        }

        // numbers
        if (this.isNumbersVisible()) {
            for (int ii = 0; ii < this.mNumberList.size(); ii++) {
                ElementStringNumber el = this.mNumberList.get(ii);
                if (el.contains(x, y)) {
                    return this.clicked(e);
                }
            }
        }

        // exponent
        if (this.isExponentAvailable()) {
            if (this.mExponentSymbol.contains(x, y)) {
                if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
                    this.mAxisElement.updateFocusedObjectsList(this.mExponentSymbol, e);
                    
                    // avoids simultaneous selection
            		if (this.mExponentSymbol.isSelected()) {
            	        this.setSelectedFlag(false);
            		}
                    return true;
                }
                return this.clicked(e);
            }
        }

        // tick marks
        if (this.isTickMarkVisible()) {
            for (int ii = 0; ii < this.mTickMarksList.size(); ii++) {
                ElementLineTickMark el = this.mTickMarksList.get(ii);
                if (el.contains(x, y)) {
                    return this.clicked(e);
                }
            }
        }

        return false;
    }
    
    /**
     * Called when the axis is clicked.
     * 
     * @param e
     *          the mouse event
     * @return true if a drawing element is clicked, and false otherwise
     */
    protected abstract boolean clicked(final MouseEvent e);
    
    /**
     * Called when the mouse is pressed.
     */
    public boolean onMousePressed(final MouseEvent e) {

        // if this axis-group is invisible, returns false
        if (!this.isVisible()) {
            return false;
        }

        final int x = e.getX();
        final int y = e.getY();

        // axis line
        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            if (this.mAxisLines[ii].contains(x, y)) {
            	this.mDraggingElement = this.mAxisLines[ii];
                this.prepare();
                return true;
            }
        }

        // title
        if (this.isTitleVisible()) {
            if (this.mTitle.contains(x, y)) {
            	this.mDraggingElement = this.mTitle;
                this.prepare();
                return true;
            }
        }
        
        // exponent
        if (this.isExponentAvailable()) {
            if (this.mExponentSymbol.contains(x, y)) {
            	this.mDraggingElement = this.mExponentSymbol;
                this.prepare();
                return true;
            }
        }

        // numbers for axis
        if (this.isNumbersVisible()) {
            for (int ii = 0; ii < this.mNumberList.size(); ii++) {
                ElementStringNumber el = this.mNumberList.get(ii);
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y)) {
                	// clears the selection of axis
                	this.clearFocus();
            		
                    this.mAxisElement.mDraggableAxisScaleElement = el;
                    this.mAxisElement.mPressedElementOrigin = new Point(
                            (int) (e.getX() - el.getX()),
                            (int) (e.getY() - el.getY()));
                    this.mTempRange = this.mAxis.getRange();
                    this.mDraggingElement = new ElementStringNumber(el);
                    this.mDraggingElement.setMagnification(el
                            .getMagnification());
                    this.prepare();
                    return true;
                }
            }
        }

        // scale lines for axis
        if (this.isTickMarkVisible()) {
            for (int ii = 0; ii < this.mTickMarksList.size(); ii++) {
                ElementLineTickMark el = this.mTickMarksList.get(ii);
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y)) {
                	// clears the selection of axis
                	this.clearFocus();
            		
                    this.mAxisElement.mDraggableAxisScaleElement = el;
                    this.mAxisElement.mPressedElementOrigin = e.getPoint();
                    this.mTempRange = this.mAxis.getRange();
                    this.mDraggingElement = this.createScaleLineInstance(el);
                    this.mDraggingElement.setMagnification(el
                            .getMagnification());
                    this.prepare();
                    return true;
                }
            }
        }

        return false;
    }
    
    private void clearFocus() {
		this.setSelected(false);
		this.mTitle.setSelectedFlag(false);
		this.mExponentSymbol.setSelectedFlag(false);
    }
    
    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    public boolean preview() {
        if (this.createDrawingElements() == false) {
            return false;
        }
        this.notifyAxisScaleChange();
        this.mAxisElement.notifyChange();
        this.mAxisElement.repaint();
        return true;
    }

    /**
     * Commit the change of the properties.
     * 
     * @return true if succeeded
     */
    public boolean commit() {
    
        // compare two properties
        SGProperties pTemp = this.mTemporaryProperties;
        SGProperties pPresent = this.getProperties();
        if (pTemp.equals(pPresent) == false) {
            this.setChanged(true);
        }
        
        // clear temporary properties
        this.mTemporaryProperties = null;

        // update drawing elements
        if (this.createDrawingElements() == false) {
            return false;
        }
        this.notifyAxisScaleChange();
        this.mAxisElement.notifyChangeOnCommit();
        this.mAxisElement.repaint();

        return true;
    }

    /**
     * Cancel the setting of properties.
     * @return
     *         true if succeeded
     */
    public boolean cancel() {
    
        // set temporary properties to drawing elements to cancel the change
        if (this.setProperties(this.mTemporaryProperties) == false) {
            return false;
        }

        // clear temporary properties
        this.mTemporaryProperties = null;
        
        // update drawing elements
        if (this.createDrawingElements() == false) {
            return false;
        }
        this.notifyAxisScaleChange();
        this.mAxisElement.notifyChangeOnCancel();
        this.mAxisElement.repaint();

        return true;
    }

    protected ElementLineTickMark createScaleLineInstance(ElementLineTickMark el) {
    	return new ElementLineTickMark(el);
    }

    // temporary range for mouse drag
    protected SGTuple2d mTempRange = null;

    /**
     * Called when the mouse is dragged.
     * 
     * @param e
     *          the mouse event
     * @return true if succeeded
     */
    public boolean onMouseDragged(final MouseEvent e) {
        if (!this.mVisible) {
            return false;
        }
        if (this.mDraggingElement == null || this.mTempRange == null) {
            return false;
        }

        AxisValueRange range = null;
        if (this.mDraggingElement instanceof ElementStringNumber) {
        	// ElementStringOfScale
        	range = this.dragScaleNumber(e);
        } else if (this.mDraggingElement instanceof ElementLineTickMark) {
            // ElementLineOfScale
        	range = this.dragElementLineOfScale(e);
        }

        if (range != null) {
            // set the range to the axis
            final SGAxisValue minValue = range.min;
            final SGAxisValue maxValue = range.max;
            final int scaleType = this.getScaleType();
            this.mAxis.setScale(minValue, maxValue, scaleType);
            
            // create drawing elements
            if (this.createDrawingElements() == false) {
                return false;
            }

            // notify to listeners
            if (SGFigureElementAxis.mNotifyChangeOnDraggingFlag) {
            	this.mAxisElement.notifyChange();
            }
            
            // notify to listeners
            this.notifyAxisScaleChange();
        }

        return true;
    }
    
    protected void notifyAxisScaleChange() {
        for (AxisScaleChangeListener l : this.mAxisScaleChangeListenerList) {
        	l.axisScaleChanged(this.mAxis);
        }
    }

	protected boolean onTitleDragged(MouseEvent e) {
    	Point pos = this.mAxisElement.getPressedPoint();
    	if (pos != null) {
    		if (this.mTitle.isSelected()) {
    	        final int dx = e.getX() - pos.x;
    	        final int dy = e.getY() - pos.y;
    			this.mTitle.translate(dx, dy);
    			this.mAxisElement.setPressedPoint(e.getPoint());
    		}
    		return true;
    	}
    	return false;
	}
	
	protected void onTitleReleased() {
		this.mDraggingElement = null;
	}

	protected boolean onExponentDragged(MouseEvent e) {
    	Point pos = this.mAxisElement.getPressedPoint();
    	if (pos != null) {
    		if (this.mExponentSymbol.isSelected()) {
    	        final int dx = e.getX() - pos.x;
    	        final int dy = e.getY() - pos.y;
    			this.mExponentSymbol.translate(dx, dy);
    			this.mAxisElement.setPressedPoint(e.getPoint());
    		}
    		return true;
    	}
    	return false;
	}

	protected void onExponentReleased() {
		this.mDraggingElement = null;
	}

	protected float roundOffSpace(final float value) {
		final int digitShift = SPACE_MINIMAL_ORDER - 1;
		final float shift = (float) SGUtilityNumber.roundOffNumber(
				value, digitShift);
		return shift;
	}

	protected float roundOffTitleShift(final float value) {
		final int digitShift = TITLE_SHIFT_MINIMAL_ORDER - 1;
		final float shift = (float) SGUtilityNumber.roundOffNumber(
				value, digitShift);
		return shift;
	}

	protected float roundOffExponentShift(final float value) {
		final int digitShift = EXPONENT_LOCATION_MINIMAL_ORDER - 1;
		final float shift = (float) SGUtilityNumber.roundOffNumber(
				value, digitShift);
		return shift;
	}

    protected static class ValueInScale {
        double minValueTempInScale;
        double maxValueTempInScale;
        double oldValueInScale;
    }
    
    private ValueInScale getValueTempInScale(final double elValue) {
        final int scaleType = this.getScaleType();
        double minValueTempInScale;
        double maxValueTempInScale;
        double oldValueInScale;
        switch (scaleType) {
        case SGAxis.LINEAR_SCALE:
            minValueTempInScale = this.mTempRange.x;
            maxValueTempInScale = this.mTempRange.y;
            oldValueInScale = elValue;
        	break;
        case SGAxis.LOG_SCALE:
            minValueTempInScale = Math.log10(this.mTempRange.x);
            maxValueTempInScale = Math.log10(this.mTempRange.y);
            oldValueInScale = Math.log10(elValue);
        	break;
       	default:
            throw new Error("Ivalid scaleType: " + scaleType);
        }
        ValueInScale value = new ValueInScale();
        value.minValueTempInScale = minValueTempInScale;
        value.maxValueTempInScale = maxValueTempInScale;
        value.oldValueInScale = oldValueInScale;
        return value;
    }
    
    /**
     * Called when a string of the scale number is dragged.
     */
    protected abstract AxisValueRange dragScaleNumber(MouseEvent e);
    
    /**
     * Called when a scale number is dragged horizontally.
     */
    protected AxisValueRange dragScaleNumberHorizontal(MouseEvent e, ElementLineAxis line) {
        final boolean invcoord = this.isInvertCoordinates();
        final ElementStringNumber el = (ElementStringNumber) this.mDraggingElement;
        ValueInScale value = this.getValueTempInScale(el.mValue);
        final double minValueTempInScale = value.minValueTempInScale;
        final double maxValueTempInScale = value.maxValueTempInScale;
        final double oldValueInScale = value.oldValueInScale;
        final Rectangle2D rect = el.getElementBounds();

        double draggedCoordinate = e.getX()
                - this.mAxisElement.mPressedElementOrigin.x
                + rect.getWidth() / 2.0;
        final float minCoordinate = line.getStart().x;
        final float maxCoordinate = line.getEnd().x;

        if (invcoord) {
            if (draggedCoordinate < minCoordinate) {
                draggedCoordinate = minCoordinate;
            }
            if (draggedCoordinate >= maxCoordinate) {
                return null;
            }
        } else {
            if (draggedCoordinate > maxCoordinate) {
                draggedCoordinate = maxCoordinate;
            }
            if (draggedCoordinate <= minCoordinate) {
                return null;
            }
        }

        final double valueInScale;
        if (invcoord) {
            valueInScale = minValueTempInScale
            + (maxValueTempInScale - minValueTempInScale)
            * (maxCoordinate - draggedCoordinate)
            / (maxCoordinate - minCoordinate);
        } else {
            valueInScale = minValueTempInScale
            + (maxValueTempInScale - minValueTempInScale)
            * (draggedCoordinate - minCoordinate)
            / (maxCoordinate - minCoordinate);
        }

        // get new range
        double minValueInScale = minValueTempInScale;
        double maxValueInScale = minValueTempInScale
                + (maxValueTempInScale - minValueTempInScale)
                * (oldValueInScale - minValueTempInScale)
                / (valueInScale - minValueTempInScale);
        minValueInScale = SGUtilityNumber.getNumberInRangeOrder(minValueInScale, this.mAxis);
        maxValueInScale = SGUtilityNumber.getNumberInRangeOrder(maxValueInScale, this.mAxis);

        final float x = (float) (draggedCoordinate - rect
                .getWidth() / 2.0);
        el.setLocation(x, el.getLocation().y);

        final float ratio = (float) ((maxValueInScale - minValueInScale) / (maxValueTempInScale - minValueTempInScale));
        if (ratio < 0.05) {
            return null;
        }

        return this.createValueRange(minValueInScale, maxValueInScale);
    }

    /**
     * Called when a scale number is dragged vertically.
     */
    protected AxisValueRange dragScaleNumberPerpendiculer(MouseEvent e, ElementLineAxis line) {
        final boolean invcoord = this.isInvertCoordinates();
        final ElementStringNumber el = (ElementStringNumber) this.mDraggingElement;
        ValueInScale value = this.getValueTempInScale(el.mValue);
        final double minValueTempInScale = value.minValueTempInScale;
        final double maxValueTempInScale = value.maxValueTempInScale;
        final double oldValueInScale = value.oldValueInScale;
        final Rectangle2D rect = el.getElementBounds();

		float draggedCoordinate = e.getY()
				- this.mAxisElement.mPressedElementOrigin.y
				+ (float) rect.getHeight() / 2.0f;
		final float minCoordinate = line.getEnd().y;
		final float maxCoordinate = line.getStart().y;
		if (invcoord) {
			if (draggedCoordinate <= minCoordinate) {
				return null;
			}
			if (draggedCoordinate > maxCoordinate) {
				draggedCoordinate = maxCoordinate;
			}
		} else {
			if (draggedCoordinate >= maxCoordinate) {
				return null;
			}
			if (draggedCoordinate < minCoordinate) {
				draggedCoordinate = minCoordinate;
			}
		}

		final double valueInScale;
		if (invcoord) {
			valueInScale = minValueTempInScale
					+ (maxValueTempInScale - minValueTempInScale)
					* (1.0 - (maxCoordinate - draggedCoordinate)
							/ (maxCoordinate - minCoordinate));
		} else {
			valueInScale = minValueTempInScale
					+ (maxValueTempInScale - minValueTempInScale)
					* (1.0 - (draggedCoordinate - minCoordinate)
							/ (maxCoordinate - minCoordinate));
		}

		// get new range
		double minValueInScale = minValueTempInScale;
		double maxValueInScale = minValueTempInScale
				+ (maxValueTempInScale - minValueTempInScale)
				* (oldValueInScale - minValueTempInScale)
				/ (valueInScale - minValueTempInScale);
		minValueInScale = SGUtilityNumber.getNumberInRangeOrder(
				minValueInScale, this.mAxis);
		maxValueInScale = SGUtilityNumber.getNumberInRangeOrder(
				maxValueInScale, this.mAxis);

		final float y = (float) (draggedCoordinate - rect.getHeight() / 2.0);
		el.setLocation(el.getLocation().x, y);

		final float ratio = (float) ((maxValueInScale - minValueInScale) / (maxValueTempInScale - minValueTempInScale));
		if (ratio < 0.05) {
			return null;
		}

        return this.createValueRange(minValueInScale, maxValueInScale);
    }

    /**
     * Called when a string of the scale number is dragged.
     */
    protected abstract AxisValueRange dragElementLineOfScale(MouseEvent e);
    
    /**
     * Called when a scale line is dragged horizontally.
     */
    protected AxisValueRange dragScaleLineHorizontal(MouseEvent e, ElementLineAxis line) {
        final ElementLineTickMark el = (ElementLineTickMark) this.mDraggingElement;
        ValueInScale value = this.getValueTempInScale(el.mValue);
        final double minValueTempInScale = value.minValueTempInScale;
        final double maxValueTempInScale = value.maxValueTempInScale;
        final double oldValueInScale = value.oldValueInScale;
        
        // calculate the location of the point at which the mouse button is dragged
        float draggedCoordinate = e.getX();
        if (this.mAxisElement.mPressedElementOrigin.x == draggedCoordinate) {
            return null;
        }

        final float minCoordinate = line.getStart().x;
        final float maxCoordinate = line.getEnd().x;
        if (draggedCoordinate > maxCoordinate) {
            draggedCoordinate = maxCoordinate;
        }
        if (draggedCoordinate < minCoordinate) {
            draggedCoordinate = minCoordinate;
        }

		final double draggedValue;
		if (this.isInvertCoordinates()) {
			draggedValue = minValueTempInScale
					+ (maxValueTempInScale - minValueTempInScale)
					* (maxCoordinate - draggedCoordinate)
					/ (maxCoordinate - minCoordinate);
		} else {
			draggedValue = minValueTempInScale
					+ (maxValueTempInScale - minValueTempInScale)
					* (draggedCoordinate - minCoordinate)
					/ (maxCoordinate - minCoordinate);
		}
		final double diff = Math.abs(draggedValue - oldValueInScale);

        // get new range
        double minValueInScale;
        double maxValueInScale;
        if (draggedValue > oldValueInScale) {
            minValueInScale = minValueTempInScale - diff;
            maxValueInScale = maxValueTempInScale - diff;
        } else {
            minValueInScale = minValueTempInScale + diff;
            maxValueInScale = maxValueTempInScale + diff;
        }
        minValueInScale = SGUtilityNumber.getNumberInRangeOrder(
                minValueInScale, this.mAxis);
        maxValueInScale = SGUtilityNumber.getNumberInRangeOrder(
                maxValueInScale, this.mAxis);

        return this.createValueRange(minValueInScale, maxValueInScale);
    }
    
    /**
     * Called when a scale line is dragged vertically.
     */
    protected AxisValueRange dragScaleLinePerpendiculer(MouseEvent e, ElementLineAxis line) {
        final ElementLineTickMark el = (ElementLineTickMark) this.mDraggingElement;
        ValueInScale value = this.getValueTempInScale(el.mValue);
        final double minValueTempInScale = value.minValueTempInScale;
        final double maxValueTempInScale = value.maxValueTempInScale;
        final double oldValueInScale = value.oldValueInScale;
        
        // calculate the location of the point at which the mouse button is dragged
        float draggedCoordinate = e.getY();
        if (this.mAxisElement.mPressedElementOrigin.y == draggedCoordinate) {
            return null;
        }

        final float minCoordinate = line.getEnd().y;
        final float maxCoordinate = line.getStart().y;
        if (draggedCoordinate > maxCoordinate) {
            draggedCoordinate = maxCoordinate;
        }
        if (draggedCoordinate < minCoordinate) {
            draggedCoordinate = minCoordinate;
        }
		final double draggedValue;
		if (this.isInvertCoordinates()) {
			draggedValue = minValueTempInScale
					+ (maxValueTempInScale - minValueTempInScale)
					* (1.0 - (maxCoordinate - draggedCoordinate)
							/ (maxCoordinate - minCoordinate));
		} else {
			draggedValue = minValueTempInScale
					+ (maxValueTempInScale - minValueTempInScale)
					* (1.0 - (draggedCoordinate - minCoordinate)
							/ (maxCoordinate - minCoordinate));
		}
		final double diff = Math.abs(draggedValue - oldValueInScale);

        // get new range
        double minValueInScale;
        double maxValueInScale;
        if (draggedValue > oldValueInScale) {
            minValueInScale = minValueTempInScale - diff;
            maxValueInScale = maxValueTempInScale - diff;
        } else {
            minValueInScale = minValueTempInScale + diff;
            maxValueInScale = maxValueTempInScale + diff;
        }
        minValueInScale = SGUtilityNumber.getNumberInRangeOrder(
                minValueInScale, this.mAxis);
        maxValueInScale = SGUtilityNumber.getNumberInRangeOrder(
                maxValueInScale, this.mAxis);

        return this.createValueRange(minValueInScale, maxValueInScale);
    }
    
    private AxisValueRange createValueRange(final double minValueInScale, final double maxValueInScale) {
        final int scaleType = this.getScaleType();
        double minValue;
        double maxValue;
        switch (scaleType) {
        case SGAxis.LINEAR_SCALE:
            minValue = minValueInScale;
            maxValue = maxValueInScale;
        	break;
        case SGAxis.LOG_SCALE:
            minValue = Math.pow(10.0, minValueInScale);
            maxValue = Math.pow(10.0, maxValueInScale);
        	break;
       	default:
            throw new Error("Ivalid scaleType: " + scaleType);
        }
        AxisValueRange range = new AxisValueRange();
        final boolean dateMode = this.getDateMode();
        range.min = dateMode ? new SGAxisDateValue(minValue) : new SGAxisDoubleValue(minValue);
        range.max = dateMode ? new SGAxisDateValue(maxValue) : new SGAxisDoubleValue(maxValue);
        return range;
    }
    
    protected static class AxisValueRange {
    	SGAxisValue min;
    	SGAxisValue max;
    }
    
    /**
     * 
     */
    public abstract boolean onMouseReleased(final MouseEvent e);
    
    protected void onMouseReleasedFinally() {
//        if (this.mTemporaryProperties != null) {
//            SGProperties pTemp = this.mTemporaryProperties;
//            SGProperties pPresent = this.getProperties();
//            if (pTemp.equals(pPresent) == false) {
//                this.setChanged(true);
//            }
//            this.mTemporaryProperties = null;
//    		this.mDraggingElement = null;
//        }
    }
    
    protected void onMouseReleasedNumberOrTickMark() {
        this.mTempRange = null;
        this.mDraggingElement = null;
        this.mAxisElement.createAllDrawingElements();
        this.mAxisElement.initializePressedElement();
    }
    
    /**
     * 
     */
    public boolean setMouseCursor(final int x, final int y) {
        boolean flag = false;
        if (this.isVisible()) {
            flag = this.contains(x, y);
        } else {
            for (int ii = 0; ii < this.mAxisLines.length; ii++) {
                flag = this.mAxisLines[ii].contains(x, y);
                if (flag) {
                    break;
                }
            }
        }

        if (flag) {
            this.mAxisElement.setMouseCursor(Cursor.HAND_CURSOR);
            return true;
        }

        return false;
    }

    /**
     * 
     * @param x
     * @param y
     * @return
     */
    public boolean contains(final int x, final int y) {
        // axis line
        for (int ii = 0; ii < this.mAxisLines.length; ii++) {
            if (this.mAxisLines[ii].contains(x, y)) {
                return true;
            }
        }

        // title
        if (this.isTitleVisible()) {
            if (this.mTitle.contains(x, y)) {
                return true;
            }
        }

        // numbers of axis
        if (this.isNumbersVisible()) {
            for (int ii = 0; ii < this.mNumberList.size(); ii++) {
                final ElementStringNumber el = this.mNumberList.get(ii);
                if (el.contains(x, y)) {
                    return true;
                }
            }
        }

        // scale lines of axis
        if (this.isTickMarkVisible()) {
            for (int ii = 0; ii < this.mTickMarksList.size(); ii++) {
                final ElementLineTickMark el = this.mTickMarksList.get(ii);
                if (el.contains(x, y)) {
                    return true;
                }
            }
        }

        // exponent
        if (this.isExponentAvailable()) {
            if (this.mExponentSymbol.contains(x, y)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Creates all drawing elements.
     */
    protected boolean createDrawingElements() {

        // The order of calling these methods are important.

        // axis line
        if (this.setAxisLineProperties() == false) {
            return false;
        }
        if (this.setLocationOfAxisLines() == false) {
            return false;
        }
        // System.out.println("axis line");

        // numbers and scale lines
        if (this.createScaleDrawingElements() == false) {
            return false;
        }
        // System.out.println("numbers and scale lines");

        // title
        if (this.setLocationOfTitle() == false) {
            return false;
        }
        // System.out.println("title");

        return true;
    }

    // Updates the location of drawing elements.
    boolean updateLocationOfDrawingElements() {

        // axis line
        if (this.setLocationOfAxisLines() == false) {
            return false;
        }

        // scale numbers
        if (this.isNumbersVisible()) {
            if (this.setLocationOfScaleNumbers() == false) {
                return false;
            }
        }

        // scale lines
        if (this.isTickMarkVisible()) {
            if (this.createTickMarks() == false) {
                return false;
            }
        }

        // title
        if (this.isTitleVisible()) {
            if (this.setLocationOfTitle() == false) {
                return false;
            }
        }

        return true;
    }

    // create drawing elements of scale lines and numbers
    private boolean createScaleDrawingElements() {

        // calculate scale numbers
        if (this.calcValueArrayInScale() == false) {
            return false;
        }
        // System.out.println("calcValueArrayInScale");

        // create instances of numbers
        if (this.createScaleNumberInstances() == false) {
            return false;
        }
        // System.out.println("createScaleNumberInstances");

        // determine the location of numbers
        if (this.setLocationOfScaleNumbers() == false) {
            return false;
        }
        // System.out.println("createScaleLines");
        // System.out.println(this.mNumberList);

        // create scale lines
        if (this.createTickMarks() == false) {
            return false;
        }
        // System.out.println("createScaleLines");

        return true;
    }

    /**
     * Sets the location of the axis line.
     * 
     * @return true if succeeded
     */
    protected abstract boolean setLocationOfAxisLines();

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
     * Sets the location of the title.
     * 
     * @return true if succeeded
     */
    protected abstract boolean setLocationOfTitle();
    
    // Creates instances of the scale numbers.
    private boolean createScaleNumberInstances() {

        // clear
        this.mNumberList.clear();
        this.mExponentSymbol = null;

        final double[] vArray = this.mAxisValueArray;
        final String[] strArray;
        if (this.getDateMode()) {
        	
        	String format = this.getNumberDateFormat();
        	
            // create string array
            strArray = new String[vArray.length];
            for (int ii = 0; ii < strArray.length; ii++) {
            	String str = SGDateUtility.toStringByDateValue(vArray[ii], 
            			SGDateUtility.getUTCTimeZoneInstance());
            	str = SGDateUtility.format(str, format);
            	strArray[ii] = str;
            }
        	
        } else {
            // scale type
            final int scaleType = this.mAxis.getScaleType();
        	
            // exponent
            double[] valueArray = null;
            final int eValue = this.getExponentValue();
            if (this.isExponentVisible()) {
                valueArray = new double[this.mAxisValueArray.length];
                for (int ii = 0; ii < valueArray.length; ii++) {
                    BigDecimal db = new BigDecimal(Double.toString(vArray[ii]));
                    db = db.movePointLeft(eValue);
                    valueArray[ii] = db.doubleValue();
                }
            } else {
                valueArray = vArray;
            }

            // create string array
            strArray = new String[valueArray.length];
            if (scaleType == SGAxis.LINEAR_SCALE) {
                if (this.isNumbersInteger()) {
                    for (int ii = 0; ii < strArray.length; ii++) {
                        strArray[ii] = Integer.toString((int) valueArray[ii]);
                    }
                } else {
                    for (int ii = 0; ii < strArray.length; ii++) {
                        strArray[ii] = Double.toString(valueArray[ii]);
                    }
                }
            } else if (scaleType == SGAxis.LOG_SCALE) {
                for (int ii = 0; ii < strArray.length; ii++) {
                    final int order = SGUtilityNumber.getOrder(valueArray[ii]);
                    strArray[ii] = SGUtilityText.getSuperscriptString("10",
                            Integer.toString(order));
                }
            }
        }

        // create drawing elements of the scale numbers
        final float mag = this.mAxisElement.getMagnification();
        for (int ii = 0; ii < vArray.length; ii++) {
            ElementStringNumber el = this.createSingleNumberInstance(
                    strArray[ii]);
            el.mValue = vArray[ii];
            if (this.mAxis.insideRange(el.mValue) == false) {
                el.setVisible(false);
            }
            el.setMagnification(mag);
            this.mNumberList.add(el);
        }

        // create the exponent drawing element
        this.createExponentDrawingElement();

        return true;
    }
    
    /**
     * Creates a drawing element of a scale number.
     */
    protected ElementStringNumber createSingleNumberInstance(
            final String str) {
        ElementStringNumber el = new ElementStringNumber(
                str, this.mNumberFontName, 
                this.mNumberFontStyle, 
                this.mNumberFontSize, 
                this.mNumberFontColor, 
                this.mAxisElement.getMagnification(),
                this.mNumberAngle,
                this.mLocationInPlane);
        return el;
    }

    // Creates an instance of the exponent.
    private boolean createExponentDrawingElement() {
        // creates an instance
        String str = multiply + "10";
        str = SGUtilityText.getSuperscriptString(str, Integer.toString(this
                .getExponentValue()));

        // set to the attribute
        this.mExponentSymbol = this.createExponentStringElement(str);

        // set the location
        this.setLocationOfExponentDrawingElement();

        return true;
    }
    
    /**
     * Creates a string element for the exponent.
     */
    protected ElementStringExponent createExponentStringElement(
            final String str) {
    	ElementStringExponent el = new ElementStringExponent(
                this, str, this.mNumberFontName,
                this.mNumberFontStyle,
                this.mNumberFontSize,
                this.mNumberFontColor,
                this.mAxisElement.getMagnification(), 0.0f);
        return el;
    }

    /**
     * Sets the location of exponent object.
     * 
     * @return true if succeeded
     */
    protected abstract boolean setLocationOfExponentDrawingElement();

    /**
     * Sets the location of scale numbers.
     * 
     * @return true if succeeded
     */
    protected abstract boolean setLocationOfScaleNumbers();

    /**
     * Create the scale lines.
     * 
     * @return true if succeeded
     */
    protected abstract boolean createTickMarks();
    
    /**
     * Creates scale lines in the linear scale.
     * 
     * @return true if succeeded
     */
    protected boolean createTickMarksInLinearScale(final float width,
            final float length, final Color cl) {

    	final float mag = this.mAxisElement.getMagnification();
        final float len = mag * length;
        final double[] valueArray = this.mAxisValueArray;
        
        for (int ii = 0; ii < valueArray.length; ii++) {
            final double value = valueArray[ii];
            this.createTicks(value, width, len, cl);
        }

        // create scale lines between scale numbers
        if (this.mMinorTickMarkNumber != 0) {
            final float minorLen = mag * this.mMinorTickMarkLength;
            final double denominator = this.mMinorTickMarkNumber + 1;
            for (int ii = 0; ii < valueArray.length - 1; ii++) {
            	final double numerator = valueArray[ii + 1] - valueArray[ii];
            	final double minorStepValue = numerator / denominator;
            	for (int jj = 0; jj < this.mMinorTickMarkNumber; jj++) {
                    final double value = valueArray[ii] + (jj + 1) * minorStepValue;
                    this.createTicks(value, width, minorLen, cl);
            	}
            }
        }
        
        return true;
    }

    /**
     * Creates scale lines in the log scale.
     * 
     * @return true if succeeded
     */
    protected boolean createTickMarksInLogScale(final float width,
            final float length, final Color cl) {
        // create the major scale lines
        final float len = this.mAxisElement.getMagnification() * length;
        for (int ii = 0; ii < this.mAxisValueArray.length; ii++) {
            final double value = this.mAxisValueArray[ii];
            this.createTicks(value, width, len, cl);
        }

        // create the minor scale lines
        final float minorLen = this.mMinorTickMarkLength;
        for (int ii = 0; ii < this.mAxisValueArray.length; ii++) {
            for (int jj = 2; jj < 10; jj++) {
                final double value = this.mAxisValueArray[ii] * 0.10 * jj;
                this.createTicks(value, width, minorLen, cl);
            }
        }
        if (this.mAxisValueArray.length >= 1) {
            for (int jj = 2; jj < 10; jj++) {
                final double value = this.mAxisValueArray[this.mAxisValueArray.length - 1]
                        * jj;
                this.createTicks(value, width, minorLen, cl);
            }
        }

        return true;
    }

    // Creates tick marks for a given axis value.
	private void createTicks(final double value, final float width,
			final float len, final Color cl) {
		if (!this.isTickMarkBothsides()) {
	    	this.createTicksSub(value, width, len, cl, true);
		} else {
	    	this.createTicksSub(value, width, len, cl, true);
	    	this.createTicksSub(value, width, len, cl, false);
		}
	}

    // Creates tick marks for a given axis value for given direction.
    protected abstract void createTicksSub(final double value, 
            final float width, final float len, final Color cl, final boolean inside);
	
    /**
     * Sets up the properties of created scale line.
     * 
     * @param el
     *           a scale line
     * @param value
     *           axis value set to the scale line
     * @param cl
     *           the color of line
     */
    protected void setupCreatedScaleLineProperties(final ElementLineTickMark el, 
            final double value, final Color cl) {
        el.mValue = value;
        el.setMagnification(this.mAxisElement.getMagnification());
        el.setColor(cl);
        this.mTickMarksList.add(el);
    }

    /**
     * Creates a scale line.
     * 
     * @param value
     *            axis value of the scale line
     * @param length
     *            the length of the scale line
     * @param inside
     *            true for inside direction
     * @return an instance of the scale line
     */
    protected abstract ElementLineTickMark createSingleTickMark(final double value,
            final float width, final float length, final boolean inside);

    /**
     * Create an array of numbers for current scale type.
     */
    private boolean calcValueArrayInScale() {

        double[] array = null;
        switch (this.mAxis.getScaleType()) {
        case SGAxis.LINEAR_SCALE: // linear scale
            array = this.calcScaleValuesInLinearScale();
            break;
        case SGAxis.LOG_SCALE: // log scale
            array = this.calcScaleValuesInLogScale();
            break;
        default:
        }

        // set to an attribute
        this.mAxisValueArray = array;

        return true;
    }

    /**
     * Calculate the numbers of scale in for the linear scale.
     * 
     * @return an array of axis values
     */
    private double[] calcScaleValuesInLinearScale() {
        SGAxis axis = this.mAxis;
        final double minValue = axis.getMinDoubleValue();
        final double maxValue = axis.getMaxDoubleValue();
        final SGAxisValue baseline;
        final SGAxisStepValue step;
        if (this.isScaleAuto()) {
        	baseline = this.mAxisElement.calcBaselineValue(axis);
        	step = this.mAxisElement.calcStepValue(axis);
            this.mBaselineValue = baseline;
            this.mStepValue = step;
        } else {
            baseline = this.getScaleBase();
            step = this.getScaleStep();
        }

        SGAxisValue[] axisValueArray;
        if (this.getDateMode()) {

            // get an array of numbers
            axisValueArray = SGUtilityNumber.calcStepValueSorted(
            		new SGAxisDateValue(minValue), 
            		new SGAxisDateValue(maxValue), 
            		baseline, step, SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT);

        } else {
        	
            // get an array of numbers
            axisValueArray = SGUtilityNumber.calcStepValueSorted(
            		new SGAxisDoubleValue(minValue), 
            		new SGAxisDoubleValue(maxValue), 
            		baseline, step, SGIConstants.AXIS_SCALE_EFFECTIVE_DIGIT);
            
            // set scale numbers integer when all numbers
            // can be replaced with an integer
            if (this.mAxisElement.mStartFlag) {
                boolean flag = true;
                for (int ii = 0; ii < axisValueArray.length; ii++) {
                    final double value = axisValueArray[ii].getValue();
                    final long round = Math.round(value);
                    final double diff = Math.abs(value - round);
                    if (diff != 0.0) {
                        flag = false;
                        break;
                    }
                }
                this.setNumbersInteger(flag);
            }

            // When the scale numbers are set to be integer,
            // cast values to integer
            if (this.isNumbersInteger()) {

                final List<Integer> numList = new ArrayList<Integer>();
                for (int ii = 0; ii < axisValueArray.length; ii++) {
                    double value;
                    if (this.isExponentVisible()) {
                        BigDecimal db = new BigDecimal(Double
                                .toString(axisValueArray[ii].getValue()));
                        db = db.movePointLeft(this.getExponentValue());
                        value = db.doubleValue();
                    } else {
                        value = axisValueArray[ii].getValue();
                    }
                    final int num = (int) value;
                    if (Math.abs(num - value) < Double.MIN_VALUE) {
                        numList.add(Integer.valueOf(num));
                    }
                }

                // remove the same values
                for (int ii = numList.size() - 1; ii >= 1; ii--) {
                    final Integer n1 = numList.get(ii);
                    for (int jj = ii - 1; jj >= 0; jj--) {
                        final Integer n2 = numList.get(jj);
                        if (n2.intValue() == n1.intValue()) {
                            numList.remove(ii);
                            break;
                        }
                    }
                }

                final double[] valueArray = new double[numList.size()];
                for (int ii = 0; ii < valueArray.length; ii++) {
                    double value = ((Integer) numList.get(ii)).doubleValue();
                    if (this.isExponentVisible()) {
                        BigDecimal db = new BigDecimal(Double.toString(value));
                        db = db.movePointRight(this.getExponentValue());
                        value = db.doubleValue();
                    }
                    valueArray[ii] = value;
                }
                
                // these values reflect the integer flag,
                // but are not influenced by the exponent flag
                axisValueArray = new SGAxisDoubleValue[valueArray.length];
                for (int ii = 0; ii < axisValueArray.length; ii++) {
                	axisValueArray[ii] = new SGAxisDoubleValue(valueArray[ii]);
                }
            }
        }
        
        double[] ret = new double[axisValueArray.length];
        for (int ii = 0; ii < axisValueArray.length; ii++) {
        	ret[ii] = axisValueArray[ii].getValue();
        }
        return ret;
    }
    
    /**
     * Calculate the numbers of scale in for the log scale.
     * 
     * @return an array of axis values
     */
    private double[] calcScaleValuesInLogScale() {
        // minimum and maximum values of axis range
        final SGTuple2d range = this.mAxis.getRange();
        final double min = range.x;
        final double max = range.y;

        // array of values for scale
        final int minOrder = SGUtilityNumber.getOrder(min);
        final int maxOrder = SGUtilityNumber.getOrder(max);
        List<Integer> list = new ArrayList<Integer>();
        for (int ii = minOrder; ii <= maxOrder; ii++) {
            list.add(Integer.valueOf(ii));
        }
        double[] axisValueArray = new double[list.size()];
        for (int ii = 0; ii < axisValueArray.length; ii++) {
            final int num = ((Integer) list.get(ii)).intValue();
            axisValueArray[ii] = SGUtilityNumber.getPowersOfTen(num);
        }
        return axisValueArray;
    }

    /**
     * Returns the tag name.
     * 
     * @return the tag name
     */
    public String getTagName() {
        return SGIFigureElementAxis.TAG_NAME_AXIS;
    }

    /**
     * Create an Element object.
     * 
     * @param document
     *           a Document object to append the Element object
     * @return an Element object
     */
    public Element createElement(final Document document, SGExportParameter params) {
        Element element = document.createElement(this.getTagName());
        if (this.writeProperty(element, params) == false) {
            return null;
        }
        return element;
    }

    /**
     * Write the properties to an given Element object.
     * 
     * @param el
     *          an Element object
     * @return true if succeeded
     */
    public boolean writeProperty(final Element el, SGExportParameter params) {
    	SGPropertyMap map = this.getPropertyFileMap(params);
    	map.setToElement(el);
    	return true;
    }

    protected abstract String getLineVisiblePropertyFileKey();

    protected abstract String getLineWidthPropertyFileKey();
    
    protected abstract String getLineColorPropertyFileKey();
    
    protected abstract String getSpaceLineAndNumberPropertyFileKey();

    protected abstract String getLineVisibleCommandKey();

    protected abstract String getLineWidthCommandKey();
    
    protected abstract String getLineColorCommandKey();
    
    protected abstract String getSpaceLineAndNumberCommandKey();

    /**
     * Read properties from a given Element object.
     * 
     * @param element
     *           an Element object
     * @param versionNumber
     *           version number
     * @return true if succeeded
     */
    protected boolean readProperties(final Element element, final String versionNumber) {
		String str = null;
		Number num = null;
		Boolean b = null;
		Color cl = null;

		// visible
		str = element.getAttribute(KEY_VISIBLE);
		if (str.length() == 0) {
			// for backward compatibility
			// version number <= 2.0.0
			str = element.getAttribute("AxisVisible");
		}
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			final boolean axisVisible = b.booleanValue();
			if (this.setVisible(axisVisible) == false) {
				return false;
			}
		}

		//
		// Axis Line
		//

        str = element.getAttribute(this.getLineVisiblePropertyFileKey());
        if (str.length() != 0) {
        	b = SGUtilityText.getBoolean(str);
        	if (b != null) {
        		if (this.setAxisLineVisible(b.booleanValue()) == false) {
        			return false;
        		}
        	}
        }

		str = element.getAttribute(this.getLineWidthPropertyFileKey());
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setAxisLineWidth(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(this.getLineColorPropertyFileKey());
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setAxisLineColor(cl) == false) {
				return false;
			}
		}

		str = element.getAttribute(this.getSpaceLineAndNumberPropertyFileKey());
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setSpaceAxisLineAndNumbers(num.floatValue(),
					unit.toString()) == false) {
				return false;
			}
		}

		//
		// Title
		//

		str = element.getAttribute(KEY_TITLE_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setTitleVisible(b.booleanValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TITLE_TEXT);
		if (str != null) {
			// modifies the text string for previous releases older than 2_0_0
			str = SGUtilityForFigureElementJava2D.modifyStringForCompatibility(str,
					versionNumber);
		}
		final String titleString = str;
		if (this.setTitleText(titleString) == false) {
			return false;
		}

		str = element.getAttribute(KEY_SPACE_TITLE_AND_NUMBERS);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setSpaceTitleAndNumbers(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TITLE_SHIFT_FROM_CENTER);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setTitleShiftFromCenter(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TITLE_FONT_NAME);
		if (str.length() != 0) {
			if (this.setTitleFontName(str) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TITLE_FONT_SIZE);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setTitleFontSize(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TITLE_FONT_STYLE);
		if (str.length() != 0) {
			Integer fontStyle = SGUtilityText.getFontStyle(str);
			if (fontStyle == null) {
				return false;
			}
			if (this.setTitleFontStyle(fontStyle.intValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TITLE_FONT_COLOR);
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setTitleFontColor(cl) == false) {
				return false;
			}
		}

		//
		// Number
		//

		str = element.getAttribute(KEY_NUMBER_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setNumbersVisible(b.booleanValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_NUMBER_INTEGER);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setNumbersInteger(b.booleanValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(SGIFigureElementAxis.KEY_NUMBER_ANGLE);
		if (str.length() != 0) {
			num = SGUtilityText.getFloat(str);
			if (num == null) {
				return false;
			}
			if (this.setNumbersAngle(num.floatValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setExponentVisible(b.booleanValue()) == false) {
				return false;
			}
		}

		// exponent value
		str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_VALUE);
		if (str.length() != 0) {
			num = SGUtilityText.getInteger(str);
			if (num == null) {
				return false;
			}
			if (this.setExponent(num.intValue()) == false) {
				return false;
			}
		}

		// exponent location
		str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_LOCATION_X);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setExponentLocationX(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}
		str = element.getAttribute(SGIFigureElementAxis.KEY_EXPONENT_LOCATION_Y);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setExponentLocationY(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_NUMBER_FONT_NAME);
		if (str.length() != 0) {
			if (this.setNumberFontName(str) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_NUMBER_FONT_SIZE);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setNumberFontSize(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_NUMBER_FONT_STYLE);
		if (str.length() != 0) {
			Integer fontStyle = SGUtilityText.getFontStyle(str);
			if (fontStyle == null) {
				return false;
			}
			if (this.setNumberFontStyle(fontStyle.intValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_NUMBER_FONT_COLOR);
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setNumberFontColor(cl) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_NUMBER_DATE_FORMAT);
		if (str.length() != 0) {
			if (SGDateUtility.isValidDateFormat(str) == false) {
				return false;
			}
			if (this.setNumberDateFormat(str) == false) {
				return false;
			}
		}

		//
		// Scale
		//

		str = element.getAttribute(KEY_AXIS_INVERT_COORDINATES);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setInvertedCoordinates(b.booleanValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_AUTO_CALC_NUMBER);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setCalculateAutomatically(b.booleanValue()) == false) {
				return false;
			}
		}

		//
		// Tick Mark
		//

		str = element.getAttribute(KEY_TICK_MARK_VISIBLE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setTickMarkVisible(b.booleanValue()) == false) {
				return false;
			}
		}
		
		str = element.getAttribute(KEY_TICK_MARK_BOTHSIDES);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			if (this.setTickMarkBothsides(b.booleanValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TICK_MARK_WIDTH);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setTickMarkWidth(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TICK_MARK_LENGTH);
		if (str.length() != 0) {
			StringBuffer unit = new StringBuffer();
			num = SGUtilityText.getNumber(str, unit);
			if (num == null) {
				return false;
			}
			if (this.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
				return false;
			}
			if (this.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
				return false;
			}
		} else {
			str = element.getAttribute(KEY_MAJOR_TICK_MARK_LENGTH);
			if (str.length() != 0) {
				StringBuffer unit = new StringBuffer();
				num = SGUtilityText.getNumber(str, unit);
				if (num == null) {
					return false;
				}
				if (this.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
					return false;
				}
			}
			str = element.getAttribute(SGIFigureElementAxis.KEY_MINOR_TICK_MARK_LENGTH);
			if (str.length() != 0) {
				StringBuffer unit = new StringBuffer();
				num = SGUtilityText.getNumber(str, unit);
				if (num == null) {
					return false;
				}
				if (this.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
					return false;
				}
			}
		}

		// Note: set this property after tick mark length is set
		str = element.getAttribute(KEY_TICK_MARK_INSIDE);
		if (str.length() != 0) {
			b = SGUtilityText.getBoolean(str);
			if (b == null) {
				return false;
			}
			float majorLength = Math.abs(this.getMajorTickMarkLength());
			float minorLength = Math.abs(this.getMinorTickMarkLength());
			if (!b) {
				majorLength *= -1;
				minorLength *= -1;
			}
			this.setMajorTickMarkLength(majorLength);
			this.setMinorTickMarkLength(minorLength);
		}

		str = element.getAttribute(KEY_MINOR_TICK_MARK_NUMBER);
		if (str.length() != 0) {
			num = SGUtilityText.getInteger(str);
			if (num == null) {
				return false;
			}
			if (this.setMinorTickMarkNumber(num.intValue()) == false) {
				return false;
			}
		}

		str = element.getAttribute(KEY_TICK_MARK_COLOR);
		if (str.length() != 0) {
			cl = SGUtilityText.parseColor(str);
			if (cl == null) {
				return false;
			}
			if (this.setTickMarkColor(cl) == false) {
				return false;
			}
		}

		return true;
    }
    
	protected boolean readScaleProperties(Element element, 
			String versionNumber, final boolean dateMode) {
		String str = null;
		Number num = null;
		SGDate date = null;
		
		//
		// scale type, minimum value and maximum value
		//
		
		int scaleType = -1;
		str = element.getAttribute(KEY_AXIS_SCALE_TYPE);
		if (str.length() != 0) {
			scaleType = SGUtilityText.getScaleType(str);
		}
		
		SGAxisValue minValue = null;
		str = element.getAttribute(KEY_AXIS_MIN_VALUE);
		if (str.length() != 0) {
			if (dateMode) {
				date = SGUtilityText.getDate(str);
				if (date != null) {
					minValue = new SGAxisDateValue(date);
				}
			} else {
				num = SGUtilityText.getDouble(str);
				if (num != null) {
					minValue = new SGAxisDoubleValue(num.doubleValue());
				}
			}
		}
		
		SGAxisValue maxValue = null;
		str = element.getAttribute(KEY_AXIS_MAX_VALUE);
		if (str.length() != 0) {
			if (dateMode) {
				date = SGUtilityText.getDate(str);
				if (date != null) {
					maxValue = new SGAxisDateValue(date);
				}
			} else {
				num = SGUtilityText.getDouble(str);
				if (num != null) {
					maxValue = new SGAxisDoubleValue(num.doubleValue());
				}
			}
		}
		
		if ((scaleType != -1) && (minValue != null)
				&& (maxValue != null)) {
			if (this.setScale(minValue, maxValue, scaleType) == false) {
				return false;
			}
		}

		// baseline value
		str = element.getAttribute(SGIFigureElementAxis.KEY_BASELINE_VALUE);
		if (str.length() != 0) {
			SGAxisValue baseline = null;
			if (dateMode) {
				date = SGUtilityText.getDate(str);
				if (date != null) {
					baseline = new SGAxisDateValue(date);
				}
			} else {
				num = SGUtilityText.getDouble(str);
				if (num != null) {
					baseline = new SGAxisDoubleValue(num.doubleValue());
				}
			}
			if (baseline != null) {
				if (this.setBaselineValue(baseline) == false) {
					return false;
				}
			}
		}

		// step value
		str = element.getAttribute(KEY_STEP_VALUE);
		if (str.length() != 0) {
			SGAxisStepValue step = null;
			if (dateMode) {
				Period p = SGUtilityText.getPeriod(str);
				if (p != null) {
					step = new SGAxisDateStepValue(p);
				}
			} else {
				num = SGUtilityText.getDouble(str);
				if (num != null) {
					step = new SGAxisDoubleStepValue(num.doubleValue());
				}
			}
			if (step != null) {
				if (this.setStepValue(step) == false) {
					return false;
				}
			}
		}

		return true;
	}

    public SGProperties getProperties() {
        AxisProperties p = new AxisProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }
    
    public boolean getProperties(SGProperties p) {
        AxisProperties ap = (AxisProperties) p;
        
        ap.axisVisible = this.isVisible();
        
        // Axis Line
    	ap.axisLineVisible = this.isAxisLineVisible();
        ap.axisLineWidth = this.getAxisLineWidth();
        ap.axisLineColor = this.getAxisLineColor();
        ap.spaceLineAndNumbers = this.getSpaceAxisLineAndNumbers();
        
        // Title
        ap.titleVisible = this.isTitleVisible();
        ap.titleText = this.getTitleString();
        ap.spaceTitleAndNumbers = this.getSpaceTitleAndNumbers();
        ap.titleShiftFromCenter = this.getTitleShiftFromCenter();
        ap.titleFontName = this.getTitleFontName();
        ap.titleFontSize = this.getTitleFontSize();
        ap.titleFontStyle = this.getTitleFontStyle();
        ap.titleFontColor = this.getTitleFontColor();
        
        // Scale
        ap.dateMode = this.getDateMode();
        ap.numberInteger = this.isNumbersInteger();
        ap.minValue = this.getMinValue();
        ap.maxValue = this.getMaxValue();
        ap.scaleType = this.getScaleType();
        ap.invertedCoordinates = this.isInvertCoordinates();
        ap.autoCalc = this.isScaleAuto();
        ap.stepValue = this.getScaleStep();
        ap.baselineValue = this.getScaleBase();
        ap.exponentVisible = this.isExponentVisible();
        ap.exponent = this.getExponentValue();
        
        // Number
        ap.numberVisible = this.isNumbersVisible();
        ap.numberAngle = this.getNumberAngle();
        ap.numberFontName = this.getNumberFontName();
        ap.numberFontSize = this.getNumberFontSize();
        ap.numberFontStyle = this.getNumberFontStyle();
        ap.numberFontColor = this.getNumberFontColor();
        ap.exponentLocationX = this.getExponentLocationX();
        ap.exponentLocationY = this.getExponentLocationY();
        ap.numberDateFormat = this.getNumberDateFormat();
        
        // Tick Mark
        ap.tickMarkVisible = this.isTickMarkVisible();
        ap.tickMarkBothsides = this.isTickMarkBothsides();
        ap.tickMarkWidth = this.getTickMarkWidth();
        ap.tickMarkLength = this.getMajorTickMarkLength();
        ap.minorTickMarkNumber = this.getMinorTickMarkNumber();
        ap.minorTickMarkLength = this.getMinorTickMarkLength();
        ap.tickMarkColor = this.getTickMarkColor();
        
        return true;
    }

    /**
     * 
     */
    public boolean setProperties(SGProperties p) {
        AxisProperties ap = (AxisProperties) p;

        this.setVisible(ap.axisVisible);
        
        // Axis Line
    	this.setAxisLineVisible(ap.axisLineVisible);
        this.setAxisLineWidth(ap.axisLineWidth);
        this.setAxisLineColor(ap.axisLineColor);
        this.setSpaceAxisLineAndNumbers(ap.spaceLineAndNumbers);
        
        // Title
        this.setTitleVisible(ap.titleVisible);
        this.setTitleText(ap.titleText);
        this.setSpaceTitleAndNumbers(ap.spaceTitleAndNumbers);
        this.setTitleShiftFromCenter(ap.titleShiftFromCenter);
        this.setTitleFontName(ap.titleFontName);
        this.setTitleFontStyle(ap.titleFontStyle);
        this.setTitleFontSize(ap.titleFontSize);
        this.setTitleFontColor(ap.titleFontColor);
        
        // Scale
        this.setDateMode(ap.dateMode);
        this.setNumbersInteger(ap.numberInteger);
        this.mAxis.setScale(ap.minValue, ap.maxValue, ap.scaleType);
        this.setInvertedCoordinates(ap.invertedCoordinates);
        this.setCalculateAutomatically(ap.autoCalc);
        this.setStepValue(ap.stepValue);
        this.setBaselineValue(ap.baselineValue);
        this.setExponentVisible(ap.exponentVisible);
        this.setExponent(ap.exponent);
        
        // Number
        this.setNumbersVisible(ap.numberVisible);
        this.setNumbersAngle(ap.numberAngle);
        this.setNumberFontName(ap.numberFontName);
        this.setNumberFontStyle(ap.numberFontStyle);
        this.setNumberFontSize(ap.numberFontSize);
        this.setNumberFontColor(ap.numberFontColor);
        this.setExponentLocationX(ap.exponentLocationX);
        this.setExponentLocationY(ap.exponentLocationY);
        this.setNumberDateFormat(ap.numberDateFormat);
        
        // Tick Mark
        this.setTickMarkVisible(ap.tickMarkVisible);
        this.setTickMarkBothsides(ap.tickMarkBothsides);
        this.setTickMarkWidth(ap.tickMarkWidth);
        this.setMajorTickMarkLength(ap.tickMarkLength);
        this.setMinorTickMarkNumber(ap.minorTickMarkNumber);
        this.setMinorTickMarkLength(ap.minorTickMarkLength);
        this.setTickMarkColor(ap.tickMarkColor);
        
        return true;
    }

    /**
	 * Sets the properties of this axis.
	 * 
	 * @param map
	 *            a map of properties
	 * @return the result of setting properties
	 */
    public SGPropertyResults setProperties(SGPropertyMap map) {
        SGPropertyResults result = new SGPropertyResults();
        
        // prepare
        if (this.prepare() == false) {
            return null;
        }
        
        result = this.setProperties(map, result);
        if (result == null) {
        	return null;
        }
        
        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.mAxisElement.notifyToRoot();
        this.mAxisElement.notifyChange();
        this.mAxisElement.repaint();
        
        return result;
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
    protected SGPropertyResults setProperties(SGPropertyMap map,
    		SGPropertyResults iResult) {
        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_AXIS_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setVisible(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_VISIBLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (this.getLineVisibleCommandKey().equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(this.getLineVisibleCommandKey(),
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setAxisLineVisible(b.booleanValue()) == false) {
                    result.putResult(this.getLineVisibleCommandKey(),
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(this.getLineVisibleCommandKey(),
                        SGPropertyResults.SUCCEEDED);
            } else if (this.getLineWidthCommandKey().equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(this.getLineWidthCommandKey(),
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setAxisLineWidth(num.floatValue(), unit.toString()) == false) {
                    result.putResult(this.getLineWidthCommandKey(),
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(this.getLineWidthCommandKey(),
                        SGPropertyResults.SUCCEEDED);
			} else if (this.getLineColorCommandKey().equalsIgnoreCase(key)) {
				Color cl = SGUtilityText.getColor(value);
				if (cl != null) {
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(this.getLineColorCommandKey(),
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				} else {
					cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(this.getLineColorCommandKey(),
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(this.getLineColorCommandKey(),
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(this.getLineColorCommandKey(),
						SGPropertyResults.SUCCEEDED);
            } else if (this.getSpaceLineAndNumberCommandKey().equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(this.getSpaceLineAndNumberCommandKey(), 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setSpaceAxisLineAndNumbers(num.floatValue(), unit.toString()) == false) {
                    result.putResult(this.getSpaceLineAndNumberCommandKey(), 
                    		SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(this.getSpaceLineAndNumberCommandKey(), 
                		SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_TITLE_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTitleVisible(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_TITLE_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TITLE_VISIBLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_TEXT.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_AXIS_TITLE_TEXT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (this.setTitleText(value) == false) {
                    result.putResult(COM_AXIS_TITLE_TEXT,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TITLE_TEXT,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_SPACE_TITLE_AND_NUMBER.equalsIgnoreCase(key)
            		|| COM_AXIS_SPACE_TO_TITLE.equalsIgnoreCase(key)) {
            	String k = map.getOriginalKey(key);
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setSpaceTitleAndNumbers(num.floatValue(), unit.toString()) == false) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k, SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_CENTER_SHIFT.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_TITLE_CENTER_SHIFT,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTitleShiftFromCenter(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_TITLE_CENTER_SHIFT,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TITLE_CENTER_SHIFT,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_FONT_NAME.equalsIgnoreCase(key)) {
            	final String name = SGUtility.findFontFamilyName(value);
            	if (name == null) {
                    result.putResult(COM_AXIS_TITLE_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (this.setTitleFontName(name) == false) {
                    result.putResult(COM_AXIS_TITLE_FONT_NAME,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TITLE_FONT_NAME,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_FONT_STYLE.equalsIgnoreCase(key)) {
                Integer style = SGUtilityText.getFontStyle(value);
                if (style == null) {
                    result.putResult(COM_AXIS_TITLE_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTitleFontStyle(style.intValue()) == false) {
                    result.putResult(COM_AXIS_TITLE_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TITLE_FONT_STYLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_FONT_SIZE.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_TITLE_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTitleFontSize(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_TITLE_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TITLE_FONT_SIZE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TITLE_FONT_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setTitleFontColor(cl) == false) {
                        result.putResult(COM_AXIS_TITLE_FONT_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_AXIS_TITLE_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setTitleFontColor(cl) == false) {
                        result.putResult(COM_AXIS_TITLE_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_AXIS_TITLE_FONT_COLOR,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_NUMBER_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumbersVisible(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_NUMBER_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_NUMBER_VISIBLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_INTEGER.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_NUMBER_INTEGER,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumbersInteger(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_NUMBER_INTEGER,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_NUMBER_INTEGER,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_ANGLE.equalsIgnoreCase(key)) {
                Float num = SGUtilityText.getFloat(value);
                if (num == null) {
                    result.putResult(COM_AXIS_NUMBER_ANGLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumbersAngle(num.intValue()) == false) {
                    result.putResult(COM_AXIS_NUMBER_ANGLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_NUMBER_ANGLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_EXPONENT_VISIBLE.equalsIgnoreCase(key)
            		|| COM_AXIS_NUMBER_FORMAT_EXPONENT_VISIBLE.equalsIgnoreCase(key)) {
            	String k = map.getOriginalKey(key);
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setExponentVisible(b.booleanValue()) == false) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k, SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_EXPONENT_VALUE.equalsIgnoreCase(key)
            		|| COM_AXIS_NUMBER_FORMAT_EXPONTNE_VALUE.equalsIgnoreCase(key)) {
            	String k = map.getOriginalKey(key);
                Integer num = SGUtilityText.getInteger(value);
                if (num == null) {
                    result.putResult(k,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setExponent(num.intValue()) == false) {
                    result.putResult(k,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_EXPONENT_LOCATION_X.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_EXPONENT_LOCATION_X,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setExponentLocationX(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_EXPONENT_LOCATION_X,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_EXPONENT_LOCATION_X,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_EXPONENT_LOCATION_Y.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_EXPONENT_LOCATION_Y,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setExponentLocationY(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_EXPONENT_LOCATION_Y,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_EXPONENT_LOCATION_Y,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_FONT_NAME.equalsIgnoreCase(key)) {
            	final String name = SGUtility.findFontFamilyName(value);
            	if (name == null) {
                    result.putResult(COM_AXIS_NUMBER_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (this.setNumberFontName(name) == false) {
                    result.putResult(COM_AXIS_NUMBER_FONT_NAME,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_NUMBER_FONT_NAME,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_FONT_STYLE.equalsIgnoreCase(key)) {
                Integer style = SGUtilityText.getFontStyle(value);
                if (style == null) {
                    result.putResult(COM_AXIS_NUMBER_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumberFontStyle(style.intValue()) == false) {
                    result.putResult(COM_AXIS_NUMBER_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_NUMBER_FONT_STYLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_FONT_SIZE.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_NUMBER_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumberFontSize(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_NUMBER_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_NUMBER_FONT_SIZE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_NUMBER_FONT_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setNumberFontColor(cl) == false) {
                        result.putResult(COM_AXIS_NUMBER_FONT_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_AXIS_NUMBER_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setNumberFontColor(cl) == false) {
                        result.putResult(COM_AXIS_NUMBER_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_AXIS_NUMBER_FONT_COLOR,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_SCALE_TYPE.equalsIgnoreCase(key)) {
                final int scaleType = SGUtilityText.getScaleType(value);
                if (scaleType == -1) {
                    result.putResult(COM_AXIS_SCALE_TYPE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setScaleType(scaleType) == false) {
                    result.putResult(COM_AXIS_SCALE_TYPE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_SCALE_TYPE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_INVERT_COORDINATES.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_INVERT_COORDINATES,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setInvertedCoordinates(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_INVERT_COORDINATES,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_INVERT_COORDINATES,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_SCALE_AUTO.equalsIgnoreCase(key)
            		|| COM_AXIS_TICK_MARK_AUTO.equalsIgnoreCase(key)) {
            	String k = map.getOriginalKey(key);
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setCalculateAutomatically(b.booleanValue()) == false) {
                    result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(k, SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TICK_MARK_VISIBLE.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_TICK_MARK_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTickMarkVisible(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_TICK_MARK_VISIBLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TICK_MARK_VISIBLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TICK_MARK_BOTHSIDES.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTickMarkBothsides(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TICK_MARK_BOTHSIDES.equalsIgnoreCase(key)) {
            	Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTickMarkBothsides(b.booleanValue()) == false) {
                    result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TICK_MARK_BOTHSIDES,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TICK_MARK_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_TICK_MARK_WIDTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTickMarkWidth(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_TICK_MARK_WIDTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TICK_MARK_WIDTH,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_MAJOR_TICK_MARK_LENGTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_MAJOR_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_MAJOR_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_MAJOR_TICK_MARK_LENGTH,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_MINOR_TICK_MARK_NUMBER.equalsIgnoreCase(key)) {
                Integer num = SGUtilityText.getInteger(value);
                if (num == null) {
                    result.putResult(COM_AXIS_MINOR_TICK_MARK_NUMBER,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setMinorTickMarkNumber(num.intValue()) == false) {
                    result.putResult(COM_AXIS_MINOR_TICK_MARK_NUMBER,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_MINOR_TICK_MARK_NUMBER,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_MINOR_TICK_MARK_LENGTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_MINOR_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_MINOR_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_MINOR_TICK_MARK_LENGTH,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TICK_MARK_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setTickMarkColor(cl) == false) {
                        result.putResult(COM_AXIS_TICK_MARK_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_AXIS_TICK_MARK_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setTickMarkColor(cl) == false) {
						result.putResult(COM_AXIS_TICK_MARK_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_AXIS_TICK_MARK_COLOR,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_TICK_MARK_LENGTH.equalsIgnoreCase(key)) {
            	// for backward compatibility (<= 2.0.0)
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setMajorTickMarkLength(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setMinorTickMarkLength(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_TICK_MARK_LENGTH,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_TICK_MARK_LENGTH,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_LINE_COLOR.equalsIgnoreCase(key)) {
            	// for backward compatibility (<= 2.0.0)
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setAxisLineColor(cl) == false) {
                        result.putResult(COM_AXIS_LINE_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setTickMarkColor(cl) == false) {
                        result.putResult(COM_AXIS_LINE_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
					if (cl == null) {
						result.putResult(COM_AXIS_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setAxisLineColor(cl) == false) {
						result.putResult(COM_AXIS_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
					if (this.setTickMarkColor(cl) == false) {
						result.putResult(COM_AXIS_LINE_COLOR,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
                }
                result.putResult(COM_AXIS_LINE_COLOR,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_FONT_NAME.equalsIgnoreCase(key)) {
            	// for backward compatibility (<= 2.0.0)
            	final String name = SGUtility.findFontFamilyName(value);
            	if (name == null) {
                    result.putResult(COM_AXIS_FONT_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                if (this.setTitleFontName(name) == false) {
                    result.putResult(COM_AXIS_FONT_NAME,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumberFontName(name) == false) {
                    result.putResult(COM_AXIS_FONT_NAME,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_FONT_NAME,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_FONT_STYLE.equalsIgnoreCase(key)) {
            	// for backward compatibility (<= 2.0.0)
                Integer style = SGUtilityText.getFontStyle(value);
                if (style == null) {
                    result.putResult(COM_AXIS_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTitleFontStyle(style.intValue()) == false) {
                    result.putResult(COM_AXIS_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumberFontStyle(style.intValue()) == false) {
                    result.putResult(COM_AXIS_FONT_STYLE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_FONT_STYLE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_FONT_SIZE.equalsIgnoreCase(key)) {
            	// for backward compatibility (<= 2.0.0)
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_AXIS_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setTitleFontSize(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setNumberFontSize(num.floatValue(), unit.toString()) == false) {
                    result.putResult(COM_AXIS_FONT_SIZE,
                            SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_AXIS_FONT_SIZE,
                        SGPropertyResults.SUCCEEDED);
            } else if (COM_AXIS_FONT_COLOR.equalsIgnoreCase(key)) {
            	// for backward compatibility (<= 2.0.0)
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setTitleFontColor(cl) == false) {
                        result.putResult(COM_AXIS_FONT_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setNumberFontColor(cl) == false) {
                        result.putResult(COM_AXIS_FONT_COLOR,
                                SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
                	if (cl == null) {
                        result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                    if (this.setTitleFontColor(cl) == false) {
                        result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                    if (this.setNumberFontColor(cl) == false) {
                        result.putResult(COM_AXIS_FONT_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                }
                result.putResult(COM_AXIS_FONT_COLOR,
                        SGPropertyResults.SUCCEEDED);
            }
        }
        
        // Note: after set tick mark length
    	// for backward compatibility (<= 2.0.0)
        if (map.getKeys().contains(COM_AXIS_TICK_MARK_INNER.toUpperCase())) {
        	String value = map.getValue(COM_AXIS_TICK_MARK_INNER);
	        Boolean b = SGUtilityText.getBoolean(value);
	        if (b == null) {
	            result.putResult(COM_AXIS_TICK_MARK_INNER,
	                    SGPropertyResults.INVALID_INPUT_VALUE);
	        } else {
		        float majorLength = Math.abs(this.getMajorTickMarkLength());
		        float minorLength = Math.abs(this.getMinorTickMarkLength());
		        if (!b) {
		        	majorLength *= -1;
		        	minorLength *= -1;
		        }
		        int status = SGPropertyResults.SUCCEEDED;
		        if (this.setMajorTickMarkLength(majorLength) == false) {
		        	status = SGPropertyResults.INVALID_INPUT_VALUE;
		        }
	        	if (this.setMinorTickMarkLength(minorLength) == false) {
		        	status = SGPropertyResults.INVALID_INPUT_VALUE;
	        	}
		        result.putResult(COM_AXIS_TICK_MARK_INNER, status);
	        }
        }

        return result;
    }
    
    /**
     * Returns the stroke for the axis line.
     * 
     * @return the stroke for the axis line
     */
    public SGStroke getAxisLineStroke() {
    	return this.mAxisLineStroke;
    }

    /**
     * Returns axis line width.
     * 
     * @return axis line width
     */
    public float getAxisLineWidth() {
    	return this.mAxisLineStroke.getLineWidth();
    }

    /**
     * Returns axis line width in given unit of length.
     * 
     * @param unit
     *           the unit of length
     * @return line width in given unit of length
     */
    public float getAxisLineWidth(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getAxisLineWidth(), unit);
    }

    /**
     * Sets axis line width.
     * 
     * @param lw
     *          line width
     * @return true if succeeded
     */
    public boolean setAxisLineWidth(final float lw) {
    	if (lw < 0.0f) {
    		throw new IllegalArgumentException("lw < 0.0f");
    	}
    	this.mAxisLineStroke.setLineWidth(lw);
    	return true;
    }
    
    /**
     * Sets the axis line width in a given unit.
     * 
     * @param lw
     *          the line width to set in a given unit
     * @param unit
     *          the unit of length
     * @return true if succeeded
     */
    public boolean setAxisLineWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        return this.setAxisLineWidth(lwNew);
    }
    
    /**
     * Returns color of the axis line.
     * 
     * @return color of the axis line
     */
    public Color getAxisLineColor() {
        return this.mAxisLineColor;
    }

    /**
     * Returns color of the tick marks.
     * 
     * @return color of the tick marks
     */
    public Color getTickMarkColor() {
        return this.mTickMarkColor;
    }

    /**
     * Returns the stroke for the tick marks.
     * 
     * @return the stroke for the tick marks
     */
    public SGStroke getTickMarkStroke() {
    	return this.mTickMarkStroke;
    }

    public float getSpaceToScale(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.mSpaceAxisLineAndNumbers, unit);
    }

    public float getSpaceToTitle(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.mSpaceTitleAndNumbers, unit);
    }

    public boolean setAxisLineColor(Color cl) {
        this.mAxisLineColor = cl;
        return true;
    }

    public boolean setTickMarkColor(Color cl) {
        this.mTickMarkColor = cl;
        return true;
    }
    
	/**
	 * Sets the space to the scale numbers.
	 * 
	 * @param space
	 *           the space to the scale numbers to set
	 * @return true if succeeded
	 */
    public boolean setSpaceToScale(final float space) {
    	this.mSpaceAxisLineAndNumbers = space;
    	return true;
    }

	/**
	 * Sets the space to the scale numbers in a given unit.
	 * 
	 * @param space
	 *           the space to the scale numbers to set in a given unit
	 * @param unit
	 *           an unit of length
	 * @return true if succeeded
	 */
    public boolean setSpaceToScale(float space, String unit) {
		final Float sNew = SGUtility.calcPropertyValue(space, unit,
				SPACE_UNIT, SPACE_BETWEEN_LINE_AND_NUMBERS_MIN, 
				SPACE_BETWEEN_LINE_AND_NUMBERS_MAX,
				SPACE_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		return this.setSpaceToScale(sNew.floatValue());
    }

	/**
	 * Sets the space to the title.
	 * 
	 * @param space
	 *           the space to the title to set
	 * @return true if succeeded
	 */
    public boolean setSpaceToTitle(final float space) {
    	this.mSpaceTitleAndNumbers = space;
    	return true;
    }
    
	/**
	 * Sets the space to the title in a given unit.
	 * 
	 * @param space
	 *           the space to the title to set in a given unit
	 * @param unit
	 *           an unit of length
	 * @return true if succeeded
	 */
    public boolean setSpaceToTitle(float space, String unit) {
		final Float sNew = SGUtility.calcPropertyValue(space, unit,
				SPACE_UNIT, SPACE_BETWEEN_TITLE_AND_NUMBERS_MIN, SPACE_BETWEEN_TITLE_AND_NUMBERS_MAX,
				SPACE_MINIMAL_ORDER);
		if (sNew == null) {
			return false;
		}
		return this.setSpaceToTitle(sNew.floatValue());
    }
 
    /**
     * Returns the font name of the title.
     * 
     * @return font name of the title
     */
    public String getTitleFontName() {
        return this.mTitle.getFontName();
    }

    /**
     * Returns the font style of the title.
     * 
     * @return font style of the title
     */
    public int getTitleFontStyle() {
        return this.mTitle.getFontStyle();
    }

    /**
     * Returns the font size of the title.
     * 
     * @return font size of the title
     */
    public float getTitleFontSize() {
        return this.mTitle.getFontSize();
    }

    /**
     * Returns the font size of the title in given unit.
     * 
     * @param unit
     *            unit for the font size
     * @return font size of the title in given unit
     */
    public float getTitleFontSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getTitleFontSize(),
                unit);
    }

    /**
     * Returns the color of title.
     * 
     * @return color of title
     */
    public Color getTitleFontColor() {
        return this.mTitle.getColor();
    }
    
    /**
     * Sets the font name of the title.
     * 
     * @param name
     *            font name of the title to set
     * @return true if succeeded
     */
    public boolean setTitleFontName(final String name) {
        this.mTitle.setFontName(name);
        return true;
    }

    /**
     * Set font style of the title.
     * 
     * @param style
     *            font style of the title
     * @return
     */
    public boolean setTitleFontStyle(final int style) {
        if (SGUtilityText.isValidFontStyle(style) == false) {
            return false;
        }
        this.mTitle.setFontStyle(style);
        return true;
    }

    /**
     * Sets the font size of the title.
     * 
     * @param size
     *            font size of the title
     * @return true if succeeded
     */
    public boolean setTitleFontSize(final float size) {
        if (size < 0) {
            throw new IllegalArgumentException("size < 0");
        }
        this.mTitle.setFontSize(size);
        return true;
    }
    
    /**
     * Sets the title font size in a given unit.
     * 
     * @param size
     *           the font size to set in a given unit
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setTitleFontSize(final float size, final String unit) {
        final Float sNew = SGUtility.getFontSize(size, unit);
        if (sNew == null) {
            return false;
        }
        return this.setTitleFontSize(sNew);
    }

    /**
     * Sets the font color of title.
     * 
     * @param color
     *           the font color to set
     * @return true if succeeded
     */
    public boolean setTitleFontColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.mTitle.setColor(color);
        return true;
    }

    /**
     * Returns the font name of the scale.
     * 
     * @return font name of the scale.
     */
    public String getNumberFontName() {
        return this.mNumberFontName;
    }

    /**
     * Returns the font style of the scale.
     * 
     * @return font style of the scale
     */
    public int getNumberFontStyle() {
        return this.mNumberFontStyle;
    }

    /**
     * Returns the font size of the scale.
     * 
     * @return font size of the scale
     */
    public float getNumberFontSize() {
        return this.mNumberFontSize;
    }

    /**
     * Returns the font size of the scale in given unit.
     * 
     * @param unit
     *            unit for the font size
     * @return font size of the scale in given unit
     */
    public float getNumberFontSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getNumberFontSize(),
                unit);
    }

    /**
     * Returns the color of scale numbers.
     * 
     * @return color of scale numbers
     */
    public Color getNumberFontColor() {
        return this.mNumberFontColor;
    }
    
    /**
     * Sets the scale font size in a given unit.
     * 
     * @param size
     *           the font size to set in a given unit
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setNumberFontSize(final float size, final String unit) {
        final Float sNew = SGUtility.getFontSize(size, unit);
        if (sNew == null) {
            return false;
        }
        return this.setNumberFontSize(sNew);
    }

    /**
     * Sets the scale font size.
     * 
     * @param size
     *           the font size to set
     * @return true if succeeded
     */
    public boolean setNumberFontSize(final float size) {
        if (size < 0) {
            throw new IllegalArgumentException("size < 0");
        }
        this.mNumberFontSize = size;
        return true;
    }

    /**
     * Sets the scale font style.
     * 
     * @param style
     *           the font style to set
     * @return true if succeeded
     */
    public boolean setNumberFontStyle(final int style) {
        if (SGUtilityText.isValidFontStyle(style) == false) {
            return false;
        }
        this.mNumberFontStyle = style;
        return true;
    }

    /**
     * 
     * @return
     */
    public boolean setNumberFontName(final String name) {
        this.mNumberFontName = name;
        return true;
    }

    /**
     * Sets the font color of scale.
     * 
     * @param color
     *           the font color to set
     * @return true if succeeded
     */
    public boolean setNumberFontColor(final Color color) {
        if (color == null) {
            throw new IllegalArgumentException("color == null");
        }
        this.mNumberFontColor = color;
        return true;
    }
    
    public float getTickMarkWidth() {
    	return this.mTickMarkStroke.getLineWidth();
    }

    public float getTickMarkWidth(String unit) {
    	final float lw = this.getTickMarkWidth();
        return (float) SGUtilityText.convertFromPoint(lw, unit);
    }
    
    public float getMajorTickMarkLength() {
    	return this.mMajorTickMarkLength;
    }
    
    public float getMajorTickMarkLength(String unit) {
        return (float) SGUtilityText.convertFromPoint(this.mMajorTickMarkLength, unit);
    }

    /**
     * Sets the tick mark width.
     * 
     * @param lw
     *           the tick mark width
     * @return true if succeeded
     */
    public boolean setTickMarkWidth(final float lw) {
        if (lw < 0.0f) {
            throw new IllegalArgumentException("lw < 0.0f");
        }
        this.mTickMarkStroke.setLineWidth(lw);
        return true;
    }

    /**
     * Sets the tick mark width in a given unit.
     * 
     * @param lw
     *          the tick mark width in a given unit
     * @param unit
     *          the unit of length
     * @return true if succeeded
     */
    public boolean setTickMarkWidth(final float lw, final String unit) {
        final Float lwNew = SGUtility.getLineWidth(lw, unit);
        if (lwNew == null) {
            return false;
        }
        return this.setTickMarkWidth(lwNew);
    }

    /**
     * Sets the length of major tick marks.
     * 
     * @param len
     *           the length of the major tick marks
     * @return true if succeeded
     */
    public boolean setMajorTickMarkLength(final float len) {
        this.mMajorTickMarkLength = len;
        return true;
    }

    /**
     * Sets the length of major tick marks in a given unit.
     * 
     * @param len
     *           the length of the major tick marks in a given unit
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setMajorTickMarkLength(final float len, final String unit) {
        final Float lNew = SGUtility.calcPropertyValue(len, unit, 
                TICK_MARK_LENGTH_UNIT, TICK_MARK_LENGTH_MIN, TICK_MARK_LENGTH_MAX, 
                TICK_MARK_LENGTH_MINIMAL_ORDER);
        if (lNew == null) {
            return false;
        }
        return this.setMajorTickMarkLength(lNew);
    }
    
    /**
     * Returns the space between axis line and numbers in the default unit.
     * 
     * @return the space between axis line and numbers in the default unit
     */
    public float getSpaceAxisLineAndNumbers() {
        return this.mSpaceAxisLineAndNumbers;
    }

    /**
     * Returns the space between axis line and numbers in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return the space between axis line and numbers in given unit
     */
    public float getSpaceAxisLineAndNumbers(final String unit) {
        return (float) SGUtilityText.convert(this.getSpaceAxisLineAndNumbers(), SPACE_UNIT, unit);
    }

    /**
     * Sets the space between axis line and numbers.
     * 
     * @param space
     *           the space to set
     * @return true if succeeded
     */
    public boolean setSpaceAxisLineAndNumbers(final float space) {
        this.mSpaceAxisLineAndNumbers = space;
        return true;
    }

    /**
     * Sets the space between axis line and numbers in given unit.
     * 
     * @param space
     *           the space to set
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setSpaceAxisLineAndNumbers(final float space,
            final String unit) {
        final double conv = SGUtilityText.convert(space, unit,
                SPACE_UNIT);
        if (conv < SPACE_BETWEEN_LINE_AND_NUMBERS_MIN) {
            return false;
        }
        if (conv > SPACE_BETWEEN_LINE_AND_NUMBERS_MAX) {
            return false;
        }
        return this.setSpaceAxisLineAndNumbers(
        		this.roundOffSpace((float) conv));
    }

    /**
     * Returns the space between numbers and title in the default unit.
     * 
     * @return the space between numbers and title in the default unit
     */
    public float getSpaceTitleAndNumbers() {
        return this.mSpaceTitleAndNumbers;
    }

    /**
     * Returns the space between numbers and title in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return the space between numbers and title in given unit
     */
    public float getSpaceTitleAndNumbers(final String unit) {
        return (float) SGUtilityText.convert(this.getSpaceTitleAndNumbers(), SPACE_UNIT, unit);
    }

    /**
     * Sets the space between numbers and title.
     * 
     * @param space
     *           the space to set
     * @return true if succeeded
     */
    public boolean setSpaceTitleAndNumbers(final float space) {
        this.mSpaceTitleAndNumbers = space;
        return true;
    }

    /**
     * Sets the space between numbers and title in given unit.
     * 
     * @param space
     *           the space to set
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setSpaceTitleAndNumbers(final float space, 
    		final String unit) {
        final double conv = SGUtilityText.convert(space, unit,
                SPACE_UNIT);
        if (conv < SPACE_BETWEEN_TITLE_AND_NUMBERS_MIN) {
            return false;
        }
        if (conv > SPACE_BETWEEN_TITLE_AND_NUMBERS_MAX) {
            return false;
        }
        return this.setSpaceTitleAndNumbers(
        		this.roundOffSpace((float) conv));
    }

    /**
     * Returns the shift of title from the center line.
     * 
     * @return the space between numbers and title
     */
    public float getTitleShiftFromCenter() {
    	return this.mTitleCenterShift;
    }

    /**
     * Returns the shift of title from the center line in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return the space between numbers and title in given unit
     */
    public float getTitleShiftFromCenter(final String unit) {
        return (float) SGUtilityText.convert(this.getTitleShiftFromCenter(), TITLE_SHIFT_UNIT, unit);
    }

    /**
     * Sets the shift of title from the center line.
     * 
     * @param shift
     *           the space to set
     * @return true if succeeded
     */
    public boolean setTitleShiftFromCenter(final float shift) {
        this.mTitleCenterShift = shift;
        return true;
    }

    /**
     * Sets the shift of title from the center line in given unit.
     * 
     * @param shift
     *           shift from the center line
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setTitleShiftFromCenter(final float shift, 
    		final String unit) {
        final double conv = SGUtilityText.convert(shift, unit,
                SPACE_UNIT);
        if (conv < TITLE_SHIFT_MIN) {
            return false;
        }
        if (conv > TITLE_SHIFT_MAX) {
            return false;
        }
        return this.setTitleShiftFromCenter(
        		this.roundOffSpace((float) conv));
    }
    
    /**
     * Returns the x-coordinate of the exponent symbol.
     * 
     * @return the x-coordinate of the exponent symbol
     */
    public float getExponentLocationX() {
    	return this.mExponentLocationX;
    }

    /**
     * Returns the y-coordinate of the exponent symbol.
     * 
     * @return the y-coordinate of the exponent symbol
     */
    public float getExponentLocationY() {
    	return this.mExponentLocationY;
    }

    /**
     * Returns the x-coordinate of the exponent symbol in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return Returns the x-coordinate of the exponent symbol in given unit
     */
    public float getExponentLocationX(final String unit) {
        return (float) SGUtilityText.convert(this.getExponentLocationX(), EXPONENT_LOCATION_UNIT, unit);
    }

    /**
     * Returns the y-coordinate of the exponent symbol in given unit.
     * 
     * @param unit
     *           the unit of length
     * @return Returns the y-coordinate of the exponent symbol in given unit
     */
    public float getExponentLocationY(final String unit) {
        return (float) SGUtilityText.convert(this.getExponentLocationY(), EXPONENT_LOCATION_UNIT, unit);
    }

    /**
     * Sets the x-coordinate of the exponent symbol.
     * 
     * @param x
     *           the x-coordinate
     * @return true if succeeded
     */
    public boolean setExponentLocationX(final float x) {
    	this.mExponentLocationX = x;
        return true;
    }

    /**
     * Sets the y-coordinate of the exponent symbol.
     * 
     * @param y
     *           the y-coordinate
     * @return true if succeeded
     */
    public boolean setExponentLocationY(final float y) {
    	this.mExponentLocationY = y;
        return true;
    }

    /**
     * Sets the x-coordinate of the exponent symbol in given unit.
     * 
     * @param x
     *           the x-coordinate
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setExponentLocationX(final float x, 
    		final String unit) {
        final double conv = SGUtilityText.convert(x, unit,
        		EXPONENT_LOCATION_UNIT);
        if (conv < EXPONENT_LOCATION_MIN) {
            return false;
        }
        if (conv > EXPONENT_LOCATION_MAX) {
            return false;
        }
        return this.setExponentLocationX(
        		this.roundOffExponentShift((float) conv));
    }
    
    /**
     * Sets the y-coordinate of the exponent symbol in given unit.
     * 
     * @param y
     *           the y-coordinate
     * @param unit
     *           the unit of length
     * @return true if succeeded
     */
    public boolean setExponentLocationY(final float y, 
    		final String unit) {
        final double conv = SGUtilityText.convert(y, unit,
        		EXPONENT_LOCATION_UNIT);
        if (conv < EXPONENT_LOCATION_MIN) {
            return false;
        }
        if (conv > EXPONENT_LOCATION_MAX) {
            return false;
        }
        return this.setExponentLocationY(
        		this.roundOffExponentShift((float) conv));
    }
    
    public boolean isHorizontal() {
    	return this.mAxisElement.isHorizontal(this.mAxis);
    }

    public boolean isVertical() {
    	return this.mAxisElement.isVertical(this.mAxis);
    }
    
    public boolean isNormal() {
    	return this.mAxisElement.isNormal(this.mAxis);
    }
    
    public SGProperties getMemento() {
        return this.getProperties();
    }

    public boolean setMemento(SGProperties p) {
        return this.setProperties(p);
    }
    
    public boolean isUndoable() {
        return this.mUndoManager.isUndoable();
    }

    public boolean isRedoable() {
        return this.mUndoManager.isRedoable();
    }

    public boolean initPropertiesHistory() {
        return this.mUndoManager.initPropertiesHistory();
    }

    public boolean setMementoBackward() {
        if (this.mUndoManager.setMementoBackward() == false) {
            return false;
        }
        if (this.createDrawingElements() == false) {
            return false;
        }
        this.mAxisElement.notifyChange();
        return true;
    }

    public boolean setMementoForward() {
        if (this.mUndoManager.setMementoForward() == false) {
            return false;
        }
        if (this.createDrawingElements() == false) {
            return false;
        }
        this.mAxisElement.notifyChange();
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

    public void initUndoBuffer() {
        this.mUndoManager.initUndoBuffer();
    }

    public boolean isChanged() {
        return this.mUndoManager.isChanged();
    }

    public void setChanged(boolean b) {
        this.mUndoManager.setChanged(b);
    }

    public boolean deleteForwardHistory() {
        return this.mUndoManager.deleteForwardHistory();
    }

    public boolean isChangedRoot() {
        return this.isChanged();
    }

    public void clearChanged() {
        this.setChanged(false);
    }

    public void notifyToRoot() {
        this.mAxisElement.notifyToRoot();
    }
    
    /**
     * Returns the name of location.
     * 
     * @return the name of location
     */
	@Override
	public String getLocationName() {
		return this.mAxisElement.getLocationName(this.mAxis);
	}
	
    protected boolean setChangedFocusedObjects() {
		SGProperties temp = this.mTemporaryProperties;
		if (temp != null) {
			SGProperties p = this.getProperties();
			if (p.equals(temp) == false) {
				this.setChanged(true);
			}
		}
		return true;
    }
    
    /**
     * Sets selected or deselected the object.
     * 
     * @param b
     *            true selects and false deselects the object
     */
    @Override
    public void setSelected(final boolean b) {
        this.setSelectedFlag(b);
    	this.mTemporaryProperties = b ? this.getProperties() : null;
    }
    
    void setSelectedFlag(final boolean b) {
    	this.mSelectedFlag = b;
    }

    /**
     * Returns whether this object is selected.
     * 
     * @return true if this object is selected
     */
    public boolean isSelected() {
        return this.mSelectedFlag;
    }
    
    protected boolean isChildFocusedObjectChanged() {
    	final boolean b = this.isChanged();
    	if (this.mTitle.isSelected()) {
    		return b;
    	} else if (this.mExponentSymbol.isSelected()) {
    		return b;
    	} else {
        	return false;
    	}
    }
    
    void updateSelectedFlag(final boolean b) {
    	this.mTemporaryProperties = b ? this.getProperties() : null;
    }
    
    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
	public String getCommandString() {
		StringBuffer sb = new StringBuffer();
		
		// creates the command for this axis
		String axisCommands = SGCommandUtility.createCommandString(COM_AXIS, 
				this.getLocationName(), this.getPropertyMap());
		sb.append(axisCommands);

		return sb.toString();
	}

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyMap() {
    	SGPropertyMap map = new SGPropertyMap();
    	
    	// visible
    	SGPropertyUtility.addProperty(map, COM_AXIS_VISIBLE, this.isVisible());

    	// frame line
    	this.addAxisLineProperties(map, 
    			this.getLineVisibleCommandKey(), this.getLineWidthCommandKey(), 
    			this.getSpaceLineAndNumberCommandKey(), this.getLineColorCommandKey());

    	// title
    	SGPropertyUtility.addQuotedStringProperty(map, COM_AXIS_TITLE_TEXT, this.getTitleString());
    	this.addTitleProperties(map, COM_AXIS_TITLE_VISIBLE, COM_AXIS_SPACE_TITLE_AND_NUMBER, 
    			COM_AXIS_TITLE_CENTER_SHIFT, COM_AXIS_TITLE_FONT_NAME, 
    			COM_AXIS_TITLE_FONT_SIZE, COM_AXIS_TITLE_FONT_STYLE, COM_AXIS_TITLE_FONT_COLOR);

    	// number
    	this.addNumberProperties(map, COM_AXIS_NUMBER_VISIBLE, COM_AXIS_NUMBER_INTEGER, 
    			COM_AXIS_NUMBER_ANGLE, COM_AXIS_EXPONENT_VISIBLE, COM_AXIS_EXPONENT_VALUE, 
    			COM_AXIS_EXPONENT_LOCATION_X, COM_AXIS_EXPONENT_LOCATION_Y, COM_AXIS_NUMBER_FONT_NAME, 
    			COM_AXIS_NUMBER_FONT_SIZE, COM_AXIS_NUMBER_FONT_STYLE, COM_AXIS_NUMBER_FONT_COLOR);

    	// scale
    	StringBuffer sbScale = new StringBuffer();
    	sbScale.append('(');
    	sbScale.append(this.getMinValue());
    	sbScale.append(',');
    	sbScale.append(this.getMaxValue());
    	sbScale.append(',');
    	sbScale.append(SGUtilityText.getScaleTypeName(this.getScaleType()));
    	sbScale.append(')');
    	SGPropertyUtility.addProperty(map, COM_AXIS_SCALE_RANGE, 
    			sbScale.toString());
		this.addScaleProperties(map, COM_AXIS_INVERT_COORDINATES,
				COM_AXIS_SCALE_AUTO, COM_AXIS_SCALE_STEP, COM_AXIS_SCALE_BASE);
    	
    	// tick mark
    	this.addTickMarkProperties(map, COM_AXIS_TICK_MARK_VISIBLE, COM_AXIS_TICK_MARK_BOTHSIDES, 
    			COM_AXIS_TICK_MARK_WIDTH, COM_AXIS_MAJOR_TICK_MARK_LENGTH, COM_AXIS_MINOR_TICK_MARK_LENGTH, 
    			COM_AXIS_MINOR_TICK_MARK_NUMBER, COM_AXIS_TICK_MARK_COLOR);
    	
    	return map;
    }

    /**
     * Creates and returns the map of properties for the property file.
     * 
     * @return the map of properties
     */
    public SGPropertyMap getPropertyFileMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
    	
    	// visible
    	SGPropertyUtility.addProperty(map, KEY_VISIBLE, this.isVisible());

    	// axis line
    	this.addAxisLineProperties(map, 
    			this.getLineVisibleCommandKey(), this.getLineWidthPropertyFileKey(), 
    			this.getSpaceLineAndNumberPropertyFileKey(), this.getLineColorPropertyFileKey());

        // Title
    	SGPropertyUtility.addProperty(map, KEY_TITLE_TEXT, this.getTitleString());
    	this.addTitleProperties(map, KEY_TITLE_VISIBLE, KEY_SPACE_TITLE_AND_NUMBERS, 
    			KEY_TITLE_SHIFT_FROM_CENTER, KEY_TITLE_FONT_NAME, 
    			KEY_TITLE_FONT_SIZE, KEY_TITLE_FONT_STYLE, KEY_TITLE_FONT_COLOR);
    	
        // Number
    	this.addNumberProperties(map, KEY_NUMBER_VISIBLE, KEY_NUMBER_INTEGER, 
    			KEY_NUMBER_ANGLE, KEY_EXPONENT_VISIBLE, KEY_EXPONENT_VALUE, 
    			KEY_EXPONENT_LOCATION_X, KEY_EXPONENT_LOCATION_Y, KEY_NUMBER_FONT_NAME, 
    			KEY_NUMBER_FONT_SIZE, KEY_NUMBER_FONT_STYLE, KEY_NUMBER_FONT_COLOR);

        // Scale
    	SGPropertyUtility.addProperty(map, KEY_AXIS_MIN_VALUE, 
    			this.mAxis.getMinValue());
    	SGPropertyUtility.addProperty(map, KEY_AXIS_MAX_VALUE, 
    			this.mAxis.getMaxValue());
    	SGPropertyUtility.addProperty(map, KEY_AXIS_SCALE_TYPE, 
    			SGUtilityText.getScaleTypeName(this.getScaleType()));
    	this.addScaleProperties(map, KEY_AXIS_INVERT_COORDINATES, 
    			KEY_AUTO_CALC_NUMBER, KEY_STEP_VALUE, KEY_BASELINE_VALUE);

        // Tick Mark
    	this.addTickMarkProperties(map, KEY_TICK_MARK_VISIBLE, KEY_TICK_MARK_BOTHSIDES, 
    			KEY_TICK_MARK_WIDTH, KEY_MAJOR_TICK_MARK_LENGTH, KEY_MINOR_TICK_MARK_LENGTH, 
    			KEY_MINOR_TICK_MARK_NUMBER, KEY_TICK_MARK_COLOR);

    	return map;
    }
    
    private void addAxisLineProperties(SGPropertyMap map, String lineVisibleKey,
    		String lineWidthKey, String spaceKey, String colorKey) {
    	SGPropertyUtility.addProperty(map, lineVisibleKey, 
    			this.isAxisLineVisible());
    	SGPropertyUtility.addProperty(map, lineWidthKey,
    			SGUtility.getExportLineWidth(this.getAxisLineWidth(LINE_WIDTH_UNIT)),
    			LINE_WIDTH_UNIT);
		SGPropertyUtility.addProperty(map, spaceKey,
				SGUtility.getExportValue(
						this.getSpaceAxisLineAndNumbers(SPACE_UNIT),
						SPACE_MINIMAL_ORDER), SPACE_UNIT);
    	SGPropertyUtility.addProperty(map, colorKey, this.mAxisLineColor);
    }
    
    private void addTitleProperties(SGPropertyMap map, String visibleKey,
    		String spaceKey, String centerShiftKey, String fontNameKey,
    		String fontSizeKey, String fontStyleKey, String fontColorKey) {
    	SGPropertyUtility.addProperty(map, visibleKey, this.isTitleVisible());
    	SGPropertyUtility.addProperty(map, spaceKey,
    			SGUtility.getExportValue(
    					this.getSpaceTitleAndNumbers(SPACE_UNIT), 
    					SPACE_MINIMAL_ORDER), SPACE_UNIT);
    	SGPropertyUtility.addProperty(map, centerShiftKey,
    			SGUtility.getExportValue(
    					this.getTitleShiftFromCenter(TITLE_SHIFT_UNIT), 
    					TITLE_SHIFT_MINIMAL_ORDER), TITLE_SHIFT_UNIT);
    	SGPropertyUtility.addProperty(map, fontNameKey, this.getTitleFontName());
    	SGPropertyUtility.addProperty(map, fontSizeKey,
    			SGUtility.getExportFontSize(this.getTitleFontSize(FONT_SIZE_UNIT)),
    			FONT_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, fontStyleKey, 
    			SGUtilityText.getFontStyleName(this.getTitleFontStyle()));
    	SGPropertyUtility.addProperty(map, fontColorKey, this.getTitleFontColor());
    }
    
    private void addNumberProperties(SGPropertyMap map, String numberVisibleKey,
    		String integerKey, String angleKey, String exponentVisibleKey,
    		String exponentValueKey, String exponentXKey, String exponentYKey,
    		String fontNameKey, String fontSizeKey, String fontStyleKey,
    		String fontColorKey) {
    	SGPropertyUtility.addProperty(map, numberVisibleKey, this.isNumbersVisible());
    	SGPropertyUtility.addProperty(map, integerKey, this.isNumbersInteger());
    	SGPropertyUtility.addProperty(map, angleKey, this.getNumberAngle());
    	SGPropertyUtility.addProperty(map, exponentVisibleKey, this.isExponentVisible());
    	SGPropertyUtility.addProperty(map, exponentValueKey, this.getExponentValue());
    	SGPropertyUtility.addProperty(map, exponentXKey,
    			SGUtility.getExportValue(
    					this.getExponentLocationX(EXPONENT_LOCATION_UNIT), 
    					EXPONENT_LOCATION_MINIMAL_ORDER), EXPONENT_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, exponentYKey,
    			SGUtility.getExportValue(
    					this.getExponentLocationY(EXPONENT_LOCATION_UNIT), 
    					EXPONENT_LOCATION_MINIMAL_ORDER), EXPONENT_LOCATION_UNIT);
    	SGPropertyUtility.addProperty(map, fontNameKey, this.getNumberFontName());
    	SGPropertyUtility.addProperty(map, fontSizeKey, 
    			SGUtility.getExportFontSize(this.getNumberFontSize(FONT_SIZE_UNIT)),
    			FONT_SIZE_UNIT);
    	SGPropertyUtility.addProperty(map, fontStyleKey, 
    			SGUtilityText.getFontStyleName(this.getNumberFontStyle()));
    	SGPropertyUtility.addProperty(map, fontColorKey, this.getNumberFontColor());
    }
    
	private void addScaleProperties(SGPropertyMap map, String invertKey,
			String autoKey, String stepKey, String baseKey) {
    	SGPropertyUtility.addProperty(map, invertKey, 
    			this.isInvertCoordinates());
    	SGPropertyUtility.addProperty(map, autoKey, this.isScaleAuto());
    	SGPropertyUtility.addProperty(map, stepKey, this.getScaleStep());
    	SGPropertyUtility.addProperty(map, baseKey, this.getScaleBase());
    }
    
	private void addTickMarkProperties(SGPropertyMap map, String visibleKey,
			String bothsidesKey, String widthKey, String majorLengthKey, String minorLengthKey,
			String minorNumberKey, String colorKey) {
    	SGPropertyUtility.addProperty(map, visibleKey, this.isTickMarkVisible());
    	SGPropertyUtility.addProperty(map, bothsidesKey, this.isTickMarkBothsides());
    	SGPropertyUtility.addProperty(map, widthKey,
    			SGUtility.getExportLineWidth(
    					this.getTickMarkWidth(LINE_WIDTH_UNIT)), LINE_WIDTH_UNIT);
    	SGPropertyUtility.addProperty(map, majorLengthKey,
    			SGUtility.getExportValue(
    					this.getMajorTickMarkLength(TICK_MARK_LENGTH_UNIT), 
    					TICK_MARK_LENGTH_MINIMAL_ORDER), TICK_MARK_LENGTH_UNIT);
    	SGPropertyUtility.addProperty(map, minorLengthKey,
    			SGUtility.getExportValue(
    					this.getMinorTickMarkLength(TICK_MARK_LENGTH_UNIT), 
    					TICK_MARK_LENGTH_MINIMAL_ORDER), TICK_MARK_LENGTH_UNIT);
    	SGPropertyUtility.addProperty(map, minorNumberKey, this.getMinorTickMarkNumber());
    	SGPropertyUtility.addProperty(map, colorKey, this.getTickMarkColor());
	}
	
    /**
     * Returns the flag for date mode.
     * 
     * @return the flag for date mode
     */
	@Override
	public boolean getDateMode() {
		return this.mAxis.getDateMode();
	}
	
    /**
     * Sets the flag for date mode.
     * 
     * @param b
     *          the flag to set
     * @param true if succeeded
     */
	@Override
	public boolean setDateMode(final boolean b) {
		this.mAxis.setDateMode(b);
		return true;
	}
	
	public boolean isAxisLineVisible() {
		return this.mAxisLineVisible;
	}
	
	public boolean setAxisLineVisible(final boolean b) {
		this.mAxisLineVisible = b;
		return true;
	}
	
    public boolean setNumberDateFormat(final String format) {
        this.mDateFormat = format;
        return true;
    }
    
    public String getNumberDateFormat() {
    	return this.mDateFormat;
    }
   
    protected void setScaleMinValue(SGPropertyMap map,
    		String key, String value, final boolean dateMode, 
    		SGPropertyResults result) {
    	final SGAxisValue axisValue;
    	if (dateMode) {
    		SGDate date = SGUtilityText.getDate(value);
    		if (date == null) {
                result.putResult(COM_AXIS_SCALE_MIN_VALUE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
    		}
            axisValue = new SGAxisDateValue(date);
    	} else {
            Double num = SGUtilityText.getDouble(value);
            if (num == null) {
                result.putResult(COM_AXIS_SCALE_MIN_VALUE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                result.putResult(COM_AXIS_SCALE_MIN_VALUE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            axisValue = new SGAxisDoubleValue(num);
    	}
        if (this.setScale(axisValue, null) == false) {
            result.putResult(COM_AXIS_SCALE_MIN_VALUE,
                    SGPropertyResults.INVALID_INPUT_VALUE);
            return;
        }
        result.putResult(COM_AXIS_SCALE_MIN_VALUE,
                SGPropertyResults.SUCCEEDED);
    }
    
    protected void setScaleMaxValue(SGPropertyMap map,
    		String key, String value, final boolean dateMode, 
    		SGPropertyResults result) {
    	final SGAxisValue axisValue;
    	if (dateMode) {
    		SGDate date = SGUtilityText.getDate(value);
    		if (date == null) {
                result.putResult(COM_AXIS_SCALE_MAX_VALUE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
    		}
            axisValue = new SGAxisDateValue(date);
    	} else {
            Double num = SGUtilityText.getDouble(value);
            if (num == null) {
                result.putResult(COM_AXIS_SCALE_MAX_VALUE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                result.putResult(COM_AXIS_SCALE_MAX_VALUE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            axisValue = new SGAxisDoubleValue(num);
    	}
        if (this.setScale(null, axisValue) == false) {
            result.putResult(COM_AXIS_SCALE_MAX_VALUE,
                    SGPropertyResults.INVALID_INPUT_VALUE);
            return;
        }
		result.putResult(COM_AXIS_SCALE_MAX_VALUE, 
				SGPropertyResults.SUCCEEDED);
    }

    protected void setScaleRange(SGPropertyMap map,
    		String key, String value, final boolean dateMode, 
    		SGPropertyResults result) {
        String[] strArray = SGUtilityText.getStringsInBracket(value);
        if (strArray == null) {
            result.putResult(COM_AXIS_SCALE_RANGE,
                    SGPropertyResults.INVALID_INPUT_VALUE);
            return;
        }
        final SGAxisValue min;
        final SGAxisValue max;
        if (dateMode) {
        	SGDate dMin = null;
        	SGDate dMax = null;
            if (strArray.length >= 2) {
                dMin = SGUtilityText.getDate(strArray[0]);
                dMax = SGUtilityText.getDate(strArray[1]);
            }
            if (dMin == null || dMax == null) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            min = new SGAxisDateValue(dMin);
            max = new SGAxisDateValue(dMax);
        } else {
            Double dMin = null;
            Double dMax = null;
            if (strArray.length >= 2) {
                dMin = SGUtilityText.getDouble(strArray[0]);
                dMax = SGUtilityText.getDouble(strArray[1]);
            }
            if (dMin == null || dMax == null) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (SGUtility.isValidPropertyValue(dMin.doubleValue()) == false) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (SGUtility.isValidPropertyValue(dMax.doubleValue()) == false) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            min = new SGAxisDoubleValue(dMin);
            max = new SGAxisDoubleValue(dMax);
        }
        if (strArray.length == 2) {
            if (this.setScale(min, max) == false) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
        } else if (strArray.length == 3) {
            final int scaleType = SGUtilityText.getScaleType(strArray[2]);
            if (scaleType == -1) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (this.setScale(min, max, scaleType) == false) {
                result.putResult(COM_AXIS_SCALE_RANGE,
                        SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
        } else {
            result.putResult(COM_AXIS_SCALE_RANGE,
                    SGPropertyResults.INVALID_INPUT_VALUE);
            return;
        }
        result.putResult(COM_AXIS_SCALE_RANGE,
                SGPropertyResults.SUCCEEDED);
    }
    
    protected void setScaleBaselineValue(SGPropertyMap map,
    		String key, String value, final boolean dateMode, 
    		SGPropertyResults result) {
    	String k = map.getOriginalKey(key);
    	final SGAxisValue axisValue;
    	if (dateMode) {
    		SGDate date = SGUtilityText.getDate(value);
            if (date == null) {
                result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            axisValue = new SGAxisDateValue(date);
    	} else {
            Double num = SGUtilityText.getDouble(value);
            if (num == null) {
                result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            axisValue = new SGAxisDoubleValue(num.doubleValue());
    	}
        if (this.setBaselineValue(axisValue) == false) {
            result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
            return;
        }
        result.putResult(k, SGPropertyResults.SUCCEEDED);
    }

    protected void setScaleStepValue(SGPropertyMap map,
    		String key, String value, final boolean dateMode, 
    		SGPropertyResults result) {
    	String k = map.getOriginalKey(key);
    	final SGAxisStepValue step;
    	if (dateMode) {
    		Period p = SGUtilityText.getPeriod(value);
    		if (p == null) {
                result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                return;
    		}
            step = new SGAxisDateStepValue(p);
    	} else {
            Double num = SGUtilityText.getDouble(value);
            if (num == null) {
                result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            if (SGUtility.isValidPropertyValue(num.doubleValue()) == false) {
                result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
                return;
            }
            step = new SGAxisDoubleStepValue(num.doubleValue());
    	}
        if (this.setStepValue(step) == false) {
            result.putResult(k, SGPropertyResults.INVALID_INPUT_VALUE);
            return;
        }
        result.putResult(k, SGPropertyResults.SUCCEEDED);
    }
    
    boolean hasDraggableAxisScaleElement() {
    	return this.mAxisElement.hasDraggableAxisScaleElement();
    }
    
    void addAxisScaleChangeListener(AxisScaleChangeListener l) {
    	this.mAxisScaleChangeListenerList.add(l);
    }
    
    void removeAxisScaleChangeListener(AxisScaleChangeListener l) {
    	this.mAxisScaleChangeListenerList.remove(l);
    }
    
    double getStepValue() {
        SGAxisStepValue step = this.getScaleStep();
        final double value;
        if (step instanceof SGAxisDoubleStepValue) {
        	value = ((SGAxisDoubleStepValue) step).getValue();
        } else if (step instanceof SGAxisDateStepValue) {
        	Period p = ((SGAxisDateStepValue) step).getPeriod();
        	value = SGDateUtility.toApproximateDateValue(p);
        } else {
        	throw new Error("This cannot happen.");
        }
        return value;
    }
    
    public static interface AxisScaleChangeListener {
    	
    	public void axisScaleChanged(SGAxis axis);
    	
    }
}

// axis line
class ElementLineAxis extends SGSimpleLine2D {
    ElementLineAxis() {
        super();
    }
}

//axis title
class ElementStringTitle extends
        SGDrawingElementString2DExtended implements SGIMovable, SGIFigureElementAxisConstants {
	
	SGAxisElement mAxisElement = null;
	
    ElementStringTitle(final SGAxisElement aElement) {
        super();
        this.mAxisElement = aElement;
    }
    
    private boolean mSelected = false;

	@Override
	public void setSelected(boolean b) {
		this.setSelectedFlag(b);
		this.mAxisElement.updateSelectedFlag(b);
	}

    void setSelectedFlag(final boolean b) {
    	this.mSelected = b;
    }

	@Override
	public boolean isSelected() {
		return this.mSelected;
	}
	
    List<Point2D> getAnchorPoints() {
        List<Point2D> list = new ArrayList<Point2D>();
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

	@Override
	public void translate(final float dx, final float dy) {
		final float factor = SGIConstants.CM_POINT_RATIO / this.mMagnification;
		final float space = this.mAxisElement.getSpaceTitleAndNumbers();
		final float centerShift = this.mAxisElement.getTitleShiftFromCenter();
		final float spaceDiff, centerShiftDiff;
		final int loc = this.mAxisElement.getLocationInPlane();
        switch(loc) {
        case AXIS_HORIZONTAL_1:
        case AXIS_NORMAL_HORIZONTAL_LOWER:
            // horizontal axes
        	spaceDiff = dy;
        	centerShiftDiff = dx;
        	break;
        case AXIS_HORIZONTAL_2:
        case AXIS_NORMAL_HORIZONTAL_UPPER:
            // horizontal axes
        	spaceDiff = - dy;
        	centerShiftDiff = dx;
        	break;
        case AXIS_VERTICAL_1:
        case AXIS_NORMAL_VERTICAL_LEFT:
            // vertical axes
        	spaceDiff = - dx;
        	centerShiftDiff = dy;
        	break;
        case AXIS_VERTICAL_2:
        case AXIS_NORMAL_VERTICAL_RIGHT:
            // vertical axes
        	spaceDiff = dx;
        	centerShiftDiff = dy;
        	break;
       	default:
            throw new Error("Invalid location: " + loc);
        }

        final float spaceNew = this.mAxisElement.roundOffSpace(space + factor * spaceDiff);
        final float centerNew = this.mAxisElement.roundOffTitleShift(centerShift + factor * centerShiftDiff);
		this.mAxisElement.setSpaceTitleAndNumbers(spaceNew);
		this.mAxisElement.setTitleShiftFromCenter(centerNew);
		
		final float spaceDiffNew = (spaceNew - space) / factor;
		final float centerShiftDiffNew = (centerNew - centerShift) / factor;
		
		final float dxNew, dyNew;
        switch(loc) {
        case AXIS_HORIZONTAL_1:
        case AXIS_NORMAL_HORIZONTAL_LOWER:
            // horizontal axes
        	dyNew = spaceDiffNew;
        	dxNew = centerShiftDiffNew;
        	break;
        case AXIS_HORIZONTAL_2:
        case AXIS_NORMAL_HORIZONTAL_UPPER:
            // horizontal axes
        	dyNew = - spaceDiffNew;
        	dxNew = centerShiftDiffNew;
        	break;
        case AXIS_VERTICAL_1:
        case AXIS_NORMAL_VERTICAL_LEFT:
            // vertical axes
        	dxNew = - spaceDiffNew;
        	dyNew = centerShiftDiffNew;
        	break;
        case AXIS_VERTICAL_2:
        case AXIS_NORMAL_VERTICAL_RIGHT:
            // vertical axes
        	dxNew = spaceDiffNew;
        	dyNew = centerShiftDiffNew;
        	break;
       	default:
            throw new Error("Invalid location: " + loc);
        }
		
		this.setLocation(this.getX() + dxNew, this.getY() + dyNew);
	}
}

// line for tick marks
class ElementLineTickMark extends SGDrawingElementLine2D implements AxisScaleElement {
    
    SGTuple2f mStart = new SGTuple2f();
    
    SGTuple2f mEnd = new SGTuple2f();
    
    SGAxisElement mAxisElement = null;
    
    // value in scale
    double mValue;

    ElementLineTickMark(final SGTuple2f start, final SGTuple2f end, SGAxisElement axisElement) {
        super();
        this.mAxisElement = axisElement;
        this.mStart.setValues(start);
        this.mEnd.setValues(end);
    }

    ElementLineTickMark(final ElementLineTickMark el) {
        super();
        this.mValue = el.mValue;
    }

    @Override
    public Color getColor() {
        return this.mAxisElement.getTickMarkColor();
    }

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
        return this.mAxisElement.mTickMarkStroke;
    }

    @Override
    public boolean setColor(Color cl) {
        // do nothing
        return true;
    }

    @Override
    public boolean setLineType(int type) {
        // do nothing
        return true;
    }

    @Override
    public boolean setLineWidth(float width) {
        // do nothing
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
        return this.mAxisElement.getMagnification();
    }

    @Override
    public boolean setMagnification(float mag) {
        // do nothing
        return true;
    }
}

// string for scale numbers
class ElementStringNumber extends SGDrawingElementString2DExtended implements AxisScaleElement {
	
    // value in scale
    double mValue;
    final int mLocationInPlane;

    ElementStringNumber(final String str, final String fontName,
            final int fontStyle, final float fontSize,
            final Color fontColor, final float mag, final float angle,
            final int locationInPlane) {
        super(str, fontName, fontStyle, fontSize, fontColor, mag, angle);
        this.mLocationInPlane = locationInPlane;
    }

    ElementStringNumber(final ElementStringNumber el) {
        super(el);
        this.mValue = el.mValue;
        this.mLocationInPlane = el.mLocationInPlane;
    }
    
    private float[] movePositionForRotation() {
        float thisx = this.getX();
        float thisy = this.getY();
        float angle = this.getAngle();
        Rectangle2D rect = super.getElementBounds();
        if (thisy > rect.getY()) {
            float diffy = thisy-(float)rect.getY();
            this.setLocation(thisx, thisy+diffy);
        }
        if (angle>90 && angle<=120) {
            float diffx = (float)rect.getWidth();
            this.setLocation(thisx+diffx/30.0f*(angle-90.0f), thisy);
        } else if (angle>120) {
            float diffx = (float)rect.getWidth();
            this.setLocation(thisx+diffx, thisy);
        } else if (angle<0.0 && angle>=-90.0f) {
            float diffx = (float)rect.getWidth();
            this.setLocation(thisx+diffx/90.0f*(-angle), thisy);
        } else if (angle<-90.0f) {
            float diffx = (float)rect.getWidth();
            this.setLocation(thisx+diffx, thisy);
        }
            
        if (this.mLocationInPlane == SGIFigureElementAxis.AXIS_VERTICAL_1 ||
                mLocationInPlane == SGIFigureElementAxis.AXIS_NORMAL_VERTICAL_LEFT) {
            Rectangle2D replacedRect = super.getElementBounds();
            float diffx = (float)(replacedRect.getX()+replacedRect.getWidth()-thisx-rect.getWidth());
            if (diffx>0) {
                this.setLocation(this.getX()-diffx, thisy);
            }
        } else if (this.mLocationInPlane == SGIFigureElementAxis.AXIS_VERTICAL_2 ||
                mLocationInPlane == SGIFigureElementAxis.AXIS_NORMAL_VERTICAL_RIGHT) {
            Rectangle2D replacedRect = super.getElementBounds();
            float diffx = (float)(replacedRect.getX()-thisx);
            if (diffx>0) {
                this.setLocation(this.getX()-diffx, thisy);
            }
        }
        
        return new float[] { thisx, thisy };
    }
    
    @Override
    public void paint(final Graphics2D g2d) {
        float[] originalPosition = movePositionForRotation();
        super.paint(g2d);
        this.setLocation(originalPosition[0], originalPosition[1]);
    }
    
    @Override
    public Rectangle2D getElementBounds() {
        float[] originalPosition = movePositionForRotation();
        Rectangle2D rect = super.getElementBounds();
        this.setLocation(originalPosition[0], originalPosition[1]);
        return rect;
    }
}

// exponent symbol
class ElementStringExponent extends
        SGDrawingElementString2DExtended implements SGIMovable, SGIFigureElementAxisConstants {
	
	SGAxisElement mAxisElement = null;
	
    public ElementStringExponent(final SGAxisElement aElement, String str, String fontName,
			final int fontStyle, final float fontSize,
			Color fontColor, final float mag, final float angle) {
		super(str, fontName, fontStyle, fontSize, fontColor, mag, angle);
        this.mAxisElement = aElement;
	}

	private boolean mSelected = false;

    /**
     * Sets selected or deselected the object.
     * 
     * @param b
     *            true selects and false deselects the object
     */
	@Override
	public void setSelected(boolean b) {
		this.setSelectedFlag(b);
		this.mAxisElement.updateSelectedFlag(b);
	}

    void setSelectedFlag(final boolean b) {
    	this.mSelected = b;
    }

    /**
     * Returns whether this object is selected
     * 
     * @return whether this object is selected
     */
    @Override
	public boolean isSelected() {
		return this.mSelected;
	}
	
    List<Point2D> getAnchorPoints() {
        List<Point2D> list = new ArrayList<Point2D>();
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

	@Override
	public void translate(final float dx, final float dy) {
		final float factor = SGIConstants.CM_POINT_RATIO / this.mMagnification;
		final float x = this.mAxisElement.getExponentLocationX();
		final float y = this.mAxisElement.getExponentLocationY();
		final float xNew = this.mAxisElement.roundOffExponentShift(x + factor * dx);
		final float yNew = this.mAxisElement.roundOffExponentShift(y + factor * dy);
		this.mAxisElement.setExponentLocationX(xNew);
		this.mAxisElement.setExponentLocationY(yNew);
		final float dxNew = (xNew - x) / factor;
		final float dyNew = (yNew - y) / factor;
		this.setLocation(this.getX() + dxNew, this.getY() + dyNew);
	}
}

/**
 * Properties for a single axis.
 * 
 */
class AxisProperties extends SGProperties {

    boolean axisVisible;

    boolean dateMode;

	boolean axisLineVisible;

    float axisLineWidth;

    Color axisLineColor;

    float spaceLineAndNumbers;
    
    boolean titleVisible;

    String titleText;

    float spaceTitleAndNumbers;
    
    float titleShiftFromCenter;
    
    String titleFontName;

    int titleFontStyle;

    float titleFontSize;

    Color titleFontColor;

    boolean numberInteger;
    
    SGAxisValue minValue;

    SGAxisValue maxValue;

    int scaleType;
    
    boolean invertedCoordinates;

    boolean autoCalc;

    SGAxisValue baselineValue;

    SGAxisStepValue stepValue;

    boolean exponentVisible;
    
    int exponent;
    
    boolean numberVisible;

    float numberAngle;

    String numberFontName;

    float numberFontSize;

    int numberFontStyle;

    Color numberFontColor;

    float exponentLocationX;

    float exponentLocationY;
    
    String numberDateFormat;
    
    boolean tickMarkVisible;

    boolean tickMarkBothsides;

    float tickMarkWidth;

    float tickMarkLength;

    int minorTickMarkNumber;
    
    float minorTickMarkLength;

    Color tickMarkColor;

    /**
     * Returns whether a given object is equal to this.
     * 
     * @param obj
     *           an object
     * @return true if a given object is equals to this
     */
    @Override
    public boolean equals(final Object obj) {
        if ((obj instanceof AxisProperties) == false) {
            return false;
        }
        AxisProperties p = (AxisProperties) obj;
        if (p.axisVisible != this.axisVisible) {
            return false;
        }
        if (p.dateMode != this.dateMode) {
            return false;
        }
        if (p.axisLineVisible != this.axisLineVisible) {
        	return false;
        }
        if (p.axisLineWidth != this.axisLineWidth) {
        	return false;
        }
        if (!SGUtility.equals(p.axisLineColor, this.axisLineColor)) {
        	return false;
        }
        if (p.titleVisible != this.titleVisible) {
            return false;
        }
        if (!SGUtility.equals(p.titleText, this.titleText)) {
            return false;
        }
        if (p.spaceLineAndNumbers != this.spaceLineAndNumbers) {
        	return false;
        }
        if (p.spaceTitleAndNumbers != this.spaceTitleAndNumbers) {
            return false;
        }
        if (p.titleShiftFromCenter != this.titleShiftFromCenter) {
        	return false;
        }
        if (!SGUtility.equals(p.titleFontName, this.titleFontName)) {
        	return false;
        }
        if (p.titleFontSize != this.titleFontSize) {
        	return false;
        }
        if (p.titleFontStyle != this.titleFontStyle) {
        	return false;
        }
        if (!SGUtility.equals(p.titleFontColor, this.titleFontColor)) {
        	return false;
        }
        if (p.numberInteger != this.numberInteger) {
            return false;
        }
        if (!SGUtility.equals(p.minValue, this.minValue)) {
            return false;
        }
        if (!SGUtility.equals(p.maxValue, this.maxValue)) {
            return false;
        }
        if (p.scaleType != this.scaleType) {
            return false;
        }
        if (p.invertedCoordinates != this.invertedCoordinates) {
            return false;
        }
        if (p.autoCalc != this.autoCalc) {
            return false;
        }
        if (p.stepValue != this.stepValue) {
            return false;
        }
        if (p.baselineValue != this.baselineValue) {
            return false;
        }
        if (p.exponentVisible != this.exponentVisible) {
            return false;
        }
        if (p.exponent != this.exponent) {
            return false;
        }
        if (p.numberVisible != this.numberVisible) {
            return false;
        }
        if (p.numberAngle != this.numberAngle) {
            return false;
        }
        if (!SGUtility.equals(p.numberFontName, this.numberFontName)) {
        	return false;
        }
        if (p.numberFontSize != this.numberFontSize) {
        	return false;
        }
        if (p.numberFontStyle != this.numberFontStyle) {
        	return false;
        }
        if (!SGUtility.equals(p.numberFontColor, this.numberFontColor)) {
        	return false;
        }
        if (p.exponentLocationX != this.exponentLocationX) {
        	return false;
        }
        if (p.exponentLocationY != this.exponentLocationY) {
        	return false;
        }
        if (!SGUtility.equals(p.numberDateFormat, this.numberDateFormat)) {
        	return false;
        }
        if (p.tickMarkVisible != this.tickMarkVisible) {
            return false;
        }
        if (p.tickMarkBothsides != this.tickMarkBothsides) {
            return false;
        }
        if (p.tickMarkWidth != this.tickMarkWidth) {
            return false;
        }
        if (p.tickMarkLength != this.tickMarkLength) {
            return false;
        }
        if (p.minorTickMarkNumber != this.minorTickMarkNumber) {
        	return false;
        }
        if (p.minorTickMarkLength != this.minorTickMarkLength) {
        	return false;
        }
        if (!SGUtility.equals(p.tickMarkColor, this.tickMarkColor)) {
        	return false;
        }
        return true;
    }
}

