package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.concurrent.Callable;
import org.junit.jupiter.api.Test;

/** Unit tests of the async worker. */
class SGAsyncWorkerTest {

  @Test
  void postReturnsTheTaskResult() throws Exception {
    final String result = SGAsyncWorker.post(() -> "hello");
    assertEquals("hello", result);
  }

  @Test
  void postReturnsTheObjectResult() throws Exception {
    final Object value = new Object();
    assertSame(value, SGAsyncWorker.post(() -> value));
  }

  @Test
  void postPropagatesTheTaskException() {
    final IllegalStateException expected = new IllegalStateException("boom");
    final Callable<Object> task =
        () -> {
          throw expected;
        };
    final IllegalStateException thrown =
        assertThrows(IllegalStateException.class, () -> SGAsyncWorker.post(task));
    assertSame(expected, thrown);
  }

  @Test
  void postReturnsNullWhenTheTaskReturnsNull() throws Exception {
    assertNull(SGAsyncWorker.post(() -> null));
  }
}
