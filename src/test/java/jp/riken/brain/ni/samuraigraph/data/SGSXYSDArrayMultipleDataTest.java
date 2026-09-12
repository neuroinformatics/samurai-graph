package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGIntegerSeriesSet;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import jp.riken.brain.ni.samuraigraph.base.SGTuple2d;
import org.junit.jupiter.api.Test;

/** Unit tests for {@link SGSXYSDArrayMultipleData} built from in-memory columns. */
class SGSXYSDArrayMultipleDataTest {

  private SGSXYSDArrayMultipleData createData(Integer[] xIndices, Integer[] yIndices) {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0}),
      new SGNumberDataColumn("y3", new double[] {13.0, 14.0, 15.0, 16.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYSDArrayMultipleData(
        file,
        new SGDataSourceObserver(),
        xIndices,
        yIndices,
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
  void multipleYValuesData() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1, 2});
    assertEquals(SGDataTypeConstants.SXY_MULTIPLE_DATA, data.getDataType());
    assertEquals(1, data.getXNumber());
    assertEquals(2, data.getYNumber());
    assertEquals(2, data.getChildNumber());
    assertTrue(data.hasMultipleYValues());
    assertFalse(data.hasPairOfXYValues());
  }

  @Test
  void pairOfXYValuesData() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    assertEquals(1, data.getXNumber());
    assertEquals(1, data.getYNumber());
    assertEquals(1, data.getChildNumber());
    assertTrue(data.hasPairOfXYValues());
  }

  @Test
  void isValidIndicesValidatesNullAndRange() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    assertTrue(data.isValidIndices(new Integer[] {0}));
    assertTrue(data.isValidIndices(new Integer[] {3}));
    assertFalse(data.isValidIndices(null));
    assertFalse(data.isValidIndices(new Integer[] {4}));
    assertFalse(data.isValidIndices(new Integer[] {0, null}));
  }

  @Test
  void isValidXYValueIndicesValidatesShape() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    assertTrue(data.isValidXYValueIndices(new Integer[] {0}, new Integer[] {1, 2}));
    assertTrue(data.isValidXYValueIndices(new Integer[] {0, 1}, new Integer[] {2}));
    assertFalse(data.isValidXYValueIndices(null, new Integer[] {1, 2}));
    assertFalse(data.isValidXYValueIndices(new Integer[] {}, new Integer[] {1, 2}));
    assertFalse(data.isValidXYValueIndices(new Integer[] {0, 1}, new Integer[] {2, 3}));
    assertFalse(data.isValidXYValueIndices(new Integer[] {9}, new Integer[] {1, 2}));
  }

  @Test
  void constructorRejectsInvalidXYIndices() {
    assertThrows(
        IllegalArgumentException.class,
        () -> this.createData(new Integer[] {0, 1}, new Integer[] {2, 3}));
    assertThrows(
        IllegalArgumentException.class,
        () -> this.createData(new Integer[] {0}, new Integer[] {4}));
  }

  @Test
  void tickLabelAvailabilityFollowsIndices() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    assertFalse(data.isTickLabelAvailable());
  }

  @Test
  void exponentRoundTrip() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    data.setExponent(2);
    assertEquals(2, data.getExponent());
  }

  @Test
  void shiftIsClonedOnGetAndSet() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    SGTuple2d shift = data.getShift();
    assertEquals(0.0, shift.x, 0.0);
    assertEquals(0.0, shift.y, 0.0);
    data.setShift(new SGTuple2d(3.0, -2.0));
    SGTuple2d got = data.getShift();
    assertEquals(3.0, got.x, 0.0);
    assertEquals(-2.0, got.y, 0.0);
    assertThrows(IllegalArgumentException.class, () -> data.setShift(null));
  }

  private static final SGSXYDataBufferPolicy VALUE_POLICY =
      new SGSXYDataBufferPolicy(false, false, false, false, false);

  @Test
  void errorBarValuesAreReadFromColumns() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("y2", new double[] {9.0, 10.0, 11.0, 12.0}),
      new SGNumberDataColumn("le", new double[] {1.0, 1.0, 1.0, 1.0}),
      new SGNumberDataColumn("ue", new double[] {2.0, 2.0, 2.0, 2.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    SGSXYSDArrayMultipleData data =
        new SGSXYSDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            new Integer[] {0},
            new Integer[] {1, 2},
            new Integer[] {3},
            new Integer[] {4},
            new Integer[] {1},
            null,
            null,
            null,
            null,
            true,
            null);
    assertTrue(data.isErrorBarAvailable());
    double[][] le = data.getLowerErrorValueArray(VALUE_POLICY);
    double[][] ue = data.getUpperErrorValueArray(VALUE_POLICY);
    assertEquals(2, le.length);
    assertEquals(2, ue.length);
    assertEquals(4, le[0].length);
    assertEquals(1.0, le[0][0], 0.0);
    assertEquals(1.0, le[0][3], 0.0);
    assertEquals(2.0, ue[0][0], 0.0);
    assertEquals(2.0, ue[0][3], 0.0);
    assertEquals(2, data.getSXYDataArray().length);
    assertEquals(2, data.getSXYTypeMultipleDataArray().length);
  }

  @Test
  void tickLabelsAreAvailableWithHolder() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("label", new double[] {1.0, 2.0, 3.0, 4.0})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    SGSXYSDArrayMultipleData data =
        new SGSXYSDArrayMultipleData(
            file,
            new SGDataSourceObserver(),
            new Integer[] {0},
            new Integer[] {1},
            null,
            null,
            null,
            new Integer[] {2},
            new Integer[] {0},
            null,
            null,
            true,
            null);
    assertTrue(data.isTickLabelAvailable());
    assertFalse(data.isErrorBarAvailable());
  }

  @Test
  void strideLimitsValueArrays() {
    SGSXYSDArrayMultipleData data =
        new SGSXYSDArrayMultipleData(
            new SGSDArrayFile(
                "dummy.dat",
                new SGDataColumn[] {
                  new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
                  new SGNumberDataColumn("y1", new double[] {5.0, 6.0, 7.0, 8.0})
                }),
            new SGDataSourceObserver(),
            new Integer[] {0},
            new Integer[] {1},
            null,
            null,
            null,
            null,
            null,
            new SGIntegerSeriesSet(0, 3, 2),
            null,
            true,
            null);
    double[][] x = data.getXValueArray(false);
    double[][] y = data.getYValueArray(false);
    assertEquals(1, x.length);
    assertEquals(2, x[0].length);
    assertEquals(1.0, x[0][0], 0.0);
    assertEquals(3.0, x[0][1], 0.0);
    assertEquals(5.0, y[0][0], 0.0);
    assertEquals(7.0, y[0][1], 0.0);
  }

  @Test
  void propertiesRoundTrip() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1, 2});
    SGProperties properties = data.getProperties();
    assertNotNull(properties);
    SGSXYSDArrayMultipleData restored = this.createData(new Integer[] {0}, new Integer[] {1});
    assertTrue(restored.setProperties(properties));
    org.junit.jupiter.api.Assertions.assertArrayEquals(new Integer[] {0}, restored.getXIndices());
    org.junit.jupiter.api.Assertions.assertArrayEquals(
        new Integer[] {1, 2}, restored.getYIndices());
  }

  @Test
  void setStrideRefetchesValues() {
    SGSXYSDArrayMultipleData data = this.createData(new Integer[] {0}, new Integer[] {1});
    data.setStride(new SGIntegerSeriesSet(0, 3, 2));
    double[][] x = data.getXValueArray(false);
    assertEquals(1, x.length);
    assertEquals(2, x[0].length);
    assertEquals(1.0, x[0][0], 0.0);
    assertEquals(3.0, x[0][1], 0.0);
  }
}
