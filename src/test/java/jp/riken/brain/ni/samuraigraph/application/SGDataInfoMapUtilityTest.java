package jp.riken.brain.ni.samuraigraph.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import javax.xml.parsers.DocumentBuilderFactory;
import jp.riken.brain.ni.samuraigraph.base.SGPropertyMap;
import jp.riken.brain.ni.samuraigraph.data.SGDataTypeConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataCommandConstants;
import jp.riken.brain.ni.samuraigraph.data.SGIDataInformationKeyConstants;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** Unit tests for {@link SGDataInfoMapUtility}. */
class SGDataInfoMapUtilityTest {

  @Test
  void createInfoMapFromPropertyMapForSXYData() {
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(SGDataTypeConstants.SXY_DATA, new SGPropertyMap());
    assertNotNull(infoMap);
    assertEquals(
        SGDataTypeConstants.SXY_DATA, infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE));
    assertEquals(Boolean.FALSE, infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE));
    assertEquals(Boolean.FALSE, infoMap.get(SGIDataInformationKeyConstants.KEY_STRIDE_AVAILABLE));
  }

  @Test
  void createInfoMapFromPropertyMapForVXYDataWithPolar() {
    SGPropertyMap map = new SGPropertyMap();
    map.putValue(SGIDataCommandConstants.COM_DATA_POLAR, "true");
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(SGDataTypeConstants.VXY_DATA, map);
    assertNotNull(infoMap);
    assertEquals(Boolean.TRUE, infoMap.get(SGIDataInformationKeyConstants.KEY_VXY_POLAR_SELECTED));
  }

  @Test
  void createInfoMapFromPropertyMapRejectsMissingPolarFlag() {
    assertNull(
        SGDataInfoMapUtility.createInfoMap(SGDataTypeConstants.VXY_DATA, new SGPropertyMap()));
  }

  @Test
  void createInfoMapFromElementForSXYData() throws Exception {
    Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element el = doc.createElement("Data");
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(SGDataTypeConstants.SXY_DATA, el);
    assertNotNull(infoMap);
    assertEquals(
        SGDataTypeConstants.SXY_DATA, infoMap.get(SGIDataInformationKeyConstants.KEY_DATA_TYPE));
    assertFalse(
        ((Boolean) infoMap.get(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE)).booleanValue());
  }

  @Test
  void putSamplingRateAcceptsPositiveValue() {
    SGPropertyMap map = new SGPropertyMap();
    map.putValue(SGIDataCommandConstants.COM_DATA_SAMPLING_RATE, "0.5");
    Map<String, Object> infoMap =
        SGDataInfoMapUtility.createInfoMap(SGDataTypeConstants.SXY_MULTIPLE_DATA, map);
    assertNotNull(infoMap);
    assertTrue(infoMap.containsKey(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE));
    assertEquals(0.5, (Double) infoMap.get(SGIDataInformationKeyConstants.KEY_SAMPLING_RATE), 0.0);
  }
}
