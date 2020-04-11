package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jp.riken.brain.ni.samuraigraph.base.SGAxis;
import jp.riken.brain.ni.samuraigraph.base.SGCommandUtility;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGData.DataProperties;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingElement;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIAnimationConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementForData.DataLabel;
import jp.riken.brain.ni.samuraigraph.base.SGITextDataConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIUndoable;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyResults;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGStyle;
import jp.riken.brain.ni.samuraigraph.base.SGUndoManager;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData.ArrayDataProperties;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataAnimation;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIIndexData;
import jp.riken.brain.ni.samuraigraph.data.SGIMDArrayConstants;
import jp.riken.brain.ni.samuraigraph.data.SGISXYTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGITwoDimensionalData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroupSet;

import org.w3c.dom.Element;

/**
 * The class for the set of drawing elements for data objects.
 * 
 */
public abstract class SGElementGroupSetForData extends
        SGElementGroupSet implements SGIElementGroupSetForData,
        SGIUndoable, SGIDataPropertyDialogObserver, SGIConstants,
        SGITextDataConstants, SGIDataAnimation, ActionListener {

    /**
     * The related data object.
     */
    protected SGData mData = null;

    /**
     * The name of this group set.
     */
    protected String mName = null;

    /**
     * A flag whether this group set is visible in legend.
     */
    protected boolean mVisibleInLegendFlag = true;

    /**
     * Flag whether this object is focused.
     */
    protected boolean mSelectedFlag = false;

    /**
     * The X-axis for this group set.
     */
    protected SGAxis mXAxis = null;

    /**
     * The Y-axis for this group set.
     */
    protected SGAxis mYAxis = null;

    /**
     * The Z-axis for this group set.
     */
    protected SGAxis mZAxis = null;

    /**
     * The label list of this group set.
     *
     * This list has relation of label id with label text.
     */
    protected Map<Integer, String> mLabelStringList = new HashMap<Integer, String>();

    /**
     * The flag whether the data object is anchored.
     */
    protected boolean mAnchorFlag = true;

    /**
     * The ID number.
     */
    private int mID;

    /**
     * Undo manager object.
     */
    protected SGUndoManager mUndoManager = new SGUndoManager(this);
    
    /**
     * The flag for the loop play back of animation.
     */
    private boolean mLoopPlaybackFlag = false;

    /**
     * The frame rate for animation.
     */
    private double mFrameRate = 1.0;
    
    /**
     * The default constructor.
     */
    public SGElementGroupSetForData(SGData data) {
        super();
        this.mData = data;
    }

    /**
     * Returns the related data object.
     * @return
     *         the related data object.
     */
    public SGData getData() {
    	return this.mData;
    }

    /**
     * Returns the ID of this object.
     */
    public int getID() {
        return this.mID;
    }

    /**
     * Sets the ID to this object.
     */
    public boolean setID(final int id) {
        this.mID = id;
        return true;
    }

    /**
     * Dispose this object.
     */
    public void dispose() {
        super.dispose();

        this.mName = null;

        this.mUndoManager.dispose();
        this.mUndoManager = null;

        this.mXAxis = null;
        this.mYAxis = null;
        this.mZAxis = null;
    }

    /**
     * Sets the name to this group set.
     *
     * @param name
     *            the name to set to this group set
     * @return true if succeeded
     */
    public boolean setName(final String name) {
        this.mName = name;
        return true;
    }

    /**
     * Sets visibility of this group set in legend.
     *
     * @param b
     *            a value to set to visibility in legend
     * @return true if succeeded
     */
    public boolean setVisibleInLegend(final boolean b) {
        this.mVisibleInLegendFlag = b;
        return true;
    }

    /**
     * Returns the name.
     *
     * @return the name
     */
    public String getName() {
        return this.mName;
    }

    /**
     * Returns the visibility in legend.
     *
     * @return the visibility in legend
     */
    public boolean isVisibleInLegend() {
        return this.mVisibleInLegendFlag;
    }

    /**
     * Get the flag as a focused object.
     *
     * @return whether this object is focused.
     */
    public boolean isSelected() {
        return this.mSelectedFlag;
    }

    /**
     * Set the flag as a focused object.
     *
     * @param b
     *            focused
     */
    public void setSelected(final boolean b) {
        this.mSelectedFlag = b;
    }

    /**
     * Set the X-axis.
     *
     * @param axis
     *            an axis to set to the X-axis
     * @return true if succeeded
     */
    public boolean setXAxis(final SGAxis axis) {
        this.mXAxis = axis;
        return true;
    }

    /**
     * Set the Y-axis.
     *
     * @param axis
     *            an axis to set to the Y-axis
     * @return true if succeeded
     */
    public boolean setYAxis(final SGAxis axis) {
        this.mYAxis = axis;
        return true;
    }

    /**
     * Sets the Z-axis.
     *
     * @param axis
     *            an axis to set to the Z-axis
     * @return true if succeeded
     */
    public boolean setZAxis(SGAxis axis) {
        this.mZAxis = axis;
        return true;
    }

    /**
     * Returns the X-axis.
     *
     * @return the X-axis
     */
    public SGAxis getXAxis() {
        return this.mXAxis;
    }

    /**
     * Returns the Y-axis.
     *
     * @return the Y-axis
     */
    public SGAxis getYAxis() {
        return this.mYAxis;
    }

    /**
     * Returns the Z-axis.
     *
     * @return the Z-axis
     */
    public SGAxis getZAxis() {
        return this.mZAxis;
    }

    /**
     * Sets the location of x-axis.
     *
     * @param location
     *           the location of the x-axis
     * @return true if succeeded
     */
    public abstract boolean setXAxisLocation(final int location);

    /**
     * Sets the location of y-axis.
     *
     * @param location
     *           the location of the y-axis
     * @return true if succeeded
     */
    public abstract boolean setYAxisLocation(final int location);

    /**
     *
     */
    public List<Boolean> getVisibleFlagList() {
        final List<Boolean> list = new ArrayList<Boolean>();
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            final SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList.get(ii);
            final boolean flag = group.isVisible();
            list.add(Boolean.valueOf(flag));
        }
        return list;
    }

    /**
	 * Check whether this group set contains a given point.
	 *
	 * @param x
	 *            the x coordinate
	 * @param y
	 *            the y coordinate
	 * @return true if this group set contains a given point
	 */
    public boolean onDrawingElement(final int x, final int y) {
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            SGElementGroup group = (SGElementGroup) this.mDrawingElementGroupList
                    .get(ii);
            SGDrawingElement[] array = group.getDrawingElementArray();
            for (int jj = 0; jj < array.length; jj++) {
                if (array[jj].contains(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Write properties of this object to the Element.
     *
     * @param el
     *            the Element object
     * @param operation
     *            the operation
     * @return true if succeeded
     */
    public boolean writeProperty(final Element el, final SGExportParameter params) {
        OPERATION type = params.getType();
        SGArrayData data = (SGArrayData) this.getData();
        
        el.setAttribute(SGIFigureElement.KEY_DATA_NAME, this.mName);
        el.setAttribute(SGIFigureElement.KEY_VISIBLE_IN_LEGEND, Boolean
                .toString(this.mVisibleInLegendFlag));
        
        // attributes for the animation
        if (data.isAnimationAvailable()) {
            final int digitFrameRate = SGIAnimationConstants.FRAME_RATE_MINIMAL_ORDER - 1;
            final double frameRate = SGUtilityNumber.roundOffNumber(
                	this.mFrameRate, digitFrameRate);
            el.setAttribute(SGIFigureElement.KEY_ANIMATION_ARRAY_SECTION, 
            		this.getAnimationArraySection().toString());
            el.setAttribute(SGIFigureElement.KEY_ANIMATION_FRAME_RATE, Double.toString(frameRate));
            el.setAttribute(SGIFigureElement.KEY_ANIMATION_LOOP_PLAYBACK, 
            		Boolean.toString(this.mLoopPlaybackFlag));
        }

        final String dataType;
        if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {
            dataType = data.getNetCDFDataSetDataType();
        } else {
            dataType = data.getDataType();
        }
        el.setAttribute(SGIFigureElement.KEY_DATA_TYPE, dataType);

        switch (type) {
        case SAVE_TO_PROPERTY_FILE:
            SGData sdata = this.getData();
            if (sdata instanceof SGSXYSDArrayMultipleData) {
                // write information of sampling rate.
                ((SGSXYSDArrayMultipleData)sdata).writeSamplingRateToProperty(el);
            }
        case SAVE_TO_ARCHIVE_DATA_SET:
        case SAVE_TO_ARCHIVE_DATA_SET_107:
        case DUPLICATE_OBJECT:
        case COPY_OBJECT:
        case CUT_OBJECT:
        case SAVE_TO_DATA_SET_NETCDF:
            if (data.writeProperty(el, params) == false) {
                return false;
            }
            break;
        default:
        }

        return true;
    }

    /**
     * Returns properties of this group set.
     *
     * @return properties of this group set
     */
    public SGProperties getProperties() {
        ElementGroupSetPropertiesInFigureElement ep = new ElementGroupSetPropertiesInFigureElement();
        if (this.getProperties(ep) == false) {
            return null;
        }
        return ep;
    }

    /**
     * Returns properties of this group set.
     *
     * @return properties of this group set
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
        ep.name = this.mName;
        ep.animationArraySection = this.getAnimationArraySection();
        ep.visibleInLegend = this.mVisibleInLegendFlag;
        ep.frameRate = this.mFrameRate;
        ep.loopPlayback = this.mLoopPlaybackFlag;
        ep.dataProperties = (DataProperties) this.getDataProperties();
        ep.labelStringList.clear();
        ep.labelStringList.putAll(this.mLabelStringList);
        return true;
    }

    /**
     * Set properties to this group set.
     *
     * @param p
     *            properties to set to this group set
     * @return true if succeeded
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
        this.setName(ep.name);
        this.setVisibleInLegend(ep.visibleInLegend);
        this.setFrameIndices(ep.animationArraySection);
        this.setFrameRate(ep.frameRate);
        this.setLoopPlaybackAvailable(ep.loopPlayback);
        this.setDataProperties(ep.dataProperties);
        this.mLabelStringList.clear();
        this.mLabelStringList.putAll(ep.labelStringList);
        return true;
    }

//    /**
//     * Sets the properties of element groups.
//     *
//     * @param elementGroupPropertiesList
//     * @return true if succeeded
//     *
//     */
//    protected boolean setElementGroupProperties(List elementGroupPropertiesList) {
//
//        for (int ii = 0; ii < elementGroupPropertiesList.size(); ii++) {
//            SGProperties gp = (SGProperties) elementGroupPropertiesList.get(ii);
//            SGElementGroup group = null;
//            if (gp instanceof SGElementGroupLine.LineProperties) {
//                group = this.getLineGroup();
//            } else if (gp instanceof SGElementGroupSymbol.SymbolProperties) {
//                group = this.getSymbolGroup();
//            } else if (gp instanceof SGElementGroupBar.BarProperties) {
//                group = this.getBarGroup();
//            } else if (gp instanceof SGElementGroupErrorBar.ErrorBarProperties) {
//                group = this.getErrorBarGroup();
//            } else if (gp instanceof SGElementGroupString.StringProperties) {
//                group = this.getTickLabelGroup();
//            } else if (gp instanceof SGElementGroupArrow.ArrowProperties) {
//                group = this.getArrowGroup();
//            } else if (gp instanceof SGElementGroupColorMap.ColorMapProperties) {
//                group = this.getColorMapGroup();
//            } else if (gp instanceof SGElementGroupGridColorMap.GridColorMapProperties) {
//                group = this.getGridColorMapGroup();
//            } else {
//                throw new Error("Illegal group property: " + gp);
//            }
//            if (group == null) {
//                continue;
//            }
//            if (group.setProperties(gp) == false) {
//                return false;
//            }
//        }
//        return true;
//    }

    /**
     * Sets the properties of data.
     * @param dp
     *           properties of data
     *
     */
    public boolean setDataProperties(final SGProperties dp) {
    	SGData data = this.getData();
    	if (data != null) {
            return data.setProperties(dp);
    	}
    	return false;
    }

    /**
     * Returns the properties of data.
     * @return
     *         properties of data
     */
    public SGProperties getDataProperties() {
    	SGData data = this.getData();
    	if (data != null) {
            return data.getProperties();
    	} else {
    		return null;
    	}
    }


    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[name=");
        sb.append(this.mName);
        sb.append(", ");
        sb.append(super.toString());
        sb.append("]");
        return sb.toString();
    }


    public boolean isChanged() {
        return this.mUndoManager.isChanged();
    }

    public void setChanged(final boolean b) {
        this.mUndoManager.setChanged(b);
    }

    public boolean isChangedRoot() {
        return this.isChanged();
    }

    public SGProperties getMemento() {
        return this.getProperties();
    }

    public boolean setMemento(SGProperties p) {
        return this.setProperties(p);
    }

    public boolean isUndoable() {
        return this.mUndoManager.isUndoable();
    }

    public boolean isRedoable() {
        return this.mUndoManager.isRedoable();
    }

    public boolean setMementoBackward() {
        if (this.mUndoManager.setMementoBackward() == false) {
            return false;
        }

        // set previous properties to the data object
        if (this.synchronizeDataOnUndo() == false) {
            return false;
        }

        return true;
    }

    public boolean setMementoForward() {
        if (this.mUndoManager.setMementoForward() == false) {
            return false;
        }

        // set next properties to the data object
        if (this.synchronizeDataOnUndo() == false) {
            return false;
        }

        return true;
    }

    /**
     * Synchronize the data object to the properties of this group set.
     * @return
     *         true if succeeded
     */
    private boolean synchronizeDataOnUndo() {
        SGData data = this.getData();
        ElementGroupSetPropertiesInFigureElement p = (ElementGroupSetPropertiesInFigureElement) this.getProperties();
        if (data.setProperties(p.dataProperties) == false) {
            return false;
        }
        return true;
    }

    public boolean undo() {
        return this.setMementoBackward();
    }

    public boolean redo() {
        return this.setMementoForward();
    }

    public boolean updateHistory() {
        return this.mUndoManager.updateHistory();
    }

    public void initUndoBuffer() {
        this.mUndoManager.initUndoBuffer();
    }

    public boolean initPropertiesHistory() {
        return this.mUndoManager.initPropertiesHistory();
    }

    /**
     * Delete all forward histories.
     *
     * @return
     *         true if succeeded
     */
    public boolean deleteForwardHistory() {
	return this.mUndoManager.deleteForwardHistory();
    }

    /**
     * Clear changed flag of this undoable object and all child objects.
     *
     */
    public void clearChanged() {
        this.setChanged(false);
    }

    /**
     * A class of properties of a group set in figure element.
     */
    public static class ElementGroupSetPropertiesInFigureElement extends
            ElementGroupSetProperties {

        // name of data
        String name;

        // visible in legend
        boolean visibleInLegend;
        
        SGIntegerSeriesSet animationArraySection;
        
        double frameRate;
        
        boolean loopPlayback;

        // x-axis
        int xAxis;

        // y-axis
        int yAxis;

        // properties of data
        DataProperties dataProperties;

        Map<Integer, String> labelStringList = new HashMap<Integer, String>();
        
        /**
         * The default constructor.
         */
        public ElementGroupSetPropertiesInFigureElement() {
            super();
        }

        /**
         * Returns whether this object is equal to given object.
         *
         * @param obj
         *            an object to be compared
         * @return true if two objects are equal
         */
        public boolean equals(final Object obj) {

            if ((obj instanceof ElementGroupSetPropertiesInFigureElement) == false) {
                return false;
            }

            if (super.equals(obj) == false) {
                return false;
            }

            ElementGroupSetPropertiesInFigureElement p = (ElementGroupSetPropertiesInFigureElement) obj;

            if (p.name.equals(this.name) == false) {
                return false;
            }
            if (p.visibleInLegend != this.visibleInLegend) {
                return false;
            }
            if (!SGUtility.equals(this.animationArraySection, p.animationArraySection)) {
            	return false;
            }
            if (p.frameRate != this.frameRate) {
            	return false;
            }
            if (p.loopPlayback != this.loopPlayback) {
            	return false;
            }
            if (p.xAxis != this.xAxis) {
                return false;
            }
            if (p.yAxis != this.yAxis) {
                return false;
            }
            if (SGUtility.equals(p.dataProperties, this.dataProperties) == false) {
                return false;
            }
            if (p.labelStringList.equals(this.labelStringList) == false) {
                return false;
            }
            if (p.mElementGroupPropertiesList.equals(this.mElementGroupPropertiesList) == false) {
            	return false;
            }

            return true;
        }

        /**
         * Returns string representation of label list in this object.
         *
         * @return string representation of label list in this object
         */
        public String toStringLabelList() {
            StringBuffer sb = new StringBuffer();
            if (this.labelStringList.size()>0) {
                sb.append(", labelList=");
                Iterator<Integer> it = this.labelStringList.keySet().iterator();
                while (it.hasNext()) {
                    Integer v = it.next();
                    sb.append("("+v.intValue()+","+this.labelStringList.get(v)+")");
                }
            }
            return sb.toString();
        }

        /**
         * Copy this object.
         * @return a copied object
         */
        public Object copy() {
            Object obj = super.copy();
            ElementGroupSetPropertiesInFigureElement p = (ElementGroupSetPropertiesInFigureElement) obj;
            if (this.dataProperties != null) {
                p.dataProperties = (DataProperties) this.dataProperties.copy();
            }
            p.labelStringList.clear();
            p.labelStringList.putAll(this.labelStringList);
            return p;
        }

        /**
         * Dispose this object.
         */
        public void dispose() {
            super.dispose();
            if (this.dataProperties != null) {
                this.dataProperties.dispose();
                this.dataProperties = null;
            }
            this.labelStringList.clear();
            this.labelStringList = null;
            this.name = null;
        }
    }


    /**
     * Returns an array of titles.
     * @return
     *        an array of titles
     */
    public String[] getTitleArray() {
        SGData data = this.getData();
        if (!(data instanceof SGSDArrayData)) {
            throw new Error("Data is not of array type.");
        }
        SGSDArrayData aData = (SGSDArrayData) data;
        return aData.getTitles();
    }

    /**
     * Returns a map which has data information.
     * @return
     *        a map which has data information
     */
    public Map<String, Object> getInfoMap() {
        return this.getData().getInfoMap();
    }

    /**
     * Sets the information of data columns.
     * @param columns
     *                information of data columns
     */
    public boolean setColumnInfo(SGDataColumnInfo[] columns, String message) {
        SGData data = this.getData();
        if (!(data instanceof SGArrayData)) {
            throw new Error("Data is not of array type.");
        }
        SGArrayData aData = (SGArrayData) data;
        return aData.setColumnInfo(columns);
    }

    /**
     * Returns the type of data.
     * @return
     *         the type of data
     */
    public String getDataType() {
        return this.getData().getDataType();
    }

    /**
     * Returns an array of information of data columns.
     * @return
     *        an array of information of data columns
     */
    public SGDataColumnInfo[] getDataColumnInfoArray() {
        SGData data = this.getData();
        SGDataColumnInfo[] colArray = null;
        if (SGDataUtility.isSDArrayData(data)) {
            SGSDArrayData aData = (SGSDArrayData) data;
            colArray = aData.getColumnInfo();
        } else if (SGDataUtility.isNetCDFData(data)) {
            SGNetCDFData nData = (SGNetCDFData) data;
            colArray = nData.getColumnInfo();
        } else if (SGDataUtility.isMDArrayData(data)) {
        	SGMDArrayData mdData = (SGMDArrayData) data;
        	colArray = mdData.getColumnInfo();
        } else {
            throw new Error("Data type is not supported: " + data.getDataType());
        }
        return colArray;
    }

    /**
     * Returns a label for a data object.
     *
     * @return a label for a data object
     */
    public DataLabel getDataLabel() {
        SGData data = this.getData();
        StringBuffer sb = new StringBuffer();
        int x = 0;
        int y = 0;
        boolean valid = false;
        if (data instanceof SGNetCDFData) {
        	SGNetCDFData nData = (SGNetCDFData) data;
            SGNetCDFFile ncFile = nData.getNetcdfFile();
            List<SGNetCDFVariable> varList = ncFile.getVariables();
            final int size = varList.size();
            boolean first = true;
            for (int ii = 0; ii < size; ii++) {
            	SGNetCDFVariable var = varList.get(ii);

                // only for coordinate variables
                if (var.isCoordinateVariable() == false) {
                    continue;
                }

                // get the name
                String name = var.getName();

                // only fixed coordinate variable
                if (nData.isFixedCoordinateVariable(name) == false) {
                    continue;
                }

                // get value
                int origin = -1;
                if (nData instanceof SGSXYNetCDFMultipleData) {
                	SGSXYNetCDFMultipleData dData = (SGSXYNetCDFMultipleData) nData;
                	if (dData.isDimensionPicked()) {
                        if (SGDataUtility.isSameNetCDFGroup(name, dData.getDimensionName())) {
                            if (name.equals(dData.getDimensionName())) {
                            	int[] array = dData.getIndices().getNumbers();
                            	origin = array[0];
                            } else {
                                origin = nData.getOrigin(name);
                            }
                        } else {
                            continue;
                        }
                	} else {
                        origin = nData.getOrigin(name);
                	}
                } else {
                    origin = nData.getOrigin(name);
                }
                if (origin == -1) {
                    continue;
                }
                double value;
                try {
                    value = nData.getCoordinateValue(name, origin);
                } catch (IOException e) {
                    return null;
                }

                valid = true;

                // get unit string
                String unit = var.getUnitsString();

                // add escape characters
                String escName = SGUtility.addEscapeChar(var.getName());
                String escUnit = null;
                if (unit != null) {
                    escUnit = SGUtility.addEscapeChar(unit);
                }

                // append values
                if (!first) {
                    sb.append(", ");
                }
                sb.append(escName);
                sb.append('=');
                sb.append(value);
                if (escUnit != null) {
                    sb.append('[');
                    sb.append(escUnit);
                    sb.append(']');
                }

                first = false;
            }
        } else {
            // not supported
            return null;
        }
        if (!valid) {
        	return null;
        }
        DataLabel dl = new DataLabel(sb.toString(), x, y);
        return dl;
    }

    /**
     * Add ID and string of label into list stored.
     * @param index
     * @param initialLabelText
     * @return
     */
    public boolean addLabelStringId(final int index, final String initialLabelText) {
        Integer key = Integer.valueOf(index);
        String s = this.mLabelStringList.get(key);
        if (s==null || s.equals(initialLabelText)==false) {
            this.mLabelStringList.put(Integer.valueOf(index), initialLabelText);
            return true;
        }
        return false;
    }

    /**
     * Temporary data properties.
     */
    protected SGProperties mTempDataProperties = null;

    /**
     * Returns the number of frames.
     *
     * @return the number of frames
     */
    public int getFrameNumber() {
        if (SGDataUtility.isNetCDFData(this.mData)) {
            SGNetCDFData data = (SGNetCDFData) this.mData;
            double[] array = data.getTimeValueArray();
            if (array == null) {
                return -1;
            }
            return array.length;
        } else if (SGDataUtility.isMDArrayData(this.mData)) {
        	SGMDArrayData data = (SGMDArrayData) this.mData;
        	return data.getTimeDimensionLength();
        } else {
            throw new Error("Animation is not supported.");
        }
    }

    /**
     * Returns the current frame index.
     *
     * @return the current frame index
     */
    public int getCurrentFrameIndex() {
        if (SGDataUtility.isNetCDFData(this.mData)) {
            SGNetCDFData data = (SGNetCDFData) this.mData;
            return data.getCurrentTimeValueIndex();
        } else if (SGDataUtility.isMDArrayData(this.mData)) {
        	SGMDArrayData data = (SGMDArrayData) this.mData;
        	return data.getCurrentTimeValueIndex();
        } else {
            throw new Error("Animation is not supported.");
        }
    }

    /**
     * Sets the current frame index.
     *
     * @param index the frame index to be set
     */
    public void setCurrentFrameIndex(int index) {
        if (SGDataUtility.isNetCDFData(this.mData)) {
            SGNetCDFData data = (SGNetCDFData) this.mData;
            data.setCurrentTimeValueIndex(index);
        } else if (SGDataUtility.isMDArrayData(this.mData)) {
        	SGMDArrayData data = (SGMDArrayData) this.mData;
            data.setCurrentTimeValueIndex(index);
        } else {
            throw new Error("Animation is not supported.");
        }
    }

    /**
     * Saves all changes of this data source.
     *
     */
    @Override
    public void saveChanges() {
//        if (SGDataUtility.isNetCDFData(this.mData)) {
//            SGNetCDFData data = (SGNetCDFData) this.mData;
//            SGProperties p = data.getProperties();
//            if (p.equals(this.mTempDataProperties) == false) {
//                this.onSaveChanges();
//            }
//            this.mTempDataProperties = null;
//        } else if (SGDataUtility.isMDArrayData(this.mData)) {
//            SGMDArrayData data = (SGMDArrayData) this.mData;
//            SGProperties p = data.getProperties();
//            if (p.equals(this.mTempDataProperties) == false) {
//                this.onSaveChanges();
//            }
//            this.mTempDataProperties = null;
//        } else {
//            throw new Error("Animation is not supported.");
//        }
        SGProperties p = this.getProperties();
        if (p.equals(this.mTempDataProperties) == false) {
            this.onSaveChanges();
        }
        this.mTempDataProperties = null;
    }

    protected abstract void onSaveChanges();

    /**
     * Cancels all changes of this data source.
     *
     */
    @Override
    public void cancelChanges() {
//        if (SGDataUtility.isNetCDFData(this.mData)) {
//            SGNetCDFData data = (SGNetCDFData) this.mData;
//            data.setProperties(this.mTempDataProperties);
//
//            // update drawing elements
//            this.updateWithData();
//            this.mTempDataProperties = null;
//        } else if (SGDataUtility.isMDArrayData(this.mData)) {
//            SGMDArrayData data = (SGMDArrayData) this.mData;
//            data.setProperties(this.mTempDataProperties);
//
//            // update drawing elements
//            this.updateWithData();
//            this.mTempDataProperties = null;
//        } else {
//            throw new Error("Animation is not supported.");
//        }
    	this.setProperties(this.mTempDataProperties);
        this.updateWithData();
        this.mTempDataProperties = null;
    }

    /**
     * Returns the name of data source.
     *
     * @return the name of this animation data source
     */
    public String getDataSourceName() {
    	if (SGDataUtility.isNetCDFData(this.mData)) {
            SGNetCDFData data = (SGNetCDFData) this.mData;
            SGNetCDFVariable var = data.getTimeVariable();
            if (var != null) {
                return var.getName();
            } else {
                return null;
            }
    	} else if (SGDataUtility.isMDArrayData(this.mData)) {
    		return this.getName();
    	} else {
    		throw new Error("Invalid data type: " + this.mData.getDataType());
    	}
    }

    /**
     * Returns the unit string of data source.
     *
     * @return the unit string of data source
     */
    public String getDataSourceUnitString() {
    	if (SGDataUtility.isNetCDFData(this.mData)) {
            SGNetCDFData data = (SGNetCDFData) this.mData;
            SGNetCDFVariable var = data.getTimeVariable();
            if (var != null) {
                return var.getUnitsString();
            } else {
                return null;
            }
    	} else if (SGDataUtility.isMDArrayData(this.mData)) {
    		return "";
    	} else {
    		throw new Error("Invalid data type: " + this.mData.getDataType());
    	}
    }

    /**
     * Returns the current value of data object.
     *
     * @return the current value of data object
     */
    public Number getCurrentValue() {
    	if (SGDataUtility.isNetCDFData(this.mData)) {
            SGNetCDFData data = (SGNetCDFData) this.mData;
            return data.getCurrentTimeValue();
    	} else if (SGDataUtility.isMDArrayData(this.mData)) {
    		SGMDArrayData data = (SGMDArrayData) this.mData;
    		return data.getCurrentTimeValue();
    	} else {
    		throw new Error("Invalid data type: " + this.mData.getDataType());
    	}
    }

    /**
     * Sets the properties.
     *
     * @param map
     *           a map of properties
     * @return the result of setting properties
     */
    public SGPropertyResults setProperties(SGPropertyMap map) {
        SGPropertyResults result = new SGPropertyResults();

        // prepare
        if (this.prepare() == false) {
            return null;
        }

        result = this.setProperties(map, result);
        if (result == null) {
            return null;
        }

        // commit the changes
        if (this.commit() == false) {
            return null;
        }
        this.notifyToRoot();

        return result;
    }

    protected void setFailedColumnTypeResult(SGPropertyMap map, SGPropertyResults result) {
        result.putResult(COM_DATA_COLUMN_TYPE, SGPropertyResults.INVALID_INPUT_VALUE);
		List<String> keys = map.getKeys();
		String[] skippedKeys = {COM_DATA_ARRAY_SECTION, COM_DATA_X_ARRAY_SECTION, COM_DATA_Y_ARRAY_SECTION,
				COM_DATA_TICK_LABEL_ARRAY_SECTION, COM_DATA_INDEX_ARRAY_SECTION, COM_DATA_ANIMATION_FRAME_DIMENSION,
				COM_DATA_PICKUP_DIMENSION, COM_DATA_PICKUP_START, COM_DATA_PICKUP_END,
				COM_DATA_PICKUP_STEP, COM_DATA_PICKUP_INDICES};
		for (String sKey : skippedKeys) {
	        if (keys.contains(sKey.toUpperCase())) {
	            result.putResult(sKey, SGPropertyResults.SKIPPED);
	        }
		}
    }

    protected SGPropertyResults setProperties(SGPropertyMap map,
            SGPropertyResults iResult) {
        SGPropertyResults result = (SGPropertyResults) iResult.clone();
        Iterator<String> itr = map.getKeyIterator();
        while (itr.hasNext()) {
            String key = itr.next();
            String value = map.getValueString(key);
            if (COM_DATA_AXIS_X.equalsIgnoreCase(key)) {
                final int loc = SGUtility.getAxisLocation(value);
                if (loc == -1) {
                    result.putResult(COM_DATA_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setXAxisLocation(loc) == false) {
                    result.putResult(COM_DATA_AXIS_X, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_AXIS_X, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_AXIS_Y.equalsIgnoreCase(key)) {
                final int loc = SGUtility.getAxisLocation(value);
                if (loc == -1) {
                    result.putResult(COM_DATA_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setYAxisLocation(loc) == false) {
                    result.putResult(COM_DATA_AXIS_Y, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_AXIS_Y, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_VISIBLE_IN_LEGEND.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_VISIBLE_IN_LEGEND, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                if (this.setVisibleInLegend(b.booleanValue()) == false) {
                    result.putResult(COM_DATA_VISIBLE_IN_LEGEND, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_VISIBLE_IN_LEGEND, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ANIMATION_FRAME_RATE.equalsIgnoreCase(key)) {
                Double rate = SGUtilityText.getDouble(value);
                if (rate == null || rate.doubleValue() <= 0.0) {
                    result.putResult(COM_DATA_ANIMATION_FRAME_RATE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setFrameRate(rate.doubleValue());
                result.putResult(COM_DATA_ANIMATION_FRAME_RATE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ANIMATION_LOOP_PLAYBACK.equalsIgnoreCase(key)) {
                Boolean b = SGUtilityText.getBoolean(value);
                if (b == null) {
                    result.putResult(COM_DATA_ANIMATION_LOOP_PLAYBACK, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setLoopPlaybackAvailable(b.booleanValue());
                result.putResult(COM_DATA_ANIMATION_LOOP_PLAYBACK, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_NAME.equalsIgnoreCase(key)) {
            	if (map.isDoubleQuoted(key) == false) {
                    result.putResult(COM_DATA_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
            	String name = value;
                if (this.setName(name) == false) {
                    result.putResult(COM_DATA_NAME, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                result.putResult(COM_DATA_NAME, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ORIGIN.equalsIgnoreCase(key)
            		|| COM_DATA_COORDINATE_VARIABLES_INDEX.equalsIgnoreCase(key)) {
            	if (SGDataUtility.isNetCDFData(this.mData)) {
            		SGNetCDFData nData = (SGNetCDFData) this.mData;
                	boolean succeeded = true;

                	// parse the input string
                	String[] values = SGUtilityText.getStringsInBracket(value);
                	if (values == null) {
                        result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	if (values.length == 0) {
                        result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	String[] dimArray = new String[values.length];
                	int[] originArray = new int[values.length];
                	for (int ii = 0; ii < values.length; ii++) {
                		final int openIndex = values[ii].indexOf('(');
                		if (openIndex == -1) {
                			succeeded = false;
                			break;
                		}
                		final int closedIndex = values[ii].lastIndexOf(')');
                		if (closedIndex == -1) {
                			succeeded = false;
                			break;
                		}
                		String dim = values[ii].substring(0, openIndex);
                		String origin = values[ii].substring(openIndex + 1, closedIndex);
                		Integer num = SGUtilityText.getInteger(origin.trim());
                		if (num == null) {
                			succeeded = false;
                			break;
                		}
                		dimArray[ii] = dim.trim();
                		originArray[ii] = num.intValue();
                	}
                	if (!succeeded) {
                        result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}

                	// set to the data
                	for (int ii = 0; ii < values.length; ii++) {
                		if (nData.setOrigin(dimArray[ii], originArray[ii]) == false) {
                			succeeded = false;
                			break;
                		}
                	}
                	if (!succeeded) {
                        result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
            	} else if (SGDataUtility.isMDArrayData(this.mData)) {
            		SGMDArrayData mdData = (SGMDArrayData) this.mData;
                	boolean succeeded = true;

                	// parses the input string
                    String innerValue = SGUtilityText.getInnerString(value, '(', ')');

                    List<String> valueList = new ArrayList<String>();
                    char[] allCharArray = innerValue.toCharArray();
                    int depth = 0;
                	List<Character> cList = new ArrayList<Character>();
                    for (int ii = 0; ii < allCharArray.length; ii++) {
                    	final char c = allCharArray[ii];
                    	final boolean atLast = (ii == allCharArray.length - 1);
                    	if ((c == ',' && depth == 0) || atLast) {
                    		if (atLast) {
                    			cList.add(c);
                    		}
                			char[] cArray = new char[cList.size()];
                			for (int jj = 0; jj < cArray.length; jj++) {
                				cArray[jj] = cList.get(jj);
                			}
                			String str = new String(cArray);
                			valueList.add(str);
                			cList.clear();
                			continue;
                    	} else if (c == '(') {
                    		depth++;
                    	} else if (c == ')') {
                    		depth--;
                    	}
                		cList.add(c);
                    }

                	String[] values = new String[valueList.size()];
                	values = valueList.toArray(values);
                	if (values.length == 0) {
                        result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}
                	DimensionInfo[] dimInfoArray = new DimensionInfo[values.length];
                	for (int ii = 0; ii < values.length; ii++) {
                		final int openIndex = values[ii].indexOf('(');
                		if (openIndex == -1) {
                			succeeded = false;
                			break;
                		}
                		final int closedIndex = values[ii].lastIndexOf(')');
                		if (closedIndex == -1) {
                			succeeded = false;
                			break;
                		}

                		String varName = values[ii].substring(0, openIndex).trim();
                		SGMDArrayVariable var = mdData.findVariable(varName);
                		if (var == null) {
                			succeeded = false;
                			break;
                		}
                		int[] dims = var.getDimensions();
                		DimensionInfo dimInfo = new DimensionInfo();
                		dimInfo.varName = varName;
                		dimInfo.origins = var.getOrigins().clone();

                		String origins = values[ii].substring(openIndex + 1, closedIndex);
                		String[] oArray = origins.split(",");
                		for (int jj = 0; jj < oArray.length; jj++) {
                			String[] oPair = oArray[jj].split(":");
                			if (oPair.length != 2) {
                    			succeeded = false;
                    			break;
                			}
                			Integer dim = SGUtilityText.getInteger(oPair[0].trim());
                			if (dim == null) {
                    			succeeded = false;
                    			break;
                			}
                			if (dim < 0 || dimInfo.origins.length <= dim) {
                    			succeeded = false;
                    			break;
                			}
                			Integer origin = SGUtilityText.getInteger(oPair[1].trim());
                			if (origin == null) {
                				succeeded = false;
                				break;
                			}
                			if (origin < 0 || dims[dim] <= origin) {
                				succeeded = false;
                				break;
                			}

                			dimInfo.origins[dim] = origin;
                		}
                		if (!succeeded) {
                			break;
                		}

                		dimInfoArray[ii] = dimInfo;
                	}
                	if (!succeeded) {
                        result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                        continue;
                	}

                	// set to the data
                	for (int ii = 0; ii < values.length; ii++) {
                		DimensionInfo dimInfo = dimInfoArray[ii];
                		if (mdData.setOrigin(dimInfo.varName, dimInfo.origins) == false) {
                			succeeded = false;
                			break;
                		}
                	}

            	} else {
                    result.putResult(COM_DATA_ORIGIN, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
            	}
                result.putResult(COM_DATA_ORIGIN, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_COLUMN_TYPE.equalsIgnoreCase(key)) {
            	if (!this.setDataColumnType(value, map, result)) {
            		continue;
            	}
                result.putResult(COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);
            } else if (COM_DATA_ARRAY_SECTION_AVAILABLE.equalsIgnoreCase(key)) {
                Boolean strideAvailable = SGUtilityText.getBoolean(value);
                if (strideAvailable == null) {
                    result.putResult(COM_DATA_ARRAY_SECTION_AVAILABLE, SGPropertyResults.INVALID_INPUT_VALUE);
                    continue;
                }
                this.setStrideAvailable(strideAvailable);
                result.putResult(COM_DATA_ARRAY_SECTION_AVAILABLE, SGPropertyResults.SUCCEEDED);
            }
        }

        return result;
    }
    
    protected boolean setDataColumnType(String value, SGPropertyMap map, SGPropertyResults result) {
    	
    	String[] values = SGUtilityText.getStringsInBracket(value);
    	if (values == null) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	if (values.length == 0) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

    	final boolean isSDArrayData = SGDataUtility.isSDArrayData(this.mData);
    	final boolean isNetCDFData = SGDataUtility.isNetCDFData(this.mData);
    	final boolean isMDArrayData = SGDataUtility.isMDArrayData(this.mData);

    	// get arrays of column names or indices and values for each column
    	boolean succeeded = true;
    	String[] colNames = new String[values.length];
    	String[] colValues = new String[values.length];
    	@SuppressWarnings("unchecked")
		Map<String, Integer>[] colDims = new Map[values.length];
    	if (isSDArrayData || isNetCDFData) {
        	for (int ii = 0; ii < values.length; ii++) {
        		String[] tokens = values[ii].split(":");
        		if (tokens == null || tokens.length != 2) {
        			succeeded = false;
        			break;
        		}
        		colNames[ii] = tokens[0].trim();
        		colValues[ii] = tokens[1].trim();
        	}
    	} else if (isMDArrayData) {
    		SGMDArrayData mdData = (SGMDArrayData) this.mData;
			String dataType = mdData.getDataType();
    		if (SGDataUtility.isSXYTypeData(dataType)) {
            	for (int ii = 0; ii < values.length; ii++) {
            		String[] tokens = values[ii].split(":");
            		if (tokens == null || tokens.length != 3) {
            			succeeded = false;
            			break;
            		}
            		Integer dim = SGUtilityText.getInteger(tokens[1]);
            		if (dim == null) {
            			succeeded = false;
            			break;
            		}
            		String varName = tokens[0].trim();
            		SGMDArrayVariable var = mdData.findVariable(varName);
            		if (var == null) {
            			succeeded = false;
            			break;
            		}
            		int[] dims = var.getDimensions();
            		if (dim < 0 || dims.length <= dim) {
            			succeeded = false;
            			break;
            		}

            		colDims[ii] = new HashMap<String, Integer>();
            		colDims[ii].put(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, dim);
            		colDims[ii].put(SGIMDArrayConstants.KEY_TIME_DIMENSION, -1);	// fixed value
            		colNames[ii] = varName;
            		colValues[ii] = tokens[2].trim();
            	}
    		} else {
    			// SXYZ or VXY
    			String keyX, keyY;
    			if (SGDataUtility.isSXYZTypeData(dataType)) {
    				keyX = SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION;
    				keyY = SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION;
    			} else if (SGDataUtility.isVXYTypeData(dataType)) {
    				keyX = SGIMDArrayConstants.KEY_VXY_X_DIMENSION;
    				keyY = SGIMDArrayConstants.KEY_VXY_Y_DIMENSION;
    			} else {
    				throw new Error("Invalid data type: ");
    			}
            	for (int ii = 0; ii < values.length; ii++) {
            		String[] tokens = values[ii].split(":");
            		if (tokens == null) {
            			succeeded = false;
            			break;
            		}
            		colDims[ii] = new HashMap<String, Integer>();
            		if (tokens.length == 3) {
                		String varName = tokens[0].trim();
                		colNames[ii] = varName;
                		colValues[ii] = tokens[2].trim();
                		Integer dim = SGUtilityText.getInteger(tokens[1]);
                		if (dim == null) {
                			succeeded = false;
                			break;
                		}
                		SGMDArrayVariable var = mdData.findVariable(varName);
                		if (var == null) {
                			succeeded = false;
                			break;
                		}
                		int[] dims = var.getDimensions();
                		if (dim < 0 || dims.length <= dim) {
                			succeeded = false;
                			break;
                		}
                		colDims[ii].put(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, dim);
            		} else if (tokens.length == 4) {
                		String varName = tokens[0].trim();
                		colNames[ii] = varName;
                		colValues[ii] = tokens[3].trim();
                		Integer dimX = SGUtilityText.getInteger(tokens[1]);
                		if (dimX == null) {
                			succeeded = false;
                			break;
                		}
                		Integer dimY = SGUtilityText.getInteger(tokens[2]);
                		if (dimY == null) {
                			succeeded = false;
                			break;
                		}
                		SGMDArrayVariable var = mdData.findVariable(varName);
                		if (var == null) {
                			succeeded = false;
                			break;
                		}
                		int[] dims = var.getDimensions();
                		if (dimX < 0 || dims.length <= dimX) {
                			succeeded = false;
                			break;
                		}
                		if (dimY < 0 || dims.length <= dimY) {
                			succeeded = false;
                			break;
                		}
                		colDims[ii].put(keyX, dimX);
                		colDims[ii].put(keyY, dimY);
                		colDims[ii].put(SGIMDArrayConstants.KEY_GENERIC_DIMENSION, -1);	// fixed value
            		} else {
            			succeeded = false;
            			break;
            		}
            		colDims[ii].put(SGIMDArrayConstants.KEY_TIME_DIMENSION, -1);	// fixed value
            	}
    		}
    	}
    	if (!succeeded) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

		// check duplication of column names
    	for (int ii = 0; ii < colNames.length - 1; ii++) {
    		for (int jj = ii + 1; jj < colNames.length; jj++) {
    			if (colNames[ii].equals(colNames[jj])) {
    				succeeded = false;
    				break;
    			}
    		}
    	}
    	if (!succeeded) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	
    	String[] columnTypeArray = SGDataUtility.getCanonicalColumnTypes(colValues);

		if (isSDArrayData) {
			if (!this.setDataColumnType((SGSDArrayData) this.mData, colNames,
					columnTypeArray, map, result)) {
				return false;
			}
		} else if (isNetCDFData) {
			if (!this.setDataColumnType((SGNetCDFData) this.mData, colNames,
					columnTypeArray, map, result)) {
				return false;
			}
    	} else if (isMDArrayData) {
			if (!this.setDataColumnType((SGMDArrayData) this.mData, colNames,
					columnTypeArray, colDims, map, result)) {
				return false;
			}
    	}

    	return true;
    }
    
    private boolean setDataColumnType(SGSDArrayData aData, String[] colNames, String[] colValues,
    		SGPropertyMap map, SGPropertyResults result) {
    	boolean succeeded = true;
    	
		// get column indices
		int[] colIndices = new int[colNames.length];
		for (int ii = 0; ii < colNames.length; ii++) {
			Integer colIndex = SGUtilityText.getInteger(colNames[ii]);
			if (colIndex == null) {
				succeeded = false;
				break;
			}
			colIndices[ii] = colIndex.intValue();
		}
    	if (!succeeded) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

    	// create an array of column types
    	final int dataColNum = aData.getColNum();
    	String[] columnTypes = new String[dataColNum];
    	for (int ii = 0; ii < columnTypes.length; ii++) {
    		columnTypes[ii] = "";
    	}
    	for (int ii = 0; ii < colIndices.length; ii++) {
    		final int index = colIndices[ii] - 1;
    		if (index < 0 || index >= columnTypes.length) {
				succeeded = false;
    			break;
    		}
    		columnTypes[index] = colValues[ii];
    	}
    	if (!succeeded) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

    	// set to the data
    	if (aData.setColumnType(columnTypes) == false) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	
    	SGDataColumnInfo[] columns = aData.getColumnInfo();
    	
    	// checks the input
    	String dataType = aData.getDataType();
    	Map<String, Object> infoMap = aData.getInfoMap();
    	if (SGDataUtility.checkDataColumns(dataType, columns, infoMap) == false) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

    	if (this.setProperties(map, result, columns) == false) {
    		return false;
    	}

    	return true;
    }
    
    private boolean setDataColumnType(SGNetCDFData nData, String[] colNames, String[] colValues,
    		SGPropertyMap map, SGPropertyResults result) {
    	boolean succeeded = true;

		SGNetCDFFile ncfile = nData.getNetcdfFile();
		List<SGNetCDFVariable> varList = ncfile.getVariables();

    	// create an array of column types
		String[] columnTypes = new String[varList.size()];
    	for (int ii = 0; ii < columnTypes.length; ii++) {
    		columnTypes[ii] = "";
    	}
    	for (int ii = 0; ii < colNames.length; ii++) {
        	final int index = ncfile.getVariableIndex(colNames[ii]);
        	if (index == -1) {
				succeeded = false;
    			break;
        	}
        	columnTypes[index] = colValues[ii];
    	}
    	if (!succeeded) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	SGDataColumnInfo[] columns = nData.getColumnInfo();
    	for (int ii = 0; ii < columns.length; ii++) {
    		columns[ii].setColumnType(columnTypes[ii]);
    	}

    	// checks the input
    	String dataType = nData.getDataType();
    	Map<String, Object> infoMap = nData.getInfoMap();
    	if (SGDataUtility.checkDataColumns(dataType, columns, infoMap) == false) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	if (SGDataUtility.isSXYTypeData(dataType)) {
    		// updates the information map
    		List<SGDataColumnInfo> pickUpList = SGDataUtility.findColumnsWithColumnType(
    				columns, SGIDataColumnTypeConstants.PICKUP);
    		final boolean multipleVariable = !(pickUpList.size() == 1);
			infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, multipleVariable);
    	}
    	if (SGDataUtility.checkNetCDFDataColumns(columns, dataType,
    			nData.getNetcdfFile(), infoMap) == false) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

    	if (this.setProperties(map, result, columns) == false) {
    		return false;
    	}
    	
    	return true;
    }
    
    private boolean setDataColumnType(SGMDArrayData mdData, String[] colNames, String[] colValues,
    		Map<String, Integer>[] colDims,
    		SGPropertyMap map, SGPropertyResults result) {
    	boolean succeeded = true;
    	
		SGMDArrayVariable[] vars = mdData.getVariables();

    	// create an array of column types
		String[] columnTypes = new String[vars.length];
    	@SuppressWarnings("unchecked")
		Map<String, Integer>[] dimensionMaps = new Map[vars.length];
    	for (int ii = 0; ii < columnTypes.length; ii++) {
    		columnTypes[ii] = "";
    	}
    	for (int ii = 0; ii < colNames.length; ii++) {
        	final int index = mdData.getVariableIndex(colNames[ii]);
        	if (index == -1) {
				succeeded = false;
    			break;
        	}
        	columnTypes[index] = colValues[ii];
        	dimensionMaps[index] = colDims[ii];
    	}
    	if (!succeeded) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	SGDataColumnInfo[] columns = mdData.getColumnInfo();
    	for (int ii = 0; ii < columns.length; ii++) {
    		SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) columns[ii];
    		mdInfo.setColumnType(columnTypes[ii]);
    		if (dimensionMaps[ii] != null) {
    			mdInfo.clearDimensionIndices();
            	mdInfo.putAllDimensionIndices(dimensionMaps[ii]);
    		}
    	}

    	// checks the input
    	String dataType = mdData.getDataType();
    	Map<String, Object> infoMap = mdData.getInfoMap();
    	if (SGDataUtility.checkDataColumns(dataType, columns, infoMap) == false) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}
    	if (SGDataUtility.isSXYTypeData(dataType)) {
    		// updates the information map
    		List<SGDataColumnInfo> pickUpList = SGDataUtility.findColumnsWithColumnType(
    				columns, SGIDataColumnTypeConstants.PICKUP);
    		final boolean multipleVariable = !(pickUpList.size() == 1);
			infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, multipleVariable);
    	}
    	if (SGDataUtility.checkMDArrayDataColumns(columns, dataType,
    			mdData.getMDArrayFile(), infoMap) == false) {
    		this.setFailedColumnTypeResult(map, result);
    		return false;
    	}

    	if (this.setProperties(map, result, columns) == false) {
    		return false;
    	}

    	return true;
    }

	// clears time dimension
    protected void clearTimeDimension() {
    	if (SGDataUtility.isMDArrayData(this.mData)) {
    		SGMDArrayData mdData = (SGMDArrayData) this.mData;
    		SGMDArrayVariable[] vars = mdData.getVariables();
        	for (int ii = 0; ii < vars.length; ii++) {
        		String name = vars[ii].getName();
        		SGMDArrayVariable var = mdData.findVariable(name);
        		var.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, -1);
        	}
    	}
    }

    static class DimensionInfo {
    	String varName = null;
    	int[] origins = null;
    }

    protected boolean setTimeDimension(final String value) {
		if (!SGDataUtility.isMDArrayData(this.mData)) {
            return false;
		}
		SGMDArrayData mdData = (SGMDArrayData) this.mData;

		Map<String, Integer> timeDimMap = this.createTimeDimMap(mdData, value);
		if (timeDimMap == null) {
			return false;
		}

		return this.setTimeDimension(timeDimMap);
    }

    protected Map<String, Integer> createTimeDimMap(SGMDArrayData mdData, String value) {
    	String[] values = SGUtilityText.getStringsInBracket(value);
    	if (values == null) {
            return null;
    	}
    	if (values.length == 0) {
            return null;
    	}

    	Map<String, Map<String, Integer>> dimensionIndexMap = mdData.getUsedDimensionIndexMap();

    	boolean succeeded = true;
		Map<String, Integer> timeDimMap = new HashMap<String, Integer>();
		int dimLen = -1;
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
        		SGMDArrayVariable var = mdData.findVariable(name);
        		if (var == null) {
        			succeeded = false;
        			break;
        		}
        		final int[] dims = var.getDimensions();
        		if (dimIndex < 0 || dims.length <= dimIndex) {
        			succeeded = false;
        			break;
        		}

        		Map<String, Integer> indexMap = dimensionIndexMap.get(name);
        		Iterator<Entry<String, Integer>> indexItr = indexMap.entrySet().iterator();
        		while (indexItr.hasNext()) {
        			Entry<String, Integer> entry = indexItr.next();
        			if (entry.getKey().equals(SGIMDArrayConstants.KEY_TIME_DIMENSION)) {
        				continue;
        			}
        			Integer index = entry.getValue();
        			if (index == null || index == -1) {
        				continue;
        			}
        			if (dimIndex.equals(index)) {
            			succeeded = false;
            			break;
        			}
        		}
        		if (!succeeded) {
        			break;
        		}
        		if (dimLen == -1) {
        			dimLen = dims[dimIndex];
        		} else {
        			if (dims[dimIndex] != dimLen) {
            			succeeded = false;
            			break;
        			}
        		}
    		}
    		timeDimMap.put(name, dimIndex);
    	}
    	if (!succeeded) {
            return null;
    	}
    	return timeDimMap;
    }

    protected boolean setTimeDimension(Map<String, Integer> timeDimMap) {
		if (!SGDataUtility.isMDArrayData(this.mData)) {
            return false;
		}
		SGMDArrayData mdData = (SGMDArrayData) this.mData;

		// get current time dimension
		Map<String, Integer> curTimeDimMap = new HashMap<String, Integer>();
		SGDataColumnInfo[] cols = this.getDataColumnInfoArray();
		for (int ii = 0; ii < cols.length; ii++) {
			SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
			Integer timeDim = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
			if (timeDim == null || timeDim == -1) {
				continue;
			}
			if ("".equals(mdCol.getColumnType())) {
				continue;
			}
			int[] dims = mdCol.getDimensions();
			final int len = dims[timeDim];
			if (len != -1) {
				curTimeDimMap.put(mdCol.getName(), timeDim);
			}
		}

		// merges two maps
		Map<String, Integer> mergedTimeDimMap = new HashMap<String, Integer>(curTimeDimMap);
    	Iterator<Entry<String, Integer>> entryItr = timeDimMap.entrySet().iterator();
    	while (entryItr.hasNext()) {
    		Entry<String, Integer> entry = entryItr.next();
    		String name = entry.getKey();
    		Integer index = entry.getValue();
    		mergedTimeDimMap.put(name, index);
    	}

    	// checks the length
    	int curLen = -1;
    	entryItr = mergedTimeDimMap.entrySet().iterator();
    	while (entryItr.hasNext()) {
    		Entry<String, Integer> entry = entryItr.next();
    		String name = entry.getKey();
    		Integer index = entry.getValue();
    		if (index == -1) {
    			continue;
    		}
    		SGMDArrayVariable var = mdData.findVariable(name);
    		int[] dims = var.getDimensions();
    		if (curLen == -1) {
    			curLen = dims[index];
    		} else {
    			if (curLen != dims[index]) {
    				return false;
    			}
    		}
    	}

    	// sets to data
    	entryItr = timeDimMap.entrySet().iterator();
    	while (entryItr.hasNext()) {
    		Entry<String, Integer> entry = entryItr.next();
    		String name = entry.getKey();
    		Integer index = entry.getValue();
    		SGMDArrayVariable var = mdData.findVariable(name);
    		var.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, index);
    	}

    	// equalizes the origin of time dimension
    	this.equalizeTimeDimension(mdData);
    
    	// updates time stride
    	boolean timeStrideUpdate = false;
    	SGIntegerSeriesSet timeStride = mdData.getTimeStride();
    	if (timeStride != null) {
    		final int fullLen = timeStride.getEndIndex() + 1;
    		timeStrideUpdate = (fullLen != curLen);
    	} else {
    		timeStrideUpdate = true;
    	}
    	if (timeStrideUpdate) {
    		mdData.setTimeStride(SGIntegerSeriesSet.createInstance(curLen));
    	}

    	return true;
    }

    // equalizes the time dimension
	protected void equalizeTimeDimension(SGMDArrayData mdData) {
		final int fIndex = SGDataUtility.findFrequentTimeOrigin(this.getDataColumnInfoArray());
		if (fIndex != -1) {
			SGMDArrayVariable[] vars = mdData.getAssignedVariables();
			for (int ii = 0; ii < vars.length; ii++) {
				Integer timeDim = vars[ii].getDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION);
				if (timeDim != null && timeDim != -1) {
					int[] origins = vars[ii].getOrigins();
					origins[timeDim] = fIndex;
					vars[ii].setOrigins(origins);
				}
			}
		}
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
    protected abstract boolean setProperties(SGPropertyMap map, SGPropertyResults result,
    		SGDataColumnInfo[] cols);

    /**
     * Compares current data columns and temporary data columns.
     *
     * @param pTemp
     *          temporary properties
     * @return true if data columns are changed
     */
    protected boolean compareDataColumns(final SGProperties pTemp) {
        SGProperties pPresent = this.getProperties();
        ElementGroupSetPropertiesInFigureElement egTemp = (ElementGroupSetPropertiesInFigureElement) pTemp;
        ElementGroupSetPropertiesInFigureElement egPresent = (ElementGroupSetPropertiesInFigureElement) pPresent;
        return egTemp.dataProperties.hasEqualColumnTypes(egPresent.dataProperties);
    }

    protected boolean compareDataSize(final SGProperties pTemp) {
        SGProperties pPresent = this.getProperties();
        ElementGroupSetPropertiesInFigureElement egTemp = (ElementGroupSetPropertiesInFigureElement) pTemp;
        ElementGroupSetPropertiesInFigureElement egPresent = (ElementGroupSetPropertiesInFigureElement) pPresent;
        return egTemp.dataProperties.hasEqualSize(egPresent.dataProperties);
    }

    /**
     * Compares current data properties and temporary data properties.
     *
     * @param pTemp
     *          temporary properties
     * @return true if data properties are changed
     */
    protected boolean compareDataProperties(final SGProperties pTemp) {
        SGProperties pPresent = this.getProperties();
        ElementGroupSetPropertiesInFigureElement egTemp = (ElementGroupSetPropertiesInFigureElement) pTemp;
        ElementGroupSetPropertiesInFigureElement egPresent = (ElementGroupSetPropertiesInFigureElement) pPresent;
        return SGUtility.equals(egTemp.dataProperties, egPresent.dataProperties);
    }

    protected abstract void notifyToListener(final String msg);

    protected abstract void notifyToListener(final String msg, Object source);

    /**
     * Sets the data.
     *
     * @param data
     *            a data object
     * @return true if succeeded
     */
    public boolean setData(SGData data) {
    	this.mData = data;
    	return true;
    }

    /**
     * Returns whether this group set "contains" the given group set.
     * By default, this method returns the equality obtained by "equals" method.
     *
     * @param gs
     *           the group set
     * @return true if this group set "contains" the given group set
     */
    public boolean contains(SGElementGroupSetForData gs) {
    	return this.equals(gs);
    }

    /**
     * Saves data to given file.
     *
     * @param data
     *             data to be saved
     * @param policy
     *             the policy for saving data
     * @return true if succeeded
     */
    public boolean saveData(final File file, final SGExportParameter params, SGDataBufferPolicy policy) {
    	SGArrayData data = (SGArrayData) this.getData();
    	OPERATION mode = params.getType();
    	if (OPERATION.EXPORT_TO_FILE_AS_SAME_FORMAT.equals(mode)) {
    		return data.saveToSameFormatFile(file, params, policy);
    	} else if (OPERATION.EXPORT_TO_TEXT.equals(mode)) {
    		return data.saveToTextFile(file, params, policy);
    	} else if (OPERATION.EXPORT_TO_NETCDF.equals(mode)) {
    		return data.saveToNetCDFFile(file, params, policy);
    	} else if (OPERATION.EXPORT_TO_HDF5.equals(mode)) {
    		return data.saveToHDF5File(file, params, policy);
    	} else if (OPERATION.EXPORT_TO_MATLAB.equals(mode)) {
    		return data.saveToMATLABFile(file, params, policy);
    	} else if (SGDataUtility.isArchiveDataSetOperation(mode)) {
        	return data.saveToArchiveDataSetFile(file, params);
    	} else if (OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(mode)) {
            return data.saveToDataSetNetCDFFile(file);
    	}
    	return false;
    }

    /**
     * Sets whether the stride of data arrays is available.
     *
     * @param b
     *          true if the stride of data arrays is available
     */
    public void setStrideAvailable(final boolean b) {
		SGArrayData data = (SGArrayData) this.getData();
		data.setStrideAvailable(b);
	}

    protected boolean setSDArrayStride(String value) {
        SGData data = this.getData();
    	if (!(data instanceof SGSDArrayData)) {
    		return false;
    	}
		SGSDArrayData sdData = (SGSDArrayData) data;
    	final int len = sdData.getAllPointsNumber();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
    	if (stride == null) {
            return false;
    	}
    	sdData.setStride(stride);
        return true;
    }

    protected boolean setNetCDFIndexStride(String value) {
        SGData data = this.getData();
    	if (!(data instanceof SGNetCDFData)) {
    		return false;
    	}
    	SGNetCDFData ncData = (SGNetCDFData) data;
    	if (!ncData.isIndexAvailable()) {
    		return false;
    	}
    	final int len = ncData.getAllPointsNumber();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
    	if (stride == null) {
            return false;
    	}
    	ncData.setIndexStride(stride);
        return true;
    }

    protected boolean setStrideX(String value) {
        SGData data = this.getData();
    	if (!(data instanceof SGITwoDimensionalData)) {
    		return false;
    	}
    	if (data instanceof SGNetCDFData) {
    		SGNetCDFData ncData = (SGNetCDFData) data;
    		if (ncData.isIndexAvailable()) {
    			return false;
    		}
    	}
    	SGITwoDimensionalData tData = (SGITwoDimensionalData) data;
    	final int len = tData.getXDimensionLength();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
    	if (stride == null) {
            return false;
    	}
    	tData.setXStride(stride);
        return true;
    }

    protected boolean setStrideY(String value) {
        SGData data = this.getData();
    	if (!(data instanceof SGITwoDimensionalData)) {
    		return false;
    	}
    	if (data instanceof SGNetCDFData) {
    		SGNetCDFData ncData = (SGNetCDFData) data;
    		if (ncData.isIndexAvailable()) {
    			return false;
    		}
    	}
    	SGITwoDimensionalData tData = (SGITwoDimensionalData) data;
    	final int len = tData.getYDimensionLength();
    	SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(value, len);
    	if (stride == null) {
            return false;
    	}
    	tData.setYStride(stride);
        return true;
    }

    protected boolean setStrideXToData(SGIntegerSeriesSet stride) {
        SGData data = this.getData();
    	if (!(data instanceof SGITwoDimensionalData)) {
    		return false;
    	}
    	SGITwoDimensionalData tData = (SGITwoDimensionalData) data;
    	tData.setXStride(stride);
        return true;
	}

	protected boolean setStrideYToData(SGIntegerSeriesSet stride) {
        SGData data = this.getData();
    	if (!(data instanceof SGITwoDimensionalData)) {
    		return false;
    	}
    	SGITwoDimensionalData tData = (SGITwoDimensionalData) data;
    	tData.setYStride(stride);
        return true;
	}

    protected boolean setIndexStrideToData(SGIntegerSeriesSet stride) {
        SGData data = this.getData();
        if (!(data instanceof SGIIndexData)) {
        	return false;
        }
    	SGIIndexData indexData = (SGIIndexData) data;
		indexData.setIndexStride(stride);
        return true;
	}

    protected boolean setSDArrayStrideToData(SGIntegerSeriesSet stride) {
        SGData data = this.getData();
    	if (!(data instanceof SGSDArrayData)) {
    		return false;
    	}
    	SGSDArrayData sdData = (SGSDArrayData) data;
    	sdData.setStride(stride);
        return true;
	}

	protected boolean setSXYStrideToData(SGIntegerSeriesSet stride) {
        SGData data = this.getData();
        if (!(data instanceof SGISXYTypeData)) {
            return false;
        }
    	SGISXYTypeData sxyData = (SGISXYTypeData) data;
        sxyData.setStride(stride);
        return true;
	}

	protected boolean setTickLabelStrideToData(SGIntegerSeriesSet stride) {
        SGData data = this.getData();
        if (!(data instanceof SGISXYTypeData)) {
            return false;
        }
    	SGISXYTypeData sxyData = (SGISXYTypeData) data;
        sxyData.setTickLabelStride(stride);
        return true;
	}
	
	

    /**
     * Returns the array section for animation
     *
     * @return the array section for animation
     */
	@Override
	public SGIntegerSeriesSet getAnimationArraySection() {
		SGIntegerSeriesSet stride = null;
        SGData data = this.getData();
        if (data instanceof SGNetCDFData) {
        	SGNetCDFData ncData = (SGNetCDFData) data;
        	if (ncData.isTimeVariableAvailable()) {
        		stride = ncData.getTimeStride();
        	}
        } else if (data instanceof SGMDArrayData) {
        	SGMDArrayData mdData = (SGMDArrayData) data;
        	if (mdData.isTimeDimensionAvailable()) {
        		stride = mdData.getTimeStride();
        	}
        }
		return stride;
	}
	
    /**
     * Sets the array section for animation
     *
     * @param arraySection
     *           the array section for animation
     */
	@Override
    public void setFrameIndices(SGIntegerSeriesSet arraySection) {
        SGData data = this.getData();
        if (data instanceof SGNetCDFData) {
        	SGNetCDFData ncData = (SGNetCDFData) data;
        	ncData.setTimeStride(arraySection);
        } else if (data instanceof SGMDArrayData) {
        	SGMDArrayData mdData = (SGMDArrayData) data;
        	mdData.setTimeStride(arraySection);
        }
    }

    /**
     * Returns whether loop play back is available.
     * 
     * @return true if loop play back is available
     */
	@Override
    public boolean isLoopPlaybackAvailable() {
		return this.mLoopPlaybackFlag;
	}

    /**
     * Sets whether loop play back is available.
     * 
     * @param b
     *          the flag to set
     */
	@Override
    public void setLoopPlaybackAvailable(final boolean b) {
		this.mLoopPlaybackFlag = b;
	}

    /**
     * Returns the frame rate.
     * 
     * @return the frame rate
     */
	@Override
    public double getFrameRate() {
		return this.mFrameRate;
	}
    
    /**
     * Sets the frame rate.
     * 
     * @param rate
     *           the frame rate to set
     */
	@Override
    public void setFrameRate(final double rate) {
		if (rate < 0.0) {
			throw new IllegalArgumentException("rate < 0.0: " + rate);
		}
		this.mFrameRate = rate;
	}

	/**
	 * Sets the style of drawing elements.
	 *
	 * @param styleList
	 *          the list of style
	 * @return true if succeeded
	 */
    public boolean setStyle(List<SGStyle> styleList) {
    	// do nothing by default
    	return true;
    }

	/**
	 * Returns the style of drawing elements.
	 *
	 * @return the list of style
	 */
    public List<SGStyle> getStyle() {
    	// returns null by default
    	return null;
    }

    /**
     * Returns whether data object is anchored.
     * 
     * @return true if data object is anchored
     */
    public boolean isAnchored() {
    	return this.mAnchorFlag;
    }

    /**
     * Sets the flag whether data object is anchored
     * 
     * @param b
     *           true to set data object anchored
     */
    public void setAnchored(final boolean b) {
    	this.mAnchorFlag = b;
    }

    /**
     * Returns the figure element.
     * 
     * @return the figure element
     */
    public abstract SGFigureElementForData getFigureElement();
    
	/**
	 * Returns true if all stride of this data are available and each string representation 
	 * is different from "0:end".
	 * 
	 * @return true if all stride are effective
	 */
	@Override
	public boolean hasEffectiveStride() {
		return this.getData().hasEffectiveStride();
	}

    /**
     * Creates and returns the map of properties.
     * 
     * @return the map of properties
     */
    @Override
    public SGPropertyMap getCommandPropertyMap(SGExportParameter params) {
    	SGPropertyMap map = new SGPropertyMap();
		SGArrayData data = (SGArrayData) this.getData();

		OPERATION type = params.getType();
		if (OPERATION.SAVE_INTO_FILE_ATTRIBUTE.equals(type)) {
			SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_FILE_PATH, FILE_PATH_NETCDF_ITSELF);
		}
    	SGPropertyUtility.addProperty(map, COM_DATA_TYPE, data.getDataType());
    	SGPropertyUtility.addProperty(map, COM_DATA_VISIBLE_IN_LEGEND, 
    			this.isVisibleInLegend());
    	if (data.isAnimationAvailable()) {
        	SGIntegerSeriesSet animationArraySection = this.getAnimationArraySection();
        	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_ANIMATION_ARRAY_SECTION, 
        			animationArraySection.toString());
        	SGPropertyUtility.addProperty(map, COM_DATA_ANIMATION_FRAME_RATE,
        			this.getFrameRate());
        	SGPropertyUtility.addProperty(map, COM_DATA_ANIMATION_LOOP_PLAYBACK, 
        			this.isLoopPlaybackAvailable());
    	}
    	SGPropertyUtility.addQuotedStringProperty(map, COM_DATA_NAME, this.getName());
    	SGPropertyUtility.addProperty(map, COM_DATA_COLUMN_TYPE, 
    			data.getColumnTypeCommandString());
    	String originStr = data.getOriginCommandString();
    	if (originStr != null) {
    		SGPropertyUtility.addProperty(map, COM_DATA_ORIGIN, originStr);
    	}
    	if (SGDataUtility.isMDArrayData(data)) {
    		SGMDArrayData mdData = (SGMDArrayData) data;
    			
    		// animation frame dimension
        	String animationStr = mdData.getAnimationFrameDimensionCommandString();
        	if (animationStr != null) {
        		SGPropertyUtility.addProperty(map, COM_DATA_ANIMATION_FRAME_DIMENSION, animationStr);
        	}
    	}
    	data.setArraySectionProperty(map);
    	return map;
    }
    
    /**
     * Returns a text string of the commands.
     * 
     * @return a text string of the commands
     */
    @Override
	public String getCommandString(SGExportParameter params) {
    	if (params instanceof SGDataExportParameter) {
    		SGDataExportParameter dParams = (SGDataExportParameter) params;
    		if (!dParams.canExport(this.getData())) {
    			return "";
    		}
    	}

		StringBuffer sb = new StringBuffer();
		
		// creates the command for this data
		String dataCommands = this.createCommandString(params);
		sb.append(dataCommands);

		return sb.toString();
	}

	/**
	 * Creates and returns a text string of commands.
	 * 
	 * @return a text string of commands
	 */
	@Override
    public String createCommandString(SGExportParameter params) {
    	return SGCommandUtility.createCommandString(COM_DATA, 
				Integer.toString(this.mID), this.getCommandPropertyMap(params));
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
    public void replaceDataSource(final SGIDataSource srcOld, final SGIDataSource srcNew,
    		final SGDataSourceObserver obs) {
    	SGData data = this.getData();
        SGIDataSource src = data.getDataSource();
        if (srcOld.equals(src)) {
        	// clear all edited values in the memento list
        	List mementoList = this.mUndoManager.getMementoList();
        	for (int ii = 0; ii < mementoList.size(); ii++) {
        		ElementGroupSetPropertiesInFigureElement memento = (ElementGroupSetPropertiesInFigureElement) mementoList.get(ii);
        		ArrayDataProperties ap = (ArrayDataProperties) memento.dataProperties;
        		ap.clearEditedDataValueList();
        	}

        	data.setDataSource(srcNew);
        	obs.unregister(data, srcOld);
        	obs.register(data, srcNew);
        }
    }
    
	/**
	 * Prepares for the animation.
	 * 
	 */
    @Override
    public void prepareForChanges() {
    	this.mTempDataProperties = this.getProperties();
    }

    protected abstract boolean checkChanged();

    // Compare current data properties and temporary data properties and notify the change.
	protected void notifyDataProperties(SGProperties temp, String msgStructureChange,
			String msgPropChange) {
        // compare current data properties and temporary data properties
        if (this.compareDataColumns(temp) == false
        		|| this.compareDataSize(temp) == false) {
        	this.notifyToListener(msgStructureChange);
        } else if (this.compareDataProperties(temp) == false) {
        	this.notifyToListener(msgPropChange);
    	}
	}
	
	/**
	 * Returns true if the data has edited values.
	 * 
	 * @return true if the data has edited values
	 */
	@Override
	public boolean hasEditedDataValues() {
		return (this.getData().getEditedValueList().size() > 0);
	}

}
