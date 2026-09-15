package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import jp.riken.brain.ni.samuraigraph.base.SGDataSourceObserver;
import jp.riken.brain.ni.samuraigraph.base.SGProperties;
import org.junit.jupiter.api.Test;

/** Property round trip tests of the in-memory single SXY, vector and colormap data. */
class SGDataPropertiesRoundTripTest {

  private SGSXYSDArrayData createSXYData() {
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

  private SGVXYSDArrayData createVXYData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0, 4.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0, 8.0}),
      new SGNumberDataColumn("c1", new double[] {0.1, 0.2, 0.3, 0.4}),
      new SGNumberDataColumn("c2", new double[] {0.4, 0.5, 0.6, 0.7})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGVXYSDArrayData(file, new SGDataSourceObserver(), 0, 1, 2, 3, false, null, true);
  }

  private SGSXYZSDArrayData createSXYZData() {
    SGDataColumn[] columns = {
      new SGNumberDataColumn("x", new double[] {1.0, 2.0, 3.0}),
      new SGNumberDataColumn("y", new double[] {5.0, 6.0, 7.0}),
      new SGNumberDataColumn("z", new double[] {0.5, 1.5, 2.5})
    };
    SGSDArrayFile file = new SGSDArrayFile("dummy.dat", columns);
    return new SGSXYZSDArrayData(file, new SGDataSourceObserver(), 0, 1, 2, null, true);
  }

  @Test
  void sxyDataPropertiesRoundTrip() {
    SGSXYSDArrayData data = this.createSXYData();
    SGProperties p = data.getProperties();
    assertTrue(data.setProperties(data.getProperties()));
    assertNotNull(data.getProperties());
    assertEquals(p.clone(), data.getProperties());
  }

  @Test
  void vxyDataPropertiesRoundTrip() {
    SGVXYSDArrayData data = this.createVXYData();
    SGProperties p = data.getProperties();
    assertNotNull(p);
    assertTrue(data.setProperties(p));
    assertTrue(p.equals(data.getProperties()));
  }

  @Test
  void sxyzDataPropertiesRoundTrip() {
    SGSXYZSDArrayData data = this.createSXYZData();
    SGProperties p = data.getProperties();
    assertNotNull(p);
    assertTrue(p.equals(data.getProperties()));
  }
}
