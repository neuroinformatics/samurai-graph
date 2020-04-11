package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGData.DataProperties;
import jp.riken.brain.ni.samuraigraph.base.SGDataAxisInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementForData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGISelectable;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPlotTypeConstants;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeSingleData;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIVXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGIXYData;
import jp.riken.brain.ni.samuraigraph.data.SGIXYZData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager;
import jp.riken.brain.ni.samuraigraph.figure.SGColorMapManager.HueColorMap;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGIArrowConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIColorMapConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIErrorBarConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureDrawingElementConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGILineConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGISymbolConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGITickLabelConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGIVXYDataConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGLineStyle;
import jp.riken.brain.ni.samuraigraph.figure.SGUtilityForFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetForData.ElementGroupSetPropertiesInFigureElement;
import jp.riken.brain.ni.samuraigraph.figure.java2d.SGElementGroupSetInGraphSXYMultiple.MultipleSXYElementGroupSetPropertiesInFigureElement;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * The base class of figure element which has data object and related element group set.
 *
 */
public abstract class SGFigureElementForData extends SGFigureElement2D implements
    SGIFigureDrawingElementConstants, SGIFigureElementForData,
    SGIDrawingElementConstants, SGISXYDataConstants, SGIVXYDataConstants {

    /**
     * The axis element.
     */
    protected SGIFigureElementAxis mAxisElement = null;

    /**
     * Default scale reference.
     */
    public static final String DEFAULT_SCALE_REFERENCE = SGIFigureElementAxis.LEFT_BOTTOM;

    /**
     * A map of property dialogs for data.
     */
    private HashMap mDataDialogMap = new HashMap();

    private static final String DIALOG_KEY_SXY = "SXY";

    private static final String DIALOG_KEY_VXY = "VXY";

    private static final String DIALOG_KEY_SXYZ = "SXYZ";

    /**
     * The default constructor.
     *
     */
    public SGFigureElementForData() {
	super();
    }

    /**
     * Returns the group set object related to the given data object.
     * @param data
     *             a data object
     * @return
     *             A group set object related to the given data object.
     *             If it does not exist, returns null.
     */
    protected SGElementGroupSetForData getElementGroupSet(SGData data) {
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGElementGroupSetForData groupSet
                = (SGElementGroupSetForData) this.mChildList.get(ii);
            if (groupSet.getData().equals(data)) {
                return groupSet;
            }
        }
        return null;
    }

    /**
     * Returns the data object related to the given group set object.
     * @param groupSet
     *             a group set object
     * @return
     *             A data object related to the given group set object.
     *             If it does not exist, returns null.
     */
    protected SGData getData(SGElementGroupSetForData groupSet) {
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGElementGroupSetForData gs
            	= (SGElementGroupSetForData) this.mChildList.get(ii);
            if (gs.contains(groupSet)) {
                SGData data = groupSet.getData();
                return data;
            }
        }
        return null;
    }

    /**
     * Returns the name of a given data object.
     * @param data
     *             the data object to get the name
     * @return
     *             the name of a given data object
     */
    public String getDataName(final SGData data) {
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet != null) {
            return groupSet.getName();
        }
        return null;
    }

    /**
     * Set the name of a data object.
     * @param name
     *             the name to set
     * @param data
     *             the data object to set the name
     * @return
     *         true if succeeded
     */
    public boolean setDataName(String name, SGData data) {
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet != null) {
            groupSet.setName(name);
            return true;
        }
        return false;
    }

    /**
     * Returns properties of a given data object.
     * @param data
     *             a data object
     * @return
     *             the properties
     */
    public SGProperties getDataProperties(SGData data) {
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet != null) {
            return groupSet.getProperties();
        }
        return null;
    }

    /**
     * Returns whether a given data is visible.
     * @param data
     *             a data object
     * @return
     *             true if a given data is visible
     */
    public boolean isDataVisible(SGData data) {
    	SGElementGroupSetForData groupSet = (SGElementGroupSetForData) this.getElementGroupSet(data);
        return groupSet.isVisible();
    }

    /**
     * Returns whether a given data is visible in legend.
     *
     * @param data
     *           a data
     * @return true if a given data is visible in legend
     */
    public boolean isDataVisibleInLegend(final SGData data) {
    	SGElementGroupSetForData groupSet = (SGElementGroupSetForData) this.getElementGroupSet(data);
        return groupSet.isVisibleInLegend();
    }

    /**
     * Sets the visibility of a given data.
     * @param data
     *             a data object
     * @param b
     *             visibility to set to the data
     * @return
     *             true if succeeded
     */
    public boolean setDataVisible(final SGData data, final boolean b) {
    	SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        groupSet.setVisible(b);
        return true;
    }

    /**
     * Returns the list of visible data objects.
     *
     * @return a list of visible data objects
     */
    public List<SGData> getVisibleDataList() {
    	List<SGData> list = new ArrayList<SGData>();
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGElementGroupSetForData groupSet
                = (SGElementGroupSetForData) this.mChildList.get(ii);
            if (groupSet.isVisible()) {
	        	SGData data = groupSet.getData();
	        	list.add(data);
            }
        }
        return list;
    }

    /**
     * Returns the list of visible flags of drawing elements.
     * @param data
     *             a data object
     * @return
     *             the list of visible flags of drawing elements
     */
    public List<Boolean> getVisibleFlagList(final SGData data) {
        if (data == null) {
            throw new IllegalArgumentException("data==null");
        }
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet == null) {
            throw new Error("group set is not found");
        }
        List<Boolean> list = groupSet.getVisibleFlagList();
        return list;
    }

    /**
     * Returns whether a given data is visible in the legend.
     * @param data
     *             a data object
     * @return
     *             true if a given data object is visible in the legend
     */
    public boolean getVisibleInLegendFlag(SGData data) {
        if (data == null) {
            throw new IllegalArgumentException("data==null");
        }
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet == null) {
            throw new Error("group set is not found");
        }
        boolean flag = groupSet.isVisibleInLegend();
        return flag;
    }

    /**
     * Returns the X axis for a given data.
     * @param data
     *             a data obejct
     * @return
     *             an axis object related to this data for the X-direction
     */
    public SGAxis getXAxis(final SGData data) {
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet != null) {
            return groupSet.getXAxis();
        }
        return null;
    }

    /**
     * Returns the Y axis for a given data.
     * @param data
     *             a data obejct
     * @return
     *             an axis object related to this data for the Y-direction
     */
    public SGAxis getYAxis(final SGData data) {
        final SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        if (groupSet != null) {
            return groupSet.getYAxis();
        }
        return null;
    }

    /**
     * Returns a list of useless data objects in this figure element.
     *
     * @return a list of useless data objects in this figure element
     */
    public List<SGData> getUselessDataList() {
		List<SGData> dataList = new ArrayList<SGData>(this.mDataList);
		Set cSet = this.getAvailableChildSet();
		Iterator itr = cSet.iterator();
		while (itr.hasNext()) {
		    SGElementGroupSetForData gs
		    	= (SGElementGroupSetForData)itr.next();
		    SGData data = gs.getData();
		    dataList.remove(data);
		}
		return dataList;
    }

    /**
     * Returns the property dialog of data.
     * @param dataType
     *                the type of data
     * @return
     *                the property dialog for given data type
     */
    public SGPropertyDialog getDataDialog(SGData data) {

        String key = null;
        if (data instanceof SGISXYTypeData) {
            key = DIALOG_KEY_SXY;
        } else if (data instanceof SGIVXYTypeData) {
            key = DIALOG_KEY_VXY;
        } else if (data instanceof SGISXYZTypeData) {
            key = DIALOG_KEY_SXYZ;
        } else {
            throw new Error("Unsupported data type: " + data.getDataType());
        }

        Object obj = mDataDialogMap.get(key);
        SGPropertyDialog dg = null;
        if (obj != null) {
            dg = (SGPropertyDialog) obj;
        } else {
            if (DIALOG_KEY_SXY.equals(key)) {
                dg = new SGPropertyDialogSXYData(this.mDialogOwner, true);
            } else if (DIALOG_KEY_VXY.equals(key)) {
                dg = new SGPropertyDialogVXYData(this.mDialogOwner, true);
            } else if (DIALOG_KEY_SXYZ.equals(key)) {
                dg = new SGPropertyDialogSXYZData(this.mDialogOwner, true);
            }
            mDataDialogMap.put(key, dg);
        }
        return dg;
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();

        // dispose dialog map
        Iterator itr = this.mDataDialogMap.keySet().iterator();
        while (itr.hasNext()) {
            Object key = itr.next();
            SGPropertyDialog dg = (SGPropertyDialog) this.mDataDialogMap
                    .get(key);
            dg.dispose();
        }
        this.mDataDialogMap.clear();
        this.mDataDialogMap = null;
    }

    /**
     * Create an instance of element group set in this figure element.
     *
     * @param data
     *            related data
     * @param name
     *            the name of data
     * @param infoMap
     *            information map
     * @return created object
     */
    protected SGIElementGroupSetForData createGroupSet(SGData data, String name, Map<String, 
    		Object> infoMap) {

        // get axes
        final SGAxis bAxis = this.mAxisElement
                .getAxisInPlane(SGIFigureElementAxis.AXIS_HORIZONTAL_1);
        final SGAxis tAxis = this.mAxisElement
                .getAxisInPlane(SGIFigureElementAxis.AXIS_HORIZONTAL_2);
        final SGAxis lAxis = this.mAxisElement
                .getAxisInPlane(SGIFigureElementAxis.AXIS_VERTICAL_1);
        final SGAxis rAxis = this.mAxisElement
                .getAxisInPlane(SGIFigureElementAxis.AXIS_VERTICAL_2);
        SGAxis axisX = null;
        SGAxis axisY = null;
        if (DEFAULT_SCALE_REFERENCE.equals(SGIFigureElementAxis.LEFT_BOTTOM)) {
            axisX = bAxis;
            axisY = lAxis;
        } else if (DEFAULT_SCALE_REFERENCE
                .equals(SGIFigureElementAxis.LEFT_TOP)) {
            axisX = tAxis;
            axisY = lAxis;
        } else if (DEFAULT_SCALE_REFERENCE
                .equals(SGIFigureElementAxis.RIGHT_BOTTOM)) {
            axisX = bAxis;
            axisY = rAxis;
        } else if (DEFAULT_SCALE_REFERENCE
                .equals(SGIFigureElementAxis.RIGHT_TOP)) {
            axisX = tAxis;
            axisY = rAxis;
        } else {
            return null;
        }

        // create an instance
        SGIElementGroupSetForData groupSet = null;
        if (data instanceof SGISXYTypeSingleData) {
        	SGIElementGroupSetSXY groupSetSXY = this.createGroupSetSXY(
        			(SGISXYTypeSingleData) data, axisX, axisY, false, name);
            this.setInfoMapToGroupSet(groupSetSXY, infoMap);
        	groupSet = groupSetSXY;
        } else if (data instanceof SGISXYTypeMultipleData) {
        	SGIElementGroupSetMultipleSXY groupSetSXY = this.createGroupSetMultipleSXY(
                    (SGISXYTypeMultipleData) data, axisX, axisY, name);
            this.setInfoMapToGroupSet(groupSetSXY, infoMap);
            // initializes the line style of child objects
            if (groupSetSXY.initChildLineStyle() == false) {
                return null;
            }
            groupSet = groupSetSXY;
        } else if (data instanceof SGIVXYTypeData) {
            groupSet = this.createGroupSetVXY((SGIVXYTypeData) data, axisX,
                    axisY, name);
        } else if (data instanceof SGISXYZTypeData) {
            SGAxis axisZ = this.mAxisElement.getZAxis();
            groupSet = this.createGroupSetSXYZ((SGISXYZTypeData) data, axisX,
                    axisY, axisZ, name);
        } else {
            throw new Error("Not supported data type: " + data.getDataType());
        }

        if (groupSet == null) {
            return null;
        }

        // set magnification
        groupSet.setMagnification(this.getMagnification());

        return groupSet;
    }
    
    private void setInfoMapToGroupSet(SGIElementGroupSetXY groupSet, Map<String, Object> map) {
        if (map==null) {
            return;
        }

        boolean isLineVisible = true;
        boolean isSymbolVisible = false;
        boolean isBarVisible = false;
        boolean isErrorBarOnLine = true;
        boolean isLineColorAutoAssign = false;

		Object o = map.get(SGPlotTypeConstants.KEY_PLOT_TYPE_LINE);
		if (o != null && o instanceof Boolean) {
			isLineVisible = ((Boolean) o).booleanValue();
		}
		o = map.get(SGPlotTypeConstants.KEY_PLOT_TYPE_SYMBOL);
		if (o != null && o instanceof Boolean) {
			isSymbolVisible = ((Boolean) o).booleanValue();
		}
		o = map.get(SGPlotTypeConstants.KEY_PLOT_TYPE_BAR);
		if (o != null && o instanceof Boolean) {
			isBarVisible = ((Boolean) o).booleanValue();
		}
		o = map.get(SGPlotTypeConstants.KEY_PLOT_TYPE_ERRORBAR_ONLINE);
		if (o != null && o instanceof Boolean) {
			isErrorBarOnLine = ((Boolean) o).booleanValue();
		}
        o = map.get(SGPlotTypeConstants.KEY_PLOT_TYPE_LINE_COLOR_AUTO_ASSIGNED);
        if (o != null && o instanceof Boolean) {
        	isLineColorAutoAssign = ((Boolean) o).booleanValue();
        }

        groupSet.setLineVisible(isLineVisible);
        groupSet.setSymbolVisible(isSymbolVisible);
        groupSet.setBarVisible(isBarVisible);
        groupSet.setBarWidthValue(groupSet.getBarWidthValue());
        groupSet.setErrorBarOnLinePosition(isErrorBarOnLine);
        groupSet.setLineColorAutoAssigned(isLineColorAutoAssign);

        return;
    }

    // create SXY groupSet
    protected SGIElementGroupSetSXY createGroupSetSXY(
            final SGISXYTypeSingleData data, final SGAxis axisX, final SGAxis axisY,
            final boolean multiple, final String name) {
        SGIElementGroupSetSXY groupSet = this.createSingleGroupSetSXY(
                data, axisX, axisY, multiple);
        if (groupSet == null) {
        	return null;
        }
        groupSet.setName(name);
        return groupSet;
    }

    // create an instance of SXY groupSet
    protected SGIElementGroupSetSXY createSingleGroupSetSXY(
            final SGISXYTypeSingleData data, final SGAxis axisX, final SGAxis axisY,
            final boolean multiple) {

        final SGIElementGroupSetSXY groupSet
            = this.createSXYGroupSetInstance(data);
        if (groupSet == null) {
        	return null;
        }

        // set axes
        groupSet.setXAxis(axisX);
        groupSet.setYAxis(axisY);

        // add drawing element groups of lines, symbols and bars
        if (groupSet.addDrawingElementGroup(SGIElementGroupConstants.RECTANGLE_GROUP) == false) {
            return null;
        }

        // set bar width from the interval of x values and graph width
        if (!multiple) {
            SGElementGroupBar groupBar = (SGElementGroupBar) groupSet.getBarGroup();
			if (data != null) {
				final boolean vertical = groupBar.isVertical();
				final double[] valueArray = vertical ? data.getXValueArray(false)
						: data.getYValueArray(false);
				final SGAxis axis = vertical ? axisX : axisY;
				final double widthValue = this.calcDefaultBarWidth(valueArray,
						axis);
				groupBar.setWidthValue(widthValue);
			}
        }

        // create error bar instance
        groupSet.createErrorBars(data);

        if (groupSet
                .addDrawingElementGroup(SGIElementGroupConstants.POLYLINE_GROUP) == false) {
            return null;
        }

        if (groupSet
                .addDrawingElementGroup(SGIElementGroupConstants.SYMBOL_GROUP) == false) {
            return null;
        }

        // create tick label instance
        SGElementGroupTickLabel tickLabels = groupSet.createTickLabels(data);

        if (!multiple) {
            // set decimal places and exponents of data object to tick labels
            final int dp = data.getDecimalPlaces();
            final int exp = data.getExponent();
            tickLabels.setDecimalPlaces(dp);
            tickLabels.setExponent(exp);
        }

        return groupSet;
    }

    private double calcDefaultBarWidth(final double[] xArray, final SGAxis axisX) {
        double minDiff = Double.MAX_VALUE;
        for (int ii = 0; ii < xArray.length - 1; ii++) {
            final double diff = Math.abs(xArray[ii + 1] - xArray[ii]);
            if (diff < minDiff) {
                minDiff = diff;
            }
        }
        minDiff = SGUtilityNumber.getNumberInRangeOrder(minDiff, axisX);
        return minDiff;
    }

    /**
     * Return bar width of a given groupSet.
     * @param gs
     * @return
     */
    protected double getBarWidthOfBarSeries(SGIElementGroupSetXY gs) {
        final double[] valueArray;
        SGElementGroupBar bar;
        SGData data;
        boolean vertical;
        SGAxis axis;

        if (gs instanceof SGElementGroupSetInGraphSXY) {
            SGElementGroupSetInGraphSXY egs = (SGElementGroupSetInGraphSXY)gs;
            data = egs.getData();
            bar = egs.getBarGroup();
            vertical = bar.isVertical();
            axis = vertical ? egs.getXAxis() : egs.getYAxis();
        } else if (gs instanceof SGElementGroupSetInGraphSXYMultiple) {
        	SGElementGroupSetInGraphSXYMultiple egs = (SGElementGroupSetInGraphSXYMultiple)gs;
            data = egs.getData();
            bar = egs.getBarGroup();
            vertical = bar.isVertical();
            axis = vertical ? egs.getXAxis() : egs.getYAxis();
        } else {
            return Double.NaN;
        }

        if (bar.isVisible()) {
			if (data instanceof SGISXYTypeSingleData) {
				valueArray = vertical ? ((SGISXYTypeSingleData) data)
						.getXValueArray(false) : ((SGISXYTypeSingleData) data)
						.getYValueArray(false);
			} else if (data instanceof SGISXYTypeMultipleData) {
				valueArray = vertical ? ((SGISXYTypeMultipleData) data)
						.getXValueArray(false)[0]
						: ((SGISXYTypeMultipleData) data).getYValueArray(false)[0];
			} else {
				return Double.NaN;
			}
        } else {
            return Double.NaN;
        }
        double widthValue = this.calcDefaultBarWidth(valueArray, axis);

        widthValue = SGUtilityNumber.getNumberInRangeOrder(widthValue, axis);
        return widthValue;
    }

    /**
     * Return bar width from the interval of x values and graph width.
     * @param groupBar
     * @param barGroups
     * @param data
     * @param axisX
     * @param axisY
     * @return calculated bar width
     */
    public double getAutoArrangementOfBarWidth(
            final SGElementGroupBar groupBar, final int numberOfBars,
            final SGISXYTypeMultipleData data, final SGAxis axisX, final SGAxis axisY) {
        final boolean vertical = groupBar.isVertical();
        final double[][] valueArray = vertical ? data.getXValueArray(false) : data.getYValueArray(false);
        final SGAxis axis = vertical ? axisX : axisY;

        double widthValue = this.calcDefaultBarWidth(valueArray[0], axis);

        if (numberOfBars>=1) {
            widthValue = widthValue/(numberOfBars+1);
        }

        return widthValue;
    }

    /**
     * Return bar width from the interval of x values and graph width.
     *
     * @param groupBar
     * @param numberOfBars
     * @param data
     * @param axisX
     * @param axisY
     * @return calculated bar width
     */
    public double getAutoArrangementOfBarWidth(
            final SGElementGroupBar groupBar, final int numberOfBars,
            final SGISXYTypeSingleData data, final SGAxis axisX, final SGAxis axisY) {
        final boolean vertical = groupBar.isVertical();
        final double[] valueArray = vertical ? data.getXValueArray(false) : data.getYValueArray(false);
        final SGAxis axis = vertical ? axisX : axisY;

        double widthValue = this.calcDefaultBarWidth(valueArray, axis);

        if (numberOfBars>=1) {
            widthValue = widthValue/(numberOfBars+1);
        }

        return widthValue;
    }

    // create VXY groupSet
    protected SGIElementGroupSetVXY createGroupSetVXY(
            final SGIVXYTypeData data, final SGAxis axisX, final SGAxis axisY,
            final String name) {
        SGIElementGroupSetVXY groupSet = this.createSingleGroupSetVXY(
                data, axisX, axisY);
        if (groupSet == null) {
        	return null;
        }
        groupSet.setName(name);
        return groupSet;
    }

    // create SXYZ groupSet
    protected SGIElementGroupSetSXYZ createGroupSetSXYZ(
            final SGISXYZTypeData data, final SGAxis axisX, final SGAxis axisY,
            final String name) {
        SGIElementGroupSetSXYZ groupSet = this.createSingleGroupSetSXYZ(
                data, axisX, axisY);
        if (groupSet == null) {
        	return null;
        }
        groupSet.setName(name);
        return groupSet;
    }

    // create an instance of VXY groupSet
    protected SGIElementGroupSetVXY createSingleGroupSetVXY(
            final SGIVXYTypeData data, final SGAxis axisX, final SGAxis axisY) {

        final SGIElementGroupSetVXY groupSet
            = this.createVXYGroupSetInstance(data);
        if (groupSet == null) {
        	return null;
        }

        // set axes
        groupSet.setXAxis(axisX);
        groupSet.setYAxis(axisY);

        // add drawing element groups of arrows
        if (groupSet
                .addDrawingElementGroup(SGIElementGroupConstants.ARROW_GROUP) == false) {
            return null;
        }

        // set the magnitude per cm
        final float percmReduced = this.getInitialMagnitudePerCM(data);
        groupSet.setMagnitudePerCM(percmReduced);
        groupSet.setDirectionInvariant(data.isPolar());

        return groupSet;
    }

    /**
     * Calculates the initial magnitude of the vector per centimeter.
     *
     * @param data
     *           the vector data
     * @return the initial magnitude of the vector per centimeter
     */
    protected float getInitialMagnitudePerCM(SGIVXYTypeData data) {
        final float mag = SGDataUtility.getInitialMagnitudePerCM(data);
    	return mag;
    }

    // create an instance of SXYZ groupSet
    protected SGIElementGroupSetSXYZ createSingleGroupSetSXYZ(
            final SGISXYZTypeData data, final SGAxis axisX, final SGAxis axisY) {

        final SGIElementGroupSetSXYZ groupSet
            = this.createSXYZGroupSetInstance(data);
        if (groupSet == null) {
        	return null;
        }

        // set axes
        groupSet.setXAxis(axisX);
        groupSet.setYAxis(axisY);

        // add drawing element groups of rectangles
        if (groupSet
                .addDrawingElementGroup(SGIElementGroupConstants.RECTANGLE_GROUP) == false) {
            return null;
        }

        if (groupSet.updateColorMapSize() == false) {
            return null;
        }

        return groupSet;
    }

    // create multiple SXY groupSet
    protected SGIElementGroupSetMultipleSXY createGroupSetMultipleSXY(
            final SGISXYTypeMultipleData data, final SGAxis axisX,
            final SGAxis axisY, final String name) {

        final SGIElementGroupSetMultipleSXY groupSet
            = this.createMultipleSXYGroupSetInstance(data);
        if (groupSet == null) {
            return null;
        }

        // set the name
        groupSet.setName(name);

        // set axes
        groupSet.setXAxis(axisX);
        groupSet.setYAxis(axisY);

        // set up child group sets
        if (this.updateChildGroupSet(groupSet) == false) {
        	return null;
        }

        // add element groups to the multiple group set
        if (!groupSet.addDrawingElementGroup(SGIElementGroupConstants.RECTANGLE_GROUP)) {
            return null;
        }

        // set bar width from the interval of x values and graph width
        SGElementGroupBar groupBar = (SGElementGroupBar) groupSet.getBarGroup();
        List<SGElementGroupBar> barGroups = groupSet.getBarGroups();
        final int numberOfBars = barGroups.size()-1;
        double barWidth = this.getAutoArrangementOfBarWidth(groupBar, numberOfBars, data, axisX, axisY);
        if (barWidth!=0.0) {
            int digit = BAR_WIDTH_INITIAL_ORDER-1;
            double widthValue = SGUtilityNumber.roundOffNumber(barWidth, digit);
            int count = 20;
            while (widthValue == 0.0 && count-->0) {
                digit = digit -1;
                widthValue = SGUtilityNumber.roundOffNumber(barWidth, digit);
            }
            barWidth = widthValue;
        }
        final double intervalValue = barWidth;
        groupBar.setWidthValue(barWidth);
        groupBar.setInterval(intervalValue);
        for (int ii = 0; ii < barGroups.size(); ii++) {
            SGElementGroupBar barGroup = barGroups.get(ii);
            barGroup.setWidthValue(barWidth);
            barGroup.setInterval(intervalValue);
        }

        // create error bars
        groupSet.createErrorBars(data);
        Boolean errorBarVertical = data.isErrorBarVertical();
        if (errorBarVertical != null) {
        	groupSet.setErrorBarDirection(errorBarVertical.booleanValue());
        }

        if (!groupSet.addDrawingElementGroup(SGIElementGroupConstants.POLYLINE_GROUP)) {
        	return null;
        }
        if (!groupSet.addDrawingElementGroup(SGIElementGroupConstants.SYMBOL_GROUP)) {
        	return null;
        }

        // create tick labels
        groupSet.createTickLabels(data);
        Boolean tickLabelHorizontal = data.isTickLabelHorizontal();
        if (tickLabelHorizontal != null) {
        	groupSet.setTickLabelAlignment(tickLabelHorizontal.booleanValue());
        }

        return groupSet;
    }

    // Sets up child group sets of multiple data.
    protected boolean updateChildGroupSet(SGIElementGroupSetMultipleSXY groupSet) {

    	// get current values
    	final SGISXYTypeMultipleData dataMult = (SGISXYTypeMultipleData) groupSet.getData();
    	final SGAxis axisX = groupSet.getXAxis();
    	final SGAxis axisY = groupSet.getYAxis();
    	final String name = groupSet.getName();
    	final SGIElementGroupSetForData[] gsArray = groupSet.getChildGroupSetArray();

    	// get new data array
        final SGISXYTypeSingleData[] sxyArray = dataMult.getSXYDataArray();

        try {
            for (int ii = 0; ii < gsArray.length; ii++) {
                gsArray[ii].setID(ii + 1);
                ((SGIElementGroupSetSXY) gsArray[ii]).setSeriesIndexAndNumber(ii, sxyArray.length);
            }

            if (gsArray.length < sxyArray.length) {
                // set new data
                for (int ii = 0; ii < gsArray.length; ii++) {
                    SGData d = (SGData) sxyArray[ii];
                    gsArray[ii].setData((SGData) d.copy());
                }

                // create new child group sets
                for (int ii = gsArray.length; ii < sxyArray.length; ii++) {
                	SGData d = (SGData) sxyArray[ii];
                    SGIElementGroupSetSXY gs = this.createSingleGroupSetSXY(
                            (SGISXYTypeSingleData) d.copy(), axisX, axisY, true);
                    if (gs == null) {
                        return false;
                    }
                    gs.setID(ii + 1);
                    gs.setSeriesIndexAndNumber(ii, sxyArray.length);
                    gs.setName(name);
                    groupSet.addChildGroupSet(gs);
                }

            } else {

            	// remove child group set
            	for (int ii = gsArray.length - 1; ii >= sxyArray.length; ii--) {
                    groupSet.removeChildGroupSet(gsArray[ii]);
            	}

                // set new data
                for (int ii = 0; ii < sxyArray.length; ii++) {
                    SGData d = (SGData) sxyArray[ii];
                    gsArray[ii].setData((SGData) d.copy());
                }
            }

        } finally {
            if (sxyArray != null) {
    	    	// disposes of data objects
    	        SGDataUtility.disposeSXYDataArray(sxyArray);
            }
        }

        return true;
    }

    // create SXYZ groupSet
    protected SGIElementGroupSetSXYZ createGroupSetSXYZ(
            final SGISXYZTypeData data, final SGAxis axisX, final SGAxis axisY,
            final SGAxis axisZ, final String name) {
        SGIElementGroupSetSXYZ groupSet = this.createSingleGroupSetSXYZ(
                data, axisX, axisY, axisZ);
        if (groupSet == null) {
        	return null;
        }
        groupSet.setName(name);
        return groupSet;
    }

    // create an instance of SXYZ groupSet
    protected SGIElementGroupSetSXYZ createSingleGroupSetSXYZ(
            final SGISXYZTypeData data, final SGAxis axisX, final SGAxis axisY,
            final SGAxis axisZ) {

        final SGIElementGroupSetSXYZ groupSet
            = this.createSXYZGroupSetInstance(data);
        if (groupSet == null) {
        	return null;
        }

        // set axes
        groupSet.setXAxis(axisX);
        groupSet.setYAxis(axisY);
        groupSet.setZAxis(axisZ);

        // add drawing element groups
        if (groupSet
                .addDrawingElementGroup(SGIElementGroupConstants.RECTANGLE_GROUP) == false) {
            return null;
        }

        if (groupSet.updateColorMapSize() == false) {
        	return null;
        }

        return groupSet;
    }

    protected abstract SGIElementGroupSetSXY createSXYGroupSetInstance(SGISXYTypeSingleData dataSXY);
    protected abstract SGIElementGroupSetVXY createVXYGroupSetInstance(SGIVXYTypeData dataVXY);
    protected abstract SGIElementGroupSetMultipleSXY createMultipleSXYGroupSetInstance(SGISXYTypeMultipleData dataMultSXY);
    protected abstract SGIElementGroupSetSXYZ createSXYZGroupSetInstance(SGISXYZTypeData dataSXYZ);

    /**
     *
     */
    public boolean createDataObject(final Element el, final SGData data, final boolean readDataProperty) {

        if (super.createDataObject(el, data, readDataProperty) == false) {
            return false;
        }

        SGIFigureElementAxis aElement = this.mAxisElement;
        String str = null;

        // name of data
        String name = null;
        str = el.getAttribute(KEY_DATA_NAME);
        if (str.length() == 0) {
            return false;
        }
        name = str;

        // x-axis
        SGAxis axisX = null;
        str = el.getAttribute(KEY_X_AXIS_POSITION);
        if (str.length() != 0) {
            axisX = aElement.getAxis(str);
            if (axisX == null) {
                return false;
            }
        }

        // y-axis
        SGAxis axisY = null;
        str = el.getAttribute(KEY_Y_AXIS_POSITION);
        if (str.length() != 0) {
            axisY = aElement.getAxis(str);
            if (axisY == null) {
                return false;
            }
        }

        // visibility in legend
        Boolean vLegend = Boolean.FALSE;
        str = el.getAttribute(KEY_VISIBLE_IN_LEGEND);
        if (str.length() != 0) {
            Boolean b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return false;
            }
            vLegend = b;
        }
        
        // animation array section
        SGArrayData aData = (SGArrayData) data;
        SGIntegerSeriesSet animationArraySection = null;
        if (aData.isAnimationAvailable()) {
            str = el.getAttribute(KEY_ANIMATION_ARRAY_SECTION);
            if (str.length() != 0) {
                final int animationLength = aData.getAnimationLength();
            	animationArraySection = SGIntegerSeriesSet.parse(str, animationLength);
            }
        }
        
        // frame rate
        Double frameRate = null;
        str = el.getAttribute(KEY_ANIMATION_FRAME_RATE);
        if (str.length() != 0) {
        	frameRate = SGUtilityText.getDouble(str);
        }
        
        // loop play back
        Boolean loopPlayback = null;
        str = el.getAttribute(KEY_ANIMATION_LOOP_PLAYBACK);
        if (str.length() != 0) {
        	loopPlayback = SGUtilityText.getBoolean(str);
        }

        SGIElementGroupSetForData gs = this.createGroupSet(el, data, axisX, axisY, name, readDataProperty);
        if (gs == null) {
            return false;
        }

        // set properties
        if (gs.setVisibleInLegend(vLegend.booleanValue()) == false) {
            return false;
        }
        if (animationArraySection != null) {
            if (SGDataUtility.isNetCDFData(data)) {
            	SGNetCDFData ncData = (SGNetCDFData) data;
            	ncData.setTimeStride(animationArraySection);
            } else if (SGDataUtility.isMDArrayData(data)) {
            	SGMDArrayData mdData = (SGMDArrayData) data;
            	mdData.setTimeStride(animationArraySection);
            }
        }
        if (frameRate != null) {
        	if (frameRate.doubleValue() >= 0.0) {
            	gs.setFrameRate(frameRate.doubleValue());
        	}
        }
        if (loopPlayback != null) {
        	gs.setLoopPlaybackAvailable(loopPlayback.booleanValue());
        }

        // add to the child list
        this.addToList(gs);

        // initialize the property history
        gs.initPropertiesHistory();

        return true;
    }

    protected SGIElementGroupSetForData createGroupSet(final Element el, final SGData data,
            final SGAxis axisX, final SGAxis axisY, final String name, final boolean readDataProperty) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;

        // create an instance
        SGIElementGroupSetForData gs = null;
        if (data instanceof SGISXYTypeSingleData) {
            SGISXYTypeSingleData dataSXY = (SGISXYTypeSingleData) data;
            gs = this.createGroupSetSXY(dataSXY, axisX, axisY, true, name);
            SGIElementGroupSetSXY groupSet = (SGIElementGroupSetSXY) gs;
            if (this.setPropertyOfElementGroupSetSXY(el, groupSet, dataSXY) == ic) {
                return null;
            }
        } else if (data instanceof SGISXYTypeMultipleData) {
            SGISXYTypeMultipleData dataMultSXY = (SGISXYTypeMultipleData) data;
            gs = this.createGroupSetMultipleSXY(dataMultSXY, axisX, axisY, name);
            SGIElementGroupSetMultipleSXY groupSet = (SGIElementGroupSetMultipleSXY) gs;
            if (this.setPropertyOfElementGroupSetForMultipleSXY(el,
                    groupSet, dataMultSXY) == ic) {
                return null;
            }
        } else if (data instanceof SGIVXYTypeData) {
            SGIVXYTypeData dataVXY = (SGIVXYTypeData) data;
            gs = this.createGroupSetVXY((SGIVXYTypeData) data, axisX,
                    axisY, name);
            SGIElementGroupSetVXY groupSet = (SGIElementGroupSetVXY) gs;
            if (this.setPropertyOfElementGroupSetVXY(el, groupSet, dataVXY) == ic) {
                return null;
            }
        } else if (data instanceof SGISXYZTypeData) {
            SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) data;
            gs = this.createGroupSetSXYZ(dataSXYZ, axisX, axisY, name);
            SGIElementGroupSetSXYZ groupSet = (SGIElementGroupSetSXYZ) gs;
            SGAxis axisZ = this.mAxisElement.getZAxis();
            groupSet.setZAxis(axisZ);
            if (this.setPropertyOfElementGroupSetSXYZ(el, groupSet, dataSXYZ) == ic) {
                return null;
            }
        } else {
            throw new Error("Not supported data type: " + data.getDataType());
        }

        return gs;
    }

    protected int setPropertyOfElementGroupSetSXY(final Element el,
            final SGIElementGroupSetSXY groupSet,
            final SGISXYTypeSingleData data) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;

        // create drawing element groups
        NodeList nList = null;

        // bar
        nList = el.getElementsByTagName(SGIBarConstants.TAG_NAME_BAR);
        if (nList.getLength() != 1) {
            return ic;
        }
        Element bar = (Element) nList.item(0);
        SGElementGroupBar barGroup = groupSet.getBarGroup();
        if (barGroup.readProperty(bar) == false) {
            return ic;
        }

        // code for the versions older than 0.9.1
        final float rectWidth = barGroup.getRectangleWidth();
        if (rectWidth != 0.0f) {
            SGAxis axisX = groupSet.getXAxis();
            final double max = axisX.getMaxDoubleValue();
            final double min = axisX.getMinDoubleValue();
            final float ratio = rectWidth / this.mGraphRectWidth;
            double value = (max - min) * ratio;
            value = SGUtilityNumber.getNumberInRangeOrder(value, axisX);
            barGroup.setWidthValue(value);
        }

        // error bar
        nList = el.getElementsByTagName(SGIErrorBarConstants.TAG_NAME_ERROR_BAR);
        if (nList.getLength() == 1) {
            if (data.isErrorBarAvailable()) {
                SGElementGroupErrorBar eGroup = groupSet.getErrorBarGroup();
                Element errorBar = (Element) nList.item(0);
                if (eGroup.readProperty(errorBar) == false) {
                    return ic;
                }
                eGroup.updateLocation();
            }
        } else if (nList.getLength() == 0) {
        	// do nothing
        } else {
            return ic;
        }

        // line
        nList = el.getElementsByTagName(SGILineConstants.TAG_NAME_LINE);
        if (nList.getLength() != 1) {
            return ic;
        }
        Element line = (Element) nList.item(0);
        SGElementGroupLine lineGroup = groupSet.getLineGroup();
        if (lineGroup.readProperty(line) == false) {
            return ic;
        }
        SGLineStyle lineStyle = new SGLineStyle(lineGroup.getLineType(), lineGroup.getColor(), 
        		lineGroup.getLineWidth());
        groupSet.setLineStyle(lineStyle);
        
        // symbol
        nList = el.getElementsByTagName(SGISymbolConstants.TAG_NAME_SYMBOL);
        if (nList.getLength() != 1) {
            return ic;
        }
        Element symbol = (Element) nList.item(0);
        SGElementGroupSymbol symbolGroup = groupSet.getSymbolGroup();
        if (symbolGroup.readProperty(symbol) == false) {
            return ic;
        }

        // tick label
        nList = el.getElementsByTagName(SGITickLabelConstants.TAG_NAME_TICK_LABELS);
        if (nList.getLength() == 1) {
            if (data.isTickLabelAvailable()) {
                SGElementGroupTickLabel tGroup = groupSet.getTickLabelGroup();
                Element tickLabel = (Element) nList.item(0);
                if (tGroup.readProperty(tickLabel) == false) {
                    return ic;
                }

                // update tick label strings
                groupSet.updateTickLabelStrings();

                // update the location
                tGroup.updateLocation();
            }
        } else if (nList.getLength() == 0) {
        	// do nothing
        } else {
            return ic;
        }

        // Enabled to hide all types of drawing elements at the same time.
        /*
        // lines, symbols or bars cannot hide at the same time
        if (!lineGroup.isVisible() && !symbolGroup.isVisible() && !barGroup.isVisible()) {
        	return PROPERTY_FILE_INCORRECT;
        }
        */

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    protected int setPropertyOfElementGroupSetForMultipleSXY(
            final Element el, final SGIElementGroupSetMultipleSXY groupSet,
            final SGISXYTypeMultipleData data) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;

        SGISXYTypeSingleData[] sxyArray = data.getSXYDataArray();
        try {
            String name = groupSet.getName();
            SGAxis axisX = groupSet.getXAxis();
            SGAxis axisY = groupSet.getYAxis();

            SGProperties[] dataProperties = new SGProperties[sxyArray.length];
            for (int ii = 0; ii < sxyArray.length; ii++) {
    			SGData d = (SGData) sxyArray[ii];
    			dataProperties[ii] = d.getProperties();
            }
            
            // gets the line element
            NodeList lineNodeList = el.getElementsByTagName(SGILineConstants.TAG_NAME_LINE);
            if (lineNodeList.getLength() != 1) {
                return ic;
            }
            Element lineElement = (Element) lineNodeList.item(0);

            // setup child group sets
            SGIElementGroupSetForData[] childArray = groupSet.getChildGroupSetArray();
            for (int ii = 0; ii < sxyArray.length; ii++) {
                SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) this.createGroupSet(
                        el, (SGData) sxyArray[ii], axisX, axisY, name, false);
                if (gs == null) {
                	return ic;
                }
                SGProperties gp = gs.getProperties();

                SGIElementGroupSetSXY gsChild = (SGIElementGroupSetSXY) childArray[ii];
                if (gsChild.setProperties(gp) == false) {
                    return ic;
                }

                // sets data properties
                SGElementGroupSetForData fgs = (SGElementGroupSetForData) gsChild;
                if (fgs.setDataProperties(dataProperties[ii]) == false) {
                	return ic;
                }
                gsChild.setName(name);
            }
            
            // set the direction of error bars
            Boolean errorBarVertical = data.isErrorBarVertical();
            if (errorBarVertical != null) {
                for (int ii = 0; ii < sxyArray.length; ii++) {
                    SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) childArray[ii];
                    gs.setErrorBarDirection(errorBarVertical.booleanValue());
                }
            }

            // set the alignment of tick labels
            Boolean tickLabelHorizontal = data.isTickLabelHorizontal();
            if (tickLabelHorizontal != null) {
                for (int ii = 0; ii < sxyArray.length; ii++) {
                    SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) childArray[ii];
                    gs.setTickLabelAlignment(tickLabelHorizontal.booleanValue());
                }
            }

            // set the shift value for the x-axis direction
            String str = el.getAttribute(KEY_SHIFT_X);
            if (str.length() != 0) {
                Double num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return ic;
                }
                final double shiftX = num.doubleValue();
                if (groupSet.setShiftX(shiftX) == false) {
                    return ic;
                }
            }

            // set the shift value for the y-axis direction
            str = el.getAttribute(KEY_SHIFT_Y);
            if (str.length() != 0) {
                Double num = SGUtilityText.getDouble(str);
                if (num == null) {
                    return ic;
                }
                final double shiftY = num.doubleValue();
                if (groupSet.setShiftY(shiftY) == false) {
                    return ic;
                }
            }

            // for backward compatibility
            // version number == 2.0.0
            String version = SGUtility.getVersionNumber(el);
        	if ("2.0.0".equals(version)) {
                Double shiftX = null;
                Double shiftY = null;
                if (lineNodeList.getLength() > 0) {
                	Element lineNode = (Element) lineNodeList.item(0);
                	str = lineNode.getAttribute(KEY_SHIFT_X);
                	if (str.length() != 0) {
                		shiftX = SGUtilityText.getDouble(str);
                    	if (shiftX == null) {
                    		return ic;
                    	}
                    	if (!groupSet.setShiftX(shiftX.doubleValue())) {
                    		return ic;
                    	}
                	}
                	str = lineNode.getAttribute(KEY_SHIFT_Y);
                	if (str.length() != 0) {
                		shiftY = SGUtilityText.getDouble(str);
                    	if (shiftY == null) {
                    		return ic;
                    	}
                    	if (!groupSet.setShiftY(shiftY.doubleValue())) {
                    		return ic;
                    	}
                	}
                }
                if (shiftX != null && shiftY != null) {
                    NodeList barNodeList = el.getElementsByTagName(SGIBarConstants.TAG_NAME_BAR);
                    if (barNodeList.getLength() > 0) {
                    	Element barNode = (Element) barNodeList.item(0);
                    	str = barNode.getAttribute(KEY_SHIFT_X);
                    	if (str.length() != 0) {
                    		Double shiftXOld = SGUtilityText.getDouble(str);
                        	if (shiftXOld == null) {
                        		return ic;
                        	}
                        	final double offsetX = shiftXOld.doubleValue() - shiftX.doubleValue();
                        	List<SGElementGroupBar> bars = groupSet.getBarGroups();
                        	for (SGElementGroupBar bar : bars) {
                            	if (!bar.setOffsetX(offsetX)) {
                            		return ic;
                            	}
                        	}
                    	}
                    	str = barNode.getAttribute(KEY_SHIFT_Y);
                    	if (str.length() != 0) {
                    		Double shiftYOld = SGUtilityText.getDouble(str);
                        	if (shiftYOld == null) {
                        		return ic;
                        	}
                        	final double offsetY = shiftYOld.doubleValue() - shiftY.doubleValue();
                        	List<SGElementGroupBar> bars = groupSet.getBarGroups();
                        	for (SGElementGroupBar bar : bars) {
                            	if (!bar.setOffsetY(offsetY)) {
                            		return ic;
                            	}
                        	}
                    	}
                    }
                }
        	}

            // set the properties of element groups
            SGIElementGroupSetForData firstGroupSet = groupSet.getFirst();
        	Map<String, SGProperties> elpMap = firstGroupSet.getElementGroupPropertiesMap();
            for (int ii = 0; ii < sxyArray.length; ii++) {
                SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) childArray[ii];
                if (gs.isErrorBarAvailable()) {
                	elpMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_ERROR_BAR,
                			gs.getErrorBarGroup().getProperties());
                }
                if (gs.isTickLabelAvailable()) {
                	elpMap.put(SGIElementGroupSetXY.ELEMENT_GROUP_TICK_LABEL, 
                			gs.getTickLabelGroup().getProperties());
                }
            }
            if (groupSet.setElementGroupPropertiesMap(elpMap) == false) {
                return ic;
            }
            
            // sets tick label properties to data: decimal places and exponent
            if (groupSet.isTickLabelAvailable()) {
                SGElementGroupTickLabel tl = groupSet.getTickLabelGroup();
            	data.setDecimalPlaces(tl.getDecimalPlaces());
            	data.setExponent(tl.getExponent());
            	data.setDateFormat(tl.getDateFormat());
            }
            
            // sets the line style
            NodeList lineStylesNodeList = lineElement.getElementsByTagName(TAG_NAME_STYLES);
            Element lineStylesElement = null;
            if (lineStylesNodeList.getLength() > 0) {
            	lineStylesElement = (Element) lineStylesNodeList.item(0);
            }
            if (!groupSet.initLineStyle(lineStylesElement)) {
            	return ic;
            }

            // setup the color map
            NodeList colorMapsNodeList = el.getElementsByTagName(SGColorMapManager.TAG_NAME_COLOR_STYLES);
            if (colorMapsNodeList.getLength() > 0) {
            	Element colorMapsElement = (Element) colorMapsNodeList.item(0);
            	if (groupSet.readColorMap(colorMapsElement) == false) {
            		return ic;
            	}
            }
            
            // initialize the history
            for (int ii = 0; ii < childArray.length; ii++) {
                SGIElementGroupSetSXY gs = (SGIElementGroupSetSXY) childArray[ii];
                gs.initPropertiesHistory();
            }

        } finally {
        	if (sxyArray != null) {
    	    	// disposes of data objects
    	        SGDataUtility.disposeSXYDataArray(sxyArray);
        	}
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    protected int setPropertyOfElementGroupSetVXY(final Element el,
            final SGIElementGroupSetVXY groupSet,
            final SGIVXYTypeData data) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;
        // SGIFigureElementAxis aElement = this.mAxisElement;
        String str = null;
        Number num = null;
        Boolean b = null;

        //
        // create drawing element groups
        //

        SGElementGroup group = null;
        NodeList nList = null;

        // arrow
        nList = el.getElementsByTagName(SGIArrowConstants.TAG_NAME_ARROW);
        if (nList.getLength() != 1) {
            return ic;
        }
        Element arrow = (Element) nList.item(0);
        group = groupSet.getArrowGroup();
        if (group.readProperty(arrow) == false) {
            return ic;
        }

        // magnitude
        str = el.getAttribute(SGIFigureElementGraph.KEY_MAGNITUDE_PER_CM);
        if (str.length() != 0) {
            num = SGUtilityText.getFloat(str);
            if (num == null) {
                return ic;
            }
            groupSet.setMagnitudePerCM(num.floatValue());
        }

        // direction invariance
        str = el.getAttribute(SGIFigureElementGraph.KEY_DIRECTION_INVARIANT);
        if (str.length() != 0) {
            b = SGUtilityText.getBoolean(str);
            if (b == null) {
                return ic;
            }
            groupSet.setDirectionInvariant(b.booleanValue());
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    protected int setPropertyOfElementGroupSetSXYZ(final Element el,
            final SGIElementGroupSetSXYZ groupSet,
            final SGISXYZTypeData data) {

        final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;

        // create drawing element groups
        SGElementGroup group = null;
        NodeList nList = null;

        // color map
        nList = el.getElementsByTagName(SGIColorMapConstants.TAG_NAME_COLOR_MAP);
        if (nList.getLength() == 0) {
            // for backward compatibility
            nList = el.getElementsByTagName(SGIColorMapConstants.TAG_NAME_GRID_COLOR_MAP);
        }
        if (nList.getLength() != 1) {
            return ic;
        }
        Element colorMap = (Element) nList.item(0);
        group = groupSet.getColorMap();
        if (group.readProperty(colorMap) == false) {
            return ic;
        }

        return SGIConstants.SUCCESSFUL_COMPLETION;
    }

    /**
     * Sets the dialog owner this figure element.
     *
     * @param frame
     *            the dialog owner
     * @return true if succeeded
     */
    public boolean setDialogOwner(final Frame frame) {
        if (super.setDialogOwner(frame) == false) {
            return false;
        }
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (mDataDialogMap != null) {
                    if (mDataDialogMap.get(DIALOG_KEY_SXY) == null) {
                        mDataDialogMap.put(DIALOG_KEY_SXY, new SGPropertyDialogSXYData(
                                mDialogOwner, true));
                    }
                    if (mDataDialogMap.get(DIALOG_KEY_VXY) == null) {
                        mDataDialogMap.put(DIALOG_KEY_VXY, new SGPropertyDialogVXYData(
                                mDialogOwner, true));
                    }
                }
//                if (mAnimationDialog == null) {
//                    mAnimationDialog = new SGDataAnimationConfigurationDialog(mDialogOwner, true);
//                }
            }
        });
        return true;
    }

    /**
     * Checks whether the objects related to a given data are changed.
     * 
     * @param data
     *          a data
     * @return true if something changed related to a given data
     */
    @Override
    public boolean checkDataChanged(final SGData data) {
        SGElementGroupSetForData gs = this.getElementGroupSet(data);
        return gs.checkChanged();
    }

    public boolean isDataChanged(final SGData data) {
        SGElementGroupSetForData gs = this.getElementGroupSet(data);
        return gs.isChanged();
    }

    /**
     * Synchronize the data properties with given data properties.
     *
     * @param p
     *          the properties
     * @param dp
     *          data properties
     * @return synchronized properties
     */
    public SGProperties synchronizeDataProperties(SGProperties p, SGProperties dp) {
        if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
            return null;
        }
        if ((dp instanceof DataProperties) == false) {
            return null;
        }
        ElementGroupSetPropertiesInFigureElement gp = (ElementGroupSetPropertiesInFigureElement) p.copy();
        gp.dataProperties = (DataProperties) dp.copy();
        gp.labelStringList.clear();
        if (gp instanceof MultipleSXYElementGroupSetPropertiesInFigureElement) {
        	MultipleSXYElementGroupSetPropertiesInFigureElement mgp = (MultipleSXYElementGroupSetPropertiesInFigureElement) gp;
        	ElementGroupSetPropertiesInFigureElement first = (ElementGroupSetPropertiesInFigureElement) mgp.childPropertyList.get(0);
        	first.dataProperties = (DataProperties) dp.copy();
        	mgp.childPropertyList.clear();
        	mgp.childPropertyList.add(first);
        }
        return gp;
    }

    /**
     * Returns a list of labels for data objects.
     *
     * @return a list of labels for data objects
     */
    public List<DataLabel> getDataLabelList() {
        List<SGData> dList = this.getFocusedDataList();
        List<DataLabel> list = new ArrayList<DataLabel>();
        for (int ii = 0; ii< dList.size(); ii++) {
            SGData data = dList.get(ii);
            SGElementGroupSetForData gs = this.getElementGroupSet(data);
            DataLabel dl = gs.getDataLabel();
            if (dl != null) {
                list.add(dl);
            } else {
                if (list.size() == 0) {
                    SGUtility.showErrorMessageDialog(this.mDialogOwner,
                            gs.getName() + ": " + "Coordinate variables with fixed values do not exist.",
                            SGIConstants.ERROR);
                }
            }
        }
        return list;
    }

    /**
     * Hide the selected objects.
     * @return
     *         true if selected
     */
    @Override
    public boolean hideSelectedObjects() {
    	List<SGISelectable> listBefore = this.getFocusedObjectsList();
        if (listBefore.size()>0) {
            this.notifyToListener(NOTIFY_DATA_WILL_BE_HIDDEN);
        }
        if (super.hideSelectedObjects() == false) {
            return false;
        }
        List<SGISelectable> listAfter = this.getFocusedObjectsList();
        if (listBefore.size() != listAfter.size()) {
            this.notifyToListener(NOTIFY_DATA_HIDDEN);
        }
        return true;
    }

    /**
     * Hide the selected data objects.
     * @return
     *         true if selected
     */
    protected boolean hideSelectedData() {
    	List<SGISelectable> listBefore = this.getFocusedObjectsList();
        if (listBefore.size()>0) {
            this.notifyToListener(NOTIFY_DATA_WILL_BE_HIDDEN);
        }
        if (super.hideSelectedData() == false) {
            return false;
        }
        List<SGISelectable> listAfter = this.getFocusedObjectsList();
        if (listBefore.size() != listAfter.size()) {
            this.notifyToListener(NOTIFY_DATA_HIDDEN);
        }
        return true;
    }

