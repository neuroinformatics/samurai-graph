package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIIndex;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGXYSimpleIndexBlock;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYZDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupPseudocolorMap.PseudocolorMapRectangle;

import org.w3c.dom.Element;

/**
 * A class for the scalar XYZ-type data object in graph.
 *
 */
public class SGElementGroupSetInGraphSXYZ extends SGElementGroupSetInGraph
    implements SGIElementGroupSetSXYZ, SGISXYZDataDialogObserver {

    /**
     * An array of coordinates.
     */
    private SGTuple2f[] mPointsArray = null;

    /**
     * The array of x-coordinates.
     */
    private float[] mXArray = null;
    
    /**
     * The array of y-coordinates.
     */
    private float[] mYArray = null;

    /**
     * Builds a object.
     * 
     * @param data
     *           the data
     * @param graph
     *           the graph that this object belongs.
     */
    public SGElementGroupSetInGraphSXYZ(SGData data, SGFigureElementGraph graph) {
        super(data, graph);
    }
    
    /**
     * Initialize the points.
     * 
     * @param num
     *          the number of points
     */
    protected void initPointsArray(final int num) {
        SGTuple2f[] pointsArray = new SGTuple2f[num];
        for (int ii = 0; ii < num; ii++) {
            pointsArray[ii] = new SGTuple2f();
        }
        this.mPointsArray = pointsArray;
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
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();
        this.mPointsArray = null;
        this.mXArray = null;
        this.mYArray = null;
    }

    /**
     * Called when the location of data points is changed.
     * 
     * @param data
     *           a data object
     * @return true if succeeded
     */
    public boolean updateDrawingElementsLocation(final SGIData data) {
        if ((data instanceof SGISXYZTypeData) == false) {
            return false;
        }
        SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) data;
        if (this.isGridMode((SGData) data)) {
        	
        	// get x and y-values
            final double[] xValues = dataSXYZ.getXValueArray(true);
            final double[] yValues = dataSXYZ.getYValueArray(true);

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

            List<SGXYSimpleDoubleValueIndexBlock> blockList = dataSXYZ.getZValueBlockList();
            if (blockList == null) {
            	return false;
            }

            List<SGElementGroup> gList = this.mDrawingElementGroupList;
            for (int ii = 0; ii < gList.size(); ii++) {
                SGElementGroup group = (SGElementGroup) gList.get(ii);
                if (group.isVisible()) {
                    if (group instanceof SGElementGroupPseudocolorMap) {
                    	SGElementGroupPseudocolorMap colorMap = (SGElementGroupPseudocolorMap) group;
                    	
                    	// sets the location of the rectangles
                        if (colorMap.setLocation(this.mXArray, this.mYArray) == false) {
                            return false;
                        }

                        // sets the z-values
                        if (colorMap.setZValues(blockList) == false) {
                            return false;
                        }
                    }
                }
            }

        } else {

            final int num = dataSXYZ.getPointsNumber();
            this.initPointsArray(num);

            SGTuple2d[] xyValueArray = dataSXYZ.getXYValueArray(false);
            
            // set the location of the data points
            if (this.mGraph.calcLocationOfPoints(xyValueArray, 
                    this.getXAxis(), this.getYAxis(), this.mPointsArray) == false) {
                return false;
            }

            // get z-values
            final double[] zValueArray = dataSXYZ.getZValueArray(false);
            
            List<SGElementGroup> gList = this.mDrawingElementGroupList;
            for (int ii = 0; ii < gList.size(); ii++) {
                SGElementGroup group = (SGElementGroup) gList.get(ii);
                if (group.isVisible()) {
                    if (group instanceof SGElementGroupPseudocolorMap) {
                    	SGElementGroupPseudocolorMapInGraph colorMap = (SGElementGroupPseudocolorMapInGraph) group;
                        
                        // sets the location
                        if (colorMap.setLocation(this.mPointsArray) == false) {
                            return false;
                        }
                        
                        // sets the z-values
                        colorMap.setZValues(zValueArray);
                        
                        // updates the visibility
                        colorMap.updateVisibility();
                    }
                }
            }
        }

        return true;
    }

    /**
     * Add an element group.
     */
    public boolean addDrawingElementGroup(int type) {
        SGElementGroupPseudocolorMap colorMap = null;
        if (type == SGIElementGroupConstants.RECTANGLE_GROUP) {
            colorMap = new SGElementGroupPseudocolorMapInGraph(this.mGraph);
            colorMap.setColorBarModel(this.mGraph.getColorBarModel());
            colorMap.setGridMode(this.isGridMode());
        } else {
            return false;
        }

        // add a group
        if (this.addElementGroup(colorMap) == false) {
        	return false;
        }

        if (!this.isGridMode()) {
            // set the colors
            SGISXYZTypeData data = (SGISXYZTypeData) this.mData;
            final double[] zArray = data.getZValueArray(false);
            colorMap.setColors(zArray);
        }
        
        return true;
    }
    
    private boolean addElementGroup(final SGElementGroupPseudocolorMap group) {

        // set the group set
        SGIElementGroupInGraph iGroup = (SGIElementGroupInGraph) group;
        iGroup.setElementGroupSet(this);

        // create drawing elements
        if (group.initDrawingElement(this.mXArray, this.mYArray) == false) {
        	return false;
        }
        if (group.initDrawingElement(this.mPointsArray) == false) {
        	return false;
        }

        // add a group to the list
        this.mDrawingElementGroupList.add(group);

        return true;
    }
    
    /**
     * Paint the objects.
     * 
     * @param g2d
     *           the graphic context
     */
    public void paintGraphics2D(Graphics2D g2d) {
        Rectangle2D gRect = null;
        if (!this.getClipFlag()) {
            gRect = this.mGraph.getGraphRect();
        }
        
        // color map
        final SGElementGroupPseudocolorMapInGraph colorMap = this.mGraph.getColorMap(this);
        if (colorMap != null) {
            if (colorMap.isVisible()) {
                colorMap.paintElement(g2d, gRect);
            }
        }

        this.paintFocusedShapes(g2d);
    }

    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    /**
     * Returns a text string denoting this class.
     */
    public String getClassDescription() {
        // returns an empty string
        return "";
    }

    public boolean getLegendVisibleFlag() {
        return this.isVisibleInLegend();
    }

    /**
     * Returns a color map which is the first element of an array.
     * 
     * @return the first element of an array of color maps, or null when this
     *         group set does not have any color maps
     */
    public SGElementGroupPseudocolorMap getColorMap() {
        return this.getColorMapGroup();
    }
    
    /**
     * Sets the color bar model.
     * 
     * @param model
     *           the color bar model
     * @return true if succeeded
     */
    public boolean setColorBarModel(final SGColorMap model) {
        List<SGElementGroupPseudocolorMap> groups = this.getColorMapGroups();
        for (int ii = 0; ii < groups.size(); ii++) {
            SGElementGroupPseudocolorMap colorMap = groups.get(ii);
            colorMap.setColorBarModel(model);
        }
        this.mZAxis = model.getAxis();
        return true;
    }


    public double getRectangleWidthValue() {
        return this.getColorMap().getWidthValue();
    }
    
    public double getRectangleHeightValue() {
        return this.getColorMap().getHeightValue();
    }

    public boolean setRectangleWidthValue(final double value) {
        return this.getColorMap().setWidthValue(value);
    }

    public boolean setRectangleHeightValue(final double value) {
        return this.getColorMap().setHeightValue(value);
    }

    public int getXAxisLocation() {
        return this.mGraph.getAxisElement().getLocationInPlane(this.getXAxis());
    }

    public int getYAxisLocation() {
        return this.mGraph.getAxisElement().getLocationInPlane(this.getYAxis());
    }
    
    public boolean hasValidRectangleWidthValue(final int config,
            final Number value) {
        final SGAxis axis = (config == -1) ? this.mXAxis : this.mGraph
                .getAxisElement().getAxisInPlane(config);
        final double v = (value != null) ? value.doubleValue() : this
                .getRectangleWidthValue();
        return axis.isValidValue(v);
    }

    public boolean hasValidRectangleHeightValue(final int config,
            final Number value) {
        final SGAxis axis = (config == -1) ? this.mYAxis : this.mGraph
                .getAxisElement().getAxisInPlane(config);
        final double v = (value != null) ? value.doubleValue() : this
                .getRectangleHeightValue();
        return axis.isValidValue(v);
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
        el.setAttribute(SGIFigureElement.KEY_X_AXIS_POSITION, strX);
        el.setAttribute(SGIFigureElement.KEY_Y_AXIS_POSITION, strY);
        return true;
    }

    /**
     * Retruns the location at a given index.
     * 
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getLocation(final int index) {
        if (this.mPointsArray == null) {
            return null;
        }
        if (index < 0 || index >= this.mPointsArray.length) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        return this.mPointsArray[index];
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
			if (COM_DATA_X_ARRAY_SECTION.equalsIgnoreCase(key)) {
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

        List<SGElementGroupPseudocolorMap> colorMaps = this.getColorMapGroups();
        for (int ii = 0; ii < colorMaps.size(); ii++) {
            SGElementGroupPseudocolorMapForData colorMap = (SGElementGroupPseudocolorMapForData) colorMaps.get(ii);
            result = colorMap.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }
        
        return result;
    }

    private boolean setIndexStride(String value) {
    	SGData data = this.getData();
    	if (data instanceof SGSXYZSDArrayData) {
    		SGSXYZSDArrayData sdData = (SGSXYZSDArrayData) data;
    		final int len = sdData.getAllPointsNumber();
        	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
        	if (stride == null) {
                return false;
        	}
            sdData.setStride(stride);
            return true;
    	} else if (data instanceof SGSXYZNetCDFData) {
    		SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
    		if (!ncData.isIndexAvailable()) {
    			return false;
    		}
            return this.setNetCDFIndexStride(value);
    	} else if (data instanceof SGSXYZMDArrayData) {
    		SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) data;
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
	 * Returns a list of color map groups.
	 * 
	 * @return a list of color map groups
	 */
	public List<SGElementGroupPseudocolorMap> getColorMapGroups() {
		List<SGElementGroupPseudocolorMap> retList = new ArrayList<SGElementGroupPseudocolorMap>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupPseudocolorMap.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupPseudocolorMap) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns a color map group which is the first element of an array.
	 * 
	 * @return the first element of an array of arrow groups, or null when this
	 *         group set does not have any arrow groups
	 */
	public SGElementGroupPseudocolorMap getColorMapGroup() {
		return (SGElementGroupPseudocolorMap) SGUtilityForFigureElement
				.getGroup(SGElementGroupPseudocolorMap.class,
						this.mDrawingElementGroupList);
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
            if (gp instanceof SGElementGroupPseudocolorMap.PseudocolorMapProperties) {
                group = this.getColorMapGroup();
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
    	SGISXYZTypeData data = (SGISXYZTypeData) this.getData();
    	this.initPointsArray(data.getPointsNumber());
    	
    	// update the grid mode
    	if (this.updateGridMode() == false) {
    		return false;
    	}
    	
    	return true;
    }

    private boolean updateGridMode() {
    	final boolean b = this.isGridMode();
    	List<SGElementGroupPseudocolorMap> maps = this.getColorMapGroups();
    	for (int ii = 0; ii < maps.size(); ii++) {
    		SGElementGroupPseudocolorMap map = maps.get(ii);
    		if (map.setGridMode(b) == false) {
    			return false;
    		}
    	}
    	return true;
    }

    /**
     * Sets the data.
     * 
     * @param data
     *            a data object
     * @return true if succeeded
     */
    public boolean setData(SGData data) {
    	if (!super.setData(data)) {
    		return false;
    	}
    	if (data == null) {
        	this.initPointsArray(0);
    		return true;
    	} else {
        	if (!(data instanceof SGISXYZTypeData)) {
        		return false;
        	}
        	SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) data;
        	this.initPointsArray(dataSXYZ.getPointsNumber());
        	if (!this.updateDrawingElementsLocation(dataSXYZ)) {
        		return false;
        	}
    	}
    	return true;
    }

    /**
     * Updates the size of color map.
     * 
     * @return true if succeeded
     */
    public boolean updateColorMapSize() {
    	
    	SGISXYZTypeData data = (SGISXYZTypeData) this.getData();
    	
    	SGAxis axisX = this.getXAxis();
    	final double[] xArray = data.getXValueArray(false);
    	final double widthValue = SGUtilityForFigureElementJava2D.calcSizeValue(
    			xArray, axisX, true);
    	
    	SGAxis axisY = this.getYAxis();
    	final double[] yArray = data.getYValueArray(false);
    	final double heightValue = SGUtilityForFigureElementJava2D.calcSizeValue(
    			yArray, axisY, false);
    	
        SGElementGroupPseudocolorMap colorMap = (SGElementGroupPseudocolorMap) this.getColorMap();
        colorMap.setWidthValue(widthValue);
        colorMap.setHeightValue(heightValue);
        
        return true;
    }

    /**
     * Overrode to update the grid mode.
     * 
     */
    public boolean setProperties(SGProperties p ) {
    	if (super.setProperties(p) == false) {
    		return false;
    	}
    	this.updateGridMode();
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
	    		SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) this.mData;
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
	        		SGSXYZSDArrayData sdData = (SGSXYZSDArrayData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcSDArrayDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
        			sdData.setStride(stride);
	        	} else if (SGDataUtility.isNetCDFData(this.mData)) {
	        		SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcNetCDFDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet strideX = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X);
        			ncData.setXStride(strideX);
        			SGIntegerSeriesSet strideY = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y);
        			ncData.setYStride(strideY);
        			SGIntegerSeriesSet indexStride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
        			ncData.setIndexStride(indexStride);
	        	} else if (SGDataUtility.isMDArrayData(this.mData)) {
	        		if (!SGDataUtility.addGridType(cols, this.mData.getDataType(), infoMap)) {
	        			return false;
	        		}
	        		SGSXYZMDArrayData mdData = (SGSXYZMDArrayData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcMDArrayDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet strideX = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X);
        			mdData.setXStride(strideX);
        			SGIntegerSeriesSet strideY = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y);
        			mdData.setYStride(strideY);
        			SGIntegerSeriesSet indexStride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE);
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
    	
        // drawing elements
        SGElementGroupPseudocolorMapForData colorMapGroup = (SGElementGroupPseudocolorMapForData) this.getColorMapGroup();
        colorMapGroup.getProperties(map);

    	return map;
	}
	
	private final Map<SGDataViewerDialog, List<SGTuple2f>> mFocusedSymbolSizeListMap = new HashMap<SGDataViewerDialog, List<SGTuple2f>>();

	@Override
    public void removeFocusedValueList(final SGDataViewerDialog dg) {
    	super.removeFocusedValueList(dg);
    	this.mFocusedSymbolSizeListMap.remove(dg);
    }

	@Override
    public void initFocusedValueList(final SGDataViewerDialog dg) {
    	super.initFocusedValueList(dg);
    	this.mFocusedSymbolSizeListMap.put(dg, new ArrayList<SGTuple2f>());
    }

	@Override
    public void clearFocusedValueListMap() {
    	super.clearFocusedValueListMap();
    	this.mFocusedSymbolSizeListMap.clear();
    }

	@Override
    protected void setFocusedValueIndices(final SGDataViewerDialog dg,
    		List<SGXYSimpleIndexBlock> blockList) {

    	List<SGTuple2d> valueList = this.mFocusedValueListMap.get(dg);
    	List<SGTuple2f> sizeList = this.mFocusedSymbolSizeListMap.get(dg);
		SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) this.getData();
		SGElementGroupPseudocolorMap colorMap = (SGElementGroupPseudocolorMap) this.getColorMap();
		
    	if (this.isGridMode()) {
    		String columnType = dg.getColumnType();
    		float[] widthArray = colorMap.getWidthArray();
    		float[] heightArray = colorMap.getHeightArray();
            final double[] xValues = dataSXYZ.getXValueArray(true);
            final double[] yValues = dataSXYZ.getYValueArray(true);
            
            if (SGIDataColumnTypeConstants.Z_VALUE.equals(columnType)) {
            	for (SGXYSimpleIndexBlock block : blockList) {
            		// calculate the center-X of a block
            		SGIntegerSeries xSeries = block.getXSeries();
            		int[] xIndices = xSeries.getNumbers();
            		FocusSymbolResult xResult = this.calcFocusSymbolX(xIndices, xValues, widthArray);
            		final double xCenterValue = xResult.centerValue;
            		final float rectWidth = xResult.size;
            		
            		// calculate the center-Y of a block
            		SGIntegerSeries ySeries = block.getYSeries();
            		int[] yIndices = ySeries.getNumbers();
            		FocusSymbolResult yResult = this.calcFocusSymbolY(yIndices, yValues, heightArray);
            		final double yCenterValue = yResult.centerValue;
            		final float rectHeight = yResult.size;

            		valueList.add(new SGTuple2d(xCenterValue, yCenterValue));
            		sizeList.add(new SGTuple2f(rectWidth, rectHeight));
            	}
            	
            } else if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)) {
            	
        		// calculate the center-Y of a block
        		SGIntegerSeriesSet yStride = dataSXYZ.getYStride();
        		List<SGIntegerSeries> ySeriesList = yStride.getSeriesList();
        		double[] yCenterValueArray = new double[ySeriesList.size()];
        		float[] rectHeightArray = new float[ySeriesList.size()];
        		for (int ii = 0; ii < ySeriesList.size(); ii++) {
        			SGIntegerSeries ySeries = ySeriesList.get(ii);
            		int[] yIndices = ySeries.getNumbers();
            		FocusSymbolResult yResult = this.calcFocusSymbolY(yIndices, yValues, heightArray);
            		final double yCenterValue = yResult.centerValue;
            		final float rectHeight = yResult.size;
            		yCenterValueArray[ii] = yCenterValue;
            		rectHeightArray[ii] = rectHeight;
        		}

            	for (SGXYSimpleIndexBlock block : blockList) {
            		// calculate the center-X of a block
            		SGIntegerSeries rowSeries = block.getYSeries();
            		int[] xIndices = rowSeries.getNumbers();
            		FocusSymbolResult xResult = this.calcFocusSymbolX(xIndices, xValues, widthArray);
            		final double xCenterValue = xResult.centerValue;
            		final float rectWidth = xResult.size;
            		
        			// add to list
            		for (int ii = 0; ii < ySeriesList.size(); ii++) {
                		valueList.add(new SGTuple2d(xCenterValue, yCenterValueArray[ii]));
                		sizeList.add(new SGTuple2f(rectWidth, rectHeightArray[ii]));
            		}
            	}
            	
            } else if (SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {

        		// calculate the center-X of a block
        		SGIntegerSeriesSet xStride = dataSXYZ.getXStride();
        		List<SGIntegerSeries> xSeriesList = xStride.getSeriesList();
        		double[] xCenterValueArray = new double[xSeriesList.size()];
        		float[] rectWidthArray = new float[xSeriesList.size()];
        		for (int ii = 0; ii < xSeriesList.size(); ii++) {
        			SGIntegerSeries xSeries = xSeriesList.get(ii);
            		int[] xIndices = xSeries.getNumbers();
            		FocusSymbolResult xResult = this.calcFocusSymbolX(xIndices, xValues, widthArray);
            		final double xCenterValue = xResult.centerValue;
            		final float rectWidth = xResult.size;
            		xCenterValueArray[ii] = xCenterValue;
            		rectWidthArray[ii] = rectWidth;
        		}

            	for (SGXYSimpleIndexBlock block : blockList) {
            		// calculate the center-Y of a block
            		SGIntegerSeries rowSeries = block.getYSeries();
            		int[] yIndices = rowSeries.getNumbers();
            		FocusSymbolResult yResult = this.calcFocusSymbolY(yIndices, yValues, heightArray);
            		final double yCenterValue = yResult.centerValue;
            		final float rectHeight = yResult.size;
            		
        			// add to list
            		for (int ii = 0; ii < xSeriesList.size(); ii++) {
                		valueList.add(new SGTuple2d(xCenterValueArray[ii], yCenterValue));
                		sizeList.add(new SGTuple2f(rectWidthArray[ii], rectHeight));
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

        	final float minSize = 20.0f;
        	SGDrawingElement[] elArray = colorMap.getDrawingElementArray();
            SGTuple2d[] xyValueArray = dataSXYZ.getXYValueArray(false);
    		for (int pointIndex : pointIndexList) {
    			SGTuple2d value = xyValueArray[pointIndex];
    			valueList.add(value);
    			
    			PseudocolorMapRectangle rect = (PseudocolorMapRectangle) elArray[pointIndex];
    			float width = rect.getWidth();
    			if (width < minSize) {
    				width = minSize;
    			}
    			float height = rect.getHeight();
    			if (height < minSize) {
    				height = minSize;
    			}
    			SGTuple2f size = new SGTuple2f(width, height);
    			sizeList.add(size);
    		}
    	}
    }
	
	static class FocusSymbolResult {
		float size;
		double centerValue;
	}

	private FocusSymbolResult calcFocusSymbolX(int[] xIndices, double[] xValues,
			float[] widthArray) {
		List<Float> xLocationList = new ArrayList<Float>();
		for (int ii = 0; ii < xIndices.length; ii++) {
			final int xIndex = xIndices[ii];
			final double xValue = xValues[xIndex];
			final float center = this.mGraph.calcLocation(xValue, this.mXAxis, true);
			final float halfWidth = widthArray[xIndex] / 2;
			xLocationList.add(center - halfWidth);
			xLocationList.add(center + halfWidth);
		}
		float[] xLocationArray = new float[xLocationList.size()];
		for (int ii = 0; ii < xLocationList.size(); ii++) {
			xLocationArray[ii] = xLocationList.get(ii);
		}
		Arrays.sort(xLocationArray);
		final float xMinLocation = xLocationArray[0];
		final float xMaxLocation = xLocationArray[xLocationArray.length - 1];
		final float rectWidth = xMaxLocation - xMinLocation;
		final float xCenterLocation = (xMinLocation + xMaxLocation) / 2;
		final double xCenterValue = this.mGraph.calcValue(xCenterLocation, this.mXAxis, true);

		FocusSymbolResult ret = new FocusSymbolResult();
		ret.centerValue = xCenterValue;
		ret.size = rectWidth;
		return ret;
	}
	
	private FocusSymbolResult calcFocusSymbolY(int[] yIndices, double[] yValues,
			float[] heightArray) {
		List<Float> yLocationList = new ArrayList<Float>();
		for (int ii = 0; ii < yIndices.length; ii++) {
			final int yIndex = yIndices[ii];
			final double yValue = yValues[yIndex];
			final float center = this.mGraph.calcLocation(yValue, this.mYAxis, false);
			final float halfHeight = heightArray[yIndex] / 2;
			yLocationList.add(center - halfHeight);
			yLocationList.add(center + halfHeight);
		}
		float[] yLocationArray = new float[yLocationList.size()];
		for (int ii = 0; ii < yLocationList.size(); ii++) {
			yLocationArray[ii] = yLocationList.get(ii);
		}
		Arrays.sort(yLocationArray);
		final float yMinLocation = yLocationArray[0];
		final float yMaxLocation = yLocationArray[yLocationArray.length - 1];
		final float rectHeight = yMaxLocation - yMinLocation;
		final float yCenterLocation = (yMinLocation + yMaxLocation) / 2;
		final double yCenterValue = this.mGraph.calcValue(yCenterLocation, this.mYAxis, false);

		FocusSymbolResult ret = new FocusSymbolResult();
		ret.centerValue = yCenterValue;
		ret.size = rectHeight;
		return ret;
	}
	
	@Override
    protected void paintFocusedShapes(Graphics2D g2d) {
        g2d.setColor(new Color(255, 0, 0, 100));
        Iterator<Entry<SGDataViewerDialog, List<SGTuple2d>>> itr = this.mFocusedValueListMap.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<SGDataViewerDialog, List<SGTuple2d>> entry = itr.next();
        	SGDataViewerDialog dg = entry.getKey();
        	List<SGTuple2d> valueList = entry.getValue();
        	List<SGTuple2f> sizeList = this.mFocusedSymbolSizeListMap.get(dg);
        	for (int ii = 0; ii < valueList.size(); ii++) {
        		SGTuple2d value = valueList.get(ii);
        		SGTuple2f size = sizeList.get(ii);
        		final float width = size.x;
        		final float height = size.y;
        		final float xLocation = this.mGraph.calcLocation(value.x, mXAxis, true) - width / 2;
        		final float yLocation = this.mGraph.calcLocation(value.y, mYAxis, false) - height / 2;
        		Rectangle2D rect = new Rectangle2D.Float(xLocation, yLocation, width, height);
        		g2d.fill(rect);
        	}
        }
    }

	@Override
	protected String getToolTipText(final int x, final int y) {
		if (!this.mGraph.getGraphRect().contains(x, y)) {
			return null;
		}
		
		SGArrayData data = (SGArrayData) this.getData();
        SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) data;
        String spatiallyVariedText = null;
    	SGElementGroupPseudocolorMapInGraph colorMap = (SGElementGroupPseudocolorMapInGraph) this.getColorMap();
    	SXYZDataValue dataValue = null;
        if (this.isGridMode()) {
        	spatiallyVariedText = colorMap.getToolTipTextSpatiallyVaried(x, y);
        	SGTwoDimensionalArrayIndex index = (SGTwoDimensionalArrayIndex) colorMap.getIndexAt(x, y);
        	if (index == null) {
        		return null;
        	}
        	dataValue = colorMap.getDataValueAt(dataSXYZ, index.getColumn(), index.getRow());
        } else {
            final double[] xValueArray = dataSXYZ.getXValueArray(false);
            final double[] yValueArray = dataSXYZ.getYValueArray(false);
        	SGTuple2f[] pointsArray = this.getXYPoints(xValueArray, yValueArray);
        	spatiallyVariedText = colorMap.getToolTipTextSpatiallyVaried(x, y, pointsArray);
        	SGArrayIndex index = (SGArrayIndex) colorMap.getIndexAt(x, y, pointsArray);
        	if (index == null) {
        		return null;
        	}
        	dataValue = colorMap.getDataValueAt(dataSXYZ, index.getIndex());
        }
		String dataValueText = null;
		if (dataValue != null) {
			final double xValue = SGUtilityNumber.getNumberInRangeOrder(
					dataValue.xValue, this.mXAxis);
			final double yValue = SGUtilityNumber.getNumberInRangeOrder(
					dataValue.yValue, this.mYAxis);
			final double zValue;
			if (!dataValue.zValue.missing) {
				if (Double.isNaN(dataValue.zValue.number)) {
					zValue = dataValue.zValue.number;
				} else {
					zValue = SGUtilityNumber.getNumberInRangeOrder(
							dataValue.zValue.number, this.mZAxis);
				}
			} else {
				zValue = dataValue.zValue.number;
			}
			StringBuffer sb = new StringBuffer();
			sb.append("(");
			sb.append(xValue);
			sb.append(", ");
			sb.append(yValue);
			sb.append(", ");
			sb.append(zValue);
			sb.append(")");
			dataValueText = sb.toString();
		}
		String notSpatiallyVariedText = data.getToolTipTextNotSpatiallyVaried();
        
		return this.createToolTipText(spatiallyVariedText,
				notSpatiallyVariedText, dataValueText);
	}

	@Override
    public String getAxisValueString(final int x, final int y) {
        SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) this.mData;
    	SGTuple2f[] pointArray = null;
    	if (dataSXYZ.isIndexAvailable()) {
            final double[] xValueArray = dataSXYZ.getXValueArray(false);
            final double[] yValueArray = dataSXYZ.getYValueArray(false);
        	pointArray = this.getXYPoints(xValueArray, yValueArray);
    	}
    	SGElementGroupPseudocolorMapInGraph colorMap = (SGElementGroupPseudocolorMapInGraph) this.getColorMap();
    	Value zValue = colorMap.getZValue(x, y, pointArray);
    	if (zValue == null) {
    		return null;
    	}
    	double xValue = this.mGraph.calcValue(x, this.mXAxis, true);
    	double yValue = this.mGraph.calcValue(y, this.mYAxis, false);
    	xValue = SGUtilityNumber.getNumberInRangeOrder(xValue, this.mXAxis);
    	yValue = SGUtilityNumber.getNumberInRangeOrder(yValue, this.mYAxis);
    	if (!zValue.missing) {
    		if (!Double.isNaN(zValue.number)) {
            	zValue.number = SGUtilityNumber.getNumberInRangeOrder(zValue.number, this.mZAxis);
    		}
    	}

    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	sb.append(xValue);
    	sb.append(", ");
    	sb.append(yValue);
    	sb.append(", ");
    	sb.append(zValue.number);
    	sb.append(')');
    	return sb.toString();
    }

	@Override
    protected List<SGTwoDimensionalArrayIndex> getDataViewerIndex(final int x, final int y) {
		List<SGTwoDimensionalArrayIndex> ret = new ArrayList<SGTwoDimensionalArrayIndex>();
    	SGElementGroupPseudocolorMapInGraph colorMap = (SGElementGroupPseudocolorMapInGraph) this.getColorMap();
        if (this.isGridMode()) {
        	SGIIndex idx = colorMap.getIndexAt(x, y);
        	ret.add((SGTwoDimensionalArrayIndex) idx);
        } else {
            SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) this.getData();
            final double[] xValueArray = dataSXYZ.getXValueArray(false);
            final double[] yValueArray = dataSXYZ.getYValueArray(false);
        	SGTuple2f[] pointArray = this.getXYPoints(xValueArray, yValueArray);
        	SGArrayIndex index = (SGArrayIndex) colorMap.getIndexAt(x, y, pointArray);
    		ret.add(new SGTwoDimensionalArrayIndex(0, index.getIndex()));
        }
    	return ret;
	}
}
