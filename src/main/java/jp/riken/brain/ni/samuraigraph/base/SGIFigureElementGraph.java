package jp.riken.brain.ni.samuraigraph.base;

import static jp.riken.brain.ni.samuraigraph.base.SGConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGDrawingElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.base.SGFigureElementConstants.*;
import static jp.riken.brain.ni.samuraigraph.data.SGDataInformationKeyConstants.*;

import java.util.List;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** An object which displays the data objects as a graph. */
public interface SGIFigureElementGraph extends SGIFigureElementForData {

  /** */
  public boolean setAxisElement(SGIFigureElementAxis aElement);

  /**
   * @param data
   */
  public SGAxis getXAxis(SGData data);

  /**
   * @param data
   */
  public SGAxis getYAxis(SGData data);

  /**
   * Create and add Element to the Document object.
   *
   * @param document the document
   * @param elList a list of Element objects
   * @param dataList a list of data
   * @param params the parameters
   */
  public boolean createElementOfData(
      Document document, List<Element> elList, List<SGData> dataList, SGExportParameter params);

  /** */
  public int getSelectedDataNumber();

  /**
   * @param data
   */
  public boolean getVisibleInLegendFlag(SGData data);

  /**
   * Returns an array of data columns of given data.
   *
   * @param data a data
   */
  public SGDataColumnInfo[] getDataColumnInfoArray(SGData data);

  /**
   * Returns information map of given data.
   *
   * @param data a data
   */
  public Map<String, Object> getInfoMap(SGData data);

  /**
   * Show the netCDF data labels.
   *
   * @param fes the string element which is added with text label.
   */
  public String showNetCDFLabels(final SGIFigureElementString fes);

  /**
   * Show labels of the given netCDF data.
   *
   * @param fes
   * @param dataList
   */
  public String showNetCDFLabels(final SGIFigureElementString fes, final List<SGData> dataList);

  /**
   * Update the netCDF data labels.
   *
   * @param fes the string element which is updated with text label.
   */
  public void updateNetCDFLabels(SGIFigureElementString fes);

  /**
   * Hide the netCDF data labels.
   *
   * @param fes the string element which hides text label.
   * @param data
   */
  public void hideNetCDFLabels(final SGIFigureElementString fes, final SGData data);

  /**
   * Hide the netCDF data labels which belongs to focused data.
   *
   * @param fes the string element which hides text label.
   */
  public void hideNetCDFLabelsOfFocusedObjects(final SGIFigureElementString fes);

  /**
   * Update the location of drawing elements of given data.
   *
   * @param data a data
   */
  public boolean updateDrawingElementsLocation(final SGData data);

  public boolean isBarVisible();

  public boolean alignVisibleBars();

  public String getAxisValueString(final int x, final int y);

  public List<SGTwoDimensionalArrayIndex> getSelectedDataIndexList(SGIData data, String columnType);

  public static final String TAG_NAME_GRAPH = "Graph";

  public static final String TAG_NAME_DATA = "Data";

  public static final String KEY_SAMPLING_RATE = "SamplingRate";

  public static final String KEY_POLAR = "Polar";

  public static final String KEY_MAGNITUDE_PER_CM = "MagnitudePerCM";

  public static final String KEY_DIRECTION_INVARIANT = "DirectionInvariant";

  public static final String KEY_NODE_MAP = "NodeMap";

  public static final String KEY_GROUP_NAME = "GroupName";
}
