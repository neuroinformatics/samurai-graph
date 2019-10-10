package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;

import org.w3c.dom.Element;

/**
 * A group of lines in the graph object.
 * 
 * @author kuromaru
 * 
 */
public class SGElementGroupLineInGraph extends SGElementGroupLineForData implements
		SGIElementGroupSXYInGraph, SGIStrokeEditDialogObserver,
        SGISXYDataConstants {

    /**
     * Maximum number of anchors.
     */
    public static final int MAX_NUMBER_OF_ANCHORS = 8;

    private SGFigureElementGraph mGraph = null;

    // 
    private SGElementGroupSetInGraph mGroupSet = null;

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
    public SGElementGroupLineInGraph(SGISXYTypeData data, SGFigureElementGraph graph) {
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
        el.setAttribute(KEY_LINE_CONNECT_ALL_EFFECTIVE_POINTS, Boolean.toString(this.mConnectingAllFlag));
        return true;
    }

    /**
     * 
     */
    public boolean setLocation(final SGTuple2f[] pointArray) {
        final int mode = this.mGraph.getMode();
        if (mode == MODE_EXPORT_AS_IMAGE) {
            this.mMode = MODE_ALL;
        } else if (mode == MODE_DISPLAY) {
            this.mMode = MODE_OMIT;
        }
        return this.setLocationSub(pointArray);
    }

    /**
     * 
     */
    public boolean paintElement(final Graphics2D g2d, final Rectangle2D rect) {
        super.paintElement(g2d, rect);

        SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) this.mGroupSet;

        if (this.isFocused()
                && this.mGraph.isSymbolsVisibleAroundFocusedObjects()) {
            if (!groupSet.isSymbolVisible() && !groupSet.isBarVisible()) {
                List<GeneralPath> pathList = this.mConnectedPathList;
                for (int ii = 0; ii < pathList.size(); ii++) {
                    GeneralPath gp = (GeneralPath) pathList.get(ii);

                    int num = 0;
                    ArrayList list = new ArrayList();

                    PathIterator itr = gp
                            .getPathIterator(new AffineTransform());
                    final float[] array = new float[6];
                    itr.currentSegment(array);
                    Point2D prev = new Point2D.Float(array[0], array[1]);
                    itr.next();

                    while (!itr.isDone()) {
                        itr.currentSegment(array);
                        Point2D pos = new Point2D.Float(array[0], array[1]);
                        pos.setLocation((prev.getX() + pos.getX()) / 2.0, (prev
                                .getY() + pos.getY()) / 2.0);
                        prev.setLocation(array[0], array[1]);
                        list.add(pos);
                        itr.next();
                        num++;
                    }

                    if (num <= MAX_NUMBER_OF_ANCHORS) {
                        for (int jj = 0; jj < num; jj++) {
                            Point2D pos = (Point2D) list.get(jj);
                            SGUtilityForFigureElementJava2D
                                    .drawAnchorAsFocusedObject(pos, g2d);
                        }
                    } else {
                        int div = num / MAX_NUMBER_OF_ANCHORS;
                        int cnt = 0;
                        while (true) {
                            Point2D pos = (Point2D) list.get(cnt);
                            SGUtilityForFigureElementJava2D
                                    .drawAnchorAsFocusedObject(pos, g2d);
                            cnt += div;
                            if (cnt >= num) {
                                break;
                            }
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
            	LineInGraph el = (LineInGraph) this.mDrawingElementArray[ii];
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

    /**
     * Returns the location of the end point at a given index.
     * 
     * @param index
     *           the index of line
     * @return the location
     */
    public SGTuple2f getEnd(final int index) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
            final int n = this.getEndIndex(index);
            SGTuple2f tuple = gs.getLineLocation(n);
            return new SGTuple2f(tuple);
        } else {
            throw new Error("Not supported.");
        }
    }

    /**
     * Returns the location of the start point at a given index.
     * 
     * @param index
     *           the index of line
     * @return the location
     */
    public SGTuple2f getStart(int index) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
            final int n = this.getStartIndex(index);
            SGTuple2f tuple = gs.getLineLocation(n);
            return new SGTuple2f(tuple);
        } else {
            throw new Error("Not supported.");
        }
    }

    protected SGTuple2f getEnd(int index, SGTuple2f[] pointsArray) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
            final int n = this.getEndIndex(index);
            SGTuple2f tuple = gs.getLocation(n, pointsArray);
            return new SGTuple2f(tuple);
        } else {
            throw new Error("Not supported.");
        }
    }

    protected SGTuple2f getStart(int index, SGTuple2f[] pointsArray) {
        if (this.mGroupSet instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mGroupSet;
            final int n = this.getStartIndex(index);
            SGTuple2f tuple = gs.getLocation(n, pointsArray);
            return new SGTuple2f(tuple);
        } else {
            throw new Error("Not supported.");
        }
    }

    public boolean readProperty(final Element el) {
        if (super.readProperty(el) == false) {
            return false;
        }

        return this.mGraph.readProperty(this, el);
    }

    @Override
    protected SGDrawingElement createDrawingElementInstance(final int index) {
        return new LineInGraph(this, index);
    }

    protected class LineInGraph extends LineInGroup {

		public LineInGraph(SGElementGroupLine group, int index) {
			super(group, index);
		}
    	
        boolean contains(final int x, final int y, SGTuple2f[] pointsArray,
        		final double[] xValues, final double[] yValues) {
        	SGElementGroupLineInGraph lineGroup = (SGElementGroupLineInGraph) this.mGroup;
        	SGTuple2f start = lineGroup.getStart(this.mIndex, pointsArray);
        	SGTuple2f end = lineGroup.getEnd(this.mIndex, pointsArray);
        	return this.contains(x, y, start, end);
        }
    }

    @Override
	public String getToolTipTextSpatiallyVaried(List<Integer> indices) {
    	SGISXYTypeSingleData sxyData = (SGISXYTypeSingleData) this.mGroupSet.getData();
    	return sxyData.getToolTipSpatiallyVaried(indices.get(0), indices.get(1));
	}

	@Override
	public List<Integer> getIndicesAt(final int x, final int y,
			final SGTuple2f[] pointsArray, final double[] xValues,
			final double[] yValues) {
        if (this.mDrawingElementArray != null) {
            for (int ii = 0; ii < this.mDrawingElementArray.length; ii++) {
            	LineInGraph el = (LineInGraph) this.mDrawingElementArray[ii];
                if (el.isVisible() == false) {
                    continue;
                }
                if (el.contains(x, y, pointsArray, xValues, yValues)) {
                	final int index = el.getIndex();
                	int[] endIndex = this.mEndPointsIndexArray[index];
                	SGISXYTypeSingleData data = (SGISXYTypeSingleData) this.mGroupSet.getData();
                	final int arrayIndex0, arrayIndex1;
                	if (data.isStrideAvailable()) {
                    	SGIntegerSeriesSet stride = data.isIndexAvailable() ? data.getIndexStride() : data.getStride();
                    	int[] indices = stride.getNumbers();
                    	arrayIndex0 = indices[endIndex[0]];
                    	arrayIndex1 = indices[endIndex[1]];
                	} else {
                		arrayIndex0 = endIndex[0];
                		arrayIndex1 = endIndex[1];
                	}
                	List<Integer> ret = new ArrayList<Integer>();
                	ret.add(arrayIndex0);
                	ret.add(arrayIndex1);
                	return ret;
                }
            }
        }
		return null;
	}

}
