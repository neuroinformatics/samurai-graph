package jp.riken.brain.ni.samuraigraph.application;

import static jp.riken.brain.ni.samuraigraph.application.SGApplicationCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGApplicationTextConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGArchiveFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGImageConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGPreferencesConstants.*;
import static jp.riken.brain.ni.samuraigraph.application.SGUpgradeConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGPropertyFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGRootObjectConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataColumnTypeConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataCommandConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataFileConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGNetCDFConstants.*;
import static jp.riken.brain.ni.samuraigraph.figure.SGAxisConstants.*;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDrawingWindow;
import jp.riken.brain.ni.samuraigraph.base.SGFigure;
import jp.riken.brain.ni.samuraigraph.base.SGIFigureElementGraph;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeries;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2f;
import jp.riken.brain.ni.samuraigraph.base.SGUtilityText;
import jp.riken.brain.ni.samuraigraph.data.SGDataDataTypeUtility;
import jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataPropertyKeyConstants;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/** Static helper for data information map construction. */
final class SGDataInfoMapUtility {

  private SGDataInfoMapUtility() {}

  /**
   * Put sampling rate value to the information map.
   *
   * @return false if d <= 0.0. Otherwise true.
   */
  private static boolean putSamplingRate(Map<String, Object> infoMap, Double d) {
    if (d.doubleValue() <= 0.0) {
      return false;
    }
    infoMap.put(SGDataInformationKeyConstants.KEY_SAMPLING_RATE, d);
    return true;
  }

  /**
   * Put sampling rate to the information map.
   *
   * @return false if str is not double or less than 0. Otherwise true.
   */
  private static boolean putSamplingRate(Map<String, Object> infoMap, String str) {
    Double d = SGUtilityText.getDouble(str);
    if (d == null) {
      return false;
    }
    return putSamplingRate(infoMap, d);
  }

  /**
   * Create a map of information for each data type.
   *
   * <p>from Data command.
   *
   * @param dataType the type of data
   * @param map a map of properties
   * @return a map of information
   */
  static Map<String, Object> createInfoMap(String dataType, SGPropertyMap map) {
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, dataType);

