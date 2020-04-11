package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIIndex;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.base.SGXYSimpleIndexBlock;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.VXYDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;

import org.w3c.dom.Element;

/**
 * 
 */
public class SGElementGroupSetInGraphVXY extends SGElementGroupSetInGraph
        implements SGIElementGroupSetVXY, SGIVXYDataDialogObserver, SGIArrowConstants {

	/**
	 * The array of coordinate of the start points.
	 */
	protected SGTuple2f[] mStartPointsArray = null;

	/**
	 * The array of coordinate of the end points.
	 */
    protected SGTuple2f[] mEndPointsArray = null;

    /**
     * The array of x-coordinates.
     * These values are used only in the grid mode.
     */
    protected float[] mXArray = null;
    
    /**
     * The array of y-coordinates.
     * These values are used only in the grid mode.
     */
    protected float[] mYArray = null;

    /**
     * The scaling factor for the magnitude of vectors.
     */
    private float mMagnitudePerCM = 1.0f;

    /**
     * Builds a group set object.
     * 
     * @param data
     *           the data
     * @param graph
     *           the graph that this object belongs.
     */
    protected SGElementGroupSetInGraphVXY(SGData data, SGFigureElementGraph graph) {
        super(data, graph);
    }

    /**
     * Dispose of this object.
     */
    public void dispose() {
        super.dispose();
        this.mStartPointsArray = null;
        this.mEndPointsArray = null;
        this.mXArray = null;
        this.mYArray = null;
    }

    // initialize the points
    protected void initPointsArray(final int num) {
        SGTuple2f[] sArray = new SGTuple2f[num];
        SGTuple2f[] eArray = new SGTuple2f[num];
        for (int ii = 0; ii < num; ii++) {
            sArray[ii] = new SGTuple2f();
            eArray[ii] = new SGTuple2f();
        }
        this.mStartPointsArray = sArray;
        this.mEndPointsArray = eArray;
    }

    /**
     * Initialize the points.
     * 
     * @param numX
     *          the number of x-coordinates
     * @param numY
     *          the number of y-coordinates
     */
    protected void initPointsArray(final int numX, final int numY) {
        this.mXArray = new float[numX];
        this.mYArray = new float[numY];
    }

    /**
     * Returns the magnitude of vectors per centimeter.
     * 
     * @return the magnitude of vectors per centimeter
     */
    public float getMagnitudePerCM() {
        return this.mMagnitudePerCM;
    }

    /**
     * Sets the magnitude of vectors per centimeter.
     * 
     * @param mag
     *           the magnitude of vectors per centimeter
     * @return true if succeeded
     */
    public boolean setMagnitudePerCM(final float mag) {
        if (mag <= 0.0) {
            return false;
        }
        SGIVXYTypeData vData = (SGIVXYTypeData) this.mData;
        this.mMagnitudePerCM = SGDataUtility.roundMagnitudePerCM(mag, vData);
        this.updateDrawingElementsLocation(this.mGraph.getData(this));
        return true;
    }

    /**
     * 
     * @return
     */
    public String getClassDescription() {
        return "";
    }

    public boolean addDrawingElementGroup(final int type) {
    	SGElementGroupArrow group = null;
        if (type == SGIElementGroupConstants.ARROW_GROUP) {
            group = new SGElementGroupArrowInGraph(this.mGraph);
            group.setGridMode(this.isGridMode());
        } else {
            return false;
        }

        // add a group
        this.addElementGroup(group);

        return true;
    }

    private boolean addElementGroup(final SGElementGroupArrow group) {
        // set the group set
        SGIElementGroupInGraph iGroup = (SGIElementGroupInGraph) group;
        iGroup.setElementGroupSet(this);

        // initialize the drawing element
        if (group.initDrawingElement(this.mStartPointsArray,
                this.mEndPointsArray) == false) {
            throw new Error();
        }
        
        // add the new group to the list
        this.mDrawingElementGroupList.add(group);

        return true;
    }

    /**
     * A flag for the invariance.
     */
    private boolean mDirectionFixedFlag;

    /**
     * Returns whether the direction is invariant when the scale of the x-axis direction 
     * and the y-axis direction is different.
     * 
     * @return a flag for the invariance
     */
    public boolean isDirectionInvariant() {
        return this.mDirectionFixedFlag;
    }

    /**
     * Sets whether the direction is invariant when the scale of the x-axis direction 
     * and the y-axis direction is set to be different.
     * 
     * @param b
     *          a flag to set to the invariance
     * @return true if succeeded
     */
    public boolean setDirectionInvariant(final boolean b) {
        this.mDirectionFixedFlag = b;
        return true;
    }

    /**
     * Called when the location of data points have changed.
     */
    public boolean updateDrawingElementsLocation(final SGIData data) {
        if ((data instanceof SGIVXYTypeData) == false) {
            return false;
        }
        SGIVXYTypeData dataVXY = (SGIVXYTypeData) data;
        if (this.isGridMode((SGData) data)) {
        	
        	// get x and y values
            final double[] xValues = dataVXY.getXValueArray(true);
            final double[] yValues = dataVXY.getYValueArray(true);
            
            // initialize the points
            this.initPointsArray(xValues.length, yValues.length);
    		
            // set the location of the data points
            if (this.mGraph.calcLocationOfPoints(xValues,
                    this.getXAxis(), true, this.mXArray) == false) {
                return false;
            }
            if (this.mGraph.calcLocationOfPoints(yValues, 
                    this.getYAxis(), false, this.mYArray) == false) {
                return false;
            }

            List<SGXYSimpleDoubleValueIndexBlock> firstBlockList = dataVXY.getFirstComponentValueBlockList();
            if (firstBlockList == null) {
            	return false;
            }
            List<SGXYSimpleDoubleValueIndexBlock> secondBlockList = dataVXY.getSecondComponentValueBlockList();
            if (secondBlockList == null) {
            	return false;
            }

            final int num = dataVXY.getPointsNumber();
            if (this.mStartPointsArray == null || this.mStartPointsArray.length != num) {
                this.mStartPointsArray = new SGTuple2f[num];
                for (int ii = 0; ii < num; ii++) {
                    this.mStartPointsArray[ii] = new SGTuple2f();
                }
            }
            if (this.mEndPointsArray == null || this.mEndPointsArray.length != num) {
                this.mEndPointsArray = new SGTuple2f[num];
                for (int ii = 0; ii < num; ii++) {
                    this.mEndPointsArray[ii] = new SGTuple2f();
                }
            }

            final double xyRatio = SGArrowUtility.calcXYRatio(dataVXY, this.mGraph, 
            		this, this.isDirectionInvariant());
            
            List<SGElementGroup> gList = this.mDrawingElementGroupList;
            for (int ii = 0; ii < gList.size(); ii++) {
                SGElementGroup group = (SGElementGroup) gList.get(ii);
                if (group.isVisible()) {
                    if (group instanceof SGElementGroupArrow) {
                    	SGElementGroupArrowInGraph arrows = (SGElementGroupArrowInGraph) group;
                    	
                        // sets the components
                        if (arrows.setLocation(this.mXArray, this.mYArray,
                        		firstBlockList, secondBlockList, dataVXY.isPolar(),
                        		this.getMagnitudePerCM(), this.isDirectionInvariant(), 
                        		xyRatio, this.mStartPointsArray, this.mEndPointsArray) == false) {
                            return false;
                        }
                    }
                }
            }
            
        } else {
        	
        	// get the number of the points
            final int num = dataVXY.getPointsNumber();
            
            // initialize the points
            this.initPointsArray(num);

            // set the location of the start points
            SGTuple2d[] xyValueArray = dataVXY.getXYValueArray(false);
            SGAxis xAxis = this.getXAxis();
            SGAxis yAxis = this.getYAxis();
            if (this.mGraph.calcLocationOfPoints(xyValueArray, xAxis, yAxis,
                    this.mStartPointsArray) == false) {
                return false;
            }

            // calculate the location of the end points and set to the groups
            double[] fValueArray = dataVXY.getFirstComponentValueArray(false);
            double[] sValueArray = dataVXY.getSecondComponentValueArray(false);
            if (SGArrowUtility.calcEndLocation(dataVXY, this.mGraph, this, 
            		this.getMagnitudePerCM(), this.isDirectionInvariant(), fValueArray, 
            		sValueArray, this.mStartPointsArray, this.mEndPointsArray) == false) {
            	return false;
            }
            
            for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
                SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
                if (group.isVisible()) {
                    if (group instanceof SGElementGroupArrowInGraph) {
                        SGElementGroupArrowInGraph vGroup = (SGElementGroupArrowInGraph) group;
                        vGroup.setLocation(this.mStartPointsArray, this.mEndPointsArray);
                    }
                }
            }
        }
        
        return true;
    }

    public void paintGraphics2D(final Graphics2D g2d) {
        Rectangle2D gRect = null;
        if (!this.getClipFlag()) {
            gRect = this.mGraph.getGraphRect();
        }

        List<SGElementGroup> list = this.mDrawingElementGroupList;
        for (int ii = 0; ii < list.size(); ii++) {
            final SGElementGroupArrowInGraph group = (SGElementGroupArrowInGraph) list
                    .get(ii);
            if (group.isVisible()) {
                group.paintElement(g2d, gRect);
            }
        }

        this.paintFocusedShapes(g2d);
    }

    /**
     * Returns whether the legend is visible.
     */
    public boolean getLegendVisibleFlag() {
        return this.isVisibleInLegend();
    }

    /**
     * Returns the location of x-axis.
     */
    public int getXAxisLocation() {
        return this.mGraph.getAxisElement().getLocationInPlane(this.getXAxis());
    }

    /**
     * Returns the location of y-axis.
     */
    public int getYAxisLocation() {
        return this.mGraph.getAxisElement().getLocationInPlane(this.getYAxis());
    }

    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    /**
     * Write properties of this object to the Element.
     * 
     * @param el
     *            the Element object
     * @param type
     *            type of the method to save properties
     * @return true if succeeded
     */
    public boolean writeProperty(final Element el, final SGExportParameter params) {
        if (super.writeProperty(el, params) == false) {
            return false;
        }

        final String strX = this.mGraph.getAxisElement().getLocationName(
                this.getXAxis());
        final String strY = this.mGraph.getAxisElement().getLocationName(
                this.getYAxis());

        el.setAttribute(SGIFigureElementGraph.KEY_X_AXIS_POSITION, strX);
        el.setAttribute(SGIFigureElementGraph.KEY_Y_AXIS_POSITION, strY);

        // write polar flag
        SGIVXYTypeData data = (SGIVXYTypeData) this.mGraph.getData(this);
        final boolean polar = data.isPolar();
        el.setAttribute(SGIFigureElementGraph.KEY_POLAR, Boolean
                .toString(polar));

        // write magnification
        final float factor = this.getMagnitudePerCM();
        el.setAttribute(SGIFigureElementGraph.KEY_MAGNITUDE_PER_CM, Float
                .toString(factor));

        final boolean invariant = this.isDirectionInvariant();
        el.setAttribute(SGIFigureElementGraph.KEY_DIRECTION_INVARIANT, Boolean
                .toString(invariant));

        return true;
    }

    /**
     * Returns the properties of this object.
     */
    public SGProperties getProperties() {
        ElementGroupSetInVXYGraphProperties ep = new ElementGroupSetInVXYGraphProperties();
        if (this.getProperties(ep) == false) {
            return null;
        }
        return ep;
    }

    /**
     * Get the properties of this object.
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetInVXYGraphProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }

        ElementGroupSetInVXYGraphProperties ep = (ElementGroupSetInVXYGraphProperties) p;
        ep.mMagnitudeScalingFactor = this.getMagnitudePerCM();
        ep.mDirectionInvariant = this.isDirectionInvariant();
        
        return true;
    }

    /**
     * Set properties to this object.
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetInVXYGraphProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        this.updateGridMode();

        ElementGroupSetInVXYGraphProperties ep = (ElementGroupSetInVXYGraphProperties) p;
        this.setMagnitudePerCM(ep.mMagnitudeScalingFactor);
        this.setDirectionInvariant(ep.mDirectionInvariant);
        
        return true;
    }

    public float getLineWidth(final String unit) {
        return this.getArrowGroup().getLineWidth(unit);
    }

    public int getLineType() {
        return this.getArrowGroup().getLineType();
    }

    public Color getColor() {
        return this.getArrowGroup().getColor();
    }

    public float getHeadSize(final String unit) {
        return this.getArrowGroup().getHeadSize(unit);
    }

    public float getHeadOpenAngle() {
        return this.getArrowGroup().getHeadOpenAngle();
    }

    public float getHeadCloseAngle() {
        return this.getArrowGroup().getHeadCloseAngle();
    }

    public int getStartHeadType() {
        return this.getArrowGroup().getStartHeadType();
    }

    public int getEndHeadType() {
        return this.getArrowGroup().getEndHeadType();
    }

    public boolean setLineWidth(final float lw, final String unit) {
        return this.getArrowGroup().setLineWidth(lw, unit);
    }

    public boolean setLineType(final int type) {
        return this.getArrowGroup().setLineType(type);
    }

    public boolean setColor(final Color cl) {
        return this.getArrowGroup().setColor(cl);
    }

    public boolean setHeadSize(final float size, final String unit) {
        return this.getArrowGroup().setHeadSize(size, unit);
    }

    public boolean setHeadAngle(final Float openAngle, final Float closeAngle) {
        return this.getArrowGroup().setHeadAngle(openAngle, closeAngle);
    }

    public boolean setStartHeadType(final int type) {
        return this.getArrowGroup().setStartHeadType(type);
    }

    public boolean setEndHeadType(final int type) {
        return this.getArrowGroup().setEndHeadType(type);
    }

    public boolean hasValidAngle(final Number open, final Number close) {
        final float openAngle = (open != null) ? open.floatValue() : this
                .getHeadOpenAngle();
        final float closeAngle = (close != null) ? close.floatValue() : this
                .getHeadCloseAngle();
        return (openAngle < closeAngle);
    }

    /**
     * A class of properties of a group set in VXY graph element.
     */
    public static class ElementGroupSetInVXYGraphProperties extends
            ElementGroupSetPropertiesInFigureElement {

        float mMagnitudeScalingFactor;

        boolean mDirectionInvariant;
        
        /**
         * The default constructor.
         */
        public ElementGroupSetInVXYGraphProperties() {
            super();
        }

        /**
         * Returns whether this object is equal to given object.
         * 
         * @param obj
         *            an object to be compared
         * @return true if two objects are equal
         */
        public boolean equals(final Object obj) {
            if ((obj instanceof ElementGroupSetInVXYGraphProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }

            ElementGroupSetInVXYGraphProperties p = (ElementGroupSetInVXYGraphProperties) obj;
            if (p.mMagnitudeScalingFactor != this.mMagnitudeScalingFactor) {
                return false;
            }
            if (p.mDirectionInvariant != this.mDirectionInvariant) {
                return false;
            }

            return true;
        }

        /**
         * Returns string representation of this object.
         * 
         * @return string representation of this object
         */
        public String toString() {
            String str = super.toString();
            StringBuffer sb = new StringBuffer();
            sb.append("[");
            sb.append(str);
            sb.append(", Magnitude Scaling Factor=");
            sb.append(this.mMagnitudeScalingFactor);
            sb.append(", Direction Invariant=");
            sb.append(this.mDirectionInvariant);
            sb.append("]");
            return sb.toString();
        }

    }

    /**
     * Returns the location of the start point at a given index.
     * 
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getStartLocation(final int index) {
        if (this.mStartPointsArray == null) {
            return null;
        }
        if (index < 0 || index >= this.mStartPointsArray.length) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return this.mStartPointsArray[index];
    }

    /**
     * Returns the location of the end point at a given index.
     * 
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getEndLocation(final int index) {
        if (this.mEndPointsArray == null) {
            return null;
        }
        if (index < 0 || index >= this.mEndPointsArray.length) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return this.mEndPointsArray[index];
    }

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        result = super.setProperties(map, result);
        if (result == null) {
            return null;
        }
        
		List<String> keys = map.getKeys();
        final boolean dataColumnContained = keys.contains(COM_DATA_COLUMN_TYPE.toUpperCase());

        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_DATA_ARROW_MAGNITUDE_PER_CM.equalsIgnoreCase(key)) {
                Float mag = SGUtilityText.getFloat(value);
                if (mag == null) {
                    result.putResult(COM_DATA_ARROW_MAGNITUDE_PER_CM, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGUtility.isValidPropertyValue(mag.floatValue()) == false) {
                    result.putResult(COM_DATA_ARROW_MAGNITUDE_PER_CM, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setMagnitudePerCM(mag.floatValue()) == false) {
                    result.putResult(COM_DATA_ARROW_MAGNITUDE_PER_CM, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_MAGNITUDE_PER_CM, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARROW_DIRECTION_INVARIANT.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_ARROW_DIRECTION_INVARIANT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setDirectionInvariant(b.booleanValue()) == false) {
                    result.putResult(COM_DATA_ARROW_DIRECTION_INVARIANT, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_ARROW_DIRECTION_INVARIANT, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_X_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_X_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setStrideX(value) == false) {
                    result.putResult(COM_DATA_X_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_X_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_Y_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_Y_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setStrideY(value) == false) {
                    result.putResult(COM_DATA_Y_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_Y_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setSDArrayStride(value) == false) {
                    result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_INDEX_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setIndexStride(value) == false) {
                    result.putResult(COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ANIMATION_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	SGArrayData data = (SGArrayData) this.getData();
            	if (!data.isAnimationAvailable()) {
                    result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	final int len = data.getAnimationLength();
                if (len == -1) {
                    result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                SGIntegerSeriesSet arraySection = SGIntegerSeriesSet.parse(value, len);
                if (arraySection == null) {
                    result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setFrameIndices(arraySection);
                result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_ANIMATION_FRAME_DIMENSION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for pick up indices
        			continue;
        		}
        		if (!this.setTimeDimension(value)) {
                    result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
				result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.SUCCEEDED);
			}
        }

        List<SGElementGroupArrow> arrowGroups = this.getArrowGroups();
        for (int ii = 0; ii < arrowGroups.size(); ii++) {
            SGElementGroupArrowForData arrows = (SGElementGroupArrowForData) arrowGroups.get(ii);
            result = arrows.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }
        
        return result;
    }

    private boolean setIndexStride(String value) {
    	SGData data = this.getData();
    	if (data instanceof SGVXYSDArrayData) {
    		SGVXYSDArrayData sdData = (SGVXYSDArrayData) data;
    		final int len = sdData.getAllPointsNumber();
        	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
        	if (stride == null) {
                return false;
        	}
            sdData.setStride(stride);
            return true;
    	} else if (data instanceof SGVXYNetCDFData) {
    		SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
    		if (!ncData.isIndexAvailable()) {
    			return false;
    		}
            return this.setNetCDFIndexStride(value);
    	} else if (data instanceof SGVXYMDArrayData) {
    		SGVXYMDArrayData mdData = (SGVXYMDArrayData) data;
    		if (!mdData.isIndexAvailable()) {
    			return false;
    		}
        	final int len = mdData.getAllPointsNumber();
        	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
        	if (stride == null) {
                return false;
        	}
        	mdData.setIndexStride(stride);
        	return true;
    	} else {
    		return false;
    	}
    }

	/**
	 * Returns a list of arrow groups.
	 * 
	 * @return a list of arrow groups
	 */
	public List<SGElementGroupArrow> getArrowGroups() {
		List<SGElementGroupArrow> retList = new ArrayList<SGElementGroupArrow>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupArrow.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupArrow) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns an arrow group which is the first element of an array.
	 * 
	 * @return the first element of an array of arrow groups, or null when this
	 *         group set does not have any arrow groups
	 */
	public SGElementGroupArrow getArrowGroup() {
		return (SGElementGroupArrow) SGUtilityForFigureElement.getGroup(
				SGElementGroupArrow.class, this.mDrawingElementGroupList);
	}

    /**
     * Sets the properties of element groups.
     * 
     * @param elementGroupPropertiesList
     * @return true if succeeded
     * 
     */
    protected boolean setElementGroupProperties(List elementGroupPropertiesList) {
        for (int ii = 0; ii < elementGroupPropertiesList.size(); ii++) {
            SGProperties gp = (SGProperties) elementGroupPropertiesList.get(ii);
            SGElementGroup group = null;
            if (gp instanceof SGElementGroupArrow.ArrowProperties) {
                group = this.getArrowGroup();
            } else {
                throw new Error("Illegal group property: " + gp);
            }
            if (group == null) {
                continue;
            }
            if (group.setProperties(gp) == false) {
                return false;
            }
        }
        return true;
    }

    private boolean updateGridMode() {
    	final boolean b = this.isGridMode();
    	List<SGElementGroupArrow> groupList = this.getArrowGroups();
    	for (int ii = 0; ii < groupList.size(); ii++) {
    		SGElementGroupArrow group = groupList.get(ii);
    		if (group.setGridMode(b) == false) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Sets the information of data columns.
     * @param columns
     *                information of data columns
     */
    public boolean setColumnInfo(SGDataColumnInfo[] columns, String message) {
    	if (super.setColumnInfo(columns, message) == false) {
    		return false;
    	}

    	// initialize the points
    	SGIVXYTypeData data = (SGIVXYTypeData) this.getData();
    	this.initPointsArray(data.getPointsNumber());
    	
    	// update the grid mode
    	if (this.updateGridMode() == false) {
    		return false;
    	}
    	
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
		// do nothing
	}

    /**
     * Translates the data object.
     * 
     * @param x
     *           the x-coordinate that this object is translated to
     * @param y
     *           the y-coordinate that this object is translated to
     */
    protected void translateTo(final int x, final int y) {
		// do nothing
	}

    /**
     * Records the pressed point.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     */
    protected void recordPresentPoint(final int x, final int y) {
    	// do nothing
    }

    /**
     * Sets the stride for the x-direction.
     * 
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
	@Override
    public boolean setStrideX(SGIntegerSeriesSet stride) {
		return this.setStrideXToData(stride);
	}

    /**
     * Sets the stride for the y-direction.
     * 
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
	@Override
    public boolean setStrideY(SGIntegerSeriesSet stride) {
		return this.setStrideYToData(stride);
	}

    /**
     * Sets the stride of the indices.
     * 
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
	@Override
    public boolean setIndexStride(SGIntegerSeriesSet stride) {
		return this.setIndexStrideToData(stride);
    }

    /**
     * Sets the stride for single dimensional data.
     * 
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
	@Override
    public boolean setSDArrayStride(SGIntegerSeriesSet stride) {
		return this.setSDArrayStrideToData(stride);
    }

    /**
     * Sets the column types and related parameters.
     * 
     * @param map
     *           property map
     * @param result
     *           results of setting properties
     * @param cols
     *           an array of data columns
     * @return true if succeeded
     */
	@Override
    protected boolean setProperties(SGPropertyMap map, SGPropertyResults result,
    		SGDataColumnInfo[] cols) {

		String[] columnTypes = new String[cols.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = cols[ii].getColumnType();
		}
		
    	// set column types
		SGArrayData aData = (SGArrayData) this.mData;
		SGDataColumnInfo[] preColumnInfo = aData.getColumnInfo();
		try {
	    	if (SGDataUtility.isMDArrayData(this.mData)) {
	    		SGVXYMDArrayData mdData = (SGVXYMDArrayData) this.mData;
	        	if (mdData.setColumnType(cols) == false) {
	        		this.setFailedColumnTypeResult(map, result);
	                return false;
	        	}
	            result.putResult(COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);
	    	} else {
	    		// SDArray or NetCDF
		    	if (aData.setColumnType(columnTypes) == false) {
	        		this.setFailedColumnTypeResult(map, result);
		            return false;
		    	}
		        result.putResult(COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);
	    	}
		} finally {
			// updates the stride with new column types
	    	if (!SGDataUtility.hasEqualInput(preColumnInfo, cols)) {
	    		Map<String, Object> infoMap = new HashMap<String, Object>();
	    		infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, this.mData.getDataType());
	    		infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(
	    				this.mGraph.getGraphRectWidth(), this.mGraph.getGraphRectHeight()));
	        	if (SGDataUtility.isSDArrayData(this.mData)) {
	        		SGVXYSDArrayData sdData = (SGVXYSDArrayData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcSDArrayDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        			sdData.setStride(stride);
	        	} else if (SGDataUtility.isNetCDFData(this.mData)) {
            		SGVXYNetCDFData ncData = (SGVXYNetCDFData) this.mData;
            		infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, ncData.isPolar());
            		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcNetCDFDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet strideX = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
        			ncData.setXStride(strideX);
        			SGIntegerSeriesSet strideY = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
        			ncData.setYStride(strideY);
        			SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        			ncData.setIndexStride(stride);
            	} else if (SGDataUtility.isMDArrayData(this.mData)) {
            		SGVXYMDArrayData mdData = (SGVXYMDArrayData) this.mData;
            		infoMap.put(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, mdData.isPolar());
	        		if (!SGDataUtility.addGridType(cols, this.mData.getDataType(), infoMap)) {
	        			return false;
	        		}
            		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcMDArrayDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet strideX = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X);
        			mdData.setXStride(strideX);
        			SGIntegerSeriesSet strideY = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
        			mdData.setYStride(strideY);
        			SGIntegerSeriesSet indexStride = strideMap.get(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE);
        			mdData.setIndexStride(indexStride);
            	}
	    	}

	    	// clears time dimension
	    	this.clearTimeDimension();
		}
    	
    	// sets animation dimension
    	if (SGDataUtility.isMDArrayData(this.mData)) {
        	List<String> keys = map.getKeys();
        	String strTimeDimension = map.getValueString(COM_DATA_ANIMATION_FRAME_DIMENSION);
            final boolean timeDimContained = keys.contains(COM_DATA_ANIMATION_FRAME_DIMENSION.toUpperCase())
            		&& !"".equals(strTimeDimension);
        	if (timeDimContained) {
            	if (!this.setTimeDimension(strTimeDimension)) {
                    result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
            	} else {
                    result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.SUCCEEDED);
            	}
        	}
    	}

    	// sets the stride
        Iterator<String> itr = map.getKeyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = map.getValueString(key);
	    	if ("".equals(value)) {
	    		continue;
	    	}
			if (COM_DATA_X_ARRAY_SECTION.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_X_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setStrideX(value) == false) {
                    result.putResult(COM_DATA_X_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_X_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_Y_ARRAY_SECTION.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_Y_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setStrideY(value) == false) {
                    result.putResult(COM_DATA_Y_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_Y_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_INDEX_ARRAY_SECTION.equalsIgnoreCase(key)) {
	        	if (map.isDoubleQuoted(COM_DATA_INDEX_ARRAY_SECTION) == false) {
	                result.putResult(COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
	        	}
	        	if (this.setIndexStride(value) == false) {
	                result.putResult(COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
	        	}
	            result.putResult(COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_ARRAY_SECTION.equalsIgnoreCase(key)) {
	        	if (map.isDoubleQuoted(COM_DATA_ARRAY_SECTION) == false) {
	                result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
	        	}
	        	if (this.setIndexStride(value) == false) {
	                result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
	        	}
	            result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ANIMATION_ARRAY_SECTION.equalsIgnoreCase(key)) {
            	SGArrayData data = (SGArrayData) this.getData();
            	if (!data.isAnimationAvailable()) {
                    result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	final int len = data.getAnimationLength();
                if (len == -1) {
                    result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                SGIntegerSeriesSet arraySection = SGIntegerSeriesSet.parse(value, len);
                if (arraySection == null) {
                    result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setFrameIndices(arraySection);
                result.putResult(COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_ANIMATION_FRAME_DIMENSION.equalsIgnoreCase(key)) {
	        	if (!this.setTimeDimension(value)) {
	                result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
	        	}
	            result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.SUCCEEDED);
			}
		}
		
    	// updates the grid mode
    	if (this.updateGridMode() == false) {
    		return false;
    	}

		return true;
	}

    /**
     * Returns the figure element.
     * 
     * @return the figure element
     */
    public SGFigureElementForData getFigureElement() {
    	return this.mGraph;
    }

	@Override
	public Map<String, SGProperties> getElementGroupPropertiesMap() {
		return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
	}

	@Override
	public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
		return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
	}

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
	@Override
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = super.getCommandPropertyMap(params);
		SGIVXYTypeData vxyData = (SGIVXYTypeData) this.getData();
		SGPropertyUtility.addProperty(map, COM_DATA_POLAR, 
				Boolean.toString(vxyData.isPolar()));
		SGPropertyUtility.addProperty(map, COM_DATA_ARROW_MAGNITUDE_PER_CM, 
				this.getMagnitudePerCM());
		SGPropertyUtility.addProperty(map, COM_DATA_ARROW_DIRECTION_INVARIANT, 
				this.isDirectionInvariant());
		
		// drawing elements
		SGElementGroupArrowForData arrowGroup = (SGElementGroupArrowForData) this.getArrowGroup();
		arrowGroup.getProperties(map);
		
    	return map;
    }
	
	@Override
    protected void setFocusedValueIndices(final SGDataViewerDialog dg,
    		List<SGXYSimpleIndexBlock> blockList) {

    	List<SGTuple2d> valueList = this.mFocusedValueListMap.get(dg);
		SGIVXYTypeData dataVXY = (SGIVXYTypeData) this.getData();

    	if (this.isGridMode()) {
    		String columnType = dg.getColumnType();
            final double[] xValues = dataVXY.getXValueArray(true);
            final double[] yValues = dataVXY.getYValueArray(true);

            final boolean polar = dataVXY.isPolar();
            final String first = SGDataUtility.getVXYFirstComponentColumnType(polar);
            final String second = SGDataUtility.getVXYSecondComponentColumnType(polar);
            
            if (first.equals(columnType) || second.equals(columnType)) {
            	for (SGXYSimpleIndexBlock block : blockList) {
            		// x-coordinate
            		SGIntegerSeries xSeries = block.getXSeries();
            		int[] xIndices = xSeries.getNumbers();
            		List<Double> xValueList = new ArrayList<Double>();
            		for (int xIndex : xIndices) {
            			final double xValue = xValues[xIndex];
            			xValueList.add(xValue);
            		}
            		
            		// y-coordinate
            		SGIntegerSeries ySeries = block.getYSeries();
            		int[] yIndices = ySeries.getNumbers();
            		List<Double> yValueList = new ArrayList<Double>();
            		for (int yIndex : yIndices) {
            			final double yValue = yValues[yIndex];
            			yValueList.add(yValue);
            		}
            		
        			// add to list
            		for (Double yValue : yValueList) {
            			for (Double xValue : xValueList) {
                    		valueList.add(new SGTuple2d(xValue, yValue));
            			}
            		}
            	}

            } else if (SGIDataColumnTypeConstants.X_COORDINATE.equals(columnType)) {
            	
            	// y-coordinate
        		SGIntegerSeriesSet yStride = dataVXY.getYStride();
        		int[] yIndices = yStride.getNumbers();
        		double[] yValueArray = new double[yIndices.length];
        		for (int ii = 0; ii < yIndices.length; ii++) {
        			final int yIndex = yIndices[ii];
            		yValueArray[ii] = yValues[yIndex];
        		}

        		// x-coordinate
            	for (SGXYSimpleIndexBlock block : blockList) {
            		SGIntegerSeries rowSeries = block.getYSeries();
            		int[] xIndices = rowSeries.getNumbers();
            		for (int xIndex : xIndices) {
            			final double xValue = xValues[xIndex];

            			// add to list
            			for (double yValue : yValueArray) {
                			valueList.add(new SGTuple2d(xValue, yValue));
            			}
            		}
            	}
            	
            } else if (SGIDataColumnTypeConstants.Y_COORDINATE.equals(columnType)) {

            	// x-coordinate
        		SGIntegerSeriesSet xStride = dataVXY.getXStride();
        		int[] xIndices = xStride.getNumbers();
        		double[] xValueArray = new double[xIndices.length];
        		for (int ii = 0; ii < xIndices.length; ii++) {
        			final int xIndex = xIndices[ii];
            		xValueArray[ii] = xValues[xIndex];
        		}

        		// y-coordinate
            	for (SGXYSimpleIndexBlock block : blockList) {
            		SGIntegerSeries rowSeries = block.getYSeries();
            		int[] yIndices = rowSeries.getNumbers();
            		for (int yIndex : yIndices) {
            			final double yValue = yValues[yIndex];

            			// add to list
            			for (double xValue : xValueArray) {
                			valueList.add(new SGTuple2d(xValue, yValue));
            			}
            		}
            	}
            }

    	} else {
        	// index list of values
        	List<Integer> valueIndexList = new ArrayList<Integer>();
        	for (SGXYSimpleIndexBlock block : blockList) {
        		SGIntegerSeries valueIndexSeries = block.getYSeries();
        		valueIndexList.addAll(valueIndexSeries.getNumberList());
        	}
        	if (valueIndexList.size() == 0) {
        		return;
        	}

        	// get array section
        	SGIntegerSeriesSet stride = this.mData.getDataViewerRowStride(dg.getColumnType());
        	int[] arraySectionIndices = stride.getNumbers();
    		List<Integer> pointIndexList = new ArrayList<Integer>();
        	int focusedCnt = 0;
        	for (int ii = 0; ii < arraySectionIndices.length; ii++) {
        		if (valueIndexList.get(focusedCnt) == arraySectionIndices[ii]) {
        			pointIndexList.add(ii);
            		focusedCnt++;
            		if (focusedCnt >= valueIndexList.size()) {
            			break;
            		}
        		}
        	}

            SGTuple2d[] xyValueArray = dataVXY.getXYValueArray(false);
    		for (int pointIndex : pointIndexList) {
    			SGTuple2d value = xyValueArray[pointIndex];
    			valueList.add(value);
    		}
    	}
    }

	@Override
	protected String getToolTipText(int x, int y) {
		if (!this.mGraph.getGraphRect().contains(x, y)) {
			return null;
		}

		SGArrayData data = (SGArrayData) this.getData();
        SGIVXYTypeData dataVXY = (SGIVXYTypeData) data;
    	SGElementGroupArrowInGraph arrowGroup = (SGElementGroupArrowInGraph) this.getArrowGroup();
		SGIIndex index = arrowGroup.getIndexAt(x, y, this.mStartPointsArray,
				this.mEndPointsArray);
		if (index == null) {
			return null;
		}
    	VXYDataValue dataValue = null;
    	if (this.isGridMode()) {
			SGTwoDimensionalArrayIndex tdIndex = (SGTwoDimensionalArrayIndex) index;
			dataValue = arrowGroup.getDataValueAt(dataVXY,
					tdIndex.getColumn(), tdIndex.getRow());
    	} else {
    		SGArrayIndex arrayIndex = (SGArrayIndex) index;
        	dataValue = arrowGroup.getDataValueAt(dataVXY, arrayIndex.getIndex());
    	}
		String dataValueText = null;
		if (dataValue != null) {
			final double xValue = SGUtilityNumber.getNumberInRangeOrder(
					dataValue.xValue, this.mXAxis);
			final double yValue = SGUtilityNumber.getNumberInRangeOrder(
					dataValue.yValue, this.mYAxis);
	    	final double fValue;
	    	if (Double.isNaN(dataValue.fValue.number)) {
	    		fValue = Double.NaN;
	    	} else {
		    	fValue = this.reduceFirstComponentValue(dataValue.fValue.number);
	    	}
	    	final double sValue;
	    	if (Double.isNaN(dataValue.sValue.number)) {
	    		sValue = Double.NaN;
	    	} else {
		    	sValue = this.reduceSecondComponentValue(dataValue.sValue.number);
	    	}

			StringBuffer sb = new StringBuffer();
			sb.append("(");
			sb.append(xValue);
			sb.append(", ");
			sb.append(yValue);
			sb.append(", ");
			sb.append(fValue);
			sb.append(", ");
			sb.append(sValue);
			sb.append(")");
			dataValueText = sb.toString();
		}
		String spatiallyVariedText = arrowGroup.getToolTipTextSpatiallyVaried(
				x, y, this.mStartPointsArray, this.mEndPointsArray);
		String notSpatiallyVariedText = data.getToolTipTextNotSpatiallyVaried();

		return this.createToolTipText(spatiallyVariedText,
				notSpatiallyVariedText, dataValueText);
	}

	@Override
    public String getAxisValueString(final int x, final int y) {
    	SGElementGroupArrowInGraph arrowGroup = (SGElementGroupArrowInGraph) this.getArrowGroup();
    	VXYDataValue componentValues = arrowGroup.getComponentValues(x, y, 
    			this.mStartPointsArray, this.mEndPointsArray);
    	if (componentValues == null) {
    		return null;
    	}
    	double fValue = componentValues.fValue.number;
    	double sValue = componentValues.sValue.number;
    	double xValue = this.mGraph.calcValue(x, this.mXAxis, true);
    	double yValue = this.mGraph.calcValue(y, this.mYAxis, false);
    	xValue = SGUtilityNumber.getNumberInRangeOrder(xValue, this.mXAxis);
    	yValue = SGUtilityNumber.getNumberInRangeOrder(yValue, this.mYAxis);
    	if (!componentValues.fValue.missing) {
    		if (!Double.isNaN(fValue)) {
            	fValue = this.reduceFirstComponentValue(fValue);
    		}
    	}
    	if (!componentValues.sValue.missing) {
    		if (!Double.isNaN(sValue)) {
            	sValue = this.reduceSecondComponentValue(sValue);
    		}
    	}

    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	sb.append(xValue);
    	sb.append(", ");
    	sb.append(yValue);
    	sb.append(", ");
    	sb.append(fValue);
    	sb.append(", ");
    	sb.append(sValue);
    	sb.append(')');
    	return sb.toString();
    }
	
	private double reduceFirstComponentValue(final double fValue) {
    	SGIVXYTypeData vxyData = (SGIVXYTypeData) this.mData;
    	double[] fValues = vxyData.getFirstComponentValueArray(true);
    	final double fMaxValue = SGUtilityNumber.absMax(fValues);
    	if (fMaxValue != 0.0 && !Double.isNaN(fMaxValue)) {
        	return SGUtilityNumber.getNumberInNumberOrder(fValue, fMaxValue);
    	}
		return fValue;
	}

	private double reduceSecondComponentValue(final double sValue) {
    	SGIVXYTypeData vxyData = (SGIVXYTypeData) this.mData;
    	double[] sValues = vxyData.getSecondComponentValueArray(true);
    	final double sMaxValue = SGUtilityNumber.absMax(sValues);
    	if (sMaxValue != 0.0 && !Double.isNaN(sMaxValue)) {
        	return SGUtilityNumber.getNumberInNumberOrder(sValue, sMaxValue);
    	}    	
		return sValue;
	}

	@Override
    protected List<SGTwoDimensionalArrayIndex> getDataViewerIndex(final int x, final int y) {
		List<SGTwoDimensionalArrayIndex> ret = new ArrayList<SGTwoDimensionalArrayIndex>();
    	SGElementGroupArrowInGraph arrowGroup = (SGElementGroupArrowInGraph) this.getArrowGroup();
    	SGIIndex idx = arrowGroup.getIndexAt(x, y, this.mStartPointsArray, this.mEndPointsArray);
    	if (idx != null) {
        	if (this.isGridMode()) {
        		ret.add((SGTwoDimensionalArrayIndex) idx);
        	} else {
        		SGArrayIndex index = (SGArrayIndex) idx; 
        		ret.add(new SGTwoDimensionalArrayIndex(0, index.getIndex()));
        	}
    	}
    	return ret;
	}
}