//    /**
//     * A dialog to setup the animation of data objects.
//     */
//    protected SGDataAnimationConfigurationDialog mAnimationDialog = null;
//
//    /**
//     * Do the animation for a given group set.
//     *
//     * @param gs
//     *          a group set to do animation
//     */
//    protected void doAnimation(SGElementGroupSetForData gs) {
//        SGData data = gs.getData();
//        boolean valid = false;
//        if (SGDataUtility.isNetCDFData(data)) {
//            SGNetCDFData nData = (SGNetCDFData) data;
//            if (nData.isTimeVariableAvailable()) {
//            	valid = true;
//            } else {
//                SGUtility.showErrorMessageDialog(this.mDialogOwner,
//                        "Variable for animation frame is not selected properly.", SGIConstants.ERROR);
//            }
//        } else if (SGDataUtility.isMDArrayData(data)) {
//        	SGMDArrayData mdData = (SGMDArrayData) data;
//        	if (mdData.isTimeDimensionAvailable()) {
//            	valid = true;
//        	} else {
//                SGUtility.showErrorMessageDialog(this.mDialogOwner,
//                        "Dimensions for animation frame are not selected properly.", SGIConstants.ERROR);
//        	}
//        } else {
//            throw new Error("Not supported data type: " + data.getDataType());
//        }
//        if (!valid) {
//        	return;
//        }
//        this.mAnimationDialog.setAnimation(gs);
//        this.mAnimationDialog.setCenter(this.mDialogOwner);
//        this.mAnimationDialog.setVisible(true);
//    }

    /**
     * Returns the list of axis information of a given data.
     *
     * @param data
     *           a data object
     * @return the list of axis information of a given data
     */
    public List<SGDataAxisInfo> getAxisInfoList(SGData data,
    		final boolean forAnimationFrames) {
        SGElementGroupSetForData gs = this.getElementGroupSet(data);
        SGAxis axis = null;
        SGValueRange bounds = null;
        String title = null;
        List<SGDataAxisInfo> aList = new ArrayList<SGDataAxisInfo>();
        if (data instanceof SGIXYData) {
            SGIXYData twoDimensionalData = (SGIXYData) data;
            axis = gs.getXAxis();
            if (axis != null) {
                bounds = forAnimationFrames ? 
                		twoDimensionalData.getAllAnimationFrameBoundsX() :
                		twoDimensionalData.getBoundsX();
                title = twoDimensionalData.getTitleX();
                this.addAxisInfo(data, axis, bounds, title, aList);
            }
            axis = gs.getYAxis();
            if (axis != null) {
                bounds = forAnimationFrames ?
                		twoDimensionalData.getAllAnimationFrameBoundsY() :
                		twoDimensionalData.getBoundsY();
                title = twoDimensionalData.getTitleY();
                this.addAxisInfo(data, axis, bounds, title, aList);
            }
            if (data instanceof SGIXYZData) {
	            SGIXYZData threeDimensionalData = (SGIXYZData) data;
	            axis = gs.getZAxis();
	            if (axis != null) {
	                bounds = forAnimationFrames ?
	                		threeDimensionalData.getAllAnimationFrameBoundsZ() :
	                		threeDimensionalData.getBoundsZ();
	                title = threeDimensionalData.getTitleZ();
	                this.addAxisInfo(data, axis, bounds, title, aList);
	            }
            }
        } else {
            throw new IllegalArgumentException("Unsupported data type: " + data.getDataType());
        }
        return aList;
    }

    private void addAxisInfo(final SGData data, final SGAxis axis,
    		final SGValueRange bounds,
            final String title, final List<SGDataAxisInfo> aList) {
        final int location = this.mAxisElement.getLocationInPlane(axis);
        SGDataAxisInfo info = new SGDataAxisInfo(data, axis, bounds, title, location);
        aList.add(info);
    }


