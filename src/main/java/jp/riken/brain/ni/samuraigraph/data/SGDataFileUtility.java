package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5DataClass;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5DataTypeInformation;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.HDF5EnumerationValue;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Reader;
import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.NamedNodeMap;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

/** Static helper for the File responsibility. */
public final class SGDataFileUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private static final Logger logger = LogManager.getLogger(SGDataFileUtility.class);

  private SGDataFileUtility() {}

  static SGMDArrayDataColumnInfo createDataColumnInfo(
      SGMDArrayVariable var, String columnTypeHeader, String holderName) {
    if (var == null) {
      return null;
    }
    SGMDArrayDataColumnInfo info = new SGMDArrayDataColumnInfo(var, null, var.getValueType());
    StringBuilder sb = new StringBuilder();
    sb.append(columnTypeHeader);
    sb.append(MID_COLUMN);
    sb.append(holderName);
    info.setColumnType(sb.toString());
    return info;
  }

  static SGMDArrayDataColumnInfo createErrorBarInfo(
      SGMDArrayVariable var,
      String nameStr,
      SGMDArrayVariable leVar,
      SGMDArrayVariable ueVar,
      SGMDArrayVariable ehVar) {
    String name;
    if (leVar.equals(ueVar)) {
      name = LOWER_UPPER_ERROR_VALUE;
    } else {
      name = nameStr;
    }
    String ehName = ehVar.getName();
    return SGDataFileUtility.createDataColumnInfo(var, name, ehName);
  }

  static SGMDArrayDataColumnInfo[] createDataColumnInfoArray(
      SGMDArrayVariable[] vars, String[] columnTypes) {
    if (vars == null) {
      return null;
    }
    SGMDArrayDataColumnInfo[] infoArray = new SGMDArrayDataColumnInfo[vars.length];
    for (int ii = 0; ii < infoArray.length; ii++) {
      infoArray[ii] = createDataColumnInfo(vars[ii], columnTypes[ii]);
    }
    return infoArray;
  }

  static SGMDArrayDataColumnInfo[] createDataColumnInfoArray(
      SGMDArrayVariable[] vars, String columnType) {
    if (vars == null) {
      return null;
    }
    SGMDArrayDataColumnInfo[] infoArray = new SGMDArrayDataColumnInfo[vars.length];
    for (int ii = 0; ii < infoArray.length; ii++) {
      infoArray[ii] = createDataColumnInfo(vars[ii], columnType);
    }
    return infoArray;
  }

  static SGMDArrayDataColumnInfo[] createDataColumnInfoArray(
      SGMDArrayVariable var, String columnType) {
    SGMDArrayDataColumnInfo info = createDataColumnInfo(var, columnType);
    if (info == null) {
      return new SGMDArrayDataColumnInfo[0];
    }
    return new SGMDArrayDataColumnInfo[] {info};
  }

  static SGMDArrayDataColumnInfo createDataColumnInfo(SGMDArrayVariable var, String columnType) {
    if (var == null) {
      return null;
    }
    SGMDArrayDataColumnInfo mdInfo = new SGMDArrayDataColumnInfo(var, null, var.getValueType());
    mdInfo.setColumnType(columnType);
    return mdInfo;
  }

  static SGNetCDFDataColumnInfo createErrorBarInfo(
      SGNetCDFVariable var,
      String nameStr,
      SGNetCDFVariable leVar,
      SGNetCDFVariable ueVar,
      SGNetCDFVariable ehVar) {
    String name;
    if (leVar.equals(ueVar)) {
      name = LOWER_UPPER_ERROR_VALUE;
    } else {
      name = nameStr;
    }
    String ehName = ehVar.getName();
    return SGDataFileUtility.createDataColumnInfo(var, name, ehName);
  }

  static SGNetCDFDataColumnInfo[] createDataColumnInfoArray(
      SGNetCDFVariable[] vars, String[] columnTypes) {
    if (vars == null) {
      return null;
    }
    SGNetCDFDataColumnInfo[] infoArray = new SGNetCDFDataColumnInfo[vars.length];
    for (int ii = 0; ii < infoArray.length; ii++) {
      infoArray[ii] = createDataColumnInfo(vars[ii], columnTypes[ii]);
    }
    return infoArray;
  }

  static SGNetCDFDataColumnInfo[] createDataColumnInfoArray(
      SGNetCDFVariable[] vars, String columnType) {
    if (vars == null) {
      return null;
    }
    SGNetCDFDataColumnInfo[] infoArray = new SGNetCDFDataColumnInfo[vars.length];
    for (int ii = 0; ii < infoArray.length; ii++) {
      infoArray[ii] = createDataColumnInfo(vars[ii], columnType);
    }
    return infoArray;
  }

  static SGNetCDFDataColumnInfo[] createDataColumnInfoArray(
      SGNetCDFVariable var, String columnType) {
    SGNetCDFDataColumnInfo info = createDataColumnInfo(var, columnType);
    return new SGNetCDFDataColumnInfo[] {info};
  }

  static SGNetCDFDataColumnInfo createDataColumnInfo(
      SGNetCDFVariable var, String columnTypeHeader, String holderName) {
    if (var == null) {
      return null;
    }
    SGNetCDFDataColumnInfo info = new SGNetCDFDataColumnInfo(var, null, var.getValueType());
    StringBuilder sb = new StringBuilder();
    sb.append(columnTypeHeader);
    sb.append(MID_COLUMN);
    sb.append(holderName);
    info.setColumnType(sb.toString());
    return info;
  }

  static SGNetCDFDataColumnInfo createDataColumnInfo(SGNetCDFVariable var, String columnType) {
    if (var == null) {
      return null;
    }
    SGNetCDFDataColumnInfo info = new SGNetCDFDataColumnInfo(var, null, var.getValueType());
    info.setColumnType(columnType);
    return info;
  }

  /**
   * Writes attributes to the writer.
   *
   * @param writer the writer
   * @param reader the reader
   * @param path the path
   * @param attrNameList the list of the name of attributes
   * @return true if succeeded
   */
  public static boolean writeHDF5Attribute(
      final IHDF5Writer writer,
      final IHDF5Reader reader,
      final String path,
      final List<String> attrNameList) {
    for (String attrName : attrNameList) {
      HDF5DataTypeInformation attrInfo = reader.object().getAttributeInformation(path, attrName);
      final boolean arrayType = (attrInfo.getNumberOfElements() > 1);
      HDF5DataClass attrDataClass = attrInfo.getDataClass();
      if (HDF5DataClass.STRING.equals(attrDataClass)) {
        if (arrayType) {
          final String[] value = reader.string().getArrayAttr(path, attrName);
          writer.string().setArrayAttr(path, attrName, value);
        } else {
          final String value = reader.string().getAttr(path, attrName);
          writer.string().setAttr(path, attrName, value);
        }
      } else if (HDF5DataClass.INTEGER.equals(attrDataClass)) {
        if (arrayType) {
          final int[] value = reader.int32().getArrayAttr(path, attrName);
          writer.int32().setArrayAttr(path, attrName, value);
        } else {
          final int value = reader.int32().getAttr(path, attrName);
          writer.int32().setAttr(path, attrName, value);
        }
      } else if (HDF5DataClass.FLOAT.equals(attrDataClass)) {
        if (arrayType) {
          final float[] value = reader.float32().getArrayAttr(path, attrName);
          writer.float32().setArrayAttr(path, attrName, value);
        } else {
          final float value = reader.float32().getAttr(path, attrName);
          writer.float32().setAttr(path, attrName, value);
        }
      } else if (HDF5DataClass.BOOLEAN.equals(attrDataClass)) {
        try {
          final boolean value = reader.bool().getAttr(path, attrName);
          writer.bool().setAttr(path, attrName, value);
        } catch (Exception e) {
          logger.warn("Error in data utility operation", e);
          return false;
        }
      } else if (HDF5DataClass.ENUM.equals(attrDataClass)) {
        try {
          HDF5EnumerationValue value = reader.enumeration().getAttr(path, attrName);
          writer.enumeration().setAttr(path, attrName, value);
        } catch (Exception e) {
          logger.warn("Error in data utility operation", e);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Finds and returns the list of attributes.
   *
   * @param reader reader of HDF5
   * @param path the path
   * @param attrNameList the list of the name of attributes
   * @return the list of attributes
   */
  public static List<SGAttribute> findHDF5Attributes(
      final IHDF5Reader reader, final String path, final List<String> attrNameList) {
    List<SGAttribute> aList = new ArrayList<SGAttribute>();
    for (String name : attrNameList) {
      HDF5DataTypeInformation info = reader.object().getAttributeInformation(path, name);
      HDF5DataClass dClass = info.getDataClass();
      final boolean arrayType = (info.getNumberOfElements() > 1);
      Object obj = null;
      if (HDF5DataClass.STRING.equals(dClass)) {
        if (arrayType) {
          obj = reader.string().getArrayAttr(path, name);
        } else {
          obj = reader.string().getAttr(path, name);
        }
      } else if (HDF5DataClass.INTEGER.equals(dClass)) {
        if (arrayType) {
          obj = reader.int32().getArrayAttr(path, name);
        } else {
          obj = reader.int32().getAttr(path, name);
        }
      } else if (HDF5DataClass.FLOAT.equals(dClass)) {
        if (arrayType) {
          obj = reader.float32().getArrayAttr(path, name);
        } else {
          obj = reader.float32().getAttr(path, name);
        }
      } else if (HDF5DataClass.BOOLEAN.equals(dClass)) {
        try {
          obj = reader.bool().getAttr(path, name);
        } catch (Exception e) {
          logger.warn("Error in data utility operation", e);
        }
      } else if (HDF5DataClass.ENUM.equals(dClass)) {
        try {
          obj = reader.enumeration().getAttr(path, name);
        } catch (Exception e) {
          logger.warn("Error in data utility operation", e);
        }
      }
      SGAttribute attr = new SGAttribute(name, obj);
      aList.add(attr);
    }
    return aList;
  }

  public static SGMDArrayDataColumnInfo[] getMDArrayDataColumnInfo(
      SGMDArrayFile mdFile, SGMDArrayVariable[] vars, Map<String, Object> infoMap) {

    // get the origin map
    NamedNodeMap nodeMap = (NamedNodeMap) infoMap.get(SGIFigureElementGraph.KEY_NODE_MAP);
    Map<String, int[]> originMap = null;
    if (nodeMap != null) {
      originMap = SGDefaultColumnTypeUtility.getMDArrayDataOriginMap(nodeMap);
      if (originMap == null) {
        return null;
      }
    }

    // get the group name
    String groupName = (String) infoMap.get(SGIFigureElementGraph.KEY_GROUP_NAME);
    if (groupName != null && originMap != null) {
      // replaces the origin map
      Map<String, int[]> tempMap = new HashMap<String, int[]>();
      Set<Entry<String, int[]>> entrySet = originMap.entrySet();
      for (Entry<String, int[]> e : entrySet) {
        String varName = e.getKey();
        int[] value = e.getValue();
        String key = SGDataTextUtility.appendGroupName(varName, groupName);
        tempMap.put(key, value);
      }
      originMap = tempMap;
    }

    // get column information: title and value type
    SGMDArrayDataColumnInfo[] colArray = new SGMDArrayDataColumnInfo[vars.length];
    for (int ii = 0; ii < colArray.length; ii++) {
      SGMDArrayVariable var = vars[ii];
      if (originMap != null) {
        int[] origins = originMap.get(var.getName());
        if (origins != null) {
          var.setOrigins(origins);
        }
      }
      colArray[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
    }

    /*
     * Object pickUpDimensionName = infoMap.get(SGIDataInformationKeyConstants.
     * KEY_SXY_MDARRAY_PICKUP_DATASET_NAME);
     * if (pickUpDimensionName != null) {
     * SGMDArrayDataColumnInfo pickUpCol = (SGMDArrayDataColumnInfo)
     * SGDataColumnInfoUtility.findColumnWithName(
     * colArray, pickUpDimensionName.toString());
     * if (pickUpCol != null) {
     * Map<String, Integer> dimensionIndexMap = (Map<String, Integer>) infoMap.get(
     * SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP);
     * for (int ii = 0; ii < colArray.length; ii++) {
     * SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
     * Integer dimensionIndex = dimensionIndexMap.get(mdCol.getName());
     * if (dimensionIndex != null) {
     * final int[] dims = mdCol.getDimensions();
     * final int generic = mdCol.getGenericDimensionIndex();
     * if (0 < dimensionIndex && dimensionIndex < dims.length
     * && !dimensionIndex.equals(generic)) {
     * mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION,
     * dimensionIndex);
     * }
     * }
     * }
     * }
     * }
     */

    Map<?, ?> timeDimensionMap =
        (infoMap.get(SGIDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP)
                instanceof Map<?, ?> m1)
            ? m1
            : null;
    Map<?, ?> pickupDimensionMap =
        (infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP)
                instanceof Map<?, ?> m2)
            ? m2
            : null;
    for (int ii = 0; ii < colArray.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
      final int generic = mdCol.getGenericDimensionIndex();
      final int[] dims = mdCol.getDimensions();
      boolean tValid = false;
      Integer timeDimension = null;
      if (timeDimensionMap != null) {
        Object td = timeDimensionMap.get(mdCol.getName());
        if (td instanceof Integer) {
          timeDimension = (Integer) td;
        }
        tValid = (timeDimension != null && timeDimension != -1);
        if (tValid && timeDimension != null) {
          if (timeDimension < 0 || timeDimension >= dims.length) {
            return null;
          }
          if (timeDimension.equals(generic)) {
            return null;
          }
          mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_TIME_DIMENSION, timeDimension);
        }
      }
      if (pickupDimensionMap != null) {
        Object pd = pickupDimensionMap.get(mdCol.getName());
        Integer pickupDimension = (pd instanceof Integer) ? (Integer) pd : null;
        final boolean pValid = (pickupDimension != null && pickupDimension != -1);
        if (pValid && pickupDimension != null) {
          if (pickupDimension < 0 || pickupDimension >= dims.length) {
            return null;
          }
          if (pickupDimension.equals(generic)) {
            return null;
          }
          if (tValid) {
            if (pickupDimension.equals(timeDimension)) {
              return null;
            }
          }
          mdCol.setDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION, pickupDimension);
        }
      }
    }

    return colArray;
  }

  public static SGNetCDFDataColumnInfo[] getNetCDFDataColumnInfo(
      List<SGNetCDFVariable> varList, Map<String, Object> infoMap) {

    // get the origin map
    NamedNodeMap nodeMap = (NamedNodeMap) infoMap.get(SGIFigureElementGraph.KEY_NODE_MAP);
    Map<String, Integer> originMap = null;
    if (nodeMap != null) {
      originMap = SGDefaultColumnTypeUtility.getNetCDFOriginMap(nodeMap);
      if (originMap == null) {
        return null;
      }
    }

    // get the group name
    String groupName = (String) infoMap.get(SGIFigureElementGraph.KEY_GROUP_NAME);
    if (groupName != null && originMap != null) {
      // replaces the origin map
      Map<String, Integer> tempMap = new HashMap<String, Integer>();
      Set<Entry<String, Integer>> entrySet = originMap.entrySet();
      for (Entry<String, Integer> e : entrySet) {
        String varName = e.getKey();
        Integer value = e.getValue();
        String key = SGDataTextUtility.appendGroupName(varName, groupName);
        tempMap.put(key, value);
      }
      originMap = tempMap;
    }

    // get column information: title and value type
    SGNetCDFDataColumnInfo[] colArray = new SGNetCDFDataColumnInfo[varList.size()];
    for (int ii = 0; ii < colArray.length; ii++) {
      SGNetCDFVariable var = varList.get(ii);

      // get the origin
      int origin = 0;
      if (originMap != null) {
        if (var.isCoordinateVariable()) {
          String varName = var.getName();
          Integer num = originMap.get(varName);
          if (num != null) {
            Dimension dim = var.getDimension(0);
            origin = num.intValue();
            if (origin < 0 || origin >= dim.getLength()) {
              origin = 0;
            }
          }
        }
      }

      colArray[ii] = new SGNetCDFDataColumnInfo(var, var.getName(), var.getValueType(), origin);
    }

    return colArray;
  }

  /**
   * Return whether given netCDF data can connect with its file.
   *
   * @param data
   * @return
   */
  public static boolean canOpenNetCDF(SGNetCDFData data) {
    try {
      boolean isNetCDFFile =
          NetcdfFiles.canOpen(data.getNetcdfFile().getNetcdfFile().getLocation());
      return isNetCDFFile;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Return netCDF attribute for given valueType.
   *
   * @param valueType
   * @return
   */
  public static Attribute getValueTypeAttribute(final String valueType) {
    Attribute attr = new Attribute(ATTRIBUTE_VALUE_TYPE, valueType);
    return attr;
  }

  /**
   * Return whether given netCDF variable is SGTextVariable.
   *
   * <p>True if data type of the variable is CHAR and it has an attribute "value_type=Text".
   *
   * @param var
   * @return true if var is SGTextVariable.
   */
  public static boolean isSGTextVariable(final Variable var) {
    if (DataType.CHAR.equals(var.getDataType())) {
      for (Attribute attr : var.attributes()) {
        if (attr.isString()) {
          String name = attr.getShortName();
          String value = attr.getStringValue();
          if (name != null
              && value != null
              && ATTRIBUTE_VALUE_TYPE.equals(name.trim())
              && SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(value.trim())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Return whether given netCDF variable is SGDateVariable.
   *
   * <p>True if data type of the variable is CHAR and it has an attribute "value_type=Date".
   *
   * @param var
   * @return true if a given variable is SGDateVariable.
   */
  public static boolean isSGDateVariable(final Variable var) {
    if (DataType.CHAR.equals(var.getDataType())) {
      for (Attribute attr : var.attributes()) {
        if (attr.isString()) {
          String name = attr.getShortName();
          String value = attr.getStringValue();
          if (ATTRIBUTE_VALUE_TYPE.equals(name.trim())
              && SGIDataColumnTypeConstants.VALUE_TYPE_DATE.equals(value.trim())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  public static boolean updatePickupParameters(
      final Map<String, Object> infoMap, SGMDArrayDataColumnInfo[] mdCols) {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet();
    SGMDArrayDataColumnInfo col = getMDArrayPickUpColumn(mdCols);
    final SGIntegerSeries series;
    if (col != null) {
      int[] dims = col.getDimensions();
      Integer pickUpDim = col.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
      if (pickUpDim == null || pickUpDim == -1) {
        return false;
      }
      final int len = dims[pickUpDim];
      series = createDefaultStepSeries(len);
    } else {
      series = new SGIntegerSeries(0);
    }
    indices.add(series);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
    return true;
  }

  /**
   * Updates pick up parameters for netCDF data.
   *
   * @param infoMap information map
   * @param nCols an array of columns
   * @return true if succeeded
   */
  public static boolean updatePickupParameters(
      final Map<String, Object> infoMap, SGNetCDFDataColumnInfo[] nCols) {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet();
    SGNetCDFDataColumnInfo col = getDimensionDataColumnInfo(nCols);
    final SGIntegerSeries series;
    if (col != null) {
      SGDimensionInfo dim = col.getDimension(0);
      final int len = dim.getLength();
      series = createDefaultStepSeries(len);
    } else {
      series = new SGIntegerSeries(0);
    }
    indices.add(series);
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, indices);
    return true;
  }

  private static SGNetCDFDataColumnInfo getDimensionDataColumnInfo(
      final SGNetCDFDataColumnInfo[] nCols) {
    SGNetCDFDataColumnInfo info = null;
    for (int i = 0; i < nCols.length; i++) {
      String colType = nCols[i].getColumnType();
      if (PICKUP.equalsIgnoreCase(colType)) {
        info = nCols[i];
        break;
      }
    }
    if (info != null && info.isCoordinateVariable()) {
      return info;
    } else {
      return null;
    }
  }

  public static SGNetCDFDataColumnInfo findHolderInfo(
      SGDataColumnInfo[] cols, SGNetCDFDataColumnInfo info) {
    String holderName = getHolderName(info);
    return (SGNetCDFDataColumnInfo) SGDataColumnInfoUtility.findColumnWithName(cols, holderName);
  }

  /**
   * Checks the data columns for the multidimensional array data.
   *
   * @param cols the data columns
   * @param dataType the data type
   * @param mdFile the multidimensional data
   * @param infoMap the information map
   * @param errmsgBuffer buffer for the error message
   * @return true if columns are valid
   */
  public static boolean checkMDArrayDataColumns(
      final SGDataColumnInfo[] cols,
      final String dataType,
      final SGMDArrayFile mdFile,
      final Map<String, Object> infoMap,
      StringBuilder errmsgBuffer) {

    if (isSXYTypeData(dataType)) {
      // picked up dimension
      Map<?, ?> dimensionIndexMap =
          (infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP)
                  instanceof Map<?, ?> m)
              ? m
              : null;
      SGMDArrayDataColumnInfo pickUpColumn = null;
      for (int ii = 0; ii < cols.length; ii++) {
        SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
        String columnType = mdCol.getColumnType();
        if (X_VALUE.equals(columnType) || Y_VALUE.equals(columnType)) {
          Integer dim = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
          if (SGDataDataTypeUtility.isValidDimensionIndex(dim)) {
            pickUpColumn = mdCol;
            break;
          }
        }
      }
      boolean pickUpAssigned = (pickUpColumn != null);
      int pickUpDimLen = 0;
      if (pickUpAssigned) {
        if (pickUpColumn == null) {
          throw new Error("pickUpColumn == null");
        }

        int[] dims = pickUpColumn.getDimensions();
        Object dimIndexObj =
            (dimensionIndexMap != null) ? dimensionIndexMap.get(pickUpColumn.getName()) : null;
        Integer dimIndex = (dimIndexObj instanceof Integer) ? (Integer) dimIndexObj : null;
        if (!isValidPickUpValue(dimIndex)) {
          throw new Error("Invalid dimension index: " + dimIndex);
        }
        if (dimIndex == null || dimIndex < 0 || dims.length < dimIndex) {
          throw new Error("Invalid dimension index: " + dimIndex);
        }
        pickUpDimLen = dims[dimIndex];

        SGIntegerSeriesSet indices =
            (SGIntegerSeriesSet) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
        if (indices == null) {
          errmsgBuffer.append(MSG_PROPER_PICK_UP_INDICES);
          return false;
        }
        int[] indexArray = indices.getNumbers();
        for (int ii = 0; ii < indexArray.length; ii++) {
          if (indexArray[ii] < 0) {
            errmsgBuffer.append(MSG_PROPER_PICK_UP_INDICES);
            return false;
          }
          if (pickUpDimLen <= indexArray[ii]) {
            errmsgBuffer.append(MSG_PROPER_PICK_UP_INDICES);
            return false;
          }
        }
      }

      // x and y values
      final List<SGDataColumnInfo> xColList = getColumnList(cols, X_VALUE);
      final List<SGDataColumnInfo> yColList = getColumnList(cols, Y_VALUE);
      if (xColList.size() == 0 && yColList.size() == 0) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      if (pickUpAssigned) {
        if (xColList.size() > 1) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
        if (yColList.size() > 1) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
      } else {
        if (!(xColList.size() == 0 || xColList.size() == 1)
            && !(yColList.size() == 0 || yColList.size() == 1)) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
      }

      // lower and upper error values
      final List<String> lNameList = new ArrayList<String>();
      final List<Integer> lIndexList = new ArrayList<Integer>();
      final List<String> uNameList = new ArrayList<String>();
      final List<Integer> uIndexList = new ArrayList<Integer>();
      if (getColumnNameAndAppendedNumberList(cols, LOWER_ERROR_VALUE, lNameList, lIndexList)
          == false) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      if (getColumnNameAndAppendedNumberList(cols, UPPER_ERROR_VALUE, uNameList, uIndexList)
          == false) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      if (lNameList.size() != uNameList.size()) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      final List<String> luNameList = new ArrayList<String>();
      final List<Integer> luIndexList = new ArrayList<Integer>();
      if (getColumnNameAndAppendedNumberList(cols, LOWER_UPPER_ERROR_VALUE, luNameList, luIndexList)
          == false) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      for (int ii = 0; ii < luNameList.size(); ii++) {
        // checks whether lower/upper indices is contained
        // in lower indices or upper indices
        Integer luIndex = luIndexList.get(ii);
        if (lIndexList.contains(luIndex)) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
        if (uIndexList.contains(luIndex)) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
      }
      final List<SGDataColumnInfo> lColList = getColumnListStartsWith(cols, LOWER_ERROR_VALUE);
      final List<SGDataColumnInfo> uColList = getColumnListStartsWith(cols, UPPER_ERROR_VALUE);
      final List<SGDataColumnInfo> luColList =
          getColumnListStartsWith(cols, LOWER_UPPER_ERROR_VALUE);
      if (pickUpAssigned) {
        if (lColList.size() > 1 || uColList.size() > 1 || luColList.size() > 1) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
        if (lColList.size() != uColList.size()) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
        if (lColList.size() + luColList.size() > 1) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
        if (lColList.size() == 1 && uColList.size() == 1) {
          SGMDArrayDataColumnInfo lCol = (SGMDArrayDataColumnInfo) lColList.get(0);
          Integer lIndex = lCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
          if (isValidPickUpValue(lIndex)) {
            final int[] lDims = lCol.getDimensions();
            if (lDims[lIndex] != pickUpDimLen) {
              errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
              return false;
            }
          }
          SGMDArrayDataColumnInfo uCol = (SGMDArrayDataColumnInfo) uColList.get(0);
          Integer uIndex = uCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
          if (isValidPickUpValue(uIndex)) {
            final int[] uDims = uCol.getDimensions();
            if (uDims[uIndex] != pickUpDimLen) {
              errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
              return false;
            }
          }
        } else if (luColList.size() == 1) {
          SGMDArrayDataColumnInfo luCol = (SGMDArrayDataColumnInfo) luColList.get(0);
          Integer lIndex = luCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
          if (isValidPickUpValue(lIndex)) {
            final int[] lDims = luCol.getDimensions();
            if (lDims[lIndex] != pickUpDimLen) {
              errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
              return false;
            }
          }
        }
      }

      // tick labels
      final List<SGDataColumnInfo> tColList = getColumnListStartsWith(cols, TICK_LABEL);
      if (pickUpAssigned) {
        if (tColList.size() > 1) {
          errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
          return false;
        }
        if (tColList.size() == 1) {
          SGMDArrayDataColumnInfo tCol = (SGMDArrayDataColumnInfo) tColList.get(0);
          Integer index = tCol.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
          if (isValidPickUpValue(index)) {
            final int[] dims = tCol.getDimensions();
            if (dims[index] != pickUpDimLen) {
              errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH_PICK_UP);
              return false;
            }
          }
        }
      }

      // check length of all variables
      List<SGDataColumnInfo> allColList = new ArrayList<SGDataColumnInfo>();
      allColList.addAll(xColList);
      allColList.addAll(yColList);
      allColList.addAll(lColList);
      allColList.addAll(uColList);
      allColList.addAll(luColList);
      allColList.addAll(tColList);
      int len = 0;
      for (int ii = 0; ii < allColList.size(); ii++) {
        SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) allColList.get(ii);
        if (ii == 0) {
          len = col.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
        } else {
          final int gLen = col.getGenericDimensionLength();
          if (gLen == -1) {
            return false;
          }
          if (gLen != len) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
      }

    } else if (isSXYZTypeData(dataType)) {

      final List<SGDataColumnInfo> zColList = getColumnList(cols, Z_VALUE);
      if (zColList.size() != 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
      final int[] zDims = zCol.getDimensions();

      final List<SGDataColumnInfo> xColList = getColumnList(cols, X_VALUE);
      if (xColList.size() > 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      final List<SGDataColumnInfo> yColList = getColumnList(cols, Y_VALUE);
      if (yColList.size() > 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }

      // checks whether given column types are for grid plot or scatter plot
      Integer zIndex = zCol.getGenericDimensionIndex();
      final boolean gridPlot = (zIndex.intValue() == -1);

      // checks length of dimensions
      if (gridPlot) {
        // grid plot
        if (xColList.size() == 1) {
          SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
          final int len = xCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          final Integer zxIndex = zCol.getDimensionIndex(KEY_SXYZ_X_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(zxIndex)) {
            return false;
          }
          final int zxLen = zDims[zxIndex];
          if (len != zxLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
        if (yColList.size() == 1) {
          SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
          final int len = yCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          final Integer zyIndex = zCol.getDimensionIndex(KEY_SXYZ_Y_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(zyIndex)) {
            return false;
          }
          final int zyLen = zDims[zyIndex];
          if (len != zyLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
      } else {
        // scatter plot
        final int zLen = zCol.getGenericDimensionLength();
        if (zLen == -1) {
          return false;
        }
        if (xColList.size() == 1) {
          SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
          final int len = xCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          if (len != zLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
        if (yColList.size() == 1) {
          SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
          final int len = yCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          if (len != zLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
      }

    } else if (isVXYTypeData(dataType)) {

      final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
      final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);
      final List<SGDataColumnInfo> fColList = getColumnList(cols, first);
      if (fColList.size() != 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      final List<SGDataColumnInfo> sColList = getColumnList(cols, second);
      if (sColList.size() != 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }

      SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
      final int[] fDims = fCol.getDimensions();
      SGMDArrayDataColumnInfo sCol = (SGMDArrayDataColumnInfo) sColList.get(0);
      final int[] sDims = sCol.getDimensions();
      final List<SGDataColumnInfo> xColList = getColumnList(cols, X_COORDINATE);
      if (xColList.size() > 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }
      final List<SGDataColumnInfo> yColList = getColumnList(cols, Y_COORDINATE);
      if (yColList.size() > 1) {
        errmsgBuffer.append(MSG_PROPER_COLUMN_TYPE);
        return false;
      }

      // checks whether given column types are for grid plot or scatter plot
      Integer fIndex = fCol.getGenericDimensionIndex();
      final boolean gridPlot = (fIndex.intValue() == -1);

      // check length of dimensions
      if (gridPlot) {
        // grid plot
        if (xColList.size() == 1) {
          SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
          final int len = xCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          final Integer fxIndex = fCol.getDimensionIndex(KEY_VXY_X_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(fxIndex)) {
            return false;
          }
          final int fxLen = fDims[fxIndex];
          if (fxLen == -1) {
            return false;
          }
          if (len != fxLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
          final Integer sxIndex = sCol.getDimensionIndex(KEY_VXY_X_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(sxIndex)) {
            return false;
          }
          final int sxLen = sDims[sxIndex];
          if (sxLen == -1) {
            return false;
          }
          if (len != sxLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
        if (yColList.size() == 1) {
          SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
          final int len = yCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          final Integer fyIndex = fCol.getDimensionIndex(KEY_VXY_Y_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(fyIndex)) {
            return false;
          }
          final int fyLen = fDims[fyIndex];
          if (fyLen == -1) {
            return false;
          }
          if (len != fyLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
          final Integer syIndex = sCol.getDimensionIndex(KEY_VXY_Y_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(syIndex)) {
            return false;
          }
          final int syLen = sDims[syIndex];
          if (syLen == -1) {
            return false;
          }
          if (len != syLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }

      } else {
        // scatter plot
        final int fLen = fCol.getGenericDimensionLength();
        if (fLen == -1) {
          return false;
        }
        final int sLen = sCol.getGenericDimensionLength();
        if (sLen == -1) {
          return false;
        }
        if (xColList.size() == 1) {
          SGMDArrayDataColumnInfo xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
          final int len = xCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          if (len != fLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
          if (len != sLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
        if (yColList.size() == 1) {
          SGMDArrayDataColumnInfo yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
          final int len = yCol.getGenericDimensionLength();
          if (len == -1) {
            return false;
          }
          if (len != fLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
          if (len != sLen) {
            errmsgBuffer.append(MSG_DIMENSIONS_SAME_LENGTH);
            return false;
          }
        }
      }
    }

    return true;
  }

  public static boolean checkMDArrayDataColumns(
      final SGDataColumnInfo[] cols,
      final String dataType,
      final SGMDArrayFile mdFile,
      final Map<String, Object> infoMap) {
    return checkMDArrayDataColumns(cols, dataType, mdFile, infoMap, new StringBuilder());
  }

  /**
   * Checks the data columns for netCDF data.
   *
   * @param cols the data columns
   * @param dataType the data type
   * @param ncfile the netCDF data
   * @param infoMap the information map
   * @return true if columns are valid
   */
  public static boolean checkNetCDFDataColumns(
      final SGDataColumnInfo[] cols,
      final String dataType,
      final SGNetCDFFile ncfile,
      final Map<String, Object> infoMap) {

    // animation
    final List<String> timeNameList = getColumnNameList(cols, ANIMATION_FRAME);
    if (timeNameList.size() > 1) {
      return false;
    }
    SGNetCDFVariable timeVar = null;
    Dimension timeDim = null;
    if (timeNameList.size() == 1) {
      String aName = timeNameList.get(0);
      timeVar = ncfile.findVariable(aName);
      if (timeVar == null) {
        return false;
      }
      if (!timeVar.isCoordinateVariable()) {
        return false;
      }
      timeDim = timeVar.getDimension(0);
    }

    // index
    final List<String> indexNameList = getColumnNameList(cols, INDEX);
    if (indexNameList.size() > 1) {
      return false;
    }
    SGNetCDFVariable indexVar = null;
    Dimension indexDim = null;
    if (indexNameList.size() == 1) {
      String sName = indexNameList.get(0);
      indexVar = ncfile.findVariable(sName);
      if (indexVar == null) {
        return false;
      }
      if (!indexVar.isCoordinateVariable()) {
        return false;
      }
      indexDim = indexVar.getDimension(0);
    }

    if (isSXYTypeData(dataType)) {
      Boolean multipleVariable =
          (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE);
      if (multipleVariable == null) {
        return false;
      }

      // x and y values
      final List<String> xNameList = getColumnNameList(cols, X_VALUE);
      final List<String> yNameList = getColumnNameList(cols, Y_VALUE);
      if (xNameList.size() == 0 || yNameList.size() == 0) {
        return false;
      }
      if (multipleVariable.booleanValue()) {
        if (xNameList.size() != 1 && yNameList.size() != 1) {
          return false;
        }
      } else {
        if (xNameList.size() != 1 || yNameList.size() != 1) {
          return false;
        }
      }
      final String xName0 = xNameList.get(0);
      final String yName0 = yNameList.get(0);
      final SGNetCDFVariable xVar0 = ncfile.findVariable(xName0);
      final SGNetCDFVariable yVar0 = ncfile.findVariable(yName0);
      final SGNetCDFVariable cVar;
      final List<String> oNameList;
      if (indexVar != null) {
        cVar = indexVar;
        oNameList = new ArrayList<String>();
        oNameList.addAll(xNameList);
        oNameList.addAll(yNameList);
      } else {
        if (xNameList.size() == 1 && yNameList.size() == 1) {
          if (xVar0.isCoordinateVariable()) {
            cVar = xVar0;
            oNameList = yNameList;
          } else if (yVar0.isCoordinateVariable()) {
            cVar = yVar0;
            oNameList = xNameList;
          } else {
            return false;
          }
        } else if (xNameList.size() == 1) {
          if (!xVar0.isCoordinateVariable()) {
            return false;
          }
          cVar = xVar0;
          oNameList = yNameList;
        } else {
          if (!yVar0.isCoordinateVariable()) {
            return false;
          }
          cVar = yVar0;
          oNameList = xNameList;
        }
      }

      List<SGNetCDFVariable> oVarList = new ArrayList<SGNetCDFVariable>();
      for (int ii = 0; ii < oNameList.size(); ii++) {
        String oName = oNameList.get(ii);
        SGNetCDFVariable oVar = ncfile.findVariable(oName);
        if (oVar == null) {
          return false;
        }
        if (oVar.isCoordinateVariable()) {
          return false;
        }
        oVarList.add(oVar);
      }

      // variable must have a dimension equal to the coordinate variable
      Dimension cDim = cVar.getDimension(0);
      for (int ii = 0; ii < oVarList.size(); ii++) {
        SGNetCDFVariable oVar = oVarList.get(ii);
        List<Dimension> oDims = oVar.getDimensions();
        if (!oDims.contains(cDim)) {
          return false;
        }
      }

      // lower and upper error values
      if (!multipleVariable) {
        // multiple dimension

        final List<String> lNameList = getColumnNameList(cols, LOWER_ERROR_VALUE);
        final List<String> uNameList = getColumnNameList(cols, UPPER_ERROR_VALUE);
        if (!(lNameList.size() == 0 && uNameList.size() == 0)
            && !(lNameList.size() == 1 && uNameList.size() == 1)) {
          return false;
        }

        // if lower and upper errors are selected, lower-upper must not
        // be selected
        final List<String> luNameList = getColumnNameList(cols, LOWER_UPPER_ERROR_VALUE);
        if (lNameList.size() == 1 && uNameList.size() == 1) {
          if (luNameList.size() != 0) {
            return false;
          }
        }

        if (lNameList.size() != 0) {
          String lName = lNameList.get(0);
          String uName = uNameList.get(0);
          SGNetCDFVariable lVar = ncfile.findVariable(lName);
          if (lVar == null) {
            return false;
          }
          SGNetCDFVariable uVar = ncfile.findVariable(uName);
          if (uVar == null) {
            return false;
          }

          // check validity of selected variables
          final boolean bl = lVar.isCoordinateVariable();
          if (bl) {
            return false;
          }
          final boolean bu = uVar.isCoordinateVariable();
          if (bu) {
            return false;
          }

          // variables must have a dimension equal to the coordinate variable
          List<Dimension> lDims = lVar.getDimensions();
          if (!lDims.contains(cDim)) {
            return false;
          }
          List<Dimension> uDims = uVar.getDimensions();
          if (!uDims.contains(cDim)) {
            return false;
          }

        } else if (luNameList.size() != 0) {

          String luName = luNameList.get(0);
          SGNetCDFVariable luVar = ncfile.findVariable(luName);
          if (luVar == null) {
            return false;
          }

          // check validity of selected variables
          final boolean blu = luVar.isCoordinateVariable();
          if (blu) {
            return false;
          }

          // variables must have a dimension equal to the coordinate variable
          List<Dimension> luDims = luVar.getDimensions();
          if (!luDims.contains(cDim)) {
            return false;
          }
        }

      } else {
        // single variable

        final List<String> lNameList = new ArrayList<String>();
        final List<Integer> lIndexList = new ArrayList<Integer>();
        final List<String> uNameList = new ArrayList<String>();
        final List<Integer> uIndexList = new ArrayList<Integer>();
        if (getColumnNameAndAppendedNumberList(cols, LOWER_ERROR_VALUE, lNameList, lIndexList)
            == false) {
          return false;
        }
        if (getColumnNameAndAppendedNumberList(cols, UPPER_ERROR_VALUE, uNameList, uIndexList)
            == false) {
          return false;
        }
        if (lNameList.size() != uNameList.size()) {
          return false;
        }
        for (int ii = 0; ii < lNameList.size(); ii++) {
          String lName = lNameList.get(ii);
          String uName = uNameList.get(ii);
          SGNetCDFVariable lVar = ncfile.findVariable(lName);
          if (lVar == null) {
            return false;
          }
          SGNetCDFVariable uVar = ncfile.findVariable(uName);
          if (uVar == null) {
            return false;
          }

          // check validity of selected variables
          final boolean bl = lVar.isCoordinateVariable();
          if (bl) {
            return false;
          }
          final boolean bu = uVar.isCoordinateVariable();
          if (bu) {
            return false;
          }

          // variables must have a dimension equal to the coordinate variable
          List<Dimension> lDims = lVar.getDimensions();
          if (!lDims.contains(cDim)) {
            return false;
          }
          List<Dimension> uDims = uVar.getDimensions();
          if (!uDims.contains(cDim)) {
            return false;
          }
        }

        final List<String> luNameList = new ArrayList<String>();
        final List<Integer> luIndexList = new ArrayList<Integer>();
        if (getColumnNameAndAppendedNumberList(
                cols, LOWER_UPPER_ERROR_VALUE, luNameList, luIndexList)
            == false) {
          return false;
        }
        for (int ii = 0; ii < luNameList.size(); ii++) {

          String luName = luNameList.get(ii);
          SGNetCDFVariable luVar = ncfile.findVariable(luName);
          if (luVar == null) {
            return false;
          }

          // check validity of selected variables
          final boolean blu = luVar.isCoordinateVariable();
          if (blu) {
            return false;
          }

          // variables must have a dimension equal to the coordinate variable
          List<Dimension> luDims = luVar.getDimensions();
          if (!luDims.contains(cDim)) {
            return false;
          }

          // checks whether lower/upper indices is contained
          // in lower indices or upper indices
          Integer luIndex = luIndexList.get(ii);
          if (lIndexList.contains(luIndex)) {
            return false;
          }
          if (uIndexList.contains(luIndex)) {
            return false;
          }
        }
      }

      // index
      if (indexVar != null) {
        for (int ii = 0; ii < oVarList.size(); ii++) {
          SGNetCDFVariable oVar = oVarList.get(ii);
          List<Dimension> oDims = oVar.getDimensions();
          if (!oDims.contains(indexDim)) {
            return false;
          }
        }
      }

      // multiple
      if (!multipleVariable.booleanValue()) {
        // multiple dimension

        final List<String> pickUpDimNameList = getColumnNameList(cols, PICKUP);
        if (pickUpDimNameList.size() != 1) {
          return false;
        }
        String pickUpDimName = pickUpDimNameList.get(0);
        Dimension pickUpDim = ncfile.getNetcdfFile().findDimension(pickUpDimName);
        if (pickUpDim == null) {
          return false;
        }
        if (pickUpDim.equals(cDim) || pickUpDim.equals(timeDim)) {
          return false;
        }
        boolean found = false;
        for (SGNetCDFVariable oVar : oVarList) {
          List<Dimension> oDims = oVar.getDimensions();
          if (oDims.contains(pickUpDim)) {
            found = true;
            break;
          }
        }
        if (!found) {
          return false;
        }
      }

    } else if (SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType)
        || SGDataTypeConstants.VXY_NETCDF_DATA.equals(dataType)) {

      String xColumnType =
          SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType) ? X_VALUE : X_COORDINATE;
      String yColumnType =
          SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType) ? Y_VALUE : Y_COORDINATE;

      final List<String> xNameList = getColumnNameList(cols, xColumnType);
      if (xNameList.size() != 1) {
        return false;
      }
      final List<String> yNameList = getColumnNameList(cols, yColumnType);
      if (yNameList.size() != 1) {
        return false;
      }
      String xName = xNameList.get(0);
      String yName = yNameList.get(0);
      SGNetCDFVariable xVar = ncfile.findVariable(xName);
      if (xVar == null) {
        return false;
      }
      SGNetCDFVariable yVar = ncfile.findVariable(yName);
      if (yVar == null) {
        return false;
      }

      final List<String> xIndexNameList = getColumnNameList(cols, X_INDEX);
      if (xIndexNameList.size() > 1) {
        return false;
      }
      SGNetCDFVariable xIndexVar =
          (xIndexNameList.size() == 1) ? ncfile.findVariable(xIndexNameList.get(0)) : null;

      final List<String> yIndexNameList = getColumnNameList(cols, Y_INDEX);
      if (yIndexNameList.size() > 1) {
        return false;
      }
      SGNetCDFVariable yIndexVar =
          (yIndexNameList.size() == 1) ? ncfile.findVariable(yIndexNameList.get(0)) : null;

      // x and y variables must be coordinate variables
      if (indexVar != null) {
        if (xVar.isCoordinateVariable()) {
          return false;
        }
        if (yVar.isCoordinateVariable()) {
          return false;
        }
      } else {
        if (xIndexVar != null) {
          if (!xIndexVar.isCoordinateVariable()) {
            return false;
          }
          if (xVar.isCoordinateVariable()) {
            return false;
          }
        } else {
          if (!xVar.isCoordinateVariable()) {
            return false;
          }
        }
        if (yIndexVar != null) {
          if (!yIndexVar.isCoordinateVariable()) {
            return false;
          }
          if (yVar.isCoordinateVariable()) {
            return false;
          }
        } else {
          if (!yVar.isCoordinateVariable()) {
            return false;
          }
        }
      }

      List<Dimension> xDims = xVar.getDimensions();
      List<Dimension> yDims = yVar.getDimensions();
      Dimension xDim = null;
      Dimension yDim = null;
      if (indexVar == null) {
        if (xIndexVar != null) {
          xDim = xIndexVar.getDimension(0);
          if (!xDims.contains(xDim)) {
            return false;
          }
        } else {
          xDim = xDims.get(0);
        }
        if (yIndexVar != null) {
          yDim = yIndexVar.getDimension(0);
          if (!yDims.contains(yDim)) {
            return false;
          }
        } else {
          yDim = yDims.get(0);
        }
        if (xDim.equals(timeDim)) {
          return false;
        }
        if (yDim.equals(timeDim)) {
          return false;
        }
      }

      if (SGDataTypeConstants.SXYZ_NETCDF_DATA.equals(dataType)) {

        final List<String> zNameList = getColumnNameList(cols, Z_VALUE);
        if (zNameList.size() != 1) {
          return false;
        }
        String zName = zNameList.get(0);
        SGNetCDFVariable zVar = ncfile.findVariable(zName);
        if (zVar == null) {
          return false;
        }

        // z variable must not be a coordinate variable
        if (zVar.isCoordinateVariable()) {
          return false;
        }

        // variable must have a dimension equal to the coordinate variable
        List<Dimension> zDims = zVar.getDimensions();
        if (indexVar != null) {
          if (!xDims.contains(indexDim)) {
            return false;
          }
          if (!yDims.contains(indexDim)) {
            return false;
          }
          if (!zDims.contains(indexDim)) {
            return false;
          }
        } else {
          if (!zDims.contains(xDim)) {
            return false;
          }
          if (!zDims.contains(yDim)) {
            return false;
          }
        }

      } else if (SGDataTypeConstants.VXY_NETCDF_DATA.equals(dataType)) {

        final String first = SGDataStrideUtility.getVXYFirstComponentColumnType(infoMap);
        final String second = SGDataStrideUtility.getVXYSecondComponentColumnType(infoMap);

        final List<String> fNameList = getColumnNameList(cols, first);
        if (fNameList.size() != 1) {
          return false;
        }
        final List<String> sNameList = getColumnNameList(cols, second);
        if (sNameList.size() != 1) {
          return false;
        }
        String fName = fNameList.get(0);
        String sName = sNameList.get(0);
        SGNetCDFVariable fVar = ncfile.findVariable(fName);
        if (fVar == null) {
          return false;
        }
        SGNetCDFVariable sVar = ncfile.findVariable(sName);
        if (sVar == null) {
          return false;
        }

        // component variables must not be coordinate variable
        if (fVar.isCoordinateVariable()) {
          return false;
        }
        if (sVar.isCoordinateVariable()) {
          return false;
        }

        List<Dimension> fDims = fVar.getDimensions();
        List<Dimension> sDims = sVar.getDimensions();
        if (indexVar != null) {
          if (xDims.contains(indexDim) == false) {
            return false;
          }
          if (yDims.contains(indexDim) == false) {
            return false;
          }
          if (fDims.contains(indexDim) == false) {
            return false;
          }
          if (sDims.contains(indexDim) == false) {
            return false;
          }
        } else {
          if (fDims.contains(xDim) == false) {
            return false;
          }
          if (fDims.contains(yDim) == false) {
            return false;
          }
          if (sDims.contains(xDim) == false) {
            return false;
          }
          if (sDims.contains(yDim) == false) {
            return false;
          }
        }
      }

    } else {
      throw new Error("Invalid data type: " + dataType);
    }

    return true;
  }

  /**
   * @param nc
   * @param columns
   * @param xValueNames
   * @param yValueNames
   * @param leValueNames
   * @param ueValueNames
   * @param ehValueNames
   * @param tlValueNames
   * @param thValueNames
   */
  public static void updateColumnTypeOfSXYMultipleVariableNetCDFDataFromVariableNames(
      final SGNetCDFFile nc,
      final SGDataColumnInfo[] columns,
      final String[] xValueNames,
      final String[] yValueNames,
      final String[] leValueNames,
      final String[] ueValueNames,
      final String[] ehValueNames,
      final String[] tlValueNames,
      final String[] thValueNames) {
    for (int i = 0; i < xValueNames.length; i++) {
      int xVarIndex = nc.getVariableIndex(xValueNames[i]);
      columns[xVarIndex].setColumnType(SGIDataColumnTypeConstants.X_VALUE);
    }
    for (int i = 0; i < yValueNames.length; i++) {
      int yVarIndex = nc.getVariableIndex(yValueNames[i]);
      columns[yVarIndex].setColumnType(SGIDataColumnTypeConstants.Y_VALUE);
    }

    boolean errorbarFlag = false;
    boolean tickLabelFlag = false;
    if (leValueNames != null
        && leValueNames.length > 0
        && ueValueNames != null
        && ueValueNames.length > 0
        && ehValueNames != null
        && ehValueNames.length > 0
        && leValueNames.length == ueValueNames.length
        && leValueNames.length == ehValueNames.length) {
      errorbarFlag = true;
    }
    if (tlValueNames != null
        && tlValueNames.length > 0
        && thValueNames != null
        && thValueNames.length > 0
        && tlValueNames.length == thValueNames.length) {
      tickLabelFlag = true;
    }
    if (errorbarFlag && leValueNames != null && ueValueNames != null && ehValueNames != null) {
      for (int i = 0; i < leValueNames.length; i++) {
        int leVarIndex = nc.getVariableIndex(leValueNames[i]);
        int ueVarIndex = nc.getVariableIndex(ueValueNames[i]);
        if (leVarIndex != ueVarIndex) {
          columns[leVarIndex].setColumnType(
              SGIDataColumnTypeConstants.LOWER_ERROR_VALUE + " for " + ehValueNames[i]);
          columns[ueVarIndex].setColumnType(
              SGIDataColumnTypeConstants.UPPER_ERROR_VALUE + " for " + ehValueNames[i]);
        } else {
          columns[leVarIndex].setColumnType(
              SGIDataColumnTypeConstants.LOWER_UPPER_ERROR_VALUE + " for " + ehValueNames[i]);
        }
      }
    }
    if (tickLabelFlag && tlValueNames != null && thValueNames != null) {
      for (int i = 0; i < tlValueNames.length; i++) {
        int tlVarIndex = nc.getVariableIndex(tlValueNames[i]);
        columns[tlVarIndex].setColumnType(
            SGIDataColumnTypeConstants.TICK_LABEL + " for " + thValueNames[i]);
      }
    }
  }
}
