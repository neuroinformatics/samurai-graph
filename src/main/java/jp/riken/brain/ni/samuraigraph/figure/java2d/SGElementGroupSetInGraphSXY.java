package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIPaint;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGXYSimpleIndexBlock;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataValue.SXYSingleDataValue;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayData;
import jp.riken.brain.ni.samuraigraph.figure.SGDrawingElementString;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupBarInGraph.BarInGraph;

import org.w3c.dom.Element;

/**
 * The child object of the scalar type XY graph.
 *
 */
public class SGElementGroupSetInGraphSXY extends SGElementGroupSetInGraph
        implements SGIElementGroupSetInGraphSXY, SGIElementGroupSetSXY,
        SGIFigureDrawingElementConstants, 
        SGIStrokeEditDialogObserver, SGISXYAxisShiftEnabled, SGISXYDataConstants {

    /**
     * Shift to the x-axis direction.
     */
    protected double mShiftX;

    /**
     * Shift to the y-axis direction.
     */
    protected double mShiftY;
    
    private double[] mXValuesArray = null;
    
    private double[] mYValuesArray = null;

    /**
     * The series index in multiple graph
     * when this group set is created from the group set of multiple graph.
     */
    private int mSeriesIndex = -1;

    /**
     * The total number of series in multiple graph
     * which creates this group set.
     */
    private int mNumberOfSeries = -1;

    private SGLineStyle mLineStyle = null;

    /**
     *
     */
    protected SGElementGroupSetInGraphSXY(SGData data, SGFigureElementGraph graph) {
        super(data, graph);
    }

    /**
     *
     */
    public void dispose() {
        super.dispose();
        this.mXValuesArray = null;
        this.mYValuesArray = null;
        if (this.mData != null) {
            this.mData.dispose();
            this.mData = null;
        }
    }

    /**
     *
     * @return
     */
    public String getClassDescription() {
        return "";
    }

    /**
     *
     */
    public boolean getLegendVisibleFlag() {
        return this.isVisibleInLegend();
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

    public ELEMENT_TYPE getSelectedGroupType() {
        return this.mSelectedGroupType;
    }

    private ELEMENT_TYPE mSelectedGroupType = ELEMENT_TYPE.Void;

    //
    // Line
    //

    public boolean isLineVisible() {
        return this.getLineGroup().isVisible();
    }

    public float getLineWidth() {
        return this.getLineGroup().getLineWidth();
    }

    public float getLineWidth(final String unit) {
        return this.getLineGroup().getLineWidth(unit);
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
        this.getLineGroup().setVisible(b);
        return true;
    }

    public boolean setLineWidth(final float width, final String unit) {
        return this.getLineGroup().setLineWidth(width, unit);
    }

    public boolean setLineType(final int type) {
        return this.getLineGroup().setLineType(type);
    }

    public boolean setLineColor(final Color cl) {
        return this.getLineGroup().setColor(cl);
    }

    /**
     * Sets whether the lines connect all effective points.
     *
     * @return true if succeeded
     */
    public boolean setLineConnectingAll(final boolean b) {
        return this.getLineGroup().setLineConnectingAll(b);
    }

    public void setLineNumber1(int num) {
        this.getLineGroup().setLineNumber1(num);
    }

    public void setLineNumber2(int num) {
        this.getLineGroup().setLineNumber2(num);
    }

    public void setLineLength1(float len) {
        this.getLineGroup().setLineLength1(len);
    }

    public void setLineLength2(float len) {
        this.getLineGroup().setLineLength2(len);
    }

    public void setSpace(float space) {
        this.getLineGroup().setSpace(space);
    }

    public int getLineNum1() {
        return this.getLineGroup().getLineNum1();
    }

    public int getLineNum2() {
        return this.getLineGroup().getLineNum2();
    }

    public float getLineLength1() {
        return this.getLineGroup().getLineLength1();
    }

    public float getLineLength2() {
        return this.getLineGroup().getLineLength2();
    }

    public float getSpace() {
        return this.getLineGroup().getSpace();
    }

    //
    // Symbol
    //

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
        return this.getSymbolGroup().getSize(unit);
    }

    public float getSymbolLineWidth() {
        return this.getSymbolGroup().getLineWidth();
    }

    public float getSymbolLineWidth(final String unit) {
        return this.getSymbolGroup().getLineWidth(unit);
    }

    public SGIPaint getSymbolInnerPaint() {
        return this.getSymbolGroup().getInnerPaint();
    }

    public Color getSymbolLineColor() {
        return this.getSymbolGroup().getLineColor();
    }

    public boolean isSymbolLineVisible() {
        return this.getSymbolGroup().isLineVisible();
    }

    public boolean setSymbolVisible(final boolean b) {
        this.getSymbolGroup().setVisible(b);
        return true;
    }

    public boolean setSymbolType(final int type) {
        return this.getSymbolGroup().setType(type);
    }

    public boolean setSymbolSize(final float size, final String unit) {
        return this.getSymbolGroup().setSize(size, unit);
    }

    public boolean setSymbolLineWidth(final float lw, final String unit) {
        return this.getSymbolGroup().setLineWidth(lw, unit);
    }

    public boolean setSymbolInnerPaint(final SGIPaint paint) {
        return this.getSymbolGroup().setInnerPaint(paint);
    }

    public boolean setSymbolLineColor(final Color cl) {
        return this.getSymbolGroup().setLineColor(cl);
    }

    public boolean setSymbolLineVisible(boolean b) {
        return this.getSymbolGroup().setLineVisible(b);
    }

    //
    // Bar
    //

    public boolean isBarVisible() {
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
        return this.getBarGroup().getEdgeLineWidth(unit);
    }

    public SGIPaint getBarInnerPaint() {
        return this.getBarGroup().getInnerPaint();
    }

    public Color getBarEdgeLineColor() {
        return this.getBarGroup().getEdgeLineColor();
    }

    public boolean isBarEdgeLineVisible() {
        return this.getBarGroup().isEdgeLineVisible();
    }

    public boolean isBarVertical() {
        return this.getBarGroup().isVertical();
    }

    public boolean setBarVisible(final boolean b) {
        this.getBarGroup().setVisible(b);
        return true;
    }

    public boolean setBarBaselineValue(final double value) {
        return this.getBarGroup().setBaselineValue(value);
    }

    public boolean setBarWidthValue(final double value) {
        return this.getBarGroup().setWidthValue(value);
    }

    public boolean setBarEdgeLineWidth(final float lw, final String unit) {
        return this.getBarGroup().setEdgeLineWidth(lw, unit);
    }

    public boolean setBarInnerPaint(final SGIPaint paint) {
        return this.getBarGroup().setInnerPaint(paint);
    }

    public boolean setBarEdgeLineColor(final Color cl) {
        return this.getBarGroup().setEdgeLineColor(cl);
    }

    public boolean setBarEdgeLineVisible(boolean b) {
        return this.getBarGroup().setEdgeLineVisible(b);
    }

    public boolean setBarVertical(boolean b) {
        return this.getBarGroup().setVertical(b);
    }

    public boolean hasValidBaselineValue(final int config, final Number value) {
        final SGAxis axis = (config == -1) ? this.mYAxis : this.mGraph
                .getAxisElement().getAxisInPlane(config);
        final double v = (value != null) ? value.doubleValue() : this
                .getBarBaselineValue();
        return axis.isValidValue(v);
    }

    // Error Bar
    public boolean isErrorBarAvailable() {
        SGISXYTypeSingleData data = (SGISXYTypeSingleData) this.mData;
        return data.isErrorBarAvailable();
    }

    public boolean isErrorBarVisible() {
        return this.getErrorBarGroup().isVisible();
    }

    public int getErrorBarHeadType() {
        return this.getErrorBarGroup().getHeadType();
    }

    public float getErrorBarHeadSize() {
        return this.getErrorBarGroup().getHeadSize();
    }

    public float getErrorBarHeadSize(final String unit) {
        return this.getErrorBarGroup().getHeadSize(unit);
    }

    public Color getErrorBarColor() {
        return this.getErrorBarGroup().getColor();
    }

    public float getErrorBarLineWidth() {
        return this.getErrorBarGroup().getLineWidth();
    }

    public float getErrorBarLineWidth(final String unit) {
        return this.getErrorBarGroup().getLineWidth(unit);
    }

    public int getErrorBarStyle() {
        return this.getErrorBarGroup().getErrorBarStyle();
    }

    public boolean isErrorBarVertical() {
    	return this.getErrorBarGroup().isVertical();
    }

    public boolean isErrorBarOnLinePosition() {
        return this.getErrorBarGroup().isPositionOnLine();
    }

    public boolean setErrorBarVisible(final boolean b) {
        this.getErrorBarGroup().setVisible(b);
        return true;
    }

    public boolean setErrorBarHeadType(final int type) {
        return this.getErrorBarGroup().setHeadType(type);
    }

    public boolean setErrorBarHeadSize(final float size, final String unit) {
        return this.getErrorBarGroup().setHeadSize(size, unit);
    }

    public boolean setErrorBarColor(final Color cl) {
        return this.getErrorBarGroup().setColor(cl);
    }

    public boolean setErrorBarLineWidth(final float lw, final String unit) {
        return this.getErrorBarGroup().setLineWidth(lw, unit);
    }

    public boolean setErrorBarStyle(final int style) {
        return this.getErrorBarGroup().setErrorBarStyle(style);
    }

    public boolean setErrorBarVertical(final boolean b) {
    	return this.getErrorBarGroup().setVertical(b);
    }

    @Override
    public boolean setErrorBarOnLinePosition(boolean b) {
        this.getErrorBarGroup().setPositionOnLine(b);
        return true;
    }

    // Tick Label
    public boolean isTickLabelAvailable() {
        SGISXYTypeSingleData data = (SGISXYTypeSingleData) this.mData;
        return data.isTickLabelAvailable();
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
        return this.getTickLabelGroup().getFontSize(unit);
    }

    public Color getTickLabelColor() {
        return this.getTickLabelGroup().getColor();
    }

    public float getTickLabelAngle() {
        return this.getTickLabelGroup().getAngle();
    }

    public boolean hasTickLabelHorizontalAlignment() {
        return this.getTickLabelGroup().hasHorizontalAlignment();
    }

    public int getTickLabelDecimalPlaces() {
        return this.getTickLabelGroup().getDecimalPlaces();
    }

    public int getTickLabelExponent() {
        return this.getTickLabelGroup().getExponent();
    }
    
    public String getTickLabelDateFormat() {
    	return this.getTickLabelGroup().getDateFormat();
    }

    public boolean setTickLabelVisible(final boolean b) {
        this.getTickLabelGroup().setVisible(b);
        return true;
    }

    public boolean setTickLabelFontName(final String name) {
        return this.getTickLabelGroup().setFontName(name);
    }

    public boolean setTickLabelFontStyle(final int style) {
        return this.getTickLabelGroup().setFontStyle(style);
    }

    public boolean setTickLabelFontSize(final float size, final String unit) {
        return this.getTickLabelGroup().setFontSize(size, unit);
    }

    public boolean setTickLabelColor(final Color cl) {
        return this.getTickLabelGroup().setColor(cl);
    }

    public boolean setTickLabelAngle(final float angle) {
        return this.getTickLabelGroup().setAngle(angle);
    }

    public boolean setTickLabelDecimalPlaces(final int dp) {
        return this.getTickLabelGroup().setDecimalPlaces(dp);
    }

    public boolean setTickLabelExponent(final int exp) {
        return this.getTickLabelGroup().setExponent(exp);
    }
    
    public boolean setTickLabelDateFormat(final String format) {
    	return this.getTickLabelGroup().setDateFormat(format);
    }

	public boolean setTickLabelHorizontalAlignment(boolean b) {
		return this.getTickLabelGroup().setHorizontalAlignment(b);
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

        // add a group to the list
        this.mDrawingElementGroupList.add(group);

        return true;
    }

    /**
     * Creates drawing elements of error bars.
     *
     * @param dataXY
     *             XY type data
     * @return the group of error bars
     */
    public SGElementGroupErrorBar createErrorBars(
            SGISXYTypeSingleData dataSXY) {
        final SGElementGroupErrorBarInGraph group = new SGElementGroupErrorBarInGraph(
                dataSXY, this.mGraph);
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
    public SGElementGroupTickLabel createTickLabels(
            SGISXYTypeSingleData dataXY) {
        final SGElementGroupTickLabelInGraphSXY group = new SGElementGroupTickLabelInGraphSXY(
                dataXY, this.mGraph);
        this.mDrawingElementGroupList.add(group);
        group.mGroupSet = this;
        return group;
    }

    protected void initPointsArray(final int num) {
//        this.mLinePointsArray = this.createPoints(num);
//        this.mBarPointsArray = this.createPoints(num);
    }

//    private SGTuple2f[] createPoints(final int num) {
//        SGTuple2f[] pointsArray = new SGTuple2f[num];
//        for (int ii = 0; ii < num; ii++) {
//            pointsArray[ii] = new SGTuple2f();
//        }
//        return pointsArray;
//    }
    
    /**
     * Called when the location of data points is changed.
     *
     * @param data
     *           a data object
     * @return true if succeeded
     */
    public boolean updateDrawingElementsLocation(final SGIData data) {

        if ((data instanceof SGISXYTypeSingleData) == false) {
            return false;
        }
        SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;

        // set the location of the data points
        final double[] xValueArray = dataSXY.getXValueArray(false);
        final double[] yValueArray = dataSXY.getYValueArray(false);
        if (xValueArray == null || yValueArray == null) {
        	return true;
        }
        if (xValueArray.length != yValueArray.length) {
        	return false;
        }
        final int num = xValueArray.length;
        this.mXValuesArray = xValueArray;
        this.mYValuesArray = yValueArray;
        
        SGElementGroupLine lineGroup = this.getLineGroup();
        SGElementGroupSymbol symbolGroup = this.getSymbolGroup();
        SGElementGroupBar barGroup = this.getBarGroup();
        SGElementGroupErrorBar errorBarGroup = this.getErrorBarGroup();
        SGElementGroupTickLabel tickLabelGroup = this.getTickLabelGroup();
        
        SGAxis xAxis = this.getXAxis();
        SGAxis yAxis = this.getYAxis();

        // calculate the values and the location of points
        SGTuple2d[] lineValues = null;
        SGTuple2f[] linePoints = null;
        if (lineGroup.isVisible() || symbolGroup.isVisible() 
        		|| (errorBarGroup.isVisible() && errorBarGroup.isPositionOnLine())) {
            lineValues = this.calcLineValues(xValueArray, yValueArray);
            linePoints = this.calcPointsArray(lineValues);
        }
        SGTuple2d[] barValues = null;
        SGTuple2f[] barPoints = null;
        if (barGroup.isVisible() 
        		|| (errorBarGroup.isVisible() && !errorBarGroup.isPositionOnLine())) {
	        barValues = this.calcBarValues(xValueArray, yValueArray);
	        barPoints = this.calcBarPointsArray(xValueArray, yValueArray);
        }
        
		// tick labels
		double[] tickLabelValues = null;
		if (dataSXY.isTickLabelAvailable()) {
	        Boolean horizontal = dataSXY.isTickLabelHorizontal();
	        if (horizontal == null) {
	        	return false;
	        }
	        final double[] valueArray;
//	        final SGAxis axis;
	        final double tlShift;
	        if (horizontal) {
	        	valueArray = dataSXY.getXValueArray(true);
//	        	axis = xAxis;
	        	tlShift = this.getShiftX();
	        } else {
	        	valueArray = dataSXY.getYValueArray(true);
//	        	axis = yAxis;
	        	tlShift = this.getShiftY();
	        }
			final int[] tickLabelIndices = dataSXY.getTickLabelValueIndices();
			tickLabelValues = new double[tickLabelIndices.length];
			for (int ii = 0; ii < tickLabelValues.length; ii++) {
				tickLabelValues[ii] = valueArray[tickLabelIndices[ii]] + tlShift;
			}
//			float[] tickLabelPointsArray = new float[tickLabelValues.length];
//			if (!this.mGraph.calcLocationOfPoints(tickLabelValues, axis, horizontal, tickLabelPointsArray)) {
//				return false;
//			}
		}

        List<SGElementGroup> gList = this.mDrawingElementGroupList;
        for (int ii = 0; ii < gList.size(); ii++) {
            SGElementGroup group = (SGElementGroup) gList.get(ii);
            if (group.isVisible()) {
                if (group.equals(errorBarGroup)) {

                    // if the data does not have error values, go next
                    if (!dataSXY.isErrorBarAvailable()) {
                        continue;
                    }

                    // set the location of error bars
                    SGElementGroupErrorBarInGraph eGroup = (SGElementGroupErrorBarInGraph) group;
                    final SGTuple2d[] valueArray;
                    final SGTuple2f[] pointsArray;
                    if (eGroup.isPositionOnLine()) {
                        valueArray = lineValues;
                        pointsArray = linePoints;
                    } else {
                    	valueArray = barValues;
                    	pointsArray = barPoints;
                    }
                    if (eGroup.calcLocation(dataSXY, xAxis, yAxis, valueArray, pointsArray) == false) {
                        return false;
                    }
                    if (eGroup.updateLocation() == false) {
                        return false;
                    }
                    
                } else if (group.equals(tickLabelGroup)) {

                    // if the data does not have tick label, go next
                    if (!dataSXY.isTickLabelAvailable()) {
                        continue;
                    }

                    // set the location of tick labels
                    Boolean horizontal = dataSXY.isTickLabelHorizontal();
                    if (horizontal == null) {
                    	return false;
                    }
                    SGElementGroupTickLabelInGraph tGroup = (SGElementGroupTickLabelInGraph) group;
                    tGroup.setHorizontalAlignment(horizontal.booleanValue());
                    if (tGroup.calcLocation(dataSXY, xAxis, yAxis, tickLabelValues) == false) {
                        return false;
                    }
                    if (tGroup.updateLocation() == false) {
                        return false;
                    }
                    
                } else if (group.equals(lineGroup)) {
                    SGDrawingElement[] elArray = lineGroup.getDrawingElementArray();
                    if (elArray == null || elArray.length != num) {
                    	lineGroup.initDrawingElement(linePoints);
                    }
                    if (lineGroup.setLocation(linePoints) == false) {
                        return false;
                    }
                } else if (group.equals(symbolGroup)) {
                    SGDrawingElement[] elArray = symbolGroup.getDrawingElementArray();
                    if (elArray == null || elArray.length != num) {
                    	symbolGroup.initDrawingElement(linePoints);
                    }
                    if (symbolGroup.setLocation(linePoints) == false) {
                        return false;
                    }
                } else if (group.equals(barGroup)) {
                    SGDrawingElement[] elArray = barGroup.getDrawingElementArray();
                    if (elArray == null || elArray.length != num) {
                    	barGroup.initDrawingElement(barPoints);
                    }
                    if (barGroup.setLocation(barPoints) == false) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Returns the bounding box of tick labels for a given data.
     * @param data
     *             a data object
     */
    public Rectangle2D getTickLabelsBoundingBox(final SGIData data) {

        if ((data instanceof SGISXYTypeSingleData) == false) {
            throw new IllegalArgumentException("(data instanceof SGISXYTypeData) == false");
        }

        ArrayList rectList = new ArrayList();
        List<SGElementGroupTickLabel> tickLabelGroups = this.getTickLabelGroups();
        if (tickLabelGroups.size() == 0) {
            return null;
        }
        final SGElementGroupTickLabel group = tickLabelGroups
                .get(0);
        if (!group.isVisible()) {
            return null;
        }

        ArrayList strList = new ArrayList();
        SGDrawingElement[] elArray = group.getDrawingElementArray();
        for (int ii = 0; ii < elArray.length; ii++) {
            if (!elArray[ii].isVisible()) {
                continue;
            }
            if (elArray[ii] instanceof SGDrawingElementString) {
                SGDrawingElementString elStr = (SGDrawingElementString) elArray[ii];
                String str = elStr.getString();

                // skip an empty string
                if ("".equals(str)) {
                    continue;
                }
            }
            strList.add(elArray[ii]);
        }

        Rectangle2D rectAll = SGUtilityForFigureElementJava2D.getBoundingBox(strList);
        if (rectAll == null) {
            return null;
        }
        rectList.add(rectAll);

        // join the rectangles
        Rectangle2D uniRect = SGUtility.createUnion(rectList);
        if (uniRect == null) {
            return null;
        }

        return uniRect;
    }

    /**
     * Paint bars, error bars, lines and symbols.
     */
    @Override
    public void paintGraphics2D(final Graphics2D g2d) {

        Rectangle2D gRect = null;
        if (!this.getClipFlag()) {
            gRect = this.mGraph.getGraphRect();
        }

        // bar
        final SGElementGroupBarInGraph groupBar = this.mGraph.getGroupBar(this);
        if (groupBar != null) {
            if (groupBar.isVisible()) {
                groupBar.paintElement(g2d, gRect);
            }
        }

        // error bar
        SGData data = this.getData();
        if (data instanceof SGISXYTypeSingleData) {
            SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
            if (dataSXY.isErrorBarAvailable()) {
                final SGElementGroupErrorBar eGroup = this.getErrorBarGroup();
                if (eGroup != null) {
                    if (eGroup.isVisible()) {
                        eGroup.paintElement(g2d, gRect);
                    }
                }
            }
        }

        // line
        final SGElementGroupLineInGraph groupLine = this.mGraph
                .getGroupLine(this);
        if (groupLine != null) {
            if (groupLine.isVisible()) {
                groupLine.paintElement(g2d, gRect);
            }
        }

        // symbol
        final SGElementGroupSymbolInGraph groupSymbol = this.mGraph
                .getGroupSymbol(this);
        if (groupSymbol != null) {
            if (groupSymbol.isVisible()) {
                groupSymbol.paintElement(g2d, gRect);
            }
        }

    }

    /**
     * Paint tick labels.
     * @param g2d
     *            the graphics context
     */
    public boolean paintDataString(final Graphics2D g2d) {

        SGData data = this.getData();
        if (data instanceof SGISXYTypeSingleData) {
            SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
            if (dataSXY.isTickLabelAvailable()) {
                List<SGElementGroupTickLabel> tList = this.getTickLabelGroups();
                if (tList.size() != 0) {
                    final SGElementGroupTickLabel groupString = tList.get(0);
                    if (groupString != null) {
                        if (groupString.isVisible()) {
                            groupString.paintElement(g2d);
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
     *
     */
    public boolean setXAxisLocation(final int config) {
        if (config != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                && config != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
            return false;
        }
        return this.setXAxis(this.getAxis(config));
    }

    /**
     *
     */
    public boolean setYAxisLocation(final int config) {
        if (config != SGIFigureElementAxis.AXIS_VERTICAL_1
                && config != SGIFigureElementAxis.AXIS_VERTICAL_2) {
            return false;
        }
        return this.setYAxis(this.getAxis(config));
    }

    private SGAxis getAxis(final int config) {
        return this.mGraph.mAxisElement.getAxisInPlane(config);
    }

    /**
     *
     */
    public boolean prepare() {
        this.mTemporaryProperties = this.getProperties();
        return true;
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

        final String strX = this.mGraph.getAxisElement().getLocationName(
                this.getXAxis());
        final String strY = this.mGraph.getAxisElement().getLocationName(
                this.getYAxis());

        el.setAttribute(SGIFigureElement.KEY_X_AXIS_POSITION, strX);
        el.setAttribute(SGIFigureElement.KEY_Y_AXIS_POSITION, strY);

        return true;
    }

    /**
     *
     * @param e
     * @param groupSet
     * @return
     */
    protected boolean onMouseClicked(final MouseEvent e) {
        if (super.onMouseClicked(e) == false) {
            return false;
        }

        SGElementGroup group = this.getElementGroupAt(e.getX(), e.getY());

        ELEMENT_TYPE type = ELEMENT_TYPE.Void;
        if (group instanceof SGElementGroupLineInGraph) {
            type = ELEMENT_TYPE.Line;
        } else if (group instanceof SGElementGroupSymbolInGraph) {
            type = ELEMENT_TYPE.Symbol;
        } else if (group instanceof SGElementGroupBarInGraph) {
            type = ELEMENT_TYPE.Bar;
        } else if (group instanceof SGElementGroupErrorBarInGraph) {
            type = ELEMENT_TYPE.ErrorBar;
        } else if (group instanceof SGElementGroupTickLabelInGraph) {
            type = ELEMENT_TYPE.TickLabel;
        }
        this.mSelectedGroupType = type;

        return true;
    }

    /**
     * Returns an element group at a given point.
     *
     * @param x
     *            the x coordinate
     * @param y
     *            the y coordinate
     * @return an element group if it contains the given point
     */
    @Override
    public SGElementGroup getElementGroupAt(final int x, final int y) {
        SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) this.mData;
        final double[] xValueArray = dataSXY.getXValueArray(false);
        final double[] yValueArray = dataSXY.getYValueArray(false);

        SGElementGroupLineInGraph lineGroup = (SGElementGroupLineInGraph) this.getLineGroup();
        SGElementGroupSymbolInGraph symbolGroup = (SGElementGroupSymbolInGraph) this.getSymbolGroup();
        SGElementGroupBarInGraph barGroup = (SGElementGroupBarInGraph) this.getBarGroup();
        SGElementGroupErrorBarInGraph errorBarGroup = (SGElementGroupErrorBarInGraph) this.getErrorBarGroup();
        SGElementGroupTickLabelInGraph tickLabelGroup = (SGElementGroupTickLabelInGraph) this.getTickLabelGroup();

        SGTuple2f[] linePoints = null;
        if (lineGroup.isVisible() || symbolGroup.isVisible() 
        		|| (errorBarGroup.isVisible() && errorBarGroup.isPositionOnLine())) {
            linePoints = this.calcLinePointsArray(xValueArray, yValueArray);
        }
        if (lineGroup.isVisible()) {
        	if (lineGroup.contains(x, y, linePoints, xValueArray, yValueArray)) {
        		return lineGroup;
        	}
        }
        if (symbolGroup.isVisible()) {
        	if (symbolGroup.contains(x, y, linePoints, xValueArray, yValueArray)) {
        		return symbolGroup;
        	}
        }
        
        SGTuple2f[] barPoints = null;
        if (barGroup.isVisible() 
        		|| (errorBarGroup.isVisible() && !errorBarGroup.isPositionOnLine())) {
	        barPoints = this.calcBarPointsArray(xValueArray, yValueArray);
        }
        if (barGroup.isVisible()) {
        	if (barGroup.contains(x, y, barPoints, xValueArray, yValueArray)) {
        		return barGroup;
        	}
        }

        SGTuple2f[] errorBarPoints = errorBarGroup.isPositionOnLine() ? linePoints : barPoints;
        if (this.isErrorBarAvailable()) {
        	if (errorBarGroup.isVisible()) {
        		if (errorBarGroup.contains(x, y, errorBarPoints, xValueArray, yValueArray)) {
        			return errorBarGroup;
        		}
        	}
        }
        if (this.isTickLabelAvailable()) {
        	if (tickLabelGroup.contains(x, y)) {
        		return tickLabelGroup;
        	}
        }
        return null;
    }

    /**
     * Update the text strings of tick labels.
     */
    public void updateTickLabelStrings() {
        SGData data = this.getData();
        if (data != null) {
            SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
            if (dataSXY.isTickLabelAvailable()) {
                SGElementGroupTickLabel tg = this.getTickLabelGroup();
                boolean eq = true;

                // set decimal places and exponent
                final int dp = tg.getDecimalPlaces();
                final int exp = tg.getExponent();
                final String dateFormat = tg.getDateFormat();
                if (dp != dataSXY.getDecimalPlaces() 
                		|| exp != dataSXY.getExponent()
                		|| !SGUtility.equals(dateFormat, dataSXY.getDateFormat())) {
                    eq = false;
                }

            	// set the decimal places and the exponent to the data object
                dataSXY.setDecimalPlaces(dp);
                dataSXY.setExponent(exp);
                dataSXY.setDateFormat(dateFormat);

                // compare all text strings
                String[] strArray = dataSXY.getStringArray(false);
                SGDrawingElement[] tlArray = tg.getDrawingElementArray();
                if (strArray.length != tlArray.length) {
                	eq = false;
                	tg.initDrawingElement(strArray.length);
                }

                // set the text strings to the tick labels when tick labels, decimal places
                // or the exponent are not equal
                if (!eq) {
                    tg.setStrings(strArray);
                }
            } else {
                SGElementGroupTickLabel tg = this.getTickLabelGroup();
                SGDrawingElement[] tlArray = tg.getDrawingElementArray();
                tg.initDrawingElement(tlArray.length);
            }
        }
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
     * Sets the properties of data.
     * @param dp
     *           properties of data
     */
    @Override
    public boolean setDataProperties(final SGProperties dp) {
        if (super.setDataProperties(dp) == false) {
        	return false;
        }
        this.updateTickLabelStrings();
        return true;
    }

    /**
     * Returns the location of a line and symbols at a given index.
     *
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getLineLocation(final int index) {
    	SGTuple2f[] pointsArray = this.calcLinePointsArray(this.mXValuesArray, this.mYValuesArray);
    	return this.getLocation(index, pointsArray);
    }

    /**
     * Returns the location of a symbol at a given index.
     *
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getSymbolLocation(final int index) {
    	return this.getLineLocation(index);
    }

    /**
     * Returns the location of a bar at a given index.
     *
     * @param index
     *           the index
     * @return the location
     */
    public SGTuple2f getBarLocation(final int index) {
    	SGTuple2f[] pointsArray = this.calcBarPointsArray(this.mXValuesArray, this.mYValuesArray);
    	return this.getLocation(index, pointsArray);
    }

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

        return result;
    }

    /**
     * Sets the data.
     *
     * @param data
     *            a data object
     * @return true if succeeded
     */
    public boolean setData(SGData data) {
    	if (!super.setData(data)) {
    		return false;
    	}
    	if (data == null) {
        	this.initPointsArray(0);
    		return true;
    	} else {
        	if (!(data instanceof SGISXYTypeSingleData)) {
        		return false;
        	}
        	SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
        	this.initPointsArray(dataSXY.getPointsNumber());
        	if (!this.updateDrawingElementsLocation(dataSXY)) {
        		return false;
        	}
    	}
    	return true;
    }

    /**
	 * Returns a list of line groups.
	 *
	 * @return a list of line groups
	 */
	public List<SGElementGroupLine> getLineGroups() {
		List<SGElementGroupLine> retList = new ArrayList<SGElementGroupLine>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupLine.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupLine) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns a list of symbol groups.
	 *
	 * @return a list of symbol groups
	 */
	public List<SGElementGroupSymbol> getSymbolGroups() {
		List<SGElementGroupSymbol> retList = new ArrayList<SGElementGroupSymbol>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupSymbol.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupSymbol) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns a list of bar groups.
	 *
	 * @return a list of bar groups
	 */
	public List<SGElementGroupBar> getBarGroups() {
		List<SGElementGroupBar> retList = new ArrayList<SGElementGroupBar>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupBar.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupBar) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns a list of error bar groups.
	 *
	 * @return a list of error bar groups
	 */
	public List<SGElementGroupErrorBar> getErrorBarGroups() {
		List<SGElementGroupErrorBar> retList = new ArrayList<SGElementGroupErrorBar>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupErrorBar.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupErrorBar) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns a list of tick label groups.
	 *
	 * @return a list of tick label groups
	 */
	public List<SGElementGroupTickLabel> getTickLabelGroups() {
		List<SGElementGroupTickLabel> retList = new ArrayList<SGElementGroupTickLabel>();
		List<SGElementGroup> list = SGUtilityForFigureElement.getGroups(
				SGElementGroupTickLabel.class, this.mDrawingElementGroupList);
		for (int ii = 0; ii < list.size(); ii++) {
			retList.add((SGElementGroupTickLabel) list.get(ii));
		}
		return retList;
	}

	/**
	 * Returns a line group which is the first element of an array.
	 *
	 * @return the first element of an array of line groups, or null when this
	 *         group set does not have any line groups
	 */
	public SGElementGroupLine getLineGroup() {
		return (SGElementGroupLine) SGUtilityForFigureElement.getGroup(
				SGElementGroupLine.class, this.mDrawingElementGroupList);
	}

	/**
	 * Returns a symbol group which is the first element of an array.
	 *
	 * @return the first element of an array of symbol groups, or null when this
	 *         group set does not have any symbol groups
	 */
	public SGElementGroupSymbol getSymbolGroup() {
		return (SGElementGroupSymbol) SGUtilityForFigureElement.getGroup(
				SGElementGroupSymbol.class, this.mDrawingElementGroupList);
	}

	/**
	 * Returns a bar group which is the first element of an array.
	 *
	 * @return the first element of an array of bar groups, or null when this
	 *         group set does not have any bar groups
	 */
	public SGElementGroupBar getBarGroup() {
		return (SGElementGroupBar) SGUtilityForFigureElement.getGroup(
				SGElementGroupBar.class, this.mDrawingElementGroupList);
	}

	/**
	 * Returns an error bar group which is the first element of an array.
	 *
	 * @return the first element of an array of error bar groups, or null when
	 *         this group set does not have any error bar groups
	 */
	public SGElementGroupErrorBar getErrorBarGroup() {
		return (SGElementGroupErrorBar) SGUtilityForFigureElement
				.getGroup(SGElementGroupErrorBar.class,
						this.mDrawingElementGroupList);
	}

	/**
	 * Returns a tick label group which is the first element of an array.
	 *
	 * @return the first element of an array of tick label groups, or null when
	 *         this group set does not have any tick label groups
	 */
	public SGElementGroupTickLabel getTickLabelGroup() {
		return (SGElementGroupTickLabel) SGUtilityForFigureElement
				.getGroup(SGElementGroupTickLabel.class,
						this.mDrawingElementGroupList);
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

    public int getSeriesIndex() {
        return this.mSeriesIndex;
    }

    public int getNumberOfSeries() {
        return this.mNumberOfSeries;
    }

    public void setSeriesIndexAndNumber(final int index, final int number) {
        this.mSeriesIndex = index;
        this.mNumberOfSeries = number;
    }

    public double getShiftX() {
    	return this.mShiftX;
    }

    public double getShiftY() {
    	return this.mShiftY;
    }

    public boolean setShiftX(final double shift) {
    	this.mShiftX = shift;
        SGISXYTypeData data = (SGISXYTypeData) this.mData;
        SGTuple2d curShift = data.getShift();
        data.setShift(new SGTuple2d(shift, curShift.y));
    	return true;
    }

    public boolean setShiftY(final double shift) {
    	this.mShiftY = shift;
        SGISXYTypeData data = (SGISXYTypeData) this.mData;
        SGTuple2d curShift = data.getShift();
        data.setShift(new SGTuple2d(curShift.x, shift));
    	return true;
    }

    public double getBarOffsetX() {
        return this.getBarGroup().getOffsetX();
    }

    public double getBarOffsetY() {
        return this.getBarGroup().getOffsetY();
    }

    public boolean setBarOffsetX(final double shift) {
        return this.getBarGroup().setOffsetX(shift);
    }

    public boolean setBarOffsetY(final double shift) {
        return this.getBarGroup().setOffsetY(shift);
    }

    public double getBarInterval() {
        return this.getBarGroup().getInterval();
    }

    public boolean setBarInterval(final double interval) {
        return this.getBarGroup().setInterval(interval);
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
		return this.setErrorBarVertical(vertical);
	}

	/**
	 * Sets the alignment of tick label.
	 *
	 * @param horizontal
	 *           true to align horizontally
	 * @return true if succeeded
	 */
	public boolean setTickLabelAlignment(final boolean horizontal) {
		return this.setTickLabelHorizontalAlignment(horizontal);
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
		// do nothing
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
		// do nothing
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
    	// do nothing
    }

	@Override
	public boolean setIndexStride(SGIntegerSeriesSet stride) {
		// do nothing
		return true;
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
		// do nothing
		return true;
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
		// do nothing
		return true;
	}

	public void setLineStyle(SGLineStyle style) {
		SGLineStyle lineStyle = (SGLineStyle) style.clone();
		this.mLineStyle = lineStyle;
		SGElementGroupLine lineGroup = this.getLineGroup();
		lineGroup.setStyle(lineStyle);
	}

	public SGLineStyle getLineStyle() {
		return (this.mLineStyle != null) ? (SGLineStyle) this.mLineStyle.clone() : null;
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
	}

    /**
     * A class of properties for multiple data.
     *
     */
    public static class SXYElementGroupSetPropertiesInFigureElement extends
    		ElementGroupSetPropertiesInFigureElement {

    	double shiftX;

    	double shiftY;
    	
    	boolean lineColorAutoAssigned;

        String lineColorMapName = null;

        /**
         * Default constructors.
         */
        public SXYElementGroupSetPropertiesInFigureElement() {
            super();
        }
        
        @Override
        public void dispose() {
        	super.dispose();
            this.lineColorMapName = null;
        }

        /**
         * Returns whether this object is equal to given object.
         *
         * @param obj
         *            an object to be compared
         * @return true if two objects are equal
         */
    	public boolean equals(final Object obj) {
            if ((obj instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
                return false;
            }
            if (super.equals(obj) == false) {
                return false;
            }
            SXYElementGroupSetPropertiesInFigureElement p = (SXYElementGroupSetPropertiesInFigureElement) obj;
            if (p.shiftX != this.shiftX) {
                return false;
            }
            if (p.shiftY != this.shiftY) {
                return false;
            }
            if (p.lineColorAutoAssigned != this.lineColorAutoAssigned) {
            	return false;
            }
            if (!SGUtility.equals(p.lineColorMapName, this.lineColorMapName)) {
            	return false;
            }
    		return true;
    	}
    }

    @Override
    public SGProperties getProperties() {
    	SXYElementGroupSetPropertiesInFigureElement p = new SXYElementGroupSetPropertiesInFigureElement();
        if (this.getProperties(p) == false) {
            return null;
        }
        return p;
    }

    @Override
    public boolean getProperties(SGProperties p) {
        if ((p instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        SXYElementGroupSetPropertiesInFigureElement mp = (SXYElementGroupSetPropertiesInFigureElement) p;
        mp.shiftX = this.getShiftX();
        mp.shiftY = this.getShiftY();
        mp.lineColorAutoAssigned = this.isLineColorAutoAssigned();
        mp.lineColorMapName = this.getLineColorMapName();
        return true;
    }

    @Override
    public boolean setProperties(SGProperties p) {
        if ((p instanceof SXYElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }

        SXYElementGroupSetPropertiesInFigureElement mp = (SXYElementGroupSetPropertiesInFigureElement) p;
        this.setShiftX(mp.shiftX);
        this.setShiftY(mp.shiftY);
        this.setLineColorAutoAssigned(mp.lineColorAutoAssigned);
        this.setLineColorMapName(mp.lineColorMapName);
        return true;
    }
    
    private String mLineColorMapName = null;

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
	 * Sets the color map for lines.
	 * 
	 * @param name
	 *           name of the map to set
	 */
    public void setLineColorMapName(String name) {
    	this.mLineColorMapName = name;
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
    public Map<String, SGProperties> getElementGroupPropertiesMap() {
    	return SGUtilityForFigureElementJava2D.getElementGroupPropertiesMap(this);
    }
    
    @Override
    public boolean setElementGroupPropertiesMap(Map<String, SGProperties> pMap) {
        return SGUtilityForFigureElementJava2D.setElementGroupPropertiesMap(this, pMap);
    }
    
    public void setFocusedValueIndices(final SGDataViewerDialog dg, int[] focusedIndices,
    		int[] arraySectionIndices) {
    	
		List<Integer> pointIndexList = new ArrayList<Integer>();
    	int focusedCnt = 0;
    	for (int ii = 0; ii < arraySectionIndices.length; ii++) {
    		if (focusedIndices[focusedCnt] == arraySectionIndices[ii]) {
    			pointIndexList.add(ii);
        		focusedCnt++;
        		if (focusedCnt >= focusedIndices.length) {
        			break;
        		}
    		}
    	}
    	
    	// refresh the map
    	List<SGTuple2d> valueList = new ArrayList<SGTuple2d>();
    	SGTuple2d[] values;
		values = this.calcLineValues(this.mXValuesArray, this.mYValuesArray);
    	for (int ii = 0; ii < pointIndexList.size(); ii++) {
    		Integer index = pointIndexList.get(ii);
    		valueList.add(values[index]);
    	}
		this.mFocusedValueListMap.put(dg, valueList);
    }
    
    private SGTuple2d[] calcLineValues(final double[] xValueArray, final double[] yValueArray) {
    	final int num = xValueArray.length;
		final double dataShiftX = this.getShiftX();
		final double dataShiftY = this.getShiftY();
		SGTuple2d[] valueArray = new SGTuple2d[num];
		for (int ii = 0; ii < valueArray.length; ii++) {
			valueArray[ii] = new SGTuple2d(xValueArray[ii] + dataShiftX, yValueArray[ii] + dataShiftY);
		}
		return valueArray;
    }

    private SGTuple2d calcLineValue(final double xValue, final double yValue) {
		final double dataShiftX = this.getShiftX();
		final double dataShiftY = this.getShiftY();
		return new SGTuple2d(xValue + dataShiftX, yValue + dataShiftY);
    }

    private SGTuple2f[] calcLinePointsArray(final double[] xValueArray, final double[] yValueArray) {
    	SGTuple2d[] valueArray = this.calcLineValues(xValueArray, yValueArray);
    	return this.calcPointsArray(valueArray);
    }
    
    private SGTuple2d[] calcBarValues(final double[] xValueArray, final double[] yValueArray) {
		final double barOffsetX = this.getBarOffsetX();
		final double barOffsetY = this.getBarOffsetY();
    	SGTuple2d[] valueArray = this.calcLineValues(xValueArray, yValueArray);
    	for (int ii = 0; ii < valueArray.length; ii++) {
    		valueArray[ii].x += barOffsetX;
    		valueArray[ii].y += barOffsetY;
    	}
    	return valueArray;
    }

    private SGTuple2d calcBarValue(final double xValue, final double yValue) {
		final double barOffsetX = this.getBarOffsetX();
		final double barOffsetY = this.getBarOffsetY();
    	SGTuple2d value = this.calcLineValue(xValue, yValue);
		value.x += barOffsetX;
		value.y += barOffsetY;
		return value;
    }

    private SGTuple2f[] calcBarPointsArray(final double[] xValueArray, final double[] yValueArray) {
    	SGTuple2d[] valueArray = this.calcBarValues(xValueArray, yValueArray);
    	return this.calcPointsArray(valueArray);
    }

    private SGTuple2f[] calcPointsArray(SGTuple2d[] valueArray) {
    	final int num = valueArray.length;
		final SGAxis xAxis = this.getXAxis();
		final SGAxis yAxis = this.getYAxis();
    	SGTuple2f[] pointsArray = new SGTuple2f[num];
    	for (int ii = 0; ii < num; ii++) {
    		pointsArray[ii] = new SGTuple2f();
    	}
		if (!this.mGraph.calcLocationOfPoints(valueArray, xAxis, yAxis, pointsArray)) {
			return null;
		}
		return pointsArray;
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
    	SGElementGroup group = this.getElementGroupAt(x, y);
    	return (group != null);
    }

	@Override
	public void setFocusedValueIndices(SGDataViewerDialog dg) {
		// do nothing
	}

	@Override
    protected void setFocusedValueIndices(final SGDataViewerDialog dg,
    		List<SGXYSimpleIndexBlock> blockList) {
		// do nothing
	}
	
	protected void setFocusedValueIndices(SGDataViewerDialog dg,
			List<Integer> indexList, int[] arraySectionIndices) {
    	// refresh the map
		this.mFocusedValueListMap.put(dg, new ArrayList<SGTuple2d>());
    	List<SGTuple2d> focusedValueList = this.mFocusedValueListMap.get(dg);
    	
		List<Integer> pointIndexList = new ArrayList<Integer>();
    	int focusedCnt = 0;
    	for (int ii = 0; ii < arraySectionIndices.length; ii++) {
    		if (indexList.get(focusedCnt) == arraySectionIndices[ii]) {
    			pointIndexList.add(ii);
        		focusedCnt++;
        		if (focusedCnt >= indexList.size()) {
        			break;
        		}
    		}
    	}

    	final boolean lineSymbolVisible = this.isLineVisible() || this.isSymbolVisible();
    	if (lineSymbolVisible) {
    		for (int pointIndex : pointIndexList) {
    			SGTuple2d value = this.calcLineValue(this.mXValuesArray[pointIndex], this.mYValuesArray[pointIndex]);
    			focusedValueList.add(value);
    		}
    	}
    	if (this.isBarVisible()) {
			SGElementGroupBarInGraph barGroup = (SGElementGroupBarInGraph) this.getBarGroup();
			SGDrawingElement[] barArray = barGroup.getDrawingElementArray();
    		for (int pointIndex : pointIndexList) {
    			BarInGraph bar = (BarInGraph) barArray[pointIndex];
    			Rectangle2D rect = bar.getElementBounds();
    			SGTuple2d value = this.calcBarValue(this.mXValuesArray[pointIndex], this.mYValuesArray[pointIndex]);
    			if (bar.isVertical()) {
    				value.x = this.mGraph.calcValue((float) rect.getCenterX(), this.mXAxis, true);
    			} else {
    				value.y = this.mGraph.calcValue((float) rect.getCenterY(), this.mYAxis, false);
    			}
    			focusedValueList.add(value);
    		}
    	}
	}
	
	@Override
	protected String getToolTipText(final int x, final int y) {
        Rectangle2D graphRect = this.mGraph.getGraphRect();
        List<Integer> indexList = null;
        String spatiallyVariedText = null;
        if (graphRect.contains(x, y)) {
            SGElementGroupLineInGraph lineGroup = (SGElementGroupLineInGraph) this.getLineGroup();
            SGElementGroupSymbolInGraph symbolGroup = (SGElementGroupSymbolInGraph) this.getSymbolGroup();
            SGElementGroupBarInGraph barGroup = (SGElementGroupBarInGraph) this.getBarGroup();
            SGElementGroupErrorBarInGraph errorBarGroup = (SGElementGroupErrorBarInGraph) this.getErrorBarGroup();
            
    		SGData data = this.getData();
            SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
            final double[] xValueArray = dataSXY.getXValueArray(false);
            final double[] yValueArray = dataSXY.getYValueArray(false);
            
            SGTuple2f[] linePoints = null;
            if (lineGroup.isVisible() || symbolGroup.isVisible() 
            		|| (errorBarGroup.isVisible() && errorBarGroup.isPositionOnLine())) {
                linePoints = this.calcLinePointsArray(xValueArray, yValueArray);
            }

            if (symbolGroup.isVisible()) {
				indexList = symbolGroup.getIndicesAt(x, y, linePoints,
						xValueArray, yValueArray);
				if (indexList != null) {
	            	spatiallyVariedText = symbolGroup.getToolTipTextSpatiallyVaried(indexList);
            		return this.getToolTipTextReturned(spatiallyVariedText, indexList);
				}
            }

            SGTuple2f[] barPoints = null;
            if (barGroup.isVisible() 
            		|| (this.isErrorBarAvailable() && errorBarGroup.isVisible() 
            				&& !errorBarGroup.isPositionOnLine())) {
    	        barPoints = this.calcBarPointsArray(xValueArray, yValueArray);
            }

            if (this.isErrorBarAvailable()) {
                SGTuple2f[] errorBarPoints = errorBarGroup.isPositionOnLine() ? linePoints : barPoints;
            	if (errorBarGroup.isVisible()) {
    				indexList = errorBarGroup.getIndicesAt(x, y, errorBarPoints,
    						xValueArray, yValueArray);
    				if (indexList != null) {
        	        	spatiallyVariedText = errorBarGroup.getToolTipTextSpatiallyVaried(indexList);
    	        		return this.getToolTipTextReturned(spatiallyVariedText, indexList);
    				}
            	}
            }

            if (barGroup.isVisible()) {
				indexList = barGroup.getIndicesAt(x, y, barPoints,
						xValueArray, yValueArray);
				if (indexList != null) {
	            	spatiallyVariedText = barGroup.getToolTipTextSpatiallyVaried(indexList);
            		return this.getToolTipTextReturned(spatiallyVariedText, indexList);
				}
            }

            if (lineGroup.isVisible()) {
				indexList = lineGroup.getIndicesAt(x, y, linePoints,
						xValueArray, yValueArray);
				if (indexList != null) {
	            	spatiallyVariedText = lineGroup.getToolTipTextSpatiallyVaried(indexList);
            		return this.getToolTipTextReturned(spatiallyVariedText, indexList);
				}
            }
        }
        
        return null;
	}
	
	private SXYSingleDataValue getShiftedValue(SGISXYTypeSingleData data,
			SXYSingleDataValue value) {
		SGTuple2d shift = data.getShift();
		SXYSingleDataValue ret = new SXYSingleDataValue();
		ret.xValue = value.xValue + shift.x;
		ret.yValue = value.yValue + shift.y;
		return ret;
	}
	
	private String getDataValueTooltipText(final int index) {
		SGISXYTypeSingleData data = (SGISXYTypeSingleData) this.getData();
		SXYSingleDataValue value = (SXYSingleDataValue) data.getDataValue(index);
		value = this.getShiftedValue(data, value);
		final double xValue = SGUtilityNumber.getNumberInRangeOrder(
				value.xValue, this.mXAxis);
		final double yValue = SGUtilityNumber.getNumberInRangeOrder(
				value.yValue, this.mYAxis);
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(xValue);
		sb.append(", ");
		sb.append(yValue);
		sb.append(")");
		return sb.toString();
	}

	private String getDataValueTooltipText(final int index0, final int index1) {
		SGISXYTypeSingleData data = (SGISXYTypeSingleData) this.getData();
		SXYSingleDataValue value0 = (SXYSingleDataValue) data.getDataValue(index0);
		value0 = this.getShiftedValue(data, value0);
		SXYSingleDataValue value1 = (SXYSingleDataValue) data.getDataValue(index1);
		value1 = this.getShiftedValue(data, value1);
		final double xValue0 = SGUtilityNumber.getNumberInRangeOrder(
				value0.xValue, this.mXAxis);
		final double yValue0 = SGUtilityNumber.getNumberInRangeOrder(
				value0.yValue, this.mYAxis);
		final double xValue1 = SGUtilityNumber.getNumberInRangeOrder(
				value1.xValue, this.mXAxis);
		final double yValue1 = SGUtilityNumber.getNumberInRangeOrder(
				value1.yValue, this.mYAxis);
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(xValue0);
		sb.append(", ");
		sb.append(yValue0);
		sb.append(") - (");
		sb.append(xValue1);
		sb.append(", ");
		sb.append(yValue1);
		sb.append(")");
		return sb.toString();
	}

	private String getToolTipTextReturned(final String spatiallyVariedText, 
			List<Integer> indices) {
		SGArrayData data = (SGArrayData) this.getData();
		
		StringBuffer sbSpatiallyVaried = new StringBuffer();
		if (data instanceof SGSDArrayData) {
			if (this.mNumberOfSeries > 1) {
				// append the index of child data only for SDArray data
				sbSpatiallyVaried.append(this.mSeriesIndex);
				sbSpatiallyVaried.append(": ");
			}
		}
		sbSpatiallyVaried.append(spatiallyVariedText);
		
		String dataValueText = null;
		if (indices.size() == 1) {
			final int index = indices.get(0);
			dataValueText = this.getDataValueTooltipText(index);
		} else if (indices.size() == 2) {
			final int index0 = indices.get(0);
			final int index1 = indices.get(1);
			dataValueText = this.getDataValueTooltipText(index0, index1);
		}
		String notSpatiallyVariedText = data.getToolTipTextNotSpatiallyVaried();
		
		return this.createToolTipText(sbSpatiallyVaried.toString(),
				notSpatiallyVariedText, dataValueText);
	}

	@Override
    protected List<SGTwoDimensionalArrayIndex> getDataViewerIndex(final int x, final int y) {
		// always returns null
		return null;
	}
	
    protected List<Integer> getDataViewerRowIndex(final int x, final int y) {
        Rectangle2D graphRect = this.mGraph.getGraphRect();
        List<Integer> ret = null;
        
        if (graphRect.contains(x, y)) {
        	
            SGElementGroupLineInGraph lineGroup = (SGElementGroupLineInGraph) this.getLineGroup();
            SGElementGroupSymbolInGraph symbolGroup = (SGElementGroupSymbolInGraph) this.getSymbolGroup();
            SGElementGroupBarInGraph barGroup = (SGElementGroupBarInGraph) this.getBarGroup();
            SGElementGroupErrorBarInGraph errorBarGroup = (SGElementGroupErrorBarInGraph) this.getErrorBarGroup();
            
    		SGData data = this.getData();
            SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
            final double[] xValueArray = dataSXY.getXValueArray(false);
            final double[] yValueArray = dataSXY.getYValueArray(false);
            
            SGTuple2f[] linePoints = null;
            if (lineGroup.isVisible() || symbolGroup.isVisible() 
            		|| (errorBarGroup.isVisible() && errorBarGroup.isPositionOnLine())) {
                linePoints = this.calcLinePointsArray(xValueArray, yValueArray);
            }

            if (symbolGroup.isVisible()) {
				ret = symbolGroup.getIndicesAt(x, y, linePoints, xValueArray,
						yValueArray);
            	if (ret != null) {
	        		return ret;
	        	}
            }

            SGTuple2f[] barPoints = null;
            if (barGroup.isVisible() 
            		|| (this.isErrorBarAvailable() && errorBarGroup.isVisible() 
            				&& !errorBarGroup.isPositionOnLine())) {
    	        barPoints = this.calcBarPointsArray(xValueArray, yValueArray);
            }

            if (this.isErrorBarAvailable()) {
                SGTuple2f[] errorBarPoints = errorBarGroup.isPositionOnLine() ? linePoints : barPoints;
            	if (errorBarGroup.isVisible()) {
					ret = errorBarGroup.getIndicesAt(x, y, errorBarPoints,
							xValueArray, yValueArray);
	            	if (ret != null) {
		        		return ret;
    	        	}
            	}
            }

            if (barGroup.isVisible()) {
				ret = barGroup.getIndicesAt(x, y, barPoints, xValueArray,
						yValueArray);
            	if (ret != null) {
	        		return ret;
	        	}
            }

            if (lineGroup.isVisible()) {
				ret = lineGroup.getIndicesAt(x, y, linePoints, xValueArray,
						yValueArray);
            	if (ret != null) {
	        		return ret;
	        	}
            }
        }

		return null;
	}

	@Override
	public String getAxisValueString(int x, int y) {
		// always returns null
		return null;
	}
	
}