//    protected SGElementGroupSetInFigureElement getGroupSet(final int id) {
//        SGElementGroupSetInFigureElement el = (SGElementGroupSetInFigureElement) this
//                .getVisibleChild(id);
//        if (el == null) {
//            return null;
//        }
//        return el;
//    }

    /**
     * Returns the data of a given ID.
     *
     * @param id
     *           the ID of data
     * @return the data object if it exists
     */
    public SGData getData(final int id) {
        SGIChildObject c = this.getVisibleChild(id);
        if (c!=null) {
            SGElementGroupSetForData gs = (SGElementGroupSetForData)c;
            return gs.getData();
        }

        SGElementGroupSetForData gs = (SGElementGroupSetForData) this
                .getChild(id);
        if (gs == null) {
            return null;
        }
        return gs.getData();
    }

    /**
     * Sets the properties of child object.
     *
     * @param id
     *           the ID of child object
     * @param map
     *           a map properties
     * @return the result of setting properties
     */
    public SGPropertyResults setChildProperties(final int id, SGPropertyMap map) {
        SGIChildObject child = this.getVisibleChild(id);
        if (child == null) {
            // returns null when data object is not found
            return null;
        }

        // set properties to the child object
        SGPropertyResults result = child.setProperties(map);

        return result;
    }

    /**
     * Sets the properties of sub child object.
     * 
     * @param id
     *           the ID of child object
     * @param subId
     *           the ID of sub child object
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setChildProperties(final int id, final int subId,
            SGPropertyMap map) {
        SGIChildObject child = this.getVisibleChild(id);
        if (child == null) {
            // returns null when data object is not found
            return null;
        }
        SGElementGroupSetForData gs = (SGElementGroupSetForData) child;
        if (!(gs instanceof SGIElementGroupSetMultipleSXY)) {
        	return null;
        }
        SGIElementGroupSetMultipleSXY gsSXY = (SGIElementGroupSetMultipleSXY) gs;

        // set properties to the child object
        SGPropertyResults result = gsSXY.setProperties(map, subId);
        return result;
    }
    
    /**
     * Sets the properties of sub child object.
     * 
     * @param id
     *           the ID of child object
     * @param colorMapName
     *           the name of color map
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setChildColorMapProperties(final int id, 
    		final String colorMapName, SGPropertyMap map) {
        SGIChildObject child = this.getVisibleChild(id);
        if (child == null) {
            // returns null when data object is not found
            return null;
        }
        SGElementGroupSetForData gs = (SGElementGroupSetForData) child;
        if (!(gs instanceof SGIElementGroupSetMultipleSXY)) {
        	return null;
        }
        SGIElementGroupSetMultipleSXY gsSXY = (SGIElementGroupSetMultipleSXY) gs;
    	
        // set properties to the color map
        SGPropertyResults result = gsSXY.setColorMapProperties(colorMapName, map);
        
        return result;
    }


    /**
     * Overrode not to call other "addData" method.
     *
     * @param data
     *             added data.
     * @param name
     *             the name set to the data
     * @param p
     *             properties set to be data.
     * @return true if succeeded
     */
    public boolean addData(final SGData data, final String name,
            final SGProperties p) {
        if (data == null) {
            throw new Error("data==null");
        }
        // just add to the list
        this.mDataList.add(data);
        return true;
    }

    /**
     * Adds data objects with given name.
     * 
     * @param data
     *            an array of data objects
     * @param name
     *            an array of names of data objects
     * @param infoMap
     *            the information map of data
     * @return true if succeeded
     */
    @Override
    public boolean addData(final SGData[] data, final String[] name,
            final Map<String, Object> infoMap) {
    	if (!super.addData(data, name, infoMap)) {
    		return false;
    	}
    	Boolean b = (Boolean) infoMap.get(SGPlotTypeConstants.KEY_PLOT_TYPE_LINE_COLOR_AUTO_ASSIGNED);
    	final boolean autoAssigned = (b != null) ? b.booleanValue() : false;
    	if (autoAssigned) {
    		List<SGLineStyle> lineStyleList = SGUtilityForFigureElement.createLineStyleList(
    				new HueColorMap(), data.length);
    		for (int ii = 0; ii < data.length; ii++) {
    			SGElementGroupSetForData gs = this.getElementGroupSet(data[ii]);
    			SGLineStyle lineStyle = lineStyleList.get(ii);
    			if (gs instanceof SGIElementGroupSetMultipleSXY) {
    				SGIElementGroupSetMultipleSXY gsXY = (SGIElementGroupSetMultipleSXY) gs;
    				gsXY.setLineStyle(lineStyle, 0);
    				gsXY.initPropertiesHistory();
    			}
    		}
    	}
    	return true;
    }
    
    /**
     * Removes a data object.
     *
     * @param data
     *             data to be removed
     * @return true if succeeded
     */
    public boolean removeData(SGData data) {
    	if (super.removeData(data) == false) {
    		return false;
    	}
    	for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGElementGroupSetForData groupSet = (SGElementGroupSetForData) this.mChildList.get(ii);
            if (data.equals(groupSet.getData())) {
                if (groupSet.setData(null) == false) {
                	return false;
                }
            }
    	}
    	return true;
    }

    /**
     * Returns the child object for given data.
     *
     * @param data
     *           data object
     * @return the child object
     */
	@Override
	public SGIChildObject getChild(SGData data) {
		return this.getElementGroupSet(data);
	}

    /**
     * Returns a list of focused data objects.
     * 
     * @return a list of focused data objects
     */
    public List<SGData> getFocusedDataList() {
        List<SGISelectable> fList = this.getFocusedObjectsList();
        List<SGData> list = new ArrayList<SGData>();
        for (int ii = 0; ii < fList.size(); ii++) {
        	Object obj = fList.get(ii);
        	if (obj instanceof SGElementGroupSetForData) {
            	SGElementGroupSetForData gs = (SGElementGroupSetForData) obj;
                SGData data = this.getData(gs);
                list.add(data);
        	}
        }
        return list;
    }

    /**
     * Returns whether given group set is enabled to be merged.
     *
     * @param gs
     *          a group set
     * @return true if given group set enabled to be merged
     */
    protected boolean isMergeEnabled(SGElementGroupSetForData gs) {
        List<SGData> dataList = this.getFocusedDataList();
        SGData data = gs.getData();
        SGIDataSource dataSource = data.getDataSource();
        for (int ii = 0; ii < dataList.size(); ii++) {
        	SGData d = dataList.get(ii);
        	if (data.equals(d)) {
        		continue;
        	}
        	SGIDataSource dsrc = d.getDataSource();
            if (dataSource.equals(dsrc)) {
            	return true;
            }
        }
        return false;
    }

    /**
     * Returns the style of drawing elements of given data.
     *
     * @param data
     *          a data
     * @return list of the style
     */
    @Override
    public List<SGStyle> getStyle(SGData data) {
    	SGElementGroupSetForData gs = this.getElementGroupSet(data);
    	if (gs == null) {
    		return null;
    	}
    	return gs.getStyle();
    }

    /**
     * Returns whether it is available to assign line colors in given group set.
     * 
     * @param gs
     *           a group set
     * @return true if it is available
     */
    protected boolean isLineColorAssignAvailable(SGIElementGroupSetMultipleSXY gs) {
        List<SGData> dataList = this.getFocusedDataList();
        int cnt = 0;
        for (SGData data : dataList) {
        	SGElementGroupSetForData groupSet = this.getElementGroupSet(data);
        	if (groupSet instanceof SGIElementGroupSetMultipleSXY) {
        		SGIElementGroupSetMultipleSXY gsSXY = (SGIElementGroupSetMultipleSXY) groupSet;
            	cnt += gsSXY.getChildNumber();
        	}
        }
    	return (cnt > 1);
    }
    
    /**
     * Sets the flag whether data objects in this figure are anchored
     * 
     * @param b
     *           true to set data objects in this figure anchored
     * @return true if succeeded
     */
    public boolean setDataAnchored(final boolean b) {
    	// sets the flag to all sxy-type data objects
    	for (int ii = 0; ii < this.mChildList.size(); ii++) {
    		SGElementGroupSetForData gs = (SGElementGroupSetForData) this.mChildList.get(ii);
    		gs.setAnchored(b);
    	}
    	return true;
    }

    @Override
    public boolean hideData(final int[] dataIdArray) {
        boolean changed = false;
        for (int ii = 0; ii < dataIdArray.length; ii++) {
            SGIChildObject obj = this.getVisibleChild(dataIdArray[ii]);
            if (obj == null) {
                continue;
            }
            SGElementGroupSetForData groupSet = (SGElementGroupSetForData) obj;
            groupSet.setVisible(false);
            changed = true;
        }
        if (changed) {
            this.setChanged(true);
        }
        return true;
    }

    public void showDataPopupMenu(final SGElementGroupSetForData gs, final int x, final int y,
    		final boolean inGraph, SGDrawingWindow wnd) {
    	SGData data = gs.getData();
    	String dataType = data.getDataType();
    	SGDataPopupMenu menu;
    	if (SGDataUtility.isSXYTypeData(dataType)) {
    		menu = new SGSXYDataPopupMenu(wnd, gs, inGraph);
    	} else if (SGDataUtility.isSXYZTypeData(dataType)) {
    		menu = new SGSXYZDataPopupMenu(wnd, gs, inGraph);
    	} else if (SGDataUtility.isVXYTypeData(dataType)) {
    		menu = new SGVXYDataPopupMenu(wnd, gs, inGraph);
    	} else {
    		return;
    	}
    	menu.show(this.getComponent(), x, y);
    }

    /**
     * Returns the list of selected property dialog observers of given class type.
     * 
     * @param cl
     *            the class
     * @return the list of selected property dialog observers
     */
    @Override
    public List<SGIPropertyDialogObserver> getSelectedPropertyDialogObserverList(Class<?> cl) {
    	return this.getSelectedPropertyDialogObserverList();
    }

    /**
     * Returns the list of visible property dialog observers of given class type.
     * 
     * @param cl
     *            the class
     * @return the list of visible property dialog observers
     */
    @Override
    public List<SGIPropertyDialogObserver> getVisiblePropertyDialogObserverList(Class<?> cl) {
    	return this.getVisiblePropertyDialogObserverList();
    }

    /**
     * Returns the list of all property dialog observers of given class type.
     * 
     * @param cl
     *            the class
     * @return the list of all property dialog observers
     */
    @Override
    public List<SGIPropertyDialogObserver> getAllPropertyDialogObserverList(Class<?> cl) {
    	return this.getVisiblePropertyDialogObserverList();
    }

    /**
     * Returns the class object of property dialog observer.
     * 
     * @return the class object
     */
    @Override
    public Class<?> getPropertyDialogObserverClass() {
    	return SGElementGroupSetForData.class;
    }

    /**
     * Sets the data selection.
     *
     * @param data
     *           the data to set the state of selection
     * @param b
     *           true to be selected
     */
    public void setDataFocused(SGData data, boolean b) {
        SGElementGroupSetForData gs = (SGElementGroupSetForData) this.getElementGroupSet(data);
        if (gs == null) {
            throw new IllegalArgumentException("Data is not found.");
        }
        gs.setSelected(b);

        // notify that the state of data selection is changed
        this.notifyDataSelection();
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
        for (int ii = 0; ii < this.mChildList.size(); ii++) {
            SGElementGroupSetForData groupSet
                = (SGElementGroupSetForData) this.mChildList.get(ii);
        	groupSet.replaceDataSource(srcOld, srcNew, obs);
        }
    }

    protected boolean synchronizeToDataElement(SGIFigureElementForData dElement, 
    		final String msg) {
    	
		List<SGData> gDataList = dElement.getDataList();
		if (gDataList.size() != this.mDataList.size()) {
		    throw new Error("dataList.size()!= this.mDataList.size()  "+
		            gDataList.size()+", "+this.mDataList.size());
		}

        if (this.isNotificationMessage(msg)) {
			// synchronize the properties of drawing elements
			List<SGIChildObject> cList = this.getVisibleChildList();
			for (int ii = 0; ii < cList.size(); ii++) {
				SGElementGroupSetForData groupSet = (SGElementGroupSetForData) cList.get(ii);
				SGData data = groupSet.getData();
				SGProperties gdp = dElement.getDataProperties(data);
				final boolean bUpdate;
				if (SGIFigureElement.NOTIFY_CHANGE_ON_COMMIT.equals(msg)) {
					bUpdate = dElement.isDataChanged(data);
					groupSet.setChanged(bUpdate);
				} else if (SGIFigureElement.NOTIFY_CHANGE_ON_CANCEL.equals(msg)) {
					bUpdate = true;
				} else {
					bUpdate = dElement.checkDataChanged(data);
				}
				if (bUpdate) {
					if (groupSet.setProperties(gdp) == false) {
						return false;
					}
	                if (groupSet.updateWithData() == false) {
	                	return false;
	                }
				}
			}

		} else if (SGIFigureElement.NOTIFY_DATA_SELECTION.equals(msg)) {

			// update data selection
			List<SGIChildObject> cList = this.getVisibleChildList();
			for (int ii = 0; ii < cList.size(); ii++) {
				SGElementGroupSetForData groupSet = (SGElementGroupSetForData) cList.get(ii);
				SGData data = groupSet.getData();
				final boolean b = dElement.isDataSelected(data);
				groupSet.setSelected(b);
			}
		}

		return true;
    }
}
