package jp.riken.brain.ni.samuraigraph.data;

import java.util.*;
import java.util.function.Function;
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
      final Function<String, SGNetCDFVariable> findVariable = ncFile::findVariable;

      data.mXVariables =
          SGDataMiscUtility.resolveVariables(sp.xNames, findVariable, SGNetCDFVariable[]::new);
      data.mYVariables =
          SGDataMiscUtility.resolveVariables(sp.yNames, findVariable, SGNetCDFVariable[]::new);
      data.mLowerErrorVariables =
          SGDataMiscUtility.resolveVariables(sp.lNames, findVariable, SGNetCDFVariable[]::new);
      data.mUpperErrorVariables =
          SGDataMiscUtility.resolveVariables(sp.uNames, findVariable, SGNetCDFVariable[]::new);
      data.mErrorBarHolderVariables =
          SGDataMiscUtility.resolveVariables(sp.ehNames, findVariable, SGNetCDFVariable[]::new);
      data.mTickLabelVariables =
          SGDataMiscUtility.resolveVariables(sp.tNames, findVariable, SGNetCDFVariable[]::new);
      data.mTickLabelHolderVariables =
          SGDataMiscUtility.resolveVariables(sp.thNames, findVariable, SGNetCDFVariable[]::new);

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
      sp.xNames = SGDataMiscUtility.getNames(data.mXVariables);
      sp.yNames = SGDataMiscUtility.getNames(data.mYVariables);
      if (data.isErrorBarAvailable()) {
        sp.lNames = SGDataMiscUtility.getNames(data.mLowerErrorVariables);
        sp.uNames = SGDataMiscUtility.getNames(data.mUpperErrorVariables);
        sp.ehNames = SGDataMiscUtility.getNames(data.mErrorBarHolderVariables);
      }
      if (data.mTickLabelVariables != null) {
        sp.tNames = SGDataMiscUtility.getNames(data.mTickLabelVariables);
        sp.thNames = SGDataMiscUtility.getNames(data.mTickLabelHolderVariables);
      }
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
      SGDataMiscUtility.setSXYMultipleVariableNameAttributes(
          el,
          data.isErrorBarAvailable(),
          data.mTickLabelVariables != null,
          SGDataTextUtility.bindVariableNamesInBracket(data.mXVariables),
          SGDataTextUtility.bindVariableNamesInBracket(data.mYVariables),
          () -> SGDataTextUtility.bindVariableNamesInBracket(data.mLowerErrorVariables),
          () -> SGDataTextUtility.bindVariableNamesInBracket(data.mUpperErrorVariables),
          () -> SGDataTextUtility.bindVariableNamesInBracket(data.mErrorBarHolderVariables),
          () -> SGDataTextUtility.bindVariableNamesInBracket(data.mTickLabelVariables),
          () -> SGDataTextUtility.bindVariableNamesInBracket(data.mTickLabelHolderVariables));
    }

    // stride
    if (!data.isIndexAvailable()) {
      el.setAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION, data.mStride.toString());
    }
    SGDataMiscUtility.setTickLabelArraySection(el, data);

    return true;
  }
}
