package jp.riken.brain.ni.samuraigraph.data;

import java.util.*;
import jp.riken.brain.ni.samuraigraph.base.*;
import org.w3c.dom.Element;

class SGSXYNetCDFMultipleDataPropertyIO {

  private final SGSXYNetCDFMultipleData data;

  public SGSXYNetCDFMultipleDataPropertyIO(final SGSXYNetCDFMultipleData data) {
    this.data = data;
  }

  public boolean setProperties(SGProperties p) {
    if (!(p instanceof SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties)) {
      return false;
    }
    if (data.callSuperSetProperties(p) == false) {
      return false;
    }
    SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties sp =
        (SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties) p;
    SGNetCDFPickUpDimensionInfo pickUpInfo = sp.mPickUpInfo;
    if (pickUpInfo != null) {
      data.mXVariables = data.findVariables(sp.xNames);
      data.mYVariables = data.findVariables(sp.yNames);
      if (sp.lNames != null && sp.uNames != null) {
        data.mLowerErrorVariables = data.findVariables(sp.lNames);
        data.mUpperErrorVariables = data.findVariables(sp.uNames);
        data.mErrorBarHolderVariables = data.findVariables(sp.ehNames);
      } else {
        data.mLowerErrorVariables = null;
        data.mUpperErrorVariables = null;
        data.mErrorBarHolderVariables = null;
      }

      if (sp.tNames != null) {
        data.mTickLabelVariables = data.findVariables(sp.tNames);
        data.mTickLabelHolderVariables = data.findVariables(sp.thNames);
      } else {
        data.mTickLabelVariables = null;
        data.mTickLabelHolderVariables = null;
      }

    } else {

      SGNetCDFFile ncFile = data.getNetcdfFile();

      SGNetCDFVariable[] xVars = new SGNetCDFVariable[sp.xNames.length];
      for (int ii = 0; ii < xVars.length; ii++) {
        xVars[ii] = ncFile.findVariable(sp.xNames[ii]);
      }
      SGNetCDFVariable[] yVars = new SGNetCDFVariable[sp.yNames.length];
      for (int ii = 0; ii < yVars.length; ii++) {
        yVars[ii] = ncFile.findVariable(sp.yNames[ii]);
      }
      SGNetCDFVariable[] lVars = null;
      if (sp.lNames != null) {
        lVars = new SGNetCDFVariable[sp.lNames.length];
        for (int ii = 0; ii < lVars.length; ii++) {
          lVars[ii] = ncFile.findVariable(sp.lNames[ii]);
        }
      }
      SGNetCDFVariable[] uVars = null;
      if (sp.uNames != null) {
        uVars = new SGNetCDFVariable[sp.uNames.length];
        for (int ii = 0; ii < uVars.length; ii++) {
          uVars[ii] = ncFile.findVariable(sp.uNames[ii]);
        }
      }
      SGNetCDFVariable[] ehVars = null;
      if (sp.ehNames != null) {
        ehVars = new SGNetCDFVariable[sp.ehNames.length];
        for (int ii = 0; ii < ehVars.length; ii++) {
          ehVars[ii] = ncFile.findVariable(sp.ehNames[ii]);
        }
      }
      SGNetCDFVariable[] tVars = null;
      if (sp.tNames != null) {
        tVars = new SGNetCDFVariable[sp.tNames.length];
        for (int ii = 0; ii < tVars.length; ii++) {
          tVars[ii] = ncFile.findVariable(sp.tNames[ii]);
        }
      }
      SGNetCDFVariable[] thVars = null;
      if (sp.thNames != null) {
        thVars = new SGNetCDFVariable[sp.thNames.length];
        for (int ii = 0; ii < thVars.length; ii++) {
          thVars[ii] = ncFile.findVariable(sp.thNames[ii]);
        }
      }

      // set to the attributes
      data.mXVariables = xVars;
      data.mYVariables = yVars;
      data.mLowerErrorVariables = lVars;
      data.mUpperErrorVariables = uVars;
      data.mErrorBarHolderVariables = ehVars;
      data.mTickLabelVariables = tVars;
      data.mTickLabelHolderVariables = thVars;

      data.updateIsSingleVariableDateFlag();
    }

    data.setDecimalPlaces(sp.mDecimalPlaces);
    data.setExponent(sp.mExponent);
    data.setPickUpProperties(sp);
    data.setStride(sp.mStride);
    data.setTickLabelStride(sp.mTickLabelStride);

    return true;
  }

