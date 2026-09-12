package jp.riken.brain.ni.samuraigraph.application;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5Exception;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.jmatio.io.MatFileReader;
import java.awt.Point;
import java.awt.Window;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ucar.nc2.NetcdfFile;

/** Transition logic between the wizard dialogs for data addition. */
final class SGMainFunctionsWizardTransition
    implements SGIApplicationConstants, SGIApplicationTextConstants {

  private static final Logger logger = LogManager.getLogger(SGMainFunctionsWizardTransition.class);

  private final SGMainFunctions mMain;

  SGMainFunctionsWizardTransition(SGMainFunctions main) {
    this.mMain = main;
  }

  boolean setupNetCDFDefaultDimensionValues(
      final String dataType,
      final Map<String, Object> infoMap,
      final SGNetCDFDataSetupWizardDialog dg) {
    final boolean multipleVariable = true;
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
        Boolean.valueOf(multipleVariable));

    SGNetCDFFile ncSource =
        (SGNetCDFFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
    NetcdfFile ncFile = null;
    try {
      String path = ncSource.getNetcdfFile().getLocation();
      ncFile = SGApplicationUtility.openNetCDF(path);
      if (ncFile == null) {
        return false;
      }
      SGNetCDFFile nc = new SGNetCDFFile(ncFile);
      if (this.setupNetCDFDataSetupDialog(nc, dataType, infoMap, dg) == false) {
        return false;
      }
    } catch (IOException e1) {
      return false;
    } finally {
      try {
        if (ncFile != null) {
          ncFile.close();
        }
      } catch (IOException e1) {
        logger.debug("Exception occurred", e1);
      }
    }

    SGIntegerSeriesSet indices = dg.getSXYPickUpIndices();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);

    return true;
  }

  boolean setupMDArrayDefaultDimensionValues(
      final String dataType,
      final Map<String, Object> infoMap,
      SGMDArrayDataSetupWizardDialog dg,
      final boolean showDefault) {
    final boolean multipleVariable = true;
    infoMap.put(
        SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
        Boolean.valueOf(multipleVariable));

    SGMDArrayFile dataSource =
        (SGMDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
    SGMDArrayFile mdFile = null;
    String path = dataSource.getPath();
    SGDataColumnInfoSet colInfoSet = null;
    if (path != null) {
      mdFile = SGApplicationUtility.openMDArrayFile(dataType, path);
    } else {
      mdFile = this.mMain.mVirtualMDArrayData.file;
      if (this.mMain.mVirtualMDArrayData.colInfoSet != null) {
        colInfoSet = (SGDataColumnInfoSet) this.mMain.mVirtualMDArrayData.colInfoSet.clone();
      }
    }
    if (this.setupMDArrayDataSetupDialog(mdFile, dataType, infoMap, dg, colInfoSet, showDefault)
        == false) {
      return false;
    }

    SGIntegerSeriesSet indices = dg.getSXYPickUpIndices();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);

    return true;
  }

  /**
   * Makes the transition between wizard dialogs.
   *
   * @param prev previous dialog to select data type
   * @param next next dialog to select column
   * @param f a data file
   * @return true if succeeded
   */
  boolean makeTransition(
      SGDataTypeWizardDialog prev,
      SGSDArrayDataSetupWizardDialog next,
      String path,
      int figureID,
      Point pos) {

    SGDrawingWindow wnd = prev.getOwnerWindow();

    // set invisible the dialog
    prev.setVisible(false);

    // get selected file type
    String dataType = prev.getSelectedDataType();
    if (dataType == null) {
      SGUtility.showErrorMessageDialog(
          wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
      return false;
    }

    // create a map of information
    Map<String, Object> infoMap = SGDataInfoMapUtility.createInfoMap(dataType, prev, figureID, pos);

    SGDataColumnInfoSet colInfoSet =
        this.mMain
            .getPropertyFileHandler()
            .getSDArrayDefaultDataColumnInfo(path, dataType, infoMap, false, null);
    if (colInfoSet == null) {
      SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
      return false;
    }

    // create default data name
    String dataName = SGUtility.createDataNameBase(path);

    // put into the infoMap
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

    SGSDArrayFile sdFile;
    try {
      sdFile = (SGSDArrayFile) SGApplicationUtility.createDataSource(path, colInfoSet, infoMap);
    } catch (FileNotFoundException e) {
      return false;
    }

    // set information to the dialog for data column selection
    next.setData(sdFile, dataType, colInfoSet, infoMap, false);

    // show the dialog to select data columns
    next.setCenter(wnd);
    next.setVisible(true);

    return true;
  }

  /**
   * Makes the transition between wizard dialogs of netCDF data.
   *
   * @param prev previous dialog to select data type
   * @param next next dialog to select column
   * @param path a data file path
   * @return true if succeeded
   */
  boolean makeTransition(
      SGDataTypeWizardDialog prev,
      SGNetCDFDataSetupWizardDialog next,
      String path,
      int figureID,
      Point pos) {

    SGDrawingWindow wnd = prev.getOwnerWindow();

    // set invisible the dialog
    prev.setVisible(false);

    // get selected file type
    String dataType = prev.getSelectedDataType();
    if (dataType == null) {
      SGUtility.showErrorMessageDialog(
          wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
      return false;
    }

    // create a map of information
    Map<String, Object> infoMap = SGDataInfoMapUtility.createInfoMap(dataType, prev, figureID, pos);

    // create default data name
    String dataName = SGUtility.createDataNameBase(path);

    // put into the infoMap
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

    // open the file
    NetcdfFile ncFile = null;
    try {
      ncFile = SGApplicationUtility.openNetCDF(path);
    } catch (Exception e1) {
      SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
      return false;
    }
    if (ncFile == null) {
      SGApplicationUtility.showDataFileInvalidMessageDialog(wnd);
      return false;
    }
    SGNetCDFFile nc = new SGNetCDFFile(ncFile);
    try {
      if (this.setupNetCDFDataSetupDialog(nc, dataType, infoMap, next) == false) {
        return false;
      }
    } finally {
      try {
        ncFile.close();
      } catch (IOException e1) {
        return false;
      }
    }

    // show the dialog to select data columns
    next.setCenter(wnd);
    next.setVisible(true);

    return true;
  }

  /**
   * Makes the transition between wizard dialogs of netCDF data.
   *
   * @param prev previous dialog to select data type
   * @param next next dialog to select column
   * @param path a data file path
   * @return true if succeeded
   */
  boolean makeTransition(
      SGDataTypeWizardDialog prev,
      SGMDArrayDataSetupWizardDialog next,
      String path,
      int figureID,
      Point pos,
      final FILE_TYPE fileType,
      String dataName,
      final boolean showDefault) {

    SGDrawingWindow wnd = prev.getOwnerWindow();

    // set invisible the dialog
    prev.setVisible(false);

    // get selected file type
    String dataType = prev.getSelectedDataType();
    if (dataType == null) {
      SGUtility.showErrorMessageDialog(
          wnd, SGMainFunctions.ERRMSG_TO_GET_DATA_TYPE, SGIConstants.TITLE_ERROR);
      return false;
    }

    // create a map of information
    Map<String, Object> infoMap = SGDataInfoMapUtility.createInfoMap(dataType, prev, figureID, pos);

    // put into the infoMap
    infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_NAME, dataName);

    // open the file
    if (fileType == FILE_TYPE.HDF5_DATA) {
      IHDF5Reader reader = null;
      try {
        reader = SGApplicationUtility.openHDF5(path);
      } catch (HDF5Exception e1) {
        return false;
      }
      SGHDF5File hdf5File = new SGHDF5File(reader);
      try {
        if (this.setupMDArrayDataSetupDialog(hdf5File, dataType, infoMap, next, null, showDefault)
            == false) {
          return false;
        }
      } finally {
        reader.close();
      }
    } else if (fileType == FILE_TYPE.MATLAB_DATA) {
      MatFileReader reader = null;
      try {
        reader = SGApplicationUtility.openMAT(path);
      } catch (IOException e) {
        return false;
      }
      SGMATLABFile matFile = new SGMATLABFile(path, reader);
      if (this.setupMDArrayDataSetupDialog(matFile, dataType, infoMap, next, null, showDefault)
          == false) {
        return false;
      }
    } else if (fileType == FILE_TYPE.VIRTUAL_DATA) {
      SGDataColumnInfoSet colInfoSet = null;
      if (this.mMain.mVirtualMDArrayData.colInfoSet != null) {
        colInfoSet = (SGDataColumnInfoSet) this.mMain.mVirtualMDArrayData.colInfoSet.clone();
      }

      // puts information for grid plot / scatter plot
      if (this.mMain.mVirtualMDArrayData.buffer != null) {
        final String key = this.mMain.mVirtualMDArrayData.buffer.getGridTypeKey();
        if (key != null) {
          infoMap.put(key, this.mMain.mVirtualMDArrayData.buffer.isGridType());
        }
      }

      if (this.setupMDArrayDataSetupDialog(
              this.mMain.mVirtualMDArrayData.file, dataType, infoMap, next, colInfoSet, showDefault)
          == false) {
        return false;
      }
    } else {
      throw new Error("Invalid file type: " + fileType);
    }

    // show the dialog to select data columns
    next.setCenter(wnd);
    next.setVisible(true);

    return true;
  }

  /**
   * Sets up the the dialog to setup the netCDF data.
   *
   * @param ncFile a netCDF file
   * @param dataType the type of data
   * @param dg the dialog to setup netCDF data
   * @return true if succeeded
   */
  private boolean setupNetCDFDataSetupDialog(
      SGNetCDFFile ncFile,
      String dataType,
      Map<String, Object> infoMap,
      SGNetCDFDataSetupWizardDialog dg) {

    Window wnd = dg.getOwner();

    // create column info
    List<SGNetCDFVariable> varList = ncFile.getVariables();
    final int size = varList.size();
    SGNetCDFDataColumnInfo[] cols = new SGNetCDFDataColumnInfo[size];
    int cnt = 0;
    for (SGNetCDFVariable var : varList) {
      cols[cnt] = new SGNetCDFDataColumnInfo(var, var.getName(), var.getValueType());
      cnt++;
    }
    SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(cols);

    // set the data
    if (dg.setData(ncFile, dataType, colInfoSet, infoMap, true) == false) {
      SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
      return false;
    }

    // pack the dialog
    dg.pack();

    return true;
  }

  /**
   * Sets up the the dialog to setup the multidimensional data.
   *
   * @param mdFile a multidimensional data file
   * @param dataType the type of data
   * @param dg the dialog to setup multidimensional data
   * @return true if succeeded
   */
  private boolean setupMDArrayDataSetupDialog(
      SGMDArrayFile mdFile,
      String dataType,
      Map<String, Object> infoMap,
      SGMDArrayDataSetupWizardDialog dg,
      SGDataColumnInfoSet colInfoSet,
      final boolean showDefault) {

    Window wnd = dg.getOwner();

    // create column info

    SGDataColumnInfoSet colInfoSetNew = null;
    if (colInfoSet != null) {
      colInfoSetNew = (SGDataColumnInfoSet) colInfoSet.clone();
    } else {
      SGMDArrayVariable[] vars = mdFile.getVariables();
      SGMDArrayDataColumnInfo[] cols = new SGMDArrayDataColumnInfo[vars.length];
      for (int ii = 0; ii < cols.length; ii++) {
        SGMDArrayVariable var = vars[ii];
        cols[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
      }
      colInfoSetNew = new SGDataColumnInfoSet(cols);
    }

    // set the data
    if (dg.setData(mdFile, dataType, colInfoSetNew, infoMap, showDefault) == false) {
      SGUtility.showErrorMessageDialog(wnd, MSG_INVALID_DATA_FILE, SGIConstants.TITLE_ERROR);
      return false;
    }

    // pack the dialog
    dg.pack();

    return true;
  }
}
