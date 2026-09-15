package jp.riken.brain.ni.samuraigraph.data;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGMDArrayConstants.*;

import com.github.neuroinformatics.samurai_graph.lib.hdf5.IHDF5Writer;
import com.github.neuroinformatics.samurai_graph.lib.mdarray.MDDoubleArray;
import com.jmatio.io.MatFileWriter;
import com.jmatio.types.MLArray;
import com.jmatio.types.MLDouble;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import jp.riken.brain.ni.samuraigraph.base.SGData;
import jp.riken.brain.ni.samuraigraph.base.SGDataBuffer;
import jp.riken.brain.ni.samuraigraph.base.SGDataBufferPolicy;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGDataValueHistory;
import jp.riken.brain.ni.samuraigraph.base.SGDate;
import jp.riken.brain.ni.samuraigraph.base.SGExportParameter;
import jp.riken.brain.ni.samuraigraph.base.SGIDataSource;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyUtility;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import jp.riken.brain.ni.samuraigraph.base.SGTwoDimensionalArrayIndex;
import jp.riken.brain.ni.samuraigraph.base.SGUtility;
import jp.riken.brain.ni.samuraigraph.base.SGValueRange;
import jp.riken.brain.ni.samuraigraph.data.SGMDArrayVariable.MDArrayDataType;
import org.w3c.dom.Element;
import ucar.ma2.Array;
import ucar.ma2.ArrayObject;
import ucar.ma2.DataType;
import ucar.ma2.Index;
import ucar.nc2.Dimension;
import ucar.nc2.write.NetcdfFormatWriter;

