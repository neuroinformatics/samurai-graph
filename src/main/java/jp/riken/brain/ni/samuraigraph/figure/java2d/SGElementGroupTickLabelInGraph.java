package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;

import org.w3c.dom.Element;

/**
 * @author okumura
 */
public abstract class SGElementGroupTickLabelInGraph extends SGElementGroupTickLabelForData
        implements SGIElementGroupSXYInGraph {

    /**
     * Maximum number of anchors.
     */
    public static final int MAX_NUMBER_OF_ANCHORS = 8;

    protected SGFigureElementGraph mGraph = null;

    protected SGElementGroupSetInGraph mGroupSet = null;

    public boolean setElementGroupSet(SGElementGroupSetInGraph gs) {
        this.mGroupSet = gs;
        return true;
    }

    /**
     * The array of coordinates of tick labels.
     */
    protected SGTuple2f[] mPointsArray = null;

    /**
     * Initializes the points array with given number.
     * 
     * @param num
     *           the number of points
     */
    protected void initPointsArray(final int num) {
        SGTuple2f[] pointArray = new SGTuple2f[num];
        for (int ii = 0; ii < num; ii++) {
            pointArray[ii] = new SGTuple2f();
        }
        this.mPointsArray = pointArray;
    }

    protected boolean mFocusedFlag = false;

    public void setFocused(final boolean b) {
        this.mFocusedFlag = b;
    }

    public boolean isFocused() {
        return this.mFocusedFlag;
    }

    protected SGElementGroupTickLabelInGraph(SGISXYTypeData data,
    		SGFigureElementGraph graph) {
        super(data);
        this.mGraph = graph;
    }

    /**
     * 
     */
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new TickLabelStringElement(this, index);
    }

    /**
     * 
     */
    public void dispose() {
        super.dispose();
        this.mPointsArray = null;
    }

    /**
     * 
     */
    public String getTagName() {
        return TAG_NAME_TICK_LABELS;
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
    protected static class TickLabelStringElement extends
            SGDrawingElementString2DExtended {
    	
        /**
         * The group of symbols.
         */
        protected SGElementGroupTickLabel mGroup = null;

    	int mIndex;
    	
    	double mValue;
    	
        protected TickLabelStringElement(SGElementGroupTickLabel tickLabelGroup,
        		final int index) {
            super();
            this.mGroup = tickLabelGroup;
            this.mIndex = index;
        }
        
        public int getIndex() {
        	return this.mIndex;
        }
    }

    /**
     * Update the location of tick labels.
     * @return true if succeeded
     */
    public boolean updateLocation() {
        return this.setLocation(this.mPointsArray);
    }
    
    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, Rectangle2D clipRect) {
	if (super.paintElement(g2d, clipRect) == false) {
	    return false;
	}
	
        SGDrawingElement[] array = this.mDrawingElementArray;
        if (this.isFocused() && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
            final int num = array.length;
            if (num <= MAX_NUMBER_OF_ANCHORS) {
                for (int ii = 0; ii < num; ii++) {
                    SGDrawingElementString2D label = (SGDrawingElementString2D) array[ii];
                    if (label.isVisible()) {
                        emphasisLabel(label, g2d);
                    }
                }
            } else {
                int div = num / MAX_NUMBER_OF_ANCHORS + 1;
                int cnt = 0;
                while (true) {
                    SGDrawingElementString2D label = (SGDrawingElementString2D) array[cnt];
                    if (label.isVisible()) {
                        emphasisLabel(label, g2d);
                    }
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
     * Draw anchors around a given label.
     * @param label
     *              a string element
     * @param g2d
     *              the graphics context
     */
    private void emphasisLabel(final SGDrawingElementString2D label,
            final Graphics2D g2d) {
        SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(label
                .getElementBounds(), g2d);
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
        // if a given point is inside of the graph rectangle, returns false
        Rectangle2D gRect = this.mGraph.getGraphRect();
        if (gRect.contains(x, y)) {
            return false;
        }
        return super.contains(x, y);
    }

    /**
     * Calculates and updates the location.
     * 
     * @param true if succeeded
     */
    protected abstract boolean calcLocation(final SGISXYTypeSingleData dataSXY,
    		final SGAxis axisX, final SGAxis axisY, final double[] valueArray);
    
}
