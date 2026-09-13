package jp.riken.brain.ni.samuraigraph.figure;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.data.*;
import jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants;
import ucar.nc2.Dimension;

class SGColumnTypeUpdater {

  private final SGElementGroupSetInGraphSXYMultiple owner;

  public SGColumnTypeUpdater(final SGElementGroupSetInGraphSXYMultiple owner) {
    this.owner = owner;
  }

  void setPickUpAndTimeDimension(
      SGPropertyResults result, final String pickUpValue, final String timeValue) {

    if (!(owner.mData instanceof SGSXYMDArrayMultipleData)) {
      return;
    }
    SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) owner.mData;

    boolean pickUpValid = true;
    boolean timeValid = true;

    // creates dimension map
    Map<String, Integer> pickUpDimMap = owner.createPickUpDimMap(sxyData, pickUpValue);
    if (pickUpValid && pickUpDimMap != null) {
      if (!owner.setPickUpDimension(pickUpDimMap)) {
        pickUpValid = false;
      }
    } else {
      pickUpValid = false;
    }

    Map<String, Integer> timeDimMap = owner.createTimeDimMap(sxyData, timeValue);
    if (pickUpDimMap != null && timeDimMap != null) {
      // checks overlapping
      Iterator<String> keyItr = timeDimMap.keySet().iterator();
      while (keyItr.hasNext()) {
        String name = keyItr.next();
        Integer timeIndex = timeDimMap.get(name);
        if (timeIndex == null || timeIndex == -1) {
          continue;
        }
        Integer pickUpIndex = pickUpDimMap.get(name);
        if (timeIndex.equals(pickUpIndex)) {
          timeValid = false;
          break;
        }
      }
    }
    if (timeValid && timeDimMap != null) {
      if (!owner.setTimeDimension(timeDimMap)) {
        timeValid = false;
      }
    } else {
      timeValid = false;
    }

    // sets the status
    final int pickUpStatus =
        pickUpValid ? SGPropertyResults.SUCCEEDED : SGPropertyResults.INVALID_INPUT_VALUE;
    result.putResult(SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION, pickUpStatus);

