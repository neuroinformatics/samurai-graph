package jp.riken.brain.ni.samuraigraph.application;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.riken.brain.ni.samuraigraph.application.SGIApplicationConstants.FILE_TYPE;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.TransformedData;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGDataUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSamplingDataColumn;

class SGMainFunctionsTransform {
    
    private final SGMainFunctions mMain;
    
    /**
     * A wizard dialog to select the data type.
     */
    private final SGDataTypeWizardDialog mDataTypeWizardDialog;

    /**
     * A wizard dialog to select data columns.
     */
    private final SGSDArrayDataSetupWizardDialog mSDArrayDataSetupWizardDialog;

    /**
     * A wizard dialog to setup the netCDF data.
     */
    private final SGNetCDFDataSetupWizardDialog mNetCDFDataSetupWizardDialog;

    /**
     * A wizard dialog to setup the HDF5 data.
     */
    private final SGMDArrayDataSetupWizardDialog mMDArrayDataSetupWizardDialog;

    /**
     * A wizard dialog to select the plot type if the data type is scalar-xy.
     */
    private final SGPlotTypeSelectionWizardDialog mPlotTypeSelectionWizardDialog;
    
    private final TransformedData mTransformedData;
    
    SGMainFunctionsTransform(final SGMainFunctions mainFunctions,
            final SGDataTypeWizardDialog dataTypeWizardDialog,
            final SGSDArrayDataSetupWizardDialog dataColumnSelectionWizardDialog,
            final SGNetCDFDataSetupWizardDialog netCDFSetupWizardDialog,
            final SGMDArrayDataSetupWizardDialog mdArrayDataSetupWizardDialog,
            final SGPlotTypeSelectionWizardDialog plotTypeSelectionWizardDialog,
            final TransformedData transformedData) {
        
    	super();
    	
        this.mMain = mainFunctions;
        this.mDataTypeWizardDialog = dataTypeWizardDialog;
        this.mSDArrayDataSetupWizardDialog = dataColumnSelectionWizardDialog;
        this.mNetCDFDataSetupWizardDialog = netCDFSetupWizardDialog;
        this.mMDArrayDataSetupWizardDialog = mdArrayDataSetupWizardDialog;
        this.mPlotTypeSelectionWizardDialog = plotTypeSelectionWizardDialog;
        
        this.mTransformedData = transformedData;
    }

