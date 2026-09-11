package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import jp.riken.brain.ni.samuraigraph.base.SGDataColumnInfo;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataColumnTitleUtility}. */
class SGDataColumnTitleUtilityTest {

  /** Minimal concrete subclass for testing column-title helpers. */
  private static class TestColumnInfo extends SGDataColumnInfo {

    TestColumnInfo(String title, String valueType) {
      super(title, valueType);
    }
  }

  @Test
  void appendColumnNoAppendsOneBasedIndex() {
    assertEquals("SXY for No.1", SGDataColumnTitleUtility.appendColumnNo("SXY", 0));
    assertEquals("SXY for No.3", SGDataColumnTitleUtility.appendColumnNo("SXY", 2));
  }

  @Test
  void appendColumnTitleAppendsVariableName() {
    assertEquals("SXY for x", SGDataColumnTitleUtility.appendColumnTitle("SXY", "x"));
  }

  @Test
  void appendColumnNoOrTitleChoosesByFlag() {
    assertEquals(
        "SXY for No.2", SGDataColumnTitleUtility.appendColumnNoOrTitle("SXY", 1, true, "ignored"));
    assertEquals(
        "SXY for title", SGDataColumnTitleUtility.appendColumnNoOrTitle("SXY", 0, false, "title"));
  }

  @Test
  void removeHeaderTitleExtractsTitle() {
    assertEquals("x", SGDataColumnTitleUtility.removeHeaderTitle("SXY for x"));
    assertNull(SGDataColumnTitleUtility.removeHeaderTitle("SXY"));
  }

  @Test
  void removeHeaderNoExtractsNumber() {
    assertEquals("3", SGDataColumnTitleUtility.removeHeaderNo("SXY for No.3"));
    assertNull(SGDataColumnTitleUtility.removeHeaderNo("SXY for x"));
  }

  @Test
  void appendColumnTitleWithIndexUsesColumnTitle() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals("SXY for x", SGDataColumnTitleUtility.appendColumnTitle("SXY", 0, colInfo));
  }

  @Test
  void appendColumnTypeAppendsTitleForNetCDFOrMDArrayData() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        "SXY for x", SGDataColumnTitleUtility.appendColumnType("SXY", 0, true, colInfo, false));
  }

  @Test
  void appendColumnTypeAppendsNumberOrTitleForOtherData() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        "SXY for No.1", SGDataColumnTitleUtility.appendColumnType("SXY", 0, false, colInfo, true));
    assertEquals(
        "SXY for x", SGDataColumnTitleUtility.appendColumnType("SXY", 0, false, colInfo, false));
  }

  @Test
  void getAppendedColumnIndexReturnsZeroBasedIndex() {
    assertEquals(
        Integer.valueOf(2), SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for No.3"));
    assertNull(SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for x"));
  }

  @Test
  void getAppendedColumnIndexWithTitlesFindsByTitle() {
    String[] titles = {"x", "y"};
    assertEquals(
        Integer.valueOf(1), SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for y", titles));
    assertNull(SGDataColumnTitleUtility.getAppendedColumnIndex("SXY for z", titles));
  }

  @Test
  void getColumnIndexOfAppendedColumnTitleFindsByTitle() {
    SGDataColumnInfo[] colInfo = {
      new TestColumnInfo("x", "NUMBER"), new TestColumnInfo("y", "NUMBER")
    };
    assertEquals(
        Integer.valueOf(1),
        SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle("SXY for y", colInfo));
    assertNull(SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle("SXY for z", colInfo));
    assertNull(SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnTitle("SXY", colInfo));
  }

  @Test
  void getColumnIndexOfAppendedColumnTypeUsesTitleForNetCDF() {
    SGDataColumnInfo[] colInfo = {new TestColumnInfo("x", "NUMBER")};
    assertEquals(
        Integer.valueOf(0),
        SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnType(
            "SXY for x", SGDataTypeConstants.SXY_NETCDF_DATA, colInfo));
    assertEquals(
        Integer.valueOf(0),
        SGDataColumnTitleUtility.getColumnIndexOfAppendedColumnType(
            "SXY for No.1", SGDataTypeConstants.SXY_DATA, colInfo));
  }
}
