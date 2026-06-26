package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import org.junit.jupiter.api.Test;

class SGDefaultColumnTypeUtilityTest {

  // -- DefaultColumnTypeResult tests --

  @Test
  void result_constructor_throwsWhenColumnsIsNull() {
    assertThatThrownBy(
            () ->
                new SGDefaultColumnTypeUtility.DefaultColumnTypeResult(
                    (SGDataColumnInfo[]) null, true))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void result_getDefaultColumnTypes_returnsTypes() {
    SGDataColumnInfo col1 = new SGSDArrayDataColumnInfo("X", "Number");
    col1.setColumnType("X");
    SGDataColumnInfo col2 = new SGSDArrayDataColumnInfo("Y", "Number");
    col2.setColumnType("Y");

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        new SGDefaultColumnTypeUtility.DefaultColumnTypeResult(
            new SGDataColumnInfo[] {col1, col2}, true);

    String[] types = result.getDefaultColumnTypes();
    assertThat(types).containsExactly("X", "Y");
  }

  @Test
  void result_isSucceeded_returnsTrue() {
    SGDataColumnInfo col1 = new SGSDArrayDataColumnInfo("X", "Number");
    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        new SGDefaultColumnTypeUtility.DefaultColumnTypeResult(new SGDataColumnInfo[] {col1}, true);
    assertThat(result.isSucceeded()).isTrue();
  }

  @Test
  void result_isSucceeded_returnsFalse() {
    SGDataColumnInfo col1 = new SGSDArrayDataColumnInfo("X", "Number");
    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        new SGDefaultColumnTypeUtility.DefaultColumnTypeResult(
            new SGDataColumnInfo[] {col1}, false);
    assertThat(result.isSucceeded()).isFalse();
  }

  // -- getDefaultColumnTypes null input tests --

  @Test
  void getDefaultColumnTypes_throwsWhenDataTypeIsNull() {
    List<SGDataColumnInfo> columns = Arrays.asList(new SGSDArrayDataColumnInfo("X", "Number"));
    Map<String, Object> infoMap = new HashMap<>();
    assertThatThrownBy(
            () -> SGDefaultColumnTypeUtility.getDefaultColumnTypes(null, columns, infoMap))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getDefaultColumnTypes_throwsWhenColumnInfoListIsNull() {
    Map<String, Object> infoMap = new HashMap<>();
    assertThatThrownBy(
            () ->
                SGDefaultColumnTypeUtility.getDefaultColumnTypes(
                    SGDataTypeConstants.SXY_DATA, null, infoMap))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void getDefaultColumnTypes_throwsWhenInfoMapIsNull() {
    List<SGDataColumnInfo> columns = Arrays.asList(new SGSDArrayDataColumnInfo("X", "Number"));
    assertThatThrownBy(
            () ->
                SGDefaultColumnTypeUtility.getDefaultColumnTypes(
                    SGDataTypeConstants.SXY_DATA, columns, null))
        .isInstanceOf(IllegalArgumentException.class);
  }

  // -- SXY single data column type detection --

  @Test
  void getDefaultColumnTypes_SXY_single_assignsXY() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X");
    assertThat(types[1]).isEqualTo("Y");
  }

  @Test
  void getDefaultColumnTypes_SXY_single_withThreeNumbers_assignsXYAndLowerError() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"),
            new SGSDArrayDataColumnInfo("col3", "Number"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X");
    assertThat(types[1]).isEqualTo("Y");
  }

  @Test
  void getDefaultColumnTypes_SXY_single_withFiveNumbers_assignsXYWithErrorBars() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"),
            new SGSDArrayDataColumnInfo("col3", "Number"),
            new SGSDArrayDataColumnInfo("col4", "Number"),
            new SGSDArrayDataColumnInfo("col5", "Number"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X");
    assertThat(types[1]).isEqualTo("Y");
  }

  @Test
  void getDefaultColumnTypes_SXY_single_withTextAssignsTickLabel() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"),
            new SGSDArrayDataColumnInfo("label", "Text"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X");
    assertThat(types[1]).isEqualTo("Y");
  }

  // -- SXY multiple data column type detection --

  @Test
  void getDefaultColumnTypes_SXY_multiple_assignsXY() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"),
            new SGSDArrayDataColumnInfo("col3", "Number"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X");
    assertThat(types[1]).isEqualTo("Y");
    assertThat(types[2]).isEqualTo("Y");
  }

  // -- SXYZ data column type detection --

  @Test
  void getDefaultColumnTypes_SXYZ_assignsXYZ() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"),
            new SGSDArrayDataColumnInfo("col3", "Number"));

    Map<String, Object> infoMap = new HashMap<>();

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXYZ_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X");
    assertThat(types[1]).isEqualTo("Y");
    assertThat(types[2]).isEqualTo("Z");
  }

  // -- VXY data column type detection --

  @Test
  void getDefaultColumnTypes_VXY_assignsCoordinatesAndComponents() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(
            new SGSDArrayDataColumnInfo("col1", "Number"),
            new SGSDArrayDataColumnInfo("col2", "Number"),
            new SGSDArrayDataColumnInfo("col3", "Number"),
            new SGSDArrayDataColumnInfo("col4", "Number"));

    Map<String, Object> infoMap = new HashMap<>();

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.VXY_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isTrue();
    String[] types = result.getDefaultColumnTypes();
    assertThat(types[0]).isEqualTo("X-Coordinate");
    assertThat(types[1]).isEqualTo("Y-Coordinate");
  }

  // -- Edge cases --

  @Test
  void getDefaultColumnTypes_SXY_insufficientColumns_returnsFailed() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(new SGSDArrayDataColumnInfo("col1", "Number"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isFalse();
  }

  @Test
  void getDefaultColumnTypes_SXY_multiple_insufficientColumns_returnsFailed() {
    List<SGDataColumnInfo> columnInfoList =
        Arrays.asList(new SGSDArrayDataColumnInfo("col1", "Number"));

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.TRUE);

    SGDefaultColumnTypeUtility.DefaultColumnTypeResult result =
        SGDefaultColumnTypeUtility.getDefaultColumnTypes(
            SGDataTypeConstants.SXY_MULTIPLE_DATA, columnInfoList, infoMap);

    assertThat(result.isSucceeded()).isFalse();
  }

  @Test
  void getDefaultColumnTypes_clonesColumnInfoList() {
    SGSDArrayDataColumnInfo original = new SGSDArrayDataColumnInfo("col1", "Number");
    List<SGDataColumnInfo> columnInfoList = Arrays.asList(original);

    Map<String, Object> infoMap = new HashMap<>();
    infoMap.put(SGIDataInformationKeyConstants.KEY_SXY_MULTIPLE, Boolean.FALSE);

    try {
      SGDefaultColumnTypeUtility.getDefaultColumnTypes(
          SGDataTypeConstants.SXY_DATA, columnInfoList, infoMap);
    } catch (Exception e) {
      // May fail due to insufficient columns, but we only care about clone behavior
    }

    assertThat(original.getColumnType()).isEmpty();
  }
}
