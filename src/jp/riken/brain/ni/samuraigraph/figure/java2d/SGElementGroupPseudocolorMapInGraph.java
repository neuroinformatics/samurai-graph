package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIIndex;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYZDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.Value;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYZDataConstants;

import org.w3c.dom.Element;

/**
 * The pseudocolor map in the graph object.
 *
 */
public class SGElementGroupPseudocolorMapInGraph extends SGElementGroupPseudocolorMapForData
    implements SGIElementGroupInGraph, SGISXYZDataConstants {

    /**
     * Maximum number of anchors.
     */
    public static final int MAX_NUMBER_OF_ANCHORS = 20;
    
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
    
    public SGElementGroupPseudocolorMapInGraph(SGFigureElementGraph graph) {
        super();
        this.mGraph = graph;
        
        // initialize
        this.setVisible(DEFAULT_COLOR_MAP_VISIBLE);
    }

    /**
     * Sets the location of each rectangle.
     * 
     * @param pointArray
     *          an array of location
     */
    public boolean setLocation(SGTuple2f[] pointArray) {
        if (this.mDrawingElementArray == null) {
            return true;
        }
        if (pointArray.length != this.mDrawingElementArray.length) {
        	this.initDrawingElement(pointArray);
        }
        final SGElementGroupSetInGraphSXYZ gs = (SGElementGroupSetInGraphSXYZ) this.mGroupSet;
        final SGAxis xAxis = gs.getXAxis();
        final SGAxis yAxis = gs.getYAxis();
        final double xMin = xAxis.getMinDoubleValue();
        final double xMax = xAxis.getMaxDoubleValue();
        final double yMin = yAxis.getMinDoubleValue();
        final double yMax = yAxis.getMaxDoubleValue();
        final float w = this.mGraph.getGraphRectWidth()
                * (float) (this.mWidthValue / (xMax - xMin));
        final float h = this.mGraph.getGraphRectHeight()
                * (float) (this.mHeightValue / (yMax - yMin));
        this.setRectangleWidth(w);
        this.setRectangleHeight(h);

        for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            // set the bounds of rectangle
            final PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[ii];
            final float x = pointArray[ii].x;
            final float y = pointArray[ii].y;
            rect.setBounds(x, y, w, h);
            
            // updates the color
            if (this.hasValidZValue(rect)) {
                final double zValue = rect.getZValue();
                final Color cl = this.mColorBarModel.evaluate(zValue);
                rect.setInnerPaint(new SGFillPaint(cl));
            }
        }
        
        return true;
    }
    
    protected void updateVisibility() {
        for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            // set the bounds of rectangle
            final PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[ii];
            final float x = rect.getX();
            final float y = rect.getY();
            if (Float.isInfinite(x) || Float.isNaN(x) 
                    || Float.isInfinite(y) || Float.isNaN(y)) {
                rect.setVisible(false);
                continue;
            }
            rect.setVisible(this.hasValidZValue(rect));
        }
    }
    
    private boolean hasValidZValue(PseudocolorMapRectangle rect) {
        final double zValue = rect.getZValue();
        if (Double.isNaN(zValue) || Double.isInfinite(zValue)) {
        	return false;
        } else {
        	if (this.mColorBarModel.getScaleType() == SGAxis.LOG_SCALE && zValue <= 0.0) {
        		return false;
        	}
        }
        return true;
    }
    
    /**
     * The number of anchors for one direction.
     */
    public static final int NUMBER_OF_ANCHORS = 8;
    
    public static final Color FOCUS_COLOR = new Color(63, 63, 63, 63);

    /**
     * Paint the objects.
     */
    public boolean paintElement(Graphics2D g2d, Rectangle2D clipRect) {
        super.paintElement(g2d, clipRect);

        if (this.mGridMode) {
            if (this.isFocused() && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
                final float minX = (float) this.mBounds.getMinX();
                final float maxX = (float) this.mBounds.getMaxX();
                final float minY = (float) this.mBounds.getMinY();
                final float maxY = (float) this.mBounds.getMaxY();
                final float width = maxX - minX;
                final float height = maxY - minY;

                g2d.setPaint(FOCUS_COLOR);
                Rectangle2D rect = new Rectangle2D.Double(minX, minY, width, height);
                g2d.fill(rect);

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
            SGDrawingElement[] array = this.mDrawingElementArray;
            if (this.isFocused() && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
                final int num = array.length;
                if (num <= MAX_NUMBER_OF_ANCHORS) {
                    for (int ii = 0; ii < num; ii++) {
                        PseudocolorMapRectangle rect = (PseudocolorMapRectangle) array[ii];
                        Point2D pos = new Point2D.Float();
                        pos.setLocation(rect.getX() + rect.getWidth() / 2.0, rect.getY() + rect.getHeight() / 2.0);
                        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pos, g2d);
                    }
                } else {
                    int div = num / MAX_NUMBER_OF_ANCHORS;
                    int cnt = 0;
                    while (true) {
                        PseudocolorMapRectangle rect = (PseudocolorMapRectangle) array[cnt];
                        Point2D pos = new Point2D.Float();
                        pos.setLocation(rect.getX() + rect.getWidth() / 2.0, rect.getY() + rect.getHeight() / 2.0);
                        SGUtilityForFigureElementJava2D.drawAnchorAsFocusedObject(pos, g2d);
                        cnt += div;
                        if (cnt >= num) {
                            break;
                        }
                    }
                }
            }
        }

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
    	if (this.mGridMode) {
            Rectangle2D gRect = this.mGraph.getGraphRect();
            Rectangle2D uRect = gRect.createIntersection(this.mBounds);
            return uRect.contains(x, y);
    	} else {
            // if a given point is out of the graph rectangle, returns false;
            Rectangle2D gRect = this.mGraph.getGraphRect();
            if (!gRect.contains(x, y)) {
                return false;
            }
            return super.contains(x, y);
    	}
    }
    
    /**
     * Write properties to the Element object.
     * 
     * @param el
     *          the Element object
     */
    public boolean writeProperty(final Element el) {
        if (super.writeProperty(el) == false) {
            return false;
        }
        el.setAttribute(KEY_VISIBLE, Boolean.toString(this.mVisibleFlag));
        return true;
    }

    /**
     * Read properties from the Element object.
     * 
     * @param el
     *          the Element object
     */
    public boolean readProperty(final Element el) {
        if (super.readProperty(el) == false) {
            return false;
        }
        return this.mGraph.readProperty(this, el);
    }

    @Override
    public float getX(int index) {
        return this.getLocation(index).x;
    }

    @Override
    public float getY(int index) {
        return this.getLocation(index).y;
    }
    
    /**
     * Returns the location at a given index.
     * 
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getLocation(int index) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXYZ) {
            SGElementGroupSetInGraphSXYZ gs = (SGElementGroupSetInGraphSXYZ) this.mGroupSet;
            return gs.getLocation(index);
        } else {
            throw new Error("Not supported.");
        }
    }
    
    Value getZValue(final int x, final int y, final SGTuple2f[] pointArray) {
    	Value ret = null;
    	SGData data = this.mGroupSet.getData();
    	if (this.isGridMode()) {
            for (SGXYSimpleDoubleValueIndexBlock block : this.mZValueBlockList) {
            	SGIntegerSeries xSeries = block.getXSeries();
            	SGIntegerSeries ySeries = block.getYSeries();
            	final int[] xIndices = xSeries.getNumbers();
            	final int[] yIndices = ySeries.getNumbers();
            	final double[] values = block.getValues();
            	for (int yy = 0; yy < yIndices.length; yy++) {
            		final int yIndex = yIndices[yy];
            		final float height = this.mHeightArray[yIndex];
                    final float yCur = this.mYCoordinateArray[yIndex] - height / 2.0f;
                    if (!SGUtilityNumber.contains(yCur, yCur + height, y)) {
                    	continue;
                    }
                    boolean found = false;
            		for (int xx = 0; xx < xIndices.length; xx++) {
                        final int index = xIndices.length * yy + xx;
            			final int xIndex = xIndices[xx];
                		final float width = this.mWidthArray[xIndex];
                        final float xCur = this.mXCoordinateArray[xIndex] - width / 2.0f;
                        if (!SGUtilityNumber.contains(xCur, xCur + width, x)) {
                        	continue;
                        }
                        final double value = values[index];
                        if (Double.isNaN(value) && data instanceof SGSXYZNetCDFData) {
                    		SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
                            ret = new Value(ncData.getZValueAt(yIndex, xIndex));
                        } else {
                            ret = new Value(value);
                        }
                        found = true;
                        break;
            		}
            		if (found) {
            			break;
            		}
            	}
            }
            
    	} else {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	PseudocolorMapRectangleInGraph el = (PseudocolorMapRectangleInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointArray)) {
                    final double value = el.getZValue();
                    if (Double.isNaN(value) && data instanceof SGSXYZNetCDFData) {
                		SGSXYZNetCDFData ncData = (SGSXYZNetCDFData) data;
        				ret = new Value(ncData.getZValueAt(el.getIndex()));
                    } else {
                    	ret = new Value(value);
                    }
                    break;
                }
            }
    	}
    	
    	return ret;
    }

	String getToolTipTextSpatiallyVaried(final int x, final int y,
			final SGTuple2f[] pointsArray) {
		String text = null;
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	PseudocolorMapRectangleInGraph el = (PseudocolorMapRectangleInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray)) {
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
                	SGISXYZTypeData sxyzData = (SGISXYZTypeData) data;
                	text = sxyzData.getToolTipSpatiallyVaried(arrayIndex);
                	break;
                }
            }
        }
		return text;
	}
	
	SGIIndex getIndexAt(final int x, final int y,
			final SGTuple2f[] pointsArray) {
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	PseudocolorMapRectangleInGraph el = (PseudocolorMapRectangleInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray)) {
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
		return null;
	}

	SGIIndex getIndexAt(final int x, final int y) {
		SGIIndex ret = null;
        for (SGXYSimpleDoubleValueIndexBlock block : this.mZValueBlockList) {
        	SGIntegerSeries xSeries = block.getXSeries();
        	SGIntegerSeries ySeries = block.getYSeries();
        	final int[] xIndices = xSeries.getNumbers();
        	final int[] yIndices = ySeries.getNumbers();
        	for (int yy = 0; yy < yIndices.length; yy++) {
        		final int yIndex = yIndices[yy];
        		final float height = this.mHeightArray[yIndex];
                final float yCur = this.mYCoordinateArray[yIndex] - height / 2.0f;
                if (!SGUtilityNumber.contains(yCur, yCur + height, y)) {
                	continue;
                }
                boolean found = false;
        		for (int xx = 0; xx < xIndices.length; xx++) {
        			final int xIndex = xIndices[xx];
            		final float width = this.mWidthArray[xIndex];
                    final float xCur = this.mXCoordinateArray[xIndex] - width / 2.0f;
                    if (!SGUtilityNumber.contains(xCur, xCur + width, x)) {
                    	continue;
                    }
                    ret = new SGTwoDimensionalArrayIndex(xIndex, yIndex);
                    found = true;
                    break;
        		}
        		if (found) {
        			break;
        		}
        	}
        }
		return ret;
	}

	String getToolTipTextSpatiallyVaried(final int x, final int y) {
		String text = null;
        for (SGXYSimpleDoubleValueIndexBlock block : this.mZValueBlockList) {
        	SGIntegerSeries xSeries = block.getXSeries();
        	SGIntegerSeries ySeries = block.getYSeries();
        	final int[] xIndices = xSeries.getNumbers();
        	final int[] yIndices = ySeries.getNumbers();
        	for (int yy = 0; yy < yIndices.length; yy++) {
        		final int yIndex = yIndices[yy];
        		final float height = this.mHeightArray[yIndex];
                final float yCur = this.mYCoordinateArray[yIndex] - height / 2.0f;
                if (!SGUtilityNumber.contains(yCur, yCur + height, y)) {
                	continue;
                }
                boolean found = false;
        		for (int xx = 0; xx < xIndices.length; xx++) {
        			final int xIndex = xIndices[xx];
            		final float width = this.mWidthArray[xIndex];
                    final float xCur = this.mXCoordinateArray[xIndex] - width / 2.0f;
                    if (!SGUtilityNumber.contains(xCur, xCur + width, x)) {
                    	continue;
                    }
                    SGISXYZTypeData sxyzData = (SGISXYZTypeData) this.mGroupSet.getData();
                    text = sxyzData.getToolTipSpatiallyVaried(xIndex, yIndex);
                    found = true;
                    break;
        		}
        		if (found) {
        			break;
        		}
        	}
        }
        return text;
	}

	SXYZDataValue getDataValueAt(SGISXYZTypeData data, final int index) {
		return (SXYZDataValue) SGDataUtility.getDataValue(data, index);
	}

	SXYZDataValue getDataValueAt(SGISXYZTypeData data, final int xIndex,
			final int yIndex) {
		return (SXYZDataValue) SGDataUtility.getDataValue(data, xIndex, yIndex);
	}
	
    protected SGTuple2f getLocation(final int index, SGTuple2f[] pointsArray) {
        SGElementGroupSetInGraphSXYZ gs = (SGElementGroupSetInGraphSXYZ) this.mGroupSet;
        SGTuple2f location = gs.getLocation(index, pointsArray);
        return new SGTuple2f(location.x, location.y);
    }

    /**
     * Creates and returns an instance of a drawing element.
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new PseudocolorMapRectangleInGraph(this, index);
    }

    protected class PseudocolorMapRectangleInGraph extends PseudocolorMapRectangle {
    	
        public PseudocolorMapRectangleInGraph(SGElementGroupPseudocolorMapInGraph group, int index) {
			super(group, index);
		}

		boolean contains(final int x, final int y, SGTuple2f[] pointsArray) {
			SGElementGroupPseudocolorMapInGraph colorMap = (SGElementGroupPseudocolorMapInGraph) this.mGroup;
        	SGTuple2f location = colorMap.getLocation(this.mIndex, pointsArray);
        	return this.contains(x, y, location);
        }
    }

}