    final int timeStatus =
        timeValid ? SGPropertyResults.SUCCEEDED : SGPropertyResults.INVALID_INPUT_VALUE;
    result.putResult(SGDataCommandConstants.COM_DATA_ANIMATION_FRAME_DIMENSION, timeStatus);
  }

  boolean setPickUpIndices(
      SGPropertyMap map,
      SGPropertyResults result,
      final String key,
      final String value,
      Map<String, SGInteger> pickUpMap) {

    if (SGDataCommandConstants.COM_DATA_PICKUP_INDICES.equalsIgnoreCase(key)) {
      if (map.isDoubleQuoted(SGDataCommandConstants.COM_DATA_PICKUP_INDICES) == false) {
        result.putResult(
            SGDataCommandConstants.COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
        return false;
      }
      if (SGDataDataTypeUtility.isNetCDFData(owner.mData)) {
        SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) owner.mData;
        SGNetCDFVariable pickUpVar = owner.getNetCDFPickUpVariable(null);
        if (pickUpVar == null) {
          result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        Dimension dim = pickUpVar.getDimension(0);
        final int len = dim.getLength();
        SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(value, len);
        if (indices == null) {
          result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        SGNetCDFPickUpDimensionInfo info =
            new SGNetCDFPickUpDimensionInfo(dim.getShortName(), indices);
        if (sxyData.setPickUpDimensionInfo(info) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
              SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }

      } else if (SGDataDataTypeUtility.isMDArrayData(owner.mData)) {
        SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) owner.mData;
        if (!sxyData.isDimensionPicked()) {
          result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        SGMDArrayPickUpDimensionInfo info =
            (SGMDArrayPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();
        List<SGMDArrayVariable> pickUpVarList = sxyData.getPickUpMDArrayVariables();
        final int len =
            pickUpVarList.get(0).getDimensionLength(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        SGIntegerSeriesSet indices = SGIntegerSeriesSet.parse(value, len);
        if (indices == null) {
          result.putResult(key, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        info.setIndices(indices);
        if (sxyData.setPickUpDimensionInfo(info) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
              SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
      } else {
        // cannot apply
        result.putResult(
            SGDataCommandConstants.COM_DATA_PICKUP_INDICES, SGPropertyResults.INVALID_INPUT_VALUE);
        return false;
      }
      result.putResult(SGDataCommandConstants.COM_DATA_PICKUP_INDICES, SGPropertyResults.SUCCEEDED);
    } else if (SGDataCommandConstants.COM_DATA_PICKUP_START.equalsIgnoreCase(key)) {
      if (SGDataDataTypeUtility.isNetCDFData(owner.mData)) {
        Integer num = owner.getNetCDFPickUpNumber(map, key, value);
        if (num == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        if (num != null) {
          pickUpMap.put(SGDataCommandConstants.COM_DATA_PICKUP_START, new SGInteger(num));
        }
      } else {
        // cannot apply
        result.putResult(
            SGDataCommandConstants.COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
        return false;
      }
    } else if (SGDataCommandConstants.COM_DATA_PICKUP_END.equalsIgnoreCase(key)) {
      if (SGDataDataTypeUtility.isNetCDFData(owner.mData)) {
        Integer num = owner.getNetCDFPickUpNumber(map, key, value);
        if (num == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        if (num != null) {
          pickUpMap.put(SGDataCommandConstants.COM_DATA_PICKUP_END, new SGInteger(num));
        }
      } else {
        // cannot apply
        result.putResult(
            SGDataCommandConstants.COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
        return false;
      }
    } else if (SGDataCommandConstants.COM_DATA_PICKUP_STEP.equalsIgnoreCase(key)) {
      if (SGDataDataTypeUtility.isNetCDFData(owner.mData)) {
        Integer num = owner.getNetCDFPickUpNumber(map, key, value);
        if (num == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
          return false;
        }
        if (num != null) {
          pickUpMap.put(SGDataCommandConstants.COM_DATA_PICKUP_STEP, new SGInteger(num));
        }
      } else {
        // cannot apply
        result.putResult(
            SGDataCommandConstants.COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
        return false;
      }
    }

    return true;
  }

  void setPickUpResults(
      SGPropertyMap map,
      SGPropertyResults result,
      SGInteger pickUpStart,
      SGInteger pickUpEnd,
      SGInteger pickUpStep) {
    if (owner.mData instanceof SGSXYNetCDFMultipleData) {
      SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) owner.mData;
      if (SGIntegerSeries.isValidSeries(pickUpStart, pickUpEnd, pickUpStep)) {
        SGNetCDFVariable pickUpVar = owner.getNetCDFPickUpVariable(null);
        if (pickUpVar == null) {
          owner.setPickUpResults(
              map,
              result,
              pickUpStart,
              pickUpEnd,
              pickUpStep,
              SGPropertyResults.INVALID_INPUT_VALUE);
          return;
        }
        Dimension dim = pickUpVar.getDimension(0);
        final int len = dim.getLength();
        SGInteger start = owner.createInteger(pickUpStart, len);
        SGInteger end = owner.createInteger(pickUpEnd, len);
        SGInteger step = owner.createInteger(pickUpStep, len);
        SGIntegerSeriesSet indices = new SGIntegerSeriesSet(start, end, step);
        SGNetCDFPickUpDimensionInfo info =
            new SGNetCDFPickUpDimensionInfo(dim.getShortName(), indices);
        if (sxyData.setPickUpDimensionInfo(info) == false) {
          owner.setPickUpResults(
              map,
              result,
              pickUpStart,
              pickUpEnd,
              pickUpStep,
              SGPropertyResults.INVALID_INPUT_VALUE);
        }
        owner.setPickUpResults(
            map, result, pickUpStart, pickUpEnd, pickUpStep, SGPropertyResults.SUCCEEDED);
      } else {
        owner.setPickUpResults(
            map, result, pickUpStart, pickUpEnd, pickUpStep, SGPropertyResults.INVALID_INPUT_VALUE);
      }
    } else if (owner.mData instanceof SGSXYMDArrayMultipleData) {
      // do nothing
      return;
    } else {
      owner.setPickUpResults(
          map, result, pickUpStart, pickUpEnd, pickUpStep, SGPropertyResults.INVALID_INPUT_VALUE);
    }
  }

  protected boolean setProperties(
      SGPropertyMap map, SGPropertyResults result, SGDataColumnInfo[] cols) {

    // set column types
    SGArrayData aData = (SGArrayData) owner.mData;
    SGDataColumnInfo[] preColumnInfo = aData.getColumnInfo();
    try {
      if (SGDataDataTypeUtility.isNetCDFData(owner.mData)) {
        if (owner.setNetCDFColumnType(map, result, cols) == false) {
          return false;
        }
      } else if (SGDataDataTypeUtility.isMDArrayData(owner.mData)) {
        if (owner.setMDArrayColumnType(map, result, cols) == false) {
          return false;
        }
      }
    } finally {
      // updates the stride with new column types
      if (!SGDataColumnInfoUtility.hasEqualInput(preColumnInfo, cols)) {
        Map<String, Object> infoMap = new HashMap<String, Object>();
        infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, owner.mData.getDataType());
        infoMap.put(
            SGDataInformationKeyConstants.KEY_FIGURE_SIZE,
            new SGTuple2f(owner.mGraph.getGraphRectWidth(), owner.mGraph.getGraphRectHeight()));
        if (SGDataDataTypeUtility.isSDArrayData(owner.mData)) {
          SGSXYSDArrayMultipleData sdData = (SGSXYSDArrayMultipleData) owner.mData;
          Map<String, SGIntegerSeriesSet> strideMap =
              SGDataStrideUtility.calcSDArrayDefaultStride(cols, infoMap);
          SGIntegerSeriesSet stride =
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
          sdData.setStride(stride);
          SGIntegerSeriesSet tickLabelStride =
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
          sdData.setTickLabelStride(tickLabelStride);
        } else if (SGDataDataTypeUtility.isNetCDFData(owner.mData)) {
          SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) owner.mData;
          Map<String, SGIntegerSeriesSet> strideMap =
              SGDataStrideUtility.calcNetCDFDefaultStride(cols, infoMap);
          SGIntegerSeriesSet stride = strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE);
          ncData.setStride(stride);
          SGIntegerSeriesSet tickLabelStride =
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
          ncData.setTickLabelStride(tickLabelStride);
          SGIntegerSeriesSet indexStride =
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE);
          ncData.setIndexStride(indexStride);
        } else if (SGDataDataTypeUtility.isMDArrayData(owner.mData)) {
          SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) owner.mData;
          Map<String, SGIntegerSeriesSet> strideMap =
              SGDataStrideUtility.calcMDArrayDefaultStride(cols, infoMap);
          SGIntegerSeriesSet stride = strideMap.get(SGDataInformationKeyConstants.KEY_SXY_STRIDE);
          mdData.setStride(stride);
          SGIntegerSeriesSet tickLabelStride =
              strideMap.get(SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE);
          mdData.setTickLabelStride(tickLabelStride);
        }
      }

      // clears time dimension
      owner.clearTimeDimension();
    }

    // sets picked up dimension and animation dimension
    if (SGDataDataTypeUtility.isMDArrayData(owner.mData)) {
      List<String> keys = map.getKeys();
      String strPickUpDimension =
          map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION);
      String strTimeDimension =
          map.getValueString(SGDataCommandConstants.COM_DATA_ANIMATION_FRAME_DIMENSION);
      final boolean pickUpDimContained =
          keys.contains(SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION.toUpperCase())
              && !"".equals(strPickUpDimension);
      final boolean timeDimContained =
          keys.contains(SGDataCommandConstants.COM_DATA_ANIMATION_FRAME_DIMENSION.toUpperCase())
              && !"".equals(strTimeDimension);
      if (timeDimContained && pickUpDimContained) {
        owner.setPickUpAndTimeDimension(result, strPickUpDimension, strTimeDimension);
      } else {
        if (pickUpDimContained) {
          if (!owner.setPickUpDimension(strPickUpDimension)) {
            result.putResult(
                SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION,
                SGPropertyResults.INVALID_INPUT_VALUE);
          } else {
            result.putResult(
                SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION, SGPropertyResults.SUCCEEDED);
          }
        }
        if (timeDimContained) {
          if (!owner.setTimeDimension(strTimeDimension)) {
            result.putResult(
                SGDataCommandConstants.COM_DATA_ANIMATION_FRAME_DIMENSION,
                SGPropertyResults.INVALID_INPUT_VALUE);
          } else {
            result.putResult(
                SGDataCommandConstants.COM_DATA_ANIMATION_FRAME_DIMENSION,
                SGPropertyResults.SUCCEEDED);
          }
        }
      }
    }

    // sets picked up indices
    Map<String, SGInteger> pickUpMap = new HashMap<String, SGInteger>();
    String strPickUpIndices = map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_INDICES);
    if (!"".equals(strPickUpIndices)) {
      if (!owner.setPickUpIndices(
          map,
          result,
          SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
          strPickUpIndices,
          pickUpMap)) {
        return false;
      }
    }
    String strPickUpStart = map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_START);
    if (!"".equals(strPickUpStart)) {
      if (!owner.setPickUpIndices(
          map, result, SGDataCommandConstants.COM_DATA_PICKUP_START, strPickUpStart, pickUpMap)) {
        return false;
      }
    }
    String strPickUpEnd = map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_END);
    if (!"".equals(strPickUpEnd)) {
      if (!owner.setPickUpIndices(
          map, result, SGDataCommandConstants.COM_DATA_PICKUP_END, strPickUpEnd, pickUpMap)) {
        return false;
      }
    }
    String strPickUpStep = map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_STEP);
    if (!"".equals(strPickUpStep)) {
      if (!owner.setPickUpIndices(
          map, result, SGDataCommandConstants.COM_DATA_PICKUP_STEP, strPickUpStep, pickUpMap)) {
        return false;
      }
    }

    // sets the stride
    Iterator<String> itr = map.getKeyIterator();
    while (itr.hasNext()) {
      String key = itr.next();
      String value = map.getValueString(key);
      if ("".equals(value)) {
        continue;
      }
      if (SGDataCommandConstants.COM_DATA_ARRAY_SECTION.equalsIgnoreCase(key)) {
        if (map.isDoubleQuoted(key) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (owner.setStride(value) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_ARRAY_SECTION, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(
            SGDataCommandConstants.COM_DATA_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
      } else if (SGDataCommandConstants.COM_DATA_TICK_LABEL_ARRAY_SECTION.equalsIgnoreCase(key)) {
        if (map.isDoubleQuoted(key) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_TICK_LABEL_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (owner.setTickLabelStride(result, value) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_TICK_LABEL_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(
            SGDataCommandConstants.COM_DATA_TICK_LABEL_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
      } else if (SGDataCommandConstants.COM_DATA_INDEX_ARRAY_SECTION.equalsIgnoreCase(key)) {
        if (map.isDoubleQuoted(SGDataCommandConstants.COM_DATA_INDEX_ARRAY_SECTION) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_INDEX_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        if (owner.setIndexStride(value) == false) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_INDEX_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        result.putResult(
            SGDataCommandConstants.COM_DATA_INDEX_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
      } else if (SGDataCommandConstants.COM_DATA_ANIMATION_ARRAY_SECTION.equalsIgnoreCase(key)) {
        SGArrayData data = (SGArrayData) owner.getData();
        if (!data.isAnimationAvailable()) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_ANIMATION_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        final int len = data.getAnimationLength();
        if (len == -1) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_ANIMATION_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        SGIntegerSeriesSet arraySection = SGIntegerSeriesSet.parse(value, len);
        if (arraySection == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_ANIMATION_ARRAY_SECTION,
              SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        owner.setFrameIndices(arraySection);
        result.putResult(
            SGDataCommandConstants.COM_DATA_ANIMATION_ARRAY_SECTION, SGPropertyResults.SUCCEEDED);
      }
    }

    return true;
  }

  boolean setNetCDFColumnType(
      SGPropertyMap map, SGPropertyResults result, SGDataColumnInfo[] cols) {

    String[] columnTypes = new String[cols.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = cols[ii].getColumnType();
    }

    SGSXYNetCDFMultipleData sxyData = (SGSXYNetCDFMultipleData) owner.mData;
    SGNetCDFPickUpDimensionInfo curInfo =
        (SGNetCDFPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();

    SGNetCDFVariable pickUpVar = owner.getNetCDFPickUpVariable(columnTypes);
    if (pickUpVar != null) {
      // pick up variable is assigned

      SGNetCDFPickUpDimensionInfo newInfo = null;
      if (curInfo != null) {
        SGNetCDFFile ncfile = sxyData.getNetcdfFile();
        String curDimName = curInfo.getDimensionName();
        String pickUpDimName = pickUpVar.getName();
        Dimension curDim = ncfile.findDimension(curDimName);
        final int curLen = curDim.getLength();
        Dimension newDim = ncfile.findDimension(pickUpDimName);
        final int newLen = newDim.getLength();
        SGIntegerSeriesSet curIndices = curInfo.getIndices();
        SGIntegerSeries curSeries = curIndices.testReduce();
        newInfo = owner.getNetCDFPickUpInfo(map, result, pickUpDimName, curLen, curSeries);
        if (newInfo == null) {
          SGIntegerSeriesSet indicesNew =
              new SGIntegerSeriesSet(SGDataStrideUtility.createDefaultStepSeries(newLen));
          newInfo = new SGNetCDFPickUpDimensionInfo(pickUpDimName, indicesNew);
        }

      } else {
        // pick up variable is newly-assigned
        Dimension dim = pickUpVar.getDimension(0);
        final int len = dim.getLength();
        String strideValue = map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_INDICES);
        if (!"".equals(strideValue)) {
          SGIntegerSeriesSet indices = null;
          if (map.isDoubleQuoted(SGDataCommandConstants.COM_DATA_PICKUP_INDICES)) {
            indices = SGIntegerSeriesSet.parse(strideValue, len);
          } else {
            result.putResult(
                SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
                SGPropertyResults.INVALID_INPUT_VALUE);
          }
          if (indices == null) {
            result.putResult(
                SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
                SGPropertyResults.INVALID_INPUT_VALUE);
            indices = new SGIntegerSeriesSet(SGDataStrideUtility.createDefaultStepSeries(len));
          }
          newInfo = new SGNetCDFPickUpDimensionInfo(pickUpVar.getName(), indices);
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_INDICES, SGPropertyResults.SUCCEEDED);
        } else {
          newInfo = owner.getNetCDFPickUpInfo(map, result, dim.getShortName(), len, null);
          if (newInfo == null) {
            // get default pick up stride
            SGDataColumnInfo[] infoArray = owner.getDataColumnInfoArray();
            SGNetCDFDataColumnInfo[] nCols = null;
            SGNetCDFFile ncFile = sxyData.getNetcdfFile();
            nCols = new SGNetCDFDataColumnInfo[cols.length];
            for (int ii = 0; ii < nCols.length; ii++) {
              SGNetCDFDataColumnInfo nCol = (SGNetCDFDataColumnInfo) infoArray[ii];
              String varName = nCol.getName();
              SGNetCDFVariable var = ncFile.findVariable(varName);
              nCols[ii] = new SGNetCDFDataColumnInfo(var, var.getName(), var.getValueType());
              nCols[ii].setColumnType(columnTypes[ii]);
            }
            Map<String, Object> infoMap = new HashMap<String, Object>();
            SGDataFileUtility.updatePickupParameters(infoMap, nCols);
            SGIntegerSeriesSet indices =
                (SGIntegerSeriesSet)
                    infoMap.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
            newInfo = new SGNetCDFPickUpDimensionInfo(pickUpVar.getName(), indices);
          }
        }
      }

      // set to data
      if (sxyData.setColumnType(columnTypes, newInfo) == false) {
        owner.setFailedColumnTypeResult(map, result);

        // recovers pick up information
        sxyData.setPickUpDimensionInfo(curInfo);
        return false;
      }

    } else {
      // pick up variable is not assigned

      // clears pick up information
      sxyData.setPickUpDimensionInfo(null);

      // sets the column types
      if (sxyData.setColumnType(columnTypes) == false) {
        owner.setFailedColumnTypeResult(map, result);

        // recovers pick up information
        sxyData.setPickUpDimensionInfo(curInfo);
        return false;
      }
    }

    result.putResult(SGDataCommandConstants.COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);
    return true;
  }

  SGNetCDFPickUpDimensionInfo getNetCDFPickUpInfo(
      SGPropertyMap map,
      SGPropertyResults result,
      String dimName,
      final int len,
      final SGIntegerSeries curSeries) {
    SGNetCDFPickUpDimensionInfo info = null;
    SGInteger start = null;
    SGInteger end = null;
    SGInteger step = null;
    List<String> keys = map.getKeys();
    for (String key : keys) {
      String value = map.getValueString(key);
      if (SGDataCommandConstants.COM_DATA_PICKUP_START.equalsIgnoreCase(key)) {
        Number num = owner.getPickUpNumber(value, len);
        if (num == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_START, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        start = new SGInteger(num.intValue());
      } else if (SGDataCommandConstants.COM_DATA_PICKUP_END.equalsIgnoreCase(key)) {
        Number num = owner.getPickUpNumber(value, len);
        if (num == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_END, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        end = new SGInteger(num.intValue());
      } else if (SGDataCommandConstants.COM_DATA_PICKUP_STEP.equalsIgnoreCase(key)) {
        Number num = owner.getPickUpNumber(value, len);
        if (num == null) {
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_STEP, SGPropertyResults.INVALID_INPUT_VALUE);
          continue;
        }
        step = new SGInteger(num.intValue());
      }
    }
    if (start != null || end != null || step != null) {
      if (!(start != null && end != null && step != null) && (curSeries == null)) {
        owner.setPickUpResults(
            map, result, start, end, step, SGPropertyResults.INVALID_INPUT_VALUE);
      } else {
        final int nStart =
            (start != null) ? start.getNumber().intValue() : curSeries.getStart().getNumber();
        final int nEnd =
            (end != null) ? end.getNumber().intValue() : curSeries.getEnd().getNumber();
        final int nStep =
            (step != null) ? step.getNumber().intValue() : curSeries.getStep().getNumber();
        if (SGIntegerSeriesSet.isValidInput(nStart, nEnd, nStep)) {
          SGIntegerSeriesSet indicesNew = new SGIntegerSeriesSet(nStart, nEnd, nStep);
          info = new SGNetCDFPickUpDimensionInfo(dimName, indicesNew);
          owner.setPickUpResults(map, result, start, end, step, SGPropertyResults.SUCCEEDED);
        } else {
          owner.setPickUpResults(
              map, result, start, end, step, SGPropertyResults.INVALID_INPUT_VALUE);
        }
      }
    }
    return info;
  }

  void setPickUpResults(
      SGPropertyMap map,
      SGPropertyResults result,
      SGInteger start,
      SGInteger end,
      SGInteger step,
      final int status) {
    List<String> keys = map.getKeys();
    if (start != null
        && keys.contains(SGDataCommandConstants.COM_DATA_PICKUP_START.toUpperCase())) {
      result.putResult(SGDataCommandConstants.COM_DATA_PICKUP_START, status);
    }
    if (end != null && keys.contains(SGDataCommandConstants.COM_DATA_PICKUP_END.toUpperCase())) {
      result.putResult(SGDataCommandConstants.COM_DATA_PICKUP_END, status);
    }
    if (step != null && keys.contains(SGDataCommandConstants.COM_DATA_PICKUP_STEP.toUpperCase())) {
      result.putResult(SGDataCommandConstants.COM_DATA_PICKUP_STEP, status);
    }
  }

  boolean setMDArrayColumnType(
      SGPropertyMap map, SGPropertyResults result, SGDataColumnInfo[] cols) {

    String[] columnTypes = new String[cols.length];
    for (int ii = 0; ii < columnTypes.length; ii++) {
      columnTypes[ii] = cols[ii].getColumnType();
    }

    SGSXYMDArrayMultipleData sxyData = (SGSXYMDArrayMultipleData) owner.mData;
    SGMDArrayPickUpDimensionInfo curInfo =
        (SGMDArrayPickUpDimensionInfo) sxyData.getPickUpDimensionInfo();
    String strPickUpDimension =
        map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_DIMENSION);
    Map<String, Integer> pickUpMap = owner.createPickUpDimMap(sxyData, strPickUpDimension);

    if (pickUpMap != null && pickUpMap.size() != 0) {
      // pick up variable is assigned

      int newLen = -1;
      Iterator<Map.Entry<String, Integer>> pickUpItr = pickUpMap.entrySet().iterator();
      while (pickUpItr.hasNext()) {
        Map.Entry<String, Integer> pickUpEntry = pickUpItr.next();
        String pickUpVarName = pickUpEntry.getKey();
        Integer pickUpDim = pickUpEntry.getValue();
        SGMDArrayVariable newVar = sxyData.findVariable(pickUpVarName);
        int[] dims = newVar.getDimensions();
        if (pickUpDim == -1) {
          continue;
        }
        if (pickUpDim < 0 || pickUpDim >= dims.length) {
          return false;
        }
        final int len = dims[pickUpDim];
        if (newLen != -1) {
          if (len != newLen) {
            return false;
          }
        }
        newLen = len;
      }

      SGMDArrayPickUpDimensionInfo newInfo = null;
      if (curInfo != null) {
        SGIntegerSeriesSet indicesNew =
            new SGIntegerSeriesSet(SGDataStrideUtility.createDefaultStepSeries(newLen));
        newInfo = new SGMDArrayPickUpDimensionInfo(pickUpMap, indicesNew);

      } else {
        // pick up variable is newly-assigned
        String strideValue = map.getValueString(SGDataCommandConstants.COM_DATA_PICKUP_INDICES);
        if (!"".equals(strideValue)) {
          SGIntegerSeriesSet indices = null;
          if (map.isDoubleQuoted(SGDataCommandConstants.COM_DATA_PICKUP_INDICES)) {
            indices = SGIntegerSeriesSet.parse(strideValue, newLen);
          } else {
            result.putResult(
                SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
                SGPropertyResults.INVALID_INPUT_VALUE);
          }
          if (indices == null) {
            result.putResult(
                SGDataCommandConstants.COM_DATA_PICKUP_INDICES,
                SGPropertyResults.INVALID_INPUT_VALUE);
            indices = new SGIntegerSeriesSet(SGDataStrideUtility.createDefaultStepSeries(newLen));
          }
          newInfo = new SGMDArrayPickUpDimensionInfo(pickUpMap, indices);
          result.putResult(
              SGDataCommandConstants.COM_DATA_PICKUP_INDICES, SGPropertyResults.SUCCEEDED);
        } else {
          // get default pick up stride
          SGDataColumnInfo[] infoArray = owner.getDataColumnInfoArray();
          SGMDArrayDataColumnInfo[] mdCols = new SGMDArrayDataColumnInfo[cols.length];
          for (int ii = 0; ii < mdCols.length; ii++) {
            SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) infoArray[ii];
            String varName = mdCol.getName();
            SGMDArrayVariable var = sxyData.findVariable(varName);
            mdCols[ii] = new SGMDArrayDataColumnInfo(var, var.getName(), var.getValueType());
            mdCols[ii].setColumnType(columnTypes[ii]);
          }
          Map<String, Object> infoMap = new HashMap<String, Object>();
          SGDataFileUtility.updatePickupParameters(infoMap, mdCols);
          SGIntegerSeriesSet indices =
              (SGIntegerSeriesSet)
                  infoMap.get(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES);
          newInfo = new SGMDArrayPickUpDimensionInfo(pickUpMap, indices);
        }
      }

      // set to data
      if (sxyData.setColumnType(cols, newInfo) == false) {
        owner.setFailedColumnTypeResult(map, result);

        // recovers pick up information
        sxyData.setPickUpDimensionInfo(curInfo);
        return false;
      }

    } else {
      // pick up variable is not assigned

      // sets the column types
      if (sxyData.setColumnType(cols, null) == false) {
        owner.setFailedColumnTypeResult(map, result);

        // recovers pick up information
        sxyData.setPickUpDimensionInfo(curInfo);
        return false;
      }
    }

    result.putResult(SGDataCommandConstants.COM_DATA_COLUMN_TYPE, SGPropertyResults.SUCCEEDED);

    return true;
  }
}
