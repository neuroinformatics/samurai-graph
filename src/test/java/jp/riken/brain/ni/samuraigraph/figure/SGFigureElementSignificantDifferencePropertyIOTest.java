package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementSignificantDifference}. */
class SGFigureElementSignificantDifferencePropertyIOTest {

  private SGFigureElementSignificantDifference difference;

  @AfterEach
  void disposeDifference() {
    if (this.difference != null) {
      this.difference.dispose();
    }
  }

  private SGFigureElementSignificantDifference createDifference() {
    this.difference = new SGFigureElementSignificantDifference();
    return this.difference;
  }

  @Test
  void significantDifferenceCanBeCreatedAndDisposed() {
    SGFigureElementSignificantDifference difference = this.createDifference();
    assertNotNull(difference);
    assertEquals("Significant Difference Symbols", difference.getClassDescription());
  }

  @Test
  void mementoRoundTripPreservesElementState() {
    SGFigureElementSignificantDifference difference = this.createDifference();
    assertTrue(difference.setGraphRect(10.0f, 20.0f, 100.0f, 50.0f));

    SGProperties p = difference.getProperties();
    assertNotNull(p);

    SGFigureElementSignificantDifference restored = new SGFigureElementSignificantDifference();
    assertTrue(restored.setProperties(p));
    assertNotNull(restored.getProperties());
    restored.dispose();
  }

  @Test
  void setPropertiesRejectsNonElementProperties() {
    SGFigureElementSignificantDifference difference = this.createDifference();
    assertFalse(difference.setProperties(new SGProperties() {}));
  }
}