/** The class of multiple scalar XY type data for multidimensional data. */
public class SGSXYMDArrayMultipleData extends SGMDArrayData
    implements SGISXYTypeMultipleData, SGISXYMultipleDimensionData {

  private final SGSXYMDArrayMultipleDataExporter mExporter =
      new SGSXYMDArrayMultipleDataExporter(this);

  /** The variables for x-values. */
  protected SGMDArrayVariable[] mXVariables = null;

  /** The variables for y-values. */
  protected SGMDArrayVariable[] mYVariables = null;

  /** The variables for lower error values. */
  protected SGMDArrayVariable[] mLowerErrorVariables = null;

  /** The variables for upper error values. */
  protected SGMDArrayVariable[] mUpperErrorVariables = null;

  /** The variables for values that holds error values. */
  protected SGMDArrayVariable[] mErrorBarHolderVariables = null;

  /** The variables for tick labels. */
  protected SGMDArrayVariable[] mTickLabelVariables = null;

  /** The variables for values that holds tick labels. */
  protected SGMDArrayVariable[] mTickLabelHolderVariables = null;

  /** The information for picked up dimension. */
  protected SGMDArrayPickUpDimensionInfo mPickUpDimensionInfo = null;

  /** The stride of array. */

  /** The number format state. */
  private final SGXYNumberFormat mFormat = new SGXYNumberFormat();

  /** The default constructor. */
  public SGSXYMDArrayMultipleData() {
    super();
  }

  protected SGMDArrayVariable getVariable(final SGMDArrayVariable[] vars) {
    if (vars == null || vars.length == 0) {
      return null;
    } else {
      return vars[0];
    }
  }

  private SGMDArrayVariable[] getVariables(SGMDArrayVariable[] vars) {
    if (vars != null) {
      return vars.clone();
    } else {
      return null;
    }
  }

  public SGMDArrayVariable[] getXVariables() {
    return this.getVariables(this.mXVariables);
  }

  public SGMDArrayVariable[] getYVariables() {
    return this.getVariables(this.mYVariables);
  }

  public SGMDArrayVariable[] getLowerErrorVariables() {
    return this.getVariables(this.mLowerErrorVariables);
  }

  public SGMDArrayVariable[] getUpperErrorVariables() {
    return this.getVariables(this.mUpperErrorVariables);
  }

  public SGMDArrayVariable[] getErrorHolderVariables() {
    return this.getVariables(this.mErrorBarHolderVariables);
  }

  public SGMDArrayVariable[] getTickLabelVariables() {
    return this.getVariables(this.mTickLabelVariables);
  }

  public SGMDArrayVariable[] getTickLabelHolderVariables() {
    return this.getVariables(this.mTickLabelHolderVariables);
  }

  protected SGMDArrayVariable getXVariable() {
    return this.getVariable(this.mXVariables);
  }

  protected SGMDArrayVariable getYVariable() {
    return this.getVariable(this.mYVariables);
  }

  protected SGMDArrayVariable getLowerErrorVariable() {
    return this.getVariable(this.mLowerErrorVariables);
  }

  protected SGMDArrayVariable getUpperErrorVariable() {
    return this.getVariable(this.mUpperErrorVariables);
  }

  protected SGMDArrayVariable getErrorBarHolderVariable() {
    return this.getVariable(this.mErrorBarHolderVariables);
  }

  protected SGMDArrayVariable getTickLabelVariable() {
    return this.getVariable(this.mTickLabelVariables);
  }

  protected SGMDArrayVariable getTickLabelHolderVariable() {
    return this.getVariable(this.mTickLabelHolderVariables);
  }

  protected SGMDArrayVariable[] createVariableArray(final SGMDArrayVariable var) {
    if (var != null) {
      SGMDArrayVariable[] vars = new SGMDArrayVariable[1];
      vars[0] = var;
      return vars;
    } else {
      return new SGMDArrayVariable[0];
    }
  }

  protected boolean isValidVariables(
      final SGMDArrayFile mdFile, final SGMDArrayDataColumnInfo[] cols) {
    if (cols == null) {
      return false;
    }
    SGMDArrayVariable[] vars = mdFile.getVariables();
    for (int ii = 0; ii < cols.length; ii++) {
      if (cols[ii] == null) {
        return false;
      }
      boolean found = false;
      for (SGMDArrayVariable var : vars) {
        if (var.getName().equals(cols[ii].getName())) {
          found = true;
          break;
        }
      }
      if (!found) {
        return false;
      }
    }
    return true;
  }

  private boolean contains(
      final SGMDArrayDataColumnInfo[] infoArray, final SGMDArrayDataColumnInfo info) {
    for (int ii = 0; ii < infoArray.length; ii++) {
      if (info.getName().equals(infoArray[ii].getName())) {
        return true;
      }
    }
    return false;
  }

  protected boolean isValidErrorBarVars(
      final SGMDArrayFile mdFile,
      final SGMDArrayDataColumnInfo[] multipleInfo,
      final SGMDArrayDataColumnInfo[] leInfo,
      final SGMDArrayDataColumnInfo[] ueInfo,
      final SGMDArrayDataColumnInfo[] ehInfo) {

    if (leInfo != null && ueInfo != null) {
      if (ehInfo == null) {
        return false;
      }
      if (!this.isValidVariables(mdFile, leInfo)) {
        return false;
      }
      if (!this.isValidVariables(mdFile, ueInfo)) {
        return false;
      }
      if (leInfo.length != ueInfo.length) {
        return false;
      }
      if (!this.isValidVariables(mdFile, ehInfo)) {
        return false;
      }
      if (ehInfo.length != leInfo.length) {
        return false;
      }
      for (SGMDArrayDataColumnInfo info : ehInfo) {
        if (!this.contains(multipleInfo, info)) {
          return false;
        }
      }
    }
    return true;
  }

  protected boolean isValidTickLabelVars(
      final SGMDArrayFile mdFile,
      final SGMDArrayDataColumnInfo[] multipleInfo,
      final SGMDArrayDataColumnInfo[] tlInfo,
      final SGMDArrayDataColumnInfo[] thInfo) {
    if (tlInfo != null) {
      if (thInfo == null) {
        return false;
      }
      if (!this.isValidVariables(mdFile, tlInfo)) {
        return false;
      }
      if (!this.isValidVariables(mdFile, thInfo)) {
        return false;
      }
      if (thInfo.length != tlInfo.length) {
        return false;
      }
      for (SGMDArrayDataColumnInfo info : thInfo) {
        if (!this.contains(multipleInfo, info)) {
          return false;
        }
      }
    }
    return true;
  }

  /**
   * Builds a data object with given data file.
   *
   * @param mdFile multidimensional data file
   * @param obs data observer
   * @param xInfo the x variables
   * @param yInfo the y variables
   * @param leInfo the lower error variables
   * @param ueInfo the upper error variables
   * @param ehInfo the error bar holder variables
   * @param tlInfo the tick label variables
   * @param thInfo the tick label holder variables
   * @param stride stride of an array
   * @param tickLabelStride stride for tick label
   * @param strideAvailable flag whether to set available the stride
   */
  public SGSXYMDArrayMultipleData(
      final SGMDArrayFile mdFile,
      final SGDataSourceObserver obs,
      final SGMDArrayDataColumnInfo[] xInfo,
      final SGMDArrayDataColumnInfo[] yInfo,
      final SGMDArrayDataColumnInfo[] leInfo,
      final SGMDArrayDataColumnInfo[] ueInfo,
      final SGMDArrayDataColumnInfo[] ehInfo,
      final SGMDArrayDataColumnInfo[] tlInfo,
      final SGMDArrayDataColumnInfo[] thInfo,
      final SGIntegerSeriesSet stride,
      final SGIntegerSeriesSet tickLabelStride,
      final boolean strideAvailable) {

    super(mdFile, obs, strideAvailable);

    if (xInfo == null || yInfo == null) {
      throw new IllegalArgumentException("xInfo == null || yInfo == null");
    }
    if (xInfo.length == 0 && yInfo.length == 0) {
      throw new IllegalArgumentException(
          "The number of variables for x-values and that of y-values are both equal to zero.");
    }
    if (xInfo.length > 1 && yInfo.length > 1) {
      throw new IllegalArgumentException(
          "Invalid size of x-values and y-values: " + xInfo.length + ", " + yInfo.length);
    }
    if (!this.isValidVariables(mdFile, xInfo)) {
      throw new IllegalArgumentException("Variables for x values are invalid.");
    }
    if (!this.isValidVariables(mdFile, yInfo)) {
      throw new IllegalArgumentException("Variables for y values are invalid.");
    }

    // get variables
    SGMDArrayVariable[] xVars = new SGMDArrayVariable[xInfo.length];
    for (int ii = 0; ii < xVars.length; ii++) {
      SGMDArrayVariable var = this.initVariable(mdFile, xInfo[ii]);
      if (var == null) {
        throw new IllegalArgumentException(
            "The name for x-values is not found: " + xInfo[ii].getName());
      }
      xVars[ii] = var;
    }
    SGMDArrayVariable[] yVars = new SGMDArrayVariable[yInfo.length];
    for (int ii = 0; ii < yVars.length; ii++) {
      SGMDArrayVariable var = this.initVariable(mdFile, yInfo[ii]);
      if (var == null) {
        throw new IllegalArgumentException(
            "The name for y-values is not found: " + yInfo[ii].getName());
      }
      yVars[ii] = var;
    }

    // check the input for error bars
    final boolean multipleY;
    if (xInfo.length < yInfo.length) {
      multipleY = true;
    } else if (xInfo.length == 1 && yInfo.length == 1) {
      if ((ehInfo != null && this.contains(ehInfo, yInfo[0]))
          || (thInfo != null && this.contains(thInfo, yInfo[0]))) {
        multipleY = true;
      } else {
        multipleY = false;
      }
    } else {
      multipleY = false;
    }
    SGMDArrayDataColumnInfo[] multipleVars = multipleY ? yInfo : xInfo;
    if (!this.isValidErrorBarVars(mdFile, multipleVars, leInfo, ueInfo, ehInfo)) {
      throw new IllegalArgumentException("Column indices for the error bars are invalid.");
    }
    if (!this.isValidTickLabelVars(mdFile, multipleVars, tlInfo, thInfo)) {
      throw new IllegalArgumentException("Column indices for the tick labels are invalid.");
    }

    // find variables
    SGMDArrayVariable[] leVars = null;
    SGMDArrayVariable[] ueVars = null;
    SGMDArrayVariable[] ehVars = null;
    if (leInfo != null && leInfo.length != 0 && ueInfo != null && ehInfo != null) {
      leVars = new SGMDArrayVariable[leInfo.length];
      for (int ii = 0; ii < leInfo.length; ii++) {
        SGMDArrayVariable var = this.initVariable(mdFile, leInfo[ii]);
        if (var == null) {
          throw new IllegalArgumentException(
              "A variable for lower error values is not found: " + leInfo[ii].getName());
        }
        leVars[ii] = var;
      }
      ueVars = new SGMDArrayVariable[ueInfo.length];
      for (int ii = 0; ii < ueInfo.length; ii++) {
        SGMDArrayVariable var = this.initVariable(mdFile, ueInfo[ii]);
        if (var == null) {
          throw new IllegalArgumentException(
              "A variable for upper error values is not found: " + ueInfo[ii].getName());
        }
        ueVars[ii] = var;
      }
      ehVars = new SGMDArrayVariable[ehInfo.length];
      for (int ii = 0; ii < ehInfo.length; ii++) {
        String name = ehInfo[ii].getName();
        SGMDArrayVariable var = null;
        SGMDArrayVariable[] vars;
        if (multipleY) {
          vars = yVars;
        } else {
          vars = xVars;
        }
        for (int jj = 0; jj < vars.length; jj++) {
          if (name.equals(vars[jj].getName())) {
            var = vars[jj];
            break;
          }
        }
        if (var == null) {
          throw new IllegalArgumentException(
              "A variable for error value holder is not found: " + name);
        }
        ehVars[ii] = var;
      }
    }
    SGMDArrayVariable[] tlVars = null;
    SGMDArrayVariable[] thVars = null;
    if (tlInfo != null && tlInfo.length != 0 && thInfo != null) {
      tlVars = new SGMDArrayVariable[tlInfo.length];
      for (int ii = 0; ii < tlInfo.length; ii++) {
        SGMDArrayVariable var = this.initVariable(mdFile, tlInfo[ii]);
        if (var == null) {
          throw new IllegalArgumentException(
              "A variable for tick label is not found: " + tlInfo[ii].getName());
        }
        tlVars[ii] = var;
      }
      thVars = new SGMDArrayVariable[thInfo.length];
      for (int ii = 0; ii < thInfo.length; ii++) {
        String name = thInfo[ii].getName();
        SGMDArrayVariable var = null;
        SGMDArrayVariable[] vars;
        if (multipleY) {
          vars = yVars;
        } else {
          vars = xVars;
        }
        for (int jj = 0; jj < vars.length; jj++) {
          if (name.equals(vars[jj].getName())) {
            var = vars[jj];
            break;
          }
        }
        if (var == null) {
          throw new IllegalArgumentException(
              "A variable for tick label holder is not found: " + name);
        }
        thVars[ii] = var;
      }
    }

    // set to the attributes
    this.mXVariables = xVars;
    this.mYVariables = yVars;
    this.mLowerErrorVariables = leVars;
    this.mUpperErrorVariables = ueVars;
    this.mErrorBarHolderVariables = ehVars;
    this.mTickLabelVariables = tlVars;
    this.mTickLabelHolderVariables = thVars;
    final int len = this.getAllPointsNumber();
    this.mStride = this.createStride(stride, len);
    this.mTickLabelStride = this.createStride(tickLabelStride, len);
    this.initTimeStride();
  }

  /**
   * Builds a data object with given multidimensional data file.
   *
   * @param mdFile multidimensional data file
   * @param obs netCDF data observer
   * @param xInfo information of x values
   * @param yInfo information of y values
   * @param leInfo information of lower error values
   * @param ueInfo information of lower upper values
   * @param tlInfo information of tick labels
   * @param indices indices for given dimension
   * @param stride stride of an array
   * @param tickLabelStride stride for tick label
   * @param strideAvailable flag whether to set available the stride
   */
  public SGSXYMDArrayMultipleData(
      final SGMDArrayFile mdFile,
      final SGDataSourceObserver obs,
      final SGMDArrayDataColumnInfo xInfo,
      final SGMDArrayDataColumnInfo yInfo,
      final SGMDArrayDataColumnInfo leInfo,
      final SGMDArrayDataColumnInfo ueInfo,
      final SGMDArrayDataColumnInfo tlInfo,
      final SGIntegerSeriesSet indices,
      final SGIntegerSeriesSet stride,
      final SGIntegerSeriesSet tickLabelStride,
      final boolean strideAvailable) {

    super(mdFile, obs, strideAvailable);

    if (xInfo == null && yInfo == null) {
      throw new IllegalArgumentException("xInfo == null && yInfo == null");
    }
    if (!(leInfo == null && ueInfo == null) && !(leInfo != null && ueInfo != null)) {
      throw new IllegalArgumentException("leInfo = " + leInfo + ", ueInfo = " + ueInfo);
    }
    if (indices == null) {
      throw new IllegalArgumentException("indices == null");
    }

    // get variables
    SGMDArrayVariable xVar = this.initVariable(mdFile, xInfo);
    SGMDArrayVariable yVar = this.initVariable(mdFile, yInfo);
    SGMDArrayVariable leVar = null;
    SGMDArrayVariable ueVar = null;
    SGMDArrayVariable ehVar = null;
    if (leInfo != null && ueInfo != null) {
      leVar = this.initVariable(mdFile, leInfo);
      ueVar = this.initVariable(mdFile, ueInfo);
      String hName = SGDataColumnInfoUtility.getHolderName(leInfo);
      ehVar = this.getHolderVariable(hName, xVar, yVar);
    }
    SGMDArrayVariable tlVar = null;
    SGMDArrayVariable thVar = null;
    if (tlInfo != null) {
      tlVar = this.initVariable(mdFile, tlInfo);
      String hName = SGDataColumnInfoUtility.getHolderName(tlInfo);
      thVar = this.getHolderVariable(hName, xVar, yVar);
    }

    final Integer xPickUpDim =
        (xInfo != null) ? xInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION) : null;
    final Integer yPickUpDim =
        (yInfo != null) ? yInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION) : null;
    final boolean xValid = SGDataColumnInfoUtility.isValidPickUpValue(xPickUpDim);
    final boolean yValid = SGDataColumnInfoUtility.isValidPickUpValue(yPickUpDim);
    if (!xValid && !yValid) {
      throw new IllegalArgumentException("Both of x and y indices are invalid.");
    }
    Map<String, Integer> pickUpDimMap = new HashMap<String, Integer>();
    if (xValid && xInfo != null) {
      pickUpDimMap.put(xInfo.getName(), xPickUpDim);
    }
    if (yValid && yInfo != null) {
      pickUpDimMap.put(yInfo.getName(), yPickUpDim);
    }

    // set to attributes
    this.mXVariables = this.createVariableArray(xVar);
    this.mYVariables = this.createVariableArray(yVar);
    this.mLowerErrorVariables = this.createVariableArray(leVar);
    this.mUpperErrorVariables = this.createVariableArray(ueVar);
    this.mErrorBarHolderVariables = this.createVariableArray(ehVar);
    this.mTickLabelVariables = this.createVariableArray(tlVar);
    this.mTickLabelHolderVariables = this.createVariableArray(thVar);
    this.mPickUpDimensionInfo =
        new SGMDArrayPickUpDimensionInfo(pickUpDimMap, (SGIntegerSeriesSet) indices.clone());
    this.updateDimensionIndices();
    final int len = this.getAllPointsNumber();
    this.mStride = this.createStride(stride, len);
    this.mTickLabelStride = this.createStride(tickLabelStride, len);
    this.initTimeStride();
  }

  private SGMDArrayVariable getHolderVariable(
      String hName, SGMDArrayVariable xVar, SGMDArrayVariable yVar) {
    SGMDArrayVariable hVar = null;
    if (xVar == null) {
      hVar = yVar;
    } else {
      if (yVar == null) {
        hVar = xVar;
      } else {
        hVar = hName.equals(xVar.getName()) ? xVar : yVar;
      }
    }
    return hVar;
  }

  protected SGMDArrayVariable initVariable(SGMDArrayFile file, SGMDArrayDataColumnInfo info) {
    SGMDArrayVariable var = super.initVariable(file, info);
    if (info != null) {
      Integer pickUpDimension = info.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
      Integer value = null;
      if (pickUpDimension != null) {
        value = pickUpDimension;
      } else {
        value = -1;
      }
      var.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, value);
    }
    return var;
  }

  protected SGMDArrayVariable initVariable(SGMDArrayVariable var) {
    super.initVariable(var);
    var.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, -1);
    return var;
  }

  // Updates dimension indices.
  protected void updateDimensionIndices() {
    if (this.mPickUpDimensionInfo != null) {
      Map<String, Integer> dimensionMap = this.mPickUpDimensionInfo.getDimensionMap();
      Iterator<Entry<String, Integer>> itr = dimensionMap.entrySet().iterator();
      while (itr.hasNext()) {
        Entry<String, Integer> entry = itr.next();
        String name = entry.getKey();
        Integer dimension = entry.getValue();
        SGMDArrayVariable var = this.findVariable(name);
        var.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, dimension);
      }
    } else {
      SGMDArrayVariable[] vars = this.getVariables();
      for (SGMDArrayVariable var : vars) {
        var.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, -1);
      }
    }
  }

  /** Returns whether a dimension is picked up. */
  public boolean isDimensionPicked() {
    return (this.mPickUpDimensionInfo != null);
  }

  public List<SGMDArrayVariable> getPickUpMDArrayVariables() {
    if (this.mPickUpDimensionInfo != null) {
      List<SGMDArrayVariable> varList = new ArrayList<SGMDArrayVariable>();
      Map<String, Integer> dimensionMap = this.mPickUpDimensionInfo.getDimensionMap();
      Iterator<Entry<String, Integer>> itr = dimensionMap.entrySet().iterator();
      while (itr.hasNext()) {
        Entry<String, Integer> entry = itr.next();
        String name = entry.getKey();
        Integer dim = entry.getValue();
        if (dim != -1) {
          SGMDArrayVariable var = this.findVariable(name);
          varList.add(var);
        }
      }
      return varList;
    } else {
      return null;
    }
  }

  private int[] getPickUpDimensionIndices() {
    if (this.mPickUpDimensionInfo != null) {
      return this.mPickUpDimensionInfo.getIndices().getNumbers();
    } else {
      return null;
    }
  }

  /**
   * Disposes of this data object. Do not call any method of this object after called this method.
   */
  public void dispose() {
    super.dispose();
    this.mStride = null;
    this.mTickLabelStride = null;
    this.mFormat.dispose();
  }

  /**
   * Sets the decimal places for the tick labels.
   *
   * @param dp a value to set to the decimal places
   */
  @Override
  public void setDecimalPlaces(int dp) {
    this.mFormat.setDecimalPlaces(dp);
  }

  /**
   * Sets the exponent for the tick labels.
   *
   * @param exp a value to set to the exponent
   */
  @Override
  public void setExponent(int exp) {
    this.mFormat.setExponent(exp);
  }

  /** Returns the decimal places for the tick labels. */
  @Override
  public int getDecimalPlaces() {
    return this.mFormat.getDecimalPlaces();
  }

  /** Returns the exponent for tick labels. */
  @Override
  public int getExponent() {
    return this.mFormat.getExponent();
  }

  /** Returns the bounds of x-values. */
  @Override
  public SGValueRange getBoundsX() {
    return SGDataRangeUtility.getBoundsX(this);
  }

  /** Returns the bounds of y-values. */
  @Override
  public SGValueRange getBoundsY() {
    return SGDataRangeUtility.getBoundsY(this);
  }

  public boolean useCache(final boolean all) {
    if (!all) {
      return true;
    }
    if (!this.isStrideAvailable()) {
      return true;
    }
    return this.mStride.isComplete();
  }

  /** Returns a variable for multiple values. */
  public SGMDArrayVariable[] getMultipleVariables() {
    if (this.isDimensionPicked()) {
      return null;
    } else {
      if (this.hasMultipleYValues()) {
        return this.mYVariables;
      } else {
        return this.mXVariables;
      }
    }
  }

  /** Returns a variable for "not" multiple values. */
  public SGMDArrayVariable getSingleVariable() {
    if (this.isDimensionPicked()) {
      if (this.hasMultipleYValues()) {
        return this.getXVariable();
      } else {
        return this.getYVariable();
      }
    } else {
      if (this.hasMultipleYValues()) {
        if (this.mXVariables != null && this.mXVariables.length == 1) {
          return this.mXVariables[0];
        } else {
          return null;
        }
      } else {
        if (this.mYVariables != null && this.mYVariables.length == 1) {
          return this.mYVariables[0];
        } else {
          return null;
        }
      }
    }
  }

  /**
   * Returns an array of values of multiple variables.
   *
   * @param stride the stride to use
   */
  protected double[][] getMultipleVariableValueArray(SGIntegerSeriesSet stride) {
    SGMDArrayVariable[] vars = this.getMultipleVariables();
    double[][] array = new double[vars.length][];
    for (int ii = 0; ii < array.length; ii++) {
      array[ii] = vars[ii].getGenericNumberArray(stride);
    }
    return array;
  }

  /**
   * Returns an array of values of picked up variables.
   *
   * @param pickUpAll the pickUpAll parameter
   * @param stride the stride to use
   * @param pickUpVar picked up variable
   */
  protected double[][] getPickUpValueArray(
      SGIntegerSeriesSet stride, SGMDArrayVariable pickUpVar, final boolean pickUpAll) {
    SGIntegerSeriesSet pickUpStride = pickUpAll ? null : this.mPickUpDimensionInfo.getIndices();
    int[] pickUpIndices = null;
    if (pickUpStride != null) {
      pickUpIndices = pickUpStride.getNumbers();
    } else {
      final int pickUpLen = pickUpVar.getDimensionLength(KEY_SXY_PICKUP_DIMENSION);
      pickUpIndices = new int[pickUpLen];
      for (int ii = 0; ii < pickUpLen; ii++) {
        pickUpIndices[ii] = ii;
      }
    }
    final int len = (stride != null) ? stride.getLength() : this.getAllPointsNumber();
    int[] indices = null;
    if (stride != null) {
      indices = stride.getNumbers();
    } else {
      indices = new int[len];
      for (int ii = 0; ii < len; ii++) {
        indices[ii] = ii;
      }
    }
    final int dimensionIndex = pickUpVar.getGenericDimensionIndex();
    final int pickUpDimensionIndex = pickUpVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
    double[][] array = new double[pickUpIndices.length][];
    for (int ii = 0; ii < array.length; ii++) {
      int[] origins = pickUpVar.getOrigins();
      origins[pickUpDimensionIndex] = pickUpIndices[ii];
      array[ii] = new double[len];
      for (int jj = 0; jj < len; jj++) {
        final int index = indices[jj];
        array[ii][jj] = pickUpVar.getDoubleValue(dimensionIndex, index, origins);
      }
    }
    return array;
  }

  /** Returns whether this multiple data has multiple arrays for y-values. */
  public boolean hasMultipleYValues() {
    if (this.isDimensionPicked()) {
      SGMDArrayVariable yVar = this.getYVariable();
      if (yVar != null) {
        Integer index = yVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        return (index != null && index != -1);
      } else {
        return false;
      }
    } else {
      return (this.mYVariables.length >= this.mXVariables.length);
    }
  }

  protected boolean hasEqualLowerUpperErrorVariable() {
    if (!this.isErrorBarAvailable()) {
      return false;
    }
    return this.mLowerErrorVariables[0].equals(this.mUpperErrorVariables[0]);
  }

  protected int find(SGMDArrayVariable var, SGMDArrayVariable[] vars) {
    if (vars == null) {
      return -1;
    }
    for (int ii = 0; ii < vars.length; ii++) {
      if (vars[ii].equals(var)) {
        return ii;
      }
    }
    return -1;
  }

  /** The class of properties for multiple multidimensional data. */
  public static class SXYMDArrayMultipleDataProperties extends MDArrayDataProperties {

    protected String[] xNames = null;

    protected String[] yNames = null;

    protected String[] lNames = null;

    protected String[] uNames = null;

    protected String[] ehNames = null;

    protected String[] tNames = null;

    protected String[] thNames = null;

    protected SGMDArrayPickUpDimensionInfo mPickUpInfo = null;

    SGIntegerSeriesSet mStride = null;

    SGIntegerSeriesSet mTickLabelStride = null;

    /** The default constructor. */
    public SXYMDArrayMultipleDataProperties() {
      super();
    }

    /** Dispose this object. */
    @Override
    public void dispose() {
      super.dispose();
      this.xNames = null;
      this.yNames = null;
      this.lNames = null;
      this.uNames = null;
      this.ehNames = null;
      this.tNames = null;
      this.thNames = null;
      this.mPickUpInfo = null;
      this.mStride = null;
      this.mTickLabelStride = null;
    }

    @Override
    public boolean equals(Object obj) {
      if ((obj instanceof SXYMDArrayMultipleDataProperties) == false) {
        return false;
      }
      if (super.equals(obj) == false) {
        return false;
      }
      SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) obj;
      return true;
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), this.mDecimalPlaces, this.mExponent);
    }

    @Override
    public boolean hasEqualSize(DataProperties dp) {
      if ((dp instanceof SXYMDArrayMultipleDataProperties) == false) {
        return false;
      }
      if (!super.hasEqualSize(dp)) {
        return false;
      }
      SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) dp;
      if (!SGUtility.equals(this.mPickUpInfo, p.mPickUpInfo)) {
        return false;
      }
      if (!SGUtility.equals(this.mStride, p.mStride)) {
        return false;
      }
      if (!SGUtility.equals(this.mTickLabelStride, p.mTickLabelStride)) {
        return false;
      }
      return true;
    }

    /**
     * Returns whether this data property has the equal column types with given data property.
     *
     * @param dp a data property
     */
    @Override
    public boolean hasEqualColumnTypes(DataProperties dp) {
      if ((dp instanceof SXYMDArrayMultipleDataProperties) == false) {
        return false;
      }
      SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) dp;
      if (SGUtility.equals(this.xNames, p.xNames) == false) {
        return false;
      }
      if (SGUtility.equals(this.yNames, p.yNames) == false) {
        return false;
      }
      if (SGUtility.equals(this.lNames, p.lNames) == false) {
        return false;
      }
      if (SGUtility.equals(this.uNames, p.uNames) == false) {
        return false;
      }
      if (SGUtility.equals(this.ehNames, p.ehNames) == false) {
        return false;
      }
      if (SGUtility.equals(this.tNames, p.tNames) == false) {
        return false;
      }
      if (SGUtility.equals(this.thNames, p.thNames) == false) {
        return false;
      }
      return true;
    }

    /** Copy this object. */
    public Object copy() {
      SXYMDArrayMultipleDataProperties p = (SXYMDArrayMultipleDataProperties) super.copy();
      p.mStride = (this.mStride != null) ? (SGIntegerSeriesSet) this.mStride.clone() : null;
      p.mTickLabelStride =
          (this.mTickLabelStride != null)
              ? (SGIntegerSeriesSet) this.mTickLabelStride.clone()
              : null;
      return p;
    }
  }

  /** Returns the properties of this data. */
  @Override
  public SGProperties getProperties() {
    SXYMDArrayMultipleDataProperties p = new SXYMDArrayMultipleDataProperties();
    if (!this.getProperties(p)) {
      return null;
    }
    return p;
  }

  /**
   * Returns the properties of this data.
   *
   * @param p the properties of this data
   */
  @Override
  public boolean getProperties(SGProperties p) {
    if (!(p instanceof SXYMDArrayMultipleDataProperties)) {
      return false;
    }
    if (super.getProperties(p) == false) {
      return false;
    }
    SXYMDArrayMultipleDataProperties sp = (SXYMDArrayMultipleDataProperties) p;

    String[] xNames = null;
    if (this.mXVariables != null) {
      xNames = new String[this.mXVariables.length];
      for (int ii = 0; ii < xNames.length; ii++) {
        xNames[ii] = this.mXVariables[ii].getName();
      }
    }
    String[] yNames = null;
    if (this.mYVariables != null) {
      yNames = new String[this.mYVariables.length];
      for (int ii = 0; ii < yNames.length; ii++) {
        yNames[ii] = this.mYVariables[ii].getName();
      }
    }
    String[] lNames = null;
    String[] uNames = null;
    String[] ehNames = null;
    if (this.isErrorBarAvailable()) {
      lNames = new String[this.mLowerErrorVariables.length];
      for (int ii = 0; ii < lNames.length; ii++) {
        lNames[ii] = this.mLowerErrorVariables[ii].getName();
      }
      uNames = new String[this.mUpperErrorVariables.length];
      for (int ii = 0; ii < uNames.length; ii++) {
        uNames[ii] = this.mUpperErrorVariables[ii].getName();
      }
      if (this.isDimensionPicked()) {
        lNames = new String[] {this.getName(this.getLowerErrorVariable())};
        uNames = new String[] {this.getName(this.getUpperErrorVariable())};
        ehNames = new String[] {this.getName(this.getErrorBarHolderVariable())};
      } else {
        ehNames = new String[this.mErrorBarHolderVariables.length];
        for (int ii = 0; ii < ehNames.length; ii++) {
          ehNames[ii] = this.mErrorBarHolderVariables[ii].getName();
        }
      }
    }
    String[] tNames = null;
    String[] thNames = null;
    if (this.isTickLabelAvailable()) {
      tNames = new String[this.mTickLabelVariables.length];
      for (int ii = 0; ii < tNames.length; ii++) {
        tNames[ii] = this.mTickLabelVariables[ii].getName();
      }
      if (this.isDimensionPicked()) {
        tNames = new String[] {this.getName(this.getTickLabelVariable())};
        thNames = new String[] {this.getName(this.getTickLabelHolderVariable())};
      } else {
        thNames = new String[this.mTickLabelHolderVariables.length];
        for (int ii = 0; ii < thNames.length; ii++) {
          thNames[ii] = this.mTickLabelHolderVariables[ii].getName();
        }
      }
    }
    sp.xNames = xNames;
    sp.yNames = yNames;
    sp.lNames = lNames;
    sp.uNames = uNames;
    sp.ehNames = ehNames;
    sp.tNames = tNames;
    sp.thNames = thNames;
    sp.mDecimalPlaces = this.getDecimalPlaces();
    sp.mExponent = this.getExponent();
    sp.mPickUpInfo = (SGMDArrayPickUpDimensionInfo) this.getPickUpDimensionInfo();
    sp.mStride = this.getStride();
    sp.mTickLabelStride = this.getTickLabelStride();
    return true;
  }

  /**
   * Sets the properties to this data.
   *
   * @param p properties to set
   */
  @Override
  public boolean setProperties(SGProperties p) {
    if (!(p instanceof SXYMDArrayMultipleDataProperties)) {
      return false;
    }
    if (super.setProperties(p) == false) {
      return false;
    }
    SXYMDArrayMultipleDataProperties sp = (SXYMDArrayMultipleDataProperties) p;

    SGMDArrayVariable[] xVars = null;
    if (sp.xNames != null) {
      xVars = new SGMDArrayVariable[sp.xNames.length];
      for (int ii = 0; ii < xVars.length; ii++) {
        xVars[ii] = this.findVariable(sp.xNames[ii]);
      }
    }
    SGMDArrayVariable[] yVars = null;
    if (sp.yNames != null) {
      yVars = new SGMDArrayVariable[sp.yNames.length];
      for (int ii = 0; ii < yVars.length; ii++) {
        yVars[ii] = this.findVariable(sp.yNames[ii]);
      }
    }
    SGMDArrayVariable[] lVars = null;
    if (sp.lNames != null) {
      lVars = new SGMDArrayVariable[sp.lNames.length];
      for (int ii = 0; ii < lVars.length; ii++) {
        lVars[ii] = this.findVariable(sp.lNames[ii]);
      }
    }
    SGMDArrayVariable[] uVars = null;
    if (sp.uNames != null) {
      uVars = new SGMDArrayVariable[sp.uNames.length];
      for (int ii = 0; ii < uVars.length; ii++) {
        uVars[ii] = this.findVariable(sp.uNames[ii]);
      }
    }
    SGMDArrayVariable[] ehVars = null;
    if (sp.ehNames != null) {
      ehVars = new SGMDArrayVariable[sp.ehNames.length];
      for (int ii = 0; ii < ehVars.length; ii++) {
        ehVars[ii] = this.findVariable(sp.ehNames[ii]);
      }
    }
    SGMDArrayVariable[] tVars = null;
    if (sp.tNames != null) {
      tVars = new SGMDArrayVariable[sp.tNames.length];
      for (int ii = 0; ii < tVars.length; ii++) {
        tVars[ii] = this.findVariable(sp.tNames[ii]);
      }
    }
    SGMDArrayVariable[] thVars = null;
    if (sp.thNames != null) {
      thVars = new SGMDArrayVariable[sp.thNames.length];
      for (int ii = 0; ii < thVars.length; ii++) {
        thVars[ii] = this.findVariable(sp.thNames[ii]);
      }
    }

    // set to the attributes
    this.mXVariables = xVars;
    this.mYVariables = yVars;
    this.mLowerErrorVariables = lVars;
    this.mUpperErrorVariables = uVars;
    this.mErrorBarHolderVariables = ehVars;
    this.mTickLabelVariables = tVars;
    this.mTickLabelHolderVariables = thVars;

    this.setDecimalPlaces(sp.mDecimalPlaces);
    this.setExponent(sp.mExponent);
    this.mPickUpDimensionInfo =
        (sp.mPickUpInfo != null) ? (SGMDArrayPickUpDimensionInfo) sp.mPickUpInfo.clone() : null;
    this.setStride(sp.mStride);
    this.setTickLabelStride(sp.mTickLabelStride);

    this.updateDimensionIndices();

    return true;
  }

  /** Returns the index of picked up dimension. */
  public Integer getPickUpDimension(SGMDArrayVariable var) {
    if (this.isDimensionPicked()) {
      return this.mPickUpDimensionInfo.getDimension(var.getName());
    } else {
      return null;
    }
  }

  public SGIntegerSeriesSet getIndices() {
    if (this.isDimensionPicked()) {
      return this.mPickUpDimensionInfo.getIndices();
    } else {
      return null;
    }
  }

  @Override
  public SGPickUpDimensionInfo getPickUpDimensionInfo() {
    if (this.isDimensionPicked()) {
      return (SGPickUpDimensionInfo) this.mPickUpDimensionInfo.clone();
    } else {
      return null;
    }
  }

  @Override
  public boolean setPickUpDimensionInfo(SGPickUpDimensionInfo info) {
    if (info != null) {
      if (!(info instanceof SGMDArrayPickUpDimensionInfo)) {
        throw new IllegalArgumentException("Invalid input: " + info);
      }
      this.mPickUpDimensionInfo = (SGMDArrayPickUpDimensionInfo) info.clone();
    } else {
      this.mPickUpDimensionInfo = null;
    }
    this.updateDimensionIndices();

    return true;
  }

  /** Returns whether error bars are available. */
  @Override
  public boolean isErrorBarAvailable() {
    return (this.getLowerErrorVariable() != null && this.getUpperErrorVariable() != null);
  }

  /** Returns whether tick labels are available. */
  @Override
  public boolean isTickLabelAvailable() {
    return (this.getTickLabelVariable() != null);
  }

  /**
   * Returns whether the error bars are vertical. If this data does not have error values, returns
   * null.
   *
   * <p>not have error values
   */
  @Override
  public Boolean isErrorBarVertical() {
    if (this.isErrorBarAvailable()) {
      return SGUtility.contains(this.mErrorBarHolderVariables, this.mYVariables);
    } else {
      return null;
    }
  }

  /**
   * Returns whether the tick labels align horizontally. If this data does not have tick labels,
   * returns null.
   *
   * <p>do not have tick labels
   */
  @Override
  public Boolean isTickLabelHorizontal() {
    if (this.isTickLabelAvailable()) {
      return SGUtility.contains(this.mTickLabelHolderVariables, this.mYVariables);
    } else {
      return null;
    }
  }

  /** Returns a text string of data type. */
  @Override
  public String getDataType() {
    SGIDataSource src = this.getDataSource();
    if (src instanceof SGHDF5File) {
      return SGDataTypeConstants.SXY_MULTIPLE_HDF5_DATA;
    } else if (src instanceof SGMATLABFile) {
      return SGDataTypeConstants.SXY_MULTIPLE_MATLAB_DATA;
    } else if (src instanceof SGVirtualMDArrayFile) {
      return SGDataTypeConstants.SXY_MULTIPLE_VIRTUAL_MDARRAY_DATA;
    } else {
      throw new Error("This cannot happen.");
    }
  }

  /** Returns the title for the X-axis. */
  @Override
  public String getTitleX() {
    if (this.isDimensionPicked()) {
      SGMDArrayVariable xVar = this.getXVariable();
      if (xVar != null) {
        return xVar.getSimpleName();
      } else {
        return "";
      }
    } else {
      if (this.mXVariables != null && this.mXVariables.length == 1) {
        return this.mXVariables[0].getSimpleName();
      } else {
        return "";
      }
    }
  }

  /** Returns the title for the Y-axis. */
  @Override
  public String getTitleY() {
    if (this.isDimensionPicked()) {
      SGMDArrayVariable yVar = this.getYVariable();
      if (yVar != null) {
        return yVar.getSimpleName();
      } else {
        return "";
      }
    } else {
      if (this.mYVariables != null && this.mYVariables.length == 1) {
        return this.mYVariables[0].getSimpleName();
      } else {
        return "";
      }
    }
  }

  /** Returns an array of SXYData objects. */
  @Override
  public SGISXYTypeSingleData[] getSXYDataArray() {
    SGSXYMultipleDataCache cache = (SGSXYMultipleDataCache) this.getCache();
    SGSXYDataCache[] sxyCacheArray = null;
    if (cache != null) {
      sxyCacheArray = cache.mCacheArray;
    }
    if (this.isDimensionPicked()) {
      final SGMDArrayVariable xVar = this.getXVariable();
      final SGMDArrayVariable yVar = this.getYVariable();
      final SGMDArrayVariable leVar = this.getLowerErrorVariable();
      final SGMDArrayVariable ueVar = this.getUpperErrorVariable();
      final SGMDArrayVariable ehVar = this.getErrorBarHolderVariable();
      final SGMDArrayVariable tlVar = this.getTickLabelVariable();
      final SGMDArrayVariable thVar = this.getTickLabelHolderVariable();
      SGMDArrayDataColumnInfo xInfo = SGDataFileUtility.createDataColumnInfo(xVar, X_VALUE);
      SGMDArrayDataColumnInfo yInfo = SGDataFileUtility.createDataColumnInfo(yVar, Y_VALUE);
      SGMDArrayDataColumnInfo leInfo = null;
      SGMDArrayDataColumnInfo ueInfo = null;
      SGMDArrayDataColumnInfo ehInfo = null;
      if (leVar != null && ueVar != null && ehVar != null) {
        leInfo = SGDataFileUtility.createDataColumnInfo(leVar, LOWER_ERROR_VALUE);
        ueInfo = SGDataFileUtility.createDataColumnInfo(ueVar, UPPER_ERROR_VALUE);
        ehInfo = (SGMDArrayDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
      }
      SGMDArrayDataColumnInfo tlInfo = null;
      SGMDArrayDataColumnInfo thInfo = null;
      if (tlVar != null) {
        tlInfo = SGDataFileUtility.createDataColumnInfo(tlVar, TICK_LABEL);
        thInfo = (SGMDArrayDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
      }
      SGMDArrayVariable[] vars = this.getVariables();
      int[] pickUpIndices = this.getPickUpDimensionIndices();
      List<SGMDArrayVariable> pickUpVars = this.getPickUpMDArrayVariables();
      final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[pickUpIndices.length];
      for (int ii = 0; ii < dataArray.length; ii++) {
        SGSXYMDArrayData data =
            new SGSXYMDArrayData(
                this.getMDArrayFile(),
                this.getDataSourceObserver(),
                xInfo,
                yInfo,
                leInfo,
                ueInfo,
                ehInfo,
                tlInfo,
                thInfo,
                this.mStride,
                this.mTickLabelStride,
                this.isStrideAvailable());
        for (SGMDArrayVariable var : vars) {
          final int[] origins = var.getOrigins();
          final String varName = var.getName();
          if (pickUpVars.contains(var)) {
            Integer pickUpDim = var.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
            origins[pickUpDim] = pickUpIndices[ii];
          }
          if (leVar != null && ueVar != null && leInfo != null && ueInfo != null) {
            if (varName.equals(leVar.getName())) {
              final Integer index = leInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
              if (index != null && index != -1) {
                origins[index] = pickUpIndices[ii];
              }
            } else if (varName.equals(ueVar.getName())) {
              final Integer index = ueInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
              if (index != null && index != -1) {
                origins[index] = pickUpIndices[ii];
              }
            }
          }
          if (tlVar != null && tlInfo != null) {
            if (varName.equals(tlVar.getName())) {
              final Integer index = tlInfo.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
              if (index != null && index != -1) {
                origins[index] = pickUpIndices[ii];
              }
            }
          }
          data.setOrigin(varName, origins);
        }
        data.setDecimalPlaces(this.getDecimalPlaces());
        data.setExponent(this.getExponent());
        data.setTimeStride(this.mTimeStride);
        data.setShift(this.getShift());

        // sets the cache
        if (sxyCacheArray != null) {
          data.setCache(sxyCacheArray[ii]);
        }

        dataArray[ii] = (SGISXYTypeSingleData) data;
      }

      SGDataViewerUtility.syncDataValueHistory(this.mEditedDataValueList, dataArray);

      return dataArray;

    } else {
      SGMDArrayVariable varSingle = this.getSingleVariable();
      SGMDArrayVariable[] varMultiple = this.getMultipleVariables();
      final int len = varMultiple.length;
      final boolean by = this.hasMultipleYValues();
      final boolean be = this.isErrorBarAvailable();
      final boolean bt = this.isTickLabelAvailable();
      final SGISXYTypeSingleData[] dataArray = new SGISXYTypeSingleData[len];
      for (int ii = 0; ii < dataArray.length; ii++) {
        SGMDArrayVariable xVar = by ? varSingle : varMultiple[ii];
        SGMDArrayVariable yVar = by ? varMultiple[ii] : varSingle;
        SGMDArrayVariable leVar = null;
        SGMDArrayVariable ueVar = null;
        SGMDArrayVariable ehVar = null;
        if (be) {
          for (int jj = 0; jj < this.mErrorBarHolderVariables.length; jj++) {
            SGMDArrayVariable var = this.mErrorBarHolderVariables[jj];
            if (var.equals(xVar) || var.equals(yVar)) {
              ehVar = var;
              leVar = this.mLowerErrorVariables[jj];
              ueVar = this.mUpperErrorVariables[jj];
              break;
            }
          }
        }
        SGMDArrayVariable tlVar = null;
        SGMDArrayVariable thVar = null;
        if (bt) {
          for (int jj = 0; jj < this.mTickLabelHolderVariables.length; jj++) {
            SGMDArrayVariable var = this.mTickLabelHolderVariables[jj];
            if (var.equals(xVar) || var.equals(yVar)) {
              thVar = var;
              tlVar = this.mTickLabelVariables[jj];
              break;
            }
          }
        }
        SGMDArrayDataColumnInfo xInfo = SGDataFileUtility.createDataColumnInfo(xVar, X_VALUE);
        SGMDArrayDataColumnInfo yInfo = SGDataFileUtility.createDataColumnInfo(yVar, Y_VALUE);
        SGMDArrayDataColumnInfo leInfo = null;
        SGMDArrayDataColumnInfo ueInfo = null;
        SGMDArrayDataColumnInfo ehInfo = null;
        if (leVar != null && ueVar != null && ehVar != null) {
          leInfo =
              SGDataFileUtility.createErrorBarInfo(leVar, LOWER_ERROR_VALUE, leVar, ueVar, ehVar);
          ueInfo =
              SGDataFileUtility.createErrorBarInfo(ueVar, UPPER_ERROR_VALUE, leVar, ueVar, ehVar);
          ehInfo = (SGMDArrayDataColumnInfo) (ehVar.equals(xVar) ? xInfo : yInfo).clone();
        }
        SGMDArrayDataColumnInfo tlInfo = null;
        SGMDArrayDataColumnInfo thInfo = null;
        if (tlVar != null && thVar != null) {
          tlInfo = SGDataFileUtility.createDataColumnInfo(tlVar, TICK_LABEL, thVar.getName());
          thInfo = (SGMDArrayDataColumnInfo) (thVar.equals(xVar) ? xInfo : yInfo).clone();
        }
        SGSXYMDArrayData data =
            new SGSXYMDArrayData(
                (SGMDArrayFile) this.getDataSource(),
                this.getDataSourceObserver(),
                xInfo,
                yInfo,
                leInfo,
                ueInfo,
                ehInfo,
                tlInfo,
                thInfo,
                this.mStride,
                this.mTickLabelStride,
                this.isStrideAvailable());
        data.setDecimalPlaces(this.getDecimalPlaces());
        data.setExponent(this.getExponent());
        data.setTimeStride(this.mTimeStride);
        data.setOrigin(this.getOriginMap());
        data.setShift(this.getShift());

        // sets the cache
        if (sxyCacheArray != null) {
          data.setCache(sxyCacheArray[ii]);
        }

        dataArray[ii] = (SGISXYTypeSingleData) data;
      }

      SGDataViewerUtility.syncDataValueHistory(this.mEditedDataValueList, dataArray);

      return dataArray;
    }
  }

  /** Returns the number of child data. */
  @Override
  public int getChildNumber() {
    if (this.isDimensionPicked()) {
      int[] pickUpIndices = this.getPickUpDimensionIndices();
      return pickUpIndices.length;
    } else {
      SGMDArrayVariable[] varMultiple = this.getMultipleVariables();
      return varMultiple.length;
    }
  }

  /** Returns an array of multiple SXYData objects. */
  @Override
  public SGISXYTypeMultipleData[] getSXYTypeMultipleDataArray() {
    if (this.isDimensionPicked()) {
      SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
      SGISXYTypeMultipleData[] ret = new SGISXYTypeMultipleData[sxyArray.length];
      for (int ii = 0; ii < sxyArray.length; ii++) {
        SGSXYMDArrayData data = (SGSXYMDArrayData) sxyArray[ii];
        List<SGMDArrayVariable> pickUpVars = this.getPickUpMDArrayVariables();
        SGMDArrayVariable pickUpVar = pickUpVars.get(0);
        final int len = pickUpVar.getDimensionLength(KEY_SXY_PICKUP_DIMENSION);
        Integer pickUpDim = pickUpVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        ret[ii] = data.toMultiple(this.hasMultipleYValues(), pickUpDim, len);
      }
      // disposes of data objects
      SGDataMiscUtility.disposeSXYDataArray(sxyArray);
      return ret;
    } else {
      SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
      SGISXYTypeMultipleData[] ret = new SGISXYTypeMultipleData[sxyArray.length];
      for (int ii = 0; ii < sxyArray.length; ii++) {
        SGSXYMDArrayData data = (SGSXYMDArrayData) sxyArray[ii];
        ret[ii] = data.toMultiple();
      }
      // disposes of data objects
      SGDataMiscUtility.disposeSXYDataArray(sxyArray);
      return ret;
    }
  }

  /** Returns true if this data enables to be split. */
  @Override
  public boolean isSplitEnabled() {
    if (this.isDimensionPicked()) {
      int[] indices = this.getPickUpDimensionIndices();
      return (indices.length > 1);
    } else {
      SGMDArrayVariable[] varMultiple = this.getMultipleVariables();
      final int len = varMultiple.length;
      if (len > 1) {
        return true;
      }
      return false;
    }
  }

  /** Returns an array of current column types. */
  @Override
  public String[] getCurrentColumnType() {
    SGMDArrayVariable[] vars = this.getVariables();
    String[] array = new String[vars.length];
    for (int ii = 0; ii < array.length; ii++) {
      SGMDArrayVariable var = vars[ii];
      final int lIndex = this.find(var, this.mLowerErrorVariables);
      final int uIndex = this.find(var, this.mUpperErrorVariables);
      final int tIndex = this.find(var, this.mTickLabelVariables);
      if (this.find(var, this.mXVariables) != -1) {
        array[ii] = X_VALUE;
      } else if (this.find(var, this.mYVariables) != -1) {
        array[ii] = Y_VALUE;
      } else if ((lIndex != -1) || (uIndex != -1)) {
        String name = null;
        int index = -1;
        if (lIndex == uIndex) {
          name = LOWER_UPPER_ERROR_VALUE;
          index = lIndex;
        } else if (lIndex != -1) {
          name = LOWER_ERROR_VALUE;
          index = lIndex;
        } else if (uIndex != -1) {
          name = UPPER_ERROR_VALUE;
          index = uIndex;
        }
        if (name == null) {
          return null;
        }
        String varName = this.mErrorBarHolderVariables[index].getName();
        final int hIndex = this.getVariableIndex(varName);
        if (hIndex == -1) {
          return null;
        }
        array[ii] = SGDataColumnTitleUtility.appendColumnTitle(name, varName);
      } else if (tIndex != -1) {
        String varName = this.mTickLabelHolderVariables[tIndex].getName();
        final int hIndex = this.getVariableIndex(varName);
        if (hIndex == -1) {
          return null;
        }
        array[ii] = SGDataColumnTitleUtility.appendColumnTitle(TICK_LABEL, varName);
      } else {
        array[ii] = "";
      }
    }
    return array;
  }

  /**
   * Sets the type of data columns.
   *
   * @param columns an array of column types
   */
  @Override
  public boolean setColumnType(String[] columns) {
    if (this.isDimensionPicked()) {
      return this.setColumnTypeDimensionPicked(columns);
    } else {
      return this.setColumnTypeDimensionNotPicked(columns);
    }
  }

  /** Returns a map which has data information. */
  @Override
  public Map<String, Object> getInfoMap() {
    Map<String, Object> infoMap = super.getInfoMap();
    infoMap.put(KEY_SXY_MULTIPLE, Boolean.TRUE);
    if (this.isDimensionPicked()) {
      infoMap.put(KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
      infoMap.put(KEY_SXY_PICKUP_INDICES, this.mPickUpDimensionInfo.getIndices());
      Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
      SGMDArrayVariable[] vars = this.getVariables();
      for (int ii = 0; ii < vars.length; ii++) {
        String name = vars[ii].getName();
        Integer index = vars[ii].getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        if (index != null && index != -1) {
          dimensionIndexMap.put(name, index);
        }
      }
      infoMap.put(KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP, dimensionIndexMap);

    } else {
      infoMap.put(KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    }
    return infoMap;
  }

  /**
   * Sets a given data.
   *
   * @param data a data
   */
  @Override
  public boolean setData(SGData data) {
    if (!(data instanceof SGSXYMDArrayMultipleData)) {
      throw new IllegalArgumentException(
          "Given data is not the instance of SGSXYMultipleMDArrayData class.");
    }
    if (super.setData(data) == false) {
      return false;
    }
    SGSXYMDArrayMultipleData mdData = (SGSXYMDArrayMultipleData) data;
    this.mXVariables = mdData.getXVariables();
    this.mYVariables = mdData.getYVariables();
    if (mdData.isErrorBarAvailable()) {
      this.mLowerErrorVariables = this.createVariableArray(mdData.getLowerErrorVariable());
      this.mUpperErrorVariables = this.createVariableArray(mdData.getUpperErrorVariable());
      this.mErrorBarHolderVariables = this.createVariableArray(mdData.getErrorBarHolderVariable());
    }
    if (mdData.isTickLabelAvailable()) {
      this.mTickLabelVariables = this.createVariableArray(mdData.getTickLabelVariable());
      this.mTickLabelHolderVariables =
          this.createVariableArray(mdData.getTickLabelHolderVariable());
    }
    if (mdData.mPickUpDimensionInfo != null) {
      this.mPickUpDimensionInfo =
          (SGMDArrayPickUpDimensionInfo) mdData.mPickUpDimensionInfo.clone();
    }
    this.mStride = mdData.getStride();
    this.mTickLabelStride = mdData.getTickLabelStride();
    this.updateDimensionIndices();
    this.setShift(mdData.getShift());
    return true;
  }

  /**
   * Writes properties of this object to the Element.
   *
   * @param el the Element object
   * @param params type of the method to save properties
   */
  @Override
  public boolean writeProperty(Element el, SGExportParameter params) {
    return this.mExporter.writeProperty(el, params);
  }

  String bindVariableNameInBracket(String name) {
    StringBuilder sb = new StringBuilder();
    sb.append('{');
    sb.append(name);
    sb.append('}');
    return sb.toString();
  }

  String bindVariableNamesInBracket(
      SGMDArrayVariable[] variables, final boolean withDimensionIndex) {
    StringBuilder sb = new StringBuilder("{");
    for (int ii = 0; ii < variables.length; ii++) {
      if (ii > 0) {
        sb.append(",");
      }
      SGMDArrayVariable var = variables[ii];
      sb.append(var.getName());
      if (withDimensionIndex) {
        sb.append(":");
        sb.append(var.getGenericDimensionIndex());
      }
    }
    sb.append("}");
    return sb.toString();
  }

  String bindVariableNames(SGMDArrayVariable var, final boolean withDimensionIndex) {
    StringBuilder sb = new StringBuilder();
    sb.append(var.getName());
    if (withDimensionIndex) {
      sb.append(":");
      sb.append(var.getGenericDimensionIndex());
    }
    return sb.toString();
  }

  public static SGISXYTypeMultipleData merge(final List<SGData> dataList) {
    return SGDataMergeUtility.mergeMDArray(dataList);
  }

  /** Returns the stride. */
  public SGIntegerSeriesSet getStride() {
    if (this.mStride != null) {
      return (SGIntegerSeriesSet) this.mStride.clone();
    } else {
      return null;
    }
  }

  /**
   * Sets the stride.
   *
   * @param stride the stride
   */
  public void setStride(final SGIntegerSeriesSet stride) {
    if (!SGUtility.equals(this.mStride, stride)) {
      this.clearCache();
    }
    if (stride != null) {
      this.mStride = (SGIntegerSeriesSet) stride.clone();
    } else {
      this.mStride = null;
    }
  }

  /** Returns the copy of this data object. */
  public Object clone() {
    SGSXYMDArrayMultipleData data = (SGSXYMDArrayMultipleData) super.clone();
    data.mXVariables = copyVariables(this.mXVariables);
    data.mYVariables = copyVariables(this.mYVariables);
    data.mLowerErrorVariables = copyVariables(this.mLowerErrorVariables);
    data.mUpperErrorVariables = copyVariables(this.mUpperErrorVariables);
    data.mErrorBarHolderVariables = copyVariables(this.mErrorBarHolderVariables);
    data.mTickLabelVariables = copyVariables(this.mTickLabelVariables);
    data.mTickLabelHolderVariables = copyVariables(this.mTickLabelHolderVariables);
    data.mPickUpDimensionInfo = (SGMDArrayPickUpDimensionInfo) this.getPickUpDimensionInfo();
    data.mStride = this.getStride();
    data.mTickLabelStride = this.getTickLabelStride();
    data.setShift(this.getShift());
    return data;
  }

  /** Returns a map of stride for data arrays. */
  @Override
  protected Map<String, SGIntegerSeriesSet> getStrideMap() {
    Map<String, SGIntegerSeriesSet> map = new HashMap<String, SGIntegerSeriesSet>();
    map.put(KEY_SXY_STRIDE, this.mStride);
    map.put(KEY_SXY_TICK_LABEL_STRIDE, this.mTickLabelStride);
    return map;
  }

  /**
   * Sets the stride of data arrays.
   *
   * @param map the map of the stride
   */
  @Override
  public void setStrideMap(Map<String, SGIntegerSeriesSet> map) {
    this.mStride = map.get(KEY_SXY_STRIDE);
  }

  /** Returns the indices of tick labels. */

  /** Returns an index array of dimension. */
  public int[] getDimensionIndices() {
    return this.getPickUpDimensionIndices();
  }

  /** Returns the number of data points without taking into account the stride. */
  @Override
  public int getAllPointsNumber() {
    SGMDArrayVariable var;
    if (this.isDimensionPicked()) {
      SGMDArrayVariable xVar = this.getXVariable();
      if (xVar != null) {
        var = xVar;
      } else {
        var = this.getYVariable();
      }
    } else {
      SGMDArrayVariable singleVar = this.getSingleVariable();
      if (singleVar != null) {
        var = singleVar;
      } else {
        SGMDArrayVariable[] multipleVars = this.getMultipleVariables();
        var = multipleVars[0];
      }
    }
    return var.getGenericDimensionLength();
  }

  /**
   * Sets the information of data columns.
   *
   * @param cols an array of column information
   * @param info information of picked up column
   */
  public boolean setColumnType(SGDataColumnInfo[] cols, SGPickUpDimensionInfo info) {

    // set dimensions
    if (this.setDimensionMap(cols) == false) {
      return false;
    }

    // set column types
    String[] columns = new String[cols.length];
    for (int ii = 0; ii < columns.length; ii++) {
      columns[ii] = cols[ii].getColumnType();
    }
    return this.setColumnTypeWithPickUp(columns, info);
  }

  boolean callSuperWriteProperty(final Element el, final SGExportParameter type) {
    return super.writeProperty(el, type);
  }

  @Override
  public boolean setColumnTypeDimensionPicked(String[] columns) {
    List<SGMDArrayVariable> xVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> yVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> lVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> uVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> tVarList = new ArrayList<SGMDArrayVariable>();
    List<String> lColList = new ArrayList<String>();
    List<String> uColList = new ArrayList<String>();
    List<String> tColList = new ArrayList<String>();
    if (!this.getVariables(
        columns, xVarList, yVarList, lVarList, uVarList, tVarList, lColList, uColList, tColList)) {
      return false;
    }

    SGMDArrayVariable xVar = null;
    if (xVarList.size() == 1) {
      xVar = xVarList.get(0);
    }
    SGMDArrayVariable yVar = null;
    if (yVarList.size() == 1) {
      yVar = yVarList.get(0);
    }
    if (xVar == null && yVar == null) {
      return false;
    }
    SGMDArrayVariable lVar = lVarList.size() == 1 ? lVarList.get(0) : null;
    SGMDArrayVariable uVar = uVarList.size() == 1 ? uVarList.get(0) : null;
    SGMDArrayVariable tVar = tVarList.size() == 1 ? tVarList.get(0) : null;
    if (!((lVar != null) && (uVar != null)) && !((lVar == null) && (uVar == null))) {
      return false;
    }

    List<SGMDArrayVariable> ehVarList =
        this.findErrorVarHolderVars(xVarList, yVarList, lVarList, uVarList, lColList, uColList);
    SGMDArrayVariable ehVar = null;
    if (ehVarList != null && ehVarList.size() == 1) {
      ehVar = ehVarList.get(0);
    }

    List<SGMDArrayVariable> thVarList =
        this.findTickLabelHolderVars(xVarList, yVarList, tVarList, tColList);
    SGMDArrayVariable thVar = null;
    if (thVarList != null && thVarList.size() == 1) {
      thVar = thVarList.get(0);
    }

    // set variables
    this.mXVariables = this.createVariableArray(xVar);
    this.mYVariables = this.createVariableArray(yVar);
    this.mLowerErrorVariables = this.createVariableArray(lVar);
    this.mUpperErrorVariables = this.createVariableArray(uVar);
    this.mErrorBarHolderVariables = this.createVariableArray(ehVar);
    this.mTickLabelVariables = this.createVariableArray(tVar);
    this.mTickLabelHolderVariables = this.createVariableArray(thVar);

    return true;
  }

  public boolean setColumnTypeDimensionNotPicked(String[] columns) {
    List<SGMDArrayVariable> xVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> yVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> lVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> uVarList = new ArrayList<SGMDArrayVariable>();
    List<SGMDArrayVariable> tVarList = new ArrayList<SGMDArrayVariable>();
    List<String> lColList = new ArrayList<String>();
    List<String> uColList = new ArrayList<String>();
    List<String> tColList = new ArrayList<String>();
    if (!this.getVariables(
        columns, xVarList, yVarList, lVarList, uVarList, tVarList, lColList, uColList, tColList)) {
      return false;
    }

    List<SGMDArrayVariable> ehVarList =
        this.findErrorVarHolderVars(xVarList, yVarList, lVarList, uVarList, lColList, uColList);
    if (ehVarList == null) {
      return false;
    }
    List<SGMDArrayVariable> thVarList =
        this.findTickLabelHolderVars(xVarList, yVarList, tVarList, tColList);
    if (thVarList == null) {
      return false;
    }

    SGMDArrayVariable[] xVars = xVarList.toArray(new SGMDArrayVariable[xVarList.size()]);
    SGMDArrayVariable[] yVars = yVarList.toArray(new SGMDArrayVariable[yVarList.size()]);
    SGMDArrayVariable[] lVars = null;
    SGMDArrayVariable[] uVars = null;
    SGMDArrayVariable[] ehVars = null;
    if (lVarList.size() != 0) {
      lVars = lVarList.toArray(new SGMDArrayVariable[lVarList.size()]);
      uVars = uVarList.toArray(new SGMDArrayVariable[uVarList.size()]);
      ehVars = ehVarList.toArray(new SGMDArrayVariable[ehVarList.size()]);
    }
    SGMDArrayVariable[] tVars = null;
    SGMDArrayVariable[] thVars = null;
    if (tVarList.size() != 0) {
      tVars = tVarList.toArray(new SGMDArrayVariable[tVarList.size()]);
      thVars = thVarList.toArray(new SGMDArrayVariable[thVarList.size()]);
    }

    // set to the attributes
    this.mXVariables = xVars;
    this.mYVariables = yVars;
    this.mLowerErrorVariables = lVars;
    this.mUpperErrorVariables = uVars;
    this.mErrorBarHolderVariables = ehVars;
    this.mTickLabelVariables = tVars;
    this.mTickLabelHolderVariables = thVars;

    return true;
  }

  private List<SGMDArrayVariable> findErrorVarHolderVars(
      List<SGMDArrayVariable> xVarList,
      List<SGMDArrayVariable> yVarList,
      List<SGMDArrayVariable> lVarList,
      List<SGMDArrayVariable> uVarList,
      List<String> lColList,
      List<String> uColList) {

    SGMDArrayVariable[] vars = this.getVariables();
    List<SGMDArrayVariable> ehVarList = new ArrayList<SGMDArrayVariable>();
    List<Integer> ehList = new ArrayList<Integer>();
    for (int ii = 0; ii < uVarList.size(); ii++) {
      String col = uColList.get(ii);
      Integer eh = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(col, this);
      if (eh == null) {
        return null;
      }
      ehList.add(eh);
    }
    for (int ii = 0; ii < lVarList.size(); ii++) {
      String col = lColList.get(ii);
      Integer eh = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(col, this);
      if (eh == null) {
        return null;
      }
      if (ehList.contains(eh) == false) {
        return null;
      }
      SGMDArrayVariable ehVar = vars[eh.intValue()];
      if (!xVarList.contains(ehVar) && !yVarList.contains(ehVar)) {
        return null;
      }
      ehVarList.add(ehVar);
    }
    return ehVarList;
  }

  private List<SGMDArrayVariable> findTickLabelHolderVars(
      List<SGMDArrayVariable> xVarList,
      List<SGMDArrayVariable> yVarList,
      List<SGMDArrayVariable> tVarList,
      List<String> tColList) {

    SGMDArrayVariable[] vars = this.getVariables();
    List<SGMDArrayVariable> thVarList = new ArrayList<SGMDArrayVariable>();
    List<Integer> thList = new ArrayList<Integer>();
    for (int ii = 0; ii < tVarList.size(); ii++) {
      String col = tColList.get(ii);
      Integer th = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(col, this);
      if (th == null) {
        return null;
      }
      thList.add(th);
    }
    for (int ii = 0; ii < tVarList.size(); ii++) {
      String col = tColList.get(ii);
      Integer th = SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle(col, this);
      if (th == null) {
        return null;
      }
      if (thList.contains(th) == false) {
        return null;
      }
      SGMDArrayVariable thVar = vars[th.intValue()];
      if (!xVarList.contains(thVar) && !yVarList.contains(thVar)) {
        return null;
      }
      thVarList.add(thVar);
    }
    return thVarList;
  }

  private boolean getVariables(
      String[] columns,
      List<SGMDArrayVariable> xVarList,
      List<SGMDArrayVariable> yVarList,
      List<SGMDArrayVariable> lVarList,
      List<SGMDArrayVariable> uVarList,
      List<SGMDArrayVariable> tVarList,
      List<String> lColList,
      List<String> uColList,
      List<String> tColList) {

    SGMDArrayVariable[] vars = this.getVariables();
    for (int ii = 0; ii < columns.length; ii++) {
      SGMDArrayVariable var = vars[ii];
      String valueType = var.getValueType();
      if (SGDataDataTypeUtility.isEqualColumnType(X_VALUE, columns[ii])) {
        if (!VALUE_TYPE_NUMBER.equals(valueType)) {
          return false;
        }
        xVarList.add(var);
      } else if (SGDataDataTypeUtility.isEqualColumnType(Y_VALUE, columns[ii])) {
        if (!VALUE_TYPE_NUMBER.equals(valueType)) {
          return false;
        }
        yVarList.add(var);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(columns[ii], LOWER_ERROR_VALUE)) {
        if (!VALUE_TYPE_NUMBER.equals(valueType)) {
          return false;
        }
        lVarList.add(var);
        lColList.add(columns[ii]);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(columns[ii], UPPER_ERROR_VALUE)) {
        if (!VALUE_TYPE_NUMBER.equals(valueType)) {
          return false;
        }
        uVarList.add(var);
        uColList.add(columns[ii]);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(columns[ii], LOWER_UPPER_ERROR_VALUE)) {
        if (!VALUE_TYPE_NUMBER.equals(valueType)) {
          return false;
        }
        lVarList.add(var);
        uVarList.add(var);
        lColList.add(columns[ii]);
        uColList.add(columns[ii]);
      } else if (SGDataDataTypeUtility.columnTypeStartsWith(columns[ii], TICK_LABEL)) {
        tVarList.add(var);
        tColList.add(columns[ii]);
      } else if ("".equals(columns[ii])) {
        continue;
      } else {
        return false;
      }
    }

    // checks common conditions
    if (xVarList.size() == 0 && yVarList.size() == 0) {
      return false;
    }
    if (xVarList.size() > 1 && yVarList.size() > 1) {
      return false;
    }
    if (lVarList.size() != uVarList.size()) {
      return false;
    }

    return true;
  }

  /** Clears all information for picked up dimension. */
  public boolean clearPickUp() {
    if (!this.isDimensionPicked()) {
      return true;
    }

    // get pick up variable
    // SGMDArrayVariable pickUpVar = this.getPickUpMDArrayVariable();
    SGDataColumnInfo[] cols = this.getColumnInfo();

    // replaces attributes
    this.mErrorBarHolderVariables = new SGMDArrayVariable[1];
    this.mTickLabelHolderVariables = new SGMDArrayVariable[1];
    for (int ii = 0; ii < cols.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
      String columnType = mdCol.getColumnType();
      String uColumnType = columnType.toUpperCase();
      String cType;
      final boolean ebFlag;
      if (uColumnType.startsWith(LOWER_ERROR_VALUE.toUpperCase())) {
        ebFlag = true;
        cType = LOWER_ERROR_VALUE;
      } else if (uColumnType.startsWith(UPPER_ERROR_VALUE.toUpperCase())) {
        ebFlag = true;
        cType = UPPER_ERROR_VALUE;
      } else if (uColumnType.startsWith(LOWER_UPPER_ERROR_VALUE.toUpperCase())) {
        ebFlag = true;
        cType = LOWER_UPPER_ERROR_VALUE;
      } else if (columnType.startsWith(TICK_LABEL)) {
        ebFlag = false;
        cType = TICK_LABEL;
      } else {
        continue;
      }
      StringBuilder sb = new StringBuilder();
      sb.append(cType);
      sb.append(SGDataColumnTitleUtility.MID_COLUMN);
      final int index = sb.toString().length();
      String holderVarName = columnType.substring(index);
      SGMDArrayVariable holderVar = this.findVariable(holderVarName);
      if (ebFlag) {
        this.mErrorBarHolderVariables[0] = holderVar;
      } else {
        this.mTickLabelHolderVariables[0] = holderVar;
      }
    }

    // clears the dimension index
    for (int ii = 0; ii < cols.length; ii++) {
      SGMDArrayDataColumnInfo mdCol = (SGMDArrayDataColumnInfo) cols[ii];
      mdCol.setDimensionIndex(KEY_SXY_PICKUP_DIMENSION, -1);
    }

    // clears the attribute
    this.setPickUpDimensionInfo(null);

    return true;
  }

  /** Returns the map of dimension index that are used. */
  public Map<String, Map<String, Integer>> getUsedDimensionIndexMap() {
    return this.getDimensionIndexMap();
  }

  /** Returns an array of variables that are assigned the column type. */
  @Override
  public SGMDArrayVariable[] getAssignedVariables() {
    List<SGMDArrayVariable> varList = new ArrayList<SGMDArrayVariable>();
    for (SGMDArrayVariable var : this.mXVariables) {
      varList.add(var);
    }
    for (SGMDArrayVariable var : this.mYVariables) {
      varList.add(var);
    }
    if (this.isErrorBarAvailable()) {
      for (SGMDArrayVariable var : this.mLowerErrorVariables) {
        varList.add(var);
      }
      for (SGMDArrayVariable var : this.mUpperErrorVariables) {
        varList.add(var);
      }
    }
    if (this.isTickLabelAvailable()) {
      for (SGMDArrayVariable var : this.mTickLabelVariables) {
        varList.add(var);
      }
    }
    SGMDArrayVariable[] vars = new SGMDArrayVariable[varList.size()];
    return varList.toArray(vars);
  }

  void getVariableNames(
      List<String> xNameList,
      List<String> yNameList,
      List<String> leNameList,
      List<String> ueNameList,
      List<String> tlNameList) {

    if (this.isDimensionPicked()) {
      if (this.hasMultipleYValues()) {
        if (this.mXVariables.length > 0) {
          xNameList.add(this.mXVariables[0].getName());
        }
        yNameList.add(this.mYVariables[0].getName());
      } else {
        if (this.mYVariables.length > 0) {
          yNameList.add(this.mYVariables[0].getName());
        }
        xNameList.add(this.mXVariables[0].getName());
      }
      if (this.isErrorBarAvailable()) {
        leNameList.add(this.mLowerErrorVariables[0].getName());
        ueNameList.add(this.mUpperErrorVariables[0].getName());
      }
      if (this.isTickLabelAvailable()) {
        tlNameList.add(this.mTickLabelVariables[0].getName());
      }

    } else {
      for (int ii = 0; ii < this.mXVariables.length; ii++) {
        xNameList.add(this.mXVariables[ii].getName());
      }
      for (int ii = 0; ii < this.mYVariables.length; ii++) {
        yNameList.add(this.mYVariables[ii].getName());
      }
      if (this.isErrorBarAvailable()) {
        for (int ii = 0; ii < this.mLowerErrorVariables.length; ii++) {
          leNameList.add(this.mLowerErrorVariables[ii].getName());
        }
        for (int ii = 0; ii < this.mUpperErrorVariables.length; ii++) {
          ueNameList.add(this.mUpperErrorVariables[ii].getName());
        }
      }
      if (this.isTickLabelAvailable()) {
        for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
          tlNameList.add(this.mTickLabelVariables[ii].getName());
        }
      }
    }
  }

  static final String INDEX_DIM_NAME = "index";

  static final String PICKUP_DIM_NAME = "pickUp";

  static final String X_VALUE_VAR_NAME = "x";

  static final String Y_VALUE_VAR_NAME = "y";

  /**
   * Adds variables to a netCDF file.
   *
   * @param builder a netCDF file
   */
  @Override
  protected boolean addVariables(NetcdfFormatWriter.Builder builder) {
    return this.mExporter.addVariables(builder);
  }

  protected void addPickUpDimension(
      SGMDArrayVariable var, Dimension pickUpDim, List<Dimension> dimList) {
    this.addDimension(var, KEY_SXY_PICKUP_DIMENSION, pickUpDim, dimList);
  }

  boolean addDoubleVariable(
      NetcdfFormatWriter.Builder builder,
      Dimension indexDim,
      Dimension timeDim,
      Dimension pickUpDim,
      String name) {
    SGMDArrayVariable var = this.findVariable(name);
    List<Dimension> dimList = new ArrayList<Dimension>();
    dimList.add(indexDim);
    dimList = this.addTimeDimension(var, timeDim, dimList);
    this.addPickUpDimension(var, pickUpDim, dimList);
    return this.addDoubleVariable(builder, dimList, name);
  }

  boolean addStringVariable(
      NetcdfFormatWriter.Builder builder,
      Dimension indexDim,
      Dimension timeDim,
      Dimension pickUpDim,
      String name,
      final int maxStrLen) {
    SGMDArrayVariable var = this.findVariable(name);
    List<Dimension> dimList = new ArrayList<Dimension>();
    dimList.add(indexDim);
    dimList = this.addTimeDimension(var, timeDim, dimList);
    this.addPickUpDimension(var, pickUpDim, dimList);
    return this.addStringVariable(builder, dimList, name, maxStrLen);
  }

  /**
   * Writes data to a netCDF file.
   *
   * @param writer a netCDF file
   */
  protected boolean writeData(NetcdfFormatWriter writer) {
    return this.mExporter.writeData(writer);
  }

  private boolean writeDoubleData(NetcdfFormatWriter writer, List<String> nameList) {
    return this.mExporter.writeDoubleData(writer, nameList);
  }

  private boolean writeStringData(NetcdfFormatWriter writer, List<String> nameList) {
    return this.mExporter.writeStringData(writer, nameList);
  }

  boolean write1DStringArray(
      NetcdfFormatWriter writer,
      String varName,
      List<Dimension> ncDimList,
      Map<String, Integer> mdArrayIndexMap,
      final int index) {
    SGMDArrayVariable mdVar = this.findVariable(varName);
    Dimension ncDim = ncDimList.get(0);
    Integer dimension = mdArrayIndexMap.get(ncDim.getShortName());
    final int num = ncDim.getLength();
    String[] textArray = new String[num];
    for (int ii = 0; ii < num; ii++) {
      String str = mdVar.getString(dimension, ii);
      textArray[ii] = SGDataTextUtility.encodeString(str);
    }
    Array array = new ArrayObject.D1(DataType.STRING, String.class, false, num);
    Index tempIndex = array.getIndex();
    for (int ii = 0; ii < num; ii++) {
      array.setObject(tempIndex.set(ii), textArray[ii]);
    }
    if (!this.writeStringArray(writer, varName, array)) {
      return false;
    }
    return true;
  }

  boolean write2DStringArray(
      NetcdfFormatWriter writer,
      String varName,
      List<Dimension> ncDimList,
      Map<String, Integer> mdArrayIndexMap,
      final int index) {
    SGMDArrayVariable mdVar = this.findVariable(varName);
    Integer timeDimIndex = mdArrayIndexMap.get(TIME_DIM_NAME);
    String mdSecondDimName = (timeDimIndex != null) ? KEY_TIME_DIMENSION : KEY_SXY_PICKUP_DIMENSION;
    String mapSecondDimName = (timeDimIndex != null) ? TIME_DIM_NAME : PICKUP_DIM_NAME;
    final int firstDimLen = this.getAllPointsNumber();
    final int secondDimLen = mdVar.getDimensionLength(mdSecondDimName);
    Integer dimension1 = mdArrayIndexMap.get(INDEX_DIM_NAME);
    Integer dimension2 = mdArrayIndexMap.get(mapSecondDimName);
    String[][] textArray = new String[firstDimLen][secondDimLen];
    for (int ii = 0; ii < firstDimLen; ii++) {
      for (int jj = 0; jj < secondDimLen; jj++) {
        int[] origins = mdVar.getOrigins();
        origins[dimension1] = ii;
        origins[dimension2] = jj;
        String str = mdVar.getString(origins);
        textArray[ii][jj] = SGDataTextUtility.encodeString(str);
      }
    }
    Array array =
        new ArrayObject.D2(DataType.STRING, String.class, false, firstDimLen, secondDimLen);
    Index tempIndex = array.getIndex();
    for (int ii = 0; ii < firstDimLen; ii++) {
      for (int jj = 0; jj < secondDimLen; jj++) {
        array.setObject(tempIndex.set(ii, jj), textArray[ii][jj]);
      }
    }
    if (!this.writeStringArray(writer, varName, array)) {
      return false;
    }
    return true;
  }

  boolean write3DStringArray(
      NetcdfFormatWriter writer,
      String varName,
      List<Dimension> ncDimList,
      Map<String, Integer> mdArrayIndexMap,
      final int index) {
    SGMDArrayVariable mdVar = this.findVariable(varName);
    final int firstDimLen = this.getAllPointsNumber();
    final int secondDimLen = mdVar.getDimensionLength(KEY_TIME_DIMENSION);
    final int thirdDimLen = mdVar.getDimensionLength(KEY_SXY_PICKUP_DIMENSION);
    Integer dimension1 = mdArrayIndexMap.get(INDEX_DIM_NAME);
    Integer dimension2 = mdArrayIndexMap.get(TIME_DIM_NAME);
    Integer dimension3 = mdArrayIndexMap.get(PICKUP_DIM_NAME);
    String[][][] textArray = new String[firstDimLen][secondDimLen][thirdDimLen];
    for (int ii = 0; ii < firstDimLen; ii++) {
      for (int jj = 0; jj < secondDimLen; jj++) {
        for (int kk = 0; kk < thirdDimLen; kk++) {
          int[] origins = mdVar.getOrigins();
          origins[dimension1] = ii;
          origins[dimension2] = jj;
          origins[dimension3] = kk;
          String str = mdVar.getString(origins);
          textArray[ii][jj][kk] = SGDataTextUtility.encodeString(str);
        }
      }
    }
    Array array =
        new ArrayObject.D3(
            DataType.STRING, String.class, false, firstDimLen, secondDimLen, thirdDimLen);
    Index tempIndex = array.getIndex();
    for (int ii = 0; ii < firstDimLen; ii++) {
      for (int jj = 0; jj < secondDimLen; jj++) {
        for (int kk = 0; kk < thirdDimLen; kk++) {
          array.setObject(tempIndex.set(ii, jj, kk), textArray[ii][jj][kk]);
        }
      }
    }
    if (!this.writeStringArray(writer, varName, array)) {
      return false;
    }
    return true;
  }

  int getMaxLength(SGMDArrayVariable var) {
    int maxLength = 0;
    Integer genericIndex = var.getDimensionIndex(KEY_GENERIC_DIMENSION);
    Integer pickUpIndex = var.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
    Integer timeIndex = var.getDimensionIndex(KEY_TIME_DIMENSION);
    final int pIdx = (pickUpIndex != null) ? pickUpIndex.intValue() : -1;
    final int tIdx = (timeIndex != null) ? timeIndex.intValue() : -1;
    int[] dimension = var.getDimensions();
    final int genericNum = dimension[genericIndex];
    final int pickUpNum = (pIdx != -1) ? dimension[pIdx] : -1;
    final int timeNum = (tIdx != -1) ? dimension[tIdx] : -1;
    for (int ii = 0; ii < genericNum; ii++) {
      if (pickUpNum != -1 && timeNum != -1) {
        for (int jj = 0; jj < pickUpNum; jj++) {
          for (int kk = 0; kk < timeNum; kk++) {
            int[] origins = var.getOrigins();
            origins[genericIndex] = ii;
            origins[pIdx] = jj;
            origins[tIdx] = kk;
            maxLength = this.getMaxLength(var, origins, maxLength);
          }
        }
      } else if (pickUpNum != -1) {
        for (int jj = 0; jj < pickUpNum; jj++) {
          int[] origins = var.getOrigins();
          origins[genericIndex] = ii;
          origins[pIdx] = jj;
          maxLength = this.getMaxLength(var, origins, maxLength);
        }

      } else if (timeNum != -1) {
        for (int kk = 0; kk < timeNum; kk++) {
          int[] origins = var.getOrigins();
          origins[genericIndex] = ii;
          origins[tIdx] = kk;
          maxLength = this.getMaxLength(var, origins, maxLength);
        }
      } else {
        int[] origins = var.getOrigins();
        origins[genericIndex] = ii;
        maxLength = this.getMaxLength(var, origins, maxLength);
      }
    }

    return maxLength;
  }

  int getMaxLength(SGMDArrayVariable var, int[] origins, final int curMaxLength) {
    int maxLength = curMaxLength;
    String textString = var.getString(origins);
    byte[] byteArray;
    try {
      byteArray = textString.getBytes(CHAR_SET_NAME_UTF8);
    } catch (UnsupportedEncodingException e) {
      return maxLength;
    }
    if (maxLength < byteArray.length) {
      maxLength = byteArray.length;
    }
    return maxLength;
  }

  /** Returns a text string of the data type to save into a NetCDF data set file. */
  @Override
  public String getNetCDFDataSetDataType() {
    if (this.isDimensionPicked()) {
      return SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DIMENSION_DATA;
    } else {
      return SGDataTypeConstants.SXY_MULTIPLE_NETCDF_DATA;
    }
  }

  /** Returns the shift. */
  public SGTuple2d getShift() {
    return this.mFormat.getShift();
  }

  /**
   * Sets the shift.
   *
   * @param shift the shift to set
   */
  public void setShift(SGTuple2d shift) {
    this.mFormat.setShift(shift);
  }

  /** Returns the list of child objects. */
  @Override
  public List<String> getChildNameList() {
    List<String> nameList = new ArrayList<String>();
    if (this.isDimensionPicked()) {
      SGIntegerSeriesSet indices = this.mPickUpDimensionInfo.getIndices();
      int[] indexArray = indices.getNumbers();
      for (int ii = 0; ii < indexArray.length; ii++) {
        nameList.add(Integer.toString(indexArray[ii]));
      }
    } else {
      SGMDArrayVariable[] vars = this.hasMultipleYValues() ? this.mYVariables : this.mXVariables;
      for (SGMDArrayVariable var : vars) {
        nameList.add(var.getName());
      }
    }
    return nameList;
  }

  /**
   * Returns the list of the name of child objects with given pick up information.
   *
   * @param pickUpInfo pick up information
   */
  public static List<String> getChildNameList(SGMDArrayPickUpDimensionInfo pickUpInfo) {
    List<String> nameList = new ArrayList<String>();
    SGIntegerSeriesSet indices = pickUpInfo.getIndices();
    int[] indexArray = indices.getNumbers();
    for (int ii = 0; ii < indexArray.length; ii++) {
      nameList.add(Integer.toString(indexArray[ii]));
    }
    return nameList;
  }

  /**
   * Returns the list of the name of child objects with given data columns.
   *
   * @param cols data columns
   */
  public static List<String> getChildNameList(SGDataColumnInfo[] cols) {
    int xNum = 0;
    int yNum = 0;
    for (int ii = 0; ii < cols.length; ii++) {
      String columnType = cols[ii].getColumnType();
      if (X_VALUE.equals(columnType)) {
        xNum++;
      } else if (Y_VALUE.equals(columnType)) {
        yNum++;
      }
    }
    List<String> nameList = new ArrayList<String>();
    for (int ii = 0; ii < cols.length; ii++) {
      String name = null;
      String columnType = cols[ii].getColumnType();
      String colName = cols[ii].getName();
      if (xNum > 1) {
        if (X_VALUE.equals(columnType)) {
          name = colName;
        }
      } else {
        if (Y_VALUE.equals(columnType)) {
          name = colName;
        }
      }
      if (name != null) {
        nameList.add(name);
      }
    }
    return nameList;
  }

  /**
   * Creates and returns a data buffer with given array of child indices.
   *
   * @param param parameters for data buffer
   * @param indices array of child indices
   */
  @Override
  public SGDataBuffer getDataBuffer(SGSXYDataBufferPolicy param, int[] indices) {
    return SGDataBufferUtility.getDataBuffer(this, param, indices);
  }

  static class SXYExportInfo {
    String[] xNames;
    double[][] xValues;
    MDArrayDataType[] xDataTypes;
    boolean xValid;
    String[] yNames;
    double[][] yValues;
    MDArrayDataType[] yDataTypes;
    boolean yValid;
    String[] leNames;
    double[][] leValues;
    MDArrayDataType[] leDataTypes;
    String[] ueNames;
    double[][] ueValues;
    MDArrayDataType[] ueDataTypes;
    String[] tlNames;
    String[][] tlValues;
    MDArrayDataType[] tlDataTypes;
  }

  static final String DEFAULT_VAR_NAME_BASE_X = "X";

  static final String DEFAULT_VAR_NAME_BASE_Y = "Y";

  private SXYExportInfo exportCommon(SGExportParameter mode, SGDataBufferPolicy policy) {
    return this.mExporter.exportCommon(mode, policy);
  }

  SGMDArrayVariable[] getVariablesInXYOrder(
      SGMDArrayVariable[] vars, SGMDArrayVariable[] holderVars) {
    SGMDArrayVariable[] xyVars = null;
    if (this.mXVariables != null) {
      if (SGUtility.containsAll(this.mXVariables, holderVars)) {
        xyVars = this.mXVariables;
      }
    }
    if (this.mYVariables != null) {
      if (SGUtility.containsAll(this.mYVariables, holderVars)) {
        xyVars = this.mYVariables;
      }
    }
    if (xyVars == null) {
      throw new Error("This should not happen.");
    }
    SGMDArrayVariable[] ret = new SGMDArrayVariable[xyVars.length];
    for (int ii = 0; ii < xyVars.length; ii++) {
      SGMDArrayVariable xyVar = xyVars[ii];
      int varIndex = -1;
      for (int jj = 0; jj < holderVars.length; jj++) {
        if (holderVars[jj].equals(xyVar)) {
          varIndex = jj;
          break;
        }
      }
      if (varIndex != -1) {
        ret[ii] = vars[varIndex];
      }
    }
    return ret;
  }

  String[] getNames(SGMDArrayVariable[] vars) {
    String[] names = new String[vars.length];
    for (int ii = 0; ii < names.length; ii++) {
      names[ii] = (vars[ii] != null) ? vars[ii].getName() : null;
    }
    return names;
  }

  MDArrayDataType[] getDataTypes(SGMDArrayVariable[] vars) {
    MDArrayDataType[] dataTypes = new MDArrayDataType[vars.length];
    for (int ii = 0; ii < dataTypes.length; ii++) {
      dataTypes[ii] = (vars[ii] != null) ? vars[ii].getDataType() : null;
    }
    return dataTypes;
  }

  /**
   * Exports to a HDF5 file.
   *
   * @param mode the mode parameter
   * @param policy the policy parameter
   * @param writer HDF5-file writer
   */
  @Override
  protected boolean exportToHDF5(
      IHDF5Writer writer, final SGExportParameter mode, SGDataBufferPolicy policy) {
    SXYExportInfo info = this.exportCommon(mode, policy);
    final double[][] xValues = info.xValues;
    final double[][] yValues = info.yValues;
    if (this.isDimensionPicked()) {
      if (info.xValid) {
        this.write(writer, info.xNames[0], info.xDataTypes[0], xValues);
      }
      if (info.yValid) {
        this.write(writer, info.yNames[0], info.yDataTypes[0], yValues);
      }
      if (this.isErrorBarAvailable()) {
        this.write(writer, info.leNames[0], info.leDataTypes[0], info.leValues);
        if (this.hasSameErrorValues(info, 0)) {
          this.write(writer, info.ueNames[0], info.ueDataTypes[0], info.ueValues);
        }
      }
      if (this.isTickLabelAvailable()) {
        this.write(writer, info.tlNames[0], info.tlValues);
      }
    } else {
      if (info.xValid) {
        for (int ii = 0; ii < info.xNames.length; ii++) {
          this.write(writer, info.xNames[ii], info.xDataTypes[ii], xValues[ii]);
        }
      }
      if (info.yValid) {
        for (int ii = 0; ii < info.yNames.length; ii++) {
          this.write(writer, info.yNames[ii], info.yDataTypes[ii], yValues[ii]);
        }
      }
      if (this.isErrorBarAvailable()) {
        for (int ii = 0; ii < info.leNames.length; ii++) {
          if (info.leNames[ii] != null && info.leValues[ii] != null) {
            this.write(writer, info.leNames[ii], info.leDataTypes[ii], info.leValues[ii]);
          }
        }
        for (int ii = 0; ii < info.ueNames.length; ii++) {
          if (this.hasSameErrorValues(info, ii)) {
            if (info.ueNames[ii] != null && info.ueValues[ii] != null) {
              this.write(writer, info.ueNames[ii], info.ueDataTypes[ii], info.ueValues[ii]);
            }
          }
        }
      }
      if (this.isTickLabelAvailable()) {
        for (int ii = 0; ii < info.tlNames.length; ii++) {
          if (info.tlNames[ii] != null && info.tlValues[ii] != null) {
            writer.writeStringArray(info.tlNames[ii], info.tlValues[ii]);
          }
        }
      }
    }
    return true;
  }

  private boolean hasSameErrorValues(SXYExportInfo info, final int index) {
    return (info.leNames[index] != null)
        && (info.ueNames[index] != null)
        && !info.ueNames[index].equals(info.leNames[index]);
  }

  /**
   * Exports to a MATLAB file.
   *
   * @param mode the mode parameter
   * @param policy the policy parameter
   * @param file the MATLAB file
   * @param writer MAT-file writer
   */
  @Override
  protected boolean exportToMATLAB(
      File file, MatFileWriter writer, final SGExportParameter mode, SGDataBufferPolicy policy) {
    SXYExportInfo info = this.exportCommon(mode, policy);
    List<MLArray> mlList = new ArrayList<MLArray>();
    final double[][] xValues = info.xValues;
    final double[][] yValues = info.yValues;
    if (info.xValid) {
      String[] xNames = info.xNames;
      for (int ii = 0; ii < xNames.length; ii++) {
        MLDouble xArray = new MLDouble(xNames[ii], xValues[ii], 1);
        mlList.add(xArray);
      }
    }
    if (info.yValid) {
      String[] yNames = info.yNames;
      for (int ii = 0; ii < yNames.length; ii++) {
        MLDouble yArray = new MLDouble(yNames[ii], yValues[ii], 1);
        mlList.add(yArray);
      }
    }
    try {
      new MatFileWriter(file.getPath(), mlList);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Creates and returns a data buffer.
   *
   * @param param parameters for data buffer
   */
  @Override
  public SGDataBuffer getDataBuffer(SGDataBufferPolicy param) {
    return SGDataBufferUtility.getDataBuffer(this, (SGSXYDataBufferPolicy) param);
  }

  @Override
  public Boolean getDateFlag() {
    // always returns null
    return null;
  }

  @Override
  public boolean hasGenericTickLabels() {
    // always returns true
    return true;
  }

  @Override
  public SGDate[] getDateArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getDateArray(this, policy);
  }

  /**
   * Returns true if this data has at lease one "effective" stride that has the string
   * representation different from "0:end".
   */
  @Override
  public boolean hasEffectiveStride() {
    return SGDataViewerUtility.hasEffectiveStride(this);
  }

  @Override
  protected MDArrayDataType getExportNumberDataType(
      SGMDArrayVariable var, SGExportParameter mode, SGDataBufferPolicy policy) {
    if (SGDataMiscUtility.isArchiveDataSetOperation(mode.getType())) {
      return var.getDataType();
    } else {
      SGSXYDataBufferPolicy sxyPolicy = (SGSXYDataBufferPolicy) policy;
      if (sxyPolicy.isShiftValuesContained()) {
        return var.getExportFloatingNumberDataType();
      } else {
        return var.getDataType();
      }
    }
  }

  @Override
  public double[][] getXValueArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getXValues(this, policy);
  }

  @Override
  public double[][] getYValueArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getYValues(this, policy);
  }

  /**
   * Returns unshifted x-value arrays with given policy.
   *
   * @param policy policy to get values
   */
  @Override
  public double[][] getUnshiftedXValueArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getUnshiftedXValues(this, policy);
  }

  /**
   * Returns unshifted y-value arrays with given policy.
   *
   * @param policy policy to get values
   */
  @Override
  public double[][] getUnshiftedYValueArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getUnshiftedYValues(this, policy);
  }

  /**
   * Returns arrays of lower error values with given policy.
   *
   * @param policy policy to get values
   */
  @Override
  public double[][] getLowerErrorValueArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getLowerErrorValueArray(this, policy);
  }

  /**
   * Returns arrays of upper error values with given policy.
   *
   * @param policy policy to get values
   */
  @Override
  public double[][] getUpperErrorValueArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getUpperErrorValueArray(this, policy);
  }

  /**
   * Returns arrays of tick labels with given policy.
   *
   * @param policy policy to get values
   */
  @Override
  public String[][] getTickLabelArray(SGSXYDataBufferPolicy policy) {
    return SGDataBufferUtility.getTickLabelArray(this, policy);
  }

  @Override
  public SGDate[] getDateArray(boolean all) {
    // always returns null
    return null;
  }

  @Override
  public Boolean isYValuesHolder() {
    if (this.isErrorBarAvailable()) {
      return SGUtility.contains(this.mYVariables, this.mErrorBarHolderVariables);
    } else if (this.isTickLabelAvailable()) {
      return SGUtility.contains(this.mYVariables, this.mTickLabelHolderVariables);
    } else {
      return null;
    }
  }

  @Override
  public boolean hasOneSidedMultipleValues() {
    if (this.isDimensionPicked()) {
      // returns true if only one variable is picked up
      return (this.mPickUpDimensionInfo.getDimensionMap().size() == 1);
    } else {
      // returns true when dimension is not picked
      return true;
    }
  }

  @Override
  protected void getVarNameColumnTypeList(List<String> varList, List<String> columnTypeList) {

    super.getVarNameColumnTypeList(varList, columnTypeList);

    if (this.mXVariables != null) {
      for (int ii = 0; ii < this.mXVariables.length; ii++) {
        String strX = this.getOneDimensionalVarCommandString(this.mXVariables[ii]);
        varList.add(strX);
        columnTypeList.add(X_VALUE);
      }
    }
    if (this.mYVariables != null) {
      for (int ii = 0; ii < this.mYVariables.length; ii++) {
        String strY = this.getOneDimensionalVarCommandString(this.mYVariables[ii]);
        varList.add(strY);
        columnTypeList.add(Y_VALUE);
      }
    }
    if (this.isErrorBarAvailable()) {
      final int num = this.mLowerErrorVariables.length;
      for (int ii = 0; ii < num; ii++) {
        if (this.mLowerErrorVariables[ii] == null) {
          continue;
        }
        SGMDArrayVariable leVar = this.mLowerErrorVariables[ii];
        SGMDArrayVariable ueVar = this.mUpperErrorVariables[ii];
        String leStr = this.getOneDimensionalVarCommandString(leVar);
        String ueStr = this.getOneDimensionalVarCommandString(ueVar);
        String ehName = this.mErrorBarHolderVariables[ii].getName();
        boolean equalFlag = leVar.equals(ueVar);
        if (equalFlag) {
          varList.add(leStr);
          String columnTypeStr =
              SGDataColumnTitleUtility.appendColumnTitle(LOWER_UPPER_ERROR_VALUE, ehName);
          columnTypeList.add(columnTypeStr);
        } else {
          varList.add(leStr);
          String leColumnTypeStr =
              SGDataColumnTitleUtility.appendColumnTitle(LOWER_ERROR_VALUE, ehName);
          columnTypeList.add(leColumnTypeStr);

          varList.add(ueStr);
          String ueColumnTypeStr =
              SGDataColumnTitleUtility.appendColumnTitle(UPPER_ERROR_VALUE, ehName);
          columnTypeList.add(ueColumnTypeStr);
        }
      }
    }
    if (this.isTickLabelAvailable()) {
      for (int ii = 0; ii < this.mTickLabelVariables.length; ii++) {
        SGMDArrayVariable tlVar = this.mTickLabelVariables[ii];
        String tlStr = this.getOneDimensionalVarCommandString(tlVar);
        String thName = this.mTickLabelHolderVariables[ii].getName();
        varList.add(tlStr);
        String columnTypeStr = SGDataColumnTitleUtility.appendColumnTitle(TICK_LABEL, thName);
        columnTypeList.add(columnTypeStr);
      }
    }
  }

  /** Returns a text string for the command of pick up dimension. */
  public String getPickUpDimensionCommandString() {
    if (!this.isDimensionPicked()) {
      return null;
    }
    return this.getDimensionCommandString(KEY_SXY_PICKUP_DIMENSION);
  }

  @Override
  public boolean setArraySectionPropertySub(SGPropertyMap map) {
    if (this.isStrideAvailable()) {
      SGIntegerSeriesSet arraySection = this.getStride();
      if (!arraySection.isComplete()) {
        SGPropertyUtility.addQuotedStringProperty(
            map, COM_DATA_ARRAY_SECTION, arraySection.toString());
      }
      if (this.isTickLabelAvailable()) {
        SGIntegerSeriesSet tickLabelArraySection = this.getTickLabelStride();
        if (!tickLabelArraySection.isComplete()) {
          SGPropertyUtility.addQuotedStringProperty(
              map, COM_DATA_TICK_LABEL_ARRAY_SECTION, tickLabelArraySection.toString());
        }
      }
    }
    return true;
  }

  /**
   * Sets the data source.
   *
   * @param src a data source
   */
  @Override
  public void setDataSource(SGIDataSource src) {
    super.setDataSource(src);
    SGMDArrayFile mdFile = (SGMDArrayFile) src;
    this.mXVariables = this.findVariables(mdFile, this.mXVariables);
    this.mYVariables = this.findVariables(mdFile, this.mYVariables);
    if (this.isErrorBarAvailable()) {
      this.mLowerErrorVariables = this.findVariables(mdFile, this.mLowerErrorVariables);
      this.mUpperErrorVariables = this.findVariables(mdFile, this.mUpperErrorVariables);
      this.mErrorBarHolderVariables = this.findVariables(mdFile, this.mErrorBarHolderVariables);
    }
    if (this.isTickLabelAvailable()) {
      this.mTickLabelVariables = this.findVariables(mdFile, this.mTickLabelVariables);
      this.mTickLabelHolderVariables = this.findVariables(mdFile, this.mTickLabelHolderVariables);
    }
  }

  @Override
  public String getDateFormat() {
    // always returns null
    return null;
  }

  @Override
  public void setDateFormat(String format) {
    // do nothing
  }

  /**
   * Returns the bounds of x-values for all animation frames.
   *
   * @return the bounds of x-values
   */
  @Override
  public SGValueRange getAllAnimationFrameBoundsX() {
    return SGDataRangeUtility.getAllAnimationFrameBoundsX(this);
  }

  /**
   * Returns the bounds of y-values for all animation frames.
   *
   * @return the bounds of y-values
   */
  @Override
  public SGValueRange getAllAnimationFrameBoundsY() {
    return SGDataRangeUtility.getAllAnimationFrameBoundsY(this);
  }

  /**
   * Returns the array of column types.
   *
   * @return the array of column types
   */
  @Override
  public String[] getDataViewerColumnTypes() {
    List<String> list = new ArrayList<String>();
    if (this.mXVariables != null && this.mXVariables.length != 0) {
      list.add(X_VALUE);
    }
    if (this.mYVariables != null && this.mYVariables.length != 0) {
      list.add(Y_VALUE);
    }
    String[] ret = list.toArray(new String[list.size()]);
    return ret;
  }

  /**
   * Returns preferred column type for data viewer.
   *
   * @return preferred column type for data viewer
   */
  @Override
  public String getPreferredDataViewColumnType() {
    return SGDataViewerUtility.getPreferredDataViewColumnType(this);
  }

  /**
   * Returns whether the index is available.
   *
   * @return true if the index is available
   */
  @Override
  public boolean isIndexAvailable() {
    // always returns true
    return true;
  }

  @Override
  public double getXValueAt(final int childIndex, final int arrayIndex) {
    SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
    double[] values = sxyArray[childIndex].getXValueArray(false);
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);
    return values[arrayIndex];
  }

  @Override
  public double getYValueAt(final int childIndex, final int arrayIndex) {
    SGISXYTypeSingleData[] sxyArray = this.getSXYDataArray();
    double[] values = sxyArray[childIndex].getYValueArray(false);
    SGDataMiscUtility.disposeSXYDataArray(sxyArray);
    return values[arrayIndex];
  }

  @Override
  public int getDataViewerColumnNumber(final String columnType, final boolean all) {
    final int num;
    if (Y_VALUE.equals(columnType)) {
      if (this.hasMultipleYValues()) {
        num = this.getChildNumber();
      } else {
        SGMDArrayVariable yVar = this.getYVariable();
        Integer pickUpDimension = yVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          num = yVar.getDimensions()[pickUpDimension];
        } else {
          num = 1;
        }
      }
    } else {
      if (!this.hasMultipleYValues()) {
        num = this.getChildNumber();
      } else {
        SGMDArrayVariable xVar = this.getXVariable();
        Integer pickUpDimension = xVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          num = xVar.getDimensions()[pickUpDimension];
        } else {
          num = 1;
        }
      }
    }
    return num;
  }

  @Override
  public int getDataViewerRowNumber(final String columnType, final boolean all) {
    if (all) {
      return this.getAllPointsNumber();
    } else {
      return this.getPointsNumber();
    }
  }

  @Override
  public SGIntegerSeriesSet getDataViewerColStride(String columnType) {
    return new SGIntegerSeriesSet(0, this.getChildNumber() - 1, 1);
  }

  @Override
  public SGIntegerSeriesSet getDataViewerRowStride(String columnType) {
    return this.mStride;
  }

  @Override
  public void setDataViewerValue(
      final String columnType, final int row, final int col, final Object value) {
    List<SGDataValueHistory> editedDataValueList =
        SGDataViewerUtility.getEditedDataValueList(this, columnType, row, col, value, this.mStride);
    this.mEditedDataValueList.addAll(editedDataValueList);
  }

  @Override
  public SGIntegerSeriesSet getIndexStride() {
    return this.getStride();
  }

  /**
   * Returns a text string of the unit for X values.
   *
   * @return a text string of the unit for X values
   */
  @Override
  public String getUnitsStringX() {
    // always returns null
    return null;
  }

  /**
   * Returns a text string of the unit for Y values.
   *
   * @return a text string of the unit for Y values
   */
  @Override
  public String getUnitsStringY() {
    // always returns null
    return null;
  }

  @Override
  public void setDataValue(SGDataValueHistory value) {
    SGDataViewerUtility.setDataViewerValue(
        this,
        value.getColumnType(),
        value.getRowIndex(),
        value.getColumnIndex(),
        value.getValue(),
        this.mStride);
  }

  @Override
  public boolean hasDateTypeXVariable() {
    // always returns false
    return false;
  }

  @Override
  public boolean hasDateTypeYVariable() {
    // always returns false
    return false;
  }

  @Override
  protected boolean matches(
      final int col, final int row, String columnType, SGDataValueHistory value, final double d) {
    return SGDataViewerUtility.matches(col, row, columnType, value, d);
  }

  private Map<Integer, Double> getEditedValueMap(SGMDArrayVariable var, final boolean dimReversed) {

    String varName = var.getName();
    int[] dims = var.getDimensions();

    Map<Integer, Double> valueMap = new HashMap<Integer, Double>();

    for (int ii = 0; ii < this.mEditedDataValueList.size(); ii++) {
      SGDataValueHistory.MDArray.MD1 dataValue =
          (SGDataValueHistory.MDArray.MD1) this.mEditedDataValueList.get(ii);
      if (!varName.equals(dataValue.getVarName())) {
        continue;
      }

      final int dimension = dataValue.getDimension();
      final int dimIndex = dataValue.getIndex();

      final int pickUpDimension = dataValue.getPickUpDimension();
      final int pickUpDimIndex = dataValue.getPickUpDimIndex();

      int animationDimension = dataValue.getAnimationDimension();
      int animationDimIndex = dataValue.getAnimationDimIndex();

      int[] origins = var.getOrigins();
      if (pickUpDimension != -1 && pickUpDimIndex != -1) {
        origins[pickUpDimension] = pickUpDimIndex;
      }
      if (animationDimension != -1 && animationDimIndex != -1) {
        origins[animationDimension] = animationDimIndex;
      }

      final int[] factors = this.getFactors(dims, dimReversed);
      final int factor = factors[dimension];
      int offset = 0;
      for (int jj = 0; jj < dims.length; jj++) {
        if (jj == dimension) {
          continue;
        }
        offset += factors[jj] * origins[jj];
      }
      final int arrayIndex = factor * dimIndex + offset;
      final double value = dataValue.getValue();

      valueMap.put(arrayIndex, value);
    }

    return valueMap;
  }

  @Override
  protected MDDoubleArray setEditedValues(
      IHDF5Writer writer, SGMDArrayVariable var, MDDoubleArray array) {

    // get edited values
    Map<Integer, Double> valueMap = this.getEditedValueMap(var, true);

    // set to the array
    Iterator<Entry<Integer, Double>> itr = valueMap.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<Integer, Double> entry = itr.next();
      final int arrayIndex = entry.getKey();
      final double value = entry.getValue();
      array.set(value, arrayIndex);
    }

    return array;
  }

  @Override
  protected MLDouble setEditedValues(SGMDArrayVariable var, MLDouble array) {

    int[] dims = var.getDimensions();
    int size = 1;
    for (int jj = 0; jj < dims.length; jj++) {
      size *= dims[jj];
    }

    // copies value
    String varName = var.getName();
    MLDouble ret = new MLDouble(varName, dims);
    for (int jj = 0; jj < size; jj++) {
      final double value = var.getDoubleValue(jj);
      ret.set(value, jj);
    }

    // get edited values
    Map<Integer, Double> valueMap = this.getEditedValueMap(var, false);

    // set to the array
    Iterator<Entry<Integer, Double>> itr = valueMap.entrySet().iterator();
    while (itr.hasNext()) {
      Entry<Integer, Double> entry = itr.next();
      final int arrayIndex = entry.getKey();
      final double value = entry.getValue();
      ret.set(value, arrayIndex);
    }

    return ret;
  }

  @Override
  public double[][] getXValueArray(final boolean all) {
    return SGDataViewerUtility.getXValueArray(this, all);
  }

  @Override
  public double[][] getXValueArray(boolean all, boolean useCache, boolean removeInvalidValues) {
    return SGDataViewerUtility.getXValueArray(this, all, useCache, removeInvalidValues);
  }

  @Override
  public double[][] getXValueArray(boolean all, boolean useCache) {
    return SGDataViewerUtility.getXValueArray(this, all, useCache);
  }

  @Override
  public double[][] getYValueArray(final boolean all) {
    return SGDataViewerUtility.getYValueArray(this, all);
  }

  @Override
  public double[][] getYValueArray(boolean all, boolean useCache, boolean removeInvalidValues) {
    return SGDataViewerUtility.getYValueArray(this, all, useCache, removeInvalidValues);
  }

  @Override
  public double[][] getYValueArray(boolean all, boolean useCache) {
    return SGDataViewerUtility.getYValueArray(this, all, useCache);
  }

  @Override
  public void addSingleDimensionEditedDataValue(SGDataValueHistory dataValue) {
    SGDataValueHistory.MDArray.D1 dValue = (SGDataValueHistory.MDArray.D1) dataValue;
    SGDataValueHistory prev = dValue.getPreviousValue();
    SGDataValueHistory.MDArray.MD1 mdValue;
    if (prev != null) {
      mdValue =
          new SGDataValueHistory.MDArray.MD1(
              dValue.getValue(),
              dValue.getColumnType(),
              0,
              dValue.getIndex(),
              dValue.getVarName(),
              prev.getValue());
    } else {
      mdValue =
          new SGDataValueHistory.MDArray.MD1(
              dValue.getValue(), dValue.getColumnType(), 0, dValue.getIndex(), dValue.getVarName());
    }
    this.mEditedDataValueList.add(mdValue);
  }

  @Override
  public SGTwoDimensionalArrayIndex getDataViewerCell(
      SGTwoDimensionalArrayIndex cell, String columnType, final boolean bStride) {
    SGTwoDimensionalArrayIndex ret = null;
    if (this.hasMultipleYValues()) {
      if (Y_VALUE.equals(columnType)) {
        ret = super.getDataViewerCell(cell, columnType, bStride);
      } else {
        SGMDArrayVariable xVar = this.getXVariable();
        Integer pickUpDimension = xVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          ret = super.getDataViewerCell(cell, columnType, bStride);
        } else {
          ret =
              super.getDataViewerCell(
                  new SGTwoDimensionalArrayIndex(0, cell.getRow()), columnType, bStride);
        }
      }
    } else {
      if (X_VALUE.equals(columnType)) {
        ret = super.getDataViewerCell(cell, columnType, bStride);
      } else {
        SGMDArrayVariable yVar = this.getYVariable();
        Integer pickUpDimension = yVar.getDimensionIndex(KEY_SXY_PICKUP_DIMENSION);
        if (pickUpDimension != null && pickUpDimension != -1) {
          ret = super.getDataViewerCell(cell, columnType, bStride);
        } else {
          ret =
              super.getDataViewerCell(
                  new SGTwoDimensionalArrayIndex(0, cell.getRow()), columnType, bStride);
        }
      }
    }
    return ret;
  }

  /** Restores the cache. */
  @Override
  public void restoreCache() {
    SGDataMiscUtility.restoreCache(this);
  }
}
