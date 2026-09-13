package jp.riken.brain.ni.samuraigraph.data;

import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.*;
import jp.riken.brain.ni.samuraigraph.base.SGConstants;
import org.w3c.dom.Element;
import ucar.ma2.*;
import ucar.nc2.*;
import ucar.nc2.dataset.*;
import ucar.nc2.write.*;

class SGSXYMDArrayMultipleDataExporter {

  private final SGSXYMDArrayMultipleData data;

  public SGSXYMDArrayMultipleDataExporter(final SGSXYMDArrayMultipleData data) {
    this.data = data;
  }

  public boolean writeProperty(Element el, SGExportParameter params) {
    if (data.callSuperWriteProperty(el, params) == false) {
      return false;
    }
    SGConstants.OPERATION type = params.getType();
    if (SGDataMiscUtility.isArchiveDataSetOperation(type)
        || SGConstants.OPERATION.SAVE_TO_PROPERTY_FILE.equals(type)) {

      String value = null;
      if (data.isDimensionPicked()) {
        SGMDArrayVariable xVar = data.getXVariable();
        SGMDArrayVariable yVar = data.getYVariable();
        if (xVar != null) {
          value = data.bindVariableNames(xVar, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, value);
        }
        if (yVar != null) {
          value = data.bindVariableNames(yVar, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, value);
        }
        if (data.isErrorBarAvailable()) {
          SGMDArrayVariable lVar = data.getLowerErrorVariable();
          value = data.bindVariableNames(lVar, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAME, value);
          SGMDArrayVariable uVar = data.getUpperErrorVariable();
          value = data.bindVariableNames(uVar, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAME, value);
          SGMDArrayVariable hVar = data.getErrorBarHolderVariable();
          value = data.bindVariableNames(hVar, false);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAME, value);
        }
        if (data.isTickLabelAvailable()) {
          SGMDArrayVariable tVar = data.getTickLabelVariable();
          value = data.bindVariableNames(tVar, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_NAME, value);
          SGMDArrayVariable hVar = data.getTickLabelHolderVariable();
          value = data.bindVariableNames(hVar, false);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_NAME, value);
        }

        String pickUpIndicesStr = data.mPickUpDimensionInfo.getIndices().toString();
        el.setAttribute(SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION_INDICES, pickUpIndicesStr);

        StringBuilder sb = new StringBuilder();
        SGMDArrayVariable[] vars = data.getVariables();
        int cnt = 0;
        for (int ii = 0; ii < vars.length; ii++) {
          Integer index = vars[ii].getDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
          if (index == null || index == -1) {
            continue;
          }
          if (cnt > 0) {
            sb.append(',');
          }
          sb.append(vars[ii].getName());
          sb.append(':');
          sb.append(index);
          cnt++;
        }
        el.setAttribute(SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION, sb.toString());

      } else {
        value = data.bindVariableNamesInBracket(data.mXVariables, true);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, value);
        value = data.bindVariableNamesInBracket(data.mYVariables, true);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, value);
        if (data.isErrorBarAvailable()) {
          value = data.bindVariableNamesInBracket(data.mLowerErrorVariables, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, value);
          value = data.bindVariableNamesInBracket(data.mUpperErrorVariables, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, value);
          value = data.bindVariableNamesInBracket(data.mErrorBarHolderVariables, false);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, value);
        }
        if (data.isTickLabelAvailable()) {
          value = data.bindVariableNamesInBracket(data.mTickLabelVariables, true);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_NAMES, value);
          value = data.bindVariableNamesInBracket(data.mTickLabelHolderVariables, false);
          el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_NAMES, value);
        }
      }

      // stride
      el.setAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION, data.mStride.toString());
      if (data.isTickLabelAvailable()) {
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION,
            data.mTickLabelStride.toString());
      }

    } else if (SGConstants.OPERATION.SAVE_TO_DATA_SET_NETCDF.equals(type)) {

      // get variable names
      List<String> xNameList = new ArrayList<String>();
      List<String> yNameList = new ArrayList<String>();
      List<String> leNameList = new ArrayList<String>();
      List<String> ueNameList = new ArrayList<String>();
      List<String> tlNameList = new ArrayList<String>();
      data.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);

      // index variable
      el.setAttribute(SGDataPropertyKeyConstants.KEY_INDEX_VARIABLE_NAME, data.INDEX_DIM_NAME);

      // stride as the index stride
      el.setAttribute(SGDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION, data.mStride.toString());

      if (data.isTickLabelAvailable()) {
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION,
            data.mTickLabelStride.toString());
      }

      String value = null;
      if (xNameList.size() == 0) {
        value = data.bindVariableNameInBracket(data.X_VALUE_VAR_NAME);
      } else {
        value = SGDataTextUtility.bindVariableNamesInBracket(xNameList);
      }
      el.setAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, value);

      if (yNameList.size() == 0) {
        value = data.bindVariableNameInBracket(data.Y_VALUE_VAR_NAME);
      } else {
        value = SGDataTextUtility.bindVariableNamesInBracket(yNameList);
      }
      el.setAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, value);

      if (data.isErrorBarAvailable()) {
        value = SGDataTextUtility.bindVariableNamesInBracket(leNameList);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, value);
        value = SGDataTextUtility.bindVariableNamesInBracket(ueNameList);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, value);
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mErrorBarHolderVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, value);
      }

      if (data.isTickLabelAvailable()) {
        value = SGDataTextUtility.bindVariableNamesInBracket(tlNameList);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_NAMES, value);
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mTickLabelHolderVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_NAMES, value);
      }

      if (data.isDimensionPicked()) {
        el.setAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME, data.PICKUP_DIM_NAME);
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION_INDICES,
            data.mPickUpDimensionInfo.getIndices().toString());
      }
    }

    return true;
  }

  SGSXYMDArrayMultipleData.SXYExportInfo exportCommon(
      SGExportParameter mode, SGDataBufferPolicy policy) {
    SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
    final boolean all = sxyPolicy.isAllValuesGotten();
    final boolean shift = sxyPolicy.isShiftValuesContained();
    final boolean archiveFlag = SGDataMiscUtility.isArchiveDataSetOperation(mode.getType());
    final boolean exportFlag = (shift || (data.hasEffectiveStride() && !all)) && !archiveFlag;
    boolean xValid = true;
    boolean yValid = true;

    // gets arrays of variables in the same order of XY variables
    SGMDArrayVariable[] leVars = null;
    SGMDArrayVariable[] ueVars = null;
    if (data.isErrorBarAvailable()) {
      leVars = data.getVariablesInXYOrder(data.mLowerErrorVariables, data.mErrorBarHolderVariables);
      ueVars = data.getVariablesInXYOrder(data.mUpperErrorVariables, data.mErrorBarHolderVariables);
    }
    SGMDArrayVariable[] tlVars = null;
    if (data.isTickLabelAvailable()) {
      tlVars = data.getVariablesInXYOrder(data.mTickLabelVariables, data.mTickLabelHolderVariables);
    }

    SGMDArrayVariable[] vars = data.getVariables();
    String[] xNames = null;
    if (data.mXVariables == null || data.mXVariables.length == 0) {
      if (exportFlag) {
        xNames = new String[] {data.getUniqueVarName(data.DEFAULT_VAR_NAME_BASE_X, vars)};
      } else {
        xValid = false;
      }
    } else {
      xNames = data.getNames(data.mXVariables);
    }
    String[] yNames = null;
    if (data.mYVariables == null || data.mYVariables.length == 0) {
      if (exportFlag) {
        yNames = new String[] {data.getUniqueVarName(data.DEFAULT_VAR_NAME_BASE_Y, vars)};
      } else {
        yValid = false;
      }
    } else {
      yNames = data.getNames(data.mYVariables);
    }
    String[] leNames = null;
    String[] ueNames = null;
    if (data.isErrorBarAvailable()) {
      leNames = data.getNames(leVars);
      ueNames = data.getNames(ueVars);
    }
    String[] tlNames = null;
    if (data.isTickLabelAvailable()) {
      tlNames = data.getNames(tlVars);
    }

    double[][] xValues = null;
    double[][] yValues = null;
    double[][] leValues = null;
    double[][] ueValues = null;
    String[][] tickLabels = null;
    SGIDataSource src = data.getDataSource();
    if ((src instanceof SGVirtualMDArrayFile && archiveFlag) || !archiveFlag) {
      // export data to a file or save virtual MDArray data to an archive data set
      // file
      SGSXYMultipleDataBuffer buffer = (SGSXYMultipleDataBuffer) data.getDataBuffer(policy);
      xValues = buffer.getXValues();
      yValues = buffer.getYValues();
      leValues = buffer.getLowerErrorValues();
      ueValues = buffer.getUpperErrorValues();
      tickLabels = buffer.getTickLabels();
    }

    SGSXYMDArrayMultipleData.SXYExportInfo info = new SGSXYMDArrayMultipleData.SXYExportInfo();
    info.xNames = xNames;
    info.yNames = yNames;
    info.leNames = leNames;
    info.ueNames = ueNames;
    info.tlNames = tlNames;
    info.xValues = xValues;
    info.yValues = yValues;
    info.leValues = leValues;
    info.ueValues = ueValues;
    info.tlValues = tickLabels;
    SGMDArrayVariable.MDArrayDataType[] xDataTypes = null;
    if (xValid) {
      if (data.mXVariables == null || data.mXVariables.length == 0) {
        xDataTypes = new SGMDArrayVariable.MDArrayDataType[1];
        final SGMDArrayVariable.VALUE_TYPE valueType =
            shift ? SGMDArrayVariable.VALUE_TYPE.FLOAT : SGMDArrayVariable.VALUE_TYPE.INTEGER;
        xDataTypes[0] = new SGMDArrayVariable.MDArrayDataType(valueType);
      } else {
        xDataTypes = new SGMDArrayVariable.MDArrayDataType[data.mXVariables.length];
        for (int ii = 0; ii < data.mXVariables.length; ii++) {
          xDataTypes[ii] = data.getExportNumberDataType(data.mXVariables[ii], mode, sxyPolicy);
        }
      }
    }
    SGMDArrayVariable.MDArrayDataType[] yDataTypes = null;
    if (yValid) {
      if (data.mYVariables == null || data.mYVariables.length == 0) {
        yDataTypes = new SGMDArrayVariable.MDArrayDataType[1];
        final SGMDArrayVariable.VALUE_TYPE valueType =
            shift ? SGMDArrayVariable.VALUE_TYPE.FLOAT : SGMDArrayVariable.VALUE_TYPE.INTEGER;
        yDataTypes[0] = new SGMDArrayVariable.MDArrayDataType(valueType);
      } else {
        yDataTypes = new SGMDArrayVariable.MDArrayDataType[data.mYVariables.length];
        for (int ii = 0; ii < data.mYVariables.length; ii++) {
          yDataTypes[ii] = data.getExportNumberDataType(data.mYVariables[ii], mode, sxyPolicy);
        }
      }
    }
    SGMDArrayVariable.MDArrayDataType[] leDataTypes = null;
    SGMDArrayVariable.MDArrayDataType[] ueDataTypes = null;
    if (data.isErrorBarAvailable()) {
      leDataTypes = data.getDataTypes(leVars);
      ueDataTypes = data.getDataTypes(ueVars);
    }
    SGMDArrayVariable.MDArrayDataType[] tlDataTypes = null;
    if (data.isTickLabelAvailable()) {
      tlDataTypes = data.getDataTypes(tlVars);
    }

    info.xDataTypes = xDataTypes;
    info.yDataTypes = yDataTypes;
    info.leDataTypes = leDataTypes;
    info.ueDataTypes = ueDataTypes;
    info.tlDataTypes = tlDataTypes;

    info.xValid = xValid;
    info.yValid = yValid;

    return info;
  }

  protected boolean writeData(NetcdfFormatWriter writer) {

    // get the number of points
    final int num = data.getAllPointsNumber();

    // whether equal variables are assigned to lower and upper errors
    final boolean bEqualLowerUpperErrorVariable = data.hasEqualLowerUpperErrorVariable();

    // get variable names
    List<String> xNameList = new ArrayList<String>();
    List<String> yNameList = new ArrayList<String>();
    List<String> leNameList = new ArrayList<String>();
    List<String> ueNameList = new ArrayList<String>();
    List<String> tlNameList = new ArrayList<String>();
    data.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);

    // index
    Dimension indexDim = writer.findDimension(data.INDEX_DIM_NAME);
    if (!data.writeSequentialIntegerNumbers(writer, data.INDEX_DIM_NAME, indexDim.getLength())) {
      return false;
    }

    // time
    if (!data.writeTimeData(writer)) {
      return false;
    }

    // pickup
    Dimension pickUpDim = writer.findDimension(data.PICKUP_DIM_NAME);
    if (pickUpDim != null) {
      if (!data.writeSequentialIntegerNumbers(
          writer, data.PICKUP_DIM_NAME, pickUpDim.getLength())) {
        return false;
      }
    }

    // x-values
    if (xNameList.size() == 0) {
      if (!data.writeSequentialDoubleNumbers(writer, data.X_VALUE_VAR_NAME, num)) {
        return false;
      }
    } else {
      if (!writeDoubleData(writer, xNameList)) {
        return false;
      }
    }

    // y-values
    if (yNameList.size() == 0) {
      if (!data.writeSequentialDoubleNumbers(writer, data.Y_VALUE_VAR_NAME, num)) {
        return false;
      }
    } else {
      if (!writeDoubleData(writer, yNameList)) {
        return false;
      }
    }

    // error values
    if (data.isErrorBarAvailable()) {
      if (!writeDoubleData(writer, leNameList)) {
        return false;
      }
      if (!bEqualLowerUpperErrorVariable) {
        if (!writeDoubleData(writer, ueNameList)) {
          return false;
        }
      }
    }

    // tick label
    if (data.isTickLabelAvailable()) {
      if (!writeStringData(writer, tlNameList)) {
        return false;
      }
    }

    return true;
  }

  protected boolean addVariables(NetcdfFormatWriter.Builder builder) {

    // get the number of points
    final int num = data.getAllPointsNumber();

    // whether equal variables are assigned to lower and upper errors
    final boolean bEqualLowerUpperErrorVariable = data.hasEqualLowerUpperErrorVariable();

    // whether y variable is picked
    List<SGMDArrayVariable> pickUpVarList = data.getPickUpMDArrayVariables();
    boolean yVariablePicked = false;
    if (data.isDimensionPicked()) {
      if (pickUpVarList.contains(data.mYVariables[0])) {
        yVariablePicked = true;
      }
    }

    // get variable names
    List<String> xNameList = new ArrayList<String>();
    List<String> yNameList = new ArrayList<String>();
    List<String> leNameList = new ArrayList<String>();
    List<String> ueNameList = new ArrayList<String>();
    List<String> tlNameList = new ArrayList<String>();
    data.getVariableNames(xNameList, yNameList, leNameList, ueNameList, tlNameList);

    // add index dimension
    Dimension indexDim = builder.addDimension(data.INDEX_DIM_NAME, num);

    // add time dimensions
    Dimension timeDim = data.addTimeVariable(builder);

    // add pick up dimension
    Dimension pickUpDim = null;
    if (data.isDimensionPicked()) {
      SGMDArrayPickUpDimensionInfo pickUpInfo =
          (SGMDArrayPickUpDimensionInfo) data.getPickUpDimensionInfo();
      Map<String, Integer> dimensionMap = pickUpInfo.getDimensionMap();
      Iterator<Map.Entry<String, Integer>> itr = dimensionMap.entrySet().iterator();
      while (itr.hasNext()) {
        Map.Entry<String, Integer> entry = itr.next();
        String name = entry.getKey();
        Integer dimension = entry.getValue();
        if (dimension == null || dimension == -1) {
          continue;
        }
        SGMDArrayVariable var = data.findVariable(name);
        int[] dims = var.getDimensions();
        final int len = dims[dimension];
        pickUpDim = builder.addDimension(data.PICKUP_DIM_NAME, len);
        break;
      }
    }

    // add index variable
    if (!data.addSequentialIntegerNumberVariable(builder, indexDim, data.INDEX_DIM_NAME)) {
      return false;
    }

    Dimension pDim;

    // add x-variables
    pDim = (data.isDimensionPicked() && !yVariablePicked) ? pickUpDim : null;
    if (xNameList.size() == 0) {
      if (!data.addDoubleVariable(builder, indexDim, null, pDim, data.X_VALUE_VAR_NAME)) {
        return false;
      }
    } else {
      for (int ii = 0; ii < xNameList.size(); ii++) {
        String name = xNameList.get(ii);
        if (!data.addDoubleVariable(builder, indexDim, timeDim, pDim, name)) {
          return false;
        }
      }
    }

    // add y-variables
    pDim = (data.isDimensionPicked() && yVariablePicked) ? pickUpDim : null;
    if (yNameList.size() == 0) {
      if (!data.addDoubleVariable(builder, indexDim, null, pDim, data.Y_VALUE_VAR_NAME)) {
        return false;
      }
    } else {
      for (int ii = 0; ii < yNameList.size(); ii++) {
        String name = yNameList.get(ii);
        if (!data.addDoubleVariable(builder, indexDim, timeDim, pDim, name)) {
          return false;
        }
      }
    }

    // add error variables
    if (data.isErrorBarAvailable()) {
      for (int ii = 0; ii < leNameList.size(); ii++) {
        String name = leNameList.get(ii);
        if (!data.addDoubleVariable(builder, indexDim, timeDim, pickUpDim, name)) {
          return false;
        }
      }
      if (!bEqualLowerUpperErrorVariable) {
        for (int ii = 0; ii < ueNameList.size(); ii++) {
          String name = ueNameList.get(ii);
          if (!data.addDoubleVariable(builder, indexDim, timeDim, pickUpDim, name)) {
            return false;
          }
        }
      }
    }

    // add tick label variables
    if (data.isTickLabelAvailable()) {
      for (int ii = 0; ii < tlNameList.size(); ii++) {
        // adds a dimension for text strings
        String name = tlNameList.get(ii);
        SGMDArrayVariable var = data.findVariable(name);
        final int maxLength = data.getMaxLength(var);
        if (!data.addStringVariable(builder, indexDim, timeDim, pickUpDim, name, maxLength)) {
          return false;
        }
      }
    }

    // add time variable
    if (timeDim != null) {
      if (!data.addSequentialIntegerNumberVariable(builder, timeDim, data.TIME_DIM_NAME)) {
        return false;
      }
    }

    // add pick up variable
    if (pickUpDim != null) {
      if (!data.addSequentialIntegerNumberVariable(builder, pickUpDim, data.PICKUP_DIM_NAME)) {
        return false;
      }
    }

    return true;
  }

  boolean writeDoubleData(NetcdfFormatWriter writer, List<String> nameList) {
    for (int ii = 0; ii < nameList.size(); ii++) {
      String varName = nameList.get(ii);
      SGMDArrayVariable mdVar = data.findVariable(varName);
      Map<String, Integer> mdArrayIndexMap = new HashMap<String, Integer>();
      Variable ncVar = writer.findVariable(varName);
      List<Dimension> ncDimList = ncVar.getDimensions();
      for (Dimension dim : ncDimList) {
        String dimName = dim.getShortName();
        Integer index = null;
        if (data.INDEX_DIM_NAME.equals(dimName)) {
          index = mdVar.getDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION);
        } else if (data.TIME_DIM_NAME.equals(dimName)) {
          index = mdVar.getDimensionIndex(SGMDArrayConstants.KEY_TIME_DIMENSION);
        } else if (data.PICKUP_DIM_NAME.equals(dimName)) {
          index = mdVar.getDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        }
        if (index != null && index != -1) {
          mdArrayIndexMap.put(dimName, index);
        }
      }
      final int dimSize = mdArrayIndexMap.size();
      if (dimSize == 1) {
        if (!data.write1DDoubleArray(writer, varName, ncDimList, mdArrayIndexMap)) {
          return false;
        }
      } else if (dimSize == 2) {
        if (!data.write2DDoubleArray(writer, varName, ncDimList, mdArrayIndexMap)) {
          return false;
        }
      } else if (dimSize == 3) {
        if (!data.write3DDoubleArray(writer, varName, ncDimList, mdArrayIndexMap)) {
          return false;
        }
      } else {
        throw new Error("Unsupported dimension size: " + dimSize);
      }
    }
    return true;
  }

  boolean writeStringData(NetcdfFormatWriter writer, List<String> nameList) {
    for (int ii = 0; ii < nameList.size(); ii++) {
      String varName = nameList.get(ii);
      SGMDArrayVariable mdVar = data.findVariable(varName);
      Map<String, Integer> mdArrayIndexMap = new HashMap<String, Integer>();
      Variable ncVar = writer.findVariable(varName);
      List<Dimension> ncDimList = ncVar.getDimensions();
      for (Dimension dim : ncDimList) {
        String dimName = dim.getShortName();
        Integer index = null;
        if (data.INDEX_DIM_NAME.equals(dimName)) {
          index = mdVar.getDimensionIndex(SGMDArrayConstants.KEY_GENERIC_DIMENSION);
        } else if (data.TIME_DIM_NAME.equals(dimName)) {
          index = mdVar.getDimensionIndex(SGMDArrayConstants.KEY_TIME_DIMENSION);
        } else if (data.PICKUP_DIM_NAME.equals(dimName)) {
          index = mdVar.getDimensionIndex(SGMDArrayConstants.KEY_SXY_PICKUP_DIMENSION);
        } else {
          continue;
        }
        mdArrayIndexMap.put(dimName, index);
      }
      final int dimSize = mdArrayIndexMap.size(); // a hidden dimension is contained
      if (dimSize == 1) {
        if (!data.write1DStringArray(writer, varName, ncDimList, mdArrayIndexMap, ii)) {
          return false;
        }
      } else if (dimSize == 2) {
        if (!data.write2DStringArray(writer, varName, ncDimList, mdArrayIndexMap, ii)) {
          return false;
        }
      } else if (dimSize == 3) {
        if (!data.write3DStringArray(writer, varName, ncDimList, mdArrayIndexMap, ii)) {
          return false;
        }
      } else {
        throw new Error("Unsupported dimension size: " + dimSize);
      }
    }
    return true;
  }
}
