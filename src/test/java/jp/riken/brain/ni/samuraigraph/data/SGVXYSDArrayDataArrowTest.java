package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests of the arrow nested in the VXY SD array data. */
class SGVXYSDArrayDataArrowTest {

  private final SGVXYSDArrayData host = new SGVXYSDArrayData();

  private SGVXYSDArrayData.Arrow arrow(double x, double y, double f, double s) {
    return this.host.new Arrow(x, y, f, s);
  }

  @Test
  void equalsMatchesTheSameValues() {
    final SGVXYSDArrayData.Arrow a = this.arrow(1.0, 2.0, 3.0, 4.0);
    final SGVXYSDArrayData.Arrow b = this.arrow(1.0, 2.0, 3.0, 4.0);
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
    assertEquals(a, b);
  }

  @Test
  void equalsDistinguishesEachField() {
    final SGVXYSDArrayData.Arrow a = this.arrow(1.0, 2.0, 3.0, 4.0);
    assertFalse(a.equals(this.arrow(0.0, 2.0, 3.0, 4.0)));
    assertFalse(a.equals(this.arrow(1.0, 0.0, 3.0, 4.0)));
    assertFalse(a.equals(this.arrow(1.0, 2.0, 0.0, 4.0)));
    assertFalse(a.equals(this.arrow(1.0, 2.0, 3.0, 0.0)));
  }

  @Test
  void equalsRejectsOtherTypes() {
    final SGVXYSDArrayData.Arrow a = this.arrow(1.0, 2.0, 3.0, 4.0);
    assertFalse(a.equals(null));
    assertFalse(a.equals("arrow"));
  }
}