  public boolean getProperties(SGProperties p) {
    if (!(p instanceof SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties)) {
      return false;
    }
    if (data.callSuperGetProperties(p) == false) {
      return false;
    }
    SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties sp =
        (SGSXYNetCDFMultipleData.SXYNetCDFMultipleDataProperties) p;
    if (data.isDimensionPicked()) {
      sp.xNames = new String[] {data.getName(data.getXVariable())};
      sp.yNames = new String[] {data.getName(data.getYVariable())};
      if (data.isErrorBarAvailable()) {
        sp.lNames = new String[] {data.getName(data.getLowerErrorVariable())};
        sp.uNames = new String[] {data.getName(data.getUpperErrorVariable())};
        sp.ehNames = new String[] {data.getName(data.getErrorBarHolderVariable())};
      } else {
        sp.lNames = null;
        sp.uNames = null;
        sp.ehNames = null;
      }
      if (data.isTickLabelAvailable()) {
        sp.tNames = new String[] {data.getName(data.getTickLabelVariable())};
        sp.thNames = new String[] {data.getName(data.getTickLabelHolderVariable())};
      } else {
        sp.tNames = null;
        sp.thNames = null;
      }
    } else {
      String[] xNames = new String[data.mXVariables.length];
      for (int ii = 0; ii < xNames.length; ii++) {
        xNames[ii] = data.mXVariables[ii].getName();
      }
      String[] yNames = new String[data.mYVariables.length];
      for (int ii = 0; ii < yNames.length; ii++) {
        yNames[ii] = data.mYVariables[ii].getName();
      }
      String[] lNames = null;
      String[] uNames = null;
      String[] ehNames = null;
      if (data.isErrorBarAvailable()) {
        lNames = new String[data.mLowerErrorVariables.length];
        for (int ii = 0; ii < lNames.length; ii++) {
          lNames[ii] = data.mLowerErrorVariables[ii].getName();
        }
        uNames = new String[data.mUpperErrorVariables.length];
        for (int ii = 0; ii < uNames.length; ii++) {
          uNames[ii] = data.mUpperErrorVariables[ii].getName();
        }
        ehNames = new String[data.mErrorBarHolderVariables.length];
        for (int ii = 0; ii < ehNames.length; ii++) {
          ehNames[ii] = data.mErrorBarHolderVariables[ii].getName();
        }
      }
      String[] tNames = null;
      String[] thNames = null;
      if (data.mTickLabelVariables != null) {
        tNames = new String[data.mTickLabelVariables.length];
        for (int ii = 0; ii < tNames.length; ii++) {
          tNames[ii] = data.mTickLabelVariables[ii].getName();
        }
        thNames = new String[data.mTickLabelHolderVariables.length];
        for (int ii = 0; ii < thNames.length; ii++) {
          thNames[ii] = data.mTickLabelHolderVariables[ii].getName();
        }
      }
      sp.xNames = xNames;
      sp.yNames = yNames;
      sp.lNames = lNames;
      sp.uNames = uNames;
      sp.ehNames = ehNames;
      sp.tNames = tNames;
      sp.thNames = thNames;
    }

    sp.mDecimalPlaces = data.getDecimalPlaces();
    sp.mExponent = data.getExponent();
    sp.mPickUpInfo = (SGNetCDFPickUpDimensionInfo) data.getPickUpDimensionInfo();
    sp.mStride = data.getStride();
    sp.mTickLabelStride = data.getTickLabelStride();

    return true;
  }

  public boolean writeProperty(Element el, final SGExportParameter type) {
    if (data.callSuperWriteProperty(el, type) == false) {
      return false;
    }
    if (data.isDimensionPicked()) {
      el.setAttribute(
          SGDataPropertyKeyConstants.KEY_X_VALUE_NAME, data.getXVariable().getValidName());
      el.setAttribute(
          SGDataPropertyKeyConstants.KEY_Y_VALUE_NAME, data.getYVariable().getValidName());
      if (data.isErrorBarAvailable()) {
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAME,
            data.getLowerErrorVariable().getValidName());
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAME,
            data.getUpperErrorVariable().getValidName());
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAME,
            data.getErrorBarHolderVariable().getValidName());
      }
      if (data.isTickLabelAvailable()) {
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_TICK_LABEL_NAME,
            data.getTickLabelVariable().getValidName());
        el.setAttribute(
            SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_NAME,
            data.getTickLabelHolderVariable().getValidName());
      }
      String dimName = data.getDimensionName();
      if (dimName != null) {
        el.setAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME, dimName);
      }
      SGIntegerSeriesSet pickUpIndices = data.mPickUpDimensionInfo.getIndices();
      el.setAttribute(
          SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION_INDICES, pickUpIndices.toString());

    } else {
      String value = null;
      value = SGDataTextUtility.bindVariableNamesInBracket(data.mXVariables);
      el.setAttribute(SGDataPropertyKeyConstants.KEY_X_VALUE_NAMES, value);
      value = SGDataTextUtility.bindVariableNamesInBracket(data.mYVariables);
      el.setAttribute(SGDataPropertyKeyConstants.KEY_Y_VALUE_NAMES, value);
      if (data.isErrorBarAvailable()) {
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mLowerErrorVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_LOWER_ERROR_VALUE_NAMES, value);
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mUpperErrorVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_UPPER_ERROR_VALUE_NAMES, value);
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mErrorBarHolderVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_ERROR_BAR_HOLDER_NAMES, value);
      }
      if (data.mTickLabelVariables != null) {
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mTickLabelVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_NAMES, value);
        value = SGDataTextUtility.bindVariableNamesInBracket(data.mTickLabelHolderVariables);
        el.setAttribute(SGDataPropertyKeyConstants.KEY_TICK_LABEL_HOLDER_NAMES, value);
      }
    }

    // stride
    if (!data.isIndexAvailable()) {
      el.setAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION, data.mStride.toString());
    }
    if (data.isTickLabelAvailable()) {
      el.setAttribute(
          SGDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION,
          data.mTickLabelStride.toString());
    }

    return true;
  }
}
