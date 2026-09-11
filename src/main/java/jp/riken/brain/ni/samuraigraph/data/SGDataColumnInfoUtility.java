package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataStrideUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import java.util.ArrayList;
import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;

/** Static helper for the ColumnInfo responsibility. */
public final class SGDataColumnInfoUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {

  private SGDataColumnInfoUtility() {}

  /**
   * Returns whether each column type of given data column information arrays.
   *
   * @param cols1 the first column information array
   * @param cols2 the second column information array
   * @return true if each column type of given data column information arrays are equal
   */
  public static boolean hasEqualColumnType(SGDataColumnInfo[] cols1, SGDataColumnInfo[] cols2) {
    if (cols1 == null || cols2 == null) {
      throw new IllegalArgumentException("cols1 == null || cols2 == null");
    }
    if (cols1.length != cols2.length) {
      throw new IllegalArgumentException("cols1.length != cols2.length");
    }
    for (int ii = 0; ii < cols1.length; ii++) {
      if (!SGUtility.equals(cols1[ii].getColumnType(), cols2[ii].getColumnType())) {
        return false;
      }
    }
    return true;
  }

  /**
   * Finds and returns the most frequent origin of time dimension. If selected state of time
   * dimension is invalid, returns -1.
   *
   * @param cols an array of data columns
   * @return the most frequent origin of time dimension
   */
  public static int findFrequentTimeOrigin(SGDataColumnInfo[] cols) {

    // checks validity
    boolean selectionValid = true;
    int dimLen = -1;
    for (int ii = 0; ii < cols.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
      Integer index = mdCol.getTimeDimensionIndex();
      if (index != -1) {
        int[] dims = mdCol.getDimensions();
        if (dimLen == -1) {
          dimLen = dims[index];
        } else {
          if (dimLen != dims[index]) {
            selectionValid = false;
            break;
          }
        }
      }
    }
    if (dimLen == -1) {
      // time index is not selected at any column
      return -1;
    }
    if (!selectionValid) {
      return -1;
    }

    // checks whether all indices are equal
    boolean allEqual = true;
    int origin = -1;
    for (int ii = 0; ii < cols.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
      Integer index = mdCol.getTimeDimensionIndex();
      if (index != -1) {
        int[] origins = mdCol.getOrigins();
        if (origin != -1) {
          if (origin != origins[index]) {
            allEqual = false;
          }
        }
        origin = origins[index];
      }
    }
    if (allEqual) {
      // if all indices are equal, do nothing
      return -1;
    }

    // finds the most frequent origin
    List<Object> originList = new ArrayList<Object>();
    for (int ii = 0; ii < cols.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
      Integer index = mdCol.getTimeDimensionIndex();
      if (index != -1) {
        int[] origins = mdCol.getOrigins();
        origin = origins[index];
        if (origin != -1) {
          originList.add(origin);
        }
      }
    }
    List<Object> fIndexList = SGUtility.findMostFrequentObjects(originList);
    if (fIndexList.size() == 0) {
      return -1;
    }
    Object first = fIndexList.get(0); // chooses the first element
    final int fIndex = (Integer) first;
    return fIndex;
  }

  static List<SGMDArrayDataColumnInfo> findValidPickUpXYColumns(SGDataColumnInfo[] cols) {
    List<SGMDArrayDataColumnInfo> pickUpColList = new ArrayList<SGMDArrayDataColumnInfo>();
    List<SGDataColumnInfo> xColList =
        SGDataColumnInfoUtility.findColumnsWithColumnType(cols, X_VALUE);
    List<SGDataColumnInfo> yColList =
        SGDataColumnInfoUtility.findColumnsWithColumnType(cols, Y_VALUE);
    if (xColList.size() == 0 && yColList.size() == 0) {
      return null;
    } else if (xColList.size() > 1 || yColList.size() > 1) {
      return null;
    }
    SGMDArrayDataColumnInfo xCol = null;
    if (xColList.size() == 1) {
      xCol = (SGMDArrayDataColumnInfo) xColList.get(0);
    }
    SGMDArrayDataColumnInfo yCol = null;
    if (yColList.size() == 1) {
      yCol = (SGMDArrayDataColumnInfo) yColList.get(0);
    }
    if (xCol != null && yCol != null) {
      Integer xIndex = xCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
      Integer yIndex = yCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
      final boolean xValid = isValidPickUpValue(xIndex);
      final boolean yValid = isValidPickUpValue(yIndex);
      if (xValid && !yValid) {
        if (xIndex.equals(xCol.getGenericDimensionIndex())) {
          return null;
        }
        pickUpColList.add(xCol);
      } else if (!xValid && yValid) {
        if (yIndex.equals(yCol.getGenericDimensionIndex())) {
          return null;
        }
        pickUpColList.add(yCol);
      } else if (xValid && yValid) {
        if (xIndex.equals(xCol.getGenericDimensionIndex())) {
          return null;
        }
        pickUpColList.add(xCol);
        if (yIndex.equals(yCol.getGenericDimensionIndex())) {
          return null;
        }
        pickUpColList.add(yCol);
      }
    } else if (xCol != null) {
      Integer xIndex = xCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
      final boolean xValid = isValidPickUpValue(xIndex);
      if (xValid) {
        if (xIndex.equals(xCol.getGenericDimensionIndex())) {
          return null;
        }
        pickUpColList.add(xCol);
      }
    } else if (yCol != null) {
      Integer yIndex = yCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
      final boolean yValid = isValidPickUpValue(yIndex);
      if (yValid) {
        if (yIndex.equals(yCol.getGenericDimensionIndex())) {
          return null;
        }
        pickUpColList.add(yCol);
      }
    }
    return pickUpColList;
  }

