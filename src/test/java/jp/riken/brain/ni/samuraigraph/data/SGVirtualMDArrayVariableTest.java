package jp.riken.brain.ni.samuraigraph.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import jp.riken.brain.ni.samuraigraph.base.SGAttribute;
import org.junit.jupiter.api.Test;

/** Unit tests for the virtual mdarray variables with in-memory arrays. */
class SGVirtualMDArrayVariableTest {

  private static final double DELTA = 1.0e-12;

  @Test
  void oneDimensionalArrayStoresValuesAndShape() {
    SGVirtualMDArrayVariable.D1 variable =
        new SGVirtualMDArrayVariable.D1(new double[] {1.0, 2.0, 3.0}, "v");
    assertEquals(1, variable.getDimensions().length);
    assertEquals(3, variable.getDimensions()[0]);
    assertEquals(1.0, variable.get(0), DELTA);
    assertEquals(3.0, variable.get(2), DELTA);
    assertEquals("v", variable.getName());
  }

  @Test
  void twoDimensionalArrayStoresValuesAndShape() {
    SGVirtualMDArrayVariable.D2 variable =
        new SGVirtualMDArrayVariable.D2(new double[][] {{1.0, 2.0}, {3.0, 4.0}}, "d2");
    assertEquals(2, variable.getDimensions().length);
    assertEquals(2, variable.getDimensions()[0]);
    assertEquals(2, variable.getDimensions()[1]);
    // column-major flattening: (0,0), (1,0), (0,1), (1,1)
    assertEquals(1.0, variable.get(0), DELTA);
    assertEquals(4.0, variable.get(3), DELTA);
  }

  @Test
  void constructionRejectsInvalidInputs() {
    assertThrows(IllegalArgumentException.class, () -> new SGVirtualMDArrayVariable.D1(null, "d"));
    assertThrows(IllegalArgumentException.class, () -> new SGVirtualMDArrayVariable.D2(null, "e"));
    assertThrows(
        IllegalArgumentException.class,
        () -> new SGVirtualMDArrayVariable.D2(new double[3][], "f"));
  }

  @Test
  void dataTypeIsProvidedByGetDataType() {
    SGVirtualMDArrayVariable.D1 variable =
        new SGVirtualMDArrayVariable.D1(new double[] {1.0, 2.0}, "z");
    assertNotNull(variable.getDataType());
  }

  @Test
  void attributesAreEmptyByDefault() {
    SGVirtualMDArrayVariable.D1 variable = new SGVirtualMDArrayVariable.D1(new double[] {1.0}, "n");
    List<SGAttribute> attributes = variable.getAttributes();
    assertTrue(attributes.isEmpty());
  }
}
