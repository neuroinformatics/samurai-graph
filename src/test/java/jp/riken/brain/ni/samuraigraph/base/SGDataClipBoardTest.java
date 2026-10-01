package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jp.riken.brain.ni.samuraigraph.data.SGDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGNumberDataColumn;
import jp.riken.brain.ni.samuraigraph.data.SGSDArrayFile;
import jp.riken.brain.ni.samuraigraph.data.SGSXYSDArrayData;
import jp.riken.brain.ni.samuraigraph.data.SGSXYZSDArrayData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Characterization tests for the in-memory data clipboard and its DataCopy holder. */
class SGDataClipBoardTest {

  private SGDataClipBoard cb = null;

  private final SGSXYZSDArrayData data =
      new SGSXYZSDArrayData(
          new SGSDArrayFile(
              "dummy.dat",
              new SGDataColumn[] {
                new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0}),
                new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0}),
                new SGNumberDataColumn("z", new double[] {0.5, 1.5, 2.5})
              }),
          new SGDataSourceObserver(),
          0,
          1,
          2,
          null,
          true);

  private final SGSXYSDArrayData otherData =
      new SGSXYSDArrayData(
          new SGSDArrayFile(
              "dummy.dat",
              new SGDataColumn[] {
                new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
                new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0})
              }),
          new SGDataSourceObserver(),
          0,
          1,
          null,
          null,
          null,
          null,
          null,
          null,
          null,
          true,
          null);

  @BeforeEach
  void setUp() {
    this.cb = new SGDataClipBoard();
  }

  @Test
  void theTwoArgumentAddStoresAnIndependentCopyOfTheData() {
    final SGDataClipBoard.DataCopy dc = this.cb.add(this.data, "column a");
    assertEquals("column a", dc.getName());
    // the stored data is a copy, not the original object
    assertNotSame(this.data, dc.getData());
    assertEquals(1, this.cb.getDataList().size());
    assertTrue(dc.getPropertiesMap().isEmpty());
  }

  @Test
  void theThreeArgumentAddDeepCopiesThePropertiesMap() {
    final SGColorMap.ColorMapProperties p = new SGColorMap.ColorMapProperties();
    final Map<Class<?>, SGProperties> map = new HashMap<>();
    map.put(SGColorMap.ColorMapProperties.class, p);
    final SGDataClipBoard.DataCopy dc = this.cb.add(this.data, "column b", map);
    // the stored map is a copy of the argument
    assertNotSame(map, dc.getPropertiesMap());
    // getProperties returns a further copy of the stored instance
    final SGProperties got = dc.getProperties(SGColorMap.ColorMapProperties.class);
    assertNotSame(p, got);
    assertEquals(p, got);
    assertNull(dc.getProperties(SGSXYZSDArrayData.class));
  }

  @Test
  void theGetDataReturnsTheStoredCopyForTheDataItHolds() {
    final SGDataClipBoard.DataCopy dc = this.cb.add(this.data, "column c");
    // the lookup matches the stored copy itself
    assertSame(dc, this.cb.getData(dc.getData()));
    // a different data object does not match
    assertNull(this.cb.getData(this.otherData));
  }

  @Test
  void theGetDataListReturnsACopyIndependentOfTheClipboard() {
    this.cb.add(this.data, "column d");
    final List<SGDataClipBoard.DataCopy> list = this.cb.getDataList();
    list.clear();
    // clearing the returned list does not affect the clipboard
    assertEquals(1, this.cb.getDataList().size());
  }

  @Test
  void theClearRemovesAllCopiedData() {
    this.cb.add(this.data, "column e");
    this.cb.clear();
    assertTrue(this.cb.getDataList().isEmpty());
  }

  @Test
  void theClipboardAcceptsMultipleCopies() {
    this.cb.add(this.data, "first");
    final SGDataClipBoard.DataCopy second = this.cb.add(this.data, "second");
    assertEquals(2, this.cb.getDataList().size());
    assertEquals("second", this.cb.getDataList().get(1).getName());
    assertSame(second, this.cb.getData(second.getData()));
    assertDoesNotThrow(this.cb::clear);
  }
}
