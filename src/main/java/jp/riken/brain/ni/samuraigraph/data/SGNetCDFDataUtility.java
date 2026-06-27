package jp.riken.brain.ni.samuraigraph.data;

import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import ucar.ma2.DataType;
import ucar.nc2.Attribute;
import ucar.nc2.NetcdfFiles;
import ucar.nc2.Variable;

/** NetCDF-specific utility methods extracted from SGDataUtility. */
public final class SGNetCDFDataUtility {

  private static final String MID_COLUMN = " for ";

  private SGNetCDFDataUtility() {
    // utility class
  }

  // -- Phase 1: Variable/Attribute Type Detection --

  /**
   * Checks if the given NetCDF variable is an SGDateVariable.
   *
   * <p>True if data type of the variable is CHAR and it has an attribute "value_type=Date".
   *
   * @param var the NetCDF variable
   * @return true if the variable is an SGDateVariable
   */
  public static boolean isSGDateVariable(final Variable var) {
    if (DataType.CHAR.equals(var.getDataType())) {
      for (Attribute attr : var.attributes()) {
        if (attr.isString()) {
          String name = attr.getShortName();
          String value = attr.getStringValue();
          if (SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE.equals(name.trim())
              && SGIDataColumnTypeConstants.VALUE_TYPE_DATE.equals(value.trim())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Checks if the given NetCDF variable is an SGTextVariable.
   *
   * <p>True if data type of the variable is CHAR and it has an attribute "value_type=Text".
   *
   * @param var the NetCDF variable
   * @return true if the variable is an SGTextVariable
   */
  public static boolean isSGTextVariable(final Variable var) {
    if (DataType.CHAR.equals(var.getDataType())) {
      for (Attribute attr : var.attributes()) {
        if (attr.isString()) {
          String name = attr.getShortName();
          String value = attr.getStringValue();
          if (name != null
              && value != null
              && SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE.equals(name.trim())
              && SGIDataColumnTypeConstants.VALUE_TYPE_TEXT.equals(value.trim())) {
            return true;
          }
        }
      }
    }
    return false;
  }

  /**
   * Creates a NetCDF attribute for the given value type.
   *
   * @param valueType the value type string
   * @return the created attribute
   */
  public static Attribute getValueTypeAttribute(final String valueType) {
    Attribute attr = new Attribute(SGINetCDFConstants.ATTRIBUTE_VALUE_TYPE, valueType);
    return attr;
  }

  /**
   * Checks if the given NetCDF data can open its file.
   *
   * @param data the NetCDF data
   * @return true if the file can be opened
   */
  public static boolean canOpenNetCDF(final SGNetCDFData data) {
    try {
      boolean isNetCDFFile =
          NetcdfFiles.canOpen(data.getNetcdfFile().getNetcdfFile().getLocation());
      return isNetCDFFile;
    } catch (Exception e) {
      return false;
    }
  }

  // -- Phase 2: Utility Helpers --

  /**
   * Checks if two NetCDF variable names belong to the same group.
   *
   * @param name1 the first variable name
   * @param name2 the second variable name
   * @return true if both names belong to the same group
   */
  public static boolean isSameNetCDFGroup(final String name1, final String name2) {
    String[] str1 = name1.split("/");
    String[] str2 = name2.split("/");
    if (str1.length == str2.length) {
      for (int i = 0; i < str1.length - 1; i++) {
        if (str1[i].equals(str2[i]) == false) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Extracts the holder name from a column type string.
   *
   * @param info the column info
   * @return the holder name
   */
  public static String getHolderName(final SGDataColumnInfo info) {
    String columnType = info.getColumnType();
    final int lastIndex = columnType.lastIndexOf(MID_COLUMN);
    return columnType.substring(lastIndex + MID_COLUMN.length(), columnType.length());
  }

  /**
   * Finds the holder column for the given NetCDF column info.
   *
   * @param cols the column array
   * @param info the NetCDF column info
   * @return the holder column, or null if not found
   */
  public static SGNetCDFDataColumnInfo findHolderInfo(
      final SGDataColumnInfo[] cols, final SGNetCDFDataColumnInfo info) {
    String holderName = getHolderName(info);
    return (SGNetCDFDataColumnInfo) SGDataUtility.findColumnWithName(cols, holderName);
  }

  /**
   * Appends a group name prefix to a variable name.
   *
   * @param name the variable name
   * @param groupName the group name
   * @return the name with group prefix
   */
  public static String appendGroupName(final String name, final String groupName) {
    if (groupName == null || groupName.isEmpty()) {
      return name;
    }
    return groupName + "/" + name;
  }

  /**
   * Checks if the column array contains exactly one INDEX or SERIAL_NUMBERS column.
   *
   * @param cols the column array
   * @return true if an index column exists
   */
  public static boolean hasIndexColumnType(final SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> indexColumnList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.INDEX);
    if (indexColumnList.size() == 1) {
      return true;
    }
    List<SGDataColumnInfo> serialNumberColumnList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.SERIAL_NUMBERS);
    return (serialNumberColumnList.size() == 1);
  }

  /**
   * Checks if any column in the array is a pickup column.
   *
   * @param colInfo the column array
   * @return true if a pickup column is contained
   */
  public static boolean isPickupColumnContained(final SGDataColumnInfo[] colInfo) {
    for (int ii = 0; ii < colInfo.length; ii++) {
      if (colInfo[ii] instanceof SGNetCDFDataColumnInfo) {
        if (SGIDataColumnTypeConstants.PICKUP.equals(colInfo[ii].getColumnType())) {
          return true;
        }
      } else if (colInfo[ii] instanceof SGMDArrayDataColumnInfo) {
        SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
        Integer pickUpDimension =
            mdInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          return true;
        }
      }
    }
    return false;
  }

  // -- Phase 3: Data Length Queries --

  /**
   * Returns the data length for SXY NetCDF data.
   *
   * @param cols the column info array
   * @return the data length
   */
  public static int getSXYNetCDFDataLength(final SGDataColumnInfo[] cols) {
    final int len;
    List<SGDataColumnInfo> indexColList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.INDEX);
    if (indexColList.size() == 0) {
      List<SGDataColumnInfo> xColList =
          SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.X_VALUE);
      List<SGDataColumnInfo> yColList =
          SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.Y_VALUE);
      SGNetCDFDataColumnInfo xCol = (SGNetCDFDataColumnInfo) xColList.get(0);
      SGNetCDFDataColumnInfo yCol = (SGNetCDFDataColumnInfo) yColList.get(0);
      if (xCol.isCoordinateVariable()) {
        len = xCol.getDimension(0).getLength();
      } else {
        len = yCol.getDimension(0).getLength();
      }
    } else {
      SGNetCDFDataColumnInfo indexCol = (SGNetCDFDataColumnInfo) indexColList.get(0);
      len = indexCol.getDimension(0).getLength();
    }
    return len;
  }

  /**
   * Returns the data length for SXY MDArray data.
   *
   * @param cols the column info array
   * @return the data length
   */
  public static int getSXYMDArrayDataLength(final SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> xColList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.X_VALUE);
    List<SGDataColumnInfo> yColList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.Y_VALUE);
    SGMDArrayDataColumnInfo col;
    if (xColList.size() > 0) {
      col = (SGMDArrayDataColumnInfo) xColList.get(0);
    } else if (yColList.size() > 0) {
      col = (SGMDArrayDataColumnInfo) yColList.get(0);
    } else {
      return -1;
    }
    return col.getGenericDimensionLength();
  }

