package jp.riken.brain.ni.samuraigraph.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import ucar.nc2.Dimension;

/** Utility methods to merge data objects. */
public class SGDataMergeUtility implements SGIDataColumnTypeConstants {

  private SGDataMergeUtility() {}

  public static SGISXYTypeMultipleData merge(final List<SGData> dataList) {
    if (dataList.size() == 0) {
      return null;
    }
    if (dataList.size() == 1) {
      SGData data = dataList.get(0);
      if ((data instanceof SGSXYNetCDFMultipleData) == false) {
        return null;
      }
      return (SGISXYTypeMultipleData) data;
    }

    // checks the data source
    SGIDataSource src = null;
    for (SGData data : dataList) {
      SGIDataSource s = data.getDataSource();
      if (src == null) {
        src = s;
      } else {
        if (!src.equals(s)) {
          return null;
        }
      }
    }

    // checks the type
    Boolean dimensionPicked = null;
    for (SGData data : dataList) {
      if ((data instanceof SGSXYNetCDFMultipleData) == false) {
        return null;
      }
      SGSXYNetCDFMultipleData ncData = (SGSXYNetCDFMultipleData) data;
      final boolean b = ncData.isDimensionPicked();
      if (dimensionPicked == null) {
        dimensionPicked = b;
      } else {
        if (!dimensionPicked.equals(b)) {
          return null;
        }
      }
    }
    if (dimensionPicked == null) {
      return null;
    }

    SGSXYNetCDFMultipleData dataLast = (SGSXYNetCDFMultipleData) dataList.get(dataList.size() - 1);
    SGNetCDFFile ncFile = dataLast.getNetcdfFile();
    SGDataSourceObserver obs = dataLast.getDataSourceObserver();

    if (dimensionPicked) {
      SGNetCDFVariable xVar = dataLast.getXVariable();
      SGNetCDFVariable yVar = dataLast.getYVariable();
      SGNetCDFVariable leVar = dataLast.getLowerErrorVariable();
      SGNetCDFVariable ueVar = dataLast.getUpperErrorVariable();
      SGNetCDFVariable ehVar = dataLast.getErrorBarHolderVariable();
      SGNetCDFVariable tlVar = dataLast.getTickLabelVariable();
      SGNetCDFVariable thVar = dataLast.getTickLabelHolderVariable();
      SGNetCDFVariable timeVar = dataLast.getTimeVariable();
      SGNetCDFVariable indexVar = dataLast.getIndexVariable();

      SGNetCDFDataColumnInfo xInfo = SGDataFileUtility.createDataColumnInfo(xVar, X_VALUE);
      SGNetCDFDataColumnInfo yInfo = SGDataFileUtility.createDataColumnInfo(yVar, Y_VALUE);
      SGNetCDFDataColumnInfo leInfo =
          (leVar != null)
              ? SGDataFileUtility.createErrorBarInfo(leVar, LOWER_ERROR_VALUE, leVar, ueVar, ehVar)
              : null;
      SGNetCDFDataColumnInfo ueInfo =
          (ueVar != null)
              ? SGDataFileUtility.createErrorBarInfo(ueVar, UPPER_ERROR_VALUE, leVar, ueVar, ehVar)
              : null;
      SGNetCDFDataColumnInfo ehInfo =
          (ehVar != null)
              ? (SGNetCDFDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone()
              : null;
      SGNetCDFDataColumnInfo tlInfo =
          (tlVar != null)
              ? SGDataFileUtility.createDataColumnInfo(tlVar, TICK_LABEL, tlVar.getName())
              : null;
      SGNetCDFDataColumnInfo thInfo =
          (thVar != null)
              ? (SGNetCDFDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone()
              : null;
      String dimName = dataLast.getDimensionName();
      SGNetCDFDataColumnInfo timeInfo =
          dataLast.isTimeVariableAvailable()
              ? SGDataFileUtility.createDataColumnInfo(timeVar, ANIMATION_FRAME)
              : null;
      SGNetCDFDataColumnInfo indexInfo =
          dataLast.isIndexAvailable()
              ? SGDataFileUtility.createDataColumnInfo(indexVar, INDEX)
              : null;

      // get dimension indices
      Dimension dim = dataLast.getDimension();
      final int dimLen = (null != dim) ? dim.getLength() : 0;
      SGIntegerSeriesSet indices = SGDataStrideUtility.getDimensionSeries(dataList, dimLen);
      if (indices == null) {
        return null;
      }

      SGSXYNetCDFMultipleData data =
          new SGSXYNetCDFMultipleData(
              ncFile,
              obs,
              xInfo,
              yInfo,
              leInfo,
              ueInfo,
              ehInfo,
              tlInfo,
              thInfo,
              dimName,
              indices,
              timeInfo,
              indexInfo,
              dataLast.mStride,
              dataLast.mTickLabelStride,
              dataLast.isStrideAvailable());
      data.mOriginMap = new HashMap<String, Integer>(dataLast.mOriginMap);
      data.setDecimalPlaces(dataLast.getDecimalPlaces());
      data.setExponent(dataLast.getExponent());
      data.setTimeStride(dataLast.getTimeStride());
      return data;

    } else {

      List<SGNetCDFVariable> xList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> yList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> leList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> ueList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> ehList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> tlList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> thList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> timeList = new ArrayList<SGNetCDFVariable>();
      List<SGNetCDFVariable> indexList = new ArrayList<SGNetCDFVariable>();
      for (SGData data : dataList) {
        SGSXYNetCDFMultipleData dataMulti = (SGSXYNetCDFMultipleData) data;
        SGNetCDFVariable[] xVars = dataMulti.getXVariables();
        SGNetCDFVariable[] yVars = dataMulti.getYVariables();
        SGNetCDFVariable[] leVars = dataMulti.getLowerErrorVariables();
        SGNetCDFVariable[] ueVars = dataMulti.getUpperErrorVariables();
        SGNetCDFVariable[] ehVars = dataMulti.getErrorHolderVariables();
        SGNetCDFVariable[] tlVars = dataMulti.getTickLabelVariables();
        SGNetCDFVariable[] thVars = dataMulti.getTickLabelHolderVariables();
        SGNetCDFVariable timeVar = dataMulti.getTimeVariable();
        SGNetCDFVariable indexVar = dataMulti.getIndexVariable();
        for (int ii = 0; ii < xVars.length; ii++) {
          xList.add(xVars[ii]);
        }
        for (int ii = 0; ii < yVars.length; ii++) {
          yList.add(yVars[ii]);
        }
        if (dataMulti.isErrorBarAvailable()) {
          for (int ii = 0; ii < leVars.length; ii++) {
            leList.add(leVars[ii]);
          }
          for (int ii = 0; ii < ueVars.length; ii++) {
            ueList.add(ueVars[ii]);
          }
          for (int ii = 0; ii < ehVars.length; ii++) {
            ehList.add(ehVars[ii]);
          }
        }
        if (dataMulti.isTickLabelAvailable()) {
          for (int ii = 0; ii < tlVars.length; ii++) {
            tlList.add(tlVars[ii]);
          }
          for (int ii = 0; ii < thVars.length; ii++) {
            thList.add(thVars[ii]);
          }
        }
        if (timeVar != null) {
          timeList.add(timeVar);
        }
        if (indexVar != null) {
          indexList.add(indexVar);
        }
      }

      if (indexList.size() > 0) {
        return null;
      }

      // selects the time variable
      SGNetCDFVariable timeVar = null;
      if (timeList.size() != 0) {
        timeVar = timeList.get(timeList.size() - 1);
      }

      // there must exist only one common coordinate variable
      Set<SGNetCDFVariable> xSet = new HashSet<SGNetCDFVariable>(xList);
      Set<SGNetCDFVariable> ySet = new HashSet<SGNetCDFVariable>(yList);
      Boolean bCVarX = null;
      Iterator<SGNetCDFVariable> xItr = xSet.iterator();
      while (xItr.hasNext()) {
        SGNetCDFVariable xVar = xItr.next();
        if (bCVarX == null) {
          bCVarX = xVar.isCoordinateVariable();
        } else {
          if (!bCVarX.equals(xVar.isCoordinateVariable())) {
            return null;
          }
        }
      }
      Boolean bCVarY = null;
      Iterator<SGNetCDFVariable> yItr = ySet.iterator();
      while (yItr.hasNext()) {
        SGNetCDFVariable yVar = yItr.next();
        if (bCVarY == null) {
          bCVarY = yVar.isCoordinateVariable();
        } else {
          if (!bCVarY.equals(yVar.isCoordinateVariable())) {
            return null;
          }
        }
      }
      final boolean isCVarX = (bCVarX != null) ? bCVarX.booleanValue() : false;
      final boolean isCVarY = (bCVarY != null) ? bCVarY.booleanValue() : false;
      if (isCVarX || isCVarY) {

        if (isCVarX && isCVarY) {
          // both of x and y variables must not coordinate variables
          return null;
        }
        if (isCVarX) {
          // only one coordinate variable can exist
          if (xSet.size() != 1) {
            return null;
          }
        }
        if (isCVarY) {
          // only one coordinate variable can exist
          if (ySet.size() != 1) {
            return null;
          }
        }
      }

      List<SGNetCDFVariable> xListNew = new ArrayList<SGNetCDFVariable>();
      for (SGNetCDFVariable var : xList) {
        if (!xListNew.contains(var)) {
          xListNew.add(var);
        }
      }
      List<SGNetCDFVariable> yListNew = new ArrayList<SGNetCDFVariable>();
      for (SGNetCDFVariable var : yList) {
        if (!yListNew.contains(var)) {
          yListNew.add(var);
        }
      }

      SGNetCDFVariable[] x = xListNew.toArray(new SGNetCDFVariable[xListNew.size()]);
      SGNetCDFDataColumnInfo[] xInfo = SGDataFileUtility.createDataColumnInfoArray(x, X_VALUE);

      SGNetCDFVariable[] y = yListNew.toArray(new SGNetCDFVariable[yListNew.size()]);
      SGNetCDFDataColumnInfo[] yInfo = SGDataFileUtility.createDataColumnInfoArray(y, Y_VALUE);

      SGNetCDFDataColumnInfo[] leInfo = new SGNetCDFDataColumnInfo[leList.size()];
      SGNetCDFDataColumnInfo[] ueInfo = new SGNetCDFDataColumnInfo[ueList.size()];
      SGNetCDFDataColumnInfo[] ehInfo = new SGNetCDFDataColumnInfo[ehList.size()];
      for (int ii = 0; ii < leInfo.length; ii++) {
        String leColumnType, ueColumnType, ehColumnType;
        SGNetCDFVariable leVar = leList.get(ii);
        SGNetCDFVariable ueVar = ueList.get(ii);
        SGNetCDFVariable ehVar = ehList.get(ii);
        StringBuilder sb = new StringBuilder();
        final boolean common = leVar.equals(ueVar);
        String ehName = ehVar.getName();

        // lower error
        if (common) {
          sb.append(LOWER_UPPER_ERROR_VALUE);
        } else {
          sb.append(LOWER_ERROR_VALUE);
        }
        sb.append(SGDataColumnTitleUtility.MID_COLUMN);
        sb.append(ehName);
        leColumnType = sb.toString();

        // upper error
        if (common) {
          ueColumnType = leColumnType;
        } else {
          sb.setLength(0);
          sb.append(UPPER_ERROR_VALUE);
          sb.append(SGDataColumnTitleUtility.MID_COLUMN);
          sb.append(ehName);
          ueColumnType = sb.toString();
        }

        // error bar holder
        SGDataColumnInfo ehInfoX = SGDataColumnInfoUtility.findColumnWithName(xInfo, ehName);
        ehColumnType = (ehInfoX != null) ? X_VALUE : Y_VALUE;

        leInfo[ii] = new SGNetCDFDataColumnInfo(leVar, null, leVar.getValueType());
        leInfo[ii].setColumnType(leColumnType);
        ueInfo[ii] = new SGNetCDFDataColumnInfo(ueVar, null, ueVar.getValueType());
        ueInfo[ii].setColumnType(ueColumnType);
        ehInfo[ii] = new SGNetCDFDataColumnInfo(ehVar, null, ehVar.getValueType());
        ehInfo[ii].setColumnType(ehColumnType);
      }

      SGNetCDFDataColumnInfo[] tlInfo = new SGNetCDFDataColumnInfo[tlList.size()];
      SGNetCDFDataColumnInfo[] thInfo = new SGNetCDFDataColumnInfo[thList.size()];
      for (int ii = 0; ii < tlInfo.length; ii++) {
        String tlColumnType, thColumnType;
        SGNetCDFVariable tlVar = tlList.get(ii);
        SGNetCDFVariable thVar = thList.get(ii);
        StringBuilder sb = new StringBuilder();
        String thName = thVar.getName();

        // tick label
        sb.append(TICK_LABEL);
        sb.append(SGDataColumnTitleUtility.MID_COLUMN);
        sb.append(thName);
        tlColumnType = sb.toString();

        // error bar holder
        SGDataColumnInfo thInfoX = SGDataColumnInfoUtility.findColumnWithName(xInfo, thName);
        thColumnType = (thInfoX != null) ? X_VALUE : Y_VALUE;

        tlInfo[ii] = new SGNetCDFDataColumnInfo(tlVar, null, tlVar.getValueType());
        tlInfo[ii].setColumnType(tlColumnType);
        thInfo[ii] = new SGNetCDFDataColumnInfo(thVar, null, thVar.getValueType());
        thInfo[ii].setColumnType(thColumnType);
      }

      SGSXYNetCDFMultipleData data =
          new SGSXYNetCDFMultipleData(
              ncFile,
              obs,
              xInfo,
              yInfo,
              leInfo,
              ueInfo,
              ehInfo,
              tlInfo,
              thInfo,
              SGDataFileUtility.createDataColumnInfo(timeVar, ANIMATION_FRAME),
              null,
              dataLast.mStride,
              dataLast.mTickLabelStride,
              dataLast.isStrideAvailable());
      data.mOriginMap = new HashMap<String, Integer>(dataLast.mOriginMap);
      data.setDecimalPlaces(dataLast.getDecimalPlaces());
      data.setExponent(dataLast.getExponent());
      data.setTimeStride(dataLast.getTimeStride());

      return data;
    }
  }

  public static SGISXYTypeMultipleData mergeMDArray(final List<SGData> dataList) {
    if (dataList.size() == 0) {
      return null;
    }

    // checks whether all data is picked up or not
    Boolean dimensionPicked = null;
    for (SGData data : dataList) {
      if ((data instanceof SGSXYMDArrayMultipleData) == false) {
        return null;
      }
      SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
      final boolean b = mdData.isDimensionPicked();
      if (dimensionPicked == null) {
        dimensionPicked = b;
      } else {
        if (!dimensionPicked.equals(b)) {
          return null;
        }
      }
    }
    if (dimensionPicked == null) {
      return null;
    }

    // checks the length
    int dimLen = -1;
    for (SGData data : dataList) {
      SGSXYMDArrayMultipleData dataMulti = (SGSXYMDArrayMultipleData) data;
      final int len = dataMulti.getAllPointsNumber();
      if (dimLen == -1) {
        dimLen = len;
      } else {
        if (len != dimLen) {
          return null;
        }
      }
    }

    SGSXYMDArrayMultipleData dataLast =
        (SGSXYMDArrayMultipleData) dataList.get(dataList.size() - 1);
    SGDataSourceObserver obs = dataLast.getDataSourceObserver();

    if (dimensionPicked) {
      SGMDArrayDataColumnInfo xInfo =
          SGDataFileUtility.createDataColumnInfo(dataLast.getXVariable(), X_VALUE);
      SGMDArrayDataColumnInfo yInfo =
          SGDataFileUtility.createDataColumnInfo(dataLast.getYVariable(), Y_VALUE);
      SGMDArrayDataColumnInfo leInfo =
          (null != dataLast.getLowerErrorVariable())
              ? SGDataFileUtility.createDataColumnInfo(
                  dataLast.getLowerErrorVariable(), LOWER_ERROR_VALUE)
              : null;
      SGMDArrayDataColumnInfo ueInfo =
          (null != dataLast.getUpperErrorVariable())
              ? SGDataFileUtility.createDataColumnInfo(
                  dataLast.getUpperErrorVariable(), UPPER_ERROR_VALUE)
              : null;
      SGMDArrayDataColumnInfo tlInfo =
          (null != dataLast.getTickLabelVariable())
              ? SGDataFileUtility.createDataColumnInfo(dataLast.getTickLabelVariable(), TICK_LABEL)
              : null;

      // get dimension indices
      int len = -1;
      if (xInfo != null) {
        Integer index = xInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        if (index != null && index != -1) {
          int[] dims = xInfo.getDimensions();
          len = dims[index];
        }
      }
      if (yInfo != null && len == -1) {
        Integer index = yInfo.getDimensionIndex(SGIMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        if (index != null && index != -1) {
          int[] dims = yInfo.getDimensions();
          len = dims[index];
        }
      }
      if (len == -1) {
        return null;
      }
      SGIntegerSeriesSet indices = SGDataStrideUtility.getDimensionSeries(dataList, len);
      if (indices == null) {
        return null;
      }
      indices.addAlias(len - 1, SGIntegerSeries.ARRAY_INDEX_END);

      SGSXYMDArrayMultipleData data =
          new SGSXYMDArrayMultipleData(
              dataLast.getMDArrayFile(),
              obs,
              xInfo,
              yInfo,
              leInfo,
              ueInfo,
              tlInfo,
              indices,
              dataLast.getStride(),
              dataLast.getTickLabelStride(),
              dataLast.isStrideAvailable());
      data.setDecimalPlaces(dataLast.getDecimalPlaces());
      data.setExponent(dataLast.getExponent());
      data.setTimeStride(dataLast.getTimeStride());
      return data;

    } else {

      List<SGMDArrayVariable> xList = new ArrayList<SGMDArrayVariable>();
      List<SGMDArrayVariable> yList = new ArrayList<SGMDArrayVariable>();
      List<SGMDArrayVariable> leList = new ArrayList<SGMDArrayVariable>();
      List<SGMDArrayVariable> ueList = new ArrayList<SGMDArrayVariable>();
      List<SGMDArrayVariable> ehList = new ArrayList<SGMDArrayVariable>();
      List<SGMDArrayVariable> tlList = new ArrayList<SGMDArrayVariable>();
      List<SGMDArrayVariable> thList = new ArrayList<SGMDArrayVariable>();
      for (SGData data : dataList) {
        SGSXYMDArrayMultipleData dataMulti = (SGSXYMDArrayMultipleData) data;

        SGMDArrayVariable[] xVars = dataMulti.getXVariables();
        SGMDArrayVariable[] yVars = dataMulti.getYVariables();
        SGMDArrayVariable[] leVars = dataMulti.getLowerErrorVariables();
        SGMDArrayVariable[] ueVars = dataMulti.getUpperErrorVariables();
        SGMDArrayVariable[] ehVars = dataMulti.getErrorHolderVariables();
        SGMDArrayVariable[] tlVars = dataMulti.getTickLabelVariables();
        SGMDArrayVariable[] thVars = dataMulti.getTickLabelHolderVariables();
        for (int ii = 0; ii < xVars.length; ii++) {
          xList.add(xVars[ii]);
        }
        for (int ii = 0; ii < yVars.length; ii++) {
          yList.add(yVars[ii]);
        }
        if (dataMulti.isErrorBarAvailable()) {
          for (int ii = 0; ii < leVars.length; ii++) {
            leList.add(leVars[ii]);
          }
          for (int ii = 0; ii < ueVars.length; ii++) {
            ueList.add(ueVars[ii]);
          }
          for (int ii = 0; ii < ehVars.length; ii++) {
            ehList.add(ehVars[ii]);
          }
        }
        if (dataMulti.isTickLabelAvailable()) {
          for (int ii = 0; ii < tlVars.length; ii++) {
            tlList.add(tlVars[ii]);
          }
          for (int ii = 0; ii < thVars.length; ii++) {
            thList.add(thVars[ii]);
          }
        }
      }

      List<SGMDArrayVariable> xListNew = new ArrayList<SGMDArrayVariable>();
      for (SGMDArrayVariable var : xList) {
        if (!xListNew.contains(var)) {
          xListNew.add(var);
        }
      }
      List<SGMDArrayVariable> yListNew = new ArrayList<SGMDArrayVariable>();
      for (SGMDArrayVariable var : yList) {
        if (!yListNew.contains(var)) {
          yListNew.add(var);
        }
      }

      SGMDArrayVariable[] x = xListNew.toArray(new SGMDArrayVariable[xListNew.size()]);
      SGMDArrayDataColumnInfo[] xInfo = SGDataFileUtility.createDataColumnInfoArray(x, X_VALUE);

      SGMDArrayVariable[] y = yListNew.toArray(new SGMDArrayVariable[yListNew.size()]);
      SGMDArrayDataColumnInfo[] yInfo = SGDataFileUtility.createDataColumnInfoArray(y, Y_VALUE);

      SGMDArrayDataColumnInfo[] leInfo = new SGMDArrayDataColumnInfo[leList.size()];
      SGMDArrayDataColumnInfo[] ueInfo = new SGMDArrayDataColumnInfo[ueList.size()];
      SGMDArrayDataColumnInfo[] ehInfo = new SGMDArrayDataColumnInfo[ehList.size()];
      for (int ii = 0; ii < leInfo.length; ii++) {
        String leColumnType, ueColumnType, ehColumnType;
        SGMDArrayVariable leVar = leList.get(ii);
        SGMDArrayVariable ueVar = ueList.get(ii);
        SGMDArrayVariable ehVar = ehList.get(ii);
        StringBuilder sb = new StringBuilder();
        final boolean common = leVar.equals(ueVar);
        String ehName = ehVar.getName();

        // lower error
        if (common) {
          sb.append(LOWER_UPPER_ERROR_VALUE);
        } else {
          sb.append(LOWER_ERROR_VALUE);
        }
        sb.append(SGDataColumnTitleUtility.MID_COLUMN);
        sb.append(ehName);
        leColumnType = sb.toString();

        // upper error
        if (common) {
          ueColumnType = leColumnType;
        } else {
          sb.setLength(0);
          sb.append(UPPER_ERROR_VALUE);
          sb.append(SGDataColumnTitleUtility.MID_COLUMN);
          sb.append(ehName);
          ueColumnType = sb.toString();
        }

        // error bar holder
        SGDataColumnInfo ehInfoX = SGDataColumnInfoUtility.findColumnWithName(xInfo, ehName);
        ehColumnType = (ehInfoX != null) ? X_VALUE : Y_VALUE;

        leInfo[ii] = new SGMDArrayDataColumnInfo(leVar, null, leVar.getValueType());
        leInfo[ii].setColumnType(leColumnType);
        ueInfo[ii] = new SGMDArrayDataColumnInfo(ueVar, null, ueVar.getValueType());
        ueInfo[ii].setColumnType(ueColumnType);
        ehInfo[ii] = new SGMDArrayDataColumnInfo(ehVar, null, ehVar.getValueType());
        ehInfo[ii].setColumnType(ehColumnType);
      }

      SGMDArrayDataColumnInfo[] tlInfo = new SGMDArrayDataColumnInfo[tlList.size()];
      SGMDArrayDataColumnInfo[] thInfo = new SGMDArrayDataColumnInfo[thList.size()];
      for (int ii = 0; ii < tlInfo.length; ii++) {
        String tlColumnType, thColumnType;
        SGMDArrayVariable tlVar = tlList.get(ii);
        SGMDArrayVariable thVar = thList.get(ii);
        StringBuilder sb = new StringBuilder();
        String thName = thVar.getName();

        // tick label
        sb.append(TICK_LABEL);
        sb.append(SGDataColumnTitleUtility.MID_COLUMN);
        sb.append(thName);
        tlColumnType = sb.toString();

        // error bar holder
        SGDataColumnInfo thInfoX = SGDataColumnInfoUtility.findColumnWithName(xInfo, thName);
        thColumnType = (thInfoX != null) ? X_VALUE : Y_VALUE;

        tlInfo[ii] = new SGMDArrayDataColumnInfo(tlVar, null, tlVar.getValueType());
        tlInfo[ii].setColumnType(tlColumnType);
        thInfo[ii] = new SGMDArrayDataColumnInfo(thVar, null, thVar.getValueType());
        thInfo[ii].setColumnType(thColumnType);
      }

      SGSXYMDArrayMultipleData data =
          new SGSXYMDArrayMultipleData(
              dataLast.getMDArrayFile(),
              obs,
              xInfo,
              yInfo,
              leInfo,
              ueInfo,
              ehInfo,
              tlInfo,
              thInfo,
              dataLast.mStride,
              dataLast.mTickLabelStride,
              dataLast.isStrideAvailable());
      data.setOrigin(dataLast.getOriginMap());
      data.setDecimalPlaces(dataLast.getDecimalPlaces());
      data.setExponent(dataLast.getExponent());
      data.setTimeStride(dataLast.getTimeStride());

      return data;
    }
  }
}
