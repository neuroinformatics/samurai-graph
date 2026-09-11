package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGDataColumnInfoSet}. */
class SGDataColumnInfoSetTest {

  /** Minimal concrete subclass for testing the abstract base class. */
  private static class TestColumnInfo extends SGDataColumnInfo {

    TestColumnInfo(String title, String valueType) {
      super(title, valueType);
    }
  }

  @Test
  void constructorStoresColumnInfoArray() {
    SGDataColumnInfo[] array = {
      new TestColumnInfo("Time", "NUMBER"), new TestColumnInfo("Value", "NUMBER")
    };
    SGDataColumnInfoSet set = new SGDataColumnInfoSet(array);
    SGDataColumnInfo[] stored = set.getDataColumnInfoArray();
    assertNotNull(stored);
    assertEquals(2, stored.length);
    assertEquals("Time", stored[0].getTitle());
    assertEquals("Value", stored[1].getTitle());
  }

  @Test
  void cloneProducesEqualCopy() {
    SGDataColumnInfo[] array = {new TestColumnInfo("Time", "NUMBER")};
    SGDataColumnInfoSet set = new SGDataColumnInfoSet(array);
    SGDataColumnInfoSet copy = (SGDataColumnInfoSet) set.clone();
    assertEquals(1, copy.getDataColumnInfoArray().length);
    assertEquals("Time", copy.getDataColumnInfoArray()[0].getTitle());
  }
}
