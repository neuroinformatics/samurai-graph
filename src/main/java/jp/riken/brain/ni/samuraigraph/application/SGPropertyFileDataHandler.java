package jp.riken.brain.ni.samuraigraph.application;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.jmatio.io.MatFileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.FileColumn;
import jp.riken.brain.ni.samuraigraph.application.SGDataCreator.SDArrayFileParseResult;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.DataSourceInfo;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.FigureData;
import jp.riken.brain.ni.samuraigraph.application.SGMainFunctions.WrappedData;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfoSet;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElement;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementLegend;
import jp.riken.brain.ni.samuraigraph.base.SGIProgressControl;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeUtility.DefaultMDColumnTypeResult;
import jp.riken.brain.ni.samuraigraph.data.SGHDF5File;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMATLABFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFFile;
import jp.riken.brain.ni.samuraigraph.data.SGNetCDFVariable;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYMDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYNetCDFMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayMultipleData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYMDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYNetCDFData;
import jp.riken.brain.ni.samuraigraph.data.SGVXYSDArrayData;
import jp.riken.brain.ni.samuraigraph.figure.SGIFigureTypeConstants;
import jp.riken.brain.ni.samuraigraph.figure.SGXYFigure;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ucar.nc2.NetcdfFile;
import ucar.nc2.NetcdfFiles;

/** Creates data objects and figures from property file information. */
final class SGPropertyFileDataHandler {

  private final SGDataCreator mDataCreator;

  private final SGFigureCreator mFigureCreator;

  SGPropertyFileDataHandler(SGDataCreator dataCreator, SGFigureCreator figureCreator) {
    this.mDataCreator = dataCreator;
    this.mFigureCreator = figureCreator;
  }

  /**
   * Returns default data column information.
   *
   * @param pathName the path of data file
   * @param dataType the data type
   * @param infoMap the information map
   * @param isPropertyFileData true if the data is for the property file
   * @param versionNumber the version number of the property file
   * @return default the data column information
   */
  SGDataColumnInfoSet getSDArrayDefaultDataColumnInfo(
      String pathName,
      String dataType,
      Map<String, Object> infoMap,
      final boolean isPropertyFileData,
      final String versionNumber) {

    // parse the data file
    FileColumn[] fileColInfo = null;
    int length = -1;
    try {
      SDArrayFileParseResult result =
          this.mDataCreator.parseFileColumnType(
              pathName, dataType, isPropertyFileData, versionNumber);
      if (result == null) {
        return null;
      }
      fileColInfo = result.fileColumns;
      length = result.length;
    } catch (FileNotFoundException e1) {
      return null;
    }

    if (fileColInfo == null) {
      return null;
    }

    // get column information: title and value type
    SGDataColumnInfo[] colInfoArray = new SGDataColumnInfo[fileColInfo.length];
    for (int ii = 0; ii < fileColInfo.length; ii++) {
      colInfoArray[ii] =
          new SGSDArrayDataColumnInfo(fileColInfo[ii].title, fileColInfo[ii].valueType, length);
    }

    return this.createColumnInfoSet(dataType, infoMap, colInfoArray);
  }

  /**
   * Returns default data column information.
   *
   * @param file the data source
   * @param dataType the data type
   * @param infoMap the information map
   * @param isPropertyFileData true if the data is for the property file
   * @param versionNumber the version number of the property file
   * @return default the data column information
   */
  SGDataColumnInfoSet getDefaultDataColumnInfo(
      SGSDArrayFile file, String dataType, Map<String, Object> infoMap) {

    SGDataColumn[] columns = file.getDataColumns();
    SGDataColumnInfo[] colInfoArray = new SGDataColumnInfo[columns.length];
    for (int ii = 0; ii < colInfoArray.length; ii++) {
      colInfoArray[ii] =
          new SGSDArrayDataColumnInfo(
              columns[ii].getTitle(), columns[ii].getValueType(), columns[ii].getLength());
    }
    return this.createColumnInfoSet(dataType, infoMap, colInfoArray);
  }

  /**
   * Returns an array of the data column information.
   *
   * @param ncFile netCDF file
   * @param dataType the data type
   * @param infoMap the information map
   * @return an array of the data column information
   */
  SGDataColumnInfoSet getNetCDFDefaultDataColumnInfo(
      SGNetCDFFile ncFile, String dataType, Map<String, Object> infoMap) {
    List<SGNetCDFVariable> varList = ncFile.getVariables();
    SGNetCDFDataColumnInfo[] colArray = SGDataFileUtility.getNetCDFDataColumnInfo(varList, infoMap);
    return this.createColumnInfoSet(dataType, infoMap, colArray);
  }

