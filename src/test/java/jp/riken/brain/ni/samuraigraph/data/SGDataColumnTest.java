package jp.riken.brain.ni.samuraigraph.data;

import static org.assertj.core.api.Assertions.assertThat;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import org.junit.jupiter.api.Test;

class SGDataColumnTest {

  // -- appendColumnNo(String, int) --

  @Test
  void appendColumnNo_basic() {
    String result = SGDataUtility.appendColumnNo("X_VALUE", 0);
    assertThat(result).isEqualTo("X_VALUE for No.1");
  }

  @Test
  void appendColumnNo_index1() {
    String result = SGDataUtility.appendColumnNo("Y_VALUE", 1);
    assertThat(result).isEqualTo("Y_VALUE for No.2");
  }

  @Test
  void appendColumnNo_indexZero() {
    String result = SGDataUtility.appendColumnNo("TICK_LABEL", 0);
    assertThat(result).isEqualTo("TICK_LABEL for No.1");
  }

  // -- appendColumnTitle(String, String) --

  @Test
  void appendColumnTitle_basic() {
    String result = SGDataUtility.appendColumnTitle("X_VALUE", "temperature");
    assertThat(result).isEqualTo("X_VALUE for temperature");
  }

  @Test
  void appendColumnTitle_emptyName() {
    String result = SGDataUtility.appendColumnTitle("Y_VALUE", "");
    assertThat(result).isEqualTo("Y_VALUE for ");
  }

  // -- appendColumnNoOrTitle --

  @Test
  void appendColumnNoOrTitle_repeated() {
    String result = SGDataUtility.appendColumnNoOrTitle("Y_VALUE", 2, true, "temp");
    assertThat(result).isEqualTo("Y_VALUE for No.3");
  }

  @Test
  void appendColumnNoOrTitle_notRepeated() {
    String result = SGDataUtility.appendColumnNoOrTitle("Y_VALUE", 2, false, "temp");
    assertThat(result).isEqualTo("Y_VALUE for temp");
  }

  // -- removeHeaderTitle --

  @Test
  void removeHeaderTitle_basic() {
    String result = SGDataUtility.removeHeaderTitle("X_VALUE for temperature");
    assertThat(result).isEqualTo("temperature");
  }

  @Test
  void removeHeaderTitle_noHeader() {
    String result = SGDataUtility.removeHeaderTitle("X_VALUE");
    assertThat(result).isNull();
  }

  @Test
  void removeHeaderTitle_empty() {
    String result = SGDataUtility.removeHeaderTitle("");
    assertThat(result).isNull();
  }

  // -- removeHeaderNo --

  @Test
  void removeHeaderNo_basic() {
    String result = SGDataUtility.removeHeaderNo("X_VALUE for No.3");
    assertThat(result).isEqualTo("3");
  }

  @Test
  void removeHeaderNo_noHeader() {
    String result = SGDataUtility.removeHeaderNo("X_VALUE");
    assertThat(result).isNull();
  }

  // -- getAppendedColumnIndex(String) --

  @Test
  void getAppendedColumnIndex_basic() {
    Integer result = SGDataUtility.getAppendedColumnIndex("X_VALUE for No.1");
    assertThat(result).isEqualTo(0);
  }

  @Test
  void getAppendedColumnIndex_index5() {
    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for No.5");
    assertThat(result).isEqualTo(4);
  }

  @Test
  void getAppendedColumnIndex_noNumber() {
    Integer result = SGDataUtility.getAppendedColumnIndex("X_VALUE for temperature");
    assertThat(result).isNull();
  }

  @Test
  void getAppendedColumnIndex_null() {
    Integer result = SGDataUtility.getAppendedColumnIndex("X_VALUE");
    assertThat(result).isNull();
  }

  @Test
  void getAppendedColumnIndex_empty() {
    Integer result = SGDataUtility.getAppendedColumnIndex("");
    assertThat(result).isNull();
  }

  // -- getAppendedColumnIndex(String, SGDataColumnInfo[]) --

  @Test
  void getAppendedColumnIndexWithColInfo_byTitle() {
    SGSDArrayDataColumnInfo col1 = new SGSDArrayDataColumnInfo("temp", "number");
    SGSDArrayDataColumnInfo col2 = new SGSDArrayDataColumnInfo("pressure", "number");
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {col1, col2};

    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for pressure", colInfo);
    assertThat(result).isEqualTo(1);
  }

  @Test
  void getAppendedColumnIndexWithColInfo_byNumber() {
    SGSDArrayDataColumnInfo col1 = new SGSDArrayDataColumnInfo("col1", "number");
    SGSDArrayDataColumnInfo col2 = new SGSDArrayDataColumnInfo("col2", "number");
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {col1, col2};

    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for No.2", colInfo);
    assertThat(result).isEqualTo(1);
  }

  @Test
  void getAppendedColumnIndexWithColInfo_notFound() {
    SGSDArrayDataColumnInfo col1 = new SGSDArrayDataColumnInfo("temp", "number");
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {col1};

    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for humidity", colInfo);
    assertThat(result).isNull();
  }

  // -- getAppendedColumnIndex(String, String[]) --

  @Test
  void getAppendedColumnIndexWithTitles_match() {
    String[] titles = new String[] {"temp", "pressure", "humidity"};
    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for pressure", titles);
    assertThat(result).isEqualTo(1);
  }

