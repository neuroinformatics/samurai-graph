package jp.riken.brain.ni.samuraigraph.data;

import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.*;
import ucar.nc2.Dimension;

class SGSXYNetCDFMultipleDataAccess {

  private final SGSXYNetCDFMultipleData owner;

  public SGSXYNetCDFMultipleDataAccess(final SGSXYNetCDFMultipleData owner) {
    this.owner = owner;
  }

  public SGISXYTypeSingleData[] getSXYDataArray() {
    SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) owner.getCache();
    SGSXYDataCache[] sxyCacheArray = null;
    if (cache != null) {
      sxyCacheArray = cache.mCacheArray;
    }
    if (owner.isDimensionPicked()) {
      final int len = owner.mDimensionIndices.length;
      final List<Dimension> dimList = owner.getNetcdfFile().getDimensions();
      final String dimName = owner.getDimensionName();
      final SGNetCDFVariable xVar = owner.getXVariable();
      final SGNetCDFVariable yVar = owner.getYVariable();
      final SGNetCDFVariable leVar = owner.getLowerErrorVariable();
      final SGNetCDFVariable ueVar = owner.getUpperErrorVariable();
      final SGNetCDFVariable ehVar = owner.getErrorBarHolderVariable();
      final SGNetCDFVariable tlVar = owner.getTickLabelVariable();
      final SGNetCDFVariable thVar = owner.getTickLabelHolderVariable();
      final SGNetCDFVariable timeVar = (owner.mTimeVariable != null) ? owner.mTimeVariable : null;
      final SGNetCDFVariable indexVar =
          (owner.mIndexVariable != null) ? owner.mIndexVariable : null;

      SGNetCDFDataColumnInfo xInfo =
          SGDataFileUtility.createDataColumnInfo(xVar, SGDataColumnTypeConstants.X_VALUE);
      SGNetCDFDataColumnInfo yInfo =
          SGDataFileUtility.createDataColumnInfo(yVar, SGDataColumnTypeConstants.Y_VALUE);
      SGNetCDFDataColumnInfo leInfo = null;
      SGNetCDFDataColumnInfo ueInfo = null;
      SGNetCDFDataColumnInfo ehInfo = null;
      if (leVar != null && ueVar != null && ehVar != null) {
        leInfo =
            SGDataFileUtility.createErrorBarInfo(
                leVar, SGDataColumnTypeConstants.LOWER_ERROR_VALUE, leVar, ueVar, ehVar);
        ueInfo =
            SGDataFileUtility.createErrorBarInfo(
                ueVar, SGDataColumnTypeConstants.UPPER_ERROR_VALUE, leVar, ueVar, ehVar);
        ehInfo = (SGNetCDFDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
      }
      SGNetCDFDataColumnInfo tlInfo = null;
      SGNetCDFDataColumnInfo thInfo = null;
      if (tlVar != null && thVar != null) {
        tlInfo =
            SGDataFileUtility.createDataColumnInfo(
                tlVar, SGDataColumnTypeConstants.TICK_LABEL, thVar.getName());
        thInfo = (SGNetCDFDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
      }
      SGNetCDFDataColumnInfo timeInfo = null;
      if (timeVar != null) {
        timeInfo =
            SGDataFileUtility.createDataColumnInfo(
                timeVar, SGDataColumnTypeConstants.ANIMATION_FRAME);
      }
      SGNetCDFDataColumnInfo indexInfo = null;
      if (indexVar != null) {
        indexInfo =
            SGDataFileUtility.createDataColumnInfo(indexVar, SGDataColumnTypeConstants.INDEX);
      }

      final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[len];
      for (int ii = 0; ii < dataArray.length; ii++) {
        SGSXYNetCDFData data;
        if (owner.getXVariable() instanceof SGDateVariable
            || owner.getYVariable() instanceof SGDateVariable) {
          data =
              new SGSXYNetCDFDateData(
                  owner.getNetcdfFile(),
                  owner.getDataSourceObserver(),
                  xInfo,
                  yInfo,
                  leInfo,
                  ueInfo,
                  ehInfo,
                  tlInfo,
                  thInfo,
                  timeInfo,
                  indexInfo,
                  owner.getStride(),
                  owner.getIndexStride(),
                  owner.getTickLabelStride(),
                  owner.isStrideAvailable());
        } else {
          data =
              new SGSXYNetCDFData(
                  owner.getNetcdfFile(),
                  owner.getDataSourceObserver(),
                  xInfo,
                  yInfo,
                  leInfo,
                  ueInfo,
                  ehInfo,
                  tlInfo,
                  thInfo,
                  timeInfo,
                  indexInfo,
                  owner.getStride(),
                  owner.getIndexStride(),
                  owner.getTickLabelStride(),
                  owner.isStrideAvailable());
        }
        for (Dimension dim : dimList) {
          String name = dim.getShortName();
          Integer origin;
          if (name.equals(dimName)) {
            origin = owner.mDimensionIndices[ii];
          } else {
            origin = owner.mOriginMap.get(name);
            if (origin == null) {
              origin = Integer.valueOf(0);
            }
          }
          data.setOrigin(name, origin.intValue());
        }

        // sets attributes for tick labels
        data.setDecimalPlaces(owner.getDecimalPlaces());
        data.setExponent(owner.getExponent());
        data.setDateFormat(owner.getDateFormat());

        // sets the stride of time
        data.setTimeStride(owner.mTimeStride);

        // sets the shift
        data.setShift(owner.getShift());

        // sets the cache
        if (sxyCacheArray != null) {
          data.setCache(sxyCacheArray[ii]);
        }

        dataArray[ii] = (SGISXYTypeSingleData) data;
      }

      SGDataViewerUtility.syncDataValueHistory(owner.mEditedDataValueList, dataArray);

      return dataArray;

    } else {
      SGNetCDFVariable varSingle = owner.getSingleVariable();
      SGNetCDFVariable[] varMultiple = owner.getMultipleVariables();
      final int len = varMultiple.length;
      final SGNetCDFVariable timeVar = owner.mTimeVariable;
      final SGNetCDFVariable indexVar = owner.mIndexVariable;
      List<SGNetCDFVariable> varList = owner.getNetcdfFile().getVariables();
      final boolean by = owner.hasMultipleYValues();
      final boolean be = owner.isErrorBarAvailable();
      final boolean bt = owner.isTickLabelAvailable();
      final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[len];
      for (int ii = 0; ii < dataArray.length; ii++) {
        SGNetCDFVariable xVar = by ? varSingle : varMultiple[ii];
        SGNetCDFVariable yVar = by ? varMultiple[ii] : varSingle;
        SGNetCDFVariable leVar = null;
        SGNetCDFVariable ueVar = null;
        SGNetCDFVariable ehVar = null;
        if (be) {
          for (int jj = 0; jj < owner.mErrorBarHolderVariables.length; jj++) {
            SGNetCDFVariable var = owner.mErrorBarHolderVariables[jj];
            if (xVar.equals(var) || yVar.equals(var)) {
              ehVar = var;
              leVar = owner.mLowerErrorVariables[jj];
              ueVar = owner.mUpperErrorVariables[jj];
              break;
            }
          }
        }
        SGNetCDFVariable tlVar = null;
        SGNetCDFVariable thVar = null;
        if (bt && owner.mTickLabelHolderVariables != null) {
          // checks whether the holder variable is null because isTickLabelAvailable
          // method
          // returns true even when date variable is used and mTickLabelHolderVariables is
          // null
          for (int jj = 0; jj < owner.mTickLabelHolderVariables.length; jj++) {
            SGNetCDFVariable var = owner.mTickLabelHolderVariables[jj];
            if (xVar.equals(var) || yVar.equals(var)) {
              thVar = var;
              tlVar = owner.mTickLabelVariables[jj];
              break;
            }
          }
        }

        SGNetCDFDataColumnInfo xInfo =
            SGDataFileUtility.createDataColumnInfo(xVar, SGDataColumnTypeConstants.X_VALUE);
        SGNetCDFDataColumnInfo yInfo =
            SGDataFileUtility.createDataColumnInfo(yVar, SGDataColumnTypeConstants.Y_VALUE);
        SGNetCDFDataColumnInfo leInfo = null;
        SGNetCDFDataColumnInfo ueInfo = null;
        SGNetCDFDataColumnInfo ehInfo = null;
        if (leVar != null && ueVar != null && ehVar != null) {
          leInfo =
              SGDataFileUtility.createErrorBarInfo(
                  leVar, SGDataColumnTypeConstants.LOWER_ERROR_VALUE, leVar, ueVar, ehVar);
          ueInfo =
              SGDataFileUtility.createErrorBarInfo(
                  ueVar, SGDataColumnTypeConstants.UPPER_ERROR_VALUE, leVar, ueVar, ehVar);
          ehInfo = (SGNetCDFDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
        }
        SGNetCDFDataColumnInfo tlInfo = null;
        SGNetCDFDataColumnInfo thInfo = null;
        if (tlVar != null && thVar != null) {
          tlInfo =
              SGDataFileUtility.createDataColumnInfo(
                  tlVar, SGDataColumnTypeConstants.TICK_LABEL, thVar.getName());
          thInfo = (SGNetCDFDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
        }
        SGNetCDFDataColumnInfo timeInfo = null;
        if (timeVar != null) {
          timeInfo =
              SGDataFileUtility.createDataColumnInfo(
                  timeVar, SGDataColumnTypeConstants.ANIMATION_FRAME);
        }
        SGNetCDFDataColumnInfo indexInfo = null;
        if (indexVar != null) {
          indexInfo =
              SGDataFileUtility.createDataColumnInfo(indexVar, SGDataColumnTypeConstants.INDEX);
        }

        SGSXYNetCDFData data;
        if (owner.getNetcdfFile().findVariable(xVar.getName()) instanceof SGDateVariable
            || owner.getNetcdfFile().findVariable(yVar.getName()) instanceof SGDateVariable) {
          data =
              new SGSXYNetCDFDateData(
                  owner.getNetcdfFile(),
                  owner.getDataSourceObserver(),
                  xInfo,
                  yInfo,
                  leInfo,
                  ueInfo,
                  ehInfo,
                  tlInfo,
                  thInfo,
                  timeInfo,
                  indexInfo,
                  owner.getStride(),
                  owner.getIndexStride(),
                  owner.getTickLabelStride(),
                  owner.isStrideAvailable());
        } else {
          data =
              new SGSXYNetCDFData(
                  owner.getNetcdfFile(),
                  owner.getDataSourceObserver(),
                  xInfo,
                  yInfo,
                  leInfo,
                  ueInfo,
                  ehInfo,
                  tlInfo,
                  thInfo,
                  timeInfo,
                  indexInfo,
                  owner.getStride(),
                  owner.getIndexStride(),
                  owner.getTickLabelStride(),
                  owner.isStrideAvailable());
        }
        for (SGNetCDFVariable var : varList) {
          if (var.isCoordinateVariable()) {
            String name = var.getName();
            if (owner.mOriginMap.containsKey(name)) {
              final int origin = owner.mOriginMap.get(name);
              data.setOrigin(name, origin);
            }
          }
        }

        // sets attributes for tick labels
        data.setDecimalPlaces(owner.getDecimalPlaces());
        data.setExponent(owner.getExponent());
        data.setDateFormat(owner.getDateFormat());

        // sets the stride of time
        data.setTimeStride(owner.mTimeStride);

        // sets the shift
        data.setShift(owner.getShift());

        // sets the cache
        if (sxyCacheArray != null) {
          data.setCache(sxyCacheArray[ii]);
        }

        dataArray[ii] = (SGISXYTypeSingleData) data;
      }

      SGDataViewerUtility.syncDataValueHistory(owner.mEditedDataValueList, dataArray);

      return dataArray;
    }
  }
}
