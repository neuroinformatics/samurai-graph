package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

import org.w3c.dom.Element;

/**
 * @author okumura
 */
public class SGElementGroupSymbolInGraph extends SGElementGroupSymbolForData implements
		SGIElementGroupSXYInGraph, SGISXYDataConstants,
        SGIFigureDrawingElementConstants {

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
    public SGElementGroupSymbolInGraph(SGISXYTypeData data, SGFigureElementGraph graph) {
        super(data);
        this.mGraph = graph;
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
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D rect) {
        super.paintElement(g2d, rect);

        SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) this.mGroupSet;
        SGDrawingElement[] array = this.mDrawingElementArray;

        if (this.isFocused() && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
            if (!groupSet.isBarVisible()) {
                final int num = array.length;
                if (num <= MAX_NUMBER_OF_ANCHORS) {
                    for (int ii = 0; ii < num; ii++) {
                        SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) array[ii];
                        emphasisSymbol(symbol, g2d);
                    }
                } else {
                    int div = num / MAX_NUMBER_OF_ANCHORS;
                    int cnt = 0;
                    while (true) {
                        SGDrawingElementSymbol2D symbol = (SGDrawingElementSymbol2D) array[cnt];
                        emphasisSymbol(symbol, g2d);
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
     * 
     * @param g2d
     * @param symbol
     * @return
     */
    private boolean emphasisSymbol(final SGDrawingElementSymbol2D symbol,
            final Graphics2D g2d) {
        SGUtilityForFigureElementJava2D.drawAnchorsOnRectangle(symbol
                .getElementBounds(), g2d);
        return true;
    }

    /**
     * Returns whether this group set contains the given point.
     * 
     * @param x
     *          x coordinate
     * @param y
     *          y coordinate
     * @return true if this element group contains the given point
     */
    @Override
    public boolean contains(final int x, final int y) {
		// if a given point is out of the graph rectangle, returns false;
		Rectangle2D gRect = this.mGraph.getGraphRect();
		if (!gRect.contains(x, y)) {
		    return false;
		}
		return super.contains(x, y);
    }

	public boolean contains(final int x, final int y, final SGTuple2f[] pointsArray,
			final double[] xValues, final double[] yValues) {
		Rectangle2D gRect = this.mGraph.getGraphRect();
		if (!gRect.contains(x, y)) {
		    return false;
		}
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	SymbolInGraph el = (SymbolInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray)) {
                    return true;
                }
            }
        }
        return false;
	}

    /**
     * Returns the location at a given index.
     * 
     * @param index
     *           the index
     * @return the location
     */
	@Override
    public SGTuple2f getLocation(final int index) {
        SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
        SGTuple2f location = gs.getSymbolLocation(index);
        return new SGTuple2f(location.x, location.y);
    }

    protected SGTuple2f getLocation(final int index, SGTuple2f[] pointsArray) {
        SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
        SGTuple2f location = gs.getLocation(index, pointsArray);
        return new SGTuple2f(location.x, location.y);
    }
    
    @Override
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new SymbolInGraph(this, index);
    }

    protected class SymbolInGraph extends SymbolInGroup {
    	
        public SymbolInGraph(SGElementGroupSymbol group, int index) {
			super(group, index);
		}

		boolean contains(final int x, final int y, SGTuple2f[] pointsArray) {
        	SGElementGroupSymbolInGraph symbolGroup = (SGElementGroupSymbolInGraph) this.mGroup;
        	SGTuple2f location = symbolGroup.getLocation(this.mIndex, pointsArray);
        	return this.contains(x, y, location);
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
            	SymbolInGraph el = (SymbolInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray)) {
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
