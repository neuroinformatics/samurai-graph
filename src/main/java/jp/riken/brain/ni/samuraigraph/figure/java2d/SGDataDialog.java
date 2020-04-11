package jp.riken.brain.ni.samuraigraph.figure.java2d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyDialog;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.data.SGDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupDialog;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataSetupDialog;

/**
 * A base dialog class to set the properties of data objects.
 * 
 */
public abstract class SGDataDialog extends SGPropertyDialog {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 1237086193467780743L;

    /**
     * A dialog to select data columns.
     */
    protected SGDataSetupDialog mDataColumnSelectionDialog = null;

    /**
     * An array of data column information.
     */
    protected SGDataColumnInfo[] mDataInfoArray = null;

    /**
     * The map of the stride of arrays.
     */
    protected Map<String, SGIntegerSeriesSet> mStrideMap = new HashMap<String, SGIntegerSeriesSet>();

    /**
     * Creates new form SGPropertyDialogSXYData.
     * 
     * @param parent
     *            parent frame
     * @param modal
     *            true for a modal dialog
     */
    public SGDataDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
    }
    
    /**
     * Returns the data name of dialog observers.
     * 
     * @return data name when all names are the same, otherwise null
     */
    protected String getDataNameFromObservers() {

        List oList = this.mPropertyDialogObserverList;
        final int len = oList.size();
        if (len == 0) {
            return null;
        }

        ArrayList nList = new ArrayList(len);
        for (int ii = 0; ii < len; ii++) {
            SGIDataPropertyDialogObserver l = (SGIDataPropertyDialogObserver) oList
                    .get(ii);
            nList.add(l.getName());
        }

        String name0 = (String) nList.get(0);
        String name = name0;
        if (len > 1) {
            for (int ii = 1; ii < len; ii++) {
                String name1 = (String) nList.get(ii);
                if (name0.equals(name1) == false) {
                    name = null;
                    break;
                }
            }
        }

        return name;
    }

    /**
     * Returns visibility in the legend of dialog observers.
     * 
     * @return visibility in the legend when all values are the same, otherwise
     *         null
     */
    protected Boolean getLegendVisibleFromObservers() {

        List oList = this.mPropertyDialogObserverList;
        final int len = oList.size();
        if (len == 0) {
            return null;
        }

        ArrayList lList = new ArrayList(len);
        for (int ii = 0; ii < len; ii++) {
            SGIDataPropertyDialogObserver l = (SGIDataPropertyDialogObserver) oList
                    .get(ii);
            lList.add(Boolean.valueOf(l.getLegendVisibleFlag()));
        }

        Boolean b0 = (Boolean) lList.get(0);
        Boolean b = b0;
        if (len > 1) {
            for (int ii = 1; ii < len; ii++) {
                Boolean b1 = (Boolean) lList.get(ii);
                if (b0.equals(b1) == false) {
                    b = null;
                    break;
                }
            }
        }

        return b;
    }

    /**
     * Shows a dialog to select data columns.
     */
    protected void showDataColumnSelectionDialog() {
        
        // only one data is possible
        SGIDataPropertyDialogObserver obs = (SGIDataPropertyDialogObserver) this.mPropertyDialogObserverList
                .get(0);
        String dataType = obs.getDataType();
        
		if (SGDataUtility.isSDArrayData(dataType)) {
			this.mDataColumnSelectionDialog = new SGSDArrayDataSetupDialog(this, true);
		} else if (SGDataUtility.isNetCDFData(dataType)) {
			this.mDataColumnSelectionDialog = new SGNetCDFDataSetupDialog(this, true);
		} else if (SGDataUtility.isMDArrayData(dataType)) {
			this.mDataColumnSelectionDialog = new SGMDArrayDataSetupDialog(this, true);
		} else {
			throw new Error("Data type is not supported: " + dataType);
		}
		this.mDataColumnSelectionDialog.addActionListener(this);

        // set data to the dialog
        this.setDataToDialog(obs);
        
        // show the dialog
        this.mDataColumnSelectionDialog.setLocation(
                this.getX() + 20, this.getY() + 20);
        this.mDataColumnSelectionDialog.setVisible(true);
        this.mDataColumnSelectionDialog.removeActionListener(this);
    }
    
    private void setDataToDialog(SGIDataPropertyDialogObserver obs) {
        SGDataColumnInfo[] cols = obs.getDataColumnInfoArray();
        Map<String, Object> infoMap = this.createInfoMap(obs);

        // if attributes of data column types array already exist, set them to the column array
        if (this.mDataInfoArray != null) {
            for (int ii = 0; ii < cols.length; ii++) {
                cols[ii] = (SGDataColumnInfo) this.mDataInfoArray[ii].clone();
            }
        }
        
        SGData data = obs.getData();
        SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);
        if (SGDataUtility.isSDArrayData(data)) {
            SGSDArrayDataSetupDialog dg = (SGSDArrayDataSetupDialog) this.mDataColumnSelectionDialog;
            SGSDArrayData sData = (SGSDArrayData) data;
            dg.setData(sData, colInfoSet, infoMap, false);
        } else if (SGDataUtility.isNetCDFData(data)) {
            SGNetCDFDataSetupDialog dg = (SGNetCDFDataSetupDialog) this.mDataColumnSelectionDialog;
            SGNetCDFData nData = (SGNetCDFData) data;
            dg.setData(nData, colInfoSet, infoMap, false);
        } else if (SGDataUtility.isMDArrayData(data)) {
        	SGMDArrayDataSetupDialog dg = (SGMDArrayDataSetupDialog) this.mDataColumnSelectionDialog;
        	SGMDArrayData mdData = (SGMDArrayData) data;
        	dg.setData(mdData, colInfoSet, infoMap, false);
        }
    }
    
    protected Map<String, Object> createInfoMap(SGIDataPropertyDialogObserver obs) {
        SGData data = obs.getData();
        Map<String, Object> infoMap = obs.getInfoMap();
        
    	// updates the information
    	SGFigure dataFigure = null;
        SGDrawingWindow wnd = this.getOwnerWindow();
        List<SGFigure> figureList = wnd.getFigureList();
        for (SGFigure figure : figureList) {
        	List<SGData> dataList = figure.getDataList();
        	for (SGData d : dataList) {
        		if (d.equals(data)) {
        			dataFigure = figure;
        			break;
        		}
        	}
        	if (dataFigure != null) {
        		break;
        	}
        }
        if (dataFigure != null) {
        	// the size of figure
        	SGTuple2f figureSize = new SGTuple2f(dataFigure.getFigureWidth(), 
        			dataFigure.getFigureHeight());
        	infoMap.put(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE, figureSize);
        }

        return infoMap;
    }

    /**
     * Overrode to clear an attribute and to set data properties.
     * @return
     *        true if succeeded
     */
    protected boolean onOK() {
        if (super.onOK() == false) {
            return false;
        }
        this.mDataInfoArray = null;
        return true;
    }
    
    /**
     * Overrode to clear an attribute.
     * @return
     *        true if succeeded
     */
    protected boolean onCanceled() {
        if (super.onCanceled() == false) {
            return false;
        }
        this.mDataInfoArray = null;
        return true;
    }
}
