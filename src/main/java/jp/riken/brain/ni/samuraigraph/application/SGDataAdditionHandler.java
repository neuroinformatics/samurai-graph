package jp.riken.brain.ni.samuraigraph.application;

import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.DataSourceInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDialog;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIPropertyFileConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility;
import jp.riken.brain.ni.samuraigraph.data.SGIDataColumnTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGINetCDFConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataSetupPanel;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGVXYDataBuffer;
import jp.riken.brain.ni.samuraigraph.data.SGVXYGridDataBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/** Dialog flow and graph creation for data addition. */
final class SGDataAdditionHandler
    implements SGIUpgradeConstants,
        SGIApplicationCommandConstants,
        SGIApplicationConstants,
        SGIPropertyFileConstants,
        SGIPreferencesConstants,
        SGIApplicationTextConstants,
        SGIImageConstants,
        SGIArchiveFileConstants,
        SGIDataColumnTypeConstants,
        SGINetCDFConstants {

  private static final Logger logger = LogManager.getLogger(SGDataAdditionHandler.class);

  private final SGMainFunctions mMain;

  SGDataAdditionHandler(SGMainFunctions main) {
    this.mMain = main;
  }

  /**
   * Creates the wizard dialogs for data addition.
   *
   * @param owner the owner of wizard dialogs
   */
  void createDataAdditionWizardDialogs(final SGDrawingWindow owner) {

    // if the owner window is the same, do nothing
    if (this.mMain.mFigureIDSelectionWizardDialog != null) {
      SGDrawingWindow curOwner = this.mMain.mFigureIDSelectionWizardDialog.getOwnerWindow();
      if (curOwner.equals(owner)) {
        return;
      }
    }

    //
    // creates dialogs
    //

    // common to all data types
    this.mMain.mFigureIDSelectionWizardDialog = new SGFigureIDSelectionWizardDialog(owner, true);
    this.mMain.mSingleDataFileChooserWizardDialog =
        new SGSingleDataFileChooserWizardDialog(owner, true);
    this.mMain.mDataTypeWizardDialog = new SGDataTypeWizardDialog(owner, true);
    this.mMain.mPlotTypeSelectionWizardDialog = new SGPlotTypeSelectionWizardDialog(owner, true);

    // for text data
    this.mMain.mSDArrayDataSetupWizardDialog = new SGSDArrayDataSetupWizardDialog(owner, true);
    this.mMain.mSDArrayDataSetupWizardDialog.setPrevious(this.mMain.mDataTypeWizardDialog);

    // for netCDF data
    this.mMain.mNetCDFDataSetupWizardDialog = new SGNetCDFDataSetupWizardDialog(owner, true);
    this.mMain.mNetCDFDataSetupWizardDialog.setPrevious(this.mMain.mDataTypeWizardDialog);

    // for multidimensional array data
    this.mMain.mMDArrayDataSetupWizardDialog = new SGMDArrayDataSetupWizardDialog(owner, true);
    this.mMain.mMDArrayDataSetupWizardDialog.setPrevious(this.mMain.mDataTypeWizardDialog);

    // for HDF5 and NetCDF-4 file
    this.mMain.mFileTypeSelectionWizardDialog = new SGFileTypeSelectionWizardDialog(owner, true);

    // sets the connection between wizard dialogs
    this.mMain.mFigureIDSelectionWizardDialog.setPrevious(null);

    //
    // Remaining connections are selected depending on the method of data addition
    //

    // sets the selected file name
    String path = this.mMain.getCurrentFileDirectory();
    this.mMain.mSingleDataFileChooserWizardDialog.setCurrentFile(path, null);

    // add action listener
    this.mMain.mFigureIDSelectionWizardDialog.addActionListener(this.mMain);
    this.mMain.mSingleDataFileChooserWizardDialog.addActionListener(this.mMain);
    this.mMain.mFileTypeSelectionWizardDialog.addActionListener(this.mMain);
    this.mMain.mDataTypeWizardDialog.addActionListener(this.mMain);
    this.mMain.mSDArrayDataSetupWizardDialog.addActionListener(this.mMain);
    this.mMain.mNetCDFDataSetupWizardDialog.addActionListener(this.mMain);
    this.mMain.mMDArrayDataSetupWizardDialog.addActionListener(this.mMain);
    this.mMain.mPlotTypeSelectionWizardDialog.addActionListener(this.mMain);

    // add window listener
    this.mMain.mFigureIDSelectionWizardDialog.addWindowListener(this.mMain);
    this.mMain.mSingleDataFileChooserWizardDialog.addWindowListener(this.mMain);
    this.mMain.mFileTypeSelectionWizardDialog.addWindowListener(this.mMain);
    this.mMain.mDataTypeWizardDialog.addWindowListener(this.mMain);
    this.mMain.mSDArrayDataSetupWizardDialog.addWindowListener(this.mMain);
    this.mMain.mNetCDFDataSetupWizardDialog.addWindowListener(this.mMain);
    this.mMain.mMDArrayDataSetupWizardDialog.addWindowListener(this.mMain);
    this.mMain.mPlotTypeSelectionWizardDialog.addWindowListener(this.mMain);

    // packs the dialogs
    this.mMain.mFigureIDSelectionWizardDialog.pack();
    this.mMain.mSingleDataFileChooserWizardDialog.pack();
    this.mMain.mFileTypeSelectionWizardDialog.pack();
  }

  void setupDataAdditionWizardDialogConnection(
      final SGDataTypeWizardDialog dataTypeDialog,
      final int method,
      final FILE_TYPE fileType,
      final boolean isFileTypeDialogPrev) {

    // setup the connection
    switch (method) {
      case SGMainFunctions.DATA_ADDITION_TOOL_BAR:
        this.mMain.mFigureIDSelectionWizardDialog.setPrevious(null);

        this.mMain.mFigureIDSelectionWizardDialog.setNext(
            this.mMain.mSingleDataFileChooserWizardDialog);
        this.mMain.mSingleDataFileChooserWizardDialog.setPrevious(
            this.mMain.mFigureIDSelectionWizardDialog);

        if (dataTypeDialog != null) {
          if (isFileTypeDialogPrev) {
            this.mMain.mSingleDataFileChooserWizardDialog.setNext(
                this.mMain.mFileTypeSelectionWizardDialog);
            this.mMain.mFileTypeSelectionWizardDialog.setPrevious(
                this.mMain.mSingleDataFileChooserWizardDialog);

            this.mMain.mFileTypeSelectionWizardDialog.setNext(dataTypeDialog);
            dataTypeDialog.setPrevious(this.mMain.mFileTypeSelectionWizardDialog);
          } else {
            this.mMain.mSingleDataFileChooserWizardDialog.setNext(dataTypeDialog);
            dataTypeDialog.setPrevious(this.mMain.mSingleDataFileChooserWizardDialog);
          }
        }

        // packs
        this.mMain.mSingleDataFileChooserWizardDialog.pack();
        if (dataTypeDialog != null) {
          dataTypeDialog.pack();
        }
        break;
      case SGMainFunctions.DATA_ADDITION_DRAG_AND_DROP:
        if (dataTypeDialog != null) {
          if (isFileTypeDialogPrev) {
            this.mMain.mFileTypeSelectionWizardDialog.setPrevious(null);

            this.mMain.mFileTypeSelectionWizardDialog.setNext(dataTypeDialog);
            dataTypeDialog.setPrevious(this.mMain.mFileTypeSelectionWizardDialog);
          } else {
            dataTypeDialog.setPrevious(null);
          }

          // packs
          dataTypeDialog.pack();
        }
        break;
      case SGMainFunctions.DATA_ADDITION_VIRTUAL:
        this.mMain.mFigureIDSelectionWizardDialog.setPrevious(null);

        if (dataTypeDialog != null) {
          this.mMain.mFigureIDSelectionWizardDialog.setNext(dataTypeDialog);
          dataTypeDialog.setPrevious(this.mMain.mFigureIDSelectionWizardDialog);

          // packs
          dataTypeDialog.pack();
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid method: " + method);
    }
  }

  /**
   * Sets up the connection of wizard dialogs. If the data type is Scalar-XY, Plot type selection
   * dialog is connected.
   *
   * @param fileType the file type (array data or netcdf)
   * @param dataType the method of data addition (tool bar or drag and drop)
   */
  void setupPlotTypeSelectionWizardDialogConnection(
      final FILE_TYPE fileType, final String dataType) {

    // select the data column type wizard dialog
    final SGDataSetupWizardDialog dataSetupDialog;
    switch (fileType) {
      case TXT_DATA:
        dataSetupDialog = this.mMain.mSDArrayDataSetupWizardDialog;
        break;
      case NETCDF_DATA:
        dataSetupDialog = this.mMain.mNetCDFDataSetupWizardDialog;
        break;
      case HDF5_DATA:
      case MATLAB_DATA:
      case VIRTUAL_DATA:
        dataSetupDialog = this.mMain.mMDArrayDataSetupWizardDialog;
        break;
      default:
        throw new IllegalArgumentException("Invalid type for data file: " + fileType);
    }

    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      this.mMain.mPlotTypeSelectionWizardDialog.setDataName(dataSetupDialog.getDataName());

      dataSetupDialog.setNext(this.mMain.mPlotTypeSelectionWizardDialog);
      this.mMain.mPlotTypeSelectionWizardDialog.setPrevious(dataSetupDialog);
      this.mMain.mPlotTypeSelectionWizardDialog.setNext(null);
      dataSetupDialog.pack();
      this.mMain.mPlotTypeSelectionWizardDialog.pack();

      SGDataColumnInfo[] cols = dataSetupDialog.getDataColumnInfoSet().getDataColumnInfoArray();
      int xCnt = 0;
      int yCnt = 0;
      for (SGDataColumnInfo col : cols) {
        String columnType = col.getColumnType();
        if (X_VALUE.equals(columnType)) {
          xCnt++;
        } else if (Y_VALUE.equals(columnType)) {
          yCnt++;
        }
      }
      boolean enabled = false;
      if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
        SGNetCDFDataSetupWizardDialog ncDialog = (SGNetCDFDataSetupWizardDialog) dataSetupDialog;
        SGNetCDFDataSetupPanel ncPanel = (SGNetCDFDataSetupPanel) ncDialog.getDataSetupPanel();
        SGIntegerSeriesSet pickUpIndices = ncPanel.getSXYPickUpIndices();
        if (pickUpIndices != null) {
          final int len = pickUpIndices.getLength();
          enabled = len > 1;
        }
      } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
        SGMDArrayDataSetupWizardDialog mdDialog = (SGMDArrayDataSetupWizardDialog) dataSetupDialog;
        SGMDArrayDataSetupPanel mdPanel = (SGMDArrayDataSetupPanel) mdDialog.getDataSetupPanel();
        SGIntegerSeriesSet pickUpIndices = mdPanel.getSXYPickUpIndices();
        if (pickUpIndices != null) {
          final int len = pickUpIndices.getLength();
          enabled = len > 1;
        }
      }
      if (!enabled) {
        enabled = (xCnt > 1 || yCnt > 1);
      }
      this.mMain.mPlotTypeSelectionWizardDialog.setLineColorAutoAssignmentEnabled(enabled);
    }
  }

  boolean makeTransitionForDragAndDrop(final ActionEvent e) {
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
      if (dg.equals(this.mMain.mFileTypeSelectionWizardDialog)) {
        SGDrawingWindow wnd = dg.getOwnerWindow();
        FILE_TYPE fileType = this.mMain.mFileTypeSelectionWizardDialog.getSelectedFileType();

        // setup the wizard dialogs
        this.mMain.mDataTypeWizardDialog.setDataFileType(fileType);
        this.mMain.mDataTypeWizardDialog.setNext(dg);
        this.setupDataAdditionWizardDialogConnection(
            this.mMain.mDataTypeWizardDialog,
            SGMainFunctions.DATA_ADDITION_DRAG_AND_DROP,
            fileType,
            true);

        if (this.toNetCDFOrMDArrayDataTypeDialog(
                wnd, this.mMain.mDataTypeWizardDialog, this.mMain.mDroppedDataFile.file)
            == false) {
          return false;
        }

        // set the location of wizard dialog
        this.mMain.mDataTypeWizardDialog.setCenter(wnd);

        dg.showNext();
      } else if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
        FILE_TYPE dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();
        if (FILE_TYPE.TXT_DATA.equals(dataFileType)) {
          String path = this.mMain.mDroppedDataFile.file.getPath();
          if (this.mMain
                  .getWizardTransition()
                  .makeTransition(
                      this.mMain.mDataTypeWizardDialog,
                      this.mMain.mSDArrayDataSetupWizardDialog,
                      path,
                      this.mMain.mDroppedDataFile.figureID,
                      this.mMain.mDroppedDataFile.pos)
              == false) {
            return false;
          }
        } else if (FILE_TYPE.NETCDF_DATA.equals(dataFileType)) {
          if (this.mMain
                  .getWizardTransition()
                  .makeTransition(
                      this.mMain.mDataTypeWizardDialog,
                      this.mMain.mNetCDFDataSetupWizardDialog,
                      this.mMain.mDroppedDataFile.file.getPath(),
                      this.mMain.mDroppedDataFile.figureID,
                      this.mMain.mDroppedDataFile.pos)
              == false) {
            return false;
          }
        } else if (FILE_TYPE.HDF5_DATA.equals(dataFileType)
            || FILE_TYPE.MATLAB_DATA.equals(dataFileType)) {
          String path = this.mMain.mDroppedDataFile.file.getPath();
          String dataName = SGUtility.createDataNameBase(path);
          if (this.mMain
                  .getWizardTransition()
                  .makeTransition(
                      this.mMain.mDataTypeWizardDialog,
                      this.mMain.mMDArrayDataSetupWizardDialog,
                      path,
                      this.mMain.mDroppedDataFile.figureID,
                      this.mMain.mDroppedDataFile.pos,
                      dataFileType,
                      dataName,
                      true)
              == false) {
            return false;
          }
        }
      } else if (dg instanceof SGDataSetupWizardDialog) {
        String dataType = this.mMain.mDataTypeWizardDialog.getSelectedDataType();
        FILE_TYPE dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();
        setupPlotTypeSelectionWizardDialogConnection(dataFileType, dataType);
        dg.showNext();
      }

    } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
      try {
        if (this.addDataByDragAndDropOK(dg) == false) {
          return false;
        }
      } finally {
        this.mMain.clearTemporaryData();
      }
    }

    return true;
  }

  private String getDataTypeButtonName(String dataType) {
    // selects the button
    String btnName = null;
    if (SGDataDataTypeUtility.isSXYTypeSingleData(dataType)) {
      btnName = SGDataTypeWizardDialog.SINGLE_SXY;
    } else if (SGDataDataTypeUtility.isSXYTypeMultipleData(dataType)) {
      btnName = SGDataTypeWizardDialog.MULTIPLE_SXY;
    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      btnName = SGDataTypeWizardDialog.PSEUDOCOLOR_MAP;
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      SGDataBuffer buffer = this.mMain.mVirtualMDArrayData.buffer;
      boolean polar;
      if (buffer instanceof SGVXYDataBuffer) {
        SGVXYDataBuffer vxyBuffer = (SGVXYDataBuffer) buffer;
        polar = vxyBuffer.isPolar();
      } else if (buffer instanceof SGVXYGridDataBuffer) {
        SGVXYGridDataBuffer vxyBuffer = (SGVXYGridDataBuffer) buffer;
        polar = vxyBuffer.isPolar();
      } else {
        throw new IllegalArgumentException("Invalid data buffer.");
      }
      if (polar) {
        btnName = SGDataTypeWizardDialog.POLAR_VXY;
      } else {
        btnName = SGDataTypeWizardDialog.ORTHOGONAL_VXY;
      }
    } else {
      throw new IllegalArgumentException("Invalid data type: " + dataType);
    }
    return btnName;
  }

  boolean makeTransitionForPlugin(final ActionEvent e) {
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
      if (dg.equals(this.mMain.mFigureIDSelectionWizardDialog)) {
        this.setupDataAdditionWizardDialogConnection(
            this.mMain.mDataTypeWizardDialog,
            SGMainFunctions.DATA_ADDITION_VIRTUAL,
            FILE_TYPE.VIRTUAL_DATA,
            false);

        // selects the button
        if (this.mMain.mVirtualMDArrayData.dataType != null) {
          String btnName = this.getDataTypeButtonName(this.mMain.mVirtualMDArrayData.dataType);
          this.mMain.mDataTypeWizardDialog.setSelected(btnName);
        }

        this.mMain.mDataTypeWizardDialog.setDataName(
            this.mMain.mFigureIDSelectionWizardDialog.getDataName());

        dg.showNext();
      } else if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
        String selectedBtnName = this.mMain.mDataTypeWizardDialog.getSelected();
        final boolean showDefault;
        if (this.mMain.mVirtualMDArrayData.dataType != null) {
          String defaultBtnName =
              this.getDataTypeButtonName(this.mMain.mVirtualMDArrayData.dataType);
          showDefault = !selectedBtnName.equals(defaultBtnName);
        } else {
          showDefault = true;
        }
        if (this.mMain
                .getWizardTransition()
                .makeTransition(
                    this.mMain.mDataTypeWizardDialog,
                    this.mMain.mMDArrayDataSetupWizardDialog,
                    null,
                    this.mMain.mFigureIDSelectionWizardDialog.getFigureID(),
                    null,
                    FILE_TYPE.VIRTUAL_DATA,
                    this.mMain.mDataTypeWizardDialog.getDataName(),
                    showDefault)
            == false) {
          return false;
        }
      } else if (dg.equals(this.mMain.mMDArrayDataSetupWizardDialog)) {
        String dataType = this.mMain.mDataTypeWizardDialog.getSelectedDataType();
        this.mMain.mPlotTypeSelectionWizardDialog.setDataName(
            this.mMain.mMDArrayDataSetupWizardDialog.getDataName());
        setupPlotTypeSelectionWizardDialogConnection(FILE_TYPE.VIRTUAL_DATA, dataType);
        dg.showNext();
      }
    } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {
      try {
        if (this.addDataByPlugin(dg) == false) {
          return false;
        }
      } finally {
        this.mMain.clearTemporaryData();
      }
    }
    return true;
  }

  /**
   * Add data from a dropped file.
   *
   * @param dg an event source
   * @return true if succeeded
   */
  private boolean addDataByDragAndDropOK(SGWizardDialog dg) {

    SGDrawingWindow wnd = dg.getOwnerWindow();
    String path = this.mMain.mDroppedDataFile.file.getAbsolutePath();

    // the location of a figure if it is created
    Point figureLocation;

    Object com;
    if (this.mMain.mDroppedDataFile.pos != null) {
      com = wnd.getComponent(this.mMain.mDroppedDataFile.pos.x, this.mMain.mDroppedDataFile.pos.y);
    } else {
      com = wnd;
    }

    // get the ID of figure
    int figureID;
    if (com instanceof SGDrawingWindow) {
      // get current figure ID
      figureID = wnd.assignFigureId();

      // set the dropped point to the new figure location
      figureLocation = this.mMain.mDroppedDataFile.pos;
    } else {
      SGFigure figure = (SGFigure) com;
      figureID = figure.getID();
      figureLocation = null;
    }

    FILE_TYPE dataFileType = null;
    if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
      dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();
    }

    if (dg.equals(this.mMain.mSDArrayDataSetupWizardDialog) || dataFileType == FILE_TYPE.TXT_DATA) {
      // text data
      if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, figureLocation) == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mNetCDFDataSetupWizardDialog)
        || dataFileType == FILE_TYPE.NETCDF_DATA) {
      // netCDF data
      if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, figureLocation) == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mMDArrayDataSetupWizardDialog)
        || dataFileType == FILE_TYPE.HDF5_DATA
        || dataFileType == FILE_TYPE.MATLAB_DATA) {
      if (this.drawNewGraphOfMDArrayData(
              wnd,
              dg,
              this.mMain.mDataTypeWizardDialog,
              this.mMain.mMDArrayDataSetupWizardDialog,
              path,
              figureID,
              figureLocation)
          == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      // plot type selection (text, netCDF or multidimensional data)
      SGWizardDialog prev = dg.getPrevious();
      if (this.mMain.mSDArrayDataSetupWizardDialog.equals(prev)) {
        if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, figureLocation) == false) {
          return false;
        }
      } else if (this.mMain.mNetCDFDataSetupWizardDialog.equals(prev)) {
        if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, figureLocation) == false) {
          return false;
        }
      } else if (this.mMain.mMDArrayDataSetupWizardDialog.equals(prev)) {
        if (this.drawNewGraphOfMDArrayData(
                wnd,
                dg,
                this.mMain.mDataTypeWizardDialog,
                this.mMain.mMDArrayDataSetupWizardDialog,
                path,
                figureID,
                figureLocation)
            == false) {
          return false;
        }
      }
    }

    wnd.notifyToRoot();

    return true;
  }

  private boolean drawNewGraphOfSDArrayData(
      SGDrawingWindow wnd, SGWizardDialog dg, String path, int figureID, Point figureLocation) {

    // get data type
    String dataType = this.mMain.mDataTypeWizardDialog.getSelectedDataType();
    if (dataType == null) {
      SGUtility.showErrorMessageDialog(
          wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
      return false;
    }

    SGSDArrayDataSetupWizardDialog setupDialog = this.mMain.mSDArrayDataSetupWizardDialog;

    // get information
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(
            dataType, this.mMain.mDataTypeWizardDialog, figureID, figureLocation);

    // get selected column types
    boolean strideAvailable = false;
    SGDataColumnInfoSet colInfoSet = null;
    if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
      colInfoSet =
          this.mMain
              .getPropertyFileHandler()
              .getSDArrayDefaultDataColumnInfo(path, dataType, infoMap, false, null);
      if (colInfoSet != null) {
        // calculate the stride
        SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
        Map<String, SGIntegerSeriesSet> strideMap =
            SGDataStrideUtility.calcSDArrayDefaultStride(colArray, infoMap);
        strideMap.put(
            SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
            strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE));
        infoMap.putAll(strideMap);

        final boolean defaultStrideAvailable =
            (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
        strideAvailable = this.confirmStrideAvailable(strideMap, wnd, defaultStrideAvailable);

        String dataNameBase = SGUtility.createDataNameBase(path);
        infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);
      }
    } else if (dg.equals(setupDialog)) {
      colInfoSet = setupDialog.getDataColumnInfoSet();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
      infoMap.putAll(setupDialog.getStrideMap());
      strideAvailable = setupDialog.isStrideAvailable();
    } else if (dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      colInfoSet = setupDialog.getDataColumnInfoSet();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
      infoMap.putAll(setupDialog.getStrideMap());
      strideAvailable = setupDialog.isStrideAvailable();
      SGMainFunctions.addPlotTypeSelectionValuesToInfoMap(
          infoMap, this.mMain.mPlotTypeSelectionWizardDialog);
    }
    infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);

    if (colInfoSet == null) {
      SGUtility.showErrorMessageDialog(
          wnd, SGMainFunctions.ERRMSG_TO_DRAW_GRAPH, SGIConstants.TITLE_ERROR);
      return false;
    }

    // draw the graph
    // open the file
    DataSourceInfo dataSource = new DataSourceInfo(path);
    SGStatus status =
        this.mMain.drawGraph(
            wnd, figureID, colInfoSet, infoMap, dataSource, null, true, figureLocation);
    if (status.isSucceeded() == false) {
      String msg = status.getMessage();
      if (msg == null) {
        msg = SGMainFunctions.ERRMSG_DATA_ADDITION;
      }
      SGUtility.showErrorMessageDialog(wnd, msg, SGIConstants.TITLE_ERROR);
      return false;
    }

    return true;
  }

  private boolean drawNewGraphOfNetcdfData(
      SGDrawingWindow wnd, SGWizardDialog dg, String path, int figureID, Point figureLocation) {

    // get data type
    String dataType = this.mMain.mDataTypeWizardDialog.getSelectedDataType();
    if (dataType == null) {
      SGUtility.showErrorMessageDialog(
          wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
      return false;
    }

    // create a information map
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(
            dataType, this.mMain.mDataTypeWizardDialog, figureID, figureLocation);

    // open the file
    SGNetCDFFile nc = this.mMain.getNetCDFFile(path);
    if (nc == null) {
      return false;
    }
    DataSourceInfo dataSource = new DataSourceInfo(nc);

    // put the netCDF file
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, nc);

    // get selected column types
    SGDataColumnInfoSet colInfoSet = null;
    boolean strideAvailable = false;
    if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
      colInfoSet =
          this.mMain.getPropertyFileHandler().getNetCDFDefaultDataColumnInfo(nc, dataType, infoMap);
      if (colInfoSet == null) {
        SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
        return false;
      }

      // calculate the stride
      SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
      Map<String, SGIntegerSeriesSet> strideMap =
          SGDataStrideUtility.calcNetCDFDefaultStride(colArray, infoMap);
      strideMap.put(
          SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
          strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE));
      infoMap.putAll(strideMap);

      final boolean defaultStrideAvailable =
          (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
      strideAvailable = this.confirmStrideAvailable(strideMap, wnd, defaultStrideAvailable);

      String dataNameBase = SGUtility.createDataNameBase(path);
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);
    } else if (dg.equals(this.mMain.mNetCDFDataSetupWizardDialog)) {
      colInfoSet = this.mMain.mNetCDFDataSetupWizardDialog.getDataColumnInfoSet();
      infoMap.put(
          SGIDataInformationKeyConstants.KEY_DATA_NAME,
          this.mMain.mNetCDFDataSetupWizardDialog.getDataName());
      infoMap.putAll(this.mMain.mNetCDFDataSetupWizardDialog.getStrideMap());
      SGMainFunctions.addDimensionValuesToInfoMap(infoMap, this.mMain.mNetCDFDataSetupWizardDialog);
      strideAvailable = this.mMain.mNetCDFDataSetupWizardDialog.isStrideAvailable();
    } else if (dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      colInfoSet = this.mMain.mNetCDFDataSetupWizardDialog.getDataColumnInfoSet();
      infoMap.put(
          SGIDataInformationKeyConstants.KEY_DATA_NAME,
          this.mMain.mNetCDFDataSetupWizardDialog.getDataName());
      infoMap.putAll(this.mMain.mNetCDFDataSetupWizardDialog.getStrideMap());
      SGMainFunctions.addDimensionValuesToInfoMap(infoMap, this.mMain.mNetCDFDataSetupWizardDialog);
      SGMainFunctions.addPlotTypeSelectionValuesToInfoMap(
          infoMap, this.mMain.mPlotTypeSelectionWizardDialog);
      strideAvailable = this.mMain.mNetCDFDataSetupWizardDialog.isStrideAvailable();
    }

    infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);

    if (colInfoSet == null) {
      SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
      return false;
    }

    if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
      // set default value of dimension origin and step
      if (this.mMain
              .getWizardTransition()
              .setupNetCDFDefaultDimensionValues(
                  dataType, infoMap, this.mMain.mNetCDFDataSetupWizardDialog)
          == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mNetCDFDataSetupWizardDialog)
        || dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      // set dimension origin and step
      if (SGMainFunctions.addDimensionValuesToInfoMap(
              infoMap, this.mMain.mNetCDFDataSetupWizardDialog)
          == false) {
        return false;
      }
    }

    // draw the graph
    SGStatus status =
        this.mMain.drawGraph(
            wnd, figureID, colInfoSet, infoMap, dataSource, null, false, figureLocation);
    if (status.isSucceeded() == false) {
      String msg = status.getMessage();
      if (msg == null) {
        msg = SGMainFunctions.ERRMSG_DATA_ADDITION;
      }
      SGUtility.showErrorMessageDialog(wnd, msg, SGIConstants.TITLE_ERROR);
      return false;
    }

    // puts stride flag
    SGDataInfoMapUtility.putDataStrideAvailable(strideAvailable);

    return true;
  }

  private boolean confirmStrideAvailable(
      Map<String, SGIntegerSeriesSet> strideMap, SGDrawingWindow wnd, final boolean defaultValue) {
    boolean strideAvailable = defaultValue;
    Iterator<Entry<String, SGIntegerSeriesSet>> itr = strideMap.entrySet().iterator();
    List<String> strideKeyList = new ArrayList<String>();
    while (itr.hasNext()) {
      Entry<String, SGIntegerSeriesSet> entry = itr.next();
      String key = entry.getKey();
      SGIntegerSeriesSet stride = entry.getValue();
      if (stride != null) {
        if (!"0:end".equals(stride.toString())) {
          strideKeyList.add(key);
        }
      }
    }
    if (strideKeyList.size() > 0) {
      final String message =
          "Data plot might be too slow due to its size.\nCan you use auto assigned array section?";
      final int ret = SGUtility.showYesNoConfirmationDialog(wnd, message);
      if (ret == JOptionPane.YES_OPTION) {
        strideAvailable = true;
      }
    }
    return strideAvailable;
  }

  private boolean drawNewGraphOfMDArrayData(
      SGDrawingWindow wnd,
      SGWizardDialog dg,
      SGDataTypeWizardDialog dataTypeDialog,
      SGMDArrayDataSetupWizardDialog setupDialog,
      String path,
      int figureID,
      Point figureLocation) {

    // get data type
    String dataType = null;
    if (dg.equals(this.mMain.mFigureIDSelectionWizardDialog)) {
      if (this.mMain.mVirtualMDArrayData != null) {
        dataType = this.mMain.mVirtualMDArrayData.dataType;
      } else {
        SGUtility.showErrorMessageDialog(
            wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
        return false;
      }
    } else {
      dataType = dataTypeDialog.getSelectedDataType();
      if (dataType == null) {
        SGUtility.showErrorMessageDialog(
            wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
        return false;
      }
    }

    // create a information map
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(dataType, dataTypeDialog, figureID, figureLocation);

    SGDataColumnInfoSet colInfoSet = null;
    SGMDArrayFile file = null;
    DataSourceInfo dataSource = null;
    if (path != null) {
      FILE_TYPE type = SGApplicationUtility.identifyDataFileType(path);
      if (FILE_TYPE.HDF5_DATA.equals(type)) {
        file = this.mMain.getHDF5File(path);
        if (file == null) {
          return false;
        }
      } else {
        file = this.mMain.getMATLABFile(path);
        if (file == null) {
          return false;
        }
      }
    } else {
      file = this.mMain.mVirtualMDArrayData.file;
      SGDataBuffer buffer = this.mMain.mVirtualMDArrayData.buffer;
      if (buffer != null) {
        String gridTypeKey = buffer.getGridTypeKey();
        if (gridTypeKey != null) {
          infoMap.put(gridTypeKey, buffer.isGridType());
        }
      }
    }
    dataSource = new DataSourceInfo(file);

    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, file);

    // get selected column types
    boolean strideAvailable = false;
    if (dg.equals(dataTypeDialog) || dg.equals(this.mMain.mFigureIDSelectionWizardDialog)) {
      if (this.mMain.mVirtualMDArrayData != null) {
        if (this.mMain.mVirtualMDArrayData.infoMap != null) {
          infoMap.putAll(this.mMain.mVirtualMDArrayData.infoMap);
        }
        colInfoSet = this.mMain.mVirtualMDArrayData.colInfoSet;
        if (colInfoSet == null) {
          colInfoSet =
              this.mMain
                  .getPropertyFileHandler()
                  .getMDArrayDataDefaultDataColumnInfo(file, dataType, infoMap);
        }
      } else {
        colInfoSet =
            this.mMain
                .getPropertyFileHandler()
                .getMDArrayDataDefaultDataColumnInfo(file, dataType, infoMap);
      }
      if (colInfoSet == null) {
        SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
        return false;
      }

      // calculate the stride
      SGDataColumnInfo[] colArray = colInfoSet.getDataColumnInfoArray();
      Map<String, SGIntegerSeriesSet> strideMap =
          SGDataStrideUtility.calcMDArrayDefaultStride(colArray, infoMap);
      strideMap.put(
          SGIDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
          strideMap.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE));
      infoMap.putAll(strideMap);

      final boolean defaultStrideAvailable =
          (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE);
      strideAvailable = this.confirmStrideAvailable(strideMap, wnd, defaultStrideAvailable);

      String dataNameBase;
      if (this.mMain.mVirtualMDArrayData != null) {
        dataNameBase = this.mMain.mVirtualMDArrayData.name;
      } else {
        dataNameBase = SGUtility.createDataNameBase(path);
      }
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataNameBase);
    } else if (dg.equals(setupDialog)) {
      colInfoSet = setupDialog.getDataColumnInfoSet();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
      infoMap.putAll(setupDialog.getStrideMap());
      SGMainFunctions.addDimensionValuesToInfoMap(infoMap, setupDialog);
      strideAvailable = setupDialog.isStrideAvailable();
    } else if (dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      colInfoSet = setupDialog.getDataColumnInfoSet();
      infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, setupDialog.getDataName());
      infoMap.putAll(setupDialog.getStrideMap());
      SGMainFunctions.addDimensionValuesToInfoMap(infoMap, setupDialog);
      strideAvailable = setupDialog.isStrideAvailable();
      SGMainFunctions.addPlotTypeSelectionValuesToInfoMap(
          infoMap, this.mMain.mPlotTypeSelectionWizardDialog);
    }
    infoMap.put(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);

    if (colInfoSet == null) {
      SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
      return false;
    }

    if (dg.equals(this.mMain.mDataTypeWizardDialog)) {
      SGMDArrayDataSetupWizardDialog dataSetupDialog;
      boolean showDefault;
      FILE_TYPE dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();
      if (FILE_TYPE.HDF5_DATA.equals(dataFileType)
          || FILE_TYPE.MATLAB_DATA.equals(dataFileType)
          || FILE_TYPE.VIRTUAL_DATA.equals(dataFileType)) {
        dataSetupDialog = this.mMain.mMDArrayDataSetupWizardDialog;
        if (FILE_TYPE.VIRTUAL_DATA.equals(dataFileType)) {
          if (this.mMain.mVirtualMDArrayData.colInfoSet != null) {
            // from the plug-in
            showDefault = false;
          } else {
            // from the data viewer
            showDefault = true;
          }
        } else {
          showDefault = true;
        }
      } else {
        return false;
      }

      // set default value of dimension origin and step
      if (this.mMain
              .getWizardTransition()
              .setupMDArrayDefaultDimensionValues(dataType, infoMap, dataSetupDialog, showDefault)
          == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mMDArrayDataSetupWizardDialog)) {
      // set dimension origin and step
      if (SGMainFunctions.addDimensionValuesToInfoMap(infoMap, (SGMDArrayDataSetupWizardDialog) dg)
          == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      SGWizardDialog prev = this.mMain.mPlotTypeSelectionWizardDialog.getPrevious();
      // set dimension origin and step
      if (SGMainFunctions.addDimensionValuesToInfoMap(
              infoMap, (SGMDArrayDataSetupWizardDialog) prev)
          == false) {
        return false;
      }
    }

    // draw the graph
    SGStatus status =
        this.mMain.drawGraph(
            wnd, figureID, colInfoSet, infoMap, dataSource, null, false, figureLocation);
    if (status.isSucceeded() == false) {
      String msg = status.getMessage();
      if (msg == null) {
        msg = SGMainFunctions.ERRMSG_DATA_ADDITION;
      }
      SGUtility.showErrorMessageDialog(wnd, msg, SGIConstants.TITLE_ERROR);
      return false;
    }

    // puts stride flag
    SGDataInfoMapUtility.putDataStrideAvailable(strideAvailable);

    return true;
  }

  boolean makeTransitionForToolBar(final ActionEvent e) {

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
      if (dg.equals(this.mMain.mFigureIDSelectionWizardDialog)) {
        this.mMain.mSingleDataFileChooserWizardDialog.setCurrentFile(
            this.mMain.getCurrentFileDirectory(), null);
        dg.showNext();
      } else if (dg.equals(this.mMain.mSingleDataFileChooserWizardDialog)) {
        if (this.mMain.mSingleDataFileChooserWizardDialog.isLocalFileSelected()) {
          String fileName = this.mMain.mSingleDataFileChooserWizardDialog.getFileName();
          File file = new File(fileName);
          String filePath = file.getAbsolutePath();
          FILE_TYPE fileType = SGApplicationUtility.identifyDataFileType(filePath);
          if (FILE_TYPE.POSSIBLY_HDF5_DATA.equals(fileType)) {
            SGApplicationUtility.showHDF5ReadErrorMessageDialog(dg, filePath);
            return false;
          }
          this.mMain.mDataTypeWizardDialog.setDataFileType(fileType);

          final boolean isHDF5 = FILE_TYPE.HDF5_DATA.equals(fileType);
          this.setupDataAdditionWizardDialogConnection(
              this.mMain.mDataTypeWizardDialog,
              SGMainFunctions.DATA_ADDITION_TOOL_BAR,
              fileType,
              isHDF5);

          if (isHDF5) {
            // set up the dialog to select HDF5/NetCDF-4
            String dataName = SGUtility.createDataNameBase(filePath);
            this.mMain.mFileTypeSelectionWizardDialog.setDataName(dataName);
            FILE_TYPE selectedFileType = this.mMain.getNetCDF4orHDF5FileType(file);
            this.mMain.mFileTypeSelectionWizardDialog.setSelectedFileType(selectedFileType);
          } else {
            // for TXT, NetCDF-3 or MATLAB file
            if (FILE_TYPE.TXT_DATA.equals(fileType)) {
              if (this.toSDArrayDataTypeDialog(
                      this.mMain.mSingleDataFileChooserWizardDialog,
                      this.mMain.mDataTypeWizardDialog,
                      file)
                  == false) {
                return false;
              }
            } else if (FILE_TYPE.NETCDF_DATA.equals(fileType)
                || FILE_TYPE.MATLAB_DATA.equals(fileType)) {
              if (this.toNetCDFOrMDArrayDataTypeDialog(
                      this.mMain.mSingleDataFileChooserWizardDialog,
                      this.mMain.mDataTypeWizardDialog,
                      file)
                  == false) {
                return false;
              }
            }
          }

          // set to the file chooser because file path is due to be taken from file chooser dialog
          this.mMain.mSingleDataFileChooserWizardDialog.setSelectedFile(file);

        } else {
          this.mMain.mDataTypeWizardDialog.setDataFileType(FILE_TYPE.NETCDF_DATA);

          // setup the next dialog
          this.setupDataAdditionWizardDialogConnection(
              this.mMain.mDataTypeWizardDialog,
              SGMainFunctions.DATA_ADDITION_TOOL_BAR,
              FILE_TYPE.NETCDF_DATA,
              false);
        }

        // show the next dialog
        dg.showNext();

      } else if (dg.equals(this.mMain.mFileTypeSelectionWizardDialog)) {

        FILE_TYPE fileType = this.mMain.mFileTypeSelectionWizardDialog.getSelectedFileType();
        if (FILE_TYPE.NETCDF_DATA.equals(fileType) || FILE_TYPE.HDF5_DATA.equals(fileType)) {
          String fileName = this.mMain.mSingleDataFileChooserWizardDialog.getFileName();
          if (this.toNetCDFOrMDArrayDataTypeDialog(
                  this.mMain.mSingleDataFileChooserWizardDialog,
                  this.mMain.mDataTypeWizardDialog,
                  new File(fileName))
              == false) {
            return false;
          }
        } else {
          return false;
        }
        this.mMain.mDataTypeWizardDialog.setDataFileType(fileType);

        // show the next dialog
        dg.showNext();

      } else if (dg.equals(this.mMain.mDataTypeWizardDialog)) {

        FILE_TYPE dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();
        if (FILE_TYPE.TXT_DATA.equals(dataFileType)) {
          final int figureID = this.mMain.mFigureIDSelectionWizardDialog.getFigureID();
          File f = this.mMain.mSingleDataFileChooserWizardDialog.getSelectedFile();
          String path = f.getPath();
          if (this.mMain
                  .getWizardTransition()
                  .makeTransition(
                      this.mMain.mDataTypeWizardDialog,
                      this.mMain.mSDArrayDataSetupWizardDialog,
                      path,
                      figureID,
                      null)
              == false) {
            return false;
          }

        } else if (FILE_TYPE.NETCDF_DATA.equals(dataFileType)) {
          final int figureID = this.mMain.mFigureIDSelectionWizardDialog.getFigureID();
          String fileName = this.mMain.mSingleDataFileChooserWizardDialog.getFileName();
          if (this.mMain.mSingleDataFileChooserWizardDialog.isLocalFileSelected()) {
            if (this.mMain
                    .getWizardTransition()
                    .makeTransition(
                        this.mMain.mDataTypeWizardDialog,
                        this.mMain.mNetCDFDataSetupWizardDialog,
                        fileName,
                        figureID,
                        null)
                == false) {
              return false;
            }
          } else {
            FILE_TYPE type = SGApplicationUtility.identifyDataFileType(fileName);
            if (FILE_TYPE.NETCDF_DATA.equals(type)) {
              if (this.mMain
                      .getWizardTransition()
                      .makeTransition(
                          this.mMain.mDataTypeWizardDialog,
                          this.mMain.mNetCDFDataSetupWizardDialog,
                          fileName,
                          figureID,
                          null)
                  == false) {
                return false;
              }
            } else {
              SGUtility.showErrorMessageDialog(
                  dg, SGMainFunctions.ERRMSG_URL_OF_NETCDF, SGIConstants.TITLE_ERROR);
              this.mMain.mDataTypeWizardDialog.setVisible(false);
              this.mMain.mSingleDataFileChooserWizardDialog.setVisible(true);
            }
          }
        } else if (FILE_TYPE.HDF5_DATA.equals(dataFileType)
            || FILE_TYPE.MATLAB_DATA.equals(dataFileType)) {
          final int figureID = this.mMain.mFigureIDSelectionWizardDialog.getFigureID();
          String path = this.mMain.mSingleDataFileChooserWizardDialog.getFileName();
          String dataName = SGUtility.createDataNameBase(path);
          if (this.mMain
                  .getWizardTransition()
                  .makeTransition(
                      this.mMain.mDataTypeWizardDialog,
                      this.mMain.mMDArrayDataSetupWizardDialog,
                      path,
                      figureID,
                      null,
                      dataFileType,
                      dataName,
                      true)
              == false) {
            return false;
          }
        }

      } else if (dg instanceof SGDataSetupWizardDialog) {
        String dataType = this.mMain.mDataTypeWizardDialog.getSelectedDataType();
        FILE_TYPE dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();
        setupPlotTypeSelectionWizardDialogConnection(dataFileType, dataType);
        dg.showNext();
      }

    } else if (command.equals(SGDialog.OK_BUTTON_TEXT)) {

      try {
        if (this.addDataByToolBar(dg) == false) {
          return false;
        }
      } finally {
        this.mMain.clearTemporaryData();
      }
    }

    return true;
  }

  /**
   * Do add data to figure.
   *
   * @param dg wizard dialog which is source on doing addition.
   * @return
   */
  private boolean addDataByToolBar(SGWizardDialog dg) {

    // set invisible the dialog
    dg.setVisible(false);

    SGDrawingWindow wnd = dg.getOwnerWindow();

    // figure id
    final int figureID = this.mMain.mFigureIDSelectionWizardDialog.getFigureID();

    // data path
    String filename = this.mMain.mSingleDataFileChooserWizardDialog.getFileName();
    String path = filename;
    if (this.mMain.mSingleDataFileChooserWizardDialog.isLocalFileSelected()) {
      // path of the local data file
      File f = this.mMain.mSingleDataFileChooserWizardDialog.getSelectedFile();
      path = f.getPath();
    }

    FILE_TYPE dataFileType = this.mMain.mDataTypeWizardDialog.getDataFileType();

    if (dg.equals(this.mMain.mSDArrayDataSetupWizardDialog) || dataFileType == FILE_TYPE.TXT_DATA) {
      // text data
      if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, null) == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mNetCDFDataSetupWizardDialog)
        || dataFileType == FILE_TYPE.NETCDF_DATA) {
      // netCDF data
      if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, null) == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mMDArrayDataSetupWizardDialog)
        || dataFileType == FILE_TYPE.HDF5_DATA
        || dataFileType == FILE_TYPE.MATLAB_DATA) {
      // MDArray data
      if (this.drawNewGraphOfMDArrayData(
              wnd,
              dg,
              this.mMain.mDataTypeWizardDialog,
              this.mMain.mMDArrayDataSetupWizardDialog,
              path,
              figureID,
              null)
          == false) {
        return false;
      }
    } else if (dg.equals(this.mMain.mPlotTypeSelectionWizardDialog)) {
      if (dg.getPrevious().equals(this.mMain.mSDArrayDataSetupWizardDialog)) {
        if (this.drawNewGraphOfSDArrayData(wnd, dg, path, figureID, null) == false) {
          return false;
        }
      } else {
        if (this.drawNewGraphOfNetcdfData(wnd, dg, path, figureID, null) == false) {
          return false;
        }
      }
    }

    wnd.notifyToRoot();

    return true;
  }

  /**
   * Process to data type selection dialog.
   *
   * @param owner the owner of dialogs
   * @param next a dialog for data type selection
   * @param f a file
   * @return true if succeeded
   */
  boolean toSDArrayDataTypeDialog(Window owner, SGDataTypeWizardDialog next, File f) {

    if (f.exists() == false) {
      SGUtility.showFileNotFoundMessageDialog(owner);
      return false;
    }

    String path = f.getPath();

    List<String> cList = new ArrayList<String>();
    try {
      if (this.mMain.mDataCreator.getDataTypeCandidateList(path, cList) == false) {
        SGApplicationUtility.showDataFileInvalidMessageDialog(owner);
        return false;
      }
    } catch (FileNotFoundException ex) {
      SGUtility.showFileNotFoundMessageDialog(owner);
      return false;
    }
    if (cList.size() == 0) {
      SGApplicationUtility.showDataFileInvalidMessageDialog(owner);
      return false;
    }

    List<String> typeList = new ArrayList<String>();
    for (int i = 0; i < cList.size(); i++) {
      typeList.add(SGApplicationUtility.getArrayDataType(cList.get(i)));
    }

    // set candidate data-type to the dialog
    if (next.setAvailableDataType(typeList) == false) {
      SGApplicationUtility.showDataFileInvalidMessageDialog(owner);
      return false;
    }

    String dataName = SGUtility.createDataNameBase(path);
    next.setDataName(dataName);

    return true;
  }

  /**
   * Process to data type selection dialog.
   *
   * @param owner the owner of dialogs
   * @param next a dialog for data type selection
   * @param f a file
   * @return true if succeeded
   */
  boolean toNetCDFOrMDArrayDataTypeDialog(Window owner, SGDataTypeWizardDialog next, File f) {
    if (f.exists() == false) {
      SGUtility.showFileNotFoundMessageDialog(owner);
      return false;
    }

    next.setAllDataTypeButtonsEnabled(true);

    String path = f.getPath();
    String dataName = SGUtility.createDataNameBase(path);
    next.setDataName(dataName);

    return true;
  }

  boolean onToolBarDataAdditionExecuted(final SGDrawingWindow wnd) {

    // create wizard dialogs
    this.createDataAdditionWizardDialogs(wnd);

    // set figure ID numbers
    final int[] idArray = wnd.getVisibleFigureIDArray();
    this.mMain.mFigureIDSelectionWizardDialog.setIDNumbers(idArray);

    // sets the OK button invisible
    this.mMain.mFigureIDSelectionWizardDialog.setOKButtonVisible(false);

    // sets the name
    this.mMain.mFigureIDSelectionWizardDialog.setDataName(null);

    // packs the dialog
    this.mMain.mFigureIDSelectionWizardDialog.pack();

    // setup the connection
    this.setupDataAdditionWizardDialogConnection(
        null, SGMainFunctions.DATA_ADDITION_TOOL_BAR, null, false);

    // set location
    this.mMain.mFigureIDSelectionWizardDialog.setCenter(wnd);

    // show the first wizard dialog
    this.mMain.mFigureIDSelectionWizardDialog.setVisible(true);

    // update the selected file name
    File f = this.mMain.mSingleDataFileChooserWizardDialog.getSelectedFile();
    if (f == null) {
      return false;
    }
    this.mMain.updateCurrentFile(f, FILE_TYPE.TXT_DATA);

    return true;
  }

  /** Returns true if given object is a wizard dialog for data-addition. */
  boolean isDataAdditionDialog(Object obj) {
    return obj.equals(this.mMain.mFigureIDSelectionWizardDialog)
        || obj.equals(this.mMain.mSingleDataFileChooserWizardDialog)
        || obj.equals(this.mMain.mFileTypeSelectionWizardDialog)
        || obj.equals(this.mMain.mDataTypeWizardDialog)
        || obj.equals(this.mMain.mSDArrayDataSetupWizardDialog)
        || obj.equals(this.mMain.mNetCDFDataSetupWizardDialog)
        || obj.equals(this.mMain.mMDArrayDataSetupWizardDialog)
        || obj.equals(this.mMain.mPlotTypeSelectionWizardDialog);
  }

  private boolean addDataByPlugin(SGWizardDialog dg) {

    // set invisible the dialog
    dg.setVisible(false);

    SGDrawingWindow wnd = dg.getOwnerWindow();

    // figure id
    final int figureID = this.mMain.mFigureIDSelectionWizardDialog.getFigureID();

    if (this.drawNewGraphOfMDArrayData(
            wnd,
            dg,
            this.mMain.mDataTypeWizardDialog,
            this.mMain.mMDArrayDataSetupWizardDialog,
            null,
            figureID,
            null)
        == false) {
      return false;
    }

    wnd.notifyToRoot();

    return true;
  }
}
