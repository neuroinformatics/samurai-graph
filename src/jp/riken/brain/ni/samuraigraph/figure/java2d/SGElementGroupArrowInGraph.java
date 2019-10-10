package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIIndex;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.VXYDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;

/**
 * The group of arrows.
 * 
 */
public class SGElementGroupArrowInGraph extends SGElementGroupArrowForData implements
        SGIElementGroupInGraph, SGIVXYDataConstants {

    /**
     * The graph.
     */
    protected SGFigureElementGraph mGraph = null;

    /**
     * The group set.
     */
    protected SGElementGroupSetInGraph mGroupSet = null;

    /**
     * A flag whether this group is focused.
     */
    private boolean mFocusedFlag = false;

    /**
     * Sets the group set that this element group set belongs to
     * @param gs
     *          a group set that this element group set belongs to
     * @return true if succeeded
     */
    public boolean setElementGroupSet(SGElementGroupSetInGraph gs) {
        this.mGroupSet = gs;
        return true;
    }

    /**
     * Sets the flag whether this group is focused.
     * 
     * @param b
     *         a value to set the the flag whether this group is focused
     */
    public void setFocused(final boolean b) {
        this.mFocusedFlag = b;
    }

    /**
     * Returns whether this group is focused
     * 
     * @return a flag whether this group is focused
     */
    public boolean isFocused() {
        return this.mFocusedFlag;
    }

    /**
     * Builds the element group.
     * 
     * @param graph
     *           the graph that this element group set belongs to
     */
    protected SGElementGroupArrowInGraph(SGFigureElementGraph graph) {
        super();
        this.mGraph = graph;
        
        // initialize
        this.setVisible(DEFAULT_ARROW_VISIBLE);
    }

    /**
     * The number of anchors for one direction.
     */
    public static final int NUMBER_OF_ANCHORS = 8;
    
    public static final Color FOCUS_COLOR = new Color(63, 63, 63, 63);

    /**
     * Paint this object.
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D rect) {
        if (super.paintElement(g2d, rect) == false) {
        	return false;
        }

        if (this.mGridMode) {
            if (this.isFocused()
            		&& this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
                final float minX = (float) this.mBounds.getMinX();
                final float maxX = (float) this.mBounds.getMaxX();
                final float minY = (float) this.mBounds.getMinY();
                final float maxY = (float) this.mBounds.getMaxY();
                final float width = maxX - minX;
                final float height = maxY - minY;

                g2d.setPaint(FOCUS_COLOR);
                Rectangle2D bounds = new Rectangle2D.Double(minX, minY, width, height);
                g2d.fill(bounds);

                // draw anchors
                final int num = NUMBER_OF_ANCHORS - 1;
                final float diffX = (maxX - minX) / num;
                final float diffY = (maxY - minY) / num;
                for (int ii = 0; ii <= num; ii++) {
                    final float y = minY + ii * diffY;
                    for (int jj = 0; jj <= num; jj++) {
                        final float x = minX + jj * diffX;
                        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(x, y, g2d);
                    }
                }
            }

        } else {
            if (this.isFocused()
                    && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
                SGDrawingElement[] array = this.mDrawingElementArray;
                for (int ii = 0; ii < array.length; ii++) {
                    SGDrawingElementArrow2D arrow = (SGDrawingElementArrow2D) array[ii];
                    emphasisArrow(arrow, g2d);
                }

            }
        }

        return true;
    }

    /**
     * 
     * @param g2d
     * @param symbol
     * @return
     */
    private boolean emphasisArrow(final SGDrawingElementArrow2D arrow,
            final Graphics2D g2d) {
        SGTuple2f start = arrow.getStart();
        SGTuple2f end = arrow.getEnd();
        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
        	new Point2D.Float(start.x, start.y), g2d);
        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(
                new Point2D.Float(end.x, end.y), g2d);
        return true;
    }

    /**
     * Returns whether this group set contains the given point.
     * 
     * @param x
     *          the x coordinate
     * @param y
     *          the y coordinate
     * @return
     *          true if this element group contains the given point
     */
    public boolean contains(final int x, final int y) {
        Rectangle2D gRect = this.mGraph.getGraphRect();
		if (!gRect.contains(x, y)) {
		    return false;
		}
    	if (this.mGridMode) {
            return this.mBounds.contains(x, y);
    	} else {
    		// if a given point is out of the graph rectangle, returns false;
    		return super.contains(x, y);
    	}
    }
    
    /**
     * 
     * @return
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new ArrowInGraph(this, index);
    }

    @Override
    public SGTuple2f getEndLocation(int index) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphVXY) {
            SGElementGroupSetInGraphVXY gs = (SGElementGroupSetInGraphVXY) this.mGroupSet;
            return gs.getEndLocation(index);
        } else {
            throw new Error("Not supported");
        }
    }

    @Override
    public SGTuple2f getStartLocation(int index) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphVXY) {
            SGElementGroupSetInGraphVXY gs = (SGElementGroupSetInGraphVXY) this.mGroupSet;
            return gs.getStartLocation(index);
        } else {
            throw new Error("Not supported");
        }
    }

	public boolean initDrawingElement(float[] xArray, float[] yArray) {
        final int num = xArray.length * yArray.length;
        if (this.initDrawingElement(num) == false) {
            return false;
        }
        return true;
	}
	
	VXYDataValue getComponentValues(final int x, final int y, 
    		final SGTuple2f[] startPointsArray, final SGTuple2f[] endPointsArray) {
		VXYDataValue ret = null;
    	SGData data = this.mGroupSet.getData();
        for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
        	ArrowInGraph el = (ArrowInGraph) this.mDrawingElementArray[ii];
            if (el.isVisible() == false) {
                continue;
            }
            if (el.contains(x, y, startPointsArray, endPointsArray)) {
            	ret = new VXYDataValue();
            	final double fValue = el.getFirstComponentValue();
            	final double sValue = el.getSecondComponentValue();
                if (data instanceof SGVXYNetCDFData) {
            		SGVXYNetCDFData ncData = (SGVXYNetCDFData) data;
                	if (this.isGridMode()) {
            			ret.fValue = new Value(ncData.getFirstComponentValueAt(
            					el.mYIndex, el.mXIndex));
            			ret.sValue = new Value(ncData.getSecondComponentValueAt(
            					el.mYIndex, el.mXIndex));
                	} else {
            			ret.fValue = new Value(ncData.getFirstComponentValueAt(
            					el.mIndex));
            			ret.sValue = new Value(ncData.getSecondComponentValueAt(
            					el.mIndex));
                	}
                } else {
                	ret.fValue = new Value(fValue);
                	ret.sValue = new Value(sValue);
                }
                return ret;
            }
        }
    	return ret;
    }

    protected class ArrowInGraph extends ArrowInGroup {
    	
        private double mFirstComponentValue;

        private double mSecondComponentValue;
        
        private int mXIndex;
        
        private int mYIndex;

		public ArrowInGraph(SGElementGroupArrow group, int index) {
			super(group, index);
		}
		
		public double getFirstComponentValue() {
			return this.mFirstComponentValue;
		}
		
		public double getSecondComponentValue() {
			return this.mSecondComponentValue;
		}
		
        public void setFirstComponentValue(final double value) {
            this.mFirstComponentValue = value;
        }

        public void setSecondComponentValue(final double value) {
            this.mSecondComponentValue = value;
        }
        
		boolean contains(final int x, final int y, SGTuple2f[] startPointsArray,
				SGTuple2f[] endPointsArray) {
			SGElementGroupArrowInGraph arrowGroup = (SGElementGroupArrowInGraph) this.mGroup;
        	SGTuple2f start = arrowGroup.getLocation(this.mIndex, startPointsArray);
        	SGTuple2f end = arrowGroup.getLocation(this.mIndex, endPointsArray);
        	return this.contains(x, y, start, end);
        }
		
		public void setIndex(final int xIndex, final int yIndex) {
			this.mXIndex = xIndex;
			this.mYIndex = yIndex;
		}
		
		public int getXIndex() {
			return this.mXIndex;
		}
		
		public int getYIndex() {
			return this.mYIndex;
		}
    }
    
    public void setComponentValues(final double[] fValues, final double[] sValues) {
    	for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
    		ArrowInGraph arrow = (ArrowInGraph) this.mDrawingElementArray[ii];
    		arrow.setFirstComponentValue(fValues[ii]);
    		arrow.setSecondComponentValue(sValues[ii]);
    	}
    }
    
    protected SGTuple2f getLocation(final int index, SGTuple2f[] pointsArray) {
        SGElementGroupSetInGraphVXY gs = (SGElementGroupSetInGraphVXY) this.mGroupSet;
        SGTuple2f location = gs.getLocation(index, pointsArray);
        return new SGTuple2f(location.x, location.y);
    }

	String getToolTipTextSpatiallyVaried(final int x, final int y,
			final SGTuple2f[] startPointsArray, final SGTuple2f[] endPointsArray) {
		String text = null;
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	ArrowInGraph el = (ArrowInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, startPointsArray, endPointsArray)) {
                	SGIVXYTypeData vxyData = (SGIVXYTypeData) this.mGroupSet.getData();
                	if (this.isGridMode()) {
                		final int xIndex = el.getXIndex();
                		final int yIndex = el.getYIndex();
                		text = vxyData.getToolTipSpatiallyVaried(xIndex, yIndex);
                	} else {
                    	final int index = el.getIndex();
                    	SGData data = this.mGroupSet.getData();
                    	final int arrayIndex;
                    	if (data.isStrideAvailable()) {
                        	SGIntegerSeriesSet stride = data.getIndexStride();
                        	int[] indices = stride.getNumbers();
                        	arrayIndex = indices[index];
                    	} else {
                    		arrayIndex = index;
                    	}
                    	text = vxyData.getToolTipSpatiallyVaried(arrayIndex);
                	}
                }
            }
        }
        return text;
	}

	SGIIndex getIndexAt(final int x, final int y,
			final SGTuple2f[] startPointsArray, final SGTuple2f[] endPointsArray) {
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	ArrowInGraph el = (ArrowInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, startPointsArray, endPointsArray)) {
                	if (this.isGridMode()) {
                		final int xIndex = el.getXIndex();
                		final int yIndex = el.getYIndex();
                		return new SGTwoDimensionalArrayIndex(xIndex, yIndex);
                	} else {
                    	final int index = el.getIndex();
                    	SGData data = this.mGroupSet.getData();
                    	final int arrayIndex;
                    	if (data.isStrideAvailable()) {
                        	SGIntegerSeriesSet stride = data.getIndexStride();
                        	int[] indices = stride.getNumbers();
                        	arrayIndex = indices[index];
                    	} else {
                    		arrayIndex = index;
                    	}
                    	return new SGArrayIndex(arrayIndex);
                	}
                }
            }
        }
		return null;
	}

    /**
     * Sets the location of arrows as a list of blocks.
     * 
     * @param firstComponentValueBlockList
     *           the list of arrays of integer series for the first component
     * @param secondComponentValueBlockList
     *           the list of arrays of integer series for the second component
     * @return true if succeeded
     */
    protected boolean setLocation(
    		final float[] xArray, final float[] yArray,
    		final List<SGXYSimpleDoubleValueIndexBlock> firstComponentValueBlockList,
    		final List<SGXYSimpleDoubleValueIndexBlock> secondComponentValueBlockList, 
    		final boolean polar, final float magPerCM, final boolean invariant, 
    		final double xyRatio, final SGTuple2f[] startArray, final SGTuple2f[] endArray) {
    	
    	if (!this.isGridMode()) {
    		throw new Error("This shouldn't happen.");
    	}
    	if (firstComponentValueBlockList == null || secondComponentValueBlockList == null) {
            throw new IllegalArgumentException("firstComponentValueBlockList == null || secondComponentValueBlockList == null");
    	}
    	if (firstComponentValueBlockList.size() != secondComponentValueBlockList.size()) {
            throw new IllegalArgumentException("firstComponentValueBlockList.size() != secondComponentValueBlockList.size()");
    	}
    	if (startArray.length != endArray.length) {
            throw new IllegalArgumentException("startArray.length != endArray.length");
    	}
        if (startArray.length != this.mDrawingElementArray.length) {
        	this.initDrawingElement(startArray.length);
        }

        List<Point2D> endLocationList = new ArrayList<Point2D>();
        final float factor = this.getMagnification() / (SGIConstants.CM_POINT_RATIO * magPerCM);
        int pointIndexOffset = 0;
        for (int ii = 0; ii < firstComponentValueBlockList.size(); ii++) {
        	SGXYSimpleDoubleValueIndexBlock firstComponentBlock = firstComponentValueBlockList.get(ii);
        	SGXYSimpleDoubleValueIndexBlock secondComponentBlock = secondComponentValueBlockList.get(ii);
        	SGIntegerSeries xSeries = firstComponentBlock.getXSeries();
        	SGIntegerSeries ySeries = firstComponentBlock.getYSeries();
        	final int[] xIndices = xSeries.getNumbers();
        	final int[] yIndices = ySeries.getNumbers();
        	final int numInBlock = xIndices.length * yIndices.length;
        	final double[] fValues = firstComponentBlock.getValues();
        	final double[] sValues = secondComponentBlock.getValues();
        	if (fValues.length != numInBlock || sValues.length != numInBlock) {
        		throw new Error("Invalid number of points in a block.");
        	}
        	for (int yy = 0; yy < yIndices.length; yy++) {
        		final int yIndex = yIndices[yy];
                final float startY = yArray[yIndex];
                
        		for (int xx = 0; xx < xIndices.length; xx++) {
        			final int xIndex = xIndices[xx];
                    final float startX = xArray[xIndex];
                    final int index = xIndices.length * yy + xx;
                    final int pointIndex = index + pointIndexOffset;

                    ArrowInGraph arrow = (ArrowInGraph) this.mDrawingElementArray[pointIndex];
                    arrow.setIndex(xIndex, yIndex);

                    // sets the start location
                    startArray[pointIndex].setValues(startX, startY);

                    // sets the components
                    final double fValue = fValues[index];
                    final double sValue = sValues[index];
                    if (Double.isNaN(fValue) || Double.isInfinite(fValue)
                    		|| Double.isNaN(sValue) || Double.isInfinite(sValue)) {
                    	endArray[pointIndex].setValues(Float.NaN, Float.NaN);
                        continue;
                    }
                    
                    // sets the end location
                    SGTuple2f end = SGArrowUtility.calcEndLocation(startX, startY, fValue, sValue, 
                    		polar, magPerCM, invariant, factor, xyRatio);
                    endArray[pointIndex].setValues(end);

                    // adds to the list
                    if (!end.isNaN()) {
                        endLocationList.add(new Point2D.Float(end.x, end.y));
                    }
        		}
        	}
        	
        	pointIndexOffset += xIndices.length * yIndices.length;
        }

        // sets the bounds
        Rectangle2D rectStart = this.calcBounds(xArray, yArray);
        Rectangle2D rectEnd = SGUtilityForFigureElementJava2D.getPointsBoundingBox(endLocationList);
        this.mBounds = (rectEnd != null) ? rectStart.createUnion(rectEnd) : rectStart;
        
        // sets to the attributes
        this.mXCoordinateArray = xArray.clone();
        this.mYCoordinateArray = yArray.clone();
    	
    	return true;
    }

    // Calculates the bounds of the data points.
    private Rectangle2D calcBounds(float[] xCoordinateArray, float[] yCoordinateArray) {

        final int xNum = xCoordinateArray.length;
        final int yNum = yCoordinateArray.length;
        
        // get the bounds
        float xMin, xMax, yMin, yMax;
        final float xMinMargin;
        final float xMaxMargin;
        final float yMinMargin;
        final float yMaxMargin;

        if (xNum == 1) {
            xMin = xCoordinateArray[0];
            xMax = xMin;
            xMinMargin = SINGLE_VALUE_MARGIN * this.getMagnification();
            xMaxMargin = xMinMargin;
        } else {
            if (xCoordinateArray[0] < xCoordinateArray[xNum - 1]) {
                xMin = xCoordinateArray[0];
                xMax = xCoordinateArray[xNum - 1];
                xMinMargin = Math.abs(xCoordinateArray[1] - xCoordinateArray[0]) / 2.0f;
                xMaxMargin = Math.abs(xCoordinateArray[xNum - 1] - xCoordinateArray[xNum - 2]) / 2.0f;
            } else {
                xMin = xCoordinateArray[xNum - 1];
                xMax = xCoordinateArray[0];
                xMinMargin = Math.abs(xCoordinateArray[xNum - 1] - xCoordinateArray[xNum - 2]) / 2.0f;
                xMaxMargin = Math.abs(xCoordinateArray[1] - xCoordinateArray[0]) / 2.0f;
            }
        }
        
        if (yNum == 1) {
            yMin = yCoordinateArray[0];
            yMax = yMin;
            yMinMargin = SINGLE_VALUE_MARGIN * this.getMagnification();
            yMaxMargin = yMinMargin;
        } else {
            if (yCoordinateArray[0] < yCoordinateArray[yNum - 1]) {
                yMin = yCoordinateArray[0];
                yMax = yCoordinateArray[yNum - 1];
                yMinMargin = Math.abs(yCoordinateArray[1] - yCoordinateArray[0]) / 2.0f;
                yMaxMargin = Math.abs(yCoordinateArray[yNum - 1] - yCoordinateArray[yNum - 2]) / 2.0f;
            } else {
                yMin = yCoordinateArray[yNum - 1];
                yMax = yCoordinateArray[0];
                yMinMargin = Math.abs(yCoordinateArray[yNum - 1] - yCoordinateArray[yNum - 2]) / 2.0f;
                yMaxMargin = Math.abs(yCoordinateArray[1] - yCoordinateArray[0]) / 2.0f;
            }
        }
        
        xMin -= xMinMargin;
        xMax += xMaxMargin;
        yMin -= yMinMargin;
        yMax += yMaxMargin;
        final float w = xMax - xMin;
        final float h = yMax - yMin;
        return new Rectangle2D.Float(xMin, yMin, w, h);
    }

	VXYDataValue getDataValueAt(SGIVXYTypeData data, final int index) {
		return (VXYDataValue) SGDataUtility.getDataValue(data, index);
	}

	VXYDataValue getDataValueAt(SGIVXYTypeData data, final int xIndex,
			final int yIndex) {
		return (VXYDataValue) SGDataUtility.getDataValue(data, xIndex, yIndex);
	}

}
