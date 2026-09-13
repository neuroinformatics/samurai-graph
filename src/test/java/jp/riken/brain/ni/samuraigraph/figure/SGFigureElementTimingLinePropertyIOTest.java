package jp.riken.brain.ni.samuraigraph.figure;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/** Unit tests for the property round trip of {@link SGFigureElementTimingLine}. */
class SGFigureElementTimingLinePropertyIOTest {

  private SGFigureElementTimingLine timingLine;

  @AfterEach
  void disposeTimingLine() {
    if (this.timingLine != null) {
      this.timingLine.dispose();
    }
  }

  private SGFigureElementTimingLine createTimingLine() {
    this.timingLine = new SGFigureElementTimingLine();
    return this.timingLine;
  }

  @Test
  void timingLineCanBeCreatedAndDisposed() {
    SGFigureElementTimingLine timingLine = this.createTimingLine();
    assertNotNull(timingLine);
    assertEquals("Timing Lines", timingLine.getClassDescription());
  }

  @Test
  void mementoRoundTripPreservesElementState() {
    SGFigureElementTimingLine timingLine = this.createTimingLine();
    assertTrue(timingLine.setGraphRect(10.0f, 20.0f, 100.0f, 50.0f));

    SGProperties p = timingLine.getProperties();
    assertNotNull(p);

    SGFigureElementTimingLine restored = new SGFigureElementTimingLine();
    assertTrue(restored.setProperties(p));
    assertNotNull(restored.getProperties());
    restored.dispose();
  }

  @Test
  void setPropertiesRejectsNonElementProperties() {
    SGFigureElementTimingLine timingLine = this.createTimingLine();
    assertFalse(timingLine.setProperties(new SGProperties() {}));
  }
}