  static boolean isValidTimeValue(Integer value) {
    return (value != null) && !Integer.valueOf(-1).equals(value);
  }

  static boolean isValidPickUpValue(Integer value) {
    return (value != null) && !Integer.valueOf(-1).equals(value);
  }

  public static boolean hasEqualInput(SGDataColumnInfo[] a1, SGDataColumnInfo[] a2) {
    if (a1 == null || a2 == null) {
      throw new IllegalArgumentException("a1 == null || a2 == null");
    }
    if (a1.length != a2.length) {
      return false;
    }
    for (int ii = 0; ii < a1.length; ii++) {
      SGDataColumnInfo c1 = a1[ii];
      SGDataColumnInfo c2 = a2[ii];
      if (!c1.getClass().equals(c2.getClass())) {
        return false;
      }
      if (!SGUtility.equals(c1.getColumnType(), c2.getColumnType())) {
        return false;
      }
      if (c1 instanceof SGMDArrayDataColumnInfo) {
        SGMDArrayDataColumnInfo nc1 = (SGMDArrayDataColumnInfo) c1;
        SGMDArrayDataColumnInfo nc2 = (SGMDArrayDataColumnInfo) c2;
        if (!nc1.getDimensionIndices().equals(nc2.getDimensionIndices())) {
          return false;
        }
      }
    }
    return true;
  }

