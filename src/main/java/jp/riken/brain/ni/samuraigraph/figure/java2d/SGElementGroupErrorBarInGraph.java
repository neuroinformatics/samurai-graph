package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementErrorBar;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

import org.w3c.dom.Element;

/**
 * A group of error bars in the graph object.
 * 
 */
public class SGElementGroupErrorBarInGraph extends SGElementGroupErrorBarForData
        implements SGIElementGroupSXYInGraph, SGISXYDataConstants {

    /**
     * Maximum number of anchors.
     */
    public static final int MAX_NUMBER_OF_ANCHORS = 8;

    private SGFigureElementGraph mGraph = null;

    protected SGElementGroupSetInGraph mGroupSet = null;

    public boolean setElementGroupSet(SGElementGroupSetInGraph gs) {
        this.mGroupSet = gs;
        return true;
    }

    private SGTuple2f[] mLowerArray = null;

    private SGTuple2f[] mUpperArray = null;
    
    private SGTuple2f[] mCenterArray = null;

    private boolean mFocusedFlag = false;

    public void setFocused(final boolean b) {
        this.mFocusedFlag = b;
    }

    public boolean isFocused() {
        return this.mFocusedFlag;
    }

    public SGElementGroupErrorBarInGraph(SGISXYTypeSingleData data,
            SGFigureElementGraph graph) {
        super(data);
        this.mGraph = graph;
        final int num = (data != null) ? data.getPointsNumber() : 0;
        this.initDrawingElement(num);
    }

    public SGElementGroupErrorBarInGraph(SGISXYTypeMultipleData data,
            SGFigureElementGraph graph) {
        super(data);
        this.mGraph = graph;
        final int num = (data != null) ? data.getPointsNumber() : 0;
        this.initDrawingElement(num);
    }

    /**
     * 
     */
    public void dispose() {
        super.dispose();
        this.mCenterArray = null;
        this.mLowerArray = null;
        this.mUpperArray = null;
    }

    /**
     * 
     */
    public boolean writeProperty(final Element el) {
        if (super.writeProperty(el) == false) {
            return false;
        }
        el.setAttribute(KEY_VISIBLE, Boolean.toString(this.mVisibleFlag));
        return true;
    }

    /**
     * 
     * @param el
     * @return
     */
    public boolean readProperty(final Element el) {
        if (super.readProperty(el) == false) {
            return false;
        }

        return this.mGraph.readProperty(this, el);
    }

    protected boolean calcLocation(final SGISXYTypeSingleData dataSXY,
            final SGAxis axisX, final SGAxis axisY, final SGTuple2d[] valueArray,
            final SGTuple2f[] pointsArray) {

        final int num = dataSXY.getPointsNumber();
        final SGTuple2d[] lowerArray = new SGTuple2d[num];
        final SGTuple2d[] upperArray = new SGTuple2d[num];
        for (int ii = 0; ii < num; ii++) {
        	// initialize with center values
            lowerArray[ii] = new SGTuple2d(valueArray[ii]);
            upperArray[ii] = new SGTuple2d(valueArray[ii]);
        }
        final double[] lArray = dataSXY.getLowerErrorValueArray(false);
        final double[] uArray = dataSXY.getUpperErrorValueArray(false);
        
        Boolean vertical = dataSXY.isErrorBarVertical();
        if (vertical == null) {
        	return false;
        }

        // initialize the drawing elements
        if (this.mDrawingElementArray.length != num) {
        	this.initDrawingElement(num);
        }

        if (this.mErrorBarStyle == ERROR_BAR_BOTHSIDES) {
            for (int ii = 0; ii < num; ii++) {
                SGDrawingElementErrorBar eBar = (SGDrawingElementErrorBar) this.mDrawingElementArray[ii];
                final boolean lEnable = !Double.isNaN(lArray[ii]);
                final boolean uEnable = !Double.isNaN(uArray[ii]);
                eBar.setLowerVisible(lEnable);
                eBar.setUpperVisible(uEnable);
                final double l = lEnable ? - Math.abs(lArray[ii]) : 0.0;
                final double u = uEnable ? Math.abs(uArray[ii]) : 0.0;
                if (Boolean.TRUE.equals(vertical)) {
                    lowerArray[ii].y += l;
                    upperArray[ii].y += u;
                } else {
                    lowerArray[ii].x += l;
                    upperArray[ii].x += u;
                }
            }
        } else if (this.mErrorBarStyle == ERROR_BAR_UPSIDE) {
            for (int ii = 0; ii < num; ii++) {
                SGDrawingElementErrorBar eBar = (SGDrawingElementErrorBar) this.mDrawingElementArray[ii];
                final boolean uEnable = !Double.isNaN(uArray[ii]);
                eBar.setLowerVisible(false);
                eBar.setUpperVisible(uEnable);
                final double u = uEnable ? Math.abs(uArray[ii]) : 0.0;
                if (Boolean.TRUE.equals(vertical)) {
                    upperArray[ii].y += u;
                } else {
                    upperArray[ii].x += u;
                }
            }
        } else if (this.mErrorBarStyle == ERROR_BAR_DOWNSIDE) {
            for (int ii = 0; ii < num; ii++) {
                SGDrawingElementErrorBar eBar = (SGDrawingElementErrorBar) this.mDrawingElementArray[ii];
                final boolean lEnable = !Double.isNaN(lArray[ii]);
                eBar.setLowerVisible(lEnable);
                eBar.setUpperVisible(false);
                final double l = lEnable ? - Math.abs(lArray[ii]) : 0.0;
                if (Boolean.TRUE.equals(vertical)) {
                    lowerArray[ii].y += l;
                } else {
                    lowerArray[ii].x += l;
                }
            }
        } else {
            throw new Error();
        }
        
        // set visibility with log scale axis
        if (axisY.getScaleType() == SGAxis.LOG_SCALE) {
            for (int ii = 0; ii < num; ii++) {
                SGDrawingElementErrorBar eBar = (SGDrawingElementErrorBar) this.mDrawingElementArray[ii];
                final double cValue, lValue, uValue;
                if (Boolean.TRUE.equals(vertical)) {
                	cValue = valueArray[ii].y;
                	lValue = lowerArray[ii].y;
                	uValue = upperArray[ii].y;
                } else {
                	cValue = valueArray[ii].x;
                	lValue = lowerArray[ii].x;
                	uValue = upperArray[ii].x;
                }
                boolean lVisible = true;
                boolean uVisible = true;
                if (cValue <= 0.0) {
                	lVisible = false;
                	uVisible = false;
                } else {
                    if (lValue <= 0.0) {
                    	lVisible = false;
                    }
                    if (uValue <= 0.0) {
                    	uVisible = false;
                    }
                }
                eBar.setLowerVisible(lVisible);
                eBar.setUpperVisible(uVisible);
            }
        }

        // calculate the location
        this.mCenterArray = pointsArray;
        this.mGraph.calcLocationOfPoints(lowerArray, axisX, axisY, this.mLowerArray);
        this.mGraph.calcLocationOfPoints(upperArray, axisX, axisY, this.mUpperArray);

        return true;
    }
    
    /**
     * Overrode to update the attributes.
     * 
     * @param num
     *           the number of drawing elements
     */
    public boolean initDrawingElement(final int num) {
    	if (super.initDrawingElement(num) == false) {
    		return false;
    	}
        SGTuple2f[] centerArray = new SGTuple2f[num];
        SGTuple2f[] lowerArray = new SGTuple2f[num];
        SGTuple2f[] upperArray = new SGTuple2f[num];
        for (int ii = 0; ii < num; ii++) {
            centerArray[ii] = new SGTuple2f();
            lowerArray[ii] = new SGTuple2f();
            upperArray[ii] = new SGTuple2f();
        }
        this.mCenterArray = centerArray;
        this.mLowerArray = lowerArray;
        this.mUpperArray = upperArray;
    	return true;
    }

    /**
     * Update the location of error bars.
     * @return true if succeeded
     */
    public boolean updateLocation() {
        return this.setLocation(this.mCenterArray, this.mLowerArray, this.mUpperArray);
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
    	// if a given point is out of the graph rectangle, returns false;
    	Rectangle2D gRect = this.mGraph.getGraphRect();
    	if (!gRect.contains(x, y)) {
    	    return false;
    	}
        
        // if the data object do not have error bars, return false
        SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) this.mGraph.getData(this.mGroupSet);
        if (dataSXY == null) {
            return false;
        }
        if (dataSXY.isErrorBarAvailable() == false) {
            return false;
        }
        
    	return super.contains(x, y);
    }

    @Override
    public SGTuple2f getStartLocation(int index) {
        return this.mCenterArray[index];
    }

    @Override
    public SGTuple2f getLowerEndLocation(int index) {
        return this.mLowerArray[index];
    }

    @Override
    public SGTuple2f getUpperEndLocation(int index) {
        return this.mUpperArray[index];
    }

	@Override
	public boolean contains(int x, int y, SGTuple2f[] pointsArray,
			final double[] xValues, final double[] yValues) {
		Rectangle2D gRect = this.mGraph.getGraphRect();
		if (!gRect.contains(x, y)) {
		    return false;
		}
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	ErrorBarGroupInGraph el = (ErrorBarGroupInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray, xValues, yValues)) {
                    return true;
                }
            }
        }
        return false;
	}

    protected SGTuple2f getLocation(final int index, SGTuple2f[] pointsArray) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
            SGTuple2f location = gs.getLocation(index, pointsArray);
            return new SGTuple2f(location.x, location.y);
        } else {
            throw new Error("Not supported.");
        }
    }

    @Override
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new ErrorBarGroupInGraph(this, index);
    }

	protected class ErrorBarGroupInGraph extends ErrorBarInGroup {

		public ErrorBarGroupInGraph(SGElementGroupErrorBar group, int index) {
			super(group, index);
		}

        boolean contains(final int x, final int y, SGTuple2f[] pointsArray,
    			final double[] xValues, final double[] yValues) {
        	SGElementGroupErrorBarInGraph errorBarGroup = (SGElementGroupErrorBarInGraph) this.mGroup;
        	SGTuple2f location = errorBarGroup.getLocation(this.mIndex, pointsArray);
        	SGISXYTypeSingleData data = (SGISXYTypeSingleData) mGroupSet.getData();
        	final boolean vertical = isVertical();
        	SGAxis axis = vertical ? mGroupSet.mYAxis : mGroupSet.mXAxis;
        	final float lineWidth = this.getLineWidth() * this.getMagnification();
        	final double value = vertical ? yValues[this.mIndex] : xValues[this.mIndex];
        	if (this.isLowerVisible()) {
            	double[] leArray = data.getLowerErrorValueArray(false);
            	final double endValue = value - Math.abs(leArray[this.mIndex]);
            	if (this.contains(x, y, location, endValue, axis, vertical, lineWidth)) {
            		return true;
            	}
        	}
        	if (this.isUpperVisible()) {
            	double[] ueArray = data.getUpperErrorValueArray(false);
            	final double endValue = value + Math.abs(ueArray[this.mIndex]);
            	if (this.contains(x, y, location, endValue, axis, vertical, lineWidth)) {
            		return true;
            	}
        	}
        	return false;
        }
        
		private boolean contains(final int x, final int y, SGTuple2f centerPoint,
				double value, SGAxis axis, 
				final boolean vertical, final float lineWidth) {
        	final float endLocation = mGraph.calcLocation(value, axis, !vertical);
        	SGTuple2f endPoint = vertical ? new SGTuple2f(centerPoint.x, endLocation) : new SGTuple2f(endLocation, centerPoint.y);
        	if (SGDrawingElementLine2D.contains(x, y, centerPoint, endPoint, lineWidth)) {
        		return true;
        	}
        	final float size = this.getHeadSize() * this.getMagnification();
        	if (SGDrawingElementSymbol2D.contains(x, y, endPoint, size)) {
        		return true;
        	}
        	return false;
        }
	}
	
    @Override
	public String getToolTipTextSpatiallyVaried(List<Integer> indices) {
    	SGISXYTypeSingleData sxyData = (SGISXYTypeSingleData) this.mGroupSet.getData();
    	return sxyData.getToolTipSpatiallyVaried(indices.get(0));
	}

	@Override
	public List<Integer> getIndicesAt(final int x, final int y,
			final SGTuple2f[] pointsArray, final double[] xValues,
			final double[] yValues) {
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	ErrorBarGroupInGraph el = (ErrorBarGroupInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray, xValues, yValues)) {
                	final int index = el.getIndex();
                	SGISXYTypeSingleData data = (SGISXYTypeSingleData) this.mGroupSet.getData();
                	final int arrayIndex;
                	if (data.isStrideAvailable()) {
                    	SGIntegerSeriesSet stride = data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
                    	int[] indices = stride.getNumbers();
                    	arrayIndex = indices[index];
                	} else {
                		arrayIndex = index;
                	}
                	List<Integer> ret = new ArrayList<Integer>();
                	ret.add(arrayIndex);
                	return ret;
                }
            }
        }
        return null;
	}
}
