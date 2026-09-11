package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.data.SGDataBufferUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnInfoUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTitleUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataMiscUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataRangeUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataTextUtility.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataViewerUtility.*;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGIConstants;
import jp.riken.brain.ni.samuraigraph.base.SGInteger;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityNumber;

/** Static helper for the Stride responsibility. */
public final class SGDataStrideUtility
    implements SGIDataColumnTypeConstants,
        SGIDataPropertyKeyConstants,
        SGINetCDFConstants,
        SGIMDArrayConstants {
  // Default stride constants.
  static final int DEFAULT_MULTIPLE_DIMENSION_NUM = 4;

  static final int MAGNITUDE_PER_CM_MINIMAL_ORDER = 3;

  private SGDataStrideUtility() {}

  /**
   * Calculates the initial magnitude of the vector per centimeter.
   *
   * @param data the vector data
   * @return the initial magnitude of the vector per centimeter
   */
  public static float getInitialMagnitudePerCM(SGIVXYTypeData data) {
    final double[] mArray = data.getMagnitudeArray(false);
    final double max = SGUtilityNumber.max(mArray);
    if (Double.isNaN(max) || max <= 0.0) {
      // Returns the default magnitude of a vector per centimeter.
      return 1.0f;
    }
    final float magPerCm = (float) max;
    final float magPerCmReduced = roundMagnitudePerCM(magPerCm, max);
    return magPerCmReduced;
  }

  /**
   * Rounds the input value of the magnitude per centimeter of vector type data using the maximum
   * length of vectors.
   *
   * @param magPerCm the magnitude per centimeter of vector type data
   * @param data the vector type data
   * @return rounded value
   */
  public static float roundMagnitudePerCM(final float magPerCm, final SGIVXYTypeData data) {
    if (Float.isNaN(magPerCm)) {
      return Float.NaN;
    }
    final double[] mArray = data.getMagnitudeArray(false);
    final double max = SGUtilityNumber.max(mArray);
    if (Double.isNaN(max) || max <= 0.0) {
      return magPerCm;
    }
    final float magPerCmReduced = roundMagnitudePerCM(magPerCm, max);
    return magPerCmReduced;
  }

  private static float roundMagnitudePerCM(final float mag, final double max) {
    float magNew =
        (float)
            SGUtilityNumber.getNumberInNumberOrder(
                mag, max, MAGNITUDE_PER_CM_MINIMAL_ORDER, RoundingMode.HALF_UP.ordinal());
    return magNew;
  }

  private static int calcVectorStep(SGTuple2f figureSize, final boolean bx, final int len) {
    int step;
    if (figureSize != null) {
      final float fSizePt = bx ? figureSize.x : figureSize.y;
      final float fSizeCm = fSizePt * SGIConstants.CM_POINT_RATIO;
      final int fSize = (int) fSizeCm;
      step = len / fSize;
      if (len % fSize != 0) {
        step += +1;
      }
    } else {
      step = 1;
    }
    return step;
  }

  private static SGIntegerSeriesSet calcStride(final int len, final int fSize) {
    final int end = len - 1;
    int step = len / fSize;
    if (len % fSize != 0) {
      step += +1;
    }
    return new SGIntegerSeriesSet(0, end, step);
  }

  /**
   * Returns the column type for the second component.
   *
   * @param polar true for the polar type data
   * @return the column type for the second component
   */
  public static String getVXYSecondComponentColumnType(final boolean polar) {
    return polar ? ANGLE : Y_COMPONENT;
  }

  /**
   * Returns the column type for the second component.
   *
   * @param infoMap the information map
   * @return the column type for the second component
   */
  public static String getVXYSecondComponentColumnType(Map<String, Object> infoMap) {
    Boolean polar = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
    if (polar == null) {
      return null;
    }
    return getVXYSecondComponentColumnType(polar.booleanValue());
  }

  /**
   * Returns the column type for the first component.
   *
   * @param polar true for the polar type data
   * @return the column type for the first component
   */
  public static String getVXYFirstComponentColumnType(final boolean polar) {
    return polar ? MAGNITUDE : X_COMPONENT;
  }

  /**
   * Returns the column type for the first component.
   *
   * @param infoMap the information map
   * @return the column type for the first component
   */
  public static String getVXYFirstComponentColumnType(Map<String, Object> infoMap) {
    Boolean polar = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED);
    if (polar == null) {
      return null;
    }
    return getVXYFirstComponentColumnType(polar.booleanValue());
  }

  /**
   * Calculates the series of dimension steps.
   *
   * @param dataList a list of data
   * @return the indices of dimension
   */
  public static SGIntegerSeriesSet getDimensionSeries(List<SGData> dataList, final int len) {

    Set<Integer> indexSet = new TreeSet<Integer>();
    for (int ii = 0; ii < dataList.size(); ii++) {
      SGData data = dataList.get(ii);
      if (!(data instanceof SGISXYMultipleDimensionData)) {
        continue;
      }
      SGISXYMultipleDimensionData dataMulti = (SGISXYMultipleDimensionData) data;
      SGIntegerSeriesSet indices = dataMulti.getIndices();
      int[] indexArray = indices.getNumbers();
      for (int jj = 0; jj < indexArray.length; jj++) {
        indexSet.add(indexArray[jj]);
      }
    }

    SGIntegerSeriesSet ret = new SGIntegerSeriesSet();
    int[] indices = new int[indexSet.size()];
    Iterator<Integer> itr = indexSet.iterator();
    int cnt = 0;
    while (itr.hasNext()) {
      Integer index = itr.next();
      indices[cnt] = index;
      cnt++;
    }
    List<SGIntegerSeries> seriesList = SGIntegerSeries.createList(indices);
    for (SGIntegerSeries series : seriesList) {
      if (len > 0) {
        SGInteger start = series.getStart();
        SGInteger end = series.getEnd();
        SGInteger step = series.getStep();
        Integer nStart = start.getNumber();
        Integer nEnd = end.getNumber();
        Integer nStep = step.getNumber();
        if (nStart != null && SGUtility.equals(nStart, len - 1)) {
          start = new SGInteger(nStart, SGIntegerSeries.ARRAY_INDEX_END);
        }
        if (nEnd != null && SGUtility.equals(nEnd, len - 1)) {
          end = new SGInteger(nEnd, SGIntegerSeries.ARRAY_INDEX_END);
        }
        if (nStep != null && SGUtility.equals(nStep, len - 1)) {
          step = new SGInteger(nStep, SGIntegerSeries.ARRAY_INDEX_END);
        }
        series = new SGIntegerSeries(start, end, step);
      }
      ret.add(series);
    }

    return ret;
  }

  public static SGIntegerSeries createDefaultStepSeries(final int len) {
    final int nEnd = len - 1;
    final int nStep;
    if (len <= DEFAULT_MULTIPLE_DIMENSION_NUM) {
      nStep = 1;
    } else {
      nStep = len / DEFAULT_MULTIPLE_DIMENSION_NUM;
    }
    SGIntegerSeries series = new SGIntegerSeries(0, nEnd, nStep);
    return series;
  }

  private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
      SGDataColumnInfo[] colArray,
      String dataType,
      final int fSize,
      String cType,
      String dimName,
      String key,
      Map<String, SGMDArrayDimensionInfo> dimNameMap) {
    int len = -1;
    int cnt = 0;
    String datasetName = null;
    int index = -1;
    for (int ii = 0; ii < colArray.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
      String colType = mdCol.getColumnType();
      if (cType.equals(colType)) {
        datasetName = mdCol.getName();
        Integer dimIndex = mdCol.getDimensionIndex(dimName);
        if (dimIndex == null) {
          return null;
        }
        if (dimIndex == -1) {
          return null;
        }
        index = dimIndex.intValue();
        len = mdCol.getDimensions()[index];
        cnt++;
        if (cnt > 1) {
          break;
        }
      }
    }
    if (cnt != 1) {
      return null;
    }

    // creates an instance of stride
    SGIntegerSeriesSet stride = calcStride(len, fSize);

    // creates returned value
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    map.put(key, stride);

    // set the output
    SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo(datasetName, index);
    dimNameMap.put(key, info);

    return map;
  }

  private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
      SGDataColumnInfo[] colArray,
      String dataType,
      final int fSize,
      String cType,
      String key,
      Map<String, SGMDArrayDimensionInfo> dimNameMap) {
    int dimLen = -1;
    String datasetName = null;
    int index = -1;
    for (int ii = 0; ii < colArray.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) colArray[ii];
      String colType = mdCol.getColumnType();
      if (cType.equals(colType)) {
        datasetName = mdCol.getName();
        index = mdCol.getGenericDimensionIndex();
        final int len = mdCol.getGenericDimensionLength();
        if (len == -1) {
          return null;
        }
        if (dimLen == -1) {
          dimLen = len;
        } else {
          if (len != dimLen) {
            return null;
          }
        }
      }
    }
    if (dimLen == -1) {
      return null;
    }

    // creates an instance of stride
    SGIntegerSeriesSet stride = calcStride(dimLen, fSize);

    // creates returned value
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    map.put(key, stride);

    // set the output
    SGMDArrayDimensionInfo info = new SGMDArrayDimensionInfo(datasetName, index);
    dimNameMap.put(key, info);

    return map;
  }

  private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
      SGDataColumnInfo[] colArray,
      String dataType,
      SGTuple2f figureSize,
      String cType,
      String dimName,
      String key,
      final boolean bx,
      Map<String, SGMDArrayDimensionInfo> dimNameMap) {
    final int fSize = (int) (bx ? figureSize.x : figureSize.y);
    return calcMDArrayStride(colArray, dataType, fSize, cType, dimName, key, dimNameMap);
  }

  private static Map<String, SGIntegerSeriesSet> calcMDArrayStride(
      SGDataColumnInfo[] colArray,
      String dataType,
      SGTuple2f figureSize,
      String cType,
      String key,
      final boolean bx,
      Map<String, SGMDArrayDimensionInfo> dimNameMap) {
    final int fSize = (int) (bx ? figureSize.x : figureSize.y);
    return calcMDArrayStride(colArray, dataType, fSize, cType, key, dimNameMap);
  }

  /**
   * Calculates default stride of data arrays.
   *
   * @param colArray an array of data information
   * @param infoMap the information map
   * @param dimNameMap the map of dimension name (output)
   * @return map of the stride of data arrays
   */
  public static Map<String, SGIntegerSeriesSet> calcMDArrayDefaultStride(
      SGDataColumnInfo[] colArray,
      Map<String, Object> infoMap,
      Map<String, SGMDArrayDimensionInfo> dimNameMap) {

    SGTuple2f figureSize = (SGTuple2f) infoMap.get(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE);
    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    Map<String, SGIntegerSeriesSet> map = null;

    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      Map<String, SGIntegerSeriesSet> xMap =
          calcMDArrayStride(
              colArray,
              dataType,
              figureSize,
              X_VALUE,
              SGIDataInformationKeyConstants.KEY_SXY_STRIDE,
              true,
              dimNameMap);
      Map<String, SGIntegerSeriesSet> yMap =
          calcMDArrayStride(
              colArray,
              dataType,
              figureSize,
              Y_VALUE,
              SGIDataInformationKeyConstants.KEY_SXY_STRIDE,
              false,
              dimNameMap);
      if (xMap == null && yMap == null) {
        return null;
      } else if (xMap != null && yMap != null) {
        map = xMap;
      } else {
        if (xMap != null) {
          map = xMap;
        } else {
          map = yMap;
        }
      }
      // SGIntegerSeriesSet tickLabelStride =
      // map.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
      // map.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, (SGIntegerSeriesSet)
      // tickLabelStride.clone());

    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      map = new HashMap<String, SGIntegerSeriesSet>();
      Boolean grid = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXYZ_GRID_PLOT_FLAG);
      if (grid) {
        Map<String, SGIntegerSeriesSet> xMap =
            calcMDArrayStride(
                colArray,
                dataType,
                figureSize,
                X_VALUE,
                SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X,
                true,
                dimNameMap);
        if (xMap != null) {
          map.putAll(xMap);
        } else {
          xMap =
              calcMDArrayStride(
                  colArray,
                  dataType,
                  figureSize,
                  Z_VALUE,
                  SGIMDArrayConstants.KEY_SXYZ_X_DIMENSION,
                  SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X,
                  true,
                  dimNameMap);
          if (xMap != null) {
            map.putAll(xMap);
          }
        }
        Map<String, SGIntegerSeriesSet> yMap =
            calcMDArrayStride(
                colArray,
                dataType,
                figureSize,
                Y_VALUE,
                SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y,
                false,
                dimNameMap);
        if (yMap != null) {
          map.putAll(yMap);
        } else {
          yMap =
              calcMDArrayStride(
                  colArray,
                  dataType,
                  figureSize,
                  Z_VALUE,
                  SGIMDArrayConstants.KEY_SXYZ_Y_DIMENSION,
                  SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y,
                  false,
                  dimNameMap);
          if (yMap != null) {
            map.putAll(yMap);
          }
        }

      } else {
        String cType = null;
        List<SGDataColumnInfo> zColList = findColumnsWithColumnType(colArray, Z_VALUE);
        if (zColList.size() == 1) {
          cType = Z_VALUE;
        }
        if (cType == null) {
          List<SGDataColumnInfo> xColList = findColumnsWithColumnType(colArray, X_VALUE);
          if (xColList.size() == 1) {
            cType = X_VALUE;
          }
        }
        if (cType == null) {
          List<SGDataColumnInfo> yColList = findColumnsWithColumnType(colArray, Y_VALUE);
          if (yColList.size() == 1) {
            cType = Y_VALUE;
          }
        }
        if (cType == null) {
          return null;
        }
        final boolean bx = (figureSize.x > figureSize.y);
        map =
            calcMDArrayStride(
                colArray,
                dataType,
                figureSize,
                cType,
                SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE,
                bx,
                dimNameMap);
      }

    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      final String fType, sType;
      if (SGDataMiscUtility.isPolar(infoMap)) {
        fType = MAGNITUDE;
        sType = ANGLE;
      } else {
        fType = X_COMPONENT;
        sType = Y_COMPONENT;
      }
      map = new HashMap<String, SGIntegerSeriesSet>();
      Boolean grid = (Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_GRID_PLOT_FLAG);
      if (grid) {
        map =
            calcMDArrayVectorStride(
                colArray,
                infoMap,
                dimNameMap,
                dataType,
                figureSize,
                X_COORDINATE,
                Y_COORDINATE,
                fType,
                sType,
                SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X,
                SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
      } else {
        String cType = null;
        List<SGDataColumnInfo> fColList = findColumnsWithColumnType(colArray, fType);
        if (fColList.size() == 1) {
          cType = fType;
        }
        if (cType == null) {
          List<SGDataColumnInfo> sColList = findColumnsWithColumnType(colArray, sType);
          if (sColList.size() == 1) {
            cType = sType;
          }
        }
        if (cType == null) {
          List<SGDataColumnInfo> xColList = findColumnsWithColumnType(colArray, X_COORDINATE);
          if (xColList.size() == 1) {
            cType = X_COORDINATE;
          }
        }
        if (cType == null) {
          List<SGDataColumnInfo> yColList = findColumnsWithColumnType(colArray, Y_COORDINATE);
          if (yColList.size() == 1) {
            cType = Y_COORDINATE;
          }
        }
        if (cType == null) {
          return null;
        }
        final boolean bx = (figureSize.x > figureSize.y);
        map =
            calcMDArrayStride(
                colArray,
                dataType,
                figureSize,
                cType,
                SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE,
                bx,
                dimNameMap);
      }
    }

    return map;
  }

  private static Map<String, SGIntegerSeriesSet> calcVectorStride(
      SGDataColumnInfo[] colArray,
      Map<String, Object> infoMap,
      Map<String, String> dimNameMap,
      String dataType,
      SGTuple2f figureSize,
      List<String> cTypeXList,
      String xKey,
      List<String> cTypeYList,
      String yKey) {

    SGNetCDFDataColumnInfo xCol = null;
    SGNetCDFDataColumnInfo yCol = null;
    int xCnt = 0;
    int yCnt = 0;
    for (int ii = 0; ii < colArray.length; ii++) {
      SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) colArray[ii];
      String colType = nCol.getColumnType();
      if (cTypeXList.contains(colType)) {
        if (nCol.isCoordinateVariable()) {
          xCol = nCol;
          xCnt++;
          if (xCnt > 1) {
            break;
          }
        }
      } else if (cTypeYList.contains(colType)) {
        if (nCol.isCoordinateVariable()) {
          yCol = nCol;
          yCnt++;
          if (yCnt > 1) {
            break;
          }
        }
      }
    }
    if (xCol == null || yCol == null || xCnt != 1 || yCnt != 1) {
      return null;
    }

    SGDimensionInfo xDim = xCol.getDimension(0);
    SGDimensionInfo yDim = yCol.getDimension(0);
    final int xLen = xDim.getLength();
    final int yLen = yDim.getLength();

    // calculates the step of stride
    final int xStep = calcVectorStep(figureSize, true, xLen);
    final int yStep = calcVectorStep(figureSize, false, yLen);

    // creates returned value
    final int xEnd = xLen - 1;
    final int yEnd = yLen - 1;
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    map.put(xKey, new SGIntegerSeriesSet(0, xEnd, xStep));
    map.put(yKey, new SGIntegerSeriesSet(0, yEnd, yStep));

    // updates the output
    dimNameMap.put(xKey, xDim.getName());
    dimNameMap.put(yKey, yDim.getName());

    return map;
  }

  private static Map<String, SGIntegerSeriesSet> calcNetCDFStride(
      SGDataColumnInfo[] colArray,
      String dataType,
      final int fSize,
      List<String> cTypeList,
      String key,
      Map<String, String> dimNameMap) {

    int len = -1;
    int cnt = 0;
    String dimName = null;
    for (int ii = 0; ii < colArray.length; ii++) {
      SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) colArray[ii];
      String colType = nCol.getColumnType();

      // Only for backward compatibility <= 2.0.0
      if (SERIAL_NUMBERS.equalsIgnoreCase(colType)) {
        colType = INDEX;
      }

      if (cTypeList.contains(colType)) {
        if (nCol.isCoordinateVariable()) {
          len = nCol.getDimension(0).getLength();
          dimName = nCol.getName();
          cnt++;
          if (cnt > 1) {
            break;
          }
        }
      }
    }
    if (cnt != 1) {
      return null;
    }

    // creates an instance of stride
    SGIntegerSeriesSet stride = calcStride(len, fSize);

    // creates returned value
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    map.put(key, stride);

    // set the output
    dimNameMap.put(key, dimName);

    return map;
  }

  private static Map<String, SGIntegerSeriesSet> calcNetCDFStride(
      SGDataColumnInfo[] colArray,
      String dataType,
      SGTuple2f figureSize,
      List<String> cTypeList,
      String key,
      final boolean bx,
      Map<String, String> dimNameMap) {
    final int fSize = (int) (bx ? figureSize.x : figureSize.y);
    return calcNetCDFStride(colArray, dataType, fSize, cTypeList, key, dimNameMap);
  }

  public static Map<String, SGIntegerSeriesSet> calcSDArrayDefaultStride(
      SGDataColumnInfo[] colArray, Map<String, Object> infoMap) {
    SGTuple2f figureSize = (SGTuple2f) infoMap.get(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE);
    final int fSize = (int) (figureSize.x > figureSize.y ? figureSize.x : figureSize.y);
    SGSDArrayDataColumnInfo sdInfo = (SGSDArrayDataColumnInfo) colArray[0];
    final int len = sdInfo.getLength();
    SGIntegerSeriesSet stride = calcStride(len, fSize);
    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      map.put(SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE, stride);
    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      map.put(SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE, stride);
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      map.put(SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE, stride);
    }
    return map;
  }

  /**
   * Calculates default stride of data arrays.
   *
   * @param colArray an array of data information
   * @param infoMap the information map
   * @param dimNameMap the map of dimension name (output)
   * @return map of the stride of data arrays
   */
  public static Map<String, SGIntegerSeriesSet> calcNetCDFDefaultStride(
      SGDataColumnInfo[] colArray, Map<String, Object> infoMap, Map<String, String> dimNameMap) {

    SGTuple2f figureSize = (SGTuple2f) infoMap.get(SGIDataInformationKeyConstants.KEY_FIGURE_SIZE);
    String dataType = (String) infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE);
    Map<String, SGIntegerSeriesSet> map = null;
    final boolean hasIndex = hasIndexColumnType(colArray);
    final boolean bx = (figureSize.x > figureSize.y);
    List<String> cTypeList = new ArrayList<String>();

    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      if (hasIndex) {
        cTypeList.add(INDEX);
        map =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE,
                bx,
                dimNameMap);
      } else {
        cTypeList.add(X_VALUE);
        Map<String, SGIntegerSeriesSet> xMap =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_SXY_STRIDE,
                true,
                dimNameMap);
        cTypeList.clear();
        cTypeList.add(Y_VALUE);
        Map<String, SGIntegerSeriesSet> yMap =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_SXY_STRIDE,
                false,
                dimNameMap);
        if ((xMap != null && yMap != null) || (xMap == null && yMap == null)) {
          return null;
        }
        if (xMap != null) {
          map = xMap;
        } else {
          map = yMap;
        }
        // SGIntegerSeriesSet tickLabelStride =
        // map.get(SGIDataInformationKeyConstants.KEY_SXY_STRIDE);
        // map.put(SGIDataInformationKeyConstants.KEY_SXY_STRIDE, (SGIntegerSeriesSet)
        // tickLabelStride.clone());
      }

    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      if (hasIndex) {
        cTypeList.add(INDEX);
        map =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE,
                bx,
                dimNameMap);
      } else {
        cTypeList.add(X_VALUE);
        cTypeList.add(X_INDEX);
        map = new HashMap<String, SGIntegerSeriesSet>();
        Map<String, SGIntegerSeriesSet> xMap =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_X,
                true,
                dimNameMap);
        if (xMap != null) {
          map.putAll(xMap);
        }
        cTypeList.clear();
        cTypeList.add(Y_VALUE);
        cTypeList.add(Y_INDEX);
        Map<String, SGIntegerSeriesSet> yMap =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y,
                false,
                dimNameMap);
        if (yMap != null) {
          map.putAll(yMap);
        }
      }

    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      if (hasIndex) {
        cTypeList.add(INDEX);
        map =
            calcNetCDFStride(
                colArray,
                dataType,
                figureSize,
                cTypeList,
                SGIDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE,
                bx,
                dimNameMap);
      } else {
        List<String> cTypeXList = new ArrayList<String>();
        cTypeXList.add(X_COORDINATE);
        cTypeXList.add(X_INDEX);
        List<String> cTypeYList = new ArrayList<String>();
        cTypeYList.add(Y_COORDINATE);
        cTypeYList.add(Y_INDEX);
        map =
            calcVectorStride(
                colArray,
                infoMap,
                dimNameMap,
                dataType,
                figureSize,
                cTypeXList,
                SGIDataInformationKeyConstants.KEY_VXY_STRIDE_X,
                cTypeYList,
                SGIDataInformationKeyConstants.KEY_VXY_STRIDE_Y);
      }
    }

    return map;
  }

  public static Map<String, SGIntegerSeriesSet> calcMDArrayDefaultStride(
      SGDataColumnInfo[] colArray, Map<String, Object> infoMap) {
    return calcMDArrayDefaultStride(
        colArray, infoMap, new HashMap<String, SGMDArrayDimensionInfo>());
  }

  public static Map<String, SGIntegerSeriesSet> calcNetCDFDefaultStride(
      SGDataColumnInfo[] colArray, Map<String, Object> infoMap) {
    return calcNetCDFDefaultStride(colArray, infoMap, new HashMap<String, String>());
  }

  private static Map<String, SGIntegerSeriesSet> calcMDArrayVectorStride(
      SGDataColumnInfo[] colArray,
      Map<String, Object> infoMap,
      Map<String, SGMDArrayDimensionInfo> dimNameMap,
      String dataType,
      SGTuple2f figureSize,
      String xCType,
      String yCType,
      String fCType,
      String sCType,
      String xKey,
      String yKey) {

    SGMDArrayDataColumnInfo xCol = null;
    SGMDArrayDataColumnInfo yCol = null;
    int xCnt = 0;
    int yCnt = 0;
    for (int ii = 0; ii < colArray.length; ii++) {
      SGMDArrayDataColumnInfo nCol = (SGMDArrayDataColumnInfo) colArray[ii];
      String colType = nCol.getColumnType();
      if (xCType.equals(colType)) {
        xCol = nCol;
        xCnt++;
        if (xCnt > 1) {
          break;
        }
      } else if (yCType.equals(colType)) {
        yCol = nCol;
        yCnt++;
        if (yCnt > 1) {
          break;
        }
      }
    }
    if (xCnt > 1 || yCnt > 1) {
      return null;
    }

    String xName = null;
    String yName = null;
    int xIndex = -1;
    int yIndex = -1;
    int xLen = -1;
    int yLen = -1;
    if (xCol != null) {
      xName = xCol.getName();
      xIndex = xCol.getGenericDimensionIndex();
      xLen = xCol.getGenericDimensionLength();
      if (xLen == -1) {
        return null;
      }
    } else {
      for (int ii = 0; ii < colArray.length; ii++) {
        SGMDArrayDataColumnInfo nCol = (SGMDArrayDataColumnInfo) colArray[ii];
        String colType = nCol.getColumnType();
        if (fCType.equals(colType)) {
          Integer index = nCol.getDimensionIndex(KEY_VXY_X_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(index)) {
            return null;
          }
          xName = nCol.getName();
          xIndex = index;
          xLen = nCol.getDimensions()[index];
          break;
        }
      }
    }
    if (yCol != null) {
      yName = yCol.getName();
      yIndex = yCol.getGenericDimensionIndex();
      yLen = yCol.getGenericDimensionLength();
      if (yLen == -1) {
        return null;
      }
    } else {
      for (int ii = 0; ii < colArray.length; ii++) {
        SGMDArrayDataColumnInfo nCol = (SGMDArrayDataColumnInfo) colArray[ii];
        String colType = nCol.getColumnType();
        if (fCType.equals(colType)) {
          Integer index = nCol.getDimensionIndex(KEY_VXY_Y_DIMENSION);
          if (!SGDataDataTypeUtility.isValidDimensionIndex(index)) {
            return null;
          }
          yName = nCol.getName();
          yIndex = index;
          yLen = nCol.getDimensions()[index];
          break;
        }
      }
    }

    // calculates the step of stride
    final int xStep = calcVectorStep(figureSize, true, xLen);
    final int yStep = calcVectorStep(figureSize, false, yLen);

    // creates returned value
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    if (xLen > 0) {
      final int xEnd = xLen - 1;
      map.put(xKey, new SGIntegerSeriesSet(0, xEnd, xStep));
    }
    if (yLen > 0) {
      final int yEnd = yLen - 1;
      map.put(yKey, new SGIntegerSeriesSet(0, yEnd, yStep));
    }

    // set the output
    SGMDArrayDimensionInfo xInfo = new SGMDArrayDimensionInfo(xName, xIndex);
    dimNameMap.put(xKey, xInfo);
    SGMDArrayDimensionInfo yInfo = new SGMDArrayDimensionInfo(yName, yIndex);
    dimNameMap.put(yKey, yInfo);

    return map;
  }
}
