package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.Test;

/** Unit tests of the format state copy in {@link SGSXYSDArrayData#clone()}. */
class SGSXYSDArrayDataCloneTest {

  private SGSXYSDArrayData createData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYSDArrayData(
        file,
        new SGDataSourceObserver(),
        0,
        1,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        true,
        null);
  }

  @Test
  void copyCopiesTheFormatStateByValue() {
    SGSXYSDArrayData data = this.createData();
    data.setShift(new SGTuple2d(1.0, 2.0));
    data.setDecimalPlaces(3);
    data.setExponent(4);
    data.setDateFormat("yyyy-MM-dd");
    SGSXYSDArrayData copy = (SGSXYSDArrayData) data.copy();
    SGTuple2d shift = copy.getShift();
    assertEquals(1.0, shift.x, 0.0);
    assertEquals(2.0, shift.y, 0.0);
    assertEquals(3, copy.getDecimalPlaces());
    assertEquals(4, copy.getExponent());
    assertEquals("yyyy-MM-dd", copy.getDateFormat());
    // the copy must not share the state with the source
    assertNotSame(data.getNumberFormat(), copy.getNumberFormat());
    copy.setShift(new SGTuple2d(5.0, 6.0));
    SGTuple2d source = data.getShift();
    assertEquals(1.0, source.x, 0.0);
    assertEquals(2.0, source.y, 0.0);
  }

  @Test
  void copySurvivesDisposalOfTheSource() {
    SGSXYSDArrayData data = this.createData();
    data.setShift(new SGTuple2d(2.0, 3.0));
    SGSXYSDArrayData copy = (SGSXYSDArrayData) data.copy();
    data.dispose();
    SGTuple2d shift = copy.getShift();
    assertEquals(2.0, shift.x, 0.0);
    assertEquals(3.0, shift.y, 0.0);
  }

  @Test
  void disposingTheCopyLeavesTheSourceIntact() {
    SGSXYSDArrayData data = this.createData();
    data.setShift(new SGTuple2d(2.0, 3.0));
    SGSXYSDArrayData copy = (SGSXYSDArrayData) data.copy();
    copy.dispose();
    SGTuple2d shift = data.getShift();
    assertEquals(2.0, shift.x, 0.0);
    assertEquals(3.0, shift.y, 0.0);
  }

  @Test
  void copyIsIndependentOfSourceShiftAfterCopy() {
    SGSXYSDArrayData data = this.createData();
    data.setShift(new SGTuple2d(1.0, 1.0));
    SGSXYSDArrayData copy = (SGSXYSDArrayData) data.copy();
    // mutating the source shift must not affect the previously created copy
    data.setShift(new SGTuple2d(9.0, 9.0));
    SGTuple2d copyShift = copy.getShift();
    assertEquals(1.0, copyShift.x, 0.0);
    assertEquals(1.0, copyShift.y, 0.0);
    assertTrue(copyShift instanceof SGTuple2d);
  }

  @Test
  void setterRejectsNullShift() {
    SGSXYSDArrayData data = this.createData();
    assertThrows(IllegalArgumentException.class, () -> data.setShift(null));
  }
}