  @Test
  void getAppendedColumnIndexWithTitles_noMatch() {
    String[] titles = new String[] {"temp", "pressure"};
    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for wind", titles);
    assertThat(result).isNull();
  }

  @Test
  void getAppendedColumnIndexWithTitles_fallbackToNumber() {
    String[] titles = new String[] {"temp", "pressure"};
    Integer result = SGDataUtility.getAppendedColumnIndex("Y_VALUE for No.1", titles);
    assertThat(result).isEqualTo(0);
  }

  // -- getColumnIndexOfAppendedColumnTitle(String, SGDataColumnInfo[]) --

  @Test
  void getColumnIndexOfAppendedColumnTitle_match() {
    SGSDArrayDataColumnInfo col1 = new SGSDArrayDataColumnInfo("temp", "number");
    SGSDArrayDataColumnInfo col2 = new SGSDArrayDataColumnInfo("pressure", "number");
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {col1, col2};

    Integer result =
        SGDataUtility.getColumnIndexOfAppendedColumnTitle("Y_VALUE for pressure", colInfo);
    assertThat(result).isEqualTo(1);
  }

  @Test
  void getColumnIndexOfAppendedColumnTitle_noMatch() {
    SGSDArrayDataColumnInfo col1 = new SGSDArrayDataColumnInfo("temp", "number");
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {col1};

    Integer result =
        SGDataUtility.getColumnIndexOfAppendedColumnTitle("Y_VALUE for humidity", colInfo);
    assertThat(result).isNull();
  }

  @Test
  void getColumnIndexOfAppendedColumnTitle_emptyArray() {
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {};
    Integer result = SGDataUtility.getColumnIndexOfAppendedColumnTitle("Y_VALUE for temp", colInfo);
    assertThat(result).isNull();
  }

  // -- getColumnIndexOfAppendedColumnType --

  @Test
  void getColumnIndexOfAppendedColumnType_notNetCDF() {
    SGSDArrayDataColumnInfo col1 = new SGSDArrayDataColumnInfo("col1", "number");
    SGSDArrayDataColumnInfo col2 = new SGSDArrayDataColumnInfo("col2", "number");
    SGDataColumnInfo[] colInfo = new SGDataColumnInfo[] {col1, col2};

    Integer result =
        SGDataUtility.getColumnIndexOfAppendedColumnType(
            "Y_VALUE for No.2", SGDataTypeConstants.SXY_DATA, colInfo);
    assertThat(result).isEqualTo(1);
  }

  // -- getMinimumNumberColumns --

  @Test
  void getMinimumNumberColumns_sxy() {
    int result = SGDataUtility.getMinimumNumberColumns(SGDataTypeConstants.SXY_DATA);
    assertThat(result).isGreaterThan(0);
  }

  @Test
  void getMinimumNumberColumns_vxy() {
    int result = SGDataUtility.getMinimumNumberColumns(SGDataTypeConstants.VXY_DATA);
    assertThat(result).isGreaterThan(0);
  }

  @Test
  void getMinimumNumberColumns_sxyz() {
    int result = SGDataUtility.getMinimumNumberColumns(SGDataTypeConstants.SXYZ_DATA);
    assertThat(result).isGreaterThan(0);
  }

  // -- checkDataColumns --

  @Test
  void checkDataColumns_validSXY() {
    SGSDArrayDataColumnInfo xCol = new SGSDArrayDataColumnInfo("x", "number");
    xCol.setColumnType("X");
    SGSDArrayDataColumnInfo yCol = new SGSDArrayDataColumnInfo("y", "number");
    yCol.setColumnType("Y");
    SGDataColumnInfo[] cols = new SGDataColumnInfo[] {xCol, yCol};

    boolean result = SGDataUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, cols, null);
    assertThat(result).isTrue();
  }

  @Test
  void checkDataColumns_missingX() {
    SGSDArrayDataColumnInfo yCol = new SGSDArrayDataColumnInfo("y", "number");
    yCol.setColumnType("Y");
    SGDataColumnInfo[] cols = new SGDataColumnInfo[] {yCol};

    boolean result = SGDataUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, cols, null);
    assertThat(result).isFalse();
  }

  @Test
  void checkDataColumns_missingY() {
    SGSDArrayDataColumnInfo xCol = new SGSDArrayDataColumnInfo("x", "number");
    xCol.setColumnType("X");
    SGDataColumnInfo[] cols = new SGDataColumnInfo[] {xCol};

    boolean result = SGDataUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, cols, null);
    assertThat(result).isFalse();
  }

  @Test
  void checkDataColumns_multipleY() {
    SGSDArrayDataColumnInfo xCol = new SGSDArrayDataColumnInfo("x", "number");
    xCol.setColumnType("X");
    SGSDArrayDataColumnInfo y1 = new SGSDArrayDataColumnInfo("y1", "number");
    y1.setColumnType("Y");
    SGSDArrayDataColumnInfo y2 = new SGSDArrayDataColumnInfo("y2", "number");
    y2.setColumnType("Y");
    SGDataColumnInfo[] cols = new SGDataColumnInfo[] {xCol, y1, y2};

    boolean result = SGDataUtility.checkDataColumns(SGDataTypeConstants.SXY_DATA, cols, null);
    assertThat(result).isTrue();
  }
}
