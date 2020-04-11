package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

import org.w3c.dom.Element;

/**
 * @author okumura
 */
public class SGElementGroupBarInGraph extends SGElementGroupBarForData implements
		SGIElementGroupSXYInGraph, SGISXYDataConstants {

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

    private boolean mFocusedFlag = false;

    public void setFocused(final boolean b) {
        this.mFocusedFlag = b;
    }

    public boolean isFocused() {
        return this.mFocusedFlag;
    }

    /**
     * 
     */
    public SGElementGroupBarInGraph(SGISXYTypeData data,
    		SGFigureElementGraph graph) {
        super(data);
        this.mGraph = graph;
    }

    /**
     * 
     * @return
     */
    public boolean initDrawingElement(final int num) {
        super.initDrawingElement(num);
        return true;
    }

    /**
     * 
     * @return
     */
    protected boolean initDrawingElement(final SGTuple2f[] array) {
        super.initDrawingElement(array);
        return true;
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

    /**
     * 
     */
    private float getBaselineLocation(final double baselineValue) {
    	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
    	final boolean vertical = this.isVertical();
        final SGAxis axis = vertical ? gs.getYAxis() : gs.getXAxis();
        return this.mGraph.calcLocation(baselineValue, axis, !vertical);
    }

    /**
     * 
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {
        if (this.mDrawingElementArray == null) {
            return true;
        }

        if (pointArray.length != this.mDrawingElementArray.length) {
            this.initDrawingElement(pointArray);
        }

        // get the location of the baseline
        final float baseline = this.getBaselineLocation(this.getBaselineValue());

        boolean baselineAvailable = true;
        if (Float.isInfinite(baseline) || Float.isNaN(baseline)) {
            baselineAvailable = false;
        }

        final double range = this.calcAxisRange();

        // set the bounds of drawing elements
        final float sizeRatio = (float) (Math.abs(this.getWidthValue()) / range);
        float w = 0.0f;
        float h = 0.0f;
        if (this.mVerticalFlag) {
            w = this.mGraph.getGraphRectWidth() * sizeRatio;
            this.setRectangleWidth(w);
        } else {
            h = this.mGraph.getGraphRectHeight() * sizeRatio;
            this.setRectangleHeight(h);
        }
        
        // calculates the offset
        LocationInfo locInfo = this.getLocationInfo(range);
        final float xOffset = locInfo.xOffset;
        final float yOffset = locInfo.yOffset;
        
        for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            final SGDrawingElementBar2D bar = (SGDrawingElementBar2D) this.mDrawingElementArray[ii];
            final float x = pointArray[ii].x + xOffset;
            final float y = pointArray[ii].y + yOffset;
            if (!baselineAvailable || Float.isInfinite(x) || Float.isNaN(x) 
            		|| Float.isInfinite(y) || Float.isNaN(y)) {
                bar.setVisible(false);
                continue;
            } else {
                bar.setVisible(true);
            }
            if (this.mVerticalFlag) {
                h = baseline - y;
            } else {
                w = baseline - x;
            }
            bar.setBounds(x, y, w, h);
        }

        return true;
    }
    
    static class LocationInfo {
    	float xOffset;
    	float yOffset;
    }
    
    private double calcAxisRange() {
        final SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
        final SGAxis axis = this.mVerticalFlag ? gs.getXAxis() : gs.getYAxis();
        final double minValue = axis.getMinDoubleValue();
        final double maxValue = axis.getMaxDoubleValue();
        final double range = maxValue - minValue;
        return range;
    }
    
    private LocationInfo getLocationInfo(final double range) {
        final SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
        float xOffset = 0.0f;
        float yOffset = 0.0f;
        final float offsetRatio = (float) (Math.abs(this.getInterval()) / range);
        final float interval;
        if (this.mVerticalFlag) {
        	interval = this.mGraph.getGraphRectWidth() * offsetRatio;
        } else {
        	interval = this.mGraph.getGraphRectHeight() * offsetRatio;
        }
        final int numSeries = gs.getNumberOfSeries();
        final int seriesIndex = gs.getSeriesIndex();
        final float offset = interval * (seriesIndex - 0.5f * numSeries + 0.5f);
        if (this.mVerticalFlag) {
        	xOffset = offset;
        } else {
        	yOffset = offset;
        }
        
        LocationInfo ret = new LocationInfo();
        ret.xOffset = xOffset;
        ret.yOffset = yOffset;
        return ret;
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D rect) {
        super.paintElement(g2d, rect);

        // ElementGroupSetInGraph gs = (ElementGroupSetInGraph)this.mGroupSet;
        SGDrawingElement[] array = this.mDrawingElementArray;

        if (this.isFocused()
                && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
            final int num = array.length;
            if (num <= MAX_NUMBER_OF_ANCHORS) {
                for (int ii = 0; ii < num; ii++) {
                    SGDrawingElementBar2D bar = (SGDrawingElementBar2D) array[ii];
                    emphasisBar(bar, g2d);
                }
            } else {
                int div = num / MAX_NUMBER_OF_ANCHORS;
                int cnt = 0;
                while (true) {
                    SGDrawingElementBar2D bar = (SGDrawingElementBar2D) array[cnt];
                    emphasisBar(bar, g2d);
                    cnt += div;
                    if (cnt >= num) {
                        break;
                    }
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
    private boolean emphasisBar(final SGDrawingElementBar2D bar,
            final Graphics2D g2d) {
        SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(bar
                .getElementBounds(), g2d);
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
	// if a given point is out of the graph rectangle, returns false;
	Rectangle2D gRect = this.mGraph.getGraphRect();
	if (!gRect.contains(x, y)) {
	    return false;
	}
	return super.contains(x, y);
    }

    @Override
    public float getX(int index) {
        return this.getLocation(index).x;
    }

    @Override
    public float getY(final int index) {
        return this.getLocation(index).y;
    }
    
    public SGTuple2f getLocation(final int index) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
            return gs.getBarLocation(index);
        } else {
            throw new Error("Not supported.");
        }
    }
    
    /**
     * Sets the baseline value.
     * 
     * @param value
     *           axis value to set to the baseline value
     * @return true if succeeded
     */
    @Override
    public boolean setBaselineValue(final double value) {
        if (this.isVisible()) {
            SGAxis axis = this.isVertical() ? this.mGroupSet.getYAxis() : this.mGroupSet.getXAxis();
    	    if (axis.isValidValue(value) == false) {
    	        return false;
    	    }
    	}
        if (super.setBaselineValue(value) == false) {
        	return false;
        }
        return true;
    }

    /**
     * Sets the width value.
     * 
     * @param value
     *          axis value to set to the width value
     * @return true if succeeded
     */
    public boolean setWidthValue(final double value) {
    	SGAxis axis = this.isVertical() ? this.mGroupSet.getXAxis() : this.mGroupSet.getYAxis();
    	if (axis.isValidValue(value) == false) {
    		return false;
    	}
        if (super.setWidthValue(value) == false) {
        	return false;
        }
        return true;
    }
    
    /**
     * 
     * @return
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new BarInGraph(this, index);
    }

    protected class BarInGraph extends BarInGroup {
    	
    	private float mX;
    	
    	private float mY;
    	
        /**
         * Builds a rectangle in a group of rectangles.
         * 
         * @param group
         *           a group of rectangles
         */
        public BarInGraph(SGElementGroupBarInGraph group, final int index) {
            super(group, index);
        }
        
        /**
         * Returns the bounding box of this bar.
         * 
         * @return a bounding box of this bar
         */
        public Rectangle2D getElementBounds() {
            final float x = this.getX();
            final float y = this.getY();
            final float w = this.getWidth();
            final float h = this.getHeight();

            final float ww = Math.abs(w);
            final float hh = Math.abs(h);

            float xx;
            float yy;
            if (this.isVertical()) {
                xx = x - 0.50f * ww;
                yy = h > 0.0f ? y : y - hh;
            } else {
                xx = w > 0.0f ? x : x - ww;
                yy = y - 0.50f * hh;
            }

            Rectangle2D rect = new Rectangle2D.Float(xx, yy, ww, hh);
            return rect;
        }

        @Override
        public float getX() {
            return this.mX;
        }

        @Override
        public float getY() {
            return this.mY;
        }

        @Override
        public boolean setX(float x) {
        	this.mX = x;
        	return true;
        }

        @Override
        public boolean setY(float y) {
        	this.mY = y;
            return true;
        }

        @Override
        public float getWidth() {
            return this.isVertical() ? this.mGroup.getRectangleWidth() : this.mWidth;
        }

        @Override
        public float getHeight() {
            return this.isVertical() ? this.mHeight : this.mGroup.getRectangleHeight();
        }

        @Override
        public boolean setWidth(float w) {
            this.mWidth = w;
            return true;
        }

        @Override
        public boolean setHeight(float h) {
            this.mHeight = h;
            return true;
        }
        
        boolean contains(final int x, final int y, SGTuple2f[] pointsArray,
    			final double[] xValues, final double[] yValues) {
        	SGElementGroupBarInGraph barGroup = (SGElementGroupBarInGraph) this.mGroup;
        	final double range = calcAxisRange();
        	LocationInfo locInfo = getLocationInfo(range);
        	final float xOffset = locInfo.xOffset;
        	final float yOffset = locInfo.yOffset;
        	SGTuple2f location = barGroup.getLocation(this.mIndex, pointsArray);
        	final float width = Math.abs(this.getWidth());
        	final float height = Math.abs(this.getHeight());
        	final double baseline = this.getBaselineValue();
        	final float barX, barY;
        	if (this.isVertical()) {
        		barX = location.x - width / 2 + xOffset;
    			final float baseLocation = mGraph.calcLocation(baseline, mGroupSet.mYAxis, false);
        		barY = Math.min(baseLocation, location.y) + yOffset;
        	} else {
        		barY = location.y - height / 2 + yOffset;
        		final float baseLocation = mGraph.calcLocation(baseline, mGroupSet.mXAxis, true);
        		barX = Math.min(baseLocation, location.x) + xOffset;
        	}
        	Rectangle2D rect = new Rectangle2D.Float(barX, barY, width, height);
        	return rect.contains(x, y);
        }
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
            	BarInGraph el = (BarInGraph) this.mDrawingElementArray[ii];
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
            	BarInGraph el = (BarInGraph) this.mDrawingElementArray[ii];
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
