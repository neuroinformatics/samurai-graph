package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/** Unit tests of the data axis information. */
class SGDataAxisInfoTest {

  @Test
  void theConstructorStoresTheFields() {
    final SGAxis axis = new SGAxis(0.0, 1.0);
    final SGValueRange range = new SGValueRange(1.5, 2.5);
    final SGDataAxisInfo info = new SGDataAxisInfo(null, axis, range, "title", 2);
    assertEquals(axis, info.getAxis());
    assertEquals("title", info.getTitle());
    assertEquals(2, info.getLocation());
    assertEquals(1.5, info.getRange().getMinValue());
    assertEquals(2.5, info.getRange().getMaxValue());
  }

  @Test
  void theRangeIsCopied() {
    final SGValueRange range = new SGValueRange(1.5, 2.5);
    final SGDataAxisInfo info = new SGDataAxisInfo(null, null, range, null, 0);
    assertNotSame(range, info.getRange());
    assertEquals(range.getMinValue(), info.getRange().getMinValue());
    assertEquals(range.getMaxValue(), info.getRange().getMaxValue());
  }

  @Test
  void theDataIsReturned() {
    final SGDataAxisInfo info = new SGDataAxisInfo(null, null, new SGValueRange(0.0, 1.0), null, 0);
    assertEquals(null, info.getData());
    assertSame(info.getAxis(), info.getAxis());
  }

  @Test
  void theConstructorRequiresARange() {
    // the range copy constructor dereferences the argument
    assertThrows(NullPointerException.class, () -> new SGDataAxisInfo(null, null, null, null, 0));
  }
}
