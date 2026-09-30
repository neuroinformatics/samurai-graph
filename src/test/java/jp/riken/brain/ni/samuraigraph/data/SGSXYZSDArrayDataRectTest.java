package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/** Unit tests of the rect nested in the SXYZ SD array data. */
class SGSXYZSDArrayDataRectTest {

  private final SGSXYZSDArrayData host = new SGSXYZSDArrayData();

  private SGSXYZSDArrayData.Rect rect(double x, double y, double z) {
    return this.host.new Rect(x, y, z);
  }

  @Test
  void equalsMatchesTheSameValues() {
    final SGSXYZSDArrayData.Rect a = this.rect(1.0, 2.0, 3.0);
    final SGSXYZSDArrayData.Rect b = this.rect(1.0, 2.0, 3.0);
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
    assertEquals(a, b);
  }

  @Test
  void equalsDistinguishesEachField() {
    final SGSXYZSDArrayData.Rect a = this.rect(1.0, 2.0, 3.0);
    assertFalse(a.equals(this.rect(0.0, 2.0, 3.0)));
    assertFalse(a.equals(this.rect(1.0, 0.0, 3.0)));
    assertFalse(a.equals(this.rect(1.0, 2.0, 0.0)));
  }

  @Test
  void equalsRejectsOtherTypes() {
    final SGSXYZSDArrayData.Rect a = this.rect(1.0, 2.0, 3.0);
    assertFalse(a.equals(null));
    assertFalse(a.equals("rect"));
  }
}
