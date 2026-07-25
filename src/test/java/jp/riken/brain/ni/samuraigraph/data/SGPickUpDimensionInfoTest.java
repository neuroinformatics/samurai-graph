package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import org.junit.jupiter.api.Test;

class SGPickUpDimensionInfoTest {

  private static class TestPickUpDimensionInfo extends SGPickUpDimensionInfo {
    public TestPickUpDimensionInfo() {
      super();
    }

    public TestPickUpDimensionInfo(SGIntegerSeriesSet indices) {
      super(indices);
    }

    @Override
    public boolean equals(Object obj) {
      return super.equals(obj);
    }
  }

  @Test
  void defaultConstructorHasNullIndices() {
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo();
    assertEquals(null, info.getIndices());
  }

  @Test
  void constructorWithIndices() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo(indices);
    assertEquals(indices, info.getIndices());
  }

  @Test
  void getIndicesReturnsClone() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo(indices);
    assertNotSame(indices, info.getIndices());
  }

  @Test
  void setIndices() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo();
    info.setIndices(indices);
    assertEquals(indices, info.getIndices());
  }

  @Test
  void setIndicesNull() {
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo(new SGIntegerSeriesSet(0, 2, 1));
    info.setIndices(null);
    assertEquals(null, info.getIndices());
  }

  @Test
  void setIndicesCopies() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo();
    info.setIndices(indices);
    assertNotSame(indices, info.getIndices());
  }

  @Test
  void cloneProducesIndependentCopy() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    TestPickUpDimensionInfo original = new TestPickUpDimensionInfo(indices);
    TestPickUpDimensionInfo copy = (TestPickUpDimensionInfo) original.clone();
    assertNotSame(original, copy);
    assertEquals(original.getIndices(), copy.getIndices());
    assertNotSame(original.getIndices(), copy.getIndices());
  }

  @Test
  void equalsSameValues() {
    SGIntegerSeriesSet indices = new SGIntegerSeriesSet(0, 2, 1);
    TestPickUpDimensionInfo a = new TestPickUpDimensionInfo(indices);
    TestPickUpDimensionInfo b = new TestPickUpDimensionInfo(indices);
    assertEquals(a, b);
  }

  @Test
  void equalsWithItself() {
    TestPickUpDimensionInfo info = new TestPickUpDimensionInfo(new SGIntegerSeriesSet(0, 2, 1));
    assertEquals(info, info);
  }
}