  /**
   * Returns the X dimension length for SXYZ NetCDF data.
   *
   * @param cols the column info array
   * @return the X dimension length
   */
  public static int getSXYZNetCDFDataXLength(final SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, SGIDataColumnTypeConstants.X_VALUE);
  }

  /**
   * Returns the Y dimension length for SXYZ NetCDF data.
   *
   * @param cols the column info array
   * @return the Y dimension length
   */
  public static int getSXYZNetCDFDataYLength(final SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, SGIDataColumnTypeConstants.Y_VALUE);
  }

  /**
   * Returns the X dimension length for SXYZ MDArray data.
   *
   * @param cols the column info array
   * @return the X dimension length
   */
  public static int getSXYZMDArrayDataXLength(final SGDataColumnInfo[] cols) {
    return getSXYZMDArrayDataXYLength(
        cols, SGIDataColumnTypeConstants.X_VALUE, SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION);
  }

  /**
   * Returns the Y dimension length for SXYZ MDArray data.
   *
   * @param cols the column info array
   * @return the Y dimension length
   */
  public static int getSXYZMDArrayDataYLength(final SGDataColumnInfo[] cols) {
    return getSXYZMDArrayDataXYLength(
        cols, SGIDataColumnTypeConstants.Y_VALUE, SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION);
  }

  /**
   * Returns the Z generic dimension length for SXYZ MDArray data.
   *
   * @param cols the column info array
   * @return the Z dimension length, or -1 if not found
   */
  public static int getSXYZMDArrayDataZGenericDimensionLength(final SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> zColList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.Z_VALUE);
    if (zColList.size() != 1) {
      return -1;
    }
    SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
    return zCol.getGenericDimensionLength();
  }

  /**
   * Returns the X coordinate dimension length for VXY NetCDF data.
   *
   * @param cols the column info array
   * @return the X dimension length
   */
  public static int getVXYNetCDFDataXLength(final SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, SGIDataColumnTypeConstants.X_COORDINATE);
  }

  /**
   * Returns the Y coordinate dimension length for VXY NetCDF data.
   *
   * @param cols the column info array
   * @return the Y dimension length
   */
  public static int getVXYNetCDFDataYLength(final SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, SGIDataColumnTypeConstants.Y_COORDINATE);
  }

  /**
   * Returns the X dimension length for VXY MDArray data.
   *
   * @param cols the column info array
   * @param polar whether polar coordinates
   * @return the X dimension length
   */
  public static int getVXYMDArrayDataXLength(final SGDataColumnInfo[] cols, final boolean polar) {
    return getVXYMDArrayDataXYLength(
        cols,
        SGIDataColumnTypeConstants.X_COORDINATE,
        SGIMDArrayConstants.KEY_VXY_X_DIMENSION,
        polar);
  }

  /**
   * Returns the Y dimension length for VXY MDArray data.
   *
   * @param cols the column info array
   * @param polar whether polar coordinates
   * @return the Y dimension length
   */
  public static int getVXYMDArrayDataYLength(final SGDataColumnInfo[] cols, final boolean polar) {
    return getVXYMDArrayDataXYLength(
        cols,
        SGIDataColumnTypeConstants.Y_COORDINATE,
        SGIMDArrayConstants.KEY_VXY_Y_DIMENSION,
        polar);
  }

  /**
   * Returns the component generic dimension length for VXY MDArray data.
   *
   * @param cols the column info array
   * @param polar whether polar coordinates
   * @return the component dimension length, or -1 if not found
   */
  public static int getVXYMDArrayDataComponentGenericDimensionLength(
      final SGDataColumnInfo[] cols, final boolean polar) {
    String fName =
        polar ? SGIDataColumnTypeConstants.MAGNITUDE : SGIDataColumnTypeConstants.X_COMPONENT;
    List<SGDataColumnInfo> fColList = SGDataUtility.findColumnsWithColumnType(cols, fName);
    if (fColList.size() != 1) {
      return -1;
    }
    SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
    return fCol.getGenericDimensionLength();
  }

  /**
   * Returns the index dimension length for NetCDF data.
   *
   * @param cols the column info array
   * @return the index dimension length
   */
  public static int getNetCDFDataIndexLength(final SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> colList =
        SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.INDEX);
    SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colList.get(0);
    return col.getDimension(0).getLength();
  }

  // -- Private helpers --

  private static int getTwoDimensionNetCDFDataLength(
      final SGDataColumnInfo[] cols, final String key) {
    List<SGDataColumnInfo> colList = SGDataUtility.findColumnsWithColumnType(cols, key);
    SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colList.get(0);
    return col.getDimension(0).getLength();
  }

  private static int getSXYZMDArrayDataXYLength(
      final SGDataColumnInfo[] cols, final String columnType, final String key) {
    int len = -1;
    List<SGDataColumnInfo> colList = SGDataUtility.findColumnsWithColumnType(cols, columnType);
    if (colList.size() == 1) {
      SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colList.get(0);
      len = col.getGenericDimensionLength();
    } else {
      List<SGDataColumnInfo> zColList =
          SGDataUtility.findColumnsWithColumnType(cols, SGIDataColumnTypeConstants.Z_VALUE);
      SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
      int[] zDims = zCol.getDimensions();
      Integer dim = zCol.getDimensionIndex(key);
      if (dim != null) {
        len = zDims[dim];
      }
    }
    return len;
  }

  private static int getVXYMDArrayDataXYLength(
      final SGDataColumnInfo[] cols,
      final String columnType,
      final String key,
      final boolean polar) {
    int len = -1;
    List<SGDataColumnInfo> colList = SGDataUtility.findColumnsWithColumnType(cols, columnType);
    String fName =
        polar ? SGIDataColumnTypeConstants.MAGNITUDE : SGIDataColumnTypeConstants.X_COMPONENT;
    List<SGDataColumnInfo> fColList = SGDataUtility.findColumnsWithColumnType(cols, fName);
    if (colList.size() == 1) {
      SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colList.get(0);
      len = col.getGenericDimensionLength();
    } else {
      SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
      int[] fDims = fCol.getDimensions();
      Integer dim = fCol.getDimensionIndex(key);
      if (dim != null) {
        len = fDims[dim];
      }
    }
    return len;
  }
}