  SGDataColumnInfoSet getMDArrayDataDefaultDataColumnInfo(
      SGMDArrayFile mdFile, String dataType, Map<String, Object> infoMap) {
    SGMDArrayVariable[] vars = mdFile.getVariables();
    SGMDArrayDataColumnInfo[] colArray =
        SGDataFileUtility.getMDArrayDataColumnInfo(mdFile, vars, infoMap);
    if (colArray == null) {
      return null;
    }
    return this.createColumnInfoSet(dataType, infoMap, colArray);
  }

  /**
   * @param elFigure
   * @param figure
   * @return
   */
  private int createSingleFigureFromPropertyFile(
      final Element elFigure,
      final SGDrawingWindow wnd,
      final ArrayList<WrappedData> dataList,
      final boolean readDataProperty,
      final String versionNumber,
      final int mode) {

    final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;

    String str = null;

    str = elFigure.getAttribute(SGFigure.KEY_FIGURE_TYPE);
    if (str.length() == 0) {
      return ic;
    }

    SGFigure figure = null;
    if (SGIFigureTypeConstants.FIGURE_TYPE_SXY.equals(str)
        || SGIFigureTypeConstants.FIGURE_TYPE_VXY.equals(str)
        || SGIFigureTypeConstants.FIGURE_TYPE_XY.equals(str)) {
      figure = new SGXYFigure(wnd);
      figure.setClassType(str);
    } else {
      return ic;
    }

    //
    // create a figure
    //

    final int figureID = wnd.assignFigureId();
    figure.setID(figureID);

    //
    // create SGIFigureElement objects
    //

    if (figure.readProperty(elFigure) == false) {
      return ic;
    }

    if (this.mFigureCreator.createFigureElementFromPropertyFile(figure, elFigure, versionNumber)
        == ic) {
      return ic;
    }

    // create data objects
    final int ret =
        this.createDataObjectsFromPropertyFile(
            elFigure, figure, dataList, wnd, readDataProperty, versionNumber, mode);
    if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
      return ret;
    }

    // called after data objects are created
    figure.setDataAnchored(figure.isDataAnchored());

    // add the figure to the window
    wnd.addFigure(figure);

    // initialize properties of the figure and figure elements
    figure.initPropertiesHistory();

