package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap;
import jp.riken.brain.ni.samuraigraph.base.SGColorMap.ColorMapProperties;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGInteger;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.base.SGXYSimpleIndexBlock;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYMultipleDimensionData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGPickUpDimensionInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementLine;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroupSet;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyleColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXY.SXYElementGroupSetPropertiesInFigureElement;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import ucar.nc2.Dimension;

/**
 * A element group set for multiple data.
 */
public class SGElementGroupSetInGraphSXYMultiple extends
        SGElementGroupSetInGraph implements SGIElementGroupSetMultipleSXY,
        SGIElementGroupSetInGraphSXY, SGISXYDataDialogObserver,
        SGISXYAxisShiftEnabled, SGISXYDataConstants {

    /**
     * Shift value to the x-axis direction.
     */
    protected double mShiftX;

    /**
     * Shift value to the y-axis direction.
     */
    protected double mShiftY;

    /**
     * The list of element group sets.
     */
    protected ArrayList<SGIElementGroupSetForData> mElementGroupSetList = new ArrayList<SGIElementGroupSetForData>();

    /**
     * Color map manager for lines.
     */
    private SGColorMapManager mLineColorMapManager = new SGLineStyleColorMapManager();
    
    /**
     * The color map for lines.
     */
    private String mLineColorMapName = SGLineStyleColorMapManager.COLOR_MAP_NAME_HUE_GRADATION;
    
    /**
     * Adds a group set.
     *
     * @param gs
     *           the group set to be add
     */
    public void addChildGroupSet(SGIElementGroupSetForData gs) {
    	// adds to the list
        this.mElementGroupSetList.add(gs);
    }

    /**
     * Removes a group set.
     *
     * @param gs
     *           the group set to be removed
     */
    public void removeChildGroupSet(SGIElementGroupSetForData gs) {
        this.mElementGroupSetList.remove(gs);
    }

    /**
     * The constructor of element group set for multiple data.
     *
     */
    protected SGElementGroupSetInGraphSXYMultiple(SGData data, SGFigureElementGraph graph) {
        super(data, graph);
    }

    /**
     * Disposes this object.
     *
     */
    public void dispose() {
        super.dispose();
        ArrayList list = this.mElementGroupSetList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSet gs = (SGElementGroupSet) list.get(ii);
            gs.dispose();
        }
        this.mElementGroupSetList.clear();
        this.mElementGroupSetList = null;
    }

    /**
     *
     */
    public List<Boolean> getVisibleFlagList() {
        if (this.mElementGroupSetList.size() == 0) {
            return new ArrayList<Boolean>();
        }
        SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) this.mElementGroupSetList
                .get(0);
        List<Boolean> list = groupSet.getVisibleFlagList();
        return list;
    }

    /**
     *
     */
    public void setSelected(final boolean b) {
        this.mSelectedFlag = b;
        ArrayList list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGISelectable groupSet = (SGISelectable) list.get(ii);
            groupSet.setSelected(b);
        }
    }

    /**
     *
     * @param b
     * @return
     */
    public void setVisible(final boolean b) {
        super.setVisible(b);
        ArrayList list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            groupSet.setVisible(b);
        }
    }

    /**
     *
     */
    public boolean setName(final String name) {
        super.setName(name);
        ArrayList list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            groupSet.setName(name);
        }
        return true;
    }

    /**
     *
     * @param b
     * @return
     */
    public boolean setVisibleInLegend(final boolean b) {
        super.setVisibleInLegend(b);
        ArrayList list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            groupSet.setVisibleInLegend(b);
        }
        return true;
    }

    /**
     * Returns whether this group set contains the given point.
     * 
     * @param x
     *          x coordinate
     * @param y
     *          y coordinate
     * @return true if this group set contains the given point
     */
    @Override
    public boolean contains(final int x, final int y) {
        ArrayList<SGIElementGroupSetForData> list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            if (!groupSet.isVisible()) {
            	continue;
            }
            if (groupSet.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public SGElementGroup getElementGroupAt(final int x, final int y) {
        ArrayList<SGIElementGroupSetForData> list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            if (groupSet.getData() == null) {
            	continue;
            }
            SGElementGroup group = groupSet.getElementGroupAt(x, y);
            if (group != null) {
                return group;
            }
        }
        return null;
    }

    /**
     *
     */
    public boolean setMagnification(final float mag) {
        super.setMagnification(mag);
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
            SGElementGroupSet groupSet = (SGElementGroupSet) this.mElementGroupSetList
                    .get(ii);
            groupSet.setMagnification(mag);
        }

        return true;
    }

    /**
     *
     */
    public boolean onDrawingElement(final int x, final int y) {

        ArrayList list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            boolean flag = groupSet.onDrawingElement(x, y);
            if (flag) {
                return true;
            }
        }

        return false;
    }

    /**
     *
     */
    public void paintGraphics2D(final Graphics2D g2d) {
        ArrayList list = this.mElementGroupSetList;
        for (int ii = list.size() - 1; ii >= 0; ii--) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            if (groupSet.isVisible()) {
                groupSet.paintGraphics2D(g2d);
            }
        }
        
        this.paintFocusedShapes(g2d);
    }

    /**
     *
     */
    public void setClipFlag(final boolean b) {
        super.setClipFlag(b);
        ArrayList list = this.mElementGroupSetList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSetInGraph grouptSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            grouptSet.setClipFlag(b);
        }
    }

    /**
     * Returns the first element group set.
     * @return
     *         the first element group set
     */
    public SGIElementGroupSetForData getFirst() {
        if (this.mElementGroupSetList.size() == 0) {
            return null;
        } else {
            return (SGElementGroupSetForData) this.mElementGroupSetList.get(0);
        }
    }

    /**
     * Returns whether this group set "contains" the given group set.
     *
     * @param gs
     *           the group set
     * @return true if this group set "contains" the given group set
     */
    public boolean contains(SGElementGroupSetForData gs) {
    	if (super.contains(gs)) {
    		return true;
    	}
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGIElementGroupSetForData groupSet = this.mElementGroupSetList.get(ii);
    		if (groupSet.equals(gs)) {
    			return true;
    		}
    	}
    	return false;
    }

    /**
     * Sets the data.
     *
     * @param data
     *            a data object
     * @return true if succeeded
     */
    public boolean setData(SGData data) {
    	if (super.setData(data) == false) {
    		return false;
    	}
    	if (data == null) {
        	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        		SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);

        		// disposes of the data of child group set
        		SGData d = gs.getData();
        		if (d != null) {
            		d.dispose();
        		}

        		// set null
        		gs.setData(null);
        	}
    	}
    	return true;
    }

    /**
     *
     */
    public boolean addDrawingElementGroup(final int type) {

        SGISXYTypeData data = (SGISXYTypeData) this.mData;
        SGElementGroup group = null;
        if (type == SGIElementGroupConstants.POLYLINE_GROUP) {
            group = new SGElementGroupLineInGraph(data, this.mGraph);
        } else if (type == SGIElementGroupConstants.RECTANGLE_GROUP) {
            group = new SGElementGroupBarInGraph(data, this.mGraph);
        } else if (type == SGIElementGroupConstants.SYMBOL_GROUP) {
            group = new SGElementGroupSymbolInGraph(data, this.mGraph);
        } else {
            return false;
        }

        // set the group set
        SGIElementGroupInGraph iGroup = (SGIElementGroupInGraph) group;
        iGroup.setElementGroupSet(this);

        // only one element
        if (group.initDrawingElement(1) == false) {
        	return false;
        }

//        if (group.setPropertiesOfDrawingElements() == false) {
//            return false;
//        }

        // add a group to the list
        this.mDrawingElementGroupList.add(group);

        return true;
    }

    /**
     *
     * @return
     */
    public String getClassDescription() {
        return "";
    }

    /**
     * Updates the location of drawing elements.
     * This method is overridden to update the child data objects.
     *
     * @param data
     *            a data object
     * @return true if succeeded
     */
    public boolean updateDrawingElementsLocation(final SGIData data) {
        if ((data instanceof SGISXYTypeMultipleData) == false) {
            return false;
        }
        SGISXYTypeMultipleData dataMult = (SGISXYTypeMultipleData) data;
        SGISXYTypeSingleData[] sxyArray = dataMult.getSXYDataArray();
        final int dataNum = sxyArray.length;

        try {
    		// updates the location
            for (int ii = 0; ii < dataNum; ii++) {
                SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList
                        .get(ii);

                // updates the drawing elements
                if (gs.updateDrawingElementsLocation(sxyArray[ii]) == false) {
                    return false;
                }
            }
            
            // updates the cache
        	SGDataUtility.updateCache(dataMult, sxyArray);

        } finally {
        	if (sxyArray != null) {
    	    	// disposes of data objects
    	        SGDataUtility.disposeSXYDataArray(sxyArray);
        	}
        }

        return true;
    }

    /**
     * Initializes the line style of child objects.
     *
     * @return true if succeeded
     */
    @Override
    public boolean initChildLineStyle() {
        final int dataNum = this.mElementGroupSetList.size();
        if (dataNum > 1 && this.mLineColorAutoAssigned) {
        	List<SGLineStyle> lineStyleList = SGUtilityForFigureElement.createLineStyleList(this.getLineColorMap(), dataNum);
            for (int ii = 0; ii < dataNum; ii++) {
                SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
                SGLineStyle s = lineStyleList.get(ii);
                gs.setLineStyle(s);
            }
        } else {
            for (int ii = 0; ii < dataNum; ii++) {
                SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
                SGLineStyle s = new SGLineStyle(DEFAULT_LINE_TYPE, DEFAULT_LINE_COLOR, DEFAULT_LINE_WIDTH);
                gs.setLineStyle(s);
            }
        }
        return true;
    }


    /**
     *
     */
    public Rectangle2D getTickLabelsBoundingBox(final SGIData data) {
        if ((data instanceof SGISXYTypeMultipleData) == false) {
            throw new IllegalArgumentException("(data instanceof SGISXYTypeMultipleData) == false");
        }
        ArrayList<Rectangle2D> rectList = new ArrayList<Rectangle2D>();
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGIElementGroupSetForData groupSet = this.mElementGroupSetList.get(ii);
        	SGIElementGroupSetInGraphSXY gs = (SGIElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
        	Rectangle2D rect = gs.getTickLabelsBoundingBox(groupSet.getData());
        	if (rect == null) {
        		continue;
        	}
        	rectList.add(rect);
        }
        Rectangle2D uniRect = SGUtility.createUnion(rectList);
        if (uniRect == null) {
        	return null;
        }
        return uniRect;
    }

    /**
     * Paints tick labels.
     *
     * @param g2d
     *            the graphics context
     * @return true if succeeded
     */
    public boolean paintDataString(final Graphics2D g2d) {

    	SGData data = this.getData();
    	if (data instanceof SGISXYMultipleDimensionData) {
            for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
            	SGIElementGroupSetInGraphSXY gs = (SGIElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    			gs.paintDataString(g2d);
            }
    	} else {
        	// Paints tick labels of child SXY data sets.
        	// Checks overlapping of tick label resources.
            List<Integer> indexList = new ArrayList<Integer>();
        	List<SGISXYTypeSingleData> dataList = new ArrayList<SGISXYTypeSingleData>();
            for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
                SGIElementGroupSetForData gs = this.mElementGroupSetList.get(ii);
                SGISXYTypeSingleData d = (SGISXYTypeSingleData) gs.getData();
            	if (!d.isTickLabelAvailable()) {
            		continue;
            	}
            	boolean available = true;
            	for (int jj = 0; jj < dataList.size(); jj++) {
            		SGISXYTypeSingleData avData = dataList.get(jj);
            		if (d.hasEqualTickLabelResource(avData)) {
            			available = false;
            			break;
            		}
            	}
            	if (available) {
            		dataList.add(d);
            		indexList.add(ii);
            	}
            }
            for (int ii = 0; ii < indexList.size(); ii++) {
    			int index = indexList.get(ii);
    			SGIElementGroupSetInGraphSXY groupSet = (SGIElementGroupSetInGraphSXY) this.mElementGroupSetList
    					.get(index);
    			groupSet.paintDataString(g2d);
            }
    	}

        return true;
    }

    /**
     *
     */
    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
    }

    /**
     *
     */
    public boolean setXAxisLocation(final int location) {
        if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
            return false;
        }
        this.setXAxis(this.getAxis(location));
        return true;
    }

    /**
     *
     */
    public boolean setYAxisLocation(final int location) {
        if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
            return false;
        }
        this.setYAxis(this.getAxis(location));
        return true;
    }

    private SGAxis getAxis(final int location) {
        return this.mGraph.getAxisElement().getAxisInPlane(location);
    }

    public boolean setXAxis(SGAxis axis) {
        super.setXAxis(axis);
        ArrayList list = this.mElementGroupSetList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            groupSet.setXAxis(axis);
        }
        return true;
    }

    public boolean setYAxis(SGAxis axis) {
        super.setYAxis(axis);
        ArrayList list = this.mElementGroupSetList;
        for (int ii = 0; ii < list.size(); ii++) {
            SGElementGroupSetInGraph groupSet = (SGElementGroupSetInGraph) list
                    .get(ii);
            groupSet.setYAxis(axis);
        }
        return true;
    }

    /**
     *
     * @param document
     * @return
     */
    public Element createElement(final Document document, final SGExportParameter params) {
        Element el = document.createElement(this.getTagName());
        if (this.writeProperty(el, params) == false) {
            return null;
        }

        // gets the first group set
        SGElementGroupSetInGraphSXY firstGroupSet = (SGElementGroupSetInGraphSXY) this.getFirst();
        ArrayList<SGElementGroup> firstGroupList = firstGroupSet.getElementGroupList();
        Element elLine = null;
        for (int ii = 0; ii < firstGroupList.size(); ii++) {
            SGElementGroup group = firstGroupList.get(ii);
        	Element element = group.createElement(document);
            if (element == null) {
                return null;
            }
            if (group instanceof SGElementGroupLine) {
            	elLine = element;
            }
            el.appendChild(element);
        }

        // gets the parameters of line groups
    	Element elLineStyles = document.createElement(SGILineConstants.TAG_NAME_STYLES);
        final int num = this.mElementGroupSetList.size();
        for (int ii = 0; ii < num; ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
        	SGElementGroupLine lineGroup = gs.getLineGroup();
        	Element elLineStyle = document.createElement(SGILineConstants.TAG_NAME_STYLE);
            if (SGElementGroupLine.writeProperty(elLineStyle, lineGroup.getLineType(), lineGroup.getColor(), 
            		lineGroup.getLineWidth()) == false) {
            	return null;
            }
            elLineStyles.appendChild(elLineStyle);
        }
        elLine.appendChild(elLineStyles);
        
        // color maps
        Element elColorMaps = this.mLineColorMapManager.createElement(document, params);
        if (elColorMaps == null) {
        	return null;
        }
        elLine.appendChild(elColorMaps);

        return el;
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

        // axes
        el.setAttribute(SGIFigureElement.KEY_X_AXIS_POSITION, this.getXAxisString());
        el.setAttribute(SGIFigureElement.KEY_Y_AXIS_POSITION, this.getYAxisString());

        // shift
        el.setAttribute(KEY_SHIFT_X, Double.toString(this.getShiftX()));
        el.setAttribute(KEY_SHIFT_Y, Double.toString(this.getShiftY()));

        return true;
    }
    
    public String getXAxisString() {
        SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) this.getFirst();
        final String str = this.mGraph.getAxisElement().getLocationName(
                groupSet.getXAxis());
        return str;
    }

    public String getYAxisString() {
        SGElementGroupSetInGraphSXY groupSet = (SGElementGroupSetInGraphSXY) this.getFirst();
        final String str = this.mGraph.getAxisElement().getLocationName(
                groupSet.getYAxis());
        return str;
    }

    // Line
    public List<SGElementGroupLine> getLineGroups() {
    	return SGMultipleSXYUtility.getLineGroups(this);
    }

    public List<SGElementGroupLine> getLineGroupsIgnoreNull() {
    	return SGMultipleSXYUtility.getLineGroupsIgnoreNull(this);
    }

    public SGElementGroupLine getLineGroup() {
		return (SGElementGroupLine) SGUtilityForFigureElement.getGroup(
				SGElementGroupLine.class, this.mDrawingElementGroupList);
	}

    public boolean isLineVisible() {
        return this.getLineGroup().isVisible();
    }

    public float getLineWidth() {
        return this.getLineGroup().getLineWidth();
    }

    public float getLineWidth(final String unit) {
        return (float) SGUtilityText
                .convertFromPoint(this.getLineWidth(), unit);
    }

    public int getLineType() {
        return this.getLineGroup().getLineType();
    }

    public Color getLineColor() {
        return this.getLineGroup().getColor();
    }

    /**
     * Returns whether the lines connect all effective points.
     *
     * @return true if connecting all effective points
     */
    public boolean isLineConnectingAll() {
        return this.getLineGroup().isLineConnectingAll();
    }

    public boolean setLineVisible(final boolean b) {
    	return SGMultipleSXYUtility.setLineVisible(this.getLineGroupsIgnoreNull(), b);
    }

    public boolean setLineWidth(final float width, final String unit) {
    	return SGMultipleSXYUtility.setLineWidth(this, width, unit);
    }

    public boolean setLineType(final int type) {
    	return SGMultipleSXYUtility.setLineType(this, type);
    }

    public boolean setLineColor(final Color cl) {
    	return SGMultipleSXYUtility.setLineColor(this, cl);
    }

    /**
     * Sets whether the lines connect all effective points.
     *
     * @return true if succeeded
     */
    public boolean setLineConnectingAll(final boolean b) {
    	return SGMultipleSXYUtility.setLineConnectingAll(this.getLineGroups(), b);
    }

    // Symbol
    public List<SGElementGroupSymbol> getSymbolGroups() {
    	return SGMultipleSXYUtility.getSymbolGroups(this);
    }

    public List<SGElementGroupSymbol> getSymbolGroupsIgnoreNull() {
    	return SGMultipleSXYUtility.getSymbolGroupsIgnoreNull(this);
    }

    public SGElementGroupSymbol getSymbolGroup() {
		return (SGElementGroupSymbol) SGUtilityForFigureElement.getGroup(
				SGElementGroupSymbol.class, this.mDrawingElementGroupList);
	}

    public boolean isSymbolVisible() {
        return this.getSymbolGroup().isVisible();
    }

    public int getSymbolType() {
        return this.getSymbolGroup().getType();
    }

    public float getSymbolSize() {
        return this.getSymbolGroup().getSize();
    }

    public float getSymbolSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this.getSymbolSize(),
                unit);
    }

    public float getSymbolLineWidth() {
        return this.getSymbolGroup().getLineWidth();
    }

    public float getSymbolLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(
                this.getSymbolLineWidth(), unit);
    }

    @Override
    public SGIPaint getSymbolInnerPaint() {
        return this.getSymbolGroup().getInnerPaint();
    }

    public Color getSymbolLineColor() {
        return this.getSymbolGroup().getLineColor();
    }

    @Override
    public boolean isSymbolLineVisible() {
        return this.getSymbolGroup().isLineVisible();
    }

    public boolean setSymbolVisible(final boolean b) {
    	return SGMultipleSXYUtility.setSymbolVisible(this.getSymbolGroupsIgnoreNull(), b);
    }

    public boolean setSymbolType(final int type) {
    	return SGMultipleSXYUtility.setSymbolType(this.getSymbolGroupsIgnoreNull(), type);
    }

    public boolean setSymbolSize(final float size) {
    	return SGMultipleSXYUtility.setSymbolSize(this.getSymbolGroupsIgnoreNull(), size);
    }

    public boolean setSymbolSize(final float size, final String unit) {
    	return SGMultipleSXYUtility.setSymbolSize(this.getSymbolGroupsIgnoreNull(), size, unit);
    }

    public boolean setSymbolLineWidth(final float width) {
    	return SGMultipleSXYUtility.setSymbolLineWidth(this.getSymbolGroupsIgnoreNull(), width);
    }

    public boolean setSymbolLineWidth(final float width, final String unit) {
    	return SGMultipleSXYUtility.setSymbolLineWidth(this.getSymbolGroupsIgnoreNull(), width, unit);
    }

    @Override
    public boolean setSymbolInnerPaint(final SGIPaint paint) {
    	return SGMultipleSXYUtility.setSymbolInnerPaint(this.getSymbolGroupsIgnoreNull(), paint);
    }

    public boolean setSymbolLineColor(final Color cl) {
    	return SGMultipleSXYUtility.setSymbolLineColor(this.getSymbolGroupsIgnoreNull(), cl);
    }

    @Override
    public boolean setSymbolLineVisible(final boolean b) {
        return SGMultipleSXYUtility.setSymbolLineVisible(this.getSymbolGroupsIgnoreNull(), b);
    }

    // Bar
    public List<SGElementGroupBar> getBarGroups() {
    	return SGMultipleSXYUtility.getBarGroups(this);
    }

    public List<SGElementGroupBar> getBarGroupsIgnoreNull() {
    	return SGMultipleSXYUtility.getBarGroupsIgnoreNull(this);
    }

    public SGElementGroupBar getBarGroup() {
        return (SGElementGroupBar) SGUtilityForFigureElement.getGroup(
                SGElementGroupBar.class, this.mDrawingElementGroupList);
    }

    public boolean isBarVisible() {
        if (null==this.getBarGroup()) {
            return false;
        }
        return this.getBarGroup().isVisible();
    }

    public double getBarBaselineValue() {
        return this.getBarGroup().getBaselineValue();
    }

    public float getBarWidth() {
        return this.getBarGroup().getRectangleWidth();
    }

    public double getBarWidthValue() {
        return this.getBarGroup().getWidthValue();
    }

    public float getBarEdgeLineWidth() {
        return this.getBarGroup().getEdgeLineWidth();
    }

    public float getBarEdgeLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this
                .getBarEdgeLineWidth(), unit);
    }

    @Override
    public SGIPaint getBarInnerPaint() {
        return this.getBarGroup().getInnerPaint();
    }

    @Override
    public Color getBarEdgeLineColor() {
        return this.getBarGroup().getEdgeLineColor();
    }

    @Override
    public boolean isBarEdgeLineVisible() {
        return this.getBarGroup().isEdgeLineVisible();
    }

    @Override
    public boolean isBarVertical() {
        return this.getBarGroup().isVertical();
    }

    @Override
    public double getBarInterval() {
        return this.getBarGroup().getInterval();
    }

    public boolean setBarVisible(final boolean b) {
    	return SGMultipleSXYUtility.setBarVisible(this.getBarGroupsIgnoreNull(), b);
    }

    public boolean setBarBaselineValue(final double value) {
    	return SGMultipleSXYUtility.setBarBaselineValue(this.getBarGroupsIgnoreNull(), value);
    }

    public boolean setBarWidth(final float width) {
    	return SGMultipleSXYUtility.setBarWidth(this.getBarGroupsIgnoreNull(), width);
    }

    public boolean setBarWidthValue(final double value) {
    	return SGMultipleSXYUtility.setBarWidthValue(this.getBarGroupsIgnoreNull(), value);
    }

    public boolean setBarEdgeLineWidth(final float width) {
    	return SGMultipleSXYUtility.setBarEdgeLineWidth(this.getBarGroupsIgnoreNull(), width);
    }

    public boolean setBarEdgeLineWidth(final float width, final String unit) {
    	return SGMultipleSXYUtility.setBarEdgeLineWidth(this.getBarGroupsIgnoreNull(), width, unit);
    }

    @Override
    public boolean setBarInnerPaint(final SGIPaint paint) {
        return SGMultipleSXYUtility.setBarInnerPaint(this.getBarGroupsIgnoreNull(), paint);
    }

    @Override
    public boolean setBarEdgeLineColor(final Color cl) {
    	return SGMultipleSXYUtility.setBarEdgeLineColor(this.getBarGroupsIgnoreNull(), cl);
    }

    @Override
    public boolean setBarEdgeLineVisible(final boolean b) {
        return SGMultipleSXYUtility.setBarEdgeLineVisible(this.getBarGroupsIgnoreNull(), b);
    }

    @Override
    public boolean setBarVertical(final boolean b) {
        return SGMultipleSXYUtility.setBarVertical(this.getBarGroupsIgnoreNull(), b);
    }

    public boolean hasValidBaselineValue(final int location, final Number value) {
        return ((SGElementGroupSetInGraphSXY) this.getFirst()).hasValidBaselineValue(location, value);
    }

    @Override
    public boolean setBarInterval(final double interval) {
        return SGMultipleSXYUtility.setBarInterval(this.getBarGroupsIgnoreNull(), interval);
    }

    // Error Bar
    public boolean isErrorBarAvailable() {
    	SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) this.mData;
        return data.isErrorBarAvailable();
    }

    public List<SGElementGroupErrorBar> getErrorBarGroups() {
    	return SGMultipleSXYUtility.getErrorBarGroups(this);
    }

    public List<SGElementGroupErrorBar> getErrorBarGroupsIgnoreNull() {
    	return SGMultipleSXYUtility.getErrorBarGroupsIgnoreNull(this);
    }

    public SGElementGroupErrorBar getErrorBarGroup() {
		return (SGElementGroupErrorBar) SGUtilityForFigureElement.getGroup(
				SGElementGroupErrorBar.class, this.mDrawingElementGroupList);
	}

    public boolean isErrorBarVisible() {
        return this.getErrorBarGroup().isVisible();
    }

	public boolean isErrorBarVertical() {
		return this.getErrorBarGroup().isVertical();
	}

    public int getErrorBarHeadType() {
        return this.getErrorBarGroup().getHeadType();
    }

    public float getErrorBarHeadSize() {
        return this.getErrorBarGroup().getHeadSize();
    }

    public float getErrorBarHeadSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this
                .getErrorBarHeadSize(), unit);
    }

    public Color getErrorBarColor() {
        return this.getErrorBarGroup().getColor();
    }

    public float getErrorBarLineWidth() {
        return this.getErrorBarGroup().getLineWidth();
    }

    public float getErrorBarLineWidth(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this
                .getErrorBarLineWidth(), unit);
    }

    public int getErrorBarStyle() {
        return this.getErrorBarGroup().getErrorBarStyle();
    }

    @Override
    public boolean isErrorBarOnLinePosition() {
        return this.getErrorBarGroup().isPositionOnLine();
    }

    public boolean setErrorBarVisible(final boolean b) {
    	return SGMultipleSXYUtility.setErrorBarVisible(this.getErrorBarGroupsIgnoreNull(), b);
    }

    public boolean setErrorBarHeadType(final int type) {
    	return SGMultipleSXYUtility.setErrorBarHeadType(this.getErrorBarGroupsIgnoreNull(), type);
    }

    public boolean setErrorBarHeadSize(final float size) {
    	return SGMultipleSXYUtility.setErrorBarHeadSize(this.getErrorBarGroupsIgnoreNull(), size);
    }

    public boolean setErrorBarHeadSize(final float size, final String unit) {
    	return SGMultipleSXYUtility.setErrorBarHeadSize(this.getErrorBarGroupsIgnoreNull(), size, unit);
    }

    public boolean setErrorBarColor(final Color cl) {
    	return SGMultipleSXYUtility.setErrorBarColor(this.getErrorBarGroupsIgnoreNull(), cl);
    }

    public boolean setErrorBarLineWidth(final float width) {
    	return SGMultipleSXYUtility.setErrorBarLineWidth(this.getErrorBarGroupsIgnoreNull(), width);
    }

    public boolean setErrorBarLineWidth(final float width, final String unit) {
    	return SGMultipleSXYUtility.setErrorBarLineWidth(this.getErrorBarGroupsIgnoreNull(), width, unit);
    }

    public boolean setErrorBarStyle(final int style) {
    	return SGMultipleSXYUtility.setErrorBarStyle(this.getErrorBarGroupsIgnoreNull(), style);
    }

    public boolean setErrorBarVertical(final boolean b) {
        return SGMultipleSXYUtility.setErrorBarVertical(this.getErrorBarGroupsIgnoreNull(), b);
    }

    @Override
    public boolean setErrorBarOnLinePosition(boolean b) {
        return SGMultipleSXYUtility.setErrorBarOnLinePosition(this.getErrorBarGroupsIgnoreNull(), b);
    }


    // Tick Label
    public boolean isTickLabelAvailable() {
    	SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) this.mData;
        return data.isTickLabelAvailable();
    }

    public List<SGElementGroupTickLabel> getTickLabelGroups() {
    	return SGMultipleSXYUtility.getTickLabelGroups(this);
    }

    public List<SGElementGroupTickLabel> getTickLabelGroupsIgnoreNull() {
    	return SGMultipleSXYUtility.getTickLabelGroupsIgnoreNull(this);
    }

    public SGElementGroupTickLabel getTickLabelGroup() {
		return (SGElementGroupTickLabel) SGUtilityForFigureElement.getGroup(
				SGElementGroupTickLabel.class, this.mDrawingElementGroupList);
	}

    public boolean isTickLabelVisible() {
        return this.getTickLabelGroup().isVisible();
    }

    public String getTickLabelFontName() {
        return this.getTickLabelGroup().getFontName();
    }

    public int getTickLabelFontStyle() {
        return this.getTickLabelGroup().getFontStyle();
    }

    public float getTickLabelFontSize() {
        return this.getTickLabelGroup().getFontSize();
    }

    public float getTickLabelFontSize(final String unit) {
        return (float) SGUtilityText.convertFromPoint(this
                .getTickLabelFontSize(), unit);
    }

    public Color getTickLabelColor() {
        return this.getTickLabelGroup().getColor();
    }

    public float getTickLabelAngle() {
        return this.getTickLabelGroup().getAngle();
    }

    public int getTickLabelDecimalPlaces() {
        return this.getTickLabelGroup().getDecimalPlaces();
    }

    public int getTickLabelExponent() {
        return this.getTickLabelGroup().getExponent();
    }

	@Override
	public String getTickLabelDateFormat() {
		return this.getTickLabelGroup().getDateFormat();
	}

	public boolean hasTickLabelHorizontalAlignment() {
		return this.getTickLabelGroup().hasHorizontalAlignment();
	}

    public boolean setTickLabelVisible(final boolean b) {
        return SGMultipleSXYUtility.setTickLabelVisible(this.getTickLabelGroupsIgnoreNull(), b);
    }

    public boolean setTickLabelFontName(final String name) {
        return SGMultipleSXYUtility.setTickLabelFontName(this.getTickLabelGroupsIgnoreNull(), name);
    }

    public boolean setTickLabelFontStyle(final int style) {
        return SGMultipleSXYUtility.setTickLabelFontStyle(this.getTickLabelGroupsIgnoreNull(), style);
    }

    public boolean setTickLabelFontSize(final float size) {
        return SGMultipleSXYUtility.setTickLabelFontSize(this.getTickLabelGroupsIgnoreNull(), size);
    }

    public boolean setTickLabelFontSize(final float size, final String unit) {
        return SGMultipleSXYUtility.setTickLabelFontSize(this.getTickLabelGroupsIgnoreNull(), size, unit);
    }

    public boolean setTickLabelColor(final Color cl) {
        return SGMultipleSXYUtility.setTickLabelColor(this.getTickLabelGroupsIgnoreNull(), cl);
    }

    public boolean setTickLabelAngle(final float angle) {
        return SGMultipleSXYUtility.setTickLabelAngle(this.getTickLabelGroupsIgnoreNull(), angle);
    }

    public boolean setTickLabelDecimalPlaces(final int dp) {
        return SGMultipleSXYUtility.setTickLabelDecimalPlaces(this.getTickLabelGroupsIgnoreNull(), dp);
    }

    public boolean setTickLabelExponent(final int exp) {
        return SGMultipleSXYUtility.setTickLabelExponent(this.getTickLabelGroupsIgnoreNull(), exp);
    }

	public boolean setTickLabelHorizontalAlignment(final boolean b) {
		return SGMultipleSXYUtility.setTickLabelHorizontalAlignment(
				this.getTickLabelGroupsIgnoreNull(), b);
	}

    public ELEMENT_TYPE getSelectedGroupType() {
        return this.mSelectedGroupType;
    }

    private ELEMENT_TYPE mSelectedGroupType = ELEMENT_TYPE.Void;

    /**
     *
     * @param e
     * @param groupSet
     * @return
     */
    @Override
    protected boolean onMouseClicked(final MouseEvent e) {
        SGElementGroup group = this.clickGroup(e);
        if (group == null) {
        	return false;
        }
        ELEMENT_TYPE type = ELEMENT_TYPE.Void;
		if (group instanceof SGElementGroupLineInGraph) {
			type = ELEMENT_TYPE.Line;
		} else if (group instanceof SGElementGroupSymbolInGraph) {
			type = ELEMENT_TYPE.Symbol;
		} else if (group instanceof SGElementGroupBarInGraph) {
			type = ELEMENT_TYPE.Bar;
		} else if (group instanceof SGElementGroupErrorBar) {
			type = ELEMENT_TYPE.ErrorBar;
		} else if (group instanceof SGElementGroupTickLabel) {
			type = ELEMENT_TYPE.TickLabel;
		}
        this.mSelectedGroupType = type;
        return true;
    }

    /**
     *
     */
    public int getXAxisLocation() {
		return this.mGraph.getAxisElement().getLocationInPlane(this.getXAxis());
	}

    /**
     *
     */
    public int getYAxisLocation() {
		return this.mGraph.getAxisElement().getLocationInPlane(this.getYAxis());
    }

    /**
     *
     */
    public boolean getLegendVisibleFlag() {
        return this.isVisibleInLegend();
    }

    @Override
    public SGIElementGroupSetForData[] getChildGroupSetArray() {
        SGIElementGroupSetForData[] array = new SGIElementGroupSetForData[this.mElementGroupSetList.size()];
        this.mElementGroupSetList.toArray(array);
        return array;
    }

    /**
     * Returns the number of child data objects.
     * 
     * @return the number of child data objects
     */
    @Override
    public int getChildNumber() {
    	return this.mElementGroupSetList.size();
    }

    /**
     * Returns the location of a line at a given index.
     *
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getLineLocation(final int groupIndex, final int index) {
        if (groupIndex < 0 || groupIndex >= this.mElementGroupSetList.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(groupIndex);
        return gs.getLineLocation(index);
    }

    /**
     * Returns the location of a symbol at a given index.
     *
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getSymbolLocation(final int groupIndex, final int index) {
        if (groupIndex < 0 || groupIndex >= this.mElementGroupSetList.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(groupIndex);
        return gs.getSymbolLocation(index);
    }

    /**
     * Returns the location of a bar at a given index.
     *
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getBarLocation(final int groupIndex, final int index) {
        if (groupIndex < 0 || groupIndex >= this.mElementGroupSetList.size()) {
            throw new IllegalArgumentException("Index out of bounds: " + index);
        }
        SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(groupIndex);
        return gs.getBarLocation(index);
    }

    @Override
    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {

        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        result = super.setProperties(map, result);
        if (result == null) {
            return null;
        }

        result = SGUtilityForFigureElementJava2D
				.checkDataElementGroupVisibility(this, map, result);
		if (result == null) {
			return null;
		}

        List<SGElementGroupLine> lineGroups = this.getLineGroups();
        for (int ii = 0; ii < lineGroups.size(); ii++) {
            SGElementGroupLineForData lines = (SGElementGroupLineForData) lineGroups.get(ii);
            result = lines.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }
        
        List<SGElementGroupSymbol> symbolGroups = this.getSymbolGroups();
        for (int ii = 0; ii < symbolGroups.size(); ii++) {
            SGElementGroupSymbolForData symbols = (SGElementGroupSymbolForData) symbolGroups.get(ii);
            result = symbols.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }

        List<SGElementGroupBar> barGroups = this.getBarGroups();
        for (int ii = 0; ii < barGroups.size(); ii++) {
            SGElementGroupBarForData bar = (SGElementGroupBarForData) barGroups.get(ii);
            result = bar.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }

        List<SGElementGroupErrorBar> errorBarGroups = this.getErrorBarGroups();
        for (int ii = 0; ii < errorBarGroups.size(); ii++) {
            SGElementGroupErrorBarForData errorBar = (SGElementGroupErrorBarForData) errorBarGroups.get(ii);
            result = errorBar.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }

        List<SGElementGroupTickLabel> tickLabelGroups = this.getTickLabelGroups();
        for (int ii = 0; ii < tickLabelGroups.size(); ii++) {
            SGElementGroupTickLabelForData tickLabel = (SGElementGroupTickLabelForData) tickLabelGroups.get(ii);
            result = tickLabel.setProperties(map, result);
            if (result == null) {
                return null;
            }
        }

        Map<String, SGInteger> pickUpMap = new HashMap<String, SGInteger>();
		List<String> keys = map.getKeys();
        final boolean dataColumnContained = keys.contains(COM_DATA_COLUMN_TYPE.toUpperCase());
        final boolean pickUpContained = keys.contains(COM_DATA_PICKUP_DIMENSION.toUpperCase());
        final boolean timeContained = keys.contains(COM_DATA_ANIMATION_FRAME_DIMENSION.toUpperCase());
        Iterator<String> itr = map.getKeyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = map.getValueString(key);
            if (COM_DATA_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineWidth(num.floatValue(), unit
                        .toString()) == false) {
                    result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_LINE_TYPE.equalsIgnoreCase(key)) {
                Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setLineType(type) == false) {
                    result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_LINE_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (this.setLineColor(cl) == false) {
                        result.putResult(COM_DATA_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
    				if (cl != null) {
        				if (this.setLineColor(cl) == false) {
        					result.putResult(COM_DATA_LINE_COLOR,
        							SGPropertyResults.INVALID_INPUT_VALUE);
        					continue;
        				}
    				} else {
    					result.putResult(COM_DATA_LINE_COLOR,
    							SGPropertyResults.INVALID_INPUT_VALUE);
    					continue;
    				}
                }
                result.putResult(COM_DATA_LINE_COLOR, SGPropertyResults.SUCCEEDED);
//			} else if (COM_DATA_COLOR_STYLE_REVERSED.equalsIgnoreCase(key)) {
//                Boolean b = SGUtilityText.getBoolean(value);
//                if (b == null) {
//                    result.putResult(COM_DATA_COLOR_STYLE_REVERSED,
//                            SGPropertyResults.INVALID_INPUT_VALUE);
//                    continue;
//                }
//                this.mLineColorMapManager.setReversedOrder(b.booleanValue());
//                result.putResult(COM_DATA_COLOR_STYLE_REVERSED,
//                        SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_SHIFT_X.equalsIgnoreCase(key)) {
				Number num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_DATA_SHIFT_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (!this.setShiftX(num.doubleValue())) {
					result.putResult(COM_DATA_SHIFT_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_DATA_SHIFT_X, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_SHIFT_Y.equalsIgnoreCase(key)) {
				Number num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_DATA_SHIFT_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				if (!this.setShiftY(num.doubleValue())) {
					result.putResult(COM_DATA_SHIFT_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				result.putResult(COM_DATA_SHIFT_Y, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_PICKUP_DIMENSION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for pick up indices
        			continue;
        		}
        		if (timeContained) {
        			String timeValue = map.getValueString(COM_DATA_ANIMATION_FRAME_DIMENSION);
            		this.setPickUpAndTimeDimension(result, value, timeValue);
        		} else {
            		if (!this.setPickUpDimension(value)) {
                        result.putResult(COM_DATA_PICKUP_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
            		}
    				result.putResult(COM_DATA_PICKUP_DIMENSION, SGPropertyResults.SUCCEEDED);
        		}
			} else if (COM_DATA_ANIMATION_FRAME_DIMENSION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for pick up indices
        			continue;
        		}
        		if (pickUpContained) {
        			// if PickUpDimension command exists, skip the command for pick up indices
        			continue;
        		}
        		if (!this.setTimeDimension(value)) {
                    result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
				result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_PICKUP_INDICES.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for pick up indices
        			continue;
        		}
        		if (!this.setPickUpIndices(map, result, key, value, pickUpMap)) {
        			continue;
        		}
            } else if (COM_DATA_PICKUP_START.equalsIgnoreCase(key)) {
            	if (SGDataUtility.isNetCDFData(this.mData)) {
            		if (dataColumnContained) {
            			// if ColumnType command exists, skip the command for pick up indices
            			continue;
            		}
            		if (!this.setPickUpIndices(map, result, key, value, pickUpMap)) {
            			continue;
            		}
            	} else {
            		// cannot apply
                    result.putResult(COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            } else if (COM_DATA_PICKUP_END.equalsIgnoreCase(key)) {
            	if (SGDataUtility.isNetCDFData(this.mData)) {
            		if (dataColumnContained) {
            			// if ColumnType command exists, skip the command for pick up indices
            			continue;
            		}
            		if (!this.setPickUpIndices(map, result, key, value, pickUpMap)) {
            			continue;
            		}
            	} else {
            		// cannot apply
                    result.putResult(COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            } else if (COM_DATA_PICKUP_STEP.equalsIgnoreCase(key)) {
            	if (SGDataUtility.isNetCDFData(this.mData)) {
            		if (dataColumnContained) {
            			// if ColumnType command exists, skip the command for pick up indices
            			continue;
            		}
            		if (!this.setPickUpIndices(map, result, key, value, pickUpMap)) {
            			continue;
            		}
            	} else {
            		// cannot apply
                    result.putResult(COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            } else if (COM_DATA_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setStride(value) == false) {
                    result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_TICK_LABEL_ARRAY_SECTION.equalsIgnoreCase(key)) {
        		if (dataColumnContained) {
        			// if ColumnType command exists, skip the command for the stride
        			continue;
        		}
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setTickLabelStride(result, value) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
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
			}
		}
		
		if (!dataColumnContained) {
	        // set pick up parameters
			this.setPickUpParameters(map, result, pickUpMap);
		}

		// only for backward compatibility
        itr = map.getKeyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = map.getValueString(key);
			if (COM_DATA_LINE_SHIFT_X.equalsIgnoreCase(key)) {
				Number num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_DATA_LINE_SHIFT_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				final double shiftOld = this.getShiftX();
				if (this.setShiftX(num.doubleValue()) == false) {
					result.putResult(COM_DATA_LINE_SHIFT_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				List<SGElementGroupBar> barList = this.getBarGroups();
				for (SGElementGroupBar bar : barList) {
					final double barOffset = bar.getOffsetX() + shiftOld - num.doubleValue();
					if (bar.setOffsetX(barOffset) == false) {
						result.putResult(COM_DATA_LINE_SHIFT_X,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_DATA_LINE_SHIFT_X,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_LINE_SHIFT_Y.equalsIgnoreCase(key)) {
				Number num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_DATA_LINE_SHIFT_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				final double shiftOld = this.getShiftY();
				if (this.setShiftY(num.doubleValue()) == false) {
					result.putResult(COM_DATA_LINE_SHIFT_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				List<SGElementGroupBar> barList = this.getBarGroups();
				for (SGElementGroupBar bar : barList) {
					final double barOffset = bar.getOffsetY() + shiftOld - num.doubleValue();
					if (bar.setOffsetY(barOffset) == false) {
						result.putResult(COM_DATA_LINE_SHIFT_X,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_DATA_LINE_SHIFT_Y,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_BAR_SHIFT_X.equalsIgnoreCase(key)) {
				// only for backward compatibility
				Number num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_DATA_BAR_SHIFT_X,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				final double shiftX = this.getShiftX();
				List<SGElementGroupBar> barList = this.getBarGroups();
				for (SGElementGroupBar bar : barList) {
					final double offset = num.doubleValue() - shiftX;
					if (bar.setOffsetX(offset) == false) {
						result.putResult(COM_DATA_BAR_SHIFT_X,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_DATA_BAR_SHIFT_X,
						SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_BAR_SHIFT_Y.equalsIgnoreCase(key)) {
				// only for backward compatibility
				Number num = SGUtilityText.getDouble(value);
				if (num == null) {
					result.putResult(COM_DATA_BAR_SHIFT_Y,
							SGPropertyResults.INVALID_INPUT_VALUE);
					continue;
				}
				final double shiftY = this.getShiftY();
				List<SGElementGroupBar> barList = this.getBarGroups();
				for (SGElementGroupBar bar : barList) {
					final double offset = num.doubleValue() - shiftY;
					if (bar.setOffsetY(offset) == false) {
						result.putResult(COM_DATA_BAR_SHIFT_Y,
								SGPropertyResults.INVALID_INPUT_VALUE);
						continue;
					}
				}
				result.putResult(COM_DATA_BAR_SHIFT_Y,
						SGPropertyResults.SUCCEEDED);
			}
		}

        return result;
    }

    private void setPickUpParameters(SGPropertyMap map, SGPropertyResults result,
    		Map<String, SGInteger> pickUpMap) {
		SGPickUpDimensionInfo pickUpInfo = null;
		if (SGDataUtility.isNetCDFData(this.mData)) {
			SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) this.mData;
			pickUpInfo = ncData.getPickUpDimensionInfo();
		} else if (SGDataUtility.isMDArrayData(this.mData)) {
			SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) this.mData;
			pickUpInfo = mdData.getPickUpDimensionInfo();
		} else {
			return;
		}
		if (pickUpInfo == null) {
			return;
		}
		SGIntegerSeriesSet indices = pickUpInfo.getIndices();
		SGIntegerSeries series = indices.testReduce();
		SGInteger pickUpStart = pickUpMap.get(COM_DATA_PICKUP_START);
		if (pickUpStart == null && series != null) {
			pickUpStart = new SGInteger(series.getStart().getNumber());
		}
		SGInteger pickUpEnd = pickUpMap.get(COM_DATA_PICKUP_END);
		if (pickUpEnd == null && series != null) {
			pickUpEnd = new SGInteger(series.getEnd().getNumber());
		}
		SGInteger pickUpStep = pickUpMap.get(COM_DATA_PICKUP_STEP);
		if (pickUpStep == null && series != null) {
			pickUpStep = new SGInteger(series.getStep().getNumber());
		}
        if (pickUpStart != null && pickUpEnd != null && pickUpStep != null) {
        	this.setPickUpResults(map, result, pickUpStart, pickUpEnd, pickUpStep);
        }
    }

    private void setPickUpAndTimeDimension(SGPropertyResults result,
    		final String pickUpValue, final String timeValue) {

		if (!(this.mData instanceof SGSXYMDArrayMultipleData)) {
            return;
		}
		SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) this.mData;

		boolean pickUpValid = true;
		boolean timeValid = true;

		// creates dimension map
		Map<String, Integer> pickUpDimMap = this.createPickUpDimMap(sxyData, pickUpValue);
		if (pickUpValid && pickUpDimMap != null) {
			if (!this.setPickUpDimension(pickUpDimMap)) {
				pickUpValid = false;
			}
		} else {
			pickUpValid = false;
		}

		Map<String, Integer> timeDimMap = this.createTimeDimMap(sxyData, timeValue);
		if (pickUpDimMap != null && timeDimMap != null) {
			// checks overlapping
			Iterator<String> keyItr = timeDimMap.keySet().iterator();
			while (keyItr.hasNext()) {
				String name = keyItr.next();
				Integer timeIndex = timeDimMap.get(name);
				if (timeIndex == null || timeIndex == -1) {
					continue;
				}
				Integer pickUpIndex = pickUpDimMap.get(name);
				if (timeIndex.equals(pickUpIndex)) {
					timeValid = false;
					break;
				}
			}
		}
		if (timeValid && timeDimMap != null) {
			if (!this.setTimeDimension(timeDimMap)) {
				timeValid = false;
			}
		} else {
			timeValid = false;
		}

		// sets the status
		final int pickUpStatus = pickUpValid ? SGPropertyResults.SUCCEEDED : SGPropertyResults.INVALID_INPUT_VALUE;
        result.putResult(COM_DATA_PICKUP_DIMENSION, pickUpStatus);

		final int timeStatus = timeValid ? SGPropertyResults.SUCCEEDED : SGPropertyResults.INVALID_INPUT_VALUE;
        result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, timeStatus);
    }

    private boolean setPickUpDimension(final String value) {
		if (!(this.mData instanceof SGSXYMDArrayMultipleData)) {
            return false;
		}
		SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) this.mData;

		// creates dimension map
		Map<String, Integer> pickUpDimMap = this.createPickUpDimMap(sxyData, value);
		if (pickUpDimMap == null) {
			return false;
		}

		if (!this.setPickUpDimension(pickUpDimMap)) {
			// clears pick up information
			sxyData.clearPickUp();
			return false;
		}

		return true;
    }

    private boolean setPickUpDimension(Map<String, Integer> pickUpDimMap) {
		if (!(this.mData instanceof SGSXYMDArrayMultipleData)) {
            return false;
		}
		SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) this.mData;

		boolean pickUpExists = false;
		List<Integer> pickUpIndexList = new ArrayList<Integer>(pickUpDimMap.values());
		for (int ii = 0; ii < pickUpIndexList.size(); ii++) {
			Integer index = pickUpIndexList.get(ii);
			if (index != -1) {
				pickUpExists = true;
				break;
			}
		}
		if (!pickUpExists) {
			if (sxyData.isDimensionPicked()) {
				// clears picked up information
				sxyData.clearPickUp();
			}
            return true;
		}

		// get current length and indices of picked up dimensions
		int curLen = -1;
		SGIntegerSeriesSet curIndices = null;
		if (sxyData.isDimensionPicked()) {
			SGMDArrayPickUpDimensionInfo curPickUpInfo = (SGMDArrayPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();
			Map<String, Integer> curPickUpDimensionMap = curPickUpInfo.getDimensionMap();
			Entry<String, Integer> firstEntry = curPickUpDimensionMap.entrySet().iterator().next();
			String name = firstEntry.getKey();
			Integer dimension = firstEntry.getValue();
			if (dimension != -1) {
				SGMDArrayVariable var = sxyData.findVariable(name);
				int[] dims = var.getDimensions();
				curLen = dims[dimension];
				curIndices = curPickUpInfo.getIndices();
			}
		}

    	// set to data
    	Iterator<Entry<String, Integer>> entryItr = pickUpDimMap.entrySet().iterator();
    	while (entryItr.hasNext()) {
    		Entry<String, Integer> entry = entryItr.next();
    		String name = entry.getKey();
    		Integer index = entry.getValue();
    		SGMDArrayVariable var = sxyData.findVariable(name);
    		var.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, index);
    	}

    	List<SGMDArrayDataColumnInfo> pickUpXYColList = new ArrayList<SGMDArrayDataColumnInfo>();
    	SGDataColumnInfo[] colsNew = this.getDataColumnInfoArray();
		for (int ii = 0; ii < colsNew.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colsNew[ii];
			String columnType = mdCol.getColumnType();
			if (SGIDataColumnTypeConstants.X_VALUE.equals(columnType)
					|| SGIDataColumnTypeConstants.Y_VALUE.equals(columnType)) {
    			Integer pickUpIndex = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
				if (pickUpIndex != null && pickUpIndex != -1) {
					pickUpXYColList.add(mdCol);
				}
			}
		}
		if (pickUpXYColList.size() == 0) {
			if (sxyData.isDimensionPicked()) {
				// clears picked up information
				sxyData.clearPickUp();
			}
            return true;
		}

		// updates the indices
		SGMDArrayDataColumnInfo pickUpColumn = pickUpXYColList.get(0);
		Integer pickUpIndex = pickUpColumn.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
		final int len = pickUpColumn.getDimensions()[pickUpIndex];
		SGIntegerSeriesSet indices = curIndices;
		if (curLen == -1 || len != curLen) {
			SGIntegerSeries series = SGDataUtility.createDefaultStepSeries(len);
			indices = new SGIntegerSeriesSet(series);
		}

		Map<String, Integer> pickUpDimensionMap = new HashMap<String, Integer>();
		for (SGMDArrayDataColumnInfo col : pickUpXYColList) {
			String name = col.getName();
			Integer dim = col.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			pickUpDimensionMap.put(name, dim);
		}

    	// updates the information of picked up dimension
    	SGMDArrayPickUpDimensionInfo pickUpInfo = new SGMDArrayPickUpDimensionInfo(
    			pickUpDimensionMap, indices);
    	if (sxyData.setPickUpDimensionInfo(pickUpInfo) == false) {
            return false;
    	}
    	
    	if (!this.updateChild()) {
    		return false;
    	}
    	return true;
    }

    private Map<String, Integer> createPickUpDimMap(SGSXYMDArrayMultipleData sxyData, String value) {
    	String[] values = SGUtilityText.getStringsInBracket(value);
    	if (values == null) {
            return null;
    	}
    	if (values.length == 0) {
            return null;
    	}

    	// get current parameters
		SGDataColumnInfo[] cols = sxyData.getColumnInfo();
		Map<String, Integer> curPickUpMap = new HashMap<String, Integer>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			String name = mdCol.getName();
			Integer index = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			if (index != null && index != -1) {
				curPickUpMap.put(name, index);
			}
		}

		// get input values
    	boolean succeeded = true;
		Map<String, Integer> pickUpDimMap = new HashMap<String, Integer>(curPickUpMap);
    	for (int ii = 0; ii < values.length; ii++) {
    		String[] tokens = values[ii].split(":");
    		if (tokens == null || tokens.length != 2) {
    			succeeded = false;
    			break;
    		}
    		String name = tokens[0].trim();
    		Integer dimIndex = SGUtilityText.getInteger(tokens[1].trim());
    		if (dimIndex == null) {
    			succeeded = false;
    			break;
    		}
    		if (dimIndex != -1) {
        		SGMDArrayVariable var = sxyData.findVariable(name);
        		if (var == null) {
        			succeeded = false;
        			break;
        		}
        		final int[] dims = var.getDimensions();
        		if (dimIndex < 0 || dims.length <= dimIndex) {
        			succeeded = false;
        			break;
        		}
        		Integer generic = var.getGenericDimensionIndex();
        		if (dimIndex.equals(generic)) {
        			succeeded = false;
        			break;
        		}
        		Integer time = var.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
        		if (time != null && time != -1) {
        			if (dimIndex.equals(time)) {
            			succeeded = false;
            			break;
        			}
        		}
    		}
    		pickUpDimMap.put(name, dimIndex);
    	}
    	if (!succeeded) {
            return null;
    	}

    	// creates merged map
    	Map<String, Integer> mergedPickUpDimMap = new HashMap<String, Integer>();
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			String name = mdCol.getName();
			Integer index = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
			if (index != null && index != -1) {
				mergedPickUpDimMap.put(name, index);
			}
		}
		mergedPickUpDimMap.putAll(pickUpDimMap);

    	// checks the array length
		int dimLen = -1;
		Iterator<Entry<String, Integer>> pickUpDimItr = mergedPickUpDimMap.entrySet().iterator();
    	while (pickUpDimItr.hasNext()) {
    		Entry<String, Integer> pickUpDimEntry = pickUpDimItr.next();
    		String name = pickUpDimEntry.getKey();
    		Integer dimIndex = pickUpDimEntry.getValue();
    		if (dimIndex == -1) {
    			continue;
    		}
    		SGMDArrayVariable var = sxyData.findVariable(name);
    		final int[] dims = var.getDimensions();
    		if (dimLen == -1) {
    			dimLen = dims[dimIndex];
    		} else {
    			if (dims[dimIndex] != dimLen) {
    				return null;
    			}
    		}
    	}

    	return pickUpDimMap;
    }

    private boolean setPickUpIndices(SGPropertyMap map,
            SGPropertyResults result, final String key, final String value,
            Map<String, SGInteger> pickUpMap) {

    	if (COM_DATA_PICKUP_INDICES.equalsIgnoreCase(key)) {
        	if (map.isDoubleQuoted(COM_DATA_PICKUP_INDICES) == false) {
                result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
        	}
        	if (SGDataUtility.isNetCDFData(this.mData)) {
        		SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) this.mData;
        		SGNetCDFVariable pickUpVar = this.getNetCDFPickUpVariable(null);
        		if (pickUpVar == null) {
                    result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
        		}
        		Dimension dim = pickUpVar.getDimension(0);
        		final int len = dim.getLength();
                SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(value, len);
                if (indices == null) {
                    result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
                }
                SGNetCDFPickUpDimensionInfo info = new SGNetCDFPickUpDimensionInfo(dim.getName(), indices);
            	if (sxyData.setPickUpDimensionInfo(info) == false) {
                    result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
            	}

        	} else if (SGDataUtility.isMDArrayData(this.mData)) {
        		SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) this.mData;
        		if (!sxyData.isDimensionPicked()) {
                    result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
        		}
        		SGMDArrayPickUpDimensionInfo info = (SGMDArrayPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();
        		List<SGMDArrayVariable> pickUpVarList = sxyData.getPickUpMDArrayVariables();
        		final int len = pickUpVarList.get(0).getDimensionLength(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
                SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(value, len);
                if (indices == null) {
                    result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
                }
        		info.setIndices(indices);
            	if (sxyData.setPickUpDimensionInfo(info) == false) {
                    result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
            	}
        	} else {
        		// cannot apply
                result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
        	}
            result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.SUCCEEDED);
        } else if (COM_DATA_PICKUP_START.equalsIgnoreCase(key)) {
        	if (SGDataUtility.isNetCDFData(this.mData)) {
        		Integer num = this.getNetCDFPickUpNumber(map, key, value);
        		if (num == null) {
                    result.putResult(COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
        		}
            	if (num != null) {
            		pickUpMap.put(COM_DATA_PICKUP_START, new SGInteger(num));
            	}
        	} else {
        		// cannot apply
                result.putResult(COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
        	}
        } else if (COM_DATA_PICKUP_END.equalsIgnoreCase(key)) {
        	if (SGDataUtility.isNetCDFData(this.mData)) {
        		Integer num = this.getNetCDFPickUpNumber(map, key, value);
        		if (num == null) {
                    result.putResult(COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
        		}
            	if (num != null) {
            		pickUpMap.put(COM_DATA_PICKUP_END, new SGInteger(num));
            	}
        	} else {
        		// cannot apply
                result.putResult(COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
                return false;
        	}
        } else if (COM_DATA_PICKUP_STEP.equalsIgnoreCase(key)) {
        	if (SGDataUtility.isNetCDFData(this.mData)) {
        		Integer num = this.getNetCDFPickUpNumber(map, key, value);
        		if (num == null) {
                    result.putResult(COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
                    return false;
        		}
            	if (num != null) {
            		pickUpMap.put(COM_DATA_PICKUP_STEP, new SGInteger(num));
            	}
        	} else {
        		// cannot apply
                result.putResult(COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
        		return false;
        	}
        }

    	return true;
    }

    private boolean setTickLabelStride(SGPropertyResults result, String value) {
    	SGISXYTypeMultipleData sxyData = (SGISXYTypeMultipleData) this.mData;
    	final int len = sxyData.getAllPointsNumber();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
    	if (stride == null) {
            return false;
    	}
    	if (this.setTickLabelStride(stride) == false) {
            return false;
    	}
        return true;
    }

    private boolean setIndexStride(String value) {
    	SGData data = this.getData();
    	if (!(data instanceof SGSXYNetCDFMultipleData)) {
    		return false;
    	}
    	SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
		if (!ncData.isIndexAvailable()) {
			return false;
		}
        return this.setNetCDFIndexStride(value);
    }

    /**
     * Creates drawing elements of error bars.
     *
     * @param dataXY
     *             XY type data
     * @return the group of error bars
     */
	public SGElementGroupErrorBar createErrorBars(SGISXYTypeMultipleData dataXY) {
        final SGElementGroupErrorBarInGraph group = new SGElementGroupErrorBarInGraph(
                dataXY, this.mGraph);
        this.mDrawingElementGroupList.add(group);
        group.mGroupSet = this;
        return group;
	}

    /**
     * Creates drawing elements of tick labels.
     *
     * @param dataXY
     *             XY type data
     * @return the group of tick labels
     */
	public SGElementGroupTickLabel createTickLabels(SGISXYTypeMultipleData dataXY) {
        final SGElementGroupTickLabelInGraphSXY group = new SGElementGroupTickLabelInGraphSXY(
                dataXY, this.mGraph);
        this.mDrawingElementGroupList.add(group);
        group.mGroupSet = this;
        return group;
	}

    /**
     * Sets the information of data columns.
     * This method is overridden to update the child data objects.
     *
     * @param columns
     *                information of data columns
     * @return true if succeeded
     */
    public boolean setColumnInfo(SGDataColumnInfo[] columns, String message) {
    	if (!super.setColumnInfo(columns, message)) {
    		return false;
    	}
    	if (SGMultipleSXYUtility.setColumnInfo(this, this.mGraph) == false) {
    		return false;
    	}
    	return true;
    }

    /**
     * Update drawing elements with related data object.
     * @return
     *         true if succeeded
     */
    public boolean updateWithData() {
        this.updateTickLabelStrings();
        return super.updateWithData();
    }

    /**
     * Update the text strings of tick labels.
     */
    public void updateTickLabelStrings() {
        SGData data = this.getData();
        SGISXYTypeMultipleData dataSXY = (SGISXYTypeMultipleData) data;
        if (dataSXY.isTickLabelAvailable()) {
            SGElementGroupTickLabel tg = this.getTickLabelGroup();

            // set decimal places and exponent
            final int dp = tg.getDecimalPlaces();
            final int exp = tg.getExponent();
            final String dateFormat = tg.getDateFormat();

        	// set the decimal places and the exponent to the data object
            dataSXY.setDecimalPlaces(dp);
            dataSXY.setExponent(exp);
            dataSXY.setDateFormat(dateFormat);
            for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
            	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
            	gs.setTickLabelDecimalPlaces(dp);
            	gs.setTickLabelExponent(exp);
            	gs.setTickLabelDateFormat(dateFormat);
            	gs.updateTickLabelStrings();
            }
        }
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
            if (gp instanceof SGElementGroupLine.LineProperties) {
                group = this.getLineGroup();
            } else if (gp instanceof SGElementGroupSymbol.SymbolProperties) {
                group = this.getSymbolGroup();
            } else if (gp instanceof SGElementGroupBar.BarProperties) {
                group = this.getBarGroup();
            } else if (gp instanceof SGElementGroupErrorBar.ErrorBarProperties) {
                group = this.getErrorBarGroup();
            } else if (gp instanceof SGElementGroupString.StringProperties) {
                group = this.getTickLabelGroup();
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

    @Override
    public double getBarOffsetX() {
        return this.getBarGroup().getOffsetX();
    }

    @Override
    public double getBarOffsetY() {
        return this.getBarGroup().getOffsetY();
    }

    @Override
    public boolean setBarOffsetX(final double shift) {
        return SGMultipleSXYUtility.setBarOffsetX(this.getBarGroupsIgnoreNull(), shift);
    }

    @Override
    public boolean setBarOffsetY(final double shift) {
        return SGMultipleSXYUtility.setBarOffsetY(this.getBarGroupsIgnoreNull(), shift);
    }

    @Override
    public Double getShiftMinValue(SGAxis axis) {
        Double shift = null;
        if (axis.equals(this.getXAxis())) {
            if (this.isLineVisible() || this.isSymbolVisible()) {
                shift = Double.valueOf(this.getShiftX());
            }
            if (this.isBarVisible()) {
                // minimum left shift size of bars.
                // If this groupset has multiple bar series, minimum shift means shiftx point minus
                // bar widths of series which are enabled to be visible.
                double barLeftShift = this.getBarOffsetX();
                if (this.isBarVertical()) {
                    int numberSeries = this.mGraph.getNumberOfVisibleBarSeries(this);
                    barLeftShift = barLeftShift -this.getBarInterval()*(numberSeries*0.5)-this.getBarWidthValue();
                }
                if (null==shift || shift.doubleValue()>barLeftShift) {
                    shift = Double.valueOf(barLeftShift);
                }
            }
        } else if (axis.equals(this.getYAxis())) {
            if (this.isLineVisible() || this.isSymbolVisible()) {
                shift = Double.valueOf(this.getShiftY());
            }
            if (this.isBarVisible()) {
                double barBottomShift = this.getBarOffsetY();
                if (this.isBarVertical()==false) {
                    int numberSeries = this.mGraph.getNumberOfVisibleBarSeries(this);
                    barBottomShift = barBottomShift-this.getBarInterval()*(numberSeries*0.5)-this.getBarWidthValue();
                }
                if (null==shift || shift.doubleValue()>barBottomShift) {
                    shift = Double.valueOf(barBottomShift);
                }
            }
        }
        return shift;
    }

    @Override
    public Double getShiftMaxValue(SGAxis axis) {
        Double shift = null;
        if (axis.equals(this.getXAxis())) {
            if (this.isLineVisible() || this.isSymbolVisible()) {
                shift = Double.valueOf(this.getShiftX());
            }
            if (this.isBarVisible()) {
                // maximum right shift size of bars.
                // If this groupset has multiple bar series, maximum shift means shiftx point plus
                // bar widths of series which are enabled to be visible.
                double barRightShift = this.getBarOffsetX();
                if (this.isBarVertical()) {
                    int numberSeries = this.mGraph.getNumberOfVisibleBarSeries(this);
                    barRightShift = barRightShift + this.getBarInterval()*(numberSeries*0.5)+this.getBarWidthValue();
                }
                if (null==shift || shift.doubleValue()<barRightShift) {
                    shift = Double.valueOf(barRightShift);
                }
            }
        } else if (axis.equals(this.getYAxis())) {
            if (this.isLineVisible() || this.isSymbolVisible()) {
                shift = Double.valueOf(this.getShiftY());
            }
            if (this.isBarVisible()) {
                double barTopShift = this.getBarOffsetY();
                if (this.isBarVertical()==false) {
                    int numberSeries = this.mGraph.getNumberOfVisibleBarSeries(this);
                    barTopShift = barTopShift+this.getBarInterval()*(numberSeries*0.5)+this.getBarWidthValue();
                }
                if (null==shift || shift.doubleValue()<barTopShift) {
                    shift = Double.valueOf(barTopShift);
                }
            }
        }
        return shift;
    }

    @Override
    public Double getBarBaselineForAxis(final SGAxis axis) {
        if (this.isBarVisible()==false) {
            return null;
        }
        if (axis.equals(this.getXAxis())) {
            if (this.isBarVertical()) {
                return null;
            } else {
                return Double.valueOf(this.getBarBaselineValue());
            }
        } else if (axis.equals(this.getYAxis())) {
            if (this.isBarVertical()) {
                return Double.valueOf(this.getBarBaselineValue());
            } else {
                return null;
            }
        }
        return null;
    }

	/**
	 * Sets the direction of error bars.
	 *
	 * @param vertical
	 *           true to set vertical
	 * @return true if succeeded
	 */
	public boolean setErrorBarDirection(final boolean vertical) {
    	SGIElementGroupSetForData[] childArray = this.getChildGroupSetArray();
    	for (int ii = 0; ii < childArray.length; ii++) {
    		SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) childArray[ii];
    		gs.setErrorBarDirection(vertical);
    	}
    	List<SGElementGroupErrorBar> groupList = this.getErrorBarGroups();
    	for (int ii = 0; ii < groupList.size(); ii++) {
    		SGElementGroupErrorBar group = groupList.get(ii);
    		group.setVertical(vertical);
    	}
		return true;
	}

	/**
	 * Sets the alignment of tick label.
	 *
	 * @param horizontal
	 *           true to align horizontally
	 * @return true if succeeded
	 */
	public boolean setTickLabelAlignment(final boolean horizontal) {
    	SGIElementGroupSetForData[] childArray = this.getChildGroupSetArray();
    	for (int ii = 0; ii < childArray.length; ii++) {
    		SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) childArray[ii];
    		gs.setTickLabelAlignment(horizontal);
    	}
    	List<SGElementGroupTickLabel> groupList = this.getTickLabelGroups();
    	for (int ii = 0; ii < groupList.size(); ii++) {
    		SGElementGroupTickLabel group = groupList.get(ii);
    		group.setHorizontalAlignment(horizontal);
    	}
		return true;
	}

	// Axis values at the point on which the mouse button is pressed.
	private SGTuple2d mValueAtPressedPoint = new SGTuple2d();

	// The shift value of lines.
	private SGTuple2d mShift = new SGTuple2d();

    /**
     * Called when the mouse button is pressed.
     *
     * @param e
     *          the mouse event
     * @return true if this group set is pressed or false otherwise
     */
    protected boolean onMousePressed(final MouseEvent e) {
    	if (super.onMousePressed(e)) {
    		// records the pressed point
    		this.recordPresentPoint(e.getX(), e.getY());
    		return true;
    	}
    	return false;
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
		final double xValue = this.mGraph.calcValue(x, this.getXAxis(), true);
		final double yValue = this.mGraph.calcValue(y, this.getYAxis(), false);
		this.mValueAtPressedPoint.setValues(xValue, yValue);
		this.updateShiftValueAttributes();
    }

    /**
     * Called when the mouse button is released.
     *
     * @param e
     *          the mouse event
     * @return true if succeeded
     */
    protected boolean onMouseReleased(final MouseEvent e) {
    	if (super.onMouseReleased(e) == false) {
    		return false;
    	}

		this.recordPresentPoint(e.getX(), e.getY());

    	final SGAxis xAxis = this.getXAxis();
    	final SGAxis yAxis = this.getYAxis();

    	// update shift value of the lines
        final double lineShiftX = SGUtilityNumber.getNumberInRangeOrder(this.getShiftX(), xAxis);
        final double lineShiftY = SGUtilityNumber.getNumberInRangeOrder(this.getShiftY(), yAxis);
		this.setShiftX(lineShiftX);
		this.setShiftY(lineShiftY);

    	// update shift value of the bars
        final double barShiftX = SGUtilityNumber.getNumberInRangeOrder(this.getBarOffsetX(), xAxis);
        final double barShiftY = SGUtilityNumber.getNumberInRangeOrder(this.getBarOffsetY(), yAxis);
        this.setBarOffsetX(barShiftX);
        this.setBarOffsetY(barShiftY);

        return true;
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
		if (this.mAnchorFlag) {
			return;
		}

		// calculate the current values
		final double curX = this.mGraph.calcValue(x, this.getXAxis(), true);
		final double curY = this.mGraph.calcValue(y, this.getYAxis(), false);;

		// calculate shift values
		final double shiftChangeX = curX - this.mValueAtPressedPoint.x;
		final double shiftChangeY = curY - this.mValueAtPressedPoint.y;
    	if (Double.isNaN(shiftChangeX) || Double.isNaN(shiftChangeY)) {
    		return;
    	}

		// updates the change of the shift values
		this.updateShiftValues(shiftChangeX, shiftChangeY);

		// set changed flag
		if (shiftChangeX != 0.0f || shiftChangeY != 0.0f) {
			this.setChanged(true);
		}

		// update the location of drawing elements
		this.mGraph.updateAllDrawingElementsLocation();
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
		if (this.mAnchorFlag) {
			return;
		}

		if (this.equals(this.mGraph.mPressedGroupSet)) {
			return;
		}

		// initialize the attributes of shift values
		this.updateShiftValueAttributes();

		// get the shift of values at the center of graph rectangle
		final SGAxis xAxis = this.getXAxis();
		final SGAxis yAxis = this.getYAxis();
		final int xScaleType = xAxis.getScaleType();
		final int yScaleType = yAxis.getScaleType();
		SGISXYTypeMultipleData dataSXY = (SGISXYTypeMultipleData) this.getData();
		double shiftChangeX;
		double shiftChangeY;
		if (xScaleType == SGAxis.LINEAR_SCALE) {
			shiftChangeX = this.calcLinearValueChange(xAxis, dx, true);
			if (xAxis.isInvertCoordinates()) {
				shiftChangeX *= -1;
			}
		} else if (xScaleType == SGAxis.LOG_SCALE) {
			final SGValueRange bounds = dataSXY.getBoundsX();
			shiftChangeX = this.calcLogValueChange(xAxis, bounds,
					this.getShiftX(), dx, true);
		} else {
			throw new Error("Invalid scale type: " + xScaleType);
		}
		if (yScaleType == SGAxis.LINEAR_SCALE) {
			shiftChangeY = - this.calcLinearValueChange(yAxis, dy, false);
			if (yAxis.isInvertCoordinates()) {
				shiftChangeY *= -1;
			}
		} else if (yScaleType == SGAxis.LOG_SCALE) {
			final SGValueRange bounds = dataSXY.getBoundsY();
			shiftChangeY = this.calcLogValueChange(yAxis, bounds,
					this.getShiftY(), dy, false);
		} else {
			throw new Error("Invalid scale type: " + yScaleType);
		}
    	if (Double.isNaN(shiftChangeX) || Double.isNaN(shiftChangeY)) {
    		return;
    	}

		// updates the change of the shift values
		this.updateShiftValues(shiftChangeX, shiftChangeY);

		// set changed flag
		if (shiftChangeX != 0.0f || shiftChangeY != 0.0f) {
			this.setChanged(true);
		}

		// update the location of drawing elements
		this.mGraph.updateAllDrawingElementsLocation();

		// update the attributes of shift values
		this.updateShiftValueAttributes();
	}

	private void updateShiftValueAttributes() {
		this.mShift.setValues(this.getShiftX(), this.getShiftY());
	}

	private double calcLinearValueChange(final SGAxis axis, final float d, final boolean horizontal) {
		final Rectangle2D rect = this.mGraph.getGraphRect();
		final double size = horizontal ? rect.getWidth() : rect.getHeight();
		final double min = axis.getMinDoubleValue();
		final double max = axis.getMaxDoubleValue();
		return (max - min) * (d / size);
	}

	private double calcLogValueChange(final SGAxis axis, final SGValueRange range, final double shift,
			final float d, final boolean horizontal) {
		final double minValue = SGUtilityNumber.max(axis.getMinDoubleValue(),
				range.getMinValue() + shift);
		final double maxValue = SGUtilityNumber.min(axis.getMaxDoubleValue(),
				range.getMaxValue() + shift);
		final double midValue = Math.exp((Math.log(minValue) + Math.log(maxValue)) / 2);
		final float pos = this.mGraph.calcLocation(midValue, axis, horizontal);
		final float dHalf = d / 2;
		final double v1 = this.mGraph.calcValue(pos - dHalf, axis, horizontal);
		final double v2 = this.mGraph.calcValue(pos + dHalf, axis, horizontal);
		return v2 - v1;
	}

    // Updates the shift values.
    private void updateShiftValues(final double shiftChangeX, final double shiftChangeY) {
		final double shiftX = this.mShift.x + shiftChangeX;
		final double shiftY = this.mShift.y + shiftChangeY;
		this.setShiftX(shiftX);
		this.setShiftY(shiftY);
    }

    @Override
    public SGProperties getProperties() {
    	MultipleSXYElementGroupSetPropertiesInFigureElement p = new MultipleSXYElementGroupSetPropertiesInFigureElement();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    public boolean getProperties(SGProperties p) {
        if ((p instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        MultipleSXYElementGroupSetPropertiesInFigureElement mp = (MultipleSXYElementGroupSetPropertiesInFigureElement) p;
        mp.shiftX = this.getShiftX();
        mp.shiftY = this.getShiftY();
        mp.lineColorAutoAssigned = this.isLineColorAutoAssigned();
        mp.lineColorMapName = this.getLineColorMapName();
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
            mp.childPropertyList.add(gs.getProperties());
            mp.childLineStyleList.add(gs.getLineStyle());
        }
        Map<String, SGColorMap> colorMaps = this.mLineColorMapManager.getColorMaps();
        Iterator<Entry<String, SGColorMap>> itr = colorMaps.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<String, SGColorMap> entry = itr.next();
        	String name = entry.getKey();
            SGColorMap colorMap = entry.getValue();
            mp.colorMapPropertiesMap.put(name, (ColorMapProperties) colorMap.getProperties());
        }
        return true;
    }

    /**
     *
     */
    public boolean setProperties(SGProperties p) {
        if ((p instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }

    	// disposes of old data
    	if (SGMultipleSXYUtility.removeAllChildData(this) == false) {
    	    return false;
    	}

    	// synchronize the child group set
    	if (this.mGraph.updateChildGroupSet(this) == false) {
    	    return false;
    	}

    	MultipleSXYElementGroupSetPropertiesInFigureElement mp = (MultipleSXYElementGroupSetPropertiesInFigureElement) p;
        this.setShiftX(mp.shiftX);
        this.setShiftY(mp.shiftY);
        this.setLineColorAutoAssigned(mp.lineColorAutoAssigned);
        this.setLineColorMapName(mp.lineColorMapName);

        Iterator<Entry<String, ColorMapProperties>> itr = mp.colorMapPropertiesMap.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<String, ColorMapProperties> entry = itr.next();
        	String name = entry.getKey();
        	ColorMapProperties colorMapProperties = entry.getValue();
        	SGColorMap colorMap = this.mLineColorMapManager.getColorMap(name);
        	if (!colorMap.setProperties(colorMapProperties)) {
        		return false;
        	}
        }
        
        int count = 0;
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
            if (!gs.setProperties(mp.childPropertyList.get(count))) {
                return false;
            }
            count++;
            if (count >= mp.childPropertyList.size()) {
                count = mp.childPropertyList.size() - 1;
            }
        }
        count = 0;
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
            gs.setLineStyle(mp.childLineStyleList.get(count));
            count++;
            if (count >= mp.childLineStyleList.size()) {
                count = mp.childLineStyleList.size() - 1;
            }
        }
        return true;
    }

    /**
     * Returns the shift value to the x-axis direction.
     *
     * @return the shift value
     */
    public double getShiftX() {
    	return this.mShiftX;
    }

    /**
     * Returns the shift value to the y-axis direction.
     *
     * @return the shift value
     */
    public double getShiftY() {
    	return this.mShiftY;
    }

    /**
     * Sets the shift value to the x-axis direction.
     *
     * @param shift
     *           the shift value
     * @return true if succeeded
     */
    public boolean setShiftX(final double shift) {
        this.mShiftX = shift;
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
        	gs.setShiftX(shift);
        }
        SGISXYTypeData data = (SGISXYTypeData) this.mData;
        SGTuple2d curShift = data.getShift();
        data.setShift(new SGTuple2d(shift, curShift.y));
        return true;
    }

    /**
     * Sets the shift value to the y-axis direction.
     *
     * @param shift
     *           the shift value
     * @return true if succeeded
     */
    public boolean setShiftY(final double shift) {
        this.mShiftY = shift;
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
        	gs.setShiftY(shift);
        }
        SGISXYTypeData data = (SGISXYTypeData) this.mData;
        SGTuple2d curShift = data.getShift();
        data.setShift(new SGTuple2d(curShift.x, shift));
        return true;
    }

    /**
     * Sets the information of picked up dimension.
     *
     * @param info
     *           the information of picked up dimension
     * @return true if succeeded
     */
	@Override
	public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info) {
        SGData data = this.getData();
        if (data instanceof SGISXYMultipleDimensionData) {
        	SGISXYMultipleDimensionData nData = (SGISXYMultipleDimensionData) data;
            return nData.setPickUpDimensionInfo(info);
        } else {
            return false;
        }
	}

    /**
     * Sets the stride.
     *
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
	@Override
    public boolean setStride(SGIntegerSeriesSet stride) {
		return this.setSXYStrideToData(stride);
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
     * Sets the stride of the tick labels.
     *
     * @param stride
     *           stride of arrays
     * @return true if succeeded
     */
	@Override
	public boolean setTickLabelStride(SGIntegerSeriesSet stride) {
		return this.setTickLabelStrideToData(stride);
	}

    private void setPickUpResults(SGPropertyMap map, SGPropertyResults result,
    		SGInteger pickUpStart, SGInteger pickUpEnd, SGInteger pickUpStep) {
		if (this.mData instanceof SGSXYNetCDFMultipleData) {
			SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) this.mData;
        	if (SGIntegerSeries.isValidSeries(pickUpStart, pickUpEnd, pickUpStep)) {
        		SGNetCDFVariable pickUpVar = this.getNetCDFPickUpVariable(null);
        		if (pickUpVar == null) {
            		this.setPickUpResults(map, result, pickUpStart, pickUpEnd, pickUpStep,
            				SGPropertyResults.INVALID_INPUT_VALUE);
        		}
        		Dimension dim = pickUpVar.getDimension(0);
        		final int len = dim.getLength();
        		SGInteger start = this.createInteger(pickUpStart, len);
        		SGInteger end = this.createInteger(pickUpEnd, len);
        		SGInteger step = this.createInteger(pickUpStep, len);
                SGIntegerSeriesSet indices = new SGIntegerSeriesSet(start, end, step);
                SGNetCDFPickUpDimensionInfo info = new SGNetCDFPickUpDimensionInfo(dim.getName(), indices);
            	if (sxyData.setPickUpDimensionInfo(info) == false) {
            		this.setPickUpResults(map, result, pickUpStart, pickUpEnd, pickUpStep,
            				SGPropertyResults.INVALID_INPUT_VALUE);
            	}
        		this.setPickUpResults(map, result, pickUpStart, pickUpEnd, pickUpStep,
        				SGPropertyResults.SUCCEEDED);
        	} else {
        		this.setPickUpResults(map, result, pickUpStart, pickUpEnd, pickUpStep,
        				SGPropertyResults.INVALID_INPUT_VALUE);
        	}
		} else if (this.mData instanceof SGSXYMDArrayMultipleData) {
			// do nothing
			return;
		} else {
    		this.setPickUpResults(map, result, pickUpStart, pickUpEnd, pickUpStep,
    				SGPropertyResults.INVALID_INPUT_VALUE);
		}
    }

    private SGInteger createInteger(SGInteger sNum, final int len) {
    	Integer num = sNum.getNumber();
    	if (num == null) {
    		return null;
    	}
    	final int end = len - 1;
    	if (num.intValue() == end) {
    		return new SGInteger(end, SGIntegerSeries.ARRAY_INDEX_END);
    	} else {
    		return new SGInteger(sNum);
    	}
    }

    private Integer getNetCDFPickUpNumber(SGPropertyMap map, String key, String value) {
		if (!(this.mData instanceof SGSXYNetCDFMultipleData)) {
            return null;
		}
    	SGNetCDFVariable pickUpVar = this.getNetCDFPickUpVariable(null);
    	if (pickUpVar == null) {
    		return null;
    	}
		Integer num = SGUtilityText.getInteger(value);
		if (num == null) {
            return null;
		}
		Dimension dim = pickUpVar.getDimension(0);
		final int len = dim.getLength();
		if (num >= len) {
			return null;
		}
		return num;
    }

	// Finds pickup variable from given column types or current column types of data.
    private SGNetCDFVariable getNetCDFPickUpVariable(String[] columnTypes) {
		SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) this.mData;
		SGNetCDFFile ncfile = sxyData.getNetcdfFile();
		List<SGNetCDFVariable> varList = ncfile.getVariables();
		boolean valid = true;
		SGNetCDFVariable pickUpVar = null;
		String[] colTypes = null;
		if (columnTypes != null) {
			colTypes = columnTypes;
		} else {
			colTypes = sxyData.getCurrentColumnType();
		}
		int cnt = 0;
		for (int ii = 0; ii < varList.size(); ii++) {
			if (SGIDataColumnTypeConstants.PICKUP.equalsIgnoreCase(colTypes[ii])) {
    			SGNetCDFVariable var = varList.get(ii);
    			if (!var.isCoordinateVariable()) {
    				valid = false;
    				break;
    			}
				pickUpVar = var;
				cnt++;
			}
		}
		if (cnt > 1) {
			valid = false;
		}
    	if (!valid) {
    		return null;
    	}
    	return pickUpVar;
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

		// set column types
		SGArrayData aData = (SGArrayData) this.mData;
		SGDataColumnInfo[] preColumnInfo = aData.getColumnInfo();
		try {
	    	if (SGDataUtility.isNetCDFData(this.mData)) {
	    		if (this.setNetCDFColumnType(map, result, cols) == false) {
	    			return false;
	    		}
	    	} else if (SGDataUtility.isMDArrayData(this.mData)) {
	    		if (this.setMDArrayColumnType(map, result, cols) == false) {
	    			return false;
	    		}
	    	}
		} finally {
			// updates the stride with new column types
	    	if (!SGDataUtility.hasEqualInput(preColumnInfo, cols)) {
	    		Map<String, Object> infoMap = new HashMap<String, Object>();
	    		infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, this.mData.getDataType());
	    		infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, new SGTuple2f(
	    				this.mGraph.getGraphRectWidth(), this.mGraph.getGraphRectHeight()));
	    		if (SGDataUtility.isSDArrayData(this.mData)) {
	        		SGSXYSDArrayMultipleData sdData = (SGSXYSDArrayMultipleData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcSDArrayDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        			sdData.setStride(stride);
        			SGIntegerSeriesSet tickLabelStride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        			sdData.setTickLabelStride(tickLabelStride);
	    		} else if (SGDataUtility.isNetCDFData(this.mData)) {
	        		SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcNetCDFDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        			ncData.setStride(stride);
        			SGIntegerSeriesSet tickLabelStride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        			ncData.setTickLabelStride(tickLabelStride);
        			SGIntegerSeriesSet indexStride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
        			ncData.setIndexStride(indexStride);
	        	} else if (SGDataUtility.isMDArrayData(this.mData)) {
	        		SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) this.mData;
	        		Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcMDArrayDefaultStride(cols, infoMap);
        			SGIntegerSeriesSet stride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        			mdData.setStride(stride);
        			SGIntegerSeriesSet tickLabelStride = strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
        			mdData.setTickLabelStride(tickLabelStride);
	        	}
	    	}

	    	// clears time dimension
	    	this.clearTimeDimension();
		}

    	// sets picked up dimension and animation dimension
    	if (SGDataUtility.isMDArrayData(this.mData)) {
        	List<String> keys = map.getKeys();
        	String strPickUpDimension = map.getValueString(COM_DATA_PICKUP_DIMENSION);
        	String strTimeDimension = map.getValueString(COM_DATA_ANIMATION_FRAME_DIMENSION);
            final boolean pickUpDimContained = keys.contains(COM_DATA_PICKUP_DIMENSION.toUpperCase())
            		&& !"".equals(strPickUpDimension);
            final boolean timeDimContained = keys.contains(COM_DATA_ANIMATION_FRAME_DIMENSION.toUpperCase())
            		&& !"".equals(strTimeDimension);
            if (timeDimContained && pickUpDimContained) {
            	this.setPickUpAndTimeDimension(result, strPickUpDimension, strTimeDimension);
            } else {
            	if (pickUpDimContained) {
                	if (!this.setPickUpDimension(strPickUpDimension)) {
                        result.putResult(COM_DATA_PICKUP_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
                	} else {
                        result.putResult(COM_DATA_PICKUP_DIMENSION, SGPropertyResults.SUCCEEDED);
                	}
            	}
            	if (timeDimContained) {
                	if (!this.setTimeDimension(strTimeDimension)) {
                        result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.INVALID_INPUT_VALUE);
                	} else {
                        result.putResult(COM_DATA_ANIMATION_FRAME_DIMENSION, SGPropertyResults.SUCCEEDED);
                	}
            	}
            }
    	}

    	// sets picked up indices
        Map<String, SGInteger> pickUpMap = new HashMap<String, SGInteger>();
    	String strPickUpIndices = map.getValueString(COM_DATA_PICKUP_INDICES);
    	if (!"".equals(strPickUpIndices)) {
    		if (!this.setPickUpIndices(map, result, COM_DATA_PICKUP_INDICES, strPickUpIndices,
    				pickUpMap)) {
        		return false;
        	}
    	}
    	String strPickUpStart = map.getValueString(COM_DATA_PICKUP_START);
    	if (!"".equals(strPickUpStart)) {
    		if (!this.setPickUpIndices(map, result, COM_DATA_PICKUP_START, strPickUpStart,
    				pickUpMap)) {
        		return false;
        	}
    	}
    	String strPickUpEnd = map.getValueString(COM_DATA_PICKUP_END);
    	if (!"".equals(strPickUpEnd)) {
    		if (!this.setPickUpIndices(map, result, COM_DATA_PICKUP_END, strPickUpEnd,
    				pickUpMap)) {
        		return false;
        	}
    	}
    	String strPickUpStep = map.getValueString(COM_DATA_PICKUP_STEP);
    	if (!"".equals(strPickUpStep)) {
    		if (!this.setPickUpIndices(map, result, COM_DATA_PICKUP_STEP, strPickUpStep,
    				pickUpMap)) {
        		return false;
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
			if (COM_DATA_ARRAY_SECTION.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setStride(value) == false) {
                    result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
			} else if (COM_DATA_TICK_LABEL_ARRAY_SECTION.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
        		if (this.setTickLabelStride(result, value) == false) {
                    result.putResult(COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
        		}
                result.putResult(COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
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
			}
		}

    	return true;
    }

	// Sets the column types and picked up dimension.
    private boolean setNetCDFColumnType(SGPropertyMap map, SGPropertyResults result,
    		SGDataColumnInfo[] cols) {

		String[] columnTypes = new String[cols.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = cols[ii].getColumnType();
		}

		SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) this.mData;
        SGNetCDFPickUpDimensionInfo curInfo = (SGNetCDFPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();

		SGNetCDFVariable pickUpVar = this.getNetCDFPickUpVariable(columnTypes);
		if (pickUpVar != null) {
			// pick up variable is assigned

	        SGNetCDFPickUpDimensionInfo newInfo = null;
	        if (curInfo != null) {
        		SGNetCDFFile ncfile = sxyData.getNetcdfFile();
	        	String curDimName = curInfo.getDimensionName();
	        	String pickUpDimName = pickUpVar.getName();
	        	Dimension curDim = ncfile.findDimension(curDimName);
	        	final int curLen = curDim.getLength();
	        	Dimension newDim = ncfile.findDimension(pickUpDimName);
	        	final int newLen = newDim.getLength();
	        	SGIntegerSeriesSet curIndices = curInfo.getIndices();
	        	SGIntegerSeries curSeries = curIndices.testReduce();
        		newInfo = this.getNetCDFPickUpInfo(map, result, pickUpDimName, curLen, curSeries);
        		if (newInfo == null) {
        			SGIntegerSeriesSet indicesNew = new SGIntegerSeriesSet(
        					SGDataUtility.createDefaultStepSeries(newLen));
                	newInfo = new SGNetCDFPickUpDimensionInfo(pickUpDimName, indicesNew);
        		}

	        } else {
	        	// pick up variable is newly-assigned
            	// try to get pick up information from the map
            	Dimension dim = pickUpVar.getDimension(0);
            	final int len = dim.getLength();
            	String strideValue = map.getValueString(COM_DATA_PICKUP_INDICES);
            	if (!"".equals(strideValue)) {
            		SGIntegerSeriesSet indices = null;
            		if (map.isDoubleQuoted(COM_DATA_PICKUP_INDICES)) {
                		indices = SGIntegerSeriesSet.parse(strideValue, len);
            		} else {
                        result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
            		}
            		if (indices == null) {
                        result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
            			indices = new SGIntegerSeriesSet(SGDataUtility.createDefaultStepSeries(len));
            		}
                    newInfo = new SGNetCDFPickUpDimensionInfo(pickUpVar.getName(), indices);
                    result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.SUCCEEDED);
            	} else {
            		newInfo = this.getNetCDFPickUpInfo(map, result, dim.getName(), len, null);
            		if (newInfo == null) {
                		// get default pick up stride
                		SGDataColumnInfo[] infoArray = this.getDataColumnInfoArray();
                		SGNetCDFDataColumnInfo[] nCols = null;
            			SGNetCDFFile ncFile = sxyData.getNetcdfFile();
                		nCols = new SGNetCDFDataColumnInfo[cols.length];
            			for (int ii = 0; ii < nCols.length; ii++) {
            				SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) infoArray[ii];
            				String varName = nCol.getName();
            				SGNetCDFVariable var = ncFile.findVariable(varName);
            				nCols[ii] = new SGNetCDFDataColumnInfo(var, var.getName(), var.getValueType());
            				nCols[ii].setColumnType(columnTypes[ii]);
            			}
                		Map<String, Object> infoMap = new HashMap<String, Object>();
                		SGDataUtility.updatePickupParameters(infoMap, nCols);
                		SGIntegerSeriesSet indices = (SGIntegerSeriesSet) infoMap.get(
                				SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
                        newInfo = new SGNetCDFPickUpDimensionInfo(pickUpVar.getName(), indices);
            		}
            	}
        	}

			// set to data
	    	if (sxyData.setColumnType(columnTypes, newInfo) == false) {
	    		this.setFailedColumnTypeResult(map, result);

	    		// recovers pick up information
	    		sxyData.setPickUpDimensionInfo(curInfo);
	            return false;
	    	}

		} else {
			// pick up variable is not assigned

    		// clears pick up information
    		sxyData.setPickUpDimensionInfo(null);

    		// sets the column types
        	if (sxyData.setColumnType(columnTypes) == false) {
        		this.setFailedColumnTypeResult(map, result);

        		// recovers pick up information
        		sxyData.setPickUpDimensionInfo(curInfo);
                return false;
        	}
		}

        result.putResult(COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);
    	return true;
    }

    private SGNetCDFPickUpDimensionInfo getNetCDFPickUpInfo(SGPropertyMap map, SGPropertyResults result,
    		String dimName, final int len, final SGIntegerSeries curSeries) {
    	SGNetCDFPickUpDimensionInfo info = null;
    	SGInteger start = null;
    	SGInteger end = null;
    	SGInteger step = null;
        List<String> keys = map.getKeys();
        for (String key : keys) {
            String value = map.getValueString(key);
            if (COM_DATA_PICKUP_START.equalsIgnoreCase(key)) {
            	Number num = this.getPickUpNumber(value, len);
            	if (num == null) {
                    result.putResult(COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                start = new SGInteger(num.intValue());
            } else if (COM_DATA_PICKUP_END.equalsIgnoreCase(key)) {
            	Number num = this.getPickUpNumber(value, len);
            	if (num == null) {
                    result.putResult(COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                end = new SGInteger(num.intValue());
            } else if (COM_DATA_PICKUP_STEP.equalsIgnoreCase(key)) {
            	Number num = this.getPickUpNumber(value, len);
            	if (num == null) {
                    result.putResult(COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                step = new SGInteger(num.intValue());
            }
        }
        if (start != null || end != null || step != null) {
        	if (!(start != null && end != null && step != null) && (curSeries == null)) {
            	this.setPickUpResults(map, result, start, end, step, SGPropertyResults.INVALID_INPUT_VALUE);
        	} else {
                final int nStart = (start != null) ? start.getNumber().intValue() : curSeries.getStart().getNumber();
                final int nEnd = (end != null) ? end.getNumber().intValue() : curSeries.getEnd().getNumber();
                final int nStep = (step != null) ? step.getNumber().intValue() : curSeries.getStep().getNumber();
                if (SGIntegerSeriesSet.isValidInput(nStart, nEnd, nStep)) {
                	SGIntegerSeriesSet indicesNew = new SGIntegerSeriesSet(nStart, nEnd, nStep);
                	info = new SGNetCDFPickUpDimensionInfo(
                			dimName, indicesNew);
                	this.setPickUpResults(map, result, start, end, step, SGPropertyResults.SUCCEEDED);
                } else {
                	this.setPickUpResults(map, result, start, end, step, SGPropertyResults.INVALID_INPUT_VALUE);
                }
        	}
        }
        return info;
    }

    private Number getPickUpNumber(String value, final int len) {
        Number num = SGUtilityText.getInteger(value);
        if (num == null) {
        	if (SGIntegerSeries.ARRAY_INDEX_END.equalsIgnoreCase(value)) {
        		if (len == -1) {
            		return null;
        		} else {
            		num = len - 1;
        		}
        	} else {
        		return null;
        	}
        }
        return num;
    }

    private void setPickUpResults(SGPropertyMap map, SGPropertyResults result, SGInteger start,
    		SGInteger end, SGInteger step, final int status) {
    	List<String> keys = map.getKeys();
    	if (start != null && keys.contains(COM_DATA_PICKUP_START.toUpperCase())) {
            result.putResult(COM_DATA_PICKUP_START, status);
    	}
    	if (end != null && keys.contains(COM_DATA_PICKUP_END.toUpperCase())) {
            result.putResult(COM_DATA_PICKUP_END, status);
    	}
    	if (step != null && keys.contains(COM_DATA_PICKUP_STEP.toUpperCase())) {
            result.putResult(COM_DATA_PICKUP_STEP, status);
    	}
    }

	// Sets the column types and picked up dimension.
    private boolean setMDArrayColumnType(SGPropertyMap map, SGPropertyResults result,
    		SGDataColumnInfo[] cols) {

		String[] columnTypes = new String[cols.length];
		for (int ii = 0; ii < columnTypes.length; ii++) {
			columnTypes[ii] = cols[ii].getColumnType();
		}

		SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) this.mData;
        SGMDArrayPickUpDimensionInfo curInfo = (SGMDArrayPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();
    	String strPickUpDimension = map.getValueString(COM_DATA_PICKUP_DIMENSION);
    	Map<String, Integer> pickUpMap = this.createPickUpDimMap(sxyData, strPickUpDimension);

    	if (pickUpMap != null && pickUpMap.size() != 0) {
			// pick up variable is assigned

    		int newLen = -1;
        	Iterator<Entry<String, Integer>> pickUpItr = pickUpMap.entrySet().iterator();
        	while (pickUpItr.hasNext()) {
        		Entry<String, Integer> pickUpEntry = pickUpItr.next();
            	String pickUpVarName = pickUpEntry.getKey();
            	Integer pickUpDim = pickUpEntry.getValue();
            	SGMDArrayVariable newVar = sxyData.findVariable(pickUpVarName);
            	int[] dims = newVar.getDimensions();
            	if (pickUpDim == -1) {
            		continue;
            	}
            	if (pickUpDim < 0 || pickUpDim >= dims.length) {
            		return false;
            	}
        		final int len = dims[pickUpDim];
            	if (newLen != -1) {
            		if (len != newLen) {
            			return false;
            		}
            	}
            	newLen = len;
        	}

	        SGMDArrayPickUpDimensionInfo newInfo = null;
	        if (curInfo != null) {
    			SGIntegerSeriesSet indicesNew = new SGIntegerSeriesSet(
    					SGDataUtility.createDefaultStepSeries(newLen));
            	newInfo = new SGMDArrayPickUpDimensionInfo(pickUpMap, indicesNew);

	        } else {
	    		// pick up variable is newly-assigned
            	// try to get pick up information from the map
            	String strideValue = map.getValueString(COM_DATA_PICKUP_INDICES);
            	if (!"".equals(strideValue)) {
            		SGIntegerSeriesSet indices = null;
            		if (map.isDoubleQuoted(COM_DATA_PICKUP_INDICES)) {
                		indices = SGIntegerSeriesSet.parse(strideValue, newLen);
            		} else {
                        result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
            		}
            		if (indices == null) {
                        result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
            			indices = new SGIntegerSeriesSet(SGDataUtility.createDefaultStepSeries(newLen));
            		}
                    newInfo = new SGMDArrayPickUpDimensionInfo(pickUpMap, indices);
                    result.putResult(COM_DATA_PICKUP_INDICES, SGPropertyResults.SUCCEEDED);
            	} else {
            		// get default pick up stride
            		SGDataColumnInfo[] infoArray = this.getDataColumnInfoArray();
            		SGMDArrayDataColumnInfo[] mdCols = new SGMDArrayDataColumnInfo[cols.length];
        			for (int ii = 0; ii < mdCols.length; ii++) {
        				SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) infoArray[ii];
        				String varName = mdCol.getName();
        				SGMDArrayVariable var = sxyData.findVariable(varName);
        				mdCols[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
        				mdCols[ii].setColumnType(columnTypes[ii]);
        			}
            		Map<String, Object> infoMap = new HashMap<String, Object>();
            		SGDataUtility.updatePickupParameters(infoMap, mdCols);
            		SGIntegerSeriesSet indices = (SGIntegerSeriesSet) infoMap.get(
            				SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
                    newInfo = new SGMDArrayPickUpDimensionInfo(pickUpMap, indices);
            	}
	        }

			// set to data
	    	if (sxyData.setColumnType(cols, newInfo) == false) {
	    		this.setFailedColumnTypeResult(map, result);

	    		// recovers pick up information
	    		sxyData.setPickUpDimensionInfo(curInfo);
	            return false;
	    	}

    	} else {
			// pick up variable is not assigned

    		// sets the column types
        	if (sxyData.setColumnType(cols, null) == false) {
        		this.setFailedColumnTypeResult(map, result);

        		// recovers pick up information
        		sxyData.setPickUpDimensionInfo(curInfo);
                return false;
        	}
    	}

        result.putResult(COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);

    	return true;
    }

    private boolean setStride(String value) {
    	SGISXYTypeData data = (SGISXYTypeData) this.getData();
        if (data instanceof SGSXYSDArrayMultipleData) {
        	return this.setSDArrayStride(value);
        }
        if (data instanceof SGSXYNetCDFMultipleData) {
        	SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
        	if (ncData.isIndexAvailable()) {
        		return false;
        	}
        }
    	final int len = data.getAllPointsNumber();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
    	if (stride == null) {
            return false;
    	}
    	return this.setSXYStrideToData(stride);
    }

    /**
     * A class of properties for multiple data.
     *
     */
    public static class MultipleSXYElementGroupSetPropertiesInFigureElement extends
    		SXYElementGroupSetPropertiesInFigureElement {

        List<SGProperties> childPropertyList = new ArrayList<SGProperties>();

        List<SGLineStyle> childLineStyleList = new ArrayList<SGLineStyle>();
        
        Map<String, ColorMapProperties> colorMapPropertiesMap = new HashMap<String, ColorMapProperties>();
        
        /**
         * Default constructors.
         */
        public MultipleSXYElementGroupSetPropertiesInFigureElement() {
            super();
        }

        public List<SGLineStyle> getLineStyleList() {
        	return new ArrayList<SGLineStyle>(this.childLineStyleList);
        }

        public void setLineStyleList(List<SGLineStyle> styleList) {
        	this.childLineStyleList.clear();
        	this.childLineStyleList.addAll(styleList);
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            MultipleSXYElementGroupSetPropertiesInFigureElement p = (MultipleSXYElementGroupSetPropertiesInFigureElement) obj;
            List<SGProperties> cpList = new ArrayList<SGProperties>();
            for (SGProperties cp : this.childPropertyList) {
            	cpList.add((SGProperties) cp.copy());
            }
            List<SGLineStyle> csList = new ArrayList<SGLineStyle>();
            for (SGLineStyle s : this.childLineStyleList) {
            	csList.add(s);
            }
            Map<String, ColorMapProperties> cpMap = new HashMap<String, ColorMapProperties>();
            Iterator<Entry<String, ColorMapProperties>> itr = this.colorMapPropertiesMap.entrySet().iterator();
            while (itr.hasNext()) {
            	Entry<String, ColorMapProperties> entry = itr.next();
            	String name = entry.getKey();
            	ColorMapProperties cp = entry.getValue();
            	cpMap.put(name, (ColorMapProperties) cp.copy());
            }
            p.childPropertyList = cpList;
            p.childLineStyleList = csList;
            p.colorMapPropertiesMap = cpMap;
            return p;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            this.childPropertyList.clear();
            this.childLineStyleList.clear();
            this.colorMapPropertiesMap.clear();
            this.childPropertyList = null;
            this.childLineStyleList = null;
            this.colorMapPropertiesMap = null;
        }

        /**
         * Returns whether this object is equal to given object.
         *
         * @param obj
         *            an object to be compared
         * @return true if two objects are equal
         */
    	public boolean equals(final Object obj) {
            if ((obj instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            MultipleSXYElementGroupSetPropertiesInFigureElement p = (MultipleSXYElementGroupSetPropertiesInFigureElement) obj;
            if (!p.childPropertyList.equals(this.childPropertyList)) {
            	return false;
            }
            if (!p.childLineStyleList.equals(this.childLineStyleList)) {
            	return false;
            }
            if (!this.colorMapPropertiesMap.equals(p.colorMapPropertiesMap)) {
            	return false;
            }
    		return true;
    	}
    }

    /**
     * Returns the map of line style.
     *
     * @return the map of line style
     */
    @Override
    public Map<Integer, SGLineStyle> getLineStyleMap() {
    	Map<Integer, SGLineStyle> lineStyleMap = new TreeMap<Integer, SGLineStyle>();
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		lineStyleMap.put(gs.getSeriesIndex(), gs.getLineStyle());
    	}
    	return lineStyleMap;
    }

    public Map<Integer, Float> getChildLineWidthMap() {
    	Map<Integer, Float> lineWidthMap = new TreeMap<Integer, Float>();
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		lineWidthMap.put(gs.getSeriesIndex(), gs.getLineWidth());
    	}
    	return lineWidthMap;
    }

    public Map<Integer, Integer> getChildLineTypeMap() {
    	Map<Integer, Integer> lineTypeMap = new TreeMap<Integer, Integer>();
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		lineTypeMap.put(gs.getSeriesIndex(), gs.getLineType());
    	}
    	return lineTypeMap;
    }

    public Map<Integer, Color> getChildLineColorMap() {
    	Map<Integer, Color> lineColorMap = new TreeMap<Integer, Color>();
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		lineColorMap.put(gs.getSeriesIndex(), gs.getLineColor());
    	}
    	return lineColorMap;
    }

    /**
     * Sets the line style to the child data object.
     *
     * @param style
     *          the line style to set
     * @param index
     *          array index of child data object
     * @return true if succeeded
     */
	@Override
    public boolean setLineStyle(final SGLineStyle style, final int index) {
		for (SGIElementGroupSetForData gs: this.mElementGroupSetList) {
			SGElementGroupSetInGraphSXY gsxy = (SGElementGroupSetInGraphSXY) gs;
			if (gsxy.getSeriesIndex() == index) {
	    		gsxy.setLineStyle(style);
	    		break;
			}
		}
		return true;
	}

    /**
     * Sets the line styles to the child data object.
     * 
     * @param styleList
     *           list of line styles
     * @return true if succeeded
     */
	@Override
    public boolean setLineStyle(final List<SGLineStyle> styleList) {
		if (styleList.size() != this.getChildNumber()) {
			return false;
		}
		for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
			SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
			SGLineStyle style = styleList.get(ii);
			gs.setLineStyle(style);
		}
    	return true;
    }

	/**
	 * Sets the style of drawing elements.
	 *
	 * @param styleList
	 *          the list of style
	 * @return true if succeeded
	 */
	@Override
    public boolean setStyle(List<SGStyle> styleList) {
		if (styleList.size() != this.mElementGroupSetList.size()) {
			return false;
		}
		Map<Integer, SGLineStyle> prevMap = this.getLineStyleMap();
		for (int ii = 0; ii < styleList.size(); ii++) {
			SGStyle s = styleList.get(ii);
			if (!(s instanceof SGLineStyle)) {
				return false;
			}
			SGLineStyle lineStyle = (SGLineStyle) s;
			if (!this.setLineStyle(lineStyle, ii)) {
				return false;
			}
			SGLineStyle prev = prevMap.get(ii);
			if (!lineStyle.equals(prev)) {
				this.setChanged(true);
			}
		}
		this.mGraph.notifyChangeOnCommit();
    	return true;
    }

	/**
	 * Sets the line style.
	 * 
	 * @param lineStyle
	 *           the line style
	 * @return true if succeeded
	 */
    public boolean setLineStyle(SGLineStyle lineStyle) {
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		gs.setLineStyle(lineStyle);
    	}
    	return true;
    }

	/**
	 * Returns the style of drawing elements.
	 *
	 * @return the list of style
	 */
	@Override
    public List<SGStyle> getStyle() {
		Map<Integer, SGLineStyle> styleMap = this.getLineStyleMap();
		return new ArrayList<SGStyle>(styleMap.values());
    }
	
	private boolean mLineColorAutoAssigned = false;

    /**
     * Returns whether line color is automatically assigned.
     * 
     * @return true if line color is automatically assigned
     */
	@Override
    public boolean isLineColorAutoAssigned() {
		return this.mLineColorAutoAssigned;
	}

    /**
     * Sets whether line color is automatically assigned
     * 
     * @param b
     *           a flag whether line color is automatically assigned
     */
	@Override
    public void setLineColorAutoAssigned(final boolean b) {
		this.mLineColorAutoAssigned = b;
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
        	gs.setLineColorAutoAssigned(b);
        }
	}

    /**
     * Returns the color map manager for lines.
     * 
     * @return the color map manager for lines
     */
	@Override
    public SGColorMapManager getLineColorMapManager() {
    	return this.mLineColorMapManager;
    }

    /**
     * Returns the color map for lines.
     * 
     * @return the color map for lines
     */
	@Override
    public SGColorMap getLineColorMap() {
		return this.mLineColorMapManager.getColorMap(this.mLineColorMapName);
	}

    /**
     * Returns the name of the color map for lines.
     * 
     * @return the name of the color map for lines
     */
	@Override
    public String getLineColorMapName() {
    	return this.mLineColorMapName;
    }

    /**
     * Sets the color bar style.
     * 
     * @param style
     *           the color bar style
	 * @return true if succeeded
     */
	@Override
    public boolean setLineColorMapName(final String name) {
        SGColorMap model = this.mLineColorMapManager.getColorMap(name);
        if (model == null) {
        	return false;
        }
        this.mLineColorMapName = name;
        return true;
    }

    /**
     * Returns a map of the properties of line color maps.
     * 
     * @return a map of the properties of line color maps
     */
	@Override
    public Map<String, SGProperties> getLineColorMapProperties() {
		return this.mLineColorMapManager.getColorMapProperties();
    }

    /**
     * Returns the list of child objects.
     * 
     * @return the list of child objects
     */
	@Override
    public List<String> getChildNameList() {
		SGISXYTypeMultipleData data = (SGISXYTypeMultipleData) this.mData;
		return data.getChildNameList();
	}

    /**
     * Sets the properties of line color map.
     * 
     * @param colorMapProperties
     *           the map of properties of color maps
     * @return true if succeeded
     */
	@Override
    public boolean setLineColorMapProperties(Map<String, SGProperties> colorMapProperties) {
    	return this.mLineColorMapManager.setColorMapProperties(colorMapProperties);
    }

	@Override
	public boolean initLineStyle(Element el) {
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
            SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
            SGElementGroupLine lineGroup = gs.getLineGroup();
            if (el != null) {
                if (SGElementGroupLine.readStyleProperty(el, lineGroup, ii) == false) {
                	return false;
                }
            }
            SGLineStyle lineStyle = lineGroup.getLineStyle();
            gs.setLineStyle(lineStyle);
        }
        return true;
	}

	@Override
    public boolean readColorMap(final Element el) {
		return this.mLineColorMapManager.readProperty(el);
    }

    /**
     * Returns the line style at given index.
     * 
     * @param index
     *           index of child object
     * @return true if succeeded
     */
	@Override
    public SGLineStyle getLineStyle(final int index) {
    	if (index < 0 || index >= this.getChildNumber()) {
    		throw new IllegalArgumentException("Index out of bounds: " + index);
    	}
        SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(index);
    	return (SGLineStyle) gs.getLineStyle().clone();
    }

    /**
     * Sets the properties of a child data object.
     * 
     * @param map
     *           a map of properties
     * @param childId
     *           ID of a child data object
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map, final int childId) {

        SGElementGroupSetInGraphSXY child = null;
        for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
        	SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
        	if (gs.getID() == childId) {
        		child = gs;
        		break;
        	}
        }
        if (child == null) {
        	return null;
        }
        
        // prepare
        if (this.prepare() == false) {
            return null;
        }

        SGPropertyResults iResult = new SGPropertyResults();
        SGPropertyResults result = child.setProperties(map, iResult);
        
        final int childIndex = child.getSeriesIndex();

        Iterator<String> itr = map.getKeyIterator();
		while (itr.hasNext()) {
			String key = itr.next();
			String value = map.getValueString(key);
            if (COM_DATA_LINE_WIDTH.equalsIgnoreCase(key)) {
                StringBuffer unit = new StringBuffer();
                Number num = SGUtilityText.getNumber(value, unit);
                if (num == null) {
                    result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGMultipleSXYUtility.setLineWidth(this, num.floatValue(), unit
                        .toString(), childIndex) == false) {
                    result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_LINE_WIDTH, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_LINE_TYPE.equalsIgnoreCase(key)) {
                Integer type = SGDrawingElementLine.getLineTypeFromName(value);
                if (type == null) {
                    result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (SGMultipleSXYUtility.setLineType(this, type, childIndex) == false) {
                    result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_LINE_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_LINE_COLOR.equalsIgnoreCase(key)) {
                Color cl = SGUtilityText.getColor(value);
                if (cl != null) {
                    if (SGMultipleSXYUtility.setLineColor(this, cl, childIndex) == false) {
                        result.putResult(COM_DATA_LINE_COLOR, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                    }
                } else {
                	cl = SGUtilityText.parseColor(value);
    				if (cl != null) {
        				if (SGMultipleSXYUtility.setLineColor(this, cl, childIndex) == false) {
        					result.putResult(COM_DATA_LINE_COLOR,
        							SGPropertyResults.INVALID_INPUT_VALUE);
        					continue;
        				}
    				} else {
    					result.putResult(COM_DATA_LINE_COLOR,
    							SGPropertyResults.INVALID_INPUT_VALUE);
    					continue;
    				}
                }
                result.putResult(COM_DATA_LINE_COLOR, SGPropertyResults.SUCCEEDED);
            }
		}
		
        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.notifyToRoot();

        return result;
    }

    /**
     * Sets the properties of color map.
     * 
     * @param colorMapName
     *           the name of color map
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
	@Override
    public SGPropertyResults setColorMapProperties(final String colorMapName, SGPropertyMap map) {
		SGColorMap colorMap = this.mLineColorMapManager.getColorMap(colorMapName);
		if (colorMap == null) {
			return null;
		}
		
        // prepare
        if (this.prepare() == false) {
            return null;
        }

        SGPropertyResults result = colorMap.setProperties(map);

		// assigns colors
        final List<SGLineStyle> lineStyleList = SGUtilityForFigureElement.createLineStyleList(
        		colorMap, this.getChildNumber());
        if (this.setLineStyle(lineStyleList) == false) {
        	return null;
        }

        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.notifyToRoot();
        
        return result;
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
    protected boolean setDataColumnType(String value, SGPropertyMap map, SGPropertyResults result) {
    	if (!super.setDataColumnType(value, map, result)) {
    		return false;
    	}
    	if (!this.updateChild()) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Updates the child objects.
     * 
     * @return true if succeeded
     */
    @Override
    public boolean updateChild() {
    	if (!this.mGraph.updateChildGroupSet(this)) {
    		return false;
    	}
    	if (!this.initChildLineStyle()) {
    		return false;
    	}
    	return true;
    }
    
    /**
     * Returns true if data is shifted.
     * 
     * @return true if data is shifted
     */
    @Override
    public boolean isDataShifted() {
    	return !this.mShift.isZero();
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

    	SGData data = this.getData();
		if (SGDataUtility.isSDArrayData(data)) {
			SGSXYSDArrayMultipleData sxyData = (SGSXYSDArrayMultipleData) data;
			Double samplingRate = sxyData.getSamplingRate();
			if (samplingRate != null) {
				SGPropertyUtility.addProperty(map, COM_DATA_SAMPLING_RATE, samplingRate);
			}
		}
		
        // axes
        SGPropertyUtility.addProperty(map, COM_DATA_AXIS_X, this.getXAxisString());
        SGPropertyUtility.addProperty(map, COM_DATA_AXIS_Y, this.getYAxisString());

        // shift
        SGPropertyUtility.addProperty(map, COM_DATA_SHIFT_X, this.getShiftX());
        SGPropertyUtility.addProperty(map, COM_DATA_SHIFT_Y, this.getShiftY());
        
    	if (SGDataUtility.isMDArrayData(data)) {
    		SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
    		
    		// pick up dimension
        	String pickUpStr = mdData.getPickUpDimensionCommandString();
        	if (pickUpStr != null) {
        		SGPropertyUtility.addProperty(map, COM_DATA_PICKUP_DIMENSION, pickUpStr);
        	}
    	}

        // pick up indices
        SGPickUpDimensionInfo pickUpInfo = null;
        if (SGDataUtility.isNetCDFData(data)) {
        	SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) data;
        	pickUpInfo = sxyData.getPickUpDimensionInfo();
        } else if (SGDataUtility.isMDArrayData(data)) {
        	SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) data;
        	pickUpInfo = sxyData.getPickUpDimensionInfo();
        }
        if (pickUpInfo != null) {
        	SGIntegerSeriesSet pickUpIndices = pickUpInfo.getIndices();
        	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_PICKUP_INDICES, 
        			pickUpIndices.toString());
        }
        
        // drawing elements
        Float lineWidthCommon = this.getCommonLineWidth();
        if (lineWidthCommon != null) {
        	final float lineWidthCommonUnit = (float) SGUtilityText.convertFromPoint(
        			lineWidthCommon, LINE_WIDTH_UNIT);
        	SGPropertyUtility.addProperty(map, COM_DATA_LINE_WIDTH, lineWidthCommonUnit, LINE_WIDTH_UNIT);
        }
        Integer lineTypeCommon = this.getCommonLineType();
        if (lineTypeCommon != null) {
        	SGPropertyUtility.addProperty(map, COM_DATA_LINE_TYPE, 
        			SGDrawingElementLine.getLineTypeName(lineTypeCommon));
        }
        Color lineColorCommon = this.getCommonLineColor();
        if (lineColorCommon != null) {
        	SGPropertyUtility.addProperty(map, COM_DATA_LINE_COLOR, lineColorCommon);
        }
        SGElementGroupLineForData lineGroup = (SGElementGroupLineForData) this.getLineGroup();
        lineGroup.getProperties(map);
        SGElementGroupSymbolForData symbolGroup = (SGElementGroupSymbolForData) this.getSymbolGroup();
        symbolGroup.getProperties(map);
        SGElementGroupBarForData barGroup = (SGElementGroupBarForData) this.getBarGroup();
        barGroup.getProperties(map);
        if (this.isErrorBarAvailable()) {
        	SGElementGroupErrorBarForData errorBarGroup = (SGElementGroupErrorBarForData) this.getErrorBarGroup();
        	errorBarGroup.getProperties(map);
        }
        if (this.isTickLabelAvailable()) {
        	SGElementGroupTickLabelForData tickLabelGroup = (SGElementGroupTickLabelForData) this.getTickLabelGroup();
        	tickLabelGroup.getProperties(map);
        }
        
    	return map;
    }
	
	private Float getCommonLineWidth() {
        Map<Integer, Float> lineWidthMap = this.getChildLineWidthMap();
        Set<Float> lineWidthSet = new HashSet<Float>(lineWidthMap.values());
        List<Float> lineWidthList = new ArrayList<Float>(lineWidthSet);
        final Float lineWidthCommon = (lineWidthList.size() == 1) ? lineWidthList.get(0) : null;
        return lineWidthCommon;
	}
	
	private Integer getCommonLineType() {
        Map<Integer, Integer> lineTypeMap = this.getChildLineTypeMap();
        Set<Integer> lineTypeSet = new HashSet<Integer>(lineTypeMap.values());
        List<Integer> lineTypeList = new ArrayList<Integer>(lineTypeSet);
        final Integer lineTypeCommon = (lineTypeList.size() == 1) ? lineTypeList.get(0) : null;
        return lineTypeCommon;
	}
	
	private Color getCommonLineColor() {
        Map<Integer, Color> lineColorMap = this.getChildLineColorMap();
        Set<Color> lineColorSet = new HashSet<Color>(lineColorMap.values());
        List<Color> lineColorList = new ArrayList<Color>(lineColorSet);
        final Color lineColorCommon = (lineColorList.size() == 1) ? lineColorList.get(0) : null;
        return lineColorCommon;
	}

	/**
	 * Overrode for line color map and child properties.
	 */
	@Override
    public String createCommandString(SGExportParameter params) {
    	String spResult = super.createCommandString(params);
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append(spResult);
    	
    	// child properties
        Float lineWidthCommon = this.getCommonLineWidth();
        Integer lineTypeCommon = this.getCommonLineType();
        Color lineColorCommon = this.getCommonLineColor();
        if (lineWidthCommon == null || lineTypeCommon == null || lineColorCommon == null) {
            Map<Integer, SGLineStyle> lineStyleMap = this.getLineStyleMap();
            Iterator<Entry<Integer, SGLineStyle>> cItr = lineStyleMap.entrySet().iterator();
            while (cItr.hasNext()) {
        		StringBuffer sbTmp = new StringBuffer();
            	Entry<Integer, SGLineStyle> entry = cItr.next();
            	Integer index = entry.getKey();
            	SGLineStyle lineStyle = entry.getValue();
            	sb.append(COM_DATA);
        		sbTmp.append('(');
        		sbTmp.append(this.getID());
        		sbTmp.append(", ");
        		sbTmp.append(index + 1);
        		sbTmp.append(", ");
        		SGPropertyMap cMap = new SGPropertyMap();
            	if (lineWidthCommon == null) {
            		SGPropertyUtility.addProperty(cMap, COM_DATA_LINE_WIDTH, 
            				SGUtility.getExportLineWidth(lineStyle.getLineWidth(LINE_WIDTH_UNIT)),
            				LINE_WIDTH_UNIT);
            	}
            	if (lineTypeCommon == null) {
            		SGPropertyUtility.addProperty(cMap, COM_DATA_LINE_TYPE, 
            				SGDrawingElementLine.getLineTypeName(lineStyle.getLineType()));
            	}
            	if (lineColorCommon == null) {
            		SGPropertyUtility.addProperty(cMap, COM_DATA_LINE_COLOR, lineStyle.getColor());
            	}
        		sbTmp.append(cMap.toString());
        		sbTmp.append(")\n");
        		sb.append(sbTmp.toString());
            }
        }
    	
        /*
		// line color map
		Iterator<Entry<String, SGColorMap>> colorMapItr = this.mLineColorMapManager
				.getColorMaps().entrySet().iterator();
		while (colorMapItr.hasNext()) {
			StringBuffer sbTmp = new StringBuffer();
			Entry<String, SGColorMap> entry = colorMapItr.next();
			String colorMapName = entry.getKey();
			SGColorMap colorMap = entry.getValue();
			SGPropertyMap pMap = colorMap.getPropertyMap();
			sbTmp.append(COM_DATA);
			sbTmp.append('(');
			sbTmp.append(this.getID());
			sbTmp.append(", ");
			sbTmp.append(colorMapName);
			sbTmp.append(", ");
			sbTmp.append(SGCommandUtility.createCommandString(pMap));
			sbTmp.append(")\n");
			sb.append(sbTmp.toString());
		}
		*/

    	return sb.toString();
    }

    /**
     * Replaces the data source.
     * 
     * @param srcOld
     *           old data source
     * @param srcNew
     *           new data source
     * @param obs
     *           data source observer
     */
	@Override
    public void replaceDataSource(final SGIDataSource srcOld, final SGIDataSource srcNew,
    		final SGDataSourceObserver obs) {
		super.replaceDataSource(srcOld, srcNew, obs);
		for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
			SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
			gs.replaceDataSource(srcOld, srcNew, obs);
		}
    }

	@Override
	public boolean getAxisDateMode(final int location) {
		return this.mGraph.getAxisElement().getAxisDateMode(location);
	}

	@Override
	public boolean setTickLabelDateFormat(String format) {
		return SGMultipleSXYUtility.setTickLabelDateFormat(
				this.getTickLabelGroupsIgnoreNull(), format);
	}
	
    /**
     * Undo operation.
     */
    public boolean setMementoBackward() {
        if (super.setMementoBackward() == false) {
            return false;
        }
        if (this.updateWithData() == false) {
        	return false;
        }
        return true;
    }
    
    /**
     * Redo operation.
     */
    public boolean setMementoForward() {
        if (super.setMementoForward() == false) {
            return false;
        }
        if (this.updateWithData() == false) {
        	return false;
        }
        return true;
    }

    @Override
    public boolean hasDateTickLabels() {
    	SGISXYTypeMultipleData sxyData = (SGISXYTypeMultipleData) this.mData;
    	return !sxyData.hasGenericTickLabels();
    }
    
    @Override
    protected boolean onMouseMoved(MouseEvent e) {
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY sxy = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		if (sxy.onMouseMoved(e)) {
    			return true;
    		}
    	}
    	return false;
    }
    
	@Override
    protected void setFocusedValueIndices(final SGDataViewerDialog dg,
    		List<SGXYSimpleIndexBlock> blockList) {

		// index list of child objects
		List<Integer> childIndexList = new ArrayList<Integer>();
    	for (SGXYSimpleIndexBlock block : blockList) {
    		SGIntegerSeries childIndexSeries = block.getXSeries();
    		childIndexList.addAll(childIndexSeries.getNumberList());
    	}
    	
    	// index list of values
    	List<Integer> valueIndexList = new ArrayList<Integer>();
    	for (SGXYSimpleIndexBlock block : blockList) {
    		SGIntegerSeries valueIndexSeries = block.getYSeries();
    		valueIndexList.addAll(valueIndexSeries.getNumberList());
    	}
    	if (valueIndexList.size() == 0) {
        	for (int sxyIndex : childIndexList) {
        		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(sxyIndex);
        		gs.removeFocusedValueList(dg);
        	}
    		return;
    	}

    	// get array section
    	SGIntegerSeriesSet stride = this.mData.getDataViewerRowStride(dg.getColumnType());
    	int[] arraySectionIndices = stride.getNumbers();

    	// set focused array indices to child objects
    	for (int sxyIndex : childIndexList) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(sxyIndex);
    		gs.setFocusedValueIndices(dg, valueIndexList, arraySectionIndices);
    	}
	}

	@Override
    protected void paintFocusedShapes(Graphics2D g2d) {
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		gs.paintFocusedShapes(g2d);
    	}
    }
    
	@Override
    public void removeFocusedValueList(final SGDataViewerDialog dg) {
    	super.removeFocusedValueList(dg);
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		gs.removeFocusedValueList(dg);
    	}
    }
	
	@Override
    public void initFocusedValueList(final SGDataViewerDialog dg) {
		super.initFocusedValueList(dg);
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		gs.initFocusedValueList(dg);
    	}
    }

	@Override
    public void clearFocusedValueListMap() {
    	this.mFocusedValueListMap.clear();
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		gs.clearFocusedValueListMap();
    	}
    }

	@Override
	protected String getToolTipText(final int x, final int y) {
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY sxy = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		String str = sxy.getToolTipText(x, y);
    		if (str != null) {
    			return str;
    		}
    	}
    	return null;
	}

	@Override
    protected void setCurrentFrameIndexSub(final int index) {
		for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
			SGElementGroupSetInGraphSXY gs = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
			gs.setCurrentFrameIndex(index);
		}
        this.updateDrawingElementsLocation(this.mData);
    }

	@Override
    protected List<SGTwoDimensionalArrayIndex> getDataViewerIndex(final int x, final int y) {
    	for (int ii = 0; ii < this.mElementGroupSetList.size(); ii++) {
    		SGElementGroupSetInGraphSXY sxy = (SGElementGroupSetInGraphSXY) this.mElementGroupSetList.get(ii);
    		final List<Integer> rowList = sxy.getDataViewerRowIndex(x, y);
    		if (rowList != null) {
    			List<SGTwoDimensionalArrayIndex> ret = new ArrayList<SGTwoDimensionalArrayIndex>();
    			for (Integer row : rowList) {
    				ret.add(new SGTwoDimensionalArrayIndex(ii, row));
    			}
    			return ret;
    		}
    	}
    	return null;
    }

	@Override
    public String getAxisValueString(final int x, final int y) {
    	double xValue = this.mGraph.calcValue(x, this.mXAxis, true);
    	xValue = SGUtilityNumber.getNumberInRangeOrder(xValue, this.mXAxis);
    	double yValue = this.mGraph.calcValue(y, this.mYAxis, false);
    	yValue = SGUtilityNumber.getNumberInRangeOrder(yValue, this.mYAxis);
    	StringBuffer sb = new StringBuffer();
    	sb.append('(');
    	sb.append(xValue);
    	sb.append(", ");
    	sb.append(yValue);
    	sb.append(')');
    	return sb.toString();
    }

}
