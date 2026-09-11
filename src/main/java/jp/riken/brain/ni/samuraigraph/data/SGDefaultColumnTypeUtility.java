package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeMDArrayUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeNetCDFUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDefaultColumnTypeSDArrayUtility.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SGDefaultColumnTypeUtility
    implements SGIDataColumnTypeConstants, SGIDataPropertyKeyConstants {

  /** A class for the result of default column types. */
  public static class DefaultColumnTypeResult {
    protected boolean mSucceeded = false;
    protected SGDataColumnInfo[] mColumns = null;

    public DefaultColumnTypeResult(final SGDataColumnInfo[] columns, final boolean succeeded) {
      super();
      if (columns == null) {
        throw new IllegalArgumentException("columns == null");
      }
      this.mColumns = columns;
      this.mSucceeded = succeeded;
    }

    public String[] getDefaultColumnTypes() {
      String[] columnTypes = new String[this.mColumns.length];
      for (int ii = 0; ii < columnTypes.length; ii++) {
        columnTypes[ii] = this.mColumns[ii].getColumnType();
      }
      return columnTypes;
    }

    public boolean isSucceeded() {
      return this.mSucceeded;
    }
  }

  public static class DefaultMDColumnTypeResult extends DefaultColumnTypeResult {
    public DefaultMDColumnTypeResult(final SGDataColumnInfo[] columns, final boolean succeeded) {
      super(columns, succeeded);
    }

    public int[] getIndices() {
      int[] indices = new int[this.mColumns.length];
      for (int ii = 0; ii < indices.length; ii++) {
        SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) this.mColumns[ii];
        indices[ii] = col.getGenericDimensionIndex();
      }
      return indices;
    }

    public List<Map<String, Integer>> getAllIndices() {
      List<Map<String, Integer>> indices = new ArrayList<Map<String, Integer>>();
      for (int ii = 0; ii < this.mColumns.length; ii++) {
        SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) this.mColumns[ii];
        Map<String, Integer> map = col.getDimensionIndices();
        indices.add(map);
      }
      return indices;
    }
  }

  /**
   * Returns default data column type.
   *
   * @param dataType data type
   * @param columnInfoList a list of column information
   * @param infoMap information map for data
   * @return default data column types
   */
  public static DefaultColumnTypeResult getDefaultColumnTypes(
      final String dataType,
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap) {

    if (dataType == null || columnInfoList == null || infoMap == null) {
      throw new IllegalArgumentException("Null input value.");
    }

    SGDataColumnInfo[] columns = new SGDataColumnInfo[columnInfoList.size()];
    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii] = (SGDataColumnInfo) columnInfoList.get(ii).clone();
    }

    // the node name map
    final boolean succeeded =
        SGDefaultColumnTypeUtility.getDefaultColumnTypesSub(
            dataType, columnInfoList, infoMap, columns);

    // returned value
    DefaultColumnTypeResult result;
    if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
      result = new DefaultMDColumnTypeResult(columns, succeeded);
    } else {
      result = new DefaultColumnTypeResult(columns, succeeded);
    }

    return result;
  }

  private static boolean getDefaultColumnTypesSub(
      final String dataType,
      final List<SGDataColumnInfo> columnInfoList,
      final Map<String, Object> infoMap,
      SGDataColumnInfo[] columns) {

    boolean succeeded = true;
    final int len = columnInfoList.size();
    final NamedNodeMap nodeMap = (NamedNodeMap) infoMap.get(SGIFigureElementGraph.KEY_NODE_MAP);
    final String groupName = (String) infoMap.get(SGIFigureElementGraph.KEY_GROUP_NAME);

    if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
      // single dimensional array data
      List<Integer> numberIndexList = new ArrayList<Integer>();
      List<Integer> textIndexList = new ArrayList<Integer>();
      List<Integer> dateIndexList = new ArrayList<Integer>();
      List<Integer> samplingIndexList = new ArrayList<Integer>();
      for (int ii = 0; ii < len; ii++) {
        SGDataColumnInfo cInfo = columnInfoList.get(ii);
        String valueType = cInfo.getValueType();
        if (VALUE_TYPE_NUMBER.equals(valueType)) {
          numberIndexList.add(Integer.valueOf(ii));
        } else if (VALUE_TYPE_TEXT.equals(valueType)) {
          textIndexList.add(Integer.valueOf(ii));
        } else if (VALUE_TYPE_DATE.equals(valueType)) {
          dateIndexList.add(Integer.valueOf(ii));
        } else if (VALUE_TYPE_SAMPLING_RATE.equals(valueType)) {
          samplingIndexList.add(Integer.valueOf(ii));
        }
      }
      if (SGDataDataTypeUtility.isSXYTypeData(dataType)
          || SGDataTypeConstants.SXY_SAMPLING_DATA.equals(dataType)) {
        if (nodeMap != null) {
          if (!getForSXYSDArrayData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForSXYSDArrayData(
              columnInfoList,
              len,
              infoMap,
              numberIndexList,
              textIndexList,
              dateIndexList,
              samplingIndexList,
              columns)) {
            return false;
          }
        }
      } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForSXYZSDArrayData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForSXYZSDArrayData(
              columnInfoList,
              len,
              infoMap,
              numberIndexList,
              textIndexList,
              dateIndexList,
              samplingIndexList,
              columns)) {
            return false;
          }
        }
      } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForVXYSDArrayData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForVXYSDArrayData(
              columnInfoList,
              len,
              infoMap,
              numberIndexList,
              textIndexList,
              dateIndexList,
              samplingIndexList,
              columns)) {
            return false;
          }
        }
      } else {
        throw new Error("Invalid data type: " + dataType);
      }

    } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
      // NetCDF data
      SGNetCDFFile ncFile =
          (SGNetCDFFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
      List<SGNetCDFVariable> varList = ncFile.getVariables();
      final int size = varList.size();
      if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForSXYNetCDFData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForSXYNetCDFData(infoMap, varList, size, columns)) {
            return false;
          }
        }
      } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForVXYNetCDFData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForVXYNetCDFData(infoMap, varList, size, columns)) {
            return false;
          }
        }
      } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForSXYZNetCDFData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForSXYZNetCDFData(infoMap, varList, size, columns)) {
            return false;
          }
        }
      } else {
        throw new Error("Invalid data type: " + dataType);
      }
    } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
      // multidimensional array data
      SGMDArrayFile mdFile =
          (SGMDArrayFile) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_SOURCE);
      SGMDArrayVariable[] vars = mdFile.getVariables();
      final int size = vars.length;
      if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForSXYMDArrayData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForSXYMDArrayData(infoMap, vars, size, columns)) {
            return false;
          }
        }
      } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForVXYMDArrayData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForVXYMDArrayData(infoMap, vars, size, columns)) {
            return false;
          }
        }

      } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
        if (nodeMap != null) {
          if (!getForSXYZMDArrayData(columnInfoList, infoMap, nodeMap, groupName, columns)) {
            succeeded = false;
          }
          if (succeeded) {
            if (!SGDataMiscUtility.checkDataColumns(dataType, columns, infoMap)) {
              succeeded = false;
            }
          }
        }
        if (nodeMap == null || !succeeded) {
          if (!getForSXYZMDArrayData(infoMap, vars, size, columns)) {
            return false;
          }
        }
      } else {
        throw new Error("Invalid data type: " + dataType);
      }
    } else {
      throw new Error("Unsupported data type: " + dataType);
    }
    return true;
  }

  static Integer[] getIndices(final NamedNodeMap nodeMap, final String key) {
    Node nodeIndices = nodeMap.getNamedItem(key);
    if (nodeIndices == null) {
      return null;
    }
    String strIndices = nodeIndices.getNodeValue();
    if (strIndices == null) {
      return null;
    }
    Integer[] indices = SGUtilityText.parseIndices(strIndices);
    return indices;
  }

  static boolean checkIndices(final Integer[] indices, final String[] columnTypes) {
    for (int ii = 0; ii < indices.length; ii++) {
      final int index = indices[ii].intValue();
      if (index < 0 || index >= columnTypes.length) {
        return false;
      }
    }
    return true;
  }

  // for serial number case

  // for serial number case

  // for serial number case

  // Compares two text strings for the name of NetCDF variables.
  // Slashes are replaces with underscores in the comparison.
  // This method is only for NetCDF data set file.

  // The argument "varName" is a text string that starts with the name of a data set
  // and ends with the dimension index.

  static String[] getNames(
      final NamedNodeMap nodeMap, final String[] keys, final String groupName) {
    for (int ii = 0; ii < keys.length; ii++) {
      String[] names = getNames(nodeMap, keys[ii], groupName);
      if (names != null) {
        return names;
      }
    }
    return null;
  }

  static String[] getNames(final NamedNodeMap nodeMap, final String key, final String groupName) {
    String strNames = getString(nodeMap, key);
    if (strNames == null) {
      return null;
    }
    String[] names = SGUtilityText.parseStrings(strNames);
    if (groupName != null) {
      for (int ii = 0; ii < names.length; ii++) {
        names[ii] = SGDataTextUtility.appendGroupName(names[ii], groupName);
      }
    }
    return names;
  }

  static String getString(final NamedNodeMap nodeMap, final String key) {
    Node nodeNames = nodeMap.getNamedItem(key);
    if (nodeNames == null) {
      return null;
    }
    String strNames = nodeNames.getNodeValue();
    if (strNames == null) {
      return null;
    }
    return strNames;
  }

  static boolean isNumberColumn(final List<SGDataColumnInfo> columnInfoList, final int index) {
    SGDataColumnInfo col = columnInfoList.get(index);
    return VALUE_TYPE_NUMBER.equals(col.getValueType());
  }

  static boolean isDateColumn(final List<SGDataColumnInfo> columnInfoList, final int index) {
    SGDataColumnInfo col = columnInfoList.get(index);
    return VALUE_TYPE_DATE.equals(col.getValueType());
  }

  static boolean isCoordinateVariableColumn(
      final List<SGDataColumnInfo> columnInfoList, final int index) {
    SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) columnInfoList.get(index);
    return col.isCoordinateVariable();
  }

  // Creates a map of dimensions.

  // Extracts dimensions.
}
