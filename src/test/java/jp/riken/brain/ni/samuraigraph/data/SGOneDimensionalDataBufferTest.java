package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SGOneDimensionalDataBufferTest {

  private static class TestOneDimBuffer extends SGOneDimensionalDataBuffer {
    TestOneDimBuffer(int length) {
      super();
      this.mLength = length;
    }

    @Override
    public String getDataType() {
      return "test";
    }
  }

  @Test
  void getLength() {
    TestOneDimBuffer buf = new TestOneDimBuffer(5);
    assertEquals(5, buf.getLength());
  }

  @Test
  void isGridTypeReturnsFalse() {
    TestOneDimBuffer buf = new TestOneDimBuffer(3);
    assertEquals(false, buf.isGridType());
  }

  @Test
  void getGridTypeKeyReturnsNull() {
    TestOneDimBuffer buf = new TestOneDimBuffer(3);
    assertNull(buf.getGridTypeKey());
  }
}