  public static int getNetCDFDataIndexLength(SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, INDEX);
    SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colList.get(0);
    final int len = col.getDimension(0).getLength();
    return len;
  }

  private static int getTwoDimensionNetCDFDataLength(SGDataColumnInfo[] cols, String key) {
    List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, key);
    SGNetCDFDataColumnInfo col = (SGNetCDFDataColumnInfo) colList.get(0);
    return col.getDimension(0).getLength();
  }

  private static int getVXYMDArrayDataXYLength(
      SGDataColumnInfo[] cols, String columnType, String key, final boolean polar) {
    int len = -1;
    List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, columnType);
    String fName = polar ? MAGNITUDE : X_COMPONENT;
    List<SGDataColumnInfo> fColList = findColumnsWithColumnType(cols, fName);
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

  public static int getVXYMDArrayDataComponentGenericDimensionLength(
      SGDataColumnInfo[] cols, final boolean polar) {
    String fName = polar ? MAGNITUDE : X_COMPONENT;
    List<SGDataColumnInfo> fColList = findColumnsWithColumnType(cols, fName);
    if (fColList.size() != 1) {
      return -1;
    }
    SGMDArrayDataColumnInfo fCol = (SGMDArrayDataColumnInfo) fColList.get(0);
    return fCol.getGenericDimensionLength();
  }

  public static int getVXYMDArrayDataYLength(SGDataColumnInfo[] cols, final boolean polar) {
    return getVXYMDArrayDataXYLength(cols, Y_COORDINATE, KEY_VXY_Y_DIMENSION, polar);
  }

  public static int getVXYMDArrayDataXLength(SGDataColumnInfo[] cols, final boolean polar) {
    return getVXYMDArrayDataXYLength(cols, X_COORDINATE, KEY_VXY_X_DIMENSION, polar);
  }

  public static int getVXYNetCDFDataYLength(SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, Y_COORDINATE);
  }

  public static int getVXYNetCDFDataXLength(SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, X_COORDINATE);
  }

  private static int getSXYZMDArrayDataXYLength(
      SGDataColumnInfo[] cols, String columnType, String key) {
    int len = -1;
    List<SGDataColumnInfo> colList = findColumnsWithColumnType(cols, columnType);
    if (colList.size() == 1) {
      SGMDArrayDataColumnInfo col = (SGMDArrayDataColumnInfo) colList.get(0);
      len = col.getGenericDimensionLength();
    } else {
      List<SGDataColumnInfo> zColList = findColumnsWithColumnType(cols, Z_VALUE);
      SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
      int[] zDims = zCol.getDimensions();
      Integer dim = zCol.getDimensionIndex(key);
      if (dim != null) {
        len = zDims[dim];
      }
    }
    return len;
  }

  public static int getSXYZMDArrayDataZGenericDimensionLength(SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> zColList = findColumnsWithColumnType(cols, Z_VALUE);
    if (zColList.size() != 1) {
      return -1;
    }
    SGMDArrayDataColumnInfo zCol = (SGMDArrayDataColumnInfo) zColList.get(0);
    return zCol.getGenericDimensionLength();
  }

  public static int getSXYZMDArrayDataYLength(SGDataColumnInfo[] cols) {
    return getSXYZMDArrayDataXYLength(cols, Y_VALUE, KEY_SXYZ_Y_DIMENSION);
  }

  public static int getSXYZMDArrayDataXLength(SGDataColumnInfo[] cols) {
    return getSXYZMDArrayDataXYLength(cols, X_VALUE, KEY_SXYZ_X_DIMENSION);
  }

  public static int getSXYZNetCDFDataYLength(SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, Y_VALUE);
  }

  public static int getSXYZNetCDFDataXLength(SGDataColumnInfo[] cols) {
    return getTwoDimensionNetCDFDataLength(cols, X_VALUE);
  }

  public static int getSXYMDArrayDataLength(SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> xColList = findColumnsWithColumnType(cols, X_VALUE);
    List<SGDataColumnInfo> yColList = findColumnsWithColumnType(cols, Y_VALUE);
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

  public static int getSXYNetCDFDataLength(SGDataColumnInfo[] cols) {
    final int len;
    List<SGDataColumnInfo> indexColList = findColumnsWithColumnType(cols, INDEX);
    if (indexColList.size() == 0) {
      List<SGDataColumnInfo> xColList = findColumnsWithColumnType(cols, X_VALUE);
      List<SGDataColumnInfo> yColList = findColumnsWithColumnType(cols, Y_VALUE);
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

  public static List<SGDataColumnInfo> findColumnsWithValueType(
      SGDataColumnInfo[] cols, final String valueType) {
    List<SGDataColumnInfo> colList = new ArrayList<SGDataColumnInfo>();
    for (int ii = 0; ii < cols.length; ii++) {
      SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
      if (ncInfo.getValueType().equalsIgnoreCase(valueType)) {
        colList.add(ncInfo);
      }
    }
    return colList;
  }

  public static SGDataColumnInfo findColumnWithName(SGDataColumnInfo[] cols, final String name) {
    for (int ii = 0; ii < cols.length; ii++) {
      SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
      if (ncInfo.getName().equalsIgnoreCase(name)) {
        return ncInfo;
      }
    }
    return null;
  }

  public static List<SGDataColumnInfo> findColumnsWithColumnTypeStartsWith(
      SGDataColumnInfo[] cols, final String columnType) {
    List<SGDataColumnInfo> colList = new ArrayList<SGDataColumnInfo>();
    for (int ii = 0; ii < cols.length; ii++) {
      SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
      if (ncInfo.getColumnType().toUpperCase().startsWith(columnType.toUpperCase())) {
        colList.add(ncInfo);
      }
    }
    return colList;
  }

  public static List<SGDataColumnInfo> findColumnsWithColumnType(
      SGDataColumnInfo[] cols, final String columnType) {
    List<SGDataColumnInfo> colList = new ArrayList<SGDataColumnInfo>();
    for (int ii = 0; ii < cols.length; ii++) {
      SGDataColumnInfo ncInfo = (SGDataColumnInfo) cols[ii];
      if (ncInfo.getColumnType().equalsIgnoreCase(columnType)) {
        colList.add(ncInfo);
      }
    }
    return colList;
  }

  public static boolean hasIndexColumnType(SGDataColumnInfo[] cols) {
    List<SGDataColumnInfo> indexColumnList = findColumnsWithColumnType(cols, INDEX);
    if (indexColumnList.size() == 1) {
      return true;
    } else {
      List<SGDataColumnInfo> serialNumberColumnList =
          findColumnsWithColumnType(cols, SERIAL_NUMBERS);
      return (serialNumberColumnList.size() == 1);
    }
  }

  public static SGMDArrayDataColumnInfo getMDArrayPickUpColumn(SGDataColumnInfo[] cols) {
    boolean valid = true;
    SGMDArrayDataColumnInfo pickUpCol = null;
    int cnt = 0;
    for (int ii = 0; ii < cols.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
      Integer dim = mdCol.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
      if (dim != null && dim != -1) {
        pickUpCol = mdCol;
        cnt++;
      }
    }
    if (cnt > 1) {
      valid = false;
    }
    if (!valid) {
      return null;
    }
    return pickUpCol;
  }

  public static String getHolderName(SGDataColumnInfo info) {
    String columnType = info.getColumnType();
    final int lastIndex = columnType.lastIndexOf(MID_COLUMN);
    String lastStr = columnType.substring(lastIndex + MID_COLUMN.length(), columnType.length());
    return lastStr;
  }

  /**
   * @param colInfo
   * @return true if colInfo has PICKUP column type.
   */
  public static boolean isPickupColumnContained(final SGDataColumnInfo[] colInfo) {
    for (int ii = 0; ii < colInfo.length; ii++) {
      if (colInfo[ii] instanceof SGNetCDFDataColumnInfo) {
        if (PICKUP.equals(colInfo[ii].getColumnType())) {
          return true;
        }
      } else if (colInfo[ii] instanceof SGMDArrayDataColumnInfo) {
        SGMDArrayDataColumnInfo mdInfo = (SGMDArrayDataColumnInfo) colInfo[ii];
        Integer pickUpDimension = mdInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          return true;
        }
      }
    }
    return false;
  }
}
