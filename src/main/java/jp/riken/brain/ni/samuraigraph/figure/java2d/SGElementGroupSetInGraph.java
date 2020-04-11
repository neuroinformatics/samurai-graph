package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.SwingUtilities;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIChildObject;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIData;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementAxis;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIMovable;
import jp.riken.brain.ni.samuraigraph.base.SGINode;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyDialogObserver;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGXYSimpleIndexBlock;
import jp.riken.brain.ni.samuraigraph.data.SGArrayData.ArrayDataProperties;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataViewerDialog;
import jp.riken.brain.ni.samuraigraph.data.SGISXYZTypeData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGTwoDimensionalMDArrayData;
import jp.riken.brain.ni.samuraigraph.figure.SGElementGroup;

import org.w3c.dom.Element;

/**
 * A set of element groups in the graph object.
 * 
 * 
 */
public abstract class SGElementGroupSetInGraph extends SGElementGroupSetForData
		implements SGIPropertyDialogObserver, SGINode, SGIChildObject,
		SGIConstants, SGIMovable {

    /**
     * The graph object.
     */
    protected SGFigureElementGraph mGraph = null;

    /**
     * Flag whether this object is clipped.
     */
    private boolean mClipFlag = true;

    /**
     * Temporary properties of this object.
     */
    protected SGProperties mTemporaryProperties = null;
    
    /**
     * Sets the location of x-axis.
     * 
     * @param location
     *           the location of the x-axis
     */
    public boolean setXAxisLocation(final int location) {
        if (location != SGIFigureElementAxis.AXIS_HORIZONTAL_1
                && location != SGIFigureElementAxis.AXIS_HORIZONTAL_2) {
            return false;
        }
        this.mXAxis = this.mGraph.getAxisElement().getAxisInPlane(location);
        return true;
    }

    /**
     * Sets the location of y-axis.
     * 
     * @param location
     *           the location of the y-axis
     */
    public boolean setYAxisLocation(final int location) {
        if (location != SGIFigureElementAxis.AXIS_VERTICAL_1
                && location != SGIFigureElementAxis.AXIS_VERTICAL_2) {
            return false;
        }
        this.mYAxis = this.mGraph.getAxisElement().getAxisInPlane(location);
        return true;
    }

    /**
     * Disposes this object.
     * 
     */
    public void dispose() {
        super.dispose();
        this.mTemporaryProperties = null;
        this.mGraph = null;
    }

    /**
     * 
     * @param b
     */
    protected void setClipFlag(final boolean b) {
        this.mClipFlag = b;
    }

    /**
     * 
     * @return
     */
    protected boolean getClipFlag() {
        return this.mClipFlag;
    }

    /**
     * Set the flag as a focused object.
     * 
     * @param b
     *            focused
     */
    @Override
    public void setSelected(final boolean b) {
        super.setSelected(b);
        for (int ii = 0; ii < this.mDrawingElementGroupList.size(); ii++) {
            SGIElementGroupInGraph group = (SGIElementGroupInGraph) this.mDrawingElementGroupList.get(ii);
            group.setFocused(b);
        }
        this.mTemporaryProperties = b ? this.getProperties() : null;
    }

    /**
     * 
     */
    public SGElementGroupSetInGraph(SGData data, SGFigureElementGraph graph) {
        super(data);
        this.mGraph = graph;
    }

    /**
     * 
     */
    public void actionPerformed(final ActionEvent e) {
        final String command = e.getActionCommand();
        if (command.equals(SGIConstants.MENUCMD_PROPERTY)) {
            this.mGraph.setPropertiesOfSelectedObjects(this);
//        } else if (command.equals(SGIConstants.MENUCMD_ANIMATION)) {
//            this.mTempDataProperties = this.mData.getProperties();
//            this.mGraph.notifyToListener(command, e.getSource());
//            this.mGraph.doAnimation(this);
        } else {
            this.mGraph.notifyToListener(command, e.getSource());
        }
    }
    
    /**
     * Called when the mouse button is clicked.
     * 
     * @param e
     *          the mouse event
     * @return true if this group set is clicked or false otherwise
     */
    protected boolean onMouseClicked(final MouseEvent e) {
    	SGElementGroup group = this.clickGroup(e);
        if (group != null) {
            return true;
        }
        return false;
    }

    protected SGElementGroup clickGroup(final MouseEvent e) {
        final int x = e.getX();
        final int y = e.getY();
        final int cnt = e.getClickCount();
    	SGElementGroup group = this.getElementGroupAt(x, y);
        if (group != null) {
            // update the data selection
            this.mGraph.updateFocusedObjectsList(this, e);

            // notify that the data selection is updated
            this.mGraph.notifyDataSelection();
            
            if (SwingUtilities.isLeftMouseButton(e) && cnt == 2) {
                // show the property dialog
                this.mGraph.setPropertiesOfSelectedObjects(this);
            } else if (SwingUtilities.isRightMouseButton(e) && cnt == 1) {
            	// show the pop-up menu
            	this.mGraph.showDataPopupMenu(this, x, y, true, this.mGraph.getWindow());
            }
            
            if (this.isSelected()) {
                if (SwingUtilities.isLeftMouseButton(e) && cnt == 1) {
                	this.mGraph.clearTableCellListMap();
                	List<SGTwoDimensionalArrayIndex> tableIndexList = this.getDataViewerIndex(x, y);
                	if (tableIndexList != null) {
                    	this.mGraph.putDataTableCellList(this.mData, tableIndexList);
                    	
                    	// notify clicked cells
                    	this.notifyToListener(SGIFigureElement.NOTIFY_DATA_CLICKED);
                	}
                }
            }
        }
        
        return group;
    }

    /**
     * Called when the mouse button is pressed.
     * 
     * @param e
     *          the mouse event
     * @return true if this group set is pressed or false otherwise
     */
    protected boolean onMousePressed(final MouseEvent e) {
        return this.contains(e.getX(), e.getY());
    }

    /**
     * Records the pressed point.
     * 
     * @param x
     *          the x-coordinate
     * @param y
     *          the y-coordinate
     */
    protected abstract void recordPresentPoint(final int x, final int y);

    /**
     * Called when the mouse button is released.
     * 
     * @param e
     *          the mouse event
     * @return true if succeeded
     */
    protected boolean onMouseReleased(final MouseEvent e) {
    	// do nothing
    	return true;
    }

    /**
     * Update drawing elements with related data object.
     * @return
     *         true if succeeded
     */
    public boolean updateWithData() {
        if (this.mGraph.updateAllDrawingElementsLocation() == false) {
            return false;
        }
        return true;
    }

    @Override
    public boolean commit() {
        
        // compare current data properties and temporary data properties and notify the change
    	this.notifyDataProperties(this.mTemporaryProperties, 
    			SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_COMMIT, 
    			SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_COMMIT);
        
        // compare properties
        SGProperties pTemp = this.mTemporaryProperties;
        SGProperties pPresent = this.getProperties();
        if (pTemp.equals(pPresent) == false) {
            this.setChanged(true);
        }
        
        // clear temporary properties
        this.mTemporaryProperties = null;

        // update drawing elements
        if (this.updateWithData() == false) {
            return false;
        }
        this.mGraph.notifyChangeOnCommit();
        this.mGraph.repaint();

        return true;
    }

    @Override
    public boolean cancel() {
        
        // compare current data properties and temporary data properties and notify the change
    	this.notifyDataProperties(this.mTemporaryProperties, 
    			SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_CANCEL, 
    			SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_CANCEL);

        // restore properties
        if (this.setProperties(this.mTemporaryProperties) == false) {
            return false;
        }
        
        // clear temporary properties
        this.mTemporaryProperties = null;
        
        // restore data properties
        SGData data = this.getData();
        ElementGroupSetPropertiesInFigureElement eg = (ElementGroupSetPropertiesInFigureElement) this
                .getProperties();
        data.setProperties(eg.dataProperties);

        // update drawing elements
        if (this.updateWithData() == false) {
            return false;
        }
        this.mGraph.notifyChangeOnCancel();
        this.mGraph.repaint();
        
        return true;
    }

    @Override
    public boolean preview() {

        // compare current data properties and temporary data properties and notify the change
    	this.notifyDataProperties(this.mTemporaryProperties, 
    			SGIFigureElement.NOTIFY_DATA_STRUCTURE_CHANGE_ON_PREVIEW, 
    			SGIFigureElement.NOTIFY_DATA_PROPERTIES_CHANGE_ON_PREVIEW);

        // update drawing elements
        if (this.updateWithData() == false) {
            return false;
        }
        this.mGraph.notifyChange();
        this.mGraph.repaint();
        
        return true;
    }
    
    @Override
    protected void notifyToListener(final String msg) {
    	this.mGraph.notifyToListener(msg);
    }
    
    @Override
    protected void notifyToListener(final String msg, final Object source) {
        this.mGraph.notifyToListener(msg, source);
    }

    /**
     * Returns a list of child nodes.
     * 
     * @return a list of chid nodes
     */
    public ArrayList getChildNodes() {
        return new ArrayList();
    }

    /**
     * 
     */
    public boolean getProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.getProperties(p) == false) {
            return false;
        }
        ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
        SGIFigureElementAxis aElement = this.mGraph.getAxisElement();
        ep.xAxis = aElement.getLocationInPlane(this.getXAxis());
        ep.yAxis = aElement.getLocationInPlane(this.getYAxis());
        return true;
    }

    /**
     * 
     */
    public boolean setProperties(final SGProperties p) {
        if ((p instanceof ElementGroupSetPropertiesInFigureElement) == false) {
            return false;
        }
        if (super.setProperties(p) == false) {
            return false;
        }
        ElementGroupSetPropertiesInFigureElement ep = (ElementGroupSetPropertiesInFigureElement) p;
        SGIFigureElementAxis aElement = this.mGraph.getAxisElement();
        this.setXAxis(aElement.getAxisInPlane(ep.xAxis));
        this.setYAxis(aElement.getAxisInPlane(ep.yAxis));
        return true;
    }

    /**
     * Returns a text string that represents the description of this instance.
     * 
     * @return a text string that represents the description of this instance
     */
    public String getInstanceDescription() {
        String dataType = this.getDataType();
		String dataTypeLong = SGDataTypeConstants
				.getLongDataTypeConstant(dataType);
		StringBuffer sb = new StringBuffer();
		sb.append(this.getID());
		sb.append(": ");
		sb.append(dataTypeLong);
		sb.append(": ");
		sb.append(SGUtility.removeEscapeChar(this.mName));
		return sb.toString();
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
        return true;
    }

    /**
     * 
     * @return
     */
    public String getTagName() {
        return SGIFigureElementGraph.TAG_NAME_DATA;
    }

    /**
     * Update the location of drawing elements.
     */
    protected abstract boolean updateDrawingElementsLocation(final SGIData data);

    /**
     * Undo operation.
     */
    public boolean setMementoBackward() {
        if (super.setMementoBackward() == false) {
            return false;
        }
        this.mGraph.updateAllDrawingElementsLocation();
        this.mGraph.notifyChangeOnUndo();
        return true;
    }

    /**
     * Redo operation.
     */
    public boolean setMementoForward() {
        if (super.setMementoForward() == false) {
            return false;
        }
        this.mGraph.updateAllDrawingElementsLocation();
        this.mGraph.notifyChangeOnUndo();
        return true;
    }

    /**
     * 
     */
    public void notifyToRoot() {
        this.mGraph.notifyToRoot();
    }

    /**
     * Returns a property dialog.
     * 
     * @return property dialog
     */
    public SGPropertyDialog getPropertyDialog() {
        SGData data = this.mGraph.getData(this);
        SGPropertyDialog dg = this.mGraph.getDataDialog(data);
        return dg;
    }

    /**
     * Sets the current frame index.
     * 
     * @param index the frame index to be set
     */
    @Override
    public void setCurrentFrameIndex(final int index) {
        super.setCurrentFrameIndex(index);
        this.setCurrentFrameIndexSub(index);
    }
    
    protected void setCurrentFrameIndexSub(final int index) {
        this.updateDrawingElementsLocation(this.mData);
    }
    
    protected void onSaveChanges() {
        // notify the change
        this.setChanged(true);
        this.mGraph.notifyChangeOnCommit();
        
        // update drawing elements
        this.updateWithData();
        
        this.mGraph.repaint();
    }

    /**
     * Cancels all changes of this data source.
     *
     */
    public void cancelChanges() {
        super.cancelChanges();
        this.mGraph.repaint();
    }

    /**
     * Translates the data object.
     * 
     * @param x
     *           the x-coordinate that this object is translated to
     * @param y
     *           the y-coordinate that this object is translated to
     */
    protected abstract void translateTo(final int x, final int y);
    
    /**
     * Returns true if this group set is under the grid mode.
     * 
     * @return true if this group set is under the grid mode
     */
    public boolean isGridMode() {
    	return this.isGridMode(this.getData());
    }

    /**
     * Returns true if a given data is under the grid mode.
     * 
     * @param data
     *           the data
     * @return true if a given data is under the grid mode
     */
    public boolean isGridMode(SGData data) {
    	if (SGDataUtility.isNetCDFData(data)) {
    		SGNetCDFData nData = (SGNetCDFData) data;
    		return !nData.isIndexAvailable();
    	} else if (data instanceof SGTwoDimensionalMDArrayData) {
    		SGTwoDimensionalMDArrayData mdData = (SGTwoDimensionalMDArrayData) data;
    		return !mdData.isIndexAvailable();
    	} else {
    		return false;
    	}
    }

	public boolean getAxisDateMode(final int location) {
		return this.mGraph.getAxisElement().getAxisInPlane(location).getDateMode();
	}

    protected boolean onMouseMoved(MouseEvent e) {
    	String tooltipText = this.getToolTipText(e);
		SGUtilityForFigureElementJava2D.showGraphToolTip(this.mGraph, tooltipText);
    	return (tooltipText != null);
    }

    private String getToolTipText(MouseEvent e) {
    	final int x = e.getX();
    	final int y = e.getY();
    	String str = this.getToolTipText(x, y);
    	if (str == null || "".equals(str)) {
    		return null;
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append("<html>");
    	sb.append(str);
    	sb.append("</html>");
    	return sb.toString();
    }

    protected abstract String getToolTipText(final int x, final int y);

    public void setFocusedValueIndices(final SGDataViewerDialog dg) {
    	// initialize the map
		this.initFocusedValueList(dg);

		// get selected cells
		List<SGXYSimpleIndexBlock> blockList;
		if (dg.isHilighting()) {
			blockList = dg.getSelectedCells();
		} else {
			blockList = new ArrayList<SGXYSimpleIndexBlock>();
		}
		
		this.setFocusedValueIndices(dg, blockList);
    }

    protected abstract void setFocusedValueIndices(final SGDataViewerDialog dg,
    		List<SGXYSimpleIndexBlock> blockList);

    public void removeFocusedValueList(final SGDataViewerDialog dg) {
    	this.mFocusedValueListMap.remove(dg);
    }

    public void initFocusedValueList(final SGDataViewerDialog dg) {
    	this.mFocusedValueListMap.put(dg, new ArrayList<SGTuple2d>());
    }

    public void clearFocusedValueListMap() {
    	this.mFocusedValueListMap.clear();
    }

    protected final Map<SGDataViewerDialog, List<SGTuple2d>> mFocusedValueListMap = new HashMap<SGDataViewerDialog, List<SGTuple2d>>();

    Map<SGDataViewerDialog, List<SGTuple2d>> getFocusedValueListMap() {
    	return this.mFocusedValueListMap;
    }
    
    protected void paintFocusedShapes(Graphics2D g2d) {
    	final float size = 20;
    	final float halfSize = size / 2;
        g2d.setColor(new Color(255, 0, 0, 100));
        Iterator<Entry<SGDataViewerDialog, List<SGTuple2d>>> itr = this.mFocusedValueListMap.entrySet().iterator();
        while (itr.hasNext()) {
        	Entry<SGDataViewerDialog, List<SGTuple2d>> entry = itr.next();
        	List<SGTuple2d> valueList = entry.getValue();
        	for (SGTuple2d value : valueList) {
        		final float xLocation = this.mGraph.calcLocation(value.x, mXAxis, true) - halfSize;
        		final float yLocation = this.mGraph.calcLocation(value.y, mYAxis, false) - halfSize;
        		Rectangle2D rect = new Rectangle2D.Float(xLocation, yLocation, size, size);
        		g2d.fill(rect);
        	}
        }
    }
    
    @Override
    protected boolean checkChanged() {
    	if (this.mTemporaryProperties == null) {
    		return false;
    	}
    	return !this.mTemporaryProperties.equals(this.getProperties());
    }
    
    public abstract String getAxisValueString(final int x, final int y);
    
	protected SGTuple2f[] getXYPoints(final double[] xValueArray, final double[] yValueArray) {
        SGISXYZTypeData dataSXYZ = (SGISXYZTypeData) this.mData;
    	final int num = dataSXYZ.getPointsNumber();
    	SGTuple2d[] valueArray = new SGTuple2d[num];
    	for (int ii = 0; ii < num; ii++) {
    		valueArray[ii] = new SGTuple2d(xValueArray[ii], yValueArray[ii]);
    	}
    	SGTuple2f[] pointArray = new SGTuple2f[num];
    	for (int ii = 0; ii < num; ii++) {
    		pointArray[ii] = new SGTuple2f();
    	}
    	this.mGraph.calcLocationOfPoints(valueArray, this.mXAxis, this.mYAxis, pointArray);
    	return pointArray;
	}

	public void dataEdited(SGDataViewerDialog dg) {
		// update the graph
		this.updateWithData();
		
		// update focused values
		this.setFocusedValueIndices(dg);

		// get previous data properties
		List mementoList = this.mUndoManager.getMementoList();
		ElementGroupSetPropertiesInFigureElement prev = (ElementGroupSetPropertiesInFigureElement) mementoList.get(this.mUndoManager.getMementoIndex());
		ArrayDataProperties dpPrev = (ArrayDataProperties) prev.dataProperties;
		List<SGDataValueHistory> prevDataValueList = dpPrev.getEditedDataValueList();
		ElementGroupSetPropertiesInFigureElement cur = (ElementGroupSetPropertiesInFigureElement) this.getProperties();
		ArrayDataProperties dpCur = (ArrayDataProperties) cur.dataProperties;
		List<SGDataValueHistory> curDataValueList = dpCur.getEditedDataValueList();
		if (!SGUtility.equals(prevDataValueList, curDataValueList)) {
			// update previous data values
			for (SGDataValueHistory curDataValue : curDataValueList) {
				if (!prevDataValueList.contains(curDataValue)) {
					dpPrev.addEditedDataValue(curDataValue.getPreviousValue());
				}
			}

			// notify the change
			this.setChanged(true);
			this.notifyToRoot();
		}
	}
	
    /**
     * Sets the properties of data.
     * @param dp
     *           properties of data
     * @return true if succeeded
     */
    @Override
    public boolean setDataProperties(final SGProperties dp) {
        if (super.setDataProperties(dp) == false) {
        	return false;
        }
        
        // update data values
        ArrayDataProperties adp = (ArrayDataProperties) dp;
        List<SGDataValueHistory> editedValueList = adp.getEditedDataValueList();
        for (SGDataValueHistory dataValue : editedValueList) {
        	this.mData.setDataValue(dataValue);
        }
        
        return true;
    }

    protected abstract List<SGTwoDimensionalArrayIndex> getDataViewerIndex(final int x, final int y);

	// Create a text string for tooltip.
    protected String createToolTipText(final String spatiallyVariedText,
    		final String notSpatiallyVariedText, final String dataValueText) {
		StringBuffer sb = new StringBuffer();
		boolean exists = false;
		if (SGDataUtility.isValidTooltipTextString(dataValueText)) {
			if (exists) {
				sb.append(",<br>");
			}
			sb.append(dataValueText);
			exists = true;
		}
		if (SGDataUtility.isValidTooltipTextString(spatiallyVariedText)) {
			if (exists) {
				sb.append(",<br>");
			}
            sb.append(spatiallyVariedText);
            exists = true;
        }
		if (SGDataUtility.isValidTooltipTextString(notSpatiallyVariedText)) {
			if (exists) {
				sb.append(",<br>");
			}
			sb.append(notSpatiallyVariedText);
			exists = true;
		}
		return sb.toString();
    }
    
    protected List<SGTwoDimensionalArrayIndex> mDataTableCellList = new ArrayList<SGTwoDimensionalArrayIndex>();
    
    public List<SGTwoDimensionalArrayIndex> getDataTableCellList() {
    	return new ArrayList<SGTwoDimensionalArrayIndex>(this.mDataTableCellList);
    }
    
    public void addDataTableCellList(List<SGTwoDimensionalArrayIndex> cellList) {
    	this.mDataTableCellList.addAll(cellList);
    }
    
    public void clearDataTableCellList() {
    	this.mDataTableCellList.clear();
    }
}
