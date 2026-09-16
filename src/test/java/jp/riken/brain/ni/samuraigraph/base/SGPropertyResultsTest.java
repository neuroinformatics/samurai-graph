package jp.riken.brain.ni.samuraigraph.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests of the property results. */
class SGPropertyResultsTest {

  @Test
  void putResultStoresTheStatusAndUppercasesTheKey() {
    final SGPropertyResults results = new SGPropertyResults();
    results.putResult("Key", SGPropertyResults.INVALID_INPUT_VALUE);
    assertEquals(SGPropertyResults.INVALID_INPUT_VALUE, results.getResult("key"));
    assertEquals(SGPropertyResults.INVALID_INPUT_VALUE, results.getResult("KEY"));
    assertEquals("Key", results.getOriginalKey("KEY"));
  }

  @Test
  void putResultMovesTheKeyToTheEndOnRePut() {
    final SGPropertyResults results = new SGPropertyResults();
    results.putResult("a", SGPropertyResults.SUCCEEDED);
    results.putResult("b", SGPropertyResults.SUCCEEDED);
    results.putResult("a", SGPropertyResults.SKIPPED);
    assertEquals(2, countEntries(results));
    assertEquals(java.util.List.of("B", "A"), collectKeys(results));
  }

  @Test
  void removeResultRemovesAndReturnsTheStatus() {
    final SGPropertyResults results = new SGPropertyResults();
    results.putResult("k", SGPropertyResults.SUCCEEDED);
    assertEquals(SGPropertyResults.SUCCEEDED, results.removeResult("K"));
    assertNull(results.getResult("k"));
    assertTrue(countEntries(results) == 0);
  }

  @Test
  void getResultReturnsNullForMissingKey() {
    final SGPropertyResults results = new SGPropertyResults();
    assertNull(results.getResult("missing"));
  }

  @Test
  void getOriginalKeyReturnsTheInsertedCasing() {
    final SGPropertyResults results = new SGPropertyResults();
    results.putResult("foO", SGPropertyResults.SUCCEEDED);
    assertEquals("foO", results.getOriginalKey("FOO"));
    assertNull(results.getOriginalKey("missing"));
  }

  @Test
  void cloneCopiesTheStatusMapIndependently() {
    final SGPropertyResults results = new SGPropertyResults();
    results.putResult("k", SGPropertyResults.SUCCEEDED);
    final SGPropertyResults copy = (SGPropertyResults) results.clone();
    copy.putResult("k", SGPropertyResults.NOT_FOUND);
    assertEquals(SGPropertyResults.SUCCEEDED, results.getResult("k"));
    assertEquals(SGPropertyResults.NOT_FOUND, copy.getResult("k"));
  }

  @Test
  void statusConstantsAreDistinct() {
    assertEquals(0, SGPropertyResults.SUCCEEDED);
    assertEquals(1, SGPropertyResults.INVALID_INPUT_VALUE);
    assertEquals(2, SGPropertyResults.NOT_FOUND);
    assertEquals(3, SGPropertyResults.SKIPPED);
    assertFalse(
        SGPropertyResults.SUCCEEDED == SGPropertyResults.INVALID_INPUT_VALUE
            || SGPropertyResults.SUCCEEDED == SGPropertyResults.NOT_FOUND
            || SGPropertyResults.SUCCEEDED == SGPropertyResults.SKIPPED);
  }

  private static int countEntries(final SGPropertyResults results) {
    return collectKeys(results).size();
  }

  private static java.util.List<String> collectKeys(final SGPropertyResults results) {
    final java.util.List<String> keys = new java.util.ArrayList<String>();
    final java.util.Iterator<String> itr = results.getKeyIterator();
    while (itr.hasNext()) {
      keys.add(itr.next());
    }
    return keys;
  }
}