    return SGIConstants.SUCCESSFUL_COMPLETION;
  }

  private int createDataObjectsFromPropertyFile(
      final Element elFigure,
      final SGFigure figure,
      final ArrayList<WrappedData> dataList,
      final SGIProgressControl progress,
      final boolean readDataProperty,
      final String versionNumber,
      final int mode) {

    final int ic = SGIConstants.PROPERTY_FILE_INCORRECT;
    final int di = SGIConstants.DATA_FILE_INVALID;

    NodeList nList = elFigure.getElementsByTagName(SGIFigureElementGraph.TAG_NAME_DATA);
    final int len = nList.getLength();
    if (len != dataList.size()) {
      return ic;
    }

    int[] indexArray = new int[len];
    SGData[] dataArray = new SGData[len];
    boolean indexValid = true;
    for (int ii = 0; ii < len; ii++) {

      //
      // get information from the Element object
      //

      Node node = nList.item(ii);
      if ((node instanceof Element) == false) {
        continue;
      }
      Element elData = (Element) node;

      // data type
      String dataType = elData.getAttribute(SGIFigureElement.KEY_DATA_TYPE);
      if (dataType == null) {
        return ic;
      }

      if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
        // replaces the data type
        // because sampling SXY-data and date SXY-date is to be eliminated at some stage.
        dataType = SGApplicationUtility.getArrayDataType(dataType);
      }

      Class<?> dataClass = null;
      if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
        if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
          dataClass = SGSXYSDArrayMultipleData.class;
        } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
          dataClass = SGVXYSDArrayData.class;
        } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
          dataClass = SGSXYZSDArrayData.class;
        }
      } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
        if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
          dataClass = SGSXYNetCDFMultipleData.class;
        } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
          dataClass = SGVXYNetCDFData.class;
        } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
          dataClass = SGSXYZNetCDFData.class;
        }
      } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
        if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
          dataClass = SGSXYMDArrayMultipleData.class;
        } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
          dataClass = SGVXYMDArrayData.class;
        } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
          dataClass = SGSXYZMDArrayData.class;
        }
      }
      if (dataClass == null) {
        return ic;
      }

      // create a new SGData object
      SGData data = SGApplicationUtility.createDataInstance(dataClass);
      if (data == null) {
        return ic;
      }

      // set the class type for backward compatibility between 1.0.7
      if (SGIFigureTypeConstants.FIGURE_TYPE_XY.equals(figure.getClassType())) {
        String type = null;
        if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
          type = SGIFigureTypeConstants.FIGURE_TYPE_VXY;
        } else {
          type = SGIFigureTypeConstants.FIGURE_TYPE_SXY;
        }
        figure.setClassType(type);
      }

      // create information map
      Map<String, Object> infoMap = SGDataInfoMapUtility.createInfoMap(dataType, elData);

      // get the data file
      if (dataList.size() == 0) {
        return SGIConstants.DATA_NUMBER_SHORTAGE;
      }

      // get an object from dataList
      WrappedData wData = dataList.get(ii);
      if (!wData.hasFigureData()) {
        SGPropertyFileData pfData = wData.getPropertyFileData();
        String fileName = pfData.getFileName();

        try {
          // get the data column information
          SGDataColumnInfoSet colInfoSet = pfData.getColumnInfoSet();
          if (colInfoSet == null) {
            if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
              // get default column information
              colInfoSet =
                  this.getSDArrayDefaultDataColumnInfo(
                      fileName, dataType, infoMap, true, versionNumber);
            } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
              NetcdfFile ncFile = null;
              try {
                if (NetcdfFiles.canOpen(fileName) == false) {
                  return di;
                }
                ncFile = SGApplicationUtility.openNetCDF(fileName);
                if (ncFile == null) {
                  return di;
                }
                try {
                  SGNetCDFFile nc = new SGNetCDFFile(ncFile);
                  infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, nc);
                  colInfoSet = this.getNetCDFDefaultDataColumnInfo(nc, dataType, infoMap);
                } finally {
                  try {
                    ncFile.close();
                  } catch (IOException e) {
                    return di;
                  }
                }
              } catch (Exception e) {
                return di;
              }
            } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
              SGMDArrayFile mdFile = null;
              IHDF5Reader hdf5Reader = null;
              MatFileReader matReader = null;
              try {
                if (SGDataDataTypeUtility.isHDF5FileData(dataType)) {
                  hdf5Reader = SGApplicationUtility.openHDF5(fileName);
                  mdFile = new SGHDF5File(hdf5Reader);
                } else if (SGDataDataTypeUtility.isMATLABData(dataType)) {
                  matReader = SGApplicationUtility.openMAT(fileName);
                  mdFile = new SGMATLABFile(fileName, matReader);
                } else {
                  return di;
                }
                infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_SOURCE, mdFile);
                colInfoSet = this.getMDArrayDataDefaultDataColumnInfo(mdFile, dataType, infoMap);
              } catch (Exception e) {
                return di;
              } finally {
                if (hdf5Reader != null) {
                  hdf5Reader.close();
                }
              }
            } else {
              return di;
            }
            if (colInfoSet == null) {
              return di;
            }
          }

          Map<String, Object> pfInfoMap = pfData.getInfoMap();
          if (pfInfoMap.size() == 0) {
            // for the case of Samurai Graph Archive (.sga) file
            pfInfoMap = new HashMap<String, Object>(infoMap);
          }

          // data type
          String dataTypeNew = (String) pfInfoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
          if (dataTypeNew == null) {
            return di;
          }
          infoMap.put(SGIDataInformationKeyConstants.KEY_DATA_TYPE, dataTypeNew);

          // updates the information map
          final int ret = SGApplicationUtility.updateInformationMap(colInfoSet, infoMap, pfInfoMap);
          if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
            return ret;
          }

          // create data object
          DataSourceInfo dataSource = new DataSourceInfo(fileName);
          SGDataCreator.CreatedDataSet cdSet =
              this.mDataCreator.create(
                  dataSource, colInfoSet, infoMap, progress, versionNumber, mode);
          if (cdSet == null) {
            return di;
          }
          if (cdSet.getDataLength() == 0) {
            return di;
          }
          SGDataCreator.CreatedData cd = cdSet.getData(0);
          SGData firstData = cd.getData();
          try {
            if (data.setData(firstData) == false) {
              return di;
            }
          } finally {
            // disposes data
            firstData.dispose();
          }

        } catch (FileNotFoundException ex) {
          return SGIConstants.FILE_OPEN_FAILURE;
        }

      } else if (wData.hasFigureData()) {
        if (data.setData(wData.getFigureData().getData()) == false) {
          return ic;
        }
      } else {
        return ic;
      }

      // get the index in the legend
      String str = elData.getAttribute(SGIFigureElement.KEY_INDEX_IN_LEGEND);
      if (str.length() != 0) {
        Number num = SGUtilityText.getInteger(str);
        if (num == null) {
          return ic;
        }
        indexArray[ii] = num.intValue();
        dataArray[ii] = data;
      } else {
        indexValid = false;
      }

      // create data objects
      if (figure.createDataObjectFromPropertyFile(elData, data, readDataProperty) == false) {
        return ic;
      }
    }

    // sort the order of data objects in legend
    if (indexValid) {
      SGIFigureElementLegend lElement = figure.getLegendElement();
      lElement.sortLegend(dataArray, indexArray);
    }

    SGIFigureElement[] array = figure.getIFigureElementArray();
    for (int ii = 0; ii < array.length; ii++) {
      array[ii].initPropertiesHistory();
    }

    return SGIConstants.SUCCESSFUL_COMPLETION;
  }

  /**
   * @param elWnd
   * @param wnd
   * @return
   */
  int createFiguresFromPropertyFile(
      final Element elWnd,
      final SGDrawingWindow wnd,
      final WrappedData[] wDataArray,
      final boolean readDataProperty,
      final String versionNumber,
      final int mode) {

    NodeList nList = elWnd.getElementsByTagName(SGFigure.TAG_NAME_FIGURE);
    final int len = nList.getLength();

    for (int ii = 0; ii < len; ii++) {
      Node node = nList.item(ii);
      if (node instanceof Element) {
        Element el = (Element) node;
        ArrayList<WrappedData> dataList = new ArrayList<WrappedData>();
        for (int jj = 0; jj < wDataArray.length; jj++) {
          if (wDataArray[jj].hasFigureData()) {
            FigureData fd = wDataArray[jj].getFigureData();
            if (fd.getSerialFigureNo() == ii) {
              dataList.add(wDataArray[jj]);
            }
          } else {
            SGPropertyFileData pfData = wDataArray[jj].getPropertyFileData();
            if (pfData.getFigureId() == ii + 1) {
              String fileName = pfData.getFileName();
              if (fileName != null
                  && !SGPropertyDataFileChooserWizardDialog.NO_DATA.equals(fileName)) {
                dataList.add(wDataArray[jj]);
              }
            }
          }
        }
        final int ret =
            this.createSingleFigureFromPropertyFile(
                el, wnd, dataList, readDataProperty, versionNumber, mode);
        if (ret != SGIConstants.SUCCESSFUL_COMPLETION) {
          return ret;
        }
      }
    }

    return SGIConstants.SUCCESSFUL_COMPLETION;
  }

  /**
   * @param dataType
   * @param infoMap
   * @param colInfoArray
   * @return Set of column information. Return null if failed to get default column types.
   */
  SGDataColumnInfoSet createColumnInfoSet(
      String dataType, Map<String, Object> infoMap, SGDataColumnInfo[] colInfoArray) {

    SGDataColumnInfo[] aColInfoArray =
        SGApplicationUtility.getAdditionalInfoArray(dataType, infoMap, colInfoArray);

    SGDataColumnInfo[] allInfoArray =
        new SGDataColumnInfo[colInfoArray.length + aColInfoArray.length];
    for (int ii = 0; ii < colInfoArray.length; ii++) {
      allInfoArray[ii] = colInfoArray[ii];
    }
    for (int ii = 0; ii < aColInfoArray.length; ii++) {
      allInfoArray[ii + colInfoArray.length] = aColInfoArray[ii];
    }

    List<SGDataColumnInfo> columnInfoList =
        new ArrayList<SGDataColumnInfo>(Arrays.asList(allInfoArray));

    // get default column types and set the each column information
    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(dataType, columnInfoList, infoMap);
    if (result.isSucceeded() == false) {
      return null;
    }

    String[] columnTypes = result.getDefaultColumnTypes();
    for (int ii = 0; ii < columnTypes.length; ii++) {
      allInfoArray[ii].setColumnType(columnTypes[ii]);
    }
    if (result instanceof DefaultMDColumnTypeResult) {
      // for multidimensional result, sets the dimension index
      DefaultMDColumnTypeResult dResult = (DefaultMDColumnTypeResult) result;
      //        	int[] indices = dResult.getIndices();
      List<Map<String, Integer>> indices = dResult.getAllIndices();
      for (int ii = 0; ii < columnTypes.length; ii++) {
        SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) allInfoArray[ii];
        //            	mdInfo.setDefaultDimensionIndex(indices[ii]);
        mdInfo.putAllDimensionIndices(indices.get(ii));
      }
    }

    SGDataColumnInfoSet colInfoSet = new SGDataColumnInfoSet(allInfoArray);
    return colInfoSet;
  }
}