    /**
     *  
     * @param e
     * @return
     */
    boolean addDataByDataTransformation(final ActionEvent e) {
        Object source = e.getSource();
        SGWizardDialog dg = (SGWizardDialog) source;
        String command = e.getActionCommand();

        // cancel or previous
        if (command.equals(SGDialog.CANCEL_BUTTON_TEXT)) {
            dg.setVisible(false);
            this.mMain.clearTemporaryData();
        } else if (command.equals(SGDialog.PREVIOUS_BUTTON_TEXT)) {
            dg.showPrevious();
        } else if (command.equals(SGDialog.NEXT_BUTTON_TEXT)) {

            if (dg instanceof SGDataTypeWizardDialog) {
                // current is column selection dialog.
            	SGDataTypeWizardDialog prev = (SGDataTypeWizardDialog) dg;

                // set invisible the dialog
                prev.setVisible(false);

                // get selected file type
                String dataType = prev.getSelectedDataType();
                if (dataType == null) {
                    SGDrawingWindow wnd = prev.getOwnerWindow();
                    SGUtility.showErrorMessageDialog(wnd,
                            SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE,
                            SGIConstants.TITLE_ERROR);
                    return false;
                }

                // creates a map of information
                Map<String, Object> infoMap = new HashMap<String, Object>();
                infoMap.putAll(this.mTransformedData.data.getInfoMap());
                
                // removes unused keys from the information map
                infoMap.remove(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
                infoMap.remove(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);

                // adds data name
                String dataName = this.mTransformedData.name;
                infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

                // overwrites the properties gotten from the previous dialog
                infoMap.putAll(SGMainFunctions.createInfoMap(dataType, prev, 
                		this.mTransformedData.figureId, null));
                
                FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
                if (dataFileType == FILE_TYPE.TXT_DATA) {
                    this.mMain.setupPlotTypeSelectionWizardDialogConnection(
                            FILE_TYPE.TXT_DATA, dataType);
                    SGSDArrayData aData = (SGSDArrayData) this.mTransformedData.data;
                    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, aData.getDataSource());
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mSDArrayDataSetupWizardDialog,
                            (SGSDArrayData) this.mTransformedData.data, dataType,
                            infoMap) == false) {
                        return false;
                    }
                } else if (dataFileType == FILE_TYPE.NETCDF_DATA) {
                    this.mMain.setupPlotTypeSelectionWizardDialogConnection(
                            FILE_TYPE.NETCDF_DATA, dataType);
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mNetCDFDataSetupWizardDialog,
                            (SGNetCDFData) this.mTransformedData.data, dataType,
                            infoMap) == false) {
                        return false;
                    }
                } else if (dataFileType == FILE_TYPE.HDF5_DATA
                		|| dataFileType == FILE_TYPE.MATLAB_DATA
                		|| dataFileType == FILE_TYPE.VIRTUAL_DATA) {
                    this.mMain.setupPlotTypeSelectionWizardDialogConnection(
                            FILE_TYPE.HDF5_DATA, dataType);
                    if (this.makeTransition(
                            this.mDataTypeWizardDialog,
                            this.mMDArrayDataSetupWizardDialog,
                            (SGMDArrayData) this.mTransformedData.data, dataType,
                            infoMap) == false) {
                        return false;
                    }
                }
                
            } else if (dg.equals(this.mSDArrayDataSetupWizardDialog)
            		|| dg.equals(this.mNetCDFDataSetupWizardDialog)
            		|| dg.equals(this.mMDArrayDataSetupWizardDialog)) {
                dg.showNext();
            }
            
        } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
            try {
                if (this.addDataByTransformation(dg) == false) {
                    return false;
                }
            } finally {
                this.mMain.clearTemporaryData();
            }
        }

        return true;
    }

    /**
     * Makes the transition between wizard dialogs.
     * 
     * @param prev
     *            previous dialog to select data type
     * @param next
     *            next dialog to select column
     * @param data
     *            an array data
     * @param dataType
     *            the type of data
     * @param infoMap
     *            the information map
     * @return true if succeeded
     */
    private boolean makeTransition(
    		SGDataTypeWizardDialog prev,
            SGSDArrayDataSetupWizardDialog next, SGSDArrayData data,
            String dataType, Map<String, Object> infoMap) {

        // creates column information set
        SGDataColumnInfoSet colInfoSet = this.createColumnInfoList(data.getDataFile(), 
                        dataType, infoMap);
        if (colInfoSet == null) {
            return false;
        }
        
        // set information to the dialog for data column selection
        SGSDArrayFile sdFile = (SGSDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
        if (next.setData(sdFile, dataType, colInfoSet, infoMap, false) == false) {
            return false;
        }

        // show the dialog to select data columns
        next.setCenter(prev.getOwner());
        next.setVisible(true);

        return true;
    }

    /**
     * Makes the transition between wizard dialogs.
     * 
     * @param prev
     *            previous dialog to select data type
     * @param next
     *            next dialog to select column
     * @param data
     *            an array data
     * @param dataType
     *            the type of data
     * @param infoMap
     *            the information map
     * @return true if succeeded
     */
    private boolean makeTransition(
    		SGDataTypeWizardDialog prev,
            SGNetCDFDataSetupWizardDialog next, SGNetCDFData data,
            String dataType, Map<String, Object> infoMap) {

        // creates column information set
        SGDataColumnInfoSet colInfoSet = this.createColumnInfoList(data, 
                        dataType, infoMap);
        if (colInfoSet == null) {
            return false;
        }
                
        // set information to the dialog for data column selection
        SGNetCDFFile ncfile = (SGNetCDFFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
        if (next.setData(ncfile, dataType, colInfoSet, infoMap, false) == false) {
            return false;
        }
        next.pack();

        // show the dialog to select data columns
        next.setCenter(prev.getOwner());
        next.setVisible(true);

        return true;
    }

    private boolean makeTransition(
    		SGDataTypeWizardDialog prev,
            SGMDArrayDataSetupWizardDialog next, SGMDArrayData data,
            String dataType, Map<String, Object> infoMap) {

        // creates column information set
        SGDataColumnInfoSet colInfoSet = this.createColumnInfoList(data, 
                        dataType, infoMap);
        if (colInfoSet == null) {
            return false;
        }
                
        // set information to the dialog for data column selection
        SGMDArrayFile mdFile = (SGMDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
        if (next.setData(mdFile, dataType, colInfoSet, infoMap, false) == false) {
            return false;
        }
        next.pack();

        // show the dialog to select data columns
        next.setCenter(prev.getOwner());
        next.setVisible(true);

        return true;
    }

    private SGDataColumnInfo[] removeAdditionalColumnInfo(SGDataColumnInfo[] colInfo) {
        List<SGDataColumnInfo> newColInfo = new ArrayList<SGDataColumnInfo>();
        for (int i = 0; i < colInfo.length; i++) {
            if (SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE.equals(colInfo[i].getValueType())==false) {
                newColInfo.add(colInfo[i]);
            }
        }
        return newColInfo.toArray(new SGDataColumnInfo[newColInfo.size()]);
    }
    
    private SGDataColumnInfoSet createColumnInfoList(SGSDArrayFile f,
                String dataType, Map<String, Object> infoMap) {
        
        SGDataColumn[] columns = f.getDataColumns();
        
        // get column information: title and value type
        SGDataColumnInfo[] colArray = new SGDataColumnInfo[columns.length];
        for (int ii = 0; ii < colArray.length; ii++) {
            colArray[ii] =
                new SGSDArrayDataColumnInfo(columns[ii].getTitle(), 
                		columns[ii].getValueType(), columns[ii].getLength());
        }
        colArray = removeAdditionalColumnInfo(colArray);
        return this.mMain.createColumnInfoSet(dataType, infoMap, colArray);
    }

    private SGDataColumnInfoSet createColumnInfoList(SGNetCDFData data,
                String dataType, Map<String, Object> infoMap) {
        
        SGNetCDFFile ncfile = data.getNetcdfFile();
        List<SGNetCDFVariable> varList = ncfile.getVariables();
        
        // get column information: title and value type
        SGNetCDFDataColumnInfo[] colArray = new SGNetCDFDataColumnInfo[varList.size()];
        for (int ii = 0; ii < colArray.length; ii++) {
            SGNetCDFVariable var = varList.get(ii);
            int origin = 0;
            if (var.isCoordinateVariable()) {
                origin = data.getOrigin(var.getName());
            }
            colArray[ii] = new SGNetCDFDataColumnInfo(var, var.getName(), var.getValueType(), origin);
        }
        return this.mMain.createColumnInfoSet(dataType, infoMap, colArray);
    }

    private SGDataColumnInfoSet createColumnInfoList(SGMDArrayData data,
            String dataType, Map<String, Object> infoMap) {
    
	    SGMDArrayFile mdFile = data.getMDArrayFile();
	    SGMDArrayVariable[] vars = mdFile.getVariables();
	    
	    // get column information: title and value type
	    SGMDArrayDataColumnInfo[] colArray = new SGMDArrayDataColumnInfo[vars.length];
	    for (int ii = 0; ii < colArray.length; ii++) {
	        SGMDArrayVariable var = vars[ii];
	        colArray[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType(), 
	        		var.getOrigins());
	    }
	    return this.mMain.createColumnInfoSet(dataType, infoMap, colArray);
	}

    /**
     * Adds data by transforming an existing data.
     * 
     * @param dg
     *            an event source
     * @return true if succeeded
     */
    private boolean addDataByTransformation(SGWizardDialog dg) {
        SGDrawingWindow wnd = dg.getOwnerWindow();
        final int figureID = this.mTransformedData.figureId;
        SGFigure fig = wnd.getFigure(figureID);
        SGData data = this.mTransformedData.data;
        FILE_TYPE dataFileType = this.mDataTypeWizardDialog.getDataFileType();
        if (dg.equals(this.mSDArrayDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.TXT_DATA) {
            // single dimensional array data
            if (addSDArrayDataByTransformation(wnd, figureID, fig, data, dg) == false) {
                return false;
            }
        } else if (dg.equals(this.mNetCDFDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.NETCDF_DATA) {
            // netCDF data
            if (addNetCDFDataByTransformation(wnd, figureID, fig, data, dg) == false) {
                return false;
            }
        } else if (dg.equals(this.mMDArrayDataSetupWizardDialog)
        		|| dataFileType == FILE_TYPE.HDF5_DATA
        		|| dataFileType == FILE_TYPE.MATLAB_DATA) {
            // HDF5 data
            if (addMDArrayDataByTransformation(wnd, figureID, fig, data, dg,
            		this.mDataTypeWizardDialog, this.mMDArrayDataSetupWizardDialog) == false) {
                return false;
            }
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            if (dg.getPrevious().equals(this.mSDArrayDataSetupWizardDialog)) {
                if (addSDArrayDataByTransformation(wnd, figureID, fig, data, dg) == false) {
                    return false;
                }
            } else if (dg.getPrevious().equals(this.mNetCDFDataSetupWizardDialog)) {
                if (addNetCDFDataByTransformation(wnd, figureID, fig, data, dg) == false) {
                    return false;
                }
            } else if (dg.getPrevious().equals(this.mMDArrayDataSetupWizardDialog)) {
                if (addMDArrayDataByTransformation(wnd, figureID, fig, data, dg,
                		this.mDataTypeWizardDialog, this.mMDArrayDataSetupWizardDialog) == false) {
                    return false;
                }
            } else {
                throw new InternalError("dg.getPrevious() equals null.");
            }
        } else {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_DRAW_GRAPH,
                    SGIConstants.TITLE_ERROR);
                return false;
        }
        
        wnd.notifyToRoot();

        return true;
    }
    
    /**
     * Adds data by transforming an existing text data.
     * 
     */
    private boolean addSDArrayDataByTransformation(
            final SGDrawingWindow wnd, final int figureID, final SGFigure fig,
            SGData data, SGWizardDialog dg) {
        
        final SGDataCreator.CreatedDataSet cdSet;
        Map<String, Object> infoMap = null;
        
        SGSDArrayData aData = (SGSDArrayData) data;
        SGSDArrayFile file = aData.getDataFile();

        // get data type
        String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        // get information
        infoMap = SGMainFunctions.createInfoMap(dataType, this.mDataTypeWizardDialog,
        		figureID, null);
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, data.getDataSource());

        // get selected column types
        SGDataColumnInfoSet colInfoSet = null;
        String dataName = this.mTransformedData.name;
        if (dg.equals(this.mDataTypeWizardDialog)) {
            colInfoSet = this.createColumnInfoList(file, dataType, infoMap);
            dataName = this.mTransformedData.name;
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            
            // calculate the stride
            SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
            Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcSDArrayDefaultStride(
            		colArray, infoMap);
            infoMap.putAll(strideMap);
        } else if (dg.equals(this.mSDArrayDataSetupWizardDialog)) {
            colInfoSet = this.mSDArrayDataSetupWizardDialog.getDataColumnInfoSet();
            dataName = this.mSDArrayDataSetupWizardDialog.getDataName();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            colInfoSet = this.mSDArrayDataSetupWizardDialog.getDataColumnInfoSet();
            dataName = this.mSDArrayDataSetupWizardDialog.getDataName();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            SGMainFunctions.addPlotTypeSelectionValuesToInfoMap(infoMap, this.mPlotTypeSelectionWizardDialog);
        }
        if (colInfoSet == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_DRAW_GRAPH,
                    SGIConstants.TITLE_ERROR);
            return false;
        }
        
        final SGDataColumn[] columns = file.getDataColumns();
        final int dataLength = columns[0].getLength();
        SGDataColumnInfo[] colInfo = colInfoSet.getDataColumnInfoArray();
        if (colInfo.length!=columns.length) {
            SGDataColumn[] newColumns = new SGDataColumn[colInfo.length];
            for (int i = 0; i < colInfo.length; i++) {
                String valueType = colInfo[i].getValueType();
                if (!SGIDataColumnTypeConstants.VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
                    newColumns[i] = columns[i];
                } else {
                    Object obj = infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE);
                    if (null!=obj && (obj instanceof Number)) {
                        double samplingRate = ((Number)obj).doubleValue();
                        newColumns[i] = new SGSamplingDataColumn(samplingRate, dataLength);
                    } else {
                        return false;
                    }
                }
            }
            file.setDataColumns(newColumns);
        }
        
        // creates a data object
        cdSet = this.mMain.mDataCreator.create(file, colInfoSet, infoMap, wnd);
        if (cdSet == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_DRAW_GRAPH,
                    SGIConstants.TITLE_ERROR);
            return false;
        }
        
        // hide the original data
        if (wnd.hideSelectedObjects() == false) {
            return false;
        }

        return this.mMain.addDataToDrawGraph(
                wnd, figureID, null, true, infoMap,
                null, fig, cdSet, dataName, true);
    }
    
    /**
     * Adds data by transforming an existing netCDF data.
     * 
     */
    private boolean addNetCDFDataByTransformation(
            final SGDrawingWindow wnd, final int figureID, final SGFigure fig,
            SGData data, SGWizardDialog dg) {
        
        final SGDataCreator.CreatedDataSet cdSet;
        Map<String, Object> infoMap = null;
        
        // get data type
        String dataType = this.mDataTypeWizardDialog.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        // create a information map
        infoMap = SGMainFunctions.createInfoMap(dataType, this.mDataTypeWizardDialog,
        		figureID, null);
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, data.getDataSource());

        // get selected column types
        SGDataColumnInfoSet colInfoSet = null;
        String dataName = "";
        if (dg.equals(this.mDataTypeWizardDialog)) {
            SGNetCDFFile ncFile = (SGNetCDFFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
            colInfoSet = this.mMain.getNetCDFDefaultDataColumnInfo(ncFile, dataType, infoMap);
            dataName = this.mTransformedData.name;
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            
            // calculate the stride
            SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
            Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcNetCDFDefaultStride(
            		colArray, infoMap);
            infoMap.putAll(strideMap);

        } else if (dg.equals(this.mNetCDFDataSetupWizardDialog)) {
            colInfoSet = this.mNetCDFDataSetupWizardDialog.getDataColumnInfoSet();
            dataName = this.mNetCDFDataSetupWizardDialog.getDataName();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            infoMap.putAll(this.mNetCDFDataSetupWizardDialog.getStrideMap());
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            colInfoSet = this.mNetCDFDataSetupWizardDialog.getDataColumnInfoSet();
            dataName = this.mNetCDFDataSetupWizardDialog.getDataName();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            infoMap.putAll(this.mNetCDFDataSetupWizardDialog.getStrideMap());
            SGMainFunctions.addPlotTypeSelectionValuesToInfoMap(infoMap, this.mPlotTypeSelectionWizardDialog);
        }

        if (colInfoSet == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.MSG_INVALID_DATA_FILE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        if (dg.equals(this.mDataTypeWizardDialog)) {
            // set default value of dimension origin and step
            if (this.mMain.setupNetCDFDefaultDimensionValues(dataType, infoMap, this.mNetCDFDataSetupWizardDialog) == false) {
                return false;
            }
        } else if (dg.equals(this.mNetCDFDataSetupWizardDialog)) {
            // set dimension origin and step
            if (SGMainFunctions.addDimensionValuesToInfoMap(infoMap, this.mNetCDFDataSetupWizardDialog) == false) {
                return false;
            }
        }

        // creates data
        cdSet = this.mMain.mDataCreator.create(data.getDataSource(), colInfoSet, infoMap, wnd);
        if (cdSet == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_DRAW_GRAPH,
                    SGIConstants.TITLE_ERROR);
            return false;
        }
        
        // hide the original data
        if (wnd.hideSelectedObjects() == false) {
            return false;
        }

        return this.mMain.addDataToDrawGraph(
                wnd, figureID, null, true, infoMap,
                null, fig, cdSet, dataName, true);
    }
    
    /**
     * Adds data by transforming an existing HDF5 data.
     * 
     */
    private boolean addMDArrayDataByTransformation(
            final SGDrawingWindow wnd, final int figureID, final SGFigure fig,
            SGData data, SGWizardDialog dg,
            SGDataTypeWizardDialog dataTypeDialog, 
            SGMDArrayDataSetupWizardDialog dataSetupDialog) {
        
        final SGDataCreator.CreatedDataSet cdSet;
        Map<String, Object> infoMap = null;
        
        // get data type
        String dataType = dataTypeDialog.getSelectedDataType();
        if (dataType == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        // create a information map
        infoMap = SGMainFunctions.createInfoMap(dataType, dataTypeDialog, figureID, null);
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, data.getDataSource());

        // get selected column types
        SGDataColumnInfoSet colInfoSet = null;
        String dataName = "";
        if (dg.equals(dataTypeDialog)) {
            SGMDArrayFile mdFile = (SGMDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
            colInfoSet = this.mMain.getMDArrayDataDefaultDataColumnInfo(mdFile, dataType, infoMap);
            dataName = this.mTransformedData.name;
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            
            // calculate the stride
            SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
            Map<String, SGIntegerSeriesSet> strideMap = SGDataUtility.calcMDArrayDefaultStride(
            		colArray, infoMap);
            infoMap.putAll(strideMap);
        } else if (dg.equals(dataSetupDialog)) {
            colInfoSet = dataSetupDialog.getDataColumnInfoSet();
            dataName = dataSetupDialog.getDataName();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            infoMap.putAll(dataSetupDialog.getStrideMap());
        } else if (dg.equals(this.mPlotTypeSelectionWizardDialog)) {
            colInfoSet = dataSetupDialog.getDataColumnInfoSet();
            dataName = dataSetupDialog.getDataName();
            infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);
            infoMap.putAll(dataSetupDialog.getStrideMap());
            SGMainFunctions.addPlotTypeSelectionValuesToInfoMap(infoMap, this.mPlotTypeSelectionWizardDialog);
        }

        if (colInfoSet == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.MSG_INVALID_DATA_FILE,
                    SGIConstants.TITLE_ERROR);
            return false;
        }

        if (dg.equals(dataTypeDialog)) {
            // set default value of dimension origin and step
            if (this.mMain.setupMDArrayDefaultDimensionValues(dataType, infoMap, dataSetupDialog, true) == false) {
                return false;
            }
        } else if (dg.equals(dataSetupDialog)) {
            // set dimension origin and step
            if (SGMainFunctions.addDimensionValuesToInfoMap(infoMap, dataSetupDialog) == false) {
                return false;
            }
        }

        // creates data
        cdSet = this.mMain.mDataCreator.create(data.getDataSource(), colInfoSet, infoMap, wnd);
        if (cdSet == null) {
            SGUtility.showErrorMessageDialog(wnd,
                    SGMainFunctions.ERRMSG_TO_DRAW_GRAPH,
                    SGIConstants.TITLE_ERROR);
            return false;
        }
        
        // hide the original data
        if (wnd.hideSelectedObjects() == false) {
            return false;
        }

        return this.mMain.addDataToDrawGraph(
                wnd, figureID, null, true, infoMap,
                null, fig, cdSet, dataName, true);
    }

}