    if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      String str = null;
      Iterator<String> itr = map.getKeyIterator();
      while (itr.hasNext()) {
        String key = itr.next();
        if (COM_DATA_POLAR.equalsIgnoreCase(key)) {
          str = map.getValueString(key);
          break;
        }
      }
      if (str == null) {
        return null;
      }
      Boolean b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return null;
      }
      infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, b);
    }

    final boolean multiple = SGDataDataTypeUtility.isMultipleData(dataType);
    infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.valueOf(multiple));
    if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
      if (SGDataDataTypeUtility.isNetCDFDimensionData(dataType)) {
        // only for backward compatibility
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
        Integer start = SGUtilityText.getInteger(map.getValue(COM_DATA_PICKUP_START));
        if (start == null) {
          return null;
        }
        Integer end = SGUtilityText.getInteger(map.getValue(COM_DATA_PICKUP_END));
        if (end == null) {
          return null;
        }
        Integer step = SGUtilityText.getInteger(map.getValue(COM_DATA_PICKUP_STEP));
        if (step == null) {
          return null;
        }
        if (!SGIntegerSeries.isValidSeries(start, end, step)) {
          return null;
        }
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START, start);
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END, end);
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP, step);
      } else {
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
      }
    } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {
      infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
    }

    // add the sampling rate to the infoList
    if (dataType.equalsIgnoreCase(SGDataTypeConstants.SXY_SAMPLING_DATA)
        || dataType.equalsIgnoreCase(SGDataTypeConstants.SXY_MULTIPLE_DATA)) {
      String str = null;
      Iterator<String> itr = map.getKeyIterator();
      while (itr.hasNext()) {
        String key = itr.next();
        if (COM_DATA_SAMPLING_RATE.equalsIgnoreCase(key)) {
          str = map.getValueString(key);
          break;
        }
      }
      if (str != null) {
        if (putSamplingRate(infoMap, str) == false) {
          return null;
        }
        if (dataType.equalsIgnoreCase(SGDataTypeConstants.SXY_SAMPLING_DATA)) {
          infoMap.put(
              SGDataInformationKeyConstants.KEY_DATA_TYPE, SGDataTypeConstants.SXY_MULTIPLE_DATA);
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, false);
        }
      }
    }

    // stride of data arrays
    getDataStrideAvailable(infoMap);

    return infoMap;
  }

  /**
   * Create a map of information for each data type.
   *
   * <p>from property file.
   *
   * @param dataType the type of data
   * @param el an Element object
   * @return a map of information
   */
  static Map<String, Object> createInfoMap(String dataType, Element el) {

    String str = null;
    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, dataType);

    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      final boolean multiple = SGDataDataTypeUtility.isMultipleData(dataType);
      infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.valueOf(multiple));

      // picked up dimension
      SGIntegerSeriesSet pickUpIndices = null;
      str = el.getAttribute(SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION_INDICES);
      if (str != null && str.length() != 0) {
        Map<String, Integer> aliasMap = new HashMap<String, Integer>();
        aliasMap.put(SGIntegerSeries.ARRAY_INDEX_END, null);
        pickUpIndices = SGIntegerSeriesSet.parse(str, aliasMap);
        if (pickUpIndices != null) {
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_INDICES, pickUpIndices);
        }
      } else {
        str = el.getAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_START);
        if (null != str && str.length() != 0) {
          Integer num = SGUtilityText.getInteger(str.trim());
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_START, num);
        }
        str = el.getAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_END);
        if (null != str && str.length() != 0) {
          Integer num = SGUtilityText.getInteger(str.trim());
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_END, num);
        }
        str = el.getAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_STEP);
        if (null != str && str.length() != 0) {
          Integer num = SGUtilityText.getInteger(str.trim());
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_PICKUP_DIMENSION_STEP, num);
        }
      }

      if (SGDataDataTypeUtility.isSDArrayData(dataType)) {
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
      } else if (SGDataDataTypeUtility.isNetCDFData(dataType)) {
        if (SGDataDataTypeUtility.isNetCDFDimensionData(dataType)) {
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
        } else {
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.TRUE);
        }
        str = el.getAttribute(SGDataPropertyKeyConstants.KEY_PICKUP_DIMENSION_NAME);
        if (null != str && str.length() != 0) {
          infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, Boolean.FALSE);
        }

      } else if (SGDataDataTypeUtility.isMDArrayData(dataType)) {

        String dimensionIndexMapStr =
            el.getAttribute(SGDataPropertyKeyConstants.KEY_PICK_UP_DIMENSION);
        if (dimensionIndexMapStr != null && dimensionIndexMapStr.length() != 0) {
          Map<String, Integer> dimensionIndexMap = new HashMap<String, Integer>();
          String[] tokens = dimensionIndexMapStr.split(",");
          for (String token : tokens) {
            String[] array = token.split(":");
            if (array.length != 2) {
              return null;
            }
            String name = array[0].trim();
            String indexStr = array[1].trim();
            Integer index = SGUtilityText.getInteger(indexStr);
            if (index == null) {
              return null;
            }
            dimensionIndexMap.put(name, index);
          }
          infoMap.put(
              SGDataInformationKeyConstants.KEY_SXY_MDARRAY_PICKUP_DIMENSION_INDEX_MAP,
              dimensionIndexMap);
        }
        final boolean multipleVar =
            (dimensionIndexMapStr == null
                || dimensionIndexMapStr.length() == 0
                || pickUpIndices == null);
        infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE, multipleVar);
      }

    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      str = el.getAttribute(SGIFigureElementGraph.KEY_POLAR);
      if (str.length() == 0) {
        return null;
      }
      Boolean b = SGUtilityText.getBoolean(str);
      if (b == null) {
        return null;
      }
      infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, b);
    }

    String timeIndexMapStr = el.getAttribute(SGDataPropertyKeyConstants.KEY_ANIMATION_DIMENSION);
    if (timeIndexMapStr != null && timeIndexMapStr.length() != 0) {
      Map<String, Integer> timeIndexMap = new HashMap<String, Integer>();
      String[] tokens = timeIndexMapStr.split(",");
      for (String token : tokens) {
        String[] array = token.split(":");
        if (array.length != 2) {
          return null;
        }
        String name = array[0].trim();
        String indexStr = array[1].trim();
        Integer index = SGUtilityText.getInteger(indexStr);
        if (index == null) {
          return null;
        }
        timeIndexMap.put(name, index);
      }
      infoMap.put(SGDataInformationKeyConstants.KEY_TIME_DIMENSION_INDEX_MAP, timeIndexMap);
    }

    // add the flag of availability of stride
    Boolean strideAvailable = null;
    str = el.getAttribute(SGDataPropertyKeyConstants.KEY_ARRAY_SECTION_AVAILABLE);
    if (str != null && str.length() != 0) {
      strideAvailable = SGUtilityText.getBoolean(str);
    }
    if (strideAvailable != null) {
      infoMap.put(SGDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, strideAvailable);
    } else {
      // get from the preferences
      getDataStrideAvailable(infoMap);
    }

    // add the stride for each data type
    Map<String, Integer> aliasMap = new HashMap<String, Integer>();
    aliasMap.put(SGIntegerSeries.ARRAY_INDEX_END, null);
    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      String strideKey =
          SGDataDataTypeUtility.isSDArrayData(dataType)
              ? SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE
              : SGDataInformationKeyConstants.KEY_SXY_STRIDE;
      updateStrideInfo(
          el, SGDataPropertyKeyConstants.KEY_ARRAY_SECTION, strideKey, infoMap, aliasMap);

      // tick label
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_TICK_LABEL_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXY_TICK_LABEL_STRIDE,
          infoMap,
          aliasMap);

      // index
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE,
          infoMap,
          aliasMap);
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_SERIAL_NUMBER_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXY_INDEX_STRIDE,
          infoMap,
          aliasMap);
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      // x-direction
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_X_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_VXY_STRIDE_X,
          infoMap,
          aliasMap);

      // y-direction
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_Y_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_VXY_STRIDE_Y,
          infoMap,
          aliasMap);

      // index
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE,
          infoMap,
          aliasMap);
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE,
          infoMap,
          aliasMap);
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_SERIAL_NUMBER_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_VXY_INDEX_STRIDE,
          infoMap,
          aliasMap);
    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      // x-direction
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_X_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_X,
          infoMap,
          aliasMap);

      // y-direction
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_Y_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXYZ_STRIDE_Y,
          infoMap,
          aliasMap);

      // index
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE,
          infoMap,
          aliasMap);
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_INDEX_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE,
          infoMap,
          aliasMap);
      updateStrideInfo(
          el,
          SGDataPropertyKeyConstants.KEY_SERIAL_NUMBER_ARRAY_SECTION,
          SGDataInformationKeyConstants.KEY_SXYZ_INDEX_STRIDE,
          infoMap,
          aliasMap);
    }

    // add the sampling rate
    str = el.getAttribute(SGIFigureElementGraph.KEY_SAMPLING_RATE);
    if (str != null && str.length() != 0) {
      putSamplingRate(infoMap, str);
    }

    // add the node map
    NamedNodeMap nodeMap = el.getAttributes();
    infoMap.put(SGIFigureElementGraph.KEY_NODE_MAP, nodeMap);

    return infoMap;
  }

  /**
   * Create a map of information for each data type.
   *
   * @param figureID the figureID parameter
   * @param pos the pos parameter
   * @param dataType the type of data
   * @param dg a wizard dialog to select data type
   * @return a map of information
   */
  static Map<String, Object> createInfoMap(
      String dataType, SGDataTypeWizardDialog dg, int figureID, Point pos) {

    Map<String, Object> infoMap = new HashMap<String, Object>();
    infoMap.put(SGDataInformationKeyConstants.KEY_DATA_TYPE, dataType);

    if (SGDataDataTypeUtility.isSXYTypeData(dataType)) {
      final Boolean multiple = dg.isMultipleSelected();
      infoMap.put(SGDataInformationKeyConstants.KEY_SXY_MULTIPLE, multiple);
      if (SGDataDataTypeUtility.isNetCDFData(dataType)
          || SGDataDataTypeUtility.isMDArrayData(dataType)) {
        final boolean multipleVariable = true;
        infoMap.put(
            SGDataInformationKeyConstants.KEY_SXY_MULTIPLE_VARIABLE,
            Boolean.valueOf(multipleVariable));
      } else if (SGDataDataTypeUtility.isSDArrayData(dataType)
          && SGDataDataTypeUtility.isSXYTypeData(dataType)) {
        // add the sampling rate to the infoList
        Double d = dg.getSamplingRate();
        if (d != null) {
          putSamplingRate(infoMap, d);
        }
      }
    } else if (SGDataDataTypeUtility.isSXYZTypeData(dataType)) {
      //        	// set true by default
    } else if (SGDataDataTypeUtility.isVXYTypeData(dataType)) {
      boolean b = dg.isPolarSelected();
      infoMap.put(SGDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED, Boolean.valueOf(b));

      //        	// set true by default
    }

    // figure size
    SGDrawingWindow wnd = dg.getOwnerWindow();
    SGFigure figure = wnd.getFigure(figureID);
    SGTuple2f size = null;
    if (figure != null) {
      size = new SGTuple2f(figure.getGraphRectWidth(), figure.getGraphRectHeight());
    } else {
      size = SGMainFunctions.getDefaultFigureSize(wnd, pos, dataType);
    }
    infoMap.put(SGDataInformationKeyConstants.KEY_FIGURE_SIZE, size);

    // stride of data arrays
    getDataStrideAvailable(infoMap);

    return infoMap;
  }

  private static void updateStrideInfo(
      Element el,
      String propertyKey,
      String infoKey,
      Map<String, Object> infoMap,
      Map<String, Integer> aliasMap) {
    String str = el.getAttribute(propertyKey);
    if (str != null && str.length() != 0) {
      SGIntegerSeriesSet stride = SGIntegerSeriesSet.parse(str, aliasMap);
      if (stride != null) {
        infoMap.put(infoKey, stride);
      }
    }
  }

  static void putDataStrideAvailable(final boolean b) {
    /*
    Preferences pref = Preferences.userNodeForPackage(SGMainFunctions.class);
    pref.putBoolean(PREF_KEY_DATA_STRIDE_AVAILABLE, b);
    */
    // do nothing
  }

  static void getDataStrideAvailable(Map<String, Object> infoMap) {

    // sets false by default
    infoMap.put(SGDataInformationKeyConstants.KEY_STRIDE_AVAILABLE, false);
  }
}
