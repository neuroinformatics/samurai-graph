package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import ucar.nc2.Dimension;

class SGDimensionInfoTest {

  private Dimension createMockDimension(String name, int length) {
    Dimension dim = mock(Dimension.class);
    when(dim.getShortName()).thenReturn(name);
    when(dim.getLength()).thenReturn(length);
    return dim;
  }

  @Test
  void constructorWithDimension() {
    Dimension dim = createMockDimension("lat", 10);
    SGDimensionInfo info = new SGDimensionInfo(dim);
    assertEquals("lat", info.getName());
    assertEquals(10, info.getLength());
  }

  @Test
  void equalsSameValues() {
    Dimension dim = createMockDimension("lat", 10);
    SGDimensionInfo a = new SGDimensionInfo(dim);
    SGDimensionInfo b = new SGDimensionInfo(dim);
    assertEquals(a, b);
  }

  @Test
  void equalsDifferentName() {
    SGDimensionInfo a = new SGDimensionInfo(createMockDimension("lat", 10));
    SGDimensionInfo b = new SGDimensionInfo(createMockDimension("lon", 10));
    assertNotEquals(a, b);
  }

  @Test
  void equalsDifferentLength() {
    SGDimensionInfo a = new SGDimensionInfo(createMockDimension("lat", 10));
    SGDimensionInfo b = new SGDimensionInfo(createMockDimension("lat", 20));
    assertNotEquals(a, b);
  }

  @Test
  void equalsNull() {
    SGDimensionInfo info = new SGDimensionInfo(createMockDimension("lat", 10));
    assertNotEquals(null, info);
  }

  @Test
  void equalsDifferentType() {
    SGDimensionInfo info = new SGDimensionInfo(createMockDimension("lat", 10));
    assertNotEquals("lat", info);
  }

  @Test
  void hashCodeConsistentWithEquals() {
    Dimension dim = createMockDimension("lat", 10);
    SGDimensionInfo a = new SGDimensionInfo(dim);
    SGDimensionInfo b = new SGDimensionInfo(dim);
    assertEquals(a.hashCode(), b.hashCode());
  }
}
