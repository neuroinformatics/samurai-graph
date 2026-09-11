package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataColumnInfo}. */
class SGDataColumnInfoTest {

  /** Minimal concrete subclass for testing the abstract base class. */
  private static class TestColumnInfo extends SGDataColumnInfo {

    TestColumnInfo(String title, String valueType) {
      super(title, valueType);
    }
  }

  @Test
  void constructorStoresTitleAndValueType() {
    SGDataColumnInfo info = new TestColumnInfo("Time", "NUMBER");
    assertEquals("Time", info.getTitle());
    assertEquals("NUMBER", info.getValueType());
  }

  @Test
  void columnTypeIsEmptyUntilSet() {
    SGDataColumnInfo info = new TestColumnInfo("Time", "NUMBER");
    assertEquals("", info.getColumnType());
    info.setColumnType("X_TIME");
    assertEquals("X_TIME", info.getColumnType());
  }

  @Test
  void getNameReturnsTitle() {
    SGDataColumnInfo info = new TestColumnInfo("Time", "NUMBER");
    assertEquals("Time", info.getName());
  }

  @Test
  void attributesAreEmptyByDefault() {
    SGDataColumnInfo info = new TestColumnInfo("Time", "NUMBER");
    assertNotNull(info.getAttributes());
    assertTrue(info.getAttributes().isEmpty());
  }

  @Test
  void equalsComparesDefinition() {
    SGDataColumnInfo a = new TestColumnInfo("Time", "NUMBER");
    SGDataColumnInfo b = new TestColumnInfo("Time", "NUMBER");
    SGDataColumnInfo c = new TestColumnInfo("Temp", "NUMBER");
    assertEquals(a, b);
    assertFalse(a.equals(c));
  }

  @Test
  void cloneProducesEqualCopy() {
    SGDataColumnInfo info = new TestColumnInfo("Time", "NUMBER");
    SGDataColumnInfo copy = (SGDataColumnInfo) info.clone();
    assertEquals(info, copy);
  }

  @Test
  void toStringContainsTitleAndValueType() {
    SGDataColumnInfo info = new TestColumnInfo("Time", "NUMBER");
    String result = info.toString();
    assertTrue(result.contains("title=Time"));
    assertTrue(result.contains("valueType=NUMBER"));
  }
}
