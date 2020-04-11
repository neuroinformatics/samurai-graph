package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGFillPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGXYSimpleDoubleValueIndexBlock;
import jp.riken.brain.ni.samuraigraph.figure.SGIColorMapConstants;

import org.w3c.dom.Element;

/**
 * The base class for the pseudocolor map.
 * 
 */
public abstract class SGElementGroupPseudocolorMap extends SGElementGroupRectangle implements 
        SGIColorMapConstants, SGIElementGroupGridSXY {

    /**
     * A value for rectangle width with related axis.
     */
    protected double mWidthValue;
    
    /**
     * A value for rectangle height with related axis.
     */
    protected double mHeightValue;
    
    /**
     * The type of color map.
     */
    protected SGColorMap mColorBarModel = null;
    
    /**
     * The array of x-coordinates.
     */
    protected float[] mXCoordinateArray = null;
    
    /**
     * The array of y-coordinates.
     */
    protected float[] mYCoordinateArray = null;

    /**
     * The array of rectangle width.
     */
    protected float[] mWidthArray = null;
    
    /**
     * The array of rectangle height.
     */
    protected float[] mHeightArray = null;

    /**
     * The array of z-values.
     */
    protected double[] mZValuesArray = null;
    
    /**
     * List of blocks of z-values.
     */
    protected List<SGXYSimpleDoubleValueIndexBlock> mZValueBlockList = null;
    
    /**
     * The bounds of this color map.
     */
    protected Rectangle2D mBounds = new Rectangle2D.Float();

    /**
     * A flag whether the color map is under the grid mode.
     */
    protected boolean mGridMode = false;

    // the margin for single data value
    protected static final float SINGLE_VALUE_MARGIN = 10.0f;
    
    // width for single data value
    protected static final float SINGLE_VALUE_WIDTH = 2 * SINGLE_VALUE_MARGIN;

    /**
     * Build this object.
     *
     */
    public SGElementGroupPseudocolorMap() {
        super();
    }

    /**
     * Disposes this object.
     * 
     */
    public void dispose() {
    	super.dispose();
    	this.mColorBarModel = null;
        this.mXCoordinateArray = null;
        this.mYCoordinateArray = null;
        this.mZValuesArray = null;
        this.mBounds = null;
        if (this.mZValueBlockList != null) {
            this.mZValueBlockList.clear();
        }
        this.mZValueBlockList = null;
    }
    
    /**
     * Sets the color bar model.
     * 
     * @param model
     *           the color bar model
     */
    public void setColorBarModel(SGColorMap model) {
        if (model == null) {
            throw new IllegalArgumentException("model == null");
        }
        this.mColorBarModel = model;
    }

    /**
     * Returns the color bar model.
     * 
     * @return the color bar model
     */
    public SGColorMap getColorBarModel() {
        return this.mColorBarModel;
    }
    
    /**
     * Returns the value for rectangle width.
     * 
     * @return the value for rectangle width
     */
    public double getWidthValue() {
        return this.mWidthValue;
    }

    /**
     * Sets the value for rectangle width.
     * 
     * @param value
     *           rectangle width to set
     * @return true if succeeded
     */
    public boolean setWidthValue(final double value) {
    	if (value < 0.0) {
    		return false;
    	}
        this.mWidthValue = value;
        return true;
    }

    /**
     * Returns the value for rectangle height.
     * 
     * @return the value for rectangle height
     */
    public double getHeightValue() {
        return this.mHeightValue;
    }

    /**
     * Sets the value for rectangle height.
     * 
     * @param value
     *           rectangle height to set
     * @return true if succeeded
     */
    public boolean setHeightValue(final double value) {
    	if (value < 0.0) {
    		return false;
    	}
        this.mHeightValue = value;
        return true;
    }
    
    /**
     * Sets the location of drawing elements.
     * 
     * @param pointArray
     *           an array of coordinates
     * @return true if succeeded
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {

        if (this.mDrawingElementArray == null) {
            return true;
        }

        if (pointArray.length != this.mDrawingElementArray.length) {
        	this.initDrawingElement(pointArray);
        }

        SGDrawingElement[] array = this.mDrawingElementArray;
        for (int ii = 0; ii < array.length; ii++) {
            PseudocolorMapRectangle rect = (PseudocolorMapRectangle) array[ii];
            final boolean eff = !(pointArray[ii].isInfinite() || pointArray[ii]
                    .isNaN());
            rect.setVisible(eff);
            if (eff) {
                rect.setLocation(pointArray[ii]);
            }
        }

        return true;
    }

    /**
     * Sets the location of drawing elements.
     * 
     * @param xCoordinateArray
     *           an array of x-coordinates
     * @param yCoordinateArray
     *           an array of y-coordinates
     * @return true if succeeded
     */
    public boolean setLocation(float[] xCoordinateArray, float[] yCoordinateArray) {
        if (xCoordinateArray == null || yCoordinateArray == null) {
            throw new IllegalArgumentException(
                    "xCoordinateArray == null || yCoordinateArray == null");
        }
        
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
        this.mBounds = new Rectangle2D.Float(xMin, yMin, w, h);
        
        // set to the attributes
        this.mXCoordinateArray = xCoordinateArray;
        this.mYCoordinateArray = yCoordinateArray;

        return true;
    }

    /**
     * Returns the tag name.
     * 
     * @return the tag name
     */
    public String getTagName() {
        return TAG_NAME_COLOR_MAP;
    }

    /**
     * Paint the rectangles.
     * 
     * @param g2d
     *          graphic context
     * @param clipRect
     *          a rectangle for clipping
     * @return true if succeeded
     */
    public boolean paintElement(Graphics2D g2d, Rectangle2D clipRect) {
    	
    	if (this.mGridMode) {
            Area clipArea = null;
            if (clipRect != null) {
                clipArea = new Area(clipRect);
            }
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
            		for (int xx = 0; xx < xIndices.length; xx++) {
                        final int index = xIndices.length * yy + xx;
                        final double value = values[index];
                        if (Double.isNaN(value) || Double.isInfinite(value)) {
                            continue;
                        }
            			final int xIndex = xIndices[xx];
                		final float width = this.mWidthArray[xIndex];
                        final float xCur = this.mXCoordinateArray[xIndex] - width / 2.0f;
                        final Rectangle2D dRect = new Rectangle2D.Double(xCur, yCur, width, height);

                        // paint
                        Color cl = this.mColorBarModel.evaluate(value);
                        g2d.setPaint(cl);

                        if (clipArea != null) {
                            Area inner = new Area(dRect);
                            inner.intersect(clipArea);
                            g2d.fill(inner);
                        } else {
                            g2d.fill(dRect);
                        }
            		}
            	}
            }
            
    	} else {
            SGDrawingElement[] array = this.mDrawingElementArray;
            if (array != null) {
                for (int ii = 0; ii < array.length; ii++) {
                    PseudocolorMapRectangle el = (PseudocolorMapRectangle) array[ii];
                    el.paint(g2d, clipRect);
                }
            }
    	}
    	
        return true;
    }
    
    private void updateSize() {
        final int xLen = this.mXCoordinateArray.length;
        final int yLen = this.mYCoordinateArray.length;
        this.mWidthArray = new float[xLen];
        this.mHeightArray = new float[yLen];
        
        for (SGXYSimpleDoubleValueIndexBlock block : this.mZValueBlockList) {
        	// x-coordinates
        	this.updateSizeSub(block.getXSeries(), this.mXCoordinateArray, this.mWidthArray);

        	// y-coordinates
        	this.updateSizeSub(block.getYSeries(), this.mYCoordinateArray, this.mHeightArray);
        }
    }
    
    private void updateSizeSub(SGIntegerSeries series, final float[] coordinateArray, 
    		final float[] sizeArray) {
    	final int[] indices = series.getNumbers();
    	final int step = series.getStep().getNumber();
		for (int ii = 0; ii < indices.length; ii++) {
			final int cur = indices[ii];
    		final float size;
            if (indices.length == 1) {
            	size = SINGLE_VALUE_WIDTH * this.getMagnification();
            } else {
            	final int maxIndex = coordinateArray.length - 1;
				if (cur == 0) {
					size = Math.abs(coordinateArray[step] - coordinateArray[0]);
				} else if (cur == maxIndex) {
					size = Math.abs(coordinateArray[maxIndex] - coordinateArray[maxIndex - step]);
				} else {
					if (ii == 0) {
						final int next = indices[ii + 1];
						size = Math.abs(coordinateArray[next] - coordinateArray[cur]);
					} else if (ii == indices.length - 1) {
						final int prev = indices[ii - 1];
						size = Math.abs(coordinateArray[cur] - coordinateArray[prev]);
					} else {
						final int prev = indices[ii - 1];
						final int next = indices[ii + 1];
						final float diffPrev = Math.abs(coordinateArray[cur]
								- coordinateArray[prev]);
						final float diffNext = Math.abs(coordinateArray[next]
								- coordinateArray[cur]);
						size = Math.max(diffPrev, diffNext);
					}
				}
            }
            sizeArray[cur] = size;
		}
    }

    /**
     * Initializes the drawing elements.
     * 
     * @param array
     *           the location of drawing elements
     */
    protected boolean initDrawingElement(SGTuple2f[] array) {
//    	if (this.mGridMode) {
//            // do nothing
//    	} else {
//    		if (super.initDrawingElement(array) == false) {
//    			return false;
//    		}
//    	}
		if (super.initDrawingElement(array) == false) {
			return false;
		}
        return true;
    }

    /**
     * Initializes the drawing elements.
     * 
     * @param array
     *           the location of drawing elements
     */
    public boolean initDrawingElement(float[] xArray, float[] yArray) {
        // do nothing
        return true;
    }

    /**
     * Creates and returns an instance of a drawing element.
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new PseudocolorMapRectangle(this, index);
    }

    /**
     * Read properties from the Element object.
     * 
     * @param el
     *          the Element object
     */
    public boolean readProperty(Element el) {
    	
    	if (this.mGridMode) {
    		// do nothing
    	} else {
            String str = null;
            Number num = null;

            // width of the rectangle
            str = el.getAttribute(KEY_RECTANGLE_WIDTH_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                this.setWidthValue(num.doubleValue());
            }
            
            // height of the rectangle
            str = el.getAttribute(KEY_RECTANGLE_HEIGHT_VALUE);
            if (str.length() != 0) {
                num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return false;
                }
                this.setHeightValue(num.doubleValue());
            }
    	}
    	
        return true;
    }

    /**
     * Write properties to the Element object.
     * 
     * @param el
     *          the Element object
     */
    public boolean writeProperty(Element el) {
    	if (this.mGridMode) {
    		// do nothing
    	} else {
            el.setAttribute(KEY_RECTANGLE_WIDTH_VALUE, Double.toString(this.mWidthValue));
            el.setAttribute(KEY_RECTANGLE_HEIGHT_VALUE, Double.toString(this.mHeightValue));
    	}
        return true;
    }

    
    /**
     * A rectangle in the pseudocolor map.
     *
     */
    protected static class PseudocolorMapRectangle extends RectInGroup implements
        SGIDrawingElementJava2D {

        /**
         * Z-value.
         */
        private double mZValue;
        
        /**
         * Builds a rectangle in a color map.
         * 
         * @param group
         *           a group of rectangles
         */
        public PseudocolorMapRectangle(final SGElementGroupPseudocolorMap group, final int index) {
            super(group, index);
        }

        /**
         * Sets the z-value.
         * 
         * @param value
         *           a value to set
         */
        public void setZValue(final double value) {
            this.mZValue = value;
        }
        
        /**
         * Returns the z-value.
         * 
         * @return the z-value
         */
        public double getZValue() {
            return this.mZValue;
        }
        
        /**
         * Paint this object.
         * 
         * @param g2d
         *            graphics context
         */
        public void paint(final Graphics2D g2d) {
            if (this.isVisible() == false) {
                return;
            }
            final Rectangle2D dRect = this.getElementBounds();
            Color cl = this.getColor();
            if (cl != null) {
                g2d.setPaint(cl);
                g2d.fill(dRect);
            }
        }

        public boolean contains(int x, int y) {
            return this.getElementBounds().contains(x, y);
        }

        public Rectangle2D getElementBounds() {
            final float x = this.getX();
            final float y = this.getY();
            final float w = this.getWidth();
            final float h = this.getHeight();

            final float ww = Math.abs(w);
            final float hh = Math.abs(h);

            final float xx = x - 0.50f * ww;
            final float yy = y - 0.50f * hh;

            Rectangle2D rect = new Rectangle2D.Float(xx, yy, ww, hh);
            return rect;
        }

        public void paint(Graphics2D g2d, Rectangle2D clipRect) {
            if (clipRect == null) {
                this.paint(g2d);
            } else {
                final Rectangle2D dRect = this.getElementBounds();
                Area clipArea = new Area(clipRect);
                Area inner = new Area(dRect);
                inner.intersect(clipArea);

                // paint
                Color cl = this.getColor();
                if (cl != null) {
                    g2d.setPaint(cl);
                    g2d.fill(inner);
                }
            }
        }

        public Color getColor() {
            SGColorMap model = ((SGElementGroupPseudocolorMap) this.mGroup).getColorBarModel();
            if (Double.isNaN(this.mZValue)) {
            	return null;
            } else {
                return model.evaluate(this.mZValue);
            }
        }
    }
    
    /**
     * Properties of the pseudocolor map.
     * 
     */
    public static class PseudocolorMapProperties extends ElementGroupProperties {
        double widthValue;
        double heightValue;
        PseudocolorMapProperties() {
            super();
        }
        public Object copy() {
            Object obj = super.copy();
            PseudocolorMapProperties p = (PseudocolorMapProperties) obj;
            p.widthValue = this.widthValue;
            p.heightValue = this.heightValue;
            return p;
        }
        public boolean equals(final Object obj) {
            if ((obj instanceof PseudocolorMapProperties) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            PseudocolorMapProperties p = (PseudocolorMapProperties) obj;
            if (this.widthValue != p.widthValue) {
                return false;
            }
            if (this.heightValue != p.heightValue) {
                return false;
            }
            return true;
        }
    }


    private boolean mFocusedFlag = false;

    public void setFocused(final boolean b) {
        this.mFocusedFlag = b;
    }

    public boolean isFocused() {
        return this.mFocusedFlag;
    }

    private SGElementGroupSetInGraph mGroupSet = null;

    public boolean setElementGroupSet(SGElementGroupSetInGraph gs) {
        this.mGroupSet = gs;
        return true;
    }

    /**
     * Sets the color to each rectangle.
     * 
     * @param values
     *           values for colors
     */
    public void setColors(final double[] values) {
        if (values == null) {
            throw new IllegalArgumentException("values == null");
        }
        if (values.length != this.mDrawingElementArray.length) {
            throw new IllegalArgumentException("Illegal array length: " + values.length 
                    + " != " + this.mDrawingElementArray.length);
        }
        for (int ii = 0; ii < values.length; ii++) {
            PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[ii];
            rect.setZValue(values[ii]);
            if (Double.isNaN(values[ii]) || Double.isInfinite(values[ii])) {
                rect.setVisible(false);
            } else {
                rect.setVisible(true);
                Color cl = this.mColorBarModel.evaluate(values[ii]);
                rect.setInnerPaint(new SGFillPaint(cl));
            }
        }
    }
    
    /**
     * Sets the value range.
     * 
     * @param min
     *           the minimum value
     * @param max
     *           the maximum value
     */
    public void setValueRange(final double min, final double max) {
        this.mColorBarModel.setValueRange(min, max);
    }
    
    public SGProperties getProperties() {
        PseudocolorMapProperties p = new PseudocolorMapProperties();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }
    
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof PseudocolorMapProperties) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        PseudocolorMapProperties cp = (PseudocolorMapProperties) p;
        cp.widthValue = this.mWidthValue;
        cp.heightValue = this.mHeightValue;
        return true;
    }
    
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof PseudocolorMapProperties) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        PseudocolorMapProperties cp = (PseudocolorMapProperties) p;
        this.mWidthValue = cp.widthValue;
        this.mHeightValue = cp.heightValue;
        return true;
    }
    
    /**
     * Sets the z-values to the rectangles.
     * 
     * @param zValues
     *          an array of z-values
     * @return true if succeeded
     */
    protected boolean setZValues(final double[] zValues) {
        if (zValues == null) {
            throw new IllegalArgumentException("zValues == null");
        }
        if (this.mGridMode) {
            this.mZValuesArray = zValues;
        } else {
            if (zValues.length != this.mDrawingElementArray.length) {
                throw new IllegalArgumentException(
                        "The number of z values is different from that of the rectangles: "
                                + this.mDrawingElementArray.length + " != "
                                + zValues.length);
            }
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                PseudocolorMapRectangle rect = (PseudocolorMapRectangle) this.mDrawingElementArray[ii];
                rect.setZValue(zValues[ii]);
            }
        }
        return true;
    }

    /**
     * Sets the z-values as a list of blocks.
     * 
     * @param blockList
     *           the list of arrays of integer series
     * @return true if succeeded
     */
    protected boolean setZValues(final List<SGXYSimpleDoubleValueIndexBlock> blockList) {
    	if (blockList == null) {
            throw new IllegalArgumentException("blockList == null");
    	}
    	if (this.isGridMode()) {
    		// sets to the attribute
        	this.mZValueBlockList = new ArrayList<SGXYSimpleDoubleValueIndexBlock>(blockList);
        	
        	// updates the size of rectangles
        	this.updateSize();
    	}
    	return true;
    }
    
    /**
     * Sets the edge line width in a given unit.
     * 
     * @param lw
     *           the edge line width to set
     * @param unit
     *           the unit for given line width
     * @return true if succeeded
     */
    public boolean setEdgeLineWidth(float lw, String unit) {
        // do nothing
        return true;
    }

    /**
     * Returns whether this color map is under the grid mode.
     * 
     * @return true if this color map is under the grid mode
     */
    public boolean isGridMode() {
    	return this.mGridMode;
    }

    /**
     * Sets the grid mode.
     * 
     * @param b
     *          true to set the grid mode
     * @return true if succeeded
     */
    public boolean setGridMode(final boolean b) {
    	this.mGridMode = b;
    	return true;
    }
    
    /**
     * Returns the x-coordinate of the rectangle at given index.
     * 
     * @param index
     *           the array index
     * @return the x-coordinate
     */
    public float getX(final int index) {
        return this.mXCoordinateArray[index];
    }

    /**
     * Returns the y-coordinate of the rectangle at given index.
     * 
     * @param index
     *           the array index
     * @return the y-coordinate
     */
    public float getY(int index) {
        return this.mYCoordinateArray[index];
    }
    
    float[] getWidthArray() {
    	return this.mWidthArray;
    }
    
    float[] getHeightArray() {
    	return this.mHeightArray;
    }
    
    List<SGXYSimpleDoubleValueIndexBlock> getZValueBlockList() {
    	return this.mZValueBlockList;
    }
}
