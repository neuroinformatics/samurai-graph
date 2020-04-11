package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.geom.Rectangle2D;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;
import jp.riken.brain.ni.samuraigraph.figure.SGIAxisConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGITickLabelConstants;

/**
 * Tick labels for SXY type graph.
 *
 */
public class SGElementGroupTickLabelInGraphSXY extends
		SGElementGroupTickLabelInGraph {

	/**
	 * Builds the tick labels.
	 * 
	 * @param dataSXY
	 *            SXY type data
	 * @param graph
	 *            the graph
	 */
	public SGElementGroupTickLabelInGraphSXY(SGISXYTypeSingleData dataSXY,
			SGFigureElementGraph graph) {
		super(dataSXY, graph);
        final int num = (dataSXY != null) ? dataSXY.getStringNumber() : 0;
        this.initDrawingElement(num);
        String[] strArray = null;
        if (dataSXY != null && dataSXY.isTickLabelAvailable()) {
            strArray = dataSXY.getStringArray(false);
        } else {
            // set empty strings temporarily
            strArray = new String[num];
            for (int ii = 0; ii < num; ii++) {
                strArray[ii] = "";
            }
        }
        SGDrawingElement[] array = this.mDrawingElementArray;
        for (int ii = 0; ii < num; ii++) {
            SGDrawingElementString el = (SGDrawingElementString) array[ii];
            el.setString(strArray[ii]);
        }
	}

	/**
	 * Builds the tick labels.
	 * 
	 * @param dataSXY
	 *            SXY type data
	 * @param graph
	 *            the graph
	 */
	public SGElementGroupTickLabelInGraphSXY(SGISXYTypeMultipleData dataSXY,
			SGFigureElementGraph graph) {
		super(dataSXY, graph);
        this.initDrawingElement(1);
        SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[0];
        el.setString("");
	}

    /**
     * Sets the location.
     * 
     * @param pointArray
     *            array of the location
     * @return true if succeeded
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {
        if (!super.setLocation(pointArray)) {
        	return false;
        }
        SGElementGroupSetForData groupSet = (SGElementGroupSetForData) this.mGroupSet;
        SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) this.mGraph.getData(groupSet);
        if (dataSXY != null) {
        	final SGAxis axis;
        	final Boolean horizontal = this.hasHorizontalAlignment();
        	if (horizontal != null) {
            	if (horizontal.booleanValue()) {
            		axis = groupSet.getXAxis();
            	} else {
            		axis = groupSet.getYAxis();
            	}
                for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
                    TickLabelStringElement el = (TickLabelStringElement) this.mDrawingElementArray[ii];
                    el.setVisible(axis.insideRange(el.mValue));
                }
        	}
        }
        return true;
    }


    /**
     * Calculates and updates the location.
     * 
     * @param true if succeeded
     */
    protected boolean calcLocation(final SGISXYTypeSingleData dataSXY,
    		final SGAxis axisX, final SGAxis axisY,  final double[] valueArray) {
    	
    	final int num = dataSXY.getStringNumber();
    	if (valueArray.length != num) {
    		return false;
    	}
    	
        final Boolean alignHorizontal = this.hasHorizontalAlignment();
        if (alignHorizontal == null) {
        	return false;
        }
        
        // initialize the drawing elements and locations
        if (this.mDrawingElementArray == null || this.mDrawingElementArray.length != num) {
        	this.initDrawingElement(num);
        }
        if (this.mPointsArray == null || this.mPointsArray.length != num) {
        	this.initPointsArray(num);
        }
    	for (int ii = 0; ii < num; ii++) {
            final TickLabelStringElement el = (TickLabelStringElement) this.mDrawingElementArray[ii];
    		el.mValue = valueArray[ii];
    	}

        // update text string
        String[] strArray = dataSXY.getStringArray(false);
    	for (int ii = 0; ii < num; ii++) {
            final TickLabelStringElement el = (TickLabelStringElement) this.mDrawingElementArray[ii];
            el.setString(strArray[ii]);
    	}
    	
    	final float gx = this.mGraph.getGraphRectX();
		final float gy = this.mGraph.getGraphRectY();
		final float gw = this.mGraph.getGraphRectWidth();
		final float gh = this.mGraph.getGraphRectHeight();
		final SGIFigureElementAxis aElement = this.mGraph.getAxisElement();
		final int axisLocationX = aElement.getLocationInPlane(axisX);
		final int axisLocationY = aElement.getLocationInPlane(axisY);
		final float angle = this.getAngle();
        final float radAngle = angle * SGIConstants.RADIAN_DEGREE_RATIO;
		final SGAxis axis = alignHorizontal ? axisX : axisY;

        // calculate baseline height
        float maxascent = 0.0f;
        float maxdescent = 0.0f;
        for (int ii = 0; ii < num; ii++) {
            final TickLabelStringElement el = (TickLabelStringElement) this.mDrawingElementArray[ii];
            final float a = el.getAscent();
            final float b = el.getDescent();
            if (a > maxascent) {
                maxascent = a;
            }
            if (b > maxdescent) {
                maxdescent = b;
            }
        }
        
        // updates the coordinates of each labels
		for (int ii = 0; ii < num; ii++) {
			final TickLabelStringElement el = (TickLabelStringElement) this.mDrawingElementArray[ii];
			final float location = this.mGraph.calcLocation(el.mValue, axis,
					alignHorizontal);
			Rectangle2D rect = el.getElementBounds();
			final float w = (float) rect.getWidth();
			final float h = (float) rect.getHeight();
			final float wHalf = w / 2;
			final float hHalf = h / 2;
			Rectangle2D sRect = el.getStringRect();
			final float sw = (float) sRect.getWidth();
			final float sh = (float) sRect.getHeight();
			float x = 0.0f;
			float y = 0.0f;
			final float space;
			
			if (alignHorizontal) {
				// aligned to the x-axis

				// x-coordinates
				if (angle == SGITickLabelConstants.ANGLE_HORIZONTAL) {
					x = location - wHalf;
				} else {
					if (axisLocationX == SGIFigureElementAxis.AXIS_HORIZONTAL_1) {
						x = location - sw * ((float) Math.cos(radAngle));
					} else if (axisLocationX == SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
						x = location - 0.5f * sh * ((float) Math.sin(radAngle));
					}
				}

				// y-coordinates
				float yDefault = 0.0f;
				if (axisLocationX == SGIFigureElementAxis.AXIS_HORIZONTAL_1) {
					if (angle == SGITickLabelConstants.ANGLE_HORIZONTAL) {
						yDefault = maxascent - el.getAscent();
					}
					space = this.calcSpace(SGIFigureElementAxis.AXIS_HORIZONTAL_1);
					yDefault += gy + gh + space;
				} else if (axisLocationX == SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
					if (angle == SGITickLabelConstants.ANGLE_HORIZONTAL) {
						yDefault = -maxdescent + el.getDescent();
					}
					space = this.calcSpace(SGIFigureElementAxis.AXIS_HORIZONTAL_2);
					yDefault += gy - space - h;
				}

				el.setLocation(x, yDefault);
				final float yy = (float) el.getElementBounds().getY();
				y = yDefault + (yDefault - yy);

			} else {
				// aligned to the y-axis

				// y-coordinates
				if (angle == SGITickLabelConstants.ANGLE_HORIZONTAL) {
					y = location - hHalf;
				} else {
					if (axisLocationY == SGIFigureElementAxis.AXIS_VERTICAL_1) {
						y = location - 0.5f * sh + sw
								* ((float) Math.sin(radAngle));
					} else if (axisLocationY == SGIFigureElementAxis.AXIS_VERTICAL_2) {
						y = location - 0.5f * sh * ((float) Math.cos(radAngle));
					}
				}

				// x-coordinates
				if (angle == SGITickLabelConstants.ANGLE_HORIZONTAL) {
					if (axisLocationY == SGIFigureElementAxis.AXIS_VERTICAL_1) {
						space = this.calcSpace(SGIFigureElementAxis.AXIS_VERTICAL_1);
						x = gx - space - w;
					} else if (axisLocationY == SGIFigureElementAxis.AXIS_VERTICAL_2) {
						space = this.calcSpace(SGIFigureElementAxis.AXIS_VERTICAL_2);
						x = gx + gw + space;
					}
				} else {
					if (axisLocationY == SGIFigureElementAxis.AXIS_VERTICAL_1) {
						space = this.calcSpace(SGIFigureElementAxis.AXIS_VERTICAL_1);
						x = gx - space - (float) rect.getWidth();
						if (angle > 90) {
							x = x - sw * ((float) Math.cos(radAngle));
						} else if (angle < 0 && angle >= -90) {
							x = x - sh * ((float) Math.sin(radAngle));
						} else if (angle < -90) {
							x = x - sh * ((float) Math.sin(radAngle)) + sw
									* ((float) Math.sin(radAngle) + 1.0f);
						}
					} else if (axisLocationY == SGIFigureElementAxis.AXIS_VERTICAL_2) {
						space = this.calcSpace(SGIFigureElementAxis.AXIS_VERTICAL_2);
						x = gx + gw + space;
						if (angle > 90) {
							x = x - sw * ((float) Math.cos(radAngle));
						} else if (angle < 0 && angle >= -90) {
							x = x - sh * ((float) Math.sin(radAngle));
						} else if (angle < -90) {
							x = x - sh * ((float) Math.sin(radAngle)) + sw
									* ((float) Math.sin(radAngle) + 1.0f);
						}
					}
				}
			}

			// set to variable
			this.mPointsArray[ii].setValues(x, y);
		}

        return true;
    }
    
    private float calcSpace(final int location) {
		final SGIFigureElementAxis aElement = this.mGraph.getAxisElement();
		float space = aElement.getSpaceAxisLineAndNumber(location) * this.mMagnification;
		space = (float) SGUtilityText.convert(space, SGIAxisConstants.SPACE_UNIT, 
				SGIConstants.pt);
		return space;
    }

    /**
     * Overrode to set properties to all string elements.
     */
    @Override
    public boolean initDrawingElement(final int num) {
    	if (super.initDrawingElement(num) == false) {
    		return false;
    	}
    	for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
    		SGDrawingElementString el = (SGDrawingElementString) this.mDrawingElementArray[ii];
    		el.setMagnification(this.mMagnification);
    		el.setColor(this.mColor);
    		el.setFont(this.mFontName, this.mFontStyle, this.mFontSize);
    		el.setAngle(this.mAngle);
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
        // if the data object do not have error bars, return false
        SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) this.mGraph.getData(this.mGroupSet);
        if (dataSXY == null) {
            return false;
        }
        if (dataSXY.isTickLabelAvailable() == false) {
            return false;
        }
        return super.contains(x, y);
    }

	@Override
	public boolean contains(int x, int y, SGTuple2f[] pointsArray,
			final double[] xValues, final double[] yValues) {
		// only calls another method
		return this.contains(x, y);
	}

    @Override
	public String getToolTipTextSpatiallyVaried(List<Integer> indices) {
		// always returns null
    	return null;
	}

	@Override
	public List<Integer> getIndicesAt(final int x, final int y,
			final SGTuple2f[] pointsArray, final double[] xValues,
			final double[] yValues) {
		// always returns null
		return null;
	}

}
