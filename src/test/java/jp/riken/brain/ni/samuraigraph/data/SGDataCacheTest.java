package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertNotSame;

import org.junit.jupiter.api.Test;

class SGDataCacheTest {

  private static class TestDataCache extends SGDataCache {
    TestDataCache() {
      super();
    }
  }

  @Test
  void cloneProducesIndependentCopy() {
    TestDataCache original = new TestDataCache();
    TestDataCache copy = (TestDataCache) original.clone();
    assertNotSame(original, copy);
  }
}
